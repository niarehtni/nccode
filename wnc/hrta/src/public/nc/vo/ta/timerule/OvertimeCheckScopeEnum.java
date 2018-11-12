package nc.vo.ta.timerule;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class OvertimeCheckScopeEnum extends MDEnum {

	public OvertimeCheckScopeEnum(IEnumValue enumValue) {
		super(enumValue);
	}

	public static final OvertimeCheckScopeEnum ONEMONTH = (OvertimeCheckScopeEnum) MDEnum
			.valueOf(OvertimeCheckScopeEnum.class, Integer.valueOf(1));
	public static final OvertimeCheckScopeEnum THREEMONTH = (OvertimeCheckScopeEnum) MDEnum
			.valueOf(OvertimeCheckScopeEnum.class, Integer.valueOf(2));

}
