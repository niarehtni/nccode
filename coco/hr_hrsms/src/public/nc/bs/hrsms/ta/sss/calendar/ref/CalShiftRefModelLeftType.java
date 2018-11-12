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
		// ����
		setRefTitle(ResHelper.getString("hrbd","0hrbd0223")/*@res "���(ҵ��Ԫ)"*/);
		setRootName(ResHelper.getString("shifttype","2bdsfttype-0000")/*@res "������"*/);
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
		// �ұ�
		setDocJoinField(ShiftVO.PK_SHIFTTYPE);
		setPkFieldCode(ShiftVO.PK_SHIFT);
        setRefCodeField(ShiftVO.CODE);
        setRefNameField(ShiftVO.NAME);
        setDefaultFieldCount(2);
        setTableName(ShiftVO.getDefaultTableName());
        setHiddenFieldCode(new String[]{ShiftVO.PK_SHIFT,ShiftVO.PK_ORG});
        setFieldCode(new String[]{ShiftVO.CODE, ShiftVO.NAME});
        setFieldName(new String[]{ResHelper.getString("hrbd","0hrbd0221")/*@res "��α���"*/, 
        		ResHelper.getString("hrbd","0hrbd0222")/*@res "�������"*/});
        setWherePart(" 1 = 1 and "+ShiftVO.ENABLESTATE+" != 3 ");
      //  setPKMatch(true);//��֪��Ϊʲô��������
        resetFieldName();
//        setFilterRefNodeName(new String[]{"��֯������ȫ�֣�(����)"});
        setFilterRefNodeName(new String[]{"ҵ��Ԫ"});/*-=notranslate=-*/
	}

	@SuppressWarnings("rawtypes")
	@Override
	//����д������������Ա�������������ư��ʱ�� ���û�����İ��ctrl+v���ɹ����������reset()��ֱ��setPKMatch��֪��Ϊʲô���ɹ���������д�˷���
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
		//������ʾ����֯�İ��֮�⣬��Ҫ��ʾȫ�ֵġ����ݡ���Σ�ϵͳԤ�ã����û����壩�������ţ��ŵ꣩�İ��
		String whereSql = " pk_dept = '"+SessionUtil.getPk_mng_dept()+"' or (pk_org='"+IOrgConst.GLOBEORG+"' and pk_shift <> '0001Z70000000DEFAULT')";
		whereSql += " or ( PK_SHIFT  in (select pk_shift from BD_SHIFT where PK_ORG='"+getPk_org()+"' and PK_DEPT='~')) ";
		//String whereSql = " pk_org='"+getPk_org()+"' or (pk_org='"+IOrgConst.GLOBEORG+"' and pk_shift <> '0001Z70000000DEFAULT') ";
		//add begin @shaochj ��̫ƽ����Ŀ��
	//	whereSql += " or pk_dept = '"+SessionUtil.getPk_mng_dept()+"'";
		//add end @shaochj ��̫ƽ����Ŀ��
		if(StringUtils.isNotEmpty(getPk_shifttype())){
			whereSql += " and " + ShiftVO.PK_SHIFTTYPE + " = '" + getPk_shifttype() + "' ";
		}
		return whereSql;
	}

	 @Override
	    protected void addJoinCondition(StringBuffer sqlBuffer)
	    {
	        // �������---���ǲ�����WherePart
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
	            /** �ӻ�������� */
	            v = getCacheValue(sql);
	        }
	        
	        if (v == null)
	        {
	            try
	            {
	                /** �����ݿ�� */
	                v = queryMain(getDataSource(), sql);
	            }
	            catch (Exception e)
	            {
	                Logger.error(e.getMessage(), e);
	            }
	            
	            // ���һ�����޷��ࡱ���������ڵ�
	            Vector noClassItem = new Vector();
	            noClassItem.add("");
	            noClassItem.add(ResHelper.getString("hrbd", "0hrbd0233")
	            /* @res "δ����" */);
	            noClassItem.add(NoClassRefGridTreeModel.NOCLASS_VALUE);
	            noClassItem.add("GLOBLE00000000000000");
	            noClassItem.add(null);
	            
	            if (v == null)
	            {
	                v = noClassItem;
	            }
	            else
	            {
	                /** ���뵽������ */
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
