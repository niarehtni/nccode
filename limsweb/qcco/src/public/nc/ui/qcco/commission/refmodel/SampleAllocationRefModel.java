package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class SampleAllocationRefModel extends AbstractRefModel{
	private String pk_commission_h;

	public String getPk_commission_h() {
		return pk_commission_h;
	}

	public void setPk_commission_h(String pk_commission_h) {
		this.pk_commission_h = pk_commission_h;
	}

	public SampleAllocationRefModel(String pk_commission_h) {
		super();
		this.pk_commission_h = pk_commission_h;
	}

	public SampleAllocationRefModel() {
		super();
		// TODO Auto-generated constructor stub
		this.setTableName("NC_SAMPLE_GROUP");
		this.setMutilLangNameRef(false);
	}
	
	public java.lang.String[] getFieldCode() {
		return new String[] { "name",};
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "样品分配名称" };
	}


	

	public int getDefaultFieldCount() {
		return 1;
	}

	public java.lang.String getRefTitle() {
		return "请选样品分配";
	}

	protected String getSql(String strPatch, String[] strFieldCode,
			String[] hiddenFields, String strTableName, String strWherePart,
			String strGroupField, String strOrderField) {
		return "select NC_SAMPLE_CODE,NC_SAMPLE_NAME, PK_SAMPLE_GROUP from NC_SAMPLE_GROUP WHERE ISENABLE=1 ORDER BY NC_SAMPLE_CODE";
	}
}
