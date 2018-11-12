package nc.bs.hrsms.ta.empleavereg4store.win;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.empleavereg4store.StoreListBaseView;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.leave.LeaveConsts;
import nc.itf.ta.ILeaveRegisterManageMaintain;
import nc.itf.ta.ILeaveRegisterQueryMaintain;
import nc.itf.ta.PeriodServiceFacade;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.ta.leave.LeaveRegVO;

/**
 * @author renyp
 * @date 2015-4-29
 * @ClassName功能名称：店员休假登记view页面控制类
 * @Description功能描述：功能是
 * 
 */
public class EmpLeaveReg4StoreViewCtrl extends StoreListBaseView implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;


	
	/**
	 * @author renyp
	 * @date 2015-4-29
	 * @Description方法功能描述：单据类型编码
	 * 
	 */
	@Override
	protected String getBillTypeCode() {
		return LeaveConsts.BILL_TYPE_CODE;
	}

	/**
	 * @author renyp
	 * @date 2015-4-29
	 * @Description方法功能描述： 主数据集ID
	 * 
	 */
	@Override
	protected String getDatasetId() {
		return "ds_leavereg";
	}

	@Override
	protected String getPopWindowId() {
		return "EmpLeaveRegAddWin";
	}

	@Override
	protected String getPopWindowTitle(String operateflag) {
		if (HrssConsts.POPVIEW_OPERATE_ADD.equals(operateflag)) {
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","休假登记")/*@res "休假申请新增"*/;
		} else if (HrssConsts.POPVIEW_OPERATE_EDIT.equals(operateflag)) {
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0069")/*@res "休假申请修改"*/;
		} else if (HrssConsts.POPVIEW_OPERATE_VIEW.equals(operateflag)) {
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0070")/*@res "休假申请详细"*/;
		} else if (HrssConsts.POPVIEW_OPERATE_COPY.equals(operateflag)) {
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0071")/*@res "休假申请复制"*/;
		}
		return null;
	}

	/**
	 * @author renyp
	 * @date 2015-4-24
	 * @Description方法功能描述：作用是 获取Vo
	 * 
	 */
	@Override
	protected AggregatedValueObject[] getAggVOs(FromWhereSQL fromWhereSQL) {
			return null;

	}
	/**
	 * @author renyp
	 * @throws BusinessException 
	 * @date 2015-4-24
	 * @Description方法功能描述：作用是 数据集加载事件
	 * 
	 */
	public void onDataLoad_hrtaleave(DataLoadEvent dataLoadEvent) throws BusinessException {
		super.onDataLoad(dataLoadEvent);
		
	}
	/**
	 *按钮事件分发
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void btnAction(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		String id= mouseEvent.getSource().getId();
		//新增按钮
		if(id.equals("btnAdd")){
			this.btnAdd(mouseEvent);
			//新增哺乳假按钮
		}else if(id.equals("btnAddBreastLeave")){
			this.btnAddBreastLeave(mouseEvent);
		
			//批量新增按钮
	    }else if(id.equals("btnAddBatch")){
		this.btnAddBatch(mouseEvent);
	
		//销假按钮
       }else if(id.equals("btnSickLeave")){
	     this.btnSickLeave(mouseEvent);
       
	     //删除按钮
	   }else if(id.equals("btnDel")){
		this.btnDel(mouseEvent);
	}

				
	}
	/**
	 *按钮事件 新增假
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void btnAdd(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		//父类
      this.addBill(mouseEvent);
	}
	/**
	 *按钮事件 新增哺乳假
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void btnAddBreastLeave(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		this.addFeed(mouseEvent);
		
	}
	/**
	 *按钮事件  批量新增
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void btnAddBatch(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
//		mouseEvent.getSource().getId();
		this.batchAdd(mouseEvent);
	}
	/**
	 *按钮事件  销假
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void btnSickLeave(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		this.sickLeave(mouseEvent);
	}
	
	/**
	 *按钮事件  删除
	 * @param mouseEvent
	 */
	public void btnDel(MouseEvent<MenuItem> mouseEvent) {
		if(!AppInteractionUtil.showConfirmDialog("删除确认框", "是否确认删除")){
			return;
		}
		Dataset ds =  ViewUtil.getCurrentView().getViewModels().getDataset(getDatasetId());
		SuperVO  vo = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds)[0];
		LeaveRegVO leaveRegVO = new LeaveRegVO();
		String[] names = leaveRegVO.getAttributeNames();
		for(int i =0;i<names.length;i++){
			leaveRegVO.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
		}
		ILeaveRegisterManageMaintain dservice = NCLocator.getInstance().lookup(ILeaveRegisterManageMaintain.class);
		try {
			//校验是否封存考勤期间内
			PeriodServiceFacade.checkDateScope(leaveRegVO.getPk_org(), leaveRegVO.getBegindate(), leaveRegVO.getEnddate());
			dservice.deleteData(leaveRegVO);
			// 执行左侧快捷查询
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		} catch (BusinessException e) {
			throw new LfwRuntimeException(e.getMessage());
		}
	}

	@Override
	protected SuperVO[] getVOs(FromWhereSQL fromWhereSQL) {
		// TODO 自动生成的方法存根
		ILeaveRegisterQueryMaintain maintain = NCLocator.getInstance().lookup(ILeaveRegisterQueryMaintain.class);
		LeaveRegVO[]regVos=null;
		try {
			regVos=maintain.queryByCond(SessionUtil.getLoginContext(), fromWhereSQL, null);
		} catch (BusinessException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return regVos;
	}

	/**
	 * @author renyp
	 * @date 2015-4-29
	 * @Description方法功能描述： 查看详细操作
	 * 
	 */
	public void showDetail(ScriptEvent scriptEvent) {
		// 主键
		String primaryKey = AppLifeCycleContext.current().getParameter("dsMain_primaryKey");
		String operate_status = HrssConsts.POPVIEW_OPERATE_VIEW;
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, primaryKey);
		ILeaveRegisterQueryMaintain maintain = NCLocator.getInstance().lookup(ILeaveRegisterQueryMaintain.class);
		LeaveRegVO[]regVos=null;
		try {
			regVos=maintain.queryByPks(new String[]{primaryKey});
		} catch (BusinessException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		UFBoolean islactation = regVos[0].getIslactation();
		if(islactation.booleanValue()){
			CommonUtil.showWindowDialog("EmpLeaveRegFeedWin", "休假登记编辑", "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
		}else{
			CommonUtil.showWindowDialog(getPopWindowId(), "休假登记编辑", "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
		}
	}
}