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
	 * ������PROCESSOR
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
	 * �����ݼ��ļ����¼�
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
		// ���ݿ��ڹ������ÿ������ݵ�Сʱλ��
		setTimeDatasPrecision(view);
		FormComp frmleaveinfo = (FormComp)view.getViewComponents().getComponent("frmleaveinfo");
		// �������ʾ��־
		boolean isLactationShow = TimeItemCopyVO.LEAVETYPE_LACTATION.equals(pk_leavetype);
		if(frmleaveinfo != null){
			/** ���ݿ��ڵ�λ����Lable��"��"��"Сʱ"����ʾ */
			if(!isLactationShow){
				// �ݼ�ʱ��
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveinfo, LeaveoffVO.REGLEAVEHOURCOPY, timeitemunit,
					ResHelper.getString("c_ta-res", "0c_ta-res0205")/* @ res"�ݼ�ʱ��"*/);
				// ����ʱ��
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveinfo, "pk_leavereg_resteddayorhour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0063")/* @ res"����ʱ��"*/);
				// ����ʱ��
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveinfo, "pk_leavereg_realdayorhour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0064")/* @ res"����ʱ��"*/);
				// ����ʱ��
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveinfo, "pk_leavereg_restdayorhour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0065")/* @ res"����ʱ��"*/);
				// ����ʱ��
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveinfo, "pk_leavereg_usefuldayorhour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0067")/* @ res"����ʱ��"*/);
				// ����ʱ��
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveinfo, "pk_leavereg_freezedayorhour", timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0066")/* @ res"����ʱ��"*/);
			} else {
				// ���ղ���ʱ��
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveinfo, "pk_leavereg_lactationhour", TimeItemCopyVO.TIMEITEMUNIT_HOUR,
						ResHelper.getString("c_ta-res", "0c_ta-res0208")/* @ res"���ղ���ʱ��"*/);
			}
			
			/** �����ݼ����,���õĲ���ٺͷǲ����ҳ�����ʾ */
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
			
			/** ���������Ƿ���ʾ����,���õ�"����ʱ��"�� "����ʱ��"��"����ʱ��"��ʾ */
			UFBoolean ishrssshow = timeItemCopyVO.getIshrssshow();
			if(ishrssshow != null){
				ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, "pk_leavereg_realdayorhour", ishrssshow.booleanValue());//����ʱ��
				ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, "pk_leavereg_restdayorhour", ishrssshow.booleanValue());//����ʱ��
				ShopLeaveOffUtils.setFormElementVisible(frmleaveinfo, "pk_leavereg_usefuldayorhour", ishrssshow.booleanValue());//����ʱ��
			}
		}
		
		FormComp frmleaveoff = (FormComp)view.getViewComponents().getComponent("frmleaveoff");
		if(frmleaveoff != null){
			if(!isLactationShow){
				// ʵ���ݼ�ʱ��
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveoff, LeaveoffVO.REALLYLEAVEHOUR, timeitemunit,
					ResHelper.getString("c_ta-res", "0c_ta-res0206")/* @ res"ʵ���ݼ�ʱ��"*/);
				// ����ʱ��
				ShopLeaveOffUtils.setFormElemTextByUnit(frmleaveoff, LeaveoffVO.DIFFERENCEHOUR, timeitemunit,
						ResHelper.getString("c_ta-res", "0c_ta-res0207")/* @ res"����ʱ��"*/);
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

		/** �������ݼ��ֶβ���Ϊ�� */
		Dataset dsLeaveOff = view.getViewModels().getDataset(ShopLeaveOffConsts.MAIN_DS_NAME);
		ShopLeaveOffUtils.setDatasettNullAble(dsLeaveOff, LeaveoffVO.LEAVEBEGINTIME, isLactationShow);
		ShopLeaveOffUtils.setDatasettNullAble(dsLeaveOff, LeaveoffVO.LEAVEENDTIME, isLactationShow);
		ShopLeaveOffUtils.setDatasettNullAble(dsLeaveOff, LeaveoffVO.LEAVEBEGINDATE, !isLactationShow);
		ShopLeaveOffUtils.setDatasettNullAble(dsLeaveOff, LeaveoffVO.LEAVEENDDATE, !isLactationShow);
	}
	
	public void onAfterDataChange_dsLeaveOff(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		// �ֶ�˳��
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
		/** ���ò���ʱ����ʵ��ʱ�� */
		UFBoolean islactation = selRow.getUFBoolean(ds.nameToIndex(LeaveoffVO.ISLACTATION));
		if(islactation != null && UFBoolean.TRUE.equals(islactation)){ //�����
			if(colIndex == ds.nameToIndex(LeaveoffVO.LEAVEBEGINDATE)) {
				/** ����ʵ�ʿ�ʼ��������ʵ�ʿ�ʼʱ�� */
				UFLiteralDate leaveoffbegindate = (UFLiteralDate)selRow.getValue(ds.nameToIndex(LeaveoffVO.LEAVEBEGINDATE));
				if(leaveoffbegindate == null){
					selRow.setValue(ds.nameToIndex(LeaveoffVO.LEAVEBEGINTIME), null);
				}else {
					selRow.setValue(ds.nameToIndex(LeaveoffVO.LEAVEBEGINTIME), getPsnDefaultOnOffDutyTime(leaveoffbegindate,true));
				}
			}			
			if(colIndex == ds.nameToIndex(LeaveoffVO.LEAVEENDDATE)) {
				/** ����ʵ�ʽ�����������ʵ�ʽ���ʱ�� */
				UFLiteralDate leaveoffenddate = (UFLiteralDate)selRow.getValue(ds.nameToIndex(LeaveoffVO.LEAVEENDDATE));
				if(leaveoffenddate == null){
					selRow.setValue(ds.nameToIndex(LeaveoffVO.LEAVEENDTIME), null);
				}else {
					selRow.setValue(ds.nameToIndex(LeaveoffVO.LEAVEENDTIME), getPsnDefaultOnOffDutyTime(leaveoffenddate,false));
				}
			}
			
		} else{
			if(colIndex == ds.nameToIndex(LeaveoffVO.LEAVEBEGINTIME)) {
				/** ����ʵ�ʿ�ʼʱ������ʵ�ʿ�ʼ���� */
				UFDateTime leaveoffbegintime = (UFDateTime)selRow.getValue(ds.nameToIndex(LeaveoffVO.LEAVEBEGINTIME));
				if(leaveoffbegintime == null){
					selRow.setValue(ds.nameToIndex(LeaveoffVO.LEAVEBEGINDATE), null);
				}else {
					selRow.setValue(ds.nameToIndex(LeaveoffVO.LEAVEBEGINDATE), UFLiteralDate.getDate(leaveoffbegintime.getDate().toString()));
				}
			}
			
			if(colIndex == ds.nameToIndex(LeaveoffVO.LEAVEENDTIME)) {
				/** ����ʵ�ʽ���ʱ������ʵ�ʽ������� */
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
	 * ȡ��Ĭ���ϰ�ʱ����°�ʱ��
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
	 * ���ú�̨����ʵ��ʱ���Ͳ���ʱ��
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
	 * ���ݿ��ڹ������ÿ������ݵ�Сʱλ��
	 * 
	 */
	private static void setTimeDatasPrecision(LfwView viewMain) {
		// ��������
		String[] timeDatas = getTimeDataFields();
		if (timeDatas == null || timeDatas.length == 0) {
			return;
		}
		Dataset[] dss = viewMain.getViewModels().getDatasets();
		if (dss == null || dss.length == 0) {
			return;
		}
		// ����λ��
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
	 * ��ÿ���λ��
	 * 
	 * @return
	 */
	private static int getPointNum() {
		TimeRuleVO timeRuleVO = ShopTaAppContextUtil.getTimeRuleVO();
		if (timeRuleVO == null) {
			// û�п��ڹ�������������Ĭ��ֵ
			return ShopTaListBasePageModel.DEFAULT_PRECISION;
		}
		int pointNum = Math.abs(timeRuleVO.getTimedecimal());
		return pointNum;
	}
	/**
	 * ���ÿ������ݵ�Сʱλ��<br/>
	 * String[]�����õĿ��������ֶ�����<br/>
	 * 
	 * @return
	 */
	protected static String[] getTimeDataFields() {
		return new String[] { "regleavehourcopy", "reallyleavehour", "differencehour" };
	}
}
