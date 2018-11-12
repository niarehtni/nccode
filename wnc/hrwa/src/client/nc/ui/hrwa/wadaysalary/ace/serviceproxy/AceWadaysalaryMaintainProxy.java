package nc.ui.hrwa.wadaysalary.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.paydata.AggDaySalaryVO;
import nc.vo.wa.pub.WaDayLoginContext;
import nc.itf.hrwa.IWadaysalaryMaintain;

/**
 * ʾ�����ݵĲ�������
 * 
 * @author author
 * @version tempProject version
 */
public class AceWadaysalaryMaintainProxy implements IDataOperationService,
		IQueryService ,ISingleBillService<AggDaySalaryVO>{
	
	private WaDayLoginContext context;
	
	
	public WaDayLoginContext getContext() {
		return context;
	}

	public void setContext(WaDayLoginContext context) {
		this.context = context;
	}

	@Override
	public IBill[] insert(IBill[] value) throws BusinessException {
		IWadaysalaryMaintain operator = NCLocator.getInstance().lookup(
				IWadaysalaryMaintain.class);
		AggDaySalaryVO[] vos = operator.insert((AggDaySalaryVO[]) value);
		return vos;
	}

	@Override
	public IBill[] update(IBill[] value) throws BusinessException {
		IWadaysalaryMaintain operator = NCLocator.getInstance().lookup(
				IWadaysalaryMaintain.class);
		AggDaySalaryVO[] vos = operator.update((AggDaySalaryVO[]) value);
		return vos;
	}

	@Override
	public IBill[] delete(IBill[] value) throws BusinessException {
		// Ŀǰ��ɾ�����������������������pubapp��֧�ִ����������ִ��ɾ������
		// ���ݵ�ɾ��ʵ����ʹ�õ��ǣ�ISingleBillService<AggSingleBill>��operateBill
		IWadaysalaryMaintain operator = NCLocator.getInstance().lookup(
				IWadaysalaryMaintain.class);
		operator.delete((AggDaySalaryVO[]) value);
		return value;
	}
	
	@Override
	public AggDaySalaryVO operateBill(AggDaySalaryVO bill) throws Exception {
		IWadaysalaryMaintain operator = NCLocator.getInstance().lookup(
				IWadaysalaryMaintain.class);
		operator.delete(new AggDaySalaryVO[] { bill });
		return bill;
	}

	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IWadaysalaryMaintain query = NCLocator.getInstance().lookup(
				IWadaysalaryMaintain.class);
		return query.query(queryScheme,getContext());
	}

}
