package nc.ui.wa.paydata.view.dialog;

import java.awt.Container;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JComponent;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.validation.ValidationException;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IHRWAActionCode;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.IPaydataQueryService;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.hr.util.HrDataPermHelper;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.bill.AbstractBillItemRowDigitsAdapter;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillModelCellEditableController;
import nc.ui.pub.bill.BillScrollPane.BillTable;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.action.BillTableLineAction;
import nc.ui.uif2.editor.value.AbstractComponentValueAdapter;
import nc.ui.uif2.editor.value.BillCardPanelBodyVOValueAdapter;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.DefaultAppModelDataManager;
import nc.ui.wa.paydata.model.WadataAppDataModel;
import nc.ui.wa.ref.WaClassFomulaItemRefModel;
import nc.ui.wa.ref.WaPaydataPsnRefModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletHeadVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.FromEnumVO;
import nc.vo.wa.paydata.DataSVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaState;

public class DefaultSepPsnChangeDialog extends WaAbstractCardPanelDialog {

	public Set<WaState> wadataSatateSet = null;
	protected WaClassItemVO classItemVO;
	protected DefaultAppModelDataManager modelDataManager = null;
	protected BillCardPanelBodyVOValueAdapter adapter = null;
	protected WaLoginVO waLoginVO = null;

	public DefaultSepPsnChangeDialog(Container parent,
			DefaultAppModelDataManager modelDataManager, WaLoginVO waLoginVO) {
		super(parent);
		this.modelDataManager = modelDataManager;
		this.waLoginVO = waLoginVO;
		BillCardPanel billCardPanel = new BillCardPanel() {
			@Override
			public ArrayList<Action> getDefaultTabActions(int pos) {

				ArrayList<Action> acts = new ArrayList<Action>();

				if (pos == IBillItem.BODY) {
					acts.add(BillTableLineAction.getInstance(this,
							BillTableLineAction.ADDLINE));
					acts.add(BillTableLineAction.getInstance(this,
							BillTableLineAction.INSERTLINE));
					acts.add(BillTableLineAction.getInstance(this,
							BillTableLineAction.DELLINE));
				}
				return acts;
			}
		};
		BillData billData = new BillData(getBillTempletVO());
		billCardPanel.setBillData(billData);
		// 已提交方案显示浏览态
		if (getUnableDataStateSet().contains(getContext().getWaState())) {
			billCardPanel.getBodyTabbedPane().removeTableActions();
			billCardPanel.setEnabled(false);
		} else {
			billCardPanel.addDefaultTabAction(IBillItem.BODY);
			billCardPanel.setEnabled(true);
		}

		billCardPanel.setBodyMenuShow(false);
		BillModel billModel = billCardPanel.getBillModel();
		billModel.addDecimalListener(new AbstractBillItemRowDigitsAdapter(
				billModel, DataSVO.IFLDDECIMAL, new String[] {
						DataSVO.CACULATEVALUE, DataSVO.VALUE }) {
			@Override
			public int getDecimalFromSource(int row, Object sourcePkValue) {
				return new Integer(sourcePkValue.toString());
			}
		});

		billModel
				.setCellEditableController(new BillModelCellEditableController() {
					@Override
					public boolean isCellEditable(boolean blIsEditable,
							int iRowIndex, String strItemKey) {
						return isHeadCellEditable(blIsEditable, iRowIndex,
								strItemKey);
					}
				});
		setBillCardPanel(billCardPanel);

		// 已提交方案显示浏览态
		if (getUnableDataStateSet().contains(getContext().getWaState())) {
			getBtnCancel().setVisible(false);
			getBtnOk()
					.setText(
							ResHelper.getString("60130paydata",
									"060130paydata0421")/* @res "关闭" */);
		}

		initUI();
		initRefModel(DataSVO.ITEMNAME, getItemModel());
		initRefModel(DataSVO.CLERKCODE, getPsnModel());
		((UIRefPane) getBillCardPanel().getBodyItem(DataSVO.VALUE)
				.getComponent()).addValueChangedListener(this);

		// 20151223 shenliangc NCdp205564067 薪资未屏蔽右键菜单和表格中回车加行的功能的节点
		BillPanelUtils.disabledRightMenuAndAutoAddLine(getBillCardPanel());
	}

