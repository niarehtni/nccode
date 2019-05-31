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
 * �ݼ�����ĵ���Ƭ�ε�Controllor
 * 
 * @author qiaoxp
 */
public class LeaveApplyView extends TaApplyBaseView implements IController {
	private static final long serialVersionUID = 1L;

	/**
	 * ��������
	 * 
	 * @return
	 */
	@Override
	protected String getBillType() {
		return LeaveConsts.BILL_TYPE_CODE;
	}

	/**
	 * ���ݼ�ID
	 * 
	 * @return
	 */
	@Override
	protected String getDatasetId() {
		return LeaveConsts.DS_MAIN_NAME;
	}

	/**
	 * ���ݾۺ�����VO
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends AggregatedValueObject> getAggVoClazz() {
		return LeaveConsts.CLASS_NAME_AGGVO;
	}

	/**
	 * �޸ĵ�PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends IEditProcessor> getEditPrcss() {
		return LeaveEditProcessor.class;
	}

	/**
	 * ���Ƶ�PROCESSOR
	 */
	@Override
	protected Class<? extends ICopyProcessor> getCopyPrcss() {
		return LeaveCopyProcessor.class;
	}

	/**
	 * �����PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ISaveProcessor> getSavePrcss() {
		return LeaveSaveProcessor.class;
	}

	/**
	 * ���沢������PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ISaveProcessor> getSaveAddPrcss() {
		return LeaveSaveAddProcessor.class;
	}

	/**
	 * ��������PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ILineInsertProcessor> getLineAddPrcss() {
		return LeaveLineAddProcessor.class;
	}

	/**
	 * ��ɾ����PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ILineDelProcessor> getLineDelPrcss() {
		return LeaveLineDelProcessor.class;
	}

	/**
	 * ������PROCESSOR
	 */
	@Override
	protected Class<? extends IAddProcessor> getAddPrcss() {
		return LeaveAddProcessor.class;
	}

