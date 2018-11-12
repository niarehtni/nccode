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
 * ViewList�ĸ���
 * 
 * @author shaochj
 * @date May 8, 2015
 * 
 */
public abstract class ShopTaListBaseView extends BaseController {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	/**
	 * �������ͱ���
	 * 
	 * @return
	 */
	protected abstract String getBillTypeCode();

	/**
	 * �����ݼ�ID
	 * 
	 * @return
	 */
	protected abstract String getDatasetId();

	/**
	 * �ύ�Ĳ�����
	 * 
	 * @return
	 */
	protected Class<? extends ICommitProcessor> getCommitPrcss() {
		return null;
	}

	/**
	 * �ۺ�VO
	 * 
	 * @return
	 */
	protected abstract Class<? extends AggregatedValueObject> getAggVOClazz();

	/**
	 * ��ѯ��������ʵ��Class
	 * 
	 * @return
	 */
	protected abstract Class<? extends SuperVO> getMainEntityClazz();

	/**
	 * ��ѯ���
	 * 
	 * @return
	 */
	protected abstract AggregatedValueObject[] getAggVOs(FromWhereSQL fromWhereSQL);

	/**
	 * ����ҳ���WindowId
	 * 
	 * @return
	 */
	protected abstract String getPopWindowId();

	/**
	 * ���ҳ�����
	 * 
	 * @param operateflag
	 * @return
	 */
	protected abstract String getPopWindowTitle(String operateflag);

