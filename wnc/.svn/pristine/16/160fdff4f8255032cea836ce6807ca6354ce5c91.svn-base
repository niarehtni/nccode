package nc.impl.wa.func;

import nc.bs.dao.BaseDAO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 取台灣勞健保參數設置值
 * 
 * @author ssx
 * @version 6.33
 * @since 2017-07-26
 */
public class NHISettingValue extends AbstractPreExcutorFormulaParse {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 2789369685388368500L;
	private BaseDAO baseDao;

	@Override
	public void excute(Object formula, WaLoginContext context)
			throws BusinessException {
		String[] arguments = getArguments(formula.toString());
		String settion_code = arguments[0];

		String sql = "update wa_cacu_data set cacu_value = (select case when doctype=1 then numbervalue when doctype=2 then numbervalue/100 else 0 end from twhr_basedoc where pk_org='"
				+ context.getPk_org()
				+ "' and code='"
				+ settion_code
				+ "')"
				+ " where  "
				+ "pk_wa_class = '"
				+ context.getPk_wa_class()
				+ "' and creator ='" + context.getPk_loginUser() + "'";

		getBaseDao().executeUpdate(sql);
	}

	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}
}
