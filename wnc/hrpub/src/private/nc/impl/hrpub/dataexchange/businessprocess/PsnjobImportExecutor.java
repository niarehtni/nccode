package nc.impl.hrpub.dataexchange.businessprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.uap.oid.OidGenerator;
import nc.impl.hrpub.dataexchange.DataImportExecutor;
import nc.itf.hrpub.IDataExchangeExternalExecutor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;

import org.apache.commons.lang.StringUtils;

public class PsnjobImportExecutor extends DataImportExecutor implements IDataExchangeExternalExecutor {
	private Map<String, String> psnIDPKMap;
	private Map<String, String> psnOrgPKMap;
	private Map<String, String> rowDeptCodeMap;
	private Map<String, String> rowPsnCodeMap;
	private Map<String, Map<UFDate, String>> psnOrgMap;
	private List<String> notNullProperties = new ArrayList<String>();
	private List<String> needReorder;
	String newPK = "'$NEWID$'";

	public PsnjobImportExecutor() throws BusinessException {
		super();

		this.notNullProperties.add("pk_psndoc");
		this.setActionOnDataExists(ExecuteActionEnum.UPDATE);
	}

	@Override
	public Object getBizEntityID() throws BusinessException {
		// 必須賦值，否則不會加載本類型
		// 實體ID為md_class的ID欄位
		// md_class.name = hi_psnjob
		return "7156d223-4531-4337-b192-492ab40098f1";
	}

