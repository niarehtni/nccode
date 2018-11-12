package nc.bs.hrsms.ta.sss.overtime.ctrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.sss.common.ShopSetMenuItemVisible;
import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrsms.ta.sss.common.ShopTaPeriodValUtils;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.away.lsnr.AwaySaveProcessor;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.ta.IOvertimeApplyQueryMaintain;
import nc.itf.ta.IOvertimeRegisterInfoDisplayer;
import nc.itf.ta.IOvertimeRegisterManageMaintain;
import nc.itf.ta.IOvertimeRegisterQueryMaintain;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.overtime.OvertimeCommonVO;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;
import nc.vo.util.remotecallcombination.IRemoteCallCombinatorService;
import nc.vo.util.remotecallcombination.RemoteCallInfo;
import nc.vo.util.remotecallcombination.RemoteCallResult;

import org.apache.commons.lang.StringUtils;

public class ShopOverTimeRegCardView  implements IController{
	
	private IOvertimeRegisterInfoDisplayer regInfoDisplayer;
	
	protected String getDatasetId() {
		// TODO Auto-generated method stub
		return "hrtaovertimereg";
	}
	/**
	 * beforeShow事件
	 * @param dialogEvent
	 */
	public void onBeforeShow(DialogEvent dialogEvent) {
		Dataset ds = ViewUtil.getDataset(ViewUtil.getCurrentView(),getDatasetId());
		
		String pk_tbm_overtimereg = (String)AppLifeCycleContext.current().getApplicationContext().getAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM);
		AppLifeCycleContext.current().getApplicationContext().removeAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM);
		FormComp from = (FormComp) ViewUtil.getCurrentView().getViewComponents().getComponent("headTab_card_overtimeinf_form");
