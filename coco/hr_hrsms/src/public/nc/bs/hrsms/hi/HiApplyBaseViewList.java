package nc.bs.hrsms.hi;

import java.util.HashMap;
import java.util.Map;

import nc.bs.hrss.pub.DialogSize;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PFApproveInfoCmd;
import nc.bs.hrss.pub.cmd.PFCommitCmd;
import nc.bs.hrss.pub.cmd.PFDeleteCmd;
import nc.bs.hrss.pub.cmd.PFReCallCmd;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.trn.PsnApplyConsts;
import nc.bs.uif2.validation.ValidationException;
import nc.bs.uif2.validation.ValidationFailure;
import nc.itf.hi.IPsndocQryService;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.ctx.WindowContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.hi.entrymng.InMultiBillValidator;
import nc.vo.hi.trnstype.TrnstypeVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.trn.regmng.RegapplyVO;
import nc.vo.trn.transmng.StapplyVO;

/**
 * ViewList的父类
 * 
 * 
 */
public abstract class HiApplyBaseViewList {

	// private String PLUGIN_PARAM_ID = "conditionRow";

	/**
	 * 主数据集ID
	 * 
	 * @return
	 */
	protected abstract String getDatasetId();

	/**
	 * 主数据集ID
	 * 
	 * @return
	 */
	protected abstract Class<? extends AggregatedValueObject> getAggVOClazz();

	/**
	 * 新增页面标题
	 * 
	 * @return
	 */
	protected abstract String getAddViewTitle();

	/**
	 * 查看详细页面标题
	 * 
	 * @return
	 */
	protected abstract String getDetailViewTitle();

	/**
	 * 修改页面标题
	 * 
	 * @return
	 */
	protected abstract String getEditViewTitle();

	/**
	 * 弹出页面的WindowId
	 * 
	 * @return
	 */
	protected abstract String getPopWindowId();

	public void dataLoad(DataLoadEvent dataLoadEvent) throws BusinessException {
		LfwView viewMain = getCurrentActiveView();
		if (viewMain == null) {
			return;
		}
		Dataset ds = viewMain.getViewModels().getDataset(getDatasetId());
		if (ds == null) {
			return;
		}
		DatasetUtil.clearData(ds);
		SuperVO[] vos = queryVOs();
		new SuperVO2DatasetSerializer().serialize(vos, ds, Row.STATE_NORMAL);
		// ds.setRowSelectIndex(0);
		// ButtonStateManager.updateButtons();
	}

	protected SuperVO[] queryVOs() {
		return null;
	}

