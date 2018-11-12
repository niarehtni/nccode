package nc.ui.ta.validator;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.para.IParaEditComponentCtrl;

/**
 * �M������TWHRT08���Ӱ��D�a���ݼ�e�����տ����
 * 
 * @since 2018-09-16
 * @author ssx
 * @version NC V65 Taiwan Localization 3.2.1
 * @see S6.5.1 ��NC65-6501-LocalizationLSLV1-SA01_�ڻ����Ą�.docx��
 */
public class SysInitLeaveTypeParaEditCtrl implements IParaEditComponentCtrl {

    @Override
    public void initComponentProp(Object refPane, Object objPkOrg) {
	UIRefPane ref = (UIRefPane) refPane;
	String pk_org = objPkOrg.toString();

	if (ref != null && ref.getRefModel() instanceof nc.ui.ta.timeitem.ref.LeaveTypeCopyRefModel) {
	    // �����������x����ݼ�e��ؐ�r���O�î�ǰ�M��
	    ref.setPk_org(pk_org);
	}
    }

}
