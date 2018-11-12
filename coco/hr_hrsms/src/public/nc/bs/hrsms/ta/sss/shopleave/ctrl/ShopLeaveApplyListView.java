package nc.bs.hrsms.ta.sss.shopleave.ctrl;

import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.hrsms.ta.sss.common.ShopTaListBaseView;
import nc.bs.hrsms.ta.sss.shopleave.ShopLeaveApplyConsts;
import nc.bs.hrsms.ta.sss.shopleave.lsnr.ShopLeaveCommitProcessor;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.itf.hrss.pub.cmd.prcss.ICommitProcessor;
import nc.itf.ta.ILeaveApplyQueryMaintain;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.uif2.LoginContext;
/**
 * ��Ա�ݼ�����View
 * @author shaochj
 * @date May 8, 2015
 */
public class ShopLeaveApplyListView extends ShopTaListBaseView{

	@Override
	protected String getBillTypeCode() {
		// TODO Auto-generated method stub
		return ShopLeaveApplyConsts.BILL_TYPE_CODE;
	}

	@Override
	protected String getDatasetId() {
		// TODO Auto-generated method stub
		return ShopLeaveApplyConsts.DS_MAIN_NAME;
	}

	@Override
	protected Class<? extends AggregatedValueObject> getAggVOClazz() {
		// TODO Auto-generated method stub
		return ShopLeaveApplyConsts.CLASS_NAME_AGGVO;
	}

	@Override
	protected Class<? extends SuperVO> getMainEntityClazz() {
		// TODO Auto-generated method stub
		return LeavehVO.class;
	}

