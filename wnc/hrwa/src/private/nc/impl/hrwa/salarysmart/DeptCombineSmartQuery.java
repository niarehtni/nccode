package nc.impl.hrwa.salarysmart;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.ift.hrwa.salarysmart.IDeptCombineSmartQuery;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.metadata.Field;
import nc.pub.smart.metadata.MetaData;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFLiteralDate;

public class DeptCombineSmartQuery implements IDeptCombineSmartQuery {
	
	private SmartQueryUtils utils = new SmartQueryUtils();
	
	private static final String IDL = "IDL";
	
	private static final String DL = "DL";
	
	private static final String REGEX = "::";
	
	/**
	 * 部门合并的最小单位(部 : 70)
	 */
	private static final int MIN_LEVEL = 70;
	
	/**
	 * 参数-人员类别
	 */
	private static final String PARAMTER_PSN_TYPE = "psnType";

	@Override
	public DataSet deptCombine(String pk_org,String pk_wa_class,String startPeriod,String endPeriod,Double source,String psnType)
			throws BusinessException {
		List<DataDTO> rsList = new ArrayList<>();
		
		deptCombine(rsList,pk_org,pk_wa_class,startPeriod,endPeriod,source,psnType);
		
		return processResult(rsList,pk_wa_class,source);
	}

	private List<DataDTO> deptCombine(List<DataDTO> rsList,String pk_org,String pk_wa_class,String startPeriod,String endPeriod,Double source,String psnType) throws BusinessException{
		
		if(pk_org==null||startPeriod==null||endPeriod==null||
				pk_org.equals("")||startPeriod.equals("")||endPeriod.equals("")){
			return rsList;
		}
		if(source==0&&(pk_wa_class==null ||pk_wa_class.trim().equals(""))){
			throw new BusinessException("來源為薪資時,請輸入薪資方案!");
		}
		Set<String> psnTyps = new HashSet<>();

		//是否查询了特定的人员类别
		if(source == 0){
			if(psnType!=null && psnType.trim().length() > 0){
				psnTyps.add(psnType);
			}else{
				psnTyps.add(IDL);
				psnTyps.add(DL);
			}
		}else{
			//二代健保的人员别及以下规则,按IDL进行
			psnTyps.add(IDL);
		}
		
		//按月份进行循环
		Set<String> periods = getPeriodApart(startPeriod,endPeriod);
		for(String period : periods){
			//按人员类别进行合并
			for(String type : psnTyps){
				String[] periodApart = period.split(REGEX);
				Map<String,DataDTO> datas = getData(type,pk_org,pk_wa_class,periodApart[0],periodApart[1],source);
				if(datas==null){
					continue;
				}
				//月份填充 2020年10月27日21:39:22 業務是按月進行匯總的
				fillCperiod(datas,periodApart[0]);
				//获取现有编制部门
				Map<String,DataDTO> formDepts = getFormDepts(psnType,pk_org);
				
				List<DataDTO> rs = doCombine(datas,type,formDepts);
				
				rsList.addAll(rs);
			}
		}
		
		
		return rsList;
	}
	private void fillCperiod(Map<String, DataDTO> datas, String periodApart) {
		Collection<DataDTO> values = datas.values();
		String year = periodApart.substring(0,4);
		String cperiod =  periodApart.substring(4,6);
		for(DataDTO dto : values){
			if(dto.year==null){
				dto.year = year;
			}
			if(dto.period==null){
				dto.period = cperiod;
			}
		}
		
	}

