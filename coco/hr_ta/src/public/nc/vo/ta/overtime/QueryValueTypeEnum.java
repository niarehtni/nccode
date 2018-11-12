package nc.vo.ta.overtime;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * 
 * 
 * @author
 * @version NCPrj ??
 */
public class QueryValueTypeEnum extends MDEnum {
    public QueryValueTypeEnum(IEnumValue enumvalue) {
	super(enumvalue);
    }

    /**
     * 所有
     */
    public static final QueryValueTypeEnum ALL = MDEnum.valueOf(QueryValueTypeEnum.class, Integer.valueOf(1));

    /**
     * 平日
     */
    public static final QueryValueTypeEnum NORMAL = MDEnum.valueOf(QueryValueTypeEnum.class, Integer.valueOf(2));

    /**
     * 休息日
     */
    public static final QueryValueTypeEnum OFFDAY = MDEnum.valueOf(QueryValueTypeEnum.class, Integer.valueOf(3));

    /**
     * 例假日
     */
    public static final QueryValueTypeEnum HOLIDAY = MDEnum.valueOf(QueryValueTypeEnum.class, Integer.valueOf(4));

    /**
     * 国假
     */
    public static final QueryValueTypeEnum NATIONALDAY = MDEnum.valueOf(QueryValueTypeEnum.class, Integer.valueOf(5));

    /**
     * 金额
     */
    public static final QueryValueTypeEnum TOTALFEE = MDEnum.valueOf(QueryValueTypeEnum.class, Integer.valueOf(6));

}
