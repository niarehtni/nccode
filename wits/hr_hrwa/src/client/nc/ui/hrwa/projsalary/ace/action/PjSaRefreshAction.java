/**
 * @(#)PjSaRefreshAction.java 1.0 2017Äê9ÔÂ13ÈÕ
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.hrwa.projsalary.ace.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction;
import nc.vo.wa.pub.WaLoginContext;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings({ "restriction", "serial" })
public class PjSaRefreshAction extends DefaultRefreshAction {
	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
	}

	@Override
	protected boolean isActionEnable() {
		WaLoginContext waLoginContext = (WaLoginContext) getModel().getContext();
		return super.isActionEnable() && waLoginContext.isContextNotNull();
	}
}
