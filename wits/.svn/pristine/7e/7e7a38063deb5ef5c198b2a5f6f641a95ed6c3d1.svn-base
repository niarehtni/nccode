/**
 * @(#)ProjSalaryModel.java 1.0 2017Äê9ÔÂ13ÈÕ
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.hrwa.projsalary.ace.model;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.hr.wa.IPayfileQueryService;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.vo.pub.BusinessException;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.WaClassStateHelper;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;

import org.apache.commons.lang.ArrayUtils;
import org.jfree.util.Log;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings("restriction")
public class ProjSalaryModel extends BillManageModel {

	public boolean isPayDataApproved() {
		WaLoginContext waContext = (WaLoginContext) getContext();
		PayfileVO[] payDataVos = null;
		try {
			payDataVos = NCLocator.getInstance().lookup(IPayfileQueryService.class)
					.queryPayfileVOByCondition(waContext, null, null);
		} catch (BusinessException e) {
			Log.error(e);
		}
		if (!ArrayUtils.isEmpty(payDataVos)) {
			for (PayfileVO vo : payDataVos) {
				if (vo.getCheckflag().booleanValue()) {
					return true;
				}
			}
		}
		return false;
	}

	public void setCurrWaStatus(WaLoginContext context) {
		if (null == context) {
			context = (WaLoginContext) getContext();
		}
		try {
			WaLoginVO waLoginVO = WaClassStateHelper.getWaclassVOWithState(context.getWaLoginVO());
			if (!context.getWaState().equals(waLoginVO.getState())) {
				context.setWaLoginVO(waLoginVO);
			}
		} catch (BusinessException e) {
			Logger.error(e);
		}
	}
}
