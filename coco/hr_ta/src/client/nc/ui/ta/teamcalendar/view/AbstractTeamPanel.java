package nc.ui.ta.teamcalendar.view;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.ta.calendar.pub.CalendarAppEventConst;
import nc.ui.ta.pub.AbstractUIF2Panel;
import nc.ui.ta.pub.IColorConst;
import nc.ui.ta.pub.colordescription.DescriptionPanel;
import nc.ui.ta.teamcalendar.model.TeamCalendarAppModel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.ITabbedPaneAwareComponentListener;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.ta.PublicLangRes;

/**
 * ���������ʱ��ν��游��
 * @author yucheng
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractTeamPanel extends AbstractUIF2Panel implements ListSelectionListener,BillEditListener {

	//�������¼�
	protected MouseSelectListener mouseListener = new MouseSelectListener();
	//��Ƭ�л��¼�
	protected TabbedAwareListener tabbedAwareListener = new TabbedAwareListener();

	@Override
	public void valueChanged(ListSelectionEvent e){
		if(e.getValueIsAdjusting())
			return;
		int selRow = getPsnBillListPanel().getHeadTable().getSelectedRow();
		((TeamCalendarAppModel)model).setSelectedRow(selRow);
	}

	@Override
	public void handleEvent(AppEvent event) {
		if(AppEventConst.MODEL_INITIALIZED.equals(event.getType())){
			//ע�⣬��δ���ֻ��Ԫ�����������ã��������в������á������е���ֵ��Ҫ�������м�������
			getPsnBillListPanel().getBillListData().setHeaderValueObjectByMetaData(getAppModel().getData());
			getPsnBillListPanel().getHeadBillModel().updateValue();
			return;
		}
		if(AppEventConst.SELECTION_CHANGED.equals(event.getType())){
			int selRow = getAppModel().getSelectedRow();
			if(selRow == getPsnBillListPanel().getHeadTable().getSelectedRow())
				return;
			getPsnBillListPanel().getHeadTable().getSelectionModel().setSelectionInterval(selRow, selRow);
			return;
		}
		if (AppEventConst.UISTATE_CHANGED.equals(event.getType())) {
			setEditable(model.getUiState() == UIState.EDIT);
			return;
		}
		//����������¼�
		if(CalendarAppEventConst.MODEL_SORTED.equals(event.getType())){
			//�����ǰpanel��֧������ģ�����ִ�в�������ֹ�¼�Ƕ�ף���Ϊ�¼�Դ��������
			if(getPsnBillListPanel().getHeadTable().isSortEnabled())
				return;
			BillModel billModel = getPsnBillListPanel().getHeadBillModel();
			getPsnBillListPanel().getBillListData().setHeaderValueObjectByMetaData(getAppModel().getData());
			billModel.updateValue();
			getPsnBillListPanel().getHeadTable().getSelectionModel().setSelectionInterval(getAppModel().getSelectedRow(), getAppModel().getSelectedRow());
			return;
		}
	}

	protected int getTableHeaderHeight(){
		return 38;
	}

	protected UIPanel createDescriptionPanel(){
		DescriptionPanel descPanel = new DescriptionPanel();
		descPanel.setLeadString(PublicLangRes.DESCRIPTION2()+":");
		descPanel.add(nc.ui.bd.holiday.IColorConst.COLOR_WORKDAY, nc.ui.bd.holiday.IColorConst.COLOR_WORKDAY_BORDER,
				PublicLangRes.WORKDAY());// ������
		
		descPanel.add(nc.ui.bd.holiday.IColorConst.COLOR_NONWORKDAY,
				nc.ui.bd.holiday.IColorConst.COLOR_NONWORKDAY_BORDER, 
				ResHelper.getString("twhr_psncalendar", "psncalendar-0019"));// �ǹ�����-��Ϣ��
		
		descPanel.add(nc.ui.bd.holiday.IColorConst.COLOR_HOLIDAY, nc.ui.bd.holiday.IColorConst.COLOR_HOLIDAY_BORDER,
				ResHelper.getString("twhr_psncalendar", "psncalendar-0020"));// ����-������
		
		descPanel.add(nc.ui.bd.holiday.IColorConst.COLOR_OFFICALHOLIDAY, nc.ui.bd.holiday.IColorConst.COLOR_OFFICALHOLIDAY_BORDER,
				ResHelper.getString("twhr_psncalendar", "psncalendar-0021"));//������
		
		
		descPanel.add(nc.ui.bd.holiday.IColorConst.COLOR_NOTALLHOLIDAY,
				nc.ui.bd.holiday.IColorConst.COLOR_NOTALLHOLIDAY_BORDER, ResHelper.getString("6017teamcalendar",
						"06017teamcalendar0021")/* @res "��ȫԱ����" */);
		
		descPanel.add(IColorConst.COLOR_SHIFTDIFF, IColorConst.COLOR_SHIFTDIFF_BORDER, ResHelper.getString(
				"6017teamcalendar", "06017teamcalendar0025")/* @res "��β�һ��" */);
		
		descPanel.add(IColorConst.COLOR_NONTBMPSNDOC, IColorConst.COLOR_NONTBMPSNDOC_BORDER, ResHelper.getString(
				"6017teamcalendar", "06017teamcalendar0049")/* @res "δ�������" */);
		
		descPanel.init();
		descPanel.setPreferredSize(new Dimension(270, 25) );
		return descPanel;
	}
	/**
	 * �����������������ڱ�����ѡ��
	 *
	 */
	class MouseSelectListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			resetSelectedDate();
		}
	}

	/**
	 * ҳǩ�л����������������ڱ�����ѡ��
	 * @author yucheng
	 *
	 */
	class TabbedAwareListener implements ITabbedPaneAwareComponentListener {

		@Override
		public void componentHidden() {}

		@Override
		public void componentShowed() {
			resetSelectedDate();
		}
	}

	public abstract void resetSelectedDate();

	protected abstract void setEditable(boolean isEdit);
	protected abstract BillListPanel getPsnBillListPanel();

	public TeamCalendarAppModel getAppModel(){
		return (TeamCalendarAppModel) getModel();
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {}
}