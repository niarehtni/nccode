package nc.ui.ta.leave.register.view;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ILeaveRegisterManageMaintain;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.IBillItem;
import nc.ui.ta.bill.BillInfoDlg;
import nc.ui.ta.pub.standardpsntemplet.OffCommonDialog;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.leave.LeaveRegVO;

/**
 * 对话框
 *
 */
public class LeaveOffDialog extends OffCommonDialog{

	private static final long serialVersionUID = 3332662095181519691L;
	public LeaveRegVO regVO = null; //登记VO

	public LeaveOffDialog(Container parent) {
		super(parent,ResHelper.getString("6017leave","06017leave0200")
/*@res "销假"*/);
	}

	public LeaveOffDialog(Container parent, String title) {
		super(parent, title);
	}

	@Override
	public String getMetaData() {
		return "hrta.hrtaleavereg";
	}

	@Override
	public List<BillTempletBodyVO> getTimeBillTempletVO(int pos, int showOrder,
			String metaData) {
		List<BillTempletBodyVO> timeListBodyVO = new ArrayList<BillTempletBodyVO>();
		BillTempletBodyVO tempBodyVO = createDefaultBillTempletBodyVO(pos);

		BillTempletBodyVO templetBodyVO = new BillTempletBodyVO();

		templetBodyVO = (BillTempletBodyVO) tempBodyVO.clone();
		templetBodyVO.setDefaultshowname(ResHelper.getString("6017leave","06017leave0205")
/*@res "申请开始时间"*/);
		templetBodyVO.setItemkey(LeaveRegVO.LEAVEBEGINTIME);
		templetBodyVO.setDatatype(IBillItem.DATETIME);
		templetBodyVO.setMetadataproperty(metaData+"."+LeaveRegVO.LEAVEBEGINTIME);
		templetBodyVO.setMetadatapath(LeaveRegVO.LEAVEBEGINTIME);
		templetBodyVO.setShoworder(showOrder++);
		templetBodyVO.setNullflag(true);
		templetBodyVO.setNewlineflag(UFBoolean.TRUE);
		timeListBodyVO.add(templetBodyVO);

		templetBodyVO = (BillTempletBodyVO) tempBodyVO.clone();
		templetBodyVO.setDefaultshowname(ResHelper.getString("6017leave","06017leave0206")
/*@res "申请结束时间"*/);
		templetBodyVO.setItemkey(LeaveRegVO.LEAVEENDTIME);
		templetBodyVO.setDatatype(IBillItem.DATETIME);
		templetBodyVO.setMetadataproperty(metaData+"."+LeaveRegVO.LEAVEENDTIME);
		templetBodyVO.setMetadatapath(LeaveRegVO.LEAVEENDTIME);
		templetBodyVO.setShoworder(showOrder++);
		templetBodyVO.setNullflag(true);
		timeListBodyVO.add(templetBodyVO);


		templetBodyVO = (BillTempletBodyVO) tempBodyVO.clone();
		templetBodyVO.setDefaultshowname(ResHelper.getString("6017leave","06017leave0207")
/*@res "申请开始日期"*/);
		templetBodyVO.setItemkey(LeaveRegVO.LEAVEBEGINDATE);
		templetBodyVO.setDatatype(IBillItem.DATE);
		templetBodyVO.setMetadataproperty(metaData+"."+LeaveRegVO.LEAVEBEGINDATE);
		templetBodyVO.setMetadatapath(LeaveRegVO.LEAVEBEGINDATE);
		templetBodyVO.setShoworder(showOrder++);
		templetBodyVO.setNewlineflag(UFBoolean.TRUE);
		templetBodyVO.setNullflag(true);
		templetBodyVO.setShowflag(false);
		timeListBodyVO.add(templetBodyVO);

		templetBodyVO = (BillTempletBodyVO) tempBodyVO.clone();
		templetBodyVO.setDefaultshowname(ResHelper.getString("6017leave","06017leave0208")
/*@res "申请结束日期"*/);
		templetBodyVO.setItemkey(LeaveRegVO.LEAVEENDDATE);
		templetBodyVO.setDatatype(IBillItem.DATE);
		templetBodyVO.setMetadataproperty(metaData+"."+LeaveRegVO.LEAVEENDDATE);
		templetBodyVO.setMetadatapath(LeaveRegVO.LEAVEENDDATE);
		templetBodyVO.setShoworder(showOrder++);
		templetBodyVO.setNullflag(true);
		templetBodyVO.setShowflag(false);
		timeListBodyVO.add(templetBodyVO);

		templetBodyVO = (BillTempletBodyVO) tempBodyVO.clone();
		templetBodyVO.setDefaultshowname(ResHelper.getString("common","UC000-0001540")
/*@res "实际开始时间"*/);
//		templetBodyVO.setDefaultshowname("休假实际开始时间");
		templetBodyVO.setItemkey("factbegintime");
		templetBodyVO.setDatatype(IBillItem.DATETIME);
		templetBodyVO.setShoworder(showOrder++);
		templetBodyVO.setEditflag(true);
		templetBodyVO.setNullflag(true);
		templetBodyVO.setNewlineflag(UFBoolean.TRUE);
		timeListBodyVO.add(templetBodyVO);

		templetBodyVO = (BillTempletBodyVO) tempBodyVO.clone();
		templetBodyVO.setDefaultshowname(ResHelper.getString("6017leave","06017leave0209")
/*@res "实际结束时间"*/);
//		templetBodyVO.setDefaultshowname("休假实际结束时间");
		templetBodyVO.setItemkey("factendtime");
		templetBodyVO.setDatatype(IBillItem.DATETIME);
		templetBodyVO.setShoworder(showOrder++);
		templetBodyVO.setEditflag(true);
		templetBodyVO.setNullflag(true);
		timeListBodyVO.add(templetBodyVO);



		templetBodyVO = (BillTempletBodyVO) tempBodyVO.clone();
//		templetBodyVO.setDefaultshowname(ResHelper.getString("6017leave","06017leave0210")
///*@res "实际开始日期"*/);
		templetBodyVO.setDefaultshowname("休假实际开始日期");
		templetBodyVO.setItemkey("factbegindate");
		templetBodyVO.setDatatype(IBillItem.DATE);
		templetBodyVO.setShoworder(showOrder++);
		templetBodyVO.setEditflag(true);
		templetBodyVO.setNullflag(true);
		templetBodyVO.setShowflag(false);
		templetBodyVO.setNewlineflag(UFBoolean.TRUE);
		timeListBodyVO.add(templetBodyVO);

		templetBodyVO = (BillTempletBodyVO) tempBodyVO.clone();
//		templetBodyVO.setDefaultshowname(ResHelper.getString("6017leave","06017leave0211")
///*@res "实际结束日期"*/);
		templetBodyVO.setDefaultshowname("休假实际结束日期");
		templetBodyVO.setItemkey("factenddate");
		templetBodyVO.setDatatype(IBillItem.DATE);
		templetBodyVO.setShoworder(showOrder++);
		templetBodyVO.setEditflag(true);
		templetBodyVO.setNullflag(true);
		templetBodyVO.setShowflag(false);
		timeListBodyVO.add(templetBodyVO);

		return timeListBodyVO;
	}

