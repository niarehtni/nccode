package nc.impl.wa.paydata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.InSQLCreator;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hr.wa.IPaydataManageService;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.twhr.nhicalc.NhiCalcUtils;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;

/**
 * 計算數據加解密工具類
 * 
 * @author ssx
 * 
 * @since 6.5
 */
/**
 * @author ssx
 * 
 * @since 6.5
 */
public class CalculatingDataDEUtil {
	/**
	 * 檢查checkedList中的PK是否存在於已解密表中
	 * 
	 * @param checkedList
	 *            檢查PKs
	 * @return 已解密PKs
	 */
	@SuppressWarnings("unchecked")
	public static List<String> filterDecryptedExists(List<String> checkedList) throws BusinessException {
		if (checkedList != null && checkedList.size() > 0) {
			InSQLCreator parms = new InSQLCreator();
			String wherePart = " pk_wa_data in (" + parms.getInSQL(checkedList.toArray(new String[0])) + ")";
			List<String> existsList = (List<String>) new BaseDAO().executeQuery("select pk_wa_data from "
					+ IPaydataManageService.DECRYPTEDPKTABLENAME + " where " + wherePart, new ColumnListProcessor());
			return existsList;
		}

		return new ArrayList<String>();
	}

	private BaseDAO baseDAO = null;

	private IPsndocQryService psnQueryService = null;

	private Map<DataSourceTypeEnum, String> dataTableNameMap = null;
	private Map<DataSourceTypeEnum, Class> dataClassMap = null;
	private Map<String, String[]> decryptScopeMap = null;
	private Map<String, String> decryptPsndocTempTableMap = null;
	private Map<DataSourceTypeEnum, String[]> dataTableFieldMap = null;
	private Map<DataSourceTypeEnum, List<String>> dataDecryptedMap = null;
	private Map<String, List<String>> decryptedWaDataConditionPKs = null;
	private WaLoginContext loginContext = null;

	public CalculatingDataDEUtil(WaLoginContext context) throws BusinessException {
		loginContext = context;

		createDecryptedPKTable();
	}

	/**
	 * 初始化人員信息子集計算設置
	 * 
	 * @throws BusinessException
	 */
	public void initPsnInfosets() throws BusinessException {
		// 勞保勞退投保
		this.addCalculateSettings(DataSourceTypeEnum.LABORINS, null, PsndocDefTableUtil.getPsnLaborClass(),
				NhiCalcUtils.getLaborInsEncryptionAttributes());
		// 健保投保
		this.addCalculateSettings(DataSourceTypeEnum.HEALTHINS, null, PsndocDefTableUtil.getPsnHealthClass(),
				NhiCalcUtils.getHealthInsEncryptionAttributes());
		// 勞保勞退明細
		this.addCalculateSettings(DataSourceTypeEnum.LABORDETAIL, null, PsndocDefTableUtil.getPsnNHIDetailClass(),
				NhiCalcUtils.getNhiDetailEncryptionAttributes());
		// 勞保勞退彙總
		this.addCalculateSettings(DataSourceTypeEnum.LABORSUM, null, PsndocDefTableUtil.getPsnNHISumClass(),
				NhiCalcUtils.getNhiSumEncryptionAttributes());
		// 團保投保
		this.addCalculateSettings(DataSourceTypeEnum.GROUPINS, null, PsndocDefTableUtil.getGroupInsuranceClass(),
				NhiCalcUtils.getGroupInsEncryptionAttributes());
		// // 定調資
		// this.addCalculateSettings(DataSourceTypeEnum.WADOC, null,
		// PsndocWadocVO.class,
		// NhiCalcUtils.getWaDocEncryptionAttributes());
	}

	/**
	 * 增加計算設置
	 * 
	 * @param dataType
	 *            數據來源類型
	 * @param tableName
	 *            數據表
	 * @param fieldNames
	 *            字段名
	 */
	public void addCalculateSettings(DataSourceTypeEnum dataType, String tableName, Class clazz, String[] fieldNames) {
		if (!this.getDataTableNameMap().containsKey(dataType)) {
			this.getDataTableNameMap().put(dataType, tableName);
		}

		if (!this.getDataClassMap().containsKey(dataType)) {
			this.getDataClassMap().put(dataType, clazz);
		}

		if (!this.getDataTableFieldMap().containsKey(dataType)) {
			this.getDataTableFieldMap().put(dataType, fieldNames);
		}
	}

