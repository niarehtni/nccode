package nc.ui.bm.bmfile.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.ui.bm.bmfile.view.dialog.SearchPsnWizardStep;
import nc.ui.bm.bmfile.view.dialog.SelectBmfileWizardStep;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.wizard.IWizardDialogListener;
import nc.ui.pub.beans.wizard.WizardActionException;
import nc.ui.pub.beans.wizard.WizardDialog;
import nc.ui.pub.beans.wizard.WizardEvent;
import nc.ui.pub.beans.wizard.WizardModel;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.uif2.model.IQueryAndRefreshManager;
import nc.vo.bm.data.BmDataVO;
import nc.vo.bm.pub.BmLoginContext;
import nc.vo.pub.BusinessException;


import org.apache.commons.lang.StringUtils;

/**
 * 向导公共类
 * @author duyao
 *
 */
public abstract class BmfileWizardAction extends HrAction implements IWizardDialogListener{

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	WizardModel wizardModel = null;

	IQueryAndRefreshManager dataManager = null;

	public BmfileWizardAction() {
		super();
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		WizardDialog wizardDialog = new WizardDialog(getModel().getContext().getEntranceUI(),
				getWizardModel(), getSteps(), null);
		wizardDialog.setWizardDialogListener(this);
		wizardDialog.setResizable(true);
		wizardDialog.setSize(new Dimension(800, 560));
		wizardDialog.showModal();
	}

	protected List<WizardStep> getSteps() {
		List<WizardStep> list = new ArrayList<WizardStep>();
		list.add(new SearchPsnWizardStep(this.getBtnName(),getModel()));
		list.add(new SelectBmfileWizardStep(getBmLoginContext(),this.getBtnName(),(String) getValue(INCAction.CODE)));
		return list;
	}

	public WizardModel getWizardModel() {
		if (wizardModel == null) {
			wizardModel = new WizardModel();
			wizardModel.setSteps(getSteps());
		}
		return wizardModel;
	}

	@Override
	public void wizardFinish(WizardEvent event) throws WizardActionException {
		BmDataVO[] psnVOs = (BmDataVO[])getWizardModel().getAttr("selectedPsn");
		if (psnVOs != null && psnVOs.length > 0) {
			for (int i = 0; i < psnVOs.length; i++) {
				psnVOs[i].setCyear(((BmLoginContext)getContext()).getCyear());
				psnVOs[i].setCperiod(((BmLoginContext)getContext()).getCperiod());
				//psnVOs[i].setPaylocation(null);
			}
		}
		try {
			doProcess(psnVOs);
			getDataManager().refresh();
		} catch (BusinessException e) {
			WizardActionException ex = new WizardActionException(e);
			ex.addMsg(ResHelper.getString("60150bmfile","060150bmfile0004")
/*@res "错误"*/, e.getMessage());
			throw ex;
		}
	}

	public abstract void doProcess(BmDataVO[] psnVOs) throws BusinessException;



	@Override
	public void wizardFinishAndContinue(WizardEvent event)
			throws WizardActionException {
		// null

	}

	@Override
	public void wizardCancel(WizardEvent event) throws WizardActionException {
		// null

	}

	@Override
	protected boolean isActionEnable() {
		return super.isActionEnable()&& StringUtils.isNotBlank(getBmLoginContext().getCyear());
	}

	public BmLoginContext getBmLoginContext(){
		return (BmLoginContext)getModel().getContext();
	}

	public IQueryAndRefreshManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IQueryAndRefreshManager dataManager) {
		this.dataManager = dataManager;
	}
}