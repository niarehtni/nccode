package nc.ui.wa.formular;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;

import nc.hr.utils.ResHelper;
import nc.ui.bd.ref.model.DefdocGridRefModel;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.hr.dataio.ConstEnumFactory;

/**
 * #21266 按日合计加班费函数面板
 * 
 * @author ssx
 * @date 2019-4-5
 */
public class LeaveOverTimeFeeFunctionEditor extends WaAbstractFunctionEditor implements ActionListener {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = -4468484862772531470L;

	private UILabel lblIsTaxFree = null;
	private UILabel lblIsComp = null;
	private UIComboBox yOrnCBox = null;
	private UIComboBox cboIsComp = null;

	// 薪资项目分组参照 Ares.Tank 2019年1月20日21:17:47
	private UILabel groupLabel = null;
	private UIRefPane groupRef = null;

	private UILabel isHourLabel = null;
	private UIComboBox isHourBox = null;

	@Override
	public void setModel(AbstractUIAppModel model) {
		// TODO Auto-generated method stub
		super.setModel(model);
	}

	/**
	 * WaParaPanel 构造子注解。
	 */
	public LeaveOverTimeFeeFunctionEditor() {
		super();
		initialize();
	}

	private static final String funcname = "@" + ResHelper.getString("6013commonbasic", "06013commonbasic0274") + "@";

	// "taxRate";
	@Override
	public String getFuncName() {
		// TODO Auto-generated method stub
		return funcname;
	}

	/**
	 * 初始化类。
	 */
	/* 警告：此方法将重新生成。 */
	private void initialize() {
		try {
			setLayout(null);
			setSize(300, 210);
			setTitle("請選擇參數");

			add(getLblIsComp(), getLblIsComp().getName());
			add(getCboComp(), getCboComp().getName());

			add(getLblIsTaxFree(), getLblIsTaxFree().getName());
			add(getCboYesNo(), getCboYesNo().getName());

			add(getGroupLabel(), getGroupLabel().getName());
			add(getGroupRef(), getGroupRef().getName());

			add(getIsHourLabel(), getIsHourLabel().getName());
			add(getIsHourBox(), getIsHourBox().getName());

			add(getOkButton(), getOkButton().getName());
			add(getCancelButton(), getCancelButton().getName());

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}

		initConnection();

	}