	@Override
	public void setValue(SuperVO vo) {
		regVO = (LeaveRegVO)vo;
		if(regVO==null)return;
		getCenterPanel().getBillData().setBillValueObjectByMetaData(vo);
		getCenterPanel().execHeadTailLoadFormulas();
		//设置实际开始时间和实际结束时间的初始值
		//台湾本地化  由于销的是调休单  只允许全部销假 在重新申请相应对的时数 2018-5-8 09:41:12 但强
		if(((LeaveRegVO)vo).getPk_leavetype().equals("1002Z710000000021ZM1")){
			getCenterPanel().getBillData().getHeadItem("factbegintime").setValue(((LeaveRegVO)vo).getLeavebegintime());
			getCenterPanel().getBillData().getHeadItem("factendtime").setValue(((LeaveRegVO)vo).getLeavebegintime());
			getCenterPanel().getBillData().getHeadItem("factbegindate").setValue(((LeaveRegVO)vo).getLeavebegindate());
			getCenterPanel().getBillData().getHeadItem("factenddate").setValue(((LeaveRegVO)vo).getLeavebegindate());
		}else{
			getCenterPanel().getBillData().getHeadItem("factbegintime").setValue(((LeaveRegVO)vo).getLeavebegintime());
			getCenterPanel().getBillData().getHeadItem("factendtime").setValue(((LeaveRegVO)vo).getLeaveendtime());
			getCenterPanel().getBillData().getHeadItem("factbegindate").setValue(((LeaveRegVO)vo).getLeavebegindate());
			getCenterPanel().getBillData().getHeadItem("factenddate").setValue(((LeaveRegVO)vo).getLeaveenddate());
		}

		boolean islactation = regVO.getIslactation()==null?false:regVO.getIslactation().booleanValue();
		getCenterPanel().getHeadItem(LeaveRegVO.LEAVEBEGINTIME).setShow(!islactation);
		getCenterPanel().getHeadItem(LeaveRegVO.LEAVEENDTIME).setShow(!islactation);
		getCenterPanel().getHeadItem("factbegintime").setShow(!islactation);
		getCenterPanel().getHeadItem("factendtime").setShow(!islactation);
		//台湾本地化  由于销的是调休单  只允许全部销假 在重新申请相应对的时数 2018-5-8 09:41:12 但强
		if(((LeaveRegVO)vo).getPk_leavetype().equals("1002Z710000000021ZM1")){
			getCenterPanel().getHeadItem("factbegintime").setEnabled(false);
			getCenterPanel().getHeadItem("factendtime").setEnabled(false);
		}else{
			getCenterPanel().getHeadItem("factbegintime").setEnabled(false);
			getCenterPanel().getHeadItem("factendtime").setEnabled(false);
		}
		getCenterPanel().getHeadItem(LeaveRegVO.LEAVEBEGINDATE).setShow(islactation);
		getCenterPanel().getHeadItem(LeaveRegVO.LEAVEENDDATE).setShow(islactation);
		getCenterPanel().getHeadItem("factbegindate").setShow(islactation);
		getCenterPanel().getHeadItem("factenddate").setShow(islactation);
		getCenterPanel().setBillData(getCenterPanel().getBillData());
	}

