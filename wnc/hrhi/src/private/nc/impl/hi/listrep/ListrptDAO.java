package nc.impl.hi.listrep;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.bd.pubinfo.IAddressService_C;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.org.IOrgConst;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ArrayProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.md.innerservice.MDQueryService;
import nc.md.model.type.IType;
import nc.md.model.type.impl.EnumType;
import nc.pub.tools.HiSQLHelper;
import nc.pub.tools.PageSplitUtils;
import nc.ui.bd.pubinfo.address.AddressFormater;
import nc.ui.bd.ref.IRefConst;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.vo.bd.address.AddressFormatVO;
import nc.vo.bd.ref.RefInfoVO;
import nc.vo.hi.listrep.ReportConditionVO;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.pub.CommonValue;
import nc.vo.hi.pub.FldreftypeVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.formulaset.FormuaDateHelper;
import nc.vo.hr.global.DateFormulaParse;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hr.tools.pub.GeneralVOProcessor;
import nc.vo.hr.tools.pub.Pair;
import nc.vo.hr.tools.rtf.RefDataTypeHelper;
import nc.vo.hr.tools.rtf.RefSetFieldParameter;
import nc.vo.org.OrgVO;
import nc.vo.org.orgmodel.OrgTypeVO;
import nc.vo.org.util.OrgTypeManager;
import nc.vo.pub.BusinessException;
import nc.vo.uap.rbac.util.JoinCondHelper;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 人员花名册 DAO类
 * 
 * @author fengwei on 2010-01-11
 */
public class ListrptDAO extends BaseDAOManager
{
    
    private HashMap<String, String> refsqlMap = new HashMap<String, String>();
    
    private HashMap<String, String> pkAndValue = new HashMap<String, String>();
    
    private String dealOrderStr(String orderstr, HashMap<String, String> hash)
    {
        Set<String> keySet = hash.keySet();
        Iterator<String> it = keySet.iterator();
        while (it.hasNext())
        {
            String key = it.next();
            if (orderstr.contains(key))
            {
                String name = hash.get(key);
                orderstr = orderstr.replaceAll(key, name);
            }
        }
        return orderstr;
    }
    
    // protected Connection getConnection() throws SQLException {
    // return ConnectionFactory.getConnection(getDataSourceName());
    // }
    
    // private String getDataSourceName() {
    // String dsName = InvocationInfoProxy.getInstance().getUserDataSource();
    // // if (isEmpty(dsName))
    // // dsName = InvocationInfoProxy.getInstance().getDefaultDataSource();
    // if (isEmpty(dsName)) {
    // // 一般用于在开发环境中，直接指定数据库的DataSource名称：
    // dsName = "design";
    // }
    // return dsName;
    // }
    
    /**
     * 得到查询字段
     * 
     * @author fengwei on 2010-01-11
     * @param tables
     * @param codes
     * @param hash
     * @return ArrayList<String>
     * @throws BusinessException
     */
    private ArrayList<String> getDefaultQueryFields(String[] tables, String[] codes, HashMap<String, String> hash) throws BusinessException
    {
        // 拼查询字段
        ArrayList<String> fields = new ArrayList<String>();
        
        for (int i = 0; i < tables.length; i++)
        {
            boolean iscontain = hash != null && hash.containsKey(tables[i]);
            String tableName = iscontain ? hash.get(tables[i]) : tables[i];//
            if (tableName.startsWith("hi_psndoc_"))
            {
                tableName = tableName.substring(10);
            }
            String field = codes[i];
            int index = 1;
            String strFields = tableName + "." + codes[i] + " as " + tableName + "_" + field;
            
            // 处理日期公式
            String code = parseFormulaDateField(codes[i], tables[i]);
            if (code != null && code.trim().length() > 0)
            {
                if (iscontain)
                {
                    codes[i] = code.replace(tables[i] + ".", tableName + ".");
                }
                else
                {
                    codes[i] = code;
                }
                if (fields.contains(codes[i] + " as " + tableName + "_" + field))
                {
                    field += index;
                    index++;
                }
                fields.add(codes[i] + " as " + tableName + "_" + field);
                
                continue;
            }
            
            if (fields.contains(strFields))
            {
                strFields += index;
                index++;
            }
            fields.add(strFields);
        }
        
        String extraField =
            (hash != null && hash.containsKey("hi_psnjob") ? getTableAlias("hi_psnjob", hash) : "hi_psnjob") + ".showorder ";
        if (!fields.contains(extraField))
        {
            fields.add(extraField);
        }
        
        String extraField2 =
            (hash != null && hash.containsKey("hi_psnjob") ? getTableAlias("hi_psnjob", hash) : "hi_psnjob") + ".pk_psnjob ";
        if (!fields.contains(extraField2))
        {
            fields.add(extraField2);
        }
		/**
		 * 得到变换排序条件的字段，主要目的是为了在去重的情况下可以使排序查找有效
		 * @author yangzxa 时间2016年1月23日14:26:40
		 */
        Set<String> keySet = hash.keySet();
        for (String key : keySet) {
			if(key.endsWith("_flg")){
				fields.add(hash.get(key));
			}
		}

        return fields;
    }
    