	private Set<String> getPeriodApart(String startPeriod, String endPeriod) throws BusinessException {
		Set<String> rs = new HashSet<>();
		String startYear = startPeriod.substring(0, 4);
		String startmonth = startPeriod.substring(4, 6);
		
		String endYear = endPeriod.substring(0, 4);
		String endmonth = endPeriod.substring(4, 6);
		
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.set(Integer.valueOf(startYear), Integer.valueOf(startmonth)-1, 1);
		
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.set(Integer.valueOf(endYear), Integer.valueOf(endmonth)-1, 1);
		
		int count = 0;
		
		while(count <24){
			int tempStartYear = startCalendar.get(Calendar.YEAR);
			int tempStartMonth = startCalendar.get(Calendar.MONTH);
			
			int tempEndYear = endCalendar.get(Calendar.YEAR);
			int tempEndMonth = endCalendar.get(Calendar.MONTH);
			
			String startDate = new UFLiteralDate(startCalendar.getTime()).toStdString();
			String startDateStr = startDate.replaceAll("-", "").substring(0,6);
			rs.add(startDateStr+REGEX+startDateStr);
			
			if(tempStartYear==tempEndYear && tempStartMonth == tempEndMonth){
				break;
			}
			count++;
			startCalendar.add(Calendar.MONTH, 1);
			
		}
		if(count == 24){
			throw new BusinessException("請不要查詢超過24個月的訊息!");
		}
		
		return rs;
	}


	/**
	 * 合并部门
	 * @param datas 需要合并的部门
	 * @param psnType 人员类型
	 * @param formDepts 现有部门编制(全部)
	 * @return
	 * @throws BusinessException
	 */
	private List<DataDTO> doCombine(Map<String,DataDTO> datas,String psnType, Map<String, DataDTO> formDepts) throws BusinessException{
		//复制一份
		Map<String,DataDTO> oldDatas = new HashMap<>();
		oldDatas.putAll(datas);
		
		//维护部门归属映射<pk_dept_belog,List<String>> 部门pk->List<合并到该部门的下级部门,包括自己>
		Map<String,List<DataDTO>> deptBelongs = initDeptBelong(datas);
		//最下层合并到部
		combineLastDept(datas,deptBelongs,formDepts);
		//例外部门合并
		combineExceptionDept(datas,deptBelongs,formDepts);
		//合并3人一下的部门
		combineMinDept(datas,deptBelongs,formDepts,psnType);
		//向下分摊
		departHeadDept(datas,deptBelongs,psnType);
		
		//生成最終數據
		return buildResult(datas,deptBelongs,oldDatas);
	}
	
