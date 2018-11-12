package nc.impl.wa.repay;

import java.util.List;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.impl.wa.paydata.AbstractCaculateService;
import nc.impl.wa.paydata.IPreTranslate;
import nc.impl.wa.paydata.WaPreTranslate;
import nc.impl.wa.paydata.precacu.TaxFormulaPreExcutor;
import nc.impl.wa.paydata.tax.TaxFormulaUtil;
import nc.impl.wa.paydata.tax.TaxFormulaVO;
import nc.itf.hr.wa.IRepayQueryService;
import nc.jdbc.framework.SQLParameter;
import nc.vo.dataitem.pub.DataVOUtils;
import nc.vo.hr.caculate.CaculateTypeVO;
import nc.vo.hr.func.FunctionVO;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.formula.HrWaXmlReader;
import nc.vo.wa.func.FuncParse;
import nc.vo.wa.func.SqlFragment;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.paydata.IFormula;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaLoginVOHelper;

/**
 * 
 * @author: zhangg
 * @date: 2010-7-13 下午02:12:25
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class ReDataCaculateService extends AbstractCaculateService {

	/**
	 * 补发
	 * 
	 * 说明： 后台业务操作，补发大部分时间是已父方案为准的。 只有累加到薪资发放时，是使用子方案pk。
	 * 
	 * @author zhangg on 2009-9-27
	 * @param selectWhere
	 *            影响到的pk_wa_data范围
	 * @throws DAOException
	 */
	public ReDataCaculateService(WaLoginContext loginContext, CaculateTypeVO caculateTypeVO, String selectWhere)
			throws BusinessException {
		super(loginContext);

		/**
		 * 范围还是有问题的
		 */
		setWaDataCacuRange4Class(caculateTypeVO, selectWhere);

	}

	@Override
	protected void initClassItems() throws BusinessException {

		// （1）系统项目系统公式（非自定义项）重新生成一遍。
		// 薪资补发是与被补发方案相关的。

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer
				.append("select wa_item.itemkey, wa_item.iitemtype, wa_item.ifldwidth,wa_item.category_id, wa_classitem.iflddecimal, wa_item.defaultflag, wa_item.iproperty, wa_classitem.*, 'Y' editflag"); // 1
		sqlBuffer.append("  from wa_classitem, wa_item");
		sqlBuffer.append("  where wa_classitem.pk_wa_item = wa_item.pk_wa_item and wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append("  and wa_classitem.cyear = ?  and wa_classitem.cperiod = ?  ");
		sqlBuffer.append(" order by icomputeseq");

		SQLParameter parameter = new SQLParameter();
		WaLoginVO waLoginVO = getLoginContext().getWaLoginVO();

		// 现在就能够进行业务操作的都是多次
		parameter.addParam(WaLoginVOHelper.getChildClassPK(waLoginVO));
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());

		classItemVOs = executeQueryVOs(sqlBuffer.toString(), parameter, WaClassItemVO.class);

	}

	@Override
	public void doCaculate() throws BusinessException {
		// ssx add on 20181105
		// for Decrypt before payment calculation.
		doDecryptWaData();
		// end

		// 薪酬业务员录入补发金额。薪资补发一般是公式项目进行补发，所以正常发薪的公式失效
		// 正常发新的额计算顺序也失效。我们的顺序就是
		// 已扣税基数--->已扣税---->本次扣税基数---->本次扣税---->应发合计---->扣款合计----->实发合计
		// 对于薪资补发来说，有用的公式只有系统项目公式！

		// 清空系统项目
		clearSysItem();

		// 对数值型项目。精度处理
		WaClassItemVO[] vos = getClassItemVOs();

		for (int i = 0; i < vos.length; i++) {
			updateDigits(vos[i]);
		}

		// 计算扣税基数
		Integer rePayTaxmode = getLoginContext().getWaLoginVO().getTaxmode();
		if (rePayTaxmode >= 2) {

			// 已扣税基数
			calculateLastTaxBase();

			// 本次扣税基数(本次扣税基数会包含已扣税基数)

			// ? 对于 代缴税呢？
			calculateCurrentTaxBase();

			// 算税
			calculateCurrentTax();

			copyCaculateResult2Waredata();

		}

		// 计算应发合计
		calculateF1();

		// 计算扣款合计
		calculateF2();
		// 计算实发合计
		calculateF3();

		afterCaculate();

		// ssx add on 20181105
		// for Encrypt after payment calculation.
		doEncrypt();
		// end
	}

	public void clearMidData() throws BusinessException {
		// 薪资计算完毕。如果不是调试状态，不是输出所有信息状态，则删除wa_cacu_data 中所有数据
		if (!Logger.isDebugEnabled() && !Logger.isInfoEnabled()) {
			StringBuffer deleteWa_cacu_data = new StringBuffer();
			deleteWa_cacu_data.append("delete from wa_cacu_data  ");
			deleteWa_cacu_data.append(" where pk_wa_class = '" + getLoginContext().getWaLoginVO().getPk_wa_class()
					+ "' ");
			executeSQLs(deleteWa_cacu_data);
		}
	}

	private void afterCaculate() throws BusinessException {
		// 删掉汇总数据（汇总数据－1年 ,-1期间 ）,
		// 设置补发数据为已经计算
		deleteCollectData();

		setRecaculateflag();

		// 重置wa_data中补发数据 为 0
		// 设置 最新起期间为尚未计算
		clearWaDataBufa();

		// 更新计算薪资期间状态标示为 尚未计算
		setWaperiodStateCaculateFlag();

		// clearMidData();
	}

	private void setWaperiodStateCaculateFlag() throws BusinessException {

		String condition = "  where wa_periodstate.pk_wa_period = ( select pk_wa_period  from wa_period where cyear = '"
				+ getLoginContext().getWaYear()
				+ "' and cperiod = '"
				+ getLoginContext().getWaPeriod()
				+ "' and pk_periodscheme ='"
				+ getLoginContext().getWaLoginVO().getPk_periodscheme()
				+ "'  )  and wa_periodstate.pk_wa_class = '"
				+ getZPk_wa_class(getLoginContext().getWaLoginVO())
				+ "'  ";
		updateTableByColKey("wa_periodstate", "caculateflag", UFBoolean.FALSE, condition);
	}

	/**
	 * 清空薪资补发数据中的 应发合计、扣款合计、实发合计、本次扣税基数、本次扣岁、已扣税基数、已扣税
	 * 
	 * @throws BusinessException
	 */
	private void clearSysItem() throws BusinessException {

		StringBuilder sbd = new StringBuilder();
		sbd.append("update wa_redata set f_1 = 0 ,	f_2 = 0 ,	f_3 = 0 ,	f_4 = 0 ,	f_5 = 0 ,	f_6 = 0 ,	f_7 = 0 ");
		sbd.append(getCacuRangeWhere());

		executeSQLs(sbd.toString());

	}

	private void calculateCurrentTax() throws BusinessException {
		// 得到算税公式
		WaClassItemVO[] vos = getClassItemVOs();
		WaClassItemVO itemVO = null;

		for (int i = 0; i < vos.length; i++) {
			if (vos[i].getItemkey().equals("f_5")) {// f_5 本次扣税
				itemVO = (WaClassItemVO) vos[i].clone();
				break;
			}
		}
		FunctionVO functionVO = HrWaXmlReader.getInstance().getTaxrateFunctionVO(
				getLoginContext().getWaLoginVO().getPk_country());// getFunctionVO(FunctionKey.TAXRATE);
		TaxFormulaVO taxFormulaVO = TaxFormulaUtil.translate2FormulaVO(functionVO, itemVO.getVformula());
		taxFormulaVO.setClass_type(TaxFormulaVO.CLASS_TYPE_REDATA);
		IFormula excutor = new TaxFormulaPreExcutor();
		getLoginContext().setInitData(itemVO);
		excutor.excute(taxFormulaVO, getLoginContext());

	}

	/**
	 * 
	 * @throws BusinessException
	 */
	private void copyCaculateResult2Waredata() throws BusinessException {
		// 计算完毕后，将 本次扣税基数，本次扣税 ，已扣税与已扣税基数复制过来。
		// 薪资类别仍旧是 父方案的PK

		WaLoginContext context = getLoginContext();
		String pk_wa_class = getYRePk_wa_class(context.getWaLoginVO());
		String userid = context.getPk_loginUser();

		StringBuilder sbd = new StringBuilder();
		sbd.append(" update	wa_redata ");
		sbd.append(" set ");
		sbd.append(" 	wa_redata.f_5 = (	select  ");// 本次扣税
		sbd.append("						coalesce(wa_cacu_data.cacu_value, 0) ");
		sbd.append("					from	wa_cacu_data 	 ");
		sbd.append("                    where	wa_redata.pk_wa_class = wa_cacu_data.pk_wa_class  and   wa_redata.pk_psndoc = wa_cacu_data.pk_psndoc and wa_cacu_data.pk_wa_class = '"
				+ pk_wa_class + "'     and wa_cacu_data.creator = '" + userid + "' ");

		sbd.append("	) ,");

		sbd.append(" wa_redata.f_4 = (	select "); // 本次扣税基数
		sbd.append("						coalesce(wa_cacu_data.tax_base, 0) 	from	wa_cacu_data 	where wa_redata.pk_wa_class = wa_cacu_data.pk_wa_class  and   wa_redata.pk_psndoc = wa_cacu_data.pk_psndoc and wa_cacu_data.pk_wa_class = '"
				+ pk_wa_class + "'     and wa_cacu_data.creator = '" + userid + "' ");
		sbd.append("	)  ");
		sbd.append(getCacuRangeWhere());
		// sbd.append(" where wa_redata.pk_wa_class = '"+ pk_wa_class
		// +"' and wa_redata.cyear = '"+ cyear +"' and wa_redata.cperiod = '"+
		// cPeriod +"' and wa_redata.creyear = '"+ reyear
		// +"' and wa_redata.creperiod = '"+ reperiod +"' ");

		executeSQLs(sbd.toString());

		WaClassItemVO itemVO = getClassitem("f_5");
		updateDigits(itemVO);
		itemVO = getClassitem("f_4");
		updateDigits(itemVO);
	}

	private void calculateLastTaxBase() throws BusinessException {
		// uodo
		WaLoginContext context = getLoginContext();
		Integer rePayTaxmode = context.getWaLoginVO().getTaxmode();
		// 0 不扣税 1 累计到本期扣税 2 补发期间扣税（不考虑补发历史） 3 补发期间扣税（考虑补发历史）
		if (rePayTaxmode == 2) {
			// 补发期间扣税（不考虑补发历史）,将 reyear \ reperiod 的wa_data 中的本次扣税基数与本次扣税直接 更新到
			// 中间表
			executeSQLs(getRePeriodTaxbaseSQL());

		} else if (rePayTaxmode == 3) {
			// 补发期间扣税（不考虑补发历史），寻找最后一次补发历史，将 的本次扣税基数与本次扣税直接 更新到 中间表
			IRepayQueryService service = NCLocator.getInstance().lookup(IRepayQueryService.class);
			PeriodStateVO periodStateVO = service.getRepayLastPeriod(context.getWaLoginVO());

			if (periodStateVO == null) {
				executeSQLs(getRePeriodTaxbaseSQL());
			} else {
				String pk_wa_class_z = periodStateVO.getPk_wa_class();
				String lastcyear = periodStateVO.getCyear();
				String lastcperiod = periodStateVO.getCperiod();
				executeSQLs(getLastRedataTaxbaseSQL(pk_wa_class_z, lastcyear, lastcperiod));
			}
		}
		// 已扣税基数
		WaClassItemVO itemVO = getClassitem("f_6");
		updateDigits(itemVO);
		// 已扣税
		itemVO = getClassitem("f_7");
		updateDigits(itemVO);

	}

	private String getRePeriodTaxbaseSQL() {
		WaLoginContext context = getLoginContext();
		String pk_wa_class = getYRePk_wa_class(context.getWaLoginVO());
		String reyear = context.getWaLoginVO().getReyear();
		String reperiod = context.getWaLoginVO().getReperiod();

		// 更新wa_redata 中的已扣税基数\
		// 更新wa_redata 中的已扣税
		StringBuilder sbd = new StringBuilder();
		// wa_redata中的已扣税基数=
		sbd.append(" update  wa_redata set f_6 = ");
		sbd.append(" (select wa_data.f_4 from wa_data where pk_wa_class = '" + pk_wa_class + "' and cyear = '" + reyear
				+ "' and cperiod = '" + reperiod + "'  ");
		sbd.append(" and  wa_redata.pk_psndoc = wa_data.pk_psndoc ), ");
		// wa_redata中的已扣税=
		sbd.append(" f_7 =  (select wa_data.f_5 from wa_data where pk_wa_class = '" + pk_wa_class + "' and cyear = '"
				+ reyear + "' and cperiod = '" + reperiod + "'  ");
		sbd.append("   and  wa_redata.pk_psndoc = wa_data.pk_psndoc ) ");
		sbd.append(getCacuRangeWhere());

		return sbd.toString();
	}

	private String getLastRedataTaxbaseSQL(String pk_wa_class_z, String lastcyear, String lastcperiod) {
		WaLoginContext context = getLoginContext();
		String pk_wa_class = getYRePk_wa_class(context.getWaLoginVO());
		String reyear = context.getWaLoginVO().getReyear();
		String reperiod = context.getWaLoginVO().getReperiod();

		StringBuilder sbd = new StringBuilder();
		// wa_redata中的已扣税基数=
		sbd.append(" update	wa_redata set	f_6 = (	select  max(lastredata.f_4) from	wa_redata  lastredata ");
		sbd.append(" 	 where	lastredata.cyear = '" + lastcyear + "' 	and lastredata.cperiod = '" + lastcperiod
				+ "' and lastredata.creyear = '" + reyear + "' and lastredata.creperiod = '" + reperiod
				+ "' and lastredata.pk_wa_class = '" + pk_wa_class + "' ");
		sbd.append(" 		and lastredata.pk_wa_class_z = '" + pk_wa_class_z
				+ "'  and   wa_redata.pk_psndoc = lastredata.pk_psndoc   ), ");
		// wa_redata中的已扣税基数= 最后一次的 已扣税＋ 最后一次的扣税
		sbd.append("	f_7 = (	select  max(lastredata2.f_5+  lastredata2.f_7 )  from	wa_redata  lastredata2 ");
		sbd.append("		 where	lastredata2.cyear = '" + lastcyear + "'  	and lastredata2.cperiod = '" + lastcperiod
				+ "' and lastredata2.creyear = '" + reyear + "' and lastredata2.creperiod = '" + reperiod
				+ "' and lastredata2.pk_wa_class = '" + pk_wa_class + "'");
		sbd.append("		and lastredata2.pk_wa_class_z = '" + pk_wa_class_z
				+ "'  and   wa_redata.pk_psndoc = lastredata2.pk_psndoc ) ");

		sbd.append(getCacuRangeWhere());

		return sbd.toString();
	}

	private void calculateCurrentTaxBase() throws BusinessException {
		WaClassItemVO[] vos = getClassItemVOs();
		WaClassItemVO itemVO = null;

		if (vos == null)
			return;
		for (int i = 0; i < vos.length; i++) {
			if (vos[i].getItemkey().equals("f_4")) {
				itemVO = (WaClassItemVO) vos[i].clone();
				break;
			}
		}
		if (itemVO == null)
			return;
		// 根据公式计算扣税基数。
		execute(itemVO);

		updateDigits(itemVO);
		// setCurrentTaxBase();

	}

	// private void setCurrentTaxBase()throws BusinessException{
	// WaLoginContext context = getLoginContext();
	// String pk_wa_class = context.getPk_wa_class();
	// String reyear = context.getWaLoginVO().getReyear();
	// String reperiod = context.getWaLoginVO().getReperiod();
	// String cyear = context.getWaLoginVO().getCyear();
	// String cPeriod = context.getWaLoginVO().getCperiod();
	//
	//
	//
	// StringBuilder sbd = new StringBuilder();
	// sbd.append(" update  wa_cacu_data set tax_base = taxedbase + "
	// );//上次扣税基数＋本次扣税基数
	// sbd.append("  (select  f_4  from wa_redata where pk_wa_class = '"+pk_wa_class+"' and cyear = '"+
	// cyear +"' and cperiod = '"+ cPeriod
	// +"' and creyear = '"+reyear+"' and creperiod = '"+reperiod+"' and wa_redata.pk_psndoc =  wa_cacu_data.pk_psndoc) "
	// );
	// sbd.append("		where   wa_cacu_data.pk_wa_class ='" + pk_wa_class + "'  "
	// );
	// sbd.append("		and wa_cacu_data.creator = '"+context.getPk_loginUser()+"'    "
	// );
	// executeSQLs(sbd.toString());
	// }

	private void execute(WaClassItemVO itemVO) throws BusinessException {
		String formula = itemVO.getVformula();
		formula = parse(formula);
		SqlFragment sqlFragment = new SqlFragment();
		sqlFragment.setValue(formula);
		execute(itemVO, sqlFragment);
	}

	private void execute(WaClassItemVO itemVO, SqlFragment sqlFragment) throws BusinessException {

		try {
			getBaseDao().executeUpdate(translate2ExecutableSql(itemVO, sqlFragment));
			updateDigits(itemVO);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130repaydata", "060130repaydata0511")/*
																									 * @
																									 * res
																									 * "薪资项目:"
																									 */
					+ itemVO.getMultilangName() + ResHelper.getString("60130repaydata", "060130repaydata0512")/*
																											 * @
																											 * res
																											 * "公式设置错误。 请检查。"
																											 */);
		}
	}

	@Override
	public String translate2ExecutableSql(WaClassItemVO itemVO, SqlFragment sqlFragment) throws BusinessException {
		String value = sqlFragment.getValue();

		value = parse(value);
		value = FuncParse.addSourceTable2Value(value);

		String condition = sqlFragment.getCondition();
		condition = parse(condition);
		condition = FuncParse.addSourceTable2Conditon(condition);

		String where = getCacuRangeWhere();
		if (condition != null) {
			condition = WherePartUtil.formatAddtionalWhere(condition);
			where += condition;
		}
		value = value.replaceAll("wa_data", "wa_redata");

		where = where.replaceAll("wa_data", "wa_redata");

		return "update wa_redata set wa_redata." + itemVO.getItemkey() + " = (" + value + ") " + where;

	}

	private void updateDigits(WaClassItemVO itemVO) throws BusinessException {
		if (itemVO == null) {
			return;
		}
		if (DataVOUtils.isDigitsAttribute(itemVO.getItemkey())) {

			int digits = itemVO.getIflddecimal();
			/**
			 * 四舍五入， 进位， 舍位 薪资提供的进行方式同普通意义上的进位不同， 需求如下：
			 * 
			 * 在小数位数后增加进位方式属性,下拉选择,系统提供进位 舍位和四舍五入三种舍位方式
			 * 进位，根据用户设定的小数位数,当计算结果中小数位数的的后一位不等于0时小数位数的最后一位加1
			 * 舍位，根据用户设定的小数位数,不论计算结果中小数位数的后一位是否等于0,均直接舍弃
			 * 四舍五入,根据用户设定的小数位数,计算结果中小数位数的后一位按照四舍五入的规则进行进位或舍位计算
			 */

			if (itemVO.getRound_type() == null || itemVO.getRound_type().intValue() == 0) {// 四舍五入

				String where = getCacuRangeWhere();
				String sql = "update wa_redata set wa_redata." + itemVO.getItemkey() + " = round((wa_redata."
						+ itemVO.getItemkey() + "), " + digits + " )" + where;
				executeSQLs(sql);
			} else {
				UFDouble f = UFDouble.ZERO_DBL;
				if (itemVO.getRound_type().intValue() == 1) {// 进位
					f = new UFDouble(0.4f);
				} else if (itemVO.getRound_type().intValue() == 2) {// 舍位操作
					f = new UFDouble(-0.5f);
				} else {// 默认四舍五入
					f = UFDouble.ZERO_DBL;
				}

				f = f.multiply(Math.pow(0.1, digits));
				String where = getCacuRangeWhere() + " and wa_redata." + itemVO.getItemkey() + " > 0";
				String sql = "update wa_redata set wa_redata." + itemVO.getItemkey() + " = round(((wa_redata."
						+ itemVO.getItemkey() + ") + " + f + "), " + digits + " )" + where;

				String where1 = getCacuRangeWhere() + " and wa_redata." + itemVO.getItemkey() + " < 0";
				f = f.multiply(-1);
				String sql1 = "update wa_redata set wa_redata." + itemVO.getItemkey() + " = round(((wa_redata."
						+ itemVO.getItemkey() + ") + " + f + "), " + digits + " )" + where1;

				executeSQLs(sql, sql1);
			}

		}

	}

	/**
	 * 计算应发合计
	 * 
	 * @throws BusinessException
	 */
	private void calculateF1() throws BusinessException {

		createF_1();

		// 调整小数位数
		WaClassItemVO itemVO = getClassitem("f_1");
		updateDigits(itemVO);
	}

	WaClassItemVO getClassitem(String itemkey) {
		WaClassItemVO[] vos = getClassItemVOs();
		for (int i = 0; i < vos.length; i++) {
			if (vos[i].getItemkey().equals(itemkey)) {
				return vos[i];
			}

		}

		return null;
	}

	private void calculateF2() throws BusinessException {

		executeSQLs(createF_2());

		WaClassItemVO itemVO = getClassitem("f_2");
		updateDigits(itemVO);

		// 对于代缴税应该执行调整
		adjustF2();

	}

	private void adjustF2() throws BusinessException {
		WaClassItemVO f_2 = new WaClassItemVO();
		for (WaClassItemVO itemVO : getClassItemVOs()) {
			if (itemVO.getItemkey().equalsIgnoreCase("f_2")) {
				f_2 = itemVO;
				break;
			}
		}
		// 系统公式。
		if (f_2.getItemkey().equalsIgnoreCase("f_2") && f_2.getIssysformula().booleanValue()) {
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append(" where  wa_redata.pk_wa_redata in "); // 1
			sqlBuffer.append("(select wa_redata.pk_wa_redata "); // 1
			sqlBuffer.append("  from wa_redata, wa_cacu_data ");
			sqlBuffer.append(" where wa_redata.pk_psndoc = wa_cacu_data.pk_psndoc ");
			sqlBuffer.append("   and wa_redata.pk_wa_class = wa_cacu_data.pk_wa_class ");
			sqlBuffer.append("   and wa_redata.pk_wa_class_z = '" + getZPk_wa_class(getLoginContext().getWaLoginVO())
					+ "' ");
			sqlBuffer.append("   and wa_redata.cyear = '" + getLoginContext().getWaYear() + "'  ");
			sqlBuffer.append("   and wa_redata.cperiod = '" + getLoginContext().getWaPeriod() + "'");

			// 必须添加补发年与补发期间
			sqlBuffer.append("   and wa_redata.pk_wa_class = '" + getYRePk_wa_class(getLoginContext().getWaLoginVO())
					+ "'");
			sqlBuffer.append(" and wa_redata.creyear = '" + getLoginContext().getWaLoginVO().getReyear() + "'  ");
			sqlBuffer.append(" and wa_redata.creperiod = '" + getLoginContext().getWaLoginVO().getReperiod()
					+ "'  and wa_cacu_data.taxtype = 1  )");

			String formualr = "  (f_2 - f_5) ";
			String f = " update wa_redata set wa_redata.f_2 = " + formualr + sqlBuffer.toString();

			executeSQLs(f);
		}
	}

	private void calculateF3() throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update wa_redata "); // 1
		sqlBuffer.append("   set wa_redata.f_3 =  wa_redata.f_1-wa_redata.f_2"); // 2
		sqlBuffer.append(getCacuRangeWhere());
		executeSQLs(sqlBuffer);

		WaClassItemVO itemVO = getClassitem("f_3");
		updateDigits(itemVO);

	}

	/**
	 * 当前薪资方案的补发数据删除。
	 * 
	 * @throws BusinessException
	 */
	private void deleteCollectData() throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" delete from  wa_redata   "); // 1
		sqlBuffer.append("   where  wa_redata.pk_wa_class_z = '" + getZPk_wa_class(getLoginContext().getWaLoginVO())
				+ "' ");
		sqlBuffer.append("   and wa_redata.cyear = '" + getLoginContext().getWaYear() + "'  ");
		sqlBuffer.append("   and wa_redata.cperiod = '" + getLoginContext().getWaPeriod() + "'");

		// 必须添加补发年与补发期间
		sqlBuffer.append(" and wa_redata.creyear = '-1'  ");
		sqlBuffer.append(" and wa_redata.creperiod = '-1'");

		executeSQLs(sqlBuffer.toString());

	}

	/**
	 * 当前薪资方案的补发数据置零（子方案与普通薪资方案） wa_data
	 * 
	 * @throws BusinessException
	 */
	private void clearWaDataBufa() throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();

		sqlBuffer.append(" update wa_data set f_8=0, f_9=0  where pk_wa_class = '"
				+ getZPk_wa_class(getLoginContext().getWaLoginVO()) + "' and cyear  = '"
				+ getLoginContext().getWaYear() + "' and cperiod = '" + getLoginContext().getWaPeriod() + "' "); // 1
		executeSQLs(sqlBuffer.toString());
	}

	/**
	 * 父方案与普通方案
	 * 
	 * @throws BusinessException
	 */

	private void setRecaculateflag() throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" update wa_redata set caculateflag='Y',reflag='N'   "); // 已经计算,尚未传递
		sqlBuffer.append("   where  wa_redata.pk_wa_class_z = '" + getZPk_wa_class(getLoginContext().getWaLoginVO())
				+ "' ");
		sqlBuffer.append("   and wa_redata.cyear = '" + getLoginContext().getWaYear() + "'  ");
		sqlBuffer.append("   and wa_redata.cperiod = '" + getLoginContext().getWaPeriod() + "'");
		sqlBuffer
				.append("   and wa_redata.pk_wa_class = '" + getYRePk_wa_class(getLoginContext().getWaLoginVO()) + "'");
		sqlBuffer.append(" and wa_redata.creyear = '" + getLoginContext().getWaLoginVO().getReyear() + "'  ");
		sqlBuffer.append(" and wa_redata.creperiod = '" + getLoginContext().getWaLoginVO().getReperiod() + "'");

		executeSQLs(sqlBuffer.toString());

	}

	/**
	 * 应发合计
	 * 
	 * @author zhangg on 2010-7-13
	 * @return
	 * @throws BusinessException
	 */
	private void createF_1() throws BusinessException {

		WaClassItemVO f_1 = new WaClassItemVO();
		for (WaClassItemVO itemVO : getClassItemVOs()) {
			if (itemVO.getItemkey().equalsIgnoreCase("f_1")) {
				f_1 = itemVO;
				break;
			}
		}

		String vformual = f_1.getVformula();
		f_1.setVformula(vformual.replaceAll("wa_data", "wa_redata"));

		SqlFragment sqlFragment = new SqlFragment();
		sqlFragment.setValue(f_1.getVformula());
		translate2ExecutableSqlForFormula(f_1, sqlFragment);

		//
		// String sql = "(0";
		//
		// if (getClassItemVOs() != null && getClassItemVOs().length > 0) {
		// for (WaClassItemVO itemVO : getClassItemVOs()) {
		// if (DataVOUtils.isDigitsAttribute(itemVO.getItemkey())
		// && !itemVO.getDefaultflag().booleanValue() &&
		// itemVO.getIproperty().intValue() == 0) {
		// sql += "+";
		// sql += itemVO.getItemkey();
		// }
		// }
		// }
		// sql += " + f_8 "; // 补发
		//
		// sql += "" + ") ";
		//
		// return sql;

	}

	/**
	 * 扣款合计
	 * 
	 * @author zhangg on 2010-7-13
	 * @return
	 * @throws BusinessException
	 */
	private String createF_2() throws BusinessException {
		// String sql = "(0";
		//
		// if (getClassItemVOs() != null && getClassItemVOs().length > 0) {
		// for (WaClassItemVO itemVO : getClassItemVOs()) {
		// if (DataVOUtils.isDigitsAttribute(itemVO.getItemkey())
		// && !itemVO.getDefaultflag().booleanValue() &&
		// itemVO.getIproperty().intValue() == 1) {
		// sql += "+";
		// sql += itemVO.getItemkey();
		// }
		// }
		// }
		//
		// sql += " f_5 " + ") ";//本次扣税
		//
		// return sql;

		WaClassItemVO f_2 = new WaClassItemVO();
		for (WaClassItemVO itemVO : getClassItemVOs()) {
			if (itemVO.getItemkey().equalsIgnoreCase("f_2")) {
				f_2 = itemVO;
				break;
			}
		}

		String vformual = f_2.getVformula();
		f_2.setVformula(vformual.replaceAll("wa_data", "wa_redata"));

		SqlFragment sqlFragment = new SqlFragment();
		sqlFragment.setValue(f_2.getVformula());
		return translate2ExecutableSql(f_2, sqlFragment);

	}

	// /**
	// * 扣税基数
	// *
	// * @author zhangg on 2010-7-13
	// * @return
	// */
	// private String createF_4() {
	// String sql = "(0";
	//
	// if (getClassItemVOs() != null && getClassItemVOs().length > 0) {
	// for (WaClassItemVO itemVO : getClassItemVOs()) {
	// if (DataVOUtils.isDigitsAttribute(itemVO.getItemkey())
	// && !itemVO.getDefaultflag().booleanValue() &&
	// itemVO.getTaxflag().booleanValue()) {
	// if (itemVO.getIproperty().intValue() == 1) {
	// sql += "-";
	// } else {
	// sql += "+";
	// }
	//
	// sql += itemVO.getItemkey();
	// }
	// }
	// }
	// sql += " + f_9 "; // 补发
	//
	// sql += "" + ") ";
	//
	// return sql;
	//
	// }

	/**
	 * 本次扣税
	 * 
	 * @author zhangg on 2010-7-13
	 * @return
	 * @throws BusinessException
	 */
	// private String createF_5() throws BusinessException {
	// WaClassItemVO f_5 = new WaClassItemVO();
	// for (WaClassItemVO itemVO : getClassItemVOs()) {
	// if (itemVO.getItemkey().equalsIgnoreCase("f_5")) {
	// f_5 = itemVO;
	// break;
	// }
	// }
	//
	// String vformual = f_5.getVformula();
	// f_5.setVformula(vformual.replaceAll("wa_data", "wa_redata"));
	//
	// SqlFragment sqlFragment = new SqlFragment();
	// sqlFragment.setValue(f_5.getVformula());
	// return translate2ExecutableSql(f_5, sqlFragment);
	// }

	private void setWaDataCacuRange4Class(CaculateTypeVO caculateTypeVO, String where) throws BusinessException {
		// 计算范围
		boolean all = caculateTypeVO.getRange().booleanValue();
		if (where == null || all) {
			where = null;
		}

		// 计算方式
		boolean type = caculateTypeVO.getType().booleanValue();
		if (type) {
			where = where == null ? " (1=1) " : where;
		} else {
			String addWhere = " wa_redata.caculateflag = 'N'   ";
			where = where == null ? addWhere : (where + " and   " + addWhere);
		}

		// where 里面应该加上业务操作权限范围限制

		// String poweConditon2 =
		// DataPermissionUtils.getDataPermissionSQLWherePart(IHRWADataResCode.REPAYDATA,
		// IHRWAActionCode.ReCalculate);
		// String poweConditon = WaPowerSqlHelper.getWaPowerSql(null,
		// IHRWADataResCode.REPAYDATA, IHRWAActionCode.ReCalculate,
		// "wa_redata");
		//
		// //添加 add
		// where = where + WherePartUtil.formatAddtionalWhere(poweConditon);
		setWaDataCacuRange4Class(where);

	}

	/**
	 * 根据 （父方案与普通薪资方案） 确定计薪范围
	 */
	@Override
	public void setWaDataCacuRange4Class(String where) throws BusinessException {

		if (where == null) {
			where = getCommonWhereCondtion4Data(getLoginContext().getWaLoginVO());
		} else {
			where = where + " and " + getCommonWhereCondtion4Data(getLoginContext().getWaLoginVO());
		}

		where = where + " and " + getCommonWhereCondtion4ReData(getLoginContext().getWaLoginVO());

		String creator = getLoginContext().getPk_loginUser();
		String pk_wa_class = getYRePk_wa_class(getLoginContext().getWaLoginVO());
		// 删除该该方案计算范围内的数据
		StringBuffer deleteWa_cacu_data = new StringBuffer();
		deleteWa_cacu_data.append("delete from wa_cacu_data  ");
		deleteWa_cacu_data.append(" where pk_wa_class = '" + pk_wa_class + "' ");

		// 增加.
		StringBuffer insert2Wa_cacu_data = new StringBuffer();
		insert2Wa_cacu_data.append("insert into wa_cacu_data "); // 1
		insert2Wa_cacu_data
				.append("  (pk_cacu_data,  taxtype , taxtableid , isndebuct,pk_wa_class, pk_wa_data, pk_psndoc, cacu_value, creator, currencyrate,redatamode) "); // 2
		insert2Wa_cacu_data
				.append("  select wa_data.pk_wa_data , wa_data.taxtype , wa_data.taxtableid , wa_data.isndebuct, wa_data.pk_wa_class, wa_data.pk_wa_data, wa_data.pk_psndoc, 0, '"
						+ creator + "',  " + getCurrenyRate() + "," + getRedataMode()); // 3
		insert2Wa_cacu_data
				.append("    from wa_data   inner join wa_redata on  wa_redata.pk_psndoc =wa_data.pk_psndoc");
		insert2Wa_cacu_data.append("   where   " + where);

		executeSQLs(deleteWa_cacu_data, insert2Wa_cacu_data);

	}

	/**
	 * 查询被补发期间的人员扣税等信息
	 * 
	 * @param waLoginVO
	 * @return
	 */
	public static String getCommonWhereCondtion4Data(WaLoginVO waLoginVO) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("  wa_data.pk_wa_class = '" + getYRePk_wa_class(waLoginVO) + "' "); // 1
		sqlBuffer.append("   and wa_data.cyear = '" + waLoginVO.getReyear() + "' "); // 2
		sqlBuffer.append("   and wa_data.cperiod = '" + waLoginVO.getReperiod() + "' "); // 3
		sqlBuffer.append("   and wa_data.stopflag = 'N'"); // 4
		return sqlBuffer.toString();
	}

	/**
	 * 得到 被补发薪资方案的pk
	 * 
	 * @return
	 */
	private static String getYRePk_wa_class(WaLoginVO waLoginVO) {

		return waLoginVO.getPk_prnt_class();
		// if(waLoginVO.isMultiClass()){
		// return waLoginVO.getPk_prnt_class();
		// }else{
		// return waLoginVO.getPk_wa_class();
		// }
	}

	/**
	 * 得到当前薪资方案 只有多次发薪，累加到薪资发放时，使用子方案PK
	 * 
	 * @param waLoginVO
	 * @return
	 */
	private static String getZPk_wa_class(WaLoginVO waLoginVO) {
		return waLoginVO.getPk_wa_class();
	}

	/**
	 * 
	 * @param waLoginVO
	 * @return
	 */
	public static String getCommonWhereCondtion4ReData(WaLoginVO waLoginVO) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("  wa_redata.pk_wa_class_z = '" + getZPk_wa_class(waLoginVO) + "' "); // 1
		sqlBuffer.append("   and wa_redata.cyear = '" + waLoginVO.getCyear() + "' "); // 2
		sqlBuffer.append("   and wa_redata.cperiod = '" + waLoginVO.getCperiod() + "' "); // 3
		sqlBuffer.append("   and wa_redata.pk_wa_class = '" + getYRePk_wa_class(waLoginVO) + "' "); // 3
		sqlBuffer.append("   and wa_redata.creyear = '" + waLoginVO.getReyear() + "' "); // 2
		sqlBuffer.append("   and wa_redata.creperiod = '" + waLoginVO.getReperiod() + "' "); // 3
		sqlBuffer.append("   and wa_redata.stopflag = 'N'"); // 4
		return sqlBuffer.toString();
	}

	/**
	 * 得到薪资补发计算人员范围
	 * 
	 * 需要重新 增加一个薪资方案pk
	 * 
	 * @author zhangg on 2009-12-4
	 * @param waLoginVO
	 * @return
	 */
	@Override
	public String getCacuRangeWhere() {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" where  wa_redata.pk_wa_redata in "); // 1
		sqlBuffer.append("(select wa_redata.pk_wa_redata "); // 1
		sqlBuffer.append("  from wa_redata, wa_cacu_data ");
		sqlBuffer.append(" where wa_redata.pk_psndoc = wa_cacu_data.pk_psndoc ");
		sqlBuffer.append("   and wa_redata.pk_wa_class = wa_cacu_data.pk_wa_class ");
		sqlBuffer.append("   and wa_redata.pk_wa_class_z = '" + getZPk_wa_class(getLoginContext().getWaLoginVO())
				+ "' ");
		sqlBuffer.append("   and wa_redata.cyear = '" + getLoginContext().getWaYear() + "'  ");
		sqlBuffer.append("   and wa_redata.cperiod = '" + getLoginContext().getWaPeriod() + "'");

		// 必须添加补发年与补发期间
		sqlBuffer
				.append("   and wa_redata.pk_wa_class = '" + getYRePk_wa_class(getLoginContext().getWaLoginVO()) + "'");
		sqlBuffer.append(" and wa_redata.creyear = '" + getLoginContext().getWaLoginVO().getReyear() + "'  ");
		sqlBuffer.append(" and wa_redata.creperiod = '" + getLoginContext().getWaLoginVO().getReperiod() + "')");

		return sqlBuffer.toString();
	}

	/**
	 * 用于完善系统公式补丁，由于薪资补发需要处理(如果 则 否则)的公式
	 * 
	 * @param itemVO
	 * @param sqlFragment
	 * @return
	 * @throws BusinessException
	 */
	public String translate2Sql(WaClassItemVO itemVO, SqlFragment sqlFragment) throws BusinessException {
		String value = sqlFragment.getValue();

		value = parse(value);
		value = FuncParse.addSourceTable2Value(value);

		String condition = sqlFragment.getCondition();
		condition = parse(condition);
		condition = FuncParse.addSourceTable2Conditon(condition);

		String where = getCacuRangeWhere();
		if (condition != null) {
			condition = WherePartUtil.formatAddtionalWhere(condition);
			where += condition;
		}

		// 对于数值型，添加默认值0
		value = addDefaultVaule(itemVO, value);
		StringBuilder sbd = new StringBuilder();
		if (DataVOUtils.isDigitsAttribute(itemVO.getItemkey())
				&& (itemVO.getRound_type() == null || itemVO.getRound_type().intValue() == 0)) {
			int digits = itemVO.getIflddecimal();
			sbd.append(" update wa_redata set wa_redata." + itemVO.getItemkey() + " = round((" + value + "), " + digits
					+ " ) " + where);
		} else {
			sbd.append("update wa_redata set wa_redata." + itemVO.getItemkey() + " = (" + value + ") " + where);
		}
		return sbd.toString();

	}

	private String addDefaultVaule(WaClassItemVO itemVO, String value) {
		if (itemVO.getIitemtype().equals(TypeEnumVO.FLOATTYPE.value())) {
			return " isnull((" + value + "),0)";
		}
		return value;
	}

	public void translate2ExecutableSqlForFormula(WaClassItemVO itemVO, SqlFragment sqlFragment)
			throws BusinessException {
		// wanglqh 由于薪资补发需要处理(如果 则 否则)的公式

		IPreTranslate waPreTranslate = new WaPreTranslate();
		String formua = waPreTranslate.parse(itemVO.getPk_org(), itemVO.getVformula());
		itemVO.setVformula(formua);
		List<SqlFragment> fragmentList = FuncParse.parse(formua);
		if (fragmentList != null && fragmentList.size() > 0) {
			for (SqlFragment sqlFragmentList : fragmentList) {
				getBaseDao().executeUpdate(translate2Sql(itemVO, sqlFragmentList));
			}
		}
	}

}