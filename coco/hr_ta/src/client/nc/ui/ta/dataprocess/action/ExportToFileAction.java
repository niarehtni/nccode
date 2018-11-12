package nc.ui.ta.dataprocess.action;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.format.NCFormater;
import nc.ui.ta.dailydata.model.DailyDataAppModel;
import nc.ui.ta.dailydata.view.DailyDataPanel;
import nc.ui.ta.dailydata.view.PsnPanel;
import nc.ui.ta.dataprocess.view.DateTimeDataPanel;
import nc.ui.ta.dataprocess.view.PsnTimeDataPanel;
import nc.ui.ta.pub.ExportTBM;
import nc.ui.ta.pub.action.TAExportAction;
import nc.ui.uif2.UIState;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.ta.dailydata.IDailyData;
import nc.vo.ta.dataprocess.TimeDataVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

public class ExportToFileAction extends TAExportAction{

	/**
	 *
	 */
	private static final long serialVersionUID = -7298866131644590206L;
//	private ExportToFileDialog dialog;
	private DailyDataAppModel modelforpsnview;//����Ա�鿴ʱ��model
	private DailyDataAppModel modelfordateview;//�����ڲ鿴ʱ��model
	private PsnTimeDataPanel psnDailyDataPanel;
	private DateTimeDataPanel dateDailyDataPanel;
	private PsnPanel psnPanel;
	
