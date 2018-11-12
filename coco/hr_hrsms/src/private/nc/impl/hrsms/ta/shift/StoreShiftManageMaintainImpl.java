package nc.impl.hrsms.ta.shift;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nc.bs.bd.baseservice.BaseService;
import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.businessevent.IEventType;
import nc.bs.businessevent.bd.BDCommonEvent;
import nc.bs.dao.BaseDAO;
import nc.hr.utils.ResHelper;
import nc.itf.hrsms.ta.shift.IStoreShiftManageMaintain;
import nc.itf.ta.algorithm.IRelativeCompleteCheckTimeScope;
import nc.itf.ta.algorithm.IRelativeTime;
import nc.itf.ta.algorithm.IRelativeTimeScope;
import nc.itf.ta.algorithm.RelativeCompleteCheckTimeScopeUtils;
import nc.itf.ta.algorithm.RelativeTimeScopeUtils;
import nc.itf.ta.algorithm.RelativeTimeUtils;
import nc.itf.ta.algorithm.impl.DefaultRelativeTime;
import nc.itf.ta.algorithm.impl.DefaultRelativeTimeScope;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.CapRTVO;
import nc.vo.bd.shift.CapWTVO;
import nc.vo.bd.shift.RTVO;
import nc.vo.bd.shift.ShiftMutexBUVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.bd.shift.WTVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.util.AuditInfoUtil;
import nc.vo.util.BDPKLockUtil;
import nc.vo.util.BDVersionValidationUtil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("deprecation")
public class StoreShiftManageMaintainImpl extends BaseService<ShiftVO> implements IStoreShiftManageMaintain{

	BaseDAO dao = new BaseDAO();
	
	StoreShiftQueryMaintainImpl shiftMaintain = null;
	public StoreShiftQueryMaintainImpl getShiftMaintain(){
		if(shiftMaintain == null)
			shiftMaintain = new StoreShiftQueryMaintainImpl();
		return shiftMaintain;
	}
	
	public static final String DOC_NAME = "8cc504ed-4371-43c6-81a0-8758684b436f";

	public StoreShiftManageMaintainImpl(){
		super(DOC_NAME);
	}
	
	@Override
	public AggShiftVO insert(AggShiftVO vo) throws BusinessException {
		// TODO Auto-generated method stub
		BDPKLockUtil.lockAggVO(vo);
		//У����Ψһ��
		StoreShiftUniqueValidator unval = new StoreShiftUniqueValidator();
		unval.validate(vo);
		//У���ζ����еĹ���
		StoreShiftValidator val = new StoreShiftValidator();
		val.validate(vo);
		//����ҹ��ʱ��
		vo.getShiftVO().setNightgzsj(computeNightgzsj(vo.getShiftVO()));
		
		//������Ϣʱ���ȡ����ʱ�䣬��������ɾ��ԭ����ʱ��
		setWorkTimes(vo);
		//���ݲ�����Ϣʱ���ȡ���ܹ���ʱ�䣬��������ɾ��ԭ���ܹ���ʱ��
		//setCapWorkTimes(vo);
		// ��������Ϣ
		vo.getShiftVO().setStatus(VOStatus.NEW);
		AuditInfoUtil.addData(vo.getShiftVO());
		//������
		//����ǰ�¼�
		EventDispatcher.fireEvent(new BusinessEvent(DOC_NAME, IEventType.TYPE_INSERT_BEFORE, vo));
		String pk = getMDService().saveBill(vo);
		//�������¼�
		EventDispatcher.fireEvent(new BusinessEvent(DOC_NAME, IEventType.TYPE_INSERT_AFTER, vo));
		AggShiftVO returnvo = (AggShiftVO) getMDQueryService().queryBillOfVOByPK(AggShiftVO.class, pk, false);
		return returnvo;
		//return getServiceTemplate().insert(vo);
		
		/*//����ǰ�¼�
//		EventDispatcher.fireEvent(new BusinessEvent(DOC_NAME, IEventType.TYPE_INSERT_BEFORE, vo));
		String pk = getMDService().saveBill(vo);
		//�������¼�
//		����EventDispatcher.fireEvent(new BusinessEvent(DOC_NAME, IEventType.TYPE_INSERT_AFTER, vo));

		AggShiftVO returnvo = (AggShiftVO) getMDQueryService().queryBillOfVOByPK(AggShiftVO.class, pk, false);
		return returnvo;*/
	}

