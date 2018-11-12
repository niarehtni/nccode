package nc.vo.hrpub.mdmapping;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.hrpub.mdmapping.MDClassVO")

public class AggMDClassVO extends AbstractBill {
	
	  @Override
	  public IBillMeta getMetaData() {
	  	IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggMDClassVOMeta.class);
	  	return billMeta;
	  }
	    
	  @Override
	  public MDClassVO getParentVO(){
	  	return (MDClassVO)this.getParent();
	  }
	  
}