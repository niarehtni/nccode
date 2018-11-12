package nc.ui.twhr.nhicalc.action;

import java.awt.event.ActionEvent;

import nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel;
import nc.ui.uif2.UIFCheckBoxAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.BatchBillTableModel;

import org.apache.commons.lang.StringUtils;

public class FilterAction extends UIFCheckBoxAction {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = -2703248092481298479L;

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

	public NhiOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(NhiOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

	private BatchBillTableModel model = null;

	private BatchBillTable editor = null;

	private NhiOrgHeadPanel orgpanel = null;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		if (!StringUtils.isEmpty(getOrgpanel().getRefPane().getRefPK())
				&& !StringUtils.isEmpty(this.getOrgpanel().getWaPeriodRefPane()
						.getRefModel().getRefNameValue())) {
			if (isSelected()) {
				this.getOrgpanel()
						.getDataManager()
						.initModelBySqlWhere(
								" and pk_org='"
										+ getOrgpanel().getRefPane().getRefPK()
										+ "' and cyear='"
										+ this.getOrgpanel()
												.getWaPeriodRefPane()
												.getRefModel()
												.getRefNameValue().split("-")[0]
										+ "' and cperiod='"
										+ this.getOrgpanel()
												.getWaPeriodRefPane()
												.getRefModel()
												.getRefNameValue().split("-")[1]
										+ "' and dr=0  and "
										+ "(isnull(oldlaborsalary, 0)<>isnull(laborsalary, 0)"
										+ " or isnull(oldlaborrange, 0)<>isnull(laborrange, 0)"
										+ " or isnull(oldretirerange, 0)<>isnull(retirerange, 0)"
										+ " or isnull(oldhealthsalary, 0)<>isnull(healthsalary, 0)"
										+ " or isnull(oldhealthrange, 0)<>isnull(healthrange, 0))");
			} else {
				this.getOrgpanel()
						.getDataManager()
						.initModelBySqlWhere(
								" and pk_org='"
										+ getOrgpanel().getRefPane().getRefPK()
										+ "' and cyear='"
										+ this.getOrgpanel()
												.getWaPeriodRefPane()
												.getRefModel()
												.getRefNameValue().split("-")[0]
										+ "' and cperiod='"
										+ this.getOrgpanel()
												.getWaPeriodRefPane()
												.getRefModel()
												.getRefNameValue().split("-")[1]
										+ "' and dr=0 ");
			}

			this.getModel().setUiState(UIState.NOT_EDIT);
		}
	}

	public boolean isEnabled() {
		return !StringUtils.isEmpty(getOrgpanel().getRefPane().getRefPK())
				&& !StringUtils.isEmpty(this.getOrgpanel().getWaPeriodRefPane()
						.getRefModel().getRefNameValue());
	}
}
