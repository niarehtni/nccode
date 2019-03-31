package nc.vo.wa.itemgroup.ref;

import nc.ui.bd.ref.AbstractRefModel;


public class ItemGroupRefModel extends AbstractRefModel {

	public ItemGroupRefModel() {
		reset();
	}

	public void reset() {
		setRefNodeName("薪资项目分组");

		setFieldCode(new String[] { "groupcode", "groupname" });
		setFieldName(new String[] {
				"編碼",
				"名稱" });

		setHiddenFieldCode(new String[] { "pk_org", "pk_itemgroup" });
		setPkFieldCode("pk_itemgroup");
		setRefCodeField("groupcode");
		setRefNameField("groupname");
		setTableName("wa_itemgroup");

		setOrderPart("groupcode");

		setAddEnableStateWherePart(true);

		setResourceID("itemgroup");

		resetFieldName();
	}

	protected String getEnvWherePart() {
		//return " pk_org = '"+getPk_org()+"' and dr = 0";
		
		return " dr = 0";
	}
	
	@Override
	protected String getDisableDataWherePart(boolean isDisableDataShow) {
		return " 11 = 11 ";
	}

}
