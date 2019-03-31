package nc.impl.ta.overtime;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.pubitf.ta.leaveextrarest.ILeaveExtraRestService;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

public class AutoSettleLeaveBalancePlugin implements IBackgroundWorkPlugin {

	@Override
	public PreAlertObject executeTask(BgWorkingContext context) throws BusinessException {
		// 结算日期：当前日期前一日
		UFLiteralDate settleDate = new UFLiteralDate().getDateBefore(1);
		String[] pk_orgs = context.getPk_orgs();

		if (pk_orgs != null && pk_orgs.length > 0) {
			// 结算：加班转调休
			ISegDetailService otSegSettleSvc = NCLocator.getInstance().lookup(ISegDetailService.class);
			for (String pk_org : pk_orgs) {
				otSegSettleSvc.settleByExpiryDate(pk_org, null, settleDate, false);
			}

			// 结算：外加补休
			ILeaveExtraRestService leaveExtraRestSvc = NCLocator.getInstance().lookup(ILeaveExtraRestService.class);
			for (String pk_org : pk_orgs) {
				leaveExtraRestSvc.settledByExpiryDate(pk_org, null, settleDate, false);
			}
		}

		return null;
	}

}
