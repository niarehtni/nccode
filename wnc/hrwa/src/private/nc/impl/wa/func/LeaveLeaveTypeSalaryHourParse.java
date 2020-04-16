package nc.impl.wa.func;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.impl.wa.paydata.PaydataDAO;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.pub.WaLoginContext;

public class LeaveLeaveTypeSalaryHourParse extends AbstractPreExcutorFormulaParse {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 2061110567221193366L;
	private String pk_leaveitem;

	private int INTCOMP = -11; // 1: 离职

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void excute(Object formula, WaLoginContext waLoginContext) throws BusinessException {
		// 根據薪資期間取考勤期間起迄日期
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();
		String pk_wa_class = context.getPk_wa_class();
		String pk_creator = context.getPk_loginUser();

		int rows = (int) this.getBaseDao().executeQuery(
				"select count(*) from wa_cacu_leavehour where pk_wa_class='" + pk_wa_class + "' and intcomp="
						+ String.valueOf(INTCOMP) + " and creator='" + pk_creator + "' and pk_timeitem='"
						+ this.getPk_leaveitem() + "' and cyear='" + cyear + "' and cperiod='" + cperiod + "'",
				new ColumnProcessor());

		if (rows == 0) {
			String sql = "select begindate, enddate from tbm_period "
					+ " inner join wa_period on wa_period.caccyear = tbm_period.accyear and wa_period.caccperiod = tbm_period.accmonth "
					+ " inner join wa_waclass on wa_waclass.pk_periodscheme = wa_period.pk_periodscheme "
					+ " where wa_waclass.pk_wa_class = '" + pk_wa_class + "' and wa_period.cyear = '" + cyear
					+ "' and wa_period.cperiod = '" + cperiod + "'";
			ArrayList dates = (ArrayList) getBaseDao().executeQuery(sql, new ArrayListProcessor());
			Object[] periodDates = (Object[]) dates.get(0);

			periodDates[0] = new UFLiteralDate((String) periodDates[1]).getDateAfter(1).toString();
			periodDates[1] = (new UFLiteralDate((String) periodDates[0]).getDateAfter(new UFLiteralDate(
					(String) periodDates[0]).getDaysMonth() - new UFLiteralDate((String) periodDates[0]).getDay() + 1))
					.toString();

			PersistenceManager sessionManager = null;
			try {
				sessionManager = PersistenceManager.getInstance();
				JdbcSession session = sessionManager.getJdbcSession();

				// 取本次計算的人員列表
				String psnlistsql = PaydataDAO.getLeavePsndocSQL(waLoginContext.getPk_org(),
						waLoginContext.getPk_wa_class(), waLoginContext.getCyear(), waLoginContext.getCperiod(),
						new UFLiteralDate((String) periodDates[0]), new UFLiteralDate((String) periodDates[1]),
						pk_creator);

				sql = PaydataDAO.getLeaveHoursSQLByPeriodCondtions(this.getPk_leaveitem(), periodDates, psnlistsql);
				sql = "select psndoc pk_psndoc, sum(leavehour) leavehour from (" + sql + ") tmp GROUP BY psndoc";

				List<Map<String, Object>> results = (List<Map<String, Object>>) this.getBaseDao().executeQuery(sql,
						new MapListProcessor());
				if (results != null && results.size() > 0) {
					for (Map<String, Object> result : results) {
						if (result != null) {
							sql = "insert into wa_cacu_leavehour (intcomp, pk_timeitem, cyear, cperiod, hours, pk_wa_class, creator, pk_psndoc)"
									+ " values ("
									+ String.valueOf(INTCOMP)
									+ ",'"
									+ this.getPk_leaveitem()
									+ "','"
									+ cyear
									+ "','"
									+ cperiod
									+ "',"
									+ String.valueOf(result.get("leavehour"))
									+ ",'"
									+ context.getPk_wa_class()
									+ "','"
									+ pk_creator
									+ "','"
									+ result.get("pk_psndoc")
									+ "')";
							session.addBatch(sql);
						}
					}
					session.executeBatch();
				}
			} catch (DbException e) {
				throw new BusinessException(e.getMessage());
			} finally {
				if (sessionManager != null) {
					sessionManager.release();
				}
			}
		}
	}

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		String[] params = this.getArguments(formula);
		this.setPk_leaveitem(params[0]);

		excute(formula, getContext());

		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String cyear = getContext().getCyear();
		String cperiod = getContext().getCperiod();
		String pk_waclass = getContext().getPk_wa_class();
		String pk_creator = getContext().getPk_loginUser();

		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr("coalesce((select hours from wa_cacu_leavehour where intcomp = " + INTCOMP
				+ " and pk_timeitem='" + this.getPk_leaveitem() + "' and cyear='" + cyear + "' and cperiod='" + cperiod
				+ "' and pk_wa_class='" + pk_waclass + "' and creator='" + pk_creator
				+ "' and pk_psndoc = wa_cacu_data.pk_psndoc)" + ", 0)");
		return fvo;
	}

	private BaseDAO baseDao;

	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

	public String getPk_leaveitem() {
		return pk_leaveitem;
	}

	public void setPk_leaveitem(String pk_leaveitem) {
		this.pk_leaveitem = pk_leaveitem;
	}

}
