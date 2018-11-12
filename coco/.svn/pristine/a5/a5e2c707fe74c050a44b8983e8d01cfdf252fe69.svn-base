package nc.bs.hrsms.ta.SignReg.ctrl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.SignReg.signreg.SignRegConsts;
import nc.bs.hrsms.ta.common.ctrl.TBMQueryPsnJobVOUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hrss.ta.timeapply.IQueryOrgOrDeptVid;
import nc.itf.ta.ISignCardRegisterManageMaintain;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.PaginationInfo;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.signcard.SignCardBeyondTimeVO;
import nc.vo.ta.signcard.SignRegVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;


public class BatchSignRegMainctrl implements IController{
 
// private static final String PK_TBM_SIGNREG = "pk_tbm_signreg";
  
  public void onDatasetLoad_dsPerson(DataLoadEvent dataLoadEvent){
		
	  	PsnJobVO[] psnjobVOs = TBMQueryPsnJobVOUtil.getPsnJobs();
		if (null == psnjobVOs || psnjobVOs.length == 0) {
			return;
		}
		Dataset dsPsn = ViewUtil.getDataset(ViewUtil.getCurrentView(), "dsPerson");
		if (!isPagination(dsPsn)) {
			DatasetUtil.clearData(dsPsn);
			dsPsn.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		}
		dsPsn.setVoMeta(PsnJobVO.class.getName());
		SuperVO[] vos = DatasetUtil.paginationMethod(dsPsn, psnjobVOs);//psnJobList.toArray(new PsnJobVO[0]));
		new SuperVO2DatasetSerializer().serialize(vos, dsPsn, Row.STATE_NORMAL);
		
	}
	/**
	 * 分页操作标志
	 * 
	 * @param ds
	 * @return
	 */
	private boolean isPagination(Dataset ds) {
		PaginationInfo pg = ds.getCurrentRowSet().getPaginationInfo();
		return pg.getRecordsCount() > 0;
	}
  
