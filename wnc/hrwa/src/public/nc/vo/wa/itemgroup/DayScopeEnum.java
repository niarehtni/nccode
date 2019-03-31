package nc.vo.wa.itemgroup;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

//天数范围来源枚举
public class DayScopeEnum extends MDEnum {

	public DayScopeEnum(IEnumValue enumValue) {
		super(enumValue);
	}

	// 固定值30天
	public static final DayScopeEnum FIX30 = MDEnum.valueOf(DayScopeEnum.class, 1);
	// 固定值21.75天
	public static final DayScopeEnum FIX22 = MDEnum.valueOf(DayScopeEnum.class, 2);
	// 薪资期间天数
	public static final DayScopeEnum WPDAYS = MDEnum.valueOf(DayScopeEnum.class, 3);
	// 薪资期间计薪日天数
	public static final DayScopeEnum WPCDAYS = MDEnum.valueOf(DayScopeEnum.class, 4);
	// 考勤计薪天数
	public static final DayScopeEnum TPDAYS = MDEnum.valueOf(DayScopeEnum.class, 5);
}
