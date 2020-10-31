package nc.impl.wa.func;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 區間留停天數
 * 
 * @author: xqy
 * @date: 2018-05-31
 */
@SuppressWarnings({ "serial", "restriction" })
public class IntervalDurationParse extends AbstractWAFormulaParse {

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		String[] arguments = getArguments(formula);
		String replaceArg = "SELECT " + arguments[0] + "," + arguments[1] + " FROM wa_data WHERE wa_data.pk_org='"
				+ context.getPk_org() + "' AND wa_data.pk_wa_class='" + context.getPk_wa_class()
				+ "' AND wa_data.cyearperiod='" + context.getCyear() + context.getCperiod() + "'";

		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr(" wa_cacu_data.cacu_value ");

		valueOfTBMPsn(getContext(), replaceArg);
		return fvo;
	}

	/**
	 * 取區間留停天數
	 * 
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public static void valueOfTBMPsn(WaLoginContext context, String replaceArg) throws BusinessException {
		// 留停異動類型
		String refTransType = SysInitQuery.getParaString(context.getPk_org(), "TWHR11").toString();
		// 複職異動類型
		String refReturnType = SysInitQuery.getParaString(context.getPk_org(), "TWHR12").toString();

		if (refTransType == null || refTransType.equals("~")) {
			throw new BusinessException("系統參數[TWHR11]未指定用於留停的異動類型。");
		}
		if (refReturnType == null || refReturnType.equals("~")) {
			throw new BusinessException("系統參數[TWHR12]未指定用於留停復職的異動類型。");
		}
		BaseDAO dao = new BaseDAO();
		String sql = "update wa_cacu_data set cacu_value = 0 where  " + "pk_wa_class = '" + context.getPk_wa_class()
				+ "' and creator = '" + context.getPk_loginUser() + "'";
		dao.executeUpdate(sql);

		sql = "select DISTINCT pk_psndoc from wa_cacu_data where pk_wa_class= '" + context.getPk_wa_class()
				+ "' and creator = '" + context.getPk_loginUser() + "'";

		ArrayList<String> pk_psndocs = (ArrayList<String>) dao.executeQuery(sql, new ColumnListProcessor());
		sql = "SELECT DISTINCT pk_psndoc FROM hi_psnjob WHERE trnstype = '" + refTransType + "' and pk_psndoc in ("
				+ sql + ")";// (SELECT pk_trnstype FROM hr_trnstype WHERE
							// trnstypecode = '0305')
		ArrayList<String> id_psndocs = (ArrayList<String>) dao.executeQuery(sql, new ColumnListProcessor());
		String beginDate = "", endDate = "";
		if (id_psndocs != null && id_psndocs.size() > 0) {
			sql = replaceArg + " AND wa_data.pk_psndoc='" + id_psndocs.get(0) + "'";
			ArrayList<Object[]> countDates = (ArrayList<Object[]>) dao.executeQuery(sql, new ArrayListProcessor());
			if (countDates != null) {
				Object[] cDates = countDates.get(0);
				beginDate = (String) cDates[1];
				endDate = (String) cDates[0];
			}
			String str = "(";
			for (int a = 0; a < id_psndocs.size(); a++) {
				str += "'" + id_psndocs.get(a) + "'";
				if (a == id_psndocs.size() - 1) {
					str += ")";
				} else {
					str += ",";
				}
			}
			sql = "select hi_psnjob.pk_psndoc,hi_psnjob.begindate,hi_psnjob.trnstype FROM hi_psnjob "
					+ " WHERE hi_psnjob.dr = 0 AND hi_psnjob.trnstype IN ('" + refTransType + "','" + refReturnType
					+ "')" + " AND hi_psnjob.pk_psndoc IN " + str + " AND hi_psnjob.pk_org = '" + context.getPk_org()
					+ "' AND hi_psnjob.pk_group = '" + context.getPk_group()
					+ "' ORDER BY hi_psnjob.pk_psndoc,hi_psnjob.begindate";
			ArrayList<Object[]> result = (ArrayList<Object[]>) dao.executeQuery(sql, new ArrayListProcessor());
			Boolean start = true, end = false, isExistPsn = false, isExistTrnstype = false, firstNoExist = false;
			String idBeginDate = "", idEndDate = "", psn = "", oldPsn = "";
			Map<String, Long> idMap = new HashMap<String, Long>();
			if (result != null) {
				Object[] firstRecoed = result.get(0);
				oldPsn = (String) firstRecoed[0];
				for (int i = 0; i < result.size(); i++) {
					Object[] records = result.get(i);
					psn = (String) records[0];
					isExistPsn = pk_psndocs.contains(psn);
					isExistTrnstype = (records[2] != null);
					if (isExistPsn) {
						firstNoExist = true;
						if (!idMap.containsKey(psn))
							idMap.put(psn, (long) 0);
						// 判断单个人员信息是否读取完成
						if (!oldPsn.equals(psn)) {
							if (end && !"".equals(idBeginDate)) {
								idMap.put(oldPsn, idMap.get(oldPsn) + getDaySub(idBeginDate, endDate));
								start = true;
								end = false;
							}
							oldPsn = psn;
						}
						if (start && isExistTrnstype && refTransType.equals((String) records[2])) {
							idBeginDate = records[1] == null ? "" : (String) records[1];
							if (!"".equals(idBeginDate) && getDaySub(idBeginDate, endDate) > 0) {
								if (getDaySub(idBeginDate, beginDate) > 0)
									idBeginDate = beginDate;
								start = false;
								end = true;
								// 最后一条记录判断
								if (i == result.size() - 1 && getDaySub(idBeginDate, endDate) > 0) {
									end = false;
									idMap.put(psn, idMap.get(psn) + getDaySub(idBeginDate, endDate));
								}
							}

						} else if (end && isExistTrnstype && refReturnType.equals((String) records[2])) {
							idEndDate = records[1] == null ? "" : (String) records[1];
							if (!"".equals(idEndDate)) {
								if (getDaySub(beginDate, idEndDate) > 0) {
									if (getDaySub(endDate, idEndDate) > 0)
										idEndDate = endDate;
									idMap.put(psn, idMap.get(psn) + getDaySub(idBeginDate, idEndDate));
								}
								end = false;
							}
							start = true;
						}
					} else if (firstNoExist) {
						if (end && !"".equals(idBeginDate)) {
							idMap.put(oldPsn, idMap.get(oldPsn) + getDaySub(idBeginDate, endDate));
							start = true;
							end = false;
						}
						oldPsn = psn;
						firstNoExist = false;
					}
				}
			}

			PersistenceManager sessionManager = null;
			try {
				sessionManager = PersistenceManager.getInstance();
				JdbcSession session = sessionManager.getJdbcSession();
				Iterator<Map.Entry<String, Long>> iterator = idMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, Long> entry = iterator.next();
					sql = "update wa_cacu_data set cacu_value = " + entry.getValue() + " where  " + "pk_wa_class = '"
							+ context.getPk_wa_class() + "' and creator = '" + context.getPk_loginUser()
							+ "'  and pk_psndoc = '" + entry.getKey() + "'";

					session.addBatch(sql);
				}
				session.executeBatch();
			} catch (DbException e) {
				throw new DAOException(e.getMessage());
			} finally {
				if (sessionManager != null) {
					sessionManager.release();
				}
			}
		}

	}

	/**
	 * <li>功能描述：时间相减得到天数
	 * 
	 * @param beginDateStr
	 * @param endDateStr
	 * @return long
	 * @author xqy
	 */
	public static long getDaySub(String beginDateStr, String endDateStr) {
		long day = 0;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date beginDate;
		Date endDate;
		try {
			beginDate = format.parse(beginDateStr);
			endDate = format.parse(endDateStr);
			day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
			// System.out.println("相隔的天数="+day);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return day;
	}

}
