package nc.ui.qcco.commission.ace.view;

import nc.bs.pubapp.utils.UserDefineRefUtils;
import nc.ui.pubapp.uif2app.event.AppUiStateChangeEvent;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.pubapp.uif2app.view.ShowUpableBillListView;
import nc.ui.qcco.commission.model.MainSubBillModel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.RowSelectionOperationInfo;
import nc.vo.pub.BusinessException;
import nc.vo.qcco.commission.AggCommissionHVO;
import nc.vo.qcco.commission.CommissionBVO;
import uap.iweb.log.Logger;

public class MainBillList extends ShowUpableBillListView {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = -7998745627745366520L;

	public void handleEvent(AppEvent event) {
		super.handleEvent(event);

		if (event.getType().equals("Multi_Selection_Changed")) {
			RowSelectionOperationInfo rowInfo = (RowSelectionOperationInfo) event.getContextObject();
			AggCommissionHVO aggvo = (AggCommissionHVO) rowInfo.getRowDatas()[0];
			int row = rowInfo.getRowIndexes()[0];

			refreshListHeadBodyDefRefs(aggvo, row);
		} else if (event.getType().equals("Selection_Changed")) {
			MainSubBillModel billModel = (MainSubBillModel) event.getSource();
			AggCommissionHVO aggvo = (AggCommissionHVO) billModel.getSelectedData();
			int row = billModel.getSelectedRow();

			refreshListHeadBodyDefRefs(aggvo, row);
		} else if (event instanceof AppUiStateChangeEvent) {
			AppUiStateChangeEvent e = (AppUiStateChangeEvent) event;
			if (e.getOldState().equals(UIState.NOT_EDIT) && e.getNewState().equals(UIState.EDIT)) {
				MainSubBillModel billModel = (MainSubBillModel) event.getSource();
				AggCommissionHVO aggvo = (AggCommissionHVO) billModel.getSelectedData();
				int row = billModel.getSelectedRow();

				refreshCardHeadBodyGrandDefRefs(aggvo, (BillForm) billModel.getBillFormView().getMainPanel(), row);
			}
		}
	}

	private void refreshCardHeadBodyGrandDefRefs(AggCommissionHVO aggvo, BillForm billForm, int row) {
		if (aggvo != null) {
			UserDefineRefUtils.refreshBillCardHeadDefRefs(aggvo, billForm, row);
			UserDefineRefUtils.refreshBillCardBodyDefRefs(aggvo, billForm, "pk_commission_b", CommissionBVO.class);
		}
	}

	private void refreshListHeadBodyDefRefs(AggCommissionHVO aggvo, int row) {
		if (aggvo != null) {
			UserDefineRefUtils.refreshBillListHeadDefRefs(aggvo, this, row);

			// 点击表头行，更新子行
			try {
				UserDefineRefUtils.refreshBillListBodyDefRefs(aggvo, this, "pk_commission_b", CommissionBVO.class);
			} catch (BusinessException e) {
				Logger.error(e.getMessage());
			}
		}
	}

}
