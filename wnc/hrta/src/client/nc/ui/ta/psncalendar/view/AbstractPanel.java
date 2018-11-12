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
 * ʱ���panel������panel�ĸ��࣬��ȡ�����ǵ�һЩ���Ե�
 * 
 * @author zengcheng
 *
 */
public abstract class AbstractPanel extends AbstractUIF2Panel implements ListSelectionListener, BillEditListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 5363504373636481590L;

	// �������¼�
	protected MouseSelectListener mouseListener = new MouseSelectListener();
	// ��Ƭ�л��¼�
	protected TabbedAwareListener tabbedAwareListener = new TabbedAwareListener();

	public AbstractPanel() {

	}

	protected int getTableHeaderHeight() {
		return 38;
	}

	@Override
	public void handleEvent(AppEvent event) {
		if (AppEventConst.MODEL_INITIALIZED.equals(event.getType())) {
			// ע�⣬��δ���ֻ��Ԫ�����������ã��������в������á������е���ֵ��Ҫ�������м�������
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
		// ����������¼�
		if (CalendarAppEventConst.MODEL_SORTED.equals(event.getType())) {
			// �����ǰpanel��֧������ģ�����ִ�в�������ֹ�¼�Ƕ�ף���Ϊ�¼�Դ��������
			// if(getPsnBillListPanel().getHeadTable().isSortEnabled())
			// return;
			// �����ǰ��������ʾ�ģ���ִ�в���
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
	 * ��������������ϵͳ������ѡ��
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
				ResHelper.getString("twhr_psncalendar", "psncalendar-0018"));// ������
		
		descPanel.add(nc.ui.bd.holiday.IColorConst.COLOR_NONWORKDAY,
				nc.ui.bd.holiday.IColorConst.COLOR_NONWORKDAY_BORDER, 
				ResHelper.getString("twhr_psncalendar", "psncalendar-0019"));//�ǹ�����-��Ϣ��
		
		descPanel.add(nc.ui.bd.holiday.IColorConst.COLOR_HOLIDAY, nc.ui.bd.holiday.IColorConst.COLOR_HOLIDAY_BORDER,
				ResHelper.getString("twhr_psncalendar", "psncalendar-0020"));//����-��������
		
		descPanel.add(nc.ui.bd.holiday.IColorConst.COLOR_OFFICALHOLIDAY, nc.ui.bd.holiday.IColorConst.COLOR_OFFICALHOLIDAY_BORDER,
				ResHelper.getString("twhr_psncalendar", "psncalendar-0021"));//������
		
		descPanel.add(IColorConst.COLOR_NONTBMPSNDOC, IColorConst.COLOR_NONTBMPSNDOC_BORDER,
				PublicLangRes.NOTBMPSNDOC());//�o���ڙn��
		
		descPanel.init();
		return descPanel;
	}

	public PsnCalendarAppModel getPsnCalendarAppModel() {
		return (PsnCalendarAppModel) getModel();
	}

}