	/**
	 * 增加計算範圍
	 * 
	 * @param pk_wa_class
	 *            薪資方案
	 * @param cyear
	 *            期間年度
	 * @param cperiod
	 *            期間號
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public void addCalculateScope(String pk_wa_class, String cyear, String cperiod) throws BusinessException {
		String key = this.getKey(pk_wa_class, cyear, cperiod);

		if (!this.getDecryptScopeMap().containsKey(key)) {
			this.getDecryptScopeMap().put(key, new String[] { pk_wa_class, cyear, cperiod });
		}

		if (!this.getDecryptPsndocTempTableMap().containsKey(key)) {
			String strSQL = "select bd.pk_psndoc from bd_psndoc bd left join wa_data wad on bd.pk_psndoc = wad.PK_PSNDOC where wad.PK_WA_CLASS = '"
					+ pk_wa_class
					+ "' and wad.CPERIOD = '"
					+ cperiod
					+ "' and wad.CYEAR='"
					+ cyear
					+ "' and bd.pk_psndoc in (select pk_psndoc from wa_cacu_data where creator='"
					+ this.getLoginContext().getPk_loginUser() + "' and pk_wa_class='" + pk_wa_class + "')";
			ArrayList<String> pk_psndocs = (ArrayList<String>) new BaseDAO().executeQuery(strSQL,
					new ColumnListProcessor());
			if (pk_psndocs != null && pk_psndocs.size() > 0) {
				InSQLCreator parms = new InSQLCreator();
				String wherePart = " pk_psndoc in (" + parms.getInSQL(pk_psndocs.toArray(new String[0])) + ")";
				this.getDecryptPsndocTempTableMap().put(key, wherePart);
			}
		}
	}

	/**
	 * 增加計算範圍
	 * 
	 * @param loginContext
	 *            薪資計算上下文對象
	 * @throws BusinessException
	 */
	public void addCalculateScope() throws BusinessException {
		this.addCalculateScope(this.getLoginContext().getPk_wa_class(), this.getLoginContext().getCyear(), this
				.getLoginContext().getCperiod());
	}

	/**
	 * 數據解密
	 * 
	 * @throws BusinessException
	 */
	public void decryptPsnInfosetData() throws BusinessException {
		// 任一計算範圍為空均退出解密，不報錯
		if (this.getDecryptScopeMap().size() == 0 || this.getDecryptPsndocTempTableMap().size() == 0
				|| dataTableFieldMap.size() == 0) {
			return;
		}

		for (DataSourceTypeEnum dataType : this.getDataTableNameMap().keySet()) {
			for (Entry<String, String[]> scope : this.getDecryptScopeMap().entrySet()) {
				if (!this.isDecrypted(dataType, scope.getKey())) {
					Logger.error("-----------------WA-CALCULATE-DECRYPT-" + dataType.toString()
							+ "-START------------------");
					// 未解密的部份開始解密
					String strTempTableWherePart = this.getDecryptPsndocTempTableMap().get(scope.getKey());
					String[] fieldNames = this.getDataTableFieldMap().get(dataType);
					List<String> decryptedPKs = new ArrayList<String>();
					Class dataClass = this.getDataClassMap().get(dataType);
					SuperVO[] vos = this.getPsnQueryService()
							.querySubVOWithoutS(dataClass, strTempTableWherePart, null);
					if (vos != null && vos.length > 0) {
						for (SuperVO vo : vos) {
							for (String fn : fieldNames) {
								UFDouble value = getUFDouble(vo.getAttributeValue(fn));
								double decryptedValue = SalaryDecryptUtil.decrypt(value.doubleValue());
								vo.setAttributeValue(fn, new UFDouble(decryptedValue));
							}
							decryptedPKs.add((String) vo.getAttributeValue("pk_psndoc_sub"));
						}

						if (decryptedPKs.size() > 0) {
							this.getBaseDAO().updateVOArray(vos);
							this.appendToDecryptedPKTable(decryptedPKs, dataType); // 將已解密行加入物理臨時表中
						}
					}
					Logger.error("-----------------ROW-NO-" + String.valueOf(vos == null ? 0 : vos.length)
							+ "----------------------");
					Logger.error("-----------------WA-CALCULATE-DECRYPT-" + dataType.toString()
							+ "-END------------------");
				}
			}
		}
	}

