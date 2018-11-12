package nc.bs.hrsms.ta.sss.monthreport.state;

import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.itf.ta.PeriodServiceFacade;
import nc.uap.lfw.core.bm.IStateManager;
import nc.uap.lfw.core.bm.dft.AbstractStateManager;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.WindowContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.pub.BusinessException;
import nc.vo.ta.period.PeriodVO;

public class Total_StateManager extends AbstractStateManager{

	@Override
	public State getState(WebComponent target, LfwView view) {
		WindowContext winCtx=getLifeCycleContext().getWindowContext();
		if(winCtx == null){
			return IStateManager.State.DISABLED;
		}
		
		String pk_org = SessionUtil.getHROrg();
	
		LfwView simpQryView = getLifeCycleContext().getWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset(HrssConsts.DS_SIMPLE_QUERY);
		// 年度
		String year = null;
		// 期间
		String month =  null;
		// 查询无数据记录
		if (dsSearch != null) {
			Row row = dsSearch.getSelectedRow();
			year = row.getString(dsSearch.nameToIndex("tbmyear"));
			month = row.getString(dsSearch.nameToIndex("tbmmonth"));
			PeriodVO periodVO;
			try {
				periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
				if(periodVO==null){
					return IStateManager.State.DISABLED;
				}
				return IStateManager.State.ENABLED;
			} catch (BusinessException e) {
				new HrssException(e).deal();
			}
			
			
		}
		return IStateManager.State.ENABLED;
		
		
	}

	public AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}
}
