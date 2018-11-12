package nc.vo.ta.overtime;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.ta.overtime.SegDetailVO")
public class AggSegDetailVO extends AbstractBill {

    /**
     * serial no
     */
    private static final long serialVersionUID = 5390988695320443535L;

    @Override
    public IBillMeta getMetaData() {
	IBillMeta billMeta = BillMetaFactory.getInstance().getBillMeta(AggSegDetailVOMeta.class);
	return billMeta;
    }

    @Override
    public SegDetailVO getParentVO() {
	return (SegDetailVO) this.getParent();
    }

}