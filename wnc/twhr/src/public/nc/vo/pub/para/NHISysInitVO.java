package nc.vo.pub.para;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.RuntimeEnv;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.pub.para.ICheckParaFinal;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class NHISysInitVO implements ICheckParaFinal {

	@Override
	public CheckParaVO paraEditabilityCheck(SysInitVO paramSysInitVO, SysInitVO[] paramArrayOfSysInitVO) {
		return null;
	}

	private String autoid = "PSETTW10000000000000";

	@Override
	public CheckParaVO paraBeforeSavingCheck(SysInitVO paramSysInitVO, SysInitVO[] paramArrayOfSysInitVO) {
		CheckParaVO chkvo = new CheckParaVO();
		if (paramSysInitVO.getInitcode().substring(0, 3).equals("TWHR")) {
			if (paramSysInitVO.getInitcode().equals("TWHR01") && "Y".equals(String.valueOf(paramSysInitVO.getValue()))) { // 啟用台灣人力資源
				// 读预置脚本
				StringBuilder[] sbPreset = readSQL("/ierp/twhr/*.sql", paramSysInitVO);

				// 执行预置脚本
				BaseDAO dao = new BaseDAO();
				for (StringBuilder sbSQL : sbPreset) {
					try {
						dao.executeUpdate(sbSQL.toString());
					} catch (BusinessException e) {
						chkvo.setLegal(false);
						chkvo.setErrMsg(e.getMessage());
						ExceptionUtils.wrappBusinessException(e.getMessage());
					}
				}

				// 返回预置结果
				chkvo.setLegal(true);
				chkvo.setErrMsg("");
				return chkvo;
			}
		}
		return null;
	}

	private StringBuilder[] readSQL(String filename, SysInitVO paramSysInitVO) {
		List<StringBuilder> sbContents = new ArrayList<StringBuilder>();
		try {
			RuntimeEnv.getInstance().getNCHome();
			File filelistObj = new File(RuntimeEnv.getInstance().getNCHome() + filename);
			File[] fileobjs = filelistObj.listFiles();

			for (File fileobj : fileobjs) {
				StringBuilder sbContent = new StringBuilder();
				InputStreamReader reader = new InputStreamReader(new FileInputStream(fileobj), "UTF-8");
				BufferedReader br = new BufferedReader(reader);
				String line = "";
				line = br.readLine();
				long lineno = 1;
				while (line != null) {
					line = br.readLine(); // 一次读入一行数据
					if (!line.trim().substring(0, 2).equals("--")) { // 注释行不读取
						line = replaceRefs(line, paramSysInitVO, fileobj.getName(), lineno);
						sbContent.append(line);
					}
					lineno++;
				}
				reader.close();

				sbContents.add(sbContent);
			}
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}

		return sbContents.toArray(new StringBuilder[0]);
	}

	private String replaceRefs(String line, SysInitVO paramSysInitVO, String filename, long lineno) {
		// pk_group
		line.replaceAll("$$PKGROUP$$", getPkGroupByOrg(paramSysInitVO.getPk_org()));
		// pk_org
		line.replaceAll("$$PKORG$$", paramSysInitVO.getPk_org());
		// time
		line.replaceAll("$$TS$$", paramSysInitVO.getModifiedTime().toLocalString());
		line.replaceAll("$$CREATETIME$$", paramSysInitVO.getModifiedTime().toLocalString());
		// user
		line.replaceAll("$$CREATOR$$", paramSysInitVO.getModifier());
		// id
		line.replaceAll("$$NEWID$$", generateAutoAddID());
		// Query
		line = replaceQuery(line, filename, lineno);
		return line;
	}

	private Map<String, String> queryValueMap = new HashMap<String, String>();

	private String replaceQuery(String line, String filename, long lineno) {
		String mark = "$$QUERY$$";
		int begin = 0;
		int end = 0;
		BaseDAO dao = new BaseDAO();
		while (begin >= 0) {
			if (begin == 0) {
				begin = line.indexOf(mark, 0);
			} else {
				begin = line.indexOf(mark, end + 1);
			}

			if (begin >= 0) {
				end = line.indexOf(mark, begin + mark.length()) + mark.length() - 1;
				if (end < 0) {
					ExceptionUtils.wrappBusinessException("預置腳本發生錯誤，查詢標記不匹配：" + filename + ": 第"
							+ String.valueOf(lineno) + "行");
				}
				String strQueryWithMark = line.substring(begin, end);
				String strQuery = strQueryWithMark.replace(mark, "");
				if (!queryValueMap.containsKey(strQuery)) {
					try {
						String value = (String) dao.executeQuery(strQuery, new ColumnProcessor());
						queryValueMap.put(strQuery, value);
					} catch (BusinessException e) {
						ExceptionUtils.wrappBusinessException(e.getMessage() + "：" + filename + ": 第"
								+ String.valueOf(lineno) + "行");
					}
				}
				line.replaceAll(strQueryWithMark, queryValueMap.get(strQuery));
			} else {
				break;
			}
		}
		return line;
	}

	private Map<String, String> grouporgMap = new HashMap<String, String>();

	private String getPkGroupByOrg(String pk_org) {
		if (grouporgMap.containsKey(pk_org)) {
			return grouporgMap.get(pk_org);
		} else {
			BaseDAO dao = new BaseDAO();
			String sql = "select pk_group from org_orgs where pk_org='" + pk_org + "'";
			try {
				String pk_group = (String) dao.executeQuery(sql, new ColumnProcessor());
				grouporgMap.put(pk_org, pk_group);
			} catch (DAOException e) {
				ExceptionUtils.wrappBusinessException(e.getMessage());
			}
		}
		return grouporgMap.get(pk_org);
	}

	private String generateAutoAddID() {
		for (int i = autoid.length() - 1; i >= 0; i--) {
			String cur = autoid.substring(i, i + 1);
			String nxt = findNext(cur);

			autoid = autoid.substring(0, i) + nxt
					+ (i == autoid.length() - 1 ? "" : autoid.substring(i + 1, autoid.length()));

			if (!nxt.equals("0")) {
				break;
			}
		}

		return autoid;
	}

	private String findNext(String current) {
		String seed = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int cur_pos = seed.indexOf(current);
		int nxt_pos = cur_pos + 1;
		if (nxt_pos > seed.length() - 1) {
			nxt_pos = 0;
		}
		return seed.substring(nxt_pos, nxt_pos + 1);
	}
}
