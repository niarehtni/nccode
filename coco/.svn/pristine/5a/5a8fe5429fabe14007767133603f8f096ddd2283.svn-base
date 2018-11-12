package nc.bs.hr.hrsms.hi.ref.model;

import nc.ui.bd.ref.IRefMaintenanceHandler;
import nc.ui.bd.ref.model.DefdocDefaultModelUtil;
import nc.ui.bd.ref.model.DefdocGridRefModel;
import nc.vo.ml.NCLangRes4VoTransl;

public class DefdocGridRefNoMemoModel extends DefdocGridRefModel {

	private DefdocDefaultModelUtil util = new DefdocDefaultModelUtil();

	public void reset()
	/*     */{
		/* 30 */setFieldCode(new String[] { "code", "name" });
		/*     */
		/*     */
		/* 33 */setFieldName(new String[] { NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0003279"),
				NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001155"),
				NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001376"),
				NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0000703") });
		/*     */
		/*     */
		/*     */
		/*     */
		/*     */
		/*     */
		/*     */
		/*     */
		/*     */
		/*     */
		/*     */
		/* 45 */setHiddenFieldCode(new String[] { "pk_defdoc" });
		/*     */
		/*     */
		/* 48 */setRefCodeField("code");
		/* 49 */setRefNameField("name");
		/* 50 */setPkFieldCode("pk_defdoc");
		/* 51 */setTableName(getPara2());
		/*     */
		/*     */
		/* 55 */setOrderPart("code");
		/*     */
		/* 57 */setResourceID(getResourceCode());
		/* 58 */final String[] funcodes = this.util.getFuncode(getPara1());
		/*     */
		/* 60 */setAddEnableStateWherePart(true);
		/* 61 */if (this.util.isContainBuData(getPara1())) {
			/* 62 */setFilterRefNodeName(new String[] { "业务单元" });
			/*     */}
		/*     */
		/*     */
		/*     */
		/* 67 */setRefMaintenanceHandler(new IRefMaintenanceHandler()
		/*     */{
			/*     */public String[] getFucCodes()
			/*     */{
				/* 71 */return funcodes;
				/*     */}

			/*     */
			/*     */public nc.ui.bd.ref.IRefDocEdit getRefDocEdit()
			/*     */{
				/* 76 */return null;
				/*     */}
			/* 78 */
		});
		/* 79 */resetFieldName();
		/*     */}

	private String getResourceCode()
	/*     */{
		/* 89 */return this.util.getResourceCode(getRefNodeName());
		/*     */}
}
