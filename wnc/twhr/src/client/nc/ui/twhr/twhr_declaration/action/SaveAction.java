package nc.ui.twhr.twhr_declaration.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.twhr.ITwhr_declarationMaintain;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pubapp.uif2app.query2.model.IModelDataManager;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.twhr.twhr_declaration.CompanyAdjustBVO;

import org.apache.commons.lang3.StringUtils;

public class SaveAction extends HrAction {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = -3737456463041286502L;
	private ShowUpableBillForm billForm;
	private BillListView billListView;
	private IModelDataManager dataManager;

	public SaveAction() {
		putValue("Code", "saveAction");
		setBtnName("保存");

		putValue("ShortDescription", "保存" + "(Ctrl+S)");
		putValue("AcceleratorKey", javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		CompanyAdjustBVO[] changedVOs = (CompanyAdjustBVO[]) this.getBillForm().getBillCardPanel().getBodyPanel()
				.getTableModel().getBodyValueChangeVOs(CompanyAdjustBVO.class.getName());

		String pk_org = this.getBillForm().getModel().getContext().getPk_org();

		List<CompanyAdjustBVO> insertRows = new ArrayList<CompanyAdjustBVO>();
		List<CompanyAdjustBVO> updateRows = new ArrayList<CompanyAdjustBVO>();
		List<CompanyAdjustBVO> deleteRows = new ArrayList<CompanyAdjustBVO>();
		if (changedVOs != null && changedVOs.length > 0) {
			for (CompanyAdjustBVO vo : changedVOs) {
				if (vo.getStatus() == VOStatus.DELETED) {
					deleteRows.add(vo);
				} else if (vo.getStatus() == VOStatus.NEW) {
					insertRows.add(vo);
				} else if (vo.getStatus() == VOStatus.UPDATED) {
					updateRows.add(vo);
				}

				if (StringUtils.isEmpty(pk_org)
						|| (!StringUtils.isEmpty(vo.getPk_org()) && !pk_org.equals(vo.getPk_org()))) {
					pk_org = vo.getPk_org();
				}
			}
		}

		if (insertRows.size() == 0 && updateRows.size() == 0 && deleteRows.size() == 0) {
			throw new BusinessException("未發現要保存的數據。");
		}

		if (StringUtils.isEmpty(pk_org)) {
			throw new BusinessException("無法取得當前作業組織資料。");
		}

		ITwhr_declarationMaintain saveSrv = NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class);

		if (insertRows.size() > 0) {
			for (CompanyAdjustBVO vo : insertRows) {
				vo.setPk_group(getModel().getContext().getPk_group());
				vo.setPk_org(getModel().getContext().getPk_org());
				vo.setStatus(VOStatus.NEW);
				vo.setTs(new UFDateTime());
			}
			saveSrv.insertCompanyAdjustVOs(pk_org, insertRows.toArray(new CompanyAdjustBVO[0]));
		}

		if (updateRows.size() > 0) {
			saveSrv.updateCompanyAdjustVOs(pk_org, updateRows.toArray(new CompanyAdjustBVO[0]));
		}

		if (deleteRows.size() > 0) {
			saveSrv.deleteCompanyAdjustVOs(deleteRows.toArray(new CompanyAdjustBVO[0]));
		}

		for (int i = 0; i < getBillForm().getBillCardPanel().getBodyTabbedPane().getTabCount(); i++) {
			if (i < 4) {
				getBillForm().getBillCardPanel().getBodyTabbedPane().setEnabledAt(i, true);
				getBillForm().getBillCardPanel().getBodyTabbedPane().setTabVisible(i, true);
			}
		}

		for (int i = 0; i < getBillListView().getBillListPanel().getBodyTabbedPane().getTabCount(); i++) {
			if (i < 4) {
				getBillListView().getBillListPanel().getBodyTabbedPane().setEnabledAt(i, true);
				getBillListView().getBillListPanel().getBodyTabbedPane().setTabVisible(i, true);
			}
		}

		getModel().setUiState(UIState.NOT_EDIT);
		getModel().setOtherUiState(UIState.NOT_EDIT);

		this.getBillForm().getBillOrgPanel().setVisible(true);
		this.getBillForm().getBillCardPanel().getTailScrollPane().setVisible(false);

		getBillForm().getBillCardPanel().getBodyTabbedPane().setSelectedIndex(0);
		getBillListView().getBillListPanel().getBodyTabbedPane().setSelectedIndex(0);

		ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getSaveSuccessInfo(), getModel().getContext());

		getDataManager().refresh();
	}

	public BillListView getBillListView() {
		return billListView;
	}

	public void setBillListView(BillListView billListView) {
		this.billListView = billListView;
	}

	public ShowUpableBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(ShowUpableBillForm billForm) {
		this.billForm = billForm;
	}

	public IModelDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IModelDataManager dataManager) {
		this.dataManager = dataManager;
	}
}
