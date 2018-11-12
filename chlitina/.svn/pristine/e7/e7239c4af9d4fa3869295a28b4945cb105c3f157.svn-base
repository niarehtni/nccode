package nc.ui.ta.psndocwadoc.view.labourjoin;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.hr.hi.InsuranceTypeEnum;
import nc.itf.hr.wa.IPsndocwadocLabourService;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.ta.pub.standardpsntemplet.PsnTempletQuitUtils;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.psndoc.TBMPsndocVO;

@SuppressWarnings({"restriction","unchecked"})
public class ConfirmPsnPanelForQuitJoin extends UIPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5985391483446618328L;
	
	private BillListPanel billListPanel = null;

	public ConfirmPsnPanelForQuitJoin() {
		
	}
	
	public void init(){
		setLayout(new BorderLayout());
		add(getBillListPanel(),BorderLayout.CENTER);
		
	}
	
	
//	public void setFormVOs(SuperVO[] vos){
//		getBillListPanel().getBillListData().setHeaderValueObjectByMetaData(vos);
//		if(!ArrayUtils.isEmpty(vos))
//			getBillListPanel().getHeadBillModel().setRowState(0, vos.length-1, BillModel.SELECTED);
//	}
	public void setFormVOs(PsnJobVO[] vos, String laoJiJu, String tuiJiJu, String jianJiJu) throws BusinessException {
		vos = rebuildVOs(vos, laoJiJu, tuiJiJu, jianJiJu);
		getBillListPanel().getBillListData().setHeaderValueObjectByMetaData(vos);

		for (int i = 0; i < getBillListPanel().getHeadBillModel().getRowCount(); i++) {
			String pk_psndoc = (String) getBillListPanel().getHeadBillModel().getValueAt(i, "pk_psndoc");
			for (PsnJobVO vo : vos) {
				if (pk_psndoc.equals(vo.getAttributeValue("pk_psndoc"))) {
					getBillListPanel().getHeadBillModel().setValueAt(vo.getAttributeValue("insurancetype"), i,
							"insurancetype");
					getBillListPanel().getHeadBillModel().setValueAt(vo.getAttributeValue("gradedistance"), i,
							"gradedistance");
					i++;
				}
			}
			i--;
		}

		if (!ArrayUtils.isEmpty(vos))
			getBillListPanel().getHeadBillModel().setRowState(0, vos.length - 1, BillModel.SELECTED);
	}

	private PsnJobVO[] rebuildVOs(PsnJobVO[] vos, String laoJiJu, String tuiJiJu, String jianJiJu) throws BusinessException {
		List<PsnJobVO> psnjobs = new ArrayList<PsnJobVO>();
		List<String> pk_psndocs = new ArrayList<String>();
		IPsndocwadocLabourService wadocQs = NCLocator.getInstance().lookup(IPsndocwadocLabourService.class);
		for(PsnJobVO vo : vos){
			pk_psndocs.add(vo.getPk_psndoc());
		}
		Map<String,String[]> QuitMap = wadocQs.queryLabour(pk_psndocs, laoJiJu, tuiJiJu, jianJiJu);
		
		if (vos != null) {
			for (PsnJobVO vo : vos) {
				vo.setAttributeValue("insurancetype", "勞保");
				vo.setAttributeValue("gradedistance", new UFDouble(QuitMap.get(vo.getPk_psndoc()) == null ? "" : QuitMap.get(vo.getPk_psndoc())[0]));
				psnjobs.add(vo);

				PsnJobVO rtvo = (PsnJobVO) vo.clone();
				vo.setAttributeValue("insurancetype", "勞退");
				vo.setAttributeValue("gradedistance", new UFDouble(QuitMap.get(vo.getPk_psndoc()) == null ? "" : QuitMap.get(vo.getPk_psndoc())[1]));
				psnjobs.add(rtvo);

				PsnJobVO hlvo = (PsnJobVO) vo.clone();
				vo.setAttributeValue("insurancetype", "健保");
				vo.setAttributeValue("gradedistance", new UFDouble(QuitMap.get(vo.getPk_psndoc()) == null ? "" : QuitMap.get(vo.getPk_psndoc())[2]));
				psnjobs.add(hlvo);
			}
		}
		WorkbenchEnvironment.getInstance().removeClientCache("QuitMap");
		return psnjobs.toArray(new PsnJobVO[0]);
	}
	
	public Map<String,Set<InsuranceTypeEnum>> getSelPsndocsAndInsuranceType(){
		PsnJobVO[] selVOs = BillPanelUtils.getMultiSelectedData(getBillListPanel().getHeadBillModel(), PsnJobVO.class);
		if(ArrayUtils.isEmpty(selVOs))
			return null;
		Map<String,Set<InsuranceTypeEnum>> result = new HashMap<>();
		for(int i=0;i<selVOs.length;i++){
			String pk_psndoc =selVOs[i].getPk_psndoc();
			if(null == pk_psndoc){
				continue;
			}
			if(!result.containsKey(pk_psndoc)){
				result.put(pk_psndoc, new HashSet<InsuranceTypeEnum>());
			}
			if(selVOs[i].getAttributeValue("insurancetype") != null 
					&& selVOs[i].getAttributeValue("insurancetype").equals("勞保")){
				result.get(pk_psndoc).add(InsuranceTypeEnum.Labour);
			}else if (selVOs[i].getAttributeValue("insurancetype") != null 
					&& selVOs[i].getAttributeValue("insurancetype").equals("勞退")){
				result.get(pk_psndoc).add(InsuranceTypeEnum.LabourRetreat);
			}else if (selVOs[i].getAttributeValue("insurancetype") != null 
					&& selVOs[i].getAttributeValue("insurancetype").equals("健保")){
				result.get(pk_psndoc).add(InsuranceTypeEnum.Health);
			}
		}
		return result;
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
			BillTempletVO btv = PsnTempletQuitUtils.createSeniorItems(IBillItem.HEAD);
			BillListData listData = new BillListData(btv);
			billListPanel.setListData(listData);
			billListPanel.setMultiSelect(true);
		}
		return billListPanel;
	}

}
