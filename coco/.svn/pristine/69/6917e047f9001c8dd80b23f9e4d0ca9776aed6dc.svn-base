package nc.bs.hrsms.ta.sss.away.ctrl;

import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.sss.away.ShopAwayRegConsts;
import nc.bs.hrsms.ta.sss.common.ShopTaPeriodValUtils;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.qry.QueryUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.itf.hrsms.ta.away.IShopAwayRegQueryMaintain;
import nc.itf.ta.IAwayRegisterManageMaintain;
import nc.itf.ta.PeriodServiceFacade;
import nc.uap.ctrl.tpl.qry.FromWhereSQLImpl;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.ctrl.IController;
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
import nc.vo.hrsms.ta.sss.shop.ShopQryConditionVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.away.AwayRegVO;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

public class ShopAwayRegListView implements IController{

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
			if (dsSearch.nameToIndex(ShopAwayRegConsts.FD_BEGINDATE) > -1) {
				// 开始日期
				selRow.setValue(dsSearch.nameToIndex(ShopAwayRegConsts.FD_BEGINDATE), String.valueOf(dates[0]));
			}
			if (dsSearch.nameToIndex(ShopAwayRegConsts.FD_ENDDATE) > -1) {
				// 结束日期
				selRow.setValue(dsSearch.nameToIndex(ShopAwayRegConsts.FD_ENDDATE), String.valueOf(dates[1]));
			}
			
