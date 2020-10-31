package nc.ui.twhr.twhr.view;

import nc.ui.bd.ref.AbstractRefModel;

public class PeriodRefModel extends AbstractRefModel {

	public PeriodRefModel() {
		super();
		this.setTableName("(select DISTINCT cyear,cperiod,(cyear +'-'+cperiod) yearmonth from wa_period)");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "cyear", "cperiod","yearmonth" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "年", "月","期間編碼" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] {};
	}

	public java.lang.String getPkFieldCode() {
		return "yearmonth";
	}

	public java.lang.String getOrderPart() {
		return "yearmonth";
	}

	public int getDefaultFieldCount() {
		return 3;
	}

	public java.lang.String getRefTitle() {
		return "請選擇期間";
	}

	protected String getSql(String strPatch, String[] strFieldCode, String[] hiddenFields, String strTableName,
			String strWherePart, String strGroupField, String strOrderField) {
		return " select cyear,cperiod,yearmonth from "
				+ " (select DISTINCT cyear,cperiod,(cyear +'-'+cperiod) yearmonth from wa_period) t  order by yearmonth desc";
	}

}
