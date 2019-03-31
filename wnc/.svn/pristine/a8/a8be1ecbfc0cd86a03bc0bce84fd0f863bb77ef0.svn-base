package nc.vo.wa.itemgroup;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.wa.itemgroup.ItemGroupVO")
public class AggItemGroupVO extends AbstractBill {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = -6636182337148687565L;

	@Override
	public IBillMeta getMetaData() {
		IBillMeta billMeta = BillMetaFactory.getInstance().getBillMeta(AggItemGroupVOMeta.class);
		return billMeta;
	}

	@Override
	public ItemGroupVO getParentVO() {
		return (ItemGroupVO) this.getParent();
	}

}