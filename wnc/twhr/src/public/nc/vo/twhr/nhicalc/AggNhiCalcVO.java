package nc.vo.twhr.nhicalc;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.twhr.nhicalc.NhiCalcVO")

public class AggNhiCalcVO extends AbstractBill {
	
	  @Override
	  public IBillMeta getMetaData() {
	  	IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggNhiCalcVOMeta.class);
	  	return billMeta;
	  }
	    
	  @Override
	  public NhiCalcVO getParentVO(){
	  	return (NhiCalcVO)this.getParent();
	  }
	  
}