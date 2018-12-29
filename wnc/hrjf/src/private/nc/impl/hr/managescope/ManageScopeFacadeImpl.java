package nc.impl.hr.managescope;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.itf.hr.managescope.IManagescopeFacade;
import nc.itf.hr.managescope.IManageScopeQueryService;
import nc.itf.hr.managescope.IManagescopeResultQry;
import nc.itf.hr.managescope.ManageScopeConst;
import nc.itf.om.IAOSQueryService;
import nc.itf.om.IAOSQueryService.OrgQueryMode;
import nc.itf.org.orgmodel.IOrgRelationTypeConst;
import nc.jdbc.framework.DataSourceCenter;
import nc.jdbc.framework.util.DBConsts;
import nc.md.data.criterion.QueryCondition;
import nc.md.data.criterion.expression.Restrictions;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.enumeration.PsnType;
import nc.vo.hr.managescope.HrRelationDeptVO;
import nc.vo.hr.managescope.HrRelationPsnVO;
import nc.vo.hr.managescope.ManagescopeBusiregionEnum;
import nc.vo.hr.managescope.ManagescopeTypeEnum;
import nc.vo.org.OrgVO;
import nc.vo.org.orgmodel.OrgRelationVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

/**
 * 业务委托对外接口实现类
 */

public class ManageScopeFacadeImpl implements IManagescopeFacade
{
    private IAOSQueryService aosQueryService;
    
    private IManageScopeQueryService manageScopeQueryService;
    
    private IManagescopeResultQry managescopeResultQry;
    
    private IMDPersistenceQueryService mdPersistenceQueryService;
    
    /**
     * 内部使用
     * 加上业务条件
     * @param busiregionEnum
     * @return
     */
    private String getBusinessCondition(ManagescopeBusiregionEnum busiregionEnum)
    {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }
    
    private IAOSQueryService getIAOSQueryService()
    {
        
        if (aosQueryService == null)
        {
            aosQueryService = NCLocator.getInstance().lookup(IAOSQueryService.class);
        }
        return aosQueryService;
    }
    
    private IMDPersistenceQueryService getIMDPersistenceQueryService()
    {
        
        if (mdPersistenceQueryService == null)
        {
            mdPersistenceQueryService = NCLocator.getInstance().lookup(IMDPersistenceQueryService.class);
        }
        return mdPersistenceQueryService;
    }
    
    /**
     * 内部使用
     * field 为 hi_psnjob.pk_psnjob
     * @param hrorgPk
     * @param attr
     * @param containsParttimeJob 是否包含兼职
     * @return minus
     */
    private String getInnerSQLFaster(String hrorgPk, String attr, boolean containsParttimeJob)
    {
        StringBuilder sb = new StringBuilder();
        String minus = " minus ";
        
        int databaseType = DataSourceCenter.getInstance().getDatabaseType();
        
        if (DBConsts.ORACLE != databaseType)
        {
            minus = " except ";
        }
        
        sb.append(" (hi_psnjob.pk_psnjob in (select hi_psnjob.pk_psnjob  from hi_psnjob where")
            .append(" hi_psnjob.pk_dept in (select rd.pk_dept from hr_relation_dept rd where rd.pk_hrorg='").append(hrorgPk)
            .append("' and rd.").append(attr).append("='Y')");
        
        if (!containsParttimeJob)
        {
            sb.append(" and hi_psnjob.ismainjob='Y'");
        }
        
        sb.append(minus)
            .append(" select rp.pk_psnjob from hr_relation_psn rp where")
            .append(" rp.")
            .append(attr)
            .append("='Y' and rp.pk_hrorg!='")
            .append(hrorgPk)
            .append("'")
            .append(" union")
            .append(" select rp.pk_psnjob from hr_relation_psn rp where rp.pk_hrorg='")
            .append(hrorgPk)
            .append("' and rp.")
            .append(attr)
            .append("='Y'))")
            .append(
                " and hi_psnjob." + ManageScopeConst.PSNJOB_SCOPE_CONDITION + " and hi_psnjob."
                    + ManageScopeConst.PSNJOB_AVAILABLE_CONDITION + " ");
        
        return sb.toString();
    }
    