	public void encryptPsnInfosetData() throws BusinessException {
		// 已解密數據為空的直接退出
		if (this.getDataDecryptedMap().size() == 0) {
			return;
		}

		for (DataSourceTypeEnum dataType : this.getDataDecryptedMap().keySet()) {
			if (DataSourceTypeEnum.WADATA != dataType) {
				Logger.error("-----------------WA-CALCULATE-ENCRYPT-" + dataType.toString()
						+ "-START------------------");
				List<String> strKeys = this.getDataDecryptedMap().get(dataType);
				for (String strKey : strKeys) {
					// 已解密的部份開始加密
					String strTempTableWherePart = "pk_psndoc_sub='" + strKey + "'";
					String[] fieldNames = this.getDataTableFieldMap().get(dataType);
					Class dataClass = this.getDataClassMap().get(dataType);
					SuperVO[] vos = this.getPsnQueryService()
							.querySubVOWithoutS(dataClass, strTempTableWherePart, null);
					if (vos != null && vos.length > 0) {
						for (SuperVO vo : vos) {
							for (String fn : fieldNames) {
								UFDouble value = getUFDouble(vo.getAttributeValue(fn));
								double encryptedValue = SalaryEncryptionUtil.encryption(value.doubleValue());
								vo.setAttributeValue(fn, new UFDouble(encryptedValue));
							}
						}

						this.getBaseDAO().updateVOArray(vos);
					}
				}
				Logger.error("-----------------ROW-NO-" + String.valueOf(strKeys == null ? 0 : strKeys.size())
						+ "----------------------");
				Logger.error("-----------------WA-CALCULATE-ENCRYPT-" + dataType.toString() + "-END------------------");

				this.deleteDecryptedPKFromTable(dataType);
			}
		}
	}

