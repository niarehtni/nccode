package nc.bs.hrsms.hi.employ.ShopTransfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.hrsms.hi.HiApplyBaseViewList;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.trn.PsnApplyConsts;
import nc.bs.logging.Logger;
import nc.itf.trn.transmng.ITransmngQueryService;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.uap.lfw.core.bm.ButtonStateManager;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifOpenViewCmd;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DatasetEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.trn.pub.TRNConst;
import nc.vo.trn.regmng.RegapplyVO;
import nc.vo.trn.transmng.AggStapply;
import nc.vo.trn.transmng.StapplyVO;

import org.apache.commons.lang.ArrayUtils;

public class TranListViewMain extends HiApplyBaseViewList implements IController{
	@SuppressWarnings("unused")
	private static final long serialVersionUID=1L;
	@Override 
	public void addBill(  MouseEvent<MenuItem> mouseEvent){
		CmdInvoker.invoke(new UifOpenViewCmd(PsnApplyConsts.TRANSTYPE_VIEW_ID, "400", "200", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","选择业务类型")/*@res "选择业务类型0c_trn-res0002"*/));
  }
	
  @Override 
  protected SuperVO[] queryVOs(){
		return queryStapplyVOs(TRNConst.TRNSEVENT_TRANS);
  }
  
  @Override
  protected String getNodecode() {
  	return PsnApplyConsts.TRANSFER_FUNC_CODE;
  }
  
    public static SuperVO[] queryStapplyVOs(int trnsevent){
    	SessionBean session = SessionUtil.getSessionBean();
		String pk_dept = SessionUtil.getPk_mng_dept();
		String condition = "oldpk_dept = '" + pk_dept + "' and " + RegapplyVO.ISHRSSBILL + " = '" + UFBoolean.TRUE + 
				"' and pk_trnstype in (select pk_trnstype from hr_trnstype where trnsevent = " + trnsevent + 
				")  and APPROVE_STATE in ('-1','3')" ;
		List<SuperVO> list = new ArrayList<SuperVO>();
		try {
			AggStapply[] aggVOs = ServiceLocator.lookup(ITransmngQueryService.class).queryByCondition(session.getContext(), condition);
			if (!ArrayUtils.isEmpty(aggVOs)) {
				for (AggStapply aggVO : aggVOs) {
					list.add((SuperVO) aggVO.getParentVO());
				}
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		} catch (HrssException e) {
			Logger.error(e.getMessage(), e);
		}
		return list.toArray(new SuperVO[0]);
  }
    public void RefreshBill(MouseEvent<MenuItem> mouseEvent){
		  CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	  }
    
    @Override
    protected String getPopWindowId() {
    	return "ShopTransferCard";
    }
  
  @Override protected String getDatasetId(){
    return PsnApplyConsts.TRANSFER_DS_ID;
  }
  @Override protected Class<? extends AggregatedValueObject> getAggVOClazz(){
    return PsnApplyConsts.TRANSFER_AGGVOCLASS;
  }
  @Override protected String getAddViewTitle(){
    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","调配申请新增")/*@res "调配申请新增0c_trn-res0030"*/;
  }
  @Override protected String getDetailViewTitle(){
    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","调配申请详细")/*@res "调配申请详细0c_trn-res0031"*/;
  }
  @Override protected String getEditViewTitle(){
    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","调配申请修改")/*@res "调配申请修改0c_trn-res0032"*/;
  }
  public void onAfterRowSelect(  DatasetEvent datasetEvent){
	  ButtonStateManager.updateButtons();
  }
  
  @Override
  protected String getBillTypeCode() {
	    return PsnApplyConsts.TRANSFER_BILLTYPE_CODE;
  }
  
  /**
	 * 选择业务类型窗口关闭事件
	 * 
	 * @param keys
	 */
	public void pluginopenMain(Map<String, Object> keys) {
		ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();
		appCtx.addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, HrssConsts.POPVIEW_OPERATE_ADD);
		appCtx.addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, null);
		CommonUtil.showWindowDialog(getPopWindowId(), getAddViewTitle(), "-1","-1", getParamMap(), ApplicationContext.TYPE_DIALOG,false,false);
	}
	
	/**
	 * 分类切换事件(改：条件查询事件)
	 * 
	 * @param keys
	 */
	public void plugininParam(Map<String, Object> keys) {
		try {
			// 获得选择的管理部门
			String pk_mng_dept = SessionUtil.getPk_mng_dept();
			if (StringUtil.isEmptyWithTrim(pk_mng_dept)) {
				return;
			}
			String wheresql=keys.get("whereSql")+" ";
			LfwView mainWidget = AppLifeCycleContext.current().getViewContext().getView();
			Dataset ds = mainWidget.getViewModels().getDataset(getDatasetId());
			DatasetUtil.clearData(ds);
			SuperVO[] svos = queryPsndocVOsByDeptPK(pk_mng_dept,wheresql);//DatasetUtil.paginationMethod(ds, psnJobVOs);
			new SuperVO2DatasetSerializer().serialize(svos, ds,Row.STATE_NORMAL);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}
	public SuperVO[] queryPsndocVOsByDeptPK(String pkDept, String WhereSQL) throws BusinessException
	{
		String sql = "select * "+WhereSQL+" and oldpk_dept = '" + pkDept + "' and " + RegapplyVO.ISHRSSBILL + " = '" 
				+ UFBoolean.TRUE + "' and pk_trnstype in (select pk_trnstype from hr_trnstype where trnsevent = " 
				+ TRNConst.TRNSEVENT_TRANS + ")  and APPROVE_STATE in ('-1','3') ";
		ArrayList<StapplyVO> al = (ArrayList<StapplyVO>) new BaseDAO().executeQuery(sql, new BeanListProcessor(StapplyVO.class));
		return al == null ? null : al.toArray(new SuperVO[0]);
	}
	
}