	@Override
	public AggShiftVO update(AggShiftVO vo) throws BusinessException {
		// TODO Auto-generated method stub
		//return getServiceTemplate().update(vo,true);
		//����
		BDPKLockUtil.lockAggVO(vo);
	
		ShiftVO mainvo = (ShiftVO) vo.getParentVO();
		AggShiftVO dbAggShiftVO = getShiftMaintain().queryByPk(mainvo.getPk_shift());
		ShiftVO dbShiftVO = (ShiftVO)dbAggShiftVO.getParentVO();
		
		BDVersionValidationUtil.validateSuperVO(mainvo);
		//�����Ĭ�ϰ�Σ����ε�ʱ�䲻���г�ͻ
		if(dbShiftVO.getDefaultflag() != null && dbShiftVO.getDefaultflag().booleanValue()) {
			checkMutex(mainvo, mainvo);
		}
		
		vo.getParentVO().setStatus(VOStatus.UPDATED);
		//У����Ψһ��
		StoreShiftUniqueValidator unval = new StoreShiftUniqueValidator();
		unval.validate(vo);
		//У���ζ����еĹ���
		StoreShiftValidator val = new StoreShiftValidator();
		val.validate(vo);
		
		// ��������Ϣ
		AuditInfoUtil.updateData(mainvo);
		//����ҹ��ʱ��
		mainvo.setNightgzsj(computeNightgzsj(mainvo));	
		
		// �����¼�  �鿴ʹ�øð�εĹ���������Ӧ�����ڼ��Ƿ��Ѿ���棬�����棬�������޸�
		BDCommonEvent beforEvent = new BDCommonEvent(DOC_NAME, IEventType.TYPE_UPDATE_BEFORE,vo);
		EventDispatcher.fireEvent(beforEvent);

		//�޸İ��ʱ��ɾ��ԭ����ʱ�䡢��Ϣʱ�䣬�������µĹ���ʱ��
		if(checkChgWrTime(vo)) {
			deleteOrgnWts(vo,RTVO.class);
			setWorkTimes(vo);
		}
		deleteOrgnCapWts(vo, CapWTVO.class);
		deleteOrgnWts(vo,CapRTVO.class);
//		setCapWorkTimes(vo);
		//ɾ����Ϣʱ�����������Ȼ�����ݿ��Ѿ�ɾ��������ҳ�洫����RTVO�Դ���������
		clearRtPk(vo);
		clearCapRtPk(vo);
		
		String pk = this.getMDService().saveBill(vo);
		AggShiftVO returnvo = (AggShiftVO) getMDQueryService().queryBillOfVOByPK(AggShiftVO.class, pk, false);
		//�鿴��ǰ��κ����ݿ����иð�ε�ˢ��ʱ���Ƿ���ͬ
		if(!checkKqTime(mainvo, dbShiftVO)) {
			//��ɾ��ԭ��ͻ��Ϣ
			deleteOrigMutex(returnvo);
			chkAndSaveMutex(returnvo);
		}
		//�����޸ĺ��¼� �����Խ��и��¹��������ȣ�
		BDCommonEvent afterEvent = new BDCommonEvent(DOC_NAME, IEventType.TYPE_UPDATE_AFTER,
					new Object[] { dbAggShiftVO }, new Object[] { returnvo });
		EventDispatcher.fireEvent(afterEvent);
		return returnvo;
	}
	private IMDPersistenceService getMDService() {
		return MDPersistenceService.lookupPersistenceService();
	}
	private static IMDPersistenceQueryService getMDQueryService() {
		return MDPersistenceService.lookupPersistenceQueryService();
	}

	@Override
	public void delete(AggShiftVO vo) throws BusinessException {
		// TODO Auto-generated method stub
		//getServiceTemplate().delete(vo);
		//�����¼�      ���Խ��� ���� ɾ����֮��Ӧ�Ĺ��������Ȳ���
		EventDispatcher.fireEvent(new BusinessEvent(DOC_NAME, IEventType.TYPE_DELETE_BEFORE,vo));
		//���ݿ�ɾ��
		this.getMDService().deleteBillFromDB(vo);
		//ɾ�����¼�
		EventDispatcher.fireEvent(new BusinessEvent(DOC_NAME, IEventType.TYPE_DELETE_AFTER,vo));

	}

	@Override
	public AggShiftVO disable(AggShiftVO vo) throws BusinessException {
		// TODO Auto-generated method stub
		ShiftVO mainVo = (ShiftVO)vo.getParentVO();
		if(mainVo.getDefaultflag() != null && mainVo.getDefaultflag().booleanValue()) {
			throw new BusinessException(ResHelper.getString("hrbd","0hrbd0199")/*@res "Ĭ�ϰ�β���ͣ��"*/);
		}
		mainVo = disableVOWithoutFilter(mainVo)[0];
		vo.setParentVO(mainVo);
		return vo;
		
	}

	@Override
	public AggShiftVO enable(AggShiftVO vo) throws BusinessException {
		// TODO Auto-generated method stub
		
		ShiftVO mainVo = (ShiftVO)vo.getParentVO();
		mainVo =  enableWithoutFilter(mainVo)[0];
		vo.setParentVO(mainVo);
		return vo;
	}

