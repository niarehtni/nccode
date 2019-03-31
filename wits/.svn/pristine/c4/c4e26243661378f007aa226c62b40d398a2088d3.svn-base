package nc.ui.hr.func_tax;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.hr.func.IRefPanel;
import nc.ui.hr.itemsource.view.IParaPanel;
import nc.ui.pub.beans.UIAsteriskPanelWrapper;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.wa.ctymgt.CtymgtDelegator;
import nc.vo.hr.formula.IFormulaConst;
import nc.vo.hr.func.FunctableItemVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 专项附加费用扣除-累计专项已扣
 * 
 * @author xuhw
 */
public class WATaxFunTaxTotalSpecialDeductionedPanel extends UIPanel implements ItemListener,
		IParaPanel, IRefPanel, IFormulaConst {
	private static final long serialVersionUID = -5415459347134279156L;

	private UILabel ItemText = null;
	private UIComboBox ivjcmbItem = null;// 薪资发放项目
	private WaClassItemVO[] itemdata = null;// 薪资项目
	private int datatype = 1;// 函数需要返回的数据类型，默认数字型
	private UIComboBox cmdDaystype = null;
	public int parasLen = 0;

	private WaLoginContext context = null;

	public void setContext(WaLoginContext context) {
		this.context = context;
	}

	public WaLoginContext getContext() {
		return this.context;
	}

	public int getDatatype() {
		return this.datatype;
	}

	/**
	 * WaParaPanel 构造子注解。
	 */
	public WATaxFunTaxTotalSpecialDeductionedPanel() {
		super();
		initialize();
	}

	/**
	 * 薪资项目
	 * 
	 * @return
	 */
	private UILabel getUILabel1() {
		if (ItemText == null) {
			ItemText = new UILabel();
			ItemText.setName("ItemText");
			ItemText.setText(ResHelper.getString("6013commonbasic",
					"06013commonbasic0024")/* @res "薪资项目：" */);
			ItemText.setBounds(10, 15, 80, 22);

		}
		return ItemText;
	}

	private UIAsteriskPanelWrapper cmbItemUI = null;

	private UIAsteriskPanelWrapper getcmbItemUI() {

		if (cmbItemUI == null) {
			cmbItemUI = new UIAsteriskPanelWrapper(getcmbItem());
			cmbItemUI.setBounds(93, 15, 147, 22);
			cmbItemUI.setMustInputItem(true);
		}

		return cmbItemUI;

	}

	private nc.ui.pub.beans.UIComboBox getcmbItem() {
		if (ivjcmbItem == null) {
			ivjcmbItem = new nc.ui.pub.beans.UIComboBox();
			ivjcmbItem.setName("cmbItem");
			ivjcmbItem.setBounds(100, 6, 140, 22);

			ivjcmbItem.setTranslate(true);
		}
		return ivjcmbItem;
	}

	/**
	 * 函数参数合法性校验
	 */
	public void checkPara(int dataType) throws Exception {
		try {
			// 判断非空
			String nullstr = "";
			if (getcmbItem().isVisible()
					&& getcmbItem().getSelectedIndex() <= 0) {
				if (nullstr.length() > 0) {
					nullstr += ",";
				}
				nullstr += ResHelper.getString("common", "UC000-0003385")/*
																		 * @res
																		 * "薪资项目"
																		 */;
			}

		} catch (Exception e) {
			handleException(e);
			throw e;
		}
	}

	public void clearDis() {
	}

	/**
	 * 返回函数的参数内容
	 */
	public String[] getPara() throws Exception {
		String[] paras = new String[1];

		// 项目 //判断薪资项目数据类型
		WaItemVO item = CtymgtDelegator.getWaItem().queryWaItemVOByPk(
				itemdata[getcmbItem().getSelectedIndex() - 1].getPk_wa_item());

		paras[0] = item.getItemkey().toString().trim();
		return paras;

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

	public void initData() {

		getcmbItem().removeAllItems();
		getcmbItem().addItem(" ");

		try {
			itemdata = CtymgtDelegator.getClassItemQueryService()
					.queryAllClassItemsForFormular(context.getClassPK());
			if (itemdata != null && itemdata.length > 0)
				for (int i = 0; i < itemdata.length; i++)
					getcmbItem().addItem(itemdata[i].getMultilangName());
		} catch (Exception ex) {
			handleException(ex);
		}
		// 默认选中空项
		getcmbItem().setSelectedIndex(0);
	}

	/**
	 * 初始化类。
	 */
	/* 警告：此方法将重新生成。 */
	private void initialize() {
		setName("WaParaPanel");
		setLayout(null);
		setSize(240, 200);
		add(getUILabel1(), getUILabel1().getName());
		add(getcmbItemUI(), getcmbItem().getName());
		// add(getUILabel2(), getUILabel2().getName());
		// add(getincludecmbFunc(), getincludecmbFunc().getName());
	}

	public void setDatatype(int newDatatype) {
		datatype = newDatatype;
	}

	/**
	 * 根据选择的函数更新参数设置界面
	 * 
	 * @param paras
	 *            nc.vo.wa.func.FunctableItemVO[]
	 */
	public void updateDis(FunctableItemVO[] paras) {

		getcmbItem().removeAllItems();
		getcmbItem().addItem(" ");

		try {
			itemdata = CtymgtDelegator.getClassItemQueryService()
					.queryAllClassItemsForFormular(context.getClassPK());
			if (itemdata != null && itemdata.length > 0)
				for (int i = 0; i < itemdata.length; i++)
					getcmbItem().addItem(itemdata[i].getMultilangName());
		} catch (Exception ex) {
			handleException(ex);
		}
		// 默认选中空项
		getcmbItem().setSelectedIndex(0);
	}

	public void setParasLen(int newParasLen) {
		parasLen = newParasLen;
	}

	public void updateDis(int index) {
		initData();
	}

	public void updateDis(String funcname) {

	}

	String currentItemKey = null;

	@Override
	public void setCurrentItemKey(String itemKey) {
		currentItemKey = itemKey;

	}

	@Override
	public void itemStateChanged(ItemEvent e) {

	}
}
