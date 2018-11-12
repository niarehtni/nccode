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
 * 日历界面和时间段界面父类
 * @author yucheng
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractTeamPanel extends AbstractUIF2Panel implements ListSelectionListener,BillEditListener {

	//鼠标监听事件
	protected MouseSelectListener mouseListener = new MouseSelectListener();
	//卡片切换事件
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
			//注意，这段代码只对元数据列起作用，对日期列不起作用。日期列的设值需要在子类中继续处理
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
		//如果是排序事件
		if(CalendarAppEventConst.MODEL_SORTED.equals(event.getType())){
			//如果当前panel是支持排序的，则不再执行操作，防止事件嵌套，因为事件源就是自身
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
				PublicLangRes.WORKDAY());// 工作日
		
		descPanel.add(nc.ui.bd.holiday.IColorConst.COLOR_NONWORKDAY,
				nc.ui.bd.holiday.IColorConst.COLOR_NONWORKDAY_BORDER, 
				ResHelper.getString("twhr_psncalendar", "psncalendar-0019"));// 非工作日-休息日
		
		descPanel.add(nc.ui.bd.holiday.IColorConst.COLOR_HOLIDAY, nc.ui.bd.holiday.IColorConst.COLOR_HOLIDAY_BORDER,
				ResHelper.getString("twhr_psncalendar", "psncalendar-0020"));// 假日-国假日
		
		descPanel.add(nc.ui.bd.holiday.IColorConst.COLOR_OFFICALHOLIDAY, nc.ui.bd.holiday.IColorConst.COLOR_OFFICALHOLIDAY_BORDER,
				ResHelper.getString("twhr_psncalendar", "psncalendar-0021"));//例假日
		
		
		descPanel.add(nc.ui.bd.holiday.IColorConst.COLOR_NOTALLHOLIDAY,
				nc.ui.bd.holiday.IColorConst.COLOR_NOTALLHOLIDAY_BORDER, ResHelper.getString("6017teamcalendar",
						"06017teamcalendar0021")/* @res "非全员假日" */);
		
		descPanel.add(IColorConst.COLOR_SHIFTDIFF, IColorConst.COLOR_SHIFTDIFF_BORDER, ResHelper.getString(
				"6017teamcalendar", "06017teamcalendar0025")/* @res "班次不一致" */);
		
		descPanel.add(IColorConst.COLOR_NONTBMPSNDOC, IColorConst.COLOR_NONTBMPSNDOC_BORDER, ResHelper.getString(
				"6017teamcalendar", "06017teamcalendar0049")/* @res "未加入班组" */);
		
		descPanel.init();
		descPanel.setPreferredSize(new Dimension(270, 25) );
		return descPanel;
	}
	/**
	 * 鼠标监听器，监听日期变量的选择
	 *
	 */
	class MouseSelectListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			resetSelectedDate();
		}
	}

	/**
	 * 页签切换监听器，监听日期变量的选择
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