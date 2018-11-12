package nc.bs.hrsms.hi.employ.state;

import java.util.HashMap;

import nc.bs.hrss.hi.psninfo.PsninfoConsts;
import nc.bs.hrss.hi.psninfo.PsninfoUtil;
import nc.bs.hrss.pub.exception.HrssException;
import nc.uap.lfw.core.bm.dft.AbstractStateManager;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.WindowContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.hrss.hi.setalter.HrssSetalterVO;
import nc.vo.pub.BusinessException;
/**
 * 我的档案菜单状态基类
 * 1、部门人员模块查询人员详细信息时，菜单全部隐藏
 * 2、数据集为任职记录、兼职记录、汇报关系时，菜单全部隐藏
 * @author lihha
 */
public abstract class Psninfo_Base_StateManager extends AbstractStateManager{
	@Override
	protected Dataset getCtrlDataset(LfwView widget) {
		return super.getCtrlDataset(widget);
	}
	
	@Override
	public State getState(WebComponent target, LfwView view) {
		WindowContext windowContext = AppLifeCycleContext.current().getWindowContext();
		if(windowContext==null)
			return State.HIDDEN;
//		if(PsninfoUtil.isDeptPsnNode())
//			return State.HIDDEN;
		String infoset = PsninfoUtil.getCurrDataset();
		if (PsninfoUtil.isBusinessSet(infoset)) {
			return State.HIDDEN;
		}
		boolean needAudit = true; // 是否需要审核
		int data_status = 0;      // 审核状态
		try {
			String pk_infoset = PsninfoUtil.getInfosetPKByCode(infoset);
			needAudit = PsninfoUtil.isNeedAudit(infoset);
			if(needAudit){
				@SuppressWarnings("unchecked")
				HashMap<String, HrssSetalterVO> setsVOs = (HashMap<String, HrssSetalterVO>) windowContext.getAppAttribute(PsninfoConsts.SET_ALTER_MAP);
				if(setsVOs!=null&&setsVOs.get(pk_infoset)!=null){
					data_status = setsVOs.get(pk_infoset).getData_status();
				}
			}
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return getStateByItem(infoset, needAudit, data_status);
	}
	
	protected State getStateByItem(String infoset, boolean needAudit, int data_status) {
		return null;
	}
}
