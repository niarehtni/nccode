package nc.bs.hrsms.hi.common;

import nc.uap.lfw.core.bm.IStateManager;
import nc.uap.lfw.core.bm.dft.AbstractStateManager;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.pub.pf.IPfRetCheckInfo;

import org.apache.commons.lang.StringUtils;

/**
 * 初始状态的管理器
 * 
 * @author gaomz
 * 
 */
public class Init_StateManager extends AbstractStateManager {

	private static final String FS_APPROVE_STATE = "approve_state";
	private static final String FS_PK_PSNJOB = "pk_psnjob";

	@Override
	public IStateManager.State getState(WebComponent target, LfwView widget) {
		if ("list_add".equals(target.getId())) {// 新增按钮
			return IStateManager.State.ENABLED;
		}
		if ("list_refresh".equals(target.getId())) {// 新增按钮
			return IStateManager.State.ENABLED;
		}
		Dataset ds = getCtrlDataset(widget);
		Row row = ds.getSelectedRow();
		if (row == null|| row.getContent()==null) {// 没有选中行
			return IStateManager.State.DISABLED;
		}
		if ("list_copy".equals(target.getId()) || "attachment".equals(target.getId())) {// 复制按钮/附件管理按钮
			return IStateManager.State.ENABLED;
		}
		// 审批状态
		Integer approve_state = getApproveState(ds, row);
		// 人员任职主键
		String pk_psnjob = getPk_psnjob(ds, row);
		if (approve_state == null || StringUtils.isEmpty(pk_psnjob)) {
			return IStateManager.State.DISABLED;
		}
		if ("list_edit".equals(target.getId()) || "list_delete".equals(target.getId()) || "list_submit".equals(target.getId())) {// 修改按钮/提交按钮
			return IPfRetCheckInfo.NOSTATE != approve_state ? IStateManager.State.DISABLED : IStateManager.State.ENABLED;
		} else if ("list_callback".equals(target.getId())) {// 收回按钮
			return getCallbackState(approve_state, ds, row);
		} else if ("list_apporvestate".equals(target.getId())) {// 查看审批流按钮
			return IStateManager.State.ENABLED;
		}
		// 其他按钮
		return IStateManager.State.ENABLED;

	}
	
	/**
	 * 获得收回按钮状态
	 * @param approve_state
	 * @param ds
	 * @param row
	 * @return
	 */
	protected IStateManager.State getCallbackState(Integer approve_state, Dataset ds, Row row){
		return IPfRetCheckInfo.COMMIT != approve_state ? IStateManager.State.DISABLED : IStateManager.State.ENABLED;
	}

	/**
	 * 获得审批状态
	 * 
	 * @param ds
	 * @param row
	 * @return
	 */
	private Integer getApproveState(Dataset ds, Row row) {
		Integer approve_state = 0;
		if (ds.nameToIndex(FS_APPROVE_STATE) > -1) {
			approve_state = (Integer) row.getValue(ds.nameToIndex(FS_APPROVE_STATE));
		}
		return approve_state;
	}

	/**
	 * 获得人员任职主键
	 * 
	 * @param ds
	 * @param row
	 * @return
	 */
	private String getPk_psnjob(Dataset ds, Row row) {
		String pk_psnjob = null;
		if (ds.nameToIndex(FS_PK_PSNJOB) > -1) {
			pk_psnjob = row.getString(ds.nameToIndex(FS_PK_PSNJOB));
		}
		return pk_psnjob;
	}
}
