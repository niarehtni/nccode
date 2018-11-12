package nc.bs.hrsms.hi.employ.ShopDimission;

import java.util.ArrayList;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.hi.HiApplyBaseViewList;
import nc.bs.hrsms.hi.employ.ShopTransfer.TranListViewMain;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.trn.PsnApplyConsts;
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
import nc.uap.lfw.core.exception.LfwRuntimeException;
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
import nc.vo.trn.transmng.StapplyVO;

public class DimissionListViewMain extends HiApplyBaseViewList implements IController {
	  @SuppressWarnings("unused")
	private static final long serialVersionUID=1L;
	  @Override
	  public void addBill(  MouseEvent<MenuItem> mouseEvent){
			CmdInvoker.invoke(new UifOpenViewCmd(PsnApplyConsts.TRANSTYPE_VIEW_ID, "400", "200", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","选择业务类型")/*@res "选择业务类型0c_trn-res0002"*/));
	  }
	  public void RefreshBill(MouseEvent<MenuItem> mouseEvent){
		  CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	  }
	  
	  @Override protected SuperVO[] queryVOs(){
		  return TranListViewMain.queryStapplyVOs(TRNConst.TRNSEVENT_DIMIS);
	  }
	  
	  @Override
	  protected String getNodecode() {
	  	return PsnApplyConsts.DIMISSION_FUNC_CODE;
	  }
	  
	  @Override public void editBill(  MouseEvent<MenuItem> mouseEvent){
	    Dataset ds = getCurrentActiveView().getViewModels().getDataset(getDatasetId());
			Row selRow = ds.getSelectedRow();
			if (selRow == null) {
				throw new LfwRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","请选择待修改的记录！")/*@res "请选择待修改的记录！0c_trn-res0003"*/);
			}
			// 设置组织和部门pk到session
			SessionBean session = SessionUtil.getSessionBean();
			String newpk_org = selRow.getString(ds.nameToIndex(RegapplyVO.NEWPK_ORG));
			session.setExtendAttribute(PsnApplyConsts.TRN_NEWPK_ORG, newpk_org);
			String newpk_dept = selRow.getString(ds.nameToIndex(RegapplyVO.NEWPK_DEPT));
			session.setExtendAttribute(PsnApplyConsts.TRN_NEWPK_DEPT, newpk_dept);
			super.editBill(mouseEvent);
	  }
	  @Override protected String getDatasetId(){
	    return PsnApplyConsts.TRANSFER_DS_ID;
	  }
	  @Override protected Class<? extends AggregatedValueObject> getAggVOClazz(){
	    return PsnApplyConsts.TRANSFER_AGGVOCLASS;
	  }
	  @Override protected String getAddViewTitle(){
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","离职申请新增")/*@res "离职申请新增0c_trn-res0004"*/;
	  }
	  @Override protected String getDetailViewTitle(){
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","离职申请详细")/*@res "离职申请详细0c_trn-res0005"*/;
	  }
	  @Override protected String getEditViewTitle(){
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","离职申请修改")/*@res "离职申请修改0c_trn-res0006"*/;
	  }
	  @Override protected String getBillTypeCode(){
	    return PsnApplyConsts.DIMISSION_BILLTYPE_CODE;
	  }
	  public void onAfterRowSelect(  DatasetEvent datasetEvent){
		  ButtonStateManager.updateButtons();
	  }
	  
	    @Override
	    protected String getPopWindowId() {
		    return "ShopDimissionCard";
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
				SuperVO[] svos = queryPsndocVOsByDeptPK(pk_mng_dept,wheresql);
				new SuperVO2DatasetSerializer().serialize(svos, ds,Row.STATE_NORMAL);
			} catch (BusinessException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		public SuperVO[] queryPsndocVOsByDeptPK(String pkDept, String WhereSQL) throws BusinessException
		{
			String sql = "select * "+WhereSQL+" and oldpk_dept = '" + pkDept + "' and " + RegapplyVO.ISHRSSBILL + " = '" 
					+ UFBoolean.TRUE + "' and pk_trnstype in (select pk_trnstype from hr_trnstype where trnsevent = " 
					+ TRNConst.TRNSEVENT_DIMIS + ")  and APPROVE_STATE in ('-1','3') ";
			ArrayList<StapplyVO> al = (ArrayList<StapplyVO>) new BaseDAO().executeQuery(sql, new BeanListProcessor(StapplyVO.class));
			return al == null ? null : al.toArray(new SuperVO[0]);
		}
		
}
