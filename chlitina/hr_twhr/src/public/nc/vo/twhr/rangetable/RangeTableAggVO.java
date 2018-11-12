package nc.vo.twhr.rangetable;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.twhr.rangetable.RangeTableVO")
public class RangeTableAggVO extends AbstractBill {

    /**
     * serial no
     */
    private static final long serialVersionUID = 9034899922431611637L;

    @Override
    public IBillMeta getMetaData() {
	IBillMeta billMeta = BillMetaFactory.getInstance().getBillMeta(RangeTableAggVOMeta.class);
	return billMeta;
    }

    @Override
    public RangeTableVO getParentVO() {
	return (RangeTableVO) this.getParent();
    }
}