    private String getInnerSQLFaster2(String attr, boolean containsParttimeJob)
    {
        StringBuilder sb = new StringBuilder();
        String minus = " minus ";
        
        int databaseType = DataSourceCenter.getInstance().getDatabaseType();
        
        if (DBConsts.ORACLE != databaseType)
        {
            minus = " except ";
        }
        
        sb.append(" (hi_psnjob.pk_psnjob in (select hi_psnjob.pk_psnjob  from hi_psnjob where")
            .append(" hi_psnjob.pk_dept in (select rd.pk_dept from hr_relation_dept rd where rd.pk_hrorg= ? ").append(" and rd.")
            .append(attr).append("='Y')");
        
        if (!containsParttimeJob)
        {
            sb.append(" and hi_psnjob.ismainjob='Y'");
        }
        
        sb.append(minus)
            .append(" select rp.pk_psnjob from hr_relation_psn rp where")
            .append(" rp.")
            .append(attr)
            .append("='Y' and rp.pk_hrorg!= ? ")
            .append(" union")
            .append(" select rp.pk_psnjob from hr_relation_psn rp where rp.pk_hrorg= ? ")
            .append(" and rp.")
            .append(attr)
            .append("='Y'))")
            .append(
                " and hi_psnjob." + ManageScopeConst.PSNJOB_SCOPE_CONDITION + " and hi_psnjob."
                    + ManageScopeConst.PSNJOB_AVAILABLE_CONDITION + " ");
        
        return sb.toString();
    }
    
    public IManageScopeQueryService getManagescopeQueryService()
    {
        if (manageScopeQueryService == null)
        {
            manageScopeQueryService = NCLocator.getInstance().lookup(IManageScopeQueryService.class);
        }
        
        return manageScopeQueryService;
    }
    
    public IManagescopeResultQry getManagescopeResultQry()
    {
        if (managescopeResultQry == null)
        {
            managescopeResultQry = NCLocator.getInstance().lookup(IManagescopeResultQry.class);
        }
        
        return managescopeResultQry;
    }
    
    @Override
    public boolean isEntityUsed(ManagescopeTypeEnum managescopeType, String entityPk) throws BusinessException
    {
        if (StringUtils.isBlank(entityPk))
        {
            return false;
        }
        
        QueryCondition orgRelationPost = new QueryCondition(OrgRelationVO.class);
        orgRelationPost.addCondition(Restrictions.eq(OrgRelationVO.SOURCER, entityPk))
            .addCondition(Restrictions.eq(OrgRelationVO.SOURCEENTITY, managescopeType.getEntityType()))
            .addCondition(Restrictions.eq(OrgRelationVO.PK_RELATIONTYPE, IOrgRelationTypeConst.HRCORPDEPTCONSIGN));
        
        List<OrgRelationVO> orgRelationVOPostList = (List) getIMDPersistenceQueryService().queryBillOfVOByCond(orgRelationPost, true);
        
        boolean isUsed = false;
        
        if (orgRelationVOPostList != null && orgRelationVOPostList.size() > 0)
        {
            isUsed = true;
        }
        
        return isUsed;
    }
    
    @Override
    public String[] queryHrorgsByAssgidPsnorgAndBusiregion(String psnorgPk, int assgid, ManagescopeBusiregionEnum busiregionEnum)
            throws BusinessException
    {
        String[] hrorgPks = null;
        String psnCondition = PsnJobVO.PK_PSNORG + "='" + psnorgPk + "' and " + PsnJobVO.ASSGID + "=" + assgid;
        
        // PsnJobVO psnjob = getIPsndocQryService().queryAvailablePsnjobByCondition(psnCondition, null)[0];
        PsnJobVO[] psnjobArray = getManagescopeResultQry().queryPsnjobByCondition(psnCondition, null);
        
        if (psnjobArray == null || psnjobArray.length != 1)
        {
//            Logger.error("此次查询的人员不存在或者和管理范围没有关系！psnorgPk=" + psnorgPk + " assgid=" + assgid);
//            throw new BusinessException(ResHelper.getString("6005mngscp", "06005mngscp0126")
//            /* @res "此次查询的人员不存在或者和管理范围没有关系！" */);
        	return null;
        }
        
        PsnJobVO psnjob = psnjobArray[0];
        
        // 兼职人员的人事业务没有HR组织
        if (!psnjob.getIsmainjob().booleanValue() && busiregionEnum == ManagescopeBusiregionEnum.psndoc)
        {
            return null;
        }
        
        // 相关人员的合同业务没有HR组织
        if (psnjob.getPsntype() == Integer.parseInt(PsnType.POI.getEnumValue().getValue())
            && ManagescopeBusiregionEnum.psnpact == busiregionEnum)
        {
            return null;
        }
        
        String attr = busiregionEnum.getCode() + ManageScopeConst.PROPERTY_BUSI;
        
        // 先查询人员委托关系表如果有则找到，如果没有则找部门委托关系表
        String condition =
            HrRelationPsnVO.PK_PSNORG + "='" + psnorgPk + "' and " + HrRelationPsnVO.ASSGID + "=" + assgid + " and " + attr + "='Y'";
        
        HrRelationPsnVO[] hrRelationPsnVOs = getManagescopeResultQry().queryPsnRelationByCondition(condition, null);
        
        if (hrRelationPsnVOs != null && hrRelationPsnVOs.length > 0)
        {
            hrorgPks = new String[hrRelationPsnVOs.length];
            for (int i = 0; i < hrorgPks.length; i++)
            {
                hrorgPks[i] = hrRelationPsnVOs[i].getPk_hrorg();
            }
        }
        else
        {
            // 找部门委托关系
            String pkDept = psnjob.getPk_dept();
            String deptCondition = HrRelationDeptVO.PK_DEPT + "='" + pkDept + "' and " + attr + "='Y'";
            HrRelationDeptVO[] hrRelationDeptVOs = getManagescopeResultQry().queryDeptRelationByCondition(deptCondition, null);
            if (hrRelationDeptVOs != null)
            {
                hrorgPks = new String[hrRelationDeptVOs.length];
                for (int i = 0; i < hrorgPks.length; i++)
                {
                    hrorgPks[i] = hrRelationDeptVOs[i].getPk_hrorg();
                }
            }
        }
        
        return hrorgPks;
    }
    
    @Override
    public String[] queryHrOrgsByDeptAndBusiregion(String pkDept, ManagescopeBusiregionEnum busiregionEnum) throws BusinessException
    {
        if (StringUtils.isBlank(pkDept) || busiregionEnum == null)
        {
            return null;
        }
        
        String attr = busiregionEnum.getCode() + ManageScopeConst.PROPERTY_BUSI;
        String condition = HrRelationDeptVO.PK_DEPT + "='" + pkDept + "' and " + attr + "='Y'";
        
        HrRelationDeptVO[] hrRelationDeptVOs = getManagescopeResultQry().queryDeptRelationByCondition(condition, null);
        
        if (hrRelationDeptVOs == null)
        {
            return null;
        }
        
        String[] hrorgPks = new String[hrRelationDeptVOs.length];
        
        for (int i = 0; i < hrorgPks.length; i++)
        {
            hrorgPks[i] = hrRelationDeptVOs[i].getPk_hrorg();
        }
        
        return hrorgPks;
    }
    
    @Override
    public String[] queryHrOrgsByPostAndBusiregion(String pkPost, ManagescopeBusiregionEnum busiregionEnum) throws BusinessException
    {
        if (StringUtils.isBlank(pkPost) || busiregionEnum == null)
        {
            return null;
        }
        
        StringBuilder whereCondStrSB = new StringBuilder();
        whereCondStrSB.append(" sourceentity = '").append(ManagescopeTypeEnum.post.getEntityType()).append("' and sourcer = '")
            .append(pkPost).append("' and pk_relationtype = '" + IOrgRelationTypeConst.HRCORPDEPTCONSIGN + "' ");
        
        whereCondStrSB
            .append(
                " and exists (select 1 from org_relation_attr where org_relation.pk_relation = org_relation_attr.pk_relation "
                    + " and pk_associateattr = '").append(busiregionEnum.getId()).append("')");
        
        List<OrgRelationVO> postRelationList =
            (List) getIMDPersistenceQueryService().queryBillOfVOByCond(OrgRelationVO.class, whereCondStrSB.toString(), true);
        
        String[] hrorgPks = null;
        
        if (postRelationList != null && postRelationList.size() > 0)
        {
            hrorgPks = new String[postRelationList.size()];
            for (int i = 0; i < hrorgPks.length; i++)
            {
                hrorgPks[i] = postRelationList.get(i).getTarget();
            }
        }
        
        return hrorgPks;
    }
    
