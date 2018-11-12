package nc.ui.twhr.nhicalc.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.actions.DifferentVOSaveAction;
import nc.ui.uif2.UIState;
import nc.vo.twhr.nhicalc.AggNhiCalcVO;

public class SaveAction extends DifferentVOSaveAction {
    private nc.vo.ta.pub.TALoginContext context = null;

    @Override
    public void doAction(ActionEvent e) throws Exception {
	Object value = getEditor().getValue();

	AggNhiCalcVO aggvo = (AggNhiCalcVO) value;
	aggvo.getParentVO().setPk_org(context.getPk_org());

	if (getModel().getUiState() == UIState.ADD) {
	    doAddSave(value);
	} else if (getModel().getUiState() == UIState.EDIT) {
	    doEditSave(value);
	}

	showSuccessInfo();
    }

    public nc.vo.ta.pub.TALoginContext getContext() {
	return context;
    }

    public void setContext(nc.vo.ta.pub.TALoginContext context) {
	this.context = context;
    }
}
