package nc.vo.ta.overtime;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggOTLeaveBalanceVOMeta extends AbstractBillMeta{
	
	public AggOTLeaveBalanceVOMeta(){
		this.init();
	}
	
	private void init() {
		this.setParent(nc.vo.ta.overtime.OTLeaveBalanceVO.class);
		this.addChildren(nc.vo.ta.overtime.OTBalanceDetailVO.class);
	}
}