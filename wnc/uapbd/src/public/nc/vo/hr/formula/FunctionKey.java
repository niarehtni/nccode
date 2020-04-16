package nc.vo.hr.formula;

import nc.hr.utils.ResHelper;

/**
 * @author: zhangg
 * @date: 2010-5-6 11:54:20
 * 
 */
public class FunctionKey {
	public static final String BUDGETITEM = "BUDGETITEM";//

	public static final String CALMONTH = "CALMONTH";// 自然月份

	public static final String CALMONTH_DES = ResHelper.getString("6001formula", "06001formula0022")
	/* @res "自然月份" */;// 自然月份

	public String getCALMONTH_DES() {
		return ResHelper.getString("6001formula", "06001formula0022")
		/* @res "自然月份" */;// 自然月份
	}

	public static final String CALYEAR = "CALYEAR";// 自然年度

	public static final String CALYEAR_DES = ResHelper.getString("6001formula", "06001formula0023")
	/* @res "自然年度" */;// 自然年度

	public String getCALYEAR_DES() {
		return ResHelper.getString("6001formula", "06001formula0023")
		/* @res "自然年度" */;// 自然年度
	}

	public static final String CURRDATE = "CURRDATE";// 当前系统日期

	public static final String CURRDATE_DES = ResHelper.getString("6001formula", "06001formula0024")
	/* @res "当前系统日期" */;// 当前系统日期

	public String getCURRDATE_DES() {
		return ResHelper.getString("6001formula", "06001formula0024")
		/* @res "当前系统日期" */;// 当前系统日期
	}

	public static final String CURRORGCODE = "CURRORGCODE";// 当前组织编码

	public static final String CURRORGCODE_DES = ResHelper.getString("6001formula", "06001formula0025")
	/* @res "当前组织编码" */;// 当前组织编码

	public String getCURRORGCODE_DES() {
		return ResHelper.getString("6001formula", "06001formula0025")
		/* @res "当前组织编码" */;// 当前组织编码
	}

	public static final String CURRORGNAME = "CURRORGNAME";// 当前组织名称

	public static final String CURRORGNAME_DES = ResHelper.getString("6001formula", "06001formula0026")
	/* @res "当前组织名称" */;// 当前组织名称

	public String getCURRORGNAME_DES() {
		return ResHelper.getString("6001formula", "06001formula0026")
		/* @res "当前组织名称" */;// 当前组织名称
	}

	public static final String CURRORGPK = "CURRORGPK";// 当前组织主键

	public static final String CURRORGPK_DES = ResHelper.getString("6001formula", "06001formula0027")
	/* @res "当前组织主键" */;// 当前组织主键

	public String getCURRORGPK_DES() {
		return ResHelper.getString("6001formula", "06001formula0027")
		/* @res "当前组织主键" */;// 当前组织主键
	}

	public static final String EXCHANGERATE = "EXCHANGERATE";// 汇率函数

	public static final String EXCHANGERATE_DES = ResHelper.getString("6001formula", "06001formula0028")
	/* @res "汇率函数" */;// 汇率函数

	public String getEXCHANGERATE_DES() {
		return ResHelper.getString("6001formula", "06001formula0028")
		/* @res "汇率函数" */;// 汇率函数
	}

	public static final String FIRSTMONWORKDAYS = "FIRSTMONWORKDAYS";// 受聘月工作日天数

	public static final String FIRSTMONWORKDAYS_DES = ResHelper.getString("6001formula", "06001formula0029")
	/* @res "受聘月工作日天数" */;// 受聘月工作日天数

	public String getFIRSTMONWORKDAYS_DES() {
		return ResHelper.getString("6001formula", "06001formula0029")
		/* @res "受聘月工作日天数" */;// 受聘月工作日天数
	}

	public static final String FUNPAYDATA = "FUNPAYDATA";// 取指定类别指定期间的薪资汇总数据

	public static final String GRADEINFO = "GRADEINFO";// 取指定薪资标准类别下的薪资级别和档次
	public static final String GRADEVALUE = "GRADEVALUE";// 取指定薪资标准类别下的数值

