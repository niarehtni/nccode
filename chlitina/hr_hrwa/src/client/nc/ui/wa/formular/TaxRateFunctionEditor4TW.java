package nc.ui.wa.formular;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IClassItemQueryService;
import nc.itf.hr.wa.IItemQueryService;
import nc.ui.hr.formula.itf.IVariableFactory;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.hr.dataio.ConstEnumFactory;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.ClassItemContext;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author:
 * @date: 2010-6-6 ����10:19:13
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class TaxRateFunctionEditor4TW extends WaAbstractFunctionEditor {
	private static final long serialVersionUID = 6414923710103945313L;
	private nc.ui.pub.beans.UIComboBox ivjcmbItem = null;
	private nc.ui.pub.beans.UILabel ivjUILabel2 = null;
	private nc.ui.pub.beans.UILabel ivjUILabel3 = null;

	private nc.ui.pub.beans.UILabel ivjUILabel4 = null;

	private nc.ui.pub.beans.UILabel ivjUILabel5 = null;
	private nc.ui.pub.beans.UIComboBox ivjcmbItem4 = null;
	private nc.ui.pub.beans.UIComboBox ivjcmbItem5 = null;

	private UIComboBox taxRateMode = null;

	@Override
	public void setModel(AbstractUIAppModel model) {
		super.setModel(model);
		initData();
	}

	/**
	 * WaParaPanel ������ע�⡣
	 */
	public TaxRateFunctionEditor4TW() {
		super();
		initialize();
	}

	private static final String funcname = "@"
			+ ResHelper.getString("6013commonbasic", "06013commonbasic0065")
			+ "@";

	// "taxRate";
	@Override
	public String getFuncName() {
		return funcname;
	}

	/**
	 * ��ʼ���ࡣ
	 */
	/* ���棺�˷������������ɡ� */
	private void initialize() {
		try {
			setLayout(null);
			setSize(300, 300);

			add(getUILabel3(), getUILabel3().getName());
			add(getTaxMode(), getTaxMode().getName());

			add(getUILabel2(), getUILabel2().getName());
			add(getcmbItem(), getcmbItem().getName());

			add(getUILabel4(), getUILabel4().getName());
			add(getcmbItem4(), getcmbItem4().getName());

			add(getUILabel5(), getUILabel5().getName());
			add(getcmbItem5(), getcmbItem5().getName());

			add(getOkButton(), getOkButton().getName());
			add(getCancelButton(), getOkButton().getName());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}

		initConnection();

	}

	/**
	 * ���� UILabel3 ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UILabel getUILabel3() {
		if (ivjUILabel3 == null) {
			try {
				ivjUILabel3 = new nc.ui.pub.beans.UILabel();
				ivjUILabel3.setName("UILabel3");
				ivjUILabel3.setText(ResHelper.getString("6013commonbasic",
						"06013commonbasic0025")/* @res "��˰��ʽ" */);
				ivjUILabel3.setBounds(10, 30, 80, 22);

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabel3;
	}

	/**
	 * ���� UILabel2 ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UILabel getUILabel2() {
		if (ivjUILabel2 == null) {
			try {
				ivjUILabel2 = new nc.ui.pub.beans.UILabel();
				ivjUILabel2.setName("UILabel2");
				ivjUILabel2.setText(ResHelper.getString("6013commonbasic",
						"06013commonbasic0024")/* @res "н����Ŀ��" */);
				ivjUILabel2.setBounds(10, 70, 80, 22);

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabel2;
	}

	/**
	 * ���� UILabel2 ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UILabel getUILabel4() {
		if (ivjUILabel4 == null) {
			try {
				ivjUILabel4 = new nc.ui.pub.beans.UILabel();
				ivjUILabel4.setName("UILabel2");
				ivjUILabel4.setText(ResHelper.getString("6013commonbasic",
						"06013commonbasic0225")/* @res "���¹��ʣ�" */);
				ivjUILabel4.setBounds(10, 110, 80, 22);
				ivjUILabel4.setVisible(false);

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabel4;
	}

	private nc.ui.pub.beans.UILabel getUILabel5() {
		if (ivjUILabel5 == null) {
			try {
				ivjUILabel5 = new nc.ui.pub.beans.UILabel();
				ivjUILabel5.setName("UILabel2");
				ivjUILabel5.setText(ResHelper.getString("6013commonbasic",
						"06013commonbasic0224")/* @res "�ڼ������" */);
				ivjUILabel5.setBounds(10, 150, 80, 22);
				ivjUILabel5.setVisible(false);

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabel5;
	}

	/**
	 * ���� refPeriodItem ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	/* ���棺�˷������������ɡ� */
	private UIComboBox getTaxMode() {
		if (taxRateMode == null) {
			try {
				taxRateMode = new UIComboBox();
				String[] ml = new String[1];
				ml[0] = ResHelper.getString("6013commonbasic",
						"06013commonbasic0022")/* @res "��ͨ��˰" */;
				// ml[1] =
				// ResHelper.getString("6013commonbasic","06013commonbasic0023")/*
				// @res "���ս���˰" */;

				Integer[] mlDefault = new Integer[] { 0, 1 };/* -=notranslate=- */
				ConstEnumFactory<Integer> mPairFactory = new ConstEnumFactory<Integer>(
						ml, mlDefault);
				taxRateMode.addItems(mPairFactory.getAllConstEnums());
				taxRateMode.setBounds(100, 30, 140, 22);
				taxRateMode.addItemListener(new ItemListener() {

					@Override
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							IConstEnum constEnum = (IConstEnum) e.getItem();

							if (constEnum.getValue().equals(0)) {
								// ���¹��ʲ��ɼ�

								getUILabel4().setVisible(false);
								getcmbItem4().setVisible(false);

								getUILabel5().setVisible(false);
								getcmbItem5().setVisible(false);
								getUILabel2().setText(
										ResHelper.getString("6013commonbasic",
												"06013commonbasic0024")/*
																		 * @res
																		 * "н����Ŀ��"
																		 */);

							} else {
								getUILabel4().setVisible(true);
								getcmbItem4().setVisible(true);

								getUILabel5().setVisible(true);
								getcmbItem5().setVisible(true);

								getUILabel2().setText(
										ResHelper.getString("6013commonbasic",
												"06013commonbasic0226")/*
																		 * @res
																		 * "��Ƚ���"
																		 */);

							}
						}

					}
				});
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return taxRateMode;
	}

	/**
	 * ���� cmbItem ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UIComboBox getcmbItem() {
		if (ivjcmbItem == null) {
			try {
				ivjcmbItem = new nc.ui.pub.beans.UIComboBox();
				ivjcmbItem.setName("cmbItem");
				ivjcmbItem.setBounds(100, 70, 140, 22);
				ivjcmbItem.setTranslate(true);

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjcmbItem;
	}

	private nc.ui.pub.beans.UIComboBox getcmbItem4() {
		if (ivjcmbItem4 == null) {
			try {
				ivjcmbItem4 = new nc.ui.pub.beans.UIComboBox();
				ivjcmbItem4.setName("ivjcmbItem4");
				ivjcmbItem4.setBounds(100, 110, 140, 22);
				ivjcmbItem4.setTranslate(true);
				ivjcmbItem4.setVisible(false);

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjcmbItem4;
	}

	private nc.ui.pub.beans.UIComboBox getcmbItem5() {
		if (ivjcmbItem5 == null) {
			try {
				ivjcmbItem5 = new nc.ui.pub.beans.UIComboBox();
				ivjcmbItem5.setName("ivjcmbItem5");
				ivjcmbItem5.setBounds(100, 150, 140, 22);
				ivjcmbItem5.setTranslate(true);
				ivjcmbItem5.setVisible(false);

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjcmbItem5;
	}

	/**
	 * /** * ���������Ϸ���У�� * @param ����˵�� * @return ����ֵ * @exception �쳣���� * @see
	 * ��Ҫ�μ����������� * @since �������һ���汾���˷��������ӽ���������ѡ�� *
	 * 
	 * @deprecated�÷����������һ���汾���Ѿ������������滻������ѡ�� *-/
	 * 
	 * @return java.lang.String
	 */
	@Override
	public boolean checkPara(int dataType) {

		try {
			// �жϷǿ�
			String nullstr = "";
			if (getcmbItem().getSelectedIndex() < 0) {
				if (nullstr.length() > 0)
					nullstr += ",";
				nullstr += ResHelper.getString("common", "UC000-0003385")/*
																		 * @res
																		 * "н����Ŀ"
																		 */;
			}
			if (nullstr.length() > 0)
				throw new Exception(nullstr
						+ ResHelper.getString("6013commonbasic",
								"06013commonbasic0021")/* @res "����Ϊ�գ�" */);
			return true;

		} catch (Exception e) {
			handleException(e);
			showErrMsg(e.getMessage());
			return false;
		}
	}

	/**
	 * ���ڼ���Ŀ����ȡָ������µ�н�����ݣ���������ǰ����µ��ڼ䣬н�ʷ�����н����Ŀ��
	 * 
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String[] getPara() {
		String[] paras = new String[4];

		WaItemVO item = (WaItemVO) getcmbItem().getSelectdItemValue();

		// ��˰��ʽ
		paras[0] = getTaxMode().getSelectdItemValue().toString();

		// ��˰����
		paras[1] = item.getItemkey().toString().trim();

		if (getTaxMode().getSelectdItemValue().toString().equals("1")) {
			// ���¹���
			item = (WaItemVO) getcmbItem4().getSelectdItemValue();
			if (item != null) {
				paras[2] = item.getItemkey().toString().trim();
			} else {
				paras[2] = "null";
			}

			// �ڼ����
			item = (WaItemVO) getcmbItem5().getSelectdItemValue();
			if (item != null) {
				paras[3] = item.getItemkey().toString().trim();
			} else {
				paras[3] = "null";
			}
		} else {
			paras[2] = "null";
			paras[3] = "null";
		}

		return paras;
	}

	/**
	 * /** * ������ʾ���û��Ĳ����������� * @param ����˵�� * @return ����ֵ * @exception �쳣���� * @see
	 * ��Ҫ�μ����������� * @since �������һ���汾���˷��������ӽ���������ѡ�� *
	 * 
	 * @deprecated�÷����������һ���汾���Ѿ������������滻������ѡ�� *-/
	 * 
	 * @return java.lang.String
	 */
	// ������ʾ�õĲ�����
	@Override
	public String[] getParaStr() {
		ArrayList<String> al = new ArrayList<String>();
		// ��˰��ʽ
		al.add(getTaxMode().getSelectdItemValue().toString());

		// ��˰����
		WaItemVO item = (WaItemVO) getcmbItem().getSelectdItemValue();
		if (item instanceof WaClassItemVO) {
			al.add(getStdDes(IVariableFactory.WA_CLASSITEM_DES,
					item.getMultilangName()));
		} else {
			al.add(getStdDes(IVariableFactory.WA_ITEM_DES,
					item.getMultilangName()));
		}

		if (getTaxMode().getSelectdItemValue().toString().equals("1")) {
			// ���¹���
			item = (WaItemVO) getcmbItem4().getSelectdItemValue();
			if (item instanceof WaClassItemVO) {
				al.add(getStdDes(IVariableFactory.WA_CLASSITEM_DES,
						item.getMultilangName()));
			} else {
				al.add(getStdDes(IVariableFactory.WA_ITEM_DES,
						item.getMultilangName()));
			}

			// �ڼ����
			item = (WaItemVO) getcmbItem5().getSelectdItemValue();
			if (item != null) {
				if (item instanceof WaClassItemVO) {
					al.add(getStdDes(IVariableFactory.WA_CLASSITEM_DES,
							item.getMultilangName()));
				} else {
					al.add(getStdDes(IVariableFactory.WA_ITEM_DES,
							item.getMultilangName()));
				}
			} else {
				al.add("12");
			}
		} else {
			// ��ͨ��˰����λΪ null
			al.add("null");
			al.add("null");
		}

		return al.toArray(new String[0]);

	}

	protected String getStdDes(String tableDes, String fldDes) {
		if (StringUtils.isBlank(tableDes)) {
			return IVariableFactory.VARIABLE_LEFT_BRACKET + fldDes
					+ IVariableFactory.VARIABLE_RIGHT_BRACKET;
		}
		return IVariableFactory.VARIABLE_LEFT_BRACKET + tableDes
				+ IVariableFactory.ITEM_SEPERATOR + fldDes
				+ IVariableFactory.VARIABLE_RIGHT_BRACKET;

	}

	/**
	 * /** * ��ʼ��������ʾ * @param ����˵�� * @return ����ֵ * @exception �쳣���� * @see
	 * ��Ҫ�μ����������� * @since �������һ���汾���˷��������ӽ���������ѡ�� *
	 * 
	 * @deprecated�÷����������һ���汾���Ѿ������������滻������ѡ�� *-/
	 * 
	 */
	public void initData() {
		try {
			// ��ѯ������֯��������ֵ�͵Ĺ���н����Ŀ
			WaItemVO[] items = getItems(getContext());
			getcmbItem().addItems(items);

			getcmbItem4().addItems(items);

			getcmbItem5().addItem(null);
			getcmbItem5().addItems(items);

		} catch (Exception e) {
			handleException(e);
		}
	}

	private WaItemVO[] getItems(LoginContext context) throws BusinessException {
		if (context instanceof ClassItemContext) {
			ClassItemContext ctx = (ClassItemContext) context;
			return NCLocator
					.getInstance()
					.lookup(IClassItemQueryService.class)
					.queryItemInfoVO(ctx.getPk_org(), ctx.getPk_wa_class(),
							ctx.getWaLoginVO().getCyear(),
							ctx.getWaLoginVO().getCperiod(),
							"  wa_item.iitemtype = 0 and wa_item.itemkey='f_4' ");
		} else {
			return NCLocator
					.getInstance()
					.lookup(IItemQueryService.class)
					.queryWaItemVOsByOrg(getContext().getPk_group(),
							getContext().getPk_org(),
							" iitemtype = 0 and itemkey='f_4' ");

		}

	}

}