package nc.ui.ta.teamcalendar.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.pub.beans.UIDialog;

public class QueryDialog extends UIDialog implements ActionListener {
	private nc.ui.pub.beans.UIButton UIButtonComm = null;// ��ť
	private nc.ui.pub.beans.UIButton UIButtonCanel = null;
	private javax.swing.JPanel ivjUIDialogContentPane = null;// ��pane
	private nc.ui.pub.beans.UILabel UILabel1 = null;// ��ǩ
	private nc.ui.pub.beans.UILabel UILabel2 = null;
	private nc.ui.pub.beans.UILabel UILabel3 = null;
	private nc.ui.pub.beans.UIComboBox ComboBoxto = null;// ����
	private nc.ui.pub.beans.UITextField chooseField = null;// �ı���
	private String date = new String();
	private String pk_psndoc = new String();
	private String value = null;// ʹ��value��������������ǵ��ʲô��ť

	@SuppressWarnings("deprecation")
	public QueryDialog() {
		initialize();
	}

	// ��ʼ��
	private void initialize() {
		setName("QueryDialog");
		this.setTitle("�x���������");// ���ñ���
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);// �رվ��˳�
		setSize(330, 180);// ��С
		setContentPane(getUIDialogContentPane());// ��panne
		getUIButtonComm().addActionListener(this);// ������ť��Ӽ���
		getUIButtonCanel().addActionListener(this);//
	}

	// ��ť�ĳ�ʼ
	private nc.ui.pub.beans.UIButton getUIButtonComm() {
		if (UIButtonComm == null) {
			UIButtonComm = new nc.ui.pub.beans.UIButton();
			UIButtonComm.setName("UIButtonComm");
			UIButtonComm.setText("ȷ��");
			UIButtonComm.setBounds(90, 110, 55, 22);
		}
		return UIButtonComm;
	}

	// ��ť��ʼ
	private nc.ui.pub.beans.UIButton getUIButtonCanel() {
		if (UIButtonCanel == null)
			UIButtonCanel = new nc.ui.pub.beans.UIButton();
		UIButtonCanel.setName("UIButtonCanel");
		UIButtonCanel.setText("ȡ��");
		UIButtonCanel.setBounds(170, 110, 55, 22);
		return UIButtonCanel;
	}

	// ���Ӱ�ť�ȹ�������panne�ĳ�ʼ
	private javax.swing.JPanel getUIDialogContentPane() {
		if (ivjUIDialogContentPane == null) {
			ivjUIDialogContentPane = new javax.swing.JPanel();
			ivjUIDialogContentPane.setName("UIDialogContentPane"); // ����
			ivjUIDialogContentPane.setLayout(null);// ����
			getUIDialogContentPane().add(getUIButtonComm(),
					getUIButtonComm().getName()); // ��ť
			getUIDialogContentPane().add(getUIButtonCanel(),
					getUIButtonCanel().getName()); // ��ť
			getUIDialogContentPane()
					.add(getUILabel1(), getUILabel1().getName()); //
			getUIDialogContentPane()
					.add(getUILabel2(), getUILabel2().getName()); //
			getUIDialogContentPane().add(getComboBoxto(),
					getComboBoxto().getName()); // ����
		}
		return ivjUIDialogContentPane;
	}

	private Component getUILabel1() {// ��ǩ��ʼ
		if (UILabel1 == null) {
			UILabel1 = new nc.ui.pub.beans.UILabel();
			UILabel1.setName("Label1");
			UILabel1.setText("������ͣ�");
			UILabel1.setBounds(10, 15, 60, 20);
		}
		return UILabel1;
	}

	private Component getUILabel2() {
		if (UILabel2 == null) {
			UILabel2 = new nc.ui.pub.beans.UILabel();
			UILabel2.setName("Label2");
			UILabel2.setText("����");
			UILabel2.setBounds(20, 55, 40, 20);
		}
		return UILabel2;
	}

	private Component getComboBoxto() {// ������ʼ
		if (ComboBoxto == null) {
			ComboBoxto = new nc.ui.pub.beans.UIComboBox();
			DefaultComboBoxModel invalue = new DefaultComboBoxModel(
					new String[] { "ƽ��", "��Ϣ��", "������", "������" });
			ComboBoxto.setModel(invalue);
			ComboBoxto.setName("ComboBoxto");
			ComboBoxto.setBounds(60, 55, 100, 20);
		}
		return ComboBoxto;
	}

	private Component getUITextField() {// �ı���ʼ
		if (chooseField == null) {
			chooseField = new nc.ui.pub.beans.UITextField();
			chooseField.setName("chooseField");
			chooseField.setBounds(220, 55, 80, 20);
		}
		return chooseField;
	}

	public void actionPerformed(ActionEvent e) {// �����ťʱ����
		if (e.getSource() == getUIButtonComm()) {
			value = UIButtonComm.getText();
			getDateType();
			this.closeOK();
		}
		if (e.getSource() == getUIButtonCanel()) {
			value = UIButtonCanel.getText();
			this.closeOK();
		}
	}

	public int getDateType() {// ȷ���ǵ���
		String values = new String();
		String ComboBoxt = ComboBoxto.getSelectdItemValue() == null ? ""
				: ComboBoxto.getSelectdItemValue().toString();
		values = ComboBoxt;
		int type = 0;
		switch (values) {
		case "ƽ��":
			type = 0;
			break;
		case "��Ϣ��":
			type = 1;
			break;
		case "������":
			type = 4;
			break;
		case "������":
			type = 2;
			break;
		default:
			type = 3;
			break;
		}
		return type;
	}

	public String getOkCanel() {// ȡ��ʱ����
		return value;
	}
}