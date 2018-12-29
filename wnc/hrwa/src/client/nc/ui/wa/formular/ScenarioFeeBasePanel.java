package nc.ui.wa.formular;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.hr.dataio.ConstEnumFactory;

public class ScenarioFeeBasePanel extends WaAbstractFunctionEditor {
	private static final long serialVersionUID = 6414923710103948013L;

	private UILabel label = null;
	private UIComboBox yOrnCBox = null;

	@Override
	public void setModel(AbstractUIAppModel model) {
		// TODO Auto-generated method stub
		super.setModel(model);
	}

	/**
	 * WaParaPanel 构造子注解。
	 */
	public ScenarioFeeBasePanel() {
		super();
		initialize();
	}

	private static final String funcname = "@" + ResHelper.getString("wadaysalary", "wadaysalary_func_999997") + "@";

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
			setSize(500, 300);

			add(getUILabel(), getUILabel().getName());
			add(getYOrnCBox(), getYOrnCBox().getName());

			add(getOkButton(), getOkButton().getName());
			add(getCancelButton(), getCancelButton().getName());

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}

		initConnection();

	}

	/**
	 * 返回 UILabel3 特性值。
	 * 
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private UILabel getUILabel() {
		if (label == null) {
			try {
				label = new UILabel();
				label.setName("label");
				label.setText(ResHelper.getString("wadaysalary", "wadaysalary_func_999996")/*
																							 * @
																							 * res
																							 * "日是否直接進位成月"
																							 */);
				label.setBounds(10, 30, 80, 22);

			} catch (java.lang.Throwable labExc) {
				handleException(labExc);
			}
		}
		return label;
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
				yOrnCBox.setBounds(100, 30, 140, 22);
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
			if (getYOrnCBox().getSelectedIndex() < 0) {
				if (nullstr.length() > 0)
					nullstr += ",";
				nullstr += ResHelper.getString("wadaysalary", "wadaysalary_func_999996")/*
																						 * @
																						 * res
																						 * "是否免税"
																						 */;
			}
			if (nullstr.length() > 0)
				throw new Exception(nullstr + ResHelper.getString("6013commonbasic", "06013commonbasic0021")/*
																											 * @
																											 * res
																											 * "不能为空！"
																											 */);
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
		String[] paras = new String[1];
		// 日是否直接進位成月0否 1是
		paras[0] = getYOrnCBox().getSelectdItemValue().toString();

		return paras;
	}
}
