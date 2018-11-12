package nc.vo.hr.tools.rtf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.rm.innerjobapply.RmDeptRefModel;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.hr.infoset.IInfoSetQry;
import nc.itf.uap.IUAPQueryBS;
import nc.md.MDBaseQueryFacade;
import nc.md.common.AssociationKind;
import nc.md.model.IAttribute;
import nc.md.model.IBean;
import nc.md.model.ICardinality;
import nc.md.model.MetaDataException;
import nc.md.model.type.IType;
import nc.md.persist.designer.vo.BizItfMapVO;
import nc.md.persist.designer.vo.ColumnVO;
import nc.md.persist.designer.vo.PropertyVO;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.hr.tools.rtf.jacob.DHyperlink;
import nc.ui.hr.tools.rtf.jacob.WordUtil;
import nc.vo.bd.ref.RefInfoVO;
import nc.vo.hi.pub.FldreftypeVO;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.uif2.LoginContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import nc.bs.ml.NCLangResOnserver;

/**
 * 解析参照信息内容
 * @author sunxj
 * @version 2009-12-29
 */

public class RefDataTypeHelper
{
    private static RefSetFieldParameter convertBean(IBean bean)
    {
        RefSetFieldParameter para = new RefSetFieldParameter();
        String tableName = bean.getTable().getName();
        para.setTableName(tableName);
        String classID = bean.getID();
        IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
        Collection<BizItfMapVO> mapVOCol = null;
        try
        {
            mapVOCol =
                iUAPQueryBS.retrieveByClause(BizItfMapVO.class, "classid='" + classID
                    + "' and bizinterfaceid='6c8722b9-911a-489b-8d0d-18bd3734fcf6' and intattrid='89578a97-42fe-439b-827c-8eabd9e3604c'");
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
            throw new BusinessRuntimeException(e.getMessage(), e);
        }
        if (mapVOCol == null || mapVOCol.size() == 0)
        {
            throw new BusinessRuntimeException(StringUtils.replace(ResHelper.getString("6001rtf", "06001rtf0151")
            /* @res "元数据{0}必须实现IBDObject接口!" */, "{0}", bean.getName()));
        }
        
        if (mapVOCol.toArray(new BizItfMapVO[0])[0].getClassAttrPath() != null
            && mapVOCol.toArray(new BizItfMapVO[0])[0].getClassAttrPath().indexOf(".") >= 0)
        {
            // 处理名称多级关联，通过“.”来判断
            String nameAttID = mapVOCol.toArray(new BizItfMapVO[0])[0].getClassAttrID();
            PropertyVO propVO = null;
            try
            {
                propVO = (PropertyVO) iUAPQueryBS.retrieveByPK(PropertyVO.class, nameAttID);
            }
            catch (BusinessException e)
            {
                Logger.error(e.getMessage(), e);
                throw new BusinessRuntimeException(e.getMessage(), e);
            }
            try
            {
                IBean bn = MDBaseQueryFacade.getInstance().getBeanByID(propVO.getDataType());
                RefSetFieldParameter rfp = convertBean(bn);
                String colID = bean.getORMaps().get(propVO.getId());
                ColumnVO colVO = null;
                try
                {
                    colVO = (ColumnVO) iUAPQueryBS.retrieveByPK(ColumnVO.class, colID);
                }
                catch (BusinessException e)
                {
                    Logger.error(e.getMessage(), e);
                    throw new BusinessRuntimeException(e.getMessage(), e);
                }
                rfp.setTableName(" ( select  " + SQLHelper.getMultiLangNameColumn(rfp.getTableName() + "." + rfp.getDisplayFieldName())
                    + " as " + rfp.getDisplayFieldName() + "  ," + tableName + "." + bean.getPrimaryKey().getPKColumn().getName() + " as "
                    + rfp.getPkFieldName() + " from " + tableName + " left outer join " + rfp.getTableName() + " on " + tableName + "."
                    + colVO.getName() + " = " + rfp.getTableName() + "." + rfp.getPkFieldName() + " ) ");
                rfp.setMutilangText(false);
                return rfp;
            }
            catch (MetaDataException e)
            {
                Logger.error(e.getMessage(), e);
                throw new BusinessRuntimeException(e.getMessage(), e);
            }
        }
        else
        {
            String nameAttID = mapVOCol.toArray(new BizItfMapVO[0])[0].getClassAttrID();
            PropertyVO propVO = null;
            try
            {
                propVO = (PropertyVO) iUAPQueryBS.retrieveByPK(PropertyVO.class, nameAttID);
            }
            catch (BusinessException e)
            {
                Logger.error(e.getMessage(), e);
                throw new BusinessRuntimeException(e.getMessage(), e);
            }
            // 如果是多语字段
            if (propVO.getDataType().equals("BS000010000100001058"))
            {
                para.setMutilangText(true);
            }
            String colID = bean.getORMaps().get(propVO.getId());
            
            ColumnVO colVO = null;
            try
            {
                colVO = (ColumnVO) iUAPQueryBS.retrieveByPK(ColumnVO.class, colID);
            }
            catch (BusinessException e)
            {
                Logger.error(e.getMessage(), e);
                throw new BusinessRuntimeException(e.getMessage(), e);
            }
            para.setDisplayFieldName(colVO.getName());
            para.setPkFieldName(bean.getPrimaryKey().getPKColumn().getName());
            return para;
        }
    }
    
