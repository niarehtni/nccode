package nc.ui.overtime.otleavebalance.handler;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.ui.overtime.otleavebalance.view.OTLeaveBalanceOrgPanel;
import nc.ui.pub.bill.BillScrollPane.BillTable;
import nc.ui.pubapp.uif2app.event.list.ListBodyRowChangedEvent;
import nc.ui.pubapp.uif2app.event.list.ListHeadRowChangedEvent;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.overtime.OTBalanceDetailVO;
import nc.vo.ta.pub.TALoginContext;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;

import org.apache.commons.lang.StringUtils;

public class MouseClickEventHandler implements AppEventListener {
    private TALoginContext context = null;
    private AbstractAppModel hierachicalModel = null;
    private OTLeaveBalanceOrgPanel orgpanel = null;
    private BillListView otListView = null;
    private AbstractUIAppModel headModel;
    private AbstractUIAppModel otListModel;

    @Override
    public void handleEvent(AppEvent e) {
	String pk_leavetype = null;
	try {
	    pk_leavetype = SysInitQuery.getParaString(getContext().getPk_org(), "TWHRT08");
	    LeaveTypeCopyVO leaveTypeVo = (LeaveTypeCopyVO) getHierachicalModel().getSelectedData();
	    if (leaveTypeVo != null) {
		if (e instanceof ListHeadRowChangedEvent) {
		    // 表头点击
		    if (leaveTypeVo != null && leaveTypeVo.getPk_timeitemcopy().equals(pk_leavetype)) {
			// 加班转调休
			String pk_psndoc = (String) ((BillTable) e.getSource()).getModel().getValueAt(
				((ListHeadRowChangedEvent) e).getRow(), 0);

			OTBalanceDetailVO[] detailVos = getSegDetailService().getOvertimeToRestByType(
				getContext().getPk_org(), pk_psndoc,
				new UFLiteralDate(((UFDate) getOrgpanel().getRefBeginDate().getValueObj()).toDate()),
				new UFLiteralDate(((UFDate) getOrgpanel().getRefEndDate().getValueObj()).toDate()),
				null);
			this.getOtListView().getBillListPanel().setBodyValueVO(detailVos);
		    }
		} else if (e instanceof ListBodyRowChangedEvent) {
		    // 表体点击
		    String pk_sourceBill = (String) ((BillTable) e.getSource()).getModel().getValueAt(
			    ((ListBodyRowChangedEvent) e).getRow(), 0);
		    if (!StringUtils.isEmpty(pk_sourceBill)) {

		    }
		}
	    }
	} catch (BusinessException ex) {
	    ExceptionUtils.wrappBusinessException(ex.getMessage());
	}
    }

    private ISegDetailService service = null;

    private ISegDetailService getSegDetailService() {
	if (service == null) {
	    service = NCLocator.getInstance().lookup(ISegDetailService.class);
	}
	return service;
    }

    public AbstractAppModel getHierachicalModel() {
	return hierachicalModel;
    }

    public void setHierachicalModel(AbstractAppModel hierachicalModel) {
	this.hierachicalModel = hierachicalModel;
    }

    public TALoginContext getContext() {
	return context;
    }

    public void setContext(TALoginContext context) {
	this.context = context;
    }

    public OTLeaveBalanceOrgPanel getOrgpanel() {
	return orgpanel;
    }

    public void setOrgpanel(OTLeaveBalanceOrgPanel orgpanel) {
	this.orgpanel = orgpanel;
    }

    public BillListView getOtListView() {
	return otListView;
    }

    public void setOtListView(BillListView otListView) {
	this.otListView = otListView;
    }

    public AbstractUIAppModel getHeadModel() {
	return headModel;
    }

    public void setHeadModel(AbstractUIAppModel headModel) {
	this.headModel = headModel;
	this.headModel.addAppEventListener(this);
    }

    public AbstractUIAppModel getOtListModel() {
	return otListModel;
    }

    public void setOtListModel(AbstractUIAppModel otListModel) {
	this.otListModel = otListModel;
	this.otListModel.addAppEventListener(this);
    }

}
