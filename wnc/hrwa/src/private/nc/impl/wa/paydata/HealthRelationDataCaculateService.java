package nc.impl.wa.paydata;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.hr.caculate.CaculateTypeVO;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.pub.WaLoginContext;


public class HealthRelationDataCaculateService extends DataCaculateService {

	public HealthRelationDataCaculateService(WaLoginContext loginContext,
			CaculateTypeVO caculateTypeVO, String selectWhere)
			throws BusinessException {
		super(loginContext, caculateTypeVO, selectWhere);
	}
	private BaseDAO subDao = null;
	
	
	/**
	 * 只获取和二代健保薪资项有关的发放项目
	 */
	@Override
	protected void initClassItems() throws BusinessException {
		super.initClassItems();
		String pk_org = getLoginContext().getWaLoginVO().getPk_org();
		String pk_wa_class = getLoginContext().getWaLoginVO().getPk_wa_class();
		String cyear = getLoginContext().getWaLoginVO().getCyear();
		String cperiod = getLoginContext().getWaLoginVO().getCperiod();
		//需要重新计算的薪资itemKey(包含二代健保的itemkey)
		Set<String> waClassKey4ReCaculate = new HashSet<>();
		//需要重新计算的薪资项目pk(二代健保PK除外)
		List<WaClassItemVO> waClassItem4ReCacalateList = new ArrayList<>();
		//搜索二代健保回写的薪资项
		// 查询薪資項目上補充保費項目(TWEX0000规定的)
		String strSQL = "select itemkey from wa_classitem where pk_org='"
				+ pk_org
				+ "' and pk_wa_class='"
				+ pk_wa_class
				+ "' and cyear= '"
				+ cyear
				+ "' and cperiod= '"
				+ cperiod
				+ "' and pk_wa_item = (select refvalue from twhr_basedoc where pk_org='"
				+ pk_org + "' and code = 'TWEX0000' and dr=0) ";

		String itemkey = null ;
		try {
			itemkey = (String) getSubDao().executeQuery(strSQL,
					new ColumnProcessor());
		} catch (DAOException e) {
			throw new BusinessException("計算失敗!未找到補充保費項目TWEX0000.");
		}
		if(null == itemkey){
			throw new BusinessException("計算失敗!未找到補充保費項目TWEX0000.");
		}
		if (!StringUtils.isEmpty(itemkey)) {
			
			//查出本薪资方案,组织,期间内的所有薪资项目的主键,计算顺序,公式,和薪资项
			//classItemVOs;
			
			//顺序查找二代健保需要的重新计算的薪资项目
			waClassKey4ReCaculate.add(itemkey);
			int size = 1;
			do{
				size = waClassKey4ReCaculate.size();
				for (WaClassItemVO vo : classItemVOs) {
					// (如果已经包含了改项,再循环了)
					if (null == vo || vo.getItemkey() == null || waClassKey4ReCaculate.contains(vo.getItemkey())) {
						continue;
					}
					// 如果公式中包含了这些薪资项,则加入重新计算列表,加入新的薪资项,
					if (isFormulaContain(vo, waClassKey4ReCaculate)) {
						waClassItem4ReCacalateList.add(vo);
						waClassKey4ReCaculate.add(vo.getItemkey());
						// 数据集有更新,break掉,重新搜索,因为因为只有个薪资项目需要建立依赖集,所有用时间复杂度为n2,如果多个薪资项目则需要优化此段算法
						// 2019年9月2日11:09:21 tank
						break;
					}
				}
				
			}while(waClassKey4ReCaculate.size() != size);
			//薪资项目没有增加的时候,则可以结束搜索了
			
		}
		//waClassItem4ReCacalateList一定要按顺序进行重新计算
		classItemVOs = new ArrayList<>(waClassItem4ReCacalateList).toArray(new WaClassItemVO[0]);
	}

	/**
	 * 判断VO里的公司是不是包含了涉及二代健保的薪资项
	 * @param vo
	 * @param waClassKey4ReCaculate
	 * @return
	 */
	private boolean isFormulaContain(WaClassItemVO vo,
			Set<String> waClassKey4ReCaculate) {
		if(null == vo || null==vo.getPk_wa_classitem() || null == vo.getVformula() || null == vo.getItemkey()){
			return false;
		}
		//需要判别的公式
		String formula = vo.getVformula();
		for(String healthRelationItemKey : waClassKey4ReCaculate){
			if(null == healthRelationItemKey){
				continue;
			}
			//包含itemkey的字段
			int index = formula.indexOf(healthRelationItemKey);
			if(index >= 0){
				//紧接的一位不能是0-9的数字,比如要查找f_1,不能跑出f_11
				//如果包含的key已经在最后的位置,那么是符合要求的
				boolean isLast = (index + healthRelationItemKey.length()) >= formula.length();
				if(isLast){
					return true;
				}else{
					char nextChar = formula.charAt(index + healthRelationItemKey.length());
					//如果是数字,则不符合
					if(Character.isDigit(nextChar)){
						return false;
					}else{
						//不是数字,符合
						return true;
					}
				}
			}	
		}
		return false;
	}

	@Override
	public void doCaculate() throws BusinessException {
		super.doCaculate();
	}

	public BaseDAO getSubDao() {
		if(null == subDao){
			subDao = new BaseDAO();
		}
		return subDao ;
	}

	public void setSubDao(BaseDAO subDao) {
		this.subDao = subDao;
	}
	
	

}
