package nc.impl.hrpub.dataexchange.businessprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.impl.hrpub.dataexchange.DataImportExecutor;
import nc.itf.hrpub.IDataExchangeExternalExecutor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

public class ImportDataImportExecutor extends DataImportExecutor implements IDataExchangeExternalExecutor {

	private List<String> notNullProperties = new ArrayList<String>();
	private Map<String, String> rowPsnCode;

	public ImportDataImportExecutor() throws BusinessException {
		super();

		this.notNullProperties.add("pk_psndoc");
		this.setActionOnDataExists(ExecuteActionEnum.UPDATE);
	}

	@Override
	public Object getBizEntityID() throws BusinessException {
		// ����xֵ����t�������d�����
		// ���wID��md_class��ID��λ
		return "556ce8f3-ab1e-4820-8e1e-b34e500cfd22";
	}

	@Override
	public void beforeUpdate() throws BusinessException {
		// ���Ӷ����Ψһ�Լ������������ڲ��딵��֮ǰ������ԓ�l���ęz�飬�Դ_���I�����I�����}
		this.setUniqueCheckExtraCondition("1=0");
		// ����������Ψһ�Լ������������ڲ��딵��֮ǰ���ô˗l����Q�z��l���������Ի�İ�Code�DPK���~��l���z��
		this.setUniqueCheckExclusiveCondition(this.getUniqueCheckExtraCondition());

		if (this.getNcValueObjects() != null && this.getNcValueObjects().size() > 0) {
			String rowNo = "";
			for (Map<String, Object> rowNCMap : this.getNcValueObjects()) {
				try {
					rowNo = rowNCMap.keySet().toArray(new String[0])[0].split(":")[0];

					// CALENDARDATE
					String signDate = (String) rowNCMap.get(rowNo + ":" + this.getReservedPropertyName("WDAT"));

					if (!StringUtils.isEmpty(signDate)) {
						rowNCMap.put(rowNo + ":calendardate", this.getDateString(signDate));
					}

					String signTime = (String) rowNCMap.get(rowNo + ":calendartime");

					if (!StringUtils.isEmpty(signTime)) {
						rowNCMap.put(rowNo + ":calendardate", this.getDateString(signTime.substring(0, 10)));
						signDate = this.getDateString(signTime.substring(0, 10));
					} else if (!StringUtils.isEmpty(signDate)) {
						rowNCMap.put(
								rowNo + ":calendartime",
								this.getDateString(signDate + " " + signTime.substring(0, 2) + ":"
										+ signTime.substring(2).trim() + ":00"));
					}
					// CREATOR
					rowNCMap.put(rowNo + ":creator", this.getCuserid());

					// DATASOURCE
					rowNCMap.put(rowNo + ":datasource", 0);

					// DATATYPE
					String signType = (String) rowNCMap.get(rowNo + ":datastatus");
					if (signType.equals("1")) {
						rowNCMap.put(rowNo + ":datastatus", 0);
					} else if (signType.equals("2")) {
						rowNCMap.put(rowNo + ":datastatus", 1);
					} else {
						rowNCMap.put(rowNo + ":datastatus", 2);
					}
					rowNCMap.put(rowNo + ":datatype", 0);

					// PK_GROUP
					rowNCMap.put(rowNo + ":pk_group", this.getPk_group());

					// PK_ORG
					rowNCMap.put(rowNo + ":pk_org", this.getPk_org());

					// PK_PSNDOC
					String pk_psndoc = this.getPsndocByCodeDate(this.getRowPsnCode().get(rowNo), signDate);

					if (StringUtils.isEmpty(pk_psndoc)) {
						throw new BusinessException("δ�ҵ��T�� [" + this.getRowPsnCode().get(rowNo) + "] �����n");
					} else {
						rowNCMap.put(rowNo + ":pk_psndoc", pk_psndoc);
					}

					PsndocDismissedValidator dismChecker = new PsndocDismissedValidator();
					dismChecker.validate(pk_psndoc, new UFLiteralDate(signDate));

					// DR
					rowNCMap.put(rowNo + ":dr", 0);

					// TS
					String ts = getDateString(new UFDateTime().toString());
					rowNCMap.put(rowNo + ":ts", ts);
				} catch (Exception e) {
					this.getErrorMessages().put(rowNo, e.getMessage());
				}
			}
		}
	}

	public Map<String, String> getRowPsnCode() {
		if (rowPsnCode == null) {
			rowPsnCode = new HashMap<String, String>();
		}
		return rowPsnCode;
	}

	public void setRowPsnCode(Map<String, String> rowPsnCode) {
		this.rowPsnCode = rowPsnCode;
	}

	@Override
	public void afterUpdate() throws BusinessException {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void afterConvert() throws BusinessException {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void beforeConvert() throws BusinessException {
		if (this.getJsonValueObjects() != null && this.getJsonValueObjects().size() > 0) {
			for (Map<String, Object> jsonobj : this.getJsonValueObjects()) {
				String rowNo = "";
				for (Entry<String, Object> entry : jsonobj.entrySet()) {
					if (entry.getKey().equals("ROWNO")) {
						rowNo = (String) entry.getValue();
					} else if (entry.getKey().equals("EMPNO")) {
						this.getRowPsnCode().put(rowNo, (String) entry.getValue());
					}
				}
			}
		}
	}

	@Override
	public void beforeQuery() throws BusinessException {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void afterQuery() throws BusinessException {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void beforeInsertOperation(Map<String, Object> rowMap) throws BusinessException {
		for (String checkKey : this.notNullProperties) {
			if (!rowMap.containsKey(checkKey) || StringUtils.isEmpty((String) rowMap.get(checkKey.toLowerCase()))
					|| "~".equals(rowMap.get(checkKey.toLowerCase()))) {
				throw new BusinessException("�ǿ�У��e�`������ӛ䛕r [" + checkKey + "] ���ܞ�ա�");
			}
		}
	}

	@Override
	public void beforeUpdateOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO �Զ����ɵķ������

	}

	@Override
	public void doUpdateByBP() throws BusinessException {
		// TODO �Զ����ɵķ������

	}

}
