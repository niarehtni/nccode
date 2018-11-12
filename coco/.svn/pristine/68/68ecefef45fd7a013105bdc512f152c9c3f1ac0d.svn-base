package nc.bs.hrsms.ta.sss.leaveoff.ctrl;

import java.util.Calendar;
import java.util.TimeZone;

import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrsms.ta.sss.common.ShopTAUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyBaseView;
import nc.bs.hrsms.ta.sss.common.ShopTaListBasePageModel;
import nc.bs.hrsms.ta.sss.leaveoff.ShopLeaveOffConsts;
import nc.bs.hrsms.ta.sss.leaveoff.ShopLeaveOffUtils;
import nc.bs.hrsms.ta.sss.leaveoff.prcss.ShopLeaveOffAddProcessor;
import nc.bs.hrsms.ta.sss.leaveoff.prcss.ShopLeaveOffSaveProcessor;
import nc.hr.utils.ResHelper;
import nc.itf.hrss.pub.cmd.prcss.IAddProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineInsertProcessor;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.itf.ta.ILeaveOffManageMaintain;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.FieldSet;
import nc.uap.lfw.core.data.MdDataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.data.UnmodifiableMdField;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Datasets2AggVOSerializer;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leaveoff.AggLeaveoffVO;
import nc.vo.ta.leaveoff.LeaveoffVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

public class ShopLeaveOffApplyCardView extends ShopTaApplyBaseView implements IController {

	@Override
	protected String getBillType() {
		// TODO Auto-generated method stub
		return ShopLeaveOffConsts.BILL_TYPE_CODE;
	}

	@Override
	protected String getDatasetId() {
		// TODO Auto-generated method stub
		return ShopLeaveOffConsts.MAIN_DS_NAME;
	}

	@Override
	protected String getDetailDsId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Class<? extends AggregatedValueObject> getAggVoClazz() {
		// TODO Auto-generated method stub
		return AggLeaveoffVO.class;
	}

	/**
	 * 新增的PROCESSOR
	 * 
	 * @return
	 */
	protected Class<? extends IAddProcessor> getAddPrcss() {
		return ShopLeaveOffAddProcessor.class;
	}
	@Override
	protected Class<? extends ISaveProcessor> getSavePrcss() {
		// TODO Auto-generated method stub
		return ShopLeaveOffSaveProcessor.class;
	}

	@Override
	protected Class<? extends ISaveProcessor> getSaveAddPrcss() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Class<? extends ILineInsertProcessor> getLineAddPrcss() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 主数据集的加载事件
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad_dsLeaveOff(DataLoadEvent dataLoadEvent) {
		super.onDataLoad(dataLoadEvent);
		Dataset ds = dataLoadEvent.getSource();
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			return;
		}	
		
		String pk_leavetype = selRow.getString(ds.nameToIndex(LeaveoffVO.PK_LEAVETYPE));
		String pk_org = selRow.getString(ds.nameToIndex(LeaveoffVO.PK_ORG));
		TimeItemCopyVO timeItemCopyVO = ShopTAUtil.getTimeItemCopyVO(pk_org, pk_leavetype);
		Integer timeitemunit = timeItemCopyVO.getTimeitemunit();
		
