package nc.vo.om.hrdept;

import nc.hr.utils.MultiLangHelper;
import nc.vo.org.DeptVO;
import nc.vo.pub.IVOMeta;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * HR����VO
 * <p>
 * �̳���UAP�Ĳ���VO<br>
 * 
 * @author zhangdd
 * 
 */
public class HRDeptVO extends DeptVO
{
    
    /** UID */
    private static final long serialVersionUID = 1L;
    
    public static Integer DEFAULT_DISPLAY_ORDER = 999999;
    
    /** �ϼ������Ƿ�仯 */
    private Boolean fatherDeptChanged = false;
    
    /** ��׼�ĺ� */
    private String approvenum;
    /** ��׼��λ */
    private String approvedept;
    private String deptduty;
    // �жϲ����Ƿ���ί��
    private UFBoolean managescope;
    //�ϼ����Ź�����Ա
    private String dept_charge;
    
    public String getApprovenum() {
		return approvenum;
	}

	public void setApprovenum(String approvenum) {
		this.approvenum = approvenum;
	}

	public String getApprovedept() {
		return approvedept;
	}

	public void setApprovedept(String approvedept) {
		this.approvedept = approvedept;
	}

	public static final String DEPT_CHARGE = "DEPT_CHARGE";
    
    public UFBoolean isManagescope()
    {
        return managescope;
    }
    
    public void setManagescope(UFBoolean managescope)
    {
        this.managescope = managescope;
    }
    
    public String getDeptduty()
    {
        return deptduty;
    }
    
    public void setDeptduty(String deptduty)
    {
        this.deptduty = deptduty;
    }
    
    public boolean isFatherDeptChanged()
    {
        return fatherDeptChanged;
    }
    
    public void setFatherDeptChanged(Boolean fatherDeptChanged)
    {
        if (fatherDeptChanged == null)
        {
            this.fatherDeptChanged = Boolean.FALSE;
        }
        else
        {
            this.fatherDeptChanged = fatherDeptChanged;
        }
    }
    
    /*
     * public DeptHistoryVO[] getDepthistory() { return depthistory; }
     */
    
    /*
     * public void setDepthistory(DeptHistoryVO[] depthistory) { // ���趨ǰ����Ҫ������Ч���ڵĵ������� if
     * (!ArrayUtils.isEmpty(depthistory)) { SuperVOHelper.sort(depthistory, new String[] {
     * DeptHistoryVO.EFFECTDATE }, new boolean[] { true }, false); } this.depthistory = depthistory; }
     */
    
    /*
     * public DeptManager[] getDeptmanager() { return deptmanager; } public void setDeptmanager(DeptManager[]
     * deptmanager) { this.deptmanager = deptmanager; } public DeptOtherVO[] getDeptother() { return
     * deptother; } public void setDeptother(DeptOtherVO[] deptother) { this.deptother = deptother; }
     */
    
    @Override
    public IVOMeta getMetaData()
    {
        return VOMetaFactory.getInstance().getVOMeta("hrjf.hrdept");
    }
    
    /*
     * @Override public void setAttributeValue(String name, Object value) { if ("depthistory".equals(name)) {
     * setDepthistory((DeptHistoryVO[]) value); return; } else if ("deptmanager".equals(name)) {
     * setDeptmanager((DeptManager[]) value); return; } else if ("deptother".equals(name)) {
     * setDeptother((DeptOtherVO[]) value); return; } super.setAttributeValue(name, value); }
     */
    
    @Override
    public String toString()
    {
        
        return getCode() + " " + getMultilangName();
    }
    
    public String getMultilangName()
    {
        return MultiLangHelper.getName(this);
    }
    
    @Override
    public String getPrimaryKey()
    {
        return getPk_dept();
    }
    
    @Override
    public void setPrimaryKey(String key)
    {
        setPk_dept(key);
        
    }

	public String getDept_charge() {
		return dept_charge;
	}

	public void setDept_charge(String dept_charge) {
		this.dept_charge = dept_charge;
	}
    
}