	/**
	 * ����ҹ��Ĺ���ʱ��
	 * @param vo
	 * @return
	 */
	private UFDouble computeNightgzsj(ShiftVO vo){

		if(!vo.getIncludenightshift().booleanValue()){
			return new UFDouble(0);
		}
		if(!StringUtils.isEmpty(vo.getNightbegintime()) && !StringUtils.isEmpty(vo.getNightendtime())){
			IRelativeTimeScope nightTime = new DefaultRelativeTimeScope();
			nightTime.setScopeStartDate(vo.getNightbeginday());
			nightTime.setScopeStartTime(vo.getNightbegintime());
			nightTime.setScopeEndDate(vo.getNightendday());
			nightTime.setScopeEndTime(vo.getNightendtime());
			UFDouble nightgzsj = new UFDouble(RelativeTimeScopeUtils.getLength(nightTime)/3600.0);
			return nightgzsj;
		}
		return null;
	}
	
	/**
	 * ������Ϣʱ���ȡ����ʱ�䣬��������ɾ��ԭ����ʱ��
	 * @param aggVo
	 */
	private void setWorkTimes(AggShiftVO aggVo) throws BusinessException{

		if(aggVo ==null) {
			return;
		}
		//��ɾ�����ݿ��еĹ���ʱ��
		deleteOrgnWts(aggVo,WTVO.class);
		ShiftVO vo = (ShiftVO)aggVo.getParentVO();

		//Ŀǰ������°�����ʱ���κ�һ��Ϊ�գ��򲻽����κβ���
		if(vo.getBeginday() == null || vo.getBegintime() == null
			|| vo.getEndday() == null || vo.getEndtime() == null) {
			return;
		}

		//��ǰ��Ϣʱ��
		RTVO[] rtVos = (RTVO[])aggVo.getTableVO(AggShiftVO.RT_SUB);
		WTVO[] wtVos = getWtVos(rtVos, vo);

		aggVo.setTableVO(AggShiftVO.WT_SUB, wtVos);
	}
	
	/**
	 * �������°�ʱ�����Ϣʱ���ȡ����ʱ���б�
	 * a)	������ϰ�ʱ�䵯�ԣ���̻����ϰ�ʱ����°�ʱ��Ϊ��
	 * 		�����ϰ�������ϰ���м��Ϊ�ʼ���ϰ�ʱ�䣻�����°�������°���м��Ϊ����ʱ��������°�ʱ��
	 * b)	�������Ϣʱ�䵯�ԣ���ÿһ����
	 * 			��Ϣ��ʼʱ��Ϊ��������Ϣ����ʱ��-������Ϣ��ʼʱ�䣩/2-��Ϣʱ��/2��
	 * 			��Ϣ����ʱ��Ϊ��������Ϣ����ʱ��-������Ϣ��ʼʱ�䣩/2+��Ϣʱ��/2��
	 * @param rtVos �� ��Ϣʱ��
	 * @param mainVo
	 * @return
	 * @throws BusinessException
	 */
	private WTVO[] getWtVos(RTVO[] rtVos,ShiftVO mainVo)
		throws BusinessException {

		//���°�ʱ������ʱ����
		IRelativeTimeScope[] wtTimeScopes = null;
		//��Ϣʱ������ʱ����
		IRelativeTimeScope[] rtTimeScopes = null;
		//�̶��ࣺ���ղ�Ϊ����
		if(!mainVo.getIsflexiblefinal().booleanValue()) {
			wtTimeScopes = new DefaultRelativeTimeScope[]{(DefaultRelativeTimeScope)mainVo.toRelativeWorkScope()};
			//������Ϣʱ����
//			setSldRtScope(rtVos);
			rtTimeScopes = rtVos;
		//����ʱ�䵯�ԣ��ϰ�ʱ���ֶ�����Ϊ����
		} else if(mainVo.getIsotflexible().booleanValue()) {
			//�̻�����ʱ��
			wtTimeScopes = new DefaultRelativeTimeScope[]{(DefaultRelativeTimeScope)getOtflexScope(mainVo)};
			rtTimeScopes = rtVos;
//			setSldRtScope(rtVos);
		//��Ϣʱ�䵯��
		} else {
			//�̻���Ϣʱ��
			wtTimeScopes = new DefaultRelativeTimeScope[]{(DefaultRelativeTimeScope)mainVo.toRelativeWorkScope()};
			rtTimeScopes =  getRtFlexScope(mainVo, rtVos);
		}
		//���û����Ϣʱ�䣬����ʱ����һ����¼
		//�������Ϣʱ�䣬����ʱ�����Ϣʱ�䳤�ȶ�1.
		int rtLength = 1;
		if(rtVos != null) {
			rtLength = rtVos.length + 1;
		}
		//�õ���ζ���ʱ��
		IRelativeTimeScope[] wtScopes = RelativeTimeScopeUtils.minusRelativeTimeScopes(wtTimeScopes, rtTimeScopes);
		WTVO[] wtVos = new WTVO[rtLength];

		List<WTVO> wtVoList = new ArrayList<WTVO>();
		for (int i = 0; i < wtScopes.length; i++) {
			IRelativeTimeScope wtScope = wtScopes[i];
			WTVO wtVo = new WTVO();
			wtVo.setStatus(VOStatus.NEW);
			wtVo.setPk_group(mainVo.getPk_group());
			wtVo.setPk_org(mainVo.getPk_org());
			wtVo.setWtbeginday(wtScope.getScopeStartDate());
			wtVo.setKssj(wtScope.getScopeStartTime());
			wtVo.setWtendday(wtScope.getScopeEndDate());
			wtVo.setJssj(wtScope.getScopeEndTime());
			wtVo.setScopeStartDate(wtScope.getScopeStartDate());
			wtVo.setScopeStartTime(wtScope.getScopeStartTime());
			wtVo.setScopeEndDate(wtScope.getScopeEndDate());
			wtVo.setScopeEndTime(wtScope.getScopeEndTime());
			wtVo.setContainsLastSecond(false);
			wtVo.setTimeid(i);
			//���繤����ʼʱ��һ����
			//����������ʱ��һ����
			//�����Ϣʱ��򿨣������Ϣʱ���ǰһ������ʱ��εĽ���ʱ����Ҫ��
			//����Ϣʱ�����һ������ʱ��εĿ�ʼʱ����Ҫ��
			wtVo.setCheckinflag(i==0?UFBoolean.TRUE:rtVos[i-1].getCheckflag());
			wtVo.setCheckoutflag(i==wtScopes.length-1?UFBoolean.TRUE:rtVos[i].getCheckflag());
			wtVoList.add(wtVo);
		}
		if(wtVoList.size() <= 0) {
			return null;
		}

		wtVos = wtVoList.toArray(new WTVO[0]);

		return saveWtskTime(rtVos, mainVo, wtVos);
	}
	
