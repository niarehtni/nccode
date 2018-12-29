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
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class OTLeaveBalanceMaintainProxy implements IDataOperationService, IQueryService,
	ISingleBillService<AggOTLeaveBalanceVO> {
    private OTLeaveBalanceOrgPanel orgPanel = null;
    private HierachicalDataAppModel hierachicalModel = null;

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

    @Override
    public AggOTLeaveBalanceVO operateBill(AggOTLeaveBalanceVO paramE) throws Exception {
	// TODO 自動產生的方法 Stub
	return null;
    }

    @Override
    public IBill[] insert(IBill[] paramArrayOfIBill) throws BusinessException {
	// TODO 自動產生的方法 Stub
	return null;
    }

    @Override
    public IBill[] update(IBill[] paramArrayOfIBill) throws BusinessException {
	// TODO 自動產生的方法 Stub
	return null;
    }

    @Override
    public IBill[] delete(IBill[] paramArrayOfIBill) throws BusinessException {
	// TODO 自動產生的方法 Stub
	return null;
    }

}
