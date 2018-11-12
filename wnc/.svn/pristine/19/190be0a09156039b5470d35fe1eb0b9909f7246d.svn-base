package nc.ui.twhr.nhicalc.action;

import nc.bs.framework.common.NCLocator;
import nc.itf.twhr.INhicalcMaintain;
import nc.ui.pubapp.uif2app.actions.batch.BatchEditAction;
import nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.lang.StringUtils;

public class EditAction extends BatchEditAction {
	/**
	 * serial version id
	 */
	private static final long serialVersionUID = -1199547268191528188L;

	private BatchBillTable editor = null;

	private NhiOrgHeadPanel orgpanel = null;

	public BatchBillTableModel getModel() {
		return super.getModel();
	}

	public void setModel(BatchBillTableModel model) {
		super.setModel(model);
		model.addAppEventListener(this);
	}

	public BatchBillTable getEditor() {
		return editor;
	}

	public void setEditor(BatchBillTable editor) {
		this.editor = editor;
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

		return (!isaudit) && this.getModel().getUiState() == UIState.NOT_EDIT
				&& !this.getModel().getRows().isEmpty();
	}

	public NhiOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(NhiOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}
}
