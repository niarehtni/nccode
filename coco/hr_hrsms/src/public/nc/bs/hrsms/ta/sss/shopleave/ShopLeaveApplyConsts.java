package nc.bs.hrsms.ta.sss.shopleave;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.ta.leave.AggLeaveVO;

public class ShopLeaveApplyConsts {

		/** 模板标识*/
		public static final String NODE_KEY = "6017leaveapply_b";

		/** 单据类型编码*/
		public static final String BILL_TYPE_CODE = "6404";

		/**  聚合VO类名*/
		public static final Class<? extends AggregatedValueObject> CLASS_NAME_AGGVO = AggLeaveVO.class;

		/**  主数据集名称*/
		public static final String DS_MAIN_NAME = "hrtaleave";

		/** 子数据集名称*/
		public static final String DS_SUB_NAME = "hrtaleaveb";

		/** FORM-单据信息*/
		public static final String VIEW_FORM_BILLINFO = "headTab_card_pk_leaveh_form";

		/** 休假审批使用常量 */
		// 页面Form-休假信息
		public static final String PAGE_FORM_LEAVEINFO = "headTab_card_leaveinf_form";
		
		/**  GRID-单据子表*/
		public static final String VIEW_GRID_BODY = "bodyTab_card_leaveb_sub_grid";

		/** 年度下拉数据集的ID*/
		public static final String WIDGET_COMBODATA_YEAR = "combo_hrtaleave_leaveyear";

		/**  期间下拉数据集的ID*/
		public static final String WIDGET_COMBODATA_MONTH = "combo_hrtaleave_leavemonth";
		
}
