package nc.vo.wa.projsalary;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@SuppressWarnings("serial")
@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.wa.projsalary.ProjSalaryHVO")
public class AggProjSalaryVO extends AbstractBill {

	@Override
	public IBillMeta getMetaData() {
		IBillMeta billMeta = BillMetaFactory.getInstance().getBillMeta(AggProjSalaryVOMeta.class);
		return billMeta;
	}

	@Override
	public ProjSalaryHVO getParentVO() {
		return (ProjSalaryHVO) this.getParent();
	}
}