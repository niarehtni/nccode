package nc.ui.ta.psncalendar.view;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import nc.ui.pub.bill.BillTableCellRenderer;
import nc.ui.ta.calendar.pub.CalendarColorUtils;
import nc.ui.ta.psncalendar.model.PsnCalendarAppModel;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;

public class PsnCalendarTableCellRenderer extends BillTableCellRenderer {

	PsnCalendarAppModel model;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PsnCalendarTableCellRenderer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		TableColumnModel columnModel = table.getColumnModel();
		TableColumn col = columnModel.getColumn(column);
		String date = col.getHeaderValue().toString().substring(0, 10);
		PsnJobCalendarVO vo = model.getData()[row];
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		Color color = CalendarColorUtils.getDateColor(date, vo);
		if(color!=null)
			setBackground(color);
		if(!vo.isEffectiveDate(date)){//如果是无考勤档案的日期
			setText(null);
			return comp;
		}
		//另外配色 但强 2018-4-24 15:25:34
		if(MapUtils.isNotEmpty(vo.getLeaveMap())){
			
			Map<String, String> leaveMap = vo.getLeaveMap();
			String leaveType = leaveMap.get(date);
			if(StringUtils.isNotBlank(leaveType)){
				setText(leaveMap.get(date));
				return comp;
			}
		}
		return comp;
	}

	public PsnCalendarAppModel getModel() {
		return model;
	}

	public void setModel(PsnCalendarAppModel model) {
		this.model = model;
	}

}
