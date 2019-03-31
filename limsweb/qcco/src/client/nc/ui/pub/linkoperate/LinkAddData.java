package nc.ui.pub.linkoperate;

public class LinkAddData implements ILinkAddData {
	private String billID;

	private String billType;

	private String pk_org;

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public void setBillID(String billID) {
		this.billID = billID;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	@Override
	public String getSourceBillID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSourceBillType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSourcePkOrg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getUserObject() {
		// TODO Auto-generated method stub
		return null;
	}

}
