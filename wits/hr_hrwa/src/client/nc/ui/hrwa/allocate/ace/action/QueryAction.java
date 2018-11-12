/**
 * @(#)QueryAction.java 1.0 2017Äê9ÔÂ19ÈÕ
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.hrwa.allocate.ace.action;

import java.awt.event.ActionEvent;

import nc.hr.utils.ResHelper;
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
@SuppressWarnings({ "serial", "restriction" })
public class QueryAction extends DefaultQueryAction {

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// super.doAction(e);
		WaLoginContext context = (WaLoginContext) this.getModel().getContext();
		ProjSalaryQryDlg dlg = new ProjSalaryQryDlg(context, Boolean.FALSE, ResHelper.getString("allocate",
				"0allcate-ui-000003"));
		if (dlg.showModal() == 1) {
			ProjSalaryQryVO qryConditionVO = dlg.getResultObject();
			QueryScheme qryScheme = new QueryScheme();
			qryScheme.putName("allocateQry");
			qryScheme.put("pubapp_key_transtype", null);
			qryScheme.put("key_func_node", "60130allocate");
			qryScheme.put("bean_id", "ccf59f8a-45cb-40a0-aeb8-ba8f7dee175b");
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
