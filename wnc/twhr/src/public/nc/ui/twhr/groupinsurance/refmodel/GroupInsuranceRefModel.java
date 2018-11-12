package nc.ui.twhr.groupinsurance.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class GroupInsuranceRefModel extends AbstractRefModel {
	public GroupInsuranceRefModel() {
		super();
		init();
	}

	private void init() {
		String grpinsField = "(SELECT name FROM bd_defdoc WHERE pk_defdoc=twhr_groupinsurancesetting.cgrpinsid) grpins";
		String grprelField = "(SELECT name FROM bd_defdoc WHERE pk_defdoc=twhr_groupinsurancesetting.cgrpinsrelid) grprel";
		String caclmodeField = "(SELECT name FROM md_enumvalue WHERE value = twhr_groupinsurancesetting.icalmode and id = '09180c4d-fd76-483e-8477-07ae3258af40') caclmode";
		setRefNodeName("團保投保設定");
		setRefTitle("團保投保設定");
		setDefaultFieldCount(6);
		setFieldCode(new String[] { "code", "name", grpinsField, grprelField,
				caclmodeField, "bselfpay" });
		setFieldName(new String[] { "編碼", "名稱", "團保險種", "投保人員", "計算方式", "自費" });
		setHiddenFieldCode(new String[] { "id" });
		setPkFieldCode("id");
		setMutilLangNameRef(false);
		setTableName("twhr_groupinsurancesetting");
		setWherePart("isnull(dr, 0)=0");
		setRefCodeField("code");
		setRefNameField("name");
		setOrderPart("");
	}
}
