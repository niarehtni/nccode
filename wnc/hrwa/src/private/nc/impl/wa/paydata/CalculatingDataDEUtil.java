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
 * Ӌ�㔵���ӽ��ܹ����
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
	 * �z��checkedList�е�PK�Ƿ������ѽ��ܱ���
	 * 
	 * @param checkedList
	 *            �z��PKs
	 * @return �ѽ���PKs
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
	 * ��ʼ���ˆT��Ϣ�Ӽ�Ӌ���O��
	 * 
	 * @throws BusinessException
	 */
	public void initPsnInfosets() throws BusinessException {
		// �ڱ�����Ͷ��
		this.addCalculateSettings(DataSourceTypeEnum.LABORINS, null, PsndocDefTableUtil.getPsnLaborClass(),
				NhiCalcUtils.getLaborInsEncryptionAttributes());
		// ����Ͷ��
		this.addCalculateSettings(DataSourceTypeEnum.HEALTHINS, null, PsndocDefTableUtil.getPsnHealthClass(),
				NhiCalcUtils.getHealthInsEncryptionAttributes());
		// �ڱ���������
		this.addCalculateSettings(DataSourceTypeEnum.LABORDETAIL, null, PsndocDefTableUtil.getPsnNHIDetailClass(),
				NhiCalcUtils.getNhiDetailEncryptionAttributes());
		// �ڱ����ˏ���
		this.addCalculateSettings(DataSourceTypeEnum.LABORSUM, null, PsndocDefTableUtil.getPsnNHISumClass(),
				NhiCalcUtils.getNhiSumEncryptionAttributes());
		// �F��Ͷ��
		this.addCalculateSettings(DataSourceTypeEnum.GROUPINS, null, PsndocDefTableUtil.getGroupInsuranceClass(),
				NhiCalcUtils.getGroupInsEncryptionAttributes());
		// // ���{�Y
		// this.addCalculateSettings(DataSourceTypeEnum.WADOC, null,
		// PsndocWadocVO.class,
		// NhiCalcUtils.getWaDocEncryptionAttributes());
	}

	/**
	 * ����Ӌ���O��
	 * 
	 * @param dataType
	 *            ������Դ���
	 * @param tableName
	 *            ������
	 * @param fieldNames
	 *            �ֶ���
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
	 * ����Ӌ�㹠��
	 * 
	 * @param pk_wa_class
	 *            н�Y����
	 * @param cyear
	 *            ���g���
	 * @param cperiod
	 *            ���g̖
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
	 * ����Ӌ�㹠��
	 * 
	 * @param loginContext
	 *            н�YӋ�������Č���
	 * @throws BusinessException
	 */
	public void addCalculateScope() throws BusinessException {
		this.addCalculateScope(this.getLoginContext().getPk_wa_class(), this.getLoginContext().getCyear(), this
				.getLoginContext().getCperiod());
	}

	/**
	 * ��������
	 * 
	 * @throws BusinessException
	 */
	public void decryptPsnInfosetData() throws BusinessException {
		// ��һӋ�㹠����վ��˳����ܣ������e
		if (this.getDecryptScopeMap().size() == 0 || this.getDecryptPsndocTempTableMap().size() == 0
				|| dataTableFieldMap.size() == 0) {
			return;
		}

		for (DataSourceTypeEnum dataType : this.getDataTableNameMap().keySet()) {
			for (Entry<String, String[]> scope : this.getDecryptScopeMap().entrySet()) {
				if (!this.isDecrypted(dataType, scope.getKey())) {
					Logger.error("-----------------WA-CALCULATE-DECRYPT-" + dataType.toString()
							+ "-START------------------");
					// δ���ܵĲ����_ʼ����
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
							this.appendToDecryptedPKTable(decryptedPKs, dataType); // ���ѽ����м��������R�r����
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
		// �ѽ��ܔ�����յ�ֱ���˳�
		if (this.getDataDecryptedMap().size() == 0) {
			return;
		}

		for (DataSourceTypeEnum dataType : this.getDataDecryptedMap().keySet()) {
			if (DataSourceTypeEnum.WADATA != dataType) {
				Logger.error("-----------------WA-CALCULATE-ENCRYPT-" + dataType.toString()
						+ "-START------------------");
				List<String> strKeys = this.getDataDecryptedMap().get(dataType);
				for (String strKey : strKeys) {
					// �ѽ��ܵĲ����_ʼ����
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
	 * ���l������WaData������ǰ��أ��Է����}���ܣ�
	 * 
	 * @param condition
	 *            WaData�ĺY�x�l��
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public void decryptWaDataByCondition(String condition) throws BusinessException {
		if (!this.getDecryptedWaDataConditionPKs().containsKey(condition)) {
			Logger.error("-----------------WA-CALCULATE-DECRYPT-WADATA-START------------------");
			// ���������ȫһ�ӵėl�������J���ѽ���

			// ȡ����Ҫ���ܵĔ��� start
			if (condition.trim().toLowerCase().startsWith("where ")) {
				condition = condition.substring("where ".length());
			}
			Collection<DataVO> voList = this.getBaseDAO().retrieveByClause(DataVO.class, condition);
			// end

			// �z���Ƿ��c֮ǰ�����^�Ĕ����й����دB
			List<String> decryptedPKs = new ArrayList<String>();
			List<DataVO> decryptedVOList = new ArrayList<DataVO>();
			for (DataVO vo : voList) {
				boolean exists = false;
				// ��v�ѽ���PK
				for (List<String> decryptedPKList : this.getDecryptedWaDataConditionPKs().values()) {
					for (String pk : decryptedPKList) {
						if (pk.equals(vo.getPk_wa_data())) {
							// �ѽ���PK�к��б��δ����ܵ�PK
							exists = true;
							break;
						}
					}

					if (exists) {
						break;
					}
				}

				if (!exists) {
					// ���δ�����PK
					decryptedPKs.add(vo.getPk_wa_data());
					decryptedVOList.add(vo);
				}
			}

			// ����
			if (decryptedPKs.size() > 0) {
				DataVO[] vos = SalaryDecryptUtil.decrypt4Array(decryptedVOList.toArray(new DataVO[0]));
				this.getBaseDAO().updateVOArray(vos);
				this.appendToDecryptedPKTable(decryptedPKs, DataSourceTypeEnum.WADATA); // ��wa_data�ѽ����м��������R�r���У���ͬһ�������S�ȵ��^�̲�ԃ
			}

			// �����ѽ��ܗl�����
			this.getDecryptedWaDataConditionPKs().put(condition, decryptedPKs);

			Logger.error("-----------------ROW-NO-" + String.valueOf(decryptedPKs.size()) + "----------------------");
			Logger.error("-----------------WA-CALCULATE-DECRYPT-WADATA-END------------------");
		}
	}

	private void appendToDecryptedPKTable(List<String> decryptedPKs, DataSourceTypeEnum srcType)
			throws BusinessException {
		// ���딵��
		for (String pk : decryptedPKs) {
			this.getBaseDAO().executeUpdate(
					"insert into " + IPaydataManageService.DECRYPTEDPKTABLENAME
							+ "(pk_wa_data, srctype, creator) values ('" + pk + "','" + srcType.toString() + "', '"
							+ this.getLoginContext().getPk_loginUser() + "');");
		}
	}

	private void createDecryptedPKTable() throws BusinessException {
		// ��������ڄt������
		if (!this.getBaseDAO().isTableExisted(IPaydataManageService.DECRYPTEDPKTABLENAME)) {
			this.getBaseDAO()
					.executeUpdate(
							"create table "
									+ IPaydataManageService.DECRYPTEDPKTABLENAME
									+ " (pk_wa_data varchar2(20) not null, srctype varchar2(20), creator varchar2(20), ts char(19) not null);");
		} else {
			// �h�����ܴ��ڵ��v����
			// deleteDecryptedPKFromTable(null);
		}
	}

	private void deleteDecryptedPKFromTable(DataSourceTypeEnum srcType) throws DAOException {
		this.getBaseDAO().executeUpdate(
				"delete from " + IPaydataManageService.DECRYPTEDPKTABLENAME + " where creator='"
						+ this.getLoginContext().getPk_loginUser() + "' "
						+ (srcType == null ? "" : (" and srctype='" + srcType.toString() + "'"))); // �������R�r��������ѽ��ܔ���
	}

	/**
	 * �����ѽ��ܔ���
	 */
	@SuppressWarnings("unchecked")
	public void encryptWaData() throws BusinessException {
		// �������ѽ��ܔ����rֱ���˳�
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
	 * ָ��������Դָ�������Ƿ��ѽ���
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
	 * ȡӋ�㹠��Key
	 * 
	 * @param waLogin
	 * @return
	 */
	public String getKey(WaLoginVO waLogin) {
		return getKey(waLogin.getPk_wa_class(), waLogin.getCyear(), waLogin.getCperiod());
	}

	/**
	 * ȡӋ�㹠��Key
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
	 * ������Դ���
	 * 
	 * @author ssx
	 * 
	 * @since 6.5
	 */
	public enum DataSourceTypeEnum {
		// ����Ͷ���Ӽ�
		LABORINS,
		// ����Ͷ���Ӽ�
		HEALTHINS,
		// ����Ӌ�������Ӽ�
		LABORDETAIL,
		// ����Ӌ�㏡���Ӽ�
		LABORSUM,
		// �F��Ͷ���Ӽ�
		GROUPINS,
		// ���{�Y
		WADOC,
		// н�Y
		WADATA
	}

	/**
	 * ��Դ������������
	 * 
	 * @return Map<������Դ���, ��������>
	 */
	public Map<DataSourceTypeEnum, String> getDataTableNameMap() {
		if (dataTableNameMap == null) {
			dataTableNameMap = new HashMap<DataSourceTypeEnum, String>();
		}
		return dataTableNameMap;
	}

	/**
	 * ��Դ�������
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
	 * �O�Á�Դ��������ӳ��
	 * 
	 * @param dataTableNameMap
	 */
	public void setDataTableNameMap(Map<DataSourceTypeEnum, String> dataTableNameMap) {
		this.dataTableNameMap = dataTableNameMap;
	}

	/**
	 * ȡ�������ֶ�ӳ��
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
	 * ȡӋ�㹠��
	 * 
	 * @return Map<����Key, ������pk_wa_class, cyear, cperiod>
	 */
	public Map<String, String[]> getDecryptScopeMap() {
		if (decryptScopeMap == null) {
			decryptScopeMap = new HashMap<String, String[]>();
		}
		return decryptScopeMap;
	}

	/**
	 * ȡ�����ѽ��ܼ���
	 * 
	 * @return Map<������Դ���, �ѽ���Key���M>
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
	 * ȡ�����ˆT�R�r��
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
					// ����wa_data
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
