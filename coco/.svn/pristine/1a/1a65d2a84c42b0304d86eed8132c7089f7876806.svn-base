package nc.impl.ta.syscard;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.impl.ta.persistence.TASimpleDocServiceTemplate;
import nc.itf.ta.ISysCardManageService;
import nc.itf.ta.ISysCardQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.algorithm.ITimeScope;
import nc.jdbc.framework.SQLParameter;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.importdata.ImportDataVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.PsnInSQLDateScope;
import nc.vo.ta.syscard.SysCardVO;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;



public class SysCardServiceImpl
  implements ISysCardQueryService, ISysCardManageService
{
  private TASimpleDocServiceTemplate serviceTemplate;
  
  public TASimpleDocServiceTemplate getServiceTemplate()
  {
    if (serviceTemplate == null)
      serviceTemplate = new TASimpleDocServiceTemplate("8b7d0b1d-f9e3-416a-8532-72d9af2fd1c8");
    return serviceTemplate;
  }
  







  public SysCardVO[] saveSysCardVOs(List<SysCardVO> voList, PsnInSQLDateScope psnScope)
    throws BusinessException
  {
    if (CollectionUtils.isEmpty(voList)) {
      return null;
    }
    String delCond = "pk_psndoc in (" + psnScope.getPsndocInSQL() + ") and " + "signdate" + " between ? and ? ";
    SQLParameter param = new SQLParameter();
    //param.addParam(psnScope.getPk_org());
    param.addParam(psnScope.getBeginDate());
    param.addParam(psnScope.getEndDate());
    new BaseDAO().deleteByClause(SysCardVO.class, delCond, param);
    
    return (SysCardVO[])getServiceTemplate().batchInsertDirect(voList.toArray(new SysCardVO[0]));
  }
  


  public SysCardVO[] queryArrayByPsndocAndDate(String pk_org, String pk_psndoc, ITimeScope timeScope, boolean containsFirstSecond, boolean containsLastSecond)
    throws BusinessException
  {
    if (StringUtils.isEmpty(pk_psndoc))
      return null;
    String biggerThan = containsFirstSecond ? " >= " : " > ";
    String lessThan = containsLastSecond ? " <= " : " < ";
    String cond = "and pk_psndoc=? and signtime" + biggerThan + "? and " + "signtime" + lessThan + "?";
    SQLParameter para = new SQLParameter();
    //para.addParam(pk_org);
    para.addParam(pk_psndoc);
    para.addParam(timeScope.getScope_start_datetime().toStdString(ICalendar.BASE_TIMEZONE));
    para.addParam(timeScope.getScope_end_datetime().toStdString(ICalendar.BASE_TIMEZONE));
    return (SysCardVO[])CommonUtils.toArray(SysCardVO.class, new BaseDAO().retrieveByClause(SysCardVO.class, cond, "signtime", para));
  }
  


  public Map<String, SysCardVO[]> queryAllSuperVOIncEffectiveByPsndocInSQLDate(String pk_org, String psndocInSQL, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    Collection<ImportDataVO> returnVos = null;
    SQLParameter parameter = null;
    parameter = new SQLParameter();
    //parameter.addParam(pk_org);
    parameter.addParam(beginDate.getDateBefore(2));
    parameter.addParam(endDate.getDateAfter(2));
    BaseDAO dao = new BaseDAO();
    
    returnVos = dao.retrieveByClause(SysCardVO.class, "pk_psndoc in(" + psndocInSQL + ") and " + "signtime" + " between ? and ? order by " + "signtime", parameter);
    


    if (CollectionUtils.isEmpty(returnVos)) {
      return null;
    }
    SysCardVO[] vos = (SysCardVO[])returnVos.toArray(new SysCardVO[0]);
    return CommonUtils.group2ArrayByField("pk_psndoc", vos);
  }
  

  public void deleteByCondition(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    TBMPsndocVO[] tbmPsnVO = ((ITBMPsndocQueryService)NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)).queryLatestByCondition(pk_org, fromWhereSQL, beginDate, endDate);
    
    if (ArrayUtils.isEmpty(tbmPsnVO))
      return;
    InSQLCreator isc = new InSQLCreator();
    String inSQL = isc.getInSQL(tbmPsnVO, "pk_psndoc");
    SQLParameter parameter = new SQLParameter();
    //parameter.addParam(pk_org);
    parameter.addParam(new UFDateTime(beginDate.toString() + " 00:00:00").toStdString(ICalendar.BASE_TIMEZONE));
    parameter.addParam(new UFDateTime(endDate.toString() + " 23:59:59").toStdString(ICalendar.BASE_TIMEZONE));
    String sqlWhere = "pk_psndoc in (" + inSQL + ") and " + "signtime" + " between ? and ? ";
    

    new BaseDAO().deleteByClause(SysCardVO.class, sqlWhere, parameter);
  }
}

/* Location:           C:\ncProjects\6.5\COCO-PRO\home201712131929\home\modules\hrta\META-INF\lib\hrta_hrtadataprocess.jar
 * Qualified Name:     nc.impl.ta.syscard.SysCardServiceImpl
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */