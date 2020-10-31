package nc.individuation.defaultData;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.individuation.property.pub.AbstractIndividuationPage;
import nc.individuation.property.pub.IExceptionHandler;
import nc.pubitf.rbac.IUserPubConstants;
import nc.pubitf.rbac.IUserPubService;
import nc.pubitf.setting.defaultdata.IUAPDefaultSettingConst;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIPasswordField;
import nc.ui.pub.beans.UITextField;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.vo.uap.rbac.util.RbacUserPwdUtil;

import com.borland.jbcl.layout.VerticalFlowLayout;

/**
 * 个性化中心邮箱绑定功能
 * 
 * @author liuxing0
 * 
 */
public class MailBingPage extends AbstractIndividuationPage implements
		IUAPDefaultSettingConst, IExceptionHandler {
	private static final long serialVersionUID = -6050467056666432780L;
	private UILabel mailPromptUL; // 邮箱输入提示
	private UITextField mailTfd;// 邮箱输入框
	private UILabel pwdPromptUL;// 密码输入提示
	private UIPasswordField pwdTfd; // 密码输入框
	private UIPanel mailPanel, pwdPanel, up1, up2; // 分别存放电子邮件、密码，两个提示panel
	private UILabel promotUL1, promotUL2;// 输入提示
	private UILabel emailUL;// 用来存放输入的邮箱
	private String emailValue;// 用户输入的邮箱
	private String password;// 用户输入的密码
	private UserVO currentUser;// 当前登录的用

	public MailBingPage() {
		currentUser = WorkbenchEnvironment.getInstance().getLoginUser();
		initUI();
	}

	/**
	 * 初始化邮箱绑定界面
	 */
	private void initUI() {
		setLayout(new VerticalFlowLayout());
		createElementDiff();
		initEmailPanel();
		initPwdPanel();
		initPromotPanel();
		add(new UIPanel());
		add(up1);
		add(up2);
		add(new UIPanel());
		add(mailPanel);
		add(pwdPanel);
	}

	/**
	 * 根据用户是否绑定邮箱来构造不同的控件
	 */
	private void createElementDiff() {
		mailPromptUL = new UILabel();
		mailPromptUL.setPreferredSize(new Dimension(80, 20));
		mailPromptUL.setHorizontalAlignment(SwingConstants.RIGHT);
		if (currentUser.getEmail() == null) {
			promotUL1 = new UILabel(NCLangRes.getInstance().getStrByID("sfapp",
					"MailBingPage-00003"));/* 为了你的账户安全，请你绑定邮箱。绑定时需要提供系统密码以确定身份。 */
			emailUL = new UILabel();
			promotUL2 = new UILabel();
			mailPromptUL.setText(NCLangRes.getInstance().getStrByID("sfapp",
					"MailBingPage-00004")); /* 邮箱 */
		} else {
			promotUL1 = new UILabel(NCLangRes.getInstance().getStrByID("sfapp",
					"MailBingPage-00008")); // /*您已经成功绑定邮箱：*/
			emailUL = new UILabel(currentUser.getEmail());
			promotUL2 = new UILabel(NCLangRes.getInstance().getStrByID("sfapp",
					"MailBingPage-00009"));/*
											 * 输入新邮箱可以更改绑定邮箱。更改邮箱时需要您提供系统登录密码以确定您的身份
											 * 。
											 */
			;
			mailPromptUL.setText(NCLangRes.getInstance().getStrByID("sfapp",
					"MailBingPage-00006")); /* 新邮箱 */
		}
		emailUL.setFont(new Font("宋体", Font.BOLD, 12));
	}

	/**
	 * 初始化邮箱Panel
	 */
	private void initEmailPanel() {
		mailPanel = new UIPanel();
		mailPanel.setOpaque(false);
		mailPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 40, 5));

		mailTfd = new UITextField();
		mailTfd.setPreferredSize(new Dimension(200, 20));
		mailPanel.add(mailPromptUL);
		mailPanel.add(mailTfd);
	}

	/**
	 * 密码输入panel：密码提示和密码框
	 */
	private void initPwdPanel() {
		pwdPanel = new UIPanel();
		pwdPanel.setOpaque(false);
		pwdPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 40, 5));
		pwdPromptUL = new UILabel();
		pwdPromptUL.setText(NCLangRes.getInstance().getStrByID("sfapp",
				"MailBingPage-00005"));/* 系统登录密码 */
		pwdPromptUL.setPreferredSize(new Dimension(80, 20));
		pwdPromptUL.setHorizontalAlignment(SwingConstants.RIGHT);
		pwdPanel.add(pwdPromptUL);
		pwdTfd = new UIPasswordField();
		pwdTfd.setAllowOtherCharacter(true);
		pwdTfd.setPreferredSize(new Dimension(200, 20));
		pwdPanel.add(pwdTfd);
	}

	/**
	 * 初始化提示信息的Panel
	 */
	private void initPromotPanel() {
		up1 = new UIPanel();
		up1.setLayout(new FlowLayout(FlowLayout.LEADING, 20, 0));
		up2 = new UIPanel();
		up2.setLayout(new FlowLayout(FlowLayout.LEADING, 20, 0));
		up1.add(promotUL1);
		up1.add(emailUL);
		up2.add(promotUL2);
	}

	/**
	 * 描述
	 */
	@Override
	public String getDescription() {
		return NCLangRes.getInstance()
				.getStrByID("sfapp", "MailBingPage-00002")/*
														 * 绑定邮箱可用于密码重置等操作，增强您账户的安全性
														 * 。
														 */;
	}

	/**
	 * 界面标题
	 */
	@Override
	public String getTitle() {
		return NCLangRes.getInstance()
				.getStrByID("sfapp", "MailBingPage-00001")/* 邮箱绑定 */;
	}

	@Override
	public void onApply() throws Exception {
		getViewValues();
		validateInput();
		doUpdateEmail();
	}

	/**
	 * 验证输入
	 */
	private void validateInput() throws BusinessException {
		if (!validateEmailForm(emailValue)) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID(
					"sfapp", "MailBingPage-00012")/* 您输入的有邮箱不正确 */,
					IUserPubConstants.EMAIL_FORM_ERROR_CODE);
		}
	}

	private void doUpdateEmail() throws BusinessException {
		updateEmail();
		updateView();
		currentUser.setEmail(emailValue);
	}

	/**
	 * 更改用户的邮箱
	 */
	private void updateEmail() throws BusinessException {
		IUserPubService service = NCLocator.getInstance().lookup(
				IUserPubService.class);
		service.updateUserEmail(currentUser.getCuserid(), emailValue,
				RbacUserPwdUtil.getEncodedPassword(currentUser, password));
	}

	/**
	 * 获取用户输入的邮箱账号和密码
	 */
	private void getViewValues() {
		emailValue = mailTfd.getText();
		password = new String(pwdTfd.getPassword());
	}

	/**
	 * 更新界面
	 */
	private void updateView() {
		emailUL.setText(emailValue);
		mailPromptUL.setText(NCLangRes.getInstance().getStrByID("sfapp",
				"MailBingPage-00006")); /* 新邮箱 */
		promotUL1.setText(NCLangRes.getInstance().getStrByID("sfapp",
				"MailBingPage-00008")); /* 您已经成功绑定邮箱： */
		promotUL2.setText(NCLangRes.getInstance().getStrByID("sfapp",
				"MailBingPage-00009")); /*
										 * 输入新邮箱可以更改绑定邮箱。更改邮箱时需要您提供系统登录密码以确定您的身份。
										 */
		mailTfd.setText(null);
		pwdTfd.setText(null);
	}

	/**
	 * 检验输入的邮箱格式是否正确
	 * 
	 * @return
	 */
	private boolean validateEmailForm(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[_-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	private void showWorning(String wornMsg) {
		MessageDialog.showErrorDlg(
				this,
				NCLangRes.getInstance().getStrByID("sfapp",
						"MailBingPage-00010"), wornMsg);
	}

	@Override
	public void onOK() throws Exception {
		if (isEditStatus()) {
			onApply();
			/* "您还有没处理完的操作！" */
		} else {
			super.onCancel();
		}
	}

	/*
	 * 界面是否处于编辑状态
	 */
	private boolean isEditStatus() {
		boolean isEditing = true;
		if ("".equals(mailTfd.getText().trim())
				&& "".equals(new String(pwdTfd.getPassword()))) {
			isEditing = false;
		}
		return isEditing;
	}

	@Override
	public IExceptionHandler getExceptionHandler() {
		return this;
	}

	@Override
	public void handleException(final Throwable e) {
		SwingWorker<Object[], Object> sw = new SwingWorker<Object[], Object>() {
			@Override
			protected void done() {
				if (e instanceof BusinessException) {
					BusinessException ex = (BusinessException) e;
					if (IUserPubConstants.EMAIL_FORM_ERROR_CODE.equals(ex
							.getErrorCodeString())) {
						requestEmailFocus();
					} else if (IUserPubConstants.PASSWORG_ERROR_CODE.equals(ex
							.getErrorCodeString())) {
						requestPasswordFocus();
					}
				}
			}

			@Override
			protected Object[] doInBackground() throws Exception {
				showWorning(e.getMessage());
				return null;
			}
		};
		sw.execute();
	}

	private void requestEmailFocus() {
		mailTfd.requestFocus();
	}

	private void requestPasswordFocus() {
		pwdTfd.selectAll();
		pwdTfd.requestFocus();
	}
}
