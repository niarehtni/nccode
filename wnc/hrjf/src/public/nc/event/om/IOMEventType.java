package nc.event.om;

/**
 * 
 * ��֯�����¼����ͳ����ӿ�<br>
 * 
 * @author zhangdd
 * 
 */
public interface IOMEventType {

	/** �������Ʊ���� */
	public static final String DEPT_RENAME_AFTER = "60050701";

	/** ����ת��ǰ */
	public static final String DEPT_SHIFT_BEFORE = "60050702";

	/** ���źϲ�ǰ */
	public static final String DEPT_MERGE_BEFORE = "60050703";

	/** ���źϲ��� */
	public static final String DEPT_MERGE_AFTER = "60050704";

	/** ���ų���ǰ */
	public static final String DEPT_CANCEL_BEFORE = "60050705";

	/** ���ų����� */
	public static final String DEPT_CANCEL_AFTER = "60050706";

	/** ְ���ְ�����仯���¼� */
	public static final String JOBTYPE_OF_JOB_CHANGED_AFTER = "6005100901";

	/** ְ���ع����¼� */
	public static final String JOB_GRADE_REFORM_AFTER = "6005100902";
	
	/** ����ؓ؟�˱���� */
	public static final String DEPT_PRINCIPALCHANGE_AFTER = "60050799";
}
