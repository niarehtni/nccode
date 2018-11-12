package nc.ui.ta.teamcalendar.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import nc.ui.pub.bill.BillTableCellRenderer;
import nc.ui.ta.calendar.pub.CalendarColorUtils;
import nc.ui.ta.teamcalendar.model.TeamCalendarAppModel;
import nc.vo.ta.teamcalendar.TeamInfoCalendarVO;

/**
 * 班组工作日历表渲染器
 * 用颜色区别班次信息
 * @author yucheng
 *
 */
@SuppressWarnings("serial")
public class TeamCalendarTableCellRenderer extends BillTableCellRenderer {

	private TeamCalendarAppModel model;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		TableColumnModel columnModel = table.getColumnModel();
		TableColumn col = columnModel.getColumn(column);
		String date = col.getHeaderValue().toString().substring(0, 10);
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		TeamInfoCalendarVO vo = (TeamInfoCalendarVO) model.getData()[row];
		Color color = CalendarColorUtils.getDateColor(date, vo);
		if(color!=null)
			setBackground(color);
		return comp;
	}

	public TeamCalendarAppModel getModel() {
		return model;
	}

	public void setModel(TeamCalendarAppModel model) {
		this.model = model;
	}
	
}
