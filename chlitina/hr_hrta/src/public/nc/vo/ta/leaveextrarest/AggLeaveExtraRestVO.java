package nc.vo.ta.leaveextrarest;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.ta.leaveextrarest.LeaveExtraRestVO")

public class AggLeaveExtraRestVO extends AbstractBill {
	
	  @Override
	  public IBillMeta getMetaData() {
	  	IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggLeaveExtraRestVOMeta.class);
	  	return billMeta;
	  }
	    
	  @Override
	  public LeaveExtraRestVO getParentVO(){
	  	return (LeaveExtraRestVO)this.getParent();
	  }
	  
}