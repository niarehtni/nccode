package nc.vo.wa.payfile;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * <b>��̎��Ҫ������ö�e�Ĺ��� </b>
 * <p>
 * ��̎���ԓö�e��������Ϣ
 * </p>
 * ��������:2020/10/20
 * 
 * @author
 * @version NCPrj ??
 */
public class ExNhiTypeEnum extends MDEnum {
	public ExNhiTypeEnum(IEnumValue enumValue) {
		super(enumValue);
		// TODO �ԄӮa���Ľ����� Stub
	}

	// �a�䱣�M�M��
	public static final ExNhiTypeEnum EXNHIBASE = MDEnum.valueOf(ExNhiTypeEnum.class, Integer.valueOf(1));

	// ������
	public static final ExNhiTypeEnum PARTTIME = MDEnum.valueOf(ExNhiTypeEnum.class, Integer.valueOf(2));

}
