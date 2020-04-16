package nc.ui.wa.formular;

import java.util.Collection;

import javax.swing.SwingConstants;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.pub.BusinessException;
import nc.vo.ta.timeitem.LeaveTypeVO;

/**
 * #21266 按日合计休假時數參數面板
 * 
 * @author ssx
 * @date 2019-12-23
 */
public class TermLeaveHourFunctionEditor extends WaAbstractFunctionEditor {

	private static final long serialVersionUID = 6414923710103945313L;

	private UILabel itemlabel = null;
	private UIComboBox itemCBox = null;

	@Override
	public void setModel(AbstractUIAppModel model) {
		super.setModel(model);
		initData();
	}

	public void initData() {
		try {
			LeaveTypeVO[] items = getItems(getContext().getPk_org());
			getItemCBox().addItems(items);
		} catch (Exception e) {
			handleException(e);
		}
	}

	/**
	 * WaParaPanel 构造子注解。
	 */
	public TermLeaveHourFunctionEditor() {
		super();
		initialize();
	}

	private static final String funcname = "@" + ResHelper.getString("6013commonbasic", "06013commonbasic0282") + "@";

	// "taxRate";
	@Override
	public String getFuncName() {
		return funcname;
	}

	/**
	 * 初始化类。
	 */
	/* 警告：此方法将重新生成。 */
	private void initialize() {
		try {
			setLayout(null);
			setSize(300, 180);
			setTitle("請選擇參數");

			add(getItemLabel(), getItemLabel().getName());
			add(getItemCBox(), getItemCBox().getName());

			add(getOkButton(), getOkButton().getName());
			add(getCancelButton(), getCancelButton().getName());

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}

		initConnection();

	}

	private UILabel getItemLabel() {
		if (itemlabel == null) {
			try {
				itemlabel = new UILabel();
				itemlabel.setName("itemlabel");
				itemlabel.setText(ResHelper.getString("6013commonbasic", "06013commonbasic0273")/*
																								 * @
																								 * res
																								 * "休假类别"
																								 */);
				itemlabel.setBounds(10, 50, 100, 22);
				itemlabel.setHorizontalAlignment(SwingConstants.RIGHT);
			} catch (java.lang.Throwable labExc) {
				handleException(labExc);
			}
		}
		return itemlabel;
	}

	private UIComboBox getItemCBox() {
		if (itemCBox == null) {
			try {
				itemCBox = new UIComboBox();
				itemCBox.setName("itemCBox");
				itemCBox.setBounds(120, 50, 150, 22);
				itemCBox.setTranslate(true);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return itemCBox;
	}

	public String[] getPara() {
		String[] paras = new String[3];

		LeaveTypeVO itemVO = (LeaveTypeVO) getItemCBox().getSelectdItemValue();
		// 考勤月报项目主键
		paras[0] = "\"" + itemVO.getPk_timeitem().trim() + "\"";

		return paras;
	}

	private LeaveTypeVO[] getItems(String pk_org) throws BusinessException {
		IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		String sql = "select tbm_timeitem.timeitemcode,tbm_timeitem.timeitemname,tbm_timeitem.pk_timeitem,tbm_timeitem.itemtype,tbm_timeitemcopy.pk_timeitemcopy,tbm_timeitemcopy.pk_org,tbm_timeitemcopy.timeitemunit,tbm_timeitemcopy.leavesetperiod "
				+ "from tbm_timeitem inner join tbm_timeitemcopy on tbm_timeitem.pk_timeitem = tbm_timeitemcopy.pk_timeitem "
				+ "and tbm_timeitemcopy.pk_org = '"
				+ pk_org
				+ "' and islactation = 'N' and (tbm_timeitem.itemtype = 0 and tbm_timeitemcopy.enablestate = 2)";
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Collection<LeaveTypeVO> c = (Collection) query.executeQuery(sql, new BeanListProcessor(LeaveTypeVO.class));
		LeaveTypeVO[] leaveTypeVo = (LeaveTypeVO[]) c.toArray(new LeaveTypeVO[0]);

		return leaveTypeVo;

	}

	@Override
	public boolean checkPara(int dataType) {
		return true;
	}

	public UIButton getOkButton() {
		if (okButton == null) {
			okButton = new UIButton(ResHelper.getString("common", "UC001-0000044"));
			okButton.setBounds(48, 145, 80, 22);
		}
		return okButton;
	}

	public UIButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new UIButton(ResHelper.getString("common", "UC001-0000008"));
			cancelButton.setBounds(172, 145, 80, 22);
		}
		return cancelButton;
	}
}