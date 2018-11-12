package nc.bs.hrsms.ta.common.ctrl;

import nc.bs.hrss.pub.PageModel;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.WebSession;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.refnode.RefNode;
import nc.uap.lfw.reference.ILfwRefModel;
import nc.uap.lfw.reference.app.AppReferenceController;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.ta.psndoc.TBMPsndocVO;
import uap.web.bd.pub.AppUtil;

public class BURefController extends AppReferenceController
{
  protected void processSelfWherePart(Dataset ds, RefNode rfnode, String filterValue, ILfwRefModel refModel)
  {
    resetRefnode(rfnode, refModel);
  }

  protected void processTreeSelWherePart(Dataset ds, RefNode rfnode, ILfwRefModel refModel)
  {
    resetRefnode(rfnode, refModel);
  }

  private void resetRefnode(RefNode rfnode, ILfwRefModel refModel)
  {
    String pk_group = null;

    String pk_bu = null;
    if (isMngFunc())
    {
      pk_group = SessionUtil.getPk_mng_group();

      pk_bu = SessionUtil.getPk_mng_org();
    } else {
      TBMPsndocVO tbmPsndocVO = (TBMPsndocVO)AppUtil.getAppAttr("APP_TBMPSNDOC");
      if (tbmPsndocVO == null) {
        String pk_psndoc = SessionUtil.getPk_psndoc();
        tbmPsndocVO = TBMPsndocUtil.getTBMPsndoc(pk_psndoc, new UFDateTime());
      }

      String pk_psnjob = tbmPsndocVO.getPk_psnjob();
      PsnJobVO psnjobVO = null;
      try {
        psnjobVO = (PsnJobVO)((IPersistenceRetrieve)ServiceLocator.lookup(IPersistenceRetrieve.class)).retrieveByPk(PsnJobVO.class, pk_psnjob, null);
      } catch (BusinessException e) {
        new HrssException(e).deal();
      } catch (HrssException e) {
        e.alert();
      }
      if (psnjobVO != null)
      {
        pk_group = psnjobVO.getPk_group();

        pk_bu = psnjobVO.getPk_org();
      }
    }

    refModel.setPk_group(pk_group);
    refModel.setPk_org(pk_bu);
  }

  @SuppressWarnings("rawtypes")
private String getCurrentFunCode()
  {
    WebSession parentSession = LfwRuntimeEnvironment.getWebContext().getParentSession();
    WebSession session = parentSession;
    Class clazz = (Class)parentSession.getAttribute("HRSS.PAGE.modelClass");
    if (null == clazz){
    	session = LfwRuntimeEnvironment.getWebContext().getWebSession();
    	clazz = (Class) session.getAttribute("HRSS.PAGE.modelClass");
    }
    if(null == clazz)
      return null;
    if (PageModel.class.isAssignableFrom(clazz)) {
      return (String)session.getAttribute("HRSS.PAGE.FunCode");
    }
    return null;
  }

  private boolean isMngFunc()
  {
    String funCode = getCurrentFunCode();
    // edit by shaochj Sep 14, 2015  begin
	//Ô­´úÂë£º/*  return (null != funCode) && (funCode.startsWith("E204"));*/
	// edit by shaochj Sep 14, 2015 end
   if(((null != funCode) && (funCode.startsWith("E204"))) || ((null != funCode) && (funCode.startsWith("E206"))))
	   return true;
   return false;
  }
}