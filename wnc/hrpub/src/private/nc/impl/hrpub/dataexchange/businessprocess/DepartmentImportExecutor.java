package nc.impl.hrpub.dataexchange.businessprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.uap.oid.OidGenerator;
import nc.impl.hrpub.dataexchange.DataImportExecutor;
import nc.itf.hrpub.IDataExchangeExternalExecutor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;

import org.apache.commons.lang.StringUtils;

/**
 * 部門檔案導入業務加工類
 * 
 * 生成SQL中可使用$NEWID$表示新保存部門的PK_DEPT， 後續執行腳本時基類會自動替換
 * 
 * @author ssx
 * 
 */
public class DepartmentImportExecutor extends DataImportExecutor implements IDataExchangeExternalExecutor {
	String newDeptPK = "'$NEWID$'";
	String newDeptVersionPK = "'$NEWVID$'";
	boolean isNew = true;

	public DepartmentImportExecutor() throws BusinessException {
		super();

		this.setActionOnDataExists(ExecuteActionEnum.UPDATE);
	}

	@Override
	public void beforeConvert() throws BusinessException {
		// 数据预处理：去掉值的首尾空格
		if (this.getJsonValueObjects() != null && this.getJsonValueObjects().size() > 0) {
			for (Map<String, Object> jsonobj : this.getJsonValueObjects()) {
				for (Entry<String, Object> entry : jsonobj.entrySet()) {
					jsonobj.put(entry.getKey(), ((String) entry.getValue()).trim());
				}
			}
		}
	}

	@Override
	public void beforeUpdate() throws BusinessException {
		if (this.getNcValueObjects() != null && this.getNcValueObjects().size() > 0) {
			String rowNo = "";
			try {
				for (Map<String, Object> rowNCMap : this.getNcValueObjects()) {
					rowNo = rowNCMap.keySet().toArray(new String[0])[0].split(":")[0];

					String deptCode = (String) rowNCMap.get(rowNo + ":code");
					String effectDate = getDateString((String) rowNCMap.get(rowNo + ":createdate"));
					getNewIDByCode(deptCode, effectDate);

					if (isNew) {
						// 部門新增時
						// 補充：版本PK
						rowNCMap.put(rowNo + ":pk_vid", newDeptVersionPK.replaceAll("'", ""));
						// 補充：集團PK
						rowNCMap.put(rowNo + ":pk_group", this.getPk_group());
						// 補充：多語名稱
						if (!rowNCMap.containsKey(rowNo + "name2")) {
							rowNCMap.put(rowNo + ":name2", rowNCMap.get(rowNo + ":name"));
						}
						if (!rowNCMap.containsKey(rowNo + "name3")) {
							rowNCMap.put(rowNo + ":name3", rowNCMap.get(rowNo + ":glbdef5"));
						}
						// 補充：顯示順序
						rowNCMap.put(rowNo + ":displayorder", "999999");
						// 補充：是否啟用
						rowNCMap.put(rowNo + ":enablestate", 2);
						// 補充：是否最新版本
						rowNCMap.put(rowNo + ":islastversion", "Y");
						// 補充：innercode，只處理新增，異動不支持
						String innercode = getInnerCode((String) rowNCMap.get(rowNo + ":code"),
								(String) rowNCMap.get(rowNo + ":pk_fatherorg"));
						if (!StringUtils.isEmpty(innercode)) {
							rowNCMap.put(rowNo + ":innercode", innercode);
						}
						// 補充：審計信息-創建時間
						if (rowNCMap.containsKey(rowNo + ":creationtime")) {
							String madedate = (String) rowNCMap.get(rowNo + ":creationtime");
							if (!StringUtils.isEmpty(madedate.trim())) {
								String[] datetime = madedate.trim().split(" ");
								rowNCMap.put(rowNo + ":creationtime", datetime[0].replace("/", "-") + " " + datetime[1]);
							} else {
								rowNCMap.put(rowNo + ":creationtime", "");
							}
						}
					} else {
						// 部門已存在的時候，只支持更新部門撤消，更名

						// 刪除組織：不支持組織間移轉
						if (rowNCMap.containsKey(rowNo + ":pk_org")) {
							rowNCMap.remove(rowNo + ":pk_org");
						}

						// 補充：innercode，只處理新增，異動不支持
						String innercode = getInnerCode((String) rowNCMap.get(rowNo + ":code"),
								(String) rowNCMap.get(rowNo + ":pk_fatherorg"));
						if (!StringUtils.isEmpty(innercode)) {
							rowNCMap.put(rowNo + ":innercode", innercode);
						}
					}

					// 補充：部門是否撤消（根據傳入的部門撤消日期判斷）
					if (rowNCMap.containsKey(rowNo + ":deptcanceldate")) {
						rowNCMap.put(rowNo + ":hrcanceled", isCanceled(rowNCMap, rowNo) ? "Y" : "N");
					}

					if (!this.getExtendSQLs().containsKey(rowNo)) {
						this.getExtendSQLs().put(rowNo, new ArrayList<String>());
					}

					if (isNew) {
						// 新增部門版本
						this.getExtendSQLs().get(rowNo).addAll(getExtNewDeptVersionSQL(rowNCMap, rowNo));

						// 新增組織
						this.getExtendSQLs().get(rowNo).addAll(getExtNewOrgSQL(rowNCMap, rowNo));

						// 新增組織版本
						this.getExtendSQLs().get(rowNo).addAll(getExtNewOrgVersionSQL(rowNCMap, rowNo));

						// 新增部門負責人
						this.getExtendSQLs().get(rowNo).addAll(getExtNewPrincipleSQL(rowNCMap, rowNo));

						// 新增部門變更記錄
						this.getExtendSQLs().get(rowNo).addAll(getExtNewHistorySQL(rowNCMap, rowNo));

					}
				}
			} catch (Exception e) {
				this.getErrorMessages().put(rowNo, e.getMessage());
			}
		}
	}

	private List<String> getExtNewHistorySQL(Map<String, Object> rowNCMap, String rowno) {
		List<String> sqls = new ArrayList<String>();
		sqls.add("INSERT INTO OM_DEPTHISTORY "
				+ "(CHANGENUM, CHANGETYPE, CODE, DEPTLEVEL, DR, EFFECTDATE, ISRECEIVED, MEMO, "
				+ "NAME, NAME2, NAME3, NAME4, NAME5, NAME6, "
				+ "PK_DEPT, PK_DEPTHISTORY, PK_ORG, PK_ORG_V, PRINCIPAL) "
				+ "VALUES "
				+ "('"
				+ OidGenerator.getInstance().nextOid()
				+ "', '1', '"
				+ (String) rowNCMap.get(rowno + ":code")
				+ "', '~', 0, "
				+ getDateString(rowNCMap, rowno)
				+ ", 'N', null, "
				+ getStringValue(rowNCMap.get(rowno + ":name"))
				+ ", "
				+ (rowNCMap.containsKey(rowno + ":name2") ? getStringValue(rowNCMap.get(rowno + ":name2"))
						: getStringValue(rowNCMap.get(rowno + ":name")))
				+ ", "
				+ (rowNCMap.containsKey(rowno + ":name3") ? getStringValue(rowNCMap.get(rowno + ":name3"))
						: getStringValue(rowNCMap.get(rowno + ":glbdef5"))) + ", null, null, null, " + newDeptPK
				+ ", '" + OidGenerator.getInstance().nextOid() + "', '" + rowNCMap.get(rowno + ":pk_org")
				+ "', (select pk_vid from org_orgs where pk_org='" + rowNCMap.get(rowno + ":pk_org")
				+ "' and islastversion='Y'), '~');");

		return sqls;
	}

	private List<String> getExtNewOrgVersionSQL(Map<String, Object> rowNCMap, String rowno) {
		List<String> sqls = new ArrayList<String>();
		sqls.add("INSERT INTO ORG_ORGS_V "
				+ "(ADDRESS, CODE, COUNTRYZONE, CREATIONTIME, CREATOR, DATAORIGINFLAG, "
				+ "DEF1, DEF10, DEF11, DEF12, DEF13, DEF14, DEF15, DEF16, DEF17, DEF18, DEF19, DEF2, DEF20, DEF3, DEF4, DEF5, DEF6, DEF7, DEF8, DEF9, "
				+ "DR, ENABLESTATE, INNERCODE, ISBUSINESSUNIT, ISLASTVERSION, MEMO, MNECODE, MODIFIEDTIME, MODIFIER, "
				+ "NAME, NAME2, NAME3, NAME4, NAME5, NAME6, NCINDUSTRY, ORGANIZATIONCODE, "
				+ "ORGTYPE1, ORGTYPE10, ORGTYPE11, ORGTYPE12, ORGTYPE13, ORGTYPE14, ORGTYPE15, ORGTYPE16, ORGTYPE17, ORGTYPE18, ORGTYPE19, ORGTYPE2, "
				+ "ORGTYPE20, ORGTYPE21, ORGTYPE22, ORGTYPE23, ORGTYPE24, ORGTYPE25, ORGTYPE26, ORGTYPE27, ORGTYPE28, ORGTYPE29, ORGTYPE3, ORGTYPE30, "
				+ "ORGTYPE31, ORGTYPE32, ORGTYPE33, ORGTYPE34, ORGTYPE35, ORGTYPE36, ORGTYPE37, ORGTYPE38, ORGTYPE39, ORGTYPE4, ORGTYPE40, ORGTYPE41, "
				+ "ORGTYPE42, ORGTYPE43, ORGTYPE44, ORGTYPE45, ORGTYPE46, ORGTYPE47, ORGTYPE48, ORGTYPE49, ORGTYPE5, ORGTYPE50, ORGTYPE6, ORGTYPE7, "
				+ "ORGTYPE8, ORGTYPE9, PK_FATHERORG, PK_FORMAT, PK_GROUP, PK_ORG, PK_OWNORG, PK_TIMEZONE, PK_VID, PRINCIPAL, "
				+ "SHORTNAME, SHORTNAME2, SHORTNAME3, SHORTNAME4, SHORTNAME5, SHORTNAME6, TEL, VENDDATE, "
				+ "VNAME, VNAME2, VNAME3, VNAME4, VNAME5, VNAME6, VNO, VSTARTDATE, CHARGELEADER, ENTITYTYPE, ISBALANCEUNIT, "
				+ "ISRETAIL, PK_ACCPERIODSCHEME, PK_CONTROLAREA, PK_CORP, PK_CURRTYPE, PK_EXRATESCHEME, REPORTCONFIRM, WORKCALENDAR) "
				+ "VALUES " + "(null, "
				+ getStringValue(rowNCMap.get(rowno + ":code"))
				+ ", '~', "
				+ getStringValue(rowNCMap.get(rowno + ":creationtime"))
				+ ", "
				+ getStringValue(rowNCMap.get(rowno + ":creator"))
				+ ", 0, "
				+ "'~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', "
				+ "0, "
				+ rowNCMap.get(rowno + ":enablestate")
				+ ", "
				+ getStringValue(rowNCMap.get(rowno + ":innercode"))
				+ ", 'N',"
				+ getStringValue(rowNCMap.get(rowno + ":islastversion"))
				+ ", null, null, null, '~', "
				+ getStringValue(rowNCMap.get(rowno + ":name"))
				+ ", "
				+ (rowNCMap.containsKey(rowno + ":name2") ? getStringValue(rowNCMap.get(rowno + ":name2"))
						: getStringValue(rowNCMap.get(rowno + ":name")))
				+ ", "
				+ (rowNCMap.containsKey(rowno + ":name3") ? getStringValue(rowNCMap.get(rowno + ":name3"))
						: getStringValue(rowNCMap.get(rowno + ":glbdef5")))
				+ ", null, null, null, '~', null, "
				+ "'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', "
				+ "'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', "
				+ "'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', "
				+ "'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', "
				+ "'N', 'N', "
				+ getStringValue(rowNCMap.get(rowno + ":pk_fatherorg"))
				+ ", '~', "
				+ getStringValue(rowNCMap.get(rowno + ":pk_group"))
				+ ", "
				+ newDeptPK
				+ ", "
				+ getStringValue(rowNCMap.get(rowno + ":pk_org"))
				+ ", '~', "
				+ newDeptVersionPK
				+ ", '~', "
				+ "null, null, null, null, null, null, null, '9999-12-31 23:59:59', "
				+ "'初始版本', null, null, null, null, null, "
				+ (isNewVersion(rowNCMap, rowno)
						&& !StringUtils.isEmpty(((String) rowNCMap.get(rowno + ":createdate"))) ? "'"
						+ getDateString((String) rowNCMap.get(rowno + ":createdate")).replace("/", "-").substring(0, 7)
								.replace("-", "") + "'" : (!StringUtils.isEmpty((String) rowNCMap.get(rowno
						+ ":createdate")) ? getDateString((String) rowNCMap.get(rowno + ":createdate"))
						.replace("/", "").replace("-", "").substring(0, 4) : "000000"))
				+ ", "
				+ ((getDateString((String) rowNCMap.get(rowno + ":createdate")) == null ? "null" : "'"
						+ getDateString((String) rowNCMap.get(rowno + ":createdate")) + "'"))
				+ ", '~', '~', null, "
				+ "null, '~', '~', '" + rowNCMap.get(rowno + ":pk_org") + "', '~', '~', null, '~');");
		return sqls;
	}

	private List<String> getExtNewOrgSQL(Map<String, Object> rowNCMap, String rowno) {
		List<String> sqls = new ArrayList<String>();
		sqls.add("INSERT INTO ORG_ORGS "
				+ "(ADDRESS, CODE, COUNTRYZONE, CREATIONTIME, CREATOR, DATAORIGINFLAG, "
				+ "DEF1, DEF10, DEF11, DEF12, DEF13, DEF14, DEF15, DEF16, DEF17, DEF18, DEF19, DEF2, DEF20, DEF3, DEF4, DEF5, DEF6, DEF7, DEF8, DEF9, "
				+ "DR, ENABLESTATE, INNERCODE, ISBUSINESSUNIT, ISLASTVERSION, MEMO, MNECODE, MODIFIEDTIME, MODIFIER, "
				+ "NAME, NAME2, NAME3, NAME4, NAME5, NAME6, NCINDUSTRY, ORGANIZATIONCODE, "
				+ "ORGTYPE1, ORGTYPE10, ORGTYPE11, ORGTYPE12, ORGTYPE13, ORGTYPE14, ORGTYPE15, ORGTYPE16, ORGTYPE17, ORGTYPE18, ORGTYPE19, ORGTYPE2, "
				+ "ORGTYPE20, ORGTYPE21, ORGTYPE22, ORGTYPE23, ORGTYPE24, ORGTYPE25, ORGTYPE26, ORGTYPE27, ORGTYPE28, ORGTYPE29, ORGTYPE3, ORGTYPE30, "
				+ "ORGTYPE31, ORGTYPE32, ORGTYPE33, ORGTYPE34, ORGTYPE35, ORGTYPE36, ORGTYPE37, ORGTYPE38, ORGTYPE39, ORGTYPE4, ORGTYPE40, ORGTYPE41, "
				+ "ORGTYPE42, ORGTYPE43, ORGTYPE44, ORGTYPE45, ORGTYPE46, ORGTYPE47, ORGTYPE48, ORGTYPE49, ORGTYPE5, ORGTYPE50, ORGTYPE6, ORGTYPE7, "
				+ "ORGTYPE8, ORGTYPE9, PK_FATHERORG, PK_FORMAT, PK_GROUP, PK_ORG, PK_OWNORG, PK_TIMEZONE, PK_VID, PRINCIPAL, "
				+ "SHORTNAME, SHORTNAME2, SHORTNAME3, SHORTNAME4, SHORTNAME5, SHORTNAME6, TEL, VENDDATE, "
				+ "VNAME, VNAME2, VNAME3, VNAME4, VNAME5, VNAME6, VNO, VSTARTDATE, CHARGELEADER, ENTITYTYPE, ISBALANCEUNIT, "
				+ "ISRETAIL, PK_ACCPERIODSCHEME, PK_CONTROLAREA, PK_CORP, PK_CURRTYPE, PK_EXRATESCHEME, REPORTCONFIRM, WORKCALENDAR) "
				+ "VALUES " + "(null, '"
				+ rowNCMap.get(rowno + ":code")
				+ "', '~', "
				+ getStringValue(rowNCMap.get(rowno + ":creationtime"))
				+ ", "
				+ getStringValue(rowNCMap.get(rowno + ":creator"))
				+ ", 0, "
				+ "'~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', "
				+ "0, "
				+ rowNCMap.get(rowno + ":enablestate")
				+ ", "
				+ getStringValue(rowNCMap.get(rowno + ":innercode"))
				+ ", 'N', "
				+ getStringValue(rowNCMap.get(rowno + ":islastversion"))
				+ ", null, null, null, '~', "
				+ getStringValue(rowNCMap.get(rowno + ":name"))
				+ ", "
				+ (rowNCMap.containsKey(rowno + ":name2") ? getStringValue(rowNCMap.get(rowno + ":name2"))
						: getStringValue(rowNCMap.get(rowno + ":name")))
				+ ", "
				+ (rowNCMap.containsKey(rowno + ":name3") ? getStringValue(rowNCMap.get(rowno + ":name3"))
						: getStringValue(rowNCMap.get(rowno + ":glbdef5")))
				+ ", null, null, null, '~', null, "
				+ "'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', "
				+ "'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', "
				+ "'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', "
				+ "'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', "
				+ "'N', 'N', '"
				+ rowNCMap.get(rowno + ":pk_fatherorg")
				+ "', '~', '"
				+ rowNCMap.get(rowno + ":pk_group")
				+ "', "
				+ newDeptPK
				+ ", '"
				+ rowNCMap.get(rowno + ":pk_org")
				+ "', '~', "
				+ newDeptVersionPK
				+ ", '~', null, null, null, null, null, null, null, '9999-12-31 23:59:59', "
				+ "'初始版本', null, null, null, null, null, "
				+ (isNewVersion(rowNCMap, rowno)
						&& !StringUtils.isEmpty(((String) rowNCMap.get(rowno + ":createdate"))) ? "'"
						+ getDateString((String) rowNCMap.get(rowno + ":createdate")).replace("/", "-").substring(0, 7)
								.replace("-", "") + "'" : (!StringUtils.isEmpty((String) rowNCMap.get(rowno
						+ ":createdate")) ? getDateString((String) rowNCMap.get(rowno + ":createdate"))
						.replace("/", "").replace("-", "").substring(0, 4) : "000000"))
				+ ", "
				+ ((getDateString((String) rowNCMap.get(rowno + ":createdate")) == null ? "null" : "'"
						+ getDateString((String) rowNCMap.get(rowno + ":createdate")) + "'"))
				+ ", '~', '~', null, "
				+ "null, '~', '~', '" + rowNCMap.get(rowno + ":pk_org") + "', '~', '~', null, '~');");
		return sqls;
	}

