package nc.impl.hrwa.salarysmart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.hr.utils.InSQLCreator;
import nc.ift.hrwa.salarysmart.ISalaryScalesSmartQuary;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.metadata.Field;
import nc.pub.smart.metadata.MetaData;
import nc.pub.smart.context.SmartContext;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.item.WaItemVO;
public class SalaryScalesSmartQuaryImpl implements ISalaryScalesSmartQuary {
	//固定薪资项:順序-公共薪资项目编码
	public static Map<Integer,String> FIXED_SALARY_MAP = new HashMap();
	{
		FIXED_SALARY_MAP.put(1,"2001");
		FIXED_SALARY_MAP.put(2,"2002");
		FIXED_SALARY_MAP.put(3,"2003");
		FIXED_SALARY_MAP.put(4,"2004");
		FIXED_SALARY_MAP.put(5,"2005");
		FIXED_SALARY_MAP.put(6,"2007");
	}
	//增项:順序-公共薪资项目编码
	public static Map<Integer,String> PLUS_SALARY_MAP = new HashMap();
	{
		PLUS_SALARY_MAP.put(1,"4001");
		PLUS_SALARY_MAP.put(2,"4008");
		PLUS_SALARY_MAP.put(3,"4009");
		PLUS_SALARY_MAP.put(4,"4010");
		PLUS_SALARY_MAP.put(5,"4011");
		PLUS_SALARY_MAP.put(6,"5001");
		PLUS_SALARY_MAP.put(7,"5002");
		PLUS_SALARY_MAP.put(8,"6001");
		PLUS_SALARY_MAP.put(9,"6002");
		PLUS_SALARY_MAP.put(10,"6003");
		PLUS_SALARY_MAP.put(11,"6005");
		PLUS_SALARY_MAP.put(12,"6008");
		PLUS_SALARY_MAP.put(13,"6101");
		PLUS_SALARY_MAP.put(14,"6102");
		PLUS_SALARY_MAP.put(15,"6201");
		PLUS_SALARY_MAP.put(16,"6204");
		PLUS_SALARY_MAP.put(17,"6205");
		PLUS_SALARY_MAP.put(18,"6206");
		PLUS_SALARY_MAP.put(19,"6207");
		PLUS_SALARY_MAP.put(20,"6210");
		PLUS_SALARY_MAP.put(21,"6302");
		PLUS_SALARY_MAP.put(22,"6303");
		PLUS_SALARY_MAP.put(23,"6304");
		PLUS_SALARY_MAP.put(24,"6305");
		PLUS_SALARY_MAP.put(25,"6306");
		PLUS_SALARY_MAP.put(26,"6307");
		PLUS_SALARY_MAP.put(27,"6308");
		PLUS_SALARY_MAP.put(28,"6309");
		PLUS_SALARY_MAP.put(29,"6310");
		PLUS_SALARY_MAP.put(30,"6311");
		PLUS_SALARY_MAP.put(31,"6312");
		PLUS_SALARY_MAP.put(32,"6313");
		PLUS_SALARY_MAP.put(33,"6314");
		PLUS_SALARY_MAP.put(34,"6315");
		PLUS_SALARY_MAP.put(35,"6316");
		PLUS_SALARY_MAP.put(36,"6317");
		PLUS_SALARY_MAP.put(37,"6318");
		PLUS_SALARY_MAP.put(38,"6320");
		PLUS_SALARY_MAP.put(39,"6321");
		PLUS_SALARY_MAP.put(40,"6322");
		PLUS_SALARY_MAP.put(41,"6323");
		PLUS_SALARY_MAP.put(42,"6324");
		PLUS_SALARY_MAP.put(43,"6501");
		PLUS_SALARY_MAP.put(44,"6502");
		PLUS_SALARY_MAP.put(45,"6503");
		PLUS_SALARY_MAP.put(46,"6504");
		PLUS_SALARY_MAP.put(47,"8001");
		PLUS_SALARY_MAP.put(48,"8002");
		PLUS_SALARY_MAP.put(49,"8003");
		PLUS_SALARY_MAP.put(50,"8004");
		PLUS_SALARY_MAP.put(51,"8005");
		PLUS_SALARY_MAP.put(52,"8006");
		PLUS_SALARY_MAP.put(53,"8007");
		PLUS_SALARY_MAP.put(54,"8010");
	}
	//减项:順序-公共薪资项目编码
	public static Map<Integer,String> DEL_SALARY_MAP = new HashMap();
	{
		DEL_SALARY_MAP.put(1,"f_5");
		DEL_SALARY_MAP.put(2,"2016");
		DEL_SALARY_MAP.put(3,"7224");
		DEL_SALARY_MAP.put(4,"7225");
		DEL_SALARY_MAP.put(5,"3101");
		DEL_SALARY_MAP.put(6,"3102");
		DEL_SALARY_MAP.put(7,"3103");
		DEL_SALARY_MAP.put(8,"3104");
		DEL_SALARY_MAP.put(9,"3105");
		DEL_SALARY_MAP.put(10,"3106");
		DEL_SALARY_MAP.put(11,"3107");
		DEL_SALARY_MAP.put(12,"4106");
		DEL_SALARY_MAP.put(13,"4118");
		DEL_SALARY_MAP.put(14,"4107");
		DEL_SALARY_MAP.put(15,"4119");
		DEL_SALARY_MAP.put(16,"4108");
		DEL_SALARY_MAP.put(17,"4120");
		DEL_SALARY_MAP.put(18,"4109");
		DEL_SALARY_MAP.put(19,"4121");
		DEL_SALARY_MAP.put(20,"4110");
		DEL_SALARY_MAP.put(21,"4122");
		DEL_SALARY_MAP.put(22,"4111");
		DEL_SALARY_MAP.put(23,"4123");
		DEL_SALARY_MAP.put(24,"4113");
		DEL_SALARY_MAP.put(25,"4125");
		DEL_SALARY_MAP.put(26,"4133");
		DEL_SALARY_MAP.put(27,"7001");
		DEL_SALARY_MAP.put(28,"7002");
		DEL_SALARY_MAP.put(29,"7201");
		DEL_SALARY_MAP.put(30,"7202");
		DEL_SALARY_MAP.put(31,"7203");
		DEL_SALARY_MAP.put(32,"7204");
		DEL_SALARY_MAP.put(33,"7205");
		DEL_SALARY_MAP.put(34,"7206");
		DEL_SALARY_MAP.put(35,"7207");
		DEL_SALARY_MAP.put(36,"7208");
		DEL_SALARY_MAP.put(37,"7221");
		DEL_SALARY_MAP.put(38,"7222");
		DEL_SALARY_MAP.put(39,"7223");
		DEL_SALARY_MAP.put(40,"7209");
		DEL_SALARY_MAP.put(41,"7210");
		DEL_SALARY_MAP.put(42,"7211");
		DEL_SALARY_MAP.put(43,"7212");
		DEL_SALARY_MAP.put(44,"7214");
		DEL_SALARY_MAP.put(45,"7215");
		DEL_SALARY_MAP.put(46,"7216");
		DEL_SALARY_MAP.put(47,"7217");
		DEL_SALARY_MAP.put(48,"7218");
		DEL_SALARY_MAP.put(49,"7219");
		DEL_SALARY_MAP.put(50,"7224");
		DEL_SALARY_MAP.put(51,"7226");
		DEL_SALARY_MAP.put(52,"7227");
		DEL_SALARY_MAP.put(53,"7228");
		DEL_SALARY_MAP.put(54,"7229");
		DEL_SALARY_MAP.put(55,"7501");
		DEL_SALARY_MAP.put(56,"7502");
		DEL_SALARY_MAP.put(57,"7503");
		DEL_SALARY_MAP.put(58,"7504");
		DEL_SALARY_MAP.put(59,"7505");
		DEL_SALARY_MAP.put(60,"7506");
		DEL_SALARY_MAP.put(61,"7507");
		DEL_SALARY_MAP.put(62,"7508");
		DEL_SALARY_MAP.put(63,"7509");
		DEL_SALARY_MAP.put(64,"7510");
		DEL_SALARY_MAP.put(65,"7511");
		DEL_SALARY_MAP.put(66,"7512");
		DEL_SALARY_MAP.put(67,"7513");
		DEL_SALARY_MAP.put(68,"7514");
	}
	//加班项:順序-公共薪资项目编码
	public static Map<Integer,String> OVERTIME_SALARY_MAP = new HashMap();
	{
		OVERTIME_SALARY_MAP.put(1,"4002");
		OVERTIME_SALARY_MAP.put(2,"4004");
		OVERTIME_SALARY_MAP.put(3,"4193");
		OVERTIME_SALARY_MAP.put(4,"4194");
	}
	//请假项:順序-公共薪资项目编码
	public static Map<Integer,String> LEAVE_SALARY_MAP = new HashMap();
	{
		LEAVE_SALARY_MAP.put(1,"4101");
		LEAVE_SALARY_MAP.put(2,"4102");
		LEAVE_SALARY_MAP.put(3,"4136");
		LEAVE_SALARY_MAP.put(4,"4137");
		LEAVE_SALARY_MAP.put(5,"4138");
		LEAVE_SALARY_MAP.put(6,"4139");
		LEAVE_SALARY_MAP.put(7,"4140");
		LEAVE_SALARY_MAP.put(8,"4142");
		LEAVE_SALARY_MAP.put(9,"4143");
		LEAVE_SALARY_MAP.put(10,"4144");
	}
	private BaseDAO baseDAO = new BaseDAO();
	
	

