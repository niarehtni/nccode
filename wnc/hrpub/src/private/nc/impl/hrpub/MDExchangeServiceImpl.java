package nc.impl.hrpub;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.bs.logging.Logger;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.pa.PreAlertReturnType;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.impl.hrpub.dataexchange.AbstractExecutor;
import nc.impl.hrpub.dataexchange.DataExportExecutor;
import nc.impl.hrpub.dataexchange.DataImportExecutor;
import nc.impl.hrpub.dataexchange.businessprocess.DepartmentHistoryImportExecutor;
import nc.impl.hrpub.dataexchange.businessprocess.DepartmentImportExecutor;
import nc.impl.hrpub.dataexchange.businessprocess.HRInfosetExportExecutor;
import nc.impl.hrpub.dataexchange.businessprocess.ImportDataImportExecutor;
import nc.impl.hrpub.dataexchange.businessprocess.LeaveBPImportExecutor;
import nc.impl.hrpub.dataexchange.businessprocess.LeaveImportExecutor;
import nc.impl.hrpub.dataexchange.businessprocess.OvertimeBPImportExecutor;
import nc.impl.hrpub.dataexchange.businessprocess.OvertimeImportExecutor;
import nc.impl.hrpub.dataexchange.businessprocess.PsnInfosetImportExecutor;
import nc.impl.hrpub.dataexchange.businessprocess.PsndocImportExecutor;
import nc.impl.hrpub.dataexchange.businessprocess.PsnjobBPImportExecutor;
import nc.impl.hrpub.dataexchange.businessprocess.PsnjobImportExecutor;
import nc.impl.hrpub.dataexchange.businessprocess.SignBPImportExecutor;
import nc.itf.hrpub.IMDExchangeLogService;
import nc.itf.hrpub.IMDExchangeService;
import nc.itf.hrpub.util.CsvUtil;
import nc.itf.hrpub.util.JsonUtil;
import nc.itf.hrpub.util.MapKeyComparator;
import nc.itf.uap.rbac.userpassword.IUserPasswordManage;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;

import org.apache.commons.lang.StringUtils;

public class MDExchangeServiceImpl implements IMDExchangeService, IBackgroundWorkPlugin {
	private BaseDAO baseDAO;
	private IMDExchangeLogService logSrv;

	public IMDExchangeLogService getLogService() {
		if (logSrv == null) {
			logSrv = NCLocator.getInstance().lookup(IMDExchangeLogService.class);
		}
		return logSrv;
	}

	/**
	 * 数据处理方式
	 * 
	 * @author ssx
	 * 
	 */
	public enum ExchangeTypeEnum {
		NONE, // 不处理
		SYSTEMBP, // 系统保存校验
		CUSTOMIZE // 自定义处理
	}

