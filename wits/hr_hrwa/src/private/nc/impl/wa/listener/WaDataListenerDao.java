package nc.impl.wa.listener;

import java.util.ArrayList;
import java.util.HashMap;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IWaBmfileQueryService;
import nc.jdbc.framework.DataSourceCenter;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.MapProcessor;
import nc.jdbc.framework.util.DBConsts;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hr.pub.WaBmFileOrgVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

/**
 * 薪资响应人事变动DAO
 */
public class WaDataListenerDao extends BaseDAOManager{

	private String getFilterSql(PsnJobVO psnjobBefore, PsnJobVO psnJobAfter,
			int changeDate) throws BusinessException {

		String sql = "  select wa_data.pk_wa_data,wa_period.cstartdate,wa_period.cenddate from  wa_data "
				+ "  inner join wa_waclass on wa_waclass.pk_wa_class = wa_data.pk_wa_class"
				+ "  inner join wa_periodscheme on wa_waclass.pk_periodscheme = wa_periodscheme.pk_periodscheme"
				+ "  inner join wa_period on wa_period.pk_periodscheme = wa_periodscheme.pk_periodscheme and  wa_period.cyear = wa_data.cyear and wa_period.cperiod = wa_data.cperiod"
				+ "  where   wa_data.checkflag = 'N'";
//		20160116  xiejie3  NCdp205574437  人员调配后再修改参数是否立即反应变化为25，人员在该日期前再次发生调配后，薪资档案中没有立即变			
//		原因：当变动人员业务参数为0时，不立即更新薪资档案人员工作记录，导致薪资档案存的是上一条工作记录 job1，而此时工作记录有两条job1，job2，
//		这时，再把参数改成31，增加人员工作记录job3，人事传过来的 工作记录 为 job2，job3，这时，用job2去薪资档案去匹配人，将匹配不到，
//		xxxx 错误方案》》》方案1：不再通过工作记录去更新薪资档案数据，根据人员主键查询，返回的是所有未审核的社保档案，然后再和业务参数进行比较是否符合要求。
//		xiejie3  采用方案2：方案1没有考虑兼职记录的情况，当薪资档案加入的是兼职记录的时候，只有兼职记录发生变化，才需要更新薪资档案。
//			 目前方案：根据传过来的工作记录，判断是主职还是兼职，然后查询相应的全部工作记录。
		if (psnjobBefore != null) {
//			sql += " and wa_data.pk_psnjob = '" + psnjobBefore.getPk_psnjob()
//					+ "'";
			sql += " and   wa_data.pk_psnjob in ( select hi_psnjob.pk_psnjob  from  hi_psnjob  where  hi_psnjob.pk_psndoc = '" + psnjobBefore.getPk_psndoc() +"' and hi_psnjob.ismainjob = '"+ psnjobBefore.getIsmainjob() +"'  )  ";
		} else {
			sql += " and wa_data.pk_psndoc = '" + psnJobAfter.getPk_psndoc()
					+ "'";
		}
//			end  
		GeneralVO[] generalVOs = executeQueryVOs(sql, GeneralVO.class);

		String cchangeDay = "" + changeDate;
		String cstartdate = "";
		String cenddate = "";
		String cchangeDate = "";
		if (changeDate < 10) {
			cchangeDay = "0" + changeDate;
		}
		ArrayList<String> waDataPkList = new ArrayList<String>();
		for (GeneralVO generalVO : generalVOs) {
			cstartdate = generalVO.getAttributeValue("cstartdate").toString();
			cenddate = generalVO.getAttributeValue("cenddate").toString();
			if (cstartdate.substring(0, 8).equals(

					psnJobAfter.getBegindate().toString().substring(0, 8))) {
				cchangeDate = cstartdate.substring(0, 8).concat(cchangeDay);
			} else {
				cchangeDate = cenddate.substring(0, 8).concat(cchangeDay);
			}
			if (cchangeDate.compareTo(psnJobAfter.getBegindate().toString()) >= 0) {
				waDataPkList.add(generalVO.getAttributeValue("pk_wa_data")
						.toString());
			}
		}
		String filterSql = "";
		if (waDataPkList.isEmpty()) {
			return filterSql;
		}
		InSQLCreator inC = new InSQLCreator();
		try {
			filterSql = " and pk_wa_data in ("
					+ inC.getInSQL(waDataPkList.toArray(new String[0])) + ")";

		} catch (BusinessException e) {
			throw new DAOException(e);
		} finally {
			try {
				inC.clear();
			} catch (Exception e2) {
				Logger.error(e2.getMessage(), e2);
			}
		}
		return filterSql;
	}

