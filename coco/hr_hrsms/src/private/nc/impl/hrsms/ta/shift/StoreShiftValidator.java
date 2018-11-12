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
 * ��ζ���У����
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
	 * ��֤��ζ�����Ϣ
	 * @param aggVo
	 * @throws BusinessException
	 */
	private void validateAggShift(AggShiftVO aggVo) throws BusinessException{
		if(aggVo == null) {
			return;
		}
		ShiftVO vo = (ShiftVO)aggVo.getParentVO();

		//��ѯ���°����ʱ��
		IRelativeTimeScope workScope = vo.toRelativeWorkScope();
		//��ȡ�ϰ�ʱ��
		IRelativeTime wkStartTime = RelativeTimeScopeUtils.getStartTime(workScope);
		//��ȡ�°�ʱ��
		IRelativeTime wkEndTime = RelativeTimeScopeUtils.getEndTime(workScope);

		//��֤���°�ʱ��
		wkTimeValidate(wkStartTime,wkEndTime,vo.getIsotflexible().booleanValue());
		//��֤�����ϰ�ʱ��������°�ʱ��
//		flexWkTimeValidate(flexWkStartTime, flexWkEndTime);
		//��֤����ʱ��
		kqTimeValidate(vo,wkStartTime,wkEndTime);

		//��֤��Ϣʱ���
		double rttime = restTimeValidate(aggVo,wkStartTime,wkEndTime);
		//��֤���°�ʱ��������ڹ���ʱ������Ϣʱ��֮��
		wtAndrtValidate(wkStartTime, wkEndTime, vo.getGzsj().doubleValue(), rttime);
		//��֤ҹ��ʱ��
		nightTimeValidate(vo);
		//�ٵ�����
		if(vo.getAllowlate() != null && vo.getLargelate() != null && vo.getAllowlate().doubleValue()>vo.getLargelate().doubleValue()) {
			throw new ValidationException(ResHelper.getString("hrbd","0hrbd0208")
/*@res "����ٵ�ʱ�޲��ܴ������ٵ�ʱ��"*/);
		}
		//�ٵ�����
		if(vo.getAllowearly() != null && vo.getLargeearly() != null && vo.getAllowearly().doubleValue()>vo.getLargeearly().doubleValue()) {
			throw new ValidationException(ResHelper.getString("hrbd","0hrbd0209")
/*@res "��������ʱ�޲��ܴ����������ʱ��"*/);
		}
		//��֤�Ӱ����
		//��ʱ�Ӱ����
		if(vo.getUseovertmrule().booleanValue()) {
			if(vo.getOvertmbeyond().doubleValue() < vo.getOvertmbegin().doubleValue()) {
				throw new ValidationException(ResHelper.getString("hrbd","0hrbd0210")
/*@res "�°��ʼ����Ӱ��ʱ��β��ܳ�����Ϊ�Ӱ��ʱ���"*/);
			}
		}
		//��ǰ�Ӱ����
		if(vo.getUseontmrule().booleanValue()) {
			if(vo.getOntmbeyond().doubleValue() < vo.getOntmend().doubleValue()) {
				throw new ValidationException(ResHelper.getString("hrbd","0hrbd0211")
/*@res "�����ϰ�ǰ�Ӱ��ֹʱ��β��ܳ����ϰ�ǰ��Ϊ�Ӱ��ʱ���"*/);
			}
		}
	}

	/**
	 * ��֤ҹ��ʱ��
	 * @param vo	�����
	 */
	private void nightTimeValidate(ShiftVO vo) throws ValidationException	{
		//�������ҹ�����ҹ��ʱ��Ϊ�գ����ж�
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
		//ҹ�࿪ʼʱ�䲻������ҹ�����ʱ��
		if(nightBeginTime.after(nightEndTime)) {
			throw new ShiftTimeException(ResHelper.getString("common","UC000-0001396")
/*@res "ҹ�࿪ʼʱ��"*/,ResHelper.getString("common","UC000-0001398")
/*@res "ҹ�����ʱ��"*/);
		}
		//��ѯ�������ʱ��
		IRelativeTimeScope kqScope = vo.toRelativeKqScope();
		//��ȡ���ڿ�ʼʱ��
		IRelativeTime getKqStartTime = RelativeTimeScopeUtils.getStartTime(kqScope);
		//��ȡ���ڽ���ʱ��
		IRelativeTime getKqEndTime = RelativeTimeScopeUtils.getEndTime(kqScope);
		//ҹ�࿪ʼʱ�������ˢ����ʼʱ��֮��ҹ�����ʱ�������ˢ������ʱ��֮ǰ
		if(nightBeginTime.before(getKqStartTime) || nightEndTime.after(getKqEndTime)
//				|| (nightBeginTime.getDate()==getKqStartTime.getDate() && nightBeginTime.getTime().equals(getKqStartTime.getTime())
//					|| nightBeginTime.getDate()==getKqEndTime.getDate() && nightBeginTime.getTime().equals(getKqEndTime.getTime()))
				) {
			throw new ValidationException(ResHelper.getString("hrbd","0hrbd0212")
/*@res "ҹ��ʱ�α�����ˢ��ʱ����"*/);
		}
		//����ҹ��ʱ��
		UFDouble nightgzsj = new UFDouble(RelativeTimeScopeUtils.getLength(nightTime)/3600.0);
		vo.setNightgzsj(nightgzsj);
	}

	/**
	 * У�����°�ʱ��
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
		//�ϰ�ʱ�䲻���Դ����°�ʱ�䣺�����ϰ�ʱ�䲻�����������°�ʱ����ж���ǰ̨panel�н��У����
		//����ֻ�����ϰ�ʱ����°�ʱ����ж�
		String onworktime = ResHelper.getString("hrbd","2bdshift-000072")
/*@res "�ϰ�ʱ��"*/;
		String offworktime = ResHelper.getString("hrbd","2bdshift-000007")
