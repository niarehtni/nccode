package nc.ui.pubapp.uif2app.components.grand;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import nc.bs.pubapp.utils.UserDefineRefUtils;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pubapp.uif2app.components.grand.action.ExpendShrinkGrandListAction;
import nc.ui.pubapp.uif2app.components.grand.model.MainGrandAppEvent;
import nc.ui.pubapp.uif2app.components.grand.model.MainGrandEventTypeEnum;
import nc.ui.pubapp.uif2app.components.grand.model.MainGrandStatusTypeEnum;
import nc.ui.pubapp.uif2app.components.grand.util.CardPanelEventUtil;
import nc.ui.pubapp.uif2app.event.list.ListBodyRowChangedEvent;
import nc.ui.pubapp.uif2app.event.list.ListBodyTabChangedEvent;
import nc.ui.pubapp.uif2app.event.list.ListHeadRowChangedEvent;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.AutoShowUpEventSource;
import nc.ui.uif2.components.IAutoShowUpEventListener;
import nc.ui.uif2.editor.BillListView;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.uif2.AppStatusRegisteryCallback;

/**
 * 主子孙列表界面
 */
public class ListGrandPanelComposite extends GrandPanelComposite {
	private static final long serialVersionUID = 1480002637488567694L;

	// 列表展开收缩Action
	private ExpendShrinkGrandListAction expendShrinkGrandListAction = null;

	public JPanel switchGrandPanel;

	protected ListGrandPanel expendPane;

	private AutoShowUpEventSource autoShowUpComponent = new AutoShowUpEventSource(this);

	@Override
	public void initUI() {
		// 读取缓存信息
		this.readListCacheInfo();
		// 构造主子孙界面
		super.initUI();
		// 添加监听
		this.initListListener();
		// 设置辅助类主子孙model
		this.getMainGrandAssist().setMainGrandModel(this.getModel());
		// 设置回调方法(用于记录上次打开界面状态)
		this.registeCallbak();
	}

	/**
	 * 读取列表缓存信息
	 */
	public void readListCacheInfo() {
		int lastsize = this.getLastSize();
		if (lastsize == 0 || lastsize < 0) {
			this.getModel().setMainGrandListEnumState(MainGrandStatusTypeEnum.list_main_show_grand_hide);
			this.getListCardSplitPane().setEnabled(false);
		} else {
			this.getModel().setMainGrandListEnumState(MainGrandStatusTypeEnum.list_main_show_grand_show);
			this.getListCardSplitPane().setDividerLocation(lastsize);
			this.setCurrentLocation(lastsize);
			if (this.getModel().getListGrandEnumState() == MainGrandStatusTypeEnum.list_grand_hide) {
				this.getListCardSplitPane().setEnabled(false);
			} else {
				this.getListCardSplitPane().setEnabled(true);
			}
		}
	}

	@Override
	public Component getGrandPanel() {
		if (this.expendPane != null) {
			if (this.expendPane.getGrandComoPanel() != null
					&& this.expendPane.getGrandComoPanel().getRightComponent() != null) {
				if (this.getModel().getListGrandEnumState() == MainGrandStatusTypeEnum.list_grand_show) {
					// 如果model当前孙表的状态为存在的情况、则把孙表设置为true
					this.expendPane.getGrandComoPanel().getRightComponent().setVisible(true);
				} else {
					this.expendPane.getGrandComoPanel().getRightComponent().setVisible(false);
				}
			}
		} else {
			// 从XML中读取信息
			this.readListGrandConfigXML();
			// 判断列表下的显示状态、如果为0标识不显示列表状态下孙表信息
			if (this.getModel().getListGrandEnumState() == MainGrandStatusTypeEnum.list_grand_hide) {
				this.expendPane.getGrandComoPanel().getRightComponent().setVisible(false);

				this.listPanelBeforeResize();
			} else {
				this.expendPane.getGrandComoPanel().getRightComponent().setVisible(true);
			}
		}

		return this.expendPane;
	}

