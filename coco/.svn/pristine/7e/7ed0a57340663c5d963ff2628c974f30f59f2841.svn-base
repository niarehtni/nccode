package nc.bs.twhr.basedoc.bp;

import nc.impl.pubapp.pattern.data.vo.SchemeVOQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.twhr.basedoc.BaseDocVO;

public class BasedocBP {
  
  public BaseDocVO[] queryByQueryScheme(IQueryScheme querySheme) {
    QuerySchemeProcessor p = new QuerySchemeProcessor(querySheme);
    p.appendFuncPermissionOrgSql();
    return new SchemeVOQuery<BaseDocVO>(BaseDocVO.class).query(
        querySheme, null);
  }

}