	@Override
	public void doExport() throws Exception {
		//�����ݱ�������ͽ������ݴ����̨������ǰ���Ա��ѯ��ô��Ҫ����Ա��������ͱ�ѡ�е���Ա��¼���ݴ����̨
		int colnums = this.getDataPanel().getBillListPanel().getHeadTable().getColumnCount();
		int  rows = this.getDataPanel().getBillListPanel().getHeadTable().getRowCount();
		List<String> colNameList = new ArrayList<String>();
		List<String> psnValueList = new ArrayList<String>();
		if(this.getPsnPanel() != null&&getPsnPanel().isComponentVisible()){
			int psncolnums = this.getPsnPanel().getPsnListPanel().getPsnBillListPanel().getHeadTable().getColumnCount();
			int selectedpsnrow = this.getPsnPanel().getPsnListPanel().getPsnBillListPanel().getHeadTable().getSelectedRow();
			for(int i=0;i<psncolnums;i++){
				colNameList.add(this.getPsnPanel().getPsnListPanel().getPsnBillListPanel().getHeadTable().getColumnName(i));
				String v = (this.getPsnPanel().getPsnListPanel().getPsnBillListPanel().getHeadTable().getValueAt(selectedpsnrow, i)) == null?
						"":(this.getPsnPanel().getPsnListPanel().getPsnBillListPanel().getHeadTable().getValueAt(selectedpsnrow, i)).toString().trim();
				psnValueList.add(v);
			}
		}
		List<List<String>> dataValueList = new ArrayList<List<String>>();
		for(int i=0;i<colnums;i++){
			colNameList.add(this.getDataPanel().getBillListPanel().getHeadTable().getColumnName(i));
		}
		IDailyData[] dailyData = ((DailyDataAppModel)getDataPanel().getModel()).getDailyData();
		for(int i=0;i<rows;i++){
			List<String> temp = new ArrayList<String>();
			for(int j=0;j<colnums;j++){
				String key = getDataPanel().getBillListPanel().getParentListPanel().getBodyKeyByCol(j);
				if(TimeDataVO.MIDWAYOUTCOUNT.equals(key) ||
            			TimeDataVO.MIDWAYOUTTIME.equals(key)){
					TimeDataVO vo = (TimeDataVO) dailyData[i];
					//modify by tangcht on 2015.7.29
            		//���� Ismidoutabnormal ���ж� 
            		if(vo.getIsmidoutabnormal() != null && vo.getIsmidoutabnormal().booleanValue()){
            			temp.add(ResHelper.getString("6017dataprocess","06017dataprocess0026")/*@res "�쳣"*/); //��;����쳣��ʾ�쳣
            			continue;
            		}
				}
				if(TimeDataVO.PLACEABNORMAL.equals(key)){
					String value = this.getDataPanel().getBillListPanel().getHeadTable().getValueAt(i, j).toString();
					if("0".equals(value)){
						temp.add(ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0095")/*@res"��"*/);
		    		}else{
		    			temp.add(ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0094")
						/*@res"��"*/);
		    		}
					continue;
				}
				String v = this.getDataPanel().getBillListPanel().getHeadTable().getValueAt(i, j) == null?
						"":this.getDataPanel().getBillListPanel().getHeadTable().getValueAt(i, j).toString();
				//ϵͳ����Ĭ�ϴ���Ǳ���ʱ������������ת���ɷ�����ʱ��
				if(j>1&&j<colnums-1&&!StringUtils.isEmpty(v)){
					v=NCFormater.formatDateTime(new UFDateTime(v,TimeZone.getTimeZone("GMT+8:00"))).getValue();
				}
				temp.add(v);
			}
			dataValueList.add(temp);
		}
		exportToFile(strFileName,colNameList, psnValueList, dataValueList);
	}
	
	/**
	 * ִ�е������ļ�
	 * @param file
	 * @param cols
	 * @param psndataList
	 * @param timedataList
	 * @throws BusinessException
	 */
	public void exportToFile(String file,List<String> cols,List<String> psndataList,List<List<String>> timedataList) throws BusinessException{
		int rownums = timedataList.size();
		int colnums = 0;
		List<List<String>> mergedDataList = new ArrayList<List<String>>();
		if(CollectionUtils.isEmpty(psndataList)){
			colnums = timedataList.get(0).size();
			mergedDataList = timedataList;
		}else{
			colnums = psndataList.size() + timedataList.get(0).size();
			for(List<String> t:timedataList){
				List<String> tempList = new ArrayList<String>();
				tempList.addAll(psndataList);
				tempList.addAll(t);
				mergedDataList.add(tempList);
			}
		}
		String[][] datas = new String[rownums][colnums];
		for(int i=0;i<rownums;i++){
			for(int j=0;j<colnums;j++){
				datas[i][j] = (mergedDataList.get(i)).get(j);
			}
		}

		try {
			new ExportTBM().exportChkAttdDataExcelFile(file, cols.toArray(new String[0]), datas);
		} catch (Exception e) {
			Logger.error(e.getMessage(),e);
			throw new BusinessException(e.getMessage(),e);
		}
	}

//	@Override
//	public void doAction(ActionEvent e) throws Exception {
//		TimeDataVO[] objs = (TimeDataVO[])getModel().getDailyData();
//		if(ArrayUtils.isEmpty(objs))
//			throw new BusinessException(ResHelper.getString("6017dataprocess","06017dataprocess0001")/*@res "��ǰû����Ҫ����������!"*/);
//		getDialog().setDatas(objs);
//		if(UIDialog.ID_OK!=getDialog().showModal()) {
//			putValue(MESSAGE_AFTER_ACTION, IShowMsgConstant.getCancelInfo());
//			return;
//		}
//		putValue(MESSAGE_AFTER_ACTION, ResHelper.getString("6001uif2", "06001uif20010", getBtnName())/* @res "{0}�ɹ���" */);
//	}

//	private ExportToFileDialog getDialog(){
//		if(dialog == null){
//			dialog = new ExportToFileDialog(getModel().getContext().getEntranceUI());
//			dialog.initUI();
//		}
//		if(this.getPsnDailyDataPanel() != null && this.getPsnDailyDataPanel().isShowing()){
//			dialog.setPsnPanel(this.getPsnPanel());
//			dialog.setDataPanel(this.getPsnDailyDataPanel());
//		}else if(this.getDateDailyDataPanel() != null && this.getDateDailyDataPanel().isShowing()){
//			dialog.setPsnPanel(null);
//			dialog.setDataPanel(this.getDateDailyDataPanel());
//		}
//
//		return dialog;
//	}
	
	public DailyDataPanel getDataPanel() {
		if(this.getPsnDailyDataPanel() != null && this.getPsnDailyDataPanel().isShowing()){
			return getPsnDailyDataPanel();
		}
		return getDateDailyDataPanel();
	}

	public DailyDataAppModel getModelforpsnview() {
		return modelforpsnview;
	}

	public void setModelforpsnview(DailyDataAppModel modelforpsnview) {
		this.modelforpsnview = modelforpsnview;
		this.modelforpsnview.addAppEventListener(this);
	}

	public DailyDataAppModel getModelfordateview() {
		return modelfordateview;
	}

	public void setModelfordateview(DailyDataAppModel modelfordateview) {
		this.modelfordateview = modelfordateview;
		this.modelfordateview.addAppEventListener(this);
	}

	public PsnTimeDataPanel getPsnDailyDataPanel() {
		return psnDailyDataPanel;
	}

	public void setPsnDailyDataPanel(PsnTimeDataPanel psnDailyDataPanel) {
		this.psnDailyDataPanel = psnDailyDataPanel;
	}

	public DateTimeDataPanel getDateDailyDataPanel() {
		return dateDailyDataPanel;
	}

	public void setDateDailyDataPanel(DateTimeDataPanel dateDailyDataPanel) {
		this.dateDailyDataPanel = dateDailyDataPanel;
	}

	public PsnPanel getPsnPanel() {
		return psnPanel;
	}

	public void setPsnPanel(PsnPanel psnPanel) {
		this.psnPanel = psnPanel;
	}

	@Override
	public DailyDataAppModel getModel()
    {
		if(this.getDateDailyDataPanel() != null && this.getDateDailyDataPanel().isShowing()){
			return this.getModelfordateview();
		}
		return this.getModelforpsnview();
    }

	@Override
	protected boolean isActionEnable() {
		if(StringUtils.isBlank(getModel().getContext().getPk_org()))
			return false;
		if(ArrayUtils.isEmpty(getModel().getDailyData()))
			return false;
		return getModel().getUiState() == UIState.INIT || getModel().getUiState() == UIState.NOT_EDIT;
	}
}