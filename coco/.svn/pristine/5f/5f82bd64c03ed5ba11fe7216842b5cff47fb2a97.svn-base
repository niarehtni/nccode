package nc.ui.ta.overtime.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class SegruleRefModel extends AbstractRefModel {
    private Integer dateType;

    public SegruleRefModel() {
	super();
	init();
    }

    private void init() {

	setRefNodeName("加班分段依据");
	setRefTitle("加班分段依据");
	setFieldCode(new String[] { "code", "name", "datetype", "additionaldays", "isdefault" });
	setFieldName(new String[] { "编码", "名称", "日历天类型", "外加补休基准天数", "是否默认" });
	setHiddenFieldCode(new String[] { "pk_segrule", "pk_segrule", "pk_group", "pk_org", "pk_org_v", "creator",
		"creationtime", "modifier", "modifiedtime", "isenabled", "memo", "makedate" });
	setPkFieldCode("pk_segrule");
	setTableName("hrta_segrule");
	setRefCodeField("code");
	setRefNameField("name");

    }

    public String getWherePart() {
	return "1=1 and isenabled='Y' and isnull(dr,0)=0 "
		+ (getDateType() == null ? "" : ("and datetype=" + String.valueOf(getDateType())));
    }

    public Integer getDateType() {
	return dateType;
    }

    public void setDateType(Integer dateType) {
	this.dateType = dateType;
    }

}