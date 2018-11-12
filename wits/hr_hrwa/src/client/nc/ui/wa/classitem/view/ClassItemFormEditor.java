package nc.ui.wa.classitem.view;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IClassItemQueryService;
import nc.itf.hr.wa.IItemQueryService;
import nc.md.model.impl.MDEnum;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefRecentRecordsUtil;
import nc.ui.hr.itemsource.view.AbstractBillItemEditor;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.wa.classitem.model.ClassItemAppModel;
import nc.ui.wa.item.model.HRWADefdocGridRefModel;
import nc.ui.wa.item.model.ItemDefaultValueProvider;
import nc.ui.wa.item.util.ItemUtils;
import nc.ui.wa.item.view.ItemBillFormEditor;
import nc.ui.wa.item.view.custom.CheckedTextField;
import nc.ui.wa.ref.ClassItemTransferRefModel;
import nc.ui.wa.ref.WaItemRefForClassItem;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.wa.classitem.RoundTypeEnum;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.PropertyEnumVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.util.WaConstant;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author: wh
 * @date: 2009-12-8 ����01:01:25
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
@SuppressWarnings("serial")
public class ClassItemFormEditor extends ItemBillFormEditor {

	private WaItemRefForClassItem waItemRefModel;
	private ClassItemDataSourcePanel dataSourcePanel;
	private CheckedTextItemEditor transferEditor;

	private CheckedTextItemEditor getTransferEditor() {
		return transferEditor;
	}

