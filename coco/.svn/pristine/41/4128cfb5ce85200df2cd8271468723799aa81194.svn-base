package nc.impl.ta.signcard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.DataPermissionUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ISignCardManageService;
import nc.itf.ta.ISignCardQueryService;
import nc.itf.ta.ISignCardRegisterManageMaintain;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.jdbc.framework.SQLParameter;
import nc.md.data.access.NCObject;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.pub.PsnInSQLDateScope;
import nc.vo.ta.signcard.AggSignVO;
import nc.vo.ta.signcard.SignCardBeyondTimeVO;
import nc.vo.ta.signcard.SignCommonVO;
import nc.vo.ta.signcard.SignRegVO;
import nc.vo.ta.signcard.SignbVO;
import nc.vo.ta.signcard.SignhVO;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.uap.pf.PFBatchExceptionInfo;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.vo.uif2.LoginContext;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;




public class SignCardServiceImpl
  implements ISignCardQueryService, ISignCardManageService
{
  private SimpleDocServiceTemplate serviceTemplate;
  
  public Map<String, SignCommonVO[]> queryAllSuperVOExcNoPassByCondDate(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    throw new UnsupportedOperationException("this operation is not supported!");
  }
  
  public SignCommonVO[] queryAllSuperVOExcNoPassByPsn(String pkOpk_orgrg, String pk_psndoc)
    throws BusinessException
  {
    throw new UnsupportedOperationException("this operation is not supported!");
  }
  

  public SignCommonVO[] queryAllSuperVOExcNoPassByPsnDate(String pk_org, String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    throw new UnsupportedOperationException("this operation is not supported!");
  }
  

  public Map<String, SignRegVO[]> queryAllSuperVOIncEffectiveByCondDate(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    throw new UnsupportedOperationException("this operation is not supported!");
  }
  
  public SignRegVO[] queryAllSuperVOIncEffectiveByPsn(String pk_org, String pk_psndoc)
    throws BusinessException
  {
    throw new UnsupportedOperationException("this operation is not supported!");
  }
  

  public SignRegVO[] queryAllSuperVOIncEffectiveByPsnDate(String pk_org, String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    throw new UnsupportedOperationException("this operation is not supported!");
  }
  


  public Map<String, SignRegVO[]> queryAllSuperVOIncEffectiveByPsndocsDate(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    if ((StringUtils.isBlank(pk_org)) || (ArrayUtils.isEmpty(pk_psndocs))) {
      return null;
    }
    
    InSQLCreator isc = new InSQLCreator();
    try {
      return queryAllSuperVOIncEffectiveByPsndocInSQLDate(pk_org, isc.getInSQL(pk_psndocs), beginDate, endDate);
    } finally {
      isc.clear();
    }
  }
  


  public Map<String, SignRegVO[]> queryAllSuperVOIncEffectiveByPsndocInSQLDate(String pk_org, String psndocInSQL, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    Collection<SignRegVO> returnVos = null;
    SQLParameter parameter = null;
    parameter = new SQLParameter();
    //parameter.addParam(pk_org);
    parameter.addParam(new UFDateTime(beginDate.getDateBefore(2).toStdString() + " 00:00:00", ICalendar.BASE_TIMEZONE));
    parameter.addParam(new UFDateTime(endDate.getDateAfter(2).toStdString() + " 23:59:59", ICalendar.BASE_TIMEZONE));
    returnVos = new BaseDAO().retrieveByClause(SignRegVO.class, "pk_psndoc in(" + psndocInSQL + ") and " + "signtime" + " between ? and ? ", "signtime", parameter);
    

    if (CollectionUtils.isEmpty(returnVos)) {
      return null;
    }
    SignRegVO[] vos = (SignRegVO[])returnVos.toArray(new SignRegVO[0]);
    return CommonUtils.group2ArrayByField("pk_psndoc", vos);
  }
  

  public Map<String, SignCommonVO[]> queryAllSuperVOExcNoPassByPsndocInSQLDate(String pkOrg, String psndocInSQL, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    throw new UnsupportedOperationException("this operation is not supported!");
  }
  
  public SignCommonVO[] queryIntersectionBills(SignCommonVO bill)
    throws BusinessException
  {
    throw new UnsupportedOperationException("this operation is not supported!");
  }
  
  public Map<String, SignCommonVO[]> queryIntersectionBillsMap(SignCommonVO[] bills)
    throws BusinessException
  {
    throw new UnsupportedOperationException("this operation is not supported!");
  }
  
  public void deleteData(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException
  {
    String[] pk_psndocs = ((ITBMPsndocQueryService)NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)).queryLatestPsndocsByCondition(pk_org, fromWhereSQL, beginDate, endDate);
    if (ArrayUtils.isEmpty(pk_psndocs))
      return;
    InSQLCreator isc = null;
    try {
      isc = new InSQLCreator();
      String inSQL = isc.getInSQL(pk_psndocs);
      SQLParameter para = new SQLParameter();
      //para.addParam(pk_org);
      para.addParam(beginDate);
      para.addParam(endDate);
      new BaseDAO().executeUpdate("delete from " + SignRegVO.getDefaultTableName() + " where pk_psndoc" + " in (" + inSQL + ") and " + "signdate" + " between ? and ?", para);
      


      PsnInSQLDateScope vo = new PsnInSQLDateScope();
      vo.setPk_org(pk_org);
      vo.setPsndocInSQL(inSQL);
      vo.setBeginDate(beginDate.getDateBefore(1));
      vo.setEndDate(endDate.getDateAfter(1));
      EventDispatcher.fireEvent(new BusinessEvent("5b53e23c-9bd9-4eef-bea7-0b4eb38fe120", "1006", vo));
    }
    finally
    {
      if (isc != null) {
        isc.clear();
      }
    }
  }
  
  public SignRegVO[] queryAllSuperVOIncEffectiveByPsn(String pk_org, String pk_psndoc, ITimeScope timeScope, boolean containsFirstSecond, boolean containsLastSecond)
    throws BusinessException
  {
    if (StringUtils.isEmpty(pk_psndoc))
      return null;
    String biggerThan = containsFirstSecond ? " >= " : " > ";
    String lessThan = containsLastSecond ? " <= " : " < ";
    String cond = "pk_psndoc=? and signtime" + biggerThan + "? and " + "signtime" + lessThan + "?";
    SQLParameter para = new SQLParameter();
    //para.addParam(pk_org);
    para.addParam(pk_psndoc);
    para.addParam(timeScope.getScope_start_datetime().toStdString(ICalendar.BASE_TIMEZONE));
    para.addParam(timeScope.getScope_end_datetime().toStdString(ICalendar.BASE_TIMEZONE));
    return (SignRegVO[])CommonUtils.toArray(SignRegVO.class, new BaseDAO().retrieveByClause(SignRegVO.class, cond, "signtime", para));
  }
  
  public SimpleDocServiceTemplate getServiceTemplate() {
    if (serviceTemplate == null) {
      serviceTemplate = new SimpleDocServiceTemplate("hrtasigncardh");
    }
    return serviceTemplate;
  }
  



  public PfProcessBatchRetObject approveValidate(AggregatedValueObject[] aggvos, LoginContext context, String operateCode, String mdOperateCode, String resourceCode)
    throws BusinessException
  {
    TimeRuleVO timeRuleVo = ((ITimeRuleQueryService)NCLocator.getInstance().lookup(ITimeRuleQueryService.class)).queryByOrg(context.getPk_org());
    Integer signCounts = timeRuleVo.getSigncounts();
    
    List<SignbVO> subVOs = new ArrayList();
    ArrayList<AggregatedValueObject> al = new ArrayList();
    PFBatchExceptionInfo errInfo = new PFBatchExceptionInfo();
    for (int i = 0; i < aggvos.length; i++)
    {
      AggregatedValueObject dbVO = (AggregatedValueObject)getServiceTemplate().queryByPk(AggSignVO.class, aggvos[i].getParentVO().getPrimaryKey(), true);
      if (dbVO == null)
      {
        errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6001pf", "06001pf0053"));


      }
      else if (!checkDataPermission(operateCode, mdOperateCode, resourceCode, dbVO))
      {
        errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6001pf", "06001pf0054"));

      }
      else
      {
        NCObject dbObj = NCObject.newInstance(dbVO);
        
        IFlowBizItf flowBiz = (IFlowBizItf)dbObj.getBizInterface(IFlowBizItf.class);
        

        if (!ArrayUtils.contains(new int[] { 3, 2 }, flowBiz.getApproveStatus().intValue()))
        {
          errInfo.putErrorMessage(i, dbVO, ResHelper.getString("6001pf", "06001pf0055"));


        }
        else if ((!isDirectApprove(flowBiz)) && (!isCheckman(flowBiz)))
        {
          errInfo.putErrorMessage(i, dbVO, ResHelper.getString("6001pf", "06001pf0056"));

        }
        else
        {
          SignhVO headVO = ((AggSignVO)aggvos[i]).getSignhVO();
          SignbVO[] thisVOs = ((AggSignVO)aggvos[i]).getSignbVOs();
          for (SignbVO subVO : thisVOs) {
            subVO.setPk_psndoc(headVO.getPk_psndoc());
            subVO.setPk_psnjob(headVO.getPk_psnjob());
            subVOs.add(subVO);
          }
          SignCardBeyondTimeVO[] beyondVOs = ((ISignCardRegisterManageMaintain)NCLocator.getInstance().lookup(ISignCardRegisterManageMaintain.class)).vldAndGetBydPrt(context.getPk_org(), (SignCommonVO[])subVOs.toArray(new SignbVO[0]));
          if (!ArrayUtils.isEmpty(beyondVOs)) {
            String err = ResHelper.getString("6017signcardapp", "06017signcardapp0037", new String[] { signCounts.toString() });
            
            errInfo.putErrorMessage(i, aggvos[i], err);
          }
          else {
            al.add(aggvos[i]);
          } } } }
    return new PfProcessBatchRetObject(al.toArray(new AggSignVO[0]), errInfo);
  }
  











  private boolean checkDataPermission(String operateCode, String mdOperateCode, String resourceCode, AggregatedValueObject aggVO)
    throws BusinessException
  {
    if (((StringUtils.isBlank(operateCode)) && (StringUtils.isBlank(mdOperateCode))) || (StringUtils.isBlank(resourceCode)))
    {
      return true;
    }
    
    boolean blHasDataPermission = true;
    
    String resDataId = aggVO.getParentVO().getPrimaryKey();
    if (!StringUtils.isBlank(mdOperateCode))
    {
      blHasDataPermission = DataPermissionUtils.isUserhasPermissionByMetaDataOperation(resourceCode, resDataId, mdOperateCode);
    }
    else
    {
      blHasDataPermission = DataPermissionUtils.isUserhasPermission(resourceCode, resDataId, operateCode);
    }
    
    return blHasDataPermission;
  }
  
  private boolean isDirectApprove(IFlowBizItf itf) throws BusinessException
  {
    String strBillType = StringUtils.isBlank(itf.getTranstype()) ? itf.getBilltype() : itf.getTranstype();
    return !getIPFWorkflowQry().isApproveFlowStartup(itf.getBillId(), strBillType);
  }
  
  private IPFWorkflowQry getIPFWorkflowQry()
  {
    return (IPFWorkflowQry)NCLocator.getInstance().lookup(IPFWorkflowQry.class);
  }
  
  private boolean isCheckman(IFlowBizItf itf) throws BusinessException
  {
    String strBillType = StringUtils.isBlank(itf.getTranstype()) ? itf.getBilltype() : itf.getTranstype();
    
    return getIPFWorkflowQry().isCheckman(itf.getBillId(), strBillType, PubEnv.getPk_user());
  }
}

/* Location:           C:\ncProjects\6.5\COCO-PRO\home201712131929\home\modules\hrta\META-INF\lib\hrta_hrtasigncard.jar
 * Qualified Name:     nc.impl.ta.signcard.SignCardServiceImpl
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */