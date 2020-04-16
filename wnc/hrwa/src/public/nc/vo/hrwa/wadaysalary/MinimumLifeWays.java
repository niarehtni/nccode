package nc.vo.hrwa.wadaysalary;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class MinimumLifeWays extends MDEnum {

	public MinimumLifeWays(IEnumValue enumValue) {
		super(enumValue);
	}

	/**
	 * 使用應發合計保留最低生活費
	 */
	public static final MinimumLifeWays TOTAL_PAY_OF_SHOULD = MDEnum.valueOf(MinimumLifeWays.class, Integer.valueOf(1));
	/**
	 * 使用實發合計計算保留最低生活費
	 */
	public static final MinimumLifeWays TOTAL_PAY_OF_REAL = MDEnum.valueOf(MinimumLifeWays.class, Integer.valueOf(2));
	/**
	 * 三分之一應發合計比較計算
	 */
	public static final MinimumLifeWays ONE_THIRD_OF_REAL = MDEnum.valueOf(MinimumLifeWays.class, Integer.valueOf(3));

}
