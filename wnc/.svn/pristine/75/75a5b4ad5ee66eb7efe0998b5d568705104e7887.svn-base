package nc.ui.wa.formular;

import java.util.ArrayList;
import java.util.List;

import nc.hr.utils.ResHelper;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetHelper;

import org.apache.commons.lang.StringUtils;

public class NhiInfoFunctionEditor extends WaAbstractFunctionEditor {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = -5600542042246314373L;

	private UILabel lblFieldName = null;
	private UIComboBox cboFieldName = null;

	public NhiInfoFunctionEditor() {
		initialize();
	}

	private UIComboBox getCboFieldName() {
		if (cboFieldName == null) {
			try {
				cboFieldName = new UIComboBox();
				cboFieldName.setName("txtFieldName");
				cboFieldName.setBounds(100, 30, 140, 22);
				cboFieldName.setVisible(true);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return cboFieldName;
	}

	private UILabel getLblFieldName() {
		if (lblFieldName == null) {
			try {
				lblFieldName = new nc.ui.pub.beans.UILabel();
				lblFieldName.setName("lblFieldName");
				lblFieldName.setText(NCLangRes.getInstance().getStrByID(
						"twhr_waitem", "NhiInfoFunctionEditor-0001")/* 欄位名稱 */);
				lblFieldName.setBounds(10, 30, 80, 22);
				lblFieldName.setVisible(true);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return lblFieldName;
	}

	private void initialize() {
		try {
			setLayout(null);
			setSize(300, 300);

			add(getLblFieldName(), getLblFieldName().getName());
			add(getCboFieldName(), getCboFieldName().getName());

			add(getOkButton(), getOkButton().getName());
			add(getCancelButton(), getCancelButton().getName());

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}

		initConnection();
	}

	@Override
	public void setModel(AbstractUIAppModel model) {
		super.setModel(model);
		initData();
	}

	public void initData() {
		try {
			InfoItemVO[] infoItems = InfoSetHelper
					.getInfoItemByInfoSetPk("1001ZZ10000000002U2R");
			List<IConstEnum> iconsts = new ArrayList<IConstEnum>();
			for (InfoItemVO item : infoItems) {
				if ((item.getData_type() == 1 || item.getData_type() == 2 || item
						.getData_type() == 18)
						&& !item.getItem_code().equals("recordnum")) {
					iconsts.add(new DefaultConstEnum(item.getItem_code(), item
							.getItem_name2()));
				}
			}
			this.getCboFieldName().removeAllItems();
			this.getCboFieldName().addItems(iconsts.toArray(new IConstEnum[0]));

		} catch (Exception e) {
			handleException(e);
		}
	}

	private static final String funcname = "@"
			+ NCLangRes.getInstance().getStrByID("twhr_waitem",
					"NhiInfoFunctionEditor-0011", null, new String[] {}) + "@"/*
																			 * 勞健保彙總項目
																			 */;

	@Override
	public String getFuncName() {
		return funcname;
	}

	@Override
	public String[] getPara() {
		String[] paras = new String[1];

		// 欄位名稱
		paras[0] = "\"" + (String) getCboFieldName().getSelectdItemValue()
				+ "\"";

		return paras;
	}

	@Override
	public boolean checkPara(int dataType) {

		try {
			// 判断非空
			String nullstr = "";
			if (StringUtils.isEmpty((String) getCboFieldName()
					.getSelectdItemValue())) {
				if (nullstr.length() > 0)
					nullstr += ",";
				nullstr += NCLangRes.getInstance().getStrByID("twhr_waitem",
						"NhiInfoFunctionEditor-0001")/* 欄位名稱 */;
			}
			if (nullstr.length() > 0)
				throw new Exception(nullstr
						+ ResHelper.getString("6013commonbasic",
								"06013commonbasic0021")/* @res "不能为空！" */);
			return true;

		} catch (Exception e) {
			handleException(e);
			showErrMsg(e.getMessage());
			return false;
		}
	}
}