	@Override
	public void beforeUpdate() throws BusinessException {
		// 員工工作記錄 hi_psnjob
		// 增加额外的唯一性检查条件，基類在插入數據之前，增加該條件的檢查，以確保業務主鍵不重複
		this.setUniqueCheckExtraCondition("'$begindate$' between begindate and isnull(enddate, '9999-12-31') and trnsevent=$trnsevent$");
		// 增加排他的唯一性检查条件，基類在插入數據之前，用此條件替換檢查條件，不再以基類的按Code轉PK加額外條件檢查
		this.setUniqueCheckExclusiveCondition(" pk_psndoc='$pk_psndoc$' and " + this.getUniqueCheckExtraCondition());

		if (this.getNcValueObjects() != null && this.getNcValueObjects().size() > 0) {
			String rowNo = "";
			for (Map<String, Object> rowNCMap : this.getNcValueObjects()) {
				try {
					rowNo = rowNCMap.keySet().toArray(new String[0])[0].split(":")[0];

					// ASSGID
					rowNCMap.put(rowNo + ":assgid", 1);
					// ISMAINJOB
					rowNCMap.put(rowNo + ":ismainjob", "Y");
					// LASTFLAG
					rowNCMap.put(rowNo + ":lastflag", "N");
					// PSNTYPE (0=員工)
					rowNCMap.put(rowNo + ":psntype", 0);
					// RECORDNUM
					rowNCMap.put(rowNo + ":recordnum", 0);
					// SHOWORDER
					rowNCMap.put(rowNo + ":showorder", 9999999);

					String code = this.getRowPsnCodeMap().get(rowNo);

					// 工號
					String clerkcode = (String) rowNCMap.get(rowNo + ":" + "clerkcode");
					if (StringUtils.isEmpty(clerkcode)) {
						throw new BusinessException("员工号不能为空。");
					}

					String id = (String) rowNCMap.get(rowNo + ":" + this.getReservedPropertyName("IDNO"));
					// pk_psndoc
					String pk_psndoc = "";
					if (this.getPsnIDPKMap().containsKey(id) && this.getPsnIDPKMap().get(id) != null) {
						pk_psndoc = this.getPsnIDPKMap().get(id);
					} else {
						pk_psndoc = this.getPsndocByCode(code);

						if (StringUtils.isEmpty(pk_psndoc) || "~".equals(pk_psndoc)) {
							throw new BusinessException("找不到身份證件號碼 [" + id + "] 對應的員工");
						} else {
							this.getPsnIDPKMap().put(id, pk_psndoc);
						}
					}

					rowNCMap.put(rowNo + ":pk_psndoc", pk_psndoc);

					// 組織集團
					String orgCode = (String) rowNCMap.get(rowNo + ":" + this.getReservedPropertyName("NCOMNO"));
					String pk_org = getPk_Org(orgCode);
					String pk_org_v = getPk_org_vid(pk_org);
					rowNCMap.put(rowNo + ":pk_org", pk_org);
					rowNCMap.put(rowNo + ":pk_hrorg", pk_org);
					rowNCMap.put(rowNo + ":pk_group", this.getPk_group());
					rowNCMap.put(rowNo + ":pk_hrgroup", this.getPk_group());
					rowNCMap.put(rowNo + ":pk_org_v", pk_org_v);

					// 異動事件
					String trnsReasonCode = (String) rowNCMap.get(rowNo + ":" + this.getReservedPropertyName("ETSNO"));
					String trnsEventCode = (String) rowNCMap.get(rowNo + ":"
							+ this.getReservedPropertyName("TRNSEVENT"));
					int trnsevent = 0;
					if (!StringUtils.isEmpty(trnsEventCode)) {
						trnsevent = Integer.valueOf(trnsEventCode);
					} else {
						if (trnsReasonCode.equals("Z01")) {
							trnsevent = 1;
						} else {
							throw new BusinessException("无法找到当前人员的异动事件");
						}
					}
					rowNCMap.put(rowNo + ":trnsevent", trnsevent);

					// 生效日期
					String effectDate = getDateString((String) rowNCMap.get(rowNo + ":"
							+ this.getReservedPropertyName("CRDATE")));
					// 離職記錄開始日期+1
					if (trnsevent == 4) {
						effectDate = new UFDate(effectDate).getDateAfter(1).toString();
					}
					rowNCMap.put(rowNo + ":begindate", effectDate.substring(0, 10));

					// 部門Code
					String deptCode = this.getRowDeptCodeMap().get(rowNo);

					// 部門
					String pk_dept = getPk_dept(deptCode, effectDate);
					String pk_dept_v = getPk_dept_v(pk_dept, effectDate);

					if (StringUtils.isEmpty(pk_dept)) {
						pk_dept = getPk_dept("A99999", "9999-12-31");
						pk_dept_v = getPk_dept_v(pk_dept, "9999-12-31");
					}
					rowNCMap.put(rowNo + ":pk_dept", pk_dept);
					rowNCMap.put(rowNo + ":pk_dept_v", pk_dept_v);

					// 異動原因
					String trnsreason = trnsevent == 4 ? getTransReason("J00") : (getTransReason(trnsReasonCode));
					rowNCMap.put(rowNo + ":trnsreason", trnsreason);

					String pk_psnjob = getPsnjobByEvent(pk_psndoc, trnsevent, effectDate);
					if (!StringUtils.isEmpty(pk_psnjob)) {
						rowNCMap.put(rowNo + ":pk_psnjob", pk_psnjob);
					}

					// 時間戳
					String ts = getDateString(new UFDateTime().toString());

					if (!this.getExtendSQLs().containsKey(rowNo)) {
						this.getExtendSQLs().put(rowNo, new ArrayList<String>());
					}

					// 組織關係
					this.getExtendSQLs().get(rowNo)
							.addAll(dealPsnorg(rowNo, pk_psndoc, pk_org, effectDate, trnsevent, ts));
					rowNCMap.put(rowNo + ":pk_psnorg", this.getPsnOrgPKMap().get(rowNo));

					// 職等
					if (this.isPropertyChanged(rowNCMap, rowNo, this.getReservedPropertyName("OCLASS1"),
							this.getReservedPropertyName("NCLASS1"))) {
						String pk_jobrank = getPk_jobrank((String) rowNCMap.get(rowNo + ":"
								+ this.getReservedPropertyName("NCLASS1")));
						rowNCMap.put(rowNo + ":pk_jobrank", pk_jobrank);
					}

					// 人員類別
					String pk_psncl = getPk_psncl((String) rowNCMap.get(rowNo + ":"
							+ this.getReservedPropertyName("NPEONO")));
					rowNCMap.put(rowNo + ":pk_psncl", pk_psncl);

					// 班次
					if (this.isPropertyChanged(rowNCMap, rowNo, this.getReservedPropertyName("OCLSNO"),
							this.getReservedPropertyName("NCLSNO"))) {
						String pk_shift = getPk_shift(pk_org,
								(String) rowNCMap.get(rowNo + ":" + this.getReservedPropertyName("NCLSNO")));
						rowNCMap.put(rowNo + ":jobglbdef1", pk_shift);
					}

					// 職稱
					if (this.isPropertyChanged(rowNCMap, rowNo, this.getReservedPropertyName("OTILNO"),
							this.getReservedPropertyName("NTILNO"))) {
						String pk_post = getPk_post((String) rowNCMap.get(rowNo + ":"
								+ this.getReservedPropertyName("NTILNO")));
						rowNCMap.put(rowNo + ":jobglbdef4", pk_post);
					}

					// 工作地點
					if (this.isPropertyChanged(rowNCMap, rowNo, this.getReservedPropertyName("OPLACENO"),
							this.getReservedPropertyName("NPLACENO"))) {
						String pk_post = getPk_place((String) rowNCMap.get(rowNo + ":"
								+ this.getReservedPropertyName("NPLACENO")));
						rowNCMap.put(rowNo + ":jobglbdef5", pk_post);
					}

					// 在岗
					rowNCMap.put(rowNo + ":poststat", "N");

					// 试用
					rowNCMap.put(rowNo + ":trial_flag", "N");

					// 備註
					String memo = (String) rowNCMap.get(rowNo + ":" + this.getReservedPropertyName("REMARK"));
					if (!StringUtils.isEmpty(memo)) {
						rowNCMap.put(rowNo + ":memo", memo);
					}

					this.getExtendSQLs()
							.get(rowNo)
							.addAll(this
									.dealPsnwork(pk_psnjob, pk_psndoc, pk_dept_v, effectDate, pk_org, trnsevent, ts));

					if (!this.getNeedReorder().contains(pk_psndoc)) {
						this.getNeedReorder().add(pk_psndoc);
					}
				} catch (Exception e) {
					this.getErrorMessages().put(rowNo, e.getMessage());
				}
			}
		}
	}

