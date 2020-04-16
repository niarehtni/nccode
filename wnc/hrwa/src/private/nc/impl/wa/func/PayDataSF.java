package nc.impl.wa.func;

import nc.bs.dao.BaseDAO;
import nc.bs.logging.Logger;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.impl.wa.paydata.precacu.WaOrtherFormulaPreExcutor;
import nc.vo.hr.func.FunctionVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.formula.IFormulaAli;
import nc.vo.wa.paydata.IFormula;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.YearPeriodSeperatorVO;

/**
 * 
 * @author: zhangg
 * @date: 2010-5-11 上午10:13:52
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class PayDataSF extends AbstractPreExcutorFormulaParse implements IFormulaAli {
	private static final long serialVersionUID = 1L;

	public final static int DB2 = 0;
	public final static int ORACLE = 1;
	public final static int SQLSERVER = 2;
	public final static int SYBASE = 3;
	public final static int UNKOWNDATABASE = -1;

	private BaseDAO baseDao;

	/**
	 * @author zhangg on 2010-6-3
	 * @see nc.vo.wa.paydata.IFormula#excute(java.lang.Object,
	 *      nc.vo.wa.pub.WaLoginContext)
	 */
	@Override
	public void excute(Object formula, WaLoginContext context) throws BusinessException {

		String[] arguments = getArguments(formula.toString());
		String pk_wa_class = arguments[0];
		String itemid = arguments[1];
		String period = arguments[2];
		String type = arguments[3];
		String refld = arguments[4];
		pk_wa_class = trans2OrgPk(pk_wa_class);

		YearPeriodSeperatorVO yearPeriodSeperatorVO = trans2YearPeriodSeperatorVO(period);

		String cyear = yearPeriodSeperatorVO.getYear();
		String cperiod = yearPeriodSeperatorVO.getPeriod();

		String tableName = HRWACommonConstants.WA_TEMP_DATAGROUP;
		// 创建临时表
		InSQLCreator isc = new InSQLCreator();

		try {

			String columns = "";
			if (getDataBaseType() == ORACLE) {
				columns = "   pk_attribute varchar(20), group_value number(31,8) default 0  ";
			} else {
				columns = "   pk_attribute varchar(20), group_value decimal(31,8) default 0  ";
			}

			String index = "pk_attribute";

			getDaoManager().getBaseDao().getDBType();
			tableName = isc.createTempTable(tableName, columns, index);

			if (refld.equalsIgnoreCase("pk_dept")) {

				StringBuffer sqlBuffer0 = new StringBuffer();
				sqlBuffer0.append("     insert into  " + tableName
						+ "  (pk_attribute,group_value )  select  hi_psnjob.pk_dept  as pk_attribute  ," + type + "("
						+ itemid + " ) as group_value ");
				sqlBuffer0.append("          from wa_data, hi_psnjob ");
				sqlBuffer0.append("         where hi_psnjob.pk_psnjob = wa_data.pk_psnjob ");
				sqlBuffer0.append("           and pk_wa_class = '" + pk_wa_class + "' ");
				sqlBuffer0.append("           and cyear ='" + cyear + "' and  cperiod = '" + cperiod
						+ "' and  wa_data.stopflag = 'N' ");
				sqlBuffer0.append("         group by hi_psnjob.pk_dept ");
				getBaseDao().executeUpdate(sqlBuffer0.toString());

				StringBuffer sqlBuffer = new StringBuffer();
				sqlBuffer.append("select group_value "); // 1
				sqlBuffer.append("  from wa_data, ");
				sqlBuffer.append("       hi_psnjob," + tableName + " ");
				sqlBuffer.append(" where hi_psnjob.pk_psnjob = wa_data.pk_psnjob ");
				sqlBuffer.append("   and " + tableName + ".pk_attribute = hi_psnjob.pk_dept ");
				sqlBuffer.append("   and wa_data.pk_wa_data = wa_cacu_data.pk_wa_data ");

				IFormula excutor = new WaOrtherFormulaPreExcutor();
				excutor.excute(sqlBuffer.toString(), getContext());

			} else if (refld.equalsIgnoreCase("pk_psncl")) {

				StringBuffer sqlBuffer0 = new StringBuffer();
				sqlBuffer0.append("     insert into  " + tableName
						+ "  (pk_attribute,group_value )  select hi_psnjob.pk_psncl  as pk_attribute," + type + "("
						+ itemid + " ) as group_value ");
				sqlBuffer0.append("          from wa_data, hi_psnjob ");
				sqlBuffer0.append("         where hi_psnjob.pk_psnjob = wa_data.pk_psnjob ");
				sqlBuffer0.append("           and pk_wa_class = '" + pk_wa_class + "' ");
				sqlBuffer0.append("           and cyear ='" + cyear + "' and  cperiod = '" + cperiod
						+ "' and  wa_data.stopflag = 'N' ");
				sqlBuffer0.append("         group by hi_psnjob.pk_psncl ");

				getBaseDao().executeUpdate(sqlBuffer0.toString());

				StringBuffer sqlBuffer = new StringBuffer();
				sqlBuffer.append("select group_value "); // 1
				sqlBuffer.append("  from wa_data, ");
				sqlBuffer.append("       hi_psnjob," + tableName + " ");
				sqlBuffer.append(" where hi_psnjob.pk_psnjob = wa_data.pk_psnjob ");
				sqlBuffer.append("   and " + tableName + ".pk_attribute = hi_psnjob.pk_psncl ");
				sqlBuffer.append("   and wa_data.pk_wa_data = wa_cacu_data.pk_wa_data ");

				IFormula excutor = new WaOrtherFormulaPreExcutor();
				excutor.excute(sqlBuffer.toString(), getContext());

			} else {
				// 组SQL语句

				StringBuffer sqlBuffer0 = new StringBuffer();
				sqlBuffer0.append("     insert into  " + tableName + "  (pk_attribute,group_value )  select " + refld
						+ "  as pk_attribute  ," + type + "(" + itemid + " ) as group_value ");
				sqlBuffer0.append("          from wa_data ");
				sqlBuffer0.append("         where  pk_wa_class = '" + pk_wa_class + "' ");
				sqlBuffer0.append("           and cyear ='" + cyear + "' and  cperiod = '" + cperiod
						+ "' and  wa_data.stopflag = 'N' ");
				sqlBuffer0.append("         group by  " + refld);
				getBaseDao().executeUpdate(sqlBuffer0.toString());

				StringBuffer sqlBuffer = new StringBuffer();
				sqlBuffer.append("select group_value "); // 1
				sqlBuffer.append("  from wa_data, " + tableName + " ");

				sqlBuffer.append(" where " + tableName + ".pk_attribute = wa_data. " + refld + " ");
				sqlBuffer.append("   and wa_data.pk_wa_data = wa_cacu_data.pk_wa_data ");

				IFormula excutor = new WaOrtherFormulaPreExcutor();
				excutor.excute(sqlBuffer.toString(), getContext());
			}

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			WaClassItemVO vo = (WaClassItemVO) context.getInitData();
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0444")/*
																								 * @
																								 * res
																								 * "薪资项目:"
																								 */
					+ vo.getMultilangName() + ResHelper.getString("60130paydata", "060130paydata0445")/*
																									 * @
																									 * res
																									 * "公式设置错误。 请检查。"
																									 */);

		}
	}

	/**
	 * @author zhangg on 2010-6-10
	 * @see nc.vo.wa.formula.IFormulaAli#getAliItemKeys(nc.vo.wa.classitem.WaClassItemVO,
	 *      nc.vo.wa.pub.WaLoginContext, nc.vo.hr.func.FunctionVO)
	 */
	@Override
	public String[] getAliItemKeys(WaClassItemVO itemVO, WaLoginContext context, FunctionVO functionVO)
			throws BusinessException {

		if (itemVO == null || context == null || functionVO == null || itemVO.getVformula() == null) {
			return null;
		}

		setFunctionVO(functionVO);
		setContext(context);

		String[] arguments = getArguments(itemVO.getVformula());

		String pk_wa_class = arguments[0];
		String itemid = arguments[1];
		String period = arguments[2];

		// 看薪资类别是否相同
		if (!pk_wa_class.equalsIgnoreCase(context.getPk_wa_class())) {
			return null;
		}
		// 看薪资期间是本期间
		if (!period.equals(same_period) && !period.equals(context.getWaYear() + context.getWaPeriod())) {
			return null;
		}

		return new String[] { itemid };

	}
}
