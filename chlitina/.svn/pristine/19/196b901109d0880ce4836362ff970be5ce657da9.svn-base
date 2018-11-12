package nc.ui.bd.workcalendar.pub;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.table.TableModel;

import nc.bs.logging.Logger;
import nc.ui.bd.workcalendar.actions.WorkCalendarCancelSelectAction;
import nc.ui.bd.workcalendar.actions.WorkCalendarHolidayDayAction;
import nc.ui.bd.workcalendar.actions.WorkCalendarRestDayAction;
import nc.ui.bd.workcalendar.actions.WorkCalendarWeekendDayAction;
import nc.ui.bd.workcalendar.actions.WorkCalendarWorkingDayAction;
import nc.ui.bd.workcalendar.actions.WorkCalendarWorkingTimeAction;
import nc.ui.bd.workcalendar.actions.WorkCalendarLiHolidayDayAction;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.bd.workcalendar.WorkCalendarVO;

/**
 * 工作日历Panel 创建日期（2009-09-03）
 *
 * @author guohuia
 * @version 6.0
 *
 */
public class WorkCalendarPanel extends UIPanel implements AppEventListener {

  private static final int CALENDAR_PANEL_NUM = 12;

  /**
   *
   */
  private static final long serialVersionUID = -7967888616314856629L;

  private List<CalendarCellPanel> calendarList = null;

  private WorkCalendarCancelSelectAction cancelSelectAction = null;

  private UIPanel containerPanel = new UIPanel();

  private List<CalendarDayTable> dayTables = new ArrayList<CalendarDayTable>();

  private int firstweekday = Calendar.SUNDAY;

  private WorkCalendarHolidayDayAction holidayDayAction = null;
  
  private WorkCalendarLiHolidayDayAction LiholidayDayAction = null;

  private AbstractUIAppModel model = null;

  private CalendarMouseAdapter mouseAdapter = null;

  private CalendarPaginationBar paginationBar = null;

  private UIScrollPane scrollContainer = new UIScrollPane(this.containerPanel);

  private List<ICalendarAppModel> tableModels = new ArrayList<ICalendarAppModel>();

  private WorkCalendarWeekendDayAction weekendDayAction = null;
  
  private WorkCalendarRestDayAction restDayAction = null;

  private WorkCalendarWorkingDayAction workingDayAction = null;

  private WorkCalendarWorkingTimeAction workingTimeAction = null;

  public WorkCalendarCancelSelectAction getCancelSelectAction() {
    return this.cancelSelectAction;
  }

  // 以字符串数组的形式返回被选中的日期
  public ArrayList<String> getDayBySelected() {
    // 首先清空原有的数据
    ArrayList<String> selectedDay = new ArrayList<String>();
    for (int i = 0; i < WorkCalendarPanel.CALENDAR_PANEL_NUM; i++) {
      ICalendarAppModel tableModel = this.tableModels.get(i);
      Set<Integer> cache = tableModel.getSelectedDay();
      if (cache.size() != 0) {
        java.util.Iterator<Integer> it = cache.iterator();
        int year = tableModel.getCalendarYear();
        int month = tableModel.getCalendarMonth() + 1;
        String sDate = null;
        while (it.hasNext()) {
          int day = it.next();
          sDate = this.toDateString(year, month, day);
          selectedDay.add(sDate);
        }
      }
    }
    return selectedDay;
  }

  public WorkCalendarHolidayDayAction getHolidayDayAction() {
    return this.holidayDayAction;
  }
  
//例假日GET
  public WorkCalendarLiHolidayDayAction getLiHolidayDayAction() {
	    return this.LiholidayDayAction;
	  }
 //休息日
  public WorkCalendarRestDayAction getRestDayAction() {
	    return this.restDayAction;
	  }

  public AbstractUIAppModel getModel() {
    return this.model;
  }

  public CalendarPaginationBar getPaginationBar() {
    return this.paginationBar;
  }

  public List<ICalendarAppModel> getTableModels() {
    return this.tableModels;
  }

  public CalendarModelState getTableModelState() {
    CalendarTableModel tModel =
        (CalendarTableModel) this.calendarList.get(0).getCalendarDayTable().getModel();
    return tModel.getModelState();
  }

