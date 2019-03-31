package nc.ui.wa.category.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import nc.ui.hr.uif2.view.HrAppEventConst;
import nc.ui.hr.uif2.view.HrBillListView;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.wa.category.WaClassVO;

/**
 * 
 * @author: xuanlt 
 * @date: 2009-11-18 上午10:29:43
 * @since: eHR V6.0
 * @走查人: 
 * @走查日期: 
 * @修改人: 
 * @修改日期: 
 */
public class WaClassListView extends HrBillListView {

	/**
	 * @author xuanlt on 2009-11-18 
	 */
	private static final long serialVersionUID = -7027983504813076656L;
	
	@Override
	public void initUI() {
		super.initUI();
		//集团薪资方案不显示汇总方案 
		if(isGroupNode()){
			this.getBillListPanel().hideHeadTableCol(WaClassVO.COLLECTFLAG);
			this.getBillListPanel().hideHeadTableCol(WaClassVO.RANGERULE);
			this.getBillListPanel().hideHeadTableCol(WaClassVO.MUTIPLEFLAG);
			this.getBillListPanel().hideHeadTableCol(WaClassVO.ISAPPORVE);
// {MOD:新个税补丁}
// begin
			//2019 新税改
			this.getBillListPanel().hideHeadTableCol(WaClassVO.YEARBONUSFLAG);
// end
		}else  if(isOrgNode()){
			//zuzhi
			this.getBillListPanel().hideHeadTableCol(WaClassVO.ADDFLAG);
			
		}
		
		this.getBillListPanel().getHeadTable().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (getModel().getUiState() == UIState.NOT_EDIT) {
					int curSelectRow = billListPanel.getHeadTable().getSelectedRow();
					// 双击切换到卡片界面
					if (e.getClickCount() == 2 && curSelectRow > -1) {
						getModel().fireEvent(new AppEvent(HrAppEventConst.SHOW_FORM, this, null));
					}
				}
			}
		});
	
	
	}
	
	private boolean isGroupNode() {
		return getModel().getContext().getNodeType() == NODE_TYPE.GROUP_NODE;

	}
	
	private boolean isOrgNode() {
		return getModel().getContext().getNodeType() == NODE_TYPE.ORG_NODE;

	}



}
