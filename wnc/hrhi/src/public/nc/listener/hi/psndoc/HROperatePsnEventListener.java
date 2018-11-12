package nc.listener.hi.psndoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.hi.IPersonRecordService;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.pubitf.uapbd.IPsndocHRPubService;
import nc.vo.bd.psn.PsnjobVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.pub.HiBatchEventValueObject;
import nc.vo.hi.pub.HiEventValueObject;
import nc.vo.hi.pub.IHiEventType;
import nc.vo.hr.infoset.DefineMap;
import nc.vo.om.pub.SuperVOHelper;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * HR操作人员信息时监听
 * 
 * @author: fengwei
 * @date: 2010-6-24 下午06:47:37
 * @version 最后修改日期
 * @see xxxxxxx
 * @since 从产品的V60版本，此类被添加进来。（可选）
 */
public class HROperatePsnEventListener implements IBusinessListener
{
    
    private SimpleDocServiceTemplate serviceTemplate;
    
    private SimpleDocServiceTemplate getServiceTemplate()
    {
        if (serviceTemplate == null)
        {
            serviceTemplate = new SimpleDocServiceTemplate("HROperatePsnEventListener");
        }
        return serviceTemplate;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public void doAction(IBusinessEvent event) throws BusinessException
    {
        // 首先判断是否启用了人员信息模块
        boolean isEnable = PubEnv.isModuleStarted(PubEnv.getPk_group(), PubEnv.MODULE_HRHI);
        if (!isEnable)
        {// 如果没有启用，则直接返回
            return;
        }
        
        BusinessEvent be = (BusinessEvent) event;
        Object obj = be.getObject();
        if (obj == null)
        {
            return;
        }
        
        if (obj instanceof HiBatchEventValueObject)
        {
            // 批量操作/目前包括离职人员转移&转档
            HiBatchEventValueObject evt = (HiBatchEventValueObject) obj;
            PsnJobVO[] jobs = evt.getPsnjobs_after();
            if (ArrayUtils.isEmpty(jobs))
            {
                return;
            }
            
            if (event.getEventType() == IHiEventType.INDOC_AFTER)
            {// 转档
            
                // 同步基本信息自定义项 fengwei 2013-01-14
                syncPsndocDefineAttrs(jobs);
                
                syncHRToUAPInDoc(jobs);
            }
            else if (event.getEventType() == IHiEventType.PARTCHGAFTER || event.getEventType() == IHiEventType.TYPE_INSERT_AFTER)
            {// 批量兼职变更//离职人员转移//部门合并
            
                // 同步基本信息自定义项 fengwei 2013-01-14
                syncPsndocDefineAttrs(jobs);
                
                syncHRToUAPBatch(jobs);
            }
            
            return;
        }
        
        HiEventValueObject valueObj = (HiEventValueObject) obj;
        PsnJobVO before = valueObj.getPsnjob_before();
        PsnJobVO after = valueObj.getPsnjob_after();
        
        String pk_psndoc = "";
        if (after != null && after.getPk_psndoc() != null)
        {
            pk_psndoc = after.getPk_psndoc();
        }
        else if (before != null && before.getPk_psndoc() != null)
        {
            pk_psndoc = before.getPk_psndoc();
        }
        
        // 当是保存时，判断该人员是否已转入人员档案，如果没有转档，则不用同步。
        String eventType = event.getEventType();
        if (eventType == IHiEventType.SAVE_EMPLOYEE_AFTER)
        {
            String pk_psnorg = after.getPk_psnorg();
            if (pk_psnorg != null)
            {
                PsnOrgVO orgVO = getServiceTemplate().queryByPk(PsnOrgVO.class, pk_psnorg);
                UFBoolean indocflag = orgVO.getIndocflag();
                if (!indocflag.booleanValue())
                {
                    return;
                }
            }
        }
        
        HashMap<String, PsnjobVO[]> map = new HashMap<String, PsnjobVO[]>();
        
        // 如果启用了人员信息模块，同步HR中的人员工作记录到UAP的任职记录
        PsndocVO psn = (PsndocVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByPk(null, PsndocVO.class, pk_psndoc);
        if (psn == null)
        {
            // 人员被删除了,那UAP下的人员工作记录也要删除
            map.put(pk_psndoc, null);
            NCLocator.getInstance().lookup(IPsndocHRPubService.class).synchronizePsnjobs(map);
            return;
        }
        
        // 得到当前员工类型最新的组织关系主键
        String pk_psnorg = NCLocator.getInstance().lookup(IPersonRecordService.class).getEmpPsnorgByPsndoc(pk_psndoc);
        if (StringUtils.isBlank(pk_psnorg))
        {
            // 只剩相关人员的组织关系记录,删除对应的UAP工作记录,只剩基本信息
            map.put(pk_psndoc, null);
            NCLocator.getInstance().lookup(IPsndocHRPubService.class).synchronizePsnjobs(map);
            return;
        }
        
        Map<String, PsnJobVO[]> psnjobMap = getAllAvailableJobs(new String[]{pk_psnorg}, new String[]{pk_psndoc});
        String[][] psnjobDefineAttrs = getPsnjobDefineAttr();
        
        /**
         * add by yanglt 2015-05-26 处理HR传递给UAP的人员工作记录数据，去掉离职记录，将离职日期放入最近一条的非离职记录的结束日期中
         * */
        PsnJobVO[] hrJobVOs = null;
        HashMap<String, String[]> mappks =
            NCLocator.getInstance().lookup(IPersonRecordService.class).getAllPsnorgByPsndoc(new String[]{pk_psndoc});
        if (MapUtils.isEmpty(mappks))
        {
            return;
        }
        Set set = mappks.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext())
        {
            String key = (String) iterator.next();
            if (StringUtils.isBlank(key))
            {
                continue;
            }
            String[] pk_psnorgs = mappks.get(key);
            if (ArrayUtils.isEmpty(pk_psnorgs))
            {
                continue;
            }
            
            List<PsnJobVO> allpsnjobs = new ArrayList<PsnJobVO>();
            for (String pkpsnorg : pk_psnorgs)
            {
                if (StringUtils.isBlank(pkpsnorg))
                {
                    continue;
                }
                hrJobVOs = MapUtils.isEmpty(psnjobMap) ? null : psnjobMap.get(pkpsnorg);
                hrJobVOs = getPsnJobs(hrJobVOs);
                if(hrJobVOs !=null && hrJobVOs.length>0){
	                for (PsnJobVO psnjobvo : hrJobVOs)
	                {
	                    allpsnjobs.add(psnjobvo);
	                }
                }
            }
            
            hrJobVOs = allpsnjobs.toArray(new PsnJobVO[allpsnjobs.size()]);
        }
        /**
         * end
         * */
        
        // 同步基本信息自定义项 fengwei 2013-01-14
        syncPsndocDefineAttrs(hrJobVOs);
        // 将得到的工作信息VO同步到UAP的任职记录VO中
        // 同时同步自定义项 fengwei 2013-01-14
        PsnjobVO[] uapJobVOs = null;
        if (!ArrayUtils.isEmpty(hrJobVOs) && psnjobDefineAttrs != null)
        {
            uapJobVOs = createSuperVOsFromSuperVOs(hrJobVOs, PsnjobVO.class, psnjobDefineAttrs[0], psnjobDefineAttrs[1], false);
        }
        map.put(pk_psndoc, uapJobVOs);
        // 调用UAP接口将HR中的人员工作记录同步到UAP的任职记录中
        NCLocator.getInstance().lookup(IPsndocHRPubService.class).synchronizePsnjobs(map);
    }
    
    // 处理每一个组织关系（同一个pk_psnorg）内的工作记录数据，将离职记录剔除，离职记录前一条的结束日期加一天
    public PsnJobVO[] getPsnJobs(PsnJobVO[] hrJobVOs) throws BusinessException
    {
        if (ArrayUtils.isEmpty(hrJobVOs))
        {
            return null;
        }
        List<PsnJobVO> listpsnjobs = new ArrayList<PsnJobVO>();
        PsnJobVO jobvo_bjdateMax = hrJobVOs[0];// 默认定义第一条为开始日期最大的数据
        PsnJobVO jobvo_end = null;// 如果有离职数据记录下来
        //int mainjob=-1;
        for (int i = 0; i < hrJobVOs.length; i++)
        {
            // 如果是离职的记录不参与比较
            boolean cond =
                hrJobVOs[i].getLastflag().booleanValue() && (hrJobVOs[i].getTrnsevent() != null && hrJobVOs[i].getTrnsevent() == 4);
            if (!cond && hrJobVOs[i].getBegindate() !=null && hrJobVOs[i].getBegindate().after(jobvo_bjdateMax.getBegindate()))
            {
                jobvo_bjdateMax = hrJobVOs[i];
            }
            
            if (!cond)
            {
//            	/**
//            	 * @author yangzxa
//            	 * 目的是确保同步到UAP的时候，人员的主职记录只有一条
//            	 */
//            	if(hrJobVOs[i].getIsmainjob().booleanValue()){
//            		if(hrJobVOs[i].getLastflag().booleanValue()&&
//            				!hrJobVOs[i].getEndflag().booleanValue()){
//            			//保证只有最新的未结束的主职被写入
//            			mainjob=i;
//            		}
//            	}else{
            		 listpsnjobs.add(hrJobVOs[i]);
            	//}
               
            }
            else
            {// 离职数据
                jobvo_end = hrJobVOs[i];
            }
        }
//        if(mainjob!=-1){
//        	listpsnjobs.add(hrJobVOs[mainjob]);
//        }
        // 如果有离职数据的时候，就需要考虑是否有返聘情况
        if (jobvo_end != null&& !listpsnjobs.isEmpty())
        {
            jobvo_bjdateMax = listpsnjobs.get(0);
            for (int j = 0; j < listpsnjobs.size(); j++)
            {
                UFBoolean endflag = listpsnjobs.get(j).getEndflag();
                // 如果是返聘的人员，排序时需要将他新入职时的工作记录剔除，不参加排序
                if (endflag.booleanValue() && listpsnjobs.get(j).getBegindate().after(jobvo_bjdateMax.getBegindate()))
                {
                    jobvo_bjdateMax = listpsnjobs.get(j);
                }
            }
        }
        
        // 这里不知道此人是否离职，但上面已经将离职记录剔除，所以此时最近的一条非离职工作记录如果有结束日期（说明此人的确是离职人员），需要给结束日期加1天，如果无结束日期（在职人员）则不用处理
        if (jobvo_end != null)
        {
            jobvo_bjdateMax.setEnddate(jobvo_bjdateMax.getEnddate() == null ? null : jobvo_bjdateMax.getEnddate().getDateAfter(1));
        }
        else
        {
            jobvo_bjdateMax.setEnddate(jobvo_bjdateMax.getEnddate() == null ? null : jobvo_bjdateMax.getEnddate());
        }
        
        if (jobvo_end != null)
        {
            // 如果有离职记录就取离职记录的人员类别，没有就直接用自己的
            jobvo_bjdateMax.setPk_psncl(jobvo_bjdateMax.getEnddate() == null ? jobvo_bjdateMax.getPk_psncl() : jobvo_end.getPk_psncl());
        }
        if(jobvo_end != null && listpsnjobs.isEmpty() && hrJobVOs.length == 1 && hrJobVOs[0].getIsmainjob().booleanValue()){
        	listpsnjobs.add(hrJobVOs[0]);//新增导入离职人员成功后人员节点查询不到(只有一条离职记录)
        }
        hrJobVOs = listpsnjobs.toArray(new PsnJobVO[listpsnjobs.size()]);
        
        return hrJobVOs;
    }
    
    /**
     * 根据人员基本信息主键pk_psndoc查找当前最新员工类型组织关系主键pk_psnorg
     * @param psndocInSql
     * @return Map<pk_psndoc, pk_psnorg>
     * @throws BusinessException
     */
    private Map<String, String> getPsnOrgPKByPsndoc(String psndocInSql) throws BusinessException
    {
        PsnOrgVO[] psnorgs = CommonUtils.retrieveByClause(PsnOrgVO.class, " pk_psndoc in (" + psndocInSql + ") order by orgrelaid desc ");
        Map<String, PsnOrgVO[]> psnorgMap = CommonUtils.group2ArrayByField(PsnOrgVO.PK_PSNDOC, psnorgs);
        if (MapUtils.isEmpty(psnorgMap)) return null;
        Map<String, String> resultMap = new HashMap<String, String>();
        for (String pk_psndoc : psnorgMap.keySet())
        {
            PsnOrgVO[] psnorgVOs = psnorgMap.get(pk_psndoc);
            for (PsnOrgVO psnorgVO : psnorgVOs)
            {
                if (psnorgVO.getPsntype() != null && psnorgVO.getPsntype() == 1) continue;
                resultMap.put(pk_psndoc, psnorgVO.getPk_psnorg());
                break;
            }
        }
        return resultMap;
    }
    
    /**
     * 批量同步uap人员
     * 
     * @param jobs
     * @throws BusinessException
     */
    private void syncHRToUAPBatch(PsnJobVO[] jobs) throws BusinessException
    {
        InSQLCreator isc = new InSQLCreator();
        String psndocInSql = isc.getInSQL(StringPiecer.getStrArrayDistinct(jobs, PsnJobVO.PK_PSNDOC));
        // 查询所有人员
        PsndocVO[] psndocVOs = CommonUtils.retrieveByClause(PsndocVO.class, " pk_psndoc in (" + psndocInSql + ") ");
        Map<String, PsndocVO> psndocMap = CommonUtils.toMap(PsndocVO.PK_PSNDOC, psndocVOs);
        // 查询工作记录信息
        Map<String, PsnJobVO[]> psnjobMap =
            getAllAvailableJobs(StringPiecer.getStrArrayDistinct(jobs, PsnJobVO.PK_PSNORG),
                StringPiecer.getStrArrayDistinct(jobs, PsnJobVO.PK_PSNDOC));
        String[][] psnjobDefineAttrs = getPsnjobDefineAttr();
        // 查询最新的组织关系
        Map<String, String> psnorgMap = getPsnOrgPKByPsndoc(psndocInSql);
        HashMap<String, PsnjobVO[]> map = new HashMap<String, PsnjobVO[]>();
        for (PsnJobVO job : jobs)
        {
            PsndocVO psn = MapUtils.isEmpty(psndocMap) ? null : psndocMap.get(job.getPk_psndoc());
            if (psn == null)
            {
                // 人员被删除了,那UAP下的人员工作记录也要删除
                map.put(job.getPk_psndoc(), null);
                continue;
            }
            
            // 得到当前员工类型最新的组织关系主键
            String pk_psnorg = MapUtils.isEmpty(psnorgMap) ? null : psnorgMap.get(job.getPk_psndoc());
            if (StringUtils.isBlank(pk_psnorg))
            {
                // 只剩相关人员的组织关系记录,删除对应的UAP工作记录,只剩基本信息
                map.put(job.getPk_psndoc(), null);
                continue;
            }
        }
        
        HashMap<String, String[]> mappks =
            NCLocator.getInstance().lookup(IPersonRecordService.class)
                .getAllPsnorgByPsndoc(StringPiecer.getStrArrayDistinct(jobs, PsnJobVO.PK_PSNDOC));
        if (MapUtils.isEmpty(mappks))
        {
            return;
        }
        Set set = mappks.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext())
        {
            String key = (String) iterator.next();
            if (StringUtils.isBlank(key))
            {
                continue;
            }
            String[] pk_psnorgs = mappks.get(key);
            if (ArrayUtils.isEmpty(pk_psnorgs))
            {
                continue;
            }
            
            List<PsnJobVO> allpsnjobs = new ArrayList<PsnJobVO>();
            for (String pk_psnorg : pk_psnorgs)
            {
                if (StringUtils.isBlank(pk_psnorg))
                {
                    continue;
                }
                PsnJobVO[] hrJobVOs = MapUtils.isEmpty(psnjobMap) ? null : psnjobMap.get(pk_psnorg);
                hrJobVOs = getPsnJobs(hrJobVOs);
                for (PsnJobVO psnjobvo : hrJobVOs)
                {
                    allpsnjobs.add(psnjobvo);
                }
            }
            
            List<PsnjobVO> listpsnjobs = new ArrayList<PsnjobVO>();
            if ((allpsnjobs != null && allpsnjobs.size() > 0) && psnjobDefineAttrs != null)
            {
                PsnjobVO[] uapJobVOs =
                    createSuperVOsFromSuperVOs(allpsnjobs.toArray(new PsnJobVO[0]), PsnjobVO.class, psnjobDefineAttrs[0],
                        psnjobDefineAttrs[1], false);
                if (!ArrayUtils.isEmpty(uapJobVOs))
                {
                    listpsnjobs = Arrays.asList(uapJobVOs);
                }
            }
            
            map.put(key, listpsnjobs.toArray(new PsnjobVO[listpsnjobs.size()]));
        }
        
        // 调用UAP接口将HR中的人员工作记录同步到UAP的任职记录中
        NCLocator.getInstance().lookup(IPsndocHRPubService.class).synchronizePsnjobs(map);
    }
    
