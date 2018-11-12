package nc.bs.hrsms.hi.employ.ShopTransfer.lsnr;

import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.trn.PsnApplyConsts;
import nc.bs.hrss.trn.TrnUtil;
import nc.bs.hrss.trn.lsnr.TrnBaseAddProcessor;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.pub.BusinessException;
import nc.vo.trn.pub.TRNConst;
import nc.vo.trn.transmng.StapplyVO;

public class TransferApplyAddProcessor extends TrnBaseAddProcessor{
	@Override
	public void onBeforeRowAdd(Dataset ds, Row row, String billTypeCode) {
		ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();
		int stapply_mode = (Integer) appCtx.getAppAttribute(PsnApplyConsts.STAPPLY_MODE_COMB);
		String transtype = (String) appCtx.getAppAttribute(PsnApplyConsts.TRANSTYPE_COMB);
		// 调配方式
		row.setValue(ds.nameToIndex(StapplyVO.STAPPLY_MODE), stapply_mode);
		// 业务类型
		row.setValue(ds.nameToIndex(StapplyVO.PK_TRNSTYPE), transtype);
		// 设置调配前人事组织 add by lihy5 11/7/7
		SessionBean session = SessionUtil.getSessionBean();
		PsndocVO psndocVO = session.getPsndocVO();
		PsnJobVO psnJobVO = psndocVO.getPsnJobVO();
		row.setValue(ds.nameToIndex(StapplyVO.PK_OLD_HI_ORG), psnJobVO.getPk_hrorg());
		if (stapply_mode==TRNConst.TRANSMODE_INNER) {
			row.setValue(ds.nameToIndex(StapplyVO.PK_HI_ORG),psnJobVO.getPk_hrorg());
		}
		super.onBeforeRowAdd(ds, row, billTypeCode);
	}
	
	@Override
	public void onAfterRowAdd(Dataset ds, Row row) {
		super.onAfterRowAdd(ds, row);
		SessionBean session = SessionUtil.getSessionBean();
		PsndocVO psndocVO = session.getPsndocVO();
		String dsId = PsnApplyConsts.TRANSFER_DS_ID;
		ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();
		int stapply_mode = (Integer)appCtx.getAppAttribute(PsnApplyConsts.STAPPLY_MODE_COMB);
		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		try {
			// 设置调配后人事组织不可用
			FormComp frmPsnInfo = (FormComp) widget.getViewComponents().getComponent(PsnApplyConsts.TRANSFER_FORM_NEWORGS);
			frmPsnInfo.getElementById(StapplyVO.PK_HI_ORG + PsnApplyConsts.REF_SUFFIX).setEnabled(stapply_mode!=TRNConst.TRANSMODE_INNER);
			// 设置异动项目默认值
			TrnUtil.setPersonInfo(dsId, PsnApplyConsts.REGULAR_FORM_OLDPSNINFO, row, psndocVO.getPsnJobVO());
			TrnUtil.setPersonInfo(dsId, PsnApplyConsts.REGULAR_FORM_NEWPSNINFO, row, psndocVO.getPsnJobVO());
		} catch (BusinessException e1) {
			new HrssException(e1).deal();
		} catch (HrssException e1) {
			new HrssException(e1).alert();
		}
	}
}
