package nc.vo.wa.paydata;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.wa.paydata.DaySalaryVO")

public class AggDaySalaryVO extends AbstractBill {
	
	  @Override
	  public IBillMeta getMetaData() {
	  	IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggDaySalaryVOMeta.class);
	  	return billMeta;
	  }
	    
	  @Override
	  public DaySalaryVO getParentVO(){
	  	return (DaySalaryVO)this.getParent();
	  }
	  
}