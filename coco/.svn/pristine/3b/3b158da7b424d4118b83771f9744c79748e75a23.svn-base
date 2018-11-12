package nc.ui.twhr.nhicalc.action;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.itf.twhr.ICalculateTWNHI;
import nc.itf.twhr.INhicalcMaintain;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.lang.StringUtils;

public class CalculateAction extends NCAction {
	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 387162769827081843L;

	private BatchBillTableModel model = null;

	private BatchBillTable editor = null;

	private NhiOrgHeadPanel orgpanel = null;

	public BatchBillTableModel getModel() {
		return model;
	}

	public void setModel(BatchBillTableModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public BatchBillTable getEditor() {
		return editor;
	}

	public void setEditor(BatchBillTable editor) {
		this.editor = editor;
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		new SwingWorker() {

			BannerTimerDialog dialog = new BannerTimerDialog(
					SwingUtilities.getWindowAncestor(getModel().getContext()
							.getEntranceUI()));
			String error = null;

			protected Boolean doInBackground() throws Exception {
				try {
					dialog.setStartText("正在進行勞健保計算");
					dialog.start();

					ICalculateTWNHI calcSrv = NCLocator.getInstance().lookup(
							ICalculateTWNHI.class);
					calcSrv.calculate(getOrgpanel().getRefPane().getRefPK(),
							getOrgpanel().getPeriodRefModel().getRefNameValue()
									.split("-")[0], getOrgpanel()
									.getPeriodRefModel().getRefNameValue()
									.split("-")[1]);

					getOrgpanel().getDataManager().initModelBySqlWhere(
							" and pk_org='"
									+ getOrgpanel().getRefPane().getRefPK()
									+ "' and cyear='"
									+ getOrgpanel().getWaPeriodRefPane()
											.getRefModel().getRefNameValue()
											.split("-")[0]
									+ "' and cperiod='"
									+ getOrgpanel().getWaPeriodRefPane()
											.getRefModel().getRefNameValue()
											.split("-")[1] + "' and dr=0 ");

					getModel().setUiState(UIState.NOT_EDIT);

				} catch (LockFailedException le) {
					error = le.getMessage();
				} catch (VersionConflictException le) {
					throw new BusinessException(le.getBusiObject().toString(),
							le);
				} catch (Exception e) {
					error = e.getMessage();
				} finally {
					dialog.end();
				}
				return Boolean.TRUE;
			}

			protected void done() {
				if (error != null) {
					ShowStatusBarMsgUtil.showErrorMsg("計算勞健保發生錯誤：", error,
							getModel().getContext());
				} else {
					ShowStatusBarMsgUtil.showStatusBarMsg("勞健保計算成功。",
							getModel().getContext());
				}
			}
		}.execute();
	}

	public boolean isEnabled() {
		INhicalcMaintain nhiSrv = NCLocator.getInstance().lookup(
				INhicalcMaintain.class);
		boolean isaudit = false;
		try {
			if (!StringUtils.isEmpty(getOrgpanel().getRefPane().getRefPK())
					&& !StringUtils.isEmpty(this.getOrgpanel()
							.getPeriodRefModel().getRefNameValue())) {
				isaudit = nhiSrv.isAudit(getOrgpanel().getRefPane().getRefPK(),
						this.getOrgpanel().getPeriodRefModel()
								.getRefNameValue().split("-")[0], this
								.getOrgpanel().getPeriodRefModel()
								.getRefNameValue().split("-")[1]);
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}

		return (!isaudit) && model.getUiState() == UIState.NOT_EDIT
				&& !model.getRows().isEmpty();
	}

	public NhiOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(NhiOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}
}
