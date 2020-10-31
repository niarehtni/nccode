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
	 * ���źϲ�����С��λ(�� : 70)
	 */
	private static final int MIN_LEVEL = 70;
	
	/**
	 * ����-��Ա���
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
			throw new BusinessException("��Դ��н�Y�r,Ոݔ��н�Y����!");
		}
		Set<String> psnTyps = new HashSet<>();

		//�Ƿ��ѯ���ض�����Ա���
		if(source == 0){
			if(psnType!=null && psnType.trim().length() > 0){
				psnTyps.add(psnType);
			}else{
				psnTyps.add(IDL);
				psnTyps.add(DL);
			}
		}else{
			//������������Ա�����¹���,��IDL����
			psnTyps.add(IDL);
		}
		
		//���·ݽ���ѭ��
		Set<String> periods = getPeriodApart(startPeriod,endPeriod);
		for(String period : periods){
			//����Ա�����кϲ�
			for(String type : psnTyps){
				String[] periodApart = period.split(REGEX);
				Map<String,DataDTO> datas = getData(type,pk_org,pk_wa_class,periodApart[0],periodApart[1],source);
				if(datas==null){
					continue;
				}
				//�·���� 2020��10��27��21:39:22 �I���ǰ����M�ЅR����
				fillCperiod(datas,periodApart[0]);
				//��ȡ���б��Ʋ���
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
			throw new BusinessException("Ո��Ҫ��ԃ���^24���µ�ӍϢ!");
		}
		
		return rs;
	}


	/**
	 * �ϲ�����
	 * @param datas ��Ҫ�ϲ��Ĳ���
	 * @param psnType ��Ա����
	 * @param formDepts ���в��ű���(ȫ��)
	 * @return
	 * @throws BusinessException
	 */
	private List<DataDTO> doCombine(Map<String,DataDTO> datas,String psnType, Map<String, DataDTO> formDepts) throws BusinessException{
		//����һ��
		Map<String,DataDTO> oldDatas = new HashMap<>();
		oldDatas.putAll(datas);
		
		//ά�����Ź���ӳ��<pk_dept_belog,List<String>> ����pk->List<�ϲ����ò��ŵ��¼�����,�����Լ�>
		Map<String,List<DataDTO>> deptBelongs = initDeptBelong(datas);
		//���²�ϲ�����
		combineLastDept(datas,deptBelongs,formDepts);
		//���ⲿ�źϲ�
		combineExceptionDept(datas,deptBelongs,formDepts);
		//�ϲ�3��һ�µĲ���
		combineMinDept(datas,deptBelongs,formDepts,psnType);
		//���·�̯
		departHeadDept(datas,deptBelongs,psnType);
		
		//������K����
		return buildResult(datas,deptBelongs,oldDatas);
	}
	
	/**
	 * ���Զ��嵵��WNCDEPTBLG�в�ѯ���ⲿ��,�����ⲿ���������ĺϲ����涨����
	 * PS:������ⲿ�Ż��߹涨���Ų��ڱ��η�н�Ĳ�����,�򲻽��д����߼� --by ���� 2020��10��30��15:36:42
	 * @param datas ��Ҫ�ϲ�������
	 * @param deptBelongs ���źϲ���ϵ pk_dept->�ϲ����ò��ŵ����в���(�����Լ�)
	 * @param formDepts ���в��ű���
	 * @throws BusinessException 
	 */
	private void combineExceptionDept(Map<String, DataDTO> datas,
			Map<String, List<DataDTO>> deptBelongs,
			Map<String, DataDTO> formDepts) throws BusinessException {
		//��ѯ���ⲿ��
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
				//�������ž���������кϲ�
				if(sourceDTO!=null && tartgetDTO!=null){

					tartgetDTO.belong_dept_nums +=sourceDTO.belong_dept_nums;
					
					//�������ż��뵽�ϼ����ŵĹ���������
					deptBelongs.get(tartgetDTO.pk_dept).add(sourceDTO);
					//�������ŵĹ������ϼ��뵽�ϼ�������
					deptBelongs.get(tartgetDTO.pk_dept).addAll(deptBelongs.get(sourceDTO.pk_dept));
					
					//ɾ���ò���
					datas.remove(sourceDTO.pk_dept);
					deptBelongs.remove(sourceDTO.pk_dept);
				}
			}
		}
		
	}


	/**
	 * ��ʼ����ϵ�� ������->������
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
	 * ��������(���T���e��С�����)�z�飬����С춵��2�˵Ĳ��T���t���˔��ρ�����һ��ͬ��֮���T�У�
	 * ���T���a��С���˔������0�Ĳ��T������һ�Ӳ��Tȫ������0(������Ϊ0,����һ������2,��Ϊ��2������,����ֻɨ��һ�㼴��)���t������һ�ӡ�
	 * @param datas ��Ҫ��ֵĲ���
	 * @param deptBelongs ���źϲ���ϵ ��ǰ����pk->�ϲ�����ǰ���ŵ������¼�����
	 * @throws BusinessException
	 */
	private void departHeadDept(Map<String,DataDTO> datas, Map<String, List<DataDTO>> deptBelongs,String psnType) throws BusinessException{
		//ά�����¼���ϵ�ϼ�����->�¼����ż���(��ǰ�ϲ���Ĳ��żܹ�)
		Map<String,List<DataDTO>>dept2LowDepts = new HashMap<>();
		for(DataDTO dept : datas.values()){
			List<DataDTO> list = dept2LowDepts.get(dept.pk_fatherorg);
			if(list==null){
				list = new ArrayList<>();
			}
			list.add(dept);
			dept2LowDepts.put(dept.pk_fatherorg, list);
		}
		//����
		Collection<DataDTO> dtos = datas.values();
		DataDTO[] dtoArray = dtos.toArray(new DataDTO[0]);
		Arrays.sort(dtoArray,new DecComparator());
		
		//ɨ��С�� ����2�˵Ĳ���
		for(DataDTO dept : dtoArray){
			if(dept.belong_dept_nums <=2&&dept2LowDepts.get(dept.pk_dept)!=null){
				//��ȡ����Ҫ����¼�����
				DataDTO lowDept = getAvilableDept(dept2LowDepts.get(dept.pk_dept));
				if(lowDept==null){
					continue;
				}
				//���ò��źϲ����¼�����
				deptBelongs.get(lowDept.pk_dept).add(dept);
				//���Ĳ����������ϼ��뵽�¼����ŵļ���
				deptBelongs.get(lowDept.pk_dept).addAll(deptBelongs.get(dept.pk_dept));
				
				//ɾ���ò���
				datas.remove(dept.pk_dept);
				deptBelongs.remove(dept.pk_dept);
			}
		}
		
	}
	
	/**
	 * ��ȡ������С,������Ϊ0�Ĳ���,�Ҳ��ż���С�ڵ���70
	 * @param list ����pks
	 * @param datas ������ϸ��Ϣ
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
	 * ����С�ڵ���2�˵Ĳ���,�ϲ����ϼ�����.(��������ѭ��ɨ��) sum ����
	 * 	IDL:����ϼ����Ų��ڱ��η�н��,�����ϼ����ϼ�,һֱ�ҵ�λ��
	 *  DL:����ϼ����Ų��ڱ��η�н��,���ٺϲ����ϼ�����
	 * @param datas ��Ҫ�ϲ�������
	 * @param deptBelongs ���źϲ���ϵ pk_dept->�ϲ����ò��ŵ����в���(�����Լ�)
	 * @param formDepts ���в��ű���
	 * @throws BusinessException
	 */
	private void combineMinDept(Map<String,DataDTO> datas, Map<String, List<DataDTO>> deptBelongs,Map<String, DataDTO> formDepts, String psnType) throws BusinessException{
		boolean hasLastDept = true;
		
		//��¼����Ҫ�����Ϻϲ��Ĳ���(DL)
		Set<String> noCombineDepts = new HashSet<>();
		
		while(hasLastDept){
			hasLastDept = false;
			
			//����
			Collection<DataDTO> dtos = datas.values();
			DataDTO[] dtoArray = dtos.toArray(new DataDTO[0]);
			Arrays.sort(dtoArray,new AsceComparator());
			
			//ɨ��С�� ����2�˵Ĳ���
			for(DataDTO dept : dtoArray){
				if(dept.belong_dept_nums <=2 && dept.pk_fatherorg!=null&&!noCombineDepts.contains(dept.pk_dept)){
					hasLastDept = true;
					//�Ѹò��ŵ������ӵ��ϼ�����
					DataDTO faDept = datas.get(dept.pk_fatherorg);
					if(faDept == null){
						if(psnType.equals(DL)){
							//DL �������Ϻϲ�
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
					
					//�������ż��뵽�ϼ����ŵĹ���������
					deptBelongs.get(faDept.pk_dept).add(dept);
					//�������ŵĹ������ϼ��뵽�ϼ�������
					deptBelongs.get(faDept.pk_dept).addAll(deptBelongs.get(dept.pk_dept));
					
					//ɾ���ò���
					datas.remove(dept.pk_dept);
					deptBelongs.remove(dept.pk_dept);
				}
			}
		}
		
	}
	/**
	 * Ѱ���ϼ�����,���ֱ���ϼ�����Ҫ�ϲ��Ĳ����в�����,���ڱ��������ϼ����ϼ�,����ѭ��
	 * @param pk_fatherorg
	 * @param datas ��Ҫ�ϲ��Ĳ���
	 * @param formDepts ���в��ű���
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
					Logger.error("����["+pk_fatherorg+"],�����ϼ�δ�ڱ��η�н������....");
					continue;
				}
			}else{
				return datas.get(pk_fa_loop);
			}
		}
		return null;
	}

	/**
	 * ����С��"��"(70)�Ĳ���ȫ���ϲ����ϼ� (��������ѭ��ɨ��) sum ����
	 * DL/IDL:���û���ҵ��ϼ�,�����б��Ʋ�������.
	 * @param datas ��Ҫ�ϲ��Ĳ���
	 * @param deptBelongs ���Ź�����ϵ
	 * @param formDepts ���в��ű���(ȫ��)
	 * @throws BusinessException
	 */
	private void combineLastDept(Map<String,DataDTO> datas, Map<String, List<DataDTO>> deptBelongs, Map<String, DataDTO> formDepts) throws BusinessException{
		
		
		boolean hasLastDept = true;
		while(hasLastDept){
			hasLastDept = false;
			
			//����
			Collection<DataDTO> dtos = datas.values();
			DataDTO[] dtoArray = dtos.toArray(new DataDTO[0]);
			Arrays.sort(dtoArray,new AsceComparator());
			
			//ɨ��С�� MIN_LEVEL�Ĳ���
			for(DataDTO dept : dtoArray){
				if(dept.deptLevel > MIN_LEVEL && dept.pk_fatherorg!=null && !dept.pk_fatherorg.equals(dept.pk_dept)){
					hasLastDept = true;
					
					//�Ѹò��ŵ������ӵ��ϼ�����
					DataDTO faDept = datas.get(dept.pk_fatherorg);
					//�����ǰ��н�Ĳ����Ҳ���,��Ӳ��ű������ҵ�����,ͬʱ�����Ʋ��ŷ��뵱ǰ���źϲ���������
					if(faDept==null){
						faDept = formDepts.get(dept.pk_fatherorg);
						
						List<DataDTO> list = new ArrayList<DataDTO>();
						list.add(faDept);
						deptBelongs.put(dept.pk_fatherorg, list);
						datas.put(dept.pk_fatherorg, faDept);
					}
					faDept.belong_dept_nums +=dept.belong_dept_nums;
					
					//�������ż����ϼ����ŵĹ�������
					deptBelongs.get(dept.pk_fatherorg).add(dept);
					
					//�������ŵĹ��������뵽�ϼ�����
					deptBelongs.get(dept.pk_fatherorg).addAll(deptBelongs.get(dept.pk_dept));
					
					//ɾ���ò���
					deptBelongs.remove(dept.pk_dept);
					datas.remove(dept.pk_dept);
					
				}
			}
		}
		
	}
	
	
	/**
	 * ���в��ű���(ȫ��)
	 * @param psnType 
	 * @return
	 * @throws BusinessException 
	 */
	private Map<String, DataDTO> getFormDepts(String psnType,String pk_org) throws BusinessException {
		//��ȡ���б��Ʋ���
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

		//������Դ:0:н��,1:��������
		Integer sourceInt = source.intValue();
		if(sourceInt == 1){
			return getDataHealth(pk_org,pk_wa_class,startPeriod,endPeriod,psnType);
		}else{
			return getDataSalary(pk_org,pk_wa_class,startPeriod,endPeriod,psnType);
		}
	}

	
	/**
	 * ������������Ա��IDL
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
	 * �ӵײ㵽�ϲ�
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
	 * ���ϲ㵽�ײ�
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
	 * �������ӵ�ds��
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
	 * @param datas �ρ���Ĕ���
	 * @param deptBelongs ���T�ρ��P�S �ϼ����T->list<�ρ㵽ԓ���T�Ĳ��T>
	 * @param oldDatas ��ԭʼ�Ĕ���
	 * @return
	 */
	private List<DataDTO> buildResult(Map<String, DataDTO> datas,
			Map<String, List<DataDTO>> deptBelongs,
			Map<String, DataDTO> oldDatas) {
		List<DataDTO> rsList = new ArrayList<>();
		Set<String> keyset = oldDatas.keySet();
		
		//���첿�T->�w�ٲ��T���ձ�
		Map<String,String> combineDeptMap = buildRefMap(deptBelongs);
		
		for(String pk_dept : keyset){
			DataDTO oldDept = oldDatas.get(pk_dept);
			//�@ȡ�ρ�Ěw�ٲ��T
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
	 * ������
	 * ����ԴΪ����������,���н�ʷ�������Ա���ֶ�
	 * @param dataList
	 * @param metaData
	 * @param pk_wa_class
	 * @param source
	 * @return
	 */
	private Object[][] processData(List<DataDTO> dataList, MetaData metaData, String pk_wa_class, int source) {
		Object[][] rs = new Object[dataList.size()][metaData.getFieldNum()];
		//���r�g �ł���
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
			//���еĔ���
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
		field.setCaption("�ϼ����T���I");
		field.setFldname("pk_fatherorg");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);

		field = new Field();
		field.setCaption("���T���I");
		field.setFldname("pk_dept");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);

		field = new Field();
		field.setCaption("�w�ٲ��T���I");
		field.setFldname("pk_dept_belong");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);

		field = new Field();
		field.setCaption("���T���e");
		field.setFldname("dept_level");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);

		field = new Field();
		field.setCaption("�ˆTe");
		field.setFldname("dir");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);
		
		field = new Field();
		field.setCaption("������������");
		field.setFldname("belong_dept_nums");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);
		
		field = new Field();
		field.setCaption("�M��");
		field.setFldname("pk_org");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);
		
		field = new Field();
		field.setCaption("н�ʷ���");
		field.setFldname("pk_wa_class");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);
		
		field = new Field();
		field.setCaption("��");
		field.setFldname("year");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);
		
		field = new Field();
		field.setCaption("��");
		field.setFldname("month");
		field.setPrecision(20);
		field.setDbColumnType(12);
		meta.addField(field);
		
		return meta;
	}

	/**
	 * ���ݴ洢
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
		 * ��ǰ��������(���ܺϲ����¼�����)
		 */
		public Integer belong_dept_nums = 0;
	}

}
