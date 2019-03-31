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

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pubapp.uif2app.components.grand.action.ExpendShrinkGrandCardAction;
import nc.ui.pubapp.uif2app.components.grand.model.DefaultGrandValidationService;
import nc.ui.pubapp.uif2app.components.grand.model.IGrandValidationService;
import nc.ui.pubapp.uif2app.components.grand.model.MainGrandAppEvent;
import nc.ui.pubapp.uif2app.components.grand.model.MainGrandEventTypeEnum;
import nc.ui.pubapp.uif2app.components.grand.model.MainGrandStatusTypeEnum;
import nc.ui.pubapp.uif2app.components.grand.util.CardPanelEventUtil;
import nc.ui.pubapp.uif2app.components.grand.valueStrategy.AbstractGrandComponentValueAdapter;
import nc.ui.pubapp.uif2app.components.grand.valueStrategy.MainGrandNotDelValueAdapter;
import nc.ui.pubapp.uif2app.event.OrgChangedEvent;
import nc.ui.pubapp.uif2app.event.card.BodyRowEditType;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeTabChangeEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyRowChangedEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyRowEditEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyTabChangedEvent;
import nc.ui.pubapp.uif2app.event.list.ListHeadDoubleClickEvent;
import nc.ui.pubapp.uif2app.event.list.ListHeadRowChangedEvent;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.AutoShowUpEventSource;
import nc.ui.uif2.components.IAutoShowUpEventListener;
import nc.ui.uif2.editor.BillListView;
import nc.ui.uif2.editor.value.IComponentValueStrategy;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.uif2.AppStatusRegisteryCallback;

/**
 * 主子孙卡片界面
 */
public class CardGrandPanelComposite extends GrandPanelComposite {

	private static final long serialVersionUID = 3909667539006135048L;

	private ExpendShrinkGrandCardAction expendShrinkGrandCardAction = null;

	private JSplitPane headpanelcombo;

	protected CardGrandPanel expendPane;

	public JPanel switchGrandCardPanel;

	private boolean grandAutoAddLine = false;

	// 孙表校验
	private IGrandValidationService grandValidationService = new DefaultGrandValidationService();

	// 空行过滤类
	private MainGrandBlankFilter mainGrandBlankFilter;

	private AutoShowUpEventSource autoShowUpComponent = new AutoShowUpEventSource(this);

	// 取值策略
	protected IComponentValueStrategy componentValueManager;

	@Override
	public void setAutoShowUpEventListener(IAutoShowUpEventListener l) {
		this.autoShowUpComponent.setAutoShowUpEventListener(l);
	}

	@Override
	public void showMeUp() {

		this.getModel().mainCardGrandInit();

		this.autoShowUpComponent.showMeUp();

		showGranListUp();
	}

	/**
	 * 设置表体行显示孙表信息
	 */
	private void showGranListUp() {
		int currentRow = ((BillForm) this.mainPanel).getBillCardPanel().getBillTable().getSelectedRow();

		if (currentRow < 0) {
			currentRow = CardPanelEventUtil.setBodyRowSelectionInterval(this);
		}

		CardBodyRowChangedEvent event = new CardBodyRowChangedEvent(((BillForm) this.mainPanel).getBillCardPanel(),
				new BillEditEvent(this, currentRow, currentRow));
		this.getModel().getMainModel().fireEvent(event);
	}

	@Override
	public void initUI() {
		// 从缓存中读取卡片信息
		this.readCardCacheInfo();
		// 构造主子孙界面
		super.initUI();

		this.initCardListener();
		// 设置辅助类主子孙model
		this.getMainGrandAssist().setMainGrandModel(this.getModel());

		this.registeCallbak();
		// 构造孙面板伪列
		this.getMainGrandAssist().constructPreColumBillItem((BillForm) this.getMainPanel());
		this.getModel().setMaingrandrelationship(this.getMaingrandrelationship());
	}

