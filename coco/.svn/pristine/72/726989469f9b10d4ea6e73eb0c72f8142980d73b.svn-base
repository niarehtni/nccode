package nc.bs.hrsms.hi;

import nc.bs.hrss.pub.BillCoderUtils;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.cmd.AddCmd;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.cmd.EditCmd;
import nc.bs.hrss.pub.cmd.SaveCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.trn.PsnApplyConsts;
import nc.bs.hrss.trn.TrnUtil;
import nc.bs.logging.Logger;
import nc.itf.hrss.pub.cmd.prcss.IAddProcessor;
import nc.itf.hrss.pub.cmd.prcss.IEditProcessor;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.util.AppDynamicCompUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.trn.pub.TRNConst;
import nc.vo.trn.transmng.StapplyVO;
import nc.bs.hrss.trn.lsnr.TrnBaseAddProcessor;

/**
 * POPView的父类
 * 
 * 
 */

public abstract class HiApplyBaseViewMain extends HiApproveView {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	/**
	 * 单据类型
	 */
	protected abstract String getBillType();

	/**
	 * 数据集ID
	 * 
	 * @return
	 */
	protected abstract String getDatasetId();

	/**
	 * 单据聚合类型VO
	 * 
	 * @return
	 */
	protected abstract Class<? extends AggregatedValueObject> getAggVoClazz();

	/**
	 * 弹出片段Id
	 * 
	 * @return
	 */
	protected String getPopViewId() {
		return HrssConsts.PAGE_MAIN_WIDGET;
	}

	/**
	 * 新增的PROCESSOR
	 * 
	 * @return
	 */
	protected Class<? extends IAddProcessor> getAddPrcss() {
		return TrnBaseAddProcessor.class;
	}

	/**
	 * 编辑的PROCESSOR
	 * 
	 * @return
	 */
	private Class<? extends IEditProcessor> getEditPrcss() {
		return null;
	}

	/**
	 * 保存的PROCESSOR
	 * 
	 * @return
	 */
	protected abstract Class<? extends ISaveProcessor> getSavePrcss();

	/**
	 * 行删除的PROCESSOR<br/>
	 * 默认没有
	 * 
	 * @param mouseEvent
	 */
	protected String getLineDelPrcss() {
		return null;
	}

	/**
	 * 数据加载事件
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad(DataLoadEvent dataLoadEvent) {

		LfwView widget = getLifeCycleContext().getViewContext().getView();
		ApplicationContext appCtx = getLifeCycleContext().getApplicationContext();
		// 区分新增/修改/浏览
		String operateStatus = (String) appCtx.getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
		String primaryKey = (String) appCtx.getAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM);
		if (StringUtil.isEmptyWithTrim(operateStatus)) {
			return;
		}

		if (HrssConsts.POPVIEW_OPERATE_ADD.equals(operateStatus)) {
			CmdInvoker.invoke(new AddCmd(getDatasetId(), getBillType(), getAddPrcss()));
		} else if (HrssConsts.POPVIEW_OPERATE_EDIT.equals(operateStatus)) {
			CmdInvoker.invoke(new EditCmd(getDatasetId(), primaryKey, getEditPrcss(), getAggVoClazz()));
		} else if (HrssConsts.POPVIEW_OPERATE_VIEW.equals(operateStatus)) {
			CmdInvoker.invoke(new EditCmd(getDatasetId(), primaryKey, getEditPrcss(), getAggVoClazz()));
		}
		setTrnItem();
		Dataset ds = dataLoadEvent.getSource();
		Row row = ds.getSelectedRow();
		if (row == null) {
			return;
		}
		// 根据申请单数据设置异动项目是否可编辑
		setTrnItemsEnableByApplyData(widget, ds, row);
	}

	private void setTrnItem() {
		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		Dataset dsMaster = widget.getViewModels().getDataset(getDatasetId());
		// 主数据集的当前选中行
		Row row = dsMaster.getSelectedRow();
		// 设置异动项目是否可编辑、是否必填
		try {
			SuperVO[] itemvos = getTrnItems(dsMaster, row);
			TrnUtil.setTrnItems(widget, getDatasetId(), itemvos, PsnApplyConsts.REGULAR_FORM_OLDPSNINFO, PsnApplyConsts.REGULAR_FORM_NEWPSNINFO);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		} catch (HrssException e) {
			Logger.error(e.getMessage(), e);
		}
	}

	@Override
	protected String getNewFormId() {
		return PsnApplyConsts.REGULAR_FORM_NEWPSNINFO;
	}

	protected SuperVO[] getTrnItems(Dataset dsMaster, Row row) throws BusinessException, HrssException {
		String pk_trnstype = (String) row.getValue(dsMaster.nameToIndex(StapplyVO.PK_TRNSTYPE));
		String pk_group = row.getString(dsMaster.nameToIndex(StapplyVO.PK_GROUP));//单据所属集团
		String pk_org = row.getString(dsMaster.nameToIndex(StapplyVO.PK_ORG));//单据所属组织
		SuperVO[] itemvos = TrnUtil.getTrnItems(TRNConst.TRNSITEM_BEANID, pk_trnstype,pk_group, pk_org);
		return itemvos;
	}

	/**
	 * 保存事件
	 * 
	 * @param mouseEvent
	 */
	public void onSave(MouseEvent<MenuItem> mouseEvent) {
		CmdInvoker.invoke(new SaveCmd(getDatasetId(), getSavePrcss(), getAggVoClazz()));
	}

