package nc.ui.ta.psncalendar.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JSplitPane;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.IActionCode;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.ResHelper;
import nc.itf.ta.IPsnCalendarQueryMaintain;
import nc.itf.ta.ITBMPsndocQueryMaintain;
import nc.uap.rbac.core.dataperm.DataPermissionFacade;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.shift.ref.model.ShiftRefModelLeftType;
import nc.ui.calendarX.CalendarObservable;
import nc.ui.calendarX.CalendarXPanel;
import nc.ui.calendarX.itf.ICalendarQuery;
import nc.ui.calendarX.tableCellRenderer.CalendarTableCellRenderer;
import nc.ui.calendarX.util.CalendarVO;
import nc.ui.calendarX.util.CalendarXUtils;
import nc.ui.calendarX.util.Pair;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UISplitPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.ta.calendar.pub.CalendarAppEventConst;
import nc.ui.ta.calendar.pub.CalendarColorUtils;
import nc.ui.ta.psncalendar.model.PsnCalendarAppModelDataManager;
import nc.ui.ta.pub.standardpsntemplet.BasicPsnListPanel;
import nc.ui.ta.pub.standardpsntemplet.PsnListPanel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.RowOperationInfo;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;
import nc.vo.ta.psndoc.TBMPsndocVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * �������鿴��panel���������Ա�б����ұ�������
 * @author zengcheng
 *
 */
public class CalendarPanel extends AbstractPanel implements ICalendarQuery, BillEditListener2{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3097281178378346088L;
	
	private UIRefPane shiftRefPane;
	
	private PsnListPanel psnListPanel = null; 
	private CalendarXPanel calendarXPanel;
	private CalendarObservable calendarObservable=null;
	private boolean needRemoteQueryWhenShowMeUp = false;//�л����˽���ʱ���Ƿ���Ҫ����̨��ѯ

	public CalendarPanel() {
		super();
	}

	public void initUI(){
		addTabbedPaneAwareComponentListener(tabbedAwareListener);
		UISplitPane splitPane = new UISplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(getPsnListPanel());
		splitPane.setRightComponent(getCalendarXPanel());
		splitPane.setResizeWeight(0.18);
		setLayout(new BorderLayout());
		//add(splitPane, BorderLayout.CENTER);
		UIScrollPane scrollPane= new UIScrollPane();
		scrollPane.setViewportView(splitPane);
		add(scrollPane,BorderLayout.CENTER);
		getPsnBillListPanel().getParentListPanel().addEditListener(this);
	}


	public PsnListPanel getPsnListPanel() {
		if(psnListPanel==null){
			psnListPanel = new BasicPsnListPanel();
			psnListPanel.init();
			psnListPanel.getPsnBillListPanel().getHeadTable().getSelectionModel().addListSelectionListener(this);
			psnListPanel.getPsnBillListPanel().getHeadBillModel().addSortRelaObjectListener(getPsnCalendarAppModel());
			psnListPanel.getPsnBillListPanel().getHeadBillModel().addSortListener(getPsnCalendarAppModel());
			//�˽������Ա�б���ֹ������Ϊ�׽���������������������涼��������Ļ������
//			psnListPanel.getPsnBillListPanel().getHeadTable().setSortEnabled(false);
			//���ñ�ͷ�ĸ߶ȣ��Ա������������ͷ�߶ȵ�һ��
			psnListPanel.getPsnBillListPanel().getHeadTable().getTableHeader().setPreferredSize(new Dimension(500,getTableHeaderHeight()));
			psnListPanel.getPsnBillListPanel().getHeadTable().addMouseListener(new MouseAdapter() {//˫���л�����
				@Override
				public void mouseClicked(MouseEvent e) {
					if(getModel().getUiState()==UIState.EDIT)
						return;
//					Debug.error("click count:"+e.getClickCount());
					if(e.getClickCount()!=2)
						return;
					getModel().fireEvent(new AppEvent(CalendarAppEventConst.SWITCH_TO_GRID, getModel(), null));
				}});
		}
		return psnListPanel;
	}

	@Override
	protected BillListPanel getPsnBillListPanel() {
		return getPsnListPanel().getPsnBillListPanel();
	}

//	@Override
//	protected void processRefModel(String pk_org) {
//		getBclbRefPane().getRefModel().setPk_org(pk_org);
//	}

	@Override
	public void afterEdit(BillEditEvent e) {
		UFLiteralDate date = getCalendarXPanel().getSelDate();
		String pk_shift = getShiftRefPane().getRefPK();
		((PsnJobCalendarVO)getModel().getSelectedData()).getModifiedCalendarMap().put(date.toString(), pk_shift);
		AbstractRefModel model = getShiftRefPane().getRefModel();
		//�򿪲���֮��ͣ�ð����Ҫ��ʾ
		model.setDisabledDataShow(true);
		
	}


	@Override
	public void bodyRowChange(BillEditEvent e) {
		
		
	}

	@Override
	public void modifyListChange(List<Pair> valueChangeList,Pair curValueChangePair){
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, CalendarVO> queryData(UFLiteralDate firstDay,
			UFLiteralDate lastDay) {
		PsnJobCalendarVO[] data = getPsnCalendarAppModel().getData();
		int selectRow = getPsnCalendarAppModel().getSelectedRow();
		if(selectRow<0||ArrayUtils.isEmpty(data))
			return CalendarXUtils.createDefaultCalendarVOMap(firstDay, lastDay);
		//���Ч�ʵľٴ룺����������浱ǰ��û����ʾ����������̨��ѯ���������ݼ��ɣ�������grid�����л���ԱҲ�ᵼ��Զ������
		//��������Ҫһ�����ȴ�ʩ�����û��л������������ʱ����Ҫ����̨��ѯ
		if(!isComponentVisible()){
			needRemoteQueryWhenShowMeUp=true;
			return CalendarXUtils.createDefaultCalendarVOMap(firstDay, lastDay);
		}
		//�����ڷ�Χ��û�г���model�������ݵ����ڷ�Χ�����û�г�������ֱ�Ӵ�model����ȡ������Ҫ����̨ȡ����
		PsnJobCalendarVO vo = null;
		if(!firstDay.before(getPsnCalendarAppModel().getBeginDate())&&!lastDay.after(getPsnCalendarAppModel().getEndDate()))
			vo=data[selectRow];
		else
			try {
				PsnJobCalendarVO[] vos = NCLocator.getInstance().lookup(IPsnCalendarQueryMaintain.class).
				queryCalendarVOByPsndocs(getModel().getContext().getPk_org(), new String[]{data[selectRow].getPk_psndoc()}, firstDay, lastDay);
				if(vos!=null&&vos.length>0){
					vo = vos[0];
					data[selectRow].getCalendarMap().putAll(vo.getCalendarMap());
					data[selectRow].getPsndocEffectiveDateSet().addAll(vo.getPsndocEffectiveDateSet());
					data[selectRow].getOrgMap().putAll(vo.getOrgMap());
				}
				else{//������ڷ�Χ������Ч�Ŀ��ڵ�����¼���򷵻ص�����Ϊ�գ���ʱ��Ҫ����newһ��
					vo = new PsnJobCalendarVO();
					vo.setPk_psndoc(data[selectRow].getPk_psndoc());
					vo.setPk_psnjob(data[selectRow].getPk_psnjob());
				}
			} catch (BusinessException e) {
				Debug.error(e.getMessage(), e);
				throw new BusinessRuntimeException(e.getMessage(), e);
			}
		return createDataByPsnjobCalendarVO(firstDay, lastDay, vo);
	}
	
	private Map<String, CalendarVO> createDataByPsnjobCalendarVO(UFLiteralDate firstDay,
			UFLiteralDate lastDay,PsnJobCalendarVO jobCalendarVO){
		UFLiteralDate[] allDates = CommonUtils.createDateArray(firstDay, lastDay);
		Map<String, CalendarVO> retMap = new HashMap<String, CalendarVO>();
		for(UFLiteralDate date:allDates){
			CalendarVO vo = new CalendarVO();
			retMap.put(date.toString(), vo);
			Color color = CalendarColorUtils.getDateColor(date.toString(), jobCalendarVO);//��ɫ
			if(color!=null)
				vo.setColor(color);
			if(!jobCalendarVO.isEffectiveDate(date.toString())){
				vo.setModifiable(false);
				continue;
			}
			vo.setModifiable(true);
			//�����������
			vo.setObj(getShiftName(date.toString(), jobCalendarVO));
		}
		return retMap;
	}

	private String getShiftName(String date,PsnJobCalendarVO jobCalendarVO){
		String pk_shift = jobCalendarVO.getCalendarMap().get(date);
		if(StringUtils.isEmpty(pk_shift))
			return null;
		getShiftRefPane().setPK(pk_shift);
		return getShiftRefPane().getRefName();
	}
	
	public CalendarXPanel getCalendarXPanel() {
		if(calendarXPanel==null){
			calendarXPanel = new CalendarXPanel(this, new CalendarTableCellRenderer(true), getShiftRefPane(), "");
			UIPanel descPanel =createDescriptionPanel();
			calendarXPanel.getCalendarPanel().getBillScrollPaneResult().addEditListener2(this);
			calendarXPanel.getCalendarPanel().getBillScrollPaneResult().addEditListener(this);
			calendarXPanel.getCalendarPanel().getBillScrollPaneResult().getTable().addMouseListener(mouseListener);
			calendarXPanel.getTodayUIButton().addMouseListener(mouseListener);
			calendarXPanel.setDescUIPanelExplain(descPanel);
			calendarXPanel.init();
			//ȥ���������ܵ�enter���к�����Ҽ�����
			BillPanelUtils.disabledRightMenuAndAutoAddLine(calendarXPanel.getCalendarPanel().getBillScrollPaneResult());
		}
		return calendarXPanel;
	}

	public UIRefPane getShiftRefPane() {
		if(shiftRefPane==null){
			shiftRefPane = new UIRefPane();
//			AbstractRefModel model = new BclbRefModel();
			AbstractRefModel model = new ShiftRefModelLeftType();
			//ͣ��������ʾ���ش򿪣���Ϊ���򿪵Ļ������״̬��ͣ�ð�εĸ���ʾΪ��
			model.setDisabledDataShow(true);
			shiftRefPane.setRefModel(model);
		}
		return shiftRefPane;
	}

	@Override
	protected void onEdit() {
		getCalendarXPanel().setCurStatus(1);
		getPsnBillListPanel().getHeadTable().setEnabled(false);
		
	}

	@Override
	protected void onNotEdit() {
		getCalendarXPanel().setCurStatus(0);
		getPsnBillListPanel().getHeadTable().setEnabled(true);
	}

	protected CalendarObservable getCalendarObservable() {
		if(calendarObservable==null){
			calendarObservable = new CalendarObservable();
			calendarObservable.addObserver(getCalendarXPanel());
		}
		return calendarObservable;
	}

	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if(AppEventConst.DATA_UPDATED.equals(event.getType())){//����󣬴��¼��ᱻ����
			RowOperationInfo info = (RowOperationInfo)event.getContextObject();
			if(info==null)
				return;
			int[] updateindices = info.getRowIndexes();
			if(org.apache.commons.lang.ArrayUtils.isEmpty(updateindices))
				return;
			PsnJobCalendarVO[] updateVOs = (PsnJobCalendarVO[])info.getRowDatas();
			BillListData listData = getPsnBillListPanel().getBillListData();
			for(int i=0;i<updateindices.length;i++){
				int index = updateindices[i];
				listData.setHeaderValueRowObjectByMetaData(updateVOs[i], index);
			}
			getPsnBillListPanel().getHeadBillModel().updateValue();
			if(isComponentVisible())//refresh�����ᵼ��Զ�����ӣ���˴�tab����ʾ��ʱ��refresh����Ҫ���Ǻţ�tab��ʾ��ʱ��Ҫ��refresh
				refreshCalendarPanel();
			else
				needRemoteQueryWhenShowMeUp=true;
			return;
		}
		if(AppEventConst.SELECTION_CHANGED.equals(event.getType())){
			if(isComponentVisible())//refresh�����ᵼ��Զ�����ӣ���˴�tab����ʾ��ʱ��refresh����Ҫ���Ǻţ�tab��ʾ��ʱ��Ҫ��refresh
				refreshCalendarPanel();
			else
				needRemoteQueryWhenShowMeUp=true;
		}
		if(CalendarAppEventConst.DATE_CHANGED.equals(event.getType())){
			UFLiteralDate beginDate = getPsnCalendarAppModel().getBeginDate();
			UFLiteralDate endDate = getPsnCalendarAppModel().getEndDate();
			if(beginDate==null||endDate==null)
				return;
			getCalendarXPanel().setNewdate(beginDate, true);
			return;
		}
		if(CalendarAppEventConst.EDIT_CANCELED.equals(event.getType())){
			if(!isComponentVisible())//������Ϊ��ǰ�༭����ʱ������Ҫ����ȡ���¼�
				return;
			if(org.apache.commons.lang.ArrayUtils.isEmpty(getPsnCalendarAppModel().getData()))
				return;
			int selRow = getPsnCalendarAppModel().getSelectedRow();
			if(selRow<0)
				return;
			PsnJobCalendarVO vo = getPsnCalendarAppModel().getSelectedVO();
			if(vo.getModifiedCalendarMap().isEmpty())
				return;
			//���û��޸Ĺ������ڻָ�
			String[] modifiedDates = vo.getModifiedCalendarMap().keySet().toArray(new String[0]);
			for(String date:modifiedDates){
				getCalendarXPanel().getCalendarPanel().paintOneGrid(date, getShiftName(date, vo));
			}
		}
		if(CalendarAppEventConst.SWITCH_TO_CALENDAR.equals(event.getType())){
			if(getModel().getUiState()==UIState.EDIT)
				return;
			showMeUp();
		}
	}

