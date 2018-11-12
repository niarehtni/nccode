package nc.ui.twhr.rangetable.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.pub.power.PowerCheckUtils;
import nc.ui.pubapp.uif2app.actions.pflow.DeleteScriptAction;
import nc.ui.uif2.components.ITabbedPaneAwareComponent;
import nc.ui.uif2.model.IMultiRowSelectModel;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.pubapp.pub.power.PowerActionEnum;
import nc.vo.pubapp.vosplit.ExVOUtils;

public class RangetableDeleteAction extends DeleteScriptAction {

  private static final long serialVersionUID = 1L;

  private String billCodeName;
  
  private boolean powercheck;
  
  @Override
  public void doAction(ActionEvent e) throws Exception {
    if (this.powercheck) {
      Object[] tempData = null;
      if(this.editor != null && ((ITabbedPaneAwareComponent) this.editor)
            .isComponentVisible()) {       
        if (null != this.getModel().getSelectedData()) {
          tempData = new Object[] {
            this.getModel().getSelectedData()
          };
        }
      }
      tempData =
            ((IMultiRowSelectModel) this.getModel()).getSelectedOperaDatas();

      IBill[] bills = ExVOUtils.convertArrayType(tempData);
      PowerCheckUtils.checkHasPermission(bills, this.getBillType(),
          PowerActionEnum.DELETE.getActioncode(), this.getBillCodeName());
    }

    super.doAction(e);
  }
  
  public String getBillCodeName() {
    return this.billCodeName;
  }

  public void setBillCodeName(String billCodeName) {
    this.billCodeName = billCodeName;
  }
  
  public boolean isPowercheck() {
    return this.powercheck;
  }

  public void setPowercheck(boolean powercheck) {
    this.powercheck = powercheck;
  }
  

}
