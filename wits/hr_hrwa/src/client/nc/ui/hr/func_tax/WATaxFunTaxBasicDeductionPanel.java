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
 * �������ÿ۳��� ����˰���ȡ�������ÿ۳���
 * 
 * @author xuhw
 */
public class WATaxFunTaxBasicDeductionPanel extends UIPanel implements
		ItemListener, IParaPanel, IRefPanel, IFormulaConst {
	private static final long serialVersionUID = -5415459347134279156L;

	private int datatype = 1;// ������Ҫ���ص��������ͣ�Ĭ��������
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
	 * WaParaPanel ������ע�⡣
	 */
	public WATaxFunTaxBasicDeductionPanel() {
		super();
		initialize();
	}

	/**
	 * ���������Ϸ���У��
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
	 * ���غ����Ĳ�������
	 */
	public String[] getPara() throws Exception {

		return null;

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

	public void initData() {
	}

	/**
	 * ��ʼ���ࡣ
	 */
	/* ���棺�˷������������ɡ� */
	private void initialize() {
		setName("WaParaPanel");
		setLayout(null);
		setSize(240, 200);
	}

	public void setDatatype(int newDatatype) {
		datatype = newDatatype;
	}

	/**
	 * ����ѡ��ĺ������²������ý���
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
