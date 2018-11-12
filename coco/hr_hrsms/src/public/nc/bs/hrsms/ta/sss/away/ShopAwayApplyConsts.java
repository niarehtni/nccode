package nc.bs.hrsms.ta.sss.away;

import nc.vo.pub.AggregatedValueObject;
import nc.vo.ta.away.AggAwayVO;
/**
 * �곤������������ĳ�����.
 * 
 * @author shaochj
 * @date May 20, 2015
 * 
 */
public class ShopAwayApplyConsts {
	
	/** ģ���ʶ*/
	public static final String NODE_KEY = "awayapply";

	/**�������ͱ���*/
	public static final String BILL_TYPE_CODE = "6403";

	/** �ۺ�VO����*/
	public static final Class<? extends AggregatedValueObject> CLASS_NAME_AGGVO = AggAwayVO.class;

	/** �����ݼ�����*/
	public static final String DS_MAIN_NAME = "hrtaawayh";

	/**�����ݼ�����*/
	public static final String DS_SUB_NAME = "hrtaawayb";

	/** FORM-������Ϣ*/
	public static final String VIEW_FORM_BILLINFO = "headTab_card_pk_awayh_form";
	
	/** GRID-�����ӱ�*/
	public static final String VIEW_GRID_BODY = "bodyTab_card_awayb_sub_grid";
	
	public static final String WIN_CARD_NAME = "ShopAwayApplyCard";
}
