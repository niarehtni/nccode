package nc.vo.hrta.hrtaovertimereg;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * <b>�˴���Ҫ������ö�ٵĹ��� </b>
 * <p>
 *   �˴���Ӹ�ö�ٵ�������Ϣ
 * </p>
 *  ��������:2018-8-15
 * @author 
 * @version NCPrj ??
 */
public class SpecialEnume extends MDEnum{
	public SpecialEnume(IEnumValue enumvalue){
		super(enumvalue);
	}

	
	public static final SpecialEnume SPECIAL_NOT = MDEnum.valueOf(SpecialEnume.class, String.valueOf(0));
	
	public static final SpecialEnume SPECIAL_NEXT = MDEnum.valueOf(SpecialEnume.class, String.valueOf(1));
	
	public static final SpecialEnume SPECIAL_CURRENT = MDEnum.valueOf(SpecialEnume.class, String.valueOf(2));
	

}