package nc.ui.hi.register.action;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;

import javax.swing.JComponent;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIManager;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UITextArea;
import nc.ui.pub.style.Style;
import nc.uitheme.ui.ThemeResourceCenter;
import nc.vo.jcom.lang.StringUtil;

public class SyncWorkDialog extends UIDialog {

	private static final long serialVersionUID = 2840016606194311849L;

	protected JComponent centerPanel;

	private UICheckBox checkBox;

	public final static int OK_OPTION = 0;

	public final static int YES_NO_OPTION = 2;

	public final static int SIZE_WIDTH_LITTLE = 320;

	public final static int SIZE_HEIGHT_LITTLE = 160;

	public static int size_height = SIZE_HEIGHT_LITTLE;

	public static int size_width = SIZE_WIDTH_LITTLE;

	private javax.swing.JPanel ivjUIDialogContentPane = null;

	private nc.ui.pub.beans.UIButton ivjUIButton1 = null;

	private nc.ui.pub.beans.UIButton ivjUIButton2 = null;

	private int m_nType = OK_OPTION;

	private nc.ui.pub.beans.UIScrollPane ivjUIScrollPane_Message = null;

	private UIPanel adjustpanel = null;

	private nc.ui.pub.beans.UIPanel ivjUIPanel_Icon = null;

	private nc.ui.pub.beans.UIPanel ivjUIPanel_Buttons = null;

	private nc.ui.pub.beans.UIComboBox ivjUIComboBox_Input = null;

	private nc.ui.pub.beans.UIPanel ivjUIPanel_Message = null;

	IvjEventHandler ivjEventHandler = new IvjEventHandler();

	private nc.ui.pub.beans.UITextArea ivjUITextMessage = null;

	private nc.ui.pub.beans.UILabel ivjUILabelIcon = null;

	public final static int BOUND_WIDTH_LITTLE = 236;

	public final static int BOUND_WIDTH_MIDDLE = 286;

	public final static int BOUND_HEIGHT_LITTLE = 100;

	public final static int BOUND_HEIGHT_MIDDLE = 170;

	public final static int SIZE_WIDTH_MIDDLE = 400; // 450

	public final static int SIZE_HEIGHT_MIDDLE = 250; // 280

	public final static int SIZE_CHOOSER_LENGTH = 1500; // 使用哪种窗口大小的长度界限,FontMetrics.stringWidth()

	public static int bound_x = 11;

	public static int bound_y = 6;

	public static int bound_width = BOUND_WIDTH_LITTLE;

	public static int bound_height = BOUND_HEIGHT_LITTLE;

	private int focusbutton = -1; // 焦点按钮序号

	public final static int WARNING_ICON = 0;

	public final static int ERROR_ICON = 1;

	public final static int HINT_ICON = 2;

	public final static int QUESTION_ICON = 3;

	public static FontMetrics fm = null;

	private String sShortKey;

	private boolean keyListenerDisabled = false;

	private static Color dlgContentBGColor = ThemeResourceCenter.getInstance().getColor(
			"themeres/dialog/dialogResConf", "dialogContentPane.backgroundColor");

	class IvjEventHandler implements java.awt.event.ActionListener {

		public void actionPerformed(java.awt.event.ActionEvent e) {

			if (e.getSource() == SyncWorkDialog.this.getUIButton1())
				connEtoC1(e);
			if (e.getSource() == SyncWorkDialog.this.getUIButton2())
				connEtoC2(e);
		};
	};

	/* 警告:此方法将重新生成. */
	private void connEtoC1(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.btn1_ActionPerformed();
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	/**
	 * 每当部件抛出异常时被调用
	 * 
	 * @param exception
	 *            java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {
		/* 除去下列各行的注释,以将未捕捉到的异常打印至 stdout. */

	}

	/* 警告:此方法将重新生成. */
	private void connEtoC2(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.btn2_ActionPerformed();
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}

	private void btn1_ActionPerformed() {

		switch (m_nType) {

		case YES_NO_OPTION:
			setResult(ID_YES);
			break;
		case OK_OPTION:
		default:
			setResult(ID_OK);
		}
		close();
		return;
	}