	/**
	 * 读取卡片缓存信息
	 */
	public void readCardCacheInfo() {
		int lastsize = this.getLastSize();
		if (lastsize == 0 || lastsize < 0) {
			this.getModel().setMainGrandCardEnumState(MainGrandStatusTypeEnum.card_main_show_grand_hide);
			this.getListCardSplitPane().setEnabled(false);
		} else {
			this.getModel().setMainGrandCardEnumState(MainGrandStatusTypeEnum.card_main_show_grand_show);
			this.getListCardSplitPane().setDividerLocation(lastsize);
			this.setCurrentLocation(lastsize);
			if (this.getModel().getCardGrandEnumState() == MainGrandStatusTypeEnum.card_grand_hide) {
				this.getListCardSplitPane().setEnabled(false);
			} else {
				this.getListCardSplitPane().setEnabled(true);
			}
		}
	}

	/**
	 * 孙面板最大化
	 */
	@Override
	public boolean setGrandPanelMax() {
		boolean ret = false;
		if (this.getModel().getCardGrandEnumState() == MainGrandStatusTypeEnum.card_grand_max) {
			this.getListCardSplitPane().setResizeWeight(0);
			float currentLocation = this.getListCardSplitPane().getDividerLocation();
			float mainPanelSize = this.getHeight();
			this.setCurrentLocation(currentLocation / mainPanelSize);
			this.getListCardSplitPane().setDividerLocation(0);
			this.getListCardSplitPane().resetToPreferredSizes();
			ret = true;
		}
		return ret;
	}

	/**
	 * 获取客户端的打开大小
	 * 
	 * @return
	 */
	@Override
	public Integer getLastSize() {
		int d = 0;

		BillManageModel mainmodel = (BillManageModel) ((BillForm) this.getMainPanel()).getModel();

		Object statusObj = mainmodel.getContext().getStatusRegistery().getAppStatusObject(getGrandCacheId());
		if (statusObj == null || !(statusObj instanceof HashMap)) {
			return 0;
		}
		d = (Integer) ((HashMap) statusObj).get("Size");
		return d;
	}

	/**
	 * 获取孙面板
	 */
	@Override
	public Component getGrandPanel() {
		if (this.expendPane != null) {
			if (this.expendPane.getGrandComoPanel() != null
					&& this.expendPane.getGrandComoPanel().getRightComponent() != null) {
				if (this.getModel().getCardGrandEnumState() == MainGrandStatusTypeEnum.card_grand_show) {
					// 如果model当前孙表的状态为存在的情况、则把孙表设置为true
					this.expendPane.getGrandComoPanel().getRightComponent().setVisible(true);
				} else {
					this.expendPane.getGrandComoPanel().getRightComponent().setVisible(false);
				}
				return this.expendPane;
			}

		}

		// 从XML中读取主子孙卡片情况下的配置信息
		this.readCardGrandConfigXML();

		// 判断列表下的显示状态、如果为0标识不显示列表状态下孙表信息
		if (this.getModel().getCardGrandEnumState() == MainGrandStatusTypeEnum.card_grand_hide) {
			this.expendPane.getGrandComoPanel().getRightComponent().setVisible(false);

			this.cardPanelBeforeResize();
		} else {
			this.expendPane.getGrandComoPanel().getRightComponent().setVisible(true);
		}

		return this.expendPane;
	}

