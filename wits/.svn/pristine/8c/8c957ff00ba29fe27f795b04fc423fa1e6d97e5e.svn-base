package nc.vo.wa.allocate;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@SuppressWarnings("serial")
@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.wa.allocate.AllocateOutHVO")
public class AggAllocateOutVO extends AbstractBill {

	@Override
	public IBillMeta getMetaData() {
		IBillMeta billMeta = BillMetaFactory.getInstance().getBillMeta(AggAllocateOutVOMeta.class);
		return billMeta;
	}

	@Override
	public AllocateOutHVO getParentVO() {
		return (AllocateOutHVO) this.getParent();
	}
}