	/**
	 * ���ݼ����¼�
	 * 
	 * @param dataLoadEvent
	 * @throws BusinessException 
	 */
	protected void onDataLoad(DataLoadEvent dataLoadEvent) throws BusinessException {
//		((ILFWLicenceCheck)NCLocator.getInstance().lookup(ILFWLicenceCheck.class)).checkLicence(ShopTaApplyConsts.PRODUCTNAME);
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) getApplicationContext().getAppAttribute(ShopTaApplyConsts.APP_ATTR_FROMWHERESQL);
		if (fromWhereSQL == null) {// ��ʼ������
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		} else {
			Dataset ds = dataLoadEvent.getSource();
			getTaApplyDatas(fromWhereSQL, ds);
		}
	}

	/**
	 * �رյ���ҳ�����ٲ�ѯ����
	 * 
	 * @param keys
	 */
	public void pluginReSearch(Map<String, Object> keys) {
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) getApplicationContext().getAppAttribute(ShopTaApplyConsts.APP_ATTR_FROMWHERESQL);
		Dataset ds = getCurrentView().getViewModels().getDataset(getDatasetId());
		getTaApplyDatas(fromWhereSQL, ds);
	}
	
	/**
	 * �����л��¼�
	 * @param keys
	 * @throws BusinessException 
	 */
	public void pluginDeptChange(Map<String, Object> keys) throws BusinessException{
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	/**
	 * ��ѯ����
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
		// // ����ҳ���
		// ds.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		DatasetUtil.clearData(ds);
		getTaApplyDatas(fromWhereSQL, ds);
	}

	/**
	 * ��������
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
	 * �޸Ĳ���
	 * 
	 * @param mouseEvent
	 */
	public void editBill(MouseEvent<MenuItem> mouseEvent) {
		if (TaAppContextUtil.getTBMPsndocVO() == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_ta-res","0c_ta-res0181"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0049")/*
																															 * @
																															 * res
																															 * "����û�����ÿ��ڵ��������ܽ����޸Ĳ�����"
																															 */);
		}
		Dataset ds = getCurrentView().getViewModels().getDataset(getDatasetId());
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_ta-res","0c_ta-res0181"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0050")/*
																															 * @
																															 * res
																															 * "��ѡ����޸ĵļ�¼��"
																															 */);
		}
		AggregatedValueObject aggVO = new Datasets2AggVOSerializer().serialize(ds, null, getAggVOClazz().getName());
		IFlowBizItf itf = HrssBillCommand.getFlowBizImplByMdComp(ds, aggVO);
		if (itf != null && IPfRetCheckInfo.NOSTATE != itf.getApproveStatus()) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_ta-res","0c_ta-res0181"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0051")/*
																															 * @
																															 * res
																															 * "����״̬�Ѳ�Ϊ����̬,�����޸ģ�"
																															 */);
		}
		// �����ֶ�
		String primaryField = DatasetUtil.getPrimaryField(ds).getId();
		String primarykey = selRow.getString(ds.nameToIndex(primaryField));
		String operate_status = HrssConsts.POPVIEW_OPERATE_EDIT;
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, primarykey);
		
		checkAggVO(primarykey);
		
		showWindowDialog(operate_status);
	}
	
	
	/**
	 * �жϵ����Ƿ��Ѿ���ɾ��
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
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0166")/* @res "��ʾ��Ϣ" */,
					ResHelper.getString("c_pub-res", "0c_pub-res0026")/* @res "�����ѱ�ɾ����" */);
		}
	}

	/**
	 * �鿴��ϸ����
	 * 
	 * @param mouseEvent
	 */
	public void showDetail(ScriptEvent scriptEvent) {
		// ����
		String primaryKey = AppLifeCycleContext.current().getParameter("dsMain_primaryKey");
		String operate_status = HrssConsts.POPVIEW_OPERATE_VIEW;
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, primaryKey);
		checkAggVO(primaryKey);
		showWindowDialog(operate_status);
	}

	/**
	 * ɾ������
	 * 
	 * @param mouseEvent
	 */
	public void deleteBill(MouseEvent<MenuItem> mouseEvent) {
		if(CommonUtil.showConfirmDialog("��ʾ", "��ȷ��ɾ����ѡ���ݣ�")){
			CmdInvoker.invoke(new PFDeleteCmd(getDatasetId(), getAggVOClazz()));
		}
	}

	/**
	 * �ύ����
	 * 
	 * @param mouseEvent
	 */
	public void sumbitBill(MouseEvent<MenuItem> mouseEvent) {
		/*if (TaAppContextUtil.getTBMPsndocVO() == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0172"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0054")
																															 * @
																															 * res
																															 * "����û�����ÿ��ڵ��������ܽ����ύ������"
																															 );
		}*/
		CmdInvoker.invoke(new PFCommitCmd(getDatasetId(), getCommitPrcss(), getAggVOClazz()));
	}

	/**
	 * �ջز���
	 * 
	 * @param mouseEvent
	 */
	public void callBackBill(MouseEvent<MenuItem> mouseEvent) {
		/*if (TaAppContextUtil.getTBMPsndocVO() == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0173"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0055")
																															 * @
																															 * res
																															 * "����û�����ÿ��ڵ��������ܽ����ջز�����"
																															 );
		}*/
		PFReCallCmd reCallCmd = new PFReCallCmd(getDatasetId(), null, getAggVOClazz());
		CmdInvoker.invoke(reCallCmd);
	}

	/**
	 * �鿴����������
	 * 
	 * @param mouseEvent
	 */
	public void showApproveState(MouseEvent<MenuItem> mouseEvent) {
		PFApproveInfoCmd approveInfoCmd = new PFApproveInfoCmd(getDatasetId(), getAggVOClazz());
		CmdInvoker.invoke(approveInfoCmd);
	}

	/**
	 * �����������
	 * 
	 * @param mouseEvent
	 */
	public void addAttachment(MouseEvent<MenuItem> mouseEvent) {
		LfwView mainWidget = AppLifeCycleContext.current().getViewContext().getView();
		Dataset ds = mainWidget.getViewModels().getDataset(getDatasetId());
		CommonUtil.Attachment(ds, true);
	}

	/**
	 * ��������״̬
	 * 
	 * @return
	 */
	protected TAPFBillQueryParams getEtraConds() {
		TAPFBillQueryParams params = new TAPFBillQueryParams();
		// ��������״̬
		params.setStateCode(HRConstEnum.ALL_INTEGER);
		return params;
	}

	/**
	 * ���л��¼�
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
