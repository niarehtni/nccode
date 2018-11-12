package nc.bs.hrsms.hi.employ.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrss.pub.DialogSize;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hrsms.hi.entrymng.IEntrymngManageService;
import nc.itf.hrsms.hi.entrymng.IEntrymngQueryService;
import nc.itf.hrsms.hi.psndoc.manage.IPsndocManageServicePB;
import nc.itf.hrsms.hi.psndoc.qry.IPsndocQryservice;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.hi.entrymng.AggEntryapplyVO;
import nc.vo.hi.entrymng.EntryapplyVO;
import nc.vo.hi.entrymng.HiSendMsgHelper;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.pf.IPfRetCheckInfo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

public class DeptPsnViewContr implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	private static final String DEPT_PSN_DSID = "DsDeptPsn";
	private static final String STATUS = "status";
	private static final String ONLY_INFO_CARD_ID = "onlyinfo";
	private static final String PSN_EMPLOY_CARD_ID = "psn_employ";

	public void onDataLoad(DataLoadEvent dataLoadEvent)
			throws BusinessException, HrssException {
		String wheresql = "1=1";
		loadPsndoc(wheresql);
	}

	/**
	 * 
	 * @param keys
	 * @throws HrssException
	 * @throws BusinessException
	 */
	public void plugininMain(Map<String, Object> keys) throws HrssException,
			BusinessException {
		try {
			String wheresql = "  hi_psnjob.pk_psndoc in (select hi_psnjob.pk_psndoc "
					+ keys.get("whereSql") + ")";
			loadPsndoc(wheresql);
		} catch (BusinessException e) {
			new HrssException(e).alert();
		}
	}

	/**
	 * 加载列表界面
	 * 
	 * @param WhereSql
	 *            sql 条件语句
	 * @throws BusinessException
	 * @throws HrssException
	 */
	private void loadPsndoc(String WhereSql) throws BusinessException,
			HrssException {
		PsnJobVO[] psnJobVOs = null;
		// 获得选择的管理部门
		String pk_mng_dept = SessionUtil.getPk_mng_dept();
		if (StringUtil.isEmptyWithTrim(pk_mng_dept)) {
			return;
		}
		psnJobVOs = queryPsnJobVOsByDeptPK(pk_mng_dept, WhereSql);
		// 去掉多余的工作记录
		psnJobVOs = distinctPsnJobVO(psnJobVOs);
		LfwView mainWidget = AppLifeCycleContext.current().getViewContext()
				.getView();
		Dataset ds = mainWidget.getViewModels().getDataset(DEPT_PSN_DSID);
		DatasetUtil.clearData(ds);
		SuperVO[] svos = DatasetUtil.paginationMethod(ds, psnJobVOs);
		new SuperVO2DatasetSerializer().serialize(svos, ds, Row.STATE_NORMAL);
		Row[] rows = ds.getAllRow();
		for (Row row : rows) {
			AggEntryapplyVO[] vos = ServiceLocator.lookup(
					IEntrymngQueryService.class).queryByCondition(
					null,
					" " + PsnJobVO.PK_PSNJOB + " ='"
							+ row.getValue(ds.nameToIndex(PsnJobVO.PK_PSNJOB))
							+ "'");
			if (vos != null && vos.length > 0) {
				row.setValue(ds.nameToIndex(STATUS), ((EntryapplyVO) vos[0]
						.getParentVO()).getApprove_state());
			}
		}
	}

	/**
	 * 管理部门变更
	 * 
	 * @param keys
	 * @throws BusinessException
	 */
	public void pluginDeptChange(Map<String, Object> keys)
			throws BusinessException {
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	/**
	 * 对于同一个pk_psndoc在psnJobVOs中有多条记录，如果这多条记录都是兼职的，就随机留下一条，其余的去掉；
	 * 如果这多条记录中有一条是主职的，就把主职的留下，其余的去掉。
	 * 
	 * @param psnJobVOs
	 * @return
	 */
	public PsnJobVO[] distinctPsnJobVO(PsnJobVO[] psnJobVOs) {
		if (ArrayUtils.isEmpty(psnJobVOs)) {
			return null;
		}
		Map<String, ArrayList<PsnJobVO>> map = new HashMap<String, ArrayList<PsnJobVO>>();
		List<PsnJobVO> listPsnJobVO = new ArrayList<PsnJobVO>();
		for (PsnJobVO psnJobVO : psnJobVOs) {
			ArrayList<PsnJobVO> list = null;
			list = map.get(psnJobVO.getPk_psndoc());
			if (CollectionUtils.isEmpty(list)) {
				list = new ArrayList<PsnJobVO>();
				list.add(psnJobVO);
				map.put(psnJobVO.getPk_psndoc(), list);
			} else {
				if (psnJobVO.getIsmainjob().booleanValue()) {
					list.clear();
					list.add(psnJobVO);
				}
			}
		}

		for (String pk_psndoc : map.keySet()) {
			listPsnJobVO.addAll(map.get(pk_psndoc));
		}
		return listPsnJobVO.toArray(new PsnJobVO[0]);
	}

	public void addBill(MouseEvent<MenuItem> mouseEvent)
			throws BusinessException {
		AppLifeCycleContext.current().getApplicationContext()
				.addAppAttribute("scj_pkone", null);
		CommonUtil.showViewDialog(ONLY_INFO_CARD_ID, "请输入人员唯一性信息",
				DialogSize.TINY);
	}

	public void DelBill(MouseEvent<MenuItem> mouseEvent)
			throws BusinessException, HrssException {
		LfwView viewMain = ViewUtil.getCurrentView();
		Dataset ds = ViewUtil.getDataset(viewMain, DEPT_PSN_DSID);
		Row row = ds.getSelectedRow();
		if (row == null) {
			CommonUtil.showErrorDialog("提示", "请选择一条记录");
		}
		PsnJobVO vo = new PsnJobVO();
		BeanUtils
				.copyProperties(new Dataset2SuperVOSerializer<PsnJobVO>()
						.serialize(ds, row)[0], vo);
		// 根据pk_psnjob查询PsndocAggVO
		PsndocAggVO aggvo = ServiceLocator.lookup(IPsndocQryService.class)
				.queryPsndocVOByPsnjobPk(vo.getPk_psnjob());

		if (aggvo == null) {
			CommonUtil.showErrorDialog("提示", "人员信息不存在");
		} else if (CommonUtil.showConfirmDialog("确认删除该人员信息吗？")) {
			// 根据pk_psnjob查询PsndocAggVO
			AggEntryapplyVO[] vos = ServiceLocator.lookup(
					IEntrymngQueryService.class).queryByCondition(
					null,
					"(approve_state=-1 or approve_state=0 )and pk_psnjob='"
							+ vo.getPk_psnjob() + "'");
			IEntrymngManageService entryService = ServiceLocator
					.lookup(IEntrymngManageService.class);
			entryService.doDelete(vos);
			if (vos != null) {
				ServiceLocator.lookup(IPsndocManageServicePB.class)
						.deletePsnJob(vo.getPk_psnjob());
				ServiceLocator.lookup(IPsndocManageServicePB.class)
						.deleteNotIndocPsnOrg(vo.getPk_psndoc());
				if (vos != null) {
					CommonUtil.showShortMessage("删除成功！");
				}
			} else {
				CommonUtil.showShortMessage("找不到入职申请单或者入职申请单已提交！");
			}
		}
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	/**
	 * 刷新事件
	 * 
	 * @param mouseEvent
	 * @throws BusinessException
	 */
	public void RefreshBill(MouseEvent<MenuItem> mouseEvent)
			throws BusinessException {
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	/**
	 * 提交事件
	 * 
	 * @param mouseEvent
	 * @throws BusinessException
	 * @throws HrssException
	 */
	public void CommitBill(MouseEvent<MenuItem> mouseEvent)
			throws BusinessException, HrssException {
		LfwView viewMain = ViewUtil.getCurrentView();
		Dataset ds = ViewUtil.getDataset(viewMain, DEPT_PSN_DSID);
		Row row = ds.getSelectedRow();
		PsnJobVO vo = new PsnJobVO();
		if (row == null) {
			CommonUtil.showErrorDialog("提示", "请选择一条记录");
		}
		BeanUtils
				.copyProperties(new Dataset2SuperVOSerializer<PsnJobVO>()
						.serialize(ds, row)[0], vo);

		AggEntryapplyVO[] vos = ServiceLocator.lookup(
				IEntrymngQueryService.class).queryByCondition(null,
				"approve_state =-1 and pk_psnjob='" + vo.getPk_psnjob() + "'");

		if (vos != null) {
			((EntryapplyVO) vos[0].getParentVO())
					.setApprove_state(IPfRetCheckInfo.COMMIT);
			AggEntryapplyVO vo2 = ServiceLocator.lookup(
					IEntrymngManageService.class).updateBill(vos[0], true);
			if (vo2 != null)
				CommonUtil.showShortMessage("入职申请提交成功！");
			String tempCode = HICommonValue.msgcode_entry_approve;// 入职直批通知消息模板源编码
			HiSendMsgHelper.sendMessage1(tempCode, vos, SessionUtil.getHROrg());
		} else {
			CommonUtil.showShortMessage("入职申请提交失败！");
		}

		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	public void UmcimmitBill(MouseEvent<MenuItem> mouseEvent)
			throws BusinessException, HrssException {
		LfwView viewMain = ViewUtil.getCurrentView();
		Dataset ds = ViewUtil.getDataset(viewMain, "DsDeptPsn");
		Row row = ds.getSelectedRow();
		PsnJobVO vo = new PsnJobVO();
		if (row == null) {
			CommonUtil.showErrorDialog("提示", "请选择一条记录");
		}
		BeanUtils
				.copyProperties(new Dataset2SuperVOSerializer<PsnJobVO>()
						.serialize(ds, row)[0], vo);
		// 根据pk_psnjob查询PsndocAggVO
		AggEntryapplyVO[] vos = ServiceLocator.lookup(
				IEntrymngQueryService.class).queryByCondition(null,
				"approve_state=3 and pk_psnjob='" + vo.getPk_psnjob() + "'");

		if (vos != null) {
			((EntryapplyVO) vos[0].getParentVO())
					.setApprove_state(IPfRetCheckInfo.NOSTATE);
			AggEntryapplyVO vo2 = ServiceLocator.lookup(
					IEntrymngManageService.class).updateBill(vos[0], true);
			if (vo2 != null)
				CommonUtil.showShortMessage("入职申请收回成功！");
		} else if (row != null && vos == null) {
			CommonUtil.showErrorDialog("提示", "入职申请未提交！");
		}
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	/**
	 * @param pkDept
	 *            部门pk
	 * @param WhereSQL
	 *            sql 语句
	 * @return
	 * @throws BusinessException
	 */
	public PsnJobVO[] queryPsnJobVOsByDeptPK(String pkDept, String WhereSQL)
			throws BusinessException {
		String sql = "select * from hi_psnjob hi_psnjob inner join hi_psnorg hi_psnorg on hi_psnjob.pk_psndoc = hi_psnorg.pk_psndoc left outer join org_orgs org_orgs  on hi_psnjob.pk_org = org_orgs.pk_org  left outer join org_dept org_dept  on hi_psnjob.pk_dept = org_dept.pk_dept  where  (hi_psnorg.indocflag='N' and hi_psnorg.psntype=0  and hi_psnjob.lastflag='Y'  and hi_psnjob.endflag='N' and hi_psnjob.ismainjob = 'Y' ) and hi_psnjob.pk_org in (select pk_adminorg from org_admin_enable) and hi_psnjob.pk_dept = '"
				+ pkDept + "'";
		if (StringUtils.isNotBlank(WhereSQL))
			sql = sql + " and " + WhereSQL;
		LfwLogger.info(sql);
		PsnJobVO[] al = null;
		try {
			al = ServiceLocator.lookup(IPsndocQryservice.class)
					.queryPsnJobVOsByCondition(sql);
		} catch (HrssException e) {
			e.alert();
		}
		return al;
	}

	/**
	 * @author shaochj
	 * @date Jun 19, 2015
	 * 
	 * @param mouseEvent
	 */
	public void editBill(MouseEvent<MenuItem> mouseEvent) {
		LfwView viewMain = ViewUtil.getCurrentView();
		Dataset ds = ViewUtil.getDataset(viewMain, "DsDeptPsn");
		Row row = ds.getSelectedRow();
		if (row == null) {
			CommonUtil.showErrorDialog("提示", "请选择一条记录");
		}
		String pk = (String) row.getValue(ds.nameToIndex("pk_psndoc"));
		AppLifeCycleContext.current().getApplicationContext()
				.addAppAttribute("scj_pk", pk);
		CommonUtil.showViewDialogNoLine("psn_employ", "店员入职信息修改", "800", "600",
				false, false);
	}

	// add by shaochj Jun 24, 2015 begin
	/**
	 * 行切换事件 处理修改按钮
	 * 
	 * @param datasetEvent
	 */
	public void onAfterRowSelect(DatasetEvent datasetEvent) {
		MenubarComp menus = ViewUtil.getCurrentView().getViewMenus()
				.getMenuBar("menu_list");
		MenuItem menuID = menus.getElementById("edit");//
		menuID.setVisible(false);
	}

	/**
	 * 查看详细操作
	 * 
	 * @param mouseEvent
	 */
	public void showDetail(ScriptEvent scriptEvent) {
		// 主键
		String primaryKey = AppLifeCycleContext.current().getParameter(
				"dsMain_primaryKey");
		String status = AppLifeCycleContext.current().getParameter("status");
		AppLifeCycleContext.current().getApplicationContext()
				.addAppAttribute("scj_pk", primaryKey);
		AppLifeCycleContext.current().getApplicationContext()
				.addAppAttribute("scj_status", status);
		CommonUtil.showViewDialogNoLine(PSN_EMPLOY_CARD_ID, "店员入职信息修改", "800",
				"600", false, false);
	}
}
