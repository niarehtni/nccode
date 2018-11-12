package nc.bs.hrsms.ta.sss.away;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.ta.away.AggAwayVO;
/**
 * 店长自助出差申请的常量类.
 * 
 * @author shaochj
 * @date May 20, 2015
 * 
 */
public class ShopAwayApplyConsts {
	
	/** 模板标识*/
	public static final String NODE_KEY = "awayapply";

	/**单据类型编码*/
	public static final String BILL_TYPE_CODE = "6403";

	/** 聚合VO类名*/
	public static final Class<? extends AggregatedValueObject> CLASS_NAME_AGGVO = AggAwayVO.class;

	/** 主数据集名称*/
	public static final String DS_MAIN_NAME = "hrtaawayh";

	/**子数据集名称*/
	public static final String DS_SUB_NAME = "hrtaawayb";

	/** FORM-单据信息*/
	public static final String VIEW_FORM_BILLINFO = "headTab_card_pk_awayh_form";
	
	/** GRID-单据子表*/
	public static final String VIEW_GRID_BODY = "bodyTab_card_awayb_sub_grid";
	
	public static final String WIN_CARD_NAME = "ShopAwayApplyCard";
}
