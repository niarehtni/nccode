package nc.ui.wa.paydata.action;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.twhr.ICalculateTWNHI;
import nc.pubitf.para.SysInitQuery;
import nc.ui.pub.beans.MessageDialog;
import nc.vo.pub.lang.UFDate;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wa.pub.WaState;
import nc.vo.wa.pub.WadataState;

public class PayAction extends PayDataBaseAction {
	private static final long serialVersionUID = 1L;

	public PayAction() {
		putValue("Code", "PayAction");
		setBtnName(ResHelper.getString("60130paydata", "060130paydata0343"));

		putValue("ShortDescription",
				ResHelper.getString("60130paydata", "060130paydata0343")
						+ "(Ctrl+Alt+P)");
		putValue("AcceleratorKey", javax.swing.KeyStroke.getKeyStroke(80, 10));
	}

	public void doActionForExtend(ActionEvent e) throws Exception {
		PeriodStateVO periodStateVO = (PeriodStateVO) getEditor().getValue();

		UFDate payDate = periodStateVO.getCpaydate();
		String payComment = periodStateVO.getVpaycomment();

		// ssx added on 2015-12-07
		// for SysInit code = TW07
		//String payDateTWSetting = SysInitQuery.getParaString(this.getContext()
		//		.getPk_org(), "TWHR07");
		if (payDate == null) { // payDateTWSetting.equals("發放日期") remarked by
								// ssx on 2017/7/4, for 二代健保強制限制發放日期不能為空
			MessageDialog.showErrorDlg(SwingUtilities
					.getWindowAncestor(getWaContext().getEntranceUI()),
					ResHelper.getString("twhr_paydata",
							"TaiwanNHICalculator-0007"), ResHelper.getString(
							"twhr_paydata", "TaiwanNHICalculator-0008")); // 薪資發放日期不允許為空。
			return;
		}
		if (((payComment == null) || (payComment.trim().length() < 1))
				&& (showYesNoMessage(ResHelper.getString("60130paydata",
						"060130paydata0346")) != 4)) {
			return;
		}

		String keyName = ResHelper.getString("60130paydata",
				"060130paydata0343");
		String[] files = getPaydataManager().getAlterFiles(keyName);
		showAlertInfo(files);

		getPeriodVO().setCpaydate(payDate);
		getPeriodVO().setVpaycomment(payComment);

		getPaydataManager().onPay();

		// ssx added for extend NHI on 2017/7/4
		//Ares.Tank 二代健保計算位置移動
		/*ICalculateTWNHI nhiSrv = NCLocator.getInstance().lookup(
				ICalculateTWNHI.class);
		nhiSrv.updateExtendNHIInfo(getWaContext().getPk_group(), getWaContext()
				.getPk_org(), getWaContext().getPk_wa_class(), getWaContext()
				.getWaLoginVO().getPk_periodscheme(), getWaContext()
				.getWaLoginVO().getPeriodVO().getPk_wa_period(), payDate);*/
		//
	}

	public Set<WaState> getEnableStateSet() {
		if (waStateSet == null) {
			waStateSet = new HashSet<>();
			waStateSet.add(WaState.CLASS_IS_APPROVED);
			waStateSet.add(WaState.CLASS_WITHOUT_PAY);
		}
		return waStateSet;
	}

	protected boolean isActionEnable() {
		boolean enable = super.isActionEnable();
		if (enable) {
			Set<WadataState> set = getEnableDataStateSet();
			if (set != null) {
				enable = set.contains(getWadataState());
			}
		}
		if (enable) {
			if (!WaLoginVOHelper.isMultiClass(getWaContext().getWaLoginVO()))
				return true;
		}
		return false;
	}

	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
		putValue("message_after_action",
				ResHelper.getString("60130paydata", "060130paydata0518"));
	}
}
