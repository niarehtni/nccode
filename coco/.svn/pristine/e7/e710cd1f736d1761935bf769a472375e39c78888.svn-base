package nc.ui.twhr.rangetable.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.pubapp.uif2app.AppUiState;
import nc.ui.pubapp.uif2app.actions.AbstractBodyTableExtendAction;
import nc.ui.pubapp.uif2app.actions.AddAction;
import nc.ui.pubapp.uif2app.event.billform.AddEvent;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.IEditor;
import nc.uif2.annoations.MethodType;
import nc.uif2.annoations.ViewMethod;
import nc.uif2.annoations.ViewType;
/**
 * 级距表新增
 * @author Ares.Tank
 *
 */
public class RangetableAddAction extends AddAction {
	 private BillForm billfrom;
	 public RangetableAddAction(BillForm billfrom){
		 this.billfrom = billfrom;
	 }


	/**
	 * 
	 */
	private static final long serialVersionUID = -9177609475568684704L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		this.model.setUiState(UIState.ADD);
		//将默认组织设定为当前组织(此组织仅做校验用,数据库不会保存任何的组织和集团信息)
		String pk_org = WorkbenchEnvironment.getInstance().getLoginUser().getPk_org();
		getModel().getContext().setPk_org(pk_org);
		//getModel().getContext().getPk_org();
		//super.doAction(e);
		
		this.billfrom.addNew();
		//getModel().setUiState(UIState.EDIT);
		//this.billfrom.getModel().setAppUiState(AppUiState.EDIT);
	}
	
	

}