	/**
	 * onAfterDataChange事件
	 * 处理值变化
	 * @param datasetCellEvent
	 */
	public void onAfterDataChange(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		// 字段顺序
		int colIndex = datasetCellEvent.getColIndex();
		if(colIndex != ds.nameToIndex("signEndDate")
		  &&colIndex != ds.nameToIndex("signBeginDate")
		  &&colIndex != ds.nameToIndex("signtimeto")
				){
			return;
		}
		Row row = ds.getSelectedRow();
		if(row == null ){
			return;
		}
		//修改开始时间、结束时间
		//FormComp form = (FormComp) ViewUtil.getCurrentView().getViewComponents().getComponent("headTab_card_awayinf_form");
		if(colIndex == ds.nameToIndex("signBeginDate")||colIndex == ds.nameToIndex("signEndDate")
				||colIndex == ds.nameToIndex("signtimeto")){
			UFDateTime signBeginDate = (UFDateTime) row.getValue(ds.nameToIndex("signBeginDate"));
			UFDateTime signEndDate=(UFDateTime) row.getValue(ds.nameToIndex("signEndDate"));
			String signtimeto= (String)row.getValue(ds.nameToIndex("signtimeto"));
			row.setValue(ds.nameToIndex("signtimeto"), timeFormate(signtimeto));
			if(signBeginDate.getDate().after(signEndDate.getDate())){
				CommonUtil.showErrorDialog("提示", "开始日期不能晚于结束日期，请重新输入！");
			}
			if(signBeginDate.equals(signEndDate)){
				return;
			}
		}
		
	}
	protected String getDatasetId() {
		
		return "SignReg_DataSet";
	}
	/**
	 * 保存
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void onSave(MouseEvent<MenuItem> mouseEvent) throws BusinessException{
		Dataset ds =  ViewUtil.getCurrentView().getViewModels().getDataset(getDatasetId());
		Row row = ds.getSelectedRow();
		if (row.getValue(ds.nameToIndex("signBeginDate")) == null||row.getValue(ds.nameToIndex("signEndDate")) == null) {
			throw new LfwRuntimeException("请先选择开始或结束日期！");
		}
		if (row.getValue(ds.nameToIndex("signreason_name")) == null) {
			throw new LfwRuntimeException("请选择签卡原因！");
		}
		
		   UFDateTime signBeginDate = (UFDateTime) row.getValue(ds.nameToIndex("signBeginDate"));
		   UFDateTime signEndDate=(UFDateTime) row.getValue(ds.nameToIndex("signEndDate"));
		   String signtimeto= (String)row.getValue(ds.nameToIndex("signtimeto"));
		   if(signtimeto==null||signtimeto.isEmpty()){
			   throw new LfwRuntimeException("请输入签卡时间！");
		   }
		   signBeginDate.getDate();
		   String tmp = new UFLiteralDate(signBeginDate.toString())+" "+signtimeto.toString();
		   UFDateTime signBeginDate1 = new UFDateTime(tmp);
		   UFDate begin=signBeginDate.getBeginDate();
		   UFDate end=signEndDate.getEndDate();
		  
		   int dayLenth=UFDate.getDaysBetween(begin, end);
	       UFDateTime[] days= new UFDateTime [dayLenth+1];
	       
	       for (int i=0;i<=dayLenth;i++){
	    	   days[i]=signBeginDate1.getDateTimeAfter(i);
	    	   
	       }
	       String remark=(String) row.getValue(ds.nameToIndex("signremark"));

	       String reasoncode=(String) row.getValue(ds.nameToIndex("signreason"));
	       Integer status=(Integer) row.getValue(ds.nameToIndex("signstatus"));
		SuperVO  vo = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds)[0];
		SignRegVO  SnregVO = new SignRegVO();
		
		
		String[] names = SnregVO.getAttributeNames();
		for(int i =0;i<names.length;i++){
			SnregVO.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
		}

		
		
		Dataset dsPsn =  ViewUtil.getCurrentView().getViewModels().getDataset("dsPerson");
		Row[] selRow = dsPsn.getAllSelectedRows();
		
		
		if(selRow == null || selRow.length==0){
			throw new LfwRuntimeException("请选择人员！");
		}
		
//		ISignCardRegisterManageMaintain service=NCLocator.getInstance().lookup(ISignCardRegisterManageMaintain.class);	    
	    String pk_org = SessionUtil.getPsndocVO().getPk_hrorg();
    	String pk_group=SessionUtil.getPsndocVO().getPk_group();
		
		List<SignRegVO> listVO = new ArrayList<SignRegVO>();
		for(int i=0;i<selRow.length;i++){
			SignRegVO[] saveVOs=new SignRegVO[days.length];
			for(int j=0;j<days.length;j++){
				saveVOs[j]=new SignRegVO();
				
				String pk_psndoc = (String) selRow[i].getValue(dsPsn.nameToIndex("pk_psndoc"));
				String pk_psnjob = (String) selRow[i].getValue(dsPsn.nameToIndex("pk_psnjob"));
				ShopTaAppContextUtil.addTaAppContext(pk_psndoc);
				saveVOs[j].setSigntime(days[j]);
				saveVOs[j].setPk_group(pk_group);
				saveVOs[j].setPk_org(pk_org);
				saveVOs[j].setBillsource(new Integer(2));
				saveVOs[j].setSignstatus(new Integer(status));
				if(saveVOs[j].getSigntime()==null){
		    		throw new BusinessException(ResHelper.getString("6017signcardapp","开始或截止日期为空")
							/*@res"签卡内容不能为空！"*/);
		    	}	
				
				saveVOs[j].setSignreason(new String(reasoncode));
				saveVOs[j].setSignremark(remark);
				saveVOs[j].setPk_psndoc(pk_psndoc);
				saveVOs[j].setPk_psnjob(pk_psnjob);
				saveVOs[j].setCreator(SnregVO.getCreator());
				saveVOs[j].setCreationtime(SnregVO.getCreationtime());
				TBMPsndocVO tbmPsndocVO = ShopTaAppContextUtil.getTBMPsndocVO();
				saveVOs[j].setPk_psnorg(tbmPsndocVO.getPk_psnorg());
				List<String> list = getVersionIds(pk_psnjob);
				if (list != null && list.size() > 0) {
					// 人员任职所属业务单元的版本id
					saveVOs[j].setPk_org_v(list.get(0));
					// 人员任职所属部门的版本pk_dept_v
					saveVOs[j].setPk_dept_v(list.get(1));
				}
				listVO.add(saveVOs[j]);
			}
		}
		SignRegVO[] Scvos = listVO.toArray(new SignRegVO[listVO.size()]);
		
		/*String[] name2 = SnregVO.getAttributeNames();
		for(int k=0;k<Scvos.length;k++){
			for(int i =0;i<name2.length;i++){
				if(name2[i].equals(PK_TBM_SIGNREG))
					continue;
				else
				    Scvos[k].setAttributeValue(name2[i], Scvos[k].getAttributeValue(name2[i]));
			}
		}*/
		
		
		
		try {
			// 第一次保存
			SignCardBeyondTimeVO[] beyondVOs = NCLocator.getInstance().lookup(ISignCardRegisterManageMaintain.class).firstBatchInsert(pk_org, Scvos);
//			// 有返回值表示有超过签卡次数的
			if(!ArrayUtils.isEmpty(beyondVOs)){
				if(CommonUtil.showConfirmDialog("签卡超过规定签卡次数，是否继续？")){
					// 第二次保存
					NCLocator.getInstance().lookup(ISignCardRegisterManageMaintain.class).secondBatchInsert(pk_org, Scvos, beyondVOs);
				}
//				SignCardBeyondTimeVO[] unSelectedBeyonds = showBeyondTimeDialog(beyondVOs);
				
	    	}
			
//			String pk = SnregVO.getPrimaryKey();
//	    	if(StringUtils.isEmpty(pk)){
//				service.insertData(Scvos,false);
//			}
	    	
	    	CommonUtil.showShortMessage("保存成功！");
			// 关闭弹出页面
			CmdInvoker.invoke(new CloseWindowCmd());
			// 执行左侧快捷查询
			CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET,"closewindow"));
		
			
			
		} catch (BusinessException e) {
			new HrssException(e.getMessage()).alert();
		}
		
	}

	
	/**
	 * 获得人员任职所属业务单元/部门的版本id
	 * 
	 * @param pk_psnjob
	 * @return
	 */
	private static List<String> getVersionIds(String pk_psnjob) {
		List<String> list = null;
		IQueryOrgOrDeptVid service;
		try {
			service = ServiceLocator.lookup(IQueryOrgOrDeptVid.class);
			list = service.getOrgOrDeptVidByPsnjob(pk_psnjob);
		} catch (HrssException ex) {
			ex.alert();
		} catch (BusinessException ex) {
			new HrssException(ex).deal();
		}
		return list;
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
  
  
//  /**
//	 * 取消
//	 * 
//	 * @param mouseEvent
//	 */
//	@SuppressWarnings("rawtypes")
//	public void onCancel(MouseEvent mouseEvent) {
//		AppLifeCycleContext.current().getApplicationContext().closeWinDialog();
//	}
  
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
	 * beforeShow事件
	 * @param dialogEvent
	 */
	public void onBeforeShow(DialogEvent dialogEvent) {
		Dataset ds = ViewUtil.getDataset(ViewUtil.getCurrentView(),SignRegConsts.DS_MAIN_NAME);
		Row row = ds.getEmptyRow();
//		String str;
//		String str2;
//	    ITimeScope defaultScope = ShopDefaultTimeScope.getDefaultTimeScope(SessionUtil.getPk_org(), null, null, TimeZone.getDefault());
//	    str =defaultScope.getScope_start_datetime().toString();
//	    str2=defaultScope.getScope_end_datetime().toString();
//		UFLiteralDate[] dates = TBMPeriodUtil.getDefaultBeginEndDate();
	    row.setValue(ds.nameToIndex("signBeginDate"),new UFDateTime().getDate());
        row.setValue(ds.nameToIndex("signEndDate"),  new UFDateTime().getDate());
//	    row.setValue(ds.nameToIndex("signtimeto"),   new UFTime());
//	    String aa=new UFTime().toString();
	    row.setValue(ds.nameToIndex("signstatus"), new Integer(0));
//	    row.setValue(ds.nameToIndex("signreason_name"), new Integer(1));
//	    row.setValue(ds.nameToIndex("signremark"), );
	  
		
		
		ds.setCurrentKey(Dataset.MASTER_KEY);
		ds.addRow(row);
		ds.setRowSelectIndex(0);
		ds.setEnabled(true);
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
}
