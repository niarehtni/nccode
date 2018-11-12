package nc.ui.wa.psndocwadoc.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.bs.uif2.IActionCode;
import nc.ui.hr.uif2.action.QueryAction;
import nc.ui.pub.query.tools.ImageIconAccessor;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel;
import nc.ui.wa.psndocwadoc.view.PsnWadocMainPane;
import nc.vo.hi.wadoc.PsndocwadocCommonDef;

/**
 * ��������Ϣά�� ��ѯ
 * 
 * @author: xuhw
 * @date: 2009-12-26 ����09:27:04
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class QueryPsndocwadocAction extends QueryAction {
	private PsnWadocMainPane mainComponent;

	public QueryPsndocwadocAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.QUERY);
		putValue(Action.SMALL_ICON, ImageIconAccessor.getIcon("toolbar/icon/query.gif"));
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		((PsndocwadocAppModel) this.getModel()).setState(PsndocwadocCommonDef.QUERY_STATE);
		super.doAction(e);
		for (int i = 0; i < this.getMainComponent().getBillScrollPane().getTableModel().getRowCount(); i++) {
			this.getMainComponent().getBillScrollPane().getTableModel().setCellEditable(i, "CHECKED", true);
		}
	}

	@Override
	protected boolean isActionEnable() {
		if (getModel().getContext().getPk_org() == null) {
			return false;
		}
		return this.getModel().getUiState() == UIState.NOT_EDIT || this.getModel().getUiState() == UIState.INIT;
	}

	public PsnWadocMainPane getMainComponent() {
		return mainComponent;
	}

	public void setMainComponent(PsnWadocMainPane mainComponent) {
		this.mainComponent = mainComponent;
	}
}
