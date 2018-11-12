package nc.bs.hrsms.ta.SignReg.ctrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.SignReg.common.SignCardBaseViewCtrl;
import nc.bs.hrsms.ta.SignReg.lsnr.SignRegLineAddProcessor;
import nc.bs.hrsms.ta.SignReg.signreg.SignRegConsts;
import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.ResHelper;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hrss.pub.cmd.prcss.IAddProcessor;
import nc.itf.hrss.pub.cmd.prcss.ICopyProcessor;
import nc.itf.hrss.pub.cmd.prcss.IEditProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineDelProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineInsertProcessor;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.itf.hrss.ta.timeapply.IQueryOrgOrDeptVid;
import nc.itf.ta.ISignCardRegisterManageMaintain;
import nc.itf.ta.ISignCardRegisterQueryMaintain;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.pub.TALoginContext;
import nc.vo.ta.signcard.SignCardBeyondTimeVO;
import nc.vo.ta.signcard.SignRegVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 签卡登记卡片页面Controller
 * @author mayif
 * @date May 07, 2015
 */
public class SignRegCardMainctrl extends SignCardBaseViewCtrl implements IController{
  @SuppressWarnings("unused")
private static final long serialVersionUID=1L;
  @SuppressWarnings("unused")
private static final long ID=5L;
//  private String pk_operatePsn = null;
  /**
	 * 单据类型
	 * 
	 * @return
	 */
	@Override
	protected String getBillType() {
//		return LeaveConsts.BILL_TYPE_CODE;
		return null;
	}

	/**
	 * 数据集ID
	 * 
	 * @return
	 */
	@Override
	protected String getDatasetId() {
		return "SignReg_DataSet";
	}
	/**
	 * 单据聚合类型VO
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends AggregatedValueObject> getAggVoClazz() {
//		return LeaveConsts.CLASS_NAME_AGGVO;
		return null;
	}
	/**
	 * 修改的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends IEditProcessor> getEditPrcss() {
//		return RegLeaveEditProcessor.class;
		return null;
	}

	/**
	 * 复制的PROCESSOR
	 */
	@Override
	protected Class<? extends ICopyProcessor> getCopyPrcss() {
//		return RegLeaveCopyProcessor.class;
		return null;
	}