		LfwView view = ViewUtil.getCurrentView();
		// 根据考勤规则设置考勤数据的小时位数
		setTimeDatasPrecision(view);
		FormComp frmleaveinfo = (FormComp)view.getViewComponents().getComponent("frmleaveinfo");
		// 哺乳假显示标志
		boolean isLactationShow = TimeItemCopyVO.LEAVETYPE_LACTATION.equals(pk_leavetype);
		if(frmleaveinfo != null){
			/** 根据考勤单位设置Lable的"天"或"小时"的显示 */
			if(!isLactationShow){
				// 休假时长
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveinfo, LeaveoffVO.REGLEAVEHOURCOPY, timeitemunit,
					ResHelper.getString("c_ta-res", "0c_ta-res0205")/* @ res"休假时长"*/);
				// 已休时长
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveinfo, "pk_leavereg_resteddayorhour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0063")/* @ res"已休时长"*/);
				// 享有时长
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveinfo, "pk_leavereg_realdayorhour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0064")/* @ res"享有时长"*/);
				// 结余时长
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveinfo, "pk_leavereg_restdayorhour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0065")/* @ res"结余时长"*/);
				// 可用时长
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveinfo, "pk_leavereg_usefuldayorhour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0067")/* @ res"可用时长"*/);
				// 冻结时长
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveinfo, "pk_leavereg_freezedayorhour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0066")/* @ res"冻结时长"*/);
			} else {
				// 单日哺乳时长
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveinfo, "pk_leavereg_lactationhour", TimeItemCopyVO.TIMEITEMUNIT_HOUR,
						ResHelper.getString("c_ta-res", "0c_ta-res0208")/* @ res"单日哺乳时长"*/);
			}
			
			/** 根据休假类别,设置的哺乳假和非哺乳假页面的显示 */
			ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, "pk_leavereg_leaveyear", !isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, "pk_leavereg_leavemonth", !isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, LeaveoffVO.REGBEGINTIMECOPY, !isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, LeaveoffVO.REGENDTIMECOPY, !isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, LeaveoffVO.REGLEAVEHOURCOPY, !isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, "pk_leavereg_resteddayorhour", !isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, "pk_leavereg_realdayorhour", !isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, "pk_leavereg_restdayorhour", !isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, "pk_leavereg_usefuldayorhour", !isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, "pk_leavereg_freezedayorhour", !isLactationShow);

			ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, LeaveoffVO.REGBEGINDATECOPY, isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, LeaveoffVO.REGENDDATECOPY, isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, "pk_leavereg_lactationholidaytype", isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, "pk_leavereg_lactationhour", isLactationShow);
			
			/** 根据自助是否显示享有,设置的"享有时长"、 "结余时长"和"可用时长"显示 */
			UFBoolean ishrssshow = timeItemCopyVO.getIshrssshow();
			if(ishrssshow != null){
				ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, "pk_leavereg_realdayorhour", ishrssshow.booleanValue());//享有时长
				ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, "pk_leavereg_restdayorhour", ishrssshow.booleanValue());//结余时长
				ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, "pk_leavereg_usefuldayorhour", ishrssshow.booleanValue());//可用时长
			}
		}
		
		FormComp frmleaveoff = (FormComp)view.getViewComponents().getComponent("frmleaveoff");
		if(frmleaveoff != null){
			if(!isLactationShow){
				// 实际休假时长
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveoff, LeaveoffVO.REALLYLEAVEHOUR, timeitemunit,
					ResHelper.getString("c_ta-res", "0c_ta-res0206")/* @ res"实际休假时长"*/);
				// 差异时长
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveoff, LeaveoffVO.DIFFERENCEHOUR, timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0207")/* @ res"差异时长"*/);
			}
			
			ShopLeaveOffUtils.setFormElementVisible(frmleaveoff, LeaveoffVO.LEAVEBEGINTIME, !isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveoff, LeaveoffVO.LEAVEENDTIME, !isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveoff, LeaveoffVO.LEAVEBEGINDATE, isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveoff, LeaveoffVO.LEAVEENDDATE, isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveoff, LeaveoffVO.REALLYLEAVEHOUR, !isLactationShow);
			ShopLeaveOffUtils.setFormElementVisible(frmleaveoff, LeaveoffVO.DIFFERENCEHOUR, !isLactationShow);
			
			ShopLeaveOffUtils.setFormElementNullAble(frmleaveoff, LeaveoffVO.LEAVEBEGINTIME, isLactationShow);
			ShopLeaveOffUtils.setFormElementNullAble(frmleaveoff, LeaveoffVO.LEAVEENDTIME, isLactationShow);
			ShopLeaveOffUtils.setFormElementNullAble(frmleaveoff, LeaveoffVO.LEAVEBEGINDATE, !isLactationShow);
			ShopLeaveOffUtils.setFormElementNullAble(frmleaveoff, LeaveoffVO.LEAVEENDDATE, !isLactationShow);
			
		}

		/** 设置数据集字段不能为空 */
		Dataset dsLeaveOff = view.getViewModels().getDataset(ShopLeaveOffConsts.MAIN_DS_NAME);
		ShopLeaveOffUtils.setDatasettNullAble(dsLeaveOff, LeaveoffVO.LEAVEBEGINTIME, isLactationShow);
		ShopLeaveOffUtils.setDatasettNullAble(dsLeaveOff, LeaveoffVO.LEAVEENDTIME, isLactationShow);
		ShopLeaveOffUtils.setDatasettNullAble(dsLeaveOff, LeaveoffVO.LEAVEBEGINDATE, !isLactationShow);
		ShopLeaveOffUtils.setDatasettNullAble(dsLeaveOff, LeaveoffVO.LEAVEENDDATE, !isLactationShow);
	}
	
	public void onAfterDataChange_dsLeaveOff(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		// 字段顺序
		int colIndex = datasetCellEvent.getColIndex();
		if (colIndex != ds.nameToIndex(LeaveoffVO.LEAVEBEGINTIME)
				&& colIndex != ds.nameToIndex(LeaveoffVO.LEAVEENDTIME)
				&& colIndex != ds.nameToIndex(LeaveoffVO.LEAVEBEGINDATE)
				&& colIndex != ds.nameToIndex(LeaveoffVO.LEAVEENDDATE)) {
			return;
		}
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			return;
		}
		/** 设置差异时长和实际时长 */
		UFBoolean islactation = selRow.getUFBoolean(ds.nameToIndex(LeaveoffVO.ISLACTATION));
		if(islactation != null && UFBoolean.TRUE.equals(islactation)){ //哺乳假
			if(colIndex == ds.nameToIndex(LeaveoffVO.LEAVEBEGINDATE)) {
				/** 根据实际开始日期设置实际开始时间 */
				UFLiteralDate leaveoffbegindate = (UFLiteralDate)selRow.getValue(ds.nameToIndex(LeaveoffVO.LEAVEBEGINDATE));
				if(leaveoffbegindate == null){
					selRow.setValue(ds.nameToIndex(LeaveoffVO.LEAVEBEGINTIME), null);
				}else {
					selRow.setValue(ds.nameToIndex(LeaveoffVO.LEAVEBEGINTIME), getPsnDefaultOnOffDutyTime(leaveoffbegindate,true));
				}
			}			
			if(colIndex == ds.nameToIndex(LeaveoffVO.LEAVEENDDATE)) {
				/** 根据实际结束日期设置实际结束时间 */
				UFLiteralDate leaveoffenddate = (UFLiteralDate)selRow.getValue(ds.nameToIndex(LeaveoffVO.LEAVEENDDATE));
				if(leaveoffenddate == null){
					selRow.setValue(ds.nameToIndex(LeaveoffVO.LEAVEENDTIME), null);
				}else {
					selRow.setValue(ds.nameToIndex(LeaveoffVO.LEAVEENDTIME), getPsnDefaultOnOffDutyTime(leaveoffenddate,false));
				}
			}
			
		} else{
			if(colIndex == ds.nameToIndex(LeaveoffVO.LEAVEBEGINTIME)) {
				/** 根据实际开始时间设置实际开始日期 */
				UFDateTime leaveoffbegintime = (UFDateTime)selRow.getValue(ds.nameToIndex(LeaveoffVO.LEAVEBEGINTIME));
				if(leaveoffbegintime == null){
					selRow.setValue(ds.nameToIndex(LeaveoffVO.LEAVEBEGINDATE), null);
				}else {
					selRow.setValue(ds.nameToIndex(LeaveoffVO.LEAVEBEGINDATE), UFLiteralDate.getDate(leaveoffbegintime.getDate().toString()));
				}
			}
			
			if(colIndex == ds.nameToIndex(LeaveoffVO.LEAVEENDTIME)) {
				/** 根据实际结束时间设置实际结束日期 */
				UFDateTime leaveoffendtime = (UFDateTime)selRow.getValue(ds.nameToIndex(LeaveoffVO.LEAVEENDTIME));
				if(leaveoffendtime == null){
					selRow.setValue(ds.nameToIndex(LeaveoffVO.LEAVEENDDATE), null);
				}else {
					selRow.setValue(ds.nameToIndex(LeaveoffVO.LEAVEENDDATE), UFLiteralDate.getDate(leaveoffendtime.getDate().toString()));
				}
			}
		}
		Datasets2AggVOSerializer serializer = new Datasets2AggVOSerializer();
		AggLeaveoffVO aggVO = (AggLeaveoffVO) serializer.serialize(ds,
				null, AggLeaveoffVO.class.getName());
		AggLeaveoffVO newAggVO = getCalculate(aggVO);
		selRow.setValue(ds.nameToIndex(LeaveoffVO.REALLYLEAVEHOUR),
				newAggVO.getParentVO().getAttributeValue(LeaveoffVO.REALLYLEAVEHOUR));
		selRow.setValue(ds.nameToIndex(LeaveoffVO.DIFFERENCEHOUR),
				 newAggVO.getParentVO().getAttributeValue(LeaveoffVO.DIFFERENCEHOUR));
	}
	
	/**
	 * 取得默认上班时间和下班时间
	 * 
	 * @param date
	 */
	public static UFDateTime getPsnDefaultOnOffDutyTime(UFLiteralDate date, boolean isBegin){
		TimeZone clientTimeZone = Calendar.getInstance().getTimeZone();
		UFDateTime time = new UFDateTime();
		try {
			IPsnCalendarQueryService service = ServiceLocator.lookup(IPsnCalendarQueryService.class);
			String beginTimeStr = service.getPsnDefaultOnOffDutyTime(SessionUtil.getPk_psndoc(), date, clientTimeZone, isBegin);
			time = new UFDateTime(date+" "+beginTimeStr,clientTimeZone);
		} catch (HrssException e) {
			e.alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return time;
	}

	/**
	 * 调用后台计算实际时长和差异时长
	 * 
	 * @param ds
	 * @param dsDetail
	 * @param rowMaster
	 */
	private AggLeaveoffVO getCalculate(AggLeaveoffVO aggvo) {
		AggLeaveoffVO aggVO = null;
		try {
			aggVO = ServiceLocator.lookup(ILeaveOffManageMaintain.class)
					.calculate(aggvo);
		} catch (HrssException e) {
			e.alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return aggVO;
	}
	
	/**
	 * 根据考勤规则设置考勤数据的小时位数
	 * 
	 */
	private static void setTimeDatasPrecision(LfwView viewMain) {
		// 考勤数据
		String[] timeDatas = getTimeDataFields();
		if (timeDatas == null || timeDatas.length == 0) {
			return;
		}
		Dataset[] dss = viewMain.getViewModels().getDatasets();
		if (dss == null || dss.length == 0) {
			return;
		}
		// 考勤位数
		int pointNum = getPointNum();
		for (Dataset ds : dss) {
			if (ds instanceof MdDataset) {
				for (String filedId : timeDatas) {
					int index = ds.getFieldSet().nameToIndex(filedId);
					if (index >= 0) {
						FieldSet fieldSet = ds.getFieldSet();
						Field field = fieldSet.getField(filedId);
						if(field instanceof UnmodifiableMdField) 
							field = ((UnmodifiableMdField) field).getMDField();
						fieldSet.updateField(filedId, field);
						
						field.setPrecision(String.valueOf(pointNum));
						
					}
				}
			}
		}
	}

	/**
	 * 获得考勤位数
	 * 
	 * @return
	 */
	private static int getPointNum() {
		TimeRuleVO timeRuleVO = ShopTaAppContextUtil.getTimeRuleVO();
		if (timeRuleVO == null) {
			// 没有考勤规则的情况，设置默认值
			return ShopTaListBasePageModel.DEFAULT_PRECISION;
		}
		int pointNum = Math.abs(timeRuleVO.getTimedecimal());
		return pointNum;
	}
	/**
	 * 设置考勤数据的小时位数<br/>
	 * String[]待设置的考勤数据字段数组<br/>
	 * 
	 * @return
	 */
	protected static String[] getTimeDataFields() {
		return new String[] { "regleavehourcopy", "reallyleavehour", "differencehour" };
	}
}
