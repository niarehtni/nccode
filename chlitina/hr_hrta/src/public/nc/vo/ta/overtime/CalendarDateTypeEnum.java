package nc.vo.ta.overtime;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * <b>��̎��Ҫ������ö�e�Ĺ��� </b>
 * <p>
 * ��̎���ԓö�e��������Ϣ
 * </p>
 * ��������:2018/9/7
 * 
 * @author
 * @version NCPrj ??
 */
public class CalendarDateTypeEnum extends MDEnum {
	public CalendarDateTypeEnum(IEnumValue enumvalue) {
		super(enumvalue);
	}

	/**
	 * ƽ��
	 */
	public static final CalendarDateTypeEnum NORMAL = MDEnum.valueOf(CalendarDateTypeEnum.class, Integer.valueOf(1));

	/**
	 * ��Ϣ��
	 */
	public static final CalendarDateTypeEnum OFFDAY = MDEnum.valueOf(CalendarDateTypeEnum.class, Integer.valueOf(2));

	/**
	 * ������
	 */
	public static final CalendarDateTypeEnum HOLIDAY = MDEnum.valueOf(CalendarDateTypeEnum.class, Integer.valueOf(3));

	/**
	 * ����
	 */
	public static final CalendarDateTypeEnum NATIONALDAY = MDEnum.valueOf(CalendarDateTypeEnum.class,
			Integer.valueOf(4));

}
