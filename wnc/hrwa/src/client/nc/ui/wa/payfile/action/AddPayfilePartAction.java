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
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.uif2.UIState;
import nc.ui.wa.payfile.model.PayfileAppModel;
import nc.ui.wa.payfile.refmodel.PersonType;
import nc.ui.wa.payfile.view.PayfileFormEditor;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.payfile.PayfileVO;

/**
 * ������ְ��Ա
 *
 * @author: zhoucx
 * @date: 2009-11-30 ����04:40:03
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class AddPayfilePartAction extends AddPayfileAction {

	private static final long serialVersionUID = -5475225571900947575L;
	
	private static String DF9A="1001ZZ1000000001NEGQ";//ҵ�����
	private static String DF9B="1001ZZ1000000001NEGR";//���ñ����
	private static String DF92="1001ZZ1000000001NEGT";//��Ŀ����

	public AddPayfilePartAction() {
		super();
		setBtnName(ResHelper.getString("60130payfile","060130payfile0238")/*@res "������ְ��Ա"*/);
		putValue(INCAction.CODE, "AddPart");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK + Event.SHIFT_MASK));
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("60130payfile","060130payfile0239")/*@res "������ְ��Ա(Ctrl+Shif+P)"*/);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nc.ui.uif2.actions.AddAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {
		((PayfileAppModel) getModel()).setPType(PersonType.PARTTIME);
		getModel().setUiState(UIState.ADD);
		
		// {MOD:��˰�걨}
				// begin
		IUAPQueryBS query = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String wa_class = getWaContext().getPk_wa_class();//getModel().getSelectedData();
		WaClassVO vo = (WaClassVO) query.retrieveByPK(WaClassVO.class, wa_class);
		BillCardPanel panel = ((PayfileFormEditor)getEditor()).getBillCardPanel();
		String declaretype = vo==null?"":vo.getDeclareform();
		if(DF9A.equals(declaretype)){
			panel.getHeadItem("biztype").setEnabled(true);//ҵ�����
			panel.getHeadItem("feetype").setEnabled(false);//���ñ����
			panel.getHeadItem("projectcode").setEnabled(false);//��Ŀ����
		}else if(DF9B.equals(declaretype)){
			panel.getHeadItem("biztype").setEnabled(false);//ҵ�����
			panel.getHeadItem("feetype").setEnabled(true);//���ñ����
			panel.getHeadItem("projectcode").setEnabled(false);//��Ŀ����
		}else if(DF92.equals(declaretype)){
			panel.getHeadItem("biztype").setEnabled(false);//ҵ�����
			panel.getHeadItem("feetype").setEnabled(false);//���ñ����
			panel.getHeadItem("projectcode").setEnabled(true);//��Ŀ����
		}else{
			panel.getHeadItem("biztype").setEnabled(false);//ҵ�����
			panel.getHeadItem("feetype").setEnabled(false);//���ñ����
			panel.getHeadItem("projectcode").setEnabled(false);//��Ŀ����
		}
				// end
	}

}