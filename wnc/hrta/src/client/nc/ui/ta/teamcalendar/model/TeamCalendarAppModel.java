package nc.ui.ta.teamcalendar.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.ITeamCalendarManageMaintain;
import nc.ui.pub.bill.BillSortListener;
import nc.ui.pub.bill.IBillRelaSortListener;
import nc.ui.ta.calendar.pub.CalendarAppEventConst;
import nc.ui.ta.pub.model.IDoCancelAppModel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.RowOperationInfo;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;
import nc.vo.ta.teamcalendar.TeamInfoCalendarVO;

import org.apache.commons.lang.ArrayUtils;

public class TeamCalendarAppModel extends AbstractUIAppModel implements BillSortListener,IBillRelaSortListener,IDoCancelAppModel{
	
	//记录行号的map，key是pk_psndoc，value是行号
	Map<String, Integer> indexMap = new HashMap<String, Integer>();
	//在排序时调用的list，每次排序后，此list中的数据都是符合顺序的，见getRelaSortObject()方法
	List<TeamInfoCalendarVO> sortList = new ArrayList<TeamInfoCalendarVO>();
	
	private TeamInfoCalendarVO[] data;
	private int selectedRow;
	
	private UFLiteralDate beginDate;
	private UFLiteralDate endDate;
	private UFLiteralDate selectedDate;
	private PsnJobCalendarVO[] psnCalendarVOs;
	
	
	public void setBeginEndDate(UFLiteralDate beginDate, UFLiteralDate endDate){
		this.beginDate=beginDate;
		this.endDate=endDate;
		fireEvent(new AppEvent(CalendarAppEventConst.DATE_CHANGED, this, null));
	}
	
	public UFLiteralDate getBeginDate() {
		return beginDate;
	}
	public UFLiteralDate getEndDate() {
		return endDate;
	}
	public UFLiteralDate getSelectedDate() {
		return selectedDate;
	}
	public void setSelectedDate(UFLiteralDate selectedDate) {
		this.selectedDate = selectedDate;
	}
	public PsnJobCalendarVO[] getPsnCalendarVOs() {
		return psnCalendarVOs;
	}
	public void setPsnCalendarVOs(PsnJobCalendarVO[] psnCalendarVOs) {
		this.psnCalendarVOs = psnCalendarVOs;
		fireEvent(new AppEvent(CalendarAppEventConst.BODY_CHANGED, this, null));
	}
	
	public TeamInfoCalendarVO[] getData(){
		return data;
	}

	public int getSelectedRow() {
		return selectedRow;
	}
	
	public void setSelectedRow(int selectedRow) {
		if(this.selectedRow == selectedRow)
			return;
		this.selectedRow = selectedRow;
		fireEvent(new AppEvent(AppEventConst.SELECTION_CHANGED,this,null));
	}
	
	public void setSelectedVO(TeamInfoCalendarVO selectedVO){
		if(selectedVO!=null){
			selectedRow = indexMap.get(selectedVO.getCteamid());
		}
		fireEvent(new AppEvent(AppEventConst.SELECTION_CHANGED,this,null));
	}
	
	public TeamInfoCalendarVO getSelectedVO(){
		if(selectedRow<0||ArrayUtils.isEmpty(data))
			return null;
		if(selectedRow>=data.length)
			return null;
		return data[selectedRow];
	}
	
	@Override
	public Object getSelectedData() {
		return getSelectedVO();
	}
	
	public TeamInfoCalendarVO[] save(TeamInfoCalendarVO[] saveData) throws BusinessException{
		if(ArrayUtils.isEmpty(saveData))
			return null;
		saveData = NCLocator.getInstance().lookup(ITeamCalendarManageMaintain.class).saveNODateype(getContext().getPk_org(), saveData,true);
		directlyUpdate(saveData);
		return saveData;
	}
	
	/**
	 * 更新model的内容，不连接后台
	 * @param updateData
	 */
	public void directlyUpdate(TeamInfoCalendarVO[] updateData){
		if(ArrayUtils.isEmpty(updateData))
			return;
		List<Integer> updateIndexList = new ArrayList<Integer>();
		//将data中的数据update
		for(int i=0;i<updateData.length;i++){
			int index = indexMap.get(updateData[i].getCteamid());
			this.data[index]= updateData[i];
			updateIndexList.add(index);
		}
		RowOperationInfo rowOpInfo = new RowOperationInfo(updateIndexList, updateData);
		fireEvent(new AppEvent(AppEventConst.DATA_UPDATED, this, rowOpInfo));
	}

	@Override
	public void afterSort(String key) {
		if(ArrayUtils.isEmpty(data))
			return;
		//将data设置为与sortList中的顺序一致，并设置新的selectedrow
		//排序后的index map，key是人员主键pk_psndoc，value是序号
		Map<String, Integer> newIndexMap = new HashMap<String, Integer>();
		for(int i=0;i<sortList.size();i++){
			newIndexMap.put(sortList.get(i).getCteamid(), i);
		}
		//设置新的选中行
		if(selectedRow>=0)
			selectedRow = newIndexMap.get(data[selectedRow].getCteamid());
		//将data中的顺序设置得与sortlist中一致
		for(int i=0;i<data.length;i++){
			data[i]=sortList.get(i);
		}
		//更新indexMap
		indexMap.clear();
		indexMap.putAll(newIndexMap);
		fireEvent(new AppEvent(CalendarAppEventConst.MODEL_SORTED, this, null));
	}
	
	/**
	 * 更新子表数据
	 */
	public void refreshPsnCalendars(){
		TeamInfoCalendarVO vo = getSelectedVO();
		PsnJobCalendarVO[] psnVOs = null;
		if(vo!=null){
			try {
				psnVOs = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryByTeam(getContext().getPk_org(), vo.getCteamid(), beginDate, endDate);
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
			}
		}
		setPsnCalendarVOs(psnVOs);
	}

	@Override
	public void initModel(Object data) {
		selectedRow = -1;
		this.data=(TeamInfoCalendarVO[])data;
		setSelectedDate(null);
		fireEvent(new AppEvent(AppEventConst.MODEL_INITIALIZED,this,null));
		int count = this.data==null?0:this.data.length;
		setSelectedRow(count>0?0:-1);
		indexMap.clear();
		if(!ArrayUtils.isEmpty(this.data)){
			for(int i=0;i<this.data.length;i++){
				indexMap.put(this.data[i].getCteamid(), i);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getRelaSortObject() {
		sortList.clear();
		if(ArrayUtils.isEmpty(data))
			return sortList;
		sortList.addAll(Arrays.asList(data));
		return sortList;
	}

	@Override
	public void doCancel() {
		fireEvent(new AppEvent(CalendarAppEventConst.EDIT_CANCELED, this, null));
		if(!ArrayUtils.isEmpty(data)){
			for(TeamInfoCalendarVO vo:data)
				vo.getModifiedCalendarMap().clear();
		}
		setUiState(UIState.NOT_EDIT);
	}
	
	public void buChanged(){
		fireEvent(new AppEvent(CalendarAppEventConst.BU_CHANGED, this, null));
	}
}
