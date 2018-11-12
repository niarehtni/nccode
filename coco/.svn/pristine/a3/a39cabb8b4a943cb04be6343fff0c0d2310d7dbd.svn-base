package nc.bs.hrsms.ta.sss.calendar.ctrl;

import nc.bs.hrsms.ta.sss.calendar.WorkCalendarConsts;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.uap.ctrl.tpl.qry.IQueryController;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.hrss.ta.calendar.QryConditionVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

public class WorkCalendarListQueryCtrl extends IQueryController {

	@Override
	public void simpleQueryonDataLoad(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			selRow = DatasetUtil.initWithEmptyRow(ds, true, Row.STATE_NORMAL);
		}
		setDefaultConditions(ds, selRow);
	}
	@Override
	public void advaceDsConditionChanged(DatasetCellEvent dataLoadEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void simpleValueChanged(DatasetCellEvent dataLoadEvent) {
		// TODO Auto-generated method stub
		Dataset ds = dataLoadEvent.getSource();
		int colIndex = dataLoadEvent.getColIndex();
		if (colIndex == ds.nameToIndex(WorkCalendarConsts.FD_BEGINDATE) || colIndex == ds.nameToIndex(WorkCalendarConsts.FD_ENDDATE)) {
			ApplicationContext appCxt = AppLifeCycleContext.current().getApplicationContext();
			String oldValue = (String) dataLoadEvent.getOldValue();
			String newValue = (String) dataLoadEvent.getNewValue();
			if (oldValue.equals(newValue)) {
				appCxt.addAppAttribute(WorkCalendarConsts.SESSION_DATE_CHANGE, UFBoolean.FALSE);
			} else {
				// 开始日期/结束日期发生了变化
				appCxt.addAppAttribute(WorkCalendarConsts.SESSION_DATE_CHANGE, UFBoolean.TRUE);
			}
		}
	}
	/**
	 * 搜索部分-设置默认查询条件
	 * 
	 * @param ds
	 * @param selRow
	 */
	private void setDefaultConditions(Dataset ds, Row selRow) {

		SessionBean sess = SessionUtil.getSessionBean();
		QryConditionVO vo = (QryConditionVO) sess.getExtendAttributeValue(WorkCalendarConsts.SESSION_QRY_CONDITIONS);
		if (isCatagoryAccess() && vo != null) {// 按时段/日历查看进入
			if (ds.nameToIndex(WorkCalendarConsts.FD_BEGINDATE) > -1) {
				// 开始日期
				selRow.setValue(ds.nameToIndex(WorkCalendarConsts.FD_BEGINDATE), String.valueOf(vo.getBeginDate()));
			}
			// 结束日期
			if (ds.nameToIndex(WorkCalendarConsts.FD_ENDDATE) > -1) {
				selRow.setValue(ds.nameToIndex(WorkCalendarConsts.FD_ENDDATE), String.valueOf(vo.getEndDate()));
			}
			// 排班条件
			if (ds.nameToIndex(WorkCalendarConsts.FD_ARRANGEFLAG) > -1) {
				selRow.setValue(ds.nameToIndex(WorkCalendarConsts.FD_ARRANGEFLAG), vo.getArrangeflag());
			}
		} else {// 通过菜单进入
			// 管理部门所在的HR组织
			String pk_hr_org = SessionUtil.getHROrg();
			UFLiteralDate[] dates = TBMPeriodUtil.getDefaultBeginEndDateByPkOrg(pk_hr_org);
			if (ds.nameToIndex(WorkCalendarConsts.FD_BEGINDATE) > -1) {
				// 开始日期
				selRow.setValue(ds.nameToIndex(WorkCalendarConsts.FD_BEGINDATE), String.valueOf(dates[0]));
			}
			if (ds.nameToIndex(WorkCalendarConsts.FD_ENDDATE) > -1) {
				if(dates[1].after(dates[0].getDateAfter(60))){
					dates[1] = dates[0].getDateAfter(60);
				}
				// 结束日期
				selRow.setValue(ds.nameToIndex(WorkCalendarConsts.FD_ENDDATE), String.valueOf(dates[1]));
			}
			if (ds.nameToIndex(WorkCalendarConsts.FD_ARRANGEFLAG) > -1) {
				// 排班条件
				selRow.setValue(ds.nameToIndex(WorkCalendarConsts.FD_ARRANGEFLAG), String.valueOf(WorkCalendarConsts.QUERYSCOPE_ALL));
			}
		}
	}

	/**
	 * 是否通过查看方式切换(按时段/日历查看)进入页面
	 * 
	 * @return
	 */
	public boolean isCatagoryAccess() {
		String flag = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(WorkCalendarConsts.SESSION_CATAGORY_ACCESS);
		if (StringUtils.isEmpty(flag)) {
			return false;
		}
		ApplicationContext appCxt = AppLifeCycleContext.current().getApplicationContext();
		appCxt.addAppAttribute(WorkCalendarConsts.SESSION_CATAGORY_ACCESS, flag);
		return true;
	}
}
