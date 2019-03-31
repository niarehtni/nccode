package nc.ui.hr.func_tax;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import nc.bs.logging.Logger;
import nc.ui.hr.func.IRefPanel;
import nc.ui.hr.itemsource.view.IParaPanel;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIPanel;
import nc.vo.hr.formula.IFormulaConst;
import nc.vo.hr.func.FunctableItemVO;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 基本费用扣除额 根据税表获取基本费用扣除额
 * 
 * @author xuhw
 */
public class WATaxFunTaxBasicDeductionPanel extends UIPanel implements
		ItemListener, IParaPanel, IRefPanel, IFormulaConst {
	private static final long serialVersionUID = -5415459347134279156L;

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
	public WATaxFunTaxBasicDeductionPanel() {
		super();
		initialize();
	}

	/**
	 * 函数参数合法性校验
	 */
	public void checkPara(int dataType) throws Exception {
		try {

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

		return null;

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
	}

	/**
	 * 初始化类。
	 */
	/* 警告：此方法将重新生成。 */
	private void initialize() {
		setName("WaParaPanel");
		setLayout(null);
		setSize(240, 200);
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
