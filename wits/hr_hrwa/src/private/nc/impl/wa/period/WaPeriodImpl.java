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
 * н���ڼ��̨ʵ����
 * 
 * @author: liangxr
 * @param <T>
 * @date: 2009-11-11 ����10:21:23
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
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
	 * ���ӱ��������
	 * 
	 * @author liangxr on 2009-11-11
	 * @param vo
	 * @param isUpdate
	 * @return AggPeriodSchemeVO
	 * @throws BusinessException
	 */
	private AggPeriodSchemeVO sortWaPeriod(Object object) throws BusinessException {
		AggPeriodSchemeVO vo = (AggPeriodSchemeVO) object;
		// ���ӱ��������
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
	 * ����
	 * 
	 * @author liangxr on 2009-11-12
	 * @see nc.itf.hr.wa.IWaPeriod#copyWaPeriod(nc.vo.wa.period.AggPeriodSchemeVO)
	 */
	@Override
	public Object copyWaPeriod(AggPeriodSchemeVO vo) throws BusinessException {
		// ������������
		vo.getParentVO().setPrimaryKey(null);
		// �����ӱ�����
		for (int i = 0; i < vo.getChildrenVO().length; i++) {
			((PeriodVO) vo.getChildrenVO()[i]).setPk_wa_period(null);
		}
		// ���������Ϣ
		resetAudit(vo.getParentVO());

		return sortWaPeriod(getServiceTemplate().insert(vo));
	}

	/**
	 * ����ʱ���������Ϣ
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
	 * ɾ��
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
	 * ����
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
	 * �޸�
	 * 
	 * @author liangxr on 2009-11-27
	 * @see nc.hr.frame.persistence.IHrAppModelService#update(java.lang.Object)
	 */
	@Override
	public Object update(Object object) throws BusinessException {
		Object obj = getServiceTemplate().update(object,true);
		/**
		 * ��ѯ�������ڼ䷽��������н�ʷ�������չ�ڼ���
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
		// ��ѯ������������н���ڼ�
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
		// �����ڼ�״̬
		WaPeriodstateVO[] vos2 = new WaPeriodstateVO[statevos.size()];
		getMDPersistenceService().saveBill(statevos.toArray(vos2));
	}

	/**
	 * ����Ԫ���ݳ־û��������
	 */
	protected static IMDPersistenceService getMDPersistenceService() {
		return MDPersistenceService.lookupPersistenceService();
	}
	

	public void copyWaClassitem(WaClassVO vo, String cyear, String cperiod)
			throws DAOException {
		// �õ���˾��н����Ŀ
		String condition = " pk_wa_class = '" + vo.getPk_wa_class()
				+ "' and cyear = '" + cyear + "' and cperiod = '" + cperiod
				+ "'";
		WaClassItemVO[] nextPeriodItemVOs = getPeriodDAO().retrieveByClause(
				WaClassItemVO.class, condition);
		// ����һ�ڵ���Ŀ�����ڼ�
		for (WaClassItemVO waClassItemVO : nextPeriodItemVOs) {
			waClassItemVO.setPk_wa_classitem(null);
			waClassItemVO.setCyear(vo.getCyear());
			waClassItemVO.setCperiod(vo.getCperiod());
		}
		// ������һ��
		getPeriodDAO().insertVOArrayReturnVOArray(nextPeriodItemVOs);
	}
	

	public void deleteClassitem(String pk_wa_class) throws BusinessException {
		// ��ɾ��ԭ�еģ��ٲ����µġ���Ϊ����������������ݿ�����Ψһ�ġ�����ֱ�Ӱ���н�ʷ�����������ɾ��
		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate(" delete from  wa_classitem   where pk_wa_class= '"
					+ pk_wa_class + "'");
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0083")/* @res "н�ʷ����ķ�����Ŀɾ��ʧ��" */);
		}

	}

}
