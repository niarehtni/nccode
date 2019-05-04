package nc.vo.hi.pub;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.md.model.IBusinessEntity;
import nc.md.model.IComponent;
import nc.md.model.impl.Attribute;
import nc.md.model.type.IType;
import nc.vo.hi.psndoc.AssVO;
import nc.vo.hi.psndoc.BminfoVO;
import nc.vo.hi.psndoc.CapaVO;
import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.KeyPsnVO;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnChgVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.TitleVO;
import nc.vo.hi.psndoc.WainfoVO;
import nc.vo.hr.dataio.DefaultHookPublic;

public class PsnInfoDataIOHookPublic extends DefaultHookPublic
{
    
    @Override
    public void init(String strFuncCode)
    {
        super.init(strFuncCode);
        
        IComponent component = getComponent(strFuncCode);
        
        List<IBusinessEntity> listBean = component.getBusinessEntities();
        
        for (int i = 0; i < listBean.size(); i++)
        {
            // 组织关系
            if (listBean.get(i).getFullClassName().equalsIgnoreCase(PsnOrgVO.class.getName()))
            {
                // addDefaultImportBean(listBean.get(i).getDisplayName());
                addExcludeColumn(PsnOrgVO.class.getName(), new String[]{
                    PsnOrgVO.PK_PSNDOC,
                    PsnOrgVO.LASTFLAG,
                    PsnOrgVO.ORGRELAID,
                    //王永文 20180426 模板中增加人员类型 begin
                    /* PsnOrgVO.PSNTYPE, PsnOrgVO.ENDFLAG */});
                	//王永文 20180426 模板中增加人员类型 end
                addDefaultUnionKeies(PsnOrgVO.class.getName(), new String[]{PsnOrgVO.BEGINDATE});
            }
            // 工作记录
            else if (listBean.get(i).getFullClassName().equalsIgnoreCase(PsnJobVO.class.getName()))
            {
                addDefaultImportBean(listBean.get(i).getDisplayName());
                addExcludeColumn(PsnJobVO.class.getName(), new String[]{
                    PsnJobVO.PK_PSNDOC,
                    PsnJobVO.PK_PSNORG,
                    PsnJobVO.LASTFLAG,
                    PsnJobVO.RECORDNUM,
                    PsnJobVO.ENDFLAG,
                  //王永文 20180426 模板中增加人员类型 begin
//                    PsnJobVO.PSNTYPE,
                  //王永文 20180426 模板中增加人员类型 end
                    PsnJobVO.ISMAINJOB,
                    PsnJobVO.ORIBILLTYPE,
                    PsnJobVO.ORIBILLPK,
                    PsnJobVO.DATAORIGINFLAG,
                    PsnJobVO.ASSGID,
                    PsnJobVO.PK_HRORG,
                    PsnJobVO.TRIAL_FLAG,
                    PsnJobVO.TRIAL_TYPE});
                addNotNullColumn(PsnJobVO.class.getName(), new String[]{
                    PsnJobVO.BEGINDATE,
                    PsnJobVO.PK_DEPT,
                    PsnJobVO.PSNTYPE,
                    PsnJobVO.TRNSEVENT});
                
            }
            // 证件信息
            else if (listBean.get(i).getFullClassName().equalsIgnoreCase(CertVO.class.getName()))
            {
                // addDefaultImportBean(listBean.get(i).getDisplayName());
                addNotNullColumn(CertVO.class.getName(), new String[]{CertVO.ISEFFECT, CertVO.ISSTART});
                addDefaultUnionKeies(CertVO.class.getName(), new String[]{CertVO.IDTYPE, CertVO.ID});
            }
            // 兼职记录
            else if (listBean.get(i).getFullClassName().equalsIgnoreCase(PartTimeVO.class.getName()))
            {
                addNotNullColumn(PartTimeVO.class.getName(), new String[]{PartTimeVO.CLERKCODE});
                addDefaultUnionKeies(PartTimeVO.class.getName(), new String[]{PartTimeVO.BEGINDATE, PartTimeVO.CLERKCODE});
                addExcludeColumn(PartTimeVO.class.getName(), new String[]{
                    PartTimeVO.ISMAINJOB,
                    PartTimeVO.ORIBILLTYPE,
                    PartTimeVO.ORIBILLPK,
                    PartTimeVO.DATAORIGINFLAG,
                    PartTimeVO.TRIAL_FLAG,
                    PartTimeVO.TRIAL_TYPE,
                    PartTimeVO.PSNTYPE,
                    PartTimeVO.PK_HRORG,
                    PartTimeVO.PK_HRGROUP});
            }
            // 流动情况
            else if (listBean.get(i).getFullClassName().equalsIgnoreCase(PsnChgVO.class.getName()))
            {
                // addDefaultImportBean(listBean.get(i).getDisplayName());
                addDefaultUnionKeies(PsnChgVO.class.getName(), new String[]{PsnChgVO.BEGINDATE});
            }
            // 合同信息
            else if (listBean.get(i).getFullClassName().equalsIgnoreCase(CtrtVO.class.getName()))
            {
                addNotNullColumn(CtrtVO.class.getName(), new String[]{CtrtVO.IFWRITE});
                addExcludeColumn(CtrtVO.class.getName(), new String[]{CtrtVO.CONTID, CtrtVO.IFWRITE, CtrtVO.PK_UNCHREASON});
                
            }
            else if (listBean.get(i).getFullClassName().equalsIgnoreCase(TitleVO.class.getName()))
            {
                addNotNullColumn(TitleVO.class.getName(), new String[]{TitleVO.TIPTOP_FLAG});
            }
            else if (listBean.get(i).getFullClassName().equalsIgnoreCase(AssVO.class.getName()))
            {
                addExcludeColumn(listBean.get(i).getFullClassName(), new String[]{AssVO.PK_ORG});
            }
            // 其它业务子集
            else if (PsndocAggVO.hashBusinessInfoSet.contains(listBean.get(i).getTable().getName()))
            {
                addExcludeColumn(listBean.get(i).getFullClassName(), new String[]{
                    PsnJobVO.PK_PSNDOC,
                    PsnJobVO.PK_PSNORG,
                    PsnJobVO.LASTFLAG,
                    PsnJobVO.RECORDNUM});
            }
            // 非业务子集
            else
            {
                addExcludeColumn(listBean.get(i).getFullClassName(),
                    new String[]{PsnJobVO.PK_PSNDOC, PsnJobVO.LASTFLAG, PsnJobVO.RECORDNUM});
            }
            // 人员信息
            if (listBean.get(i).getFullClassName().equalsIgnoreCase(PsndocVO.class.getName()))
            {
                addDefaultImportBean(listBean.get(i).getDisplayName());
                addExcludeColumn(listBean.get(i).getFullClassName(), new String[]{
                    PsndocVO.ISSHOPASSIST,
                    PsndocVO.ENABLESTATE,
                    PsndocVO.DATAORIGINFLAG,
                    PsndocVO.ISHISKEYPSN,
                    PsndocVO.ISHISLEADER,
                    PsndocVO.ISCADRE,
                    PsndocVO.EDU,
                    PsndocVO.PK_DEGREE,
                    PsndocVO.TITLETECHPOST,
                    PsndocVO.PROF});
                addDefaultUnionKeies(PsndocVO.class.getName(), new String[]{PsndocVO.CODE});
                
                try
                {
                    List<Attribute> attrList = new ArrayList<Attribute>();
                    Attribute attr = (Attribute) listBean.get(i).getAttributeByName(PsndocVO.ADDR);
                    Attribute attr1;
                    
                    attr1 = (Attribute) BeanUtils.cloneBean(attr);
                    attr1.setDataTypeStyle(IType.STYLE_REF);
                    attr1.setName("addr.country");
                    attr1.setDisplayName(ResHelper.getString("6007psn", "06007psn0513")/*
                                                                                        * @res "家庭地址（国家）"
                                                                                        */);
                    attr1.setRefModelName("国家地区");
                    attrList.add(attr1);
                    
                    Attribute attr2 = (Attribute) BeanUtils.cloneBean(attr);
                    attr2.setDataTypeStyle(IType.STYLE_REF);
                    attr2.setName("addr.province");
                    attr2.setDisplayName(ResHelper.getString("6007psn", "06007psn0514")/*
                                                                                        * @res "家庭地址（省份）"
                                                                                        */);
                    attr2.setRefModelName("行政区划");
                    attrList.add(attr2);
                    
                    Attribute attr3 = (Attribute) BeanUtils.cloneBean(attr);
                    attr3.setDataTypeStyle(IType.STYLE_REF);
                    attr3.setName("addr.city");
                    attr3.setDisplayName(ResHelper.getString("6007psn", "06007psn0515")/*
                                                                                        * @res "家庭地址（城市）"
                                                                                        */);
                    attr3.setRefModelName("行政区划");
                    attrList.add(attr3);
                    
                    Attribute attr4 = (Attribute) BeanUtils.cloneBean(attr);
                    attr4.setDataTypeStyle(IType.STYLE_REF);
                    attr4.setName("addr.county");
                    attr4.setDisplayName(ResHelper.getString("6007psn", "06007psn0516")/*
                                                                                        * @res "家庭地址（地区）"
                                                                                        */);
                    attr4.setRefModelName("行政区划");
                    attrList.add(attr4);
                    
                    Attribute attr5 = (Attribute) BeanUtils.cloneBean(attr);
                    attr5.setDataTypeStyle(IType.STYLE_SINGLE);
                    attr5.setName("addr.detail");
                    attr5.setDisplayName(ResHelper.getString("6007psn", "06007psn0517")/*
                                                                                        * @res "家庭地址（详细地址）"
                                                                                        */);
                    attrList.add(attr5);
                    
                    Attribute attr6 = (Attribute) BeanUtils.cloneBean(attr);
                    attr6.setDataTypeStyle(IType.STYLE_SINGLE);
                    attr6.setName("addr.postcode");
                    attr6.setDisplayName(ResHelper.getString("6007psn", "06007psn0518")/*
                                                                                        * @res "家庭地址（邮政编码）"
                                                                                        */);
                    attrList.add(attr6);
                    addSpecialAttrRule(PsndocVO.ADDR, attrList);
                }
                catch (Exception e)
                {
                    Logger.error(e.getMessage());
                }
                
            }
            // 模板过滤掉创建时间，创建人，修改时间，修改人HR人员、
            // 行政组织版本(所有)、HR部门版本(所有)、HR组织关系、人员工作记录、人力资源组织(所有)字段
            addExcludeColumn(listBean.get(i).getFullClassName(), new String[]{
                PsndocVO.CREATOR,
                PsndocVO.CREATIONTIME,
                PsndocVO.MODIFIER,
                PsndocVO.MODIFIEDTIME,
                PsndocVO.PK_PSNDOC,
                PsnJobVO.PK_ORG_V,
                PsnJobVO.PK_DEPT_V,
                PsnOrgVO.PK_PSNORG,
                PsnJobVO.PK_PSNJOB,
            /* TrainVO.PK_ORG */});
        }
        addBeanShowOrder("bd_psndoc", "hi_psnorg", "hi_psnjob", "hi_psndoc_parttime", "hi_psndoc_trial", "hi_psndoc_psnchg",
            "hi_psndoc_work", "hi_psndoc_ctrt", "hi_psndoc_ass", "hi_psndoc_edu", "hi_psndoc_cert", "hi_psndoc_langability",
            "hi_psndoc_train", "hi_psndoc_capa", "hi_psndoc_family", "hi_psndoc_linkman", "hi_psndoc_title", "hi_psndoc_nationduty",
            "hi_psndoc_qulify", "hi_psndoc_speitem", "hi_psndoc_partylog", "hi_psndoc_abroad", "hi_psndoc_enc", "hi_psndoc_pun",
            "hi_psndoc_retire", "hi_psndoc_wainfo", "hi_psndoc_bminfo", "hi_psndoc_keypsn");
        
        addDefaultRefRule(PsndocVO.getDefaultTableName(), PsndocVO.PK_ORG, PsndocVO.PK_GROUP);
        addDefaultRefRule(PsndocVO.getDefaultTableName(), PsndocVO.PK_HRORG, PsndocVO.PK_GROUP);
        addDefaultRefRule(PsndocVO.getDefaultTableName(), PsndocVO.NATIVEPLACE, PsndocVO.COUNTRY);
        addDefaultRefRule(PsndocVO.getDefaultTableName(), PsndocVO.PERMANRESIDE, PsndocVO.COUNTRY);
        
        addDefaultRefRule(CertVO.getDefaultTableName(), CertVO.PK_ORG, CertVO.PK_GROUP);
        
        // 主职依赖关系
        addDefaultRefRule(PsnJobVO.getDefaultTableName(), PsnJobVO.PK_ORG, PsnJobVO.PK_GROUP);
        addDefaultRefRule(PsnJobVO.getDefaultTableName(), PsnJobVO.PK_DEPT, PsnJobVO.PK_ORG);
        addDefaultRefRule(PsnJobVO.getDefaultTableName(), PsnJobVO.PK_POSTSERIES, PsnJobVO.PK_DEPT);
        addDefaultRefRule(PsnJobVO.getDefaultTableName(), PsnJobVO.PK_POST, PsnJobVO.PK_POSTSERIES);
        addDefaultRefRule(PsnJobVO.getDefaultTableName(), PsnJobVO.PK_JOB, PsnJobVO.SERIES);
        addDefaultRefRule(PsnJobVO.getDefaultTableName(), PsnJobVO.PK_JOBGRADE, PsnJobVO.PK_POST);
        addDefaultRefRule(PsnJobVO.getDefaultTableName(), PsnJobVO.TRNSTYPE, PsnJobVO.TRNSEVENT);
        addDefaultRefRule(PsnJobVO.getDefaultTableName(), PsnJobVO.PK_JOBRANK, PsnJobVO.PK_JOBGRADE);
        
        // 兼职依赖关系
        addDefaultRefRule(PartTimeVO.getDefaultTableName(), PsnJobVO.PK_ORG, PsnJobVO.PK_GROUP);
        addDefaultRefRule(PartTimeVO.getDefaultTableName(), PsnJobVO.PK_DEPT, PsnJobVO.PK_ORG);
        addDefaultRefRule(PartTimeVO.getDefaultTableName(), PsnJobVO.PK_POSTSERIES, PsnJobVO.PK_DEPT);
        addDefaultRefRule(PartTimeVO.getDefaultTableName(), PsnJobVO.PK_POST, PsnJobVO.PK_POSTSERIES);
        addDefaultRefRule(PartTimeVO.getDefaultTableName(), PsnJobVO.PK_JOB, PsnJobVO.SERIES);
        addDefaultRefRule(PartTimeVO.getDefaultTableName(), PsnJobVO.PK_JOBGRADE, PsnJobVO.PK_POST);
        addDefaultRefRule(PartTimeVO.getDefaultTableName(), PsnJobVO.TRNSTYPE, PsnJobVO.TRNSEVENT);
        addDefaultRefRule(PartTimeVO.getDefaultTableName(), PsnJobVO.PK_JOBRANK, PsnJobVO.PK_JOBGRADE);
        
        addDefaultRefRule(PsnOrgVO.getDefaultTableName(), PsnOrgVO.PK_HRORG, PsnOrgVO.PK_GROUP);
        addDefaultRefRule(PsnOrgVO.getDefaultTableName(), PsnOrgVO.PK_ORG, PsnOrgVO.PK_GROUP);
        
        addDefaultRefRule(CtrtVO.getDefaultTableName(), CtrtVO.PK_ORG, CtrtVO.PK_GROUP);
        
        addDefaultRefRule(CapaVO.getDefaultTableName(), CapaVO.PK_PE_SCOGRDITEM, CapaVO.PK_PE_INDI);
        
        // 过滤不需要导出的子集，薪资，社保,员工考核信息,干部相关子集，协议信息子集
        /**
         * modify start ：人员信息导出模板中去掉协议信息子集********2014-6-3 yanglt
         * ps:人员信息可以查看人员的协议信息,关键人员历史，但人事导入工具不应支持协议信息的导入。 请在导出模板中去掉协议信息子集。 nc.vo.hi.psndoc.AgreementVO
         * */
        setExcludeBean(WainfoVO.class.getName(), BminfoVO.class.getName(), KeyPsnVO.class.getName(), "nc.vo.hi.psndoc.AgreementVO",
            "nc.vo.mm.ldpsninfo.LdApptInfoVO", "nc.vo.mm.appremdec.HIAppremDecisionVO", "nc.vo.mm.ldpsninfo.LdClassHisInfoVO");
        
        addDefaultUnionKeies(PsnJobVO.class.getName(), new String[]{PsnJobVO.BEGINDATE, PsnJobVO.CLERKCODE});
        addExportRefColumn("部门HR", "org_orgs.code as org_code", "org_orgs.name as org_name");
        addExportRefColumn("nc.ui.om.ref.PostRefModel2", "org_orgs.code as org_code", "org_orgs.name as org_name",
            "org_dept.code as dept_code", "org_dept.name as dept_name", "OM_POSTSERIES.POSTSERIESCODE", "OM_POSTSERIES.POSTSERIESNAME");
        addExportRefColumn("职级(设置职务主键)", "om_joblevelsys.code as syscode", "om_joblevelsys.name as sysname");
        addExportRefColumn("nc.ui.om.ref.JobTypeJobRefModel4", "OM_JOBTYPE.JOBTYPECODE", "OM_JOBTYPE.JOBTYPENAME");
        
    }
}
