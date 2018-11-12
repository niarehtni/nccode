package nc.bs.hrsms.hi.employ.ShopDimission.lsnr;

import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.itf.trn.transmng.ITransmngManageService;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

public class ShopDimissionApplySaveProcessor implements ISaveProcessor {

	/**
	 * 保存前操作
	 * 
	 * @param aggVo
	 * @return
	 */
	@Override
	public void onBeforeVOSave(AggregatedValueObject aggVO) {

	}

	/**
	 * 保存前操作
	 * 
	 * @param aggVo
	 * @return
	 */
	@Override
	public boolean checkBeforeVOSave(AggregatedValueObject aggVO) throws Exception {
		return true;
	}

	/**
	 * 保存操作
	 * 
	 * @param aggVo
	 * @return
	 */
	@Override
	public AggregatedValueObject onVOSave(AggregatedValueObject aggVO) {
		// 申请审批节点的管理操作接口
		ITransmngManageService service = null;
		try {
			String primaryKey = aggVO.getParentVO().getPrimaryKey();
			service = ServiceLocator.lookup(ITransmngManageService.class);
			if (StringUtil.isEmptyWithTrim(primaryKey)) {
				aggVO = service.insertBill(aggVO);
			} else {
				aggVO = service.updateBill(aggVO, true);
			}
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			new HrssException(e).alert();
		}
		return aggVO;
	}

	/**
	 * 保存后操作
	 */
	@Override
	public AggregatedValueObject onAfterVOSave(Dataset ds, Dataset[] dsDetails, AggregatedValueObject aggVO) {
		ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();
		appCtx.removeAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
		// 关闭弹出页面
		CmdInvoker.invoke(new CloseWindowCmd());
		// 执行左侧快捷查询
		CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET, "closewindow"));
		return null;
	}

}
