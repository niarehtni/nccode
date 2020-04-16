package nc.bs.hrwa.pub.plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.plugin.WaDeptBelongVO;

import org.apache.commons.lang.StringUtils;

/**
 * @description н�ʲ����������Ų�ѯ��̨����
 * @description ÿ��ִ�б����ѯǰ����Ҫ���ֶ�ִ�и�������ȷ���������ݵ���ȷ��
 * @since 2019-05-25
 * @author Connie.ZH
 * 
 */
//20200107	by Jimmy	wnc0001	���Ĳ��Tcombine_logic���¡�
/*
 * combine_logic
 1.
 �������£���ρ����������Ϟ�ֹ��
 2.
 ������T����ͬ���ϼ����T���t��춵��2�˵Ĳ��TҪ�R����ρ㵽�ϼ����T����ȥ�Д��ϼ����T�Ƿ���춵��2�ˡ�
 ���磬A���T����A1��A2��A3�����Ӳ��T��A���T��1���ˣ�A1���T��1���ˣ�A2���T��3���ˣ�A3���T��1���ˣ�
 ���N�R����A2���T�Ěw�ٲ��T߀��A2��
 A1��A3���T��춶�����춵��2���ˣ�����Ҫ��A1��A3���T���˔��R���������Ϻρ㣬�ρ��A���T��3�ˡ�
 ��������^�m���Ϻρ㣬ֱ���R�����ϼ����T�˔����2�˕r��ֹ�� 
 3.
 �ρ�r������֪���ϼ��Ƿ��IDL or DL����һ���Д� >> ���ϼ�ԭʼ�˔� > 0 �źρ�
 4.
 ���߀�����˔����3�˵�Ԓ���^�m���Ϻρ㣬һֱ�������L�Ҟ�ֹ
*/
public class CalWaDeptBelongPlugin implements IBackgroundWorkPlugin {

	private static BaseDAO dao; 

	// ���³����ڲ���PK
	private static final String CHAIRMAN_DEPT = "1001A1100000000001V9";

	// ��ѯ��ײ�Ĳ���
	private static final String getButtomDepts = "SELECT org_dept.pk_dept FROM org_dept WHERE org_dept.enablestate=2 and org_dept.hrcanceled='N' and org_dept.pk_dept not in ( (SELECT pk_dept FROM org_dept WHERE pk_dept  in ( SELECT pk_fatherorg FROM org_dept))) group by org_dept.pk_dept";

	// ��ѯ���в��ŌӼ�
	private static final String getDeptsLevel = "SELECT to_number(NVL(def.code,0)) FROM org_dept dept LEFT JOIN bd_defdoc def ON dept.deptlevel = def.pk_defdoc WHERE to_number(NVL(def.code,0)) != 0 GROUP BY to_number(NVL(def.code,0)) ORDER BY to_number(NVL(def.code,0)) DESC";
	
	public static final String CAL_CYEAR = "cyear";
	private static final String CAL_CPERIOD = "cperiod";
	
