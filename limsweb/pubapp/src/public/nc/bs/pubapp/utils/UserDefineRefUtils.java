package nc.bs.pubapp.utils;

import java.util.List;
import java.util.Vector;

import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.uif2.editor.BillListView;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.SuperVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

import org.apache.commons.lang.StringUtils;

import uap.iweb.log.Logger;

public class UserDefineRefUtils {
	public static void refreshBillCardHeadDefRefs(AbstractBill aggvo, BillForm billForm, int selectedRow) {
		for (BillItem item : billForm.getBillCardPanel().getHeadItems()) {
			if (!StringUtils.isEmpty(item.getRefType()) && item.getRefType().contains("<")) {
				refreshModelRefValue((SuperVO) aggvo.getParentVO(), billForm, selectedRow, item, true);
				refreshItemRefValue((SuperVO) aggvo.getParentVO(), billForm.getBillCardPanel().getBillTable(),
						selectedRow, item, true);
			}
		}
	}

	private static void refreshModelRefValue(SuperVO parentVO, BillForm billForm, int selectedRow, BillItem item,
			boolean onlyDisplayItem) {
		BillItem pkItem = billForm.getBillCardPanel().getBillModel().getItemByKey("pk_" + item.getKey());
		if (pkItem != null) {
			Object value = parentVO.getAttributeValue(pkItem.getKey());
			billForm.getBillCardPanel().getBillModel().setValueAt(value, selectedRow, item.getKey());
		}
	}

	public static void refreshBillCardBodyDefRefs(AbstractBill aggvo, BillForm billForm, String tabCode,
			Class<? extends SuperVO> bodyVOClass) {
		for (int row = 0; row < billForm.getBillCardPanel().getBillModel(tabCode).getRowCount(); row++) {
			SuperVO bodyVO = (SuperVO) billForm.getBillCardPanel().getBillModel(tabCode)
					.getBodyValueRowVO(row, bodyVOClass.getName());
			SuperVO fullBodyVO = null;
			if (aggvo.getAllChildrenVO() != null) {
				for (ISuperVO realBody : aggvo.getChildren(bodyVOClass)) {
					if (bodyVO.getPrimaryKey().equals(realBody.getPrimaryKey())) {
						fullBodyVO = (SuperVO) realBody;
						break;
					}
				}

				if (fullBodyVO != null) {
					for (BillItem item : billForm.getBillCardPanel().getBillModel(tabCode).getBodyItems()) {
						if (!StringUtils.isEmpty(item.getRefType()) && item.getRefType().contains("<")) {
							String itemKey = item.getKey();
							BillItem pkItem = billForm.getBillCardPanel().getBillModel(tabCode)
									.getItemByKey("pk_" + itemKey);
							if (pkItem != null) {
								fullBodyVO.setAttributeValue(itemKey, fullBodyVO.getAttributeValue("pk_" + itemKey));
							}
							UserDefineRefUtils.refreshItemRefValue(fullBodyVO, billForm.getBillCardPanel()
									.getBillTable(tabCode), row, item, true);
						}
					}
				}
			}
		}
	}

	public static void refreshBillCardGrandDefRefs(BillForm grandBillForm, String tabCode, List<Object> grandVOList) {
		int row = grandBillForm.getBillCardPanel().getBillModel().getRowCount();
		for (int i = 0; i < row; i++) {
			CircularlyAccessibleValueObject vo = grandBillForm.getBillCardPanel().getBillModel()
					.getBodyValueRowVOByVisualValue(i, grandVOList.get(0).getClass().getName());
			try {
				if (vo.getPrimaryKey() != null) {
					SuperVO superVO = (SuperVO) getSuperVOByPK(grandVOList, vo.getPrimaryKey());
					for (BillItem billItem : grandBillForm.getBillCardPanel().getBillModel().getBodyItems()) {

						String itemKey = billItem.getKey();
						BillItem pkItem = grandBillForm.getBillCardPanel().getBillModel(tabCode)
								.getItemByKey("pk_" + itemKey);
						if (pkItem != null) {
							superVO.setAttributeValue(itemKey, superVO.getAttributeValue("pk_" + itemKey));
						}
						UserDefineRefUtils.refreshItemRefValue(superVO,
								grandBillForm.getBillCardPanel().getBillTable(tabCode), i, billItem, true);
					}
				}
			} catch (BusinessException e) {
				Logger.error(e.getMessage());
			}
		}
	}