	/**
	 * ���ݹ���ʱ�����ˢ��ʱ���
	 * @param rtVos
	 * @param mainVo
	 * @param wtVos
	 * @return
	 */
	private WTVO[] saveWtskTime(RTVO[] rtVos,ShiftVO mainVo,WTVO[] wtVos) {
		//ˢ����ʼ���ʱ��
		IRelativeTime timeStartTime = new DefaultRelativeTime();
		timeStartTime.setDate(mainVo.getTimebeginday());
		timeStartTime.setTime(mainVo.getTimebegintime());
		//ˢ���������ʱ��
		IRelativeTime timeEndTime = new DefaultRelativeTime();
		timeEndTime.setDate(mainVo.getTimeendday());
		timeEndTime.setTime(mainVo.getTimeendtime());

		//�õ�ˢ��ʱ���
		IRelativeCompleteCheckTimeScope[] checkTimeScope = RelativeCompleteCheckTimeScopeUtils.mergeWorkTime(timeStartTime, timeEndTime, wtVos);
		for(int i = 0; i < checkTimeScope.length; i++) {
			for(int j = 0; j < wtVos.length; j++) {
				//������ϰ࿨����ˢ����ʼʱ�丳ֵ���ù���ʱ��ε�ˢ����ʼʱ����Ӧ�ֶ���
				if(checkTimeScope[i].getCheckinScopeTimeID() == wtVos[j].getTimeid()) {
					wtVos[j].setKsfromday(checkTimeScope[i].getKsfromday());
					wtVos[j].setKsfromtime(checkTimeScope[i].getKsfromtime());
					wtVos[j].setKstoday(checkTimeScope[i].getKstoday());
					wtVos[j].setKstotime(checkTimeScope[i].getKstotime());
				}
				if(checkTimeScope[i].getCheckoutScopeTimeID() == wtVos[j].getTimeid()) {
					wtVos[j].setJsfromday(checkTimeScope[i].getJsfromday());
					wtVos[j].setJsfromtime(checkTimeScope[i].getJsfromtime());
					wtVos[j].setJstoday(checkTimeScope[i].getJstoday());
					wtVos[j].setJstotime(checkTimeScope[i].getJstotime());
				}
			}
		}

		return wtVos;
	}
	
	/**
	 * ɾ��ԭ����ʱ�����Ϣʱ��
	 * �仯�����Ϣʱ��ʱ����
	 * @param aggVo
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void deleteOrgnWts(AggShiftVO aggVo, Class wrClass) throws BusinessException {

		if(aggVo == null)
			return;
		SuperVO[] wrVos = (SuperVO[])queryWRByCondition(aggVo,wrClass);

		if(wrVos != null) {
			dao.deleteVOArray(wrVos);
		}

	}
	
	/**
	 * �̻����Թ���ʱ��
	 * �̻������°࿪ʼʱ��ͽ���ʱ��Ϊ��
	 * �����ϰ�������ϰ���м��Ϊ�ʼ���ϰ�ʱ�䣻�����°�������°���м��Ϊ����ʱ��������°�ʱ��
	 * @param vo
	 * @return
	 */
	private IRelativeTimeScope getOtflexScope(ShiftVO vo) {
		//�����ϰ�ʱ��
		IRelativeTime elstStartTime = new DefaultRelativeTime();
		elstStartTime.setDate(vo.getBeginday());
		elstStartTime.setTime(vo.getBegintime());

		//�����ϰ�ʱ��
		IRelativeTime ltstStartTime = new DefaultRelativeTime();
		ltstStartTime.setDate(vo.getLatestbeginday());
		ltstStartTime.setTime(vo.getLatestbegintime());

		//�����°�ʱ��
		IRelativeTime elstEndTime = new DefaultRelativeTime();
		elstEndTime.setDate(vo.getEarliestendday());
		elstEndTime.setTime(vo.getEarliestendtime());

		//�����°�ʱ��
		IRelativeTime ltstEndTime = new DefaultRelativeTime();
		ltstEndTime.setDate(vo.getEndday());
		ltstEndTime.setTime(vo.getEndtime());

		//ȡ�����ϰ�ʱ��������ϰ�ʱ����м��
		IRelativeTime midOnTime = RelativeTimeUtils.getMidTime(elstStartTime, ltstStartTime);
		//ȡ�����°�ʱ��������°�ʱ����м��
		IRelativeTime midOffTime = RelativeTimeUtils.getMidTime(elstEndTime, ltstEndTime);

		IRelativeTimeScope wtScope = new DefaultRelativeTimeScope();
		wtScope.setScopeStartDate(midOnTime.getDate());
		wtScope.setScopeStartTime(midOnTime.getTime());
		wtScope.setScopeEndDate(midOffTime.getDate());
		wtScope.setScopeEndTime(midOffTime.getTime());
		wtScope.setContainsLastSecond(false);
		return wtScope;
	}
	
