package nc.ui.bd.defdoc.view;
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
 *  * 自定义对话框 对话框包括一个label、一个文本框和2个按钮。  PropertyChangeListener,
 */
public class DateChooseDialog extends UIDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String reason;
	
	// 是否关闭
	public boolean isOK;
	
	private UIRefPane refEnableDate = null;
	private  UFDate result = new UFDate();
	private UILabel lblEnableDate = null;
	private boolean clossflag = true;
	
	public UFDate getReturn(){
		return result;
	}


	// 按钮
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
	 * 构造函数，参数为父窗体和对话框的标题      
	 */
	public DateChooseDialog(Container conter,String title,String desc, boolean t) {
		// 添加Label和输入文本框
		super(conter, t);
		setTitle(title);
		setReason(desc);
		
		JPanel p1 = new JPanel();
		p1.add(getLblEnableDate());
		p1.add(getRefEnableDate());
		getContentPane().add("Center", p1);

		
		// 添加确定和取消按钮
		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.RIGHT));
		p2.add(getBtnOk());
		//p2.add(getCancelButton());
		getContentPane().add("South", p2);

		// 调整对话框布局大小
		pack();
		setSize(300, 70);
		this.setModal(true);
		registerAction();
		
	}


	/** 注册按钮事件 */
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
			refEnableDate = new UIRefPane("日期");
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
			setButton = new JButton("确 定");
			setButton.addActionListener(this);
		}
		return setButton;
	}
	public JButton getCancelButton(){
		if(null == cancelButton){
			cancelButton = new JButton("取 消");
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
				MessageDialog.showErrorDlg(this.getOwner(),"提示:","請輸入正確的日期格式!");
				this.getRefEnableDate().setValueObj(new UFDate().toStdString());
				result = new UFDate();
				clossflag = !clossflag;
			}
		}
		
	}
	
	
	
	
	
}