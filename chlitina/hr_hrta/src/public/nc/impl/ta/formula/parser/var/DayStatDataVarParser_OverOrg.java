package nc.impl.ta.formula.parser.var;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.impl.hr.formula.parser.AbstractVarParser;
import nc.itf.ta.ITimeItemQueryMaintain;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.algorithm.DateTimeUtils;
import nc.jdbc.framework.DataSourceCenter;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.leavebalance.LeaveFormulaCalParam;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

/**
 * 休假规则的公式中，对当期累计，上期汇总，当年累计，上年汇总的处理
 * 当期累计、上期汇总的解释，分三种情况：
 * 按期间结算的类别，当期就是计算时指定的year+month对应的期间，若期间不存在，则结果为0.累计的日期范围为期间第一天到min(期间最后一天,计算日)
 * 					上期汇总的上期就是上面确定的当期的上期，汇总的日期范围为上期第一天到最后一天
 * 按年结算的类别，当期就是min(年度最后一个考勤期间，计算日所属期间)，若两个期间中有一个为空，则结果为0
 * 				     上期就是min(上年度倒数第二个期间，计算日所属期间的上期)，若两个期间中有一个为空，则结果为0
 * 按入职日结算的类别，当期就是计算日所属期间，上期就是计算日所属期间的上期
 * 
 * 当年累计、上年汇总的解释，也分三种情况：
 * 按期间结算的类别，当年就是计算时指定的year对应的考勤年度，若不存在，则结果为0.累计的范围是年度第一天到min(年度最后一天,计算日)
 * 					上年汇总的上年就是上面确定的考勤年度的上一年，若不存在，则结果为0
 * 按年结算的类别，与按期间结算一样
 * 按入职年结算的类别，当年就是year对应的入职年，累计的范围是入职年第一天到min(入职年最后一天,计算日)
 * 					  上年就是year对应的上一个入职年
 * 
 * 其实，当期累计、上期汇总对于按年、入职日结算的类别，应该是无意义的
 * @author zengcheng
 *
 */
