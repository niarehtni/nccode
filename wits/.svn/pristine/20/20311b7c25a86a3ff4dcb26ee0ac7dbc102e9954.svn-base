package nc.bs.twhr.nhicalc.bp;

import nc.impl.pubapp.pattern.data.vo.SchemeVOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.twhr.nhicalc.NhiCalcVO;

public class NhicalcBP {
  
  public NhiCalcVO[] queryByQueryScheme(IQueryScheme querySheme) {
    QuerySchemeProcessor p = new QuerySchemeProcessor(querySheme);
    p.appendFuncPermissionOrgSql();
    return new SchemeVOQuery<NhiCalcVO>(NhiCalcVO.class).query(
        querySheme, null);
  }

}
