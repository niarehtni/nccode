package nc.ui.twhr.basedoc.action;

import nc.ui.pubapp.uif2app.actions.batch.BatchAddLineAction;
import nc.vo.twhr.basedoc.BaseDocVO;

public class BasedocAddLineAction extends BatchAddLineAction {

  private static final long serialVersionUID = 1L;
  
  @Override
  protected void setDefaultData(Object obj) {
    super.setDefaultData(obj);
    BaseDocVO baseDocVO = (BaseDocVO) obj;
    baseDocVO.setPk_group(this.getModel().getContext().getPk_group());
    baseDocVO.setPk_org(this.getModel().getContext().getPk_org());
  }


}
