  package nc.ui.twhr.glb.model;
  
  import nc.ui.pubapp.pub.smart.SmartBatchAppModelService;
import nc.ui.pubapp.uif2app.model.BatchModelDataManager;
import nc.ui.uif2.model.AbstractBatchAppModel;
import nc.vo.bd.currtype.CurrtypeVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
 
  
  public class BatchModelDataManagerGlb extends BatchModelDataManager implements nc.ui.uif2.model.IAppModelDataManagerEx
  {
			String wheresql = "and pk_org='GLOBLE00000000000000'";
    public void initModel()
    {
	
      try {
        this.model.initModel(this.service.queryByWhereSql(wheresql));
         }
         catch (Exception e)
         {
         throw new BusinessRuntimeException("", e);
         }
    }
    

  }


