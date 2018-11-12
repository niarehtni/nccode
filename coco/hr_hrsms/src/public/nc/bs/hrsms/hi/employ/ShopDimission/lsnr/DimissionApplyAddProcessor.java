package nc.bs.hrsms.hi.employ.ShopDimission.lsnr;

import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.trn.PsnApplyConsts;
import nc.bs.hrss.trn.TrnUtil;
import nc.bs.hrss.trn.lsnr.TrnBaseAddProcessor;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.trn.pub.TRNConst;
import nc.vo.trn.transmng.StapplyVO;

public class DimissionApplyAddProcessor extends TrnBaseAddProcessor{
	@Override
	public void onBeforeRowAdd(Dataset ds, Row row, String billTypeCode) {
		ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();
		String transtype = (String) appCtx.getAppAttribute(PsnApplyConsts.TRANSTYPE_COMB);
		UFBoolean isChangePsnorg = (UFBoolean) appCtx.getAppAttribute(PsnApplyConsts.ISCHANGEPSNORG_CHECK);
		// 业务类型
		row.setValue(ds.nameToIndex(StapplyVO.PK_TRNSTYPE), transtype);
		// 调配方式
		row.setValue(ds.nameToIndex(StapplyVO.STAPPLY_MODE), isChangePsnorg.booleanValue()?TRNConst.TRANSMODE_CROSS_OUT:TRNConst.TRANSMODE_INNER);
		SessionBean session = SessionUtil.getSessionBean();
		PsndocVO psndocVO = session.getPsndocVO();
		// 员工主职VO
		PsnJobVO psnJobVO = psndocVO.getPsnJobVO();
		row.setValue(ds.nameToIndex(StapplyVO.PK_OLD_HI_ORG), psnJobVO.getPk_hrorg());
		row.setValue(ds.nameToIndex(StapplyVO.NEWPK_PSNCL), null);
		super.onBeforeRowAdd(ds, row, billTypeCode);
	}
	
	@Override
	public void onAfterRowAdd(Dataset ds, Row row) {
		super.onAfterRowAdd(ds, row);
		SessionBean session = SessionUtil.getSessionBean();
		PsndocVO psndocVO = session.getPsndocVO();
		String dsId = PsnApplyConsts.TRANSFER_DS_ID;
		ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();
		UFBoolean isChangePsnorg = (UFBoolean) appCtx.getAppAttribute(PsnApplyConsts.ISCHANGEPSNORG_CHECK);
		try {
			// 设置离职后管理组织是否可编辑
			if(!isChangePsnorg.booleanValue())
				row.setValue(ds.nameToIndex(StapplyVO.PK_HI_ORG), psndocVO.getPsnJobVO().getPk_hrorg());
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
