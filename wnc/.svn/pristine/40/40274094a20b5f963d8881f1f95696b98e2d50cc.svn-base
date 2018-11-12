package nc.ui.bd.workcalendar.pub;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import nc.ui.bd.workcalendar.actions.WorkCalendarCancelSelectAction;
import nc.ui.bd.workcalendar.actions.WorkCalendarHolidayDayAction;
import nc.ui.bd.workcalendar.actions.WorkCalendarRestDayAction;
import nc.ui.bd.workcalendar.actions.WorkCalendarWeekendDayAction;
import nc.ui.bd.workcalendar.actions.WorkCalendarWorkingDayAction;
import nc.ui.bd.workcalendar.actions.WorkCalendarWorkingTimeAction;
import nc.ui.bd.workcalendar.actions.WorkCalendarLiHolidayDayAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIPopupMenu;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.util.ManageModeUtil;

/**
 * 工作日历鼠标右键菜单
 * creation time : 2009-10-29 下午04:48:47
 * @author guohuia
 * @version V6.0
 */
public class CalendarMouseAdapter extends MouseAdapter {

  private UIPopupMenu popupMenu = null;

  private WorkCalendarWorkingDayAction workingDayAction = null;

  private WorkCalendarWorkingTimeAction workingTimeAction = null;

  private WorkCalendarHolidayDayAction holidayDayAction = null;
  
  private WorkCalendarLiHolidayDayAction LiholidayDayAction = null;
  
  private WorkCalendarRestDayAction restDayAction = null;

  private WorkCalendarWeekendDayAction weekendDayAction = null;

  private WorkCalendarCancelSelectAction cancelSelectAction = null;

  private AbstractUIAppModel model = null;

  public CalendarMouseAdapter() {
    super();
  }

  public void mouseReleased(MouseEvent e) {
    if (e.isPopupTrigger()) {
      if (e.getSource() instanceof CalendarDayTable) {
        CalendarDayTable table = (CalendarDayTable) e.getSource();
        if (table.isEnabled() && controlModeValidate()) {
          buttonPopupAction(e);
        }
      }
    }
  }

  private boolean controlModeValidate() {
    if (!ManageModeUtil.manageable(getModel().getSelectedData(), getModel().getContext())) {
      MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), null,
          getDisManageableMsg());
      return false;
    }
    return true;
  }

  protected String getDisManageableMsg() {
    return ManageModeUtil.getDisManageableMsg(getModel().getContext().getNodeType());
  }

  private void buttonPopupAction(MouseEvent e) {
    int y = 0;
    if (e.getY() + getPopupMenu().getHeight() > 768)
      y = e.getY() - getPopupMenu().getHeight();
    else
      y = e.getY();
    getPopupMenu().show((Component) e.getSource(), e.getX(), y);
  }

  public UIPopupMenu getPopupMenu() {
    if (popupMenu == null) {
      popupMenu = new UIPopupMenu();
      popupMenu.add(workingDayAction);
      popupMenu.add(workingTimeAction);
      popupMenu.addSeparator();
      popupMenu.add(holidayDayAction);
      popupMenu.addSeparator();
//      popupMenu.add(weekendDayAction);
      popupMenu.add(restDayAction);
      popupMenu.add(LiholidayDayAction);
      popupMenu.addSeparator();
      popupMenu.add(cancelSelectAction);
    }
    return popupMenu;
  }

  public AbstractUIAppModel getModel() {
    return model;
  }

  public void setModel(AbstractUIAppModel model) {
    this.model = model;
  }

  public void setWorkingDayAction(WorkCalendarWorkingDayAction workingDayAction) {
    this.workingDayAction = workingDayAction;
  }

  public void setHolidayDayAction(WorkCalendarHolidayDayAction holidayDayAction) {
    this.holidayDayAction = holidayDayAction;
  }
  
//例假日SET
  public void setLiHolidayDayAction(WorkCalendarLiHolidayDayAction LiholidayDayAction) {
	    this.LiholidayDayAction = LiholidayDayAction;
	  }
//例假日SET
  public void setRestDayAction(WorkCalendarRestDayAction restDayAction) {
	    this.restDayAction = restDayAction;
	  }

  public void setWeekendDayAction(WorkCalendarWeekendDayAction weekendDayAction) {
    this.weekendDayAction = weekendDayAction;
  }

  public void setWorkingTimeAction(WorkCalendarWorkingTimeAction workingTimeAction) {
    this.workingTimeAction = workingTimeAction;
  }

  public WorkCalendarCancelSelectAction getCancelSelectAction() {
    return cancelSelectAction;
  }

  public void setCancelSelectAction(WorkCalendarCancelSelectAction cancelSelectAction) {
    this.cancelSelectAction = cancelSelectAction;
  }

}
