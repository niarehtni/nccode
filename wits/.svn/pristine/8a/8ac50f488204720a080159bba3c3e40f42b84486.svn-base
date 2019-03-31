package nc.ui.wa.taxaddtional.action;

import nc.ui.uif2.actions.RefreshAction;
import nc.ui.wa.psntax.model.PsnTaxModel;
import nc.vo.wa.pub.WaLoginContext;

/**
 * @author: xuhw
 * @date: 2010-6-25 下午04:15:19
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class RefreshTaxaddtionalAction extends RefreshAction
{
	@Override
	protected boolean isActionEnable()
	{
		PsnTaxModel model = (PsnTaxModel) getModel();
		WaLoginContext context = (WaLoginContext) model.getContext();
		if (context.getPk_org() == null || context.getWaYear() == null)
		{
			return false;
		}
		return super.isActionEnable();
	}
}
