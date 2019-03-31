package nc.ui.pubapp.uif2app.components.grand.util;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ListSelectionModel;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.utils.UserDefineRefUtils;
import nc.itf.pubapp.uif2app.components.grand.IGrandAggVosQueryService;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pubapp.uif2app.components.grand.CardGrandPanelComposite;
import nc.ui.pubapp.uif2app.components.grand.GrandPanelComposite;
import nc.ui.pubapp.uif2app.components.grand.ListGrandPanelComposite;
import nc.ui.pubapp.uif2app.components.grand.MainGrandBlankFilter;
import nc.ui.pubapp.uif2app.components.grand.MainGrandRelationShip;
import nc.ui.pubapp.uif2app.components.grand.model.IGrandValidationService;
import nc.ui.pubapp.uif2app.event.card.CardBodyRowChangedEvent;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillListView;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

import org.apache.commons.lang.StringUtils;

/**
 * 主子孙卡片事件处理工具类
 */
public class CardPanelEventUtil {

	/**
	 * 编辑态行切换事件
	 */
	public static void rowChangeStateIsEdit(CardGrandPanelComposite mainGrandPanel, CardBodyRowChangedEvent event,
			IGrandValidationService grandValidationService, MainGrandBlankFilter filter) {
		// 清除当前缓存标识
		mainGrandPanel.getModel().getTestAddFirstMap().clear();
		int lastrow = event.getOldRow();
		int currentRow = event.getRow();
		if (currentRow >= 0) {
			BillCardPanel mainBillCardPanel = ((BillForm) mainGrandPanel.getMainPanel()).getBillCardPanel();
			String currentbodyTabCode = mainBillCardPanel.getCurrentBodyTableCode();
			// 获得主子孙的对应关系
			MainGrandRelationShip relationShip = mainGrandPanel.getMaingrandrelationship();
			BillForm grandBillForm = (BillForm) relationShip.getBodyTabTOGrandCardComposite().get(currentbodyTabCode);
			if (grandBillForm != null) {
				BillCardPanel grandBillCardPanel = grandBillForm.getBillCardPanel();
				String grandTabCode = grandBillCardPanel.getCurrentBodyTableCode();
				RowChangeBean rowChangeBean = setChangeRowInfo(mainGrandPanel, lastrow, currentRow, grandBillForm);
				CircularlyAccessibleValueObject[] bodyVos = MainGrandUtil.getBodyVOsByTabCode(
						(BillForm) mainGrandPanel.getMainPanel(), currentbodyTabCode);
				if (bodyVos != null && bodyVos.length > 0) {
					if (bodyVos.length < lastrow + 1) {
						return;
					}
				}
				List<Object> grandVOList = mainGrandPanel.getMainGrandAssist().getGrandCardDataByMainRow(rowChangeBean,
						relationShip, grandValidationService, filter);
				if (grandVOList != null && grandVOList.size() > 0) {
					setGrandToFormStausIsEdit(grandBillForm, grandTabCode, grandVOList);
				} else {
					grandBillCardPanel.getBillModel(grandTabCode).clearBodyData();
				}
			}
		}
	}

	/**
	 * 编辑状态将孙表VO设置到界面
	 */
	private static void setGrandToFormStausIsEdit(BillForm grandBillForm, String grandTabCode, List<Object> grandVOList) {
		grandBillForm.getBillCardPanel().getBillData()
				.setBodyValueVO(grandTabCode, grandVOList.toArray(new SuperVO[0]));
		BillModel billModel = grandBillForm.getBillCardPanel().getBillModel();
		int rowCount = billModel.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			SuperVO vo = (SuperVO) grandVOList.get(i);
			int voStatus = vo.getStatus();
			if (voStatus == VOStatus.NEW) {
				billModel.setRowState(i, BillModel.ADD);
			} else if (voStatus == VOStatus.UPDATED) {
				billModel.setRowState(i, BillModel.MODIFICATION);
			} else {
				billModel.setRowState(i, BillModel.NORMAL);
			}
		}
		CardPanelEventUtil.execlFormula(grandBillForm, grandTabCode);

