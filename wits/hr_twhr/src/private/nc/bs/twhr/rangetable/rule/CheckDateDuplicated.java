package nc.bs.twhr.rangetable.rule;

import java.util.Collection;

import nc.bs.dao.BaseDAO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.twhr.rangetable.RangeTableAggVO;
import nc.vo.twhr.rangetable.RangeTableVO;

import org.apache.commons.lang.StringUtils;

public class CheckDateDuplicated implements IRule<RangeTableAggVO> {

	@Override
	public void process(RangeTableAggVO[] vos) {
		if (vos != null && vos.length > 0) {
			try {
				for (RangeTableAggVO aggvo : vos) {
					checkDate(aggvo);
				}
			} catch (BusinessException e) {
				ExceptionUtils.wrappBusinessException(e.getMessage());
			}
		}
	}

	private void checkDate(RangeTableAggVO aggvo) throws BusinessException {
		RangeTableVO headVO = (RangeTableVO) aggvo.getParent();
		String strSQL = "";
		if (StringUtils.isEmpty(headVO.getPk_rangetable())) {
			strSQL = "pk_org='" + headVO.getPk_org()
					+ "' and tabletype="
					+ String.valueOf(headVO.getTabletype())
					+ " and dr=0 and enddate >= '" // and [enabled]=N'Y'
					+ headVO.getStartdate().toString() + "' and startdate <='"
					+ headVO.getEnddate().toString() + "'";
		} else {
			strSQL = "pk_org='" + headVO.getPk_org() + "' and pk_rangetable<>'"
					+ headVO.getPk_rangetable()
					+ "' and tabletype="
					+ String.valueOf(headVO.getTabletype())
					+ " and dr=0 and enddate >= '" // and [enabled]=N'Y'
					+ headVO.getStartdate().toString() + "' and startdate <='"
					+ headVO.getEnddate().toString() + "'";
		}

		BaseDAO dao = new BaseDAO();
		Collection<RangeTableVO> rangeTables = dao.retrieveByClause(
				RangeTableVO.class, strSQL);

		if (rangeTables != null && rangeTables.size() > 0) {
			throw new BusinessException(
					NCLangRes4VoTransl.getNCLangRes().getStrByID("68861025",
							"RangetableGenerateAction-0006"/*
															 * 当前级距表生效范围与系统中现有级距表重叠
															 * ， 请修正后保存 。
															 */));
		}
	}
}
