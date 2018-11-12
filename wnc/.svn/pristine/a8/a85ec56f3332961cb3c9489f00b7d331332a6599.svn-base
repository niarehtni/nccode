package nc.ui.ta.leave.pf.view;

import java.util.Arrays;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.hr.frame.IHrBillCode;
import nc.itf.ta.ILeaveAppInfoDisplayer;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.itf.ta.PeriodServiceFacade;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.ta.leave.pf.model.LeaveAppModel;
import nc.ui.ta.period.utils.PeriodUtils;
import nc.ui.ta.psndoc.ref.TBMPsndocRefModel;
import nc.ui.ta.wf.pub.TBMPubBillCardForm;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeaveConst;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.overtime.OvertimehVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.pub.TALoginContext;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author wwb
 * 
 */
public class LeaveCardForm extends TBMPubBillCardForm implements BillCardBeforeEditListener {
	private static final long serialVersionUID = 1L;
	
	private Map<String, String[]> periodMap;
	
	private PeriodVO curPeriodVO;
	
	private boolean isAgentPsnNull;//工作交接人是否可空
	private boolean isWorkProcessNull;//工作交接情况是否可空
	private boolean isRelatetelNull ;//假期联系电话是否可空
	
	private void addListeners() {
		getBillCardPanel().addEditListener(this);
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
		getModel().addAppEventListener(this);
	}


//	public void resetPsndoc(){
//		UIRefPane psnRef = (UIRefPane) getBillCardPanel().getHeadItem("pk_psnjob").getComponent();
////		psnRef.getRefModel().addWherePart(PubPermissionUtils.getTBMPsnjobPermission());
//		AllParams allParams = ((TALoginContext)getModel().getContext()).getAllParams();
//		if(allParams!=null) //初始化界面时AllParams没有初始化，需要MODEL_INITIALIZED再重新加载一次
//			psnRef.getRefModel().addWherePart(allParams.gettBMPsnjobPermission());
//	}

	@Override
	public void afterEdit(BillEditEvent e) {
		super.afterEdit(e);
		int row = e.getRow();
		if (LeavebVO.LEAVEBEGINTIME.equals(e.getKey())||LeavebVO.LEAVEENDTIME.equals(e.getKey())) {
			//同时记录相应修改字段的日期
			UFDateTime cellTime = (UFDateTime) getBillCardPanel().getBodyValueAt(row, e.getKey());
			UFDate cellDate = null;
			if (cellTime != null) {
				cellDate = cellTime.getDate();
			}
			getBillCardPanel().setBodyValueAt(cellDate, row, StringUtils.replace(e.getKey(), "time", "date"));
			synTimeLong();
		}
		else if (LeavebVO.LEAVEBEGINDATE.equals(e.getKey())||LeavebVO.LEAVEENDDATE.equals(e.getKey())) {
			//同时记录相应修改字段的日期时间
			UFLiteralDate cellDate = (UFLiteralDate) getBillCardPanel().getBodyValueAt(row, e.getKey());
			UFDateTime cellTime = null;
			if (cellDate != null) {
				cellTime = new UFDateTime(cellDate.toDate().getTime());
			}
			getBillCardPanel().setBodyValueAt(cellTime, row, StringUtils.replace(e.getKey(), "date", "time"));
			getBillCardPanel().setBodyValueAt(UFDouble.ZERO_DBL, row, LeavebVO.LEAVEHOUR);
			synTimeLong();
		}		
		else if (LeavehVO.PK_PSNJOB.equals(e.getKey())) {
			// 员工号
			if(e.getSource() instanceof UIRefPane) {
				String pk_value = (String)((UIRefPane)e.getSource()).getRefValue("bd_psndoc.pk_psndoc");
				getBillCardPanel().setHeadItem(LeavehVO.PK_PSNDOC, pk_value);
				
				String psnorg = (String)((UIRefPane)e.getSource()).getRefValue("hi_psnjob.pk_psnorg");
				getBillCardPanel().setHeadItem(LeavehVO.PK_PSNORG, psnorg);
				
				String pk_org_v = (String)((UIRefPane)e.getSource()).getRefValue(TBMPsndocRefModel.PK_ORG_V);
				getBillCardPanel().setHeadItem(LeavehVO.PK_ORG_V,pk_org_v);
				
				String pk_dept_v = (String)((UIRefPane)e.getSource()).getRefValue(TBMPsndocRefModel.PK_DEPT_V);
				getBillCardPanel().setHeadItem(LeavehVO.PK_DEPT_V,pk_dept_v);
				
				String billcode = (String)((UIRefPane)e.getSource()).getRefValue("leaveplan.billcode");
				getBillCardPanel().setHeadItem(LeavehVO.BILL_CODE,billcode);
				
				
				
				dealDayOrHour();
				synTimeLong();
				resetBodyVOStatusAfterHeadChange();
			}
		} else if (LeavehVO.PK_LEAVETYPE.equals(e.getKey())||LeavehVO.PK_LEAVETYPECOPY.equals(e.getKey())) {
			if(e.getSource() instanceof UIRefPane) {
				// 休假类别,获得假期结余。假期年度及期间月
				setLeaveTypeCopy();
				dealDayOrHour();
				synTimeLong();
				resetBodyVOStatusAfterHeadChange();
			}
			
		} else if (LeavehVO.LEAVEYEAR.equals(e.getKey())) {
			changedMonth(ObjectUtils.toString(e.getValue()));
			dealDayOrHour();
			synTimeLong();
			resetBodyVOStatusAfterHeadChange();
		} else if (LeavehVO.LEAVEMONTH.equals(e.getKey())) {
			dealDayOrHour();
			synTimeLong();
			resetBodyVOStatusAfterHeadChange();
		} else if(LeavehVO.TRANSTYPEID.equals(e.getKey())) {
			UIRefPane refPane = (UIRefPane)getBillCardPanel().getHeadItem(LeavehVO.TRANSTYPEID).getComponent();
			getBillCardPanel().setHeadItem(LeavehVO.TRANSTYPE, refPane.getRefCode());
		}
//		else if(LeavebVO.PK_AGENTPSN.equals(e.getKey())){
//			BillCellEditor cellEdit = (BillCellEditor)e.getSource();
//			UIRefPane p = (UIRefPane)cellEdit.getComponent();
//			getBillCardPanel().setBodyValueAt(p.getRefPK(), row, e.getKey());
//		}
	}
	
