package nc.ui.twhr.rangetable.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.actions.AbstractReferenceAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractAppModel;

public class RangetableAddAction extends AbstractReferenceAction {

  private static final long serialVersionUID = 1L;
  
  private AbstractAppModel model;

  @Override
  public void doAction(ActionEvent e) throws Exception {
    this.model.setUiState(UIState.ADD);    
  }
  
  public AbstractAppModel getModel() {
    return this.model;
  }

  public void setModel(AbstractAppModel model) {
    this.model = model;
    this.model.addAppEventListener(this);
  }
  
  @Override
  protected boolean isActionEnable() {
    return this.model.getUiState() == UIState.NOT_EDIT;
  }
  
  @Override
  protected boolean isManual() {
    return true;
  }

}