			// 清空保存的查询条件
			SessionUtil.getSessionBean().setExtendAttribute(ShopAwayRegConsts.SESSION_QRY_CONDITIONS, null);
			// 执行左侧快捷查询
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		}
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
	 * 查询操作
	 * 
	 * @param keys
	 * @throws BusinessException 
	 */
	public void pluginSearch(Map<String, Object> keys) throws BusinessException {
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(ShopAwayRegConsts.DS_MAIN_NAME);
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
		
		ShopQryConditionVO vo = this.getConditions(keys);
		nc.ui.querytemplate.querytree.FromWhereSQLImpl fws = new nc.ui.querytemplate.querytree.FromWhereSQLImpl();
		StringBuffer condition = new StringBuffer();
		condition.append(vo.getFromWhereSQL().getWhere());
		condition.append(" and awaybegindate >= '"+vo.getBeginDate()+"'" );
		condition.append(" and awayenddate <= '"+vo.getEndDate()+"'");
		try {
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
			fws.setFrom(" tbm_awayreg ");
			fws.setWhere(condition.toString()+" order by creationtime desc");
			
			AwayRegVO[] vos = this.getRegVOs(context, fws);
			SuperVO[] svos = DatasetUtil.paginationMethod(ds, vos);
			new SuperVO2DatasetSerializer().serialize(svos, ds, Row.STATE_NORMAL);
//			//默认选中第一行
			ds.setRowSelectIndex(CommonUtil.getAppAttriSelectedIndex());
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
	}
	
	public void onDataLoad(DataLoadEvent dataLoadEvent) throws BusinessException{
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	
	/**
	 * 行切换事件
	 * 
	 * @param datasetEvent
	 */
	public void onAfterRowSelect(DatasetEvent datasetEvent) {
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(ShopAwayRegConsts.DS_MAIN_NAME);
		
		Row row = ds.getSelectedRow();
		if(row == null){
			return;
		}
		MenubarComp items = (MenubarComp) getCurrentActiveView().getViewMenus().getMenuBar("menu_list");
		List<MenuItem> list = items.getMenuList();
		AwayRegVO  regVO = new Dataset2SuperVOSerializer<AwayRegVO>().serialize(ds)[0];
		//判断单据来源
		Integer billsource = regVO.getBillsource();
		for(MenuItem item:list){
			item.setEnabled(true);
			if(billsource!=null && ICommonConst.BILL_SOURCE_REG== billsource.intValue()){
				if("list_awayoff".equals(item.getId())){
					item.setEnabled(false);
				}
			}
			if(ICommonConst.BILL_SOURCE_REG != billsource.intValue()){
				if("list_delete".equals(item.getId())){
					item.setEnabled(false);
				}
			}
		}
	}
	
	
	/**
	 * 新增操作
	 * 
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void addBill(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		String operate_status = HrssConsts.POPVIEW_OPERATE_ADD;
		getCurrentApplication().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getCurrentApplication().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, null);
		showWindowDialog(operate_status);
	}
	/**
	 * 批量新增操作
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void addBatchBill(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		String operate_status = "addBatch";
		getCurrentApplication().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getCurrentApplication().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, null);
		CommonUtil.showWindowDialog("ShopAwayRegBatchCard", getPopWindowTitle(operate_status), "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
		//showWindowDialog(operate_status);
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
		showWindowDialog(operate_status);
	}
	
	/**
	 * 
	 * @param operate_status
	 */
	private void showWindowDialog(String operate_status) {
		
		CommonUtil.showWindowDialog("ShopAwayRegCard", getPopWindowTitle(operate_status), "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
	}
	
	protected String getPopWindowTitle(String operateflag) {
		if (HrssConsts.POPVIEW_OPERATE_ADD.equals(operateflag)) {
			return "店员出差登记新增";
		} else if (HrssConsts.POPVIEW_OPERATE_EDIT.equals(operateflag)) {
			return "店员出差登记修改";
		} else if (HrssConsts.POPVIEW_OPERATE_VIEW.equals(operateflag)) {
			return "店员出差登记详细";
		}else if("addBatch".equals(operateflag)){
			return "店员出差登记批量新增";
		}
		return null;
	}
	/**
	 * 查询
	 * @param context
	 * @param fws
	 * @param etraConds
	 * @return
	 */
	public AwayRegVO[] getRegVOs(LoginContext context,FromWhereSQL fws){
		AwayRegVO[] vos = null;
		try {
			IShopAwayRegQueryMaintain service = NCLocator.getInstance().lookup(IShopAwayRegQueryMaintain.class);
			vos = service.queryByCondition(context,fws.getWhere());
		}catch (BusinessException e) {
			e.printStackTrace();
		}
		return vos;
	}
	
	/**
	 * 获得查询条件.
	 * 
	 * @param keys
	 * @return
	 */
	public ShopQryConditionVO getConditions(Map<String, Object> keys) {
		ViewContext leftView = AppLifeCycleContext.current().getWindowContext().getViewContext("pubview_simplequery");
		FormComp searchForm = null;
		if (leftView != null && leftView.getView() != null) {
			searchForm = (FormComp) leftView.getView().getViewComponents().getComponent("mainform");
		}
		SessionBean sess = SessionUtil.getSessionBean();
//		ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();

		ShopQryConditionVO vo = new ShopQryConditionVO();
		FromWhereSQLImpl whereSql = (nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get(HrssConsts.PO_SEARCH_WHERESQL);
		nc.ui.querytemplate.querytree.FromWhereSQLImpl fromWhereSQL = (nc.ui.querytemplate.querytree.FromWhereSQLImpl) CommonUtil.getUAPFromWhereSQL((nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get(HrssConsts.PO_SEARCH_WHERESQL));
		// 获得自定义条件
		Map<String, String> selfDefMap = whereSql.getFieldAndSqlMap();
		
		// 查询条件-开始日期
		String beginDate = selfDefMap.get(ShopAwayRegConsts.FD_BEGINDATE);
		String endDate = selfDefMap.get(ShopAwayRegConsts.FD_ENDDATE);
		String name = selfDefMap.get("pk_psndoc_name");
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
		vo.setBeginDate(new UFLiteralDate(beginDate));
		vo.setEndDate(new UFLiteralDate(endDate));
		if(null != name){
			fromWhereSQL.setWhere(" tbm_awayreg.pk_psndoc in ( select bd_psndoc.pk_psndoc from bd_psndoc where name like '%" + name + "%') ");
		}else{
			fromWhereSQL.setWhere(" 1=1");
		}
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
		sess.setExtendAttribute(ShopAwayRegConsts.SESSION_QRY_CONDITIONS, vo);

		return vo;
	}
	/**
	 * 删除操作
	 * @param mouseEvent
	 */
	public void deleteBill(MouseEvent<MenuItem> mouseEvent){
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(ShopAwayRegConsts.DS_MAIN_NAME);
		Row row = ds.getSelectedRow();
		if(row == null){
			return;
		}
		if(CommonUtil.showConfirmDialog("提示", "确定删除所选记录？")){
			SuperVO  vo = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds)[0];
			AwayRegVO regVO = new AwayRegVO();
			String[] names = regVO.getAttributeNames();
			for(int i =0;i<names.length;i++){
				regVO.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
			}
			//判断考勤期间是否已封存
			ShopTaPeriodValUtils.getPeriodVal(regVO.getPk_org(), new AwayRegVO[]{regVO});
			try {
				NCLocator.getInstance().lookup(IAwayRegisterManageMaintain.class).deleteData(regVO);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 执行左侧快捷查询
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		}
	}
	/**
	 * 销差操作
	 * @param mouseEvent
	 */
	public void doAwayOff(MouseEvent<MenuItem> mouseEvent){
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(ShopAwayRegConsts.DS_MAIN_NAME);
		
		Row row = ds.getSelectedRow();
		if(row == null){
			return;
		}
		SuperVO  vo = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds)[0];
		AwayRegVO  regVO = new AwayRegVO();
		String[] names = regVO.getAttributeNames();
		for(int i =0;i<names.length;i++){
			regVO.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
		}
		//验证出差开始、结束日期是否在考勤期间范围内或是否已封存
		try {
			PeriodServiceFacade.checkDateScope(regVO.getPk_org(), regVO.getAwaybegindate(), regVO.getAwayenddate());
		} catch (BusinessException e) {
			new HrssException(e.getMessage()).alert();
		}
		if(CommonUtil.showConfirmDialog("提示", "确定对选中的数据执行销差操作？")){
			regVO.setBacktime(new UFDateTime());
			regVO.setIsawayoff(UFBoolean.TRUE);
			try {
				IAwayRegisterManageMaintain service = ServiceLocator.lookup(IAwayRegisterManageMaintain.class);
				service.updateData(regVO);
				CommonUtil.showShortMessage("销差成功！");
				// 执行左侧快捷查询
				CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
			} catch (HrssException e) {
//				e.printStackTrace();
				new HrssException(e.getMessage()).alert();
			} catch (BusinessException e) {
//				e.printStackTrace();
				new HrssException(e.getMessage()).alert();
			}
			
		}
	}
	

}
