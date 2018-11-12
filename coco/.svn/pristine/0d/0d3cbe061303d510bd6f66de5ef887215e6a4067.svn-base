package nc.ui.twhr.allowance.refmodel;

import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.ml.NCLangRes4VoTransl;

public class AllowanceRefModel extends AbstractRefModel {

	public AllowanceRefModel() {
		super();
		init();
	}

	private void init() {

		setRefNodeName(NCLangRes4VoTransl.getNCLangRes().getStrByID("68860545", "AllowanceRefModel-0000")/*补助类项目*/);
		setRefTitle(NCLangRes4VoTransl.getNCLangRes().getStrByID("68860545", "AllowanceRefModel-0000")/*补助类项目*/);
		setFieldCode(new String[] { "code", "name", "allowancetype",
				"allowanceamount" });
		setFieldName(new String[] { "编码", NCLangRes4VoTransl.getNCLangRes().getStrByID("68860545", "AllowanceRefModel-0001")/*名称*/, NCLangRes4VoTransl.getNCLangRes().getStrByID("68860545", "AllowanceRefModel-0002")/*补助方式*/, NCLangRes4VoTransl.getNCLangRes().getStrByID("68860545", "AllowanceRefModel-0003")/*补助数量*/ });
		setHiddenFieldCode(new String[] { "def1", "def2", "def3", "def4",
				"def5", "def6", "def7", "def8", "def9", "def10", "def11",
				"def12", "def13", "def14", "def15", "def16", "def17", "def18",
				"def19", "def20", "id", "startdate", "enddate", "creationtime",
				"modifiedtime" });
		setPkFieldCode("id");
		setWherePart("1=1 and isnull(dr,0)=0");
		setTableName("twhr_allowance");
		setRefCodeField("code");
		setRefNameField("name");

	}

}