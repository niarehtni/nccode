package nc.bs.hrss.ta.leave.ctrl;

import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.common.ctrl.TaApplyBaseView;
import nc.bs.hrss.ta.leave.LeaveConsts;
import nc.bs.hrss.ta.leave.lsnr.LeaveAddProcessor;
import nc.bs.hrss.ta.leave.lsnr.LeaveCopyProcessor;
import nc.bs.hrss.ta.leave.lsnr.LeaveEditProcessor;
import nc.bs.hrss.ta.leave.lsnr.LeaveLineAddProcessor;
import nc.bs.hrss.ta.leave.lsnr.LeaveLineDelProcessor;
import nc.bs.hrss.ta.leave.lsnr.LeaveSaveAddProcessor;
import nc.bs.hrss.ta.leave.lsnr.LeaveSaveProcessor;
import nc.bs.hrss.ta.utils.ComboDataUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.itf.hrss.pub.cmd.prcss.IAddProcessor;
import nc.itf.hrss.pub.cmd.prcss.ICopyProcessor;
import nc.itf.hrss.pub.cmd.prcss.IEditProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineDelProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineInsertProcessor;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.itf.ta.ILeaveAppInfoDisplayer;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.itf.ta.ITimeItemQueryService;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.combodata.ComboData;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.data.RowData;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.Datasets2AggVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

/**
 * 休假申请的弹出片段的Controllor
 * 
 * @author qiaoxp
 */
public class LeaveApplyView extends TaApplyBaseView implements IController {
	private static final long serialVersionUID = 1L;

	/**
	 * 单据类型
	 * 
	 * @return
	 */
	@Override
	protected String getBillType() {
		return LeaveConsts.BILL_TYPE_CODE;
	}

	/**
	 * 数据集ID
	 * 
	 * @return
	 */
	@Override
	protected String getDatasetId() {
		return LeaveConsts.DS_MAIN_NAME;
	}

	/**
	 * 单据聚合类型VO
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends AggregatedValueObject> getAggVoClazz() {
		return LeaveConsts.CLASS_NAME_AGGVO;
	}

	/**
	 * 修改的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends IEditProcessor> getEditPrcss() {
		return LeaveEditProcessor.class;
	}

	/**
	 * 复制的PROCESSOR
	 */
	@Override
	protected Class<? extends ICopyProcessor> getCopyPrcss() {
		return LeaveCopyProcessor.class;
	}

	/**
	 * 保存的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ISaveProcessor> getSavePrcss() {
		return LeaveSaveProcessor.class;
	}

	/**
	 * 保存并新增的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ISaveProcessor> getSaveAddPrcss() {
		return LeaveSaveAddProcessor.class;
	}

	/**
	 * 行新增的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ILineInsertProcessor> getLineAddPrcss() {
		return LeaveLineAddProcessor.class;
	}

	/**
	 * 行删除的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ILineDelProcessor> getLineDelPrcss() {
		return LeaveLineDelProcessor.class;
	}

	/**
	 * 新增的PROCESSOR
	 */
	@Override
	protected Class<? extends IAddProcessor> getAddPrcss() {
		return LeaveAddProcessor.class;
	}

