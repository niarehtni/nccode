package nc.bs.hrsms.ta.sss.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.cmd.PFApproveInfoCmd;
import nc.bs.hrss.pub.cmd.PFCommitCmd;
import nc.bs.hrss.pub.cmd.PFDeleteCmd;
import nc.bs.hrss.pub.cmd.PFReCallCmd;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.cmd.base.HrssBillCommand;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.ta.common.ctrl.BaseController;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.hr.utils.ResHelper;
import nc.itf.hrss.pub.cmd.prcss.ICommitProcessor;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.MDPersistenceService;
import nc.uap.lfw.core.bm.ButtonStateManager;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Datasets2AggVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.ta.pub.TAPFBillQueryParams;

/**
 * ViewList的父类
 * 
 * @author shaochj
 * @date May 8, 2015
 * 
 */
public abstract class ShopTaListBaseView extends BaseController {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	/**
	 * 单据类型编码
	 * 
	 * @return
	 */
	protected abstract String getBillTypeCode();

	/**
	 * 主数据集ID
	 * 
	 * @return
	 */
	protected abstract String getDatasetId();

	/**
	 * 提交的操作类
	 * 
	 * @return
	 */
	protected Class<? extends ICommitProcessor> getCommitPrcss() {
		return null;
	}

	/**
	 * 聚合VO
	 * 
	 * @return
	 */
	protected abstract Class<? extends AggregatedValueObject> getAggVOClazz();

	/**
	 * 查询条件的主实体Class
	 * 
	 * @return
	 */
	protected abstract Class<? extends SuperVO> getMainEntityClazz();

	/**
	 * 查询结果
	 * 
	 * @return
	 */
	protected abstract AggregatedValueObject[] getAggVOs(FromWhereSQL fromWhereSQL);

	/**
	 * 弹出页面的WindowId
	 * 
	 * @return
	 */
	protected abstract String getPopWindowId();

	/**
	 * 获得页面标题
	 * 
	 * @param operateflag
	 * @return
	 */
	protected abstract String getPopWindowTitle(String operateflag);

