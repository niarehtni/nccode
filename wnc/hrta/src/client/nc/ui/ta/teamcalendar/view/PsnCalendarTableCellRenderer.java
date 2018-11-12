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
 * ������Ա����������Ⱦ��
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
		// ������޿��ڵ��������ڣ�����Ҫ����������β�һ�� �� ����δ�Ű�
		// �޿��ڵ���ʱҲ�����β�һ��
		if(value==null) //!vo.isEffectiveDate(date)
			return comp;
		// ������β�һ�µĴ���
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
