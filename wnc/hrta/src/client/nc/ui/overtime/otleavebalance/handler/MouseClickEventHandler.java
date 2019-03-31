package nc.ui.overtime.otleavebalance.handler;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.ta.leaveextrarest.ILeaveExtraRestService;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.ui.overtime.otleavebalance.view.OTLeaveBalanceOrgPanel;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
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
import nc.vo.ta.overtime.OTBalanceLeaveVO;
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
	private BillListView leaveListView;

	@Override
	public void handleEvent(AppEvent e) {
		if (StringUtils.isEmpty(getContext().getPk_org())) {
			return;
		}

		String pk_otleavetype = null;
		String pk_exleavetype = null;
		OTBalanceDetailVO[] detailVos = null;
		try {
			pk_otleavetype = SysInitQuery.getParaString(getContext().getPk_org(), "TWHRT08");
			pk_exleavetype = SysInitQuery.getParaString(getContext().getPk_org(), "TWHRT10");
			LeaveTypeCopyVO leaveTypeVo = (LeaveTypeCopyVO) getHierachicalModel().getSelectedData();

			if (leaveTypeVo != null) {
				if (e instanceof ListHeadRowChangedEvent) {
					String pk_psndoc = (String) ((BillTable) e.getSource()).getModel().getValueAt(
							((ListHeadRowChangedEvent) e).getRow(),
							((ListHeadRowChangedEvent) e).getBillListPanel().getHeadBillModel()
									.getBodyColByKey("pk_psndoc") - 1);
					UFLiteralDate beginDate = (UFLiteralDate) ((BillTable) e.getSource()).getModel().getValueAt(
							((ListHeadRowChangedEvent) e).getRow(),
							((ListHeadRowChangedEvent) e).getBillListPanel().getHeadBillModel()
									.getBodyColByKey("qstartdate"));
					UFLiteralDate endDate = (UFLiteralDate) ((BillTable) e.getSource()).getModel().getValueAt(
							((ListHeadRowChangedEvent) e).getRow(),
							((ListHeadRowChangedEvent) e).getBillListPanel().getHeadBillModel()
									.getBodyColByKey("qenddate"));
					// ��ͷ���
					if (leaveTypeVo != null && leaveTypeVo.getPk_timeitemcopy().equals(pk_otleavetype)) {
						// �Ӱ�ת����
						detailVos = getSegDetailService().getOvertimeToRestByType(getContext().getPk_org(), pk_psndoc,
								beginDate, endDate, null);

					} else if (leaveTypeVo != null && leaveTypeVo.getPk_timeitemcopy().equals(pk_exleavetype)) {
						// ��Ӳ���
						detailVos = getLeaveExtraRestService().getLeaveExtByType(getContext().getPk_org(), pk_psndoc,
								null, beginDate, endDate, pk_exleavetype, false);
					}
					this.getOtListView().getBillListPanel().setBodyValueVO(detailVos);
				} else if (e instanceof ListBodyRowChangedEvent) {
					// ������
					String pk_sourceBill = (String) ((BillTable) e.getSource()).getModel().getValueAt(
							((ListBodyRowChangedEvent) e).getRow(), 0);
					int sourceType = (int) ((DefaultConstEnum) ((BillTable) e.getSource()).getModel().getValueAt(
							((ListBodyRowChangedEvent) e).getRow(), 1)).getValue();
					if (!StringUtils.isEmpty(pk_sourceBill)) {
						UFLiteralDate beginDate = (UFLiteralDate) getOrgpanel().getRefBeginDate().getValueObj();
						UFLiteralDate endDate = (UFLiteralDate) getOrgpanel().getRefEndDate().getValueObj();
						String queryYear = (String) getOrgpanel().getCboYear().getSelectdItemValue();
						OTBalanceLeaveVO[] data = getSegDetailService().getLeaveRegBySourceBill(
								sourceType,
								pk_sourceBill,
								queryYear,
								beginDate == null ? null : new UFLiteralDate(((UFDate) getOrgpanel().getRefBeginDate()
										.getValueObj()).toDate()),
								endDate == null ? null : new UFLiteralDate(((UFDate) getOrgpanel().getRefEndDate()
										.getValueObj()).toDate()));
						this.getLeaveListView().getBillListPanel().setBodyValueVO(data);
					}
				}
			}
		} catch (BusinessException ex) {
			ExceptionUtils.wrappBusinessException(ex.getMessage());
		}
	}

	private ISegDetailService otService = null;

	private ISegDetailService getSegDetailService() {
		if (otService == null) {
			otService = NCLocator.getInstance().lookup(ISegDetailService.class);
		}
		return otService;
	}

	private ILeaveExtraRestService erService = null;

	public ILeaveExtraRestService getLeaveExtraRestService() {
		if (erService == null) {
			erService = NCLocator.getInstance().lookup(ILeaveExtraRestService.class);
		}
		return erService;
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

	public BillListView getLeaveListView() {
		return leaveListView;
	}

	public void setLeaveListView(BillListView leaveListView) {
		this.leaveListView = leaveListView;
	}

}