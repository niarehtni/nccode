package nc.bs.hrsms.ta.sss.overtime.ctrl;

import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrsms.ta.sss.common.ShopTaPeriodValUtils;
import nc.bs.hrsms.ta.sss.overtime.ShopOverTimeRegConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.qry.QueryUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.itf.hrsms.ta.overtime.IShopOvertimeRegQueryMaintain;
import nc.itf.ta.IOvertimeRegisterManageMaintain;
import nc.itf.ta.IOvertimeRegisterQueryMaintain;
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
import nc.uap.lfw.core.exception.LfwRuntimeException;
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
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class ShopOverTimeRegListView implements IController{

	/**
	 * ��õ�ǰApplication
	 */
	private ApplicationContext getCurrentApplication() {
		return AppLifeCycleContext.current().getApplicationContext();
	}
	/**
	 * ��õ�ǰƬ��
	 * 
	 * @return
	 */
	protected LfwView getCurrentActiveView() {
		return AppLifeCycleContext.current().getViewContext().getView();
	}
	/**
	 * ��õ�ǰWindowContext
	 */
	private WindowContext getCurrentWindowContext() {
		return AppLifeCycleContext.current().getWindowContext();
	}
	/**
	 * �����л�
	 * @param keys
	 * @throws BusinessException 
	 */
	public void pluginDeptChange(Map<String, Object> keys) throws BusinessException{
		LfwView simpQryView = getCurrentWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset("mainds");
		if (dsSearch != null) {
			Row selRow = dsSearch.getSelectedRow();
			// ���������ڵ�HR��֯
			String pk_hr_org = SessionUtil.getPsndocVO().getPk_group();
			UFLiteralDate[] dates = TBMPeriodUtil.getDefaultBeginEndDateByPkOrg(pk_hr_org);
			if (dsSearch.nameToIndex(ShopOverTimeRegConsts.FD_BEGINDATE) > -1) {
				// ��ʼ����
				selRow.setValue(dsSearch.nameToIndex(ShopOverTimeRegConsts.FD_BEGINDATE), String.valueOf(dates[0]));
			}
			if (dsSearch.nameToIndex(ShopOverTimeRegConsts.FD_ENDDATE) > -1) {
				// ��������
				selRow.setValue(dsSearch.nameToIndex(ShopOverTimeRegConsts.FD_ENDDATE), String.valueOf(dates[1]));
			}
			
			// ��ձ���Ĳ�ѯ����
			SessionUtil.getSessionBean().setExtendAttribute(ShopOverTimeRegConsts.SESSION_QRY_CONDITIONS, null);
			// ִ������ݲ�ѯ
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
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
	 * ������������Ƭ����ص��б����Ĳ�ѯ����
	 * 
	 * @param keys
	 */
	public void pluginBatch_ReSearch(Map<String, Object> keys){
		// ִ������ݲ�ѯ
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	
	/**
	 * ��ѯ����
	 * 
	 * @param keys
	 * @throws BusinessException 
	 */
	public void pluginSearch(Map<String, Object> keys) throws BusinessException {
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(ShopOverTimeRegConsts.DS_MAIN_NAME);
		if (keys == null || keys.size() == 0) {
			return;
		}
		// ���Ĭ�ϵ���֯
		String hrOrg = SessionUtil.getPk_org();
		// ���Ĭ�ϼ���
		String hrGroup = SessionUtil.getPk_group();
		// �Ƿ�����¼�����
		boolean isContainSub = SessionUtil.isIncludeSubDept();
		// ��ѯ����-����
		String pk_dept = SessionUtil.getPk_mng_dept();
		if (StringUtils.isEmpty(pk_dept)) {
			return;
		}
		
		ShopQryConditionVO vo = this.getConditions(keys);
		nc.ui.querytemplate.querytree.FromWhereSQLImpl fws = new nc.ui.querytemplate.querytree.FromWhereSQLImpl();
		StringBuffer condition = new StringBuffer();
		condition.append(vo.getFromWhereSQL().getWhere());
		condition.append(" and overtimebegindate >= '"+vo.getBeginDate()+"'" );
		condition.append(" and overtimeenddate <= '"+vo.getEndDate()+"'");
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
			fws.setFrom(" tbm_overtimereg ");
			fws.setWhere(condition.toString()+" order by creationtime desc");
			
			OvertimeRegVO[] vos = this.getRegVOs(context,fws);
			SuperVO[] svos = DatasetUtil.paginationMethod(ds, vos);
			new SuperVO2DatasetSerializer().serialize(svos, ds, Row.STATE_NORMAL);
			//Ĭ��ѡ�е�һ��
			ds.setRowSelectIndex(CommonUtil.getAppAttriSelectedIndex());
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
	}
	
	public void onDataLoad(DataLoadEvent dataLoadEvent) throws BusinessException{
		// ִ������ݲ�ѯ
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	
	/**
	 * ���л��¼�
	 * 
	 * @param datasetEvent
	 */
	public void onAfterRowSelect(DatasetEvent datasetEvent) {
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(ShopOverTimeRegConsts.DS_MAIN_NAME);
		
		Row row = ds.getSelectedRow();
		if(row == null){
			return;
		}
		MenubarComp items = (MenubarComp) getCurrentActiveView().getViewMenus().getMenuBar("menu_list");
		List<MenuItem> list = items.getMenuList();
		OvertimeRegVO  regVO = new Dataset2SuperVOSerializer<OvertimeRegVO>().serialize(ds)[0];
		//�жϵ�����Դ
		Integer billsource = regVO.getBillsource();
		//�ж��Ƿ���ת����
		UFBoolean istorest = regVO.getIstorest();
		for(MenuItem item:list){
			item.setEnabled(true);
			if(ICommonConst.BILL_SOURCE_REG!=billsource||istorest.booleanValue()){
				if("list_delete".equals(item.getId())){
					item.setEnabled(false);
				}
			}
			if(!regVO.getIsneedcheck().booleanValue() || regVO.getIscheck().booleanValue() || regVO.getIstorest().booleanValue()){
				if("list_validator".equals(item.getId())){
					item.setEnabled(false);
				}
			}
			//δУ��Ĳ���Ҫ��У��/ת���ݵĲ��ܷ�У�� 
			if(!regVO.getIscheck().booleanValue()||istorest.booleanValue()){
				if("list_unvalidator".equals(item.getId())){
					item.setEnabled(false);
				}
			}			
		}
	}
	
	
	/**
	 * ��������
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
	 * ������������
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void addBatchBill(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		String operate_status = "addBatch";
		getCurrentApplication().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getCurrentApplication().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, null);
		CommonUtil.showWindowDialog("ShopOverTimeRegBatchCard", getPopWindowTitle(operate_status), "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
		//showWindowDialog(operate_status);
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
		getCurrentApplication().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getCurrentApplication().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, primaryKey);
		showWindowDialog(operate_status);
	}
	/**
	 * ˢ/ǩ����Ϣ
	 * @param mouseEvent
	 */
	public void qryCardInfo(MouseEvent<MenuItem> mouseEvent) {
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(ShopOverTimeRegConsts.DS_MAIN_NAME);
		Row row = ds.getSelectedRow();
		if(row == null){
			return;
		}
		String pk_overtimereg = (String) row.getValue(ds.nameToIndex("pk_overtimereg"));
		getCurrentApplication().addAppAttribute("CardInfo_pk_overtimereg", pk_overtimereg);
		CommonUtil.showWindowDialog("ShopOverTimeCardInfo", "ˢ/ǩ����Ϣ", 800, 450, null, ApplicationContext.TYPE_DIALOG);
	}
	/**
	 * 
	 * @param operate_status
	 */
	private void showWindowDialog(String operate_status) {
		
		CommonUtil.showWindowDialog("ShopOverTimeRegCard", getPopWindowTitle(operate_status), "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
	}
	
	protected String getPopWindowTitle(String operateflag) {
		if (HrssConsts.POPVIEW_OPERATE_ADD.equals(operateflag)) {
			return "��Ա�Ӱ�Ǽ�����";
		} else if (HrssConsts.POPVIEW_OPERATE_EDIT.equals(operateflag)) {
			return "��Ա�Ӱ�Ǽ��޸�";
		} else if (HrssConsts.POPVIEW_OPERATE_VIEW.equals(operateflag)) {
			return "��Ա�Ӱ�Ǽ���ϸ";
		}else if("addBatch".equals(operateflag)){
			return "��Ա�Ӱ�Ǽ���������";
		}
		return null;
	}
	/**
	 * ��ѯ
	 * @param context
	 * @param fws
	 * @param etraConds
	 * @return
	 */
	public OvertimeRegVO[] getRegVOs(LoginContext context,FromWhereSQL fws){
		OvertimeRegVO[] vos = null;
		try {
			IShopOvertimeRegQueryMaintain service = NCLocator.getInstance().lookup(IShopOvertimeRegQueryMaintain.class);
			vos = service.queryByCondition(context,fws.getWhere());
		}catch (BusinessException e) {
			e.printStackTrace();
		}
		return vos;
	}
	
	/**
	 * ��ò�ѯ����.
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
		// ����Զ�������
		Map<String, String> selfDefMap = whereSql.getFieldAndSqlMap();
		
		// ��ѯ����-��ʼ����
		String beginDate = selfDefMap.get(ShopOverTimeRegConsts.FD_BEGINDATE);
		String endDate = selfDefMap.get(ShopOverTimeRegConsts.FD_ENDDATE);
		String name = selfDefMap.get("pk_psndoc_name");
		if (StringUtils.isEmpty(beginDate)) {
			CommonUtil.showCompErrorDialog(searchForm, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0017")/*
																																		 * @
																																		 * res
																																		 * "��ʼ���ڲ���Ϊ�գ�"
																																		 */);

		}
		// ��ѯ����-��������
		if (StringUtils.isEmpty(endDate)) {
			CommonUtil.showCompErrorDialog(searchForm, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0018")/*
																																		 * @
																																		 * res
																																		 * "�������ڲ���Ϊ�գ������룡"
																																		 */);
		}
		if (new UFLiteralDate(beginDate).afterDate(new UFLiteralDate(endDate))) {
			CommonUtil.showCompErrorDialog(searchForm, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0019")/*
																																		 * @
																																		 * res
																																		 * "��ʼ���ڲ������ڽ������ڣ����������룡"
																																		 */);

		}
		vo.setBeginDate(new UFLiteralDate(beginDate));
		vo.setEndDate(new UFLiteralDate(endDate));
		if(null != name){
			fromWhereSQL.setWhere(" tbm_overtimereg.pk_psndoc in ( select bd_psndoc.pk_psndoc from bd_psndoc where name like '%"+ name +"%')");
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
		// ��¼���β�ѯ����
		sess.setExtendAttribute(ShopOverTimeRegConsts.SESSION_QRY_CONDITIONS, vo);

		return vo;
	}
	/**
	 * ɾ������
	 * @param mouseEvent
	 */
	public void deleteBill(MouseEvent<MenuItem> mouseEvent){
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(ShopOverTimeRegConsts.DS_MAIN_NAME);
		Row row = ds.getSelectedRow();
		if(row == null){
			return;
		}
		if(CommonUtil.showConfirmDialog("��ʾ", "ȷ��ɾ����ѡ��¼��")){
			SuperVO  vo = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds)[0];
			OvertimeRegVO regVO = new OvertimeRegVO();
			String[] names = regVO.getAttributeNames();
			for(int i =0;i<names.length;i++){
				regVO.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
			}
			//�жϵ�����Դ
//			Integer billsource = regVO.getBillsource();
//			if(ICommonConst.BILL_SOURCE_REG!=billsource){
//				throw new LfwRuntimeException("ֻ��ɾ���Ǽǽڵ����������ݣ�");
//			}
			//�ж��Ƿ���ת����
//			UFBoolean istorest = regVO.getIstorest();
//			if(istorest.booleanValue()){
//				throw new LfwRuntimeException("��ת���ݵĵ��ݲ���ɾ��");
//			}
			//�жϿ����ڼ��Ƿ��ѷ��
			ShopTaPeriodValUtils.getPeriodVal(regVO.getPk_org(), new OvertimeRegVO[]{regVO});
			try {
				NCLocator.getInstance().lookup(IOvertimeRegisterManageMaintain.class).deleteData(regVO);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// ִ������ݲ�ѯ
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		}
	}
	/**
	 * У�����
	 * @param mouseEvent
	 */
	public void doCheck(MouseEvent<MenuItem> mouseEvent){
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(ShopOverTimeRegConsts.DS_MAIN_NAME);
		Row row = ds.getSelectedRow();
		if(row == null){
			return;
		}
		SuperVO  vo = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds)[0];
		OvertimeRegVO regVO = new OvertimeRegVO();
		String[] names = regVO.getAttributeNames();
		for(int i =0;i<names.length;i++){
			regVO.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
		}
//		if(!regVO.getIsneedcheck().booleanValue() || regVO.getIscheck().booleanValue() || regVO.getIstorest().booleanValue()){
//			return;
//		}
		if(CommonUtil.showConfirmDialog("��ʾ", "ȷ��У����ѡ�Ӱ�������")){
			try {
				OvertimeRegVO[] unChangeVOs = NCLocator.getInstance().lookup(IOvertimeRegisterQueryMaintain.class).doBeforeCheck(new OvertimeRegVO[]{regVO});
				if(!ArrayUtils.isEmpty(unChangeVOs)){
					throw new LfwRuntimeException("��ѡ���ݼӰ�ʱ�����ڵĿ����ڼ��ѷ�����ת���ݣ����ݲ������޸ġ�");
				}
				// У�鵥��
				NCLocator.getInstance().lookup(IOvertimeRegisterManageMaintain.class).doCheck(new OvertimeRegVO[]{regVO});
				// ִ������ݲ�ѯ
				CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * ��У�����
	 * @param mouseEvent
	 */
	public void doUnCheck(MouseEvent<MenuItem> mouseEvent){
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(ShopOverTimeRegConsts.DS_MAIN_NAME);
		Row row = ds.getSelectedRow();
		if(row == null){
			return;
		}
		SuperVO  vo = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds)[0];
		OvertimeRegVO regVO = new OvertimeRegVO();
		String[] names = regVO.getAttributeNames();
		for(int i =0;i<names.length;i++){
			regVO.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
		}
//		if(!regVO.getIscheck().booleanValue()) //δУ��Ĳ���Ҫ��У��
//			throw new LfwRuntimeException("δУ��Ĳ���Ҫ��У��");
//		if(regVO.getIstorest().booleanValue()){//ת���ݵĲ��ܷ�У�� 
//			throw new LfwRuntimeException("������ת���ݵ��ݣ�������У�飡");
//		}
		if(CommonUtil.showConfirmDialog("��ʾ", "ȷ��У����ѡ�Ӱ�������")){
			try {
				NCLocator.getInstance().lookup(IOvertimeRegisterManageMaintain.class).undoCheck(new OvertimeRegVO[]{regVO});
				// ִ������ݲ�ѯ
				CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
}
