package nc.ui.overtime.otleavebalance.view;

import nc.ui.pubapp.uif2app.event.list.ListBillItemHyperlinkEvent;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.uap.sf.SFClientUtil;
import nc.vo.sf.pub.linkquery.LinkQueryData;

public class LeaveLinkClickMediator {
	private String hyperLinkColumn;
	private BillListView listView;
	private OTLeaveBalanceOrgPanel orgPanel;
	private BillManageModel lvListModel;

	class ListHyperLinkListener implements nc.ui.pubapp.uif2app.event.IAppEventHandler<ListBillItemHyperlinkEvent> {

		ListHyperLinkListener() {

		}

		public void handleAppEvent(ListBillItemHyperlinkEvent e) {
			if ((e.getItem().getKey().equals(getHyperLinkColumn()))) {
				String pk_org = orgPanel.getRefPane().getRefPK();

				LinkQueryData data = new LinkQueryData();
				String billID = (String) getListView().getBillListPanel().getBodyBillModel()
						.getValueAt(e.getRow(), "pk_leavereg");
				String pkOrg = pk_org;
				data.setBillID(billID);
				data.setBillType(null);
				data.setPkOrg(pkOrg);

				SFClientUtil.openLinkedQueryDialog("60170leavergst", getListView().getBillListPanel(), data);
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

	public BillManageModel getLvListModel() {
		return lvListModel;
	}

	public void setLvListModel(BillManageModel lvListModel) {
		this.lvListModel = lvListModel;
		this.lvListModel.addAppEventListener(ListBillItemHyperlinkEvent.class, new ListHyperLinkListener());
	}

}
