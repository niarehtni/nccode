package nc.ui.om.ref;

import nc.ui.bd.ref.AbstractRefModel;

public class DeptPrincipalRefModel extends AbstractRefModel {

	/** HR��֯��������ϵ�е��ڲ��� */
	private String pk_dept = null;


	/**
	 * Ĭ�Ϲ��캯����ʹ������֯��Ϊ��ѯ����<br>
	 */
	public DeptPrincipalRefModel() {
		initModel();
	}


	private void initModel() {
		//���յ�����
		setRefNodeName("�����ˆT");
		setRefTitle("���T�����ˆT");
		setDefaultFieldCount(2);
		//��ѯ���ֶ�
		setFieldCode(new String[] { "bd_psndoc.code", "bd_psndoc.name"});
		setFieldName(new String[] { "��̖", "����" });
		setHiddenFieldCode(new String[]{"bd_psndoc.pk_psndoc"});
		//��Ա��
		setPkFieldCode("bd_psndoc.pk_psndoc");
		setMutilLangNameRef(false);
		setTableName(" bd_psndoc ");
		//where����
		StringBuffer wheresql = new StringBuffer();
		wheresql.append("(bd_psndoc.pk_psndoc = (select org_dept.principal from org_dept where org_dept.pk_dept = '")
		.append(this.getPk_dept()).append("')) ")
		.append("or (bd_psndoc.pk_psndoc = (select org_dept.glbdef3 from org_dept where org_dept.pk_dept = '")
		.append(this.getPk_dept()).append("')) ");
		setWherePart(wheresql.toString());
		//code�ֶκ�name�ֶ�
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
