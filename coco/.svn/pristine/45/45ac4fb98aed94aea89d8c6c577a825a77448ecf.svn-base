package nc.bs.hrsms.ta.sss.away.ctrl;

import java.util.Map;
import java.util.TimeZone;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.sss.common.ShopSetMenuItemVisible;
import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.away.lsnr.AwaySaveProcessor;
import nc.hr.utils.ResHelper;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.ta.IAwayRegisterInfoDisplayer;
import nc.itf.ta.IAwayRegisterManageMaintain;
import nc.itf.ta.IAwayRegisterQueryMaintain;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.away.AwayRegVO;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;

import org.apache.commons.lang.StringUtils;

public class ShopAwayRegCardView implements IController{
	
	// 信息项自动带出
	private IAwayRegisterInfoDisplayer appAutoDisplayer;
	
	protected String getDatasetId() {
		// TODO Auto-generated method stub
		return "hrtaawayreg";
	}
	/**
	 * beforeShow事件
	 * @param dialogEvent
	 */
	public void onBeforeShow(DialogEvent dialogEvent) {
		Dataset ds = ViewUtil.getDataset(ViewUtil.getCurrentView(),getDatasetId());
		
		String pk_awayreg = (String)AppLifeCycleContext.current().getApplicationContext().getAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM);
		AppLifeCycleContext.current().getApplicationContext().removeAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM);
		if(StringUtils.isEmpty(pk_awayreg)){
			Row row = ds.getEmptyRow();
			row.setString(ds.nameToIndex("pk_org"), SessionUtil.getPk_org());
		    row.setString(ds.nameToIndex("pk_group"), SessionUtil.getPk_group());
		    row.setString(ds.nameToIndex("creator"), SessionUtil.getPk_user());
		    // 创建时间
		 	row.setValue(ds.nameToIndex("creationtime"), new UFDateTime());
		 	row.setValue(ds.nameToIndex(AwayRegVO.BILLSOURCE), ICommonConst.BILL_SOURCE_REG);
			row.setValue(ds.nameToIndex(AwayRegVO.AWAYHOUR), UFDouble.ZERO_DBL);
		 	ds.setCurrentKey(Dataset.MASTER_KEY);
			ds.addRow(row);
			ds.setRowSelectIndex(0);
			ds.setEnabled(true);
		}else{
			AwayRegVO vo = getAwayRegVOByPK(pk_awayreg);
			new SuperVO2DatasetSerializer().serialize(new SuperVO[]{vo}, ds, Row.STATE_NORMAL);
			ds.setCurrentKey(Dataset.MASTER_KEY);
			ds.setRowSelectIndex(0);
			if(ICommonConst.BILL_SOURCE_REG != vo.getBillsource().intValue()){
				ShopSetMenuItemVisible.setMenuItemVisible("menu_operate", "btnSave");
				ds.setEnabled(false);
			}else{
				ds.setEnabled(true);
			}
			
			
		}
		
	}
	/**
	 * 根据pk获取AwayRegVO
	 * @param pk
	 * @return VO
	 */
	private AwayRegVO getAwayRegVOByPK(String pk){
		AwayRegVO vo = null;
		try {
			vo = NCLocator.getInstance().lookup(IAwayRegisterQueryMaintain.class).queryByPk(pk);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vo;
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
		if(colIndex != ds.nameToIndex(AwayRegVO.PK_PSNJOB)
				&&	colIndex != ds.nameToIndex(AwayRegVO.PK_AWAYTYPE)
				&&	colIndex != ds.nameToIndex("awaybegintime")
				&&	colIndex != ds.nameToIndex("awayendtime")){
			return;
		}
		Row row = ds.getSelectedRow();
		if(row == null ){
			return;
		}
		if(colIndex == ds.nameToIndex(AwayRegVO.PK_PSNJOB)){
			String pk_psndoc  = (String) row.getValue(ds.nameToIndex("pk_psndoc"));
			ShopTaAppContextUtil.addTaAppForTransferContext(pk_psndoc);
			ShopAwayAfterDataChange.onAfterDataChange(ds, row);
		}
		AwayRegVO  vo =  new Dataset2SuperVOSerializer<AwayRegVO>().serialize(ds)[0];
		if(vo.getPk_psnjob() == null){
			return;
		}
		
//		AwayRegVO vo = new AwayRegVO();
		try {
			vo = (AwayRegVO) getRegAutoDisplaer().calculate(vo, TimeZone.getDefault());
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		row.setValue(ds.nameToIndex("awaybegintime"), vo.getScope_start_datetime());
		row.setValue(ds.nameToIndex("awayendtime"), vo.getScope_end_datetime());
		row.setValue(ds.nameToIndex(AwayRegVO.AWAYHOUR), vo.getAwayhour());
		
	}

	/**
	 * 保存
	 * @param mouseEvent
	 */
	public void onSave(MouseEvent<MenuItem> mouseEvent){
		
		Dataset ds =  ViewUtil.getCurrentView().getViewModels().getDataset(getDatasetId());
		Row row = ds.getSelectedRow();
		//条件判断
		if (row.getValue(ds.nameToIndex(AwayRegVO.PK_PSNJOB)) == null) {
			throw new LfwRuntimeException("请先选择人员！");
		}
		if (row.getValue(ds.nameToIndex(AwayRegVO.PK_AWAYTYPE)) == null) {
			throw new LfwRuntimeException("请先选择加班类别！");
		}
		SuperVO  vo = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds)[0];
		AwayRegVO  regVO = new AwayRegVO();
//		AwayRegVO  regVO =  (AwayRegVO) vo;
		IAwayRegisterManageMaintain service = NCLocator.getInstance().lookup(IAwayRegisterManageMaintain.class);
		try {
			
			String[] names = regVO.getAttributeNames();
			for(int i =0;i<names.length;i++){
				regVO.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
			}
			//考勤档案已经结束的人员新增档案结束日期前的数据时pk_psnorg字段为空，无法保存数据
			PsnJobVO psnjobVO = null;
			try {
				psnjobVO = (PsnJobVO) ServiceLocator.lookup(IPersistenceRetrieve.class).retrieveByPk(PsnJobVO.class, regVO.getPk_psnjob(), null);
			} catch (BusinessException e1) {
				throw new LfwRuntimeException(e1.getMessage());
			} catch (HrssException e1) {
				throw new LfwRuntimeException(e1.getMessage());
			}
			regVO.setPk_psnorg(psnjobVO.getPk_psnorg());
			regVO.setBegindate(new UFLiteralDate(regVO.getAwaybegintime().toString()));
			regVO.setEnddate(new UFLiteralDate(regVO.getAwayendtime().toString()));
			regVO.setAwaybegindate(new UFLiteralDate(regVO.getAwaybegintime().toString()));
			regVO.setAwayenddate(new UFLiteralDate(regVO.getAwayendtime().toString()));
			TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(regVO.getPk_org(), regVO.getPk_awaytype());
			if (timeItemCopyVO != null) {
				regVO.setPk_awaytypecopy(timeItemCopyVO.getPk_timeitemcopy());
			}else{
				regVO.setPk_awaytypecopy(null);
			}
			if(regVO.getAwayhour().doubleValue()<0.001){
				throw new BusinessException(ResHelper.getString("6017overtime","06017overtime0062"/*@res"单据时长不能为0"*/));
			}
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
//			//校验
//			getCheckResult(regVO);
//			//判断考勤期间是否已封存
//			ShopTaPeriodValUtils.getPeriodVal(regVO.getPk_org(), new AwayRegVO[]{regVO});
			
			String pk = regVO.getPrimaryKey();
			if(StringUtils.isEmpty(pk)){
				service.insertData(regVO);
			}else{
				service.updateData(regVO);
			}
			CommonUtil.showShortMessage("保存成功！");
			// 关闭弹出页面
			CmdInvoker.invoke(new CloseWindowCmd());
			// 执行左侧快捷查询
			CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET,"closewindow"));
		} catch (BusinessException e) {
			new HrssException(e.getMessage()).alert();
			//e.printStackTrace();
		}
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
	
	public IAwayRegisterInfoDisplayer getRegAutoDisplaer() {
		if (appAutoDisplayer == null) {
			appAutoDisplayer = NCLocator.getInstance().lookup(
					IAwayRegisterInfoDisplayer.class);
		}
		return appAutoDisplayer;
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
}
