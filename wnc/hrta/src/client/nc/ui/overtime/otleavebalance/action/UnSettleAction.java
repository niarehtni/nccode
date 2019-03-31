package nc.ui.overtime.otleavebalance.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.ta.leaveextrarest.ILeaveExtraRestService;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager;
import nc.ui.overtime.otleavebalance.view.OTLeaveBalanceOrgPanel;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.overtime.OTLeaveBalanceVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;

public class UnSettleAction extends HrAction {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 4342543837345824769L;

	public UnSettleAction() {
		setCode("UnSettleAction");
		setBtnName("������");
	}

	private OTLeaveBalanceOrgPanel orgpanel;
	private OTLeaveBalanceModeDataManager headModelDataManager;

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		String pk_otleavetype = null;
		String pk_exleavetype = null;

		try {
			pk_otleavetype = SysInitQuery.getParaString(getContext().getPk_org(), "TWHRT08");
			pk_exleavetype = SysInitQuery.getParaString(getContext().getPk_org(), "TWHRT10");
			// �ݼ����
			LeaveTypeCopyVO leaveTypeVo = (LeaveTypeCopyVO) ((OTLeaveBalanceModeDataManager) getOrgpanel()
					.getDataManager()).getHierachicalModel().getSelectedData();

			if (leaveTypeVo != null) {
				// �x���ˆT
				OTLeaveBalanceVO headvo = (OTLeaveBalanceVO) getHeadModelDataManager().getModel().getSelectedData();
				String pk_psndoc = headvo.getPk_psndoc();
				if (leaveTypeVo != null && leaveTypeVo.getPk_timeitemcopy().equals(pk_otleavetype)) {
					// �Ӱ�ת����
					ISegDetailService otSegSettleSvc = NCLocator.getInstance().lookup(ISegDetailService.class);
					otSegSettleSvc.unSettleByPsn(pk_psndoc);
					ShowStatusBarMsgUtil.showStatusBarMsg("�Ӱ��D�{�ݷ��Y����ɡ�", getModel().getContext());
				} else if (leaveTypeVo != null && leaveTypeVo.getPk_timeitemcopy().equals(pk_exleavetype)) {
					// ��Ӳ���
					ILeaveExtraRestService leaveExtraRestSvc = NCLocator.getInstance().lookup(
							ILeaveExtraRestService.class);
					leaveExtraRestSvc.unSettleByPsn(pk_psndoc);
					ShowStatusBarMsgUtil.showStatusBarMsg("����a�ݷ��Y����ɡ�", getModel().getContext());
				}

				this.getHeadModelDataManager().refresh();
			}
		} catch (BusinessException ex) {
			ExceptionUtils.wrappBusinessException(ex.getMessage());
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
		// MOD ����ѡȡ��Ȳ�Ϊ�� �� ��ʼ�������ֹ���ڲ�Ϊ�� ��һ����ʱ�ɷ�����by Andy on 2019-01-23
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