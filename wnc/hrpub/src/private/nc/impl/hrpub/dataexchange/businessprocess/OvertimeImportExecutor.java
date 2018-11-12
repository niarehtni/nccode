package nc.impl.hrpub.dataexchange.businessprocess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.impl.hrpub.dataexchange.DataImportExecutor;
import nc.itf.hrpub.IDataExchangeExternalExecutor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;

import org.apache.commons.lang.StringUtils;

public class OvertimeImportExecutor extends DataImportExecutor implements
		IDataExchangeExternalExecutor {

	private List<String> notNullProperties = new ArrayList<String>();
	private Map<String, String> rowOTType;
	private Map<String, String> rowPsnCode;
	public OvertimeImportExecutor() throws BusinessException {
		super();

		this.notNullProperties.add("pk_psndoc");
		this.setActionOnDataExists(ExecuteActionEnum.UPDATE);
	}

	@Override
	public Object getBizEntityID() throws BusinessException {
		// 必須賦值，否則不會加載本類型
		// 實體ID為md_class的ID欄位
		// md_class.name = hi_psnjob
		return "d83e3326-c2db-44e5-9cd6-a12bdb786a2a";
	}

	@Override
	public void beforeUpdate() throws BusinessException {
		// 員工工作記錄 tbm_overtimereg
		// 增加额外的唯一性检查条件，基類在插入數據之前，增加該條件的檢查，以確保業務主鍵不重複
		this.setUniqueCheckExtraCondition("1=0");
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

					// 创建时间 CREATIONTIME
					String begindate = getDateString((String) rowNCMap
							.get(rowNo + ":"
									+ this.getReservedPropertyName("MADEDATE")));
					if (StringUtils.isEmpty(begindate)) {
						begindate = getDateString((String) rowNCMap.get(rowNo
								+ ":overtimebegindate")
								+ " 00:00:00");
					}
					rowNCMap.put(rowNo + ":creationtime", begindate);

					// 创建人 CREATOR
					rowNCMap.put(rowNo + ":creator", this.getCuserid());

					// 扣除时间 DEDUCT. 0
					rowNCMap.put(rowNo + ":deduct", 0);

					// 是否已校验 ISCHECK, 'N'
					rowNCMap.put(rowNo + ":ischeck", "N");

					// 是否需要校验 ISNEEDCHECK, 'N'
					rowNCMap.put(rowNo + ":isneedcheck", "N");

					// 是否转调休 ISTOREST, 'N'
					rowNCMap.put(rowNo + ":istorest", "N");

					// 所在考勤期间已加班时数 OVERTIMEALREADY
					rowNCMap.put(rowNo + ":overtimealready", 0);

					// 加班开始日期 OVERTIMEBEGINDATE = WDAT
					String startdate = getDateString((String) rowNCMap
							.get(rowNo + ":overtimebegindate"));

					// 加班开始时刻 OVERTIMEBEGINTIME
					String starttime = (String) rowNCMap.get(rowNo
							+ ":overtimebegintime");
					if (!StringUtils.isEmpty(starttime)
							&& starttime.length() == 4) {
						starttime = starttime.substring(0, 2) + ":"
								+ starttime.substring(2, 4) + ":00";
					} else {
						starttime = "00:00:00";
					}
					rowNCMap.put(rowNo + ":overtimebegintime", startdate + " "
							+ starttime);

					Double addTime = Double.parseDouble((String) rowNCMap
							.get(rowNo + ":overtimehour"));
					if (addTime == null) {
						addTime = 0.0;
					}

					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					java.util.Date dStart = formatter.parse(startdate + " "
							+ starttime);

					Calendar cal = Calendar.getInstance();
					cal.setTime(dStart);
					cal.add(Calendar.MINUTE, (int) (60 * addTime));
					UFDate nDate = new UFDate(cal.getTime());

					// 加班结束日期 OVERTIMEENDDATE
					rowNCMap.put(rowNo + ":overtimeenddate",
							getDateString(nDate.toString().substring(0, 10)));

					// 加班结束时刻 OVERTIMEENDTIME
					rowNCMap.put(rowNo + ":overtimeendtime",
							getDateString(nDate.toString()));

					// 加班时长 OVERTIMEHOUR
					rowNCMap.put(rowNo + ":overtimehour", addTime);

					// 所属集团 PK_GROUP
					rowNCMap.put(rowNo + ":pk_group", this.getPk_group());

					// 所属组织 PK_ORG
					rowNCMap.put(rowNo + ":pk_org", this.getPk_org());
					rowNCMap.put(rowNo + ":pk_org_v", this.getPk_org_v());

					// 加班类别 PK_OVERTIMETYPE
					String otType = this.getTimeItemByCode(this.getRowOTType()
							.get(rowNo));
					rowNCMap.put(rowNo + ":pk_overtimetype", 0);

					String otTypeCopy = this.getTimeItemCopyByOrg(otType,
							this.getPk_org());
					// 加班类别copy PK_OVERTIMETYPECOPY
					rowNCMap.put(rowNo + ":pk_overtimetypecopy", otTypeCopy);

					// 人员基本档案主键 PK_PSNDOC
					String pk_psndoc = this.getPsndocByCodeDate(this
							.getRowPsnCode().get(rowNo), startdate);

					if (StringUtils.isEmpty(pk_psndoc)) {
						throw new BusinessException("未找到員工 ["
								+ this.getRowPsnCode().get(rowNo) + "] 的主檔");
					} else {
						rowNCMap.put(rowNo + ":pk_psndoc", pk_psndoc);
					}

					// 人员工作记录 PK_PSNJOB
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

					// 组织关系主键 PK_PSNORG
					rowNCMap.put(rowNo + ":pk_psnorg",
							(String) psnjob.get("pk_psnorg"));

					// 转调休时长 TORESTHOUR
					rowNCMap.put(rowNo + ":toresthour", 0);

					// 時間戳
					String ts = getDateString(new UFDateTime().toString());
					rowNCMap.put(rowNo + ":ts", ts);

				} catch (Exception e) {
					this.getErrorMessages().put(rowNo, e.getMessage());
				}
			}
		}
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
					} else if (entry.getKey().equals("TYPE")) {
						this.getRowOTType().put(rowNo,
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

	public Map<String, String> getRowOTType() {
		if (rowOTType == null) {
			rowOTType = new HashMap<String, String>();
		}
		return rowOTType;
	}

	public void setRowOTType(Map<String, String> rowOTType) {
		this.rowOTType = rowOTType;
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
