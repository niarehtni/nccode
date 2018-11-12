package nc.bs.hrsms.ta.empleavereg4store.win;

import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.uap.ctrl.tpl.qry.IQueryController;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.vo.pub.lang.UFLiteralDate;

/**
 * @author renyp
 * @date 2015-4-24
 * @ClassName功能名称：店员休假查询查询模板处理Controller
 * @Description功能描述：功能是
 * 
 */
public class EmpLeave4StoreQueryCtrl extends IQueryController {
	// 字段
	public static final String FS_BEGINDATE = "leavebegindate";
	public static final String FS_ENDDATE = "leaveenddate";
	/**
	 * @author renyp
	 * @date 2015-4-24
	 * @Description方法功能描述：作用是 设置默认值
	 * 
	 */
	@Override
	public void simpleQueryonDataLoad(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		Row row = DatasetUtil.initWithEmptyRow(ds, Row.STATE_NORMAL);
		if (row == null) {
			return;
		}
		// 人员所在的HR组织
		String pk_hr_org = SessionUtil.getHROrg();
		UFLiteralDate[] dates = TBMPeriodUtil.getDefaultBeginEndDateByPkOrg(pk_hr_org);
		String begindate = dates[0].toString();
		String enddate = dates[1].toString();
		// 开始日期
		row.setValue(ds.nameToIndex(FS_BEGINDATE), begindate);
		// 结束日期
		row.setValue(ds.nameToIndex(FS_ENDDATE), enddate);
	}
	/**
	 * @author zhanggpa@张高盼 【太平鸟】
	 * @date 2015-4-24
	 * @Description方法功能描述：作用是 前台年度输入框变化的响应事件
	 * 
	 */
	@Override
	public void simpleValueChanged(DatasetCellEvent datasetCellEvent) {
	}

	@Override
	public void advaceDsConditionChanged(DatasetCellEvent dataLoadEvent) {
	}

}
