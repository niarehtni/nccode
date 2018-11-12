package nc.bs.hrss.ta.common.ctrl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PFApproveInfoCmd;
import nc.bs.hrss.pub.cmd.PFCommitCmd;
import nc.bs.hrss.pub.cmd.PFDeleteCmd;
import nc.bs.hrss.pub.cmd.PFReCallCmd;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.cmd.base.HrssBillCommand;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.common.TaApplyConsts;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.hr.utils.ResHelper;
import nc.itf.hrss.pub.cmd.prcss.ICommitProcessor;
import nc.itf.ta.ISignCardApplyQueryMaintain;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.MDPersistenceService;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.WebContext;
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
import nc.uap.lfw.core.model.plug.TranslatedRow;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Datasets2AggVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.ta.pub.TAPFBillQueryParams;
import nc.vo.ta.signcard.AggSignVO;

import org.apache.commons.lang.StringUtils;

/**
 * ViewList的父类
 * 
 * @author qiaoxp
 * 
 */
public abstract class TaListBaseView extends BaseController {
	/**
	 * 标准功能签卡申请的节点编码
	 */
	private static final String SIGN_CARD_APPLY_APP_OLD = "E20200917";
	/**
	 * 新增店长代签卡申请的节点编码
	 */
	private static final String SIGN_CARD_APPLY_APP_SHOP = "E2H600981";


	private static final long serialVersionUID = 1L;

	/**
	 * 单据类型编码
	 * 
	 * @return
	 */
	protected abstract String getBillTypeCode();

	/**
	 * 主数据集ID
	 * 
	 * @return
	 */
	protected abstract String getDatasetId();

	/**
	 * 提交的操作类
	 * 
	 * @return
	 */
	protected Class<? extends ICommitProcessor> getCommitPrcss() {
		return null;
	}

	/**
	 * 聚合VO
	 * 
	 * @return
	 */
	protected abstract Class<? extends AggregatedValueObject> getAggVOClazz();

	/**
	 * 查询条件的主实体Class
	 * 
	 * @return
	 */
	protected abstract Class<? extends SuperVO> getMainEntityClazz();

	/**
	 * 查询结果
	 * 
	 * @return
	 */
	protected abstract AggregatedValueObject[] getAggVOs(FromWhereSQL fromWhereSQL);

	/**
	 * 弹出页面的WindowId
	 * 
	 * @return
	 */
	protected abstract String getPopWindowId();

	/**
	 * 获得页面标题
	 * 
	 * @param operateflag
	 * @return
	 */
	protected abstract String getPopWindowTitle(String operateflag);

	/**
	 * 数据加载事件
	 * 
	 * @param dataLoadEvent
	 */
	protected void onDataLoad(DataLoadEvent dataLoadEvent) {
		
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) getApplicationContext().getAppAttribute(TaApplyConsts.APP_ATTR_FROMWHERESQL);
		if (fromWhereSQL == null) {
		 // 初始化操作
//			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());   提高效率不调用查询模板进行搜索 by tianxx5
			//获取页面查询条件，主动查询  by tianxx5
			Dataset ds = dataLoadEvent.getSource();
			Dataset mainds = getLifeCycleContext().getWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView().getViewModels().getDataset("mainds");
			Row selRow = mainds.getSelectedRow();
			if (selRow == null) {
				selRow = DatasetUtil.initWithEmptyRow(mainds, true, Row.STATE_NORMAL);
			}
			//如果获取日期为空,则加入默认的日期 Ares.Tank 2018-7-19 11:11:01 start
			int start_index = mainds.nameToIndex("apply_date_start");
			UFLiteralDate apply_date_start = new UFLiteralDate();
			if(start_index>=0){
				apply_date_start = (UFLiteralDate) selRow.getValue(mainds.nameToIndex("apply_date_start"));
			}else{//获取默认日期
				Date dt =new Date();
				Calendar rightNow = Calendar.getInstance();
				rightNow.setTime(dt);
				rightNow.add(Calendar.MONTH,-1);//日期－1个月
				Date dt1=rightNow.getTime();
				apply_date_start = new UFLiteralDate(dt1);
			}
			
			int end_index = mainds.nameToIndex("apply_date_end");
			Object apply_date_end = new UFLiteralDate();
			if(end_index>=0){
				apply_date_end = selRow.getValue(mainds.nameToIndex("apply_date_end"));
			}
			//如果获取日期为空,则加入默认的日期 Ares.Tank 2018-7-19 11:11:01 end
			String tableName = ds.getTableName();
			Map<String,String> attrpath_alias_map = new HashMap<String,String>();
			attrpath_alias_map.put(".", tableName);
			fromWhereSQL = new FromWhereSQLImpl();
			fromWhereSQL.setAttrpath_alias_map(attrpath_alias_map);
			String from = tableName+" "+tableName;
			fromWhereSQL.setFrom(from);
			String where = tableName+".apply_date >='"+ apply_date_start +"' and "+ tableName+".apply_date <='"+ apply_date_end +" 23:59:59'";
			fromWhereSQL.setWhere(where);
			getTaApplyDatas(fromWhereSQL,ds);
		} else {
			Dataset ds = dataLoadEvent.getSource();
			getTaApplyDatas(fromWhereSQL, ds);
		}
	}

	/**
	 * 关闭弹出页面后的再查询操作
	 * 
	 * @param keys
	 */
	public void pluginReSearch(Map<String, Object> keys) {
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) getApplicationContext().getAppAttribute(TaApplyConsts.APP_ATTR_FROMWHERESQL);
		Dataset ds = getCurrentView().getViewModels().getDataset(getDatasetId());
		getTaApplyDatas(fromWhereSQL, ds);
	}

	/**
	 * 查询操作
	 * 
	 * @param keys
	 */
	public void pluginSearch(Map<String, Object> keys) {
		TBMPsndocUtil.checkTimeRuleVO();
		if (keys == null || keys.size() == 0) {
			return;
		}
		Dataset ds = getCurrentView().getViewModels().getDataset(getDatasetId());
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) CommonUtil.getUAPFromWhereSQL((nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get("where"));
		
		ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();
		//左侧页签对应的节点pk
		String pk_node = (String) SessionUtil.getAttribute("pk_node");
//		pk_node =  (String) AppLifeCycsleContext.current().getWindowContext().getAppAttribute(pk_node);
		if(fromWhereSQL != null){
			String where = fromWhereSQL.getWhere();
			//调班
			int changebb = StringUtils.indexOf(where, "changeshift_sub_changeshiftbegindate");
			int changebe = StringUtils.indexOf(where, "changeshift_sub_changeshiftenddate");
			if(changebb>=0&&changebe>=0){
				if(changebb==0){
					where = where.replaceFirst("changeshift_sub_changeshiftbegindate",
							"  pk_changeshifth in (select pk_changeshifth from tbm_changeshiftb where tbm_changeshiftb.changeshiftbegindate");
				}else{
					where = where.replaceFirst("changeshift_sub_changeshiftbegindate",
							" and pk_changeshifth in (select pk_changeshifth from tbm_changeshiftb where tbm_changeshiftb.changeshiftbegindate");
				}
				where = where.replaceFirst("changeshift_sub_changeshiftenddate",
						" and tbm_changeshiftb.changeshiftenddate " );
			}else if(changebb>=0&&changebe<0){
				if(changebb ==0){
					where = where.replaceFirst("changeshift_sub_changeshiftbegindate",
							"  pk_changeshifth in (select pk_changeshifth from tbm_changeshiftb where tbm_changeshiftb.changeshiftbegindate");
				}else{
					where = where.replaceFirst("changeshift_sub_changeshiftbegindate",
							" and pk_changeshifth in (select pk_changeshifth from tbm_changeshiftb where tbm_changeshiftb.changeshiftbegindate");
				}
			}else if(changebb<0&&changebe>=0){
				if(changebe ==0){
					where = where.replaceFirst("changeshift_sub_changeshiftenddate",
							"  pk_changeshifth in (select pk_changeshifth from tbm_changeshiftb where tbm_changeshiftb.changeshiftenddate" );
				}else{
					where = where.replaceFirst("changeshift_sub_changeshiftenddate",
							" and pk_changeshifth in (select pk_changeshifth from tbm_changeshiftb where tbm_changeshiftb.changeshiftenddate" );
				}
			}
			//签卡
			int signdate = StringUtils.indexOf(where, "signb_sub_signdate");
			if(signdate == 0){
				where = where.replaceFirst("signb_sub_signdate",
						"  pk_signh in ( select pk_signh from tbm_signb where signdate ");
			}else{
				where = where.replaceFirst("signb_sub_signdate",
						" and pk_signh in ( select pk_signh from tbm_signb where signdate ");
			}
			where = where.replaceFirst("signb_sub_signdate",
					" signdate " );
			//出差
			int awaybbegin = StringUtils.indexOf(where, "away_sub_awaybegindate");
			int awaybend = StringUtils.indexOf(where, "away_sub_awayenddate");
			if(awaybbegin>=0&&awaybend>=0){
				if(awaybbegin==0){
					where = where.replaceFirst("away_sub_awaybegindate",
							"  pk_awayh in ( select pk_awayh from tbm_awayb where awaybegindate ");
				}else{
					where = where.replaceFirst("away_sub_awaybegindate",
							" and pk_awayh in ( select pk_awayh from tbm_awayb where awaybegindate ");
				}
				where = where.replaceFirst("away_sub_awayenddate",
						" and awayenddate " );
			}else if(awaybbegin>=0&&awaybend<0){
				if(awaybbegin==0){
					where = where.replaceFirst("away_sub_awaybegindate",
							"  pk_awayh in ( select pk_awayh from tbm_awayb where awaybegindate ");
				}else{
					where = where.replaceFirst("away_sub_awaybegindate",
							" and pk_awayh in ( select pk_awayh from tbm_awayb where awaybegindate ");
				}
			}else if(awaybbegin<0&&awaybend>=0){
				if(awaybend==0){
					where = where.replaceFirst("away_sub_awayenddate",
							"  pk_awayh in ( select pk_awayh from tbm_awayb where awayenddate ");
				}else{
					where = where.replaceFirst("away_sub_awayenddate",
							" and pk_awayh in ( select pk_awayh from tbm_awayb where awayenddate ");
				}
			}
			if((changebb>=0||changebe>=0||awaybbegin>=0||awaybend>=0||signdate>=0)&&where.contains("(")){
				where+=")";
			}
			
			//加班
			int otbbegin = StringUtils.indexOf(where, "overtime_sub_overtimebegindate");
			int otbend = StringUtils.indexOf(where, "overtime_sub_overtimeenddate");
			if(otbbegin>=0&&otbend>=0){
				if(otbbegin==0){
					
					where = where.replaceFirst("overtime_sub_overtimebegindate",
							"  pk_overtimeh in ( select pk_overtimeh from tbm_overtimeb where overtimebegindate ");
				}else{
					where = where.replaceFirst("overtime_sub_overtimebegindate",
							" and pk_overtimeh in ( select pk_overtimeh from tbm_overtimeb where overtimebegindate ");
				}
				where = where.replaceFirst("overtime_sub_overtimeenddate",
						" and overtimeenddate " );
			}else if(otbbegin>=0&&otbend<0){
				if(otbbegin==0){
					where = where.replaceFirst("overtime_sub_overtimebegindate",
							"  pk_overtimeh in ( select pk_overtimeh from tbm_overtimeb where overtimebegindate ");
				}else{
					where = where.replaceFirst("overtime_sub_overtimebegindate",
							" and pk_overtimeh in ( select pk_overtimeh from tbm_overtimeb where overtimebegindate ");
				}
			}else if(otbbegin<0&&otbend>=0){
				if(otbend==0){
					where = where.replaceFirst("overtime_sub_overtimeenddate",
							"  pk_overtimeh in ( select pk_overtimeh from tbm_overtimeb where overtimeenddate ");
				}else{
					where = where.replaceFirst("overtime_sub_overtimeenddate",
							" and pk_overtimeh in ( select pk_overtimeh from tbm_overtimeb where overtimeenddate ");
				}
			}
			if(otbbegin>0||otbend>0){
				int ottype = StringUtils.indexOf(where, "and  (tbm_overtimeh.pk_overtimetype");
				if(ottype>otbbegin&&ottype>otbend){
//					where = where.replaceFirst("and  (tbm_overtimeh.pk_over", ") and  (tbm_overtimeh.pk_over");
					where = org.apache.tools.ant.util.StringUtils.replace(where, "and  (tbm_overtimeh.pk_overtimetype", ")and  (tbm_overtimeh.pk_overtimetype");
				}else{
					if(where.contains("(")){
						where+=")";
					}
				}
			}
			
			
			//当pk_node不为空，并且值为LeaveApplyApp时，表示是“休假申请”
			if(pk_node != null && pk_node.equals("LeaveApplyApp")){
				int leavebbegin = StringUtils.indexOf(where, "leaveb_sub_leavebegindate");
				int leavebend = StringUtils.indexOf(where, "leaveb_sub_leaveenddate");
				if(leavebbegin>=0&&leavebend>=0){
//					if(leavebbegin==0){
//						where = where.replaceFirst("leaveb_sub_leavebegindate",
//								"  pk_leaveh in ( select pk_leaveh from tbm_leaveb where leavebegindate ");
//					}else{
//						where = where.replaceFirst("leaveb_sub_leavebegindate",
//								"  pk_leaveh in ( select pk_leaveh from tbm_leaveb where leavebegindate ");
////					" and pk_leaveh in ( select pk_leaveh from tbm_leaveb where leavebegindate ");
//					}
					where = where.replaceFirst("and leaveb_sub_leavebegindate",
							" and pk_leaveh in ( select pk_leaveh from tbm_leaveb where leavebegindate ");
					where = where.replaceFirst("leaveb_sub_leavebegindate",
							" and pk_leaveh in ( select pk_leaveh from tbm_leaveb where leavebegindate ");
					where = where.replaceFirst("leaveb_sub_leaveenddate",
							" and leaveenddate " );
				}else if(leavebbegin>=0&&leavebend<0){
//					if(leavebbegin==0){
////						where = where.replaceFirst("leaveb_sub_leavebegindate",
////								"  pk_leaveh in ( select pk_leaveh from tbm_leaveb where leavebegindate ");
//						where = where.replaceFirst("and leaveb_sub_leavebegindate",
//								" and pk_leaveh in ( select pk_leaveh from tbm_leaveb where leavebegindate ");
//						where = where.replaceFirst("leaveb_sub_leavebegindate",
//								" and pk_leaveh in ( select pk_leaveh from tbm_leaveb where leavebegindate ");
//					}else{
//						where = where.replaceFirst("leaveb_sub_leavebegindate",
//								"  pk_leaveh in ( select pk_leaveh from tbm_leaveb where leavebegindate ");
////					" and pk_leaveh in ( select pk_leaveh from tbm_leaveb where leavebegindate ");
//					}
					where = where.replaceFirst("and leaveb_sub_leavebegindate",
							" and pk_leaveh in ( select pk_leaveh from tbm_leaveb where leavebegindate ");
					where = where.replaceFirst("leaveb_sub_leavebegindate",
							" and pk_leaveh in ( select pk_leaveh from tbm_leaveb where leavebegindate ");
				}else if(leavebbegin<0&&leavebend>=0){
//					if(leavebend == 0){
//						
//						where = where.replaceFirst("leaveb_sub_leaveenddate",
//								"  pk_leaveh in ( select pk_leaveh from tbm_leaveb where leaveenddate ");
//					}else{
//						where = where.replaceFirst("leaveb_sub_leaveenddate",
//								"  pk_leaveh in ( select pk_leaveh from tbm_leaveb where leaveenddate ");
////					" and pk_leaveh in ( select pk_leaveh from tbm_leaveb where leaveenddate ");
//					}
					where = where.replaceFirst("and leaveb_sub_leaveenddate",
							" and pk_leaveh in ( select pk_leaveh from tbm_leaveb where leaveenddate ");
					where = where.replaceFirst("leaveb_sub_leaveenddate",
							" and pk_leaveh in ( select pk_leaveh from tbm_leaveb where leaveenddate ");
				}
				if(leavebbegin>=0||leavebend>=0){
					int leavetype = StringUtils.indexOf(where, "and  (tbm_leaveh.pk_leavetype");
					if(leavetype>otbbegin&&leavetype>otbend){
//						where = where.replaceFirst("and  (tbm_overtimeh.pk_over", ") and  (tbm_overtimeh.pk_over");
						where = org.apache.tools.ant.util.StringUtils.replace(where, "and  (tbm_leaveh.pk_leavetype", ")and  (tbm_leaveh.pk_leavetype");
					}else{
						if(where.contains("(")){
							where+=")";
						}
					}
				}
				
			}
			
			//当pk_node不为空，并且值为LactationApplyApp时，表示是“哺乳假申请”
			if(pk_node != null && pk_node.equals("LactationApplyApp")){
				int leavebbegin = StringUtils.indexOf(where, "leaveb_sub_leavebegindate");
				int leavebend = StringUtils.indexOf(where, "leaveb_sub_leaveenddate");
				int app = StringUtils.indexOf(where, "and apply_date");
				if(leavebbegin>=0&&leavebend>=0){
					where = where.replaceFirst("leaveb_sub_leavebegindate",
							"  pk_leaveh in ( select pk_leaveh from tbm_leaveb where leavebegindate ");
					where = where.replaceFirst("leaveb_sub_leaveenddate",
							" and leaveenddate " );
				}else if(leavebbegin>=0&&leavebend<0){
					where = where.replaceFirst("leaveb_sub_leavebegindate",
							"  pk_leaveh in ( select pk_leaveh from tbm_leaveb where leavebegindate ");
				}else if(leavebbegin<0&&leavebend>=0){
					where = where.replaceFirst("leaveb_sub_leaveenddate",
							"  pk_leaveh in ( select pk_leaveh from tbm_leaveb where leaveenddate ");
				}
				if(leavebbegin>=0||leavebend>=0){
					if(app>0){
						where = where.replaceFirst("and apply_date",
								" ) and apply_date" );
					}else{
						if(where.contains("(")){
							where+=")";
						}
					}
				}
				
			}
			
			
			
			
			fromWhereSQL.setWhere(where);
		}
		getApplicationContext().addAppAttribute(TaApplyConsts.APP_ATTR_FROMWHERESQL, fromWhereSQL);
		// // 重置页序号
		// ds.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		DatasetUtil.clearData(ds);
		getTaApplyDatas(fromWhereSQL, ds);
	}

	/**
	 * z 分类切换事件
	 * 
	 * @param keys
	 */
	public void pluginCatagory(Map<String, Object> keys) {
		if (keys == null || keys.size() == 0) {
			return;
		}
		TranslatedRow row = (TranslatedRow) keys.get("key2");
		if (row == null) {
			return;
		}
		String pk_node = (String) row.getValue("pk_node");
		if (StringUtils.isEmpty(pk_node)) {
			return;
		}
		ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();
		SessionUtil.setAttribute("pk_node", pk_node);
//		String url = LfwRuntimeEnvironment.getRootPath() + "/app/" + pk_node + "?nodecode=" + getNodecodeByApp(pk_node);
//		appCtx.sendRedirect(url,"E20200915");
		String url = LfwRuntimeEnvironment.getRootPath() + "/app/" + pk_node + "?nodecode=" + getNodecodeByApp(pk_node) + "&pk_node=" + pk_node;
		if (!CommonUtil.checkLoginUserFuncPermission("E20200915")) {
			appCtx.sendRedirect(url, getFirstTimeApplyNodeCode());
		} else {
			appCtx.sendRedirect(url, "E20200915");
		}
	}

	public static String getNodecodeByApp(String appId) {
		if (TaApplyCatDataProvider.APP_ID_CHGSHIFT.equals(appId)) {// 调班申请
			return "E20200915";
		} else if (TaApplyCatDataProvider.APP_ID_SIGNCARD.equals(appId)) {// 签卡申请
			return "E20200917";
		} else if (TaApplyCatDataProvider.APP_ID_AWAY.equals(appId)) {// 出差申请
			return "E20200919";
		} else if (TaApplyCatDataProvider.APP_ID_OVERTIME.equals(appId)) {// 加班申请
			return "E20200921";
		} else if (TaApplyCatDataProvider.APP_ID_LEAVE.equals(appId)) {// 休假申请
			return "E20200923";
		} else if (TaApplyCatDataProvider.APP_ID_LACTATION.equals(appId)) {// 哺乳假申请
			return "E20200925";
		} else if (TaApplyCatDataProvider.APP_ID_LEAVEOFF.equals(appId)) {// 销假申请
			return "E20200930";
		}else if (TaApplyCatDataProvider.APP_ID_AWAYOFF.equals(appId)) {// 销差申请
			return "E20200932";
		}
		return null;
	}

	/**
	 * 新增操作
	 * 
	 * @param mouseEvent
	 */
	public void addBill(MouseEvent<MenuItem> mouseEvent) {
		
		String nodeCode = (String)getApplicationContext().getAppSession().getAttribute("nodecode");
		
		if (TaAppContextUtil.getTBMPsndocVO() == null) {
			//start 如果是店长代申请签卡的节点,没找到考勤档案也允许进入 Ares 2018-7-23 15:34:54 
			if(nodeCode!=null&&nodeCode.equals(SIGN_CARD_APPLY_APP_SHOP)){
				;
			}else{//end 如果是店长代申请签卡的节点,没找到考勤档案也允许进入 Ares 2018-7-23 15:34:54 
				CommonUtil.showErrorDialog(ResHelper.getString("c_ta-res","0c_ta-res0180"),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0048")/*
																																 * @
																																 * res
																																 * "您还没有设置考勤档案，不能进行新增操作！"
																																 */);
			}
			
		}
		String operate_status = HrssConsts.POPVIEW_OPERATE_ADD;
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, null);
		showWindowDialog(operate_status);
	}

	private void showWindowDialog(String operate_status) {
		CommonUtil.showWindowDialog(getPopWindowId(), getPopWindowTitle(operate_status), "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
	}

	/**
	 * 修改操作
	 * 
	 * @param mouseEvent
	 */
	public void editBill(MouseEvent<MenuItem> mouseEvent) {
		String nodeCode = (String)getApplicationContext().getAppSession().getAttribute("nodecode");
		
		if (TaAppContextUtil.getTBMPsndocVO() == null) {
			//start 如果是店长代申请签卡的节点,没找到考勤档案也允许进入 Ares 2018-7-23 15:34:54 
			if(nodeCode!=null&&nodeCode.equals(SIGN_CARD_APPLY_APP_SHOP)){
				;
			}else{ 
				//end 如果是店长代申请签卡的节点,没找到考勤档案也允许进入 Ares 2018-7-23 15:34:54 
				CommonUtil.showErrorDialog(ResHelper.getString("c_ta-res","0c_ta-res0181"),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0049")/*
																																 * @
																																 * res
																																 * "您还没有设置考勤档案，不能进行修改操作！"
																																 */);
				}
			
		}
		Dataset ds = getCurrentView().getViewModels().getDataset(getDatasetId());
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_ta-res","0c_ta-res0181"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0050")/*
																															 * @
																															 * res
																															 * "请选择待修改的记录！"
																															 */);
		}
		AggregatedValueObject aggVO = new Datasets2AggVOSerializer().serialize(ds, null, getAggVOClazz().getName());
		IFlowBizItf itf = HrssBillCommand.getFlowBizImplByMdComp(ds, aggVO);
		if (itf != null && IPfRetCheckInfo.NOSTATE != itf.getApproveStatus()) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_ta-res","0c_ta-res0181"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0051")/*
																															 * @
																															 * res
																															 * "单据状态已不为自由态,不可修改！"
																															 */);
		}
		// 主键字段
		String primaryField = DatasetUtil.getPrimaryField(ds).getId();
		String primarykey = selRow.getString(ds.nameToIndex(primaryField));
		String operate_status = HrssConsts.POPVIEW_OPERATE_EDIT;
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, primarykey);
		
		checkAggVO(primarykey);
		
		showWindowDialog(operate_status);
	}
	
	private String getFirstTimeApplyNodeCode() {
		  String[] funcodes = new String[] { "E20200917", "E20200919", "E20200921", "E20200923", "E20200925","E20200930" };
		  String funcode = null;
		  for (String temp : funcodes) {
		   /** 权限判断 */
		   if (CommonUtil.checkLoginUserFuncPermission(temp)) {
		    funcode = temp;
		    break;
		   }
		  }
		  if (StringUtils.isEmpty(funcode)) {
		   return null;
		  }
		  return funcode;
		 }


	
	/**
	 * 判断单据是否已经被删除
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
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0166")/* @res "提示信息" */,
					ResHelper.getString("c_pub-res", "0c_pub-res0026")/* @res "单据已被删除！" */);
		}
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
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, primaryKey);
		checkAggVO(primaryKey);
		showWindowDialog(operate_status);
	}

	/**
	 * 复制操作
	 * 
	 * @param mouseEvent
	 */
	public void copyBill(MouseEvent<MenuItem> mouseEvent) {
		String nodeCode = (String)getApplicationContext().getAppSession().getAttribute("nodecode");
		
		if (TaAppContextUtil.getTBMPsndocVO() == null) {
			//start 如果是店长代申请签卡的节点,没找到考勤档案也允许进入 Ares 2018-7-23 15:34:54 
			if(nodeCode!=null&&nodeCode.equals(SIGN_CARD_APPLY_APP_SHOP)){
				;
			}else{//end 如果是店长代申请签卡的节点,没找到考勤档案也允许进入 Ares 2018-7-23 15:34:54 
				CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0175"),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0052")/*
																																 * @
																																 * res
																																 * "您还没有设置考勤档案，不能进行复制操作！"
																																 */);	
			}
			
		}
		Dataset ds = getCurrentView().getViewModels().getDataset(getDatasetId());
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0175"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0053")/*
																															 * @
																															 * res
																															 * "请选择待复制的记录！"
																															 */);
		}
		// 主键字段
		String primaryField = DatasetUtil.getPrimaryField(ds).getId();
		String primarykey = selRow.getString(ds.nameToIndex(primaryField));
		String operate_status = HrssConsts.POPVIEW_OPERATE_COPY;
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, primarykey);
		
		checkAggVO(primarykey);
		showWindowDialog(operate_status);
	}

	/**
	 * 删除操作
	 * 
	 * @param mouseEvent
	 */
	public void deleteBill(MouseEvent<MenuItem> mouseEvent) {
		CmdInvoker.invoke(new PFDeleteCmd(getDatasetId(), getAggVOClazz()));
	}

	/**
	 * 提交操作
	 * 
	 * @param mouseEvent
	 */
	public void sumbitBill(MouseEvent<MenuItem> mouseEvent) {
		String nodeCode = (String)getApplicationContext().getAppSession().getAttribute("nodecode");
		
		if (TaAppContextUtil.getTBMPsndocVO() == null) {
			//start 如果是店长代申请签卡的节点,没找到考勤档案也允许进入 Ares 2018-7-23 15:34:54 
			if(nodeCode!=null&&nodeCode.equals(SIGN_CARD_APPLY_APP_SHOP)){
				;
			}else{//end 如果是店长代申请签卡的节点,没找到考勤档案也允许进入 Ares 2018-7-23 15:34:54 
				CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0172"),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0054")/*
																																 * @
																																 * res
																																 * "您还没有设置考勤档案，不能进行提交操作！"
																																 */);
			}
			
		}
		CmdInvoker.invoke(new PFCommitCmd(getDatasetId(), getCommitPrcss(), getAggVOClazz()));
	}

	/**
	 * 收回操作
	 * 
	 * @param mouseEvent
	 */
	public void callBackBill(MouseEvent<MenuItem> mouseEvent) {
		String nodeCode = (String)getApplicationContext().getAppSession().getAttribute("nodecode");
		
		if (TaAppContextUtil.getTBMPsndocVO() == null) {
			//start 如果是店长代申请签卡的节点,没找到考勤档案也允许进入 Ares 2018-7-23 15:34:54 
			if(nodeCode!=null&&nodeCode.equals(SIGN_CARD_APPLY_APP_SHOP)){
				;
			}else{//end 如果是店长代申请签卡的节点,没找到考勤档案也允许进入 Ares 2018-7-23 15:34:54 
				CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0173"),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0055")/*
																																 * @
																																 * res
																																 * "您还没有设置考勤档案，不能进行收回操作！"
																																 */);
				}
			
		}
		PFReCallCmd reCallCmd = new PFReCallCmd(getDatasetId(), null, getAggVOClazz());
		CmdInvoker.invoke(reCallCmd);
	}

	/**
	 * 查看审批流操作
	 * 
	 * @param mouseEvent
	 */
	public void showApproveState(MouseEvent<MenuItem> mouseEvent) {
		PFApproveInfoCmd approveInfoCmd = new PFApproveInfoCmd(getDatasetId(), getAggVOClazz());
		CmdInvoker.invoke(approveInfoCmd);
	}

	/**
	 * 附件管理操作
	 * 
	 * @param mouseEvent
	 */
	public void addAttachment(MouseEvent<MenuItem> mouseEvent) {
		LfwView mainWidget = AppLifeCycleContext.current().getViewContext().getView();
		Dataset ds = mainWidget.getViewModels().getDataset(getDatasetId());
		CommonUtil.Attachment(ds, true);
	}

	/**
	 * 设置审批状态
	 * 
	 * @return
	 */
	protected TAPFBillQueryParams getEtraConds() {
		TAPFBillQueryParams params = new TAPFBillQueryParams();
		// 设置审批状态
		params.setStateCode(HRConstEnum.ALL_INTEGER);
		return params;
	}

	/**
	 * 行切换事件
	 * 
	 * @param datasetEvent
	 */
	public void onAfterRowSelect(DatasetEvent datasetEvent) {
		ButtonStateManager.updateButtons();
	}
	/**
	 * 店长代申请签卡时,查询店长申请过的单据
	 * @param fromWhereSQL
	 * @return
	 */
	protected AggregatedValueObject[] getAggVOsForShop(FromWhereSQL fromWhereSQL) {
		AggSignVO[] aggVOs = null;
		try {
			ISignCardApplyQueryMaintain queryServ = (ISignCardApplyQueryMaintain) ServiceLocator
					.lookup(ISignCardApplyQueryMaintain.class);
			aggVOs = (AggSignVO[]) queryServ.queryByPsndoc("", fromWhereSQL, getEtraConds());
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return aggVOs;
	}

	private void getTaApplyDatas(FromWhereSQLImpl fromWhereSQL, Dataset ds) {
		String nodeCode = (String)getApplicationContext().getAppSession().getAttribute("nodecode");
		AggregatedValueObject[] aggVOs = new AggregatedValueObject[0];
		if(nodeCode!=null&&nodeCode.equals(SIGN_CARD_APPLY_APP_SHOP)){
			aggVOs = getAggVOsForShop(fromWhereSQL);
		}else{
			aggVOs = getAggVOs(fromWhereSQL);
		}
		
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