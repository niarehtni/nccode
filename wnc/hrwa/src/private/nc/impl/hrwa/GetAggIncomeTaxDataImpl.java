package nc.impl.hrwa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.wa.util.LocalizationSysinitUtil;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.itf.hrwa.IGetAggIncomeTaxData;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.pubitf.twhr.IBasedocPubQuery;
import nc.vo.bd.countryzone.CountryZoneVO;
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
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.twhr.basedoc.BaseDocVO;
import nc.vo.wa.payfile.PayfileVO;

import org.apache.commons.lang.StringUtils;

/**
 * @author ward.wong
 * @date 20180126
 * @version v1.0
 * @�������� ��ȡ�걨��ϸ�����ܽӿ�ʵ����
 * 
 */
public class GetAggIncomeTaxDataImpl implements IGetAggIncomeTaxData {

	private static final String Residence_Due_Date = "TWHRLPSN04";

	/**
	 * @�������� �����������������Ա�걨��ϸ��
	 * @param unifiednumber
	 *            ͳһ���
	 * @param declaretype
	 *            �걨ƾ����ʽ
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
	public List<AggIncomeTaxVO> getPsnIncomeTaxData(String unifiednumber, String[] declaretype, String year,
			String beginMonth, String endMonth) throws BusinessException {
		// ����ͳһ��Ż�ȡ��֯
		String[] orgs = getOrgs(unifiednumber);
		StringBuffer qrySql = new StringBuffer();
		qrySql.append("SELECT\n");
		qrySql.append("	psn.pk_hrorg AS pk_hrorg,\n");// ������Դ��֯
		qrySql.append("	hi_psndoc_ptcost.declareformat AS declaretype,\n");// �걨��ʽ
		qrySql.append("	hi_psndoc_ptcost.pk_psndoc_sub AS pk_wa_data,\n");// �����������
		qrySql.append("	hi_psndoc_ptcost.projectcode AS projectcode,\n");// ��Ŀ����
		qrySql.append("	hi_psndoc_ptcost.feetype AS feetype,\n");// �ѱ�
		qrySql.append("	hi_psndoc_ptcost.biztype AS biztype,\n");// ҵ��
		qrySql.append("	psn.code AS code,\n");
		qrySql.append("	psn.pk_psndoc AS pk_psndoc,\n");
		qrySql.append("	psn.id AS id,\n");
		qrySql.append("	psn.pk_group AS pk_group,\n");
		qrySql.append(" hi_psndoc_ptcost.paydate AS cpaydate,\n");
		qrySql.append(" SUBSTRING(hi_psndoc_ptcost.paydate,1,4) || SUBSTRING(hi_psndoc_ptcost.paydate,6,2) AS cperiod,\n");
		qrySql.append(" SUBSTRING(hi_psndoc_ptcost.paydate,1,4) || SUBSTRING(hi_psndoc_ptcost.paydate,6,2) AS cyearperiod,\n");// ����ڼ�
		qrySql.append(" hi_psndoc_ptcost.payamount AS taxbase,\n");
		qrySql.append(" hi_psndoc_ptcost.taxamount AS cacu_value,\n");
		qrySql.append(" ISNULL(hi_psndoc_ptcost.payamount, 0)-ISNULL(hi_psndoc_ptcost.taxamount, 0) AS netincome,\n");
		qrySql.append(" 'N' AS isforeignmonthdec,\n");
		qrySql.append(" '" + year + "' AS cyear\n");
		qrySql.append(" FROM hi_psndoc_ptcost hi_psndoc_ptcost\n");
		qrySql.append(" INNER JOIN bd_psndoc psn ON psn.pk_psndoc = hi_psndoc_ptcost.pk_psndoc\n");
		qrySql.append(" WHERE ISNULL(hi_psndoc_ptcost.dr, 0)=0 AND ISNULL(psn.dr, 0)=0\n");

		InSQLCreator isc = new InSQLCreator();
		String inDeclaretypeSql = isc.getInSQL(declaretype);
		qrySql.append(" AND hi_psndoc_ptcost.declareformat in (" + inDeclaretypeSql + ")\n");// �걨ƾ����ʽ
		String[] startStr = beginMonth.split("-");
		String[] endStr = endMonth.split("-");
		int startyear = Integer.parseInt(startStr[0]);
		int startMonth = Integer.parseInt(startStr[1]);
		int endyear = Integer.parseInt(endStr[0]);
		int endMon = Integer.parseInt(endStr[1]);
		String firstday = getFirstDayOfMonth(startyear, startMonth);
		// String firstday =
		// getFirstDayOfMonth(Integer.parseInt(year),Integer.parseInt(beginMonth));
		String lastday = getLastDayOfMonth(endyear, endMon);
		qrySql.append(" AND hi_psndoc_ptcost.paydate>='" + firstday + "'\n");
		qrySql.append(" AND hi_psndoc_ptcost.paydate<='" + lastday + "'\n");

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
					hvo.setCacu_value(new UFDouble(SalaryEncryptionUtil.encryption(hvo.getCacu_value().getDouble())));
				}
				if (hvo.getNetincome() != null) {
					hvo.setNetincome(new UFDouble(SalaryEncryptionUtil.encryption(hvo.getNetincome().getDouble())));
				}
				if (hvo.getTaxbase() != null) {
					hvo.setTaxbase(new UFDouble(SalaryEncryptionUtil.encryption(hvo.getTaxbase().getDouble())));
				}
				if (hvo.getPickedup() != null) {
					hvo.setPickedup(new UFDouble(SalaryEncryptionUtil.encryption(hvo.getPickedup().getDouble())));
				}
				// mod end Ares.Tank �Ըոս��ܵ������ټ��ܻ�ȥ 2018-8-21 11:04:06
				aggVO.setParentVO(hvo);
				aggVoList.add(aggVO);
				psnList.add(hvo.getPk_psndoc());
				pk_wa_dataList.add(hvo.getPk_wa_data());
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
	 * @�������� �������������걨��ϸ��
	 * @param isForeignDeparture
	 *            �⼮�T���x�����
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
	 * @param offBeginDate
	 *            �x�_ʼ����
	 * @param offEndDate
	 *            �x�Y������
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public List<AggIncomeTaxVO> getAggIncomeTaxData(boolean isForeignDeparture, boolean isForeignMonth,
			String unifiednumber, String[] declaretype, String[] waclass, String year, String beginMonth,
			String endMonth, UFLiteralDate offBeginDate, UFLiteralDate offEndDate) throws BusinessException {
		// ����ͳһ��Ż�ȡ��֯
		String[] orgs = getOrgs(unifiednumber);
		// ��ȡ����TWHR07��ֵ
		Map<String, String> twhr07Map = getSysinit(orgs, "TWHR07");
		StringBuffer qrySql = new StringBuffer();
		qrySql.append("SELECT\n");
		qrySql.append("	psn.code AS code,\n");// ��Ա����
		qrySql.append("	psn.pk_psndoc AS pk_psndoc,\n");// ��Ա
		qrySql.append("	psn.id AS id,\n");// ���֤
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
				+ " + SALARY_DECRYPT(wa." + LocalizationSysinitUtil.getTwhrlWa("TWHRLWA05") + ")" + " AS pickedup,\n");// Ա��������
		qrySql.append(" wac.declareform AS declaretype,\n");// �걨��ʽ
		qrySql.append(" wa.biztype AS biztype,\n");// ҵ���
		qrySql.append(" wa.projectcode AS projectcode,\n");// ��Ŀ��
		qrySql.append(" wa.feetype AS feetype\n");// ���ñ�
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
		String inDeclaretypeSql = isc.getInSQL(declaretype);
		qrySql.append(" wac.declareform in (" + inDeclaretypeSql + ")\n");// �걨ƾ����ʽ

		// ���һ�����ڶ�η�н,���ȡ��η�н��н�ʷ���,���򷵻ص�ǰ��н�ʷ���.
		waclass = getRealWaclass(waclass, beginMonth, endMonth, year);
		String inWaclassSql = isc.getInSQL(waclass);
		qrySql.append(" AND wac.pk_wa_class in (" + inWaclassSql + ")\n");// н�ʷ���
		if (isForeignMonth) {
			qrySql.append("AND psn." + LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01") + "='Y'\n");
		}
		qrySql.append("AND wap.payoffflag='Y' AND wap.cpaydate is not null \n");// �ѷ���
		qrySql.append("AND (\n");
		String[] startStr = beginMonth.split("-");
		String[] endStr = endMonth.split("-");
		int startyear = Integer.parseInt(startStr[0]);
		int startMonth = Integer.parseInt(startStr[1]);
		int endyear = Integer.parseInt(endStr[0]);
		int endMon = Integer.parseInt(endStr[1]);
		String firstday = getFirstDayOfMonth(startyear, startMonth);
		String lastday = getLastDayOfMonth(endyear, endMon);
		for (int i = 0; i < orgs.length; i++) {
			String twhr07 = twhr07Map.get(orgs[i]);
			if (i != 0) {
				qrySql.append(" or \n");
			}
			if ("1001ZZ1000000001O22Q".equals(twhr07)) {// ��������
				qrySql.append("(wac.pk_org = '" + orgs[i] + "' AND wap.cpaydate<='" + lastday
						+ " 23:59:59' AND wap.cpaydate>='" + firstday + " 00:00:00')\n");
			} else if ("1001ZZ1000000001O22R".equals(twhr07)) {// н���ڼ�
				String inCyearperiod = isc.getInSQL(getCyearperiod(year, beginMonth, endMonth));
				qrySql.append("(wac.pk_org = '" + orgs[i]
						+ "' AND (cast(wa.cyear as varchar(20)) || cast(wa.cperiod as varchar(20))) in ("
						+ inCyearperiod + "))\n");
			} else {
				throw new BusinessException(ResHelper.getString("incometax", "2incometax-n-000048")/* "��֯����TWHR07δά��" */);
			}
		}
		qrySql.append(")\n");
		// mod by Connie.ZH
		// add filter to sql(when departure declare)
		// 2019-06-04 start
		if (isForeignDeparture) {
			String sql4Residence_Due_Date_Column = "select value from pub_sysinit where initcode='"
					+ Residence_Due_Date + "' and dr=0 and pk_org = 'GLOBLE00000000000000'";
			String Residence_Due_Date_Column = (String) getDao().executeQuery(sql4Residence_Due_Date_Column,
					new ResultSetProcessor() {
						@Override
						public Object handleResultSet(ResultSet rs) throws SQLException {
							if (rs.next()) {
								return rs.getString(1);
							}
							return null;
						}
					});
			if (StringUtils.isEmpty(Residence_Due_Date_Column)) {
				throw new BusinessException(ResHelper.getString("incometax", "2incometax-n-000055")/* "�����趨(ȫ��)-�뾳����(TWHRLPSN04)-����δά��" */);
			}
			UFDate dueDate = new UFDate(lastday);
			Map<String, String> twhr14Map = getSysinit(orgs, "TWHR14");// н�Y�l����춾����C������ǰN��rҕ���x�����
			qrySql.append("AND (\n");
			for (int i = 0; i < orgs.length; i++) {
				String twhr14 = twhr14Map.get(orgs[i]);
				if (i != 0) {
					qrySql.append(" or \n");
				}
				if (!StringUtils.isEmpty(twhr14)) {
					UFDate tmpDate = dueDate.getDateAfter(new Integer(twhr14));
					qrySql.append(" (psn.pk_org = '" + orgs[i] + "' AND psn." + Residence_Due_Date_Column + " <= ('"
							+ tmpDate.toString().substring(0, 10) + " 23:59:59'))\n");
				} else {
					qrySql.append(" (psn.pk_org = '" + orgs[i] + "' AND psn." + Residence_Due_Date_Column + " <= ('"
							+ dueDate.toString().substring(0, 10) + " 23:59:59'))\n");
				}

				if (offBeginDate != null && offEndDate != null) {
					qrySql.append(" and ((select count(pk_psnjob) from hi_psnjob where pk_psndoc=wa.pk_psndoc and trnsevent=4 and begindate between '"
							+ offBeginDate.toString() + "' and '" + offEndDate.toString() + "') > 0)");
				}

			}
			qrySql.append(")\n");
		}
		// 2019-06-04 end
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
					hvo.setCacu_value(new UFDouble(SalaryEncryptionUtil.encryption(hvo.getCacu_value().getDouble())));
				}
				if (hvo.getNetincome() != null) {
					hvo.setNetincome(new UFDouble(SalaryEncryptionUtil.encryption(hvo.getNetincome().getDouble())));
				}
				if (hvo.getTaxbase() != null) {
					hvo.setTaxbase(new UFDouble(SalaryEncryptionUtil.encryption(hvo.getTaxbase().getDouble())));
				}
				if (hvo.getPickedup() != null) {
					hvo.setPickedup(new UFDouble(SalaryEncryptionUtil.encryption(hvo.getPickedup().getDouble())));
				}
				// mod end Ares.Tank �Ըոս��ܵ������ټ��ܻ�ȥ 2018-8-21 11:04:06
				aggVO.setParentVO(hvo);
				aggVoList.add(aggVO);
				psnList.add(hvo.getPk_psndoc());
				pk_wa_dataList.add(hvo.getPk_wa_data());
			}
		}

		// Map<String, String> twhr08Map = getSysinit(orgs, "TWHR08");
		// Map<String, String> twhr09Map = getSysinit(orgs, "TWHR09");
		Map<String, UFDouble> expireNumMap = getBaseDocUFDoubleValue(orgs, "TWSP0013");
		Map<String, PsndocVO> psnMap = getMapPsndocVO(psnList.toArray(new String[0]));

		// MOD(�⼮����δ��183��Ա���������ɵ��ж��޸�)
		// by ssx on 2019-05-21
		if (!isForeignDeparture) {
			if (isForeignMonth) {
				// �⼮�����걨���ж��Ƿ��⼮Ա�����������183�죬�������⼮Ա��
				Iterator<AggIncomeTaxVO> it = aggVoList.iterator();
				while (it.hasNext()) {
					AggIncomeTaxVO aggVO = it.next();
					IncomeTaxVO hvo = aggVO.getParentVO();
					// String twhr08 = twhr08Map.get(hvo.getPk_hrorg());
					// String twhr09 = twhr09Map.get(hvo.getPk_hrorg());
					UFBoolean isForeign = (UFBoolean) psnMap.get(hvo.getPk_psndoc()).getAttributeValue(
							LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01"));
					UFBoolean isResident = (UFBoolean) psnMap.get(hvo.getPk_psndoc()).getAttributeValue(
							LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN02"));
					isForeign = isForeign == null ? UFBoolean.FALSE : isForeign;
					UFDouble expireNum = expireNumMap.get(hvo.getPk_hrorg());
					// ��������n�����e�`��������ȡֵ���� by George 20200304 ȱ��Bug #33464
					// �� �ˆT�Y��(bd_psndoc) �� �Ƿ��⼮�T��(glbdef7) �]���x����r������ null ��
					// 'N'��null�r����NullPointerException

					// ssx modified on 2020-08-21
					// �Д�183��׃���㷨
					// isExpire(twhr08, twhr09, expireNum,
					// psnMap.get(hvo.getPk_psndoc()),
					// hvo.getCyear(),hvo.getCperiod())
					if (!isForeign.booleanValue()
							|| (isForeign.booleanValue() && (isResident.booleanValue() || isExpire(expireNum,
									psnMap.get(hvo.getPk_psndoc()), hvo.getCyear(),
									hvo.getCpaydate().toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE))))) {
						// end
						it.remove();
					}
				}
			} else {
				// �������걨���ж��Ƿ��⼮Ա��������183�죬Ӧ���걨��Χ��ɾ��
				Iterator<AggIncomeTaxVO> it = aggVoList.iterator();
				LocalizationSysinitUtil.reloadRefs();
				while (it.hasNext()) {
					AggIncomeTaxVO aggVO = it.next();
					IncomeTaxVO hvo = aggVO.getParentVO();
					// String twhr08 = twhr08Map.get(hvo.getPk_hrorg());
					// String twhr09 = twhr09Map.get(hvo.getPk_hrorg());
					UFBoolean isForeign = (UFBoolean) psnMap.get(hvo.getPk_psndoc()).getAttributeValue(
							LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01"));

					// ��������n�����e�`��������ȡֵ���� by George 20200304 ȱ��Bug #33464
					// �� �ˆT�Y��(bd_psndoc) �� �Ƿ��⼮�T��(glbdef7) �]���x����r������ null ��
					// 'N'��null�r����NullPointerException
					isForeign = isForeign == null ? UFBoolean.FALSE : isForeign;

					UFDouble expireNum = expireNumMap.get(hvo.getPk_hrorg());

					// ssx modified on 2020-08-21
					// �Д�183��׃���㷨
					// isExpire(twhr08, twhr09, expireNum,
					// psnMap.get(hvo.getPk_psndoc()),
					// hvo.getCyear(),hvo.getCperiod())
					if (isForeign.booleanValue()
							&& !isExpire(expireNum, psnMap.get(hvo.getPk_psndoc()), hvo.getCyear(), hvo.getCpaydate()
									.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE))) {
						// end
						it.remove();
					}
				}
			}
		}
		// end

		// ��������n�����e�`��������ȡֵ���� by George 20200304 ȱ��Bug #33464
		// �жϵ����Ƿ��Ѿ�����������Ѿ�����δ����򸲸ǣ�����������
		Map<String, UFBoolean> isdeclare = getIsdeclare(pk_wa_dataList.toArray(new String[0]));
		List<String> deleteList = new ArrayList<String>();
		Iterator<AggIncomeTaxVO> it = aggVoList.iterator();
		while (it.hasNext()) {
			AggIncomeTaxVO aggVO = it.next();
			IncomeTaxVO hvo = aggVO.getParentVO();
			// �Д��Ƿ����
			if (null != isdeclare.get(hvo.getPk_wa_data())) {
				if (isdeclare.get(hvo.getPk_wa_data()).booleanValue()) {// �����
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
	 * ���һ�����ڶ�η�н,���ȡ��η�н��н�ʷ���,���򷵻ص�ǰ��н�ʷ���. ע:��׼ƽ̨�и���,��η�нʱ,ʵ������ʹ�ò�ͬ��н�ʷ�����н��
	 * 
	 * @param waclass
	 * @return
	 * @throws DAOException
	 */
	private String[] getRealWaclass(String[] waclass, String startMonthStr, String endMonthStr, String yearStr)
			throws DAOException {
		Set<String> result = new HashSet<>();
		int startyear = 0;
		int endyear = 0;
		int startMonth = 0;
		int endMonth = 0;
		try {
			String[] startStr = startMonthStr.split("-");
			String[] endStr = endMonthStr.split("-");
			startyear = Integer.parseInt(startStr[0]);
			startMonth = Integer.parseInt(startStr[1]);
			endyear = Integer.parseInt(endStr[0]);
			endMonth = Integer.parseInt(endStr[1]);
		} catch (NumberFormatException e) {
			return waclass;
		}
		if (0 == startyear || 0 == startMonth || 0 == endMonth || 0 == endyear) {
			return waclass;
		}
		if (waclass != null && startyear != 0 && startMonth != 0 && endMonth != 0 && endyear != 0) {
			List<String> exStrList = new ArrayList<>();
			// ƴһ�º�׺
			for (int j = startyear; j <= endyear; j++) {
				int begin = 1;
				int end = 12;
				if (startyear == j) {
					begin = startMonth;
				}
				if (endyear == j) {
					end = endMonth;
				}
				for (int i = begin; i <= end; i++) {
					// 201712_times_
					String ex = "" + j + "" + (i < 10 ? "0" + i : i) + "_times_";
					exStrList.add(ex);
				}
			}

			StringBuilder sqlStr = new StringBuilder();
			for (String pkWaClass : waclass) {
				// ��ѯ���н�ʷ�����code
				sqlStr.delete(0, sqlStr.length());
				sqlStr.append("select code from wa_waclass where pk_wa_class = '").append(pkWaClass).append("'");
				String code = (String) getDao().executeQuery(sqlStr.toString(), new ColumnProcessor());
				result.add(pkWaClass);

				sqlStr.delete(0, sqlStr.length());
				// mod by Connie.ZH
				// add showflag = 'Y' to filter multi-waclass
				// 2019-06-08 STARTED
				sqlStr.append(" select pk_wa_class from wa_waclass where showflag= 'Y' and ");
				// END
				int i = 0;
				for (String ex : exStrList) {
					if (0 == i) {
						sqlStr.append(" code like '").append(code).append(ex).append("%'");
					} else {
						sqlStr.append(" or code like '").append(code).append(ex).append("%'");
					}
					i++;
				}
				sqlStr.append("");
				List<Map<String, String>> realPkRowList = (List) getDao().executeQuery(sqlStr.toString(),
						new MapListProcessor());
				int count = 0;
				for (Map<String, String> rowMap : realPkRowList) {
					if (rowMap != null && rowMap.get("pk_wa_class") != null) {
						count++;
						result.add(rowMap.get("pk_wa_class"));
					}
				}
				if (count <= 0) {
					result.add(pkWaClass);
				}
			}
			return result.toArray(new String[0]);
		}
		return waclass;
	}

	@SuppressWarnings("unchecked")
	private boolean isExpire(UFDouble expireNum, PsndocVO psn, String year, UFLiteralDate countDate)
			throws BusinessException {
		// ���o���ղ�ԃ�T������
		Map<String, String> inOutDate = (Map<String, String>) this
				.getDao()
				.executeQuery(
						"select begindate, isnull(enddate, '9999-12-31') enddate from hi_psnorg where pk_psnorg in (select pk_psnorg from hi_psnjob where pk_psndoc='"
								+ psn.getPk_psndoc()
								+ "' and '"
								+ countDate.toString()
								+ "' between begindate and isnull(enddate, '9999-12-31') and ismainjob='Y') and lastflag='Y'",
						new MapProcessor());

		if (inOutDate == null) {
			throw new BusinessException("�@ȡ�T�� [" + psn.getCode() + "] �ĵ��Օr���F�e�`��");
		}

		UFLiteralDate baseDate = new UFLiteralDate(year + "-01-01");
		if (Integer.valueOf(inOutDate.get("begindate").substring(0, 4)).equals(Integer.valueOf(year))) {
			// �Ǯ�����: MIN(�x��,�o����) - 1/1 + 1
			baseDate = new UFLiteralDate(inOutDate.get("begindate"));
		}
		// ������: MIN(�x��,�o����) - ���� + 1

		countDate = countDate.before(new UFLiteralDate(inOutDate.get("enddate"))) ? countDate : new UFLiteralDate(
				inOutDate.get("enddate"));

		return (UFLiteralDate.getDaysBetween(baseDate, countDate) + 1) > expireNum.intValue();
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		List<String> cyearperiodList = new ArrayList<String>();

		String[] startStr = beginMonth.split("-");
		String[] endStr = endMonth.split("-");
		int startyear = Integer.parseInt(startStr[0]);
		int startMonth = Integer.parseInt(startStr[1]);
		int endyear = Integer.parseInt(endStr[0]);
		int endMon = Integer.parseInt(endStr[1]);
		// ƴһ�º�׺
		for (int j = startyear; j <= endyear; j++) {
			int begin = 1;
			int end = 12;
			if (startyear == j) {
				begin = startMonth;
			}
			if (endyear == j) {
				end = endMon;
			}
			for (int i = begin; i <= end; i++) {
				// �������
				cal.set(Calendar.YEAR, Integer.valueOf(j));
				// �����·�
				cal.set(Calendar.MONTH, i - 1);
				cyearperiodList.add(sdf.format(cal.getTime()));
				// cyearperiods[i] = sdf.format(cal.getTime());
			}
		}
		String[] cyearperiods = new String[cyearperiodList.size()];
		cyearperiodList.toArray(cyearperiods);
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

	/**
	 * �жϵ����Ƿ��Ѿ�����������Ѿ�����δ�����򸲸ǣ�����������
	 * 
	 * @param pk_wa_data
	 * @return
	 * @throws BusinessException
	 */
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
	 * �жϵ����Ƿ��Ѿ�����������Ѿ�����δ����򸲸ǣ�����������
	 * 
	 * @author by George
	 * @param pk_wa_data
	 * @date 20200204
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, UFBoolean> getIsdeclare(String[] pk_wa_data) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		String inPsndocSql = isc.getInSQL(pk_wa_data);
		String qrySql = "SELECT\n" + "	pk_wa_data,\n" + "	isdeclare\n" + "FROM\n" + "	hrwa_incometax\n" + "WHERE\n"
				+ "	pk_wa_data IN (" + inPsndocSql + ")\n" + "AND ISNULL(dr, 0) = 0";
		List<HashMap<String, Object>> listMaps = (ArrayList<HashMap<String, Object>>) getDao().executeQuery(
				qrySql.toString(), new MapListProcessor());
		Map<String, UFBoolean> map = new HashMap<String, UFBoolean>();
		if (listMaps != null && listMaps.size() > 0) {
			for (int i = 0; i < listMaps.size(); i++) {
				HashMap<String, Object> hashMap = listMaps.get(i);
				String pk_org = hashMap.get("pk_wa_data") != null ? hashMap.get("pk_wa_data").toString() : "";
				String value = hashMap.get("isdeclare") != null ? hashMap.get("isdeclare").toString() : "N";
				map.put(pk_org, new UFBoolean(value));
			}
		}
		return map;
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
	public void reloadLocalizationRefs() throws BusinessException {
		LocalizationSysinitUtil.reloadRefs();
	}

	@Override
	public String getIdtypeno(String pk_psndoc, String pk_wa_data, String cyearperiod) throws BusinessException {
		PsndocVO psndoc = (PsndocVO) getDao().retrieveByPK(PsndocVO.class, pk_psndoc);

		PayfileVO payfileVo = (PayfileVO) getDao().retrieveByPK(PayfileVO.class, pk_wa_data);

		String paydate = payfileVo == null ? null : payfileVo.getCpaydate()
				.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).toString();

		if (StringUtils.isEmpty(paydate)) {
			paydate = (String) getDao().executeQuery(
					"select paydate from hi_psndoc_ptcost where pk_psndoc_sub = '" + pk_wa_data + "'",
					new ColumnProcessor());
		}

		if (StringUtils.isEmpty(paydate)) {
			throw new BusinessException("�@ȡ�T�� [" + psndoc.getCode() + "] �Ľo�������e�`��");
		}
		// ��ѯ��������code

		// ssx modified on 2020-07-30
		// �޸�����Աȡ��Ա�������Ҵ����߼�����
		String country = "TW";
		if (null != psndoc.getCountry()) {
			CountryZoneVO countryVO = (CountryZoneVO) this.dao.retrieveByPK(CountryZoneVO.class, psndoc.getCountry());
			country = countryVO.getCode();
		}
		// end

		// ����Ϊ�ջ��߹���Ϊtw��֤�ű�Ϊ0
		// ����Ϊ��½ δ��183��Ϊ3����183��Ϊ5
		// // ����Ϊ��̨��Ǵ�½��δ��183��Ϊ3����183��Ϊ7
		// String twhr08 = getSysinitValue(psndoc.getPk_org(), "TWHR08");
		// String twhr09 = getSysinitValue(psndoc.getPk_org(), "TWHR09");
		UFDouble expireNum = getBaseDocUFDoubleValue(psndoc.getPk_org(), "TWSP0013");
		String cyear = cyearperiod.substring(0, 4);
		// String cperiod = cyearperiod.substring(5, 6);
		if ("TW".equals(country)) {
			return "0";
		} else if ("CN".equals(country)) {
			if (isExpire(expireNum, psndoc, cyear, new UFLiteralDate(paydate))) {
				return "5";
			} else {
				return "3";
			}
		} else {
			if (!isExpire(expireNum, psndoc, cyear, new UFLiteralDate(paydate))) {
				return "7";
			} else {
				return "3";
			}
		}
	}

}
