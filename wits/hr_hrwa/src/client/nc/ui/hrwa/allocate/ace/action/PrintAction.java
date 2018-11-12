/**
 * @(#)PrintAction.java 1.0 2017Äê9ÔÂ19ÈÕ
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.hrwa.allocate.ace.action;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.MultiLangHelper;
import nc.itf.hrwa.IProjsalaryMaintain;
import nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.uif2.UIState;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.allocate.AggAllocateOutVO;
import nc.vo.wa.allocate.AllocateOutHVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings({ "serial", "restriction" })
public class PrintAction extends MetaDataBasedPrintAction {

	@Override
	public void doAction(ActionEvent evt) throws Exception {
		super.doAction(evt);
	}

	@Override
	public Object[] getDatas() {
		Object[] datas = super.getDatas();
		Set<String> projectPkSet = new HashSet<String>();
		for (Object o : datas) {
			AggAllocateOutVO aggvo = (AggAllocateOutVO) o;
			projectPkSet.add(aggvo.getParentVO().getDef1());
		}
		Map<String, DefdocVO> pjPkVOMap = new HashMap<String, DefdocVO>();
		try {
			pjPkVOMap = NCLocator.getInstance().lookup(IProjsalaryMaintain.class)
					.qryProjectMap(null, new String[] { "pk_defdoc" });
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}
		if (!pjPkVOMap.isEmpty()) {
			for (Object o : datas) {
				AggAllocateOutVO aggvo = (AggAllocateOutVO) o;
				AllocateOutHVO hvo = aggvo.getParentVO();
				DefdocVO pjVO = pjPkVOMap.get(hvo.getDef1());
				aggvo.getParentVO().setDef10(pjVO.getCode());
				aggvo.getParentVO().setDef11(MultiLangHelper.getName(pjVO));
			}
		}
		return datas;
	}

	@Override
	protected boolean isActionEnable() {
		Object[] datas = ((BillManageModel) getModel()).getSelectedOperaDatas();
		boolean enable = (getModel().getUiState() == UIState.NOT_EDIT || getModel().getUiState() == UIState.INIT)
				&& !ArrayUtils.isEmpty(datas); // && ((WaLoginContext)
												// getContext()).isContextNotNull();
		return super.isActionEnable() && enable;
	}

}
