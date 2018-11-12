package nc.vo.pub.table;

import nc.vo.pub.ValidationException;
import nc.vo.pub.ValueObject;

public class TableVO
  extends ValueObject
{
  private String m_tableName;
  private TableFieldVO[] m_tableFields;
  private String[] m_primaryKey;
  
  public TableVO() {}
  
  public TableVO(String tableName)
  {
    setTableName(tableName);
  }
  
  public String getEntityName()
  {
    return null;
  }
  
  public TableFieldVO[] getTableFields()
  {
    return m_tableFields;
  }
  
  public String getTableName()
  {
    return m_tableName;
  }
  
  public String[] getTablePrimaryKey()
  {
	if("md_component".equals(m_tableName)){
		return new String[]{"id"};
	}else if("md_class".equals(m_tableName)){
		return new String[]{"id"};
	}else if("md_enumvalue".equals(m_tableName)){
		return new String[]{"id"};
	}else if("md_accessorpara".equals(m_tableName)){
		return new String[]{"id"};
	}else if("md_bizitfmap".equals(m_tableName)){
		return new String[]{"intattrid"};
	}else if("md_ormap".equals(m_tableName)){
		return new String[]{"attributeid"};
	}else if("md_table".equals(m_tableName)){
		return new String[]{"id"};
	}else if("md_column".equals(m_tableName)){
		return new String[]{"id"};
	}else if("md_db_relation".equals(m_tableName)){
		return new String[]{"id"};
	}else if("md_property".equals(m_tableName)){
		return new String[]{"id"};
	}else if("md_association".equals(m_tableName)){
		return new String[]{"id"};
	}else if("md_operation".equals(m_tableName)){
		return new String[]{"id"};
	}else if("md_parameter".equals(m_tableName)){
		return new String[]{"id"};
	}else if("md_busiop".equals(m_tableName)){
		return new String[]{"id"};
	}else{
		return m_primaryKey;
	}
  }
  
  public void setTableFields(TableFieldVO[] newTableFields)
  {
    m_tableFields = newTableFields;
  }
  
  public void setTableName(String newTableName)
  {
    m_tableName = newTableName;
  }
  
  public void setTablePrimaryKey(String[] newPrimaryKey)
  {
    m_primaryKey = newPrimaryKey;
  }
  
  public void validate()
    throws ValidationException
  {}
}

/* Location:           F:\projects\CTG\CTGCODE\external\lib\basic.jar
 * Qualified Name:     nc.vo.pub.table.TableVO
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */