package nc.vo.wa.payfile;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * <b>此處簡要描述此枚舉的功能 </b>
 * <p>
 * 此處添加該枚舉的描述信息
 * </p>
 * 創建日期:2020/10/20
 * 
 * @author
 * @version NCPrj ??
 */
public class ExNhiTypeEnum extends MDEnum {
	public ExNhiTypeEnum(IEnumValue enumValue) {
		super(enumValue);
		// TODO 自動產生的建構子 Stub
	}

	// 補充保費費基
	public static final ExNhiTypeEnum EXNHIBASE = MDEnum.valueOf(ExNhiTypeEnum.class, Integer.valueOf(1));

	// 兼職所得
	public static final ExNhiTypeEnum PARTTIME = MDEnum.valueOf(ExNhiTypeEnum.class, Integer.valueOf(2));

}
