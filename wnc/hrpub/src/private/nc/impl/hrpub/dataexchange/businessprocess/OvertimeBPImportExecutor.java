package nc.impl.hrpub.dataexchange.businessprocess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.impl.hrpub.dataexchange.DataImportExecutor;
import nc.itf.hrpub.IDataExchangeExternalExecutor;
import nc.itf.ta.IOvertimeApplyQueryMaintain;
import nc.itf.ta.IOvertimeRegisterManageMaintain;
import nc.itf.ta.IOvertimeRegisterQueryMaintain;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.overtime.OvertimeCommonVO;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.util.remotecallcombination.IRemoteCallCombinatorService;
import nc.vo.util.remotecallcombination.RemoteCallInfo;
import nc.vo.util.remotecallcombination.RemoteCallResult;

import org.apache.commons.lang.StringUtils;

public class OvertimeBPImportExecutor extends DataImportExecutor implements IDataExchangeExternalExecutor {
	private Map<String, OvertimeRegVO> rowNCVO;
	private Map<String, String> rowOTType;

	public OvertimeBPImportExecutor() throws BusinessException {
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

					OvertimeRegVO vo = new OvertimeRegVO();
					vo.setBillsource(0);
					vo.setCreationtime(new UFDateTime());
					vo.setCreator(this.getCuserid());
					vo.setDeduct(new UFDouble((String) rowNCMap.get(rowNo + ":deduct")).intValue());

					String otType = this.getTimeItemByCode(this.getRowOTType().get(rowNo));
					if (StringUtils.isEmpty(otType.replace("~", ""))) {
						throw new BusinessException("沒有找到有效的加班類別");
					} else {
						vo.setPk_overtimetype(otType);
					}
					String otTypeCopy = this.getTimeItemCopyByOrg(otType, this.getPk_org());
					vo.setPk_overtimetypecopy(otTypeCopy);

					String pk_psndoc = (String) rowNCMap.get(rowNo + ":pk_psndoc");
					vo.setPk_psndoc(pk_psndoc);
					vo.setIstorest(UFBoolean.valueOf((String) rowNCMap.get(rowNo + ":istorest")));

					vo.setOvertimeremark(replaceBlank((String) rowNCMap.get(rowNo + ":overtimeremark")));
					vo.setIscheck(UFBoolean.FALSE);
					vo.setIsneedcheck(UFBoolean.TRUE);
					vo.setOvertimealready(UFDouble.ZERO_DBL);

					vo.setOvertimebegintime(new UFDateTime((String) rowNCMap.get(rowNo + ":overtimebegintime")));
					vo.setOvertimebegindate(new UFLiteralDate((String) rowNCMap.get(rowNo + ":overtimebegintime")));
					vo.setOvertimeendtime(new UFDateTime((String) rowNCMap.get(rowNo + ":overtimeendtime")));
					vo.setOvertimeenddate(new UFLiteralDate((String) rowNCMap.get(rowNo + ":overtimeendtime")));
					vo.setOvertimehour(new UFDouble((String) rowNCMap.get(rowNo + ":overtimehour")));

					// ssx added on 2020-02-16
					if (rowNCMap.get(rowNo + ":acthour") != null) {
						vo.setActhour(new UFDouble((String) rowNCMap.get(rowNo + ":acthour")));
					}
					// end

					vo.setPk_org(this.getPk_org());
					vo.setPk_org_v(this.getPk_org_v());
					vo.setPk_group(this.getPk_group());
					vo.setToresthour(UFDouble.ZERO_DBL);
					vo.setTs(new UFDateTime());

					// MOD(补审核时间) ssx modified for Mapped Approve time
					if (!StringUtils.isEmpty((String) rowNCMap.get(rowNo + ":approve_time"))) {
						vo.setApprove_time(new UFDateTime((String) rowNCMap.get(rowNo + ":approve_time")));
					}
					// 添加归属日期 Ares.Tank 2018-12-10 15:37:39
					if (!StringUtils.isEmpty((String) rowNCMap.get(rowNo + ":vestdate"))) {
						vo.setVestdate(new UFLiteralDate((String) rowNCMap.get(rowNo + ":vestdate")));
					}

					// 人员工作记录 PK_PSNJOB
					UFLiteralDate startdate = BillProcessHelper.getShiftRegDateByOvertime(vo);

					PsndocDismissedValidator dismChecker = new PsndocDismissedValidator(vo.getOvertimebegintime());
					dismChecker.validate(vo.getPk_psndoc(), startdate);

					Map<String, Object> psnjob = this.getPsnjob(pk_psndoc, startdate.toString());
					if (psnjob != null && psnjob.size() > 0 && !StringUtils.isEmpty((String) psnjob.get("pk_psnjob"))) {
						vo.setPk_psnjob((String) psnjob.get("pk_psnjob"));
						vo.setPk_dept_v((String) psnjob.get("pk_dept_v"));
						vo.setPk_psnorg((String) psnjob.get("pk_psnorg"));
					} else {
						throw new BusinessException("未找到員工工作記錄");
					}

					this.getRowNCVO().put(rowNo + ":" + vo.getApprove_time().toString(), vo);
				} catch (Exception e) {
					this.getErrorMessages().put(rowNo, e.getMessage());
				}
			}
		}
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
		if (this.getJsonValueObjects() != null && this.getJsonValueObjects().size() > 0) {
			for (Map<String, Object> jsonobj : this.getJsonValueObjects()) {
				String rowNo = "";
				for (Entry<String, Object> entry : jsonobj.entrySet()) {
					if (entry.getKey().equals("ROWNO")) {
						rowNo = (String) entry.getValue();
					} else if (entry.getKey().equals("DAYTYPE")) {
						this.getRowOTType().put(rowNo, (String) entry.getValue());
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
	public void doUpdateByBP() throws BusinessException {
		if (rowNCVO != null && rowNCVO.size() > 0) {
			List<String> arraylist = new ArrayList<String>();
			for (Entry<String, OvertimeRegVO> rowData : rowNCVO.entrySet()) {
				arraylist.add(rowData.getKey());
			}
			// 通过日期进行排序
			ListSort(arraylist);
			for (String s : arraylist) {
				// 校验单据，解决相同单据仍然能导入数据库的bug wangywt 20190603 begin
				try {
					// check(rowNCVO.get(s));
					duplicateCheck(rowNCVO.get(s));
				} catch (Exception bme) {
					this.getErrorMessages().put(s.split(":")[0], bme.getMessage().replace("\"", "'"));
					continue;
				}
				// 校验单据，解决相同单据仍然能导入数据库的bug wangywt 20190603 end
				try {
					this.getVOSaveService().insertData(rowNCVO.get(s));
				} catch (Exception e) {
					this.getErrorMessages().put(s.split(":")[0], e == null ? "未知錯誤" : e.getMessage());
				}
			}
		}
	}

	private void duplicateCheck(OvertimeRegVO overtimeRegVO) throws BusinessException {
		Object result = this.getBaseDAO().executeQuery(
				"select cast(sum(isnull(overtimehour,0)) as float) overtimehour  from tbm_overtimereg where pk_psndoc = '"
						+ overtimeRegVO.getPk_psndoc() + "' and overtimebegintime = '"
						+ overtimeRegVO.getOvertimebegintime().toString() + "' and overtimeendtime ='"
						+ overtimeRegVO.getOvertimeendtime().toString()
						+ "' group by pk_psndoc, overtimebegintime, overtimeendtime", new ColumnProcessor());

		UFDouble totalHour = result == null ? UFDouble.ZERO_DBL : new UFDouble(String.valueOf(result));

		if (totalHour != null && totalHour.floatValue() > 0.0 && overtimeRegVO.getOvertimehour().floatValue() > 0.0) {
			throw new BusinessException("加班時段發生重疊。");
		}
	}

	@Override
	public void beforeInsertOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void beforeUpdateOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自动生成的方法存根

	}

	IOvertimeRegisterManageMaintain saveService = null;

	private IOvertimeRegisterManageMaintain getVOSaveService() {
		if (saveService == null) {
			saveService = NCLocator.getInstance().lookup(IOvertimeRegisterManageMaintain.class);
		}
		return saveService;
	}

	public Map<String, OvertimeRegVO> getRowNCVO() {
		if (rowNCVO == null) {
			rowNCVO = new HashMap<String, OvertimeRegVO>();
		}
		return rowNCVO;
	}

	public void setRowNCVO(Map<String, OvertimeRegVO> rowNCVO) {
		this.rowNCVO = rowNCVO;
	}

	public static void ListSort(List<String> list) {
		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					Date dt1 = format.parse(o1.split(":")[1]);
					Date dt2 = format.parse(o2.split(":")[1]);
					if (dt1.getTime() > dt2.getTime()) {
						return 1;
					} else if (dt1.getTime() < dt2.getTime()) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
	}

	/**
	 * 校验单据的方法
	 * 
	 * @param regvo
	 * @return
	 * @throws BusinessException
	 * @throws BillMutexException
	 */
	protected Object check(OvertimeRegVO regvo) throws BusinessException, BillMutexException {

		if (regvo.getActhour().doubleValue() < 0.001D) {
			throw new BusinessException(ResHelper.getString("6017overtime", "06017overtime0062"));
		}
		List<RemoteCallInfo> remoteList = new ArrayList<RemoteCallInfo>();

		RemoteCallInfo checkRemote = new RemoteCallInfo();
		checkRemote.setClassName(IOvertimeRegisterQueryMaintain.class.getName());
		checkRemote.setMethodName("check");
		checkRemote.setParamTypes(new Class[] { SuperVO.class });
		checkRemote.setParams(new Object[] { regvo });
		remoteList.add(checkRemote);

		RemoteCallInfo checkLengthRemote = new RemoteCallInfo();
		checkLengthRemote.setClassName(IOvertimeApplyQueryMaintain.class.getName());
		checkLengthRemote.setMethodName("checkOvertimeLength");
		checkLengthRemote.setParamTypes(new Class[] { String.class, OvertimeCommonVO[].class });
		checkLengthRemote.setParams(new Object[] { regvo.getPk_org(), regvo });
		remoteList.add(checkLengthRemote);

		RemoteCallInfo checkFlag = new RemoteCallInfo();
		checkFlag.setClassName(IOvertimeApplyQueryMaintain.class.getName());
		checkFlag.setMethodName("checkIsNeed");
		checkFlag.setParamTypes(new Class[] { String.class, OvertimeCommonVO[].class });
		checkFlag.setParams(new Object[] { regvo.getPk_org(), regvo });
		remoteList.add(checkFlag);

		RemoteCallInfo checkHolidayRemote = new RemoteCallInfo();
		checkHolidayRemote.setClassName(IOvertimeApplyQueryMaintain.class.getName());
		checkHolidayRemote.setMethodName("checkOverTimeHoliday");
		checkHolidayRemote.setParamTypes(new Class[] { String.class, OvertimeCommonVO[].class });
		checkHolidayRemote.setParams(new Object[] { regvo.getPk_org(), regvo });
		remoteList.add(checkHolidayRemote);

		List<RemoteCallResult> returnList = ((IRemoteCallCombinatorService) NCLocator.getInstance().lookup(
				IRemoteCallCombinatorService.class)).doRemoteCall(remoteList);
		if (returnList.isEmpty())
			return null;
		RemoteCallResult[] returns = (RemoteCallResult[]) returnList.toArray(new RemoteCallResult[0]);
		String checkLength = (String) returns[1].getResult();
		Map<String, String[]> holidayMap = (Map) returns[3].getResult();
		if (!StringUtils.isBlank(checkLength)) {
		}
		String checkFlagReslut = (String) returns[2].getResult();
		if (!StringUtils.isBlank(checkFlagReslut)) {
		}
		if (holidayMap != null) {
			String psnNames = ((String[]) holidayMap.keySet().toArray(new String[0]))[0];
			String msg = ResHelper.getString("6017overtime", "06017overtime0053") + psnNames;
		}
		return returns[0].getResult();
	}

	public static String replaceBlank(String str) {
		if (str != null) {
			str = str.replaceAll("\\\\r|\\\\n", "");
		}
		return str;
	}

	@Override
	public void doQueryByBP() throws BusinessException {
		// TODO 自動產生的方法 Stub

	}
}