	/**
	 * �̻�������Ϣʱ��
	 * �̻�����Ϣ��ʼʱ��Ϊ����������Ϣ����ʱ��-������Ϣ��ʼʱ�䣩/2-��Ϣʱ��/2��
	 * ��Ϣ����ʱ��Ϊ: ��������Ϣ����ʱ��-������Ϣ��ʼʱ�䣩/2+��Ϣʱ��/2��
	 */
	private IRelativeTimeScope[] getRtFlexScope(ShiftVO mainVo, RTVO[] rtVos) {
		ArrayList<RTVO> flexRtvos = new ArrayList<RTVO>();
		for (int i = 0; i < rtVos.length; i++) {
			RTVO vo = (RTVO)rtVos[i].clone();
			if(vo.getIsflexible().booleanValue()) {
				//������Ϣ��ʼʱ��
				IRelativeTime elstStartTime = new DefaultRelativeTime();
				elstStartTime.setDate(vo.getBeginday());
				elstStartTime.setTime(vo.getBegintime());

				//������Ϣ����ʱ��
				IRelativeTime ltstEndTime = new DefaultRelativeTime();
				ltstEndTime.setDate(vo.getEndday());
				ltstEndTime.setTime(vo.getEndtime());

				//ȡ������Ϣ��ʼʱ���������Ϣ����ʱ����м��
				IRelativeTime midTime = RelativeTimeUtils.getMidTime(elstStartTime, ltstEndTime);

				//ȡ����Ϣ��ʼʱ��
				IRelativeTime startTime = RelativeTimeUtils.minus(midTime, vo.getResttime().longValue()/2*60);
				//ȡ�ý�����ʼʱ��
				IRelativeTime endTime = RelativeTimeUtils.add(midTime, vo.getResttime().longValue()/2*60);
				//������Ϣʱ����
				vo.setScopeStartDate(startTime.getDate());
				vo.setScopeStartTime(startTime.getTime());
				vo.setScopeEndDate(endTime.getDate());
				vo.setScopeEndTime(endTime.getTime());
//				vo.setContainsLastSecond(false);
			}
			flexRtvos.add(vo);
		}
		RTVO[] newRtVos = new RTVO[flexRtvos.size()];
		return flexRtvos.toArray(newRtVos);
	}
	
	/**
	 * ��ѯ����µĹ���ʱ�����Ϣʱ��
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private <T>T[] queryWRByCondition(AggShiftVO aggVo,Class<T> wrClass) throws BusinessException {

		String condition = ShiftVO.PK_SHIFT + " = '" + aggVo.getShiftVO().getPk_shift() + "' ";

		Collection<?> c = getMDQueryService().queryBillOfVOByCond(wrClass, condition, false);

        if (c != null && c.size() > 0)
        {
            return c.toArray((T[]) Array.newInstance(wrClass, c.size()));
        }

        return null;
	}
	
	/**
	 * �鿴�Ƿ���Ҫ�ı乤��ʱ���
	 * �����Ϣʱ��Ϊ�����仯���򲻸ı�
	 * @param aggVo
	 * @return
	 */
	private boolean checkChgWrTime(AggShiftVO aggVo) throws BusinessException {

		if(aggVo == null) {
			return false;
		}
		//��ǰ��Ϣʱ��
		RTVO[] rtVos = (RTVO[])aggVo.getTableVO(AggShiftVO.RT_SUB);

		//���ݿ��еİ��
		AggShiftVO dbAggVo = getShiftMaintain().queryByPk(aggVo.getShiftVO().getPk_shift());
		RTVO[] dbRtVos = (RTVO[])dbAggVo.getTableVO(AggShiftVO.RT_SUB);

		//������ݿ����Ϣʱ��͵�ǰ��Ϣʱ���Ϊ�գ�����δ�����仯
		if((rtVos == null && dbRtVos == null))
				return false;
		if(!ArrayUtils.isEmpty(rtVos) && !ArrayUtils.isEmpty(dbRtVos) && rtVos==dbRtVos) {
			return false;
		}
		return true;
	}