	@Override
	public String JsonDataExchange(String jSONString) {
		String method = "";
		String hrorg = "";
		String schema = "";
		String datetime = "";
		String user = "";
		String password = "";
		String session = "";
		String forcesyn = "";
		String language = "";
		String classid = "";
		String rtn = "";
		try {
			Logger.error("---------------------WNC-EXCHANGE-RCV---------------");
			Logger.error(jSONString);
			Logger.error("---------------------WNC-EXCHANGE-END---------------");
			// jSONString to Map
			Map<String, Object> jsonMap = JsonUtil.json2Map(jSONString);

			// "SESSIONKEY": "<UNIQUE SESSION KEY>",
			session = (String) jsonMap.get("SESSIONKEY");

			if (StringUtils.isEmpty(session)) {
				throw new BusinessException("交易标识 [SESSIONKEY] 不能为空");
			}

			this.getLogService().saveDataFile(session, new UFDate().toString(), jSONString, true);

			// "METHOD": "<IMPORT|EXPORT>",
			method = (String) jsonMap.get("METHOD");
			// "HRORG": "<ORG CODE>",
			hrorg = (String) jsonMap.get("HRORG");
			// "SCHEMA": "<SCHEMA CODE>",
			schema = (String) jsonMap.get("SCHEMA");
			// "DATETIME": "<NOW>",
			datetime = (String) jsonMap.get("DATETIME");
			// "OPERATOR": "<USER CODE>",
			user = (String) jsonMap.get("OPERATOR");
			// "PASSWORD": "<USER PASSWORD>",
			password = (String) jsonMap.get("PASSWORD");
			// "FORCESYN": "<TRUE|FALSE>",
			forcesyn = (String) jsonMap.get("FORCESYN");
			// 語言: simpchn, tradchn, english
			language = (String) jsonMap.get("LANGUAGE");

			if (StringUtils.isEmpty(language)) {
				language = "simpchn";
			} else {
				language = language.toLowerCase();
			}

			if (StringUtils.isEmpty(method)) {
				throw new BusinessException("操作方式 [METHOD] 不能为空");
			}

			if (StringUtils.isEmpty(hrorg)) {
				throw new BusinessException("导入导出方案所属组织编码 [HRORG] 不能为空");
			}
			String pk_org = getPk_org(hrorg);

			if (StringUtils.isEmpty(schema)) {
				throw new BusinessException("导入导出方案编码 [SCHEMA] 不能为空");
			}

			String pk_group = getPk_group(pk_org);
			InvocationInfoProxy.getInstance().setGroupId(pk_group);

			if (StringUtils.isEmpty(user)) {
				throw new BusinessException("操作员 [OPERATOR] 不能为空");
			}
			String cuserid = getUserID(user);

			if (StringUtils.isEmpty(password)) {
				// throw new BusinessException("密码 [PASSWORD] 不能为空");
			} else {
				// 用户密码检查
				checkUserPassword(cuserid, password);
			}

			String pk_ioschema = getPk_ioschema(schema);

			Map<String, Object> data = null;

			if (method.equals("IMPORT")) {
				data = (Map<String, Object>) jsonMap.get("IMPORTDATA");
			} else if (method.equals("EXPORT")) {
				data = (Map<String, Object>) jsonMap.get("EXPORTDATA");
			}

			String metadata = (String) data.get("METADATA");
			if (StringUtils.isEmpty(metadata)) {
				throw new BusinessException("语义元数据 [METADATA] 不能为空");
			}

			// 用户导入导出方案权限检查
			classid = checkExchangeRights(method, pk_org, pk_ioschema, cuserid, metadata);

			List<Map<String, Object>> lineRecords = null;

			AbstractExecutor executor = null;

			if (method.toUpperCase().equals("IMPORT")) {
				lineRecords = (List<Map<String, Object>>) data.get("RECORD");
				executor = getDataImportExecutor(metadata, ExchangeTypeEnum.SYSTEMBP);
				executor.setSkipNotExistMapping(true);
			} else if (method.toUpperCase().equals("EXPORT")) {
				lineRecords = new ArrayList<Map<String, Object>>();
				lineRecords.add(jsonMap);
				executor = getDataExportExecutor(metadata, ExchangeTypeEnum.CUSTOMIZE);
			} else {
				throw new BusinessException("数据结构错误，资料传输失败");
			}

			executor.setNcEntityName(metadata);
			executor.setJsonValueObjects(lineRecords);
			executor.setPk_ioschema(pk_ioschema);
			executor.setPk_org(pk_org);
			executor.setPk_group(pk_group);
			executor.setCuserid(cuserid);
			executor.setClassid(classid);
			executor.setOperationType(method.toUpperCase());
			executor.setSessionid(session);
			executor.setLanguage(language);
			executor.setHoldReservedProperties(false);
			executor.execute();

			if (executor.getNcValueObjects().size() == 0) {
				rtn = "{}";
			} else {
				rtn = executor.convertJsonValueObjectsToString();
			}
		} catch (Exception e) {
			String rowno = "";
			String message = "";
			if (e.getMessage().startsWith("ROWNO")) {
				rowno = e.getMessage().split(":")[0].split("=")[1];
				message = e.getMessage().substring(e.getMessage().indexOf(":") + 1);
			} else {
				message = e.getMessage();
			}
			rtn = getJsonErrorMessage(session, rowno, message);
		}

		try {
			this.getLogService().saveDataFile(session, new UFDate().toString(), rtn, false);
		} catch (BusinessException e) {
			rtn = e.getMessage();
		}

		return rtn;
	}

	private String getPk_group(String pk_org) throws BusinessException {
		return (String) this.getBaseDAO().executeQuery("select pk_group from org_orgs where pk_org = '" + pk_org + "'",
				new ColumnProcessor());
	}

