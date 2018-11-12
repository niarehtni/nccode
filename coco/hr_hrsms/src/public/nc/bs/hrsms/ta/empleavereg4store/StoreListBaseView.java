package nc.bs.hrsms.ta.empleavereg4store;

import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.common.TaApplyConsts;
import nc.bs.hrss.ta.common.ctrl.BaseController;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.uap.lfw.core.bm.ButtonStateManager;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.ta.leave.LeaveRegVO;


/**
 * @author renyp
 * @date 2015-4-29
 * @ClassName�������ƣ��곤�������б�ҳ����ʾ���������
 * @Description����������������
 * copy  OF nc.bs.hrsms.ta.common.ctrl.TaListBaseView
 */
public abstract class StoreListBaseView extends BaseController {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	// ��ѯ����������ʼʱ��
	public static final String PARAM_ID_BEGIN = "leavebegindate";
	// ��ѯ������������ʱ��
	public static final String PARAM_ID_END = "leaveenddate";
	/**
	 * @author renyp
	 * @date 2015-4-29
	 * @Description���������������������ͱ���
	 * 
	 */
	protected abstract String getBillTypeCode();

	/**
	 * @author renyp
	 * @date 2015-4-29
	 * @Description�������������� �����ݼ�ID
	 * 
	 */
	protected abstract String getDatasetId();
	/**
	 * @author renyp
	 * @date 2015-4-29
	 * @Description�������������� ��ѯ���
	 * 
	 */
	protected abstract AggregatedValueObject[] getAggVOs(FromWhereSQL fromWhereSQL);
	/**
	 * @author renyp
	 * @date 2015-4-29
	 * @Description�������������� ����ҳ���WindowId
	 * 
	 */
	protected abstract String getPopWindowId();
	/**
	 * @author renyp
	 * @date 2015-4-29
	 * @Description��������������  ���ҳ�����
	 * 
	 */
	protected abstract String getPopWindowTitle(String operateflag);
	/**
	 * @author renyp
	 * @throws BusinessException 
	 * @date 2015-4-29
	 * @Description�����������������ݼ����¼�
	 * 
	 */

