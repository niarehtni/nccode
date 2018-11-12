package nc.bs.hrpub.ioschema.ace.bp;

import nc.impl.pubapp.pattern.data.vo.SchemeVOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.hrpub.mdmapping.IOSchemaVO;

public class AceIOSchemaBP {

	public IOSchemaVO[] queryByQueryScheme(IQueryScheme querySheme) {
		QuerySchemeProcessor p = new QuerySchemeProcessor(querySheme);
		p.appendFuncPermissionOrgSql();
		return new SchemeVOQuery<IOSchemaVO>(IOSchemaVO.class).query(querySheme,
				null);
	}
}