	private List<String> getExtNewDeptVersionSQL(Map<String, Object> rowNCMap, String rowno) {
		List<String> sqls = new ArrayList<String>();
		sqls.add("INSERT INTO ORG_DEPT_V "
				+ "(ADDRESS, CODE, CREATEDATE, CREATIONTIME, CREATOR, DATAORIGINFLAG, "
				+ "DEF1, DEF10, DEF11, DEF12, DEF13, DEF14, DEF15, DEF16, DEF17, DEF18, DEF19, DEF2, DEF20, DEF3, DEF4, DEF5, DEF6, DEF7, DEF8, DEF9, "
				+ "DEPTCANCELDATE, DEPTTYPE, DISPLAYORDER, DR, ENABLESTATE, HRCANCELED, INNERCODE, ISLASTVERSION, "
				+ "ISRETAIL, MEMO, MNECODE, MODIFIEDTIME, MODIFIER, NAME, NAME2, NAME3, NAME4, NAME5, NAME6, "
				+ "PK_DEPT, PK_FATHERORG, PK_GROUP, PK_ORG, PK_VID, PRINCIPAL, RESPOSITION, "
				+ "SHORTNAME, SHORTNAME2, SHORTNAME3, SHORTNAME4, SHORTNAME5, SHORTNAME6, "
				+ "TEL, VENDDATE, VNAME, VNAME2, VNAME3, VNAME4, VNAME5, VNAME6, VNO, VSTARTDATE, "
				+ "CHARGELEADER, DEPTLEVEL, ORGTYPE13, ORGTYPE17, DEPTDUTY) " + "VALUES " + "('~',"
				+ getStringValue(rowNCMap.get(rowno + ":code"))
				+ ", "
				+ (getDateString((String) rowNCMap.get(rowno + ":createdate")) == null ? "null" : "'"
						+ getDateString((String) rowNCMap.get(rowno + ":createdate")) + "'")
				+ ", "
				+ getStringValue(rowNCMap.get(rowno + ":creationtime"))
				+ ", "
				+ getStringValue(rowNCMap.get(rowno + ":creator"))
				+ ", 0, "
				+ "null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "
				+ "null, 0, 999999, 0, "
				+ rowNCMap.get(rowno + ":enablestate")
				+ ", "
				+ getStringValue(rowNCMap.get(rowno + ":hrcanceled"))
				+ ", "
				+ getStringValue(rowNCMap.get(rowno + ":innercode"))
				+ ", null, "
				+ "null, null, null, null, '~', "
				+ getStringValue(rowNCMap.get(rowno + ":name"))
				+ ", "
				+ (rowNCMap.containsKey(rowno + ":name2") ? getStringValue(rowNCMap.get(rowno + ":name2"))
						: getStringValue(rowNCMap.get(rowno + ":name")))
				+ ", "
				+ (rowNCMap.containsKey(rowno + ":name3") ? getStringValue(rowNCMap.get(rowno + ":name3"))
						: getStringValue(rowNCMap.get(rowno + ":glbdef5")))
				+ ", null, null, null, "
				+ newDeptPK
				+ ", "
				+ getStringValue(rowNCMap.get(rowno + ":pk_fatherorg"))
				+ ", "
				+ getStringValue(rowNCMap.get(rowno + ":pk_group"))
				+ ", "
				+ getStringValue(rowNCMap.get(rowno + ":pk_org"))
				+ ", "
				+ newDeptVersionPK
				+ ", '~', '~', "
				+ "null, null, null, null, null, null, "
				+ "null, '9999-12-31 23:59:59', '初始版本', null, null, null, null, null, "
				+ (isNewVersion(rowNCMap, rowno)
						&& !StringUtils.isEmpty(((String) rowNCMap.get(rowno + ":createdate"))) ? "'"
						+ getDateString((String) rowNCMap.get(rowno + ":createdate")).replace("/", "-").substring(0, 7)
								.replace("-", "") + "'" : (!StringUtils.isEmpty((String) rowNCMap.get(rowno
						+ ":createdate")) ? getDateString((String) rowNCMap.get(rowno + ":createdate"))
						.replace("/", "").replace("-", "").substring(0, 6) : "000000"))
				+ ", "
				+ ((getDateString((String) rowNCMap.get(rowno + ":createdate")) == null ? "null" : "'"
						+ getDateString((String) rowNCMap.get(rowno + ":createdate")) + "'"))
				+ ", "
				+ "null, '~', 'Y', null, null);");
		return sqls;
	}

	private void getNewIDByCode(String deptCode, String effectDate) throws BusinessException {
		String strSQL = "select pk_dept, pk_vid from org_dept where code = '" + deptCode + "' and islastversion='Y'";
		Map<String, Object> pks = (Map<String, Object>) this.getBaseDAO().executeQuery(strSQL, new MapProcessor());
		if (pks != null && pks.size() > 0) {
			newDeptPK = "'" + String.valueOf(pks.get("pk_dept")) + "'";
			newDeptVersionPK = "'" + String.valueOf(pks.get("pk_vid")) + "'";
			isNew = false;
		}
	}

	private List<String> getExtDeptChangeSQL(Map<String, Object> rowNCMap, String rowno) throws BusinessException {
		List<String> sqls = new ArrayList<String>();
		if (deptOrgHistoryExists((String) rowNCMap.get(rowno + ":code"))) {
			sqls.add("UPDATE OM_DEPTHISTORY SET EFFECTDATE="
					+ getDateString(rowNCMap, rowno)
					+ ", ISRECEIVED='N', NAME='"
					+ rowNCMap.get(rowno + ":name")
					+ "', name2="
					+ (rowNCMap.containsKey(rowno + ":name2") ? getStringValue(rowNCMap.get(rowno + ":name2"))
							: getStringValue(rowNCMap.get(rowno + ":name")))
					+ ", name3="
					+ (rowNCMap.containsKey(rowno + ":name3") ? getStringValue(rowNCMap.get(rowno + ":name3"))
							: getStringValue(rowNCMap.get(rowno + ":glbdef5")))
					+ ", pk_dept=(select pk_dept from org_dept where islastversion='Y' and code = '"
					+ rowNCMap.get(rowno + ":code")
					+ "') WHERE changetype='1' and pk_dept = (select pk_dept from org_dept where code = '"
					+ rowNCMap.get(rowno + ":code") + "')");
		} else {
			sqls.add("INSERT INTO OM_DEPTHISTORY "
					+ "(CHANGENUM, CHANGETYPE, CODE, DEPTLEVEL, DR, EFFECTDATE, ISRECEIVED, MEMO, "
					+ "NAME, NAME2, NAME3, NAME4, NAME5, NAME6, "
					+ "PK_DEPT, PK_DEPTHISTORY, PK_ORG, PK_ORG_V, PRINCIPAL) "
					+ "VALUES "
					+ "('"
					+ OidGenerator.getInstance().nextOid()
					+ "', '1', '"
					+ (String) rowNCMap.get(rowno + ":code")
					+ "', '~', 0, "
					+ getDateString(rowNCMap, rowno)
					+ ", 'N', null, "
					+ getStringValue(rowNCMap.get(rowno + ":name"))
					+ ", "
					+ (rowNCMap.containsKey(rowno + ":name2") ? getStringValue(rowNCMap.get(rowno + ":name2"))
							: getStringValue(rowNCMap.get(rowno + ":name")))
					+ ", "
					+ (rowNCMap.containsKey(rowno + ":name3") ? getStringValue(rowNCMap.get(rowno + ":name3"))
							: getStringValue(rowNCMap.get(rowno + ":glbdef5"))) + ", null, null, null, " + newDeptPK
					+ ", '" + OidGenerator.getInstance().nextOid() + "', '" + rowNCMap.get(rowno + ":pk_org")
					+ "', (select pk_vid from org_orgs where pk_org='" + rowNCMap.get(rowno + ":pk_org")
					+ "' and islastversion='Y'), '~');");
		}
		return sqls;
	}

	private String getDateString(Map<String, Object> rowNCMap, String rowno) {
		String dateString = (String) rowNCMap.get(rowno + ":createdate");

		if (!StringUtils.isEmpty(dateString)) {
			dateString = "'" + getDateString(((String) rowNCMap.get(rowno + ":createdate"))) + "'";
		} else {
			dateString = (!StringUtils.isEmpty((String) rowNCMap.get(rowno + ":createdate")) ? getDateString((String) rowNCMap
					.get(rowno + ":createdate")) : "0001-01-01");
		}

		return dateString;
	}