	/**
	 * 读取配置文件XML中关于列表的信息
	 */
	public void readListGrandConfigXML() {
		Map<String, Object> panelmap = this.getMaingrandrelationship().getBodyTabTOGrandListComposite();

		Iterator<String> keys = panelmap.keySet().iterator();
		this.switchGrandPanel = new UIPanel();
		this.switchGrandPanel.setLayout(new CardLayout());

		this.expendPane = new ListGrandPanel();
		this.expendPane.setGrandlabel(new JLabel(this.getGrandString()));
		this.expendPane.setExpendShrinkAction(this.expendShrinkGrandListAction);

		this.getModel().addAppEventListener(this.expendPane);
		// 主子孙model派发事件
		this.getModel().mainListGrandInit();
		JSplitPane splitane = this.expendPane.getGrandComoPanel();
		splitane.setEnabled(false);
		if (this.getModel().getListGrandEnumState() == MainGrandStatusTypeEnum.list_grand_hide) {
			// 如果孙表不显示的情况则置为false
			splitane.setBottomComponent(this.switchGrandPanel);
			splitane.getBottomComponent().setVisible(true);
		} else {
			splitane.setBottomComponent(this.switchGrandPanel);
		}

		while (keys.hasNext()) {
			String uniquekey = keys.next();
			Object component = panelmap.get(uniquekey);
			Object addcomponent = null;
			if (component instanceof BillListView) {
				addcomponent = ((BillListView) component).getBillListPanel();
				// 给列表页签切换添加监听
				((BillListView) component).getBillListPanel().getBodyTabbedPane().addChangeListener(this.getMediator());
			} else if (component instanceof BillForm) {
				addcomponent = ((BillForm) component).getBillCardPanel();
			}
			this.switchGrandPanel.add((Component) addcomponent, uniquekey);
		}
	}

	/**
	 * 初始化主子孙面板列表信息
	 */
	protected void initListListener() {
		// 对主子model添加监听
		((BillListView) this.mainPanel).getModel().addAppEventListener(this);
	}

	@Override
	public void paint(Graphics g) {

		super.paint(g);

		String currentBodyTab = ((BillListView) this.getMainPanel()).getBillListPanel().getChildListPanel()
				.getTableCode();
		Component grandComposite = (Component) this.getMaingrandrelationship().getBodyTabTOGrandCardComposite()
				.get(currentBodyTab);
		if (this.getGrandPanel() != null && grandComposite != null && isNeedToRepaint()) {

			if (this.getModel().getListGrandEnumState() == MainGrandStatusTypeEnum.list_grand_hide) {

				setGrandHideStatus(true);

			} else if (this.getModel().getCardGrandEnumState() == MainGrandStatusTypeEnum.card_grand_show) {

				if (getCurrentLocation() < 1 && getCurrentLocation() > 0.9) {

					this.getListCardSplitPane().setDividerLocation(0.7);
				} else {
					// 非比例值时
					this.getListCardSplitPane().setDividerLocation((int) getCurrentLocation());
				}
			}
			this.setNeedToRepaint(false);
		}
	}

	/**
	 * 收缩孙视图列表方法
	 */
	public void shrinkGrandComboList() {
		((ListGrandPanel) this.getGrandPanel()).shrinkGrandListComboPanel();
		this.setCurrentLocation(this.getListCardSplitPane().getDividerLocation());
		this.setGrandPanelMin();
	}

	/**
	 * 孙面板最大化
	 */
	@Override
	public boolean setGrandPanelMax() {
		boolean ret = false;
		if (this.getModel().getListGrandEnumState() != MainGrandStatusTypeEnum.list_grand_hide) {// 如果不为显示状态则不进行显示
			this.getListCardSplitPane().setResizeWeight(0);
			this.getListCardSplitPane().setDividerLocation(0);
			this.getListCardSplitPane().resetToPreferredSizes();
		}
		ret = true;
		return ret;
	}

	@Override
	public void showMeUp() {
		// 在转单后数据处理界面，返回按钮在编辑态也是可见的，所以在返回切会列表界面的时候
		// 要把UI状态修改成非编辑态，否则界面的按钮显示会出现问题。
		if (((BillListView) this.getMainPanel()).getModel() != null
				&& ((BillListView) this.getMainPanel()).getModel().getUiState() != UIState.NOT_EDIT) {
			((BillListView) this.getMainPanel()).getModel().setUiState(UIState.NOT_EDIT);
		}
		if (!this.getModel().isAddReturnList()) {// 如果是编辑返回，则进行一次事件转发
			// 触发行改变事件
			int currentRow = ((BillListView) this.mainPanel).getBillListPanel().getBodyTable().getSelectedRow();
			ListBodyRowChangedEvent event = new ListBodyRowChangedEvent(
					((BillListView) this.mainPanel).getBillListPanel(), new BillEditEvent(this, currentRow, currentRow));
			this.getModel().getMainModel().fireEvent(event);
		}
		this.autoShowUpComponent.showMeUp();
	}

	/**
	 * 获取客户端的打开大小
	 * 
	 * @return
	 */
	@Override
	public Integer getLastSize() {
		int d = 0;

		BillManageModel mainmodel = (BillManageModel) ((BillListView) this.getMainPanel()).getModel();

		Object statusObj = mainmodel.getContext().getStatusRegistery().getAppStatusObject(getListCacheId());
		if (statusObj == null || !(statusObj instanceof HashMap)) {
			return 0;
		}
		d = (Integer) ((HashMap) statusObj).get("Size");
		return d;
	}

