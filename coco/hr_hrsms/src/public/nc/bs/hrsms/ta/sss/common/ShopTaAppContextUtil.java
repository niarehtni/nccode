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

	// AppAttribute-ָ����Ա�Ŀ��ڵ���
		public static final String APP_ATTRIBUTE_TBMPSNDOC = "APP_TBMPSNDOC";
		// AppAttribute-ָ����֯�ĵ�ǰ���ڹ���
		public static final String APP_ATTRIBUTE_TIMERULE = "APP_TIMERULE";
		// AppAttribute-ָ����֯�ĵ�ǰ�����ڼ�
		public static final String APP_ATTRIBUTE_TBMPERIOD = "APP_TBMPERIOD";
		// AppAttribute-ָ����֯�����п�����ȺͿ����ڼ�
		public static final String APP_ATTRIBUTE_TBMPERIODMAP = "APP_TBMPERIODMAP";
		// AppAttribute-��ǰHR��֯
		public static final String APP_ATTRIBUTE_HRORG = "APP_TBMHRORG";

		/**
		 * ��ApplicationContext����ӿ��ڵ�������
		 * 
		 * @return
		 */
		public static void addTaAppContext(String pk_psndoc) {
			ApplicationContext appContext = getApplicationContext();
			// ��Ա������������
			SessionUtil.getPk_psndoc();
			//SessionUtil.getPsndocVO().getPk_hrorg();
			
			//String pk_psndoc = SessionUtil.getPk_psndoc();
			if (StringUtils.isEmpty(pk_psndoc)) {
				appContext.addAppAttribute(APP_ATTRIBUTE_TBMPSNDOC, null);
			}
			TBMPsndocVO tbmPsndocVO = TBMPsndocUtil.getTBMPsndoc(pk_psndoc, new UFDateTime());
			appContext.addAppAttribute(APP_ATTRIBUTE_TBMPSNDOC, tbmPsndocVO);

			/**
			 * HR��֯����.<br/>
			 * 1. ���ڿ��ڵ���,HR��֯�����Կ��ڵ���������֯.<br/>
			 * 2.û�п��ڵ���,HR��֯��������ְ��ְ���ڲ��ŵ�HR��֯.
			 */
			String pk_hr_org = SessionUtil.getPsndocVO().getPk_hrorg();
			appContext.addAppAttribute(APP_ATTRIBUTE_HRORG, pk_hr_org);

			/* ���ڿ��ڵ�������� */
			if (!StringUtils.isEmpty(pk_hr_org)) {
				TimeRuleVO timeRuleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_hr_org);
				appContext.addAppAttribute(APP_ATTRIBUTE_TIMERULE, timeRuleVO);

				// �����ڼ�
				PeriodVO tbmPeriodVO = TBMPeriodUtil.getLatestPeriodVO(pk_hr_org);
				appContext.addAppAttribute(APP_ATTRIBUTE_TBMPERIOD, tbmPeriodVO);
			} else {
				appContext.addAppAttribute(APP_ATTRIBUTE_TIMERULE, null);
				appContext.addAppAttribute(APP_ATTRIBUTE_TBMPERIOD, null);
			}
			// ���п�����ȺͿ����ڼ�
			appContext.addAppAttribute(APP_ATTRIBUTE_TBMPERIODMAP, null);
		}
		
		/**
		 * ��ApplicationContext����ӿ��ڵ�������
		 * 
		 * ����һ��������Ψһ�����ǲ�ѯ���ڵ����ķ�����ͬ�����������Ҫ�����ְ�������Ա
		 * 
		 * @return
		 */
		public static void addTaAppForTransferContext(String pk_psndoc) {
			ApplicationContext appContext = getApplicationContext();
			// ��Ա������������
			SessionUtil.getPk_psndoc();
			//SessionUtil.getPsndocVO().getPk_hrorg();
			
			//String pk_psndoc = SessionUtil.getPk_psndoc();
			if (StringUtils.isEmpty(pk_psndoc)) {
				appContext.addAppAttribute(APP_ATTRIBUTE_TBMPSNDOC, null);
			}
			TBMPsndocVO tbmPsndocVO = TBMPsndocUtil.getTBMPsndoc(pk_psndoc, new UFDateTime());
			appContext.addAppAttribute(APP_ATTRIBUTE_TBMPSNDOC, tbmPsndocVO);

			/**
			 * HR��֯����.<br/>
			 * 1. ���ڿ��ڵ���,HR��֯�����Կ��ڵ���������֯.<br/>
			 * 2.û�п��ڵ���,HR��֯��������ְ��ְ���ڲ��ŵ�HR��֯.
			 */
			String pk_hr_org = SessionUtil.getPsndocVO().getPk_hrorg();
			appContext.addAppAttribute(APP_ATTRIBUTE_HRORG, pk_hr_org);

			/* ���ڿ��ڵ�������� */
			if (!StringUtils.isEmpty(pk_hr_org)) {
				TimeRuleVO timeRuleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_hr_org);
				appContext.addAppAttribute(APP_ATTRIBUTE_TIMERULE, timeRuleVO);

				// �����ڼ�
				PeriodVO tbmPeriodVO = TBMPeriodUtil.getLatestPeriodVO(pk_hr_org);
				appContext.addAppAttribute(APP_ATTRIBUTE_TBMPERIOD, tbmPeriodVO);
			} else {
				appContext.addAppAttribute(APP_ATTRIBUTE_TIMERULE, null);
				appContext.addAppAttribute(APP_ATTRIBUTE_TBMPERIOD, null);
			}
			// ���п�����ȺͿ����ڼ�
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
		 * ��ApplicationContext�л��HR��֯Pk
		 * 
		 * @return
		 */
		public static final String getHROrg() {
			return (String) getApplicationContext().getAppAttribute(APP_ATTRIBUTE_HRORG);
		}

		/**
		 * ��ApplicationContext�л����Ա���ڵ����ļ���PK
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
		 * ��ApplicationContext�л����Ա���ڵ�������֯Pk
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
		 * ��ApplicationContext�л����Ա���ڵ���VO
		 * 
		 * @return
		 */

		public static final TBMPsndocVO getTBMPsndocVO() {
			TBMPsndocVO tbmPsndocVO = (TBMPsndocVO) getApplicationContext().getAppAttribute(APP_ATTRIBUTE_TBMPSNDOC);
			return tbmPsndocVO;
		}

		/**
		 * ��ApplicationContext�л�ÿ��ڹ���VO
		 * 
		 * @return
		 */
		public static final TimeRuleVO getTimeRuleVO() {
			TimeRuleVO timeRuleVO = (TimeRuleVO) getApplicationContext().getAppAttribute(APP_ATTRIBUTE_TIMERULE);
			return timeRuleVO;
		}

		/**
		 * ��ApplicationContext�л����Ա���ڵ�������֯Pk
		 * 
		 * @return
		 */
		public static final PeriodVO getLatestPeriodVO() {
			PeriodVO tbmPeriodVO = (PeriodVO) getApplicationContext().getAppAttribute(APP_ATTRIBUTE_TBMPERIOD);
			return tbmPeriodVO;
		}

		/**
		 * ���ApplicationContext
		 * 
		 * @return
		 */
		private static ApplicationContext getApplicationContext() {
			return AppLifeCycleContext.current().getApplicationContext();
		}
}