	public void onDataLoad_hrtaleaveh(DataLoadEvent dataLoadEvent) {
		super.onDataLoad(dataLoadEvent);
		
		Dataset ds = dataLoadEvent.getSource();
		Row masterRow = ds.getSelectedRow();
		// 申请单据所属组织
		String pk_org = masterRow.getString(ds.nameToIndex(LeavehVO.PK_ORG));

		// 获得休假类别PK
		String pk_leavetype = masterRow.getString(ds.nameToIndex(LeavehVO.PK_LEAVETYPE));
		if(StringUtils.isEmpty(pk_leavetype)){
			return;
		}
		TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(pk_org, pk_leavetype);
		LfwView viewMain = ViewUtil.getCurrentView();
		FormComp formComp = (FormComp) viewMain.getViewComponents().getComponent(LeaveConsts.PAGE_FORM_LEAVEINFO);
		setElementVisible(timeItemCopyVO, formComp);
		
		//添加    设置”己休時長”,”享有時長”,”結餘時長” 20171027 xw start
		setLeaveDayOrHour(ds,masterRow);
		if(HrssConsts.POPVIEW_OPERATE_COPY.equals(getApplicationContext().getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS))){
			setLeaveTypeChage(viewMain,ds,masterRow);
		}
		//添加    设置”己休時長”,”享有時長”,”結餘時長” 20171027 xw end
	}

	/**
	 * 休假申请子表的值变化事件<br/>
	 * 
	 * @param datasetCellEvent
	 */
	public void onAfterDataChage_hrtaleaveb(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		// 字段顺序
		int colIndex = datasetCellEvent.getColIndex();
		// 开始时间|结束时间
		if (colIndex != ds.nameToIndex(LeavebVO.LEAVEBEGINTIME) && colIndex != ds.nameToIndex(LeavebVO.LEAVEENDTIME)) {
			return;
		}
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			return;
		}
		UFDateTime beiginTime = (UFDateTime) selRow.getValue(ds.nameToIndex(LeavebVO.LEAVEBEGINTIME));
		UFDateTime endTime = (UFDateTime) selRow.getValue(ds.nameToIndex(LeavebVO.LEAVEENDTIME));
		if (colIndex == ds.nameToIndex(LeavebVO.LEAVEBEGINTIME)) {
			if (beiginTime != null) {
				// 设置了开始日期
				selRow.setValue(ds.nameToIndex(LeavebVO.LEAVEBEGINDATE), new UFLiteralDate(beiginTime.getDate().toString()));
			} else {
				// 设置了开始日期
				selRow.setValue(ds.nameToIndex(LeavebVO.LEAVEBEGINDATE), null);
			}
		}
		if (colIndex == ds.nameToIndex(LeavebVO.LEAVEENDTIME)) {
			if (endTime != null) {
				// 设置了结束日期
				selRow.setValue(ds.nameToIndex(LeavebVO.LEAVEENDDATE), new UFLiteralDate(endTime.getDate().toString()));
			} else {
				// 设置了结束日期
				selRow.setValue(ds.nameToIndex(LeavebVO.LEAVEENDDATE), null);
			}
		}
		LfwView view = getLifeCycleContext().getViewContext().getView();
		Dataset dsMaster = view.getViewModels().getDataset(LeaveConsts.DS_MAIN_NAME);
		Row rowMaster = dsMaster.getSelectedRow();
		// 调用后台计算时长
		calculate(dsMaster, ds, rowMaster);
	}

	/**
	 * 休假申请主表的值变化事件<br/>
	 * 休假类别发生变化影响:1.考勤单位2.期间是否可编辑3.已休时长 |享有时长 |结余时长4.休假总时长5.子表休假时长<br/>
	 * 年度发生变化影响:1.期间的下拉数据集,2.已休时长 |享有时长 |结余时长<br/>
	 * 期间发生变化影响:1.已休时长 |享有时长 |结余时长<br/>
	 * 
	 * @param datasetCellEvent
	 */
	public void onAfterDataChange_hrtaleaveh(DatasetCellEvent datasetCellEvent) {
		LfwView viewMain = getLifeCycleContext().getViewContext().getView();
		Dataset ds = datasetCellEvent.getSource();
		// 字段顺序
		int colIndex = datasetCellEvent.getColIndex();
		if (colIndex != ds.nameToIndex(LeavehVO.LEAVEYEAR) && colIndex != ds.nameToIndex(LeavehVO.LEAVEMONTH) && colIndex != ds.nameToIndex(LeavehVO.PK_LEAVETYPE)) {
			return;
		}
		Row selRow = ds.getSelectedRow();
		if (colIndex == ds.nameToIndex(LeavehVO.PK_LEAVETYPE)) {// 休假类别
			setLeaveTypeChage(viewMain, ds, selRow);
		} else if (colIndex == ds.nameToIndex(LeavehVO.LEAVEYEAR)) {// 年度切换
			setLeaveYearChange(viewMain, ds, selRow);
		} else if (colIndex == ds.nameToIndex(LeavehVO.LEAVEMONTH)) {// 期间切换
			setLeaveMonthChange(viewMain, ds, selRow);
		}
	}

	/**
	 * 休假类别发生变化影响:<br/>
	 * 1.考勤单位<br/>
	 * 2.期间是否可编辑<br/>
	 * 启用往期假优先时,期间不可编辑;<br/>
	 * 未启用往期假优先时,休假类别Copy的结算方式-按月结算时,期间可编辑;<br/>
	 * 3.已休时长 |享有时长 |结余时长<br/>
	 * 4.休假总时长<br/>
	 * 5.子表休假时长<br/>
	 * 
	 * @param ds
	 * @param selRow
	 */
	private void setLeaveTypeChage(LfwView viewMain, Dataset ds, Row selRow) {
		// // 设置考勤单位
		TimeItemCopyVO timeItemCopyVO = setTimeUnitText(ds, selRow);
		// 2.期间是否可编辑, 启用往期假优先时,期间不可编辑;
		// 未启用往期假优先时,休假类别Copy的结算方式-按月结算时,期间可编辑;
		FormComp formComp = (FormComp) viewMain.getViewComponents().getComponent(LeaveConsts.PAGE_FORM_LEAVEINFO);
		if (formComp == null) {
			return;
		}
		// 考勤规则
		TimeRuleVO timeRuleVO = TaAppContextUtil.getTimeRuleVO();
		FormElement monthElem = formComp.getElementById(LeavehVO.LEAVEMONTH);
		if (monthElem != null) {
			if (timeRuleVO.isPreHolidayFirst()) {
				// 启用往期假优先时,期间不可编辑;
				monthElem.setEnabled(false);
			} else {
				Integer leavesetperiod = null;
				if (timeItemCopyVO != null) {
					// 未启用往期假优先时,根据休假类别Copy的结算方式设置;
					leavesetperiod = timeItemCopyVO.getLeavesetperiod();
				}
				if (leavesetperiod != null && leavesetperiod == TimeItemCopyVO.LEAVESETPERIOD_MONTH) {
					monthElem.setEnabled(true);
				} else {
					monthElem.setEnabled(false);
				}
			}
		}
		
		// 根据休假类别显示或隐藏享有时长
		setElementVisible(timeItemCopyVO, formComp);
		// 3.已休时长 |享有时长 |结余时长
		setLeaveDayOrHour(ds, selRow);

		LfwView view = getLifeCycleContext().getViewContext().getView();
		Dataset dsDetail = view.getViewModels().getDataset(LeaveConsts.DS_SUB_NAME);
		// 4.休假总时长及5.子表休假时长
		RowData rowData = dsDetail.getCurrentRowData();
		if (rowData == null) {
			return;
		}
		Row[] rows = rowData.getRows();
		if (rows == null || rows.length == 0) {
			return;
		}
		// 调用后台计算休假总时长
		calculate(ds, dsDetail, selRow);
	}
	
	/**
	 * 根据休假类别设置字段显示
	 * 
	 * @param timeItemCopyVO
	 * @param formComp
	 */
	public static void setElementVisible(TimeItemCopyVO timeItemCopyVO, FormComp formComp){
		if(timeItemCopyVO == null){
			return;
		}
		UFBoolean ishrssshow = timeItemCopyVO.getIshrssshow();
		FormElement realElement = formComp.getElementById(LeavehVO.REALDAYORHOUR);
		FormElement restElement = formComp.getElementById(LeavehVO.RESTDAYORHOUR);
		FormElement usefulElement = formComp.getElementById(LeavehVO.USEFULDAYORHOUR);
		
		if (realElement != null && AppUtil.getAppAttr(LeavehVO.REALDAYORHOUR) == null) {
			AppUtil.addAppAttr(LeavehVO.REALDAYORHOUR, UFBoolean.valueOf(realElement.isVisible()));
		}
		if (restElement != null && AppUtil.getAppAttr(LeavehVO.RESTDAYORHOUR) == null) {
			AppUtil.addAppAttr(LeavehVO.RESTDAYORHOUR, UFBoolean.valueOf(restElement.isVisible()));
		}
		if (usefulElement != null && AppUtil.getAppAttr(LeavehVO.USEFULDAYORHOUR) == null) {
			AppUtil.addAppAttr(LeavehVO.USEFULDAYORHOUR, UFBoolean.valueOf(usefulElement.isVisible()));
		}
		
		if(ishrssshow != null && !ishrssshow.booleanValue()){
			if(realElement != null){
				realElement.setVisible(false);
			}
			if(restElement != null){
				restElement.setVisible(false);
			}
			if(usefulElement != null){
				usefulElement.setVisible(false);
			}
		}else{
			
			if(realElement != null){
				realElement.setVisible(true && ((UFBoolean)AppUtil.getAppAttr(LeavehVO.REALDAYORHOUR)).booleanValue());
			}
			if(restElement != null){
				restElement.setVisible(true && ((UFBoolean)AppUtil.getAppAttr(LeavehVO.RESTDAYORHOUR)).booleanValue());
			}
			if(usefulElement != null){
				usefulElement.setVisible(true && ((UFBoolean)AppUtil.getAppAttr(LeavehVO.USEFULDAYORHOUR)).booleanValue());
			}
		}
	}

	/**
	 * 年度发生变化<br/>
	 * 影响:1.期间的下拉数据集,2.已休时长 |享有时长 |结余时长<br/>
	 * 
	 * @param viewMain
	 * @param ds
	 * @param selRow
	 */
	private void setLeaveYearChange(LfwView viewMain, Dataset ds, Row selRow) {
		// 1.期间的下拉数据集
		setMonthComboData(viewMain, ds, selRow);
		setDefaultLeaveDayOrHour(ds, selRow);
	}

	/**
	 * 期间发生变化<br/>
	 * 影响:1.已休时长 |享有时长 |结余时长<br/>
	 * 
	 * @param viewMain
	 * @param ds
	 * @param selRow
	 */
	private void setLeaveMonthChange(LfwView viewMain, Dataset ds, Row selRow) {
		// 2.已休时长 |享有时长 |结余时长
		setLeaveDayOrHour(ds, selRow);
	}

	/**
	 * 设置期间的下拉数据集
	 * 
	 * @param viewMain
	 * @param ds
	 * @param selRow
	 */
	private void setMonthComboData(LfwView viewMain, Dataset ds, Row selRow) {

		ComboData monthData = viewMain.getViewModels().getComboData(LeaveConsts.WIDGET_COMBODATA_MONTH);
		// 获得组织主键
		String pk_org = (String) selRow.getValue(ds.nameToIndex(LeavehVO.PK_ORG));
		// 获得年度
		String leaveYear = (String) selRow.getValue(ds.nameToIndex(LeavehVO.LEAVEYEAR));
		Map<String, String[]> periodMap = TBMPeriodUtil.getPeriodMap(pk_org);
		String[] months = null;
		if (periodMap != null && periodMap.size() > 0) {
			months = periodMap.get(leaveYear);
		}
		if (months != null && months.length > 0) {
			ComboDataUtil.addCombItemsAfterClean(monthData, months);
		}
		// 年度起换,默认选中期间为空
		selRow.setValue(ds.nameToIndex(LeavehVO.LEAVEMONTH), null);
	}

	/**
	 * 根据人员主键,休假类别, 年度,期间,<br/>
	 * 查询|指定考勤期间内|申请人员|选择休假类别|的 已休时长 |享有时长 |结余时长.
	 * 
	 * @param ds
	 * @param selRow
	 * 
	 */
	private void setLeaveDayOrHour(Dataset ds, Row selRow) {
		SuperVO[] superVOs = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds, selRow);
		if (superVOs == null || superVOs.length == 0) {
			setDefaultLeaveDayOrHour(ds, selRow);
			return;
		}
		String leaveYear = selRow.getString(ds.nameToIndex(LeavehVO.LEAVEYEAR));
		String leaveMonth = selRow.getString(ds.nameToIndex(LeavehVO.LEAVEMONTH));
		if (StringUtils.isEmpty(leaveYear) && StringUtils.isEmpty(leaveMonth)) {
			setDefaultLeaveDayOrHour(ds, selRow);
			return;
		}
		LeavehVO leavehVO = (LeavehVO) superVOs[0];
		// 计算享有时间,已休时间,结余时间
		LeaveBalanceVO leaveBalanceVO = getLeaveBalanceVO(leavehVO);
		if (leaveBalanceVO == null) {
			setDefaultLeaveDayOrHour(ds, selRow);
			return;
		}
		// 设置享有时间
		selRow.setValue(ds.nameToIndex(LeavehVO.REALDAYORHOUR), leaveBalanceVO.getCurdayorhour());
		// 已休时间
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTEDDAYORHOUR), leaveBalanceVO.getYidayorhour());
		// 结余时间
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTDAYORHOUR), leaveBalanceVO.getRestdayorhour());
		// 冻结时长
		selRow.setValue(ds.nameToIndex(LeavehVO.FREEZEDAYORHOUR), leaveBalanceVO.getFreezedayorhour());
		// 可用时长
		selRow.setValue(ds.nameToIndex(LeavehVO.USEFULDAYORHOUR), new UFDouble(leaveBalanceVO.getUsefulRestdayorhour()));

		// 假期结算顺序号
		selRow.setValue(ds.nameToIndex(LeavehVO.LEAVEINDEX), leavehVO.getLeaveindex() == null ? 1 : leavehVO.getLeaveindex());

	}

	/**
	 * 设置 已休时长 |享有时长 |结余时长的默认值.
	 * 
	 * @param ds
	 * @param selRow
	 */
	private void setDefaultLeaveDayOrHour(Dataset ds, Row selRow) {
		// 设置享有时间
		selRow.setValue(ds.nameToIndex(LeavehVO.REALDAYORHOUR), UFDouble.ZERO_DBL);
		// 已休时间
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTEDDAYORHOUR), UFDouble.ZERO_DBL);
		// 结余时间
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTDAYORHOUR), UFDouble.ZERO_DBL);
		// 冻结时长
		selRow.setValue(ds.nameToIndex(LeavehVO.FREEZEDAYORHOUR), UFDouble.ZERO_DBL);
		// 可用时长
		selRow.setValue(ds.nameToIndex(LeavehVO.USEFULDAYORHOUR), UFDouble.ZERO_DBL);
		// 假期结算顺序号
		selRow.setValue(ds.nameToIndex(LeavehVO.LEAVEINDEX), 1);
	}

	/**
	 * 计算享有时间,已休时间,结余时间
	 * 
	 * @param leavehVO
	 * @return
	 */
	private LeaveBalanceVO getLeaveBalanceVO(LeavehVO leavehVO) {
		String[] keys = null;
		Map<String, LeaveBalanceVO> leaveBalanceVOMap = null;
		try {
			ILeaveBalanceManageService LeaveBalanceServ = ServiceLocator.lookup(ILeaveBalanceManageService.class);
			leaveBalanceVOMap = LeaveBalanceServ.queryAndCalLeaveBalanceVO(leavehVO.getPk_org(), leavehVO);
			keys = leaveBalanceVOMap.keySet().toArray(new String[0]);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return ArrayUtils.isEmpty(keys) ? null : leaveBalanceVOMap.get(keys[0]);
	}

	/**
	 * 设置考勤单据相关字段的显示
	 * 
	 * @param view
	 * @param ds
	 * @param selRow
	 */
	public static TimeItemCopyVO setTimeUnitText(Dataset ds, Row masterRow) {

		// 申请单据所属组织
		String pk_org = masterRow.getString(ds.nameToIndex(LeavehVO.PK_ORG));

		// 获得休假类别PK
		String pk_leavetype = masterRow.getString(ds.nameToIndex(LeavehVO.PK_LEAVETYPE));

		TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(pk_org, pk_leavetype);
		Integer timeitemunit = null;
		if (timeItemCopyVO != null) {
			// 设置休假类别copy的PK
			masterRow.setValue(ds.nameToIndex(LeavehVO.PK_LEAVETYPECOPY), timeItemCopyVO.getPk_timeitemcopy());
			timeitemunit = timeItemCopyVO.getTimeitemunit();
		} else {
			// 设置休假类别copy的PK
			masterRow.setValue(ds.nameToIndex(LeavehVO.PK_LEAVETYPECOPY), null);
		}
		// 休假信息
		LfwView view = AppLifeCycleContext.current().getViewContext().getView();
		FormComp form = (FormComp) view.getViewComponents().getComponent("headTab_card_leaveinf_form");
		// 休假总时长
		FormElement elem = form.getElementById(LeavehVO.SUMHOUR);
		String text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0062")/*
																										 * @
																										 * res
																										 * "休假总时长"
																										 */;
		setText(timeitemunit, elem, text);
		// 已休时长
		elem = form.getElementById(LeavehVO.RESTEDDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0063")/*
																								 * @
																								 * res
																								 * "已休时长"
																								 */;
		setText(timeitemunit, elem, text);
		// 享有时长
		elem = form.getElementById(LeavehVO.REALDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0064")/*
																								 * @
																								 * res
																								 * "享有时长"
																								 */;
		setText(timeitemunit, elem, text);
		// 结余时长
		elem = form.getElementById(LeavehVO.RESTDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0065")/*
																								 * @
																								 * res
																								 * "结余时长"
																								 */;
		setText(timeitemunit, elem, text);

		// 冻结时长
		elem = form.getElementById(LeavehVO.FREEZEDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0066")/*
																								 * @
																								 * res
																								 * "冻结时长"
																								 */;
		setText(timeitemunit, elem, text);

		// 可用时长
		elem = form.getElementById(LeavehVO.USEFULDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0067")/*
																								 * @
																								 * res
																								 * "可用时长"
																								 */;
		setText(timeitemunit, elem, text);
		return timeItemCopyVO;
	}

	private static void setText(Integer timeitemunit, FormElement elem, String text) {
		if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_DAY == timeitemunit) {// 天
			elem.setLabel(text + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0001")/*
																													 * @
																													 * res
																													 * "(天)"
																													 */);
		} else if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_HOUR == timeitemunit) {// 小时
			elem.setLabel(text + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0002")/*
																													 * @
																													 * res
																													 * "(小时)"
																													 */);
		} else {
			elem.setLabel(text);
		}
	}

	/**
	 * 根据休假类别PK和组织PK, 获得休假类别copy的PK
	 * 
	 * @param pk_org
	 * @param pk_leavetype
	 * @return
	 */
	public static TimeItemCopyVO getTimeItemCopyVO(String pk_org, String pk_leavetype) {
		TimeItemCopyVO timeItemCopyVO = null;
		// 查询休假类别copy的PK
		try {
			ITimeItemQueryService service = ServiceLocator.lookup(ITimeItemQueryService.class);
			timeItemCopyVO = service.queryCopyTypesByDefPK(pk_org, pk_leavetype, TimeItemVO.LEAVE_TYPE);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return timeItemCopyVO;
	}

	/**
	 * 调用后台计算休假总时长
	 * 
	 * @param ds
	 * @param dsDetail
	 * @param rowMaster
	 */
	private void calculate(Dataset ds, Dataset dsDetail, Row rowMaster) {
		Datasets2AggVOSerializer serializer = new Datasets2AggVOSerializer();
		// 设置明细时长,总时长
		AggLeaveVO aggVO = (AggLeaveVO) serializer.serialize(ds, new Dataset[] { dsDetail }, LeaveConsts.CLASS_NAME_AGGVO.getName());
		// 计算前的准备
		prepareBeforeCal(aggVO);
		try {
			TimeZone clientTimeZone = Calendar.getInstance().getTimeZone();
			ILeaveAppInfoDisplayer service = ServiceLocator.lookup(ILeaveAppInfoDisplayer.class);
			aggVO = service.calculate(aggVO, clientTimeZone);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		/** 重新设置主表数据 */
		LeavehVO headVO = (LeavehVO) aggVO.getParentVO();
		// 设置休假总时长
		rowMaster.setValue(ds.nameToIndex(LeavehVO.SUMHOUR), headVO.getSumhour());
		/** 重新设置子表数据 */
		LeavebVO[] vos = aggVO.getLeavebVOs();
		new SuperVO2DatasetSerializer().update(vos, dsDetail);
	}

	/**
	 * 计算前的准备
	 * 
	 * @param aggVO
	 */
	private void prepareBeforeCal(AggLeaveVO aggVO) {
		LeavehVO headVO = aggVO.getLeavehVO();
		LeavebVO[] vos = aggVO.getLeavebVOs();
		for (LeavebVO subVO : vos) {
			// 设置单据所属组织主键
			subVO.setPk_group(headVO.getPk_group());
			// 设置单据所属组织主键
			subVO.setPk_org(headVO.getPk_org());
			// 设置人员主键
			subVO.setPk_psndoc(headVO.getPk_psndoc());
			// 人员任职主键
			subVO.setPk_psnjob(headVO.getPk_psnjob());
			// 组织关系主键
			subVO.setPk_psnorg(headVO.getPk_psnorg());
			// 休假类别
			subVO.setPk_leavetype(headVO.getPk_leavetype());
			// 休假类别Copy
			subVO.setPk_leavetypecopy(headVO.getPk_leavetypecopy());
		}

	}

	@Override
	protected String getDetailDsId() {
		return LeaveConsts.DS_SUB_NAME;
	}
	
	/**
	 * 弹出我的假期查询窗口
	 * 
	 * @param mouseEvent
	 */
	public void toMyLeaveInfo(MouseEvent<MenuItem> mouseEvent) {
		String app = "/app/LeaveInfoEmpApp?nodecode=E20200907";
		String url = LfwRuntimeEnvironment.getRootPath() + app;
		String title = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("node_ta-res", "w_ta-001146")/** *@res"我的休假记录"*/;
		AppLifeCycleContext.current().getApplicationContext()
				.popOuterWindow(url,  title, "80%", "80%", "TYPE_DIALOG", true);
	}
}