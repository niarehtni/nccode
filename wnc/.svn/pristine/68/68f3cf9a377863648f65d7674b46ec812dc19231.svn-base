package nc.bs.hrss.ta.leaveoff.ctrl;

import java.util.Calendar;
import java.util.TimeZone;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.common.ctrl.TaApplyBaseView;
import nc.bs.hrss.ta.leaveoff.LeaveOffUtils;
import nc.bs.hrss.ta.leaveoff.prcss.LeaveOffAddProcessor;
import nc.bs.hrss.ta.leaveoff.prcss.LeaveOffSaveProcessor;
import nc.bs.hrss.ta.utils.TAUtil;
import nc.hr.utils.ResHelper;
import nc.itf.hrss.pub.cmd.prcss.IAddProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineInsertProcessor;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.itf.ta.ILeaveOffManageMaintain;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.ViewComponents;
import nc.uap.lfw.core.page.ViewModels;
import nc.uap.lfw.core.serializer.impl.Datasets2AggVOSerializer;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leaveoff.AggLeaveoffVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;

public class LeaveOffApplyView extends TaApplyBaseView {
	public static final String DS_ID = "dsLeaveOff";

	public void onDataLoad_dsLeaveOff(DataLoadEvent dataLoadEvent) {
		super.onDataLoad(dataLoadEvent);
		Dataset ds = (Dataset) dataLoadEvent.getSource();
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			return;
		}

		String pk_leavetype = selRow.getString(ds.nameToIndex("pk_leavetype"));
		String pk_org = selRow.getString(ds.nameToIndex("pk_org"));
		TimeItemCopyVO timeItemCopyVO = TAUtil.getTimeItemCopyVO(pk_org,
				pk_leavetype);
		Integer timeitemunit = timeItemCopyVO.getTimeitemunit();

		LfwView view = ViewUtil.getCurrentView();
		FormComp frmleaveinfo = (FormComp) view.getViewComponents()
				.getComponent("frmleaveinfo");

		boolean isLactationShow = "1002Z710000000021ZM3".equals(pk_leavetype);
		if (frmleaveinfo != null) {
			if (!isLactationShow) {
				LeaveOffUtils.setFormElemTextByUnit(frmleaveinfo,
						"regleavehourcopy", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0205"));

				LeaveOffUtils.setFormElemTextByUnit(frmleaveinfo,
						"pk_leavereg_resteddayorhour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0063"));

				LeaveOffUtils.setFormElemTextByUnit(frmleaveinfo,
						"pk_leavereg_realdayorhour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0064"));

				LeaveOffUtils.setFormElemTextByUnit(frmleaveinfo,
						"pk_leavereg_restdayorhour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0065"));