/*@res "�°�ʱ��"*/;
		if(isotflexible) {
			onworktime = ResHelper.getString("hrbd","0hrbd0052")
/*@res "�����ϰ�ʱ��"*/;
			offworktime = ResHelper.getString("hrbd","0hrbd0053")
/*@res "�����°�ʱ��"*/;
		}
		if(wkEndTime.before(wkStartTime) || (wkEndTime.getTime() != null && wkStartTime.getTime() != null
				&& wkEndTime.getDate()==wkStartTime.getDate() && wkEndTime.getTime().equals(wkStartTime.getTime()))) {
			throw new ShiftTimeException(onworktime,offworktime);
		}
	}

	/**
	 * ��֤����ʱ��
	 * 1�����ڿ�ʼʱ�䲻�ܳ�������ʱ��
	 * 2���ϰ�ʱ�䲻�����ڿ��ڿ�ʼʱ��
	 * 3���°�ʱ�䲻�����ڿ��ڽ���ʱ��
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

		//��ѯ�������ʱ��
		IRelativeTimeScope kqScope = vo.toRelativeKqScope();
		//��ȡ���ڿ�ʼʱ��
		IRelativeTime getKqStartTime = RelativeTimeScopeUtils.getStartTime(kqScope);
		//��ȡ���ڽ���ʱ��
		IRelativeTime getKqEndTime = RelativeTimeScopeUtils.getEndTime(kqScope);

		//���ڿ�ʼʱ�䲻���Դ��ڿ��ڽ���ʱ��
		if(getKqEndTime.before(getKqStartTime)) {
			throw new ShiftTimeException(ResHelper.getString("hrbd","0hrbd0048")
/*@res "ˢ����ʼʱ��"*/,ResHelper.getString("common","UC000-0003232")
/*@res "����ʱ��"*/);
		}


		//���ڿ�ʼʱ�䲻�ܳ����ϰ�ʱ��
//		if(wkStartTime.before(getKqStartTime) || (wkStartTime.getDate()==getKqStartTime.getDate() &&
//				wkStartTime.getTime().equals(getKqStartTime.getTime()))) {
//			throw new ShiftTimeException("ˢ����ʼʱ��","�ϰ�ʱ��");
//		}
		if(wkStartTime.before(getKqStartTime)) {
			throw new ShiftTimeException(ResHelper.getString("hrbd","0hrbd0048")
/*@res "ˢ����ʼʱ��"*/,ResHelper.getString("hrbd","2bdshift-000072")
/*@res "�ϰ�ʱ��"*/);
		}

