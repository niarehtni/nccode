package nc.bs.hrsms.ta.sss.overtime.ctrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.common.ctrl.TBMQueryPsnJobVOUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrsms.ta.sss.common.ShopTaPeriodValUtils;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.away.lsnr.AwaySaveProcessor;
import nc.bs.logging.Logger;
import nc.itf.hrss.ta.timeapply.IQueryOrgOrDeptVid;
import nc.itf.ta.IOvertimeApplyQueryMaintain;
import nc.itf.ta.IOvertimeRegisterInfoDisplayer;
import nc.itf.ta.IOvertimeRegisterManageMaintain;
import nc.itf.ta.IOvertimeRegisterQueryMaintain;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.PaginationInfo;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.overtime.OvertimeCommonVO;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;
import nc.vo.util.remotecallcombination.IRemoteCallCombinatorService;
import nc.vo.util.remotecallcombination.RemoteCallInfo;
import nc.vo.util.remotecallcombination.RemoteCallResult;

import org.apache.commons.lang.StringUtils;

public class ShopOverTimeRegBatchCardView implements IController{

	protected String getDatasetId() {
		// TODO Auto-generated method stub
		return "hrtaovertimereg";
	}
	/**
	 * beforeShow�¼�
	 * @param dialogEvent
	 */
	public void onBeforeShow(DialogEvent dialogEvent) {
		Dataset ds = ViewUtil.getDataset(ViewUtil.getCurrentView(),getDatasetId());
		Row row = ds.getEmptyRow();
		
		row.setString(ds.nameToIndex("pk_org"), SessionUtil.getPk_org());
	    row.setString(ds.nameToIndex("pk_group"), SessionUtil.getPk_group());
	    row.setString(ds.nameToIndex("creator"), SessionUtil.getPk_user());
	    // ����ʱ��
	 	row.setValue(ds.nameToIndex("creationtime"), new UFDateTime());
		row.setValue(ds.nameToIndex("deduct"), new Integer(0));
		row.setValue(ds.nameToIndex("overtimehour"), UFDouble.ZERO_DBL);
		row.setValue(ds.nameToIndex("acthour"), UFDouble.ZERO_DBL);
		row.setValue(ds.nameToIndex("diffhour"), UFDouble.ZERO_DBL);
		row.setValue(ds.nameToIndex("billsource"), ICommonConst.BILL_SOURCE_REG);
		row.setValue(ds.nameToIndex("ischeck"), UFBoolean.FALSE);
		row.setValue(ds.nameToIndex(OvertimeRegVO.TORESTHOUR), UFDouble.ZERO_DBL);
		
		ITimeScope defaultScope = ShopDefaultTimeScope.getDefaultTimeScope(SessionUtil.getPk_org(), null, null, TimeZone.getDefault());
		row.setValue(ds.nameToIndex("overtimebegintime"), defaultScope.getScope_start_datetime());
		row.setValue(ds.nameToIndex("overtimeendtime"), defaultScope.getScope_end_datetime());
		
		ds.setCurrentKey(Dataset.MASTER_KEY);
		ds.addRow(row);
		ds.setRowSelectIndex(0);
		ds.setEnabled(true);
	}
	
