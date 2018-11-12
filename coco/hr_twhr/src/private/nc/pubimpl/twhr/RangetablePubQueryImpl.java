package nc.pubimpl.twhr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.pubitf.twhr.IRangetablePubQuery;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.twhr.rangetable.RangeLineVO;
import nc.vo.twhr.rangetable.RangeTableAggVO;
import nc.vo.twhr.rangetable.RangeTableVO;

public class RangetablePubQueryImpl implements IRangetablePubQuery {

	@SuppressWarnings("unchecked")
	@Override
	public RangeTableAggVO[] queryRangetableByType(String pk_org, int tableType, UFDate queryDate)
			throws BusinessException {

		// Ares.Tank 2018-7-26 16:47:26 走新方法 不需要组织
		return queryRangetableByType(tableType, queryDate);
	}

	@Override
	public RangeLineVO queryRangeLineByAmount(String pk_org, int tableType, UFDate queryDate, UFDouble amount)
			throws BusinessException {
		// Ares.Tank 2018-7-26 16:47:26 走新方法 不需要组织
		return queryRangeLineByAmount(tableType, queryDate, amount);
	}

	private RangeLineVO getRangeLineByAmount(UFDouble amount, RangeTableAggVO[] rangeTables) {
		for (RangeTableAggVO aggvo : rangeTables) {
			for (ISuperVO line : aggvo.getChildren(RangeLineVO.class)) {
				UFDouble stdUpperValue = ((RangeLineVO) line).getRangeupper();
				UFDouble stdLowerValue = ((RangeLineVO) line).getRangelower();
				if (amount.doubleValue() >= stdLowerValue.doubleValue()
						&& amount.doubleValue() <= stdUpperValue.doubleValue()) {
					return (RangeLineVO) line;
				}
			}
		}

		return null;
	}

	@Override
	public Map<UFDouble, RangeLineVO> batchQueryRangeLineByAmount(String pk_org, int tableType, UFDate queryDate,
			UFDouble[] amounts) throws BusinessException {

		// Ares.Tank 2018-7-26 16:47:26 走新方法 不需要组织
		return batchQueryRangeLineByAmount(tableType, queryDate, amounts);
	}

	// start 增加方法 Ares.Tank 2018-7-25 18:16:12
	@SuppressWarnings("unchecked")
	@Override
	public RangeTableAggVO[] queryRangetableByType(int tableType, UFDate queryDate) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String strCondition = "dr=0 " + (tableType == -1 ? "" : "' and tabletype=" + String.valueOf(tableType))
				+ " and startdate<='" + queryDate.toString() + "' and enddate>='" + queryDate.toString()
				+ "' and pk_group = 'GLOBLE00000000000000' " + " and pk_org = 'GLOBLE00000000000000' ";
		Collection<RangeTableVO> rangeTables = dao.retrieveByClause(RangeTableVO.class, strCondition);

		List<RangeTableAggVO> aggvos = new ArrayList<RangeTableAggVO>();
		if (rangeTables != null && rangeTables.size() > 0) {
			for (RangeTableVO rangeTable : rangeTables) {
				RangeTableAggVO aggvo = new RangeTableAggVO();
				aggvo.setParent(rangeTable);

				Collection<RangeLineVO> rangeLines = dao.retrieveByClause(RangeLineVO.class,
						"  dr=0 and pk_rangetable='" + rangeTable.getPk_rangetable() + "'");
				aggvo.setChildrenVO(rangeLines.toArray(new RangeLineVO[0]));
				aggvos.add(aggvo);
			}
		}

		return aggvos.toArray(new RangeTableAggVO[0]);
	}

	@Override
	public RangeLineVO queryRangeLineByAmount(int tableType, UFDate queryDate, UFDouble amount)
			throws BusinessException {
		RangeTableAggVO[] rangeTables = queryRangetableByType(tableType, queryDate);
		if (rangeTables != null && rangeTables.length > 0) {
			return getRangeLineByAmount(amount, rangeTables);
		}

		return null;
	}

	@Override
	public Map<UFDouble, RangeLineVO> batchQueryRangeLineByAmount(int tableType, UFDate queryDate, UFDouble[] amounts)
			throws BusinessException {
		RangeTableAggVO[] rangeTables = queryRangetableByType(tableType, queryDate);
		Map<UFDouble, RangeLineVO> rangeMap = new HashMap<UFDouble, RangeLineVO>();
		if (rangeTables != null && rangeTables.length > 0) {
			for (UFDouble amount : amounts) {
				RangeLineVO line = getRangeLineByAmount(amount, rangeTables);
				rangeMap.put(amount, line);
			}
		}

		return rangeMap;
	}
	// end 增加方法 Ares.Tank 2018-7-25 18:16:12

}