    @Override
    public String[] queryHrOrgsByPostDeptAndBusiregion(String pkPost, String deptPk, ManagescopeBusiregionEnum busiregionEnum)
            throws BusinessException
    {
        String[] hrorgPks = queryHrOrgsByPostAndBusiregion(pkPost, busiregionEnum);
        
        if (hrorgPks == null)
        {
            hrorgPks = queryHrOrgsByDeptAndBusiregion(deptPk, busiregionEnum);
        }
        
        return hrorgPks;
    }
    
    @Override
    public String[] queryHrOrgsByPsnjobAndBusiregion(PsnJobVO psnjob, ManagescopeBusiregionEnum busiregionEnum) throws BusinessException
    {
        if (psnjob == null)
        {
            return null;
        }
        
        return queryHrOrgsByPostDeptAndBusiregion(psnjob.getPk_post(), psnjob.getPk_dept(), busiregionEnum);
    }
    
    @Override
    public String queryOtherPsnjobByHrorg(String hrorgPk, ManagescopeBusiregionEnum busiregionEnum, boolean includeHrBranch,
            boolean containsParttimeJob) throws BusinessException
    {
        if (includeHrBranch)
        {
            return queryOtherPsnjobByHrorg0(hrorgPk, busiregionEnum, containsParttimeJob);
        }
        
        OrgVO[] orgVOs = getIAOSQueryService().queryOrgByHROrgPK(hrorgPk, null, null, OrgQueryMode.Independent);
        String[] pkOrgArray = new String[orgVOs.length];
        
        for (int i = 0; i < orgVOs.length; i++)
        {
            pkOrgArray[i] = orgVOs[i].getPk_org();
        }
        
        InSQLCreator isc = new InSQLCreator();
        String attr = busiregionEnum.getCode() + ManageScopeConst.PROPERTY_BUSI;
        String pkOrgs = isc.getInSQL(pkOrgArray);
        StringBuilder deptPkSQL = new StringBuilder();
        // 此处可不处理启用问题
        deptPkSQL.append("(select pk_dept from org_dept where org_dept.pk_org in (" + pkOrgs + ")  ) dept");
        // 归这个hr组织所管理的并且排除掉行政组织是它的
        String whereConditon = " and not exists (select 1 from " + deptPkSQL + " where dept.pk_dept = hi_psnjob.pk_dept)";
        
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append("    select hi_psnjob.pk_psnjob  from hi_psnjob   where   ");
        // sb.append(getInnerSQL(hrorgPk, attr));
        sb.append(getInnerSQLFaster(hrorgPk, attr, containsParttimeJob));
        sb.append(whereConditon);
        sb.append(getBusinessCondition(busiregionEnum));
        sb.append(")");
        
        return sb.toString();
        
    }
    
    private String queryOtherPsnjobByHrorg0(String hrorgPk, ManagescopeBusiregionEnum busiregionEnum, boolean containsParttimeJob)
            throws BusinessException
    {
        String attr = busiregionEnum.getCode() + ManageScopeConst.PROPERTY_BUSI;
        
        String innerCode = getIAOSQueryService().queryInnerCodeByOrgPK(hrorgPk);
        StringBuilder deptPkSQL = new StringBuilder();
        
        // 此处可不处理启用问题
        deptPkSQL.append("(select pk_dept from org_dept where org_dept.pk_org in (select org_orgs.pk_org ");
        deptPkSQL.append("from org_orgs inner join org_adminorg on org_orgs.pk_org = org_adminorg.pk_adminorg ");
        deptPkSQL.append("where org_adminorg.innercode like '").append(innerCode).append("%' )  ) dept");
        
        // 归这个hr组织所管理的并且排除掉行政组织是它的
        String whereConditon = " and not exists (select 1 from " + deptPkSQL + " where dept.pk_dept = hi_psnjob.pk_dept)";
        
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append("    select hi_psnjob.pk_psnjob  from hi_psnjob   where   ");
        // sb.append(getInnerSQL(hrorgPk, attr));
        sb.append(getInnerSQLFaster(hrorgPk, attr, containsParttimeJob));
        sb.append(whereConditon);
        sb.append(getBusinessCondition(busiregionEnum));
        sb.append(")");
        
        return sb.toString();
    }
    
