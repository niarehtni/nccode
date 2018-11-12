package nc.vo.om.hrdept;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.om.hrdept.HRDeptAdjustVO")

public class AggHRDeptAdjustVO extends AbstractBill {
	
	  @Override
	  public IBillMeta getMetaData() {
	  	IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggHRDeptAdjustVOMeta.class);
	  	return billMeta;
	  }
	    
	  @Override
	  public HRDeptAdjustVO getParentVO(){
	  	return (HRDeptAdjustVO)this.getParent();
	  }
	  
}