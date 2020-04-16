package nc.ui.pub.bill;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;

import nc.bs.logging.Logger;
import nc.md.data.access.DASFacade;
import nc.md.data.access.NCObject;
import nc.md.model.IBusinessEntity;
import nc.md.model.access.javamap.BeanStyleEnum;
import nc.mddb.constant.ElementConstant;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.BatchMatchContext;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.RefEditEvent;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.itemeditors.ComboBoxItemEditor;
import nc.ui.pub.formulaparse.FormulaParse;
import nc.vo.bill.pub.BillUtil;
import nc.vo.bill.pub.BillXMLUtil;
import nc.vo.bill.pub.MiscUtil;
import nc.vo.ml.MultiLangContext;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ExAggregatedVO;
import nc.vo.pub.ExtendedAggregatedValueObject;
import nc.vo.pub.NullFieldException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.bill.BillStructVO;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTableVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletHeadVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.MultiLangText;

/**
 * 单据模板数据控制类. 创建日期:(01-2-23 14:20:11)
 * 
 * @author:宋杰
 */
@SuppressWarnings("serial")
public class BillData implements java.io.Serializable, IBillItem {
	public static final String DEFAULT_BODY_TABLECODE = ExAggregatedVO.defaultTableCode;

	public final String DEFAULT_BODY_TABLENAME = BillUtil.getDefaultTableName(BODY);
	// ExAggregatedVO.defaultTableName;

	public static final String DEFAULT_HEAD_TABBEDCODE = "main";

	public final String DEFAULT_HEAD_TABBEDNAME = BillUtil.getDefaultTableName(HEAD);
	// nc.ui.ml.NCLangRes
	// .getInstance().getStrByID("_Bill", "UPP_Bill-000001")/* @res "主表" */;

	public static final String DEFAULT_TAIL_TABBEDCODE = "tail";

	public final String DEFAULT_TAIL_TABBEDNAME = BillUtil.getDefaultTableName(TAIL);

	// nc.ui.ml.NCLangRes
	// .getInstance().getStrByID("_Bill", "UPP_Bill-000001")/* @res "主表" */;

	protected BillItem[] m_biHeadItems = null; // 表头元素数组

	protected BillItem[] m_biTailItems = null; // 表尾元素数组

	// protected BillItem[] m_biBodyItems = null; //表体元素数组

	// protected BillModel m_bmBillModel = null; //表体数据控制模型
	protected Hashtable<String, BillModel> hBillModels = new Hashtable<String, BillModel>(); // tableCode
	// +
	// 表体数据控制模型

	protected Hashtable<String, BillItem> hHeadItems = new Hashtable<String, BillItem>(); // itemKey
	// +
	// BillItem

	// protected Hashtable hBodyItems = new Hashtable();
	// tableCode + Hashtable(itemKey + BillItem)
	protected Hashtable<String, BillItem> hTailItems = new Hashtable<String, BillItem>();

	// tablecode + BillItem[]
	// protected Hashtable hBodyItemArray = new Hashtable();

	// (pos+tablecode,BillTabVO)
	protected HashtableBillTabVO hBillTabs = new HashtableBillTabVO();

	private BillTableVO[] billTableVos = null;

	// 单据类型
	protected String m_strBillType = null;

	// 单据标题
	protected String m_strTitle = null;

	// 单据编辑状态
	protected boolean m_bEnabled = true;

	// 公式执行器
	protected FormulaParse m_formulaParse;

	// cardStyle
	private String cardStyle = null;

	// 调试
	// private boolean debug = false;

	// design pattern
	// private boolean design = false;
	// BillCardPanel for formula
	private BillCardPanel billCardPanel = null;

	BillTempletVO billTempletVO = null;

	private int billstatus = VOStatus.UNCHANGED;

	// 是否在导入状态
	private boolean isImporting = false;

	/**
	 * BillData 构造子注解.
	 */
	public BillData() {
		super();
	}

	/**
	 * BillData 构造子注解.
	 */
	public BillData(BillTempletVO newTempletVO) {
		super();
		initTempletData(newTempletVO, null);
	}

	/**
	 * BillData 构造子注解.
	 */
	public BillData(BillTempletVO newTempletVO, HashMap<String, BillItemUISet> sets) {
		super();
		initTempletData(newTempletVO, sets);
	}

	/**
	 * 创建日期:(2002-09-18 13:30:14)
	 * 
	 * @param pos
	 *            int
	 * @param tablecode
	 *            java.lang.String
	 * @param item
	 *            nc.ui.pub.bill1.BillItem
	 */
	public void addBillItem(int pos, String tablecode, BillItem item) {
		// BillItem[] oldItems = null;
		addToHashtable(pos, item);
		if (pos == BODY) { // 对于表体数据,同时添加至单据项数组
			BillModel model = getBillModel(tablecode);
			if (model == null)
				model = addBillModel(tablecode);
			BillItem[] items = model.getBodyItems();
			model.setBodyItems((BillItem[]) appendItemToArray(items, item));
		} else {
			BillItem[] items = pos == HEAD ? m_biHeadItems : m_biTailItems;
			items = (BillItem[]) appendItemToArray(items, item);
			BillUtil.sortBillItemsByShowOrder(items);
			if (pos == HEAD) {
				m_biHeadItems = items;
			} else {
				m_biTailItems = items;
			}
		}
		// accordItems(pos, false);
	}

	/**
	 * 数组追加对象 创建日期:(2003-6-23 9:24:52)
	 * 
	 * @param os
	 *            origin object array
	 * @param o
	 *            the appended object
	 * @return the new object array contains the inserted object
	 */
	private Object[] appendItemToArray(Object[] os, Object o) {
		if (o == null)
			return os;
		Object[] os1 = (Object[]) java.lang.reflect.Array.newInstance(o.getClass(), os != null ? os.length + 1 : 1);
		if (os != null)
			System.arraycopy(os, 0, os1, 0, os.length);
		os1[os1.length - 1] = o;
		return os1;
	}

	/**
	 * 创建日期:(2003-6-30 14:46:50)
	 */
	public void addBodyTabVO(BillTabVO billTabVO) {
		if (billTabVO == null)
			return;
		hBillTabs.put(billTabVO.getPos() + billTabVO.getTabcode(), billTabVO);
	}

	/**
	 * 增加单据行. 创建日期:(01-2-23 15:03:07)
	 */
	public void addLine() {
		BillModel bm = getBillModel();
		if (bm != null)
			bm.addLine();
	}

	/**
	 * 增加单据行. 创建日期:(01-2-23 15:03:07)
	 */
	public void addLine(String tableCode) {
		BillModel bm = getBillModel(tableCode);
		if (bm != null)
			bm.addLine();
	}

	/**
	 * 创建新单据. 创建日期:(01-2-23 15:03:07)
	 */
	public void addNew() {
		// 清除数据
		clearViewData();

		setBillstatus(VOStatus.NEW);

		setHeadTailItemDefaultValue();
	}

