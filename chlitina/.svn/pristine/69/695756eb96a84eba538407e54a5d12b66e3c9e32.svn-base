package nc.ui.wa.formular;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.pub.BusinessException;
import nc.vo.ta.timeitem.LeaveTypeVO;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.item.WaItemVO;

import org.apache.commons.lang.ArrayUtils;

public class ALPaidHoursFunctionEditor extends WaAbstractFunctionEditor{

	private UILabel ivjUILabel = null;
	private UIComboBox ivjcmbItem = null;

	// 指定专案代码薪资维护函数
	private static final String funcname = "@" + ResHelper.getString("wadaysalary","wadaysalary_func_000003") + "@";

	public ALPaidHoursFunctionEditor() {
		initialize();
	}

	public String getFuncName() {
		return funcname;
	}

	private void initialize() {
		try {
			setLayout(null);
			setSize(300, 300);

			add(getUILabel(), getUILabel().getName());
			add(getcmbItem(), getcmbItem().getName());

			add(getOkButton(), getOkButton().getName());
			add(getCancelButton(), getOkButton().getName());
		} catch (Throwable ivjExc) {
			handleException(ivjExc);
		}

		initConnection();
	}

	public boolean checkPara(int dataType) {
		try {
			String nullstr = "";
			if (getcmbItem().getSelectedIndex() < 0) {
				if (nullstr.length() > 0) {
					nullstr = nullstr + ",";
				}
				nullstr = nullstr + ResHelper.getString("common", "UC000-0000234");
			}

			if (nullstr.length() > 0) {
				throw new Exception(nullstr + ResHelper.getString("6013commonbasic", "06013commonbasic0021"));
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
		LeaveTypeVO item = (LeaveTypeVO) getcmbItem().getSelectdItemValue();
		paras[0] = item.getTimeitemcode().toString().trim();
		return paras;
	}

	@SuppressWarnings("unchecked")
	public void initData() {
		try {
			LeaveTypeVO[] items = getItems(getContext());
			LeaveTypeVO itemVO = (LeaveTypeVO) getModel().getSelectedData();
			if (!ArrayUtils.isEmpty(items)) {
				getcmbItem().addItems(items);
				for (LeaveTypeVO vo : items) {
					if (itemVO.getTimeitemcode().equals(vo.getTimeitemcode())) {
						getcmbItem().setSelectedItem(vo);
					}
				}
			} else {
				getcmbItem().addItem(itemVO);
			}

		} catch (Exception e) {
			handleException(e);
		}
	}

	public void setModel(AbstractUIAppModel model) {
		super.setModel(model);
		initData();
	}

	private UIComboBox getcmbItem() {
		if (this.ivjcmbItem == null) {
			try {
				this.ivjcmbItem = new UIComboBox();
				this.ivjcmbItem.setName("cmbItem");
				this.ivjcmbItem.setBounds(100, 70, 140, 22);
				this.ivjcmbItem.setTranslate(true);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return this.ivjcmbItem;
	}

	private UILabel getUILabel() {
		if (this.ivjUILabel == null) {
			try {
				this.ivjUILabel = new UILabel();
				this.ivjUILabel.setName("UILabel2");
				this.ivjUILabel.setText(ResHelper.getString("6013commonbasic", "06013commonbasic0273"));
				this.ivjUILabel.setBounds(10, 70, 80, 22);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return this.ivjUILabel;
	}

	@SuppressWarnings("unchecked")
	private LeaveTypeVO[] getItems(LoginContext context) throws BusinessException {
		LeaveTypeVO[] items = null;
		StringBuilder where = new StringBuilder();
		//where.append(" (pk_group='").append(context.getPk_group()).append("' ");
		//where.append(" or pk_org='").append(context.getPk_org()).append("') ");
		where.append(" PK_TIMEITEM IN (select PK_TIMEITEM from TBM_TIMEITEMCOPY where "
				+ "ISSPECIALREST ='Y' and (pk_org='"+context.getPk_org()+"' or pk_group='"+context.getPk_group()+"'))");
		List<LeaveTypeVO> result = (List<LeaveTypeVO>) NCLocator.getInstance().lookup(IUAPQueryBS.class)
				.retrieveByClause(LeaveTypeVO.class, where.toString());
		if (null != result) {
			items = result.toArray(new LeaveTypeVO[0]);
		}

		return items;
	}
}