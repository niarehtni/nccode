package nc.bs.hrsms.hi.employ.ShopTransfer;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.page.ViewComponents;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.vo.pub.SuperVO;
import org.apache.commons.lang.ArrayUtils;
import nc.uap.lfw.core.ctrl.IController;
import nc.vo.pub.BusinessException;
import nc.uap.lfw.core.ctx.WindowContext;
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
public class TranstypeViewController implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID=1L;
	public void onConfirm(  MouseEvent<MenuItem> mouseEvent){
		ViewComponents viewComponents = AppLifeCycleContext.current().getViewContext().getView().getViewComponents();
		ComboBoxComp transComb = (ComboBoxComp)viewComponents.getComponent(PsnApplyConsts.TRANSTYPE_COMB);
		String transtype = transComb.getValue();
		if(StringUtils.isEmpty(transtype)){
			CommonUtil. showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","提示信息")/*@res "提示信息0c_hi-res0017"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","请选择调配类型")/*@res "请选择调配业务类型0c_trn-res0007"*/);
		}
		try {
			SuperVO[] itemvos = TrnUtil.getTrnItems(TRNConst.TRNSITEM_BEANID, transtype, SessionUtil.getPk_group(), SessionUtil.getPk_org());
			if(ArrayUtils.isEmpty(itemvos)){
				CommonUtil. showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_hi-res0017")/*@res "提示信息"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_trn-res0034")/*@res "未设置调配项目，不能填写调配申请单！"*/);
			}
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			new HrssException(e).alert();
		}
		WindowContext windowContext = AppLifeCycleContext.current().getWindowContext();
		windowContext.closeView(PsnApplyConsts.TRANSTYPE_VIEW_ID); // 关闭当前窗口
		ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();
		appCtx.addAppAttribute(PsnApplyConsts.STAPPLY_MODE_COMB, Integer.parseInt("1"));
		appCtx.addAppAttribute(PsnApplyConsts.TRANSTYPE_COMB, transtype);
		CmdInvoker.invoke(new UifPlugoutCmd("transtype", "outOpenMain"));
	}
}
