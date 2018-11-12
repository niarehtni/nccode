package nc.bs.hrsms.ta.sss.calendar.ref;

import java.util.Vector;

import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.org.IOrgConst;
import nc.jdbc.framework.util.DBConsts;
import nc.ui.bd.ref.IRefConst;
import nc.ui.bd.ref.model.NoClassRefGridTreeModel;
import nc.ui.hr.ref.NoClassRefGridTreeNoBigDataModel;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.vo.bd.shift.ShiftTypeVO;
import nc.vo.bd.shift.ShiftVO;

import org.apache.commons.lang.StringUtils;

public class CalShiftRefModelLeftType extends NoClassRefGridTreeNoBigDataModel {

	private String pk_shifttype;
	public CalShiftRefModelLeftType(){
		reset();
	}

	@Override
    public void reset(){
		// 左树
		setRefTitle(ResHelper.getString("hrbd","0hrbd0223")/*@res "班次(业务单元)"*/);
		setRootName(ResHelper.getString("shifttype","2bdsfttype-0000")/*@res "班次类别"*/);
		setClassTableName(ShiftTypeVO.getDefaultTableName());
        setClassRefNameField(ShiftTypeVO.NAME);
		setClassFieldCode(new String[] { ShiftTypeVO.CODE, ShiftTypeVO.NAME, ShiftTypeVO.PK_SHIFTTYPE, ShiftTypeVO.PK_ORG});
		setClassDefaultFieldCount(2);
		setClassJoinField(ShiftTypeVO.PK_SHIFTTYPE);
		setClassFatherField(ShiftTypeVO.PK_ORG);
		setChildField(ShiftTypeVO.PK_SHIFTTYPE);
		setAddEnableStateWherePart(false); 
//		setCompositeTreeByClassValue(false); 
		setClassWherePart("1 = 1 and " +ShiftTypeVO.PK_ORG + " = '" + getPk_org() + "' and  "+ShiftTypeVO.ENABLESTATE+" !=3  " );
//		setClassJoinValue(NoClassRefGridTreeModel.NOCLASS_VALUE);
		// 右表
		setDocJoinField(ShiftVO.PK_SHIFTTYPE);
		setPkFieldCode(ShiftVO.PK_SHIFT);
        setRefCodeField(ShiftVO.CODE);
        setRefNameField(ShiftVO.NAME);
        setDefaultFieldCount(2);
        setTableName(ShiftVO.getDefaultTableName());
        setHiddenFieldCode(new String[]{ShiftVO.PK_SHIFT,ShiftVO.PK_ORG});
        setFieldCode(new String[]{ShiftVO.CODE, ShiftVO.NAME});
        setFieldName(new String[]{ResHelper.getString("hrbd","0hrbd0221")/*@res "班次编码"*/, 
        		ResHelper.getString("hrbd","0hrbd0222")/*@res "班次名称"*/});
        setWherePart(" 1 = 1 and "+ShiftVO.ENABLESTATE+" != 3 ");
      //  setPKMatch(true);//不知道为什么不起做用
        resetFieldName();
//        setFilterRefNodeName(new String[]{"组织（包含全局）(所有)"});
        setFilterRefNodeName(new String[]{"业务单元"});/*-=notranslate=-*/
	}

	@SuppressWarnings("rawtypes")
	@Override
	//不重写这个方法会造成员工工作日历复制班次时候 如果没有类别的班次ctrl+v不成功，在上面的reset()中直接setPKMatch不知道为什么不成功，所以重写此方法
	public Vector matchData(String[] fields, String[] values) {
		setPKMatch(true);
		Vector v = super.matchData(fields, values);
		setPKMatch(false);
		return v;
	}
	
	@Override
	public void filterValueChanged(ValueChangedEvent changedValue) {
		String[] pk_orgs = (String[]) changedValue.getNewValue();
		if (pk_orgs != null && pk_orgs.length > 0) {
			setPk_org(pk_orgs[0]);
		}
	}
	