	public void onDatasetLoad_dsPerson(DataLoadEvent dataLoadEvent){
		
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
		SuperVO[] vos = DatasetUtil.paginationMethod(dsPsn, psnjobVOs);//psnJobList.toArray(new PsnJobVO[0]));
		new SuperVO2DatasetSerializer().serialize(vos, dsPsn, Row.STATE_NORMAL);
		
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
	 * onAfterDataChange�¼�
	 * ����ֵ�仯
	 * @param datasetCellEvent
	 */
	public void onAfterDataChange(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		// �ֶ�˳��
		int colIndex = datasetCellEvent.getColIndex();
		if(colIndex != ds.nameToIndex(OvertimeRegVO.PK_OVERTIMETYPE)
				&&	colIndex != ds.nameToIndex("overtimebegintime")
				&&	colIndex != ds.nameToIndex("overtimeendtime")
				&&	colIndex != ds.nameToIndex("deduct")
				&&  colIndex != ds.nameToIndex("isneedcheck")){
			return;
		}
		Row row = ds.getSelectedRow();
		if(row == null ){
			return;
		}
		//�޸Ŀ�ʼʱ�䡢����ʱ��
		//FormComp form = (FormComp) ViewUtil.getCurrentView().getViewComponents().getComponent("headTab_card_awayinf_form");
		if(colIndex == ds.nameToIndex("overtimebegintime")||colIndex == ds.nameToIndex("overtimeendtime")){
			UFDateTime begintime = (UFDateTime) row.getValue(ds.nameToIndex("overtimebegintime"));
			UFDateTime endtime = (UFDateTime) row.getValue(ds.nameToIndex("overtimeendtime"));
			if(begintime.after(endtime)){
				CommonUtil.showErrorDialog("��ʾ", "��ʼ���ڲ������ڽ������ڣ����������룡");
			}
		}
		// �޸����Ƿ�����У��
		if(colIndex == ds.nameToIndex("isneedcheck")){
			UFBoolean isCheck = (UFBoolean) row.getValue(ds.nameToIndex("isneedcheck"));
			boolean isNeedCheck  = isCheck.booleanValue();
			//����޸�Ϊ������У������Ҫ�ж�
			if(!isNeedCheck)
				return;
			SuperVO  vo =  new Dataset2SuperVOSerializer<SuperVO>().serialize(ds)[0];
			OvertimeRegVO  regVO = new OvertimeRegVO();
			String[] names = regVO.getAttributeNames();
			for(int i =0;i<names.length;i++){
				regVO.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
			}
			if(regVO.getPk_overtimetype()==null||regVO.getOvertimebegintime()==null||regVO.getOvertimeendtime()==null){
				row.setValue(ds.nameToIndex("isneedcheck"), UFBoolean.FALSE);
				return;
			}
			//���ú�̨�ж��Ƿ��У��
			isNeedCheck = false;
			try {
				isNeedCheck = NCLocator.getInstance().lookup(IOvertimeApplyQueryMaintain.class).isCanCheck(regVO);
			} catch (BusinessException e1) {
				Logger.error(e1.getMessage(), e1);
			}
			row.setValue(ds.nameToIndex("isneedcheck"), UFBoolean.valueOf(isNeedCheck));
		}
	}
	
  
	/**
	 * ����
	 * @param mouseEvent
	 */
	public void onSave(MouseEvent<MenuItem> mouseEvent){
		Dataset ds =  ViewUtil.getCurrentView().getViewModels().getDataset(getDatasetId());
		Row row = ds.getSelectedRow();
		if (row.getValue(ds.nameToIndex(OvertimeRegVO.PK_OVERTIMETYPE)) == null) {
			throw new LfwRuntimeException("����ѡ��Ӱ����");
		}
		SuperVO  vo = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds)[0];
		OvertimeRegVO  regVO = new OvertimeRegVO();
		String[] names = regVO.getAttributeNames();
		for(int i =0;i<names.length;i++){
			regVO.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
		}
		regVO.setBegindate(new UFLiteralDate(regVO.getOvertimebegintime().toString()));
		regVO.setEnddate(new UFLiteralDate(regVO.getOvertimeendtime().toString()));
		regVO.setOvertimebegindate(new UFLiteralDate(regVO.getOvertimebegintime().toString()));
		regVO.setOvertimeenddate(new UFLiteralDate(regVO.getOvertimeendtime().toString()));
		
		Dataset dsPsn =  ViewUtil.getCurrentView().getViewModels().getDataset("dsPerson");
		Row[] selRow = dsPsn.getAllSelectedRows();
		if(selRow == null || selRow.length==0){
			throw new LfwRuntimeException("��ѡ����Ա��");
		}
		//SuperVO[] psnVos = new Dataset2SuperVOSerializer<SuperVO>().serialize(dsPsn, selRow);
		IOvertimeRegisterManageMaintain service = NCLocator.getInstance().lookup(IOvertimeRegisterManageMaintain.class);
		List<OvertimeRegVO> listVO = new ArrayList<OvertimeRegVO>();
		for(int i=0;i<selRow.length;i++){
			OvertimeRegVO saveVO = new OvertimeRegVO();
			String pk_psndoc = (String) selRow[i].getValue(dsPsn.nameToIndex("pk_psndoc"));
			String pk_psnjob = (String) selRow[i].getValue(dsPsn.nameToIndex("pk_psnjob"));
			ShopTaAppContextUtil.addTaAppContext(pk_psndoc);
			saveVO.setPk_overtimetype(regVO.getPk_overtimetype());
			TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(regVO.getPk_org(), regVO.getPk_overtimetype());
			if (timeItemCopyVO != null) {
				saveVO.setPk_timeitem(timeItemCopyVO.getPk_timeitem());
				saveVO.setPk_overtimetypecopy(timeItemCopyVO.getPk_timeitemcopy());
			}else{
				saveVO.setPk_timeitem(null);
				saveVO.setPk_overtimetypecopy(null);
			}
			saveVO.setIscheck(regVO.getIscheck());
			saveVO.setIsneedcheck(regVO.getIsneedcheck());
			saveVO.setIstorest(regVO.getIstorest());
			saveVO.setToresthour(regVO.getToresthour());
			saveVO.setBegindate(regVO.getBegindate());
			saveVO.setEnddate(regVO.getEnddate());
			saveVO.setOvertimebegindate(regVO.getOvertimebegindate());
			saveVO.setOvertimebegintime(regVO.getOvertimebegintime());
			saveVO.setOvertimeenddate(regVO.getOvertimeenddate());
			saveVO.setOvertimeendtime(regVO.getOvertimeendtime());
			saveVO.setDeduct(regVO.getDeduct());
			saveVO.setOvertimeremark(regVO.getOvertimeremark());
			saveVO.setBillsource(regVO.getBillsource());
			saveVO.setPk_psndoc(pk_psndoc);
			saveVO.setPk_psnjob(pk_psnjob);
			saveVO.setCreator(regVO.getCreator());
			saveVO.setCreationtime(regVO.getCreationtime());
			saveVO.setPk_group(regVO.getPk_group());
			saveVO.setPk_org(regVO.getPk_org());
			TBMPsndocVO tbmPsndocVO = ShopTaAppContextUtil.getTBMPsndocVO();
			saveVO.setPk_psnorg(tbmPsndocVO.getPk_psnorg());
			List<String> list = getVersionIds(pk_psnjob);
			if (list != null && list.size() > 0) {
				// ��Ա��ְ����ҵ��Ԫ�İ汾id
				saveVO.setPk_org_v(list.get(0));
				// ��Ա��ְ�������ŵİ汾pk_dept_v
				saveVO.setPk_dept_v(list.get(1));
			}
			try {
				saveVO = (OvertimeRegVO) getInfoDisplayer().calculate(saveVO, TimeZone.getDefault());
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			listVO.add(saveVO);
		}
		OvertimeRegVO[] vos = listVO.toArray(new OvertimeRegVO[listVO.size()]);
		try {
			// ����ǰ��У��һ�Σ�����е��ݳ�ͻ�������Ǳ�����ģ�����ʾ��Щ��ͻ���ݣ���ѯ���û��Ƿ񱣴�
			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkMutextResult = null;
			try {
				checkMutextResult = ServiceLocator.lookup(IOvertimeRegisterQueryMaintain.class).check(SessionUtil.getPk_org(),vos);
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
			getCheckResult(SessionUtil.getPk_org(),vos);
			//�жϿ����ڼ��Ƿ��ѷ��
			ShopTaPeriodValUtils.getPeriodVal(SessionUtil.getPk_org(),vos);
			service.insertData(vos,false);
			CommonUtil.showShortMessage("����ɹ���");
			// �رյ���ҳ��
			CmdInvoker.invoke(new CloseWindowCmd());
			// ִ������ݲ�ѯ
			CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET,"closewindow"));
		} catch (BusinessException e) {
			new HrssException(e.getMessage()).alert();
		}
		
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
	/**
	 * ȡ����ť����
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onCancel(MouseEvent mouseEvent) {
		// �رյ���ҳ��
		CmdInvoker.invoke(new CloseWindowCmd());
	}
	
	/**
	 * 
	 * @param regvo
	 * @throws BusinessException
	 */
	private  void getCheckResult(String pk_org,OvertimeRegVO[] regvo) throws BusinessException{
		List<RemoteCallInfo> remoteList = new ArrayList<RemoteCallInfo>();
		//ʱ��У��
		RemoteCallInfo checkLengthRemote = new RemoteCallInfo();
		checkLengthRemote.setClassName(IOvertimeApplyQueryMaintain.class.getName());
		checkLengthRemote.setMethodName("checkOvertimeLength");
		checkLengthRemote.setParamTypes(new Class[]{String.class, OvertimeCommonVO[].class});
		checkLengthRemote.setParams(new Object[]{pk_org, regvo});
		remoteList.add(checkLengthRemote);
		
		//У���ʶ
		RemoteCallInfo checkFlag = new RemoteCallInfo();
		checkFlag.setClassName(IOvertimeApplyQueryMaintain.class.getName());
		checkFlag.setMethodName("checkIsNeed");
		checkFlag.setParamTypes(new Class[]{String.class, OvertimeCommonVO[].class});
		checkFlag.setParams(new Object[]{pk_org, regvo});
		remoteList.add(checkFlag);
		
		//�����յļӰ����ͺͼӰ������ļӰ����͵�һ����У��
		RemoteCallInfo checkHolidayRemote = new RemoteCallInfo();
		checkHolidayRemote.setClassName(IOvertimeApplyQueryMaintain.class.getName());
		checkHolidayRemote.setMethodName("checkOverTimeHolidayMsg");
		checkHolidayRemote.setParamTypes(new Class[]{String.class, OvertimeCommonVO[].class});
		checkHolidayRemote.setParams(new Object[]{pk_org, regvo});
		remoteList.add(checkHolidayRemote);
		//���ִ��
		List<RemoteCallResult> returnList = NCLocator.getInstance().lookup(IRemoteCallCombinatorService.class).doRemoteCall(remoteList);
		if(returnList.isEmpty())
			return;
		RemoteCallResult[] returns = returnList.toArray(new RemoteCallResult[0]);
		
		String checkLength = (String) returns[0].getResult();
		if(!StringUtils.isBlank(checkLength)){
			throw new LfwRuntimeException(checkLength);
		}
		String checkFlagReslut = (String)returns[0].getResult();
		if(!StringUtils.isBlank(checkFlagReslut)){
			throw new LfwRuntimeException(checkFlagReslut);
		}
		String holidayResult = (String) returns[2].getResult();
		if(holidayResult!=null){
			throw new LfwRuntimeException(holidayResult);
		}
	}
	
	/**
	 * ���ݼӰ����PK����֯PK, ��üӰ����copy��PK
	 * 
	 * @param pk_org
	 * @param pk_leavetype
	 * @return
	 */
	public static TimeItemCopyVO getTimeItemCopyVO(String pk_org, String pk_overtimetype) {
		TimeItemCopyVO timeItemCopyVO = null;
		// ��ѯ�ݼ����copy��PK
		try {
			ITimeItemQueryService service = ServiceLocator.lookup(ITimeItemQueryService.class);
			timeItemCopyVO = service.queryCopyTypesByDefPK(pk_org, pk_overtimetype, TimeItemVO.OVERTIME_TYPE);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return timeItemCopyVO;
	}
	
	private IOvertimeRegisterInfoDisplayer regInfoDisplayer;
	
	public IOvertimeRegisterInfoDisplayer getInfoDisplayer() {
		if(regInfoDisplayer==null)
			regInfoDisplayer = NCLocator.getInstance().lookup(IOvertimeRegisterInfoDisplayer.class);
		return regInfoDisplayer;
	}
}
