package nc.ui.hrjf.deptadj.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrjf.IDeptadjMaintain;
import nc.itf.hrta.ILeaveplanMaintain;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.ta.leaveplan.AggLeavePlanVO;

/**
 * ʾ�����ݵĲ�������
 * 
 * @author author
 * @version tempProject version
 */
public class AceDeptadjMaintainProxy implements IDataOperationService,
IQueryService ,ISingleBillService<AggHRDeptAdjustVO>{
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IDeptadjMaintain query = NCLocator.getInstance().lookup(
				IDeptadjMaintain.class);
		return query.query(queryScheme);
	}

	

	@Override
	public IBill[] insert(IBill[] value) throws BusinessException {
		IDeptadjMaintain insert = NCLocator.getInstance().lookup(
				IDeptadjMaintain.class);
		return insert.insert((AggHRDeptAdjustVO[])value);
	}

	@Override
	public IBill[] update(IBill[] value) throws BusinessException {
		IDeptadjMaintain update = NCLocator.getInstance().lookup(
				IDeptadjMaintain.class);
		return update.update((AggHRDeptAdjustVO[])value);
	}

	@Override
	public IBill[] delete(IBill[] value) throws BusinessException {
		// Ŀǰ��ɾ�����������������������pubapp��֧�ִ����������ִ��ɾ������
				// ���ݵ�ɾ��ʵ����ʹ�õ��ǣ�ISingleBillService<AggSingleBill>��operateBill
		IDeptadjMaintain operator = NCLocator.getInstance().lookup(
				IDeptadjMaintain.class);
				operator.delete((AggHRDeptAdjustVO[]) value);
				return value;
	}



	@Override
	public AggHRDeptAdjustVO operateBill(AggHRDeptAdjustVO bill)
			throws Exception {
		IDeptadjMaintain operator = NCLocator.getInstance().lookup(
				IDeptadjMaintain.class);
		operator.delete(new AggHRDeptAdjustVO[] { bill });
		return bill;
	}



	
}