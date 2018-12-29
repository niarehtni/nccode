package nc.ui.wa.psndocwadoc.action;

import java.awt.event.ActionEvent;

import nc.funcnode.ui.action.MenuAction;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.uif2.UIState;
import nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel;

public class BatchOperateAction extends HrAction {

	/**
	 * Serial no
	 */
	private static final long serialVersionUID = 4570376284929646389L;

	private MenuAction dataIOGroupAction = null;
	boolean isBatch = false;

	public BatchOperateAction() {
		setBtnName(isBatch);
		setCode("BatchOperateAction");
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		isBatch = !isBatch;
		setBtnName(isBatch);

		if (isBatch) {
			// 批量
			this.getDataIOGroupAction().setEnabled(false);
			((PsndocwadocAppModel) this.getModel()).setBatch(true);
			this.getModel().setUiState(UIState.EDIT);
		} else {
			// 退出批量
			this.getDataIOGroupAction().setEnabled(true);
			((PsndocwadocAppModel) this.getModel()).setBatch(false);
			this.getModel().setUiState(UIState.NOT_EDIT);
		}
	}

	private void setBtnName(boolean isBatchStatus) {
		if (isBatchStatus) {
			setBtnName("退出批量作業");
		} else {
			setBtnName("批量作業");
		}
	}

	public boolean isBatch() {
		return isBatch;
	}

	public nc.funcnode.ui.action.MenuAction getDataIOGroupAction() {
		return dataIOGroupAction;
	}

	public void setDataIOGroupAction(nc.funcnode.ui.action.MenuAction dataIOGroupAction) {
		this.dataIOGroupAction = dataIOGroupAction;
	}
}
