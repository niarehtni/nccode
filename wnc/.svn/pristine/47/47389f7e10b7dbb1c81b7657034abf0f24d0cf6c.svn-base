package nc.impl.wa.paydata.precacu;

import java.util.ArrayList;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.impl.wa.func.AbstractPreExcutorFormulaParse;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.ta.leaveextrarest.ILeaveExtraRestService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.OTLeaveBalanceVO;
import nc.vo.wa.pub.WaLoginContext;

public class YearRestHoursFormulaPreExecutor extends AbstractPreExcutorFormulaParse {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = -8561642632930708398L;

	@SuppressWarnings("unchecked")
	@Override
	public void excute(Object formula, WaLoginContext context) throws BusinessException {
		// 先清空數據
		String sql = "update wa_cacu_data set cacu_value = 0 where  " + "pk_wa_class = '" + context.getPk_wa_class()
				+ "' and creator = '" + context.getPk_loginUser() + "'";
		getBaseDao().executeUpdate(sql);

		// 取本次計算的人員列表
		sql = "select DISTINCT pk_psndoc from wa_cacu_data where pk_wa_class= '" + context.getPk_wa_class()
				+ "' and creator = '" + context.getPk_loginUser() + "'";
		ArrayList<String> pk_psndocs = (ArrayList<String>) getBaseDao().executeQuery(sql, new ColumnListProcessor());

		// 根據薪資期間取考勤期間起迄日期
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();
		String pk_waclass = context.getPk_wa_class();
		sql = "select begindate, enddate from tbm_period "
				+ " inner join wa_period on wa_period.caccyear = tbm_period.accyear and wa_period.caccperiod = tbm_period.accmonth "
				+ " inner join wa_waclass on wa_waclass.pk_periodscheme = wa_period.pk_periodscheme "
				+ " where wa_waclass.pk_wa_class = '" + pk_waclass + "' and wa_period.cyear = '" + cyear
				+ "' and wa_period.cperiod = '" + cperiod + "'";
		ArrayList dates = (ArrayList) getBaseDao().executeQuery(sql, new ArrayListProcessor());
		Object[] periodDates = (Object[]) dates.get(0);
		// 年度補休休假類型
		PersistenceManager sessionManager = null;
		try {
			sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			String initexleavetype = SysInitQuery.getParaString(context.getPk_org(), "TWHRT10");

			// 根據人員、起迄日期、休假類型
			ILeaveExtraRestService service = NCLocator.getInstance().lookup(ILeaveExtraRestService.class);
			OTLeaveBalanceVO[] results = service.getLeaveExtHoursByType(context.getPk_org(),
					pk_psndocs.toArray(new String[0]), new UFLiteralDate(String.valueOf(periodDates[0])),
					new UFLiteralDate(String.valueOf(periodDates[1])), initexleavetype, true);

			if (results != null && results.length > 0) {
				for (OTLeaveBalanceVO vo : results) {
					sql = "update wa_cacu_data set cacu_value = " + vo.getRemainhours().toString() + " where  "
							+ "pk_wa_class = '" + context.getPk_wa_class() + "' and creator = '"
							+ context.getPk_loginUser() + "'  and pk_psndoc = '" + vo.getPk_psndoc() + "'";
					session.addBatch(sql);
				}

				session.executeBatch();
			}
		} catch (DbException e) {
			throw new DAOException(e.getMessage());
		} finally {
			if (sessionManager != null) {
				sessionManager.release();
			}
		}

	}

	private BaseDAO baseDao;

	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}
}
