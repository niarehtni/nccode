package nc.bs.overtime.segrule.rule;

import java.util.Collection;

import nc.bs.dao.BaseDAO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.overtime.AggSegRuleVO;
import nc.vo.ta.timeitem.OverTimeTypeVO;

/**
 * 刪除時檢查是否被引用，如引用（含已刪除加班分段明細）則不允許刪除，只能停用
 * 
 * @author ssx
 * @since 2018-05-01
 * @version 6.5
 */
public class RefCheckRule implements IRule<AggSegRuleVO> {
	BaseDAO baseDao = new BaseDAO();

	@Override
	public void process(AggSegRuleVO[] aggvos) {
		try {
			if (aggvos != null && aggvos.length > 0) {
				for (AggSegRuleVO aggvo : aggvos) {
					if (ifRefByOvertimeType(aggvo)) {
						throw new BusinessException("加班分段依據 ["
								+ aggvo.getParentVO().getCode()
								+ "] 已經被引用，無法刪除。");
					}
				}
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
	}

	private boolean ifRefByOvertimeType(AggSegRuleVO aggvo)
			throws BusinessException {
		Collection ovRegs = baseDao.retrieveByClause(OverTimeTypeVO.class,
				"pk_segrule='" + aggvo.getPrimaryKey() + "' and dr=0");
		if (ovRegs != null && ovRegs.size() > 0) {
			return true;
		}
		return false;
	}

}