	@Override
	protected String getEnvWherePart() {
		//除了显示本组织的班别之外，还要显示全局的“公休”班次（系统预置，非用户定义）、本部门（门店）的班次
		String whereSql = " pk_dept = '"+SessionUtil.getPk_mng_dept()+"' or (pk_org='"+IOrgConst.GLOBEORG+"' and pk_shift <> '0001Z70000000DEFAULT')";
		whereSql += " or ( PK_SHIFT  in (select pk_shift from BD_SHIFT where PK_ORG='"+getPk_org()+"' and PK_DEPT='~')) ";
		//String whereSql = " pk_org='"+getPk_org()+"' or (pk_org='"+IOrgConst.GLOBEORG+"' and pk_shift <> '0001Z70000000DEFAULT') ";
		//add begin @shaochj 【太平鸟项目】
	//	whereSql += " or pk_dept = '"+SessionUtil.getPk_mng_dept()+"'";
		//add end @shaochj 【太平鸟项目】
		if(StringUtils.isNotEmpty(getPk_shifttype())){
			whereSql += " and " + ShiftVO.PK_SHIFTTYPE + " = '" + getPk_shifttype() + "' ";
		}
		return whereSql;
	}

	 @Override
	    protected void addJoinCondition(StringBuffer sqlBuffer)
	    {
	        // 处理关联---但是不加入WherePart
	        if (getClassJoinValue() != null && !getClassJoinValue().equals(IRefConst.QUERY))
	        {
	            if (isExactOn())
	            {
	                if (NoClassRefGridTreeModel.NOCLASS_VALUE.equals(getClassJoinValue()))
	                {
	                    sqlBuffer.append(" and ( ").append(getDocJoinField()).append(" = '" + DBConsts.NULL_WAVE + "' or " +getDocJoinField()+" is null)");
	                }
	                else
	                {
	                    sqlBuffer.append(" and ( ").append(getDocJoinField()).append(" = '").append(getClassJoinValue()).append("' )");
	                }
	            }
	            else
	            {
	                sqlBuffer.append(" and ( " + getDocJoinField() + " like '" + getClassJoinValue() + "%' )");
	            }
	        }
	    }
	    
	    @SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
	    public Vector getClassData()
	    {
	        String sql = getClassRefSql();
	        
	        Vector v = null;
	        
	        if (isCacheEnabled())
	        {
	            /** 从缓存读数据 */
	            v = getCacheValue(sql);
	        }
	        
	        if (v == null)
	        {
	            try
	            {
	                /** 从数据库读 */
	                v = queryMain(getDataSource(), sql);
	            }
	            catch (Exception e)
	            {
	                Logger.error(e.getMessage(), e);
	            }
	            
	            // 添加一个“无分类”的虚拟树节点
	            Vector noClassItem = new Vector();
	            noClassItem.add("");
	            noClassItem.add(ResHelper.getString("hrbd", "0hrbd0233")
	            /* @res "未分类" */);
	            noClassItem.add(NoClassRefGridTreeModel.NOCLASS_VALUE);
	            noClassItem.add("GLOBLE00000000000000");
	            noClassItem.add(null);
	            
	            if (v == null)
	            {
	                v = noClassItem;
	            }
	            else
	            {
	                /** 加入到缓存中 */
	                // cache.putValue(sql, v);
	                v.add(noClassItem);
	            }
	            
	            setCacheValue(sql, v);
	        }
	        
	        setClassVecData(v);
	        
	        return getClassVecData();
	    }
	
	
	
	public String getPk_shifttype() {
		return pk_shifttype;
	}

	public void setPk_shifttype(String pkShifttype) {
		pk_shifttype = pkShifttype;
	}

	@Override
	public String getRefSql() {
		return super.getRefSql();
	}

	@Override
	public boolean isCompositeTreeByClassValue() {
		// TODO Auto-generated method stub
		return super.isCompositeTreeByClassValue();
	}

	@Override
	protected void addClassAreaCondition(StringBuffer sqlBuffer) {
		// TODO Auto-generated method stub
		String sql = getClassSql(new String[] { getClassJoinField() },  getClassTableName(), getClassWherePart(), null);
		if (sql != null) {
			sqlBuffer.append(" and (" + getDocJoinField() + " = '" + DBConsts.NULL_WAVE + "' or " +getDocJoinField()+" is null or " + getDocJoinField() + " in (" + sql + "))");
		}
//		super.addClassAreaCondition(sqlBuffer);
	}

	
}
