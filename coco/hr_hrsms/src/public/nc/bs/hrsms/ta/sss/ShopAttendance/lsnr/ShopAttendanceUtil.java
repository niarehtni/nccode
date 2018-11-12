package nc.bs.hrsms.ta.sss.ShopAttendance.lsnr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel.ShopAttForEmpPageModel;
import nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel.ShopAttendanceForBatchPageModel;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.utils.TAUtil;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.hr.utils.InSQLCreator;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.uap.lfw.core.comp.GridColumn;
import nc.uap.lfw.core.comp.GridColumnGroup;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.uap.lfw.jsp.uimeta.UILayoutPanel;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.lfw.jsp.uimeta.UITabComp;
import nc.uap.lfw.jsp.uimeta.UITabItem;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.ta.dailydata.view.DisplayUtils;
import nc.ui.ta.pub.IColorConst;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.dailydata.IMidOutData;
import nc.vo.ta.dailydata.IWTCount;
import nc.vo.ta.dataprocess.TimeDataVO;
import nc.vo.ta.lateearly.LateEarlyUtils;
import nc.vo.ta.lateearly.LateEarlyVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.psndoc.TbmPropEnum;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class ShopAttendanceUtil {

	// 控件：考勤表格：分组：第一时段
	public static final String COMP_GRID_GROUP_ONE = "one";
	// 控件：考勤表格：分组：第二时段
	public static final String COMP_GRID_GROUP_TWO = "two";
	// 控件：考勤表格：分组：第三时段
	public static final String COMP_GRID_GROUP_THREE = "three";
	// 控件：考勤表格：分组：第四时段
	public static final String COMP_GRID_GROUP_FOUR = "four";

	// 容器：考勤数据页签
	public static final String TAB_TIME_DATA = "tabTimeData";
	// 页签：考勤数据：机器考勤
	public static final String TAB_ITEM_MACHINE = "tabMachine";
	// 页签：考勤数据：手工考勤
	public static final String TAB_ITEM_MANUAL = "tabManual";

	/**
	 * 填充页面考勤数据
	 * 
	 * @param ctx
	 *            页面上下文
	 * @param tvos
	 *            机器考勤数据
	 * @param lvos
	 *            手工考勤数据
	 * @author haoy 2011-6-13
	 */
	public static void fillData(AppLifeCycleContext ctx, TimeDataVO[] tvos, LateEarlyVO[] lvos, int tbm_prop) {

		LfwView viewMain = ctx.getViewContext().getView();
        
		
		// 设置页签的显示
		setTabLayoutDisplay(ctx, viewMain, tvos, lvos, tbm_prop);
		GridComp gridMachine = (GridComp) viewMain.getViewComponents().getComponent(ShopAttForEmpPageModel.COMP_GRID_MACHINE_DATA);
		GridComp gridManual = (GridComp) viewMain.getViewComponents().getComponent(ShopAttForEmpPageModel.COMP_GRID_MANUAL_DATA);
		
	
		
		// 调整显示列
		adjustWorkTimeColumn(tvos, gridMachine);
		adjustWorkTimeColumn(lvos, gridManual);

		// 为数据填充颜色
//		ctx.getApplicationContext().getClientSession().setAttribute(TimeDataForEmpPageModel.CSES_COLOR_MAP,TimeDataForEmpPageModel.getTimeDataColorInJSON(tvos, lvos));
//		ctx.getApplicationContext().addBeforeExecScript("window.colorMap = null");
		// 将可编辑的单元格信息传递到前台
//		ctx.getApplicationContext().getClientSession().setAttribute(TimeDataForEmpPageModel.CSES_EDIT_LIST, getTimeDataEditable(lvos));
//		ctx.getApplicationContext().addBeforeExecScript("window.editList = null");

	}

	/**
	 * 取得可编辑的单元格
	 * 
	 * @param laData
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getTimeDataEditable(LateEarlyVO[] laData) {
		StringBuilder buf = new StringBuilder("");
		if (null != laData && 0 < laData.length) {
			// 获取所有的班别定义
			HashMap<String, ShiftVO> map = new HashMap<String, ShiftVO>();
			List<String> pk_shift_List = new ArrayList<String>();
			for (LateEarlyVO vo : laData) {
				if (StringUtil.isEmpty(vo.getPk_shift()))
					continue;
				if (!pk_shift_List.contains(vo.getPk_shift())) {
					pk_shift_List.add(vo.getPk_shift());
				}
			}
			String condition = "";
			Collection<ShiftVO> shiftScope = null;
			
//			LfwView viewMain = AppLifeCycleContext.current().getWindowContext().getCurrentViewContext().getView();
//					
//			GridComp gridManual = (GridComp) viewMain.getViewComponents().getComponent(ShopAttForEmpPageModel.COMP_GRID_MANUAL_DATA);
//			if(pk_shift_List.size()==0){
//				// 考勤数据的时间段分组
////				String[] groups = new String[] { COMP_GRID_GROUP_ONE, COMP_GRID_GROUP_TWO};
//				((GridColumnGroup) gridManual.getColumnById("one")).setVisible(false);
//				((GridColumnGroup) gridManual.getColumnById("two")).setVisible(false);
//			}
//			else{
//				
//				((GridColumnGroup) gridManual.getColumnById("one")).setVisible(true);
//				((GridColumnGroup) gridManual.getColumnById("two")).setVisible(true);
//				
//			}
			
			
			try {
				condition = LateEarlyVO.PK_SHIFT + " in ("
						+ (new InSQLCreator().getInSQL(pk_shift_List.toArray(new String[0]))) + ")";
				shiftScope = new BaseDAO().retrieveByClause(ShiftVO.class, condition);
			} catch (DAOException e) {
				new HrssException(e).deal();
			} catch (BusinessException e1) {
				new HrssException(e1).deal();
			}
			for(ShiftVO shiftVO : shiftScope){
				map.put(shiftVO.getPk_shift(), shiftVO);
			}
			// 将所有可编辑的单元格集中
			String[] attrs = laData[0].getAttributeNames();
			for (LateEarlyVO vo : laData) {
				ShiftVO bclb = map.get(vo.getPk_shift());
				for (String attr : attrs) {
					boolean editable = LateEarlyUtils.isEditable(vo, bclb, attr);
					if (editable){
						if (buf.length() > 0)
							buf.append(",");
						buf.append("\"").append(vo.getPk_psndoc()).append(vo.getCalendar().toStdString()).append(attr).append("\":\"").append("right\"");
					}
				}
			}
		}
		return "{" + buf.toString() + "}";
	}

	/**
	 * 设置页签的显示.<br/>
	 * 
	 * @param ctx
	 *            页面上下文
	 * @param viewMain
	 * @param tvos
	 *            机器考勤数据
	 * @param lvos
	 *            手工考勤数据
	 */
	private static void setTabLayoutDisplay(AppLifeCycleContext ctx, LfwView viewMain, TimeDataVO[] tvos,
			LateEarlyVO[] lvos, int tbm_prop) {

		UIMeta um = (UIMeta) ctx.getViewContext().getUIMeta();
		UITabComp tabComp = (UITabComp) um.findChildById(TAB_TIME_DATA);
		List<UILayoutPanel> itemList = tabComp.getPanelList();

		int item_index_machine = 0;
		int item_index_manual = 0;
		for (int i = 0; i < itemList.size(); i++) {
			UITabItem item = (UITabItem) itemList.get(i);
			if (TAB_ITEM_MACHINE.endsWith(item.getId())) {// 机器考勤
				if (null != tvos && tvos.length > 0) {
					Dataset dsMachine = viewMain.getViewModels().getDataset(ShopAttForEmpPageModel.DATASET_MACHINE);
					TimeDataVO[] curPageTvos = DatasetUtil.paginationMethod(dsMachine, tvos);
					new SuperVO2DatasetSerializer().serialize(curPageTvos, dsMachine, Row.STATE_NORMAL);
					dsMachine.setRowSelectIndex(0);
					item.setVisible(true);
					ctx.getApplicationContext().getClientSession().setAttribute(ShopAttForEmpPageModel.CSES_COLOR_MAP,ShopAttForEmpPageModel.getTimeDataColorInJSON(curPageTvos, null));
					ctx.getApplicationContext().addBeforeExecScript("setColorMap();");
				} else {
					if (null != lvos && lvos.length > 0) {// 无机器考勤且有手工考勤，则隐藏机器考勤页签
						item.setVisible(false);
					} else {
						if(tbm_prop == TbmPropEnum.MANUAL_CHECK.toIntValue()){
							item.setVisible(false); //无机器考勤、无手工考勤且考勤档案考勤方式为手工考勤，则隐藏机器考勤页签
						}else{
							item.setVisible(true);// 无机器考勤、无手工考勤且考勤档案考勤方式不为手工考勤，则显示机器考勤页签
						}
						
					}
				}
			} else {// 手工考勤
				if (null != lvos && lvos.length > 0) {
					item_index_manual = i;
					Dataset dsManual = viewMain.getViewModels().getDataset(ShopAttForEmpPageModel.DATASET_MANUAL);
					SessionBean bean = SessionUtil.getSessionBean();
					UFLiteralDate begindate=(UFLiteralDate)bean.getExtendAttributeValue(ShopAttendanceForBatchPageModel.FLD_BEGIN);
					UFLiteralDate enddate=(UFLiteralDate)bean.getExtendAttributeValue(ShopAttendanceForBatchPageModel.FLD_END);
					String newClass=(String)bean.getExtendAttributeValue(ShopAttendanceForBatchPageModel.FLD_NEW_CLASS);
					String editdate=(String)bean.getExtendAttributeValue(ShopAttendanceForBatchPageModel.FLD_EDITDATE);
					String datestatus=(String)bean.getExtendAttributeValue(ShopAttendanceForBatchPageModel.FLD_DATESTATUS);
					boolean isBatchEdit=(Boolean) bean.getExtendAttributeValue(ShopAttendanceForBatchPageModel.ISBATCHEDIT);
				
					
					for(int j=0;j<lvos.length;j++){
						if(isBatchEdit){
							if(begindate!=null||enddate!=null){
								if(lvos[j].getDate().before(enddate.getDateAfter(1))&&lvos[j].getDate().after(begindate.getDateBefore(1))){
									if(lvos[j].getPk_shift().equals(newClass)){
										if(editdate.equals("所有时段")){
											if(datestatus.equals("正常")){
												lvos[j].setOnebeginstatus(0);
												lvos[j].setTwoendstatus(0);
											}else if(datestatus.equals("迟到或早退")){
												lvos[j].setOnebeginstatus(1);
												lvos[j].setTwoendstatus(1);
												
											}else if(datestatus.equals("未出勤")){
												lvos[j].setOnebeginstatus(2);
												lvos[j].setTwoendstatus(2);
											}
										}
									}
								}
							}
						}
							
						
						
						//不符合条件的默认设置为未出勤
						if(lvos[j].getOnebeginstatus()==null||lvos[j].getOnebeginstatus()==-1){
							if(ShiftVO.PK_GX.equals(lvos[j].getPk_shift())){
								lvos[j].setOnebeginstatus(0);
							}else{
								lvos[j].setOnebeginstatus(2);
							}
						} 
						if(lvos[j].getTwoendstatus()==null||lvos[j].getTwoendstatus()==-1){
							if(ShiftVO.PK_GX.equals(lvos[j].getPk_shift())){
								lvos[j].setTwoendstatus(0);
							}else{
								lvos[j].setTwoendstatus(2);
							}
						}
					}
					
					
					LateEarlyVO[] curPageLvos = DatasetUtil.paginationMethod(dsManual, lvos);
					new SuperVO2DatasetSerializer().serialize(curPageLvos, dsManual, Row.STATE_NORMAL);
					dsManual.setRowSelectIndex(0);
					dsManual.setEnabled(true);
					item.setVisible(true);
					ctx.getApplicationContext().getClientSession().setAttribute(ShopAttForEmpPageModel.CSES_COLOR_MAP,ShopAttForEmpPageModel.getTimeDataColorInJSON(null, curPageLvos));
					ctx.getApplicationContext().getClientSession().setAttribute(ShopAttForEmpPageModel.CSES_EDIT_LIST, getTimeDataEditable(curPageLvos));
					ctx.getApplicationContext().addBeforeExecScript("setColorMap();setEditList()");
				} else {
					if (null != tvos && tvos.length > 0) { //无手工考勤且有机器考勤，则隐藏手工考勤
						item.setVisible(false);
					}else{
						if(tbm_prop == TbmPropEnum.MANUAL_CHECK.toIntValue()){
							item.setVisible(true);
						}else{
							item.setVisible(false);
						}
					}
					
				}
			}
		}
		// 定位页签
		String currentItem = (String) ctx.getApplicationContext().getAppAttribute("HrssCurrentItem");
		if(!StringUtils.isEmpty(currentItem) && "1".equals(currentItem)){
			ctx.getApplicationContext().removeAppAttribute("HrssCurrentItem");
		}else{
			if (ArrayUtils.isEmpty(tvos) && !ArrayUtils.isEmpty(lvos)) {// 无机器考勤且有手工考勤的情况显示手工考勤
				tabComp.setCurrentItem(String.valueOf(item_index_manual));
			} else {
				tabComp.setCurrentItem(String.valueOf(item_index_machine));
			}
		}

	}

	/**
	 * 设置机器/手工考勤数据的时间段\中途外出\考勤地点异常的显示或不显示.
	 * 
	 * @param vos
	 *            机器/手工考勤数据
	 * @param grid
	 *            机器/手工考勤表格
	 */
	public static void adjustWorkTimeColumn(IWTCount[] vos, GridComp grid) {

		boolean isManual = ShopAttForEmpPageModel.COMP_GRID_MANUAL_DATA.equals(grid.getId());
		if (vos == null || vos.length == 0) {
			return;
		}
		// 考勤数据的时间段分组
//		String[] groups = new String[] { COMP_GRID_GROUP_ONE, COMP_GRID_GROUP_TWO};
//				, COMP_GRID_GROUP_THREE,
//				COMP_GRID_GROUP_FOUR 
//		// 默认时间段分组都不显示
		
//		for (int i = 0; i <groups.length ; i++) {
//			((GridColumnGroup) grid.getColumnById(groups[i])).setVisible(true);
//		}
		
		// 再设置要显示的时间段分组
		
//		boolean ma=((GridColumnGroup) grid.getColumnById(groups[0])).isVisible();
//		boolean ma1=((GridColumnGroup) grid.getColumnById(groups[1])).isVisible();
//		boolean[][] dispSeg = DisplayUtils.getMaxWtDispInfo(vos);
//	
//		for (int i = 0; i < groups.length; i++) {
//			if (i < dispSeg.length){
//				if(!(dispSeg[i][0] || dispSeg[i][1])){
//					((GridColumnGroup) grid.getColumnById(groups[i])).setVisible(false);
//				}
//				else
//					((GridColumnGroup) grid.getColumnById(groups[i])).setVisible(true);
//				
//			}else {
//				((GridColumnGroup) grid.getColumnById(groups[i])).setVisible(false);
//			}
//		}
		

		
		
//		for (int i = 0; null != dispSeg && i < dispSeg.length/2; i++) {
//			if (!(dispSeg[i][0] || dispSeg[i][1]))
//				continue;
//			GridColumnGroup col = (GridColumnGroup) grid.getColumnById(groups[i]);
//			col.setVisible(dispSeg[i][0]||dispSeg[i][1]);
//			
//		}
		if (isManual) { // 手工考勤数据不做下述处理
			return;
		}
		/* 中途外出 */
		boolean dispOut = (null != vos && 0 < vos.length && vos[0] instanceof IMidOutData && DisplayUtils
				.isDispMidOut((IMidOutData[]) vos));
		GridColumnGroup colMidWayOut = (GridColumnGroup) grid.getColumnById("out");
		colMidWayOut.setVisible(dispOut);

		/* 刷卡地异常 */
		String pk_org = SessionUtil.getHROrg();
		// 获取考勤规则
		TimeRuleVO ruleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_org);
		if (null != ruleVO.getWorkplaceflag() && ruleVO.getWorkplaceflag().booleanValue()) {
			GridColumn colPlace = (GridColumn) grid.getColumnById("placeabnormal");
			if (null != colPlace) {
				colPlace.setVisible(true);
			}
		}
	}

	/**
	 * 获取出勤情况的颜色说明html
	 * 
	 * @return
	 * @author haoy 2011-8-23
	 */
	public static String getColorBrief(boolean isMachine) {
		String[][] cMap = null;
		String[] borders = null;
		if (isMachine) {// 机器考勤
			String pk_org = SessionUtil.getHROrg();
			/* 刷卡地异常 */
			TimeRuleVO ruleVO = null;
			if(!StringUtil.isEmptyWithTrim(pk_org)){
				ruleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_org);
			}
			if (ruleVO != null && null != ruleVO.getWorkplaceflag() && ruleVO.getWorkplaceflag().booleanValue()) {
				cMap = new String[][] {						
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0122")/*@res "未出勤"*/, TAUtil.getHexDesc(IColorConst.COLOR_ABSENT) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0121")/*@res "迟到早退"*/, TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0126")/*@res "考勤地点异常"*/, TAUtil.getHexDesc(IColorConst.COLOR_PLACEEXCEPTION) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0125")/*@res "中途外出"*/, TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0124")/*@res "未生成考勤数据"*/, TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0123")/*@res "未设置工作日历"*/, TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR) }};
				borders = new String[] { 
						TAUtil.getHexDesc(IColorConst.COLOR_ABSENT_BORDER)/* 未出勤*/,
						TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY_BORDER)/* 迟到早退*/,
						TAUtil.getHexDesc(IColorConst.COLOR_PLACEEXCEPTION_BORDER) /* 考勤地点异常*/,
						TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT_BORDER)/* 中途外出 */,
						TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA_BORDER)/* 未生成考勤数据*/,
						TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR_BORDER)/* 未设置工作日历 */};
			} else {
				cMap = new String[][] {						
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0122")/*@res "未出勤"*/, TAUtil.getHexDesc(IColorConst.COLOR_ABSENT) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0121")/*@res "迟到早退"*/, TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0125")/*@res "中途外出"*/, TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0124")/*@res "未生成考勤数据"*/, TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA) },
						{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0123")/*@res "未设置工作日历"*/, TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR) }};
				borders = new String[] { 
						TAUtil.getHexDesc(IColorConst.COLOR_ABSENT_BORDER)/* 未出勤*/,
						TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY_BORDER)/* 迟到早退*/,
						TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT_BORDER)/* 中途外出 */,
						TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA_BORDER)/* 未生成考勤数据*/,
						TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR_BORDER)/* 未设置工作日历 */};
			}
		} else {// 手工考勤
			cMap = new String[][] {
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0122")/*@res "未出勤"*/, TAUtil.getHexDesc(IColorConst.COLOR_ABSENT) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0121")/*@res "迟到早退"*/, TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0123")/*@res "未设置工作日历"*/, TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR) }};
			borders = new String[] { 
					TAUtil.getHexDesc(IColorConst.COLOR_ABSENT_BORDER)/* 未出勤*/,
					TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY_BORDER)/* 迟到早退*/,
					TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR_BORDER)/* 未设置工作日历 */};
		}
		StringBuffer buf = new StringBuffer();
		buf.append("<table height=\"40px\" cellpadding =\"0\" cellspacing=\"0\" align=\"center\">");
		buf.append("<tr>");
		for (int i = 0; i < cMap.length; i++) {
			buf.append("<td><span style=\"width:20px;height:20px;margin-right:5px;border:1px solid ").append(borders[i]).append(";background-color:");
			buf.append(cMap[i][1]);
			buf.append("\">&nbsp;&nbsp;</span>");
			buf.append("<span style=\"margin-right:20px;\">");
			buf.append(cMap[i][0]);
			buf.append("</span></td>");
		}
		buf.append("</tr>");
		buf.append("</table>");
		return buf.toString();
	}
	
	/**
	 * 获取出勤情况的颜色说明html
	 * 
	 * @return
	 * @author haoy 2011-8-23
	 */
	public static String getMngColorBrief() {
		String[][] cMap = null;
		String[] borders = null;
		String pk_org = SessionUtil.getHROrg();
		/* 刷卡地异常 */
		TimeRuleVO ruleVO = null;
		if(!StringUtil.isEmptyWithTrim(pk_org)){
			ruleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_org);
		}
		if (ruleVO != null && null != ruleVO.getWorkplaceflag() && ruleVO.getWorkplaceflag().booleanValue()) {
			cMap = new String[][] {
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0122")/*@res "未出勤"*/, TAUtil.getHexDesc(IColorConst.COLOR_ABSENT) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0121")/*@res "迟到早退"*/, TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0126")/*@res "考勤地点异常"*/, TAUtil.getHexDesc(IColorConst.COLOR_PLACEEXCEPTION) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0125")/*@res "中途外出"*/, TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0037")/*@res "无考勤档案"*/, TAUtil.getHexDesc(IColorConst.COLOR_NONTBMPSNDOC) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0124")/*@res "未生成考勤数据"*/, TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0123")/*@res "未设置工作日历"*/, TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR) }};
			borders = new String[] { 
					TAUtil.getHexDesc(IColorConst.COLOR_ABSENT_BORDER)/* 未出勤*/,
					TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY_BORDER)/* 迟到早退*/,
					TAUtil.getHexDesc(IColorConst.COLOR_PLACEEXCEPTION_BORDER) /* 考勤地点异常*/,
					TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT_BORDER)/* 中途外出 */,
					TAUtil.getHexDesc(IColorConst.COLOR_NONTBMPSNDOC_BORDER)/* 无考勤档案 */,
					TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA_BORDER)/* 未生成考勤数据*/,
					TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR_BORDER)/* 未设置工作日历 */};

		} else {
			cMap = new String[][] {
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0122")/*@res "未出勤"*/, TAUtil.getHexDesc(IColorConst.COLOR_ABSENT) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0121")/*@res "迟到早退"*/, TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0125")/*@res "中途外出"*/, TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0037")/*@res "无考勤档案"*/, TAUtil.getHexDesc(IColorConst.COLOR_NONTBMPSNDOC) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0124")/*@res "未生成考勤数据"*/, TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA) },
					{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0123")/*@res "未设置工作日历"*/, TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR) }};
			borders = new String[] { 
					TAUtil.getHexDesc(IColorConst.COLOR_ABSENT_BORDER)/* 未出勤*/,
					TAUtil.getHexDesc(IColorConst.COLOR_LATEEARLY_BORDER)/* 迟到早退*/,
					TAUtil.getHexDesc(IColorConst.COLOR_MIDOUT_BORDER)/* 中途外出 */,
					TAUtil.getHexDesc(IColorConst.COLOR_NONTBMPSNDOC_BORDER)/* 无考勤档案 */,
					TAUtil.getHexDesc(IColorConst.COLOR_NOTIMEDATA_BORDER)/* 未生成考勤数据*/,
					TAUtil.getHexDesc(IColorConst.COLOR_NONPSNCALENDAR_BORDER)/* 未设置工作日历 */};
		}
		StringBuffer buf = new StringBuffer();
		buf.append("<table height=\"40px\" cellpadding =\"0\" cellspacing=\"0\" align=\"center\">");
		buf.append("<tr>");
		for (int i = 0; i < cMap.length; i++) {
			buf.append("<td><span style=\"width:20px;height:20px;margin-right:5px;border:1px solid ").append(borders[i]).append(";background-color:");
			buf.append(cMap[i][1]);
			buf.append("\">&nbsp;&nbsp;</span>");
			buf.append("<span style=\"margin-right:20px;\">");
			buf.append(cMap[i][0]);
			buf.append("</span></td>");
		}
		buf.append("</tr>");
		buf.append("</table>");
		return buf.toString();
	}

	/**
	 * 查询部门的人员
	 * 
	 * @param fws
	 * @param begin
	 * @param end
	 * @param pk_dept
	 * @param includeSubDept
	 * @return
	 */
	public static TBMPsndocVO[] getTbmPsndoc(FromWhereSQL fws, UFLiteralDate begin, UFLiteralDate end, String pk_dept,
			boolean includeSubDept) {
		TBMPsndocVO[] psn = null;
		ITBMPsndocQueryService pqs;
		try {
			pqs = ServiceLocator.lookup(ITBMPsndocQueryService.class);
			psn = pqs.queryLatestByConditionAndDept(fws, begin, end, pk_dept, includeSubDept);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return (null == psn) ? new TBMPsndocVO[0] : psn;
	}

	/**
	 * 根据年月查询期间
	 * 
	 * @param pk_org
	 * @param year
	 * @param month
	 */
	public static PeriodVO queryPeriodVOByYearMonth(String pk_org, String year, String month) {
		PeriodVO periodVO = null;
		try {
			IPeriodQueryService service = ServiceLocator.lookup(IPeriodQueryService.class);
			periodVO = service.queryByYearMonth(pk_org, year, month);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			e.alert();
		}

		return periodVO;
	}
}