	private boolean deptOrgHistoryExists(String code) throws BusinessException {
		String strSQL = "select count(*) from om_depthistory where pk_dept = (select pk_dept from org_dept where code = '"
				+ code + "' and islastversion='Y')";
		int count = (int) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		return count > 0;
	}

	private List<String> getExtNewPrincipleSQL(Map<String, Object> rowNCMap, String rowno) throws BusinessException {
		List<String> sqls = new ArrayList<String>();
		if (deptOrgExists((String) rowNCMap.get(rowno + ":code"))) {
			// 部門主管
			String deptcode = (String) rowNCMap.get(rowno + ":code");
			String psncode = rowNCMap.get(rowno + ":principal") == null ? null : (String) rowNCMap.get(rowno
					+ ":principal");
			if (psncode != null && !principleExists(deptcode, psncode)) {
				// newPrincipleID = this.getNewPrincipleID(newPrincipleID);
				sqls.add("INSERT INTO org_orgmanager "
						+ "(cuserid, dataoriginflag, dr, pk_group, pk_org, pk_orgmanager, pk_dept, pk_psndoc, pk_psnjob, "
						+ "principalflag, viewotherdirector, viewprincipal) "
						+ "select "
						+ "(select usr.pk_psndoc from sm_user usr inner join bd_psndoc psn on usr.pk_psndoc = psn.pk_psndoc where psn.code = '"
						+ psncode + "'), 0, 0, (select pk_group from org_dept where code = '" + deptcode
						+ "'), (select pk_org from org_dept where code = '" + deptcode + "'), '"
						+ OidGenerator.getInstance().nextOid() + "', (select pk_dept from org_dept where code = '"
						+ deptcode + "'), (select pk_psndoc from bd_psndoc where code = '" + psncode + "'), null, "
						+ "'Y', null, null "
						+ " from dual where (select count(pk_psndoc) from bd_psndoc where code = '" + psncode
						+ "') > 0;");
			}

			// 部門副主管
			if (!principleExists((String) rowNCMap.get(rowno + ":code"), (String) rowNCMap.get(rowno + ":principal"))) {
				// sqls.add("");
			}
		}

		return sqls;
	}

	private boolean principleExists(String deptcode, String psncode) throws BusinessException {
		String strSQL = "select count(*) from org_orgmanager mng inner join org_dept dept on dept.pk_dept = mng.pk_dept inner join bd_psndoc psn on psn.pk_psndoc = mng.pk_psndoc where dept.code = '"
				+ deptcode + "' and psn.code = '" + psncode + "';";
		int count = (int) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		return count > 0;
	}

	private boolean isCanceled(Map<String, Object> rowNCMap, String rowno) {
		if (StringUtils.isEmpty((String) rowNCMap.get(rowno + ":deptcanceldate"))
				|| ((String) rowNCMap.get(rowno + ":deptcanceldate")).toLowerCase().equals("null")) {
			return false;
		}

		return true;
	}

	private List<String> getExtDeptVersionSQL(Map<String, Object> rowNCMap, String rowno) throws BusinessException {
		List<String> sqls = new ArrayList<String>();
		if (deptDeptVersionExists((String) rowNCMap.get(rowno + ":code"),
				getDateString((String) rowNCMap.get(rowno + ":createdate")))) {
			sqls.add("UPDATE ORG_DEPT_V "
					// set
					+ " SET MODIFIEDTIME='"
					+ (new UFDateTime()).toString()
					+ "', modifier='"
					+ this.getCuserid()
					+ "'"
					+ (rowNCMap.containsKey(rowno + ":innercode") ? (", INNERCODE=" + getStringValue(rowNCMap.get(rowno
							+ ":innercode"))) : "")
					+ (rowNCMap.containsKey(rowno + ":deptcanceldate") ? (", DEPTCANCELDATE=" + getStringValue(rowNCMap
							.get(rowno + ":deptcanceldate"))) : "")
					+ (rowNCMap.containsKey(rowno + ":name") ? (", NAME=" + getStringValue(rowNCMap
							.get(rowno + ":name"))) : "")
					+ (rowNCMap.containsKey(rowno + ":name2") ? (", NAME2=" + getStringValue(rowNCMap.get(rowno
							+ ":name2"))) : ", NAME2=" + getStringValue(rowNCMap.get(rowno + ":name")))
					+ (rowNCMap.containsKey(rowno + ":name3") ? (", NAME3=" + getStringValue(rowNCMap.get(rowno
							+ ":name3"))) : ", NAME3=" + getStringValue(rowNCMap.get(rowno + ":glbdef5")))
					+ (rowNCMap.containsKey(rowno + ":pk_fatherorg") ? ", PK_FATHERORG="
							+ getStringValue(rowNCMap.get(rowno + ":pk_fatherorg")) : "")
					+ (rowNCMap.containsKey(rowno + ":enablestate") ? (", ENABLESTATE=" + getStringValue(rowNCMap
							.get(rowno + ":enablestate"))) : "")
					+ (rowNCMap.containsKey(rowno + ":createdate") ? (", VSTARTDATE=" + (getDateString((String) rowNCMap
							.get(rowno + ":createdate")) == null ? "null" : "'"
							+ getDateString((String) rowNCMap.get(rowno + ":createdate")) + "'")) : "") + ", CREATOR="
					+ getStringValue(rowNCMap.get(rowno + ":creator")) + ", CREATIONTIME="
					+ getStringValue(rowNCMap.get(rowno + ":creationtime"))
					// where
					+ " WHERE code = '" + rowNCMap.get(rowno + ":code") + "'");
		} else {
			sqls.add("INSERT INTO ORG_DEPT_V "
					+ "(ADDRESS, CODE, CREATEDATE, CREATIONTIME, CREATOR, DATAORIGINFLAG, "
					+ "DEF1, DEF10, DEF11, DEF12, DEF13, DEF14, DEF15, DEF16, DEF17, DEF18, DEF19, DEF2, DEF20, DEF3, DEF4, DEF5, DEF6, DEF7, DEF8, DEF9, "
					+ "DEPTCANCELDATE, DEPTTYPE, DISPLAYORDER, DR, ENABLESTATE, HRCANCELED, INNERCODE, ISLASTVERSION, "
					+ "ISRETAIL, MEMO, MNECODE, MODIFIEDTIME, MODIFIER, NAME, NAME2, NAME3, NAME4, NAME5, NAME6, "
					+ "PK_DEPT, PK_FATHERORG, PK_GROUP, PK_ORG, PK_VID, PRINCIPAL, RESPOSITION, "
					+ "SHORTNAME, SHORTNAME2, SHORTNAME3, SHORTNAME4, SHORTNAME5, SHORTNAME6, "
					+ "TEL, VENDDATE, VNAME, VNAME2, VNAME3, VNAME4, VNAME5, VNAME6, VNO, VSTARTDATE, "
					+ "CHARGELEADER, DEPTLEVEL, ORGTYPE13, ORGTYPE17, DEPTDUTY) " + "VALUES " + "('~',"
					+ getStringValue(rowNCMap.get(rowno + ":code"))
					+ ", "
					+ (getDateString((String) rowNCMap.get(rowno + ":createdate")) == null ? "null" : "'"
							+ getDateString((String) rowNCMap.get(rowno + ":createdate")) + "'")
					+ ", "
					+ getStringValue(rowNCMap.get(rowno + ":creationtime"))
					+ ", "
					+ getStringValue(rowNCMap.get(rowno + ":creator"))
					+ ", 0, "
					+ "null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "
					+ "null, 0, 999999, 0, "
					+ rowNCMap.get(rowno + ":enablestate")
					+ ", "
					+ getStringValue(rowNCMap.get(rowno + ":hrcanceled"))
					+ ", "
					+ getStringValue(rowNCMap.get(rowno + ":innercode"))
					+ ", null, "
					+ "null, null, null, null, '~', "
					+ getStringValue(rowNCMap.get(rowno + ":name"))
					+ ", "
					+ (rowNCMap.containsKey(rowno + ":name2") ? getStringValue(rowNCMap.get(rowno + ":name2"))
							: getStringValue(rowNCMap.get(rowno + ":name")))
					+ ", "
					+ (rowNCMap.containsKey(rowno + ":name3") ? getStringValue(rowNCMap.get(rowno + ":name3"))
							: getStringValue(rowNCMap.get(rowno + ":glbdef5")))
					+ ", null, null, null, "
					+ newDeptPK
					+ ", "
					+ getStringValue(rowNCMap.get(rowno + ":pk_fatherorg"))
					+ ", "
					+ getStringValue(rowNCMap.get(rowno + ":pk_group"))
					+ ", "
					+ getStringValue(rowNCMap.get(rowno + ":pk_org"))
					+ ", "
					+ newDeptVersionPK
					+ ", '~', '~', "
					+ "null, null, null, null, null, null, "
					+ "null, '9999-12-31 23:59:59', '初始版本', null, null, null, null, null, "
					+ (isNewVersion(rowNCMap, rowno)
							&& !StringUtils.isEmpty(((String) rowNCMap.get(rowno + ":createdate"))) ? "'"
							+ getDateString((String) rowNCMap.get(rowno + ":createdate")).replace("/", "-")
									.substring(0, 7).replace("-", "") + "'" : (!StringUtils.isEmpty((String) rowNCMap
							.get(rowno + ":createdate")) ? getDateString((String) rowNCMap.get(rowno + ":createdate"))
							.replace("/", "").replace("-", "").substring(0, 6) : "000000"))
					+ ", "
					+ ((getDateString((String) rowNCMap.get(rowno + ":createdate")) == null ? "null" : "'"
							+ getDateString((String) rowNCMap.get(rowno + ":createdate")) + "'"))
					+ ", "
					+ "null, '~', 'Y', null, null);");
		}

		return sqls;
	}

