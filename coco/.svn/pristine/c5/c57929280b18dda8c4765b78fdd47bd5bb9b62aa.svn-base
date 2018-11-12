package nc.bs.hrsms.ta.sss.away.ctrl;

import nc.bs.dao.BaseDAO;
import nc.bs.hrsms.ta.sss.away.ShopAwayApplyConsts;
import nc.bs.hrsms.ta.sss.common.ShopTaListBaseView;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.itf.hrss.pub.cmd.prcss.ICommitProcessor;
import nc.itf.ta.IAwayApplyQueryMaintain;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.away.AggAwayVO;
import nc.vo.ta.away.AwayhVO;
import nc.vo.uif2.LoginContext;

public class ShopAwayApplyListView extends ShopTaListBaseView implements IController{

	@Override
	protected String getBillTypeCode() {
		return ShopAwayApplyConsts.BILL_TYPE_CODE;
	}

	/**
	 * 主数据集ID
	 *
	 * @return
	 */
	@Override
	protected String getDatasetId() {
		return ShopAwayApplyConsts.DS_MAIN_NAME;
	}

	/**
	 * 聚合VO
	 */
	@Override
	protected Class<? extends AggregatedValueObject> getAggVOClazz() {
		return ShopAwayApplyConsts.CLASS_NAME_AGGVO;
	}

	/**
	 * 查询条件的主实体Class
	 *
	 * @return
	 */
	@Override
	protected Class<? extends SuperVO> getMainEntityClazz() {
		return AwayhVO.class;
	}

	/**
	 * 弹出页面ID
	 *
	 * @return
	 */
	@Override
	protected String getPopWindowId() {
		return ShopAwayApplyConsts.WIN_CARD_NAME;
	}

	/**
	 * 弹出页面标题
	 *
	 * @return
	 */
	@Override
	protected String getPopWindowTitle(String operateflag) {
		if (HrssConsts.POPVIEW_OPERATE_ADD.equals(operateflag)) {
			return "店员出差申请新增";
		} else if (HrssConsts.POPVIEW_OPERATE_EDIT.equals(operateflag)) {
			return "店员出差申请修改";
		} else if (HrssConsts.POPVIEW_OPERATE_VIEW.equals(operateflag)) {
			return "店员出差申请详细";
		}
		return null;
	}

	/**
	 * 数据集加载事件
	 *
	 * @param dataLoadEvent
	 * @throws BusinessException 
	 */
	public void onDataLoad_hrtaawayh(DataLoadEvent dataLoadEvent) throws BusinessException {
		super.onDataLoad(dataLoadEvent);
	}

	/**
	 * 查询结果
	 *
	 * @return
	 */
	@Override
	protected AggregatedValueObject[] getAggVOs(FromWhereSQL fromWhereSQL) {
		AggAwayVO[] aggVOs = null;
		// 获得选择的管理部门
		String pk_mng_dept = SessionUtil.getPk_mng_dept();
//		// 是否包含下级部门
		boolean isContainSub = SessionUtil.isIncludeSubDept();
//		// 获得默认的组织
		String hrOrg = SessionUtil.getPk_org();
//		// 获得默认集团
		String hrGroup = SessionUtil.getPk_group();
		Dataset mainds = getLifeCycleContext().getWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView().getViewModels().getDataset("mainds");
		Row selRow = mainds.getSelectedRow();
		if (selRow == null) {
			selRow = DatasetUtil.initWithEmptyRow(mainds, true, Row.STATE_NORMAL);
		}
		UFLiteralDate  begindate = (UFLiteralDate ) selRow.getValue(mainds.nameToIndex("away_sub_awaybegindate"));
		UFLiteralDate  enddate = (UFLiteralDate ) selRow.getValue(mainds.nameToIndex("away_sub_awayenddate"));
		String where = fromWhereSQL.getWhere();
		String sql = where;
		if(null != begindate){
			String[] wheres = where.split("away_sub_awaybegindate");
			sql = wheres[0] + " and pk_awayh in ( select pk_awayh from tbm_awayb where awaybegindate>='" + begindate + "')";
		}
		if(null != enddate){
			String[] wheres = where.split("away_sub_awayenddate");
			if(null != sql){
				sql += " and pk_awayh in ( select pk_awayh from tbm_awayb where awayenddate<='" + enddate + "')";
			}else {
				sql = wheres[0] + " and pk_awayh in ( select pk_awayh from tbm_awayb where awayenddate<='" + enddate + "')";
			}
		}
		try {
			LoginContext context = SessionUtil.getLoginContext();
			context.setPk_group(hrGroup);
			context.setPk_org(hrOrg);
			StringBuffer addCond = new StringBuffer();
			addCond.append(" and tbm_awayh.ishrssbill = 'Y' ");
			if(!isContainSub){
				addCond.append(" and tbm_awayh.pk_psndoc in (select pk_psndoc from bd_psnjob where pk_dept = '"+pk_mng_dept+"')");
			}else{
				HRDeptVO deptVO = (HRDeptVO) new BaseDAO().retrieveByPK(HRDeptVO.class, pk_mng_dept);
				addCond.append(" and tbm_awayh.pk_psndoc in (select pk_psndoc from bd_psnjob where pk_dept in" +
						"(select dept.pk_dept from org_dept dept where dept.innercode like '%"+deptVO.getInnercode()+"%') )");
			}
			addCond.append(" and  pk_dept_v in (select pk_vid from org_dept where pk_dept='"+ pk_mng_dept +"')");
			FromWhereSQLImpl fromWhereSQL1 = new FromWhereSQLImpl();
			fromWhereSQL1.setFrom(fromWhereSQL.getFrom());
			fromWhereSQL1.setWhere(sql+addCond.toString());
			IAwayApplyQueryMaintain queryServ = ServiceLocator.lookup(IAwayApplyQueryMaintain.class);
			aggVOs = queryServ.queryByCondition(context, fromWhereSQL1.getWhere());
	
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return aggVOs;
	}

	/**
	 * 提交的操作类
	 */
	@Override
	protected Class<? extends ICommitProcessor> getCommitPrcss() {
		return null;
	}

	/**
	 * 
	 * @param scriptEvent
	 */
	public void reloadData(ScriptEvent scriptEvent) {
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
}
