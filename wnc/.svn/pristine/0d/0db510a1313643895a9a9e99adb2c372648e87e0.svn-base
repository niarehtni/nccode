package nc.ui.ta.teamcalendar.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.BillTableCellRenderer;
import nc.ui.ta.calendar.pub.CalendarColorUtils;
import nc.ui.ta.pub.IColorConst;
import nc.ui.ta.teamcalendar.model.TeamCalendarAppModel;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;
import nc.vo.ta.teamcalendar.TeamInfoCalendarVO;

import org.apache.commons.lang.StringUtils;

/**
 * 班组人员工作日历渲染器
 * @author yucheng
 *
 */
@SuppressWarnings("serial")
public class PsnCalendarTableCellRenderer extends BillTableCellRenderer {

	private TeamCalendarAppModel model;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		TableColumnModel columnModel = table.getColumnModel();
		TableColumn col = columnModel.getColumn(column);
		String date = col.getHeaderValue().toString().substring(0, 10);
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		PsnJobCalendarVO vo = getModel().getPsnCalendarVOs()[row];
		Color color = CalendarColorUtils.getDateColor(date, vo);
		if(color!=null)
			setBackground(color);
		// 如果是无考勤档案的日期，不需要处理与班组班次不一致 或 当天未排班
		// 无考勤档案时也处理班次不一致
		if(value==null) //!vo.isEffectiveDate(date)
			return comp;
		// 与班组班次不一致的处理
		String psnShift = (String) ((DefaultConstEnum) value).getValue();
		TeamInfoCalendarVO teamVO = getModel().getSelectedVO();
		String teamShift = teamVO.getCalendarMap().get(date);
		if(!StringUtils.equals(psnShift, teamShift))
			setForeground(IColorConst.COLOR_SHIFTDIFF);
		return comp;
	}
	
	public TeamCalendarAppModel getModel() {
		return model;
	}

	public void setModel(TeamCalendarAppModel model) {
		this.model = model;
	}
	
}
