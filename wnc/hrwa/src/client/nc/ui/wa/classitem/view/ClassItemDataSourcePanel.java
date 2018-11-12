package nc.ui.wa.classitem.view;

import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import nc.bs.logging.Logger;
import nc.ui.hr.itemsource.view.BillItemWraper;
import nc.ui.hr.itemsource.view.DataSourceItem;
import nc.ui.hr.itemsource.view.FormulaDataManager;
import nc.ui.hr.itemsource.view.NullDataManager;
import nc.ui.hr.itemsource.view.UsableController;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.wa.item.util.ItemUtils;
import nc.ui.wa.item.view.custom.CollectOnly;
import nc.ui.wa.item.view.custom.ItemDataSourcePanel;
import nc.ui.wa.item.view.custom.WaRefPaneDataManager;
import nc.ui.wa.ref.OtherSourceRefModel;
import nc.vo.hr.func.HrFormula;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.FromEnumVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.othersource.OtherSourceVO;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 
 * @author: wh 
 * @date: 2009-12-14 ����02:48:44
 * @since: eHR V6.0
 * @�߲���: 
 * @�߲�����: 
 * @�޸���: 
 * @�޸�����: 
 */
public class ClassItemDataSourcePanel extends ItemDataSourcePanel {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1991913402304387642L;
	private UIRefPane waFormulaRefPane;
	
	
	/**
	 * @author wh on 2009-12-14 
	 * @param keyOfBillItem
	 * @param model
	 */
	public ClassItemDataSourcePanel(String keyOfBillItem, AbstractAppModel model,HrBillFormEditor parentEditor) {
		super(keyOfBillItem, model,parentEditor);
	}
	
	private Component createComponetView(){
		OtherDataSourceEditor otherDataSourceEditor     =  new OtherDataSourceEditor(this);
		otherDataSourceEditor.setModel(getModel());
		
		HrFormula f = new HrFormula();
		
		WaClassItemVO waClassItemVO = (WaClassItemVO)getModel().getSelectedData();
		
		if(waClassItemVO!=null){
			//f.setBusinessLang(waClassItemVO.getVformulastr());
			f.setItemKey(waClassItemVO.getItemkey());
			f.setReturnType(waClassItemVO.getIitemtype());
			f.setScirptLang(waClassItemVO.getVformula());
			
			//waClassItemVO.getFromEnumVO() ��Ĭ��ֵ��2  �ֹ����롣��Ҫ���е�����
			f.setIfromflag(waClassItemVO.getIfromflag());
			if(waClassItemVO.getFromEnumVO().equals(FromEnumVO.USER_INPUT)){
				f.setIfromflag(FromEnumVO.WAORTHER.value());
			}
			otherDataSourceEditor.setFormula(f);
		}
		
		return otherDataSourceEditor;
	}

	
	@Override
	 protected UIRefPane getReferPanlFormula() {
		
		  try {
		   if (waFormulaRefPane == null) {
			   waFormulaRefPane = new WaClassitemFormulaRefPane(this.getModel(),getParentEditor());
			   waFormulaRefPane.setAutoCheck(false);
		   }
		  } catch (Exception ex) {
			  Logger.error(ex.getMessage(),ex);
		  }
		  return waFormulaRefPane;
		 }
	
	
	@Override
	protected boolean needClearData(){
		//�Ƿ�����״̬
		if(getModel().getUiState()==UIState.ADD){
			Object o = getParentEditor().getHeadItemValue(WaItemVO.PK_WA_ITEM);
			
			if(o==null){
				return true;
			}
		}
		
		return false;
	}


	@Override
	protected List<DataSourceItem> createDataSourceItems() {
		
		List<DataSourceItem> items = super.createDataSourceItems();
		
		/**
		 * ����ϵͳ��Ĭ���ǡ�����ϵͳ��н�ʣ���
		 */
		items.add(new DataSourceItem(FromEnumVO.WAORTHER.getName(),FromEnumVO.WAORTHER.value(),createComponetView(),
				new OtherSysFormulaDataManager(), new OtherSourceDataWraper(),new UsableController()));
		
		/**
		 * ���Ŵ������� 
		 * ע: �û�����ѡ���������Դ. ֻ�и�������н�ʷ�����Ŀ������Դ��"���Ŵ������� "
		 */
		items.add(new DataSourceItem(FromEnumVO.TIMESCOLLECT.getName(),FromEnumVO.TIMESCOLLECT.value(),new UIPanel(),
				new NullDataManager(),new BillItemWraper(),new CollectOnly((WaLoginContext)this.getModel().getContext()) ));
		
		items.add(new DataSourceItem(FromEnumVO.OTHERSOURCE.getName(),FromEnumVO.OTHERSOURCE.value(),getOtherSource(),
				new WaRefPaneDataManager(OtherSourceVO.PK_OTHERDATASOURCE),new BillItemWraper(),new UsableController() ));
		
	    
		return items;		
	}
	
	private UIRefPane otherSource;
	
	public UIRefPane getOtherSource() {
		if (otherSource == null) {
			otherSource = new UIRefPane();
			otherSource.setPreferredSize(new Dimension(200, 22));
			OtherSourceRefModel refModel = new OtherSourceRefModel();
			refModel.setContext(getModel().getContext());
			otherSource.setRefModel(refModel);
			otherSource.setReturnCode(false);
		}
		return otherSource;
	}
	
	
	   protected 	FormulaDataManager  getFormulaDataManager(){
		  return new ClassItemFormulaDataManager();
			
		}
	

	protected WaItemVO getNewItem(){
		return  new WaClassItemVO();	
	}
	

	
	@Override
	public void setContentEnabled(boolean enabled) {
		
		super.setContentEnabled(enabled);
		
		
		if(enabled&&UIState.EDIT == getModel().getUiState()){
			WaClassItemVO item = (WaClassItemVO) getModel().getSelectedData();
			if(ItemUtils.isSystemItemKey(item.getItemkey())){
				getDataSourceType().setEnabled(false);
			}
		}
	}
	
	
	public void collectData(WaItemVO itemVO){
		if(itemVO == null){
			itemVO = new WaClassItemVO();
		}	
		
		getCurrentDataSourceItem().getData(itemVO);
		
	}
	
	@Override
	public Object getValue() {
		WaClassItemVO vo = (WaClassItemVO) getCurrentDataSourceItem().getData(new WaClassItemVO());
		
		return vo.getFromEnumVO();
	}
	
	public UIComboBox getDataSourceType() {
		return super.getDataSourceType();
	}
}
