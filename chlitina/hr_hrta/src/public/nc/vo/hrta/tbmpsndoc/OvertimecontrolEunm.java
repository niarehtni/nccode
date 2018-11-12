package nc.vo.hrta.tbmpsndoc;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * <b>此处简要描述此枚举的功能 </b>
 * <p>
 *   此处添加该枚举的描述信息
 * </p>
 *  创建日期:2018-9-21
 * @author 
 * @version NCPrj ??
 */
public class OvertimecontrolEunm extends MDEnum{
	public OvertimecontrolEunm(IEnumValue enumvalue){
		super(enumvalue);
	}

	
	
	public static final OvertimecontrolEunm MANUAL_CHECK = MDEnum.valueOf(OvertimecontrolEunm.class, Integer.valueOf(1));
	
	
	public static final OvertimecontrolEunm MACHINE_CHECK = MDEnum.valueOf(OvertimecontrolEunm.class, Integer.valueOf(2));
	

}