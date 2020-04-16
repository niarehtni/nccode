package nc.impl.wa.adjust;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.bs.dao.DAOException;
import nc.hr.frame.persistence.AppendBaseDAO;
import nc.hr.utils.InSQLCreator;
import nc.jdbc.framework.SQLParameter;
import nc.vo.hr.append.AppendableVO;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.hr.pub.FormatVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.grade.WaGradeVO;

/**
 * 
 * @author: zhangg
 * @date: 2010-4-22 上午10:10:27
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WaPsnHiDAO extends AppendBaseDAO
{
	// 人员基本信息(hi_psnjob)
	private final String BD_PSNDOC = "bd_psndoc";
	// 人员属性信息编码
	private final String CODE = "item_code";
	// 人员属性信息数据类型
	private final String DATA_TYPE = "data_type";
	// 人员工作记录(hi_psnjob)
	private final String HI_PSNJOB = "hi_psnjob";
	private final String PRMTYPE = "1";// 级别
	// 人员属性标识
	private final String PSNHI = "psnhi";
	// 人员属性子表标识
	private final String PSNHI_B = "psnhi_b";

	private final String SECTYPE = "2";// 档别

	/**
	 * 获得级别或者档别对应的人员属性 classtype = 1 级别， classtype = 2 档别
	 * 
	 * @author zhangg on 2010-4-22
	 * @param appendableVOs
	 * @param classtype
	 * @return
	 */
	private List<AppendableVO> getAppendableVOsByClassType(AppendableVO[] appendableVOs, String classtype)
	{
		List<AppendableVO> list = new ArrayList<AppendableVO>();
		for (AppendableVO appendableVO : appendableVOs)
		{
			Object tableName = appendableVO.getAttributeValue(InfoSetVO.TABLE_CODE);
			Object colName = appendableVO.getAttributeValue(CODE);
			if (tableName != null && colName != null)
			{// 有人员属性关联
				if (appendableVO.getAttributeValue("classtype").toString().equals(classtype))
				{
					list.add(appendableVO);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @author zhangg on 2010-4-22
	 * @param pk_wa_grd
	 * @return
	 * @throws DAOException
	 */
	public String getCriterion(String pk_wa_grd, String pk_psndoc) throws DAOException
	{

		String strSql = getCriterionForCaculate(pk_wa_grd);
		if (strSql == null)
		{
			return null;
		}
		return strSql + " and bd_psndoc.pk_psndoc = '" + pk_psndoc + "'";

	}
	/**
	 * 
	 * @author suihang on 2011-8-22
	 * @param pk_wa_grd
	 * @return
	 * @throws BusinessException 
	 */
	public String getCriterion(String pk_wa_grd, String[] pk_psndocs) throws BusinessException
	{

		String strSql = getCriterionForCaculate(pk_wa_grd);
		if (strSql == null)
		{
			return null;
		}
		
		   InSQLCreator inSQLCreator = new InSQLCreator();
		     
		     try
		     {
		         String insql = inSQLCreator.getInSQL(pk_psndocs);
		 		
		 		return  strSql += " and bd_psndoc.pk_psndoc in (" + insql + ")";
		     }
		        finally
		        {
		         inSQLCreator.clear();
		        } 
		
	}
	
//	20151208 xiejie3  NCdp205554161  入职推调薪，单据没有根据级别档别属性值自动带出
//	推单时要求支持每个人多个业务流配置，所以修改逻辑：查询每个人的业务流配置，进行推单默认值设置。
	public String getCriterions(String[] pk_wa_grd, String[] pk_psndocs) throws BusinessException
	{

		String strSql = getCriterionForCaculate(pk_wa_grd);
		if (strSql == null)
		{
			return null;
		}
		
		   InSQLCreator inSQLCreator = new InSQLCreator();
		     
		     try
		     {
		         String insql = inSQLCreator.getInSQL(pk_psndocs);
		 		
		 		return  strSql += " and bd_psndoc.pk_psndoc in (" + insql + ")";
		     }
		        finally
		        {
		         inSQLCreator.clear();
		        } 
		
	}
	
	
	
	
	/*public PsnappaproveBVO[] getPsnappaproveBVOs(String pk_wa_grd, String[] pk_psndocs) throws BusinessException{
		
		
		String strSql = getCriterionForCaculate(pk_wa_grd);
		if (strSql == null)
		{
			return null;
		}
		
		   InSQLCreator inSQLCreator = new InSQLCreator();
		     
		     try
		     {
		         String insql = inSQLCreator.getInSQL(pk_psndocs);
		 		
		 		 strSql += " and bd_psndoc.pk_psndoc in (" + insql + ")";
		 	
		     
		    if(!StringUtils.isEmpty(strSql)){
		    	return  executeQueryVOs(strSql, PsnappaproveBVO.class);
		    }else{
		    	return new PsnappaproveBVO[0];
		    }
		    
		     }
		        finally
		        {
		         inSQLCreator.clear();
		        }
		    
		
		
	}*/
	public String getCriterionForCaculate(String pk_wa_grd) throws DAOException
	{
		AppendableVO[] appendableVOs = getInfo2GradeAppendableVOs(pk_wa_grd);
		if (isRelatedWithPsnProperty(appendableVOs))
		{// 和人员属性相关
			return getSql(appendableVOs, pk_wa_grd);
		}
		return null;
	}
	
//	20151208 xiejie3  NCdp205554161  入职推调薪，单据没有根据级别档别属性值自动带出
//	推单时要求支持每个人多个业务流配置，所以修改逻辑：查询每个人的业务流配置，进行推单默认值设置。
	public String getCriterionForCaculate(String[] pk_wa_grd) throws BusinessException
	{
		AppendableVO[] appendableVOs = getInfo2GradeAppendableVOs(pk_wa_grd);
		if (isRelatedWithPsnProperty(appendableVOs))
		{// 和人员属性相关
			return getSql(appendableVOs, pk_wa_grd);
		}
		return null;
	}
//	
	
	
	
	public String getCriterionOnly4Caculate(String pk_wa_grd) throws DAOException
	{
		AppendableVO[] appendableVOs = getInfo2GradeAppendableVOs(pk_wa_grd);
		if (isRelatedWithPsnProperty(appendableVOs))
		{// 和人员属性相关
			return getSqlOnly4Caculate(appendableVOs, pk_wa_grd);
		}
		return null;
	}

	/**
	 * @deprecated 效率问题
	 * @author zhangg on 2010-4-22
	 * @param pk_wa_grd
	 * @return
	 * @throws DAOException
	 */
	@Deprecated
	public String getCriterionForCaculateBack(String pk_wa_grd) throws DAOException
	{
		AppendableVO[] appendableVOs = getInfo2GradeAppendableVOs(pk_wa_grd);
		if (isRelatedWithPsnProperty(appendableVOs))
		{// 和人员属性相关
			// 查看是否多档
			boolean isMutiSec = UFBoolean.valueOf(appendableVOs[0].getAttributeValue(WaGradeVO.ISMULTSEC).toString()).booleanValue();
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("select wa_criterion.criterionvalue as wa_apply_money, "); // 1
			sqlBuffer.append("       wa_criterion.max_value, "); // 2
			sqlBuffer.append("       wa_criterion.min_value, "); // 3
			sqlBuffer.append("       wa_criterion.pk_wa_crt  as pk_wa_crt_apply, "); // 4
			sqlBuffer.append("       wa_criterion.pk_wa_grd, "); // 5
			sqlBuffer.append("       wa_criterion.pk_wa_prmlv as pk_wa_prmlv_apply, "); // 6
			sqlBuffer.append("       wa_criterion.pk_wa_seclv as pk_wa_seclv_apply, "); // 7
			sqlBuffer.append("       prmlv.pk_psndoc, "); // 8
			sqlBuffer.append("       prmlv.wa_prmlv_apply "); // 9
			if (isMutiSec)
			{
				sqlBuffer.append("      , seclv.wa_seclv_apply "); // 10
			}
			sqlBuffer.append("  from wa_criterion {0} {1}");
			sqlBuffer.append(" where wa_criterion.pk_wa_prmlv = prmlv.pk_wa_grdlv ");
			if (isMutiSec)
			{
				sqlBuffer.append("   and wa_criterion.pk_wa_seclv = seclv.pk_wa_grdlv ");
				sqlBuffer.append("   and prmlv.pk_psndoc = seclv.pk_psndoc ");
			}

			List<AppendableVO> prmList = getAppendableVOsByClassType(appendableVOs, PRMTYPE);
			String prmSqlFregment = getSqlFregment(prmList, PRMTYPE, pk_wa_grd);
			String prmTable = ", (" + prmSqlFregment + ") as  prmlv";
			String secTable = " ";
			// 档别
			if (isMutiSec)
			{
				List<AppendableVO> secList = getAppendableVOsByClassType(appendableVOs, SECTYPE);
				String secSqlFregment = getSqlFregment(secList, SECTYPE, pk_wa_grd);
				secTable = ", (" + secSqlFregment + ") as  seclv";
			}
			return MessageFormat.format(sqlBuffer.toString(), prmTable, secTable) + " and wa_criterion.pk_wa_grd = '" + pk_wa_grd + "' ";
		}
		return null;
	}

	/**
	 * 获得薪资标准对应的人员属性VOs
	 * 
	 * @author zhangg on 2010-4-22
	 * @param pk_wa_grd
	 * @return
	 * @throws DAOException
	 */
	private AppendableVO[] getInfo2GradeAppendableVOs(String pk_wa_grd) throws DAOException
	{
		/**
		 * 查找员工对应薪资标准类别中的等级。一定会返回一条记录，因为是left join，而且wa_grade 中有等级。
		 * 但是可能找不到hi_setdict.setcode hi_flddict.fldcode,
		 * hi_flddict.datatype。因为不是每一个等级都与 人员属性相关
		 */
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_grade.ismultsec, "); // 1
		sqlBuffer.append("       wa_psnhi.pk_wa_psnhi, "); // 2
		sqlBuffer.append("       hr_infoset.table_code, "); // 3
		sqlBuffer.append("       hr_infoset_item.item_code, "); // 4
		sqlBuffer.append("       hr_infoset_item.data_type, "); // 5
		sqlBuffer.append("       wa_psnhi.classtype "); // 6
		sqlBuffer.append("  from wa_grade ");
		sqlBuffer.append("  left join wa_psnhi on wa_grade.pk_wa_grd = wa_psnhi.pk_wa_grd ");
		sqlBuffer.append("  left join hr_infoset_item on hr_infoset_item.pk_infoset_item = wa_psnhi.pk_flddict ");
		sqlBuffer.append("  left join hr_infoset on hr_infoset.pk_infoset = hr_infoset_item.pk_infoset ");
		sqlBuffer.append(" where wa_psnhi.pk_wa_grd = ? ");

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_wa_grd);
		return executeQueryAppendableVOs(sqlBuffer.toString(), parameter, AppendableVO.class);
	}
	
	
//	20151208 xiejie3  NCdp205554161  入职推调薪，单据没有根据级别档别属性值自动带出
//	推单时要求支持每个人多个业务流配置，所以修改逻辑：查询每个人的业务流配置，进行推单默认值设置。
	/**
	 * 获得薪资标准对应的人员属性VOs
	 * 
	 * @author zhangg on 2010-4-22
	 * @param pk_wa_grd
	 * @return
	 * @throws BusinessException 
	 */
	private AppendableVO[] getInfo2GradeAppendableVOs(String[] pk_wa_grd) throws BusinessException
	{
		/**
		 * 查找员工对应薪资标准类别中的等级。一定会返回一条记录，因为是left join，而且wa_grade 中有等级。
		 * 但是可能找不到hi_setdict.setcode hi_flddict.fldcode,
		 * hi_flddict.datatype。因为不是每一个等级都与 人员属性相关
		 */
		InSQLCreator insql=new InSQLCreator();
		String     pk_wa_grdinsql=" ";
		try{
		    pk_wa_grdinsql=insql.getInSQL(pk_wa_grd);
		}finally
        {
			insql.clear();
	        } 
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_grade.ismultsec, "); // 1
		sqlBuffer.append("       wa_psnhi.pk_wa_psnhi, "); // 2
		sqlBuffer.append("       hr_infoset.table_code, "); // 3
		sqlBuffer.append("       hr_infoset_item.item_code, "); // 4
		sqlBuffer.append("       hr_infoset_item.data_type, "); // 5
		sqlBuffer.append("       wa_psnhi.classtype "); // 6
		sqlBuffer.append("  from wa_grade ");
		sqlBuffer.append("  left join wa_psnhi on wa_grade.pk_wa_grd = wa_psnhi.pk_wa_grd ");
		sqlBuffer.append("  left join hr_infoset_item on hr_infoset_item.pk_infoset_item = wa_psnhi.pk_flddict ");
		sqlBuffer.append("  left join hr_infoset on hr_infoset.pk_infoset = hr_infoset_item.pk_infoset ");
		sqlBuffer.append(" where wa_psnhi.pk_wa_grd in ");
		sqlBuffer.append(" ( "+pk_wa_grdinsql+ " )");

		return executeQueryAppendableVOs(sqlBuffer.toString(), AppendableVO.class);
	}
//	
	
	

	private String getSql(AppendableVO[] generalVOs, String pk_wa_grd)
	{

		boolean isMutiSec = false;
		if (generalVOs[0].getAttributeValue(WaGradeVO.ISMULTSEC) != null)
		{
			isMutiSec = UFBoolean.valueOf(generalVOs[0].getAttributeValue(WaGradeVO.ISMULTSEC).toString()).booleanValue();
		}
		Set<String> tableNameSet = new HashSet<String>();
		StringBuffer sql = new StringBuffer();
		boolean isPsnjobin = false;
		for (AppendableVO generalVO : generalVOs)
		{

			Object tableName = generalVO.getAttributeValue(InfoSetVO.TABLE_CODE);
			Object colName = generalVO.getAttributeValue(CODE);
			Object datatype = generalVO.getAttributeValue(InfoItemVO.DATA_TYPE);
			if (tableName != null && colName != null && datatype != null)
			{
				// 属性类型
				// 如果是人员工作记录则做个标记
				if (HI_PSNJOB.equalsIgnoreCase(tableName.toString().trim()))
				{
					isPsnjobin = true;
				}

				if (!BD_PSNDOC.equalsIgnoreCase(tableName.toString().trim()))
				{
					tableNameSet.add(tableName.toString());
				}

				String wa_psnhi_b = tableName.toString() + colName.toString() + PSNHI_B;
				String wa_psnhi = tableName.toString() + colName.toString() + PSNHI;
				tableNameSet.add(" wa_psnhi_b  " + wa_psnhi_b + " inner join wa_psnhi  " + wa_psnhi + " on " + wa_psnhi_b + ".pk_wa_psnhi=" + wa_psnhi + ".pk_wa_psnhi ");
				// mod start 如果是boolean类型需要考虑为空的问题 tank 2020年2月6日20:06:32
				if(nc.ui.pub.bill.IBillItem.BOOLEAN == (Integer)datatype){
				    sql.append(" and convert(char,isnull(" + tableName + "." + colName + " ,'N'))  = (" + wa_psnhi_b + ".vfldvalue");
				}else{
				    sql.append(" and convert(char,(" + tableName + "." + colName + " ))  = (" + wa_psnhi_b + ".vfldvalue");
				}
				//mod end如果是boolean类型需要考虑为空的问题 tank 2020年2月6日20:06:32
				sql.append(") ");

				sql.append(" and " + wa_psnhi + ".pk_wa_grd = criterion.pk_wa_grd");
				// 当级别相关的人员相关属性的时候
				sql.append(" and((" + wa_psnhi + ".classtype = 1 and  criterion.pk_wa_prmlv = " + wa_psnhi_b + ".pk_wa_grdlv)");
				if (isMutiSec)
				{
					sql.append(" or(" + wa_psnhi + ".classtype = 2 and  criterion.pk_wa_seclv = " + wa_psnhi_b + ".pk_wa_grdlv)");
				}
				sql.append(")");
			}
		}
		tableNameSet.add(" bd_psndoc");
		tableNameSet.add(" hi_psnorg ");
		tableNameSet.add(" wa_prmlv  prmlv ");
		tableNameSet.add(" wa_criterion  criterion ");
		tableNameSet.add(" wa_grade_ver gradeVer ");
		if (isMutiSec)
		{
			tableNameSet.add("wa_seclv  seclv ");
			sql.append(" and seclv.pk_wa_seclv = criterion.pk_wa_seclv ");
			sql.append(" and seclv.pk_wa_grd = criterion.pk_wa_grd ");
		}
		sql.append(" and prmlv.pk_wa_prmlv = criterion.pk_wa_prmlv ");
		sql.append(" and prmlv.pk_wa_grd = criterion.pk_wa_grd  ");
		sql.append(" and gradeVer.pk_wa_gradever= criterion.pk_wa_gradever and gradeVer.effect_flag='Y'  ");
  
		StringBuffer sqlB = new StringBuffer();

		sqlB.append("select distinct criterion.pk_wa_prmlv as pk_wa_prmlv_apply, ");
		if (isMutiSec) {
			sqlB.append("       criterion.pk_wa_seclv as pk_wa_seclv_apply, ");
		}
		sqlB.append("       criterion.criterionvalue as wa_apply_money, ");
		sqlB.append("       criterion.pk_wa_crt as pk_wa_crt, ");
		sqlB.append("       bd_psndoc.pk_psndoc, ");
		sqlB.append("       prmlv.levelname as wa_prmlv_apply,");
		sqlB.append("       criterion.max_value as crt_max_value,");
		sqlB.append("       criterion.min_value as crt_min_value");
		if (isMutiSec)
		{
			sqlB.append(" , seclv.levelname  wa_seclv_apply");
		}
		if (isPsnjobin)
		{
			sqlB.append(" ,hi_psnjob.assgid ");
		}else{
			//不关联hi_psnjob则认为是主职
			sqlB.append(" ,0 as assgid");
		}
		sqlB.append(" ,gradeVer.pk_wa_grd ");
		if (isMutiSec)
		{
			sqlB.append(" ,(prmlv.levelname || '/' || seclv.levelname) pk_wa_crt_apply_showname ");
		}else{
			sqlB.append(" ,prmlv.levelname pk_wa_crt_apply_showname ");
		}
		sqlB.append("  from  ");
		sqlB.append(FormatVO.formatArrayToString(tableNameSet.toArray(new String[tableNameSet.size()]), ""));
		sqlB.append(" where criterion.pk_wa_grd = '" + pk_wa_grd + "' ");
		sqlB.append(" and hi_psnorg.pk_psndoc =  bd_psndoc.pk_psndoc ");
		sqlB.append(" and (hi_psnorg.indocflag = 'Y' or hi_psnorg.indocflag = 'y') ");
		sqlB.append(" and (hi_psnorg.lastflag = 'Y' or hi_psnorg.lastflag = 'y') ");
		//解决离职人员也可以参照到之前的薪资标准，因此需要去掉下面这句限制
		//sqlB.append(" and (hi_psnorg.endflag = 'N' or hi_psnorg.endflag = 'n') ");
		if (isPsnjobin)
		{
			sqlB.append(" and hi_psnjob.pk_psndoc = bd_psndoc.pk_psndoc ");
			//最新的工作记录，该工作（记录）尚未结束，该工作可以是兼职。
//			sqlB.append(" and hi_psnjob.lastflag = 'Y' and hi_psnjob.endflag = 'N'  ");
			//解决离职人员也可以参照到之前的薪资标准，因此需要去掉下面这句限制
			sqlB.append(" and hi_psnjob.lastflag = 'Y'  ");
		}

		sqlB.append(sql);
		return sqlB.toString();
	}
	
//	20151208 xiejie3  NCdp205554161  入职推调薪，单据没有根据级别档别属性值自动带出
//	推单时要求支持每个人多个业务流配置，所以修改逻辑：查询每个人的业务流配置，进行推单默认值设置。
//	注意！！！！！此方法用来批量处理多个业务流程配置，下方法目前有问题，只能处理相同薪资标准类别。
//	目前此方法先保留，目前没有用，如需用到，需要进一步处理。、
//	此方法目前不可用。
	private String getSql(AppendableVO[] generalVOs, String[] pk_wa_grd) throws BusinessException
	{

		boolean isMutiSec = false;
		InSQLCreator  insql=new  InSQLCreator();
		String  pk_wa_grdinsql="";
		try{
			pk_wa_grdinsql=insql.getInSQL(pk_wa_grd);
		}finally
        {
			insql.clear();
	        } 
		if (generalVOs[0].getAttributeValue(WaGradeVO.ISMULTSEC) != null)
		{
			isMutiSec = UFBoolean.valueOf(generalVOs[0].getAttributeValue(WaGradeVO.ISMULTSEC).toString()).booleanValue();
		}
		Set<String> tableNameSet = new HashSet<String>();
		StringBuffer sql = new StringBuffer();
		boolean isPsnjobin = false;
		for (AppendableVO generalVO : generalVOs)
		{

			Object tableName = generalVO.getAttributeValue(InfoSetVO.TABLE_CODE);
			Object colName = generalVO.getAttributeValue(CODE);
			Object datatype = generalVO.getAttributeValue(InfoItemVO.DATA_TYPE);
			if (tableName != null && colName != null && datatype != null)
			{
				// 属性类型
				// 如果是人员工作记录则做个标记
				if (HI_PSNJOB.equalsIgnoreCase(tableName.toString().trim()))
				{
					isPsnjobin = true;
				}

				if (!BD_PSNDOC.equalsIgnoreCase(tableName.toString().trim()))
				{
					tableNameSet.add(tableName.toString());
				}

				String wa_psnhi_b = tableName.toString() + colName.toString() + PSNHI_B;
				String wa_psnhi = tableName.toString() + colName.toString() + PSNHI;
				tableNameSet.add(" wa_psnhi_b  " + wa_psnhi_b + " inner join wa_psnhi  " + wa_psnhi + " on " + wa_psnhi_b + ".pk_wa_psnhi=" + wa_psnhi + ".pk_wa_psnhi ");
				sql.append(" and convert(char,(" + tableName + "." + colName + " ))  = (" + wa_psnhi_b + ".vfldvalue");

				sql.append(") ");

				sql.append(" and " + wa_psnhi + ".pk_wa_grd = criterion.pk_wa_grd");
				// 当级别相关的人员相关属性的时候
				sql.append(" and((" + wa_psnhi + ".classtype = 1 and  criterion.pk_wa_prmlv = " + wa_psnhi_b + ".pk_wa_grdlv)");
				if (isMutiSec)
				{
					sql.append(" or(" + wa_psnhi + ".classtype = 2 and  criterion.pk_wa_seclv = " + wa_psnhi_b + ".pk_wa_grdlv)");
				}
				sql.append(")");
			}
		}
		tableNameSet.add(" bd_psndoc");
		tableNameSet.add(" hi_psnorg ");
		tableNameSet.add(" wa_prmlv  prmlv ");
		tableNameSet.add(" wa_criterion  criterion ");
		tableNameSet.add(" wa_grade_ver gradeVer ");
		if (isMutiSec)
		{
			tableNameSet.add("wa_seclv  seclv ");
			sql.append(" and seclv.pk_wa_seclv = criterion.pk_wa_seclv ");
			sql.append(" and seclv.pk_wa_grd = criterion.pk_wa_grd ");
		}
		sql.append(" and prmlv.pk_wa_prmlv = criterion.pk_wa_prmlv ");
		sql.append(" and prmlv.pk_wa_grd = criterion.pk_wa_grd  ");
		sql.append(" and gradeVer.pk_wa_gradever= criterion.pk_wa_gradever and gradeVer.effect_flag='Y'  ");
  
		StringBuffer sqlB = new StringBuffer();

		sqlB.append("select distinct criterion.pk_wa_prmlv as pk_wa_prmlv_apply, ");
		if (isMutiSec) {
			sqlB.append("       criterion.pk_wa_seclv as pk_wa_seclv_apply, ");
		}
		sqlB.append("       criterion.criterionvalue as wa_apply_money, ");
		sqlB.append("       criterion.pk_wa_crt as pk_wa_crt, ");
		sqlB.append("       bd_psndoc.pk_psndoc, ");
		sqlB.append("       prmlv.levelname as wa_prmlv_apply,");
		sqlB.append("       criterion.max_value as crt_max_value,");
		sqlB.append("       criterion.min_value as crt_min_value");
		if (isMutiSec)
		{
			sqlB.append(" , seclv.levelname  wa_seclv_apply");
		}
		if (isPsnjobin)
		{
			sqlB.append(" ,hi_psnjob.assgid ");
		}else{
			//不关联hi_psnjob则认为是主职
			sqlB.append(" ,0 as assgid");
		}
		sqlB.append(" ,gradeVer.pk_wa_grd ");
		if (isMutiSec)
		{
			sqlB.append(" ,(prmlv.levelname || '/' || seclv.levelname) pk_wa_crt_apply_showname ");
		}else{
			sqlB.append(" ,prmlv.levelname pk_wa_crt_apply_showname ");
		}
		sqlB.append("  from  ");
		sqlB.append(FormatVO.formatArrayToString(tableNameSet.toArray(new String[tableNameSet.size()]), ""));
//		sqlB.append(" where criterion.pk_wa_grd = '" + pk_wa_grd + "' ");
		sqlB.append(" where criterion.pk_wa_grd in ");
		sqlB.append(" ( "+ pk_wa_grdinsql +" ) ");
		
		sqlB.append(" and hi_psnorg.pk_psndoc =  bd_psndoc.pk_psndoc ");
		sqlB.append(" and (hi_psnorg.indocflag = 'Y' or hi_psnorg.indocflag = 'y') ");
		sqlB.append(" and (hi_psnorg.lastflag = 'Y' or hi_psnorg.lastflag = 'y') ");
		//解决离职人员也可以参照到之前的薪资标准，因此需要去掉下面这句限制
		//sqlB.append(" and (hi_psnorg.endflag = 'N' or hi_psnorg.endflag = 'n') ");
		if (isPsnjobin)
		{
			sqlB.append(" and hi_psnjob.pk_psndoc = bd_psndoc.pk_psndoc ");
			//最新的工作记录，该工作（记录）尚未结束，该工作可以是兼职。
//			sqlB.append(" and hi_psnjob.lastflag = 'Y' and hi_psnjob.endflag = 'N'  ");
			//解决离职人员也可以参照到之前的薪资标准，因此需要去掉下面这句限制
			sqlB.append(" and hi_psnjob.lastflag = 'Y'  ");
		}

		sqlB.append(sql);
		return sqlB.toString();
	}
//	end
	
	
	
	
	private String getSqlOnly4Caculate(AppendableVO[] generalVOs, String pk_wa_grd)
	{

		boolean isMutiSec = false;
		if (generalVOs[0].getAttributeValue(WaGradeVO.ISMULTSEC) != null)
		{
			isMutiSec = UFBoolean.valueOf(generalVOs[0].getAttributeValue(WaGradeVO.ISMULTSEC).toString()).booleanValue();
		}
		Set<String> tableNameSet = new HashSet<String>();
		StringBuffer sql = new StringBuffer();
		for (AppendableVO generalVO : generalVOs)
		{

			Object tableName = generalVO.getAttributeValue(InfoSetVO.TABLE_CODE);
			Object colName = generalVO.getAttributeValue(CODE);
			Object datatype = generalVO.getAttributeValue(InfoItemVO.DATA_TYPE);
			if (tableName != null && colName != null && datatype != null)
			{


				if (!BD_PSNDOC.equalsIgnoreCase(tableName.toString().trim())&&!HI_PSNJOB.equalsIgnoreCase(tableName.toString().trim()))
				{
					tableNameSet.add(tableName.toString());
				}

				String wa_psnhi_b = tableName.toString() + colName.toString() + PSNHI_B;
				String wa_psnhi = tableName.toString() + colName.toString() + PSNHI;
				tableNameSet.add(" wa_psnhi_b  " + wa_psnhi_b + " inner join wa_psnhi  " + wa_psnhi + " on " + wa_psnhi_b + ".pk_wa_psnhi=" + wa_psnhi + ".pk_wa_psnhi ");
				sql.append(" and convert(char,(" + tableName + "." + colName + " ))  = (" + wa_psnhi_b + ".vfldvalue");

				sql.append(") ");

				sql.append(" and " + wa_psnhi + ".pk_wa_grd = criterion.pk_wa_grd");
				// 当级别相关的人员相关属性的时候
				sql.append(" and((" + wa_psnhi + ".classtype = 1 and  criterion.pk_wa_prmlv = " + wa_psnhi_b + ".pk_wa_grdlv)");
				if (isMutiSec)
				{
					sql.append(" or(" + wa_psnhi + ".classtype = 2 and  criterion.pk_wa_seclv = " + wa_psnhi_b + ".pk_wa_grdlv)");
				}
				sql.append(")");
			}
		}
		tableNameSet.add(" bd_psndoc");
		tableNameSet.add(" hi_psnjob");
		tableNameSet.add(" hi_psnorg ");
		tableNameSet.add(" wa_prmlv  prmlv ");
		tableNameSet.add(" wa_criterion  criterion ");
		tableNameSet.add(" wa_grade_ver gradeVer ");
		if (isMutiSec)
		{
			tableNameSet.add("wa_seclv  seclv ");
			sql.append(" and seclv.pk_wa_seclv = criterion.pk_wa_seclv ");
			sql.append(" and seclv.pk_wa_grd = criterion.pk_wa_grd ");
		}
		sql.append(" and prmlv.pk_wa_prmlv = criterion.pk_wa_prmlv ");
		sql.append(" and prmlv.pk_wa_grd = criterion.pk_wa_grd  ");
		sql.append(" and gradeVer.pk_wa_gradever= criterion.pk_wa_gradever and gradeVer.effect_flag='Y'  ");
  
		StringBuffer sqlB = new StringBuffer();

		sqlB.append("select  distinct criterion.criterionvalue as wa_apply_money, bd_psndoc.pk_psndoc ,hi_psnjob.pk_psnjob  ");
		sqlB.append("  from  ");
		sqlB.append(FormatVO.formatArrayToString(tableNameSet.toArray(new String[tableNameSet.size()]), ""));
		sqlB.append(" where criterion.pk_wa_grd = '" + pk_wa_grd + "' ");
		sqlB.append(" and hi_psnorg.pk_psndoc =  bd_psndoc.pk_psndoc ");
		sqlB.append(" and (hi_psnorg.indocflag = 'Y' or hi_psnorg.indocflag = 'y') ");
		sqlB.append(" and (hi_psnorg.lastflag = 'Y' or hi_psnorg.lastflag = 'y') ");
		
		//可以对离职人员进行发薪，所以去掉该限制条件
//		sqlB.append(" and (hi_psnorg.endflag = 'N' or hi_psnorg.endflag = 'n') ");
		
		//永远关联人员工作记录
     	sqlB.append(" and hi_psnjob.pk_psndoc = bd_psndoc.pk_psndoc ");
     	
     	
		//兼职 \ 相关人员\ 主职 都能取数.-----只要确定工作记录就能取数 	
//		sqlB.append(" and hi_psnjob.lastflag = 'Y'   ");
			
			//最新的工作记录，该工作是主职。 结束了的工作记录也可以发薪，所以去掉结束标识
//		sqlB.append("  and hi_psnjob.endflag = 'N'   ");
			
//		sqlB.append("  and hi_psnjob.ismainjob = 'Y'  ");

		sqlB.append(sql);
		
		return sqlB.toString();
	}

	/**
	 * 得到人员属性对应的级别或者档别SQL片段
	 * 
	 * @author zhangg on 2010-4-22
	 * @param list
	 * @param classtype
	 * @param pk_wa_grd
	 */

	private String getSqlFregment(List<AppendableVO> list, String classtype, String pk_wa_grd)
	{

		boolean isPrmLv = classtype.equalsIgnoreCase(PRMTYPE);
		Set<String> tableNameSet = new HashSet<String>();

		StringBuffer sql = new StringBuffer();

		tableNameSet.add(" bd_psndoc");
		if (isPrmLv)
		{
			tableNameSet.add(" wa_prmlv ");
		}
		else
		{
			tableNameSet.add(" wa_seclv ");
		}

		boolean isPsnjobin = false;
		for (AppendableVO appendableVO : list)
		{

			Object tableName = appendableVO.getAttributeValue(InfoSetVO.TABLE_CODE);
			Object colName = appendableVO.getAttributeValue(CODE);
			Object datatype = appendableVO.getAttributeValue(DATA_TYPE);
			if (tableName != null && colName != null && datatype != null)
			{
				if (HI_PSNJOB.equalsIgnoreCase(tableName.toString().trim()))
				{
					isPsnjobin = true;
				}

				if (!BD_PSNDOC.equalsIgnoreCase(tableName.toString().trim()))
				{
					tableNameSet.add(tableName.toString());
				}

				String wa_psnhi_b = tableName.toString() + colName.toString() + PSNHI_B;
				String wa_psnhi = tableName.toString() + colName.toString() + PSNHI;
				tableNameSet.add(" wa_psnhi_b as " + wa_psnhi_b + " inner join wa_psnhi as " + wa_psnhi + " on " + wa_psnhi_b + ".pk_wa_psnhi=" + wa_psnhi + ".pk_wa_psnhi ");
				sql.append(" and " + tableName + "." + colName + "  = " + wa_psnhi_b + ".");
				sql.append("vfldvalue ");
				sql.append(" and " + wa_psnhi + ".pk_wa_grd = '" + pk_wa_grd + "'");
				if (isPrmLv)
				{
					sql.append(" and  wa_prmlv.pk_wa_prmlv = " + wa_psnhi_b + ".pk_wa_grdlv ");
				}
				else
				{
					sql.append(" and  wa_seclv.pk_wa_seclv = " + wa_psnhi_b + ".pk_wa_grdlv ");
				}
			}
		}
		StringBuffer sqlB = new StringBuffer();

		sqlB.append("select bd_psndoc.pk_psndoc ");
		if (isPrmLv)
		{
			sqlB.append(", wa_prmlv.pk_wa_prmlv as pk_wa_grdlv");
			sqlB.append(", wa_prmlv.levelname as wa_prmlv_apply");
		}
		else
		{
			sqlB.append(", wa_seclv.pk_wa_seclv as pk_wa_grdlv");
			sqlB.append(", wa_seclv.levelname as wa_seclv_apply");
		}

		sqlB.append("  from  ");
		sqlB.append(FormatVO.formatArrayToString(tableNameSet.toArray(new String[tableNameSet.size()]), ""));

		sqlB.append(" where " + (isPrmLv ? "wa_prmlv" : "wa_seclv") + ".pk_wa_grd = '" + pk_wa_grd + "' ");
		sqlB.append(" and (bd_psndoc.indocflag = 'Y' or bd_psndoc.indocflag = 'y') ");
		if (isPsnjobin)
		{
			sqlB.append(" and hi_psnjob.pk_psndoc = bd_psndoc.pk_psndoc ");
			sqlB.append(" and hi_psnjob.lastflag = 'Y' and hi_psnjob.endflag = 'N'");
		}
		sqlB.append(sql);

		return sqlB.toString();
	}

	/**
	 * 
	 * @author zhangg on 2010-4-22
	 * @param appendableVOs
	 * @return
	 */
	private boolean isRelatedWithPsnProperty(AppendableVO[] appendableVOs)
	{

		if (appendableVOs == null || appendableVOs.length == 0)
		{
			return false;// 没有数据， 其实通过正常的方法进入不可能为null
		}

		if (getAppendableVOsByClassType(appendableVOs, PRMTYPE).isEmpty())
		{
			// 级别没有人员属性关联
			return false;
		}
		// 查看是否多档
		boolean isMutiSec = UFBoolean.valueOf(appendableVOs[0].getAttributeValue(WaGradeVO.ISMULTSEC).toString()).booleanValue();

		if (isMutiSec)
		{// 是多档
			if (getAppendableVOsByClassType(appendableVOs, SECTYPE).isEmpty())
			{
				// 档别没有人员属性关联
				return false;
			}
		}
		return true;
	}

}
