package nc.bs.hrsms.ta.sss.leaveoff.prcss;

import java.util.Map;

import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.ta.away.lsnr.AwaySaveProcessor;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrsms.ta.sss.leaveoff.ctrl.ShopLeaveOffApplyCardWin;
import nc.hr.utils.ResHelper;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.itf.ta.ILeaveOffApplyQueryMaintain;
import nc.itf.ta.ILeaveOffManageMaintain;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.leaveoff.AggLeaveoffVO;

import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

public class ShopLeaveOffSaveProcessor implements ISaveProcessor {

	@Override
	public void onBeforeVOSave(AggregatedValueObject aggVO) throws Exception {

	}

	@Override
	public boolean checkBeforeVOSave(AggregatedValueObject aggVO)
			throws Exception {
		
		// 获得是否继续标志
		String confirmFlag = AppLifeCycleContext.current().getParameter("isContinue");
		if (StringUtils.isEmpty(confirmFlag)) {
			
			ILeaveOffApplyQueryMaintain service = null;
			try {
				service = ServiceLocator.lookup(ILeaveOffApplyQueryMaintain.class);
			} catch (HrssException e) {
				e.alert();
			}
			// 保存前先校验一次，如果有单据冲突，但是是被允许的，则显示这些冲突单据，并询问用户是否保存
			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkResult = null;
			try {
				checkResult = service.checkMutextWhenSave((AggLeaveoffVO) aggVO);
			} catch (BillMutexException e) {
				AwaySaveProcessor.showConflictInfoList(e, 
						ResHelper.getString("c_ta-res", "0c_ta-res0007")/* @ res "与下列单据有时间冲突，操作不能继续"*/, 
						ShopTaApplyConsts.DIALOG_ALERT);
				throw new BillMutexException();
			}
			if (checkResult != null) {
				AwaySaveProcessor.showConflictInfoList(new BillMutexException(null, checkResult), 
						ResHelper.getString("c_ta-res", "0c_ta-res0008")/* @res "与下列单据有时间冲突，是否保存?" */,
						ShopTaApplyConsts.DIALOG_CONFIRM);
				throw new BillMutexException();
			}
		}
		return true;
	}

	@Override
	public AggregatedValueObject onVOSave(AggregatedValueObject aggVO)
			throws Exception {
		
		AggLeaveoffVO newAggVo = null;
		AggLeaveoffVO aggLeaveoffVO = (AggLeaveoffVO) aggVO;
		String primaryKey = aggLeaveoffVO.getParentVO().getPrimaryKey();

		// 申请审批节点的管理操作接口
		ILeaveOffManageMaintain service = null;
		try {
			service = ServiceLocator.lookup(ILeaveOffManageMaintain.class);
			if (StringUtils.isEmpty(primaryKey)) {
				newAggVo = service.insertData(aggLeaveoffVO);
			} else {
				newAggVo = service.updateData(aggLeaveoffVO);
			}
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			new HrssException(e).alert();
		}
		return newAggVo;
	}

	@Override
	public AggregatedValueObject onAfterVOSave(Dataset ds, Dataset[] dsDetails,
			AggregatedValueObject aggVO) throws Exception {
		// 关闭弹出页面
		AppUtil.getCntAppCtx().closeWinDialog(ShopLeaveOffApplyCardWin.WIN_ID);
		// 执行左侧快捷查询
		CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET, "closewindow"));
		return null;
	}


}
