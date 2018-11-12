package nc.ui.hrwa.incometax.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrwa.IIncometaxMaintain;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.wa.taxrate.AggTaxBaseVO;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AceIncometaxMaintainProxy implements IDataOperationService,IQueryService,ISingleBillService<AggIncomeTaxVO> {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IIncometaxMaintain query = NCLocator.getInstance().lookup(
				IIncometaxMaintain.class);
		return query.query(queryScheme);
	}

	public AggIncomeTaxVO operateBill(AggIncomeTaxVO bill) throws Exception {
		IIncometaxMaintain operator = NCLocator.getInstance().lookup(
				IIncometaxMaintain.class);
		operator.delete(new AggIncomeTaxVO[] { bill },null);
		return bill;
	}

	public IBill[] insert(IBill[] value) throws BusinessException {
		IIncometaxMaintain operator = NCLocator.getInstance().lookup(
				IIncometaxMaintain.class);
		AggIncomeTaxVO[] vos = operator.insert((AggIncomeTaxVO[]) value,null);
		return vos;
	}

	@Override
	public IBill[] update(IBill[] value) throws BusinessException {
		IIncometaxMaintain operator = NCLocator.getInstance().lookup(
				IIncometaxMaintain.class);
		AggIncomeTaxVO[] vos = operator.update((AggIncomeTaxVO[]) value,null);
		return vos;
	}

	@Override
	public IBill[] delete(IBill[] value) throws BusinessException {
		// 目前的删除并不是走这个方法，由于pubapp不支持从这个服务中执行删除操作
		// 单据的删除实际上使用的是：ISingleBillService<AggSingleBill>的operateBill
		IIncometaxMaintain operator = NCLocator.getInstance().lookup(
				IIncometaxMaintain.class);
		operator.delete((AggIncomeTaxVO[]) value,null);
		return value;
	}

}