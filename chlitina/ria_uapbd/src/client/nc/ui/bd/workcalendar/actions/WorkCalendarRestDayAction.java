package nc.ui.bd.workcalendar.actions;

import javax.swing.Action;

import nc.vo.bd.workcalendar.CalendarDateType;

public class WorkCalendarRestDayAction extends WorkCalendarAction {

  /**
   *
   */
  private static final long serialVersionUID =1L;

  private final String btnName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("10140wcb",
      "010140wcb0056")/*@res "–›œ¢»’"*/;

  public WorkCalendarRestDayAction() {
    super();
    setBtnName(btnName);
    setCode("restDay");
    putValue(Action.SHORT_DESCRIPTION, btnName);
  }

  @Override
  public CalendarDateType getDateType() {
    return CalendarDateType.RESTDAY;
  }

}
