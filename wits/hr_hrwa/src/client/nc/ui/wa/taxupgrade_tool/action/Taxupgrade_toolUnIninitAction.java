package nc.ui.wa.taxupgrade_tool.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.PubEnv;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.uif2.UIState;
import nc.ui.wa.taxupgrade_tool.model.Taxupgrade_toolAppModel;
import nc.ui.wa.taxupgrade_tool.model.Taxupgrade_toolModelService;
import nc.ui.wa.taxupgrade_tool.view.Taxupgrade_toolForm;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa_tax.TaxupgradeDef;

/**
 * 卸载 公共薪资项目 薪资发放项目 Action
 *
 * @author: xuhw
 */
public class Taxupgrade_toolUnIninitAction extends HrAction {

	private static final long serialVersionUID = -8050698726088941767L;
	private static final String type = "0";
	private Taxupgrade_toolForm editor;

	public Taxupgrade_toolForm getEditor() {
		return editor;
	}

	public void setEditor(Taxupgrade_toolForm editor) {
		this.editor = editor;
	}

	public Taxupgrade_toolUnIninitAction() {
		super();
		putValue(INCAction.CODE, "Taxupgrade_toolUnIninitAction");
		setBtnName("一键卸载[3、4]（慎用）");
		putValue(Action.SHORT_DESCRIPTION, "一键卸载[3、4]（慎用）");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		GeneralVO[] vos = getEditor().getSelectedClassVOs();
		WaItemVO[] itemvos = getEditor().getTaxItems();
		itemvos = getEditor().getTaxItems();
		((Taxupgrade_toolModelService)getModel().getService()).taxUpgradeToSelectClass(PubEnv.getPk_group(), itemvos, vos, TaxupgradeDef.OPTYPE_UNINIT);
		getEditor().initUI();
	}

	@Override
	protected boolean isActionEnable() {
		return super.isActionEnable() && getModel().getContext().getPk_org() != null;
	}

	public Taxupgrade_toolAppModel getModel(){
		return (Taxupgrade_toolAppModel)super.getModel();
	}

}