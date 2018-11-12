package nc.vo.hrpub.mdmapping;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggMDClassVOMeta extends AbstractBillMeta{
	
	public AggMDClassVOMeta(){
		this.init();
	}
	
	private void init() {
		this.setParent(nc.vo.hrpub.mdmapping.MDClassVO.class);
		this.addChildren(nc.vo.hrpub.mdmapping.MDPropertyVO.class);
	}
}