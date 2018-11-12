package nc.ui.overtime.segrule.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.ta.overtime.ISegruleMaintain;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.ta.overtime.AggSegRuleVO;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AceSegruleMaintainProxy implements IDataOperationService,
		IQueryService, ISingleBillService<AggSegRuleVO> {
	@Override
	public IBill[] insert(IBill[] value) throws BusinessException {
		ISegruleMaintain operator = NCLocator.getInstance().lookup(
				ISegruleMaintain.class);
		AggSegRuleVO[] vos = operator.insert((AggSegRuleVO[]) value);
		return vos;
	}

	@Override
	public IBill[] update(IBill[] value) throws BusinessException {
		ISegruleMaintain operator = NCLocator.getInstance().lookup(
				ISegruleMaintain.class);
		AggSegRuleVO[] vos = operator.update((AggSegRuleVO[]) value);
		return vos;
	}

	@Override
	public IBill[] delete(IBill[] value) throws BusinessException {
		// 目前的删除并不是走这个方法，由于pubapp不支持从这个服务中执行删除操作
		// 单据的删除实际上使用的是：ISingleBillService<AggSingleBill>的operateBill
		ISegruleMaintain operator = NCLocator.getInstance().lookup(
				ISegruleMaintain.class);
		operator.delete((AggSegRuleVO[]) value);
		return value;
	}

	@Override
	public AggSegRuleVO operateBill(AggSegRuleVO bill) throws Exception {
		ISegruleMaintain operator = NCLocator.getInstance().lookup(
				ISegruleMaintain.class);
		operator.delete(new AggSegRuleVO[] { bill });
		return bill;
	}

	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		ISegruleMaintain query = NCLocator.getInstance().lookup(
				ISegruleMaintain.class);
		return query.query(queryScheme);
	}

}
