package nc.ui.wa.psndocwadoc.view;

import java.awt.BorderLayout;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.table.TableColumnModel;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.table.ColumnGroup;
import nc.ui.pub.beans.table.GroupableTableHeader;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel;
import nc.ui.wa.salaryadjmgt.WASalaryadjmgtDelegator;
import nc.vo.hi.wadoc.PsndocWadocMainVO;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.hi.wadoc.PsndocwadocCommonDef;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.item.WaItemVO;

/**
 * ��������Ϣά�� ��ͷ
 * 
 * @author: xuhw
 * @date: 2009-12-26 ����09:27:04
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class PsnWadocMainPane extends UIPanel implements BillEditListener,
		AppEventListener {
	private static final long serialVersionUID = -8711804531661400794L;
	private BillScrollPane ivjBillPane = null;
	private AbstractAppModel model;
	private int currentSelectRow = 0;
	/** ������� */
	private PsnWadocSubPane component;

	/**
	 * PsnWadocMainPanel ������ע�⡣
	 */
	public PsnWadocMainPane() {
		super();
	}

	public void initUI() {
		initialize();
		initTable();
	}

	/**
	 * ��ʼ������
	 */
	private void initTable() {

		getBillPane().addEditListener(this);
		getBillPane().setAutoAddLine(false);
		List<BillItem> listBillItems = new LinkedList<BillItem>();
		BillItem billItem = null;

		billItem = new BillItem();
		billItem.setName(ResHelper.getString("common", "UC000-0000147")/*
																		 * @res
																		 * "��Ա����
																		 */);
		billItem.setKey(PsndocWadocMainVO.PSN_CODE);
		billItem.setDataType(BillItem.STRING);
		billItem.setEdit(false);
		billItem.setWidth(70);
		billItem.setShow(true);
		billItem.setNull(false);
		listBillItems.add(billItem);

		billItem = new BillItem();
		billItem.setName(ResHelper.getString("60130adjapprove",
				"160130adjapprove0009")/* @res "Ա����" */);
		billItem.setKey(PsndocWadocMainVO.CLERKCODE);
		billItem.setDataType(BillItem.STRING);
		billItem.setEdit(false);
		billItem.setWidth(70);
		billItem.setShow(true);
		billItem.setNull(false);
		listBillItems.add(billItem);

		billItem = new BillItem();
		billItem.setName(ResHelper.getString("common", "UC000-0001403")/*
																		 * @res
																		 * "����"
																		 */);
		billItem.setKey(PsndocWadocMainVO.PSN_NAME);
		billItem.setDataType(BillItem.STRING);
		billItem.setEdit(false);
		billItem.setWidth(70);
		billItem.setShow(true);
		billItem.setNull(false);
		listBillItems.add(billItem);

		// 2015-09-16 zhousze ������ְ��֯��ְ����λ�ã��ŵ�����ǰ��
		// 20150806 xiejie3�����ϲ���NCdp205398642��������Ϣά����ͷ�����ӡ���ְ��֯�������ҿ��Զ����������,begin
		// by wangqim
		billItem = new BillItem();
		billItem.setName(ResHelper.getString("60130adjapprove",
				"160130adjapprove0014")/* @res "��ְ��֯" */);
		billItem.setKey(PsndocWadocMainVO.ORGNAME);
		billItem.setDataType(BillItem.STRING);
		billItem.setEdit(false);
		billItem.setWidth(70);
		billItem.setShow(true);
		billItem.setNull(false);
		listBillItems.add(billItem);
		// end

		billItem = new BillItem();
		billItem.setName(ResHelper.getString("common", "UC000-0004064")/*
																		 * @res
																		 * "����"
																		 */);
		billItem.setKey(PsndocWadocMainVO.DEPTNAME);
		billItem.setDataType(BillItem.STRING);
		billItem.setEdit(false);
		billItem.setWidth(70);
		billItem.setShow(true);
		billItem.setNull(false);
		listBillItems.add(billItem);

		billItem = new BillItem();
		billItem.setName(ResHelper.getString("common", "UC000-0000140")/*
																		 * @res
																		 * "��Ա���"
																		 */);
		billItem.setKey(PsndocWadocMainVO.PSNCLNAME);
		billItem.setDataType(BillItem.STRING);
		billItem.setEdit(false);
		billItem.setWidth(70);
		billItem.setShow(true);
		billItem.setNull(false);
		listBillItems.add(billItem);

		billItem = new BillItem();
		billItem.setName(ResHelper.getString("common", "UC000-0001653")/*
																		 * @res
																		 * "��λ"
																		 */);
		billItem.setKey(PsndocWadocMainVO.POSTNAME);
		billItem.setDataType(BillItem.STRING);
		billItem.setEdit(false);
		billItem.setWidth(70);
		billItem.setShow(true);
		billItem.setNull(false);
		listBillItems.add(billItem);

		billItem = new BillItem();
		billItem.setName(ResHelper.getString("common", "UC000-0001658")/*
																		 * @res
																		 * "��λ����"
																		 */);
		billItem.setKey(PsndocWadocMainVO.POSTSERISE);
		billItem.setDataType(BillItem.STRING);
		billItem.setEdit(false);
		billItem.setWidth(70);
		billItem.setShow(true);
		billItem.setNull(false);
		listBillItems.add(billItem);

		billItem = new BillItem();
		billItem.setName(ResHelper.getString("common", "UC000-0003300")/*
																		 * @res
																		 * "ְ��"
																		 */);
		billItem.setKey(PsndocWadocMainVO.JOBNAME);
		billItem.setDataType(BillItem.STRING);
		billItem.setEdit(false);
		billItem.setWidth(70);
		billItem.setShow(true);
		billItem.setNull(false);
		listBillItems.add(billItem);

		WaItemVO[] vos = null;
		String pkOrg = this.getModel().getContext().getPk_org();
		PsndocwadocAppModel model = (PsndocwadocAppModel) getModel();
		if (pkOrg == null) {
			vos = null;
		} else if (model.getCachedWaItemVOs().containsKey(pkOrg)) {
			vos = model.getCachedWaItemVOs().get(pkOrg);
		} else {
			try {
				vos = WASalaryadjmgtDelegator.getItemQueryService()
						.queryWaItemVOForWadoc(this.getModel().getContext());
			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
			model.getCachedWaItemVOs().put(pkOrg, vos);
			// getComponent().setCachedWaItemVOs(vos);
		}
		if (vos != null) {
			int iflddecimal = IBillItem.DEFAULT_DECIMAL_DIGITS;
			for (WaItemVO vo : vos) {

				iflddecimal = vo.getIflddecimal();
				billItem = new BillItem();
				billItem.setName(ResHelper.getString("60130adjapprove",
						"160130adjapprove0006")/* @res "н�ʱ�׼" */);
				billItem.setKey(vo.getPk_wa_item() + "."
						+ PsndocWadocMainVO.PK_WA_CRT_SHOWNAME);
				billItem.setDataType(BillItem.STRING);
				billItem.setEdit(false);
				billItem.setWidth(70);
				billItem.setShow(true);
				billItem.setNull(false);
				listBillItems.add(billItem);

				billItem = new BillItem();
				billItem.setName(ResHelper.getString("60130adjapprove",
						"160130adjapprove0001")/* @res "��׼���" */);
				billItem.setKey(vo.getPk_wa_item() + "."
						+ PsndocWadocMainVO.CRITERIONVALUE);
				billItem.setDataType(BillItem.DECIMAL);
				billItem.setEdit(false);
				billItem.setWidth(70);
				billItem.setDecimalDigits(iflddecimal);
				billItem.setLength(31);
				billItem.setShow(true);
				billItem.setNull(false);
				listBillItems.add(billItem);

				billItem = new BillItem();
				billItem.setName(ResHelper.getString("common", "UC000-0004112")/*
																				 * @
																				 * res
																				 * "���"
																				 */);
				billItem.setKey(vo.getPk_wa_item() + "."
						+ PsndocWadocMainVO.NMONEY);
				billItem.setDataType(BillItem.DECIMAL);
				billItem.setEdit(false);
				billItem.setWidth(70);
				billItem.setDecimalDigits(iflddecimal);
				billItem.setLength(31);
				billItem.setShow(true);
				billItem.setNull(false);
				listBillItems.add(billItem);
			}

		}

		billItem = new BillItem();
		billItem.setName(PsndocWadocMainVO.PK_PSNDOC);
		billItem.setKey(PsndocWadocMainVO.PK_PSNDOC);
		billItem.setDataType(BillItem.STRING);
		billItem.setEdit(false);
		billItem.setWidth(40);
		billItem.setShow(false);
		listBillItems.add(billItem);

		billItem = new BillItem();
		billItem.setName(PsndocWadocMainVO.PK_PSNJOB);
		billItem.setKey(PsndocWadocMainVO.PK_PSNJOB);
		billItem.setDataType(BillItem.STRING);
		billItem.setEdit(false);
		billItem.setWidth(40);
		billItem.setShow(false);
		listBillItems.add(billItem);

		billItem = new BillItem();
		billItem.setName(PsndocWadocMainVO.DEPT_CODE);
		billItem.setKey(PsndocWadocMainVO.DEPT_CODE);
		billItem.setDataType(BillItem.STRING);
		billItem.setEdit(false);
		billItem.setWidth(40);
		billItem.setShow(false);

		billItem = new BillItem();
		billItem.setName(PsndocWadocMainVO.PK_PSNDOC_SUB);
		billItem.setKey(PsndocWadocMainVO.PK_PSNDOC_SUB);
		billItem.setDataType(BillItem.STRING);
		billItem.setEdit(false);
		billItem.setWidth(40);
		billItem.setShow(false);
		listBillItems.add(billItem);

		// billItem = new BillItem();
		// billItem.setName(ResHelper.getString("common","UC000-0001192")/*@res
		// "Ա������"*/);
		// billItem.setKey(PsndocWadocMainVO.PSN_CODE);
		// billItem.setDataType(BillItem.STRING);
		// billItem.setEdit(false);
		// billItem.setWidth(70);
		// billItem.setShow(false);
		// billItem.setNull(false);
		// listBillItems.add(billItem);

		billItem = new BillItem();
		billItem.setName(PsndocWadocMainVO.PARTFLAG);
		billItem.setKey(PsndocWadocMainVO.PARTFLAG);
		billItem.setDataType(BillItem.BOOLEAN);
		billItem.setEdit(false);
		billItem.setWidth(40);
		billItem.setShow(false);
		listBillItems.add(billItem);

		billItem = new BillItem();
		billItem.setName(PsndocWadocMainVO.ASSGID);
		billItem.setKey(PsndocWadocMainVO.ASSGID);
		billItem.setDataType(BillItem.INTEGER);
		billItem.setEdit(false);
		billItem.setWidth(40);
		billItem.setShow(false);
		listBillItems.add(billItem);

		BillModel billModel = new BillModel();
		billModel.setBodyItems(listBillItems.toArray(new BillItem[listBillItems
				.size()]));

		// String[] billitems = new
		// String[]{PsndocWadocMainVO.CRITERIONVALUE,PsndocWadocMainVO.NMONEY};
		// IBillModelDecimalListener2 bmd = new
		// WaItemDecimalAdapter(WaItemVO.PK_WA_ITEM, billitems);
		// billModel.addDecimalListener(bmd);

		getBillPane().setTableModel(billModel);
		getBillPane().getTable().setSelectionMode(
				javax.swing.ListSelectionModel.SINGLE_SELECTION);
		getBillPane().getTable().setColumnSelectionAllowed(false);
		getBillPane().setRowNOShow(true);
		// �趨���ͷ
		GroupableTableHeader header = (GroupableTableHeader) getBillPane()
				.getTable().getTableHeader();
		if (vos != null) {
			for (WaItemVO element : vos) {
				header.addColumnGroup(getColumnGroup(element.getPk_wa_item(),
						element.getMultilangName()));
			}
		}
		// TableColResize.reSizeTable(getBillPane()); //����ˢ�±�����
	}

	/**
	 * ���� BillPane ����ֵ��
	 */
	private BillScrollPane getBillPane() {
		if (ivjBillPane == null) {
			ivjBillPane = new BillScrollPane();
			ivjBillPane.setName("BillPane");
		}
		return ivjBillPane;
	}

	public BillScrollPane getBillScrollPane() {
		return getBillPane();
	}

	/**
	 * ��ʼ���ࡣ
	 */
	private void initialize() {
		setName("PsnWadocMainPane");
		setLayout(new BorderLayout());
		add(getBillPane(), "Center");
	}

	public void setWadocData(PsndocWadocMainVO[] mainVOs) {
		// 2016-12-2 zhousze н�ʼ��ܣ����ﴦ����������Ϣά���ڵ��ѯˢ�½��� begin
		if (mainVOs != null) {
			for (PsndocWadocMainVO vo : mainVOs) {
				Hashtable<String, PsndocWadocVO> obj = vo.getValues();
				if (obj.size() != 0) {
					Set<String> pk = obj.keySet();
					Object[] pkArray = (Object[]) pk.toArray();
					for (int i = 0; i < pkArray.length; i++) {
						obj.get(pkArray[i])
								.setCriterionvalue(
										new UFDouble(
												SalaryDecryptUtil
														.decrypt((obj
																.get(pkArray[i])
																.getCriterionvalue() == null ? new UFDouble(
																0)
																: obj.get(
																		pkArray[i])
																		.getCriterionvalue())
																.toDouble())));
						obj.get(pkArray[i])
								.setNmoney(
										new UFDouble(
												SalaryDecryptUtil
														.decrypt((obj.get(
																pkArray[i])
																.getNmoney() == null ? new UFDouble(
																0) : obj.get(
																pkArray[i])
																.getNmoney())
																.toDouble())));
					}
				}
			}
		}
		// end

		getBillPane().getTableModel().setBodyDataVO(mainVOs);
		// TableColResize.reSizeTable(getBillPane());
	}

	/**
	 * ȡ�ñ�ͷѡ���vo
	 */
	public PsndocWadocMainVO getBodySelectedVO(int row) {
		return (PsndocWadocMainVO) getBillPane().getTableModel()
				.getBodyValueRowVO(row, PsndocWadocMainVO.class.getName());
	}

	@Override
	public void afterEdit(BillEditEvent e) {
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {
		int rowCount = getBillPane().getTable().getRowCount();
		if (rowCount <= 0) {
			// ������С�ڵ���һ�������ƶ�
			return;
		} else if (rowCount > 0) {
			// ������������
			currentSelectRow = e.getRow();
			PsndocwadocAppModel appModel = (PsndocwadocAppModel) this
					.getModel();
			appModel.setSelectedRow(currentSelectRow);
			if (appModel.getUiState() != UIState.EDIT) {
				((PsndocwadocAppModel) getModel())
						.setState(PsndocwadocCommonDef.UNKNOWN_STATE);
				PsndocWadocMainVO vo = getBodySelectedVO(currentSelectRow);
				if (vo != null) {

					getComponent().setSubVOs(vo);
					// ((PsndocwadocAppModel)
					// getModel()).setUiState(UIState.NOT_EDIT);
					if (UIState.NOT_EDIT != getModel().getUiState()) {
						getModel().setUiState(UIState.NOT_EDIT);
					}
					this.getBillPane().getTableModel();
				}
			}
		}
	}

	@Override
	public void handleEvent(AppEvent event) {
		PsndocwadocAppModel appModel = (PsndocwadocAppModel) this.getModel();
		UIState state = appModel.getUiState();
		String strState = appModel.getState();
		if (AppEventConst.MODEL_INITIALIZED.equalsIgnoreCase(event.getType())) {
			if (!PsndocwadocCommonDef.REFRESH_STATE.equals(strState)) {
				processAfterQuery(strState);
			} else {
				processAfterFresh();
			}
			if (UIState.NOT_EDIT != state) {
				getModel().setUiState(UIState.NOT_EDIT);
			}
		} else if (UIState.NOT_EDIT == state) {
			if (PsndocwadocCommonDef.REFRESH_STATE.equals(strState)) {
				processAfterFresh();
			}
			getBillPane().setEnabled(true);
			getBillPane().getTableModel().setEnabled(false);
			getBillPane().getTable().setEnabled(true);
			getBillPane().getTable().setSortEnabled(true);
		} else if (UIState.EDIT == state) {
			getBillPane().setEnabled(false);
			getBillPane().getTableModel().setEnabled(false);
			getBillPane().getTable().setEnabled(false);
			getBillPane().getTable().setSortEnabled(false);
		} else {
			getBillPane().setEnabled(true);
			getBillPane().getTableModel().setEnabled(true);
			getBillPane().getTable().setEnabled(true);
		}
	}

	/**
	 * ��ѯ��Ĵ���
	 * 
	 * @author xuhw on 2010-1-5
	 */
	private void processAfterQuery(String strState) {
		setWadocData(null);
		PsndocWadocMainVO[] mainVOs = null;
		mainVOs = ((PsndocwadocAppModel) this.getModel()).getVos();
		if (PsndocwadocCommonDef.QUERY_STATE != strState) {
			initTable();
		}
		setWadocData(mainVOs);
		if (mainVOs != null && mainVOs.length > 0) {
			getBillPane().getTable().addRowSelectionInterval(0, 0);

			getBillPane().getTable().addColumnSelectionInterval(0, 0);
		} else {
			getComponent().setWadocData(null);
		}
	}

	/**
	 * ˢ�º���
	 * 
	 * @author xuhw on 2010-1-5
	 */
	private void processAfterFresh() {
		PsndocwadocAppModel appmodel = (PsndocwadocAppModel) this.getModel();
		// getComponent().getBillScrollPane().getTable().clearSelection();
		setWadocData(null);
		getComponent().setWadocData(null);
		// getBillPane().removeEditListener();
		// initTable();
		PsndocWadocMainVO[] mainVOs = null;
		try {
			mainVOs = appmodel.getWadocMain(appmodel.getPartflag(),
					appmodel.getQuerywhere());
		} catch (BusinessException ex) {
			Logger.error(ex.getMessage(), ex);
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(),
					null,
					ResHelper.getString("60130adjmtc", "060130adjmtc0216")/*
																		 * @res
																		 * "ˢ������ʧ�ܣ�������ˢ�£�"
																		 */);
			return;
		}
		setWadocData(mainVOs);
		((PsndocwadocAppModel) getModel())
				.setState(PsndocwadocCommonDef.UNKNOWN_STATE);

		if (mainVOs != null && mainVOs.length > 0) {
			int selectRow = mainVOs.length >= currentSelectRow ? currentSelectRow
					: 0;
			selectRow = selectRow == -1 ? 0 : selectRow;
			getBillPane().getTable().addRowSelectionInterval(selectRow,
					selectRow);
			getBillPane().getTable().addColumnSelectionInterval(0, 0);
		} else {
			this.getComponent().setWadocData(null);
		}
	}

	/**
	 * ���ͷ
	 */
	private ColumnGroup getColumnGroup(String proKey, String headerName) {
		ColumnGroup columnGroup = new ColumnGroup(headerName);
		TableColumnModel cm = getBillPane().getTable().getColumnModel();
		for (int i = 0; i < cm.getColumnCount(); i++) {
			if (getBillPane().getBodyKeyByCol(i).startsWith(proKey)) {
				columnGroup.add(cm.getColumn(i));
			}
		}
		return columnGroup;
	}

	public PsnWadocSubPane getComponent() {
		return component;
	}

	public void setComponent(PsnWadocSubPane component) {
		this.component = component;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public void onRefresh() {
		processAfterFresh();
	}
	/*
	 * // �����Ƿ��Զ����� public boolean isAutoAddLine() { return false; }
	 */
}