	/**
	 * ��Ϊ�������������޸ģ�����ʱ����ԭ��β��ܹ���ʱ��ɾ������insert���ܹ���ʱ�䣬����̨insertWithPk�����У�
	 * �����vo��������Ϊ�գ���ô�������ֲ��䣻�������Ϊ�ղ�������������˱���ʱ��Ҫ��������Ϣʱ��ε�pk��Ϊ��
	 * ����ʱ��β�ǣ�������⣬��Ϊ����ʱ�����ǰ̨ҳ��û����ʾ����˴����ݿ�ɾ������ʱ��κ󲻻��������ظ�������
	 * @param vo
	 */
	private void clearCapRtPk(AggShiftVO vo) {
		CapRTVO[] caprtvos = vo.getCAPRTVOs();
		if(ArrayUtils.isEmpty(caprtvos))
			return;
		for(CapRTVO caprtvo : caprtvos) {
			caprtvo.setPk_caprt(null);
		}
	}
	
	
	/**
	 * ɾ��ԭ����ʱ�����Ϣʱ��
	 * �仯�����Ϣʱ��ʱ����
	 * @param aggVo
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void deleteOrgnCapWts(AggShiftVO aggVo, Class capwrClass) throws BusinessException {

		if(aggVo == null)
			return;
		SuperVO[] capwrVos = (SuperVO[])queryWRByCondition(aggVo,capwrClass);

		if(capwrVos != null) {
			dao.deleteVOArray(capwrVos);
		}

	}

	
	
	/**
	 * ��Ϊ�������������޸ģ�����ʱ����ԭ��β��ܹ���ʱ��ɾ������insert����ʱ�䣬����̨insertWithPk�����У�
	 * �����vo��������Ϊ�գ���ô�������ֲ��䣻�������Ϊ�ղ�������������˱���ʱ��Ҫ����Ϣʱ��ε�pk��Ϊ��
	 * ����ʱ��β�ǣ�������⣬��Ϊ����ʱ�����ǰ̨ҳ��û����ʾ����˴����ݿ�ɾ������ʱ��κ󲻻��������ظ�������
	 * @param vo
	 */
	private void clearRtPk(AggShiftVO vo) {
		RTVO[] rtvos = vo.getRTVOs();
		if(ArrayUtils.isEmpty(rtvos))
			return;
		for(RTVO rtvo : rtvos) {
			rtvo.setStatus(VOStatus.NEW);
			rtvo.setPk_rt(null);
		}
	}
	
	//�鿴����Ƿ��г�ͻ
		private void checkMutex(ShiftVO vo1, ShiftVO vo2) throws BusinessException {
			boolean isMutex = false;
			IRelativeTimeScope vo1OrigScope =  vo1.toRelativeKqScope();
			IRelativeTimeScope vo2OrigScope =  vo2.toRelativeKqScope();
			//���ʱ��Ϊ1��2ʱ���ٷֱ��ж��Ƿ�Ϊ���ڳ�ͻ���߶Ե���ͻ
			for(int i =1; i<=2; i++) {
				//vo1Ϊǰһ��
				IRelativeTimeScope vo1NewScope = new DefaultRelativeTimeScope();
				vo1NewScope.setScopeStartDate(vo1OrigScope.getScopeStartDate()-i);
				vo1NewScope.setScopeEndDate(vo1OrigScope.getScopeEndDate()-i);
				vo1NewScope.setScopeStartTime(vo1OrigScope.getScopeStartTime());
				vo1NewScope.setScopeEndTime(vo1OrigScope.getScopeEndTime());
				if(isCloseMutex(vo1NewScope,vo2OrigScope) || isExchgeMutex(vo1NewScope, vo2OrigScope)) {
					isMutex = true;
				}
				//vo2Ϊǰһ��
				IRelativeTimeScope vo2NewScope = new DefaultRelativeTimeScope(); ;
				vo2NewScope.setScopeStartDate(vo2OrigScope.getScopeStartDate()-i);
				vo2NewScope.setScopeEndDate(vo2OrigScope.getScopeEndDate()-i);
				vo2NewScope.setScopeStartTime(vo2OrigScope.getScopeStartTime());
				vo2NewScope.setScopeEndTime(vo2OrigScope.getScopeEndTime());
				if(isCloseMutex(vo2NewScope,vo1OrigScope) || isExchgeMutex(vo2NewScope, vo1OrigScope)) {
					isMutex = true;
				}
			}
			if(isMutex)
				throw new BusinessException(ResHelper.getString("hrbd","0hrbd0200")
	/*@res "Ĭ�ϰ��������ˢ��ʱ�䲻�ܳ�ͻ"*/);
		}
		
