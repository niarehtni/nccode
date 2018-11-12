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
	 * �����ݼ�ID
	 *
	 * @return
	 */
	@Override
	protected String getDatasetId() {
		return ShopAwayApplyConsts.DS_MAIN_NAME;
	}

	/**
	 * �ۺ�VO
	 */
	@Override
	protected Class<? extends AggregatedValueObject> getAggVOClazz() {
		return ShopAwayApplyConsts.CLASS_NAME_AGGVO;
	}

	/**
	 * ��ѯ��������ʵ��Class
	 *
	 * @return
	 */
	@Override
	protected Class<? extends SuperVO> getMainEntityClazz() {
		return AwayhVO.class;
	}

	/**
	 * ����ҳ��ID
	 *
	 * @return
	 */
	@Override
	protected String getPopWindowId() {
		return ShopAwayApplyConsts.WIN_CARD_NAME;
	}

	/**
	 * ����ҳ�����
	 *
	 * @return
	 */
	@Override
	protected String getPopWindowTitle(String operateflag) {
		if (HrssConsts.POPVIEW_OPERATE_ADD.equals(operateflag)) {
			return "��Ա������������";
		} else if (HrssConsts.POPVIEW_OPERATE_EDIT.equals(operateflag)) {
			return "��Ա���������޸�";
		} else if (HrssConsts.POPVIEW_OPERATE_VIEW.equals(operateflag)) {
			return "��Ա����������ϸ";
		}
		return null;
	}

	/**
	 * ���ݼ������¼�
	 *
	 * @param dataLoadEvent
	 * @throws BusinessException 
	 */
	public void onDataLoad_hrtaawayh(DataLoadEvent dataLoadEvent) throws BusinessException {
		super.onDataLoad(dataLoadEvent);
	}

	/**
	 * ��ѯ���
	 *
	 * @return
	 */
	@Override
	protected AggregatedValueObject[] getAggVOs(FromWhereSQL fromWhereSQL) {
		AggAwayVO[] aggVOs = null;
		// ���ѡ��Ĺ�����
		String pk_mng_dept = SessionUtil.getPk_mng_dept();
//		// �Ƿ�����¼�����
		boolean isContainSub = SessionUtil.isIncludeSubDept();
//		// ���Ĭ�ϵ���֯
		String hrOrg = SessionUtil.getPk_org();
//		// ���Ĭ�ϼ���
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
	 * �ύ�Ĳ�����
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
