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
 * @ClassName�������ƣ���Ա�ݼٵǼ�viewҳ�������
 * @Description����������������
 * 
 */
public class EmpLeaveReg4StoreViewCtrl extends StoreListBaseView implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;


	
	/**
	 * @author renyp
	 * @date 2015-4-29
	 * @Description���������������������ͱ���
	 * 
	 */
	@Override
	protected String getBillTypeCode() {
		return LeaveConsts.BILL_TYPE_CODE;
	}

	/**
	 * @author renyp
	 * @date 2015-4-29
	 * @Description�������������� �����ݼ�ID
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
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","�ݼٵǼ�")/*@res "�ݼ���������"*/;
		} else if (HrssConsts.POPVIEW_OPERATE_EDIT.equals(operateflag)) {
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0069")/*@res "�ݼ������޸�"*/;
		} else if (HrssConsts.POPVIEW_OPERATE_VIEW.equals(operateflag)) {
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0070")/*@res "�ݼ�������ϸ"*/;
		} else if (HrssConsts.POPVIEW_OPERATE_COPY.equals(operateflag)) {
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0071")/*@res "�ݼ����븴��"*/;
		}
		return null;
	}

	/**
	 * @author renyp
	 * @date 2015-4-24
	 * @Description�������������������� ��ȡVo
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
	 * @Description�������������������� ���ݼ������¼�
	 * 
	 */
	public void onDataLoad_hrtaleave(DataLoadEvent dataLoadEvent) throws BusinessException {
		super.onDataLoad(dataLoadEvent);
		
	}
	/**
	 *��ť�¼��ַ�
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void btnAction(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		String id= mouseEvent.getSource().getId();
		//������ť
		if(id.equals("btnAdd")){
			this.btnAdd(mouseEvent);
			//��������ٰ�ť
		}else if(id.equals("btnAddBreastLeave")){
			this.btnAddBreastLeave(mouseEvent);
		
			//����������ť
	    }else if(id.equals("btnAddBatch")){
		this.btnAddBatch(mouseEvent);
	
		//���ٰ�ť
       }else if(id.equals("btnSickLeave")){
	     this.btnSickLeave(mouseEvent);
       
	     //ɾ����ť
	   }else if(id.equals("btnDel")){
		this.btnDel(mouseEvent);
	}

				
	}
	/**
	 *��ť�¼� ������
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void btnAdd(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		//����
      this.addBill(mouseEvent);
	}
	/**
	 *��ť�¼� ���������
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void btnAddBreastLeave(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		this.addFeed(mouseEvent);
		
	}
	/**
	 *��ť�¼�  ��������
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void btnAddBatch(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
//		mouseEvent.getSource().getId();
		this.batchAdd(mouseEvent);
	}
	/**
	 *��ť�¼�  ����
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void btnSickLeave(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		this.sickLeave(mouseEvent);
	}
	
	/**
	 *��ť�¼�  ɾ��
	 * @param mouseEvent
	 */
	public void btnDel(MouseEvent<MenuItem> mouseEvent) {
		if(!AppInteractionUtil.showConfirmDialog("ɾ��ȷ�Ͽ�", "�Ƿ�ȷ��ɾ��")){
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
			//У���Ƿ��濼���ڼ���
			PeriodServiceFacade.checkDateScope(leaveRegVO.getPk_org(), leaveRegVO.getBegindate(), leaveRegVO.getEnddate());
			dservice.deleteData(leaveRegVO);
			// ִ������ݲ�ѯ
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		} catch (BusinessException e) {
			throw new LfwRuntimeException(e.getMessage());
		}
	}

	@Override
	protected SuperVO[] getVOs(FromWhereSQL fromWhereSQL) {
		// TODO �Զ����ɵķ������
		ILeaveRegisterQueryMaintain maintain = NCLocator.getInstance().lookup(ILeaveRegisterQueryMaintain.class);
		LeaveRegVO[]regVos=null;
		try {
			regVos=maintain.queryByCond(SessionUtil.getLoginContext(), fromWhereSQL, null);
		} catch (BusinessException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		return regVos;
	}

	/**
	 * @author renyp
	 * @date 2015-4-29
	 * @Description�������������� �鿴��ϸ����
	 * 
	 */
	public void showDetail(ScriptEvent scriptEvent) {
		// ����
		String primaryKey = AppLifeCycleContext.current().getParameter("dsMain_primaryKey");
		String operate_status = HrssConsts.POPVIEW_OPERATE_VIEW;
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, primaryKey);
		ILeaveRegisterQueryMaintain maintain = NCLocator.getInstance().lookup(ILeaveRegisterQueryMaintain.class);
		LeaveRegVO[]regVos=null;
		try {
			regVos=maintain.queryByPks(new String[]{primaryKey});
		} catch (BusinessException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		UFBoolean islactation = regVos[0].getIslactation();
		if(islactation.booleanValue()){
			CommonUtil.showWindowDialog("EmpLeaveRegFeedWin", "�ݼٵǼǱ༭", "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
		}else{
			CommonUtil.showWindowDialog(getPopWindowId(), "�ݼٵǼǱ༭", "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
		}
	}
}