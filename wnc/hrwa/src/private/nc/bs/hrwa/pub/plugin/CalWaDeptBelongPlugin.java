package nc.bs.hrwa.pub.plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.plugin.WaDeptBelongVO;

import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @description 薪资部门所属部门查询后台任务
 * @description 每次执行报表查询前，需要先手动执行该任务，以确保部门数据的正确性
 * @since 2019-05-25
 * @author Connie.ZH
 * 
 */
// 20200107 by Jimmy wnc0001 更改部門combine_logic如下。
/*
 * combine_logic 1. 部級以下，需合併至部級以上為止。 2.
 * 如果部門有相同的上級部門，則少於等於2人的部門要匯總后合併到上級部門，再去判斷上級部門是否少於等於2人。
 * 例如，A部門下又A1和A2、A3三個子部門，A部門有1個人，A1部門有1個人，A2部門有3個人，A3部門有1個人，
 * 那麼匯總后，A2部門的歸屬部門還是A2， A1和A3部門由於都是少於等於2個人，所以要將A1和A3部門的人數匯總后，再向上合併，合併后A部門有3人。
 * 依次類推繼續向上合併，直到匯總到上級后部門人數大於2人時為止。 3. 合併時，為了知道上級是否為IDL or DL，多一個判斷 >> 若上級原始人數 >
 * 0 才合併 4. 如果還是有人數少於3人的話，繼續向上合併，一直到董事長室為止
 */
public class CalWaDeptBelongPlugin implements IBackgroundWorkPlugin {

	private static BaseDAO dao;

	// 董事长所在部门PK
	private static final String CHAIRMAN_DEPT = "1001A1100000000001V9";

	// 查詢最新薪資期間
	private static final String getNewestYear = "SELECT max(cyear) cyear FROM wa_data wa WHERE wa.pk_wa_class ='1001X11000000000APCA' and cpaydate is not null";
	private static final String getNewestPeriod = "SELECT max(cperiod) cperiod FROM wa_data wa WHERE wa.pk_wa_class ='1001X11000000000APCA' and cpaydate is not null";

	// 查询最底层的部门
	// private static final String getButtomDepts =
	// "SELECT org_dept.pk_dept FROM org_dept WHERE org_dept.enablestate=2 and org_dept.hrcanceled='N' and org_dept.pk_dept not in ( (SELECT pk_dept FROM org_dept WHERE pk_dept  in ( SELECT pk_fatherorg FROM org_dept))) group by org_dept.pk_dept";
	// 就算是撤銷部門也要合併，因為會有換部門後，撤銷的情況。
	private static final String getButtomDepts = "SELECT org_dept.pk_dept FROM org_dept WHERE org_dept.enablestate=2 and org_dept.pk_dept not in ( (SELECT pk_dept FROM org_dept WHERE pk_dept  in ( SELECT pk_fatherorg FROM org_dept))) group by org_dept.pk_dept";

	// 查询所有部门層級
	private static final String getDeptsLevel = "SELECT to_number(NVL(def.code,0)) FROM org_dept dept LEFT JOIN bd_defdoc def ON dept.deptlevel = def.pk_defdoc WHERE to_number(NVL(def.code,0)) != 0 GROUP BY to_number(NVL(def.code,0)) ORDER BY to_number(NVL(def.code,0)) DESC";

	public static final String CAL_CYEAR = "cyear";
	private static final String CAL_CPERIOD = "cperiod";

	private synchronized static BaseDAO getDao() {
		if (dao == null) {
			dao = new BaseDAO();
		}
		return dao;
	}

	List<WaDeptBelongVO> listAll = new ArrayList<WaDeptBelongVO>();

