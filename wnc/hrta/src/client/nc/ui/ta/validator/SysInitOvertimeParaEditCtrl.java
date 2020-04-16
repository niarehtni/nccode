package nc.ui.ta.validator;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.para.IParaEditComponentCtrl;

/**
 * �M������TWHRT17���Ӱ��D�a���ݼ�e�����տ����
 * 
 * @since 2019��7��22��15:53:18
 * @author tank
 */
public class SysInitOvertimeParaEditCtrl implements IParaEditComponentCtrl {

	@Override
	public void initComponentProp(Object refPane, Object objPkOrg) {
		UIRefPane ref = (UIRefPane) refPane;
		String pk_org = objPkOrg.toString();

		if (ref != null && ref.getRefModel() instanceof nc.ui.ta.period.ref.PeriodRefModel) {
			// �����������x����ݼ�e��ؐ�r���O�î�ǰ�M��
			ref.setPk_org(pk_org);
		}
	}

}
