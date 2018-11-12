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
 * POPView�ĸ���
 * 
 * 
 */

public abstract class HiApplyBaseViewMain extends HiApproveView {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	/**
	 * ��������
	 */
	protected abstract String getBillType();

	/**
	 * ���ݼ�ID
	 * 
	 * @return
	 */
	protected abstract String getDatasetId();

	/**
	 * ���ݾۺ�����VO
	 * 
	 * @return
	 */
	protected abstract Class<? extends AggregatedValueObject> getAggVoClazz();

	/**
	 * ����Ƭ��Id
	 * 
	 * @return
	 */
	protected String getPopViewId() {
		return HrssConsts.PAGE_MAIN_WIDGET;
	}

	/**
	 * ������PROCESSOR
	 * 
	 * @return
	 */
	protected Class<? extends IAddProcessor> getAddPrcss() {
		return TrnBaseAddProcessor.class;
	}

	/**
	 * �༭��PROCESSOR
	 * 
	 * @return
	 */
	private Class<? extends IEditProcessor> getEditPrcss() {
		return null;
	}

	/**
	 * �����PROCESSOR
	 * 
	 * @return
	 */
	protected abstract Class<? extends ISaveProcessor> getSavePrcss();

	/**
	 * ��ɾ����PROCESSOR<br/>
	 * Ĭ��û��
	 * 
	 * @param mouseEvent
	 */
	protected String getLineDelPrcss() {
		return null;
	}

	/**
	 * ���ݼ����¼�
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad(DataLoadEvent dataLoadEvent) {

		LfwView widget = getLifeCycleContext().getViewContext().getView();
		ApplicationContext appCtx = getLifeCycleContext().getApplicationContext();
		// ��������/�޸�/���
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
		// �������뵥���������춯��Ŀ�Ƿ�ɱ༭
		setTrnItemsEnableByApplyData(widget, ds, row);
	}

	private void setTrnItem() {
		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		Dataset dsMaster = widget.getViewModels().getDataset(getDatasetId());
		// �����ݼ��ĵ�ǰѡ����
		Row row = dsMaster.getSelectedRow();
		// �����춯��Ŀ�Ƿ�ɱ༭���Ƿ����
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
		String pk_group = row.getString(dsMaster.nameToIndex(StapplyVO.PK_GROUP));//������������
		String pk_org = row.getString(dsMaster.nameToIndex(StapplyVO.PK_ORG));//����������֯
		SuperVO[] itemvos = TrnUtil.getTrnItems(TRNConst.TRNSITEM_BEANID, pk_trnstype,pk_group, pk_org);
		return itemvos;
	}

	/**
	 * �����¼�
	 * 
	 * @param mouseEvent
	 */
	public void onSave(MouseEvent<MenuItem> mouseEvent) {
		CmdInvoker.invoke(new SaveCmd(getDatasetId(), getSavePrcss(), getAggVoClazz()));
	}

	/**
	 * PopView��ȡ���¼�
	 * 
	 * @param mouseEvent
	 */
	public void onCancel(MouseEvent<MenuItem> mouseEvent) {
		rollBackBillCode();
		// �رմ���
		CmdInvoker.invoke(new CloseWindowCmd());
	}

	/**
	 * �ع����ݱ���
	 */
	public void rollBackBillCode() {
		String operateStatus = (String) getApplicationContext().getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
		// �ع��Զ����ɵĵ��ݱ༭
		if (!HrssConsts.POPVIEW_OPERATE_ADD.equals(operateStatus)) {
			return;
		}
		LfwView viewMain = getLifeCycleContext().getViewContext().getView();
		Dataset masterDs = viewMain.getViewModels().getDataset(getDatasetId());
		Row masterRow = masterDs.getSelectedRow();
		if (masterRow == null) {
			return;
		}
		// ���ݱ���
		String bill_code = (String) masterRow.getValue(masterDs.nameToIndex("bill_code"));
		if (StringUtil.isEmptyWithTrim(bill_code)) {
			return;
		}
		// �������ڼ���
		String tbm_pk_group = (String) masterRow.getValue(masterDs.nameToIndex("pk_group"));
		// ����������֯
		String tbm_pk_org = (String) masterRow.getValue(masterDs.nameToIndex("pk_org"));
		// ��������
		String billType = (String) masterRow.getValue(masterDs.nameToIndex("pk_billtype"));
		// ������ϵͳ�Զ�����
		if (BillCoderUtils.isAutoGenerateBillCode(tbm_pk_group, tbm_pk_org, billType)) {
			// �ع����ݱ���
			BillCoderUtils.rollbackPreBillCode(tbm_pk_group, tbm_pk_org, billType, bill_code);
		}
	}

	/**
	 * beforeShow�¼�
	 * 
	 * @param dialogEvent
	 */
	public void onBeforeShow(DialogEvent dialogEvent) {
		 LfwView viewMain =
		 getLifeCycleContext().getViewContext().getView();
		 Dataset ds = viewMain.getViewModels().getDataset(getDatasetId());
		 String currentKey = ds.getCurrentKey();
		 // ���ݼ�δ��ʼ��ʱ����ʹ��refreshDataset
		 if (!StringUtil.isEmptyWithTrim(currentKey)) {
		 // ����:��ν��������ݼ���Dataset��onDataLoad����
		 new AppDynamicCompUtil(getLifeCycleContext().getApplicationContext(),
		 getLifeCycleContext().getViewContext()).refreshDataset(ds);
		 }
	}

	public AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}
}