	private String getPsnjobByEvent(String pk_psndoc, int trnsevent, String effectDate) throws BusinessException {
		String strSQL = "select pk_psnjob from hi_psnjob where pk_psndoc=" + getStringValue(pk_psndoc)
				+ " and trnsevent=" + String.valueOf(trnsevent) + " and " + getStringValue(effectDate)
				+ " between begindate and isnull(enddate, '9999-12-31');";
		String pk_psnjob = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());

		return pk_psnjob;
	}

	private List<String> dealPsnwork(String pk_psnjob, String pk_psndoc, String pk_dept_v, String effectDate,
			String pk_org, int trnsevent, String ts) throws BusinessException {
		List<String> sqls = new ArrayList<String>();
		String pk_psndoc_sub = null;

		if (!StringUtils.isEmpty(pk_psnjob)) {
			String strSQL = "select pk_psndoc_sub from hi_psndoc_work where pk_psnjob=" + getStringValue(pk_psnjob);

			pk_psndoc_sub = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		}

		if (StringUtils.isEmpty(pk_psndoc_sub)) {
			sqls.add("INSERT INTO HI_PSNDOC_WORK (" + "BEGINDATE, BG_CHECK, CERTIFIER, CERTIPHONE, CREATIONTIME, "
					+ "CREATOR, DIMISSION_REASON, DR, ENDDATE, LASTFLAG, LINKPHONE, MEMO, MODIFIEDTIME, MODIFIER, "
					+ "PK_GROUP, PK_ORG, PK_PSNDOC, PK_PSNDOC_SUB, PK_PSNJOB, PK_PSNORG, RECORDNUM, TS, "
					+ "WORK_ADDR, WORKACHIVE, WORKCORP, WORKDEPT, WORKDUTY, WORKJOB, WORKPOST) " + "VALUES ("
					// BEGINDATE, BG_CHECK, CERTIFIER, CERTIPHONE, CREATIONTIME
					+ getStringValue(effectDate.substring(0, 10))
					+ ", null, null, '~', "
					+ getStringValue(ts)
					+ ", "
					// CREATOR, DIMISSION_REASON, DR, ENDDATE, LASTFLAG,
					// LINKPHONE, MEMO, MODIFIEDTIME, MODIFIER
					+ getStringValue(this.getCuserid())
					+ ", null, 0, "
					+ (trnsevent == 4 ? getStringValue(effectDate.substring(0, 10)) : "null")
					+ ", 'N', null, null, null, '~', "
					// PK_GROUP, PK_ORG, PK_PSNDOC, PK_PSNDOC_SUB, PK_PSNJOB,
					// PK_PSNORG, RECORDNUM, TS
					+ getStringValue(this.getPk_group())
					+ ", "
					+ getStringValue(pk_org)
					+ ", "
					+ getStringValue(pk_psndoc)
					+ ", "
					+ getStringValue(OidGenerator.getInstance().nextOid())
					+ ", "
					+ (StringUtils.isEmpty(pk_psnjob) ? newPK : getStringValue(pk_psnjob))
					+ ", '~', 0, '2018-01-22 20:24:44', "
					// WORK_ADDR, WORKACHIVE, WORKCORP, WORKDEPT, WORKDUTY,
					// WORKJOB, WORKPOST
					+ "null, null, (select name from org_orgs where pk_org="
					+ getStringValue(pk_org)
					+ "), (select name from org_dept_v where pk_vid="
					+ getStringValue(pk_dept_v)
					+ "), null, null, null);");
		} else {
			if (trnsevent == 4) {
				sqls.add("UPDATE HI_PSNDOC_WORK SET ENDDATE=" + getStringValue(effectDate.substring(0, 10))
						+ " WHERE PK_PSNDOC_SUB=" + getStringValue(pk_psndoc_sub));
			} else {
				sqls.add("UPDATE HI_PSNDOC_WORK SET BEGINDATE=" + getStringValue(effectDate.substring(0, 10))
						+ " WHERE PK_PSNDOC_SUB=" + getStringValue(pk_psndoc_sub) + " AND BEGINDATE > "
						+ getStringValue(effectDate.substring(0, 10)));
			}
		}

		return sqls;
	}

	private String getPk_place(String placeCode) throws BusinessException {
		String key = getCacheKey("PLACE", this.getPk_group(), placeCode);
		String strSQL = "select pk_defdoc from bd_defdoc where pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'WNC02') and code = "
				+ getStringValue(placeCode) + " and pk_org = " + getStringValue(this.getPk_group());
		return getKeyValue(key, strSQL);
	}

	private String getPsndocByCode(String psnCode) throws BusinessException {
		if (StringUtils.isEmpty(psnCode)) {
			return null;
		}

		String key = getCacheKey("PSNDOC", "NULL", psnCode);
		String strSQL = "select pk_psndoc from bd_psndoc where code = " + getStringValue(psnCode);
		return getKeyValue(key, strSQL);
	}

	private String getTransReason(String transReason) throws BusinessException {
		if (StringUtils.isEmpty(transReason)) {
			return null;
		}

		String key = getCacheKey("TRANSREASON", this.getPk_group(), transReason);
		String strSQL = "select pk_defdoc from bd_defdoc dd inner join bd_defdoclist dl on dl.pk_defdoclist = dd.pk_defdoclist where dl.code = 'HRHI001_0xx' and dd.code = "
				+ getStringValue(transReason);
		return getKeyValue(key, strSQL);
	}

	private String getPk_Org(String orgCode) throws BusinessException {
		String key = getCacheKey("ORG", "NULL", orgCode);
		String strSQL = "select pk_org from org_orgs where code=" + getStringValue(orgCode);
		return getKeyValue(key, strSQL);
	}

	private String getPk_org_vid(String pk_org) throws BusinessException {
		String key = getCacheKey("ORGV", "NULL", pk_org);
		String strSQL = "select pk_vid from org_orgs where pk_org=" + getStringValue(pk_org);
		return getKeyValue(key, strSQL);
	}

	private String getPk_jobrank(String jobGradeCode) throws BusinessException {
		String key = getCacheKey("JOBRANK", this.getPk_group(), jobGradeCode);
		String strSQL = "select pk_jobrank from om_jobrank where pk_org = " + getStringValue(this.getPk_group())
				+ " and jobrankcode= " + getStringValue(jobGradeCode);
		return getKeyValue(key, strSQL);
	}

	private String getPk_psncl(String psnclCode) throws BusinessException {
		String key = getCacheKey("PSNCL", "NULL", psnclCode);
		String strSQL = "select pk_psncl from bd_psncl where code=" + getStringValue(psnclCode);
		return getKeyValue(key, strSQL);
	}

	private String getPk_shift(String pk_org, String shiftCode) throws BusinessException {
		String key = getCacheKey("SHIFT", pk_org, shiftCode);
		String strSQL = "select pk_shift from bd_shift where code = " + getStringValue(shiftCode) + " and pk_org="
				+ getStringValue(pk_org);
		return getKeyValue(key, strSQL);
	}

	private String getPk_post(String postCode) throws BusinessException {
		String key = getCacheKey("POST", this.getPk_group(), postCode);
		String strSQL = "select pk_post from om_post where postcode = " + getStringValue(postCode) + " and pk_org="
				+ getStringValue(this.getPk_group());
		return getKeyValue(key, strSQL);
	}

	private String getPk_dept_v(String pk_dept, String effectDate) throws BusinessException {
		String strSQL = "select pk_vid from org_dept_v where pk_dept = " + getStringValue(pk_dept) + " and "
				+ getStringValue(effectDate) + " between vstartdate and venddate";

		String pk_vid = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		return pk_vid;
	}

	private String getPk_dept(String deptCode, String effectDate) throws BusinessException {
		String strSQL = "select pk_dept from om_depthistory where code = " + getStringValue(deptCode)
				+ " and changetype=1 and effectdate<=" + getStringValue(effectDate) + ";";
		String pk_dept = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());

		if (StringUtils.isEmpty(pk_dept)) {
			strSQL = "select pk_dept from om_depthistory where code = " + getStringValue(deptCode)
					+ " and effectdate<=" + getStringValue(effectDate) + ";";
			pk_dept = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		}

		if (StringUtils.isEmpty(pk_dept)) {
			strSQL = "select pk_dept from org_dept where code = " + getStringValue(deptCode) + " and createdate<="
					+ getStringValue(effectDate) + ";";
			pk_dept = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		}
		return pk_dept;
	}

	private List<String> dealPsnorg(String rowNo, String pk_psndoc, String pk_org, String effectDate, int trnsevent,
			String ts) throws BusinessException {
		List<String> sqls = new ArrayList<String>();

		String strSQL = "select pk_psnorg from hi_psnorg where pk_psndoc = " + getStringValue(pk_psndoc)
				+ " and pk_org = " + getStringValue(pk_org) + " and begindate <= "
				+ getStringValue(effectDate.substring(0, 10));
		String pk_psnorg = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());

		if (StringUtils.isEmpty(pk_psnorg)) {
			if (this.getPsnOrgMap().containsKey(pk_psndoc)) {
				for (Entry<UFDate, String> psnorg : this.getPsnOrgMap().get(pk_psndoc).entrySet()) {
					if (psnorg.getKey().isSameDate(new UFDate(effectDate))
							|| psnorg.getKey().before(new UFDate(effectDate))) {
						pk_psnorg = psnorg.getValue();
					}
				}
			}
		}

		if (StringUtils.isEmpty(pk_psnorg)) {
			pk_psnorg = OidGenerator.getInstance().nextOid();

			if (!this.getPsnOrgMap().containsKey(pk_psndoc)) {
				this.getPsnOrgMap().put(pk_psndoc, new HashMap<UFDate, String>());
			}

			this.getPsnOrgMap().get(pk_psndoc).put(new UFDate(effectDate), pk_psnorg);
			this.getPsnOrgPKMap().put(rowNo, pk_psnorg);

			// hi_psnorg
			sqls.add("INSERT INTO HI_PSNORG ("
					+ "ADJUSTCORPAGE, BEGINDATE, CREATIONTIME, CREATOR, DR, EMPFORMS, ENDDATE, "
					+ "ENDFLAG, INDOC_SOURCE, INDOCFLAG, JOINSYSDATE, LASTFLAG, MODIFIEDTIME, "
					+ "MODIFIER, ORGRELAID, PK_GROUP, PK_HRORG, PK_ORG, PK_PSNDOC, PK_PSNORG, "
					+ "PSNTYPE, STARTPAYDATE, STOPPAYDATE, TS, CORPWORKAGE, ORGGLBDEF1) " + "VALUES ("
					// ADJUSTCORPAGE, BEGINDATE, CREATIONTIME, CREATOR, DR,
					// EMPFORMS, ENDDATE
					+ "null, "
					+ getStringValue(effectDate.substring(0, 10))
					+ ", "
					+ getStringValue(ts)
					+ ", "
					+ getStringValue(this.getCuserid())
					+ ", 0, "
					+ getStringValue("~")
					+ ", "
					+ "null"
					+ ", "
					// ENDFLAG, INDOC_SOURCE, INDOCFLAG, JOINSYSDATE, LASTFLAG,
					// MODIFIEDTIME
					+ "'Y', null, 'Y', "
					+ getStringValue(effectDate.substring(0, 10))
					+ ", 'Y', null, "
					// MODIFIER, ORGRELAID, PK_GROUP, PK_HRORG, PK_ORG,
					// PK_PSNDOC,
					// PK_PSNORG
					+ "'~', 1, "
					+ getStringValue(this.getPk_group())
					+ ", "
					+ getStringValue(pk_org)
					+ ", "
					+ getStringValue(pk_org)
					+ ", "
					+ getStringValue(pk_psndoc)
					+ ", "
					+ getStringValue(pk_psnorg)
					+ ", "
					// PSNTYPE, STARTPAYDATE, STOPPAYDATE, TS, CORPWORKAGE,
					// ORGGLBDEF1
					+ "0, "
					+ getStringValue(effectDate.substring(0, 10))
					+ ", "
					+ "null"
					+ ", "
					+ getStringValue(ts)
					+ ", '0', '~');");
		} else {
			sqls.add("UPDATE HI_PSNORG SET BEGINDATE=" + getStringValue(effectDate.substring(0, 10))
					+ " WHERE PK_PSNORG=" + getStringValue(pk_psnorg) + " AND BEGINDATE > "
					+ getStringValue(effectDate));
			this.getPsnOrgPKMap().put(rowNo, pk_psnorg);

			if (trnsevent == 4) {
				// 离职更新组织关系
				sqls.add("update hi_psnorg set enddate=" + getStringValue(effectDate.substring(0, 10))
						+ " where pk_psnorg=" + getStringValue(pk_psnorg));
			}
		}

		return sqls;
	}

	@Override
	public void afterUpdate() throws BusinessException {
		if (this.getNeedReorder() != null && this.getNeedReorder().size() > 0) {
			for (String pk_psndoc : this.getNeedReorder()) {
				reOrderPsnJobs(pk_psndoc);
			}
		}
	}

	private void reOrderPsnJobs(String pk_psndoc) throws BusinessException {
		String strSQL = "select * from hi_psnjob where pk_psndoc=" + getStringValue(pk_psndoc)
				+ " and dr=0 order by begindate desc;";
		List<Map<String, Object>> psnjobs = (List<Map<String, Object>>) this.getBaseDAO().executeQuery(strSQL,
				new MapListProcessor());

		if (psnjobs != null && psnjobs.size() > 0) {
			List<String> sqls = new ArrayList<String>();
			for (int i = 0; i < psnjobs.size(); i++) {
				String pk_psnjob = (String) psnjobs.get(i).get("pk_psnjob");
				int trnsevent = (Integer) psnjobs.get(i).get("trnsevent");
				UFDate enddate = null;
				strSQL = "update hi_psnjob set recordnum= " + String.valueOf(i);

				if (trnsevent != 4) {
					strSQL += ", endflag='N'";
				} else {
					strSQL += ", endflag='Y'";
				}

				if (i < psnjobs.size() - 1) {
					// 離職不更新Enddate
					enddate = new UFDate((String) psnjobs.get(i + 1).get("begindate")).getDateBefore(1);
					strSQL += ", enddate=" + getStringValue(enddate.toString().substring(0, 10));
				} else {
					strSQL += ", lastflag='Y'";
				}
				strSQL += " where pk_psnjob=" + getStringValue(pk_psnjob);
				sqls.add(strSQL);
			}

			this.getSaveService().executeQueryWithNoCMT(sqls.toArray(new String[0]));
		}
	}

	@Override
	public void afterConvert() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void beforeConvert() throws BusinessException {
		if (this.getJsonValueObjects() != null && this.getJsonValueObjects().size() > 0) {
			for (Map<String, Object> jsonobj : this.getJsonValueObjects()) {
				String deptCode = "";
				String oldDeptCode = "";
				String rowNo = "";
				String errMsg = "";
				String psnCode = "";
				for (Entry<String, Object> entry : jsonobj.entrySet()) {
					jsonobj.put(entry.getKey(), ((String) entry.getValue()).trim());
					if (entry.getKey().contains("IDNO")) {
						String id = (String) entry.getValue();
						String pk_psndoc = getPKByID(id);
						if (StringUtils.isEmpty(pk_psndoc)) {
							errMsg = "根據身份證件號碼 [" + id + "] 無法找到對應的員工";
						}
					} else if (entry.getKey().contains("NDEPNO")) {
						deptCode = (String) entry.getValue();
					} else if (entry.getKey().contains("ODEPNO")) {
						oldDeptCode = (String) entry.getValue();
					} else if (entry.getKey().contains("ROWNO")) {
						rowNo = (String) entry.getValue();
					} else if (entry.getKey().contains("EMPNO")) {
						psnCode = (String) entry.getValue();
					}
				}

				this.getRowDeptCodeMap().put(rowNo, deptCode);
				this.getRowPsnCodeMap().put(rowNo, psnCode);

				if (!StringUtils.isEmpty(errMsg)) {
					this.getErrorMessages().put(rowNo, errMsg);
				}
			}
		}
	}

	private String getPKByID(String id) throws BusinessException {
		String pk_psndoc = "";
		if (this.getPsnIDPKMap().containsKey(id)) {
			pk_psndoc = this.getPsnIDPKMap().get(id);
		} else {
			String strSQL = "select pk_psndoc from bd_psndoc where id = " + getStringValue(id);
			pk_psndoc = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
			this.getPsnIDPKMap().put(id, pk_psndoc);
		}
		return pk_psndoc;
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
		for (String checkKey : this.notNullProperties) {
			if (!rowMap.containsKey(checkKey) || StringUtils.isEmpty((String) rowMap.get(checkKey.toLowerCase()))
					|| "~".equals(rowMap.get(checkKey.toLowerCase()))) {
				throw new BusinessException("非空校驗錯誤：新增記錄時 [" + checkKey + "] 不能為空。");
			}
		}
	}

	@Override
	public void beforeUpdateOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自动生成的方法存根

	}

	public Map<String, String> getPsnOrgPKMap() {
		if (psnOrgPKMap == null) {
			psnOrgPKMap = new HashMap<String, String>();
		}
		return psnOrgPKMap;
	}

	public void setPsnOrgPKMap(Map<String, String> psnOrgPKMap) {
		this.psnOrgPKMap = psnOrgPKMap;
	}

	public Map<String, String> getPsnIDPKMap() {
		if (psnIDPKMap == null) {
			psnIDPKMap = new HashMap<String, String>();
		}
		return psnIDPKMap;
	}

	public void setPsnIDPKMap(Map<String, String> psnCodePKMap) {
		this.psnIDPKMap = psnCodePKMap;
	}

	public Map<String, String> getRowDeptCodeMap() {
		if (rowDeptCodeMap == null) {
			rowDeptCodeMap = new HashMap<String, String>();
		}
		return rowDeptCodeMap;
	}

	public void setRowDeptCodeMap(Map<String, String> rowDeptCodeMap) {
		this.rowDeptCodeMap = rowDeptCodeMap;
	}

	public Map<String, String> getRowPsnCodeMap() {
		if (rowPsnCodeMap == null) {
			rowPsnCodeMap = new HashMap<String, String>();
		}
		return rowPsnCodeMap;
	}

	public void setRowPsnCodeMap(Map<String, String> rowPsnCodeMap) {
		this.rowPsnCodeMap = rowPsnCodeMap;
	}

	public Map<String, Map<UFDate, String>> getPsnOrgMap() {
		if (psnOrgMap == null) {
			psnOrgMap = new HashMap<String, Map<UFDate, String>>();
		}
		return psnOrgMap;
	}

	public void setPsnOrgMap(Map<String, Map<UFDate, String>> psnOrgMap) {
		this.psnOrgMap = psnOrgMap;
	}

	public List<String> getNeedReorder() {
		if (needReorder == null) {
			needReorder = new ArrayList<String>();
		}
		return needReorder;
	}

	public void setNeedReorder(List<String> needReorder) {
		this.needReorder = needReorder;
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
