package nc.itf.ta;

import nc.hihk.hrta.vo.importovertime.ExportVO;
import nc.hihk.hrta.vo.importovertime.ImportVO;
import nc.itf.ta.bill.IBillRegisterManageMaintain;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.ta.overtime.OvertimeRegVO;

/**
 * 加班登记管理操作组件
 * @author zengcheng
 *
 */
public interface IOvertimeRegisterManageMaintain extends IBillRegisterManageMaintain<OvertimeRegVO>{
	
	/**
	 * 校验
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	OvertimeRegVO[] doCheck(OvertimeRegVO[] vos)throws BusinessException;
	
	/**
	 * 反校验
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	OvertimeRegVO[] undoCheck(OvertimeRegVO[] vos)throws BusinessException;
	
	/**
	 * 第一次转调休，若无异常无提示消息则返回转调休后的OvertimeRegVO[]，否则返回提示信息和第二次保存需要的数据
	 * V63添加，若转调休时长为非严格控制的，超长后要给出提示信息
	 * @param vos
	 * @param 转入年度
	 * @param 转入期间。加班转调休按期间结算时，此字段有值，按年结算时，此字段为null
	 * @return
	 * @throws BusinessException
	 */
	public GeneralVO over2RestFirst(OvertimeRegVO[] vos,String toRestYear,String toRestMonth)throws BusinessException;

	/**
	 * 第一次含有提示消息后的第二次保存
	 * @param gvo  使用的好处是第二次保存的时候就不用再处理数据了，因为第一次已经处理好了可以直接使用
	 * @param toRestYear
	 * @param toRestMonth
	 * @return
	 * @throws BusinessException
	 */
	public OvertimeRegVO[]  over2RestSecond( GeneralVO gvo, String toRestYear,String toRestMonth) throws BusinessException;
	
	/**
	 * 转调休
	 * @param vos
	 * @param 转入年度
	 * @param 转入期间。加班转调休按期间结算时，此字段有值，按年结算时，此字段为null
	 * @return
	 * @throws BusinessException
	 */
	OvertimeRegVO[] over2Rest(OvertimeRegVO[] vos,String toRestYear,String toRestMonth)throws BusinessException;
	
	

	/**
	 * 反转调休
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	OvertimeRegVO[] unOver2Rest(OvertimeRegVO[] vos)throws BusinessException;
	
	/**
	 * 导入加班登记
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	ImportVO[] importdata(ImportVO[] vos)throws BusinessException;
	
	/**
	 * 导出加班登记数据模板
	 * @return
	 * @throws BusinessException
	 */
	public ExportVO exportdata()throws BusinessException;
}