	@Override
	protected AggregatedValueObject[] getAggVOs(FromWhereSQL fromWhereSQL) {
		// TODO Auto-generated method stub
		AggLeaveVO[] aggVOs = null;
		// ���ѡ��Ĺ�����
		String pk_mng_dept = SessionUtil.getPk_mng_dept();
		// �Ƿ�����¼�����
		boolean isContainSub = SessionUtil.isIncludeSubDept();
		// ���Ĭ�ϵ���֯
		String hrOrg = SessionUtil.getPk_org();
		// ���Ĭ�ϼ���
		String hrGroup = SessionUtil.getPk_group();
		Dataset mainds = getLifeCycleContext().getWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView().getViewModels().getDataset("mainds");
		Row selRow = mainds.getSelectedRow();
		if (selRow == null) {
			selRow = DatasetUtil.initWithEmptyRow(mainds, true, Row.STATE_NORMAL);
		}
		UFLiteralDate  begindate = (UFLiteralDate ) selRow.getValue(mainds.nameToIndex("leaveb_sub_leavebegindate"));
		UFLiteralDate  enddate = (UFLiteralDate ) selRow.getValue(mainds.nameToIndex("leaveb_sub_leaveenddate"));
		String islactation = (String)selRow.getValue(mainds.nameToIndex("islactation"));
		String where = fromWhereSQL.getWhere();
		String sql = null;
		if(null != begindate){
			String[] wheres = where.split("leaveb_sub_leavebegindate");
			sql = wheres[0] + " and pk_leaveh in ( select pk_leaveh from tbm_leaveb where leavebegindate>='" + begindate + "')";
		}
		if(null != enddate){
			String[] wheres = where.split("leaveb_sub_leaveenddate");
			if(null != sql){
				sql += " and pk_leaveh in ( select pk_leaveh from tbm_leaveb where leaveenddate<='" + enddate + "')";
			}else {
				sql = wheres[0] + " and pk_leaveh in ( select pk_leaveh from tbm_leaveb where leaveenddate<='" + enddate + "')";
			}
		}
		if(null != islactation && !islactation.isEmpty() && null != sql){
			sql += " and islactation = '" + islactation + "'";
		}
		if(null == sql){
			sql = where;
		}
		try {
			LoginContext context = SessionUtil.getLoginContext();
			context.setPk_group(hrGroup);
			context.setPk_org(hrOrg);
			StringBuffer addCond = new StringBuffer();
			addCond.append(" and ishrssbill = 'Y' ");
			if(!isContainSub){
				addCond.append(" and pk_psndoc in (select pk_psndoc from bd_psnjob where pk_dept = '"+pk_mng_dept+"')");
			}else{
				HRDeptVO deptVO = (HRDeptVO) new BaseDAO().retrieveByPK(HRDeptVO.class, pk_mng_dept);
				addCond.append(" and pk_psndoc in (select pk_psndoc from bd_psnjob where pk_dept in" +
						"(select dept.pk_dept from org_dept dept where dept.innercode like '%"+deptVO.getInnercode()+"%') )");
			}
			addCond.append(" and  pk_dept_v in (select pk_vid from org_dept where pk_dept='"+ pk_mng_dept +"')");
			FromWhereSQLImpl fromWhereSQL1 = new FromWhereSQLImpl();
			fromWhereSQL1.setFrom(fromWhereSQL.getFrom());
			fromWhereSQL1.setWhere(sql+addCond.toString());
			ILeaveApplyQueryMaintain queryServ = ServiceLocator.lookup(ILeaveApplyQueryMaintain.class);
//			aggVOs = queryServ.queryByPsndoc(null, fromWhereSQL, getEtraConds());queryByCond
			aggVOs = queryServ.queryByWhereSQL(context, fromWhereSQL1.getWhere());
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return aggVOs;
	}

	@Override
	protected String getPopWindowId() {
		// TODO Auto-generated method stub
		return "ShopLeaveApplytCard";
	}

	@Override
	protected String getPopWindowTitle(String operateflag) {
		// TODO Auto-generated method stub
		if (HrssConsts.POPVIEW_OPERATE_ADD.equals(operateflag)) {
			return "��Ա�ݼ���������";
		} else if (HrssConsts.POPVIEW_OPERATE_EDIT.equals(operateflag)) {
			return "��Ա�ݼ������޸�";
		} else if (HrssConsts.POPVIEW_OPERATE_VIEW.equals(operateflag)) {
			return "��Ա�ݼ�������ϸ";
		}
		return null;
	}

	public void onDataLoad_hrtaleave(DataLoadEvent dataLoadEvent) throws BusinessException {
		super.onDataLoad(dataLoadEvent);
	}
	
	/**
	 * �ύ�Ĳ�����
	 */
	@Override
	protected Class<? extends ICommitProcessor> getCommitPrcss() {
		return ShopLeaveCommitProcessor.class;
	}
	
	/**
	 * ���������
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void addLactation(MouseEvent<MenuItem> mouseEvent) throws BusinessException{
		String operate_status = HrssConsts.POPVIEW_OPERATE_ADD;
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, null);
		getApplicationContext().addAppAttribute("islactation", UFBoolean.TRUE);
		CommonUtil.showWindowDialog("ShopLactationApplyCard", "��Ա���������", "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
	}
	/**
	 * ��������ٱ���֮�󷵻�ˢ��Listҳ��
	 * @param keys
	 */
	public void pluginReLaSearch(Map<String, Object> keys) {
		// ִ������ݲ�ѯ
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	/**
	 * 
	 * @param scriptEvent
	 */
	public void reloadData(ScriptEvent scriptEvent) {
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	/**
	 * �鿴��ϸ����
	 * 
	 * @param mouseEvent
	 */
	@Override
	public void showDetail(ScriptEvent scriptEvent) {
		// ����
		String primaryKey = AppLifeCycleContext.current().getParameter("dsMain_primaryKey");
		String operate_status = HrssConsts.POPVIEW_OPERATE_VIEW;
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_PARAM, primaryKey);
		checkAggVO(primaryKey);
		try {
			AggLeaveVO aggVO = getAggVOByPk(primaryKey);
			LeavehVO leavehVO = (LeavehVO) aggVO.getParentVO();
			if(leavehVO.getIslactation().booleanValue()){
				CommonUtil.showWindowDialog("ShopLactationApplyCard", "��Ա�����������ϸ", "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
			}else{
				CommonUtil.showWindowDialog("ShopLeaveApplytCard", "��Ա�ݼ�������ϸ", "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
			}
		} catch (HrssException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public AggLeaveVO getAggVOByPk(String pk) throws HrssException{
		AggLeaveVO aggVO = null;
		ILeaveApplyQueryMaintain service = ServiceLocator.lookup(ILeaveApplyQueryMaintain.class);
		try {
			aggVO = service.queryByPk(pk);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		return aggVO;
	}
}
