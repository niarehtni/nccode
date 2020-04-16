package nc.ui.overtime.otleavebalance.action;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.ta.leaveextrarest.ILeaveExtraRestService;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.ui.bd.defdoc.view.SettleDateChooseDialog;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager;
import nc.ui.overtime.otleavebalance.view.OTLeaveBalanceOrgPanel;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.OTLeaveBalanceVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;

import org.apache.commons.lang.StringUtils;

public class SettleAction extends HrAction {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 8003671914946574952L;
	String error = null;

	public SettleAction() {
		setCode("SettleAction");
		setBtnName("����");
	}

	public Container getParentContainer() {
		return SwingUtilities.getWindowAncestor(getContext().getEntranceUI());
	}

	private OTLeaveBalanceOrgPanel orgpanel;
	private OTLeaveBalanceModeDataManager headModelDataManager;

	UFDate specificSettleDate = new UFDate();

	@Override
	public void doAction(ActionEvent e) throws Exception {
		/*String inputDate = (String) MessageDialog.showInputDlg(this.getEntranceUI(), "�Y������", "Ոָ���Y������",
				specificSettleDate, 0, 0, "TextDate");*/
	    SettleDateChooseDialog dateChooseDlg = 
			new SettleDateChooseDialog(getModel().getContext().getEntranceUI(),"�Y������","Ոָ���Y������:",false);
	    int result = dateChooseDlg.showModal();
	    UFDate inputDate = dateChooseDlg.getReturn();
		
		if (inputDate!=null && result==0) {
		    specificSettleDate = inputDate;
		} else {
		    this.putValue(MESSAGE_AFTER_ACTION, "������ȡ��");
		    return;
			
		}

		new SwingWorker<Boolean, Void>() {
			BannerTimerDialog dialog = new BannerTimerDialog(getParentContainer());

			@Override
			protected Boolean doInBackground() throws Exception {
				String pk_otleavetype = null;
				String pk_exleavetype = null;

				try {
					pk_otleavetype = SysInitQuery.getParaString(getContext().getPk_org(), "TWHRT08");
					pk_exleavetype = SysInitQuery.getParaString(getContext().getPk_org(), "TWHRT10");
					// �ݼ����
					LeaveTypeCopyVO leaveTypeVo = (LeaveTypeCopyVO) ((OTLeaveBalanceModeDataManager) getOrgpanel()
							.getDataManager()).getHierachicalModel().getSelectedData();

					// �x���ˆT
					OTLeaveBalanceVO headvo = (OTLeaveBalanceVO) getHeadModelDataManager().getModel().getSelectedData();
					String pk_psndoc = headvo.getPk_psndoc();

					if (leaveTypeVo != null) {
						// �������ڣ���ǰ����ǰһ��
						UFLiteralDate settleDate = new UFLiteralDate(specificSettleDate.toDate());

						String[] temp = (String[]) getOrgpanel().getRefPane().getValueObj();
						String pk_org = null;
						if (temp != null && temp.length > 0) {
							pk_org = temp[0];
						}
						if (pk_org == null) {
							throw new BusinessException("�xȡ�M��ʧ��!");
						}

						if (leaveTypeVo != null && leaveTypeVo.getPk_timeitemcopy().equals(pk_otleavetype)) {
							// �Ӱ�ת����
							ISegDetailService otSegSettleSvc = NCLocator.getInstance().lookup(ISegDetailService.class);
							otSegSettleSvc.settleByExpiryDate(pk_org, new String[] { pk_psndoc }, settleDate, false);
							ShowStatusBarMsgUtil.showStatusBarMsg("�Ӱ��D�{�ݽY����ɡ�", getModel().getContext());
						} else if (leaveTypeVo != null && leaveTypeVo.getPk_timeitemcopy().equals(pk_exleavetype)) {
							// ��Ӳ���
							ILeaveExtraRestService leaveExtraRestSvc = NCLocator.getInstance().lookup(
									ILeaveExtraRestService.class);
							leaveExtraRestSvc
									.settledByExpiryDate(pk_org, new String[] { pk_psndoc }, settleDate, false);
							ShowStatusBarMsgUtil.showStatusBarMsg("����a�ݽY����ɡ�", getModel().getContext());
						}

						getHeadModelDataManager().refresh();
					}
				} catch (Exception e) {
					error = e.getMessage();
				} finally {
					dialog.end();
				}
				return Boolean.TRUE;
			}
		}.execute();

		if (StringUtils.isEmpty(error)) {
			this.putValue(MESSAGE_AFTER_ACTION, error);
		}
	}

	public OTLeaveBalanceOrgPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(OTLeaveBalanceOrgPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

	@Override
	protected boolean isActionEnable() {
		return getOrgpanel().getRefPane().getValueObj() != null &&
		// MOD ����ѡȡ��Ȳ�Ϊ�� �� ��ʼ�������ֹ���ڲ�Ϊ�� ��һ����ʱ�ɽ���by Andy on 2019-01-23
				((getOrgpanel().getCboYear().getSelectdItemValue() != null && !getOrgpanel().getCboYear()
						.getSelectdItemValue().equals("")) || (getOrgpanel().getRefBeginDate().getValueObj() != null && getOrgpanel()
						.getRefEndDate().getValueObj() != null))

				&& ((OTLeaveBalanceModeDataManager) getOrgpanel().getDataManager()).getHierachicalModel()
						.getSelectedData() != null;
	}

	public OTLeaveBalanceModeDataManager getHeadModelDataManager() {
		return headModelDataManager;
	}

	public void setHeadModelDataManager(OTLeaveBalanceModeDataManager headModelDataManager) {
		this.headModelDataManager = headModelDataManager;
	}
}
