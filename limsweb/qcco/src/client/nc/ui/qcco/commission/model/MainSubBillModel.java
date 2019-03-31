package nc.ui.qcco.commission.model;

import nc.bs.pubapp.utils.UserDefineRefUtils;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.view.ShowUpableBillListView;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.qcco.commission.AggCommissionHVO;
import nc.vo.qcco.commission.CommissionBVO;
import uap.iweb.log.Logger;

public class MainSubBillModel extends BillManageModel {
	private nc.ui.pubapp.uif2app.components.grand.ListGrandPanelComposite billListView;
	private nc.ui.pubapp.uif2app.components.grand.CardGrandPanelComposite billFormView;

	public void initModel(Object data) {
		super.initModel(data);

		// 刷新列表页面自定义参照显示值
		// 只刷新主表和子表，孙表在点击子表后刷新
		AggCommissionHVO[] aggvos = (AggCommissionHVO[]) data;
		if (aggvos != null && aggvos.length > 0) {
			ShowUpableBillListView view = ((ShowUpableBillListView) this.getBillListView().getMainPanel());
			for (AggCommissionHVO aggvo : aggvos) {
				for (int row = 0; row < view.getBillListPanel().getHeadBillModel().getRowCount(); row++) {
					ISuperVO headVO = aggvo.getParentVO();
					if (headVO.getPrimaryKey().equals(
							view.getBillListPanel().getHeadBillModel().getValueAt(row, "pk_commission_h"))) {
						UserDefineRefUtils.refreshBillListHeadDefRefs(aggvo, view, row);
					}
				}
			}

			try {
				UserDefineRefUtils.refreshBillListBodyDefRefs(aggvos[0], view, "pk_commission_b", CommissionBVO.class);
			} catch (BusinessException e) {
				Logger.error(e.getMessage());
			}
		}
		//
	}

	public nc.ui.pubapp.uif2app.components.grand.ListGrandPanelComposite getBillListView() {
		return billListView;
	}

	public void setBillListView(nc.ui.pubapp.uif2app.components.grand.ListGrandPanelComposite billListView) {
		this.billListView = billListView;
	}

	public nc.ui.pubapp.uif2app.components.grand.CardGrandPanelComposite getBillFormView() {
		return billFormView;
	}

	public void setBillFormView(nc.ui.pubapp.uif2app.components.grand.CardGrandPanelComposite billFormView) {
		this.billFormView = billFormView;
	}
}