	@Override
	public void closeOK(){
		if(regVO==null)return;
		boolean islactation = regVO.getIslactation()==null?false:regVO.getIslactation().booleanValue();
		String itemBeginKey = islactation?"factbegindate":"factbegintime";
		String itemEndKey = islactation?"factenddate":"factendtime";
		//业务处理
		String factbegin = getCenterPanel().getBillData().getHeadItem(itemBeginKey).getValueObject()==null
		     	?"":getCenterPanel().getBillData().getHeadItem(itemBeginKey).getValueObject().toString();
		String factend = getCenterPanel().getBillData().getHeadItem(itemEndKey).getValueObject()==null
				?"":getCenterPanel().getBillData().getHeadItem(itemEndKey).getValueObject().toString();
		if(StringUtils.isEmpty(factbegin)){
			String beginDate = ResHelper.getString("6017leave","06017leave0210")
/*@res "实际开始日期"*/;
			String beginTime = ResHelper.getString("common","UC000-0001540")
/*@res "实际开始时间"*/;
			MessageDialog.showHintDlg(this, null, PublicLangRes.NOTNULL(islactation?beginDate:beginTime));
			return;
		}
		if(StringUtils.isEmpty(factend)){
			String endDate = ResHelper.getString("6017leave","06017leave0211")
/*@res "实际结束日期"*/;
			String endTime = ResHelper.getString("6017leave","06017leave0209")
/*@res "实际结束时间"*/;
			MessageDialog.showHintDlg(this, null, PublicLangRes.NOTNULL(islactation?endDate:endTime));
			return;
		}
		if(islactation)
		{
			if(new UFLiteralDate(factbegin).after(new UFLiteralDate(factend)))
			{
				MessageDialog.showHintDlg(this, null, ResHelper.getString("6017leave","06017leave0213")
/*@res "实际结束日期不能早于实际开始日期!"*/);
				return;
			}
		}
		else
		{
			if(new UFDateTime(factbegin).after(new UFDateTime(factend)))
			{
				MessageDialog.showHintDlg(this, null, ResHelper.getString("6017leave","06017leave0214")
/*@res "实际结束时间不能早于实际开始时间!"*/);
				return;
			}
		}
//		{
//			regVO.setLeavebegindate(new UFLiteralDate(factbegin));
//			regVO.setLeaveenddate(new UFLiteralDate(factend));
//		}
//		else
//		{
//			regVO.setLeavebegintime(new UFDateTime(factbegin));
//			regVO.setLeaveendtime(new UFDateTime(factend));
//		}
//		regVO.setIsleaveoff(UFBoolean.TRUE);
		try {
			if(islactation)
			{
				regVO = NCLocator.getInstance().lookup(ILeaveRegisterManageMaintain.class).leaveOff(regVO,new UFLiteralDate(factbegin),new UFLiteralDate(factend));
			}
			else
			{
				regVO = NCLocator.getInstance().lookup(ILeaveRegisterManageMaintain.class).leaveOff(regVO,new UFDateTime(factbegin),new UFDateTime(factend));
			}
		}
		//如果抛了BillMutexException，则表明存在不被允许的冲突，要显示这些冲突单据，并返回，不执行后面的保存
		catch(BillMutexException bme){
			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> result = bme.getMutexBillsMap();
			if(result!=null){
				BillInfoDlg.showErrorDialog(this.getParent(), null, CommonUtils.transferMap(result));
				return;
			}
		} catch (Exception e) {
			MessageDialog.showErrorDlg(this, null, e.getMessage());
			return;
		}
		super.closeOK();
	}

}