	private String getPk_ioschema(String schema) throws BusinessException {
		String strSQL = "select id from hrpub_ioschema where LOWER(code)='" + schema.toLowerCase() + "'";
		String pk_ioschema = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		if (StringUtils.isEmpty(pk_ioschema)) {
			throw new BusinessException("在系统中无法找到指定的导入导出方案" + " [" + schema + "]");
		}
		return pk_ioschema;
	}

	private String getPk_org(String hrorg) throws BusinessException {
		String strSQL = "select pk_org from org_orgs where LOWER(code)='" + hrorg.toLowerCase() + "'";
		String pk_org = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		if (StringUtils.isEmpty(pk_org)) {
			throw new BusinessException("在系统中无法找到指定的组织" + " [" + hrorg + "]");
		}
		return pk_org;
	}

	private String getUserID(String user) throws BusinessException {
		String strSQL = "select cuserid from sm_user where LOWER(user_code)='" + user.toLowerCase() + "'";
		String cuserid = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		if (StringUtils.isEmpty(cuserid)) {
			throw new BusinessException("在系统中无法找到指定的操作员" + " [" + user + "]");
		}
		return cuserid;
	}

	@Override
	public String checkExchangeRights(String method, String pk_org, String pk_ioschema, String cuserid,
			String entityName) throws BusinessException {
		String strSQL = "SELECT count(*) FROM hrpub_iopermit pmt "
				+ " LEFT JOIN hrpub_mdclass cls on pmt.pk_org = cls.pk_org and pmt.pk_mdclass = cls.pk_mdclass "
				+ " LEFT JOIN md_class mcl on cls.pk_class = mcl.id " + " WHERE pmt.pk_org = '" + pk_org + "' "
				+ " AND pmt.cuserid = '" + cuserid + "' " + " AND pmt.pk_ioschema='" + pk_ioschema + "' "
				+ (method.toUpperCase().equals("IMPORT") ? " AND enableimport = 'Y' " : " AND enableexport = 'Y' ")
				+ " AND (cls.pk_class is null or cls.pk_class = '~' or LOWER(mcl.name) = '" + entityName.toLowerCase()
				+ "') " + "AND pmt.dr = 0 " + "AND pmt.dr = 0 ";
		Integer count = (Integer) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		if (count == 0) {
			throw new BusinessException("当前用户不允许进行数据交换操作");
		}

		strSQL = "select mcl.id from hrpub_mdclass cls " + "inner join md_class mcl ON cls.pk_class = mcl.id "
				+ "where cls.pk_ioschema='" + pk_ioschema + "' and LOWER(mcl.name) =  '" + entityName.toLowerCase()
				+ "' " + "and cls.isenabled='Y' and cls.dr=0";
		String classid = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		return classid;
	}

	private String getJsonErrorMessage(String session, String row, String message) {
		StringBuffer errMsg = new StringBuffer();
		errMsg.append("{\"SESSIONKEY\":\"" + session + "\",");
		errMsg.append("\"TASKSTATUS\":\"ERROR\",");
		errMsg.append("\"MESSAGE\":[{");
		errMsg.append("\"ROWNO\":\"" + (StringUtils.isEmpty(row) ? "-" : row) + "\",");
		errMsg.append("\"MESSAGE\":\"" + message + "\"");
		errMsg.append("}]}");
		return errMsg.toString();
	}

	private void checkUserPassword(String user, String password) throws BusinessException {
		// 檢查用戶密碼
		IUserPasswordManage mng = NCLocator.getInstance().lookup(IUserPasswordManage.class);
		if (!mng.checkUserPassWord(user, password)) {
			throw new BusinessException("操作员密码不匹配");
		}
	}

	public BaseDAO getBaseDAO() {
		if (baseDAO == null) {
			baseDAO = new BaseDAO();
		}
		return baseDAO;
	}

