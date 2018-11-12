package nc.ui.twhr.nhicalc.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.twhr.INhiCalcGenerateSrv;
import nc.itf.twhr.INhicalcMaintain;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.lang.StringUtils;

public class GenerateAction extends NCAction {
	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 2810817020929924544L;

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
		if (MessageDialog.showYesNoDlg(
				getModel().getContext().getEntranceUI(),
				NCLangRes.getInstance().getStrByID("68861705",
						"AuditAction-0000")/* 提示 */, NCLangRes.getInstance()
						.getStrByID("68861705", "GenerateAction-0000")/*
																	 * 生成操作将清除所有选定期间对比数据
																	 * ，是否继续？
																	 */) == UIDialog.ID_YES) {
			INhiCalcGenerateSrv genSrv = NCLocator.getInstance().lookup(
					INhiCalcGenerateSrv.class);

			genSrv.generateAdjustNHIData(this.getOrgpanel().getRefPane()
					.getRefPK(), this.getOrgpanel().getPeriodRefModel()
					.getRefNameValue());

			getOrgpanel().getDataManager().initModelBySqlWhere(
					" and pk_org='"
							+ getOrgpanel().getRefPane().getRefPK()
							+ "' and cyear='"
							+ this.getOrgpanel().getPeriodRefModel()
									.getRefNameValue().split("-")[0]
							+ "' and cperiod='"
							+ this.getOrgpanel().getPeriodRefModel()
									.getRefNameValue().split("-")[1] + "'");

			this.getModel().setUiState(UIState.NOT_EDIT);

			ShowStatusBarMsgUtil.showStatusBarMsg("勞健保設定生成成功。", this.getModel()
					.getContext());
		}
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see javax.swing.AbstractAction#isEnabled()
	 */
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

		String pk_org = this.getOrgpanel().getRefPane().getRefPK();
		// String pk_periodPlan = this.getOrgpanel().getPeriodPlanRefPane()
		// .getRefPK();
		String pk_waPeriod = this.getOrgpanel().getWaPeriodRefPane().getRefPK();

		return (!isaudit)
				&& (!(StringUtils.isEmpty(pk_org) || StringUtils
						.isEmpty(pk_waPeriod)));
	}

	public NhiOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(NhiOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}
}
