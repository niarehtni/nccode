package nc.vo.hrss.psnmap;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;
  
/**
 * <b>�˴���Ҫ������ö�ٵĹ��� </b>
 * <p>
 *   �˴���Ӹ�ö�ٵ�������Ϣ
 * </p>
 *  ��������:2018-8-20
 * @author 
 * @version NCPrj ??
 */
public class IsMonsalaryEnum extends MDEnum{
	public IsMonsalaryEnum(IEnumValue enumvalue){
		super(enumvalue);
	}

	
	
	public static final IsMonsalaryEnum ��н = MDEnum.valueOf(IsMonsalaryEnum.class, Integer.valueOf(1));
	
	
	public static final IsMonsalaryEnum ����н = MDEnum.valueOf(IsMonsalaryEnum.class, Integer.valueOf(2));
	

}