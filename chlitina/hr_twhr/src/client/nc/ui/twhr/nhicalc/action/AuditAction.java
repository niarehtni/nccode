package nc.ui.twhr.nhicalc.action;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.itf.twhr.INhicalcMaintain;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel;
import nc.ui.twhr.nhicalc.model.NhicalcAppModel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.lang.StringUtils;

public class AuditAction extends NCAction {
    /**
     * serial version id
     */
    private static final long serialVersionUID = 3690234657179448172L;

    private NhicalcAppModel model = null;
    private BillListView editor = null;

    private NhiOrgHeadPanel orgpanel = null;

    public AuditAction() {
	setBtnName("審核");
    }

    @Override
    public void doAction(ActionEvent arg0) throws Exception {
	if (MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(),
		NCLangRes.getInstance().getStrByID("68861705", "AuditAction-0000")/* 提示 */, NCLangRes.getInstance()
			.getStrByID("68861705", "AuditAction-0001")/*
								    * 审核操作后将无法进行选定期间的劳健保数据生成
								    * 、
								    * 修改和计算，是否继续？
								    */) == UIDialog.ID_YES) {

	    new SwingWorker() {

		BannerTimerDialog dialog = new BannerTimerDialog(SwingUtilities.getWindowAncestor(getModel()
			.getContext().getEntranceUI()));
		String error = null;

		protected Boolean doInBackground() throws Exception {
		    try {
			dialog.setStartText("正在同步勞健保計算結果");
			dialog.start();

			INhicalcMaintain nhiSrv = NCLocator.getInstance().lookup(INhicalcMaintain.class);
			nhiSrv.audit(getOrgpanel().getRefPane().getRefPK(), getOrgpanel().getPeriodRefModel()
				.getRefNameValue().split("-")[0], getOrgpanel().getPeriodRefModel().getRefNameValue()
				.split("-")[1]);

			getOrgpanel().getDataManager().initModelBySqlWhere(
				"  pk_org='" + getOrgpanel().getRefPane().getRefPK() + "' and cyear='"
					+ getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[0]
					+ "' and cperiod='"
					+ getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[1] + "'");

			getModel().setUiState(UIState.NOT_EDIT);
		    } catch (LockFailedException le) {
			error = le.getMessage();
		    } catch (VersionConflictException le) {
			throw new BusinessException(le.getBusiObject().toString(), le);
		    } catch (Exception e) {
			error = e.getMessage();
		    } finally {
			dialog.end();
		    }
		    return Boolean.TRUE;
		}

		protected void done() {
		    if (error != null) {
			ShowStatusBarMsgUtil.showErrorMsg("審核發生錯誤：", error, getModel().getContext());
		    } else {
			ShowStatusBarMsgUtil.showStatusBarMsg("勞健保資料已審核並同步至員工資料子集。", getModel().getContext());
		    }
		}
	    }.execute();
	}
    }

    public NhicalcAppModel getModel() {
	return model;
    }

    public void setModel(NhicalcAppModel model) {
	this.model = model;
	model.addAppEventListener(this);
    }

    public BillListView getEditor() {
	return editor;
    }

    public void setEditor(BillListView editor) {
	this.editor = editor;
    }

    public boolean isEnabled() {
	INhicalcMaintain nhiSrv = NCLocator.getInstance().lookup(INhicalcMaintain.class);
	boolean isaudit = false;
	try {
	    if (!StringUtils.isEmpty(getOrgpanel().getRefPane().getRefPK())
		    && !StringUtils.isEmpty(this.getOrgpanel().getPeriodRefModel().getRefNameValue())) {
		isaudit = nhiSrv.isAudit(getOrgpanel().getRefPane().getRefPK(), this.getOrgpanel().getPeriodRefModel()
			.getRefNameValue().split("-")[0], this.getOrgpanel().getPeriodRefModel().getRefNameValue()
			.split("-")[1]);
	    }
	} catch (BusinessException e) {
	    ExceptionUtils.wrappBusinessException(e.getMessage());
	}

	return (!isaudit) && model.getUiState() == UIState.NOT_EDIT && !model.getData().isEmpty();
    }

    public NhiOrgHeadPanel getOrgpanel() {
	return orgpanel;
    }

    public void setOrgpanel(NhiOrgHeadPanel orgpanel) {
	this.orgpanel = orgpanel;
    }
}
