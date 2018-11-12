package nc.bs.twhr.groupgrade.ace.bp;

import nc.impl.pubapp.pattern.data.vo.SchemeVOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.twhr.groupinsurance.GroupInsuranceGradeVO;

public class AceGroupgradeBP {

	public GroupInsuranceGradeVO[] queryByQueryScheme(IQueryScheme querySheme) {
		QuerySchemeProcessor p = new QuerySchemeProcessor(querySheme);
		p.appendFuncPermissionOrgSql();
		return new SchemeVOQuery<GroupInsuranceGradeVO>(GroupInsuranceGradeVO.class).query(querySheme,
				null);
	}
}
