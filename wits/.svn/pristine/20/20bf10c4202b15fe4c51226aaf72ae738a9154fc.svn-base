package nc.vo.hrwa.incometax;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.hrwa.incometax.IncomeTaxVO")
/**
 * 
 * @author ward.wong
 * @date 20180126
 * @version v1.0
 *
 */
public class AggIncomeTaxVO extends AbstractBill {
	
	  @Override
	  public IBillMeta getMetaData() {
	  	IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggIncomeTaxVOMeta.class);
	  	return billMeta;
	  }
	    
	  @Override
	  public IncomeTaxVO getParentVO(){
	  	return (IncomeTaxVO)this.getParent();
	  }
	  
}