    /**
     * 转档时同步hr人员到uap人员
     * 
     * @param jobs
     * @throws BusinessException
     */
    private void syncHRToUAPInDoc(PsnJobVO[] jobs) throws BusinessException
    {
        Map<String, PsnJobVO[]> psnjobMap =
            getAllAvailableJobs(StringPiecer.getStrArrayDistinct(jobs, PsnJobVO.PK_PSNORG),
                StringPiecer.getStrArrayDistinct(jobs, PsnJobVO.PK_PSNDOC));
        String[][] psnjobDefineAttrs = getPsnjobDefineAttr();
        HashMap<String, PsnjobVO[]> map = new HashMap<String, PsnjobVO[]>();
        HashMap<String, String[]> mappks =
            NCLocator.getInstance().lookup(IPersonRecordService.class)
                .getAllPsnorgByPsndoc(StringPiecer.getStrArrayDistinct(jobs, PsnJobVO.PK_PSNDOC));
        if (MapUtils.isEmpty(mappks))
        {
            return;
        }
        Set set = mappks.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext())
        {
            String key = (String) iterator.next();
            if (StringUtils.isBlank(key))
            {
                continue;
            }
            String[] pk_psnorgs = mappks.get(key);
            if (ArrayUtils.isEmpty(pk_psnorgs))
            {
                continue;
            }
            
            List<PsnJobVO> allpsnjobs = new ArrayList<PsnJobVO>();
            for (String pk_psnorg : pk_psnorgs)
            {
                if (StringUtils.isBlank(pk_psnorg))
                {
                    continue;
                }
                PsnJobVO[] hrJobVOs = MapUtils.isEmpty(psnjobMap) ? null : psnjobMap.get(pk_psnorg);
                hrJobVOs = getPsnJobs(hrJobVOs);
                if(hrJobVOs != null){
		            for (PsnJobVO psnjobvo : hrJobVOs)
		            {
		                allpsnjobs.add(psnjobvo);
		            }
                }
            }
            
            List<PsnjobVO> listpsnjobs = new ArrayList<PsnjobVO>();
            if ((allpsnjobs != null && allpsnjobs.size() > 0) && psnjobDefineAttrs != null)
            {
                PsnjobVO[] uapJobVOs =
                    createSuperVOsFromSuperVOs(allpsnjobs.toArray(new PsnJobVO[0]), PsnjobVO.class, psnjobDefineAttrs[0],
                        psnjobDefineAttrs[1], false);
                if (!ArrayUtils.isEmpty(uapJobVOs))
                {
                    listpsnjobs = Arrays.asList(uapJobVOs);
                }
            }
            
            map.put(key, listpsnjobs.toArray(new PsnjobVO[listpsnjobs.size()]));
        }
        