	/**
	 * 按條件解密WaData（解密前驗重，以防重複解密）
	 * 
	 * @param condition
	 *            WaData的篩選條件
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public void decryptWaDataByCondition(String condition) throws BusinessException {
		if (!this.getDecryptedWaDataConditionPKs().containsKey(condition)) {
			Logger.error("-----------------WA-CALCULATE-DECRYPT-WADATA-START------------------");
			// 如果存在完全一樣的條件，即認為已解密

			// 取本次要加密的數據 start
			if (condition.trim().toLowerCase().startsWith("where ")) {
				condition = condition.substring("where ".length());
			}
			Collection<DataVO> voList = this.getBaseDAO().retrieveByClause(DataVO.class, condition);
			// end

			// 檢查是否與之前解密過的數據有範圍重疊
			List<String> decryptedPKs = new ArrayList<String>();
			List<DataVO> decryptedVOList = new ArrayList<DataVO>();
			for (DataVO vo : voList) {
				boolean exists = false;
				// 遍歷已解密PK
				for (List<String> decryptedPKList : this.getDecryptedWaDataConditionPKs().values()) {
					for (String pk : decryptedPKList) {
						if (pk.equals(vo.getPk_wa_data())) {
							// 已解密PK中含有本次待解密的PK
							exists = true;
							break;
						}
					}

					if (exists) {
						break;
					}
				}

				if (!exists) {
					// 本次待加密PK
					decryptedPKs.add(vo.getPk_wa_data());
					decryptedVOList.add(vo);
				}
			}

			// 解密
			if (decryptedPKs.size() > 0) {
				DataVO[] vos = SalaryDecryptUtil.decrypt4Array(decryptedVOList.toArray(new DataVO[0]));
				this.getBaseDAO().updateVOArray(vos);
				this.appendToDecryptedPKTable(decryptedPKs, DataSourceTypeEnum.WADATA); // 將wa_data已解密行加入物理臨時表中，供同一事務其他維度的過程查詢
			}

			// 加入已解密條件清單
			this.getDecryptedWaDataConditionPKs().put(condition, decryptedPKs);

			Logger.error("-----------------ROW-NO-" + String.valueOf(decryptedPKs.size()) + "----------------------");
			Logger.error("-----------------WA-CALCULATE-DECRYPT-WADATA-END------------------");
		}
	}

	private void appendToDecryptedPKTable(List<String> decryptedPKs, DataSourceTypeEnum srcType)
			throws BusinessException {
		// 插入數據
		for (String pk : decryptedPKs) {
			this.getBaseDAO().executeUpdate(
					"insert into " + IPaydataManageService.DECRYPTEDPKTABLENAME
							+ "(pk_wa_data, srctype, creator) values ('" + pk + "','" + srcType.toString() + "', '"
							+ this.getLoginContext().getPk_loginUser() + "');");
		}
	}

	private void createDecryptedPKTable() throws BusinessException {
		// 如果表不存在則創建表
		if (!this.getBaseDAO().isTableExisted(IPaydataManageService.DECRYPTEDPKTABLENAME)) {
			this.getBaseDAO()
					.executeUpdate(
							"create table "
									+ IPaydataManageService.DECRYPTEDPKTABLENAME
									+ " (pk_wa_data varchar2(20) not null, srctype varchar2(20), creator varchar2(20), ts char(19) not null);");
		} else {
			// 刪除可能存在的髒數據
			// deleteDecryptedPKFromTable(null);
		}
	}

	private void deleteDecryptedPKFromTable(DataSourceTypeEnum srcType) throws DAOException {
		this.getBaseDAO().executeUpdate(
				"delete from " + IPaydataManageService.DECRYPTEDPKTABLENAME + " where creator='"
						+ this.getLoginContext().getPk_loginUser() + "' "
						+ (srcType == null ? "" : (" and srctype='" + srcType.toString() + "'"))); // 從物理臨時表中清除已解密數據
	}

	/**
	 * 加密已解密數據
	 */
	@SuppressWarnings("unchecked")
	public void encryptWaData() throws BusinessException {
		// 不存在已解密數據時直接退出
		if (!this.getDataDecryptedMap().containsKey(DataSourceTypeEnum.WADATA)
				|| this.getDataDecryptedMap().get(DataSourceTypeEnum.WADATA) == null
				|| this.getDataDecryptedMap().get(DataSourceTypeEnum.WADATA).size() == 0) {
			return;
		}

		Logger.error("-----------------WA-CALCULATE-ENCRYPT-WADATA-START------------------");
		InSQLCreator parms = new InSQLCreator();
		String wherePart = " pk_wa_data in ("
				+ parms.getInSQL(this.getDataDecryptedMap().get(DataSourceTypeEnum.WADATA).toArray(new String[0]))
				+ ")";
		Collection<DataVO> voList = this.getBaseDAO().retrieveByClause(DataVO.class, wherePart);
		DataVO[] vos = SalaryEncryptionUtil.encryption4Array(voList.toArray(new DataVO[0]));
		this.getBaseDAO().updateVOArray(vos);
		deleteDecryptedPKFromTable(DataSourceTypeEnum.WADATA);

		Logger.error("-----------------ROW-NO-"
				+ String.valueOf(this.getDataDecryptedMap().get(DataSourceTypeEnum.WADATA).size())
				+ "----------------------");
		Logger.error("-----------------WA-CALCULATE-ENCRYPT-WADATA-END------------------");
	}

	private UFDouble getUFDouble(Object value) {
		UFDouble ret = UFDouble.ZERO_DBL;
		if (value != null) {
			ret = (UFDouble) value;
		}
		return ret;
	}

