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
	 * WorkAgePanel ������ע�⡣
	 */
	public PsnInfSetParaPanel() {
		super();
		initialize();
	}

	// ������ֻѡ����������Ŀ
	// int STRING = 0; // �ַ�
	// int INTEGER = 1; // ����
	// int DECIMAL = 2; // ����
	// int UFREF = 5; // ����
	// int COMBO = 6; // ����
	// int USERDEF = 7; // �Զ������
	// int MONEY = 18;//���
	// int FRACTION = 19;//����
	// int LITERALDATE = 20; //���ڣ���ʱ����

	public void checkPara(int dataType) throws java.lang.Exception {

		if (getrefHifld().getRefPK() == null) {
			throw new Exception(ResHelper.getString("6001itemsrce",
					"06001itemsrce0013")
					/* @res "��ѡ��Ҫȡ����Ա����Ϣ�" */);
		}

		// ���ж������Ƿ�ƥ��
		int l_datatype = Integer.parseInt(getrefHifld().getRefValue(DATATYPE)
				.toString());

		//

		// 2//��ֵ��
		// 4//bool ��
		// 3 ������
		// 0 1 5 6 7 17//�ַ�

		// hrFormular datatype 0 ��ֵ��
		// 1 �ַ���
		// 2 ������
		// 3 ������
		if (!checkType(dataType, l_datatype)) {
			throw new Exception(ResHelper.getString("6001itemsrce",
					"06001itemsrce0014")
					/* @res "��ѡ��Ϣ���������Ͳ��Ϸ���" */);
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
			if (getchkIfPeriod().isSelected()) {// �����ڼ���������
				getUILabel5().setVisible(true);
				getUILabel52().setVisible(true);
				gettxtSPeriod().setVisible(true);
				getcmbDate().setVisible(true);
				getrefDate().setVisible(false);
				getlblMsg().setText(
						ResHelper
						.getString("6001itemsrce", "06001itemsrce0015")
						/* @res "Ĭ�ϣ��ڼ�Ϊ���򰴵�ǰ�ڼ�" */);
			} else {// ��ԭ������������
				getUILabel5().setVisible(false);
				getUILabel52().setVisible(false);
				gettxtSPeriod().setVisible(false);
				getcmbDate().setVisible(false);
				getrefDate().setVisible(true);
				getlblMsg().setText(
						ResHelper
						.getString("6001itemsrce", "06001itemsrce0016")
						/* @res "Ĭ�ϣ�����Ϊ��ȡ���¼�¼������" */);
			}
		}
		return;
	}

	public void clearDis() {

	}



	/**
	 * ���� cmbDate ����ֵ��
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	/* ���棺�˷������������ɡ� */
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
				 * "��ʼ����"
				 ;
				ml[1] = ResHelper.getString("common", "UC000-0003203")
				 * @res
				 * "��ֹ����"
				 ;
				String[] mlDefault = new String[] {
						ResHelper.getString("common", "UC000-0003900")
						 * @res
						 * "��ʼ����"
						 ,
						 ResHelper.getString("common", "UC000-0003203") 
						  * @res
						  * "��ֹ����"
						  };
						  * -=
						  * notranslate
						  * =
						  * -
						  */
				String[] ml = new String[3];
				ml[0] = ResHelper.getString("common", "UC000-0003900")/*
				 * @res
				 * "��ʼ����"
				 */;
				ml[1] = ResHelper.getString("common", "UC000-0003203")/*
				 * @res
				 * "��ֹ����"
				 */;
				ml[2] = ResHelper.getString("common", "UC000-0004312")/*
				 * @res
				 * "������н���ڼ�"
				 */;
				String[] mlDefault = new String[] {
						ResHelper.getString("common", "UC000-0003900")/*
						 * @res
						 * "��ʼ����"
						 */,
						 ResHelper.getString("common", "UC000-0003203") /*
						  * @res
						  * "��ֹ����"
						  */,
						 ResHelper.getString("common", "UC000-0004312") /*
						  * @res
						  * "������н���ڼ�"
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
	 * ���� cmbRefdata ����ֵ��
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UIComboBox getcmbRefdata() {
		if (ivjcmbRefdata == null) {
			try {
				ivjcmbRefdata = new nc.ui.pub.beans.UIComboBox();
				ivjcmbRefdata.setName("cmbRefdata");
				ivjcmbRefdata.setBounds(90, 120, 140, 22);

				ivjcmbRefdata.setTranslate(true);// ���ʻ�ʱҪ����
				String[] ml = new String[3];
				ml[0] = "";
				ml[1] = ResHelper.getString("common", "UC000-0003279")/* @res "����" */;
				ml[2] = ResHelper.getString("common", "UC000-0001155")/* @res "����" */;
				String[] mlDefault = new String[] {
						"",
						ResHelper.getString("common", "UC000-0003279")/* @res "����" */,
						ResHelper.getString("common", "UC000-0001155") /*
						 * @res
						 * "����"
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
	 * ȡ"."���������
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
	 * ���� lblDate ����ֵ��
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UILabel getlblDate() {
		if (ivjlblDate == null) {
			try {
				ivjlblDate = new nc.ui.pub.beans.UILabel();
				ivjlblDate.setName("lblDate");
				ivjlblDate.setText(ResHelper.getString("6001itemsrce",
						"06001itemsrce0018")
						/* @res "��׼����" */);
				ivjlblDate.setBounds(5, 80, 70, 22);
				ivjlblDate
				.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
				ivjlblDate.setILabelType(0/** JavaĬ��(�Զ���) */
						);/* -=notranslate=- */

			} catch (java.lang.Throwable ivjExc) {

				handleException(ivjExc);
			}
		}
		return ivjlblDate;
	}

	/**
	 * ���� lblHiFld ����ֵ��
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UILabel getlblHiFld() {
		if (ivjlblHiFld == null) {
			try {
				ivjlblHiFld = new nc.ui.pub.beans.UILabel();
				ivjlblHiFld.setName("lblHiFld");
				ivjlblHiFld.setText(ResHelper.getString("6001itemsrce",
						"06001itemsrce0019")
						/* @res "Ա����Ϣ��" */);
				ivjlblHiFld.setForeground(java.awt.Color.black);
				ivjlblHiFld.setILabelType(0/** JavaĬ��(�Զ���) */
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
	 * ���� lblMsg ����ֵ��
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UILabel getlblMsg() {
		if (ivjlblMsg == null) {
			try {
				ivjlblMsg = new nc.ui.pub.beans.UILabel();
				ivjlblMsg.setName("lblMsg");
				ivjlblMsg.setText(ResHelper.getString("6001itemsrce",
						"06001itemsrce0020")
						/* @res "ע����׼����Ϊ��Ĭ��Ϊϵͳ��¼����" */);
				ivjlblMsg.setBounds(8, 200, 186, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjlblMsg;
	}

	/**
	 * ���� lblRefdata ����ֵ��
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UILabel getlblRefdata() {
		if (ivjlblRefdata == null) {
			try {
				ivjlblRefdata = new nc.ui.pub.beans.UILabel();
				ivjlblRefdata.setName("lblRefdata");
				ivjlblRefdata.setText(ResHelper.getString("601315common",
						"0601315common0017")/*
						 * @res "����ȡֵ"
						 */);
				ivjlblRefdata.setBounds(5, 120, 70, 22);
				ivjlblRefdata
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
				ivjlblRefdata.setILabelType(0/** JavaĬ��(�Զ���) */
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
						/* @res "���ڰ�н���ڼ�����" */);
				ivjchkIfPeriod.setBounds(5, 3, 168, 22);
				ivjchkIfPeriod.setActionCommand(ResHelper.getString(
						"6001itemsrce", "06001itemsrce0017")
						/* @res "���ڰ�н���ڼ�����" */);

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
		// ������ڰ���н���ڼ����ø�ʽΪXX_cstartdate������XX_cenddate
		String l_date = null;

		if (getrefHifld().isVisible()) {
			table_code = getrefHifld().getRefModel().getValue(TABLE_CODE)
					.toString();
			table_Fld = getrefHifld().getRefModel().getValue(FLDCODE)
					.toString();

			// "@��¼����@", "@BFV_LOGDATE@"
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

			// ������ȡֵ

			if (getcmbRefdata().isVisible()) {
				if (getcmbRefdata().getSelectedIndex() == 1) {
					// ��ȡ���ƣ�ȡ����
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
								/* @res "����ȡ��ֵ�� ����ԭ���Ǹ����ݶ�Ӧ��ģ��û�б�������û�ж�Ӧ����." */);
					}
				}
			}
			ref_infosetpk = getrefHifld().getRefModel()
					.getValue("infoset.pk_infoset").toString();
			// if (getcmbRefdata().isVisible() &&
			// getcmbRefdata().getSelectedIndex() > 0) {
			// if (getcmbRefdata().isVisible() ) {
			// if (getcmbRefdata().getSelectedIndex() == 1) {
			// // ��ȡ���ƣ�ȡ����
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
			// ��Թ�����¼���ְ��¼�Ĺ�ʽһ�������ԶԼ�ְ��¼�����ر���
			if (ref_infosetpk.equals(ItemPropertyConst.PARTTIME_INFSET_PK)) {
				conditon = ItemPropertyConst.PARTTIME_INFSET_PK;
			}
			if (getchkIfPreLeave().isVisible()
					&& getchkIfPreLeave().isSelected()
					&& ref_infosetpk
					.equals(ItemPropertyConst.FULLTIME_INFSET_PK)) {
				conditon = ItemPropertyConst.FULLTIME_INFSET_PK;
			}
			// �����Ų�������( �еĲ��յ� ref_table �Ƕ����,���ǽ���ȡ������Ϳ����� )
			// �����������������model ������ ��Ա���
			if (isMutlTableJoin(ref_table)) {
				// ����Ǹ�λ
				if (model instanceof PostRefModel) {
					ref_table = "om_post";
				}
				// �����ǲ���
				if (model instanceof HRDeptRefModel) {
					ref_table = "org_dept";
				}

				if (model.getClass().getName()
						.equals("nc.ui.hrcm.templet.model.ContModelRef")) {
					ref_table = "hrcm_contmodel";
				}
				//guoqt����ǲ�����֯
				if (model.getRefTitle().contains("������֯")||model.getRefTitle().contains("Financial Org")
						||model.getRefTitle().contains("ؔ�սM��")) {
					ref_table = "org_financeorg";
				}
				//guoqt�����ְ��
				if (model.getClass().getName().equals("nc.ui.om.ref.JobGradeRefModel2")) {
					ref_table = "om_joblevel";
				}
				//guoqt����Ǻ��㲿��
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
	 * ����������� org_dept.name as name �����
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
	// * * �����ʾ�����úõĲ������
	// * * @param ����˵��
	// * * @return ����ֵ
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
	// // "@��¼����@", "@BFV_LOGDATE@"
	// String l_date = ResHelper.getString("6001itemsrce", "06001itemsrce0022")
	// /* @res "@��ǰ����@" */;
	// if (getchkIfPeriod().isVisible() && getchkIfPeriod().isSelected())
	// {
	// if (gettxtSPeriod().getText() == null ||
	// gettxtSPeriod().getText().trim().length() == 0
	// || gettxtSPeriod().getText().trim().equals("0"))
	// {
	// l_date = ResHelper.getString("6001itemsrce", "06001itemsrce0023")
	// /* @res "��ǰ�ڼ�" */;
	// }
	// else
	// {
	//
	// l_date = MessageFormat.format(ResHelper.getString("6001itemsrce",
	// "06001itemsrce0024")
	// /* @res "ǰ{0}�ڼ�" */, gettxtSPeriod().getText());
	// }
	// if (getcmbDate().getSelectedIndex() == 0)
	// {
	// l_date += ResHelper.getString("common", "UC000-0003900")/* @res "��ʼ����"
	// */;
	// }
	// else
	// {
	// l_date += ResHelper.getString("common", "UC000-0003203")/* @res "��ֹ����"
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
	// // Ҫȡcode
	// }
	// else if (getcmbRefdata().getSelectedIndex() == 2)
	// {
	// l_fldname += "(" + ResHelper.getString("common", "UC000-0001155") + ")";
	// // Ҫȡname
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
	 * ���� UIRefPane11 ����ֵ��
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UIRefPane getrefDate() {
		if (ivjrefDate == null) {
			try {
				ivjrefDate = new nc.ui.pub.beans.UIRefPane();
				ivjrefDate.setName("refDate");
				ivjrefDate.setBounds(90, 80, 140, 22);
				ivjrefDate.setRefNodeName("����");
				ivjrefDate.setVisible(false);

			} catch (java.lang.Throwable ivjExc) {

				handleException(ivjExc);
			}
		}
		return ivjrefDate;
	}

	/**
	 * ���� UIRefPane1 ����ֵ��
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	/* ���棺�˷������������ɡ� */
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

					// ������ֻѡ����������Ŀ
					// int STRING = 0; // �ַ�
					// int INTEGER = 1; // ����
					// int DECIMAL = 2; // ����
					// int UFREF = 5; // ����
					// int COMBO = 6; // ����
					// int USERDEF = 7; // �Զ������
					// int MULTILANGTEXT = 17;//������
					// int MONEY = 18;//���
					// int FRACTION = 19;//����
					// int LITERALDATE = 20; //���ڣ���ʱ����
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

                            // ����ȡ��Ա����
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
	 * ���� txtSPeriod ����ֵ��
	 * @return nc.ui.pub.beans.UITextField
	 */
	/* ���棺�˷������������ɡ� */
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
	 * ���� UILabel5 ����ֵ��
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UILabel getUILabel5() {
		if (ivjUILabel5 == null) {
			try {
				ivjUILabel5 = new nc.ui.pub.beans.UILabel();
				ivjUILabel5.setName("UILabel5");
				ivjUILabel5.setText(ResHelper.getString("6001itemsrce",
						"06001itemsrce0025")
						/* @res " ǰ" */);
				ivjUILabel5.setBounds(85, 80, 28, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabel5;
	}

	/**
	 * ���� UILabel52 ����ֵ��
	 * @return nc.ui.pub.beans.UILabel
	 */
	private nc.ui.pub.beans.UILabel getUILabel52() {
		if (ivjUILabel52 == null) {
			try {
				ivjUILabel52 = new nc.ui.pub.beans.UILabel();
				ivjUILabel52.setName("UILabel52");
				ivjUILabel52.setText(ResHelper.getString("common",
						"UC000-0002560")
						/* @res "�ڼ�" */);
				ivjUILabel52.setBounds(133, 80, 28, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabel52;
	}

	/**
	 * ÿ�������׳��쳣ʱ������
	 * 
	 * @param exception
	 *            java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		Logger.error(exception.getMessage(), exception);
	}

	/**
	 * ��ʼ������
	 * 
	 * @exception java.lang.Exception
	 *                �쳣˵����
	 */
	/* ���棺�˷������������ɡ� */
	private void initConnections() throws java.lang.Exception {
		getrefHifld().addValueChangedListener(ivjEventHandler);
		getchkIfPeriod().addActionListener(ivjEventHandler);
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2002-6-22 12:24:34)
	 */
	public void initData() {

	}

	/**
	 * ��ʼ���ࡣ
	 */
	/* ���棺�˷������������ɡ� */
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
	 * table �ǲ��Ƕ������
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
			// ȡ�Ӽ���������δѡ��Ϣ��
			// ȡ��ѡȡ����Ϣ������
			int l_datatype = Integer.parseInt(getrefHifld().getRefValue(
					DATATYPE).toString());

			// 0,5
			if (l_datatype == 0 || l_datatype == 5) {
				// �̶��������ͣ����磺 Ѫ�� �� ���塢�Ա𣩣���֧��ȡcode����name
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
			//guoqtö�ٵĲ���ѡ��ȡ��ְǰ�����춯�¼���
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
	 * �˴����뷽��˵���� �������ڣ�(2002-6-22 12:56:07)
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
		 * "����"
		 */);
		getlblMsg().setText(
				ResHelper.getString("6001itemsrce", "06001itemsrce0016")
				/* @res "Ĭ�ϣ�����Ϊ��ȡ���¼�¼������" */);

		// getrefHifld().getRefModel().setWherePart(
		// "hi_setdict.pk_hr_defdoctype = '00000000000000000004' and hi_flddict.dr = 0 and datatype <> "
		// /*���˵�ͼƬ���߼����Լ���ע���ֶ�
		// */
		// + nc.vo.hi.pub.CommonValue.DATATYPE_PICTURE
		// + " and datatype<>"
		// + nc.vo.hi.pub.CommonValue.DATATYPE_BOOLEAN
		// + " and datatype<>"
		// + nc.vo.hi.pub.CommonValue.DATATYPE_MEMO
		// /*���˵����ʱ䶯�����ʵ����䶯,���ʵ����Ӽ�*/
		// +" and hi_setdict.pk_setdict<>'40000000000000000019'"
		// +" and hi_setdict.pk_setdict<>'40000000000000000039'"
		// //+" and hi_setdict.pk_setdict<>'40000000000000000036'"
		// /*ֻ��ʾ��ʾ�ֶ�*/
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
	 * ���� chkIfPeriod ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UICheckBox
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UICheckBox getchkIfPreLeave() {
		if (ivjchkIfPreLeave == null) {
			try {
				ivjchkIfPreLeave = new nc.ui.pub.beans.UICheckBox();
				ivjchkIfPreLeave.setName("chkIfPreLeave");
				ivjchkIfPreLeave.setSelected(false);
				ivjchkIfPreLeave.setText(ResHelper.getString("6001itemsrce",
						"06001itemsrce0030")
						/* @res "ȡ��ְǰ������¼"*/);
				ivjchkIfPreLeave.setBounds(5, 160, 168, 22);
				ivjchkIfPreLeave.setActionCommand(ResHelper.getString("6001itemsrce",
						"06001itemsrce0030")
						/* @res "ȡ��ְǰ������¼"*/);

			} catch (java.lang.Throwable ivjExc) {

				handleException(ivjExc);
			}
		}
		return ivjchkIfPreLeave;
	}
}
