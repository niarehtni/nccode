package nc.impl.om.hrdept;
  
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
 
import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.om.pub.LegacyDao;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.MultiLangUtil;
import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.DeptTransRefInfVO;
import nc.vo.om.hrdept.DeptTransRefNameVO;
import nc.vo.om.hrdept.DeptTransRuleVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.om.hrdept.PersonAdjustVO;
import nc.vo.om.hrdept.PostAdjustVO;
import nc.vo.om.job.JobVO;
import nc.vo.om.post.PostVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.pf.IPfRetCheckInfo;

import org.apache.commons.lang.ArrayUtils;

public class DeptDao extends LegacyDao
{
    
    /**
     * 获得人员调整信息<br>
     *  
     * @param takeOverDeptPK
     * @return
     * @throws DAOException
     */
    PersonAdjustVO[] retrievePersonAdjustVOByDeptPK(PsndocVO[] psndocVOs) throws DAOException
    {
        // StringBuilder sql = new StringBuilder();
        // sql.append("select ");
        // sql.append("hi_psnjob.pk_psnjob, bd_psndoc.code psncode, bd_psndoc.name psnname, bd_defdoc.name jobmode, ");
        // sql.append("hi_psnjob.pk_job, om_post.pk_post pk_oldpost, ");
        // sql.append("om_post.postcode oldpostcode, om_post.postname oldpostname, ");
        // sql.append("org_dept.pk_dept pk_olddept, org_dept.name olddeptname ");
        //
        // sql.append("from ");
        // sql.append("bd_psndoc inner join hi_psnorg ");
        // sql.append("on hi_psnorg.pk_psndoc = bd_psndoc.pk_psndoc and hi_psnorg.lastflag = 'Y' ");
        // sql.append("inner join hi_psnjob ");
        // sql.append("on hi_psnjob.pk_psnorg = hi_psnorg.pk_psnorg and hi_psnjob.lastflag = 'Y' ");
        // sql.append("left outer join om_post ");
        // sql.append("on om_post.pk_post = hi_psnjob.pk_post ");
        // sql.append("left outer join org_dept ");
        // sql.append("on hi_psnjob.pk_dept = org_dept.pk_dept ");
        // sql.append("left outer join bd_defdoc ");
        // sql.append("on hi_psnjob.jobmode = bd_defdoc.pk_defdoc ");
        //
        // sql.append("where ");
        // sql.append("hi_psnjob.pk_dept = ? ");
        //
        // String[] deptPKs = SuperVOHelper.getAttributeValueArray(mergedDept,
        // "pk_dept", String.class);
        // String[] inClauses = SQLHelper.joinToInSql(deptPKs);
        //
        // for (String inClause : inClauses) {
        // sql.append(" and pk_job in (").append(inClause).append(")");
        // }
        
        if (ArrayUtils.isEmpty(psndocVOs))
        {
            return new PersonAdjustVO[0];
        }
        
        // Map<String, InfoSetVO> jobModeCache = new HashMap<String,
        // InfoSetVO>();
        
        for (int i = 0; i < psndocVOs.length; i++)
        {
            if (psndocVOs[i].getPsnJobVO().getPk_post() != null)
            {
                StringBuilder sql = new StringBuilder();
                sql.append("select ");
                sql.append("om_post.pk_post pk_oldpost, om_post.postcode oldpostcode, org_dept.pk_dept pk_olddept,");
                sql.append("om_post.postname oldpostname, om_post.postname2 oldpostname2, om_post.postname3 oldpostname3, ");
                sql.append("org_dept.name olddeptname, org_dept.name2 olddeptname2, org_dept.name3 olddeptname3 ");
                sql.append("from ");
                sql.append("org_dept, om_post ");
                sql.append("where ");
                sql.append("org_dept.pk_dept = ? ");
                sql.append("and org_dept.pk_dept = ? ");
            }
            
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append("hi_psnjob.pk_psnjob, bd_psndoc.code psncode, bd_psndoc.name psnname, bd_defdoc.name jobmode, ");
        sql.append("hi_psnjob.pk_job, om_post.pk_post pk_oldpost, ");
        sql.append("om_post.postcode oldpostcode, om_post.postname oldpostname, ");
        sql.append("org_dept.pk_dept pk_olddept, org_dept.name olddeptname ");
        
        sql.append("from ");
        sql.append("bd_psndoc inner join hi_psnorg ");
        sql.append("on hi_psnorg.pk_psndoc = bd_psndoc.pk_psndoc and hi_psnorg.lastflag = 'Y' ");
        sql.append("inner join hi_psnjob ");
        sql.append("on hi_psnjob.pk_psnorg = hi_psnorg.pk_psnorg and hi_psnjob.lastflag = 'Y' ");
        sql.append("left outer join om_post ");
        sql.append("on om_post.pk_post = hi_psnjob.pk_post ");
        sql.append("left outer join org_dept ");
        sql.append("on hi_psnjob.pk_dept = org_dept.pk_dept ");
        sql.append("left outer join bd_defdoc ");
        sql.append("on hi_psnjob.jobmode = bd_defdoc.pk_defdoc ");
        
        sql.append("where ");
        sql.append("hi_psnjob.pk_dept = ? ");
        
        // String[] deptPKs = SuperVOHelper.getAttributeValueArray(mergedDept,
        // "pk_dept", String.class);
        // String[] inClauses = SQLHelper.joinToInSql(deptPKs);
        
        // for (String inClause : inClauses) {
        // sql.append(" and pk_job in (").append(inClause).append(")");
        // }
        
        BaseProcessor processor = new BeanListProcessor(PersonAdjustVO.class);
        
        @SuppressWarnings("unchecked")
        List<PersonAdjustVO> personList = (List<PersonAdjustVO>) getBaseDAO().executeQuery(sql.toString(), processor);
        
        return personList.toArray(new PersonAdjustVO[0]);
    }
    
    /**
     * 返回岗位数组Map，键是职务主键，值是职务对应的PostVO集合<br>
     * 
     * @param pk_jobs
     * @param pk_dept
     * @return
     * @throws BusinessException
     */
    Map<String, Set<PostVO>> retrievePostVOMapByJobDept(String[] pk_jobs, String pk_dept) throws BusinessException
    {
        final Map<String, Set<PostVO>> result = new HashMap<String, Set<PostVO>>();
        
        if (ArrayUtils.isEmpty(pk_jobs))
        {
            return result;
        }
        InSQLCreator isc = null;
        try
        {
            isc = new InSQLCreator();
            String insql = isc.getInSQL(pk_jobs);
            // String[] inClauses = SQLHelper.joinToInSql(pk_jobs);
            StringBuilder querySQL = new StringBuilder();
            querySQL.append("select pk_job, pk_post, postcode, postname from om_post where pk_dept = ? ");
            querySQL.append(" and  ");
            // for (String inClause : inClauses) {
            querySQL.append(" pk_job in (").append(insql).append(")");
            // querySQL.append(" or ");
            // }
            // querySQL.delete(querySQL.length() - 4, querySQL.length());
            querySQL.append("  ");
            
            SQLParameter parameter = new SQLParameter();
            parameter.addParam(pk_dept);
            
            getBaseDAO().executeQuery(querySQL.toString(), parameter, new BaseProcessor()
            {
                
                /** UID */
                private static final long serialVersionUID = 1L;
                
                @Override
                public Object processResultSet(ResultSet rs) throws SQLException
                {
                    while (rs.next())
                    {
                        PostVO postVO = new PostVO();
                        String pk_job = rs.getString("pk_job");
                        postVO.setPk_dept(pk_job);
                        postVO.setPk_post(rs.getString("pk_post"));
                        postVO.setPostcode(rs.getString("postcode"));
                        postVO.setPostname(rs.getString("postname"));
                        
                        Set<PostVO> postSet = null;
                        if (result.containsKey(pk_job))
                        {
                            postSet = result.get(pk_job);
                        }
                        else
                        {
                            postSet = new HashSet<PostVO>();
                        }
                        postSet.add(postVO);
                        
                        result.put(pk_job, postSet);
                    }
                    return null;
                }
            });
            
            return result;
        }
        finally
        {
            if (isc != null)
            {
                isc.clear();
            }
        }
    }
    
    /**
     * 返回组织下的部门数<br>
     * 
     * @param pk_org
     * @return
     * @throws DAOException
     */
    int retrieveDeptCountByOrgPK(String pk_org) throws DAOException
    {
        String sql = "select count(*) from org_dept where pk_org = ?";
        SQLParameter parameter = new SQLParameter();
        parameter.addParam(pk_org);
        
        // 返回行数
        Integer result = (Integer) getBaseDAO().executeQuery(sql, parameter, new ColumnProcessor());
        return result == null ? 0 : result;
    }
    
    /**
     * 通过部门主键返回岗位VO<br>
     * 
     * @param deptPK
     * @return
     * @throws BusinessException
     */
    PostAdjustVO[] retrievePostVOByDeptPK(String... deptPK) throws BusinessException
    {
        StringBuilder querySQL = new StringBuilder();
        querySQL.append("select om_post.postcode, om_post.postname, om_post.postname2, om_post.postname3, ");
        querySQL.append("om_post.pk_dept, om_post.pk_post, org_dept.code as deptcode, ");
        querySQL.append("org_dept.name as deptname, org_dept.name2 as deptname2, org_dept.name3 as deptname3 ");
        querySQL.append("from om_post inner join org_dept on om_post.pk_dept = org_dept.pk_dept where (om_post." + PostVO.HRCANCELED
            + " = 'N' or om_post." + PostVO.HRCANCELED + " = '~') ");
        
        InSQLCreator isc = null;
        try
        {
            isc = new InSQLCreator();
            String insql = isc.getInSQL(deptPK);
            
            // String[] inClauses = SQLHelper.joinToInSql(deptPK);
            querySQL.append(" and  ");
            // for (String inClause : inClauses) {
            querySQL.append(" om_post.pk_dept in (").append(insql).append(") ");
            // querySQL.append(" or ");
            // }
            // querySQL.delete(querySQL.length() - 4, querySQL.length());
            querySQL.append("  ");
            
            BaseProcessor processor = new BeanListProcessor(PostAdjustVO.class);
            
            @SuppressWarnings("unchecked")
            List<PostAdjustVO> resultVO = (List<PostAdjustVO>) getBaseDAO().executeQuery(querySQL.toString(), processor);
            
            if (null == resultVO || resultVO.isEmpty())
            {
                return new PostAdjustVO[0];
            }
            
            return resultVO.toArray(new PostAdjustVO[0]);
        }
        finally
        {
            if (isc != null)
            {
                isc.clear();
            }
        }
    }
    
    /**
     * 根据PK删除岗位，用于部门合并使用<br>
     * 
     * @param postPKs
     * @throws BusinessException
     */
    void deletePostByPK(String[] postPKs) throws BusinessException
    {
        
        InSQLCreator isc = null;
        try
        {
            isc = new InSQLCreator();
            String insql = isc.getInSQL(postPKs);
            
            // String[] inClauses = SQLHelper.joinToInSql(postPKs);
            StringBuilder updateSQL = new StringBuilder();
            updateSQL.append("delete from om_post where ");
            
            updateSQL.append("  ");
            // for (String inClause : inClauses) {
            updateSQL.append(" pk_post in (").append(insql).append(")");
            // updateSQL.append(" or ");
            // }
            // updateSQL.delete(updateSQL.length() - 4, updateSQL.length());
            updateSQL.append("  ");
            
            // 执行删除
            getBaseDAO().executeUpdate(updateSQL.toString());
        }
        finally
        {
            if (isc != null)
            {
                isc.clear();
            }
        }
    }
    
    /**
     * 更新岗位编码名称，用于部门合并使用<br>
     * 
     * @param postAdjustVO
     * @throws DAOException
     */
    void updatePostCodeName(PostAdjustVO postAdjustVO) throws DAOException
    {
        String sql = "update om_post set postcode = ?, postname = ? where pk_post = ?";
        SQLParameter parameter = new SQLParameter();
        parameter.addParam(postAdjustVO.getPostcode());
        parameter.addParam(postAdjustVO.getPostname());
        parameter.addParam(postAdjustVO.getPk_post());
        // 执行更新
        getBaseDAO().executeUpdate(sql, parameter);
    }
    
    /**
     * 更新該部門下人員工作記錄的所屬群組，用于部門群組有更新時<br>
     * 
     * @param postAdjustVO
     * @throws DAOException
     */
    void updateJobGroup(String glbdef11,String dept) throws DAOException
    {
        String sql = "update hi_psnjob set jobglbdef4 = ? WHERE hi_psnjob.pk_dept = ?";
        SQLParameter parameter = new SQLParameter();
        parameter.addParam(glbdef11);
        parameter.addParam(dept);
        // 执行更新
        getBaseDAO().executeUpdate(sql, parameter);
    }
    
    /**
     * 根据岗位主键来撤销岗位<br>
     * 
     * @param postPKs
     * @throws BusinessException
     */
    void cancelPostByPK(String[] postPKs) throws BusinessException
    {
        
        InSQLCreator isc = null;
        try
        {
            isc = new InSQLCreator();
            String insql = isc.getInSQL(postPKs);
            
            // String[] inClauses = SQLHelper.joinToInSql(postPKs);
            StringBuilder updateSQL = new StringBuilder();
            updateSQL.append("update om_post set " + PostVO.HRCANCELED + " = 'Y', " + PostVO.HRCANCELDATE + " = ? where ");
            
            updateSQL.append("  ");
            // for (String inClause : inClauses) {
            updateSQL.append(" pk_post in (").append(insql).append(")");
            // updateSQL.append(" or ");
            // }
            // updateSQL.delete(updateSQL.length() - 4, updateSQL.length());
            updateSQL.append("  ");
            
            UFLiteralDate now = new UFLiteralDate(new Date());
            
            SQLParameter parameter = new SQLParameter();
            parameter.addParam(now);
            
            // 执行撤销
            getBaseDAO().executeUpdate(updateSQL.toString(), parameter);
        }
        finally
        {
            if (isc != null)
            {
                isc.clear();
            }
        }
    }
    
    AggHRDeptVO[] retrieveDeptVOByAOSInnerCode(String innerCode, boolean includeCanceled) throws DAOException
    {
        StringBuilder querySQL = new StringBuilder();
        querySQL.append("select org_dept.* ");
        querySQL.append("from ");
        querySQL.append("org_dept ");
        querySQL.append("inner join org_orgs on org_orgs.pk_org = org_dept.pk_org ");
        querySQL.append("inner join org_adminorg on org_orgs.pk_org = org_adminorg.pk_adminorg ");
        querySQL.append("where ");
        querySQL.append("org_adminorg.innercode like " + "'" + innerCode + "%'");
        querySQL.append("and org_dept.enablestate = 2 ");
        if (!includeCanceled)
        {
            querySQL.append("and org_dept.hrcanceled = 'N' ");
        }
        querySQL.append("and (org_orgs.orgtype4 = 'N' or org_adminorg.innercode = '" + innerCode + "') ");
        querySQL.append("and org_adminorg.pk_adminorg not in ( ");
        querySQL.append("select ");
        querySQL.append("aosm.pk_adminorg ");
        querySQL.append("from ");
        querySQL.append("( select ");
        querySQL.append("aos.code, aos.innercode, len(aos.innercode) as innercodelen ");
        querySQL.append("from ");
        querySQL.append("org_orgs org inner join org_adminorg aos on org.pk_org = aos.pk_adminorg ");
        querySQL.append("where ");
        querySQL.append("aos.innercode like '" + innerCode + "%' and aos.innercode <> '" + innerCode + "' and orgtype4 = 'Y' ) ");
        querySQL.append(" sub_hrorg, org_adminorg aosm ");
        querySQL.append("where ");
        querySQL.append("sub_hrorg.innercode = substring(aosm. innercode , 1, sub_hrorg.innercodelen)) ");
        BaseProcessor processor = new BeanListProcessor(HRDeptVO.class);
        
        @SuppressWarnings("unchecked")
        List<HRDeptVO> result = (List<HRDeptVO>) getBaseDAO().executeQuery(querySQL.toString(), processor);
        List<AggHRDeptVO> aggList = new ArrayList<AggHRDeptVO>();
        for (HRDeptVO deptVO : result)
        {
            AggHRDeptVO aggVO = new AggHRDeptVO();
            aggVO.setParentVO(deptVO);
            aggList.add(aggVO);
        }
        return aggList.toArray(new AggHRDeptVO[0]);
        
    }
    
    /**
     * 当前组织下的所有部门(不包含下级)
     * 
     * @param innerCode
     * @param includeCanceled
     * @return
     * @throws DAOException
     */
    AggHRDeptVO[] retrieveDeptVOByCurrAOSInnerCode(String pk_adminorg, boolean includeCanceled) throws DAOException
    {
        StringBuilder querySQL = new StringBuilder();
        querySQL.append("select org_dept.* ");
        querySQL.append("from ");
        querySQL.append("org_dept ");
        querySQL.append("inner join org_orgs on org_orgs.pk_org = org_dept.pk_org ");
        querySQL.append("inner join org_adminorg on org_orgs.pk_org = org_adminorg.pk_adminorg ");
        querySQL.append("where ");
        querySQL.append("org_adminorg.pk_adminorg = '" + pk_adminorg + "'");
        querySQL.append("and org_dept.enablestate = 2 ");
        if (!includeCanceled)
        {
            querySQL.append("and org_dept.hrcanceled = 'N' ");
        }
        querySQL.append("order by org_dept.displayorder, org_dept.code");
        BaseProcessor processor = new BeanListProcessor(HRDeptVO.class);
        
        @SuppressWarnings("unchecked")
        List<HRDeptVO> result = (List<HRDeptVO>) getBaseDAO().executeQuery(querySQL.toString(), processor);
        List<AggHRDeptVO> aggList = new ArrayList<AggHRDeptVO>();
        for (HRDeptVO deptVO : result)
        {
            AggHRDeptVO aggVO = new AggHRDeptVO();
            aggVO.setParentVO(deptVO);
            aggList.add(aggVO);
        }
        return aggList.toArray(new AggHRDeptVO[0]);
    }
    
    /**
     * 寻找到指定部门范围内的存在未入档人员的部门
     * 
     * @param selDeptPks
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> getNotIndocDeptPks(String[] selDeptPks) throws BusinessException
    {
        // 转移部门主键临时表
        InSQLCreator isc = null;
        try
        {
            isc = new InSQLCreator();
            String temptable = isc.getInSQL(selDeptPks);
            String sql =
                "select distinct "
                    + SQLHelper.getMultiLangNameColumn("org_dept." + HRDeptVO.NAME)
                    + " from hi_psnjob inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg inner join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept where hi_psnorg.indocflag='N' and hi_psnjob.pk_dept  in("
                    + temptable + ") ";
            return (ArrayList<String>) new BaseDAO().executeQuery(sql, new ColumnListProcessor());
        }
        finally
        {
            if (isc != null)
            {
                isc.clear();
            }
        }
    }
    
    /**
     * 查询部门/岗位下活动的单据数量,包括 [自由 /提交/ 审批中 /审批通过]
     * 
     * @param isDeptOrPost true-部门 false-岗位
     * @param pk 部门/岗位pk
     * @return int
     * @throws BusinessException
     */
    @SuppressWarnings("unchecked")
    protected List<String> getBillCount(String[] pks) throws BusinessException
    {
        InSQLCreator isc = null;
        try
        {
            isc = new InSQLCreator();
            String temptable = isc.getInSQL(pks);
            String cond =
                " in ( " + IPfRetCheckInfo.NOSTATE + " , " + IPfRetCheckInfo.GOINGON + " , " + IPfRetCheckInfo.COMMIT + ", "
                    + IPfRetCheckInfo.PASSING + " ) and org_dept.pk_dept in (" + temptable + ")";
            String sql =
                "select " + SQLHelper.getMultiLangNameColumn("org_dept." + HRDeptVO.NAME)
                    // 入职申请单
                    + " from hi_entryapply inner join hi_psnjob on hi_entryapply.pk_psnjob = hi_psnjob.pk_psnjob "
                    + "inner join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept where hi_entryapply.approve_state " + cond
                    // 转正申请单
                    + " union select " + SQLHelper.getMultiLangNameColumn("org_dept." + HRDeptVO.NAME)
                    + " from hi_regapply inner join hi_psnjob on hi_regapply.pk_psnjob = hi_psnjob.pk_psnjob "
                    + "inner join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept where hi_regapply.approve_state " + cond
                    // 调配/离职申请单
                    + " union select " + SQLHelper.getMultiLangNameColumn("org_dept." + HRDeptVO.NAME)
                    + " from hi_stapply inner join hi_psnjob on hi_stapply.pk_psnjob = hi_psnjob.pk_psnjob "
                    + "inner join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept where hi_stapply.approve_state " + cond
                    // 兼职申请单
                    + " union select " + SQLHelper.getMultiLangNameColumn("org_dept." + HRDeptVO.NAME)
                    + " from hi_partapply inner join hi_psnjob on hi_partapply.pk_psnjob = hi_psnjob.pk_psnjob "
                    + "inner join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept where hi_partapply.approve_state " + cond;
            return (ArrayList<String>) new BaseDAO().executeQuery(sql, new ColumnListProcessor());
        }
        finally
        {
            if (isc != null)
            {
                isc.clear();
            }
        }
        
        // String cond = " 1=1 ";
        // cond = " pk_dept = '" + pk + "' ";
        // String sql =
        // " select sum( countrow ) countrow from ( ( select count(*) as countrow from hi_entryapply where approve_state in ( "
        // + IPfRetCheckInfo.NOSTATE
        // + " , "
        // + IPfRetCheckInfo.GOINGON
        // + " , "
        // + IPfRetCheckInfo.COMMIT
        // + " , "
        // + IPfRetCheckInfo.PASSING
        // + ") and pk_psnjob in (select pk_psnjob from hi_psnjob where "
        // + cond
        // + " ) ) union ( select count(*) as countrow from hi_regapply where approve_state in ( "
        // + IPfRetCheckInfo.NOSTATE
        // + " , "
        // + IPfRetCheckInfo.GOINGON
        // + " , "
        // + IPfRetCheckInfo.COMMIT
        // + ", "
        // + IPfRetCheckInfo.PASSING
        // + " ) and pk_psnjob in (select pk_psnjob from hi_psnjob where "
        // + cond
        // + " ) ) union ( select count(*) as countrow from hi_stapply where approve_state in ( "
        // + IPfRetCheckInfo.NOSTATE
        // + " , "
        // + IPfRetCheckInfo.GOINGON
        // + " , "
        // + IPfRetCheckInfo.COMMIT
        // + " , "
        // + IPfRetCheckInfo.PASSING
        // + " ) and pk_psnjob in (select pk_psnjob from hi_psnjob where " + cond + " ) ) ) temp ";
        // @SuppressWarnings("rawtypes")
        // HashMap hm = (HashMap) NCLocator.getInstance().lookup(IPersistenceHome.class).executeQuery(sql, new
        // MapProcessor());
        // Integer rowCount = (Integer) hm.get("countrow");
        // return rowCount == null ? 0 : rowCount;
    }
    
    /**
     * 根据转移规则查询转移部门时需要匹配的职务参照信息
     * 
     * @param ruleVO
     * @param pk_defdoclist
     * @return
     * @throws NamingException
     */
    public DeptTransRefNameVO queryTransDutyRefNameVO(DeptTransRuleVO ruleVO) throws DAOException, BusinessException
    {
        // 转移部门主键临时表
        InSQLCreator isc = null;
        DeptTransRefNameVO refNameVO = null;
        try
        {
            isc = new InSQLCreator();
            String temptable = isc.getInSQL(ruleVO.getPk_depts());
            String sql =
                "select distinct jobcode,jobname"
                    // 当前语种顺序号后缀
                    + MultiLangUtil.getCurrentLangSeqSuffix()
                    + " ,pk_job  from om_job where pk_org = ? and  pk_job in ( select pk_job from om_post where pk_dept in ( " + temptable
                    + ")) ";
            
            SQLParameter parameter = new SQLParameter();
            parameter.addParam(ruleVO.getOldorg());
            
            refNameVO = (DeptTransRefNameVO) getBaseDAO().executeQuery(sql, parameter, new ResultSetProcessor()
            {
                private static final long serialVersionUID = 1L;
                
                @Override
                public Object handleResultSet(ResultSet rs) throws SQLException
                {
                    DeptTransRefNameVO refNameVO = null;
                    List<DeptTransRefInfVO> result = new ArrayList<DeptTransRefInfVO>();
                    DeptTransRefInfVO refInfVO = null;
                    while (rs.next())
                    {
                        refInfVO = new DeptTransRefInfVO();
                        refInfVO.setOldcode(rs.getString(JobVO.JOBCODE));
                        refInfVO.setOldname(rs.getString(JobVO.JOBNAME + MultiLangUtil.getCurrentLangSeqSuffix()));
                        refInfVO.setOldPK(rs.getString(JobVO.PK_JOB));
                        refInfVO.setPkDoc(DeptTransRefNameVO.REF_DEFDOCLIST_JOB);
                        result.add(refInfVO);
                    }
                    if (result != null && result.size() > 0)
                    {
                        refNameVO = new DeptTransRefNameVO();
                        //
                        refNameVO.setPkDoc(DeptTransRefNameVO.REF_DEFDOCLIST_JOB);
                        //
                        refNameVO.setReftype(ResHelper.getString("60050job", "060050job0165")/* "职务(设置业务单元主键)" */);
                        refNameVO.setDoccode("");
                        refNameVO.setDocname(ResHelper.getString("common", "UC000-0003300")/* @res "职务" */);
                        //
                        refNameVO.setMatchstate(DeptTransRefNameVO.MATCHSTATE_NONE);
                        refNameVO.setTransRefInfVOs(result.toArray(new DeptTransRefInfVO[0]));
                    }
                    return refNameVO;
                }
            });
        }
        finally
        {
            if (isc != null)
            {
                isc.clear();
            }
        }
        return refNameVO;
    }
    
    public String[] getFieldValue(String field, String tableName, String condition) throws BusinessException
    {
        String querySQL = "select " + field + " from " + tableName;
        if (condition != null)
        {
            querySQL += " where " + condition;
        }
        @SuppressWarnings("unchecked")
        ArrayList<String> alist = (ArrayList<String>) new BaseDAO().executeQuery(querySQL, new ColumnListProcessor());
        
        if (alist != null && alist.size() > 0)
        {
            return alist.toArray(new String[alist.size()]);
        }
        return null;
    }
    
    /**
     * 部门编码唯一性校验<br>
     * 
     * @param pk_org
     * @param deptcodes
     * @param pk_depts
     * @return
     * @throws DbException
     */
    public String[] deptCodeUniqueValidate(String pk_org, String[] deptcodes, String[] pk_depts) throws BusinessException
    {
        String[] existsDeptCodes = getFieldValue("code", "org_dept", "pk_org = '" + pk_org + "'");
        if (ArrayUtils.isEmpty(existsDeptCodes))
        {
            return new String[0];
        }
        Set<String> deptcodeCache = new HashSet<String>();
        Collections.addAll(deptcodeCache, existsDeptCodes);
        Set<String> codeErrorSet = new HashSet<String>();
        for (int i = 0; i < deptcodes.length; i++)
        {
            if (deptcodeCache.contains(deptcodes[i]))
            {
                codeErrorSet.add(pk_depts[i]);
            }
        }
        
        return codeErrorSet.toArray(new String[0]);
    }
    
    /**
     * 部门名称唯一性校验<br>
     * 
     * @param pk_org
     * @param deptnames
     * @return
     * @throws DbException
     */
    public String[] deptNameUniqueValidate(String pk_org, String[] deptnames, String[] pk_depts) throws BusinessException
    {
        String[] existsDeptNames = getFieldValue("name", "org_dept", "pk_org = '" + pk_org + "'");
        if (ArrayUtils.isEmpty(existsDeptNames))
        {
            return new String[0];
        }
        Set<String> deptnameCache = new HashSet<String>();
        Collections.addAll(deptnameCache, existsDeptNames);
        Set<String> nameErrorSet = new HashSet<String>();
        for (int i = 0; i < deptnames.length; i++)
        {
            if (deptnameCache.contains(deptnames[i]))
            {
                nameErrorSet.add(pk_depts[i]);
            }
        }
        
        return nameErrorSet.toArray(new String[0]);
    }
    
    /**
     * 返回有数据操作权限的部门
     * 
     * @param deptVOs
     * @param wherePower
     * @return
     * @throws BusinessException
     */
    public HRDeptVO[] hasPowerDepts(HRDeptVO[] deptVOs, String wherePower) throws BusinessException
    {
        StringBuilder querySQL = new StringBuilder();
        querySQL.append("select org_dept.pk_dept ");
        querySQL.append("from ");
        querySQL.append("org_dept ");
        querySQL.append("where ");
        querySQL.append(wherePower);
        BaseProcessor processor = new BeanListProcessor(HRDeptVO.class);
        
        @SuppressWarnings("unchecked")
        List<HRDeptVO> result = (List<HRDeptVO>) getBaseDAO().executeQuery(querySQL.toString(), processor);
        List<HRDeptVO> deptList = new ArrayList<HRDeptVO>();
        List<String> deptPowerList = new ArrayList<String>();
        for (HRDeptVO deptVO : result)
        {
            deptPowerList.add(deptVO.getPk_dept());
        }
        for (HRDeptVO deptVO : deptVOs)
        {
            if (deptPowerList.contains(deptVO.getPk_dept()))
            {
                deptList.add(deptVO);
            }
            else
            {
                continue;
            }
        }
        return deptList.toArray(new HRDeptVO[0]);
    }
    
    public HRDeptVO[] queryManagedDeptsByPersonPK(String[] deptPKs) throws BusinessException
    {
        InSQLCreator isc = new InSQLCreator();
        String insql = isc.getInSQL(deptPKs);
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select org_dept.* from org_dept inner join org_adminorg on org_dept.pk_org = org_adminorg.pk_adminorg");
        sqlbuf.append(" where org_dept.pk_dept in (" + insql + ") and hrcanceled ='N'");
        
        BaseProcessor processor = new BeanListProcessor(HRDeptVO.class);
        
        @SuppressWarnings("unchecked")
        List<HRDeptVO> result = (List<HRDeptVO>) getBaseDAO().executeQuery(sqlbuf.toString(), processor);
        return result == null ? null : result.toArray(new HRDeptVO[0]);
    }
    
    /**
     * 根据条件查询某个HR组织所管理范围的部门——heqiaoa 2014-7-28
     * @param pk_orgadmin
     * @param includeCanceled
     * @param wherePart
     * @return AggHRDeptVO[]
     * @throws BusinessException
     */
    public AggHRDeptVO[]  queryDeptInfoByOrgAdminPKandCondition(String pk_orgadmin, 
    		boolean includeCanceled, String wherePart) 
    		throws BusinessException{
    	//获取innercode
    	String innerCode = nc.vo.om.pub.SQLHelper.getOrgInnercode(pk_orgadmin);
    	//构造SQL
    	StringBuilder querySQL = new StringBuilder();
		querySQL.append("select org_dept.* ");
		querySQL.append("from ");
		querySQL.append("org_dept ");
		querySQL.append("inner join org_orgs on org_orgs.pk_org = org_dept.pk_org ");
		querySQL.append("inner join org_adminorg on org_orgs.pk_org = org_adminorg.pk_adminorg ");
		querySQL.append("and org_adminorg.enablestate = 2 ");
		querySQL.append("where ");
        querySQL.append("org_adminorg.innercode like " + "'" + innerCode + "%' ");
        querySQL.append("and org_dept.enablestate = 2 ");
        querySQL.append("and (org_orgs.orgtype4 = 'N' or org_adminorg.innercode = '" + innerCode + "') ");
        //接上WherePart
      	if(!StringUtil.isEmpty(wherePart)){
      		querySQL.append("and " + wherePart + " ");
      	}
      	if (!includeCanceled) {
      		querySQL.append("and org_dept.hrcanceled = 'N' ");
      	}
        querySQL.append("and org_adminorg.pk_adminorg not in ( ");
        querySQL.append(	"select ");
        querySQL.append(	"aosm.pk_adminorg ");
        querySQL.append(	"from ");
        querySQL.append(		"( select ");
        querySQL.append(		"aos.code, aos.innercode, len(aos.innercode) as innercodelen ");
        querySQL.append(		"from ");
        querySQL.append(		"org_orgs org inner join org_adminorg aos on org.pk_org = aos.pk_adminorg ");
        querySQL.append(		"where ");
        querySQL.append(		"aos.innercode like '" + innerCode + "%' and aos.innercode <> '");
        querySQL.append( 		innerCode + "' and orgtype4 = 'Y' ) ");
        querySQL.append(		"sub_hrorg, org_adminorg aosm ");
        querySQL.append(		"where ");
        querySQL.append(		"sub_hrorg.innercode = substring(aosm. innercode , 1, sub_hrorg.innercodelen)) ");
        querySQL.append("and org_adminorg.pk_adminorg in ");
        querySQL.append("	(select pk_adminorg from org_admin_enable) ");
        querySQL.append("order by ");
        querySQL.append("org_orgs.code, org_dept.displayorder, org_dept.code ");
        BaseProcessor processor = new BeanListProcessor(HRDeptVO.class);
        
        @SuppressWarnings("unchecked")
        List<HRDeptVO> result = (List<HRDeptVO>) getBaseDAO().executeQuery(querySQL.toString(), processor);
        List<AggHRDeptVO> aggList = new ArrayList<AggHRDeptVO>();
        for (HRDeptVO deptVO : result) {
        	AggHRDeptVO aggVO = new AggHRDeptVO();
        	aggVO.setParentVO(deptVO);
        	aggList.add(aggVO);
        }		
		return aggList.toArray(new AggHRDeptVO[0]);
    }

}
