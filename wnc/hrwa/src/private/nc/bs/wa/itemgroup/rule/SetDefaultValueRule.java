package nc.bs.wa.itemgroup.rule;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.vorg.OrgVersionVO;
import nc.vo.wa.itemgroup.AggItemGroupVO;
import nc.vo.wa.itemgroup.ItemGroupVO;

import org.apache.commons.lang.StringUtils;

public class SetDefaultValueRule implements IRule<AggItemGroupVO> {

	@Override
	public void process(AggItemGroupVO[] aggvos) {
		if (aggvos != null && aggvos.length > 0) {
			for (AggItemGroupVO aggvo : aggvos) {
				ItemGroupVO head = aggvo.getParentVO();
				if (StringUtils.isEmpty(head.getPk_org())) {
					if (!StringUtils.isEmpty(head.getPk_org_v())) {
						try {
							OrgVersionVO ovvo = (OrgVersionVO) new BaseDAO().retrieveByPK(OrgVersionVO.class,
									head.getPk_org_v());
							if (ovvo != null) {
								head.setPk_org(ovvo.getPk_org());

								if (aggvo.getAllChildrenVO() != null && aggvo.getAllChildrenVO().length > 0) {
									for (CircularlyAccessibleValueObject child : aggvo.getAllChildrenVO()) {
										child.setAttributeValue("pk_org", head.getPk_org());
									}
								}
							}
						} catch (DAOException e) {
							ExceptionUtils.wrappBusinessException(e.getMessage());
						}
					}
				}
			}
		}
	}

}
