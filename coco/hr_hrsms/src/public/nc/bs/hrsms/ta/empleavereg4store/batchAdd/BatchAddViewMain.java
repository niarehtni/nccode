package nc.bs.hrsms.ta.empleavereg4store.batchAdd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.common.ctrl.TBMQueryPsnJobVOUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrsms.ta.sss.overtime.ctrl.ShopDefaultTimeScope;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.away.lsnr.AwaySaveProcessor;
import nc.hr.utils.ResHelper;
import nc.itf.hrss.ta.timeapply.IQueryOrgOrDeptVid;
import nc.itf.ta.ILeaveRegisterInfoDisplayer;
import nc.itf.ta.ILeaveRegisterManageMaintain;
import nc.itf.ta.ILeaveRegisterQueryMaintain;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.PaginationInfo;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;

import org.apache.commons.lang.ArrayUtils;

public class BatchAddViewMain implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	/**
	 * beforeShow�¼�
	 * 
	 * @param dialogEvent
	 */
	public void onBeforeShow(DialogEvent dialogEvent) {
		Dataset ds = ViewUtil.getDataset(ViewUtil.getCurrentView(), "ds_leavereg");
		Row row = ds.getEmptyRow();
		// ����Ĭ�ϵ�����Դ
		row.setValue(ds.nameToIndex(LeaveRegVO.BILLSOURCE), 2);
		row.setValue(ds.nameToIndex(LeaveRegVO.PK_ORG), SessionUtil.getPk_org());// ����
		row.setValue(ds.nameToIndex(LeaveRegVO.PK_GROUP), SessionUtil.getPk_group());// ��֯
		ITimeScope defaultScope = ShopDefaultTimeScope.getDefaultTimeScope(SessionUtil.getPk_org(), null, null,
				TimeZone.getDefault());
		row.setValue(ds.nameToIndex(LeaveRegVO.LEAVEBEGINTIME), defaultScope.getScope_start_datetime());
		row.setValue(ds.nameToIndex(LeaveRegVO.LEAVEENDTIME), defaultScope.getScope_end_datetime());

		ds.setCurrentKey(Dataset.MASTER_KEY);
		ds.addRow(row);
		ds.setRowSelectIndex(0);
		ds.setEnabled(true);

	}

	/**
	 * �ӿ�Ƭ����ص��б����Ĳ�ѯ����
	 * 
	 * @param keys
	 */
	public void pluginReSearch(Map<String, Object> keys) {
		// ִ������ݲ�ѯ
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	/**
	 * ��Ա���ݼ������¼�
	 * 
	 * @param dataLoadEvent
	 */
	public void onDatasetLoad_dsPerson(DataLoadEvent dataLoadEvent) {
		/* �ſ��¼�����Ȩ�ޣ��г����е�ǰ�����ż��¼����ŵ���Ա */
		PsnJobVO[] psnjobVOs = TBMQueryPsnJobVOUtil.getPsnJobs();

		if (null == psnjobVOs || psnjobVOs.length == 0) {
			return;
		}
		Dataset dsPsn = ViewUtil.getDataset(ViewUtil.getCurrentView(), "dsPerson");
		if (!isPagination(dsPsn)) {
			DatasetUtil.clearData(dsPsn);
			dsPsn.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		}
		dsPsn.setVoMeta(PsnJobVO.class.getName());
		SuperVO[] vos = DatasetUtil.paginationMethod(dsPsn, psnjobVOs);// psnJobList.toArray(new
																		// PsnJobVO[0]));
		new SuperVO2DatasetSerializer().serialize(vos, dsPsn, Row.STATE_NORMAL);
	}

	/**
	 * 
	 * �ݼ�������
	 * 
	 * @param dataLoadEvent
	 */
	public void onAfterDataChage_ds_leavereg(DatasetCellEvent datasetCellEvent) {

	}

	/**
	 * ��ҳ������־
	 * 
	 * @param ds
	 * @return
	 */
	private boolean isPagination(Dataset ds) {
		PaginationInfo pg = ds.getCurrentRowSet().getPaginationInfo();
		return pg.getRecordsCount() > 0;
	}

	/**
	 * ȷ��
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onSave(MouseEvent mouseEvent) {
		LfwView wdtMain = ViewUtil.getCurrentView();
		/* ��ȡ���İ��������Ϣ */
		Dataset dsClass = wdtMain.getViewModels().getDataset("ds_leavereg");
		HashMap<String, Object> value = DatasetUtil.getValueMap(dsClass);
		UFDateTime begin = (UFDateTime) value.get(LeaveRegVO.LEAVEBEGINTIME);
		UFDateTime end = (UFDateTime) value.get(LeaveRegVO.LEAVEENDTIME);
		// String description = (String) value.get(LeaveRegVO.LEAVEREMARK);
		// UFBoolean byOriClass = (UFBoolean)
		// value.get(BatchChangeShiftPageModel.FLD_BY_ORI_CLASS);
		// String oriClass = (String)
		// value.get(BatchChangeShiftPageModel.FLD_ORI_CLASS);
		// String newClass = (String)
		// value.get(BatchChangeShiftPageModel.FLD_NEW_CLASS);
		// У��
		if (begin == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0169"),
					ResHelper.getString("c_ta-res", "0c_ta-res0017")/*
																	 * @ res
																	 * "��ʼ���ڲ���Ϊ�գ�"
																	 */);
		}
		if (end == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0169"), nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0018")/*
																			 * @
																			 * res
																			 * "�������ڲ���Ϊ�գ�"
																			 */);
		}
		if (begin.after(end)) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0169"), nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0019")/*
																			 * @
																			 * res
																			 * "��ʼ���ڲ������ڽ������ڣ�"
																			 */);
		}
		Dataset dsPsn = wdtMain.getViewModels().getDataset("dsPerson");

		// ѡ�е���Ա
		Row[] psnRows = dsPsn.getSelectedRows();
		// ��ԱID List
		// List<String> pk_psndocs = new ArrayList<String>();
		if (ArrayUtils.isEmpty(psnRows)) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0169"), nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("c_ta-res", "�ݼ���Ա������Ϊ��")/*
																		 * @ res
																		 * "������Ա�Ͳ��Ų��ܶ�Ϊ�գ�"
																		 */);
		}

		LeaveRegVO leaveRegVO = new Dataset2SuperVOSerializer<LeaveRegVO>().serialize(dsClass)[0];
		TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(leaveRegVO.getPk_org(), leaveRegVO.getPk_leavetype());
		if (timeItemCopyVO != null) {
			// �����ݼ����copy��PK
			leaveRegVO.setPk_leavetypecopy(timeItemCopyVO.getPk_timeitemcopy());
		} else {
			// �����ݼ����copy��PK
			leaveRegVO.setPk_leavetypecopy(null);
		}
		// List<LeaveRegVO> lrvList = new ArrayList<LeaveRegVO>();
		LeaveRegVO[] vos = new LeaveRegVO[psnRows.length];
		ILeaveRegisterInfoDisplayer displayer = NCLocator.getInstance().lookup(ILeaveRegisterInfoDisplayer.class);
		// ѡ�е���Ա
		try {
			if (!ArrayUtils.isEmpty(psnRows)) {
				for (int i = 0; i < psnRows.length; i++) {
					LeaveRegVO lvo = new LeaveRegVO();
					lvo.setBillsource(leaveRegVO.getBillsource());
					lvo.setScope_start_datetime(leaveRegVO.getScope_start_datetime());
					lvo.setScope_end_datetime(leaveRegVO.getScope_end_datetime());
					lvo.setPk_group(leaveRegVO.getPk_group());
					lvo.setPk_timeitem(leaveRegVO.getPk_timeitem());
					lvo.setLeavebegintime(leaveRegVO.getLeavebegintime());
					lvo.setLeaveendtime(leaveRegVO.getLeaveendtime());
					lvo.setPk_org(leaveRegVO.getPk_org());
					lvo.setIslactation(leaveRegVO.getIslactation());
					lvo.setIsleaveoff(leaveRegVO.getIsleaveoff());
					lvo.setLeaveindex(leaveRegVO.getLeaveindex());
					lvo.setDr(leaveRegVO.getDr());
					lvo.setPk_leavetype(leaveRegVO.getPk_leavetype());
					lvo.setLeaveremark(leaveRegVO.getLeaveremark());
					lvo.setPk_psndoc((String) psnRows[i].getValue(dsPsn.nameToIndex(PsnJobVO.PK_PSNDOC)));
					lvo.setPk_psnjob((String) psnRows[i].getValue(dsPsn.nameToIndex(PsnJobVO.PK_PSNJOB)));
					ShopTaAppContextUtil.addTaAppContext((String) psnRows[i].getValue(dsPsn
							.nameToIndex(PsnJobVO.PK_PSNDOC)));
					TBMPsndocVO tbmPsndocVO = ShopTaAppContextUtil.getTBMPsndocVO();
					lvo.setPk_psnorg(tbmPsndocVO.getPk_psnorg());
					// lvo.setLeavehour(new UFDouble("1.00"));
					lvo.setLeaveyear(String.valueOf(new UFLiteralDate()));
					lvo.setPk_leavetypecopy(leaveRegVO.getPk_leavetypecopy());
					lvo = displayer.calculate(lvo, TimeZone.getDefault());
					List<String> list = getVersionIds((String) psnRows[i].getValue(dsPsn
							.nameToIndex(PsnJobVO.PK_PSNJOB)));
					if (list != null && list.size() > 0) {
						// ��Ա��ְ����ҵ��Ԫ�İ汾id
						lvo.setPk_org_v(list.get(0));
						// ��Ա��ְ�������ŵİ汾pk_dept_v
						lvo.setPk_dept_v(list.get(1));
					}
					vos[i] = lvo;
				}
			}
			ILeaveRegisterManageMaintain service = NCLocator.getInstance().lookup(ILeaveRegisterManageMaintain.class);
			// ����ǰ��У��һ�Σ�����е��ݳ�ͻ�������Ǳ�����ģ�����ʾ��Щ��ͻ���ݣ���ѯ���û��Ƿ񱣴�
			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkMutextResult = null;
			try {
				checkMutextResult = ServiceLocator.lookup(ILeaveRegisterQueryMaintain.class).check(
						SessionUtil.getPk_org(), vos);
				if (checkMutextResult != null) {
					AwaySaveProcessor.showConflictInfoList(new BillMutexException(null, checkMutextResult),
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0008")/*
																											 * @
																											 * res
																											 * "�����е�����ʱ���ͻ���Ƿ񱣴�?"
																											 */,
							ShopTaApplyConsts.DIALOG_CONFIRM);
					return;
				}
			} catch (HrssException e) {
				e.alert();
			} catch (BillMutexException ex) {
				AwaySaveProcessor.showConflictInfoList(((BillMutexException) ex), nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0007")/*
																				 * @
																				 * res
																				 * "�����е�����ʱ���ͻ���������ܼ���"
																				 */, ShopTaApplyConsts.DIALOG_ALERT);
				return;
			}

			service.insertArrayData(vos);
			// �رյ���ҳ��
			CmdInvoker.invoke(new CloseWindowCmd());
			// ִ������ݲ�ѯ
			CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET, "closewindow"));
		} catch (BusinessException e) {
			e.printStackTrace();
		}

	}

	/**
	 * ȡ��
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onCancel(MouseEvent mouseEvent) {
		AppLifeCycleContext.current().getApplicationContext().closeWinDialog();
	}

	protected AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
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
	 * �����Ա��ְ����ҵ��Ԫ/���ŵİ汾id
	 * 
	 * @param pk_psnjob
	 * @return
	 */
	private static List<String> getVersionIds(String pk_psnjob) {
		List<String> list = null;
		IQueryOrgOrDeptVid service;
		try {
			service = ServiceLocator.lookup(IQueryOrgOrDeptVid.class);
			list = service.getOrgOrDeptVidByPsnjob(pk_psnjob);
		} catch (HrssException ex) {
			ex.alert();
		} catch (BusinessException ex) {
			new HrssException(ex).deal();
		}
		return list;
	}
}
