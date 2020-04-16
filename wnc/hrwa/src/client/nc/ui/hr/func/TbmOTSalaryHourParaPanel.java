package nc.ui.hr.func;

import java.awt.LayoutManager;

import nc.bs.logging.Logger;
import nc.hr.utils.PairFactory;
import nc.ui.hr.itemsource.view.IParaPanel;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.vo.hr.func.FunctableItemVO;
import nc.vo.wa.pub.WaLoginContext;

public class TbmOTSalaryHourParaPanel extends UIPanel implements IParaPanel, IRefPanel {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 1695524485368585391L;
	private WaLoginContext context = null;
	private UILabel lblDateType = null;
	private UIComboBox cboDateType = null;

	public TbmOTSalaryHourParaPanel() {
		initialize();
	}

	public TbmOTSalaryHourParaPanel(LayoutManager p0) {
		super(p0);
	}

	public TbmOTSalaryHourParaPanel(LayoutManager p0, boolean p1) {
		super(p0, p1);
	}

	public TbmOTSalaryHourParaPanel(boolean p0) {
		super(p0);
	}

	@Override
	public void setContext(WaLoginContext context) {
		this.context = context;
	}

	@Override
	public WaLoginContext getContext() {
		return context;
	}

	@Override
	public void checkPara(int arg0) throws Exception {
		// TODO 自動產生的方法 Stub

	}

	@Override
	public void clearDis() {
	}

	@Override
	public Object[] getPara() throws Exception {
		String[] paras = new String[1];
		paras[0] = String.valueOf(this.getcmbItem().getSelectedIndex());
		return paras;
	}

	@Override
	public void setCurrentItemKey(String arg0) {
	}

	@Override
	public void setDatatype(int arg0) {
	}

	@Override
	public void updateDis(int index) {

	}

	@Override
	public void updateDis(FunctableItemVO[] items) {
	}

	@Override
	public void updateDis(String arg0) {
	}

	private void initialize() {
		try {
			setName("WaParaPanel");
			setLayout(null);
			setSize(240, 200);
			add(getLbldatetype(), getLbldatetype().getName());
			add(getcmbItem(), getcmbItem().getName());
		} catch (Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	private UILabel getLbldatetype() {
		if (lblDateType == null) {
			try {
				lblDateType = new UILabel();
				lblDateType.setName("lblLeaveType");
				lblDateType.setText("日曆天類型");

				lblDateType.setBounds(5, 30, 80, 22);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return lblDateType;
	}

	private UIComboBox getcmbItem() {
		if (cboDateType == null) {
			try {
				cboDateType = new UIComboBox();
				cboDateType.setName("cmbItem");
				cboDateType.setBounds(100, 30, 140, 22);
				cboDateType.setTranslate(true);

				String[] ml = new String[] { "平日", "休息日", "例假日", "國假", "事件假" };
				String[] mlDefault = { "平日", "休息日", "例假日", "國假", "事件假" };

				PairFactory mPairFactory = new PairFactory(ml, mlDefault);

				cboDateType.addItems(mPairFactory.getAllConstEnums());
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return cboDateType;
	}

	private void handleException(Throwable exception) {
		Logger.error(exception.getMessage());
	}

}
