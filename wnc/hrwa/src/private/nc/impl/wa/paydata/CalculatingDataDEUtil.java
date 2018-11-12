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
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.vo.hi.wadoc.PsndocWadocVO;
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
	 * н�Y�����R�r�����ѽ���wa_data��
	 */
	public static String DECRYPTEDPKTABLENAME = "wa_cacu_decryptedpk";

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
					+ CalculatingDataDEUtil.DECRYPTEDPKTABLENAME + " where " + wherePart, new ColumnListProcessor());
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
		// ���{�Y
		this.addCalculateSettings(DataSourceTypeEnum.WADOC, null, PsndocWadocVO.class,
				NhiCalcUtils.getWaDocEncryptionAttributes());
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
			this.getDecryptScopeMap().put(this.getKey(pk_wa_class, cyear, cperiod),
					new String[] { pk_wa_class, cyear, cperiod });
		}

		if (!this.getDecryptPsndocTempTableMap().containsKey(key)) {
			String strSQL = "select bd.pk_psndoc from bd_psndoc bd left join wa_data wad on bd.pk_psndoc = wad.PK_PSNDOC where wad.PK_WA_CLASS = '"
					+ pk_wa_class + "' and wad.CPERIOD = '" + cperiod + "' and wad.CYEAR='" + cyear + "'";
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
						}

						this.getBaseDAO().updateVOArray(vos);
						if (!this.getDataDecryptedMap().containsKey(scope.getKey())) {
							this.getDataDecryptedMap().put(dataType, new ArrayList<String>());
						}
						this.getDataDecryptedMap().get(dataType).add(scope.getKey());
					}
					Logger.error("-----------------ROW-NO-" + String.valueOf(vos.length) + "----------------------");
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
			for (List<String> strKeys : this.getDataDecryptedMap().values()) {
				for (String strKey : strKeys) {
					Logger.error("-----------------WA-CALCULATE-ENCRYPT-" + dataType.toString()
							+ "-START------------------");
					// δ���ܵĲ����_ʼ����
					String strTempTableWherePart = this.getDecryptPsndocTempTableMap().get(strKey);
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
					Logger.error("-----------------ROW-NO-" + String.valueOf(vos.length) + "----------------------");
					Logger.error("-----------------WA-CALCULATE-ENCRYPT-" + dataType.toString()
							+ "-END------------------");
				}
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
				this.appendToDecryptedPKTable(decryptedPKs); // ��wa_data�ѽ����м��������R�r���У���ͬһ�������S�ȵ��^�̲�ԃ
			}

			// �����ѽ��ܗl�����
			this.getDecryptedWaDataConditionPKs().put(condition, decryptedPKs);

			Logger.error("-----------------ROW-NO-" + String.valueOf(decryptedPKs.size()) + "----------------------");
			Logger.error("-----------------WA-CALCULATE-DECRYPT-WADATA-END------------------");
		}
	}

	private void appendToDecryptedPKTable(List<String> decryptedPKs) throws BusinessException {
		// ���딵��
		for (String pk : decryptedPKs) {
			this.getBaseDAO().executeUpdate(
					"insert into " + CalculatingDataDEUtil.DECRYPTEDPKTABLENAME + "(pk_wa_data, creator) values ('"
							+ pk + "', '" + this.getLoginContext().getPk_loginUser() + "');");
		}
	}

	private void createDecryptedPKTable() throws BusinessException {
		// ����������ڄt������
		if (!this.getBaseDAO().isTableExisted(CalculatingDataDEUtil.DECRYPTEDPKTABLENAME)) {
			this.getBaseDAO().executeUpdate(
					"create table " + CalculatingDataDEUtil.DECRYPTEDPKTABLENAME
							+ " (pk_wa_data char(20) not null, creator char(20) not null);");
		} else {
			// �h�����ܴ��ڵ��v����
			deleteDecryptedPKFromTable();
		}
	}

	private void deleteDecryptedPKFromTable() throws DAOException {
		this.getBaseDAO().executeUpdate(
				"delete from " + CalculatingDataDEUtil.DECRYPTEDPKTABLENAME + " where creator='"
						+ this.getLoginContext().getPk_loginUser() + "';"); // �������R�r��������ѽ��ܔ���
	}

	/**
	 * �����ѽ��ܔ���
	 */
	@SuppressWarnings("unchecked")
	public void encryptWaData() throws BusinessException {
		// �������ѽ��ܔ����rֱ���˳�
		if (this.getDecryptedWaDataConditionPKs().size() == 0) {
			return;
		}

		Logger.error("-----------------WA-CALCULATE-ENCRYPT-WADATA-START------------------");
		List<String> decryptedPKs = new ArrayList<String>();
		for (List<String> decryptedPKList : this.getDecryptedWaDataConditionPKs().values()) {
			if (decryptedPKList.size() > 0) {
				decryptedPKs.addAll(decryptedPKList);
			}
		}

		InSQLCreator parms = new InSQLCreator();
		String wherePart = " pk_wa_data in (" + parms.getInSQL(decryptedPKs.toArray(new String[0])) + ")";
		Collection<DataVO> voList = this.getBaseDAO().retrieveByClause(DataVO.class, wherePart);
		DataVO[] vos = SalaryEncryptionUtil.encryption4Array(voList.toArray(new DataVO[0]));
		this.getBaseDAO().updateVOArray(vos);
		deleteDecryptedPKFromTable();

		Logger.error("-----------------ROW-NO-" + String.valueOf(decryptedPKs.size()) + "----------------------");
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
		WADOC
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
}