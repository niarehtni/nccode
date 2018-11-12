package nc.ui.twhr.nhicalc.action;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.itf.twhr.INhiCalcGenerateSrv;
import nc.itf.twhr.INhicalcMaintain;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel;
import nc.ui.twhr.nhicalc.model.NhicalcAppModel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.ModelDataDescriptor;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.lang.StringUtils;

public class GenerateAction extends NCAction {
	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 2810817020929924544L;

	private NhicalcAppModel model = null;
	private BillListView editor = null;
	private NhiOrgHeadPanel orgpanel = null;

	public NhicalcAppModel getModel() {
		return model;
	}

	public void setModel(NhicalcAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public BillListView getEditor() {
		return editor;
	}

	public void setEditor(BillListView editor) {
		this.editor = editor;
	}

	public GenerateAction() {
		setBtnName("生成");
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		getModel().initModel(null, new ModelDataDescriptor(null));
		if (MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(),
				NCLangRes.getInstance().getStrByID("68861705", "AuditAction-0000")/* 提示 */, NCLangRes.getInstance()
						.getStrByID("68861705", "GenerateAction-0000")/*
																	 * 生成操作将清除所有选定期间对比数据
																	 * ，是否继续？
																	 */) == UIDialog.ID_YES) {

			new SwingWorker() {

				BannerTimerDialog dialog = new BannerTimerDialog(SwingUtilities.getWindowAncestor(getModel()
						.getContext().getEntranceUI()));
				String error = null;

				protected Boolean doInBackground() throws Exception {
					try {
						dialog.setStartText("正在生成勞健保計算記錄");
						dialog.start();
						INhiCalcGenerateSrv genSrv = NCLocator.getInstance().lookup(INhiCalcGenerateSrv.class);

						// genSrv.generateAdjustNHIData(this.getOrgpanel().getRefPane()
						// .getRefPK(), this.getOrgpanel().getPeriodRefModel()
						// .getRefNameValue());

						genSrv.generateAdjustNHIData(getOrgpanel().getRefPane().getRefPK(), getOrgpanel()
								.getPeriodRefModel().getRefNameValue());

						getOrgpanel().getDataManager().initModelBySqlWhere(
								"  pk_org='" + getOrgpanel().getRefPane().getRefPK() + "' and cyear='"
										+ getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[0]
										+ "' and cperiod='"
										+ getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[1] + "'");

						getModel().setUiState(UIState.NOT_EDIT);
					} catch (LockFailedException le) {
						error = le.getMessage();
					} catch (VersionConflictException le) {
						throw new BusinessException(le.getBusiObject().toString(), le);
					} catch (Exception e) {
						error = e.getMessage();
					} finally {
						dialog.end();
					}
					return Boolean.TRUE;
				}

				protected void done() {
					if (error != null) {
						ShowStatusBarMsgUtil.showErrorMsg("勞健保計算記錄生成發生錯誤：", error, getModel().getContext());
					} else {
						ShowStatusBarMsgUtil.showStatusBarMsg("勞健保計算記錄生成成功。", getModel().getContext());
					}
				}
			}.execute();
		}
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see javax.swing.AbstractAction#isEnabled()
	 */
	public boolean isEnabled() {
		INhicalcMaintain nhiSrv = NCLocator.getInstance().lookup(INhicalcMaintain.class);
		boolean isaudit = false;
		try {
			if (!StringUtils.isEmpty(getOrgpanel().getRefPane().getRefPK())
					&& !StringUtils.isEmpty(this.getOrgpanel().getPeriodRefModel().getRefNameValue())) {
				isaudit = nhiSrv.isAudit(getOrgpanel().getRefPane().getRefPK(), this.getOrgpanel().getPeriodRefModel()
						.getRefNameValue().split("-")[0], this.getOrgpanel().getPeriodRefModel().getRefNameValue()
						.split("-")[1]);
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}

		String pk_org = this.getOrgpanel().getRefPane().getRefPK();
		// String pk_periodPlan = this.getOrgpanel().getPeriodPlanRefPane()
		// .getRefPK();
		String pk_waPeriod = this.getOrgpanel().getWaPeriodRefPane().getRefPK();

		return (!isaudit) && (!(StringUtils.isEmpty(pk_org) || StringUtils.isEmpty(pk_waPeriod)));
	}

	public NhiOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(NhiOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}
}
