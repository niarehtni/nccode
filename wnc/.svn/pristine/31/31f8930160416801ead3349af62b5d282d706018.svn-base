package nc.impl.hrpub.dataexchange.businessprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.impl.hrpub.dataexchange.DataImportExecutor;
import nc.itf.hrpub.IDataExchangeExternalExecutor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;

import org.apache.commons.lang.StringUtils;

public class LeaveImportExecutor extends DataImportExecutor implements
		IDataExchangeExternalExecutor {

	private List<String> notNullProperties = new ArrayList<String>();
	private Map<String, String> rowLeaveType;
	private Map<String, String> rowPsnCode;
	public LeaveImportExecutor() throws BusinessException {
		super();

		this.notNullProperties.add("pk_psndoc");
		this.setActionOnDataExists(ExecuteActionEnum.ERROR);
	}

	@Override
	public Object getBizEntityID() throws BusinessException {
		// 必須賦值，否則不會加載本類型
		// 實體ID為md_class的ID欄位
		return "41c4a696-9c29-458f-92a1-a3a296170fb3";
	}

	@Override
	public void beforeUpdate() throws BusinessException {
		// 員工工作記錄 tbm_leavereg
		// 增加额外的唯一性检查条件，基類在插入數據之前，增加該條件的檢查，以確保業務主鍵不重複
		this.setUniqueCheckExtraCondition("pk_leavetype='$pk_leavetype$' and leavebegintime<='$leavebegintime$' and leaveendtime>='$leaveendtime$' ");
		// 增加排他的唯一性检查条件，基類在插入數據之前，用此條件替換檢查條件，不再以基類的按Code轉PK加額外條件檢查
		this.setUniqueCheckExclusiveCondition(" pk_psndoc='$pk_psndoc$' and "
				+ this.getUniqueCheckExtraCondition());

		if (this.getNcValueObjects() != null
				&& this.getNcValueObjects().size() > 0) {
			String rowNo = "";
			for (Map<String, Object> rowNCMap : this.getNcValueObjects()) {
				try {
					rowNo = rowNCMap.keySet().toArray(new String[0])[0]
							.split(":")[0];

					// 单据来源 BILLSOURCE, 0
					rowNCMap.put(rowNo + ":billsource", 0);

					// CREATIONTIME
					String begindate = getDateString((String) rowNCMap
							.get(rowNo + ":"
									+ this.getReservedPropertyName("MADEDATE")));
					if (StringUtils.isEmpty(begindate)) {
						begindate = getDateString((String) rowNCMap.get(rowNo
								+ ":leavebegindate")
								+ " 00:00:00");
					}
					rowNCMap.put(rowNo + ":creationtime", begindate);

					// CREATOR
					rowNCMap.put(rowNo + ":creator", this.getCuserid());

					// ISLACTATION
					rowNCMap.put(rowNo + ":islactation", "N");

					// LEAVEBEGINDATE
					String startdate = getDateString((String) rowNCMap
							.get(rowNo + ":leavebegindate"));

					// LEAVEBEGINTIME
					String starttime = (String) rowNCMap.get(rowNo
							+ ":leavebegintime");
					if (!StringUtils.isEmpty(starttime)
							&& starttime.length() == 4) {
						starttime = starttime.substring(0, 2) + ":"
								+ starttime.substring(2, 4) + ":00";
					} else {
						starttime = "00:00:00";
					}

					String endtime = (String) rowNCMap.get(rowNo
							+ ":leaveendtime");
					if (!StringUtils.isEmpty(endtime) && endtime.length() == 4) {
						endtime = endtime.substring(0, 2) + ":"
								+ endtime.substring(2, 4) + ":00";
					}

					rowNCMap.put(rowNo + ":leavebegintime", startdate + " "
							+ starttime);

					// LEAVEREMARK
					rowNCMap.put(
							rowNo + ":leaveremark",
							rowNCMap.get(rowNo + ":"
									+ this.getReservedPropertyName("REMARK")));

					// LEAVEENDDATE
					if (new UFDateTime(startdate + " " + endtime)
							.before(new UFDateTime(startdate + " " + starttime))) {
						rowNCMap.put(rowNo + ":leaveenddate",
								getDateString(new UFDateTime(startdate + " "
										+ endtime).getDateTimeAfter(1)
										.toString().substring(0, 10)));
						rowNCMap.put(rowNo + ":leaveendtime",
								getDateString(new UFDateTime(startdate + " "
										+ endtime).getDateTimeAfter(1)
										.toString()));
					} else {
						rowNCMap.put(rowNo + ":leaveenddate", startdate);
						rowNCMap.put(rowNo + ":leaveendtime", startdate + " "
								+ endtime);
					}

					// LEAVEHOUR
					Double addTime = Double.parseDouble((String) rowNCMap
							.get(rowNo + ":leavehour"));

					if (addTime == null) {
						addTime = 0.0;
					}
					rowNCMap.put(rowNo + ":leavehour", addTime);

					// LEAVEINDEX
					rowNCMap.put(rowNo + ":leaveindex", 1);

					// PK_GROUP
					rowNCMap.put(rowNo + ":pk_group", this.getPk_group());

					// PK_LEAVETYPE
					String leaveType = this.getTimeItemByCode(this
							.getRowLeaveType().get(rowNo));
					rowNCMap.put(rowNo + ":pk_leavetype", leaveType);

					// PK_LEAVETYPECOPY
					String leaveTypeCopy = this.getTimeItemCopyByOrg(leaveType,
							this.getPk_org());
					rowNCMap.put(rowNo + ":pk_leavetypecopy", leaveTypeCopy);

					// PK_ORG
					rowNCMap.put(rowNo + ":pk_org", this.getPk_org());
					rowNCMap.put(rowNo + ":pk_org_v", this.getPk_org_v());

					// PK_PSNDOC
					String pk_psndoc = this.getPsndocByCodeDate(this
							.getRowPsnCode().get(rowNo), startdate);

					if (StringUtils.isEmpty(pk_psndoc)) {
						throw new BusinessException("未找到員工 ["
								+ this.getRowPsnCode().get(rowNo) + "] 的主檔");
					} else {
						rowNCMap.put(rowNo + ":pk_psndoc", pk_psndoc);
					}

					// PK_PSNJOB
					Map<String, Object> psnjob = this.getPsnjob(pk_psndoc,
							startdate);
					if (psnjob != null
							&& psnjob.size() > 0
							&& !StringUtils.isEmpty((String) psnjob
									.get("pk_psnjob"))) {
						rowNCMap.put(rowNo + ":pk_psnjob",
								psnjob.get("pk_psnjob"));
						rowNCMap.put(rowNo + ":pk_dept_v",
								psnjob.get("pk_dept_v"));
					} else {
						throw new BusinessException("未找到員工工作記錄");
					}

					// PK_PSNORG
					rowNCMap.put(rowNo + ":pk_psnorg",
							(String) psnjob.get("pk_psnorg"));

					// TS
					String ts = getDateString(new UFDateTime().toString());
					rowNCMap.put(rowNo + ":ts", ts);

					// 處理銷假
					if (addTime < 0) {
						// 銷假時，本條記錄不保存，更新處理原請假信息
						this.getInsertSkip().add(rowNo);
						this.getUpdateSkip().add(rowNo);

						// 暫時跳過F02
						if (!this.getRowLeaveType().get(rowNo).equals("F02")) {
							if (!this.getExtendSQLs().containsKey(rowNo)) {
								this.getExtendSQLs().put(rowNo,
										new ArrayList<String>());
							}

							String strSQL = getUpdateSQLByCancelInfo(this
									.getRowLeaveType().get(rowNo),
									this.getPk_org(), pk_psndoc, leaveType,
									(String) rowNCMap.get(rowNo
											+ ":leavebegintime"),
									(String) rowNCMap.get(rowNo
											+ ":leaveendtime"), addTime);

							if (!StringUtils.isEmpty(strSQL)) {
								this.getExtendSQLs().get(rowNo).add(strSQL);
							} else {
								throw new BusinessException("無法找到銷假單對應的休假登記記錄。");
							}
						}
						continue;
					}
				} catch (Exception e) {
					this.getErrorMessages().put(rowNo, e.getMessage());
				}
			}
		}
	}

	private String getUpdateSQLByCancelInfo(String leaveTypeCode,
			String pk_org, String pk_psndoc, String leaveType,
			String newleavestarttime, String newleaveendtime, Double leaveHour)
			throws BusinessException {
		String strSQL = "select pk_leavereg, leavebegintime, leaveendtime, leavehour from tbm_leavereg where pk_org="
				+ getStringValue(pk_org)
				+ " and pk_psndoc="
				+ getStringValue(pk_psndoc)
				+ " and pk_leavetype="
				+ getStringValue(leaveType)
				+ " and (leavebegintime<="
				+ getStringValue(newleavestarttime)
				+ " and leaveendtime>="
				+ getStringValue(newleaveendtime) + ")";
		Map<String, Object> originLeave = (Map<String, Object>) this
				.getBaseDAO().executeQuery(strSQL, new MapProcessor());

		if (originLeave != null && originLeave.size() > 0) {
			UFDateTime originBeginTime = new UFDateTime(
					(String) originLeave.get("leavebegintime"));
			UFDateTime originEndTime = new UFDateTime(
					(String) originLeave.get("leaveendtime"));
			Double originLeaveHour = Double.valueOf(String.valueOf(originLeave
					.get("leavehour")));

			String newLeaveStartDate = "";
			String newLeaveStartTime = "";
			String newLeaveEndDate = "";
			String newLeaveEndTime = "";
			Double newLeaveHour = originLeaveHour + leaveHour;
			if (!leaveTypeCode.equals("F02")) {
				if (originBeginTime.equals(new UFDateTime(newleavestarttime))) {
					if (originBeginTime.after(new UFDateTime(newleaveendtime))) {
						// 舊的開始時間晚於新的開始時間：跨天
						newLeaveStartDate = new UFDateTime(newleaveendtime)
								.getDateTimeAfter(1).toString()
								.substring(0, 10);
						newLeaveStartTime = new UFDateTime(newleaveendtime)
								.getDateTimeAfter(1).toString();
					} else {
						newLeaveStartDate = new UFDateTime(newleaveendtime)
								.toString().substring(0, 10);
						newLeaveStartTime = new UFDateTime(newleaveendtime)
								.toString();
					}
					newLeaveEndDate = originEndTime.toString().substring(0, 10);
					newLeaveEndTime = originEndTime.toString();
				} else {
					newLeaveStartDate = originBeginTime.toString().substring(0,
							10);
					newLeaveStartTime = originBeginTime.toString();
					if (new UFDateTime(newleavestarttime)
							.before(originBeginTime)) {
						newLeaveEndDate = new UFDateTime(newleavestarttime)
								.getDateTimeAfter(1).toString()
								.substring(0, 10);
						newLeaveEndTime = new UFDateTime(newleavestarttime)
								.getDateTimeAfter(1).toString();
					} else {
						newLeaveEndDate = new UFDateTime(newleavestarttime)
								.toString().substring(0, 10);
						newLeaveEndTime = new UFDateTime(newleavestarttime)
								.toString();
					}
				}
			}
			strSQL = "UPDATE tbm_leavereg SET ";
			if (!leaveTypeCode.equals("F02")) {
				strSQL += "leavebegindate=" + getStringValue(newLeaveStartDate)
						+ ", leavebegintime="
						+ getStringValue(newLeaveStartTime) + ", leaveenddate="
						+ getStringValue(newLeaveEndDate) + ", leaveendtime="
						+ getStringValue(newLeaveEndTime) + ", ";
			}
			strSQL += "leavehour=" + String.valueOf(newLeaveHour)
					+ ", isleaveoff='Y' WHERE pk_leavereg="
					+ getStringValue(originLeave.get("pk_leavereg"));
		} else {
			strSQL = "";
		}

		return strSQL;
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
		if (this.getJsonValueObjects() != null
				&& this.getJsonValueObjects().size() > 0) {
			for (Map<String, Object> jsonobj : this.getJsonValueObjects()) {
				String rowNo = "";
				for (Entry<String, Object> entry : jsonobj.entrySet()) {
					if (entry.getKey().equals("ROWNO")) {
						rowNo = (String) entry.getValue();
					} else if (entry.getKey().equals("OFFNO")) {
						this.getRowLeaveType().put(rowNo,
								(String) entry.getValue());
					} else if (entry.getKey().equals("EMPNO")) {
						this.getRowPsnCode().put(rowNo,
								(String) entry.getValue());
					}
				}
			}
		}
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
	public void beforeInsertOperation(Map<String, Object> rowMap)
			throws BusinessException {
		for (String checkKey : this.notNullProperties) {
			if (!rowMap.containsKey(checkKey)
					|| StringUtils.isEmpty((String) rowMap.get(checkKey
							.toLowerCase()))
					|| "~".equals(rowMap.get(checkKey.toLowerCase()))) {
				throw new BusinessException("非空校驗錯誤：新增記錄時 [" + checkKey
						+ "] 不能為空。");
			}
		}
	}

	@Override
	public void beforeUpdateOperation(Map<String, Object> rowMap)
			throws BusinessException {
		// TODO 自动生成的方法存根

	}

	public Map<String, String> getRowLeaveType() {
		if (rowLeaveType == null) {
			rowLeaveType = new HashMap<String, String>();
		}
		return rowLeaveType;
	}

	public void setRowLeaveType(Map<String, String> rowLeaveType) {
		this.rowLeaveType = rowLeaveType;
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
	public void doUpdateByBP() throws BusinessException {
		// TODO 自动生成的方法存根

	}

}
