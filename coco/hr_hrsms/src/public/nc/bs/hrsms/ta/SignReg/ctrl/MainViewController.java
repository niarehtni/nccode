package nc.bs.hrsms.ta.SignReg.ctrl;

import java.util.HashMap;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.SignReg.signreg.SignRegConsts;
import nc.bs.hrsms.ta.shift.StoreShiftConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.qry.QueryUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.itf.hrsms.ta.SignReg.ISignRegQueryMaintain;
import nc.itf.ta.ISignCardRegisterManageMaintain;
import nc.uap.ctrl.tpl.qry.FromWhereSQLImpl;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.bm.ButtonStateManager;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifDatasetAfterSelectCmd;
import nc.uap.lfw.core.cmd.UifEnableCmd;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.cmd.UifSaveCmdRV;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.ctx.ViewContext;
import nc.uap.lfw.core.ctx.WindowContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.hrss.ta.calendar.QryConditionVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.signcard.AggSignVO;
import nc.vo.ta.signcard.SignRegVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

import uap.lfw.core.itf.ctrl.AbstractMasterSlaveViewController;


public class MainViewController extends AbstractMasterSlaveViewController {
	private static final int MAX_QUERY_DAYS = 60;
//	private static final String PARAM_BILLITEM = "billitem";
	private static final String PLUGOUT_ID = "afterSavePlugout";
	
	/** 参数--人员主键*/
	public static final String PARAM_CI_PK_PSNDOC = "ci_pk_psndoc";
	
	public void onDataLoad_dsSignReg(DataLoadEvent dataLoadEvent) throws BusinessException {
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	
	/**
	 * 点击搜索后,页面重新加载事件
	 * 
	 * @param keys
	 * @throws BusinessException 
	 */
	public void pluginSearch(Map<String, Object> keys) throws BusinessException {
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(SignRegConsts.DS_MAIN_NAME);
		if (keys == null || keys.size() == 0) {
			return;
		}
		// 获得默认的组织
		String hrOrg = SessionUtil.getPk_org();
		// 获得默认集团
		String hrGroup = SessionUtil.getPk_group();
		// 是否包含下级部门
		boolean isContainSub = SessionUtil.isIncludeSubDept();
		// 查询条件-部门
		String pk_dept = SessionUtil.getPk_mng_dept();
		if (StringUtils.isEmpty(pk_dept)) {
				return;
		}
     	QryConditionVO vo = this.getConditions(keys);
		nc.ui.querytemplate.querytree.FromWhereSQLImpl fws = new nc.ui.querytemplate.querytree.FromWhereSQLImpl();
		StringBuffer condition = new StringBuffer();
		condition.append(vo.getFromWhereSQL().getWhere());
		condition.append(" and SIGNDATE between '"+vo.getBeginDate()+"' and '"+vo.getEndDate()+"' ");
		
		try{
			LoginContext context = SessionUtil.getLoginContext();
			context.setPk_group(hrGroup);
			context.setPk_org(hrOrg);
			if(!isContainSub){
				condition.append(" and pk_psndoc in (select pk_psndoc from bd_psnjob where pk_dept = '"+pk_dept+"')");
			}else{
				HRDeptVO deptVO = (HRDeptVO) new BaseDAO().retrieveByPK(HRDeptVO.class, pk_dept);
				condition.append(" and pk_psndoc in (select pk_psndoc from bd_psnjob where pk_dept in" +
						"(select dept.pk_dept from org_dept dept where dept.innercode like '%"+deptVO.getInnercode()+"%') )");
			}
			
			fws.setFrom(" tbm_signreg ");
			fws.setWhere(condition.toString()+" order by creationtime desc");
			SignRegVO[] vos = this.getRegVOs(context,fws);
			SuperVO[] svos = DatasetUtil.paginationMethod(ds, vos);
			new SuperVO2DatasetSerializer().serialize(svos, ds, Row.STATE_NORMAL);
			//默认选中第一行
			ds.setRowSelectIndex(CommonUtil.getAppAttriSelectedIndex());
			
		}
		catch(BusinessException e){
			new HrssException(e).deal();
		}
		
	}
	/**
	 * 查询
	 * @param context
	 * @param fws
	 * @param etraConds
	 * @return
	 */
	public SignRegVO[] getRegVOs(LoginContext context,FromWhereSQL fws){
		SignRegVO[] vos = null;
		try {
			ISignRegQueryMaintain service = NCLocator.getInstance().lookup(ISignRegQueryMaintain.class);
			vos = service.queryVOsByCondition(context, fws.getWhere());
		}catch (BusinessException e) {
			e.printStackTrace();
		}
		return vos;
	}
	protected AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}
	
