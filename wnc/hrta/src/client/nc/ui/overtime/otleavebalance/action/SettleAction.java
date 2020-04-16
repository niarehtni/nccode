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
		setBtnName("结算");
	}

	public Container getParentContainer() {
		return SwingUtilities.getWindowAncestor(getContext().getEntranceUI());
	}

	private OTLeaveBalanceOrgPanel orgpanel;
	private OTLeaveBalanceModeDataManager headModelDataManager;

	UFDate specificSettleDate = new UFDate();

	@Override
	public void doAction(ActionEvent e) throws Exception {
		/*String inputDate = (String) MessageDialog.showInputDlg(this.getEntranceUI(), "結算日期", "請指定結算日期",
				specificSettleDate, 0, 0, "TextDate");*/
	    SettleDateChooseDialog dateChooseDlg = 
			new SettleDateChooseDialog(getModel().getContext().getEntranceUI(),"結算日期","請指定結算日期:",false);
	    int result = dateChooseDlg.showModal();
	    UFDate inputDate = dateChooseDlg.getReturn();
		
		if (inputDate!=null && result==0) {
		    specificSettleDate = inputDate;
		} else {
		    this.putValue(MESSAGE_AFTER_ACTION, "操作已取消");
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
					// 休假類型
					LeaveTypeCopyVO leaveTypeVo = (LeaveTypeCopyVO) ((OTLeaveBalanceModeDataManager) getOrgpanel()
							.getDataManager()).getHierachicalModel().getSelectedData();

					// 選中人員
					OTLeaveBalanceVO headvo = (OTLeaveBalanceVO) getHeadModelDataManager().getModel().getSelectedData();
					String pk_psndoc = headvo.getPk_psndoc();

					if (leaveTypeVo != null) {
						// 结算日期：当前日期前一日
						UFLiteralDate settleDate = new UFLiteralDate(specificSettleDate.toDate());

						String[] temp = (String[]) getOrgpanel().getRefPane().getValueObj();
						String pk_org = null;
						if (temp != null && temp.length > 0) {
							pk_org = temp[0];
						}
						if (pk_org == null) {
							throw new BusinessException("讀取組織失敗!");
						}

						if (leaveTypeVo != null && leaveTypeVo.getPk_timeitemcopy().equals(pk_otleavetype)) {
							// 加班转调休
							ISegDetailService otSegSettleSvc = NCLocator.getInstance().lookup(ISegDetailService.class);
							otSegSettleSvc.settleByExpiryDate(pk_org, new String[] { pk_psndoc }, settleDate, false);
							ShowStatusBarMsgUtil.showStatusBarMsg("加班轉調休結算完成。", getModel().getContext());
						} else if (leaveTypeVo != null && leaveTypeVo.getPk_timeitemcopy().equals(pk_exleavetype)) {
							// 外加补休
							ILeaveExtraRestService leaveExtraRestSvc = NCLocator.getInstance().lookup(
									ILeaveExtraRestService.class);
							leaveExtraRestSvc
									.settledByExpiryDate(pk_org, new String[] { pk_psndoc }, settleDate, false);
							ShowStatusBarMsgUtil.showStatusBarMsg("外加補休結算完成。", getModel().getContext());
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
		// MOD 补充选取年度不为空 或 开始日期与截止日期不为空 任一成立时可结算by Andy on 2019-01-23
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