	@Override
	public boolean beforeEdit(BillEditEvent e) {
		////�����ڲ�ѯ�Ľ����� ����ʾһ�����µĹ������� ����Щ�������ܲ������ڲ�ѯ�����ڣ�
		//��ʱ��PsnJobCalendarVO ����Щ������Pk_org�ǿյģ����Բ���������ķ�����
	
		UFLiteralDate date = getCalendarXPanel().getSelDate();
		//ȡ����һ���ε����������modifiedmap���������һ�죬˵���Ѿ��Ĺ��ˣ���ôȡmodifiedmap����İ��
		//���modifiedmap���治������˵��û�Ĺ�����ôȡԭʼ�İ���е�
		PsnJobCalendarVO vo = (PsnJobCalendarVO)getModel().getSelectedData();
		
		try {//Ȩ��
			TBMPsndocVO tbmpsndocvo = NCLocator.getInstance().lookup(ITBMPsndocQueryMaintain.class).queryByPsndocAndDateTime(vo.getPk_psndoc(), new UFDateTime(date+" 00:00:00"));
			 boolean canEdit = DataPermissionFacade.isUserHasPermissionByMetaDataOperation(getModel().getContext().getPk_loginUser(),
						"60170psndoc", IActionCode.EDIT, getModel().getContext().getPk_group(), tbmpsndocvo);
			 if(!canEdit){
				 ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("6017basedoc","06017basedoc1856")
							/*@res "����Ȩ��ѡ�е�����ִ���޸Ĳ���!"*/, getModel().getContext());
				 return false;
			 }
			
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(),e1);
		}
		
		AbstractRefModel model = getShiftRefPane().getRefModel();
		//�򿪲���ǰ���˵�ͣ�ð��
		model.setDisabledDataShow(false);
		//���ڲ�ͬ����Ա��ְ����֯�п��ܲ�ͬ����֯��ͬ��ʹ�õİ��Ҳ�п��ܲ�ͬ��V61�޸�
		getShiftRefPane().setPk_org(vo.getOrgMap().get(date.toString()));
		if(vo.getModifiedCalendarMap().containsKey(date.toString()))
			getShiftRefPane().setPK(vo.getModifiedCalendarMap().get(date.toString()));
		else
			getShiftRefPane().setPK(vo.getCalendarMap().get(date.toString()));
		return true;
		
		
