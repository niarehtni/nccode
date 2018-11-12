package nc.bs.twhr.rangetable.ace.bp;

import nc.bs.twhr.rangetable.plugin.bpplugin.RangetablePluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.twhr.rangetable.RangeTableAggVO;

/**
 * 标准单据删除BP
 */
public class AceRangetableDeleteBP {

  public void delete(RangeTableAggVO[] bills) {

      DeleteBPTemplate<RangeTableAggVO> bp =
          new DeleteBPTemplate<RangeTableAggVO>(RangetablePluginPoint.DELETE);
     
      bp.delete(bills);
  }
}
