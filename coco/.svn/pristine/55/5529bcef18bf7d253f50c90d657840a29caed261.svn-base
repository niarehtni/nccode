package nc.bs.hrsms.ta.shift.ctrl;

import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.shift.StoreShiftConsts;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.itf.hrsms.ta.shift.IStoreShiftManageMaintain;
import nc.itf.hrsms.ta.shift.IStoreShiftQueryMaintain;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.bm.ButtonStateManager;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.ctx.WindowContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.uif2.LoginContext;

public class MainViewController implements IController{
	
	/**
	 * 获得当前Application
	 */
	private ApplicationContext getCurrentApplication() {
		return AppLifeCycleContext.current().getApplicationContext();
	}
	/**
	 * 获得当前片段
	 * 
	 * @return
	 */
	protected LfwView getCurrentActiveView() {
		return AppLifeCycleContext.current().getViewContext().getView();
	}
	/**
	 * 获得当前WindowContext
	 */
	private WindowContext getCurrentWindowContext() {
		return AppLifeCycleContext.current().getWindowContext();
	}
	/**
	 * 
	 * @return
	 */
	private IStoreShiftQueryMaintain getQueryService(){
		IStoreShiftQueryMaintain service = NCLocator.getInstance().lookup(IStoreShiftQueryMaintain.class);
		return service;
	}
	public void plugininParam(Map<String, Object> keys) throws BusinessException{
		LfwView viewMain = getCurrentActiveView();
		if (viewMain == null) {
			return;
		}
		Dataset ds = viewMain.getViewModels().getDataset(StoreShiftConsts.DS_MAIN_NAME);
		if (ds == null) {
			return;
		}
		// 清空数据集
		DatasetUtil.clearData(ds);
		ds.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		if (keys == null || keys.size() == 0) {
			return;
		}

		

		nc.uap.ctrl.tpl.qry.FromWhereSQLImpl whereSql = (nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys
				.get(HrssConsts.PO_SEARCH_WHERESQL);
		
		// 查询条件-FromWhereSQL
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) CommonUtil.getUAPFromWhereSQL(whereSql);
		
		this.ser(ds, fromWhereSQL);
		/*try {
			FromWhereSQL fromWhereSQL1 = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL);
			
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*
		 * StringBuffer condition = new StringBuffer();
		 * if(fromWhereSQL != null){
			condition.append(fromWhereSQL.getWhere());
			condition.append(" and pk_dept = '"+pk_dept+"'");
		}else{
			condition.append(" pk_dept = '"+pk_dept+"'");
		}*/
		
	}
	
