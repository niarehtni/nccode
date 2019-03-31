package nc.ui.wa.taxaddtional.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.apache.commons.lang.StringUtils;

import nc.funcnode.ui.action.INCAction;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hr.uif2.action.QueryAction;
import nc.ui.hr.uif2.view.PrimaryOrgPanel;
import nc.ui.querytemplate.queryarea.IQueryExecutor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.ui.wa.taxaddtional.model.TaxaddtionalModel;
import nc.ui.wa.taxaddtional.model.TaxaddtionalModelDataManager;
import nc.ui.wa.taxaddtional.view.DataImportDia;
import nc.ui.wa.taxaddtional.view.TaxaddtionalListView;
import nc.vo.pub.BusinessException;

/**
 * 导出Action
 * 
 * @author: xuhw
 */
public class ExportAction extends QueryAction {

	private static final long serialVersionUID = -8050698726088941767L;
	private TaxaddtionalModelDataManager dataManager = null;

	private TaxaddtionalListView editor;
	private PrimaryOrgPanel orgpanel = null;

	public TaxaddtionalModelDataManager getDataManager() {
		return (TaxaddtionalModelDataManager) dataManager;
	}

	public void setDataManager(TaxaddtionalModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public PrimaryOrgPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(PrimaryOrgPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

	public TaxaddtionalListView getEditor() {
		return editor;
	}

	public void setEditor(TaxaddtionalListView editor) {
		this.editor = editor;
	}

	public ExportAction() {
		super();
		putValue(INCAction.CODE, "ExportAction");
		setBtnName("导出");
		putValue(Action.SHORT_DESCRIPTION, "导出");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// 获取最新状态
		getQueryDelegator().doQuery(new IQueryExecutor() {
			@Override
			public void doQuery(IQueryScheme arg0) {
				getModel().setExportQuerySql(arg0.getWhereSQLOnly());
			}
		});
		
	}

	@Override
	protected boolean isActionEnable() {
		return super.isActionEnable() && getModel().getContext().getPk_org() != null;
	}

	public TaxaddtionalModel getModel() {
		return (TaxaddtionalModel) super.getModel();
	}

}