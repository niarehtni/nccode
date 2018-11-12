package nc.vo.ta.overtime;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * <b>此處簡要描述此枚舉的功能 </b>
 * <p>
 * 此處添加該枚舉的描述信息
 * </p>
 * 創建日期:2018/9/7
 * 
 * @author
 * @version NCPrj ??
 */
public class CalendarDateTypeEnum extends MDEnum {
	public CalendarDateTypeEnum(IEnumValue enumvalue) {
		super(enumvalue);
	}

	public static final CalendarDateTypeEnum NORMAL = MDEnum.valueOf(CalendarDateTypeEnum.class, Integer.valueOf(1));

	public static final CalendarDateTypeEnum OFFDAY = MDEnum.valueOf(CalendarDateTypeEnum.class, Integer.valueOf(2));

	public static final CalendarDateTypeEnum HOLIDAY = MDEnum.valueOf(CalendarDateTypeEnum.class, Integer.valueOf(3));

	public static final CalendarDateTypeEnum NATIONALDAY = MDEnum.valueOf(CalendarDateTypeEnum.class,
			Integer.valueOf(4));

}
