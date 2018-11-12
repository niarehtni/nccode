package nc.ui.twhr.twhr_declaration.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.twhr.ITwhr_declarationMaintain;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AceTwhr_declarationMaintainProxy implements IQueryService {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		ITwhr_declarationMaintain query = NCLocator.getInstance().lookup(
				ITwhr_declarationMaintain.class);
		return query.query(queryScheme);
	}

}