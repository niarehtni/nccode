package nc.ui.bd.workcalendar.actions;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.Action;

import nc.ui.bd.workcalendar.model.WorkCalendarTreeModel;
import nc.ui.bd.workcalendar.pub.CalendarModelState;
import nc.ui.bd.workcalendar.pub.CalendarPaginationBar;
import nc.ui.bd.workcalendar.view.WorkCalendarHolidayMemoDialog;
import nc.ui.pub.beans.UIDialog;
import nc.vo.bd.workcalendar.CalendarDateType;
import nc.vo.bd.workcalendar.WorkCalendarDateVO;
import nc.vo.bd.workcalendar.WorkCalendarVO;
import nc.vo.pub.BusinessException;

public class WorkCalendarLiHolidayDayAction extends WorkCalendarAction {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private final String btnName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("10140wcb",
      "010140wcb0055")/*@res "例假日"*/;

  public WorkCalendarLiHolidayDayAction() {
    super();
    this.setBtnName(this.btnName);
    this.setCode("LiHoliday");
    this.putValue(Action.SHORT_DESCRIPTION, this.btnName);
  }

  @Override
  public void doAction(ActionEvent e) throws Exception {
    /**日期字符串YYYY-YY—DD*/
    ArrayList<String> value = (this.wCalendarPanel).getDayBySelected();
    if (value == null || value.size() == 0) {
      throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("10140wcb",
          "010140wcb0009")/*@res "未选中任何日期 ，请选择日期后再进行右键设置操作."*/);
    }
    this.validateDate(value.toArray(new String[0]));
    WorkCalendarHolidayMemoDialog memoDialog =
        new WorkCalendarHolidayMemoDialog(this.getModel().getContext().getEntranceUI(),
            nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("10140wcb", "010140wcb0010")/*@res "节假日说明"*/);
    String memo = " ";
    if (memoDialog.showModal() == UIDialog.ID_OK) {
      memo = memoDialog.getHolidayMemo();
    }
    WorkCalendarDateVO[] objs = this.constructWorkCalendarDateVOs(value);
    this.setMemo(objs, memo);
    WorkCalendarVO calendarVO = (WorkCalendarVO) this.getModel().getSelectedData();
    calendarVO.setCalendardates(objs);
    CalendarPaginationBar bar = this.wCalendarPanel.getPaginationBar();
    this.wCalendarPanel.setCalendarModelState(CalendarModelState.UPDATE);
    int currentYear = bar.getCurrentYear();
    ((WorkCalendarTreeModel) this.getModel()).update(calendarVO);
    bar.setShowYear(currentYear);
    this.showSuccessInfo();
  }

  @Override
  public CalendarDateType getDateType() {
    return CalendarDateType.OFFICALHOLIDAY;
  }

  public void setMemo(WorkCalendarDateVO[] calendarVOs, String memo) {
    if (calendarVOs == null || calendarVOs.length == 0) {
      return;
    }
    for (WorkCalendarDateVO vo : calendarVOs) {
      vo.setMemo(memo);
    }
  }

}
