package nc.impl.hrsms.ta.shift;

import nc.bs.uif2.BusinessExceptionAdapter;
import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.hr.utils.ResHelper;
import nc.itf.ta.algorithm.IRelativeTime;
import nc.itf.ta.algorithm.IRelativeTimeScope;
import nc.itf.ta.algorithm.RelativeTimeScopeUtils;
import nc.itf.ta.algorithm.RelativeTimeUtils;
import nc.itf.ta.algorithm.impl.DefaultRelativeTimeScope;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.RTVO;
import nc.vo.bd.shift.ShiftTimeException;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 班次定义校验器
 * @author shaochj
 *
 */
@SuppressWarnings("serial")
public class StoreShiftValidator implements Validator {

	@Override
	public ValidationFailure validate(Object obj) {
		// TODO Auto-generated method stub
		if(obj == null)
			return null;

		AggShiftVO aggVo = (AggShiftVO)obj;
		try {
			validateAggShift(aggVo);
//		} catch(ValidationException e) {
//			throw new BusinessExceptionAdapter(e);
		} catch (BusinessException e) {
			throw new BusinessExceptionAdapter(e);
		}
		return null;
	}

	/**
	 * 验证班次定义信息
	 * @param aggVo
	 * @throws BusinessException
	 */
	private void validateAggShift(AggShiftVO aggVo) throws BusinessException{
		if(aggVo == null) {
			return;
		}
		ShiftVO vo = (ShiftVO)aggVo.getParentVO();

		//查询上下班相对时间
		IRelativeTimeScope workScope = vo.toRelativeWorkScope();
		//获取上班时间
		IRelativeTime wkStartTime = RelativeTimeScopeUtils.getStartTime(workScope);
		//获取下班时间
		IRelativeTime wkEndTime = RelativeTimeScopeUtils.getEndTime(workScope);

		//验证上下班时间
		wkTimeValidate(wkStartTime,wkEndTime,vo.getIsotflexible().booleanValue());
		//验证最晚上班时间和最早下班时间
//		flexWkTimeValidate(flexWkStartTime, flexWkEndTime);
		//验证考勤时间
		kqTimeValidate(vo,wkStartTime,wkEndTime);

		//验证休息时间段
		double rttime = restTimeValidate(aggVo,wkStartTime,wkEndTime);
		//验证上下班时间差必须大于工作时长与休息时长之和
		wtAndrtValidate(wkStartTime, wkEndTime, vo.getGzsj().doubleValue(), rttime);
		//验证夜班时间
		nightTimeValidate(vo);
		//迟到规则
		if(vo.getAllowlate() != null && vo.getLargelate() != null && vo.getAllowlate().doubleValue()>vo.getLargelate().doubleValue()) {
			throw new ValidationException(ResHelper.getString("hrbd","0hrbd0208")
/*@res "允许迟到时限不能大于最大迟到时限"*/);
		}
		//迟到规则
		if(vo.getAllowearly() != null && vo.getLargeearly() != null && vo.getAllowearly().doubleValue()>vo.getLargeearly().doubleValue()) {
			throw new ValidationException(ResHelper.getString("hrbd","0hrbd0209")
/*@res "允许早退时限不能大于最大早退时限"*/);
		}
		//验证加班规则
		//延时加班规则
		if(vo.getUseovertmrule().booleanValue()) {
			if(vo.getOvertmbeyond().doubleValue() < vo.getOvertmbegin().doubleValue()) {
				throw new ValidationException(ResHelper.getString("hrbd","0hrbd0210")
/*@res "下班后开始计算加班的时间段不能超过计为加班的时间段"*/);
			}
		}
		//提前加班规则
		if(vo.getUseontmrule().booleanValue()) {
			if(vo.getOntmbeyond().doubleValue() < vo.getOntmend().doubleValue()) {
				throw new ValidationException(ResHelper.getString("hrbd","0hrbd0211")
/*@res "计算上班前加班截止时间段不能超过上班前计为加班的时间段"*/);
			}
		}
	}

