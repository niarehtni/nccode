package nc.ui.hr.itemsource.view;

import nc.bs.logging.Logger;
import nc.hr.utils.PairFactory;
import nc.hr.utils.ResHelper;
import nc.ui.bd.ref.IRefModel;
import nc.ui.om.ref.HRDeptRefModel;
import nc.ui.om.ref.PostRefModel;
import nc.ui.pub.beans.UIAsteriskPanelWrapper;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.IBillItem;
import nc.vo.hr.formula.IFormulaConst;
import nc.vo.hr.itemsource.ItemPropertyConst;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

import org.apache.commons.lang.StringUtils;

/**
 * @author: xuanlt
 * @date: 2010-5-24 09:56:58
 * @since: eHR V6.0
 */
public class PsnInfSetParaPanel extends UIPanel implements IParaPanel
{
	private static final String DATATYPE = "data_type";
	private static final String FLDCODE = "item_code";
	// private static final String FLDNAME = "item_name";
	private static final String INFSETCODE = "infoset_code";
	private static final String TABLE_CODE = "table_code";
	// private static final String INFSETNAME = "infoset_name";

	private int datatype = 0;
	private java.lang.String funcName;
	private nc.ui.pub.beans.UICheckBox ivjchkIfPeriod = null;
	private nc.ui.pub.beans.UIComboBox ivjcmbDate = null;
	private nc.ui.pub.beans.UIComboBox ivjcmbRefdata = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private nc.ui.pub.beans.UILabel ivjlblDate = null;
	private nc.ui.pub.beans.UILabel ivjlblHiFld = null;
	private nc.ui.pub.beans.UILabel ivjlblMsg = null;

	private nc.ui.pub.beans.UILabel ivjlblRefdata = null;
	private nc.ui.pub.beans.UIRefPane ivjrefDate = null;
	private nc.ui.pub.beans.UIRefPane ivjrefHifld = null;
	private nc.ui.pub.beans.UITextField ivjtxtSPeriod = null;
	private nc.ui.pub.beans.UILabel ivjUILabel5 = null;

	private nc.ui.pub.beans.UILabel ivjUILabel52 = null;
	private nc.ui.pub.beans.UICheckBox ivjchkIfPreLeave = null;


	private UIAsteriskPanelWrapper refHifldUI = null;;

