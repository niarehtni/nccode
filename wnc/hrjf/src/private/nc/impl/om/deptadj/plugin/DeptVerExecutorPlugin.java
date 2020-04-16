package nc.impl.om.deptadj.plugin;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.pa.PreAlertReturnType;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.itf.om.IDeptAdjustService;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

public class DeptVerExecutorPlugin implements IBackgroundWorkPlugin {

	@Override
	public PreAlertObject executeTask(BgWorkingContext arg0) throws BusinessException {
		// 執行今天的版本化
		PreAlertObject obj = new PreAlertObject();
		try {
			String errMsg = NCLocator.getInstance().lookup(IDeptAdjustService.class)
					.executeDeptVersion(new UFLiteralDate());

			if (StringUtils.isEmpty(errMsg)) {
				obj.setReturnType(PreAlertReturnType.RETURNNOTHING);
			} else {
				obj.setReturnType(PreAlertReturnType.RETURNMULTILANGTEXT);
				obj.setReturnObj(errMsg);
			}
		} catch (Exception e) {
			Debug.debug(e.getMessage());
			throw new BusinessException(e.getMessage());
		}

		return obj;
	}

}
