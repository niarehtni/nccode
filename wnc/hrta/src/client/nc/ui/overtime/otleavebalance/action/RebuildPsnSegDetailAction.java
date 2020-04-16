package nc.ui.overtime.otleavebalance.action;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.ui.dcm.chnlrplstrct.maintain.action.MessageDialog;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModel;
import nc.ui.overtime.otleavebalance.view.OTLeaveBalanceOrgPanel;
import nc.ui.overtime.otleavebalance.view.PsnListViewPanel;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.editor.IBillListPanelView;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.ta.overtime.OTLeaveBalanceVO;

public class RebuildPsnSegDetailAction extends HrAction {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 6101162527113648376L;
	private IBillListPanelView listView;
	private OTLeaveBalanceOrgPanel orgpanel;
	String error = null;

	public RebuildPsnSegDetailAction() {
		setCode("REBUILDPSNSEG");
		setBtnName("�ؽ��ֶ�");
	}

	public Container getParentContainer() {
		return SwingUtilities.getWindowAncestor(getContext().getEntranceUI());
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		OTLeaveBalanceModel headModel = ((OTLeaveBalanceModel) this.getModel());

		OTLeaveBalanceVO otlbvo = (OTLeaveBalanceVO) headModel.getSelectedData();
		if (otlbvo != null) {
			final String pk_psndoc = otlbvo.getPk_psndoc();
			BillItem billItem = this.getListView().getBillListPanel().getHeadItem("pk_psndoc");
			// ȡ�T��Code
			UIRefPane pane = (UIRefPane) billItem.getComponent();
			final Vector colval = pane.getRefModel().matchPkData(pk_psndoc);

			if (MessageDialog.showOkCancelDlg(this.getEntranceUI(), "�_�J", "�Ƿ��ؽ��T�� [" + ((Vector) colval.get(0)).get(0)
					+ "] ��δ�Y��ֶ��Y�ϣ�") == MessageDialog.ID_OK) {
				new SwingWorker<Boolean, Void>() {
					BannerTimerDialog dialog = new BannerTimerDialog(getParentContainer());

					@Override
					protected Boolean doInBackground() throws Exception {
						try {
							dialog.setStartText("�����ؽ��T�� [" + (String) ((Vector) colval.get(0)).get(1) + "] �ֶ��Y��...");
							dialog.start();

							ISegDetailService svc = NCLocator.getInstance().lookup(ISegDetailService.class);
							svc.rebuildSegDetailByPsn(pk_psndoc, (String) getOrgpanel().getCboYear()
									.getSelectdItemValue());
						} catch (Exception e) {
							error = e.getMessage();
						} finally {
							dialog.end();
						}
						return Boolean.TRUE;
					}
				}.execute();
			}
		} else {
			PsnListViewPanel psnSelPanel = new PsnListViewPanel();
			psnSelPanel.setPk_org(getContext().getPk_org());
			if (psnSelPanel.showModal() == UIDialog.ID_OK) {
				if (MessageDialog.showOkCancelDlg(this.getEntranceUI(), "�_�J", "�Ƿ��ؽ����x���T�� "
						+ (String) getOrgpanel().getCboYear().getSelectdItemValue() + " ��ȷֶ��Y�ϣ�") == MessageDialog.ID_OK) {
					if (psnSelPanel.getSelectedPsndocPKs() != null && psnSelPanel.getSelectedPsndocPKs().length > 0) {
						final String[] pks = psnSelPanel.getSelectedPsndocPKs();

						new SwingWorker<Boolean, Void>() {
							BannerTimerDialog dialog = new BannerTimerDialog(getParentContainer());

							@Override
							protected Boolean doInBackground() throws Exception {
								try {
									IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
									ISegDetailService svc = NCLocator.getInstance().lookup(ISegDetailService.class);
									dialog.start();
									for (String pk_psndoc : pks) {
										dialog.setStartText("�����ؽ��T�� ["
												+ ((PsndocVO) query.retrieveByPK(PsndocVO.class, pk_psndoc)).getName()
												+ "] �ֶ��Y��...");
										svc.rebuildSegDetailByPsn(pk_psndoc, (String) getOrgpanel().getCboYear()
												.getSelectdItemValue());
									}

								} catch (Exception e) {
									error = e.getMessage();
								} finally {
									dialog.end();
								}
								return Boolean.TRUE;
							}
						}.execute();
					}
				}
			}
		}
	}

	public IBillListPanelView getListView() {
		return listView;
	}

	public void setListView(IBillListPanelView listView) {
		this.listView = listView;
	}

	protected boolean isActionEnable() {
		// LeaveTypeCopyVO ltvo = (LeaveTypeCopyVO)
		// ((OTLeaveBalanceModeDataManager) getOrgpanel().getDataManager())
		// .getHierachicalModel().getSelectedData();
		// String pk_leavetypecopy = "";
		// try {
		// pk_leavetypecopy =
		// SysInitQuery.getParaString(getOrgpanel().getRefPane().getRefPK(),
		// "TWHRT08");
		// } catch (BusinessException e) {
		//
		// }
		// return getOrgpanel().getRefPane().getValueObj() != null
		// && (getOrgpanel().getCboYear().getSelectdItemValue() != null ||
		// getOrgpanel().getRefBeginDate()
		// .getValueObj() != null && getOrgpanel().getRefEndDate().getValueObj()
		// != null)
		// && pk_leavetypecopy != null && ltvo != null &&
		// pk_leavetypecopy.equals(ltvo.getPk_timeitemcopy());
		return false;
	}

	public OTLeaveBalanceOrgPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(OTLeaveBalanceOrgPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

}