	public void onDataLoad_hrtaleaveh(DataLoadEvent dataLoadEvent) {
		super.onDataLoad(dataLoadEvent);
		
		Dataset ds = dataLoadEvent.getSource();
		Row masterRow = ds.getSelectedRow();
		// ���뵥��������֯
		String pk_org = masterRow.getString(ds.nameToIndex(LeavehVO.PK_ORG));

		// ����ݼ����PK
		String pk_leavetype = masterRow.getString(ds.nameToIndex(LeavehVO.PK_LEAVETYPE));
		if(StringUtils.isEmpty(pk_leavetype)){
			return;
		}
		TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(pk_org, pk_leavetype);
		LfwView viewMain = ViewUtil.getCurrentView();
		FormComp formComp = (FormComp) viewMain.getViewComponents().getComponent(LeaveConsts.PAGE_FORM_LEAVEINFO);
		setElementVisible(timeItemCopyVO, formComp);
		
		//���    ���á����ݕr�L��,�����Еr�L��,���Y�N�r�L�� 20171027 xw start
		setLeaveDayOrHour(ds,masterRow);
		if(HrssConsts.POPVIEW_OPERATE_COPY.equals(getApplicationContext().getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS))){
			setLeaveTypeChage(viewMain,ds,masterRow);
		}
		//���    ���á����ݕr�L��,�����Еr�L��,���Y�N�r�L�� 20171027 xw end
	}

	/**
	 * �ݼ������ӱ��ֵ�仯�¼�<br/>
	 * 
	 * @param datasetCellEvent
	 */
	public void onAfterDataChage_hrtaleaveb(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		// �ֶ�˳��
		int colIndex = datasetCellEvent.getColIndex();
		// ��ʼʱ��|����ʱ��
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
				// �����˿�ʼ����
				selRow.setValue(ds.nameToIndex(LeavebVO.LEAVEBEGINDATE), new UFLiteralDate(beiginTime.getDate().toString()));
			} else {
				// �����˿�ʼ����
				selRow.setValue(ds.nameToIndex(LeavebVO.LEAVEBEGINDATE), null);
			}
		}
		if (colIndex == ds.nameToIndex(LeavebVO.LEAVEENDTIME)) {
			if (endTime != null) {
				// �����˽�������
				selRow.setValue(ds.nameToIndex(LeavebVO.LEAVEENDDATE), new UFLiteralDate(endTime.getDate().toString()));
			} else {
				// �����˽�������
				selRow.setValue(ds.nameToIndex(LeavebVO.LEAVEENDDATE), null);
			}
		}
		LfwView view = getLifeCycleContext().getViewContext().getView();
		Dataset dsMaster = view.getViewModels().getDataset(LeaveConsts.DS_MAIN_NAME);
		Row rowMaster = dsMaster.getSelectedRow();
		// ���ú�̨����ʱ��
		calculate(dsMaster, ds, rowMaster);
	}

	/**
	 * �ݼ����������ֵ�仯�¼�<br/>
	 * �ݼ�������仯Ӱ��:1.���ڵ�λ2.�ڼ��Ƿ�ɱ༭3.����ʱ�� |����ʱ�� |����ʱ��4.�ݼ���ʱ��5.�ӱ��ݼ�ʱ��<br/>
	 * ��ȷ����仯Ӱ��:1.�ڼ���������ݼ�,2.����ʱ�� |����ʱ�� |����ʱ��<br/>
	 * �ڼ䷢���仯Ӱ��:1.����ʱ�� |����ʱ�� |����ʱ��<br/>
	 * 
	 * @param datasetCellEvent
	 */
	public void onAfterDataChange_hrtaleaveh(DatasetCellEvent datasetCellEvent) {
		LfwView viewMain = getLifeCycleContext().getViewContext().getView();
		Dataset ds = datasetCellEvent.getSource();
		// �ֶ�˳��
		int colIndex = datasetCellEvent.getColIndex();
		if (colIndex != ds.nameToIndex(LeavehVO.LEAVEYEAR) && colIndex != ds.nameToIndex(LeavehVO.LEAVEMONTH) && colIndex != ds.nameToIndex(LeavehVO.PK_LEAVETYPE)) {
			return;
		}
		Row selRow = ds.getSelectedRow();
		if (colIndex == ds.nameToIndex(LeavehVO.PK_LEAVETYPE)) {// �ݼ����
			setLeaveTypeChage(viewMain, ds, selRow);
		} else if (colIndex == ds.nameToIndex(LeavehVO.LEAVEYEAR)) {// ����л�
			setLeaveYearChange(viewMain, ds, selRow);
		} else if (colIndex == ds.nameToIndex(LeavehVO.LEAVEMONTH)) {// �ڼ��л�
			setLeaveMonthChange(viewMain, ds, selRow);
		}
	}

	/**
	 * �ݼ�������仯Ӱ��:<br/>
	 * 1.���ڵ�λ<br/>
	 * 2.�ڼ��Ƿ�ɱ༭<br/>
	 * �������ڼ�����ʱ,�ڼ䲻�ɱ༭;<br/>
	 * δ�������ڼ�����ʱ,�ݼ����Copy�Ľ��㷽ʽ-���½���ʱ,�ڼ�ɱ༭;<br/>
	 * 3.����ʱ�� |����ʱ�� |����ʱ��<br/>
	 * 4.�ݼ���ʱ��<br/>
	 * 5.�ӱ��ݼ�ʱ��<br/>
	 * 
	 * @param ds
	 * @param selRow
	 */
	private void setLeaveTypeChage(LfwView viewMain, Dataset ds, Row selRow) {
		// // ���ÿ��ڵ�λ
		TimeItemCopyVO timeItemCopyVO = setTimeUnitText(ds, selRow);
		// 2.�ڼ��Ƿ�ɱ༭, �������ڼ�����ʱ,�ڼ䲻�ɱ༭;
		// δ�������ڼ�����ʱ,�ݼ����Copy�Ľ��㷽ʽ-���½���ʱ,�ڼ�ɱ༭;
		FormComp formComp = (FormComp) viewMain.getViewComponents().getComponent(LeaveConsts.PAGE_FORM_LEAVEINFO);
		if (formComp == null) {
			return;
		}
		// ���ڹ���
		TimeRuleVO timeRuleVO = TaAppContextUtil.getTimeRuleVO();
		FormElement monthElem = formComp.getElementById(LeavehVO.LEAVEMONTH);
		if (monthElem != null) {
			if (timeRuleVO.isPreHolidayFirst()) {
				// �������ڼ�����ʱ,�ڼ䲻�ɱ༭;
				monthElem.setEnabled(false);
			} else {
				Integer leavesetperiod = null;
				if (timeItemCopyVO != null) {
					// δ�������ڼ�����ʱ,�����ݼ����Copy�Ľ��㷽ʽ����;
					leavesetperiod = timeItemCopyVO.getLeavesetperiod();
				}
				if (leavesetperiod != null && leavesetperiod == TimeItemCopyVO.LEAVESETPERIOD_MONTH) {
					monthElem.setEnabled(true);
				} else {
					monthElem.setEnabled(false);
				}
			}
		}
		
		// �����ݼ������ʾ����������ʱ��
		setElementVisible(timeItemCopyVO, formComp);
		// 3.����ʱ�� |����ʱ�� |����ʱ��
		setLeaveDayOrHour(ds, selRow);

		LfwView view = getLifeCycleContext().getViewContext().getView();
		Dataset dsDetail = view.getViewModels().getDataset(LeaveConsts.DS_SUB_NAME);
		// 4.�ݼ���ʱ����5.�ӱ��ݼ�ʱ��
		RowData rowData = dsDetail.getCurrentRowData();
		if (rowData == null) {
			return;
		}
		Row[] rows = rowData.getRows();
		if (rows == null || rows.length == 0) {
			return;
		}
		// ���ú�̨�����ݼ���ʱ��
		calculate(ds, dsDetail, selRow);
	}
	
	/**
	 * �����ݼ���������ֶ���ʾ
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
	 * ��ȷ����仯<br/>
	 * Ӱ��:1.�ڼ���������ݼ�,2.����ʱ�� |����ʱ�� |����ʱ��<br/>
	 * 
	 * @param viewMain
	 * @param ds
	 * @param selRow
	 */
	private void setLeaveYearChange(LfwView viewMain, Dataset ds, Row selRow) {
		// 1.�ڼ���������ݼ�
		setMonthComboData(viewMain, ds, selRow);
		setDefaultLeaveDayOrHour(ds, selRow);
	}

	/**
	 * �ڼ䷢���仯<br/>
	 * Ӱ��:1.����ʱ�� |����ʱ�� |����ʱ��<br/>
	 * 
	 * @param viewMain
	 * @param ds
	 * @param selRow
	 */
	private void setLeaveMonthChange(LfwView viewMain, Dataset ds, Row selRow) {
		// 2.����ʱ�� |����ʱ�� |����ʱ��
		setLeaveDayOrHour(ds, selRow);
	}

	/**
	 * �����ڼ���������ݼ�
	 * 
	 * @param viewMain
	 * @param ds
	 * @param selRow
	 */
	private void setMonthComboData(LfwView viewMain, Dataset ds, Row selRow) {

		ComboData monthData = viewMain.getViewModels().getComboData(LeaveConsts.WIDGET_COMBODATA_MONTH);
		// �����֯����
		String pk_org = (String) selRow.getValue(ds.nameToIndex(LeavehVO.PK_ORG));
		// ������
		String leaveYear = (String) selRow.getValue(ds.nameToIndex(LeavehVO.LEAVEYEAR));
		Map<String, String[]> periodMap = TBMPeriodUtil.getPeriodMap(pk_org);
		String[] months = null;
		if (periodMap != null && periodMap.size() > 0) {
			months = periodMap.get(leaveYear);
		}
		if (months != null && months.length > 0) {
			ComboDataUtil.addCombItemsAfterClean(monthData, months);
		}
		// �����,Ĭ��ѡ���ڼ�Ϊ��
		selRow.setValue(ds.nameToIndex(LeavehVO.LEAVEMONTH), null);
	}

	/**
	 * ������Ա����,�ݼ����, ���,�ڼ�,<br/>
	 * ��ѯ|ָ�������ڼ���|������Ա|ѡ���ݼ����|�� ����ʱ�� |����ʱ�� |����ʱ��.
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
		// ��������ʱ��,����ʱ��,����ʱ��
		LeaveBalanceVO leaveBalanceVO = getLeaveBalanceVO(leavehVO);
		if (leaveBalanceVO == null) {
			setDefaultLeaveDayOrHour(ds, selRow);
			return;
		}
		// ��������ʱ��
		selRow.setValue(ds.nameToIndex(LeavehVO.REALDAYORHOUR), leaveBalanceVO.getCurdayorhour());
		// ����ʱ��
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTEDDAYORHOUR), leaveBalanceVO.getYidayorhour());
		// ����ʱ��
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTDAYORHOUR), leaveBalanceVO.getRestdayorhour());
		// ����ʱ��
		selRow.setValue(ds.nameToIndex(LeavehVO.FREEZEDAYORHOUR), leaveBalanceVO.getFreezedayorhour());
		// ����ʱ��
		selRow.setValue(ds.nameToIndex(LeavehVO.USEFULDAYORHOUR), new UFDouble(leaveBalanceVO.getUsefulRestdayorhour()));

		// ���ڽ���˳���
		selRow.setValue(ds.nameToIndex(LeavehVO.LEAVEINDEX), leavehVO.getLeaveindex() == null ? 1 : leavehVO.getLeaveindex());

	}

	/**
	 * ���� ����ʱ�� |����ʱ�� |����ʱ����Ĭ��ֵ.
	 * 
	 * @param ds
	 * @param selRow
	 */
	private void setDefaultLeaveDayOrHour(Dataset ds, Row selRow) {
		// ��������ʱ��
		selRow.setValue(ds.nameToIndex(LeavehVO.REALDAYORHOUR), UFDouble.ZERO_DBL);
		// ����ʱ��
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTEDDAYORHOUR), UFDouble.ZERO_DBL);
		// ����ʱ��
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTDAYORHOUR), UFDouble.ZERO_DBL);
		// ����ʱ��
		selRow.setValue(ds.nameToIndex(LeavehVO.FREEZEDAYORHOUR), UFDouble.ZERO_DBL);
		// ����ʱ��
		selRow.setValue(ds.nameToIndex(LeavehVO.USEFULDAYORHOUR), UFDouble.ZERO_DBL);
		// ���ڽ���˳���
		selRow.setValue(ds.nameToIndex(LeavehVO.LEAVEINDEX), 1);
	}

	/**
	 * ��������ʱ��,����ʱ��,����ʱ��
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
	 * ���ÿ��ڵ�������ֶε���ʾ
	 * 
	 * @param view
	 * @param ds
	 * @param selRow
	 */
	public static TimeItemCopyVO setTimeUnitText(Dataset ds, Row masterRow) {

		// ���뵥��������֯
		String pk_org = masterRow.getString(ds.nameToIndex(LeavehVO.PK_ORG));

		// ����ݼ����PK
		String pk_leavetype = masterRow.getString(ds.nameToIndex(LeavehVO.PK_LEAVETYPE));

		TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(pk_org, pk_leavetype);
		Integer timeitemunit = null;
		if (timeItemCopyVO != null) {
			// �����ݼ����copy��PK
			masterRow.setValue(ds.nameToIndex(LeavehVO.PK_LEAVETYPECOPY), timeItemCopyVO.getPk_timeitemcopy());
			timeitemunit = timeItemCopyVO.getTimeitemunit();
		} else {
			// �����ݼ����copy��PK
			masterRow.setValue(ds.nameToIndex(LeavehVO.PK_LEAVETYPECOPY), null);
		}
		// �ݼ���Ϣ
		LfwView view = AppLifeCycleContext.current().getViewContext().getView();
		FormComp form = (FormComp) view.getViewComponents().getComponent("headTab_card_leaveinf_form");
		// �ݼ���ʱ��
		FormElement elem = form.getElementById(LeavehVO.SUMHOUR);
		String text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0062")/*
																										 * @
																										 * res
																										 * "�ݼ���ʱ��"
																										 */;
		setText(timeitemunit, elem, text);
		// ����ʱ��
		elem = form.getElementById(LeavehVO.RESTEDDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0063")/*
																								 * @
																								 * res
																								 * "����ʱ��"
																								 */;
		setText(timeitemunit, elem, text);
		// ����ʱ��
		elem = form.getElementById(LeavehVO.REALDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0064")/*
																								 * @
																								 * res
																								 * "����ʱ��"
																								 */;
		setText(timeitemunit, elem, text);
		// ����ʱ��
		elem = form.getElementById(LeavehVO.RESTDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0065")/*
																								 * @
																								 * res
																								 * "����ʱ��"
																								 */;
		setText(timeitemunit, elem, text);

		// ����ʱ��
		elem = form.getElementById(LeavehVO.FREEZEDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0066")/*
																								 * @
																								 * res
																								 * "����ʱ��"
																								 */;
		setText(timeitemunit, elem, text);

		// ����ʱ��
		elem = form.getElementById(LeavehVO.USEFULDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0067")/*
																								 * @
																								 * res
																								 * "����ʱ��"
																								 */;
		setText(timeitemunit, elem, text);
		return timeItemCopyVO;
	}

	private static void setText(Integer timeitemunit, FormElement elem, String text) {
		if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_DAY == timeitemunit) {// ��
			elem.setLabel(text + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0001")/*
																													 * @
																													 * res
																													 * "(��)"
																													 */);
		} else if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_HOUR == timeitemunit) {// Сʱ
			elem.setLabel(text + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0002")/*
																													 * @
																													 * res
																													 * "(Сʱ)"
																													 */);
		} else {
			elem.setLabel(text);
		}
	}

	/**
	 * �����ݼ����PK����֯PK, ����ݼ����copy��PK
	 * 
	 * @param pk_org
	 * @param pk_leavetype
	 * @return
	 */
	public static TimeItemCopyVO getTimeItemCopyVO(String pk_org, String pk_leavetype) {
		TimeItemCopyVO timeItemCopyVO = null;
		// ��ѯ�ݼ����copy��PK
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
	 * ���ú�̨�����ݼ���ʱ��
	 * 
	 * @param ds
	 * @param dsDetail
	 * @param rowMaster
	 */
	private void calculate(Dataset ds, Dataset dsDetail, Row rowMaster) {
		Datasets2AggVOSerializer serializer = new Datasets2AggVOSerializer();
		// ������ϸʱ��,��ʱ��
		AggLeaveVO aggVO = (AggLeaveVO) serializer.serialize(ds, new Dataset[] { dsDetail }, LeaveConsts.CLASS_NAME_AGGVO.getName());
		// ����ǰ��׼��
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
		/** ���������������� */
		LeavehVO headVO = (LeavehVO) aggVO.getParentVO();
		// �����ݼ���ʱ��
		rowMaster.setValue(ds.nameToIndex(LeavehVO.SUMHOUR), headVO.getSumhour());
		/** ���������ӱ����� */
		LeavebVO[] vos = aggVO.getLeavebVOs();
		new SuperVO2DatasetSerializer().update(vos, dsDetail);
	}

	/**
	 * ����ǰ��׼��
	 * 
	 * @param aggVO
	 */
	private void prepareBeforeCal(AggLeaveVO aggVO) {
		LeavehVO headVO = aggVO.getLeavehVO();
		LeavebVO[] vos = aggVO.getLeavebVOs();
		for (LeavebVO subVO : vos) {
			// ���õ���������֯����
			subVO.setPk_group(headVO.getPk_group());
			// ���õ���������֯����
			subVO.setPk_org(headVO.getPk_org());
			// ������Ա����
			subVO.setPk_psndoc(headVO.getPk_psndoc());
			// ��Ա��ְ����
			subVO.setPk_psnjob(headVO.getPk_psnjob());
			// ��֯��ϵ����
			subVO.setPk_psnorg(headVO.getPk_psnorg());
			// �ݼ����
			subVO.setPk_leavetype(headVO.getPk_leavetype());
			// �ݼ����Copy
			subVO.setPk_leavetypecopy(headVO.getPk_leavetypecopy());
		}

	}

	@Override
	protected String getDetailDsId() {
		return LeaveConsts.DS_SUB_NAME;
	}
	
	/**
	 * �����ҵļ��ڲ�ѯ����
	 * 
	 * @param mouseEvent
	 */
	public void toMyLeaveInfo(MouseEvent<MenuItem> mouseEvent) {
		String app = "/app/LeaveInfoEmpApp?nodecode=E20200907";
		String url = LfwRuntimeEnvironment.getRootPath() + app;
		String title = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("node_ta-res", "w_ta-001146")/** *@res"�ҵ��ݼټ�¼"*/;
		AppLifeCycleContext.current().getApplicationContext()
				.popOuterWindow(url,  title, "80%", "80%", "TYPE_DIALOG", true);
	}
}