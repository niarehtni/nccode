package nc.ui.wa.grade.view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.ISalaryadjmgtConstant;
import nc.itf.hr.wa.IWaGradeQueryService;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.model.RegionDefaultRefTreeModel;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemHyperlinkListener;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeTabChangedEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyRowChangedEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyRowEditEvent;
import nc.ui.pubapp.uif2app.event.card.CardBodyTabChangedEvent;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent;
import nc.ui.pubapp.uif2app.event.list.ListHeadDoubleClickEvent;
import nc.ui.pubapp.uif2app.event.list.ListHeadRowChangedEvent;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.IFunNodeClosingListener;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.AutoShowUpEventSource;
import nc.ui.uif2.components.IAutoShowUpComponent;
import nc.ui.uif2.components.IAutoShowUpEventListener;
import nc.ui.uif2.components.IComponentWithActions;
import nc.ui.uif2.components.ITabbedPaneAwareComponent;
import nc.ui.uif2.components.ITabbedPaneAwareComponentListener;
import nc.ui.uif2.components.TabbedPaneAwareCompnonetDelegate;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.wa.grade.model.WaGradeBillModel;
import nc.ui.wa.ref.InfSetandFldRefModel;
import nc.ui.wa.ref.PsnInfFldRefPane;
import nc.ui.wa.ref.WaPrmlvRefModel;
import nc.ui.wa.ref.WaSeclvRefModel;
import nc.ui.wa.salaryadjmgt.WASalaryadjmgtDelegator;
import nc.ui.wa.salaryadjmgt.WaBillTableEnterKeyControler;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.hr.pub.FormatVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.adjust.IWaAdjustConstants;
import nc.vo.wa.grade.AggWaGradeVO;
import nc.vo.wa.grade.CrtVO;
import nc.vo.wa.grade.IWaGradeCommonDef;
import nc.vo.wa.grade.IWaGradeCommonDef.GradeSetType;
import nc.vo.wa.grade.IWaGradeCommonDef.OprationFlag;
import nc.vo.wa.grade.PsnhiHeaderVO;
import nc.vo.wa.grade.WaCriterionVO;
import nc.vo.wa.grade.WaGradeVO;
import nc.vo.wa.grade.WaGradeVerVO;
import nc.vo.wa.grade.WaPrmlvVO;
import nc.vo.wa.grade.WaPsnhiVO;
import nc.vo.wa.grade.WaSeclvVO;
import nc.vo.wa.grade.WaStdHiSetVO;
import nc.vo.wa.item.WaItemVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * WaGradeForm<BR>
 * <BR>
 * 
 * @author: xuhw
 * @date: 2009-11-10
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WaGradeForm extends BillForm implements ITabbedPaneAwareComponent,
		IAutoShowUpComponent, IComponentWithActions, BillEditListener,
		BillEditListener2 {
	/**
	 * @author xuhw on 2010-4-28
	 */
	private static final long serialVersionUID = 201004050505L;
	private List<Action> actions;
	private final IAutoShowUpComponent autoShowUpComponent;
	private final ITabbedPaneAwareComponent tabbedPaneAwareComponent;
	private IFunNodeClosingListener closingListener;
	/** 薪资标准设置表主表PK */
	private String strPkWaGrd = null;
	/** 薪资档别VO数组 */
	private WaSeclvVO[] seclvVos = null;
	/** 是否多档 */
	private boolean isMultsecCkeck = false;
	/** 是否宽带薪酬 */
	private boolean isRangeCkeck = false;
	/** 项目名字 */
	private String[] saClsBodyColName = null;
	/** 项目key */
	private String[] saClsBodyColKeyName = null;
	/** 项目类型 */
	private Integer[] saClsBodyColType = null;
	/** 项目名称的设定 */
	private final List<String> lisColName = new ArrayList<String>();
	/** 项目key的设定 */
	private final List<String> lisKeyName = new ArrayList<String>();
	/** 项目类型设定 */
	private final List<Integer> lisColType = new ArrayList<Integer>();
	/** 多表头 */
	private final HashMap<String, List<String>> colNameMap = new HashMap<String, List<String>>();
	private WaGradeBillModel billModel = null;

	private GradeSetType gradeSetType = null;

	/** 薪级和薪档页签名称 */
	private String tableCode = null;

	/** 薪级和薪档页签区分 */
	private int classType = 0;
	// UIChartPanel chartPanel = null;
	UIPanel mainPanel = null;

	// 记录级别人员属性页签职级列itemkey
	String pk_wa_psnhi = null;

	/* 解决连接数的问题 handelevent方法中会重复走两次查询方法，处理后可以减少三次 */

	// private final Map<String,PsnhiHeaderVO[]> cachedPsnhiHeaderVOs = new
	// HashMap<String,PsnhiHeaderVO[]>();

	public WaGradeForm() {
		super();
		autoShowUpComponent = new AutoShowUpEventSource(this);
		tabbedPaneAwareComponent = new TabbedPaneAwareCompnonetDelegate();
	}

	@Override
	public void initUI() {
		super.initUI();
		// 增加编辑监听
		this.getBillCardPanel().addEditListener(this);
		this.getBillCardPanel().getBodyPanel(IWaGradeCommonDef.WA_STD_HI)
				.addEditListener2(this);
		this.getBillCardPanel().getBodyPanel(IWaGradeCommonDef.WA_STD_HI_SECLV)
				.addEditListener2(this);
		// 设置节点编码
		InfSetandFldRefModel refmodel = new InfSetandFldRefModel();
		UIRefPane refPane = (UIRefPane) getHeadItemPK_WA_PSNHI().getComponent();
		refPane.setRefModel(refmodel);
		refmodel.setFun_code(getModel().getContext().getFuncInfo().getFuncode());
		// 设置人员属性参照为可多选
		refPane.setMultiSelectedEnabled(true);
		InfSetandFldRefModel refSeclvModel = new InfSetandFldRefModel();
		UIRefPane refSeclvPane = (UIRefPane) getHeadItem(
				IWaGradeCommonDef.PK_WA_PSNHI_SECLV).getComponent();
		refSeclvPane.setRefModel(refSeclvModel);
		refSeclvModel.setFun_code(getModel().getContext().getFuncInfo()
				.getFuncode());
		// 设置人员属性参照为可多选
		refSeclvPane.setMultiSelectedEnabled(true);

		// 去薪级，薪档排序功能
		getBillCardPanel().getBillTable(IWaGradeCommonDef.WA_PRMLV)
				.removeSortListener();
		getBillCardPanel().getBillTable(IWaGradeCommonDef.WA_SECLV)
				.removeSortListener();

		// 20151223 shenliangc NCdp205564067 薪资未屏蔽右键菜单和表格中回车加行的功能的节点
		getBillCardPanel().getBillTable(IWaGradeCommonDef.WA_STD_HI)
				.removeSortListener();
		getBillCardPanel().getBillTable(IWaGradeCommonDef.WA_STD_HI_SECLV)
				.removeSortListener();
		BillPanelUtils.disabledRightMenuAndAutoAddLine(getBillCardPanel()
				.getBodyPanel(IWaGradeCommonDef.WA_STD_HI));
		BillPanelUtils.disabledRightMenuAndAutoAddLine(getBillCardPanel()
				.getBodyPanel(IWaGradeCommonDef.WA_STD_HI_SECLV));

		getBillCardPanel().getBillTable(IWaGradeCommonDef.WA_PRMLV)
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getBillCardPanel().getBillTable(IWaGradeCommonDef.WA_SECLV)
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// 去掉向下键增加行
		getBillCardPanel().getBodyPanel(IWaGradeCommonDef.WA_CRT)
				.setAutoAddLine(false);
		getBillCardPanel().getBodyPanel(IWaGradeCommonDef.WA_PRMLV)
				.setAutoAddLine(false);
		getBillCardPanel().getBodyPanel(IWaGradeCommonDef.WA_SECLV)
				.setAutoAddLine(false);
		getBillCardPanel().getBodyPanel(IWaGradeCommonDef.PK_WA_PSNHI)
				.setAutoAddLine(false);
		getBillCardPanel().getBodyPanel(IWaGradeCommonDef.PK_WA_PSNHI_SECLV)
				.setAutoAddLine(false);
		// 去掉Enter键增加行
		getBillCardPanel().setBillTableEnterKeyControler(
				IWaGradeCommonDef.WA_CRT, new WaBillTableEnterKeyControler());
		getBillCardPanel().setBillTableEnterKeyControler(
				IWaGradeCommonDef.WA_PRMLV, new WaBillTableEnterKeyControler());
		getBillCardPanel().setBillTableEnterKeyControler(
				IWaGradeCommonDef.WA_SECLV, new WaBillTableEnterKeyControler());
		getBillCardPanel().setBillTableEnterKeyControler(
				IWaGradeCommonDef.PK_WA_PSNHI,
				new WaBillTableEnterKeyControler());
		getBillCardPanel().setBillTableEnterKeyControler(
				IWaGradeCommonDef.PK_WA_PSNHI_SECLV,
				new WaBillTableEnterKeyControler());
	}

	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);

		UIState state = this.getModel().getUiState();
		// 初始化的时候，如果没有选中多档，则挡别页签不可用。
		// 设置参照的所属组织
		if (AppEventConst.MODEL_INITIALIZED.equalsIgnoreCase(event.getType())) {
			BillPanelUtils.setPkorgToRefModel(getBillCardPanel(), getModel()
					.getContext().getPk_org());
			return;
		}

		// 增加的场合
		if (state == UIState.ADD) {
			// 设置标准值档别设定页签不可用
			this.getBillCardPanel().setTabEnabled(IBillItem.BODY,
					IWaGradeCommonDef.WA_SECLV, false);
		}
		// if (chartPanel != null && state == UIState.ADD)
		// {
		// chartPanel.setVisible(false);
		// }
		// else if (chartPanel != null)
		// {
		// chartPanel.setVisible(true);
		// }
		// setHeadItemsEdit(state == UIState.ADD);
		// if (state == UIState.ADD||state ==
		// UIState.EDIT||event.getType().equalsIgnoreCase("nc.ui.pubapp.uif2app.event.list.ListHeadDoubleClickEvent"))
		// {
		// 20150819 xiejie3 NCdp205458686
		// 薪资标准设置界面不可编辑,目前nc.ui.wa.grade.view.WaGradeForm.createWaStdHiTable(PsnhiHeaderVO[],
		// String)
		// 中为给表体中的参照字段增加一个保存主键的字段这个方法时，会重新建卡片模板，发出事件，此事件又会被setCrtAndStdHiEnabled处理，导致事件循环。固进行事件过滤，界面初始化时不进行处理。event.getType().equalsIgnoreCase("nc.ui.pubapp.uif2app.event.list.ListHeadDoubleClickEvent")||
		// if (state !=
		// UIState.NOT_EDIT||event.getClass().equals(CardBodyRowEditEvent.class)||AppEventConst.UISTATE_CHANGED
		// == event.getType()
		//
		// ||
		// event.getType().equalsIgnoreCase("nc.ui.pubapp.uif2app.event.list.ListHeadDoubleClickEvent"))
		// {
		// if (AppEventConst.MULTI_SELECTION_CHANGED !=
		// event.getType()||event.getType().equalsIgnoreCase("sub_state_changed ")
		// &&
		// !event.getType().equalsIgnoreCase("nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent"))
		// {
		// setCrtAndStdHiEnabled(state);
		// setSeclvEnabled();
		// // 20150916 shenliangc 解决薪资标准表子表无法增行问题。增行事件event.getSource()为空。
		// if (event.getSource()!= null &&
		// !event.getSource().getClass().toString().equals("class nc.ui.pub.bill.BillTabbedPane")){
		// setSelectIndex(state);
		// }
		// }
		// }
		//

		// 20151009 xiejie3 uap事件进行了修改，导致事件循环锁死。现进行事件过滤
		// 设置薪资标准类别值表和人员信息项值三个页签的编辑状态
		// 处理的事件1：主界面保存--UiState_Changed ，
		// 处理的事件2：子表的保存事件--sub_state_changed ，
		// 处理的事件3：刷新--Selected_Data_Changed
		// 处理的事件4：列表点击超链--BillItemHyperlinkListener
		// 处理的事件5：卡片翻页--ListHeadRowChangedEvent
		// 处理的事件6：列表双击事件--ListHeadDoubleClickEvent
		// setCrtAndStdHiEnabled(state);
		// setSeclvEnabled();
		// setSelectIndex(state);

		if (event.getType().equalsIgnoreCase(AppEventConst.UISTATE_CHANGED)
				|| event.getType().equalsIgnoreCase("sub_state_changed")
				|| event.getType().equalsIgnoreCase(
						AppEventConst.SELECTED_DATE_CHANGED)
				|| event.getType().equals(
						BillItemHyperlinkListener.class.getName())
				|| event.getType().equals(
						ListHeadRowChangedEvent.class.getName())
				|| event.getClass().equals(ListHeadDoubleClickEvent.class)
		// ||event.getClass().equals(CardBodyRowEditEvent.class)
		) {
			if (!event.getClass().equals(CardBodyBeforeTabChangedEvent.class)) {
				setCrtAndStdHiEnabled(state);
			}
		}
		// 据情况设置是否为多档
		// 处理的事件1：界面编辑事件--CardHeadTailBeforeEditEvent
		// 处理的事件2：刷新--Selected_Data_Changed
		// 处理的事件3：子表新增行事件--CardBodyRowEditEvent
		// 处理的事件4：切换页签--CardBodyTabChangedEvent
		// 处理的事件5：子表编辑事件--CardBodyBeforeEditEvent
		// 处理的事件6：列表双击事件--ListHeadDoubleClickEvent
		// 处理的事件7：切换页签事件--Multi_Selection_Changed
		// 处理的事件8：保存事件--UiState_Changed
		// 处理的事件9：子表增加行事件--CardBodyRowChangedEvent 20151122 xiejie3
		// NCdp205543567 薪档增行时，页签变为置灰态
		if (event.getClass().equals(CardHeadTailBeforeEditEvent.class)
				|| event.getType().equalsIgnoreCase(
						AppEventConst.SELECTED_DATE_CHANGED)
				|| event.getClass().equals(CardBodyRowEditEvent.class)
				|| event.getClass().equals(CardBodyTabChangedEvent.class)
				|| event.getClass().equals(CardBodyBeforeEditEvent.class)
				|| event.getClass().equals(ListHeadDoubleClickEvent.class)
				|| event.getType()
						.equals(AppEventConst.MULTI_SELECTION_CHANGED)
				|| event.getType().equals(AppEventConst.UISTATE_CHANGED)
				|| event.getType().equals(
						CardBodyRowChangedEvent.class.getName())) {
			setSeclvEnabled();
		}
		// 设置页签的默认选择位置
		// 处理的事件1：刷新--Selected_Data_Changed
		// 处理的事件2：主界面新增--Selected_Data_Changed
		// 处理的事件3：子表新增行事件--CardBodyRowEditEvent
		if (event.getType().equals(AppEventConst.SELECTED_DATE_CHANGED)
				|| event.getType().equalsIgnoreCase(
						AppEventConst.UISTATE_CHANGED)) {
			setSelectIndex(state);
		}

		if (AppEventConst.SHOW_EDITOR.equalsIgnoreCase(event.getType())) {
			showMeUp();
		}

		// getGradeModel().setCopying(Boolean.FALSE);

		// 去薪级，薪档排序功能
		getBillCardPanel().getBillTable(IWaGradeCommonDef.WA_PRMLV)
				.removeSortListener();
		getBillCardPanel().getBillTable(IWaGradeCommonDef.WA_SECLV)
				.removeSortListener();

		if (getBillCardPanel().getSize().equals(new Dimension(0, 0)))
			return;

		// NCdp205512489 crtVOs和model数据不同步，暂时去掉图形显示，后续考虑增加按钮，弹出图形显示区域
		// //-------------图形展示--------------------
		// CrtVO[] crtVOs =
		// disToCrtVO(billCardPanel.getBillModel(IWaGradeCommonDef.WA_CRT),
		// billCardPanel.getBillTable(IWaGradeCommonDef.WA_CRT));
		// if (crtVOs == null || crtVOs.length == 0)
		// {
		// if (chartPanel != null)
		// {
		// chartPanel.show(false);
		// }
		// return;
		// }
		// if (chartPanel == null && state != UIState.EDIT)
		// {
		//
		// chartPanel = new UIChartPanel(this,gradeSetType);
		// chartPanel.setAggGradeVO((AggWaGradeVO)
		// getModel().getSelectedData());
		// chartPanel.setCrtVOs(crtVOs);
		// chartPanel.initUI(getBillCardPanel().getSize());
		// CardAreaPanel areaPanel = new
		// CardAreaPanel(ResHelper.getString("60130paystd","060130paystd0219")/*@res
		// "图形"*/,this,false);
		// areaPanel.register(chartPanel);
		// UISplitPane sp = new UISplitPane(UISplitPane.VERTICAL_SPLIT,
		// getBillCardPanel(), chartPanel);
		// sp.setDividerSize(3);
		// sp.setDividerLocation(new
		// UFDouble(getBillCardPanel().getSize().getHeight()-20).intValue());
		// add(sp);
		// }
		// if (state == UIState.NOT_EDIT)
		// {
		// chartPanel.show(true);
		// chartPanel.setGradeSetType(gradeSetType);
		// chartPanel.setAggGradeVO((AggWaGradeVO)
		// getModel().getSelectedData());
		// chartPanel.setCrtVOs(disToCrtVO(billCardPanel.getBillModel(IWaGradeCommonDef.WA_CRT),
		// billCardPanel.getBillTable(IWaGradeCommonDef.WA_CRT)));
		// chartPanel.reload();
		// }

		/**
		 * 
		 * 2014-04-30 shenliangc为实现职级类别自动加载显示而添加。 20140902 shenliangc
		 * 级别人员属性、档别人员属性页签都要处理。
		 */
		// 级别人员属性页签
		this.reLoadJoblevelsys(IWaGradeCommonDef.WA_STD_HI);
		// 档别人员属性页签
		this.reLoadJoblevelsys(IWaGradeCommonDef.WA_STD_HI_SECLV);

		// 20151214 shenliangc NCdp205559130 薪资规则，新增一行后，填入数据用回车换行，第二行行号为空，保存时报错
		// 去掉回车增行、子表表体右键。
		getBillCardPanel().getBodyPanel(IWaGradeCommonDef.WA_CRT)
				.setBBodyMenuShow(false);
		getBillCardPanel().getBodyPanel(IWaGradeCommonDef.WA_PRMLV)
				.setBBodyMenuShow(false);
		getBillCardPanel().getBodyPanel(IWaGradeCommonDef.WA_SECLV)
				.setBBodyMenuShow(false);
		getBillCardPanel().getBodyPanel(IWaGradeCommonDef.PK_WA_PSNHI)
				.setBBodyMenuShow(false);
		getBillCardPanel().getBodyPanel(IWaGradeCommonDef.PK_WA_PSNHI_SECLV)
				.setBBodyMenuShow(false);
		// 20151223 shenliangc NCdp205564067 薪资未屏蔽右键菜单和表格中回车加行的功能的节点
		getBillCardPanel().getBillTable(IWaGradeCommonDef.WA_STD_HI)
				.removeSortListener();
		getBillCardPanel().getBillTable(IWaGradeCommonDef.WA_STD_HI_SECLV)
				.removeSortListener();
		BillPanelUtils.disabledRightMenuAndAutoAddLine(getBillCardPanel()
				.getBodyPanel(IWaGradeCommonDef.WA_STD_HI));
		BillPanelUtils.disabledRightMenuAndAutoAddLine(getBillCardPanel()
				.getBodyPanel(IWaGradeCommonDef.WA_STD_HI_SECLV));
	}

	// 20150902 shenliangc 解决SONAR问题，将职级主键和对应的职级列别名称统一查询出来。
	private HashMap<String, String> joblevelsysNameMap = null;

	public void reLoadJoblevelsys(String tablecode) {
		BillItem[] billItems = this.getBillCardPanel().getBillModel(tablecode)
				.getBodyItems();
		boolean isExistJoblevelsys = false;
		int index = -1;
		if (!ArrayUtils.isEmpty(billItems)) {
			for (int i = 0; i < billItems.length; i++) {
				if (billItems[i].getKey().equals("pk_joblevelsys")) {
					isExistJoblevelsys = true;
					index = i + 1;
					break;
				}
			}
		}
		if (isExistJoblevelsys) {
			// 20150902 shenliangc 解决SONAR问题，将职级主键和对应的职级列别名称统一查询出来。
			if (this.joblevelsysNameMap == null) {
				IWaGradeQueryService service = NCLocator.getInstance().lookup(
						IWaGradeQueryService.class);
				try {
					this.joblevelsysNameMap = service
							.queryJoblevelsysNameByJoblevelPk();
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
				}
			}

			int rowCount = this.getBillCardPanel().getBillModel(tablecode)
					.getRowCount();
			String joblevelsysName = "";
			if (rowCount > 0) {
				for (int i = 0; i < rowCount; i++) {
					String pk_joblevel = (String) this.getBillCardPanel()
							.getBillTable(tablecode).getModel()
							.getValueAt(i, index);
					if (!StringUtils.isEmpty(pk_joblevel)
							&& !this.joblevelsysNameMap.isEmpty()) {
						joblevelsysName = this.joblevelsysNameMap
								.get(pk_joblevel);
					}
					getBillCardPanel().getBillModel(tablecode).setValueAt(
							joblevelsysName, i, "pk_joblevelsys");

					// BillCellEditor editor = (BillCellEditor)
					// this.getBillCardPanel().getBillTable(IWaGradeCommonDef.WA_STD_HI).getCellEditor(i,
					// index);
					// UIRefPane pane = (UIRefPane) editor.getComponent();
					// getBillCardPanel().getBillModel(IWaGradeCommonDef.WA_STD_HI).setValueAt(pane.getRefModel().getValue("pk_joblevel"),
					// i, "pk_joblevelsys");
				}
			}
		}

	}

	/**
	 * 设置页签的默认选择位置
	 * 
	 * @author xuhw on 2010-5-5
	 * @param state
	 */
	private void setSelectIndex(UIState state) {
		UICheckBox checkBox = (UICheckBox) getBillCardPanel().getHeadItem(
				WaGradeVO.ISMULTSEC).getComponent();
		// 增加的场合
		if (state == UIState.ADD) {
			// 设置第一个页签为默认页签
			this.getBillCardPanel()
					.getBodyTabbedPane()
					.setSelectedIndex(
							ISalaryadjmgtConstant.FIRST_BODYTABBEDPANE_INDEX);
		} else if (state == UIState.EDIT) {
			int selectindex = this.getBillCardPanel().getBodyTabbedPane()
					.getSelectedIndex();

			// 薪级页签和薪档页签以外的页签
			if (selectindex != 0 && selectindex != 1
					|| (selectindex == 1 && !checkBox.isSelected())) {
				this.getBillCardPanel()
						.getBodyTabbedPane()
						.setSelectedIndex(
								ISalaryadjmgtConstant.FIRST_BODYTABBEDPANE_INDEX);
			}
		}
		// -------------add for 多版本-------------------
		else if (state == UIState.NOT_EDIT
				&& (getGradeModel().getOprationFlag() == OprationFlag.GradeverAdd || getGradeModel()
						.getOprationFlag() == OprationFlag.GradeverEdit)) {
			// 版本操作时,页签设置选中薪资标准表
			getBillCardPanel().getBodyTabbedPane().setSelectedIndex(2);
		}
	}

	/**
	 * 在编辑器进入新增状态后被调用。 此方法设置编辑器默认值.
	 */
	@Override
	protected void setDefaultValue() {
		super.setDefaultValue();
		BillItem item = getHeadItem(IWaGradeCommonDef.PK_ORG);
		item.setValue(getModel().getContext().getPk_org());
		if (getModel().getContext().getNodeType() == NODE_TYPE.ORG_NODE) {
			item.setEnabled(false);
			item.setEdit(false);
		}
		item = getHeadItem(IWaGradeCommonDef.PK_GROUP);
		item.setValue(getModel().getContext().getPk_group());
	}

	@Override
	public void afterEdit(BillEditEvent e) {

		/**
		 * 
		 * 2014-04-30 shenliangc为实现选择职级参照之后自动带出对应职级类别而添加。
		 */
		if (this.pk_wa_psnhi != null && e.getKey().contains(this.pk_wa_psnhi)) {
			BillCellEditor cellEditor = (BillCellEditor) e.getSource();
			UIRefPane pane = (UIRefPane) cellEditor.getComponent();
			getBillCardPanel().getBillModel(e.getTableCode()).setValueAt(
					pane.getRefModel().getValue(
							"om_joblevelsys.name as sysname"), e.getRow(),
					"pk_joblevelsys");
		}
		/**
		 * 
		 * 2014-04-30 shenliangc为实现选择职级参照之后自动带出对应职级类别而添加。
		 */

		// 当前选中的子表的行数
		if (WaGradeVO.ISMULTSEC.equals(e.getKey())) {
			setSeclvEnabled();
			UICheckBox checkBox = (UICheckBox) getBillCardPanel().getHeadItem(
					WaGradeVO.ISMULTSEC).getComponent();
			if (!checkBox.isSelected()) {
				this.getBillCardPanel()
						.getBodyTabbedPane()
						.setSelectedIndex(
								ISalaryadjmgtConstant.FIRST_BODYTABBEDPANE_INDEX);

			}
		} else if (e.getKey().endsWith(BillPanelUtils.REF_SHOW_NAME)) {
			// 根据所选的薪资标准类别参照设置 得到对应标准类别值内容
		}

		if (IWaGradeCommonDef.PK_WA_PSNHI.equals(e.getKey())) {
			// 级别人员属性和档别人员属性的过滤两者不能选择相同的人员属性
			UIRefPane refPane = (UIRefPane) getHeadItemPK_WA_PSNHI()
					.getComponent();
			refreshRef(IWaGradeCommonDef.PK_WA_PSNHI_SECLV, refPane.getRefPKs());

		} else if (IWaGradeCommonDef.PK_WA_PSNHI_SECLV.equals(e.getKey())) {
			// 级别人员属性和档别人员属性的过滤两者不能选择相同的人员属性
			UIRefPane refPane = (UIRefPane) getHeadItem(
					IWaGradeCommonDef.PK_WA_PSNHI_SECLV).getComponent();
			refreshRef(IWaGradeCommonDef.PK_WA_PSNHI, refPane.getRefPKs());
		}

		// 重新初始标准展示图形
		// if (IWaGradeCommonDef.CRITERIONVALUE.equals(e.getKey()) ||
		// e.getKey().indexOf("prec_") != -1 || e.getKey().indexOf("pran_") !=
		// -1 || e.getKey().indexOf("precran_") != -1)
		// {
		// chartPanel.setAggGradeVO((AggWaGradeVO)
		// getModel().getSelectedData());
		// chartPanel.setCrtVOs(disToCrtVO(billCardPanel.getBillModel(IWaGradeCommonDef.WA_CRT),
		// billCardPanel.getBillTable(IWaGradeCommonDef.WA_CRT)));
		// chartPanel.reload();
		// }
	}

	/**
	 * 为户口所在地设定显示国家
	 */
	public boolean beforeEdit(BillEditEvent e) {
		if (e.getSource() instanceof BillItem) {
			BillItem billitem = (BillItem) e.getSource();
			JComponent component = billitem.getComponent();
			if (component instanceof PsnInfFldRefPane) {
				PsnInfFldRefPane c = (PsnInfFldRefPane) component;

				AbstractRefModel model = c.getRefModel();

				if (model instanceof RegionDefaultRefTreeModel) {
					c.setMultiCorpRef(true);
				}
			}
		}

		return true;
	}

	/**
	 * 刷新标准类别参照
	 * 
	 * @author xuhw
	 * @param colKey
	 * @param refModel
	 */
	private void refreshRef(String strColKey, String[] prmlvSelecteds) {
		UIRefPane refSeclvPane = (UIRefPane) getHeadItem(strColKey)
				.getComponent();
		// 根据列的字段找到对应的列的INDEX
		if (prmlvSelecteds == null) {
			refSeclvPane.getRefModel().addWherePart("", true);
			return;
		}

		String strInWhere = FormatVO.formatArrayToString(prmlvSelecteds, "'");
		String[] pks = refSeclvPane.getRefPKs();
		refSeclvPane.getRefModel().addWherePart(
				" and pk_infoset_item not in (" + strInWhere + ")", true);
		refSeclvPane.setButtonFireEvent(true);
		refSeclvPane.setPKs(pks);
	}

	/**
	 * 设置薪资标准类别值表和人员信息项值三个页签的编辑状态
	 * 
	 * @author xuhw on 2009-12-16
	 * 
	 * @param state
	 */
	private void setCrtAndStdHiEnabled(UIState state) {
		// 2015-07-30 zhousze 解决“薪资标准设置-组织/集团”节点打不开的问题
		// if (state == UIState.EDIT)
		if (state == UIState.NOT_EDIT) {
			getBillCardPanel().setTabEnabled(IBillItem.BODY,
					IWaGradeCommonDef.WA_CRT, true);
			// 设置薪资标准值table能否编辑
			getBillModel(IWaGradeCommonDef.WA_CRT).setEnabled(
					getGradeModel().getOprationFlag() == OprationFlag.CrtEdit);
			// 设置人员相关属性table能否编辑
			getBillModel(IWaGradeCommonDef.WA_STD_HI)
					.setEnabled(
							getGradeModel().getOprationFlag() == OprationFlag.StdhiEdit);
			getBillModel(IWaGradeCommonDef.WA_STD_HI_SECLV)
					.setEnabled(
							getGradeModel().getOprationFlag() == OprationFlag.StdhiSecEdit);

			// 如果是非编辑状态，在这里要调用下面的方法
			// 设置人员相关属性

			PsnhiHeaderVO[] psnhvos = null;
			// 先初始化对象属性，再加载级别、档别人员属性
			initValues(null);
			if (strPkWaGrd != null) {
				psnhvos = findPsnHiByStdPK();
			}
			setPsnhiProperty(psnhvos);

			// 创建薪资人员属性值页签内容
			createWaStdHiTable(psnhvos, IWaGradeCommonDef.WA_STD_HI);
			// 创建薪资人员属性值页签内容
			createWaStdHiTable(psnhvos, IWaGradeCommonDef.WA_STD_HI_SECLV);
			// 创建薪资标准值页签内容
			createWaCrtTable();
			// 设置薪资标准类别值表和人员信息项值两个页签的编辑状态
			if (getGradeModel().getOprationFlag() == OprationFlag.CrtEdit
					|| getGradeModel().getOprationFlag() == OprationFlag.StdhiEdit
					|| getGradeModel().getOprationFlag() == OprationFlag.StdhiSecEdit) {
				getBillCardPanel()
						.setTabEnabled(
								IBillItem.BODY,
								IWaGradeCommonDef.WA_CRT,
								getGradeModel().getOprationFlag() == OprationFlag.CrtEdit);
				getBillCardPanel()
						.setTabEnabled(
								IBillItem.BODY,
								IWaGradeCommonDef.WA_STD_HI,
								getGradeModel().getOprationFlag() == OprationFlag.StdhiEdit);
				getBillCardPanel()
						.setTabEnabled(
								IBillItem.BODY,
								IWaGradeCommonDef.WA_STD_HI_SECLV,
								getGradeModel().getOprationFlag() == OprationFlag.StdhiSecEdit);
			}
			setItemsEnableWhenVerAdd(getGradeModel().getOprationFlag());

		} else if (getGradeModel().isCopying()) {
			getBillCardPanel().setTabEnabled(IBillItem.BODY,
					IWaGradeCommonDef.WA_CRT, true);
			// 设置薪资标准值table能否编辑
			getBillModel(IWaGradeCommonDef.WA_CRT).setEnabled(
					getGradeModel().getOprationFlag() == OprationFlag.CrtEdit);
			// 设置人员相关属性table能否编辑
			getBillModel(IWaGradeCommonDef.WA_STD_HI)
					.setEnabled(
							getGradeModel().getOprationFlag() == OprationFlag.StdhiEdit);
			getBillModel(IWaGradeCommonDef.WA_STD_HI_SECLV)
					.setEnabled(
							getGradeModel().getOprationFlag() == OprationFlag.StdhiSecEdit);

			// 如果是非编辑状态，在这里要调用下面的方法
			// 设置人员相关属性
			// 20151122 xiejie3 NCdp205543567
			// 复制时，档别人员属性清空,级别人员属性（档别人员属性）设置的值没有复制过来
			// 先初始化对象属性，再加载级别、档别人员属性
			initValues(null);
			// end
			PsnhiHeaderVO[] psnhvos = findPsnHiByStdPK();
			setPsnhiProperty(psnhvos);

			getBillCardPanel().setTabEnabled(IBillItem.BODY,
					IWaGradeCommonDef.WA_CRT,
					getGradeModel().getOprationFlag() == OprationFlag.CrtEdit);
			getBillCardPanel()
					.setTabEnabled(
							IBillItem.BODY,
							IWaGradeCommonDef.WA_STD_HI,
							getGradeModel().getOprationFlag() == OprationFlag.StdhiEdit);
			getBillCardPanel()
					.setTabEnabled(
							IBillItem.BODY,
							IWaGradeCommonDef.WA_STD_HI_SECLV,
							getGradeModel().getOprationFlag() == OprationFlag.StdhiSecEdit);
		} else {
			if (state == UIState.ADD) {
				// 设置参照
				refreshRef(IWaGradeCommonDef.PK_WA_PSNHI_SECLV, null);
				refreshRef(IWaGradeCommonDef.PK_WA_PSNHI, null);
			}
			tabEnabledSetting(IWaGradeCommonDef.WA_CRT);
			tabEnabledSetting(IWaGradeCommonDef.WA_STD_HI);
			tabEnabledSetting(IWaGradeCommonDef.WA_STD_HI_SECLV);

		}
	}

	/**
	 * 设置字表页签的相关属性<BR>
	 * <BR>
	 * 
	 * @author xuhw on 2010-3-22
	 */
	private void tabEnabledSetting(String strTabName) {
		getBillCardPanel().setTabEnabled(IBillItem.BODY, strTabName, false);
		getBillModel(strTabName).clearBodyData();
		getBillModel(strTabName).setEnabled(false);
	}

	/**
	 * 根据情况设置是否为多档<BR>
	 * 1)设置档别页签是否可用<BR>
	 * 2)设置表头项目档别人员属性是否可用<BR>
	 * 
	 * @author xuhw on 2009-11-24
	 */
	private void setSeclvEnabled() {
		UICheckBox checkBox = (UICheckBox) getBillCardPanel().getHeadItem(
				WaGradeVO.ISMULTSEC).getComponent();
		if (checkBox.isSelected()) {
			getBillCardPanel().setTabEnabled(IBillItem.BODY,
					IWaGradeCommonDef.WA_SECLV, true);
			setHeadItemEdit(IWaGradeCommonDef.PK_WA_PSNHI_SECLV, (this
					.getModel().getUiState() == UIState.ADD || this.getModel()
					.getUiState() == UIState.EDIT));
			UIRefPane refSeclvPane = (UIRefPane) getHeadItem(
					IWaGradeCommonDef.PK_WA_PSNHI_SECLV).getComponent();
			refSeclvPane.getUITextField().setEnabled(
					(this.getModel().getUiState() == UIState.ADD || this
							.getModel().getUiState() == UIState.EDIT));
			setHeadItemEdit(WaGradeVO.SECLV_MONEY_SORT, (this.getModel()
					.getUiState() == UIState.ADD || this.getModel()
					.getUiState() == UIState.EDIT));
		} else {
			getBillCardPanel().setTabEnabled(IBillItem.BODY,
					IWaGradeCommonDef.WA_SECLV, false);
			getHeadItem(IWaGradeCommonDef.PK_WA_PSNHI_SECLV).setValue(null);
			// getBillCardPanel().getBillModel(IWaGradeCommonDef.WA_SECLV).clearBodyData();
			setHeadItemEdit(IWaGradeCommonDef.PK_WA_PSNHI_SECLV, false);
			setHeadItemEdit(WaGradeVO.SECLV_MONEY_SORT, false);
			UIRefPane refSeclvPane = (UIRefPane) getHeadItem(
					IWaGradeCommonDef.PK_WA_PSNHI_SECLV).getComponent();
			refSeclvPane.getUITextField().setEnabled(false);
		}
	}

	/**
	 * 生成薪资标准值页签<BR>
	 * 四种类型<BR>
	 * 1)多级 非多档 非宽带薪酬<BR>
	 * 2)多级 非多档 宽带薪酬<BR>
	 * 3)多级 多档 宽带薪酬<BR>
	 * 4)多级 多档 宽带薪酬<BR>
	 * 
	 * @author xuhw on 2009-11-24
	 * @see nc.ui.uif2.NCAction#doAction(java.awt.event.ActionEvent)
	 */
	public void createWaCrtTable() {
		try {
			// 多级 非多档 非宽带薪酬
			if (gradeSetType == GradeSetType.SingleSeclv) {
				initPrmlvTable();
			}
			// 多级 非多档 宽带薪酬
			else if (gradeSetType == GradeSetType.SingleSeclvRange) {
				initPrmlvRangeTable();
			}
			// 多级 多档 非宽带薪酬
			else if (gradeSetType == GradeSetType.MultSeclv) {
				initPrmlvReclvTable();
			}
			// 多级 多档 宽带薪酬
			else {
				initPrmlvReclvRangeTable();
			}

			this.getBillCardPanel().addEditListener(this);
		} catch (Exception ex) {
			Logger.error("生成薪资类别标准值页签时候出现异常", ex);
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(),
					null,
					ResHelper.getString("60130paystd", "060130paystd0184")/*
																		 * @res
																		 * "生成薪资类别标准值页签时候出现异常。"
																		 */);
			return;
		}
	}

	/**
	 * 从画面上取得VO数组
	 * 
	 * @author xuhw on 2009-12-10
	 * 
	 * @param strGrdPK
	 * @return
	 * @throws BusinessException
	 */
	public WaStdHiSetVO[] disTostdHiVO(String strGrdPK)
			throws BusinessException {
		StringBuffer validatercell = new StringBuffer();
		// 根据界面的显示形成更新树组
		HashMap<String, String> hmStdHi = new HashMap<String, String>();
		BillItem[] items = billCardPanel.getBillModel().getBodyItems();
		int intRowCnt = billCardPanel.getBillModel().getRowCount();
		WaStdHiSetVO[] waStdHiSetVOs = new WaStdHiSetVO[intRowCnt];
		String[] strRowPks = new String[intRowCnt];
		StringBuffer sbLineOnlyFlag = new StringBuffer();
		String strPk_wa_psnhi = null;
		Object strPkWaGrd = null;
		for (int i = 0; i < intRowCnt; i++) {
			sbLineOnlyFlag = new StringBuffer();
			waStdHiSetVOs[i] = new WaStdHiSetVO();
			waStdHiSetVOs[i].setSortgroup(String.valueOf(i));

			strPkWaGrd = billCardPanel.getBillModel().getValueAt(i,
					IWaGradeCommonDef.PRMLVSECLV);
			// sbLineOnlyFlag.append(strPkWaGrd);
			waStdHiSetVOs[i].setWaGrdlvPK(strPkWaGrd.toString());
			for (int j = 0; j < items.length; j++) {
				strPk_wa_psnhi = items[j].getKey();
				Object obj;
				// 如果是为了参照显示名称列则直接返回
				if (items[j].getDataType() == IBillItem.COMBO) {
					obj = billCardPanel.getBillModel().getValueAt(i,
							strPk_wa_psnhi + "_ID");
				} else {
					obj = billCardPanel.getBillModel().getValueAt(i,
							strPk_wa_psnhi);
				}
				if (strPk_wa_psnhi.endsWith(BillPanelUtils.REF_SHOW_NAME)
						|| IWaGradeCommonDef.PRMLVSECLV.equals(strPk_wa_psnhi)
						|| obj == null) {
					// 20160106 xiejie3 NCdp205569417
					// 当部门/岗位撤销后，薪资标准人员属性设置，属性为空可保存
					// 校验单元格的值是否发生了变化，例如部门撤销，界面部门的名字已经无法查出，所以为null，此时修改，要进行校验，如果pk有值，而name无值，则说明此参照属性已经被修改，需要通知用户重新选择
					// 20160115 xiejie3 NCdp205573927 标准表设置人员级别属性且属性内容为空时，保存报错
					if (strPk_wa_psnhi.endsWith(BillPanelUtils.REF_SHOW_NAME)
							&& (obj == null || StringUtils.isBlank(obj
									.toString()))) {
						if (!StringUtils
								.isBlank(waStdHiSetVOs[i]
										.getAttributeValue(
												billCardPanel.getBillModel()
														.getBodyItems()[j - 1]
														.getKey()).toString())) {
							validatercell.append(i + "/" + items[j].getName()
									+ ",");
						}
					}
					// end NCdp205573927
					// end NCdp205569417
					continue;
				}
				waStdHiSetVOs[i].setPk_wa_psnhi(strPk_wa_psnhi);
				waStdHiSetVOs[i].setAttributeValue(billCardPanel.getBillModel()
						.getBodyItems()[j].getKey(), obj);

				// 不需要放入级别和档别PK
				// 同一个属性不能具有两个级别，同理同一个属性页不能同时有两个档别
				sbLineOnlyFlag.append(obj);
			}
			strRowPks[i] = sbLineOnlyFlag.toString();
			hmStdHi.put(sbLineOnlyFlag.toString(), String.valueOf(i));
		}
		validaterStdHinullNamecell(validatercell);
		validaterStdHinullLine(strRowPks);
		validaterStdHiRepLine(hmStdHi, strRowPks);
		return waStdHiSetVOs;
	}

	/**
	 * 取得画面上的VO数组
	 */
	public CrtVO[] disToCrtVO(BillModel billModel, UITable uiTable) {
		// 根据界面的显示形成更新树组
		CrtVO[] crtvo = getGradeModel().getCrtvos();
		// 直接修改crtvo即可
		if (crtvo == null) {
			return new CrtVO[0];
		}
		int cols = billModel.getColumnCount();
		for (int i = 0; i < crtvo.length; i++) {
			// 列的统计
			for (int j = 1; j < cols; j++) {
				Object obj = uiTable.getValueAt(i, j);
				if (obj == null || obj.equals(new UFDouble(0))) {
					obj = new UFDouble(0);
				}
				crtvo[i].setAttributeValue(
						billModel.getBodyItems()[j].getKey(), obj);
			}
		}
		return crtvo;
	}

	/**
	 * 停止单元格的编辑
	 * 
	 * @author xuhw on 2009-12-10
	 */
	public void stopCellExiting(String strTableCode) {
		if (billCardPanel.getBillTable(strTableCode).getCellEditor() != null) {
			billCardPanel.getBillTable(strTableCode).getCellEditor()
					.stopCellEditing();
		}
	}

	/**
	 * 设置画面VO
	 * 
	 * @author xuhw on 2009-12-10
	 * 
	 * @param supervos
	 * @param strTableCode
	 */
	public void setBodyDataVO(CircularlyAccessibleValueObject[] supervos,
			String strTableCode) {
		billCardPanel.getBillModel(strTableCode).setBodyDataVO(supervos);
		billCardPanel.getBillModel(strTableCode).setEnabled(false);
	}

	/**
	 * 取得人员相关属性VO<BR>
	 * 根据选择的人员相关属性，转换成对应的人员相关属性VO<BR>
	 * 
	 * @author xuhw on 2009-12-4
	 * @param vecPsnhivos
	 */
	public WaPsnhiVO[] getPsnHiVO() throws BusinessException {
		List<WaPsnhiVO> listPsnhivo = new ArrayList<WaPsnhiVO>();
		WaPsnhiVO[] psnhivos = null;
		int psnhicnt = 0;
		int psnhiseclvcnt = 0;
		getPsnHiVOSub(IWaGradeCommonDef.PK_WA_PSNHI, listPsnhivo);
		// 级别人员属性数量
		psnhicnt = listPsnhivo.size();
		getPsnHiVOSub(IWaGradeCommonDef.PK_WA_PSNHI_SECLV, listPsnhivo);
		// 档别级别人员属性数量之和
		psnhiseclvcnt = listPsnhivo.size();

		if ((Boolean) getHeadItem(WaGradeVO.ISMULTSEC).getValueObject()) {
			if ((psnhiseclvcnt > psnhicnt) && (psnhicnt == 0)) {
				Logger.debug("多档时，档别人员属性不为空，则级别人员属性也不能为空！");
				throw new BusinessException(ResHelper.getString("60130paystd",
						"060130paystd0185")/*
											 * @res
											 * "多档时，档别人员属性不为空，则级别人员属性也不能为空！"
											 */);
			} else if ((psnhiseclvcnt == psnhicnt) && (psnhicnt > 0)) {
				Logger.debug("多档时，级别人员属性不为空，则档别人员属性也不能为空！");
				throw new BusinessException(ResHelper.getString("60130paystd",
						"060130paystd0186")/*
											 * @res
											 * "多档时，级别人员属性不为空，则档别人员属性也不能为空！"
											 */);
			}

		}
		psnhivos = new WaPsnhiVO[listPsnhivo.size()];
		return listPsnhivo.toArray(psnhivos);
	}

	/**
	 * 取得人员相关属性VO<BR>
	 * 根据选择的人员相关属性，转换成对应的人员相关属性VO<BR>
	 * 
	 * @author xuhw on 2009-12-4
	 * @param vecPsnhivos
	 */
	private List<WaPsnhiVO> getPsnHiVOSub(String strPsnhiType,
			List<WaPsnhiVO> listPsnHiVOs) throws BusinessException {
		BillItem checkItem = billCardPanel.getHeadItem(strPsnhiType);
		Vector<?> vovector = (((UIRefPane) checkItem.getComponent())
				.getRefModel()).getSelectedData();
		WaPsnhiVO[] psnhivos = null;
		int intClassTtype = 0;
		if (vovector == null || vovector.size() == 0) {
			return listPsnHiVOs;
		} else {
			// 得到人员属性类别
			if (IWaGradeCommonDef.PK_WA_PSNHI.equals(strPsnhiType)) {
				intClassTtype = 1;
			} else {
				intClassTtype = 2;
			}
			psnhivos = new WaPsnhiVO[vovector.size()];
			for (int i = 0; i < vovector.size(); i++) {
				psnhivos[i] = new WaPsnhiVO();
				Vector<?> vt = (Vector<?>) vovector.get(i);
				// 1 薪资标准类别级别 2 薪资标准类别档别
				psnhivos[i].setClasstype(intClassTtype);
				psnhivos[i].setPk_flddict((String) vt
						.get(IWaGradeCommonDef.PK_FLDDICT));
				psnhivos[i].setPk_wa_grd(null);
				psnhivos[i].setVfldcode(vt.get(IWaGradeCommonDef.VFLDCODE)
						.toString());
				psnhivos[i].setVfldname(vt.get(IWaGradeCommonDef.VFLDNAME)
						.toString());
				psnhivos[i].setStatus(VOStatus.NEW);
				listPsnHiVOs.add(psnhivos[i]);
			}
		}
		return listPsnHiVOs;
	}

	/**
	 * 取得画面上显示的级别和档别VO
	 * 
	 * @return List
	 */
	public List<SuperVO[]> getDisPlayPrmAndSecVos() {
		List<SuperVO[]> lisDisPlayPrmAndSecVos = new ArrayList<SuperVO[]>();
		// 取得画面上的显示的级别VO
		WaPrmlvVO[] waPrmlvVOArr = (WaPrmlvVO[]) billCardPanel.getBillModel(
				IWaGradeCommonDef.WA_PRMLV).getBodyValueVOs(
				WaPrmlvVO.class.getName());
		// 取得画面上的显示的档别VO
		WaSeclvVO[] waSeclvVOArr = (WaSeclvVO[]) billCardPanel.getBillModel(
				IWaGradeCommonDef.WA_SECLV).getBodyValueVOs(
				WaSeclvVO.class.getName());
		lisDisPlayPrmAndSecVos.add(waPrmlvVOArr);
		lisDisPlayPrmAndSecVos.add(waSeclvVOArr);
		return lisDisPlayPrmAndSecVos;
	}

	/**
	 * 创建人员信息项值设置页签式样
	 * 
	 * @throws BusinessException
	 */
	public void createWaStdHiTable(PsnhiHeaderVO[] psnhvos, String strTabType) {
		initValues(strTabType);
		// 定义局部VO对象
		BillModel bmStdHi = getBillModel(strTabType);

		PsnhiHeaderVO[] hvos = null;
		List<PsnhiHeaderVO> hvosList = new ArrayList<PsnhiHeaderVO>();
		if (psnhvos == null) {
			tabEnabledSetting(strTabType);
			return;
		}
		// 从所有局部对象中取指定的局部对象
		for (PsnhiHeaderVO psnhvo : psnhvos) {
			if (IWaGradeCommonDef.WA_STD_HI.equals(strTabType)) {
				if (psnhvo.getClasstype().intValue() == IWaGradeCommonDef.CLASS_TYPE_PRMLV) {
					hvosList.add(psnhvo);
				}
			} else {
				if (psnhvo.getClasstype().intValue() == IWaGradeCommonDef.CLASS_TYPE_SECLV) {
					hvosList.add(psnhvo);
				}
			}
		}
		if (hvosList.size() > 0) {
			hvos = hvosList.toArray(new PsnhiHeaderVO[hvosList.size()]);
		}
		if (hvos == null) {
			tabEnabledSetting(strTabType);
			return;
		}
		int intPsnhiVos = hvos.length + 1;
		BillItem[] stdHiItems = new BillItem[intPsnhiVos];
		for (int i = 0; i < intPsnhiVos; i++) {
			stdHiItems[i] = new BillItem();
			stdHiItems[i].setWidth(100);
			stdHiItems[i].setEnabled(true);
			stdHiItems[i].setEdit(true);
			stdHiItems[i].setNull(false);
			if (intPsnhiVos > i + 1) {
				// 设置BillItem式样(金额级别档别值)
				setBillItemPsnHi(stdHiItems[i], hvos[i]);
			} else if (intPsnhiVos == i + 1) {
				// 设置BillItem式样(金额级别档别参照)
				setBillItemPrmSecRefModel(stdHiItems[i], classType);
			}
		}

		/**
		 * 2014-04-30 shenliangc 为实现级别人员属性选择职级之后，级别人员属性页签自动在职级字段之间添加职级类别字段。
		 */
		ArrayList<BillItem> itemList = new ArrayList<BillItem>();
		for (int i = 0; i < hvos.length; i++) {
			if (hvos[i].getVfldcode().equals("pk_jobgrade")) {
				this.pk_wa_psnhi = hvos[i].getPk_wa_psnhi();
				break;
			}
		}
		for (int i = 0; i < stdHiItems.length; i++) {
			if (stdHiItems[i].getKey().equals(this.pk_wa_psnhi)) {
				BillItem billItem = new BillItem();
				billItem.setName(ResHelper.getString("60130paystd",
						"060130paystd0255"));// 职级类别
				billItem.setKey("pk_joblevelsys");
				billItem.setWidth(100);
				billItem.setEnabled(true);
				billItem.setEdit(true);
				billItem.setNull(false);
				billItem.setEdit(false);
				itemList.add(billItem);
			}
			itemList.add(stdHiItems[i]);
		}
		/**
		 * 2014-04-30 shenliangc 为实现级别人员属性选择职级之后，级别人员属性页签自动在职级字段之间添加职级类别字段。
		 */

		// 创建项目
		bmStdHi.setBodyItems(itemList.toArray(new BillItem[0]));
		getBillCardPanel().setBillModel(strTabType, bmStdHi);
		getBillCardPanel().addEditListener(strTabType, this);
		UITable table = getBillCardPanel().getBillTable(strTabType);
		// 记录设置前的子表页签的位置
		int intSelectIndex = getBillCardPanel().getBodyTabbedPane()
				.getSelectedIndex();
		// 给表体中的参照字段增加一个保存主键的字段
		// xiejie3
		BillPanelUtils.dealWithRefField(getBillCardPanel(), strTabType);
		// 初始化数据
		initStdHiTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getBillCardPanel().getBodyTabbedPane().setSelectedIndex(intSelectIndex);

		// 20151223 shenliangc NCdp205564067 薪资未屏蔽右键菜单和表格中回车加行的功能的节点
		BillPanelUtils.disabledRightMenuAndAutoAddLine(getBillCardPanel()
				.getBodyPanel(strTabType));
		// 20151223 shenliangc NCdp205564067 薪资未屏蔽右键菜单和表格中回车加行的功能的节点
		getBillCardPanel().getBillTable(IWaGradeCommonDef.WA_STD_HI)
				.removeSortListener();
		getBillCardPanel().getBillTable(IWaGradeCommonDef.WA_STD_HI_SECLV)
				.removeSortListener();
		BillPanelUtils.disabledRightMenuAndAutoAddLine(getBillCardPanel()
				.getBodyPanel(IWaGradeCommonDef.WA_STD_HI));
		BillPanelUtils.disabledRightMenuAndAutoAddLine(getBillCardPanel()
				.getBodyPanel(IWaGradeCommonDef.WA_STD_HI_SECLV));
	}

	/**
	 * 设置表尾值
	 * 
	 * @param value
	 * @param strKey
	 */
	public void setTailCellValue(Object value, String strKey) {
		getBillCardPanel().getHeadItem(strKey).setValue(value);
	}

	/**
	 * 初始化各项需要的属性
	 */
	private void initValues(String tableCode) {
		AggWaGradeVO aggWagradevo = (AggWaGradeVO) getModel().getSelectedData();
		if (aggWagradevo != null) {
			WaGradeVO gradeVo = aggWagradevo.getParentVO();
			// 薪资标准设置表主表PK
			strPkWaGrd = gradeVo.getPk_wa_grd();
			// 薪资档别VO数组
			seclvVos = (WaSeclvVO[]) aggWagradevo
					.getTableVO(IWaGradeCommonDef.WA_SECLV);

			// 是否多档
			if (gradeVo.getIsmultsec() != null) {
				isMultsecCkeck = gradeVo.getIsmultsec().booleanValue();
			}

			// 是否宽带薪酬
			if (gradeVo.getIsrange() != null) {
				isRangeCkeck = gradeVo.getIsrange().booleanValue();
			}
			// 薪级和薪档区分
			this.tableCode = tableCode;

			if (tableCode != null) {
				if (IWaGradeCommonDef.WA_STD_HI.equals(tableCode)) {
					classType = ISalaryadjmgtConstant.PRMLV_TABLE_FLAG;
				} else if (IWaGradeCommonDef.WA_STD_HI_SECLV.equals(tableCode)) {
					classType = ISalaryadjmgtConstant.SECLV_TABLE_FLAG;
				}
			}
		}

		billModel = (WaGradeBillModel) getModel();
		// 清空项目名称
		lisColName.clear();
		// 清空项目key
		lisKeyName.clear();
		// 清空项目类型
		lisColType.clear();
		// 设置级别相关属性
		// 级别的名称
		lisColName
				.add(ResHelper.getString("60130paystd", "160130paystd0008")/*
																			 * @res
																			 * "级别"
																			 */);
		// 级别的key
		lisKeyName.add(WaCriterionVO.PRMLVNAME);
		// 级别类型 字符型
		lisColType.add(BillItem.STRING);

		// 清空多表头数组
		colNameMap.clear();

		// 初始标准类型
		initGradeType();
	}

	/**
	 * 四种类型<BR>
	 * 1)多级 非多档 非宽带薪酬<BR>
	 * 2)多级 非多档 宽带薪酬<BR>
	 * 3)多级 多档 宽带薪酬<BR>
	 * 4)多级 多档 宽带薪酬<BR>
	 * 
	 */
	private void initGradeType() {
		// 多级 非多档 非宽带薪酬
		if (!isMultsecCkeck && !isRangeCkeck) {
			gradeSetType = GradeSetType.SingleSeclv;
		}
		// 多级 非多档 宽带薪酬
		else if (!isMultsecCkeck && isRangeCkeck) {
			gradeSetType = GradeSetType.SingleSeclvRange;
		}
		// 多级 多档 非宽带薪酬
		else if (isMultsecCkeck && !isRangeCkeck) {
			gradeSetType = GradeSetType.MultSeclv;
		}
		// 多级 多档 宽带薪酬
		else {
			gradeSetType = GradeSetType.MultSeclvRange;
		}
	}

	/**
	 * 创建Table(多级 非多档 非宽带)
	 * 
	 * @throws BusinessException
	 */
	private void initPrmlvTable() throws BusinessException {

		if (seclvVos == null || seclvVos.length < 1) {
			// 档别的名称
			lisColName.add(ResHelper.getString("common", "UC000-0004112")/*
																		 * @res
																		 * "金额"
																		 */);
			// 档别key
			lisKeyName.add("criterionvalue");
			// 档别类型 币值型
			lisColType.add(BillItem.DECIMAL);
		}

		// 创建项目
		createCrtItems(lisColName, lisKeyName, lisColType);
	}

	/**
	 * 创建Table(多级 宽带 非多档)
	 * 
	 * @throws BusinessException
	 */
	private void initPrmlvRangeTable() throws BusinessException {
		if (seclvVos == null || seclvVos.length < 1) {
			// 名称的设定
			lisColName.add(ResHelper.getString("60130paystd",
					"060130paystd0180")/* @res "下限" */);
			lisColName.add(ResHelper.getString("60130paystd",
					"060130paystd0179")/* @res "基准值" */);
			lisColName.add(ResHelper.getString("60130paystd",
					"060130paystd0181")/* @res "上限" */);

			// key的设定
			// 档别的金额下限
			lisKeyName.add("pran_down_criterionvalue");
			// 档别的金额基准值
			lisKeyName.add("pran_basic_criterionvalue");
			// 档别的金额上限
			lisKeyName.add("pran_up_criterionvalue");

			// 类型的设定——币值型
			lisColType.add(BillItem.DECIMAL);
			lisColType.add(BillItem.DECIMAL);
			lisColType.add(BillItem.DECIMAL);
		}

		// 创建项目
		createCrtItems(lisColName, lisKeyName, lisColType);
		WaGradeColumnGroupTool groupTool = new WaGradeColumnGroupTool(
				getBillCardPanel().getBillTable(IWaGradeCommonDef.WA_CRT),
				getBillCardPanel().getBillModel(IWaGradeCommonDef.WA_CRT));
		groupTool
				.initColumnGroup(
						ResHelper.getString("60130paystd", "060130paystd0187")/*
																			 * @res
																			 * "一档"
																			 */,
						"pran_down_criterionvalue",
						"pran_basic_criterionvalue", "pran_up_criterionvalue");
	}

	/**
	 * 创建Table(多级 多档 非宽带)
	 * 
	 * @throws BusinessException
	 */
	private void initPrmlvReclvTable() throws BusinessException {
		if (seclvVos == null) {
			initPrmlvTable();
			return;
		}
		for (int i = 0; i < seclvVos.length; i++) {
			// 档别的name设定
			lisColName.add(seclvVos[i].getLevelname());
			// 档别的金额基准值key
			lisKeyName.add("prec_" + seclvVos[i].getPk_wa_seclv());
			// 档别类型的设定
			lisColType.add(BillItem.DECIMAL);
		}

		// 创建项目
		createCrtItems(lisColName, lisKeyName, lisColType);
	}

	/**
	 * 创建Table(多级 多档 宽带)
	 * 
	 * @throws BusinessException
	 */
	private void initPrmlvReclvRangeTable() throws BusinessException {
		if (seclvVos == null) {
			initPrmlvRangeTable();
			return;
		}

		for (int i = 0; i < seclvVos.length; i++) {
			// 名称的设定
			lisColName.add(ResHelper.getString("60130paystd",
					"060130paystd0180")/* @res "下限" */);
			lisColName.add(ResHelper.getString("60130paystd",
					"060130paystd0179")/* @res "基准值" */);
			lisColName.add(ResHelper.getString("60130paystd",
					"060130paystd0181")/* @res "上限" */);
			// key的设定
			// 档别的金额下限
			lisKeyName.add("precran_down_" + seclvVos[i].getPk_wa_seclv());
			// 档别的金额基准值
			lisKeyName.add("precran_basic_" + seclvVos[i].getPk_wa_seclv());
			// 档别的金额上限
			lisKeyName.add("precran_up_" + seclvVos[i].getPk_wa_seclv());

			// 类型的设定——币值型
			lisColType.add(BillItem.DECIMAL);
			lisColType.add(BillItem.DECIMAL);
			lisColType.add(BillItem.DECIMAL);
		}

		// 创建项目
		createCrtItems(lisColName, lisKeyName, lisColType);
		for (int i = 0; i < seclvVos.length; i++) {
			WaGradeColumnGroupTool groupTool = new WaGradeColumnGroupTool(
					getBillCardPanel().getBillTable(IWaGradeCommonDef.WA_CRT),
					getBillCardPanel().getBillModel(IWaGradeCommonDef.WA_CRT));
			groupTool.initColumnGroup(seclvVos[i].getLevelname(),
					"precran_down_" + seclvVos[i].getPk_wa_seclv(),
					"precran_basic_" + seclvVos[i].getPk_wa_seclv(),
					"precran_up_" + seclvVos[i].getPk_wa_seclv());
		}
	}

	/**
	 * 创建薪资标准类别值页签
	 * 
	 * @author xuhw on 2009-11-26
	 * @throws BusinessException
	 */
	private void createCrtItems(List<String> lisColName,
			List<String> lisKeyName, List<Integer> lisColType)
			throws BusinessException {
		saClsBodyColName = new String[lisColName.size()];
		saClsBodyColKeyName = new String[lisKeyName.size()];
		saClsBodyColType = new Integer[lisColType.size()];
		lisColName.toArray(saClsBodyColName);
		lisKeyName.toArray(saClsBodyColKeyName);
		lisColType.toArray(saClsBodyColType);
		BillItem[] biaBody = new BillItem[saClsBodyColName.length];

		// liang 取薪资项目的小数位数,设置标准表的小数位数
		// 作用：减少连接数
		AggWaGradeVO aggvo = (AggWaGradeVO) (((WaGradeBillModel) getModel())
				.getSelectedData());
		Integer iflddecimal = Integer.valueOf(0);
		if (aggvo != null && aggvo.getParentVO() != null
				&& aggvo.getParentVO().getIflddecimal() != null) {// 无数据时，默认为0精度
			iflddecimal = aggvo.getParentVO().getIflddecimal();
			// 保存的时候主VO中的iflddecimal会被清零，目前处理了新增保存，修改保存和版本信息保存时的清零情况！其他的情况直接查询处理。
			if (iflddecimal == 0) {
				WaItemVO itemvo = WASalaryadjmgtDelegator.getItemQueryService()
						.queryWaItemVOByPk(aggvo.getParentVO().getPk_wa_item());
				iflddecimal = itemvo.getIflddecimal();
				aggvo.getParentVO().setIflddecimal(iflddecimal);
			}
		}
		for (int i = 0; i < saClsBodyColName.length; i++) {
			biaBody[i] = new BillItem();
			biaBody[i].setName(saClsBodyColName[i]);
			biaBody[i].setKey(saClsBodyColKeyName[i]);
			biaBody[i].setWidth(100);
			biaBody[i].setEnabled(true);
			biaBody[i].setEdit(true);
			biaBody[i].setNull(false);
			if (saClsBodyColType[i] == BillItem.STRING) {
				// 字符型
				biaBody[i].setDataType(BillItem.STRING);
				biaBody[i].setEdit(false);
				biaBody[i].setEnabled(false);
			} else if (saClsBodyColType[i] == BillItem.DECIMAL) {
				// 币值型
				biaBody[i].setDataType(BillItem.DECIMAL);
				biaBody[i].setDecimalDigits(iflddecimal);

				// 整数部分长度不要超过12,小数位数还占一位
				int ifldlength = IWaAdjustConstants.IFLDLENGTH_WAADJUST + 1
						+ iflddecimal;
				biaBody[i].setLength(ifldlength);

				((UIRefPane) biaBody[i].getComponent()).getUITextField()
						.setMinValue(0);
			}
		}
		// 薪资标准值model
		BillModel bmCrt = getBillModel(IWaGradeCommonDef.WA_CRT);
		bmCrt.setBodyItems(biaBody);
		getBillCardPanel().setBillModel(IWaGradeCommonDef.WA_CRT, bmCrt);
		UITable table = getBillCardPanel().getBillTable(
				IWaGradeCommonDef.WA_CRT);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		initGradeVerInfo(isMultsecCkeck, strPkWaGrd);
		// 自动对列的宽度进行调整的话，如果列的数量太多的时候就不会有滚动条，而且特别拥挤显得
		// table.setAutoResizeMode(table.AUTO_RESIZE_LAST_COLUMN);
	}

	/**
	 * 初始化表体的数据
	 */
	public void initGradeVerInfo(boolean isMultsec, String strPkWaGrd)
			throws BusinessException {
		WaGradeVerVO gradevervo = getGradeModel().queryCriterionByClassid(
				isMultsec, strPkWaGrd);
		getBillCardPanel().getBillTable(IWaGradeCommonDef.WA_CRT)
				.setSortEnabled(false);

		// 2016-11-29 zhousze 薪资加密：查询薪资标准表数据时，解密数据 begin
		CrtVO[] crtVOs = gradevervo.getCrtVOs();
		for (CrtVO vo : crtVOs) {
			ArrayList<Object> list = (ArrayList<Object>) vo.getVecData();
			for (Object waCriVO : list) {
				WaCriterionVO vo1 = (WaCriterionVO) waCriVO;
				vo1.setCriterionvalue(new UFDouble(
						SalaryDecryptUtil
								.decrypt((vo1.getCriterionvalue() == null ? new UFDouble(
										0) : vo1.getCriterionvalue())
										.toDouble())));
				vo1.setMax_value(new UFDouble(SalaryDecryptUtil.decrypt((vo1
						.getMax_value() == null ? new UFDouble(0) : vo1
						.getMax_value()).toDouble())));
				vo1.setMin_value(new UFDouble(SalaryDecryptUtil.decrypt((vo1
						.getMin_value() == null ? new UFDouble(0) : vo1
						.getMin_value()).toDouble())));
			}
		}
		getGradeModel().setCrtvos(crtVOs);
		// end
		// getGradeModel().setCrtvos(gradevervo.getCrtVOs());
		if (StringUtils.isBlank(gradevervo.getPk_wa_gradever())) {
			initGradeVerData(gradevervo);
			return;
		}
		verInfoDisplay(gradevervo);
	}

	/**
	 * 设置人员相关属性参照的内容
	 * 
	 * @author xuhw on 2009-12-7
	 */
	private void setPsnhiProperty(PsnhiHeaderVO[] psnhvos) {
		// 设置人员相关属性参照
		UIRefPane refPane = (UIRefPane) getHeadItemPK_WA_PSNHI().getComponent();
		UIRefPane refSeclvPane = (UIRefPane) getHeadItem(
				IWaGradeCommonDef.PK_WA_PSNHI_SECLV).getComponent();

		// 清空人员属性相关内容
		refPane.setText("");
		refPane.getUITextField().setText("");
		refPane.setPKs(null);
		refSeclvPane.setText("");
		refSeclvPane.getUITextField().setText("");
		refSeclvPane.setPKs(null);

		StringBuffer sbPsnhiName = new StringBuffer();
		StringBuffer sbPsnhiSecName = new StringBuffer();
		// 给pk赋值
		List<String> psnhiPKs = new ArrayList<String>();
		List<String> psnhiSevPKs = new ArrayList<String>();
		if (psnhvos == null || psnhvos.length == 0) {
			return;
		}

		for (int i = 0; i < psnhvos.length; i++) {
			if (psnhvos[i].getClasstype() == null) {
				continue;
			}
			String vfldname = psnhvos[i].getVfldname();
			if (!StringUtils.isEmpty(ResHelper.getString(
					psnhvos[i].getRespath(), psnhvos[i].getResid()))) {
				vfldname = ResHelper.getString(psnhvos[i].getRespath(),
						psnhvos[i].getResid());
			}
			if (psnhvos[i].getClasstype() == IWaGradeCommonDef.CLASS_TYPE_PRMLV) {
				sbPsnhiName.append(vfldname + ",");
				psnhiPKs.add(psnhvos[i].getPk_flddict());
			} else if (psnhvos[i].getClasstype() == IWaGradeCommonDef.CLASS_TYPE_SECLV) {
				sbPsnhiSecName.append(vfldname + ",");
				psnhiSevPKs.add(psnhvos[i].getPk_flddict());
			}
		}

		if (sbPsnhiName.length() != 0) {
			String strPsnhiName = sbPsnhiName.toString();
			strPsnhiName = strPsnhiName.substring(0,
					strPsnhiName.lastIndexOf(','));
			refPane.setPKs(psnhiPKs.toArray(new String[0]));
			refreshRef(IWaGradeCommonDef.PK_WA_PSNHI_SECLV,
					psnhiPKs.toArray(new String[0]));
			// refPane.setText(strPsnhiName);
			// refPane.getUITextField().setText(strPsnhiName);
		}
		if (sbPsnhiSecName.length() != 0) {
			String strPsnhiName = sbPsnhiSecName.toString();
			strPsnhiName = strPsnhiName.substring(0,
					strPsnhiName.lastIndexOf(','));
			refSeclvPane.setPKs(psnhiSevPKs.toArray(new String[0]));
			refreshRef(IWaGradeCommonDef.PK_WA_PSNHI,
					psnhiSevPKs.toArray(new String[0]));
			// refSeclvPane.setText(strPsnhiName);
			// refSeclvPane.getUITextField().setText(strPsnhiName);
		}
	}

	/**
	 * 设置项目参照类型
	 * 
	 * @author xuhw on 2009-12-8
	 * 
	 * @param type
	 * @param s
	 * @return
	 */
	private PsnInfFldRefPane setBillItemType(int type, String s) {
		if (s == null) {
			return null;

		}
		if (type == IBillItem.UFREF) {
			return new PsnInfFldRefPane(s, (WaGradeBillModel) getModel());
		} else if (type == IBillItem.DATE) {
			return new PsnInfFldRefPane(ResHelper.getString("60130paystd",
					"060130paystd0188")/* @res "日历" */,
					(WaGradeBillModel) getModel());
		} else {
			return null;
		}
	}

	private UIComboBox setBillItemType2(String strPk) {
		UIComboBox psnHiRefComboBox = new UIComboBox();
		GeneralVO[] bdEnumValues = new GeneralVO[] {};
		try {
			bdEnumValues = ((WaGradeBillModel) getModel())
					.queryBdEnumValues(strPk);

		} catch (BusinessException e) {
			Logger.error("取得人员相关属性发生异常", e);
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(),
					null,
					ResHelper.getString("60130paystd", "060130paystd0189")/*
																		 * @res
																		 * "取得人员相关属性发生异常。"
																		 */);
			return null;
		}

		psnHiRefComboBox.removeAll();
		if (!ArrayUtils.isEmpty(bdEnumValues)) {
			for (GeneralVO bdEnumValue : bdEnumValues) {
				/*
				 * Vector vectField =new Vector(); vectField.addElement(new
				 * Pair<
				 * String>(bdEnumValue.getAttributeValue("name").toString(),
				 * bdEnumValue.getAttributeValue("value").toString()));
				 */
				String name = "";
				if (bdEnumValue.getAttributeValue("resmodule") == null
						|| bdEnumValue.getAttributeValue("resid") == null) {
					name = (String) bdEnumValue.getAttributeValue("name");
				} else {
					name = ResHelper
							.getString((String) bdEnumValue
									.getAttributeValue("resmodule"),
									(String) bdEnumValue
											.getAttributeValue("resid"));
				}
				psnHiRefComboBox.addItem(new DefaultConstEnum(bdEnumValue
						.getAttributeValue("value").toString(),
				// bdEnumValue.getAttributeValue("name").toString()));
						name));
			}
		}

		return psnHiRefComboBox;
	}

	/**
	 * 设置BillItem式样(金额级别档别参照)
	 * 
	 * @author xuhw on 2009-12-16
	 */
	private void setBillItemPrmSecRefModel(BillItem stdHiItem, int classType) {
		stdHiItem.setName(ResHelper
				.getString("60130paystd", "160130paystd0016")/* @res "薪级" */);
		AbstractRefModel prmSecRef = new WaPrmlvRefModel(strPkWaGrd);
		if (classType == IWaGradeCommonDef.CLASS_TYPE_SECLV) {
			stdHiItem.setName(ResHelper.getString("60130paystd",
					"160130paystd0018")/* @res "薪档" */);
			prmSecRef = new WaSeclvRefModel(strPkWaGrd);
		}

		stdHiItem.setKey(IWaGradeCommonDef.PRMLVSECLV);
		UIRefPane psnStdRef = new UIRefPane();
		psnStdRef.setButtonFireEvent(true);
		psnStdRef.setRefModel(prmSecRef);
		psnStdRef.setReturnCode(false);
		stdHiItem.setComponent(psnStdRef);
		stdHiItem.setNull(true);
		stdHiItem.setDataType(IBillItem.UFREF);
	}

	/**
	 * 设置BillItem式样(金额级别档别值)
	 * 
	 * @author xuhw on 2009-12-16
	 */
	private void setBillItemPsnHi(BillItem stdHiItem, PsnhiHeaderVO hvo) {
		// stdHiItem.setName(ResHelper.getString(hvo.getRespath(),
		// hvo.getResid()).equals("")?hvo.getVfldname():ResHelper.getString(hvo.getRespath(),
		// hvo.getResid()));

		// shenliangc 20140902
		// 由于wa_psnhi表中没有vfldname的多语字段，只能从hr_infoset_item中取得繁体中文vfldname字段。
		// 解决自定义信息项输入了多个语种名称之后繁体中文登陆在薪资标准子表级别人员属性页签表头显示简体中文的问题。
		stdHiItem.setName(hvo.getVfldname());
		stdHiItem.setKey(hvo.getPk_wa_psnhi());
		JComponent psnHiRef = null;
		if (hvo.getData_type() != IBillItem.COMBO) {
			psnHiRef = setBillItemType(hvo.getData_type(),
					hvo.getRef_model_name());
		} else {
			psnHiRef = setBillItemType2(hvo.getPk_flddict());
		}
		if (psnHiRef != null) {
			stdHiItem.setComponent(psnHiRef);
			if (hvo.getData_type() == IBillItem.UFREF) {
				stdHiItem.setDataType(IBillItem.UFREF);
			} else if (hvo.getData_type() == IBillItem.COMBO) {
				stdHiItem.setDataType(IBillItem.COMBO);
			}
		}
	}

	/**
	 * 取得人员相关属性ByGrdPK
	 * 
	 * @author xuhw on 2009-12-16
	 * 
	 * @return PsnhiHeaderVO[]
	 */
	public PsnhiHeaderVO[] findPsnHiByStdPK() {
		PsnhiHeaderVO[] hvos = null;
		// if(cachedPsnhiHeaderVOs.containsKey(strPkWaGrd)){
		// hvos = cachedPsnhiHeaderVOs.get(strPkWaGrd);
		// return hvos;
		// }
		try {
			hvos = getGradeModel().findPsnHiByStdPK(strPkWaGrd);
			// getGradeModel().getCachedPsnhiHeaderVOs().put(strPkWaGrd, hvos);
		} catch (Exception ex) {
			Logger.error("取得人员相关属性发生异常", ex);
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(),
					null,
					ResHelper.getString("60130paystd", "060130paystd0189")/*
																		 * @res
																		 * "取得人员相关属性发生异常。"
																		 */);
			return null;
		}
		return hvos;
	}

	/**
	 * 初始化薪资人员相关属性的设置页签
	 * 
	 * @author xuhw on 2009-12-9
	 * @throws BusinessException
	 */
	private void initStdHiTable() {
		if (billModel == null) {
			billModel = getGradeModel();
		}
		WaStdHiSetVO[] waStdHiVOs = null;
		try {
			waStdHiVOs = billModel.queryStdHiByGrdPk(strPkWaGrd, classType);
		} catch (BusinessException e) {
			Logger.error("生成薪资人员相关属性的设置页签失败！", e);
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(),
					null,
					ResHelper.getString("60130paystd", "060130paystd0190")/*
																		 * @res
																		 * "生成薪资人员相关属性的设置页签失败！"
																		 */);
			return;
		}

		// 设置界面显示
		getBillModel(tableCode).setBodyDataVO(waStdHiVOs);
		BillPanelUtils.dealWithRefShowNameByPk(getBillCardPanel(), tableCode);
	}

	/**
	 * 保存前的相关校验处理<BR>
	 * 校验是否有行的内容重复的情况<BR>
	 * 
	 * @author xuhw on 2009-12-11
	 * 
	 * @param ht
	 * @param pks
	 * @throws BusinessException
	 */
	private void validaterStdHiRepLine(HashMap<String, String> hmStdHi,
			String[] pks) throws BusinessException {
		for (int i = 0; i < pks.length; i++) {
			String strRowLine = hmStdHi.get(pks[i]);
			if (!StringUtils.isBlank(strRowLine)
					&& !(strRowLine.equals(Integer.toString(i)))) {
				Logger.debug(+(i) + "/" + strRowLine + "行属性组合重复" + "!");
				throw new BusinessException(
						(i + 1)
								+ "/"
								+ (Integer.valueOf(strRowLine) + 1)
								+ ResHelper.getString("60130paystd",
										"060130paystd0191")/* @res "行属性组合重复" */
								+ "!");
			}
		}
	}

	// 20160106 xiejie3 NCdp205569417 当新增薪资标准时，设置属性为空点击保存，可保存，不提示。应提示为null。
	/**
	 * 保存前的相关校验处理<BR>
	 * 校验是否有行的内容为 null 或者为 "" 的情况<BR>
	 * 
	 * @author xiejie3 on 2016-01-06
	 * 
	 * @param ht
	 * @param pks
	 * @throws BusinessException
	 */
	private void validaterStdHinullLine(String[] pks) throws BusinessException {
		StringBuffer nullNumber = new StringBuffer();
		for (int i = 0; i < pks.length; i++) {

			if (StringUtils.isBlank(pks[i])) {
				if (StringUtils.isBlank(nullNumber.toString())) {
					nullNumber.append(i);
				} else {
					nullNumber.append("," + i);
				}
			}
		}
		if (!StringUtils.isBlank(nullNumber.toString())) {
			Logger.debug(nullNumber.toString() + "行属性组合为空" + "!");
			throw new BusinessException(nullNumber.toString()
					+ ResHelper.getString("60130paystd", "060130paystd0256")/*
																			 * @res
																			 * "行属性组合为空"
																			 */
					+ "!");
		}

	}

	// end NCdp205569417
	// 20160106 xiejie3 NCdp205569417 当部门/岗位撤销后，薪资标准人员属性设置，属性为空可保存
	/**
	 * 保存前的相关校验处理<BR>
	 * 校验单元格的值是否发生了变化，例如部门撤销，界面部门的名字已经无法查出，所以为null，此时修改，要进行校验，如果pk有值，而name无值，
	 * 则说明此参照属性已经被修改，需要通知用户重新选择<BR>
	 * 
	 * @author xiejie3 on 2016-01-06
	 * 
	 * @param ht
	 * @param pks
	 * @throws BusinessException
	 */
	private void validaterStdHinullNamecell(StringBuffer validatercell)
			throws BusinessException {

		if (!StringUtils.isBlank(validatercell.toString())) {
			Logger.debug(validatercell.toString()
					+ ResHelper.getString("60130paystd", "060130paystd0257")/*
																			 * @res
																			 * "属性值发生变化，请重新设置属性值！"
																			 */);
			throw new BusinessException(validatercell.toString()
					+ ResHelper.getString("60130paystd", "060130paystd0257")/*
																			 * @res
																			 * "属性值发生变化，请重新设置属性值！"
																			 */);
		}

	}

	// end NCdp205569417

	@Override
	public void addTabbedPaneAwareComponentListener(
			ITabbedPaneAwareComponentListener l) {
		tabbedPaneAwareComponent.addTabbedPaneAwareComponentListener(l);
	}

	@Override
	public boolean canBeHidden() {
		setSeclvEnabled();
		if (getModel().getUiState() == UIState.ADD
				|| getModel().getUiState() == UIState.EDIT
				|| getGradeModel().getOprationFlag() == OprationFlag.CrtEdit
				|| getGradeModel().getOprationFlag() == OprationFlag.StdhiEdit
				|| getGradeModel().getOprationFlag() == OprationFlag.StdhiSecEdit) {
			return false;
		}
		return tabbedPaneAwareComponent.canBeHidden();
	}

	/**
	 * 设置表头项目能否编辑<BR>
	 * <BR>
	 * 
	 * @author xuhw on 2010-3-31
	 * 
	 * @param strItemName
	 */
	private void setHeadItemEdit(String strItemName, boolean isEdit) {
		getHeadItem(strItemName).setEdit(isEdit);
	}

	/**
	 * 设置表头项目的可用性<BR>
	 * <BR>
	 * 
	 * @author xuhw on 2010-3-31
	 * 
	 * @param isEdit
	 */
	private void setHeadItemsEdit(boolean isEdit) {
		// 设置是否多档check可用
		setHeadItemEdit(WaGradeVO.ISMULTSEC, isEdit);
		// 设置是否宽带酬薪check可用
		setHeadItemEdit(WaGradeVO.ISRANGE, isEdit);
		// 设置级别金额排列方式的可用性
		setHeadItemEdit(WaGradeVO.PRMLV_MONEY_SORT, isEdit);
		// 设置档别金额排列方式的可用性
		setHeadItemEdit(WaGradeVO.SECLV_MONEY_SORT, isEdit);
		setHeadItemEdit(IWaGradeCommonDef.PK_WA_PSNHI, isEdit);
		// 设置档别金额排列方式的可用性
		setHeadItemEdit(IWaGradeCommonDef.PK_WA_PSNHI_SECLV, isEdit);
		setHeadItemEdit(IWaGradeCommonDef.PK_WA_PSNHI, isEdit);
		// 设置档别金额排列方式的可用性
		setHeadItemEdit(IWaGradeCommonDef.PK_WA_PSNHI_SECLV, isEdit);
		((UIRefPane) getHeadItemPK_WA_PSNHI().getComponent()).getUITextField()
				.setEnabled(isEdit);
		((UIRefPane) getHeadItem(IWaGradeCommonDef.PK_WA_PSNHI_SECLV)
				.getComponent()).getUITextField().setEnabled(isEdit);
	}

	/**
	 * 重置薪资标准表数据
	 */
	public void resetCrtTableData() {
		getBillCardPanel().stopEditing();
		int cols = billCardPanel.getBillModel(IWaGradeCommonDef.WA_CRT)
				.getColumnCount();
		int rows = billCardPanel.getBillModel(IWaGradeCommonDef.WA_CRT)
				.getRowCount();
		for (int i = 0; i < rows; i++) {
			// 列的统计
			for (int j = 1; j < cols; j++) {
				billCardPanel.getBillTable(IWaGradeCommonDef.WA_CRT)
						.setValueAt(0, i, j);
			}
		}

		// 重新初始标准展示图形
		// {
		// chartPanel.setAggGradeVO((AggWaGradeVO)
		// getModel().getSelectedData());
		// chartPanel.setCrtVOs(disToCrtVO(billCardPanel.getBillModel(IWaGradeCommonDef.WA_CRT),
		// billCardPanel.getBillTable(IWaGradeCommonDef.WA_CRT)));
		// chartPanel.reload();
		// }

	}

	// ------------------------------------------------------------------------------------------------------------------------------
	/**
	 * 取得界面上的薪资版本相关信息
	 * 
	 * @return
	 */
	public WaGradeVerVO getGradeVerVO() {
		WaGradeVerVO vervo = new WaGradeVerVO();
		vervo.setPk_wa_gradever((String) getHeadItem(
				WaGradeVerVO.PK_WA_GRADEVER).getValueObject());
		vervo.setGradever_name((String) getHeadItem(WaGradeVerVO.GRADEVER_NAME)
				.getValueObject());
		vervo.setPk_wa_grd((String) getHeadItem(WaGradeVerVO.PK_WA_GRD)
				.getValueObject());
		vervo.setVer_create_date((UFDate) getHeadItem(
				WaGradeVerVO.VER_CREATE_DATE).getValueObject());
		vervo.setEffect_flag(UFBoolean.valueOf((Boolean) getHeadItem(
				WaGradeVerVO.EFFECT_FLAG).getValueObject()));
		vervo.setGradever_num(new UFDouble(getHeadItem(
				WaGradeVerVO.GRADEVER_NUM).getValueObject() == null ? "0"
				: getHeadItem(WaGradeVerVO.GRADEVER_NUM).getValueObject()
						.toString()));
		vervo.setCrtVOs(disToCrtVO(
				billCardPanel.getBillModel(IWaGradeCommonDef.WA_CRT),
				billCardPanel.getBillTable(IWaGradeCommonDef.WA_CRT)));
		if (getGradeModel().getOprationFlag() == OprationFlag.GradeverAdd) {
			vervo.setStatus(VOStatus.NEW);
		}
		return vervo;
	}

	/**
	 * 标准信息画面回显
	 * 
	 * @param verVO
	 */
	public void verInfoDisplay(WaGradeVerVO verVO) {
		setBodyDataVO(verVO.getCrtVOs(), IWaGradeCommonDef.WA_CRT);
		getHeadItem(WaGradeVerVO.PK_WA_GRADEVER).setValue(
				verVO.getPk_wa_gradever());
		getHeadItem(WaGradeVerVO.GRADEVER_NAME).setValue(
				verVO.getGradever_name());
		getHeadItem(WaGradeVerVO.VER_CREATE_DATE).setValue(
				verVO.getVer_create_date());
		getHeadItem(WaGradeVerVO.EFFECT_FLAG).setValue(verVO.getEffect_flag());
		getHeadItem(WaGradeVerVO.GRADEVER_NUM)
				.setValue(verVO.getGradever_num());
		if (verVO.getAttributeValue("GradeTS") != null)
			getHeadItem("ts").setValue(verVO.getGradets());

	}

	/**
	 * 设置版本相关属性的可编辑性
	 * 
	 * @param blnIsE
	 */
	public void setItemsEnableWhenVerAdd(OprationFlag oprationFlag) {
		if (getGradeModel().getOprationFlag() == OprationFlag.GradeverAdd) {
			resetCrtTableData();
			// 甚至版本项目的默认值
			getHeadItem(WaGradeVerVO.PK_WA_GRADEVER).setValue(null);
			getHeadItem(WaGradeVerVO.GRADEVER_NAME).setValue(null);
			getHeadItem(WaGradeVerVO.VER_CREATE_DATE).setValue(null);
			getHeadItem(WaGradeVerVO.EFFECT_FLAG).setValue(null);
			getHeadItem(WaGradeVerVO.VER_CREATE_DATE).setValue(
					PubEnv.getServerDate());
		}
		// 设置版本信息项目的可编辑性
		getHeadItem(WaGradeVerVO.PK_WA_GRADEVER).setEnabled(false);
		getHeadItem(WaGradeVerVO.GRADEVER_NAME).setEnabled(false);
		getHeadItem(WaGradeVerVO.VER_CREATE_DATE).setEnabled(false);
		getHeadItem(WaGradeVerVO.EFFECT_FLAG).setEnabled(false);
		getBillModel(IWaGradeCommonDef.WA_CRT).setEnabled(
				OprationFlag.CrtEdit == oprationFlag);
	}

	/**
	 * 初始版本数据
	 */
	private void initGradeVerData(WaGradeVerVO gradevervo) {
		setBodyDataVO(gradevervo.getCrtVOs(), IWaGradeCommonDef.WA_CRT);
		// 甚至版本项目的默认值
		getHeadItem(WaGradeVerVO.PK_WA_GRADEVER).setValue(null);
		getHeadItem(WaGradeVerVO.GRADEVER_NAME).setValue(null);
		getHeadItem(WaGradeVerVO.VER_CREATE_DATE).setValue(null);
		getHeadItem(WaGradeVerVO.EFFECT_FLAG).setValue(null);
	}

	public GradeSetType getGradeSetType() {
		return gradeSetType;
	}

	public WaSeclvVO[] getSeclvVos() {
		return seclvVos;
	}

	public String getStrPkWaGrd() {
		return strPkWaGrd;
	}

	public void setStrPkWaGrd(String strPkWaGrd) {
		this.strPkWaGrd = strPkWaGrd;
	}

	public boolean isMultsecCkeck() {
		return isMultsecCkeck;
	}

	public boolean isRangeCkeck() {
		return isRangeCkeck;
	}

	/**
	 * 取得表头项目<BR>
	 * <BR>
	 * 
	 * @author xuhw on 2010-3-31
	 */
	public BillItem getHeadItem(String strItemName) {
		return getBillCardPanel().getHeadItem(strItemName);
	}

	/**
	 * 取得级别表头项目<BR>
	 * <BR>
	 * 
	 * @author xuhw on 2010-3-31
	 */
	public BillItem getHeadItemPK_WA_PSNHI() {
		return getBillCardPanel().getHeadItem(IWaGradeCommonDef.PK_WA_PSNHI);
	}

	public WaGradeBillModel getGradeModel() {
		return (WaGradeBillModel) getModel();
	}

	private BillModel getBillModel(String strTableCode) {
		return getBillCardPanel().getBillModel(strTableCode);
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {
	}

	@Override
	public boolean isComponentVisible() {
		return tabbedPaneAwareComponent.isComponentVisible();
	}

	@Override
	public void setComponentVisible(boolean visible) {
		tabbedPaneAwareComponent.setComponentVisible(visible);
	}

	@Override
	public void setAutoShowUpEventListener(IAutoShowUpEventListener l) {
		autoShowUpComponent.setAutoShowUpEventListener(l);
	}

	public IFunNodeClosingListener getClosingListener() {
		return closingListener;
	}

	public void setClosingListener(IFunNodeClosingListener closingListener) {
		this.closingListener = closingListener;
	}

	@Override
	public void showMeUp() {
		autoShowUpComponent.showMeUp();
	}

	@Override
	protected void onAdd() {
		super.onAdd();
		showMeUp();
		if (getGradeModel().isCopying()) {
			AggWaGradeVO aggVO = ((AggWaGradeVO) getGradeModel()
					.getSelectedData()).clone();
			// 如果是组织节点复制集团数据，人力资源组织赋值为当前组织
			if (getModel().getContext().getNodeType() == NODE_TYPE.ORG_NODE
					&& getModel().getContext().getPk_org() != null) {
				aggVO.getParentVO().setPk_org(
						getModel().getContext().getPk_org());
			}
			setValue(aggVO);
		}
	}

	@Override
	protected void onEdit() {
		super.onEdit();
		showMeUp();
		UIRefPane refPane = (UIRefPane) getHeadItemPK_WA_PSNHI().getComponent();
		UIRefPane refPaneOther = (UIRefPane) getHeadItem(
				IWaGradeCommonDef.PK_WA_PSNHI_SECLV).getComponent();
		refPane.getRefModel().getData();
		refPaneOther.getRefModel().getData();
	}

	@Override
	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	// public void onRefresh() throws BusinessException{
	// AggregatedValueObject aggVO = (AggregatedValueObject) getValue();
	//
	// SuperVO oldVO = (SuperVO) aggVO.getParentVO();
	//
	//
	// NCObject newVO =
	// MDPersistenceService.lookupPersistenceQueryService().queryBillOfNCObjectByPK(oldVO.getClass(),
	// oldVO.getPrimaryKey());
	//
	// billModel.directlyUpdate(newVO.getContainmentObject());
	// }
}
