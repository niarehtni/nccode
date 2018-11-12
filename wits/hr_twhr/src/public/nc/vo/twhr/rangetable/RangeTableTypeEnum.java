package nc.vo.twhr.rangetable;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class RangeTableTypeEnum extends MDEnum {

	public RangeTableTypeEnum(IEnumValue enumValue) {
		super(enumValue);
	}

	/**
	 * 劳保级距
	 */
	public static final RangeTableTypeEnum LABOR_RANGETABLE = MDEnum.valueOf(
			RangeTableTypeEnum.class, Integer.valueOf(1));

	/**
	 * 劳退级距
	 */
	public static final RangeTableTypeEnum RETIRE_RANGETABLE = MDEnum.valueOf(
			RangeTableTypeEnum.class, Integer.valueOf(2));

	/**
	 * 健保级距
	 */
	public static final RangeTableTypeEnum NHI_RANGETABLE = MDEnum.valueOf(
			RangeTableTypeEnum.class, Integer.valueOf(3));
	//
	// /**
	// * 综所税级距
	// */
	// public static final RangeTableTypeEnum TAX_RANGETABLE = MDEnum.valueOf(
	// RangeTableTypeEnum.class, Integer.valueOf(4));
}
