package nc.ui.hrwa.sumincometax.view;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.cmp.contract.component.CMPLabel;
import nc.ui.cmp.contract.component.LabbedCom;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;

/**
 * @author ward.wong
 * @date 20180126
 * @version v1.0
 * @功能描述 申报明细档汇总参数设置界面
 * 
 */
public class AddSumIncomeTaxView extends UIDialog implements ActionListener,
		ValueChangedListener {

	public AddSumIncomeTaxView() {
		super();
		initialize();
	}

	public AddSumIncomeTaxView(Container arg1, String arg2) {
		super(arg1, arg2);
	}

	public AddSumIncomeTaxView(Frame arg1) {
		super(arg1);
	}

	public AddSumIncomeTaxView(Frame arg1, String arg2) {
		super(arg1, arg2);
	}

	public AddSumIncomeTaxView(Container arg1) {
		super(arg1);
		initialize();
	}

	private void initialize() {
		try {
			setName("AddIncomeTaxView");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(LabbedCom.MaxSize.width * 4, 450);
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
				// 申报凭单格式
				ivjUIPanel1.add(getDeclaretypeLabel());
				ivjUIPanel1.add(getDeclaretypePane());
				// 凭单填发发格式
				ivjUIPanel1.add(getGranttypeLable());
				ivjUIPanel1.add(getGranttypePane());
				// 申报次数
				ivjUIPanel1.add(getDeclarenumLable());
				ivjUIPanel1.add(getDeclarenumField());
				// 重复申报原因
				ivjUIPanel1.add(getReasonLable());
				ivjUIPanel1.add(getReasonPane());
				// 业别代号
				ivjUIPanel1.add(getBusinessnoLabel());
				ivjUIPanel1.add(getBusinessnoPane());
				// 费用别代号
				ivjUIPanel1.add(getCostnoLabel());
				ivjUIPanel1.add(getCostnoPane());
				// 项目代号
				ivjUIPanel1.add(getProjectnoLabel());
				ivjUIPanel1.add(getProjectnoPane());
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

	private CMPLabel getDeclaretypeLabel() {
		if (declaretypeLabel == null) {
			try {
				declaretypeLabel = new CMPLabel();
				declaretypeLabel.setName("declaretypeLabel");
				declaretypeLabel.setText(ResHelper.getString("incometax",
						"2incometax-n-000033")/* "申报凭单格式" */);
				declaretypeLabel.setBounds(0, 10, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return declaretypeLabel;
	}

	private CMPLabel getGranttypeLable() {
		if (granttypeLable == null) {
			try {
				granttypeLable = new CMPLabel();
				granttypeLable.setName("unifiednumberLabel");
				granttypeLable.setText(ResHelper.getString("incometax",
						"2incometax-n-000034")/* "凭单填发格式" */);
				granttypeLable.setBounds(0, 50, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return granttypeLable;
	}

	private CMPLabel getDeclarenumLable() {
		if (declarenumLable == null) {
			try {
				declarenumLable = new CMPLabel();
				declarenumLable.setName("waclassLabel");
				declarenumLable.setText(ResHelper.getString("incometax",
						"2incometax-n-000035")/* "申报次数" */);
				declarenumLable.setBounds(0, 90, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return declarenumLable;
	}

	private CMPLabel getReasonLable() {
		if (reasonLable == null) {
			try {
				reasonLable = new CMPLabel();
				reasonLable.setName("reasonLable");
				reasonLable.setText(ResHelper.getString("incometax",
						"2incometax-n-000036")/* "重复申报原因" */);
				reasonLable.setBounds(0, 130, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return reasonLable;
	}

	private CMPLabel getBusinessnoLabel() {
		if (businessnoLabel == null) {
			try {
				businessnoLabel = new CMPLabel();
				businessnoLabel.setName("businessnoLabel");
				businessnoLabel.setText(ResHelper.getString("incometax",
						"2incometax-n-000037")/* "业别代号" */);
				businessnoLabel.setBounds(0, 170, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return businessnoLabel;
	}

	private CMPLabel getCostnoLabel() {
		if (costnoLabel == null) {
			try {
				costnoLabel = new CMPLabel();
				costnoLabel.setName("costnoLabel");
				costnoLabel.setText(ResHelper.getString("incometax",
						"2incometax-n-000038")/* "费用别代号" */);
				costnoLabel.setBounds(0, 210, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return costnoLabel;
	}

	private CMPLabel getProjectnoLabel() {
		if (projectnoLabel == null) {
			try {
				projectnoLabel = new CMPLabel();
				projectnoLabel.setName("projectnoLabel");
				projectnoLabel.setText(ResHelper.getString("incometax",
						"2incometax-n-000039")/* "项目代号" */);
				projectnoLabel.setBounds(0, 250, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return projectnoLabel;
	}

	private CMPLabel getContactNameLabel() {
		if (contactNameLabel == null) {
			try {
				contactNameLabel = new CMPLabel();
				contactNameLabel.setName("contactNameLabel");
				contactNameLabel.setText(ResHelper.getString("incometax",
						"2incometax-n-000040")/* "联络人姓名" */);
				contactNameLabel.setBounds(0, 290, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return contactNameLabel;
	}

	private CMPLabel getContactTelLabel() {
		if (contactTelLabel == null) {
			try {
				contactTelLabel = new CMPLabel();
				contactTelLabel.setName("contactTelLabel");
				contactTelLabel.setText(ResHelper.getString("incometax",
						"2incometax-n-000041")/* "联络人电话" */);
				contactTelLabel.setBounds(0, 330, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return contactTelLabel;
	}

	private CMPLabel getContactEmailLabel() {
		if (contactEmailLabel == null) {
			try {
				contactEmailLabel = new CMPLabel();
				contactEmailLabel.setName("contactEmailLabel");
				contactEmailLabel.setText(ResHelper.getString("incometax",
						"2incometax-n-000042")/* "申报单位电子邮箱" */);
				contactEmailLabel.setBounds(0, 370, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return contactEmailLabel;
	}

	/**
	 * 申报格式
	 * 
	 * @return
	 */
	private UIRefPane getDeclaretypePane() {
		if (declaretypePane == null) {
			try {
				declaretypePane = new UIRefPane();
				declaretypePane.setName("declaretypePane");
				declaretypePane.setRefNodeName("申報格式(自定义档案)");
				declaretypePane.setBounds(200, 10, 150, 50);
				declaretypePane.addValueChangedListener(this);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return declaretypePane;
	}

	/**
	 * 凭单填发格式
	 * 
	 * @return
	 */
	private UIRefPane getGranttypePane() {
		if (granttypePane == null) {
			try {
				granttypePane = new UIRefPane();
				granttypePane.setName("granttypePane");
				granttypePane.setRefNodeName("憑單填發格式(自定义档案)");
				granttypePane.setBounds(200, 50, 150, 50);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return granttypePane;
	}

	/**
	 * 申报次数
	 * 
	 * @return
	 */
	private JTextField getDeclarenumField() {
		if (this.declarenumField == null) {
			try {
				declarenumField = new UITextField();
				declarenumField.setName("declarenumField");
				declarenumField.setBounds(200, 90, 150, 30);
				((UITextField) declarenumField).setTextType("TextInt");
				((UITextField) declarenumField).setMaxLength(2);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}

		}
		return this.declarenumField;
	}

	/**
	 * 重复申报原因
	 * 
	 * @return
	 */
	private UIRefPane getReasonPane() {
		if (this.reasonPane == null) {
			try {
				reasonPane = new UIRefPane();
				reasonPane.setName("reasonPane");
				reasonPane.setRefNodeName("重複申報原因(自定义档案)");
				reasonPane.setBounds(200, 130, 150, 50);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}

		}
		return this.reasonPane;
	}

	/**
	 * 业别代号
	 * 
	 * @return
	 */
	private UIRefPane getBusinessnoPane() {
		if (this.businessnoPane == null) {
			try {
				businessnoPane = new UIRefPane();
				businessnoPane.setName("businessnoPane");
				businessnoPane.setRefNodeName("執行業務者業別(自定义档案)");
				businessnoPane.setBounds(200, 170, 150, 50);
				businessnoPane.setEditable(false);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return this.businessnoPane;
	}

	/**
	 * 费用别代号
	 * 
	 * @return
	 */
	private UIRefPane getCostnoPane() {
		if (this.costnoPane == null) {
			try {
				costnoPane = new UIRefPane();
				costnoPane.setName("costnoPane");
				costnoPane.setRefNodeName("費用別代號(自定义档案)");
				costnoPane.setBounds(200, 210, 150, 50);
				costnoPane.setEditable(false);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return this.costnoPane;
	}

	/**
	 * 项目代号
	 * 
	 * @return
	 */
	private UIRefPane getProjectnoPane() {
		if (this.projectnoPane == null) {
			try {
				projectnoPane = new UIRefPane();
				projectnoPane.setName("projectnoPane");
				projectnoPane.setRefNodeName("項目代號(自定义档案)");
				projectnoPane.setBounds(200, 250, 150, 50);
				projectnoPane.setEditable(false);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return this.projectnoPane;
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
				contactNameField.setBounds(200, 290, 150, 30);
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
				contactTelField.setBounds(200, 330, 150, 30);
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
				contactEmailField.setBounds(200, 370, 150, 30);
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
		this.declaretype = getDeclaretypePane().getRefPK();
		if (null == this.declaretype || "".equals(this.declaretype)) {
			MessageDialog
					.showWarningDlg(this, ResHelper.getString("incometax",
							"2incometax-n-000004"), ResHelper.getString(
							"incometax", "2incometax-n-000043") /* "申报凭单格式不能为空" */);
			return;
		}
		this.granttype = getGranttypePane().getRefPK();
		if (null == this.granttype || "".equals(granttype)) {
			MessageDialog
					.showWarningDlg(this, ResHelper.getString("incometax",
							"2incometax-n-000004"), ResHelper.getString(
							"incometax", "2incometax-n-000044")/* "凭单填发格式不能为空" */);
			return;
		}
		this.declarenum = getDeclarenumField().getText();
		this.reason = getReasonPane().getRefPK();
		this.businessno = getBusinessnoPane().getRefPK();
		this.costno = getCostnoPane().getRefPK();
		this.projectno = getProjectnoPane().getRefPK();
		if ("1001ZZ1000000001NEGQ".equals(businessno)) {
			if (null == this.businessno || "".equals(this.businessno)) {
				MessageDialog
						.showWarningDlg(this, ResHelper.getString("incometax",
								"2incometax-n-000004"), ResHelper.getString(
								"incometax", "2incometax-n-000045")/* "业别代号不能为空" */);
				return;
			}

		} else if ("1001ZZ1000000001NEGR".equals(declaretype)) {
			if (null == this.costno || "".equals(this.costno)) {
				MessageDialog
						.showWarningDlg(this, ResHelper.getString("incometax",
								"2incometax-n-000004"), ResHelper.getString(
								"incometax", "2incometax-n-000046")/* "费用别代号不能为空" */);
				return;
			}

		} else if ("1001ZZ1000000001NEGT".equals(declaretype)) {
			if (null == this.projectno || "".equals(this.projectno)) {
				MessageDialog
						.showWarningDlg(this, ResHelper.getString("incometax",
								"2incometax-n-000004"), ResHelper.getString(
								"incometax", "2incometax-n-000047")/* "项目代号不能为空" */);
				return;
			}
		}
		this.contactName = getContactNameField().getText();
		this.contactTel = getContactTelField().getText();
		this.contactEmail = getContactEmailField().getText();
		this.closeOK();

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == this.getBtnOK()) {
			new Thread() {
				@Override
				public void run() {

					IProgressMonitor progressMonitor = NCProgresses
							.createDialogProgressMonitor(getParent());

					try {
						progressMonitor.beginTask("匯出中...",
								IProgressMonitor.UNKNOWN_REMAIN_TIME);
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

	public void valueChanged(ValueChangedEvent e) {
		if (e.getSource() == getDeclaretypePane()) {
			String declaretype = getDeclaretypePane().getRefPK();
			if ("1001ZZ1000000001NEGQ".equals(declaretype)) {
				getBusinessnoPane().setEditable(true);
				getCostnoPane().setEditable(false);
				getProjectnoPane().setEditable(false);
				getBusinessnoPane().setEnabled(true);
				getCostnoPane().setEnabled(false);
				getProjectnoPane().setEnabled(false);
				getCostnoPane().setPK(null);
				getProjectnoPane().setPK(null);
			} else if ("1001ZZ1000000001NEGR".equals(declaretype)) {
				getBusinessnoPane().setPK(null);
				getBusinessnoPane().setEditable(false);
				getCostnoPane().setEditable(true);
				getProjectnoPane().setPK(null);
				getProjectnoPane().setEditable(false);
				getBusinessnoPane().setEnabled(false);
				getCostnoPane().setEnabled(true);
				getProjectnoPane().setEnabled(false);
			} else if ("1001ZZ1000000001NEGT".equals(declaretype)) {
				getBusinessnoPane().setPK(null);
				getCostnoPane().setPK(null);
				getBusinessnoPane().setEditable(false);
				getCostnoPane().setEditable(false);
				getProjectnoPane().setEditable(true);
				getBusinessnoPane().setEnabled(false);
				getCostnoPane().setEnabled(false);
				getProjectnoPane().setEnabled(true);
			} else {
				getBusinessnoPane().setEditable(false);
				getCostnoPane().setEditable(false);
				getProjectnoPane().setEditable(false);
				getBusinessnoPane().setEnabled(false);
				getCostnoPane().setEnabled(false);
				getProjectnoPane().setEnabled(false);
				getBusinessnoPane().setPK(null);
				getCostnoPane().setPK(null);
				getProjectnoPane().setPK(null);
			}
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
				btnOK.setText(ResHelper.getString("incometax",
						"2incometax-n-000027")/* "确定(Y)" */);
				btnOK.addActionListener(this);
				btnOK.registerKeyboardAction(this, KeyStroke.getKeyStroke(
						KeyEvent.VK_Y, InputEvent.ALT_MASK),
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
				btnCancel.setText(ResHelper.getString("incometax",
						"2incometax-n-000028")/* "取消(C)" */);
				btnCancel.addActionListener(this);
				btnCancel.registerKeyboardAction(this, KeyStroke.getKeyStroke(
						KeyEvent.VK_C, InputEvent.ALT_MASK),
						JComponent.WHEN_IN_FOCUSED_WINDOW);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btnCancel;
	}

	public String getDeclaretype() {
		return declaretype;
	}

	public void setDeclaretype(String declaretype) {
		this.declaretype = declaretype;
	}

	public String getGranttype() {
		return granttype;
	}

	public void setGranttype(String granttype) {
		this.granttype = granttype;
	}

	public String getDeclarenum() {
		return declarenum;
	}

	public void setDeclarenum(String declarenum) {
		this.declarenum = declarenum;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getBusinessno() {
		return businessno;
	}

	public void setBusinessno(String businessno) {
		this.businessno = businessno;
	}

	public String getCostno() {
		return costno;
	}

	public void setCostno(String costno) {
		this.costno = costno;
	}

	public String getProjectno() {
		return projectno;
	}

	public void setProjectno(String projectno) {
		this.projectno = projectno;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactTel() {
		return contactTel;
	}

	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
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
	private CMPLabel declaretypeLabel = null;// 申报凭单格式
	private CMPLabel granttypeLable;// 凭单填发格式
	private CMPLabel declarenumLable = null;// 申报次数
	private CMPLabel reasonLable = null;// 重复申报原因
	private CMPLabel businessnoLabel = null;// 业别代号
	private CMPLabel costnoLabel = null;// 费用别代号
	private CMPLabel projectnoLabel = null;// 项目代号
	private CMPLabel contactNameLabel = null;// 联络人姓名
	private CMPLabel contactTelLabel = null;// 联络人电话
	private CMPLabel contactEmailLabel = null;// 申报单位电子邮箱

	private UIRefPane declaretypePane = null;// 申报凭单格式
	private UIRefPane granttypePane = null;// 凭单填发格式
	private JTextField declarenumField = null;// 申报次数
	private UIRefPane reasonPane = null;// 重复申报原因
	private UIRefPane businessnoPane = null;// 业别代号
	private UIRefPane costnoPane = null;// 费用别代号
	private UIRefPane projectnoPane = null;// 项目代号
	private JTextField contactNameField = null;// 联络人姓名
	private JTextField contactTelField = null;// 联络人电话
	private JTextField contactEmailField = null;// 申报单位电子邮箱

	private String declaretype = null;// 申报凭单格式
	private String granttype = null;// 凭单填发格式
	private String declarenum = null;// 申报次数
	private String reason = null;// 重复申报原因
	private String businessno = null;// 业别代号
	private String costno = null;// 费用别代号
	private String projectno = null;// 项目代号
	private String contactName = null;// 联络人姓名
	private String contactTel = null;// 联络人电话
	private String contactEmail = null;// 申报单位电子邮箱

}