    /**
     * 得到关联表
     * 
     * @author fengwei on 2010-01-11
     * @param tables
     * @param strFromPart
     * @param hash 前台查询模板返回的表名与别名的对应
     * @return
     */
    private String getJoinedTableStr(String[] tables, String[] rptTables, String strFromPart, HashMap<String, String> hash)
    {
        // 查询某一次组织关系中的最新任职。即使组织关系结束了，最新任职标记不要清除掉。
        String joinedTableStr = strFromPart;
        
        List<String> tableList = new ArrayList<String>();
        for (String table : tables)
        {
            // if (table.startsWith("hi_psndoc_")) {
            // tableList.add(table.substring(10));
            // } else {
            tableList.add(table);
            // }
        }
        for (String table : rptTables)
        {
            // String temp = table;
            // if (temp.startsWith("")) {
            // temp = temp.substring(10);
            // }
            if (!tableList.contains(table))
            {
                tableList.add(table);
            }
        }
        
        String[] finalTables = tableList.toArray(new String[tableList.size()]);
        
        if (finalTables == null || finalTables.length < 1)
        {
            return joinedTableStr;
        }
        for (String tablename : finalTables)
        {
            if (",bd_psndoc,hi_psnorg,hi_psnjob".indexOf(tablename) > 0)
            {
                continue;
            }
            
            // String temp = tablename;
            // if(temp.startsWith("hi_psndoc_")){
            // temp = temp.substring(10);
            // }
            if (strFromPart.indexOf(tablename) > 0)
            {
                // 已经关联了相关字表,也不需要关联
                continue;
            }
            // 组织关系别名
            String psnorg = hash == null ? PsnOrgVO.getDefaultTableName() : getTableAlias(PsnOrgVO.getDefaultTableName(), hash);
            // 工作记录别名
            String psnjob = hash == null ? PsnJobVO.getDefaultTableName() : getTableAlias(PsnJobVO.getDefaultTableName(), hash);
            
            if (tablename.equalsIgnoreCase("om_job"))
            {
                // 岗位关联
                joinedTableStr += " left outer join " + tablename + " on " + tablename + ".pk_job=" + psnjob + ".pk_job";
            }
            else if (tablename.equalsIgnoreCase("org_dept"))
            {
                // 部门关联
                joinedTableStr += " left outer join " + tablename + " on " + tablename + ".pk_dept=" + psnjob + ".pk_dept";
            }
            else if (PartTimeVO.getDefaultTableName().equals(tablename))
            {
                // 兼职记录
                String part = hash == null ? PartTimeVO.getDefaultTableName() : getTableAlias(PartTimeVO.getDefaultTableName(), hash);
                if (part.startsWith("hi_psndoc_"))
                {
                    part = part.substring(10);
                }
                // wangxbd 工业和信息化部电信研究院 要求子集取最新记录
                joinedTableStr +=
                    " left outer join hi_psnjob " + part + " on " + psnorg + ".pk_psnorg = " + part + ".pk_psnorg and " + part
                        + ".ismainjob = 'N'  and " + part + ".lastflag='Y' and " + part + ".recordnum=0 ";
            }
            else if (PsndocAggVO.hashBusinessInfoSet.contains(tablename))
            {
                // 业务子集
                String tabAlias = hash == null ? tablename : getTableAlias(tablename, hash);
                if (tabAlias.startsWith("hi_psndoc_"))
                {
                    tabAlias = tabAlias.substring(10);
                }
                
                // wangxbd 工业和信息化部电信研究院 要求子集取最新记录
                joinedTableStr +=
                    " left outer join " + tablename + " " + tabAlias + " on " + tabAlias + ".pk_psnorg = " + psnorg + ".pk_psnorg and "
                        + tabAlias + ".lastflag='Y' and " + tabAlias + ".recordnum=0 ";
            }
            else
            {
                // 一般子表
                String tabAlias = hash == null ? tablename : getTableAlias(tablename, hash);
                if (tabAlias.startsWith("hi_psndoc_"))
                {
                    tabAlias = tabAlias.substring(10);
                }
                // wangxbd 工业和信息化部电信研究院 要求子集取最新记录
                if (tabAlias.equals("edu"))
                {
                    joinedTableStr +=
                        " left outer join " + tablename + " " + tabAlias + " on " + tabAlias + ".pk_psndoc=bd_psndoc.pk_psndoc and "
                            + tabAlias + ".lasteducation = 'Y' ";// 如果是学历的话，则使用是否最高学历作为条件
                }
                else
                {
                    joinedTableStr +=
                        " left outer join " + tablename + " " + tabAlias + " on " + tabAlias + ".pk_psndoc=bd_psndoc.pk_psndoc and "
                            + tabAlias + ".lastflag='Y' and " + tabAlias + ".recordnum=0 ";
                }
            }
        }
        
        return joinedTableStr;
    }
    
