package nc.ui.wa.itemgroup.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.itemgroup.AggItemGroupVO;
import nc.itf.hrwa.IWaitemgroupMaintain;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AceWaitemgroupMaintainProxy implements IDataOperationService,
		IQueryService ,ISingleBillService<AggItemGroupVO>{
	@Override
	public IBill[] insert(IBill[] value) throws BusinessException {
		IWaitemgroupMaintain operator = NCLocator.getInstance().lookup(
				IWaitemgroupMaintain.class);
		AggItemGroupVO[] vos = operator.insert((AggItemGroupVO[]) value);
		return vos;
	}

	@Override
	public IBill[] update(IBill[] value) throws BusinessException {
		IWaitemgroupMaintain operator = NCLocator.getInstance().lookup(
				IWaitemgroupMaintain.class);
		AggItemGroupVO[] vos = operator.update((AggItemGroupVO[]) value);
		return vos;
	}

	@Override
	public IBill[] delete(IBill[] value) throws BusinessException {
		// 目前的删除并不是走这个方法，由于pubapp不支持从这个服务中执行删除操作
		// 单据的删除实际上使用的是：ISingleBillService<AggSingleBill>的operateBill
		IWaitemgroupMaintain operator = NCLocator.getInstance().lookup(
				IWaitemgroupMaintain.class);
		operator.delete((AggItemGroupVO[]) value);
		return value;
	}
	
	@Override
	public AggItemGroupVO operateBill(AggItemGroupVO bill) throws Exception {
		IWaitemgroupMaintain operator = NCLocator.getInstance().lookup(
				IWaitemgroupMaintain.class);
		operator.delete(new AggItemGroupVO[] { bill });
		return bill;
	}

	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IWaitemgroupMaintain query = NCLocator.getInstance().lookup(
				IWaitemgroupMaintain.class);
		return query.query(queryScheme);
	}

}
