package nc.ui.twhr.twhr_declaration.ace.view;
import nc.ui.pubapp.uif2app.view.ShowUpableBillListView;
import nc.ui.uif2.AppEvent;

public class DeclarationListView extends ShowUpableBillListView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6178345724917694319L;


	@Override
	public void handleEvent(AppEvent event) {
		if(null != getBillListPanel().getChildListPanel("id_companybvo")){
			getBillListPanel().getChildListPanel("id_companybvo").setTotalRowShow(true);
		}
		if(null != getBillListPanel().getParentListPanel()){
			//隐藏主表,主表的值是通过查询的聚合vo,并不是真正的数据库表,显示出来并无意义
			getBillListPanel().getParentListPanel().setVisible(false);
		}
		
		super.handleEvent(event);
		
	}
	
	
	
	
}
