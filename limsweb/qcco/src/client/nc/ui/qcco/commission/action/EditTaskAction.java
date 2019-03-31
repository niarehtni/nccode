package nc.ui.qcco.commission.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.qcco.ITaskMaintain;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.linkoperate.LinkEditData;
import nc.ui.uap.sf.SFClientUtil;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillListView;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.qcco.commission.AggCommissionHVO;
import nc.vo.qcco.commission.CommissionHVO;
import nc.vo.qcco.task.AggTaskHVO;
import nc.vo.qcco.task.TaskHVO;

public class EditTaskAction extends NCAction {
	private final String FUN_CODE = "C0J00203";
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 7999906859786627909L;
	private BillListView billListPanel;

	public EditTaskAction() {
		this.setCode("EDITTASKACTION");
		this.setBtnName("任务单维护");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		if (this.getBillListPanel().getModel().getSelectedRow() >= 0) {
			if (MessageDialog.showOkCancelDlg(this.getBillListPanel(), "维护任务单", "是否编辑当前委托单对应的任务单？") == MessageDialog.ID_OK) {
				AggCommissionHVO selectData = (AggCommissionHVO) this.getBillListPanel().getModel().getSelectedData();
				if (null == selectData.getParentVO()) {
					MessageDialog.showErrorDlg(getBillListPanel(), "错误!", "未找到任务单主档!");
					return;
				}

				LinkEditData data = new LinkEditData();
				String pk_task_h = lookTaskID(selectData.getPrimaryKey());
				if (null == pk_task_h) {
					if (MessageDialog.ID_YES == MessageDialog.showYesNoDlg(getBillListPanel(), "未找到对应的任务单!",
							"是否创建新的任务单?")) {
						// 生成新的任务单
						pk_task_h = createNewTask(selectData.getParentVO());
					} else {
						return;
					}
				}
				data.setBillID(pk_task_h);
				SFClientUtil.openNodeLinkedMaintain(FUN_CODE, data);
			}
		}
	}

	/**
	 * 创建新的任务单
	 * 
	 * @param parentVO
	 * @return
	 * @throws BusinessException
	 */
	private String createNewTask(CommissionHVO commissionHVO) throws BusinessException {
		if (null == commissionHVO) {
			throw new BusinessException("未找到委托单主表消息!");
		}
		ITaskMaintain ITaskMaintain = (ITaskMaintain) NCLocator.getInstance().lookup(ITaskMaintain.class);
		AggTaskHVO newVO = new AggTaskHVO();
		TaskHVO parentVO = new TaskHVO();

		parentVO.setPk_commission_h(commissionHVO.getPk_commission_h());
		parentVO.setPk_group(commissionHVO.getPk_group());
		parentVO.setPk_org(commissionHVO.getPk_org());
		parentVO.setPk_org_v(commissionHVO.getPk_org_v());
		parentVO.setApprovestatus(-1);

		newVO.setParent(parentVO);
		AggTaskHVO[] newVOs = { newVO };
		AggTaskHVO[] results = ITaskMaintain.insert(newVOs);
		if (null == results || results.length <= 0 || null == results[0] || results[0].getPrimaryKey() == null) {
			throw new BusinessException("创建失败,请检查数据库连接!");
		}
		return results[0].getPrimaryKey();
	}

	private String lookTaskID(String pk_common_h) {
		String result = null;
		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		try {
			result = (String) iUAPQueryBS.executeQuery("select pk_task_h from qc_task_h where pk_commission_h = '"
					+ pk_common_h + "' and dr = 0 ", new ColumnProcessor());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public BillListView getBillListPanel() {
		return billListPanel;
	}

	public void setBillListPanel(BillListView billListPanel) {
		this.billListPanel = billListPanel;
		this.billListPanel.getModel().addAppEventListener(this);
	}

	protected boolean isActionEnable() {
		AbstractBill aggVO = (AbstractBill) this.getBillListPanel().getModel().getSelectedData();
		if (aggVO == null) {
			return false;
		}
		SuperVO hvo = (SuperVO) aggVO.getParentVO();
		if (hvo == null) {
			return false;
		}
		return this.getBillListPanel().getModel().getUiState() == UIState.NOT_EDIT;
	}
}