	/**
	 * 从自定义档案WNCDEPTBLG中查询例外部门,将例外部门无条件的合并到规定部门
	 * PS:如果例外部门或者规定部门不在本次发薪的部门中,则不进行此条逻辑 --by 老李 2020年10月30日15:36:42
	 * @param datas 需要合并的数据
	 * @param deptBelongs 部门合并关系 pk_dept->合并到该部门的所有部门(包括自己)
	 * @param formDepts 现有部门编制
	 * @throws BusinessException 
	 */
	private void combineExceptionDept(Map<String, DataDTO> datas,
			Map<String, List<DataDTO>> deptBelongs,
			Map<String, DataDTO> formDepts) throws BusinessException {
		//查询例外部门
		String sql = " select sourcedept.PK_DEPT sdept,targetdept.PK_DEPT tdept from BD_DEFDOC doc "
				+" inner join bd_defdoclist dlist on dlist.code = 'WNCDEPTBLG' and dlist.PK_DEFDOCLIST = doc.PK_DEFDOCLIST "
				+" left join org_dept sourcedept on sourcedept.CODE = doc.code "
				+" left join org_dept targetdept on targetdept.CODE = doc.MNECODE ";
		IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		@SuppressWarnings("unchecked")
		Map<String,String> rsMap = (Map<String, String>) bs.executeQuery(sql, new ResultSetProcessor() {
			private static final long serialVersionUID = 1L;
			private Map<String,String> rsMap = new HashMap<>();
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while(rs.next()){
					String sourceDept = rs.getString(1);
					String targetDept = rs.getString(2);
					rsMap.put(sourceDept, targetDept);
				}
				return rsMap;
			}
		});
		if(rsMap.size() > 0){
			Set<String> keySet = rsMap.keySet();
			for(String sourceDept : keySet){
				String targetDept = rsMap.get(sourceDept);
				
				DataDTO sourceDTO = datas.get(sourceDept);
				DataDTO tartgetDTO = datas.get(targetDept);
				//两个部门均存在则进行合并
				if(sourceDTO!=null && tartgetDTO!=null){

					tartgetDTO.belong_dept_nums +=sourceDTO.belong_dept_nums;
					
					//将本部门加入到上级部门的归属集合中
					deptBelongs.get(tartgetDTO.pk_dept).add(sourceDTO);
					//将本部门的归属集合加入到上级部门中
					deptBelongs.get(tartgetDTO.pk_dept).addAll(deptBelongs.get(sourceDTO.pk_dept));
					
					//删除该部门
					datas.remove(sourceDTO.pk_dept);
					deptBelongs.remove(sourceDTO.pk_dept);
				}
			}
		}
		
	}


	/**
	 * 初始化关系表 本部门->本部门
	 * @param datas
	 * @return
	 */
	private Map<String, List<DataDTO>> initDeptBelong(Map<String, DataDTO> datas) {
		Map<String, List<DataDTO>> rs = new HashMap<>();
		for(DataDTO dept: datas.values()){
			List<DataDTO> lowDepts = rs.get(dept.pk_dept);
			if(lowDepts==null){
				lowDepts = new ArrayList<>();
			}
			lowDepts.add(dept);
			rs.put(dept.pk_dept, lowDepts);
		}
		return rs;
	}

	/**
	 * 從上至下(部門級別最小到最大)檢查，如有小於等於2人的部門，則其人數合併至下一層同級之部門中，
	 * 部門編碼最小且人數不等於0的部門，如下一層部門全部都為0(不可能为0,而且一定大于2,因为第2步限制,所以只扫描一层即可)，則再往下一層。
	 * @param datas 需要拆分的部门
	 * @param deptBelongs 部门合并关系 当前部门pk->合并到当前部门的所有下级部门
	 * @throws BusinessException
	 */
	private void departHeadDept(Map<String,DataDTO> datas, Map<String, List<DataDTO>> deptBelongs,String psnType) throws BusinessException{
		//维护上下级关系上级部门->下级部门集合(当前合并后的部门架构)
		Map<String,List<DataDTO>>dept2LowDepts = new HashMap<>();
		for(DataDTO dept : datas.values()){
			List<DataDTO> list = dept2LowDepts.get(dept.pk_fatherorg);
			if(list==null){
				list = new ArrayList<>();
			}
			list.add(dept);
			dept2LowDepts.put(dept.pk_fatherorg, list);
		}
		//排序
		Collection<DataDTO> dtos = datas.values();
		DataDTO[] dtoArray = dtos.toArray(new DataDTO[0]);
		Arrays.sort(dtoArray,new DecComparator());
		
		//扫描小于 等于2人的部门
		for(DataDTO dept : dtoArray){
			if(dept.belong_dept_nums <=2&&dept2LowDepts.get(dept.pk_dept)!=null){
				//获取符合要求的下级部门
				DataDTO lowDept = getAvilableDept(dept2LowDepts.get(dept.pk_dept));
				if(lowDept==null){
					continue;
				}
				//将该部门合并到下级部门
				deptBelongs.get(lowDept.pk_dept).add(dept);
				//将改部门所属集合加入到下级部门的集合
				deptBelongs.get(lowDept.pk_dept).addAll(deptBelongs.get(dept.pk_dept));
				
				//删除该部门
				datas.remove(dept.pk_dept);
				deptBelongs.remove(dept.pk_dept);
			}
		}
		
	}
	
	/**
	 * 获取编码最小,人数不为0的部门,且部门级别小于等于70
	 * @param list 部门pks
	 * @param datas 部门详细信息
	 * @return
	 */
	private DataDTO getAvilableDept(List<DataDTO> lowDatas) {
		if(lowDatas==null||lowDatas.size() <= 0){
			return null;
		}
		DataDTO avalableDept = null;
		long minCode = Long.MAX_VALUE;
		for(DataDTO dept : lowDatas){
			long code = utils.strToLong(dept.deptcode);
			if(code<=minCode && dept.deptLevel <= MIN_LEVEL){
				avalableDept = dept;
			}
		}
		return avalableDept;
	}

	/**
	 * 人数小于等于2人的部门,合并到上级部门.(从下往上循环扫描) sum 人数
	 * 	IDL:如果上级部门不在本次发薪中,则找上级的上级,一直找到位置
	 *  DL:如果上级部门不在本次发薪中,则不再合并到上级部门
	 * @param datas 需要合并的数据
	 * @param deptBelongs 部门合并关系 pk_dept->合并到该部门的所有部门(包括自己)
	 * @param formDepts 现有部门编制
	 * @throws BusinessException
	 */
	private void combineMinDept(Map<String,DataDTO> datas, Map<String, List<DataDTO>> deptBelongs,Map<String, DataDTO> formDepts, String psnType) throws BusinessException{
		boolean hasLastDept = true;
		
		//记录不需要再往上合并的部门(DL)
		Set<String> noCombineDepts = new HashSet<>();
		
		while(hasLastDept){
			hasLastDept = false;
			
			//排序
			Collection<DataDTO> dtos = datas.values();
			DataDTO[] dtoArray = dtos.toArray(new DataDTO[0]);
			Arrays.sort(dtoArray,new AsceComparator());
			
			//扫描小于 等于2人的部门
			for(DataDTO dept : dtoArray){
				if(dept.belong_dept_nums <=2 && dept.pk_fatherorg!=null&&!noCombineDepts.contains(dept.pk_dept)){
					hasLastDept = true;
					//把该部门的人数加到上级部门
					DataDTO faDept = datas.get(dept.pk_fatherorg);
					if(faDept == null){
						if(psnType.equals(DL)){
							//DL 不再往上合并
							noCombineDepts.add(dept.pk_dept);
							continue;
						}else{
							faDept = getIDLFatherDept(dept.pk_fatherorg,datas,formDepts);
						}
					}
					if(faDept==null){
						noCombineDepts.add(dept.pk_dept);
					}
					
					faDept.belong_dept_nums +=dept.belong_dept_nums;
					
					//将本部门加入到上级部门的归属集合中
					deptBelongs.get(faDept.pk_dept).add(dept);
					//将本部门的归属集合加入到上级部门中
					deptBelongs.get(faDept.pk_dept).addAll(deptBelongs.get(dept.pk_dept));
					
					//删除该部门
					datas.remove(dept.pk_dept);
					deptBelongs.remove(dept.pk_dept);
				}
			}
		}
		
	}
	/**
	 * 寻找上级部门,如果直接上级在需要合并的部门中不存在,则在编制中找上级的上级,依次循环
	 * @param pk_fatherorg
	 * @param datas 需要合并的部门
	 * @param formDepts 现有部门编制
	 * @return
	 */
	private DataDTO getIDLFatherDept(String pk_fatherorg,
			Map<String, DataDTO> datas, Map<String, DataDTO> formDepts) {
		boolean flag = true;
		String pk_fa_loop = pk_fatherorg;
		while(flag){
			if(datas.get(pk_fa_loop)==null){
				pk_fa_loop = formDepts.get(pk_fa_loop).pk_fatherorg;
				if(pk_fa_loop==null || pk_fa_loop.equals("") || pk_fa_loop.equals("~")){
					flag = false;
					Logger.error("部门["+pk_fatherorg+"],及其上级未在本次发薪部门中....");
					continue;
				}
			}else{
				return datas.get(pk_fa_loop);
			}
		}
		return null;
	}

	/**
	 * 级别小于"部"(70)的部门全部合并到上级 (从下往上循环扫描) sum 人数
	 * DL/IDL:如果没有找到上级,则到现有编制部门中找.
	 * @param datas 需要合并的部门
	 * @param deptBelongs 部门归属关系
	 * @param formDepts 现有部门编制(全部)
	 * @throws BusinessException
	 */
	private void combineLastDept(Map<String,DataDTO> datas, Map<String, List<DataDTO>> deptBelongs, Map<String, DataDTO> formDepts) throws BusinessException{
		
		
		boolean hasLastDept = true;
		while(hasLastDept){
			hasLastDept = false;
			
			//排序
			Collection<DataDTO> dtos = datas.values();
			DataDTO[] dtoArray = dtos.toArray(new DataDTO[0]);
			Arrays.sort(dtoArray,new AsceComparator());
			
			//扫描小于 MIN_LEVEL的部门
			for(DataDTO dept : dtoArray){
				if(dept.deptLevel > MIN_LEVEL && dept.pk_fatherorg!=null && !dept.pk_fatherorg.equals(dept.pk_dept)){
					hasLastDept = true;
					
					//把该部门的人数加到上级部门
					DataDTO faDept = datas.get(dept.pk_fatherorg);
					//如果当前发薪的部门找不到,则从部门编制中找到部门,同时将编制部门放入当前部门合并的数据中
					if(faDept==null){
						faDept = formDepts.get(dept.pk_fatherorg);
						
						List<DataDTO> list = new ArrayList<DataDTO>();
						list.add(faDept);
						deptBelongs.put(dept.pk_fatherorg, list);
						datas.put(dept.pk_fatherorg, faDept);
					}
					faDept.belong_dept_nums +=dept.belong_dept_nums;
					
					//将本部门加入上级部门的归属集中
					deptBelongs.get(dept.pk_fatherorg).add(dept);
					
					//将本部门的归属集加入到上级部门
					deptBelongs.get(dept.pk_fatherorg).addAll(deptBelongs.get(dept.pk_dept));
					
					//删除该部门
					deptBelongs.remove(dept.pk_dept);
					datas.remove(dept.pk_dept);
					
				}
			}
		}
		
	}
	
	
	/**
	 * 现有部门编制(全部)
	 * @param psnType 
	 * @return
	 * @throws BusinessException 
	 */
	private Map<String, DataDTO> getFormDepts(String psnType,String pk_org) throws BusinessException {
		//获取现有编制部门
		String sql = "select dept.PK_FATHERORG pk_fatherorg, "
				+" dept.PK_DEPT PK_DEPT, "
				+" leveldoc.code levelcode, "
				+" '"+psnType+"' psntype, "
       +" dept.INNERCODE INNERCODE, "
       +" dept.code deptcode, "
       +" 0    belong_dept_nums, "
       +" dept.PK_ORG pk_org "
       +" from ORG_DEPT dept "
       +" inner join bd_defdoc leveldoc on leveldoc.PK_DEFDOC = dept.DEPTLEVEL "
       +" where dept.PK_ORG = '"+pk_org+"' ";
		
		IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		@SuppressWarnings("unchecked")
		Map<String, DataDTO> rs = (Map<String, DataDTO>) bs.executeQuery(sql, new ResultSetProcessor() {
			
			private static final long serialVersionUID = 1L;
			private Map<String, DataDTO> rsMap = new HashMap<>();
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while(rs.next()){
					DataDTO dto = new DataDTO();
					dto.pk_fatherorg = rs.getString(1);
					dto.pk_dept = rs.getString(2);
					dto.deptLevel = rs.getInt(3);
					dto.psnType = rs.getString(4);
					dto.innerCode = rs.getString(5);
					dto.deptcode = rs.getString(6);
					dto.belong_dept_nums = rs.getInt(7);
					dto.pk_org = rs.getString(8);
					
					rsMap.put(dto.pk_dept, dto);
				}
				return rsMap;
			}
		});
		return rs;
	}

	private Map<String,DataDTO> getData(String psnType,String pk_org,String pk_wa_class,String startPeriod,String endPeriod,Double source) throws BusinessException {

		//数据来源:0:薪资,1:二代健保
		Integer sourceInt = source.intValue();
		if(sourceInt == 1){
			return getDataHealth(pk_org,pk_wa_class,startPeriod,endPeriod,psnType);
		}else{
			return getDataSalary(pk_org,pk_wa_class,startPeriod,endPeriod,psnType);
		}
	}

	
	/**
	 * 二代健保的人员别按IDL
	 * @param pk_org
	 * @param pk_wa_class
	 * @param startPeriod 202009
	 * @param endPeriod
	 * @param psnType
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, DataDTO> getDataHealth(String pk_org,
			String pk_wa_class, String startPeriod, String endPeriod, String psnType) throws BusinessException{
		String sql = "select dept.PK_FATHERORG PK_FATHERORG, "
				+" t1.PK_DEPT        PK_DEPT, "
				+" leveldoc.CODE     deptlevel, "
				+" '"+psnType+"' psntype, "
				+" dept.INNERCODE    INNERCODE, "
				+" dept.code         deptcode, "
				+" t1.nums           belong_dept_nums, "
				+" dept.PK_ORG       pk_org "
				+" from (select dc.pk_dept, count(dc.PK_PSNDOC) nums "
				+" from declaration_company dc "
				+" left join org_dept dept on dept.PK_DEPT = dc.PK_DEPT "
				+" inner join bd_defdoc leveldoc on leveldoc.PK_DEFDOC = dept.DEPTLEVEL "
				+" where dc.vbdef1 = '"+pk_org+"' "
				+" and dc.PAY_DATE >= '"+startPeriod.substring(0,4)+"-"+startPeriod.substring(4,6)+"-01 00:00:00' "
				+" and dc.PAY_DATE <= '"+endPeriod.substring(0,4)+"-"+endPeriod.substring(4,6)+"-31 23:59:59' "
				+" group by dc.pk_dept) t1 "
				+" left join org_dept dept on dept.PK_DEPT = t1.PK_DEPT "
				+" inner join bd_defdoc leveldoc on leveldoc.PK_DEFDOC = dept.DEPTLEVEL ";
		
		IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		@SuppressWarnings("unchecked")
		Map<String, DataDTO> rsMap =  (Map<String, DataDTO>)bs.executeQuery(sql, new ResultSetProcessor() {
			private static final long serialVersionUID = -6443152203902127043L;
			private Map<String, DataDTO> rsMap = new HashMap<>();
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while(rs.next()){
					DataDTO vo = new DataDTO();
					vo.pk_fatherorg = rs.getString(1);
					vo.pk_dept = rs.getString(2);
					vo.deptLevel = rs.getInt(3);
					vo.psnType = rs.getString(4);
					vo.innerCode = rs.getString(5);
					vo.deptcode = rs.getString(6);
					vo.belong_dept_nums = rs.getInt(7);
					vo.pk_org = rs.getString(8);
					rsMap.put(vo.pk_dept, vo);
				}
				return rsMap;
			}
		});
		return rsMap;
	}

	private Map<String, DataDTO> getDataSalary(String pk_org,
			String pk_wa_class, String startCperiod, String endCperiod, String psnType) throws BusinessException {
		String sql = "select dept.PK_FATHERORG pk_fatherorg, "
				+ "dept.PK_DEPT pk_dept, "
				+ "leveldoc.CODE deptlevel, "
				+ "'"+psnType+"'          psntype, "
				+ "dept.INNERCODE innercode, "
				+ " dept.CODE deptcode, "
				+ "cb.nums       belong_dept_nums, "
				+ " dept.PK_ORG pk_org "
				+ "from (select job.PK_DEPT pk_dept, count(wd.PK_PSNJOB) nums "
				+ "from WA_DATA wd "
				+ "inner join hi_psnjob job on job.PK_PSNJOB = wd.PK_PSNJOB and wd.pk_org = '"+pk_org+"' and "
				+ "wd.PK_WA_CLASS = '"+pk_wa_class+"' "
				+ "inner join org_dept dept on job.PK_DEPT = dept.PK_DEPT "
				+ "inner join bd_defdoc leveldoc on leveldoc.PK_DEFDOC = dept.DEPTLEVEL "
				+ "inner join bd_psncl psntype on psntype.PK_PSNCL = job.pk_psncl "
				+ "where wd.CYEARPERIOD >= '"+startCperiod+"' "
				+ "and wd.CYEARPERIOD <= '"+endCperiod+"' "
				+ "and psntype.name like '"+psnType+"%' "
				+ "group by job.PK_DEPT) cb "
				+ "left join org_dept dept on cb.PK_DEPT = dept.PK_DEPT "
				+ "left join bd_defdoc leveldoc on leveldoc.PK_DEFDOC = dept.DEPTLEVEL ";
		
		IUAPQueryBS bs = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		@SuppressWarnings("unchecked")
		Map<String, DataDTO> rsMap =  (Map<String, DataDTO>)bs.executeQuery(sql, new ResultSetProcessor() {
			private static final long serialVersionUID = -6443152203902127043L;
			private Map<String, DataDTO> rsMap = new HashMap<>();
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while(rs.next()){
					DataDTO vo = new DataDTO();
					vo.pk_fatherorg = rs.getString(1);
					vo.pk_dept = rs.getString(2);
					vo.deptLevel = rs.getInt(3);
					vo.psnType = rs.getString(4);
					vo.innerCode = rs.getString(5);
					vo.deptcode = rs.getString(6);
					vo.belong_dept_nums = rs.getInt(7);
					vo.pk_org = rs.getString(8);
					rsMap.put(vo.pk_dept, vo);
				}
				return rsMap;
			}
		});
		return rsMap;
	}
	
	/**
	 * 从底层到上层
	 * @author Administrator
	 *
	 */
	class AsceComparator implements Comparator<DataDTO>{
		@Override
		public int compare(DataDTO o1, DataDTO o2) {
			return o2.innerCode.length()-o1.innerCode.length();
		}
		
	}
	
	/**
	 * 从上层到底层
	 * @author Administrator
	 *
	 */
	class DecComparator implements Comparator<DataDTO>{
		@Override
		public int compare(DataDTO o1, DataDTO o2) {
			return o1.innerCode.length()-o2.innerCode.length();
		}
		
	}
	/**
	 * 将结果添加到ds中
	 * @param psnType 
	 * @param pk_wa_class 
	 * @param datas
	 * @param ds
	 * @return
	 */
	private DataSet processResult(List<DataDTO> dataList, String pk_wa_class, Double source) throws BusinessException{
		
		MetaData metaData = buildMetaData();
		Object[][] datas = processData(dataList,metaData,pk_wa_class,source.intValue());
		
		DataSet ds = new DataSet();
		ds.setMetaData(metaData);
		ds.setDatas(datas);
		
		return ds;
	}
	
	/**
	 * 
	 * @param datas 合併後的數據
	 * @param deptBelongs 部門合併關係 上級部門->list<合併到該部門的部門>
	 * @param oldDatas 最原始的數據
	 * @return
	 */
	private List<DataDTO> buildResult(Map<String, DataDTO> datas,
			Map<String, List<DataDTO>> deptBelongs,
			Map<String, DataDTO> oldDatas) {
		List<DataDTO> rsList = new ArrayList<>();
		Set<String> keyset = oldDatas.keySet();
		
		//構造部門->歸屬部門對照表
		Map<String,String> combineDeptMap = buildRefMap(deptBelongs);
		
		for(String pk_dept : keyset){
			DataDTO oldDept = oldDatas.get(pk_dept);
			//獲取合併的歸屬部門
			String pk_dept_belong = combineDeptMap.get(pk_dept);
			DataDTO deptCombine = datas.get(pk_dept_belong);
			
			DataDTO newDept = new DataDTO();
			
			newDept.pk_fatherorg=oldDept.pk_fatherorg;
			newDept.pk_dept=oldDept.pk_dept;
			newDept.pk_dept_belong=pk_dept_belong;
			newDept.deptLevel=oldDept.deptLevel;
			newDept.psnType=oldDept.psnType;
			newDept.belong_dept_nums=deptCombine.belong_dept_nums;
			newDept.pk_org = oldDept.pk_org;
			
			newDept.year = oldDept.year;
			newDept.period = oldDept.period;
			rsList.add(newDept);
		}
		return rsList;
	}
	
	private Map<String, String> buildRefMap(
			Map<String, List<DataDTO>> deptBelongs) {
		Map<String,String> rsMap = new HashMap<>();
		Set<String> keySet = deptBelongs.keySet();
		for(String pk_dept_belong : keySet){
			List<DataDTO> list = deptBelongs.get(pk_dept_belong);
			for(DataDTO dept : list){
				rsMap.put(dept.pk_dept, pk_dept_belong);
			}
		}
		return rsMap;
	}
	/**
	 * 处理结果
	 * 当来源为二代健保是,情况薪资方案和人员别字段
	 * @param dataList
	 * @param metaData
	 * @param pk_wa_class
	 * @param source
	 * @return
	 */
	private Object[][] processData(List<DataDTO> dataList, MetaData metaData, String pk_wa_class, int source) {
		Object[][] rs = new Object[dataList.size()][metaData.getFieldNum()];
		//按時間 排個序
		DataDTO[] datas = dataList.toArray(new DataDTO[0]);
		Arrays.sort(datas, new Comparator<DataDTO>(){

			@Override
			public int compare(DataDTO o1, DataDTO o2) {
				String year1 = o1.year;
				String period1 = o1.period;
				int num1 = Integer.valueOf(year1+period1);
				
				String year2 = o2.year;
				String period2 = o2.period;
				Integer num2 = Integer.valueOf(year2+period2);
				
				return num1-num2;
			}
			
		});
		if(dataList.size() <=0){
			return rs;
		}
		for(int i =0;i<datas.length;i++){
			//本行的數據
			DataDTO dept = dataList.get(i);
			
			rs[i][0] = dept.pk_fatherorg;
			rs[i][1] = dept.pk_dept;
			rs[i][2] = dept.pk_dept_belong;
			rs[i][3] = dept.deptLevel;
			
			rs[i][5] = dept.belong_dept_nums;
			rs[i][6] = dept.pk_org;
			
			rs[i][8] = dept.year;
			rs[i][9] = dept.period;
			
			if(source!=1){
				rs[i][4] = dept.psnType;
				rs[i][7] = pk_wa_class;
			}
			
			
		}
		return rs;
	}

	private MetaData buildMetaData() {
		MetaData meta = new MetaData();
		
		Field field = new Field();
		field.setCaption("上級部門主鍵");
		field.setFldname("pk_fatherorg");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);

		field = new Field();
		field.setCaption("部門主鍵");
		field.setFldname("pk_dept");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);

		field = new Field();
		field.setCaption("歸屬部門主鍵");
		field.setFldname("pk_dept_belong");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);

		field = new Field();
		field.setCaption("部門級別");
		field.setFldname("dept_level");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);

		field = new Field();
		field.setCaption("人員類別");
		field.setFldname("dir");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);
		
		field = new Field();
		field.setCaption("归属部门人数");
		field.setFldname("belong_dept_nums");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);
		
		field = new Field();
		field.setCaption("組織");
		field.setFldname("pk_org");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);
		
		field = new Field();
		field.setCaption("薪资方案");
		field.setFldname("pk_wa_class");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);
		
		field = new Field();
		field.setCaption("年");
		field.setFldname("year");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);
		
		field = new Field();
		field.setCaption("月");
		field.setFldname("month");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);
		
		return meta;
	}

	/**
	 * 数据存储
	 * @author Administrator
	 *
	 */
	class DataDTO extends SuperVO {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2274347443685214161L;
		public String pk_org;
		public String pk_fatherorg;
		public String pk_dept;
		public Integer deptLevel;
		public String psnType;
		public String innerCode;
		public String deptcode;
		public String pk_dept_belong;
		public String year;
		public String period;
		/**
		 * 当前部门人数(可能合并了下级部门)
		 */
		public Integer belong_dept_nums = 0;
	}

}
