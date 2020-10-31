package nc.ui.twhr.twhr_declaration.ace.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.twhr.ITwhr_declarationMaintain;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.twhr.twhr_declaration.ace.view.DateGeneraDlg;
import nc.vo.pub.lang.UFDate;

/**
 * 生成按钮
 * 
 * @author: Ares.Tank
 * @date: 2018-9-26 19:36:05
 * @since: eHR V6.5
 */
public class GeneratAction extends HrAction {
	private static final long serialVersionUID = 1L;
	private nc.ui.twhr.glb.view.OrgPanel_Org primaryOrgPanel = null;
	private Object startDateMonth = null;
	private ShowUpableBillForm billForm;
	private BillListView billListView;

	public ShowUpableBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(ShowUpableBillForm billForm) {
		this.billForm = billForm;
	}

	public nc.ui.twhr.glb.view.OrgPanel_Org getPrimaryOrgPanel() {
		return primaryOrgPanel;
	}

	public void setPrimaryOrgPanel(nc.ui.twhr.glb.view.OrgPanel_Org primaryOrgPanel) {
		this.primaryOrgPanel = primaryOrgPanel;
	}

	public GeneratAction() {
		putValue("Code", "generatAction");
		setBtnName("生成");

		putValue("ShortDescription", ResHelper.getString("68J61710", "declartic0001") + "(Ctrl+F)");
		putValue("AcceleratorKey", javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK));
	}

	public void doActionForExtend(ActionEvent e) throws Exception {

	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// PeriodStateVO periodStateVO = (PeriodStateVO) getEditor().getValue();

		// 生成日期确认
		DateGeneraDlg dlg = new DateGeneraDlg(null, "選擇月份", "年份");

		dlg.initUI();
		if (dlg.showModal() == 1) {
			startDateMonth = dlg.getdEffectiveDateMonth();

			if (null == startDateMonth) {
				return;
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					IProgressMonitor progressMonitor = NCProgresses.createDialogProgressMonitor(getEntranceUI());
					try {
						progressMonitor.beginTask("生成中...", -1); // 导入数据...
						progressMonitor.setProcessInfo("請稍後..."); // 数据导入中,请稍后......
						ITwhr_declarationMaintain service = NCLocator.getInstance().lookup(
								ITwhr_declarationMaintain.class);
						service.generatCompanyBVO(new UFDate(startDateMonth + "-01 00:00:00"), getPrimaryOrgPanel()
								.getRefPane().getRefPK(), getContext().getPk_group());
						((nc.ui.pubapp.uif2app.query2.model.ModelDataManager) (getPrimaryOrgPanel()).getDataManager())
								.refresh();
					} catch (Exception e) {
						MessageDialog.showErrorDlg(getEntranceUI(), null, e.getMessage());
					} finally {
						progressMonitor.done();
					}
				}
			}).start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nc.ui.uif2.NCAction#isActionEnable()
	 */
	@Override
	protected boolean isActionEnable() {
		String tableCode = null;
		String pk_org = null;
		try {
			tableCode = getBillListView().getBillListPanel().getBodyTabbedPane().getSelectedTableCode();
			pk_org = getPrimaryOrgPanel().getRefPane().getRefPK();
		} catch (Exception e) {
			pk_org = null;
			tableCode = null;
		}
		if (pk_org != null && tableCode != null && tableCode.equals("id_companybvo")) {
			return true;
		} else {
			return false;
		}
	}

	public BillListView getBillListView() {
		return billListView;
	}

	public void setBillListView(BillListView billListView) {
		this.billListView = billListView;
	}

}