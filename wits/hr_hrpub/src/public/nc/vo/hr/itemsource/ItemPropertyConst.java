package nc.vo.hr.itemsource;

public interface ItemPropertyConst {

	public static final Integer BM = 10;// 数据来源是bm
	public static final int Date_Digit = 0;// 日期型小数位数
	public static final int Date_width = 10;// 日期型宽度
	public static final Integer FIX_VALUE = 3;// 数据来源是固定值
	public static final int Float_Digit = 2;// 数值型默认小数位数
	public static final int Float_MAX_decimalwidth = 8;// 数值型最大小数位数

	public static final int Float_MIN_decimalwidth = 0;// 数值型最小小数位数

	public static final int Float_MAX_width = 22;// 数值型最大宽度
	public static final int Float_width = 12;// 数值型默认宽度

	public static final Integer FORMULA = 0;// 数据来源是公式
	public static final Integer HI = 7;// 数据来源是人事

	public static final String IFLDDECIMAL = "iflddecimal";
	public static final String IFLDWIDTH = "ifldwidth";
	public static final String IFROMFLAG = "ifromflag";
	public static final String IITEMTYPE = "iitemtype";
	public static final String ITEMKEY = "itemkey";
	public static final int MIN_width = 1; // 数值型最小宽度

	public static final Integer OTHER_SYSTEM = 5;// 数据来源是其它系统
	public static final Integer PE = 11;// 数据来源是其它绩效
	public static final String PK_WA_GRADE = "pk_wa_grade";
	public static final String PK_WA_WAGEFORM = "pk_wa_wageform";
	public static final int String_Digit = 0;// 字符型小数位数
	public static final int String_MAX_width = 128;// 字符型最大长度

	public static final int String_width = 20;// 字符型默认长度
	public static final Integer TA = 9;// 数据来源是考勤
	public static final String TABLE_NAME = "wa_item";
	public static final Integer TIMESCOLLECT = 8;// 数据来源是次数汇总
	public static final Integer USER_INPUT = 2;// 数据来源是手工输入
	public static final String VFORMULA = "vformula";

	public static final String VFORMULASTR = "vformulastr";

	public static final Integer WA_GRADE = 4;

	public static final Integer WA_WAGEFORM = 1;
	public static final Integer WAORTHER = 6;// 数据来源是其它系统-薪资
	public static final Integer Float_type = 2;

	// ssx added for TWHR on 2017-07-24
	public static final Integer TWBASEDOC = 12;// 数据来源是其它系统-劳健保参数设置

	// 兼职记录信息集PK
	public static final String PARTTIME_INFSET_PK = "1001Z71000000000GJAH";
	// 工作记录信息集PK,此值代表取离职前工作记录
	public static final String FULLTIME_INFSET_PK = "1002Z710000000001FP5";

}
