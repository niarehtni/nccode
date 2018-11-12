/**
 * 
 */
package nc.ui.bm.bmfile.view;

import java.util.Arrays;

import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nc.ui.hr.comp.mergetable.IMergeModel;
import nc.ui.hr.comp.mergetable.MergeTable;
import nc.ui.hr.uif2.view.HrBillListView;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillTableSelectionEvent;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.model.AppEventConst;

import org.apache.commons.lang.ArrayUtils;

/**
 * 列表界面，支持合并单元格
 * 
 * @author duyao
 */
public class BmfileListView extends HrBillListView implements ListSelectionListener {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	int[] oldrows = null;

	@Override
	public void initUI() {
		super.initUI();
		// this.getBillListPanel().getParentListPanel();
		doMergeTable();
	}

	private void doMergeTable() {
		JViewport tableViewport = getBillListPanel().getParentListPanel().getViewport();
		MergeTable defTable = new MergeTable(getBillListPanel().getParentListPanel(), getBillListPanel()
				.getParentListPanel().getTableModel());
		tableViewport.add(defTable);
		tableViewport.addChangeListener(defTable);
		defTable.getSelectionModel().addListSelectionListener(this);
		getBillListPanel().getParentListPanel().setTable(defTable);
		// defTable.getModel().addTableModelListener(this);
		defTable.setSortEnabled(false);// 合并单元格的table不容许排序
		getBillListPanel().getHeadTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		// guoqt 社保档案节点打不开
		if (event.getType() == AppEventConst.MODEL_INITIALIZED || event.getType() == AppEventConst.SELECTION_CHANGED) {
			refreshMergeCells();
		}
	}

	/*
	 * 合并指标类型列单元格
	 */
	private void mergeCells() {
		MergeTable mergeTable = (MergeTable) (getBillListPanel().getHeadTable());
		if (!mergeTable.getIsNeedMerge()) {
			return;
		}
		int colIndex = 0;
		// mergeTable.mergeValueEqualCellsByColumn(colIndex, colIndex);
		// mergeTable.mergeValueEqualCellsByColumn(colIndex+1, colIndex+1);
		// mergeTable.mergeValueEqualCellsByColumn(colIndex+2, colIndex+2);
		// mergeTable.mergeValueEqualCellsByColumn(colIndex+3, colIndex+3);
		// mergeTable.mergeValueEqualCellsByColumn(colIndex+4, colIndex+4);
		// mergeTable.mergeValueEqualCellsByColumn(colIndex+5, colIndex+5);
		// mergeTable.mergeValueEqualCellsByColumn(colIndex+6, colIndex+6);

		mergeTable.mergeByColAndGroupByCol(colIndex, colIndex);
		mergeTable.mergeByColAndGroupByCol(colIndex, colIndex + 1);
		mergeTable.mergeByColAndGroupByCol(colIndex, colIndex + 2);
		mergeTable.mergeByColAndGroupByCol(colIndex, colIndex + 3);
		mergeTable.mergeByColAndGroupByCol(colIndex, colIndex + 4);
		mergeTable.mergeByColAndGroupByCol(colIndex, colIndex + 5);
		mergeTable.mergeByColAndGroupByCol(colIndex, colIndex + 6);
		// 20151112 xiejie3 NCdp205540416 社保档案人员信息的人员类别字段需合并显示
		mergeTable.mergeByColAndGroupByCol(colIndex, colIndex + 7);
		// end

		// MOD {增加社保代理公司的合并} kevin.nie 2017-11-13 start
		mergeTable.mergeByColAndGroupByCol(colIndex, colIndex + 8);
		// {增加社保代理公司的合并} kevin.nie 2017-11-13 end
	}

	/*
	 * 刷新合并单元格
	 */
	public void refreshMergeCells() {
		// getBillListPanel().getHeadTable();
		// getBillListPanel().getParentListPanel().getTable();
		IMergeModel defTableModel = ((MergeTable) getBillListPanel().getHeadTable()).getGridBagModel();
		defTableModel.clearMergence();
		mergeCells();
	}

	// @Override
	// public void tableChanged(TableModelEvent e) {
	// if(e.getType() == TableModelEvent.INSERT || e.getType() ==
	// TableModelEvent.DELETE){
	// refreshMergeCells();
	// }
	// }

	@Override
	public void valueChanged(ListSelectionEvent e) {

		if (!e.getValueIsAdjusting()) {
			if (getBillListPanel().getParentListPanel().getRowSelectionChangeListener() != null) {
				BillTableSelectionEvent be = new BillTableSelectionEvent(getBillListPanel().getParentListPanel());
				be.setSelectIndex(((ListSelectionModel) e.getSource()).getAnchorSelectionIndex());
				getBillListPanel().getParentListPanel().getRowSelectionChangeListener().selectionChanged(be);
			}
		}
		if (getBillListPanel().getParentListPanel().getEditListener() != null) {

			int row = getBillListPanel().getParentListPanel().getTable().getSelectedRow();
			int oldviewrow = -1;
			// //为了支持多选时发事件，暂时注释掉判断，目前传入BillEditEvent的参数是有问题的。
			int[] rows = getBillListPanel().getParentListPanel().getTable().getSelectedRows();
			if (!isEqual(oldrows, rows)) {
				BillEditEvent ev = new BillEditEvent(getBillListPanel().getParentListPanel().getTable(), oldviewrow,
						row);
				ev.setOldrows(oldrows);
				ev.setRows(rows);

				int column = getBillListPanel().getParentListPanel().getTable().getSelectedColumn();
				if (column >= 0) {
					BillItem item = getBillListPanel().getParentListPanel().getTableModel().getBodyItems()[column];
					ev.setKey(item.getKey());
				}
				getBillListPanel().getParentListPanel().getEditListener().bodyRowChange(ev);
			}
			oldrows = rows;
		}

	}

	private boolean isEqual(int[] oldRows, int[] rows) {

		if (ArrayUtils.isEmpty(oldRows) && ArrayUtils.isEmpty(rows)) {
			return true;
		} else if (!ArrayUtils.isEmpty(oldRows) && !ArrayUtils.isEmpty(rows)) {

			Arrays.sort(oldRows);
			Arrays.sort(rows);
			return Arrays.equals(oldRows, rows);
		}

		return false;

	}
}