	/**
	 * PopView的取消事件
	 * 
	 * @param mouseEvent
	 */
	public void onCancel(MouseEvent<MenuItem> mouseEvent) {
		rollBackBillCode();
		// 关闭窗体
		CmdInvoker.invoke(new CloseWindowCmd());
	}

	/**
	 * 回滚单据编码
	 */
	public void rollBackBillCode() {
		String operateStatus = (String) getApplicationContext().getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
		// 回滚自动生成的单据编辑
		if (!HrssConsts.POPVIEW_OPERATE_ADD.equals(operateStatus)) {
			return;
		}
		LfwView viewMain = getLifeCycleContext().getViewContext().getView();
		Dataset masterDs = viewMain.getViewModels().getDataset(getDatasetId());
		Row masterRow = masterDs.getSelectedRow();
		if (masterRow == null) {
			return;
		}
		// 单据编码
		String bill_code = (String) masterRow.getValue(masterDs.nameToIndex("bill_code"));
		if (StringUtil.isEmptyWithTrim(bill_code)) {
			return;
		}
		// 单据所在集团
		String tbm_pk_group = (String) masterRow.getValue(masterDs.nameToIndex("pk_group"));
		// 单据所在组织
		String tbm_pk_org = (String) masterRow.getValue(masterDs.nameToIndex("pk_org"));
		// 单据类型
		String billType = (String) masterRow.getValue(masterDs.nameToIndex("pk_billtype"));
		// 编码由系统自动生成
		if (BillCoderUtils.isAutoGenerateBillCode(tbm_pk_group, tbm_pk_org, billType)) {
			// 回滚单据编码
			BillCoderUtils.rollbackPreBillCode(tbm_pk_group, tbm_pk_org, billType, bill_code);
		}
	}

	/**
	 * beforeShow事件
	 * 
	 * @param dialogEvent
	 */
	public void onBeforeShow(DialogEvent dialogEvent) {
		 LfwView viewMain =
		 getLifeCycleContext().getViewContext().getView();
		 Dataset ds = viewMain.getViewModels().getDataset(getDatasetId());
		 String currentKey = ds.getCurrentKey();
		 // 数据集未初始化时，不使用refreshDataset
		 if (!StringUtil.isEmptyWithTrim(currentKey)) {
		 // 作用:多次进入主数据集的Dataset的onDataLoad方法
		 new AppDynamicCompUtil(getLifeCycleContext().getApplicationContext(),
		 getLifeCycleContext().getViewContext()).refreshDataset(ds);
		 }
	}

	public AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}
}
