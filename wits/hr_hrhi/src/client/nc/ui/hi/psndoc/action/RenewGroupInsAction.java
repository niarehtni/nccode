package nc.ui.hi.psndoc.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.pubitf.para.SysInitQuery;
import nc.ui.hi.employee.model.EmployeePsndocModel;
import nc.ui.hi.employee.view.PeriodChooseDlg;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

public class RenewGroupInsAction extends HrAction {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 1826333282547686387L;
	private LoginContext context;
	private EmployeePsndocModel model;
	private String pk_wa_class = null;
	private String cStartPeriod = null;
	private String cEndPeriod = null;
	private UFDate dEffectiveDate = null;
	private String errorMessage = "";

	public String getPk_wa_class() {
		return pk_wa_class;
	}

	public void setPk_wa_class(String pk_wa_class) {
		this.pk_wa_class = pk_wa_class;
	}

	public String getcStartPeriod() {
		return cStartPeriod;
	}

	public void setcStartPeriod(String cStartPeriod) {
		this.cStartPeriod = cStartPeriod;
	}

	public String getcEndPeriod() {
		return cEndPeriod;
	}

	public void setcEndPeriod(String cEndPeriod) {
		this.cEndPeriod = cEndPeriod;
	}

	public UFDate getdEffectiveDate() {
		return dEffectiveDate;
	}

	public void setdEffectiveDate(UFDate dEffectiveDate) {
		this.dEffectiveDate = dEffectiveDate;
	}

	public RenewGroupInsAction() {
		this.setBtnName("ͬ���F������");
		this.setCode("RenewGroupInsAction");
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		if (getModel().getSelectedOperaRows() == null
				|| getModel().getSelectedOperaRows().length == 0) {
			errorMessage = "ͬ���F�������l���e�`��" + "Ո�x��Ҫ�M��ͬ���ĆT��";
			this.putValue("message_after_action", errorMessage);
			return;
		}

		JComponent parentUi = getModel().getContext().getEntranceUI();
		PeriodChooseDlg dlg = new PeriodChooseDlg(parentUi,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
						"twhr_personalmgt", "068J61035-0006")/* @res Ո�x��н�Y�������g */);

		dlg.initUI();
		dlg.setContext(this.getContext());
		dlg.loadPeriod();

		if (dlg.showModal() == UIDialog.ID_OK) {

			this.setPk_wa_class(dlg.getPk_wa_class());
			this.setcStartPeriod(dlg.getcStartPeriod());
			this.setcEndPeriod(dlg.getcEndPeriod());
			this.setdEffectiveDate(dlg.getdEffectiveDate());

			new SwingWorker() {
				BannerTimerDialog dialog = new BannerTimerDialog(
						SwingUtilities.getWindowAncestor(getModel()
								.getContext().getEntranceUI()));
				String error = null;

				protected Boolean doInBackground() throws Exception {
					try {
						dialog.setStartText("����ͬ���F����н���� ");
						dialog.start();

						Object[] rows = getModel().getSelectedOperaDatas();
						List<String> pk_psndocs = new ArrayList<String>();
						for (Object row : rows) {
							pk_psndocs.add(((PsndocAggVO) row).getParentVO()
									.getPk_psndoc());
						}

						// �@ȡ�Y��
						IPsndocSubInfoService4JFS service = NCLocator
								.getInstance().lookup(
										IPsndocSubInfoService4JFS.class);
						service.renewGroupIns(getContext().getPk_org(),
								pk_psndocs.toArray(new String[0]),
								getPk_wa_class(), getcStartPeriod(),
								getcEndPeriod(), getdEffectiveDate());

					} catch (LockFailedException le) {
						error = le.getMessage();
					} catch (VersionConflictException le) {
						throw new BusinessException(le.getBusiObject()
								.toString(), le);
					} catch (Exception e) {
						error = e.getMessage();
					} finally {
						dialog.end();
					}
					return Boolean.TRUE;
				}

				protected void done() {
					if (error != null) {
						errorMessage = "ͬ���F����н�����l���e�`��" + error;
					} else {
						errorMessage = "ͬ���F����н�����ɹ���";
					}
				}
			}.execute();

			this.putValue("message_after_action", errorMessage);
		}
	}

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}

	public EmployeePsndocModel getModel() {
		return model;
	}

	public void setModel(EmployeePsndocModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	@Override
	public boolean isActionEnable() {
		if (StringUtils.isEmpty(this.getContext().getPk_org())) {
			return false;
		} else {
			try {
				return SysInitQuery.getParaBoolean(
						this.getContext().getPk_org(), "TWHR06").booleanValue();
			} catch (BusinessException e) {
				ShowStatusBarMsgUtil.showStatusBarMsg(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"twhr_personalmgt", "068J61035-0010")/*
																	 * @res
																	 * ȡ�_���ڽ��������l���e�`
																	 * ��
																	 */
								+ e.getMessage(), getModel().getContext());
			}
		}

		return true;
	}

}