	@Override
	public void initUI() {
		super.initUI();
		UIRefPane refPane = (UIRefPane) billCardPanel.getHeadItem(WaClassItemVO.PK_WA_ITEM).getComponent();
		refPane.setButtonFireEvent(true);
		waItemRefModel = (WaItemRefForClassItem) refPane.getRefModel();
		waItemRefModel.setContext((WaLoginContext) getModel().getContext());
		// ���� ��Ŀ����Ĳ��գ��ӹ���н����Ŀ��Ƭ���濽�������ģ���������Ŀ��Ƭ����
		// ����Ŀ���ࡱ�Ĳ�������Ϊ����н����Ŀ����Ŀ������ա�Ŀ���ǽ��ò��նԻ����С���������ť���Ρ�
		// ����������룺NCdp204895901��2014.01.07
		BillItem categoryID = getClassItemCategory();
		UIRefPane ref = (UIRefPane) categoryID.getComponent();
		HRWADefdocGridRefModel hrWADefdocGridRefModel = new HRWADefdocGridRefModel(ItemDefaultValueProvider
				.getDefdocVO().getPk_defdoclist());
		hrWADefdocGridRefModel.setPara1("1002Z710000000004RC2");
		ref.setRefModel(hrWADefdocGridRefModel);
		// guoqtȡ�����ջ�������������ʾ������Ȩ��������
		refPane.getUITextField().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent evt) {
				RefRecentRecordsUtil.clear(waItemRefModel);
			}
		});
	}

	/**
	 * ȡ�÷�����Ŀ��Ƭ���桰��Ŀ���ࡱ����Ԫ��
	 */
	protected BillItem getClassItemCategory() {
		return getHeadItem(WaItemVO.CATEGORY_ID);
	}

	@Override
	protected void onEdit() {
		super.onEdit();

		updateUIOnEdit(getTypeEnumValue());
		updteWaItemFileState(false);

	}

	@Override
	protected BillItem getMidBillItem() {
		return getHeadItem(WaItemVO.MID);
	}

	/**
	 * ���ý�ת���ú�Ԥ�����������Ƿ���Ա༭
	 * 
	 * @param isEnable
	 */
	private void setEnable(boolean isEnable) {

		// ���������Ͳ��ǡ���ֵ�͡�ʱ��Ԥ�����������á�by��xiejie
		TypeEnumVO typeEnum = getTypeEnumValue();
		if (!typeEnum.equals(TypeEnumVO.FLOATTYPE)) {
			isEnable = false;
		}
		// end by:xiejie

		billCardPanel.getHeadItem("transfer").setEnabled(isEnable);
		billCardPanel.getHeadItem("sumceil").setEnabled(isEnable);
		billCardPanel.getHeadItem("sumfloor").setEnabled(isEnable);
		billCardPanel.getHeadItem("psnceil").setEnabled(isEnable);
		billCardPanel.getHeadItem("psnfloor").setEnabled(isEnable);

	}

	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);

		if (AppEventConst.UISTATE_CHANGED == event.getType()) {

			if (getModel().getUiState() == UIState.ADD) {
			} else if (getModel().getUiState() == UIState.EDIT) {
			} else {
				if (((ClassItemAppModel) getModel()).getData() != null
						&& ((ClassItemAppModel) getModel()).getData().size() > 0
						&& ((ClassItemAppModel) getModel()).getSelectedRow() == -1) {
					if (((ClassItemAppModel) getModel()).getSelectedOperaRows().length == 0) {
						((BillManageModel) getModel()).setSelectedRow(0);
					} else {
						((BillManageModel) getModel()).setSelectedRow(((ClassItemAppModel) getModel())
								.getSelectedOperaRows()[0]);
					}
				}
			}
		}

		if (getModel().getUiState() == UIState.NOT_EDIT) {
			setEnable(false);
		} else if (getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT) {
			setEnable(true);
		}
	}

	private CheckedTextItemEditor createTransferEditor() {
		transferEditor = new CheckedTextItemEditor("transfer", "istransfer", "destitempk", "destitemcol");
		AbstractRefModel refmodel = new ClassItemTransferRefModel(this);
		transferEditor.setRefmodel(refmodel);

		BillItem item = billCardPanel.getHeadItem(transferEditor.getKeyOfBillItem());
		transferEditor.setItem(item);
		// transferEditor.initialize();
		// ��ʼ��Ϊ������
		transferEditor.setContentEnabled(true);
		item.setItemEditor(transferEditor);
		return transferEditor;
	}

	/**
	 * @author xuanlt on 2010-1-21
	 * @see nc.ui.wa.item.view.ItemBillFormEditor#createCustomEditors()
	 */
	@Override
	protected AbstractBillItemEditor[] createCustomEditors() {
		// TODO Auto-generated method stub
		AbstractBillItemEditor[] editors = new AbstractBillItemEditor[] {

		createTransferEditor(), getItemDataSourcePanel(), new CheckedTextField("sumceil", "sumceilflag", "nsumceil"),
				new CheckedTextField("sumfloor", "sumfloorflag", "nsumfloor"),
				new CheckedTextField("psnceil", "psnceilflag", "npsnceil"),
				new CheckedTextField("psnfloor", "psnfloorflag", "npsnfloor"), };
		return editors;
	}

	/**
	 * �޸�ʱ���趨����Ŀ�����
	 * 
	 * @author xuanlt on 2010-1-18
	 * @return void
	 */
	protected void updateUIOnEdit(TypeEnumVO typeName) {

		WaClassItemVO classItem = (WaClassItemVO) getModel().getSelectedData();
		boolean isGroupItem = false;
		try {
			// ����Ƿ��Ǽ�����Ŀ���ǵĻ�ֵ���޸�����
			isGroupItem = NCLocator.getInstance().lookup(IClassItemQueryService.class).checkGroupItem(classItem);
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}
		setRoundTypeEnable();
		String itemkey = classItem.getItemkey();
		if (ItemUtils.isSystemItemKey(itemkey)) {
			// ��������ϵͳ��Ŀ
			for (BillItem item : billCardPanel.getHeadItems()) {
				// NAME�����޸�
				if (item.getKey().equals(WaClassItemVO.NAME) || item.getKey().equals(WaClassItemVO.ROUND_TYPE)
						|| item.getKey().equals(WaClassItemVO.BANKACCOUNT)
						|| item.getKey().equals(WaClassItemVO.INAPPROVEDITEM)) {
					item.setEnabled(true);
				} else if (item.getKey().equals(DATA_SOURCE_PANEL)) {
					// ������Դ �� �ѿ�˰ ���ѿ�˰���� ��������˰ �� ������˰���������޸�
					if (ItemUtils.isSpecialSystemItem(itemkey)) {
						item.setEnabled(false);
					} else {
						item.setEnabled(true);
					}
				} else if (isAlertItem(item.getKey())) {
					// Ԥ���������Ա༭
					item.setEnabled(true);
				} else {
					// ���������Ա༭
					item.setEnabled(false);
				}
			}
		} else if (isGroupItem) {
			// ��������ϵͳ��Ŀ
			for (BillItem item : billCardPanel.getHeadItems()) {
				// NAME�����޸�
				if (item.getKey().equals(WaClassItemVO.NAME)) {
					item.setEnabled(true);
				} else {
					// ���������Ա༭
					item.setEnabled(false);
				}
			}
		} else {
			// ������������ ���� ��λ��ʽ�Ƿ����
			setHeadItemEnable(WaClassItemVO.PK_WA_ITEM, false);
		}

	}

	private void setRoundTypeEnable() {
		Integer itemType = (Integer) getHeadItemValue(WaClassItemVO.IITEMTYPE);
		if (TypeEnumVO.valueOf(TypeEnumVO.class, itemType).equals(TypeEnumVO.FLOATTYPE)) {
			setHeadItemEnable(WaClassItemVO.ROUND_TYPE, true);
		} else {
			setHeadItemEnable(WaClassItemVO.ROUND_TYPE, false);
		}

	}

	private boolean isAlertItem(String item) {
		if (item.equals("sumceil") || item.equals("sumfloor") || item.equals("psnceil") || item.equals("psnfloor")) {
			return true;

		}

		return false;
	}

	@Override
	protected void onAdd() {
		waItemRefModel.clearData();
		super.onAdd();
		setRoundTypeEnable();

	}

	@Override
	public WaClassItemVO getValue() {
		WaClassItemVO vo = (WaClassItemVO) super.getValue();

		if (!isAddPrivateItem()) {
			// �����������ǲ��ԣ���Ҫ��������һ��
			vo.setIitemtype((Integer) getHeadItemValue(WaItemVO.PK_WA_ITEM + "." + WaItemVO.IITEMTYPE));
		}

		getTransferEditor().collectData(vo);

		/**
		 * Ӧ��uap��value ==null �򲻸���ֵ������
		 */
		if (vo.getCategory_id() == null) {
			vo.setCategory_id("");
		}

		return vo;
	}

	private boolean isAddPrivateItem() {
		String pkwaitem = (String) getHeadItemValue(WaClassItemVO.PK_WA_ITEM);
		return StringUtils.isBlank(pkwaitem);
	}

	/**
	 * @author xuanlt on 2010-1-21
	 * @see nc.ui.wa.item.view.ItemBillFormEditor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object object) {
		// TODO Auto-generated method stub
		super.setValue(object);

		// �趨 ��ת
		// getTransferEditor().setDataToEditor((WaClassItemVO)object);

	}

	@Override
	protected BillItem getInHiBillItem() {
		return getHeadItem(WaItemVO.PK_WA_ITEM + "." + WaItemVO.ISINHI);
	}

	@Override
	protected BillItem getTaxFlagBillItem() {
		return getHeadItem(WaClassItemVO.TAXFLAG);
	}

	@Override
	protected BillItem getIpropertyBillItem() {
		return getHeadItem(WaClassItemVO.IPROPERTY);
	}

	@Override
	protected BillItem getFldWidthBillItem() {
		return getHeadItem(WaClassItemVO.IFLDWIDTH);
	}

	@Override
	protected BillItem getFldDecimalBillItem() {
		return getHeadItem(WaClassItemVO.IFLDDECIMAL);
	}

	// "pk_wa_item.iitemtype"
	@Override
	public Integer getItemDataType() {
		Integer itemType = (Integer) getHeadItem(WaItemVO.PK_WA_ITEM + "." + WaClassItemVO.IITEMTYPE).getValueObject();
		if (itemType == null) {
			itemType = 0;
		}

		return itemType;
	}

	@Override
	public void afterEdit(BillEditEvent e) {
		super.afterEdit(e);
		if (WaClassItemVO.PK_WA_ITEM.equals(e.getKey())) {
			String pk_wa_item = (String) getHeadItemValue(WaClassItemVO.PK_WA_ITEM);
			try {
				WaItemVO item = new WaItemVO();
				if (!StringUtils.isBlank(pk_wa_item)) {
					item = NCLocator.getInstance().lookup(IItemQueryService.class).queryWaItemVOByPk(pk_wa_item);
				}

				WaClassItemVO classItemVO = getValue();
				classItemVO.merge(item);
				classItemVO.setCreator(null);
				classItemVO.setCreationtime(null);
				classItemVO.setModifier(null);
				classItemVO.setModifiedtime(null);
				setValue(classItemVO);

				// ��ȡ��Ŀ�ĳ��Ⱥ;��Ȳ�����Ԥ�㾫�� by wangqim
				resetPrecision();

				// ���ù�����Ŀ�ֶεĿɱ༭��
				if (StringUtils.isBlank(pk_wa_item)) {
					updteWaItemFileState(true);

				} else {
					updteWaItemFileState(false);

					// �����������ԡ�Ϊ�������ʱ�����á��м��Ϊ�ɱ༭״̬�� by��xiejie
					PropertyEnumVO propEnum = MDEnum.valueOf(PropertyEnumVO.class,
							(Integer) (getHeadItem(WaItemVO.IPROPERTY).getValueObject()));
					getMidBillItem().setEnabled(propEnum == PropertyEnumVO.OTHER);
					// //end by:xiejie

				}
				// �趨�Զ���༭����״̬
				updateCustomEditorState(getTypeEnumValue());

				updateTaxFlag(getTypeEnumValue());

				updateRoundType(getTypeEnumValue());

			} catch (BusinessException e1) {
				throw new BusinessRuntimeException(ResHelper.getString("60130payitem", "060130payitem0213")/*
																											 * @
																											 * res
																											 * "��ѯн����Ŀʱ�����쳣��"
																											 */, e1);
			}
		}
		if (WaClassItemVO.IITEMTYPE.equals(e.getKey())) {
			updateRoundType(getTypeEnumValue());
		}
	}

	//

	private void updteWaItemFileState(boolean flag) {
		getHeadItem(WaClassItemVO.IITEMTYPE).setEnabled(flag);
		getHeadItem(WaClassItemVO.IFLDWIDTH).setEnabled(flag);
		getHeadItem(WaClassItemVO.IPROPERTY).setEnabled(flag);
		getHeadItem(WaClassItemVO.MID).setEnabled(flag);
		updteWaItemDecimalState();
	}

	private void updteWaItemDecimalState() {
		TypeEnumVO typeEnum = getTypeEnumValue();
		if (getModel().getUiState().equals(UIState.ADD) || getModel().getUiState().equals(UIState.EDIT)) {
			if (TypeEnumVO.FLOATTYPE.equals(typeEnum)) {
				getHeadItem(WaClassItemVO.IFLDDECIMAL).setEnabled(true);
			} else {
				getHeadItem(WaClassItemVO.IFLDDECIMAL).setEnabled(false);
			}
		} else {
			getHeadItem(WaClassItemVO.IFLDDECIMAL).setEnabled(false);
		}

	}

	private void updateTaxFlag(TypeEnumVO typeName) {
		BillItem taxItem = getHeadItem(WaClassItemVO.TAXFLAG);
		if (typeName.equals(TypeEnumVO.FLOATTYPE)) {
			taxItem.setEnabled(true);
		} else {
			taxItem.setEnabled(false);
		}

	}

	private void updateRoundType(TypeEnumVO typeName) {
		BillItem roundTypeItem = getHeadItem(WaClassItemVO.ROUND_TYPE);
		if (typeName.equals(TypeEnumVO.FLOATTYPE)) {
			// ��������
			((UIComboBox) roundTypeItem.getComponent()).setSelectedItem(RoundTypeEnum.ROUND.value());
			roundTypeItem.setEnabled(true);
		} else {
			// ��ѡ
			((UIComboBox) roundTypeItem.getComponent()).setSelectedIndex(0);
			roundTypeItem.setEnabled(false);
		}

	}

	@Override
	protected ClassItemDataSourcePanel createItemDataSourcePanel() {
		if (dataSourcePanel == null) {
			dataSourcePanel = new ClassItemDataSourcePanel(DATA_SOURCE_PANEL, getModel(), this);
		}
		return dataSourcePanel;
	}

	@Override
	protected BillItem getItemCategory() {
		return getHeadItem(WaItemVO.PK_WA_ITEM + "." + WaItemVO.CATEGORY_ID);
	}

	// MOD {X ���ϵ�y�M�з֔�̎��,��������J����Ŀ�������̾��a} kevin.nie 2017-11-09 start
	@Override
	public boolean beforeEdit(BillItemEvent evt) {
		return super.beforeEdit(evt);
	}
	// {X ���ϵ�y�M�з֔�̎��,��������J����Ŀ�������̾��a} kevin.nie 2017-11-09 end
}