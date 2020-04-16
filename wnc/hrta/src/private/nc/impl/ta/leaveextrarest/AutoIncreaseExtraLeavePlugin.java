package nc.impl.ta.leaveextrarest;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.pubitf.ta.leaveextrarest.ILeaveExtraRestService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

public class AutoIncreaseExtraLeavePlugin implements IBackgroundWorkPlugin {

	@Override
	public PreAlertObject executeTask(BgWorkingContext context) throws BusinessException {
		String[] pk_orgs = context.getPk_orgs();

		if (pk_orgs != null && pk_orgs.length > 0) {
			for (String pk_org : pk_orgs) {
				UFLiteralDate checkDate = (context.getKeyMap().containsKey("BaseDate") && context.getKeyMap().get(
						"BaseDate") != null) ? new UFLiteralDate((String) context.getKeyMap().get("BaseDate"))
						: new UFLiteralDate().getDateBefore(1);
				ILeaveExtraRestService leaveService = NCLocator.getInstance().lookup(ILeaveExtraRestService.class);
				leaveService.autoIncreaseExtraLeave(pk_org, checkDate);
			}
		}

		return null;
	}

}