	private boolean deptDeptVersionExists(String code, String effectDate) throws BusinessException {
		String strSQL = "select count(*) from org_dept_v where code = '" + code.trim() + "' and createdate='"
				+ effectDate + "'";
		Integer count = (Integer) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		return count > 0;
	}

	private List<String> getExtOrgSQL(Map<String, Object> rowNCMap, String rowno) throws BusinessException {
		List<String> sqls = new ArrayList<String>();

		if (deptOrgExists((String) rowNCMap.get(rowno + ":code"))) {
			// org_orgs
			sqls.add("UPDATE ORG_ORGS "
					// set
					+ " SET MODIFIEDTIME='"
					+ (new UFDateTime()).toString()
					+ "', modifier='"
					+ this.getCuserid()
					+ "'"
					+ (rowNCMap.containsKey(rowno + ":innercode") ? (", INNERCODE=" + getStringValue(rowNCMap.get(rowno
							+ ":innercode"))) : "")
					+ (rowNCMap.containsKey(rowno + ":name") ? (", NAME=" + getStringValue(rowNCMap
							.get(rowno + ":name"))) : "")
					+ (rowNCMap.containsKey(rowno + ":name2") ? (", NAME2=" + getStringValue(rowNCMap.get(rowno
							+ ":name2"))) : ", NAME2=" + getStringValue(rowNCMap.get(rowno + ":name")))
					+ (rowNCMap.containsKey(rowno + ":name3") ? (", NAME3=" + getStringValue(rowNCMap.get(rowno
							+ ":name3"))) : ", NAME3=" + getStringValue(rowNCMap.get(rowno + ":glbdef5")))
					+ (rowNCMap.containsKey(rowno + ":pk_fatherorg") ? ", PK_FATHERORG="
							+ getStringValue(rowNCMap.get(rowno + ":pk_fatherorg")) : "")
					+ (rowNCMap.containsKey(rowno + ":enablestate") ? (", ENABLESTATE='"
							+ String.valueOf(rowNCMap.get(rowno + ":enablestate")) + "'") : "")
					+ (rowNCMap.containsKey(rowno + ":createdate") ? (", VSTARTDATE=" + ((getDateString((String) rowNCMap
							.get(rowno + ":createdate")) == null ? "null" : "'"
							+ getDateString((String) rowNCMap.get(rowno + ":createdate")) + "'"))) : "") + ", CREATOR="
					+ getStringValue(rowNCMap.get(rowno + ":creator")) + ", CREATIONTIME="
					+ getStringValue(rowNCMap.get(rowno + ":creationtime"))
					// where
					+ " WHERE code = '" + rowNCMap.get(rowno + ":code") + "'");
			// org_orgs_v
			sqls.add("UPDATE ORG_ORGS_V "
					// set
					+ "SET MODIFIEDTIME='"
					+ (new UFDateTime()).toString()
					+ "', modifier='"
					+ this.getCuserid()
					+ "'"
					+ (rowNCMap.containsKey(rowno + ":innercode") ? (", INNERCODE=" + getStringValue(rowNCMap.get(rowno
							+ ":innercode"))) : "")
					+ (rowNCMap.containsKey(rowno + ":name") ? (", NAME=" + getStringValue(rowNCMap
							.get(rowno + ":name"))) : "")
					+ (rowNCMap.containsKey(rowno + ":name2") ? (", NAME2=" + getStringValue(rowNCMap.get(rowno
							+ ":name2"))) : ", NAME2=" + getStringValue(rowNCMap.get(rowno + ":name")))
					+ (rowNCMap.containsKey(rowno + ":name3") ? (", NAME3=" + getStringValue(rowNCMap.get(rowno
							+ ":name3"))) : ", NAME3=" + getStringValue(rowNCMap.get(rowno + ":glbdef5")))
					+ (rowNCMap.containsKey(rowno + ":pk_fatherorg") ? ", PK_FATHERORG="
							+ getStringValue(rowNCMap.get(rowno + ":pk_fatherorg")) : "")
					+ (rowNCMap.containsKey(rowno + ":enablestate") ? (", ENABLESTATE='"
							+ String.valueOf(rowNCMap.get(rowno + ":enablestate")) + "'") : "")
					+ (rowNCMap.containsKey(rowno + ":createdate") ? (", VSTARTDATE=" + ((getDateString((String) rowNCMap
							.get(rowno + ":createdate")) == null ? "null" : "'"
							+ getDateString((String) rowNCMap.get(rowno + ":createdate")) + "'"))) : "") + ", CREATOR="
					+ getStringValue(rowNCMap.get(rowno + ":creator")) + ", CREATIONTIME="
					+ getStringValue(rowNCMap.get(rowno + ":creationtime"))
					// where
					+ " WHERE code = '" + rowNCMap.get(rowno + ":code") + "'");
		} else {
			// org_orgs
			sqls.add("INSERT INTO ORG_ORGS "
					+ "(ADDRESS, CODE, COUNTRYZONE, CREATIONTIME, CREATOR, DATAORIGINFLAG, "
					+ "DEF1, DEF10, DEF11, DEF12, DEF13, DEF14, DEF15, DEF16, DEF17, DEF18, DEF19, DEF2, DEF20, DEF3, DEF4, DEF5, DEF6, DEF7, DEF8, DEF9, "
					+ "DR, ENABLESTATE, INNERCODE, ISBUSINESSUNIT, ISLASTVERSION, MEMO, MNECODE, MODIFIEDTIME, MODIFIER, "
					+ "NAME, NAME2, NAME3, NAME4, NAME5, NAME6, NCINDUSTRY, ORGANIZATIONCODE, "
					+ "ORGTYPE1, ORGTYPE10, ORGTYPE11, ORGTYPE12, ORGTYPE13, ORGTYPE14, ORGTYPE15, ORGTYPE16, ORGTYPE17, ORGTYPE18, ORGTYPE19, ORGTYPE2, "
					+ "ORGTYPE20, ORGTYPE21, ORGTYPE22, ORGTYPE23, ORGTYPE24, ORGTYPE25, ORGTYPE26, ORGTYPE27, ORGTYPE28, ORGTYPE29, ORGTYPE3, ORGTYPE30, "
					+ "ORGTYPE31, ORGTYPE32, ORGTYPE33, ORGTYPE34, ORGTYPE35, ORGTYPE36, ORGTYPE37, ORGTYPE38, ORGTYPE39, ORGTYPE4, ORGTYPE40, ORGTYPE41, "
					+ "ORGTYPE42, ORGTYPE43, ORGTYPE44, ORGTYPE45, ORGTYPE46, ORGTYPE47, ORGTYPE48, ORGTYPE49, ORGTYPE5, ORGTYPE50, ORGTYPE6, ORGTYPE7, "
					+ "ORGTYPE8, ORGTYPE9, PK_FATHERORG, PK_FORMAT, PK_GROUP, PK_ORG, PK_OWNORG, PK_TIMEZONE, PK_VID, PRINCIPAL, "
					+ "SHORTNAME, SHORTNAME2, SHORTNAME3, SHORTNAME4, SHORTNAME5, SHORTNAME6, TEL, VENDDATE, "
					+ "VNAME, VNAME2, VNAME3, VNAME4, VNAME5, VNAME6, VNO, VSTARTDATE, CHARGELEADER, ENTITYTYPE, ISBALANCEUNIT, "
					+ "ISRETAIL, PK_ACCPERIODSCHEME, PK_CONTROLAREA, PK_CORP, PK_CURRTYPE, PK_EXRATESCHEME, REPORTCONFIRM, WORKCALENDAR) "
					+ "VALUES " + "(null, '"
					+ rowNCMap.get(rowno + ":code")
					+ "', '~', "
					+ getStringValue(rowNCMap.get(rowno + ":creationtime"))
					+ ", "
					+ getStringValue(rowNCMap.get(rowno + ":creator"))
					+ ", 0, "
					+ "'~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', "
					+ "0, "
					+ rowNCMap.get(rowno + ":enablestate")
					+ ", "
					+ getStringValue(rowNCMap.get(rowno + ":innercode"))
					+ ", 'N', "
					+ getStringValue(rowNCMap.get(rowno + ":islastversion"))
					+ ", null, null, null, '~', "
					+ getStringValue(rowNCMap.get(rowno + ":name"))
					+ ", "
					+ (rowNCMap.containsKey(rowno + ":name2") ? getStringValue(rowNCMap.get(rowno + ":name2"))
							: getStringValue(rowNCMap.get(rowno + ":name")))
					+ ", "
					+ (rowNCMap.containsKey(rowno + ":name3") ? getStringValue(rowNCMap.get(rowno + ":name3"))
							: getStringValue(rowNCMap.get(rowno + ":glbdef5")))
					+ ", null, null, null, '~', null, "
					+ "'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', "
					+ "'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', "
					+ "'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', "
					+ "'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', "
					+ "'N', 'N', '"
					+ rowNCMap.get(rowno + ":pk_fatherorg")
					+ "', '~', '"
					+ rowNCMap.get(rowno + ":pk_group")
					+ "', "
					+ newDeptPK
					+ ", '"
					+ rowNCMap.get(rowno + ":pk_org")
					+ "', '~', "
					+ newDeptVersionPK
					+ ", '~', null, null, null, null, null, null, null, '9999-12-31 23:59:59', "
					+ "'初始版本', null, null, null, null, null, "
					+ (isNewVersion(rowNCMap, rowno)
							&& !StringUtils.isEmpty(((String) rowNCMap.get(rowno + ":createdate"))) ? "'"
							+ getDateString((String) rowNCMap.get(rowno + ":createdate")).replace("/", "-")
									.substring(0, 7).replace("-", "") + "'" : (!StringUtils.isEmpty((String) rowNCMap
							.get(rowno + ":createdate")) ? getDateString((String) rowNCMap.get(rowno + ":createdate"))
							.replace("/", "").replace("-", "").substring(0, 4) : "000000"))
					+ ", "
					+ ((getDateString((String) rowNCMap.get(rowno + ":createdate")) == null ? "null" : "'"
							+ getDateString((String) rowNCMap.get(rowno + ":createdate")) + "'"))
					+ ", '~', '~', null, "
					+ "null, '~', '~', '"
					+ rowNCMap.get(rowno + ":pk_org")
					+ "', '~', '~', null, '~');");

			// org_orgs_v
			sqls.add("INSERT INTO ORG_ORGS_V "
					+ "(ADDRESS, CODE, COUNTRYZONE, CREATIONTIME, CREATOR, DATAORIGINFLAG, "
					+ "DEF1, DEF10, DEF11, DEF12, DEF13, DEF14, DEF15, DEF16, DEF17, DEF18, DEF19, DEF2, DEF20, DEF3, DEF4, DEF5, DEF6, DEF7, DEF8, DEF9, "
					+ "DR, ENABLESTATE, INNERCODE, ISBUSINESSUNIT, ISLASTVERSION, MEMO, MNECODE, MODIFIEDTIME, MODIFIER, "
					+ "NAME, NAME2, NAME3, NAME4, NAME5, NAME6, NCINDUSTRY, ORGANIZATIONCODE, "
					+ "ORGTYPE1, ORGTYPE10, ORGTYPE11, ORGTYPE12, ORGTYPE13, ORGTYPE14, ORGTYPE15, ORGTYPE16, ORGTYPE17, ORGTYPE18, ORGTYPE19, ORGTYPE2, "
					+ "ORGTYPE20, ORGTYPE21, ORGTYPE22, ORGTYPE23, ORGTYPE24, ORGTYPE25, ORGTYPE26, ORGTYPE27, ORGTYPE28, ORGTYPE29, ORGTYPE3, ORGTYPE30, "
					+ "ORGTYPE31, ORGTYPE32, ORGTYPE33, ORGTYPE34, ORGTYPE35, ORGTYPE36, ORGTYPE37, ORGTYPE38, ORGTYPE39, ORGTYPE4, ORGTYPE40, ORGTYPE41, "
					+ "ORGTYPE42, ORGTYPE43, ORGTYPE44, ORGTYPE45, ORGTYPE46, ORGTYPE47, ORGTYPE48, ORGTYPE49, ORGTYPE5, ORGTYPE50, ORGTYPE6, ORGTYPE7, "
					+ "ORGTYPE8, ORGTYPE9, PK_FATHERORG, PK_FORMAT, PK_GROUP, PK_ORG, PK_OWNORG, PK_TIMEZONE, PK_VID, PRINCIPAL, "
					+ "SHORTNAME, SHORTNAME2, SHORTNAME3, SHORTNAME4, SHORTNAME5, SHORTNAME6, TEL, VENDDATE, "
					+ "VNAME, VNAME2, VNAME3, VNAME4, VNAME5, VNAME6, VNO, VSTARTDATE, CHARGELEADER, ENTITYTYPE, ISBALANCEUNIT, "
					+ "ISRETAIL, PK_ACCPERIODSCHEME, PK_CONTROLAREA, PK_CORP, PK_CURRTYPE, PK_EXRATESCHEME, REPORTCONFIRM, WORKCALENDAR) "
					+ "VALUES " + "(null, "
					+ getStringValue(rowNCMap.get(rowno + ":code"))
					+ ", '~', "
					+ getStringValue(rowNCMap.get(rowno + ":creationtime"))
					+ ", "
					+ getStringValue(rowNCMap.get(rowno + ":creator"))
					+ ", 0, "
					+ "'~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', "
					+ "0, "
					+ rowNCMap.get(rowno + ":enablestate")
					+ ", "
					+ getStringValue(rowNCMap.get(rowno + ":innercode"))
					+ ", 'N',"
					+ getStringValue(rowNCMap.get(rowno + ":islastversion"))
					+ ", null, null, null, '~', "
					+ getStringValue(rowNCMap.get(rowno + ":name"))
					+ ", "
					+ (rowNCMap.containsKey(rowno + ":name2") ? getStringValue(rowNCMap.get(rowno + ":name2"))
							: getStringValue(rowNCMap.get(rowno + ":name")))
					+ ", "
					+ (rowNCMap.containsKey(rowno + ":name3") ? getStringValue(rowNCMap.get(rowno + ":name3"))
							: getStringValue(rowNCMap.get(rowno + ":glbdef5")))
					+ ", null, null, null, '~', null, "
					+ "'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', "
					+ "'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', "
					+ "'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', "
					+ "'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', "
					+ "'N', 'N', "
					+ getStringValue(rowNCMap.get(rowno + ":pk_fatherorg"))
					+ ", '~', "
					+ getStringValue(rowNCMap.get(rowno + ":pk_group"))
					+ ", "
					+ newDeptPK
					+ ", "
					+ getStringValue(rowNCMap.get(rowno + ":pk_org"))
					+ ", '~', "
					+ newDeptVersionPK
					+ ", '~', "
					+ "null, null, null, null, null, null, null, '9999-12-31 23:59:59', "
					+ "'初始版本', null, null, null, null, null, "
					+ (isNewVersion(rowNCMap, rowno)
							&& !StringUtils.isEmpty(((String) rowNCMap.get(rowno + ":createdate"))) ? "'"
							+ getDateString((String) rowNCMap.get(rowno + ":createdate")).replace("/", "-")
									.substring(0, 7).replace("-", "") + "'" : (!StringUtils.isEmpty((String) rowNCMap
							.get(rowno + ":createdate")) ? getDateString((String) rowNCMap.get(rowno + ":createdate"))
							.replace("/", "").replace("-", "").substring(0, 4) : "000000"))
					+ ", "
					+ ((getDateString((String) rowNCMap.get(rowno + ":createdate")) == null ? "null" : "'"
							+ getDateString((String) rowNCMap.get(rowno + ":createdate")) + "'"))
					+ ", '~', '~', null, "
					+ "null, '~', '~', '"
					+ rowNCMap.get(rowno + ":pk_org")
					+ "', '~', '~', null, '~');");
		}
		return sqls;
	}

