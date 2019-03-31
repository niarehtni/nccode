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
 * �ݼٹ���Ĺ�ʽ�У��Ե����ۼƣ����ڻ��ܣ������ۼƣ�������ܵĴ���
 * �����ۼơ����ڻ��ܵĽ��ͣ������������
 * ���ڼ�������𣬵��ھ��Ǽ���ʱָ����year+month��Ӧ���ڼ䣬���ڼ䲻���ڣ�����Ϊ0.�ۼƵ����ڷ�ΧΪ�ڼ��һ�쵽min(�ڼ����һ��,������)
 * 					���ڻ��ܵ����ھ�������ȷ���ĵ��ڵ����ڣ����ܵ����ڷ�ΧΪ���ڵ�һ�쵽���һ��
 * ����������𣬵��ھ���min(������һ�������ڼ䣬�����������ڼ�)���������ڼ�����һ��Ϊ�գ�����Ϊ0
 * 				     ���ھ���min(����ȵ����ڶ����ڼ䣬�����������ڼ������)���������ڼ�����һ��Ϊ�գ�����Ϊ0
 * ����ְ�ս������𣬵��ھ��Ǽ����������ڼ䣬���ھ��Ǽ����������ڼ������
 * 
 * �����ۼơ�������ܵĽ��ͣ�Ҳ�����������
 * ���ڼ�������𣬵�����Ǽ���ʱָ����year��Ӧ�Ŀ�����ȣ��������ڣ�����Ϊ0.�ۼƵķ�Χ����ȵ�һ�쵽min(������һ��,������)
 * 					������ܵ������������ȷ���Ŀ�����ȵ���һ�꣬�������ڣ�����Ϊ0
 * ������������밴�ڼ����һ��
 * ����ְ��������𣬵������year��Ӧ����ְ�꣬�ۼƵķ�Χ����ְ���һ�쵽min(��ְ�����һ��,������)
 * 					  �������year��Ӧ����һ����ְ��
 * 
 * ��ʵ�������ۼơ����ڻ��ܶ��ڰ��ꡢ��ְ�ս�������Ӧ�����������
 * @author zengcheng
 *
 */
public class DayStatDataVarParser_OverOrg extends AbstractVarParser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 335713535227580392L;
	
	private static final int CUR_PERIOD=1;//����
	private static final int PREVIOUS_PERIOD=2;//����
	private static final int CUR_YEAR=3;//����
	private static final int PREVIOUS_YEAR=4;//����
	
	public DayStatDataVarParser_OverOrg(){
		setVarName("DAYSTATDATA_OVERORG");
	}
	
	private String[] getSumBeginEndDate(int periodType,LeaveFormulaCalParam param){
		LeaveTypeCopyVO typeVO = param.getTypeVO();
		int settlePeriod = typeVO.getLeavesetperiod().intValue();//��������
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
	
	private String LEASTFUNC(){//LEAST���������֡�oracleԤ����least������sqlserver��db2û�У���Ҫ�����û�����HR_LEAST.�ڴ���HRϵͳ��ʱ������
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
		boolean isdaystatb = CommonMethods.isPrimaryKey(fieldCode);//�����20λ����������Ҫ���ձ��ӱ���ȡ��
//		String daystatCond = "where daystat.pk_org=tbm_leavebalance.pk_org " +
//					"and daystat.pk_psndoc=tbm_leavebalance.pk_psndoc and daystat.calendar between "+
//					dateScope[0]+" and "+dateScope[1];
		//��Ҫ����֯ȡ����ȥ����֯��ȵ�����
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
		//������ְ�յĵ����ۼƣ�����Ҫ���⴦����Ϊ�������п��ܱ���ְ��ҪС����sql��between�����ǲ�����������˭��˭С�ģ�
		//�ڼ����ձ���ְ�ջ�С������£�sqlҲ��������������ǲ��Ե�
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
	 * ����֯ȡ����Ҫ���������λ��һ�µ����
	 * ��ȡĿ����֯�ݼ����ݻ��ܵĳ�������������λΪСʱ�򷵻�1����Ϊ���򷵻ؿ��ڹ����й�����Сʱ��
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
			//������λΪСʱ
			if(TimeItemCopyVO.TIMEITEMUNIT_HOUR == copyVo.getTimeItemUnit())
				return 1;
			//������λΪ���
			TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
			return timeRuleVO.getDaytohour2();
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}
		return 1;
	}
}
