package nc.ui.overtime.otleavebalance.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.ta.overtime.IOTLeaveBalanceMaintain;
import nc.ui.overtime.otleavebalance.view.OTLeaveBalanceOrgPanel;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.ta.overtime.AggOTLeaveBalanceVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;

import org.apache.commons.lang.StringUtils;

/**
 * ʾ�����ݵĲ�������
 * 
 * @author author
 * @version tempProject version
 */
public class OTLeaveBalanceMaintainProxy implements IDataOperationService, IQueryService,
	ISingleBillService<AggOTLeaveBalanceVO> {
    private OTLeaveBalanceOrgPanel orgPanel = null;
    private HierachicalDataAppModel hierachicalModel = null;

    @Override
    public IBill[] insert(IBill[] value) throws BusinessException {
	IOTLeaveBalanceMaintain operator = NCLocator.getInstance().lookup(IOTLeaveBalanceMaintain.class);
	AggOTLeaveBalanceVO[] vos = operator.insert((AggOTLeaveBalanceVO[]) value);
	return vos;
    }

    @Override
    public IBill[] update(IBill[] value) throws BusinessException {
	IOTLeaveBalanceMaintain operator = NCLocator.getInstance().lookup(IOTLeaveBalanceMaintain.class);
	AggOTLeaveBalanceVO[] vos = operator.update((AggOTLeaveBalanceVO[]) value);
	return vos;
    }

    @Override
    public IBill[] delete(IBill[] value) throws BusinessException {
	// Ŀǰ��ɾ�����������������������pubapp��֧�ִ����������ִ��ɾ������
	// ���ݵ�ɾ��ʵ����ʹ�õ��ǣ�ISingleBillService<AggSingleBill>��operateBill
	IOTLeaveBalanceMaintain operator = NCLocator.getInstance().lookup(IOTLeaveBalanceMaintain.class);
	operator.delete((AggOTLeaveBalanceVO[]) value);
	return value;
    }

    @Override
    public AggOTLeaveBalanceVO operateBill(AggOTLeaveBalanceVO bill) throws Exception {
	IOTLeaveBalanceMaintain operator = NCLocator.getInstance().lookup(IOTLeaveBalanceMaintain.class);
	operator.delete(new AggOTLeaveBalanceVO[] { bill });
	return bill;
    }

    @Override
    public Object[] queryByQueryScheme(IQueryScheme queryScheme) throws Exception {
	IOTLeaveBalanceMaintain query = NCLocator.getInstance().lookup(IOTLeaveBalanceMaintain.class);
	UFLiteralDate beginDate = new UFLiteralDate(((UFDate) getOrgPanel().getRefBeginDate().getValueObj()).toDate());
	UFLiteralDate endDate = new UFLiteralDate(((UFDate) getOrgPanel().getRefEndDate().getValueObj()).toDate());
	String pk_leavetypecopy = null;
	if (getHierachicalModel().getSelectedData() instanceof LeaveTypeCopyVO) {
	    pk_leavetypecopy = ((LeaveTypeCopyVO) getHierachicalModel().getSelectedData()).getPk_timeitemcopy();
	}

	if (StringUtils.isEmpty(pk_leavetypecopy)) {
	    return null;
	}

	return query.query(queryScheme, pk_leavetypecopy, beginDate.toString(), endDate.toString());
    }

    public OTLeaveBalanceOrgPanel getOrgPanel() {
	return orgPanel;
    }

    public void setOrgPanel(OTLeaveBalanceOrgPanel orgPanel) {
	this.orgPanel = orgPanel;
    }

    public HierachicalDataAppModel getHierachicalModel() {
	return hierachicalModel;
    }

    public void setHierachicalModel(HierachicalDataAppModel hierachicalModel) {
	this.hierachicalModel = hierachicalModel;
    }

}
