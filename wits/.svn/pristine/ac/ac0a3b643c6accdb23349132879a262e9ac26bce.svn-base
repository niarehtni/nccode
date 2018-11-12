package nc.ui.wa.item.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.desktop.ui.ServerTimeProxy;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.md.model.impl.MDEnum;
import nc.ui.hr.itemsource.view.AbstractBillItemEditor;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.hrp.budgetitemcmp.model.SetbudgetitemNameRefModel;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.wa.item.model.HRWADefdocGridRefModel;
import nc.ui.wa.item.model.ItemDefaultValueProvider;
import nc.ui.wa.item.view.custom.CheckedTextField;
import nc.ui.wa.item.view.custom.ItemDataSourcePanel;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.hr.itemsource.ItemPropertyConst;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.hr.pub.FormatVO;
import nc.vo.hrp.setbudgetitem.SetBudgetContext;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.PropertyEnumVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.util.WaConstant;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author: wh
 * @date: 2009-11-25 下午07:22:10
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("serial")
public class ItemBillFormEditor extends HrBillFormEditor implements BillCardBeforeEditListener {

	public static final int Float_width = 12;
	public static final int Float_Digit = 2;
	public static final int String_width = 20;
	public static final int String_Digit = 0;
	public static final int Date_width = 10;
	public static final int Date_Digit = 0;

	public static final int String_MAX_width = 128;

	private AbstractBillItemEditor[] customEditors;

	protected static final String DATA_SOURCE_PANEL = "datasourcetype";

	protected ItemDataSourcePanel itemDataSourcePanel;

	/**
	 * 自定义初始化方法
	 * 
	 * @see nc.ui.hr.frame.view.HrBillFormEditor#initUI()
	 */
	@Override
	public void initUI() {
		super.initUI();
		// 确定及集团是否可用

		itemDataSourcePanel = createItemDataSourcePanel();
		customEditors = createCustomEditors();

		// 设定数据长度，小数位数的最大、最小输入值
		BillItem fldWidth = getFldWidthBillItem();
		BillItem fldDecimal = getFldDecimalBillItem();

		if (fldWidth != null) {
			((UIRefPane) fldWidth.getComponent()).setMaxValue(ItemPropertyConst.Float_MAX_width);
			((UIRefPane) fldWidth.getComponent()).setMinValue(ItemPropertyConst.MIN_width);
			((UIRefPane) fldDecimal.getComponent()).setMaxValue(ItemPropertyConst.Float_MAX_decimalwidth);
			((UIRefPane) fldDecimal.getComponent()).setMinValue(ItemPropertyConst.Float_MIN_decimalwidth);

		}
		for (AbstractBillItemEditor editor : customEditors) {
			initCustomEditor(editor);
		}

		billCardPanel.setBillData(billCardPanel.getBillData());

		// 为总额项目设定参照
		BillItem item = getHeadItem(WaItemVO.TOTALITEM);
		if (item != null) {
			LoginContext context = getModel().getContext();
			SetBudgetContext bugetContext = new SetBudgetContext();
			bugetContext.setPk_group(context.getPk_group());
			bugetContext.setPk_org(context.getPk_org());
			bugetContext.setPk_loginUser(context.getPk_loginUser());

			// 设置年信息
			bugetContext.setYearinfo(String.valueOf(ServerTimeProxy.getInstance().getServerTime().getYear()));
			SetbudgetitemNameRefModel refmodel = new SetbudgetitemNameRefModel(bugetContext);
			((UIRefPane) item.getComponent()).setRefModel(refmodel);
			((UIRefPane) item.getComponent()).setMultiSelectedEnabled(true);
		}

		// 重置 项目分类的参照!
		BillItem categoryID = getItemCategory();
		UIRefPane ref = (UIRefPane) categoryID.getComponent();
		HRWADefdocGridRefModel hrWADefdocGridRefModel = new HRWADefdocGridRefModel(ItemDefaultValueProvider
				.getDefdocVO().getPk_defdoclist());
		hrWADefdocGridRefModel.setPara1("1002Z710000000004RC2");
		ref.setRefModel(hrWADefdocGridRefModel);

		// BillModel billModel = billCardPanel.getBillModel();
		// billModel.addDecimalListener(new
		// AbstractBillItemRowDigitsAdapter(billModel,WaItemVO.IFLDDECIMAL, new
		// String[]{
		// WaItemVO.NSUMCEIL, WaItemVO.NSUMFLOOR,WaItemVO.NPSNCEIL,
		// WaItemVO.NPSNFLOOR})
		// {
		// @Override
		// public int getDecimalFromSource(int row, Object sourcePkValue)
		// {
		// return new Integer(sourcePkValue.toString());
		// }
		// });

		// new CheckedTextField("sumceil","sumceilflag","nsumceil"),
		// new CheckedTextField("sumfloor","sumfloorflag","nsumfloor"),
		// new CheckedTextField("psnceil","psnceilflag","npsnceil"),
		// new CheckedTextField("psnfloor","psnfloorflag","npsnfloor"),

		resetPrecision();
	}

