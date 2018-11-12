package nc.itf.jf;

public interface IUpdatePsnJob {
	/**
	 * 更新人员的工作记录中的是否主管字段 by he
	 * @param principal
	 */
	void updateSupervisor(String principal);
	/**
	 * 获取原有的部门负责人
	 * @param pk_dept
	 */
	String getOldPrincipal(String pk_dept);
	/**
	 * 更新部门负责人
	 * @param principal
	 * @param principal2
	 */
	void updateSupervisor(String newprincipal, String oldprincipal);

}
