package nc.bs.hrsms.ta.sss.shopleave;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.ta.leave.AggLeaveVO;

public class ShopLeaveApplyConsts {

		/** ģ���ʶ*/
		public static final String NODE_KEY = "6017leaveapply_b";

		/** �������ͱ���*/
		public static final String BILL_TYPE_CODE = "6404";

		/**  �ۺ�VO����*/
		public static final Class<? extends AggregatedValueObject> CLASS_NAME_AGGVO = AggLeaveVO.class;

		/**  �����ݼ�����*/
		public static final String DS_MAIN_NAME = "hrtaleave";

		/** �����ݼ�����*/
		public static final String DS_SUB_NAME = "hrtaleaveb";

		/** FORM-������Ϣ*/
		public static final String VIEW_FORM_BILLINFO = "headTab_card_pk_leaveh_form";

		/** �ݼ�����ʹ�ó��� */
		// ҳ��Form-�ݼ���Ϣ
		public static final String PAGE_FORM_LEAVEINFO = "headTab_card_leaveinf_form";
		
		/**  GRID-�����ӱ�*/
		public static final String VIEW_GRID_BODY = "bodyTab_card_leaveb_sub_grid";

		/** ����������ݼ���ID*/
		public static final String WIDGET_COMBODATA_YEAR = "combo_hrtaleave_leaveyear";

		/**  �ڼ��������ݼ���ID*/
		public static final String WIDGET_COMBODATA_MONTH = "combo_hrtaleave_leavemonth";
		
}