	/**
	 * 更新薪资档案需要按参数指定的日期过滤
	 * 
	 * @param psnjobBefore
	 *            PsnJobVO
	 * @param psnJobAfter
	 *            PsnJobVO
	 * @throws BusinessException
	 */
	public void updateWaDataByPsnJob(PsnJobVO psnjobBefore,
			PsnJobVO psnJobAfter, int changeDate) throws BusinessException {

		String filterSql = getFilterSql(psnjobBefore, psnJobAfter, changeDate);
		if (StringUtils.isEmpty(filterSql)) {
			return;
		}
		String sql = "";
		if (psnJobAfter == null) {
			// 兼职记录有可能被删光。
			sql = " delete from wa_data where  pk_psnjob = '"
					+ psnjobBefore.getPk_psnjob() + "' and checkflag = 'N'";
			sql += filterSql;
			getBaseDao().executeUpdate(sql);
		} else {
			sql = " update wa_data set pk_psnjob = '"
//		20160116  xiejie3  NCdp205574437  人员调配后再修改参数是否立即反应变化为25，人员在该日期前再次发生调配后，薪资档案中没有立即变			
//				原因：当变动人员业务参数为0时，不立即更新薪资档案人员工作记录，导致薪资档案存的是上一条工作记录 job1，而此时工作记录有两条job1，job2，
//				这时，再把参数改成31，增加人员工作记录job3，人事传过来的 工作记录 为 job2，job3，这时，用job2去薪资档案去匹配人，将匹配不到，
//				方案：不再通过工作记录去更新薪资档案数据，根据人员主键查询，返回的是所有未审核的社保档案，然后再和业务参数进行比较是否符合要求。
//					+ psnJobAfter.getPk_psnjob() + "' where pk_psnjob = '"
//					+ psnjobBefore.getPk_psnjob() + "' and checkflag = 'N'";
					+ psnJobAfter.getPk_psnjob() + "' where " +
					"  checkflag = 'N' ";
//		end  NCdp205574437
			sql += filterSql;
			getBaseDao().executeUpdate(sql);
			updateWaDataMulVer(psnJobAfter.getPk_psnjob(), filterSql);
		}
	}
	/**
	 * 更新薪资档案
	 *
	 * @param psnjobBefore PsnJobVO
	 * @param psnJobAfter PsnJobVO
	 * @throws BusinessException
	 */
	public void updateWaDataByPsnJob(PsnJobVO psnjobBefore,PsnJobVO psnJobAfter) throws BusinessException{

		String sql = "";
		if(psnJobAfter==null){
			//兼职记录有可能被删光。
			sql = " delete from wa_data where  pk_psnjob = '"+psnjobBefore.getPk_psnjob()+"' and checkflag = 'N'";
			getBaseDao().executeUpdate(sql);
		}else{
			sql = " update wa_data set pk_psnjob = '"+psnJobAfter.getPk_psnjob()+"' where pk_psnjob = '"+psnjobBefore.getPk_psnjob()+"' and checkflag = 'N'";
			getBaseDao().executeUpdate(sql);
			updateWaDataMulVer(psnJobAfter.getPk_psnjob(), null);
		}
	}
	/**
	 * 更新薪资档案
	 *
	 * @param psnjobBefore PsnJobVO
	 * @param psnJobAfter PsnJobVO
	 * @throws BusinessException
	 */
	public void updateWaDataByPsnDoc(PsnJobVO psnJobAfter, int changeDate)
			throws BusinessException {
		String filterSql = getFilterSql(null, psnJobAfter, changeDate);
		if (StringUtils.isEmpty(filterSql)
				// 2015-10-23 zhousze 没有加入薪资档案的人员就不同步薪资档案 begin
//				&& changeDate < psnJobAfter.getBegindate().getDay()
				// end
				) {
			return;
		}
		String sql = "update wa_data set pk_psnjob = '"+psnJobAfter.getPk_psnjob()+"' where pk_psndoc = '"+psnJobAfter.getPk_psndoc()+"' and checkflag = 'N'";
		sql += filterSql;
		getBaseDao().executeUpdate(sql);
		updateWaDataMulVer(psnJobAfter.getPk_psnjob(), filterSql);
	}

