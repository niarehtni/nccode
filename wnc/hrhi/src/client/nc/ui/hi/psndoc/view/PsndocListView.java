package nc.ui.hi.psndoc.view;

import java.awt.Component;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.md.data.access.DASFacade;
import nc.md.data.access.NCObject;
import nc.md.model.IBusinessEntity;
import nc.ui.bd.pubinfo.address.AddressRefModel;
import nc.ui.hi.psndoc.model.PsndocDataManager;
import nc.ui.hi.psndoc.model.PsndocModel;
import nc.ui.hi.pub.HiAppEventConst;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.hr.uif2.view.HrBillListView;
import nc.ui.hr.uif2.view.HrPsnclTemplateContainer;
import nc.ui.pub.beans.ExtTabbedPane;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillTableCellRenderer;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.RowOperationInfo;
import nc.vo.hi.psndoc.KeyPsnVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.QulifyVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.psnclrule.PsnclinfosetVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillTempletBodyVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;

/***************************************************************************
 * <br>
 * Created on 2010-2-26 8:49:58<br>
 * 
 * @author dusx, Rocex Wang
 ***************************************************************************/
public class PsndocListView extends HrBillListView {
	private PsndocDataManager dataManger;

	private String lastPk_psncl; // 记录上次加载的人员类别信息
	private String[] fldBlastList = new String[] { PsndocVO.ISHISKEYPSN,
			PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_PSNDOC,
			PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_DEPT_V,
			PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_ORG_V, PsnJobVO.PK_DEPT_V, PsnJobVO.PK_ORG_V,
			PsnJobVO.PK_PSNDOC };

