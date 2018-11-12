package nc.impl.hi.repdef;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.DataManageObject;
import nc.bs.pub.SystemException;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.uap.sf.IServiceProviderSerivce;
import nc.jdbc.framework.util.DBUtil;
import nc.vo.hi.repdef.InfoSetItemVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

public class PsnInfReportDAO extends DataManageObject
{

	/**
	 * PsnInfReportDMO 构造子注解。
	 * 
	 * @throws NamingException
	 * @throws SystemException
	 */
	public PsnInfReportDAO() throws NamingException, SystemException
	{
		super();
	}

	/**
	 * PsnInfReportDMO 构造子注解。
	 * 
	 * @param dbName java.lang.String
	 * @exception javax.naming.NamingException 异常说明。
	 * @exception nc.bs.pub.SystemException 异常说明。
	 */
	public PsnInfReportDAO(String dbName) throws NamingException, SystemException
	{
		super(dbName);
	}

	/**
	 * 插入已排序的信息项
	 * 
	 * @param pkRptDef
	 * @param userid
	 * @param itemVOs
	 * @param isCopy TODO
	 * @return
	 * @throws java.sql.SQLException
	 */
	public String[] insertOrderedFlddict(String pkRptDef, String userid, InfoSetItemVO[] itemVOs, boolean isCopy) throws java.sql.SQLException
	{

		String sql = "insert into hr_rpt_list_field(pk_rpt_field, fieldcode, fieldname, pk_flddict, dr, pk_rpt_def, isdisplay, showorder) "
				+ "values (?,?,?,?,?,?,?,?)";
		String sql1 = "insert into hr_rpt_list_sort(pk_rpt_sort, fieldcode, fieldname, pk_flddict, dr, pk_rpt_def, sortorder, is_asc) "
				+ "values (?,?,?,?,?,?,?,?)";
		String sql2 = "delete from hr_rpt_list_field where pk_rpt_def = ? ";
		String sql3 = "delete from hr_rpt_list_sort where pk_rpt_def = ? ";
		String sql4 = "update hr_rpt_def set modifiedtime = ? ,modifier = ? where pk_rpt_def = ? ";

		String[] keys = null;
		Connection con = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		try
		{
			con = getConnection();
			stmt2 = con.prepareStatement(sql2);
			stmt2.setString(1, pkRptDef);
			stmt2.executeUpdate();
			stmt3 = con.prepareStatement(sql3);
			stmt3.setString(1, pkRptDef);
			stmt3.executeUpdate();

			if (itemVOs != null && itemVOs.length > 0)
			{
				keys = getOIDs(itemVOs.length);
				stmt = con.prepareStatement(sql);
				stmt1 = con.prepareStatement(sql1);
				for (int i = 0; i < itemVOs.length; i++)
				{
					stmt.setString(1, keys[i]);
					stmt1.setString(1, keys[i]);
					if (itemVOs[i].getCode() == null)
					{
						stmt.setNull(2, Types.CHAR);
						stmt1.setNull(2, Types.CHAR);
					}
					else
					{
						stmt.setString(2, itemVOs[i].getCode());
						stmt1.setString(2, itemVOs[i].getCode());
					}
					if (itemVOs[i].getName() == null)
					{
						stmt.setNull(3, Types.CHAR);
						stmt1.setNull(3, Types.CHAR);
					}
					else
					{
						stmt.setString(3, itemVOs[i].getName());
						stmt1.setString(3, itemVOs[i].getName());
					}
					if (itemVOs[i].getPk_infoset_item() == null)
					{
						stmt.setNull(4, Types.CHAR);
						stmt1.setNull(4, Types.CHAR);
					}
					else
					{
						stmt.setString(4, itemVOs[i].getPk_infoset_item());
						stmt1.setString(4, itemVOs[i].getPk_infoset_item());
					}
					stmt.setInt(5, 0);
					stmt1.setInt(5, 0);
					stmt.setString(6, pkRptDef);
					stmt1.setString(6, pkRptDef);
					if (itemVOs[i].getIs_display() != null && !itemVOs[i].getIs_display().booleanValue())
					{
						stmt.setString(7, "N");
					}
					else
					{
						stmt.setString(7, "Y");
					}
					if (itemVOs[i].getShoworder() == null)
					{
						stmt.setNull(8, Types.INTEGER);
					}
					else
					{
						stmt.setInt(8, itemVOs[i].getShoworder().intValue());
					}
					if (itemVOs[i].getSortorder() == null)
					{
						stmt1.setNull(7, Types.INTEGER);
					}
					else
					{
						stmt1.setInt(7, itemVOs[i].getSortorder().intValue());
					}
					if (itemVOs[i].getIs_asc() == null)
					{
						stmt1.setNull(8, Types.CHAR);
					}
					else
					{
						stmt1.setString(8, itemVOs[i].getIs_asc());
					}
					stmt.addBatch();
					stmt1.addBatch();
				}
				stmt.executeBatch();
				stmt1.executeBatch();
			}

			if (isCopy)
			{
				return keys;
			}
			stmt4 = con.prepareStatement(sql4);
			// 得到时间戳
			String tsReal = PubEnv.getServerTime().toString();			
			stmt4.setString(1, tsReal);
			stmt4.setString(2, userid);
			stmt4.setString(3, pkRptDef);
			stmt4.executeUpdate();
		} finally {
			DBUtil.closeStmt(stmt);
			DBUtil.closeStmt(stmt1);
			DBUtil.closeStmt(stmt2);
			DBUtil.closeStmt(stmt3);
			DBUtil.closeStmt(stmt4);
			DBUtil.closeConnection(con);
		}

		return keys;
	}

