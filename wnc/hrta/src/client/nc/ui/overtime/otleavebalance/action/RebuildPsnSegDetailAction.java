package nc.ui.overtime.otleavebalance.action;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.ui.dcm.chnlrplstrct.maintain.action.MessageDialog;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager;
import nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModel;
import nc.ui.overtime.otleavebalance.view.OTLeaveBalanceOrgPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.editor.IBillListPanelView;
import nc.vo.pub.BusinessException;
import nc.vo.ta.overtime.OTLeaveBalanceVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;

import org.apache.commons.lang.StringUtils;

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
		final String pk_psndoc = otlbvo.getPk_psndoc();
		BillItem billItem = this.getListView().getBillListPanel().getHeadItem("pk_psndoc");
		// ȡ�T��Code
		UIRefPane pane = (UIRefPane) billItem.getComponent();
		Vector colval = pane.getRefModel().matchPkData(pk_psndoc);

		if (MessageDialog.showOkCancelDlg(this.getEntranceUI(), "�_�J", "�Ƿ��ؽ��T�� [" + ((Vector) colval.get(0)).get(0)
				+ "] ��δ�Y��ֶ��Y�ϣ�") == MessageDialog.ID_OK) {
			// TODO �����\�о���
			new SwingWorker<Boolean, Void>() {
				BannerTimerDialog dialog = new BannerTimerDialog(getParentContainer());

				@Override
				protected Boolean doInBackground() throws Exception {
					try {
						dialog.setStartText("�����ؽ��ֶ��Y��...");
						dialog.start();

						IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
						String strSQL = "";
						// TODO ȡ�ˆT�ؽ�δ�Y��^�g
						strSQL = "select pk_overtimereg from hrta_segdetail where pk_psndoc='" + pk_psndoc
								+ "' and nodecode = (select min(nodecode) from hrta_segdetail where pk_psndoc='"
								+ pk_psndoc + "' and issettled = 'N')";
						String pk_firstotreg = (String) query.executeQuery(strSQL, new ColumnProcessor());

						if (StringUtils.isEmpty(pk_firstotreg)) {

						}

						// TODO ȡ�ˆTδ�Y��^�g�Ӱ���

						// TODO ȡ�ˆTδ�Y��^�g�ݼن�

						// TODO ����δ�Y�㔵��

						// TODO �ؽ��ѽY��朱�����
						// select pk, pk_parent order by code

						// TODO �ؽ��Ӱ�����

						// TODO �ؽ��ݼٔ���
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

	public IBillListPanelView getListView() {
		return listView;
	}

	public void setListView(IBillListPanelView listView) {
		this.listView = listView;
	}

	protected boolean isActionEnable() {
		try {
			String pk_otleavetype = SysInitQuery.getParaString(getContext().getPk_org(), "TWHRT08");

			LeaveTypeCopyVO leaveTypeVo = (LeaveTypeCopyVO) ((OTLeaveBalanceModeDataManager) getOrgpanel()
					.getDataManager()).getHierachicalModel().getSelectedData();

			if (leaveTypeVo != null && leaveTypeVo.getPk_timeitemcopy().equals(pk_otleavetype)) {
				OTLeaveBalanceModel headModel = ((OTLeaveBalanceModel) this.getModel());
				if ((OTLeaveBalanceVO) headModel.getSelectedData() != null) {
					return true;
				}
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}

		return false;
	}

	public OTLeaveBalanceOrgPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(OTLeaveBalanceOrgPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

}