/**
 * @(#)AllocateFormEditor.java 1.0 2017年9月14日
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.hrwa.allocate.ace.view;

import org.apache.commons.lang.StringUtils;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.wa.ref.WaClassItemRefModel;
import nc.vo.wa.pub.WaLoginContext;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings({ "restriction", "serial" })
public class AllocateFormEditor extends ShowUpableBillForm implements BillCardBeforeEditListener {

	@Override
	public void initUI() {
		super.initUI();
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
	}

	/**
	 * @Description: 根据组织面板设置薪资发放项目
	 */
	protected void initWaClassRef(String whereSql) {
		BillItem item = getBillCardPanel().getHeadItem("pk_classitem");
		if (null != item.getComponent() && item.getComponent() instanceof UIRefPane) {
			UIRefPane refPane = (UIRefPane) item.getComponent();
			WaClassItemRefModel refModel = (WaClassItemRefModel) refPane.getRefModel();
			if (null != refModel) {
				WaLoginContext context = (WaLoginContext) getModel().getContext();
				StringBuilder otherCondition = new StringBuilder();
				// otherCondition.append("wa_classitem.def1 in ( select bd_defdoc.pk_defdoc from bd_defdoclist ");
				// otherCondition.append("inner join bd_defdoc on bd_defdoclist.pk_defdoclist=bd_defdoc.pk_defdoclist ");
				// otherCondition.append("where bd_defdoclist.code='WITSCS' and bd_defdoc.code='B' ) ");
				if (StringUtils.isNotEmpty(whereSql)) {
					if (!whereSql.trim().toLowerCase().startsWith("and")) {
						otherCondition.append(" and ");
					}
					otherCondition.append(whereSql);
				}
				refModel.setPk_org(context.getPk_org());
				refModel.setPk_wa_class(context.getClassPK());
				refModel.setPeriod(context.getCyear() + context.getCperiod());
				refModel.setOtherConditon(otherCondition.toString());
			}
		}
	}

	@Override
	public boolean beforeEdit(BillItemEvent evt) {
		BillItem item = evt.getItem();
		if (item.getKey().equals("pk_classitem")) {
			initWaClassRef(null);
		}
		return true;
	}

	@Override
	protected void onAdd() {
		super.onAdd();
		initWaClassRef(null);
	}

	@Override
	protected void onEdit() {
		super.onEdit();
		initWaClassRef(null);
	}
}