	/**
	 * 指定數據來源指定範圍是否已解密
	 * 
	 * @param dateType
	 * @param calculatingKey
	 * @return
	 */
	public boolean isDecrypted(DataSourceTypeEnum dateType, String calculatingKey) {
		if (this.getDataDecryptedMap().containsKey(dateType)) {
			for (String key : this.getDataDecryptedMap().get(dateType)) {
				if (key.toUpperCase().equals(calculatingKey.toUpperCase())) {
					return true;
				}
			}
		}

		return false;
	}

	public void initDataDecrptedMap() throws BusinessException {
		List<String> decryptedPKList = getDecryptedPKByType(DataSourceTypeEnum.GROUPINS);
		if (decryptedPKList != null && decryptedPKList.size() > 0) {
			this.getDataDecryptedMap().put(DataSourceTypeEnum.GROUPINS, decryptedPKList);
		}

		decryptedPKList = getDecryptedPKByType(DataSourceTypeEnum.HEALTHINS);
		if (decryptedPKList != null && decryptedPKList.size() > 0) {
			this.getDataDecryptedMap().put(DataSourceTypeEnum.HEALTHINS, decryptedPKList);
		}

		decryptedPKList = getDecryptedPKByType(DataSourceTypeEnum.LABORDETAIL);
		if (decryptedPKList != null && decryptedPKList.size() > 0) {
			this.getDataDecryptedMap().put(DataSourceTypeEnum.LABORDETAIL, decryptedPKList);
		}

		decryptedPKList = getDecryptedPKByType(DataSourceTypeEnum.LABORINS);
		if (decryptedPKList != null && decryptedPKList.size() > 0) {
			this.getDataDecryptedMap().put(DataSourceTypeEnum.LABORINS, decryptedPKList);
		}

		decryptedPKList = getDecryptedPKByType(DataSourceTypeEnum.LABORSUM);
		if (decryptedPKList != null && decryptedPKList.size() > 0) {
			this.getDataDecryptedMap().put(DataSourceTypeEnum.LABORSUM, decryptedPKList);
		}

		// decryptedPKList = getDecryptedPKByType(DataSourceTypeEnum.WADOC);
		// if (decryptedPKList != null && decryptedPKList.size() > 0) {
		// this.getDataDecryptedMap().put(DataSourceTypeEnum.WADOC,
		// decryptedPKList);
		// }

		decryptedPKList = getDecryptedPKByType(DataSourceTypeEnum.WADATA);
		if (decryptedPKList != null && decryptedPKList.size() > 0) {
			this.getDataDecryptedMap().put(DataSourceTypeEnum.WADATA, decryptedPKList);
		}
	}

	@SuppressWarnings("unchecked")
	private List<String> getDecryptedPKByType(DataSourceTypeEnum srcType) throws BusinessException {
		List<String> decryptedPKList = (List<String>) this.getBaseDAO().executeQuery(
				"select pk_wa_data from " + IPaydataManageService.DECRYPTEDPKTABLENAME + " where srctype='"
						+ srcType.toString() + "' and creator='" + this.getLoginContext().getPk_loginUser() + "'",
				new ColumnListProcessor());

		return decryptedPKList;
	}

	/**
	 * 取計算範圍Key
	 * 
	 * @param waLogin
	 * @return
	 */
	public String getKey(WaLoginVO waLogin) {
		return getKey(waLogin.getPk_wa_class(), waLogin.getCyear(), waLogin.getCperiod());
	}

	/**
	 * 取計算範圍Key
	 * 
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @return
	 */
	public String getKey(String pk_wa_class, String cyear, String cperiod) {
		return pk_wa_class + "-" + cyear + "-" + cperiod;
	}

	/**
	 * 數據來源類型
	 * 
	 * @author ssx
	 * 
	 * @since 6.5
	 */
	public enum DataSourceTypeEnum {
		// 勞退投保子集
		LABORINS,
		// 健保投保子集
		HEALTHINS,
		// 勞退計算明細子集
		LABORDETAIL,
		// 勞退計算彙總子集
		LABORSUM,
		// 團保投保子集
		GROUPINS,
		// 定調資
		WADOC,
		// 薪資
		WADATA
	}

