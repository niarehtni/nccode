package nc.ui.hrwa.wadaysalary.ace.action;

import org.apache.commons.lang.StringUtils;

import nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction;
import nc.vo.wa.pub.WaDayLoginContext;
/**
 * 
 * @author ward
 * @date 20180510
 * @功能描述：日薪计算：查询按钮
 *
 */
public class DaySalaryQueryAction extends DefaultQueryAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8088023326075404559L;


	protected boolean isActionEnable() {
		boolean isaction=super.isActionEnable();
		if (isaction&&(StringUtils.isEmpty(((WaDayLoginContext) getModel().getContext())
				.getPk_hrorg())
				|| null == (((WaDayLoginContext) getModel().getContext())
						.getCalculdate()))) {
			return false;
		}
		return true;
	}

}
