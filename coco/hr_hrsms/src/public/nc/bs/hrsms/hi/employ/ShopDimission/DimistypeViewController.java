package nc.bs.hrsms.hi.employ.ShopDimission;

import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.page.ViewComponents;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.vo.pub.SuperVO;
import nc.uap.lfw.core.ctrl.IController;
import nc.vo.pub.BusinessException;
import nc.uap.lfw.core.ctx.WindowContext;
import nc.bs.logging.Logger;
import nc.vo.pub.lang.UFBoolean;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.vo.trn.pub.TRNConst;
import nc.bs.hrss.pub.exception.HrssException;
import nc.uap.lfw.core.comp.text.ComboBoxComp;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.trn.PsnApplyConsts;
import nc.bs.hrss.trn.TrnUtil;

import org.apache.commons.lang.StringUtils;
import nc.uap.lfw.core.event.MouseEvent;

public class DimistypeViewController implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	public void onConfirm(MouseEvent<MenuItem> mouseEvent) {
		ViewComponents viewComponents = AppLifeCycleContext.current().getViewContext().getView().getViewComponents();
		ComboBoxComp transComb = (ComboBoxComp) viewComponents.getComponent(PsnApplyConsts.TRANSTYPE_COMB);
		String transtype = transComb.getValue();
		if (StringUtils.isEmpty(transtype)) {
			CommonUtil.showErrorDialog(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0017")/*
																									 * @
																									 * res
																									 * "提示信息"
																									 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "请选择调配业务类型")/*
																								 * @
																								 * res
																								 * "请选择调配业务类型0c_trn-res0007"
																								 */);
		}
		try {
			SuperVO[] itemvos = TrnUtil.getTrnItems(TRNConst.TRNSITEM_BEANID, transtype, SessionUtil.getPk_group(),
					SessionUtil.getPk_org());
			if (itemvos.length == 0) {
				CommonUtil.showErrorDialog(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0017")/*
																										 * @
																										 * res
																										 * "提示信息"
																										 */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "未设置离职项目，不能填写离职申请单！")/*
																												 * @
																												 * res
																												 * "未设置离职项目，不能填写离职申请单！0c_trn-res0008"
																												 */);
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		} catch (HrssException e) {
			Logger.error(e.getMessage(), e);
		}
		WindowContext windowContext = AppLifeCycleContext.current().getWindowContext();
		// 不询问是否关闭窗口，直接强制关闭
		LfwRuntimeEnvironment.getWebContext().getPageMeta().setHasChanged(false);
		windowContext.closeView(PsnApplyConsts.TRANSTYPE_VIEW_ID);
		ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();
		appCtx.addAppAttribute(PsnApplyConsts.ISCHANGEPSNORG_CHECK, UFBoolean.valueOf(Boolean.FALSE));
		appCtx.addAppAttribute(PsnApplyConsts.TRANSTYPE_COMB, transtype);
		CmdInvoker.invoke(new UifPlugoutCmd("transtype", "outOpenMain"));
	}
}
