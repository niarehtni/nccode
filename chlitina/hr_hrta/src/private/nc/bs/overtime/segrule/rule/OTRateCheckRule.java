package nc.bs.overtime.segrule.rule;

import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.overtime.AggSegRuleVO;
import nc.vo.ta.overtime.SegRuleTermVO;
import nc.vo.ta.overtime.TaxFlagEnum;

public class OTRateCheckRule implements IRule<AggSegRuleVO> {

    @Override
    public void process(AggSegRuleVO[] aggvos) {
	for (AggSegRuleVO aggvo : aggvos) {
	    SegRuleTermVO[] terms = (SegRuleTermVO[]) aggvo.getChildren(SegRuleTermVO.class);
	    for (SegRuleTermVO term : terms) {
		if (term.getTaxflag().equals(TaxFlagEnum.TAXABLE) && term.getTaxableotrate() == null) {
		    ExceptionUtils.wrappBusinessException("應稅分段 [" + term.getSegno() + "] 未指定有效的應稅加班分段費率。");
		}

		if (term.getTaxflag().equals(TaxFlagEnum.TAXFREE) && term.getTaxfreeotrate() == null) {
		    ExceptionUtils.wrappBusinessException("免税分段 [" + term.getSegno() + "] 未指定有效的免税加班分段費率。");
		}
	    }
	}
    }

}
