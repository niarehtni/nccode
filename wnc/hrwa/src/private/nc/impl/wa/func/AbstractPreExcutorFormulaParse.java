package nc.impl.wa.func;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.dataitem.pub.DataVOUtils;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.paydata.IFormula;

/**
 * ��wa_cacu_data��ǰ���㣬Ϊ����������׼��
 * 
 * @author: zhangg
 * @date: 2010-5-14 ����08:59:21
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
@SuppressWarnings("serial")
public abstract class AbstractPreExcutorFormulaParse extends AbstractWAFormulaParse implements IFormula {

	/**
	 * @author zhangg on 2010-5-12
	 * @see nc.impl.wa.func.AbstractWAFormulaParse#getReplaceStr(java.lang.String)
	 */
	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {

		excute(formula, getContext());

		FunctionReplaceVO fvo = new FunctionReplaceVO();

		boolean digits = true;
		Object object = getContext().getInitData();
		if (object != null && object instanceof WaClassItemVO) {
			WaClassItemVO itemVO = (WaClassItemVO) object;
			digits = DataVOUtils.isDigitsAttribute(itemVO.getItemkey());
		}
		fvo.setAliTableName("wa_cacu_data");
		if (digits) {
			fvo.setReplaceStr("coalesce(wa_cacu_data.cacu_value, 0)");
		} else {
			fvo.setReplaceStr(" wa_cacu_data.char_value ");
		}
		return fvo;
	}

	protected void writeToWaOTTempData(String pk_wa_class, String creator, Map<String, UFDouble[]> ovtFeeResult,
			boolean isLeave) {
		// ��������
		try {
			for (String pk_psndoc : ovtFeeResult.keySet()) {
				if (!isLeave) {
					// �D�{��
					UFDouble amountTaxFree = (ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 1) ? UFDouble.ZERO_DBL
							: ovtFeeResult.get(pk_psndoc)[0];
					UFDouble amountTaxable = (ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 3) ? UFDouble.ZERO_DBL
							: ovtFeeResult.get(pk_psndoc)[2];
					getBaseDao().executeUpdate(
							"delete from wa_cacu_overtimefee where pk_wa_class='" + pk_wa_class + "' and creator='"
									+ creator + "' and pk_psndoc='" + pk_psndoc + "' and intcomp=0;");
					getBaseDao().executeUpdate(
							"insert into wa_cacu_overtimefee (pk_wa_class, creator, pk_psndoc, intcomp, amounttaxfree, amounttaxable) values ('"
									+ pk_wa_class + "','" + creator + "','" + pk_psndoc + "'," + String.valueOf(0)
									+ "," + amountTaxFree.toString() + "," + amountTaxable.toString() + ");");

					// ���D�{��
					amountTaxFree = (ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 2) ? UFDouble.ZERO_DBL
							: ovtFeeResult.get(pk_psndoc)[1];
					amountTaxable = (ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 4) ? UFDouble.ZERO_DBL
							: ovtFeeResult.get(pk_psndoc)[3];
					getBaseDao().executeUpdate(
							"delete from wa_cacu_overtimefee where pk_wa_class='" + pk_wa_class + "' and creator='"
									+ creator + "' and pk_psndoc='" + pk_psndoc + "' and intcomp=1;");
					getBaseDao().executeUpdate(
							"insert into wa_cacu_overtimefee (pk_wa_class, creator, pk_psndoc, intcomp, amounttaxfree, amounttaxable) values ('"
									+ pk_wa_class + "','" + creator + "','" + pk_psndoc + "'," + String.valueOf(1)
									+ "," + amountTaxFree.toString() + "," + amountTaxable.toString() + ");");

					// ��Ӌ
					amountTaxFree = ((ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 1) ? UFDouble.ZERO_DBL
							: ovtFeeResult.get(pk_psndoc)[0]).add((ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult
							.get(pk_psndoc).length < 2) ? UFDouble.ZERO_DBL : ovtFeeResult.get(pk_psndoc)[1]);
					amountTaxable = ((ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 3) ? UFDouble.ZERO_DBL
							: ovtFeeResult.get(pk_psndoc)[2]).add((ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult
							.get(pk_psndoc).length < 4) ? UFDouble.ZERO_DBL : ovtFeeResult.get(pk_psndoc)[3]);
					getBaseDao().executeUpdate(
							"delete from wa_cacu_overtimefee where pk_wa_class='" + pk_wa_class + "' and creator='"
									+ creator + "' and pk_psndoc='" + pk_psndoc + "' and intcomp=2;");
					getBaseDao().executeUpdate(
							"insert into wa_cacu_overtimefee (pk_wa_class, creator, pk_psndoc, intcomp, amounttaxfree, amounttaxable) values ('"
									+ pk_wa_class + "','" + creator + "','" + pk_psndoc + "'," + String.valueOf(2)
									+ "," + amountTaxFree.toString() + "," + amountTaxable.toString() + ");");
				} else {
					// �D�{��
					UFDouble amountTaxFree = (ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 1) ? UFDouble.ZERO_DBL
							: ovtFeeResult.get(pk_psndoc)[0];
					UFDouble amountTaxable = (ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 3) ? UFDouble.ZERO_DBL
							: ovtFeeResult.get(pk_psndoc)[2];
					getBaseDao().executeUpdate(
							"delete from wa_cacu_overtimefee where pk_wa_class='" + pk_wa_class + "' and creator='"
									+ creator + "' and pk_psndoc='" + pk_psndoc + "' and intcomp=-2;");
					getBaseDao().executeUpdate(
							"insert into wa_cacu_overtimefee (pk_wa_class, creator, pk_psndoc, intcomp, amounttaxfree, amounttaxable) values ('"
									+ pk_wa_class + "','" + creator + "','" + pk_psndoc + "'," + String.valueOf(-2)
									+ "," + amountTaxFree.toString() + "," + amountTaxable.toString() + ");");

					// ���D�{��
					amountTaxFree = (ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 2) ? UFDouble.ZERO_DBL
							: ovtFeeResult.get(pk_psndoc)[1];
					amountTaxable = (ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 4) ? UFDouble.ZERO_DBL
							: ovtFeeResult.get(pk_psndoc)[3];
					getBaseDao().executeUpdate(
							"delete from wa_cacu_overtimefee where pk_wa_class='" + pk_wa_class + "' and creator='"
									+ creator + "' and pk_psndoc='" + pk_psndoc + "' and intcomp=-3;");
					getBaseDao().executeUpdate(
							"insert into wa_cacu_overtimefee (pk_wa_class, creator, pk_psndoc, intcomp, amounttaxfree, amounttaxable) values ('"
									+ pk_wa_class + "','" + creator + "','" + pk_psndoc + "'," + String.valueOf(-3)
									+ "," + amountTaxFree.toString() + "," + amountTaxable.toString() + ");");

					// ��Ӌ
					amountTaxFree = ((ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 1) ? UFDouble.ZERO_DBL
							: ovtFeeResult.get(pk_psndoc)[0]).add((ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult
							.get(pk_psndoc).length < 2) ? UFDouble.ZERO_DBL : ovtFeeResult.get(pk_psndoc)[1]);
					amountTaxable = ((ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult.get(pk_psndoc).length < 3) ? UFDouble.ZERO_DBL
							: ovtFeeResult.get(pk_psndoc)[2]).add((ovtFeeResult.get(pk_psndoc) == null || ovtFeeResult
							.get(pk_psndoc).length < 4) ? UFDouble.ZERO_DBL : ovtFeeResult.get(pk_psndoc)[3]);
					getBaseDao().executeUpdate(
							"delete from wa_cacu_overtimefee where pk_wa_class='" + pk_wa_class + "' and creator='"
									+ creator + "' and pk_psndoc='" + pk_psndoc + "' and intcomp=-1;");
					getBaseDao().executeUpdate(
							"insert into wa_cacu_overtimefee (pk_wa_class, creator, pk_psndoc, intcomp, amounttaxfree, amounttaxable) values ('"
									+ pk_wa_class + "','" + creator + "','" + pk_psndoc + "'," + String.valueOf(-1)
									+ "," + amountTaxFree.toString() + "," + amountTaxable.toString() + ");");
				}
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	protected UFLiteralDate[] getBaseYear(UFLiteralDate baseDate, UFLiteralDate checkDate) {
		int checkYear = baseDate.getYear() - 2;
		UFLiteralDate[] scopeDates = new UFLiteralDate[2];
		do {
			checkYear++;
			scopeDates[0] = getDateInYear(baseDate, checkYear);
			scopeDates[1] = getDateInYear(baseDate, checkYear + 1).getDateBefore(1);
		} while (checkDate.before(scopeDates[0]) || checkDate.after(scopeDates[1]));
		return scopeDates;
	}

	protected UFLiteralDate getDateInYear(UFLiteralDate baseDate, int checkYear) {
		UFLiteralDate yearBeginDate;
		if (baseDate.toString().contains("-02-29")) {
			if (UFLiteralDate.isLeapYear(checkYear)) {
				yearBeginDate = new UFLiteralDate(String.valueOf(checkYear) + "-02-29");
			} else {
				yearBeginDate = new UFLiteralDate(String.valueOf(checkYear) + "-03-01");
			}
		} else {
			yearBeginDate = new UFLiteralDate(String.valueOf(checkYear) + baseDate.toString().substring(4));
		}
		return yearBeginDate;
	}

	private BaseDAO baseDao;

	protected BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

	protected String getCheckYear(UFLiteralDate endDate, Map<String, Object> psndates, UFLiteralDate[] scopeDates,
			LeaveTypeCopyVO leaveTypeCopy) {
		if (LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE == leaveTypeCopy.getLeavesetperiod()) {
			// �����Y������
			scopeDates = getBaseYear(new UFLiteralDate((String) psndates.get("workagestartdate")), endDate);
		} else if (LeaveTypeCopyVO.LEAVESETPERIOD_YEAR == leaveTypeCopy.getLeavesetperiod()) {
			// ����Ȼ��
			scopeDates[0] = new UFLiteralDate(String.valueOf(endDate.getYear()) + "-01-01");
			scopeDates[1] = new UFLiteralDate(String.valueOf(endDate.getYear()) + "-12-31");
		} else if (LeaveTypeCopyVO.LEAVESETPERIOD_DATE == leaveTypeCopy.getLeavesetperiod()) {
			// �������ڣ��M���P�S.�M�����ڣ�
			scopeDates = getBaseYear((UFLiteralDate) psndates.get("begindate"), endDate);
		}

		String checkYear = String.valueOf(scopeDates[0].getYear());
		return checkYear;
	}

	@SuppressWarnings("unchecked")
	protected Collection<PsnJobVO> getPsnJobsWithNoLeaveByPsnSQL(String psnInSQL, Collection<PeriodVO> periodVos)
			throws DAOException {
		return this.getBaseDao().retrieveByClause(
				PsnJobVO.class,
				"pk_psndoc in (" + psnInSQL
						+ ") and trnsevent <> 4 and trnstype <> '1001X110000000003O5G' and begindate <= '"
						+ periodVos.toArray(new PeriodVO[0])[0].getEnddate()
						+ "' and isnull(enddate, '9999-12-31') >= '"
						+ periodVos.toArray(new PeriodVO[0])[0].getBegindate() + "'");
	}

	@SuppressWarnings("unchecked")
	protected List<Map<String, Object>> getNewestPsnOrgByPsnSQL(String psnInSQL) throws DAOException {
		return (List<Map<String, Object>>) this
				.getBaseDao()
				.executeQuery(
						"select pk_psndoc, begindate, joinsysdate, workagestartdate from hi_psnorg org where begindate = (select max(begindate) from hi_psnorg where pk_psndoc = org.pk_psndoc) and pk_psndoc  in ("
								+ psnInSQL + ")", new MapListProcessor());
	}
}
