package nc.ui.twhr.twhr.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.twhr.DataItfFileReaderAccount;
import nc.itf.twhr.ITwhrMaintain;
import nc.pub.wa.datainterface.DataItfConst;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.twhr.twhr.ace.view.AccountOrgHeadPanel;
import nc.ui.uif2.NCAction;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.twhr.nhicalc.BaoAccountVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class ImportRetireAction extends HrAction {
	private NCAction batchRefreshAction;

	private AccountOrgHeadPanel orgpanel;

	private ITwhrMaintain service;

	public NCAction getBatchRefreshAction() {
		return batchRefreshAction;
	}

	public void setBatchRefreshAction(NCAction batchRefreshAction) {
		this.batchRefreshAction = batchRefreshAction;
	}

	public AccountOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(AccountOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

	public ImportRetireAction() {
		setBtnName("匯入勞退帳單");
		setCode("importRetirePayData");
		// setBtnName(ResHelper.getString("6013dataitf_01", "dataitf-01-0001"));
		// // 批量期间导入
	}

	public ITwhrMaintain getService() {
		if (null == service) {
			service = NCLocator.getInstance().lookup(ITwhrMaintain.class);
		}
		return service;
	}

	@Override
	public void doAction(ActionEvent paramActionEvent) throws Exception {
		AccoutImportDlg dlg = new AccoutImportDlg(getContext());
		if (1 == dlg.showModal()) {
			putValue(HrAction.MESSAGE_AFTER_ACTION, "");
			final String filePath = dlg.getFilePathPane().getText();
			// 判断文件是否是劳退
			if (!filePath.contains("勞退")) {
				MessageDialog.showHintDlg(ImportRetireAction.this.getEntranceUI(), null, "請導入正確文件!");
				return;
			}
			final Integer dataType = (Integer) dlg.getUiCbxDataType().getSelectdItemValue();
			final LoginContext waContext = (LoginContext) getContext();
			new Thread(new Runnable() {
				@Override
				public void run() {
					IProgressMonitor progressMonitor = NCProgresses.createDialogProgressMonitor(ImportRetireAction.this
							.getEntranceUI());

					progressMonitor.beginTask(ResHelper.getString("6013dataitf_01", "dataitf-01-0042"), -1); // 导入数据...
					progressMonitor.setProcessInfo(ResHelper.getString("6013dataitf_01", "dataitf-01-0043")); // 数据导入中,请稍后......
					try {
						ImportRetireAction.this.RetireImport(filePath, waContext, dataType);
						getBatchRefreshAction().doAction(null);
						MessageDialog.showHintDlg(ImportRetireAction.this.getEntranceUI(), null,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0044")); // 数据导入成功！
					} catch (Exception e) {
						Logger.error(e);
						try {
							getBatchRefreshAction().doAction(null);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						MessageDialog.showErrorDlg(ImportRetireAction.this.getEntranceUI(), null, e.getMessage());
					} finally {

						progressMonitor.done();

					}
				}
			}).start();

		} else {
			putValue(HrAction.MESSAGE_AFTER_ACTION, "");
		}

	}

	protected void RetireImport(String filePath, LoginContext waContext, Integer dataType) throws Exception {
		if (null == dataType) {
			Logger.error("import data type is null!");
			// "导入数据类型为空!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0019"));
		}
		if (StringUtils.isBlank(filePath)) {
			Logger.error("import filepath is bank!");
			// "导入数据文件为空!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0020"));
		} else if (!DataItfFileReaderAccount.isTxtFile(filePath)) {
			// "只能导入txt类型数据文件!"
			throw new Exception(ResHelper.getString("twhr_laborhealthimport", "import-00001"));
		}
		switch (dataType.intValue()) {
		case DataItfConst.VALUE_SALARY_DETAIL:
			importretireDataSD(filePath, waContext);
			break;

		default:
			Logger.error("import data type is out of type arry combobox!");
			// "导入的数据类型超出定义的下列表!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0021"));
		}

	}

	private void importretireDataSD(String filePath, LoginContext waContext) throws Exception {
		BaoAccountVO[] vos = null;
		int count = 0;
		List<BaoAccountVO> list = new ArrayList<BaoAccountVO>();
		try {
			do {
				String pk_org = this.getOrgpanel().getRefPane().getRefPK();
				waContext.setPk_org(pk_org);
				String waperiod = AccountOrgHeadPanel.getWaperiod();
				// if (!StringUtils.isEmpty(pk_org)) {
				// vos = DataItfFileReaderAccount.readFileSD(pk_org,
				// filePath, waContext);
				vos = DataItfFileReaderAccount.readRetireTxtFileSD(pk_org, waperiod, filePath, waContext);
				if (!ArrayUtils.isEmpty(vos)) {

					StringBuilder condition = new StringBuilder(getFilterCondition(waContext));
					// BatchOperateVO bo = new BatchOperateVO();
					// bo.setAddObjs(vos);
					for (BaoAccountVO vo : vos) {
						list.add(vo);
					}
					getService().insertupdateretire(list.toArray(new BaoAccountVO[0]));
					count += vos.length;
				}
				// }
			} while (ArrayUtils.isEmpty(vos));
		} catch (Exception e) {
			// "成功导入[{0}]条记录!"
			StringBuffer errormsg = new StringBuffer();

			errormsg.append(e.getMessage());
			throw new Exception(errormsg.toString());
		}
	}

	protected void healthInsuranceImport(String filePath, LoginContext waContext, Integer dataType) throws Exception {
		if (null == dataType) {
			Logger.error("import data type is null!");
			// "导入数据类型为空!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0019"));
		}
		if (StringUtils.isBlank(filePath)) {
			Logger.error("import filepath is bank!");
			// "导入数据文件为空!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0020"));
		} else if (!DataItfFileReaderAccount.isTxtFile(filePath)) {
			// "只能导入txt类型数据文件!"
			throw new Exception(ResHelper.getString("twhr_laborhealthimport", "import-00001"));
		}
		switch (dataType.intValue()) {
		case DataItfConst.VALUE_SALARY_DETAIL:
			retireimport(filePath, waContext);
			break;

		default:
			Logger.error("import data type is out of type arry combobox!");
			// "导入的数据类型超出定义的下列表!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0021"));
		}

	}

	private void retireimport(String filePath, LoginContext waContext) throws Exception {
		BaoAccountVO[] vos = null;
		int count = 0;

		try {
			do {
				String pk_org = this.getOrgpanel().getRefPane().getRefPK();
				String waperiod = AccountOrgHeadPanel.getWaperiod();
				if (!StringUtils.isEmpty(pk_org)) {
					vos = DataItfFileReaderAccount.readRetireTxtFileSD(pk_org, waperiod, filePath, waContext);
					if (!ArrayUtils.isEmpty(vos)) {
						getService().insertupdateretire(vos);

						count += vos.length;
					}
				}
			} while (ArrayUtils.isEmpty(vos));
		} catch (Exception e) {
			// "成功导入[{0}]条记录!"
			StringBuffer errormsg = new StringBuffer();

			errormsg.append(e.getMessage());
			throw new Exception(errormsg.toString());
		}

	}

	protected String getFilterCondition(LoginContext waContext) {
		SqlBuilder condition = new SqlBuilder();
		String pk_org = this.getOrgpanel().getRefPane().getRefPK();
		condition.append("pk_group", waContext.getPk_group());
		condition.append("and pk_org", pk_org);

		return condition.toString();
	}
}