	private synchronized static BaseDAO getDao() {
		if (dao == null) {
			dao = new BaseDAO();
		}
		return dao;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PreAlertObject executeTask(BgWorkingContext bgwc) throws BusinessException {
		// 0. clear
		getDao().executeUpdate("truncate table wa_dept_belong");
		LinkedHashMap<String, Object> keyMap = bgwc.getKeyMap();
		String cyear = (String) keyMap.get(CAL_CYEAR);
		String cperiod = (String) keyMap.get("cperiod");
		if (StringUtils.isEmpty(cyear) || StringUtils.isEmpty(cperiod)) {
			throw new BusinessException("Both cyear and cperiod can not be empty!");
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
		Integer[] DeptsLevel = (Integer[]) getDao().executeQuery(getDeptsLevel, new ResultSetProcessor() {
			@Override
			public Object handleResultSet(ResultSet rs) {
				List<Integer> DeptsLevel = new ArrayList<Integer>();
				try {
					while (rs.next()) {
						DeptsLevel.add(rs.getInt(1));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return DeptsLevel.toArray(new Integer[0]);
			}
		});
		// 2. insert all the original employees's number to the all depts
		//��ԃIDL & DL���T(�׌Ӳ��T)
		List<WaDeptBelongVO> buttonDeptListIDL = new ArrayList<WaDeptBelongVO>();
		try {
			buttonDeptListIDL = (ArrayList<WaDeptBelongVO>) getDao()
					.executeQuery(
							" SELECT dept.pk_dept,v.num,dept.pk_fatherorg,to_number(NVL(def.code,0)) dept_level FROM org_dept dept "
									+ "LEFT JOIN bd_defdoc def ON dept.deptlevel = def.pk_defdoc "
									+ "LEFT JOIN  V_WA_DEPT_CAL_IDL v on v.workdept=dept.pk_dept and cyear='"
									+ cyear
									+ "' and cperiod = '"
									+ cperiod
									+ "'  WHERE  dept.pk_dept in ('"
									+ StringUtils.join(buttomDepts, "','") + "')", new ResultSetProcessor() {
								@Override
								public Object handleResultSet(ResultSet rs) {
									List<WaDeptBelongVO> retList = new ArrayList<WaDeptBelongVO>();
									try {
										while (rs.next()) {
											WaDeptBelongVO vo = new WaDeptBelongVO();
											vo.setError_flag("N");
											vo.setDir("IDL");
											vo.setDept_level(rs.getInt(4));
											vo.setPk_dept(rs.getString(1));
											vo.setOri_num(null == rs.getString(2) ? 0 : new Integer(rs.getString(2)));
											vo.setPk_fatherorg(rs.getString(3));
											vo.setCal_num(vo.getOri_num());
											retList.add(vo);
										}
									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return retList;
								}
							});
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage());
		}

		List<WaDeptBelongVO> buttonDeptListDL = new ArrayList<WaDeptBelongVO>();
		try {
			buttonDeptListDL = (ArrayList<WaDeptBelongVO>) getDao()
					.executeQuery(
							" SELECT dept.pk_dept,v.num,dept.pk_fatherorg,to_number(NVL(def.code,0)) dept_level FROM org_dept dept "
									+ "LEFT JOIN bd_defdoc def ON dept.deptlevel = def.pk_defdoc "
									+ "LEFT JOIN  V_WA_DEPT_CAL_DL v on v.workdept=dept.pk_dept and cyear='"
									+ cyear
									+ "' and cperiod = '"
									+ cperiod
									+ "' WHERE  dept.pk_dept in ('"
									+ StringUtils.join(buttomDepts, "','") + "')", new ResultSetProcessor() {
								@Override
								public Object handleResultSet(ResultSet rs) {
									List<WaDeptBelongVO> retList = new ArrayList<WaDeptBelongVO>();
									try {
										while (rs.next()) {
											WaDeptBelongVO vo = new WaDeptBelongVO();
											vo.setError_flag("N");
											vo.setDir("DL");
											vo.setDept_level(rs.getInt(4));
											vo.setPk_dept(rs.getString(1));
											vo.setOri_num(null == rs.getString(2) ? 0 : new Integer(rs.getString(2)));
											vo.setPk_fatherorg(rs.getString(3));
											vo.setCal_num(vo.getOri_num());
											retList.add(vo);
										}
									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return retList;
								}
							});
		} catch (DAOException e) {
			throw new BusinessException(e.getMessage());
		}
		//����wa_dept_belong���Y�ρ�Դ IDL & DL IN �׌�
		getDao().insertVOList(buttonDeptListDL);
		getDao().insertVOList(buttonDeptListIDL);
		//����wa_dept_belong���Y�ρ�Դ IDL & DL NOT IN �׌�
		insertDeptBelong("DL", cyear, cperiod, buttomDepts);
		insertDeptBelong("IDL", cyear, cperiod, buttomDepts);
		// query all depts
		List<WaDeptBelongVO> allIDLs = (List<WaDeptBelongVO>) getDao().retrieveByClause(WaDeptBelongVO.class,
				" dir = 'IDL' AND dept_level != 0  order by dept_level desc,pk_fatherorg,pk_dept");
		List<WaDeptBelongVO> allDLs = (List<WaDeptBelongVO>) getDao().retrieveByClause(WaDeptBelongVO.class,
				" dir = 'DL' AND dept_level != 0  order by dept_level desc,pk_fatherorg,pk_dept");


//combine_logic(1)
		
		for (WaDeptBelongVO idlvo : allIDLs) {
			int num = idlvo.getCal_num();
			for (WaDeptBelongVO cal_idlvo : allIDLs) {
				if (cal_idlvo.getDept_level() > 70 && idlvo.getPk_dept().equals(cal_idlvo.getPk_fatherorg())){
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
				if (cal_dlvo.getDept_level() > 70 && dlvo.getPk_dept().equals(cal_dlvo.getPk_fatherorg())){
					cal_dlvo.setPk_dept_belong(dlvo.getPk_dept());
					num += cal_dlvo.getCal_num();
					cal_dlvo.setCal_num(0);
					dlvo.setCal_num(num);
				}
			}
		}
		
		

//combine_logic(2 & 3)
		
		for (WaDeptBelongVO idlvo : allIDLs) {

			int num = idlvo.getCal_num();
			for (WaDeptBelongVO cal_idlvo : allIDLs) {
				if (cal_idlvo.getDept_level() <= 70 && idlvo.getPk_dept().equals(cal_idlvo.getPk_fatherorg()) && idlvo.getOri_num() > 0){
					if(idlvo.getCal_num() < 3 || cal_idlvo.getCal_num() < 3 ){
						cal_idlvo.setPk_dept_belong(idlvo.getPk_dept());
						num += cal_idlvo.getCal_num();
						cal_idlvo.setCal_num(0);
						idlvo.setCal_num(num);
					}else{
						cal_idlvo.setPk_dept_belong(cal_idlvo.getPk_dept());
						idlvo.setPk_dept_belong(idlvo.getPk_dept());
						idlvo.setCal_num(num);
					}
				}else if (StringUtils.isEmpty(cal_idlvo.getPk_fatherorg())){
					cal_idlvo.setPk_dept_belong(idlvo.getPk_dept());
				}
			}
		}

		//���ºρ㲿�T��λ�����ѽ��ρ���Ĳ��T���������ϼ���K�ρ�Ĳ��T
		for (WaDeptBelongVO idlvo : allIDLs) {
			for (WaDeptBelongVO cal_idlvo : allIDLs) {
				if (idlvo.getDept_level() <= 70 && cal_idlvo.getCal_num() == 0 && idlvo.getPk_dept().equals(cal_idlvo.getPk_dept_belong()) && idlvo.getOri_num() > 0){
					if(!StringUtils.isEmpty(idlvo.getPk_dept_belong())){
						cal_idlvo.setPk_dept_belong(idlvo.getPk_dept_belong());
					}
				}else if (idlvo.getDept_level() > 70 && cal_idlvo.getCal_num() == 0 && idlvo.getPk_dept().equals(cal_idlvo.getPk_dept_belong()) && cal_idlvo.getDept_level() > 70){
					if(!StringUtils.isEmpty(idlvo.getPk_dept_belong())){
						cal_idlvo.setPk_dept_belong(idlvo.getPk_dept_belong());
					}
				}
			}
		}

		//���ºρ㲿�T��λ����δ�ρ�Ĳ��T�����ºρ㲿�T���Լ�
		for (WaDeptBelongVO idlvo : allIDLs) {
			if (StringUtils.isEmpty(idlvo.getPk_dept_belong())){
				idlvo.setPk_dept_belong(idlvo.getPk_dept());
			}
		}
		

		for (WaDeptBelongVO dlvo : allDLs) {

			int num = dlvo.getCal_num();
			for (WaDeptBelongVO cal_dlvo : allDLs) {
				if (cal_dlvo.getDept_level() <= 70 && dlvo.getPk_dept().equals(cal_dlvo.getPk_fatherorg()) && dlvo.getOri_num() > 0){
					if(dlvo.getCal_num() < 3 || cal_dlvo.getCal_num() < 3 ){
						cal_dlvo.setPk_dept_belong(dlvo.getPk_dept());
						num += cal_dlvo.getCal_num();
						cal_dlvo.setCal_num(0);
						dlvo.setCal_num(num);
					}else{
						cal_dlvo.setPk_dept_belong(cal_dlvo.getPk_dept());
						dlvo.setPk_dept_belong(dlvo.getPk_dept());
						dlvo.setCal_num(num);
					}
				}else if (StringUtils.isEmpty(cal_dlvo.getPk_fatherorg())){
					cal_dlvo.setPk_dept_belong(dlvo.getPk_dept());
				}
			}
		}
		

//combine_logic(4)
		
		//���ºρ㲿�T��λ�����ѽ��ρ���Ĳ��T���������ϼ���K�ρ�Ĳ��T
		for (WaDeptBelongVO dlvo : allDLs) {
			for (WaDeptBelongVO cal_dlvo : allDLs) {
				if (dlvo.getDept_level() <= 70 && cal_dlvo.getCal_num() == 0 && dlvo.getPk_dept().equals(cal_dlvo.getPk_dept_belong()) && dlvo.getOri_num() > 0){
					if(!StringUtils.isEmpty(dlvo.getPk_dept_belong())){
						cal_dlvo.setPk_dept_belong(dlvo.getPk_dept_belong());
					}
				}else if (dlvo.getDept_level() > 70 && cal_dlvo.getCal_num() == 0 && dlvo.getPk_dept().equals(cal_dlvo.getPk_dept_belong()) && cal_dlvo.getDept_level() > 70){
					if(!StringUtils.isEmpty(dlvo.getPk_dept_belong())){
						cal_dlvo.setPk_dept_belong(dlvo.getPk_dept_belong());
					}
				}
			}
		}
		
		//���ºρ㲿�T��λ����δ�ρ�Ĳ��T�����ºρ㲿�T���Լ�
		for (WaDeptBelongVO dlvo : allDLs) {
			if (StringUtils.isEmpty(dlvo.getPk_dept_belong())){
				dlvo.setPk_dept_belong(dlvo.getPk_dept());
			}
		}


		//����ERROR��λ�����ρ������3�˵Ĳ��T��ERROR = Y
		for (WaDeptBelongVO idlvo : allIDLs) {
			if ((idlvo.getCal_num() < 3 && idlvo.getCal_num() > 0) || (!idlvo.getPk_dept().equals(idlvo.getPk_dept_belong()) && idlvo.getCal_num() > 0)){
				idlvo.setError_flag("Y");
			}else{
				idlvo.setError_flag("N");
			}
		}
		for (WaDeptBelongVO dlvo : allDLs) {
			if ((dlvo.getCal_num() < 3 && dlvo.getCal_num() > 0) || (!dlvo.getPk_dept().equals(dlvo.getPk_dept_belong()) && dlvo.getCal_num() > 0)){
				dlvo.setError_flag("Y");
			}else{
				dlvo.setError_flag("N");
			}
		}

		//����ERROR��λ=Y�ģ�����ρ����˔����3�����όӲ��T
		//Ӌ���e�`ޒȦ�����C���T����0�e�`
		int error_count = 3;
		while(error_count != 0){

			for (WaDeptBelongVO idlvo : allIDLs) {
				int num = idlvo.getCal_num();
				for (WaDeptBelongVO cal_idlvo : allIDLs) {
					String calDept = cal_idlvo.getPk_dept() == cal_idlvo.getPk_dept_belong() ? cal_idlvo.getPk_fatherorg() : cal_idlvo.getPk_dept_belong();
					if (cal_idlvo.getError_flag() == "Y" && idlvo.getPk_dept().equals(calDept)){
						if((num + cal_idlvo.getCal_num()) >= 3){
							cal_idlvo.setPk_dept_belong(idlvo.getPk_dept());
							num += cal_idlvo.getCal_num();
							cal_idlvo.setCal_num(0);
							idlvo.setCal_num(num);
							cal_idlvo.setError_flag("N");
							//���¼����T֮ǰ�ĺρ㲿�T�����ĳ����όӵĺρ㲿�T
							for (WaDeptBelongVO below_idlvo : allIDLs) {
								if (cal_idlvo.getPk_dept().equals(below_idlvo.getPk_dept_belong())){
									below_idlvo.setPk_dept_belong(calDept);
								}
							}
						//���ϼ��˔���0���t�^�m���ϸ��ºρ㲿�T
						}else{
							String idlvoCalDept = idlvo.getPk_dept() == idlvo.getPk_dept_belong() ? idlvo.getPk_fatherorg() : idlvo.getPk_dept_belong();
							if(!StringUtils.isEmpty(idlvoCalDept)){
								cal_idlvo.setPk_dept_belong(idlvoCalDept);
							}else{
								cal_idlvo.setPk_dept_belong(cal_idlvo.getPk_dept());
							}
						}
					}
				}
			}
			for (WaDeptBelongVO dlvo : allDLs) {
				int num = dlvo.getCal_num();
				for (WaDeptBelongVO cal_dlvo : allDLs) {
					String calDept = cal_dlvo.getPk_dept() == cal_dlvo.getPk_dept_belong() ? cal_dlvo.getPk_fatherorg() : cal_dlvo.getPk_dept_belong();
					if (cal_dlvo.getError_flag() == "Y" && dlvo.getPk_dept().equals(calDept)){
						if((num + cal_dlvo.getCal_num()) >= 3){
							cal_dlvo.setPk_dept_belong(dlvo.getPk_dept());
							num += cal_dlvo.getCal_num();
							cal_dlvo.setCal_num(0);
							dlvo.setCal_num(num);
							cal_dlvo.setError_flag("N");
							//���¼����T֮ǰ�ĺρ㲿�T�����ĳ����όӵĺρ㲿�T
							for (WaDeptBelongVO below_dlvo : allDLs) {
								if (cal_dlvo.getPk_dept().equals(below_dlvo.getPk_dept_belong())){
									below_dlvo.setPk_dept_belong(calDept);
								}
							}
						//���ϼ��˔���0���t�^�m���ϸ��ºρ㲿�T
						}else{
							String dlvoCalDept = dlvo.getPk_dept() == dlvo.getPk_dept_belong() ? dlvo.getPk_fatherorg() : dlvo.getPk_dept_belong();
							if(!StringUtils.isEmpty(dlvoCalDept)){
								cal_dlvo.setPk_dept_belong(dlvoCalDept);
							}else{
								cal_dlvo.setPk_dept_belong(cal_dlvo.getPk_dept());
							}
							
							
						}
					}
				}
			}
			error_count--;
		}
		
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
	private void insertDeptBelong(String type, String cyear, String cperiod, String[] buttomDeptArr)
			throws DAOException, BusinessException {
		final List<WaDeptBelongVO> list = new ArrayList<WaDeptBelongVO>();
		final String dir = type;
		String tableName = "V_WA_DEPT_CAL_IDL";
		if ("DL".equals(type)) {
			tableName = "V_WA_DEPT_CAL_DL";
		}
		String sql = " SELECT dept.pk_dept,dept.pk_fatherorg,v.num,to_number(def.code) dept_level FROM org_dept dept "
				+ "LEFT JOIN bd_defdoc def ON dept.deptlevel = def.pk_defdoc "
				+ "LEFT JOIN " + tableName
				+ " v on v.workdept = dept.pk_dept and cyear='" + cyear + "' and cperiod = '" + cperiod
				+ "' and psnclcode = '" + dir
				+ "' WHERE dept.HRCANCELED='N' and dept.enablestate = 2 and dept.pk_dept not in ('"
				+ StringUtils.join(buttomDeptArr, "','") + "')";
		getDao().executeQuery(sql, new ResultSetProcessor() {
			@Override
			public Object handleResultSet(ResultSet rs) {
				try {
					while (rs.next()) {
						WaDeptBelongVO vo = new WaDeptBelongVO();
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
			getDao().insertVOList(list);
		} else {
			throw new BusinessException("Query dept error !");
		}
	}

}