  public WorkCalendarWeekendDayAction getWeekendDayAction() {
    return this.weekendDayAction;
  }

  public WorkCalendarWorkingDayAction getWorkingDayAction() {
    return this.workingDayAction;
  }

  public WorkCalendarWorkingTimeAction getWorkingTimeAction() {
    return this.workingTimeAction;
  }

  @Override
  public void handleEvent(AppEvent event) {
    if (AppEventConst.SELECTION_CHANGED == event.getType()) {
      this.synchronizeDataFromModel();
      this.setCalendarModelState(CalendarModelState.INITIALIZE);
    }
    else if (AppEventConst.UISTATE_CHANGED == event.getType()) {

      if (this.model.getUiState() == UIState.ADD) {
        this.onAdd();
      }
      else {
        this.setEnabled(false);
        this.synchronizeDataFromModel();
      }
    }
    else if (AppEventConst.DATA_UPDATED == event.getType()) {
      if (this.getTableModelState() == CalendarModelState.INITIALIZE) {
        this.setCalendarModelState(CalendarModelState.UPDATE);
      }
    }
  }

  public void initialize() {
    this.setLayout(new BorderLayout());
    this.containerPanel.setLayout(new GridLayout(4, 3));
    this.containerPanel.setBorder(null);
    this.scrollContainer.setBorder(null);
    if (this.mouseAdapter == null) {
      this.initMouseAdapter();
    }
    if (this.calendarList == null) {
      this.initCalendarList();
    }
    this.addCalendarListToCantianer();
    this.paginationBar = new CalendarPaginationBar();
    this.paginationBar.setEditor(this);
    this.add(this.paginationBar, BorderLayout.NORTH);
    this.add(this.scrollContainer, BorderLayout.CENTER);
    this.setVisible(false);
  }

  public void setCalendarModelState(CalendarModelState state) {
    CalendarTableModel tModel = null;
    for (CalendarCellPanel cp : this.calendarList.toArray(new CalendarCellPanel[0])) {
      tModel = (CalendarTableModel) cp.getCalendarDayTable().getModel();
      tModel.setModelState(state);
    }
  }

  public void setCancelSelectAction(WorkCalendarCancelSelectAction cancelSelectAction) {
    this.cancelSelectAction = cancelSelectAction;
    this.cancelSelectAction.setWCalendarPanel(this);
  }

  public void setHolidayDayAction(WorkCalendarHolidayDayAction holidayDayAction) {
    this.holidayDayAction = holidayDayAction;
    this.holidayDayAction.setWCalendarPanel(this);
  }
  
  //例假日SET
  public void setLiHolidayDayAction(WorkCalendarLiHolidayDayAction LiholidayDayAction) {
	    this.LiholidayDayAction = LiholidayDayAction;
	    this.LiholidayDayAction.setWCalendarPanel(this);
	  }
  //休息日
  public void setRestDayAction(WorkCalendarRestDayAction restDayAction) {
	    this.restDayAction = restDayAction;
	    this.restDayAction.setWCalendarPanel(this);
	  }

  public void setModel(AbstractUIAppModel model) {
    this.model = model;
    this.model.addAppEventListener(this);
  }

  public void setShowYear(int cYear) {
    boolean repaint = this.isRepaint();
    for (ICalendarAppModel model : this.tableModels) {
      model.setCalendarYear(cYear);
    }
  }

  @Override
  public void setVisible(boolean flag) {
    this.scrollContainer.setVisible(flag);
    this.paginationBar.setVisible(flag);
  }

  public void setWeekendDayAction(WorkCalendarWeekendDayAction weekendDayAction) {
    this.weekendDayAction = weekendDayAction;
    this.weekendDayAction.setWCalendarPanel(this);
  }

  public void setWorkingDayAction(WorkCalendarWorkingDayAction workingDayAction) {
    this.workingDayAction = workingDayAction;
    this.workingDayAction.setWCalendarPanel(this);
  }

  public void setWorkingTimeAction(WorkCalendarWorkingTimeAction workingTimeAction) {
    this.workingTimeAction = workingTimeAction;
    this.workingTimeAction.setWCalendarPanel(this);
  }