    private static RefSetFieldParameter convertRefModel(AbstractRefModel refModel)
    {
        
        RefSetFieldParameter para = new RefSetFieldParameter();
        
        String tableName = refModel.getTableName();
        if (StringUtils.isBlank(tableName))
        {
            throw new BusinessRuntimeException(StringUtils.replace(ResHelper.getString("6001rtf", "06001rtf0152")
            /* @res "{0}的tablename为空!" */, "{0}", refModel.getClass().getName()));
        }
        if (tableName.indexOf(",") > -1)
        {
            // nc.ui.hi.ref.DutyRef
            if (!StringUtils.isBlank(refModel.getWherePart()))
            {
                tableName =
                    "(select " + refModel.getPkFieldCode() + "," + refModel.getRefNameField() + " from " + tableName + " where "
                        + refModel.getWherePart() + ")";
            }
            else
            {
                tableName = "(select " + refModel.getPkFieldCode() + "," + refModel.getRefNameField() + " from " + tableName + ")";
            }
            
        }
        else
        {
            // nc.ui.hi.hi_301.ref.JobRef
            Pattern pattern = Pattern.compile("(left|right|full|inner)\\s+(outer)?\\s+join");
            Matcher matcher = pattern.matcher(tableName.toLowerCase());
            if (matcher.find())
            {
                tableName = "(select " + refModel.getPkFieldCode() + "," + refModel.getRefNameField() + " from " + tableName + ")";
            }
        }
        para.setTableName(tableName);
        
        String PkFieldCode = refModel.getPkFieldCode();
        PkFieldCode = PkFieldCode.contains(".") ? PkFieldCode.substring(PkFieldCode.indexOf(".") + 1) : PkFieldCode;
        para.setPkFieldName(PkFieldCode);
        
        para.setMutilangText(refModel.isMutilLangNameRef());
        
        String RefNameField = refModel.getRefNameField();
        RefNameField = RefNameField.contains(".") ? RefNameField.substring(RefNameField.indexOf(".") + 1) : RefNameField;
        para.setDisplayFieldName(RefNameField);
        
        return para;
    }
    
    /**
     * 得到所有表的主键
     * @author fengwei on 2010-6-3
     * @param bean 主表bean
     * @param listBean 子表bean
     * @return
     */
    public static HashMap<String, String> getAllPrimarykey(IBean bean, List<IBean> listBean)
    {
        HashMap<String, String> primarykeyMap = new HashMap<String, String>();
        primarykeyMap.put(bean.getTable().getName(), bean.getPrimaryKey().getPKColumn().getName());
        if (CollectionUtils.isEmpty(listBean))
        {
            return primarykeyMap;
        }
        for (IBean subBean : listBean)
        {
            String tableName = subBean.getTable().getName();
            if (ResHelper.getString("6001rtf", "06001rtf0153")
            /* @res "兼职记录" */.equals(subBean.getDisplayName()))
            {
                tableName = "hi_psndoc_parttime";
            }
            String primarykey = subBean.getPrimaryKey().getPKColumn().getName();
            primarykeyMap.put(tableName, primarykey);
        }
        return primarykeyMap;
    }
    
