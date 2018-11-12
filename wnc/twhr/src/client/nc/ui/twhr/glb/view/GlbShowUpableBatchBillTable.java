package nc.ui.twhr.glb.view;

import nc.ui.uif2.components.AutoShowUpEventSource;
import nc.ui.uif2.components.IAutoShowUpComponent;
import nc.ui.uif2.components.IAutoShowUpEventListener;
import nc.ui.uif2.components.ITabbedPaneAwareComponent;
import nc.ui.uif2.components.ITabbedPaneAwareComponentListener;
import nc.ui.uif2.components.TabbedPaneAwareCompnonetDelegate;
import nc.ui.uif2.editor.BatchBillTable;
import nc.uif2.annoations.MethodType;
import nc.uif2.annoations.ViewMethod;
import nc.uif2.annoations.ViewType;



public class GlbShowUpableBatchBillTable extends BatchBillTable implements
    ITabbedPaneAwareComponent, IAutoShowUpComponent {

	
  private static final long serialVersionUID = 1L;
  private BatchBillTable editor = null;
  private AutoShowUpEventSource autoShowUpComponent =
      new AutoShowUpEventSource(this);

  private TabbedPaneAwareCompnonetDelegate tabbedPaneAwareCompnonetDelegate =
      new TabbedPaneAwareCompnonetDelegate();

  @Override
  public void addTabbedPaneAwareComponentListener(
      ITabbedPaneAwareComponentListener l) {
    this.tabbedPaneAwareCompnonetDelegate
        .addTabbedPaneAwareComponentListener(l);
  }

  @Override
  public boolean canBeHidden() {
    return this.tabbedPaneAwareCompnonetDelegate.canBeHidden();
  }

  @Override
  public boolean isComponentVisible() {
    return this.tabbedPaneAwareCompnonetDelegate.isComponentVisible();
  }

  @Override
  public void setAutoShowUpEventListener(IAutoShowUpEventListener l) {
    this.autoShowUpComponent.setAutoShowUpEventListener(l);
  }

  @Override
  public void setComponentVisible(boolean visible) {
    this.tabbedPaneAwareCompnonetDelegate.setComponentVisible(visible);
  }

  @Override
  public void showMeUp() {
    this.autoShowUpComponent.showMeUp();
  }
  
  /*public void initUI() {
		// TODO Auto-generated method stub
		super.initUI();
		this.getBillCardPanel().setBodyMultiSelect(true);
		//this.getBillListPanel().set
		   getModel().setInMultiSelectmode(true);
	}*/
  
  @ViewMethod(viewType=ViewType.BatchBillTable, methodType=MethodType.GETTER)
    public BatchBillTable getEditor() {
       return this.editor;
     }
  
    @ViewMethod(viewType=ViewType.BatchBillTable, methodType=MethodType.SETTER)
     public void setEditor(BatchBillTable editor) {
       this.editor = editor;
     }
}
