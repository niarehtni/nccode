package nc.ui.qcco.task.ace.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.borland.jbcl.control.CheckboxPanel;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;
import nc.ui.qcco.commission.refmodel.SampleInfoRefModel;
import nc.ui.qcco.commission.refmodel.TestInitRefModel;
import nc.ui.qcco.commission.refmodel.UnitTypeRefModel;
import nc.ui.qcco.task.view.SampleAllocationPanel;

public class AceBodyBeforeEditHandler implements
		IAppEventHandler<CardBodyBeforeEditEvent>,ValueChangedListener, ActionListener {
	private SampleAllocationPanel samplepanel;
	private UIPanel ivjUIPanel2 = null;
	@Override
	public void handleAppEvent(CardBodyBeforeEditEvent e) {
		if ("sampleallocation".equals(e.getKey())) {
			if (samplepanel == null) {
				samplepanel = new SampleAllocationPanel();
				samplepanel.setTitle("样品分配参照");
			}
			if (samplepanel.showModal() == 1) {
			
		}
			
			
			
			
	}
		e.setReturnValue(true);
	}

	@Override
	public void valueChanged(ValueChangedEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	private UIButton getBtnCancel() {
		if (btnCancel == null) {
			try {
				btnCancel = new UIButton();
				btnCancel.setName("btnCancel");
				btnCancel.setText("取消");
				btnCancel.addActionListener(this);
				btnCancel.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK),
						JComponent.WHEN_IN_FOCUSED_WINDOW);
			} catch (Throwable ivjExc) {
				ivjExc.getStackTrace();
			}
		}
		return btnCancel;
	}
	private UIButton btnOK = null;// 确定按钮
	private UIButton btnCancel = null;// 取消按钮
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
