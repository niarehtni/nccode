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
 * ���Ի���������󶨹���
 * 
 * @author liuxing0
 * 
 */
public class MailBingPage extends AbstractIndividuationPage implements
		IUAPDefaultSettingConst, IExceptionHandler {
	private static final long serialVersionUID = -6050467056666432780L;
	private UILabel mailPromptUL; // ����������ʾ
	private UITextField mailTfd;// ���������
	private UILabel pwdPromptUL;// ����������ʾ
	private UIPasswordField pwdTfd; // ���������
	private UIPanel mailPanel, pwdPanel, up1, up2; // �ֱ��ŵ����ʼ������룬������ʾpanel
	private UILabel promotUL1, promotUL2;// ������ʾ
	private UILabel emailUL;// ����������������
	private String emailValue;// �û����������
	private String password;// �û����������
	private UserVO currentUser;// ��ǰ��¼����

	public MailBingPage() {
		currentUser = WorkbenchEnvironment.getInstance().getLoginUser();
		initUI();
	}

	/**
	 * ��ʼ������󶨽���
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
	 * �����û��Ƿ�����������첻ͬ�Ŀؼ�
	 */
	private void createElementDiff() {
		mailPromptUL = new UILabel();
		mailPromptUL.setPreferredSize(new Dimension(80, 20));
		mailPromptUL.setHorizontalAlignment(SwingConstants.RIGHT);
		if (currentUser.getEmail() == null) {
			promotUL1 = new UILabel(NCLangRes.getInstance().getStrByID("sfapp",
					"MailBingPage-00003"));/* Ϊ������˻���ȫ����������䡣��ʱ��Ҫ�ṩϵͳ������ȷ����ݡ� */
			emailUL = new UILabel();
			promotUL2 = new UILabel();
			mailPromptUL.setText(NCLangRes.getInstance().getStrByID("sfapp",
					"MailBingPage-00004")); /* ���� */
		} else {
			promotUL1 = new UILabel(NCLangRes.getInstance().getStrByID("sfapp",
					"MailBingPage-00008")); // /*���Ѿ��ɹ������䣺*/
			emailUL = new UILabel(currentUser.getEmail());
			promotUL2 = new UILabel(NCLangRes.getInstance().getStrByID("sfapp",
					"MailBingPage-00009"));/*
											 * ������������Ը��İ����䡣��������ʱ��Ҫ���ṩϵͳ��¼������ȷ���������
											 * ��
											 */
			;
			mailPromptUL.setText(NCLangRes.getInstance().getStrByID("sfapp",
					"MailBingPage-00006")); /* ������ */
		}
		emailUL.setFont(new Font("����", Font.BOLD, 12));
	}

	/**
	 * ��ʼ������Panel
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
	 * ��������panel��������ʾ�������
	 */
	private void initPwdPanel() {
		pwdPanel = new UIPanel();
		pwdPanel.setOpaque(false);
		pwdPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 40, 5));
		pwdPromptUL = new UILabel();
		pwdPromptUL.setText(NCLangRes.getInstance().getStrByID("sfapp",
				"MailBingPage-00005"));/* ϵͳ��¼���� */
		pwdPromptUL.setPreferredSize(new Dimension(80, 20));
		pwdPromptUL.setHorizontalAlignment(SwingConstants.RIGHT);
		pwdPanel.add(pwdPromptUL);
		pwdTfd = new UIPasswordField();
		pwdTfd.setAllowOtherCharacter(true);
		pwdTfd.setPreferredSize(new Dimension(200, 20));
		pwdPanel.add(pwdTfd);
	}

	/**
	 * ��ʼ����ʾ��Ϣ��Panel
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
	 * ����
	 */
	@Override
	public String getDescription() {
		return NCLangRes.getInstance()
				.getStrByID("sfapp", "MailBingPage-00002")/*
														 * ������������������õȲ�������ǿ���˻��İ�ȫ��
														 * ��
														 */;
	}

	/**
	 * �������
	 */
	@Override
	public String getTitle() {
		return NCLangRes.getInstance()
				.getStrByID("sfapp", "MailBingPage-00001")/* ����� */;
	}

	@Override
	public void onApply() throws Exception {
		getViewValues();
		validateInput();
		doUpdateEmail();
	}

	/**
	 * ��֤����
	 */
	private void validateInput() throws BusinessException {
		if (!validateEmailForm(emailValue)) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID(
					"sfapp", "MailBingPage-00012")/* ������������䲻��ȷ */,
					IUserPubConstants.EMAIL_FORM_ERROR_CODE);
		}
	}

	private void doUpdateEmail() throws BusinessException {
		updateEmail();
		updateView();
		currentUser.setEmail(emailValue);
	}

	/**
	 * �����û�������
	 */
	private void updateEmail() throws BusinessException {
		IUserPubService service = NCLocator.getInstance().lookup(
				IUserPubService.class);
		service.updateUserEmail(currentUser.getCuserid(), emailValue,
				RbacUserPwdUtil.getEncodedPassword(currentUser, password));
	}

	/**
	 * ��ȡ�û�����������˺ź�����
	 */
	private void getViewValues() {
		emailValue = mailTfd.getText();
		password = new String(pwdTfd.getPassword());
	}

	/**
	 * ���½���
	 */
	private void updateView() {
		emailUL.setText(emailValue);
		mailPromptUL.setText(NCLangRes.getInstance().getStrByID("sfapp",
				"MailBingPage-00006")); /* ������ */
		promotUL1.setText(NCLangRes.getInstance().getStrByID("sfapp",
				"MailBingPage-00008")); /* ���Ѿ��ɹ������䣺 */
		promotUL2.setText(NCLangRes.getInstance().getStrByID("sfapp",
				"MailBingPage-00009")); /*
										 * ������������Ը��İ����䡣��������ʱ��Ҫ���ṩϵͳ��¼������ȷ��������ݡ�
										 */
		mailTfd.setText(null);
		pwdTfd.setText(null);
	}

	/**
	 * ��������������ʽ�Ƿ���ȷ
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
			/* "������û������Ĳ�����" */
		} else {
			super.onCancel();
		}
	}

	/*
	 * �����Ƿ��ڱ༭״̬
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
