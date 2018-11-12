package nc.ui.hrwa.projsalary.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrwa.IProjsalaryMaintain;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
@SuppressWarnings("restriction")
public class AceProjsalaryMaintainProxy implements IQueryService {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme) throws Exception {
		IProjsalaryMaintain query = NCLocator.getInstance().lookup(IProjsalaryMaintain.class);
		return query.query(queryScheme);
	}

}