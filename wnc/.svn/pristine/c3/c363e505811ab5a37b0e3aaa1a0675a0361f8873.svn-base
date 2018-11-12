package nc.ui.ta.psncalendar.view;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.IActionCode;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ITBMPsndocManageMaintain;
import nc.itf.ta.ITBMPsndocQueryMaintain;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.rbac.core.dataperm.DataPermissionFacade;
import nc.ui.bd.ref.AbstractRefGridTreeModel;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UISplitPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.ta.calendar.pub.CalendarAppEventConst;
import nc.ui.ta.calendar.pub.CalendarTempletCreator;
import nc.ui.ta.calendar.pub.DateHeaderCellRenderer;
import nc.ui.ta.psncalendar.model.PsnCalendarAppModel;
import nc.ui.ta.psncalendar.model.PsnCalendarAppModelDataManager;
import nc.ui.ta.pub.standardpsntemplet.PsnTempletUtils;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.RowOperationInfo;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;
import nc.vo.ta.psndoc.TBMPsndocVO;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 按日期查看的panel
 * 
 * @author zengcheng
 * 
 */
public class GridPanel extends AbstractPanel implements BillEditListener2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = -280144646662709037L;

	private BillListPanel gridBillListPanel;// 用时间段显示工作日历的panel，用表体显示
	private UISplitPane splitPane;
	private PsnCalendarAppModelDataManager manager;

	public PsnCalendarAppModelDataManager getManager() {
		return manager;
	}

	public void setManager(PsnCalendarAppModelDataManager manager) {
		this.manager = manager;
	}

	public GridPanel() {

	}

	/**
	 * 处理参照的pk_org
	 */
	// protected void processRefModel(){
	// BillItem[] billItems =
	// getGridBillListPanel().getHeadBillModel().getBodyItems();
	// if(org.apache.commons.lang.ArrayUtils.isEmpty(billItems))
	// return;
	// for(BillItem item:billItems){
	// if(item.getComponent()==null||!(item.getComponent() instanceof
	// UIRefPane))
	// continue;
	// UIRefPane refPane = (UIRefPane)item.getComponent();
	// if(refPane.getRefModel()==null)
	// continue;
	// refPane.getRefModel().setPk_org(model.getContext().getPk_org());
	// }
	// }

	public void initUI() {
		setLayout(new CardLayout());
		addTabbedPaneAwareComponentListener(tabbedAwareListener);
		getSplitPane().setTopComponent(getGridBillListPanel());
		UIPanel descPanel = createDescriptionPanel();
		getSplitPane().setBottomComponent(descPanel);
		add(getSplitPane(), getSplitPane().getName());
		// 默认显示业务日期所属期间的开始日期和结束日期，若没有所属期间，则显示业务日期的自然月的第一天和最后一天
		// 2012-04-25注释，由DataManager初始model时发事件
		// UFDate busDate = WorkbenchEnvironment.getInstance().getBusiDate();
		// UFLiteralDate busLiteralDate =
		// UFLiteralDate.getDate(busDate.toString().substring(0, 10));
		// PeriodVO period = null;
		// try {
		// period =
		// NCLocator.getInstance().lookup(IPeriodQueryService.class).queryByDate(model.getContext().getPk_org(),
		// busLiteralDate);
		// } catch (BusinessException e) {
		// Logger.error(e.getMessage(), e);
		// return;
		// }
		// //有对应期间则是期间第一天，否则是自然月第一天
		// UFLiteralDate beginDate =
		// period!=null?period.getBegindate():UFLiteralDate.getDate(busDate.toString().substring(0,
		// 8)+"01");
		// //有对应期间则是期间最后一天，否则是自然月最后一天
		// UFLiteralDate endDate =
		// period!=null?period.getEnddate():UFLiteralDate.getDate(busDate.toString().substring(0,
		// 8)+UFLiteralDate.getDaysMonth(busDate.getYear(),
		// busDate.getMonth()));
		// getPsnCalendarAppModel().setBeginEndDate(beginDate, endDate);
		// getPsnCalendarAppModel().setBeginEndDate(beginDate, endDate);
	}

	public BillListPanel getGridBillListPanel() {
		if (gridBillListPanel == null) {
			gridBillListPanel = new BillListPanel();
		}
		return gridBillListPanel;
	}

	@Override
	protected UIPanel createDescriptionPanel() {
		UIPanel descPanel = super.createDescriptionPanel();
		descPanel.setPreferredSize(new Dimension(270, 20));
		return descPanel;
	}

	/**
	 * 按日期范围初始化grid，编码姓名组织部门之外，一个日期一列
	 * 
	 * @param beginDate
	 * @param endDate
	 */
	@SuppressWarnings("restriction")
	protected void initGridBillListPanel(UFLiteralDate beginDate, UFLiteralDate endDate) {
		BillTempletVO btv = CalendarTempletCreator.createPsnCalendarBillTempletVO(beginDate, endDate);
		getSplitPane().remove(getGridBillListPanel());
		// 此处gridBillListPanel必须重新new一次，不然第二次调用initGridBillListPanel的时候，构造的单据模板不正确，原因未知
		gridBillListPanel = new BillListPanel();
		getSplitPane().setTopComponent(getGridBillListPanel());
		BillListPanel billListPanel = gridBillListPanel;
		// 去除右键功能和回车加行的功能
		BillPanelUtils.disabledRightMenuAndAutoAddLine(billListPanel);
		BillListData data = new BillListData(btv);
		billListPanel.setListData(data);
		billListPanel.setEnabled(false);
		BillPanelUtils.dealWithRefField(billListPanel);
		// 循环处理所有的班次参照，设置为树表型(单据模板有bug，会导致树表型参照只显示表格)
		BillItem[] items = data.getHeadItems();
		if (!org.apache.commons.lang.ArrayUtils.isEmpty(items))
			for (BillItem item : items) {
				JComponent comp = item.getComponent();
				if (comp == null || !(comp instanceof UIRefPane))
					continue;
				AbstractRefModel refModel = ((UIRefPane) comp).getRefModel();
				if (refModel instanceof AbstractRefGridTreeModel)
					((UIRefPane) comp).setRefType(IRefConst.GRIDTREE);
			}
		TableColumnModel columnModel = billListPanel.getHeadTable().getColumnModel();
		// 判断是否是日期列的pattern
		Pattern datePattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}.+");
		DateHeaderCellRenderer renderer = new DateHeaderCellRenderer();
		PsnCalendarTableCellRenderer cellRenderer = new PsnCalendarTableCellRenderer();
		cellRenderer.setModel(getPsnCalendarAppModel());
		// 循环处理所有列，如果是日期列，则使用DateHeaderRenderer，此renderer能自动将列名中的日期和星期进行换行处理
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			TableColumn col = columnModel.getColumn(i);
			// 表的列名
			String name = col.getHeaderValue().toString();
			Matcher m = datePattern.matcher(name);
			if (m.matches()) {
				col.setHeaderRenderer(renderer);
				col.setCellRenderer(cellRenderer);
				col.setPreferredWidth(70);
			}
		}
		billListPanel.getParentListPanel().lockTableCol(PsnTempletUtils.getBasicVisibleItemCount() - 1);
		// 锁定表双击
		getGridBillListPanel().getParentListPanel().getFixColTable().addMouseListener(new MouseAdapter() {// 双击切换界面
					@Override
					public void mouseClicked(MouseEvent e) {
						if (getModel().getUiState() == UIState.EDIT)// 编辑状态下不切换
							return;
						if (e.getClickCount() != 2)
							return;
						getModel().fireEvent(new AppEvent(CalendarAppEventConst.SWITCH_TO_CALENDAR, getModel(), null));
					}
				});
		billListPanel.getHeadTable().getSelectionModel().addListSelectionListener(this);
		billListPanel.getParentListPanel().addEditListener(this);
		billListPanel.getParentListPanel().addEditListener2(this);
		billListPanel.getHeadTable().addMouseListener(mouseListener);
		billListPanel.getHeadBillModel().addSortRelaObjectListener(getPsnCalendarAppModel());
		billListPanel.getHeadBillModel().addSortListener(getPsnCalendarAppModel());
		// 设置表头的高度，以保持两个界面表头高度的一致
		billListPanel.getHeadTable().getTableHeader().setPreferredSize(new Dimension(100000, getTableHeaderHeight()));
		// 正常表双击
		billListPanel.getHeadTable().addMouseListener(new MouseAdapter() {// 双击切换界面
					@SuppressWarnings({ "static-access", "unused" })
					@Override
					public void mousePressed(MouseEvent e) {
						if (getModel().getUiState() == UIState.EDIT)// 编辑状态下不切换
							return;
						PsnCalendarAppModel obj = getPsnCalendarAppModel();
						UFLiteralDate date = obj.getSelectedDate();
						PsnJobCalendarVO data2 = (PsnJobCalendarVO) obj.getSelectedData();
						String pk_psndoc = data2.getPk_psndoc();
						if (e.getButton() == e.BUTTON3) {
							if (date == null || pk_psndoc == null) {
								MessageDialog.showErrorDlg(null, "錯誤", "請先點選一個日期");
							} else {
								String sql = "select begindate from tbm_psndoc where pk_psndoc = '" + pk_psndoc
										+ "' and pk_psnjob = '" + data2.getPk_psnjob() + "'";
								IUAPQueryBS ser = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class);
								String result = "";
								try {
									Object res = ser.executeQuery(sql, new ColumnProcessor());
									if (res == null) {
										MessageDialog.showErrorDlg(null, "錯誤", "沒有找到考勤檔案");
										return;
									} else {
										String ldate = (String) res;
										UFLiteralDate lidate = new UFLiteralDate(ldate);
										if (lidate.after(date)) {
											MessageDialog.showErrorDlg(null, "錯誤", "不在考勤範圍內");
											return;
										}
									}
								} catch (BusinessException exx) {
									LfwLogger.error(exx.getMessage());
								}
								if (data2.getDayTypeMap().get(date.toString()) == null) {
									MessageDialog.showErrorDlg(null, "錯誤", "當天沒有考勤檔案");
								}
								QueryDialog dialog = new QueryDialog(date.toString(), data2.getPk_psndoc());
								dialog.showModal();
								String OkorNo = dialog.getOkCanel() == null ? "" : dialog.getOkCanel();// 按钮点击判断
								if (OkorNo.equals("") || OkorNo.equals("取消")) { // 如果没有点击或点击取消则退出
									return;
								}
								int type = dialog.getDateType();// 否者，取得参数，进行业务操作

								boolean isCheck = true;
								try {
									isCheck = SysInitQuery.getParaBoolean(obj.getContext().getPk_org(), "TWHRT03")
											.booleanValue();
								} catch (BusinessException ex1) {
									MessageDialog.showErrorDlg(null, "錯誤", ex1.getMessage());
									return;
								}

								// MOD (一例一休校驗)
								// ssx added on 2018-06-10
								try {
									NCLocator
											.getInstance()
											.lookup(ITBMPsndocManageMaintain.class)
											.update(date.toString(), pk_psndoc, type, obj.getContext().getPk_org());
									getManager().refresh();
								} catch (BusinessException ex) {
									if (isCheck) {
										MessageDialog.showErrorDlg(null, "錯誤", ex.getMessage());
									} else {
										try {
											if (UIDialog.ID_OK == MessageDialog.showOkCancelDlg(null, "警告",
													ex.getMessage() + "按<確定>繼續，按<取消>中止。")) {
												NCLocator
														.getInstance()
														.lookup(ITBMPsndocManageMaintain.class)
														.update(date.toString(), pk_psndoc, type,
																obj.getContext().getPk_org());
												getManager().refresh();
											}
										} catch (BusinessException e1) {
											MessageDialog.showErrorDlg(null, "錯誤", ex.getMessage());
										}
									}
								}
								// end
							}

						}
					}

					@Override
					public void mouseClicked(MouseEvent e) {
						if (getModel().getUiState() == UIState.EDIT)// 编辑状态下不切换
							return;
						if (e.getClickCount() != 2)
							return;
						// 如果现在是被锁定，则双击的肯定是日期格，因为前5列人员信息已经被挪到锁定表中去了
						if (getGridBillListPanel().getParentListPanel().isLockCol()) {
							getModel().fireEvent(new AppEvent(CalendarAppEventConst.SHOW_DETAIL, getModel(), null));
							return;
						}
						int selCol = getGridBillListPanel().getHeadTable().getSelectedColumn();
						if (selCol > PsnTempletUtils.getBasicVisibleItemCount() - 1) {// 双击前5列才切换，因为从6列开始都是日期，双击日期应该弹出排班的详细信息，不应该切换界面
							getModel().fireEvent(new AppEvent(CalendarAppEventConst.SHOW_DETAIL, getModel(), null));
							return;
						}
						getModel().fireEvent(new AppEvent(CalendarAppEventConst.SWITCH_TO_CALENDAR, getModel(), null));
					}
				});
	}

	@Override
	public void afterEdit(BillEditEvent e) {
		int row = e.getRow();
		PsnJobCalendarVO vo = getPsnCalendarAppModel().getData()[row];
		String date = e.getKey().substring(0, 10);
		String newPkShift = (String) getGridBillListPanel().getHeadBillModel().getValueAt(row, date);
		String pkShift = vo.getCalendarMap().get(date);
		if (StringUtils.isEmpty(newPkShift))
			newPkShift = null;
		if (StringUtils.isEmpty(pkShift))
			pkShift = null;
		// 如果新旧班次一样，则将ModifiedCalendarMap中的记录去掉，即不持久化这一天的数据(因为持久化和不持久化的结果一样)
		if (StringUtils.equals(newPkShift, pkShift)) {
			vo.getModifiedCalendarMap().remove(date);
			return;
		}
		vo.getModifiedCalendarMap().put(date, newPkShift);
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {
		// TODO Auto-generated method stub

	}

	protected void onEdit() {
		getPsnBillListPanel().setEnabled(true);
	}

	protected void onNotEdit() {
		if (getPsnBillListPanel().getHeadTable().getCellEditor() != null)
			getPsnBillListPanel().getHeadTable().getCellEditor().stopCellEditing();
		getPsnBillListPanel().setEnabled(false);

		// 先将横向滚动条置到最左，再锁定列
		int selectedRow = getPsnBillListPanel().getHeadTable().getSelectedRow();
		getPsnBillListPanel().getHeadTable().changeSelection(selectedRow, 0, false, true);
		getPsnBillListPanel().getParentListPanel().lockTableCol(PsnTempletUtils.getBasicVisibleItemCount() - 1);
		// 锁定表双击
		getGridBillListPanel().getParentListPanel().getFixColTable().addMouseListener(new MouseAdapter() {// 双击切换界面
					@Override
					public void mouseClicked(MouseEvent e) {
						if (getModel().getUiState() == UIState.EDIT)// 编辑状态下不切换
							return;
						if (e.getClickCount() != 2)
							return;
						getModel().fireEvent(new AppEvent(CalendarAppEventConst.SWITCH_TO_CALENDAR, getModel(), null));
					}
				});
	}

	@Override
	protected BillListPanel getPsnBillListPanel() {
		return getGridBillListPanel();
	}

	/**
	 * 考勤档案有效日期范围之外的日期的班次的参照应该不能编辑
	 * 
	 * @param e
	 * @return
	 */
	@Override
	public boolean beforeEdit(BillEditEvent e) {
		int row = e.getRow();
		PsnJobCalendarVO vo = getPsnCalendarAppModel().getData()[row];
		String key = e.getKey();
		if (!key.endsWith(BillPanelUtils.REF_SHOW_NAME)) {
			return false;
		}
		String date = key.substring(0, 10);
		boolean canEdit = vo.isEffectiveDate(date);
		if (canEdit) {
			try {// 维护权限判断
				TBMPsndocVO tbmpsndocvo = NCLocator.getInstance().lookup(ITBMPsndocQueryMaintain.class)
						.queryByPsndocAndDateTime(vo.getPk_psndoc(), new UFDateTime(date + " 00:00:00"));
				boolean can = DataPermissionFacade.isUserHasPermissionByMetaDataOperation(getModel().getContext()
						.getPk_loginUser(), "60170psndoc", IActionCode.EDIT, getModel().getContext().getPk_group(),
						tbmpsndocvo);
				canEdit = can;
				if (!can) {
					ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("6017basedoc", "06017basedoc1856")
					/* @res "您无权对选中的数据执行修改操作!" */, getModel().getContext());
				}

			} catch (BusinessException e1) {
				Logger.error(e1.getMessage(), e1);
			}
		}
		if (canEdit) {
			BillItem item = getGridBillListPanel().getHeadBillModel().getItemByKey(e.getKey());
			UIRefPane refPane = (UIRefPane) item.getComponent();
			if (refPane.getRefModel() == null)
				return canEdit;
			// 一个人在不同的日期，所属的业务单元有可能不一样，不同的业务单元使用不同的班次，V61修改
			refPane.getRefModel().setPk_org(vo.getOrgMap().get(date));
		}

		return canEdit;
	}

	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if (AppEventConst.DATA_UPDATED.equals(event.getType())) {
			RowOperationInfo info = (RowOperationInfo) event.getContextObject();
			if (info == null)
				return;
			int[] updateindices = info.getRowIndexes();
			if (org.apache.commons.lang.ArrayUtils.isEmpty(updateindices))
				return;
			PsnJobCalendarVO[] updateVOs = (PsnJobCalendarVO[]) info.getRowDatas();
			BillListData listData = getPsnBillListPanel().getBillListData();
			UFLiteralDate[] dateScope = CommonUtils.createDateArray(getPsnCalendarAppModel().getBeginDate(),
					getPsnCalendarAppModel().getEndDate());
			BillModel billModel = getPsnBillListPanel().getHeadBillModel();
			for (int i = 0; i < updateindices.length; i++) {
				int index = updateindices[i];
				listData.setHeaderValueRowObjectByMetaData(updateVOs[i], index);
				// 日期格对应的班次需要重新画。由于是更新，原来的界面上可能已经有数据了，因此先把单元格set为空，然后再set班次
				for (UFLiteralDate date : dateScope) {
					if (updateVOs[i].getCalendarMap().containsKey(date.toString())) {
						billModel
								.setValueAt(updateVOs[i].getCalendarMap().get(date.toString()), index, date.toString());
						continue;
					}
					billModel.setValueAt(null, index, date.toString());
				}
			}
			getPsnBillListPanel().getHeadBillModel().updateValue();
			BillPanelUtils.dealWithRefShowNameByPk(getPsnBillListPanel(), IBillItem.HEAD);
			return;
		}
		if (AppEventConst.MODEL_INITIALIZED.equals(event.getType())) {
			syncDataFormModel();
			return;
		}
		if (CalendarAppEventConst.DATE_CHANGED.equals(event.getType())) {
			UFLiteralDate beginDate = getPsnCalendarAppModel().getBeginDate();
			UFLiteralDate endDate = getPsnCalendarAppModel().getEndDate();
			if (beginDate == null || endDate == null)
				return;
			initGridBillListPanel(beginDate, endDate);
			return;
		}
		if (CalendarAppEventConst.EDIT_CANCELED.equals(event.getType())) {// 点击取消事件
			if (!isComponentVisible())// 本界面为当前编辑界面时，才需要处理取消事件
				return;
			if (org.apache.commons.lang.ArrayUtils.isEmpty(getPsnCalendarAppModel().getData()))
				return;
			BillModel billModel = getPsnBillListPanel().getHeadBillModel();
			for (int i = 0; i < billModel.getRowCount(); i++) {
				PsnJobCalendarVO vo = getPsnCalendarAppModel().getData()[i];
				if (vo.getModifiedCalendarMap().isEmpty())
					continue;
				// 将用户修改过的单元格恢复
				String[] modifiedDates = vo.getModifiedCalendarMap().keySet().toArray(new String[0]);
				for (String date : modifiedDates) {
					billModel.setValueAt(vo.getCalendarMap().get(date), i, date);
				}
			}
			getPsnBillListPanel().getHeadBillModel().updateValue();
			BillPanelUtils.dealWithRefShowNameByPk(getPsnBillListPanel(), IBillItem.HEAD);
			return;
		}
		// 如果是排序事件
		if (CalendarAppEventConst.MODEL_SORTED.equals(event.getType())) {
			syncDataFormModel();
			return;
		}
		if (CalendarAppEventConst.SWITCH_TO_GRID.equals(event.getType())) {
			if (getModel().getUiState() == UIState.EDIT)
				return;
			showMeUp();
		}
	}

	private void syncDataFormModel() {
		if (ArrayUtils.isEmpty(getPsnCalendarAppModel().getData()))
			return;
		// 由于单据模板是用元数据画的，而日期列又不是元数据，因此对于日期列的值，只能使用setValueAt来设置值
		int rowCount = getPsnCalendarAppModel().getData().length;
		BillModel billModel = getPsnBillListPanel().getHeadBillModel();
		UFLiteralDate[] allDates = CommonUtils.createDateArray(getPsnCalendarAppModel().getBeginDate(),
				getPsnCalendarAppModel().getEndDate());
		for (int i = 0; i < rowCount; i++) {
			PsnJobCalendarVO vo = getPsnCalendarAppModel().getData()[i];
			Map<String, String> calendarMap = vo.getCalendarMap();
			if (MapUtils.isEmpty(calendarMap))
				continue;
			for (UFLiteralDate date : allDates) {
				billModel.setValueAt(calendarMap.get(date.toString()), i, date.toString());
			}
		}
		getPsnBillListPanel().getHeadBillModel().updateValue();
		BillPanelUtils.dealWithRefShowNameByPk(getPsnBillListPanel(), IBillItem.HEAD);
	}

	protected UISplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new UISplitPane(JSplitPane.VERTICAL_SPLIT);
			splitPane.setName("splitpanel");
			splitPane.setResizeWeight(0.99f);
		}
		return splitPane;
	}

	/**
	 * 设置当前选中的日期
	 */
	@Override
	public void setModelDate(MouseEvent e) {
		int col = getPsnBillListPanel().getHeadTable().getSelectedColumn();
		String key = getPsnBillListPanel().getParentListPanel().getBodyKeyByCol(col);
		UFLiteralDate thisDate = null;
		if (key != null && key.length() > 10) {
			String date = key.substring(0, 10);
			try {
				thisDate = UFLiteralDate.getDate(date);
			} catch (IllegalArgumentException e1) {
				thisDate = null;
			}
		}
		getPsnCalendarAppModel().setSelectedDate(thisDate);
		getModel().fireEvent(new AppEvent(CalendarAppEventConst.MOUSE_SELECTED_CHANGED, this, null));
	}
}