	/**
	 * 根据花名册报表主键查询已排序的信息项
	 * 
	 * @author fengwei on 2010-3-29
	 * @param pkRptDef
	 * @return
	 * @throws SQLException
	 * @exception 异常描述
	 * @see 需要参见的其它内容
	 * @since 从类的V60版本，此方法被添加进来。（可选）
	 */
	public InfoSetItemVO[] queryOrderedItem(String pkRptDef) throws SQLException
	{
		//兼职信息无法使用hi_psnjob做前缀,要使用hi_psndoc_parttime
		String sql = "select sort.fieldcode, sort.fieldname, item.data_type, item.pk_infoset_item, case when infoset.infoset_code = 'hi_psndoc_parttime' then 'hi_psndoc_parttime' else infoset.table_code end as table_code, sort.sortorder, "
				+ "field.isdisplay, sort.is_asc, item.pk_infoset, item.meta_data, item.resid, item.respath "
				+ "from hr_rpt_list_sort sort inner join hr_infoset_item item on item.pk_infoset_item = sort.pk_flddict "
				+ "inner join hr_infoset infoset on item.pk_infoset = infoset.pk_infoset "
				+ "inner join hr_rpt_list_field field on field.pk_rpt_def=sort.pk_rpt_def "
				+ "where sort.pk_rpt_def = '"
				+ pkRptDef
				+ "' and item.pk_infoset_item = field.pk_flddict " + "order by field .showorder";

		InfoSetItemVO[] itemVOs = null;
		Vector<InfoSetItemVO> v = new Vector<InfoSetItemVO>();
		Connection con = null;
		PreparedStatement stmt = null;

		try
		{
			con = getConnection();
			stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			//
			while (rs.next())
			{
				InfoSetItemVO item = new InfoSetItemVO();
				// name 编码 :
				String fldcode = rs.getString(1);
				item.setCode(fldcode == null ? null : fldcode.trim());
				// displayname 名称 :
				String fldname = rs.getString(2);
				item.setName(fldname == null ? null : fldname.trim());
				
				//update 2018-4-23 zhangwxf 啟碁科技股份有限公司 登陆时使用繁体时人员花名册节点表头以及导出仍为简体
				String resid = rs.getString(11);
				String respath =rs.getString(12);
				if(respath==null){
					respath = "6007psn";
				}
				String name = ResHelper.getString(respath, resid);
				if(!StringUtils.isEmpty(name)){
					item.setName(name);
				}
				else{
					//自定义信息集信息项或者预置子集自定义信息项没有多语
					item.setName(fldname == null ? null : fldname.trim());
				}
				//end 2018-4-23
				
				
				// datatype :
				Integer datatype = (Integer) rs.getObject(3);
				item.setData_type(datatype == null ? null : datatype);
				//pk_infoset_item
				String pk_infoset_item = rs.getString(4);
				item.setPk_infoset_item(pk_infoset_item == null ? null : pk_infoset_item);
				//tablecode
				String tablecode = rs.getString(5);
				//默认人员的字表都是hi_psndoc_开头的,截取后半部分作为表名
				// if(tablecode.startsWith("hi_psndoc_")){
				// tablecode = tablecode.substring(10);
				// }
				item.setTable_code(tablecode == null ? null : tablecode.trim());

				// sortorder :
				Integer sortorder = (Integer) rs.getObject(6);
				item.setSortorder(sortorder == null ? null : sortorder);

				//isdisplay
				if ("N".equals((String) rs.getObject(7)))
				{
					item.setIs_display(UFBoolean.FALSE);
				}
				else
				{
					item.setIs_display(UFBoolean.TRUE);
				}
				//is_asc
				String is_asc = rs.getString(8);
				item.setIs_asc(is_asc == null ? null : is_asc.trim());

				String pk_infoset = rs.getString(9);
				item.setPk_infoset(pk_infoset == null ? null : pk_infoset.trim());
				
				String meta_date = rs.getString(10);
				item.setMeta_data(meta_date == null ? null : meta_date.trim());

				v.addElement(item);
			}
		}
		finally
		{
			try
			{
				if (stmt != null)
				{
					stmt.close();
				}
			}
			catch (Exception e)
			{
				Logger.error(e.getMessage(), e);
			}
			try
			{
				if (con != null)
				{
					con.close();
				}
			}
			catch (Exception e)
			{
				Logger.error(e.getMessage(), e);
			}
			itemVOs = new InfoSetItemVO[v.size()];
			if (v.size() > 0)
			{
				v.copyInto(itemVOs);
			}
		}

		return itemVOs;
	}

}
