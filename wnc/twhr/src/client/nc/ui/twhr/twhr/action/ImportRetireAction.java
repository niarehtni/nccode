package nc.ui.twhr.twhr.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		setBtnName("�R����ˎ���");
		setCode("importRetirePayData");
		// setBtnName(ResHelper.getString("6013dataitf_01", "dataitf-01-0001"));
		// // �����ڼ䵼��
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
		dlg.setTitle("���ˎ��΅R��");
		if (1 == dlg.showModal()) {
			putValue(HrAction.MESSAGE_AFTER_ACTION, "");
			final String filePath = dlg.getFilePathPane().getText();
			final String pk_legal_org = dlg.getLegalOrgPanl().getRefPK();
			final String waPeriod = dlg.getPeriodPanel().getRefPK();
			// �ж��ļ��Ƿ�������
			if (!filePath.contains("����")) {
				MessageDialog.showHintDlg(ImportRetireAction.this.getEntranceUI(), null, "Ո�������_�ļ�!");
				return;
			}
			final Integer dataType = (Integer) dlg.getUiCbxDataType().getSelectdItemValue();
			new Thread(new Runnable() {
				@Override
				public void run() {
					IProgressMonitor progressMonitor = NCProgresses.createDialogProgressMonitor(ImportRetireAction.this
							.getEntranceUI());

					progressMonitor.beginTask(ResHelper.getString("6013dataitf_01", "dataitf-01-0042"), -1); // ��������...
					progressMonitor.setProcessInfo(ResHelper.getString("6013dataitf_01", "dataitf-01-0043")); // ���ݵ�����,���Ժ�......
					try {
						Map<Integer, Set<String>> errorMap = 
								ImportRetireAction.this.RetireImport(filePath, dataType,pk_legal_org,waPeriod);
						getBatchRefreshAction().doAction(null);
						
						if(errorMap!=null && errorMap.size() > 0){
							StringBuilder sb = new StringBuilder();
							if(errorMap.get(ITwhrMaintain.ERROR_NO_MATCH_ORG)!=null){
								sb.append("��������C̖δƥ�䵽�M��,Ո�ˌ��ˆT��ӛ䛺��C��̖:");
								Set<String> psnIdSet = errorMap.get(ITwhrMaintain.ERROR_NO_MATCH_ORG);
								for(String id : psnIdSet){
									sb.append("[").append(id).append("] ");
								}
							}
							MessageDialog.showWarningDlg(ImportRetireAction.this.getEntranceUI(), 
									"����Y��", sb.toString());
						}else{
							MessageDialog.showHintDlg(ImportRetireAction.this.getEntranceUI(), null,
									ResHelper.getString("6013dataitf_01", "dataitf-01-0044")); // ���ݵ���ɹ���
						}
					} catch (Exception e) {
						Logger.error(e);
						try {
							getBatchRefreshAction().doAction(null);
						} catch (Exception e1) {
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

	protected Map<Integer, Set<String>> RetireImport(String filePath,Integer dataType,String pk_legal_org,String waPeriod) throws Exception {
		if (null == dataType) {
			Logger.error("import data type is null!");
			// "������������Ϊ��!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0019"));
		}
		if (StringUtils.isBlank(filePath)) {
			Logger.error("import filepath is bank!");
			// "���������ļ�Ϊ��!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0020"));
		} else if (!DataItfFileReaderAccount.isTxtFile(filePath)) {
			// "ֻ�ܵ���txt���������ļ�!"
			throw new Exception(ResHelper.getString("twhr_laborhealthimport", "import-00001"));
		}
		switch (dataType.intValue()) {
		case DataItfConst.VALUE_SALARY_DETAIL:
			return importretireDataSD(filePath,pk_legal_org,waPeriod);

		default:
			Logger.error("import data type is out of type arry combobox!");
			// "������������ͳ�����������б�!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0021"));
		}

	}

	private Map<Integer, Set<String>> importretireDataSD(String filePath,String pk_legal_org,String waPeriod) throws Exception {
		BaoAccountVO[] vos = null;
		List<BaoAccountVO> list = new ArrayList<BaoAccountVO>();
		Map<Integer, Set<String>> errorMap = null;
		try {
			do {
				//String pk_org = this.getOrgpanel().getRefPane().getRefPK();
				//String waperiod = AccountOrgHeadPanel.getWaperiod();
				// if (!StringUtils.isEmpty(pk_org)) {
				// vos = DataItfFileReaderAccount.readFileSD(pk_org,
				// filePath, waContext);
				vos = DataItfFileReaderAccount.readRetireTxtFileSD(waPeriod, filePath);
				if (!ArrayUtils.isEmpty(vos)) {

					//StringBuilder condition = new StringBuilder(getFilterCondition(waContext));
					// BatchOperateVO bo = new BatchOperateVO();
					// bo.setAddObjs(vos);
					for (BaoAccountVO vo : vos) {
						list.add(vo);
					}
					errorMap =
							getService().insertupdateretire(list.toArray(new BaoAccountVO[0]),pk_legal_org,waPeriod);

				}
				// }
			} while (ArrayUtils.isEmpty(vos));
		} catch (Exception e) {
			// "�ɹ�����[{0}]����¼!"
			StringBuffer errormsg = new StringBuffer();

			errormsg.append(e.getMessage());
			throw new Exception(errormsg.toString());
		}
		return errorMap;
	}

	/*protected void healthInsuranceImport(String filePath, Integer dataType,String pk_legal_org,String waPeriod) throws Exception {
		if (null == dataType) {
			Logger.error("import data type is null!");
			// "������������Ϊ��!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0019"));
		}
		if (StringUtils.isBlank(filePath)) {
			Logger.error("import filepath is bank!");
			// "���������ļ�Ϊ��!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0020"));
		} else if (!DataItfFileReaderAccount.isTxtFile(filePath)) {
			// "ֻ�ܵ���txt���������ļ�!"
			throw new Exception(ResHelper.getString("twhr_laborhealthimport", "import-00001"));
		}
		switch (dataType.intValue()) {
		case DataItfConst.VALUE_SALARY_DETAIL:
			retireimport(filePath, pk_legal_org,waPeriod);
			break;

		default:
			Logger.error("import data type is out of type arry combobox!");
			// "������������ͳ�����������б�!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0021"));
		}

	}*/

	/*private void retireimport(String filePath, String pk_legal_org,String waPeriod) throws Exception {
		BaoAccountVO[] vos = null;
		int count = 0;

		try {
			do {
				//String waperiod = AccountOrgHeadPanel.getWaperiod();
				if (!StringUtils.isEmpty(waPeriod)) {
					vos = DataItfFileReaderAccount.readRetireTxtFileSD( waPeriod, filePath, pk_legal_org);
					if (!ArrayUtils.isEmpty(vos)) {
						getService().insertupdateretire(vos,pk_legal_org);

						count += vos.length;
					}
				}
			} while (ArrayUtils.isEmpty(vos));
		} catch (Exception e) {
			// "�ɹ�����[{0}]����¼!"
			StringBuffer errormsg = new StringBuffer();

			errormsg.append(e.getMessage());
			throw new Exception(errormsg.toString());
		}

	}*/

	protected String getFilterCondition(LoginContext waContext) {
		SqlBuilder condition = new SqlBuilder();
		String pk_org = this.getOrgpanel().getRefPane().getRefPK();
		condition.append("pk_group", waContext.getPk_group());
		condition.append("and pk_org", pk_org);

		return condition.toString();
	}
}
