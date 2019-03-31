package nc.ui.wa.taxspecial_statistics.view;

import java.awt.Container;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.hr.uif2.view.HrBillListView;
import nc.ui.pub.beans.table.ColumnGroup;
import nc.ui.pub.beans.table.GroupableTableHeader;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsModelDataManager;
import nc.vo.wa.pub.WaState;
import nc.vo.wa.taxspecial_statistics.TaxSpecialStatisticsVO;

/**
 * 专项附加费用扣除统计列表页面
 * 
 * @author: xuhw
 * @since: eHR V6.5
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("serial")
public class TaxSpecialStatisticsListView extends HrBillListView {
//
	private Map<String, Object> aboutRuleMap = null;

	private TaxSpecialStatisticsModelDataManager dataManager = null;
	private List<String[]> headlist = null;
//	@Override
	public void handleEvent(AppEvent event) {
		
//		if (event.getType() == AppEventConst.MODEL_INITIALIZED) {
			
//		}
		 BillPanelUtils.setPkorgToRefModel(getBillListPanel(), getModel().getContext().getPk_org());
		super.handleEvent(event);
//		this.getBillListPanel().getParentListPanel().removeTableSortListener();
//		getBillScrollPane().setRowNOShow(true);
		initPageInfo();
		

	}
	
	private void setBillListPanelColor(){
		
	}

	public Set<WaState> getEnableStateSet() {
		Set<WaState> waStateSet = new HashSet<WaState>();
		waStateSet.add(WaState.NO_WA_DATA_FOUND);
		waStateSet.add(WaState.CLASS_WITHOUT_RECACULATED);
		waStateSet.add(WaState.CLASS_RECACULATED_WITHOUT_CHECK);
		waStateSet.add(WaState.CLASS_PART_CHECKED);
		return waStateSet;
	}

	/**
	 * 对表头进行分组
	 * 
	 * @param proKey
	 * @param headerName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private ColumnGroup getColumnGroup(String[] headerName) {
		ColumnGroup columnGroup = new ColumnGroup(headerName[0]);
		TableColumnModel cm = getBillListPanel().getHeadTable().getColumnModel();
		// 进行多表头的设定
		for (int i = 0; i < cm.getColumnCount(); i++) {
			// 循环需要设置的表头
			for (int n = 1; n < headerName.length; n++) {
				if (getBillListPanel().getHeadBillModel().getBodyKeyByCol(i)
						.equalsIgnoreCase(headerName[n])) {
					columnGroup.add(cm.getColumn(i));
					break;
				}
			}
		}
		return columnGroup;
	}
	
	/**
	 * 初始化页面信息，针对页面上的多表头的形式
	 */
	private void initPageInfo() {
		// 设定多表头
		GroupableTableHeader header = (GroupableTableHeader) getBillListPanel().getHeadTable().getTableHeader();
		for (int i = 0; i < getTableHeadInfo().size(); i++) {
			String[] headname = getTableHeadInfo().get(i);
			header.addColumnGroup(getColumnGroup(headname));
		}
	}
	
	/**
	 * 把分组的字段放到一个map中
	 * 
	 * @return
	 */
	private List<String[]> getTableHeadInfo() {
		if (headlist == null) {
			headlist = new ArrayList<String[]>();
			// 赡养老人
			String parents[] = new String[] {
							"赡养老人",
							TaxSpecialStatisticsVO.PARENTFEETOTAL,
							TaxSpecialStatisticsVO.PARETNFEEDTOTAL,
							TaxSpecialStatisticsVO.PARENTFEE };
			headlist.add(parents);
			//子女教育
			String childs[] = new String[] {
					"子女教育",
					TaxSpecialStatisticsVO.CHILDFEETOTAL,
					TaxSpecialStatisticsVO.CHILDFEEDTOTAL,
					TaxSpecialStatisticsVO.CHILDFEE
			 };
			headlist.add(childs);
			// 房屋津贴
			String hourser[] = new String[] {
					"房屋津贴",
					TaxSpecialStatisticsVO.HOURSEFEETOTAL,
					TaxSpecialStatisticsVO.HOURSEFEEDTOTAL,
					TaxSpecialStatisticsVO.HOURSEFEE};
			headlist.add(hourser);
			// 房屋津贴-租房
			String hourserzu[] = new String[] {
					"房屋津贴-租房",
					TaxSpecialStatisticsVO.HOURSEZUFEETOTAL,
					TaxSpecialStatisticsVO.HOURSEZUFEEDTOTAL,
					TaxSpecialStatisticsVO.HOURSEZUFEE};
			headlist.add(hourserzu);
			// 大病医疗
			String health[] = new String[] {
					"大病医疗",
					TaxSpecialStatisticsVO.HEALTHYFEETOTAL,
					TaxSpecialStatisticsVO.HEALTHYFEEDTOTAL,
					TaxSpecialStatisticsVO.HEALTHYFEE};
			headlist.add(health);
			// 继续教育
			String education[] = new String[] {
					"继续教育",
					TaxSpecialStatisticsVO.EDUCATIONFEETOTAL,
					TaxSpecialStatisticsVO.EDUCATIONFEEDTOTAL,
					TaxSpecialStatisticsVO.EDUCATIONFEE};
			headlist.add(education);
			// 汇总
			String total[] = new String[] {
					"汇总",
					TaxSpecialStatisticsVO.ALLFEETOTAL,
					TaxSpecialStatisticsVO.ALLFEEDTOTAL,
					TaxSpecialStatisticsVO.ALLFEE};
			headlist.add(total);
		}
		return headlist;
	}

	
	public Container getParentContainer() {
		return SwingUtilities.getWindowAncestor(getModel().getContext().getEntranceUI());
	}

	public void setDataManager(TaxSpecialStatisticsModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public TaxSpecialStatisticsModelDataManager getDataManager() {
		return dataManager;
	}

	public BillScrollPane getBillScrollPane() {
		return getBillListPanel().getParentListPanel();
	}
}