	public void pluginDeptChange(Map<String, Object> keys){
		
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	/**
	 * 从卡片界面回到列表界面的查询操作
	 * 
	 * @param keys
	 * @throws BusinessException 
	 */
	public void pluginReSearch(Map<String, Object> keys) throws BusinessException{
		FromWhereSQL fromwheresql = null;

		fromwheresql = (FromWhereSQL) AppLifeCycleContext.current()
				.getWindowContext().getAppAttribute("whereSql");

		Dataset ds = AppLifeCycleContext.current().getViewContext().getView()
				.getViewModels().getDataset(StoreShiftConsts.DS_MAIN_NAME);
		
		this.ser(ds, fromwheresql);
	}
	public void ser(Dataset ds, FromWhereSQL fromwheresql) {
		// 获得选择的管理部门
		String pk_mng_dept = SessionUtil.getPk_mng_dept();
		// 获得默认的组织
		String hrOrg = SessionUtil.getPk_org();
		// 获得默认集团
		String hrGroup = SessionUtil.getPk_group();
		// 管理部门
		String pk_dept = SessionUtil.getPk_mng_dept();
		// 是否包含下级部门
		boolean containsSubDepts = SessionUtil.isIncludeSubDept();
		LoginContext context = SessionUtil.getLoginContext();
		context.setPk_group(hrGroup);
		context.setPk_org(hrOrg);
		
		if (StringUtil.isEmptyWithTrim(pk_mng_dept)) {
			return;
		}
		LfwView viewMain = getCurrentActiveView();
		if (viewMain == null) {
			return;
		}
		if (ds == null) {
			return;
		}
		DatasetUtil.clearData(ds);
		try {
			//FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromwheresql);
			AggregatedValueObject[] aggVOs = getAggVOs(context,pk_dept,containsSubDepts,fromwheresql);
			if (aggVOs == null || aggVOs.length == 0) {
				ButtonStateManager.updateButtons();
				return;
			}
			ShiftVO[] vos = new ShiftVO[aggVOs.length];
			for(int i = 0;i<aggVOs.length;i++){
				vos[i] = (ShiftVO) aggVOs[i].getParentVO();
			}
			SuperVO[] svos = DatasetUtil.paginationMethod(ds, vos);
			new SuperVO2DatasetSerializer().serialize(svos, ds, Row.STATE_NORMAL);
			//ds.setRowSelectIndex(CommonUtil.getAppAttriSelectedIndex());
		} catch (HrssException e) {
			// TODO Auto-generated catch block
			new HrssException(e).deal();
		}
	}
	public void onDataLoad(DataLoadEvent dataLoadEvent) throws BusinessException{
		LfwView viewMain = getCurrentActiveView();
		if (viewMain == null) {
			return;
		}
		Dataset ds = AppLifeCycleContext.current().getViewContext().getView().getViewModels().getDataset(StoreShiftConsts.DS_MAIN_NAME);
		DatasetUtil.clearData(ds);
		// 我们在数据分页的时候，需要调用到Ondataload事件，那么我们需要拿到左侧查询条件，如果初始加载，查询条件为空。
		FromWhereSQL fws = (FromWhereSQL) getCurrentWindowContext().getAppAttribute("whereSql");
		ser(ds,fws);
	}
	/**
	 * 
	 * @param condition
	 * @return
	 * @throws HrssException
	 */
	protected AggregatedValueObject[] getAggVOs(LoginContext context,String pk_dept,boolean containsSubDepts,FromWhereSQL fromWhereSQL) throws HrssException{
		AggShiftVO[] vos = null;
		StringBuffer condition = new StringBuffer();
		try {
			if(!containsSubDepts){
				condition.append("  bd_shift.pk_dept = '"+pk_dept+"'");
				if(fromWhereSQL!=null){
					condition.append(" and "+fromWhereSQL.getWhere());
				}
				vos = getQueryService().queryByCondition(context,condition.toString());
			}else{
				HRDeptVO deptVO = (HRDeptVO) new BaseDAO().retrieveByPK(HRDeptVO.class, pk_dept);
				condition.append("  bd_shift.pk_dept in(select dept.pk_dept from org_dept dept where dept.innercode like '%"+deptVO.getInnercode()+"%')");
				if(fromWhereSQL!=null){
					condition.append(" and "+fromWhereSQL.getWhere());
				}
				vos = getQueryService().queryByCondition(context,condition.toString());
			}
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return vos;
	}
	
	/**
	 * 行切换事件
	 * 
	 * @param datasetEvent
	 */
	public void onAfterRowSelect(DatasetEvent datasetEvent) {
		MenubarComp menus = getCurrentActiveView().getViewMenus().getMenuBar("menu_list");
		MenuItem deleteID = menus.getElementById("list_delete");
		deleteID.setEnabled(true);
		Dataset ds = datasetEvent.getSource();
		Row row = ds.getSelectedRow();
		int enablestate = (Integer) row.getValue(ds.nameToIndex("enablestate"));
		if(enablestate==2){
			MenuItem menuID = menus.getElementById("list_disable");//
			menuID.setEnabled(true);
			MenuItem enable = menus.getElementById("list_enable");//
			enable.setEnabled(false);
		}else{
			MenuItem menuID = menus.getElementById("list_enable");//list_enable
			menuID.setEnabled(true);
			MenuItem disable = menus.getElementById("list_disable");//list_enable
			disable.setEnabled(false);
		}
	}
	/**
	 * 新增
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void addShift(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		CommonUtil.showWindowDialog("StoreShiftCard", "门店班次定义新增", "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
	}
	/**
	 * 删除
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void deleteShift(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		Dataset ds = AppLifeCycleContext.current().getViewContext().getView().getViewModels().getDataset(StoreShiftConsts.DS_MAIN_NAME);
		Row row = ds.getSelectedRow();
		if(!showConfirmDialog("您确定要删除所选数据")){
			return;
		}
		String pk_shift = (String) row.getValue(ds.nameToIndex("pk_shift"));
		AggShiftVO aggVo = getAggVOByPK(pk_shift);
		try {
			NCLocator.getInstance().lookup(IStoreShiftManageMaintain.class).delete(aggVo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			new HrssException(e).deal();
		}
		this.onDataLoad(null);
	}
	/**
	 * 启用
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void enableShift(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		Dataset ds = AppLifeCycleContext.current().getViewContext().getView().getViewModels().getDataset(StoreShiftConsts.DS_MAIN_NAME);
		Row row = ds.getSelectedRow();
		if(!showConfirmDialog("您确定要启用所选数据")){
			return;
		}
		String pk_shift = (String) row.getValue(ds.nameToIndex("pk_shift"));
		AggShiftVO aggVo = getAggVOByPK(pk_shift);
		try {
			NCLocator.getInstance().lookup(IStoreShiftManageMaintain.class).enable(aggVo);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			new HrssException(e).deal();
		}
		this.onDataLoad(null);
	}
	/**
	 * 停用
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void disableShift(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		Dataset ds = AppLifeCycleContext.current().getViewContext().getView().getViewModels().getDataset(StoreShiftConsts.DS_MAIN_NAME);
		Row row = ds.getSelectedRow();
		if(!showConfirmDialog("您确定要停用所选数据")){
			return;
		}
		String pk_shift = (String) row.getValue(ds.nameToIndex("pk_shift"));
		AggShiftVO aggVo = getAggVOByPK(pk_shift);
		try {
			NCLocator.getInstance().lookup(IStoreShiftManageMaintain.class).disable(aggVo);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			new HrssException(e).deal();
		}
		this.onDataLoad(null);
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
		getCurrentApplication().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getCurrentApplication().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, primaryKey);

		CommonUtil.showWindowDialog("StoreShiftCard", "门店班次定义编辑", "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
	}
	
	/**
	 * 显示确认提示框，并返回结果
	 * 
	 * @param msg
	 * @return
	 */
	private boolean showConfirmDialog(String msg) {
		return AppInteractionUtil.showConfirmDialog(nc.vo.ml.NCLangRes4VoTransl
				.getNCLangRes().getStrByID("c_rm-res", "0c_rm-res0003")/*
																		 * @ res
																		 * "确认对话"
																		 */,
				msg);
	}
	/**
  	 * 根据PK获取AggVo
  	 * @param pk
  	 * @return
  	 */
	private AggShiftVO getAggVOByPK(String pk){
		AggShiftVO aggVO = null;
  		try {
			aggVO = NCLocator.getInstance().lookup(IStoreShiftQueryMaintain.class).queryByPk(pk);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			new HrssException(e).deal();
		}
  		return aggVO;
	}
}
