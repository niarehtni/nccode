package nc.ui.hrta.leaveextrarest.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.leaveextrarest.AggLeaveExtraRestVO;
import nc.itf.hrta.ILeaveextrarestMaintain;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AceLeaveextrarestMaintainProxy implements IDataOperationService,
		IQueryService ,ISingleBillService<AggLeaveExtraRestVO>{
	@Override
	public IBill[] insert(IBill[] value) throws BusinessException {
		ILeaveextrarestMaintain operator = NCLocator.getInstance().lookup(
				ILeaveextrarestMaintain.class);
		AggLeaveExtraRestVO[] vos = operator.insert((AggLeaveExtraRestVO[]) value);
		return vos;
	}

	@Override
	public IBill[] update(IBill[] value) throws BusinessException {
		ILeaveextrarestMaintain operator = NCLocator.getInstance().lookup(
				ILeaveextrarestMaintain.class);
		AggLeaveExtraRestVO[] vos = operator.update((AggLeaveExtraRestVO[]) value);
		return vos;
	}

	@Override
	public IBill[] delete(IBill[] value) throws BusinessException {
		// 目前的删除并不是走这个方法，由于pubapp不支持从这个服务中执行删除操作
		// 单据的删除实际上使用的是：ISingleBillService<AggSingleBill>的operateBill
		ILeaveextrarestMaintain operator = NCLocator.getInstance().lookup(
				ILeaveextrarestMaintain.class);
		operator.delete((AggLeaveExtraRestVO[]) value);
		return value;
	}
	
	@Override
	public AggLeaveExtraRestVO operateBill(AggLeaveExtraRestVO bill) throws Exception {
		ILeaveextrarestMaintain operator = NCLocator.getInstance().lookup(
				ILeaveextrarestMaintain.class);
		operator.delete(new AggLeaveExtraRestVO[] { bill });
		return bill;
	}

	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		ILeaveextrarestMaintain query = NCLocator.getInstance().lookup(
				ILeaveextrarestMaintain.class);
		return query.query(queryScheme);
	}

}
