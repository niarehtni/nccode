package nc.ui.hr.func;

import java.awt.LayoutManager;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.PairFactory;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ITimeItemQueryService;
import nc.pubitf.para.SysInitQuery;
import nc.ui.hr.itemsource.view.IParaPanel;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.vo.hr.func.FunctableItemVO;
import nc.vo.pub.BusinessException;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.wa.pub.WaLoginContext;

public class TbmOTLeaveBalanceParaPanel extends UIPanel implements IParaPanel, IRefPanel {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 1695524485368585391L;
	private WaLoginContext context = null;
	private UILabel lblLeaveType = null;
	private UIComboBox ivjcmbItem = null;
	private UILabel lblValueColumn = null;
	private UIComboBox ivjcmbFunc = null;

	public TbmOTLeaveBalanceParaPanel() {
		initialize();
	}

	public TbmOTLeaveBalanceParaPanel(LayoutManager p0) {
		super(p0);
	}

	public TbmOTLeaveBalanceParaPanel(LayoutManager p0, boolean p1) {
		super(p0, p1);
	}

	public TbmOTLeaveBalanceParaPanel(boolean p0) {
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
		// TODO �ԄӮa���ķ��� Stub

	}

	@Override
	public void clearDis() {
	}

	@Override
	public Object[] getPara() throws Exception {
		String[] paras = new String[2];
		paras[0] = ((LeaveTypeCopyVO) getcmbItem().getSelectedItem()).getTimeitemcode();
		int index = getcmbFunc().getSelectedIndex();
		paras[1] = String.valueOf(index);
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
		if (this.getContext() == null) {
			return;
		}

		this.getcmbItem().removeAllItems();

		String otLeaveType = "";
		String extraLeaveType = "";
		try {
			otLeaveType = SysInitQuery.getParaString(this.getContext().getPk_org(), "TWHRT08");// �Ӱ��a��ָ���لe
			extraLeaveType = SysInitQuery.getParaString(this.getContext().getPk_org(), "TWHRT10"); // ����a���씵ָ���لe
		} catch (BusinessException e) {
			Logger.error("ϵ�y����ȡֵ�e�`��" + e.getMessage());
		}

		ITimeItemQueryService timeitemService = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		try {
			LeaveTypeCopyVO[] typeVOs = (LeaveTypeCopyVO[]) timeitemService.queryLeaveCopyTypesByOrg(this.getContext()
					.getPk_org());

			if (typeVOs != null && typeVOs.length > 0) {
				for (LeaveTypeCopyVO typevo : typeVOs) {
					if (typevo.getPk_timeitemcopy().equals(otLeaveType)
							|| typevo.getPk_timeitemcopy().equals(extraLeaveType)) {
						this.getcmbItem().addItem(typevo);
					}
				}
			}
		} catch (BusinessException e) {
			handleException(e);
		}
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
			add(getLblleavetype(), getLblleavetype().getName());
			add(getcmbItem(), getcmbItem().getName());
			add(getLblvaluecolumn(), getLblvaluecolumn().getName());
			add(getcmbFunc(), getcmbFunc().getName());
		} catch (Throwable ivjExc) {
			handleException(ivjExc);
		}

		initData();
	}

	public void initData() {

	}

	private UILabel getLblleavetype() {
		if (lblLeaveType == null) {
			try {
				lblLeaveType = new UILabel();
				lblLeaveType.setName("lblLeaveType");
				lblLeaveType.setText(ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0248"));

				lblLeaveType.setBounds(5, 30, 80, 22);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return lblLeaveType;
	}

	private UIComboBox getcmbItem() {
		if (ivjcmbItem == null) {
			try {
				ivjcmbItem = new UIComboBox();
				ivjcmbItem.setName("cmbItem");
				ivjcmbItem.setBounds(100, 30, 140, 22);
				ivjcmbItem.setTranslate(true);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjcmbItem;
	}

	private UILabel getLblvaluecolumn() {
		if (lblValueColumn == null) {
			try {
				lblValueColumn = new UILabel();
				lblValueColumn.setName("lblValueColumn");
				lblValueColumn.setText("��λ");

				lblValueColumn.setBounds(5, 60, 80, 22);
				lblValueColumn.setVisible(true);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return lblValueColumn;
	}

	private UIComboBox getcmbFunc() {
		if (ivjcmbFunc == null) {
			try {
				ivjcmbFunc = new UIComboBox();
				ivjcmbFunc.setName("cmbFunc");
				ivjcmbFunc.setBounds(100, 60, 140, 22);
				ivjcmbFunc.setVisible(true);
				ivjcmbFunc.setTranslate(true);
				ivjcmbFunc.setTranslate(true);

				String[] ml = new String[] { "����", "����", "�Y�N", "���Y", "����" };
				String[] mlDefault = { "����", "����", "�Y�N", "���Y", "����" };

				PairFactory mPairFactory = new PairFactory(ml, mlDefault);

				ivjcmbFunc.addItems(mPairFactory.getAllConstEnums());
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjcmbFunc;
	}

	private void handleException(Throwable exception) {
		Logger.error(exception.getMessage());
	}

}