	/**
	 * 更新多版本信息
	 * @param pk_psnjob
	 * @throws DAOException
	 */
	public void updateWaDataMulVer(String pk_psnjob, String filterSql)
			throws DAOException {
		//IWaBmfileQueryService
		WaBmFileOrgVO payOrgVO = new WaBmFileOrgVO();
		String pk_financeorg = null;
		String pk_financedept = null;
		String fiporgvid = null;
		String fipdeptvid = null;
		String pk_costcenter = null;
		try {
			// 2015-10-23 zhousze 判断该VO是否为空 begin
			//20151212 shenliangc 人员离职记录没有对应财务组织，导致薪资档案更新报错。
			payOrgVO = NCLocator.getInstance().lookup(IWaBmfileQueryService.class).getPkFinanceOrg(pk_psnjob);
			if(payOrgVO!=null){
	    		pk_financeorg = payOrgVO.getPk_financeorg();
	    		pk_financedept = payOrgVO.getPk_financedept();
	    		fiporgvid = payOrgVO.getFiporgvid();
	    		fipdeptvid = payOrgVO.getFipdeptvid();
	    		StringBuffer sbd = new StringBuffer();
	    		sbd.append(" select resa_costcenter.pk_costcenter pk_costcenter from resa_costcenter, resa_ccdepts ");
	    		sbd.append(" where resa_costcenter.pk_costcenter = resa_ccdepts.pk_costcenter and resa_ccdepts.pk_dept in ");
	     		sbd.append(" (SELECT org_dept.pk_dept FROM hi_psnjob,org_dept,wa_data WHERE ");
	    		sbd.append(" wa_data.pk_psnjob = hi_psnjob.pk_psnjob AND hi_psnjob.pk_dept = org_dept.pk_dept");
	    		sbd.append(" and wa_data.checkflag = 'N' and wa_data.pk_psnjob = '"+pk_psnjob+"' )");
	    		sbd.append(" and resa_costcenter.enablestate = '2'");
	    		HashMap<String, String> map4lo = (HashMap<String, String>)getBaseDao().executeQuery(sbd.toString(), new MapProcessor()); 
	    		if (map4lo != null && map4lo.size()>0) {
	    			pk_costcenter = map4lo.get("pk_costcenter");
		    	}
			}
			// end
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		String sql = "";
		//20151212 shenliangc 人员离职记录没有对应财务组织，导致薪资档案更新报错。
		String sqlfi = (pk_financeorg != null ? " , pk_financeorg = '" + pk_financeorg + "'" : " , pk_financeorg = null ")
				+ (pk_financedept != null ? " , pk_financedept = '" + pk_financedept + "'" : " , pk_financedept = null ")
				+ (fiporgvid != null ? " , fiporgvid = '" + fiporgvid + "'" : " , fiporgvid = null ")
				+ (fipdeptvid != null ? " , fipdeptvid = '" + fipdeptvid + "'" : " , fipdeptvid = null ");
		if (getDataBaseType() == DBConsts.SQLSERVER) {
			sql = "UPDATE wa_data " + " SET workorg = (SELECT org_orgs.pk_org "
					+ "				FROM hi_psnjob,org_orgs "
					+ "				WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "					AND hi_psnjob.pk_org = org_orgs.pk_org " + " ) "
					+ "	, workorgvid = (SELECT org_orgs.pk_vid "
					+ "					FROM hi_psnjob,org_orgs "
					+ "					WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "						AND hi_psnjob.pk_org = org_orgs.pk_org " + " ) "
					+ "	, workdept = (	SELECT org_dept.pk_dept "
					+ "					FROM hi_psnjob,org_dept "
					+ "					WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "						AND hi_psnjob.pk_dept = org_dept.pk_dept " + " ) "
					+ "	, workdeptvid = (	SELECT org_dept.pk_vid "
					+ "						FROM hi_psnjob,org_dept "
					+ "						WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "							AND hi_psnjob.pk_dept = org_dept.pk_dept "+ " ) "
					+ sqlfi;
			if(StringUtils.isNotBlank(pk_costcenter)){
				sql +=", pk_liabilityorg = '" + pk_costcenter + "'"
					+ "	, pk_liabilitydept = (SELECT org_dept.pk_dept FROM hi_psnjob,org_dept WHERE " 
					+ "                     wa_data.pk_psnjob = hi_psnjob.pk_psnjob AND hi_psnjob.pk_dept = org_dept.pk_dept )"
					+ "	, libdeptvid = (SELECT org_dept.pk_vid FROM hi_psnjob,org_dept WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob AND "
					+ "						hi_psnjob.pk_dept = org_dept.pk_dept ) ";
			}else{
				sql +=", pk_liabilityorg = null"
					+ "	, pk_liabilitydept = null " 
					+ "	, libdeptvid = null ";
			}
			sql	+= " WHERE wa_data.checkflag = 'N' and pk_psnjob = ?";

		} else {
			sql = "UPDATE wa_data "
					+ " SET (workorg, workorgvid) = (SELECT org_orgs.pk_org,org_orgs.pk_vid "
					+ "				FROM hi_psnjob,org_orgs "
					+ "				WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "					AND hi_psnjob.pk_org = org_orgs.pk_org	) "
					+ "	, (workdept, workdeptvid) = (	SELECT org_dept.pk_dept,org_dept.pk_vid "
					+ "					FROM hi_psnjob,org_dept "
					+ "					WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "						AND hi_psnjob.pk_dept = org_dept.pk_dept	) "
					+ sqlfi;
			if(StringUtils.isNotBlank(pk_costcenter)){
				sql +=", pk_liabilityorg = '" + pk_costcenter + "'"
					+ "	, pk_liabilitydept = (SELECT org_dept.pk_dept FROM hi_psnjob,org_dept WHERE " 
					+ "                     wa_data.pk_psnjob = hi_psnjob.pk_psnjob AND hi_psnjob.pk_dept = org_dept.pk_dept )"
					+ "	, libdeptvid = (SELECT org_dept.pk_vid FROM hi_psnjob,org_dept WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob AND "
					+ "						hi_psnjob.pk_dept = org_dept.pk_dept ) ";
			}else{
				sql +=", pk_liabilityorg = null"
						+ "	, pk_liabilitydept = null " 
						+ "	, libdeptvid = null ";
				}
			sql	+= " WHERE wa_data.checkflag = 'N' and pk_psnjob = ?";
		}
		if (!StringUtils.isEmpty(filterSql)) {
			sql += filterSql;
		}
		SQLParameter param = new SQLParameter();
		param.addParam(pk_psnjob);
		getBaseDao().executeUpdate(sql, param);

	}

	/**
	 * 检查工作记录是否已引用
	 *
	 * @param psnjobBefore PsnJobVO
	 * @throws BusinessException
	 */
	public void checkPsnJobUsed(PsnJobVO psnjobBefore) throws BusinessException{
		String sql = " select pk_psndoc from wa_data where pk_psnjob = '"+psnjobBefore.getPk_psnjob()+"' and  checkflag = 'Y' ";
		if(isValueExist(sql)){
			throw new BusinessException(ResHelper.getString("6013salarypmt","06013salarypmt0269")/*@res "数据已被引用，不能删除！"*/);
		}
	}

	/**
	 * 检查人员信息是否已引用
	 *
	 * @param psnjobBefore PsnJobVO
	 * @throws BusinessException
	 */
	public void checkPsnUsed(PsnJobVO psnjobBefore) throws BusinessException{
		String sql = "select pk_psndoc from wa_data where pk_psndoc = '"+psnjobBefore.getPk_psndoc()+"'";
		if(isValueExist(sql)){
			throw new BusinessException(ResHelper.getString("6013salarypmt","06013salarypmt0269")/*@res "数据已被引用，不能删除！"*/);
		}
	}

	/**
	 * 检查工作记录是否已引用
	 *
	 * @param psnJobAfter PsnJobVO
	 * @throws BusinessException
	 */
	public void checkPsnChange(PsnJobVO psnJobAfter) throws BusinessException{
		String sql = "select pk_psndoc from wa_data where pk_psndoc = '"+psnJobAfter.getPk_psndoc()+"' and  pk_psnjob not in(select pk_psnjob from hi_psnjob where pk_psndoc = '"+psnJobAfter.getPk_psndoc()+"')";
		if(isValueExist(sql)){
			throw new BusinessException(ResHelper.getString("6013salarypmt","06013salarypmt0269")/*@res "数据已被引用，不能删除！"*/);
		}
	}

	private int getDataBaseType() {
		return DataSourceCenter.getInstance().getDatabaseType(null);
	}
}