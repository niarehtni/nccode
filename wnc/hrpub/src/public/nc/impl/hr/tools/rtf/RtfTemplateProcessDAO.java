package nc.impl.hr.tools.rtf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import nc.bs.dao.BaseDAO;
import nc.bs.logging.Logger;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.hr.tools.rtf.QuerySQLElement;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

/**
 * Rtf ģ�崦��ӿ� DAO��
 * @author: fengwei
 * @date: 2010-3-3 13:55:35
 */
public class RtfTemplateProcessDAO
{
    protected BaseDAO baseDAOManager = new BaseDAO();
    
    /**
     * �����ַ
     * @author fengwei on 2011-7-27
     * @param pk_address
     * @return
     * @throws BusinessException
     */
    
    public String dealAddress(String pk_address) throws BusinessException
    {
        String address = "";
        String sql =
            "select postcode, country.name as country, province.name as province, city.name as city, vsection.name as vsection, "
                + "detailinfo from bd_address " + "left outer join bd_defdoc country on country.pk_defdoc = bd_address.country "
                + "left outer join bd_defdoc province on province.pk_defdoc = bd_address.province "
                + "left outer join bd_defdoc city on city.pk_defdoc = bd_address.city "
                + "left outer join bd_defdoc vsection on vsection.pk_defdoc = bd_address.vsection " + "where pk_address = '" + pk_address
                + "'";
        
        List<String> result = null;
        try
        {
            result = (List) baseDAOManager.executeQuery(sql, new MapListProcessor());
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
            throw new BusinessException(e);
        }
        
        if (result != null && result.size() > 0)
        {
            Object obj = result.get(0);
            Map<String, String> map = new HashMap<String, String>();
            map = (Map<String, String>) obj;
            
            String postcode = map.get("postcode");
            if (!StringUtils.isBlank(postcode))
            {
                address += postcode + " ";
            }
            String country = map.get("country");
            if (!StringUtils.isBlank(country))
            {
                address += country + " ";
            }
            String province = map.get("province");
            if (!StringUtils.isBlank(province))
            {
                address += province + " ";
            }
            String city = map.get("city");
            if (!StringUtils.isBlank(city))
            {
                address += city + " ";
            }
            String vsection = map.get("vsection");
            if (!StringUtils.isBlank(vsection))
            {
                address += vsection;
            }
            String detailinfo = map.get("detailinfo");
            if (!StringUtils.isBlank(detailinfo))
            {
                address += detailinfo + " ";
            }
        }
        
        return address;
    }
    
    /**
     * ���� SQLElement �õ�order by�Ӿ�
     * @author fengwei on 2010-3-3
     * @param sqlElem
     * @return
     * @throws BusinessException
     */
    public String getOrderBySQLElement(QuerySQLElement sqlElem) throws BusinessException
    {
        String table = sqlElem.getTable();
        String sql =
            "select b.item_code from hr_infoset a inner join hr_infoset_item b on a.pk_infoset=b.pk_infoset where a.table_code = '"
                + table.toLowerCase() + "'";
        Set fieldSet = new HashSet();
        String orderBy = "";
        try
        {
            List result = (List) baseDAOManager.executeQuery(sql, new ArrayListProcessor());
            if (result == null)
            {
                orderBy = " order by " + getTablePKField(table);
            }
            for (int i = 0, len = result.size(); i < len; i++)
            {
                Object[] o = (Object[]) result.get(i);
                fieldSet.add(((String) o[0]).toLowerCase());
            }
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
            throw new BusinessException(e);
        }
        
        if (fieldSet.contains("pk_corp"))
        {
            orderBy = " order by " + table + ".pk_corp";
        }
        if (fieldSet.contains("recordnum"))
        {
            if (orderBy.contains("order by"))
            {
                orderBy = orderBy + "," + table + ".recordnum asc";
            }
            else
            {
                orderBy = " order by " + table + ".recordnum asc";
            }
        }
        
        if (orderBy.contains("order by"))
        {
            orderBy = orderBy + "," + getTablePKField(table);
        }
        else
        {
            orderBy = " order by " + getTablePKField(table);
        }
        
        return orderBy;
    }
    
    public String getOrderBySQLElement(QuerySQLElement sqlElem, HashMap<String, String> primarykeyMap) throws BusinessException
    {
        String table = sqlElem.getTable();
        if ("hi_psndoc_parttime".equals(table))
        {
            table = "hi_psnjob";
        }
        String sql =
            "select b.item_code from hr_infoset a inner join hr_infoset_item b on a.pk_infoset=b.pk_infoset where a.table_code = '"
                + table.toLowerCase() + "'";
        Set fieldSet = new HashSet();
        String orderBy = "";
        try
        {
            List result = (List) baseDAOManager.executeQuery(sql, new ArrayListProcessor());
            if (result == null)
            {
                orderBy = " order by " + getTablePKField(table);
            }
            for (int i = 0, len = result.size(); i < len; i++)
            {
                Object[] o = (Object[]) result.get(i);
                fieldSet.add(((String) o[0]).toLowerCase());
            }
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
            throw new BusinessException(e);
        }
        
        if (fieldSet.contains("pk_corp"))
        {
            orderBy = " order by " + table + ".pk_corp";
        }
        
        String primarykey = primarykeyMap.get(table);
        
        //�ȸ���recordnum�����ٸ�������
        if (fieldSet.contains("recordnum"))
        {
            if (orderBy.contains("order by"))
            {
                orderBy = orderBy + "," + table + ".recordnum asc";
            }
            else
            {
                orderBy = " order by " + table + ".recordnum asc";
            }
        }
        
        if (orderBy.contains("order by"))
        {
            orderBy = orderBy + "," + table + "." + primarykey;
        }
        else
        {
            orderBy = " order by " + table + "." + primarykey;
        }
        
        return orderBy;
    }
    