	public void setLeaveTypeCopy()
	{
		UIRefPane pane = (UIRefPane)getBillCardPanel().getHeadItem(LeavehVO.PK_LEAVETYPE).getComponent();

		//存实例的PK
		String pk_timeitemcopy = (String)pane.getRefValue("tbm_timeitemcopy.pk_timeitemcopy");
		getBillCardPanel().setHeadItem(LeavehVO.PK_LEAVETYPECOPY, pk_timeitemcopy);
		
//		getMonthItem().setValue(null);
//		getYearItem().setValue(null);
		
		if(!islactation()&&curPeriodVO!=null)
		{
			//如果是新增，则如果往期结余假优先，则期间年度都设置为null
			if(getModel().getUiState()==UIState.ADD){
				if(isPreHolidayFirst()){
					getYearItem().setValue(null);
					getMonthItem().setValue(null);
				}
				else{
					initYear();
					getYearItem().setValue(curPeriodVO.getAccyear());
					getMonthItem().setValue(isYear()?null:curPeriodVO.getAccmonth());	
				}
			}
			//如果是修改，则年度设为最新年度，如果是按年结，期间置为空，否则期间设置为当前最新期间（这样做其实有问题，但暂时先这么处理）
			else{
				getYearItem().setValue(curPeriodVO.getAccyear());
				getMonthItem().setValue(isYear()?null:curPeriodVO.getAccmonth());	
			}
		}
		
		changeYearAndMonthEdit();

	}
	
	private boolean isPreHolidayFirst(){
		TALoginContext context = (TALoginContext)getModel().getContext();
		TimeRuleVO timerulevo = context.getAllParams().getTimeRuleVO();
		return timerulevo.isPreHolidayFirst();
	}
	
	public void changeYearAndMonthEdit()
	{
		getMonthItem().setNull(false);
		getYearItem().setNull(false);
		//如果是往期结余假优先，则年度和期间都不可编辑
		boolean isPreHolFirst = isPreHolidayFirst();
		if(isPreHolFirst){
			getMonthItem().setEdit(false);
		
			getYearItem().setEdit(false);
			return;
		}
		//如果往期结余假不优先，则年度可编辑，期间是否可编辑要看是否是按年结算
		getMonthItem().setEdit(!isYear());
		getYearItem().setEdit(!islactation());
		
		getBillCardPanel().setBillData(getBillCardPanel().getBillData());
	}
	