	class IvjEventHandler implements java.awt.event.ActionListener,
	nc.ui.pub.beans.ValueChangedListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == PsnInfSetParaPanel.this.getchkIfPeriod()) {
				chkIfPeriod_ActionPerformed(e);
			}
		};

		public void valueChanged(nc.ui.pub.beans.ValueChangedEvent event) {
			if (event.getSource() == PsnInfSetParaPanel.this.getrefHifld()) {
				refHifld_ValueChanged(event);
			}
		};
	}

	/**
	 * WorkAgePanel 构造子注解。
	 */
	public PsnInfSetParaPanel() {
		super();
		initialize();
	}

	// 得设置只选择日期型项目
	// int STRING = 0; // 字符
	// int INTEGER = 1; // 整数
	// int DECIMAL = 2; // 数量
	// int UFREF = 5; // 参照
	// int COMBO = 6; // 下拉
	// int USERDEF = 7; // 自定义项档案
	// int MONEY = 18;//金额
	// int FRACTION = 19;//分数
	// int LITERALDATE = 20; //日期（无时区）

	public void checkPara(int dataType) throws java.lang.Exception {

		if (getrefHifld().getRefPK() == null) {
			throw new Exception(ResHelper.getString("6001itemsrce",
					"06001itemsrce0013")
					/* @res "请选择要取数的员工信息项！" */);
		}

		// 再判断类型是否匹配
		int l_datatype = Integer.parseInt(getrefHifld().getRefValue(DATATYPE)
				.toString());

		//

		// 2//数值型
		// 4//bool 型
		// 3 日期型
		// 0 1 5 6 7 17//字符

		// hrFormular datatype 0 数值性
		// 1 字符性
		// 2 日期性
		// 3 布尔性
		if (!checkType(dataType, l_datatype)) {
			throw new Exception(ResHelper.getString("6001itemsrce",
					"06001itemsrce0014")
					/* @res "所选信息项数据类型不合法！" */);
		}

	}

	private boolean checkType(int dataType, int l_datatype) {
		return dataType == 0
				&& (l_datatype == IBillItem.DECIMAL
				|| l_datatype == IBillItem.INTEGER
				|| l_datatype == IBillItem.MONEY || l_datatype == IBillItem.FRACTION)

				|| dataType == 1
				&& (l_datatype == IBillItem.STRING
				|| l_datatype == IBillItem.UFREF
				|| l_datatype == IBillItem.COMBO
				|| l_datatype == IBillItem.USERDEF
				|| l_datatype == IBillItem.BOOLEAN || l_datatype == IBillItem.MULTILANGTEXT)
				|| dataType == 2 && l_datatype == IBillItem.LITERALDATE
				|| dataType == 3 && l_datatype == IBillItem.BOOLEAN;

	}

	/**
	 * Comment
	 */
	public void chkIfPeriod_ActionPerformed(
			java.awt.event.ActionEvent actionEvent) {
		if (actionEvent.getSource().equals(getchkIfPeriod())) {
			if (getchkIfPeriod().isSelected()) {// 按照期间设置日期
				getUILabel5().setVisible(true);
				getUILabel52().setVisible(true);
				gettxtSPeriod().setVisible(true);
				getcmbDate().setVisible(true);
				getrefDate().setVisible(false);
				getlblMsg().setText(
						ResHelper
						.getString("6001itemsrce", "06001itemsrce0015")
						/* @res "默认：期间为空则按当前期间" */);
			} else {// 按原来的日期设置
				getUILabel5().setVisible(false);
				getUILabel52().setVisible(false);
				gettxtSPeriod().setVisible(false);
				getcmbDate().setVisible(false);
				getrefDate().setVisible(true);
				getlblMsg().setText(
						ResHelper
						.getString("6001itemsrce", "06001itemsrce0016")
						/* @res "默认：日期为空取最新记录的数据" */);
			}
		}
		return;
	}

	public void clearDis() {

	}



	/**
	 * 返回 cmbDate 特性值。
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIComboBox getcmbDate() {
		if (ivjcmbDate == null) {
			try {
				ivjcmbDate = new nc.ui.pub.beans.UIComboBox();
				ivjcmbDate.setName("cmbDate");
				ivjcmbDate.setBounds(150, 80, 75, 22);
				ivjcmbDate.setTranslate(true);

				/*String[] ml = new String[2];
				ml[0] = ResHelper.getString("common", "UC000-0003900")
				 * @res
				 * "起始日期"
				 ;
				ml[1] = ResHelper.getString("common", "UC000-0003203")
				 * @res
				 * "终止日期"
				 ;
				String[] mlDefault = new String[] {
						ResHelper.getString("common", "UC000-0003900")
						 * @res
						 * "起始日期"
						 ,
						 ResHelper.getString("common", "UC000-0003203") 
						  * @res
						  * "终止日期"
						  };
						  * -=
						  * notranslate
						  * =
						  * -
						  */
				String[] ml = new String[3];
				ml[0] = ResHelper.getString("common", "UC000-0003900")/*
				 * @res
				 * "起始日期"
				 */;
				ml[1] = ResHelper.getString("common", "UC000-0003203")/*
				 * @res
				 * "终止日期"
				 */;
				ml[2] = ResHelper.getString("common", "UC000-0004312")/*
				 * @res
				 * "包含于薪资期间"
				 */;
				String[] mlDefault = new String[] {
						ResHelper.getString("common", "UC000-0003900")/*
						 * @res
						 * "起始日期"
						 */,
						 ResHelper.getString("common", "UC000-0003203") /*
						  * @res
						  * "终止日期"
						  */,
						 ResHelper.getString("common", "UC000-0004312") /*
						  * @res
						  * "包含于薪资期间"
						  */};/*
						  * -=
						  * notranslate
						  * =
						  * -
						  */
				PairFactory mPairFactory = new PairFactory(ml, mlDefault);
				ivjcmbDate.addItems(mPairFactory.getAllConstEnums());
			} catch (java.lang.Throwable ivjExc) {

				handleException(ivjExc);
			}
		}
		return ivjcmbDate;
	}

	/**
	 * 返回 cmbRefdata 特性值。
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIComboBox getcmbRefdata() {
		if (ivjcmbRefdata == null) {
			try {
				ivjcmbRefdata = new nc.ui.pub.beans.UIComboBox();
				ivjcmbRefdata.setName("cmbRefdata");
				ivjcmbRefdata.setBounds(90, 120, 140, 22);

				ivjcmbRefdata.setTranslate(true);// 国际化时要翻译
				String[] ml = new String[3];
				ml[0] = "";
				ml[1] = ResHelper.getString("common", "UC000-0003279")/* @res "编码" */;
				ml[2] = ResHelper.getString("common", "UC000-0001155")/* @res "名称" */;
				String[] mlDefault = new String[] {
						"",
						ResHelper.getString("common", "UC000-0003279")/* @res "编码" */,
						ResHelper.getString("common", "UC000-0001155") /*
						 * @res
						 * "名称"
						 */};/*
						 * -=
						 * notranslate
						 * =
						 * -
						 */
				PairFactory mPairFactory = new PairFactory(ml, mlDefault);
				getcmbRefdata().addItems(mPairFactory.getAllConstEnums());
				getcmbRefdata().setSelectedIndex(2);

			} catch (java.lang.Throwable ivjExc) {

				handleException(ivjExc);
			}
		}
		return ivjcmbRefdata;
	}

	public int getDatatype() {
		return datatype;
	}

	/**
	 * 取"."后面的数据
	 * @param table_Fld
	 * @return
	 */
	private String getFldAfterdot(String table_Fld) {
		if (StringUtils.isBlank(table_Fld)) {
			return table_Fld;
		}
		if (table_Fld.contains(".")) {
			return table_Fld.substring(table_Fld.indexOf(".") + 1);
		}

		return table_Fld;

	}

	public java.lang.String getFuncName() {
		return funcName;
	}

	/**
	 * 返回 lblDate 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getlblDate() {
		if (ivjlblDate == null) {
			try {
				ivjlblDate = new nc.ui.pub.beans.UILabel();
				ivjlblDate.setName("lblDate");
				ivjlblDate.setText(ResHelper.getString("6001itemsrce",
						"06001itemsrce0018")
						/* @res "基准日期" */);
				ivjlblDate.setBounds(5, 80, 70, 22);
				ivjlblDate
				.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
				ivjlblDate.setILabelType(0/** Java默认(自定义) */
						);/* -=notranslate=- */

			} catch (java.lang.Throwable ivjExc) {

				handleException(ivjExc);
			}
		}
		return ivjlblDate;
	}

	/**
	 * 返回 lblHiFld 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getlblHiFld() {
		if (ivjlblHiFld == null) {
			try {
				ivjlblHiFld = new nc.ui.pub.beans.UILabel();
				ivjlblHiFld.setName("lblHiFld");
				ivjlblHiFld.setText(ResHelper.getString("6001itemsrce",
						"06001itemsrce0019")
						/* @res "员工信息项" */);
				ivjlblHiFld.setForeground(java.awt.Color.black);
				ivjlblHiFld.setILabelType(0/** Java默认(自定义) */
						); /* -=notranslate=- */
				ivjlblHiFld.setFont(new java.awt.Font("dialog", 0, 12));
				ivjlblHiFld.setBounds(5, 40, 70, 22);
				ivjlblHiFld
				.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjlblHiFld;
	}

	/**
	 * 返回 lblMsg 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getlblMsg() {
		if (ivjlblMsg == null) {
			try {
				ivjlblMsg = new nc.ui.pub.beans.UILabel();
				ivjlblMsg.setName("lblMsg");
				ivjlblMsg.setText(ResHelper.getString("6001itemsrce",
						"06001itemsrce0020")
						/* @res "注：基准日期为空默认为系统登录日期" */);
				ivjlblMsg.setBounds(8, 200, 186, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjlblMsg;
	}

	/**
	 * 返回 lblRefdata 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getlblRefdata() {
		if (ivjlblRefdata == null) {
			try {
				ivjlblRefdata = new nc.ui.pub.beans.UILabel();
				ivjlblRefdata.setName("lblRefdata");
				ivjlblRefdata.setText(ResHelper.getString("601315common",
						"0601315common0017")/*
						 * @res "参照取值"
						 */);
				ivjlblRefdata.setBounds(5, 120, 70, 22);
				ivjlblRefdata
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
				ivjlblRefdata.setILabelType(0/** Java默认(自定义) */
						);

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjlblRefdata;
	}

	private nc.ui.pub.beans.UICheckBox getchkIfPeriod() {
		if (ivjchkIfPeriod == null) {
			try {
				ivjchkIfPeriod = new nc.ui.pub.beans.UICheckBox();
				ivjchkIfPeriod.setName("chkIfPeriod");
				ivjchkIfPeriod.setSelected(true);
				ivjchkIfPeriod.setText("  "
						+ ResHelper.getString("6001itemsrce",
								"06001itemsrce0017")
						/* @res "日期按薪资期间设置" */);
				ivjchkIfPeriod.setBounds(5, 3, 168, 22);
				ivjchkIfPeriod.setActionCommand(ResHelper.getString(
						"6001itemsrce", "06001itemsrce0017")
						/* @res "日期按薪资期间设置" */);

			} catch (java.lang.Throwable ivjExc) {

				handleException(ivjExc);
			}
		}
		return ivjchkIfPeriod;
	}

	/**
	 * para: table_code, item_code, code_name, date, ref_table, ref_item_code
	 * 
	 * @author zhangg on 2010-6-7
	 * @see nc.ui.hr.itemsource.view.IParaPanel#getPara()
	 */
	public java.lang.String[] getPara() throws java.lang.Exception {
		String table_code;
		String table_Fld;
		String code_name = "null";
		String ref_table = "null";
		String ref_item_code = "null";
		String ref_item_name = "null";
		String ref_pk_field = "null";

		String ref_infosetpk = "null";
		IRefModel model = null;
		// 如果日期按照薪资期间设置格式为XX_cstartdate或者是XX_cenddate
		String l_date = null;

		if (getrefHifld().isVisible()) {
			table_code = getrefHifld().getRefModel().getValue(TABLE_CODE)
					.toString();
			table_Fld = getrefHifld().getRefModel().getValue(FLDCODE)
					.toString();

			// "@登录日期@", "@BFV_LOGDATE@"
			if (getchkIfPeriod().isVisible() && getchkIfPeriod().isSelected()) {

				if (gettxtSPeriod().getText() == null
						|| gettxtSPeriod().getText().trim().length() == 0) {
					l_date = "0";
				} else {
					l_date = gettxtSPeriod().getText();
				}

				l_date += IFormulaConst.splitSymbol
						+ (getcmbDate().getSelectedIndex() == 0 ? "cstartdate"
								: (getcmbDate().getSelectedIndex() == 1 ? "cenddate" : "middledate"));
			} else if (getrefDate().getText() != null
					&& getrefDate().getText().length() > 0) {
				Object object = getrefDate().getValueObj();
				if (object instanceof UFDate) {
					UFDate date = (UFDate) object;
					l_date = date.getYear() + IFormulaConst.splitSymbol
							+ date.getMonth() + IFormulaConst.splitSymbol
							+ date.getDay();
				}
			}

			// 参照型取值

			if (getcmbRefdata().isVisible()) {
				if (getcmbRefdata().getSelectedIndex() == 1) {
					// 不取名称，取编码
					code_name = "code";
				} else {
					code_name = "name";
				}
			}


			if (getrefHifld().getRefModel().getValue("ref_model_name") != null) {
				String ref_model_name = getrefHifld().getRefModel()
						.getValue("ref_model_name").toString();

				model = InfoSetModelHelper.getRefModel(ref_model_name);
				if (model != null) {
					ref_table = model.getTableName();
					ref_item_name = model.getRefNameField();
					ref_item_name = getRef_item_name(ref_item_name);
					ref_item_code = model.getRefCodeField();
					ref_pk_field = model.getPkFieldCode();

					if (ref_table == null || ref_item_code == null) {
						throw new BusinessException(ResHelper.getString(
								"6001itemsrce", "06001itemsrce0021")
								/* @res "不能取该值， 具体原因是该数据对应的模型没有表名或者没有对应的列." */);
					}
				}
			}
			ref_infosetpk = getrefHifld().getRefModel()
					.getValue("infoset.pk_infoset").toString();
			// if (getcmbRefdata().isVisible() &&
			// getcmbRefdata().getSelectedIndex() > 0) {
			// if (getcmbRefdata().isVisible() ) {
			// if (getcmbRefdata().getSelectedIndex() == 1) {
			// // 不取名称，取编码
			// code_name = "code";
			// } else {
			// code_name = "name";
			// }
			// }

			int l_datatype = Integer.parseInt(getrefHifld().getRefValue(
					DATATYPE).toString());

			// (hi_psnjob,pk_jobrank,null,null,om_jobrank,jobrankname)
			String refcodeORname = ref_item_name;
			if (code_name.equals("code")) {
				refcodeORname = ref_item_code;
			}
			String conditon = "null";

			if (l_datatype == IBillItem.COMBO) {
				Object obj = getrefHifld().getRefModel().getValue(
						"pk_infoset_item");
				if (obj != null) {
					conditon = obj.toString();
				}

			}
			// 针对工作记录与兼职记录的公式一样，所以对兼职记录进行特别处理
			if (ref_infosetpk.equals(ItemPropertyConst.PARTTIME_INFSET_PK)) {
				conditon = ItemPropertyConst.PARTTIME_INFSET_PK;
			}
			if (getchkIfPreLeave().isVisible()
					&& getchkIfPreLeave().isSelected()
					&& ref_infosetpk
					.equals(ItemPropertyConst.FULLTIME_INFSET_PK)) {
				conditon = ItemPropertyConst.FULLTIME_INFSET_PK;
			}
			// 处理部门参照问题( 有的参照的 ref_table 是多个表,我们仅仅取到主表就可以了 )
			// 单独处理连个特殊的model 部门与 人员类别
			if (isMutlTableJoin(ref_table)) {
				// 如果是岗位
				if (model instanceof PostRefModel) {
					ref_table = "om_post";
				}
				// 如若是部门
				if (model instanceof HRDeptRefModel) {
					ref_table = "org_dept";
				}

				if (model.getClass().getName()
						.equals("nc.ui.hrcm.templet.model.ContModelRef")) {
					ref_table = "hrcm_contmodel";
				}
				//guoqt如果是财务组织
				if (model.getRefTitle().contains("财务组织")||model.getRefTitle().contains("Financial Org")
						||model.getRefTitle().contains("財務組織")) {
					ref_table = "org_financeorg";
				}
				//guoqt如果是职级
				if (model.getClass().getName().equals("nc.ui.om.ref.JobGradeRefModel2")) {
					ref_table = "om_joblevel";
				}
				//guoqt如果是核算部门
				if (model.getClass().getName().equals("nc.ui.org.ref.BusinessUnitAndDeptDefaultRefModel")) {
					ref_table = "org_dept";
					ref_pk_field = "pk_dept";
				}
			}

			return new String[] { table_code, getFldAfterdot(table_Fld),
					getFldAfterdot(refcodeORname), l_date, ref_table,
					getFldAfterdot(ref_pk_field), conditon };
		}
		return null;
	}

	/**
	 * 处理多语言中 org_dept.name as name 的情况
	 * @param ref_item_name
	 * @return
	 */
	private String getRef_item_name(String ref_item_name) {
		if (StringUtils.isEmpty(ref_item_name)) {
			return "";
		}

		String namtrim = ref_item_name.trim();
		if (StringUtils.isEmpty(namtrim)) {
			return "";
		}

		String[] names = namtrim.split(" ");
		return names[0];

	}

	// /**
	// * /**
	// * * 获得显示用设置好的参数结果
	// * * @param 参数说明
	// * * @return 返回值
	// * *
	// * *
	// * *
	// * *
	// * *-/
	// * @return java.lang.String
	// */
	// public java.lang.String getParaStr()
	// {
	// if (getrefHifld().isVisible())
	// {
	// // "@登录日期@", "@BFV_LOGDATE@"
	// String l_date = ResHelper.getString("6001itemsrce", "06001itemsrce0022")
	// /* @res "@当前日期@" */;
	// if (getchkIfPeriod().isVisible() && getchkIfPeriod().isSelected())
	// {
	// if (gettxtSPeriod().getText() == null ||
	// gettxtSPeriod().getText().trim().length() == 0
	// || gettxtSPeriod().getText().trim().equals("0"))
	// {
	// l_date = ResHelper.getString("6001itemsrce", "06001itemsrce0023")
	// /* @res "当前期间" */;
	// }
	// else
	// {
	//
	// l_date = MessageFormat.format(ResHelper.getString("6001itemsrce",
	// "06001itemsrce0024")
	// /* @res "前{0}期间" */, gettxtSPeriod().getText());
	// }
	// if (getcmbDate().getSelectedIndex() == 0)
	// {
	// l_date += ResHelper.getString("common", "UC000-0003900")/* @res "起始日期"
	// */;
	// }
	// else
	// {
	// l_date += ResHelper.getString("common", "UC000-0003203")/* @res "终止日期"
	// */;
	// }
	//
	// }
	// else if (getrefDate().getText() != null &&
	// getrefDate().getText().length() > 0)
	// {
	// l_date = getrefDate().getText();
	// }
	// String l_fldname =
	// getrefHifld().getRefModel().getValue(INFSETNAME).toString();
	// l_fldname += "." +
	// getrefHifld().getRefModel().getValue(FLDNAME).toString();
	//
	// if (getcmbRefdata().isVisible())
	// {
	// if (getcmbRefdata().getSelectedIndex() == 1)
	// {
	// l_fldname += "(" + ResHelper.getString("common", "UC000-0003279") + ")";
	// // 要取code
	// }
	// else if (getcmbRefdata().getSelectedIndex() == 2)
	// {
	// l_fldname += "(" + ResHelper.getString("common", "UC000-0001155") + ")";
	// // 要取name
	// }
	// }
	// return l_fldname + "," + l_date;
	//
	// }
	// else
	// {
	//
	// return "";
	// }
	// }

	/**
	 * 返回 UIRefPane11 特性值。
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIRefPane getrefDate() {
		if (ivjrefDate == null) {
			try {
				ivjrefDate = new nc.ui.pub.beans.UIRefPane();
				ivjrefDate.setName("refDate");
				ivjrefDate.setBounds(90, 80, 140, 22);
				ivjrefDate.setRefNodeName("日历");
				ivjrefDate.setVisible(false);

			} catch (java.lang.Throwable ivjExc) {

				handleException(ivjExc);
			}
		}
		return ivjrefDate;
	}

	/**
	 * 返回 UIRefPane1 特性值。
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UIRefPane getrefHifld() {
		if (ivjrefHifld == null) {
			try {
				ivjrefHifld = new nc.ui.pub.beans.UIRefPane();
				ivjrefHifld.setName("refHifld");
				ivjrefHifld.setBounds(90, 40, 140, 22);

				try {
					Class refmodel = Class
							.forName("nc.ui.hr.itemsource.view.ParaInfSetandFldRefModel");
					ivjrefHifld
					.setRefModel((nc.ui.bd.ref.AbstractRefModel) refmodel
							.newInstance());

					// 得设置只选择日期型项目
					// int STRING = 0; // 字符
					// int INTEGER = 1; // 整数
					// int DECIMAL = 2; // 数量
					// int UFREF = 5; // 参照
					// int COMBO = 6; // 下拉
					// int USERDEF = 7; // 自定义项档案
					// int MULTILANGTEXT = 17;//多语言
					// int MONEY = 18;//金额
					// int FRACTION = 19;//分数
					// int LITERALDATE = 20; //日期（无时区）
					// IBillItem.BOOLEAN =4

					ivjrefHifld
					.getRefModel()
					.setWherePart(
							"   infosetitem.data_type in ('"
									+ IBillItem.STRING + "','"
									+ IBillItem.INTEGER + "','"
									+ IBillItem.DECIMAL + "','"
									+ IBillItem.UFREF + "','"
									+ IBillItem.COMBO + "','"
									+ IBillItem.USERDEF + "','"
									+ IBillItem.MULTILANGTEXT + "','"
									+ IBillItem.MONEY + "','"
									+ IBillItem.FRACTION + "','"
									+ IBillItem.LITERALDATE + "','"
									+ IBillItem.BOOLEAN + "') " +

                            // 不能取人员主键
							" and item_code <> 'pk_psndoc' and item_code <> 'addr'   ");

				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
				ivjrefHifld.setButtonFireEvent(true);

			} catch (java.lang.Throwable ivjExc) {

				handleException(ivjExc);
			}
		}
		return ivjrefHifld;
	}

	private UIAsteriskPanelWrapper getrefHifldUI() {

		if (refHifldUI == null) {
			refHifldUI = new UIAsteriskPanelWrapper(getrefHifld());
			refHifldUI.setBounds(83, 40, 147, 22);
			refHifldUI.setMustInputItem(true);
		}

		return refHifldUI;

	}

	/**
	 * 返回 txtSPeriod 特性值。
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UITextField gettxtSPeriod() {
		if (ivjtxtSPeriod == null) {
			try {
				ivjtxtSPeriod = new nc.ui.pub.beans.UITextField();
				ivjtxtSPeriod.setName("txtSPeriod");
				ivjtxtSPeriod.setBounds(100, 80, 30, 20);
				ivjtxtSPeriod.setTextType("TextInt");
				ivjtxtSPeriod.setMaxLength(2);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjtxtSPeriod;
	}

	/**
	 * 返回 UILabel5 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabel5() {
		if (ivjUILabel5 == null) {
			try {
				ivjUILabel5 = new nc.ui.pub.beans.UILabel();
				ivjUILabel5.setName("UILabel5");
				ivjUILabel5.setText(ResHelper.getString("6001itemsrce",
						"06001itemsrce0025")
						/* @res " 前" */);
				ivjUILabel5.setBounds(85, 80, 28, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabel5;
	}

	/**
	 * 返回 UILabel52 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	private nc.ui.pub.beans.UILabel getUILabel52() {
		if (ivjUILabel52 == null) {
			try {
				ivjUILabel52 = new nc.ui.pub.beans.UILabel();
				ivjUILabel52.setName("UILabel52");
				ivjUILabel52.setText(ResHelper.getString("common",
						"UC000-0002560")
						/* @res "期间" */);
				ivjUILabel52.setBounds(133, 80, 28, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabel52;
	}

	/**
	 * 每当部件抛出异常时被调用
	 * 
	 * @param exception
	 *            java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		Logger.error(exception.getMessage(), exception);
	}

	/**
	 * 初始化连接
	 * 
	 * @exception java.lang.Exception
	 *                异常说明。
	 */
	/* 警告：此方法将重新生成。 */
	private void initConnections() throws java.lang.Exception {
		getrefHifld().addValueChangedListener(ivjEventHandler);
		getchkIfPeriod().addActionListener(ivjEventHandler);
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-6-22 12:24:34)
	 */
	public void initData() {

	}

	/**
	 * 初始化类。
	 */
	/* 警告：此方法将重新生成。 */
	private void initialize() {
		try {
			setName("PsnInfSetParaPanel");
			setLayout(null);
			setSize(240, 200);
			add(getlblDate(), getlblDate().getName());
			add(getrefDate(), getrefDate().getName());
			add(getlblMsg(), getlblMsg().getName());
			add(getUILabel5(), getUILabel5().getName());

			add(getlblRefdata());
			add(getcmbRefdata());

			add(gettxtSPeriod(), gettxtSPeriod().getName());
			add(getUILabel52(), getUILabel52().getName());
			add(getcmbDate(), getcmbDate().getName());

			add(getlblHiFld(), getlblHiFld().getName());
			add(getrefHifldUI(), getrefHifld().getName());
			add(getchkIfPeriod(), getchkIfPeriod().getName());

			add(getchkIfPreLeave(), getchkIfPreLeave().getName());
			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		initData();
	}

	/**
	 * table 是不是多表联合
	 * @param table
	 * @return
	 */
	private boolean isMutlTableJoin(String table) {
		return table.trim().contains(" ") || table.trim().contains(",");
	}

	/**
	 * Comment
	 */
	public void refHifld_ValueChanged(nc.ui.pub.beans.ValueChangedEvent event) {
		getlblRefdata().setVisible(false);
		getcmbRefdata().setVisible(false);
		if (getrefHifld().getRefPK() != null) {
			// 取子集函数，且未选信息项
			// 取得选取的信息项类型
			int l_datatype = Integer.parseInt(getrefHifld().getRefValue(
					DATATYPE).toString());

			// 0,5
			if (l_datatype == 0 || l_datatype == 5) {
				// 固定档案类型（例如： 血型 、 民族、性别），则支持取code还是name
				getlblRefdata().setVisible(true);
				getcmbRefdata().setVisible(true);
			}
			if (getrefHifld().getRefValue("infoset_code").equals("hi_psnjob")) {
				if(!getchkIfPreLeave().isVisible()){
					getchkIfPreLeave().setSelected(false);
				}
				getchkIfPreLeave().setVisible(true);
			}else{
				getchkIfPreLeave().setVisible(false);
				getchkIfPreLeave().setSelected(false);
			}
			//guoqt枚举的不能选择取离职前（如异动事件）
			if(l_datatype == IBillItem.COMBO){
				getchkIfPreLeave().setVisible(false);
				getchkIfPreLeave().setSelected(false);
			}
		}
		return;
	}

	/**
	 * @author zhangg on 2010-6-3
	 * @see nc.ui.hr.itemsource.view.IParaPanel#setCurrentItemKey(java.lang.String)
	 */
	@Override
	public void setCurrentItemKey(String itemKey) {

	}

	public void setDatatype(int newDatatype) {
		datatype = newDatatype;
	}

	public void setFuncName(java.lang.String newFuncName) {
		funcName = newFuncName;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2002-6-22 12:56:07)
	 * 
	 * @param index
	 *            int
	 */
	public void updateDis(int index) {

	}

	public void updateDis(java.lang.String newFuncName) {
		setFuncName(newFuncName);
		getlblRefdata().setVisible(false);
		getcmbRefdata().setVisible(false);
		getchkIfPeriod().setVisible(false);

		getlblDate().setVisible(true);
		getlblHiFld().setVisible(true);
		getlblMsg().setVisible(true);
		getrefDate().setVisible(true);
		getrefHifld().setVisible(true);

		getchkIfPeriod().setVisible(true);
		getchkIfPeriod().setSelected(false);
		getchkIfPreLeave().setVisible(false);
		getlblDate()
		.setText(ResHelper.getString("common", "UC000-0002313")/*
		 * @res
		 * "日期"
		 */);
		getlblMsg().setText(
				ResHelper.getString("6001itemsrce", "06001itemsrce0016")
				/* @res "默认：日期为空取最新记录的数据" */);

		// getrefHifld().getRefModel().setWherePart(
		// "hi_setdict.pk_hr_defdoctype = '00000000000000000004' and hi_flddict.dr = 0 and datatype <> "
		// /*过滤掉图片，逻辑型以及备注型字段
		// */
		// + nc.vo.hi.pub.CommonValue.DATATYPE_PICTURE
		// + " and datatype<>"
		// + nc.vo.hi.pub.CommonValue.DATATYPE_BOOLEAN
		// + " and datatype<>"
		// + nc.vo.hi.pub.CommonValue.DATATYPE_MEMO
		// /*过滤掉工资变动，工资档案变动,工资档案子集*/
		// +" and hi_setdict.pk_setdict<>'40000000000000000019'"
		// +" and hi_setdict.pk_setdict<>'40000000000000000039'"
		// //+" and hi_setdict.pk_setdict<>'40000000000000000036'"
		// /*只显示显示字段*/
		// +" and hi_flddict.isdisplay='Y' and ((hi_flddict.pk_corp = '0001' and hi_flddict.isshare = 'Y') or hi_flddict.create_pk_corp = '"+
		// Global.getCorpPK()+"')");

		getUILabel5().setVisible(false);
		getUILabel52().setVisible(false);
		gettxtSPeriod().setVisible(false);
		getcmbDate().setVisible(false);

	}

	public void updateDis(nc.vo.hr.func.FunctableItemVO[] paras) {
	}

	/**
	 * 返回 chkIfPeriod 特性值。
	 * 
	 * @return nc.ui.pub.beans.UICheckBox
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UICheckBox getchkIfPreLeave() {
		if (ivjchkIfPreLeave == null) {
			try {
				ivjchkIfPreLeave = new nc.ui.pub.beans.UICheckBox();
				ivjchkIfPreLeave.setName("chkIfPreLeave");
				ivjchkIfPreLeave.setSelected(false);
				ivjchkIfPreLeave.setText(ResHelper.getString("6001itemsrce",
						"06001itemsrce0030")
						/* @res "取离职前工作记录"*/);
				ivjchkIfPreLeave.setBounds(5, 160, 168, 22);
				ivjchkIfPreLeave.setActionCommand(ResHelper.getString("6001itemsrce",
						"06001itemsrce0030")
						/* @res "取离职前工作记录"*/);

			} catch (java.lang.Throwable ivjExc) {

				handleException(ivjExc);
			}
		}
		return ivjchkIfPreLeave;
	}
}
