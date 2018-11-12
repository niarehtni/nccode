package nc.ui.hrta.leaveplan.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.leaveplan.AggLeavePlanVO;
import nc.itf.hrta.ILeaveplanMaintain;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AceLeaveplanMaintainProxy implements IDataOperationService,
		IQueryService ,ISingleBillService<AggLeavePlanVO>{
	@Override
	public IBill[] insert(IBill[] value) throws BusinessException {
		ILeaveplanMaintain operator = NCLocator.getInstance().lookup(
				ILeaveplanMaintain.class);
		AggLeavePlanVO[] vos = operator.insert((AggLeavePlanVO[]) value);
		return vos;
	}

	@Override
	public IBill[] update(IBill[] value) throws BusinessException {
		ILeaveplanMaintain operator = NCLocator.getInstance().lookup(
				ILeaveplanMaintain.class);
		AggLeavePlanVO[] vos = operator.update((AggLeavePlanVO[]) value);
		return vos;
	}

	@Override
	public IBill[] delete(IBill[] value) throws BusinessException {
		// 目前的删除并不是走这个方法，由于pubapp不支持从这个服务中执行删除操作
		// 单据的删除实际上使用的是：ISingleBillService<AggSingleBill>的operateBill
		ILeaveplanMaintain operator = NCLocator.getInstance().lookup(
				ILeaveplanMaintain.class);
		operator.delete((AggLeavePlanVO[]) value);
		return value;
	}
	
	@Override
	public AggLeavePlanVO operateBill(AggLeavePlanVO bill) throws Exception {
		ILeaveplanMaintain operator = NCLocator.getInstance().lookup(
				ILeaveplanMaintain.class);
		operator.delete(new AggLeavePlanVO[] { bill });
		return bill;
	}

	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		ILeaveplanMaintain query = NCLocator.getInstance().lookup(
				ILeaveplanMaintain.class);
		return query.query(queryScheme);
	}

}
