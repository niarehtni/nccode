package nc.ui.ta.validator;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.para.IParaEditComponentCtrl;

/**
 * 組織參數TWHRT08（加班轉補休休假類別）參照控制類
 * 
 * @since 2018-09-16
 * @author ssx
 * @version NC V65 Taiwan Localization 3.2.1
 * @see S6.5.1 《NC65-6501-LocalizationLSLV1-SA01_勞基法改動.docx》
 */
public class SysInitLeaveTypeParaEditCtrl implements IParaEditComponentCtrl {

    @Override
    public void initComponentProp(Object refPane, Object objPkOrg) {
	UIRefPane ref = (UIRefPane) refPane;
	String pk_org = objPkOrg.toString();

	if (ref != null && ref.getRefModel() instanceof nc.ui.ta.timeitem.ref.LeaveTypeCopyRefModel) {
	    // 當參數參照選擇為休假類別拷貝時，設置當前組織
	    ref.setPk_org(pk_org);
	}
    }

}
