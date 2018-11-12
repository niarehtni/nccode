package nc.bs.hrsms.ta.shift.ctrl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.shift.StoreShiftConsts;
import nc.bs.hrsms.ta.shift.lsnr.StoreShiftSaveProcessor;
import nc.bs.hrss.pub.BillCoderUtils;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.cmd.SaveCmd;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.logging.Logger;
import nc.itf.hrsms.ta.shift.IStoreShiftQueryMaintain;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.DatasetEvent;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.RTVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.StringUtils;
/**
 * 门店班次定义卡片页面Controller
 * @author shaochj
 * @date Apr 23, 2015
 */
public class StoreShiftCardViewCtrl implements IController{
	
	/**
	 * 获得当前Application
	 */
	private ApplicationContext getCurrentApplication() {
		return AppLifeCycleContext.current().getApplicationContext();
	}
	/**
	 * 保存的PROCESSOR
	 * 
	 * @return
	 */
	public Class<? extends ISaveProcessor> getStoreShiftSavePrcss() {
		return StoreShiftSaveProcessor.class;
	}
	
	public Class<? extends AggregatedValueObject> getAggVoClazz() {
		return  AggShiftVO.class;
	}
	/**
	 * beforeShow事件
	 * @param dialogEvent
	 */
	public void onBeforeShow(DialogEvent dialogEvent) {
		Dataset ds = ViewUtil.getDataset(ViewUtil.getCurrentView(),StoreShiftConsts.DS_MAIN_NAME);
		Dataset subDs = ViewUtil.getDataset(ViewUtil.getCurrentView(),StoreShiftConsts.DS_SUB_NAME);
		//String op = (String) getCurrentApplication().getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
	    String pk_shift = (String) getCurrentApplication().getAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM);
	    getCurrentApplication().removeAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM);
	    if(StringUtils.isEmpty(pk_shift)){
			Row row = ds.getEmptyRow();
		    //赋默认值
			String pk_group = SessionUtil.getPk_group();
			String pk_hrorg = SessionUtil.getHROrg();
//			String pk_hrorg = SessionUtil.getPsndocVO().getPk_hrorg();
			String code = "HRTA_shift";
			String billcode = getBillCode(pk_group,pk_hrorg,code);
			row.setString(ds.nameToIndex("code"), billcode);
//			if(billcode!=null){
//				if (BillCoderUtils.isAutoGenerateBillCode(pk_group, pk_hrorg, code)) {
//					
//					 
//				}
//			}
			
		    row.setString(ds.nameToIndex("enablestate"), "2");
		    row.setString(ds.nameToIndex("nightbeginday"), StoreShiftConsts.CUR_DAY);
		    row.setString(ds.nameToIndex("nightendday"), StoreShiftConsts.CUR_DAY);
		    row.setString(ds.nameToIndex("timebeginday"), StoreShiftConsts.CUR_DAY);
		    row.setString(ds.nameToIndex("timeendday"), StoreShiftConsts.CUR_DAY);
		    row.setString(ds.nameToIndex("beginday"), StoreShiftConsts.CUR_DAY);
		    row.setString(ds.nameToIndex("endday"), StoreShiftConsts.CUR_DAY);
		    
		    row.setString(ds.nameToIndex("latestbeginday"), StoreShiftConsts.CUR_DAY);
		    row.setString(ds.nameToIndex("earliestendday"), StoreShiftConsts.CUR_DAY);
		    row.setString(ds.nameToIndex("isautokg"), "0");
		    
		    row.setValue(ds.nameToIndex("isallowout"), new UFBoolean(true));
		    row.setString(ds.nameToIndex("allowlate"), "0");
		    row.setString(ds.nameToIndex("largelate"), "0");
		    row.setString(ds.nameToIndex("allowearly"), "0");
		    row.setString(ds.nameToIndex("largeearly"), "0");
		    row.setString(ds.nameToIndex("pk_org"), SessionUtil.getPk_org());
		    row.setString(ds.nameToIndex("pk_group"), SessionUtil.getPk_group());
		    row.setString(ds.nameToIndex("creator"), SessionUtil.getPk_user());
		    // 创建时间
			row.setValue(ds.nameToIndex("creationtime"), new UFDateTime());
		    row.setString(ds.nameToIndex("pk_dept"), SessionUtil.getPk_mng_dept());
		    
		    ds.setCurrentKey(Dataset.MASTER_KEY);
			ds.addRow(row);
			ds.setRowSelectIndex(0);
			ds.setEnabled(true);

			//子表赋值
			for(int i = 0;i<3;i++){
				Row emptyRow = subDs.getEmptyRow();
				emptyRow.setValue(subDs.nameToIndex("timeid"), i+1);
				emptyRow.setString(subDs.nameToIndex("beginday"), StoreShiftConsts.CUR_DAY);
				emptyRow.setString(subDs.nameToIndex("endday"), StoreShiftConsts.CUR_DAY);
				emptyRow.setString(ds.nameToIndex("pk_org"), SessionUtil.getPk_org());
				emptyRow.setString(ds.nameToIndex("pk_group"), SessionUtil.getPk_group());
				subDs.addRow(emptyRow);
				subDs.setRowSelectIndex(0);
				subDs.setEnabled(true);
			}
		}else{
			AggShiftVO aggVO = getAggVOByPk(pk_shift);
			ShiftVO vo = (ShiftVO) aggVO.getParentVO();
			//RadioGroup
			
			new SuperVO2DatasetSerializer().serialize(new SuperVO[]{vo}, ds, Row.STATE_NORMAL);
			ds.setCurrentKey(Dataset.MASTER_KEY);
			ds.setRowSelectIndex(0);
			
			
			Row row =  ds.getSelectedRow();
			if(!vo.getIsautokg().booleanValue()){
				row.setString(ds.nameToIndex("isautokg"), "0");
			}else{
				row.setString(ds.nameToIndex("isautokg"), "1");
			}
			
			RTVO[] subVO = (RTVO[]) aggVO.getTableVO("rt_sub");
			
			RTVO[] sus = new RTVO[3];
			for(int i=0;i<sus.length;i++){
				sus[i] = new RTVO();
				sus[i].setTimeid(i+1);
				if(sus[i].getPk_rt()==null){
					sus[i].setBeginday(new Integer(StoreShiftConsts.CUR_DAY));
					sus[i].setEndday(new Integer(StoreShiftConsts.CUR_DAY));
					sus[i].setPk_group(SessionUtil.getPk_group());
					sus[i].setPk_org(SessionUtil.getPk_group());
					sus[i].setCheckflag(UFBoolean.FALSE);
					sus[i].setIsflexible(UFBoolean.FALSE);
				}
			}
			if(subVO!=null){
				for(RTVO rvo:subVO){
					if(rvo.getTimeid()==1){
						sus[0] = rvo;
					}
					if(rvo.getTimeid()==2){
						sus[1] = rvo;
					}
					if(rvo.getTimeid()==3){
						sus[2] = rvo;
					}	
				}
			}
			new SuperVO2DatasetSerializer().serialize(sus, subDs, Row.STATE_NORMAL);
			subDs.setEnabled(true);	
			subDs.setRowSelectIndex(0);
			ds.setEnabled(true);
//			if(subVO == null){
//				//子表赋值
//				for(int i = 0;i<3;i++){
//					Row emptyRow = subDs.getEmptyRow();
//					emptyRow.setValue(subDs.nameToIndex("timeid"), i+1);
//					emptyRow.setString(subDs.nameToIndex("beginday"), StoreShiftConsts.CUR_DAY);
//					emptyRow.setString(subDs.nameToIndex("endday"), StoreShiftConsts.CUR_DAY);
//					emptyRow.setString(ds.nameToIndex("pk_org"), SessionUtil.getPk_org());
//					emptyRow.setString(ds.nameToIndex("pk_group"), SessionUtil.getPk_group());
//					subDs.addRow(emptyRow);
//					subDs.setRowSelectIndex(0);
//					subDs.setEnabled(true);
//				}
//				ds.setEnabled(true);
//			}else{
//				RTVO[] sus = new RTVO[3];
//				for(int i=0;i<sus.length;i++){
//					sus[i] = new RTVO();
//					sus[i].setTimeid(i+1);
//					if(sus[i].getPk_rt()==null){
//						sus[i].setBeginday(new Integer(StoreShiftConsts.CUR_DAY));
//						sus[i].setEndday(new Integer(StoreShiftConsts.CUR_DAY));
//						sus[i].setPk_group(SessionUtil.getPk_group());
//						sus[i].setPk_org(SessionUtil.getPk_group());
//					}
//				}
//				for(RTVO rvo:subVO){
//					if(rvo.getTimeid()==1){
//						sus[0] = rvo;
//					}
//					if(rvo.getTimeid()==2){
//						sus[1] = rvo;
//					}
//					if(rvo.getTimeid()==3){
//						sus[2] = rvo;
//					}	
//				}
//				
//				new SuperVO2DatasetSerializer().serialize(sus, subDs, Row.STATE_NORMAL);
//				subDs.setEnabled(true);	
//				subDs.setRowSelectIndex(0);
//				ds.setEnabled(true);
//			}
			if("2".equals(vo.getEnablestate().toString())){
				ds.setEnabled(false);
				LfwView view = ViewUtil.getCurrentView();
				MenuItem saveItem = view.getViewMenus().getMenuBar("menubar").getItem("save");
				saveItem.setVisible(false);
				subDs.setEnabled(false);
			}
			
		}
	    
		
	}
	public void onDataLoad(DataLoadEvent dataLoadEvent){
		
	}
	
	public void onAfterRowSelect(DatasetEvent datasetEvent){
		
	}
	/**
	 * 保存
	 * @param mouseEvent
	 */
	public void onSave(MouseEvent<MenuItem> mouseEvent){
		CmdInvoker.invoke(new SaveCmd(StoreShiftConsts.DS_MAIN_NAME, getStoreShiftSavePrcss(), getAggVoClazz()));
	}
	/**
	 * onAfterDataChange事件
	 * 处理值变化
	 * @param datasetCellEvent
	 */
	public void onAfterDataChange(DatasetCellEvent datasetCellEvent) {
		int col = datasetCellEvent.getColIndex();
		Dataset ds = ViewUtil.getDataset(ViewUtil.getCurrentView(),StoreShiftConsts.DS_MAIN_NAME);
		FormComp form = (FormComp) ViewUtil.getCurrentView().getViewComponents().getComponent("zxsj_form");
		Row row = ds.getSelectedRow();
		/**格式化时间*/
		//刷卡开始时间
		String timebegintime = row.getString(ds.nameToIndex("timebegintime"));
		
		row.setValue(ds.nameToIndex("timebegintime"), timeFormate(timebegintime));
		//刷卡结束时间
		String timeendtime = row.getString(ds.nameToIndex("timeendtime"));
		row.setValue(ds.nameToIndex("timeendtime"), timeFormate(timeendtime));
		
		//上班时间
		String begintime = row.getString(ds.nameToIndex("begintime"));
		row.setValue(ds.nameToIndex("begintime"), timeFormate(begintime));
		//最晚上班时间
		String latestbegintime = row.getString(ds.nameToIndex("latestbegintime"));
		row.setValue(ds.nameToIndex("latestbegintime"), timeFormate(latestbegintime));
		//最早下班时间
		String earliestendtime = row.getString(ds.nameToIndex("earliestendtime"));
		row.setValue(ds.nameToIndex("earliestendtime"), timeFormate(earliestendtime));
		//下班时间
		String endtime = row.getString(ds.nameToIndex("endtime"));
		row.setValue(ds.nameToIndex("endtime"), timeFormate(endtime));
		//夜班开始时间
		String nightbegintime = row.getString(ds.nameToIndex("nightbegintime"));
		row.setValue(ds.nameToIndex("nightbegintime"), timeFormate(nightbegintime));
		//夜班结束时间
		String nightendtime = row.getString(ds.nameToIndex("nightendtime"));
		row.setValue(ds.nameToIndex("nightendtime"), timeFormate(nightendtime));
		
		//gzsj getDurationHour
		String btime = row.getString(ds.nameToIndex("begintime"));
		String etime = row.getString(ds.nameToIndex("endtime"));
		
		
		//支持单次刷卡
		if(col == ds.nameToIndex("issinglecard") ){
			UFBoolean issinglecard = (UFBoolean) row.getValue(ds.nameToIndex("issinglecard"));
			if(issinglecard.booleanValue()){
				//刷卡类型
				form.getElementById("cardtype").setEnabled(true);
				form.getElementById("isotflexible").setEnabled(false);
				form.getElementById("isrttimeflexible").setEnabled(false);
			}else{
				row.setString(ds.nameToIndex("cardtype"), "");
				form.getElementById("cardtype").setEnabled(false);
				form.getElementById("isotflexible").setEnabled(true);
				form.getElementById("isrttimeflexible").setEnabled(true);
			}
		}
		//夜班
		if(col == ds.nameToIndex("includenightshift")){
			UFBoolean includenightshift = (UFBoolean) row.getValue(ds.nameToIndex("includenightshift"));
			if(includenightshift.booleanValue()){
				form.getElementById("nightbeginday").setEnabled(true);
				form.getElementById("nightbegintime").setEnabled(true);
				form.getElementById("nightbegintime").setNullAble(false);
				form.getElementById("nightendday").setEnabled(true);
				form.getElementById("nightendtime").setEnabled(true);
				form.getElementById("nightendtime").setNullAble(false);
			}else{
				row.setString(ds.nameToIndex("nightbegintime"), "");
				row.setString(ds.nameToIndex("nightendtime"), "");
				form.getElementById("nightbeginday").setEnabled(false);
				form.getElementById("nightbegintime").setEnabled(false);
				form.getElementById("nightendday").setEnabled(false);
				form.getElementById("nightendtime").setEnabled(false);
				form.getElementById("nightbegintime").setNullAble(true);
				form.getElementById("nightendtime").setNullAble(true);
			}
		}
		//上下班是否弹性
		if(col == ds.nameToIndex("isotflexible")){
			UFBoolean isotflexible = (UFBoolean) row.getValue(ds.nameToIndex("isotflexible"));
			if(isotflexible.booleanValue()){
				form.getElementById("beginday").setText("最早上班时间");
				form.getElementById("endday").setText("最晚下班时间");
				
				row.setValue(ds.nameToIndex("begintime"), "");
				row.setValue(ds.nameToIndex("endtime"), "");
				row.setValue(ds.nameToIndex("gzsj"), "");
				form.getElementById("endday").setEnabled(false);
				form.getElementById("endtime").setEnabled(false);
				form.getElementById("endtime").setNullAble(true);
				form.getElementById("latestbeginday").setVisible(true);
				form.getElementById("latestbegintime").setVisible(true);
				form.getElementById("earliestendday").setVisible(true);
				form.getElementById("earliestendtime").setVisible(true);
				form.getElementById("earliestendday").setEnabled(false);
				form.getElementById("earliestendtime").setEnabled(false);
				form.getElementById("earliestendtime").setNullAble(true);
				
				form.getElementById("issinglecard").setEnabled(false);
				form.getElementById("isrttimeflexible").setEnabled(false);
				form.getElementById("gzsj").setEnabled(true);
			}else{
				form.getElementById("beginday").setText("上班时间");
				form.getElementById("endday").setText("下班班时间");
				form.getElementById("endday").setEnabled(true);
				form.getElementById("endtime").setEnabled(true);
				form.getElementById("endtime").setNullAble(false);
				form.getElementById("latestbeginday").setVisible(false);
				form.getElementById("latestbegintime").setVisible(false);
				form.getElementById("earliestendday").setVisible(false);
				form.getElementById("earliestendtime").setVisible(false);
				form.getElementById("latestbeginday").setVisible(false);
				form.getElementById("latestbegintime").setVisible(false);
				form.getElementById("earliestendday").setVisible(false);
				form.getElementById("earliestendtime").setVisible(false);

				form.getElementById("latestbegintime").setNullAble(true);
				form.getElementById("earliestendtime").setNullAble(true);
				
				form.getElementById("issinglecard").setEnabled(true);
				form.getElementById("isrttimeflexible").setEnabled(true);
				row.setValue(ds.nameToIndex("gzsj"), "");
				form.getElementById("gzsj").setEnabled(false);
			}
		}
		//工休时间弹性
		if(col == ds.nameToIndex("isrttimeflexible")){
			UFBoolean isrttimeflexible = (UFBoolean) row.getValue(ds.nameToIndex("isrttimeflexible"));
			if(isrttimeflexible.booleanValue()){
				form.getElementById("isotflexible").setEnabled(false);
				form.getElementById("issinglecard").setEnabled(false);
			}else{
				form.getElementById("isotflexible").setEnabled(true);
				form.getElementById("issinglecard").setEnabled(true);
			}
		}
		FormComp form2 = (FormComp) ViewUtil.getCurrentView().getViewComponents().getComponent("lateleave_form");
		//系统自动计算旷工工时
		if(col == ds.nameToIndex("isautokg")){
			UFBoolean isautokg = (UFBoolean) row.getValue(ds.nameToIndex("isautokg"));
			if(isautokg.booleanValue()){
				form2.getElementById("kghours").setEnabled(true);
			}else{
				row.setString(ds.nameToIndex("kghours"), "");
				form2.getElementById("kghours").setEnabled(false);
			}
		}
		FormComp form3 = (FormComp) ViewUtil.getCurrentView().getViewComponents().getComponent("overtime_form");
		//下班延时处理规则
		if(col == ds.nameToIndex("useovertmrule")){
			UFBoolean useovertmrule = (UFBoolean) row.getValue(ds.nameToIndex("useovertmrule"));
			if(useovertmrule.booleanValue()){
				form3.getElementById("overtmbeyond").setEnabled(true);
				form3.getElementById("overtmbegin").setEnabled(true);
				form3.getElementById("overtmbeyond").setNullAble(false);
				form3.getElementById("overtmbegin").setNullAble(false);
			}else{
				form3.getElementById("overtmbeyond").setNullAble(true);
				form3.getElementById("overtmbegin").setNullAble(true);
				row.setString(ds.nameToIndex("overtmbeyond"), "");
				row.setString(ds.nameToIndex("overtmbegin"), "");
				form3.getElementById("overtmbeyond").setEnabled(false);
				form3.getElementById("overtmbegin").setEnabled(false);
			}
		}
		//提前上班处理规则
		if(col == ds.nameToIndex("useontmrule")){
			UFBoolean useontmrule = (UFBoolean) row.getValue(ds.nameToIndex("useontmrule"));
			if(useontmrule.booleanValue()){
				form3.getElementById("ontmbeyond").setEnabled(true);
				form3.getElementById("ontmend").setEnabled(true);
				form3.getElementById("ontmbeyond").setNullAble(false);
				form3.getElementById("ontmend").setNullAble(false);
			}else{
				form3.getElementById("ontmbeyond").setNullAble(true);
				form3.getElementById("ontmend").setNullAble(true);
				row.setString(ds.nameToIndex("ontmbeyond"), "");
				row.setString(ds.nameToIndex("ontmend"), "");
				form3.getElementById("ontmbeyond").setEnabled(false);
				form3.getElementById("ontmend").setEnabled(false);
			}
		}
		Dataset subds = ViewUtil.getDataset(ViewUtil.getCurrentView(),StoreShiftConsts.DS_SUB_NAME);
		Row[] subrow = subds.getAllRow();
		int resttimes = 0;
		for(int i=0;i<subrow.length;i++){
			String subbegintime = subrow[i].getString(subds.nameToIndex("begintime"));
			String time1 = timeFormate(subbegintime);
			subrow[i].setString(subds.nameToIndex("begintime"), time1);
			String subendtime = subrow[i].getString(subds.nameToIndex("endtime"));
			String time2 = timeFormate(subendtime);
			subrow[i].setString(subds.nameToIndex("endtime"), time2);
			
			int subbeginday = (Integer) subrow[i].getValue(subds.nameToIndex("beginday"));
			int subendday = (Integer)subrow[i].getValue(subds.nameToIndex("endday"));
			if(StringUtils.isNotEmpty(time1)&&StringUtils.isNotEmpty(time2)){
				String tm1 = getDateByStr(subbeginday+"")+" "+time1;
				String tm2 = getDateByStr(subendday+"")+" "+time2;
				String resttime = getDurationMin(tm1,tm2);
				resttimes += Integer.parseInt(resttime);
				subrow[i].setString(subds.nameToIndex("resttime"), resttime);

			}
		}
		UFBoolean isotflexible1 = (UFBoolean) row.getValue(ds.nameToIndex("isotflexible"));
		if(isotflexible1.booleanValue()){
			UFDouble gzsj = new UFDouble();
			row.getValue(ds.nameToIndex("gzsj"));
			if(row.getValue(ds.nameToIndex("gzsj"))!=null && !"".equals(row.getValue(ds.nameToIndex("gzsj")))){
				gzsj = (UFDouble) row.getValue(ds.nameToIndex("gzsj"));
			}
			
			int beginday = (Integer) row.getValue(ds.nameToIndex("beginday"));
			row.setValue(ds.nameToIndex("earliestendday"), beginday);
			int latestbeginday = (Integer) row.getValue(ds.nameToIndex("latestbeginday"));
			row.setValue(ds.nameToIndex("endday"), latestbeginday);
			String begintime1 = row.getString(ds.nameToIndex("begintime"));
			//double sc = Double.parseDouble((gzsj!=null&&gzsj!="")?gzsj:"0");
			String  earliestendtime1 = getTimeByGzsj(gzsj,begintime1,resttimes);
			row.setValue(ds.nameToIndex("earliestendtime"), earliestendtime1);
			String latestbegintime1 = row.getString(ds.nameToIndex("latestbegintime"));
			String  endtime1 = getTimeByGzsj(gzsj,latestbegintime1,resttimes);
			row.setValue(ds.nameToIndex("endtime"), endtime1);
			
			int worklen = (int) (gzsj.doubleValue()*60*60);
			row.setValue(ds.nameToIndex("worklen"), worklen);
			/*if(col == ds.nameToIndex("gzsj")){
				
				double gz1 =  gzsj.doubleValue()-resttimes/60.00;
				row.setValue(ds.nameToIndex("gzsj"), new UFDouble(gz1));
			}*/
		}else{
			if(StringUtils.isNotEmpty(btime)&&StringUtils.isNotEmpty(etime)){
				int beginday = (Integer) row.getValue(ds.nameToIndex("beginday"));
				int endday = (Integer)row.getValue(ds.nameToIndex("endday"));
				String tm1 = getDateByStr(beginday+"")+" "+btime;
				String tm2 = getDateByStr(endday+"")+" "+etime;
				String gzsj = getDurationHour(tm1,tm2);
				//row.setString(ds.nameToIndex("gzsj"), gzsj);
				int worklen = (int) (Double.parseDouble(gzsj)*60*60);
				row.setValue(ds.nameToIndex("worklen"), worklen);
				
				if(StringUtils.isNotEmpty(gzsj)){
					double gz = Double.parseDouble(gzsj);
					double gz1 =  gz-resttimes/60.00;
					row.setString(ds.nameToIndex("gzsj"),gz1+"");
				}
			}
		}
		
		
		
		
	}
	/**
	   * 时间格式转换
	   * @param time
	   * @return
	   */
	  	private String timeFormate(String time){
	  		
	  		if(StringUtils.isBlank(time))
	  			return time;
	  		if(time.contains(":")){
	  			time = time.replaceAll(":", "");
	  		}
	  		try {
	  			String tmp = null;
	  			if(time.length()==5){
	  				tmp = time+"0";
	  			}else if(time.length()==4){
	  				tmp = time+"00";
	  			}else if(time.length()==3){
	  				tmp = time+"000";
	  			}else if(time.length()==2){
	  				tmp = time+"0000";
	  			}else if(time.length()==1){
	  				tmp = time+"00000";
	  			}else if(time.length()>6){
	  				tmp="000000";
	  			}else{
	  				tmp = time;
	  			}
	  			SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HHmmss");
	  			Date date = simpleDateFormat.parse(tmp);
				SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("HH:mm:ss");
				return simpleDateFormat2.format(date);
			} catch (Exception e) {
				Logger.error(e.getMessage());
				return null;
			}
	  	}
	  	/**
	  	 * 计算休息时长（分钟）
	  	 * @param begintime
	  	 * @param endtime
	  	 * @return
	  	 */
	  	private String getDurationMin(String begintime,String endtime){
	  		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  		try {
				if(StringUtils.isEmpty(begintime)||StringUtils.isEmpty(endtime)){
					return null;
				}
				Date time1 = sdf.parse(begintime);
				Date time2 = sdf.parse(endtime);
				long tmp = time2.getTime()-time1.getTime();
				if(tmp<=0){
					return null;
				}else{
					long mins = tmp/1000/60;
					return mins+"";
				}
				
			} catch (ParseException e) {
				Logger.error(e.getMessage());
				return null;
			}
	  	}
	  	/**
	  	 * 获取时长--小时
	  	 * @param begintime
	  	 * @param endtime
	  	 * @return
	  	 */
	  	private String getDurationHour(String begintime,String endtime){
	  		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  		try {
				if(StringUtils.isEmpty(begintime)||StringUtils.isEmpty(endtime)){
					return null;
				}
				Date time1 = sdf.parse(begintime);
				Date time2 = sdf.parse(endtime);
				long tmp = time2.getTime()-time1.getTime();
				if(tmp<=0){
					return null;
				}else{
					double min = tmp/1000/60/60.00;
					return min+"";
				}
			} catch (ParseException e) {
				Logger.error(e.getMessage());
				return null;
			}
	  	}
	  	/**
	  	 * 根据相对日期枚举  获取具体日期
	  	 * @param str
	  	 * @return
	  	 */
	  	private String getDateByStr(String str){
	  		if(StringUtils.isEmpty(str)){
	  			return null;
	  		}
	  		String date = null;
	  		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	  		if("0".equals(str)){
	  			date = sdf.format(new Date());
	  		}else if("-1".equals(str)){
	  			date = getBeforeDay();
	  		}else{
	  			date = getAfterDay();
	  		}
	  		return date;
	  	}
	  	/**
	  	 * 获取前一天的日期
	  	 * @return
	  	 */
	  	private String getBeforeDay(){
	  		String beforeDay = null;
	  		Date dNow = new Date();   //当前时间
	  		Date dBefore = new Date();
	  		Calendar calendar = Calendar.getInstance(); //得到日历
	  		calendar.setTime(dNow);//把当前时间赋给日历
	  		calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
	  		dBefore = calendar.getTime();   //得到前一天的时间
	  		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
	  		beforeDay = sdf.format(dBefore);
	  		return beforeDay;
	  	}
	  	/**
	  	 * 获取后一天的日期
	  	 * @return
	  	 */
	  	private String getAfterDay(){
	  		String afterDay = null;
	  		Date dNow = new Date();   //当前时间
	  		Date dAfter = new Date();
	  		Calendar calendar = Calendar.getInstance(); //得到日历
	  		calendar.setTime(dNow);//把当前时间赋给日历
	  		calendar.add(Calendar.DAY_OF_MONTH, 1);  //设置为前一天
	  		dAfter = calendar.getTime();   //得到前一天的时间
	  		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
	  		afterDay = sdf.format(dAfter);
	  		return afterDay;
	  	}
	  	/**
	  	 * 根据工作时长获取时间
	  	 * @param gzsj
	  	 * @return
	  	 */
	  	private String getTimeByGzsj(UFDouble gzsj,String begintime,int resttimes){
	  		String time = null;
	  		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	  		try {
	  			if(StringUtils.isEmpty(begintime)){
		  			return time;
		  		}
				Date time1 = sdf.parse(begintime);
				long tmp = (long) (time1.getTime()+(gzsj!=null ? gzsj.doubleValue() :0)*1000*60*60)+resttimes*1000*60;
				Date time2 = new Date();
				time2.setTime(tmp);
				time = sdf.format(time2);
				return time;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return time;
			}
	  		
	  	}
	  	/**
	  	 * 根据PK获取AggVo
	  	 * @param pk
	  	 * @return
	  	 */
	  	private AggShiftVO getAggVOByPk(String pk){
	  		AggShiftVO aggVO = null;
	  		try {
				aggVO = NCLocator.getInstance().lookup(IStoreShiftQueryMaintain.class).queryByPk(pk);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  		return aggVO;
	  	}
	  	
	  	/**
		 * 取消按钮操作
		 * 
		 * @param mouseEvent
		 */
		@SuppressWarnings("rawtypes")
		public void onCancel(MouseEvent mouseEvent) {
			// 关闭弹出页面
			CmdInvoker.invoke(new CloseWindowCmd());
		}
		
		/**
		 * 获得编码
		 * @param pk_group
		 * @param pk_org
		 * @param billType
		 * @return
		 */
		private String getBillCode(String pk_group, String pk_org, String billType) {
			return BillCoderUtils.getBillCode(pk_group, pk_org, billType);
		}
		
		/**
		 * 回滚单据编码
		 */
		public void rollBackBillCode() {
			String operateStatus = (String) getCurrentApplication().getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
			// 回滚自动生成的单据编辑
			if (HrssConsts.POPVIEW_OPERATE_EDIT.equals(operateStatus) || HrssConsts.POPVIEW_OPERATE_VIEW.equals(operateStatus)) {
				return;
			}
			Dataset masterDs =  AppLifeCycleContext.current().getViewContext().getView().getViewModels().getDataset(StoreShiftConsts.DS_MAIN_NAME);
			Row masterRow = masterDs.getSelectedRow();
			if (masterRow == null) {
				return;
			}
			// 单据编码
			String bill_code = (String) masterRow.getValue(masterDs.nameToIndex("code"));
			if (StringUtils.isEmpty(bill_code)) {
				return;
			}
			// 单据所在集团
			String pk_group = (String) masterRow.getValue(masterDs.nameToIndex("pk_group"));
			// 单据所在组织
			String pk_org = (String) masterRow.getValue(masterDs.nameToIndex("pk_org"));
			// 单据类型
			String billType = "HRTA_shift";
			// 编码由系统自动生成
			if (BillCoderUtils.isAutoGenerateBillCode(pk_group,pk_org, billType)) {
				// 回滚单据编码
				BillCoderUtils.rollbackPreBillCode(pk_group, pk_org, billType, bill_code);
			}
		}
}
