package nc.impl.ta.importdata;

import java.util.Collection;
import java.util.Map;
import nc.bs.dao.BaseDAO;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.itf.ta.IImportDataQueryService;
import nc.itf.ta.algorithm.ITimeScope;
import nc.jdbc.framework.SQLParameter;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.importdata.ImportDataVO;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;


public class ImportDataServiceImpl
  implements IImportDataQueryService
{
  private BaseDAO dao = new BaseDAO();
  

  public ImportDataVO[] queryArrayByPsndocAndDate(String pk_org, String pk_psndoc, UFLiteralDate date)
    throws BusinessException
  {
    return null;
  }
  


  public ImportDataVO[][] queryArraysByPsndocsAndDateScope(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    return (ImportDataVO[][])null;
  }
  












  public Map<String, ImportDataVO[]> queryArrayMapByPsndocsAndDateScope(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    if ((StringUtils.isBlank(pk_org)) || (ArrayUtils.isEmpty(pk_psndocs))) {
      return null;
    }
    
    InSQLCreator isc = new InSQLCreator();
    try {
      String pkpsndocinSql = isc.getInSQL(pk_psndocs);
      
      return queryArrayMapByPsndocInSQLAndDateScope(pk_org, pkpsndocinSql, beginDate, endDate);
    } finally {
      isc.clear();
    }
  }
  


  public Map<String, ImportDataVO[]> queryArrayMapByPsndocInSQLAndDateScope(String pk_org, String psndocInSQL, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    Collection<ImportDataVO> returnVos = null;
    SQLParameter parameter = null;
    parameter = new SQLParameter();
    //parameter.addParam(pk_org);
    parameter.addParam(beginDate.getDateBefore(2));
    parameter.addParam(endDate.getDateAfter(2));
    
    returnVos = dao.retrieveByClause(ImportDataVO.class, "pk_psndoc in(" + psndocInSQL + ") and " + "calendardate" + " between ? and ? order by " + "calendartime", parameter);
    


    if (CollectionUtils.isEmpty(returnVos)) {
      return null;
    }
    ImportDataVO[] vos = (ImportDataVO[])returnVos.toArray(new ImportDataVO[0]);
    return CommonUtils.group2ArrayByField("pk_psndoc", vos);
  }
  



  public ImportDataVO[] queryArrayByPsndocAndDate(String pk_org, String pk_psndoc, ITimeScope timeScope, boolean containsFirstSecond, boolean containsLastSecond)
    throws BusinessException
  {
    if (StringUtils.isEmpty(pk_psndoc))
      return null;
    String biggerThan = containsFirstSecond ? " >= " : " > ";
    String lessThan = containsLastSecond ? " <= " : " < ";
    String cond = "pk_psndoc=? and calendartime" + biggerThan + "? and " + "calendartime" + lessThan + "?";
    SQLParameter para = new SQLParameter();
    //para.addParam(pk_org);
    para.addParam(pk_psndoc);
    para.addParam(timeScope.getScope_start_datetime().toStdString(ICalendar.BASE_TIMEZONE));
    para.addParam(timeScope.getScope_end_datetime().toStdString(ICalendar.BASE_TIMEZONE));
    return (ImportDataVO[])CommonUtils.toArray(ImportDataVO.class, new BaseDAO().retrieveByClause(ImportDataVO.class, cond, "calendardate", para));
  }
}

/* Location:           C:\ncProjects\6.5\COCO-PRO\home201712131929\home\modules\hrta\META-INF\lib\hrta_hrtadataprocess.jar
 * Qualified Name:     nc.impl.ta.importdata.ImportDataServiceImpl
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */