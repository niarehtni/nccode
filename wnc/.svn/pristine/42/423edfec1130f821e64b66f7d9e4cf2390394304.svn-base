package nc.ui.ta.leaveoff.pf.view;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.itf.hr.frame.IHrBillCode;
import nc.itf.ta.ILeaveOffManageMaintain;
import nc.pubitf.para.SysInitQuery;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.textfield.formatter.DefaultTextFiledFormatterFactory;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.ta.leaveoff.pf.model.LeaveoffAppModel;
import nc.ui.ta.wf.pub.TBMPubBillCardForm;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.ta.leave.LeaveConst;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leaveoff.AggLeaveoffVO;
import nc.vo.ta.leaveoff.LeaveoffVO;
import nc.vo.ta.pub.AllParams;
import nc.vo.ta.pub.TALoginContext;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public class LeaveoffCardForm  extends TBMPubBillCardForm implements BillCardBeforeEditListener{
	private LeaveRegVO regvo=null;
	private boolean islactation=false;
	
	@Override
	public void initUI() {
		super.initUI();
		getBillCardPanel().addEditListener(this);
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);// BillItemEvent事件监听
		UIRefPane diffPanel = (UIRefPane) getBillCardPanel().getHeadItem(LeaveoffVO.DIFFERENCEHOUR).getComponent();
	diffPanel.getUITextField().setTextFiledFormatterFactory(
		new DefaultTextFiledFormatterFactory(new LeaveoffDiffNumFormatter(diffPanel.getUITextField())));
	}
	
	@Override
	protected void setDefaultValue() {
		super.setDefaultValue();
		getBillCardPanel().getHeadItem(LeaveoffVO.PK_LEAVEREG).setValue(regvo.getPk_leavereg());
		getBillCardPanel().getHeadItem(LeaveoffVO.PK_PSNJOB).setValue(regvo.getPk_psnjob());
		getBillCardPanel().getHeadItem(LeaveoffVO.PK_PSNDOC).setValue(regvo.getPk_psndoc());
		getBillCardPanel().getHeadItem(LeaveoffVO.PK_PSNORG).setValue(regvo.getPk_psnorg());
		getBeginTimeListener().setPk_psndoc(regvo.getPk_psndoc());//选择日期时间的监听，根据班次设置开始结束时间
		getEndTimeListener().setPk_psndoc(regvo.getPk_psndoc());
		
		//开始结束时间，以及休假时长要用一个新的字段备份，要不然在销假审批结束后回写完休假登记表信息 ，休假登记原有的这几个字段会跟着变动
		getBillCardPanel().getHeadItem(LeaveoffVO.REGBEGINTIMECOPY).setValue(regvo.getLeavebegintime());
		getBillCardPanel().getHeadItem(LeaveoffVO.REGENDTIMECOPY).setValue(regvo.getLeaveendtime());
		getBillCardPanel().getHeadItem(LeaveoffVO.REGBEGINDATECOPY).setValue(regvo.getLeavebegindate());
		getBillCardPanel().getHeadItem(LeaveoffVO.REGENDDATECOPY).setValue(regvo.getLeaveenddate());
		getBillCardPanel().getHeadItem(LeaveoffVO.REGLEAVEHOURCOPY).setValue(regvo.getLeavehour());
//		if(regvo.getPk_leavetype().equals("1002Z710000000021ZM1")){
//			getBillCardPanel().getHeadItem(LeaveoffVO.REGBEGINTIMECOPY).setValue(regvo.getLeavebegintime());
//			getBillCardPanel().getHeadItem(LeaveoffVO.REGBEGINTIMECOPY).setEdit(false);
//			getBillCardPanel().getHeadItem(LeaveoffVO.REGENDTIMECOPY).setValue(regvo.getLeavebegintime());
//			getBillCardPanel().getHeadItem(LeaveoffVO.REGENDTIMECOPY).setEdit(false);
//			getBillCardPanel().getHeadItem(LeaveoffVO.REGBEGINDATECOPY).setValue(regvo.getLeavebegindate());
//			getBillCardPanel().getHeadItem(LeaveoffVO.REGENDDATECOPY).setValue(regvo.getLeavebegindate());
//			getBillCardPanel().getHeadItem(LeaveoffVO.REGLEAVEHOURCOPY).setValue(0);
//			UFDouble zero = UFDouble.ZERO_DBL;
//			getBillCardPanel().getHeadItem(LeaveoffVO.DIFFERENCEHOUR).setValue(zero.sub(regvo.getLeavehour()));
//		}else{
//		}
		
		getBillCardPanel().getHeadItem(LeaveoffVO.BILLMAKER).setValue(PubEnv.getPk_user());
		getBillCardPanel().getHeadItem(LeaveoffVO.APPLY_DATE).setValue(PubEnv.getServerDate());
		getBillCardPanel().getHeadItem(LeaveoffVO.PK_GROUP).setValue(getModel().getContext().getPk_group());
		getBillCardPanel().getHeadItem(LeaveoffVO.PK_ORG).setValue(getModel().getContext().getPk_org());
		getBillCardPanel().getHeadItem(LeaveoffVO.PK_BILLTYPE).setValue(LeaveConst.BillTYPE_LEAVEOFF);
		getBillCardPanel().getHeadItem(LeaveoffVO.APPROVE_STATE).setValue(IPfRetCheckInfo.NOSTATE);
		getBillCardPanel().getHeadItem(LeaveoffVO.PK_LEAVETYPE).setValue(regvo.getPk_leavetype());
		getBillCardPanel().getHeadItem(LeaveoffVO.PK_LEAVETYPECOPY).setValue(regvo.getPk_leavetypecopy());
		getBillCardPanel().getHeadItem(LeaveoffVO.ISLACTATION).setValue(regvo.getIslactation());
		
		//和自助保持一致，默认时间为登记时间
		//台湾本地化 销假申请 针对加班转调休的单据 只允许全部销假  不允许部分销假  2018-5-8 10:50:41 但强 start 
	UFBoolean twEnabled = UFBoolean.FALSE;
	String pk_leavetypecopy = "";
	try {
	    twEnabled = SysInitQuery.getParaBoolean(regvo.getPk_org(), "TWHR01");// 啟用臺灣本地化
	    if (twEnabled != null && twEnabled.booleanValue()) {
		pk_leavetypecopy = SysInitQuery.getParaString(regvo.getPk_org(), "TWHRT08");// 加班轉調休休假類別
	    }
	} catch (BusinessException e) {
	    Logger.error(e);
	}
	if (regvo.getPk_leavetype().equals(pk_leavetypecopy)) {
			getBillCardPanel().getHeadItem(LeaveoffVO.LEAVEBEGINDATE).setValue(regvo.getLeavebegindate());
			getBillCardPanel().getHeadItem(LeaveoffVO.LEAVEBEGINTIME).setValue(regvo.getLeavebegintime());
			getBillCardPanel().getHeadItem(LeaveoffVO.LEAVEENDDATE).setValue(regvo.getLeavebegindate());
			getBillCardPanel().getHeadItem(LeaveoffVO.LEAVEENDTIME).setValue(regvo.getLeavebegintime());
			getBillCardPanel().getHeadItem(LeaveoffVO.REALLYLEAVEHOUR).setValue(0);
			UFDouble zero = UFDouble.ZERO_DBL;
			getBillCardPanel().getHeadItem(LeaveoffVO.DIFFERENCEHOUR).setValue(zero.sub(regvo.getLeavehour()));
			
			getBillCardPanel().getHeadItem(LeaveoffVO.LEAVEBEGINTIME).setEdit(false);
			getBillCardPanel().getHeadItem(LeaveoffVO.LEAVEENDTIME).setEdit(false);
		}else{
			getBillCardPanel().getHeadItem(LeaveoffVO.LEAVEBEGINDATE).setValue(regvo.getLeavebegindate());
			getBillCardPanel().getHeadItem(LeaveoffVO.LEAVEBEGINTIME).setValue(regvo.getLeavebegintime());
			getBillCardPanel().getHeadItem(LeaveoffVO.LEAVEENDDATE).setValue(regvo.getLeaveenddate());
			getBillCardPanel().getHeadItem(LeaveoffVO.LEAVEENDTIME).setValue(regvo.getLeaveendtime());
			getBillCardPanel().getHeadItem(LeaveoffVO.LEAVEBEGINTIME).setEdit(true);
			getBillCardPanel().getHeadItem(LeaveoffVO.LEAVEENDTIME).setEdit(true);
		}
		//台湾本地化 销假申请 针对加班转调休的单据 只允许全部销假  不允许部分销假  2018-5-8 10:50:41 但强 start 
		getBillCardPanel().getHeadItem(LeaveoffVO.BILL_CODE).setValue(((LeaveoffAppModel)getModel()).getBillcode());
		
	}
	

	public void rollBackCode(String autoGeneratedCode){
		if(StringUtils.isNotEmpty(autoGeneratedCode)){
			IHrBillCode hrBillCode = NCLocator.getInstance().lookup(IHrBillCode.class);
			try {
		hrBillCode.rollbackPreBillCode(LeaveConst.BillTYPE_LEAVEOFF, getModel().getContext().getPk_group(),
			getModel().getContext().getPk_org(), autoGeneratedCode);
			} catch (BusinessException e) {
				Debug.error(e.getMessage(), e);
				throw new BusinessRuntimeException(e.getMessage(),e);
			}
		}
	}
	
	@Override
	protected AutoCalTimeInfoVO getAutoCalTimeInfoVO() {
		return null;
	}
	

	@Override
	protected ChangeLabelVO getChangeLabelVO() {
		if(changeLabelVO == null){
			changeLabelVO = new ChangeLabelVO();
			changeLabelVO.setKqTypeKey(LeaveoffVO.PK_LEAVETYPE);
	    changeLabelVO.setChangeLabelKeys(new String[] { "regleavehourcopy", "pk_leavereg.resteddayorhour",
		    "pk_leavereg.realdayorhour", "pk_leavereg.restdayorhour", "pk_leavereg.freezedayorhour",
		    "pk_leavereg.usefuldayorhour", "pk_leavereg.lactationhour" });
		}
		return changeLabelVO;
	}

	@Override
	protected String[] getFloatItems() {
	return new String[] { "pk_leavereg.leavehour", "pk_leavereg.resteddayorhour", "pk_leavereg.realdayorhour",
		"pk_leavereg.restdayorhour", "pk_leavereg.freezedayorhour", "pk_leavereg.usefuldayorhour",
		"pk_leavereg.lactationhour", LeaveoffVO.REALLYLEAVEHOUR, LeaveoffVO.DIFFERENCEHOUR,
		LeaveoffVO.REGLEAVEHOURCOPY };
	}
	@Override
	public boolean beforeEdit(BillItemEvent e) {
		// 编辑前修改参照数据范围
		if (LeaveoffVO.TRANSTYPEID.equals(e.getItem().getKey())) {
			try {
				//如果是直批，则流程类型不可编辑
				if(((nc.ui.hr.pf.model.PFAppModel)getModel()).isDirectApprove())
					return true;
			} catch(BusinessException ex) {
				Logger.error(ex.getMessage(), ex);
			}
			// 审批流
			((UIRefPane) e.getItem().getComponent()).getRefModel().addWherePart(
					" and (( parentbilltype = '" + LeaveConst.BillTYPE_LEAVEOFF + "' and pk_group = '"
							+ getModel().getContext().getPk_group() + "') or pk_billtypecode = '"
							+ LeaveConst.BillTYPE_LEAVEOFF + "' )");
			((UIRefPane) e.getItem().getComponent()).getRefModel().reloadData();
		}
		return true;
	}
	@Override
	public void afterEdit(BillEditEvent e) {
		super.afterEdit(e);
		if(LeaveoffVO.TRANSTYPEID.equals(e.getKey())) {
			UIRefPane refPane = (UIRefPane)getBillCardPanel().getHeadItem(LeaveoffVO.TRANSTYPEID).getComponent();
			getBillCardPanel().setHeadItem(LeaveoffVO.TRANSTYPE, refPane.getRefCode());
	} else if (LeaveoffVO.LEAVEBEGINTIME.equals(e.getKey()) || LeaveoffVO.LEAVEENDTIME.equals(e.getKey())) {
			//同时记录相应修改字段的日期
	    LeaveoffVO leaveoffvo = (LeaveoffVO) getBillCardPanel().getBillData().getHeaderValueVO(
		    LeaveoffVO.class.getName());
	    if (leaveoffvo == null)
		return;
			if(LeaveoffVO.LEAVEBEGINTIME.equals(e.getKey())) {
//				getBillCardPanel().setHeadItem(LeaveoffVO.LEAVEOFFBEGINDATE,leaveoffvo.getLeaveoffbegintime()==null?null:leaveoffvo.getLeaveoffbegintime());
		getBillCardPanel().setHeadItem(LeaveoffVO.LEAVEBEGINDATE,
			leaveoffvo.getLeavebegintime() == null ? null : leaveoffvo.getLeavebegintime());
	    } else {
//				getBillCardPanel().setHeadItem(LeaveoffVO.LEAVEOFFENDDATE,leaveoffvo.getLeaveoffendtime()==null?null:leaveoffvo.getLeaveoffendtime());
		getBillCardPanel().setHeadItem(LeaveoffVO.LEAVEENDDATE,
			leaveoffvo.getLeaveendtime() == null ? null : leaveoffvo.getLeaveendtime());
			}
			
//			if(leaveoffvo.getLeaveoffbegintime()!=null && leaveoffvo.getLeaveoffendtime()!=null){
			if(leaveoffvo.getLeavebegintime()!=null && leaveoffvo.getLeaveendtime()!=null){
				AggLeaveoffVO aggvo= new AggLeaveoffVO();
				aggvo.setParentVO(leaveoffvo);
				setOtherItem(aggvo);
			}
	} else if (LeaveoffVO.LEAVEBEGINDATE.equals(e.getKey()) || LeaveoffVO.LEAVEENDDATE.equals(e.getKey())) {
			//同时记录相应修改字段的日期时间
	    LeaveoffVO leaveoffvo = (LeaveoffVO) getBillCardPanel().getBillData().getHeaderValueVO(
		    LeaveoffVO.class.getName());
	    if (leaveoffvo == null)
		return;
			if(LeaveoffVO.LEAVEBEGINDATE.equals(e.getKey())) {
//				getBillCardPanel().setHeadItem(StringUtils.replace(e.getKey(), "date", "time"),leaveoffvo.getLeaveoffbegindate()==null?null:(new  UFDateTime(leaveoffvo.getLeaveoffbegindate().toDate().getTime())));
		// "date",
		// "time"),leaveoffvo.getLeaveoffbegindate()==null?null:(new
		getBillCardPanel().setHeadItem(
			StringUtils.replace(e.getKey(), "date", "time"),
			leaveoffvo.getLeavebegindate() == null ? null : (new UFDateTime(leaveoffvo.getLeavebegindate()
				.toDate().getTime())));
	    } else {
//				getBillCardPanel().setHeadItem(StringUtils.replace(e.getKey(), "date", "time"),leaveoffvo.getLeaveoffenddate()==null?null:(new  UFDateTime(leaveoffvo.getLeaveoffenddate().toDate().getTime())));
		// "date",
		getBillCardPanel().setHeadItem(
			StringUtils.replace(e.getKey(), "date", "time"),
			leaveoffvo.getLeaveenddate() == null ? null : (new UFDateTime(leaveoffvo.getLeaveenddate()
				.toDate().getTime())));
			}
//			if(leaveoffvo.getLeaveoffbegindate()!=null && leaveoffvo.getLeaveoffenddate()!=null){
			if(leaveoffvo.getLeavebegindate()!=null && leaveoffvo.getLeaveenddate()!=null){
				AggLeaveoffVO aggvo= new AggLeaveoffVO();
				aggvo.setParentVO(leaveoffvo);
				setOtherItem(aggvo);
			}
		}		
		
	}
	public void setOtherItem(AggLeaveoffVO aggvo){
		AggLeaveoffVO aggleaveoffvo= getCalculate(aggvo);
			if(null==aggleaveoffvo)
				return;
		TimeRuleVO  timerule=getTimeRule();
		if(null==timerule)
			return; 
		getBillCardPanel().setHeadItem(LeaveoffVO.REALLYLEAVEHOUR,aggleaveoffvo.getLeaveoffVO().getReallyleavehour());
		getBillCardPanel().setHeadItem(LeaveoffVO.DIFFERENCEHOUR,aggleaveoffvo.getLeaveoffVO().getDifferencehour());
		getBillCardPanel().getHeadItem(LeaveoffVO.REALLYLEAVEHOUR).setDecimalDigits(timerule.getTimedecimal());
		getBillCardPanel().getHeadItem(LeaveoffVO.DIFFERENCEHOUR).setDecimalDigits(timerule.getTimedecimal());
		
	}
	
	public TimeRuleVO getTimeRule(){
		AllParams params = ((TALoginContext)getModel().getContext()).getAllParams();
		if(params == null)
			return null;
		TimeRuleVO timeRuleVO = params.getTimeRuleVO();
		if(timeRuleVO == null)
			return null;
		return timeRuleVO;
	}
	AggLeaveoffVO getCalculate(AggLeaveoffVO aggvo){
		AggLeaveoffVO aggleaveoffvo=null;
		try {
			aggleaveoffvo= NCLocator.getInstance().lookup(ILeaveOffManageMaintain.class).calculate(aggvo);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		return aggleaveoffvo;
		
	}
	
	@Override
	public void setValue(Object object) {
		super.setValue(object);
		Object obj=getBillCardPanel().getHeadItem("pk_leavereg.pk_leavetype").getValueObject();
		if(null==obj)
			return ;
		if(obj.equals(TimeItemCopyVO.LEAVETYPE_LACTATION))
			islactation=true;
		else
			islactation=false;
		changeTemplate();
		
	}
	public LeaveRegVO getRegvo() {
		return regvo;
	}
	public void setRegvo(LeaveRegVO regvo) {
		this.regvo = regvo;
	}
	@Override
	protected void onAdd() {
		super.onAdd();
		AggLeaveoffVO aggvo=(AggLeaveoffVO) getValue();
		islactation=regvo.getIslactation().booleanValue();
		changeTemplate();
		aggvo = getCalculate(aggvo);
		setValue(aggvo);
	}
	@Override
	protected void onEdit() {
		super.onEdit();
		Object obj=getBillCardPanel().getHeadItem("pk_leavereg.pk_leavetype").getValueObject();
		if(obj.equals(TimeItemCopyVO.LEAVETYPE_LACTATION))
			islactation=true;
		else
			islactation=false;
		changeTemplate();
	String pk_psndoc = (String) ((UIRefPane) getBillCardPanel().getHeadItem(LeaveoffVO.PK_PSNDOC).getComponent())
		.getRefPK();
		getBeginTimeListener().setPk_psndoc(pk_psndoc);//选择日期时间的监听，根据班次设置开始结束时间
		getEndTimeListener().setPk_psndoc(pk_psndoc);
	}
	

	/**
	 * 切换模板页面,动态隐藏列
	 */
    private void changeTemplate() {
		   boolean isNotLactation = !islactation;
			billCardPanel.getHeadItem(LeaveoffVO.LEAVEBEGINTIME).setShow(isNotLactation);
			billCardPanel.getHeadItem(LeaveoffVO.LEAVEENDTIME).setShow(isNotLactation);
			billCardPanel.getHeadItem(LeaveoffVO.REALLYLEAVEHOUR).setShow(isNotLactation);
			billCardPanel.getHeadItem(LeaveoffVO.DIFFERENCEHOUR).setShow(isNotLactation);
			billCardPanel.getHeadItem("pk_leavereg.leaveyear").setShow(isNotLactation);
			billCardPanel.getHeadItem("pk_leavereg.leavemonth").setShow(isNotLactation);
//			billCardPanel.getHeadItem("pk_leavereg.leavebegintime").setShow(isNotLactation);
//			billCardPanel.getHeadItem("pk_leavereg.leaveendtime").setShow(isNotLactation);
//			billCardPanel.getHeadItem("pk_leavereg.leavehour").setShow(isNotLactation);
			billCardPanel.getHeadItem("regbegintimecopy").setShow(isNotLactation);
			billCardPanel.getHeadItem("regendtimecopy").setShow(isNotLactation);
			billCardPanel.getHeadItem("regleavehourcopy").setShow(isNotLactation);
			billCardPanel.getHeadItem("pk_leavereg.relatetel").setShow(isNotLactation);
			billCardPanel.getHeadItem("pk_leavereg.resteddayorhour").setShow(isNotLactation) ;// 已休时长
			billCardPanel.getHeadItem("pk_leavereg.realdayorhour").setShow(isNotLactation);//享有时长
			billCardPanel.getHeadItem("pk_leavereg.restdayorhour").setShow(isNotLactation);//结余时长
			billCardPanel.getHeadItem("pk_leavereg.freezedayorhour").setShow(isNotLactation);//冻结时长
			billCardPanel.getHeadItem("pk_leavereg.usefuldayorhour").setShow(isNotLactation);//可用时长
			
			billCardPanel.getHeadItem(LeaveoffVO.LEAVEBEGINDATE).setShow(islactation);
			billCardPanel.getHeadItem(LeaveoffVO.LEAVEENDDATE).setShow(islactation);
//			billCardPanel.getHeadItem("pk_leavereg.leavebegindate").setShow(islactation);
//			billCardPanel.getHeadItem("pk_leavereg.leaveenddate").setShow(islactation);
			billCardPanel.getHeadItem("regbegindatecopy").setShow(islactation);
			billCardPanel.getHeadItem("regenddatecopy").setShow(islactation);
			billCardPanel.getHeadItem("pk_leavereg.lactationholidaytype").setShow(islactation);//哺乳时段
			billCardPanel.getHeadItem("pk_leavereg.lactationhour").setShow(islactation);//单日哺乳时长
			
		billCardPanel.setBillData(billCardPanel.getBillData());
		
	}
	
	@Override
	protected void setTimeRefListener() {
		BillItem beginTimeItem = billCardPanel.getHeadItem(LeaveoffVO.LEAVEBEGINTIME);
		((UIRefPane)beginTimeItem.getComponent()).addCalendarValueChangeListener(getBeginTimeListener());
		BillItem endTimeItem = billCardPanel.getHeadItem(LeaveoffVO.LEAVEENDTIME);
		((UIRefPane)endTimeItem.getComponent()).addCalendarValueChangeListener(getEndTimeListener());
	}

	
}
