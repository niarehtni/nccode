package nc.ui.ta.psncalendar.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.ta.calendar.pub.CalendarAppEventConst;
import nc.ui.ta.psncalendar.model.PsnCalendarAppModel;
import nc.ui.ta.pub.AbstractUIF2Panel;
import nc.ui.ta.pub.IColorConst;
import nc.ui.ta.pub.colordescription.DescriptionPanel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.ITabbedPaneAwareComponentListener;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.ta.PublicLangRes;

/**
 * 时间段panel和日历panel的父类，抽取了他们的一些共性点
 * 
 * @author zengcheng
 *
 */
public abstract class AbstractPanel extends AbstractUIF2Panel implements ListSelectionListener, BillEditListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 5363504373636481590L;

	// 鼠标监听事件
	protected MouseSelectListener mouseListener = new MouseSelectListener();
	// 卡片切换事件
	protected TabbedAwareListener tabbedAwareListener = new TabbedAwareListener();

	public AbstractPanel() {

	}

	protected int getTableHeaderHeight() {
		return 38;
	}

	@Override
	public void handleEvent(AppEvent event) {
		if (AppEventConst.MODEL_INITIALIZED.equals(event.getType())) {
			// 注意，这段代码只对元数据列起作用，对日期列不起作用。日期列的设值需要在子类中继续处理
			getPsnBillListPanel().getBillListData().setHeaderValueObjectByMetaData(getPsnCalendarAppModel().getData());
			getPsnBillListPanel().getHeadBillModel().updateValue();
			// processRefModel();
			return;
		}
		if (AppEventConst.SELECTION_CHANGED.equals(event.getType())) {
			int selRow = getPsnCalendarAppModel().getSelectedRow();
			Object obj = getPsnBillListPanel().getHeadBillModel().getValueAt(selRow, "pk_psnjob.pk_psndoc.name");
			String psnName = obj == null ? null : obj.toString();
			getPsnCalendarAppModel().setSelectedPsnName(psnName);
			if (selRow == getPsnBillListPanel().getHeadTable().getSelectedRow())
				return;
			getPsnBillListPanel().getHeadTable().getSelectionModel().setSelectionInterval(selRow, selRow);
			return;
		}
		if (AppEventConst.UISTATE_CHANGED.equals(event.getType())) {
			if (model.getUiState() == UIState.EDIT) {
				onEdit();
				return;
			}
			onNotEdit();
			return;
		}
		// 如果是排序事件
		if (CalendarAppEventConst.MODEL_SORTED.equals(event.getType())) {
			// 如果当前panel是支持排序的，则不再执行操作，防止事件嵌套，因为事件源就是自身
			// if(getPsnBillListPanel().getHeadTable().isSortEnabled())
			// return;
			// 如果当前界面是显示的，则不执行操作
			if (isComponentVisible())
				return;
			BillModel billModel = getPsnBillListPanel().getHeadBillModel();
			billModel.setSortColumn(new String[] {});
			getPsnBillListPanel().getBillListData().setHeaderValueObjectByMetaData(getPsnCalendarAppModel().getData());
			billModel.updateValue();
			getPsnBillListPanel().getHeadTable().getSelectionModel().setSelectionInterval(
					getPsnCalendarAppModel().getSelectedRow(), getPsnCalendarAppModel().getSelectedRow());
			return;
		}
	}

	// protected abstract void processRefModel(String pk_org);

	protected abstract void onEdit();

	protected abstract void onNotEdit();

	/**
	 * 鼠标监听器，监听系统变量的选择
	 *
	 */
	class MouseSelectListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			setModelDate(e);
		}
	}

	class TabbedAwareListener implements ITabbedPaneAwareComponentListener {

		@Override
		public void componentHidden() {
		}

		@Override
		public void componentShowed() {
			setModelDate(null);
		}
	}

	public abstract void setModelDate(MouseEvent e);

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting())
			return;
		int selRow = getPsnBillListPanel().getHeadTable().getSelectedRow();
		((PsnCalendarAppModel) model).setSelectedRow(selRow);
	}

	protected abstract BillListPanel getPsnBillListPanel();

	protected UIPanel createDescriptionPanel() {
		DescriptionPanel descPanel = new DescriptionPanel();
		descPanel.setLeadString(PublicLangRes.DESCRIPTION2() + ":");

		descPanel.add(nc.ui.bd.holiday.IColorConst.COLOR_WORKDAY, nc.ui.bd.holiday.IColorConst.COLOR_WORKDAY_BORDER,
				ResHelper.getString("twhr_psncalendar", "psncalendar-0018"));// 工作日
		
		descPanel.add(nc.ui.bd.holiday.IColorConst.COLOR_NONWORKDAY,
				nc.ui.bd.holiday.IColorConst.COLOR_NONWORKDAY_BORDER, 
				ResHelper.getString("twhr_psncalendar", "psncalendar-0019"));//非工作日-休息日
		
		descPanel.add(nc.ui.bd.holiday.IColorConst.COLOR_HOLIDAY, nc.ui.bd.holiday.IColorConst.COLOR_HOLIDAY_BORDER,
				ResHelper.getString("twhr_psncalendar", "psncalendar-0020"));//假日-國定假日
		
		descPanel.add(nc.ui.bd.holiday.IColorConst.COLOR_OFFICALHOLIDAY, nc.ui.bd.holiday.IColorConst.COLOR_OFFICALHOLIDAY_BORDER,
				ResHelper.getString("twhr_psncalendar", "psncalendar-0021"));//例假日
		
		descPanel.add(IColorConst.COLOR_NONTBMPSNDOC, IColorConst.COLOR_NONTBMPSNDOC_BORDER,
				PublicLangRes.NOTBMPSNDOC());//無考勤檔案
		
		descPanel.init();
		return descPanel;
	}

	public PsnCalendarAppModel getPsnCalendarAppModel() {
		return (PsnCalendarAppModel) getModel();
	}

}