package nc.ui.twhr.nhicalc.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.ui.hr.pf.model.PFAppModelService;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.twhr.nhicalc.AggNhiCalcVO;
import nc.itf.twhr.INhicalcMaintain;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AceNhicalcMaintainProxy extends PFAppModelService implements IDataOperationService,
		IQueryService ,ISingleBillService<AggNhiCalcVO>{
	@Override
	public IBill[] insert(IBill[] value) throws BusinessException {
		INhicalcMaintain operator = NCLocator.getInstance().lookup(
				INhicalcMaintain.class);
		AggNhiCalcVO[] vos = operator.insert((AggNhiCalcVO[]) value);
		return vos;
	}

	@Override
	public IBill[] update(IBill[] value) throws BusinessException {
		INhicalcMaintain operator = NCLocator.getInstance().lookup(
				INhicalcMaintain.class);
		AggNhiCalcVO[] vos = operator.update((AggNhiCalcVO[]) value);
		return vos;
	}

	@Override
	public IBill[] delete(IBill[] value) throws BusinessException {
		// 目前的删除并不是走这个方法，由于pubapp不支持从这个服务中执行删除操作
		// 单据的删除实际上使用的是：ISingleBillService<AggSingleBill>的operateBill
		INhicalcMaintain operator = NCLocator.getInstance().lookup(
				INhicalcMaintain.class);
		operator.delete((AggNhiCalcVO[]) value);
		return value;
	}
	
	@Override
	public AggNhiCalcVO operateBill(AggNhiCalcVO bill) throws Exception {
		INhicalcMaintain operator = NCLocator.getInstance().lookup(
				INhicalcMaintain.class);
		operator.delete(new AggNhiCalcVO[] { bill });
		return bill;
	}

	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		INhicalcMaintain query = NCLocator.getInstance().lookup(
				INhicalcMaintain.class);
		return query.query(queryScheme);
	}

}