	/**
	 * 验证夜班时间
	 * @param vo	：班次
	 */
	private void nightTimeValidate(ShiftVO vo) throws ValidationException	{
		//如果不是夜班或者夜班时间为空，则不判断
		if(!vo.getIncludenightshift().booleanValue() || StringUtils.isEmpty(vo.getNightbegintime()) || StringUtils.isEmpty(vo.getNightendtime()))
			return;
		IRelativeTimeScope nightTime = new DefaultRelativeTimeScope();
		nightTime.setScopeStartDate(vo.getNightbeginday());
		nightTime.setScopeStartTime(vo.getNightbegintime());
		nightTime.setScopeEndDate(vo.getNightendday());
		nightTime.setScopeEndTime(vo.getNightendtime());
		IRelativeTime nightBeginTime = RelativeTimeScopeUtils.getStartTime(nightTime);
		IRelativeTime nightEndTime = RelativeTimeScopeUtils.getEndTime(nightTime);
		if(nightBeginTime == null || nightEndTime == null)
			return;
		//夜班开始时间不能晚于夜班结束时间
		if(nightBeginTime.after(nightEndTime)) {
			throw new ShiftTimeException(ResHelper.getString("common","UC000-0001396")
/*@res "夜班开始时间"*/,ResHelper.getString("common","UC000-0001398")
/*@res "夜班结束时间"*/);
		}
		//查询考勤相对时间
		IRelativeTimeScope kqScope = vo.toRelativeKqScope();
		//获取考勤开始时间
		IRelativeTime getKqStartTime = RelativeTimeScopeUtils.getStartTime(kqScope);
		//获取考勤结束时间
		IRelativeTime getKqEndTime = RelativeTimeScopeUtils.getEndTime(kqScope);
		//夜班开始时间必须在刷卡开始时间之后，夜班结束时间必须在刷卡结束时间之前
		if(nightBeginTime.before(getKqStartTime) || nightEndTime.after(getKqEndTime)
//				|| (nightBeginTime.getDate()==getKqStartTime.getDate() && nightBeginTime.getTime().equals(getKqStartTime.getTime())
//					|| nightBeginTime.getDate()==getKqEndTime.getDate() && nightBeginTime.getTime().equals(getKqEndTime.getTime()))
				) {
			throw new ValidationException(ResHelper.getString("hrbd","0hrbd0212")
/*@res "夜班时段必须在刷卡时段内"*/);
		}
		//计算夜班时长
		UFDouble nightgzsj = new UFDouble(RelativeTimeScopeUtils.getLength(nightTime)/3600.0);
		vo.setNightgzsj(nightgzsj);
	}

	/**
	 * 校验上下班时间
	 * @param wkStartTime
	 * @param wkEndTime
	 * @param isotflexible
	 * @throws ValidationException
	 */
	private void wkTimeValidate(IRelativeTime wkStartTime,IRelativeTime wkEndTime,boolean isotflexible)
		throws ValidationException	{

		if(wkStartTime == null || wkEndTime == null) {
			return;
		}
		//上班时间不可以大于下班时间：最早上班时间不能晚于最晚下班时间的判断在前台panel中进行，因此
		//这里只进行上班时间和下班时间的判断
		String onworktime = ResHelper.getString("hrbd","2bdshift-000072")
/*@res "上班时间"*/;
		String offworktime = ResHelper.getString("hrbd","2bdshift-000007")
/*@res "下班时间"*/;
		if(isotflexible) {
			onworktime = ResHelper.getString("hrbd","0hrbd0052")
/*@res "最早上班时间"*/;
			offworktime = ResHelper.getString("hrbd","0hrbd0053")
/*@res "最晚下班时间"*/;
		}
		if(wkEndTime.before(wkStartTime) || (wkEndTime.getTime() != null && wkStartTime.getTime() != null
				&& wkEndTime.getDate()==wkStartTime.getDate() && wkEndTime.getTime().equals(wkStartTime.getTime()))) {
			throw new ShiftTimeException(onworktime,offworktime);
		}
	}

