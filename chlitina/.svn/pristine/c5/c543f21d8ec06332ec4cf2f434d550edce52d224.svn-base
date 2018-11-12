package nc.vo.twhr.twhr_declaration;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.twhr.twhr_declaration.DeclarationHVO")
public class AggDeclarationVO extends AbstractBill {

  @Override
  public IBillMeta getMetaData() {
    IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggDeclarationVOMeta.class);
    return billMeta;
  }

  @Override
  public DeclarationHVO getParentVO() {
    return (DeclarationHVO) this.getParent();
  }
}