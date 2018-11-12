package nc.ui.ta.leave.register.view;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.itf.ta.ILeaveRegisterInfoDisplayer;
import nc.itf.ta.PeriodServiceFacade;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pub.bill.IBillItem;
import nc.ui.ta.leave.register.model.LeaveRegAppModel;
import nc.ui.ta.period.utils.PeriodUtils;
import nc.ui.ta.psndoc.ref.TBMPsndocRefModel;
import nc.ui.ta.wf.pub.TBMPubBillCardForm;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveConst;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.pub.TALoginContext;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 休假登记cardform，卡片类
 * @author    zhouhf
 * @version	   最后修改日期 2010年9月16日19:50:02
 * @see	   HrBillFormEditor
 */ 
@SuppressWarnings("serial")
public class LeaveRegCardView extends TBMPubBillCardForm implements
		FocusListener,BillCardBeforeEditListener {

	//信息项自动带出
	private ILeaveRegisterInfoDisplayer appAutoDisplayer;
	
	private Map<String, String[]> periodMap;
	
	private PeriodVO curPeriodVO;
	
	private String currOrg;
	
	private boolean isAgentPsnNull;//工作交接人是否可空
	private boolean isWorkProcessNull;//工作交接情况是否可空
	private boolean isRelatetelNull;//假期联系电话是否可空
	
	@Override
	public void initUI() {
		super.initUI();
		addListeners();
		
		isAgentPsnNull = billCardPanel.getHeadItem(LeaveRegVO.PK_AGENTPSN).isNull();
		isWorkProcessNull = billCardPanel.getHeadItem(LeaveRegVO.WORKPROCESS).isNull();
		isRelatetelNull = billCardPanel.getHeadItem(LeaveRegVO.RELATETEL).isNull();
		resetPsndoc();
	}
//	public void resetPsndoc(){
//		UIRefPane psnRef = (UIRefPane) getBillCardPanel().getHeadItem("pk_psnjob").getComponent();
////		psnRef.getRefModel().addWherePart(PubPermissionUtils.getTBMPsnjobPermission());//这种方法多用了远程连接
//		AllParams allParams = ((TALoginContext)getModel().getContext()).getAllParams();
//		if(allParams!=null) //初始化界面时AllParams没有初始化，需要MODEL_INITIALIZED再重新加载一次
//			psnRef.getRefModel().addWherePart(allParams.gettBMPsnjobPermission());
//	}
	private void addListeners() {
		getBillCardPanel().addEditListener(this);
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
		getModel().addAppEventListener(this);
	}
	
	/**
	 * 初始化假期年度，期间
	 */
	private void initYear() {
		try {
			periodMap = PeriodServiceFacade.queryPeriodYearAndMonthByOrg(getModel().getContext().getPk_org());
			curPeriodVO = null;
			UIComboBox yearComboBox = (UIComboBox)getBillCardPanel().getHeadItem(LeaveRegVO.LEAVEYEAR).getComponent();
			String[] years=null;
			if(periodMap!=null)
			{
				years = periodMap.keySet().toArray(new String[0]);
				Arrays.sort(years);				
			}
			
			yearComboBox.removeAllItems();
			//考虑到入职日结算的，需要往前再加一年
			if(!ArrayUtils.isEmpty(years))
				yearComboBox.addItem(Integer.toString(Integer.parseInt(years[0])-1));
			yearComboBox.addItems(years);
			if(years!=null&&years.length>0)
			{
				changedMonth(getCurPeriodVO().getAccyear());				
			}
		} catch (BusinessException e) {
			Logger.debug("初始化假期年度，期间失败!");
		}
	}
	
	/**
	 * 假期年度改变事件
	 */
	public void changedMonth(String strYear) {
		if(!isYear()&&!islactation())
		{
			UIComboBox monthComboBox = (UIComboBox) getBillCardPanel().getHeadItem(LeaveRegVO.LEAVEMONTH).getComponent();
			monthComboBox.removeAllItems();
			if(periodMap!=null)
			{
				monthComboBox.addItems(periodMap.get(strYear));				
			}
		}
	}

	@Override
	public void afterEdit(BillEditEvent e) {
		super.afterEdit(e);
		if(LeaveRegVO.ISLACTATION.equals(e.getKey())) {
			changeTemplate();
		}
		else if (LeaveRegVO.LEAVEBEGINTIME.equals(e.getKey())||LeaveRegVO.LEAVEENDTIME.equals(e.getKey())) {
			//同时记录相应修改字段的日期
			LeaveRegVO regVO = (LeaveRegVO)getBillCardPanel().getBillData().getHeaderValueVO(LeaveRegVO.class.getName());
			if(regVO==null)return;
			if(LeaveRegVO.LEAVEBEGINTIME.equals(e.getKey())) {
				getBillCardPanel().setHeadItem(StringUtils.replace(e.getKey(), "time", "date"),regVO.getLeavebegintime()==null?null:regVO.getLeavebegintime());
			}
			else {
				getBillCardPanel().setHeadItem(StringUtils.replace(e.getKey(), "time", "date"),regVO.getLeaveendtime()==null?null:regVO.getLeaveendtime());
			}
			//add by chenklb@yonyou.com 2018.5.7  修改休假时间时同时修改生效时间begin
			getBillCardPanel().setHeadItem(LeaveRegVO.EFFECTIVEDATE, regVO.getLeaveenddate()==null?null:new UFLiteralDate().before(regVO.getLeaveenddate())?regVO.getLeaveenddate():new UFLiteralDate());
			//add by chenklb@yonyou.com 2018.5.7  修改休假时间时同时修改生效时间end
			synTimeLong();
		}
		else if (LeaveRegVO.LEAVEBEGINDATE.equals(e.getKey())||LeaveRegVO.LEAVEENDDATE.equals(e.getKey())) {
			//同时记录相应修改字段的日期时间
			LeaveRegVO regVO = (LeaveRegVO)getBillCardPanel().getBillData().getHeaderValueVO(LeaveRegVO.class.getName());
			if(regVO==null)return;
			if(LeaveRegVO.LEAVEBEGINDATE.equals(e.getKey())) {
				getBillCardPanel().setHeadItem(StringUtils.replace(e.getKey(), "date", "time"),regVO.getLeavebegindate()==null?null:(new  UFDateTime(regVO.getLeavebegindate().toDate().getTime())));
			}
			else {
				getBillCardPanel().setHeadItem(StringUtils.replace(e.getKey(), "date", "time"),regVO.getLeaveenddate()==null?null:(new  UFDateTime(regVO.getLeaveenddate().toDate().getTime())));
			}
			//add by chenklb@yonyou.com 2018.5.7  修改休假时间时同时修改生效时间begin
			getBillCardPanel().setHeadItem(LeaveRegVO.EFFECTIVEDATE, regVO.getLeaveenddate()==null?null:new UFLiteralDate().before(regVO.getLeaveenddate())?regVO.getLeaveenddate():new UFLiteralDate());
			//add by chenklb@yonyou.com 2018.5.7  修改休假时间时同时修改生效时间end
			synTimeLong();
		}
		//add bychenklb@yonyou.com 2018.5.7  增加修改生效时间限制begin
		else if (LeaveRegVO.EFFECTIVEDATE.equals(e.getKey())) {
			
			LeaveRegVO regVO = (LeaveRegVO)getBillCardPanel().getBillData().getHeaderValueVO(LeaveRegVO.class.getName());
			//当前时间
			UFLiteralDate nowDate = new UFLiteralDate();
			//满足条件最早时间
			UFLiteralDate nowDate1=nowDate.before(regVO.getLeaveenddate())?regVO.getLeaveenddate():nowDate;
			if(regVO==null)return;
			getBillCardPanel().setHeadItem(LeaveRegVO.EFFECTIVEDATE, regVO.getEffectivedate()==null?nowDate1:(regVO.getEffectivedate().before(nowDate1)?nowDate1:regVO.getEffectivedate()));
			synTimeLong();
		}
		//add bychenklb@yonyou.com 2018.5.7  增加修改生效时间限制end
		else if (LeaveRegVO.PK_PSNJOB.equals(e.getKey())) {
			// 员工号
			if(e.getSource() instanceof UIRefPane) {
				UIRefPane refPane = (UIRefPane)e.getSource();
				String pk_value = (String)refPane.getRefValue("bd_psndoc.pk_psndoc");
				getBillCardPanel().setHeadItem(LeaveRegVO.PK_PSNDOC, pk_value);
				String psnorg = (String)refPane.getRefValue("hi_psnjob.pk_psnorg");
				getBillCardPanel().setHeadItem(LeaveRegVO.PK_PSNORG, psnorg);
				String pk_org_v = (String)refPane.getRefValue(TBMPsndocRefModel.PK_ORG_V);
				String pk_dept_v = (String)refPane.getRefValue(TBMPsndocRefModel.PK_DEPT_V);
				this.getBillCardPanel().setHeadItem(LeaveRegVO.PK_ORG_V, pk_org_v);
				this.getBillCardPanel().setHeadItem(LeaveRegVO.PK_DEPT_V, pk_dept_v);
				//add bychenklb@yonyou.com 2018.5.7  增加默认生效时间begin
				LeaveRegVO regVO = (LeaveRegVO)getBillCardPanel().getBillData().getHeaderValueVO(LeaveRegVO.class.getName());
				getBillCardPanel().setHeadItem(LeaveRegVO.EFFECTIVEDATE, regVO.getEffectivedate()==null?new UFLiteralDate():regVO.getEffectivedate());
				//add bychenklb@yonyou.com 2018.5.7  增加默认生效时间end
				dealDayOrHour();
				synTimeLong();
				
			}
		} else if (LeaveRegVO.PK_LEAVETYPE.equals(e.getKey())||LeaveRegVO.PK_LEAVETYPECOPY.equals(e.getKey())) {
			if(e.getSource() instanceof UIRefPane){
				//存实例的PK,休假类别
				setLeaveTypeCopy();
				dealDayOrHour();
				synTimeLong();
			}
		} else if (LeaveRegVO.LEAVEYEAR.equals(e.getKey())) {
			changedMonth(ObjectUtils.toString(e.getValue()));
			dealDayOrHour();
			synTimeLong();
		} else if (LeaveRegVO.LEAVEMONTH.equals(e.getKey())) {
			dealDayOrHour();
			synTimeLong();
		}
	}
	
	/**
	 * 计算享有时间、结余时间
	 */
	public void dealDayOrHour() {
		try {
			LeaveRegVO leaveVO = (LeaveRegVO) getBillCardPanel().getBillData().getHeaderValueVO(LeaveRegVO.class.getName());
			
			LeaveBalanceVO leaveBalanceVO = null;
			boolean isYear = isYear();
			//只有在用户录入了年度期间的情况下，才去计算结余时长等信息
			if((isYear&&!StringUtils.isEmpty(leaveVO.getLeaveyear()))||(!isYear&&!StringUtils.isEmpty(leaveVO.getLeaveyear())&&!StringUtils.isEmpty(leaveVO.getLeavemonth()))){
				Map<String, LeaveBalanceVO> balanceMap = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class)
					.queryAndCalLeaveBalanceVO(getModel().getContext().getPk_org(), leaveVO);
				leaveBalanceVO = MapUtils.isEmpty(balanceMap)?null:balanceMap.get(leaveVO.getPk_psnorg()+leaveVO.getPk_leavetype()+leaveVO.getLeaveyear()+(isYear?"":leaveVO.getLeavemonth()));
			}
			boolean isNull = leaveBalanceVO == null;
			// 当前享有时长
			setHeadItemValue(LeaveRegVO.REALDAYORHOUR, isNull?UFDouble.ZERO_DBL:leaveBalanceVO.getCurdayorhour());
			// 已休时长
			setHeadItemValue(LeaveRegVO.RESTEDDAYORHOUR, isNull?UFDouble.ZERO_DBL:leaveBalanceVO.getYidayorhour());
			// 结余时长
			setHeadItemValue(LeaveRegVO.RESTDAYORHOUR, isNull?UFDouble.ZERO_DBL:leaveBalanceVO.getRestdayorhour());
			//V61增加，冻结时长和可用时长
			setHeadItemValue(LeaveRegVO.FREEZEDAYORHOUR, isNull?UFDouble.ZERO_DBL:leaveBalanceVO.getFreezedayorhour());
			setHeadItemValue(LeaveRegVO.USEFULDAYORHOUR, isNull?UFDouble.ZERO_DBL:leaveBalanceVO.getUsefulrestdayorhour());
			
			//假期结算顺序号
			setHeadItemValue(LeaveRegVO.LEAVEINDEX, isNull?1:leaveBalanceVO.getLeaveindex());
			
		} catch (BusinessException ex) {
			Logger.error(ex.getMessage(), ex);
		}
	}
	
	//调用接口获得时长,此处无不需要实时显示，则后台持久化时调用亦可.如需要实时则调用	
	private void  synTimeLong() {
		//设置明细时长,总时长
		LeaveRegVO leaveVO = (LeaveRegVO) getBillCardPanel().getBillData().getHeaderValueVO(LeaveRegVO.class.getName());
		try {
			if(!islactation()) {
				ILeaveRegisterInfoDisplayer displayer = NCLocator.getInstance().lookup(ILeaveRegisterInfoDisplayer.class);
				leaveVO = displayer.calculate(leaveVO, TimeZone.getDefault());
			}
			getBillCardPanel().setHeadItem(LeaveRegVO.LEAVEHOUR, leaveVO.getLeavehour()==null?new UFDouble(0):leaveVO.getLeavehour());
			setValue(leaveVO);
			setValueAfterCalculate(leaveVO);
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
		}
	}
	
	private boolean isPreHolidayFirst(){
		TALoginContext context = (TALoginContext)getModel().getContext();
		TimeRuleVO timerulevo = context.getAllParams().getTimeRuleVO();
		return timerulevo.isPreHolidayFirst();
	}
	
	
	public ILeaveRegisterInfoDisplayer getRegAutoDisplaer() {
		if(appAutoDisplayer == null) {
			appAutoDisplayer =  NCLocator.getInstance().lookup(ILeaveRegisterInfoDisplayer.class);
		}
		return appAutoDisplayer;
	}

	@Override
	public void focusGained(FocusEvent e) {

	}

	
	@Override
	public void focusLost(FocusEvent e) {
	}

	@Override
	protected void setDefaultValue() {
		super.setDefaultValue();
		//登记来源为登记节点
		getBillCardPanel().getHeadItem(LeaveRegVO.BILLSOURCE).setValue(ICommonConst.BILL_SOURCE_REG);
		getBillCardPanel().getHeadItem(LeaveRegVO.ISLACTATION).setValue(getModel().Islactation());
	}
	
	@Override
	public LeaveRegAppModel getModel() {
		return (LeaveRegAppModel) super.getModel();
	}
	
	public void setModel(LeaveRegAppModel model) {
		super.setModel(model);
	}
	
	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if (AppEventConst.SELECTION_CHANGED.equals(event.getType())) {
			changeTemplate();
		}
		if (AppEventConst.MODEL_INITIALIZED.equals(event.getType())) {
			// 所属组织没切换不重置考勤期间信息
			if(StringUtils.equals(currOrg, getModel().getContext().getPk_org()))
				return;
			currOrg = getModel().getContext().getPk_org();
			initYear();
			resetPsndoc();
			
			UIRefPane refPane = (UIRefPane)getBillCardPanel().getHeadItem(LeaveRegVO.PK_AGENTPSN).getComponent();
			refPane.getRefModel().addWherePart(" and hi_psnjob.poststat = 'Y' and hi_psnorg.indocflag = 'Y' ");
		}
	}
	
	
	/**
	 * 切换模板页面,动态隐藏列
	 */
	private void changeTemplate()
	{
		//根据是否哺乳假控制界面字段显示

		boolean isLactation = islactation();
		boolean isNotLactation = !isLactation;
		BillCardPanel billCardPanel = getBillCardPanel();
		billCardPanel.getHeadItem(LeaveRegVO.LEAVEYEAR).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.LEAVEYEAR).setNull(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.LEAVEMONTH).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.LEAVEMONTH).setNull(isNotLactation&&!isYear());//如果是按年结算，则期间不用输入
		billCardPanel.getHeadItem(LeaveRegVO.RESTEDDAYORHOUR).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.RESTEDDAYORHOUR).setNull(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.RESTDAYORHOUR).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.RESTDAYORHOUR).setNull(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.REALDAYORHOUR).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.REALDAYORHOUR).setNull(isNotLactation);
		//61新增，冻结时长和可用时长
		billCardPanel.getHeadItem(LeaveRegVO.FREEZEDAYORHOUR).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.FREEZEDAYORHOUR).setNull(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.USEFULDAYORHOUR).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.USEFULDAYORHOUR).setNull(isNotLactation);
		
		billCardPanel.getHeadItem(LeaveRegVO.LEAVEHOUR).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.LEAVEHOUR).setNull(isNotLactation);
		
		billCardPanel.getHeadItem(LeaveRegVO.LACTATIONHOLIDAYTYPE).setShow(isLactation);
		billCardPanel.getHeadItem(LeaveRegVO.LACTATIONHOLIDAYTYPE).setNull(isLactation);
		billCardPanel.getHeadItem(LeaveRegVO.LACTATIONHOUR).setShow(isLactation);
		billCardPanel.getHeadItem(LeaveRegVO.LACTATIONHOUR).setNull(isLactation);
		billCardPanel.getHeadItem(LeaveRegVO.LEAVEBEGINDATE).setShow(isLactation);
		billCardPanel.getHeadItem(LeaveRegVO.LEAVEBEGINDATE).setNull(isLactation);
		billCardPanel.getHeadItem(LeaveRegVO.LEAVEENDDATE).setShow(isLactation);
		billCardPanel.getHeadItem(LeaveRegVO.LEAVEENDDATE).setNull(isLactation);
		billCardPanel.getHeadItem(LeaveRegVO.LEAVEBEGINTIME).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.LEAVEBEGINTIME).setNull(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.LEAVEENDTIME).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.LEAVEENDTIME).setNull(isNotLactation);
		//哺乳假不用显示工作交接人
		billCardPanel.getHeadItem(LeaveRegVO.PK_AGENTPSN).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.WORKPROCESS).setShow(isNotLactation);
		billCardPanel.getHeadItem(LeaveRegVO.RELATETEL).setShow(isNotLactation);//假期联系电话不需要在哺乳假中显示
		//如是哺乳假，则交接人非必输
		if(isLactation){
			billCardPanel.getHeadItem(LeaveRegVO.PK_AGENTPSN).setNull(isNotLactation);
			billCardPanel.getHeadItem(LeaveRegVO.WORKPROCESS).setNull(isNotLactation);
			billCardPanel.getHeadItem(LeaveRegVO.RELATETEL).setNull(isNotLactation);
		}else{
			billCardPanel.getHeadItem(LeaveRegVO.PK_AGENTPSN).setNull(isAgentPsnNull);
			billCardPanel.getHeadItem(LeaveRegVO.WORKPROCESS).setNull(isWorkProcessNull);
			billCardPanel.getHeadItem(LeaveRegVO.RELATETEL).setNull(isRelatetelNull);
		}
		
		billCardPanel.setBillData(billCardPanel.getBillData());
	}
	
	@Override
	public boolean beforeEdit(BillItemEvent e) {
		// 编辑前修改参照数据范围
		if(LeaveRegVO.PK_LEAVETYPE.equals(e.getItem().getKey()))
		{
			String islactation = islactation()?"'Y'":"'N'";
			UIRefPane refPane = (UIRefPane)getBillCardPanel().getHeadItem(LeaveRegVO.PK_LEAVETYPE).getComponent();
			refPane.getRefModel().addWherePart(" and islactation="+islactation+" ");
	} else if (LeaveRegVO.LEAVEPLAN.equals(e.getItem().getKey())) {
	    UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem("leaveplan").getComponent();
	    UIRefPane psndoc = (UIRefPane) getBillCardPanel().getHeadItem("pk_psndoc").getComponent();
	    refPane.getRefModel().addWherePart(" and pk_psndoc= '" + psndoc.getRefPK() + "'", true);
		}
		return true;
	}
	
	private boolean islactation()
	{
		boolean islactation = false;
		if(getModel().getUiState()==UIState.ADD)
		{
			islactation = getModel().Islactation()==null?false:getModel().Islactation().booleanValue();
		}
		else
		{
			LeaveRegVO regVO = (LeaveRegVO)getModel().getSelectedData();
			if(regVO!=null)
			{
				islactation = regVO.getIslactation()==null?false:regVO.getIslactation().booleanValue();				
			}
		}
		
		return islactation;
	}
	
	private boolean isYear()
	{
		UIRefPane pane = (UIRefPane)getBillCardPanel().getHeadItem(LeaveRegVO.PK_LEAVETYPE).getComponent();
		//控制期间月
		Integer leavesetperiod = (Integer)pane.getRefValue("tbm_timeitemcopy.leavesetperiod");
		return leavesetperiod!=null&&leavesetperiod!=TimeItemCopyVO.LEAVESETPERIOD_MONTH;
	}

	@Override
	protected void onAdd() {
		super.onAdd();
		getBillCardPanel().setHeadItem(LeaveRegVO.ISLACTATION, getModel().Islactation());
		getBillCardPanel().setHeadItem(LeaveRegVO.REALDAYORHOUR, UFDouble.ZERO_DBL);
		getBillCardPanel().setHeadItem(LeaveRegVO.RESTDAYORHOUR, UFDouble.ZERO_DBL);
		getBillCardPanel().setHeadItem(LeaveRegVO.RESTEDDAYORHOUR, UFDouble.ZERO_DBL);
		//V61增加，冻结时长和可用时长
		getBillCardPanel().setHeadItem(LeaveRegVO.FREEZEDAYORHOUR, UFDouble.ZERO_DBL);
		getBillCardPanel().setHeadItem(LeaveRegVO.USEFULDAYORHOUR, UFDouble.ZERO_DBL);
		
		getBillCardPanel().setHeadItem(LeaveRegVO.LEAVEHOUR, UFDouble.ZERO_DBL);
		
		if(islactation())
		{
			getBillCardPanel().setHeadItem(LeaveRegVO.PK_LEAVETYPE, LeaveConst.LEAVETYPE_SUCKLE);
		}
		
		setLeaveTypeCopy();
		changeTemplate();
		setValue(getValue());
	}
	
	private void setLeaveTypeCopy()
	{
		UIRefPane pane = (UIRefPane)getBillCardPanel().getHeadItem(LeaveRegVO.PK_LEAVETYPE).getComponent();

		//存实例的PK
		String pk_timeitemcopy = (String)pane.getRefValue("tbm_timeitemcopy.pk_timeitemcopy");
		getBillCardPanel().setHeadItem(LeaveRegVO.PK_LEAVETYPECOPY, pk_timeitemcopy);
		
//		getBillCardPanel().getHeadItem(LeaveRegVO.LEAVEMONTH).setValue(null);
//		getBillCardPanel().getHeadItem(LeaveRegVO.LEAVEYEAR).setValue(null);
		
		if(!islactation()&&getCurPeriodVO()!=null)
		{
			//如果是新增，则如果往期结余假优先，则期间年度都设置为null
			if(getModel().getUiState()==UIState.ADD){
				if(isPreHolidayFirst()){
					getBillCardPanel().getHeadItem(LeaveRegVO.LEAVEYEAR).setValue(null);
					getBillCardPanel().getHeadItem(LeaveRegVO.LEAVEMONTH).setValue(null);
				}
				else{
					getBillCardPanel().getHeadItem(LeaveRegVO.LEAVEYEAR).setValue(getCurPeriodVO().getAccyear());
					getBillCardPanel().getHeadItem(LeaveRegVO.LEAVEMONTH).setValue(isYear()?null:getCurPeriodVO().getAccmonth());	
				}
			}
			//如果是修改，则年度设为最新年度，如果是按年结，期间置为空，否则期间设置为当前最新期间（这样做其实有问题，但暂时先这么处理）
			else{
				getBillCardPanel().getHeadItem(LeaveRegVO.LEAVEYEAR).setValue(getCurPeriodVO().getAccyear());
				getBillCardPanel().getHeadItem(LeaveRegVO.LEAVEMONTH).setValue(isYear()?null:getCurPeriodVO().getAccmonth());
			}
		}
		changeYearAndMonthEdit();
	}
	
	
	public void changeYearAndMonthEdit()
	{
		getBillCardPanel().getHeadItem(LeaveRegVO.LEAVEMONTH).setNull(false);
		getBillCardPanel().getHeadItem(LeaveRegVO.LEAVEYEAR).setNull(false);
		//如果是往期结余假优先，则年度和期间都不可编辑
		boolean isPreHolFirst = isPreHolidayFirst();
		if(isPreHolFirst){
			getBillCardPanel().getHeadItem(LeaveRegVO.LEAVEMONTH).setEdit(false);
			getBillCardPanel().getHeadItem(LeaveRegVO.LEAVEYEAR).setEdit(false);
			return;
		}
		//如果往期结余假不优先，则年度可编辑，期间是否可编辑要看是否是按年结算
		getBillCardPanel().getHeadItem(LeaveRegVO.LEAVEMONTH).setEdit(!isYear());
		getBillCardPanel().getHeadItem(LeaveRegVO.LEAVEYEAR).setEdit(!islactation());
		
		getBillCardPanel().setBillData(getBillCardPanel().getBillData());
	}

	@Override
	protected ChangeLabelVO getChangeLabelVO() {
		if(changeLabelVO == null){
			changeLabelVO = new ChangeLabelVO();
			changeLabelVO.setKqTypeKey(LeaveRegVO.PK_LEAVETYPE);
			changeLabelVO.setChangeLabelKeys( new String[]{LeaveRegVO.LEAVEHOUR,LeaveRegVO.REALDAYORHOUR,LeaveRegVO.RESTDAYORHOUR,LeaveRegVO.RESTEDDAYORHOUR,
					LeaveRegVO.FREEZEDAYORHOUR,LeaveRegVO.USEFULDAYORHOUR,//V61增加，冻结时长和可用时长
					LeaveRegVO.LACTATIONHOUR});
		}
		return changeLabelVO;
	}

	@Override
	protected String[] getFloatItems() {
		return new String[]{LeaveRegVO.REALDAYORHOUR,LeaveRegVO.RESTDAYORHOUR,LeaveRegVO.RESTEDDAYORHOUR,
				LeaveRegVO.FREEZEDAYORHOUR,LeaveRegVO.USEFULDAYORHOUR,//V61增加，冻结时长和可用时长
				LeaveRegVO.LACTATIONHOUR,LeaveRegVO.LEAVEHOUR};
	}

	@Override
	protected void onEdit() {
		super.onEdit();
		changeTemplate();
		changeYearAndMonthEdit();
	}

	@Override
	protected void handTableRowChange() {

	}
	@Override
	protected AutoCalTimeInfoVO getAutoCalTimeInfoVO() {
		AutoCalTimeInfoVO info = new AutoCalTimeInfoVO();
		info.setPos(IBillItem.HEAD);
		info.setBeginTimeField(LeaveRegVO.LEAVEBEGINTIME);
		info.setEndTimeField(LeaveRegVO.LEAVEENDTIME);
		return info;
	}

	/**
	 * 取当前考勤期间
	 * 2012-04-27改为懒加载方式
	 * 在使用时再加载，切换组织时在initYear中置为空
	 * @return
	 */
	public PeriodVO getCurPeriodVO() {
		if(curPeriodVO==null)
			curPeriodVO = PeriodUtils.getDefaultPeriod(getModel().getContext());
		return curPeriodVO;
	}
}