  public String toDateString(int year, int month, int day) {
    String strYear = String.valueOf(year);
    for (int j = strYear.length(); j < 4; j++) {
      strYear = "0" + strYear;
    }
    String strMonth = String.valueOf(month);
    if (strMonth.length() < 2) {
      strMonth = "0" + strMonth;
    }
    String strDay = String.valueOf(day);
    if (strDay.length() < 2) {
      strDay = "0" + strDay;
    }
    return strYear + "-" + strMonth + "-" + strDay;

  }

  protected void onAdd() {
    this.setVisible(false);
  }

  protected void onEdit() {

  }

  protected void synchronizeDataFromModel() {
    Logger.debug("entering synchronizeDataFromModel");
    this.paginationBar.setToDefault();
    Object selectedData = this.model.getSelectedData();
    if (selectedData == null) {
      this.setVisible(false);
      return;
    }
    this.setVisible(true);
    this.calendarShow();
    Logger.debug("leaving synchronizeDataFromModel");
  }

  private void addCalendarListToCantianer() {
    if (this.calendarList == null) {
      return;
    }
    ListIterator<CalendarCellPanel> iterator = this.calendarList.listIterator();
    while (iterator.hasNext()) {
      this.containerPanel.add(iterator.next());
    }
  }

  private void calendarShow() {
    CalendarTableModel tModel =
        (CalendarTableModel) this.calendarList.get(0).getCalendarDayTable().getModel();
    if (tModel.getModelState() == CalendarModelState.INITIALIZE) {
      Object selectedData = this.model.getSelectedData();
      int beginYear = ((WorkCalendarVO) selectedData).getBegindate().getYear();
      this.setShowYear(beginYear);
      this.paginationBar.setShowYear(beginYear);
    }
    else {
      this.setShowYear(tModel.getCalendarYear());
      this.paginationBar.setShowYear(tModel.getCalendarYear());
    }
  }

  private void initCalendarList() {
    if (this.calendarList == null) {
      this.calendarList = new ArrayList<CalendarCellPanel>();
      for (int i = 0; i < WorkCalendarPanel.CALENDAR_PANEL_NUM; i++) {
        CalendarCellPanel newPanel = new CalendarCellPanel(i, this.firstweekday);
        newPanel.getCalendarDayTable().addMouseListener(this.mouseAdapter);
        TableModel tableModel = newPanel.getCalendarDayTable().getModel();
        this.tableModels.add((CalendarTableModel) tableModel);
        this.dayTables.add(newPanel.getCalendarDayTable());
        ((CalendarTableModel) tableModel).setCalendarModel(this.getModel());
        this.calendarList.add(newPanel);
      }
    }
  }

  private void initMouseAdapter() {
    this.mouseAdapter = new CalendarMouseAdapter();
    this.mouseAdapter.setHolidayDayAction(this.holidayDayAction);
//    this.mouseAdapter.setWeekendDayAction(this.weekendDayAction);
    this.mouseAdapter.setWorkingDayAction(this.workingDayAction);
    this.mouseAdapter.setWorkingTimeAction(this.workingTimeAction);
    this.mouseAdapter.setLiHolidayDayAction(this.LiholidayDayAction);
    this.mouseAdapter.setRestDayAction(this.restDayAction);
    this.mouseAdapter.setCancelSelectAction(this.cancelSelectAction);
    this.mouseAdapter.setModel(this.getModel());
  }

  /**
   * 根据策略更新模型
   * 
   * @return 是否重画table
   */
  private boolean isRepaint() {
    WorkCalendarVO calendarVO = (WorkCalendarVO) this.getModel().getSelectedData();
    if (calendarVO == null) {
      return false;
    }
    Integer weekday = calendarVO.getFfirstweekday();
    // 默认为周日时不处理
    if (weekday == null || weekday.intValue() > 7 || weekday.intValue() < 1
        || this.firstweekday == weekday.intValue()) {
      return false;
    }
    this.firstweekday = weekday.intValue();
    this.containerPanel.removeAll();
    this.calendarList = null;
    this.tableModels.clear();
    this.dayTables.clear();
    this.initCalendarList();
    this.addCalendarListToCantianer();
    this.repaint();
    return true;
  }
}