        NCLocator.getInstance().lookup(IPsndocHRPubService.class).synchronizePsnjobs(map);
    }
    
    /**
     * 根据人员信息主键，查出该人的全部工作信息。 同一部门下只有一条任职记录，如果有主职，则取主职信息；如果没有主职，只有兼职，则取兼职的任职ID最小的记录。 edit by heqiaoa 2014-12-07
     * FOR: 同部门的人员兼职到同部门的岗位，业务审批流无法找到兼职岗位的审批人<br>
     * 产生原因：如果给一个增加一个同部门的兼职，改监听无法将该兼职记录同步到bd_psnjob，而不同部门的兼职则可以同步
     * @param pk_psnorgs
     * @return Map<pk_psnorg, psnJobVO[]>
     * @throws BusinessException
     */
    private Map<String, PsnJobVO[]> getAllAvailableJobs(String[] pk_psnorgs, String[] pk_psndocs) throws BusinessException
    {
        if (ArrayUtils.isEmpty(pk_psnorgs)) return null;
        // 主职&兼职
        PsnJobVO[] jobVOs = null;
        if (ArrayUtils.isEmpty(pk_psndocs))
        {
            jobVOs =
                NCLocator.getInstance().lookup(IPsndocQryService.class)
                    .queryAvailablePsnjobByCondition("pk_psnorg in (" + new InSQLCreator().getInSQL(pk_psnorgs) + ") ", "begindate");// 为了排序，将assgid替换为begindate
        }
        else
        {
            jobVOs =
                NCLocator.getInstance().lookup(IPsndocQryService.class)
                    .queryAvailablePsnjobByCondition("pk_psndoc in (" + new InSQLCreator().getInSQL(pk_psndocs) + ") ", "begindate");// 为了排序，将assgid替换为begindate
        }
        
        if (ArrayUtils.isEmpty(jobVOs)) return null;
        Map<String, PsnJobVO[]> jobMap = CommonUtils.group2ArrayByField(PsnJobVO.PK_PSNORG, jobVOs);
        Map<String, PsnJobVO[]> resultMap = new HashMap<String, PsnJobVO[]>();
        for (String key : jobMap.keySet())
        {
            PsnJobVO[] psnjobVOs = jobMap.get(key);
            List<PsnJobVO> hrJobList = new ArrayList<PsnJobVO>();
            // 不进行部门主键的过滤
            // Set<String> deptSet = new HashSet<String>();
            for (PsnJobVO job : psnjobVOs)
            {
                if (job == null)
                {
                    // 已结束的兼职也同步uap人员
                    continue;
                }
                // String pk_dept = job.getPk_dept();
                // 不进行部门主键的过滤
                // if (deptSet.add(pk_dept)) {
                hrJobList.add(job);
                // }
            }
            if (CollectionUtils.isEmpty(hrJobList)) continue;
            resultMap.put(key, hrJobList.toArray(new PsnJobVO[0]));
        }
        return resultMap;
    }
    
    /**
     * 同步基本信息自定义项
     * <ol>
     * <li>得到需要同步的基本信息自定义项</li>
     * <li>根据HR工作记录得到HR基本信息VO</li>
     * <li>根据基本信息自定义项和HR基本信息得到UAP基本信息</li>
     * </ol>
     * 
     * @param psndoc
     * @throws BusinessException
     */
    private void syncPsndocDefineAttrs(PsnJobVO[] hrJobVOs) throws BusinessException
    {
        DefineMap[] defineMaps = CommonUtils.retrieveByClause(DefineMap.class, DefineMap.UAP_TABLENAME + " = 'bd_psndoc'");
        if (ArrayUtils.isEmpty(defineMaps)) return;
        List<nc.vo.bd.psn.PsndocVO> uapPsndocList = new ArrayList<nc.vo.bd.psn.PsndocVO>();
        String[] srcExtraAttrs = new String[defineMaps.length];
        String[] desExtraAttrs = new String[defineMaps.length];
        for (int i = 0; i < defineMaps.length; i++)
        {
            srcExtraAttrs[i] = defineMaps[i].getHr_fieldname();
            desExtraAttrs[i] = defineMaps[i].getPropindex();
        }
        
        InSQLCreator isc = new InSQLCreator();
        String psndocInSql = isc.getInSQL(StringPiecer.getStrArrayDistinct(hrJobVOs, PsnJobVO.PK_PSNDOC));
        // 查询所有人员
        PsndocVO[] psndocVOs = CommonUtils.retrieveByClause(PsndocVO.class, " pk_psndoc in (" + psndocInSql + ") ");
        Map<String, PsndocVO> psndocMap = CommonUtils.toMap(PsndocVO.PK_PSNDOC, psndocVOs);
        List<String> pk_psndocList = new ArrayList<String>();
        for (PsnJobVO hrJobVO : hrJobVOs)
        {
            String pk_psndoc = hrJobVO.getPk_psndoc();
            if (pk_psndocList.contains(pk_psndoc))
            {
                continue;
            }
            
            PsndocVO psndocVO = MapUtils.isEmpty(psndocMap) ? null : psndocMap.get(pk_psndoc);
            nc.vo.bd.psn.PsndocVO uapPsnVO = new nc.vo.bd.psn.PsndocVO();
            copySuperVOAttributes(new PsndocVO[]{psndocVO}, new nc.vo.bd.psn.PsndocVO[]{uapPsnVO}, srcExtraAttrs, desExtraAttrs, false);
            uapPsndocList.add(uapPsnVO);
            pk_psndocList.add(pk_psndoc);
        }
        getBaseDAO().updateVOArray(uapPsndocList.toArray(new nc.vo.bd.psn.PsndocVO[uapPsndocList.size()]));
    }
    
    /**
     * 得到需要同步的工作信息的自定义项
     * @return
     * @throws BusinessException
     */
    private String[][] getPsnjobDefineAttr() throws BusinessException
    {
        // 得到需要同步的工作信息的自定义项
        DefineMap[] definemaps = CommonUtils.retrieveByClause(DefineMap.class, DefineMap.UAP_TABLENAME + " = 'bd_psnjob'");
        int len = ArrayUtils.getLength(definemaps);
        String[][] result = new String[2][];
        String[] srcExtraAttrs = new String[len + 3];
        String[] desExtraAttrs = new String[len + 3];
        srcExtraAttrs[0] = "clerkcode";
        srcExtraAttrs[1] = "begindate";
        srcExtraAttrs[2] = "enddate";// HR的结束日期
        desExtraAttrs[0] = "psncode";
        desExtraAttrs[1] = "indutydate";
        desExtraAttrs[2] = "enddutydate";// UAP的任职结束日期
        if (len > 0)
        {
            for (int i = 0; i < len; i++)
            {
                srcExtraAttrs[i + 3] = definemaps[i].getHr_fieldname();
                desExtraAttrs[i + 3] = definemaps[i].getPropindex();
            }
        }
        result[0] = srcExtraAttrs;
        result[1] = desExtraAttrs;
        return result;
    }
    
    public static void copySuperVOAttributes(SuperVO[] source, SuperVO[] destiny, String[] srcExtraAttrs, String[] desExtraAttrs,
            boolean extraOnly)
    {
        if (!extraOnly)
        {
            SuperVOHelper.copySuperVOAttributes(source, destiny);
        }
        
        for (int i = 0; i < source.length; i++)
        {
            for (int j = 0; j < srcExtraAttrs.length; j++)
            {
                String value =
                    source[i].getAttributeValue(srcExtraAttrs[j]) == null ? null : source[i].getAttributeValue(srcExtraAttrs[j]).toString();
                destiny[i].setAttributeValue(desExtraAttrs[j], value);
            }
        }
    }
    
    public static <T extends SuperVO> T[] createSuperVOsFromSuperVOs(SuperVO[] source, Class<T> clazz, String[] srcExtraAttrs,
            String[] desExtraAttrs, boolean extraOnly)
    {
        T[] instances = SuperVOHelper.initArray(clazz, source.length);
        
        for (int i = 0; i < source.length; i++)
        {
            try
            {
                instances[i] = clazz.newInstance();
                
                if (!extraOnly)
                {
                    SuperVOHelper.copySuperVOAttributes(source[i], instances[i]);
                }
                
                for (int j = 0; j < srcExtraAttrs.length; j++)
                {
                    if (j < 2)
                    {
                        instances[i].setAttributeValue(desExtraAttrs[j], source[i].getAttributeValue(srcExtraAttrs[j]));
                    }
                    else
                    {
                        String value =
                            source[i].getAttributeValue(srcExtraAttrs[j]) == null ? null : source[i].getAttributeValue(srcExtraAttrs[j])
                                .toString();
                        instances[i].setAttributeValue(desExtraAttrs[j], value);
                    }
                }
            }
            catch (Exception e)
            {
                throw new BusinessRuntimeException(ResHelper.getString("6001pub", "06001pub0054", clazz.getName())
                /* @res "创建 {0} 实例时出错！" */);
            }
        }
        
        return instances;
    }
    
    private BaseDAO baseDAO;
    
    public BaseDAO getBaseDAO()
    {
        if (baseDAO == null)
        {
            baseDAO = new BaseDAO();
        }
        return baseDAO;
    }
}
