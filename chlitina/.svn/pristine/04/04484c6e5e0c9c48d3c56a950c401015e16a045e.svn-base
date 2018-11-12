package nc.vo.ta.overtime;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.ta.overtime.SegDetailVO")

public class AggSegDetailVO extends AbstractBill {
	
	  @Override
	  public IBillMeta getMetaData() {
	  	IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggSegDetailVOMeta.class);
	  	return billMeta;
	  }
	    
	  @Override
	  public SegDetailVO getParentVO(){
	  	return (SegDetailVO)this.getParent();
	  }
	  
}