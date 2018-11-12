package nc.ui.twhr.twhr_declaration.ace.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.twhr.ITwhr_declarationMaintain;
import nc.ui.bd.pub.BDOrgPanel;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hr.uif2.view.PrimaryOrgPanel;
import nc.ui.pubapp.uif2app.query2.model.IModelDataManager;
import nc.ui.twhr.twhr_declaration.ace.view.DateGeneraDlg;
import nc.vo.pub.lang.UFDate;


/**
 * 生成按钮
 *
 * @author: Ares.Tank
 * @date: 2018-9-26 19:36:05
 * @since: eHR V6.5
 */
public class GeneratAction extends HrAction {
	private static final long serialVersionUID = 1L;
	private PrimaryOrgPanel primaryOrgPanel = null;
	private IModelDataManager iModelDataManager;


	public PrimaryOrgPanel getPrimaryOrgPanel() {
		return primaryOrgPanel;
	}

	public void setPrimaryOrgPanel(PrimaryOrgPanel primaryOrgPanel) {
		this.primaryOrgPanel = primaryOrgPanel;
	}

	public GeneratAction() {
		putValue("Code", "generatAction");
		setBtnName("生成");

		putValue("ShortDescription", ResHelper.getString("68J61710", "declartic0001") + "(Ctrl+F)");
		putValue("AcceleratorKey", javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK));
	}

	public void doActionForExtend(ActionEvent e) throws Exception {

	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// PeriodStateVO periodStateVO = (PeriodStateVO) getEditor().getValue();

		// 生成日期确认
		DateGeneraDlg dlg = new DateGeneraDlg(getEntranceUI(), "選擇月份","年份");

		dlg.initUI();
		if(dlg.showModal() == 1){
			Object startDateMonth = dlg.getdEffectiveDateMonth();

			if( null == startDateMonth){
				return ;
			}
			ITwhr_declarationMaintain service = NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class);
			service.generatCompanyBVO(new UFDate(startDateMonth + "-01 00:00:00" ),
					getPrimaryOrgPanel().getRefPane().getRefPK(),
					getContext().getPk_group()
					);
			((nc.ui.pubapp.uif2app.query2.model.ModelDataManager)
					((BDOrgPanel)getPrimaryOrgPanel()).getDataManager()).refresh();
		}



		/*ICalculateTWNHI nhiSrv = NCLocator.getInstance().lookup(ICalculateTWNHI.class);
		nhiSrv.updateExtendNHIInfo(getWaContext().getPk_group(), getWaContext().getPk_org(),
				getWaContext().getPk_wa_class(), getWaContext().getWaLoginVO().getPk_periodscheme(),
				getWaContext().getWaLoginVO().getPeriodVO().getPk_wa_period(), payDate);*/
		//


	}

	/* (non-Javadoc)
	 * @see nc.ui.uif2.NCAction#isActionEnable()
	 */
	@Override
	protected boolean isActionEnable() {
		if(null == getModel().getSelectedData()){
			return false;
		}else{
			return true;
		}
	}





}