	private UIComboBox getUIComboBox_Input() {

		if (ivjUIComboBox_Input == null) {
			try {
				ivjUIComboBox_Input = new nc.ui.pub.beans.UIComboBox();
				ivjUIComboBox_Input.setName("UIComboBox_Input");
				ivjUIComboBox_Input.setPreferredSize(new java.awt.Dimension(200, 22));
				ivjUIComboBox_Input.setBounds(29, 172, 260, 22);
				ivjUIComboBox_Input.setVisible(false);
				// user code begin {1}
				ivjUIComboBox_Input.setMaximumRowCount(2);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjUIComboBox_Input;
	}

	private void btn2_ActionPerformed() {
		setResult(ID_NO);
		close();
		return;
	}

	public SyncWorkDialog(Container parent) {
		super(parent);
		initialize();
	}

	private void initialize() {
		try {
			setName("MessageDialog");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(size_width, size_height);
			setModal(true);
			setResizable(false);
			getContentPane().removeAll();
			setContentPane(getUIDialogContentPane());
			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
	}

	private void initConnections() throws java.lang.Exception {
		getUIButton1().addActionListener(ivjEventHandler);
		getUIButton2().addActionListener(ivjEventHandler);
	}

	private UIButton getUIButton1() {
		if (ivjUIButton1 == null) {
			ivjUIButton1 = new nc.ui.pub.beans.UIButton();
			ivjUIButton1.setName("UIButton1");
			ivjUIButton1.setIButtonType(0/** Java默认(自定义) */
			);
			ivjUIButton1.setPreferredSize(new java.awt.Dimension(72, 22));
			ivjUIButton1.setText("UIButton");
			ivjUIButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
		}
		return ivjUIButton1;
	}

	/* 警告:此方法将重新生成. */
	private UIButton getUIButton2() {
		if (ivjUIButton2 == null) {
			ivjUIButton2 = new nc.ui.pub.beans.UIButton();
			ivjUIButton2.setName("UIButton2");
			ivjUIButton2.setIButtonType(0/** Java默认(自定义) */
			);
			ivjUIButton2.setPreferredSize(new java.awt.Dimension(72, 22));
			ivjUIButton2.setText("UIButton");
			ivjUIButton2.setMargin(new java.awt.Insets(2, 2, 2, 2));
		}
		return ivjUIButton2;
	}

	private javax.swing.JPanel getUIDialogContentPane() {
		if (ivjUIDialogContentPane == null) {
			try {
				ivjUIDialogContentPane = new javax.swing.JPanel();
				ivjUIDialogContentPane.setName("UIDialogContentPane");
				ivjUIDialogContentPane.setOpaque(true);
				ivjUIDialogContentPane.setBackground(UIManager.getColor("MessageDialog.bgcolor"));
				ivjUIDialogContentPane.setLayout(new CardLayout(2, 1));
				ivjUIDialogContentPane.setLayout(new java.awt.BorderLayout());
				UIPanel pnl = new UIPanel();
				pnl.setMinimumSize(new Dimension(50, 20));
				pnl.setOpaque(false);
				ivjUIDialogContentPane.add(pnl, "North");

				getUIDialogContentPane().add(getUIPanel_Icon(), "West");

				getUIDialogContentPane().add(getUIPanel_Buttons(), "South");

				// zsb+:设置信息区的间距
				javax.swing.JPanel panCenter = new javax.swing.JPanel();
				panCenter.setOpaque(false);
				panCenter.setLayout(new java.awt.GridBagLayout());
				java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
				gbc.fill = GridBagConstraints.BOTH;
				gbc.insets = new java.awt.Insets(0, 5, 8, 8);
				gbc.weightx = 1;
				gbc.weighty = 1;
				gbc.gridy = 0;
				panCenter.add(getUIPanel_Message(), gbc);
				getUIDialogContentPane().add(panCenter, "Center");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIDialogContentPane;
	}

	private UIPanel getUIPanel_Message() {
		if (ivjUIPanel_Message == null) {
			try {
				ivjUIPanel_Message = new UIPanel();
				ivjUIPanel_Message.setName("UIPanel_Message");
				ivjUIPanel_Message.setPreferredSize(new java.awt.Dimension(bound_width + bound_x + 5, bound_height
						+ bound_y + 5));
				ivjUIPanel_Message.setLayout(new java.awt.BorderLayout());
				ivjUIPanel_Message.setOpaque(false);
				ivjUIPanel_Message.add(getAdjustPanel(), "North");
				ivjUIPanel_Message.add(getUIScrollPane_Message(), "Center");
				ivjUIPanel_Message.add(getCheckBox(), BorderLayout.SOUTH);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIPanel_Message;
	}

	private UIPanel getAdjustPanel() {
		if (adjustpanel == null) {
			adjustpanel = new UIPanel();
			adjustpanel.setOpaque(false);
			adjustpanel.setPreferredSize(new Dimension(200, 5));
		}
		return adjustpanel;
	}

	private UIScrollPane getUIScrollPane_Message() {
		if (ivjUIScrollPane_Message == null) {
			try {
				ivjUIScrollPane_Message = new nc.ui.pub.beans.UIScrollPane();
				// zsb+:去掉border
				ivjUIScrollPane_Message.setBorder(null);
				ivjUIScrollPane_Message.setOpaque(false);
				ivjUIScrollPane_Message.setName("UIScrollPane_Message");
				ivjUIScrollPane_Message.setPreferredSize(new java.awt.Dimension(bound_width, bound_height));
				ivjUIScrollPane_Message
						.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				ivjUIScrollPane_Message
						.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				ivjUIScrollPane_Message.setBounds(bound_x, bound_y, bound_width, bound_height);
				getUIScrollPane_Message().setViewportView(getUITextMessage());
				getUIScrollPane_Message().setBorder(null);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIScrollPane_Message;
	}

	private UITextArea getUITextMessage() {
		if (ivjUITextMessage == null) {
			try {
				ivjUITextMessage = new nc.ui.pub.beans.UITextArea();
				ivjUITextMessage.setName("UITextMessage");
				ivjUITextMessage.setLineWrap(true);
				ivjUITextMessage.setTabSize(4);
				ivjUITextMessage.setWrapStyleWord(true);
				ivjUITextMessage.setText("");
				ivjUITextMessage.setColumns(36);
				ivjUITextMessage.setRows(0);
				ivjUITextMessage.setMargin(new java.awt.Insets(2, 4, 2, 4));
				ivjUITextMessage.setBounds(0, 0, bound_width - 20, bound_height - 20);
				ivjUITextMessage.setEditable(false);
				ivjUITextMessage.setBackground(dlgContentBGColor);
				ivjUITextMessage.setBorder(null);
				ivjUITextMessage.setForeground(Color.black);
				ivjUITextMessage.setOpaque(true);
				ivjUITextMessage.updateUI();

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUITextMessage;
	}

	@SuppressWarnings("serial")
	private UIPanel getUIPanel_Buttons() {
		if (ivjUIPanel_Buttons == null) {
			try {
				// zsb update:
				ivjUIPanel_Buttons = new nc.ui.pub.beans.UIPanel() {
					public void paint(java.awt.Graphics g) {
						super.paint(g);
						// 最上面画一个黑线
						g.setColor(UIManager.getColor("MessageDialog.linecolor"));
						g.drawLine(5, 0, getWidth() - 5, 0);
						// 再在它下面画一个白线
						g.setColor(java.awt.Color.white);
						g.drawLine(5, 1, getWidth() - 5, 1);
					}
				};
				ivjUIPanel_Buttons.setName("UIPanel_Buttons");
				ivjUIPanel_Buttons.setPreferredSize(new java.awt.Dimension(0, 40));
				ivjUIPanel_Buttons.setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 8));
				ivjUIPanel_Buttons.setOpaque(false);
				getUIPanel_Buttons().add(getUIButton1(), getUIButton1().getName());
				getUIPanel_Buttons().add(getUIButton2(), getUIButton2().getName());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIPanel_Buttons;
	}

	private UIPanel getUIPanel_Icon() {
		if (ivjUIPanel_Icon == null) {
			try {
				ivjUIPanel_Icon = new nc.ui.pub.beans.UIPanel();
				ivjUIPanel_Icon.setName("UIPanel_Icon");
				ivjUIPanel_Icon.setPreferredSize(new java.awt.Dimension(60, 0));
				ivjUIPanel_Icon.setOpaque(false);
				ivjUIPanel_Icon.setLayout(null);
				getUIPanel_Icon().add(getUILabelIcon());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIPanel_Icon;
	}

	private UILabel getUILabelIcon() {
		if (ivjUILabelIcon == null) {
			try {
				ivjUILabelIcon = new nc.ui.pub.beans.UILabel();
				ivjUILabelIcon.setName("UILabelIcon");
				ivjUILabelIcon.setText("");
				ivjUILabelIcon.setBounds(20, 5, 32, 32);
				ivjUILabelIcon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
				ivjUILabelIcon.setILabelType(0/** Java默认(自定义) */
				);
				ivjUILabelIcon.setIcon(null);
				ivjUILabelIcon.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
				ivjUILabelIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabelIcon;
	}

	public int showYesNoDlg(Container parent, String title, String question) {
		if (title == null) {
			title = nc.ui.ml.NCLangRes.getInstance().getStrByID("_Beans", "UPP_Beans-000093")/*
																							 * @
																							 * res
																							 * "询问"
																							 */;
		}
		return showMessageBox(parent, MessageDialog.YES_NO_OPTION, MessageDialog.QUESTION_ICON, title, question, ID_YES);
	}

	private void setDefaultButton(int button) {
		UIButton btn = getButtonById(button);
		if (btn != null)
			btn.getRootPane().setDefaultButton(btn);
	}

	public void setFocusButton(int nbutton) {
		focusbutton = nbutton;

	}

	public int getFocusButton() {
		return focusbutton;
	}

	private UIButton getButtonById(int curbtn) {
		UIButton button = null;
		switch (getFocusButton()) {
		case ID_NO:
			button = getUIButton2();
			break;
		case ID_OK:
		case ID_YES:
		default:
			button = getUIButton1();
		}
		return button;
	}

	public static SyncWorkDialog getSingleton(Container parent) {
		return new SyncWorkDialog(parent);
	}

	private int showMessageBox(Container parent, int type, int icon, String title, String message, int button) {
		String messageDisplay = StringUtil.recoverWrapLineChar(message);
		if (messageDisplay == null)
			messageDisplay = "";

		this.setFocusButton(button);
		this.setDefaultButton(button);

		if (fm == null)
			fm = this.getFontMetrics(new Font("Dialog", Font.PLAIN, 20));

		if (message != null && fm.stringWidth(message) >= SIZE_CHOOSER_LENGTH + 100) {
			size_width = SIZE_WIDTH_MIDDLE;
			size_height = SIZE_HEIGHT_MIDDLE;
			bound_width = BOUND_WIDTH_MIDDLE;
			bound_height = BOUND_HEIGHT_MIDDLE;
		} else {
			size_width = SIZE_WIDTH_LITTLE;
			size_height = SIZE_HEIGHT_LITTLE;
			bound_width = BOUND_WIDTH_LITTLE;
			bound_height = BOUND_HEIGHT_LITTLE;
		}
		this.setSize(size_width, size_height);
		this.getUIScrollPane_Message().setBounds(bound_x, bound_y, bound_width, bound_height);

		this.getUIComboBox_Input().setVisible(false);

		this.getUIScrollPane_Message().setBounds(bound_x, bound_y, bound_width, bound_height);
		this.getUIScrollPane_Message().setPreferredSize(new java.awt.Dimension(bound_width, bound_height));

		if (!this.keyListenerDisabled) {
			this.addKeyListener(this);
			this.keyListenerDisabled = true;
		}

		// 设置按钮
		this.m_nType = type;
		switch (type) {

		case YES_NO_OPTION:
			this.getUIButton1().setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("_Beans", "UPP_Beans-000051")/*
																												 * @
																												 * res
																												 * "是"
																												 */
					+ "(Y)");
			this.getUIButton1().setVisible(true);
			this.getUIButton2().setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("_Beans", "UPP_Beans-000050")/*
																												 * @
																												 * res
																												 * "否"
																												 */
					+ "(N)");
			this.getUIButton2().setVisible(true);
			this.setShortKey("YN");
			break;

		case OK_OPTION:
		default:
			this.getUIButton1().setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("_Beans", "UPP_Beans-000021")/*
																												 * @
																												 * res
																												 * "确定"
																												 */
					+ "(Y)");
			this.getUIButton1().setVisible(true);
			this.getUIButton2().setVisible(false);
			this.setShortKey("Y");
		}
		// 显示图标
		switch (icon) {
		case WARNING_ICON:
			this.getUILabelIcon().setIcon(Style.getImage("消息.警告"));/*
																	 * -=notranslate
																	 * =-
																	 */

			break;
		case QUESTION_ICON:
			this.getUILabelIcon().setIcon(Style.getImage("消息.问题"));/*
																	 * -=notranslate
																	 * =-
																	 */

			break;
		case ERROR_ICON:
			this.getUILabelIcon().setIcon(Style.getImage("消息.错误"));/*
																	 * -=notranslate
																	 * =-
																	 */

			break;
		case HINT_ICON:
		default:
			this.getUILabelIcon().setIcon(Style.getImage("消息.提示"));/*
																	 * -=notranslate
																	 * =-
																	 */

		}

		// 显示消息
		// 计算TextArea高度
		// 许文耀修改开始
		/*
		 * int len = messageDisplay.getBytes().length; if (len > 180) { int rows
		 * = (len - 1) / 36 + 1; int height = 16; int first = 10;
		 * dlg.getUITextMessage().setBounds(0, 0, 200, rows * height + first);
		 * dlg.getUITextMessage().setPreferredSize(new Dimension(200, rows *
		 * height + first)); }
		 */
		java.util.StringTokenizer token = new java.util.StringTokenizer(messageDisplay, "\n");
		int iRows = 0;
		while (token.hasMoreElements()) {
			int iLen = token.nextElement().toString().getBytes().length;
			iRows += (iLen - 1) / 36 + 1;
		}

		if (iRows >= 5) {
			int height = 18;// 17
			int first = 20;
			this.getUITextMessage().setBounds(0, 0, 200, iRows * height + first);
		}

		// add by hxr
		if (messageDisplay.length() >= this.getUITextMessage().getMaxLength())
			messageDisplay = messageDisplay.substring(0, this.getUITextMessage().getMaxLength() - 5) + "...";

		this.getUITextMessage().setText(messageDisplay);
		// 设置标题
		this.setTitle(title);

		int returnKeyCode = this.showModal();
		// 恢复当前节点号
		this.destroy();
		return returnKeyCode;
	}

	public JComponent createCenterPanel() {
		if (centerPanel != null) {
			return centerPanel;
		}
		centerPanel = new UIPanel();
		centerPanel.setMinimumSize(new Dimension(80, 20));
		centerPanel.setOpaque(false);
		centerPanel.add(getCheckBox());
		return centerPanel;
	}

	public UICheckBox getCheckBox() {
		if (checkBox == null) {
			checkBox = new UICheckBox();
			checkBox.setName("extendAgreement");
			checkBox.setBorder(null);
			checkBox.setText(ResHelper.getString("6007entry", "26007entry-000005")/* "同步履历" */);
			checkBox.setPreferredSize(new Dimension(120, 75));
			checkBox.setBackground(dlgContentBGColor);
			// ssx added on 2020-08-15
			// 同步履历默认为否
			checkBox.setSelected(false);
			checkBox.setVisible(false);
			// end
		}
		return checkBox;
	}

	protected void setShortKey(String sKey) {
		sShortKey = sKey;
	}

	public String getShortKey() {
		return sShortKey;
	}

}
