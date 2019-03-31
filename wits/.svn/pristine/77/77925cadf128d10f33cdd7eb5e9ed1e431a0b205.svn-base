package nc.ui.wa.taxaddtional.model;

import java.util.ArrayList;
import java.util.Date;

import nc.ui.hr.uif2.view.HrNormalQueryPanel;
import nc.ui.hr.uif2.view.RecentPeriodPanel;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.vo.hr.pf.PFQueryParams;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletHeadVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.wa_tax.DateUtil;

public class NormalQueryPanel extends HrNormalQueryPanel {
	@Override
	public void clearData() {
		super.clearData();
	}

	@Override
	protected BillTempletVO getBillTempletVO() {
		int iShowOrder = 0;

		BillTempletVO billTempletVO = new BillTempletVO();
		ArrayList<BillTempletBodyVO> arrListBodyVO = new ArrayList<BillTempletBodyVO>();

		BillTempletBodyVO templetBodyVO = new BillTempletBodyVO();
		templetBodyVO.setCardflag(true);
		templetBodyVO.setDatatype(IBillItem.COMBO);
		templetBodyVO.setDefaultshowname("纳税年度");
		templetBodyVO.setEditflag(true);
		templetBodyVO.setInputlength(20);
		templetBodyVO.setItemkey("taxyear");
		templetBodyVO.setItemtype(0);
		templetBodyVO.setNullflag(false);
		templetBodyVO.setPos(IBillItem.HEAD);
		templetBodyVO.setShowflag(true);
		templetBodyVO.setShoworder(iShowOrder++);
		templetBodyVO.setWidth(3);
		templetBodyVO.setNewlineflag(UFBoolean.TRUE);
		arrListBodyVO.add(templetBodyVO);

		BillTempletHeadVO parent = new BillTempletHeadVO();
		parent.setPkPubBilltempletHead(getClass().getName());
		parent.setTs(new UFDateTime("2015-05-15 13:53:22"));
		billTempletVO.setParentVO(parent);
		billTempletVO.setChildrenVO(arrListBodyVO.toArray(new BillTempletBodyVO[0]));
		return billTempletVO;
	}

	@Override
	public TaxaddtionalModel getModel() {
		return (TaxaddtionalModel) super.getModel();
	}
	 
	@Override
	public void initBillCardPanel() {
		initCombobox();
	}
 
	protected void initCombobox() {
		// 初始化审批状态
		BillItem bi = getBillCardPanel().getHeadItem("taxyear");

		if (bi != null) {
			UIComboBox approveState = (UIComboBox) bi.getComponent();
			  int year = DateUtil.getYear(new Date());
			  int minyear = 2019;
			  int maxyear = year+1;
			  if (maxyear <= 2019) {
				  maxyear = 2019+1;
			  } 
			  for (int i = minyear ; i <= maxyear; i++) {
				  IConstEnum constEnum2 = new DefaultConstEnum(i+"", i+"");
				  approveState.addItem(constEnum2);
			  }
		}
	}

	@Override
	public void initUI() {
		super.initUI();
	}

	public void setModel(TaxaddtionalModel model) {
		super.setModel(model);
	}
    public String getWhereSql()
    {
        return null;
    }
	@Override
	public void setNormalQueryObject(Object qryObject) {
		if (qryObject == null || !(qryObject instanceof PFQueryParams)) {
			return;
		}
		BillItem headItem = getBillCardPanel().getHeadItem("taxyear");
		headItem.setValue(headItem.getValueObject());
	}
	
	public String getSelectTaxYearValue(){
	return	(String) ((UIComboBox)getBillCardPanel().getHeadItem("taxyear").getComponent()).getSelectdItemValue();
	}
}
