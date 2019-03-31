package nc.impl.wa.period;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nc.bs.bd.cache.CacheProxy;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.core.service.TimeService;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IWaPeriod;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.period.AggPeriodSchemeVO;
import nc.vo.wa.period.PeriodSchemeVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.period.WaClassViewVO;
import nc.vo.wa.periodsate.WaPeriodstateVO;
import nc.vo.wabm.util.WaCacheUtils;

import org.apache.commons.lang.ArrayUtils;

/**
 * 薪资期间后台实现类
 * 
 * @author: liangxr
 * @param <T>
 * @date: 2009-11-11 上午10:21:23
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WaPeriodImpl implements IWaPeriod {
	private final String DOC_NAME = "waperiod";

	private SimpleDocServiceTemplate serviceTemplate;

	private SimpleDocServiceTemplate getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new SimpleDocServiceTemplate(DOC_NAME);
			serviceTemplate.setValidatorFactory(new PeriodValidatorFactory());
		}
		return serviceTemplate;
	}

	/**
	 * 对子表进行排序
	 * 
	 * @author liangxr on 2009-11-11
	 * @param vo
	 * @param isUpdate
	 * @return AggPeriodSchemeVO
	 * @throws BusinessException
	 */
	private AggPeriodSchemeVO sortWaPeriod(Object object) throws BusinessException {
		AggPeriodSchemeVO vo = (AggPeriodSchemeVO) object;
		// 对子表进行排序
		PeriodVO[] childVOs = (PeriodVO[]) vo.getChildrenVO();
		if (childVOs != null) {
			Arrays.sort(childVOs, new WaPeriodCompare());
		}
		vo.setChildrenVO(childVOs);

		return vo;
	}

	WaPeriodDAO dao = null;
	private WaPeriodDAO getPeriodDAO() {
		if (dao == null) {
			dao = new WaPeriodDAO();
		}
		return dao;
	}
	
	@Override
	public Map<String, String[]> queryWaClassYearAndPeriod(String[] pk_wa_classes) throws BusinessException {
		// if(pk_wa_classes==null||pk_wa_classes.length<1)return null;
		
		
		InSQLCreator inSQLCreator = new InSQLCreator();
	     
	     try
	     {
	      
	         Map<String, String[]> map = new HashMap<String, String[]>();
	 		String in_sql = "";
			if (pk_wa_classes != null && pk_wa_classes.length > 0) {
				in_sql = "  pk_wa_class in(" + inSQLCreator.getInSQL(pk_wa_classes)+")";
			}
			String[] cyears = getPeriodDAO().queryCyears(in_sql);
			String[] cperiods = getPeriodDAO().queryCperiods(in_sql);
			map.put("cyear", cyears);
			map.put("cperiod", cperiods);
			return map;
	         
	         
	        }
	        finally
	        {
	         inSQLCreator.clear();
	        }

		
	}
	
	/**
	 * 复制
	 * 
	 * @author liangxr on 2009-11-12
	 * @see nc.itf.hr.wa.IWaPeriod#copyWaPeriod(nc.vo.wa.period.AggPeriodSchemeVO)
	 */
	@Override
	public Object copyWaPeriod(AggPeriodSchemeVO vo) throws BusinessException {
		// 重置主表主键
		vo.getParentVO().setPrimaryKey(null);
		// 重置子表主键
		for (int i = 0; i < vo.getChildrenVO().length; i++) {
			((PeriodVO) vo.getChildrenVO()[i]).setPk_wa_period(null);
		}
		// 重置审计信息
		resetAudit(vo.getParentVO());

		return sortWaPeriod(getServiceTemplate().insert(vo));
	}

	/**
	 * 复制时重置审计信息
	 * 
	 * @author liangxr on 2009-11-12
	 * @param vo
	 */
	private void resetAudit(CircularlyAccessibleValueObject vo) {
		String user = InvocationInfoProxy.getInstance().getUserId();
		UFDateTime dt = new UFDateTime(new Date(TimeService.getInstance().getTime()));
		vo.setAttributeValue("creator", user);
		vo.setAttributeValue("creationtime", dt);
		vo.setAttributeValue("modifier", null);
		vo.setAttributeValue("modifiedtime", null);

	}

	/**
	 * 删除
	 * 
	 * @author liangxr on 2009-11-27
	 * @see nc.hr.frame.persistence.IHrAppModelService#delete(java.lang.Object)
	 */
	@Override
	public void delete(Object object) throws BusinessException {
		AggPeriodSchemeVO aggvo = (AggPeriodSchemeVO)object;
		String pk = aggvo.getParentVO().getPrimaryKey();
		getServiceTemplate().delete(object);
		CacheProxy.fireDataDeleted(PeriodSchemeVO.getDefaultTableName(), pk);
	}

	/**
	 * 新增
	 * 
	 * @author liangxr on 2009-11-27
	 * @see nc.hr.frame.persistence.IHrAppModelService#insert(java.lang.Object)
	 */
	@Override
	public Object insert(Object object) throws BusinessException {
		WaCacheUtils.synCache(PeriodSchemeVO.getDefaultTableName());
		return sortWaPeriod(getServiceTemplate().insert(object));
	}

	/**
	 * 修改
	 * 
	 * @author liangxr on 2009-11-27
	 * @see nc.hr.frame.persistence.IHrAppModelService#update(java.lang.Object)
	 */
	@Override
	public Object update(Object object) throws BusinessException {
		Object obj = getServiceTemplate().update(object,true);
		/**
		 * 查询关联该期间方案的所有薪资方案，扩展期间月
		 */
		AggPeriodSchemeVO vo = (AggPeriodSchemeVO) obj;
		WaPeriodQueryImpl impl = new WaPeriodQueryImpl();
		WaClassViewVO[] classes = impl.getWaClassByScheme(vo.getParentVO().getPrimaryKey(), null);
		if (classes != null) {
			for (int i = 0; i < classes.length; i++) {
				insertPeriodState(vo.getParentVO().getPrimaryKey(), classes[i]);
			}
		}
		WaCacheUtils.synCache(PeriodSchemeVO.getDefaultTableName());
		return sortWaPeriod(obj);
	}

	private void insertPeriodState(String pk_periodScheme, WaClassViewVO classvo) throws BusinessException {
		// 查询出符合条件的薪资期间
		WaPeriodDAO dao = new WaPeriodDAO();
		PeriodVO[] vos = dao.queryPeriodNotInClass(pk_periodScheme, classvo.getPk_wa_class());

		if (ArrayUtils.isEmpty(vos)) {
			return;
		}
		ArrayList<WaPeriodstateVO> statevos = new ArrayList<WaPeriodstateVO>();
		for (int index = 0; index < vos.length; index++) {
			PeriodVO periodVO = vos[index];
			WaPeriodstateVO tempVO = new WaPeriodstateVO();
			tempVO.setEnableflag(UFBoolean.FALSE);
			tempVO.setIsapproved(UFBoolean.FALSE);
			tempVO.setCheckflag(UFBoolean.FALSE);
			tempVO.setCaculateflag(UFBoolean.FALSE);
			tempVO.setPayoffflag(UFBoolean.FALSE);
			tempVO.setAccountmark(UFBoolean.FALSE);
			tempVO.setIsapporve(UFBoolean.TRUE);
			tempVO.setPk_group(classvo.getPk_group());
			tempVO.setPk_org(classvo.getPk_org());
			tempVO.setPk_wa_class(classvo.getPk_wa_class());
			tempVO.setPk_wa_period(periodVO.getPk_wa_period());
			tempVO.setStatus(VOStatus.NEW);
			statevos.add(tempVO);
		}
		// 保存期间状态
		WaPeriodstateVO[] vos2 = new WaPeriodstateVO[statevos.size()];
		getMDPersistenceService().saveBill(statevos.toArray(vos2));
	}

	/**
	 * 返回元数据持久化服务对象
	 */
	protected static IMDPersistenceService getMDPersistenceService() {
		return MDPersistenceService.lookupPersistenceService();
	}
	

	public void copyWaClassitem(WaClassVO vo, String cyear, String cperiod)
			throws DAOException {
		// 得到公司下薪资项目
		String condition = " pk_wa_class = '" + vo.getPk_wa_class()
				+ "' and cyear = '" + cyear + "' and cperiod = '" + cperiod
				+ "'";
		WaClassItemVO[] nextPeriodItemVOs = getPeriodDAO().retrieveByClause(
				WaClassItemVO.class, condition);
		// 对下一期的项目更新期间
		for (WaClassItemVO waClassItemVO : nextPeriodItemVOs) {
			waClassItemVO.setPk_wa_classitem(null);
			waClassItemVO.setCyear(vo.getCyear());
			waClassItemVO.setCperiod(vo.getCperiod());
		}
		// 插入下一期
		getPeriodDAO().insertVOArrayReturnVOArray(nextPeriodItemVOs);
	}
	

	public void deleteClassitem(String pk_wa_class) throws BusinessException {
		// 先删除原有的，再插入新的。因为类别主键在整个数据库中是唯一的。所以直接按照薪资方案主键进行删除
		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate(" delete from  wa_classitem   where pk_wa_class= '"
					+ pk_wa_class + "'");
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0083")/* @res "薪资方案的发放项目删除失败" */);
		}

	}

}
