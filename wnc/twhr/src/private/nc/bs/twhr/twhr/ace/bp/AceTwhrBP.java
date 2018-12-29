package nc.bs.twhr.twhr.ace.bp;

import nc.impl.pubapp.pattern.data.vo.SchemeVOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.twhr.nhicalc.BaoAccountVO;

public class AceTwhrBP {

	public BaoAccountVO[] queryByQueryScheme(IQueryScheme querySheme) {
		QuerySchemeProcessor p = new QuerySchemeProcessor(querySheme);
		p.appendFuncPermissionOrgSql();
		return new SchemeVOQuery<BaoAccountVO>(BaoAccountVO.class).query(querySheme,
				null);
	}
}