//		if(wkEndTime.after(getKqEndTime) || (wkEndTime.getDate()==getKqEndTime.getDate() &&
//				wkEndTime.getTime().equals(getKqEndTime.getTime()))) {
//			throw new ShiftTimeException("�°�ʱ��","ˢ������ʱ��");
//		}
		if(wkEndTime.after(getKqEndTime)) {
			throw new ShiftTimeException(ResHelper.getString("hrbd","2bdshift-000007")
/*@res "�°�ʱ��"*/,ResHelper.getString("hrbd","0hrbd0049")
/*@res "ˢ������ʱ��"*/);
		}
	}


	/**
	 * ��֤��Ϣʱ���
	 * 1����ʼʱ�䲻�ܴ��ڽ���ʱ��
	 * 2�����翪ʼʱ�䲻��С���ϰ�ʱ��
	 * 3���������ʱ�䲻�ܴ����°�ʱ��
	 * 4����Ϣʱ��β����н���
	 * @param aggVo
	 * @param wkStartTime : �ϰ����ʱ��
	 * @param wkEndTime���°����ʱ��
	 * @return
	 */
	private double restTimeValidate(AggShiftVO aggVo,IRelativeTime wkStartTime,IRelativeTime wkEndTime)
		throws ValidationException {
		//��ѯ��Ϣʱ���
		RTVO[] rstVos = (RTVO[])aggVo.getTableVO(AggShiftVO.RT_SUB);
		if(rstVos == null || rstVos.length <= 0)
			return 0;
		//��Ϣ��ʼʱ��
		IRelativeTime elRtStartTime = null;
		//��Ϣ����ʱ��
		IRelativeTime lRtEnedTime = null;
		//��Ϣ��ʱ��
		double rtTime = 0;
		for(int i = 0; i < rstVos.length; i++) {
			RTVO rtVo = rstVos[i];
			if(StringUtils.isNotEmpty(rstVos[i].getBegintime())&&StringUtils.isNotEmpty(rstVos[i].getEndtime())){
				//��ȡ��Ϣ��ʼʱ��
				IRelativeTime getRtStartTime = RelativeTimeScopeUtils.getStartTime(rtVo);
				//��ȡ��Ϣ����ʱ��
				IRelativeTime getRtEndTime = RelativeTimeScopeUtils.getEndTime(rtVo);

				//��ȡ���翪ʼʱ��
				if((elRtStartTime == null) || (elRtStartTime != null && getRtStartTime.before(elRtStartTime))) {
					elRtStartTime = getRtStartTime;
				}
				//��ȡ�������ʱ��
				if((lRtEnedTime == null) || (lRtEnedTime != null && getRtEndTime.before(lRtEnedTime))) {
					lRtEnedTime = getRtEndTime;
				}
				//��Ϣ��ʼʱ�䲻���Դ�����Ϣ����ʱ��
				if(getRtEndTime.before(getRtStartTime) || (getRtEndTime.getDate()==getRtStartTime.getDate() &&
						getRtEndTime.getTime().equals(getRtStartTime.getTime()))) {
					throw new ValidationException(ResHelper.getString("hrbd","0hrbd0213"/*@res "{0}����Ϣ��ʼʱ�����С�ڽ���ʱ��"*/,(rtVo.getTimeid()+1 + "")));
				}
				if(RelativeTimeUtils.getLengthInMinute(getRtStartTime, getRtEndTime) < rtVo.getResttime().longValue()) {
					throw new ValidationException(ResHelper.getString("hrbd","0hrbd0214"/*@res "{0}����Ϣʱ�����ܴ��ڹ��ݿ�ʼʱ�������ʱ��֮��"*/,(rtVo.getTimeid()+1 + "")));
				}
				for(int j = rstVos.length-1; j >i; j--) {
					RTVO compareRtvo = rstVos[j];
					//��ȡ��Ϣ��ʼʱ��
					if(StringUtils.isNotEmpty(compareRtvo.getBegintime())&&StringUtils.isNotEmpty(compareRtvo.getEndtime())){
						if(!ArrayUtils.isEmpty(RelativeTimeScopeUtils.intersectionRelativeTimeScopes(new RTVO[]{rtVo}, new RTVO[]{compareRtvo}))) {
							throw new ValidationException(ResHelper.getString("hrbd","0hrbd0215")/*@res "��Ϣʱ��β����н���"*/);
						}
					}
				}
				if(rtVo.getResttime() != null) {
					rtTime += rtVo.getResttime().doubleValue();
				}
			}
		}

		//��Ϣ���翪ʼʱ�䲻�������ϰ�ʱ��
		if(elRtStartTime != null && wkStartTime != null && (elRtStartTime.before(wkStartTime)
				|| (elRtStartTime.getDate()==wkStartTime.getDate() && elRtStartTime.getTime().equals(wkStartTime.getTime())))) {
			throw new ValidationException(ResHelper.getString("hrbd","0hrbd0216")/*@res "�ϰ�ʱ�����������Ϣ��ʼʱ��"*/);
		}
		//������Ϣ����ʱ�䲻�������°�ʱ��
		if(lRtEnedTime != null && wkEndTime != null && (wkEndTime.before(lRtEnedTime)
				|| wkEndTime.equals(lRtEnedTime))) {
			throw new ValidationException(ResHelper.getString("hrbd","0hrbd0217")
/*@res "�°�ʱ�����������Ϣ����ʱ��"*/);
		}
		return rtTime;
	}

	/**
	 * ��֤���°�ʱ��������ڹ���ʱ������Ϣʱ��֮��
	 * @param wkStartTime	:�ϰ�ʱ������ʱ��
	 * @param wkEndTime		���°�ʱ������ʱ��
	 * @param gzsc			������ʱ��
	 * @param rttime		����Ϣʱ��
	 */
	private void wtAndrtValidate(IRelativeTime wkStartTime,IRelativeTime wkEndTime, double gzsc,double rttime)
		throws ValidationException {
		//������°�ʱ�䵯��
		long elst2ltstSec = RelativeTimeUtils.getLength(wkStartTime, wkEndTime);
		//����ʱ���ؼ��У����ʱ��Ϊ9.99995874��ؼ�servalueʱ������Ϊ10��double���ƣ�������ж�ʱ
		//�����10�жϻ���������ж�ʱʹ����ʱ����ȥ0.001
		if(elst2ltstSec < ((gzsc- 0.01)*3600 + rttime*60) ) {
			throw new ValidationException(ResHelper.getString("hrbd","0hrbd0218")
/*@res "����ʱ������Ϣʱ��֮�Ͳ��ܴ������°�ʱ���"*/);
		}
	}

	
}
