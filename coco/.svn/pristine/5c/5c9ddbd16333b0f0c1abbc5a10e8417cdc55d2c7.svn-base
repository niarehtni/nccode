package nc.ui.bd.workcalendar.pub;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import nc.vo.bd.workcalendar.WorkCalendarDateVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;

public class CalendarDayRender extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 8446793509501523842L;
	private static final Color WEEKENDDAYCOLOR = new Color(200, 230, 200);

	private static final Color HOLIDAYCOLOR = Color.ORANGE;
	
	private static final Color RESTDAY = new Color(200, 230, 200);
	
	private static final Color OFFICALHOLIDAY = Color.RED;

	private static final Color FOCUSCOLOR = new Color(200, 200, 200);

	public CalendarDayRender() {
		setHorizontalAlignment(0);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		CalendarTableModel tableModel = (CalendarTableModel) table.getModel();
		boolean isInCalendar = tableModel.isInCalendarBeginAndEndDate(row,
				column);
		initDefaultRender();
		if ((!StringUtil.isEmptyWithTrim((String) value)) && (isInCalendar)) {
			WorkCalendarDateVO vo = tableModel.getCalendarDateVO(row, column);
			if (vo != null) {
				Integer dayAttribute = vo.getDatetype();
				switch (dayAttribute.intValue()) {
				case 0:
					renderWorkingDay(vo);
					break;
				case 1:
					renderWeekendDay();
					break;
				case 2:
					renderHoliday(vo);
					break;
				case 3:
					renderOffDay(vo);
					break;
				case 4:
					renderRestDay(vo);
				}

			}
		}

		isSelected = ((CalendarDayTable) table).isCellSelected(row, column);
		if ((isSelected) && (!StringUtil.isEmptyWithTrim((String) value)))
			setBackground(FOCUSCOLOR);
		setValue(value);
		return this;
	}

	private void renderWorkingDay(WorkCalendarDateVO vo) {
		setBackground(null);
		if ((!StringUtil.isEmptyWithTrim(vo.getOndutytime()))
				&& (!StringUtil.isEmptyWithTrim(vo.getOffdutytime()))) {
			setToolTipText(NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"10140wcb", "010140wcb0017")
					+ vo.getOndutytime() + "-" + vo.getOffdutytime());
		} else {
			setToolTipText(NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"10140wcb", "010140wcb0014"));
		}
	}

	private void renderHoliday(WorkCalendarDateVO vo) {
		setBackground(HOLIDAYCOLOR);
		String tip = vo.getMemo();
		if (!StringUtil.isEmptyWithTrim(tip)) {
			setToolTipText(tip.trim());
		}
	}
	//例假日红色
	private void renderOffDay(WorkCalendarDateVO vo) {
		setBackground(OFFICALHOLIDAY);
		String tip = vo.getMemo();
		if (!StringUtil.isEmptyWithTrim(tip)) {
			setToolTipText(tip.trim());
		}
	}
	
	//休息日绿色
	private void renderRestDay(WorkCalendarDateVO vo) {
		setBackground(RESTDAY);
		String tip = vo.getMemo();
		if (!StringUtil.isEmptyWithTrim(tip)) {
			setToolTipText(tip.trim());
		}
	}
	
	

	private void renderWeekendDay() {
		setBackground(WEEKENDDAYCOLOR);
		setToolTipText(NCLangRes4VoTransl.getNCLangRes().getStrByID("10140wcb",
				"010140wcb0013"));
	}

	private void initDefaultRender() {
		setBorder(null);
		setBackground(null);
		setToolTipText(null);
	}
}
