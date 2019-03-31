package nc.ui.wa.taxspecial_statistics.action;

import java.awt.event.ActionEvent;

import nc.bs.logging.Logger;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hr.uif2.action.RefreshAction;
import nc.ui.ml.NCLangRes;
import nc.ui.wa.pub.WaOrgHeadPanel;
import nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsListView;
import nc.vo.pub.BusinessException;

/**
 * 生成Action
 * @author xuhw
 */
public class TaxSpecialStatisticsRefreshAction extends RefreshAction {
	private TaxSpecialStatisticsListView editor;

	public TaxSpecialStatisticsListView getEditor() {
		return editor;
	}

	public void setEditor(TaxSpecialStatisticsListView editor) {
		this.editor = editor;
	}
	private static final long serialVersionUID = 6499266015495519599L;

	private WaOrgHeadPanel orgpanel = null;
	public WaOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(WaOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}
	 protected void refreshMultiData() {
			try {
				getOrgpanel().refresh();
				getDataManager().refresh();
			} catch (BusinessException e) {
				Logger.error("未知错误", e);
			}
		}
	 
	 @Override
	public void doAction(ActionEvent evt) throws Exception {
		super.doAction(evt);
		putValue(HrAction.MESSAGE_AFTER_ACTION, NCLangRes.getInstance().getStrByID("common", "UCH007")/*"刷新成功！"*/);
	}

}
