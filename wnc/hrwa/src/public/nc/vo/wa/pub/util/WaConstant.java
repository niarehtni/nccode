/**
 * @(#)WaConstant.java 1.0 2017年9月23日
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.vo.wa.pub.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author niehg
 * @since 6.3
 */
public class WaConstant {
	public static String DEF_CODE_SHARE = "WITSCS";
	public static String DEF_CODE_DEBIT_ACC = "HRAL001";
	public static String DEF_CODE_DEBIT_VEN = "HRAL002";
	public static String DEF_CODE_CREDIT_ACC = "HRAL003";
	public static String DEF_CODE_CREDIT_VEN = "HRAL004";
	public static String DEF_CODE_SSMF = "HRAL005";
	public static String DEF_CODE_PROJ = "ProjectCode";
	public static String DEF_CODE_BMVEN = "HRAL006"; // 社保供應商代碼

	public static String SHARE_MONTH_WORKHOURS = "A";
	public static String SHARE_PROJ_CODE = "B";
	public static String SHARE_PROJ_OVERHOURS = "C";
	public static String SHARE_FULL_COSTCENTER = "D";
	public static String SHARE_NOT_SHARED = "Z";
	public static String SHARE_NOT_SYS__SHARED = "X";

	public static String VENDER_SIFI = "SIFILE";
	public static String VENDER_EMPL = "EMPLOYEECODE";
	public static String VENDER_SIF_COMP = "SIFILECOMPANY";
	public static String VENDER_SIF_EMPL = "SIFILEEMPLOYEE";
	public static DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
}