		/**
		 * ɾ��ԭ��ͻ��Ϣ
		 * @param aggVo
		 */
		private void deleteOrigMutex(AggShiftVO aggVo) throws BusinessException {

			if(aggVo == null)
				return;
			ShiftVO vo = (ShiftVO)aggVo.getParentVO();
			String primaryKey =vo.getPk_shift();
			if(StringUtils.isEmpty(primaryKey))
				return;
			String condition = " firstshiftid = '" + primaryKey + "' or nextshiftid = '" + primaryKey + "' ";
			new BaseDAO().deleteByClause(ShiftMutexBUVO.class, condition);
		}
		
		/**
		 * �����ͻ��
		 * 1��ɾ��ԭ��ͻ
		 * 2���Ƚ���ǰ��θ����ݿ���ͬҵ��Ԫͬ����������������Ƚ�
		 * 3���ٸ��Լ��Ƚ�
		 * @param aggVo
		 */
		private void chkAndSaveMutex(AggShiftVO aggVo) throws BusinessException {
			chkAndSaveMutexOther(aggVo);
			chkAndSaveMutexSelf(aggVo);
		}
		
		/**
		 * �����ǰ��������ݿ�����������г�ͻ�����¼��ͻ��Ϣ
		 * @param aggVo
		 * @throws BusinessException
		 */
		private void chkAndSaveMutexOther(AggShiftVO aggVo) throws BusinessException {
			ShiftVO mainVo = (ShiftVO)aggVo.getParentVO();
			//��ѯҵ��Ԫ��ͬ�����������а��
			String condition = " "+ ShiftVO.PK_ORG +" = '" + mainVo.getPk_org() +
			//�����Ҫ	"' and "+ ShiftVO.PK_SHIFTTYPE + " = '" + mainVo.getPk_shifttype() +
				"' and " + ShiftVO.PK_SHIFT + " <> '" + mainVo.getPk_shift() + "' ";
			AggShiftVO[] aggVos = getShiftMaintain().queryByCondition(condition);
			if(ArrayUtils.isEmpty(aggVos))
				return;
			//�����μ��ͻ
			for (int i = 0; i < aggVos.length; i++) {
				ShiftVO otherShiftVO = (ShiftVO)aggVos[i].getParentVO();
				saveShiftMutex(mainVo, otherShiftVO);
			}
		}
		
		/**
		 * �鿴������μ��ˢ��ʱ���Ƿ���ͬ
		 * @param firstvo
		 * @param nextvo
		 * @return
		 */
		private boolean checkKqTime(ShiftVO firstvo,ShiftVO nextvo) {
			return firstvo.getTimebeginday().equals(nextvo.getTimebeginday()) &&
				   firstvo.getTimebegintime().equals(nextvo.getTimebegintime()) &&
				   firstvo.getTimeendday().equals(nextvo.getTimeendday()) &&
				   firstvo.getTimeendtime().equals(nextvo.getTimeendtime());
		}
		
		/**
		 * �鿴��������Ƿ���ڳ�ͻ
		 * @param vo1Scope
		 * @param vo2Scope
		 * @return
		 */
		private boolean isCloseMutex(IRelativeTimeScope vo1Scope,
				IRelativeTimeScope vo2Scope) {
			DefaultRelativeTimeScope[] shiftScope1 = new DefaultRelativeTimeScope[]{(DefaultRelativeTimeScope)vo1Scope};
			DefaultRelativeTimeScope[] shiftScope2 = new DefaultRelativeTimeScope[]{(DefaultRelativeTimeScope)vo2Scope};
			//��ͻ���ʱ��
			IRelativeTimeScope[] mutexScope = RelativeTimeScopeUtils.intersectionRelativeTimeScopes(shiftScope1, shiftScope2);
			//�޳�ͻ
			if(mutexScope.length <= 0 || mutexScope==shiftScope1)
				return false;
			return true;
		}
		
		//�鿴�Ƿ�������μ��ǶԵ����
		private boolean isExchgeMutex(IRelativeTimeScope scope1, IRelativeTimeScope scope2) {
			IRelativeTime startTime1 = new DefaultRelativeTime();
			startTime1.setDate(scope1.getScopeStartDate());
			startTime1.setTime(scope1.getScopeStartTime());

			IRelativeTime endTime2 = new DefaultRelativeTime();
			endTime2.setDate(scope2.getScopeEndDate());
			endTime2.setTime(scope2.getScopeEndTime());

			return endTime2.before(startTime1);
		}
		
		/**
		 * �����İ�αȽ����⣬���Լ�Ҳ��Ҫ�Ƚϣ���Ϊ�Ű��ʱ���������������ͬ���İ�
		 * @param aggVo
		 * @throws BusinessException
		 */
		private void chkAndSaveMutexSelf(AggShiftVO aggVo) throws BusinessException  {
			ShiftVO mainVo = (ShiftVO)aggVo.getParentVO();
			ShiftVO mainVo1 = (ShiftVO)mainVo.clone();
			//saveShiftMutex(mainVo, mainVo1); ���������������ظ������ͻ��
			saveShiftMutexSelf(mainVo, mainVo1);
		}
		
