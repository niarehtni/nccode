package nc.vo.ta.overtime;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * <b> </b>
 * <p>
 * 
 * </p>
 * Create Date:2018/9/6
 * 
 * @author
 * @version NC V65 TW Localization 3.2.1
 */
public class RoundTypeEnum extends MDEnum {
	public RoundTypeEnum(IEnumValue enumvalue) {
		super(enumvalue);
	}

	public static final RoundTypeEnum ROUNDING = MDEnum.valueOf(RoundTypeEnum.class, Integer.valueOf(1));

	public static final RoundTypeEnum CARRY = MDEnum.valueOf(RoundTypeEnum.class, Integer.valueOf(2));

	public static final RoundTypeEnum TRUNCATE = MDEnum.valueOf(RoundTypeEnum.class, Integer.valueOf(3));

}