	public void resetPrecision() {
		BillItem flddecimal = getHeadItem(WaItemVO.IFLDDECIMAL);
		Integer iflddecimal = (Integer) flddecimal.getValueObject();
		if (iflddecimal == null) {
			iflddecimal = 2;
		}
		BillItem fldwidth = getHeadItem(WaItemVO.IFLDWIDTH);
		Integer ifldwidth = (Integer) fldwidth.getValueObject();
		if (ifldwidth == null) {
			ifldwidth = 12;
		}
		int length = ifldwidth + 1 + iflddecimal;
		getItemDataSourcePanel().getFixValueEditor().setMaxLength(length);
		getItemDataSourcePanel().getFixValueEditor().setNumPoint(iflddecimal);

		for (int i = 0; i < customEditors.length; i++) {
			if (customEditors[i] instanceof CheckedTextField) {
				((CheckedTextField) customEditors[i]).setMaxLength(length);
				((CheckedTextField) customEditors[i]).setNumPoint(iflddecimal);
			}
		}

	}

	protected BillItem getItemCategory() {
		return getHeadItem(WaItemVO.CATEGORY_ID);
	}

	protected BillItem getMidBillItem() {
		return getHeadItem(WaItemVO.MID);
	}

	protected BillItem getInHiBillItem() {
		return getHeadItem(WaItemVO.ISINHI);
	}

	protected BillItem getTaxFlagBillItem() {
		return getHeadItem(WaItemVO.TAXFLAG);
	}

	protected BillItem getInTotalBillItem() {
		return getHeadItem(WaItemVO.INTOTALITEM);
	}

	protected BillItem getIpropertyBillItem() {
		return getHeadItem(WaItemVO.IPROPERTY);
	}

	protected BillItem getFldWidthBillItem() {
		return getHeadItem(WaItemVO.IFLDWIDTH);
	}

	protected BillItem getFldDecimalBillItem() {
		return getHeadItem(WaItemVO.IFLDDECIMAL);
	}

	public ItemDataSourcePanel getItemDataSourcePanel() {
		return itemDataSourcePanel;
	}

	protected ItemDataSourcePanel createItemDataSourcePanel() {
		return new ItemDataSourcePanel(DATA_SOURCE_PANEL, getModel(), this);
	}

	protected AbstractBillItemEditor[] getCustomEditors() {
		return customEditors;
	}

	protected AbstractBillItemEditor[] createCustomEditors() {
		AbstractBillItemEditor[] editors = new AbstractBillItemEditor[] { getItemDataSourcePanel(),

		new CheckedTextField("sumceil", "sumceilflag", "nsumceil"),
				new CheckedTextField("sumfloor", "sumfloorflag", "nsumfloor"),
				new CheckedTextField("psnceil", "psnceilflag", "npsnceil"),
				new CheckedTextField("psnfloor", "psnfloorflag", "npsnfloor"), };
		return editors;
	}

