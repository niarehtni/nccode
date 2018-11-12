package nc.bs.hrsms.ta.sss.monthreport.ctrl;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.cmd.CloseViewCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.monthreport.ctrl.MonthReportForMngViewMain;
import nc.itf.ta.IMonthStatQueryMaintain;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.pub.BusinessException;
import nc.vo.ta.monthstat.MonthWorkVO;

/**
 * @author chouhl
 */
public class MonthReportDetailView implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	public static final String DETAIL_DATASET_ID = "dsMthDetail";

	/**
	 * 数据集加载事件
	 * 
	 * @param dataLoadEvent
	 */
	public void onMonthDetailDataLoad(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		String pk_org = SessionUtil.getHROrg();
		String pk_psndoc = (String) getLifeCycleContext().getApplicationContext().getAppAttribute(
				MonthReportForMngViewMain.PARAM_ID_PK_PSNDOC);
		String year = (String) getLifeCycleContext().getApplicationContext().getAppAttribute(
				MonthReportForMngViewMain.PARAM_ID_TBMYEAR);
		String month = (String) getLifeCycleContext().getApplicationContext().getAppAttribute(
				MonthReportForMngViewMain.PARAM_ID_TBMMONTH);
		// 查询月报详情
		queryMonthWorkVOsByPsn(ds, pk_org, pk_psndoc, year, month);
	}

	/**
	 * 查询月报详情
	 * 
	 * @param ds
	 * @param pk_org
	 * @param pk_psndoc
	 * @param year
	 * @param month
	 */
	public void queryMonthWorkVOsByPsn(Dataset ds, String pk_org, String pk_psndoc, String year, String month) {
		MonthWorkVO[] monthWorkVOs = null;
		IMonthStatQueryMaintain service = NCLocator.getInstance().lookup(IMonthStatQueryMaintain.class);
		try {
			monthWorkVOs = service.queryMonthWorkVOsByPsn(pk_org, pk_psndoc, year, month);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		new SuperVO2DatasetSerializer().serialize(monthWorkVOs, ds, Row.STATE_NORMAL);
		ds.setRowSelectIndex(0);

	}

	private AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}

	/**
	 * 关闭窗口
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onCancel(MouseEvent mouseEvent) {
		CmdInvoker.invoke(new CloseViewCmd(MonthReportForMngViewMain.PAGE_MTH_RPT_DTL_WIDGET));
	}
}
