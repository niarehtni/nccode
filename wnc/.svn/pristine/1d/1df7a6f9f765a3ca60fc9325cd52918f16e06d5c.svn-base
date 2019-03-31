package nc.ui.wa.formular;

import java.awt.Dimension;

import javax.swing.SwingConstants;

import nc.hr.utils.ResHelper;
import nc.ui.bd.ref.model.DefdocGridRefModel;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.uif2.model.AbstractUIAppModel;

/**
 * 06013commonbasic0001=免税加班薪资 2019年1月25日11:29:37 Ares.Tank
 */
@SuppressWarnings({ "restriction" })
public class OverTimePaysFunctionEditor extends WaAbstractFunctionEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7424689984458284058L;
	// 薪资项目分组参照 Ares.Tank 2019年1月20日21:17:47
	private UILabel groupLabel = null;
	private UIRefPane groupRef = null;

	@Override
	public void setModel(AbstractUIAppModel model) {
		// TODO Auto-generated method stub
		super.setModel(model);
		initData();
	}

	public void initData() {
		try {
			groupRef.setPk_org(getModel().getContext().getPk_org());
		} catch (Exception e) {
			handleException(e);
		}
	}

	/**
	 * WaParaPanel 构造子注解。
	 */
	public OverTimePaysFunctionEditor() {
		super();
		initialize();
	}

	private static final String funcname = "@"
			+ ResHelper.getString("6013commonbasicadd20180426", "06013commonbasic0001") + "@";

	// "taxRate";
	@Override
	public String getFuncName() {
		// TODO Auto-generated method stub
		return funcname;
	}

	/**
	 * 初始化类。
	 */
	/* 警告：此方法将重新生成。 */
	private void initialize() {
		try {
			setLayout(null);
			setSize(300, 150);
			setTitle("請選擇參數");

			add(getGroupLabel(), getGroupLabel().getName());
			add(getGroupRef(), getGroupRef().getName());

			add(getOkButton(), getOkButton().getName());
			add(getCancelButton(), getCancelButton().getName());

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}

		initConnection();

	}

	public UILabel getGroupLabel() {
		if (this.groupLabel == null) {
			try {
				this.groupLabel = new UILabel();
				this.groupLabel.setName("UILabel3");
				this.groupLabel.setText("薪资项目分组：");
				this.groupLabel.setHorizontalAlignment(SwingConstants.RIGHT);
				this.groupLabel.setBounds(10, 50, 100, 22);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return this.groupLabel;
	}

	public UIRefPane getGroupRef() {
		if (groupRef == null) {
			groupRef = new UIRefPane();
			DefdocGridRefModel model = new DefdocGridRefModel();
			// 设置bd_refinfo中的refclass
			groupRef.setRefModel(model);
			// 设置名称(要和bd_refinfo中的一致)
			groupRef.setRefNodeName("薪资项目分组");
			groupRef.getRefModel().setMutilLangNameRef(false);
			groupRef.setPk_org(null);
			// refGroupIns.set
			groupRef.setVisible(true);
			groupRef.setPreferredSize(new Dimension(50, 20));
			groupRef.setButtonFireEvent(true);
			groupRef.getUITextField().setShowMustInputHint(true);
			groupRef.setName("groupRef");
			groupRef.setBounds(120, 50, 150, 22);

		}
		return groupRef;
	}

	/**
	 * /** * 函数参数合法性校验 * @param 参数说明 * @return 返回值 * @exception 异常描述 * @see
	 * 需要参见的其它内容 * @since 从类的那一个版本，此方法被添加进来。（可选） *
	 * 
	 * @deprecated该方法从类的那一个版本后，已经被其它方法替换。（可选） *-/
	 * 
	 * @return java.lang.String
	 */
	public boolean checkPara(int dataType) {
		try {
			// 判断非空
			if (getGroupRef().getRefPK() == null) {
				throw new Exception("請選擇一個薪資項目分組!");
			}
			return true;

		} catch (Exception e) {
			handleException(e);
			showErrMsg(e.getMessage());
			return false;
		}
	}

	/**
	 * 按期间项目内容取指定类别下的薪资数据（参数：当前类别下的期间，薪资方案，薪资项目）
	 * 
	 * 
	 * @return java.lang.String
	 */
	public String[] getPara() {
		String[] paras = new String[1];

		paras[0] = "\"" + getGroupRef().getRefPK() + "\"";

		return paras;
	}

	public UIButton getOkButton() {
		if (okButton == null) {
			okButton = new UIButton(ResHelper.getString("common", "UC001-0000044"));
			okButton.setBounds(48, 98, 80, 22);
		}
		return okButton;
	}

	public UIButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new UIButton(ResHelper.getString("common", "UC001-0000008"));
			cancelButton.setBounds(172, 98, 80, 22);
		}
		return cancelButton;
	}
}