	/**
	 * 验证考勤时间
	 * 1、考勤开始时间不能超过结束时间
	 * 2、上班时间不能早于考勤开始时间
	 * 3、下班时间不能晚于考勤结束时间
	 * @param vo
	 * @param wkStartTime
	 * @param wkEndTime
	 * @throws ValidationException
	 */
	private void kqTimeValidate(ShiftVO vo,IRelativeTime wkStartTime,IRelativeTime wkEndTime)
		throws ValidationException {

		if(vo == null || wkStartTime == null || wkEndTime == null) {
			return;
		}

		//查询考勤相对时间
		IRelativeTimeScope kqScope = vo.toRelativeKqScope();
		//获取考勤开始时间
		IRelativeTime getKqStartTime = RelativeTimeScopeUtils.getStartTime(kqScope);
		//获取考勤结束时间
		IRelativeTime getKqEndTime = RelativeTimeScopeUtils.getEndTime(kqScope);

		//考勤开始时间不可以大于考勤结束时间
		if(getKqEndTime.before(getKqStartTime)) {
			throw new ShiftTimeException(ResHelper.getString("hrbd","0hrbd0048")
/*@res "刷卡开始时间"*/,ResHelper.getString("common","UC000-0003232")
/*@res "结束时间"*/);
		}


		//考勤开始时间不能超过上班时间
//		if(wkStartTime.before(getKqStartTime) || (wkStartTime.getDate()==getKqStartTime.getDate() &&
//				wkStartTime.getTime().equals(getKqStartTime.getTime()))) {
//			throw new ShiftTimeException("刷卡开始时间","上班时间");
//		}
		if(wkStartTime.before(getKqStartTime)) {
			throw new ShiftTimeException(ResHelper.getString("hrbd","0hrbd0048")
/*@res "刷卡开始时间"*/,ResHelper.getString("hrbd","2bdshift-000072")
/*@res "上班时间"*/);
		}

//		if(wkEndTime.after(getKqEndTime) || (wkEndTime.getDate()==getKqEndTime.getDate() &&
//				wkEndTime.getTime().equals(getKqEndTime.getTime()))) {
//			throw new ShiftTimeException("下班时间","刷卡结束时间");
//		}
		if(wkEndTime.after(getKqEndTime)) {
			throw new ShiftTimeException(ResHelper.getString("hrbd","2bdshift-000007")
/*@res "下班时间"*/,ResHelper.getString("hrbd","0hrbd0049")
/*@res "刷卡结束时间"*/);
		}
	}


