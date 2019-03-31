package nc.ui.hr.func_tax;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import nc.bs.logging.Logger;
import nc.ui.hr.func.IRefPanel;
import nc.ui.hr.itemsource.view.IParaPanel;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.vo.hr.formula.IFormulaConst;
import nc.vo.hr.func.FunctableItemVO;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 专项附加费用扣除
 * 获取专项附件费用扣除额(参数：种类 ，全部/子女教育/继续教育/住房贷款利息/住房租金/赡养老人)
 * 	String[] ml = {"0","1","2","3","4","5"};
		String[] mlDefault = new String[]{"全部","子女教育","继续教育","住房贷款利息","住房租金","赡养老人"};
		
 * @author xuhw
 */
public class WATaxFunTaxSpecialAdditionaDeductionPanel extends UIPanel implements ItemListener,
		IParaPanel, IRefPanel, IFormulaConst {
	private static final long serialVersionUID = -5415459347134279156L;

	private UILabel ItemText = null;
	private UIComboBox isIncludeComBox = null;// 是否包含
	private int datatype = 1;// 函数需要返回的数据类型，默认数字型
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
	public WATaxFunTaxSpecialAdditionaDeductionPanel() {
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
			ItemText.setText("类型");
			ItemText.setBounds(10, 15, 80, 22);

		}
		return ItemText;
	}
   
	/**
	 * 是否包含下拉
	 * 
	 * @return
	 */
	private nc.ui.pub.beans.UIComboBox getincludecmbFunc() {
		if (isIncludeComBox == null) {
			isIncludeComBox = new nc.ui.pub.beans.UIComboBox();
			isIncludeComBox.setName("isIncludeComBox");
			isIncludeComBox.setBounds(93, 15, 147, 22);
			String[] ml = new String[7];
//			子女教育/继续教育/住房贷款利息/住房租金/赡养老人
			ml[0] = "全部";
			ml[1] = "子女教育";
			ml[2] = "继续教育";
			ml[3] = "住房贷款利息";
			ml[4] = "住房租金";
			ml[5] = "赡养老人";
			ml[6] = "大病医疗";

			String[] mlDefault = new String[] { "全部", "子女教育" ,"继续教育","住房贷款利息","住房租金","赡养老人","大病医疗"};
			nc.hr.utils.PairFactory mPairFactory = new nc.hr.utils.PairFactory(
					ml, mlDefault);
			isIncludeComBox.addItems(mPairFactory.getAllConstEnums());
		}
		return isIncludeComBox;
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
 
	public String[] getPara() throws Exception {
		String[] paras = new String[1];

		// 项目 //判断薪资项目数据类型
 
		paras[0] = getincludecmbFunc().getSelectedIndex() + "";

		return paras;

	}

 
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
		add(getUILabel1(), getUILabel1().getName());
		add(getincludecmbFunc(), getincludecmbFunc().getName());
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