	/**
	 * 來源數據表名集合
	 * 
	 * @return Map<數據來源類型, 數據表名>
	 */
	public Map<DataSourceTypeEnum, String> getDataTableNameMap() {
		if (dataTableNameMap == null) {
			dataTableNameMap = new HashMap<DataSourceTypeEnum, String>();
		}
		return dataTableNameMap;
	}

	/**
	 * 來源數據類集合
	 * 
	 * @return
	 */
	public Map<DataSourceTypeEnum, Class> getDataClassMap() {
		if (dataClassMap == null) {
			dataClassMap = new HashMap<DataSourceTypeEnum, Class>();
		}
		return dataClassMap;
	}

	/**
	 * 設置來源數據表名映射
	 * 
	 * @param dataTableNameMap
	 */
	public void setDataTableNameMap(Map<DataSourceTypeEnum, String> dataTableNameMap) {
		this.dataTableNameMap = dataTableNameMap;
	}

	/**
	 * 取數據表字段映射
	 * 
	 * @return
	 */
	public Map<DataSourceTypeEnum, String[]> getDataTableFieldMap() {
		if (dataTableFieldMap == null) {
			dataTableFieldMap = new HashMap<DataSourceTypeEnum, String[]>();
		}
		return dataTableFieldMap;
	}

	/**
	 * 取計算範圍
	 * 
	 * @return Map<範圍Key, 範圍：pk_wa_class, cyear, cperiod>
	 */
	public Map<String, String[]> getDecryptScopeMap() {
		if (decryptScopeMap == null) {
			decryptScopeMap = new HashMap<String, String[]>();
		}
		return decryptScopeMap;
	}

	/**
	 * 取數據已解密集合
	 * 
	 * @return Map<數據來源類型, 已解密Key數組>
	 */
	public Map<DataSourceTypeEnum, List<String>> getDataDecryptedMap() {
		if (dataDecryptedMap == null) {
			dataDecryptedMap = new HashMap<DataSourceTypeEnum, List<String>>();
		}
		return dataDecryptedMap;
	}

	public void setBaseDAO(BaseDAO basedao) {
		baseDAO = basedao;
	}

	public BaseDAO getBaseDAO() {
		if (baseDAO == null) {
			baseDAO = new BaseDAO();
		}
		return baseDAO;
	}

	/**
	 * 取解密人員臨時表
	 * 
	 * @return
	 */
	public Map<String, String> getDecryptPsndocTempTableMap() {
		if (decryptPsndocTempTableMap == null) {
			decryptPsndocTempTableMap = new HashMap<String, String>();
		}
		return decryptPsndocTempTableMap;
	}

	public IPsndocQryService getPsnQueryService() {
		if (psnQueryService == null) {
			psnQueryService = NCLocator.getInstance().lookup(IPsndocQryService.class);
		}
		return psnQueryService;
	}

	public Map<String, List<String>> getDecryptedWaDataConditionPKs() {
		if (decryptedWaDataConditionPKs == null) {
			decryptedWaDataConditionPKs = new HashMap<String, List<String>>();
		}
		return decryptedWaDataConditionPKs;
	}

	public WaLoginContext getLoginContext() {
		return loginContext;
	}

