package nc.ui.hrpub.mdclass.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

import org.apache.commons.lang.StringUtils;

public class MDClassRefModel extends AbstractRefModel {
	private String pk_ioschema;

	public MDClassRefModel() {
		super();
		init();
	}

	private void init() {

		setRefNodeName("语义元数据");
		setRefTitle("语义元数据");
		setFieldCode(new String[] { "md_class.name code",
				"md_class.displayname name" });
		setFieldName(new String[] { "实体编码", "实体名称" });
		setHiddenFieldCode(new String[] { "pk_mdclass", "isenabled" });
		setPkFieldCode("pk_mdclass");
		setWherePart("isenabled='Y' and isnull(dr,0)=0");
		setTableName("hrpub_mdclass inner join md_class on hrpub_mdclass.pk_class=md_class.id");
		setRefCodeField("code");
		setRefNameField("name");
		setOrderPart("md_class.name");
		setMutilLangNameRef(false);
	}

	public String getWherePart() {
		StringBuffer strWhere = new StringBuffer();

		if (!StringUtils.isEmpty(this.getPk_ioschema())) {
			strWhere.append("pk_ioschema='" + this.getPk_ioschema() + "'");
		}

		return strWhere.toString();
	}

	public String getPk_ioschema() {
		return pk_ioschema;
	}

	public void setPk_ioschema(String pk_ioschema) {
		this.pk_ioschema = pk_ioschema;
	}
}
