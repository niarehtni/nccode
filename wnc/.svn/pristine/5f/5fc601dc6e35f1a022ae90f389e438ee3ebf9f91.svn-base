package nc.impl.om.deptadj.plugin;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.itf.om.IDeptAdjustService;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

public class DeptVerExecutorPlugin implements IBackgroundWorkPlugin {

	@Override
	public PreAlertObject executeTask(BgWorkingContext arg0)
			throws BusinessException {
		//執行今天的版本化
		try{
			NCLocator.getInstance().lookup(IDeptAdjustService.class).executeDeptVersion(new UFLiteralDate());
		}catch(Exception e){
			Debug.debug(e.getMessage());
			throw new BusinessException(e.getMessage());
		}
		return null;
	}

}
