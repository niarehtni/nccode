package nc.bs.hrsms.ta.sss.leaveinfo.ctrl;

import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.itf.ta.ILeaveQueryService;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.pub.BusinessException;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leavebalance.LeaveBalanceVO;

/**
 * 休假详情
 * 
 * @author qiaoxp
 */
public class ViewLeaveDetailViewMain implements IController {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	// 假期记录的选中行数据
	public static final String SESSION_SELECTED_DATAS = "sess_leavedetail_selected_datas";

	

	/**
	 * 数据集加载
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad_dsLeaveDetail(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		ApplicationContext appCtx = getLifeCycleContext().getApplicationContext();
		LeaveBalanceVO vo = (LeaveBalanceVO) appCtx.getAppAttribute(SESSION_SELECTED_DATAS);
		if (vo == null) {
			return;
		}
		LeaveRegVO[] vos = getLeaveRegVOs(vo.getPk_org(), vo.getPk_psnorg(), vo.getPk_timeitem(), vo.getCuryear(), vo.getCurmonth(), vo.getLeaveindex());
		if (vos != null && vos.length > 0) {
			ds.setVoMeta(LeaveRegVO.class.getName());
			new SuperVO2DatasetSerializer().serialize(vos, ds, Row.STATE_NORMAL);
			ds.setRowSelectIndex(0);
		}
	}

	/**
	 * 根据人员、休假类别、年度(期间)查询有效的休假单据.<br/>
	 * 用于假期计算的休假信息浏览.<br/>
	 * 
	 * @param pk_org
	 * @param pk_psnorg
	 * @param pk_leaveType
	 * @param year
	 * @param month
	 * @param leaveIndex
	 * @return
	 */
	private LeaveRegVO[] getLeaveRegVOs(String pk_org, String pk_psnorg, String pk_leaveType, String year, String month, int leaveIndex) {
		LeaveRegVO[] regVOs = null;
		try {
			ILeaveQueryService servic = ServiceLocator.lookup(ILeaveQueryService.class);
			regVOs = servic.queryByPsnLeaveTypePeriod(pk_org, pk_psnorg, pk_leaveType, year, month, leaveIndex);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return regVOs;
	}

	protected AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}
}