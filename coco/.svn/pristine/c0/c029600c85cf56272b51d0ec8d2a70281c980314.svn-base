package nc.bs.hrsms.ta.sss.monthreport.ctrl;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.cmd.CloseViewCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.monthreport.ctrl.MonthReportForMngViewMain;
import nc.itf.ta.IMonthStatQueryMaintain;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.PaginationInfo;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.ta.psndoc.TBMPsndocVO;

/**
 * @author liuhongd
 */
public class MonthReportUnGeneratePsn implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;


	/**
	 * 数据集加载事件
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad_dsTBMPsndoc(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		if (!isPagination(ds)) {
			// 清空数据集
			DatasetUtil.clearData(ds);
			ds.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		}
		queryUnGenerateByConditionAndDept(ds);
	}
	
	/**
	 * 查询未生成统计的人员
	 *
	 * @param ds
	 */
	public void queryUnGenerateByConditionAndDept(Dataset ds) {
		
		TBMPsndocVO[] tbmPsndocVOs = null;
		IMonthStatQueryMaintain service = NCLocator.getInstance().lookup(IMonthStatQueryMaintain.class);
		// 是否包含下级部门
		boolean containsSubDepts = SessionUtil.isIncludeSubDept();
		FromWhereSQL fromWhereSQL = (FromWhereSQL) getLifeCycleContext().getApplicationContext().getAppAttribute(
				MonthReportForMngViewMain.PARAM_ID_FWSQL);
		String year = (String) getLifeCycleContext().getApplicationContext().getAppAttribute(
				MonthReportForMngViewMain.PARAM_ID_YEAR);
		String month = (String) getLifeCycleContext().getApplicationContext().getAppAttribute(
				MonthReportForMngViewMain.PARAM_ID_MONTH);
		String pk_dept = (String) getLifeCycleContext().getApplicationContext().getAppAttribute(
				MonthReportForMngViewMain.PARAM_ID_DEPT);
		if (StringUtil.isEmptyWithTrim(year)) {
			return;
		}
		if (StringUtil.isEmptyWithTrim(month)) {
			return;
		}
		try {
			tbmPsndocVOs = service.queryUnGenerateByConditionAndDept(pk_dept, containsSubDepts, fromWhereSQL, year,
					month);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		SuperVO[] vos = DatasetUtil.paginationMethod(ds, tbmPsndocVOs);
		new SuperVO2DatasetSerializer().serialize(vos, ds, Row.STATE_NORMAL);
	}

	/**
	 * 关闭窗口
	 *
	 * @param mouseEvent
	 */
	public void onCancel(MouseEvent<MenuItem> mouseEvent) {
		CmdInvoker.invoke(new CloseViewCmd(MonthReportForMngViewMain.PAGE_UNGENERATE_PSN));
	}

	public AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}

	/**
	 * 分页操作标志
	 * 
	 * @param ds
	 * @return
	 */
	private boolean isPagination(Dataset ds) {
		PaginationInfo pg = ds.getCurrentRowSet().getPaginationInfo();
		return pg.getRecordsCount() > 0;
	}
}