	@SuppressWarnings("unchecked")
	@Override
	public PreAlertObject executeTask(BgWorkingContext bgwc) throws BusinessException {
		// 0. clear
		getDao().executeUpdate("truncate table wa_dept_belong");
		LinkedHashMap<String, Object> keyMap = bgwc.getKeyMap();

		// 0. find Newest Period
		String cyear = (String) keyMap.get(CAL_CYEAR);
		String cperiod = (String) keyMap.get("cperiod");
		if (StringUtils.isEmpty(cyear) || StringUtils.isEmpty(cperiod)) {
			// throw new
			// BusinessException("Both cyear and cperiod can not be empty!");

			// Get Period By wa_data
			cyear = (String) getDao().executeQuery(getNewestYear, new ColumnProcessor());
			cperiod = (String) getDao().executeQuery(getNewestPeriod, new ColumnProcessor());

			// Get Period By DateTime
			/*
			 * Date date=new Date();//此时date为当前的时间 SimpleDateFormat
			 * dateFormat_year=new SimpleDateFormat("YYYY"); SimpleDateFormat
			 * dateFormat_month=new SimpleDateFormat("MM"); Calendar rightNow =
			 * Calendar.getInstance(); rightNow.setTime(date);
			 * rightNow.add(Calendar.MONTH, -1); Date dt1 = rightNow.getTime();
			 * 
			 * cyear = dateFormat_year.format(dt1); cperiod =
			 * dateFormat_month.format(dt1);
			 */
		}
		// 1-1. find all the buttomest dept
		String[] buttomDepts = (String[]) getDao().executeQuery(getButtomDepts, new ResultSetProcessor() {
			@Override
			public Object handleResultSet(ResultSet rs) {
				List<String> buttomDepts = new ArrayList<String>();
				try {
					while (rs.next()) {
						buttomDepts.add(rs.getString(1));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return buttomDepts.toArray(new String[0]);
			}
		});

		// 1-2. find all the dept levels
		// Integer[] DeptsLevel = (Integer[])
		// getDao().executeQuery(getDeptsLevel, new ResultSetProcessor() {
		// @Override
		// public Object handleResultSet(ResultSet rs) {
		// List<Integer> DeptsLevel = new ArrayList<Integer>();
		// try {
		// while (rs.next()) {
		// DeptsLevel.add(rs.getInt(1));
		// }
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// return DeptsLevel.toArray(new Integer[0]);
		// }
		// });
		// 2. insert all the original employees's number to the all depts
		// 查詢IDL & DL部門(底層部門)
		try {

			int perint = 2000;
			int modint = 0;
			if (buttomDepts.length % perint == 0) {
				modint = buttomDepts.length / perint;
			} else {
				modint = buttomDepts.length / perint + 1;
			}
			String[] buttomDeptsPer = null;
			List<WaDeptBelongVO> listAllTemp = new ArrayList<WaDeptBelongVO>();
			for (int i = 0; i < modint; i++) {
				if (buttomDepts.length - i * perint <= perint) {
					// 最後餘數執行
					buttomDeptsPer = (String[]) Arrays.copyOfRange(buttomDepts, (modint - 1) * perint,
							buttomDepts.length);
					// 寫入wa_dept_belong，資料來源 IDL & DL底層
					listAllTemp = insertDeptBelong("DL", cyear, cperiod, buttomDeptsPer);
					if (listAllTemp != null && !listAllTemp.isEmpty()) {
						listAll.addAll(listAllTemp);
					}
					listAllTemp = insertDeptBelong("IDL", cyear, cperiod, buttomDeptsPer);
					if (listAllTemp != null && !listAllTemp.isEmpty()) {
						listAll.addAll(listAllTemp);
					}

					// getDao().insertVOList(listAll);
				} else {
					// 每次執行
					buttomDeptsPer = (String[]) Arrays.copyOfRange(buttomDepts, i * perint, (i + 1) * perint);

					// 寫入wa_dept_belong，資料來源 IDL & DL底層
					listAllTemp = insertDeptBelong("DL", cyear, cperiod, buttomDeptsPer);
					if (listAllTemp != null && !listAllTemp.isEmpty()) {
						listAll.addAll(listAllTemp);
					}
					listAllTemp = insertDeptBelong("IDL", cyear, cperiod, buttomDeptsPer);
					if (listAllTemp != null && !listAllTemp.isEmpty()) {
						listAll.addAll(listAllTemp);
					}

					// //寫入wa_dept_belong，資料來源 IDL & DL IN 底層
					// insertInDeptBelong("DL", cyear, cperiod, buttomDeptsPer);
					// insertInDeptBelong("IDL", cyear, cperiod,
					// buttomDeptsPer);
					// //寫入wa_dept_belong，資料來源 IDL & DL NOT IN 底層
					// insertNotInDeptBelong("DL", cyear, cperiod,
					// buttomDeptsPer);
					// insertNotInDeptBelong("IDL", cyear, cperiod,
					// buttomDeptsPer);
				}

				if (listAll != null && !listAll.isEmpty()) {
					for (WaDeptBelongVO vo : listAll) {
						try {
							getDao().insertVO(vo);
						} catch (DAOException e) {
							throw new BusinessException(e.getMessage());
						}
					}
				}
			}

		} catch (DAOException e) {
			throw new BusinessException(e.getMessage());
		}

		// //寫入wa_dept_belong，資料來源 IDL & DL IN 底層
		// getDao().insertVOList(buttonDeptListDL);
		// getDao().insertVOList(buttonDeptListIDL);
		// //寫入wa_dept_belong，資料來源 IDL & DL NOT IN 底層
		// insertDeptBelong("DL", cyear, cperiod, buttomDepts);
		// insertDeptBelong("IDL", cyear, cperiod, buttomDepts);
		// query all depts(合併部門初始人數)
		List<WaDeptBelongVO> allIDLs = (List<WaDeptBelongVO>) getDao().retrieveByClause(WaDeptBelongVO.class,
				" dir = 'IDL' AND dept_level != 0  order by dept_level desc,pk_fatherorg,pk_dept");
		List<WaDeptBelongVO> allDLs = (List<WaDeptBelongVO>) getDao().retrieveByClause(WaDeptBelongVO.class,
				" dir = 'DL' AND dept_level != 0  order by dept_level desc,pk_fatherorg,pk_dept");

		// combine_logic(1)

		for (WaDeptBelongVO idlvo : allIDLs) {
			int num = idlvo.getCal_num();
			for (WaDeptBelongVO cal_idlvo : allIDLs) {
				if (cal_idlvo.getDept_level() > 70 && idlvo.getPk_dept().equals(cal_idlvo.getPk_fatherorg())) {
					cal_idlvo.setPk_dept_belong(idlvo.getPk_dept());
					num += cal_idlvo.getCal_num();
					cal_idlvo.setCal_num(0);
					idlvo.setCal_num(num);
				}
			}
		}

		for (WaDeptBelongVO dlvo : allDLs) {
			int num = dlvo.getCal_num();
			for (WaDeptBelongVO cal_dlvo : allDLs) {
				if (cal_dlvo.getDept_level() > 70 && dlvo.getPk_dept().equals(cal_dlvo.getPk_fatherorg())) {
					cal_dlvo.setPk_dept_belong(dlvo.getPk_dept());
					num += cal_dlvo.getCal_num();
					cal_dlvo.setCal_num(0);
					dlvo.setCal_num(num);
				}
			}
		}

		// combine_logic(2 & 3)

		for (WaDeptBelongVO idlvo : allIDLs) {

			int num = idlvo.getCal_num();
			for (WaDeptBelongVO cal_idlvo : allIDLs) {
				if (cal_idlvo.getDept_level() <= 70 && idlvo.getPk_dept().equals(cal_idlvo.getPk_fatherorg())
						&& idlvo.getOri_num() > 0) {
					// ssx modified on 2020-09-30
					// 外層循環是父部門，內層循環是子部門，要向上合併子部門為什麼要看父部門滿不滿3人？
					// 暫時注掉，以觀後效
					// if (idlvo.getCal_num() < 3 || cal_idlvo.getCal_num() < 3)
					if (cal_idlvo.getCal_num() < 3) {
						// end 2020-09-30
						cal_idlvo.setPk_dept_belong(idlvo.getPk_dept());
						num += cal_idlvo.getCal_num();
						cal_idlvo.setCal_num(0);
						idlvo.setCal_num(num);
					} else {
						cal_idlvo.setPk_dept_belong(cal_idlvo.getPk_dept());
						idlvo.setPk_dept_belong(idlvo.getPk_dept());
						idlvo.setCal_num(num);
					}
				} else if (StringUtils.isEmpty(cal_idlvo.getPk_fatherorg())) {
					cal_idlvo.setPk_dept_belong(idlvo.getPk_dept());
				}
			}
		}

		// 更新合併部門欄位，將已經合併後的部門，更新至上級最終合併的部門
		for (WaDeptBelongVO idlvo : allIDLs) {
			for (WaDeptBelongVO cal_idlvo : allIDLs) {
				if (idlvo.getDept_level() <= 70 && cal_idlvo.getCal_num() == 0
						&& idlvo.getPk_dept().equals(cal_idlvo.getPk_dept_belong()) && idlvo.getOri_num() > 0) {
					if (!StringUtils.isEmpty(idlvo.getPk_dept_belong())) {
						cal_idlvo.setPk_dept_belong(idlvo.getPk_dept_belong());
					}
				} else if (idlvo.getDept_level() > 70 && cal_idlvo.getCal_num() == 0
						&& idlvo.getPk_dept().equals(cal_idlvo.getPk_dept_belong()) && cal_idlvo.getDept_level() > 70) {
					if (!StringUtils.isEmpty(idlvo.getPk_dept_belong())) {
						cal_idlvo.setPk_dept_belong(idlvo.getPk_dept_belong());
					}
				}
			}
		}

		// 更新合併部門欄位，將未合併的部門，更新合併部門為自己
		for (WaDeptBelongVO idlvo : allIDLs) {
			if (StringUtils.isEmpty(idlvo.getPk_dept_belong())) {
				idlvo.setPk_dept_belong(idlvo.getPk_dept());
			}
		}

		for (WaDeptBelongVO dlvo : allDLs) {

			int num = dlvo.getCal_num();
			for (WaDeptBelongVO cal_dlvo : allDLs) {
				if (cal_dlvo.getDept_level() <= 70 && dlvo.getPk_dept().equals(cal_dlvo.getPk_fatherorg())
						&& dlvo.getOri_num() > 0) {
					// ssx modified on 2020-09-30
					// 外層循環是父部門，內層循環是子部門，要向上合併子部門為什麼要看父部門滿不滿3人？
					// 暫時注掉，以觀後效
					// if (dlvo.getCal_num() < 3 || cal_dlvo.getCal_num() < 3) {
					if (cal_dlvo.getCal_num() < 3) {
						// end 2020-09-30
						cal_dlvo.setPk_dept_belong(dlvo.getPk_dept());
						num += cal_dlvo.getCal_num();
						cal_dlvo.setCal_num(0);
						dlvo.setCal_num(num);
					} else {
						cal_dlvo.setPk_dept_belong(cal_dlvo.getPk_dept());
						dlvo.setPk_dept_belong(dlvo.getPk_dept());
						dlvo.setCal_num(num);
					}
				} else if (StringUtils.isEmpty(cal_dlvo.getPk_fatherorg())) {
					cal_dlvo.setPk_dept_belong(dlvo.getPk_dept());
				}
			}
		}

		// combine_logic(4)

		// 更新合併部門欄位，將已經合併後的部門，更新至上級最終合併的部門
		for (WaDeptBelongVO dlvo : allDLs) {
			for (WaDeptBelongVO cal_dlvo : allDLs) {
				if (dlvo.getDept_level() <= 70 && cal_dlvo.getCal_num() == 0
						&& dlvo.getPk_dept().equals(cal_dlvo.getPk_dept_belong()) && dlvo.getOri_num() > 0) {
					if (!StringUtils.isEmpty(dlvo.getPk_dept_belong())) {
						cal_dlvo.setPk_dept_belong(dlvo.getPk_dept_belong());
					}
				} else if (dlvo.getDept_level() > 70 && cal_dlvo.getCal_num() == 0
						&& dlvo.getPk_dept().equals(cal_dlvo.getPk_dept_belong()) && cal_dlvo.getDept_level() > 70) {
					if (!StringUtils.isEmpty(dlvo.getPk_dept_belong())) {
						cal_dlvo.setPk_dept_belong(dlvo.getPk_dept_belong());
					}
				}
			}
		}

		// 更新合併部門欄位，將未合併的部門，更新合併部門為自己
		for (WaDeptBelongVO dlvo : allDLs) {
			if (StringUtils.isEmpty(dlvo.getPk_dept_belong())) {
				dlvo.setPk_dept_belong(dlvo.getPk_dept());
			}
		}

		// 更新ERROR欄位，將合併後少於3人的部門，ERROR = Y
		for (WaDeptBelongVO idlvo : allIDLs) {
			if ((idlvo.getCal_num() < 3 && idlvo.getCal_num() > 0)
					|| (!idlvo.getPk_dept().equals(idlvo.getPk_dept_belong()) && idlvo.getCal_num() > 0)) {
				idlvo.setError_flag("Y");
			} else {
				idlvo.setError_flag("N");
			}
		}
		for (WaDeptBelongVO dlvo : allDLs) {
			if ((dlvo.getCal_num() < 3 && dlvo.getCal_num() > 0)
					|| (!dlvo.getPk_dept().equals(dlvo.getPk_dept_belong()) && dlvo.getCal_num() > 0)) {
				dlvo.setError_flag("Y");
			} else {
				dlvo.setError_flag("N");
			}
		}

		// 更新ERROR欄位=Y的，將其合併至人數大於3的最上層部門
		// 計數錯誤迴圈，保證部門可以0錯誤
		int error_count = 3;
		while (error_count != 0) {

			for (WaDeptBelongVO idlvo : allIDLs) {
				int num = idlvo.getCal_num();
				for (WaDeptBelongVO cal_idlvo : allIDLs) {
					String calDept = cal_idlvo.getPk_dept() == cal_idlvo.getPk_dept_belong() ? cal_idlvo
							.getPk_fatherorg() : cal_idlvo.getPk_dept_belong();
					if (cal_idlvo.getError_flag() == "Y" && idlvo.getPk_dept().equals(calDept)) {
						if ((num + cal_idlvo.getCal_num()) >= 3) {
							cal_idlvo.setPk_dept_belong(idlvo.getPk_dept());
							num += cal_idlvo.getCal_num();
							cal_idlvo.setCal_num(0);
							idlvo.setCal_num(num);
							cal_idlvo.setError_flag("N");
							// 將下級部門之前的合併部門，更改成最上層的合併部門
							for (WaDeptBelongVO below_idlvo : allIDLs) {
								if (cal_idlvo.getPk_dept().equals(below_idlvo.getPk_dept_belong())) {
									below_idlvo.setPk_dept_belong(calDept);
								}
							}
							// 若上級人數為0，則繼續往上更新合併部門
						} else {
							String idlvoCalDept = idlvo.getPk_dept() == idlvo.getPk_dept_belong() ? idlvo
									.getPk_fatherorg() : idlvo.getPk_dept_belong();
							if (!StringUtils.isEmpty(idlvoCalDept)) {
								cal_idlvo.setPk_dept_belong(idlvoCalDept);
							} else {
								cal_idlvo.setPk_dept_belong(cal_idlvo.getPk_dept());
							}
						}
					}
				}
			}
			for (WaDeptBelongVO dlvo : allDLs) {
				int num = dlvo.getCal_num();
				for (WaDeptBelongVO cal_dlvo : allDLs) {
					String calDept = cal_dlvo.getPk_dept() == cal_dlvo.getPk_dept_belong() ? cal_dlvo.getPk_fatherorg()
							: cal_dlvo.getPk_dept_belong();
					if (cal_dlvo.getError_flag() == "Y" && dlvo.getPk_dept().equals(calDept)) {
						if ((num + cal_dlvo.getCal_num()) >= 3) {
							cal_dlvo.setPk_dept_belong(dlvo.getPk_dept());
							num += cal_dlvo.getCal_num();
							cal_dlvo.setCal_num(0);
							dlvo.setCal_num(num);
							cal_dlvo.setError_flag("N");
							// 將下級部門之前的合併部門，更改成最上層的合併部門
							for (WaDeptBelongVO below_dlvo : allDLs) {
								if (cal_dlvo.getPk_dept().equals(below_dlvo.getPk_dept_belong())) {
									below_dlvo.setPk_dept_belong(calDept);
								}
							}
							// 若上級人數為0，則繼續往上更新合併部門
						} else {
							String dlvoCalDept = dlvo.getPk_dept() == dlvo.getPk_dept_belong() ? dlvo.getPk_fatherorg()
									: dlvo.getPk_dept_belong();
							if (!StringUtils.isEmpty(dlvoCalDept)) {
								cal_dlvo.setPk_dept_belong(dlvoCalDept);
							} else {
								cal_dlvo.setPk_dept_belong(cal_dlvo.getPk_dept());
							}

						}
					}
				}
			}
			error_count--;
		}

		// ssx added on 2020-08-11
		// 增加指定部門合併邏輯，在自定義檔案中增加WNCDEPTBLG全局檔案，定義部門合併規則
		// code為要合併的部門，mnecode為合併到的目標部門
		List<Map<String, Object>> deptBelongList = (List<Map<String, Object>>) this
				.getDao()
				.executeQuery(
						"select (select pk_dept from org_dept where code = doc.code) pk_dept, "
								+ "(select pk_dept from org_dept where code = doc.mnecode) pk_dept_belong "
								+ "from bd_defdoc doc where doc.pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'WNCDEPTBLG')",
						new MapListProcessor());
		if (deptBelongList != null && deptBelongList.size() > 0) {
			for (WaDeptBelongVO vo : allDLs) {
				for (Map<String, Object> deptBelongMap : deptBelongList) {
					if (vo.getPk_dept().equals(deptBelongMap.get("pk_dept"))) {
						vo.setPk_dept_belong((String) deptBelongMap.get("pk_dept_belong"));
					}
				}
			}

			for (WaDeptBelongVO vo : allIDLs) {
				for (Map<String, Object> deptBelongMap : deptBelongList) {
					if (vo.getPk_dept().equals(deptBelongMap.get("pk_dept"))) {
						vo.setPk_dept_belong((String) deptBelongMap.get("pk_dept_belong"));
					}
				}
			}
		}
		// end

		getDao().updateVOList(allDLs);
		getDao().updateVOList(allIDLs);
		return null;
	}

	/**
	 * @description insert dept belong records of non-buttom depts
	 * @param type
	 * @param cyear
	 * @param cperiod
	 * @param buttomDeptArr
	 *            (buttom depts)
	 * @throws DAOException
	 * @throws BusinessException
	 */
	private List<WaDeptBelongVO> insertDeptBelong(String type, String cyear, String cperiod, String[] buttomDeptArr)
			throws DAOException, BusinessException {
		final List<WaDeptBelongVO> list = new ArrayList<WaDeptBelongVO>();
		final String dir = type;
		String tableName = "V_WA_DEPT_CAL_IDL";
		if ("DL".equals(type)) {
			tableName = "V_WA_DEPT_CAL_DL";
		}
		String sql = "";
		sql = " SELECT pk_dept,pk_fatherorg,sum(num) num,dept_level FROM( "
				+ "SELECT dept.pk_dept,dept.pk_fatherorg,v.num,to_number(def.code) dept_level FROM org_dept dept "
				+ "LEFT JOIN bd_defdoc def ON dept.deptlevel = def.pk_defdoc " + "INNER JOIN "
				+ tableName
				+ " v on v.workdept = dept.pk_dept and cyear='"
				+ cyear
				+ "' and cperiod = '"
				+ cperiod
				+ "' and psnclcode = '"
				+ dir
				+ "' WHERE dept.enablestate = 2 AND (dept.pk_dept IN ("
				// + "'" + StringUtils.join(buttomDeptArr, "','") + "'"
				+ "'"
				+ StringUtils.join((String[]) Arrays.copyOfRange(buttomDeptArr, 0, 1000), "','")
				+ "'"
				+ ") or dept.pk_dept in ("
				+ "'"
				+ StringUtils.join((String[]) Arrays.copyOfRange(buttomDeptArr, 1001, buttomDeptArr.length), "','")
				+ "'"
				+ "))"
				+ " UNION "
				+ " SELECT dept.pk_dept,dept.pk_fatherorg,v.num,to_number(def.code) dept_level FROM org_dept dept "
				+ "LEFT JOIN bd_defdoc def ON dept.deptlevel = def.pk_defdoc "
				+ "INNER JOIN "
				+ tableName
				+ " v on v.workdept = dept.pk_dept and cyear='"
				+ cyear
				+ "' and cperiod = '"
				+ cperiod
				+ "' and psnclcode = '"
				+ dir
				+ "' WHERE dept.enablestate = 2 AND (dept.pk_dept NOT IN ("
				// + "'" + StringUtils.join(buttomDeptArr, "','") + "'"
				+ "'"
				+ StringUtils.join((String[]) Arrays.copyOfRange(buttomDeptArr, 0, 1000), "','")
				+ "'"
				+ ") or dept.pk_dept in ("
				+ "'"
				+ StringUtils.join((String[]) Arrays.copyOfRange(buttomDeptArr, 1001, buttomDeptArr.length), "','")
				+ "'" + "))" + " ) GROUP BY pk_dept,pk_fatherorg,dept_level ";
		getDao().executeQuery(sql, new ResultSetProcessor() {
			@Override
			public Object handleResultSet(ResultSet rs) {
				try {
					while (rs.next()) {
						WaDeptBelongVO vo = new WaDeptBelongVO();
						// vo.setPk_dept_belong(UUID.randomUUID().toString().replace("-",
						// "").substring(0,20));
						vo.setError_flag("N");
						vo.setDir(dir);
						vo.setDept_level(rs.getInt(4));
						vo.setPk_dept(rs.getString(1));
						vo.setPk_fatherorg(rs.getString(2));
						vo.setOri_num(null == rs.getString(3) ? 0 : new Integer(rs.getString(3)));
						vo.setCal_num(vo.getOri_num());
						list.add(vo);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		});
		if (!list.isEmpty()) {
			return list;
			// getDao().insertVOList(list);
		} else {
			throw new BusinessException("Query dept error !");
		}
	}

}
