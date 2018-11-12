package nc.bs.overtime.segrule.rule;

import java.util.Collection;

import nc.bs.dao.BaseDAO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.overtime.AggSegRuleVO;
import nc.vo.ta.overtime.SegRuleVO;

public class DefaultRuleCheckRule implements IRule<AggSegRuleVO> {

    @Override
    public void process(AggSegRuleVO[] aggvos) {
	for (AggSegRuleVO aggvo : aggvos) {
	    SegRuleVO rule = aggvo.getParentVO();
	    if (rule.getIsdefault() != null && rule.getIsdefault().booleanValue()) {
		BaseDAO dao = new BaseDAO();
		try {
		    Collection defaultRules = dao.retrieveByClause(SegRuleVO.class,
			    "isnull(dr,0)=0 and isdefault='Y' and datetype=" + rule.getDatetype());

		    if (defaultRules != null && defaultRules.size() > 0) {
			throw new BusinessException("已存在同一日曆天類型的默認加班分段依據。");
		    }
		} catch (BusinessException e) {
		    ExceptionUtils.wrappBusinessException(e.getMessage());
		}
	    }
	}
    }

}