//		boolean isNeedCheck = false;
//		try {
//			isNeedCheck = NCLocator.getInstance().lookup(IOvertimeApplyQueryMaintain.class).isCanCheck(vo);
//		} catch (BusinessException e1) {
//			Logger.error(e1.getMessage(), e1);
//		}
		from.getElementById("isneedcheck").setEnabled(false);
		if(StringUtils.isEmpty(pk_tbm_overtimereg)){
			Row row = ds.getEmptyRow();
			row.setString(ds.nameToIndex("pk_org"), SessionUtil.getPk_org());
		    row.setString(ds.nameToIndex("pk_group"), SessionUtil.getPk_group());
		    row.setString(ds.nameToIndex("creator"), SessionUtil.getPk_user());
		    // 创建时间
		 	row.setValue(ds.nameToIndex("creationtime"), new UFDateTime());
			row.setValue(ds.nameToIndex("deduct"), new Integer(0));
			row.setValue(ds.nameToIndex("overtimehour"), UFDouble.ZERO_DBL);
			row.setValue(ds.nameToIndex("acthour"), UFDouble.ZERO_DBL);
			row.setValue(ds.nameToIndex("diffhour"), UFDouble.ZERO_DBL);
			row.setValue(ds.nameToIndex("billsource"), ICommonConst.BILL_SOURCE_REG);
			row.setValue(ds.nameToIndex("isneedcheck"), UFBoolean.FALSE);
			row.setValue(ds.nameToIndex(OvertimeRegVO.TORESTHOUR), UFDouble.ZERO_DBL);
			ds.setCurrentKey(Dataset.MASTER_KEY);
			ds.addRow(row);
			ds.setRowSelectIndex(0);
			ds.setEnabled(true);
		}else{
			OvertimeRegVO vo = getOverTimeRegVOByPK(pk_tbm_overtimereg);
			new SuperVO2DatasetSerializer().serialize(new SuperVO[]{vo}, ds, Row.STATE_NORMAL);
			ds.setCurrentKey(Dataset.MASTER_KEY);
			ds.setRowSelectIndex(0);
			if(ICommonConst.BILL_SOURCE_REG != vo.getBillsource().intValue()){
				ShopSetMenuItemVisible.setMenuItemVisible("menu_operate", "btnSave");
				ds.setEnabled(false);
			}else{
				ds.setEnabled(true);
			}
			ds.setEnabled(true);
		}
		
	}
	/**
	 * 根据pk获取OvertimeRegVO
	 * @param pk
	 * @return VO
	 */
	private OvertimeRegVO getOverTimeRegVOByPK(String pk){
		OvertimeRegVO vo = null;
		try {
			vo = NCLocator.getInstance().lookup(IOvertimeRegisterQueryMaintain.class).queryByPk(pk);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vo;
	}
	
	/**
	 * onAfterDataChange事件
	 * 处理值变化
	 * @param datasetCellEvent
	 */
	public void onAfterDataChange(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		// 字段顺序
		int colIndex = datasetCellEvent.getColIndex();
		if(colIndex != ds.nameToIndex(OvertimeRegVO.PK_PSNJOB)
				&&	colIndex != ds.nameToIndex(OvertimeRegVO.PK_OVERTIMETYPE)
				&&	colIndex != ds.nameToIndex("overtimebegintime")
				&&	colIndex != ds.nameToIndex("overtimeendtime")
				&&	colIndex != ds.nameToIndex("deduct")
				&&  colIndex != ds.nameToIndex("isneedcheck")){
			return;
		}
		Row row = ds.getSelectedRow();
		if(row == null ){
			return;
		}
		if(colIndex == ds.nameToIndex(OvertimeRegVO.PK_PSNJOB)){
			String pk_psndoc  = (String) row.getValue(ds.nameToIndex("pk_psndoc"));
			ShopTaAppContextUtil.addTaAppForTransferContext(pk_psndoc);
			ShopOverTimeAfterDataChange.onAfterDataChange(ds, row);
		}
		OvertimeRegVO  vo =  new Dataset2SuperVOSerializer<OvertimeRegVO>().serialize(ds)[0];
		if(vo.getPk_psnjob() == null){
			return;
		}
		if(colIndex == ds.nameToIndex(OvertimeRegVO.PK_OVERTIMETYPE)){
			row.setValue(ds.nameToIndex(OvertimeRegVO.PK_OVERTIMETYPECOPY), row.getValue(ds.nameToIndex(OvertimeRegVO.PK_OVERTIMETYPE)));
		}
//		OvertimeRegVO vo = new OvertimeRegVO();
		FormComp from = (FormComp) ViewUtil.getCurrentView().getViewComponents().getComponent("headTab_card_overtimeinf_form");
		try {
			vo = (OvertimeRegVO) getInfoDisplayer().calculate(vo, TimeZone.getDefault());
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		row.setValue(ds.nameToIndex("overtimebegintime"), vo.getScope_start_datetime());
		row.setValue(ds.nameToIndex("overtimeendtime"), vo.getScope_end_datetime());
		row.setValue(ds.nameToIndex("deduct"), vo.getDeduct());
		row.setValue(ds.nameToIndex("overtimehour"), vo.getOvertimehour());
		row.setValue(ds.nameToIndex("acthour"), vo.getActhour());
		row.setValue(ds.nameToIndex("diffhour"), vo.getDiffhour());
		// 修改了是否允许校验
//		if(colIndex == ds.nameToIndex("isneedcheck")){
			UFBoolean isCheck = (UFBoolean) row.getValue(ds.nameToIndex("isneedcheck"));
			boolean isNeedCheck  = isCheck.booleanValue();
			//如果修改为不允许校验则不需要判断
			if(!isNeedCheck)
				return;
			OvertimeRegVO  vo1 =  new Dataset2SuperVOSerializer<OvertimeRegVO>().serialize(ds)[0];
			if(vo1.getPk_psnjob()==null||vo1.getPk_overtimetype()==null||vo1.getOvertimebegintime()==null||vo1.getOvertimeendtime()==null){
				row.setValue(ds.nameToIndex("isneedcheck"), UFBoolean.FALSE);
				return;
			}
			//调用后台判断是否可校验
			isNeedCheck = false;
			try {
				isNeedCheck = NCLocator.getInstance().lookup(IOvertimeApplyQueryMaintain.class).isCanCheck(vo);
			} catch (BusinessException e1) {
				Logger.error(e1.getMessage(), e1);
			}
			from.getElementById("isneedcheck").setEnabled(isNeedCheck);
			row.setValue(ds.nameToIndex("isneedcheck"), UFBoolean.valueOf(isNeedCheck));
//		}
	}

	/**
	 * 保存
	 * @param mouseEvent
	 */
	public void onSave(MouseEvent<MenuItem> mouseEvent){
		
		Dataset ds =  ViewUtil.getCurrentView().getViewModels().getDataset(getDatasetId());
		Row row = ds.getSelectedRow();
		//条件判断
		if (row.getValue(ds.nameToIndex(OvertimeRegVO.PK_PSNJOB)) == null) {
			throw new LfwRuntimeException("请先选择人员！");
		}
		if (row.getValue(ds.nameToIndex(OvertimeRegVO.PK_OVERTIMETYPE)) == null) {
			throw new LfwRuntimeException("请先选择加班类别！");
		}
		SuperVO  vo = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds)[0];
		OvertimeRegVO  regVO = new OvertimeRegVO();
//		OvertimeRegVO  regVO =  (OvertimeRegVO) vo;
		IOvertimeRegisterManageMaintain service = NCLocator.getInstance().lookup(IOvertimeRegisterManageMaintain.class);
		try {
			
			String[] names = regVO.getAttributeNames();
			for(int i =0;i<names.length;i++){
				regVO.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
			}
			//考勤档案已经结束的人员新增档案结束日期前的数据时pk_psnorg字段为空，无法保存数据
			PsnJobVO psnjobVO = null;
			try {
				psnjobVO = (PsnJobVO) ServiceLocator.lookup(IPersistenceRetrieve.class).retrieveByPk(PsnJobVO.class, regVO.getPk_psnjob(), null);
			} catch (BusinessException e1) {
				throw new LfwRuntimeException(e1.getMessage());
			} catch (HrssException e1) {
				throw new LfwRuntimeException(e1.getMessage());
			}
			regVO.setPk_psnorg(psnjobVO.getPk_psnorg());
			regVO.setBegindate(new UFLiteralDate(regVO.getOvertimebegintime().toString()));
			regVO.setEnddate(new UFLiteralDate(regVO.getOvertimeendtime().toString()));
			regVO.setOvertimebegindate(new UFLiteralDate(regVO.getOvertimebegintime().toString()));
			regVO.setOvertimeenddate(new UFLiteralDate(regVO.getOvertimeendtime().toString()));
			
			TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(regVO.getPk_org(), regVO.getPk_overtimetype());
			if (timeItemCopyVO != null) {
				regVO.setPk_overtimetypecopy(timeItemCopyVO.getPk_timeitemcopy());
			}else{
				regVO.setPk_overtimetypecopy(null);
			}
			if(regVO.getActhour().doubleValue()<0.001){
				throw new BusinessException(ResHelper.getString("6017overtime","06017overtime0062"/*@res"单据时长不能为0"*/));
			}
			// 保存前先校验一次，如果有单据冲突，但是是被允许的，则显示这些冲突单据，并询问用户是否保存
			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkMutextResult = null;
			try {
				checkMutextResult = ServiceLocator.lookup(IOvertimeRegisterQueryMaintain.class).check(regVO);
				if (checkMutextResult != null) {
					AwaySaveProcessor
					.showConflictInfoList(
							new BillMutexException(null,
									checkMutextResult),
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("c_ta-res",
											"0c_ta-res0008")/*
															 * @ res
															 * "与下列单据有时间冲突，是否保存?"
															 */,
							ShopTaApplyConsts.DIALOG_CONFIRM);
					return;
				}
			} catch (HrssException e) {
				e.alert();
			}catch (BillMutexException ex) {
				AwaySaveProcessor.showConflictInfoList(
						((BillMutexException) ex),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"c_ta-res", "0c_ta-res0007")/*
															 * @ res
															 * "与下列单据有时间冲突，操作不能继续"
															 */,
						ShopTaApplyConsts.DIALOG_ALERT);
				return;
				
			}
			//校验
			getCheckResult(regVO);
			//判断考勤期间是否已封存
			ShopTaPeriodValUtils.getPeriodVal(regVO.getPk_org(), new OvertimeRegVO[]{regVO});
			
			String pk = regVO.getPrimaryKey();
			if(StringUtils.isEmpty(pk)){
				service.insertData(regVO);
			}else{
				service.updateData(regVO);
			}
			CommonUtil.showShortMessage("保存成功！");
			// 关闭弹出页面
			CmdInvoker.invoke(new CloseWindowCmd());
			// 执行左侧快捷查询
			CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET,"closewindow"));
		} catch (BusinessException e) {
			new HrssException(e.getMessage()).alert();
			//e.printStackTrace();
		}
	}
	/**
	 * 取消按钮操作
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onCancel(MouseEvent mouseEvent) {
		// 关闭弹出页面
		CmdInvoker.invoke(new CloseWindowCmd());
	}
	
	public IOvertimeRegisterInfoDisplayer getInfoDisplayer() {
		if(regInfoDisplayer==null)
			regInfoDisplayer = NCLocator.getInstance().lookup(IOvertimeRegisterInfoDisplayer.class);
		return regInfoDisplayer;
	}
	/**
	 * 
	 * @param regvo
	 * @throws BusinessException
	 */
	private  void getCheckResult(OvertimeRegVO regvo) throws BusinessException{
		List<RemoteCallInfo> remoteList = new ArrayList<RemoteCallInfo>();
		//时长校验
		RemoteCallInfo checkLengthRemote = new RemoteCallInfo();
		checkLengthRemote.setClassName(IOvertimeApplyQueryMaintain.class.getName());
		checkLengthRemote.setMethodName("checkOvertimeLength");
		checkLengthRemote.setParamTypes(new Class[]{String.class, OvertimeCommonVO[].class});
		checkLengthRemote.setParams(new Object[]{regvo.getPk_org(), new OvertimeCommonVO[]{regvo}});
		remoteList.add(checkLengthRemote);
		
		//校验标识
		RemoteCallInfo checkFlag = new RemoteCallInfo();
		checkFlag.setClassName(IOvertimeApplyQueryMaintain.class.getName());
		checkFlag.setMethodName("checkIsNeed");
		checkFlag.setParamTypes(new Class[]{String.class, OvertimeCommonVO[].class});
		checkFlag.setParams(new Object[]{regvo.getPk_org(), new OvertimeCommonVO[]{regvo}});
		remoteList.add(checkFlag);
		
		//含假日的加班类型和加班规则定义的加班类型的一致性校验
		RemoteCallInfo checkHolidayRemote = new RemoteCallInfo();
		checkHolidayRemote.setClassName(IOvertimeApplyQueryMaintain.class.getName());
		checkHolidayRemote.setMethodName("checkOverTimeHolidayMsg");
		checkHolidayRemote.setParamTypes(new Class[]{String.class, OvertimeCommonVO[].class});
		checkHolidayRemote.setParams(new Object[]{regvo.getPk_org(), new OvertimeCommonVO[]{regvo}});
		remoteList.add(checkHolidayRemote);
		//打包执行
		List<RemoteCallResult> returnList = NCLocator.getInstance().lookup(IRemoteCallCombinatorService.class).doRemoteCall(remoteList);
		if(returnList.isEmpty())
			return;
		RemoteCallResult[] returns = returnList.toArray(new RemoteCallResult[0]);
		
		String checkLength = (String) returns[0].getResult();
		if(!StringUtils.isBlank(checkLength)){
			throw new LfwRuntimeException(checkLength);
		}
		String checkFlagReslut = (String)returns[0].getResult();
		if(!StringUtils.isBlank(checkFlagReslut)){
			throw new LfwRuntimeException(checkFlagReslut);
		}
		String holidayResult = (String) returns[2].getResult();
		if(holidayResult!=null){
			throw new LfwRuntimeException(holidayResult);
		}
	}
	
	/**
	 * 根据加班类别PK和组织PK, 获得加班类别copy的PK
	 * 
	 * @param pk_org
	 * @param pk_leavetype
	 * @return
	 */
	public static TimeItemCopyVO getTimeItemCopyVO(String pk_org, String pk_overtimetype) {
		TimeItemCopyVO timeItemCopyVO = null;
		// 查询休假类别copy的PK
		try {
			ITimeItemQueryService service = ServiceLocator.lookup(ITimeItemQueryService.class);
			timeItemCopyVO = service.queryCopyTypesByDefPK(pk_org, pk_overtimetype, TimeItemVO.OVERTIME_TYPE);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return timeItemCopyVO;
	}
}