    public static void getAllSubTables(List<IBean> listBean)
    {
        List<String> tableName = new ArrayList<String>();
        
        for (IBean childBean : listBean)
        {
            List<IAttribute> attributes = childBean.getAttributes();
            for (IAttribute attribute : attributes)
            {
                String subColumnName = attribute.getName();
                tableName.add(subColumnName);
            }
        }
    }
    
    /**
     * 根据beanID得到Bean
     * @author fengwei on 2010-5-31
     * @param vo
     * @return
     * @throws BusinessException
     */
    public static IBean getBeanByBeanID(String metaDataId) throws BusinessException
    {
        IBean bean = null;
        
        try
        {
            bean = MDBaseQueryFacade.getInstance().getBeanByID(metaDataId);
        }
        catch (MetaDataException e)
        {
            Logger.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage(), e);
        }
        
        if (bean == null)
        {
            throw new BusinessException(/* "没有找到id为"+IMetaDataIDConst.JOB+"的bean!" */);
        }
        return bean;
    }
    
    public static IBean getBeanByName(String nameSpace, String beanName) throws BusinessException
    {
        IBean bean = null;
        
        try
        {
            bean = MDBaseQueryFacade.getInstance().getBeanByName(nameSpace, beanName);
        }
        catch (MetaDataException e)
        {
            Logger.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage(), e);
        }
        
        if (bean == null)
        {
            throw new BusinessException(/* "没有找到id为"+IMetaDataIDConst.JOB+"的bean!" */);
        }
        return bean;
    }
    
    public static Map<String, FldreftypeVO> getComboboxDataTypeMap(InfoSetVO[] vos) throws BusinessException
    {
        Map<String, FldreftypeVO> fldMap = new HashMap<String, FldreftypeVO>();
        for (InfoSetVO vo : vos)
        {
            String meta_data_id = vo.getMeta_data_id();
            if (meta_data_id == null)
            {
                continue;
            }
            IBean bean = getBeanByBeanID(meta_data_id);
            for (int i = 0; i < vo.getInfo_item().length; i++)
            {
                int datatype = vo.getInfo_item()[i].getData_type();
                if (6 == datatype)
                {
                    FldreftypeVO refVO = new FldreftypeVO();
                    refVO.setFldrefname(vo.getTable_code());
                    refVO.setFldrefcode(vo.getTable_code() + "." + vo.getInfo_item()[i].getItem_code());
                    IType dataType = getInfoItemDataType(vo.getInfo_item()[i], bean);
                    refVO.setDataType(dataType);
                    fldMap.put(refVO.getFldrefcode(), refVO);
                }
            }
        }
        return fldMap;
    }
    
    /**
     * 得到参照和下拉列表类型的所有Map
     * @author fengwei on 2010-6-11
     * @param condition
     * @param context
     * @return
     * @throws BusinessException
     */
    public static Map<Integer, Map> getDataTypeInfo(String condition, LoginContext context) throws BusinessException
    {
        InfoSetVO[] vos = NCLocator.getInstance().lookup(IInfoSetQry.class).queryInfoSet(context, condition);
        Map<String, FldreftypeVO> refDataTypeMap = getRefDataTypeMap(vos);
        Map<String, FldreftypeVO> comboDataTypeMap = getComboboxDataTypeMap(vos);
        Map<Integer, Map> dataTypeMap = new HashMap<Integer, Map>();
        dataTypeMap.put(5, refDataTypeMap);
        dataTypeMap.put(6, comboDataTypeMap);
        return dataTypeMap;
    }
    
    public static String[] getHyperlinks(RtfTemplateVO rtfVO) throws IOException, Exception
    {
        DHyperlink[] hyperLinks = null;
        GZIPInputStream template = null;
        ByteArrayInputStream zipTemplate = null;
        
        try
        {
            zipTemplate = new ByteArrayInputStream(rtfVO.getRtfDocument());
            template = new GZIPInputStream(zipTemplate);
            
            hyperLinks = WordUtil.getHyperLinkList(template);
        }
        finally
        {
            IOUtils.closeQuietly(template);
            IOUtils.closeQuietly(zipTemplate);
        }
        
        if (hyperLinks == null)
        {
            return null;
        }
        
        List<String> itemExpressesList = new ArrayList<String>();
        for (int i = 0; i < hyperLinks.length; i++)
        {
            String item = hyperLinks[i].strRefTxt;
            if (!StringUtils.isBlank(item))
            {
                itemExpressesList.add(item);
            }
        }
        
        return itemExpressesList.size() == 0 ? null : itemExpressesList.toArray(new String[itemExpressesList.size()]);
    }
    
    /**
     * @author fengwei on 2010-5-31
     * @param vo
     * @param bean
     * @param i
     * @return
     */
    public static IType getInfoItemDataType(InfoItemVO vo, IBean bean)
    {
        IAttribute attribute = bean.getAttributeByName(vo.getItem_code());
        if (attribute == null)
        {
            return null;
        }
        IType dataType = attribute.getDataType();
        return dataType;
    }
    
    /**
     * 根据beanID得到子表、主表所有bean，和主表主键
     * @author fengwei on 2010-6-11
     * @param beanID
     * @return
     * @throws BusinessException
     */
    public static HashMap<String, Object> getMetedataInfo(String beanID) throws BusinessException
    {
        IBean bean = RefDataTypeHelper.getBeanByBeanID(beanID);
        List<IBean> listBean = RefDataTypeHelper.getSubBean(bean);
        HashMap<String, String> primarykeyMap = RefDataTypeHelper.getAllPrimarykey(bean, listBean);
        HashMap<String, Object> beanMap = new HashMap<String, Object>();
        beanMap.put("mainTable", bean);
        beanMap.put("subTable", listBean);
        beanMap.put("primarykeyMap", primarykeyMap);
        return beanMap;
    }
    
    public static List<String> getNoRecodnumColSubBean(List<IBean> listBean)
    {
        List<String> tableName = new ArrayList<String>();
        List<String> table = new ArrayList<String>();
        for (IBean childBean : listBean)
        {
            String subTable = childBean.getTable().getName();
            List<IAttribute> attributes = childBean.getAttributes();
            childBean.getPrimaryKey();
            for (IAttribute attribute : attributes)
            {
                String subColumnName = attribute.getName();
                tableName.add(subColumnName);
                
            }
            if (!tableName.contains("recordnum"))
            {
                table.add(subTable);
            }
            
        }
        return table;
    }
    
    public static Map<String, FldreftypeVO> getRefDataTypeMap(InfoSetVO[] vos)
    {
        Map<String, FldreftypeVO> fldMap = new HashMap<String, FldreftypeVO>();
        for (InfoSetVO vo : vos)
        {
            if (vo.getInfo_item() == null)
            {
                continue;
            }
            for (int i = 0; i < vo.getInfo_item().length; i++)
            {
                InfoItemVO itemVO = vo.getInfo_item()[i];
                String pk_fldreftype = itemVO.getRef_model_name();
                if (pk_fldreftype != null)
                {
                    FldreftypeVO refVO = new FldreftypeVO();
                    refVO.setFldrefname(vo.getTable_code());
                    refVO.setFldrefcode(vo.getTable_code() + "." + vo.getInfo_item()[i].getItem_code());
                    refVO.setPk_fldreftype(pk_fldreftype);
                    fldMap.put(refVO.getFldrefcode(), refVO);
                }
            }
        }
        
        return fldMap;
    }
    
    public static Map<String, FldreftypeVO> getRefDataTypeMap(LoginContext context, String condition) throws BusinessException
    {
        Map<String, FldreftypeVO> fldMap = new HashMap<String, FldreftypeVO>();
        
        InfoSetVO[] vos = NCLocator.getInstance().lookup(IInfoSetQry.class).queryInfoSet(context, condition);
        
        if (vos == null)
        {
            return fldMap;
        }
        
        for (InfoSetVO vo : vos)
        {
            for (int i = 0; i < vo.getInfo_item().length; i++)
            {
                String pk_fldreftype = vo.getInfo_item()[i].getRef_model_name();
                if (pk_fldreftype != null)
                {
                    FldreftypeVO refVO = new FldreftypeVO();
                    refVO.setFldrefname(vo.getTable_code());
                    refVO.setFldrefcode(vo.getTable_code() + "." + vo.getInfo_item()[i].getItem_code());
                    refVO.setPk_fldreftype(pk_fldreftype);
                    fldMap.put(refVO.getFldrefcode(), refVO);
                }
            }
        }
        
        return fldMap;
    }
    
    /**
     * 获取所有参照类型的信息项
     */
    public static Map<String, FldreftypeVO> getRefDataTypeMap(String corpID)
    {
        Map<String, FldreftypeVO> fldMap = new HashMap<String, FldreftypeVO>();
        
        return fldMap;
    }
    
    /**
     * 根据pk_fldreftype解析参照关联表名、主键、名称
     */
    public static HashMap<String, RefSetFieldParameter> getRefSetFieldName(String[] pk_fldreftypes)
    {
        if (nc.vo.pf.pub.util.ArrayUtil.isNull(pk_fldreftypes))
        {
            return null;
        }
        // V6的hr_infoset_item.ref_model_name统一存bd_refinfo主键,通过bd_refinfo可以找到元数据的相关信息
        // 如果可以从元数据找到表名、name字段名信息，则通过元数据找，如果找不到，则还是从refmodel的getTableName等方法中找
        
        RefInfoVO[] refInfoVOs = null;
        try
        {
            // refInfoVO = (RefInfoVO) iUAPQueryBS.retrieveByPK(RefInfoVO.class, pk_fldreftype);
            refInfoVOs =
                (RefInfoVO[]) NCLocator.getInstance().lookup(IMDPersistenceQueryService.class)
                    .queryBillOfVOByPKs(RefInfoVO.class, pk_fldreftypes, false).toArray(new RefInfoVO[0]);
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
        }
        
        if (refInfoVOs == null)
        {
            return null;
        }
        
        HashMap<String, RefSetFieldParameter> refSetFieldParaHashMap = new HashMap<String, RefSetFieldParameter>();
        for (RefInfoVO refInfoVO : refInfoVOs)
        {
            // 元数据名称
            String metadatatypename = refInfoVO.getMetadataTypeName();
            String moduleName = refInfoVO.getModuleName();
            String matadatanamespace = (String) refInfoVO.getAttributeValue("metadatanamespace");
            if (metadatatypename.equals("countryzone") || metadatatypename.equals("region") || metadatatypename.equals("address"))
            {
                matadatanamespace = "uap";
            }
            // 通过元数据名称找到对应的实体
            IBean bean = null;
            try
            {
                bean = getBeanByName(moduleName, metadatatypename);
            }
            catch (BusinessException e)
            {
                Logger.error(e.getMessage(), e);
            }
            if (bean == null)
            {
                try
                {
                    bean = getBeanByName(matadatanamespace, metadatatypename);
                }
                catch (BusinessException e)
                {
                    Logger.error(e.getMessage(), e);
                }
            }
            if (bean == null)
            {
                String refModelClassName = refInfoVO.getRefclass();
                AbstractRefModel refModel = null;
                try
                {
                    refModel = (AbstractRefModel) Class.forName(refModelClassName).newInstance();
                }
                catch (Exception e)
                {
                    Logger.error(e.getMessage(), e);
                }
                if (refModel == null)
                {
                    return null;
                }
                // @jiazhtb
                // return convertRefModel(refModel);
                refSetFieldParaHashMap.put(refInfoVO.getPk_refinfo(), convertRefModel(refModel));
                // return refSetFieldParaHashMap;.
                continue;
            }
            // return convertBean(bean);
            refSetFieldParaHashMap.put(refInfoVO.getPk_refinfo(), convertBean(bean));
        }
        
        return refSetFieldParaHashMap;
    }
    
    /**
     * 根据主表的Bean得到所有子表
     * @author fengwei on 2010-6-3
     * @param bean
     * @return
     */
    public static List<IBean> getSubBean(IBean bean)
    {
        return bean.getRelatedEntities(AssociationKind.Composite, ICardinality.ASS_ALL);
    }
}
