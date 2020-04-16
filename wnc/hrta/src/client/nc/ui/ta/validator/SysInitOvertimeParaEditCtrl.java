package nc.ui.ta.validator;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.para.IParaEditComponentCtrl;

/**
 * 組織參數TWHRT17（加班轉補休休假類別）參照控制類
 * 
 * @since 2019年7月22日15:53:18
 * @author tank
 */
public class SysInitOvertimeParaEditCtrl implements IParaEditComponentCtrl {

	@Override
	public void initComponentProp(Object refPane, Object objPkOrg) {
		UIRefPane ref = (UIRefPane) refPane;
		String pk_org = objPkOrg.toString();

		if (ref != null && ref.getRefModel() instanceof nc.ui.ta.period.ref.PeriodRefModel) {
			// 當參數參照選擇為休假類別拷貝時，設置當前組織
			ref.setPk_org(pk_org);
		}
	}

}
