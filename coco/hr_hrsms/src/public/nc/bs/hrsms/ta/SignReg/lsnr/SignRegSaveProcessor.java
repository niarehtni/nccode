package nc.bs.hrsms.ta.SignReg.lsnr;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.itf.hrsms.ta.SignReg.ISignRegManageMaintain;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.data.Dataset;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.ta.signcard.AggSignVO;

public class SignRegSaveProcessor implements ISaveProcessor{

	@Override
	public boolean checkBeforeVOSave(AggregatedValueObject arg0)
			throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public AggregatedValueObject onAfterVOSave(Dataset ds, Dataset[] dsDetails,
			AggregatedValueObject aggVO) throws Exception {
		// TODO Auto-generated method stub
		// 关闭弹出页面
		CmdInvoker.invoke(new CloseWindowCmd());
		// 执行左侧快捷查询
		CmdInvoker.invoke(new UifPlugoutCmd("main", "closewindow"));
		return null;
	}

	@Override
	public void onBeforeVOSave(AggregatedValueObject arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AggregatedValueObject onVOSave(AggregatedValueObject aggVO)
			throws Exception {
		// TODO Auto-generated method stub
		ISignRegManageMaintain service = null;
		try {
			service = NCLocator.getInstance().lookup(ISignRegManageMaintain.class);
			String primaryKey = aggVO.getParentVO().getPrimaryKey();
			AggSignVO aggvo = (AggSignVO) aggVO;
			if (StringUtil.isEmptyWithTrim(primaryKey)) {
				aggvo = service.insert(aggvo);
			} else {
				aggvo = service.update(aggvo);
			}
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return aggVO;
	}

}
