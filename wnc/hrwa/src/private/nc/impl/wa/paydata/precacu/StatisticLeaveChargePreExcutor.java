package nc.impl.wa.paydata.precacu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.itf.hrwa.IWadaysalaryService;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.pub.WaLoginContext;

public class StatisticLeaveChargePreExcutor extends AbstractFormulaExecutor {

	@Override
	public void excute(Object object, WaLoginContext context)
			throws BusinessException {
		// ÏÈÇå¿Õ”µ“þ
		String sql = "update wa_cacu_data set cacu_value = 0 where  "
				+ "pk_wa_class = '" + context.getPk_wa_class()
				+ "' and creator = '" + context.getPk_loginUser() + "'";
		getBaseDao().executeUpdate(sql);

		sql = "select DISTINCT pk_psndoc from wa_cacu_data where pk_wa_class= '"
				+ context.getPk_wa_class()
				+ "' and creator = '"
				+ context.getPk_loginUser() + "'";

		@SuppressWarnings("unchecked")
		ArrayList<String> pk_psndocs = (ArrayList<String>) getBaseDao()
				.executeQuery(sql, new ColumnListProcessor());
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();
		String[] psndocs = (String[]) pk_psndocs.toArray(new String[pk_psndocs
				.size()]);
		Map<String, UFDouble> leavechargeMap = lookup(IWadaysalaryService.class)
				.statisticLeavecharge(psndocs, cyear, cperiod);
		if (leavechargeMap == null || leavechargeMap.size() == 0) {
			return;
		}

		PersistenceManager sessionManager = null;
		try {
			sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			Iterator<Map.Entry<String, UFDouble>> iterator = leavechargeMap
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, UFDouble> entry = iterator.next();
				sql = "update wa_cacu_data set cacu_value = "
						+ entry.getValue() + " where  " + "pk_wa_class = '"
						+ context.getPk_wa_class() + "' and creator = '"
						+ context.getPk_loginUser() + "'  and pk_psndoc = '"
						+ entry.getKey() + "'";
				session.addBatch(sql);
			}
			session.executeBatch();
		} catch (DbException e) {
			throw new DAOException(e.getMessage());
		} finally {
			if (sessionManager != null) {
				sessionManager.release();
			}
		}
	}
}