    protected String getSQL(QuerySQLElement sqlElem, String pk_psndoc)
    {
        return getSQL(sqlElem, pk_psndoc, "pk_psndoc");
    }
    
    protected String getSQL(QuerySQLElement sqlElem, String pk, String primarykey)
    {
        
        String table = sqlElem.getTable();
        Vector fields = sqlElem.getFields();
        Vector joinTable = sqlElem.getJoinTable();
        String wherePart = sqlElem.getWherePart();
        String orderBy = sqlElem.getOrderBy();
        
        String sql = "select ";
        
        // ����ֶ�
        for (int i = 0, len = fields.size(); i < len; i++)
        {
            sql += fields.get(i) + ",";
        }
        sql = sql.substring(0, sql.length() - 1) + " from ";
        
        // ����
        sql += table;
        
        // ������
        if (joinTable != null)
        {
            int len = joinTable.size();
            if (len != 0)
            {
                for (int i = 0; i < len; i++)
                {
                    sql += joinTable.get(i);
                }
            }
        }
        
        // where����
        sql += " where " + table + "." + primarykey + "='" + pk + "'";
        
        // �û�����where����
        if (wherePart != null)
        {
            sql += wherePart;
        }
        
        // ����
        if (orderBy != null)
        {
            
            sql += orderBy;
        }
        
        return sql;
    }
    
    private String getTablePKField(String table)
    {
        String pkField;
        if (table.equalsIgnoreCase("bd_psndoc"))
        {
            pkField = table + ".pk_psndoc";
        }
        else if (table.equalsIgnoreCase("hi_psnorg"))
        {
            pkField = table + ".pk_psnorg";
        }
        else if (table.equalsIgnoreCase("hi_psnjob"))
        {
            pkField = table + ".pk_psnjob";
        }
        else
        {
            pkField = table + ".pk_psndoc_sub";
        }
        return pkField;
    }
    
    /**
     * ���� SQLElement �õ���ѯ���
     * @author fengwei on 2010-3-3
     * @param sqlElem
     * @return
     * @throws BusinessException
     */
    public List<Object> queryValuesBySQLElement(QuerySQLElement sqlElem, String pk_psndoc) throws BusinessException
    {
        String sql = getSQL(sqlElem, pk_psndoc);
        
        if(sql.contains("name22")){
        	sql = sql.replace("name22", "name2");
        }
        
        List<Object> result = null;
        try
        {
            result = (List) baseDAOManager.executeQuery(sql, new MapListProcessor());
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
            throw new BusinessException(e);
        }
        return result;
    }
    
    public List<Object> queryValuesBySQLElement(QuerySQLElement sqlElem, String pk_psndoc, int cardtype) throws BusinessException
    {
        String sql = getSQL(sqlElem, pk_psndoc);
        
        List<Object> result = null;
        try
        {
            result = (List) baseDAOManager.executeQuery(sql, new MapListProcessor());
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
            throw new BusinessException(e);
        }
        return result;
    }
    
    public List<Object> queryValuesBySQLElement(QuerySQLElement sqlElem, String pk_psndoc, String primarykey) throws BusinessException
    {
        String sql = getSQL(sqlElem, pk_psndoc, primarykey);
        
        List<Object> result = null;
        try
        {
            result = (List) baseDAOManager.executeQuery(sql, new MapListProcessor());
            dealDefaultRank(sql, result);
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
            throw new BusinessException(e);
        }
        return result;
    }

    /**
     * ����ְ����ְ�ȵ�һ�Զ�Ĺ�ϵ
     * @param sql
     * @param result
     * @return
     */
	private List<Object> dealDefaultRank(String sql, List<Object> result) {
		// �����Ӧְ�ȵĶ��ֵ
		boolean jobrankBool=false;
		if (sql.indexOf("om_jobrank") > 0
				&& sql.indexOf("om_levelrelation") > 0) {
			int len = result.size();
			
			for (int i = 0; i < len; i++) {
				Map rowMap = (Map) result.get(i);
				HashMap<String, Object> vars = new HashMap<String, Object>();
				if (rowMap.get("pk_joblevel") != null) {
					
					for (int j = i + 1; j < len; j++) {
						//��һ��map
						HashMap rownextMap = (HashMap) result.get(j);

						if (rowMap.get("pk_joblevel").toString().equals(
								rownextMap.get("pk_joblevel").toString())) {
							if (rowMap.get("jobrank") != null) {
								//��Ϊ���浹��ѭ������������ط������һ����ֵ
								rownextMap.put("jobrank", rowMap.get("jobrank")
										.toString()
										+ ","
										+ rownextMap.get("jobrank").toString());
								jobrankBool=true;
							}
						}

					}
				}

			}
		}
		if(jobrankBool)
		{
			//ȥ�������}��
			int len = result.size();
			List<String> keyList =new ArrayList<String>();
			//����ɾ���ظ���
			for (int i = len-1; i >=0; i--) {
				Map rowMap = (Map) result.get(i);
				if(keyList.contains(rowMap.get("pk_joblevel").toString()))
				{
					result.remove(i);
					continue;
				}
				keyList.add(rowMap.get("pk_joblevel").toString());
			}
			return result;
		}else
		{
			return result;
		}
	}
    
}