	@Override
	public void showMeUp() {
		super.showMeUp();
		getModel().fireEvent(new AppEvent(HiAppEventConst.TAB_CHANGED, getModel(), null));
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-4-20 14:04:33<br>
	 * 
	 * @see nc.ui.uif2.editor.BillListView#bodyRowChange(nc.ui.pub.bill.BillEditEvent)
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public void bodyRowChange(BillEditEvent evt) {

		if (evt.getSource() != getBillListPanel().getHeadTable()) {
			return;
		}

		if (getModel().getUiState() == UIState.EDIT) {
			return;
		}

		super.bodyRowChange(evt);
		getModel().getSubHaveLoaded().clear();
		String pk_psndoc = (String) getBillListPanel().getHeadBillModel().getValueAt(evt.getRow(), PsndocVO.PK_PSNDOC);
		getModel().setCurrentPkPsndoc(pk_psndoc);
		filteByPsncl();

	}

	private void filteByPsncl() {
		if (HICommonValue.FUNC_CODE_POI.equals(getModel().getNodeCode())
				|| HICommonValue.FUNC_CODE_KEYPSN.equals(getModel().getNodeCode())) {
			return;
		}

		String pk_psncl = getModel().getPk_psncl();

		if (ObjectUtils.equals(lastPk_psncl, pk_psncl)) {
			return;
		}
		lastPk_psncl = pk_psncl;

		BillTempletBodyVO[] billTempletBodyVOs = getBillListPanel().getBillListData().getBillTempletVO().getBodyVO();
		if (billTempletBodyVOs == null || billTempletBodyVOs.length <= 0) {
			return;
		}

		HashMap<String, PsnclinfosetVO> getConfigMap = new HrPsnclTemplateContainer().getPsnclConfigMap(getModel()
				.getContext().getPk_org(), pk_psncl);

		if (getConfigMap == null || getConfigMap.isEmpty()) {
			for (BillTempletBodyVO billTempletBodyVO : billTempletBodyVOs) {
				int pos = billTempletBodyVO.getPos();
				BillItem item = null;
				if (BillItem.HEAD == pos) {
					continue;
				} else if (BillItem.BODY == pos) {
					item = getBillListPanel().getBodyItem(billTempletBodyVO.getTable_code(),
							billTempletBodyVO.getItemkey());
				}
				if (item == null) {
					continue;
				}
				if (item.getTableCode().equals(KeyPsnVO.getDefaultTableName())) {
					item.setShow(false);
					item.setNull(false);
				} else if (ArrayUtils.contains(fldBlastList, item.getKey())) {
					item.setShow(false);
					item.setNull(false);
				} else {
					item.setShow(billTempletBodyVO.getShowflag());
					item.setNull(billTempletBodyVO.getNullflag());
				}
			}

			afterFilterByPsncl();

			return;
		}

		for (BillTempletBodyVO billTempletBodyVO : billTempletBodyVOs) {
			int pos = billTempletBodyVO.getPos();
			BillItem item = null;
			if (BillItem.HEAD == pos) {
				continue;
			} else if (BillItem.BODY == pos) {
				item = getBillListPanel()
						.getBodyItem(billTempletBodyVO.getTable_code(), billTempletBodyVO.getItemkey());
			}
			if (item == null) {
				continue;
			}

			if (BillItem.BODY == pos
					&& (PsnOrgVO.getDefaultTableName().equals(billTempletBodyVO.getTable_code()) || KeyPsnVO
							.getDefaultTableName().equals(billTempletBodyVO.getTable_code()))) {
				item.setShow(false);
				item.setNull(false);
				continue;
			}

			if (ArrayUtils.contains(fldBlastList, item.getKey())) {
				item.setShow(false);
				item.setNull(false);
				continue;
			}

			item.setShow(billTempletBodyVO.getShowflag());
			item.setNull(billTempletBodyVO.getNullflag());
			// 根据 单据模板的metadata，从Map中取 PsnclinfosetVO
			PsnclinfosetVO configVO = getConfigMap.get(billTempletBodyVO.getMetadataproperty());
			if (configVO == null) {
				// Map中没有，看是不是自定义项，如果是自定义项，则不显示
				continue;
			}

			item.setShow(configVO.getUsedflag() != null && configVO.getUsedflag().booleanValue()
					&& billTempletBodyVO.getShowflag());
			if (!item.isShow()) {
				item.setNull(false);
			}

			item.setNull(configVO.getMustflag() != null && configVO.getMustflag().booleanValue() && item.isShow());

		}

		afterFilterByPsncl();
	}

	private void afterFilterByPsncl() {
		hideQutifySet();

		refreshBodyPanel();

		// 更改业务子集页签的颜色
		PsndocViewHelper.changeBusiness(getBillListPanel().getBodyTabbedPane(), getModel().getBusinessInfoSet());
		getBillListPanel().getBodyTabbedPane().setTabLayoutPolicy(ExtTabbedPane.SCROLL_TAB_LAYOUT);
		setListMultiProp();
		getBillListPanel().setChildMultiSelect(false);
		getBillListPanel().getHeadTable().setColumnSelectionAllowed(false);
		// 去掉字表的右键菜单
		String[] tabCodes = getBillListPanel().getBillListData().getBodyTableCodes();
		for (int i = 0; tabCodes != null && i < tabCodes.length; i++) {
			UITable table = getBillListPanel().getBodyTable(tabCodes[i]);
			if (table != null) {
				table.removeSortListener();
			}
		}
		setCellRenderer();
	}

	private void refreshBodyPanel() {
		int idx = getBillListPanel().getBodyTabbedPane().getSelectedIndex();
		try {
			Field m_ListData = BillListPanel.class.getDeclaredField("m_ListData");
			m_ListData.setAccessible(true);
			m_ListData.set(getBillListPanel(), getBillListPanel().getBillListData());

			Method m = BillListPanel.class.getDeclaredMethod("initChildListPanel", null);
			m.setAccessible(true);
			m.invoke(getBillListPanel(), null);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}

		int count = getBillListPanel().getBodyTabbedPane().getTabCount();
		if (idx < count) {
			getBillListPanel().getBodyTabbedPane().setSelectedIndex(idx);
		} else if (count > 0) {
			getBillListPanel().getBodyTabbedPane().setSelectedIndex(0);
		}
		getModel().fireEvent(new AppEvent(HiAppEventConst.TAB_CHANGED, getModel(), null));
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-4-20 14:04:33<br>
	 * 
	 * @see nc.ui.hr.uif2.view.HrBillListView#canBeHidden()
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public boolean canBeHidden() {
		if (getModel().isSortEdit()) {
			return false;
		}
		return super.canBeHidden();
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-4-1 11:14:42<br>
	 * 
	 * @author Rocex Wang
	 * @return the dataManger
	 ***************************************************************************/
	public PsndocDataManager getDataManger() {
		return dataManger;
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-5-10 9:17:43<br>
	 * 
	 * @see nc.ui.uif2.editor.BillListView#getModel()
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public PsndocModel getModel() {
		return (PsndocModel) super.getModel();
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-4-20 14:04:33<br>
	 * 
	 * @see nc.ui.hr.uif2.view.HrBillListView#handleEvent(nc.ui.uif2.AppEvent)
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public void handleEvent(AppEvent evt) {
		if (HICommonValue.EVENT_ADJUSTSORT.equals(evt.getType())) {
			boolean blShow = (Boolean) evt.getContextObject();
			getBillListPanel().setEnabled(blShow);
			if (blShow) {
				setBodyVisible(false);
				getBillListPanel().showHeadTableCol("hi_psnjob_showorder");
				// getBillListPanel().getHeadItem("hi_psnjob_showorder").setNull(true);
				BillModel bm = getBillListPanel().getParentListPanel().getTableModel();
				int col = bm.getBodyColByKey("hi_psnjob_showorder");

				// wanglio add 20150513
				// 设置默认顺序号不显示
				for (int i = 0; i < bm.getRowCount(); i++) {
					Object aValue = bm.getValueAt(i, col);
					if (aValue == null || aValue.toString() == null || aValue.toString().trim().equals("")) {
						continue;
					}

					if (((Integer) aValue).intValue() == HICommonValue.DEFAULT_VALUE_SHOWORDER) {
						bm.setValueAt(null, i, col);
					}
				}
			} else {
				getBillListPanel().hideHeadTableCol("hi_psnjob_showorder");
				// getBillListPanel().getHeadItem("hi_psnjob_showorder").setNull(false);
				setBodyVisible(getModel().isSubVisible());
				filteByPsncl();
				if (getModel().isSubVisible()) {
					loadCurrentRowSubData();
				}
				setListMultiProp();
				getBillListPanel().setChildMultiSelect(false);
			}
		} else if (HICommonValue.EVENT_SHOW_SUB.equals(evt.getType())) {
			boolean blShow = (Boolean) evt.getContextObject();
			setBodyVisible(blShow);
			if (blShow) {
				loadCurrentRowSubData();
				getModel().fireEvent(new AppEvent(AppEventConst.SELECTION_CHANGED, evt.getSource(), null));
			}
		}
		super.handleEvent(evt);
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-4-20 14:04:33<br>
	 * 
	 * @see nc.ui.hr.uif2.view.HrBillListView#initUI()
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public void initUI() {
		super.initUI();
		hideQutifySet();
		refreshBodyPanel();

		getBillListPanel().getBodyUIPanel().setVisible(false);
		// 增加子表页签监听。
		getBillListPanel().getBodyTabbedPane().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				if (getModel().isSubVisible()) {
					loadCurrentRowSubData();
				}
				getModel().fireEvent(new AppEvent(HiAppEventConst.TAB_CHANGED, event.getSource(), null));
			}
		});
		// 更改业务子集页签的颜色
		PsndocViewHelper.changeBusiness(getBillListPanel().getBodyTabbedPane(), getModel().getBusinessInfoSet());
		// 显示全选、全消右键菜单
		if (isMultiSelectionEnable() && getMultiSelectionMode() == CHECKBOX_SELECTION) {
			BillPanelUtils.dealWithTablePopopMenu(getBillListPanel().getParentListPanel());
		}
		getBillListPanel().getBodyTabbedPane().setTabLayoutPolicy(ExtTabbedPane.SCROLL_TAB_LAYOUT);
		getBillListPanel().setChildMultiSelect(false);
		getBillListPanel().getHeadTable().setColumnSelectionAllowed(false);

		getBillListPanel().getParentListPanel().setListTableItemAdjustButtonShow(true);

		if (HICommonValue.FUNC_CODE_KEYPSN.equals(getModel().getContext().getNodeCode())) {
			setBodyVisible(true);
			getModel().setSubVisible(true);
		}
		setCellRenderer();
		// 去掉字表的右键菜单
		String[] tabCodes = getBillListPanel().getBillListData().getBodyTableCodes();
		for (int i = 0; tabCodes != null && i < tabCodes.length; i++) {
			UITable table = getBillListPanel().getBodyTable(tabCodes[i]);
			if (table != null) {
				table.removeSortListener();
			}
		}

	}

	private boolean isNULL(Object o) {
		if (o == null || o.toString() == null || o.toString().trim().equals("")) {
			return true;
		}
		return false;
	}

	private void setCellRenderer() {
		BillModel bm = getBillListPanel().getBodyBillModel(QulifyVO.getDefaultTableName());
		if (bm == null) {
			return;
		}
		int colIndex = bm.getBodyColByKey(QulifyVO.AUTHENYEAR);
		UITable bt = getBillListPanel().getBodyTable(QulifyVO.getDefaultTableName());
		if (bt == null) {
			return;
		}
		colIndex = bt.convertColumnIndexToView(colIndex);
		bt.getColumnModel().getColumn(colIndex).setCellRenderer(new AuthenyearCellRenderer());

	}

	/***************************************************************************
	 * 处理子集加载数据<br>
	 * Created on 2010-5-8 17:30:13<br>
	 * 
	 * @author Rocex Wang
	 ***************************************************************************/
	public void loadCurrentRowSubData() {
		int tabIndex = getBillListPanel().getBodyTabbedPane().getSelectedIndex();
		if (tabIndex < 0) {
			// 如果当前选择的页签为-1 返回
			return;
		}
		BillModel billModel = getBillListPanel().getBodyBillModel();
		if (billModel.getTabvo() == null) {
			return;
		}
		String strTabCode = billModel.getTabvo().getTabcode();
		SuperVO subVOs[] = null;
		try {
			subVOs = getDataManger().querySubVO(strTabCode, null);
		} catch (BusinessException ex) {
			throw new BusinessRuntimeException(ex.getMessage(), ex);
		}
		if (subVOs != null && subVOs.length > 0) {
			billModel.clearBodyData();
			billModel.addLine(subVOs.length);
			// for (int i = 0; i < subVOs.length; i++)
			// {
			// billModel.setBodyRowObjectByMetaData(subVOs[i], i);
			// billModel.setRowState(i, BillModel.NORMAL);

			// }
			billModel.setBodyRowObjectByMetaData(subVOs, 0);
			billModel.execLoadFormula();
		}
	}

	/**
	 * 设置body是否显示，同时处理子表数据
	 * 
	 * @param visible
	 */
	public void setBodyVisible(boolean visible) {
		// 1.设置显示
		getBillListPanel().getBodyUIPanel().setVisible(visible);
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-2-25 13:59:59<br>
	 * 
	 * @param datamanger
	 * @author Rocex Wang
	 ***************************************************************************/
	public void setDataManger(PsndocDataManager datamanger) {
		this.dataManger = datamanger;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-5-10 9:18:05<br>
	 * 
	 * @param model
	 * @author Rocex Wang
	 ***************************************************************************/
	public void setModel(PsndocModel model) {
		super.setModel(model);
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-5-5 17:00:54<br>
	 * 
	 * @see nc.ui.uif2.editor.BillListView#setValueSetter()
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	protected void setValueSetter() {
		if (getBillListPanelValueSetter() == null) {
			if (!billListPanel.getBillListData().isMeataDataTemplate()) {
				setBillListPanelValueSetter(new VOBillListPanelValueSetter());
				return;
			}
			setBillListPanelValueSetter(new MDBillListPanelValueSetter() {
				@Override
				public void setHeaderRowsData(BillListPanel listPanel, Object[] datas, int[] indexs) {
					super.setHeaderRowsData(listPanel, datas, indexs);
					IBusinessEntity be = listPanel.getBillListData().getBillTempletVO().getHeadVO()
							.getBillMetaDataBusinessEntity();
					for (int i = 0; datas != null && i < datas.length; i++) {
						setHeaderOtherRowData(listPanel, datas[i], be, indexs[i]);
					}

					setRefName(listPanel);
				}

				@Override
				public void setHeaderDatas(BillListPanel listPanel, Object[] objDatas) {
					super.setHeaderDatas(listPanel, objDatas);
					IBusinessEntity be = listPanel.getBillListData().getBillTempletVO().getHeadVO()
							.getBillMetaDataBusinessEntity();
					int index = listPanel.getHeadBillModel().getItemIndex(PsndocVO.ADDR);
					UITable bt = getBillListPanel().getHeadTable();
					int colIndex = bt.convertColumnIndexToView(index);
					final AddressRefModel addressModel = new AddressRefModel();
					for (int i = 0; getModel().getData() != null && i < objDatas.length; i++) {
						// 影响列表界面工作记录字段显示
						if (colIndex != -1) {
							bt.getColumnModel().getColumn(colIndex).setCellRenderer(new BillTableCellRenderer() {

								private static final long serialVersionUID = 4482832640601668019L;

								@Override
								public Component getTableCellRendererComponent(JTable table, Object value,
										boolean isSelected, boolean hasFocus, int row, int column) {
									Component cmp = super.getTableCellRendererComponent(table, value, isSelected,
											hasFocus, row, column);
									if (value != null) {
										String result = value == null ? "" : value.toString();
										Vector<Vector> vector = addressModel.matchPkData(new String[] { result });
										if (!vector.isEmpty()) {
											Vector<String> vector1 = vector.get(0);
											result = (String) vector1.get(0);
										}
										setText(result);
										setToolTipText(result);
									}
									return cmp;
								}
							});
						}
						setHeaderOtherRowData(listPanel, getModel().getData().get(i), be, i);
					}
					// modify by chenming11 2020-01-02
					// 由于在执行显示公式之前表头工作记录组织关系上的值还没有
					// 导致显示公式无法取到工作记录和组织关系的值，且工作记录和组织关系上的显示公式执行的值也会被覆盖
					// 在这里再执行一遍显示公式
					listPanel.getBillListData().getHeadBillModel().execLoadFormula();
					setRefName(listPanel);
				}

				/***************************************************************************
				 * <br>
				 * Created on 2010-5-8 15:38:13<br>
				 * 
				 * @param listPanel
				 * @param objRowData
				 * @param be
				 * @param iRowIndex
				 * @author Rocex Wang
				 ***************************************************************************/
				private void setHeaderOtherRowData(BillListPanel listPanel, Object objRowData, IBusinessEntity be,
						int iRowIndex) {
					BillItem billItems[] = listPanel.getBillListData().getHeadItems();
					if (billItems == null || objRowData == null) {
						return;
					}
					NCObject ncObject = DASFacade.newInstanceWithContainedObject(be, objRowData);
					if (ncObject == null) {
						return;
					}
					PsndocVO psndocVO = (PsndocVO) ncObject.getModelConsistObject();
					if (psndocVO == null) {
						return;
					}
					// 设置表头数据
					for (BillItem item : billItems) {
						if ((item.getKey().startsWith("hi_psnorg_") || item.getKey().startsWith("hi_psnjob_"))
								&& item.getMetaDataProperty() != null) {
							Object value = psndocVO.getAttributeValue(item.getKey());
							if (item.isIsDef()) {
								value = item.converType(value);
							}
							listPanel.getHeadBillModel().setValueAt(value, iRowIndex, item.getKey());
						}
					}
				}

				/****************************************************************************
				 * {@inheritDoc}<br>
				 * Created on 2010-5-8 15:16:17<br>
				 * 
				 * @see nc.ui.uif2.editor.BillListView.MDBillListPanelValueSetter#setHeaderRowData(nc.ui.pub.bill.BillListPanel,
				 *      java.lang.Object, int)
				 * @author Rocex Wang
				 ****************************************************************************/
				@Override
				public void setHeaderRowData(BillListPanel listPanel, Object objRowData, int iRowIndex) {
					super.setHeaderRowData(listPanel, objRowData, iRowIndex);
					IBusinessEntity be = listPanel.getBillListData().getBillTempletVO().getHeadVO()
							.getBillMetaDataBusinessEntity();
					setHeaderOtherRowData(listPanel, objRowData, be, iRowIndex);
					setRefName(listPanel);
				}

			});
		}
	}

	public void setRefName(BillListPanel listPanel) {
		listPanel.getBillListData().getHeadBillModel().loadLoadRelationItemValue();
		// listPanel.getBillListData().getHeadBillModel().execLoadFormula();
	}

	/**
	 * 结束列表界面的编辑
	 */
	public void stopCellEditing() {
		if (getBillListPanel().getHeadTable().isEditing()) {
			if (getBillListPanel().getHeadTable() != null) {
				getBillListPanel().getHeadTable().getCellEditor().stopCellEditing();
			}
		}
	}

	@Override
	protected void handleRowInserted(AppEvent event) {
		// super.handleRowInserted(event);

		RowOperationInfo roi = (RowOperationInfo) event.getContextObject();
		for (int i = 0; i < roi.getRowDatas().length; i++) {
			getBillListPanel().getBillListData().getHeadBillModel().insertRow(roi.getRowIndexes()[i]);
			getBillListPanelValueSetter().setHeaderRowData(getBillListPanel(), roi.getRowDatas()[i],
					roi.getRowIndexes()[i]);
		}

		if (getModel().isSubVisible()) {
			loadCurrentRowSubData();
		}
	}

	@Override
	protected void syschronizeSelectedRowUpdate() {
		super.syschronizeSelectedRowUpdate();
		if (getModel().isSubVisible()) {
			loadCurrentRowSubData();
		}
	}

	@Override
	protected void handleSelectionChanged() {
		super.handleSelectionChanged();
		if (getModel().isSubVisible()) {
			loadCurrentRowSubData();
		}
	}

	@Override
	protected void handleDataUpdate(AppEvent event) {
		super.handleDataUpdate(event);
		if (getModel().isSubVisible()) {
			loadCurrentRowSubData();
		}
	}

	private void hideQutifySet() {
		boolean isJQStart = false;
		isJQStart = PubEnv.isModuleStarted(PubEnv.getPk_group(), PubEnv.MODULE_HRJQ);
		if (isJQStart) {
			return;
		}
		// 没有启用任职资格 则不显示任职资格页签
		BillModel bm = getBillListPanel().getBodyBillModel(QulifyVO.getDefaultTableName());
		if (bm == null) {
			return;
		}
		BillItem[] items = bm.getBodyItems();
		for (int i = 0; items != null && i < items.length; i++) {
			items[i].setShow(false);
		}

	}
}