	/**
	 * 验证休息时间段
	 * 1、开始时间不能大于结束时间
	 * 2、最早开始时间不能小于上班时间
	 * 3、最晚结束时间不能大于下班时间
	 * 4、休息时间段不能有交集
	 * @param aggVo
	 * @param wkStartTime : 上班相对时间
	 * @param wkEndTime：下班相对时间
	 * @return
	 */
	private double restTimeValidate(AggShiftVO aggVo,IRelativeTime wkStartTime,IRelativeTime wkEndTime)
		throws ValidationException {
		//查询休息时间段
		RTVO[] rstVos = (RTVO[])aggVo.getTableVO(AggShiftVO.RT_SUB);
		if(rstVos == null || rstVos.length <= 0)
			return 0;
		//休息开始时间
		IRelativeTime elRtStartTime = null;
		//休息结束时间
		IRelativeTime lRtEnedTime = null;
		//休息总时长
		double rtTime = 0;
		for(int i = 0; i < rstVos.length; i++) {
			RTVO rtVo = rstVos[i];
			if(StringUtils.isNotEmpty(rstVos[i].getBegintime())&&StringUtils.isNotEmpty(rstVos[i].getEndtime())){
				//获取休息开始时间
				IRelativeTime getRtStartTime = RelativeTimeScopeUtils.getStartTime(rtVo);
				//获取休息结束时间
				IRelativeTime getRtEndTime = RelativeTimeScopeUtils.getEndTime(rtVo);

				//获取最早开始时间
				if((elRtStartTime == null) || (elRtStartTime != null && getRtStartTime.before(elRtStartTime))) {
					elRtStartTime = getRtStartTime;
				}
				//获取最晚结束时间
				if((lRtEnedTime == null) || (lRtEnedTime != null && getRtEndTime.before(lRtEnedTime))) {
					lRtEnedTime = getRtEndTime;
				}
				//休息开始时间不可以大于休息结束时间
				if(getRtEndTime.before(getRtStartTime) || (getRtEndTime.getDate()==getRtStartTime.getDate() &&
						getRtEndTime.getTime().equals(getRtStartTime.getTime()))) {
					throw new ValidationException(ResHelper.getString("hrbd","0hrbd0213"/*@res "{0}行休息开始时间必须小于结束时间"*/,(rtVo.getTimeid()+1 + "")));
				}
				if(RelativeTimeUtils.getLengthInMinute(getRtStartTime, getRtEndTime) < rtVo.getResttime().longValue()) {
					throw new ValidationException(ResHelper.getString("hrbd","0hrbd0214"/*@res "{0}行休息时长不能大于工休开始时间与结束时间之差"*/,(rtVo.getTimeid()+1 + "")));
				}
				for(int j = rstVos.length-1; j >i; j--) {
					RTVO compareRtvo = rstVos[j];
					//获取休息开始时间
					if(StringUtils.isNotEmpty(compareRtvo.getBegintime())&&StringUtils.isNotEmpty(compareRtvo.getEndtime())){
						if(!ArrayUtils.isEmpty(RelativeTimeScopeUtils.intersectionRelativeTimeScopes(new RTVO[]{rtVo}, new RTVO[]{compareRtvo}))) {
							throw new ValidationException(ResHelper.getString("hrbd","0hrbd0215")/*@res "休息时间段不能有交集"*/);
						}
					}
				}
				if(rtVo.getResttime() != null) {
					rtTime += rtVo.getResttime().doubleValue();
				}
			}
		}

		//休息最早开始时间不能早于上班时间
		if(elRtStartTime != null && wkStartTime != null && (elRtStartTime.before(wkStartTime)
				|| (elRtStartTime.getDate()==wkStartTime.getDate() && elRtStartTime.getTime().equals(wkStartTime.getTime())))) {
			throw new ValidationException(ResHelper.getString("hrbd","0hrbd0216")/*@res "上班时间必须早于休息开始时间"*/);
		}
		//最晚休息结束时间不能晚于下班时间
		if(lRtEnedTime != null && wkEndTime != null && (wkEndTime.before(lRtEnedTime)
				|| wkEndTime.equals(lRtEnedTime))) {
			throw new ValidationException(ResHelper.getString("hrbd","0hrbd0217")
/*@res "下班时间必须晚于休息结束时间"*/);
		}
		return rtTime;
	}

	/**
	 * 验证上下班时间差必须大于工作时长与休息时长之和
	 * @param wkStartTime	:上班时间的相对时间
	 * @param wkEndTime		：下班时间的相对时间
	 * @param gzsc			：工作时间
	 * @param rttime		：休息时间
	 */
	private void wtAndrtValidate(IRelativeTime wkStartTime,IRelativeTime wkEndTime, double gzsc,double rttime)
		throws ValidationException {
		//如果上下班时间弹性
		long elst2ltstSec = RelativeTimeUtils.getLength(wkStartTime, wkEndTime);
		//工作时长控件中，如果时长为9.99995874则控件servalue时会设置为10（double限制），因此判断时
		//如果用10判断会有误差，因此判断时使工作时长减去0.001
		if(elst2ltstSec < ((gzsc- 0.01)*3600 + rttime*60) ) {
			throw new ValidationException(ResHelper.getString("hrbd","0hrbd0218")
/*@res "工作时长和休息时长之和不能大于上下班时间差"*/);
		}
	}

	
}
