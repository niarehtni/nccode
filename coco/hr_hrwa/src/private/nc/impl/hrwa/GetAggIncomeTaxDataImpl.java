package nc.impl.hrwa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.wa.util.LocalizationSysinitUtil;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.itf.hrwa.IGetAggIncomeTaxData;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pubitf.twhr.IBasedocPubQuery;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;
import nc.vo.hrwa.incometax.IncomeTaxUtil;
import nc.vo.hrwa.incometax.IncomeTaxVO;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;
import nc.vo.hrwa.sumincometax.CIncomeTaxVO;
import nc.vo.hrwa.sumincometax.SumIncomeTaxVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.twhr.basedoc.BaseDocVO;

import org.apache.commons.lang.StringUtils;

/**
 * @author ward.wong
 * @date 20180126
 * @version v1.0
 * @�������� ��ȡ�걨��ϸ�����ܽӿ�ʵ����
 * 
 */
public class GetAggIncomeTaxDataImpl implements IGetAggIncomeTaxData {
	/**
	 * @�������� �������������걨��ϸ��
	 * @param isForeignMonth
	 *            �Ƿ��⼮Ա�������걨
	 * @param unifiednumber
	 *            ͳһ���
	 * @param declaretype
	 *            �걨ƾ����ʽ
	 * @param waclass
	 *            н�ʷ���
	 * @param year
	 *            н�����
	 * @param beginMonth
	 *            ��ʼʱ��
	 * @param endMonth
	 *            ��ֹʱ��
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public List<AggIncomeTaxVO> getAggIncomeTaxData(boolean isForeignMonth, String unifiednumber,
			String[] declaretypes, String[] waclass, String year, String beginMonth, String endMonth)
			throws BusinessException {
		// ����ͳһ��Ż�ȡ��֯
		String[] orgs = getOrgs(unifiednumber);
		// ��ȡ����TWHR07��ֵ
		Map<String, String> twhr07Map = getSysinit(orgs, "TWHR07");
		StringBuffer qrySql = new StringBuffer();
		qrySql.append("SELECT\n");
		qrySql.append("	psn.code AS code,\n");// ��Ա����
		qrySql.append("	psn.pk_psndoc AS pk_psndoc,\n");// ��Ա
		qrySql.append("	psn.id AS id,\n");// ����֤
		qrySql.append("	wa.cyear AS cyear,\n");
		qrySql.append("	wa.cperiod AS cperiod,\n");
		qrySql.append("	wa.cyearperiod AS cyearperiod,\n");// ����ڼ�
		qrySql.append("	wa.pk_wa_class AS pk_wa_class,\n");// н�ʷ���
		qrySql.append("	wa.pk_wa_data AS pk_wa_data,\n");// н�ʷ�������
		qrySql.append("	psn.pk_hrorg AS pk_hrorg,\n");// ��֯
		qrySql.append("	psn.pk_group AS pk_group,\n");// ����
		qrySql.append(" wap.cpaydate AS cpaydate,\n");// ��������
		qrySql.append(" wap.payoffflag AS payoffflag,\n");// ���ű�־
		// mod start Ares.Tank �������������ݿ���мӼ�����ʱ�м�С���ʻ���ִ���,���Ƚ��� 2018-8-21 10:57:44
		qrySql.append("	SALARY_DECRYPT(wa." + LocalizationSysinitUtil.getTwhrlWa("TWHRLWA01") + ")" + " AS taxbase,\n");// ���ο�˰����
		qrySql.append("	SALARY_DECRYPT(wa." + LocalizationSysinitUtil.getTwhrlWa("TWHRLWA02") + ")"
				+ " AS cacu_value,\n");// ���ο�˰
		qrySql.append("	SALARY_DECRYPT(wa." + LocalizationSysinitUtil.getTwhrlWa("TWHRLWA01") + ")"
				+ " - SALARY_DECRYPT(wa." + LocalizationSysinitUtil.getTwhrlWa("TWHRLWA02") + ")" + " AS netincome,\n");// ��������
		qrySql.append("	SALARY_DECRYPT(wa." + LocalizationSysinitUtil.getTwhrlWa("TWHRLWA03") + ")"
				+ " - SALARY_DECRYPT(wa." + LocalizationSysinitUtil.getTwhrlWa("TWHRLWA04") + ")"
				+ " + SALARY_DECRYPT(wa." + LocalizationSysinitUtil.getTwhrlWa("TWHRLWA05") + ")" + " AS pickedup\n");// Ա��������
		// mod end Ares.Tank �������������ݿ���мӼ�����ʱ�м�С���ʻ���ִ���,���Ƚ��� 2018-8-21 10:57:47
		qrySql.append("FROM\n");
		qrySql.append("	wa_data wa\n");
		qrySql.append("INNER JOIN bd_psndoc psn ON wa.pk_psndoc = psn.pk_psndoc\n");
		qrySql.append("INNER JOIN wa_waclass wac on wa.pk_wa_class=wac.pk_wa_class\n");
		qrySql.append("INNER JOIN (\n");
		qrySql.append("	SELECT\n");
		qrySql.append("		wa_periodstate.pk_wa_class,\n");
		qrySql.append("		wa_periodstate.payoffflag,\n");
		qrySql.append("		wa_periodstate.cpaydate,\n");
		qrySql.append("		wa_period.cyear,\n");
		qrySql.append("		wa_period.cperiod\n");
		qrySql.append("	FROM\n");
		qrySql.append("		wa_periodstate\n");
		qrySql.append("	INNER JOIN wa_period ON (\n");
		qrySql.append("		wa_periodstate.pk_wa_period = wa_period.pk_wa_period\n");
		qrySql.append("	)\n");
		qrySql.append(") wap ON (\n");
		qrySql.append("	wa.pk_wa_class = wap.pk_wa_class\n");
		qrySql.append("	AND wa.cyear = wap.cyear\n");
		qrySql.append("	AND wa.cperiod = wap.cperiod\n");
		qrySql.append(")");
		qrySql.append("where \n");
		InSQLCreator isc = new InSQLCreator();
		// by he�걨��ʽ��ѡ���ѡ
		String inDeclaretypeSql = isc.getInSQL(declaretypes);
		qrySql.append(" wac.declareform in (" + inDeclaretypeSql + ")\n");// �걨ƾ����ʽ
		String inWaclassSql = isc.getInSQL(waclass);
		qrySql.append(" AND wac.pk_wa_class in (" + inWaclassSql + ")\n");// н�ʷ���
		if (isForeignMonth) {
			qrySql.append("AND psn." + LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01") + "='Y'\n");
		}
		qrySql.append("AND wap.payoffflag='Y'\n");// �ѷ���
		qrySql.append("AND (\n");
		for (int i = 0; i < orgs.length; i++) {
			String twhr07 = twhr07Map.get(orgs[i]);
			if (i != 0) {
				qrySql.append(" or \n");
			}
			if ("1001ZZ1000000001O22Q".equals(twhr07)) {// ��������
				String firstday = getFirstDayOfMonth(Integer.parseInt(year), Integer.parseInt(beginMonth));
				String lastday = getLastDayOfMonth(Integer.parseInt(year), Integer.parseInt(endMonth));
				qrySql.append("(wac.pk_org = '" + orgs[i] + "' AND wap.cpaydate<='" + lastday
						+ " 23:59:59' AND wap.cpaydate>='" + firstday + " 00:00:00')\n");
			} else if ("1001ZZ1000000001O22R".equals(twhr07)) {// н���ڼ�
				String inCyearperiod = isc.getInSQL(getCyearperiod(year, beginMonth, endMonth));
				qrySql.append("(wac.pk_org = '" + orgs[i] + "' AND wa.cyearperiod in (" + inCyearperiod + "))\n");
			} else {
				throw new BusinessException(ResHelper.getString("incometax", "2incometax-n-000048")/* "��֯����TWHR07δά��" */);
			}
		}
		qrySql.append(")\n");
		List<Map<String, Object>> incomeTaxList = (List<Map<String, Object>>) getDao().executeQuery(qrySql.toString(),
				new MapListProcessor());
		if (null == incomeTaxList || incomeTaxList.isEmpty()) {
			return null;
		}
		Map<String, String> voAttrTypeMap = IncomeTaxUtil.getVOFieldType(IncomeTaxVO.class);
		List<AggIncomeTaxVO> aggVoList = new ArrayList<AggIncomeTaxVO>();
		List<String> psnList = new ArrayList<String>();
		List<String> pk_wa_dataList = new ArrayList<String>();
		for (Map<String, Object> hvoMap : incomeTaxList) {
			IncomeTaxVO hvo = getHVOFromMap(hvoMap, voAttrTypeMap);
			if (null != hvo) {
				AggIncomeTaxVO aggVO = new AggIncomeTaxVO();
				// mod start Ares.Tank �Ըոս��ܵ������ټ��ܻ�ȥ 2018-8-21 11:04:06
				// ps. ����һ���ǳ�������,��Ҫ����Ϊʲô,��ֻ������bug��,��ֻ����ô��
				if (hvo.getCacu_value() != null) {
					hvo.setCacu_value(new UFDouble(hvo.getCacu_value().getDouble()));
				}
				if (hvo.getNetincome() != null) {
					hvo.setNetincome(new UFDouble(hvo.getNetincome().getDouble()));
				}
				if (hvo.getTaxbase() != null) {
					hvo.setTaxbase(new UFDouble(hvo.getTaxbase().getDouble()));
				}
				if (hvo.getPickedup() != null) {
					hvo.setPickedup(new UFDouble(hvo.getPickedup().getDouble()));
				}
				// mod end Ares.Tank �Ըոս��ܵ������ټ��ܻ�ȥ 2018-8-21 11:04:06
				aggVO.setParentVO(hvo);
				aggVoList.add(aggVO);
				psnList.add(hvo.getPk_psndoc());
				pk_wa_dataList.add(hvo.getPk_wa_data());
			}
		}
		// �ж��Ƿ��⼮Ա�����������183�죬�������⼮Ա��
		if (isForeignMonth) {
			Map<String, String> twhr08Map = getSysinit(orgs, "TWHR08");
			Map<String, String> twhr09Map = getSysinit(orgs, "TWHR09");
			Map<String, UFDouble> expireNumMap = getBaseDocUFDoubleValue(orgs, "TWSP0013");
			Map<String, PsndocVO> psnMap = getMapPsndocVO(psnList.toArray(new String[0]));
			Iterator<AggIncomeTaxVO> it = aggVoList.iterator();
			while (it.hasNext()) {
				AggIncomeTaxVO aggVO = it.next();
				IncomeTaxVO hvo = aggVO.getParentVO();
				String twhr08 = twhr08Map.get(hvo.getPk_org());
				String twhr09 = twhr09Map.get(hvo.getPk_org());
				UFDouble expireNum = expireNumMap.get(hvo.getPk_org());
				if (isExpire(twhr08, twhr09, expireNum, psnMap.get(hvo.getPk_psndoc()), hvo.getCyear(),
						hvo.getCperiod())) {
					it.remove();
				}
			}
		}
		// �жϵ����Ƿ��Ѿ�����������Ѿ�����δ�����򸲸ǣ�����������
		Map<String, UFBoolean> isgather = getIsgather(pk_wa_dataList.toArray(new String[0]));
		List<String> deleteList = new ArrayList<String>();
		Iterator<AggIncomeTaxVO> it = aggVoList.iterator();
		while (it.hasNext()) {
			AggIncomeTaxVO aggVO = it.next();
			IncomeTaxVO hvo = aggVO.getParentVO();
			if (null != isgather.get(hvo.getPk_wa_data())) {
				if (isgather.get(hvo.getPk_wa_data()).booleanValue()) {// �ѻ���
					it.remove();
				} else {
					deleteList.add(hvo.getPk_wa_data());
				}
			}
		}
		if (deleteList.size() > 0) {
			String inPsndocSql = isc.getInSQL(deleteList.toArray(new String[0]));
			String updateSql = "update hrwa_incometax set dr='1' where pk_wa_data in (" + inPsndocSql + ")";
			getDao().executeUpdate(updateSql);
		}
		return aggVoList;
	}

	/**
	 * �ж�Ա�������Ƿ���183��
	 * 
	 * @param twhr08
	 * @param twhr09
	 * @param psn
	 * @return
	 * @throws BusinessException
	 */
	private boolean isExpire(String twhr08, String twhr09, UFDouble expireNum, PsndocVO psn, String year, String month)
			throws BusinessException {
		String isResident = psn.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN02")) != null ? psn
				.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN02")).toString() : "N";
		if ("Y".equals(isResident)) {
			return true;
		}
		if ("1001ZZ1000000001NCMA".equals(twhr08)) {// ��˰�����뾳����
			UFDate permitDate = new UFDate(psn.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN03"))
					.toString());
			UFDate oneDay = new UFDate(year + "-01-01");// ������һ��
			UFDate currentMonthLastDay = new UFDate(getLastDayOfMonth(Integer.parseInt(year), Integer.parseInt(month)));// н�ʽ�������
			int days = 0;
			if (permitDate.before(oneDay)) {
				days = UFDate.getDaysBetween(oneDay, currentMonthLastDay);
			} else {
				days = UFDate.getDaysBetween(permitDate, currentMonthLastDay);
			}
			if (days - expireNum.intValue() >= 0) {
				return true;
			}
		} else if ("1001ZZ1000000001NCMB".equals(twhr08)) {// ����֤������
			if ("null".equals(twhr09) || "".equals(twhr09)) {
				throw new BusinessException(ResHelper.getString("notice", "2notice-tw-000008")/* "ϵͳ��������֤������(TWHR09)δ���ã���ά�������²�����" */);
			}
			UFDate permitDate = new UFDate(psn.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN04"))
					.toString());
			UFDate datumDay = new UFDate(twhr09);
			if (permitDate.after(datumDay)) {
				return true;
			}
		}
		return false;
	}

	public String getLastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		// �������
		cal.set(Calendar.YEAR, year);
		// �����·�
		cal.set(Calendar.MONTH, month - 1);
		// ��ȡĳ���������
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		// �����������·ݵ��������
		cal.set(Calendar.DAY_OF_MONTH, lastDay);
		// ��ʽ������
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String lastDayOfMonth = sdf.format(cal.getTime());

		return lastDayOfMonth;
	}

	public String getFirstDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		// �������
		cal.set(Calendar.YEAR, year);
		// �����·�
		cal.set(Calendar.MONTH, month - 1);
		// ��ȡĳ���������
		int firstday = cal.getMinimum(Calendar.DATE);
		// �����������·ݵ��������
		cal.set(Calendar.DAY_OF_MONTH, firstday);
		// ��ʽ������
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String firstDayOfMonth = sdf.format(cal.getTime());

		return firstDayOfMonth;
	}

	public String[] getCyearperiod(String year, String beginMonth, String endMonth) {
		Calendar cal = Calendar.getInstance();
		int begin = Integer.valueOf(beginMonth);
		int end = Integer.valueOf(endMonth);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		String[] cyearperiods = new String[end - begin + 1];
		for (int i = 0; i < cyearperiods.length; i++) {
			// �������
			cal.set(Calendar.YEAR, Integer.valueOf(year));
			// �����·�
			cal.set(Calendar.MONTH, begin - 1);
			cyearperiods[i] = sdf.format(cal.getTime());
			begin++;
		}
		return cyearperiods;
	}

	/**
	 * ��ȡ�������ò���
	 * 
	 * @param pk_org
	 * @param initcode
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, String> getSysinit(String[] pk_orgs, String initcode) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		String inOrgSql = isc.getInSQL(pk_orgs);
		String qrySql = "select pk_org,value from pub_sysinit where initcode='" + initcode + "' and pk_org in ("
				+ inOrgSql + ") and isnull(dr,0)=0";
		List<HashMap<String, Object>> listMaps = (ArrayList<HashMap<String, Object>>) getDao().executeQuery(
				qrySql.toString(), new MapListProcessor());
		Map<String, String> map = new HashMap<String, String>();
		if (listMaps != null && listMaps.size() > 0) {
			for (int i = 0; i < listMaps.size(); i++) {
				HashMap<String, Object> hashMap = listMaps.get(i);
				String pk_org = hashMap.get("pk_org") != null ? hashMap.get("pk_org").toString() : "";
				String value = hashMap.get("value") != null ? hashMap.get("value").toString() : "";
				map.put(pk_org, value);
			}
		}
		return map;
	}

	public String getSysinitValue(String pk_org, String initcode) throws BusinessException {
		String qrySql = "select value from pub_sysinit where initcode='" + initcode + "' and pk_org='" + pk_org
				+ "' and isnull(dr,0)=0";
		Object sysinitValue = getDao().executeQuery(qrySql, new ColumnProcessor());
		return String.valueOf(sysinitValue);
	}

	/**
	 * ����ͳһ��Ż�ȡ��֯
	 * 
	 * @param unifiednumber
	 * @return
	 * @throws BusinessException
	 */
	public String[] getOrgs(String unifiednumber) throws BusinessException {
		String qrySql = "select pk_hrorg from org_hrorg where " + LocalizationSysinitUtil.getTwhrlOrg("TWHRLORG03")
				+ " = '" + unifiednumber + "' or " + LocalizationSysinitUtil.getTwhrlOrg("TWHRLORG15") + " = '"
				+ unifiednumber + "'";
		List<String> orgs = (ArrayList<String>) getDao().executeQuery(qrySql, new ColumnListProcessor());
		return orgs.toArray(new String[0]);
	}

	private BaseDAO dao;

	private BaseDAO getDao() {
		if (null == dao) {
			dao = new BaseDAO();
		}
		return dao;
	}

	private Map<String, UFDouble> getBaseDocUFDoubleValue(String[] pk_orgs, String paramCode) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		String inOrgSql = isc.getInSQL(pk_orgs);
		Collection<BaseDocVO> result = getDao().retrieveByClause(BaseDocVO.class,
				"dr=0 and pk_org in (" + inOrgSql + ")  and code='" + paramCode + "'");
		if (result == null) {
			throw new BusinessException(ResHelper.getString("incometax", "2incometax-n-000049")/* "δ���xн�Y������" */
					+ paramCode);
		}
		BaseDocVO[] baseDocVOs = result.toArray(new BaseDocVO[0]);
		Map<String, UFDouble> map = new HashMap<String, UFDouble>();
		for (int i = 0; i < baseDocVOs.length; i++) {
			BaseDocVO baseDocVO = baseDocVOs[i];
			UFDouble value = UFDouble.ZERO_DBL;
			if (baseDocVO.getDoctype() == 1) {
				value = baseDocVO.getNumbervalue();
			} else if (baseDocVO.getDoctype() == 2) {
				value = baseDocVO.getNumbervalue().div(100);
			}
			map.put(baseDocVO.getPk_org(), value);
		}
		return map;
	}

	private UFDouble getBaseDocUFDoubleValue(String pk_org, String paramCode) throws BusinessException {
		IBasedocPubQuery baseQry = NCLocator.getInstance().lookup(IBasedocPubQuery.class);
		BaseDocVO baseDoc = baseQry.queryBaseDocByCode(pk_org, paramCode);
		if (baseDoc == null) {
			throw new BusinessException(ResHelper.getString("incometax", "2incometax-n-000049")/* "δ���xн�Y������" */
					+ paramCode);
		}

		UFDouble value = UFDouble.ZERO_DBL;
		if (baseDoc.getDoctype() == 1) {
			value = baseDoc.getNumbervalue();
		} else if (baseDoc.getDoctype() == 2) {
			value = baseDoc.getNumbervalue().div(100);
		}

		return value;
	}

	private IncomeTaxVO getHVOFromMap(Map<String, Object> viewValueMap, Map<String, String> voAttrTypeMap) {
		if (null != viewValueMap && !viewValueMap.isEmpty()) {
			IncomeTaxVO hvo = new IncomeTaxVO();
			for (String voAttr : voAttrTypeMap.keySet()) {
				String value = IncomeTaxUtil.getStringValue(viewValueMap.get(voAttr));
				if (StringUtils.isNotBlank(voAttr)) {
					String attrType = voAttrTypeMap.get(voAttr);
					if (StringUtils.isNotBlank(attrType)) {
						IncomeTaxUtil.setVoFieldValueByType(hvo, attrType, voAttr, value);
					}
				}
			}
			return hvo;
		}
		return null;
	}

	private Map<String, PsndocVO> getMapPsndocVO(String[] pks) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		String inPsndocSql = isc.getInSQL(pks);
		String where = PsndocVO.PK_PSNDOC + " in(" + inPsndocSql + ")";
		@SuppressWarnings("unchecked")
		List<PsndocVO> list = (ArrayList<PsndocVO>) getDao().retrieveByClause(PsndocVO.class, where);
		Map<String, PsndocVO> map = new HashMap<String, PsndocVO>();
		for (int i = 0; i < list.size(); i++) {
			PsndocVO psndocVO = list.get(i);
			map.put(psndocVO.getPk_psndoc(), psndocVO);
		}
		return map;
	}

	private Map<String, UFBoolean> getIsgather(String[] pk_wa_data) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		String inPsndocSql = isc.getInSQL(pk_wa_data);
		String qrySql = "SELECT\n" + "	pk_wa_data,\n" + "	isgather\n" + "FROM\n" + "	hrwa_incometax\n" + "WHERE\n"
				+ "	pk_wa_data IN (" + inPsndocSql + ")\n" + "AND ISNULL(dr, 0) = 0";
		List<HashMap<String, Object>> listMaps = (ArrayList<HashMap<String, Object>>) getDao().executeQuery(
				qrySql.toString(), new MapListProcessor());
		Map<String, UFBoolean> map = new HashMap<String, UFBoolean>();
		if (listMaps != null && listMaps.size() > 0) {
			for (int i = 0; i < listMaps.size(); i++) {
				HashMap<String, Object> hashMap = listMaps.get(i);
				String pk_org = hashMap.get("pk_wa_data") != null ? hashMap.get("pk_wa_data").toString() : "";
				String value = hashMap.get("isgather") != null ? hashMap.get("isgather").toString() : "N";
				map.put(pk_org, new UFBoolean(value));
			}
		}
		return map;
	}

	/**
	 * @author ward.wong
	 * @date 20180126
	 * @version v1.0
	 * @�������� ֤����жϷ���ʵ��
	 * 
	 */
	@Override
	public String getIdtypeno(String pk_psndoc, String cyearperiod) throws BusinessException {
		PsndocVO psndoc = (PsndocVO) getDao().retrieveByPK(PsndocVO.class, pk_psndoc);
		if (null == psndoc.getCountry() || "".equals(psndoc.getCountry())
				|| "0001Z010000000079UJK".equals(psndoc.getCountry())) {
			String isForeign = psndoc.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01")) != null ? psndoc
					.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01")).toString() : "N";
			if ("N".equals(isForeign)) {
				return "0";
			} else {
				throw new BusinessException(ResHelper.getString("incometax", "2incometax-n-000051")/* "��Ա" */
						+ psndoc.getName() + "(" + psndoc.getCode()
						+ ResHelper.getString("incometax", "2incometax-n-000050") /* "),֤��Ŵ������飡" */);
			}
		} else if ("0001Z010000000079UJJ".equals(psndoc.getCountry())) {
			String isForeign = psndoc.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01")) != null ? psndoc
					.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01")).toString() : "N";
			if ("N".equals(isForeign)) {
				throw new BusinessException(ResHelper.getString("incometax", "2incometax-n-000051")/* "��Ա" */
						+ psndoc.getName() + "(" + psndoc.getCode()
						+ ResHelper.getString("incometax", "2incometax-n-000050") /* "),֤��Ŵ������飡" */);
			} else {
				String twhr08 = getSysinitValue(psndoc.getPk_org(), "TWHR08");
				String twhr09 = getSysinitValue(psndoc.getPk_org(), "TWHR09");
				UFDouble expireNum = getBaseDocUFDoubleValue(psndoc.getPk_org(), "TWSP0013");
				String cyear = cyearperiod.substring(0, 4);
				String cperiod = cyearperiod.substring(5, 6);
				if (isExpire(twhr08, twhr09, expireNum, psndoc, cyear, cperiod)) {
					return "3";
				} else {
					return "5";
				}
			}
		} else {
			String isForeign = psndoc.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01")) != null ? psndoc
					.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01")).toString() : "N";
			if ("N".equals(isForeign)) {
				throw new BusinessException(ResHelper.getString("incometax", "2incometax-n-000051")/* "��Ա" */
						+ psndoc.getName() + "(" + psndoc.getCode()
						+ ResHelper.getString("incometax", "2incometax-n-000050") /* "),֤��Ŵ������飡" */);
			} else {
				String twhr08 = getSysinitValue(psndoc.getPk_org(), "TWHR08");
				String twhr09 = getSysinitValue(psndoc.getPk_org(), "TWHR09");
				UFDouble expireNum = getBaseDocUFDoubleValue(psndoc.getPk_org(), "TWSP0013");
				String cyear = cyearperiod.substring(0, 4);
				String cperiod = cyearperiod.substring(5, 6);
				if (isExpire(twhr08, twhr09, expireNum, psndoc, cyear, cperiod)) {
					return "3";
				} else {
					return "7";
				}
			}
		}
	}

	@Override
	public void deleteSumIncomeTax(String[] pk_incometax) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		String inPsndocSql = isc.getInSQL(pk_incometax);
		// ��ѯ��������Ϣ������VO
		String qrySql = "select pk_sumincometax from hrwa_incometaxdetail where pk_incometax in(" + inPsndocSql + ")";
		List<String> pk_sumincometaxs = (List<String>) getDao().executeQuery(qrySql, new ColumnListProcessor());
		// ɾ��������Ϣ����VO
		if (pk_sumincometaxs != null && pk_sumincometaxs.size() > 0)
			getDao().deleteByPKs(SumIncomeTaxVO.class, pk_sumincometaxs.toArray(new String[0]));
		// ɾ��������Ϣ�ӱ�VO
		getDao().deleteByClause(CIncomeTaxVO.class, "pk_incometax in(" + inPsndocSql + ")");
	}

	@Override
	public void markIncomeTaxVO(String[] pk_incometax) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		String inPsndocSql = isc.getInSQL(pk_incometax);
		String updateQry = "update hrwa_incometax set isdeclare='Y',ts='" + new UFDateTime().toString()
				+ "' where pk_incometax in(" + inPsndocSql + ") and isnull(dr,0)=0";
		getDao().executeUpdate(updateQry);

	}

	@Override
	public void unMarkIncomeTaxVO(String[] pk_incometax) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		String inPsndocSql = isc.getInSQL(pk_incometax);
		String updateQry = "update hrwa_incometax set isdeclare='N',ts='" + new UFDateTime().toString()
				+ "' where pk_incometax in(" + inPsndocSql + ") and isnull(dr,0)=0";
		getDao().executeUpdate(updateQry);

	}

	@Override
	public void cancleGather(String[] pk_incometax) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		String inPsndocSql = isc.getInSQL(pk_incometax);
		String updateQry = "update hrwa_incometax set isgather='N',ts='" + new UFDateTime().toString()
				+ "' where pk_incometax in(" + inPsndocSql + ") and isnull(dr,0)=0";
		getDao().executeUpdate(updateQry);

	}

	@Override
	public AggSumIncomeTaxVO getAggSumIncomeTaxByIncome(String pk_incometax) throws BusinessException {
		// �����걨��ϸ����ѯ���걨���ܵ�������PK
		String qrySql = "SELECT pk_sumincometax from hrwa_incometaxdetail where pk_incometax='" + pk_incometax
				+ "' and isnull(dr,0)=0";
		Object object = getDao().executeQuery(qrySql, new ColumnProcessor());
		if (object == null) {
			return null;
		}
		// ��ѯ����VO
		SumIncomeTaxVO sumIncomeTaxVO = (SumIncomeTaxVO) getDao().retrieveByPK(SumIncomeTaxVO.class, object.toString());
		if (sumIncomeTaxVO == null) {
			return null;
		}
		// ��ѯ�ӱ�VO
		Collection<CIncomeTaxVO> incomeTaxVOs = getDao().retrieveByClause(CIncomeTaxVO.class,
				"pk_sumincometax='" + object.toString() + "' and isnull(dr,0)=0");
		AggSumIncomeTaxVO aggvo = new AggSumIncomeTaxVO();
		aggvo.setParentVO(sumIncomeTaxVO);
		aggvo.setChildrenVO(incomeTaxVOs.toArray(new CIncomeTaxVO[0]));
		return aggvo;
	}

	public Map<String, String> getPsnNameByPks(String[] pks) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		String inPsndocSql = isc.getInSQL(pks);
		String where = PsndocVO.PK_PSNDOC + " in(" + inPsndocSql + ")";
		@SuppressWarnings("unchecked")
		List<PsndocVO> lPsndocVOs = (ArrayList<PsndocVO>) getDao().retrieveByClause(PsndocVO.class, where);
		Map<String, String> psndocMap = new HashMap<String, String>();
		for (PsndocVO vo : lPsndocVOs) {
			if (null == vo.getName2() || "".equals(vo.getName2())) {
				psndocMap.put(vo.getPk_psndoc(), vo.getName());
			} else {
				psndocMap.put(vo.getPk_psndoc(), vo.getName2());
			}
		}
		return psndocMap;
	}

	public Map<String, String> getWaClassName(String[] pks) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		String inSql = isc.getInSQL(pks);
		String qrySql = "select pk_wa_class,name from wa_waclass where pk_wa_class in(" + inSql + ")";
		@SuppressWarnings("unchecked")
		List<HashMap<String, Object>> listMaps = (ArrayList<HashMap<String, Object>>) getDao().executeQuery(
				qrySql.toString(), new MapListProcessor());
		Map<String, String> map = new HashMap<String, String>();
		if (listMaps != null && listMaps.size() > 0) {
			for (int i = 0; i < listMaps.size(); i++) {
				HashMap<String, Object> hashMap = listMaps.get(i);
				String pk_wa_class = hashMap.get("pk_wa_class") != null ? hashMap.get("pk_wa_class").toString() : "";
				String name = hashMap.get("name") != null ? hashMap.get("name").toString() : "";
				map.put(pk_wa_class, name);
			}
		}
		return map;
	}

	@Override
	public AggSumIncomeTaxVO getAggSumIncomeTaxByPK(String pk) throws BusinessException {
		if (pk == null) {
			return null;
		}
		// ��ѯ����VO
		SumIncomeTaxVO sumIncomeTaxVO = (SumIncomeTaxVO) getDao().retrieveByPK(SumIncomeTaxVO.class, pk);
		if (sumIncomeTaxVO == null) {
			return null;
		}
		// ��ѯ�ӱ�VO
		@SuppressWarnings("unchecked")
		Collection<CIncomeTaxVO> incomeTaxVOs = getDao().retrieveByClause(CIncomeTaxVO.class,
				"pk_sumincometax='" + pk + "' and isnull(dr,0)=0");
		AggSumIncomeTaxVO aggvo = new AggSumIncomeTaxVO();
		aggvo.setParentVO(sumIncomeTaxVO);
		aggvo.setChildrenVO(incomeTaxVOs.toArray(new CIncomeTaxVO[0]));
		return aggvo;
	}

	@Override
	public List<String> getLegalOrgByHROrg(String pk_hrorg) throws BusinessException {
		return LegalOrgUtilsEX.getRelationOrgWithSalary4NHI(pk_hrorg);
	}

}