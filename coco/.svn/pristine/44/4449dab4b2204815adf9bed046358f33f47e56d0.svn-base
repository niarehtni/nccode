package nc.bs.hrsms.hi.employ.ShopRegular;

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
import nc.itf.trn.regmng.IRegmngQueryService;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.uap.lfw.core.bm.ButtonStateManager;
import nc.uap.lfw.core.cmd.CmdInvoker;
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
import nc.vo.trn.regmng.AggRegapplyVO;
import nc.vo.trn.regmng.RegapplyVO;

import org.apache.commons.lang.ArrayUtils;

public class MainViewController extends HiApplyBaseViewList implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Override
	public void addBill(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		AppLifeCycleContext.current().getApplicationContext()
				.addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, HrssConsts.POPVIEW_OPERATE_ADD);
		AppLifeCycleContext.current().getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, null);
		CommonUtil.showWindowDialog(getPopWindowId(), getAddViewTitle(), "-1", "-1", getParamMap(),
				ApplicationContext.TYPE_DIALOG, false, false);
	}

	public void RefreshBill(MouseEvent<MenuItem> mouseEvent) {
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	@Override
	protected String getNodecode() {
		return PsnApplyConsts.REGULAR_FUNC_CODE;
	}

	@Override
	protected SuperVO[] queryVOs() {
		return queryRegapplyVOs();
	}

	public static SuperVO[] queryRegapplyVOs() {
		SessionBean session = SessionUtil.getSessionBean();
		String pk_dept = SessionUtil.getPk_mng_dept();
		String condition = "oldpk_dept = '" + pk_dept + "' and " + RegapplyVO.ISHRSSBILL + " = '" + UFBoolean.TRUE
				+ "' and APPROVE_STATE in ('-1','3') ";
		List<SuperVO> list = new ArrayList<SuperVO>();
		try {
			AggRegapplyVO[] aggVOs = ServiceLocator.lookup(IRegmngQueryService.class).queryByCondition(
					session.getContext(), condition);
			if (!ArrayUtils.isEmpty(aggVOs)) {
				for (AggRegapplyVO aggVO : aggVOs) {
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

	@Override
	protected String getPopWindowId() {
		return "ShopRegularApply";
	}

	@Override
	protected String getDatasetId() {
		return PsnApplyConsts.REGULAR_DS_ID;
	}

	@Override
	protected Class<? extends AggregatedValueObject> getAggVOClazz() {
		return PsnApplyConsts.REGULAR_AGGVOCLASS;
	}

	@Override
	protected String getAddViewTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "转正申请新增")/*
																						 * @
																						 * res
																						 * "转正申请新增0c_trn-res0017"
																						 */;
	}

	@Override
	protected String getDetailViewTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "转正申请详细")/*
																						 * @
																						 * res
																						 * "转正申请详细0c_trn-res0018"
																						 */;
	}

	@Override
	protected String getEditViewTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "转正申请修改")/*
																						 * @
																						 * res
																						 * "转正申请修改0c_trn-res0019"
																						 */;
	}

	@Override
	protected String getBillTypeCode() {
		return PsnApplyConsts.REGULAR_BILLTYPE_CODE;
	}

	public void onAfterRowSelect(DatasetEvent datasetEvent) {
		ButtonStateManager.updateButtons();
	}

	/**
	 * 分类切换事件(改：条件查询事件)
	 * 
	 * @param keys
	 * @throws BusinessException 
	 */
	public void plugininParam(Map<String, Object> keys) throws BusinessException {
		try {
			RegapplyVO[] psnJobVOs = null;
			// 获得选择的管理部门
			String pk_mng_dept = SessionUtil.getPk_mng_dept();
			// boolean isIncludeSubDept = SessionUtil.isIncludeSubDept();
			// String psnScopeSqlPart =
			// SessionUtil.getSessionBean().getPsnScopeSqlPart();
			if (StringUtil.isEmptyWithTrim(pk_mng_dept)) {
				return;
			}
			String wheresql = keys.get("whereSql") + " ";
			// psnJobVOs = queryPsndocVOsByDeptPK(pk_mng_dept,wheresql);
			LfwView mainWidget = AppLifeCycleContext.current().getViewContext().getView();
			Dataset ds = mainWidget.getViewModels().getDataset(getDatasetId());
			DatasetUtil.clearData(ds);
			SuperVO[] svos = queryPsndocVOsByDeptPK(pk_mng_dept, wheresql);// DatasetUtil.paginationMethod(ds,
																			// psnJobVOs);
			new SuperVO2DatasetSerializer().serialize(svos, ds, Row.STATE_NORMAL);
		} catch (BusinessException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	public SuperVO[] queryPsndocVOsByDeptPK(String pkDept, String WhereSQL) throws BusinessException {
		String sql = "select * " + WhereSQL + " and oldpk_dept = '" + pkDept + "' and " + RegapplyVO.ISHRSSBILL
				+ " = '" + UFBoolean.TRUE + "' and APPROVE_STATE in ('-1','3')";
		ArrayList<RegapplyVO> al = (ArrayList<RegapplyVO>) new BaseDAO().executeQuery(sql, new BeanListProcessor(
				RegapplyVO.class));
		return al == null ? null : al.toArray(new SuperVO[0]);
	}

}