	protected void onDataLoad(DataLoadEvent dataLoadEvent) throws BusinessException {
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) getApplicationContext().getAppAttribute(TaApplyConsts.APP_ATTR_FROMWHERESQL);
		if (fromWhereSQL == null) {// ��ʼ������
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		} else {
			Dataset ds = dataLoadEvent.getSource();
			getTaApplyDatas(fromWhereSQL, ds);
		}
	}
	/**
	 * �ӿ�Ƭ����ص��б����Ĳ�ѯ����
	 * 
	 * @param keys
	 */
	public void pluginReSearch(Map<String, Object> keys){
		// ִ������ݲ�ѯ
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	
	/**
	 * @author renyp
	 * @throws BusinessException 
	 * @date 2015-4-29
	 * @Description�������������� ��������
	 * 
	 */
	public void pluginSearch(Map<String, Object> keys) throws BusinessException {
		TBMPsndocUtil.checkTimeRuleVO();
		
		if (keys == null || keys.size() == 0) {
			return;
		}
		Dataset ds = getCurrentView().getViewModels().getDataset(getDatasetId());
		
		// ��ʼ����
		String beginDate = null;
		// ��������
		String endDate =  null;
		//����
		String name = null;
		LfwView simpQryView = getLifeCycleContext().getWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset(HrssConsts.DS_SIMPLE_QUERY);
		if (dsSearch != null) {
				Row row = dsSearch.getSelectedRow();
				beginDate = row.getValue(dsSearch.nameToIndex(PARAM_ID_BEGIN)).toString();
				endDate = row.getValue(dsSearch.nameToIndex(PARAM_ID_END)).toString();
				name = (String) row.getValue(dsSearch.nameToIndex("pk_psndoc_name"));
			}
		boolean containsSubDepts = SessionUtil.isIncludeSubDept();
		
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) CommonUtil.getUAPFromWhereSQL((nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get("where"));
		//20150430��Ӳ������� ��ӿ�ʼʱ�����ʱ��
		String pk_dept =SessionUtil.getPk_mng_dept();
		
		String  sqlDept =// " and pk_psndoc in ( select pk_psndoc from bd_psnjob where  PK_DEPT = '"+pk_dept+"') " +
				" tbm_leavereg.leavebegindate <= '"+endDate+"'and tbm_leavereg.leaveenddate >= '"+beginDate+"' ";
		if(null != name){
			sqlDept += " and tbm_leavereg.pk_psndoc in ( select bd_psndoc.pk_psndoc from bd_psndoc where name like '%" + name + "%')";
		}
		//ƴ�Ӳ���id���Ƿ�����Ӳ���
		try {
			HRDeptVO deptVO = (HRDeptVO) new BaseDAO().retrieveByPK(HRDeptVO.class, pk_dept);
			if(!containsSubDepts){
				sqlDept += " and tbm_leavereg.pk_psndoc in ( select pk_psndoc from bd_psnjob psnjob where  psnjob.PK_DEPT = '"+pk_dept+"')";
			}else{
				sqlDept += " and tbm_leavereg.pk_psndoc in ( select pk_psndoc from bd_psnjob psnjob left join org_dept dept on dept.pk_dept = psnjob.pk_dept" +
						" where  dept.innercode like '"+deptVO.getInnercode()+"')";
			}
		} catch (DAOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		
		fromWhereSQL.setWhere(sqlDept);
		getApplicationContext().addAppAttribute(TaApplyConsts.APP_ATTR_FROMWHERESQL, fromWhereSQL);

		DatasetUtil.clearData(ds);
		getTaApplyDatas(fromWhereSQL, ds);
		
		//��ApplicationContext����ӿ��ڵ�������
		TaAppContextUtil.addTaAppContext();
	}
	/**
	 * �����ű��
	 * 
	 * @param keys
	 * @throws BusinessException 
	 */
	public void pluginDeptChange(Map<String, Object> deptout) throws BusinessException {
		TaAppContextUtil.addTaAppContext();
		
		// ��ʼ������
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	/**����
	 * z �����л��¼�
	 * 
	 * @param keys
	 */
	public void pluginCatagory(Map<String, Object> keys) {
	}
	/**
	 * @author renyp
	 * @date 2015-4-29
	 * @Description�������������� �����ݼ�ID
	 * 
	 */
	public static String getNodecodeByApp(String appId) {
		return null;
	}
	/**
	 * @author renyp
	 * @throws BusinessException 
	 * @date 2015-4-29
	 * @Description��������������  ��������
	 * 
	 */
	public void addBill(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		String operate_status = HrssConsts.POPVIEW_OPERATE_ADD;
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, null);
		showWindowDialog(operate_status);
	}

	/**
	 * @author renyp
	 * @throws BusinessException 
	 * @date 2015-4-29
	 * @Description��������������  �������������
	 * 
	 */
	public void addFeed(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		String operate_status = HrssConsts.POPVIEW_OPERATE_ADD;
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, null);
		CommonUtil.showWindowDialog("EmpLeaveRegFeedWin","���������", "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
	}
	
	/**
	 * @author renyp
	 * @throws BusinessException 
	 * @date 2015-4-29
	 * @Description��������������  ������������
	 * 
	 */
	public void batchAdd(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		String operate_status = HrssConsts.POPVIEW_OPERATE_ADD;
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, null);
		CommonUtil.showWindowDialog("EmpLeaveRegBatchAddWin", "��������", "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
	}
	
	/**
	 * @author renyp
	 * @throws BusinessException 
	 * @date 2015-4-29
	 * @Description��������������  ���ٲ���
	 * 
	 */
	public void sickLeave(MouseEvent<MenuItem> mouseEvent) {
		// ����
		Dataset ds =  ViewUtil.getCurrentView().getViewModels().getDataset(getDatasetId());
		String primaryKey = ds.getSelectedRow().getValue(ds.nameToIndex(LeaveRegVO.PK_LEAVEREG)).toString();
		String operate_status = "sickLeave";
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, primaryKey);
		CommonUtil.showWindowDialog("EmpLeaveRegSickWin","����", "60%", "80%", null, ApplicationContext.TYPE_DIALOG, false, false);
	}
	
	/**
	 * @author renyp
	 * @date 2015-4-29
	 * @Description�������������� ����
	 * 
	 */
	private void showWindowDialog(String operate_status) {
		CommonUtil.showWindowDialog(getPopWindowId(), getPopWindowTitle(operate_status), "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
	}
	
	/**
	 * @author renyp
	 * @date 2015-4-29
	 * @Description��������������  ���л��¼�
	 * 
	 */
	public void onAfterRowSelect(DatasetEvent datasetEvent) {
		MenubarComp menus = ViewUtil.getCurrentView().getViewMenus().getMenuBar("menu_list");
		MenuItem deleteID = menus.getElementById("btnDel");
		Dataset ds = datasetEvent.getSource();
		Row row = ds.getSelectedRow();
		int billsource = (Integer) row.getValue(ds.nameToIndex(LeaveRegVO.BILLSOURCE));
		UFBoolean isleaveoff = (UFBoolean) row.getValue(ds.nameToIndex(LeaveRegVO.ISLEAVEOFF));
		MenuItem menuID = menus.getElementById("btnSickLeave");//
		if(billsource==2){//�жϵ�����Դ��ȷ����ť�Ƿ���ã����٣�
			menuID.setEnabled(false);
			deleteID.setEnabled(true);
		}else{
			deleteID.setEnabled(false);
			if(isleaveoff.booleanValue()){
				menuID.setEnabled(false);
			}else{
				menuID.setEnabled(true);
			} 
		}
	}

	private void getTaApplyDatas(FromWhereSQLImpl fromWhereSQL, Dataset ds) {
		SuperVO[] vos = getVOs(fromWhereSQL);
		if (vos == null || vos.length == 0) {
			DatasetUtil.clearData(ds);
			return;
		}
		new SuperVO2DatasetSerializer().serialize(DatasetUtil.paginationMethod(ds, vos), ds, Row.STATE_NORMAL);
		ds.setRowSelectIndex(CommonUtil.getAppAttriSelectedIndex());
		ButtonStateManager.updateButtons();
	}
	/**
	 * @author renyp
	 * @date 2015-4-29
	 * @Description�������������� ��ѯ���
	 * 
	 */
	protected abstract SuperVO[] getVOs(FromWhereSQL fromWhereSQL);


}