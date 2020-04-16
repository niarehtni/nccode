package nc.impl.wa.paydata.precacu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.impl.wa.func.AbstractPreExcutorFormulaParse;
import nc.itf.hrwa.IWadaysalaryQueryService;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 
 * @author ward
 * @date 2018年9月21日09:28:27
 * @desc 日薪汇总函数
 * 
 */
public class DaySalaryFormulaPreExcutor extends AbstractPreExcutorFormulaParse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -381307272761044759L;

	@Override
	public void excute(Object formula, WaLoginContext context) throws BusinessException {
		// 先清空數據
		String sql = "update wa_cacu_data set cacu_value = 0 where  " + "pk_wa_class = '" + context.getPk_wa_class()
				+ "' and creator = '" + context.getPk_loginUser() + "'";
		getBaseDao().executeUpdate(sql);

		sql = "select DISTINCT pk_psndoc from wa_cacu_data where pk_wa_class= '" + context.getPk_wa_class()
				+ "' and creator = '" + context.getPk_loginUser() + "'";

		@SuppressWarnings("unchecked")
		ArrayList<String> pk_psndocs = (ArrayList<String>) getBaseDao().executeQuery(sql, new ColumnListProcessor());
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();
		String[] arguments = getArguments(formula.toString());
		String pk_wa_item = arguments[0].replaceAll("\'", "");
		String[] psndocs = (String[]) pk_psndocs.toArray(new String[pk_psndocs.size()]);
		String pk_group_item = arguments[1].replaceAll("\'", "");;
		String pk_org = context.getPk_org();
		Map<String, UFDouble> psnSalaryMap = lookup(IWadaysalaryQueryService.class)
				.getTotalDaySalaryMapByWaItemWithoutRecalculate(
						pk_org,psndocs, context.getPk_wa_class(), cyear, cperiod,
						pk_wa_item,pk_group_item);
		if (psnSalaryMap == null || psnSalaryMap.size() == 0) {
			return;
		}

		PersistenceManager sessionManager = null;
		try {
			sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			Iterator<Map.Entry<String, UFDouble>> iterator = psnSalaryMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, UFDouble> entry = iterator.next();
				sql = "update wa_cacu_data set cacu_value = " + entry.getValue() + " where  " + "pk_wa_class = '"
						+ context.getPk_wa_class() + "' and creator = '" + context.getPk_loginUser()
						+ "'  and pk_psndoc = '" + entry.getKey() + "'";
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

	private BaseDAO baseDao;

	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}
}
