package nc.ui.wa.taxaddtional.action;

import java.awt.event.ActionEvent;

import nc.hr.utils.ResHelper;
import nc.ui.hr.uif2.action.QueryAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.querytemplate.queryarea.IQueryExecutor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.wa.psntax.model.PsnTaxModel;
import nc.ui.wa.taxaddtional.model.TaxaddtionalModel;
import nc.ui.wa.taxaddtional.model.TaxaddtionalModelDataManager;
import nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsModelDataManager;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.pub.WaClassStateHelper;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaState;

/**
 * 
 * @author: xuhw
 * @date: 2010-6-25 下午04:15:57
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class QueryTaxaddtionalAction extends QueryAction
{
	@Override
	protected boolean isActionEnable()
	{
		return super.isActionEnable();
	}
	
	@SuppressWarnings({ "unused", "restriction" })
	public void doAction(ActionEvent e) throws Exception {
		//获取最新状态
		getQueryDelegator().doQuery(new IQueryExecutor() {
			@Override
			public void doQuery(IQueryScheme arg0) {
				((TaxaddtionalModelDataManager)getDataManager()).initModelBySqlWhere(arg0.getWhereSQLOnly());
			}

		});
		queryExcuted =true;
		super.setStatusBarMsg();
	}
}
