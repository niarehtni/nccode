package nc.event.om;

/**
 * 
 * 组织机构事件类型常量接口<br>
 * 
 * @author zhangdd
 * 
 */
public interface IOMEventType {

	/** 部门名称变更后 */
	public static final String DEPT_RENAME_AFTER = "60050701";

	/** 部门转移前 */
	public static final String DEPT_SHIFT_BEFORE = "60050702";

	/** 部门合并前 */
	public static final String DEPT_MERGE_BEFORE = "60050703";

	/** 部门合并后 */
	public static final String DEPT_MERGE_AFTER = "60050704";

	/** 部门撤销前 */
	public static final String DEPT_CANCEL_BEFORE = "60050705";

	/** 部门撤销后 */
	public static final String DEPT_CANCEL_AFTER = "60050706";

	/** 职务的职务类别变化后事件 */
	public static final String JOBTYPE_OF_JOB_CHANGED_AFTER = "6005100901";

	/** 职级重构后事件 */
	public static final String JOB_GRADE_REFORM_AFTER = "6005100902";
	
	/** 部门負責人变更后 */
	public static final String DEPT_PRINCIPALCHANGE_AFTER = "60050799";
}
