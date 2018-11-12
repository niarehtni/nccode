package nc.ui.wa.payslip.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.SalaryreportUtil;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIList;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UITextArea;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.wa.pub.WaShowWarningRefPane;
import nc.ui.wa.ref.WaClassRefModel;
import nc.ui.wa.ref.WaPeriodRefTreeModel;
import nc.vo.ml.Language;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.wa.payslip.PayslipVO;
import nc.vo.wa.payslip.SendTypeEnum;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.pub.WaLoginContext;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 薪资条基本信息设置界面
 */
public class PayslipBaseSetPanel extends UIPanel implements
		ValueChangedListener {

	/** 薪资方案参照 */
	private UIRefPane classRefPanel = null;

	/** 薪资期间参照 */
	private UIRefPane qijianRefPanel = null;

	// 发送方式控件
	private UIComboBox sendTypeCombox;
	// 主题文本控件
	private UITextArea titleTextArea = null;
	// 表尾文本控件
	private UITextArea tailTextArea = null;

	// 主要内容面板
	private UIPanel contentPane = null;

	// 系统变量控件
	private UIList sysVarList = null;

	// 系统变量选择监听器
	private MouseSelectListener selectListener = new MouseSelectListener();

	// 文本控件获取焦点事件
	private TextAreaFocusListener focusListener = new TextAreaFocusListener();

	private String focusFlag = "";

	private final String TITLE = "title";

	private final String TAIL = "tail";

	// 薪资条信息
	private PayslipVO paySlipVO = null;
	private AbstractUIAppModel model = null;

	public PayslipBaseSetPanel(PayslipVO vo, AbstractUIAppModel model) {
		super();
		this.model = model;
		initialize();
		getTitleTextArea().grabFocus();

		DefaultConstEnum defaultEnum1 = new DefaultConstEnum(
				SendTypeEnum.MAIL.value(), SendTypeEnum.MAIL.getName());
		DefaultConstEnum defaultEnum2 = new DefaultConstEnum(
				SendTypeEnum.SMS.value(), SendTypeEnum.SMS.getName());

		DefaultConstEnum defaultEnum3 = new DefaultConstEnum(
				SendTypeEnum.SELF.value(), SendTypeEnum.SELF.getName());
		DefaultConstEnum defaultEnum4 = new DefaultConstEnum(
				SendTypeEnum.MOBILE.value(), SendTypeEnum.MOBILE.getName());
		if (vo != null) {
			this.paySlipVO = vo;

			getTitleTextArea().setText(vo.getTitle());
			getTailTextArea().setText(vo.getTail());
			getClassRefPane().setValueObj(vo.getPk_wa_class());
			getClassRefPane().setPK(vo.getPk_wa_class());
			getQijianRefPane().setRefModel(
					new WaPeriodRefTreeModel(getClassRefPane().getRefPK()));
			getQijianRefPane().getRefModel().reloadData();
			getQijianRefPane().setValueObj(vo.getAccyear() + vo.getAccmonth());
			getQijianRefPane().setPK(vo.getAccyear() + vo.getAccmonth());
			getQijianRefPane().setText(vo.getAccyear() + vo.getAccmonth());

			if (vo.getType().equals(SendTypeEnum.MAIL.value())) {
				getSendTypeCombox().setSelectedItem(defaultEnum1);
				getTailTextArea().setEnabled(true);
			} else if (vo.getType().equals(SendTypeEnum.SMS.value())) {
				getSendTypeCombox().setSelectedItem(defaultEnum2);
			} else if (vo.getType().equals(SendTypeEnum.SELF.value())) {
				getSendTypeCombox().setSelectedItem(defaultEnum3);
			} else if (vo.getType().equals(SendTypeEnum.MOBILE.value())) {
				getSendTypeCombox().setSelectedItem(defaultEnum4);
			}
			getClassRefPane().setEnabled(false);
			// getQijianRefPane().setEnabled(false);
			getSendTypeCombox().setEnabled(false);
		} else {

			((WaLoginContext) (model.getContext())).getWaLoginVO()
					.setPayslipType(SendTypeEnum.MAIL.toIntValue());
			getTailTextArea().setEnabled(true);
		}
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.add(getContentPane(), BorderLayout.NORTH);
		UIScrollPane pane = new UIScrollPane();
		pane.setViewportView(getSysVarList());
		this.add(pane, BorderLayout.CENTER);
	}

	private UIPanel getContentPane() {
		if (contentPane == null) {
			contentPane = new UIPanel();
			contentPane.setName("contentPane");
			contentPane.setBounds(0, 0, 160, 40);
			contentPane.setLayout(new BorderLayout());
			FormLayout layout = new FormLayout(
					"left:pref, 2dlu, left:pref, 2dlu,left:pref, 2dlu, left:pref, 80dlu,left:pref",
					"");
			DefaultFormBuilder builder = new DefaultFormBuilder(layout);
			builder.setDefaultDialogBorder();
			builder.append(
					ResHelper.getString("6013payslp", "06013payslp0179")/*
																		 * @res
																		 * "薪资方案"
																		 */,
					getClassRefPane());
			builder.append(
					ResHelper.getString("6013payslp", "06013payslp0178")/*
																		 * @res
																		 * "薪资期间"
																		 */,
					getQijianRefPane());
			builder.nextLine();
			builder.append(
					ResHelper.getString("6013payslp", "06013payslp0161")/*
																		 * @res
																		 * "发送方式"
																		 */,
					getSendTypeCombox());
			builder.nextLine();
			builder.append(
					ResHelper.getString("6013payslp", "06013payslp0162")/*
																		 * @res
																		 * "主题"
																		 */,
					getTitleTextArea(), 7);
			builder.nextLine();
			builder.append(
					ResHelper.getString("6013payslp", "06013payslp0206")/*
																		 * @res
																		 * "表尾"
																		 */,
					getTailTextArea(), 7);
			builder.nextLine();
			contentPane.add(builder.getPanel(), BorderLayout.NORTH);
		}
		return contentPane;
	}

	public UIRefPane getClassRefPane() {
		if (classRefPanel == null) {
			classRefPanel = new WaShowWarningRefPane();
			WaClassRefModel refModel = new WaClassRefModel();
			refModel.setPk_org(model.getContext().getPk_org());
			classRefPanel.setRefModel(refModel);
			classRefPanel.setPk_org(model.getContext().getPk_org());
			classRefPanel.setPreferredSize(new Dimension(180, 20));
			classRefPanel.addValueChangedListener(this);
			classRefPanel.getUITextField().setShowMustInputHint(true);
		}
		return classRefPanel;
	}

	public UIRefPane getQijianRefPane() {
		if (qijianRefPanel == null) {
			qijianRefPanel = new WaShowWarningRefPane();
			WaPeriodRefTreeModel refModel = new WaPeriodRefTreeModel("");
			refModel.setPk_org(model.getContext().getPk_org());
			qijianRefPanel.setRefModel(refModel);
			qijianRefPanel.setPk_org(model.getContext().getPk_org());
			qijianRefPanel.setPreferredSize(new Dimension(180, 20));
			qijianRefPanel.addValueChangedListener(this);
			qijianRefPanel.setNotLeafSelectedEnabled(false);
			qijianRefPanel.getUITextField().setShowMustInputHint(true);
		}
		return qijianRefPanel;
	}

	private nc.ui.pub.beans.UIList getSysVarList() {
		if (sysVarList == null) {
			// ssx modified for Standard Product Error of MultiLanguage options
			// 2015-06-18
			java.util.List<String> nameList = new ArrayList<String>();
			Language lang = NCLangRes4VoTransl.getNCLangRes().getCurrLanguage();

			if (lang.getCode().equals("tradchn")) {
				nameList.add("薪資方案.名稱");
			} else {
				nameList.add("薪资方案.名称");
			}
			String[] names = SalaryreportUtil.getSysVarName();
			for (String name : names) {
				nameList.add(name);
			}
			names = nameList.toArray(new String[0]);
			for (int i = 0; i < names.length; i++) {
				names[i] = "<@" + names[i] + "@>";
			}
			sysVarList = new nc.ui.pub.beans.UIList(names);
			sysVarList.setName("sysVarList");
			sysVarList.setBounds(0, 0, 160, 100);
			sysVarList.addMouseListener(selectListener);
			sysVarList.setBorder(new nc.ui.pub.beans.border.UITitledBorder(
					ResHelper.getString("6013payslp", "06013payslp0163")/*
																		 * @res
																		 * "系统变量"
																		 */));
		}
		return sysVarList;
	}

	public UIComboBox getSendTypeCombox() {
		if (sendTypeCombox == null) {
			sendTypeCombox = new UIComboBox();
			DefaultConstEnum defaultEnum1 = new DefaultConstEnum(
					SendTypeEnum.MAIL.value(), SendTypeEnum.MAIL.getName());
			DefaultConstEnum defaultEnum2 = new DefaultConstEnum(
					SendTypeEnum.SMS.value(), SendTypeEnum.SMS.getName());
			DefaultConstEnum defaultEnum3 = new DefaultConstEnum(
					SendTypeEnum.SELF.value(), SendTypeEnum.SELF.getName());
			// 移动模块
			DefaultConstEnum defaultEnum4 = new DefaultConstEnum(
					SendTypeEnum.MOBILE.value(), SendTypeEnum.MOBILE.getName());
			sendTypeCombox.addItem(defaultEnum1);
			sendTypeCombox.addItem(defaultEnum2);

			// 根据自助模块是否启用，决定是否添加“自助”
			if (isHRSSStarted()) {
				sendTypeCombox.addItem(defaultEnum3);
			}
			// 根据移动模块是否启用，决定是否添加“移动”
			if (isMobileStarted()) {
				sendTypeCombox.addItem(defaultEnum4);
			}
			sendTypeCombox.setPreferredSize(new Dimension(100, 20));
			sendTypeCombox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (getSendTypeCombox().getSelectdItemValue() != null) {
						((WaLoginContext) (model.getContext())).getWaLoginVO()
								.setPayslipType(
										(Integer) (getSendTypeCombox()
												.getSelectdItemValue()));
						getTailTextArea().setText(null);
						if (getSendTypeCombox().getSelectdItemValue().equals(
								SendTypeEnum.MAIL.value())) {
							getTailTextArea().setEnabled(true);
						} else {
							getTailTextArea().setEnabled(false);
						}
					}
				}
			});
		}
		return sendTypeCombox;
	}

	private boolean isHRSSStarted() {

		try {
			return PubEnv.isModuleStarted(PubEnv.getPk_group(),
					HRWACommonConstants.MODULEID_HRSS);
			// return false;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return false;
		}

	}

	/**
	 * 判断移动模块是否启用
	 * 
	 * @return
	 */
	private boolean isMobileStarted() {

		try {
			return PubEnv.isModuleStarted(PubEnv.getPk_group(),
					HRWACommonConstants.MODULEID_MOBILE);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return false;
		}

	}

	public nc.ui.pub.beans.UITextArea getTitleTextArea() {
		if (titleTextArea == null) {
			titleTextArea = new nc.ui.pub.beans.UITextArea();
			titleTextArea.setName("titleTextArea");
			titleTextArea.setLineWrap(true);
			titleTextArea.setMaxLength(500);
			titleTextArea.setPreferredSize(new Dimension(556, 22));
			titleTextArea.addFocusListener(focusListener);
		}
		return titleTextArea;
	}

	public nc.ui.pub.beans.UITextArea getTailTextArea() {
		if (tailTextArea == null) {
			tailTextArea = new nc.ui.pub.beans.UITextArea();
			tailTextArea.setName("tailTextArea");
			tailTextArea.setLineWrap(true);
			tailTextArea.setMaxLength(500);
			tailTextArea.setPreferredSize(new Dimension(556, 22));
			tailTextArea.addFocusListener(focusListener);
			tailTextArea.setEnabled(false);
		}
		return tailTextArea;
	}

	/**
	 * 列表项目选中 创建日期：(2003-6-18 17:41:11)
	 * 
	 * @param event
	 *            java.awt.event.MouseEvent
	 */
	private void listItemSelected(MouseEvent event) {
		if (event.getSource() == getSysVarList() && event.getClickCount() == 2) {
			int iSelectIndex = getSysVarList()
					.locationToIndex(event.getPoint());
			updateTitleText(getSysVarList().getModel()
					.getElementAt(iSelectIndex).toString());
		}
	}

	public void updateTitleText(String str) {
		if (str != null && str.length() > 0) {
			if (focusFlag.equals(TITLE) || !getTailTextArea().isEnabled()) {
				int iCurPos = getTitleTextArea().getCaretPosition();
				if (iCurPos >= 0) {
					getTitleTextArea().insert(str, iCurPos);
				} else {
					getTitleTextArea().append(str);
				}
				getTitleTextArea().requestFocus();
			} else if (focusFlag.equals(TAIL)) {
				int iCurPos = getTailTextArea().getCaretPosition();
				if (iCurPos >= 0) {
					getTailTextArea().insert(str, iCurPos);
				} else {
					getTailTextArea().append(str);
				}
				getTailTextArea().requestFocus();
			}
		}
	}

	/**
	 * 鼠标监听器，监听系统变量的选择
	 * 
	 */
	class MouseSelectListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			listItemSelected(e);
		}
	}

	/**
	 * 文本控件监听器，监听焦点
	 * 
	 */
	class TextAreaFocusListener implements FocusListener {
		// 文本控件获取焦点时
		@Override
		public void focusGained(FocusEvent e) {
			if (e.getSource() == getTitleTextArea()) {
				focusFlag = TITLE;
			} else if (e.getSource() == getTailTextArea()
					&& getTailTextArea().isEnabled()) {
				focusFlag = TAIL;
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			// focusFlag = "";
		}
	}

	public PayslipVO getPaySlipVO() {
		return paySlipVO;
	}

	public void setPaySlipVO(PayslipVO paySlipVO) {
		this.paySlipVO = paySlipVO;
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		if (event.getSource() == getClassRefPane()) {
			((WaShowWarningRefPane) getClassRefPane()).setShowWarning(false);
			((WaShowWarningRefPane) getQijianRefPane()).setShowWarning(false);
			getQijianRefPane().getUITextField().setText(null);
			getQijianRefPane().setRefModel(
					new WaPeriodRefTreeModel(getClassRefPane().getRefPK()));
			getQijianRefPane().getRefModel().reloadData();
		} else if (event.getSource() == getQijianRefPane()) {
			((WaShowWarningRefPane) getClassRefPane()).setShowWarning(false);
			((WaShowWarningRefPane) getQijianRefPane()).setShowWarning(false);
		}
		// else if (event.getSource() == getQijianRefPane())
		// {
		// String strYearAndMonth = getQijianRefPane().getRefPK();
		// String cyear = null;
		// String cMonth = null;
		// if (StringHelper.isEmpty(strYearAndMonth))
		// {
		// return;
		// }
		// cyear = strYearAndMonth.substring(0, 4);
		// cMonth = strYearAndMonth.substring(4);
		// PayrollVO rollvo = new PayrollVO();
		// rollvo.setPk_wa_class(getClassRefPane().getRefPK());
		// rollvo.setCyear(cyear);
		// rollvo.setCperiod(cMonth);
		// IPayrollManageService manageService =
		// NCLocator.getInstance().lookup(IPayrollManageService.class);
		// try {
		// if (!manageService.isPayed(rollvo))
		// {
		// Logger.debug("该期间的单据还没有发放!");
		// throw new BusinessRuntimeException("该期间的单据还没有发放!");
		// }
		// } catch (BusinessException e) {
		// // TODO Auto-generated catch block
		// Logger.error(e.getMessage(),e);
		// throw new BusinessRuntimeException(e.getMessage());
		// }
		// }
	}
}
