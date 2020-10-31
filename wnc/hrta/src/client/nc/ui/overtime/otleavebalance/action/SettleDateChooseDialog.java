package nc.ui.overtime.otleavebalance.action;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.vo.pub.lang.UFDate;

/**
 * ?* �Զ���Ի��� �Ի������һ��label��һ���ı����2����ť�� ?PropertyChangeListener,
 */
public class SettleDateChooseDialog extends UIDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String reason;
	
	// �Ƿ�ر�
	public boolean isOK;
	
	private UIRefPane refEnableDate = null;
	private  UFDate result = new UFDate();
	private UILabel lblEnableDate = null;
	private boolean clossflag = true;
	
	public UFDate getReturn(){
		return result;
	}


	// ��ť
	JButton setButton;
	JButton cancelButton;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}


	/**
	 * ���캯��������Ϊ������ͶԻ���ı��� ? ? ?
	 */
	public SettleDateChooseDialog(Container conter,String title,String desc, boolean t) {
		// ���Label�������ı���
		super(conter, t);
		setTitle(title);
		setReason(desc);
		
		JPanel p1 = new JPanel();
		p1.add(getLblEnableDate());
		p1.add(getRefEnableDate());
		getContentPane().add("Center", p1);

		
		// ���ȷ����ȡ����ť
		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.RIGHT));
		p2.add(getBtnOk());
		p2.add(getCancelButton());
		getContentPane().add("South", p2);

		// �����Ի��򲼾ִ�С
		pack();
		setSize(300, 70);
		this.setModal(true);
		registerAction();
		
	}


	/** ע�ᰴť�¼� */
	public void registerAction() {
		//this.cancelButton.addActionListener(this);
		this.setButton.addActionListener(this);
		this.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {
			}
			
			public void windowClosed(WindowEvent e) {
				
			}

			public void windowDeactivated(WindowEvent e) {
			}

			public void windowDeiconified(WindowEvent e) {
			}

			public void windowIconified(WindowEvent e) {
			}

			public void windowOpened(WindowEvent e) {
			}

			public void windowClosing(WindowEvent e) {
				
			}
		});
	}

	public UIRefPane getRefEnableDate() {
		if (refEnableDate == null) {
			refEnableDate = new UIRefPane("����");
			refEnableDate.setVisible(true);
			refEnableDate.setValue(result.toStdString());
			refEnableDate.setPreferredSize(new Dimension(200, 20));
			refEnableDate.setButtonFireEvent(true);
			//refEnableDate.addPropertyChangeListener(this);
		}

		return refEnableDate;
	}

	

	public JButton getBtnOk(){
		if(null == setButton){
			setButton = new JButton("ȷ ��");
			setButton.addActionListener(this);
		}
		return setButton;
	}
	public JButton getCancelButton(){
		if(null == cancelButton){
			cancelButton = new JButton("ȡ ��");
			cancelButton.addActionListener(this);
		}
		return cancelButton;
	}
	
	public UILabel getLblEnableDate() {
		if (lblEnableDate == null) {
			lblEnableDate = new UILabel();

			lblEnableDate.setText(getReason());
		}

		return lblEnableDate;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if ((source == setButton && clossflag)) {
			try{
				result = new UFDate(this.getRefEnableDate().getValueObj()
						.toString());
				this.dispose();
			}catch(Exception e){
				MessageDialog.showErrorDlg(this.getOwner(),"��ʾ:","Ոݔ�����_�����ڸ�ʽ!");
				this.getRefEnableDate().setValueObj(new UFDate().toStdString());
				result = new UFDate();
				clossflag = !clossflag;
			}
		}else if(source == cancelButton){
		    result = null;
		    this.dispose();
		}
		
	}
	
	
	
	
	
}