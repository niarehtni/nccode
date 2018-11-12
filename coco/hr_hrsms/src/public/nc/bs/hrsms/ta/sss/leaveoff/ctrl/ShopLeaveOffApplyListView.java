package nc.bs.hrsms.ta.sss.leaveoff.ctrl;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrsms.ta.sss.common.ShopTaListBaseView;
import nc.bs.hrsms.ta.sss.leaveoff.ShopLeaveOffConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.hr.utils.ResHelper;
import nc.itf.hrsms.ta.leaveoff.IShopLeaveOffApplyQueryMaintain;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.ta.leaveoff.AggLeaveoffVO;
import nc.vo.ta.leaveoff.LeaveoffVO;
import nc.vo.uif2.LoginContext;

public class ShopLeaveOffApplyListView extends ShopTaListBaseView implements IController {	
	
	public void onDataLoad_dsLeaveOff(DataLoadEvent dataLoadEvent) throws BusinessException {
		super.onDataLoad(dataLoadEvent);
	}

	@Override
	protected String getBillTypeCode() {
		// TODO Auto-generated method stub
		return ShopLeaveOffConsts.BILL_TYPE_CODE;
	}

	@Override
	protected String getDatasetId() {
		// TODO Auto-generated method stub
		return ShopLeaveOffConsts.MAIN_DS_NAME;
	}

	@Override
	protected Class<? extends AggregatedValueObject> getAggVOClazz() {
		// TODO Auto-generated method stub
		return AggLeaveoffVO.class;
	}

	@Override
	protected Class<? extends SuperVO> getMainEntityClazz() {
		// TODO Auto-generated method stub
		return LeaveoffVO.class;
	}

	@Override
	protected AggregatedValueObject[] getAggVOs(FromWhereSQL fromWhereSQL) {
		// TODO Auto-generated method stub
		AggLeaveoffVO[] aggVOs = null;
		// 获得选择的管理部门
		String pk_mng_dept = SessionUtil.getPk_mng_dept();
		// 是否包含下级部门
		boolean isContainSub = SessionUtil.isIncludeSubDept();
		// 获得默认的组织
		String hrOrg = SessionUtil.getPk_org();
		// 获得默认集团
		String hrGroup = SessionUtil.getPk_group();
		try {
			LoginContext context = SessionUtil.getLoginContext();
			context.setPk_group(hrGroup);
			context.setPk_org(hrOrg);
			StringBuffer addCond = new StringBuffer();
//			addCond.append(" and ishrssbill = 'Y' ");
			if(!isContainSub){
				addCond.append(" and pk_psndoc in (select pk_psndoc from bd_psnjob where pk_dept = '"+pk_mng_dept+"')");
			}else{
				HRDeptVO deptVO = (HRDeptVO) new BaseDAO().retrieveByPK(HRDeptVO.class, pk_mng_dept);
				addCond.append(" and pk_psndoc in (select pk_psndoc from bd_psnjob where pk_dept in" +
						"(select dept.pk_dept from org_dept dept where dept.innercode like '%"+deptVO.getInnercode()+"%') )");
			}
			FromWhereSQLImpl fromWhereSQL1 = new FromWhereSQLImpl();
			fromWhereSQL1.setFrom(fromWhereSQL.getFrom());
			fromWhereSQL1.setWhere(fromWhereSQL.getWhere()+addCond.toString());
			IShopLeaveOffApplyQueryMaintain queryServ = ServiceLocator.lookup(IShopLeaveOffApplyQueryMaintain.class);
			aggVOs = queryServ.queryByCondition(context, fromWhereSQL1.getWhere());
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
		return ShopLeaveOffApplyCardWin.WIN_ID;
	}

	@Override
	protected String getPopWindowTitle(String operateflag) {
		// TODO Auto-generated method stub
		if (HrssConsts.POPVIEW_OPERATE_EDIT.equals(operateflag)) {
			return "店员销假修改";
		} else if (HrssConsts.POPVIEW_OPERATE_VIEW.equals(operateflag)) {
			return "店员销假详细";
		}
		return null;
	}

	/**
	 * 新增操作
	 * 
	 * @param mouseEvent
	 * @throws BusinessException 
	 */
	public void addBill(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		CommonUtil.showViewDialog(ShopLeaveRegListView.VIEW_ID,
				ResHelper.getString("c_ta-res", "0c_ta-res0201")/* @res "休假记录选择对话框" */, 800, 380);
	}
	
	/**
	 * 
	 * @param scriptEvent
	 */
	public void reloadData(ScriptEvent scriptEvent) {
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
}
