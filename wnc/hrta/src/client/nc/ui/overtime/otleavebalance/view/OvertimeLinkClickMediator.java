package nc.ui.overtime.otleavebalance.view;

import nc.pubitf.para.SysInitQuery;
import nc.ui.pubapp.uif2app.event.list.ListBillItemHyperlinkEvent;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.uap.sf.SFClientUtil;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.pub.BusinessException;
import nc.vo.sf.pub.linkquery.LinkQueryData;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import uap.iweb.log.Logger;

public class OvertimeLinkClickMediator {
	private String hyperLinkColumn;
	private BillListView listView;
	private OTLeaveBalanceOrgPanel orgPanel;
	private BillManageModel otListModel;
	private AbstractAppModel hierachicalModel;
	private String pk_otleavetype = null;
	private String pk_exleavetype = null;

	class ListHyperLinkListener implements nc.ui.pubapp.uif2app.event.IAppEventHandler<ListBillItemHyperlinkEvent> {

		ListHyperLinkListener() {

		}

		public void handleAppEvent(ListBillItemHyperlinkEvent e) {
			if ((e.getItem().getKey().equals(getHyperLinkColumn()))) {
				String pk_org = orgPanel.getRefPane().getRefPK();
				try {
					setPk_otleavetype(SysInitQuery.getParaString(pk_org, "TWHRT08"));
					setPk_exleavetype(SysInitQuery.getParaString(pk_org, "TWHRT10"));
				} catch (BusinessException ex) {
					Logger.error(ex.getMessage());
				}

				LeaveTypeCopyVO leaveTypeVo = (LeaveTypeCopyVO) getHierachicalModel().getSelectedData();
				if (leaveTypeVo != null) {
					String funCode = "";
					if (leaveTypeVo != null && leaveTypeVo.getPk_timeitemcopy().equals(getPk_otleavetype())) {
						funCode = "60170otrgst";// ¼Ó°àµÇ¼Ç
					} else {
						funCode = "6017leaveextrarest"; // ÑaÐÝ¼ÙÇÚÃ÷¼š
					}

					LinkQueryData data = new LinkQueryData();
					String billID = (String) getListView().getBillListPanel().getBodyBillModel()
							.getValueAt(e.getRow(), "pk_sourcebill");
					String pkOrg = pk_org;
					data.setBillID(billID);
					data.setBillType(null);
					data.setPkOrg(pkOrg);

					SFClientUtil.openLinkedQueryDialog(funCode, getListView().getBillListPanel(), data);
				}
			}
		}
	}

	public String getHyperLinkColumn() {
		return hyperLinkColumn;
	}

	public void setHyperLinkColumn(String hyperLinkColumn) {
		this.hyperLinkColumn = hyperLinkColumn;
	}

	public BillListView getListView() {
		return listView;
	}

	public void setListView(BillListView listView) {
		this.listView = listView;
	}

	public OTLeaveBalanceOrgPanel getOrgPanel() {
		return orgPanel;
	}

	public void setOrgPanel(OTLeaveBalanceOrgPanel orgPanel) {
		this.orgPanel = orgPanel;
	}

	public BillManageModel getOtListModel() {
		return otListModel;
	}

	public void setOtListModel(BillManageModel otListModel) {
		this.otListModel = otListModel;
		this.otListModel.addAppEventListener(ListBillItemHyperlinkEvent.class, new ListHyperLinkListener());
	}

	public AbstractAppModel getHierachicalModel() {
		return hierachicalModel;
	}

	public void setHierachicalModel(AbstractAppModel hierachicalModel) {
		this.hierachicalModel = hierachicalModel;
	}

	public String getPk_otleavetype() {
		return pk_otleavetype;
	}

	public void setPk_otleavetype(String pk_otleavetype) {
		this.pk_otleavetype = pk_otleavetype;
	}

	public String getPk_exleavetype() {
		return pk_exleavetype;
	}

	public void setPk_exleavetype(String pk_exleavetype) {
		this.pk_exleavetype = pk_exleavetype;
	}
}
