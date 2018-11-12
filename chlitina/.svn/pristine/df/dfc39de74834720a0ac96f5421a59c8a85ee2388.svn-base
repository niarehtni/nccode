package nc.ui.wa.paydata.action;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IHRWAActionCode;
import nc.itf.twhr.ICalculateTWNHI;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.wa.paydata.model.WadataModelDataManager;
import nc.ui.wa.pub.WaOrgHeadPanel;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaClassStateHelper;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaState;

/**
 * 取消发放
 * 
 * @author: liangxr
 * @date: 2010-7-1 下午02:32:05
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class UnPayAction extends PayDataBaseAction {

	private static final long serialVersionUID = 1L;
	private WaOrgHeadPanel orgpanel = null;

	public UnPayAction() {
		super();
		putValue(INCAction.CODE, IHRWAActionCode.UnPayAction);
		setBtnName(ResHelper.getString("60130paydata", "060130paydata0370")/*
																			 * @res
																			 * "取消发放"
																			 */);
		putValue(Action.SHORT_DESCRIPTION,
				ResHelper.getString("60130paydata", "060130paydata0343")/*
																		 * @res
																		 * "发放"
																		 */
						+ "(Ctrl+Alt+U)");
		putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.CTRL_MASK
						+ Event.ALT_MASK));
	}

	public void doAction(ActionEvent e) throws Exception {
		WaLoginContext context = (WaLoginContext) getContext();

		WaLoginVO waLoginVO = WaClassStateHelper.getWaclassVOWithState(context
				.getWaLoginVO());

		if (UFBoolean.TRUE.equals(waLoginVO.getStopflag())) {
			MessageDialog.showHintDlg(getEntranceUI(), null,
					ResHelper.getString("60130payfile", "060130payfile0249"));
			return;
		}

		if (!context.getWaLoginVO().getState().equals(waLoginVO.getState())) {
			MessageDialog.showHintDlg(getEntranceUI(), null,
					ResHelper.getString("60130payfile", "060130payfile0250"));
			getOrgpanel().refresh();
			((WadataModelDataManager) getDataManager()).refresh();
			putValue("message_after_action",
					ResHelper.getString("60130payfile", "060130payfile0250"));
			return;
		}
		doActionForExtend(e);
	}

	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {
		// 审批流审批的类别
		PeriodStateVO period = getPeriodVO();
		if (period != null) {
			if (showYesNoMessage(ResHelper.getString("60130paydata",
					"060130paydata0371")/* @res "您确定要取消发放吗？" */) != MessageDialog.ID_YES) {
				return;
			}
		}

		// ssx added for extend NHI on 2017/7/4
		// Ares.Tank 二代健保計算位置挪移, TODO 取消計算邏輯暫時沒有需求
		/*ICalculateTWNHI nhiSrv = NCLocator.getInstance().lookup(
				ICalculateTWNHI.class);
		nhiSrv.deleteExtendNHIInfo(getWaContext().getPk_group(), getWaContext()
				.getPk_org(), getWaContext().getPk_wa_class(), getWaContext()
				.getWaLoginVO().getPk_periodscheme(), getWaContext()
				.getWaLoginVO().getPeriodVO().getPk_wa_period(), period
				.getCpaydate());*/
		//

		getPaydataManager().onUnPay();

		putValue(HrAction.MESSAGE_AFTER_ACTION,
				ResHelper.getString("60130paydata", "060130paydata0519")/*
																		 * @res
																		 * "取消发放操作成功。"
																		 */);

	}

	/**
	 * @author zhangg on 2009-12-1
	 * @see nc.ui.wa.paydata.action.PayDataBaseAction#getEnableStateSet()
	 */
	@Override
	public Set<WaState> getEnableStateSet() {
		if (waStateSet == null) {
			waStateSet = new HashSet<WaState>();
			waStateSet.add(WaState.CLASS_ALL_PAY);
		}
		return waStateSet;
	}

	public WaOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(WaOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}
}
