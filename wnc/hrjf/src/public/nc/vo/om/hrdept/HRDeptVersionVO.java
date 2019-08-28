package nc.vo.om.hrdept;

import nc.vo.pub.IVOMeta;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;
import nc.vo.vorg.DeptVersionVO;

/**
 * <b> �ڴ˴���Ҫ��������Ĺ��� </b>
 * <p>
 * �ڴ˴���Ӵ����������Ϣ
 * </p>
 * ��������:2010-04-08 10:34:45
 * 
 * @author
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
public class HRDeptVersionVO extends DeptVersionVO {
	
	private String dept_charge;
	public static final String DEPT_CHARGE = "dept_charge";
	
	
	public String getDept_charge() {
		return dept_charge;
	}


	public void setDept_charge(String dept_charge) {
		this.dept_charge = dept_charge;
	}


	@Override
    public IVOMeta getMetaData() {
    	return VOMetaFactory.getInstance().getVOMeta("hrjf.hrdept_v");
    }
}