	public static final String LASTMONWORKDAYS = "LASTMONWORKDAYS";// 离职月工作日天数
	public static final String LASTMONWORKDAYS_DES = ResHelper.getString("6001formula", "06001formula0030")
	/* @res "离职月工作日天数" */;// 离职月工作日天数

	public String getLASTMONWORKDAYS_DES() {
		return ResHelper.getString("6001formula", "06001formula0030")
		/* @res "离职月工作日天数" */;// 离职月工作日天数
	}

	/*
	 * public static final String AVEPAYDATA = "AVEPAYDATA";//取薪资数据的平均值 public
	 * static final String MAXPAYDATA = "MAXPAYDATA";//取薪资数据的最大值 public static
	 * final String MINPAYDATA = "MINPAYDATA";//取薪资数据的最小值
	 */
	public static final String PAYDATA = "PAYDATA";// 取指定类别的相同薪资期间的薪资项目数据
	public static final String PAYDATAAB = "PAYDATAAB";// 薪资统计数_相对时间段

	public static final String PAYDATAITEM = "PAYDATAITEM";// 薪资函数:按期间项目取数
	public static final String PAYDATAOP = "PAYDATAOP";// 薪资统计数_绝对时间段

	public static final String PAYDATAPD = "PAYDATAPD";// 取指定类别指定期间的薪资数据
	public static final String PAYDATASF = "PAYDATASF";// 团队统计函数

	public static final String PSNAGE = "PSNAGE";// 人事函数:年龄
	public static final String PSNAGE_DES = ResHelper.getString("common", "UC000-0001813")
	/* @res "年龄" */;// 人事函数:年龄

	public static final String TAXRATE = "TAXRATE";// 扣税函数
	public static final String TAXRATE_DES = ResHelper.getString("6001formula", "06001formula0031")
	/* @res "扣税函数" */;// 扣税函数

	public static final String UNITAGE = "UNITAGE";// 人事函数:内部工龄
	public static final String UNITAGE_DES = ResHelper.getString("common", "UC000-0000437")
	/* @res "内部工龄" */;// 人事函数:内部工龄

	public static final String USERCODE = "USERCODE";// 登录用户编码
	public static final String USERCODE_DES = ResHelper.getString("6001formula", "06001formula0032")
	/* @res "登录用户编码" */;// 登录用户编码

	public static final String USERNAME = "USERNAME";// 登录用户姓名
	public static final String USERNAME_DES = ResHelper.getString("6001formula", "06001formula0033")
	/* @res "登录用户姓名" */;// 登录用户姓名

	public static final String USERPK = "USERPK";// 登录用户主键
	public static final String USERPK_DES = ResHelper.getString("6001formula", "06001formula0034")
	/* @res "登录用户主键" */;// 登录用户主键

	public static final String VALUEOFWADOC = "VALUEOFWADOC";// 定调资数据
	public static final String WAGEFORM = "WAGEFORM";// 取指薪资规则表数据

	public static final String OTHERSOURCE = "OTHERSOURCE";// 取自外部数据源

	public static final String WAGEMONTH = "WAGEMONTH";// 薪资期间
	public static final String WAGEMONTH_DES = ResHelper.getString("6001formula", "06001formula0035")
	/* @res "薪资期间" */;// 薪资期间

	public String getWAGEMONTH_DES() {
		return ResHelper.getString("6001formula", "06001formula0035")
		/* @res "薪资期间" */;// 薪资期间
	}

	public static final String WAGEYEAR = "WAGEYEAR";// 薪资年度
	public static final String WAGEYEAR_DES = ResHelper.getString("6001formula", "06001formula0036")
	/* @res "薪资年度" */;// 薪资年度

	public String getWAGEYEAR_DES() {
		return ResHelper.getString("6001formula", "06001formula0036")
		/* @res "薪资年度" */;// 薪资年度
	}

	public static final String WAPERIODDAYS = "WAPERIODDAYS";// 期间日历天数
	public static final String WAPERIODDAYS_DES = ResHelper.getString("6001formula", "06001formula0037")
	/* @res "期间日历天数" */;// 期间日历天数

	public String getWAPERIODDAYS_DES() {
		return ResHelper.getString("6001formula", "06001formula0037")
		/* @res "期间日历天数" */;// 期间日历天数
	}

