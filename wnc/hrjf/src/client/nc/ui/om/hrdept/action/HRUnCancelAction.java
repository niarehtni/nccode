package nc.ui.om.hrdept.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.om.IDeptAdjustService;
import nc.itf.om.IDeptManageService;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.om.hrdept.model.DeptAppModel;
import nc.ui.om.hrdept.view.modification.DeptUnCancelDialog;
import nc.ui.org.editor.CreateVersionDialog;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.model.IAppModelDataManagerEx;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.DeptHistoryVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.om.orginfo.HRAdminOrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;

/**
 * 取消撤销部门Action<br>
 *
 * @author zhangdd
 *
 */
public class HRUnCancelAction extends HrAction {

	/** UID */
	private static final long serialVersionUID = 1L;

	private IAppModelDataManagerEx dataManager = null;

	public HRUnCancelAction() {
		super();
		putValue(INCAction.CODE, "HRUnCancel");
		setBtnName(ResHelper.getString("6005dept","06005dept0143")/*@res "取消撤销"*/);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_J, Event.ALT_MASK));
		// setBtnChinaName("取消撤销");
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("6005dept","06005dept0144")+"(Alt+J)"/*@res "取消撤销(Alt+J)"*/);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		checkDataPermission();
		AggHRDeptVO aggDeptVO = (AggHRDeptVO) getModel().getSelectedData();
		HRDeptVO deptVO = (HRDeptVO)((AggHRDeptVO) getModel().getSelectedData()).getParentVO();
		
		//check部门所属行政组织是否已撤销
		checkAdminOrgCancel(deptVO.getPk_org());
		
		DeptUnCancelDialog uncancelDlg = new DeptUnCancelDialog(getEntranceUI(), ResHelper.getString("6005dept","06005dept0145")/*@res "取消撤销部门"*/, deptVO);
		if (UIDialog.ID_OK == uncancelDlg.showModal()) {
			// 得到DeptHistoryVO
			DeptHistoryVO historyVO = uncancelDlg.buildDeptHistoryVO();
			//如果成立時間大於當前時間,那麼進行部門版本化操作
        	if(needDealVer(historyVO.getEffectdate()).booleanValue()){
        		NCLocator.getInstance().lookup(IDeptAdjustService.class).writeBack4DeptUnCancel(aggDeptVO,historyVO.getEffectdate());
        		this.putValue(HrAction.MESSAGE_AFTER_ACTION, "取消撤銷定時任務已添加!");
    			return;
        	}else{
			

			AggHRDeptVO[] uncanceledDepts = getDeptManageService().uncancel(aggDeptVO, historyVO, uncancelDlg.includeChildDept(),
					uncancelDlg.includePost(), true);

			// 更新左树
			// ((DeptAppModel) getModel()).directlyUpdate(uncanceledDepts);
			getDataManager().refresh();

			// 名称变更成功，提示是否执行版本化
			int res = MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6005dept", "06005dept0362")/* @res "确认执行" */, ResHelper.getString("6005dept","06005dept0146")/*@res "部门反撤销成功，是否同时执行部门版本化？"*/);
			if (res == MessageDialog.ID_YES) {
				// 部门版本化名称对话框
				CreateVersionDialog versionDlg = new CreateVersionDialog(getEntranceUI(), (HRDeptVO)uncanceledDepts[0].getParentVO());
				versionDlg.setTitle(ResHelper.getString("6005dept","06005dept0135")/*@res "部门版本化"*/);
				versionDlg.initUI();
				if (versionDlg.showModal() == UIDialog.ID_OK) {
					for (AggHRDeptVO uncanceledDept : uncanceledDepts) {
						((HRDeptVO)uncanceledDept.getParentVO()).setVname(versionDlg.getVName());
					}
					// 创建部门新版本
					AggHRDeptVO[] newVOs = getDeptManageService().createDeptVersion(uncanceledDepts);

					((DeptAppModel) getModel()).directlyUpdate(newVOs);
				}
			}
		}} else {
			this.putValue(HrAction.MESSAGE_AFTER_ACTION, ResHelper.getString("6005dept","06005dept0128")/*@res "动作已取消！"*/);
			return;
		}
	}

	private void checkAdminOrgCancel(String pk_org) throws BusinessException {
		HRAdminOrgVO adminOrgVO = (HRAdminOrgVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByPk(null, HRAdminOrgVO.class, pk_org);
		if (IPubEnumConst.ENABLESTATE_DISABLE == adminOrgVO.getEnablestate()) {
			throw new BusinessException(ResHelper.getString("6005dept", "06005dept0357")/* @res "部门所属行政组织已停用，不能进行取消撤销操作！" */);
		}
	}

	@Override
	protected boolean isActionEnable() {
		boolean isEnable = false;
		if (super.isActionEnable()) {
			isEnable = ((DeptAppModel) this.getModel()).canEdit();
		}
		//HRDeptVO deptVO = (HRDeptVO) getModel().getSelectedData();
		if( getModel().getSelectedData() == null){
			return false;
		}
		HRDeptVO deptVO = (HRDeptVO)((AggHRDeptVO) getModel().getSelectedData()).getParentVO();
		if (isEnable && UFBoolean.TRUE.equals(deptVO.getHrcanceled())) {
			return true;
		}
		return false;
	}

	public IAppModelDataManagerEx getDataManager() {
		return dataManager;
	}

	public void setDataManager(IAppModelDataManagerEx dataManager) {
		this.dataManager = dataManager;
	}

	private IDeptManageService getDeptManageService() {
		return NCLocator.getInstance().lookup(IDeptManageService.class);
	}

	// 判斷是否需要進行版本化操作
	private UFBoolean needDealVer(UFLiteralDate effectDate) {
		if (effectDate != null && effectDate.after(new UFLiteralDate())) {
			return new UFBoolean(true);
		}
		return new UFBoolean(false);
	}

}