	/**
	 * 读取配置文件XML中关于卡片的信息
	 */
	public void readCardGrandConfigXML() {
		Map<String, Object> panelmap = this.getMaingrandrelationship().getBodyTabTOGrandCardComposite();
		Iterator<String> keys = panelmap.keySet().iterator();
		this.switchGrandCardPanel = new UIPanel();
		this.switchGrandCardPanel.setLayout(new CardLayout());

		this.expendPane = new CardGrandPanel();
		this.expendPane.setGrandlabel(new JLabel(this.getGrandString()));
		this.expendPane.setExpendShrinkAction(this.expendShrinkGrandCardAction);

		this.getModel().addAppEventListener(this.expendPane);
		// model发事件
		this.getModel().mainCardGrandInit();

		JSplitPane splitane = this.expendPane.getGrandComoPanel();
		splitane.setEnabled(false);// 设置分割线不能拖动
		if (this.getModel().getCardGrandEnumState() == MainGrandStatusTypeEnum.card_grand_show) {// 如果孙表不显示的情况则置为null
			splitane.setBottomComponent(this.switchGrandCardPanel);
			splitane.getBottomComponent().setVisible(true);
		} else {
			splitane.setBottomComponent(this.switchGrandCardPanel);
		}

		while (keys.hasNext()) {

			String uniquekey = keys.next();
			Object component = panelmap.get(uniquekey);
			Object addcomponent = null;
			if (component instanceof BillListView) {
				addcomponent = ((BillListView) component).getBillListPanel();
			} else if (component instanceof BillForm) {
				addcomponent = ((BillForm) component).getBillCardPanel();
				// 对孙model添加页签切换前以及切换后监听
				((BillForm) component).getBillCardPanel().addTabbedPaneTabChangeListener(this.getMediator(),
						IBillItem.BODY);
				((BillForm) component).getBillCardPanel().addTabbedPaneTabChangeListener2(this.getMediator(),
						IBillItem.BODY);
			}
			this.switchGrandCardPanel.add((Component) addcomponent, uniquekey);
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		String currentBodyTab = ((BillForm) this.getMainPanel()).getBillCardPanel().getCurrentBodyTableCode();
		Component grandComposite = (Component) this.getMaingrandrelationship().getBodyTabTOGrandCardComposite()
				.get(currentBodyTab);
		if (isNeedToPaint()) {
			return;
		}
		if (this.getGrandPanel() != null && grandComposite != null && isNeedToRepaint()) {
			paintGrandList();
			this.setNeedToRepaint(false);
		}
	}

	/**
	 * 孙面板重绘
	 */
	private void paintGrandList() {

		if (this.getModel().getCardGrandEnumState() == MainGrandStatusTypeEnum.card_grand_hide) {

			setGrandHideStatus(false);

		} else if (this.getModel().getCardGrandEnumState() == MainGrandStatusTypeEnum.card_grand_show) {

			if (getCurrentLocation() < 1 && getCurrentLocation() > 0.9) {

				this.getListCardSplitPane().setDividerLocation(0.7);
			} else {
				if (getCurrentLocation() > 1) {
					// 非比例值时
					this.getListCardSplitPane().setDividerLocation((int) getCurrentLocation());
				} else {
					// 比例值时
					this.getListCardSplitPane().setDividerLocation(getCurrentLocation());
				}
			}
		}
	}

	/**
	 * 如果界面最大化，则不需要重绘
	 */
	private boolean isNeedToPaint() {
		boolean flag = false;

		if (this.getModel().getCardHeadStateEnum() == MainGrandStatusTypeEnum.card_head_max
				|| this.getModel().getCardBodyEnumState() == MainGrandStatusTypeEnum.card_body_max
				|| this.getModel().getCardGrandEnumState() == MainGrandStatusTypeEnum.card_grand_max) {
			flag = true;
		}

		return flag;
	}

	/**
	 * 存储客户端上一次卡片界面大小位置
	 */
	protected void registeCallbak() {
		BillManageModel mainmodel = (BillManageModel) ((BillForm) this.getMainPanel()).getModel();
		if (mainmodel.getContext() == null || mainmodel.getContext().getStatusRegistery() == null) {
			return;
		}
		mainmodel.getContext().getStatusRegistery().addCallback(new AppStatusRegisteryCallback() {
			@Override
			public Object getID() {
				return getGrandCacheId();
			}

			@Override
			public Object getStatusObject() {
				Map<Object, Object> cardmap = new HashMap<Object, Object>();
				cardmap.put("Size", CardGrandPanelComposite.this.getListCardSplitPane().getDividerLocation());
				return cardmap;
			}
		});
	}

	private String getGrandCacheId() {
		String userId = WorkbenchEnvironment.getInstance().getLoginUser().getCuserid();
		String funCode = this.getModel().getMainModel().getContext().getFuncInfo().getFuncode();
		return userId + funCode + "CardGrand_Size";
	}

	// 卡片事件处理
	@Override
	public void handleEvent(AppEvent event) {

		UIState mainState = ((BillForm) this.getMainPanel()).getModel().getUiState();

		if (event instanceof MainGrandAppEvent) {

			this.mainGrandProcess(event);

		} else if (event instanceof CardBodyRowChangedEvent) {

			this.onRowChangeHandler(mainState, (CardBodyRowChangedEvent) event, this.grandValidationService);

		} else if (event instanceof CardBodyTabChangedEvent) {

			this.onBodyTabChange(mainState, event);
		} else if (event instanceof CardBodyBeforeTabChangeEvent) {

			this.onBeforeBodyTabChange(mainState);
		} else if (event instanceof CardBodyRowEditEvent) {

			this.onRowEditHandler(event, mainState);
		} else if (event instanceof ListHeadRowChangedEvent) {

			this.onHeadRowChange(mainState);
		} else if (event instanceof ListHeadDoubleClickEvent) {

			this.onHeadDoubleClick(mainState);

		} else if (event.getType().equals(AppEventConst.UISTATE_CHANGED)) {

			this.onUiStateChange(mainState);
		} else if (event.getType().equals(AppEventConst.DATA_DELETED)) {

			CardPanelEventUtil.fixLastRowChange(this);
		} else if (OrgChangedEvent.class.getName().equals(event.getType())) {

			CardPanelEventUtil.orgChangeProcess(this);
		} else if (event.getType().equals(AppEventConst.SELECTION_CHANGED) && this.isShowing()) {

			CardPanelEventUtil.grandModelInit(this);
		}
	}

	// 主子孙事件处理情况
	private void mainGrandProcess(AppEvent event) {
		if (event.getType().equals(MainGrandEventTypeEnum.grandcard_expend.toString())) {
			// 卡片主子孙情况下伸展
			this.displayGrandPanel();
		} else if (event.getType().equals(MainGrandEventTypeEnum.grandcard_shrink.toString())) {
			// 卡片主子孙情况下收缩
			this.setGrandPanelMin();

		} else if (event.getType().equals(MainGrandEventTypeEnum.grandcard_max.toString())) {
			// 卡片主子孙情况下最大化
			this.setGrandPanelMax();

		} else if (event.getType().equals(MainGrandEventTypeEnum.grandcard_min.toString())) {
			// 卡片主子孙情况下还原
			this.resizeGrandPanel();

		} else if (event.getType().equals(MainGrandEventTypeEnum.bodycard_max.toString())) {
			// 卡片主子孙情况下主子表表体最大化
			((BillForm) this.getMainPanel()).getBillCardPanel().setPosMaximized(1);// 主表表体最大化
			((CardGrandPanel) this.getGrandPanel()).getGrandComoPanel().setVisible(false);// 孙面板隐藏
			this.hideGrandPanel();// 调整分割线位置为隐藏状态

		} else if (event.getType().equals(MainGrandEventTypeEnum.bodycard_min.toString())) {
			// 卡片主子孙情况下主子表表体还原
			((BillForm) this.getMainPanel()).getBillCardPanel().setPosMaximized(-1);// 主表表体还原
			if (this.getModel().isHandleListCardIsShow()) {// 如果孙表显示则进行控制
				((CardGrandPanel) this.getGrandPanel()).getGrandComoPanel().setVisible(true);// 孙面板显示
				this.resizeGrandPanel();// 调整分割线位置为显示状态
			}
		} else if (event.getType().equals(MainGrandEventTypeEnum.headcard_max.toString())) {
			// 卡片主子孙情况下主子表表头最大化
			((BillForm) this.getMainPanel()).getBillCardPanel().getHeadUIPanel().setVisible(true);
			((BillForm) this.getMainPanel()).getBillCardPanel().setPosMaximized(0);// 主表表头最大化
			((CardGrandPanel) this.getGrandPanel()).getGrandComoPanel().setVisible(false);// 孙面板隐藏
			this.hideGrandPanel();// 调整分割线位置为隐藏状态
		}

		if (event.getType().equals(MainGrandEventTypeEnum.headcard_min.toString())) {
			// 卡片主子孙情况下主子表表头还原
			((BillForm) this.getMainPanel()).getBillCardPanel().setPosMaximized(-1);// 主表表头还原
			if (this.getModel().isHandleListCardIsShow()) {// 如果孙表显示则进行控制
				((CardGrandPanel) this.getGrandPanel()).getGrandComoPanel().setVisible(true);// 孙面板隐藏
				this.resizeGrandPanel();// 调整分割线位置为显示状态
			}
		} else if (event.getType().equals(MainGrandEventTypeEnum.loadcardgranddata.toString())) {

			CardPanelEventUtil.grandModelInit(this);
		}
	}

	/**
	 * 页签切换的处理
	 */
	private void onBodyTabChange(UIState state, AppEvent event) {

		if (this.getModel().getCardBodyEnumState() != MainGrandStatusTypeEnum.card_body_max) {

			this.getMainGrandAssist().updateGrandCardByBodyTab(this);
		} else if (this.getModel().getCardBodyEnumState() == MainGrandStatusTypeEnum.card_body_max) {
			// 最大化子表的时候需要记录一些界面比例
			if (this.getModel().getCardGrandEnumState() == MainGrandStatusTypeEnum.card_grand_hide) {
				float panelHeight = this.getListCardSplitPane().getHeight() - 25;
				this.setUiDividerLocation(panelHeight / this.getListCardSplitPane().getHeight());
			} else if (this.getModel().getCardGrandEnumState() == MainGrandStatusTypeEnum.card_grand_show) {
				this.setUiDividerLocation(this.getCurrentLocation());
			}
		}
		if (state.equals(UIState.NOT_EDIT)) {
			int currentRow = ((BillForm) this.mainPanel).getBillCardPanel().getBillTable().getSelectedRow();
			if (currentRow < 0) {

				String currentbodyTabCode = ((BillForm) this.mainPanel).getBillCardPanel().getCurrentBodyTableCode();

				BillForm grandBillForm = (BillForm) this.getMaingrandrelationship().getBodyTabTOGrandCardComposite()
						.get(currentbodyTabCode);
				if (grandBillForm != null) {
					String grandTabCode = grandBillForm.getBillCardPanel().getCurrentBodyTableCode();
					grandBillForm.getBillCardPanel().getBillData().setBodyValueVO(grandTabCode, null);
				}
			}
		}
	}

	private void onBeforeBodyTabChange(UIState state) {
		int currentRow = ((BillForm) this.mainPanel).getBillCardPanel().getBillTable().getSelectedRow();
		if (currentRow >= 0) {
			if (state.equals(UIState.EDIT)) {
				this.getMainGrandAssist().showMainCardDataByGrandTabEdit(((BillForm) this.mainPanel), currentRow,
						this.getMaingrandrelationship());
			} else if (state.equals(UIState.ADD)) {
				this.getMainGrandAssist().showMainCardDataByGrandTab(((BillForm) this.mainPanel), currentRow,
						this.getMaingrandrelationship());
			}
		}
	}

	private void onHeadRowChange(UIState state) {

		String currentbodyTabCode = ((BillForm) this.mainPanel).getBillCardPanel().getCurrentBodyTableCode();
		BillForm grandBillForm = (BillForm) this.getMaingrandrelationship().getBodyTabTOGrandCardComposite()
				.get(currentbodyTabCode);
		if (grandBillForm != null) {
			String[] tabCodes = grandBillForm.getBillCardPanel().getBillData().getBodyTableCodes();
			for (String grandTabCode : tabCodes) {
				grandBillForm.getBillCardPanel().getBillModel(grandTabCode).clearBodyData();
			}
		}
	}

	private void onHeadDoubleClick(UIState state) {
		String currentbodyTabCode = ((BillForm) this.mainPanel).getBillCardPanel().getCurrentBodyTableCode();
		BillForm grandBillForm = (BillForm) this.getMaingrandrelationship().getBodyTabTOGrandCardComposite()
				.get(currentbodyTabCode);
		if (grandBillForm != null) {
			String grandTabCode = grandBillForm.getBillCardPanel().getCurrentBodyTableCode();
			// for (String grandTabCode : tabCodes) {
			List<Object> queryList = getMainGrandAssist().setGrand2CurrentPanel(this);
			if (queryList != null && queryList.size() > 0) {
				grandBillForm.getBillCardPanel().getBillData()
						.setBodyValueVO(grandTabCode, queryList.toArray(new SuperVO[0]));
				CardPanelEventUtil.execlFormula(grandBillForm, grandTabCode);
			} else {
				grandBillForm.getBillCardPanel().getBillModel(grandTabCode).clearBodyData();
				// }
			}
		}
	}

	private void onUiStateChange(UIState state) {
		String currentbodyTabCode = ((BillForm) this.getMainPanel()).getBillCardPanel().getCurrentBodyTableCode();
		BillForm grandBillForm = (BillForm) this.getMaingrandrelationship().getBodyTabTOGrandCardComposite()
				.get(currentbodyTabCode);
		if (state.equals(UIState.ADD)) {
			// 清除缓存数据
			this.getModel().clearBufferData();
			this.getMainGrandAssist().setGrandStatusByMainForm(this);
			if (grandBillForm != null) {
				grandBillForm.getBillCardPanel().getBillData().clearViewData();
			}
		} else if (state.equals(UIState.EDIT)) {
			this.getModel().setAddReturnList(false);
			// 清除缓存数据
			this.getModel().clearBufferData();
			this.getMainGrandAssist().setGrandStatusByMainForm(this);
			if (grandBillForm != null) {
				grandBillForm.getBillCardPanel().getBillData().clearViewData();
			}
		} else if (state.equals(UIState.NOT_EDIT)) {
			getMainGrandAssist().setGrandStatusByMainForm(this);
		}
	}

	private void onRowEditHandler(AppEvent event, UIState state) {

		if (((CardBodyRowEditEvent) event).getRowEditType() == BodyRowEditType.DELLINE) {
			String currentbodyTabCode = ((BillForm) this.mainPanel).getBillCardPanel().getCurrentBodyTableCode();
			// 根据当前子页签获取孙表view
			BillForm grandBillForm = (BillForm) this.getMaingrandrelationship().getBodyTabTOGrandCardComposite()
					.get(currentbodyTabCode);
			if (grandBillForm != null) {
				int deletine = ((CardBodyRowEditEvent) event).getBillCardPanel().getBillTable().getSelectedRow();
				if (this.getModel().isFlag() == false) {
					if (state.equals(UIState.ADD)) {
						this.getMainGrandAssist().delGrandCardDataByMainRowAdd(((BillForm) this.mainPanel), deletine,
								grandBillForm);
					} else if (state.equals(UIState.EDIT)) {
						this.getMainGrandAssist().delGrandCardDataByMainRowEdit(((BillForm) this.mainPanel), deletine,
								grandBillForm);
					}
				} else {
					this.getModel().setFlag(false);
				}
			}
		}
	}

	/**
	 * 行改变事件的操作
	 * 
	 */
	private void onRowChangeHandler(UIState state, CardBodyRowChangedEvent event,
			IGrandValidationService grandValidationService) {
		if (state.equals(UIState.EDIT)) {

			CardPanelEventUtil.rowChangeStateIsEdit(this, event, grandValidationService, this.mainGrandBlankFilter);

		} else if (state.equals(UIState.ADD)) {

			CardPanelEventUtil.rowChangeStateIsAdd(this, event, grandValidationService, this.mainGrandBlankFilter);
		} else if (state.equals(UIState.NOT_EDIT)) {

			CardPanelEventUtil.rowChangeStateIsnoEdit(this, event);
		}
	}

	/**
	 * 提供获取聚合VO
	 */
	public Object getValue() {
		int currentRow = ((BillForm) this.mainPanel).getBillCardPanel().getBillTable().getSelectedRow();
		// 停止主子表编辑
		((BillForm) this.mainPanel).getBillCardPanel().stopEditing();
		String currentbodyTabCode = ((BillForm) this.mainPanel).getBillCardPanel().getCurrentBodyTableCode();
		((BillForm) this.mainPanel).getBillCardPanel().getBillData().getBillstatus();
		Object grandObjBillForm = this.getMaingrandrelationship().getBodyTabTOGrandCardComposite()
				.get(currentbodyTabCode);

		// 子表最后一行数据点保存时强制发编辑事件保存最后一条数据
		if (grandObjBillForm != null) {
			BillForm grandBillForm = (BillForm) grandObjBillForm;
			grandBillForm.getBillCardPanel().stopEditing();
			CardBodyRowChangedEvent event = new CardBodyRowChangedEvent(((BillForm) this.mainPanel).getBillCardPanel(),
					new BillEditEvent(this, currentRow, currentRow));
			this.getModel().getMainModel().fireEvent(event);
		}

		AggregatedValueObject aggVo = (AggregatedValueObject) this.getComponentValueManager().getValue();
		return aggVo;
	}

	/**
	 * 面板设置值
	 * 
	 * @param aggvo
	 */
	public void setValue(Object object) throws BusinessException {

		this.getComponentValueManager().setValue(object);
	}

	@Override
	public boolean isDisplyGrandPanel() {
		return false;
	}

	// 初始化主子孙面板卡片信息
	protected void initCardListener() {
		// 对主子model添加监听
		((BillForm) this.mainPanel).getModel().addAppEventListener(this);
	}

	public MainGrandBlankFilter getMainGrandBlankFilter() {
		return this.mainGrandBlankFilter;
	}

	public void setMainGrandBlankFilter(MainGrandBlankFilter mainGrandBlankFilter) {
		this.mainGrandBlankFilter = mainGrandBlankFilter;
	}

	public JSplitPane getHeadpanelcombo() {
		return this.headpanelcombo;
	}

	public void setHeadpanelcombo(JSplitPane headpanelcombo) {
		this.headpanelcombo = headpanelcombo;
	}

	public ExpendShrinkGrandCardAction getExpendShrinkGrandCardAction() {
		return this.expendShrinkGrandCardAction;
	}

	public void setExpendShrinkGrandCardAction(ExpendShrinkGrandCardAction expendShrinkGrandCardAction) {
		this.expendShrinkGrandCardAction = expendShrinkGrandCardAction;
	}

	public IGrandValidationService getGrandValidationService() {
		return this.grandValidationService;
	}

	public void setGrandValidationService(IGrandValidationService grandValidationService) {
		this.grandValidationService = grandValidationService;
	}

	public boolean isGrandAutoAddLine() {
		return this.grandAutoAddLine;
	}

	public void setGrandAutoAddLine(boolean grandAutoAddLine) {
		if (this.grandAutoAddLine == grandAutoAddLine) {
			return;
		}
		this.grandAutoAddLine = grandAutoAddLine;
	}

	public IComponentValueStrategy getComponentValueManager() {
		if (this.componentValueManager == null) {
			this.componentValueManager = new MainGrandNotDelValueAdapter();
		}
		if (this.componentValueManager instanceof AbstractGrandComponentValueAdapter) {
			((AbstractGrandComponentValueAdapter) this.componentValueManager).setMainGrandPanel(this);
			((AbstractGrandComponentValueAdapter) this.componentValueManager).setMainGrandBlankFilter(this
					.getMainGrandBlankFilter());
			((AbstractGrandComponentValueAdapter) this.componentValueManager).setMainGrandRelationShip(this
					.getMaingrandrelationship());
		}
		return this.componentValueManager;
	}

	public void setComponentValueManager(IComponentValueStrategy componentValueManager) {
		this.componentValueManager = componentValueManager;
	}
}
