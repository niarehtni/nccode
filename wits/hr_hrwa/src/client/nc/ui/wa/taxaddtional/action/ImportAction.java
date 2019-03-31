package nc.ui.wa.taxaddtional.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hr.uif2.view.PrimaryOrgPanel;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.ui.uif2.model.IAppModelDataManagerEx;
import nc.ui.wa.pub.WaOrgHeadPanel;
import nc.ui.wa.taxaddtional.model.TaxaddtionalModel;
import nc.ui.wa.taxaddtional.model.TaxaddtionalModelDataManager;
import nc.ui.wa.taxaddtional.view.DataImportDia;
import nc.ui.wa.taxaddtional.view.TaxaddtionalListView;
import nc.vo.hi.wadoc.PsndocwadocCommonDef;

/**
 * 导入Action
 *
 * @author: xuhw
 */
public class ImportAction extends HrAction {

	private static final long serialVersionUID = -8050698726088941767L;
	private TaxaddtionalModelDataManager dataManager = null;
	
	private TaxaddtionalListView editor;
	private PrimaryOrgPanel orgpanel = null;
	
	public TaxaddtionalModelDataManager getDataManager() {
		return (TaxaddtionalModelDataManager)dataManager;
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

	public ImportAction() {
		super();
		putValue(INCAction.CODE, "ImportAction");
		setBtnName("导入");
		putValue(Action.SHORT_DESCRIPTION, "导入");
	}
	public void doAction(ActionEvent e) throws Exception
	{
		TaxaddtionalModel appmodel = (TaxaddtionalModel) this.getModel();
		try
		{
			TaxExcelImportEHD importEHD = new TaxExcelImportEHD(new DataImportDia(getModel().getContext(), getModel()));
			importEHD.doImport2Panel();
			getDataManager().refresh();
			getModel().setUiState(UIState.NOT_EDIT);
			ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("60130adjmtc","060130adjmtc0139")/*@res "数据导入成功！"*/, getContext());
		}
		finally
		{
		}
	}

	@Override
	protected boolean isActionEnable()
	{
		if (getModel().getContext().getPk_org() == null)
		{
			return false;
		}
		return this.getModel().getUiState() == UIState.NOT_EDIT || this.getModel().getUiState() == UIState.INIT;
	}
	public TaxaddtionalModel getModel(){
		return (TaxaddtionalModel)super.getModel();
	}

}