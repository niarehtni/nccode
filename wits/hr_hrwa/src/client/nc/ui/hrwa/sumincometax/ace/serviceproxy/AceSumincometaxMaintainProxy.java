package nc.ui.hrwa.sumincometax.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;
import nc.itf.hrwa.ISumincometaxMaintain;

/**
 * ʾ�����ݵĲ�������
 * 
 * @author author
 * @version tempProject version
 */
public class AceSumincometaxMaintainProxy implements IDataOperationService,
		IQueryService ,ISingleBillService<AggSumIncomeTaxVO>{
	@Override
	public IBill[] insert(IBill[] value) throws BusinessException {
		ISumincometaxMaintain operator = NCLocator.getInstance().lookup(
				ISumincometaxMaintain.class);
		AggSumIncomeTaxVO[] vos = operator.insert((AggSumIncomeTaxVO[]) value);
		return vos;
	}

	@Override
	public IBill[] update(IBill[] value) throws BusinessException {
		ISumincometaxMaintain operator = NCLocator.getInstance().lookup(
				ISumincometaxMaintain.class);
		AggSumIncomeTaxVO[] vos = operator.update((AggSumIncomeTaxVO[]) value);
		return vos;
	}

	@Override
	public IBill[] delete(IBill[] value) throws BusinessException {
		// Ŀǰ��ɾ�����������������������pubapp��֧�ִ����������ִ��ɾ������
		// ���ݵ�ɾ��ʵ����ʹ�õ��ǣ�ISingleBillService<AggSingleBill>��operateBill
		ISumincometaxMaintain operator = NCLocator.getInstance().lookup(
				ISumincometaxMaintain.class);
		operator.delete((AggSumIncomeTaxVO[]) value);
		return value;
	}
	
	@Override
	public AggSumIncomeTaxVO operateBill(AggSumIncomeTaxVO bill) throws Exception {
		ISumincometaxMaintain operator = NCLocator.getInstance().lookup(
				ISumincometaxMaintain.class);
		operator.delete(new AggSumIncomeTaxVO[] { bill });
		return bill;
	}

	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		ISumincometaxMaintain query = NCLocator.getInstance().lookup(
				ISumincometaxMaintain.class);
		return query.query(queryScheme);
	}

}