	/**
	 * 设置单据表头表尾默认值. 创建日期:(08-7-9 15:03:07)
	 */
	private void setHeadTailItemDefaultValue() {
		BillItem[] items = getHeadTailItems();
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				BillItem item = items[i];
				Object value = item.getDefaultValueObject();
				if (value != null)
					item.setValue(value);

			}
		}
	}

	/**
	 * 将BillItem存入Hashtable,同时刷新 表编码和名称的对照表. 创建日期:(2002-09-09 10:34:24)
	 * 
	 * @param pos
	 *            int
	 * @param tableCode
	 *            java.lang.String
	 * @param item
	 *            nc.ui.pub.bill1.BillItem[] pos and tablecode must be the same
	 */
	private void addToHashtable(BillItem[] items) {
		if (items == null || items.length == 0)
			return;

		BillItem item = items[0];
		int pos = item.getPos();
		Hashtable<String, BillItem> hashItems = null;
		if (pos == HEAD)
			hashItems = hHeadItems;
		else if (pos == TAIL)
			hashItems = hTailItems;
		if (hashItems != null) {
			for (int i = 0; i < items.length; i++)
				hashItems.put(items[i].getKey(), items[i]);
		}

		// 对于新的页签,增加编码和名称的对照
		if (!hBillTabs.containsKey(pos + item.getTableCode())) {
			String tablename = item.getTableName();
			if (tablename == null)
				tablename = item.getTableCode();
			hBillTabs.add(pos, item.getTableCode(), tablename);
		}
		if (pos == BODY) {
			BillModel model = getBillModel(item.getTableCode());
			if (model == null)
				model = addBillModel(item.getTableCode());
			model.setBodyItems(items);
		} else {
			if (pos == HEAD)
				m_biHeadItems = (BillItem[]) MiscUtil.ArraysCat(m_biHeadItems, items);
			else if (pos == TAIL)
				m_biTailItems = (BillItem[]) MiscUtil.ArraysCat(m_biTailItems, items);
		}
	}

	/**
	 * 将BillItem存入Hashtable,同时刷新 表编码和名称的对照表. 创建日期:(2002-09-09 10:34:24)
	 * 
	 * @param pos
	 *            int
	 * @param tableCode
	 *            java.lang.String
	 * @param item
	 *            nc.ui.pub.bill1.BillItem
	 */
	protected void addToHashtable(int pos, BillItem item) {
		item.setPos(pos);
		String code;
		if ((code = item.getTableCode()) == null || code.trim().length() == 0) {
			item.setTableCode(BillUtil.getDefaultTableCode(pos));
			item.setTableName(BillUtil.getDefaultTableName(pos));
		}
		java.util.Hashtable<String, BillItem> hashItems = null;
		if (pos == HEAD)
			hashItems = hHeadItems;
		else if (pos == TAIL)
			hashItems = hTailItems;
		if (hashItems != null) {
			hashItems.put(item.getKey(), item);
		}

		// 对于新的页签,增加编码和名称的对照
		if (!hBillTabs.containsKey(pos + item.getTableCode())) {
			String tablename = item.getTableName();
			if (tablename == null)
				tablename = item.getTableCode();
			hBillTabs.add(pos, item.getTableCode(), tablename);
		}

		// switch (pos) {
		// case HEAD :
		// hHeadItems.put(item.getKey(), item);
		// break;
		// case TAIL :
		// hTailItems.put(item.getKey(), item);
		// break;
		// case BODY :
		// {
		// Hashtable table = null;
		// if (hBodyItems.containsKey(item.getTableCode())) {
		// table = (Hashtable) hBodyItems.get(item.getTableCode());
		// } else {
		// table = new Hashtable();
		// }
		// table.put(item.getKey(), item);
		// hBodyItems.put(item.getTableCode(), table);
		// break;
		// }

		// }
	}

	/**
	 * 清除指定行指定列数据. 创建日期:(2001-9-14 10:18:13)
	 * 
	 * @param row
	 *            int
	 * @param keys
	 *            java.lang.String[]
	 */
	public void changeTableName(int pos, String tablecode, String newTableName) {
		BillTabVO btvo = hBillTabs.getTabVO(pos, tablecode);
		if (btvo == null)
			return;
		btvo.setTabname(newTableName);
		BillItem[] items = getBillItemsByPosAndTableCode(pos, tablecode);
		if (items != null) {
			for (int i = 0; i < items.length; i++)
				items[i].setTableName(newTableName);
		}
	}

	/**
	 * 清除指定行指定列数据. 创建日期:(2001-9-14 10:18:13)
	 * 
	 * @param row
	 *            int
	 * @param keys
	 *            java.lang.String[]
	 */
	public void clearRowData(int row, String[] keys) {
		BillModel bm = getBillModel();
		if (bm != null)
			bm.clearRowData(row, keys);
	}

	/**
	 * 清除指定行指定列数据. 创建日期:(2001-9-14 10:18:13)
	 * 
	 * @param row
	 *            int
	 * @param keys
	 *            java.lang.String[]
	 */
	public void clearRowData(String tableCode, int row, String[] keys) {
		BillModel bm = getBillModel(tableCode);
		if (bm != null)
			bm.clearRowData(row, keys);
	}

	/***
	 * 清空表头数据
	 */
	public void clearHead() {
		BillItem[] headtailitems = getHeadTailItems();
		if (headtailitems != null) {
			for (int i = 0; i < headtailitems.length; i++) {
				headtailitems[i].clearViewData();
			}
		}
	}

	/**
	 * 清空表体数据
	 */
	public void clearBody() {
		Enumeration<String> keys = hBillModels.keys();
		while (keys.hasMoreElements()) {
			hBillModels.get(keys.nextElement()).clearBodyData();
		}
	}

	/**
	 * 清除单据结构数据.
	 */
	public void clearViewData() {
		// 表头表尾
		clearHead();
		// 表体
		// m_bmBillModel.clearBodyData();
		clearBody();
	}

	// /**
	// * 创建日期:(2003-6-30 14:46:50)
	// */
	// private BillTabVO createBillTabVO(int pos, String tableCode) {
	// return BillUtil.createBillTabVO(pos, tableCode);
	// }
	/**
	 * 创建日期:(01-2-23 15:29:06)
	 * 
	 * @return ufbill.BillModel
	 */
	protected BillModel createDefaultBillMode(BillTabVO tabvo) {
		BillModel billmodel = new BillModel();

		billmodel.setTabvo(tabvo);

		return billmodel;
	}

	/**
	 * 公式执行器. 创建日期:(01-2-21 10:08:48)
	 */
	protected FormulaParse createDefaultFormulaParse() {
		return new FormulaParse();// FormulaParse();
	}

	/**
	 * 数据非空监测 创建日期:(01-2-21 10:08:48)
	 */
	public void dataNotNullValidate() throws ValidationException {
		dataNotNullValidate(null);
	}

	/**
	 * 数据非空监测 hashFilter (billitem,int[rowindex]) 创建日期:(01-2-21 10:08:48)
	 */
	public void dataNotNullValidate(Hashtable<BillItem, int[]> hashFilter) throws ValidationException {
		StringBuffer message = null;
		BillItem[] headtailitems = getHeadTailItems();

		if (headtailitems != null) {

			clearHeadTailItemShowWarning();
			getBillCardPanel().getHeadTabbedPane().clearShowWarning();
			boolean isFocus = false;

			for (int i = 0; i < headtailitems.length; i++) {

				if (headtailitems[i].isNull())
					if (isNULL(headtailitems[i].getValueObject())) {
						BillTabVO tabVO = getTabVO(headtailitems[i].getPos(), headtailitems[i].getTableCode());
						if (tabVO.getBasetab() == null) {
							getBillCardPanel().getHeadTabbedPane().showWarning(headtailitems[i].getTableCode());
						} else {
							getBillCardPanel().getHeadTabbedPane().showWarning(tabVO.getBasetab());
						}
						if (!isFocus) {
							headtailitems[i].getComponent().requestFocus();
							isFocus = true;
						}
						if (message == null)
							message = new StringBuffer();
						String comma = nc.ui.ml.NCLangRes.getInstance().getString("_bill", ",", "0_bill0034");// 逗号
						message.append("[");
						message.append(headtailitems[i].getName());
						message.append("]");
						message.append(comma);

						headtailitems[i].showWaring();

					}
			}
		}
		if (message != null) {
			message.deleteCharAt(message.length() - 1);
			throw new NullFieldException(message.toString());
		}

		// 增加多子表的循环
		String[] tabCodes = getTableCodes(BODY);
		if (tabCodes != null) {
			clearBodyShowWarning();
			boolean isFocus = false;
			for (int t = 0; t < tabCodes.length; t++) {
				String tabCode = tabCodes[t];
				for (int i = 0; i < getBillModel(tabCode).getRowCount(); i++) {
					StringBuffer rowmessage = new StringBuffer();

					rowmessage.append(" ");
					if (tabCodes.length > 1) {
						rowmessage.append(getTableName(BODY, tabCode));
						rowmessage.append("(");
						// "页签"
						rowmessage.append(nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill", "UPP_Bill-000003"));
						rowmessage.append(") ");
					}
					// rowmessage.append(i + 1);
					// rowmessage.append("(");
					// //"行"
					// rowmessage.append(nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill","UPP_Bill-000002"));
					// rowmessage.append(") ");

					String rowText = NCLangRes.getInstance().getString("_Bill", null, "UPP_Bill-000547", null,
							new String[] { String.valueOf(i + 1) })/*
																	 * @res"第{0}行:"
																	 */;
					rowmessage.append(rowText);
					// rowmessage.append(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					// "_Bill", "UPP_Bill-000547"));//"第"
					// rowmessage.append(i + 1);
					// rowmessage.append(nc.ui.ml.NCLangRes.getInstance().getStrByID(
					// "_Bill", "UPP_Bill-000002")); //"行"
					// rowmessage.append("：");

					StringBuffer errormessage = null;
					BillItem[] items = getBodyItemsForTable(tabCode);
					OUTER: for (int j = 0; j < items.length; j++) {
						BillItem item = items[j];
						int[] rows = hashFilter != null ? hashFilter.get(item) : null;
						// ssx modified on 2019-10-04
						// 引用属性的关联属性是否为空不校验（不明白保存A的时候，为什么要校验引用的B.C是否为空）
						if (!item.getKey().contains(".") && item.isNull()) {
							// end
							if (rows != null) {
								for (int ii = 0; ii < rows.length; ii++) {
									if (rows[ii] == i)
										continue OUTER;
								}
							}
							Object aValue = getBillModel(tabCode).getValueAt(i, item.getKey());
							if (isNULL(aValue)) {
								if (!isFocus) {
									getBillCardPanel().getBillTable(tabCode).requestFocus();
									getBillCardPanel().getBillTable(tabCode).editCellAt(i,
											getBillModel(tabCode).getColumnIndex(item));
									isFocus = true;
								}
								// if(message == null)
								// message = new StringBuffer(rowmessage);
								if (errormessage == null)
									errormessage = new StringBuffer();
								// 顿号
								String dunhao = nc.ui.ml.NCLangRes.getInstance().getString("_bill", "、",/*
																										 * -=
																										 * notranslate
																										 * =
																										 * -
																										 */
								"0_bill0035");/* -=notranslate=- */
								errormessage.append("[");
								errormessage.append(item.getName());
								errormessage.append("]");
								errormessage.append(dunhao);

								getBillModel(tabCode).cellShowWarning(i, item.getKey());
							}
						}
					}
					if (errormessage != null) {

						errormessage.deleteCharAt(errormessage.length() - 1);
						rowmessage.append(errormessage);
						// rowmessage.append("\n");
						if (message == null)
							message = new StringBuffer(rowmessage);
						else
							message.append(rowmessage);
						// break;
					}
				}
				if (message != null) {
					getBillCardPanel().getBodyTabbedPane().showWarning(tabCode);
					break;
				}
			}
		}
		if (message != null) {
			throw new NullFieldException(message.toString());
		}
	}

	public void dataNotNullValidateOfBody(String tableCode, CircularlyAccessibleValueObject[] vos)
			throws ValidationException {

		if (tableCode == null || vos == null)
			return;

		BillItem[] items = getBodyItemsForTable(tableCode);
		if (items == null || items.length == 0)
			return;
		for (int i = 0; i < vos.length; i++) {
			for (int j = 0; j < items.length; j++) {
				if (items[j].isNull()) {
					Object o = vos[i].getAttributeValue(items[j].getKey());
					if (isNULL(o)) {
						String colon = nc.ui.ml.NCLangRes.getInstance().getString("_bill", ":", "0_bill0036");/*
																											 * ：
																											 */
						String message = nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill", "UPP_Bill-000547")/*
																												 * @
																												 * res
																												 * "第"
																												 */
								+ (i + 1) + nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill", "UPP_Bill-000002")/*
																													 * @
																													 * res
																													 * "行"
																													 */
								+ colon + items[j].getName();
						throw new NullFieldException(message);
					}
				}

			}
		}
	}

	/**
	 * 执行表体公式 创建日期:(01-4-26 15:29:06)
	 */
	public void execBodyFormula(int row, String strkey) {
		BillItem item = getBodyItem(strkey);
		String[] formulas = item.getEditFormulas();
		execBodyFormulas(row, formulas);
	}

	/**
	 * 执行表体公式 创建日期:(01-4-26 15:29:06)
	 */
	public void execBodyFormulas(int row, String[] formulas) {
		BillModel bm = getBillModel();
		if (bm != null)
			bm.execFormulas(row, formulas);
	}

	/**
	 * 执行公式,将结果直接赋给VO,不显示到界面! 创建日期:(2003-3-3 10:21:03)
	 * 
	 * @param vos
	 *            nc.vo.pub.CircularlyAccessibleValueObject[]
	 * @param formulas
	 *            java.lang.String[]
	 */
	public void execFormulaWithVOs(nc.vo.pub.CircularlyAccessibleValueObject[] VOs, String[] formulas, boolean isHeader) {
		if (isHeader) {
			BillItemContext bc = new BillItemContext();
			bc.setItems(getHeadTailItems());
			// bc.setBd(this);
			BillFormulaContext bfc = new BillFormulaContext(BillFormulaContext.FORMULATYPE_VO);
			bfc.setVOs(VOs);
			BillFormulaUtil.execFormulas(getBillCardPanel(), bfc, bc, formulas);
		} else {
			BillModel bm = getBillModel();
			if (bm != null)
				bm.execFormulasWithVO(VOs, formulas);
		}

		// return results;
		// BillUtil.execFormulaWithVOs(VOs, formulas, isHeader ?
		// getHeadTailItems() : getBodyItems());
	}

	/**
	 * 执行表头公式 创建日期:(01-4-26 15:29:06)
	 */
	public Object execHeadFormula(String formula) {
		return execHeadTailFormula(formula);
	}

	/**
	 * 执行表头公式数组 创建日期:(01-4-26 15:29:06)
	 */
	public void execHeadFormulas(String[] formulas) {
		execHeadTailFormulas(formulas);
	}

	/**
	 * 执行表头公式 创建日期:(01-4-26 15:29:06)
	 */
	private Object execHeadTailFormula(String formula) {
		String[][] results = (String[][]) execHeadTailFormulas(new String[] { formula });
		if (results == null)
			return null;
		return results[0][0];
	}

	public void loadLoadHeadRelation() {
		loadLoadHeadRelation(false);
	}

	private void loadLoadHeadRelation(boolean isEditing) {
		BillItem[] items = getHeadTailItems();
		// 改为批量取数

		// if (items == null) {
		// return;
		// }
		// for (int i = 0; i < items.length; i++) {
		// loadHeadRelation(items[i].getKey(), isEditing);
		// }

		loadHeadRelation(items, isEditing);

	}

	public void loadEditHeadRelation(String itemkey) {
		loadHeadRelation(itemkey, true);
	}

	protected void loadHeadRelation(String itemkey, boolean isediting) {
		BillItem item = getHeadTailItem(itemkey);

		if (item.getDataType() == IBillItem.UFREF && item.getMetaDataProperty() != null) {

			ArrayList<IConstEnum> relationitem = getMetaDataRelationItems(item, isediting);

			if (relationitem != null) {
				String id = (String) item.getValueObject();
				if (id != null) {
					// IConstEnum Value Object[]:,Key:itemkey
					// IConstEnum Value Object[] 对应 传入id 的数据长度
					IConstEnum[] o = item.getGetBillRelationItemValue().getRelationItemValue(relationitem,
							new String[] { id });
					if (o != null) {
						for (int i = 0; i < o.length; i++) {
							if (o[i].getValue() != null) {
								Object[] v = (Object[]) o[i].getValue();
								getHeadTailItem(o[i].getName()).setValue(v[0]);
								if (isediting) {
									JComponent com = getHeadTailItem(o[i].getName()).getItemEditor().getComponent();
									if (com instanceof UIRefPane) {
										UIRefPane ref = (UIRefPane) com;
										ref.stateChanged(null);
									}

								}
							}
						}
					}
				} else {
					for (int i = 0; i < relationitem.size(); i++) {
						getHeadTailItem(relationitem.get(i).getName()).setValue(null);
					}
				}
			}

		}

	}

	// 批量加载关联项
	public void loadHeadRelation(BillItem[] billitems, boolean isediting) {

		if (billitems == null || billitems.length == 0) {
			return;
		}

		// 关联项
		Map<BillItem, ArrayList<IConstEnum>> iesm = new HashMap<BillItem, ArrayList<IConstEnum>>();

		for (int i = 0; i < billitems.length; i++) {

			BillItem item = billitems[i];

			if (item.getDataType() == IBillItem.UFREF && item.getMetaDataProperty() != null) {

				ArrayList<IConstEnum> relationitem = getMetaDataRelationItems(item, isediting);
				if (relationitem != null) {
					iesm.put(item, relationitem);
				}

			}

		}

		if (iesm.size() == 0) {
			return;
		}

		Map<BillItem, IConstEnum[]> dataMap = BillUtil.getRelationItemValue(billitems, iesm);

		for (int i = 0; i < billitems.length; i++) {

			if (dataMap.get(billitems[i]) != null) {

				// IConstEnum Value Object[]:,Key:itemkey
				// IConstEnum Value Object[] 对应 传入id 的数据长度
				IConstEnum[] o = dataMap.get(billitems[i]);
				if (o != null) {
					for (int j = 0; j < o.length; j++) {
						if (o[j] != null && o[j].getValue() != null) {
							Object[] v = (Object[]) o[j].getValue();
							if (getHeadTailItem(o[j].getName()) != null) {
								// 如果单据模板上的某个Billitem已经删除，但这个字段还在编辑关联项的引用中，会抛空指针。
								getHeadTailItem(o[j].getName()).setValue(v[0]);
							}
						}
					}
				}

			} else {
				// 没有取到值，赋值为NUll，保持以前的逻辑
				ArrayList<IConstEnum> relationitem = getMetaDataRelationItems(billitems[i], isediting);
				if (relationitem != null) {
					for (int j = 0; j < relationitem.size(); j++) {
						getHeadTailItem(relationitem.get(j).getName()).setValue(null);
					}
				}

			}

		}

	}

	/*
	 * IConstEnum Value metadatapath:,Name:itemkey
	 */
	protected ArrayList<IConstEnum> getMetaDataRelationItems(BillItem item, boolean isediting) {

		if (item.getDataType() != IBillItem.UFREF)
			return null;

		if (item.getMetaDataProperty() == null)
			return null;

		ArrayList<IConstEnum> ics = new ArrayList<IConstEnum>();

		// 获得ITEM编辑关联设置默认值属性
		// itemkey,访问路径:AAAA=B.A,BBBB=B.B
		// 编辑关联项
		if (item.getMetaDataRelation() != null && isediting) {
			IConstEnum[] ies = BillUtil.getConstEnumByString(item.getMetaDataRelation());

			for (int i = 0; i < ies.length; i++) {
				ics.add(ies[i]);
			}
		}

		// 获得ITEM关联属性
		if (item.getRelationItem() != null) {
			for (int i = 0; i < item.getRelationItem().size(); i++) {
				BillItem ritem = item.getRelationItem().get(i);

				IConstEnum ic = new DefaultConstEnum(ritem.getMetaDataAccessPath(), ritem.getKey());
				ics.add(ic);
			}
		}

		if (ics.size() == 0)
			ics = null;

		return ics;

	}

	/**
	 * 执行表头公式数组 创建日期:(01-4-26 15:29:06)
	 */
	private Object execHeadTailFormulas(String[] formulas) {
		BillItemContext bc = new BillItemContext();
		bc.setItems(getHeadTailItems());
		BillFormulaContext bfc = new BillFormulaContext(BillFormulaContext.FORMULATYPE_OTHER);
		String[][] results = BillFormulaUtil.execFormulas(getBillCardPanel(), bfc, bc, formulas);
		return results;
	}

	void execHeadTailFormuls(BillItem item, String type) {
		if (item == null)
			return;
		String[] formulas = IBillItem.LOAD.equals(type) ? item.getLoadFormula() : item.getEditFormulas();
		if (formulas == null || formulas.length == 0)
			return;
		BillItemContext bc = new BillItemContext();
		bc.setItems(getHeadTailItems());
		bc.setItem(item);
		BillFormulaContext bfc = new BillFormulaContext(BillFormulaContext.FORMULATYPE_OTHER);
		BillFormulaUtil.execFormulas(getBillCardPanel(), bfc, bc, formulas);
	}

	/**
	 * 执行表头公式 创建日期:(01-4-26 15:29:06)
	 */
	public Object execTailFormula(String formula) {
		return execHeadTailFormula(formula);
	}

	/**
	 * 执行表头公式数组 创建日期:(01-4-26 15:29:06)
	 */
	public void execTailFormulas(String[] formulas) {
		execHeadTailFormulas(formulas);
	}

	/**
	 * 得到表头可显示元素数组.
	 */
	BillItem[] filterItemsByTableCode(BillItem[] items, String tableCode) {
		if (items == null || items.length == 0)
			return null;
		ArrayList<BillItem> list = new ArrayList<BillItem>();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getTableCode() != null && items[i].getTableCode().equals(tableCode))
				list.add(items[i]);
		}
		if (list.size() > 0) {
			return list.toArray(new BillItem[list.size()]);
		}
		return null;
	}

	/**
	 * getHeadTableCodes or getTableCodes. 创建日期:(2002-12-5 11:10:59)
	 */
	public BillTabVO[] getAllTabVos() {
		return hBillTabs.getAllTabVos();
	}

	/**
	 * 得到表体元素数组. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public BillItem[] getBillItemsByPos(int pos) {
		if (pos == HEAD)
			return getHeadItems();
		else if (pos == BODY) {
			return getBodyAllItemss();
		} else if (pos == TAIL) {
			return getTailItems();
		}
		return null;
	}

	/**
	 * 得到表体元素数组. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public BillItem[] getBillItemsByPosAndTableCode(int pos, String tableCode) {
		switch (pos) {
		case HEAD:
			return getHeadItems(tableCode);
		case BODY:
			return getBodyItemsForTable(tableCode);
		case TAIL:
			return getTailItems(tableCode);
		}
		return null;
	}

	/**
	 * 得到表体表模式. 创建日期:(01-2-23 15:23:11)
	 * 
	 * @return ufbill.BillModel
	 */
	public BillModel getBillModel() {
		BillModel model = getBillModel(getDefaultBodyTableCode());
		// if (hBillModels != null && model == null) {
		// Iterator it = hBillModels.values().iterator();
		// while (it.hasNext()) {
		// return (BillModel) it.next();
		// }
		// }
		return model;
	}

	/**
	 * 得到表体表模式. 创建日期:(01-2-23 15:23:11)
	 * 
	 * @return ufbill.BillModel
	 */
	public BillModel getBillModel(String tableCode) {
		if (hBillModels == null)
			hBillModels = new Hashtable<String, BillModel>();
		if (!hBillModels.containsKey(tableCode)) {
			BillModel billModel = null;
			BillTabVO btvo = hBillTabs.get(BODY + tableCode);
			String baseTab = null;
			if (btvo != null) {
				baseTab = btvo.getBasetab();
			}
			if (baseTab != null) {
				// if (!hBillModels.containsKey(baseTab)) {
				// billModel = createDefaultBillMode();
				// billModel.setFormulaParse(getFormulaParse());
				// hBillModels.put(baseTab, billModel);
				// } else
				billModel = hBillModels.get(baseTab);
			}
			// else {
			// billModel = createDefaultBillMode();
			// billModel.setFormulaParse(getFormulaParse());
			// hBillModels.put(tableCode, billModel);
			// }
			return billModel;
		}
		return hBillModels.get(tableCode);
	}

	/**
	 * 创建日期:(2004-1-5 10:00:46)
	 * 
	 * @return nc.vo.pub.bill.BillTableVO[]
	 */
	public nc.vo.pub.bill.BillTableVO[] getBillTableVos() {
		return billTableVos;
	}

	/**
	 * 创建日期:(2003-6-30 14:46:50)
	 */
	public BillTabVO[] getBillTabVOs(int pos) {
		return hBillTabs.getTabVos(pos);
	}

	/**
	 * 创建日期:(2003-6-30 14:46:50)
	 */
	public BillTabVO[] getBillTabVOsByPosition(Integer position) {
		if (position == null)
			return null;
		String style = getCardStyle();
		if (style == null || style.trim().equals(BillStructVO.DEFAULT_CARD)) {
			if (position.intValue() == IBillItem.HEAD)
				return hBillTabs.getTabVos(HEAD);
			else if (position.intValue() == IBillItem.BODY)
				return hBillTabs.getTabVos(BODY);
			else if (position.intValue() == IBillItem.TAIL)
				return hBillTabs.getTabVos(TAIL);
			return null;
		}
		return hBillTabs.getTabVos(position);
	}

	/**
	 * 创建日期:(2003-6-30 14:46:50)
	 */
	public BillTabVO[] getBillBaseTabVOsByPosition(Integer position) {
		if (position == null)
			return null;
		String style = getCardStyle();
		if (style == null || style.trim().equals(BillStructVO.DEFAULT_CARD)) {
			if (position.intValue() == IBillItem.HEAD)
				return hBillTabs.getBaseTabVos(HEAD);
			else if (position.intValue() == IBillItem.BODY)
				return hBillTabs.getBaseTabVos(BODY);
			else if (position.intValue() == IBillItem.TAIL)
				return hBillTabs.getBaseTabVos(TAIL);
			return null;
		}
		return hBillTabs.getBaseTabVos(position);
	}

	/**
	 * 由类名获得单据数据 创建日期:(01-2-26 11:28:04)
	 */
	public AggregatedValueObject getBillValueChangeVO(String billVOName, String headVOName, String bodyVOName) {
		try {
			AggregatedValueObject billVO = (AggregatedValueObject) Class.forName(billVOName).newInstance();
			billVO.setParentVO(getHeaderValueVO(headVOName));
			if (hasBody())
				billVO.setChildrenVO(getBodyValueChangeVOs(bodyVOName));
			return billVO;
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage());
		} catch (InstantiationException e) {
			Logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			Logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 由类名获得单据数据 创建日期:(01-2-26 11:28:04)
	 */
	public ExtendedAggregatedValueObject getBillValueChangeVOExtended(String exBillVOName, String headVOName,
			String[] bodyVOName) {
		try {
			ExtendedAggregatedValueObject exBillVO = (ExtendedAggregatedValueObject) Class.forName(exBillVOName)
					.newInstance();
			exBillVO.setParentVO(getHeaderValueVO(headVOName));
			String[] codes = exBillVO.getTableCodes();
			if (codes != null) {
				for (int i = 0; i < codes.length; i++) {
					exBillVO.setTableVO(codes[i], getBodyValueChangeVOs(codes[i], bodyVOName[i]));
				}
			}
			return exBillVO;
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage());
		} catch (InstantiationException e) {
			Logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			Logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 由类名获得单据数据 创建日期:(01-2-26 11:28:04)
	 */
	public AggregatedValueObject getBillValueVO(String billVOName, String headVOName, String bodyVOName) {
		try {
			AggregatedValueObject billVO = (AggregatedValueObject) Class.forName(billVOName).newInstance();
			billVO.setParentVO(getHeaderValueVO(headVOName));
			billVO.setChildrenVO(getBodyValueVOs(bodyVOName));
			return billVO;
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage());
		} catch (InstantiationException e) {
			Logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			Logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 由已事例化VO获得单据数据 创建日期:(01-2-26 11:28:04)
	 */
	public void getBillValueVO(AggregatedValueObject billVO) {
		CircularlyAccessibleValueObject headVO = billVO.getParentVO();
		getHeaderValueVO(headVO);
		CircularlyAccessibleValueObject[] bodyVOs = billVO.getChildrenVO();
		getBodyValueVOs(bodyVOs);
	}

	/**
	 * 由类名获得单据数据 创建日期:(01-2-26 11:28:04)
	 */
	public ExtendedAggregatedValueObject getBillValueVOExtended(String exBillVOName, String headVOName,
			String[] bodyVONames) {
		try {
			ExtendedAggregatedValueObject exBillVO = (ExtendedAggregatedValueObject) Class.forName(exBillVOName)
					.newInstance();
			exBillVO.setParentVO(getHeaderValueVO(headVOName));
			String[] tCodes = exBillVO.getTableCodes();
			if (tCodes != null)
				for (int i = 0; i < tCodes.length; i++) {
					exBillVO.setTableVO(tCodes[i], getBodyValueVOs(tCodes[i], bodyVONames[i]));
				}
			return exBillVO;
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage());
		} catch (InstantiationException e) {
			Logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			Logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 由已事例化VO获得单据数据 创建日期:(01-2-26 11:28:04)
	 */
	public void getBillValueVOExtended(ExtendedAggregatedValueObject exBillVO) {
		CircularlyAccessibleValueObject headVO = exBillVO.getParentVO();
		getHeaderValueVO(headVO);
		String[] codes = getBodyTableCodes();
		if (codes != null) {
			for (int i = 0; i < codes.length; i++) {
				CircularlyAccessibleValueObject[] bodyVOs = exBillVO.getTableVO(codes[i]);
				if (bodyVOs != null)
					getBodyValueVOs(codes[i], bodyVOs);
			}
		}
	}

	/**
	 * 得到表体元素数组. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	BillItem[] getBodyAllItemss() {
		String[] tableCodes = getBodyTableCodes();
		if (tableCodes != null) {
			ArrayList<BillItem> list = new ArrayList<BillItem>();
			BillItem[] items;
			for (int i = 0; i < tableCodes.length; i++) {
				items = getBodyItemsForTable(tableCodes[i]);
				if (items != null) {
					for (int ii = 0; ii < items.length; ii++)
						list.add(items[ii]);
				}
			}
			if (list.size() > 0) {
				items = list.toArray(new BillItem[list.size()]);
				return items;
			}
		}
		return null;
	}

	/**
	 * 创建日期:(2003-6-27 9:51:42)
	 */
	public String getBodyBaseTableCode(String shareTableCode) {
		return hBillTabs.getBodyBaseTableCode(shareTableCode);
	}

	/**
	 * 创建日期:(2003-6-27 9:51:42)
	 */
	public String[] getBodyBaseTableCodes() {
		return hBillTabs.getBodyBaseTableCodes();
	}

	/**
	 * 得到表体关键字对应元素. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public BillItem getBodyItem(String strKey) {
		return getBodyItem(getDefaultBodyTableCode(), strKey);
	}

	/**
	 * 得到表体关键字对应元素. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public BillItem getBodyItem(String tableCode, String strKey) {
		tableCode = getBodyBaseTableCode(tableCode);
		if (hBillModels.containsKey(tableCode)) {
			return getBillModel(tableCode).getItemByKey(strKey);
		} else
			return null;
	}

	/**
	 * 得到表体元素数组. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public BillItem[] getBodyItems() {
		// return m_biBodyItems;
		return getBodyItemsForTable(getDefaultBodyTableCode());
	}

	/**
	 * 得到表体元素数组. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public BillItem[] getBodyItemsForTable(String tableCode) {
		if (BillUtil.validateTableCode(tableCode) != null)
			return null;
		String baseCode = tableCode;
		BillTabVO bvo = hBillTabs.getTabVO(BODY, tableCode);
		if (bvo != null && bvo.getBasetab() != null) {
			baseCode = bvo.getBasetab();
		}
		if (hBillModels != null && hBillModels.containsKey(baseCode)) {
			// BillItem[] items = (BillItem[]) hBodyItemArray.get(baseCode);
			// BillUtil.sortBillItemsByShowOrder(items);
			// return getBillModel(baseCode).getBodyItems();
			BillItem[] items = getBillModel(baseCode).getBodyItems();
			if (!isBodyBaseCode(tableCode)) {
				BillUtil.convertBillItemsToTableCode(items, tableCode);
			} else {
				BillUtil.convertBillItemsToTableCode(items, null);
			}
			return items;
		}
		return null;
	}

	/**
	 * 创建日期:(2003-6-27 9:51:42)
	 */
	public String[] getBodyShareTableCodes(String baseTableCode) {
		return hBillTabs.getBodyShareTableCodes(baseTableCode);
	}

	/**
	 * 创建日期:(2003-6-27 9:51:42)
	 */
	public BillTabVO[] getShareTabVOs(int pos, String baseTabCode) {
		return hBillTabs.getShareTabVOs(pos, baseTabCode);
	}

	/**
	 * 得到表体可显示元素数组.
	 */
	public BillItem[] getBodyShowItems() {
		// return getShowItems(m_biBodyItems);
		return getShowItems(getBodyItems());
	}

	/**
	 * 得到表体可显示元素数组.
	 */
	public BillItem[] getBodyShowItems(String tableCode) {
		// return getShowItems(m_biBodyItems);
		BillItem[] items = getShowItems(getBodyItemsForTable(tableCode));
		// BillUtil.convertBillItemsToTableCode(items, tableCode);
		return items;
	}

	/**
	 * 得到所有子表编码. 创建日期:(2002-09-11 16:17:23)
	 * 
	 * @return java.lang.String[]
	 */
	public int getBodyTableCodeIndex(String tableCode) {
		String[] tableCodes = getBodyTableCodes();
		if (tableCodes == null)
			return -1;
		for (int i = 0; i < tableCodes.length; i++) {
			if (tableCodes[i].equals(tableCode))
				return i;
		}
		return -1;
	}

	/**
	 * 得到所有子表编码. 创建日期:(2002-09-11 16:17:23)
	 * 
	 * @return java.lang.String[]
	 */
	public String[] getBodyTableCodes() {
		return getTableCodes(BODY);
	}

	/**
	 * 创建日期:(2002-09-12 13:20:13)
	 * 
	 * @param tablecode
	 *            java.lang.String
	 * @return java.lang.String
	 */
	public String getBodyTableName(String tablecode) {
		return getTableName(BODY, tablecode);
	}

	/**
	 * 获得表体数据. 创建日期:(01-2-23 14:22:07)
	 */
	public CircularlyAccessibleValueObject[] getBodyValueChangeVOs(String bodyVOName) {
		BillModel bm = getBillModel();
		if (bm != null)
			return bm.getBodyValueChangeVOs(bodyVOName);
		return null;
	}

	/**
	 * 获得表体数据. 创建日期:(01-2-23 14:22:07)
	 */
	public CircularlyAccessibleValueObject[] getBodyValueChangeVOs(String tableCode, String bodyVOName) {
		BillModel bm = getBillModel(tableCode);
		if (bm != null)
			return bm.getBodyValueChangeVOs(bodyVOName);
		return null;
	}

	/**
	 * 获得表体数据. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @deprecated
	 */
	public void getBodyValueVOs(CircularlyAccessibleValueObject[] bodyVOs) {
		BillModel bm = getBillModel();
		if (bm != null)
			bm.getBodyValueVOs(bodyVOs);
	}

	/**
	 * 获得表体数据. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @deprecated
	 */
	public CircularlyAccessibleValueObject[] getBodyValueVOs(String bodyVOName) {
		BillModel bm = getBillModel();
		if (bm != null)
			return bm.getBodyValueVOs(bodyVOName);
		return null;
	}

	/**
	 * 获得表体数据. 创建日期:(01-2-23 14:22:07)
	 */
	public void getBodyValueVOs(String tableCode, CircularlyAccessibleValueObject[] bodyVOs) {
		BillModel model = getBillModel(tableCode);
		if (model != null)
			model.getBodyValueVOs(bodyVOs);
	}

	/**
	 * 获得表体数据. 创建日期:(01-2-23 14:22:07)
	 */
	public CircularlyAccessibleValueObject[] getBodyValueVOs(String tableCode, String bodyVOName) {
		BillModel model = getBillModel(tableCode);
		if (model != null)
			return model.getBodyValueVOs(bodyVOName);
		return null;
	}

	/**
	 * 创建日期:(2003-7-16 10:03:50)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCardStyle() {
		return cardStyle;
	}

	/**
	 * 设置单据数据编辑状态.
	 */
	public boolean getEnabled() {
		return m_bEnabled;
	}

	/**
	 * 公式执行器. 创建日期:(01-2-21 10:08:48)
	 */
	public FormulaParse getFormulaParse() {
		if (m_formulaParse == null) {
			m_formulaParse = createDefaultFormulaParse();
		}
		return m_formulaParse;
	}

	/**
	 * 创建日期:(2003-6-30 14:46:50)
	 * 
	 * @deprecated
	 */
	public String getHeaderOption() {
		BillStructVO bsVO = getHeaderStructVO();
		if (bsVO != null)
			return BillXMLUtil.billStructVOToXml(bsVO);
		return null;
	}

	public BillStructVO getHeaderStructVO() {
		BillTabVO[] vos = hBillTabs.getAllTabVos();
		if (vos == null)
			return null;
		BillStructVO bsVO = new BillStructVO();
		bsVO.setBillTabVOs(vos);
		bsVO.setCardStyle(getCardStyle());
		bsVO.setBilltablevos(getBillTableVos());
		return bsVO;
	}

	/**
	 * 由类名获得表头,表尾数据. 创建日期:(01-2-23 14:22:07)
	 */
	public CircularlyAccessibleValueObject getHeaderValueVO(String headVOName) {
		try {
			CircularlyAccessibleValueObject headVO = (CircularlyAccessibleValueObject) Class.forName(headVOName)
					.newInstance();
			getHeaderValueVO(headVO);
			return headVO;
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage());
		} catch (InstantiationException e) {
			Logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			Logger.error(e.getMessage());
		}
		return null;
	}

	public Object getChangeBillObjectByMetaData() {

		if (!isMeataDataTemplate())
			return null;

		Object o = null;

		// 表头对象属性影射
		HashMap<String, Object> map = new HashMap<String, Object>();
		BillItem[] items = getHeadTailItems();
		BillTabVO[] tabvos = getBillTabVOs(IBillItem.BODY);

		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				BillItem item = items[i];

				if (item.getMetaDataProperty() != null && item.getIDColName() == null) {
					Object otemp = item.converType(item.getValueObject());
					map.put(item.getMetaDataAccessPath(), otemp);
				}
			}
			map.put(ElementConstant.KEY_VOSTATUS, billstatus);
			// 表体对象属性影射

			if (tabvos != null) {
				for (int i = 0; i < tabvos.length; i++) {
					BillTabVO tabVO = tabvos[i];

					BillModel model = getBillModel(tabVO.getTabcode());

					map.put(tabVO.getMetadatapath(), model.getBodyChangeValueByMetaData());
				}
			}

			o = DASFacade.newInstanceWithKeyValues(getBillTempletVO().getHeadVO().getBillMetaDataBusinessEntity(), map)
					.getContainmentObject();
		} else {// 单表体
			BillModel model = getBillModel();
			Map<String, Object>[] maps = model.getBodyChangeValueByMetaData();
			if (tabvos != null && tabvos[0].getBillMetaDataBusinessEntity() != null && maps != null) {

				Object[] os = null;
				try {
					os = (Object[]) Array.newInstance(
							Class.forName(tabvos[0].getBillMetaDataBusinessEntity().getFullClassName()), maps.length);
				} catch (NegativeArraySizeException e) {
				} catch (ClassNotFoundException e) {
				}

				if (os != null) {
					for (int i = 0; i < maps.length; i++) {
						os[i] = DASFacade.newInstanceWithKeyValues(tabvos[0].getBillMetaDataBusinessEntity(), maps[i])
								.getContainmentObject();
					}
				}

				o = os;
			}

		}

		return o;
	}

	public Object getBillObjectByMetaData() {

		if (!isMeataDataTemplate())
			return null;

		Object o = null;

		// 表头对象属性影射
		HashMap<String, Object> map = new HashMap<String, Object>();
		BillItem[] items = getHeadTailItems();
		BillTabVO[] tabvos = getBillTabVOs(IBillItem.BODY);

		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				BillItem item = items[i];

				if (item.getMetaDataProperty() != null && item.getIDColName() == null) {
					Object otemp = item.converType(item.getValueObject());
					map.put(item.getMetaDataAccessPath(), otemp);
				}

				map.put(ElementConstant.KEY_VOSTATUS, billstatus);
			}
			// 表体对象属性影射

			if (tabvos != null) {
				for (int i = 0; i < tabvos.length; i++) {
					BillTabVO tabVO = tabvos[i];

					BillModel model = getBillModel(tabVO.getTabcode());

					map.put(tabVO.getMetadatapath(), model.getBodyValueByMetaData());
				}
			}

			o = DASFacade.newInstanceWithKeyValues(getBillTempletVO().getHeadVO().getBillMetaDataBusinessEntity(), map)
					.getContainmentObject();
		} else {// 单表体
			BillModel model = getBillModel();
			Map<String, Object>[] maps = model.getBodyValueByMetaData();
			if (tabvos != null && tabvos[0].getBillMetaDataBusinessEntity() != null && maps != null) {
				Object[] os = null;
				try {
					os = (Object[]) Array.newInstance(
							Class.forName(tabvos[0].getBillMetaDataBusinessEntity().getFullClassName()), maps.length);
				} catch (NegativeArraySizeException e) {
				} catch (ClassNotFoundException e) {
				}

				if (os != null) {
					for (int i = 0; i < maps.length; i++) {
						os[i] = DASFacade.newInstanceWithKeyValues(tabvos[0].getBillMetaDataBusinessEntity(), maps[i])
								.getContainmentObject();
					}
				}

				o = os;
			}

		}

		return o;
	}

	public boolean isMeataDataTemplate() {
		if (getBillTempletVO() != null) {
			if (getBillTempletVO().getHeadVO() != null) {
				if (getBillTempletVO().getHeadVO().getMetadataclass() != null)
					return true;
			}
		}
		return false;
	}

	/**
	 * 由已事例化VO获得表头,表尾数据. 创建日期:(01-2-23 14:22:07)
	 */
	public void getHeaderValueVO(CircularlyAccessibleValueObject headVO) {
		BillItem[] items = getHeadTailItems();
		BillItem item;
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				item = items[i];

				// Logger.info(item.getKey() + ":" + item.getDataType() + ":" +
				// item
				// .getValueObject());

				if (item.getValueObject() != null && item.getDataType() == IBillItem.MULTILANGTEXT) {

					MultiLangText mlt = (MultiLangText) item.getValueObject();

					headVO.setAttributeValue(item.getKey(), mlt.getText());
					headVO.setAttributeValue(item.getKey() + "2", mlt.getText2());
					headVO.setAttributeValue(item.getKey() + "3", mlt.getText3());
					headVO.setAttributeValue(item.getKey() + "4", mlt.getText4());
					headVO.setAttributeValue(item.getKey() + "5", mlt.getText5());
					headVO.setAttributeValue(item.getKey() + "6", mlt.getText6());

				} else {
					headVO.setAttributeValue(item.getKey(), item.converType(item.getValueObject()));
				}

			}
		}
	}

	/**
	 * 得到表头关键字对应元素. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public BillItem getHeadItem(String strKey) {
		if (strKey != null && hHeadItems != null && hHeadItems.containsKey(strKey))
			return (BillItem) hHeadItems.get(strKey);
		else
			return null;
	}

	/**
	 * 得到表头关键字对应元素. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public BillItem[] getHeadItems() {
		return m_biHeadItems;
	}

	/**
	 * 得到表头可显示元素数组.
	 */
	public BillItem[] getHeadItems(String tableCode) {
		BillItem[] items = getHeadItems();
		return filterItemsByTableCode(items, tableCode);
	}

	/**
	 * 得到表头可显示元素数组.
	 */
	public BillItem[] getHeadShowItems() {
		return getShowItems(getHeadItems());
	}

	/**
	 * 得到表头可显示元素数组.
	 */
	public BillItem[] getHeadShowItems(String tableCode) {
		BillItem[] items = getHeadShowItems();
		return filterItemsByTableCode(items, tableCode);
	}

	/**
	 * 得到所有主表页签编码. 创建日期:(2002-09-11 16:17:23)
	 * 
	 * @return java.lang.String[]
	 */
	public String[] getHeadTableCodes() {
		return getTableCodes(HEAD);
	}

	/**
	 * 创建日期:(2002-09-12 13:20:13)
	 * 
	 * @param tablecode
	 *            java.lang.String
	 * @return java.lang.String
	 */
	public String getHeadTableName(String tablecode) {
		return getTableName(HEAD, tablecode);
	}

	/**
	 * 得到表头关键字对应元素. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public BillItem getHeadTailItem(String strKey) {
		BillItem item = getHeadItem(strKey);
		if (item == null)
			return getTailItem(strKey);
		return item;
	}

	/**
	 * 得到表头关键字对应元素. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public BillItem[] getHeadTailItems() {
		return (BillItem[]) nc.vo.bill.pub.MiscUtil.ArraysCat(getHeadItems(), getTailItems());
	}

	/**
	 * 得到可显示元素数组.
	 */
	private BillItem[] getShowItems(BillItem[] biItems) {
		return BillUtil.getShowItems(biItems);
	}

	/**
	 * 得到表头可显示元素数组.
	 */
	public BillItem[] getShowItems(int pos, String tablecode) {
		if (pos == HEAD)
			return getHeadShowItems(tablecode);
		else if (pos == BODY)
			return getBodyShowItems(tablecode);
		else if (pos == TAIL)
			return getTailShowItems(tablecode);
		return null;
	}

	public HashMap<String, BillItemUISet> getBillDataUISet() {
		HashMap<String, BillItemUISet> sets = new HashMap<String, BillItemUISet>();

		putShowItemToHash(getHeadItems(), sets);
		// String[] tabcodes = getBodyTableCodes();
		// if(tabcodes != null) {
		// for (int i = 0; i < tabcodes.length; i++) {
		// putShowItemToHash(getBody(tabcodes[i]),sets);
		// }
		// }
		putShowItemToHash(getBodyAllItemss(), sets);
		putShowItemToHash(getTailItems(), sets);

		return sets;
	}

	private void putShowItemToHash(BillItem[] items, HashMap<String, BillItemUISet> set) {
		if (items != null) {
			for (int i = 0; i < items.length; i++) {

				if (items[i].getUiSet() != null) {

					String key = items[i].getPos() + items[i].getTableCode() + items[i].getKey();
					key += "ts:" + getBillTempletVO().getHeadVO().getTs();// yxq
																			// 2013/08/29

					set.put(key, items[i].getUiSet());
				}
			}
		}
	}

	/**
	 * getHeadTableCodes or getTableCodes. 创建日期:(2002-12-5 11:10:59)
	 */
	public String[] getTableCodes(int pos) {
		return hBillTabs.getTableCodes(pos);
	}

	/**
	 * 创建日期:(2002-09-12 13:20:13)
	 * 
	 * @param tablecode
	 *            java.lang.String
	 * @return java.lang.String
	 */
	public String getTableName(int pos, String tablecode) {
		if (BillUtil.validate(pos, tablecode) != null)
			return null;
		BillTabVO vo = hBillTabs.getTabVO(pos, tablecode);
		return vo == null ? null : vo.getTabname();
	}

	/**
	 * 创建日期:(2003-6-30 14:46:50)
	 */
	public BillTabVO getTabVO(int pos, String tableCode) {
		return hBillTabs.getTabVO(pos, tableCode);
	}

	/**
	 * 得到表尾关键字对应元素. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public BillItem getTailItem(String strKey) {
		if (strKey != null && hTailItems != null && hTailItems.containsKey(strKey))
			return (BillItem) hTailItems.get(strKey);
		else
			return null;
	}

	/**
	 * 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public BillItem[] getTailItems() {
		return m_biTailItems;
	}

	/**
	 * 得到表头可显示元素数组.
	 */
	public BillItem[] getTailItems(String tableCode) {
		BillItem[] items = getTailItems();
		return filterItemsByTableCode(items, tableCode);
	}

	/**
	 * 得到表头可显示元素数组.
	 */
	public BillItem[] getTailShowItems() {
		return getShowItems(getTailItems());
	}

	/**
	 * 得到表头可显示元素数组.
	 */
	public BillItem[] getTailShowItems(String tableCode) {
		BillItem[] items = getTailShowItems();
		return filterItemsByTableCode(items, tableCode);
	}

	/**
	 * 得到所有主表页签编码. 创建日期:(2002-09-11 16:17:23)
	 * 
	 * @return java.lang.String[]
	 */
	public String[] getTailTableCodes() {
		return getTableCodes(TAIL);
	}

	/**
	 * 创建日期:(2002-09-12 13:20:13)
	 * 
	 * @param tablecode
	 *            java.lang.String
	 * @return java.lang.String
	 */
	public String getTailTableName(String tablecode) {
		return getTableName(TAIL, tablecode);
	}

	/**
	 * 得到单据标题. 创建日期:(01-2-23 14:22:07)
	 */
	public String getTitle() {
		return m_strTitle;
	}

	/**
	 * 合计行表模式. 创建日期:(01-2-21 10:08:48)
	 */
	public DefaultTableModel getTotalTableModel() {
		return getBillModel().getTotalTableModel();
	}

	/**
	 * 初始化模板数据. 创建日期:(01-2-23 15:05:07)
	 */
	private void initBodyVOs(BillTempletBodyVO[] bodys) {
		if (bodys == null || bodys.length == 0)
			return;
		String code;
		int pos;
		for (int i = 0; i < bodys.length; i++) {
			if ((code = bodys[i].getTableCode()) == null || code.trim().length() == 0) {
				bodys[i].setTableCode(BillUtil.getDefaultTableCode(pos = bodys[i].getPos().intValue()));
				bodys[i].setTableName(BillUtil.getDefaultTableName(pos));
			}
		}

		// 模板VO排序 by pos table_code showorder
		BillUtil.sortBodyVOsByProps(bodys, new String[] { "pos", "table_code", "showorder" });
	}

	/**
	 * 初始化模板数据. 创建日期:(01-2-23 15:05:07)
	 */
	private void initTempletData(BillTempletVO newTempletVO, HashMap<String, BillItemUISet> sets) {

		if (newTempletVO == null) {
			Logger.info("设置模板数据为空!");
			return;
		}

		billTempletVO = newTempletVO;
		billTempletVO.setParentToBody();

		BillTempletHeadVO headVO = newTempletVO.getHeadVO();
		BillTempletBodyVO[] bodyVO = newTempletVO.getBodyVO();

		if (headVO != null) {
			m_strTitle = headVO.getBillTempletCaption();
			BillStructVO btVO = hBillTabs.initByHeadVO(headVO);
			if (btVO != null) {
				setCardStyle(btVO.getCardStyle());
				billTableVos = btVO.getBilltablevos();
			}
		}

		if (bodyVO != null) {
			initBodyVOs(bodyVO);
			hHeadItems = new Hashtable<String, BillItem>();
			hTailItems = new Hashtable<String, BillItem>();
			ArrayList<BillItem> list = new ArrayList<BillItem>();
			// pos tabcode
			HashSet<String> mapPosKey = new HashSet<String>();

			int pos = -1;
			String code = null;
			for (int i = 0; i < bodyVO.length; i++) {
				BillTempletBodyVO bVO = bodyVO[i];

				BillItem item = createDefaultBillItem(bVO, bVO.getCardflag());
				if (sets != null && bVO.getShowflag()) {
					String key = bVO.getPos() + bVO.getTableCode() + bVO.getItemkey();
					key += "ts:" + getBillTempletVO().getHeadVO().getTs();// yxq
																			// 2013/08/29
					BillItemUISet set = sets.get(key);
					item.setUiSet(set);
				}
				// if(bVO.getCardflag() != null &&
				// bVO.getCardflag().booleanValue()){
				if (item.isCard()) {
					// item.setList(false);
					item.setReadOrder(i);
					if (list.size() > 0 && code != null && (pos != item.getPos() || !code.equals(item.getTableCode()))) {
						addToHashtable(list.toArray(new BillItem[list.size()]));
						list.clear();
						mapPosKey.add(pos + code);
					}
					list.add(item);
					pos = item.getPos();
					code = item.getTableCode();
				}
			}

			if (list.size() > 0) {
				addToHashtable(list.toArray(new BillItem[list.size()]));
				list.clear();
				mapPosKey.add(pos + code);
			}

			BillItem[] items = getHeadItems();
			addRelationItem(items);

			// 将其他存储方式做数据同步
			// hBillTabs.filterInvalidTab(mapPosKey);
		}
	}

	private void addRelationItem(BillItem[] items) {
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				BillItem item = items[i];
				if (item != null) {
					if (item.getIDColName() != null && !item.getKey().equals(item.getIDColName())) {
						BillItem iditem = getHeadItem(item.getIDColName());
						if (iditem != null)
							iditem.addRelationItem(item);
					}
				}
			}
		}
	}

	/**
	 * 创建日期:(2003-6-30 14:46:50)
	 */
	public boolean isBodyBaseCode(String tableCode) {
		return hBillTabs.isBodyBaseCode(tableCode);
	}

	/**
	 * 创建日期:(2003-6-30 14:46:50)
	 */
	public boolean isBaseCode(int pos, String tableCode) {
		return hBillTabs.isBaseCode(pos, tableCode);
	}

	// /**
	// * @return Returns the design.
	// */
	// public boolean isDesign() {
	// return design;
	// }
	/**
	 * 创建日期:(2003-6-30 14:46:50)
	 */
	boolean isMixStyle() {
		return BillStructVO.MIX_CARD.equals(getCardStyle());
	}

	/**
	 * 判断为空. 创建日期:(2001-6-14 10:53:32)
	 * 
	 * @param item
	 *            nc.ui.pub.bill.BillItem
	 */
	private boolean isNULL(Object o) {
		if (o == null || o.toString() == null || o.toString().trim().equals(""))
			return true;
		return false;
	}

	/**
	 * 创建日期:(2002-09-18 13:51:33)
	 * 
	 * @param pos
	 *            int
	 * @param tabcode
	 *            java.lang.String
	 * @param itemkey
	 *            java.lang.String
	 */
	public void removeBillItem(int pos, String tabcode, String itemkey) {
		Hashtable<String, BillItem> hashItems;
		switch (pos) {
		case HEAD:
		case TAIL:
			hashItems = pos == HEAD ? hHeadItems : hTailItems;
			if (hashItems != null && hashItems.containsKey(itemkey))
				hashItems.remove(itemkey);
			if (pos == HEAD)
				m_biHeadItems = BillUtil.removeBillItemFromArrayByKey(m_biHeadItems, itemkey);
			else
				m_biTailItems = BillUtil.removeBillItemFromArrayByKey(m_biTailItems, itemkey);
			break;
		case BODY: {
			if (hBillModels != null && hBillModels.containsKey(tabcode)) {
				BillModel model = getBillModel(tabcode);
				int index = model.getBodyColByKey(itemkey);
				if (index >= 0) {
					BillItem[] items = model.getBodyItems();
					model.setBodyItems((BillItem[]) BillUtil.removeItemFromArray(items, items[index]));
				}
			}
			break;
		}
		}

		if (pos == HEAD || pos == TAIL) {
			if (hBillTabs.isBaseCode(pos, tabcode)) {
				removeBaseTab(pos, tabcode);
			} else {
				String basetab = hBillTabs.getBaseTableCode(pos, tabcode);
				removeTab(pos, tabcode);
				if (!basetab.equals(tabcode)) {
					removeBaseTab(pos, basetab);
				}
			}
		} else {
			removeTab(pos, tabcode);
		}
	}

	private void removeBaseTab(int pos, String basetabcode) {
		BillItem[] items = getBillItemsByPosAndTableCode(pos, basetabcode);
		String[] sharecodes = hBillTabs.getShareTableCodes(pos, basetabcode);
		if ((items == null || items.length == 0) && (sharecodes == null || sharecodes.length == 0)) {
			hBillTabs.remove(pos, basetabcode);
		}
	}

	private void removeTab(int pos, String tabcode) {
		BillItem[] items = getBillItemsByPosAndTableCode(pos, tabcode);
		if (items == null || items.length == 0) {
			hBillTabs.remove(pos, tabcode);
			if (pos == BODY)
				hBillModels.remove(tabcode);
		}
	}

	/**
	 * 创建日期:(2002-09-18 13:51:33)
	 * 
	 * @param pos
	 *            int
	 * @param tablecode
	 *            java.lang.String
	 * @param itemkey
	 *            java.lang.String
	 */
	public void removeTabItems(int pos, String tablecode) {
		if (tablecode == null)
			return;
		switch (pos) {
		case HEAD:
		case TAIL: {
			Hashtable<String, BillItem> hashItems = pos == HEAD ? hHeadItems : hTailItems;
			BillItem[] items = pos == HEAD ? getHeadItems() : getTailItems();
			if (items != null) {
				for (int i = 0; i < items.length; i++) {
					if ((items[i].getTableCode() != null) && (items[i].getTableCode().equals(tablecode))) {
						hashItems.remove(items[i].getKey());
						items[i] = null;
					}
				}
			}
			hBillTabs.remove(pos, tablecode);
			items = (BillItem[]) arrayFilterNull(items);
			if (pos == HEAD)
				m_biHeadItems = items;
			else
				m_biTailItems = items;
			break;
		}
		case BODY: {
			hBillModels.remove(tablecode);
			hBillTabs.remove(pos, tablecode);
			break;
		}
		}
	}

	/**
	 * 过滤掉对象数组空对象.
	 * 
	 * @param os
	 *            the object array
	 * @return the object array without null object
	 */
	private Object[] arrayFilterNull(Object[] os) {
		if (os == null || os.length == 0)
			return null;
		ArrayList<Object> list = new ArrayList<Object>();
		for (int i = 0; i < os.length; i++) {
			if (os[i] != null)
				list.add(os[i]);
		}
		if (list.size() > 0) {
			final Class<?> c = list.get(0).getClass();
			os = (Object[]) java.lang.reflect.Array.newInstance(c, list.size());
			return list.toArray(os);
		}
		return null;
	}

	/**
	 * 恢复单据数据.
	 */
	public void resumeValue() {
		// 恢复表头表尾数据
		BillItem[] headTailItems = getHeadTailItems();
		if (headTailItems != null) {
			for (int i = 0; i < headTailItems.length; i++)
				headTailItems[i].resumeValue();
		}

		// 恢复表体数据
		// m_bmBillModel.resumeValue();
		if (hBillModels != null) {
			Enumeration<String> keys = hBillModels.keys();
			while (keys.hasMoreElements()) {
				hBillModels.get(keys.nextElement()).resumeValue();
			}
		}
	}

	/**
	 * 创建日期:(01-2-23 15:23:11)
	 * 
	 * @param newBmTableModel
	 *            ufbill.BillModel
	 */
	public void setBillModel(String tableCode, BillModel newbmBillModel) {
		if (newbmBillModel == null)
			hBillModels.remove(tableCode);
		hBillModels.put(tableCode, newbmBillModel);
	}

	/**
	 * 创建日期:(01-2-23 15:23:11)
	 * 
	 * @param newBmTableModel
	 *            ufbill.BillModel
	 */
	public void setBillModel(BillModel newbmBillModel) {
		// m_bmBillModel = newbmBillModel;
		setBillModel(getDefaultBodyTableCode(), newbmBillModel);
	}

	/**
	 * 创建日期:(2004-1-5 10:00:46)
	 * 
	 * @param newBillTableVos
	 *            nc.vo.pub.bill.BillTableVO[]
	 */
	public void setBillTableVos(nc.vo.pub.bill.BillTableVO[] newBillTableVos) {
		billTableVos = newBillTableVos;
	}

	/**
	 * 设置单据类型 创建日期:(01-2-26 11:28:04)
	 */
	public void setBillType(String strBillType) {
		m_strBillType = strBillType;
	}

	/**
	 * 设置单据数据 创建日期:(01-2-26 11:28:04)
	 */
	public void setBillValueVO(AggregatedValueObject billVO) {

		if (getBillTempletVO() != null && getBillTempletVO().getHeadVO().getMetadataclass() != null)
			setBillValueObjectByMetaData(billVO);
		else {
			String tablecode = DEFAULT_BODY_TABLECODE;
			String tablename = DEFAULT_BODY_TABLENAME;
			String[] tableCodes = getBodyTableCodes();
			if (tableCodes != null && tableCodes.length > 0) {
				tablecode = tableCodes[0];
				tablename = getBodyTableName(tablecode);
			}
			nc.vo.pub.ExAggregatedVO exBillVO = new nc.vo.pub.ExAggregatedVO(billVO, tablecode, tablename);
			setBillValueVO(exBillVO);
		}
	}

	/**
	 * 设置单据数据 创建日期:(01-2-26 11:28:04)
	 */
	public void setBillValueVO(ExtendedAggregatedValueObject billVO) {
		clearViewData();
		CircularlyAccessibleValueObject headVO = billVO.getParentVO();
		setHeaderValueVO(headVO);
		if (this.hBillModels == null || this.hBillModels.size() == 0)
			return;
		String[] tables = billVO.getTableCodes();
		if (tables != null) {
			for (int i = 0; i < tables.length; i++) {
				CircularlyAccessibleValueObject[] bodyVOs = billVO.getTableVO(tables[i]);
				if (bodyVOs != null)
					setBodyValueVO(tables[i], bodyVOs);
			}
		} else if (hasBody()) {
			setBodyValueVO(null);
		}
	}

	public void setBillValueObjectByMetaData(Object o) {
		if (o == null) {
			clearViewData();
			return;
		}

		IBusinessEntity be = getBillTempletVO().getHeadVO().getBillMetaDataBusinessEntity();

		long t = System.currentTimeMillis();

		BillItem[] items = getHeadTailItems();
		BillTabVO[] tabvos = getBillBaseTabVOsByPosition(IBillItem.BODY);

		if (items != null) {
			NCObject ncobject = null;

			if (be.getBeanStyle().getStyle() == BeanStyleEnum.AGGVO_HEAD)
				ncobject = DASFacade.newInstanceWithContainedObject(be, o);
			else if (be.getBeanStyle().getStyle() == BeanStyleEnum.NCVO
					|| be.getBeanStyle().getStyle() == BeanStyleEnum.POJO) {
				if (o instanceof AggregatedValueObject) {
					o = ((AggregatedValueObject) o).getParentVO();
					ncobject = DASFacade.newInstanceWithContainedObject(be, o);
				} else {
					ncobject = DASFacade.newInstanceWithContainedObject(be, o);
				}
			}
			// 设置表头数据
			try {
				/** BATCH START */
				BatchMatchContext.getShareInstance().setInBatchMatch(true);
				BatchMatchContext.getShareInstance().clear();
				for (int i = 0; i < items.length; i++) {
					if (items[i].getMetaDataProperty() != null) {
						if (!items[i].getMetaDataProperty().isRefAttribute())
							setHeadTailItem(items[i],
									ncobject.getAttributeValue(items[i].getMetaDataProperty().getAttribute()));
					}
				}
				// /***BATCH END***/
				BatchMatchContext.getShareInstance().executeBatch();
			} finally {
				BatchMatchContext.getShareInstance().setInBatchMatch(false);

			}
			loadLoadHeadRelation();
			// 设置表体数据
			if (tabvos != null) {
				if (tabvos.length > 1) {
					// 多页签批量设置加载关联项
					MetaDataGetBillModelRelationItemValue gvs = new MetaDataGetBillModelRelationItemValue();
					for (int i = 0; i < tabvos.length; i++) {
						BillTabVO tabVO = tabvos[i];

						NCObject[] ncos = (NCObject[]) ncobject.getAttributeValue(tabVO.getMetadatapath());

						BillModel model = getBillModel(tabVO.getTabcode());

						model.setBodyObjectByMetaData(ncos, true, false);
						model.prepareLoadLoadRelationItemValues(gvs);

					}
					// 执行批处理取关联项值
					gvs.executeBatch();
					// 各BillMOdel关联项赋值
					for (int i = 0; i < tabvos.length; i++) {
						BillTabVO tabVO = tabvos[i];
						BillModel model = getBillModel(tabVO.getTabcode());
						model.setLoadLoadRelationItemValuesToModel(gvs.getRelationItemValues(model));
					}

				} else {

					for (int i = 0; i < tabvos.length; i++) {
						BillTabVO tabVO = tabvos[i];

						NCObject[] ncos = (NCObject[]) ncobject.getAttributeValue(tabVO.getMetadatapath());

						BillModel model = getBillModel(tabVO.getTabcode());

						model.setBodyObjectByMetaData(ncos);

					}
				}

			}
			//
			// }
		} else {
			// 单表体
			Object[] vos = null;
			if (o instanceof AggregatedValueObject)
				vos = ((AggregatedValueObject) o).getChildrenVO();
			else if (o.getClass().isArray())
				vos = (Object[]) o;

			if (vos != null && tabvos != null && tabvos[0].getBillMetaDataBusinessEntity() != null) {

				NCObject[] ncos = new NCObject[vos.length];

				for (int i = 0; i < ncos.length; i++) {
					ncos[i] = DASFacade.newInstanceWithContainedObject(tabvos[0].getBillMetaDataBusinessEntity(),
							vos[i]);
				}

				BillModel model = getBillModel();

				model.setBodyObjectByMetaData(ncos);
			}
		}

		Logger.info("ExecBatchRefSetPk taken time:" + (System.currentTimeMillis() - t) + "ms.");

	}

	/**
	 * 设置单据数据 创建日期:(01-2-26 11:28:04)
	 */
	public void setImportBillValueVO(ExtendedAggregatedValueObject billVO) {

		setImportBillValueVO(billVO, true);

	}

	/**
	 * 
	 * @param billVO
	 *            单据VO
	 * @param isExecEditFormulas
	 *            是否执行编辑公式
	 * @param isPK
	 *            参照的值是否是PK
	 */
	public void setImportBillValueVO(ExtendedAggregatedValueObject billVO, boolean isExecEditFormulas, boolean isPK) {
		setImportBillValueVOImpl(billVO, isExecEditFormulas, isPK);
	}

	public void setImportBillValueVO(ExtendedAggregatedValueObject billVO, boolean isExecEditFormulas) {
		setImportBillValueVOImpl(billVO, isExecEditFormulas, false);
	}

	private BillItem[] getOrderedHeadTailItems() {
		BillItem[] headItems = getHeadItems();
		BillItem[] tailItems = getTailItems();
		BillUtil.sortBillItemsByShowOrder(headItems);
		BillUtil.sortBillItemsByShowOrder(tailItems);
		BillItem[] headTailBillItems = (BillItem[]) nc.vo.bill.pub.MiscUtil.ArraysCat(headItems, tailItems);
		return headTailBillItems;

	}

	/**
	 * 设置单据数据 创建日期:(01-2-26 11:28:04)
	 */
	private void setImportBillValueVOImpl(ExtendedAggregatedValueObject billVO, boolean isExecEditFormulas, boolean isPK) {
		setImporting(true);
		// clearViewData();
		CircularlyAccessibleValueObject headVO = billVO.getParentVO();

		if (headVO != null) {
			// 表头
			if (headVO != null) {
				// for (int i = 0; i < items.length; i++) {

				int length;
				BillItem[] billitems = getOrderedHeadTailItems();
				// 数据模板，赋值顺序按界面Item的显示顺序，如果是Excel导入，历史原因，还是按照VO的属性顺序 2012-11-14
				if (isPK) {
					length = billitems.length;
				} else {
					length = headVO.getAttributeNames().length;
				}
				for (int i = 0; i < length; i++) {
					BillItem item;

					if (isPK) {
						item = billitems[i];
					} else {
						item = getHeadTailItem(headVO.getAttributeNames()[i]);
					}

					if (item != null) {
						Object o = headVO.getAttributeValue(item.getKey());

						if (o != null && item.getDataType() != IBillItem.MULTILANGTEXT)
							o = item.getConverter().convertToBillItem(item.getDataType(), o);

						if (item.getDataType() == IBillItem.MULTILANGTEXT) {

							MultiLangText mlt = new MultiLangText();

							mlt.setText((String) o);
							// 如果主语种为空，说明导入的是辅语种。同步下当前登录语种的值到主语种。
							if (o == null) {
								Object name = headVO.getAttributeValue(item.getKey() + getCurrentLangIndex());
								mlt.setText((String) name);
							}

							Object name2 = headVO.getAttributeValue(item.getKey() + "2");

							mlt.setText2((String) name2);

							Object name3 = headVO.getAttributeValue(item.getKey() + "3");

							mlt.setText3((String) name3);

							Object name4 = headVO.getAttributeValue(item.getKey() + "4");

							mlt.setText4((String) name4);

							Object name5 = headVO.getAttributeValue(item.getKey() + "5");

							mlt.setText5((String) name5);

							Object name6 = headVO.getAttributeValue(item.getKey() + "6");

							mlt.setText6((String) name6);

							setHeadTailItem(item, mlt);
						} else if (item.getDataType() == UFREF) {
							UIRefPane ref = (UIRefPane) item.getComponent();
							if (o != null) {
								ref.fireRefEdit(new RefEditEvent(ref));
								ref.getRefModel().clearData();
								if (isPK) {
									ref.setPK(o);
								} else {
									AbstractRefModel model = ref.getRefModel();
									boolean isCaseSensitive = false;
									if (model != null) {
										isCaseSensitive = model.isCaseSensitve();
										model.setCaseSensive(true);
									}
									ref.setBlurValue(o.toString());
									if (model != null) {
										model.setCaseSensive(isCaseSensitive);
									}
								}

								// ref.setValueObj(ref.getRefPK());
								ref.setValueObjFireValueChangeEvent(ref.getRefPK());
							} else
								ref.setPK(null);
						} else {
							// 校验长度
							BillUtil.checkStringLength(item, o);
							if (item.getComponent() instanceof UIRefPane) {
								UIRefPane ref = (UIRefPane) item.getComponent();
								ref.fireRefEdit(new RefEditEvent(ref));
								ref.setValueObjFireValueChangeEvent(o);
							} else {
								item.setValue(o);
								item.getItemEditor().stopEditing();
								if (item.getItemEditor() instanceof ComboBoxItemEditor) {
									((ComboBoxItemEditor) item.getItemEditor()).fireAftereditEvent();
								}
							}
						}

					}
				}

				BillItem[] items = getHeadTailItems();
				String[] formulas = BillUtil.getFormulas(items, IBillItem.LOAD);
				if (formulas != null)
					execHeadFormulas(formulas);
				loadLoadHeadRelation(true);
			}
		}
		// 表体
		if (this.hBillModels == null || this.hBillModels.size() == 0)
			return;
		String[] tables = billVO.getTableCodes();
		if (tables != null) {
			for (int i = 0; i < tables.length; i++) {
				CircularlyAccessibleValueObject[] bodyVOs = billVO.getTableVO(tables[i]);
				if (bodyVOs != null) {
					BillModel bm = getBillModel(tables[i]);
					if (bm != null) {
						bm.setImporting(true);
						bm.setImportBodyDataVO(bodyVOs, isExecEditFormulas,
								getBillCardPanel().getBodyBillScrollPane(tables[i]).getBillActionListener(), isPK);
						bm.setImporting(false);
					}
				}
			}
		} else if (hasBody()) {
			setBodyValueVO(null);
		}
		setImporting(false);
	}

	private int getCurrentLangIndex() {
		int currLangIndex = 0;
		if (MultiLangContext.getInstance().getCurrentLangVO() != null) {
			currLangIndex = MultiLangContext.getInstance().getCurrentLangVO().getLangseq().intValue() - 1;
		}
		return currLangIndex;
	}

	/**
	 * 设置表体元素数组. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @param newBiHeadItems
	 *            ufbill.BillItem[]
	 */
	public void setBodyItems(BillItem[] newbiBodyItems) {
		setBodyItems(getDefaultBodyTableCode(), newbiBodyItems);
	}

	/**
	 * 设置表体元素数组. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @param newBiHeadItems
	 *            ufbill.BillItem[]
	 */
	public void setBodyItems(String tableCode, BillItem[] newbiBodyItems) {
		if (newbiBodyItems == null || newbiBodyItems.length == 0)
			removeTabItems(BODY, tableCode);
		else {
			for (int i = 0; i < newbiBodyItems.length; i++) {
				newbiBodyItems[i].setPos(BODY);
				newbiBodyItems[i].setTableCode(tableCode);
			}
			addToHashtable(newbiBodyItems);
		}
		// getBillModel(tableCode).setBodyItems(newbiBodyItems);
	}

	/**
	 * 设置表体数据. 创建日期:(01-2-23 14:22:07)
	 */
	public void setBodyValueVO(CircularlyAccessibleValueObject[] bodyVOs) {
		BillModel bm = getBillModel();
		if (bm != null)
			bm.setBodyDataVO(bodyVOs);
	}

	/**
	 * 设置表体数据. 创建日期:(01-2-23 14:22:07)
	 */
	public void setBodyValueVO(String tableCode, CircularlyAccessibleValueObject[] bodyVOs) {
		BillModel bm = getBillModel(tableCode);
		if (bm != null)
			bm.setBodyDataVO(bodyVOs);
	}

	/**
	 * 创建日期:(2003-7-16 10:03:50)
	 * 
	 * @param newCardStyle
	 *            java.lang.String
	 */
	public void setCardStyle(java.lang.String newCardStyle) {
		cardStyle = newCardStyle;
	}

	/**
	 * 设置单据数据编辑状态.
	 */
	public void setEnabled(boolean newEnabled) {
		m_bEnabled = newEnabled;
		if (hHeadItems != null)
			setEnabledToHashTable(hHeadItems, newEnabled);
		if (hTailItems != null)
			setEnabledToHashTable(hTailItems, newEnabled);
		if (!m_bEnabled) {
			clearHeadTailItemShowWarning();
			getBillCardPanel().getHeadTabbedPane().clearShowWarning();
			getBillCardPanel().getBillTable().getHeaderPopupMenu().repaint();
		}

		String[] tablecodes = getTableCodes(BODY);
		if (tablecodes != null) {
			for (int i = 0; i < tablecodes.length; i++) {
				String code = tablecodes[i];
				getBillModel(code).setEnabled(newEnabled);
			}
			if (!m_bEnabled) {
				clearBodyShowWarning();
			}
		}

		if (newEnabled)
			setBillstatus(VOStatus.UPDATED);

	}

	/**
	 * 对Hashtable中的每一个BillItem设置可编辑属性.
	 */
	private void setEnabledToHashTable(Hashtable<String, BillItem> table, boolean newEnabled) {
		Enumeration<String> keys = table.keys();
		while (keys.hasMoreElements()) {
			BillItem item = table.get(keys.nextElement());
			if (newEnabled) {
				item.setEnabled(item.isEdit());
			} else {
				item.setEnabled(newEnabled);
			}
		}
	}

	/**
	 * 公式执行器. 创建日期:(01-2-21 10:08:48)
	 */
	public void setFormulaParse(FormulaParse newFormulaParse) {
		m_formulaParse = newFormulaParse;

		// getBillModel().setFormulaParse(newFormulaParse);
		Enumeration<String> tableCodes = hBillModels.keys();
		while (tableCodes.hasMoreElements()) {
			getBillModel(tableCodes.nextElement().toString()).setFormulaParse(newFormulaParse);
		}
	}

	/**
	 * 设置表头,表尾数据. 创建日期:(01-2-23 14:22:07)
	 */
	public void setHeaderValueVO(CircularlyAccessibleValueObject headVO) {
		if (headVO == null)
			return;
		long t = System.currentTimeMillis();
		/** BATCH START */
		BatchMatchContext.getShareInstance().setInBatchMatch(true);
		BatchMatchContext.getShareInstance().clear();

		try {

			BillItem[] items = getHeadTailItems();
			if (items != null) {
				for (int i = 0; i < items.length; i++) {
					if (headVO.getAttributeValue(items[i].getKey()) != null
							&& items[i].getDataType() == IBillItem.MULTILANGTEXT) {

						MultiLangText mlt = new MultiLangText();

						mlt.setText((String) headVO.getAttributeValue(items[i].getKey()));
						mlt.setText2((String) headVO.getAttributeValue(items[i].getKey() + "2"));
						mlt.setText3((String) headVO.getAttributeValue(items[i].getKey() + "3"));
						mlt.setText4((String) headVO.getAttributeValue(items[i].getKey() + "4"));
						mlt.setText5((String) headVO.getAttributeValue(items[i].getKey() + "5"));
						mlt.setText6((String) headVO.getAttributeValue(items[i].getKey() + "6"));

						setHeadTailItem(items[i], mlt);

					} else {
						setHeadTailItem(items[i], headVO.getAttributeValue(items[i].getKey()));
					}
				}
			}

			// /***BATCH END***/
			BatchMatchContext.getShareInstance().executeBatch();
		} finally {
			BatchMatchContext.getShareInstance().setInBatchMatch(false);

		}
		Logger.info("ExecBatchRefSetPk taken time:" + (System.currentTimeMillis() - t) + "ms.");
	}

	/**
	 * 设置表头关键字对应元素. 创建日期:(01-2-23 14:22:07)
	 */
	public void setHeadItem(String strKey, Object Value) {
		if (hHeadItems.containsKey(strKey)) {
			BillItem item = hHeadItems.get(strKey);
			setHeadTailItem(item, Value);
		}
	}

	/**
	 * 设置表头元素数组. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @param newBiHeadItems
	 *            ufbill.BillItem[]
	 */
	public void setHeadItems(BillItem[] newbiHeadItems) {
		hHeadItems = new Hashtable<String, BillItem>();
		if (newbiHeadItems != null) {
			for (int i = 0; i < newbiHeadItems.length; i++) {
				newbiHeadItems[i].setPos(HEAD);
				addToHashtable(HEAD, newbiHeadItems[i]);
			}
			addRelationItem(newbiHeadItems);
		}

		// 保持set进来的顺序
		m_biHeadItems = newbiHeadItems;
	}

	/**
	 * 设置表头关键字对应元素. 创建日期:(01-2-23 14:22:07)
	 */
	private void setHeadTailItem(BillItem item, Object value) {
		if (item != null) {
			if (item.isIsDef())
				value = item.getConverter().convertToBillItem(item.getDataType(), value);
			item.setValue(value);
		}
	}

	/**
	 * 设置表头关键字对应元素. 创建日期:(01-2-23 14:22:07)
	 */
	public void setTailItem(String strKey, Object Value) {
		if (hTailItems.containsKey(strKey)) {
			BillItem item = hTailItems.get(strKey);
			setHeadTailItem(item, Value);
		}
	}

	/**
	 * 设置表尾元素数组. 创建日期:(01-2-23 14:22:07)
	 */
	public void setTailItems(BillItem[] newbiTailItems) {
		hTailItems = new Hashtable<String, BillItem>();
		if (newbiTailItems != null) {
			for (int i = 0; i < newbiTailItems.length; i++) {
				addToHashtable(TAIL, newbiTailItems[i]);
			}
		}

		// 保持set进来的顺序
		m_biTailItems = newbiTailItems;
	}

	// /**
	// * 更新自定义项. 创建日期:(2001-11-9 16:18:40)
	// */
	// public void updateItemByDef(nc.vo.bd.def.DefVO[] defVOs,
	// String fieldPrefix, boolean isHead) {
	// updateItemByDef(getDefaultBodyTableCode(), defVOs, fieldPrefix, isHead);
	// }
	//
	// /**
	// * 更新自定义项. 创建日期:(2001-11-9 16:18:40)
	// */
	// public void updateItemByDef(String tableCode, nc.vo.bd.def.DefVO[]
	// defVOs,
	// String fieldPrefix, boolean isHead) {
	// if (defVOs == null)
	// return;
	// String z1 = nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill",
	// "UPP_Bill-000502");//自定义
	// String z2 = nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill",
	// "UPP_Bill-000503");//自由项
	// for (int i = 0; i < defVOs.length; i++) {
	// nc.vo.bd.def.DefVO defVO = defVOs[i];
	// //String itemkey = defVO.getFieldName();
	// String itemkey = fieldPrefix + (i + 1);
	// BillItem item;
	//
	// //位置
	// if (isHead)
	// item = getHeadTailItem(itemkey);
	// else
	// item = getBodyItem(tableCode, itemkey);
	// if (item != null) {
	// if (defVO != null) {
	// //默认名称
	// String defaultshowname = item.getName();
	// if (defaultshowname == null
	// || defaultshowname.startsWith("自定义")
	// || defaultshowname.startsWith("自由项")
	// || defaultshowname.startsWith(z1)
	// || defaultshowname.startsWith(z2)
	// || defaultshowname.startsWith("H-UDC")
	// || defaultshowname.startsWith("B-UDC")
	// || defaultshowname.startsWith(fieldPrefix)) {
	// defaultshowname = defVO.getDefname();
	// item.setName(defaultshowname);
	// }
	// //输入长度
	// int inputlength = defVO.getLengthnum().intValue();
	// if (inputlength < item.getLength())
	// item.setLength(inputlength);
	// //数据类型
	// String type = defVO.getType();
	// int datatype = STRING;
	// if (type.equals("备注"))
	// datatype = STRING;
	// else if (type.equals("日期"))
	// datatype = DATE;
	// else if (type.equals("数字")) {
	// datatype = INTEGER;
	// if ((defVO.getDigitnum() != null)
	// && (defVO.getDigitnum().intValue() > 0)) {
	// datatype = DECIMAL;
	// item.setDecimalDigits(defVO.getDigitnum()
	// .intValue());
	// }
	// }
	// if (type.equals("统计"))
	// datatype = USERDEF;
	// item.setDataType(datatype);
	// //参照类型
	// String reftype = defVO.getDefdef().getPk_bdinfo();//Pk_defdef();
	// if (type.equals("统计")) {
	// item.setRefType(reftype);
	// }
	// item.reCreateComponent();
	// item.setIsDef(true);
	// } else { //item.setShow(false);
	// }
	// }
	// }
	// }

	/**
	 * 表头表尾数据
	 */
	public void updateHead() {
		BillItem[] headTailItems = getHeadTailItems();
		if (headTailItems != null) {
			for (int i = 0; i < headTailItems.length; i++)
				headTailItems[i].updateValue();
		}
	}

	/**
	 * 更新表体
	 */
	public void updateBody() {
		Enumeration<String> keys = hBillModels.keys();
		while (keys.hasMoreElements()) {
			hBillModels.get(keys.nextElement()).updateValue();
		}
	}

	/**
	 * 更新单据数据.
	 */
	public void updateValue() {

		// 表体数据
		updateHead();
		updateBody();
		setBillstatus(VOStatus.UNCHANGED);
	}

	/**
	 * @return Returns the billCardPanel.
	 */
	BillCardPanel getBillCardPanel() {
		return billCardPanel;
	}

	/**
	 * @param billCardPanel
	 *            The billCardPanel to set.
	 */
	void setBillCardPanel(BillCardPanel billCardPanel) {
		this.billCardPanel = billCardPanel;
	}

	/*
	 * if formulas is not null,ignore itemkeys
	 */
	public boolean execHeadTailValidateFormulas(String[] formulas, String[] itemkeys) {
		BillItem[] items = getHeadTailItems();
		if (formulas == null || formulas.length == 0) {
			items = BillFormulaUtil.getBillItemsByKey(itemkeys, items);
			formulas = BillFormulaUtil.getItemsFormulas(items, BillFormulaContext.FORMULATYPE_VALIDATE);
		}
		if (formulas == null)
			return true;
		BillFormulaContext bfc = new BillFormulaContext(BillFormulaContext.FORMULATYPE_VALIDATE);
		BillItemContext bc = new BillItemContext();
		bc.setItems(items);
		BillFormulaUtil.execFormulas(getBillCardPanel(), bfc, bc, formulas);
		return bfc.isExecNormal();
	}

	public boolean execValidateFormulas() {
		if (!execHeadTailValidateFormulas(null, null))
			return false;
		return execBodyValidateFormulas();
	}

	public boolean execBodyValidateFormulas() {
		String[] bodyTableCodes = getBodyBaseTableCodes();
		if (bodyTableCodes == null || bodyTableCodes.length == 0)
			return true;
		BillModel model;
		for (int i = 0; i < bodyTableCodes.length; i++) {
			model = getBillModel(bodyTableCodes[i]);
			if (model != null) {
				if (!model.execValidateForumlas(null, null, null))
					return false;
			}
		}
		return true;
	}

	/**
	 * 创建日期:(2003-4-22 16:44:08)
	 * 
	 * @return java.lang.String
	 */
	String getDefaultBodyTableCode() {
		String tableCode = BillData.DEFAULT_BODY_TABLECODE;
		String[] bodyCodes = getBodyTableCodes();
		if (bodyCodes != null && bodyCodes.length >= 1)
			tableCode = bodyCodes[0];
		return tableCode;
	}

	protected BillItem createDefaultBillItem(BillTempletBodyVO body, boolean iscard) {
		return new BillItem(body, iscard);
	}

	/**
	 * 得到表体表模式. 创建日期:(01-2-23 15:23:11)
	 * 
	 * @return ufbill.BillModel
	 */
	private BillModel addBillModel(String tableCode) {
		if (hBillModels == null)
			hBillModels = new Hashtable<String, BillModel>();
		if (!hBillModels.containsKey(tableCode)) {
			BillModel billModel;
			BillTabVO btvo = (BillTabVO) hBillTabs.get(BODY + tableCode);
			String baseTab = null;
			if (btvo != null) {
				baseTab = btvo.getBasetab();
			}
			if (baseTab != null) {
				if (!hBillModels.containsKey(baseTab)) {
					billModel = createDefaultBillMode(btvo);
					billModel.setFormulaParse(getFormulaParse());
					hBillModels.put(baseTab, billModel);
				} else
					billModel = hBillModels.get(baseTab);
			} else {
				billModel = createDefaultBillMode(btvo);
				billModel.setFormulaParse(getFormulaParse());
				hBillModels.put(tableCode, billModel);
			}
			return billModel;
		}
		return hBillModels.get(tableCode);
	}

	private boolean hasBody() {
		return hBillModels != null && hBillModels.size() > 0;
	}

	public BillTempletVO getBillTempletVO() {
		return billTempletVO;
	}

	public int getBillstatus() {
		return billstatus;
	}

	public void setBillstatus(int billstatus) {
		this.billstatus = billstatus;
	}

	public void addHyperlinkListener(BillItemHyperlinkListener ll) {
		BillItem[] items = getHeadItems();

		addBillItemsHyperlinkListener(items, ll);

		String[] tabcodes = getBodyTableCodes();

		if (tabcodes != null) {
			for (int i = 0; i < tabcodes.length; i++) {
				items = getBodyItemsForTable(tabcodes[i]);
				addBillItemsHyperlinkListener(items, ll);
			}
		}
	}

	private void addBillItemsHyperlinkListener(BillItem[] items, BillItemHyperlinkListener ll) {
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				if (items[i].isHyperlink())
					items[i].addBillItemHyperlinkListener(ll);
			}
		}
	}

	/**
	 * 设置表头表尾警告模式
	 */
	public void setHeadTailItemShowWarning(String[] itemKeys) {
		if (itemKeys == null) {
			return;
		}
		for (int i = 0; i < itemKeys.length; i++) {
			getHeadTailItem(itemKeys[i]).showWaring();
		}
	}

	public void clearShowWarning() {
		clearHeadTailItemShowWarning();
		clearBodyShowWarning();
	}

	public void clearHeadTailItemShowWarning() {
		BillItem[] items = getHeadTailItems();
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				items[i].clearShowWaring();
			}
		}
	}

	public void clearBodyShowWarning() {
		String[] tabcodes = getBodyTableCodes();

		if (tabcodes != null && getBillCardPanel().getBodyTabbedPane() != null) {
			getBillCardPanel().getBodyTabbedPane().clearShowWarning();
			for (int i = 0; i < tabcodes.length; i++) {
				BillModel model = getBillModel(tabcodes[i]);
				model.clearCellShowWarning();
			}
		}

	}

	public void setImporting(boolean isImporting) {
		this.isImporting = isImporting;
	}

	public boolean isImporting() {
		return isImporting;
	}
}
