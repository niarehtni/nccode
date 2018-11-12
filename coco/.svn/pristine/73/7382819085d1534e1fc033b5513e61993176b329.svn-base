package nc.bs.hr.hrsms.hi.ref.model;

import nc.ui.bd.ref.IRefDocEdit;
import nc.ui.bd.ref.IRefMaintenanceHandler;
import nc.ui.bd.ref.RefPubUtil;
import nc.ui.bd.ref.model.DefdocDefaultModelUtil;
import nc.ui.bd.ref.model.DefdocDefaultRefModel;
import nc.vo.bd.ref.RefInfoVO;
import nc.vo.ml.NCLangRes4VoTransl;

public class DefdocDefaultRefNoMemoModel extends DefdocDefaultRefModel {

	private DefdocDefaultModelUtil util = new DefdocDefaultModelUtil();

	public void reset() {
		setFieldCode(new String[] { "code", "name"});

		setFieldName(new String[] { NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0003279"),
				NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001155"),
				NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001376"),
				NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0000703") });

		setHiddenFieldCode(new String[] { "pk_defdoc" });

		setRefCodeField("code");
		setRefNameField("name");

		setTableName("bd_defdoc");
		setPkFieldCode("pk_defdoc");

		setResourceID(getResourceCode());
		final String[] funcodes = this.util.getFuncode(getPara1());

		setAddEnableStateWherePart(true);

		if (this.util.isContainBuData(getPara1())) {
			setFilterRefNodeName(new String[] { "业务单元" });
		}

		setRefMaintenanceHandler(new IRefMaintenanceHandler() {
			public String[] getFucCodes() {
				return funcodes;
			}

			public IRefDocEdit getRefDocEdit() {
				return null;
			}

		});
		resetFieldName();
	}

	private String getResourceCode() {
		RefInfoVO refinfo = RefPubUtil.getRefinfoVO(getRefNodeName());
		return (refinfo == null) || (refinfo.getReserv3() == null) ? "defdoc" : refinfo.getReserv3();
	}
}
