package nc.bs.hrsms.ta.sss.away.ctrl;

import java.util.ArrayList;
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
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.away.lsnr.AwaySaveProcessor;
import nc.itf.hrss.ta.timeapply.IQueryOrgOrDeptVid;
import nc.itf.ta.IAwayRegisterInfoDisplayer;
import nc.itf.ta.IAwayRegisterManageMaintain;
import nc.itf.ta.IAwayRegisterQueryMaintain;
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
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.away.AwayRegVO;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;

public class ShopAwayRegBatchCardView implements IController{

	
	protected String getDatasetId() {
		return "hrtaawayreg";
	}
	/**
	 * beforeShow事件
	 * @param dialogEvent
	 */
	public void onBeforeShow(DialogEvent dialogEvent) {
		Dataset ds = ViewUtil.getDataset(ViewUtil.getCurrentView(),getDatasetId());
		Row row = ds.getEmptyRow();
		
		row.setString(ds.nameToIndex("pk_org"), SessionUtil.getPk_org());
	    row.setString(ds.nameToIndex("pk_group"), SessionUtil.getPk_group());
	    row.setString(ds.nameToIndex("creator"), SessionUtil.getPk_user());
	    // 创建时间
	 	row.setValue(ds.nameToIndex("creationtime"), new UFDateTime());
	 	row.setValue(ds.nameToIndex(AwayRegVO.BILLSOURCE), ICommonConst.BILL_SOURCE_REG);
		row.setValue(ds.nameToIndex(AwayRegVO.AWAYHOUR), UFDouble.ZERO_DBL);
		
		ITimeScope defaultScope = ShopDefaultTimeScope.getDefaultTimeScope(SessionUtil.getPk_org(), null, null, TimeZone.getDefault());
		row.setValue(ds.nameToIndex("awaybegintime"), defaultScope.getScope_start_datetime());
		row.setValue(ds.nameToIndex("awayendtime"), defaultScope.getScope_end_datetime());
		
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
	 * 分页操作标志
	 * 
	 * @param ds
	 * @return
	 */
	private boolean isPagination(Dataset ds) {
		PaginationInfo pg = ds.getCurrentRowSet().getPaginationInfo();
		return pg.getRecordsCount() > 0;
	}
	
	/**
	 * onAfterDataChange事件
	 * 处理值变化
	 * @param datasetCellEvent
	 */
	public void onAfterDataChange(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		// 字段顺序
		int colIndex = datasetCellEvent.getColIndex();
		if(colIndex != ds.nameToIndex(AwayRegVO.PK_AWAYTYPE)
				&&	colIndex != ds.nameToIndex("awaybegintime")
				&&	colIndex != ds.nameToIndex("awayendtime")){
			return;
		}
		Row row = ds.getSelectedRow();
		if(row == null ){
			return;
		}
		if(colIndex == ds.nameToIndex(AwayRegVO.PK_AWAYTYPE)){
			row.setValue(ds.nameToIndex(AwayRegVO.PK_AWAYTYPECOPY), row.getValue(ds.nameToIndex(AwayRegVO.PK_AWAYTYPE)));
		}
		//修改开始时间、结束时间
		//FormComp form = (FormComp) ViewUtil.getCurrentView().getViewComponents().getComponent("headTab_card_awayinf_form");
		if(colIndex == ds.nameToIndex("awayendtime")||colIndex == ds.nameToIndex("awaybegintime")){
			UFDateTime begintime = (UFDateTime) row.getValue(ds.nameToIndex("awaybegintime"));
			UFDateTime endtime = (UFDateTime) row.getValue(ds.nameToIndex("awayendtime"));
			if(begintime.after(endtime)){
				CommonUtil.showErrorDialog("提示", "开始日期不能晚于结束日期，请重新输入！");
			}
			//计算申请时间
//			String awayhour = this.getDurationHour(begintime.toString(), endtime.toString());
//			AppLifeCycleContext.current().getWindowContext().addAppAttribute("awayhour", awayhour);
//			row.setString(ds.nameToIndex("awayhour"), awayhour);
		}
	}
	
	/**
	 * 保存
	 * @param mouseEvent
	 */
	public void onSave(MouseEvent<MenuItem> mouseEvent){
		Dataset ds =  ViewUtil.getCurrentView().getViewModels().getDataset(getDatasetId());
		Row row = ds.getSelectedRow();
		if (row.getValue(ds.nameToIndex(AwayRegVO.PK_AWAYTYPE)) == null) {
			throw new LfwRuntimeException("请先选择出差类别！");
		}
		SuperVO  vo = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds)[0];
		AwayRegVO  regVO = new AwayRegVO();
		String[] names = regVO.getAttributeNames();
		for(int i =0;i<names.length;i++){
			regVO.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
		}
		regVO.setBegindate(new UFLiteralDate(regVO.getAwaybegintime().toString()));
		regVO.setEnddate(new UFLiteralDate(regVO.getAwayendtime().toString()));
		regVO.setAwaybegindate(new UFLiteralDate(regVO.getAwaybegintime().toString()));
		regVO.setAwayenddate(new UFLiteralDate(regVO.getAwayendtime().toString()));
		
		Dataset dsPsn =  ViewUtil.getCurrentView().getViewModels().getDataset("dsPerson");
		Row[] selRow = dsPsn.getAllSelectedRows();
		if(selRow == null || selRow.length==0){
			throw new LfwRuntimeException("请选择人员！");
		}
		//SuperVO[] psnVos = new Dataset2SuperVOSerializer<SuperVO>().serialize(dsPsn, selRow);
		IAwayRegisterManageMaintain service = NCLocator.getInstance().lookup(IAwayRegisterManageMaintain.class);
		List<AwayRegVO> listVO = new ArrayList<AwayRegVO>();
		for(int i=0;i<selRow.length;i++){
			AwayRegVO saveVO = new AwayRegVO();
			String pk_psndoc = (String) selRow[i].getValue(dsPsn.nameToIndex("pk_psndoc"));
			String pk_psnjob = (String) selRow[i].getValue(dsPsn.nameToIndex("pk_psnjob"));
			ShopTaAppContextUtil.addTaAppContext(pk_psndoc);
			TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(regVO.getPk_org(), regVO.getPk_awaytype());
			saveVO.setStatus(VOStatus.NEW);
			saveVO.setPk_awaytype(regVO.getPk_awaytype());
			if (timeItemCopyVO != null) {
				saveVO.setPk_awaytypecopy(timeItemCopyVO.getPk_timeitemcopy());
			}else{
				saveVO.setPk_awaytypecopy(null);
			}
			saveVO.setBillsource(regVO.getBillsource());
			saveVO.setAwaybegindate(regVO.getAwaybegindate());
			saveVO.setAwaybegintime(regVO.getAwaybegintime());
			saveVO.setAwayenddate(regVO.getAwayenddate());
			saveVO.setAwayendtime(regVO.getAwayendtime());
			saveVO.setAwayaddress(regVO.getAwayaddress());
			saveVO.setAwayremark(regVO.getAwayremark());
			saveVO.setCreator(regVO.getCreator());
			saveVO.setCreationtime(regVO.getCreationtime());
			saveVO.setPk_group(regVO.getPk_group());
			saveVO.setPk_org(regVO.getPk_org());
//			saveVO.setAwayhour(regVO.getAwayhour());
			saveVO.setFactfee(regVO.getFactfee());
			saveVO.setAheadfee(regVO.getAheadfee());
			saveVO.setPk_psndoc(pk_psndoc);
			saveVO.setPk_psnjob(pk_psnjob);
			TBMPsndocVO tbmPsndocVO = ShopTaAppContextUtil.getTBMPsndocVO();
			saveVO.setPk_psnorg(tbmPsndocVO.getPk_psnorg());
			List<String> list = getVersionIds(pk_psnjob);
			if (list != null && list.size() > 0) {
				// 人员任职所属业务单元的版本id
				saveVO.setPk_org_v(list.get(0));
				// 人员任职所属部门的版本pk_dept_v
				saveVO.setPk_dept_v(list.get(1));
			}
			try {
				saveVO = (AwayRegVO) getRegAutoDisplaer().calculate(saveVO, TimeZone.getDefault());
			} catch (BusinessException e) {
				e.printStackTrace();
			}
			listVO.add(saveVO);
		}
		AwayRegVO[] vos = listVO.toArray(new AwayRegVO[listVO.size()]);
		try {
			IAwayRegisterQueryMaintain maintain = NCLocator.getInstance().lookup(IAwayRegisterQueryMaintain.class);
			
			// 保存前先校验一次，如果有单据冲突，但是是被允许的，则显示这些冲突单据，并询问用户是否保存
			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkMutextResult = null;
			try {
				checkMutextResult = maintain.check(regVO);
				if (checkMutextResult != null) {
					AwaySaveProcessor
					.showConflictInfoList(
							new BillMutexException(null,
									checkMutextResult),
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("c_ta-res",
											"0c_ta-res0008")/*
															 * @ res
															 * "与下列单据有时间冲突，是否保存?"
															 */,
							ShopTaApplyConsts.DIALOG_CONFIRM);
					return;
				}
			}catch (BillMutexException ex) {
				AwaySaveProcessor.showConflictInfoList(
						((BillMutexException) ex),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"c_ta-res", "0c_ta-res0007")/*
															 * @ res
															 * "与下列单据有时间冲突，操作不能继续"
															 */,
						ShopTaApplyConsts.DIALOG_ALERT);
				return;
				
			}
			//判断考勤期间是否已封存
		//	ShopTaPeriodValUtils.getPeriodVal(SessionUtil.getPk_org(),vos);
			
			service.insertData(vos,false);
			CommonUtil.showShortMessage("保存成功！");
			// 关闭弹出页面
			CmdInvoker.invoke(new CloseWindowCmd());
			// 执行左侧快捷查询
			CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET,"closewindow"));
		} catch (BusinessException e) {
			new HrssException(e.getMessage()).alert();
		}
		
	}
	/**
	 * 获得人员任职所属业务单元/部门的版本id
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
	 * 取消按钮操作
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onCancel(MouseEvent mouseEvent) {
		// 关闭弹出页面
		CmdInvoker.invoke(new CloseWindowCmd());
	}
	
	/**
	 * 根据chuchai类别PK和组织PK, 获得away类别copy的PK
	 * 
	 * @param pk_org
	 * @param pk_leavetype
	 * @return
	 */
	public static TimeItemCopyVO getTimeItemCopyVO(String pk_org, String pk_awaytype) {
		TimeItemCopyVO timeItemCopyVO = null;
		// 查询休假类别copy的PK
		try {
			ITimeItemQueryService service = ServiceLocator.lookup(ITimeItemQueryService.class);
			timeItemCopyVO = service.queryCopyTypesByDefPK(pk_org, pk_awaytype, TimeItemVO.AWAY_TYPE);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return timeItemCopyVO;
	}

	// 信息项自动带出
	private IAwayRegisterInfoDisplayer appAutoDisplayer;
	
	public IAwayRegisterInfoDisplayer getRegAutoDisplaer() {
		if (appAutoDisplayer == null) {
			appAutoDisplayer = NCLocator.getInstance().lookup(
					IAwayRegisterInfoDisplayer.class);
		}
		return appAutoDisplayer;
	}
}
