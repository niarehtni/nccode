package nc.ui.hrpub.ioschema.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class IoschemaRefModel extends AbstractRefModel {

	public IoschemaRefModel() {
		super();
		init();
	}

	private void init() {

		setRefNodeName("导入导出方案");
		setRefTitle("导入导出方案");
		setFieldCode(new String[] { "code", "name", "enableimport",
				"enableexport" });
		setFieldName(new String[] { "方案编码", "方案名称", "资料导入", "资料导出" });
		setHiddenFieldCode(new String[] { "creator", "creationtime",
				"modifier", "modifiedtime", "def1", "def2", "def3", "def4",
				"def5", "def6", "def7", "def8", "def9", "def10", "def11",
				"def12", "def13", "def14", "def15", "def16", "def17", "def18",
				"def19", "def20", "pk_group", "pk_org", "pk_org_v", "id",
				"maketime", "lastmaketime", "isenabled" });
		setPkFieldCode("id");
		setWherePart("isenabled='Y' and isnull(dr,0)=0");
		setTableName("hrpub_ioschema");
		setRefCodeField("code");
		setRefNameField("name");
		setMutilLangNameRef(false);
	}

}