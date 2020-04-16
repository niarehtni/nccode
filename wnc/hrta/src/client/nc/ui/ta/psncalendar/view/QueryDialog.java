package nc.ui.ta.psncalendar.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;

import nc.bs.framework.common.NCLocator;
import nc.itf.ta.IPsnCalendarManageMaintain;
import nc.ui.pub.beans.UIDialog;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

public class QueryDialog extends UIDialog implements ActionListener {
	private nc.ui.pub.beans.UIButton UIButtonComm = null;// 按钮
	private nc.ui.pub.beans.UIButton UIButtonCanel = null;
	private javax.swing.JPanel ivjUIDialogContentPane = null;// 主pane
	private nc.ui.pub.beans.UILabel UILabel1 = null;// 标签
	private nc.ui.pub.beans.UILabel UILabel2 = null;
	private nc.ui.pub.beans.UILabel UILabel3 = null;
	private nc.ui.pub.beans.UIComboBox ComboBoxto = null;// 下拉
	private nc.ui.pub.beans.UITextField chooseField = null;// 文本框
	private String date = new String();
	private String pk_psndoc = new String();
	private String value = null;// 使用value变量来告诉外界是点击什么按钮
	private String pk_org = null;

	@SuppressWarnings("deprecation")
	public QueryDialog(String psndate, String psnpk, String orgpk) {
		super();
		date = psndate;
		pk_psndoc = psnpk;
		pk_org = orgpk;
		initialize();
	}

	// 初始话
	private void initialize() {
		setName("QueryDialog");
		this.setTitle("查詢條件");// 设置标题
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);// 关闭就退出
		setSize(330, 180);// 大小
		setContentPane(getUIDialogContentPane());// 主panne
		getUIButtonComm().addActionListener(this);// 两个按钮添加监听
		getUIButtonCanel().addActionListener(this);//
	}

	// 按钮的初始
	private nc.ui.pub.beans.UIButton getUIButtonComm() {
		if (UIButtonComm == null) {
			UIButtonComm = new nc.ui.pub.beans.UIButton();
			UIButtonComm.setName("UIButtonComm");
			UIButtonComm.setText("確定");
			UIButtonComm.setBounds(90, 110, 55, 22);
		}
		return UIButtonComm;
	}

	// 按钮初始
	private nc.ui.pub.beans.UIButton getUIButtonCanel() {
		if (UIButtonCanel == null)
			UIButtonCanel = new nc.ui.pub.beans.UIButton();
		UIButtonCanel.setName("UIButtonCanel");
		UIButtonCanel.setText("取消");
		UIButtonCanel.setBounds(170, 110, 55, 22);
		return UIButtonCanel;
	}

	// 增加按钮等工作，主panne的初始
	private javax.swing.JPanel getUIDialogContentPane() {
		if (ivjUIDialogContentPane == null) {
			ivjUIDialogContentPane = new javax.swing.JPanel();
			ivjUIDialogContentPane.setName("UIDialogContentPane"); // 名称
			ivjUIDialogContentPane.setLayout(null);// 布局
			getUIDialogContentPane().add(getUIButtonComm(), getUIButtonComm().getName()); // 按钮
			getUIDialogContentPane().add(getUIButtonCanel(), getUIButtonCanel().getName()); // 按钮
			getUIDialogContentPane().add(getUILabel1(), getUILabel1().getName()); //
			getUIDialogContentPane().add(getUILabel2(), getUILabel2().getName()); //
			getUIDialogContentPane().add(getComboBoxto(), getComboBoxto().getName()); // 下拉
		}
		return ivjUIDialogContentPane;
	}

	private Component getUILabel1() {// 标签初始
		if (UILabel1 == null) {
			UILabel1 = new nc.ui.pub.beans.UILabel();
			UILabel1.setName("Label1");
			UILabel1.setText("查詢條件：");
			UILabel1.setBounds(10, 15, 60, 20);
		}
		return UILabel1;
	}

	private Component getUILabel2() {
		if (UILabel2 == null) {
			UILabel2 = new nc.ui.pub.beans.UILabel();
			UILabel2.setName("Label2");
			UILabel2.setText("類型");
			UILabel2.setBounds(20, 55, 40, 20);
		}
		return UILabel2;
	}

	private Component getComboBoxto() {// 下拉初始
		if (ComboBoxto == null) {
			ComboBoxto = new nc.ui.pub.beans.UIComboBox();
			DefaultComboBoxModel invalue = new DefaultComboBoxModel(new String[] { "平日", "休息日", "例假日", "国假日" });
			ComboBoxto.setModel(invalue);
			ComboBoxto.setName("ComboBoxto");
			ComboBoxto.setBounds(60, 55, 100, 20);
		}
		return ComboBoxto;
	}

	private Component getUITextField() {// 文本初始
		if (chooseField == null) {
			chooseField = new nc.ui.pub.beans.UITextField();
			chooseField.setName("chooseField");
			chooseField.setBounds(220, 55, 80, 20);
		}
		return chooseField;
	}

	public void actionPerformed(ActionEvent e) {// 点击按钮时调用
		if (e.getSource() == getUIButtonComm()) {
			int daytype = getDateType();
			value = UIButtonComm.getText();
			IPsnCalendarManageMaintain svc = NCLocator.getInstance().lookup(IPsnCalendarManageMaintain.class);
			try {
				svc.changeDateType4OneDay(pk_org, new String[] { pk_psndoc }, new UFLiteralDate(date), daytype);
			} catch (BusinessException e1) {
				e1.printStackTrace();
			}

			this.closeOK();
		}
		if (e.getSource() == getUIButtonCanel()) {
			value = UIButtonCanel.getText();
			this.closeOK();
		}
	}

	public int getDateType() {// 确认是调用
		String values = ComboBoxto.getSelectdItemValue() == null ? "" : ComboBoxto.getSelectdItemValue().toString();
		int type = 0;
		switch (values) {
		case "平日":
			type = 0;
			break;
		case "休息日":
			type = 1;
			break;
		case "例假日":
			type = 4;
			break;
		case "国假日":
			type = 2;
			break;
		default:
			type = 3;
			break;
		}
		return type;
	}

	public String getOkCanel() {// 取消时调用
		return value;
	}
}