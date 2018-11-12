package nc.login.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.plaf.metal.MetalComboBoxUI;

import nc.bcmanage.vo.BusiCenterVO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.bs.framework.common.UserExit;
import nc.bs.framework.comn.NetStreamContext;
import nc.bs.framework.util.KeyUtil;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.desktop.ui.actions.AbstractWorkbenchAction;
import nc.itf.uap.sfapp.SecurityLogInfo;
import nc.itf.uap.sfapp.SecurityLogVarItem;
import nc.itf.uap.sfapp.securityLog.SecurityLogManageFacade;
import nc.login.bs.LoginVerifyBean;
import nc.login.bs.LoginVerifyException;
import nc.login.ui.webstart.FlyPanel;
import nc.login.ui.webstart.WebStartAction;
import nc.login.ui.webstart.WebStartPanel;
import nc.login.vo.ISystemIDConstants;
import nc.login.vo.LoginRequest;
import nc.login.vo.LoginResponse;
import nc.pubitf.login.ILdapUserManager;
import nc.pubitf.login.LDAPAuthorization;
import nc.sfbase.beans.SingleColorArrowIcon;
import nc.sfbase.client.CheckDevice;
import nc.sfbase.client.ClientToolKit;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.pub.style.Style;
import nc.ui.sm.clientsetup.ClientSetup;
import nc.ui.sm.clientsetup.ClientSetupCache;
import nc.uitheme.ui.ThemeResourceCenter;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.Language;
import nc.vo.ml.LanguageTranslatorFactor;
import nc.vo.org.GroupVO;
import nc.vo.sm.UserVO;
import nc.vo.uap.rbac.profile.FunctionPermProfileManager;
import nc.vo.uap.rbac.util.RbacUserPwdUtil;
import sun.swing.DefaultLookup;

import com.dl.ui.CapsLockHintRegister;

public class LoginUISupport {
	private Map<String, Language> currLangMap = new HashMap();

	public class MLNullTranslatorAction extends AbstractWorkbenchAction {
		private static final long serialVersionUID = -7013174837807978293L;

		public MLNullTranslatorAction() {
			super();
			String name = NCLangRes.getInstance().getStrByID("smcomm",
					"UPP1005-000273")/* @res "显示资源ID号" */;
			setName(name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean check = false;
			Object b = getValue(Action.SELECTED_KEY);
			if (b != null && b instanceof Boolean) {
				check = (Boolean) b;
			}
			LanguageTranslatorFactor.getInstance().setNullTrans(check);
			refreshUIContrl();
		}
	}

	public class CloseWebStartPanelAction extends AbstractAction {
		private static final long serialVersionUID = 8802805371561236728L;

		public CloseWebStartPanelAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JComponent parent = (JComponent) getWebStartPanel().getParent();
			if (parent != null) {
				ClientSetup setup = ClientSetupCache.getGlobalClientSetup();
				setup.putBoolean("webstartpanel.closed", Boolean.TRUE);
				ClientSetupCache.storeGlobalClientSetup();

				JLayeredPane pane = parent.getRootPane().getLayeredPane();
				Rectangle rect = getWebStartPanel().getBounds();
				Rectangle rect2 = getMenuOption().getBounds();
				Point p1 = rect.getLocation();
				Point p2 = rect2.getLocation();
				p2.x += rect2.getWidth() / 2;
				fly(pane, rect.getSize(), p1, p2);

			}

		}

		private BufferedImage createImage(JComponent comp) {
			Dimension size = comp.getSize();
			BufferedImage img = new BufferedImage(size.width, size.height,
					BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g2 = null;
			try {
				g2 = img.createGraphics();
				comp.paintComponents(g2);
			} finally {
				if (g2 != null) {
					g2.dispose();
				}
			}
			return img;
		}

		private void fly(final JLayeredPane pane, Dimension size,
				final Point loc1, Point loc2) {
			final int dx = loc2.x - loc1.x;
			final int dy = loc2.y - loc1.y;
			final int dw = size.width;
			final int dh = size.height;
			BufferedImage img = createImage(getWebStartPanel());
			final FlyPanel flyPanel = new FlyPanel(img);
			flyPanel.setBounds(getWebStartPanel().getBounds());
			pane.add(flyPanel, JLayeredPane.POPUP_LAYER);
			loginUIContainer.remove(getWebStartPanel());

			SwingWorker<Object, Integer> sw = new SwingWorker<Object, Integer>() {
				int count = 100;
				int currValue = 0;

				@Override
				protected Object doInBackground() throws Exception {
					while (currValue++ < count) {
						Thread.sleep(8);
						publish(Integer.valueOf(currValue));
					}
					return null;
				}

				@Override
				protected void done() {
					pane.remove(flyPanel);
				}

				@Override
				protected void process(List<Integer> chunks) {
					int n = chunks.size();
					int val = chunks.get(n - 1);
					Rectangle rect = new Rectangle();
					rect.x = loc1.x + dx * val / count;
					rect.y = loc1.y + dy * val / count;
					rect.width = dw * (count - val) / count;
					rect.height = dh * (count - val) / count;
					flyPanel.setBounds(rect);
					flyPanel.repaint();

				}

			};

			sw.execute();

		}
	}

	public static class BusiCenterCBBCellRender extends DefaultListCellRenderer {
		private static final long serialVersionUID = 6386474428568356047L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Component comp = super.getListCellRendererComponent(list, value,
					index, isSelected, cellHasFocus);
			if (comp instanceof JLabel && value instanceof BusiCenterVO) {
				BusiCenterVO bcVO = (BusiCenterVO) value;
				if (bcVO.getCode().equals("0000")) {
					String text = NCLangRes.getInstance().getString("sfbase",
							"系统管理", "BusiCenterCache-000001")/* 系统管理 */;/*
																		 * -=
																		 * notranslate
																		 * =-
																		 */
					((JLabel) comp).setText(text);
				}
			}
			return comp;
		}

	}

	public static class MyTextFieldUI extends BasicTextFieldUI {
		private static class FocusHandler implements FocusListener {
			@Override
			public void focusGained(FocusEvent e) {
				onFocusChange(e);
			}

			@Override
			public void focusLost(FocusEvent e) {
				onFocusChange(e);
			}

			private void onFocusChange(FocusEvent e) {
				Component comp = e.getComponent();
				if (comp != null) {
					comp.repaint();
				}
			}
		}

		private static FocusHandler focusHandler = new FocusHandler();

		public static ComponentUI createUI(JComponent c) {
			return new MyTextFieldUI();
		}

		@Override
		public void installUI(JComponent c) {
			super.installUI(c);
			c.addFocusListener(focusHandler);
		}

		@Override
		public void uninstallUI(JComponent c) {
			super.uninstallUI(c);
			c.removeFocusListener(focusHandler);
		}

	}

	private static class ZipRemoteStreamAction extends AbstractAction {
		private static final long serialVersionUID = 4640550263604706078L;

		public ZipRemoteStreamAction() {
			super();
			super.putValue(Action.SELECTED_KEY, isNetStreamZip());
			String name = NCLangRes.getInstance().getStrByID("smcomm",
					"UPP1005-000246")/* 压缩远程调用流 */;
			super.putValue(Action.NAME, name);

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Boolean isSel = (Boolean) super.getValue(Action.SELECTED_KEY);
			setNetStreamZipProp(isSel);
			ClientSetup setup = ClientSetupCache.getGlobalClientSetup();
			setup.putBoolean("_isNetStreamZip", isSel);
			ClientSetupCache.storeGlobalClientSetup();

		}
	}

	private class LoginUIFocusTraversalPolicy extends
			LayoutFocusTraversalPolicy {
		private static final long serialVersionUID = -4634909526488382845L;

		@Override
		public Component getFirstComponent(Container container) {
			String text = getTfUserCode().getText().trim();
			if (text.length() == 0) {
				return getTfUserCode();
			} else if (!getPfUserPWD().isFocusOwner()) {
				return getPfUserPWD();
			} else {
				return super.getFirstComponent(container);
			}
		}

		@Override
		public Component getComponentAfter(Container container,
				Component component) {
			int index = focusTraveList.indexOf(component);
			if (index != -1) {
				index = index == focusTraveList.size() - 1 ? 0 : index + 1;
				return focusTraveList.get(index);

			}
			return super.getComponentAfter(container, component);
		}

		@Override
		public Component getComponentBefore(Container container,
				Component component) {
			int index = focusTraveList.indexOf(component);
			if (index != -1) {
				index = index == 0 ? focusTraveList.size() - 1 : index - 1;
				return focusTraveList.get(index);

			}
			return super.getComponentBefore(container, component);
		}

		@Override
		protected boolean accept(Component component) {
			return focusTraveList.contains(component);
		}

	}

