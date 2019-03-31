package nc.ui.wa.taxspecial_statistics.action;

import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.hr.utils.ResHelper;
import nc.ui.hr.uif2.action.QueryAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.querytemplate.queryarea.IQueryExecutor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.actions.IQueryDelegator;
import nc.ui.wa.pub.WaOrgHeadPanel;
import nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsModelDataManager;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.pub.WaClassStateHelper;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaState;

/**
 * 查询Action
 *
 * @author: xuhw
 * @since: eHR V6.5
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class QueryTaxSpecialStatisticsAction extends QueryAction {

	private static final long serialVersionUID = -3472806007303881245L;

	private IQueryDelegator queryDelegator = null;

	public QueryTaxSpecialStatisticsAction() {
		super();

		ActionInitializer.initializeAction(this, IActionCode.QUERY);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

		WaLoginContext context = (WaLoginContext) getContext();
		//获取最新状态
		WaLoginVO waLoginVO = WaClassStateHelper.getWaclassVOWithState(context.getWaLoginVO());

		//判断方案是否停用（在已选中方案的后，方案虽停用却可以继续操作）
		if(UFBoolean.TRUE.equals(waLoginVO.getStopflag())){
			MessageDialog.showHintDlg(getEntranceUI(), null, ResHelper.getString("60130payfile","060130payfile0249")/*@res "该方案已停用，不能继续操作！"*/);
			return;
		}
		//判断状态是否发生变化
		WaState newstate = waLoginVO.getState();
		WaState oldstate = context.getWaLoginVO().getState();
		if (!oldstate.equals(newstate)) {
			//由无人状态变成有人状态或反之，不刷新
			if ((!oldstate.equals(WaState.NO_WA_DATA_FOUND) && !oldstate
					.equals(WaState.CLASS_WITHOUT_RECACULATED)&&!oldstate
					.equals(WaState.CLASS_RECACULATED_WITHOUT_CHECK))
					|| (!newstate.equals(WaState.CLASS_WITHOUT_RECACULATED) && !newstate
							.equals(WaState.NO_WA_DATA_FOUND)&&!newstate
							.equals(WaState.CLASS_RECACULATED_WITHOUT_CHECK))) {
				MessageDialog.showHintDlg(getEntranceUI(), null, ResHelper.getString("60130payfile","060130payfile0250")/*@res "该方案状态发生了变化，系统自动刷新！"*/);
				getOrgpanel().refresh();
				((TaxSpecialStatisticsModelDataManager) getDataManager()).refresh();
				return;
			}
		}

		getQueryDelegator().doQuery(new IQueryExecutor() {
			@Override
			public void doQuery(IQueryScheme arg0) {
				((TaxSpecialStatisticsModelDataManager)getDataManager()).initModelBySqlWhere(arg0.getWhereSQLOnly());
			}

		});
		queryExcuted =true;
		super.setStatusBarMsg();
	}

	public IQueryDelegator getQueryDelegator() {
		return queryDelegator;
	}

	public void setQueryDelegator(IQueryDelegator queryDelegator) {
		this.queryDelegator = queryDelegator;
	}
	
	
	/** */
	private WaOrgHeadPanel orgpanel = null;
	
	public WaOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(WaOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

}