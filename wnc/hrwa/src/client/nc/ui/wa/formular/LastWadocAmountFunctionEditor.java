package nc.ui.wa.formular;

import javax.swing.SwingConstants;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.hr.formula.itf.IVariableFactory;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.pub.BusinessException;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.ClassItemContext;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author ssx
 * @date 2019年12月11日22:31:51
 * @desc 最新定薪函数
 * 
 */
@SuppressWarnings({ "serial" })
public class LastWadocAmountFunctionEditor extends WaAbstractFunctionEditor {
	private UILabel ivjUILabel = null;
	private UIRefPane refWaItem = null;

	public LastWadocAmountFunctionEditor() {
		super();
		initialize();
	}

	public String getFuncName() {
		return "@" + ResHelper.getString("6013commonbasic", "06013commonbasic0285") + "@";
	}

	private void initialize() {
		try {
			setLayout(null);
			setSize(300, 130);
			setTitle("請選擇參數");

			add(getWaItemLabel(), getWaItemLabel().getName());
			add(getRefWaItem(), getRefWaItem().getName());

			add(getOkButton(), getOkButton().getName());
			add(getCancelButton(), getOkButton().getName());
		} catch (Throwable ivjExc) {
			handleException(ivjExc);
		}

		initConnection();
	}

	public boolean checkPara(int dataType) {
		try {
			if (StringUtils.isEmpty(getRefWaItem().getRefPK())) {
				throw new BusinessException("請選擇薪資項目");
			}

			return true;
		} catch (Exception e) {
			handleException(e);
			showErrMsg(e.getMessage());
		}
		return false;
	}

	public String[] getPara() {
		String[] paras = new String[1];
		String pk_wa_item = getRefWaItem().getRefPK();
		String itemKey = "";
		if (!StringUtils.isEmpty(pk_wa_item)) {
			IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
			try {
				WaItemVO itemvo = (WaItemVO) query.retrieveByPK(WaItemVO.class, pk_wa_item);
				if (itemvo != null) {
					itemKey = itemvo.getCode();
				}
			} catch (BusinessException e) {
				Logger.error(e.getMessage());
			}
		}
		paras[0] = "\"" + itemKey + "\"";
		return paras;
	}

	public void initData() {
		getRefWaItem().setPk_org(getModel().getContext().getPk_org());
	}

	public void setModel(AbstractUIAppModel model) {
		super.setModel(model);
		initData();
	}

	private UIRefPane getRefWaItem() {
		if (this.refWaItem == null) {
			try {
				this.refWaItem = new UIRefPane("公共薪资项目");
				this.refWaItem.setName("refWaItem");
				this.refWaItem.setButtonFireEvent(true);
				this.refWaItem.setBounds(120, 20, 150, 22);
				this.refWaItem.setWhereString(WaItemVO.ISINHI + "='Y'");
				this.refWaItem.getUITextField().setShowMustInputHint(true);
			} catch (Throwable e) {
				handleException(e);
			}
		}

		if (this.getModel() != null) {
			this.refWaItem.setPk_org(((ClassItemContext) this.getContext()).getPk_org());
		}
		return this.refWaItem;
	}

	private UILabel getWaItemLabel() {
		if (this.ivjUILabel == null) {
			try {
				this.ivjUILabel = new UILabel();
				this.ivjUILabel.setName("UILabel2");
				this.ivjUILabel.setText(ResHelper.getString("6013commonbasic", "06013commonbasic0024"));
				this.ivjUILabel.setBounds(10, 20, 100, 22);
				this.ivjUILabel.setHorizontalAlignment(SwingConstants.RIGHT);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return this.ivjUILabel;
	}

	public UIButton getOkButton() {
		if (okButton == null) {
			okButton = new UIButton(ResHelper.getString("common", "UC001-0000044"));
			okButton.setBounds(48, 74, 80, 22);
		}
		return okButton;
	}

	public UIButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new UIButton(ResHelper.getString("common", "UC001-0000008"));
			cancelButton.setBounds(172, 74, 80, 22);
		}
		return cancelButton;
	}

	protected String getStdDes(String tableDes, String fldDes) {
		if (StringUtils.isBlank(tableDes)) {
			return IVariableFactory.VARIABLE_LEFT_BRACKET + fldDes + IVariableFactory.VARIABLE_RIGHT_BRACKET;
		}
		return IVariableFactory.VARIABLE_LEFT_BRACKET + tableDes + IVariableFactory.ITEM_SEPERATOR + fldDes
				+ IVariableFactory.VARIABLE_RIGHT_BRACKET;
	}

}