		// ssx add for append user define REFs
		// on 2019-03-06
		UserDefineRefUtils.refreshBillCardGrandDefRefs(grandBillForm, grandTabCode, grandVOList);
		//
	}

	/**
	 * 新增态的行切换事件
	 */
	public static void rowChangeStateIsAdd(CardGrandPanelComposite mainGrandPanel, CardBodyRowChangedEvent event,
			IGrandValidationService grandValidationService, MainGrandBlankFilter filter) {
		int lastrow = event.getOldRow();
		BillCardPanel mainBillCardPanel = ((BillForm) mainGrandPanel.getMainPanel()).getBillCardPanel();
		int currentRow = event.getRow();
		if (currentRow >= 0) {
			String currentbodyTabCode = mainBillCardPanel.getCurrentBodyTableCode();
			MainGrandRelationShip relationShip = mainGrandPanel.getMaingrandrelationship();
			// 根据当前子页签获取孙表的显示控件
			BillForm grandBillForm = (BillForm) relationShip.getBodyTabTOGrandCardComposite().get(currentbodyTabCode);
			if (grandBillForm == null)
				return;
			RowChangeBean rowChangeBean = setChangeRowInfo(mainGrandPanel, lastrow, currentRow, grandBillForm);
			CircularlyAccessibleValueObject[] bodyVos = MainGrandUtil.getBodyVOsByTabCode(
					(BillForm) mainGrandPanel.getMainPanel(), currentbodyTabCode);
			if (bodyVos != null && bodyVos.length > 0) {
				if (bodyVos.length < lastrow + 1) {
					return;
				}
			}
			// 获取孙表信息VO数据
			Map<String, ArrayList<Object>> grandAllVOMap = mainGrandPanel.getMainGrandAssist()
					.getGrandCardDataByMainRowAdd(rowChangeBean, mainGrandPanel.getMaingrandrelationship(),
							grandValidationService, filter);
			String[] tabCodes = grandBillForm.getBillCardPanel().getBillData().getBodyTableCodes();
			for (String grandTabcode : tabCodes) {
				ArrayList<Object> grandVOList = grandAllVOMap.get(grandTabcode);
				if (grandVOList != null) {
					grandBillForm.getBillCardPanel().getBillData()
							.setBodyValueVO(grandTabcode, grandVOList.toArray(new SuperVO[0]));
					CardPanelEventUtil.execlFormula(grandBillForm, grandTabcode);
				} else {
					grandBillForm.getBillCardPanel().getBillModel(grandTabcode).clearBodyData();
				}
			}
			grandAutoAddLine(mainGrandPanel, grandBillForm, grandAllVOMap);
		}
	}

	/**
	 * 孙表自增行
	 */
	private static void grandAutoAddLine(CardGrandPanelComposite mainGrandPanel, BillForm grandBillForm,
			Map<String, ArrayList<Object>> grandAllVOMap) {

		String currentTabCode = grandBillForm.getBillCardPanel().getCurrentBodyTableCode();
		List<Object> grandVOList = grandAllVOMap.get(currentTabCode);

		boolean isAutoGrandAddLine = mainGrandPanel.getModel().isGrandAutoAddLine();

		if ((grandVOList == null || grandVOList.size() == 0) && isAutoGrandAddLine) {

			mainGrandPanel.getMainGrandAssist().grandAddLine(grandBillForm);
		}
	}

	/**
	 * 非编辑态的行切换事件
	 */
	public static void rowChangeStateIsnoEdit(CardGrandPanelComposite mainGrandPanel, CardBodyRowChangedEvent event) {

		int currentRow = ((BillForm) mainGrandPanel.getMainPanel()).getBillCardPanel().getBillTable().getSelectedRow();
		String currentbodyTabCode = ((BillForm) mainGrandPanel.getMainPanel()).getBillCardPanel()
				.getCurrentBodyTableCode();
		// 根据当前子页签获取孙表对应的控件
		BillForm grandBillForm = (BillForm) mainGrandPanel.getMaingrandrelationship().getBodyTabTOGrandCardComposite()
				.get(currentbodyTabCode);
		if (grandBillForm != null) {
			String grandTabCode = grandBillForm.getBillCardPanel().getCurrentBodyTableCode();

			RowChangeBean rowChangeBean = setChangeRowInfo(mainGrandPanel, -1, currentRow, grandBillForm);

			List<Object> grandVOList = mainGrandPanel.getMainGrandAssist().showGrandCardDataByMainRow(rowChangeBean,
					mainGrandPanel.getMaingrandrelationship());
			if (grandVOList != null) {
				grandBillForm.getBillCardPanel().getBillData()
						.setBodyValueVO(grandTabCode, grandVOList.toArray(new SuperVO[0]));
				CardPanelEventUtil.execlFormula(grandBillForm, grandTabCode);
			} else {
				grandBillForm.getBillCardPanel().getBillModel(grandTabCode).clearBodyData();
			}
		}
	}

	/**
	 * 列表只有一条数据（孙表有数据）的时候，在卡片态将这条数据删除,不会对孙表覆盖
	 */
	public static void fixLastRowChange(CardGrandPanelComposite mainGrandPanel) {
		int rowCount = mainGrandPanel.getModel().getMainModel().getRowCount();
		if (rowCount == 0) {
			mainGrandPanel.getModel().getQueryDataMap().clear();
		}
	}

	/**
	 * 卡片孙表模型初始化数据信息
	 * 
	 */
	public static void grandModelInit(CardGrandPanelComposite mainGrandPanel) {
		// 获取聚合VO
		BillForm mainForm = (BillForm) mainGrandPanel.getMainPanel();
		String currentbodyTabCode = mainForm.getBillCardPanel().getCurrentBodyTableCode();
		CircularlyAccessibleValueObject[] childBodyVos = MainGrandUtil
				.getBodyVOsByTabCode(mainForm, currentbodyTabCode);
		if (childBodyVos == null || childBodyVos.length == 0) {
			return;
		}
		Object grandBillFormObj = mainGrandPanel.getMaingrandrelationship().getBodyTabTOGrandCardComposite()
				.get(currentbodyTabCode);
		if (grandBillFormObj != null) {
			BillForm grandBillForm = (BillForm) grandBillFormObj;
			int currentRow = ((BillForm) mainGrandPanel.getMainPanel()).getBillCardPanel().getBillTable()
					.getSelectedRow();
			if (currentRow < 0) {
				currentRow = setBodyRowSelectionInterval(mainGrandPanel);
			}
			if (currentRow >= 0) {
				SuperVO lastBodyVO = (SuperVO) childBodyVos[currentRow];
				String grandTabCode = grandBillForm.getBillCardPanel().getCurrentBodyTableCode();
				String uniqueCardKey = currentbodyTabCode + lastBodyVO.getPrimaryKey() + grandTabCode;
				ArrayList<Object> grandObjectList = mainGrandPanel.getModel().getQueryDataMap().get(uniqueCardKey);
				if (grandObjectList != null) {
					grandBillForm.getBillCardPanel().getBillData()
							.setBodyValueVO(grandTabCode, grandObjectList.toArray(new SuperVO[0]));

					// ssx add for append user define REFs
					// on 2019-03-06
					UserDefineRefUtils.refreshBillCardGrandDefRefs(grandBillForm, grandTabCode, grandObjectList);
					//

					setGrandToFormStausIsEdit(grandBillForm, grandTabCode, grandObjectList);
				} else {
					grandBillForm.getBillCardPanel().getBillData().clearViewData();
				}
			} else {
				grandBillForm.getBillCardPanel().getBillData().clearViewData();
			}
		}
	}

	/**
	 * 列表孙表模型初始化数据信息
	 * 
	 */
	public static void grandListModelInit(ListGrandPanelComposite mainGrandPanel) {
		// ssx remarked for HongFa, requirements do not need to show the first
		// row on init
		// Modfied on 2019-03-06
		// BillListView mainListPanel = (BillListView)
		// mainGrandPanel.getMainPanel();
		// String currentbodyTabCode =
		// mainListPanel.getBillListPanel().getBodyTabbedPane().getSelectedTableCode();
		// BillListView grandListPanel = (BillListView)
		// mainGrandPanel.getMaingrandrelationship()
		// .getBodyTabTOGrandListComposite().get(currentbodyTabCode);
		// if (grandListPanel != null) {
		// String className =
		// MainGrandUtil.getClassNameByListTabCode(mainListPanel,
		// currentbodyTabCode);
		// if (className == null) {
		// return;
		// }
		//
		// AbstractBill aggVO = (AbstractBill)
		// (mainListPanel).getModel().getSelectedData();
		// if (null != aggVO) {
		// CircularlyAccessibleValueObject[] childBodyVos =
		// aggVO.getTableVO(currentbodyTabCode);
		// if (childBodyVos != null && childBodyVos.length > 0) {
		// getGrand4ChildIsFirstLine(mainGrandPanel, currentbodyTabCode,
		// childBodyVos);
		// }
		// }
		// }
	}

	/**
	 * 默认显示第一行
	 */
	private static void getGrand4ChildIsFirstLine(ListGrandPanelComposite mainGrandPanel, String currentbodyTabCode,
			CircularlyAccessibleValueObject[] childBodyVos) {

		BillListView grandListPanel = (BillListView) mainGrandPanel.getMaingrandrelationship()
				.getBodyTabTOGrandListComposite().get(currentbodyTabCode);
		// setListSelectionInterval(mainGrandPanel);
		SuperVO lastBodyVO = (SuperVO) childBodyVos[0];
		String grandTabCode = grandListPanel.getBillListPanel().getBodyTabbedPane().getSelectedTableCode();
		String uniqueCardKey = currentbodyTabCode + lastBodyVO.getPrimaryKey() + grandTabCode;
		ArrayList<Object> grandObjectList = mainGrandPanel.getModel().getQueryDataMap().get(uniqueCardKey);
		if (grandObjectList != null) {
			grandListPanel.getBillListPanel().getBodyBillModel().setBodyDataVO(grandObjectList.toArray(new SuperVO[0]));
			CardPanelEventUtil.execlListFormula(grandListPanel, grandTabCode);

			// ssx add for append user define REFs
			// on 2019-03-06
			if (((BillListView) mainGrandPanel.getMainPanel()).getModel().getSelectedData() != null) {
				UserDefineRefUtils.refreshBillListGrandDefRefs(grandListPanel, grandObjectList);
			}
			//
		} else {
			grandListPanel.getBillListPanel().getBodyBillModel().clearBodyData();
		}
	}

	/**
	 * 列表界面设置首先行
	 */
	private static void setListSelectionInterval(ListGrandPanelComposite mainGrandPanel) {

		BillListView mainListPanel = (BillListView) mainGrandPanel.getMainPanel();

		ListSelectionModel selectedModel = mainListPanel.getBillListPanel().getBodyTable().getSelectionModel();

		selectedModel.setSelectionInterval(0, 0);
	}

	/**
	 * 设置选择行为0行
	 */
	public static int setBodyRowSelectionInterval(GrandPanelComposite mainGrandPanel) {

		int newSelectedRow = -1;

		BillForm mainForm = (BillForm) mainGrandPanel.getMainPanel();
		String tabCode = mainForm.getBillCardPanel().getCurrentBodyTableCode();

		CircularlyAccessibleValueObject[] childVos = MainGrandUtil.getBodyVOsByTabCode(mainForm, tabCode);

		if (childVos != null && childVos.length != 0) {

			ListSelectionModel selectedModel = ((BillForm) mainGrandPanel.getMainPanel()).getBillCardPanel()
					.getBillTable().getSelectionModel();
			selectedModel.setSelectionInterval(0, 0);
			newSelectedRow = mainForm.getBillCardPanel().getBillTable().getSelectedRow();
		}
		return newSelectedRow;
	}

	/**
	 * 组织变化的事件操作
	 * 
	 * @param mainPanel
	 */
	public static void orgChangeProcess(CardGrandPanelComposite mainPanel) {

		String currentbodyTabCode = ((BillForm) mainPanel.getMainPanel()).getBillCardPanel().getCurrentBodyTableCode();
		BillForm grandBillForm = (BillForm) mainPanel.getMaingrandrelationship().getBodyTabTOGrandCardComposite()
				.get(currentbodyTabCode);
		if (grandBillForm == null) {
			return;
		}
		if (StringUtils.isBlank(mainPanel.getModel().getMainModel().getContext().getPk_org())
				&& mainPanel.getModel().getMainModel().getUiState() == UIState.ADD) {
			grandBillForm.setEditable(false);
			grandBillForm.getBillCardPanel().getBillData().clearViewData();
		} else {
			if (mainPanel.getModel().getMainModel().getUiState() == UIState.ADD) {
				grandBillForm.setEditable(true);
			} else {
				grandBillForm.setEditable(false);
			}
		}
	}

	/**
	 * 选择一个主表则加载其对应的子表和孙表数据
	 */
	public static AbstractBill selectedDataChange(GrandPanelComposite mainPanel) {

		AbstractBill aggVoWithGrand = null;

		Component mainForm = mainPanel.getMainPanel();
		// 取到没有孙表的数据
		AbstractBill aggVoWithoutGrand = null;
		if (mainForm instanceof BillListView) {

			BillListView listView = (BillListView) mainForm;
			aggVoWithoutGrand = (AbstractBill) listView.getModel().getSelectedData();
		} else if (mainForm instanceof BillForm) {

			BillForm formView = (BillForm) mainForm;
			aggVoWithoutGrand = (AbstractBill) formView.getModel().getSelectedData();
		}
		if (aggVoWithoutGrand != null) {
			aggVoWithGrand = getFullAggVo(mainPanel, aggVoWithoutGrand);
		}
		return aggVoWithGrand;
	}

	/**
	 * 远程调用取一个完整的聚合VO
	 */
	private static AbstractBill getFullAggVo(GrandPanelComposite mainPanel, AggregatedValueObject aggVoWithoutGrand) {

		AbstractBill aggVoWithGrand = null;
		IGrandAggVosQueryService queryService = NCLocator.getInstance().lookup(IGrandAggVosQueryService.class);

		MainGrandRelationShip maingrandrelationship = mainPanel.getMaingrandrelationship();
		// 取到孙表数据
		aggVoWithGrand = queryService.getAllListGrandVOS(aggVoWithoutGrand,
				(HashMap<String, CircularlyAccessibleValueObject>) maingrandrelationship.getGrandTabAndVOMap());
		return aggVoWithGrand;
	}

	/**
	 * 构造行切换事件对象
	 */
	private static RowChangeBean setChangeRowInfo(CardGrandPanelComposite mainGrandPanel, int lastrow, int currentRow,
			BillForm grandBillForm) {

		RowChangeBean rowChangeBean = new RowChangeBean();
		rowChangeBean.setMainForm((BillForm) mainGrandPanel.getMainPanel());
		rowChangeBean.setGrandForm(grandBillForm);
		rowChangeBean.setCurrentRow(currentRow);
		rowChangeBean.setLastRow(lastrow);
		return rowChangeBean;
	}

	// 执行加载公式
	public static void execlFormula(BillForm billform, String tableCode) {
		billform.getBillCardPanel().getBillModel(tableCode).loadLoadRelationItemValue();
		billform.getBillCardPanel().getBillModel(tableCode).execLoadFormula();
	}

	// 执行列表加载公式
	public static void execlListFormula(BillListView billlistView, String tableCode) {
		billlistView.getBillListPanel().getBodyBillModel(tableCode).loadLoadRelationItemValue();
		billlistView.getBillListPanel().getBodyBillModel(tableCode).execLoadFormula();
	}

}
