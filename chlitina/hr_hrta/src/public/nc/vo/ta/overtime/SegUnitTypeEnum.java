package nc.vo.ta.overtime;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * <b>��̎��Ҫ������ö�e�Ĺ��� </b>
 * <p>
 *   ��̎���ԓö�e��������Ϣ
 * </p>
 *  ��������:2018/4/23
 * @author
 * @version NC V65 TW Localization 3.2.1
 */

public class SegUnitTypeEnum extends MDEnum {
	public SegUnitTypeEnum(IEnumValue enumvalue) {
		super(enumvalue);
	}

	public static final SegUnitTypeEnum HOUR = MDEnum.valueOf(SegUnitTypeEnum.class, Integer.valueOf(1));

	public static final SegUnitTypeEnum DAY = MDEnum.valueOf(SegUnitTypeEnum.class, Integer.valueOf(2));

}
