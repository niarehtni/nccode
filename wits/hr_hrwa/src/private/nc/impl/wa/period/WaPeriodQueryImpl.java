package nc.impl.wa.period;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import nc.bd.accperiod.AccperiodmonthAccessor;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IWaPeriodQuery;
import nc.jdbc.framework.SQLParameter;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.bd.period.AccperiodVO;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.hr.pub.FormatVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.uif2.LoginContext;
import nc.vo.util.VisibleUtil;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.period.AccPeriodVO;
import nc.vo.wa.period.AggPeriodSchemeVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.period.WaClassViewVO;
import nc.vo.wa.period.WaPeriodConstant;
import nc.vo.wa.periodsate.WaPeriodstateVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 薪资期间查询接口实现类
 *
 * @author: liangxr
 * @date: 2009-11-12 下午01:54:26
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WaPeriodQueryImpl implements IWaPeriodQuery {

	WaPeriodDAO dao = null;

	private static IMDPersistenceQueryService getMDQueryService() {
		return MDPersistenceService.lookupPersistenceQueryService();
	}

	private WaPeriodDAO getPeriodDAO() {
		if (dao == null) {
			dao = new WaPeriodDAO();
		}
		return dao;
	}

	/**
	 * 查询当前用户可见的薪资期间方案
	 *
	 * @author liangxr on 2009-11-27
	 * @see nc.hr.frame.persistence.IHrAppModelQueryService#queryByDataVisibilitySetting(nc.vo.uif2.LoginContext,
	 *      java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context, String condition, String beanId)
			throws BusinessException {
		// 获得管控模式中的可见性条件
		String visibleWhere = null;
		visibleWhere = VisibleUtil.getVisibleCondition(context, AggPeriodSchemeVO.class);
		if (!StringUtil.isEmptyWithTrim(condition)) {
			visibleWhere += "and(" + condition + ")";
		}
		visibleWhere += " order by pk_org,code";
		Collection<AggPeriodSchemeVO> result = getMDQueryService().queryBillOfVOByCond(
				AggPeriodSchemeVO.class, visibleWhere, false);
		// 对薪资规则表字表根据序号进行排序
		AggPeriodSchemeVO[] billVOs = (result.toArray(new AggPeriodSchemeVO[0]));
		PeriodVO[] childVOs = null;

		for (int i = 0; i < billVOs.length; i++) {
			childVOs = (PeriodVO[]) billVOs[i].getChildrenVO();
			if (childVOs != null) {
				Arrays.sort(childVOs, new WaPeriodCompare());
			}

			billVOs[i].setChildrenVO(childVOs);
		}
		return billVOs;

	}

	/**
	 * 获取一个薪资期间方案的所有薪资期间
	 *
	 * @author liangxr on 2009-11-12
	 * @see nc.itf.hr.wa.IWaPeriodQuery#getPeriodsByCheme(java.lang.String)
	 */
	@Override
	public PeriodVO[] getPeriodsByScheme(String periodschemePK) throws BusinessException {
		PeriodVO[] vos = null;
		vos = getPeriodDAO().queryBySchemePK(periodschemePK);

		return vos;
	}

	/**
	 * 根据 薪资方案ID与薪资期间curPeriod 查询该方案curPeriod以前的薪资期间
	 *
	 * @author liangxr on 2009-11-13
	 * @see nc.itf.hr.wa.IWaPeriodQuery#getPeriodsByChemeAndStartDate(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public PeriodVO[] getPeriodsByChemeAndStartDate(String pk_periodscheme, String curPeriod)
			throws BusinessException {
		PeriodVO[] vos = null;
		vos = getPeriodDAO().queryByChemePKAndStartDate(pk_periodscheme, curPeriod);

		return vos;
	}

	/**
	 * 根据 薪资方案ID与薪资期间curPeriod 查询该方案curPeriod以前的薪资期间
	 *
	 * @author liangxr on 2009-11-13
	 * @see nc.itf.hr.wa.IWaPeriodQuery#getPeriodsByChemeAndStartDate(java.lang.String,
	 *      java.lang.String)
	 */

	public PeriodVO[] getPeriodsByChemeAndDate(String pk_periodscheme, String cyear,String cperiod)
			throws BusinessException {
		PeriodVO[] vos = null;
		vos = getPeriodDAO().queryByChemePKAndDate(pk_periodscheme, cyear, cperiod);

		return vos;
	}

	/**
	 * 获取curPeriod期间后的所有会计期间
	 *
	 * @author liangxr on 2009-11-13
	 * @see nc.itf.hr.wa.IWaPeriodQuery#getAllAccPeriodAfter(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AccPeriodVO[] getAllAccPeriodAfter(String curPeriod) throws BusinessException {
		Vector<AccPeriodVO> v = new Vector<AccPeriodVO>();

		String year = null;
		String period = null;
		if (!StringUtil.isEmpty(curPeriod)) {
			year = curPeriod.substring(0, 4);
			period = curPeriod.substring(4);
		}
		//由于在取基准期间数据时，平台提供的函数没有判断，所以判断一下
		AccperiodmonthVO monthvo = AccperiodmonthAccessor.getInstance().queryMinAccperiodmonth(null);
		if(monthvo==null){
			throw new BusinessException(ResHelper.getString("60130period","060130period0121")/*@res "系统没有预置基准期间！"*/);
		}

		//AccperiodVO[] accArrs = AccountCalendar.getInstance().getYearVOsOfCurrentScheme();

		AccperiodVO[] accArrs = getPeriodDAO().getAllAccPeriod();

		if (ArrayUtils.isEmpty(accArrs)) {
			return null;
		}
		for (AccperiodVO vo : accArrs) {
			if (year != null && vo.getPeriodyear().compareTo(year) < 0) {
				continue;
			}
			//				AccperiodmonthVO[] accperiodmonths = AccountCalendar.getInstanceByAccperiod(
			//						vo.getPk_accperiod()).getMonthVOsOfCurrentYear();
			AccperiodmonthVO[] accperiodmonths = getPeriodDAO()
					.getAccperiodmonthVO(vo.getPk_accperiod());

			for (AccperiodmonthVO mthVO : accperiodmonths) {
				if (year == null || vo.getPeriodyear().compareTo(year) > 0
						|| period == null
						|| mthVO.getAccperiodmth().compareTo(period) >= 0) {
					AccPeriodVO simpleVo = new AccPeriodVO();
					simpleVo.setPeriodyear(vo.getPeriodyear());
					simpleVo.setAccperiodmth(mthVO.getAccperiodmth());
					simpleVo.setBegindate(mthVO.getBegindate());
					simpleVo.setEnddate(mthVO.getEnddate());
					v.add(simpleVo);
				}
			}
		}
		AccPeriodVO[] periods = new AccPeriodVO[v.size()];
		v.copyInto(periods);
		return periods;
	}


	/**
	 * 获取curPeriod期间前的所有会计期间
	 *
	 * @author liangxr on 2009-11-13
	 * @see nc.itf.hr.wa.IWaPeriodQuery#getAllAccPeriodAfter(java.lang.String)
	 */
	@Override
	public AccPeriodVO[] getAllAccPeriodBef(String curPeriod) throws BusinessException {
		Vector<AccPeriodVO> v = new Vector<AccPeriodVO>();

		String year = null;
		String period = null;
		if (!StringUtil.isEmpty(curPeriod)) {
			year = curPeriod.substring(0, 4);
			period = curPeriod.substring(4);
		}

		AccperiodVO[] accArrs = getPeriodDAO().getAllAccPeriod();
		if (ArrayUtils.isEmpty(accArrs)) {
			return null;
		}
		for (AccperiodVO vo : accArrs) {
			if (year != null && vo.getPeriodyear().compareTo(year) > 0) {
				continue;
			}
			//				AccperiodmonthVO[] accperiodmonths = AccountCalendar.getInstanceByAccperiod(
			//						vo.getPk_accperiod()).getMonthVOsOfCurrentYear();
			AccperiodmonthVO[] accperiodmonths = getPeriodDAO()
					.getAccperiodmonthVO(vo.getPk_accperiod());
			for (AccperiodmonthVO mthVO : accperiodmonths) {
				if (year == null || vo.getPeriodyear().compareTo(year) < 0
						|| period == null
						|| mthVO.getAccperiodmth().compareTo(period) <= 0) {
					AccPeriodVO simpleVo = new AccPeriodVO();
					simpleVo.setPeriodyear(vo.getPeriodyear());
					simpleVo.setAccperiodmth(mthVO.getAccperiodmth());
					simpleVo.setBegindate(mthVO.getBegindate());
					simpleVo.setEnddate(mthVO.getEnddate());
					v.add(simpleVo);
				}
			}
		}
		AccPeriodVO[] periods = new AccPeriodVO[v.size()];
		v.copyInto(periods);
		return periods;
	}

	/**
	 * 根据期间方案 ,薪资年度,薪资期间 查询 薪资期间的开始日期与结束日期
	 *
	 * @author liangxr on 2009-11-13
	 * @see nc.itf.hr.wa.IWaPeriodQuery#queryBySchemeYP(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public PeriodVO queryBySchemeYP(String pk_periodscheme, String waYear, String waPeriod)
			throws BusinessException {
		PeriodVO waperiod = new PeriodVO();
		waperiod = getPeriodDAO().queryBySchemeYP(pk_periodscheme, waYear, waPeriod);
		return waperiod;
	}

	/**
	 * 查询关联该期间的所有薪资方案
	 *
	 * @author liangxr on 2009-12-3
	 * @see nc.itf.hr.wa.IWaPeriodQuery#getWaClassByScheme(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public WaClassViewVO[] getWaClassByScheme(String periodChemmePK, String sqlWhere)
			throws BusinessException {
		WaClassViewVO[] result = getPeriodDAO().queryWaClassByCheme(periodChemmePK, sqlWhere);
		return result;
	}

	/**
	 * 查询薪资期间被引用次数
	 *
	 * @author liangxr on 2009-12-4
	 * @see nc.itf.hr.wa.IWaPeriodQuery#isPeriodRefed(java.lang.String)
	 */
	@Override
	public boolean isPeriodRefed(String pk_period) throws BusinessException {
		return getPeriodDAO().isPeriodRefs(pk_period);
	}

	/**
	 * 查询薪资期间方案是否被引用（已审核的薪资方案）
	 *
	 * @author suihang
	 * @see nc.itf.hr.wa.IWaPeriodQuery#isPeriodSchemeRef(java.lang.String)
	 */
	@Override
	public boolean isPeriodSchemeRefed(String pk_periodscheme) throws BusinessException {
		return getPeriodDAO().isPeriodSchemeRef(pk_periodscheme);
	}
	/**
	 * 获取自动生成功能所需查询的所有结果 1:查询curPeriod的以前的所有薪资期间 2:查询原有的所有薪资期间 3：根据期间方案
	 * ,薪资年度,薪资期间 查询 薪资期间的开始日期与结束日期 4:查询所有的会计期间
	 *
	 * @author liangxr on 2009-12-17
	 * @see nc.itf.hr.wa.IWaPeriodQuery#getAllForAutoCreate(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> getAllForAutoCreate(String pk_periodscheme, String curPeriod)
			throws BusinessException {
		Map<String, Object> map = new HashMap<String, Object>();
		PeriodVO[] oldPeriods = getPeriodsByScheme(pk_periodscheme);
		map.put(WaPeriodConstant.OLD_PERIOD, oldPeriods);
		if (curPeriod == null && oldPeriods != null) {
			curPeriod = oldPeriods[oldPeriods.length - 1].getCyear()
					+ oldPeriods[oldPeriods.length - 1].getCperiod();
		}
		map.put(WaPeriodConstant.ALL_ACC_PERIOD, getAllAccPeriodAfter(curPeriod));
		return map;
	}

	/**
	 * @author xuanlt on 2010-2-24
	 * @see nc.itf.hr.wa.IWaPeriodQuery#queryPeriodsByWaClass(java.lang.String)
	 */
	@Override
	public PeriodVO[] queryPeriodsByWaClass(String pk_wa_class) throws BusinessException {
		return getPeriodDAO().queryByWaClass(pk_wa_class);
	}

	@Override
	public PeriodVO[] queryPeriodsByWaClass(String pk_wa_class,boolean isEnabled) throws BusinessException{

		return getPeriodDAO().queryByWaClass(pk_wa_class, WaPeriodstateVO.ENABLEFLAG  + " = '"+ (isEnabled ? "Y":"N")+"'");
	}



	@Override
	public String queryAccPeriodVOForFormula(String pk_wa_class,
			String cyear, String cperiod) throws BusinessException {
		String sql = "select caccyear,caccperiod from wa_period " +
				"where pk_periodscheme = (select pk_periodscheme from wa_waclass where pk_wa_class = ?) " +
				"and cyear = ? and cperiod = ?";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_wa_class);
		param.addParam(cyear);
		param.addParam(cperiod);
		PeriodVO vo = getPeriodDAO().executeQueryVO(sql, param, PeriodVO.class);
		return vo.getCaccyear()+vo.getCaccperiod();
	}


	public AccPeriodVO[] getAccPeriods(String lastPeriod, AccPeriodVO[] accPeriods,PeriodVO[] oldItems,String curPeriod) throws BusinessException {

		// 过滤重复的期间
		Vector<AccPeriodVO> periods = new Vector<AccPeriodVO>();
		String accPeriod = null;
		for (int i = 0; i < accPeriods.length; i++) {
			accPeriod = accPeriods[i].getPeriodyear() + accPeriods[i].getAccperiodmth();
			// 删除已存在期间
			if (ArrayUtils.isEmpty(oldItems)
					&& accPeriod.compareTo(curPeriod) >= 0) {
				periods.addElement(accPeriods[i]);
				continue;
			}
            //oldItems存在为空的情况，所以需要处理，否则出现空指针错误
			String firstPeriod =ArrayUtils.isEmpty(oldItems)?"": oldItems[0].getCaccyear()
					+ oldItems[0].getCaccperiod();
			if (curPeriod != null && curPeriod.compareTo(firstPeriod) < 0) {
				// 指定的生成起始期间比已存在期间要早，需要验证已存在期间是否有引用
				IWaPeriodQuery periodService = NCLocator.getInstance().lookup(
						IWaPeriodQuery.class);
				String pks = FormatVO.formatArrayToString(oldItems,
						PeriodVO.PK_WA_PERIOD);
				if (periodService.isPeriodRefed(pks))
					throw new BusinessException(ResHelper.getString(
							"60130period", "060130period0096")/*
															 * @res
															 * "有薪资期间已经被引用，不能重新生成！"
															 */);
			}
			//lastPeriod可能为空，所以要处理
			lastPeriod=StringUtils.isEmpty(lastPeriod)?"":lastPeriod;
			if (accPeriod.compareTo(lastPeriod) > 0
					|| accPeriod.compareTo(firstPeriod) < 0) {
				periods.addElement(accPeriods[i]);
			}

		}
		accPeriods = new AccPeriodVO[periods.size()];
		periods.copyInto(accPeriods);

		return accPeriods;
	}
	

	@Override
	public PeriodVO queryNewWaPeriod(String pk_waclass)
			throws BusinessException {
		return getPeriodDAO().queryNewWaPeriod(pk_waclass);
	}
	
	
	@Override
	public PeriodVO queryNewWaPeriod4TaxGroup(String pk_taxgroup)
			throws BusinessException {
		return getPeriodDAO().queryNewWaPeriod4TaxGroup(pk_taxgroup);
	}
	

	@Override
	public PeriodVO queryWaPeriod(String pk_waclass, UFDate date)
			throws BusinessException {
		return getPeriodDAO().queryWaPeriod(pk_waclass, date);

	}
	
	/**
	 * 查看薪资方案在最新的业务期间是否有系统项目
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public boolean hasWaclassItem(WaClassVO vo) throws BusinessException {

		try {
			String sql = "select 1 from wa_classitem where pk_wa_class = ? and cyear = ? and cperiod = ?";
			SQLParameter para = new SQLParameter();
			para.addParam(vo.getPk_wa_class());
			para.addParam(vo.getCyear());
			para.addParam(vo.getCperiod());
			return getPeriodDAO().isValueExist(sql, para);
		} catch (DAOException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0086")/* @res "判断薪资方案[" */
					+ vo.getMultilangName()
					+ ResHelper.getString("60130waclass", "060130waclass0087")/*
																			 * @res
																			 * "]是否有薪资发放项目失败"
																			 */);
		}

	}

}