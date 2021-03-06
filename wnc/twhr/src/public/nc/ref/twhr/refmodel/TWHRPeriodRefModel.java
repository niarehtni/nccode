package nc.ref.twhr.refmodel;

import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.ml.NCLangRes4VoTransl;

import org.apache.commons.lang.StringUtils;

public class TWHRPeriodRefModel extends AbstractRefModel {
	private boolean showHistoryPeriod = false;
	private String[] pk_wa_classes = null;

	public boolean isShowHistoryPeriod() {
		return showHistoryPeriod;
	}

	public void setShowHistoryPeriod(boolean showHistoryPeriod) {
		this.showHistoryPeriod = showHistoryPeriod;
	}

	public TWHRPeriodRefModel() {
		super();

		this.setTableName("twhr.TWHRPeriodRefModel"); // 用於緩存Key
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "cperiod", "cyearperiod", "cstartdate", "cenddate", "name" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] {
				NCLangRes4VoTransl.getNCLangRes().getStrByID("68861705", "TWHRPeriodRefModel-0000")/* 期间号 */,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("68861705", "TWHRPeriodRefModel-0001")/* 薪资期间 */,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("68861705", "TWHRPeriodRefModel-0002")/* 起始日期 */,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("68861705", "TWHRPeriodRefModel-0003") /* 截止日期 */, "薪資方案" };
	}

	public int getDefaultFieldCount() {
		return 5;
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "pk_wa_period" };
	}

	public java.lang.String getPkFieldCode() {
		return "pk_wa_period";
	}

	public java.lang.String getRefTitle() {
		return NCLangRes4VoTransl.getNCLangRes().getStrByID("68861705", "TWHRPeriodRefModel-0001")/* 薪资期间 */;
	}

	protected String getSql(String strPatch, String[] strFieldCode, String[] hiddenFields, String strTableName,
			String strWherePart, String strGroupField, String strOrderField) {

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT cperiod, cyearperiod, cstartdate, cenddate, name, pk_wa_period from (");
		sql.append("select ");
		sql.append(" wa_waclass.cperiod cperiod,  wa_waclass.cyear||'-'|| wa_waclass.cperiod cyearperiod, cstartdate, cenddate, wa_waclass.name name,  wa_waclass.name2 name2,  wa_waclass.name3 name3, wa_period.pk_wa_period, wa_period.pk_periodscheme ");
		sql.append(" from ");
		sql.append(" wa_period inner join wa_periodscheme on wa_period.pk_periodscheme=wa_periodscheme.pk_periodscheme inner join wa_waclass on wa_period.cyear=wa_waclass.cyear and wa_period.cperiod=wa_waclass.cperiod and wa_waclass.dr=0");
		sql.append(" where ");
		sql.append(" wa_period.pk_periodscheme in (select pk_periodscheme from wa_periodscheme where pk_org = '"
				+ this.getPk_group() + "' or pk_org = '" + this.getPk_org() + "') ");

		// 只顯示薪資方案相關的期間
		String onlySelectWaClass = "";
		if (this.getPk_wa_classes() != null && this.getPk_wa_classes().length > 0) {
			for (String pk_wa_class : pk_wa_classes) {
				if (StringUtils.isEmpty(onlySelectWaClass)) {
					onlySelectWaClass = "'" + pk_wa_class + "'";
				} else {
					onlySelectWaClass += ",'" + pk_wa_class + "'";
				}
			}
			onlySelectWaClass = "and pk_wa_class in (" + onlySelectWaClass + ")";
		}

		// 是否只顯示歷史期間
		if (this.isShowHistoryPeriod()) {
			sql.append(" and pk_wa_period in (SELECT DISTINCT pk_wa_period FROM wa_periodstate WHERE payoffflag = 'Y' AND enableflag = 'Y' "
					+ onlySelectWaClass + ") ");
		} else {
			sql.append(" and pk_wa_period in (SELECT DISTINCT pk_wa_period FROM wa_periodstate WHERE payoffflag = 'N' AND enableflag = 'Y' "
					+ onlySelectWaClass + ") ");
		}

		sql.append(") order by cyearperiod");
		return sql.toString();
	}

	public String[] getPk_wa_classes() {
		return pk_wa_classes;
	}

	public void setPk_wa_classes(String[] pk_wa_classes) {
		this.pk_wa_classes = pk_wa_classes;
	}
}