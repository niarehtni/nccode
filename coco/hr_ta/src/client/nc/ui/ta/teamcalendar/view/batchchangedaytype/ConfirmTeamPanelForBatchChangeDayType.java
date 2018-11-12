package nc.ui.ta.teamcalendar.view.batchchangedaytype;


import java.awt.BorderLayout;

import nc.hr.utils.StringPiecer;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.ta.calendar.pub.CalendarTempletCreator;
import nc.vo.bd.pub.EnableStateEnum;
import nc.vo.bd.team.team01.entity.TeamHeadVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillTempletVO;

import org.apache.commons.lang.ArrayUtils;

@SuppressWarnings("serial")
public class ConfirmTeamPanelForBatchChangeDayType extends UIPanel {

	private BillListPanel billListPanel = null;
	
	public void init(){
		setLayout(new BorderLayout());
		add(getBillListPanel(),BorderLayout.CENTER);
		
	}
	
	public void setFormVOs(SuperVO[] vos){
		getBillListPanel().getBillListData().setHeaderValueObjectByMetaData(vos);
		if(ArrayUtils.isEmpty(vos))
			return;
		for(int i=0;i<vos.length;i++){ // 2012-04-02 与需求讨论后确定，停用的班组默认不选中
			SuperVO vo = vos[i];
			Integer enable = (Integer) vo.getAttributeValue("enablestate");
			if(EnableStateEnum.ENABLESTATE_DISABLE.toIntValue() == enable)
				continue;
			getBillListPanel().getHeadBillModel().setRowState(i, BillModel.SELECTED);
				
		}
//		if(!ArrayUtils.isEmpty(vos)){
//			getBillListPanel().getHeadBillModel().setRowState(0, vos.length-1, BillModel.SELECTED);
//		}
	}
	
	public String[] getSelTeamPKs(){
		TeamHeadVO[] selVOs = BillPanelUtils.getMultiSelectedData(getBillListPanel().getHeadBillModel(), TeamHeadVO.class);
		if(ArrayUtils.isEmpty(selVOs))
			return null;
		return StringPiecer.getStrArray(selVOs, TeamHeadVO.CTEAMID);
	}
	
	public TeamHeadVO[] getSelTeamVOs(){
		return BillPanelUtils.getMultiSelectedData(getBillListPanel().getHeadBillModel(), TeamHeadVO.class);
	}
	
	protected BillListPanel getBillListPanel() {
		if(billListPanel==null){
			billListPanel = new BillListPanel();
			BillTempletVO btv = CalendarTempletCreator.createTeamCalendarBasicItems();
			BillListData listData = new BillListData(btv);
			billListPanel.setListData(listData);
			billListPanel.setMultiSelect(true);
			billListPanel.getParentListPanel().showTableCol("enablestate");
		}
		return billListPanel;
	}
}