	/**
	 * 存储客户端列表上一次大小状态
	 */
	protected void registeCallbak() {
		BillManageModel mainmodel = ((BillListView) this.getMainPanel()).getModel();
		if (mainmodel.getContext() == null || mainmodel.getContext().getStatusRegistery() == null) {
			return;
		}
		mainmodel.getContext().getStatusRegistery().addCallback(new AppStatusRegisteryCallback() {
			@Override
			public Object getID() {
				return getListCacheId();
			}

			@Override
			public Object getStatusObject() {
				Map<Object, Object> map = new HashMap<Object, Object>();
				map.put("Size", ListGrandPanelComposite.this.getListCardSplitPane().getDividerLocation());
				return map;
			}
		});

	}

	private String getListCacheId() {
		String userId = WorkbenchEnvironment.getInstance().getLoginUser().getCuserid();
		String funCode = this.getModel().getMainModel().getContext().getFuncInfo().getFuncode();
		return userId + funCode + "ListGrand_Size";
	}

	// 列表事件处理
	@SuppressWarnings("unchecked")
	@Override
	public void handleEvent(AppEvent event) {

		if (event instanceof ListBodyRowChangedEvent) {

			this.bodyRowChangeProcess();

		} else if (event instanceof ListBodyTabChangedEvent) {

			this.tabChangeProcess();

		} else if (event instanceof MainGrandAppEvent) {

			this.grandEventProcess(event);

		} else if (event instanceof ListHeadRowChangedEvent) {

			// 列表表头行切换时的处理(清空显示的孙表表体数据)
			this.getModel().setAddReturnList(true);
			this.clearGrandData();
		} else if (event.getType().equals(AppEventConst.MODEL_INITIALIZED)) {
			this.clearGrandData();
			this.getModel().getSelectGrandRowNum().clear();
			this.getModel().getQueryDataMap().clear();
			List<Object> datas = this.getModel().getMainModel().getData();
			for (Object obj : datas) {
				CircularlyAccessibleValueObject[] childVOs = ((AggregatedValueObject) obj).getChildrenVO();
				if (childVOs != null) {
					this.getModel().parseGrandData(obj);
				}
			}
		} else if (event.getType().equals(AppEventConst.SELECTED_DATE_CHANGED)) {

			AbstractBill aggVoWihGrand = CardPanelEventUtil.selectedDataChange(this);

			if (aggVoWihGrand != null) {

				this.getModel().parseGrandData(aggVoWihGrand);
			}
		} else if (event.getType().equals(AppEventConst.SELECTION_CHANGED) && this.isShowing()) {

			CardPanelEventUtil.grandListModelInit(this);
		}
	}

	/**
	 * 清除孙表数据
	 */
	private void clearGrandData() {

		String currentbodyTabCode = ((BillListView) this.getMainPanel()).getBillListPanel().getChildListPanel()
				.getTableCode();
		// 根据当前子页签获取孙表view
		BillListView grandListView = (BillListView) this.getMaingrandrelationship().getBodyTabTOGrandListComposite()
				.get(currentbodyTabCode);
		if (grandListView != null) {
			String[] grandTabCodes = grandListView.getBillListPanel().getBillListData().getBodyTableCodes();
			for (String grandTabcode : grandTabCodes) {
				grandListView.getBillListPanel().getBillListData().getBodyBillModel(grandTabcode).clearBodyData();
			}
		}
	}

	private void grandEventProcess(AppEvent event) {

		if (event.getType().equals(MainGrandEventTypeEnum.grandlist_expend.toString())) {
			// 列表情况下扩展孙表
			this.displayGrandPanel();

		} else if (event.getType().equals(MainGrandEventTypeEnum.grandlist_shrink.toString())) {
			// 列表情况下收缩孙表
			this.setGrandPanelMin();

		} else if (event.getType().equals(MainGrandEventTypeEnum.grand_hide.toString())) {
			// 列表情况下隐藏孙表
			this.hideGrandPanel();

		} else if (event.getType().equals(MainGrandEventTypeEnum.grand_display.toString())) {
			// 列表情况下显示孙表
			this.displayGrandPanel();

		} else if (event.getType().equals(MainGrandEventTypeEnum.grandlist_max.toString())) {
			// 列表情况下最大化孙表
			this.setGrandPanelMax();

		} else if (event.getType().equals(MainGrandEventTypeEnum.grandlist_min.toString())) {
			// 列表情况还原孙表(设置默认分割线大小)
			this.resizeGrandPanel();
		}
	}

