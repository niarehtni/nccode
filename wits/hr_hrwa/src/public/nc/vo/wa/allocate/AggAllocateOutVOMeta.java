package nc.vo.wa.allocate;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggAllocateOutVOMeta extends AbstractBillMeta {
	public AggAllocateOutVOMeta() {
		this.init();
	}

	private void init() {
		this.setParent(nc.vo.wa.allocate.AllocateOutHVO.class);
	}
}