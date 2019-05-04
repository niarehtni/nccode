package nc.ui.wa.payfile.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.uif2.UIState;
import nc.ui.wa.payfile.model.PayfileAppModel;
import nc.ui.wa.payfile.refmodel.PersonType;
import nc.ui.wa.payfile.view.PayfileFormEditor;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wabm.util.WaCacheUtils;


/**
 * 增加相关人员
 *
 * @author: zhoucx
 * @date: 2009-11-30 下午04:44:48
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class AddPayfilePoiAction extends AddPayfileAction {

	private static final long serialVersionUID = 3597049063459336115L;
	
	private final String IMODELCODE = "6007";
	
	private static String DF9A="1001ZZ1000000001NEGQ";//业务代号
	private static String DF9B="1001ZZ1000000001NEGR";//费用别代号
	private static String DF92="1001ZZ1000000001NEGT";//项目代号

	public AddPayfilePoiAction() {
		super();
		setBtnName(ResHelper.getString("60130payfile","060130payfile0240")/*@res "新增相关人员"*/);
		putValue(INCAction.CODE, "AddRelation");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK + Event.SHIFT_MASK));
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("60130payfile","060130payfile0241")/*@res "新增相关人员(Ctrl+Shif+R)"*/);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nc.ui.uif2.actions.AddAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {
		if(!WaCacheUtils.isModuleStarted(IMODELCODE)){
			MessageDialog.showHintDlg(getParentContainer(), "", ResHelper.getString("60130payfile","060130payfile0334")/*@res "未安装人事模块，该功能不可用！"*/);
			return;
		}
		((PayfileAppModel) getModel()).setPType(PersonType.RELATION);
		getModel().setUiState(UIState.ADD);
		
		// {MOD:个税申报}
		// begin
		IUAPQueryBS query = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String wa_class = getWaContext().getPk_wa_class();//getModel().getSelectedData();
		WaClassVO vo = (WaClassVO) query.retrieveByPK(WaClassVO.class, wa_class);
		BillCardPanel panel = ((PayfileFormEditor)getEditor()).getBillCardPanel();
		String declaretype = vo==null?"":vo.getDeclareform();
		if(DF9A.equals(declaretype)){
			panel.getHeadItem("biztype").setEnabled(true);//业务代号
			panel.getHeadItem("feetype").setEnabled(false);//费用别代号
			panel.getHeadItem("projectcode").setEnabled(false);//项目代号
		}else if(DF9B.equals(declaretype)){
			panel.getHeadItem("biztype").setEnabled(false);//业务代号
			panel.getHeadItem("feetype").setEnabled(true);//费用别代号
			panel.getHeadItem("projectcode").setEnabled(false);//项目代号
		}else if(DF92.equals(declaretype)){
			panel.getHeadItem("biztype").setEnabled(false);//业务代号
			panel.getHeadItem("feetype").setEnabled(false);//费用别代号
			panel.getHeadItem("projectcode").setEnabled(true);//项目代号
		}else{
			panel.getHeadItem("biztype").setEnabled(false);//业务代号
			panel.getHeadItem("feetype").setEnabled(false);//费用别代号
			panel.getHeadItem("projectcode").setEnabled(false);//项目代号
		}
		// end
	}

}