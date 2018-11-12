package nc.impl.wa.func;

import java.text.MessageFormat;
import java.util.Calendar;

import nc.bs.dao.BaseDAO;
import nc.vo.hr.func.FunctionVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;
import nc.vo.wa.pub.WaLoginContext;

public class NhiTotalData extends AbstractPreExcutorFormulaParse {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 4124008885212126350L;

	private UFDate getFirstDayOfMonth(String year, String month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(year), Integer.valueOf(month) - 1, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return new UFDate(calendar.getTime()).asBegin();
	}

	private UFDate getLastDayOfMonth(String year, String month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(year), Integer.valueOf(month) - 1, 1);
		int lastday = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, lastday);
		return new UFDate(calendar.getTime()).asEnd();
	}

	@Override
	public void excute(Object formula, WaLoginContext context)
			throws BusinessException {
		FunctionVO funcVo = getFunctionVO();
		MessageFormat format = new MessageFormat(funcVo.getArguments());
		try {
			Object[] parts = format.parse(formula.toString());
			String fieldname = parts[0].toString();
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("update wa_cacu_data "); // 1
			sqlBuffer.append("   set cacu_value  = 0, "); // 2
			sqlBuffer.append("		   pk_psndoc = wa_data.pk_psndoc, "); // ssx-added
			sqlBuffer.append("from wa_data ");
			sqlBuffer.append(" where wa_cacu_data.pk_wa_class = '"
					+ context.getPk_wa_class() + "' ");
			sqlBuffer.append("   and wa_cacu_data.creator = '"
					+ context.getPk_loginUser() + "' ");
			sqlBuffer
					.append("   and wa_cacu_data.pk_wa_data = wa_data.pk_wa_data ");

			getBaseDao().executeUpdate(sqlBuffer.toString());

			String strSQL = "isnull((select top 1 def."
					+ fieldname
					+ " from "
					+ PsndocDefTableUtil.getPsnNHISumTablename()
					+ "  def "
					+ " where def.dr = 0 and def.begindate<='"
					+ this.getLastDayOfMonth(context.getWaYear(),
							context.getWaPeriod())
					+ "' and isnull(def.enddate, '9999-12-31')>='"
					+ this.getFirstDayOfMonth(context.getWaYear(),
							context.getWaPeriod())
					+ "' and def.pk_psndoc = wa_data.pk_psndoc), 0)";

			strSQL = "update wa_cacu_data set cacu_value = isnull((" + strSQL
					+ "), 0) and (wa_cacu_data.pk_wa_class = '"
					+ context.getPk_wa_class()
					+ "') and (wa_cacu_data.creator = '"
					+ context.getPk_loginUser() + "')";
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}

	private BaseDAO baseDao;

	public BaseDAO getBaseDao() {
		if (this.baseDao == null) {
			this.baseDao = new BaseDAO();
		}
		return this.baseDao;
	}
}