	public static void refreshBillListHeadDefRefs(AbstractBill aggvo, BillListView billListView, int selectedRow) {
		for (BillItem item : billListView.getBillListPanel().getHeadBillModel().getBodyItems()) {
			if (!StringUtils.isEmpty(item.getRefType()) && item.getRefType().contains("<")) {
				refreshItemRefValue((SuperVO) aggvo.getParentVO(), billListView.getBillListPanel().getHeadTable(),
						selectedRow, item, true);
			}
		}
	}

	public static void refreshBillListBodyDefRefs(AbstractBill aggvo, BillListView billListView, String tabCode,
			Class<? extends SuperVO> bodyVOClass) throws BusinessException {
		for (int row = 0; row < billListView.getBillListPanel().getBodyBillModel(tabCode).getRowCount(); row++) {
			SuperVO bodyVO = (SuperVO) billListView.getBillListPanel().getBodyBillModel(tabCode)
					.getBodyValueRowVO(row, bodyVOClass.getName());
			SuperVO fullBodyVO = null;
			if (aggvo.getAllChildrenVO() != null) {
				for (CircularlyAccessibleValueObject realBody : aggvo.getAllChildrenVO()) {
					if (bodyVO.getPrimaryKey().equals(realBody.getPrimaryKey())) {
						fullBodyVO = (SuperVO) realBody;
						break;
					}
				}
				for (BillItem item : billListView.getBillListPanel().getBodyBillModel(tabCode).getBodyItems()) {
					if (!StringUtils.isEmpty(item.getRefType()) && item.getRefType().contains("<")) {
						UserDefineRefUtils.refreshItemRefValue(fullBodyVO, billListView.getBillListPanel()
								.getBodyTable(tabCode), row, item, true);
					}
				}
			}
		}
	}

	public static void refreshBillListGrandDefRefs(BillListView grandListView, List<Object> grandVOList) {
		int row = grandListView.getBillListPanel().getBodyBillModel().getRowCount();
		for (int i = 0; i < row; i++) {
			CircularlyAccessibleValueObject vo = grandListView.getBillListPanel().getBodyBillModel()
					.getBodyValueRowVOByVisualValue(i, grandVOList.get(0).getClass().getName());
			try {
				SuperVO superVO = (SuperVO) getSuperVOByPK(grandVOList, vo.getPrimaryKey());
				for (BillItem billItem : grandListView.getBillListPanel().getBodyBillModel().getBodyItems()) {
					UserDefineRefUtils.refreshItemRefValue(superVO, grandListView.getBillListPanel().getBodyTable(), i,
							billItem, true);
				}
			} catch (BusinessException e) {
				Logger.error(e.getMessage());
			}
		}
	}

	private static SuperVO getSuperVOByPK(List<Object> grandVOList, String primaryKey) {
		for (Object obj : grandVOList) {
			String pk = (String) ((SuperVO) obj).getAttributeValue(((SuperVO) obj).getPKFieldName());
			if (primaryKey.equals(pk)) {
				return (SuperVO) obj;
			}
		}
		return null;
	}

	public static void refreshItemRefValue(SuperVO vo, UITable uiTable, int row, BillItem rowItem,
			boolean onlyDisplayItem) {
		if (vo != null && rowItem != null && (!onlyDisplayItem || rowItem.isShow())) {
			if (rowItem.getComponent() instanceof UIRefPane) {
				UIRefPane pane = (UIRefPane) rowItem.getComponent();
				AbstractRefModel refModel = pane.getRefModel();
				if (refModel != null && vo.getAttributeValue(rowItem.getKey()) != null) {
					Vector refvls = refModel.matchData(refModel.getPkFieldCode(),
							(String) vo.getAttributeValue(rowItem.getKey()));
					if (null != refvls) {
						IConstEnum val = new DefaultConstEnum(((Vector) refvls.get(0)).get(0),
								(String) ((Vector) refvls.get(0)).get(1));
						for (int col = 0; col < uiTable.getColumnCount(); col++) {
							if (uiTable.getColumnName(col).equals(rowItem.getName())) {
								uiTable.setValueAt(val, row, col);
							}
						}
					}
				}
			}
		}
	}
}
