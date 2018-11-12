package nc.vo.ta.leaveplan;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.ta.leaveplan.LeavePlanVO")

public class AggLeavePlanVO extends AbstractBill {
	
	  @Override
	  public IBillMeta getMetaData() {
	  	IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggLeavePlanVOMeta.class);
	  	return billMeta;
	  }
	    
	  @Override
	  public LeavePlanVO getParentVO(){
	  	return (LeavePlanVO)this.getParent();
	  }
	  
}