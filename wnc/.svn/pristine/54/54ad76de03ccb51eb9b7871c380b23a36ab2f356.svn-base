package nc.vo.ta.overtime;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * 加班时数结算类型
 * 
 * @author
 * @version NC V6.5 TWLC 3.2.2
 */
public class OvertimeSettleTypeEnum extends MDEnum {
    public OvertimeSettleTypeEnum(IEnumValue enumvalue) {
	super(enumvalue);
    }

    /**
     * 本期加班（转薪）时数
     */
    public static final OvertimeSettleTypeEnum PERIOD_TOSALARY = MDEnum.valueOf(OvertimeSettleTypeEnum.class,
	    Integer.valueOf(1));

    /**
     * 往期加班（转薪）时数
     */
    public static final OvertimeSettleTypeEnum OTHER_TOSALARY = MDEnum
	    .valueOf(OvertimeSettleTypeEnum.class, Integer.valueOf(2));

    /**
     * 本期加班（转休）时数
     */
    public static final OvertimeSettleTypeEnum PERIOD_TOREST = MDEnum.valueOf(OvertimeSettleTypeEnum.class, Integer.valueOf(3));

    /**
     * 全部时数
     */
    public static final OvertimeSettleTypeEnum TOTAL = MDEnum.valueOf(OvertimeSettleTypeEnum.class, Integer.valueOf(4));

}
