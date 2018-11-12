package nc.vo.hrwa.sumincometax;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.hrwa.sumincometax.SumIncomeTaxVO")

public class AggSumIncomeTaxVO extends AbstractBill {
	
	  @Override
	  public IBillMeta getMetaData() {
	  	IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggSumIncomeTaxVOMeta.class);
	  	return billMeta;
	  }
	    
	  @Override
	  public SumIncomeTaxVO getParentVO(){
	  	return (SumIncomeTaxVO)this.getParent();
	  }
	  
}