	/**
	 * 获得查询条件.
	 * 
	 * @param keys
	 * @return
	 */
	public QryConditionVO getConditions(Map<String, Object> keys) {
		ViewContext leftView = AppLifeCycleContext.current().getWindowContext().getViewContext("pubview_simplequery");
		FormComp searchForm = null;
		if (leftView != null && leftView.getView() != null) {
			searchForm = (FormComp) leftView.getView().getViewComponents().getComponent("mainform");
		}
		SessionBean sess = SessionUtil.getSessionBean();
//		ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();
		QryConditionVO vo =  new QryConditionVO();
		
		FromWhereSQLImpl whereSql = (nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get(HrssConsts.PO_SEARCH_WHERESQL);
		nc.ui.querytemplate.querytree.FromWhereSQLImpl fromWhereSQL = (nc.ui.querytemplate.querytree.FromWhereSQLImpl) CommonUtil.getUAPFromWhereSQL((nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get(HrssConsts.PO_SEARCH_WHERESQL));
		// 获得自定义条件
		Map<String, String> selfDefMap = whereSql.getFieldAndSqlMap();
		// 查询条件-开始日期
			String beginDate = selfDefMap.get(SignRegConsts.FD_BEGINDATE);
			String endDate = selfDefMap.get(SignRegConsts.FD_ENDDATE);
			if (StringUtils.isEmpty(beginDate)) {
				CommonUtil.showCompErrorDialog(searchForm, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0017")/*
																																			 * @
																																			 * res
																																			 * "开始日期不能为空！"
																																			 */);

			}
			// 查询条件-结束日期
			if (StringUtils.isEmpty(endDate)) {
				CommonUtil.showCompErrorDialog(searchForm, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0018")/*
																																			 * @
																																			 * res
																																			 * "结束日期不能为空，请输入！"
																																			 */);
			}
			if (new UFLiteralDate(beginDate).afterDate(new UFLiteralDate(endDate))) {
				CommonUtil.showCompErrorDialog(searchForm, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0019")/*
																																			 * @
																																			 * res
																																			 * "开始日期不能晚于结束日期，请重新输入！"
																																			 */);

			}
			if (new UFLiteralDate(endDate).after(new UFLiteralDate(beginDate).getDateAfter(MAX_QUERY_DAYS))) {
				CommonUtil.showCompErrorDialog(searchForm, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0139")/*
																																			 * @
																																			 * res
																																			 * 开始日期和结束日期的间隔天数大于60天
																																			 * ，
																																			 * 请重新输入
																																			 * ！
																																			 */);

			}
			vo.setBeginDate(new UFLiteralDate(beginDate));
			vo.setEndDate(new UFLiteralDate(endDate));
			String psnScopeSqlPart = null;
			try {
				psnScopeSqlPart = QueryUtil.getDeptPsnCondition();
			} catch (BusinessException e) {
//				new HrssException(e).deal();
			}

			if (fromWhereSQL != null && !StringUtils.isEmpty(psnScopeSqlPart)) {
                (fromWhereSQL).setWhere(fromWhereSQL.getWhere());
			
			}

			vo.setFromWhereSQL(fromWhereSQL);
			// 记录本次查询条件
			sess.setExtendAttribute(SignRegConsts.SESSION_QRY_CONDITIONS, vo);
		
		return vo;
	}
	

	/**
	 * 从卡片界面回到列表界面的查询操作
	 * 
	 * @param keys
	 */
	public void pluginReSearch(Map<String, Object> keys){

		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		
		
		
	}
	/**
	 * 从批量新增卡片界面回到列表界面的查询操作
	 * 
	 * @param keys
	 */
	public void pluginBatch_ReSearch(Map<String, Object> keys){
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	/**
	 * 部门切换
	 * @param keys
	 * @throws BusinessException 
	 */
    public void pluginDeptChange(Map<String, Object> keys) throws BusinessException{
    	LfwView simpQryView = getCurrentWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset("mainds");
		if (dsSearch != null) {
			Row selRow = dsSearch.getSelectedRow();
			// 管理部门所在的HR组织
			String pk_hr_org = SessionUtil.getPsndocVO().getPk_group();
			UFLiteralDate[] dates = TBMPeriodUtil.getDefaultBeginEndDateByPkOrg(pk_hr_org);
			if (dsSearch.nameToIndex(SignRegConsts.FD_BEGINDATE) > -1) {
				// 开始日期
				selRow.setValue(dsSearch.nameToIndex(SignRegConsts.FD_BEGINDATE), String.valueOf(dates[0]));
			}
			if (dsSearch.nameToIndex(SignRegConsts.FD_ENDDATE) > -1) {
				// 结束日期
				selRow.setValue(dsSearch.nameToIndex(SignRegConsts.FD_ENDDATE), String.valueOf(dates[1]));
			}
			
			// 清空保存的查询条件
			SessionUtil.getSessionBean().setExtendAttribute(SignRegConsts.SESSION_QRY_CONDITIONS, null);
			// 执行左侧快捷查询
		    CmdInvoker.invoke(new PlugoutSimpleQueryCmd());}
	}

	/**
	 * 主数据选中逻辑
	 * @param datasetEvent
	 */
	public void onAfterRowSelect(DatasetEvent dsEvent) {
		Dataset ds = dsEvent.getSource();
		CmdInvoker.invoke(new UifDatasetAfterSelectCmd(ds.getId()));
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

		CommonUtil.showWindowDialog("SignRegCard", getPopWindowTitle(operate_status), "70%", "80%", null, ApplicationContext.TYPE_DIALOG, false, false);
	}
	/**
	 * 新增签卡
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void addNewSignReg(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		String operate_status = HrssConsts.POPVIEW_OPERATE_ADD;
		getCurrentApplication().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getCurrentApplication().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, null);
		CommonUtil.showWindowDialog("SignRegCard", getPopWindowTitle(operate_status), "70%", "80%", null, ApplicationContext.TYPE_DIALOG, false, false);
	
	}
	protected String getPopWindowTitle(String operateflag) {
		if (HrssConsts.POPVIEW_OPERATE_ADD.equals(operateflag)) {
			return "新增签卡登记";
		}
		 else if (HrssConsts.POPVIEW_OPERATE_EDIT.equals(operateflag)) {
			return "签卡登记修改";
		} 
		else if (HrssConsts.POPVIEW_OPERATE_VIEW.equals(operateflag)) {
			return "签卡登记详细";
		}
		else if("addBatch".equals(operateflag)){
			return "批量新增";
		}
		return null;
	}
	/**
	 * 获得当前Application
	 */
	private ApplicationContext getCurrentApplication() {
		return AppLifeCycleContext.current().getApplicationContext();
	}
	/**
	 * 批量新增
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void addBatchSignReg(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		String operate_status = "addBatch";
		getCurrentApplication().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getCurrentApplication().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, null);
		CommonUtil.showWindowDialog("BatchSignReg", getPopWindowTitle(operate_status), "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
		
	
	}
	/**
	 * 删除签卡
	 * @param mouseEvent
	 * @throws ValidationException 
	 */
	public void deleteSignReg(MouseEvent<MenuItem> mouseEvent) throws ValidationException {
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(SignRegConsts.DS_MAIN_NAME);
		Row row = ds.getSelectedRow();
		if(row == null){
			return;
		}
		if(!showConfirmDialog("您确定要删除所选数据")){
			return;
		}
		else{
			SuperVO  vo = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds)[0];
			nc.vo.ta.signcard.SignRegVO sgnvo=new nc.vo.ta.signcard.SignRegVO();
			String[] names = sgnvo.getAttributeNames();
			for(int i =0;i<names.length;i++){
				sgnvo.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
			}
			//检验
			SignCardRegDeleteValidator vlt=new SignCardRegDeleteValidator();
			vlt.validate(sgnvo);
			try {
				NCLocator.getInstance().lookup(ISignCardRegisterManageMaintain.class).deleteData(sgnvo);
			} catch (BusinessException e) {
				e.printStackTrace();
			}
			// 执行左侧快捷查询
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		}
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
	 * @param condition
	 * @return
	 * @throws HrssException
	 */
	protected AggregatedValueObject[] getAggVOs(LoginContext context,String condition) throws HrssException{
		AggSignVO[] vos = null;
		try {
			vos = getQueryService().queryByCondition(context,condition);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return vos;
	}
	private ISignRegQueryMaintain getQueryService(){
		ISignRegQueryMaintain service = NCLocator.getInstance().lookup(ISignRegQueryMaintain.class);
		return service;
	}
	public void onDataLoad(DataLoadEvent dataLoadEvent){
		LfwView viewMain = getCurrentActiveView();
		if (viewMain == null) {
			return;
		}
		Dataset ds = AppLifeCycleContext.current().getViewContext().getView().getViewModels().getDataset(StoreShiftConsts.DS_MAIN_NAME);
		DatasetUtil.clearData(ds);
		// 我们在数据分页的时候，需要调用到Ondataload事件，那么我们需要拿到左侧查询条件，如果初始加载，查询条件为空。
		FromWhereSQL fws = (FromWhereSQL) getCurrentWindowContext().getAppAttribute("whereSql");
		try {
			String pk_dept = SessionUtil.getPk_mng_dept();
			String condition = " pk_dept = '"+pk_dept+"'";
			if(fws != null){
				condition = condition+" and "+fws.getWhere();
			}
			AggregatedValueObject[] aggVOs = getAggVOs(null,condition);
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
		} catch (HrssException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * 保存
	 * @param mouseEvent
	 */
	public void onSave(MouseEvent<?> mouseEvent){
		Dataset masterDs = this.getMasterDs();
		CmdInvoker.invoke(new UifSaveCmdRV(this.getMasterDsId(), this.getDetailDsIds(), false));
		masterDs.setEnabled(true);
		this.getCurrentAppCtx().closeWinDialog();
		
		Map<String, Object> paramMap = new HashMap<String, Object>(2);
		Row savedRow = masterDs.getSelectedRow();
		paramMap.put(OPERATE_ROW, savedRow);
		CmdInvoker.invoke(new UifPlugoutCmd(this.getCurrentView().getId(), PLUGOUT_ID, paramMap));
	}

	/**
	 * 启用
	 * @param mouseEvent
	 */
	public void onStart(MouseEvent<?> mouseEvent) {
		CmdInvoker.invoke(new UifEnableCmd(this.getMasterDsId(), true));
	}

	/**
	 * 停用
	 * @param mouseEvent
	 */
	public void onStop(MouseEvent<?> mouseEvent) {
		CmdInvoker.invoke(new UifEnableCmd(this.getMasterDsId(), false));
	}


	/**
	 * 返回
	 * @param mouseEvent
	 */
	public void onBack(MouseEvent<?> mouseEvent) {
		this.getCurrentAppCtx().closeWinDialog();
	}

	/**
	 * 获取主数据集id
	 * @return String
	 */
	@Override
	protected String getMasterDsId() {
		return "cardds";
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
	
}