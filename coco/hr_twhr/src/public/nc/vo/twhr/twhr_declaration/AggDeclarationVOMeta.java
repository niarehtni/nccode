package nc.vo.twhr.twhr_declaration;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggDeclarationVOMeta extends AbstractBillMeta {
  public AggDeclarationVOMeta() {
    this.init();
  }
  private void init() {
    this.setParent(nc.vo.twhr.twhr_declaration.DeclarationHVO.class);
    this.addChildren(nc.vo.twhr.twhr_declaration.CompanyBVO.class);
    this.addChildren(nc.vo.twhr.twhr_declaration.PartTimeBVO.class);
    this.addChildren(nc.vo.twhr.twhr_declaration.BusinessBVO.class);
    this.addChildren(nc.vo.twhr.twhr_declaration.NonPartTimeBVO.class);
  }
}