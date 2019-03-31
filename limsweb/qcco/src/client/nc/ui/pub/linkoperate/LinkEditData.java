package nc.ui.pub.linkoperate;

public class LinkEditData implements ILinkMaintainData {

	private String billID;

	public void setBillID(String billID) {
		this.billID = billID;
	}

	@Override
	public String getBillID() {
		// TODO Auto-generated method stub
		return billID;
	}

	@Override
	public Object getUserObject() {
		// TODO Auto-generated method stub
		return null;
	}

}
