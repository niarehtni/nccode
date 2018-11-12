package nc.ui.ta.psndocwadoc.view.labourjoin;

import java.awt.BorderLayout;
import org.apache.commons.lang.ArrayUtils;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.ta.pub.standardpsntemplet.PsnTempletOutUtils;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.ta.psndoc.TBMPsndocVO;

@SuppressWarnings("restriction")
public class ConfirmPsnPanelForOutJoin extends UIPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5985391483446618328L;
	
	private BillListPanel billListPanel = null;

	public ConfirmPsnPanelForOutJoin() {
		
	}
	
	public void init(){
		setLayout(new BorderLayout());
		add(getBillListPanel(),BorderLayout.CENTER);
		
	}
	
	
	public void setFormVOs(SuperVO[] vos){
		getBillListPanel().getBillListData().setHeaderValueObjectByMetaData(vos);
		if(!ArrayUtils.isEmpty(vos))
			getBillListPanel().getHeadBillModel().setRowState(0, vos.length-1, BillModel.SELECTED);
	}
	
	public String[] getSelPkPsndocs(){
		TBMPsndocVO[] selVOs = BillPanelUtils.getMultiSelectedData(getBillListPanel().getHeadBillModel(), TBMPsndocVO.class);
		if(ArrayUtils.isEmpty(selVOs))
			return null;
		String[] retPks = new String[selVOs.length];
		for(int i=0;i<selVOs.length;i++){
			retPks[i]=selVOs[i].getPk_psndoc();
		}
		return retPks;
	}

	protected BillListPanel getBillListPanel() {
		if(billListPanel==null){
			billListPanel = new BillListPanel();
			BillTempletVO btv = PsnTempletOutUtils.createSeniorItems(IBillItem.HEAD);
			BillListData listData = new BillListData(btv);
			billListPanel.setListData(listData);
			billListPanel.setMultiSelect(true);
		}
		return billListPanel;
	}
}