	/**
	 * 数据加载事件
	 * 
	 * @param dataLoadEvent
	 * @throws BusinessException 
	 */
	protected void onDataLoad(DataLoadEvent dataLoadEvent) throws BusinessException {
//		((ILFWLicenceCheck)NCLocator.getInstance().lookup(ILFWLicenceCheck.class)).checkLicence(ShopTaApplyConsts.PRODUCTNAME);
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) getApplicationContext().getAppAttribute(ShopTaApplyConsts.APP_ATTR_FROMWHERESQL);
		if (fromWhereSQL == null) {// 初始化操作
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		} else {
			Dataset ds = dataLoadEvent.getSource();
			getTaApplyDatas(fromWhereSQL, ds);
		}
	}

	/**
	 * 关闭弹出页面后的再查询操作
	 * 
	 * @param keys
	 */
	public void pluginReSearch(Map<String, Object> keys) {
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) getApplicationContext().getAppAttribute(ShopTaApplyConsts.APP_ATTR_FROMWHERESQL);
		Dataset ds = getCurrentView().getViewModels().getDataset(getDatasetId());
		getTaApplyDatas(fromWhereSQL, ds);
	}
	
	/**
	 * 部门切换事件
	 * @param keys
	 * @throws BusinessException 
	 */
	public void pluginDeptChange(Map<String, Object> keys) throws BusinessException{
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	/**
	 * 查询操作
	 * 
	 * @param keys
	 * @throws BusinessException 
	 */
	public void pluginSearch(Map<String, Object> keys) throws BusinessException {
//		((ILFWLicenceCheck)NCLocator.getInstance().lookup(ILFWLicenceCheck.class)).checkLicence(ShopTaApplyConsts.PRODUCTNAME);
		TBMPsndocUtil.checkTimeRuleVO();
		
		if (keys == null || keys.size() == 0) {
			return;
		}
		Dataset ds = getCurrentView().getViewModels().getDataset(getDatasetId());
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) CommonUtil.getUAPFromWhereSQL((nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get("where"));
		getApplicationContext().addAppAttribute(ShopTaApplyConsts.APP_ATTR_FROMWHERESQL, fromWhereSQL);
		// // 重置页序号
		// ds.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		DatasetUtil.clearData(ds);
		getTaApplyDatas(fromWhereSQL, ds);
	}

	/**
	 * 新增操作
	 * 
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void addBill(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		String operate_status = HrssConsts.POPVIEW_OPERATE_ADD;
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, null);
		showWindowDialog(operate_status);
	}

	private void showWindowDialog(String operate_status) {
		CommonUtil.showWindowDialog(getPopWindowId(), getPopWindowTitle(operate_status), "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
	}

	/**
	 * 修改操作
	 * 
	 * @param mouseEvent
	 */
	public void editBill(MouseEvent<MenuItem> mouseEvent) {
		if (TaAppContextUtil.getTBMPsndocVO() == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_ta-res","0c_ta-res0181"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0049")/*
																															 * @
																															 * res
																															 * "您还没有设置考勤档案，不能进行修改操作！"
																															 */);
		}
		Dataset ds = getCurrentView().getViewModels().getDataset(getDatasetId());
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_ta-res","0c_ta-res0181"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0050")/*
																															 * @
																															 * res
																															 * "请选择待修改的记录！"
																															 */);
		}
		AggregatedValueObject aggVO = new Datasets2AggVOSerializer().serialize(ds, null, getAggVOClazz().getName());
		IFlowBizItf itf = HrssBillCommand.getFlowBizImplByMdComp(ds, aggVO);
		if (itf != null && IPfRetCheckInfo.NOSTATE != itf.getApproveStatus()) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_ta-res","0c_ta-res0181"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0051")/*
																															 * @
																															 * res
																															 * "单据状态已不为自由态,不可修改！"
																															 */);
		}
		// 主键字段
		String primaryField = DatasetUtil.getPrimaryField(ds).getId();
		String primarykey = selRow.getString(ds.nameToIndex(primaryField));
		String operate_status = HrssConsts.POPVIEW_OPERATE_EDIT;
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, primarykey);
		
		checkAggVO(primarykey);
		
		showWindowDialog(operate_status);
	}
	
	
	/**
	 * 判断单据是否已经被删除
	 * 
	 * @param primarykey
	 */
	public void checkAggVO(String primarykey){
		
		AggregatedValueObject aggVO = null;
		try {
			aggVO = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPK(getAggVOClazz(), primarykey, false);
		} catch (MetaDataException e) {
			new HrssException(e).deal();
		}
		if (aggVO == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0166")/* @res "提示信息" */,
					ResHelper.getString("c_pub-res", "0c_pub-res0026")/* @res "单据已被删除！" */);
		}
	}

	/**
	 * 查看详细操作
	 * 
	 * @param mouseEvent
	 */
	public void showDetail(ScriptEvent scriptEvent) {
		// 主键
		String primaryKey = AppLifeCycleContext.current().getParameter("dsMain_primaryKey");
		String operate_status = HrssConsts.POPVIEW_OPERATE_VIEW;
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, primaryKey);
		checkAggVO(primaryKey);
		showWindowDialog(operate_status);
	}

	/**
	 * 删除操作
	 * 
	 * @param mouseEvent
	 */
	public void deleteBill(MouseEvent<MenuItem> mouseEvent) {
		if(CommonUtil.showConfirmDialog("提示", "你确定删除所选数据？")){
			CmdInvoker.invoke(new PFDeleteCmd(getDatasetId(), getAggVOClazz()));
		}
	}

	/**
	 * 提交操作
	 * 
	 * @param mouseEvent
	 */
	public void sumbitBill(MouseEvent<MenuItem> mouseEvent) {
		/*if (TaAppContextUtil.getTBMPsndocVO() == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0172"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0054")
																															 * @
																															 * res
																															 * "您还没有设置考勤档案，不能进行提交操作！"
																															 );
		}*/
		CmdInvoker.invoke(new PFCommitCmd(getDatasetId(), getCommitPrcss(), getAggVOClazz()));
	}

	/**
	 * 收回操作
	 * 
	 * @param mouseEvent
	 */
	public void callBackBill(MouseEvent<MenuItem> mouseEvent) {
		/*if (TaAppContextUtil.getTBMPsndocVO() == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0173"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0055")
																															 * @
																															 * res
																															 * "您还没有设置考勤档案，不能进行收回操作！"
																															 );
		}*/
		PFReCallCmd reCallCmd = new PFReCallCmd(getDatasetId(), null, getAggVOClazz());
		CmdInvoker.invoke(reCallCmd);
	}

	/**
	 * 查看审批流操作
	 * 
	 * @param mouseEvent
	 */
	public void showApproveState(MouseEvent<MenuItem> mouseEvent) {
		PFApproveInfoCmd approveInfoCmd = new PFApproveInfoCmd(getDatasetId(), getAggVOClazz());
		CmdInvoker.invoke(approveInfoCmd);
	}

	/**
	 * 附件管理操作
	 * 
	 * @param mouseEvent
	 */
	public void addAttachment(MouseEvent<MenuItem> mouseEvent) {
		LfwView mainWidget = AppLifeCycleContext.current().getViewContext().getView();
		Dataset ds = mainWidget.getViewModels().getDataset(getDatasetId());
		CommonUtil.Attachment(ds, true);
	}

	/**
	 * 设置审批状态
	 * 
	 * @return
	 */
	protected TAPFBillQueryParams getEtraConds() {
		TAPFBillQueryParams params = new TAPFBillQueryParams();
		// 设置审批状态
		params.setStateCode(HRConstEnum.ALL_INTEGER);
		return params;
	}

	/**
	 * 行切换事件
	 * 
	 * @param datasetEvent
	 */
	public void onAfterRowSelect(DatasetEvent datasetEvent) {
		ButtonStateManager.updateButtons();
	}

	private void getTaApplyDatas(FromWhereSQLImpl fromWhereSQL, Dataset ds) {
		AggregatedValueObject[] aggVOs = getAggVOs(fromWhereSQL);
		if (aggVOs == null || aggVOs.length == 0) {
			DatasetUtil.clearData(ds);
			return;
		}
		List<SuperVO> list = new ArrayList<SuperVO>();
		for (AggregatedValueObject aggVO : aggVOs) {
			list.add((SuperVO) aggVO.getParentVO());
		}
		new SuperVO2DatasetSerializer().serialize(DatasetUtil.paginationMethod(ds, list.toArray(new SuperVO[0])), ds, Row.STATE_NORMAL);
		ds.setRowSelectIndex(CommonUtil.getAppAttriSelectedIndex());
		ButtonStateManager.updateButtons();
	}
}