	private boolean deptOrgExists(String code) throws BusinessException {
		String strSQL = "select count(*) from org_orgs where code = '" + code.trim() + "'";
		Integer count = (Integer) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		return count > 0;
	}

	private boolean isNewVersion(Map<String, Object> rowNCMap, String rowno) {
		return true;
	}

	private String getInnerCode(String code, String pk_fatherorg) throws BusinessException {
		String innercode = "";
		String rdnStr = "";

		// 取父部门InnerCode
		if (!StringUtils.isEmpty(pk_fatherorg)) {
			innercode = (String) this.getBaseDAO().executeQuery(
					"select innercode from org_dept where pk_dept='" + pk_fatherorg + "'", new ColumnProcessor());
		}

		rdnStr = getRandomString(4);
		if (StringUtils.isEmpty(innercode)) {
			// 无父部门
			innercode = rdnStr;
		} else {
			innercode += rdnStr;
		}

		String strSQL = "select count(*) from org_dept where innercode = '" + innercode + "'";
		int count = (int) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());

		// 如果存在InnerCode，则递归生成
		if (count > 0) {
			innercode = getInnerCode(code, pk_fatherorg);
		}
		return innercode;
	}

	@Override
	public void afterUpdate() throws BusinessException {
		// String strSQL =
		// "update om_depthistory set effectdate = (select createdate from org_dept where pk_dept = om_depthistory.pk_dept) where changetype = 1;";
		String strSQL = "update om_depthistory set effectdate = null where changetype = 1;";
		this.getSaveService().executeQueryWithNoCMT(new String[] { strSQL });
	}

	@Override
	public void afterConvert() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public String getBizEntityID() {
		// 必須賦值，否則不會加載本類型
		// 實體ID為md_class的ID欄位
		// md_class.name = hrdept
		return "58115861-ce2b-44e8-b881-80119db7bf84";
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
	public void beforeInsertOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自动生成的方法存根
	}

	@Override
	public void beforeUpdateOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void doUpdateByBP() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void doQueryByBP() throws BusinessException {
		// TODO 自動產生的方法 Stub

	}
}
