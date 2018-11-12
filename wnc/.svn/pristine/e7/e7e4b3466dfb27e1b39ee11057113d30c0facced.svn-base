package nc.impl.wa.func;

import nc.bs.dao.BaseDAO;
import nc.bs.logging.Logger;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.hr.func.FunctionVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.formula.IFormulaAli;
import nc.vo.wa.paydata.IFormula;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.YearPeriodSeperatorVO;

/**
 * 薪资统计数:相对时间段
 * @author: zhangg
 * @date: 2010-5-11 上午09:28:53
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("serial")
public class PayDataAB extends AbstractWAFormulaParse implements IFormulaAli {


	public final static int DB2 = 0;
	public final static int ORACLE = 1;
	public final static int SQLSERVER = 2;
	public final static int SYBASE = 3;
	public final static int UNKOWNDATABASE = -1;
	private String pk_wa_class;

	public String getPk_wa_class() {
		return pk_wa_class;
	}

	public void setPk_wa_class(String pk_wa_class) {
		this.pk_wa_class = pk_wa_class;
	}
	/**
	 * （参数：薪资方案,薪资项目,统计方式,起始期间,终止期间）
	 * 
	 * @author zhangg on 2010-5-11
	 * @see nc.impl.wa.func.AbstractWAFormulaParse#getReplaceStr(java.lang.String)
	 */
	@Override
	public FunctionReplaceVO getReplaceStr(String formula)
			throws BusinessException {
		String[] arguments = getArguments(formula);

		FunctionReplaceVO fvo = new FunctionReplaceVO();

		String classpk = arguments[0];
		classpk = trans2OrgPk(classpk);
		setPk_wa_class(arguments[0]);
		String itemid = arguments[1];
		String type = arguments[2];
		String s_period = arguments[3];
		s_period = getAbsPeriod(new Integer(s_period)).getYearperiod();
		String e_period = arguments[4];
		e_period = getAbsPeriod(new Integer(e_period)).getYearperiod();

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

			tableName = isc.createTempTable(tableName, columns, index);

			// 组SQL语句
			StringBuffer sqlBuffer0 = new StringBuffer();
			sqlBuffer0
			.append("     insert into  "
					+ tableName
					+ "  (pk_attribute,group_value ) select  data_source.pk_psndoc as  pk_attribute ,"
					+ type + "(data_source." + itemid
					+ " ) as group_value  ");
			sqlBuffer0.append("  from wa_data data_source ");
			sqlBuffer0.append(" where data_source.pk_wa_class = '"
					+ getPk_wa_class() + "' ");
			sqlBuffer0
			.append("   and (data_source.cyearperiod) >= '"
					+ s_period + "' ");
			sqlBuffer0
			.append("   and (data_source.cyearperiod) <= '"
					+ e_period + "' ");
			sqlBuffer0
			.append("  and  data_source.stopflag = 'N' group by data_source.pk_psndoc ");

			new BaseDAO().executeUpdate(sqlBuffer0.toString());

			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append(" isnull((select group_value ");
			sqlBuffer.append("   from " + tableName);
			sqlBuffer.append("  where  " + tableName
					+ ".pk_attribute = wa_data.pk_psndoc),  ");
			sqlBuffer.append("  0)  ");
			IFormula excutor = new PayDataABPreExecutor();
			excutor.excute(sqlBuffer.toString(), getContext());

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			WaClassItemVO vo = (WaClassItemVO) context.getInitData();
			throw new BusinessException(ResHelper.getString("60130paydata",
					"060130paydata0444")/* @res "薪资项目:" */
					+ vo.getMultilangName()
					+ ResHelper.getString("60130paydata", "060130paydata0445")/*
					 * @res
					 * "公式设置错误。 请检查。"
					 */);

		}

		fvo.setAliTableName("wa_data");
		fvo.setReplaceStr("");
		return fvo;

	}

	/**
	 * @author zhangg on 2010-6-10
	 * @see nc.vo.wa.formula.IFormulaAli#getAliItemKeys(nc.vo.wa.classitem.WaClassItemVO, nc.vo.wa.pub.WaLoginContext, nc.vo.hr.func.FunctionVO)
	 */
	@Override
	public String[] getAliItemKeys(WaClassItemVO itemVO, WaLoginContext context, FunctionVO functionVO) throws BusinessException {
		if(itemVO == null || context == null || functionVO == null || itemVO.getVformula() == null){
			return null;
		}

		setFunctionVO(functionVO);
		setContext(context);

		String[] arguments = getArguments(itemVO.getVformula());

		String pk_wa_class = arguments[0];
		String itemid = arguments[1];
		//String type = arguments[2];
		//String s_period = arguments[3];
		String e_period = arguments[4];

		//看薪资类别是否相同
		if(!pk_wa_class.equalsIgnoreCase(context.getPk_wa_class())){
			return null;
		}
		//看薪资期间是否包括了本期间
		if(new Integer(e_period).intValue() != 0){
			return null;
		}

		return new String[]{itemid};
	}


	@Override
	protected int getDataBaseType() {
		return getDaoManager().getBaseDao().getDBType();
	}

	@Override
	public YearPeriodSeperatorVO getAbsPeriod(int i) throws BusinessException {
		if (i == 0) {
			return getSamePeriod();// 返回当前期间
		}
		YearPeriodSeperatorVO yearPeriodSeperatorVO = new YearPeriodSeperatorVO(
				"190001");

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select (cyear || cperiod) yearperiod "); // 1
		sqlB.append("  from wa_periodstate ");
		sqlB.append(" inner join wa_period on (wa_periodstate.pk_wa_period = ");
		sqlB.append("                         wa_period.pk_wa_period) ");
		sqlB.append(" where pk_wa_class = '" + getPk_wa_class() + "' ");
		sqlB.append("   and (cyear || cperiod) <= '" + getContext().getWaYear()
				+ getContext().getWaPeriod() + "' ");
		sqlB.append(" order by yearperiod desc");

		YearPeriodSeperatorVO[] periodSeperatorVOs = getDaoManager()
				.executeQueryVOs(sqlB.toString(), YearPeriodSeperatorVO.class);
		if (periodSeperatorVOs != null) {
			for (int j = 0; j < periodSeperatorVOs.length; j++) {
				if (j == i) {// 找到啦,则附值、退出
					return periodSeperatorVOs[j];
				}
			}
		}
		return yearPeriodSeperatorVO;
	}
}