	@Override
	public PreAlertObject executeTask(BgWorkingContext bgwc) throws BusinessException {
		// check csv files
		String filePath = RuntimeEnv.getInstance().getNCHome() + "/importdata/hr/dataexchange/datafiles/";
		Logger.error("--- Data Exchange Log ---");
		Logger.error("Source File Path: " + filePath);
		File files = new File(filePath);
		for (File f : files.listFiles()) {
			if (!f.isDirectory() && f.getName().endsWith(".csv")) {
				try {
					CsvUtil csvUtl = new CsvUtil(f.getPath());
					List headList = CsvUtil.fromCSVLinetoArray(csvUtl.readLine());
					String data = "";
					List<Map<String, Object>> jsonObj = new ArrayList<Map<String, Object>>();
					Integer lineNo = 1;
					Integer batchSize = (f.getName().toLowerCase().contains("dept")) ? 1 : 5000;
					Map<String, String> errMsgs = new HashMap<String, String>();
					while (!StringUtils.isEmpty(data = csvUtl.readLine())) {
						lineNo++;
						List valueList = CsvUtil.fromCSVLinetoArray(data);
						if (valueList.size() != 0 && headList.size() != valueList.size()) {
							throw new BusinessException("文件格式错误：标题与数据个数不匹配（文件：[" + f.getName() + "], 行：["
									+ lineNo.toString() + "]）");
						}
						Map<String, Object> valueMap = new HashMap<String, Object>();
						valueMap.put("ROWNO", String.valueOf(lineNo));
						for (int i = 0; i < headList.size(); i++) {
							valueMap.put((String) headList.get(i), valueList.get(i));
						}
						jsonObj.add(valueMap);
						if (jsonObj.size() == batchSize) {
							errMsgs.putAll(saveToService(f.getName().substring(0, f.getName().lastIndexOf(".csv")),
									jsonObj, bgwc));
							jsonObj.clear();
						}
					}

					if (jsonObj.size() > 0) {
						errMsgs.putAll(saveToService(f.getName().substring(0, f.getName().lastIndexOf(".csv")),
								jsonObj, bgwc));
					}

					csvUtl.closeFile();

					if (errMsgs.size() > 0) {
						File outFile = new File(f.getPath().replace(
								".csv",
								".unimported."
										+ (new UFDateTime().toString().replace(" ", "_").replace(":", "_").replace("-",
												"_")) + ".csv"));
						outFile.createNewFile();
						BufferedWriter fwriter = new BufferedWriter(new OutputStreamWriter(
								new FileOutputStream(outFile), StandardCharsets.UTF_8));
						int row = 1;
						csvUtl = new CsvUtil(f.getPath());
						while (!StringUtils.isEmpty(data = csvUtl.readLine())) {
							if (row == 1 || errMsgs.containsKey(String.valueOf(row))) {
								fwriter.write(data + "\r\n");
							}

							row++;
						}
						csvUtl.closeFile();
						fwriter.flush();
						fwriter.close();
					}

					f.renameTo(new File(f.getPath().replace(
							".csv",
							".csv."
									+ (new UFDateTime().toString().replace(" ", "_").replace(":", "_")
											.replace("-", "_")) + ".bak")));

					if (errMsgs.size() > 0) {
						File logFile = new File(f.getPath().replace(
								".csv",
								".csv."
										+ (new UFDateTime().toString().replace(" ", "_").replace(":", "_").replace("-",
												"_")))
								+ ".log");
						logFile.createNewFile();
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
								new FileOutputStream(logFile), StandardCharsets.UTF_8));
						writer.write("------ Data Migration Import Log -----\r\n\r\n");
						MapKeyComparator mapCmp = new MapKeyComparator();
						errMsgs = mapCmp.sortMapByKey(errMsgs);
						for (Entry<String, String> err : errMsgs.entrySet()) {
							writer.write("Log Time:" + new UFDateTime().toString() + "\r\nRow No.:" + err.getKey()
									+ "\r\nMessages:\r\n" + err.getValue() + "\r\n\r\n");
						}
						writer.flush();
						writer.close();
					}

				} catch (Exception e) {
					throw new BusinessException(e.getMessage());
				}
			}
		}

		PreAlertObject retObj = new PreAlertObject();
		retObj.setReturnObj("后台导入插件执行完毕.");
		retObj.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		return retObj;
	}

	private Map<String, String> saveToService(String classname, List<Map<String, Object>> jsonObj, BgWorkingContext bgwc)
			throws BusinessException {
		AbstractExecutor executor = getDataImportExecutor(classname, ExchangeTypeEnum.CUSTOMIZE);
		String pk_ioschema = (String) bgwc.getKeyMap().get("pk_ioschema");
		String pk_org = (String) bgwc.getKeyMap().get("pk_org");
		String cuserid = (String) bgwc.getKeyMap().get("cuserid");
		String classid = checkExchangeRights("IMPORT", pk_org, pk_ioschema, cuserid, classname);
		executor.setNcEntityName(classname);
		executor.setJsonValueObjects(jsonObj);
		executor.setPk_ioschema(pk_ioschema);
		executor.setPk_org(pk_org);
		executor.setPk_group(this.getPk_group(pk_org));
		executor.setCuserid(cuserid);
		executor.setClassid(classid);
		executor.setOperationType("IMPORT");
		executor.setSessionid(UUID.randomUUID().toString());
		executor.setLanguage((String) bgwc.getKeyMap().get("language"));
		executor.setHoldReservedProperties(true);
		executor.execute();

		if (executor.getErrorMessages().size() > 0) {
			return executor.getErrorMessages();
		}

		return new HashMap<String, String>();
	}

	private AbstractExecutor getDataImportExecutor(String classname, ExchangeTypeEnum exType) throws BusinessException {
		DataImportExecutor newImportExecutor = null;
		if (exType.equals(ExchangeTypeEnum.SYSTEMBP)) {
			if (classname.toLowerCase().startsWith("hi_psnjob")) {
				newImportExecutor = new PsnjobBPImportExecutor();
			} else if (classname.toLowerCase().startsWith("hrtaleavereg")) {
				newImportExecutor = new LeaveBPImportExecutor();
			} else if (classname.toLowerCase().startsWith("hrtaovertimereg")) {
				newImportExecutor = new OvertimeBPImportExecutor();
			} else if (classname.toLowerCase().startsWith("hrtasignreg")) {
				newImportExecutor = new SignBPImportExecutor();
			}

			if (newImportExecutor != null) {
				newImportExecutor.setSaveByBP(true);
			}
		}

		if (exType.equals(ExchangeTypeEnum.CUSTOMIZE) || newImportExecutor == null) {
			if (classname.toLowerCase().startsWith("hi_psndoc_")) {
				newImportExecutor = new PsnInfosetImportExecutor();
			} else {
				switch (classname) {
				case "hrdept":
					newImportExecutor = new DepartmentImportExecutor();
					break;
				case "depthistory":
					newImportExecutor = new DepartmentHistoryImportExecutor();
					break;
				case "bd_psndoc":
					newImportExecutor = new PsndocImportExecutor();
					break;
				case "hi_psnjob":
					newImportExecutor = new PsnjobImportExecutor();
					break;
				case "hrtaovertimereg":
					newImportExecutor = new OvertimeImportExecutor();
					break;
				case "hrtaleavereg":
					newImportExecutor = new LeaveImportExecutor();
					break;
				case "importdata":
					newImportExecutor = new ImportDataImportExecutor();
					break;
				}
			}

			if (newImportExecutor != null) {
				newImportExecutor.setSaveByBP(false);
			}
		}

		if (newImportExecutor == null) {
			newImportExecutor = new DataImportExecutor();
			newImportExecutor.setSaveByBP(false);
		}

		return newImportExecutor;
	}

	private AbstractExecutor getDataExportExecutor(String classname, ExchangeTypeEnum exType) throws BusinessException {
		if (exType.equals(ExchangeTypeEnum.CUSTOMIZE)) {
			if (classname.toLowerCase().startsWith("hi_psndoc_")) {
				return new HRInfosetExportExecutor();
			}
		}
		return new DataExportExecutor();
	}

	@Override
	public String taskQuery(String sessionid) {
		String strSQL = "select status, ts from hrpub_iolog where sessionid = '" + sessionid + "'";
		Map result = null;
		try {
			result = (Map) this.getBaseDAO().executeQuery(strSQL, new MapProcessor());
		} catch (DAOException e) {
			return "查詢任務時發生錯誤：" + e.getMessage();
		}

		if (result == null || result.size() == 0) {
			return "未找到指定SESSION ID的接口任務 [" + sessionid + "]";
		}

		return "當前查詢任務 [" + sessionid + "] 狀態為 [" + result.get("status") + "]，最後更新時間：[" + result.get("ts") + "] ";
	}
}