	public DataSet quarySalaryScales(SmartContext context,boolean isEN)throws BusinessException{
		String org = (String) context.getParameterValue("org");
		String year = (String) context.getParameterValue("year");
		String month = (String) context.getParameterValue("month");
		int dynamicsCounts = FIXED_SALARY_MAP.size() + PLUS_SALARY_MAP.size() + DEL_SALARY_MAP.size()
				+ OVERTIME_SALARY_MAP.size() + LEAVE_SALARY_MAP.size() ;
		DataSet result = new DataSet();
		//获取所有薪资项 eg 2001->f_338
		Map<String,String> itemNameCodeMap = getAllWaItem(org);
		String sqlStr = getSQL(itemNameCodeMap,isEN,org,year,month);
		List<Map<String,Object>> resultMapList = 
				(List<Map<String,Object>>)baseDAO.executeQuery(sqlStr, new MapListProcessor());
		if(resultMapList == null){
			return result;
		}
		MetaData metaData = builderMetaData();
		result.setMetaData(metaData);
		Object [][] objs = new Object[resultMapList.size()][dynamicsCounts*2+14];
		int i = 0;
		//中英文字段名
		/**
		 * <固定/加/减/加班/请假项,<公共薪资项目code->中/英文名称>>
		 *  resultMap.put("FIXED_SALARY", fixedSalaryCodeNameMap);
		 * 	resultMap.put("PLUS_SALARY", plusSalaryCodeNameMap);
		 * 	resultMap.put("DEL_SALARY", delSalaryCodeNameMap);
		 * 	resultMap.put("OVERTIME_SALARY", overtimeSalaryCodeNameMap);
		 * 	resultMap.put("LEAVE_SALARY", leaveSalaryCodeNameMap);
		 */
		Map<String, Map<String, String>> newsRowNameMap = getNewsRowNameMap(isEN,org);
		for(Map<String,Object> row : resultMapList){
			objs[i][0] = row.get("pk_psndoc");
			objs[i][1] = row.get("code");
			objs[i][2] = row.get("name");
			objs[i][3] = row.get("pk_wa_class");
			objs[i][4] = row.get("pk_group");
			objs[i][5] = row.get("pk_org");
			objs[i][6] = row.get("due_amount");
			objs[i][7] = row.get("deduction");
			objs[i][8] = row.get("labor_retirecomp");
			objs[i][9] = row.get("total");
			objs[i][10] = row.get("pk_bankaccbas1");
			objs[i][11] = row.get("deptcode");
			objs[i][12] = row.get("deptname");
			objs[i][13] = row.get("cpaydate");
			//開始構造動態列
			//获取值 
			//Map<FIXED_SALARY/PLUS_SALARY/DEL_SALARY/OVERTIME_SALARY/LEAVE_SALARY,List<Object>>
			Map<String,List<Object>> newsColunmMap = getNewsRow(row, newsRowNameMap);
			//进行动态列赋值(一列名称+一列值)
			
			if(newsColunmMap!=null){
				int j = 14;
				if(newsColunmMap.get("FIXED_SALARY")!=null
						&&newsColunmMap.get("FIXED_SALARY").size() > 0){
					int realcout = 0;
					for(Object value : newsColunmMap.get("FIXED_SALARY")){
						objs[i][j] = value;
						j++;
						realcout++;
					}
				}
				j = 26;
				if(newsColunmMap.get("PLUS_SALARY")!=null
						&&newsColunmMap.get("PLUS_SALARY").size() > 0){
					for(Object value : newsColunmMap.get("PLUS_SALARY")){
						objs[i][j] = value;
						j++;
					}
				}
				j = 134;
				if(newsColunmMap.get("DEL_SALARY")!=null
						&&newsColunmMap.get("DEL_SALARY").size() > 0){
					for(Object value : newsColunmMap.get("DEL_SALARY")){
						objs[i][j] = value;
						j++;
					}
				}
				j = 270;
				if(newsColunmMap.get("OVERTIME_SALARY")!=null
						&&newsColunmMap.get("OVERTIME_SALARY").size() > 0){
					for(Object value : newsColunmMap.get("OVERTIME_SALARY")){
						objs[i][j] = value;
						j++;
					}
				}
				j = 278;
				if(newsColunmMap.get("LEAVE_SALARY")!=null
						&&newsColunmMap.get("LEAVE_SALARY").size() > 0){
					for(Object value : newsColunmMap.get("LEAVE_SALARY")){
						objs[i][j] = value;
						j++;
					}
				}
				
			}
			i++;
		}
		result.setDatas(objs);
		return result;
	}
	/**
	 * 
	 * @param itemNameCodeMap 所有薪资项 eg 2001->f_338
	 * @param isEN
	 * @param org
	 * @param year
	 * @param month
	 * @return
	 */
	private String getSQL(Map<String, String> itemNameCodeMap,boolean isEN
			,String org,String year,String month) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT wa_data.pk_psndoc pk_psndoc, bd_psndoc.code code, ")
				.append (isEN ? " bd_psndoc.name3 name, ":" bd_psndoc.name name, ")
				.append (" wa_waclass.pk_wa_class pk_wa_class, ")
				.append (" wa_data.pk_group pk_group, ")
				.append (" wa_data.pk_org pk_org, ");
				//加入薪资条项目-做了個轉換,因為純數字不能做列名
				for(String name : itemNameCodeMap.keySet()){
					sb.append ( " SALARY_DECRYPT ( wa_data."+itemNameCodeMap.get(name)+" ) itemkey_"+name+", ");
				}
				sb.append ( " SALARY_DECRYPT ( wa_data.f_1 ) due_amount, ")
				.append ( " SALARY_DECRYPT ( wa_data.f_2 ) deduction, ")
				.append ( " SALARY_DECRYPT ( wa_data.f_29 ) labor_retirecomp, ")
				.append ( " SALARY_DECRYPT ( wa_data.f_3 ) total, ")
				.append ( " bas.accnum pk_bankaccbas1, ")
				//.append ( " wa_data_1.pk_bankaccbas1 pk_bankaccbas1, ")
				.append ( " org_dept.code deptcode, ")
				.append ( " org_dept.name deptname, ")
				.append ( " SUBSTR ( wa_data_1.cpaydate, 1, 10 ) cpaydate ")
				.append ( " FROM ")
				.append ( " bd_psndoc bd_psndoc ")
				.append ( " LEFT OUTER JOIN ")
				.append ( " wa_data wa_data ")
				.append ( " ON ")
				.append ( " bd_psndoc.pk_psndoc = wa_data.pk_psndoc ")
				.append ( " LEFT OUTER JOIN ")
				.append ( " wa_waclass wa_waclass ")
				.append ( " ON ")
				.append ( " wa_data.pk_wa_class = wa_waclass.pk_wa_class ")
				.append ( " LEFT OUTER JOIN ")
				.append ( " wa_data wa_data_1 ")
				.append ( " ON ")
				.append ( " wa_data.pk_wa_data = wa_data_1.pk_wa_data ")
				.append ( " LEFT OUTER JOIN ")
				.append ( " hi_psnjob hi_psnjob ")
				.append ( " ON ")
				.append ( " wa_data.pk_psnjob = hi_psnjob.pk_psnjob ")
				.append ( " LEFT OUTER JOIN ")
				.append ( " org_dept org_dept ")
				.append ( " ON	")
				.append ( " hi_psnjob.pk_dept = org_dept.pk_dept ")
				.append ( " left join bd_psnbankacc acc on acc.pk_psndoc = wa_data.pk_psndoc ")
				.append ( " left join bd_bankaccbas bas on bas.pk_bankaccbas=acc.pk_bankaccbas ")
				.append ( " WHERE	")
				.append ( " acc.payacc =1 ")
				.append ( " AND ( wa_waclass.code = 'TWMONTH'	")
				.append ( " AND wa_data.pk_org = '"+org+"' ")
				.append ( " AND wa_data.cyear = '"+year+"'	")
				.append ( " AND wa_data.cperiod = '"+month+"' ) ");
		return sb.toString();
	}
	/**
	 * 获取所有薪资项 eg 2001->f_338
	 * @param pk_org
	 * @return
	 * @throws BusinessException 
	 */
	private Map<String, String> getAllWaItem(String pk_org) throws BusinessException {
		Map<String, String> resultMap = new HashMap();
		//获取需要的薪资项
		InSQLCreator insql = new InSQLCreator();
		Set<String> colSet = new HashSet();
		colSet.addAll(FIXED_SALARY_MAP.values());
		colSet.addAll(PLUS_SALARY_MAP.values());
		colSet.addAll(DEL_SALARY_MAP.values());
		colSet.addAll(OVERTIME_SALARY_MAP.values());
		colSet.addAll(LEAVE_SALARY_MAP.values());
		String colInSQL = insql.getInSQL(colSet.toArray(new String[0]));
		
		String sql = " select wa_item.code code ,wa_item.itemkey itemkey from wa_item where "
	     + " ( wa_item.pk_org = 'GLOBLE00000000000000' OR  "
	     + " wa_item.pk_org = (select distinct pk_group from org_orgs "
	     + " where pk_org = '"+pk_org+"' and dr = 0 ))"
	     + " and wa_item.code in ("+colInSQL+")";
		List<WaItemVO> colList = 
				(List<WaItemVO>)baseDAO.executeQuery(sql, new BeanListProcessor(WaItemVO.class));
		if(colList != null && colList.size() > 0){
			for(WaItemVO vo : colList){
				if(vo!= null && vo.getItemkey()!=null && vo.getCode()!=null){
					resultMap.put(vo.getCode(), vo.getItemkey());
				}
			}
		}
	    
		return resultMap;
	}
	/**
	 * 构造中英文字段名 
	 * @param isEN 是否英文
	 * @return <固定/加/减/加班/请假项,<公共薪资项目code->中/英文名称>>
	 *  resultMap.put("FIXED_SALARY", fixedSalaryCodeNameMap);
	 * 	resultMap.put("PLUS_SALARY", plusSalaryCodeNameMap);
	 * 	resultMap.put("DEL_SALARY", delSalaryCodeNameMap);
	 * 	resultMap.put("OVERTIME_SALARY", overtimeSalaryCodeNameMap);
	 * 	resultMap.put("LEAVE_SALARY", leaveSalaryCodeNameMap);
	 * @throws BusinessException 
	 */
	public  Map<String, Map<String,String>> getNewsRowNameMap(boolean isEN,String pk_org) throws BusinessException {
		Map<String, Map<String,String>> resultMap = new HashMap();
		Map<String, String> fixedSalaryCodeNameMap = new HashMap();
		Map<String, String> plusSalaryCodeNameMap = new HashMap();
		Map<String, String> delSalaryCodeNameMap = new HashMap();
		Map<String, String> overtimeSalaryCodeNameMap = new HashMap();
		Map<String, String> leaveSalaryCodeNameMap = new HashMap();
		resultMap.put("FIXED_SALARY", fixedSalaryCodeNameMap);
		resultMap.put("PLUS_SALARY", plusSalaryCodeNameMap);
		resultMap.put("DEL_SALARY", delSalaryCodeNameMap);
		resultMap.put("OVERTIME_SALARY", overtimeSalaryCodeNameMap);
		resultMap.put("LEAVE_SALARY", leaveSalaryCodeNameMap);
		//获取需要的薪资项
		InSQLCreator insql = new InSQLCreator();
		Set<String> colSet = new HashSet();
		colSet.addAll(FIXED_SALARY_MAP.values());
		colSet.addAll(PLUS_SALARY_MAP.values());
		colSet.addAll(DEL_SALARY_MAP.values());
		colSet.addAll(OVERTIME_SALARY_MAP.values());
		colSet.addAll(LEAVE_SALARY_MAP.values());
		String colInSQL = insql.getInSQL(colSet.toArray(new String[0]));
		
		String sql = " select wa_item.code code ,"+(isEN?"wa_item.name3":"wa_item.name2")+" name from wa_item where "
	     + " ( wa_item.pk_org = 'GLOBLE00000000000000' OR  "
	     + " wa_item.pk_org = (select distinct pk_group from org_orgs "
	     + " where pk_org = '"+pk_org+"' and dr = 0 ))"
	     + " and wa_item.code in ("+colInSQL+")";
		List<WaItemVO> colList = 
				(List<WaItemVO>)baseDAO.executeQuery(sql, new BeanListProcessor(WaItemVO.class));
		if(colList != null && colList.size() > 0){
			for(WaItemVO vo : colList){
				if(vo!= null && vo.getCode()!=null){
					if(FIXED_SALARY_MAP.containsValue(vo.getCode())){
						fixedSalaryCodeNameMap.put(vo.getCode(), vo.getName());
					}else if(PLUS_SALARY_MAP.containsValue(vo.getCode())){
						plusSalaryCodeNameMap.put(vo.getCode(), vo.getName());
					}else if(DEL_SALARY_MAP.containsValue(vo.getCode())){
						delSalaryCodeNameMap.put(vo.getCode(), vo.getName());
					}else if(OVERTIME_SALARY_MAP.containsValue(vo.getCode())){
						overtimeSalaryCodeNameMap.put(vo.getCode(), vo.getName());
					}else if(LEAVE_SALARY_MAP.containsValue(vo.getCode())){
						leaveSalaryCodeNameMap.put(vo.getCode(), vo.getName());
					}
				}
			}
		}

		return resultMap;
	}
	
	/**
	 
	 */
	/**
	 * 获取动态列的值,不为空且不为0才获取,否则舍弃
	 * @param row
	 * @param newsRowNameMap
	 *  <固定/加/减/加班/请假项,<公共薪资项目code->中/英文名称>>
	 *  resultMap.put("FIXED_SALARY", fixedSalaryCodeNameMap);
	 * 	resultMap.put("PLUS_SALARY", plusSalaryCodeNameMap);
	 * 	resultMap.put("DEL_SALARY", delSalaryCodeNameMap);
	 * 	resultMap.put("OVERTIME_SALARY", overtimeSalaryCodeNameMap);
	 * 	resultMap.put("LEAVE_SALARY", leaveSalaryCodeNameMap);
	 * @return 各个动态列的值
	 * 	Map<FIXED_SALARY/PLUS_SALARY/DEL_SALARY/OVERTIME_SALARY/LEAVE_SALARY,List<Object>>
	 * @throws BusinessException 
	 */
	private  Map<String,List<Object>> getNewsRow(Map<String, Object> row ,Map<String,Map<String,String>> newsRowNameMap) throws BusinessException {
		Map<String,List<Object>> rsMap = new HashMap();
		List<Object> fixedColunm = new ArrayList<>();
		Map<String,String> fixedNameMap = newsRowNameMap.get("FIXED_SALARY");
		List<Object> plusColunm = new ArrayList<>();
		Map<String,String> plusNameMap = newsRowNameMap.get("PLUS_SALARY");
		List<Object> delColunm = new ArrayList<>();
		Map<String,String> delNameMap = newsRowNameMap.get("DEL_SALARY");
		List<Object> overtimeColunm = new ArrayList<>();
		Map<String,String> overtimeNameMap = newsRowNameMap.get("OVERTIME_SALARY");
		List<Object> leaveColunm = new ArrayList<>();
		Map<String,String> leaveNameMap = newsRowNameMap.get("LEAVE_SALARY");
		rsMap.put("FIXED_SALARY", fixedColunm);
		rsMap.put("PLUS_SALARY", plusColunm);
		rsMap.put("DEL_SALARY", delColunm);
		rsMap.put("OVERTIME_SALARY", overtimeColunm);
		rsMap.put("LEAVE_SALARY", leaveColunm);
		try{
			//固定薪資項
			for(String itemKey : FIXED_SALARY_MAP.values()){
				if(row.get("itemkey_"+itemKey)!=null 
						&& new UFDouble(String.valueOf(row.get("itemkey_"+itemKey))).abs().doubleValue() > 0.0001){
					fixedColunm.add(fixedNameMap.get(itemKey));
					fixedColunm.add(row.get("itemkey_"+itemKey));
				}
			}
			//加項
			for(String itemKey : PLUS_SALARY_MAP.values()){
				if(row.get("itemkey_"+itemKey)!=null 
						&& new UFDouble(String.valueOf(row.get("itemkey_"+itemKey))).abs().doubleValue() > 0.0001){
					plusColunm.add(plusNameMap.get(itemKey));
					plusColunm.add(row.get("itemkey_"+itemKey));
				}
			}
			//減項
			for(String itemKey : DEL_SALARY_MAP.values()){
				if(row.get("itemkey_"+itemKey)!=null 
						&& new UFDouble(String.valueOf(row.get("itemkey_"+itemKey))).abs().doubleValue() > 0.0001){
					delColunm.add(delNameMap.get(itemKey));
					delColunm.add(row.get("itemkey_"+itemKey));
				}
			}
			//加班項
			for(String itemKey : OVERTIME_SALARY_MAP.values()){
				if(row.get("itemkey_"+itemKey)!=null 
						&& new UFDouble(String.valueOf(row.get("itemkey_"+itemKey))).abs().doubleValue() > 0.0001){
					overtimeColunm.add(overtimeNameMap.get(itemKey));
					overtimeColunm.add(row.get("itemkey_"+itemKey));
				}
			}
			//請假項
			for(String itemKey : LEAVE_SALARY_MAP.values()){
				if(row.get("itemkey_"+itemKey)!=null 
						&& new UFDouble(String.valueOf(row.get("itemkey_"+itemKey))).abs().doubleValue() > 0.0001){
					leaveColunm.add(leaveNameMap.get(itemKey));
					leaveColunm.add(row.get("itemkey_"+itemKey));
				}
			}
		}catch(Exception e){
			throw new BusinessException("人員:["+row.get("code")+"]薪資數據不可識別!");
		}
		
		
		
		
		
		
		return rsMap;
	}
	/**
	 * 构造动态列
	 * 0-13 基本信息
	 * 14-19  固定薪资项
	 * 20-73 增项
	 * 74-141 减项
	 * 142-145 加班项
	 * 146-155 请假项
	 * @return
	 */
	private MetaData builderMetaData() {
		MetaData metaData = new MetaData();
		
		Field fld = new Field();
		fld.setCaption("人員基本信息");
		fld.setFldname("pk_psndoc");
		fld.setDbColumnType(12);
		fld.setPrecision(100);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("人員編碼");
		fld.setFldname("code");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("姓名");
		fld.setFldname("name");
		fld.setPrecision(300);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("薪資方案主鍵");
		fld.setFldname("pk_wa_class");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("所屬集團");
		fld.setFldname("pk_group");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("所屬組織");
		fld.setFldname("pk_org");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("應發合計");
		fld.setFldname("Due_Amount");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("應扣合計");
		fld.setFldname("Deduction");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("勞退公司提發");
		fld.setFldname("Labor_RetireComp");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("實發合計");
		fld.setFldname("TOTAL");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("銀行賬號1");
		fld.setFldname("pk_bankaccbas1");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("編碼");
		fld.setFldname("deptcode");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("名稱");
		fld.setFldname("deptname");
		fld.setPrecision(300);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		fld = new Field();
		fld.setCaption("發放日期");
		fld.setFldname("cpaydate");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] {fld});
		
		for(int i = 1 ; i<= FIXED_SALARY_MAP.size() ;i++){
			fld = new Field();
			fld.setCaption("固定薪資項名稱"+i);
			fld.setFldname("fixedglbdef"+i);
			fld.setPrecision(100);
			fld.setDbColumnType(12);
			metaData.addField(new Field[] {fld});
			
			fld = new Field();
			fld.setCaption("固定薪資項value"+i);
			fld.setFldname("fixedglbdefvalue"+i);
			fld.setPrecision(100);
			fld.setDbColumnType(12);
			metaData.addField(new Field[] {fld});
		}
		
		for(int i = 1 ; i<= PLUS_SALARY_MAP.size() ;i++){
			fld = new Field();
			fld.setCaption("增項名稱"+i);
			fld.setFldname("plusglbdef"+i);
			fld.setPrecision(100);
			fld.setDbColumnType(12);
			metaData.addField(new Field[] {fld});
			
			fld = new Field();
			fld.setCaption("增項value"+i);
			fld.setFldname("plusglbdefvalue"+i);
			fld.setPrecision(100);
			fld.setDbColumnType(12);
			metaData.addField(new Field[] {fld});
		}

		for(int i = 1 ; i<= DEL_SALARY_MAP.size() ;i++){
			fld = new Field();
			fld.setCaption("減項名稱"+i);
			fld.setFldname("delglbdef"+i);
			fld.setPrecision(100);
			fld.setDbColumnType(12);
			metaData.addField(new Field[] {fld});
			
			fld = new Field();
			fld.setCaption("減項value"+i);
			fld.setFldname("delglbdefvalue"+i);
			fld.setPrecision(100);
			fld.setDbColumnType(12);
			metaData.addField(new Field[] {fld});
		}
		
		for(int i = 1 ; i<= OVERTIME_SALARY_MAP.size() ;i++){
			fld = new Field();
			fld.setCaption("加班項名稱"+i);
			fld.setFldname("overtimeglbdef"+i);
			fld.setPrecision(100);
			fld.setDbColumnType(12);
			metaData.addField(new Field[] {fld});
			
			fld = new Field();
			fld.setCaption("加班項value"+i);
			fld.setFldname("overtimeglbdefvalue"+i);
			fld.setPrecision(100);
			fld.setDbColumnType(12);
			metaData.addField(new Field[] {fld});
		}
		
		for(int i = 1 ; i<= LEAVE_SALARY_MAP.size() ;i++){
			fld = new Field();
			fld.setCaption("請假名稱"+i);
			fld.setFldname("leaveglbdef"+i);
			fld.setPrecision(100);
			fld.setDbColumnType(12);
			metaData.addField(new Field[] {fld});
			
			fld = new Field();
			fld.setCaption("請假value"+i);
			fld.setFldname("leaveglbdefvalue"+i);
			fld.setPrecision(100);
			fld.setDbColumnType(12);
			metaData.addField(new Field[] {fld});
		}
		
		
		
		
		
		return metaData;
	}
	

}
