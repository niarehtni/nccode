package nc.itf.hr.wa;

import java.util.List;
import java.util.Map;

import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.WaLoginContext;


/**
 * 税改快速试试工具
 * @author: xuhw 
 * @date: 2018-12-04  
 * @since: eHR V6.5
 * @走查人: 
 * @走查日期: 
 * @修改人: 
 * @修改日期: 
 */
public interface ITaxupgrade_toolModelService {

/**
 * 初始化集团税改项目到公共薪资项目-集团
 */
void initTaxItems4Group(String pk_group,Map<String, WaItemVO> itemvoMap) throws BusinessException;

/**
 * 初始化某个薪资方案的某个期间的税改项目
 * 
 * @param pk_wa_class
 * @param year
 * @param month
 */
void initTaxItems4ClassItems(String pk_group, String pk_wa_class, String year, String month) throws BusinessException;

/**
 * 复制税改方案
 * @param sourceContext
 * @param targetContextList
 */
void copyTaxItemsToAnotherWaClasss(String pk_group, WaLoginContext sourceContext, List<WaLoginContext> targetContextList) throws BusinessException;

public void taxUpgradeToSelectClass(String pk_group, WaItemVO[] waitemvos, GeneralVO[] selectClassVOs, String optype) throws BusinessException;

}