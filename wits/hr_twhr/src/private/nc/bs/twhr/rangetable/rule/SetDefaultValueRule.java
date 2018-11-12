package nc.bs.twhr.rangetable.rule;

import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pub.ISuperVO;
import nc.vo.twhr.rangetable.RangeLineVO;
import nc.vo.twhr.rangetable.RangeTableAggVO;
import nc.vo.twhr.rangetable.RangeTableVO;

import org.apache.commons.lang.StringUtils;

public class SetDefaultValueRule implements IRule<RangeTableAggVO> {

	@Override
	public void process(RangeTableAggVO[] arg0) {
		for (RangeTableAggVO aggvo : arg0) {
			RangeTableVO table = aggvo.getParentVO();
			table.setStartdate(table.getStartdate().asBegin());
			table.setEnddate(table.getEnddate().asEnd());
			if (!StringUtils.isEmpty(table.getPk_group())) {
				SetChildren(aggvo.getChildren(RangeLineVO.class), "pk_group",
						table.getPk_group());
			}

			if (!StringUtils.isEmpty(table.getPk_org())) {
				SetChildren(aggvo.getChildren(RangeLineVO.class), "pk_org",
						table.getPk_org());
			}

			if (!StringUtils.isEmpty(table.getPk_org_v())) {
				SetChildren(aggvo.getChildren(RangeLineVO.class), "pk_org_v",
						table.getPk_org_v());
			}
		}
	}

	private void SetChildren(ISuperVO[] children, String fieldName,
			String fieldValue) {
		for (ISuperVO child : children) {
			child.setAttributeValue(fieldName, fieldValue);
		}
	}

}
