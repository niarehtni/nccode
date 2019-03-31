package nc.ui.wa.taxspecial_statistics.model;

import nc.ui.uif2.model.BillManageModel;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 税改专项附加扣除计算AppModel
 * 
 * @author: xuhw 
 * @since: eHR V6.5
 * @走查人: 
 * @走查日期: 
 * @修改人: 
 * @修改日期:
 */
public class TaxSpecialStatisticsAppModel extends BillManageModel {
    private String lastCondition;
    
	public String getLastCondition() {
		return lastCondition;
	}

	public void setLastCondition(String lastCondition) {
		this.lastCondition = lastCondition;
	}

	public boolean canEdit() {
		Object vo = getSelectedData();
		boolean checked = false;
		if(vo instanceof PayfileVO){
			checked =((PayfileVO)vo).getCheckflag().booleanValue();
		}
		return !checked;

	}
	
	public WaLoginContext getWaContext() {
		return (WaLoginContext)super.getContext();
	}
}