	public static final String WAPERIODENDDATE = "WAPERIODENDDATE";// 期间结束日期
	public static final String WAPERIODENDDATE_DES = ResHelper.getString("6001formula", "06001formula0038")
	/* @res "期间结束日期" */;// 期间结束日期

	public String getWAPERIODENDDATE_DES() {
		return ResHelper.getString("6001formula", "06001formula0038")
		/* @res "期间结束日期" */;// 期间结束日期
	}

	public static final String WAPERIODSTARTDATE = "WAPERIODSTARTDATE";// 期间开始日期
	public static final String WAPERIODSTARTDATE_DES = ResHelper.getString("6001formula", "06001formula0039")
	/* @res "期间开始日期" */;// 期间开始日期

	public String getWAPERIODSTARTDATE_DES() {
		return ResHelper.getString("6001formula", "06001formula0039")
		/* @res "期间开始日期" */;// 期间开始日期
	}

	public static final String WAPERIODWORKDAYS = "WAPERIODWORKDAYS";// 期间工作日天数
	public static final String WAPERIODWORKDAYS_DES = ResHelper.getString("6001formula", "06001formula0040")
	/* @res "期间工作日天数" */;// 期间工作日天数

	public String getWAPERIODWORKDAYS_DES() {
		return ResHelper.getString("6001formula", "06001formula0040")
		/* @res "期间工作日天数" */;// 期间工作日天数
	}

	public static final String WORKAGE = "WORKAGE";// 人事函数:工龄
	public static final String WORKAGE_DES = ResHelper.getString("common", "UC000-0001696")
	/* @res "工龄" */;// 人事函数:工龄

	public String getWORKAGE_DES() {
		return ResHelper.getString("common", "UC000-0001696")
		/* @res "工龄" */;// 人事函数:工龄
	}

	// mod by Connie.ZH
	// 2019-05-28 started
	public static final String Cal_Accumulate_Paid = "CalAccumulatePaid";// 福委會累計給付總額
	public static final String Cal_Paid_Welfare = "CalPaidWelfare";// 福委會累計代扣福利金
	// 2019-05-28 ended
	public static final String PREPERIODSTARTDATE = "PREPERIODSTARTDATE";// 上期间开始日期
	public static final String PREPERIODENDDATE = "PREPERIODENDDATE";// 上期间结束日期
	public static final String REGISTERDATE = "REGISTERDATE";// 入职日期
	public static final String DISMISSIONDATE = "DISMISSIONDATE";// 离职日期
	public static final String WAGEDAYS = "WAGEDAYS";// 计薪日天数

	// ssx added on 2020-02-21
	public static final String TERMWAGEDAYS = "TERMWAGEDAYS";// 在职期间计薪日天数

	public static final String INTERVALDURATION = "INTERVALDURATION";// 區間留停天數
	public static final String OVERTIMEPAY = "OVERTIMEPAY";// 免税加班薪资
	public static final String TAXABLEOVERTIMEPAY = "TAXABLEOVERTIMEPAY";// 应税加班薪资
	public static final String PREADJUSTDATE = "PREADJUSTDATE";// 上次调薪日期
	public static final String STATISTICLEAVECHARGE = "STATISTICLEAVECHARGE";// 統計請假扣款函數
																				// add
																				// by
																				// ward
																				// 20180511
	public static final String ITEMDATARATIO = "ITEMDATARATIO";// 项目占比
	public static final String COURTDEDUCT = "COURTDEDUCT";// 法院强制扣款 by he
	public static final String YEARLYRESTHOURS = "YEARLYRESTHOURS"; // 外加補休剩餘時數
																	// add by
																	// ssx on
																	// 2019-01-25
	public static final String LEAVEYEARLYRESTHOURS = "LEAVEYEARLYRESTHOURS"; // 離職人員外加補休剩餘時數
	public static final String BONUSBASEOFLASTTERM = "BONUSBASEOFLASTTERM"; // 上期期末獎金基數
	public static final String LASTWADOCAMOUNT = "LASTWADOCAMOUNT"; // 最新定薪函数
}
