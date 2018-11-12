package nc.bs.hrsms.ta.sss.common;

import java.io.Serializable;
import java.util.Map;

import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.lang.StringUtils;

public class ShopTaAppContextUtil {

	// AppAttribute-指定人员的考勤档案
		public static final String APP_ATTRIBUTE_TBMPSNDOC = "APP_TBMPSNDOC";
		// AppAttribute-指定组织的当前考勤规则
		public static final String APP_ATTRIBUTE_TIMERULE = "APP_TIMERULE";
		// AppAttribute-指定组织的当前考勤期间
		public static final String APP_ATTRIBUTE_TBMPERIOD = "APP_TBMPERIOD";
		// AppAttribute-指定组织的所有考勤年度和考勤期间
		public static final String APP_ATTRIBUTE_TBMPERIODMAP = "APP_TBMPERIODMAP";
		// AppAttribute-当前HR组织
		public static final String APP_ATTRIBUTE_HRORG = "APP_TBMHRORG";

		/**
		 * 向ApplicationContext中添加考勤档案属性
		 * 
		 * @return
		 */
		public static void addTaAppContext(String pk_psndoc) {
			ApplicationContext appContext = getApplicationContext();
			// 人员基本档案主键
			SessionUtil.getPk_psndoc();
			//SessionUtil.getPsndocVO().getPk_hrorg();
			
			//String pk_psndoc = SessionUtil.getPk_psndoc();
			if (StringUtils.isEmpty(pk_psndoc)) {
				appContext.addAppAttribute(APP_ATTRIBUTE_TBMPSNDOC, null);
			}
			TBMPsndocVO tbmPsndocVO = TBMPsndocUtil.getTBMPsndoc(pk_psndoc, new UFDateTime());
			appContext.addAppAttribute(APP_ATTRIBUTE_TBMPSNDOC, tbmPsndocVO);

			/**
			 * HR组织主键.<br/>
			 * 1. 存在考勤档案,HR组织主键以考勤档案所属组织.<br/>
			 * 2.没有考勤档案,HR组织主键以主职任职所在部门的HR组织.
			 */
			String pk_hr_org = SessionUtil.getPsndocVO().getPk_hrorg();
			appContext.addAppAttribute(APP_ATTRIBUTE_HRORG, pk_hr_org);

			/* 存在考勤档案的情况 */
			if (!StringUtils.isEmpty(pk_hr_org)) {
				TimeRuleVO timeRuleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_hr_org);
				appContext.addAppAttribute(APP_ATTRIBUTE_TIMERULE, timeRuleVO);

				// 考勤期间
				PeriodVO tbmPeriodVO = TBMPeriodUtil.getLatestPeriodVO(pk_hr_org);
				appContext.addAppAttribute(APP_ATTRIBUTE_TBMPERIOD, tbmPeriodVO);
			} else {
				appContext.addAppAttribute(APP_ATTRIBUTE_TIMERULE, null);
				appContext.addAppAttribute(APP_ATTRIBUTE_TBMPERIOD, null);
			}
			// 所有考勤年度和考勤期间
			appContext.addAppAttribute(APP_ATTRIBUTE_TBMPERIODMAP, null);
		}
		
		/**
		 * 向ApplicationContext中添加考勤档案属性
		 * 
		 * 遇上一个方法的唯一区别是查询考勤档案的方法不同，这个方法主要针对离职或调动人员
		 * 
		 * @return
		 */
		public static void addTaAppForTransferContext(String pk_psndoc) {
			ApplicationContext appContext = getApplicationContext();
			// 人员基本档案主键
			SessionUtil.getPk_psndoc();
			//SessionUtil.getPsndocVO().getPk_hrorg();
			
			//String pk_psndoc = SessionUtil.getPk_psndoc();
			if (StringUtils.isEmpty(pk_psndoc)) {
				appContext.addAppAttribute(APP_ATTRIBUTE_TBMPSNDOC, null);
			}
			TBMPsndocVO tbmPsndocVO = TBMPsndocUtil.getTBMPsndoc(pk_psndoc, new UFDateTime());
			appContext.addAppAttribute(APP_ATTRIBUTE_TBMPSNDOC, tbmPsndocVO);

			/**
			 * HR组织主键.<br/>
			 * 1. 存在考勤档案,HR组织主键以考勤档案所属组织.<br/>
			 * 2.没有考勤档案,HR组织主键以主职任职所在部门的HR组织.
			 */
			String pk_hr_org = SessionUtil.getPsndocVO().getPk_hrorg();
			appContext.addAppAttribute(APP_ATTRIBUTE_HRORG, pk_hr_org);

			/* 存在考勤档案的情况 */
			if (!StringUtils.isEmpty(pk_hr_org)) {
				TimeRuleVO timeRuleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_hr_org);
				appContext.addAppAttribute(APP_ATTRIBUTE_TIMERULE, timeRuleVO);

				// 考勤期间
				PeriodVO tbmPeriodVO = TBMPeriodUtil.getLatestPeriodVO(pk_hr_org);
				appContext.addAppAttribute(APP_ATTRIBUTE_TBMPERIOD, tbmPeriodVO);
			} else {
				appContext.addAppAttribute(APP_ATTRIBUTE_TIMERULE, null);
				appContext.addAppAttribute(APP_ATTRIBUTE_TBMPERIOD, null);
			}
			// 所有考勤年度和考勤期间
			appContext.addAppAttribute(APP_ATTRIBUTE_TBMPERIODMAP, null);
		}


		public static void setTBMPeriodVOMap(Map<String, String[]> periodMap) {
			ApplicationContext appContext = getApplicationContext();
			appContext.addAppAttribute(APP_ATTRIBUTE_TBMPERIODMAP, (Serializable) periodMap);

		}

		@SuppressWarnings("unchecked")
		public static Map<String, String[]> getTBMPeriodVOMap() {
			return (Map<String, String[]>) getApplicationContext().getAppAttribute(APP_ATTRIBUTE_TBMPERIODMAP);
		}

		/**
		 * 从ApplicationContext中获得HR组织Pk
		 * 
		 * @return
		 */
		public static final String getHROrg() {
			return (String) getApplicationContext().getAppAttribute(APP_ATTRIBUTE_HRORG);
		}

		/**
		 * 从ApplicationContext中获得人员考勤档案的集团PK
		 * 
		 * @return
		 */
		public static final String getPk_tbm_group() {
			TBMPsndocVO tbmPsndocVO = getTBMPsndocVO();
			if (tbmPsndocVO == null) {
				return null;
			}
			return tbmPsndocVO.getPk_group();
		}

		/**
		 * 从ApplicationContext中获得人员考勤档案的组织Pk
		 * 
		 * @return
		 */
		public static final String getPk_tbm_org() {
			TBMPsndocVO tbmPsndocVO = getTBMPsndocVO();
			if (tbmPsndocVO == null) {
				return null;
			}
			return tbmPsndocVO.getPk_org();
		}

		/**
		 * 从ApplicationContext中获得人员考勤档案VO
		 * 
		 * @return
		 */

		public static final TBMPsndocVO getTBMPsndocVO() {
			TBMPsndocVO tbmPsndocVO = (TBMPsndocVO) getApplicationContext().getAppAttribute(APP_ATTRIBUTE_TBMPSNDOC);
			return tbmPsndocVO;
		}

		/**
		 * 从ApplicationContext中获得考勤规则VO
		 * 
		 * @return
		 */
		public static final TimeRuleVO getTimeRuleVO() {
			TimeRuleVO timeRuleVO = (TimeRuleVO) getApplicationContext().getAppAttribute(APP_ATTRIBUTE_TIMERULE);
			return timeRuleVO;
		}

		/**
		 * 从ApplicationContext中获得人员考勤档案的组织Pk
		 * 
		 * @return
		 */
		public static final PeriodVO getLatestPeriodVO() {
			PeriodVO tbmPeriodVO = (PeriodVO) getApplicationContext().getAppAttribute(APP_ATTRIBUTE_TBMPERIOD);
			return tbmPeriodVO;
		}

		/**
		 * 获得ApplicationContext
		 * 
		 * @return
		 */
		private static ApplicationContext getApplicationContext() {
			return AppLifeCycleContext.current().getApplicationContext();
		}
}