	/**
	 * 新增操作
	 * 
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void addBill(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		SessionBean session = SessionUtil.getSessionBean();
		String pk_psnjob = session.getPsnjobVO().getPk_psnjob();

		try {
			SuperVO superVO = null;
			if (PsnApplyConsts.REGULAR_BILLTYPE_CODE.equals(getBillTypeCode())) {
				superVO = RegapplyVO.class.newInstance();
			} else {
				superVO = StapplyVO.class.newInstance();
			}
			superVO.setAttributeValue(RegapplyVO.PK_PSNJOB, pk_psnjob);
			superVO.setAttributeValue(RegapplyVO.PK_BILLTYPE, getBillTypeCode());
			InMultiBillValidator validator = new InMultiBillValidator();
			ValidationFailure failure = validator.validate(new SuperVO[] { superVO });
			if (failure == null)
				return;
			ValidationException exception = new ValidationException();
			exception.addValidationFailure(failure);
			throw exception;
		} catch (InstantiationException e) {
			new HrssException(e).deal();
		} catch (IllegalAccessException e) {
			new HrssException(e).deal();
		} catch (ValidationException e) {
			String msg = e.getMessage().substring(e.getMessage().indexOf(":") + 1);
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "提示信息")/*
																														 * @
																														 * res
																														 * "提示信息0c_hi-res0017"
																														 */, msg);
		}
	}
	
	/**
	 * 管理部门变更
	 * 
	 * @param keys
	 */
	public void pluginDeptChange(Map<String, Object> keys) {
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	
	/**
	 * 分类切换事件(改：条件查询事件)
	 * 
	 * @param keys
	 * @throws BusinessException 
	 */
	public void plugininParam(Map<String, Object> keys) throws BusinessException {
		TrnstypeVO[] psnJobVOs = null;
		// 获得选择的管理部门
		String pk_mng_dept = SessionUtil.getPk_mng_dept();
	}
	/**
	 * 关闭弹出页面后的再查询操作
	 * 
	 * @param keys
	 */
	public void pluginReSearch(Map<String, Object> keys) {
		LfwView mainWidget = AppLifeCycleContext.current().getViewContext().getView();
		Dataset ds = mainWidget.getViewModels().getDataset(getDatasetId());
		new SuperVO2DatasetSerializer().serialize(queryVOs(), ds, Row.STATE_NORMAL);
	}

	/**
	 * 修改操作
	 * 
	 * @param mouseEvent
	 */
	public void editBill(MouseEvent<MenuItem> mouseEvent) {
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(getDatasetId());
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "提示信息")/*
																														 * @
																														 * res
																														 * "提示信息0c_hi-res0017"
																														 */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "请选择待修改的记录！")/*
																																																				 * @
																																																				 * res
																																																				 * "请选择待修改的记录！0c_trn-res0038"
																																																				 */);
			// throw new
			// LfwRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_trn-res0038")/*@res
			// "请选择待修改的记录！"*/);
		}
		// 主键字段
		String primaryField = DatasetUtil.getPrimaryField(ds).getId();
		String primarykey = selRow.getString(ds.nameToIndex(primaryField));
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, HrssConsts.POPVIEW_OPERATE_EDIT);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, primarykey);
		CommonUtil.showWindowDialog(getPopWindowId(), getEditViewTitle(), DialogSize.LARGE.getWidth(), PsnApplyConsts.HEIGHT, getParamMap(), ApplicationContext.TYPE_DIALOG);
	}

	/**
	 * 查看详细操作
	 * 
	 * @param mouseEvent
	 */
	public void showDetail(ScriptEvent scriptEvent) {
		// 主键
		String primaryKey = AppLifeCycleContext.current().getParameter("dsMain_primaryKey");
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, HrssConsts.POPVIEW_OPERATE_VIEW);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, primaryKey);
		CommonUtil.showWindowDialog(getPopWindowId(), getDetailViewTitle(), "-1","-1", getParamMap(), ApplicationContext.TYPE_DIALOG, false, false);//String.valueOf(DialogSize.LARGE.getWidth()), String.valueOf(PsnApplyConsts.HEIGHT)
	}

	/**
	 * 删除操作
	 * 
	 * @param mouseEvent
	 */
	public void deleteBill(MouseEvent<MenuItem> mouseEvent) {
		CmdInvoker.invoke(new PFDeleteCmd(getDatasetId(), getAggVOClazz()));
	}

	public void sumbitBill(MouseEvent<MenuItem> mouseEvent) {
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(getDatasetId());
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "提示信息")/*
																														 * @
																														 * res
																														 * "提示信息0c_hi-res0017"
																														 */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res", "请选择待提交的记录！")/*
																																																				 * @
																																																				 * res
																																																				 * "请选择待提交的记录！0c_pub-res0186"
																																																				 */);
			// throw new
			// LfwRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_trn-res0038")/*@res
			// "请选择操作的记录！"*/);
		}
		//String pk_psndoc = selRow.getString(10);
		
		// 单据类型
		String billtype = getBillTypeCode();
		String pkUser = SessionUtil.getPk_user();
		String pkGroup = SessionUtil.getPk_group();
		try {
			// 检查是否有附件,等平台支持
			boolean isHasAttachment = true;

			if (!isHasAttachment) {
				IPsndocQryService service = ServiceLocator.lookup(IPsndocQryService.class);
				boolean isMustUpload = service.isMustUploadAttachment(billtype, pkUser, pkGroup);
				if (isMustUpload) {
					// 请上传附件
					CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "提示信息")/*
																																 * @
																																 * res
																																 * "提示信息0c_hi-res0017"
																																 */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "请上传附件！")/*
																																																						 * @
																																																						 * res
																																																						 * "请上传附件！0c_trn-res0039"
																																																						 */);
					// throw new
					// LfwRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_trn-res0039")/*@res
					// "请上传附件！"*/);
				}
			}

		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			e.alert();
		}
		CmdInvoker.invoke(new PFCommitCmd(getDatasetId(), null, getAggVOClazz()));
		
	}

	

	public void callBackBill(MouseEvent<MenuItem> mouseEvent) {
		PFReCallCmd reCallCmd = new PFReCallCmd(getDatasetId(), null, getAggVOClazz());
		CmdInvoker.invoke(reCallCmd);
	}

	public void showApproveState(MouseEvent<MenuItem> mouseEvent) {
		PFApproveInfoCmd approveInfoCmd = new PFApproveInfoCmd(getDatasetId(), getAggVOClazz());
		CmdInvoker.invoke(approveInfoCmd);
	}

	/**
	 * 单据类型编码
	 * 
	 * @return
	 */
	protected abstract String getBillTypeCode();

	/**
	 * 当前获得片段
	 * 
	 * @return
	 */
	protected LfwView getCurrentActiveView() {
		return AppLifeCycleContext.current().getViewContext().getView();
	}

	protected ApplicationContext getApplicationContext() {
		return AppLifeCycleContext.current().getApplicationContext();
	}

	/**
	 * 当前获得WindowContext
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private WindowContext getCurrentWindowContext() {
		return AppLifeCycleContext.current().getWindowContext();
	}

	/**
	 * 附件管理操作
	 * 
	 * @param mouseEvent
	 */
	public void addAttachment(MouseEvent<MenuItem> mouseEvent) {
		LfwView mainWidget = AppLifeCycleContext.current().getWindowContext().getViewContext("main").getView();
		Dataset ds = mainWidget.getViewModels().getDataset(getDatasetId());
		Row row = ds.getSelectedRow();
		if (row == null) {
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "提示信息")/*
																														 * @
																														 * res
																														 * "提示信息0c_hi-res0017"
																														 */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "请选择记录！")/*
																																																				 * @
																																																				 * res
																																																				 * "请选择记录！0c_trn-res0040"
																																																				 */);
			// throw new
			// LfwRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_trn-res0040")/*@res
			// "请选择记录！"*/);
		}
		int approve_state = row.getInt(ds.nameToIndex(StapplyVO.APPROVE_STATE));
		boolean isPower = false;
		if (approve_state == IPfRetCheckInfo.NOSTATE)
			isPower = true;
		CommonUtil.Attachment(ds, isPower);
	}

	protected abstract String getNodecode();

	protected Map<String, String> getParamMap() {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("nodecode", getNodecode());
		return paramMap;
	}
}