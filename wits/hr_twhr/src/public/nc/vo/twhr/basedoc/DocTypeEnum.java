package nc.vo.twhr.basedoc;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class DocTypeEnum extends MDEnum {

	public DocTypeEnum(IEnumValue enumValue) {
		super(enumValue);
	}

	/**
	 * 数量金额
	 */
	public static final DocTypeEnum AMOUNT = MDEnum.valueOf(DocTypeEnum.class,
			Integer.valueOf(1));

	/**
	 * 费率比例
	 */
	public static final DocTypeEnum RATE = MDEnum.valueOf(DocTypeEnum.class,
			Integer.valueOf(2));

	/**
	 * 薪资发放项目
	 */
	public static final DocTypeEnum WAGEITEM = MDEnum.valueOf(
			DocTypeEnum.class, Integer.valueOf(3));
}
