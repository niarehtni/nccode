package nc.ui.hrpub.mdmapping.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.hrpub.mdmapping.AggMDClassVO;
import nc.itf.hrpub.IMDMappingMaintain;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AceMDMappingMaintainProxy implements IDataOperationService,
		IQueryService ,ISingleBillService<AggMDClassVO>{
	@Override
	public IBill[] insert(IBill[] value) throws BusinessException {
		IMDMappingMaintain operator = NCLocator.getInstance().lookup(
				IMDMappingMaintain.class);
		AggMDClassVO[] vos = operator.insert((AggMDClassVO[]) value);
		return vos;
	}

	@Override
	public IBill[] update(IBill[] value) throws BusinessException {
		IMDMappingMaintain operator = NCLocator.getInstance().lookup(
				IMDMappingMaintain.class);
		AggMDClassVO[] vos = operator.update((AggMDClassVO[]) value);
		return vos;
	}

	@Override
	public IBill[] delete(IBill[] value) throws BusinessException {
		// 目前的删除并不是走这个方法，由于pubapp不支持从这个服务中执行删除操作
		// 单据的删除实际上使用的是：ISingleBillService<AggSingleBill>的operateBill
		IMDMappingMaintain operator = NCLocator.getInstance().lookup(
				IMDMappingMaintain.class);
		operator.delete((AggMDClassVO[]) value);
		return value;
	}
	
	@Override
	public AggMDClassVO operateBill(AggMDClassVO bill) throws Exception {
		IMDMappingMaintain operator = NCLocator.getInstance().lookup(
				IMDMappingMaintain.class);
		operator.delete(new AggMDClassVO[] { bill });
		return bill;
	}

	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IMDMappingMaintain query = NCLocator.getInstance().lookup(
				IMDMappingMaintain.class);
		return query.query(queryScheme);
	}

}
