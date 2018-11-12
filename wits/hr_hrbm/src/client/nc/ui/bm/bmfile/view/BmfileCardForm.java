package nc.ui.bm.bmfile.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.bm.bmfile.IHRBmfileConstant;
import nc.itf.bm.pub.BMDelegator;
import nc.itf.hr.wa.IWaBmfileQueryService;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bm.bmfile.model.BmfileAppModel;
import nc.ui.bm.rule.ref.BmRuleRefmodel;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.BillTabbedPane;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.wabm.file.refmodel.CostDeptVersionRefModel;
import nc.vo.bm.bmclass.BmClassItemVO;
import nc.vo.bm.bmclass.BmClassVO;
import nc.vo.bm.data.BmDataVO;
import nc.vo.bm.item.BmFromEnumVO;
import nc.vo.bm.pub.BmLoginContext;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.hr.pub.WaBmFileOrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFBoolean;

/**
 * @author duyao
 * 
 */
@SuppressWarnings("restriction")
public class BmfileCardForm extends HrBillFormEditor {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private BillTabbedPane tabbedPanel = null;

	private BillCardPanel[] classPanel = null;

	private IWaBmfileQueryService orgQuery;

	public IWaBmfileQueryService getOrgQuery() {
		if (orgQuery == null) {
			orgQuery = NCLocator.getInstance().lookup(IWaBmfileQueryService.class);
		}
		return orgQuery;
	}

	// ����������
	private Map<String, BillTempletVO> templateMap = new HashMap<String, BillTempletVO>();
	private Map<String, List<BillTempletBodyVO>> billTempletBodyVOsMap = new HashMap<String, List<BillTempletBodyVO>>();

	@Override
	public void initUI() {
		super.initUI();
		// 20151009 xiejie3 NCdp205379801 �籣������Ƭ�����ӱ���ʾ���⡣ �ǳ���л uap���ڵĴ���Э���� begin
		// ����ֱ��ȥ��body���������⣬���Բ�����ɾ������getBillTabbedPane��ֱ�ӻ�ȡ����ģ��ı��壬��synchronizeDataFromModel���б���ҳǩ�Ĳ�����begin
		// getBillCardPanel().remove(getBillCardPanel().getBodyUIPanel());
		// getBillCardPanel().add(getBillTabbedPane(), BillCardLayout.BODY);
		// end
	}

	@Override
	public void handleEvent(AppEvent event) {
		int selectindex = 0;
		// 2015-10-31 zhousze ������籣�����������󣬿�Ƭ�����ӱ���ʾ������ begin
		getBillCardPanel().getBodyUIPanel().setVisible(true);
		// end
		if (isShowing()) {
			selectindex = getBillTabbedPane().getSelectedIndex();
		}
		BmLoginContext context = (BmLoginContext) getModel().getContext();
		if (AppEventConst.MODEL_INITIALIZED.equalsIgnoreCase(event.getType())) {
			AbstractRefModel refmodel = ((UIRefPane) billCardPanel.getHeadItem(BmDataVO.PK_LIABILITYORG).getComponent())
					.getRefModel();
			if (refmodel != null) {
				refmodel.setPk_org(context.getPk_org());
			}
			((UIRefPane) billCardPanel.getHeadItem(BmDataVO.PK_LIABILITYORG).getComponent()).setMultiCorpRef(true);
		} else if (AppEventConst.SELECTION_CHANGED == event.getType()) {
			onSelectionChanged();
		} else if (AppEventConst.UISTATE_CHANGED == event.getType()) {
			if (getModel().getUiState() == UIState.EDIT || getModel().getUiState() == UIState.ADD) {
				AbstractRefModel refmodel2 = ((UIRefPane) billCardPanel.getHeadItem(BmDataVO.LIBDEPTVID).getComponent())
						.getRefModel();
				if (refmodel2 != null) {
					String pk_costcenter = (String) getRefPane(BmDataVO.PK_LIABILITYORG).getRefModel().getValue(
							"pk_costcenter");
					// ����ѡ��ĳɱ����Ļ�ȡ�����Ĳ��ţ�����ɱ������ÿ���ô��ʾȫ�����š������ɱ����Ŷ��ÿ�
					if (pk_costcenter != null) {
						getRefPane(BmDataVO.LIBDEPTVID).setWhereString("");
						((CostDeptVersionRefModel) getRefPane(BmDataVO.LIBDEPTVID).getRefModel())
								.setPk_costcenter(pk_costcenter);
					} else {
						getRefPane(BmDataVO.LIBDEPTVID).setWhereString("1=2");
					}
				}
			}
			if (getModel().getUiState() == UIState.ADD) {
				newCardForm();
				onAdd();
			} else if (getModel().getUiState() == UIState.EDIT) {

				onEdit();
			} else {
				onNotEdit();
			}
		} else if (AppEventConst.SELECTED_DATE_CHANGED == event.getType()) {
			onSelectedDataChanged();
		} else if (AppEventConst.SHOW_EDITOR == event.getType() && getModel().getUiState() == UIState.NOT_EDIT) {
			// refreshCardForm();
			synchronizeDataFromModel();
			// getBillCardPanel().getBodyUIPanel().setVisible(true);
			showMeUp();
			setEditable(false);
		}

		if (isShowing()) {
			int count = getBillTabbedPane().getTabCount();
			if (selectindex >= count || selectindex < 0) {
				selectindex = 0;
			}
			if (count > 0) {
				getBillTabbedPane().setSelectedIndex(selectindex);
			}
		}
	}

