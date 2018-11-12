package nc.bs.hrsms.ta.sss.ShopAttendance.dft;

import nc.bs.hrsms.ta.sss.ShopAttendance.lsnr.ShopAttendanceUtil;
//import nc.bs.hrss.ta.timedata.lsnr.TimeDataUtil;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.bm.IStateManager;
import nc.uap.lfw.core.bm.dft.AbstractStateManager;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.lfw.jsp.uimeta.UITabComp;
import nc.vo.jcom.lang.StringUtil;

/**
 * 我的出勤情况的初始状态的管理器
 * 
 * @author mayif
 * 
 */
public class Init_Emp_StateManager extends AbstractStateManager {

	@Override
	public IStateManager.State getState(WebComponent target, LfwView widget) {
		
		UIMeta um = (UIMeta) LfwRuntimeEnvironment.getWebContext().getUIMeta();
		UITabComp tabComp = (UITabComp) um.findChildById(ShopAttendanceUtil.TAB_TIME_DATA);
		String currentItem = tabComp.getCurrentItem();
		if (!StringUtil.isEmptyWithTrim(currentItem) && "0".equals(currentItem)) {
			Dataset ds = widget.getViewModels().getDataset("dsMachineData");
			Row[] rows = ds.getSelectedRows();
				if (ds == null || ds.getCurrentRowData() == null
						|| ds.getCurrentRowData().getRows() == null
						|| ds.getCurrentRowData().getRows().length <= 0
						|| rows == null || ds.getSelectedRow() == null) {
					return IStateManager.State.DISABLED;
				}else{
//					// 选定的签卡日期
//					UFLiteralDate[] calendars = new UFLiteralDate[rows.length];
//					for (int i = 0; i < rows.length; i++) {
//						calendars[i] = (UFLiteralDate) rows[i].getValue(ds.nameToIndex(TimeDataForEmpPageModel.FIELD_MACHINE_CALENDAR));
//					}
//					AggSignVO aggVO = null;
//					try {
//						// 为员工创建多个日期的默认签卡单据
//						aggVO = ServiceLocator.lookup(ITimeDataQueryMaintain.class).createSignCard(SessionUtil.getPk_psndoc(), calendars);
//					} catch (HrssException e) {
//						e.alert();
//					} catch (BusinessException e) {
//						new HrssException(e).deal();
//					}
//					if (null == aggVO || ArrayUtils.isEmpty(aggVO.getSignbVOs())) {
//						return IStateManager.State.DISABLED;
//					}
					return IStateManager.State.ENABLED_VISIBLE;
				}
		} 
		return IStateManager.State.DISABLED;

	}
}
