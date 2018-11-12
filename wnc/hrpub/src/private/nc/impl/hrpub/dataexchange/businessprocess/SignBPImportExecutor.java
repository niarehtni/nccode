package nc.impl.hrpub.dataexchange.businessprocess;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import nc.impl.hrpub.dataexchange.DataImportExecutor;
import nc.impl.ta.signcard.SignCardRegisterMaintainImpl;
import nc.itf.hrpub.IDataExchangeExternalExecutor;
import nc.itf.ta.ISignCardRegisterManageMaintain;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.signcard.SignRegVO;

import org.apache.commons.lang.StringUtils;

public class SignBPImportExecutor extends DataImportExecutor implements IDataExchangeExternalExecutor {

	private Map<String, SignRegVO> rowNCVO;

	public SignBPImportExecutor() throws BusinessException {
		super();
		// TODO 自动生成的构造函数存根
	}

	@Override
	public Object getBizEntityID() throws BusinessException {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public void beforeUpdate() throws BusinessException {
		if (this.getNcValueObjects() != null && this.getNcValueObjects().size() > 0) {
			String rowNo = "";
			for (Map<String, Object> rowNCMap : this.getNcValueObjects()) {
				try {
					rowNo = rowNCMap.keySet().toArray(new String[0])[0].split(":")[0];

					SignRegVO vo = new SignRegVO();
					// BILLSOURCE
					vo.setBillsource(0); // 0:申請單
					// CREATIONTIME
					vo.setCreationtime(new UFDateTime());
					// CREATOR
					vo.setCreator(this.getCuserid());
					// PK_GROUP
					vo.setPk_group(this.getPk_group());
					// PK_ORG
					vo.setPk_org(this.getPk_org());
					// PK_ORG_V
					vo.setPk_org_v(this.getPk_org_v());
					// PK_PSNDOC
					if (StringUtils.isEmpty((String) rowNCMap.get(rowNo + ":pk_psndoc"))) {
						throw new BusinessException("人員基本資訊不能為空");
					}
					vo.setPk_psndoc((String) rowNCMap.get(rowNo + ":pk_psndoc"));
					// SIGNTIME
					if (StringUtils.isEmpty((String) rowNCMap.get(rowNo + ":signtime"))) {
						throw new BusinessException("簽卡時間不能為空");
					}
					vo.setSigntime(new UFDateTime((String) rowNCMap.get(rowNo + ":signtime")));
					// SIGNDATE
					if (StringUtils.isEmpty((String) rowNCMap.get(rowNo + ":signdate"))) {
						vo.setSigndate(new UFLiteralDate((String) rowNCMap.get(rowNo + ":signtime")));
					} else {
						vo.setSigndate(new UFLiteralDate((String) rowNCMap.get(rowNo + ":signdate")));
					}

					Map<String, Object> psnjob = this.getPsnjob(vo.getPk_psndoc(), vo.getSigndate().toString());
					if (psnjob != null && psnjob.size() > 0 && !StringUtils.isEmpty((String) psnjob.get("pk_psnjob"))) {
						// PK_PSNJOB
						vo.setPk_psnjob((String) psnjob.get("pk_psnjob"));
						// PK_DEPT_V
						vo.setPk_dept_v((String) psnjob.get("pk_dept_v"));
						// PK_PSNORG
						vo.setPk_psnorg((String) psnjob.get("pk_psnorg"));
					} else {
						throw new BusinessException("未找到員工工作記錄");
					}
					// SIGNSTATUS
					if (StringUtils.isEmpty((String) rowNCMap.get(rowNo + ":signstatus"))) {
						throw new BusinessException("簽卡狀態不能為空");
					}
					vo.setSignstatus(Integer.valueOf((String) rowNCMap.get(rowNo + ":signstatus")));

					// SIGNREMARK
					vo.setSignremark((String) rowNCMap.get(rowNo + ":signremark"));
					// SIGNREASON
					vo.setSignreason((String) rowNCMap.get(rowNo + ":signreason"));
					vo.setDr(0);
					vo.setTs(new UFDateTime());
					this.getRowNCVO().put(rowNo, vo);
				} catch (Exception e) {
					this.getErrorMessages().put(rowNo, e.getMessage());
				}
			}
		}
	}

	public Map<String, SignRegVO> getRowNCVO() {
		if (rowNCVO == null) {
			rowNCVO = new HashMap<String, SignRegVO>();
		}
		return rowNCVO;
	}

	public void setRowNCVO(Map<String, SignRegVO> rowNCVO) {
		this.rowNCVO = rowNCVO;
	}

	@Override
	public void afterUpdate() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void afterConvert() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void beforeConvert() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void beforeQuery() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void afterQuery() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void doUpdateByBP() throws BusinessException {
		if (this.getRowNCVO() != null && this.getRowNCVO().size() > 0) {
			for (Entry<String, SignRegVO> rowData : this.getRowNCVO().entrySet()) {
				try {
					this.getVOSaveService().insertData(new SignRegVO[] { rowData.getValue() }, false);
				} catch (BusinessException e) {
					this.getErrorMessages().put(rowData.getKey(), e.getMessage());
				}
			}
		}
	}

	ISignCardRegisterManageMaintain saveService = null;

	private ISignCardRegisterManageMaintain getVOSaveService() {
		if (saveService == null) {
			saveService = new SignCardRegisterMaintainImpl();
		}

		return saveService;
	}

	@Override
	public void beforeInsertOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void beforeUpdateOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自动生成的方法存根

	}

}