	@Override
	protected void onSelectionChanged() {
		// refreshCardForm();
		synchronizeDataFromModel();
	}

	@Override
	protected void onAdd() {
		super.onAdd();
		// fengwei 2012-08-17 Ϊ�����ŵ�pk_org��ֵΪ��ǰ�Ĳ�����֯������start
		// String pk_org = (String) getRefPane(BmDataVO.FIPORGVID).getRefModel()
		// .getValue("pk_financeorg");
		// getRefPane(BmDataVO.FIPDEPTVID).setPk_org(pk_org);

		String pk_psnjob = billCardPanel.getHeadItem(BmDataVO.PK_PSNJOB).getValueObject().toString();

		String pk_dept = null;
		String deptvid = null;
		// ��ò���
		Object deptobj = getRefPane(BmDataVO.WORKDEPT).getValueObj();
		if (deptobj != null) {
			pk_dept = ((String[]) deptobj)[0];
		}

		// ��ò��Ű汾id
		deptobj = getRefPane(BmDataVO.WORKDEPTVID).getValueObj();
		if (deptobj != null) {
			deptvid = ((String[]) deptobj)[0];
		}

		WaBmFileOrgVO payOrgVO = new WaBmFileOrgVO();
		String pk_costcenter = null;

		// ��ѯĬ�ϲ�����֯
		try {
			payOrgVO = getOrgQuery().getPkFinanceOrg(pk_psnjob);

			// ������ְ��֯���ҹ����ĳɱ�����
			pk_costcenter = getOrgQuery().getPkCostCenter(pk_dept);

		} catch (BusinessException e1) {
			Logger.error(e1.getMessage());
			MessageDialog.showErrorDlg(this, null, e1.getMessage());
		}

		if (payOrgVO != null) {
			getRefPane(BmDataVO.PK_FINANCEORG).setPK(payOrgVO.getPk_financeorg());
			getRefPane(BmDataVO.PK_FINANCEDEPT).setPk_org(payOrgVO.getPk_financeorg());
			getRefPane(BmDataVO.PK_FINANCEDEPT).setPK(payOrgVO.getPk_financedept());

			getRefPane(BmDataVO.FIPORGVID).setPK(payOrgVO.getFiporgvid());
			getRefPane(BmDataVO.FIPDEPTVID).setPk_org(payOrgVO.getPk_financeorg());
			getRefPane(BmDataVO.FIPDEPTVID).setPK(payOrgVO.getFipdeptvid());

		}
		if (pk_costcenter != null) {
			// ��ְ���Ź����ĳɱ�������ΪĬ�ϵĳɱ����ģ���ְ������ΪĬ�ϳɱ�����
			getRefPane(BmDataVO.PK_LIABILITYORG).setPK(pk_costcenter);

			getRefPane(BmDataVO.LIBDEPTVID).setWhereString(null);

			((CostDeptVersionRefModel) getRefPane(BmDataVO.LIBDEPTVID).getRefModel()).setPk_costcenter(pk_costcenter);

			// getRefPane(PayfileVO.LIBDEPTVID).setWhereString("  pk_dept in ( "+SQLHelper.joinToInSql(pk_depts,
			// -1)+")");
			getRefPane(BmDataVO.LIBDEPTVID).setPK(deptvid);

			getRefPane(BmDataVO.PK_LIABILITYDEPT).setPK(pk_dept);

		}

		// end
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onEdit() {

		synchronizeDataFromModel();

		// fengwei 2012-08-17 Ϊ�����ŵ�pk_org��ֵΪ��ǰ�Ĳ�����֯������start
		String pk_org = (String) getRefPane(BmDataVO.FIPORGVID).getRefModel().getValue("pk_financeorg");
		getRefPane(BmDataVO.FIPDEPTVID).setPk_org(pk_org);
		// end
		// 20151110 xiejie3 NCdp205538210 �ڿ�Ƭ���棬��ʷ�ڼ�ע���ͷ����籣�������޸�
		// ��ѯ����Ƭ��������֣������ֵ�cyear��period�Ϳ�Ƭ��������ֵĽ��бȽϣ����ж��Ƿ�����ʷ�ڼ��������Ϣ��
		List<String> bmclasspks = new ArrayList<String>();
		BmClassVO[] classVOs = null;
		Map<String, BmClassVO> bmMap = new HashMap<String, BmClassVO>();
		for (int i = 0; i < classPanel.length; i++) {
			bmclasspks.add(classPanel[i].getHeadItem(BmDataVO.PK_BM_CLASS).getValueObject().toString());
		}
		try {
			classVOs = ((BmfileAppModel) getModel()).queryBmClassVOsByPK(bmclasspks.toArray(new String[0]));
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		for (BmClassVO bmclass : classVOs) {
			bmMap.put(bmclass.getPk_bm_class(), bmclass);
		}
		// end
		super.onEdit();
		String accountState = null;
		String checkFlag = null;
		BmClassVO classVO = null;
		String cyear = null;
		String cperiod = null;
		for (int i = 0; i < classPanel.length; i++) {
			accountState = classPanel[i].getHeadItem(BmDataVO.ACCOUNTSTATE).getValueObject().toString();
			checkFlag = classPanel[i].getHeadItem(BmDataVO.CHECKFLAG).getValueObject().toString();
			if ("true".equals(checkFlag)) {
				classPanel[i].stopEditing();
				classPanel[i].setEnabled(false);
			}
			if (IHRBmfileConstant.ACCOUNTSTATE_SEAL.toString().equals(accountState)
					|| IHRBmfileConstant.ACCOUNTSTATE_UNREG.toString().equals(accountState)) {
				// 20151110 xiejie3 NCdp205538210 �ڿ�Ƭ���棬��ʷ�ڼ�ע���ͷ����籣�������޸�
				// ��ѯ����Ƭ��������֣������ֵ�cyear��period�Ϳ�Ƭ��������ֵĽ��бȽϣ����ж��Ƿ�����ʷ�ڼ��������Ϣ��
				classVO = bmMap.get(classPanel[i].getHeadItem(BmDataVO.PK_BM_CLASS).getValueObject().toString());
				cyear = classPanel[i].getHeadItem(BmDataVO.CYEAR).getValueObject().toString();
				cperiod = classPanel[i].getHeadItem(BmDataVO.CPERIOD).getValueObject().toString();
				if (classVO != null && classVO.getCyear().equals(cyear) && classVO.getCperiod().equals(cperiod)) {
					classPanel[i].getHeadItem(BmDataVO.ACCOUNTSTATE).setEdit(true);
					UIComboBox cbox = (UIComboBox) classPanel[i].getHeadItem(BmDataVO.ACCOUNTSTATE).getComponent();
					Object normal = cbox.getItemAt(1);// ����
					Object olditem = cbox.getItemAt(Integer.parseInt(accountState) + 1);// ֮ǰѡ�е�
					cbox.removeAllItems();
					cbox.addItem(olditem);
					cbox.addItem(normal);
				} else {
					// 20151110 xiejie3 NCdp205538210 �������ʷ�ڼ�����ݣ������ע�������ݲ����޸ġ�
					classPanel[i].stopEditing();
					classPanel[i].setEnabled(false);
				}
				// end
			}
		}
	}

	@Override
	public void afterEdit(BillEditEvent evt) {
		// ���ö�汾��Ϣ
		if (BmDataVO.PK_LIABILITYORG.equals(evt.getKey())) {

			String pk_costcenter = (String) getRefPane(BmDataVO.PK_LIABILITYORG).getRefModel()
					.getValue("pk_costcenter");
			// ����ѡ��ĳɱ����Ļ�ȡ�����Ĳ��ţ�����ɱ������ÿ���ô��ʾȫ�����š������ɱ����Ŷ��ÿ�
			if (pk_costcenter != null) {
				getRefPane(BmDataVO.LIBDEPTVID).setWhereString("");
				((CostDeptVersionRefModel) getRefPane(BmDataVO.LIBDEPTVID).getRefModel())
						.setPk_costcenter(pk_costcenter);
			} else {
				getRefPane(BmDataVO.LIBDEPTVID).setWhereString("1=2");
			}

			getRefPane(BmDataVO.LIBDEPTVID).setPK(null);
			getRefPane(BmDataVO.PK_LIABILITYDEPT).setPK(null);
			billCardPanel.getHeadItem(BmDataVO.LIBDEPTVID).setEnabled(true);
		} else if (BmDataVO.LIBDEPTVID.equals(evt.getKey())) {
			Object pk_dept = getRefPane(BmDataVO.LIBDEPTVID).getRefModel().getValue("pk_dept");
			if (pk_dept != null) {
				getRefPane(BmDataVO.PK_LIABILITYDEPT).setPK(pk_dept.toString());
			} else {
				getRefPane(BmDataVO.PK_LIABILITYDEPT).setPK(null);
			}
		} else if (BmDataVO.FIPORGVID.equals(evt.getKey())) {
			Object pk_org = getRefPane(BmDataVO.FIPORGVID).getRefModel().getValue("pk_financeorg");
			billCardPanel.getHeadItem(BmDataVO.PK_FINANCEORG).setValue(pk_org);
			if (pk_org != null) {
				getRefPane(BmDataVO.FIPDEPTVID).setPk_org(pk_org.toString());
			} else {
				getRefPane(BmDataVO.FIPDEPTVID).setPk_org(null);
			}
			getRefPane(BmDataVO.FIPDEPTVID).setPK(null);
			// 20151210 xiejie3 NCdp205557580 ������ȡ�Ĳ���
			getRefPane(BmDataVO.PK_FINANCEDEPT).setPK(null);
			// end
			billCardPanel.getHeadItem(BmDataVO.FIPDEPTVID).setEnabled(true);
		} else if (BmDataVO.FIPDEPTVID.equals(evt.getKey())) {
			Object pk_dept = getRefPane(BmDataVO.FIPDEPTVID).getRefModel().getValue("pk_dept");
			if (pk_dept != null) {
				getRefPane(BmDataVO.PK_FINANCEDEPT).setPK(pk_dept.toString());
			} else {
				getRefPane(BmDataVO.PK_FINANCEDEPT).setPK(null);
			}
		}
	}

	@Override
	public void setEditable(boolean editable) {
		if (classPanel != null) {
			for (int i = 0; i < classPanel.length; i++) {
				classPanel[i].stopEditing();
				classPanel[i].setEnabled(editable);
			}
		}
		super.setEditable(editable);
	}

	// /**
	// * ˢ�¿�Ƭ����
	// */
	// private void refreshCardForm() {
	// BmDataVO[] vos = getBmfileAppModel().getSelectedDatas();
	// getBillTabbedPane().removeAll();
	// if (vos == null || vos.length == 0) {
	// return;
	// }
	// classPanel = new BillCardPanel[vos.length];
	// for (int i = 0; i < vos.length; i++) {
	// getBillTabbedPane().addTab(vos[i].getClassname(),
	// getClassPanel(i, vos[i].getPk_bm_class()));
	// UIRefPane panel = (UIRefPane) classPanel[i].getHeadItem(
	// BmDataVO.PAYLOCATION).getComponent();
	// BmRuleRefmodel refModel = (BmRuleRefmodel) panel.getRefModel();
	// refModel.setPk_org(getBmLoginContext().getPk_org());
	// refModel.setPk_bm_class(vos[i].getPk_bm_class());
	// refModel.setEndperiod(vos[i].getCyear() + vos[i].getCperiod());
	// }
	// }

	@Override
	protected void synchronizeDataFromModel() {
		BmDataVO[] vos = getBmfileAppModel().getSelectedDatas();
		// 20151221 xiejei3
		// NCdp205562935����Աһ���ڼ���ڶ������������������δ���ʱ�����뿨Ƭ�����ѡ������ֺ��ܽ���ɾ��
		getBmfileAppModel().setTablesData(vos);
		// end
		getBillTabbedPane().removeAll();
		if (vos == null || vos.length == 0) {
			// 20151105 xiejie3 NCdp205472541 ��������ֻ��һ���ˣ���Ƭ����ɾ��֮�󣬻��ܿ�����Ա��Ϣ������ûˢ��
			setValue(null);
			// end
			return;
		}
		classPanel = new BillCardPanel[vos.length];

		for (int i = 0; i < vos.length; i++) {
			// 20151009 xiejie3 NCdp205379801 �籣������Ƭ�����ӱ���ʾ���⡣ �ǳ���л uap���ڵĴ���Э����
			// begin
			// ������Ҫ�ڿ�Ƭ�����ӱ�������ҳǩ��vos�д������������֣��籣������һ���˿����ж�����֣�getClassPanel�����Ǹ������֣�����ҳǩcardpanel��
			// ��������Ҫ��addScrollPane �����ǽ�cardpanel�⵽BillScrollPane�С�
			// tabVO��ҳǩvo����vo�����������֡�
			// getBillTabbedPane����������ȡ���岿�֣�ͨ��addScrollPane������ҳǩ�ӵ����塣
			BillCardPanel cardpanel = getClassPanel(i, vos[i].getPk_bm_class());
			BillScrollPane scrollpane = new BillScrollPane();
			scrollpane.setViewportView(cardpanel);
			BillTabVO tabVO = new BillTabVO();
			tabVO.setTabname(vos[i].getClassname());
			getBillTabbedPane().addScrollPane(tabVO, scrollpane);
			// end
			UIRefPane panel = (UIRefPane) classPanel[i].getHeadItem(BmDataVO.PAYLOCATION).getComponent();
			BmRuleRefmodel refModel = (BmRuleRefmodel) panel.getRefModel();
			refModel.setPk_org(getBmLoginContext().getPk_org());
			refModel.setPk_bm_class(vos[i].getPk_bm_class());
			refModel.setEndperiod(vos[i].getCyear() + vos[i].getCperiod());
		}
		setValue(getModel().getSelectedData());

		for (int i = 0; i < vos.length && i < classPanel.length; i++) {
			classPanel[i].getBillData().setHeaderValueVO(vos[i]);
			// ����ע��ʱ��ʾ���ע��ԭ��
			if (IHRBmfileConstant.ACCOUNTSTATE_SEAL.equals(vos[i].getAccountstate())
					|| IHRBmfileConstant.ACCOUNTSTATE_UNREG.equals(vos[i].getAccountstate())) {
				classPanel[i].showHeadItem(new String[] { BmDataVO.VCANCELREASON });
			}
		}
	}

	// 20151009 xiejie3 NCdp205379801 �籣������Ƭ�����ӱ���ʾ���⡣ �ǳ���л uap���ڵĴ���Э����
	// ����ķ�����ԭ���ģ���������Ĵ��档
	// protected void synchronizeDataFromModel() {
	// BmDataVO[] vos = getBmfileAppModel().getSelectedDatas();
	// getBillTabbedPane().removeAll();
	// if (vos == null || vos.length == 0) {
	// return;
	// }
	// classPanel = new BillCardPanel[vos.length];
	//
	// for (int i = 0; i < vos.length; i++) {
	// getBillTabbedPane().addTab(vos[i].getClassname(),
	// getClassPanel(i, vos[i].getPk_bm_class()));
	// UIRefPane panel = (UIRefPane) classPanel[i].getHeadItem(
	// BmDataVO.PAYLOCATION).getComponent();
	// BmRuleRefmodel refModel = (BmRuleRefmodel) panel.getRefModel();
	// refModel.setPk_org(getBmLoginContext().getPk_org());
	// refModel.setPk_bm_class(vos[i].getPk_bm_class());
	// refModel.setEndperiod(vos[i].getCyear() + vos[i].getCperiod());
	// }
	// setValue(getModel().getSelectedData());
	//
	// for (int i = 0; i < vos.length && i < classPanel.length; i++) {
	// classPanel[i].getBillData().setHeaderValueVO(vos[i]);
	// // ����ע��ʱ��ʾ���ע��ԭ��
	// if (IHRBmfileConstant.ACCOUNTSTATE_SEAL.equals(vos[i]
	// .getAccountstate())
	// || IHRBmfileConstant.ACCOUNTSTATE_UNREG.equals(vos[i]
	// .getAccountstate())) {
	// classPanel[i]
	// .showHeadItem(new String[] { BmDataVO.VCANCELREASON });
	// }
	// }
	// }
	// end NCdp205379801
	/**
	 * ����ʱˢ�¿�Ƭ����
	 */
	private void newCardForm() {
		setDefaultValue(getBmfileAppModel().getNewBmDataPsnInfo());
		BmDataVO[] vos = getBmfileAppModel().getNewBmDataClassInfo();
		classPanel = new BillCardPanel[vos.length];
		getBillTabbedPane().removeAll();
		for (int i = 0; i < vos.length; i++) {
			// getBillTabbedPane().addTab(vos[i].getClassname(),
			// getClassPanel(i, vos[i].getPk_bm_class()));
			// 20151009 xiejie3 NCdp205379801 �籣������Ƭ�����ӱ���ʾ���⡣ �ǳ���л uap���ڵĴ���Э����
			// begin
			// ������Ҫ�ڿ�Ƭ�����ӱ�������ҳǩ��vos�д������������֣��籣������һ���˿����ж�����֣�getClassPanel�����Ǹ������֣�����ҳǩcardpanel��
			// ��������Ҫ��addScrollPane �����ǽ�cardpanel�⵽BillScrollPane�С�
			// tabVO��ҳǩvo����vo�����������֡�
			// getBillTabbedPane����������ȡ���岿�֣�ͨ��addScrollPane������ҳǩ�ӵ����塣
			BillCardPanel cardpanel = getClassPanel(i, vos[i].getPk_bm_class());
			BillScrollPane scrollpane = new BillScrollPane();
			scrollpane.setViewportView(cardpanel);
			BillTabVO tabVO = new BillTabVO();
			tabVO.setTabname(vos[i].getClassname());
			getBillTabbedPane().addScrollPane(tabVO, scrollpane);
			// end NCdp205379801
		}
		for (int i = 0; i < vos.length && i < classPanel.length; i++) {
			classPanel[i].getBillData().setHeaderValueVO(vos[i]);
			UIRefPane panel = (UIRefPane) classPanel[i].getHeadItem(BmDataVO.PAYLOCATION).getComponent();
			BmRuleRefmodel refModel = (BmRuleRefmodel) panel.getRefModel();
			refModel.setPk_org(getBmLoginContext().getPk_org());
			refModel.setPk_bm_class(vos[i].getPk_bm_class());
			refModel.setEndperiod(vos[i].getCyear() + vos[i].getCperiod());
		}
	}

	private UIRefPane getRefPane(String itemCode) {
		return (UIRefPane) billCardPanel.getHeadItem(itemCode).getComponent();
	}

	public BillTabbedPane getBillTabbedPane() {
		if (tabbedPanel == null) {
			// 20151009 xiejie3 NCdp205379801 �籣������Ƭ�����ӱ���ʾ���⡣ �ǳ���л uap���ڵĴ���Э����
			// begin
			// �˴������½�BillTabbedPane��������⣬���ǻ�ȡ����ģ��ı��塣
			tabbedPanel = (BillTabbedPane) (getBillCardPanel().getBodyUIPanel().getComponent(0));
			// tabbedPanel = new BillTabbedPane();
			// end NCdp205379801
		}
		return tabbedPanel;
	}

	private BillCardPanel getClassPanel(int index, String pk_bm_class) {
		classPanel[index] = new BillCardPanel();
		classPanel[index].setBillType(getBmLoginContext().getNodeCode());
		classPanel[index].setBusiType(null);
		classPanel[index].setOperator(getBmLoginContext().getPk_loginUser());
		classPanel[index].setCorp(getBmLoginContext().getPk_org());
		// ���浥��ģ��
		/**
		 * 1 ���浥��ģ�� 2 ����̶�ģ����ʾ��Ŀ 3 ��̬���ز�ͬ����ҳǩ�ĵ�����Ŀ
		 */
		String nodeCode = getBmLoginContext().getNodeCode() == null ? "nodeCode" : getBmLoginContext().getNodeCode();
		String pk_loginUser = getBmLoginContext().getPk_loginUser() == null ? "pk_loginUser" : getBmLoginContext()
				.getPk_loginUser();
		String pk_org = getBmLoginContext().getPk_org() == null ? "pk_org" : getBmLoginContext().getPk_org();

		// ����ģ��
		BillTempletVO template = null;
		if (templateMap.get(nodeCode + pk_loginUser + pk_org) == null) {
			template = classPanel[index].getDefaultTemplet(classPanel[index].getBillType(), null,
					classPanel[index].getOperator(), classPanel[index].getCorp(), "bmfilesa", null);
			templateMap.put(nodeCode + pk_loginUser + pk_org, template);
		} else {
			template = templateMap.get(nodeCode + pk_loginUser + pk_org);
		}

		if (template == null) {
			Logger.error("û���ҵ�nodekey��bmfilesa��Ӧ�Ŀ�Ƭģ��");
			return classPanel[index];
		}

		// ��ʾ��Ŀ
		List<BillTempletBodyVO> billTempletBodyVOs = new LinkedList<BillTempletBodyVO>();
		if (billTempletBodyVOsMap.get(nodeCode + pk_loginUser + pk_org) == null) {
			for (int i = 0; i < template.getChildrenVO().length; i++) {
				billTempletBodyVOs.add((BillTempletBodyVO) template.getChildrenVO()[i]);
			}
			billTempletBodyVOsMap.put(nodeCode + pk_loginUser + pk_org, billTempletBodyVOs);
		} else {
			billTempletBodyVOs = billTempletBodyVOsMap.get(nodeCode + pk_loginUser + pk_org);
		}
		List<BillTempletBodyVO> billTempletBodyVOsClone = new LinkedList<BillTempletBodyVO>();
		for (int i = 0; i < billTempletBodyVOs.size(); i++) {
			billTempletBodyVOsClone.add((BillTempletBodyVO) billTempletBodyVOs.get(i).clone());
		}
		BmClassItemVO[] bmItemVOs = null;
		try {
			bmItemVOs = BMDelegator.getBmClassQueryService().queryFileClassItem(pk_bm_class,
					getBmLoginContext().getCyear(), getBmLoginContext().getCperiod());
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}
		if (bmItemVOs != null) {
			for (int i = 0; i < bmItemVOs.length; i++) {
				billTempletBodyVOsClone.add(convert2TempletBodyVO(bmItemVOs[i]));
			}
		}

		template.setChildrenVO(billTempletBodyVOsClone.toArray(new BillTempletBodyVO[0]));
		classPanel[index].setBillData(new BillData(template));
		classPanel[index].setEnabled(false);
		return classPanel[index];

	}

	// ����ʱ�õ�����
	public BmDataVO[] getBmDataVOs() {
		BmDataVO headVO = (BmDataVO) getBillCardPanel().getBillData().getHeaderValueVO(BmDataVO.class.getName());
		BmDataVO[] vos = new BmDataVO[classPanel.length];
		for (int i = 0; i < classPanel.length; i++) {
			classPanel[i].stopEditing();
			vos[i] = (BmDataVO) classPanel[i].getBillData().getHeaderValueVO(BmDataVO.class.getName());
			vos[i].setPk_financeorg(headVO.getPk_financeorg());
			vos[i].setPk_financedept(getRefPane(BmDataVO.PK_FINANCEDEPT).getRefPK());
			vos[i].setPk_liabilityorg(headVO.getPk_liabilityorg());
			vos[i].setPk_liabilitydept(getRefPane(BmDataVO.PK_LIABILITYDEPT).getRefPK());
			vos[i].setFiporgvid(headVO.getFiporgvid());
			vos[i].setFipdeptvid(headVO.getFipdeptvid());
			vos[i].setLibdeptvid(headVO.getLibdeptvid());
			// vos[i].setTs(headVO.getTs()); //���ø���ts���ܼ̳�headvo

			// MOD {����20���Զ����ֵ} kevin.nie 2017-09-12 start
			vos[i].setDef1(headVO.getDef1());
			// vos[i].setDef2(headVO.getDef2());
			// vos[i].setDef3(headVO.getDef3());
			vos[i].setDef4(headVO.getDef4());
			vos[i].setDef5(headVO.getDef5());
			vos[i].setDef6(headVO.getDef6());
			vos[i].setDef7(headVO.getDef7());
			vos[i].setDef8(headVO.getDef8());
			vos[i].setDef9(headVO.getDef9());
			vos[i].setDef10(headVO.getDef10());
			vos[i].setDef11(headVO.getDef11());
			vos[i].setDef12(headVO.getDef12());
			vos[i].setDef13(headVO.getDef13());
			vos[i].setDef14(headVO.getDef14());
			vos[i].setDef15(headVO.getDef15());
			vos[i].setDef16(headVO.getDef16());
			vos[i].setDef17(headVO.getDef17());
			vos[i].setDef18(headVO.getDef18());
			vos[i].setDef19(headVO.getDef19());
			vos[i].setDef20(headVO.getDef20());
			// {����20���Զ����ֵ} kevin.nie 2017-09-12 end
		}
		return vos;
	}

	// ����ʱ�õ�����
	public BmDataVO[] getNewBmDataVOs() {
		BmDataVO headVO = (BmDataVO) getBillCardPanel().getBillData().getHeaderValueVO(BmDataVO.class.getName());
		BmDataVO[] vos = new BmDataVO[classPanel.length];
		for (int i = 0; i < classPanel.length; i++) {
			classPanel[i].stopEditing();
			vos[i] = (BmDataVO) classPanel[i].getBillData().getHeaderValueVO(BmDataVO.class.getName());
			vos[i].setPk_psndoc(headVO.getPk_psndoc());
			vos[i].setPk_psnjob(headVO.getPk_psnjob());
			vos[i].setPk_psnorg(headVO.getPk_psnorg());
			vos[i].setAssgid(headVO.getAssgid());
			vos[i].setPk_group(getBmLoginContext().getPk_group());
			vos[i].setPk_org(getBmLoginContext().getPk_org());
			vos[i].setCyear(getBmLoginContext().getCyear());
			vos[i].setCperiod(getBmLoginContext().getCperiod());
			vos[i].setCaculateflag(UFBoolean.FALSE);
			vos[i].setCheckflag(UFBoolean.FALSE);

			vos[i].setWorkorg(headVO.getWorkorg());
			vos[i].setWorkorgvid(headVO.getWorkorgvid());
			vos[i].setWorkdept(headVO.getWorkdept());
			vos[i].setWorkdeptvid(headVO.getWorkdeptvid());

			vos[i].setPk_financeorg(headVO.getPk_financeorg());
			vos[i].setPk_financedept(getRefPane(BmDataVO.PK_FINANCEDEPT).getRefPK());
			vos[i].setPk_liabilityorg(headVO.getPk_liabilityorg());
			vos[i].setPk_liabilitydept(getRefPane(BmDataVO.PK_LIABILITYDEPT).getRefPK());
			vos[i].setFiporgvid(headVO.getFiporgvid());
			vos[i].setFipdeptvid(headVO.getFipdeptvid());
			vos[i].setLibdeptvid(headVO.getLibdeptvid());
			// MOD {����20���Զ����ֵ} kevin.nie 2017-09-12 start
			vos[i].setDef1(headVO.getDef1());
			// vos[i].setDef2(headVO.getDef2());
			// vos[i].setDef3(headVO.getDef3());
			vos[i].setDef4(headVO.getDef4());
			vos[i].setDef5(headVO.getDef5());
			vos[i].setDef6(headVO.getDef6());
			vos[i].setDef7(headVO.getDef7());
			vos[i].setDef8(headVO.getDef8());
			vos[i].setDef9(headVO.getDef9());
			vos[i].setDef10(headVO.getDef10());
			vos[i].setDef11(headVO.getDef11());
			vos[i].setDef12(headVO.getDef12());
			vos[i].setDef13(headVO.getDef13());
			vos[i].setDef14(headVO.getDef14());
			vos[i].setDef15(headVO.getDef15());
			vos[i].setDef16(headVO.getDef16());
			vos[i].setDef17(headVO.getDef17());
			vos[i].setDef18(headVO.getDef18());
			vos[i].setDef19(headVO.getDef19());
			vos[i].setDef20(headVO.getDef20());
			// {����20���Զ����ֵ} kevin.nie 2017-09-12 end
		}
		return vos;
	}

	/**
	 * ��itemת��ΪbodyVO
	 * 
	 * @author zhangg on 2009-11-23
	 * @param baseBodyVO
	 * @param object
	 * @return
	 */
	public BillTempletBodyVO convert2TempletBodyVO(BmClassItemVO item) {

		if (item == null) {
			return null;
		}
		BillTempletBodyVO billTempletBodyVO = getbaseBodyVO("bmaccountno");
		billTempletBodyVO.setDefaultshowname(item.getMultilangName());
		billTempletBodyVO.setItemkey(item.getItemkey());
		// ¼�볤��
		billTempletBodyVO.setInputlength(item.getIfldwidth());
		// ����ITEM���������ͣ� ����BILLVO����
		if (item.getIitemtype().intValue() == TypeEnumVO.FLOATTYPE.toIntValue()) {
			billTempletBodyVO.setDatatype(IBillItem.DECIMAL);

			// ��ֵ������С��λ��
			int digits = item.getIflddecimal();
			billTempletBodyVO.setInputlength(item.getIfldwidth() + 1 + digits);
			String refType = Math.abs(digits) + "";
			billTempletBodyVO.setReftype(refType);
			billTempletBodyVO.setTotalflag(true);
		} else if (item.getIitemtype().intValue() == TypeEnumVO.DATETYPE.toIntValue()) {
			billTempletBodyVO.setDatatype(IBillItem.LITERALDATE);
		} else {
			billTempletBodyVO.setDatatype(IBillItem.STRING);
		}
		// ����������Դ�����Ƿ�����༭
		if (item.getIfromflag().intValue() == BmFromEnumVO.USER_INPUT.toIntValue()) {
			billTempletBodyVO.setEditflag(true);
		} else {
			billTempletBodyVO.setEditflag(Boolean.FALSE);
		}
		// ��ʾ˳��
		billTempletBodyVO.setShoworder(item.getIdisplayseq() + 12);
		// �Ƿ���ʾ
		billTempletBodyVO.setShowflag(item.getShowflag().booleanValue());
		return billTempletBodyVO;
	}

	public static BillTempletBodyVO getbaseBodyVO(String tabCode) {
		BillTempletBodyVO bVO = new BillTempletBodyVO();
		// �仯����
		bVO.setEditflag(Boolean.FALSE);
		bVO.setItemkey("itemKey");
		bVO.setDefaultshowname("defaultShowName");
		bVO.setShoworder(0);
		bVO.setTotalflag(Boolean.FALSE);
		bVO.setDatatype(IBillItem.STRING);
		// ���䲿��
		bVO.setInputlength(Integer.valueOf(-1));
		bVO.setNullflag(Boolean.FALSE); // null allowed
		bVO.setCardflag(Boolean.TRUE);
		bVO.setListflag(Boolean.TRUE);
		bVO.setLockflag(Boolean.FALSE);
		bVO.setShowflag(Boolean.TRUE);
		bVO.setUsereditflag(Boolean.TRUE);
		bVO.setUserflag(Boolean.TRUE);
		bVO.setUsershowflag(Boolean.TRUE);
		bVO.setLeafflag(UFBoolean.FALSE);
		bVO.setNewlineflag(UFBoolean.FALSE);
		bVO.setReviseflag(UFBoolean.FALSE);
		bVO.setPos(IBillItem.HEAD);
		bVO.setWidth(1);
		bVO.setForeground(Integer.valueOf(-1));
		bVO.setTable_code(tabCode);

		return bVO;
	}

	private BmfileAppModel getBmfileAppModel() {
		return (BmfileAppModel) getModel();
	}

	private BmLoginContext getBmLoginContext() {
		return (BmLoginContext) getModel().getContext();
	}
}
