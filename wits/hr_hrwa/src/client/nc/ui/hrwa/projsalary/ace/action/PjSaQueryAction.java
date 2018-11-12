/**
 * @(#)PjSaQueryAction.java 1.0 2017Äê9ÔÂ13ÈÕ
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.hrwa.projsalary.ace.action;

import java.awt.event.ActionEvent;

import nc.ui.hrwa.projsalary.ace.view.ProjSalaryQryDlg;
import nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction;
import nc.ui.pubapp.uif2app.query2.action.QueryExecutor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.querytemplate.querytree.QueryScheme;
import nc.vo.wa.projsalary.ProjSalaryQryVO;
import nc.vo.wa.pub.WaLoginContext;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings({ "restriction", "serial" })
public class PjSaQueryAction extends DefaultQueryAction {
	@Override
	public void doAction(ActionEvent e) throws Exception {
		// super.doAction(e);
		WaLoginContext context = (WaLoginContext) this.getModel().getContext();
		ProjSalaryQryDlg dlg = new ProjSalaryQryDlg(context);
		if (dlg.showModal() == 1) {
			ProjSalaryQryVO qryConditionVO = dlg.getResultObject();
			QueryScheme qryScheme = new QueryScheme();
			qryScheme.putName("projSalaryQry");
			qryScheme.put("pubapp_key_transtype", null);
			qryScheme.put("key_func_node", "60130projsalary");
			qryScheme.put("bean_id", "cbcf95ed-2e45-4434-9822-47636b6995fa");
			qryScheme.put("logicalcondition", new nc.vo.pub.query.ConditionVO[0]);
			qryScheme.put("pubapp_key_powerenable", "true");
			qryScheme.put("custom_query_condtion", new nc.vo.pubapp.query2.sql.process.QueryCondition[0]);
			qryScheme.put("filters", new nc.ui.querytemplate.filter.IFilter[0]);
			qryScheme.put(ProjSalaryQryVO.QRYCONDITIONVO, qryConditionVO);
			IQueryScheme queryScheme = qryScheme;
			new QueryExecutor(this).doQuery(qryScheme);
			afterProcessQuery(queryScheme);
		}

	}

	@Override
	protected boolean isActionEnable() {
		WaLoginContext waLoginContext = (WaLoginContext) getModel().getContext();
		return super.isActionEnable() && waLoginContext.isContextNotNull();
	}

}
