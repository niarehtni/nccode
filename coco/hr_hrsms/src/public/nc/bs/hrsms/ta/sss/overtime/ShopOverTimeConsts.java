package nc.bs.hrsms.ta.sss.overtime;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.ta.overtime.AggOvertimeVO;
/**
 * 店长自助加班申请的常量类.
 * 
 * @author shaochj
 * @date May 20, 2015
 * 
 */
public class ShopOverTimeConsts {

	/**模板标识*/
	public static final String NODE_KEY = "overtimeapp";

	/**单据类型编码*/
	public static final String BILL_TYPE_CODE = "6405";

	/**聚合VO类名*/
	public static final Class<? extends AggregatedValueObject> CLASS_NAME_AGGVO = AggOvertimeVO.class;

	/**主数据集名称*/
	public static final String DS_MAIN_NAME = "hrtaovertimeh";

	/**子数据集名称*/
	public static final String DS_SUB_NAME = "hrtaovertimeb";

	/** FORM-单据信息*/
	public static final String VIEW_FORM_BILLINFO = "headTab_card_pk_overtime_form";
	
	/**GRID-单据子表*/
	public static final String VIEW_GRID_BODY = "bodyTab_card_overtimeb_sub_grid";

}
