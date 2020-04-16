package nc.ui.ta.leave.balance.view;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.hr.utils.ResHelper;
import nc.uap.rbac.core.dataperm.DataPermissionFacade;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillModel;
import nc.ui.ta.leave.balance.model.LeaveBalanceModelDataManager;
import nc.ui.ta.wf.pub.TBMPubBillListView;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;

/**
 * @author 
 *
 */
public class LeaveBalanceListView extends TBMPubBillListView implements BillEditListener2
 {

	private static final long serialVersionUID = 1L;
	
	private IAppModelDataManager dataManager;
	
	@Override
	public void initUI() {
		super.initUI();
		BillPanelUtils.dealWithTablePopopMenu(getBillListPanel().getParentListPanel());
		getBillListPanel().addEditListener(this);
		getBillListPanel().getParentListPanel().addEditListener2(this);

//		//TODO:渲染时长,还有其他待加入
//		int colIndex = getBillListPanel().getHeadBillModel().getBodyColByKey(LeavehVO.SUMHOUR);
//		colIndex = getBillListPanel().getHeadTable().convertColumnIndexToView(colIndex);
//		if(colIndex>0)
//		{
//			TableColumn tc = getBillListPanel().getHeadTable().getColumnModel().getColumn(colIndex);
//			tc.setCellRenderer(new HowtimeCellRenderer(LeaveTypeCopyVO.TIMEITEMUNIT));
//		}
		
		//默认不显示转入转出时长
		getBillListPanel().getParentListPanel().hideTableCol(LeaveBalanceVO.TRANSLATEIN);
		getBillListPanel().getParentListPanel().hideTableCol(LeaveBalanceVO.TRANSLATEOUT);
		getBillListPanel().getParentListPanel().hideTableCol(LeaveBalanceVO.TRANSFLAG);
		setShowTotalLine(true);
		setBillListPanelShowTotalProp();
		getBillListPanel().getHeadItem(LeaveBalanceVO.LASTDAYORHOUR).setTatol(true);
		getBillListPanel().getHeadItem(LeaveBalanceVO.CURDAYORHOUR).setTatol(true);
		getBillListPanel().getHeadItem(LeaveBalanceVO.REALDAYORHOUR).setTatol(true);
		getBillListPanel().getHeadItem(LeaveBalanceVO.YIDAYORHOUR).setTatol(true);
		getBillListPanel().getHeadItem(LeaveBalanceVO.RESTDAYORHOUR).setTatol(true);
		getBillListPanel().getHeadItem(LeaveBalanceVO.FREEZEDAYORHOUR).setTatol(true);
		getBillListPanel().getHeadItem(LeaveBalanceVO.USEFULRESTDAYORHOUR).setTatol(true);
		getBillListPanel().getHeadItem(LeaveBalanceVO.CHANGELENGTH).setTatol(true);
		getBillListPanel().getHeadItem(LeaveBalanceVO.TRANSLATEIN).setTatol(true);
		getBillListPanel().getHeadItem(LeaveBalanceVO.TRANSLATEOUT).setTatol(true);
		getBillListPanel().getHeadBillModel().setNeedCalculate(true);
		//多选2020年3月13日 13:59:49 tank
	  	getBillListPanel().setMultiSelect(true);
	  	
	}
	@Override
	public void bodyRowChange(BillEditEvent e) {
		super.bodyRowChange(e);
		int newRow = e.getRow();
		if(newRow<0)return;

	}
	
	/**
	 * 取列表界面值
	 * @return
	 */
	public Object getBalanceValue(){
		if(billListPanel.getHeadTable().getCellEditor()!=null)
			billListPanel.getHeadTable().getCellEditor().stopCellEditing();
		return billListPanel.getHeadBillModel().getBodyValueChangeVOs(LeaveBalanceVO.class.getName());
	}
	
	@Override
    public void handleEvent(AppEvent event) {
	super.handleEvent(event);
	if (AppEventConst.MODEL_INITIALIZED.equalsIgnoreCase(event.getType())) {
	    // 若不是转工资则不显示余额结算期间
	    LeaveTypeCopyVO typevo = (LeaveTypeCopyVO) ((LeaveBalanceModelDataManager) getDataManager())
		    .getHierachicalModel().getSelectedData();
	    if (typevo != null) {
		if (TimeItemCopyVO.LEAVESETTLEMENT_MONEY == typevo.getLeavesettlement()) {
		    getBillListPanel().getParentListPanel().showTableCol(LeaveBalanceVO.SALARYYEAR);
		    getBillListPanel().getParentListPanel().showTableCol(LeaveBalanceVO.SALARYMONTH);
		} else {
		    getBillListPanel().getParentListPanel().hideTableCol(LeaveBalanceVO.SALARYYEAR);
		    getBillListPanel().getParentListPanel().hideTableCol(LeaveBalanceVO.SALARYMONTH);
		}
	    }
	    // 模型初始化后界面上的行都是ADD态，应当改为NORMAL态
	    int rowCount = getBillListPanel().getHeadBillModel().getRowCount();
	    if (rowCount <= 0)
		return;
	    getBillListPanel().getHeadBillModel().setRowState(0, rowCount - 1, BillModel.SELECTED);

	}
	if (AppEventConst.UISTATE_CHANGED.equalsIgnoreCase(event.getType())) {
	    boolean newEdit = getModel().getUiState() == UIState.EDIT;
	    getBillListPanel().setEnabled(newEdit);
	    // getBillListPanel().getHeadItem(LeaveBalanceVO.LASTDAYORHOUR).setEnabled(newEdit);
	    // getBillListPanel().getHeadItem(LeaveBalanceVO.LASTDAYORHOUR).setEdit(newEdit);
	    // getBillListPanel().getHeadItem(LeaveBalanceVO.REALDAYORHOUR).setEnabled(newEdit);
	    // getBillListPanel().getHeadItem(LeaveBalanceVO.REALDAYORHOUR).setEdit(newEdit);
	    // getBillListPanel().getHeadItem(LeaveBalanceVO.RESTDAYORHOUR).setEnabled(newEdit);
	    // getBillListPanel().getHeadItem(LeaveBalanceVO.RESTDAYORHOUR).setEdit(newEdit);
	    // getBillListPanel().getHeadItem(LeaveBalanceVO.YIDAYORHOUR).setEnabled(newEdit);
	    // getBillListPanel().getHeadItem(LeaveBalanceVO.YIDAYORHOUR).setEdit(newEdit);
	} else if (AppEventConst.SELECTION_CHANGED.equalsIgnoreCase(event.getType())) {
	    /*
	     * if(getModel().getUiState()==UIState.NOT_EDIT) { Object data =
	     * getModel().getSelectedData(); if(data==null)return; int row =
	     * getModel().getSelectedRow(); if(row<0)return;
	     * getBillListPanel().getHeadBillModel
	     * ().setBodyRowObjectByMetaData((
	     * CircularlyAccessibleValueObject)data, row);
	     * 
	     * 
	     * }
	     */
	}
	if (LeaveBalanceVO.TRANSLATEIN.equals(event.getType())) {
	    if ((Boolean) event.getContextObject()) {
		getBillListPanel().getParentListPanel().showTableCol(LeaveBalanceVO.TRANSLATEIN);
		getBillListPanel().getParentListPanel().showTableCol(LeaveBalanceVO.TRANSLATEOUT);
		getBillListPanel().getParentListPanel().showTableCol(LeaveBalanceVO.TRANSFLAG);
	    } else {
		getBillListPanel().getParentListPanel().hideTableCol(LeaveBalanceVO.TRANSLATEIN);
		getBillListPanel().getParentListPanel().hideTableCol(LeaveBalanceVO.TRANSLATEOUT);
		getBillListPanel().getParentListPanel().hideTableCol(LeaveBalanceVO.TRANSFLAG);
	    }
	}
	//if ("nc.ui.pubapp.uif2app.event.list.ListHeadRowStateChangeEvent".equalsIgnoreCase(event.getType())) {
	//if (!"Multi_Selection_Changed".equalsIgnoreCase(event.getType())) {
	    //Multi_Selection_Changed
	    int count = getBillListPanel().getHeadBillModel().getRowCount();
	    List<Integer> selectList = new ArrayList<>();
	    for (int i = 0; i < count; i++) {
		if (BillModel.SELECTED == getBillListPanel().getHeadBillModel().getRowState(i)) {
		    selectList.add(i);
		}
	    }
	    int[] selectArr = new int[selectList.size()];
	    for (int i = 0; i < selectList.size();i++) {
		if (selectList.get(i) >= 0) {
		    selectArr[i] = selectList.get(i);
		}
	    }
	    getModel().setSelectedOperaRowsWithoutEvent(selectArr);
	    getModel().setSelectedOperaRowsWithoutEvent(selectArr);
	//}
	

    }
	public IAppModelDataManager getDataManager() {
		return dataManager;
	}
	public void setDataManager(IAppModelDataManager dataManager) {
		this.dataManager = dataManager;
	}
	@Override
	public void afterEdit(BillEditEvent e) {
		super.afterEdit(e);
		if(e.getRow()<0||e.getKey()==null)return;
		String key = e.getKey();
		//如果修改了上期结余，实际享有，或者已休时长
		if(key.equals(LeaveBalanceVO.LASTDAYORHOUR)
//				||key.equals(LeaveBalanceVO.CURDAYORHOUR)
				||key.equals(LeaveBalanceVO.REALDAYORHOUR)
				||key.equals(LeaveBalanceVO.YIDAYORHOUR)
				)
		{
			LeaveBalanceVO balanceVO = (LeaveBalanceVO)getBillListPanel().getHeadBillModel().getBodyValueRowVO(e.getRow(), LeaveBalanceVO.class.getName());
			if(balanceVO.getLastdayorhour()==null)balanceVO.setLastdayorhour(UFDouble.ZERO_DBL);
			if(balanceVO.getRealdayorhour()==null)balanceVO.setRealdayorhour(UFDouble.ZERO_DBL);
			if(balanceVO.getYidayorhour()==null)balanceVO.setYidayorhour(UFDouble.ZERO_DBL);

			//计算本期结余
			UFDouble value = balanceVO.getLastdayorhour().add(balanceVO.getRealdayorhour()).sub(balanceVO.getYidayorhour());
			getBillListPanel().getHeadBillModel().setValueAt(value, e.getRow(), LeaveBalanceVO.RESTDAYORHOUR);
		}
		
	}
	@Override
	protected String[] getFloatItems() {
		return new String[]{LeaveBalanceVO.TRANSLATEIN,LeaveBalanceVO.TRANSLATEOUT,LeaveBalanceVO.REALDAYORHOUR,LeaveBalanceVO.RESTDAYORHOUR,LeaveBalanceVO.CURDAYORHOUR,LeaveBalanceVO.LASTDAYORHOUR,LeaveBalanceVO.YIDAYORHOUR,
				LeaveBalanceVO.CHANGELENGTH,LeaveBalanceVO.FREEZEDAYORHOUR,LeaveBalanceVO.USEFULRESTDAYORHOUR//V61新增冻结时长和可用时长
		};
	}
	
	@Override
	protected void resetDataFloat() {//设置转入转出时长的显示精度
		super.resetDataFloat();
//		if(StringUtils.isBlank(getModel().getContext().getPk_org()))
//			return;
//		AllParams allParams = ((TALoginContext)getModel().getContext()).getAllParams();
//		if(allParams == null)
//			return;
//		TimeRuleVO timeRuleVO = allParams.getTimeRuleVO();
//		TaUFDoubleConvert converter = new TaUFDoubleConvert(timeRuleVO);
//		BillItem inItem = getBillListPanel().getHeadItem(LeaveBalanceVO.TRANSLATEIN);
//		inItem.setConverter(converter);
//		BillItem outItem = getBillListPanel().getHeadItem(LeaveBalanceVO.TRANSLATEOUT);
//		outItem.setConverter(converter);
	}
	
	@Override
	public boolean beforeEdit(BillEditEvent e) {
		LeaveBalanceVO balanceVO = (LeaveBalanceVO) getModel().getSelectedData();
		if(balanceVO.isSettlement())//已结算的不能修改
			return false;
		//判断是否有修改权限
		TBMPsndocVO psndocVO = balanceVO.toTBMPsndocVO();
		boolean canEdit = DataPermissionFacade.isUserHasPermissionByMetaDataOperation(
				getModel().getContext().getPk_loginUser(), "60170psndoc", "EditLeaveBalance", 
				getModel().getContext().getPk_group(), psndocVO);
		if(!canEdit){
			ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("6017basedoc","06017basedoc1856")
					/*@res "您无权对选中的数据执行修改操作!"*/, getModel().getContext());
		}
		return canEdit;
	}
	
}
