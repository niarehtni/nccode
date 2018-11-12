package nc.ui.ta.overtime.register.action;

import java.awt.event.ActionEvent;

import nc.hr.utils.ResHelper;
import nc.pubitf.para.SysInitQuery;
import nc.uap.rbac.core.dataperm.DataPermissionFacade;
import nc.ui.hr.uif2.action.EditAction;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.uif2.UIState;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.pub.ICommonConst;

import org.apache.commons.lang.StringUtils;

/**
 * 加班登记 修改action
 * 
 * @author yucheng
 * 
 */
@SuppressWarnings("serial")
public class EditOvertimeRegAction extends EditAction {

	// MOD(台灣新法令) ssx added on 2018-05-29
	private HrBillFormEditor formEditor;

	public HrBillFormEditor getFormEditor() {
		return formEditor;
	}

	public void setFormEditor(HrBillFormEditor formEditor) {
		this.formEditor = formEditor;
	}

	// end

	@Override
	public void doAction(ActionEvent evt) throws Exception {
		Object objValue = getModel().getSelectedData();
		if (!hasPermission(objValue))
			throw new Exception(ResHelper.getString("6017basedoc", "06017basedoc1856")
			/* @res "您无权对选中的数据执行修改操作!" */);
		validate(objValue);
		getModel().setUiState(UIState.EDIT);

		// MOD(台灣新法令) ssx added on 2018-05-29
		UFBoolean isEnabled;
		try {
			isEnabled = new UFBoolean(SysInitQuery.getParaString(getContext().getPk_org(), "TBMOTSEG"));
			if (isEnabled != null && isEnabled.booleanValue()) {
				this.getFormEditor().getBillCardPanel().getHeadItem("istorest").setEdit(true);
				this.getFormEditor().getBillCardPanel().getHeadItem("istorest").setEnabled(true);
			} else {
				this.getFormEditor().getBillCardPanel().getHeadItem("istorest").setEdit(false);
				this.getFormEditor().getBillCardPanel().getHeadItem("istorest").setEnabled(false);
			}

		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		// end
	}

	@Override
	protected boolean isActionEnable() {
		if (StringUtils.isBlank(getContext().getPk_org()))
			return false;
		OvertimeRegVO vo = (OvertimeRegVO) getModel().getSelectedData();
		// 没有选中单据或单据来源不是登记单或单据已转调休不可修改
		if (vo == null || ICommonConst.BILL_SOURCE_REG != vo.getBillsource().intValue()
				|| vo.getIstorest().booleanValue())
			return false;
		return super.isActionEnable();
	}

	protected boolean hasPermission(Object obj) {
		return DataPermissionFacade.isUserHasPermissionByMetaDataOperation(this.getContext().getPk_loginUser(),
				getResourceCode(), getMdOperateCode(), this.getContext().getPk_group(), obj);
	}

}