	/*
	 * 当子页签切换的时候加载对应的孙表模板以及对应的数据
	 */
	private void tabChangeProcess() {
		// 获得当前选中行
		int currentRow = ((BillListView) this.mainPanel).getBillListPanel().getBodyTable().getSelectedRow();
		String currentbodyTabCode = ((BillListView) this.getMainPanel()).getBillListPanel().getChildListPanel()
				.getTableCode();
		// 根据当前子页签获取孙表view
		BillListView grandListView = (BillListView) this.getMaingrandrelationship().getBodyTabTOGrandListComposite()
				.get(currentbodyTabCode);
		// 加载孙模板
		this.getMainGrandAssist().updateGrandListByBodyTab(this);
		if (grandListView != null && currentRow != -1) {
			// 加载孙面板数据
			AbstractBill aggVO = (AbstractBill) ((BillListView) this.getMainPanel()).getModel().getSelectedData();
			String bodyClassName = aggVO.getChildrenVO()[0].getClass().getName();
			List<Object> grandVOList = this.getMainGrandAssist().getGrandListDataByMainRow(
					(BillListView) this.mainPanel, currentRow, grandListView, bodyClassName,
					this.getMaingrandrelationship());
			if (grandVOList != null) {
				// 孙面板加载数据并显示
				grandListView.getBillListPanel().getBodyBillModel().setBodyDataVO(grandVOList.toArray(new SuperVO[0]));
				grandListView.getBillListPanel().getBodyBillModel().loadLoadRelationItemValue();
				grandListView.getBillListPanel().getBodyBillModel().execLoadFormula();

				// ssx add for append user define REFs
				// on 2019-03-06
				UserDefineRefUtils.refreshBillListGrandDefRefs(grandListView, grandVOList);
				//
			} else {
				grandListView.getBillListPanel().getBodyBillModel().clearBodyData();
			}
		}
	}

	private void bodyRowChangeProcess() {
		// 获得当前选中行
		int currentRow = ((BillListView) this.mainPanel).getBillListPanel().getBodyTable().getSelectedRow();
		String currentbodyTabCode = ((BillListView) this.getMainPanel()).getBillListPanel().getChildListPanel()
				.getTableCode();
		// 根据当前子页签获取孙表view
		BillListView grandListView = (BillListView) this.getMaingrandrelationship().getBodyTabTOGrandListComposite()
				.get(currentbodyTabCode);
		if (grandListView != null) {
			AbstractBill aggVO = (AbstractBill) ((BillListView) this.getMainPanel()).getModel().getSelectedData();
			if (aggVO != null && null != aggVO.getChildrenVO() && aggVO.getChildrenVO().length != 0) {
				String bodyClassName = aggVO.getChildrenVO()[0].getClass().getName();
				List<Object> grandVOList = this.getMainGrandAssist().getGrandListDataByMainRow(
						(BillListView) this.mainPanel, currentRow, grandListView, bodyClassName,
						this.getMaingrandrelationship());
				if (grandVOList != null && grandVOList.size() != 0) {
					grandListView.getBillListPanel().getBodyBillModel()
							.setBodyDataVO(grandVOList.toArray(new SuperVO[0]));
					grandListView.getBillListPanel().getBodyBillModel().loadLoadRelationItemValue();
					grandListView.getBillListPanel().getBodyBillModel().execLoadFormula();

					// ssx add for append user define REFs
					// on 2019-03-06
					UserDefineRefUtils.refreshBillListGrandDefRefs(grandListView, grandVOList);
					//
				} else {
					String[] grandTabCodes = grandListView.getBillListPanel().getBillListData().getBodyTableCodes();
					for (String grandTabcode : grandTabCodes) {
						grandListView.getBillListPanel().getBillListData().getBodyBillModel(grandTabcode)
								.clearBodyData();
					}
				}
			}
		}
	}

	@Override
	public void setAutoShowUpEventListener(IAutoShowUpEventListener l) {
		this.autoShowUpComponent.setAutoShowUpEventListener(l);
	}

	@Override
	public boolean isDisplyGrandPanel() {
		return this.getGrandPanel().isVisible();
	}

	public ExpendShrinkGrandListAction getExpendShrinkGrandListAction() {
		return this.expendShrinkGrandListAction;
	}

	public void setExpendShrinkGrandListAction(final ExpendShrinkGrandListAction expendShrinkGrandListAction) {
		this.expendShrinkGrandListAction = expendShrinkGrandListAction;
	}
}