//		UFLiteralDate date = getCalendarXPanel().getSelDate();
//		//ȡ����һ���ε����������modifiedmap���������һ�죬˵���Ѿ��Ĺ��ˣ���ôȡmodifiedmap����İ��
//		//���modifiedmap���治������˵��û�Ĺ�����ôȡԭʼ�İ���е�
//		PsnJobCalendarVO vo = (PsnJobCalendarVO)getModel().getSelectedData();
//		String pk_psndoc=vo.getPk_psndoc();
//		//���������Լ� ��Ա������ѯ����Ա�Ĺ����Ĺ�������VO
//		 AggPsnCalendar aggPsnCalendar=null;
//		try {
//			aggPsnCalendar = NCLocator.getInstance().lookup(IPsnCalendarQueryMaintain.class).queryByPsnDate(pk_psndoc, date);
//		} catch (BusinessException e1) {
//			Logger.error(e1.getMessage(), e1);
//			e1.printStackTrace();
//		}
//		 String pk_org=aggPsnCalendar.getPk_org();
//		AbstractRefModel model = getShiftRefPane().getRefModel();
//		//�򿪲���ǰ���˵�ͣ�ð��
//		model.setDisabledDataShow(false);
//		
//		//���ڲ�ͬ����Ա��ְ����֯�п��ܲ�ͬ����֯��ͬ��ʹ�õİ��Ҳ�п��ܲ�ͬ��V61�޸�
//		getShiftRefPane().setPk_org(pk_org);
//		if(vo.getModifiedCalendarMap().containsKey(date.toString()))
//			getShiftRefPane().setPK(pk_org);
//		
////		else
////			getShiftRefPane().setPK(pk_org);
//		return true;
		
		
		
	}
	
	public void stopCellEditing(){
		getCalendarXPanel().stopCellEditing();
	}

	@Override
	public void setComponentVisible(boolean visible) {
		// TODO Auto-generated method stub
		super.setComponentVisible(visible);
		if(!visible)
			return;
		if(!needRemoteQueryWhenShowMeUp)
			return;
		//��ҪԶ�̲�ѯ��ʱ��Ų飬���򲻲�
		refreshCalendarPanel();
		needRemoteQueryWhenShowMeUp = false;
	}
	
	/**
	 * ˢ�������������ʾ
	 */
	private void refreshCalendarPanel(){
		getShiftRefPane().getRefModel().clearData();//������е��������޸İ�κ���Ϊ�����ԭ������������İ������û���޸ģ���Ҫ���¼���һ��
		getShiftRefPane().getRefModel().reloadData();
		UFLiteralDate selDate = getCalendarXPanel().getSelMonthFirstDate();
		Pair pair = new Pair();
		pair.setDate(selDate.toString());
		getCalendarObservable().setData(pair);
		getCalendarObservable().notifyObservers();
		getPsnCalendarAppModel().setSelectedDate(getCalendarXPanel().getSelDate());
		getModel().fireEvent(new AppEvent(CalendarAppEventConst.MOUSE_SELECTED_CHANGED,this,null));
	}

	@Override
	public void setModelDate(MouseEvent e) {
		getPsnCalendarAppModel().setSelectedDate(getCalendarXPanel().getSelDate());
		getModel().fireEvent(new AppEvent(CalendarAppEventConst.MOUSE_SELECTED_CHANGED,this,null));
	}

}