	/**
	 * 返回 refPeriodItem 特性值。
	 * 
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	/* 警告：此方法将重新生成。 */
	private UIComboBox getCboYesNo() {
		if (yOrnCBox == null) {
			try {
				yOrnCBox = new UIComboBox();
				String[] ml = new String[2];
				ml[0] = ResHelper.getString("6013commonbasic", "06013commonbasic0270")/*
																					 * @
																					 * res
																					 * "否"
																					 */;
				ml[1] = ResHelper.getString("6013commonbasic", "06013commonbasic0271")/*
																					 * @
																					 * res
																					 * "是"
																					 */;

				Integer[] mlDefault = new Integer[] { 0, 1 };
				ConstEnumFactory<Integer> mPairFactory = new ConstEnumFactory<Integer>(ml, mlDefault);
				yOrnCBox.addItems(mPairFactory.getAllConstEnums());
				yOrnCBox.setBounds(120, 50, 150, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return yOrnCBox;
	}

	/**
	 * 返回 UILabel3 特性值。
	 * 
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private UILabel getLblIsTaxFree() {
		if (lblIsTaxFree == null) {
			try {
				lblIsTaxFree = new UILabel();
				lblIsTaxFree.setName("label");
				lblIsTaxFree.setText(ResHelper.getString("6013commonbasic", "06013commonbasic0269")/*
																									 * @
																									 * res
																									 * "是否免税"
																									 */);
				lblIsTaxFree.setBounds(10, 50, 100, 22);
				lblIsTaxFree.setHorizontalAlignment(SwingConstants.RIGHT);

			} catch (java.lang.Throwable labExc) {
				handleException(labExc);
			}
		}
		return lblIsTaxFree;
	}

	private UILabel getLblIsComp() {
		if (lblIsComp == null) {
			try {
				lblIsComp = new UILabel();
				lblIsComp.setName("lblIsComp");
				lblIsComp.setText("加班轉調休");
				lblIsComp.setBounds(10, 20, 100, 22);
				lblIsComp.setHorizontalAlignment(SwingConstants.RIGHT);

			} catch (java.lang.Throwable labExc) {
				handleException(labExc);
			}
		}
		return lblIsComp;
	}

	private UIComboBox getCboComp() {
		if (cboIsComp == null) {
			try {
				cboIsComp = new UIComboBox();
				String[] ml = new String[3];
				ml[0] = "轉調休";
				ml[1] = "非轉調休";
				ml[2] = "合計";

				Integer[] mlDefault = new Integer[] { 0, 1, 2 };
				ConstEnumFactory<Integer> mPairFactory = new ConstEnumFactory<Integer>(ml, mlDefault);
				cboIsComp.addItems(mPairFactory.getAllConstEnums());
				cboIsComp.setBounds(120, 20, 150, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return cboIsComp;
	}

	public UILabel getIsHourLabel() {
		if (this.isHourLabel == null) {
			try {
				this.isHourLabel = new UILabel();
				this.isHourLabel.setName("isHourLabel");
				this.isHourLabel.setText("是否加班時數");
				this.isHourLabel.setHorizontalAlignment(SwingConstants.RIGHT);
				this.isHourLabel.setBounds(10, 80, 100, 22);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return this.isHourLabel;
	}

	private UIComboBox getIsHourBox() {
		if (isHourBox == null) {
			try {
				isHourBox = new UIComboBox();
				String[] ml = new String[2];
				ml[0] = ResHelper.getString("6013commonbasic", "06013commonbasic0270")/*
																					 * @
																					 * res
																					 * "否"
																					 */;
				ml[1] = ResHelper.getString("6013commonbasic", "06013commonbasic0271")/*
																					 * @
																					 * res
																					 * "是"
																					 */;

				Integer[] mlDefault = new Integer[] { 0, 1 };
				ConstEnumFactory<Integer> mPairFactory = new ConstEnumFactory<Integer>(ml, mlDefault);
				isHourBox.addItems(mPairFactory.getAllConstEnums());
				isHourBox.setBounds(120, 80, 150, 22);

				isHourBox.addActionListener(this);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return isHourBox;
	}

	public UILabel getGroupLabel() {
		if (this.groupLabel == null) {
			try {
				this.groupLabel = new UILabel();
				this.groupLabel.setName("UILabel3");
				this.groupLabel.setText("薪资项目分组");
				this.groupLabel.setHorizontalAlignment(SwingConstants.RIGHT);
				this.groupLabel.setBounds(10, 110, 100, 22);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return this.groupLabel;
	}

	public UIRefPane getGroupRef() {
		if (groupRef == null) {
			groupRef = new UIRefPane();
			DefdocGridRefModel model = new DefdocGridRefModel();
			// 设置bd_refinfo中的refclass
			groupRef.setRefModel(model);
			// 设置名称(要和bd_refinfo中的一致)
			groupRef.setRefNodeName("薪资项目分组");
			groupRef.getRefModel().setMutilLangNameRef(false);
			groupRef.setPk_org(null);
			// refGroupIns.set
			groupRef.setVisible(true);
			groupRef.setPreferredSize(new Dimension(50, 20));
			groupRef.setButtonFireEvent(true);
			groupRef.getUITextField().setShowMustInputHint(true);
			groupRef.setName("groupRef");
			groupRef.setBounds(120, 110, 150, 22);

		}
		return groupRef;
	}

	/**
	 * 返回 refPeriodItem 特性值。
	 * 
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	/* 警告：此方法将重新生成。 */
	private UIComboBox getYOrnCBox() {
		if (yOrnCBox == null) {
			try {
				yOrnCBox = new UIComboBox();
				String[] ml = new String[2];
				ml[0] = ResHelper.getString("6013commonbasic", "06013commonbasic0270")/*
																					 * @
																					 * res
																					 * "否"
																					 */;
				ml[1] = ResHelper.getString("6013commonbasic", "06013commonbasic0271")/*
																					 * @
																					 * res
																					 * "是"
																					 */;

				Integer[] mlDefault = new Integer[] { 0, 1 };
				ConstEnumFactory<Integer> mPairFactory = new ConstEnumFactory<Integer>(ml, mlDefault);
				yOrnCBox.addItems(mPairFactory.getAllConstEnums());
				yOrnCBox.setBounds(120, 50, 150, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return yOrnCBox;
	}

	/**
	 * /** * 函数参数合法性校验 * @param 参数说明 * @return 返回值 * @exception 异常描述 * @see
	 * 需要参见的其它内容 * @since 从类的那一个版本，此方法被添加进来。（可选） *
	 * 
	 * @deprecated该方法从类的那一个版本后，已经被其它方法替换。（可选） *-/
	 * 
	 * @return java.lang.String
	 */
	public boolean checkPara(int dataType) {
		try {
			// 判断非空
			String nullstr = "";
			if (getCboYesNo().getSelectedIndex() < 0) {
				if (nullstr.length() > 0)
					nullstr += ",";
				nullstr += ResHelper.getString("6013commonbasic", "06013commonbasic0269")/*
																						 * @
																						 * res
																						 * "是否免税"
																						 */;
			}

			if ((Integer) getIsHourBox().getSelectdItemValue() == 0) {
				if (getCboComp().getSelectedIndex() < 0) {
					if (nullstr.length() > 0) {
						nullstr += ",";
					}

					nullstr += "加班轉調休";
				}

				if (nullstr.length() > 0)
					throw new Exception(nullstr + ResHelper.getString("6013commonbasic", "06013commonbasic0021")/*
																												 * @
																												 * res
																												 * "不能为空！"
																												 */);
				if (getGroupRef().getRefPK() == null) {
					throw new Exception("請選擇一個薪資項目分組!");
				}
			}
			return true;

		} catch (Exception e) {
			handleException(e);
			showErrMsg(e.getMessage());
			return false;
		}
	}

	/**
	 * 按期间项目内容取指定类别下的薪资数据（参数：当前类别下的期间，薪资方案，薪资项目）
	 * 
	 * 
	 * @return java.lang.String
	 */
	public String[] getPara() {
		String[] paras = new String[3];
		paras[0] = getCboComp().getSelectdItemValue().toString();
		// 是否免税 0否 1是
		paras[1] = getCboYesNo().getSelectdItemValue().toString();
		paras[2] = "\"" + getGroupRef().getRefPK() + "\"";
		return paras;
	}

	public UIButton getOkButton() {
		if (okButton == null) {
			okButton = new UIButton(ResHelper.getString("common", "UC001-0000044"));
			okButton.setBounds(48, 158, 80, 22);
		}
		return okButton;
	}

	public UIButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new UIButton(ResHelper.getString("common", "UC001-0000008"));
			cancelButton.setBounds(172, 158, 80, 22);
		}
		return cancelButton;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getIsHourBox()) {
			if ((Integer) getIsHourBox().getSelectdItemValue() == 1) {
				this.getGroupRef().getUITextField().setShowMustInputHint(false);
				this.getGroupRef().setEnabled(false);
				this.getGroupRef().setEditable(false);
				this.getGroupRef().setValueObj(null);
			} else {
				this.getGroupRef().getUITextField().setShowMustInputHint(true);
				this.getGroupRef().setEnabled(true);
				this.getGroupRef().setEditable(true);
			}
		}
	}

}