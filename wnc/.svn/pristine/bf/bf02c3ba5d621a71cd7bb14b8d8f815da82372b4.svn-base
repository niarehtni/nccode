package nc.vo.wa.pub.plugin;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * @description 薪资部门所属部门对照表
 * @since 2019-05-25
 * @author kk
 * 
 */
public class WaDeptBelongVO extends SuperVO {
	private String id;
	private String pk_dept;
	private String pk_fatherorg;
	private String pk_dept_belong;
	private int cal_num;
	private int ori_num;
	private int dept_level;
	private String error_flag;
	private String dir;

	@Override
	public String getPKFieldName() {
		return "id";
	}

	@Override
	public String getTableName() {
		return "wa_dept_belong";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPk_dept() {
		return pk_dept;
	}

	public void setPk_dept(String pk_dept) {
		this.pk_dept = pk_dept;
	}

	public String getPk_fatherorg() {
		return pk_fatherorg;
	}

	public void setPk_fatherorg(String pk_fatherorg) {
		this.pk_fatherorg = pk_fatherorg;
	}

	public String getPk_dept_belong() {
		return pk_dept_belong;
	}

	public void setPk_dept_belong(String pk_dept_belong) {
		this.pk_dept_belong = pk_dept_belong;
	}

	public int getCal_num() {
		return cal_num;
	}

	public void setCal_num(int cal_num) {
		this.cal_num = cal_num;
	}

	public int getOri_num() {
		return ori_num;
	}

	public void setOri_num(int ori_num) {
		this.ori_num = ori_num;
	}

	public int getDept_level() {
		return dept_level;
	}

	public void setDept_level(int dept_level) {
		this.dept_level = dept_level;
	}

	public String getError_flag() {
		return error_flag;
	}

	public void setError_flag(String error_flag) {
		this.error_flag = error_flag;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	@nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.wa.pub.plugin.WaDeptBelongVO")
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("wadeptbelong.WaDeptBelongVO");

	}

}
