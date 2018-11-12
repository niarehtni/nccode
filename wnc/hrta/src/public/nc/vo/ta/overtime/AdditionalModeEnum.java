package nc.vo.ta.overtime;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * <b>此處簡要描述此枚舉的功能 </b>
 * <p>
 *   此處添加該枚舉的描述信息
 * </p>
 *  創建日期:2018/4/23
 * @author 
 * @version NCPrj ??
 */
public class AdditionalModeEnum extends MDEnum{
	public AdditionalModeEnum(IEnumValue enumvalue){
		super(enumvalue);
	}

	
	
	public static final AdditionalModeEnum EXISTS = MDEnum.valueOf(AdditionalModeEnum.class, Integer.valueOf(1));
	
	
	public static final AdditionalModeEnum RATE = MDEnum.valueOf(AdditionalModeEnum.class, Integer.valueOf(2));
	
	
	public static final AdditionalModeEnum WEIGHTRATE = MDEnum.valueOf(AdditionalModeEnum.class, Integer.valueOf(3));
	
	
	public static final AdditionalModeEnum MAXWEIGHT = MDEnum.valueOf(AdditionalModeEnum.class, Integer.valueOf(4));
	
	
	public static final AdditionalModeEnum SUMWEIGHT = MDEnum.valueOf(AdditionalModeEnum.class, Integer.valueOf(5));
	

}
