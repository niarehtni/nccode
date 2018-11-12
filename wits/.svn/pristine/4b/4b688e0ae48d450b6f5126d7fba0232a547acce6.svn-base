package nc.bs.twhr.groupinsurance.ace.bp;

import nc.impl.pubapp.pattern.data.vo.SchemeVOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.twhr.groupinsurance.GroupInsuranceSettingVO;

public class AceGroupinsuranceBP {

	public GroupInsuranceSettingVO[] queryByQueryScheme(IQueryScheme querySheme) {
		QuerySchemeProcessor p = new QuerySchemeProcessor(querySheme);
		p.appendFuncPermissionOrgSql();
		return new SchemeVOQuery<GroupInsuranceSettingVO>(
				GroupInsuranceSettingVO.class).query(querySheme, null);
	}
}
