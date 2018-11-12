package nc.vo.ta.leaveplan;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggLeavePlanVOMeta extends AbstractBillMeta{
	
	public AggLeavePlanVOMeta(){
		this.init();
	}
	
	private void init() {
		this.setParent(nc.vo.ta.leaveplan.LeavePlanVO.class);
	}
}