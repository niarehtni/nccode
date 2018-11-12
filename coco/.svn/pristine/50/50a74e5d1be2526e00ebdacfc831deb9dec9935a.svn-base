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
		//校验班次唯一性
		StoreShiftUniqueValidator unval = new StoreShiftUniqueValidator();
		unval.validate(vo);
		//校验班次定义中的规则
		StoreShiftValidator val = new StoreShiftValidator();
		val.validate(vo);
		//计算夜班时长
		vo.getShiftVO().setNightgzsj(computeNightgzsj(vo.getShiftVO()));
		
		//根据休息时间获取工作时间，并且物理删除原工作时间
		setWorkTimes(vo);
		//根据产能休息时间获取产能工作时间，并且物理删除原产能工作时间
		//setCapWorkTimes(vo);
		// 添加审计信息
		vo.getShiftVO().setStatus(VOStatus.NEW);
		AuditInfoUtil.addData(vo.getShiftVO());
		//插入班次
		//新增前事件
		EventDispatcher.fireEvent(new BusinessEvent(DOC_NAME, IEventType.TYPE_INSERT_BEFORE, vo));
		String pk = getMDService().saveBill(vo);
		//新增后事件
		EventDispatcher.fireEvent(new BusinessEvent(DOC_NAME, IEventType.TYPE_INSERT_AFTER, vo));
		AggShiftVO returnvo = (AggShiftVO) getMDQueryService().queryBillOfVOByPK(AggShiftVO.class, pk, false);
		return returnvo;
		//return getServiceTemplate().insert(vo);
		
		/*//新增前事件
//		EventDispatcher.fireEvent(new BusinessEvent(DOC_NAME, IEventType.TYPE_INSERT_BEFORE, vo));
		String pk = getMDService().saveBill(vo);
		//新增后事件
//		、、EventDispatcher.fireEvent(new BusinessEvent(DOC_NAME, IEventType.TYPE_INSERT_AFTER, vo));

		AggShiftVO returnvo = (AggShiftVO) getMDQueryService().queryBillOfVOByPK(AggShiftVO.class, pk, false);
		return returnvo;*/
	}

	@Override
	public AggShiftVO update(AggShiftVO vo) throws BusinessException {
		// TODO Auto-generated method stub
		//return getServiceTemplate().update(vo,true);
		//加锁
		BDPKLockUtil.lockAggVO(vo);
	
		ShiftVO mainvo = (ShiftVO) vo.getParentVO();
		AggShiftVO dbAggShiftVO = getShiftMaintain().queryByPk(mainvo.getPk_shift());
		ShiftVO dbShiftVO = (ShiftVO)dbAggShiftVO.getParentVO();
		
		BDVersionValidationUtil.validateSuperVO(mainvo);
		//如果是默认班次，则班次的时间不能有冲突
		if(dbShiftVO.getDefaultflag() != null && dbShiftVO.getDefaultflag().booleanValue()) {
			checkMutex(mainvo, mainvo);
		}
		
		vo.getParentVO().setStatus(VOStatus.UPDATED);
		//校验班次唯一性
		StoreShiftUniqueValidator unval = new StoreShiftUniqueValidator();
		unval.validate(vo);
		//校验班次定义中的规则
		StoreShiftValidator val = new StoreShiftValidator();
		val.validate(vo);
		
		// 添加审计信息
		AuditInfoUtil.updateData(mainvo);
		//计算夜班时长
		mainvo.setNightgzsj(computeNightgzsj(mainvo));	
		
		// 发送事件  查看使用该班次的工作日历对应考勤期间是否已经封存，如果封存，则不允许修改
		BDCommonEvent beforEvent = new BDCommonEvent(DOC_NAME, IEventType.TYPE_UPDATE_BEFORE,vo);
		EventDispatcher.fireEvent(beforEvent);

		//修改班次时先删除原工作时间、休息时间，再生成新的工作时间
		if(checkChgWrTime(vo)) {
			deleteOrgnWts(vo,RTVO.class);
			setWorkTimes(vo);
		}
		deleteOrgnCapWts(vo, CapWTVO.class);
		deleteOrgnWts(vo,CapRTVO.class);
//		setCapWorkTimes(vo);
		//删除休息时间的主键（虽然在数据库已经删除，但从页面传来的RTVO仍存在主键）
		clearRtPk(vo);
		clearCapRtPk(vo);
		
		String pk = this.getMDService().saveBill(vo);
		AggShiftVO returnvo = (AggShiftVO) getMDQueryService().queryBillOfVOByPK(AggShiftVO.class, pk, false);
		//查看当前班次和数据库中中该班次的刷卡时间是否相同
		if(!checkKqTime(mainvo, dbShiftVO)) {
			//先删除原冲突信息
			deleteOrigMutex(returnvo);
			chkAndSaveMutex(returnvo);
		}
		//发送修改后事件 （可以进行更新工作日历等）
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
		//发送事件      可以进行 例如 删除与之对应的工作日历等操作
		EventDispatcher.fireEvent(new BusinessEvent(DOC_NAME, IEventType.TYPE_DELETE_BEFORE,vo));
		//数据库删除
		this.getMDService().deleteBillFromDB(vo);
		//删除后事件
		EventDispatcher.fireEvent(new BusinessEvent(DOC_NAME, IEventType.TYPE_DELETE_AFTER,vo));

	}

	@Override
	public AggShiftVO disable(AggShiftVO vo) throws BusinessException {
		// TODO Auto-generated method stub
		ShiftVO mainVo = (ShiftVO)vo.getParentVO();
		if(mainVo.getDefaultflag() != null && mainVo.getDefaultflag().booleanValue()) {
			throw new BusinessException(ResHelper.getString("hrbd","0hrbd0199")/*@res "默认班次不能停用"*/);
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
	 * 计算夜班的工作时长
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
	 * 根据休息时间获取工作时间，并且物理删除原工作时间
	 * @param aggVo
	 */
	private void setWorkTimes(AggShiftVO aggVo) throws BusinessException{

		if(aggVo ==null) {
			return;
		}
		//先删除数据库中的工作时间
		deleteOrgnWts(aggVo,WTVO.class);
		ShiftVO vo = (ShiftVO)aggVo.getParentVO();

		//目前如果上下班日期时间任何一项为空，则不进行任何操作
		if(vo.getBeginday() == null || vo.getBegintime() == null
			|| vo.getEndday() == null || vo.getEndtime() == null) {
			return;
		}

		//当前休息时间
		RTVO[] rtVos = (RTVO[])aggVo.getTableVO(AggShiftVO.RT_SUB);
		WTVO[] wtVos = getWtVos(rtVos, vo);

		aggVo.setTableVO(AggShiftVO.WT_SUB, wtVos);
	}
	
	/**
	 * 根据上下班时间和休息时间获取工作时间列表
	 * a)	如果是上班时间弹性，则固化后上班时间和下班时间为：
	 * 		最早上班和最晚上班的中间点为最开始的上班时间；最晚下班和最早下班的中间点为工作时间的最晚下班时间
	 * b)	如果是休息时间弹性：则每一条的
	 * 			休息开始时间为（最晚休息结束时间-最早休息开始时间）/2-休息时长/2；
	 * 			休息结束时间为（最晚休息结束时间-最早休息开始时间）/2+休息时长/2；
	 * @param rtVos ： 休息时间
	 * @param mainVo
	 * @return
	 * @throws BusinessException
	 */
	private WTVO[] getWtVos(RTVO[] rtVos,ShiftVO mainVo)
		throws BusinessException {

		//上下班时间的相对时间域
		IRelativeTimeScope[] wtTimeScopes = null;
		//休息时间的相对时间域
		IRelativeTimeScope[] rtTimeScopes = null;
		//固定班：最终不为弹性
		if(!mainVo.getIsflexiblefinal().booleanValue()) {
			wtTimeScopes = new DefaultRelativeTimeScope[]{(DefaultRelativeTimeScope)mainVo.toRelativeWorkScope()};
			//设置休息时间域
//			setSldRtScope(rtVos);
			rtTimeScopes = rtVos;
		//工作时间弹性：上班时间字段最终为弹性
		} else if(mainVo.getIsotflexible().booleanValue()) {
			//固化工作时间
			wtTimeScopes = new DefaultRelativeTimeScope[]{(DefaultRelativeTimeScope)getOtflexScope(mainVo)};
			rtTimeScopes = rtVos;
//			setSldRtScope(rtVos);
		//休息时间弹性
		} else {
			//固化休息时间
			wtTimeScopes = new DefaultRelativeTimeScope[]{(DefaultRelativeTimeScope)mainVo.toRelativeWorkScope()};
			rtTimeScopes =  getRtFlexScope(mainVo, rtVos);
		}
		//如果没有休息时间，则工作时间有一条记录
		//如果有休息时间，则工作时间比休息时间长度多1.
		int rtLength = 1;
		if(rtVos != null) {
			rtLength = rtVos.length + 1;
		}
		//得到班次定义时间
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
			//最早工作开始时间一定打卡
			//最晚工作结束时间一定打卡
			//如果休息时间打卡，则该休息时间段前一个工作时间段的结束时间需要打卡
			//该休息时间段下一个工作时间段的开始时间需要打卡
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
	 * 根据工作时间计算刷卡时间段
	 * @param rtVos
	 * @param mainVo
	 * @param wtVos
	 * @return
	 */
	private WTVO[] saveWtskTime(RTVO[] rtVos,ShiftVO mainVo,WTVO[] wtVos) {
		//刷卡开始相对时间
		IRelativeTime timeStartTime = new DefaultRelativeTime();
		timeStartTime.setDate(mainVo.getTimebeginday());
		timeStartTime.setTime(mainVo.getTimebegintime());
		//刷卡结束相对时间
		IRelativeTime timeEndTime = new DefaultRelativeTime();
		timeEndTime.setDate(mainVo.getTimeendday());
		timeEndTime.setTime(mainVo.getTimeendtime());

		//得到刷卡时间段
		IRelativeCompleteCheckTimeScope[] checkTimeScope = RelativeCompleteCheckTimeScopeUtils.mergeWorkTime(timeStartTime, timeEndTime, wtVos);
		for(int i = 0; i < checkTimeScope.length; i++) {
			for(int j = 0; j < wtVos.length; j++) {
				//如果是上班卡，则将刷卡开始时间赋值到该工作时间段的刷卡开始时间相应字段中
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
	 * 删除原工作时间或休息时间
	 * 变化班次休息时间时调用
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
	 * 固化弹性工作时间
	 * 固化后上下班开始时间和结束时间为：
	 * 最早上班和最晚上班的中间点为最开始的上班时间；最晚下班和最早下班的中间点为工作时间的最晚下班时间
	 * @param vo
	 * @return
	 */
	private IRelativeTimeScope getOtflexScope(ShiftVO vo) {
		//最早上班时间
		IRelativeTime elstStartTime = new DefaultRelativeTime();
		elstStartTime.setDate(vo.getBeginday());
		elstStartTime.setTime(vo.getBegintime());

		//最晚上班时间
		IRelativeTime ltstStartTime = new DefaultRelativeTime();
		ltstStartTime.setDate(vo.getLatestbeginday());
		ltstStartTime.setTime(vo.getLatestbegintime());

		//最早下班时间
		IRelativeTime elstEndTime = new DefaultRelativeTime();
		elstEndTime.setDate(vo.getEarliestendday());
		elstEndTime.setTime(vo.getEarliestendtime());

		//最晚下班时间
		IRelativeTime ltstEndTime = new DefaultRelativeTime();
		ltstEndTime.setDate(vo.getEndday());
		ltstEndTime.setTime(vo.getEndtime());

		//取最早上班时间和最晚上班时间的中间点
		IRelativeTime midOnTime = RelativeTimeUtils.getMidTime(elstStartTime, ltstStartTime);
		//取最早下班时间和最晚下班时间的中间点
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
	 * 固化弹性休息时间
	 * 固化后休息开始时间为：（最晚休息结束时间-最早休息开始时间）/2-休息时长/2；
	 * 休息结束时间为: （最晚休息结束时间-最早休息开始时间）/2+休息时长/2；
	 */
	private IRelativeTimeScope[] getRtFlexScope(ShiftVO mainVo, RTVO[] rtVos) {
		ArrayList<RTVO> flexRtvos = new ArrayList<RTVO>();
		for (int i = 0; i < rtVos.length; i++) {
			RTVO vo = (RTVO)rtVos[i].clone();
			if(vo.getIsflexible().booleanValue()) {
				//最早休息开始时间
				IRelativeTime elstStartTime = new DefaultRelativeTime();
				elstStartTime.setDate(vo.getBeginday());
				elstStartTime.setTime(vo.getBegintime());

				//最晚休息结束时间
				IRelativeTime ltstEndTime = new DefaultRelativeTime();
				ltstEndTime.setDate(vo.getEndday());
				ltstEndTime.setTime(vo.getEndtime());

				//取最早休息开始时间和最晚休息结束时间的中间点
				IRelativeTime midTime = RelativeTimeUtils.getMidTime(elstStartTime, ltstEndTime);

				//取得休息开始时间
				IRelativeTime startTime = RelativeTimeUtils.minus(midTime, vo.getResttime().longValue()/2*60);
				//取得结束开始时间
				IRelativeTime endTime = RelativeTimeUtils.add(midTime, vo.getResttime().longValue()/2*60);
				//设置休息时间域
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
	 * 查询班次下的工作时间或休息时间
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
	 * 查看是否需要改变工作时间或
	 * 如果休息时间为发生变化，则不改变
	 * @param aggVo
	 * @return
	 */
	private boolean checkChgWrTime(AggShiftVO aggVo) throws BusinessException {

		if(aggVo == null) {
			return false;
		}
		//当前休息时间
		RTVO[] rtVos = (RTVO[])aggVo.getTableVO(AggShiftVO.RT_SUB);

		//数据库中的班次
		AggShiftVO dbAggVo = getShiftMaintain().queryByPk(aggVo.getShiftVO().getPk_shift());
		RTVO[] dbRtVos = (RTVO[])dbAggVo.getTableVO(AggShiftVO.RT_SUB);

		//如果数据库的休息时间和当前休息时间均为空，或者未发生变化
		if((rtVos == null && dbRtVos == null))
				return false;
		if(!ArrayUtils.isEmpty(rtVos) && !ArrayUtils.isEmpty(dbRtVos) && rtVos==dbRtVos) {
			return false;
		}
		return true;
	}

	/**
	 * 因为无论新增还是修改，保存时均将原班次产能工休时间删除，再insert产能工休时间，但后台insertWithPk方法中，
	 * 如果该vo的主键不为空，那么主键保持不变；如果主键为空才生成主键，因此保存时需要将产能休息时间段的pk设为空
	 * 工作时间段不牵扯此问题，因为工作时间段在前台页面没有显示，因此从数据库删除工作时间段后不会有主键重复的问题
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
	 * 删除原工作时间或休息时间
	 * 变化班次休息时间时调用
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
	 * 因为无论新增还是修改，保存时均将原班次产能工休时间删除，再insert工休时间，但后台insertWithPk方法中，
	 * 如果该vo的主键不为空，那么主键保持不变；如果主键为空才生成主键，因此保存时需要将休息时间段的pk设为空
	 * 工作时间段不牵扯此问题，因为工作时间段在前台页面没有显示，因此从数据库删除工作时间段后不会有主键重复的问题
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
	
	//查看班次是否有冲突
		private void checkMutex(ShiftVO vo1, ShiftVO vo2) throws BusinessException {
			boolean isMutex = false;
			IRelativeTimeScope vo1OrigScope =  vo1.toRelativeKqScope();
			IRelativeTimeScope vo2OrigScope =  vo2.toRelativeKqScope();
			//间隔时间为1和2时，再分别判断是否为紧邻冲突或者对调冲突
			for(int i =1; i<=2; i++) {
				//vo1为前一天
				IRelativeTimeScope vo1NewScope = new DefaultRelativeTimeScope();
				vo1NewScope.setScopeStartDate(vo1OrigScope.getScopeStartDate()-i);
				vo1NewScope.setScopeEndDate(vo1OrigScope.getScopeEndDate()-i);
				vo1NewScope.setScopeStartTime(vo1OrigScope.getScopeStartTime());
				vo1NewScope.setScopeEndTime(vo1OrigScope.getScopeEndTime());
				if(isCloseMutex(vo1NewScope,vo2OrigScope) || isExchgeMutex(vo1NewScope, vo2OrigScope)) {
					isMutex = true;
				}
				//vo2为前一天
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
	/*@res "默认班次与自身刷卡时间不能冲突"*/);
		}
		
		/**
		 * 删除原冲突信息
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
		 * 解决冲突：
		 * 1、删除原冲突
		 * 2、先将当前班次跟数据库中同业务单元同班次类别其他班次做比较
		 * 3、再跟自己比较
		 * @param aggVo
		 */
		private void chkAndSaveMutex(AggShiftVO aggVo) throws BusinessException {
			chkAndSaveMutexOther(aggVo);
			chkAndSaveMutexSelf(aggVo);
		}
		
		/**
		 * 如果当前班次与数据库中其他班次有冲突，则记录冲突信息
		 * @param aggVo
		 * @throws BusinessException
		 */
		private void chkAndSaveMutexOther(AggShiftVO aggVo) throws BusinessException {
			ShiftVO mainVo = (ShiftVO)aggVo.getParentVO();
			//查询业务单元和同班次类别下所有班次
			String condition = " "+ ShiftVO.PK_ORG +" = '" + mainVo.getPk_org() +
			//类别不需要	"' and "+ ShiftVO.PK_SHIFTTYPE + " = '" + mainVo.getPk_shifttype() +
				"' and " + ShiftVO.PK_SHIFT + " <> '" + mainVo.getPk_shift() + "' ";
			AggShiftVO[] aggVos = getShiftMaintain().queryByCondition(condition);
			if(ArrayUtils.isEmpty(aggVos))
				return;
			//保存班次间冲突
			for (int i = 0; i < aggVos.length; i++) {
				ShiftVO otherShiftVO = (ShiftVO)aggVos[i].getParentVO();
				saveShiftMutex(mainVo, otherShiftVO);
			}
		}
		
		/**
		 * 查看两个班次间的刷卡时间是否相同
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
		 * 查看两个班次是否紧邻冲突
		 * @param vo1Scope
		 * @param vo2Scope
		 * @return
		 */
		private boolean isCloseMutex(IRelativeTimeScope vo1Scope,
				IRelativeTimeScope vo2Scope) {
			DefaultRelativeTimeScope[] shiftScope1 = new DefaultRelativeTimeScope[]{(DefaultRelativeTimeScope)vo1Scope};
			DefaultRelativeTimeScope[] shiftScope2 = new DefaultRelativeTimeScope[]{(DefaultRelativeTimeScope)vo2Scope};
			//冲突相对时间
			IRelativeTimeScope[] mutexScope = RelativeTimeScopeUtils.intersectionRelativeTimeScopes(shiftScope1, shiftScope2);
			//无冲突
			if(mutexScope.length <= 0 || mutexScope==shiftScope1)
				return false;
			return true;
		}
		
		//查看是否两个班次间是对调班次
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
		 * 除与别的班次比较以外，跟自己也需要比较，因为排班的时候可能连续两天排同样的班
		 * @param aggVo
		 * @throws BusinessException
		 */
		private void chkAndSaveMutexSelf(AggShiftVO aggVo) throws BusinessException  {
			ShiftVO mainVo = (ShiftVO)aggVo.getParentVO();
			ShiftVO mainVo1 = (ShiftVO)mainVo.clone();
			//saveShiftMutex(mainVo, mainVo1); 用这个会造成数据重复放入冲突表
			saveShiftMutexSelf(mainVo, mainVo1);
		}
		
		private void saveShiftMutexSelf(ShiftVO vo1, ShiftVO vo2) throws BusinessException {
			IRelativeTimeScope vo1OrigScope =  vo1.toRelativeKqScope();
			IRelativeTimeScope vo2OrigScope =  vo2.toRelativeKqScope();
			//间隔时间为1和2时，再分别判断是否为紧邻冲突或者对调冲突
			for(int i =1; i<=2; i++) {
				//vo1为前一天
				IRelativeTimeScope vo1NewScope = new DefaultRelativeTimeScope();
				vo1NewScope.setScopeStartDate(vo1OrigScope.getScopeStartDate()-i);
				vo1NewScope.setScopeEndDate(vo1OrigScope.getScopeEndDate()-i);
				vo1NewScope.setScopeStartTime(vo1OrigScope.getScopeStartTime());
				vo1NewScope.setScopeEndTime(vo1OrigScope.getScopeEndTime());
				saveMutex(vo1,vo2,vo1NewScope,vo2OrigScope,i-1);
			}
		}
		
		/**
		 * 保存冲突信息
		 * 处理两种状况
		 * 1、紧邻冲突
		 * 2、对调冲突
		 * @param vo1：前一天的班次
		 * @param vo2：后一天的班次
		 * @param scope1:前一天的相对刷卡时间域
		 * @param scope2：后一天的相对刷卡时间域\
		 * @param sepday: 相隔天数
		 */
		private boolean saveMutex(ShiftVO vo1, ShiftVO vo2, IRelativeTimeScope scope1, IRelativeTimeScope scope2, int sepday)
		 	throws BusinessException {
			//是否紧邻冲突
			if(isCloseMutex(scope1,scope2)) {
				insertShiftMunex(vo1,vo2,sepday,ShiftMutexBUVO.NEXTMUTEX);
				return true;
			}
			//是否对调冲突
			if(isExchgeMutex(scope1, scope2)) {
				insertShiftMunex(vo1,vo2,sepday,ShiftMutexBUVO.EXGHGMUTEX);
				return true;
			}
			return false;
		}
		
		/**保存两个班次刷卡时间域的冲突信息
		 * @param vo1: 要比较的班次
		 * @param vo2：
		 * @throws BusinessException
		 */
		private void saveShiftMutex(ShiftVO vo1, ShiftVO vo2) throws BusinessException {
			IRelativeTimeScope vo1OrigScope =  vo1.toRelativeKqScope();
			IRelativeTimeScope vo2OrigScope =  vo2.toRelativeKqScope();
			//间隔时间为1和2时，再分别判断是否为紧邻冲突或者对调冲突
			for(int i =1; i<=2; i++) {
				//vo1为前一天
				IRelativeTimeScope vo1NewScope = new DefaultRelativeTimeScope();
				vo1NewScope.setScopeStartDate(vo1OrigScope.getScopeStartDate()-i);
				vo1NewScope.setScopeEndDate(vo1OrigScope.getScopeEndDate()-i);
				vo1NewScope.setScopeStartTime(vo1OrigScope.getScopeStartTime());
				vo1NewScope.setScopeEndTime(vo1OrigScope.getScopeEndTime());
				saveMutex(vo1,vo2,vo1NewScope,vo2OrigScope,i-1);
				//vo2为前一天
				IRelativeTimeScope vo2NewScope = new DefaultRelativeTimeScope(); ;
				vo2NewScope.setScopeStartDate(vo2OrigScope.getScopeStartDate()-i);
				vo2NewScope.setScopeEndDate(vo2OrigScope.getScopeEndDate()-i);
				vo2NewScope.setScopeStartTime(vo2OrigScope.getScopeStartTime());
				vo2NewScope.setScopeEndTime(vo2OrigScope.getScopeEndTime());
				saveMutex(vo2,vo1,vo2NewScope,vo1OrigScope,i-1);
			}
		}
		/**
		 * 插入班次冲突信息
		 * @param vo1
		 * @param vo2
		 * @param setday：间隔天数
		 * @param mutexType：冲突类型
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
