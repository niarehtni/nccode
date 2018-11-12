package nc.vo.hrss.psnmap;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;
  
/**
 * <b>此处简要描述此枚举的功能 </b>
 * <p>
 *   此处添加该枚举的描述信息
 * </p>
 *  创建日期:2018-8-20
 * @author 
 * @version NCPrj ??
 */
public class IsMonsalaryEnum extends MDEnum{
	public IsMonsalaryEnum(IEnumValue enumvalue){
		super(enumvalue);
	}

	
	
	public static final IsMonsalaryEnum 月薪 = MDEnum.valueOf(IsMonsalaryEnum.class, Integer.valueOf(1));
	
	
	public static final IsMonsalaryEnum 非月薪 = MDEnum.valueOf(IsMonsalaryEnum.class, Integer.valueOf(2));
	

}