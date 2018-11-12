/**
 * @(#)PjSaAddAction.java 1.0 2017Äê9ÔÂ13ÈÕ
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.hrwa.projsalary.ace.action;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import nc.ui.hrwa.projsalary.ace.model.ProjSalaryModel;
import nc.ui.pubapp.uif2app.actions.AddAction;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wa.pub.WaState;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings({ "restriction", "serial" })
public class PjSaAddAction extends AddAction {

	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
	}

	@Override
	protected boolean isActionEnable() {
		((ProjSalaryModel) getModel()).setCurrWaStatus(null);
		WaLoginContext waLoginContext = (WaLoginContext) getModel().getContext();
		if (!waLoginContext.isContextNotNull()) {
			return false;
		}
		if (null != waLoginContext.getWaState() && null != getEnableStateSet()
				&& getEnableStateSet().contains(waLoginContext.getWaState())) {
			return false;
		}
		if ((null != waLoginContext.getWaLoginVO().getBatch())
				&& (waLoginContext.getWaLoginVO().getBatch().intValue() > 100)) {
			return false;
		}
		if (WaLoginVOHelper.isMultiClass(waLoginContext.getWaLoginVO())) {
			return false;
		}
		// if (((ProjSalaryModel) getModel()).isPayDataApproved()) {
		// return false;
		// }
		return super.isActionEnable();
	}

	public Set<WaState> getEnableStateSet() {
		if (waStateSet == null) {
			waStateSet = new HashSet<WaState>();
			waStateSet.add(WaState.CLASS_IS_APPROVED);
			waStateSet.add(WaState.CLASS_WITHOUT_PAY);

			waStateSet.add(WaState.CLASS_PART_CHECKED);
			waStateSet.add(WaState.CLASS_CHECKED_WITHOUT_PAY);

			waStateSet.add(WaState.CLASS_ALL_PAY);

			waStateSet.add(WaState.CLASS_MONTH_END);
		}
		return waStateSet;
	}

	protected Set<WaState> waStateSet = null;

}
