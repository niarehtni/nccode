package nc.bs.hrsms.ta.empleavereg4store.add;

import java.util.Map;
import java.util.TimeZone;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.empleavereg4store.EmpLeaveRegDataChange;
import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.away.lsnr.AwaySaveProcessor;
import nc.bs.hrss.ta.leave.LeaveConsts;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.itf.ta.ILeaveRegisterInfoDisplayer;
import nc.itf.ta.ILeaveRegisterManageMaintain;
import nc.itf.ta.ILeaveRegisterQueryMaintain;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.data.RowData;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

/**
 * @author renyp
 * @date 2015-5-4
 * @ClassName功能名称：店员休假登记 新增按钮 的弹出片段的Controllor
 * @Description功能描述：功能是
 * 
 */
public class EmpLeaveApplyAddView implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;


	/**
	 * 数据集ID
	 * 
	 * @return
	 */
	protected String getDatasetId() {
		return "ds_leavereg";
	}
	
	/**
	 * beforeShow事件
	 * @param dialogEvent
	 */
	public void onBeforeShow(DialogEvent dialogEvent) {
		Dataset ds = ViewUtil.getDataset(ViewUtil.getCurrentView(),getDatasetId());
		String pk_leavereg = (String) AppLifeCycleContext.current().getApplicationContext().getAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM);
		String operate_status = (String) AppLifeCycleContext.current().getApplicationContext().getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
		if(StringUtils.isEmpty(pk_leavereg)){//用于判断新增或编辑
			Row row = ds.getEmptyRow();
			//设置默认单据来源
			row.setValue(ds.nameToIndex(LeaveRegVO.BILLSOURCE), 2);
			row.setValue(ds.nameToIndex(LeaveRegVO.PK_ORG),SessionUtil.getPk_org());//集团
			row.setValue(ds.nameToIndex(LeaveRegVO.PK_GROUP), SessionUtil.getPk_group());//组织
			
			row.setValue(ds.nameToIndex(LeaveRegVO.LEAVEHOUR), UFDouble.ZERO_DBL);
			row.setValue(ds.nameToIndex(LeaveRegVO.LACTATIONHOUR), UFDouble.ZERO_DBL);
			row.setValue(ds.nameToIndex(LeaveRegVO.USEFULDAYORHOUR), UFDouble.ZERO_DBL);
			row.setValue(ds.nameToIndex(LeaveRegVO.FREEZEDAYORHOUR), UFDouble.ZERO_DBL);
			row.setValue(ds.nameToIndex(LeaveRegVO.REALDAYORHOUR), UFDouble.ZERO_DBL);
			row.setValue(ds.nameToIndex(LeaveRegVO.RESTDAYORHOUR), UFDouble.ZERO_DBL);
			row.setValue(ds.nameToIndex(LeaveRegVO.RESTEDDAYORHOUR), UFDouble.ZERO_DBL);
			row.setValue(ds.nameToIndex(LeaveRegVO.ISLACTATION), UFBoolean.FALSE);
			row.setValue(ds.nameToIndex(LeaveRegVO.CREATIONTIME), new UFDateTime());
			row.setString(ds.nameToIndex("creator"), SessionUtil.getPk_user());
			
			ds.setCurrentKey(Dataset.MASTER_KEY);
			ds.addRow(row);
			ds.setRowSelectIndex(0);
			ds.setEnabled(true);
		}else{
			if(!"sickLeave".equals(operate_status)){
				ILeaveRegisterQueryMaintain service = NCLocator.getInstance().lookup(ILeaveRegisterQueryMaintain.class);
				try {
					LeaveRegVO vo = service.queryByPk(pk_leavereg);
					new SuperVO2DatasetSerializer().serialize(new SuperVO[]{vo}, ds, Row.STATE_NORMAL);
					ds.setCurrentKey(Dataset.MASTER_KEY);
					ds.setRowSelectIndex(0);
					Row row =  ds.getSelectedRow();
					//通过单据来源区分可否修改
					if(2==vo.getBillsource()){
						ds.setEnabled(true);
						row.setValue(ds.nameToIndex(LeaveRegVO.BILLSOURCE), 2);
					}else{//设置不显示保存按钮
						LfwView view = ViewUtil.getCurrentView();
						MenuItem saveItem = view.getViewMenus().getMenuBar("menu_operate").getItem("btnSave");
						saveItem.setVisible(false);
					}
				} catch (BusinessException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}else{
				ILeaveRegisterQueryMaintain service = NCLocator.getInstance().lookup(ILeaveRegisterQueryMaintain.class);
				try {
					LeaveRegVO vo = service.queryByPk(pk_leavereg);
					new SuperVO2DatasetSerializer().serialize(new SuperVO[]{vo}, ds, Row.STATE_NORMAL);
					ds.setCurrentKey(Dataset.MASTER_KEY);
					ds.setRowSelectIndex(0);
					Row row =  ds.getSelectedRow();
					row.setValue(ds.nameToIndex("oldleavebegintime"),row.getValue(ds.nameToIndex(LeaveRegVO.LEAVEBEGINTIME)));
					row.setValue(ds.nameToIndex("oldleaveendtime"),row.getValue(ds.nameToIndex(LeaveRegVO.LEAVEENDTIME)));
					ds.setEnabled(true);
				} catch (BusinessException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		}

	}
	
	public void onDataLoad_hrtaleavereg(DataLoadEvent dataLoadEvent) {
		
	}
	
	/**
	 * 从卡片界面回到列表界面的查询操作
	 * 
	 * @param keys
	 */
	public void pluginReSearch(Map<String, Object> keys){
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	
	/**
	 * 保存事件
	 * 
	 * @param mouseEvent
	 */
	public void onSave(MouseEvent<MenuItem> mouseEvent) {
		//CmdInvoker.invoke(new UifSaveCmdRV(getDatasetId(), null, false));
		//CmdInvoker.invoke(new RegLeaveSaveCmd(getDatasetId(), null, null, null, LeaveRegVO.class));
		//HrssSingleBillCommand();
		Dataset ds =  ViewUtil.getCurrentView().getViewModels().getDataset(getDatasetId());
		Row row = ds.getSelectedRow();
		String operate_status = (String) AppLifeCycleContext.current().getApplicationContext().getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
		//条件判断
		if (row.getValue(ds.nameToIndex(LeaveRegVO.PK_PSNDOC)) == null) {
			throw new LfwRuntimeException("请先选择人员！");
		}
		if (row.getValue(ds.nameToIndex(LeaveRegVO.PK_LEAVETYPE)) == null) {
			throw new LfwRuntimeException("请先选择休假类别！");
		}
		if (row.getValue(ds.nameToIndex(LeaveRegVO.LEAVEBEGINTIME)) == null) {
			throw new LfwRuntimeException("请先选择休假开始时间！");
		}
		if (row.getValue(ds.nameToIndex(LeaveRegVO.LEAVEENDTIME)) == null) {
			throw new LfwRuntimeException("请先选择休假结束时间！");
		}
		SuperVO  vo = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds)[0];
		LeaveRegVO leaveRegVO = new LeaveRegVO();
		String[] names = leaveRegVO.getAttributeNames();
		for(int i =0;i<names.length;i++){
			leaveRegVO.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
		}
		if(!leaveRegVO.getIslactation().booleanValue() && leaveRegVO.getLactationhour() != null){
			leaveRegVO.setLactationhour(null);
		}
		//考勤档案已经结束的人员新增档案结束日期前的数据时pk_psnorg字段为空，无法保存数据
		PsnJobVO psnjobVO = null;
		try {
			psnjobVO = (PsnJobVO) ServiceLocator.lookup(IPersistenceRetrieve.class).retrieveByPk(PsnJobVO.class, leaveRegVO.getPk_psnjob(), null);
		} catch (BusinessException e1) {
			throw new LfwRuntimeException(e1.getMessage());
		} catch (HrssException e1) {
			throw new LfwRuntimeException(e1.getMessage());
		}
		leaveRegVO.setPk_psnorg(psnjobVO.getPk_psnorg());
		ILeaveRegisterManageMaintain service = NCLocator.getInstance().lookup(ILeaveRegisterManageMaintain.class);
		try {
			
			// 保存前先校验一次，如果有单据冲突，但是是被允许的，则显示这些冲突单据，并询问用户是否保存
			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkMutextResult = null;
			try {
//				checkMutextResult = ServiceLocator.lookup(ILeaveRegisterQueryMaintain.class).check(leaveRegVO);
//				if (checkMutextResult != null) {
//					AwaySaveProcessor
//					.showConflictInfoList(
//							new BillMutexException(null,
//									checkMutextResult),
//							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
//									.getStrByID("c_ta-res",
//											"0c_ta-res0008")/*
//															 * @ res
//															 * "与下列单据有时间冲突，是否保存?"
//															 */,
//							ShopTaApplyConsts.DIALOG_CONFIRM);
//					return;
//				}
//			} catch (HrssException e) {
//				e.alert();
//			}catch (BillMutexException ex) {
//				AwaySaveProcessor.showConflictInfoList(
//						((BillMutexException) ex),
//						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
//								"c_ta-res", "0c_ta-res0007")/*
//															 * @ res
//															 * "与下列单据有时间冲突，操作不能继续"
//															 */,
//						ShopTaApplyConsts.DIALOG_ALERT);
//				return;
//			}
				checkMutextResult = ServiceLocator.lookup(ILeaveRegisterQueryMaintain.class).checkWhenSave(leaveRegVO).getMutexCheckResult();
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
			} catch (BillMutexException ex) {
				AwaySaveProcessor.showConflictInfoList(
						((BillMutexException) ex),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"c_ta-res", "0c_ta-res0007")/*
															 * @ res
															 * "与下列单据有时间冲突，操作不能继续"
															 */,
						ShopTaApplyConsts.DIALOG_ALERT);
				return;
			} catch(HrssException e1){
				e1.printStackTrace();
			}
			
			if("".equals(leaveRegVO.getPk_leavereg())||null==leaveRegVO.getPk_leavereg()){
				service.insertData(leaveRegVO);
				CommonUtil.showShortMessage("保存成功！");
			}else{
				if("sickLeave".equals(operate_status)){//判断是否销假
					String directUpdateSQL = "update tbm_leavereg set isleaveoff = 'Y', leavebegintime = '" + leaveRegVO.getLeavebegintime()
							+"', leaveendtime = '" + leaveRegVO.getLeaveendtime() + "', leavehour = '0', modifiedtime = '" + new UFDateTime() 
							+ "', modifier = '" + SessionUtil.getPk_user() + "' where pk_leavereg = '" + leaveRegVO.getPk_leavereg() + "'";
					new BaseDAO().executeUpdate(directUpdateSQL);
				}
				CommonUtil.showShortMessage("销假成功！");
			}
			// 关闭弹出页面
			CmdInvoker.invoke(new CloseWindowCmd());
			// 执行左侧快捷查询
			CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET,"closewindow"));
		} catch (BusinessException e) {
			CommonUtil.showErrorDialog(e.getMessage());
			return;
		}
	}

	
	/**
	 * 取消按钮操作
	 */
	public void onCancel(MouseEvent<MenuItem> mouseEvent) {
		// 关闭弹出页面
		CmdInvoker.invoke(new CloseWindowCmd());
	}
	
	/**
	 * 新增界面，人员编码改变事件
	 * 
	 * @param datasetCellEvent
	 */
	public void onAfterDataChage_ds_leavereg(DatasetCellEvent datasetCellEvent) {
		LfwView viewMain = ViewUtil.getCurrentView();
		Dataset ds = datasetCellEvent.getSource();
		// 字段顺序
		int colIndex = datasetCellEvent.getColIndex();
		if (colIndex != ds.nameToIndex(LeaveRegVO.PK_LEAVETYPE) && colIndex != ds.nameToIndex("pk_psnjob") 
				&&colIndex !=ds.nameToIndex(LeaveRegVO.LEAVEBEGINTIME) && colIndex!=ds.nameToIndex(LeaveRegVO.LEAVEENDTIME)) {
			return;
		}
		Row selRow = ds.getSelectedRow();
		String pk_psndoc =  (String) selRow.getValue(ds.nameToIndex("pk_psndoc"));
		if(null==pk_psndoc||"".equals(pk_psndoc)){
			return;
		}
		calculate( ds, selRow);
		if(colIndex == ds.nameToIndex("pk_psnjob")){
			// 在applicationContext中添加属性考勤档案和考勤规则
			ShopTaAppContextUtil.addTaAppForTransferContext(pk_psndoc);
			EmpLeaveRegDataChange.onAfterDataChange(ds, selRow);
		}else if (colIndex == ds.nameToIndex(LeaveRegVO.PK_LEAVETYPE)) {// 休假类别
			setLeaveTypeChage(viewMain, ds, selRow);
		}else if(colIndex ==ds.nameToIndex(LeaveRegVO.LEAVEBEGINTIME) || colIndex==ds.nameToIndex(LeaveRegVO.LEAVEENDTIME)){
			setLeaveDayOrHour(ds, selRow);
		}
	}



	/**
	 * 休假类别发生变化影响:<br/>
	 * 1.考勤单位<br/>
	 * 2.期间是否可编辑<br/>
	 * 启用往期假优先时,期间不可编辑;<br/>
	 * 未启用往期假优先时,休假类别Copy的结算方式-按月结算时,期间可编辑;<br/>
	 * 3.已休时长 |享有时长 |结余时长<br/>
	 * 4.休假总时长<br/>
	 * 5.子表休假时长<br/>
	 * 
	 * @param ds
	 * @param selRow
	 */
	private void setLeaveTypeChage(LfwView viewMain, Dataset ds, Row selRow) {
		// // 设置考勤单位
		TimeItemCopyVO timeItemCopyVO = setTimeUnitText(ds, selRow);
		// 2.期间是否可编辑, 启用往期假优先时,期间不可编辑;
		// 未启用往期假优先时,休假类别Copy的结算方式-按月结算时,期间可编辑;
		FormComp formComp = (FormComp) viewMain.getViewComponents().getComponent(LeaveConsts.PAGE_FORM_LEAVEINFO);
		if (formComp == null) {
			return;
		}
		// 考勤规则
		TimeRuleVO timeRuleVO = TaAppContextUtil.getTimeRuleVO();
		FormElement monthElem = formComp.getElementById(LeaveRegVO.LEAVEMONTH);
		if (monthElem != null) {
			if (timeRuleVO.isPreHolidayFirst()) {
				// 启用往期假优先时,期间不可编辑;
				monthElem.setEnabled(false);
			} else {
				Integer leavesetperiod = null;
				if (timeItemCopyVO != null) {
					// 未启用往期假优先时,根据休假类别Copy的结算方式设置;
					leavesetperiod = timeItemCopyVO.getLeavesetperiod();
				}
				if (leavesetperiod != null && leavesetperiod == TimeItemCopyVO.LEAVESETPERIOD_MONTH) {
					monthElem.setEnabled(true);
				} else {
					monthElem.setEnabled(false);
				}
			}
		}
		
		// 3.已休时长 |享有时长 |结余时长
		setLeaveDayOrHour(ds, selRow);

		LfwView view = ViewUtil.getCurrentView();
		Dataset dsDetail = view.getViewModels().getDataset(getDatasetId());
		// 4.休假总时长及5.子表休假时长
		RowData rowData = dsDetail.getCurrentRowData();
		if (rowData == null) {
			return;
		}
		Row[] rows = rowData.getRows();
		if (rows == null || rows.length == 0) {
			return;
		}
		
		// 调用后台计算休假总时长
		calculate( dsDetail, selRow);
	}
	
	/**
	 * 根据休假类别设置字段显示
	 * 
	 * @param timeItemCopyVO
	 * @param formComp
	 */
	public static void setElementVisible(TimeItemCopyVO timeItemCopyVO, FormComp formComp){
		if(timeItemCopyVO == null){
			return;
		}
		UFBoolean ishrssshow = timeItemCopyVO.getIshrssshow();
		FormElement realElement = formComp.getElementById(LeavehVO.REALDAYORHOUR);
		FormElement restElement = formComp.getElementById(LeavehVO.RESTDAYORHOUR);
		FormElement usefulElement = formComp.getElementById(LeavehVO.USEFULDAYORHOUR);
		
		if (realElement != null && AppUtil.getAppAttr(LeavehVO.REALDAYORHOUR) == null) {
			AppUtil.addAppAttr(LeavehVO.REALDAYORHOUR, UFBoolean.valueOf(realElement.isVisible()));
		}
		if (restElement != null && AppUtil.getAppAttr(LeavehVO.RESTDAYORHOUR) == null) {
			AppUtil.addAppAttr(LeavehVO.RESTDAYORHOUR, UFBoolean.valueOf(restElement.isVisible()));
		}
		if (usefulElement != null && AppUtil.getAppAttr(LeavehVO.USEFULDAYORHOUR) == null) {
			AppUtil.addAppAttr(LeavehVO.USEFULDAYORHOUR, UFBoolean.valueOf(usefulElement.isVisible()));
		}
		
		if(ishrssshow != null && !ishrssshow.booleanValue()){
			if(realElement != null){
				realElement.setVisible(false);
			}
			if(restElement != null){
				restElement.setVisible(false);
			}
			if(usefulElement != null){
				usefulElement.setVisible(false);
			}
		}else{
			
			if(realElement != null){
				realElement.setVisible(true && ((UFBoolean)AppUtil.getAppAttr(LeavehVO.REALDAYORHOUR)).booleanValue());
			}
			if(restElement != null){
				restElement.setVisible(true && ((UFBoolean)AppUtil.getAppAttr(LeavehVO.RESTDAYORHOUR)).booleanValue());
			}
			if(usefulElement != null){
				usefulElement.setVisible(true && ((UFBoolean)AppUtil.getAppAttr(LeavehVO.USEFULDAYORHOUR)).booleanValue());
			}
		}
	}



	/**
	 * 根据人员主键,休假类别, 年度,期间,<br/>
	 * 查询|指定考勤期间内|申请人员|选择休假类别|的 已休时长 |享有时长 |结余时长.
	 * 
	 * @param ds
	 * @param selRow
	 * 
	 */
	private void setLeaveDayOrHour(Dataset ds, Row selRow) {
		SuperVO[] superVOs = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds, selRow);
		if (superVOs == null || superVOs.length == 0) {
			setDefaultLeaveDayOrHour(ds, selRow);
			return;
		}
		String leaveYear = selRow.getString(ds.nameToIndex(LeaveRegVO.LEAVEYEAR));
		String leaveMonth = selRow.getString(ds.nameToIndex(LeaveRegVO.LEAVEMONTH));
		if (StringUtils.isEmpty(leaveYear) || StringUtils.isEmpty(leaveMonth)) {
			setDefaultLeaveDayOrHour(ds, selRow);
			return;
		}
		LeaveRegVO leaveregVO = (LeaveRegVO) superVOs[0];
		// 计算享有时间,已休时间,结余时间
		LeaveBalanceVO leaveBalanceVO = getLeaveBalanceVO(leaveregVO);
		if (leaveBalanceVO == null) {
			setDefaultLeaveDayOrHour(ds, selRow);
			return;
		}
		// 设置享有时间
		selRow.setValue(ds.nameToIndex(LeaveRegVO.REALDAYORHOUR), leaveBalanceVO.getCurdayorhour());
		// 已休时间
		selRow.setValue(ds.nameToIndex(LeaveRegVO.RESTEDDAYORHOUR), leaveBalanceVO.getYidayorhour());
		// 结余时间
		selRow.setValue(ds.nameToIndex(LeaveRegVO.RESTDAYORHOUR), leaveBalanceVO.getRestdayorhour());
		// 冻结时长
		selRow.setValue(ds.nameToIndex(LeaveRegVO.FREEZEDAYORHOUR), leaveBalanceVO.getFreezedayorhour());
		// 可用时长
		selRow.setValue(ds.nameToIndex(LeaveRegVO.USEFULDAYORHOUR), new UFDouble(leaveBalanceVO.getUsefulRestDayOrHour()));

		// 假期结算顺序号
		selRow.setValue(ds.nameToIndex(LeaveRegVO.LEAVEINDEX), leaveregVO.getLeaveindex() == null ? 1 : leaveregVO.getLeaveindex());

	}

	/**
	 * 设置 已休时长 |享有时长 |结余时长的默认值.
	 * 
	 * @param ds
	 * @param selRow
	 */
	private void setDefaultLeaveDayOrHour(Dataset ds, Row selRow) {
		// 设置享有时间
		selRow.setValue(ds.nameToIndex(LeavehVO.REALDAYORHOUR), UFDouble.ZERO_DBL);
		// 已休时间
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTEDDAYORHOUR), UFDouble.ZERO_DBL);
		// 结余时间
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTDAYORHOUR), UFDouble.ZERO_DBL);
		// 冻结时长
		selRow.setValue(ds.nameToIndex(LeavehVO.FREEZEDAYORHOUR), UFDouble.ZERO_DBL);
		// 可用时长
		selRow.setValue(ds.nameToIndex(LeavehVO.USEFULDAYORHOUR), UFDouble.ZERO_DBL);
		// 假期结算顺序号
		selRow.setValue(ds.nameToIndex(LeavehVO.LEAVEINDEX), 1);
	}

	/**
	 * 计算享有时间,已休时间,结余时间
	 * 
	 * @param leavehVO
	 * @return
	 */
	private LeaveBalanceVO getLeaveBalanceVO(LeaveRegVO leaveRegVO) {
		Map<String, LeaveBalanceVO> balanceMap;
		LeaveBalanceVO	leaveBalanceVO = null;
		try {
			balanceMap = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class)
					.queryAndCalLeaveBalanceVO(leaveRegVO.getPk_org(), leaveRegVO);
			leaveBalanceVO = MapUtils.isEmpty(balanceMap)?null:balanceMap.get(leaveRegVO.getPk_psnorg()+leaveRegVO.getPk_leavetype()+leaveRegVO.getLeaveyear());
		} catch (BusinessException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		return leaveBalanceVO;
	}

	/**
	 * 设置考勤单据相关字段的显示
	 * 
	 * @param view
	 * @param ds
	 * @param selRow
	 */
	public static TimeItemCopyVO setTimeUnitText(Dataset ds, Row masterRow) {

		// 申请单据所属组织
		String pk_org = masterRow.getString(ds.nameToIndex(LeaveRegVO.PK_ORG));

		// 获得休假类别PK
		String pk_leavetype = masterRow.getString(ds.nameToIndex(LeaveRegVO.PK_LEAVETYPE));

		TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(pk_org, pk_leavetype);
		Integer timeitemunit = null;
		if (timeItemCopyVO != null) {
			// 设置休假类别copy的PK
			masterRow.setValue(ds.nameToIndex(LeaveRegVO.PK_LEAVETYPECOPY), timeItemCopyVO.getPk_timeitemcopy());
			timeitemunit = timeItemCopyVO.getTimeitemunit();
		} else {
			// 设置休假类别copy的PK
			masterRow.setValue(ds.nameToIndex(LeaveRegVO.PK_LEAVETYPECOPY), null);
		}
		// 休假信息
		LfwView view = AppLifeCycleContext.current().getViewContext().getView();
		FormComp form = (FormComp) view.getViewComponents().getComponent("headTab_card_leaveinf_form");
		// 休假总时长
		FormElement elem = form.getElementById(LeaveRegVO.LEAVEHOUR);
		String text = "休假时长";
		setText(timeitemunit, elem, text);
		// 已休时长
		elem = form.getElementById(LeaveRegVO.RESTEDDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0063")/*
																								 * @
																								 * res
																								 * "已休时长"
																								 */;
		setText(timeitemunit, elem, text);
		// 享有时长
		elem = form.getElementById(LeaveRegVO.REALDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0064")/*
																								 * @
																								 * res
																								 * "享有时长"
																								 */;
		setText(timeitemunit, elem, text);
		// 结余时长
		elem = form.getElementById(LeaveRegVO.RESTDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0065")/*
																								 * @
																								 * res
																								 * "结余时长"
																								 */;
		setText(timeitemunit, elem, text);

		// 冻结时长
		elem = form.getElementById(LeaveRegVO.FREEZEDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0066")/*
																								 * @
																								 * res
																								 * "冻结时长"
																								 */;
		setText(timeitemunit, elem, text);

		// 可用时长
		elem = form.getElementById(LeaveRegVO.USEFULDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0067")/*
																								 * @
																								 * res
																								 * "可用时长"
																								 */;
		setText(timeitemunit, elem, text);
		return timeItemCopyVO;
	}

	@SuppressWarnings("deprecation")
	private static void setText(Integer timeitemunit, FormElement elem, String text) {
		if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_DAY == timeitemunit) {// 天
			elem.setLabel(text + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0001")/*
																													 * @
																													 * res
																													 * "(天)"
																													 */);
		} else if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_HOUR == timeitemunit) {// 小时
			elem.setLabel(text + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0002")/*
																													 * @
																													 * res
																													 * "(小时)"
																													 */);
		} else {
			elem.setLabel(text);
		}
	}

	/**
	 * 根据休假类别PK和组织PK, 获得休假类别copy的PK
	 * 
	 * @param pk_org
	 * @param pk_leavetype
	 * @return
	 */
	public static TimeItemCopyVO getTimeItemCopyVO(String pk_org, String pk_leavetype) {
		TimeItemCopyVO timeItemCopyVO = null;
		// 查询休假类别copy的PK
		try {
			ITimeItemQueryService service = ServiceLocator.lookup(ITimeItemQueryService.class);
			timeItemCopyVO = service.queryCopyTypesByDefPK(pk_org, pk_leavetype, TimeItemVO.LEAVE_TYPE);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return timeItemCopyVO;
	}

	/**
	 * 调用后台计算休假总时长
	 * 
	 * @param ds
	 * @param dsDetail
	 * @param rowMaster
	 */
	private void calculate(Dataset dsDetail, Row rowMaster) {
		//序列化VO
		SuperVO  vo = new Dataset2SuperVOSerializer<SuperVO>().serialize(dsDetail)[0];
		LeaveRegVO leaveRegVO = new LeaveRegVO();
		String[] names = leaveRegVO.getAttributeNames();
		for(int i =0;i<names.length;i++){
			leaveRegVO.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
		}
		ILeaveRegisterInfoDisplayer displayer = NCLocator.getInstance().lookup(ILeaveRegisterInfoDisplayer.class);
		try {
			leaveRegVO = displayer.calculate(leaveRegVO, TimeZone.getDefault());
		} catch (BusinessException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		Row row = dsDetail.getSelectedRow();
		row.setValue(dsDetail.nameToIndex(LeaveRegVO.LEAVEHOUR), leaveRegVO.getLeavehour());
		row.setValue(dsDetail.nameToIndex(LeaveRegVO.RESTEDDAYORHOUR), leaveRegVO.getRestdayorhour());
		row.setValue(dsDetail.nameToIndex(LeaveRegVO.REALDAYORHOUR), leaveRegVO.getRealdayorhour());
		row.setValue(dsDetail.nameToIndex(LeaveRegVO.RESTDAYORHOUR), leaveRegVO.getRestdayorhour());
		row.setValue(dsDetail.nameToIndex(LeaveRegVO.FREEZEDAYORHOUR), leaveRegVO.getFreezedayorhour());
		row.setValue(dsDetail.nameToIndex(LeaveRegVO.USEFULDAYORHOUR), leaveRegVO.getUsefuldayorhour());
		row.setValue(dsDetail.nameToIndex(LeaveRegVO.LEAVEBEGINDATE), leaveRegVO.getLeavebegindate());
		row.setValue(dsDetail.nameToIndex(LeaveRegVO.LEAVEENDDATE), leaveRegVO.getLeaveenddate());
		row.setValue(dsDetail.nameToIndex(LeaveRegVO.LEAVEBEGINTIME), leaveRegVO.getLeavebegintime());
		row.setValue(dsDetail.nameToIndex(LeaveRegVO.LEAVEENDTIME), leaveRegVO.getLeaveendtime());
	}

	
}