package nc.bs.hrta.leaveextrarest.ace.bp;

import nc.bs.dao.BaseDAO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.leaveextrarest.AggLeaveExtraRestVO;
import nc.vo.ta.leaveextrarest.LeaveExtraRestVO;

import org.apache.commons.lang.StringUtils;

public class CheckSettledRule implements IRule<AggLeaveExtraRestVO> {

	@Override
	public void process(AggLeaveExtraRestVO[] aggvos) {
		for (AggLeaveExtraRestVO aggvo : aggvos) {
			LeaveExtraRestVO vo = aggvo.getParentVO();
			try {
				UFLiteralDate maxLeaveDate = vo.getExpiredate();

				String strSQL = "select distinct settledate from tbm_extrarest where pk_psndoc='" + vo.getPk_psndoc()
						+ "' and expiredate='" + maxLeaveDate.toString() + "' and settledate is not null";
				String settleDate = (String) new BaseDAO().executeQuery(strSQL, new ColumnProcessor());
				if (!StringUtils.isEmpty(settleDate)) {
					throw new BusinessException("員工 ["
							+ ((PsndocVO) new BaseDAO().retrieveByPK(PsndocVO.class, vo.getPk_psndoc())).getCode()
							+ "] 的年度補休已結算，無法新增。");
				}
			} catch (BusinessException e) {
				ExceptionUtils.wrappBusinessException(e.getMessage());
			}
		}
	}

}
