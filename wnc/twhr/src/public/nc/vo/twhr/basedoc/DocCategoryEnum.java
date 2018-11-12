package nc.vo.twhr.basedoc;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class DocCategoryEnum extends MDEnum {

	public DocCategoryEnum(IEnumValue enumValue) {
		super(enumValue);
	}

	/**
	 * �ͱ����˲���
	 */
	public static final DocCategoryEnum LABORREF = MDEnum.valueOf(
			DocCategoryEnum.class, Integer.valueOf(1));

	/**
	 * ��������
	 */
	public static final DocCategoryEnum NHIREF = MDEnum.valueOf(
			DocCategoryEnum.class, Integer.valueOf(2));

	/**
	 * ���䱣�ղ���
	 */
	public static final DocCategoryEnum EXNHIREF = MDEnum.valueOf(
			DocCategoryEnum.class, Integer.valueOf(3));

	/**
	 * н�ʲ���
	 */
	public static final DocCategoryEnum WAGEREF = MDEnum.valueOf(
			DocCategoryEnum.class, Integer.valueOf(4));
	/**
	 * �ͽ����걨����
	 */
	public static final DocCategoryEnum LHIREF = MDEnum.valueOf(
			DocCategoryEnum.class, Integer.valueOf(5));
	/**
	 * ����˰�걨����
	 */
	public static final DocCategoryEnum ITAXREF = MDEnum.valueOf(
			DocCategoryEnum.class, Integer.valueOf(6));
	
	/**
	 * ̨�����ػ�����
	 */
	public static final DocCategoryEnum ITWLOCAL = MDEnum.valueOf(
			DocCategoryEnum.class, Integer.valueOf(7));
}
