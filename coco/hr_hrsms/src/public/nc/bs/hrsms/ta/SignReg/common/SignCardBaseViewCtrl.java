package nc.bs.hrsms.ta.SignReg.common;

import nc.bs.hrss.pub.BillCoderUtils;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.cmd.AddCmd;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.cmd.CopyCmd;
import nc.bs.hrss.pub.cmd.EditCmd;
import nc.bs.hrss.pub.cmd.LineAddCmd;
import nc.bs.hrss.pub.cmd.LineDelCmd;
import nc.bs.hrss.pub.cmd.LineInsertCmd;
import nc.bs.hrss.pub.cmd.PFSaveCommitCmd;
import nc.bs.hrss.pub.cmd.SaveAddCmd;
import nc.bs.hrss.pub.cmd.SaveCmd;
import nc.bs.hrss.ta.common.cmd.TaApplyEditCmd;
import nc.bs.hrss.ta.common.ctrl.BaseController;
import nc.bs.hrss.ta.common.prcss.TaBaseAddProcessor;
import nc.bs.hrss.ta.common.prcss.TaBaseCopyProcessor;
import nc.itf.hrss.pub.cmd.prcss.IAddProcessor;
import nc.itf.hrss.pub.cmd.prcss.ICommitProcessor;
import nc.itf.hrss.pub.cmd.prcss.ICopyProcessor;
import nc.itf.hrss.pub.cmd.prcss.IEditProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineDelProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineInsertProcessor;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.vo.pub.AggregatedValueObject;

import org.apache.commons.lang.StringUtils;


public abstract class SignCardBaseViewCtrl extends BaseController
{
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
	 * 子数据集ID
	 * 
	 * @return
	 */
	protected abstract String getDetailDsId();

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
	 * 查询方法的PlugoutID
	 * 
	 * @return
	 */
	protected String getPlugoutId() {
		return "plugoutSearch";
	}

	/**
	 * 新增的PROCESSOR
	 * 
	 * @return
	 */
	protected Class<? extends IAddProcessor> getAddPrcss() {
		return TaBaseAddProcessor.class;
	}

	/**
	 * 编辑的PROCESSOR
	 * 
	 * @return
	 */
	protected Class<? extends IEditProcessor> getEditPrcss() {
		return null;
	}

	/**
	 * 复制的PROCESSOR
	 * 
	 * @return
	 */
	protected Class<? extends ICopyProcessor> getCopyPrcss() {
		return TaBaseCopyProcessor.class;
	}

	/**
	 * 保存的PROCESSOR
	 * 
	 * @return
	 */
	protected abstract Class<? extends ISaveProcessor> getSavePrcss();

	/**
	 * 保存并新增的PROCESSOR
	 * 
	 * @return
	 */
	protected abstract Class<? extends ISaveProcessor> getSaveAddPrcss();

	/**
	 * 保存并提交的PROCESSOR
	 * 
	 * @return
	 */
	protected Class<? extends ICommitProcessor> getSaveCommitPrcss() {
		return null;
	}

	/**
	 * 行新增的PROCESSOR
	 * 
	 * @return
	 */
	protected abstract Class<? extends ILineInsertProcessor> getLineAddPrcss();

	/**
	 * 行删除的PROCESSOR<br/>
	 * 默认没有
	 * 
	 * @param mouseEvent
	 */
	protected Class<? extends ILineDelProcessor> getLineDelPrcss() {
		return null;
	}

	/**
	 * 提交的操作类
	 */
	protected Class<? extends ICommitProcessor> getCommitPrcss() {
		return null;
	}

	/**
	 * 数据加载事件
	 * 
	 * @param dataLoadEvent
	 */
	protected void onDataLoad(DataLoadEvent dataLoadEvent) {
		// 区分新增/修改/复制/浏览
		String operateStatus = (String) getApplicationContext().getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
		if (StringUtils.isEmpty(operateStatus)) {
			operateStatus = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(HrssConsts.POPVIEW_OPERATE_STATUS);
		}
		String primaryKey = (String) getApplicationContext().getAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM);
		if (StringUtils.isEmpty(primaryKey)) {
			primaryKey = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(HrssConsts.POPVIEW_OPERATE_PARAM);
		}
		if (StringUtils.isEmpty(operateStatus)) {
			return;
		}
		if (HrssConsts.POPVIEW_OPERATE_ADD.equals(operateStatus)) {
			AddCmd cmd = new AddCmd(getDatasetId(), getBillType(), getAddPrcss());
			CmdInvoker.invoke(cmd);
		} else if (HrssConsts.POPVIEW_OPERATE_EDIT.equals(operateStatus)) {
			EditCmd cmd = new TaApplyEditCmd(getDatasetId(), primaryKey, getEditPrcss(), getAggVoClazz());
			CmdInvoker.invoke(cmd);
		} else if (HrssConsts.POPVIEW_OPERATE_COPY.equals(operateStatus)) {
			CopyCmd cmd = new CopyCmd(getDatasetId(), primaryKey, getCopyPrcss(), getAggVoClazz());
			CmdInvoker.invoke(cmd);
		} else if (HrssConsts.POPVIEW_OPERATE_VIEW.equals(operateStatus)) {
			CmdInvoker.invoke(new TaApplyEditCmd(getDatasetId(), primaryKey, getEditPrcss(), getAggVoClazz()));
		}
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
	 * 保存并新增事件
	 * 
	 * @param mouseEvent
	 */
	public void onSaveAndAdd(MouseEvent<MenuItem> mouseEvent) {
		AddCmd addCommand = new AddCmd(getDatasetId(), getBillType(), getAddPrcss());
		SaveAddCmd saCommand = new SaveAddCmd(getDatasetId(), getSaveAddPrcss(), getAggVoClazz(), addCommand);
		CmdInvoker.invoke(saCommand);
	}

	/**
	 * 保存并提交事件
	 * 
	 * @param mouseEvent
	 */
	public void onSaveAndCommit(MouseEvent<MenuItem> mouseEvent) {
		CmdInvoker.invoke(new PFSaveCommitCmd(getDatasetId(), getSavePrcss(), getSaveCommitPrcss(), getAggVoClazz()));
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
		if (HrssConsts.POPVIEW_OPERATE_EDIT.equals(operateStatus) || HrssConsts.POPVIEW_OPERATE_VIEW.equals(operateStatus)) {
			return;
		}
		Dataset masterDs = getCurrentView().getViewModels().getDataset(getDatasetId());
		Row masterRow = masterDs.getSelectedRow();
		if (masterRow == null) {
			return;
		}
		// 单据编码
		String bill_code = (String) masterRow.getValue(masterDs.nameToIndex("bill_code"));
		if (StringUtils.isEmpty(bill_code)) {
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
	 * 行新增事件
	 * 
	 * @param mouseEvent
	 */
	public void onLineAdd(MouseEvent<MenuItem> mouseEvent) {
		CmdInvoker.invoke(new LineAddCmd(getDetailDsId(), getLineAddPrcss()));
	}

	/**
	 * 行插入事件
	 * 
	 * @param mouseEvent
	 */
	public void onLineInsert(MouseEvent<MenuItem> mouseEvent) {
		CmdInvoker.invoke(new LineInsertCmd(getDetailDsId(), getLineAddPrcss()));
	}

	/**
	 * 行删除事件
	 * 
	 * @param mouseEvent
	 */
	public void onLineDel(MouseEvent<MenuItem> mouseEvent) {
		CmdInvoker.invoke(new LineDelCmd(getDetailDsId(), getLineDelPrcss()));
	}









}