				LeaveOffUtils.setFormElemTextByUnit(frmleaveinfo,
						"pk_leavereg_usefuldayorhour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0067"));

				LeaveOffUtils.setFormElemTextByUnit(frmleaveinfo,
						"pk_leavereg_freezedayorhour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0066"));
			} else {
				LeaveOffUtils.setFormElemTextByUnit(frmleaveinfo,
						"pk_leavereg_lactationhour", Integer.valueOf(1),
						ResHelper.getString("c_ta-res", "0c_ta-res0208"));
			}

			LeaveOffUtils.setFormElementVisible(frmleaveinfo,
					"pk_leavereg_leaveyear", !isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveinfo,
					"pk_leavereg_leavemonth", !isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveinfo,
					"regbegintimecopy", !isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveinfo, "regendtimecopy",
					!isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveinfo,
					"regleavehourcopy", !isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveinfo,
					"pk_leavereg_resteddayorhour", !isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveinfo,
					"pk_leavereg_realdayorhour", !isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveinfo,
					"pk_leavereg_restdayorhour", !isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveinfo,
					"pk_leavereg_usefuldayorhour", !isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveinfo,
					"pk_leavereg_freezedayorhour", !isLactationShow);

			LeaveOffUtils.setFormElementVisible(frmleaveinfo,
					"regbegindatecopy", isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveinfo, "regenddatecopy",
					isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveinfo,
					"pk_leavereg_lactationholidaytype", isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveinfo,
					"pk_leavereg_lactationhour", isLactationShow);

			UFBoolean ishrssshow = timeItemCopyVO.getIshrssshow();
			if (ishrssshow != null) {
				LeaveOffUtils.setFormElementVisible(frmleaveinfo,
						"pk_leavereg_realdayorhour", ishrssshow.booleanValue());
				LeaveOffUtils.setFormElementVisible(frmleaveinfo,
						"pk_leavereg_restdayorhour", ishrssshow.booleanValue());
				LeaveOffUtils.setFormElementVisible(frmleaveinfo,
						"pk_leavereg_usefuldayorhour",
						ishrssshow.booleanValue());
			}
		}

		FormComp frmleaveoff = (FormComp) view.getViewComponents()
				.getComponent("frmleaveoff");
		if (frmleaveoff != null) {
			if (!isLactationShow) {
				LeaveOffUtils.setFormElemTextByUnit(frmleaveoff,
						"reallyleavehour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0206"));

				LeaveOffUtils.setFormElemTextByUnit(frmleaveoff,
						"differencehour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0207"));
			}

			LeaveOffUtils.setFormElementVisible(frmleaveoff, "leavebegintime",
					!isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveoff, "leaveendtime",
					!isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveoff, "leavebegindate",
					isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveoff, "leaveenddate",
					isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveoff, "reallyleavehour",
					!isLactationShow);
			LeaveOffUtils.setFormElementVisible(frmleaveoff, "differencehour",
					!isLactationShow);

			LeaveOffUtils.setFormElementNullAble(frmleaveoff, "leavebegintime",
					isLactationShow);
			LeaveOffUtils.setFormElementNullAble(frmleaveoff, "leaveendtime",
					isLactationShow);
			LeaveOffUtils.setFormElementNullAble(frmleaveoff, "leavebegindate",
					!isLactationShow);
			LeaveOffUtils.setFormElementNullAble(frmleaveoff, "leaveenddate",
					!isLactationShow);
		}

		timeItemCopyVO.getPk_timeitemcopy();
		
		if("1002Z710000000021ZM1".equals(pk_leavetype)){
			frmleaveoff.getElementById("leavebegintime").setEditable(false);
			frmleaveoff.getElementById("leaveendtime").setEditable(false);
		}else{
			frmleaveoff.getElementById("leavebegintime").setEditable(true);
			frmleaveoff.getElementById("leaveendtime").setEditable(true);
		}

		Dataset dsLeaveOff = view.getViewModels().getDataset("dsLeaveOff");
		LeaveOffUtils.setDatasettNullAble(dsLeaveOff, "leavebegintime",
				isLactationShow);
		LeaveOffUtils.setDatasettNullAble(dsLeaveOff, "leaveendtime",
				isLactationShow);
		LeaveOffUtils.setDatasettNullAble(dsLeaveOff, "leavebegindate",
				!isLactationShow);
		LeaveOffUtils.setDatasettNullAble(dsLeaveOff, "leaveenddate",
				!isLactationShow);
	}

	public void onAfterDataChange_dsLeaveOff(DatasetCellEvent datasetCellEvent) {
		Dataset ds = (Dataset) datasetCellEvent.getSource();

		int colIndex = datasetCellEvent.getColIndex();
		if ((colIndex != ds.nameToIndex("leavebegintime"))
				&& (colIndex != ds.nameToIndex("leaveendtime"))
				&& (colIndex != ds.nameToIndex("leavebegindate"))
				&& (colIndex != ds.nameToIndex("leaveenddate"))) {

			return;
		}
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			return;
		}

		UFBoolean islactation = selRow.getUFBoolean(ds
				.nameToIndex("islactation"));
		if ((islactation != null) && (UFBoolean.TRUE.equals(islactation))) {
			if (colIndex == ds.nameToIndex("leavebegindate")) {
				UFLiteralDate leaveoffbegindate = (UFLiteralDate) selRow
						.getValue(ds.nameToIndex("leavebegindate"));
				if (leaveoffbegindate == null) {
					selRow.setValue(ds.nameToIndex("leavebegintime"), null);
				} else {
					selRow.setValue(ds.nameToIndex("leavebegintime"),
							getPsnDefaultOnOffDutyTime(leaveoffbegindate, true));
				}
			}
			if (colIndex == ds.nameToIndex("leaveenddate")) {
				UFLiteralDate leaveoffenddate = (UFLiteralDate) selRow
						.getValue(ds.nameToIndex("leaveenddate"));
				if (leaveoffenddate == null) {
					selRow.setValue(ds.nameToIndex("leaveendtime"), null);
				} else {
					selRow.setValue(ds.nameToIndex("leaveendtime"),
							getPsnDefaultOnOffDutyTime(leaveoffenddate, false));
				}
			}
		} else {
			if (colIndex == ds.nameToIndex("leavebegintime")) {
				UFDateTime leaveoffbegintime = (UFDateTime) selRow.getValue(ds
						.nameToIndex("leavebegintime"));
				if (leaveoffbegintime == null) {
					selRow.setValue(ds.nameToIndex("leavebegindate"), null);
				} else {
					selRow.setValue(ds.nameToIndex("leavebegindate"),
							UFLiteralDate.getDate(leaveoffbegintime.getDate()
									.toString()));
				}
			}

			if (colIndex == ds.nameToIndex("leaveendtime")) {
				UFDateTime leaveoffendtime = (UFDateTime) selRow.getValue(ds
						.nameToIndex("leaveendtime"));
				if (leaveoffendtime == null) {
					selRow.setValue(ds.nameToIndex("leaveenddate"), null);
				} else {
					selRow.setValue(ds.nameToIndex("leaveenddate"),
							UFLiteralDate.getDate(leaveoffendtime.getDate()
									.toString()));
				}
			}
		}
		Datasets2AggVOSerializer serializer = new Datasets2AggVOSerializer();
		AggLeaveoffVO aggVO = (AggLeaveoffVO) serializer.serialize(ds, null,
				AggLeaveoffVO.class.getName());

		AggLeaveoffVO newAggVO = getCalculate(aggVO);
		selRow.setValue(ds.nameToIndex("reallyleavehour"), newAggVO
				.getParentVO().getAttributeValue("reallyleavehour"));

		selRow.setValue(ds.nameToIndex("differencehour"), newAggVO
				.getParentVO().getAttributeValue("differencehour"));
	}

	public static UFDateTime getPsnDefaultOnOffDutyTime(UFLiteralDate date,
			boolean isBegin) {
		TimeZone clientTimeZone = Calendar.getInstance().getTimeZone();
		UFDateTime time = new UFDateTime();
		try {
			IPsnCalendarQueryService service = (IPsnCalendarQueryService) ServiceLocator
					.lookup(IPsnCalendarQueryService.class);
			String beginTimeStr = service.getPsnDefaultOnOffDutyTime(
					SessionUtil.getPk_psndoc(), date, clientTimeZone, isBegin);
			time = new UFDateTime(date + " " + beginTimeStr, clientTimeZone);
		} catch (HrssException e) {
			e.alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return time;
	}

	private AggLeaveoffVO getCalculate(AggLeaveoffVO aggvo) {
		AggLeaveoffVO aggVO = null;
		try {
			aggVO = ((ILeaveOffManageMaintain) ServiceLocator
					.lookup(ILeaveOffManageMaintain.class)).calculate(aggvo);
		} catch (HrssException e) {
			e.alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return aggVO;
	}

	protected String getBillType() {
		return "6406";
	}

	protected String getDatasetId() {
		return "dsLeaveOff";
	}

	protected Class<? extends AggregatedValueObject> getAggVoClazz() {
		return AggLeaveoffVO.class;
	}

	protected String getDetailDsId() {
		return null;
	}

	protected Class<? extends IAddProcessor> getAddPrcss() {
		return LeaveOffAddProcessor.class;
	}

	protected Class<? extends ISaveProcessor> getSavePrcss() {
		return LeaveOffSaveProcessor.class;
	}

	protected Class<? extends ISaveProcessor> getSaveAddPrcss() {
		return null;
	}

	protected Class<? extends ILineInsertProcessor> getLineAddPrcss() {
		return null;
	}
}