public class DayStatDataVarParser_OverOrg extends AbstractVarParser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 335713535227580392L;
	
	private static final int CUR_PERIOD=1;//当期
	private static final int PREVIOUS_PERIOD=2;//上期
	private static final int CUR_YEAR=3;//当年
	private static final int PREVIOUS_YEAR=4;//上年
	
	public DayStatDataVarParser_OverOrg(){
		setVarName("DAYSTATDATA_OVERORG");
	}
	
	private String[] getSumBeginEndDate(int periodType,LeaveFormulaCalParam param){
		LeaveTypeCopyVO typeVO = param.getTypeVO();
		int settlePeriod = typeVO.getLeavesetperiod().intValue();//结算周期
		switch(settlePeriod){
		// ssx added on 2018-03-16
		// for changes of start date of company age
		case TimeItemCopyVO.LEAVESETPERIOD_STARTDATE:
			//
		case TimeItemCopyVO.LEAVESETPERIOD_DATE:
			return getSumBeginEndDate4HireDate(periodType, param);
		case TimeItemCopyVO.LEAVESETPERIOD_YEAR:
			return getSumBeginEndDate4Year(periodType, param);
		}
		return getSumBeginEndDate4Month(periodType, param);
	}

	private String[] getSumBeginEndDate4HireDate(int periodType,LeaveFormulaCalParam param){
		UFLiteralDate calDate = param.getCalDate();
		switch(periodType){
		case CUR_PERIOD:
			PeriodVO curPeriod = param.getCalDateBelongToPeriod();
			if(curPeriod==null)
				return null;
			if(calDate.before(curPeriod.getBegindate()))
				return null;
			return new String[]{"'"+curPeriod.getBegindate()+"'","'"+DateTimeUtils.min(calDate, curPeriod.getEnddate())+"'"};
		case PREVIOUS_PERIOD:
			PeriodVO previousPeriod = param.getPreviousCalDateBelongToPeriod();
			if(previousPeriod==null)
				return null;
			return new String[]{"'"+previousPeriod.getBegindate()+"'","'"+previousPeriod.getEnddate()+"'"};
		case CUR_YEAR:
			return new String[]{"tbm_leavebalance."+LeaveBalanceVO.HIREBEGINDATE,
					LEASTFUNC()+"(tbm_leavebalance."+LeaveBalanceVO.HIREENDDATE+",'"+calDate+"')"};
		case PREVIOUS_YEAR:
			String year= param.getCalYear();
			String previousYear = Integer.toString(Integer.parseInt(year)-1);
			return new String[]{"(select top 1 t.hirebegindate from tbm_leavebalance t where t.pk_org=tbm_leavebalance.pk_org "+
					"and t.pk_psnorg=tbm_leavebalance.pk_psnorg and t.pk_timeitem=tbm_leavebalance.pk_timeitem and t.curyear='"+previousYear+"')",
					"(select top 1 t.hireenddate from tbm_leavebalance t where t.pk_org=tbm_leavebalance.pk_org "+
					"and t.pk_psnorg=tbm_leavebalance.pk_psnorg and t.pk_timeitem=tbm_leavebalance.pk_timeitem and t.curyear='"+previousYear+"')"};
		}
		return null;
	}
	
	private PeriodVO getCurPeriod4Year(LeaveFormulaCalParam param){
		PeriodVO[] yearPeriodVOs = param.getCalYearPeriods();
		PeriodVO calDateBelongtoPeriod = param.getCalDateBelongToPeriod();
		if(ArrayUtils.isEmpty(yearPeriodVOs)||calDateBelongtoPeriod==null)
			return null;
		return PeriodVO.min(yearPeriodVOs[yearPeriodVOs.length-1], calDateBelongtoPeriod);
	}
	
	private PeriodVO getPreviousPeriod4Year(LeaveFormulaCalParam param){
		PeriodVO[] yearPeriodVOs = param.getCalYearPeriods();
		PeriodVO previousCalDateBelongtoPeriod = param.getPreviousCalDateBelongToPeriod();
		if(ArrayUtils.isEmpty(yearPeriodVOs)||previousCalDateBelongtoPeriod==null)
			return null;
		if(yearPeriodVOs.length>1)
			return PeriodVO.min(yearPeriodVOs[yearPeriodVOs.length-2], previousCalDateBelongtoPeriod);
		PeriodVO[] perviousYearPeriodVOs = param.getPreviousCalYearPeriods();
		if(ArrayUtils.isEmpty(perviousYearPeriodVOs))
			return null;
		return PeriodVO.min(perviousYearPeriodVOs[perviousYearPeriodVOs.length-1], previousCalDateBelongtoPeriod);
	}
	
	private String[] getSumBeginEndDate4Year(int periodType,LeaveFormulaCalParam param){
		UFLiteralDate calDate = param.getCalDate();
		switch(periodType){
		case CUR_PERIOD:
			PeriodVO curPeriod = getCurPeriod4Year(param);
			if(curPeriod==null)
				return null;
			if(calDate.before(curPeriod.getBegindate()))
				return null;
			return new String[]{"'"+curPeriod.getBegindate()+"'","'"+DateTimeUtils.min(calDate, curPeriod.getEnddate())+"'"};
		case PREVIOUS_PERIOD:
			PeriodVO previousPeriod = getPreviousPeriod4Year(param);
			if(previousPeriod==null)
				return null;
			return new String[]{"'"+previousPeriod.getBegindate()+"'","'"+previousPeriod.getEnddate()+"'"};
		case CUR_YEAR:
			return getSumBeginEndDate4Month(periodType, param);
		case PREVIOUS_YEAR:
			return getSumBeginEndDate4Month(periodType, param);
		}
		return null;
	}
	
	private String[] getSumBeginEndDate4Month(int periodType,LeaveFormulaCalParam param){
		UFLiteralDate calDate = param.getCalDate();
		switch(periodType){
		case CUR_PERIOD:
			PeriodVO curPeriod = param.getCalPeriod();
			if(curPeriod==null)
				return null;
			if(calDate.before(curPeriod.getBegindate()))
				return null;
			return new String[]{"'"+curPeriod.getBegindate()+"'","'"+DateTimeUtils.min(calDate, curPeriod.getEnddate())+"'"};
		case PREVIOUS_PERIOD:
			PeriodVO previousPeriod = param.getPreviousCalPeriod();
			if(previousPeriod==null)
				return null;
			return new String[]{"'"+previousPeriod.getBegindate()+"'","'"+previousPeriod.getEnddate()+"'"};
		case CUR_YEAR:
			UFLiteralDate yearBeginDate = param.getCalYearBeginDate();
			if(yearBeginDate==null)
				return null;
			UFLiteralDate yearEndDate = param.getCalYearEndDate();
			return new String[]{"'"+yearBeginDate+"'","'"+DateTimeUtils.min(calDate, yearEndDate)+"'"};
		case PREVIOUS_YEAR:
			UFLiteralDate previousYearBeginDate = param.getPreviousCalYearBeginDate();
			if(previousYearBeginDate==null)
				return null;
			UFLiteralDate previousYearEndDate = param.getPreviousCalYearEndDate();
			return new String[]{"'"+previousYearBeginDate+"'","'"+previousYearEndDate+"'"};
		}
		return null;
	}
	
	private String LEASTFUNC(){//LEAST函数的名字。oracle预置了least函数，sqlserver和db2没有，需要创建用户函数HR_LEAST.在创建HR系统的时候生成
		int db_type = getDbType();
		switch(db_type){
		case DataSourceCenter.SQLSERVER:
		case DataSourceCenter.DB2:
			return "DBO.HR_LEAST";
		}
		return "LEAST";
	}

	@Override
	protected String translateVar2SQL(String pk_org, String formula,
			String varStr, String[] vars, Object... params)
			throws BusinessException {
		LeaveFormulaCalParam param = (LeaveFormulaCalParam)params[0];
		int periodType = getPeriodType(vars[1]);
		String[] dateScope = getSumBeginEndDate(periodType, param);
		if(ArrayUtils.isEmpty(dateScope))
			return "0";
		String fieldCode = vars[2];
		boolean isdaystatb = CommonMethods.isPrimaryKey(fieldCode);//如果是20位主键，则需要从日报子表中取得
//		String daystatCond = "where daystat.pk_org=tbm_leavebalance.pk_org " +
//					"and daystat.pk_psndoc=tbm_leavebalance.pk_psndoc and daystat.calendar between "+
//					dateScope[0]+" and "+dateScope[1];
		//需要跨组织取数，去掉组织相等的条件
		String daystatCond = "where daystat.pk_psndoc=tbm_leavebalance.pk_psndoc and daystat.calendar between "+dateScope[0]+" and "+dateScope[1];
		String sql = null;
		if(!isdaystatb)
			sql =  "isnull((select sum("+fieldCode+") from tbm_daystat daystat "+daystatCond+"),0)";
		else{
//			sql = "isnull((select sum(hournum) from tbm_daystatb daystatb where daystatb.pk_daystat in(select pk_daystat from " +
//				"tbm_daystat daystat "+daystatCond+")),0)";
			sql = "isnull((select sum(hournumusehour)/" + getUnitValue(pk_org,fieldCode) + " from tbm_daystatb daystatb where daystatb.pk_daystat in(select pk_daystat from " +
				"tbm_daystat daystat "+daystatCond+") and daystatb.pk_timeitem ='"+fieldCode+"' ),0)";
		}
		//若是入职日的当年累计，则需要特殊处理，因为计算日有可能比入职日要小，而sql的between函数是不管两个参数谁大谁小的，
		//在计算日比入职日还小的情况下，sql也能算出数来，这是不对的
		// ssx added on 2018-03-16
		// for changes of start date of company age
		if ((param.getTypeVO().getLeavesetperiod().intValue() == TimeItemCopyVO.LEAVESETPERIOD_DATE || param
				.getTypeVO().getLeavesetperiod().intValue() == TimeItemCopyVO.LEAVESETPERIOD_STARTDATE)
				//
				&& periodType == CUR_YEAR) {
			sql = "case when '" + param.getCalDate() + "' <tbm_leavebalance."
					+ LeaveBalanceVO.HIREBEGINDATE + " then 0 else " + sql
					+ " end ";
		}
		return sql;
	}
	
	private int getPeriodType(String periodType){
		if("CURPERIODSUM".equalsIgnoreCase(periodType))
			return CUR_PERIOD;
		if("PREVIOUSPERIODSUM".equalsIgnoreCase(periodType))
			return PREVIOUS_PERIOD;
		if("CURYEARSUM".equalsIgnoreCase(periodType))
			return CUR_YEAR;
		return PREVIOUS_YEAR;
	}
	
	/**
	 * 跨组织取数需要处理计量单位不一致的情况
	 * 获取目标组织休假数据汇总的除数，若计量单位为小时则返回1，若为天则返回考勤规则中工作日小时数
	 * @param pk_org
	 * @return
	 */
	private double getUnitValue(String pk_org,String pk_timeitem){
		if(StringUtils.isBlank(pk_org)||StringUtils.isBlank(pk_timeitem))
			return 1;
		String cond = TimeItemCopyVO.PK_TIMEITEM + " = '" + pk_timeitem + "' ";
		try {
			LeaveTypeCopyVO[] copyvos = NCLocator.getInstance().lookup(ITimeItemQueryMaintain.class).queryLeaveCopyTypesByOrg(pk_org, cond);
			if(ArrayUtils.isEmpty(copyvos))
				return 1;
			LeaveTypeCopyVO copyVo = copyvos[0];
			//计量单位为小时
			if(TimeItemCopyVO.TIMEITEMUNIT_HOUR == copyVo.getTimeItemUnit())
				return 1;
			//计量单位为天的
			TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
			return timeRuleVO.getDaytohour2();
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}
		return 1;
	}
}
