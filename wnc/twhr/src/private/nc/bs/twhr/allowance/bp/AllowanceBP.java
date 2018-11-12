package nc.bs.twhr.allowance.bp;

import nc.impl.pubapp.pattern.data.vo.SchemeVOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.twhr.allowance.AllowanceVO;

public class AllowanceBP {
  
  public AllowanceVO[] queryByQueryScheme(IQueryScheme querySheme) {
    QuerySchemeProcessor p = new QuerySchemeProcessor(querySheme);
    p.appendFuncPermissionOrgSql();
    return new SchemeVOQuery<AllowanceVO>(AllowanceVO.class).query(
        querySheme, null);
  }

}
