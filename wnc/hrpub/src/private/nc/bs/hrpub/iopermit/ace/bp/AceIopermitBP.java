package nc.bs.hrpub.iopermit.ace.bp;

import nc.impl.pubapp.pattern.data.vo.SchemeVOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.hrpub.mdmapping.IOPermitVO;

public class AceIopermitBP {

	public IOPermitVO[] queryByQueryScheme(IQueryScheme querySheme) {
		QuerySchemeProcessor p = new QuerySchemeProcessor(querySheme);
		p.appendFuncPermissionOrgSql();
		return new SchemeVOQuery<IOPermitVO>(IOPermitVO.class).query(querySheme,
				null);
	}
}
