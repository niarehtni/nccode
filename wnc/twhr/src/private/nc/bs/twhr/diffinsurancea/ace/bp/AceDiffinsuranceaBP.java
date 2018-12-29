package nc.bs.twhr.diffinsurancea.ace.bp;

import nc.impl.pubapp.pattern.data.vo.SchemeVOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.twhr.diffinsurance.DiffinsuranceVO;

public class AceDiffinsuranceaBP {

	public DiffinsuranceVO[] queryByQueryScheme(IQueryScheme querySheme) {
		QuerySchemeProcessor p = new QuerySchemeProcessor(querySheme);
		p.appendFuncPermissionOrgSql();
		return new SchemeVOQuery<DiffinsuranceVO>(DiffinsuranceVO.class).query(querySheme, null);
	}
}