    private String getRefSQL(HashMap<String, FldreftypeVO> refDataTypeMap, String table, String code, String item,
            HashMap<String, RefSetFieldParameter> refSetFldParaMap)
    {
        
        FldreftypeVO fldreftypeVO = refDataTypeMap.get(item);
        if (fldreftypeVO == null)
        {
            fldreftypeVO = refDataTypeMap.get("hi_psndoc_" + item);
        }
        if (fldreftypeVO == null)
        {
            return null;
        }
        String pk_fldreftype = fldreftypeVO.getPk_fldreftype();
        // RefSetFieldParameter para = RefDataTypeHelper.getRefSetFieldName(pk_fldreftype);
        RefSetFieldParameter para = refSetFldParaMap.get(pk_fldreftype);
        String tableName = para.getTableName();
        String pkFieldName = para.getPkFieldName();
        String displayFieldName = para.getDisplayFieldName();
        String fieldName;
        String asDisplayFieldName;
        asDisplayFieldName = code.toLowerCase();
        if (!para.isMutilangText())
        {
            fieldName = displayFieldName + " " + asDisplayFieldName;
        }
        else
        {// 考虑名称多语
            String currentLangSeq = MultiLangHelper.getLangIndex();
//            String[] nameFields = new String[]{displayFieldName, displayFieldName + "2", displayFieldName + "3"};
            fieldName = "isnull(" + displayFieldName + currentLangSeq + "," + displayFieldName + ") " + asDisplayFieldName;
        }
        
        String refsql = "select " + fieldName + " from " + tableName + " where " + pkFieldName + "= ?";
        
        return refsql;
    }
    
    private String getRefValueBySQL(String strRefsql, String pk_psndoc) throws DAOException
    {
        SQLParameter param = new SQLParameter();
        param.addParam(pk_psndoc);
        Object[] rs = (Object[]) getBaseDao().executeQuery(strRefsql, param, new ArrayProcessor());
        if (rs == null || rs.length == 0 || rs[0] == null)
        {
            return null;
        }
        return rs[0].toString();
    }
    
    /**
     * 根据花名册主键得到花名册的排序字段
     * 
     * @author fengwei on 2010-01-14
     * @param pkRptDef
     * @return Pair<String>[]
     */
    public Pair<String>[] getRptSortFld(String pkRptDef) throws BusinessException
    {
        GeneralVO[] vos = queryDefaultRptSortFld(pkRptDef);
        
        List<Pair> list = new ArrayList<Pair>();
        
        if (vos == null)
        {
            return null;
        }
        
        for (GeneralVO vo : vos)
        {
            String code = vo.getAttributeValue("table_code") + "." + vo.getAttributeValue("fieldcode");
            String name = (String) vo.getAttributeValue("fieldname");
            list.add(new Pair(name, code));
        }
        
        return list.toArray(new Pair[list.size()]);
    }
    
    public String getTableAlias(String strTableName, HashMap<String, String> hash)
    {
        return hash.containsKey(strTableName) ? hash.get(strTableName) : strTableName;
    }
    
    /**
     * 得到查询sql
     * 
     * @author fengwei on 2010-01-11
     * @param strFromPart
     * @param fields
     * @param tables
     * @param condition
     * @param inSql
     * @param orderstr
     * @param hash
     * @param isGroupNode
     * @return
     * @throws SQLException
     * @throws NamingException
     */
    private String getWholeSql(String strFromPart, List<String> fields, String[] tables, String[] rptTables, String condition,
            String inSql, String orderstr, HashMap<String, String> hash, boolean isGroupNode) throws NamingException, SQLException
    {
        if (hash == null)
        {
            hash = new HashMap<String, String>();
        }
        // 拼selectStr
        String selectStr = "";
        int fieldcount = fields.size();
        for (int i = 0; i < fieldcount; i++)
        {
            String itemkey = fields.get(i);//
            if (StringUtils.isNotBlank(itemkey) && itemkey.contains("bd_psndoc.name"))
            {// 处理姓名多语问题
                itemkey = " isnull(bd_psndoc.name" + MultiLangHelper.getLangIndex() + ", bd_psndoc.name)" + " AS bd_psndoc_name";
            }
            
            selectStr = selectStr + "," + itemkey;
        }
        selectStr = selectStr.substring(1);
        
        // 得到关联表
        String joinedTableStr = getJoinedTableStr(tables, rptTables, strFromPart, hash);
        
        String tableName = getTableAlias(PsnJobVO.getDefaultTableName(), hash);
        
        if (StringUtils.isBlank(condition))
        {
            condition = " 1=1 ";
        }
        if (!isGroupNode)
        {
            // 组织权限
            String orgSql =
                HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE,
                    tableName);
            if (!StringUtils.isBlank(orgSql))
            {
                condition += " and " + orgSql;
            }
            
            // 部门权限
            String deptSql =
                HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE,
                    tableName);
            if (!StringUtils.isBlank(deptSql))
            {
                condition += " and " + deptSql;
            }
            
