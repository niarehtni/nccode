package nc.ui.twhr.allowance.action;

import nc.ui.pubapp.uif2app.actions.batch.BatchAddLineAction;
import nc.vo.twhr.allowance.AllowanceVO;

public class AllowanceAddLineAction extends BatchAddLineAction {

  private static final long serialVersionUID = 1L;
  
  @Override
  protected void setDefaultData(Object obj) {
    super.setDefaultData(obj);
    AllowanceVO baseDocVO = (AllowanceVO) obj;
    baseDocVO.setPk_group(this.getModel().getContext().getPk_group());
    baseDocVO.setPk_org(this.getModel().getContext().getPk_org());
  }


}
