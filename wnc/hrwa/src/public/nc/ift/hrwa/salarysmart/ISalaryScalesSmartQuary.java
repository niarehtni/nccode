package nc.ift.hrwa.salarysmart;

import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.vo.pub.BusinessException;

public interface ISalaryScalesSmartQuary {
	/**
	 * 封装薪资条数据
	 * @param context
	 * @param isEN true 英文薪资条  ,false : 中文薪资条
	 * @return
	 */
	DataSet quarySalaryScales(SmartContext context,boolean isEN) throws BusinessException;
	
}