	@Override
	public void afterEdit(BillEditEvent e) {
		if (WaItemVO.IITEMTYPE.equals(e.getKey())) {
			TypeEnumVO typeEnum = getTypeEnumValue();

			updateUIEnable(typeEnum);
			if (getModel().getUiState().equals(UIState.ADD)) {
				updateWidthAndDecimalOnAdd(typeEnum);
			} else if (getModel().getUiState().equals(UIState.EDIT)) {
				updateWidthAndDecimalOnEdit(typeEnum);
			}

			updateCustomEditorState(typeEnum);

			updateIPropertyState(typeEnum);

			updatePrecision(typeEnum);

		} else if (WaItemVO.IPROPERTY.equals(e.getKey()) && getModel().getUiState().equals(UIState.ADD)) {
			Integer itype = (Integer) getIpropertyBillItem().getValueObject();
			if (PropertyEnumVO.SYSTEM.value().equals(itype)) {
				((UIComboBox) getIpropertyBillItem().getComponent()).setSelectedIndex(0);
				// MessageDialog.showErrorDlg(ItemBillFormEditor.this, null,
				// ResHelper.getString("60130glbitem","060130glbitem0031")/*@res
				// "不能新增系统项目"*/);
				// throw new
				// BusinessException(ResHelper.getString("60130glbitem","060130glbitem0031")/*@res
				// "不能新增系统项目"*/);
				ShowStatusBarMsgUtil.showErrorMsg(ResHelper.getString("6013commonbasic", "06013commonbasic0020")/*
																												 * @
																												 * res
																												 * "错误"
																												 */,
						ResHelper.getString("60130glbitem", "060130glbitem0031")/*
																				 * @
																				 * res
																				 * "不能新增系统项目"
																				 */, getModel().getContext());
				return;
			}
			PropertyEnumVO propEnum = MDEnum.valueOf(PropertyEnumVO.class, itype);
			getMidBillItem().setEnabled(propEnum == PropertyEnumVO.OTHER);

			// if(FromEnumVO.FORMULA.value().equals(itemDataSourcePanel.getCurrentDataSourceItem().getValue())
			// ){
			// MessageDialog.showHintDlg(ItemBillFormEditor.this,
			// ResHelper.getString("60130glbitem","060130glbitem0032")/*@res
			// "提醒"*/,
			// ResHelper.getString("60130glbitem","060130glbitem0033")/*@res
			// "数据类型发生变化，公式需要重新定义，否则将无法保存公式定义的信息!"*/);
			// }
		} else if (WaItemVO.IFLDWIDTH.equals(e.getKey()) || WaItemVO.IFLDDECIMAL.equals(e.getKey())) {
			// 设定其他数据源-固定值的数据长度 与小数位数
			if (e.getValue() != null) {
				resetPrecision();
			}
		}
	}