	/**
	 * 保存的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ISaveProcessor> getSavePrcss() {
//		return RegLeaveSaveProcessor.class;
		return null;
	}

	/**
	 * 保存并新增的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ISaveProcessor> getSaveAddPrcss() {
//		return RegLeaveSaveAddProcessor.class;
		return null;
	}

	/**
	 * 行新增的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ILineInsertProcessor> getLineAddPrcss() {
		return SignRegLineAddProcessor.class;
//		return null;
	}

	/**
	 * 行删除的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ILineDelProcessor> getLineDelPrcss() {
//		return LeaveLineDelProcessor.class;
		return null;
	}

	/**
	 * 新增的PROCESSOR
	 */
	@Override
	protected Class<? extends IAddProcessor> getAddPrcss() {
//		return RegLeaveAddProcessor.class;
		return null;
	}
	/**
	 * onAfterDataChange事件
	 * 处理值变化
	 * @param datasetCellEvent
	 */
	public void onAfterDataChange(DatasetCellEvent datasetCellEvent){
//		SignRegVO.PK_PSNJOB;
		Dataset ds = datasetCellEvent.getSource();
		// 字段顺序
		int colIndex = datasetCellEvent.getColIndex();
		if(colIndex != ds.nameToIndex(SignRegVO.PK_PSNJOB)){
			return;
		}
		Row row = ds.getSelectedRow();
		if(row == null ){
			return;
		}
		if(colIndex == ds.nameToIndex(SignRegVO.PK_PSNJOB)){
			
			String pk_psndoc  = (String) row.getValue(ds.nameToIndex("pk_psndoc"));
			ShopTaAppContextUtil.addTaAppForTransferContext(pk_psndoc);
			SignRegAfterDataChange.onAfterDataChange(ds, row);
		}
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
	 * 保存
	 * @param mouseEvent
	 */
	public void onSave(MouseEvent<MenuItem> mouseEvent){
		Dataset ds =  ViewUtil.getCurrentView().getViewModels().getDataset(getDatasetId());
//		LfwView viewMain = getCurrentActiveView();
		Row row = ds.getSelectedRow();
		Row[] selRow = ds.getAllRow();
		//条件判断
	    if (row.getValue(ds.nameToIndex(SignRegVO.PK_PSNJOB)) == null) {
			throw new LfwRuntimeException("请先选择人员！");
			}
		if(selRow == null || selRow.length==0){
			throw new LfwRuntimeException("请输入签卡信息！");
		}
	    SuperVO  vo =new Dataset2SuperVOSerializer<SuperVO>().serialize(ds)[0];
	    SignRegVO  SnregVO = new SignRegVO();
	    String[] names = SnregVO.getAttributeNames();
    	for(int i =0;i<names.length;i++){
    		SnregVO.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
		}
    	//考勤档案已经结束的人员新增档案结束日期前的数据时pk_psnorg字段为空，无法保存数据
    	PsnJobVO psnjobVO = null;
    	try {
    		psnjobVO = (PsnJobVO) ServiceLocator.lookup(IPersistenceRetrieve.class).retrieveByPk(PsnJobVO.class, SnregVO.getPk_psnjob(), null);
    	} catch (BusinessException e1) {
			throw new LfwRuntimeException(e1.getMessage());
		} catch (HrssException e1) {
			throw new LfwRuntimeException(e1.getMessage());
		}
    	SnregVO.setPk_psnorg(psnjobVO.getPk_psnorg());
    	List<SignRegVO> listVO = new ArrayList<SignRegVO>();
	    ISignCardRegisterManageMaintain service=NCLocator.getInstance().lookup(ISignCardRegisterManageMaintain.class);
	    String pk_org = SessionUtil.getPsndocVO().getPk_hrorg();
    	String pk_group=SessionUtil.getPsndocVO().getPk_group();
	    try{
	    	for(int i=0;i<selRow.length;i++){
	    		SignRegVO saveVO=new SignRegVO();
	    		String pk_psndoc = (String) selRow[i].getValue(ds.nameToIndex("pk_psndoc"));
				String pk_psnjob = (String) selRow[i].getValue(ds.nameToIndex("pk_psnjob"));
				ShopTaAppContextUtil.addTaAppContext(pk_psndoc);
				
				saveVO.setPk_psndoc(pk_psndoc);
				saveVO.setPk_psnjob(pk_psnjob);
				saveVO.setPk_group(pk_group);
				saveVO.setPk_org(pk_org);
				saveVO.setBillsource(new Integer(2));
				
				
				saveVO.setSignreason((String)selRow[i].getValue(ds.nameToIndex("signreason")));
				saveVO.setSignstatus( (Integer)selRow[i].getValue(ds.nameToIndex("signstatus")));
				saveVO.setSignremark((String)selRow[i].getValue(ds.nameToIndex("signremark")));
				saveVO.setSigntime((UFDateTime)selRow[i].getValue(ds.nameToIndex("signtime")) );
				saveVO.setSignarea((String)selRow[i].getValue(ds.nameToIndex("signarea")));//add by ward 20180418
				saveVO.setSignshift((String)selRow[i].getValue(ds.nameToIndex("signshift")));
				if(SnregVO.getSigntime()==null){
		    		throw new BusinessException(ResHelper.getString("6017signcardapp","签卡时间不能为空！")
							/*@res"签卡时间不能为空！"*/);
		    	}
		    	
		    	saveVO.setCreationtime(SnregVO.getCreationtime());
		    	saveVO.setCreator(SnregVO.getCreator());
		    	saveVO.setPk_psnorg(SnregVO.getPk_psnorg());
//		    	TBMPsndocVO tbmPsndocVO = ShopTaAppContextUtil.getTBMPsndocVO();
//		    	saveVO.setPk_psnorg(tbmPsndocVO.getPk_psnorg());
		    	List<String> list = getVersionIds(pk_psnjob);
				if (list != null && list.size() > 0) {
					// 人员任职所属业务单元的版本id
					saveVO.setPk_org_v(list.get(0));
					// 人员任职所属部门的版本pk_dept_v
					saveVO.setPk_dept_v(list.get(1));
				}
				listVO.add(saveVO);
	    	}
 	    	
	    	SignRegVO[] Scvos= listVO.toArray(new SignRegVO[listVO.size()]);
	    	// 保存前先校验一次，如果有单据冲突，但是是被允许的，则显示这些冲突单据，并询问用户是否保存
	    	// 第一次保存
			SignCardBeyondTimeVO[] beyondVOs = NCLocator.getInstance().lookup(ISignCardRegisterManageMaintain.class).firstBatchInsert(pk_org, Scvos);
			// 有返回值表示有超过签卡次数的
			if(!ArrayUtils.isEmpty(beyondVOs)){
				LoginContext context = SessionUtil.getLoginContext();
				int signcounts= ((TALoginContext) context).getAllParams().getTimeRuleVO().getSigncounts();//签卡次数
				StringBuilder sb = new StringBuilder();
				
				String[] pk_psndocs=new String [beyondVOs.length];//超过次数的人员主键数组
				for(int m=0;m<beyondVOs.length;m++){
					pk_psndocs[m]=beyondVOs[m].getPk_psndoc();
				}
				
				IPsndocQueryService ipsndocquery =NCLocator.getInstance().lookup(IPsndocQueryService.class);//人员查询服务接口
				PsndocVO[] psndocvos=ipsndocquery.queryPsndocByPks(pk_psndocs);//获得超过次数的所有人员VO
				Map<Object, PsndocVO> psnMap = CommonUtils.toMap(PsndocVO.PK_PSNDOC, psndocvos);
				for(int j=0;j<beyondVOs.length;j++){
					//获取超过签卡次数的人员姓名
//					
					String name = MultiLangHelper.getName(psnMap.get(beyondVOs[j].getPk_psndoc()));
					sb.append(ResHelper.getString("6017signcardreg","06017signcardreg0004"
							,new String[] { beyondVOs[j].getPeriod(), beyondVOs[j].getSigncounts().toString(),name,String.valueOf(signcounts),String.valueOf(beyondVOs[j].getSigncounts()-signcounts)})
	                /*@res "{2}在考勤期间【{0}】签卡次数{1}次,规定签卡次数{3}次，超出了{4}次！"*/);
					sb.append("\n");
				}
				sb.append(ResHelper.getString("6017signcardreg","06017signcardreg0005")
	                /*@res "是否继续?"*/);
				if (CommonUtil.showConfirmDialog(sb.toString())) {
					SignRegVO[] vos = new SignRegVO[1];
					SignRegVO vo1=new SignRegVO();
					String[] names2 = SnregVO.getAttributeNames();
			    	for(int i =0;i<names2.length;i++){
			    		vo1.setAttributeValue(names[i], vo.getAttributeValue(names[i]));
					}
			    	vo1.setSigntime(new UFDateTime(SnregVO.getSigntime().toString()));
			    	vo1.setPk_group(pk_group);
			    	vo1.setPk_org(pk_org);
			    	vo1.setBillsource(new Integer(2));
			    	vo1.setSignstatus(new Integer(SnregVO.getSignstatus()));
			    	vos[0]=vo1;
			    	String pk = vos[0].getPrimaryKey();
			    	if(StringUtils.isEmpty(pk)){
						service.insertArrayData(vos);
					}
			    	else{
//						service.updateArrayData(vos);
						service.updateData(vos[0]);
					}
				}
	    	}
			
	    	
	    	CommonUtil.showShortMessage("保存成功！");
			// 关闭弹出页面
			CmdInvoker.invoke(new CloseWindowCmd());
			// 执行左侧快捷查询
			CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET,"closewindow"));
		
	    }
	    catch (BusinessException e) {
			new HrssException(e.getMessage()).alert();
			//e.printStackTrace();
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
	 * 获得当前片段
	 * 
	 * @return
	 */
	protected LfwView getCurrentActiveView() {
		return AppLifeCycleContext.current().getViewContext().getView();
	}
	@Override
	protected String getDetailDsId() {
		return SignRegConsts.DS_SUB_NAME;
	}
	/**
	 * beforeShow事件
	 * @param dialogEvent
	 */
	public void onBeforeShow(DialogEvent dialogEvent) {
		Dataset ds = ViewUtil.getDataset(ViewUtil.getCurrentView(),SignRegConsts.DS_MAIN_NAME);
		String pk_tbm_overtimereg = (String)AppLifeCycleContext.current().getApplicationContext().getAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM);
		AppLifeCycleContext.current().getApplicationContext().removeAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM);
		if(StringUtils.isEmpty(pk_tbm_overtimereg)){
			Row row = ds.getEmptyRow();
			ds.setCurrentKey(Dataset.MASTER_KEY);
			row.setString(ds.nameToIndex("pk_org"), SessionUtil.getPk_org());
		    row.setString(ds.nameToIndex("pk_group"), SessionUtil.getPk_group());
		    row.setString(ds.nameToIndex("creator"), SessionUtil.getPk_user());
		    row.setValue(ds.nameToIndex("billsource"), ICommonConst.BILL_SOURCE_REG);
		    
			ds.addRow(row);
			ds.setRowSelectIndex(0);
			ds.setEnabled(true);
		}else{
			GridComp grid = (GridComp) ViewUtil.getCurrentView().getViewComponents().getComponent("SignRegCard_List");
			nc.vo.ta.signcard.SignRegVO vo=getSignRegVOByPK(pk_tbm_overtimereg);
			new SuperVO2DatasetSerializer().serialize(new SuperVO[]{vo}, ds, Row.STATE_NORMAL);
			ds.setCurrentKey(Dataset.MASTER_KEY);
			ds.setRowSelectIndex(0);
			ds.setEnabled(false);
			//隐藏增行按钮及保存按钮
			grid.setShowImageBtn(false);
			SignSetMenuItemVisible.setMenuItemVisible("menubar", "save");
			SignSetMenuItemVisible.setMenuItemVisible("menubar", "cancel");
//			if(ICommonConst.BILL_SOURCE_REG != vo.getBillsource().intValue()){
//				SignSetMenuItemVisible.setMenuItemVisible("menubar", "save");
//				ds.setEnabled(false);
//			}else{
//				ds.setEnabled(true);
//			}
//			ds.setEnabled(true);
		}
		
	}
	/**
	 * 根据pk获取OvertimeRegVO
	 * @param pk
	 * @return VO
	 */
	private nc.vo.ta.signcard.SignRegVO getSignRegVOByPK(String pk){
		nc.vo.ta.signcard.SignRegVO vo = null;
		try {
			vo = NCLocator.getInstance().lookup(ISignCardRegisterQueryMaintain.class).queryByPk(pk);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vo;
	}
}
