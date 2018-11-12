package nc.vo.om.hrdept;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class DeptChangeType extends MDEnum {
	public static final String ESTABLISH = "1";
	public static final String HRCANCELED = "2";
	public static final String HRUNCANCELED = "3";
	public static final String RENAME = "4";
	public static final String MERGE = "5";
	public static final String SHIFT = "6";
	public static final String SPLIT = "7";
	public static final String OUTERSHIFT = "8";
	public static final String CHANGEPRINCIPAL = "9";

	public DeptChangeType(IEnumValue enumvalue) {
		super(enumvalue);
	}
}
