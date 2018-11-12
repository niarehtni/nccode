package nc.ui.twhr.twhr_declaration.action;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.vo.pub.lang.UFDate;

/**
 * @author hepy
 * @date 20180126
 * @version v1.0
 * @功能描述 申报明细档汇总参数设置界面
 * 
 */
public class ShowTaxDig extends UIDialog implements ActionListener, ValueChangedListener {

	public ShowTaxDig() {
		super();
		initialize();
	}

	public ShowTaxDig(Container arg1, String arg2) {
		super(arg1, arg2);
	}

	public ShowTaxDig(Frame arg1) {
		super(arg1);
	}

	public ShowTaxDig(Frame arg1, String arg2) {
		super(arg1, arg2);
	}

	public ShowTaxDig(Container arg1) {
		super(arg1);
		initialize();
	}

	private void initialize() {
		try {
			setName("AddIncomeTaxView");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(400, 300);
			setContentPane(getUIDialogContentPane());
		} catch (Throwable ivjExc) {
			handleException(ivjExc);
		}

	}

	private JPanel getUIDialogContentPane() {
		if (ivjUIDialogContentPane == null) {
			try {
				ivjUIDialogContentPane = new javax.swing.JPanel();
				ivjUIDialogContentPane.setName("UIDialogContentPane");
				ivjUIDialogContentPane.setLayout(new java.awt.BorderLayout());
				getUIDialogContentPane().add(getUIPanel1(), "Center");
				getUIDialogContentPane().add(getUIPanel2(), "South");
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIDialogContentPane;
	}

	private UIPanel getUIPanel1() {
		if (ivjUIPanel1 == null) {
			try {
				ivjUIPanel1 = new UIPanel();
				ivjUIPanel1.setName("UIPanel1");
				ivjUIPanel1.setLayout(null);
				// 开始日期
				ivjUIPanel1.add(getStartdatelbl());
				ivjUIPanel1.add(getRefStartdate());
				// 结束日期
				ivjUIPanel1.add(getEnddatelbl());
				ivjUIPanel1.add(getRefEnddate());

				// 联系人姓名
				ivjUIPanel1.add(getContactNameLabel());
				ivjUIPanel1.add(getContactNameField());
				// 联系人电话
				ivjUIPanel1.add(getContactTelLabel());
				ivjUIPanel1.add(getContactTelField());

				// 联系人电子邮箱
				ivjUIPanel1.add(getContactEmailLabel());
				ivjUIPanel1.add(getContactEmailField());

			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIPanel1;
	}

	private nc.ui.pub.beans.UIPanel getUIPanel2() {
		if (ivjUIPanel2 == null) {
			try {
				ivjUIPanel2 = new nc.ui.pub.beans.UIPanel();
				ivjUIPanel2.setName("UIPanel2");
				ivjUIPanel2.setBackground(new java.awt.Color(204, 204, 204));
				getUIPanel2().add(getBtnOK(), getBtnOK().getName());
				getUIPanel2().add(getBtnCancel(), getBtnCancel().getName());
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIPanel2;
	}

	private UILabel getStartdatelbl() {
		if (startdatelbl == null) {
			try {
				startdatelbl = new UILabel();
				startdatelbl.setName("startdate");
				startdatelbl.setText(ResHelper.getString("twhr_personalmgt", "068J61035-0090")/* "开始日期" */);
				startdatelbl.setBounds(0, 10, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return startdatelbl;
	}

	private UILabel getEnddatelbl() {
		if (enddatelbl == null) {
			try {
				enddatelbl = new UILabel();
				enddatelbl.setName("enddate");
				enddatelbl.setText(ResHelper.getString("twhr_personalmgt", "068J61035-0091")/* "结束日期" */);
				enddatelbl.setBounds(0, 50, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return enddatelbl;
	}

	private UILabel getContactNameLabel() {
		if (contactNameLabel == null) {
			try {
				contactNameLabel = new UILabel();
				contactNameLabel.setName("contactNameLabel");
				contactNameLabel.setText(ResHelper.getString("incometax", "2incometax-n-000040")/* "联络人姓名" */);
				contactNameLabel.setBounds(0, 90, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return contactNameLabel;
	}

	private UILabel getContactTelLabel() {
		if (contactTelLabel == null) {
			try {
				contactTelLabel = new UILabel();
				contactTelLabel.setName("contactTelLabel");
				contactTelLabel.setText(ResHelper.getString("incometax", "2incometax-n-000041")/* "联络人电话" */);
				contactTelLabel.setBounds(0, 130, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return contactTelLabel;
	}

	private UILabel getContactEmailLabel() {
		if (contactEmailLabel == null) {
			try {
				contactEmailLabel = new UILabel();
				contactEmailLabel.setName("contactEmailLabel");
				contactEmailLabel.setText(ResHelper.getString("incometax", "2incometax-n-000042")/* "申报单位电子邮箱" */);
				contactEmailLabel.setBounds(0, 170, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return contactEmailLabel;
	}

	/**
	 * 开始日期
	 * 
	 * @return
	 */

	private UIRefPane getRefStartdate() {
		if (refStartdate == null) {
			try {
				refStartdate = new UIRefPane("日期");
				refStartdate.setName("refStartdate");
				refStartdate.setBounds(200, 10, 150, 50);
				refStartdate.addValueChangedListener(this);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return refStartdate;
	}

	/**
	 * 结束日期
	 * 
	 * @return
	 */
	private UIRefPane getRefEnddate() {
		if (refEnddate == null) {
			try {
				refEnddate = new UIRefPane("日期");
				refEnddate.setName("refEnddate");
				refEnddate.setBounds(200, 50, 150, 50);
				refEnddate.addValueChangedListener(this);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return refEnddate;
	}

	/**
	 * 联系人姓名
	 * 
	 * @return
	 */
	private JTextField getContactNameField() {
		if (this.contactNameField == null) {
			try {
				contactNameField = new UITextField();
				contactNameField.setName("contactNameField");
				contactNameField.setBounds(200, 90, 150, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}

		}
		return this.contactNameField;
	}

	/**
	 * 联系人电话号码
	 * 
	 * @return
	 */
	private JTextField getContactTelField() {
		if (this.contactTelField == null) {
			try {
				contactTelField = new UITextField();
				contactTelField.setName("contactTelField");
				contactTelField.setBounds(200, 130, 150, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return this.contactTelField;
	}

	/**
	 * 申报单位电子邮箱
	 * 
	 * @return
	 */
	private JTextField getContactEmailField() {
		if (this.contactEmailField == null) {
			try {
				contactEmailField = new UITextField();
				contactEmailField.setName("contactEmailField");
				contactEmailField.setBounds(200, 170, 150, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}

		}
		return this.contactEmailField;
	}

	/**
	 * 确定操作
	 * 
	 * @throws Exception
	 */
	private void onButtonOKClicked() {
		this.startdate = new UFDate(this.getRefStartdate().getValueObj().toString());
		this.enddate = new UFDate(this.getRefEnddate().getValueObj().toString());
		if (this.startdate.after(this.enddate)) {
			MessageDialog.showWarningDlg(this, ResHelper.getString("incometax", "2incometax-n-000004"),
					ResHelper.getString("twhr_personalmgt", "068J61035-0092"));// "开始日期不能大于结束日期"
			return;
		}

		this.contactName = getContactNameField().getText();
		this.contactEmail = getContactEmailField().getText();
		this.contactTel = getContactTelField().getText();
		this.closeOK();

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == this.getBtnOK()) {
			new Thread() {
				@Override
				public void run() {

					IProgressMonitor progressMonitor = NCProgresses.createDialogProgressMonitor(getParent());

					try {
						progressMonitor.beginTask("匯出中...", IProgressMonitor.UNKNOWN_REMAIN_TIME);
						progressMonitor.setProcessInfo("匯出中，請稍候.....");
						onButtonOKClicked();
					} finally {

						progressMonitor.done(); // 进度任务结束
					}
				}
			}.start();
		}
		if (e.getSource() == this.getBtnCancel()) {
			this.closeCancel();
		}

	}

	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	protected void finalize() throws Throwable {
		super.finalize();
	}

	protected LayoutManager getLayoutManager() {
		return new GridLayout(2, 2);
	}

	/**
	 * 生成接收器的散列码。 此方法主要由散列表 支持，如 java.util 中提供的那些散列表。@return 接收器的整数散列码
	 */
	public int hashCode() {
		return super.hashCode();
	}

	private UIButton getBtnOK() {
		if (btnOK == null) {
			try {
				btnOK = new UIButton();
				btnOK.setName("btnOK");
				btnOK.setText(ResHelper.getString("incometax", "2incometax-n-000027")/* "确定(Y)" */);
				btnOK.addActionListener(this);
				btnOK.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.ALT_MASK),
						JComponent.WHEN_IN_FOCUSED_WINDOW);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btnOK;
	}

	private UIButton getBtnCancel() {
		if (btnCancel == null) {
			try {
				btnCancel = new UIButton();
				btnCancel.setName("btnCancel");
				btnCancel.setText(ResHelper.getString("incometax", "2incometax-n-000028")/* "取消(C)" */);
				btnCancel.addActionListener(this);
				btnCancel.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK),
						JComponent.WHEN_IN_FOCUSED_WINDOW);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btnCancel;
	}

	public UFDate getStartdate() {
		return startdate;
	}

	public void setStartdate(UFDate startdate) {
		this.startdate = startdate;
	}

	public UFDate getEnddate() {
		return enddate;
	}

	public void setEnddate(UFDate enddate) {
		this.enddate = enddate;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactTel() {
		return contactTel;
	}

	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	private void handleException(Throwable exception) {
		Logger.error("--------- 未捕捉到的异常 ---------");
		MessageDialog.showErrorDlg(this, null, exception.getMessage());
		exception.printStackTrace();
	}

	private UIButton btnOK = null;// 确定按钮
	private UIButton btnCancel = null;// 取消按钮
	private JPanel ivjUIDialogContentPane = null;
	private UIPanel ivjUIPanel1 = null;
	private UIPanel ivjUIPanel2 = null;
	private UILabel startdatelbl = null;// 开始日期
	private UILabel enddatelbl;// 结束日期
	private UILabel contactNameLabel = null;// 联络人姓名
	private UILabel contactTelLabel = null;// 联络人电话
	private UILabel contactEmailLabel = null;// 申报单位电子邮箱

	private UIRefPane refStartdate = null;// 开始日期参照
	private UIRefPane refEnddate = null;// 结束日期参照
	private JTextField contactNameField = null;// 联络人姓名
	private JTextField contactTelField = null;// 联络人电话
	private JTextField contactEmailField = null;// 申报单位电子邮箱

	private UFDate startdate = null;// 开始日期
	private UFDate enddate = null;// 结束日期
	private String contactName = null;// 联络人姓名
	private String contactTel = null;// 联络人电话
	private String contactEmail = null;// 申报单位电子邮箱

	@Override
	public void valueChanged(ValueChangedEvent arg0) {
		// TODO Auto-generated method stub

	}

}
