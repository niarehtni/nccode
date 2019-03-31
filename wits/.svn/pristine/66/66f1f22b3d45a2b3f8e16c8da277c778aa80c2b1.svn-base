package nc.ui.wa.taxaddtional.model;

import nc.ui.uif2.model.BillManageModel;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;
import nc.vo.wa_tax.WASpecialAdditionaDeductionVO;

/**
 * 附加费用扣除model
 * 
 * @author: xuhw
 * @date: 2010-6-25 下午04:09:24
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class TaxaddtionalModel extends BillManageModel
{
	private String exportQuerySql = null;
	private String taxyear = null;
	
	
	public String getTaxyear() {
		return taxyear;
	}

	public void setTaxyear(String taxyear) {
		this.taxyear = taxyear;
	}

	private WASpecialAdditionaDeductionVO[] psntaxvos;
	private boolean isFromHeadOut;

	public boolean isFromHeadOut() {
		return isFromHeadOut;
	}

	public void setFromHeadOut(boolean isFromHeadOut) {
		this.isFromHeadOut = isFromHeadOut;
	}

	public WASpecialAdditionaDeductionVO[] getPsntaxvos()
	{
		return psntaxvos;
	}

	public void setPsntaxvos(WASpecialAdditionaDeductionVO[] psntaxvos)
	{
		this.psntaxvos = psntaxvos;
	}
	
	public String getExportQuerySql() {
		return exportQuerySql;
	}

	public void setExportQuerySql(String exportQuerySql) {
		this.exportQuerySql = exportQuerySql;
	}

	public TaxaddtionalAppModelService getTaxModelService(){
		return (TaxaddtionalAppModelService)this.getService();
	}
	/**
	 * 数据导入到
	 * 
	 * @author xuhw  
	 * @param psndocWadocVOs
	 * @return
	 */
	public WASpecialAdditionaDeductionVO[] importData2DB(WASpecialAdditionaDeductionVO[] psndocWadocVOs, LoginContext context) throws BusinessException
	{
		return getTaxModelService().getManageService().importData(context, psndocWadocVOs);
	}

	/**
	 * 导出数据到Excel<BR>
	 * 根据查询条件查询出所有符合规则的人员，根据人员PK找出相应的最新标志为true的定调资记录<BR>
	 * 
	 * @author xuhw on 2010-5-21
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public WASpecialAdditionaDeductionVO[] exportData2Excel(LoginContext context, String condition, String orderby, String taxyear) throws BusinessException
	{
		return getTaxModelService().getQueryService().exportData(context, condition, orderby, taxyear);
	}
}