	//调用接口获得时长,此处无不需要实时显示，则后台持久化时调用亦可.如需要实时则调用	
	private void  synTimeLong()
	{
		//设置明细时长,总时长
		AggLeaveVO aggVO = (AggLeaveVO)getBillCardPanel().getBillData().getBillObjectByMetaData();

		try {
			if(!islactation())
			{
				ILeaveAppInfoDisplayer displayer = NCLocator.getInstance().lookup(ILeaveAppInfoDisplayer.class);				
				aggVO = displayer.calculate(aggVO, TimeZone.getDefault());
			}
			setValueAfterCalculate(aggVO);
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
		}
	}
	
	/**
	 * 假期年度改变事件
	 */
	public void changedMonth(String strYear) {
//		UIRefPane pane = (UIRefPane)getBillCardPanel().getHeadItem(LeavehVO.PK_LEAVETYPE).getComponent();
//		Integer leavesetperiod = (Integer)pane.getRefValue("tbm_timeitemcopy.leavesetperiod");
//		boolean isYear = leavesetperiod!=null&&SettlementPeriodEnum.YEAR.value().equals(leavesetperiod);
		if(!isYear()&&!islactation())
		{
			UIComboBox monthComboBox = getMonthComboBox();
			String selMonth = (String) monthComboBox.getSelectdItemValue();
			monthComboBox.removeAllItems();
			if(periodMap!=null)
			{
				monthComboBox.addItems(periodMap.get(strYear));				
			}
			if(selMonth!=null)
				monthComboBox.setSelectedItem(selMonth);
		}
	}
	
	protected UIComboBox getMonthComboBox(){
		return (UIComboBox) getBillCardPanel().getHeadItem(LeavehVO.LEAVEMONTH).getComponent();
	}
	
	protected BillItem getMonthItem(){
		return getBillCardPanel().getHeadItem(LeavehVO.LEAVEMONTH);
	}
	
	protected UIComboBox getYearComboBox(){
		return (UIComboBox) getBillCardPanel().getHeadItem(LeavehVO.LEAVEYEAR).getComponent();
	}
	
	protected BillItem getYearItem(){
		return getBillCardPanel().getHeadItem(LeavehVO.LEAVEYEAR);
	}
	
	@Override
	public boolean beforeEdit(BillItemEvent e) {
		// 编辑前修改参照数据范围
		if (LeavehVO.TRANSTYPEID.equals(e.getItem().getKey())) {
			
			try {
				//如果是直批，则流程类型不可编辑
				if(((nc.ui.hr.pf.model.PFAppModel)getModel()).isDirectApprove())
					return false;
			} catch(BusinessException ex) {
				Logger.error(ex.getMessage(), ex);
			}
			
			// 审批流
			((UIRefPane) e.getItem().getComponent()).getRefModel().addWherePart(
					" and (( parentbilltype = '" + LeaveConst.BillTYPE_LEAVE + "' and pk_group = '"
							+ getModel().getContext().getPk_group() + "') or pk_billtypecode = '"
							+ LeaveConst.BillTYPE_LEAVE + "' )");
			((UIRefPane) e.getItem().getComponent()).getRefModel().reloadData();
		}
		else if(LeavehVO.PK_LEAVETYPE.equals(e.getItem().getKey()))
		{
			String islactation = islactation() ? "'Y'" : "'N'";
//			UIRefPane leaveplan = (UIRefPane)getBillCardPanel().getHeadItem("leaveplan").getComponent();
			UIRefPane leavetype = (UIRefPane)getBillCardPanel().getHeadItem("pk_leavetype").getComponent();
//			leavetype.getRefModel().addWherePart(" and billcode= '"+leaveplan.getRefCode()+"' "+" and islactation=" + islactation + " ");
			leavetype.getRefModel().addWherePart(" and islactation=" + islactation + " ");
			
		}
		else if("leaveplan".equals(e.getItem().getKey())){
			UIRefPane refPane = (UIRefPane)getBillCardPanel().getHeadItem("leaveplan").getComponent();
			UIRefPane psndoc = (UIRefPane)getBillCardPanel().getHeadItem("pk_psndoc").getComponent();
			refPane.getRefModel().addWherePart(" and pk_psndoc= '"+psndoc.getRefPK()+"'",true);
			
		}
		return true;
	}

	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if (AppEventConst.SELECTION_CHANGED.equals(event.getType())) {
			changeTemplate();
		}
		if (AppEventConst.MODEL_INITIALIZED.equalsIgnoreCase(event.getType())){
			resetPsndoc();
			//工作交接过滤掉离职人员20140821  过滤掉没有转入人员档案的人员信息 20150827
			UIRefPane refPane = (UIRefPane)getBillCardPanel().getBodyItem(LeavebVO.PK_AGENTPSN).getComponent();
			refPane.getRefModel().addWherePart(" and hi_psnjob.poststat = 'Y' and hi_psnorg.indocflag = 'Y' ");
	    }
	}
	
	private boolean islactation()
	{
		boolean islactation = false;
		if(getModel().getUiState()==UIState.ADD)
		{
			islactation = getModel().isIslactation()==null?false:getModel().isIslactation().booleanValue();
		}
		else
		{
			AggLeaveVO aggVO = (AggLeaveVO)getModel().getSelectedData();
			if(aggVO!=null)
			{
				islactation = aggVO.getLeavehVO().getIslactation()==null?false:aggVO.getLeavehVO().getIslactation().booleanValue();				
			}
		}
		
		return islactation;
	}
	
	private boolean isYear()
	{
		return getLeavesetperiod()!=TimeItemCopyVO.LEAVESETPERIOD_MONTH;
	}
	
