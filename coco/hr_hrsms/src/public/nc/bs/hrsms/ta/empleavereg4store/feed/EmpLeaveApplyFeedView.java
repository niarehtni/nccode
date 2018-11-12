package nc.bs.hrsms.ta.empleavereg4store.feed;

import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.empleavereg4store.EmpLeaveRegDataChange;
import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.away.lsnr.AwaySaveProcessor;
import nc.bs.hrss.ta.leave.LeaveConsts;
import nc.bs.hrss.ta.utils.ComboDataUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.itf.ta.ILeaveRegisterManageMaintain;
import nc.itf.ta.ILeaveRegisterQueryMaintain;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
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
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.leave.LeaveConst;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

/**
 * @author renyp
 * @date 2015-5-4
 * @ClassName�������ƣ���Ա�ݼٵǼ� ������ť �ĵ���Ƭ�ε�Controllor
 * @Description����������������
 * 
 */
public class EmpLeaveApplyFeedView implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;


	/**
	 * ���ݼ�ID
	 * 
	 * @return
	 */
	protected String getDatasetId() {
		return "ds_leavereg";
	}

	/**
	 * �ӿ�Ƭ����ص��б����Ĳ�ѯ����
	 * 
	 * @param keys
	 */
	public void pluginReSearch(Map<String, Object> keys){
		// ִ������ݲ�ѯ
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	
	
	/**
	 * beforeShow�¼�
	 * @param dialogEvent
	 */
	public void onBeforeShow(DialogEvent dialogEvent) {
		Dataset ds = ViewUtil.getDataset(ViewUtil.getCurrentView(),getDatasetId());
		String pk_leavereg = (String) AppLifeCycleContext.current().getApplicationContext().getAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM);
		String operate_status = (String) AppLifeCycleContext.current().getApplicationContext().getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
		if(StringUtils.isEmpty(pk_leavereg)){//�����ж�������༭
			Row row = ds.getEmptyRow();
			NCRefNode refNode = (NCRefNode) ViewUtil.getCurrentView().getViewModels().getRefNode("refnode_ds_leavereg_pk_leavetype_timeitemname");
			refNode.setDataListener(ShopLeaveTypeController.class.getName());
			//����Ĭ�ϵ�����Դ
			row.setValue(ds.nameToIndex(LeaveRegVO.BILLSOURCE), 2);
			row.setValue(ds.nameToIndex(LeaveRegVO.PK_ORG),SessionUtil.getPk_org());//����
			row.setValue(ds.nameToIndex(LeaveRegVO.PK_GROUP), SessionUtil.getPk_group());//��֯
			TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(SessionUtil.getPk_org(), LeaveConst.LEAVETYPE_SUCKLE);
			if(timeItemCopyVO != null){
				//�����Ĭ��ֵ
				row.setValue(ds.nameToIndex(LeaveRegVO.PK_LEAVETYPE), LeaveConst.LEAVETYPE_SUCKLE);
			}
			ds.setCurrentKey(Dataset.MASTER_KEY);
			ds.addRow(row);
			ds.setRowSelectIndex(0);
			ds.setEnabled(true);
		}else{
			if(!"sickLeave".equals(operate_status)){
				ILeaveRegisterQueryMaintain service = NCLocator.getInstance().lookup(ILeaveRegisterQueryMaintain.class);
				try {
					LeaveRegVO vo = service.queryByPk(pk_leavereg);
					new SuperVO2DatasetSerializer().serialize(new SuperVO[]{vo}, ds, Row.STATE_NORMAL);
					ds.setCurrentKey(Dataset.MASTER_KEY);
					ds.setRowSelectIndex(0);
					Row row =  ds.getSelectedRow();
					//ͨ��������Դ���ֿɷ��޸�
					if(2==vo.getBillsource()){
						ds.setEnabled(true);
						row.setValue(ds.nameToIndex(LeaveRegVO.BILLSOURCE), 2);
					}else{//���ò���ʾ���水ť
						LfwView view = ViewUtil.getCurrentView();
						MenuItem saveItem = view.getViewMenus().getMenuBar("menu_operate").getItem("btnSave");
						saveItem.setVisible(false);
					}
				} catch (BusinessException e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}
			}else{
				ILeaveRegisterQueryMaintain service = NCLocator.getInstance().lookup(ILeaveRegisterQueryMaintain.class);
				try {
					LeaveRegVO vo = service.queryByPk(pk_leavereg);
					new SuperVO2DatasetSerializer().serialize(new SuperVO[]{vo}, ds, Row.STATE_NORMAL);
					ds.setCurrentKey(Dataset.MASTER_KEY);
					ds.setRowSelectIndex(0);
					Row row =  ds.getSelectedRow();
					row.setValue(ds.nameToIndex("oldleavebegintime"),row.getValue(ds.nameToIndex(LeaveRegVO.LEAVEBEGINTIME)));
					row.setValue(ds.nameToIndex("oldleaveendtime"),row.getValue(ds.nameToIndex(LeaveRegVO.LEAVEENDTIME)));
					ds.setEnabled(true);
				} catch (BusinessException e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}
			}
		}
		

	}
	
	public void onDataLoad_hrtaleavereg(DataLoadEvent dataLoadEvent) {
		
	}
	
	/**
	 * �����¼�
	 * 
	 * @param mouseEvent
	 */
	public void onSave(MouseEvent<MenuItem> mouseEvent) {
		Dataset ds =  ViewUtil.getCurrentView().getViewModels().getDataset(getDatasetId());
		Row row = ds.getSelectedRow();
		String operate_status = (String) AppLifeCycleContext.current().getApplicationContext().getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
		//�����ж�
		if (row.getValue(ds.nameToIndex(LeaveRegVO.PK_PSNDOC)) == null) {
			throw new LfwRuntimeException("����ѡ����Ա��");
		}
		if (row.getValue(ds.nameToIndex(LeaveRegVO.PK_LEAVETYPE)) == null) {
			throw new LfwRuntimeException("����ѡ���ݼ����");
		}
		if (row.getValue(ds.nameToIndex(LeaveRegVO.LEAVEBEGINDATE)) == null) {
			throw new LfwRuntimeException("����ѡ���ݼٿ�ʼ���ڣ�");
		}
		if (row.getValue(ds.nameToIndex(LeaveRegVO.LEAVEENDDATE)) == null) {
			throw new LfwRuntimeException("����ѡ���ݼٽ������ڣ�");
		}
		SuperVO  vo = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds)[0];
		LeaveRegVO leaveRegVO = new LeaveRegVO();
		String[] names = leaveRegVO.getAttributeNames();
		for(int i =0;i<names.length;i++){
			leaveRegVO.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
		}
		leaveRegVO.setLeavebegintime(new UFDateTime(leaveRegVO.getLeavebegindate().toDate()));
		leaveRegVO.setLeaveendtime(new UFDateTime(leaveRegVO.getLeaveenddate().toDate()));
		leaveRegVO.setIslactation(UFBoolean.TRUE);
		leaveRegVO.setLeaveyear(null);
		if(leaveRegVO.getLeavehour() == null){
			leaveRegVO.setLeavehour(UFDouble.ZERO_DBL);
		}
		//���ڵ����Ѿ���������Ա����������������ǰ������ʱpk_psnorg�ֶ�Ϊ�գ��޷���������
		PsnJobVO psnjobVO = null;
		try {
			psnjobVO = (PsnJobVO) ServiceLocator.lookup(IPersistenceRetrieve.class).retrieveByPk(PsnJobVO.class, leaveRegVO.getPk_psnjob(), null);
		} catch (BusinessException e1) {
			throw new LfwRuntimeException(e1.getMessage());
		} catch (HrssException e1) {
			throw new LfwRuntimeException(e1.getMessage());
		}
		leaveRegVO.setPk_psnorg(psnjobVO.getPk_psnorg());
		TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(leaveRegVO.getPk_org(), leaveRegVO.getPk_leavetype());
		if (timeItemCopyVO != null) {
			// �����ݼ����copy��PK
			leaveRegVO.setPk_leavetypecopy(timeItemCopyVO.getPk_timeitemcopy());
		} else {
			// �����ݼ����copy��PK
			leaveRegVO.setPk_leavetypecopy(null);
		}
		ILeaveRegisterManageMaintain service = NCLocator.getInstance().lookup(ILeaveRegisterManageMaintain.class);
		try {
			// ����ǰ��У��һ�Σ�����е��ݳ�ͻ�������Ǳ�����ģ�����ʾ��Щ��ͻ���ݣ���ѯ���û��Ƿ񱣴�
						Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkMutextResult = null;
						try {
							checkMutextResult = ServiceLocator.lookup(ILeaveRegisterQueryMaintain.class).check(leaveRegVO);
							if (checkMutextResult != null) {
								AwaySaveProcessor
								.showConflictInfoList(
										new BillMutexException(null,
												checkMutextResult),
										nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
												.getStrByID("c_ta-res",
														"0c_ta-res0008")/*
																		 * @ res
																		 * "�����е�����ʱ���ͻ���Ƿ񱣴�?"
																		 */,
										ShopTaApplyConsts.DIALOG_CONFIRM);
								return;
							}
						} catch (HrssException e) {
							e.alert();
						}catch (BillMutexException ex) {
							AwaySaveProcessor.showConflictInfoList(
									((BillMutexException) ex),
									nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
											"c_ta-res", "0c_ta-res0007")/*
																		 * @ res
																		 * "�����е�����ʱ���ͻ���������ܼ���"
																		 */,
									ShopTaApplyConsts.DIALOG_ALERT);
							return;
						}
			
