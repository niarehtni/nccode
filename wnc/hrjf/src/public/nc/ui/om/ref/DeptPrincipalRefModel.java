package nc.ui.om.ref;

import nc.ui.bd.ref.AbstractRefModel;

public class DeptPrincipalRefModel extends AbstractRefModel {

	/** HR组织在行政体系中的内部码 */
	private String pk_dept = null;


	/**
	 * 默认构造函数，使用主组织作为查询条件<br>
	 */
	public DeptPrincipalRefModel() {
		initModel();
	}


	private void initModel() {
		//参照的名称
		setRefNodeName("主管人員");
		setRefTitle("部門主管人員");
		setDefaultFieldCount(2);
		//查询的字段
		setFieldCode(new String[] { "bd_psndoc.code", "bd_psndoc.name"});
		setFieldName(new String[] { "編號", "姓名" });
		setHiddenFieldCode(new String[]{"bd_psndoc.pk_psndoc"});
		//人员表
		setPkFieldCode("bd_psndoc.pk_psndoc");
		setMutilLangNameRef(false);
		setTableName(" bd_psndoc ");
		//where条件
		StringBuffer wheresql = new StringBuffer();
		wheresql.append("(bd_psndoc.pk_psndoc = (select org_dept.principal from org_dept where org_dept.pk_dept = '")
		.append(this.getPk_dept()).append("')) ")
		.append("or (bd_psndoc.pk_psndoc = (select org_dept.glbdef3 from org_dept where org_dept.pk_dept = '")
		.append(this.getPk_dept()).append("')) ");
		setWherePart(wheresql.toString());
		//code字段和name字段
		setRefCodeField("bd_psndoc.code");
		setRefNameField("bd_psndoc.name");
		setOrderPart("bd_psndoc.code");
	}


	public String getPk_dept() {
		return pk_dept;
	}


	public void setPk_dept(String pk_dept) {
		this.pk_dept = pk_dept;
	}


	@Override
	public void reset() {
		initModel();
	}



}