	private AbstractRefModel getItemModel() {
		AbstractRefModel model = new WaClassFomulaItemRefModel(getContext());
		return model;
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2012-8-2 下午3:33:13<br>
	 * 
	 * @see nc.ui.wa.paydata.view.dialog.WaAbstractCardPanelDialog#getComponentValueManager()
	 * @author daicy
	 ****************************************************************************/
	public AbstractComponentValueAdapter getComponentValueManager() {
		if (adapter == null) {
			adapter = new BillCardPanelBodyVOValueAdapter();
			adapter.setBodyVOName(DataSVO.class.getName());
			adapter.setComponent(getBillCardPanel());
		}
		return adapter;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2012-8-2 下午3:49:39<br>
	 * 
	 * @return
	 * @author daicy
	 ***************************************************************************/
	public BillManageModel getModel() {
		return null;
	}

	@Override
	public void afterEdit(BillEditEvent e) {
		int rowIndex = e.getRow();
		String pk_wadata = getBodyCellValue(rowIndex, DataSVO.PK_WA_DATA)
				.toString();
		try {
			DataVO vo = NCLocator.getInstance()
					.lookup(IPaydataQueryService.class)
					.getDataVOByPk(pk_wadata);
			ValidationException ex = HrDataPermHelper.checkDataPermission(
					IHRWADataResCode.WADATA, IHRWAActionCode.SpecialPsnAction,
					IHRWAActionCode.SpecialPsnAction, vo, getContext());
			HrDataPermHelper.dealValidationException(ex);
		} catch (BusinessException e1) {
			setBodyCellValue(null, rowIndex, DataSVO.VALUE);
			MessageDialog.showErrorDlg(this, null, e1.getMessage());
		}
	}

	public WaClassItemVO getClassItemVO() {
		if (classItemVO == null) {
			classItemVO = new WaClassItemVO();
			classItemVO.setIitemtype(0);
			classItemVO.setIflddecimal(4);
			classItemVO.setIfldwidth(12);
		}
		return classItemVO;
	}

	public WaLoginContext getContext() {
		return (WaLoginContext) this.getModel().getContext();
	}

	protected boolean isHeadCellEditable(boolean blIsEditable, int iRowIndex,
			String strItemKey) {
		if (!blIsEditable) {
			return blIsEditable;
		}
		if (getUnableDataStateSet().contains(getContext().getWaState())) {
			return false;
		}
		// 人员审核则全部不可编辑
		if (getBodyCellValue(getSelectedRow(), DataSVO.CHECKFLAG) != null) {
			UFBoolean checkflag = UFBoolean.valueOf(getBodyCellValue(
					getSelectedRow(), DataSVO.CHECKFLAG).toString());
			if (checkflag.booleanValue()) {
				return false;
			}
		}

		// 没有人员， 则项目不能编辑
		if (getBodyCellValue(getSelectedRow(), DataSVO.PK_WA_DATA) == null) {
			if (strItemKey.equals(DataSVO.ITEMNAME)) {
				return false;
			}
		}
		// 没有项目则金额不能编辑
		if (getBodyCellValue(getSelectedRow(), DataSVO.PK_WA_CLASSITEM) == null) {
			if (strItemKey.equals(DataSVO.VALUE)) {
				return false;
			}
		}
		return blIsEditable;
	}

	public void mouseAdd() {
		DataSVO dataSVO = convert2DataSVO();
		if (dataSVO == null) {
			return;
		}
		getBillCardPanel().getBodyPanel().addLine();
		BillTable billTable = getBillCardPanel().getBodyPanel().getTable();
		int row = billTable.getRowCount() - 1;

		billTable.setRowSelectionInterval(row, row);
		getBillCardPanel().getBillModel().setBodyRowVO(dataSVO, row);

	}

	/**
	 * @author zhangg on 2009-11-30
	 * @param classItemVO
	 *            the classItemVO to set
	 */
	public void setClassItemVO(WaClassItemVO classItemVO) {
		this.classItemVO = classItemVO;
	}

	@Override
	protected JComponent createCenterPanel() {
		return null;
	}

	private BillTempletBodyVO getBaseBodyVO() {
		String tabCode = "wa_datas";
		BillTempletBodyVO templetBodyVO = getbaseBodyVO(tabCode);
		templetBodyVO.setWidth(100);
		templetBodyVO.setPos(IBillItem.BODY);
		return templetBodyVO;
	}

	@Override
	public BillTempletVO getBillTempletVO() {
		int iShowOrder = 0;

		BillTempletVO billTempletVO = new BillTempletVO();

		ArrayList<BillTempletBodyVO> arrListBodyVO = new ArrayList<BillTempletBodyVO>();

		BillTempletBodyVO templetBodyVO = getBaseBodyVO();
		templetBodyVO.setDefaultshowname(DataSVO.PK_WA_DATAS);
		templetBodyVO.setItemkey(DataSVO.PK_WA_DATAS);
		templetBodyVO.setShowflag(false);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		templetBodyVO = getBaseBodyVO();
		templetBodyVO.setDefaultshowname("ts");
		templetBodyVO.setItemkey("ts");
		templetBodyVO.setShowflag(false);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		templetBodyVO = getBaseBodyVO();
		templetBodyVO.setDefaultshowname(DataSVO.CHECKFLAG);
		templetBodyVO.setItemkey(DataSVO.CHECKFLAG);
		templetBodyVO.setDatatype(IBillItem.BOOLEAN);
		templetBodyVO.setShowflag(false);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		// 人员编码
		templetBodyVO = getBaseBodyVO();
		templetBodyVO.setDefaultshowname(DataSVO.PK_WA_DATA);
		templetBodyVO.setItemkey(DataSVO.PK_WA_DATA);
		templetBodyVO.setShowflag(false);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		// 薪资项目
		templetBodyVO = getBaseBodyVO();
		templetBodyVO.setDefaultshowname(DataSVO.PK_WA_CLASSITEM);
		templetBodyVO.setItemkey(DataSVO.PK_WA_CLASSITEM);
		templetBodyVO.setShowflag(false);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		// 人员编码
		templetBodyVO = getBaseBodyVO();
		templetBodyVO.setDefaultshowname(ResHelper.getString("60130adjapprove",
				"160130adjapprove0009")/* @res "员工号" */);
		templetBodyVO.setItemkey(DataSVO.CLERKCODE);
		templetBodyVO.setDatatype(IBillItem.UFREF);
		templetBodyVO.setEditflag(true);
		templetBodyVO.setNullflag(Boolean.TRUE);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		// 人员姓名
		templetBodyVO = getBaseBodyVO();
		templetBodyVO.setDefaultshowname(ResHelper.getString("common",
				"UC000-0001403")/* @res "姓名" */);
		templetBodyVO.setItemkey(DataSVO.PSNNAME);
		templetBodyVO.setNullflag(Boolean.FALSE);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		// 薪资项目
		templetBodyVO = getBaseBodyVO();
		templetBodyVO.setDefaultshowname(ResHelper.getString("common",
				"UC000-0003385")/* @res "薪资项目" */);
		templetBodyVO.setItemkey(DataSVO.ITEMNAME);
		templetBodyVO.setDatatype(IBillItem.UFREF);
		templetBodyVO.setEditflag(true);
		templetBodyVO.setNullflag(Boolean.TRUE);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		// 薪资金额
		templetBodyVO = getBaseBodyVO();
		templetBodyVO.setDefaultshowname(ResHelper.getString("60130paydata",
				"060130paydata0422")/* @res "公式金额" */);
		templetBodyVO.setDatatype(IBillItem.DECIMAL);
		templetBodyVO.setInputlength(26);
		String refType = "3,,";
		// billTempletBodyVO.setDatatype(IBillItem.DECIMAL);
		//
		// // 数值型设置小数位数
		// int digits = item.getIflddecimal();
		//
		// if(digits!=0){
		// billTempletBodyVO.setInputlength(item.getIfldwidth() + 1 + digits);
		// }
		// String refType = Math.abs(digits) + "";
		// billTempletBodyVO.setReftype(refType);
		templetBodyVO.setReftype(refType);
		templetBodyVO.setItemkey(DataSVO.CACULATEVALUE);
		templetBodyVO.setEditflag(false);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		// 薪资金额
		templetBodyVO = getBaseBodyVO();
		templetBodyVO.setDefaultshowname(ResHelper.getString("60130paydata",
				"060130paydata0423")/* @res "调整金额" */);
		templetBodyVO.setDatatype(IBillItem.DECIMAL);
		templetBodyVO.setInputlength(26);
		templetBodyVO.setReftype(refType);
		templetBodyVO.setItemkey(DataSVO.VALUE);
		templetBodyVO.setEditflag(true);
		templetBodyVO.setNullflag(Boolean.TRUE);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		// 调整原因
		templetBodyVO = getBaseBodyVO();
		templetBodyVO.setDefaultshowname(ResHelper.getString("60130paydata",
				"060130paydata0424")/* @res "调整原因" */);
		templetBodyVO.setInputlength(256);
		templetBodyVO.setItemkey(DataSVO.NOTES);
		templetBodyVO.setEditflag(true);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		// 在离职结薪节点，个别调整时，还可以将数据带入下期:应该去掉【带入下期】功能 NCdp203991699
		// 多次发薪无法带入下期
		// if( isNotSubClass () ){
		// // 带入下期
		// templetBodyVO = getBaseBodyVO();
		// templetBodyVO.setDefaultshowname(ResHelper.getString("60130paydata","060130paydata0425")/*@res
		// "带入下期"*/);
		// templetBodyVO.setItemkey(DataSVO.TO_NEXT);
		// templetBodyVO.setDatatype(IBillItem.BOOLEAN);
		// templetBodyVO.setEditflag(true);
		// templetBodyVO.setShoworder(iShowOrder++);
		// arrListBodyVO.add(templetBodyVO);
		// }

		// 输入长度
		templetBodyVO = getBaseBodyVO();
		templetBodyVO.setDefaultshowname("输入小数位数"/* -=notranslate=- */);
		templetBodyVO.setDatatype(IBillItem.INTEGER);
		templetBodyVO.setInputlength(20);
		templetBodyVO.setItemkey(DataSVO.IFLDDECIMAL);
		templetBodyVO.setEditflag(true);
		templetBodyVO.setShowflag(false);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		billTempletVO.setChildrenVO(arrListBodyVO
				.toArray(new BillTempletBodyVO[0]));
		// 解决headVO是null报错
		billTempletVO.setParentVO(new BillTempletHeadVO(this.getClass()
				.toString()));

		return billTempletVO;
	}

	@Override
	public String getTitle() {
		return ResHelper.getString("60130paydata", "060130paydata0426")/*
																		 * @res
																		 * "个别人员薪资发放项目值调整"
																		 */;
	}

	public Set<WaState> getUnableDataStateSet() {
		if (wadataSatateSet == null) {
			wadataSatateSet = new HashSet<WaState>();
			wadataSatateSet.add(WaState.CLASS_CHECKED_WITHOUT_PAY);
			wadataSatateSet.add(WaState.CLASS_CHECKED_WITHOUT_APPROVE);
			wadataSatateSet.add(WaState.CLASS_IN_APPROVEING);
			wadataSatateSet.add(WaState.CLASS_IS_APPROVED);
			wadataSatateSet.add(WaState.CLASS_ALL_PAY);
			wadataSatateSet.add(WaState.CLASS_MONTH_END);
		}
		return wadataSatateSet;
	}

	@Override
	public void initUI() {
		super.initUI();
		setSize(738, 450);
	}

	@Override
	public void closeOK() {

		super.closeOK();
	}

	protected AbstractRefModel getPsnModel() {
		AbstractRefModel model = new WaPaydataPsnRefModel(getContext());
		return model;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2012-8-2 下午4:20:12<br>
	 * 
	 * @author daicy
	 * @return the modelDataManager
	 ***************************************************************************/
	public DefaultAppModelDataManager getModelDataManager() {
		return modelDataManager;
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		int rowIndex = getSelectedRow();
		try {
			Object source = event.getSource();
			if (source instanceof UIRefPane) {

				UIRefPane refPane = (UIRefPane) source;
				if (refPane.getRefModel() instanceof WaClassFomulaItemRefModel) {
					String itemkey = refPane.getRefModel()
							.getValue("wa_item.itemkey").toString();
					Object value = refPane.getRefModel().getValue(
							"wa_item.iflddecimal");
					DataVO dataVO = ((WadataAppDataModel) getModel())
							.getPaydataService().getDataVOByPk(
									getBodyCellValue(rowIndex,
											DataSVO.PK_WA_DATA).toString());

					// 2016-11-28 zhousze 薪资加密：处理明细对比薪资数据解密 begin
					DataVO[] vos = { dataVO };
					dataVO = SalaryDecryptUtil.decrypt4Array(vos)[0];
					// end

					setBodyCellValue(refPane.getRefPK(), rowIndex,
							DataSVO.PK_WA_CLASSITEM);
					setBodyCellValue(value, rowIndex, DataSVO.IFLDDECIMAL);

					setBodyCellValue(
							dataVO == null ? null
									: dataVO.getAttributeValue(itemkey),
							rowIndex, DataSVO.CACULATEVALUE);

					setBodyCellValue(null, rowIndex, DataSVO.VALUE);
					// getBillCardPanel().getBodyItem(DataSVO.VALUE).setDecimalDigits((Integer)
					// value);
					// getBillCardPanel().getBodyItem(DataSVO.CACULATEVALUE).setDecimalDigits((Integer)
					// value);

				} else if (refPane.getRefModel() instanceof WaPaydataPsnRefModel) {
					Object value = refPane.getRefModel().getValue(
							"bd_psndoc.name");

					setBodyCellValue(refPane.getRefPK(), rowIndex,
							DataSVO.PK_WA_DATA);
					setBodyCellValue(value, rowIndex, DataSVO.PSNNAME);
					setBodyCellValue(UFBoolean.FALSE, rowIndex,
							DataSVO.CHECKFLAG);
					setBodyCellValue(null, rowIndex, DataSVO.PK_WA_CLASSITEM);
					setBodyCellValue(null, rowIndex, DataSVO.ITEMNAME);
					setBodyCellValue(null, rowIndex, DataSVO.CACULATEVALUE);
					setBodyCellValue(null, rowIndex, DataSVO.IFLDDECIMAL);
					setBodyCellValue(null, rowIndex, DataSVO.VALUE);
				}
			}

		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}
	}

	/**
	 * 快捷增加
	 */
	public DataSVO convert2DataSVO() {
		// 选择的人员
		DataVO dataVO = (DataVO) getModel().getSelectedData();
		// 选择的项目
		WaClassItemVO classItemVO = ((WadataAppDataModel) getModel())
				.getSelectedItemVO();
		if (dataVO == null || classItemVO == null
				|| !FromEnumVO.FORMULA.equalsValue(classItemVO.getIfromflag())) {
			return null;
		}
		DataSVO dataSVO = new DataSVO();
		dataSVO.setPk_wa_data(dataVO.getPk_wa_data());
		dataSVO.setClerkcode(dataVO.getAttributeValue(DataSVO.CLERKCODE)
				.toString());
		dataSVO.setPsnname(dataVO.getAttributeValue(DataSVO.PSNNAME).toString());
		dataSVO.setPk_wa_classitem(classItemVO.getPk_wa_classitem());
		dataSVO.setIflddecimal(classItemVO.getIflddecimal());
		dataSVO.setCaculatevalue(new UFDouble(dataVO.getAttributeValue(
				classItemVO.getItemkey()).toString()));
		dataSVO.setItemname(classItemVO.getMultilangName());
		dataSVO.setCheckflag(UFBoolean.FALSE);

		return dataSVO;
	}

}