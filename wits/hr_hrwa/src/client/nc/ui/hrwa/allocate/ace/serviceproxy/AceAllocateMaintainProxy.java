package nc.ui.hrwa.allocate.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrwa.IAllocateMaintain;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
@SuppressWarnings("restriction")
public class AceAllocateMaintainProxy implements IQueryService {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme) throws Exception {
		IAllocateMaintain query = NCLocator.getInstance().lookup(IAllocateMaintain.class);
		return query.query(queryScheme);
	}

}