	@SuppressWarnings("unchecked")
	public void decryptAllBySQL() throws BusinessException {
		List<String> typeList = (List<String>) this.getBaseDAO().executeQuery(
				"select distinct srctype from " + IPaydataManageService.DECRYPTEDPKTABLENAME + " where creator='"
						+ this.getLoginContext().getPk_loginUser() + "'", new ColumnListProcessor());

		if (typeList != null && typeList.size() > 0) {
			DataSourceTypeEnum srcType = null;
			for (String typeName : typeList) {
				Logger.error("-----------------WA-CALCULATE-ENCRYPT-" + typeName + "-START------------------");
				String tableName = "";
				switch (typeName) {
				case "WADATA":
					srcType = DataSourceTypeEnum.WADATA;
					tableName = "wa_data";
					break;
				case "LABORINS":
					srcType = DataSourceTypeEnum.LABORINS;
					tableName = PsndocDefTableUtil.getPsnLaborTablename();
					break;
				case "HEALTHINS":
					srcType = DataSourceTypeEnum.HEALTHINS;
					tableName = PsndocDefTableUtil.getPsnHealthTablename();
					break;
				case "LABORDETAIL":
					srcType = DataSourceTypeEnum.LABORDETAIL;
					tableName = PsndocDefTableUtil.getPsnNHIDetailTablename();
					break;
				case "LABORSUM":
					srcType = DataSourceTypeEnum.LABORSUM;
					tableName = PsndocDefTableUtil.getPsnNHISumTablename();
					break;
				case "GROUPINS":
					srcType = DataSourceTypeEnum.GROUPINS;
					tableName = PsndocDefTableUtil.getGroupInsuranceTablename();
					break;
				}

				String pkFieldname = srcType.equals(DataSourceTypeEnum.WADATA) ? "pk_wa_data" : "pk_psndoc_sub";
				String[] fieldNames = srcType.equals(DataSourceTypeEnum.WADATA) ? getWaDataFloatFieldNames() : this
						.getDataTableFieldMap().get(srcType);

				String wherePart = pkFieldname + " in (";
				wherePart += "select pk_wa_data from " + IPaydataManageService.DECRYPTEDPKTABLENAME
						+ " where creator='" + this.getLoginContext().getPk_loginUser() + "' and srctype = '"
						+ typeName + "')";

				if (srcType.equals(DataSourceTypeEnum.WADATA)) {
					// 加密wa_data
					Collection<DataVO> voList = this.getBaseDAO().retrieveByClause(DataVO.class, wherePart);
					DataVO[] vos = SalaryEncryptionUtil.encryption4Array(voList.toArray(new DataVO[0]));
					this.getBaseDAO().updateVOArray(vos);
				} else {
					Class dataClass = this.getDataClassMap().get(srcType);
					SuperVO[] vos = this.getPsnQueryService().querySubVOWithoutS(dataClass, wherePart, null);
					if (vos != null && vos.length > 0) {
						for (SuperVO vo : vos) {
							for (String fn : fieldNames) {
								UFDouble value = getUFDouble(vo.getAttributeValue(fn));
								double encryptedValue = SalaryEncryptionUtil.encryption(value.doubleValue());
								vo.setAttributeValue(fn, new UFDouble(encryptedValue));
							}
						}

						this.getBaseDAO().updateVOArray(vos);
					}
					// String strSQL = "update " + tableName + " set ";
					// for (String fieldname : fieldNames) {
					// strSQL += fieldname + "=SALARY_ENCRYPT(" + fieldname +
					// "),";
					// }
					// strSQL += "ts='" + (new UFDateTime()).toString() + "'";
					// strSQL += " where " + pkFieldname + " in (";
					// strSQL += "select pk_wa_data from " +
					// IPaydataManageService.DECRYPTEDPKTABLENAME
					// + " where creator='" +
					// this.getLoginContext().getPk_loginUser() +
					// "' and srctype = '"
					// + typeName + "')";
					// this.getBaseDAO().executeUpdate(strSQL);
				}

				String strSQL = "delete from " + IPaydataManageService.DECRYPTEDPKTABLENAME + " where creator='"
						+ this.getLoginContext().getPk_loginUser() + "' and srctype='" + typeName + "'";
				this.getBaseDAO().executeUpdate(strSQL);
				Logger.error("-----------------WA-CALCULATE-ENCRYPT-" + typeName + "-END------------------");
			}
		}
	}

	private String[] getWaDataFloatFieldNames() throws BusinessException {
		Collection<DataVO> datavo = this.getBaseDAO().retrieveByClause(DataVO.class, "rownum=1");

		if (datavo != null) {
			HashMap<String, Object> map = datavo.toArray(new DataVO[0])[0].appValueHashMap;
			Object[] pks = map.keySet().toArray();
			ArrayList<String> itemPks = new ArrayList<String>();
			for (int j = 0; j < pks.length; j++) {
				if (pks[j].toString().startsWith("f_")) {
					itemPks.add(pks[j].toString());
				}
			}
			return itemPks.toArray(new String[0]);
		}
		return new String[0];
	}
}