//			service.insertData(new LeaveRegVO[]{leaveRegVO} ,false);
			if("".equals(leaveRegVO.getPk_leavereg())||null==leaveRegVO.getPk_leavereg()){
				service.insertData(new LeaveRegVO[]{leaveRegVO} ,false);
				CommonUtil.showShortMessage("����ɹ���");
			}else{
				if("sickLeave".equals(operate_status)){//�ж��Ƿ�����
					leaveRegVO.setIsleaveoff(UFBoolean.TRUE);
				}
				service.updateData(leaveRegVO);
				CommonUtil.showShortMessage("���ٳɹ���");
			}
			CommonUtil.showShortMessage("����ɹ���");
			// �رյ���ҳ��
			CmdInvoker.invoke(new CloseWindowCmd());
			// ִ������ݲ�ѯ
			CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET,"closewindow"));
		} catch (BusinessException e) {
			e.printStackTrace();
			new HrssException(e.getMessage()).alert();
		}
		
	}

	
	/**
	 * ȡ����ť����
	 */
	public void onCancel(MouseEvent<MenuItem> mouseEvent) {
		// �رյ���ҳ��
		CmdInvoker.invoke(new CloseWindowCmd());
	}
	
	/**
	 * �������棬��Ա����ı��¼�
	 * 
	 * @param datasetCellEvent
	 */
	public void onAfterDataChage_ds_leavereg(DatasetCellEvent datasetCellEvent) {
		LfwView viewMain = ViewUtil.getCurrentView();
		Dataset ds = datasetCellEvent.getSource();
		// �ֶ�˳��
		int colIndex = datasetCellEvent.getColIndex();
		if (colIndex != ds.nameToIndex(LeaveRegVO.LEAVEYEAR) 
				&& colIndex != ds.nameToIndex(LeaveRegVO.LEAVEMONTH) 
				&& colIndex != ds.nameToIndex(LeaveRegVO.PK_LEAVETYPE) && colIndex != ds.nameToIndex("pk_psnjob")) {
			return;
		}
		Row selRow = ds.getSelectedRow();
		if(colIndex == ds.nameToIndex("pk_psnjob")){
			String pk_psndoc =  (String) selRow.getValue(ds.nameToIndex("pk_psndoc"));
			// ��applicationContext��������Կ��ڵ����Ϳ��ڹ���
			ShopTaAppContextUtil.addTaAppForTransferContext(pk_psndoc);
			EmpLeaveRegDataChange.onAfterDataChange(ds, selRow);
		}else if (colIndex == ds.nameToIndex(LeaveRegVO.PK_LEAVETYPE)) {// �ݼ����
			setLeaveTypeChage(viewMain, ds, selRow);
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
		// 2.�ڼ��Ƿ�ɱ༭, �������ڼ�����ʱ,�ڼ䲻�ɱ༭;
		// δ�������ڼ�����ʱ,�ݼ����Copy�Ľ��㷽ʽ-���½���ʱ,�ڼ�ɱ༭;
		FormComp formComp = (FormComp) viewMain.getViewComponents().getComponent(LeaveConsts.PAGE_FORM_LEAVEINFO);
		if (formComp == null) {
			return;
		}
		// ���ڹ���
		TimeRuleVO timeRuleVO = TaAppContextUtil.getTimeRuleVO();
		FormElement monthElem = formComp.getElementById(LeaveRegVO.LEAVEMONTH);
		if (monthElem != null) {
			if (timeRuleVO.isPreHolidayFirst()) {
				// �������ڼ�����ʱ,�ڼ䲻�ɱ༭;
				monthElem.setEnabled(false);
			} else {
				monthElem.setEnabled(true);
			}
		}
		
		// 3.����ʱ�� |����ʱ�� |����ʱ��
		setLeaveDayOrHour(ds, selRow);

		LfwView view = ViewUtil.getCurrentView();
		Dataset dsDetail = view.getViewModels().getDataset(getDatasetId());
		// 4.�ݼ���ʱ����5.�ӱ��ݼ�ʱ��
		RowData rowData = dsDetail.getCurrentRowData();
		if (rowData == null) {
			return;
		}
		Row[] rows = rowData.getRows();
		if (rows == null || rows.length == 0) {
			return;
		}
		
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
	 * �����ڼ���������ݼ�
	 * 
	 * @param viewMain
	 * @param ds
	 * @param selRow
	 */
	@SuppressWarnings("unused")
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
		if (StringUtils.isEmpty(leaveYear) || StringUtils.isEmpty(leaveMonth)) {
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
		selRow.setValue(ds.nameToIndex(LeavehVO.USEFULDAYORHOUR), new UFDouble(leaveBalanceVO.getUsefulRestDayOrHour()));

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

	@SuppressWarnings("deprecation")
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
}