	/**
	 * 根据自定义项目的字段类型（数值、字符、日期）设定checkBox的可用性
	 * 
	 * @author xuanlt on 2010-1-20
	 * @return void
	 */
	protected void updateUIEnable(TypeEnumVO typeName) {

		BillItem midleItem = getMidBillItem();
		BillItem isHiItem = getInHiBillItem();

		BillItem taxItem = getTaxFlagBillItem();
		BillItem inTotalItem = getInTotalBillItem();

		BillItem iflddecimal = getFldDecimalBillItem();
		BillItem fldWidth = getFldWidthBillItem();

		BillItem totalitem = getHeadItem(WaItemVO.TOTALITEM);
		// 2015-10-08 zhousze 薪资发放项目组织/集团字符型和日期型的发放项目的工资卡字段需置灰 begin
		BillItem bankaccount = getHeadItem(WaClassItemVO.BANKACCOUNT);
		// end

		BillItem ipropertyBillItem = getIpropertyBillItem();
		if (typeName.equals(TypeEnumVO.FLOATTYPE)) {
			if (PropertyEnumVO.OTHER.value().equals(ipropertyBillItem.getValueObject())) {
				midleItem.setEnabled(true);
			} else {
				midleItem.setEnabled(false);
			}
			if (isHiItem != null) {
				isHiItem.setEnabled(true);
			}

			taxItem.setEnabled(true);

			if (inTotalItem != null) {
				inTotalItem.setEnabled(true);
			}

			fldWidth.setEnabled(true);
			iflddecimal.setEnabled(true);
			if (totalitem != null) {
				totalitem.setEnabled(true);
			}
			// 2015-10-08 zhousze 薪资发放项目组织/集团字符型和日期型的发放项目的工资卡字段需置灰 begin
			if (bankaccount != null) {
				bankaccount.setEnabled(true);
			}
			// end

		} else if (typeName.equals(TypeEnumVO.CHARTYPE)) {
			midleItem.setEnabled(true);
			if (isHiItem != null) {
				isHiItem.setEnabled(false);
				// 2015-10-15 zhousze 切换数据类型后，切换为字符与日期型后，需要去电“纳入薪酬体系”的勾选项 begin
				isHiItem.setValue(UFBoolean.FALSE);
				// end
			}
			taxItem.setEnabled(false);
			taxItem.setValue(UFBoolean.FALSE);
			if (inTotalItem != null) {
				inTotalItem.setEnabled(false);
			}
			fldWidth.setEnabled(true);
			iflddecimal.setEnabled(false);
			// 2015-10-15 zhousze 薪资发放项目组织/集团字符型和日期型的发放项目的工资卡字段需置灰,
			// 并且切换数据类型后，需要清空字符、数值型的内容 begin
			if (bankaccount != null) {
				bankaccount.setEnabled(false);
				bankaccount.clearViewData();
			}
			// end
			if (totalitem != null) {
				totalitem.setEnabled(false);
				totalitem.clearViewData();
			}
		} else if (typeName.equals(TypeEnumVO.DATETYPE)) {
			midleItem.setEnabled(true);
			if (isHiItem != null) {
				isHiItem.setEnabled(false);
				// 2015-10-15 zhousze 切换数据类型后，切换为字符与日期型后，需要去电“纳入薪酬体系”的勾选项 begin
				isHiItem.setValue(UFBoolean.FALSE);
				// end
			}
			taxItem.setEnabled(false);
			taxItem.setValue(UFBoolean.FALSE);
			if (inTotalItem != null) {
				inTotalItem.setEnabled(false);
			}
			fldWidth.setEnabled(false);
			iflddecimal.setEnabled(false);
			if (totalitem != null) {
				totalitem.setEnabled(false);
				totalitem.clearViewData();
			}
			// 2015-10-15 zhousze 薪资发放项目组织/集团字符型和日期型的发放项目的工资卡字段需置灰,
			// 并且切换数据类型后，需要清空字符、数值型的内容 begin
			if (bankaccount != null) {
				bankaccount.setEnabled(false);
				bankaccount.clearViewData();
			}
			// end
		}

	}

	/**
	 * 更新精度与数据长度
	 * 
	 * @author xuanlt on 2010-4-6
	 * @param typeName
	 * @return void
	 */
	private void updatePrecision(TypeEnumVO typeName) {
		BillItem iflddecimal = getFldDecimalBillItem();

		BillItem ifldwidth = getFldWidthBillItem();

		BillItem totalItem = getHeadItem(WaItemVO.TOTALITEM);
		if (typeName.equals(TypeEnumVO.FLOATTYPE)) {
			ifldwidth.setValue(Float_width);
			iflddecimal.setValue(Float_Digit);

		} else if (typeName.equals(TypeEnumVO.CHARTYPE)) {
			ifldwidth.setValue(String_width);
			iflddecimal.setValue(String_Digit);
			if (totalItem != null) {
				totalItem.clearViewData();
			}

		} else if (typeName.equals(TypeEnumVO.DATETYPE)) {
			ifldwidth.setValue(Date_width);
			iflddecimal.setValue(Date_Digit);

			if (totalItem != null) {
				totalItem.clearViewData();
			}
		}

	}

	protected BillItem getHeadItem(String itemkey) {
		return getBillCardPanel().getHeadItem(itemkey);
	}

	/**
	 * 更新增减属性
	 * 
	 * @param type
	 */
	private void updateIPropertyState(TypeEnumVO type) {
		BillItem item = getIpropertyBillItem();
		boolean enable = type == TypeEnumVO.FLOATTYPE;
		item.setEnabled(enable);
		if (!enable) {
			item.setValue(PropertyEnumVO.OTHER.value());
		}
	}