		private void saveShiftMutexSelf(ShiftVO vo1, ShiftVO vo2) throws BusinessException {
			IRelativeTimeScope vo1OrigScope =  vo1.toRelativeKqScope();
			IRelativeTimeScope vo2OrigScope =  vo2.toRelativeKqScope();
			//���ʱ��Ϊ1��2ʱ���ٷֱ��ж��Ƿ�Ϊ���ڳ�ͻ���߶Ե���ͻ
			for(int i =1; i<=2; i++) {
				//vo1Ϊǰһ��
				IRelativeTimeScope vo1NewScope = new DefaultRelativeTimeScope();
				vo1NewScope.setScopeStartDate(vo1OrigScope.getScopeStartDate()-i);
				vo1NewScope.setScopeEndDate(vo1OrigScope.getScopeEndDate()-i);
				vo1NewScope.setScopeStartTime(vo1OrigScope.getScopeStartTime());
				vo1NewScope.setScopeEndTime(vo1OrigScope.getScopeEndTime());
				saveMutex(vo1,vo2,vo1NewScope,vo2OrigScope,i-1);
			}
		}
		
		/**
		 * �����ͻ��Ϣ
		 * ��������״��
		 * 1�����ڳ�ͻ
		 * 2���Ե���ͻ
		 * @param vo1��ǰһ��İ��
		 * @param vo2����һ��İ��
		 * @param scope1:ǰһ������ˢ��ʱ����
		 * @param scope2����һ������ˢ��ʱ����\
		 * @param sepday: �������
		 */
		private boolean saveMutex(ShiftVO vo1, ShiftVO vo2, IRelativeTimeScope scope1, IRelativeTimeScope scope2, int sepday)
		 	throws BusinessException {
			//�Ƿ���ڳ�ͻ
			if(isCloseMutex(scope1,scope2)) {
				insertShiftMunex(vo1,vo2,sepday,ShiftMutexBUVO.NEXTMUTEX);
				return true;
			}
			//�Ƿ�Ե���ͻ
			if(isExchgeMutex(scope1, scope2)) {
				insertShiftMunex(vo1,vo2,sepday,ShiftMutexBUVO.EXGHGMUTEX);
				return true;
			}
			return false;
		}
		
		/**�����������ˢ��ʱ����ĳ�ͻ��Ϣ
		 * @param vo1: Ҫ�Ƚϵİ��
		 * @param vo2��
		 * @throws BusinessException
		 */
		private void saveShiftMutex(ShiftVO vo1, ShiftVO vo2) throws BusinessException {
			IRelativeTimeScope vo1OrigScope =  vo1.toRelativeKqScope();
			IRelativeTimeScope vo2OrigScope =  vo2.toRelativeKqScope();
			//���ʱ��Ϊ1��2ʱ���ٷֱ��ж��Ƿ�Ϊ���ڳ�ͻ���߶Ե���ͻ
			for(int i =1; i<=2; i++) {
				//vo1Ϊǰһ��
				IRelativeTimeScope vo1NewScope = new DefaultRelativeTimeScope();
				vo1NewScope.setScopeStartDate(vo1OrigScope.getScopeStartDate()-i);
				vo1NewScope.setScopeEndDate(vo1OrigScope.getScopeEndDate()-i);
				vo1NewScope.setScopeStartTime(vo1OrigScope.getScopeStartTime());
				vo1NewScope.setScopeEndTime(vo1OrigScope.getScopeEndTime());
				saveMutex(vo1,vo2,vo1NewScope,vo2OrigScope,i-1);
				//vo2Ϊǰһ��
				IRelativeTimeScope vo2NewScope = new DefaultRelativeTimeScope(); ;
				vo2NewScope.setScopeStartDate(vo2OrigScope.getScopeStartDate()-i);
				vo2NewScope.setScopeEndDate(vo2OrigScope.getScopeEndDate()-i);
				vo2NewScope.setScopeStartTime(vo2OrigScope.getScopeStartTime());
				vo2NewScope.setScopeEndTime(vo2OrigScope.getScopeEndTime());
				saveMutex(vo2,vo1,vo2NewScope,vo1OrigScope,i-1);
			}
		}
		/**
		 * �����γ�ͻ��Ϣ
		 * @param vo1
		 * @param vo2
		 * @param setday���������
		 * @param mutexType����ͻ����
		 * @throws BusinessException
		 */
		private void insertShiftMunex(ShiftVO vo1, ShiftVO vo2, Integer sepday, Integer mutexType) throws BusinessException {
			ShiftMutexBUVO preMutexVO = new ShiftMutexBUVO();
			preMutexVO.setFirstshiftid(vo1.getPk_shift());
			preMutexVO.setNextshiftid(vo2.getPk_shift());
			preMutexVO.setPk_group(vo1.getPk_group());
			preMutexVO.setPk_org(vo1.getPk_org());
			preMutexVO.setSepday(sepday);
			preMutexVO.setMutextype(mutexType);
			new BaseDAO().insertVOWithPK(preMutexVO);
		}
}
