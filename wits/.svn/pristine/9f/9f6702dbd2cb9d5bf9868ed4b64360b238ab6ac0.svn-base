package nc.ui.twhr.nhicalc.action;

import java.awt.event.ActionEvent;

import nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.BatchBillTableModel;

public class QueryAction extends NCAction {
	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 2511667804224556865L;

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

	public boolean isEnabled() {
		// return !(StringUtils
		// .isEmpty(this.getOrgpanel().getRefPane().getRefPK())
		// || StringUtils.isEmpty(this.getOrgpanel()
		// .getPeriodPlanRefPane().getRefPK()) || StringUtils
		// .isEmpty(this.getOrgpanel().getWaPeriodRefPane().getRefPK()));
		return false;
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		// TODO 自动生成的方法存根

	}

	public NhiOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(NhiOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

}
