package nc.vo.twhr.allowance;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class AllowanceTypeEnum extends MDEnum {

	public AllowanceTypeEnum(IEnumValue enumValue) {
		super(enumValue);
	}

	/**
	 * ½ð¶î
	 */
	public static final AllowanceTypeEnum AMOUNT = MDEnum.valueOf(
			AllowanceTypeEnum.class, Integer.valueOf(1));

	/**
	 * ±ÈÀý
	 */
	public static final AllowanceTypeEnum RATE = MDEnum.valueOf(
			AllowanceTypeEnum.class, Integer.valueOf(2));
}
