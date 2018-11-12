package nc.vo.ta.overtime;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggSegDetailVOMeta extends AbstractBillMeta{
	
	public AggSegDetailVOMeta(){
		this.init();
	}
	
	private void init() {
		this.setParent(nc.vo.ta.overtime.SegDetailVO.class);
		this.addChildren(nc.vo.ta.overtime.SegDetailConsumeVO.class);
	}
}