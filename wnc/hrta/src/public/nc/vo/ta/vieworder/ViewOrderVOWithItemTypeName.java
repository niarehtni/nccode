package nc.vo.ta.vieworder;

import nc.hr.utils.ResHelper;
import nc.vo.ta.timeitem.TimeItemVO;

public class ViewOrderVOWithItemTypeName extends ViewOrderVO {
	/**
	 *
	 */
	private static final long serialVersionUID = -2270457732628804330L;

	public ViewOrderVOWithItemTypeName(ViewOrderVO vo){
		setName(vo.getName());
		setName2(vo.getName2());
		setName3(vo.getName3());
		setName4(vo.getName4());
		setName5(vo.getName5());
		setName6(vo.getName6());
		setCode(vo.getCode());
		setData_type(vo.getData_type());
		setItem_type(vo.getItem_type());
		setTimeitem_type(vo.getTimeitem_type());
		setField_name(vo.getField_name());
		setFun_type(vo.getFun_type());
		setReporttype(vo.getReporttype());
		setPk_group(vo.getPk_group());
		setPk_org(vo.getPk_org());
		setPk_timeitem(vo.getPk_timeitem());
		setPk_vieworder(vo.getPk_vieworder());
		setUnit(vo.getUnit());
		setView_order(vo.getView_order());
	}

	@Override
	public String toString() {
		String preFix = null;
		if(getItem_type()==ViewOrderVO.ITEM_TYPE_DAYMONTHITEM){
			preFix=ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0078")
/*@res "日报项目"*/;
		}
		else{
			switch(getTimeitem_type()){
			case TimeItemVO.LEAVE_TYPE:
				preFix=ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0082")
/*@res "休假数据"*/;
				break;
			case TimeItemVO.OVERTIME_TYPE:
				preFix=ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0083")
/*@res "加班数据"*/;
				break;
			case TimeItemVO.AWAY_TYPE:
				preFix=ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0084")
/*@res "出差数据"*/;
				break;
			case TimeItemVO.SHUTDOWN_TYPE:
				preFix=ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0085")
/*@res "停工待料数据"*/;
				break;
			}
		}
		return "["+preFix+"."+getMultilangName()+"]";
	}
}