	/**
	 * 初始化单个自定义控件
	 * 
	 * @param editor
	 */
	private void initCustomEditor(AbstractBillItemEditor editor) {
		BillItem item = billCardPanel.getHeadItem(editor.getKeyOfBillItem());
		editor.setItem(item);
		editor.initialize();
		// 初始化为不可用
		editor.setContentEnabled(false);
		item.setItemEditor(editor);
	}

	/**
	 * 事件监听处理
	 * 
	 * @see nc.ui.hr.frame.view.HrBillFormEditor#handleEvent(nc.ui.uif2.AppEvent)
	 */
	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);

		if (AppEventConst.MODEL_INITIALIZED.equalsIgnoreCase(event.getType())) {
			for (AbstractBillItemEditor editor : customEditors) {
				editor.setPk_org(getModel().getContext().getPk_org());
			}
		}

		if (getModel().getUiState() == UIState.NOT_EDIT) {
			setEnable(false);
		} else if (getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT) {
			setEnable(true);
		}
		// 2015-10-27 zhousze 公共薪资项目数值型输入预警信息后，切换时会显示之前输入的预警信息 begin
		// resetPrecision();
		// end
	}

	/**
	 * 设置结转设置和预警条件设置是否可以编辑
	 * 
	 * @param isEnable
	 */
	private void setEnable(boolean isEnable) {
		TypeEnumVO typeEnum = getTypeEnumValue();
		if (!typeEnum.equals(TypeEnumVO.FLOATTYPE)) {
			isEnable = false;
		}
		billCardPanel.getHeadItem("sumceil").setEnabled(isEnable);
		billCardPanel.getHeadItem("sumfloor").setEnabled(isEnable);
		billCardPanel.getHeadItem("psnceil").setEnabled(isEnable);
		billCardPanel.getHeadItem("psnfloor").setEnabled(isEnable);

	}

	/**
	 * 增加从自定义控件取数的逻辑
	 * 
	 * @see nc.ui.uif2.editor.BillForm#getValue()
	 */
	@Override
	public Object getValue() {
		WaItemVO itemVO = (WaItemVO) super.getValue();
		// 设定总额项目
		BillItem item = getHeadItem(WaItemVO.TOTALITEM);
		if (item != null) {
			String[] pks = ((UIRefPane) item.getComponent()).getRefPKs();
			String strPKs = "";
			if (!ArrayUtils.isEmpty(pks)) {
				strPKs = FormatVO.formatArrayToString(pks, "");
			}

			itemVO.setTotalitem(strPKs);
		}

		for (AbstractBillItemEditor editor : customEditors) {
			editor.collectData(itemVO);
		}
		if (getModel().getContext().getNodeType() == NODE_TYPE.GROUP_NODE) {
			itemVO.setPk_org(getModel().getContext().getPk_group());
		}

		return itemVO;
	}

	/**
	 * 增加对自定义控件赋值的逻辑
	 * 
	 * @see nc.ui.uif2.editor.BillForm#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object object) {
		TypeEnumVO typeEnum = getTypeEnumValue();
		updateCustomEditorState(typeEnum); // 修改面板公式面板

		super.setValue(object);

		// 设定预算总额项目

		if (object == null) {
			return;
		}
		for (AbstractBillItemEditor editor : customEditors) {
			editor.setDataToEditor((SuperVO) object);
		}

		WaItemVO itemvo = (WaItemVO) object;

		String itms = itemvo.getTotalitem();
		if (!StringUtils.isBlank(itms)) {
			String[] items = itms.split(",");
			for (int i = 0; i < items.length; i++) {
				items[i] = items[i].trim();
			}
			BillItem item = getHeadItem(WaItemVO.TOTALITEM);
			if (item != null) {
				((UIRefPane) item.getComponent()).setPKs(items);
			}
		}

	}

	public Integer getItemDataType() {

		Integer itemType = (Integer) getHeadItem(WaItemVO.IITEMTYPE).getValueObject();
		if (itemType == null) {
			itemType = 0;
		}

		return itemType;
	}

	/**
	 * 更新自定义控件的状态
	 * 
	 * @return
	 */
	protected void updateCustomEditorState(TypeEnumVO typeEnum) {
		for (AbstractBillItemEditor editor : customEditors) {
			editor.updateStateByType(typeEnum);
		}
	}

	@Override
	protected void onEdit() {
		super.onEdit();
		setHeadItemEnable(WaItemVO.IITEMTYPE, false);
		setHeadItemEnable(WaItemVO.IPROPERTY, false);
		TypeEnumVO typeEnum = getTypeEnumValue();
		updateUIEnable(typeEnum);
		updateWidthAndDecimalOnEdit(typeEnum);
		updateCustomEditorState(typeEnum);

	}

	/*
	 * private void setFldwidthMinValue(){ BillItem fldwidth =
	 * getHeadItem(WaItemVO.IFLDWIDTH); Integer ifldwidth = (Integer)
	 * fldwidth.getValueObject(); if(ifldwidth==null){ ifldwidth = 12; }
	 * 
	 * ((UIRefPane)fldwidth.getComponent()).setMinValue(ifldwidth);
	 * 
	 * }
	 * 
	 * private void setFlddecimalMinValue(){ BillItem flddecimal =
	 * getHeadItem(WaItemVO.IFLDDECIMAL); Integer iflddecimal = (Integer)
	 * flddecimal.getValueObject(); if(iflddecimal==null){ iflddecimal = 2; }
	 * 
	 * ((UIRefPane)flddecimal.getComponent()).setMinValue(iflddecimal);
	 * 
	 * }
	 */
	/**
	 * @author xuanlt on 2010-5-28
	 * @see nc.ui.hr.uif2.view.HrBillFormEditor#onAdd()
	 */
	@Override
	protected void onAdd() {
		// TODO Auto-generated method stub
		super.onAdd();
		TypeEnumVO typeEnum = getTypeEnumValue();
		updateUIEnable(typeEnum);
		updateWidthAndDecimalOnAdd(typeEnum);
		updateCustomEditorState(typeEnum);
		// 设定显示顺序.集团是0 组织是1
		if (getModel().getContext().getNodeType() == NODE_TYPE.ORG_NODE) {
			getHeadItem(WaItemVO.IDISPLAYSEQ).setValue(1);
		} else {
			getHeadItem(WaItemVO.IDISPLAYSEQ).setValue(0);
		}

	}

	private void updateWidthAndDecimalOnEdit(TypeEnumVO typeName) {
		BillItem iflddecimal = getFldDecimalBillItem();
		BillItem fldWidth = getFldWidthBillItem();

		if (typeName.equals(TypeEnumVO.FLOATTYPE)) {
			((UIRefPane) fldWidth.getComponent()).setMaxValue(ItemPropertyConst.Float_MAX_width);
			((UIRefPane) iflddecimal.getComponent()).setMaxValue(ItemPropertyConst.Float_MAX_decimalwidth);

		} else if (typeName.equals(TypeEnumVO.CHARTYPE)) {
			((UIRefPane) fldWidth.getComponent()).setMaxValue(String_MAX_width);
		}

		Integer ifldwidth = (Integer) fldWidth.getValueObject();
		if (ifldwidth == null) {
			ifldwidth = 12;
		}
		((UIRefPane) fldWidth.getComponent()).setMinValue(ifldwidth);
		Integer decimal = (Integer) iflddecimal.getValueObject();
		if (decimal == null) {
			decimal = 2;
		}
		// 20151022 shenliangc NCdp205479093 发放项目的精度修改控制错误
		// ((UIRefPane)iflddecimal.getComponent()).setMinValue(decimal);
	}

	private void updateWidthAndDecimalOnAdd(TypeEnumVO typeName) {
		BillItem iflddecimal = getFldDecimalBillItem();
		BillItem fldWidth = getFldWidthBillItem();

		if (typeName.equals(TypeEnumVO.FLOATTYPE)) {
			((UIRefPane) fldWidth.getComponent()).setMaxValue(ItemPropertyConst.Float_MAX_width);
			((UIRefPane) fldWidth.getComponent()).setMinValue(ItemPropertyConst.MIN_width);

			((UIRefPane) iflddecimal.getComponent()).setMaxValue(ItemPropertyConst.Float_MAX_decimalwidth);
			((UIRefPane) iflddecimal.getComponent()).setMinValue(ItemPropertyConst.Float_MIN_decimalwidth);
		} else if (typeName.equals(TypeEnumVO.CHARTYPE)) {
			((UIRefPane) fldWidth.getComponent()).setMaxValue(String_MAX_width);
			((UIRefPane) fldWidth.getComponent()).setMinValue(String_Digit);
		}

	}

	public TypeEnumVO getTypeEnumValue() {
		BillItem item = billCardPanel.getHeadItem(WaItemVO.IITEMTYPE);
		if (item != null) {
			TypeEnumVO typeEnum = TypeEnumVO.FLOATTYPE;
			Integer itype = (Integer) item.getValueObject();

			if (itype != null) {
				typeEnum = MDEnum.valueOf(TypeEnumVO.class, itype);
			}

			return typeEnum;
		} else {
			return TypeEnumVO.FLOATTYPE;// 默认是数值
		}
	}

	public String getItemKey() {
		return (String) billCardPanel.getHeadItem(WaItemVO.ITEMKEY).getValueObject();
	}

	public String getItemPK() {
		return (String) billCardPanel.getHeadItem(WaItemVO.PK_WA_ITEM).getValueObject();
	}

	public Integer getIfldWidth() {
		return (Integer) billCardPanel.getHeadItem(WaItemVO.IFLDWIDTH).getValueObject();
	}

	public Integer getIflddecimal() {
		return (Integer) billCardPanel.getHeadItem(WaItemVO.IFLDDECIMAL).getValueObject();
	}

	// MOD {X 不於系統進行分攤處理,不能填借貸方科目、供應商編碼} kevin.nie 2017-11-09 start
	protected Map<String, DefdocVO> shareDocMap = null;

	@Override
	public boolean beforeEdit(BillItemEvent evt) {
		String ikey = evt.getItem().getKey();
		// if (ikey.equals(WaItemVO.DEF2) || ikey.equals(WaItemVO.DEF3) ||
		// ikey.equals(WaItemVO.DEF4)
		// || ikey.equals(WaItemVO.DEF5)) {
		// String pk_share = (String)
		// getBillCardPanel().getHeadItem(WaItemVO.DEF1).getValueObject();
		// if (StringUtils.isNotEmpty(pk_share)) {
		// Map<String, DefdocVO> shareMap = getShareDocMap();
		// if (null != shareMap) {
		// DefdocVO defVO = shareMap.get(pk_share);
		// if (null != defVO &&
		// defVO.getCode().equals(WaConstant.SHARE_NOT_SYS__SHARED)) {
		// return false;
		// }
		// }
		// }
		// }

		return true;
	}

	public Map<String, DefdocVO> getShareDocMap() {
		if (null == shareDocMap) {
			StringBuilder where = new StringBuilder();
			where.append(" pk_defdoclist in (select pk_defdoclist from bd_defdoclist ");
			where.append(" where code='").append(WaConstant.DEF_CODE_SHARE).append("') ");
			where.append(" and enablestate=2 ");
			try {
				shareDocMap = queryBasicInfo(DefdocVO.class, where.toString(), new String[] { DefdocVO.PK_DEFDOC });
			} catch (BusinessException e1) {
				Logger.error(e1);
			}
		}
		return shareDocMap;
	}

	public <T extends SuperVO> Map<String, T> queryBasicInfo(Class<T> voClass, String condition, String[] fields)
			throws BusinessException {
		IUAPQueryBS qryBs = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		List<T> vos = (List<T>) qryBs.retrieveByClause(voClass, condition);
		Map<String, T> resultMap = new HashMap<String, T>();
		if (null != vos && !vos.isEmpty() && !ArrayUtils.isEmpty(fields)) {
			for (T vo : vos) {
				StringBuilder keyBuf = new StringBuilder();
				for (String attr : fields) {
					keyBuf.append(String.valueOf(vo.getAttributeValue(attr)));
				}
				resultMap.put(keyBuf.toString(), vo);
			}
		}

		return resultMap;
	}
	// {X 不於系統進行分攤處理,不能填借貸方科目、供應商編碼} kevin.nie 2017-11-09 end

	// public Integer getItemDataType(){
	// return (Integer)
	// billCardPanel.getHeadItem(WaItemVO.IFLDDECIMAL).getValueObject();
	// }
	//
}