	private class KeyHandler extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getModifiers() == 0) {
				Object source = e.getSource();
				if (source.equals(getPfUserPWD())) {
					getBtnLogin().doClick();
				} else if (source.equals(getTfUserCode())) {
					KeyboardFocusManager.getCurrentKeyboardFocusManager()
							.focusNextComponent();
				}
			}
		}
	}

	private class ItemHandler implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				Object source = e.getSource();
				if (getCbbBusiCenter().equals(source)) {
					doCbbBusiCenterSelectChange();
				} else if (getCbbLanguage().equals(source)) {
					doCbbLanguageSelectChange();
				}
			}
		}

	}

	private class LoginAction extends AbstractAction {
		private static final long serialVersionUID = -8198578873503150213L;

		private LoginAction() {
		}

		public void actionPerformed(ActionEvent e) {
			Runnable run = new Runnable() {
				public void run() {
					try {
						LoginRequest request = getLoginRequest();

						// ssx added for LDAP authorization instead of Standard
						// 2017-11-13
						Logger.error("----------wits-ad-login-begin------------");
						if (!WorkbenchEnvironment.getInstance().isSuperAdm()) {
							LoginVerifyBean verifyBean = new LoginVerifyBean(
									ISystemIDConstants.NCSYSTEM);
							String bcCode = request.getBusiCenterCode();
							Logger.error("----------bcCode='" + bcCode
									+ "'------------");
							BusiCenterVO bcVO = verifyBean
									.findBusiCenter(bcCode);
							UserVO user = verifyBean.findUser(
									bcVO.getDataSourceName(),
									request.getUserCode());

							if (user != null && user.getUser_type() == 1
									&& user.getBase_doc_type() == 0) {
								Logger.error("----------user='"
										+ user.getUser_code() + "'------------");
								String adpassword = request.getUserPWD();
								Logger.error("----------password='"
										+ adpassword + "'------------");

								// LDAP Check
								LDAPAuthorization ldapAuth = new LDAPAuthorization(
										request.getUserCode(), adpassword,
										request, false);

								ldapAuth.doAction();
								Logger.error("----------finish-ad-verify------------");

								// LDAP Passed, Change NC Password to LDAP
								// Password
								String encPassword = RbacUserPwdUtil
										.getEncodedPassword(user, adpassword);
								String originDataSource = InvocationInfoProxy
										.getInstance().getUserDataSource();
								InvocationInfoProxy.getInstance()
										.setUserDataSource(
												bcVO.getDataSourceName());
								ILdapUserManager svc = NCLocator.getInstance()
										.lookup(ILdapUserManager.class);
								svc.updateUserPassword(user.getCuserid(),
										encPassword);
								InvocationInfoProxy.getInstance()
										.setUserDataSource(originDataSource);
								Logger.error("----------finish-change-password------------");
							}
						}

						UserExit.getInstance().setUserCode(
								request.getUserCode());

						LoginAssistant loginAssi = createLoginAssistant(request);
						LoginResponse response = loginAssi.login();
						int result = response.getLoginResult();

						if (result == 0) {
							loginAssi.showWorkbench(response);
							LoginUISupport.this.loginEndInDT(result, null);
							LoginUISupport.this.storeLoginRequest(request);

							LoginUISupport.this.getService()
									.writeSecurityLogSync(
											LoginUISupport.this
													.writeSecurityLog(true));
						} else {
							LoginUISupport.this.getService()
									.writeSecurityLogSync(
											LoginUISupport.this
													.writeSecurityLog(false));

							String msg = loginAssi.getResultMessage(result);
							LoginUISupport.this.loginEndInDT(result, "" + msg);
							NetStreamContext.resetAll();
						}
					} catch (LoginVerifyException ex) {
						LoginUISupport.this.getService().writeSecurityLogSync(
								LoginUISupport.this.writeSecurityLog(false));

						NetStreamContext.resetAll();
						String errMsg = ex.getMessage();
						if ((errMsg == null) || (errMsg.trim().length() == 0)) {
							errMsg = ex.getClass().getName() + ":" + errMsg;
						}
						Logger.error(ex.getMessage(), ex);
						LoginUISupport.this.loginEndInDT(ex.getLoginResult(),
								errMsg);
					} catch (Exception ex) {
						LoginUISupport.this.getService().writeSecurityLogSync(
								LoginUISupport.this.writeSecurityLog(false));

						NetStreamContext.resetAll();
						String errMsg = ex.getMessage();
						if ((errMsg == null) || (errMsg.trim().length() == 0)) {
							errMsg = ex.getClass().getName() + ":" + errMsg;
						}
						Logger.error(ex.getMessage(), ex);
						LoginUISupport.this.loginEndInDT(-1, errMsg);
					}

				}
			};
			loginStart();
			new Thread(run, "loginNC").start();
		}
	}

	private static class ResultMessageLabel extends JLabel {
		private static final long serialVersionUID = 3429379046855159898L;

		public ResultMessageLabel() {
			setOpaque(false);
			setForeground(ThemeResourceCenter.getInstance().getColor(
					"themeres/login/loginuiResConf",
					"loginui.resultMsgLabelForegroundColor"));

			setHorizontalAlignment(0);
			setIconTextGap(6);

			ImageIcon icon = LoginUIConfig.getInstance().getErrorIcon();
			setIcon(icon);
		}

		public void setText(String text) {
			if (text == null)
				text = "null";
			super.setText(text);
			Font f = getFont();
			if (f != null) {
				FontMetrics fm = getFontMetrics(f);
				int w = SwingUtilities.computeStringWidth(fm, text);
				int h = fm.getHeight() + 2;
				setPreferredSize(new Dimension(w + 30, h));
			}
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
		}
	}

	private static class LanguageComboboxUI extends MetalComboBoxUI {
		public void paintCurrentValueBackground(Graphics g, Rectangle bounds,
				boolean hasFocus) {
		}

		@Override
		protected JButton createArrowButton() {
			JButton btn = new JButton() {
				private static final long serialVersionUID = 9205662544175749923L;

				@Override
				public void paintComponent(Graphics g) {
					Insets insets = new Insets(8, 1, 1, 3);
					int width = getWidth() - (insets.left + insets.right);
					int height = getHeight() - (insets.top + insets.bottom);
					if (height <= 0 || width <= 0) {
						return;
					}
					Icon comboIcon = getIcon();
					if (comboIcon != null) {
						int iconWidth = comboIcon.getIconWidth();
						int iconLeft = (getWidth() / 2) - (iconWidth / 2);
						int iconTop = insets.top;
						comboIcon.paintIcon(this, g, iconLeft, iconTop);

					}

				}

			};
			btn.setFocusable(false);
			btn.setRequestFocusEnabled(false);
			btn.setFocusPainted(false);
			btn.setBorderPainted(false);
			btn.setContentAreaFilled(false);
			btn.setMargin(new Insets(15, 1, 1, 3));
			btn.setIcon(arrowIcon);
			// if (btn instanceof MetalComboBoxButton && arrowIcon != null) {
			// ((MetalComboBoxButton) btn).setComboIcon(arrowIcon);
			// }
			return btn;
		}

		protected void installComponents() {
			super.installComponents();
			comboBox.setBorder(null);
		}

		public void paintCurrentValue(Graphics g, Rectangle bounds,
				boolean hasFocus) {
			ListCellRenderer renderer = comboBox.getRenderer();
			Component c;

			if (hasFocus && !isPopupVisible(comboBox)) {
				c = renderer.getListCellRendererComponent(listBox,
						comboBox.getSelectedItem(), -1, true, false);
			} else {
				c = renderer.getListCellRendererComponent(listBox,
						comboBox.getSelectedItem(), -1, false, false);
				c.setBackground(UIManager.getColor("ComboBox.background"));
			}
			Color foreground = LoginUIConfig.getInstance()
					.getSysButtonFgColor();
			c.setFont(comboBox.getFont());
			boolean oldOpaque = true;
			if (c instanceof JComponent) {
				oldOpaque = ((JComponent) c).isOpaque();
				((JComponent) c).setOpaque(false);
			}
			if (hasFocus && !isPopupVisible(comboBox)) {
				c.setForeground(foreground);
				c.setBackground(listBox.getSelectionBackground());
			} else {
				if (comboBox.isEnabled()) {
					c.setForeground(foreground);
					c.setBackground(comboBox.getBackground());
				} else {
					c.setForeground(foreground);
					c.setBackground(DefaultLookup.getColor(comboBox, this,
							"ComboBox.disabledBackground", null));
				}
			}

			// Fix for 4238829: should lay out the JPanel.
			boolean shouldValidate = false;
			if (c instanceof JPanel) {
				shouldValidate = true;
			}

			int x = bounds.x, y = bounds.y, w = bounds.width, h = bounds.height;
			// if (padding != null) {
			// x = bounds.x + padding.left;
			// y = bounds.y + padding.top;
			// w = bounds.width - (padding.left + padding.right);
			// h = bounds.height - (padding.top + padding.bottom);
			// }

			currentValuePane.paintComponent(g, c, comboBox, x, y, w, h,
					shouldValidate);
			if (c instanceof JComponent) {
				((JComponent) c).setOpaque(oldOpaque);
			}
		}
	}

	private class DocumentHandler implements DocumentListener {
		public DocumentHandler() {
			super();

		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			dealEvent(e);
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			dealEvent(e);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			dealEvent(e);
		}

		private void dealEvent(DocumentEvent e) {
			DefaultLoginUIBackGroundPainter painter = (DefaultLoginUIBackGroundPainter) getUIBGPainter();
			boolean b = getTfUserCode().getDocument().getLength() > 0
					&& getPfUserPWD().getDocument().getLength() > 0;
			if (b) {
				painter.setLoginBtnLightIcon(LoginUIConfig.getInstance()
						.getLoginBtnLightIcon());
			} else {
				painter.setLoginBtnLightIcon(null);
			}
			loginUIContainer.repaint();
		}
	}

	private Container loginUIContainer = null;

	private JComboBox cbbBusiCenter = null;
	private UIComboBox cbbLanguage = null;
	private JTextField tfUserCode = null;
	private JPasswordField pfUserPWD = null;
	private JLabel lblBusiCenter = null;
	private JLabel lblUserCode = null;
	private JLabel lblPWD = null;
	private JLabel lblCopyright = null;

	private JButton btnLogin = null;
	private JButton btnOption = null;
	private boolean isShowOptionPanel = false;
	private JPanel optionPanel = null;
	private JButton menuOption = null;
	private JPopupMenu popupOption = null;

	private JCheckBox ckbZipRemoteStream = null;

	private JMenuItem miEnableWebstart = null;
	private JCheckBoxMenuItem zipStreamCBMI = null;
	private JCheckBoxMenuItem nullTransCBMI = null;

	private ResultMessageLabel resultLbl = null;
	private JLabel lblLoginFlash = null;

	private JButton btnForgetPWD = null;
	private Dimension ctrlSize = new Dimension(160, 20);
	private Dimension defCtrlSize = new Dimension(260, 35);
	private ItemHandler itemHandler = new ItemHandler();
	protected LoginUIFocusTraversalPolicy focusTraversalPolicy = new LoginUIFocusTraversalPolicy();
	private KeyHandler keyHandler = new KeyHandler();
	private List<Component> focusTraveList = new ArrayList();

	private ILoginUIBackGroundPainter uiBGPainter = null;
	private LayoutManager loginUILayoutManager = null;
	private Shape loginBtnShape = null;
	private DocumentHandler documentHandler = new DocumentHandler();

	private WebStartPanel wsPanel = null;

	private static Properties oslangMap = null;
	private static Icon arrowIcon = null;

	static {
		if (LoginUIConfig.getInstance().isCustomLoginUI()) {
			arrowIcon = new SingleColorArrowIcon(LoginUIConfig.getInstance()
					.getSysButtonFgColor(), 10, 6);
		} else {
			arrowIcon = LoginUIConfig.getInstance().getDownArrowIcon();
		}
	}

	public LoginUISupport(Container loginUIContainer) {
		this.loginUIContainer = loginUIContainer;
		initialize();
	}

	private void initialize() {
		boolean isSel = isNetStreamZip();
		setNetStreamZipProp(isSel);

		UserExit.getInstance().setKeepToken(true);

		focusTraveList.add(getCbbBusiCenter());
		focusTraveList.add(getTfUserCode());
		focusTraveList.add(getPfUserPWD());
		focusTraveList.add(getBtnLogin());

		loginUIContainer.setFocusTraversalPolicyProvider(true);
		loginUIContainer.setFocusTraversalPolicy(focusTraversalPolicy);
	}

	public LayoutManager getLoginUILayoutManager() {
		if (loginUILayoutManager == null) {
			if (LoginUIConfig.getInstance().isCustomLoginUI()) {
				loginUILayoutManager = new LoginUILayout(this);
			} else {
				loginUILayoutManager = new DefaultLoginUILayout(this);
			}
		}
		return loginUILayoutManager;
	}

	public ILoginUIBackGroundPainter getUIBGPainter() {
		if (uiBGPainter == null) {
			if (LoginUIConfig.getInstance().isCustomLoginUI()) {
				uiBGPainter = new LoginUIBackGroundPainter(this);
			} else {
				uiBGPainter = new DefaultLoginUIBackGroundPainter(this);
			}
		}
		return uiBGPainter;
	}

	public JLabel getLblLoginFlash() {
		if (lblLoginFlash == null) {
			lblLoginFlash = new JLabel() {
				private long count = 0L;
				private static final long serialVersionUID = -4629883810823712506L;

				public boolean imageUpdate(Image img, int infoflags, int x,
						int y, int w, int h) {
					boolean b = super.imageUpdate(img, infoflags, x, y, w, h);
					try {
						Thread.sleep(count / 10L);
						if (count < 800L) {
							count += 5L;
						}
					} catch (Exception e) {
					}
					return b;
				}
			};
			ImageIcon icon = getLoginFlashImage();
			if (icon != null) {
				lblLoginFlash.setIcon(icon);
				lblLoginFlash
						.setSize(icon.getIconWidth(), icon.getIconHeight());

				lblLoginFlash.setPreferredSize(new Dimension(icon
						.getIconWidth(), icon.getIconHeight()));
			}

			lblLoginFlash.setOpaque(false);
			lblLoginFlash
					.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

			lblLoginFlash.setVisible(false);
		}
		return lblLoginFlash;
	}

	private ImageIcon getLoginFlashImage() {
		ImageIcon icon = null;
		if (LoginUIConfig.getInstance().isCustomLoginUI()) {
			icon = ClientToolKit.loadImageIcon("images/loginui6x/progress.gif");
		} else {
			icon = ThemeResourceCenter.getInstance().getImage(
					"themeres/loading/login_loading.gif");
		}

		return icon;
	}

	public JComboBox getCbbBusiCenter() {
		if (cbbBusiCenter == null) {
			if (LoginUIConfig.getInstance().isCustomLoginUI()) {
				cbbBusiCenter = new UIComboBox();
				cbbBusiCenter.setEditable(false);
				cbbBusiCenter.setPreferredSize(ctrlSize);
				cbbBusiCenter.setSize(ctrlSize);
			} else {
				cbbBusiCenter = new JComboBox();
				cbbBusiCenter.setUI(new CComboboxUI());
				cbbBusiCenter.setEditable(false);
				cbbBusiCenter.setPreferredSize(defCtrlSize);
				cbbBusiCenter.setSize(defCtrlSize);
			}
			cbbBusiCenter.setFont(getControlFont());
			cbbBusiCenter.setRenderer(new BusiCenterCBBCellRender());
			if (ClientToolKit.isAdminConsole()) {
				cbbBusiCenter.setEnabled(false);
			} else {
				cbbBusiCenter.setEnabled(true);
			}
		}
		return cbbBusiCenter;
	}

	public UIComboBox getCbbLanguage() {
		if (cbbLanguage == null) {
			cbbLanguage = new UIComboBox() {
				private static final long serialVersionUID = 8114875352879555168L;

				@Override
				public void updateUI() {
					setUI(new LanguageComboboxUI());
				}
			};
			cbbLanguage.setOpaque(false);
			cbbLanguage.setEditable(false);
			// cbbLanguage.setForeground(LoginUIConfig.getInstance().getSysButtonFgColor());

			// cbbLanguage.setPreferredSize(new Dimension(85,23));
			cbbLanguage.setSize(new Dimension(115, 23));
			cbbLanguage.setFont(getControlFont());
		}
		return cbbLanguage;
	}

	protected void doCbbLanguageSelectChange() {
		Language selLang = (Language) getCbbLanguage().getSelectedItem();
		NCLangRes.getInstance().setCurrLanguage(selLang);
		UserExit.getInstance().setLangCode(selLang.getCode());
		getLblResultMessage().setVisible(false);

		new Thread() {
			public void run() {
				NCLangRes.getInstance().getStrByID("MENUCODE", "10");
				NCLangRes.getInstance().getStrByID("FUNCODE", "10");

			}

		}.start();
		refreshUIContrl();
	}

	protected void doCbbBusiCenterSelectChange() {
		BusiCenterVO selBC = (BusiCenterVO) getCbbBusiCenter()
				.getSelectedItem();

		InvocationInfoProxy.getInstance().reset();
		UserExit.getInstance().setUserDataSource(selBC.getDataSourceName());
		UserExit.getInstance().setBizCenterCode(selBC.getCode());
	}

	public void initCbbBusiCenter(BusiCenterVO[] bcVOs) {
		getCbbBusiCenter().removeItemListener(itemHandler);
		getCbbBusiCenter().removeAllItems();
		int bcCount = bcVOs == null ? 0 : bcVOs.length;
		boolean isAdminConsole = ClientToolKit.isAdminConsole();
		for (int i = 0; i < bcCount; i++) {
			if ((RuntimeEnv.getInstance().isDevelopMode())
					|| (!(isAdminConsole ^ "0000".equals(bcVOs[i].getCode())))) {

				getCbbBusiCenter().addItem(bcVOs[i]);
			}
		}
		getCbbBusiCenter().setSelectedIndex(-1);
		getCbbBusiCenter().addItemListener(itemHandler);
		if (getCbbBusiCenter().getModel().getSize() > 0) {
			getCbbBusiCenter().setSelectedIndex(0);
		}
	}

	public void initCbbLanguage(Language[] langs) {
		getCbbLanguage().removeItemListener(itemHandler);
		getCbbLanguage().removeAllItems();
		int langCount = langs == null ? 0 : langs.length;
		int maxW = 0;
		FontMetrics fm = getCbbLanguage().getFontMetrics(
				getCbbLanguage().getFont());

		for (int i = 0; i < langCount; i++) {
			getCbbLanguage().addItem(langs[i]);
			String disName = langs[i].getDisplayName();
			if (disName != null) {
				int width = fm.stringWidth(disName);
				if (width > maxW) {
					maxW = width;
				}
			}
		}
		getCbbLanguage().setPreferredSize(new Dimension(maxW + 20, 23));
		getCbbLanguage().setSelectedIndex(-1);
		getCbbLanguage().addItemListener(itemHandler);
		if (langCount > 0) {
			getCbbLanguage().setSelectedIndex(0);
		}
	}

	public JTextField getTfUserCode() {
		if (tfUserCode == null) {
			if (LoginUIConfig.getInstance().isCustomLoginUI()) {
				tfUserCode = new JTextField();
				tfUserCode.setSize(ctrlSize);
				tfUserCode.setPreferredSize(ctrlSize);
				tfUserCode.setFont(getControlFont());
				tfUserCode.addKeyListener(keyHandler);
			} else {
				tfUserCode = new JTextField() {
					private static final long serialVersionUID = 1203731583509013110L;
					private Insets insets = new Insets(6, 6, 6, 6);
					private Color borderColor = LoginUIConfig.getInstance()
							.getInputCtrlBorderColor();
					private Color borderFocusColor = LoginUIConfig
							.getInstance().getInputCtrlBorderFocusColor();

					@Override
					protected void paintComponent(Graphics g) {
						Dimension size = getSize();
						Shape shape = getRoundShape(0, 0, size.width,
								size.height, insets);
						Color bColor = this.isFocusOwner() ? borderFocusColor
								: borderColor;
						int w = this.isFocusOwner() ? 2 : 1;
						paintCtrlBorder((Graphics2D) g, this, Color.white,
								bColor, shape, w);
						super.paintComponent(g);
					}

				};
				tfUserCode.putClientProperty("caretWidth", Integer.valueOf(2));
				tfUserCode.setCaretColor(LoginUIConfig.getInstance()
						.getInputCtrlBorderFocusColor());
				tfUserCode.setOpaque(false);
				tfUserCode.setBorder(BorderFactory
						.createEmptyBorder(7, 7, 7, 7));
				tfUserCode.setFont(getControlFont());
				tfUserCode.addKeyListener(keyHandler);
				tfUserCode.setSize(defCtrlSize);
				tfUserCode.setPreferredSize(defCtrlSize);
				tfUserCode.getDocument().addDocumentListener(documentHandler);
			}
			tfUserCode.setUI(new MyTextFieldUI());
		}
		return tfUserCode;
	}

	public JPasswordField getPfUserPWD() {
		if (pfUserPWD == null) {
			if (LoginUIConfig.getInstance().isCustomLoginUI()) {
				pfUserPWD = new JPasswordField();
				pfUserPWD.setSize(ctrlSize);
				pfUserPWD.setPreferredSize(ctrlSize);
				pfUserPWD.addKeyListener(keyHandler);
				CapsLockHintRegister
						.registerCapsLockHint(
								pfUserPWD,
								NCLangRes.getInstance().getStrByID("sfbase",
										"sfbase-0015")/* CapsLock已启用 */);
			} else {
				pfUserPWD = new JPasswordField() {
					private static final long serialVersionUID = -2364319261991684160L;
					private Insets insets = new Insets(6, 6, 6, 6);
					private Color borderColor = LoginUIConfig.getInstance()
							.getInputCtrlBorderColor();
					private Color borderFocusColor = LoginUIConfig
							.getInstance().getInputCtrlBorderFocusColor();

					@Override
					protected void paintComponent(Graphics g) {
						Dimension size = getSize();
						Shape shape = getRoundShape(0, 0, size.width,
								size.height, insets);
						Color bColor = this.isFocusOwner() ? borderFocusColor
								: borderColor;
						int w = this.isFocusOwner() ? 2 : 1;
						paintCtrlBorder((Graphics2D) g, this, Color.white,
								bColor, shape, w);
						super.paintComponent(g);
					}

				};
				pfUserPWD.putClientProperty("caretWidth", Integer.valueOf(2));
				pfUserPWD.setCaretColor(LoginUIConfig.getInstance()
						.getInputCtrlBorderFocusColor());
				pfUserPWD.setOpaque(false);
				pfUserPWD
						.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
				pfUserPWD.addKeyListener(keyHandler);
				CapsLockHintRegister
						.registerCapsLockHint(
								pfUserPWD,
								NCLangRes.getInstance().getStrByID("sfbase",
										"sfbase-0015")/* CapsLock已启用 */);
				pfUserPWD.setSize(defCtrlSize);
				pfUserPWD.setPreferredSize(defCtrlSize);
				pfUserPWD.getDocument().addDocumentListener(documentHandler);
			}

		}
		return pfUserPWD;
	}

	public JButton getForgetPWD() {
		if (btnForgetPWD == null) {
			btnForgetPWD = new JButton(NCLangRes.getInstance().getStrByID(
					"loginui", "loginuires-000007"));

			btnForgetPWD.setUI(new CustomUI());
			btnForgetPWD.setFont(new Font(Style.getFontname(), 0, 12));
			btnForgetPWD.setOpaque(false);
			btnForgetPWD.setCursor(Cursor.getPredefinedCursor(12));

			btnForgetPWD.setRolloverEnabled(true);
			btnForgetPWD.setBorder(BorderFactory.createEmptyBorder());
			btnForgetPWD.setHorizontalAlignment(4);
			btnForgetPWD.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					BusiCenterVO bcVO = (BusiCenterVO) cbbBusiCenter
							.getSelectedItem();

					String dsName = bcVO.getDataSourceName();
					InvocationInfoProxy.getInstance().setUserDataSource(dsName);
					PassWordWizardDialog wd = new PassWordWizardDialog(
							loginUIContainer, LoginUISupport.this
									.getSteps(dsName), null);

					Dimension screan = Toolkit.getDefaultToolkit()
							.getScreenSize();

					wd.setSize(647, 390);
					Dimension dlgsize = wd.getSize();
					wd.setLocation((screan.width - dlgsize.width) / 2,
							(screan.height - dlgsize.height) / 2 + 40);

					wd.showModal();
				}
			});
		}
		return btnForgetPWD;
	}

	private List<WizardStep> getSteps(String dsName) {
		List<WizardStep> steps = new ArrayList();
		String[] setpTitle = {
				NCLangRes.getInstance().getStrByID("loginui",
						"retrievepwd-000002"),
				NCLangRes.getInstance().getStrByID("loginui",
						"retrievepwd-000003"),
				NCLangRes.getInstance().getStrByID("loginui",
						"retrievepwd-000004"),
				NCLangRes.getInstance().getStrByID("loginui",
						"retrievepwd-000005") };

		for (int i = 0; i < 4; i++) {
			WizardStep step = new WizardStep();
			step.setTitle(NCLangRes.getInstance().getStrByID("loginui",
					"retrievepwd-000001"));

			step.setDescription(setpTitle[i]);
			if (i == 0) {
				step.setComp(new PassWordWizardFirstStep(dsName));
			} else if (i == 1) {
				step.setComp(new PassWordWizarSecondStep(dsName));
			} else if (i == 2) {
				step.setComp(new PassWordWizardThirdStep(dsName));
			} else if (i == 3) {
				step.setComp(new PassWordWizardFourthStep(dsName));
			}
			steps.add(step);
		}

		return steps;
	}

	private class CustomUI extends BasicButtonUI {
		private CustomUI() {
		}

		protected void paintText(Graphics g, JComponent c, Rectangle textRect,
				String text) {
			super.paintText(g, c, textRect, text);
			Dimension size = c.getSize();
			AbstractButton b = (AbstractButton) c;
			ButtonModel model = b.getModel();
			if (!model.isRollover()) {
				c.setForeground(new Color(25, 138, 232));
				g.drawLine(textRect.getLocation().x, textRect.getLocation().y
						+ size.height - 1, textRect.getLocation().x
						+ size.width, textRect.getLocation().y + size.height
						- 1);
			} else {
				c.setForeground(new Color(6, 108, 192));
				g.drawLine(textRect.getLocation().x, textRect.getLocation().y
						+ size.height - 1, textRect.getLocation().x
						+ size.width, textRect.getLocation().y + size.height
						- 1);
			}
		}
	}

	public JLabel getLblBusiCenter() {
		if (lblBusiCenter == null) {
			lblBusiCenter = new JLabel(getLblBusiCenterText());
			lblBusiCenter.setFont(getLblFont());
			lblBusiCenter.setForeground(LoginUIConfig.getInstance()
					.getMainCtrlFgColor());
		}

		return lblBusiCenter;
	}

	private String getLblBusiCenterText() {
		return NCLangRes.getInstance().getStrByID("loginui",
				"loginuires-000000");
	}

	public JLabel getLblUserCode() {
		if (lblUserCode == null) {
			lblUserCode = new JLabel(getLblUserCodeText());
			lblUserCode.setFont(getLblFont());
			lblUserCode.setForeground(LoginUIConfig.getInstance()
					.getMainCtrlFgColor());
		}

		return lblUserCode;
	}

	private String getLblUserCodeText() {
		return NCLangRes.getInstance().getStrByID("loginui",
				"loginuires-000001");
	}

	private String getWebStartPanelHintText() {
		return NCLangRes.getInstance().getStrByID("sfbase", "webstart-0001");
	}

	private String getStartWebStartBtnText() {
		return NCLangRes.getInstance().getStrByID("sfbase", "webstart-0002");
	}

	public JLabel getLblPWD() {
		if (lblPWD == null) {
			lblPWD = new JLabel(getLblPWDText());
			lblPWD.setFont(getLblFont());
			lblPWD.setForeground(LoginUIConfig.getInstance()
					.getMainCtrlFgColor());
		}

		return lblPWD;
	}

	private String getLblPWDText() {
		return NCLangRes.getInstance().getStrByID("loginui",
				"loginuires-000002");
	}

	public JLabel getLblCopyright() {
		if (lblCopyright == null) {
			lblCopyright = new JLabel(getCopyrightText());
			lblCopyright.setForeground(LoginUIConfig.getInstance()
					.getCopyrightFgColor());

			lblCopyright.setBorder(BorderFactory.createEmptyBorder());
		}
		return lblCopyright;
	}

	private String getCopyrightText() {
		String str = NCLangRes.getInstance().getStrByID("loginui",
				"loginuires-000005");

		return str.trim();
	}

	public Shape getLoginBtnShape() {
		if (loginBtnShape == null) {
			Dimension size = getBtnLogin().getSize();
			GeneralPath path = new GeneralPath();
			path.moveTo(size.width - 1, 0.0F);
			path.lineTo(size.width - 1, size.height - 66);
			path.curveTo(size.width - 1, size.height - 66 + 12, 42.0F,
					size.height - 8, 0.0F, size.height - 1);

			path.lineTo(0.0F, 63.0F);
			path.curveTo(3.0F, 30.0F, size.width - 21, 4.0F, size.width, 0.0F);
			loginBtnShape = path;
		}
		return loginBtnShape;
	}

	private JButton createDefaultLoginBtn() {
		JButton btn = new JButton() {
			private static final long serialVersionUID = 340566138580274126L;

			public void setText(String text) {
				super.setText(null);
			}

			public boolean contains(int x, int y) {
				return getLoginBtnShape().contains(x, y);
			}

			public void doClick(int pressTime) {
				model.setArmed(true);
				model.setPressed(true);
				try {
					Thread.sleep(pressTime);
				} catch (InterruptedException ie) {
				}
				model.setPressed(false);
				model.setArmed(false);
			}

		};
		btn.setAction(new LoginAction());
		btn.setFont(getControlFont());
		btn.setMargin(new Insets(0, 0, 0, 0));
		ImageIcon icon = LoginUIConfig.getInstance().getLoginBtnIcon();
		Dimension size = new Dimension(icon.getIconWidth(),
				icon.getIconHeight());

		btn.setPreferredSize(size);
		btn.setSize(size);
		btn.setOpaque(false);

		btn.setContentAreaFilled(false);
		btn.setBorderPainted(false);
		btn.setText(null);
		btn.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				LoginUISupport.this.updateLoginBtnBackground();

			}

		});
		btn.setToolTipText(getBtnLoginToolTipText());

		return btn;
	}

	private void updateLoginBtnBackground() {
		if (LoginUIConfig.getInstance().isCustomLoginUI()) {
			return;
		}
		ButtonModel model = getBtnLogin().getModel();
		ImageIcon icon = null;
		if (isSimpleChinese()) {
			icon = LoginUIConfig.getInstance().getLoginBtnIcon();
		} else {
			icon = LoginUIConfig.getInstance().getLoginBtnEnglishIcon();
		}

		if (!model.isEnabled() ? model.isSelected() :

		(model.isPressed()) && (model.isArmed())) {
		}

		if (model.isRollover()) {
			if (isSimpleChinese()) {
				icon = LoginUIConfig.getInstance().getLoginBtnOverIcon();
			} else {
				icon = LoginUIConfig.getInstance().getLoginBtnEnglishOverIcon();
			}
			if (!model.isSelected()) {
			}

		} else if (!model.isSelected()) {
		}

		DefaultLoginUIBackGroundPainter painter = (DefaultLoginUIBackGroundPainter) getUIBGPainter();
		painter.setLoginBtnBGIcon(icon);

		loginUIContainer.repaint();
	}

	private boolean isSimpleChinese() {
		return NCLangRes.getInstance().getCurrLanguage().getCode()
				.equalsIgnoreCase("simpchn");
	}

	private JButton createCustomLoginUILoginBtn() {
		JButton btn = new JButton();
		btn = new JButton();
		btn.setAction(new LoginAction());
		btn.setFont(getControlFont());
		Dimension size = new Dimension(70, 20);
		btn.setPreferredSize(size);
		btn.setSize(size);
		btn.setText(getBtnLoginText());
		btn.setToolTipText(getBtnLoginToolTipText());
		btn.setForeground(LoginUIConfig.getInstance().getMainCtrlFgColor());
		return btn;
	}

	public JButton getBtnLogin() {
		if (btnLogin == null) {
			if (LoginUIConfig.getInstance().isCustomLoginUI()) {
				btnLogin = createCustomLoginUILoginBtn();
			} else {
				btnLogin = createDefaultLoginBtn();
			}
		}
		return btnLogin;
	}

	public JButton getBtnOption() {
		if (btnOption == null) {
			try {
				btnOption = new JButton();
				btnOption.setText(NCLangRes.getInstance().getStrByID(
						"sysframev5", "UPPsysframev5-000060"));

				btnOption.setHorizontalTextPosition(2);
				btnOption.setFont(new Font(Style.getFontname(), 0, 12));

				btnOption.setIcon(ClientToolKit
						.loadImageIcon("images/loginicon/rightarrow.gif"));

				setShowOptionPanel(false);
				btnOption.setPreferredSize(new Dimension(70, 20));
				btnOption.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setShowOptionPanel(!isShowOptionPanel());
					}
				});
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}
		}

		return btnOption;
	}

	public boolean isShowOptionPanel() {
		return isShowOptionPanel;
	}

	public void setShowOptionPanel(boolean isShowOptionPanel) {
		this.isShowOptionPanel = isShowOptionPanel;
		if (isShowOptionPanel()) {
			getBtnOption().setIcon(
					ClientToolKit
							.loadImageIcon("images/loginicon/leftarrow.gif"));
		} else {
			getBtnOption().setIcon(
					ClientToolKit
							.loadImageIcon("images/loginicon/rightarrow.gif"));
		}

		getOptionPanel().setVisible(this.isShowOptionPanel);
	}

	public JPanel getOptionPanel() {
		if (optionPanel == null) {
			optionPanel = new JPanel();
			optionPanel.setOpaque(false);
			optionPanel.setLayout(null);
			optionPanel.setFocusable(false);
			optionPanel.setRequestFocusEnabled(false);
			optionPanel.setPreferredSize(new Dimension(600, 46));
			optionPanel.add(getCkbZipRemoteStream());
			optionPanel.add(getMiEnableWebstart());
		}
		return optionPanel;
	}

	public WebStartPanel getWebStartPanel() {
		if (wsPanel == null) {
			wsPanel = new WebStartPanel();
			wsPanel.setWebStartPanelHintText(getWebStartPanelHintText());
			wsPanel.setStartWebStartBtnText(getStartWebStartBtnText());
			wsPanel.setCloseBtnAction(new CloseWebStartPanelAction());
		}
		return wsPanel;
	}

	public JButton getMenuOption() {
		if (menuOption == null) {
			menuOption = new JButton(getMenuOptionText()) {
				private static final long serialVersionUID = -508472560900674587L;

				public Dimension getPreferredSize() {
					Dimension d = super.getPreferredSize();
					d.height = 23;
					return d;
				}

			};
			menuOption.setOpaque(false);
			menuOption.setIcon(arrowIcon);
			menuOption.setHorizontalTextPosition(2);
			menuOption.setContentAreaFilled(false);
			menuOption.setBorderPainted(false);
			menuOption.setFocusable(false);
			menuOption.setRequestFocusEnabled(false);
			menuOption.setForeground(LoginUIConfig.getInstance()
					.getSysButtonFgColor());

			menuOption.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Dimension size = getMenuOption().getPreferredSize();
					getPopupOption().show(getMenuOption(), 0, size.height);
				}
			});
		}

		return menuOption;
	}

	public JPopupMenu getPopupOption() {
		if (popupOption == null) {
			popupOption = new JPopupMenu();

			popupOption.add(getZipStreamCBMI());
			popupOption.add(getMiEnableWebstart());

			popupOption.add(getNullTransCBMI());
		}

		return popupOption;
	}

	public JCheckBox getCkbZipRemoteStream() {
		if (ckbZipRemoteStream == null) {
			String name = NCLangRes.getInstance().getStrByID("smcomm",
					"UPP1005-000246");

			ckbZipRemoteStream = new JCheckBox(name);
			ckbZipRemoteStream.setOpaque(false);
			ckbZipRemoteStream.setFocusPainted(false);
			ckbZipRemoteStream.setBounds(0, 0, 115, 20);
			ckbZipRemoteStream.setFont(new Font(Style.getFontname(), 0, 12));

			boolean isSel = isNetStreamZip();
			ckbZipRemoteStream.setSelected(isSel);
			setNetStreamZipProp(isSel);
			ckbZipRemoteStream.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean isSel = ckbZipRemoteStream.isSelected();
					LoginUISupport.setNetStreamZipProp(isSel);
					ClientSetup setup = ClientSetupCache.getGlobalClientSetup();
					setup.putBoolean("_isNetStreamZip", isSel);
					ClientSetupCache.storeGlobalClientSetup();
				}
			});
		}
		return ckbZipRemoteStream;
	}

	private JCheckBoxMenuItem getZipStreamCBMI() {
		if (zipStreamCBMI == null) {
			zipStreamCBMI = new JCheckBoxMenuItem(new ZipRemoteStreamAction());
			String name = NCLangRes.getInstance().getStrByID("smcomm",
					"UPP1005-000246");

			zipStreamCBMI.setText(name);
		}
		return zipStreamCBMI;
	}

	private JCheckBoxMenuItem getNullTransCBMI() {
		if (nullTransCBMI == null) {
			MLNullTranslatorAction action = new MLNullTranslatorAction();
			action.putValue("SwingSelectedKey", Boolean.valueOf(false));
			nullTransCBMI = new JCheckBoxMenuItem(action);
			String name = NCLangRes.getInstance().getStrByID("smcomm",
					"UPP1005-000273");

			nullTransCBMI.setText(name);
		}
		return nullTransCBMI;
	}

	public JMenuItem getMiEnableWebstart() {
		if (miEnableWebstart == null) {
			String name = getMiEnableWebstartText();
			miEnableWebstart = new JMenuItem(new WebStartAction());
			miEnableWebstart.setText(name);
			miEnableWebstart.setFocusPainted(false);
		}
		return miEnableWebstart;
	}

	private static void setNetStreamZipProp(boolean isZip) {
		try {
			nc.bs.framework.comn.NetStreamConstants.STREAM_NEED_COMPRESS = isZip;

		} catch (Throwable thr) {

			Logger.error(thr.getMessage(), thr);
		}
	}

	private static boolean isNetStreamZip() {
		ClientSetup setup = ClientSetupCache.getGlobalClientSetup();
		return setup.getBoolean("_isNetStreamZip", true);
	}

	private String getBtnLoginText() {
		return NCLangRes.getInstance().getStrByID("loginui",
				"loginuires-000003");
	}

	private String getBtnLoginToolTipText() {
		return NCLangRes.getInstance().getStrByID("loginui",
				"loginuires-000004");
	}

	private String getMenuOptionText() {
		return NCLangRes.getInstance().getStrByID("sysframev5",
				"UPPsysframev5-000060");
	}

	protected void refreshUIContrl() {
		getLblBusiCenter().setFont(getLblFont());
		getLblUserCode().setFont(getLblFont());
		getLblPWD().setFont(getLblFont());
		getForgetPWD().setFont(getLblFont());
		getCbbBusiCenter().setFont(getControlFont());
		getCbbLanguage().setFont(getControlFont());
		getTfUserCode().setFont(getControlFont());
		getPfUserPWD().setFont(getControlFont());
		getBtnLogin().setFont(getControlFont());

		getLblBusiCenter().setText(getLblBusiCenterText());
		getLblUserCode().setText(getLblUserCodeText());
		getLblPWD().setText(getLblPWDText());
		getForgetPWD().setText(
				NCLangRes.getInstance().getStrByID("loginui",
						"loginuires-000007"));

		getLblCopyright().setText(getCopyrightText());
		getBtnLogin().setText(getBtnLoginText());
		getBtnLogin().setToolTipText(getBtnLoginToolTipText());
		getMenuOption().setText(getMenuOptionText());

		getWebStartPanel().setWebStartPanelHintText(getWebStartPanelHintText());
		getWebStartPanel().setStartWebStartBtnText(getStartWebStartBtnText());

		getMiEnableWebstart().setText(getMiEnableWebstartText());
		getNullTransCBMI().setText(
				NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000273"));

		getZipStreamCBMI().setText(
				NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000246"));

		updateLoginBtnBackground();
	}

	private String getMiEnableWebstartText() {
		return NCLangRes.getInstance().getStrByID("sfbase", "webstart-0003");
	}

	public ResultMessageLabel getLblResultMessage() {
		if (resultLbl == null) {
			resultLbl = new ResultMessageLabel();
			resultLbl.setVisible(false);
		}
		return resultLbl;
	}

	private Font getLblFont() {
		return new Font(Style.getFontname(), 0, 12);
	}

	private Font getControlFont() {
		return new Font(Style.getFontname(), 0, 12);
	}

	protected LoginRequest getLoginRequest() {
		LoginRequest request = new LoginRequest();
		BusiCenterVO bcVO = (BusiCenterVO) getCbbBusiCenter().getSelectedItem();
		if (bcVO != null) {
			request.setBusiCenterCode(bcVO.getCode());
		}
		String userCode = getTfUserCode().getText().trim();
		request.setUserCode(userCode);
		request.setUserPWD(new String(getPfUserPWD().getPassword()).trim());
		request.setLangCode(getCurrLangCode());

		request.setDeviceType(CheckDevice.check());
		request.setLoginType(ClientToolKit.isPortalClient());
		String profileVersion = FunctionPermProfileManager.getInstance()
				.getProfileVersion(userCode);

		String profileRuleVersion = FunctionPermProfileManager.getInstance()
				.getProfileRuleVersion(userCode);

		request.putAttachProp("FUNCTIONPERMPROFILEVERSION", profileVersion);

		request.putAttachProp("FUNCTIONPERMRULEVERSIONKEY", profileRuleVersion);

		request.putAttachProp("NeedQuerySimplePerm", Boolean.valueOf(true));

		ClientSetup setup = ClientSetupCache.getGlobalClientSetup();
		String groupCode = (String) setup.get("loginGroup" + bcVO.getCode()
				+ userCode);

		request.putAttachProp(GroupVO.class.getName() + ".code", groupCode);
		return request;
	}

	protected LoginAssistant createLoginAssistant(LoginRequest request) {
		return new LoginAssistant(request);
	}

	public static String getCurrLangCode() {
		if (NCLangRes.getInstance().getCurrLanguage() != null) {
			return NCLangRes.getInstance().getCurrLanguage().getCode();
		}
		return AbstractNCLangRes.getDefaultLanguage().getCode();
	}

	protected void loginStart() {
		getLblResultMessage().setText("");
		getLblResultMessage().setVisible(false);
		getLblLoginFlash().setVisible(true);
		getBtnLogin().setCursor(new Cursor(3));
		getBtnLogin().setEnabled(false);
		getBtnOption().setEnabled(false);
		getCkbZipRemoteStream().setEnabled(false);
		getMiEnableWebstart().setEnabled(false);

		if ((getUIBGPainter() instanceof DefaultLoginUIBackGroundPainter)) {
			((DefaultLoginUIBackGroundPainter) getUIBGPainter())
					.setLoginBtnLightIcon(LoginUIConfig.getInstance()
							.getLoginBtnLightIcon());
		}

		getUIBGPainter().setIsLoginning(true);
		loginUIContainer.repaint();
	}

	private void loginEndInDT(final int result, final String resultMsg) {
		Runnable run = new Runnable() {
			public void run() {
				loginEnd(result, resultMsg);
			}
		};
		ClientToolKit.invokeInDispatchThread(run);
	}

	protected void loginEnd(int loginResult, String resultMsg) {
		getBtnLogin().setCursor(Cursor.getDefaultCursor());
		getBtnLogin().setEnabled(true);
		getBtnOption().setEnabled(true);
		getCkbZipRemoteStream().setEnabled(true);
		getMiEnableWebstart().setEnabled(true);
		getLblLoginFlash().setVisible(false);
		if (((getUIBGPainter() instanceof DefaultLoginUIBackGroundPainter))
				&& (getPfUserPWD().getPassword().length == 0)) {
			((DefaultLoginUIBackGroundPainter) getUIBGPainter())
					.setLoginBtnLightIcon(null);
		}

		if (resultMsg != null) {
			getLblResultMessage().setText(resultMsg);
			getLblResultMessage().setVisible(true);
		}
		getUIBGPainter().setIsLoginning(false);
		loginUIContainer.repaint();
	}

	private void storeLoginRequest(LoginRequest request) {
		ClientSetup setup = ClientSetupCache.getGlobalClientSetup();
		setup.put("userCode", request.getUserCode());
		setup.put("bcCode", request.getBusiCenterCode());
		setup.put("langCode", request.getLangCode());
		ClientSetupCache.storeGlobalClientSetup();
	}

	protected void initFromCookie() {
		ClientSetup setup = ClientSetupCache.getGlobalClientSetup();
		String bcCode = (String) setup.get("bcCode");
		String userCode = (String) setup.get("userCode");
		String langCode = (String) setup.get("langCode");
		if ((langCode == null) || (langCode.trim().length() == 0)) {
			langCode = getLangCodeFromOsMap();
		}
		if (userCode != null) {
			getTfUserCode().setText(userCode);
		}
		if (bcCode != null) {
			ComboBoxModel model = getCbbBusiCenter().getModel();
			int size = model.getSize();
			for (int i = 0; i < size; i++) {
				BusiCenterVO vo = (BusiCenterVO) model.getElementAt(i);
				if (bcCode.equals(vo.getCode())) {
					getCbbBusiCenter().setSelectedIndex(i);
					break;
				}
			}
		}
		if (langCode != null) {
			ComboBoxModel model = getCbbLanguage().getModel();
			int size = model.getSize();
			for (int i = 0; i < size; i++) {
				Language vo = (Language) model.getElementAt(i);
				if (langCode.equals(vo.getCode())) {
					getCbbLanguage().setSelectedIndex(i);
					break;
				}
			}
		}
	}

	private synchronized static Properties getOSLangMap() {
		if (oslangMap == null) {
			oslangMap = new Properties();
			InputStream in = null;
			try {
				in = LoginUISupport.class.getClassLoader().getResourceAsStream(
						"oslangmap.properties");
				oslangMap.load(in);
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return oslangMap;
	}

	private static String getLangCodeFromOsMap() {
		Properties prop = getOSLangMap();
		String language = System.getProperty("user.language");
		String country = System.getProperty("user.country");
		String langCode = prop.getProperty(language + "_" + country);
		if (langCode == null) {
			langCode = prop.getProperty(language);
		}
		return langCode;
	}

	protected static boolean needLayout(Container parent, Component comp) {
		return (comp.isVisible()) && (parent.isAncestorOf(comp));
	}

	protected Rectangle calculateLoginCenterSize(Container parent, int hgap,
			int vgap) {
		Dimension size = computerLoginCenterSize(parent, hgap, vgap);
		Dimension parentSize = parent.getSize();
		int x = (parentSize.width - size.width) / 2;
		ImageIcon icon = LoginUIConfig.getInstance().getCenterIcon();
		// int y = 225;
		int y = (parentSize.height - icon.getIconHeight()) / 3 + 129;
		Rectangle rect = new Rectangle(x, y, size.width, size.height);
		return rect;
	}

	protected Dimension computerLoginCenterSize(Container parent, int hgap,
			int vgap) {
		Component[] lblComps = { getLblBusiCenter(), getLblUserCode(),
				getLblPWD() };
		Component[] ctrlComps = { getCbbBusiCenter(), getTfUserCode(),
				getPfUserPWD() };
		int lblsHeight = 0;
		int lblsMaxWidth = 0;
		for (int i = 0; i < lblComps.length; i++) {
			Component lblComp = lblComps[i];
			if (needLayout(parent, lblComp)) {
				Dimension lblDim = lblComp.getPreferredSize();
				lblsHeight += lblDim.height + vgap;
				if (lblsMaxWidth < lblDim.width) {
					lblsMaxWidth = lblDim.width;
				}
			}
		}
		if (lblsHeight > 0) {
			lblsHeight -= vgap;
		}
		int ctrlsHeight = 0;
		int ctrlsMaxWidth = 0;
		for (int i = 0; i < ctrlComps.length; i++) {
			Component ctrlComp = ctrlComps[i];
			if (needLayout(parent, ctrlComp)) {
				Dimension ctrlDim = ctrlComp.getPreferredSize();
				ctrlsHeight += ctrlDim.height + vgap;
				if (ctrlsMaxWidth < ctrlDim.width) {
					ctrlsMaxWidth = ctrlDim.width;
				}
			}
		}
		if (ctrlsHeight > 0) {
			ctrlsHeight -= vgap;
		}
		int w = lblsMaxWidth + hgap + ctrlsMaxWidth;
		int h = lblsHeight > ctrlsHeight ? lblsHeight : ctrlsHeight;
		return new Dimension(w, h);
	}

	public static Shape getRoundShape(int x, int y, int width, int height,
			Insets insets) {
		GeneralPath path = new GeneralPath();
		path.moveTo(x + insets.left, y);
		path.lineTo(x + width - insets.right - 1, y);
		path.curveTo(x + width - insets.right / 2, y, x + width - 1, y
				+ insets.top / 2, x + width - 1, y + insets.top);
		path.lineTo(x + width - 1, y + height - insets.bottom - 1);
		path.curveTo(x + width - 1, y + height - insets.bottom / 2 - 1, x
				+ width - insets.right / 2 - 1, y + height - 1, x + width
				- insets.right - 1, y + height - 1);
		path.lineTo(x + insets.left, y + height - 1);
		path.curveTo(x + insets.left / 2, y + height - 1, x, y + height
				- insets.bottom / 2, x, y + height - insets.bottom - 1);
		path.lineTo(x, y + insets.top);
		path.curveTo(x, y + insets.top / 2, x + insets.left / 2, y, x
				+ insets.left, y);
		return path;
	}

	public static void paintCtrlBorder(Graphics2D g, JComponent c,
			Color fillColor, Color borderColor, Shape shape, int w) {
		Object oldValue = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		Paint oldPaint = g.getPaint();
		Stroke oldStroke = g.getStroke();
		BasicStroke bs = new BasicStroke(w);
		g.setStroke(bs);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(fillColor);
		g.fill(shape);
		g.setColor(borderColor);
		g.draw(shape);
		g.setPaint(oldPaint);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldValue);
		g.setStroke(oldStroke);
	}

	private SecurityLogInfo writeSecurityLog(boolean isSuccess) {
		SecurityLogInfo logInfo = new SecurityLogInfo();
		List<SecurityLogVarItem> logItem = new ArrayList();
		SecurityLogVarItem item = new SecurityLogVarItem();
		logInfo.setAppTag("UAP");
		logInfo.setLogType("login");
		if (isSuccess) {
			logInfo.setLogLevel(60);
			logInfo.setLogAbs("Login successful");
			logInfo.setOpResult("success");
			item.setAttributeName("Token");
			item.setAttributeVar(NetStreamContext.getToken() == null ? ""
					: KeyUtil.encodeToken(NetStreamContext.getToken()));

			item.setSecret(true);
			logItem.add(item);
			logInfo.setOpDescript(logItem);
		} else {
			logInfo.setLogLevel(40);
			logInfo.setLogAbs("Login failed");
			logInfo.setOpResult("failure");
		}
		return logInfo;
	}

	private SecurityLogManageFacade getService() {
		return (SecurityLogManageFacade) NCLocator.getInstance().lookup(
				SecurityLogManageFacade.class);
	}

	public String loginActionForWebindex(String args) {
		return actionPerformed(args);
	}

	private String actionPerformed(String args) {
		String returnStr = "";
		try {
			LoginRequest request = testGetLoginRequest(args);

			setLoginContextVariables(args);

			LoginAssistant loginAssi = createLoginAssistant(request);
			LoginResponse response = loginAssi.login();
			int result = response.getLoginResult();

			if (result == 0) {
				loginAssi.showWorkbench(response);
				loginEndInDT(result, null);
				storeLoginRequest(request);
			} else {
				String msg = loginAssi.getResultMessage(result);
				loginEndInDT(result, "" + msg);
				NetStreamContext.resetAll();
				returnStr = msg;
			}
		} catch (LoginVerifyException ex) {
			NetStreamContext.resetAll();
			String errMsg = ex.getMessage();
			if ((errMsg == null) || (errMsg.trim().length() == 0)) {
				errMsg = ex.getClass().getName() + ":" + errMsg;
			}
			Logger.error(ex.getMessage(), ex);
			loginEndInDT(ex.getLoginResult(), errMsg);
			returnStr = errMsg;
		} catch (Exception ex) {
			NetStreamContext.resetAll();
			String errMsg = ex.getMessage();
			if ((errMsg == null) || (errMsg.trim().length() == 0)) {
				errMsg = ex.getClass().getName() + ":" + errMsg;
			}
			Logger.error(ex.getMessage(), ex);
			loginEndInDT(-1, errMsg);
			returnStr = errMsg;
		}
		return returnStr + " ";
	}

	private void setLoginContextVariables(String arg) {
		String[] args = arg.split(",");
		InvocationInfoProxy.getInstance().reset();
		UserExit.getInstance().setUserCode(args[2].trim());
		UserExit.getInstance().setUserDataSource(args[4]);
		UserExit.getInstance().setBizCenterCode(args[1]);
	}

	private LoginRequest testGetLoginRequest(String arg) {
		String[] args = arg.split(",");
		LoginRequest request = new LoginRequest();
		BusiCenterVO bcVO = new BusiCenterVO();
		bcVO.setCode(args[1]);
		bcVO.setDataSourceName(args[4]);
		bcVO.setEffectDate(args[5]);
		bcVO.setExpireDate(args[6]);
		bcVO.setLocked((args[7] != null) && (!args[7].equals("")));

		bcVO.setName(args[8]);
		if (bcVO != null) {
			request.setBusiCenterCode(bcVO.getCode());
		}
		String userCode = args[2].trim();
		request.setUserCode(userCode);
		try {
			RSAUtil rsaUtil = new RSAUtil();
			request.setUserPWD(rsaUtil.decryptString(args[3].trim()));
		} catch (Exception e) {
			request.setUserPWD("");
		}

		request.setLangCode(args[0]);

		setLangCode(args[0].trim());
		String profileVersion = FunctionPermProfileManager.getInstance()
				.getProfileVersion(userCode);

		request.putAttachProp("FUNCTIONPERMPROFILEVERSION", profileVersion);

		ClientSetup setup = ClientSetupCache.getGlobalClientSetup();
		String groupCode = (String) setup.get("loginGroup" + bcVO.getCode()
				+ userCode);

		request.putAttachProp(GroupVO.class.getName() + ".code", groupCode);
		return request;
	}

	public void forgetPwd(String para) {
		String[] paras = para.split(",");

		setLangCode(paras[1].trim());
		InvocationInfoProxy.getInstance().setUserDataSource(paras[0].trim());
		PassWordWizardDialog wd = new PassWordWizardDialog(loginUIContainer,
				getSteps(paras[0].trim()), null);

		Dimension screan = Toolkit.getDefaultToolkit().getScreenSize();
		wd.setSize(647, 390);
		Dimension dlgsize = wd.getSize();
		wd.setLocation((screan.width - dlgsize.width) / 2,
				(screan.height - dlgsize.height) / 2 + 40);

		wd.showModal();
	}

	public String[] setLangCode(String code) {
		Language currLang = (Language) currLangMap.get(code);
		if (currLang == null) {
			currLang = NCLangRes.getInstance().getLanguage(code);
			currLangMap.put(code, NCLangRes.getInstance().getLanguage(code));
		}
		NCLangRes.getInstance().setCurrLanguage(currLang);
		UserExit.getInstance().setLangCode(code);
		new Thread() {
			public void run() {
				NCLangRes.getInstance().getStrByID("MENUCODE", "10");
				NCLangRes.getInstance().getStrByID("FUNCODE", "10");

			}

		}.start();
		return getLoginjspLang();
	}

	private String[] getLoginjspLang() {
		String[] disText = new String[18];
		String webstart = NCLangRes.getInstance().getStrByID("sfbase",
				"webstart-0001");

		String forgetPwd = NCLangRes.getInstance().getStrByID("loginui",
				"loginuires-000007");

		String disid = NCLangRes.getInstance().getStrByID("smcomm",
				"UPP1005-000273");

		String ckbZipStream = NCLangRes.getInstance().getStrByID("smcomm",
				"UPP1005-000246");

		disText[0] = NCLangRes.getInstance().getStrByID("loginui",
				"loginuires-000000");

		disText[1] = NCLangRes.getInstance().getStrByID("loginui",
				"loginuires-000001");

		disText[2] = NCLangRes.getInstance().getStrByID("loginui",
				"loginuires-000002");

		disText[3] = webstart.replaceAll("，", ",").replaceAll("。", ".");
		disText[4] = NCLangRes.getInstance().getStrByID("sfbase",
				"webstart-0002");

		disText[5] = NCLangRes.getInstance().getStrByID("loginui",
				"loginuires-000005");

		disText[6] = forgetPwd.replaceAll("？", "?");
		disText[7] = (ckbZipStream + " ");
		disText[8] = (disid + " ");
		disText[9] = NCLangRes.getInstance().getStrByID("sfbase",
				"webstart-0003");

		disText[10] = NCLangRes.getInstance().getStrByID("sysframev5",
				"UPPsysframev5-000060");

		disText[11] = NCLangRes.getInstance().getStrByID("loginui",
				"loginuiresExt-000001");
		disText[12] = NCLangRes.getInstance().getStrByID("loginui",
				"loginuiresExt-000002");
		disText[13] = NCLangRes.getInstance().getStrByID("loginui",
				"loginuiresExt-000003");
		disText[14] = NCLangRes.getInstance().getStrByID("loginui",
				"loginuiresExt-000004");
		disText[15] = NCLangRes.getInstance().getStrByID("loginui",
				"loginuiresExt-000005");
		disText[16] = NCLangRes.getInstance().getStrByID("loginui",
				"loginuiresExt-000006");
		disText[17] = NCLangRes.getInstance().getStrByID("loginui",
				"loginuiresExt-000007");
		return disText;
	}

	public LoginUISupport() {
	}
}
