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
 * ȡ����������Action<br>
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
		setBtnName(ResHelper.getString("6005dept","06005dept0143")/*@res "ȡ������"*/);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_J, Event.ALT_MASK));
		// setBtnChinaName("ȡ������");
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("6005dept","06005dept0144")+"(Alt+J)"/*@res "ȡ������(Alt+J)"*/);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		checkDataPermission();
		AggHRDeptVO aggDeptVO = (AggHRDeptVO) getModel().getSelectedData();
		HRDeptVO deptVO = (HRDeptVO)((AggHRDeptVO) getModel().getSelectedData()).getParentVO();
		
		//check��������������֯�Ƿ��ѳ���
		checkAdminOrgCancel(deptVO.getPk_org());
		
		DeptUnCancelDialog uncancelDlg = new DeptUnCancelDialog(getEntranceUI(), ResHelper.getString("6005dept","06005dept0145")/*@res "ȡ����������"*/, deptVO);
		if (UIDialog.ID_OK == uncancelDlg.showModal()) {
			// �õ�DeptHistoryVO
			DeptHistoryVO historyVO = uncancelDlg.buildDeptHistoryVO();
			//��������r�g��춮�ǰ�r�g,���N�M�в��T�汾������
        	if(needDealVer(historyVO.getEffectdate()).booleanValue()){
        		NCLocator.getInstance().lookup(IDeptAdjustService.class).writeBack4DeptUnCancel(aggDeptVO,historyVO.getEffectdate());
        		this.putValue(HrAction.MESSAGE_AFTER_ACTION, "ȡ�����N���r�΄������!");
    			return;
        	}else{
			

			AggHRDeptVO[] uncanceledDepts = getDeptManageService().uncancel(aggDeptVO, historyVO, uncancelDlg.includeChildDept(),
					uncancelDlg.includePost(), true);

			// ��������
			// ((DeptAppModel) getModel()).directlyUpdate(uncanceledDepts);
			getDataManager().refresh();

			// ���Ʊ���ɹ�����ʾ�Ƿ�ִ�а汾��
			int res = MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6005dept", "06005dept0362")/* @res "ȷ��ִ��" */, ResHelper.getString("6005dept","06005dept0146")/*@res "���ŷ������ɹ����Ƿ�ͬʱִ�в��Ű汾����"*/);
			if (res == MessageDialog.ID_YES) {
				// ���Ű汾�����ƶԻ���
				CreateVersionDialog versionDlg = new CreateVersionDialog(getEntranceUI(), (HRDeptVO)uncanceledDepts[0].getParentVO());
				versionDlg.setTitle(ResHelper.getString("6005dept","06005dept0135")/*@res "���Ű汾��"*/);
				versionDlg.initUI();
				if (versionDlg.showModal() == UIDialog.ID_OK) {
					for (AggHRDeptVO uncanceledDept : uncanceledDepts) {
						((HRDeptVO)uncanceledDept.getParentVO()).setVname(versionDlg.getVName());
					}
					// ���������°汾
					AggHRDeptVO[] newVOs = getDeptManageService().createDeptVersion(uncanceledDepts);

					((DeptAppModel) getModel()).directlyUpdate(newVOs);
				}
			}
		}} else {
			this.putValue(HrAction.MESSAGE_AFTER_ACTION, ResHelper.getString("6005dept","06005dept0128")/*@res "������ȡ����"*/);
			return;
		}
	}

	private void checkAdminOrgCancel(String pk_org) throws BusinessException {
		HRAdminOrgVO adminOrgVO = (HRAdminOrgVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByPk(null, HRAdminOrgVO.class, pk_org);
		if (IPubEnumConst.ENABLESTATE_DISABLE == adminOrgVO.getEnablestate()) {
			throw new BusinessException(ResHelper.getString("6005dept", "06005dept0357")/* @res "��������������֯��ͣ�ã����ܽ���ȡ������������" */);
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

	// �Д��Ƿ���Ҫ�M�а汾������
	private UFBoolean needDealVer(UFLiteralDate effectDate) {
		if (effectDate != null && effectDate.after(new UFLiteralDate())) {
			return new UFBoolean(true);
		}
		return new UFBoolean(false);
	}

}