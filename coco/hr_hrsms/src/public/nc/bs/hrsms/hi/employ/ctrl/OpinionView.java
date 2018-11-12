package nc.bs.hrsms.hi.employ.ctrl;

import java.util.Map;

import nc.bs.hrss.hi.psninfo.PsninfoConsts;
import nc.bs.hrss.hi.psninfo.PsninfoUtil;
import nc.bs.hrss.pub.DialogSize;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.logging.Logger;
import nc.itf.hrss.hi.setalter.ISetalterService;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.WindowContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.hrss.hi.setalter.HrssSetalterVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

public class OpinionView implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	public void pluginOpinionView(Map<String, Object> keys) {
		DialogSize size = DialogSize.SMALL;

		CommonUtil.showViewDialog("Opinion", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_hi-res0002")/*@res "…Û∫À“‚º˚"*/, size.getWidth(), 300);
	}
	

	
	
	
	
	public void beforeShow(DialogEvent dialogEvent) {
		SessionBean session = SessionUtil.getSessionBean();
		WindowContext windowContext = AppLifeCycleContext.current().getWindowContext();
		String dataset = (String) windowContext.getAppAttribute(PsninfoConsts.CURR_DATASET);
		String pk_infoset = null;
		try {
			pk_infoset = PsninfoUtil.getInfosetPKByCode(dataset);
			session.setExtendAttribute("pk_infoset", pk_infoset);
			Dataset ds = AppLifeCycleContext.current().getViewContext().getView().getViewModels().getDataset("OpinionDS");
			String pk_psndoc =(String)CommonUtil.getCacheValue("pk_psndoc");
			HrssSetalterVO vo = ServiceLocator.lookup(ISetalterService.class).queryNewAuditOpi(pk_psndoc, pk_infoset);
			new SuperVO2DatasetSerializer().serialize(
					vo != null ? new SuperVO[] { vo } : new SuperVO[0], ds, Row.STATE_NORMAL);
			ds.setRowSelectIndex(0);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		} catch (HrssException e) {
			Logger.error(e.getMessage(), e);
		}
	}
	
}