    /**
     * 根据 HR组织+HR业务 返回工作记录所在组织主键的sql片段<br>
     * pk_org会有重复
     * 返回结果示例如下： <br>
     * <code>
     * (select hi_psnjob.pk_org  from hi_psnjob   where ...)
     * </code>
     * @param hrorgPk HR组织PK
     * @param busiregionEnum HR业务
     * @param containsParttimeJob 是否包含兼职人员
     * @return String SQL片段
     * @throws BusinessException
     */
    @Override
    public String queryPkOrgsSQLByHrorgAndBusiregion(String hrorgPk, ManagescopeBusiregionEnum busiregionEnum, boolean containsParttimeJob)
            throws BusinessException
    {
        String attr = busiregionEnum.getCode() + ManageScopeConst.PROPERTY_BUSI;
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append("    select hi_psnjob.pk_org  from hi_psnjob   where   ");
        // sb.append(getInnerSQL(hrorgPk, attr));
        sb.append(getInnerSQLFaster(hrorgPk, attr, containsParttimeJob));
        sb.append(getBusinessCondition(busiregionEnum));
        sb.append(")");
        
        return sb.toString();
    }
    
    @Override
    public String queryPsnjobPksSQLByBusiregionWithMark(ManagescopeBusiregionEnum busiregionEnum, boolean containsParttimeJob)
            throws BusinessException
    {
        String attr = busiregionEnum.getCode() + ManageScopeConst.PROPERTY_BUSI;
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append("    select hi_psnjob.pk_psnjob  from hi_psnjob   where   ");
        sb.append(getInnerSQLFaster2(attr, containsParttimeJob));
        sb.append(getBusinessCondition(busiregionEnum));
        sb.append(")");
        
        return sb.toString();
    }
    
    /**
     * 根据 HR组织+HR业务 返回人员工作信息记录PK的sql片段
     * 返回结果示例如下： <br>
     * <code>
     * (select j.pk_psnjob  from hi_psnjob j where
     * ( j.pk_dept in (select rd.pk_dept from hr_relation_dept rd where rd.pk_hrorg='0001A7100000007G04WS' and rd.psndoc_busi='Y')
     * and not exists (select 1 from hr_relation_psn rp where rp.pk_psnjob=j.pk_psnjob and rp.psndoc_busi='Y' and rp.pk_hrorg!='0001A7100000007G04WS') )
     * or j.pk_psnjob in (select rp.pk_psnjob from hr_relation_psn rp where rp.pk_hrorg='0001A7100000007G04WS' and rp.psndoc_busi='Y') )
     * </code>
     * @param hrorgPk HR组织PK
     * @param busiregionEnum HR业务
     * @param containsParttimeJob 是否包含兼职人员
     * @return String 人员工作信息记录PK的sql片段
     * @throws BusinessException
     */
    @Override
    public String queryPsnjobPksSQLByHrorgAndBusiregion(String hrorgPk, ManagescopeBusiregionEnum busiregionEnum,
            boolean containsParttimeJob) throws BusinessException
    {
        String attr = busiregionEnum.getCode() + ManageScopeConst.PROPERTY_BUSI;
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append("    select hi_psnjob.pk_psnjob  from hi_psnjob   where   ");
        // sb.append(getInnerSQL(hrorgPk, attr));
        sb.append(getInnerSQLFaster(hrorgPk, attr, containsParttimeJob));
        sb.append(getBusinessCondition(busiregionEnum));
        sb.append(")");
        
        return sb.toString();
    }
    
    public void setManagescopeQueryService(IManageScopeQueryService manageScopeQueryService)
    {
        this.manageScopeQueryService = manageScopeQueryService;
    }
    
    public void setManagescopeResultQry(IManagescopeResultQry managescopeResultQry)
    {
        this.managescopeResultQry = managescopeResultQry;
    }
}
