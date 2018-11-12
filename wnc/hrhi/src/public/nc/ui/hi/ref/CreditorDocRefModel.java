package nc.ui.hi.ref;

import nc.ui.bd.ref.AbstractRefModel;

import org.apache.commons.lang.StringUtils;

public class CreditorDocRefModel extends AbstractRefModel {
	private String pk_psndoc;

	public CreditorDocRefModel() {
		super();
		init();
	}

	private void init() {
		setRefNodeName("债权档案");
		setRefTitle("债权档案");
		setDefaultFieldCount(4);
		setFieldCode(new String[] { "code", "name", "docno", "totalamount" });
		setFieldName(new String[] { "檔案編號", "債權方", "法院命令文號", "債權金額" });
		setHiddenFieldCode(new String[] { "pk_psndoc_sub" });
		setPkFieldCode("pk_psndoc_sub");
		setMutilLangNameRef(false);
		setTableName("hi_psndoc_creditorrecord");
		setWherePart("bstopped='N' and isnull(dr, 0)=0");
		setRefCodeField("code");
		setRefNameField("name");
		setOrderPart("");
	}

	public String getWherePart() {
		String strWhere = super.getWherePart();
		if (StringUtils.isEmpty(getPk_psndoc())) {
			strWhere += " and 1=2";
		} else {
			strWhere += " and pk_psndoc='" + getPk_psndoc() + "'";
		}
		return strWhere;
	}

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}
}
