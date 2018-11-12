package nc.vo.hrwa.wadaysalary;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class AvageMonthEnum extends MDEnum{
	public AvageMonthEnum(IEnumValue enumvalue){
		super(enumvalue);
	}

	
	
	public static final AvageMonthEnum one_monthnum = MDEnum.valueOf(AvageMonthEnum.class, Integer.valueOf(1));
	
	
	public static final AvageMonthEnum two_monthnum = MDEnum.valueOf(AvageMonthEnum.class, Integer.valueOf(2));
	
	public static final AvageMonthEnum three_monthnum = MDEnum.valueOf(AvageMonthEnum.class, Integer.valueOf(3));
	
	public static final AvageMonthEnum four_monthnum = MDEnum.valueOf(AvageMonthEnum.class, Integer.valueOf(4));
	public static final AvageMonthEnum five_monthnum = MDEnum.valueOf(AvageMonthEnum.class, Integer.valueOf(5));
	public static final AvageMonthEnum six_monthnum = MDEnum.valueOf(AvageMonthEnum.class, Integer.valueOf(6));
	public static final AvageMonthEnum seven_monthnum = MDEnum.valueOf(AvageMonthEnum.class, Integer.valueOf(7));
	public static final AvageMonthEnum eight_monthnum = MDEnum.valueOf(AvageMonthEnum.class, Integer.valueOf(8));
	public static final AvageMonthEnum nine_monthnum = MDEnum.valueOf(AvageMonthEnum.class, Integer.valueOf(9));
	public static final AvageMonthEnum ten_monthnum = MDEnum.valueOf(AvageMonthEnum.class, Integer.valueOf(10));
	public static final AvageMonthEnum eleven_monthnum = MDEnum.valueOf(AvageMonthEnum.class, Integer.valueOf(11));
	public static final AvageMonthEnum twelve_monthnum = MDEnum.valueOf(AvageMonthEnum.class, Integer.valueOf(12));
	
}