            // 人员类别权限
            String psnclSql =
                HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_PSNCL, IRefConst.DATAPOWEROPERATION_CODE,
                    tableName);
            if (!StringUtils.isBlank(psnclSql))
            {
                condition += " and " + psnclSql;
            }
            
            // 人员权限
            String psnPowerSql =
                HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB, IRefConst.DATAPOWEROPERATION_CODE,
                    tableName);
            if (!StringUtils.isBlank(psnPowerSql))
            {
                condition += " and " + psnPowerSql;
            }
        }
        
        // String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
        // HICommonValue.RESOUCECODE_6007PSNJOB, IRefConst.DATAPOWEROPERATION_CODE,
        // tableName);
        // // getPowerSql(HICommonValue.RESOUCECODE_6007PSNINFO);
        // if (!StringHelper.isEmpty(powerSql)) {
        // if (StringHelper.isEmpty(condition)) {
        // condition = powerSql;
        // } else {
        // condition += " and " + powerSql;
        // }
        // }
        
        String wheresql = "";
        String orderbysql = "";
        if (condition != null && condition.length() > 0)
        {
            wheresql = condition.trim();
            if (!wheresql.toLowerCase().startsWith("where"))
            {
                wheresql = "where " + wheresql;
            }
        }
        
        if (inSql != null)
        {
            wheresql += inSql;
        }
        
        if (orderstr != null && orderstr.length() > 0)
        {
            orderbysql = orderstr.trim();
            if (!orderbysql.toLowerCase().startsWith("order by"))
            {
                orderbysql = "order by " + orderbysql;
            }
        }
        // distinct
        // wanglqh
        String sql = "";
        if (orderbysql.contains("org_orgs"))
        {
            sql =
                "select  distinct bd_psndoc.pk_psndoc, org_orgs.code ," + selectStr + " from " + joinedTableStr + " " + wheresql + " "
                    + orderbysql;
        }
        else
        {
            sql = "select  distinct bd_psndoc.pk_psndoc, " + selectStr + " from " + joinedTableStr + " " + wheresql + " " + orderbysql;
        }
        return sql;
    }
    
    // private boolean isEmpty(String str) {
    // return str == null || "".equals(str.trim()) || "null".equals(str.trim());
    // }
    
    private HashMap<String, String> parseAttribute(HashMap<String, String> hash, String[] rptTables, String[] rptCodes)
    {
        HashMap<String, String> attributeMap = new HashMap<String, String>();
        for (int i = 0; i < rptTables.length; i++)
        {
            if (!hash.containsKey(rptTables[i]))
            {
                continue;
            }
            
            String oldAttribute = hash.get(rptTables[i]) + "_" + rptCodes[i];
            
            String table = rptTables[i];
            if (table.startsWith("hi_psndoc_"))
            {
                table = table.substring(10);
            }
            String newAttribute = table + "_" + rptCodes[i];
            attributeMap.put(newAttribute, oldAttribute.toLowerCase());
        }
        
        return attributeMap;
    }
    
    private String parseFormulaDateField(String code, String table) throws BusinessException
    {
        String truesql = "";
        if (code.indexOf(CommonValue.UFFORMULA_DATA) >= 0)
        {
            truesql = FormuaDateHelper.proDateFormulaSql(code, table, FormuaDateHelper.getFuncParser());
        }
        else if (code.indexOf("UFAGE[") >= 0)
        {
            truesql = DateFormulaParse.proDateFormula(code, table);
        }
        return truesql;
    }
    
    // 普通用户有权限的组织--按照功能节点查询
    public OrgVO[] queryCommonUserPowerOrg(String cuserid, String orgTypecode, String funcode, String pk_group) throws BusinessException
    {
        if (IOrgConst.GLOBEORG.equals(orgTypecode))
        {
            return new OrgVO[]{OrgTypeManager.getInstance().getGlobalOrgVO()};
        }
        OrgTypeVO typeVO = OrgTypeManager.getInstance().getOrgTypeByID(orgTypecode);
        String fName = typeVO.getFieldname();
        if (fName == null)
        {
            throw new BusinessException(ResHelper.getString("6007psn", "06007psn0227")/*
                                                                                       * @res
                                                                                       * "节点的组织类型不在组织类型表中 名称 = "
                                                                                       */+ fName);
        }
        OrgVO exampleVO = new OrgVO();
        JoinCondHelper helper1 = new JoinCondHelper(exampleVO);
        helper1.setDistinct(true);
        helper1.appendTable("sm_user_role", "ur");
        helper1.appendTable("sm_power_function", "pf");
        helper1.appendTable(exampleVO.getTableName(), "org");
        String joinCond1 =
            "ur.cuserid = ? and pf.resource_id = ? and org." + fName + " = 'Y' and pf.pk_group = ? "
                + "and ur.pk_role = pf.pk_role and pf.pk_org = org.pk_org";
        helper1.appendJoinCond(joinCond1);
        SQLParameter param = new SQLParameter();
        param.addParam(cuserid);
        param.addParam(funcode);
        param.addParam(pk_group);
        
        JoinCondHelper helper2 = new JoinCondHelper(exampleVO);
        helper2.appendTable("sm_user_role", "ur");
        helper2.appendTable("sm_role_inherit", "ri");
        helper2.appendTable("sm_baserole_func", "bf");
        helper2.appendTable(exampleVO.getTableName(), "org");
        
        String joinCond2 =
            "ur.cuserid = ? and bf.resourceid = ? and org."
                + fName
                + " = 'Y'  and bf.pk_group = ? and  ur.pk_role = ri.role_id and ri.inherited_role_id = bf.pk_baserole and ri.pk_org = org.pk_org ";
        helper2.appendJoinCond(joinCond2);
        helper2.setDistinct(true);
        param.addParam(cuserid);
        param.addParam(funcode);
        param.addParam(pk_group);
        
        List<OrgVO> result =
            (List<OrgVO>) new BaseDAO().executeQuery(helper1.getSQLScript() + " union " + helper2.getSQLScript(), param,
                new BeanListProcessor(OrgVO.class));
        return (OrgVO[]) result.toArray(new OrgVO[result.size()]);
    }
    
    /**
     * 根据花名册主键得到花名册的排序字段及排序顺序
     * 
     * @author fengwei on 2010-01-14
     * @param pkRptDef
     * @return
     */
    public GeneralVO[] queryDefaultRptSortFld(String pkRptDef) throws BusinessException
    {
        StringBuffer sql = new StringBuffer();
        sql.append("select rpt.fieldcode,case when infoset.infoset_code  = 'hi_psndoc_parttime' then 'hi_psndoc_parttime' else infoset.table_code end as table_code, rpt.sortorder, rpt.is_asc, rpt.fieldname ");
        sql.append("from hr_rpt_list_sort rpt ");
        sql.append("inner join hr_infoset_item item on item.pk_infoset_item = rpt.pk_flddict ");
        sql.append("inner join hr_infoset infoset on item.pk_infoset = infoset.pk_infoset ");
        sql.append("where rpt.pk_rpt_def = '" + pkRptDef + "' and rpt.sortorder >= 0 ");
        sql.append("order by rpt.sortorder");
        
        return (GeneralVO[]) getBaseDao().executeQuery(sql.toString(), new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
    }
    
    /**
     * 根据条件得到查询的人员的总数
     * 
     * @author fengwei on 2010-01-11
     * @param tables
     * @param rptCodes
     * @param rptTables
     * @param condition
     * @param strFromPart
     * @param hash
     * @param isGroupNode
     * @return
     * @throws BusinessException
     */
    public int queryPsnCountByCondition(String[] tables, String[] rptTables, String[] rptCodes, String condition, String strFromPart,
            HashMap<String, String> hash, boolean isGroupNode) throws BusinessException
    {
        // 拼selectStr
        ArrayList<String> fields = getDefaultQueryFields(rptTables, rptCodes, hash);
        String fieldStr = "";
        for (int i = 0; i < fields.size(); i++)
        {
            fieldStr = fieldStr + "," + fields.get(i);
        }
        fieldStr = fieldStr.substring(1);
        
        String selectStr = "  distinct bd_psndoc.pk_psndoc," + fieldStr + " ";
        // 得到关联表
        String joinedTableStr = getJoinedTableStr(tables, rptTables, strFromPart, hash);
        
        String tableName = getTableAlias(PsnJobVO.getDefaultTableName(), hash);
        
        if (StringUtils.isBlank(condition))
        {
            condition = " 1=1 ";
        }
        
        if (!isGroupNode)
        {
            // 组织权限
            String orgSql =
                HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE,
                    tableName);
            if (!StringUtils.isBlank(orgSql))
            {
                condition += " and " + orgSql;
            }
            
            // 部门权限
            String deptSql =
                HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE,
                    tableName);
            if (!StringUtils.isBlank(deptSql))
            {
                condition += " and " + deptSql;
            }
            
            // 人员类别权限
            String psnclSql =
                HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_PSNCL, IRefConst.DATAPOWEROPERATION_CODE,
                    tableName);
            if (!StringUtils.isBlank(psnclSql))
            {
                condition += " and " + psnclSql;
            }
            
            // 人员权限
            String psnPowerSql =
                HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB, IRefConst.DATAPOWEROPERATION_CODE,
                    tableName);
            if (!StringUtils.isBlank(psnPowerSql))
            {
                condition += " and " + psnPowerSql;
            }
        }
        
        // String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
        // HICommonValue.RESOUCECODE_6007PSNJOB, IRefConst.DATAPOWEROPERATION_CODE,
        // tableName);
        // if (!StringHelper.isEmpty(powerSql)) {
        // if (StringHelper.isEmpty(condition)) {
        // condition = powerSql;
        // } else {
        // condition += " and " + powerSql;
        // }
        // }
        
        String wheresql = "";
        if (condition != null && condition.length() > 0)
        {
            wheresql = condition.trim();
            if (!wheresql.toLowerCase().startsWith("where"))
            {
                wheresql = " where " + wheresql;
            }
        }
        
        String sql = " select  " + selectStr + " from " + joinedTableStr + " " + wheresql;
        sql = " select count(*) as totalcount from ( " + sql + " ) temp ";
        // 查询
        GeneralVO[] list = (GeneralVO[]) getBaseDao().executeQuery(sql, new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
        if (list == null || list.length == 0)
        {
            return 0;
        }
        return (Integer) list[0].getAttributeValue("totalcount");
    }
    
    @SuppressWarnings("unused")
    public GeneralVO[] queryPsnInf_PageQuery(String[] tables, String condition, String orderstr, ReportConditionVO cvo, int pageSize,
            int pageIndex) throws BusinessException
    {
        Vector<String> selectvec = new Vector<String>();
        Vector<String> selecttypevec = new Vector<String>();
        // select 中需要查询的字段
        String[] selectFields = cvo.getSelectFields();
        String[] oriselectFields = cvo.getSelectFields();
        // 查询字段的数据类型
        int[] selectFieldstype = cvo.getDataTypes();
        
        for (int s = 0; s < selectFields.length; s++)
        {
            selectvec.addElement(selectFields[s]);
            selecttypevec.addElement(String.valueOf(selectFieldstype[s]));
        }
        
        selectFields = (String[]) nc.vo.pub.util.Convertor.convertVectorToArray(selectvec);
        oriselectFields = (String[]) nc.vo.pub.util.Convertor.convertVectorToArray(selectvec);
        
        return null;
    }
    
    /**
     * 根据条件查询所有的人员信息，导出所有人员信息时用。
     * 
     * @author fengwei on 2010-01-18
     * @param tables
     * @param cvo
     * @param condition
     * @param orderstr
     * @param rptTables
     * @param rptCodes
     * @param strFromPart
     * @param hash
     * @param pkPsnjobs
     * @param alias
     * @return GeneralVO[]
     * @throws BusinessException
     * @throws SQLException
     * @throws NamingException
     */
    public GeneralVO[] queryPsnMainInfo(String[] tables, ReportConditionVO cvo, String condition, String orderstr, String[] rptTables,
            String[] rptCodes, String strFromPart, HashMap<String, String> hash, String[] pkPsnjobs, String alias, boolean isGroupNode)
            throws BusinessException, NamingException, SQLException
    {
        ArrayList<String> fields = getDefaultQueryFields(rptTables, rptCodes, hash);
        orderstr = dealOrderStr(orderstr, hash);
        orderstr = dealOrderStr2(orderstr);
        
        String inSql = null;
        InSQLCreator util = null;
        try
        {
            util = new InSQLCreator();
            if (alias != null && pkPsnjobs != null && pkPsnjobs.length > 0)
            {
                inSql = " and " + alias + ".pk_psnjob in ( " + util.getInSQL(pkPsnjobs) + " ) ";
            }
            
            String sql = getWholeSql(strFromPart, fields, tables, rptTables, condition, inSql, orderstr, hash, isGroupNode);
            
            int[] fieldtype = cvo.getDataTypes();
            String[] items = cvo.getSelectFields();
            HashMap<String, String> attributeMap = cvo.getAttributeMap();
            HashMap<String, FldreftypeVO> refDataTypeMap = cvo.getRefDataTypeMap();
            
            List<Object> combotypeList = new ArrayList<Object>();
            for (int i = 0; i < items.length; i++)
            {
                if (fieldtype[i] == 11)
                {
                    combotypeList.add(items[i]);
                }
            }
            
            // 查询
            GeneralVO[] vos = (GeneralVO[]) getBaseDao().executeQuery(sql, new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
            
            HashMap<String, String> map = parseAttribute(hash, rptTables, rptCodes);
            
            if (vos == null || vos.length == 0)
            {
                return null;
            }
            
            // 循环中判断是否引用优化
            RefInfoVO[] refInfoVOs = NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveAll(null, RefInfoVO.class);
            HashMap<String, RefInfoVO> refInfoMap = new HashMap<String, RefInfoVO>();
            for (RefInfoVO refInfoVO : refInfoVOs)
            {
                if (refInfoMap.containsKey(refInfoVO.getPk_refinfo())) continue;
                refInfoMap.put(refInfoVO.getPk_refinfo(), refInfoVO);
            }
            
            // 循环中查询引用sql的优化
            ArrayList<String> pk_fldreftypes = new ArrayList<String>();
            for (String item : items)
            {
                FldreftypeVO fldreftypeVO = refDataTypeMap.get(item);
                if (null == fldreftypeVO)
                {
                    fldreftypeVO = refDataTypeMap.get("hi_psndoc_" + item);
                }
                if (null == fldreftypeVO) continue;
                
                if (pk_fldreftypes.contains(fldreftypeVO.getPk_fldreftype())) continue;
                pk_fldreftypes.add(fldreftypeVO.getPk_fldreftype());
            }
            HashMap<String, RefSetFieldParameter> refSetFldParaMap =
                RefDataTypeHelper.getRefSetFieldName(pk_fldreftypes.toArray(new String[0]));
            
            for (GeneralVO vo : vos)
            {
                for (int i = 0; i < items.length; i++)
                {
                    if (map.containsKey(items[i]))
                    {
                        vo.setAttributeValue(items[i], vo.getAttributeValue(map.get(items[i])));
                    }
                    vo.setAttributeType(items[i], fieldtype[i]);
                    if (fieldtype[i] == 11)
                    {
                        String emunId = attributeMap.get(rptCodes[i]);
                        IType dataType = MDQueryService.lookupMDInnerQueryService().getEnumTypeByID(emunId);
                        IConstEnum constEnum = null;
                        if (vo.getAttributeValue(items[i]) != null)
                        {
                            constEnum = ((EnumType) dataType).getConstEnum(vo.getAttributeValue(items[i]));
                        }
                        if (constEnum != null)
                        {
                            String value = ((EnumType) dataType).getConstEnum(vo.getAttributeValue(items[i])).getName();
                            vo.setAttributeValue(items[i], value);
                        }
                    }
                    else if (fieldtype[i] == 6)
                    {
                        String refValue = "";
                        try
                        {
                            String pkValue = (String) vo.getAttributeValue(items[i]);
                            if (!StringUtils.isBlank(pkValue))
                            {
                                if (isAddressRef(refDataTypeMap, rptTables[i], rptCodes[i], items[i], refInfoMap))
                                {
                                    refValue = getAddress(pkValue);
                                }
                                else
                                {
                                    if (refsqlMap.get(items[i]) == null)
                                    {
                                        String strRefsql = getRefSQL(refDataTypeMap, rptTables[i], rptCodes[i], items[i], refSetFldParaMap);
                                        if (StringUtils.isBlank(strRefsql))
                                        {
                                            continue;
                                        }
                                        refsqlMap.put(items[i], strRefsql);
                                        
                                    }
                                    // 能力素质子集员工达到等级参照查的表是个临时表根据参照解析有错误，这里重新给改一下sql,暂时先这么处理
                                    if (items[i].equals("capa_pk_pe_scogrditem"))
                                    {
                                        refsqlMap
                                            .put(
                                                items[i],
                                                "select name pk_pe_scogrditem from  ( select  bd_defdoc.name as name  ,hr_indi_grade.pk_indi_grade as pk_defdoc from hr_indi_grade left outer join bd_defdoc on hr_indi_grade.pk_grade = bd_defdoc.pk_defdoc ) temptable  where pk_defdoc= ?");
                                    }
                                    
                                    refValue = getRefValueBySQL(refsqlMap.get(items[i]), (String) vo.getAttributeValue(items[i]));
                                }
                            }
                        }
                        catch (DAOException e)
                        {
                            Logger.error(e.getMessage(), e);
                        }
                        vo.setAttributeValue(items[i], refValue);
                    }
                }
            }
            return vos;
        }
        catch (Exception e)
        {
            Logger.error(e.getMessage(), e);
            return null;
        }
        finally
        {
            if (util != null)
            {
                util.clear();
            }
        }
    }
    
    /**
     * @param orderstr
     * @return
     */
    private String dealOrderStr2(String orderstr)
    {
        if (orderstr.indexOf("hi_psndoc_") > 0)
        {
            
            // StringHelper.replace(orderstr, "hi_psndoc_", "");
            orderstr = orderstr.replaceAll("hi_psndoc_", "");
        }
        return orderstr;
    }
    
    public GeneralVO[] queryPsnMainInfo_PageQuery(String[] tables, ReportConditionVO cvo, String condition, String orderstr,
            String[] rptTables, String[] rptCodes, int pageSize, int pageIndex, int count, String strFromPart,
            HashMap<String, String> hash, boolean isGroupNode) throws BusinessException, NamingException, SQLException
    {
        int[] fieldtype = cvo.getDataTypes();
        String[] items = cvo.getSelectFields();
        HashMap<String, String> attributeMap = cvo.getAttributeMap();
        HashMap<String, FldreftypeVO> refDataTypeMap = cvo.getRefDataTypeMap();
        
        ArrayList<String> fields = getDefaultQueryFields(rptTables, rptCodes, hash);
        // 增加pk_psndoc的排序，因为需要支持分页，需要有一个唯一字段排序。
        if (orderstr == null || orderstr.length() < 1)
        {
            orderstr = " order by bd_psndoc.pk_psndoc ";
        }
        else if (orderstr != null && orderstr.indexOf("bd_psndoc.pk_psndoc") < 0)
        {
            orderstr += ",bd_psndoc.pk_psndoc";
        }
        orderstr = dealOrderStr(orderstr, hash);
        String sql = getWholeSql(strFromPart, fields, tables, rptTables, condition, null, orderstr, hash, isGroupNode);
        
        // 得到分页sql
        String pageSql = new PageSplitUtils().getPageQuerySql(sql, pageSize, pageIndex, count);
        
        Logger.debug("花名册节点查询的原始sql： " + sql);
        Logger.debug("花名册节点查询的分页sql： " + pageSql);
        
        List<Object> combotypeList = new ArrayList<Object>();
        List<Object> reftypeList = new ArrayList<Object>();
        for (int i = 0; i < items.length; i++)
        {
            if (fieldtype[i] == 11)
            {// 下拉列表
                combotypeList.add(items[i]);
            }
            else if (fieldtype[i] == 6)
            {// 参照
                reftypeList.add(rptTables[i] + "." + rptCodes[i]);
            }
        }
        
        // 查询
        GeneralVO[] vos = (GeneralVO[]) getBaseDao().executeQuery(pageSql, new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
        HashMap<String, String> map = parseAttribute(hash, rptTables, rptCodes);
        
        if (vos == null || vos.length == 0)
        {
            return null;
        }
        
        // 循环中判断是否引用优化
        RefInfoVO[] refInfoVOs = NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveAll(null, RefInfoVO.class);
        HashMap<String, RefInfoVO> refInfoMap = new HashMap<String, RefInfoVO>();
        for (RefInfoVO refInfoVO : refInfoVOs)
        {
            if (refInfoMap.containsKey(refInfoVO.getPk_refinfo())) continue;
            refInfoMap.put(refInfoVO.getPk_refinfo(), refInfoVO);
        }
        
        // 循环中查询引用sql的优化
        ArrayList<String> pk_fldreftypes = new ArrayList<String>();
        for (String item : items)
        {
            FldreftypeVO fldreftypeVO = refDataTypeMap.get(item);
            if (null == fldreftypeVO)
            {
                fldreftypeVO = refDataTypeMap.get("hi_psndoc_" + item);
            }
            if (null == fldreftypeVO) continue;
            
            if (pk_fldreftypes.contains(fldreftypeVO.getPk_fldreftype())) continue;
            pk_fldreftypes.add(fldreftypeVO.getPk_fldreftype());
        }
        HashMap<String, RefSetFieldParameter> refSetFldParaMap =
            RefDataTypeHelper.getRefSetFieldName(pk_fldreftypes.toArray(new String[0]));
        
        // @jiazhtb
        // 该部分注释不要删除
        for (GeneralVO vo : vos)
        {
            // String[] names = vo.getAttributeNames();
            for (int i = 0; i < items.length; i++)
            {
                if (map.containsKey(items[i]))
                {
                    vo.setAttributeValue(items[i], vo.getAttributeValue(map.get(items[i])));
                }
                vo.setAttributeType(items[i], fieldtype[i]);
                if (fieldtype[i] == 11)
                {
                    String emunId = attributeMap.get(rptCodes[i]);
                    IType dataType = MDQueryService.lookupMDInnerQueryService().getEnumTypeByID(emunId);
                    IConstEnum constEnum = null;
                    if (vo.getAttributeValue(items[i]) != null)
                    {
                        constEnum = ((EnumType) dataType).getConstEnum(vo.getAttributeValue(items[i]));
                    }
                    
                    if (constEnum != null)
                    {
                        String value = ((EnumType) dataType).getConstEnum(vo.getAttributeValue(items[i])).getName();
                        vo.setAttributeValue(items[i], value);
                    }
                }
                else if (fieldtype[i] == 6)
                {
                    String refValue = "";
                    try
                    {
                        
                        String pkValue = (String) vo.getAttributeValue(items[i]);
                        if (!StringUtils.isBlank(pkValue) && null == pkAndValue.get(pkValue))
                        {
                            if (isAddressRef(refDataTypeMap, rptTables[i], rptCodes[i], items[i], refInfoMap))
                            {
                                refValue = getAddress(pkValue);
                            }
                            else
                            {
                                if (refsqlMap.get(items[i]) == null)
                                {
                                    String strRefsql = getRefSQL(refDataTypeMap, rptTables[i], rptCodes[i], items[i], refSetFldParaMap);
                                    //wanglinw:与之前的补丁冲突，解决多语问题加逻辑判断
                                    if(strRefsql.contains("name22")){
                                    	strRefsql=strRefsql.replace("name22", "name2");
                                    }
                                    
                                    refsqlMap.put(items[i], strRefsql);
                                    
                                }
                                refValue = getRefValueBySQL(refsqlMap.get(items[i]), pkValue);
                            }
                            pkAndValue.put(pkValue, refValue);
                        }
                        
                    }
                    catch (DAOException e)
                    {
                        Logger.error(e.getMessage(), e);
                    }
                    
                    vo.setAttributeValue(items[i], pkAndValue.get((String) vo.getAttributeValue(items[i])));
                }
            }
        }
        pkAndValue.clear();
        refsqlMap.clear();
        return vos;
    }
    
    private String getAddress(String pkValue) throws BusinessException
    {
        // 处理地址簿
        AddressFormatVO[] formatVOs = NCLocator.getInstance().lookup(IAddressService_C.class).format(new String[]{pkValue});
        if (ArrayUtils.isEmpty(formatVOs))
        {
            return null;
        }
        return AddressFormater.format(new AddressFormatVO[]{formatVOs[0]})[0];
    }
    
    private boolean isAddressRef(HashMap<String, FldreftypeVO> refDataTypeMap, String rptTables, String rptCodes, String item,
            HashMap<String, RefInfoVO> refInfoMap) throws BusinessException
    {
        FldreftypeVO fldreftypeVO = refDataTypeMap.get(item);
        if (fldreftypeVO == null)
        {
            fldreftypeVO = refDataTypeMap.get("hi_psndoc_" + item);
        }
        if (fldreftypeVO == null)
        {
            return false;
        }
        // RefInfoVO ref = (RefInfoVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
        // .retrieveByPk(null, RefInfoVO.class, fldreftypeVO.getPk_fldreftype());
        RefInfoVO ref = refInfoMap.get(fldreftypeVO.getPk_fldreftype());
        return ref.getMetadataTypeName() != null && ref.getMetadataTypeName().equals("address");
    }
    
    /**
     * 根据条件得到分页查询的人员信息
     * 
     * @author fengwei on 2010-01-11
     * @param tables
     * @param condition
     * @param orderstr
     * @param pageSize 每页记录条数
     * @param pageIndex 当前页码
     * @return
     * @throws BusinessException
     * @throws SQLException
     * @throws NamingException
     */
    public GeneralVO[] queryPsnMainInfo_PageQuery(String[] tables, String condition, String orderstr, String[] rptTables,
            String[] rptCodes, int pageSize, int pageIndex, int count) throws BusinessException, NamingException, SQLException
    {
        ArrayList<String> fields = getDefaultQueryFields(rptTables, rptCodes, null);
        String sql = getWholeSql(null, fields, tables, rptTables, condition, null, orderstr, null, false);
        
        // 得到分页sql
        String pageSql = new PageSplitUtils().getPageQuerySql(sql, pageSize, pageIndex, count);
        
        // 查询
        return (GeneralVO[]) getBaseDao().executeQuery(pageSql, new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
        
    }
    
}