//	private boolean isDate()
//	{
//		return getLeavesetperiod()==TimeItemCopyVO.LEAVESETPERIOD_DATE;
//	}
	
	private int getLeavesetperiod()
	{
		UIRefPane pane = (UIRefPane)getBillCardPanel().getHeadItem(LeavehVO.PK_LEAVETYPE).getComponent();
		//控制期间月
		Integer leavesetperiod = (Integer)pane.getRefValue("tbm_timeitemcopy.leavesetperiod");
		return leavesetperiod==null?TimeItemCopyVO.LEAVESETPERIOD_YEAR:leavesetperiod.intValue();
	}
	
	/**
	 * 切换模板页面,动态隐藏列
	 */
	private void changeTemplate()
	{
		//隐藏单日哺乳时长
		boolean isLactation = islactation();
		boolean isNotLactation = !isLactation;
		BillCardPanel billCardPanel = getBillCardPanel();
		billCardPanel.getHeadItem(LeavehVO.LEAVEYEAR).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeavehVO.LEAVEYEAR).setNull(isNotLactation);
		billCardPanel.getHeadItem(LeavehVO.LEAVEMONTH).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeavehVO.LEAVEMONTH).setNull(isNotLactation&&!isYear());//如果是按年结算，则期间不用输入
		billCardPanel.getHeadItem(LeavehVO.LACTATIONHOUR).setShow(isLactation);
		billCardPanel.getHeadItem(LeavehVO.LACTATIONHOUR).setNull(isLactation);
		billCardPanel.getHeadItem(LeavehVO.RESTEDDAYORHOUR).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeavehVO.RESTEDDAYORHOUR).setNull(isNotLactation);
		billCardPanel.getHeadItem(LeavehVO.RESTDAYORHOUR).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeavehVO.RESTDAYORHOUR).setNull(isNotLactation);
		billCardPanel.getHeadItem(LeavehVO.REALDAYORHOUR).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeavehVO.REALDAYORHOUR).setNull(isNotLactation);
		//61新增，冻结时长和可用时长
		billCardPanel.getHeadItem(LeavehVO.FREEZEDAYORHOUR).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeavehVO.FREEZEDAYORHOUR).setNull(isNotLactation);
		billCardPanel.getHeadItem(LeavehVO.USEFULDAYORHOUR).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeavehVO.USEFULDAYORHOUR).setNull(isNotLactation);
		
		billCardPanel.getHeadItem(LeavehVO.SUMHOUR).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeavehVO.SUMHOUR).setNull(isNotLactation);
		
		billCardPanel.getHeadItem(LeavehVO.RELATETEL).setShow(isNotLactation);//假期联系电话不需要在哺乳假中显示
		

		billCardPanel.getBodyItem(LeavebVO.LACTATIONHOLIDAYTYPE).setShow(isLactation);
		billCardPanel.getBodyItem(LeavebVO.LACTATIONHOLIDAYTYPE).setNull(isLactation);
		billCardPanel.getBodyItem(LeavebVO.LEAVEBEGINDATE).setShow(isLactation);
		billCardPanel.getBodyItem(LeavebVO.LEAVEBEGINDATE).setNull(isLactation);
		billCardPanel.getBodyItem(LeavebVO.LEAVEENDDATE).setShow(isLactation);
		billCardPanel.getBodyItem(LeavebVO.LEAVEENDDATE).setNull(isLactation);

		billCardPanel.getBodyItem(LeavebVO.LEAVEHOUR).setShow(isNotLactation);
		billCardPanel.getBodyItem(LeavebVO.LEAVEHOUR).setNull(isNotLactation);		
		billCardPanel.getBodyItem(LeavebVO.LEAVEBEGINTIME).setShow(isNotLactation);
		billCardPanel.getBodyItem(LeavebVO.LEAVEBEGINTIME).setNull(isNotLactation);		
		billCardPanel.getBodyItem(LeavebVO.LEAVEENDTIME).setShow(isNotLactation);
		billCardPanel.getBodyItem(LeavebVO.LEAVEENDTIME).setNull(isNotLactation);
		
		billCardPanel.getBodyItem(LeavebVO.PK_AGENTPSN).setShow(isNotLactation);
		billCardPanel.getBodyItem(LeavebVO.WORKPROCESS).setShow(isNotLactation);
		
		//如是哺乳假，则交接人非必输
		if(isLactation){
			billCardPanel.getBodyItem(LeavebVO.PK_AGENTPSN).setNull(isNotLactation);
			billCardPanel.getBodyItem(LeavebVO.WORKPROCESS).setNull(isNotLactation);
			billCardPanel.getHeadItem(LeaveRegVO.RELATETEL).setNull(isNotLactation);
		}else{
			billCardPanel.getBodyItem(LeavebVO.PK_AGENTPSN).setNull(isAgentPsnNull);
			billCardPanel.getBodyItem(LeavebVO.WORKPROCESS).setNull(isWorkProcessNull);
			billCardPanel.getHeadItem(LeaveRegVO.RELATETEL).setNull(isRelatetelNull);
			
			//不load则切换后工作交接人显示主键
			billCardPanel.getBillModel().loadLoadRelationItemValue();
		}
	
		billCardPanel.setBillData(billCardPanel.getBillData());
		
		resetDataFloatAndTransTypeRef();
	
	}
	/**
	 * 计算总休假时长
	 * 
	 */
	public void calculateRowTime() {

		BillItem totalTimeItem = getBillCardPanel().getHeadItem(LeavehVO.SUMHOUR);

		BillModel billModel = getBillCardPanel().getBillModel();

		UFDouble dblTotalTime = new UFDouble(0);
		for (int i = 0; i < billModel.getRowCount(); i++) {
			Object objValue = billModel.getValueAt(i, LeavebVO.LEAVEHOUR);
			dblTotalTime = dblTotalTime.add(new UFDouble(ObjectUtils.toString(objValue)));
		}

		if (totalTimeItem != null) {
			dblTotalTime.setTrimZero(true);
			totalTimeItem.setValue(dblTotalTime);
		}
	}
	/**
	 * 计算享有时间、结余时间
	 */
	public void dealDayOrHour() {
		try {
			LeavehVO leavehVO = (LeavehVO) getBillCardPanel().getBillData().getHeaderValueVO(LeavehVO.class.getName());
			LeaveBalanceVO leaveBalanceVO = null;
			boolean isYear = isYear();
			//只有在用户录入了年度期间的情况下，才去计算结余时长等信息
			if((isYear&&!StringUtils.isEmpty(leavehVO.getLeaveyear()))||(!isYear&&!StringUtils.isEmpty(leavehVO.getLeaveyear())&&!StringUtils.isEmpty(leavehVO.getLeavemonth()))){
				Map<String, LeaveBalanceVO> balanceMap = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class)
					.queryAndCalLeaveBalanceVO(getModel().getContext().getPk_org(), leavehVO);
				leaveBalanceVO = MapUtils.isEmpty(balanceMap)?null:balanceMap.get(leavehVO.getPk_psnorg()+leavehVO.getPk_leavetype()+leavehVO.getLeaveyear()+(isYear?"":leavehVO.getLeavemonth()));
			}
			boolean isNull = leaveBalanceVO == null;
			// 当前享有时长
			setHeadItemValue(LeavehVO.REALDAYORHOUR, isNull?UFDouble.ZERO_DBL:leaveBalanceVO.getCurdayorhour());
			// 已休时长
			setHeadItemValue(LeavehVO.RESTEDDAYORHOUR, isNull?UFDouble.ZERO_DBL:leaveBalanceVO.getYidayorhour());
			// 结余时长
			setHeadItemValue(LeavehVO.RESTDAYORHOUR, isNull?UFDouble.ZERO_DBL:leaveBalanceVO.getRestdayorhour());
			//V61增加，冻结时长和可用时长
			setHeadItemValue(LeavehVO.FREEZEDAYORHOUR, isNull?UFDouble.ZERO_DBL:leaveBalanceVO.getFreezedayorhour());
			setHeadItemValue(LeavehVO.USEFULDAYORHOUR, isNull?UFDouble.ZERO_DBL:leaveBalanceVO.getUsefulrestdayorhour());
			
			//假期结算顺序号
			setHeadItemValue(LeavehVO.LEAVEINDEX, isNull?1:leaveBalanceVO.getLeaveindex());
			
		} catch (BusinessException ex) {
			Logger.error(ex.getMessage(), ex);
		}
	}
	@Override
	public LeaveAppModel getModel() {
		return (LeaveAppModel) super.getModel();
	}
	@Override
	public void initUI() {
		super.initUI();
//		initYear();
		addListeners();
		
		isAgentPsnNull = billCardPanel.getBodyItem(LeavebVO.PK_AGENTPSN).isNull();
		isWorkProcessNull = billCardPanel.getBodyItem(LeavebVO.WORKPROCESS).isNull();
		resetPsndoc();
		//工作交接过滤掉离职人员20140821  过滤掉没有转入人员档案的人员信息 20150827
		UIRefPane refPane = (UIRefPane)getBillCardPanel().getBodyItem(LeavebVO.PK_AGENTPSN).getComponent();
		refPane.getRefModel().addWherePart(" and hi_psnjob.poststat = 'Y' and hi_psnorg.indocflag = 'Y' ");
	}

	/**
	 * 初始化假期年度，期间
	 */
	private void initYear() {
		try {
			periodMap = PeriodServiceFacade.queryPeriodYearAndMonthByOrg(getModel().getContext().getPk_org());
//			curPeriodVO = periodService.queryCurPeriod(this.getModel().getContext().getPk_org());
			curPeriodVO = PeriodUtils.getDefaultPeriod(getModel().getContext());

			String[] years=null;
			if(periodMap!=null)
			{
				years = periodMap.keySet().toArray(new String[0]);
				Arrays.sort(years);				
			}

			UIComboBox yearComboBox = getYearComboBox();
			String selYear = (String) yearComboBox.getSelectdItemValue();
			yearComboBox.removeAllItems();
			//考虑到按入职日结算的，需要往前再推一年
			if(!ArrayUtils.isEmpty(years))
				yearComboBox.addItem(Integer.toString(Integer.parseInt(years[0])-1));
			yearComboBox.addItems(years);
			if(selYear!=null){
				yearComboBox.setSelectedItem(selYear);
			}
			if(selYear!=null){
				changedMonth(selYear);
			}
			else if(years!=null&&years.length>0)
			{
				changedMonth(years[0]);				
			}
			
		} catch (BusinessException e) {
			Logger.debug("初始化假期年度，期间失败!");
		}
	}

	@Override
	public void setEditable(boolean editable) {
		super.setEditable(editable);
		if (getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT) {
			setHeadItemEnable(OvertimehVO.BILL_CODE, getModel().isBillCodeEditable());
		}
	}
	public void setModel(LeaveAppModel model) {
		super.setModel(model);
	}
	
	@Override
	public void setValue(Object object) {
		super.setValue(object);
		updateUI();
	}
	
	public void rollBackCode(String autoGeneratedCode){
		if(StringUtils.isNotEmpty(autoGeneratedCode)){
			IHrBillCode hrBillCode = NCLocator.getInstance().lookup(IHrBillCode.class);
			try {
				hrBillCode.rollbackPreBillCode(LeaveConst.BillTYPE_LEAVE, getModel().getContext().getPk_group(),  getModel().getContext().getPk_org(), autoGeneratedCode);
			} catch (BusinessException e) {
				Debug.error(e.getMessage(), e);
				throw new BusinessRuntimeException(e.getMessage(),e);
			}
		}
	}
	
	@Override
	protected void onAdd() {
		super.onAdd();

		//初始化年，期间。
		initYear();
		
		getBillCardPanel().setHeadItem(LeavehVO.ISLACTATION, islactation());
		getBillCardPanel().setHeadItem(LeavehVO.ISHRSSBILL, UFBoolean.FALSE);
		
		if(islactation())
		{
			getBillCardPanel().setHeadItem(LeavehVO.PK_LEAVETYPE, LeaveConst.LEAVETYPE_SUCKLE);
		}
		
		setLeaveTypeCopy();
		changeTemplate();
		setValue(getValue(GET_ALLVALUE_WITHDEL));
	}


	@Override
	protected void beforeGetValue() {
		super.beforeGetValue();
	}


	@Override
	public void showMeUp() {
		super.showMeUp();
	}


	@Override
	protected void onEdit() {
		super.onEdit();
		initYear();
		changeYearAndMonthEdit();
		resetDataFloatAndTransTypeRef();
		synTimeLong();//2013-03-21添加，先制作了单据，后改变了规则的小数位数，导致不正确，需重新计算一下

	}


	@Override
	protected ChangeLabelVO getChangeLabelVO() {
		if(changeLabelVO == null){
			changeLabelVO = new ChangeLabelVO();
			changeLabelVO.setKqTypeKey(LeavehVO.PK_LEAVETYPE);
			changeLabelVO.setChangeLabelKeys( new String[]{
					LeavehVO.SUMHOUR,
					LeavehVO.REALDAYORHOUR,
					LeavehVO.RESTDAYORHOUR,LeavehVO.RESTEDDAYORHOUR,
					LeavehVO.FREEZEDAYORHOUR,LeavehVO.USEFULDAYORHOUR,//61新增，冻结时长和可用时长
					LeavehVO.LACTATIONHOUR
					});
		}
		return changeLabelVO;
	}


	@Override
	protected String[] getFloatItems() {
		return new String[]{LeavehVO.SUMHOUR,LeavehVO.REALDAYORHOUR,LeavehVO.RESTDAYORHOUR,LeavehVO.RESTEDDAYORHOUR,
				LeavehVO.FREEZEDAYORHOUR,LeavehVO.USEFULDAYORHOUR,//61新增，冻结时长和可用时长
				LeavehVO.LACTATIONHOUR,LeavebVO.LEAVEHOUR};
	}


	@Override
	protected void handTableRowChange() {
		AggLeaveVO aggvo = (AggLeaveVO) getValue(GET_ALLVALUE_NOTDEL);
		LeavebVO[] subVOs = aggvo.getBodyVOs();
		UFDouble sumHour = UFDouble.ZERO_DBL;
		if(ArrayUtils.isEmpty(subVOs)){
			getBillCardPanel().getHeadItem(LeavehVO.SUMHOUR).setValue(sumHour);
			return;
		}
		for(LeavebVO subVO:subVOs){
			if(subVO.getLeavehour()==null)
				continue;
			sumHour = sumHour.add(subVO.getLeavehour());
		}
		getBillCardPanel().getHeadItem(LeavehVO.SUMHOUR).setValue(sumHour);
	}


	@Override
	protected AutoCalTimeInfoVO getAutoCalTimeInfoVO() {
		AutoCalTimeInfoVO info = new AutoCalTimeInfoVO();
		info.setPos(IBillItem.BODY);
		info.setBeginTimeField(LeavebVO.LEAVEBEGINTIME);
		info.setEndTimeField(LeavebVO.LEAVEENDTIME);
		return info;
	}
	
}
