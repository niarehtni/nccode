package nc.impl.ta.signcard;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.HrBatchService;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.FromWhereSQLUtils;
import nc.hr.utils.InSQLCreator;
import nc.impl.ta.signcard.validator.SignCardRegDeleteValidatorService;
import nc.impl.ta.signcard.validator.SignCardRegValidatorService;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.ISignCardRegisterManageMaintain;
import nc.itf.ta.ISignCardRegisterQueryMaintain;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.mddb.baseutil.MDDAOUtil;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.log.TaBusilogUtil;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.IMetaDataIDConst;
import nc.vo.ta.pub.TaNormalQueryUtils;
import nc.vo.ta.signcard.PsnJobSignVO;
import nc.vo.ta.signcard.SignCardBeyondTimeVO;
import nc.vo.ta.signcard.SignCardRegQueryParams;
import nc.vo.ta.signcard.SignCommonVO;
import nc.vo.ta.signcard.SignRegVO;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.uif2.LoginContext;
import nc.vo.vorg.AdminOrgVersionVO;
import nc.vo.vorg.DeptVersionVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class SignCardRegisterMaintainImpl implements ISignCardRegisterManageMaintain, ISignCardRegisterQueryMaintain {

	private HrBatchService serviceTemplate;

	public HrBatchService getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new HrBatchService(IMetaDataIDConst.SIGNCARD);
		}
		return serviceTemplate;
	}

	@Override
	public void deleteArrayData(SignRegVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return;
		SignCardRegDeleteValidatorService vservice = new SignCardRegDeleteValidatorService();
		vservice.validate(vos);
		// v65ע���±���˺�Ǽǵ���ά������
		// for(SignRegVO vo:vos){
		// PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(),
		// vo.getSigndate(), vo.getSigndate());
		// }
		getServiceTemplate().delete(vos);
		// ҵ����־
		TaBusilogUtil.writeSignCardDeleteBusiLog(vos);
	}

	@Override
	public void deleteData(SignRegVO vo) throws BusinessException {
		if (vo == null)
			return;
		deleteArrayData(new SignRegVO[] { vo });
	}

	/**
	 * �����������棨��һ�δ�У��ǩ��������
	 * 
	 * @param pk_org
	 * @param vos
	 * @return Ϊ�ձ�ʾ�ѳɹ�����
	 * @throws BusinessException
	 */
	@Override
	public SignCardBeyondTimeVO[] firstBatchInsert(String pk_org, SignRegVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		SignCardBeyondTimeVO[] beyondVOs = vldAndGetBydPrt(pk_org, vos);
		// ���У��ǩ��������ͨ��
		if (!ArrayUtils.isEmpty(beyondVOs))
			return beyondVOs;
		// У��ͨ����ֱ�ӱ���
		insertArrayData(vos);
		return null;
	}

	/**
	 * �����������棨�ڶ��Σ�������ѡ�ģ�����Ҫ��У�飩 ��ԭǩ����Ϣ����ȥ������Ҫ�����ǩ����Ϣ
	 * 
	 * @param pk_org
	 * @param vos
	 * @param unSelectedBeyonds
	 *            δ��ѡ�е�ǩ��������Ϣ
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public SignRegVO[] secondBatchInsert(String pk_org, SignRegVO[] vos, SignCardBeyondTimeVO[] unSelectedBeyonds)
			throws BusinessException {
		SignRegVO[] saveVOs = filterSaveVOsAfterSelected(SignRegVO.class, pk_org, vos, unSelectedBeyonds);
		return insertArrayData(saveVOs);
	}

	/**
	 * ����ǩ��������õ���Ҫ�����VO����
	 * 
	 * @param <T>
	 * @param pk_org
	 * @param vos
	 * @param unSelectedBeyonds
	 * @return
	 * @throws BusinessException
	 */
	public <T extends SignCommonVO> T[] filterSaveVOsAfterSelected(Class<T> clz, String pk_org, T[] vos,
			SignCardBeyondTimeVO[] unSelectedBeyonds) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		// ���û����Ҫȥ�����򱣴�����
		if (ArrayUtils.isEmpty(unSelectedBeyonds))
			return vos;
		// ��ѯ���ڹ���
		TimeRuleVO timeRuleVo = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		// ȡ�����ڼ���Ϣ
		Map<String, UFLiteralDate[]> periodMap = getPeriodInfoMap(pk_org);
		SignCardBeyondTimeVO tmpBeyondVO = new SignCardBeyondTimeVO();
		List<T> saveResult = new ArrayList<T>();
		for (T vo : vos) {
			if (vo.getSigndate() == null) {
				UFLiteralDate signDate = BillMethods.calculateDateFromDateTime(vo.getPk_psnjob(), vo.getSigntime(),
						timeRuleVo.getTimeZoneMap());
				vo.setSigndate(signDate);
			}
			tmpBeyondVO.setPk_psndoc(vo.getPk_psndoc());
			tmpBeyondVO.setPeriod(getPeriodString(vo.getSigndate(), periodMap));
			// �������Ҫ������б��д����򲻼������������
			if (ArrayUtils.indexOf(unSelectedBeyonds, tmpBeyondVO) >= 0)
				continue;
			saveResult.add(vo);
		}
		return CollectionUtils.isEmpty(saveResult) ? null : CommonUtils.toArray(clz, saveResult);
	}

	/**
	 * У�鿼�ڹ����ж����ǩ����������
	 * 
	 * @param vos
	 * @return �����涨ǩ���������Ƶ�ǩ��������Ϣ
	 */
	@Override
	public SignCardBeyondTimeVO[] vldAndGetBydPrt(String pk_org, SignCommonVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		// У�鿼���ڼ�
		new SignCardRegDeleteValidatorService().validate(vos);
		return vldAndGetBydPrtA(pk_org, vos);

	}

	public SignCardBeyondTimeVO[] vldAndGetBydPrtA(String pk_org, SignCommonVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		// ��ѯ���ڹ���
		TimeRuleVO timeRuleVo = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		// ������ڹ������ǩ����������<=0�����ж��Ƿ񳬹����ƣ���Ϊ���ڹ���ò���ֵĬ��Ϊ0
		if (timeRuleVo == null || timeRuleVo.getSigncounts() <= 0)
			return null;

		// ǩ����¼ (�����Ѵ��ڵ�ǩ������¼���ǩ����¼vos)
		ArrayList<SignCommonVO> curVOs = new ArrayList<SignCommonVO>();
		// �Ѵ���ǩ����¼
		ArrayList<SignRegVO> existVOs = new ArrayList<SignRegVO>();

		UFLiteralDate minDate = null;// ����ǩ����¼vos����С����
		UFLiteralDate maxDate = null;// ����ǩ����¼vos���������

		// ��Ա��ϢMap Key: ��Ա���� Value: ������¼����
		Map<String, String> psndocMap = new HashMap<String, String>();

		// �Ż���ѯ��ְ��֯,һ����ȫ�����
		InSQLCreator isc = new InSQLCreator();
		Map<String, PsnJobVO> psnjobmap = new HashMap<String, PsnJobVO>();
		try {
			String pk_psnjobs = isc.getInSQL(vos, SignCommonVO.PK_PSNJOB);
			String cond = PsnJobVO.PK_PSNJOB + " in (" + pk_psnjobs + ") ";
			PsnJobVO[] psnjobvos = CommonUtils.retrieveByClause(PsnJobVO.class, cond);
			psnjobmap = CommonUtils.toMap(PsnJobVO.PK_PSNJOB, psnjobvos);
		} finally {
			isc.clear();
		}

		for (SignCommonVO vo : vos) {
			psndocMap.put(vo.getPk_psndoc(), vo.getPk_psnjob());
			String pk_psnjoborg = null;
			if (MapUtils.isNotEmpty(psnjobmap) && psnjobmap.get(vo.getPk_psnjob()) != null)
				pk_psnjoborg = psnjobmap.get(vo.getPk_psnjob()).getPk_org();
			UFLiteralDate signDate = BillMethods.calculateDateFromDateTimeByJoborg(pk_psnjoborg, vo.getSigntime(),
					timeRuleVo.getTimeZoneMap());

			vo.setSigndate(signDate);
			if (minDate == null || signDate.compareTo(minDate) < 0)
				minDate = signDate;
			if (maxDate == null || signDate.compareTo(maxDate) > 0)
				maxDate = signDate;
			curVOs.add(vo);
		}
		// ��ȡmaxDate��minDate֮�������漰���ڼ�
		IPeriodQueryService periodService = NCLocator.getInstance().lookup(IPeriodQueryService.class);
		PeriodVO[] periods = periodService.queryPeriodsByDateScope(pk_org, minDate, maxDate);
		if (ArrayUtils.isEmpty(periods))
			return null;
		Map<String, UFLiteralDate[]> periodMap = new HashMap<String, UFLiteralDate[]>();
		PeriodVO beginPeriod = null;// ��Χ�ڿ�ʼ�ڼ�
		PeriodVO endPeriod = null;// ��Χ������ڼ�
		for (PeriodVO period : periods) {
			periodMap.put(period.getTimeyear() + "-" + period.getTimemonth(),
					new UFLiteralDate[] { period.getBegindate(), period.getEnddate() });
			if (beginPeriod == null || period.getBegindate().compareTo(beginPeriod.getBegindate()) < 0)
				beginPeriod = period;
			if (endPeriod == null || period.getEnddate().compareTo(endPeriod.getEnddate()) > 0)
				endPeriod = period;
		}
		existVOs = (ArrayList<SignRegVO>) getExistedRegVOsByPsndoc(pk_org, psndocMap.keySet().toArray(new String[0]),
				beginPeriod, endPeriod);
		if (existVOs != null)
			curVOs.addAll(existVOs);

		// ������ǩ������ת����ͳ�Ƴ�ǩ��������Ϣ
		SignCardBeyondTimeVO[] beyondVOs = new SignCardBeyondTimeVO[0];
		for (SignCommonVO curVO : curVOs) {
			SignCardBeyondTimeVO beyondVO = new SignCardBeyondTimeVO();
			beyondVO.setPk_psndoc(curVO.getPk_psndoc());
			beyondVO.setSigncounts(1);
			beyondVO.setPeriod(getPeriodString(curVO.getSigndate(), periodMap));
			beyondVO.setPk_org_v(curVO.getPk_org_v());
			beyondVO.setPk_dept_v(curVO.getPk_dept_v());
			beyondVO.setPk_org(curVO.getPk_org());
			int index = ArrayUtils.indexOf(beyondVOs, beyondVO);
			// ��������ڴ�VO
			if (index < 0) {
				beyondVOs = (SignCardBeyondTimeVO[]) ArrayUtils.add(beyondVOs, beyondVO);
				continue;
			}
			// ����Ѵ�����ǩ��������1
			beyondVOs[index].setSigncounts(beyondVOs[index].getSigncounts() + 1);
		}

		// ����ǩ�����������涨ǩ��������
		int maxSignCount = timeRuleVo.getSigncounts();
		List<SignCardBeyondTimeVO> returnVOs = new ArrayList<SignCardBeyondTimeVO>();
		for (SignCardBeyondTimeVO beyondVO : beyondVOs) {
			if (beyondVO.getSigncounts() <= maxSignCount)
				continue;
			beyondVO.setPk_psnjob(psndocMap.get(beyondVO.getPk_psndoc()));
			returnVOs.add(beyondVO);
		}
		return CollectionUtils.isEmpty(returnVOs) ? null : returnVOs.toArray(new SignCardBeyondTimeVO[0]);
	}

	/**
	 * ������֯��������Ա��Ϣ����ȡ�Ѵ��ڵ�ǩ����Ϣ
	 * 
	 * @param pk_psndocs
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	// @SuppressWarnings("unchecked")
	// private SignRegVO[] getExistedRegVOsByPsndoc(String pk_org, String[]
	// pk_psndocs) throws BusinessException{
	// InSQLCreator isc = new InSQLCreator();
	// Collection<SignRegVO> allVOs = null;
	// try {
	// String inSql = isc.getInSQL(pk_psndocs);
	// String cond = " pk_org='" + pk_org + "' and pk_psndoc in (" + inSql
	// +") ";
	// allVOs = (Collection<SignRegVO>)new
	// BaseDAO().retrieveByClause(SignRegVO.class, cond);
	// } finally {
	// isc.clear();
	// }
	// return CollectionUtils.isEmpty(allVOs) ? null : allVOs.toArray(new
	// SignRegVO[0]);
	// }

	@SuppressWarnings("unchecked")
	private Collection<SignRegVO> getExistedRegVOsByPsndoc(String pk_org, String[] pk_psndocs, PeriodVO beginPeriod,
			PeriodVO endPeriod) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		Collection<SignRegVO> allVOs = null;
		try {
			String inSql = isc.getInSQL(pk_psndocs);
			String cond = " pk_org='" + pk_org + "' and pk_psndoc in (" + inSql + ") ";
			// �����ڼ䷶Χ����
			String periodCond = MessageFormat.format(" and {0} >= {1} and {0} <={2} ", SignRegVO.SIGNDATE, "'"
					+ beginPeriod.getBegindate().toStdString() + "'", "'" + endPeriod.getEnddate().toStdString() + "'");
			cond += periodCond;
			allVOs = (Collection<SignRegVO>) new BaseDAO().retrieveByClause(SignRegVO.class, cond);
		} finally {
			isc.clear();
		}
		return CollectionUtils.isEmpty(allVOs) ? null : allVOs;
	}

	/**
	 * ������֯����ȡ���п����ڼ���Ϣ
	 * 
	 * @param pk_org
	 * @return Key: �ڼ��ʶ Value: [0]��ʼ���ڣ�[1]��������
	 * @throws BusinessException
	 */
	private Map<String, UFLiteralDate[]> getPeriodInfoMap(String pk_org) throws BusinessException {
		Map<String, UFLiteralDate[]> periodMap = new HashMap<String, UFLiteralDate[]>();
		IPeriodQueryService periodService = NCLocator.getInstance().lookup(IPeriodQueryService.class);
		// ��У��������ڼ䣬����һ�����ڿ����ڼ�
		String[] years = periodService.queryPeriodYearsByOrg(pk_org);
		if (ArrayUtils.isEmpty(years))
			return null;
		for (String year : years) {
			PeriodVO[] periods = periodService.queryByYear(pk_org, year);
			for (PeriodVO period : periods) {
				periodMap.put(period.getTimeyear() + "-" + period.getTimemonth(),
						new UFLiteralDate[] { period.getBegindate(), period.getEnddate() });
			}
		}
		return periodMap;
	}

	/**
	 * ȡָ�����ڵĿ����ڼ��ʶ
	 * 
	 * @param date
	 * @param periodMap
	 * @return
	 */
	private String getPeriodString(UFLiteralDate date, Map<String, UFLiteralDate[]> periodMap) {
		for (String key : periodMap.keySet()) {
			// ����ڿ����ڼ����ڷ�Χ�����������
			if (date.before(periodMap.get(key)[0]) || date.after(periodMap.get(key)[1]))
				continue;
			// �ڿ����ڼ����ڷ�Χ���򷵻ؿ����ڼ�����
			return key;
		}
		return null;
	}

	@Override
	public SignRegVO[] insertArrayData(SignRegVO[] vos) throws BusinessException {
		return insertData(vos, true);
	}

	@Override
	public SignRegVO[] insertData(SignRegVO[] vos, boolean needCalAndValidate) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		new SignCardRegValidatorService().validate(vos);
		if (needCalAndValidate) {
			BillMethods.processBeginEndDatePkJobOrgTimeZone(vos);
		}
		getServiceTemplate().insert(vos);
		TaBusilogUtil.writeSignCardAddBusiLog(vos);
		return vos;
	}

	@Override
	public SignRegVO insertData(SignRegVO vo) throws BusinessException {
		if (vo == null)
			return null;
		new SignCardRegValidatorService().validate(vo);
		setSignDate(vo);
		TaBusilogUtil.writeSignCardAddBusiLog(new SignRegVO[] { vo });
		return getServiceTemplate().insert(vo)[0];
	}

	/**
	 * ����ǩ��ʱ������ǩ������
	 * 
	 * @param vo
	 */
	private void setSignDate(SignRegVO vo) throws BusinessException {
		Map<String, TimeZone> timeZoneMap = NCLocator.getInstance().lookup(ITimeRuleQueryService.class)
				.queryTimeZoneMap(vo.getPk_org());
		vo.setSigndate(BillMethods.calculateDateFromDateTime(vo.getPk_psnjob(), vo.getSigntime(), timeZoneMap));
	}

	@Override
	public SignRegVO[] updateArrayData(SignRegVO[] vos) throws BusinessException {
		return null;
	}

	@Override
	public SignRegVO updateData(SignRegVO vo) throws BusinessException {
		if (vo == null)
			return null;
		BillMethods.processBeginEndDatePkJobOrgTimeZone(new SignRegVO[] { vo });
		new SignCardRegValidatorService().validate(vo);
		// v65ע���±���˺�Ǽǵ���ά������
		// PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(),
		// vo.getSigndate(), vo.getSigndate());
		setSignDate(vo);
		SignRegVO regvo = getServiceTemplate().update(true, vo)[0];
		return regvo;
	}

	@Override
	public SignRegVO queryByPk(String pk) throws BusinessException {
		return getServiceTemplate().queryByPk(SignRegVO.class, pk);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PsnJobSignVO[] queryPsnjobVOsByConditionAndDateArea(LoginContext context, FromWhereSQL fromWhereSQL,
			Object extraCond, String[] pks) throws BusinessException {
		if (extraCond != null && !(extraCond instanceof SignCardRegQueryParams))
			return null;
		SignCardRegQueryParams params = (SignCardRegQueryParams) extraCond;
		// ����Ĳ�ѯ�ֶ�
		String[] mainTableSelFields = new String[] { PsnJobSignVO.PK_PSNDOC, PsnJobSignVO.PK_ORG };

		// ����Ȩ�޴���
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		// �˴β�ѯ���漰�������ڷ�Χ
		IDateScope dateScope = TaNormalQueryUtils.getPeriodDateScope(context, SignRegVO.class,
				(SignCardRegQueryParams) extraCond);
		// ���Ӱ汾��ѯ�ֶ�
		fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, dateScope == null ? null
				: (dateScope.getEnddate() == null ? null : dateScope.getEnddate().toStdString()));

		String orgversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob." + PsnJobVO.PK_ORG_V
				+ FromWhereSQLUtils.getAttPathPostFix());
		String deptversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob." + PsnJobVO.PK_DEPT_V
				+ FromWhereSQLUtils.getAttPathPostFix());
		String[] extraFields = new String[] { " count(*) signcounts", " (max(tbm_signreg.pk_psnjob)) pk_psnjob",
				"(max(" + orgversionAlias + "." + AdminOrgVersionVO.PK_VID + ")) as " + TBMPsndocVO.PK_ORG_V,
				"(max(" + deptversionAlias + "." + DeptVersionVO.PK_VID + ")) as " + TBMPsndocVO.PK_DEPT_V };
		String extraCondition = " tbm_psndoc.pk_org=? ";
		if (!StringUtils.isBlank(params.getPk_signreason())) {
			extraCondition += " and tbm_signreg.signreason =? ";
		}
		if (params.getSignStatus() != null) {
			extraCondition += " and tbm_signreg.signstatus =? ";
		}
		// ȡ���ڼ䷶Χsql���
		extraCondition += " and " + getExtraCond(context, fromWhereSQL, dateScope);
		String[] extraJoins = new String[] { "join tbm_signreg tbm_signreg on tbm_signreg.pk_psndoc=tbm_psndoc.pk_psndoc" };
		String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, TBMPsndocVO.getDefaultTableName(),
				mainTableSelFields, extraFields, extraJoins, extraCondition, null);
		// �����������������˫���ظ����ݣ���Ϊ�е��˿��ڵ�����������Ӧ�ù��˵����ڽ�С�ģ�ȡ���ڽ���ĵ�һ�����ڵ���
		sql += "  and tbm_psndoc.enddate=(select max(enddate) from tbm_psndoc where tbm_signreg.pk_psndoc = tbm_psndoc.pk_psndoc "
				+ " and tbm_signreg.pk_org=tbm_psndoc.pk_org ) ";// ������pk_org����ɵ��䵽�����֯����֮ǰ�ڸ���֯��ǩ����¼�鲻����

		if (!ArrayUtils.isEmpty(pks)) {
			sql += " and tbm_signreg." + SignRegVO.PK_PSNDOC + " in " + MDDAOUtil.getInSql(pks);
		}
		sql += "  GROUP BY tbm_psndoc.pk_org,tbm_psndoc.pk_psndoc ";

		SQLParameter sqlParam = new SQLParameter();
		sqlParam.addParam(context.getPk_org());
		if (!StringUtils.isBlank(params.getPk_signreason())) {
			sqlParam.addParam(params.getPk_signreason());
		}
		if (params.getSignStatus() != null) {
			sqlParam.addParam(params.getSignStatus());
		}
		Collection<PsnJobSignVO> psnVos = (Collection<PsnJobSignVO>) new BaseDAO().executeQuery(sql, sqlParam,
				new BeanListProcessor(PsnJobSignVO.class));
		if (CollectionUtils.isEmpty(psnVos))
			return null;
		return psnVos.toArray(new PsnJobSignVO[0]);
	}

	/**
	 * ȡ���ڼ䷶Χ��sql���
	 * 
	 * @param context
	 * @param fromWhereSQL
	 * @param extraCond
	 * @return
	 * @throws BusinessException
	 */
	private String getExtraCond(LoginContext context, FromWhereSQL fromWhereSQL, Object extraCond)
			throws BusinessException {
		if (extraCond != null && !(extraCond instanceof SignCardRegQueryParams))
			return null;
		IDateScope dateScope = TaNormalQueryUtils.getPeriodDateScope(context, SignRegVO.class,
				(SignCardRegQueryParams) extraCond);
		return getExtraCond(context, fromWhereSQL, dateScope);
	}

	/**
	 * ȡ���ڼ䷶Χ��sql���
	 * 
	 * @param context
	 * @param fromWhereSQL
	 * @param extraCond
	 * @return
	 * @throws BusinessException
	 */
	private String getExtraCond(LoginContext context, FromWhereSQL fromWhereSQL, IDateScope dateScope)
			throws BusinessException {
		// ƴ��֯��sql
		String extraCondition = SignRegVO.getDefaultTableName() + "." + SignRegVO.PK_ORG + " = '" + context.getPk_org()
				+ "' ";
		// ȡ������
		String alias = SignRegVO.getDefaultTableName();
		// ���ڼ��ѯ
		String periodSql = TaNormalQueryUtils.getPeriodSql(SignRegVO.class, alias, dateScope);
		if (periodSql != null) {
			extraCondition += " and " + periodSql;
		}
		return extraCondition;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SignRegVO[] querySignRegVOsByPsnAndDateArea(LoginContext context, String pk_psndoc, Object extraCond)
			throws BusinessException {
		if (extraCond != null && !(extraCond instanceof SignCardRegQueryParams))
			return null;
		SignCardRegQueryParams params = (SignCardRegQueryParams) extraCond;
		String condition = " pk_psndoc = ? " + (params.getPk_signreason() == null ? "" : " and signreason = ? ")
				+ (params.getSignStatus() == null ? "" : " and signstatus = ? ");
		SQLParameter sqlParameter = new SQLParameter();
		sqlParameter.addParam(pk_psndoc);
		if (!StringUtils.isBlank(params.getPk_signreason()))
			sqlParameter.addParam(params.getPk_signreason());
		if (params.getSignStatus() != null)
			sqlParameter.addParam(params.getSignStatus());
		condition += " and " + getExtraCond(context, null, extraCond);
		Collection<SignRegVO> regvos = (Collection<SignRegVO>) new BaseDAO().retrieveByClause(SignRegVO.class,
				condition, SignRegVO.SIGNTIME, sqlParameter);
		if (CollectionUtils.isEmpty(regvos))
			return null;
		return regvos.toArray(new SignRegVO[0]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SignRegVO[] queryByCond(LoginContext context, FromWhereSQL fromWhereSQL, Object etraConds)
			throws BusinessException {
		// ƴ��֯��sql
		String etraCondsNext = SignRegVO.getDefaultTableName() + "." + SignRegVO.PK_ORG + " = '" + context.getPk_org()
				+ "' ";
		String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, SignRegVO.getDefaultTableName(), etraCondsNext,
				null);
		Collection<SignRegVO> aggvos = (Collection<SignRegVO>) new BaseDAO().executeQuery(sql, new BeanListProcessor(
				SignRegVO.class));
		if (CollectionUtils.isEmpty(aggvos))
			return null;
		return aggvos.toArray(new SignRegVO[0]);
	}

	@Override
	public SignCardBeyondTimeVO[] vldAndGetBydPrt4App(String pkOrg, SignCommonVO[] vos) throws BusinessException {
		return vldAndGetBydPrtA(pkOrg, vos);
	}

	@Override
	public String[] queryPKsByFromWhereSQL(LoginContext context, FromWhereSQL fromWhereSQL, Object etraConds)
			throws BusinessException {
		String cond = getSQLCondByFromWhereSQL(context, fromWhereSQL, etraConds);
		String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, SignRegVO.getDefaultTableName());

		// ssx added on 2020-07-25
		// �������îa�������Ӽ������I���ԃ���ޣ����T���ˆT��ԃ������
		int hasGlbdef8 = -1;
		IUAPQueryBS qry = NCLocator.getInstance().lookup(IUAPQueryBS.class);

		hasGlbdef8 = (int) qry.executeQuery(
				"select count(glbdef1) from HI_PSNDOC_GLBDEF8 where pk_psndoc = (select pk_psndoc from sm_user where cuserid = '"
						+ InvocationInfoProxy.getInstance().getUserId() + "')", new ColumnProcessor());

		String deptWherePart = "";
		if (hasGlbdef8 > 0) {
			deptWherePart = " and #DEPT_PK# in (select glbdef1 from HI_PSNDOC_GLBDEF8 where pk_psndoc = (select pk_psndoc from sm_user where cuserid = '"
					+ context.getPk_loginUser()
					+ "') and '"
					+ new UFLiteralDate().toString()
					+ "' between BEGINDATE and nvl(ENDDATE, '9999-12-31')) and (select count(pk_dept) from org_dept where pk_dept=#DEPT_PK# and isnull(HRCANCELED, 'N')='N') > 0 ";
		}

		List<String> result = excuteQueryPksBycond(
				(StringUtils.isEmpty(deptWherePart) ? cond : cond.replace("GROUP BY",
						(deptWherePart.replace("#DEPT_PK#", "T1.pk_dept") + " GROUP BY"))), alias);
		// end

		return CollectionUtils.isEmpty(result) ? null : (String[]) result.toArray(new String[0]);
	}

	public String getSQLCondByFromWhereSQL(LoginContext context, FromWhereSQL fromWhereSQL, Object extraCond)
			throws BusinessException {
		if (extraCond != null && !(extraCond instanceof SignCardRegQueryParams))
			return null;
		SignCardRegQueryParams params = (SignCardRegQueryParams) extraCond;
		// ����Ĳ�ѯ�ֶ�
		String[] mainTableSelFields = new String[] { PsnJobSignVO.PK_PSNDOC, PsnJobSignVO.PK_ORG };

		// ����Ȩ�޴���
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		// �˴β�ѯ���漰�������ڷ�Χ
		IDateScope dateScope = TaNormalQueryUtils.getPeriodDateScope(context, SignRegVO.class,
				(SignCardRegQueryParams) extraCond);
		// ���Ӱ汾��ѯ�ֶ�
		fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, dateScope == null ? null
				: (dateScope.getEnddate() == null ? null : dateScope.getEnddate().toStdString()));

		String orgversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob." + PsnJobVO.PK_ORG_V
				+ FromWhereSQLUtils.getAttPathPostFix());
		String deptversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob." + PsnJobVO.PK_DEPT_V
				+ FromWhereSQLUtils.getAttPathPostFix());
		String[] extraFields = new String[] { " count(*) signcounts", " (max(tbm_signreg.pk_psnjob)) pk_psnjob",
				"(max(" + orgversionAlias + "." + AdminOrgVersionVO.PK_VID + ")) as " + TBMPsndocVO.PK_ORG_V,
				"(max(" + deptversionAlias + "." + DeptVersionVO.PK_VID + ")) as " + TBMPsndocVO.PK_DEPT_V };
		String extraCondition = " tbm_psndoc.pk_org= '" + context.getPk_org() + "' ";
		if (!StringUtils.isBlank(params.getPk_signreason())) {
			extraCondition += " and tbm_signreg.signreason = '" + params.getPk_signreason() + "' ";
		}
		if (params.getSignStatus() != null) {
			extraCondition += " and tbm_signreg.signstatus = '" + params.getSignStatus() + "' ";
		}
		// ȡ���ڼ䷶Χsql���
		extraCondition += " and " + getExtraCond(context, fromWhereSQL, dateScope);
		String[] extraJoins = new String[] { "join tbm_signreg tbm_signreg on tbm_signreg.pk_psndoc=tbm_psndoc.pk_psndoc" };
		String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, TBMPsndocVO.getDefaultTableName(),
				mainTableSelFields, extraFields, extraJoins, extraCondition, null);
		// �����������������˫���ظ����ݣ���Ϊ�е��˿��ڵ�����������Ӧ�ù��˵����ڽ�С�ģ�ȡ���ڽ���ĵ�һ�����ڵ���
		sql += "  and tbm_psndoc.enddate=(select max(enddate) from tbm_psndoc where tbm_signreg.pk_psndoc = tbm_psndoc.pk_psndoc "
				+ " and tbm_signreg.pk_org=tbm_psndoc.pk_org ) ";// ������pk_org����ɵ��䵽�����֯����֮ǰ�ڸ���֯��ǩ����¼�鲻����
		sql += "  GROUP BY tbm_psndoc.pk_org,tbm_psndoc.pk_psndoc ";
		return sql;
	}

	private List<String> excuteQueryPksBycond(String cond, String alias) throws BusinessException {
		String sql = "";
		if (StringUtils.isEmpty(cond))
			return null;
		sql = "select a." + PsnJobSignVO.PK_PSNDOC + " from ( " + cond + " ) a";
		List<String> result = (List) new BaseDAO().executeQuery(sql, new ColumnListProcessor());
		return result;
	}

	@Override
	public Object[] queryByPks(LoginContext context, FromWhereSQL fromWhereSQL, Object extraConds, String[] pks)
			throws BusinessException {
		return queryPsnjobVOsByConditionAndDateArea(context, fromWhereSQL, extraConds, pks);
	}

}
