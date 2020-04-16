package nc.vo.hrwa.wadaysalary;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class MinimumLifeWays extends MDEnum {

	public MinimumLifeWays(IEnumValue enumValue) {
		super(enumValue);
	}

	/**
	 * ʹ�Ñ��l��Ӌ������������M
	 */
	public static final MinimumLifeWays TOTAL_PAY_OF_SHOULD = MDEnum.valueOf(MinimumLifeWays.class, Integer.valueOf(1));
	/**
	 * ʹ�Ì��l��ӋӋ�㱣����������M
	 */
	public static final MinimumLifeWays TOTAL_PAY_OF_REAL = MDEnum.valueOf(MinimumLifeWays.class, Integer.valueOf(2));
	/**
	 * ����֮һ���l��Ӌ���^Ӌ��
	 */
	public static final MinimumLifeWays ONE_THIRD_OF_REAL = MDEnum.valueOf(MinimumLifeWays.class, Integer.valueOf(3));

}
