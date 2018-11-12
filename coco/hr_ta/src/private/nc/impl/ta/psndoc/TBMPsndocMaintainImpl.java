package nc.impl.ta.psndoc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import nc.bs.bd.baseservice.ArrayClassConvertUtil;
import nc.bs.bd.cache.CacheProxy;
import nc.bs.bd.pub.distribution.util.BDDistTokenUtil;
import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.businessevent.IEventType;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfo;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.execute.Executor;
import nc.bs.logging.Logger;
import nc.bs.uif2.IActionCode;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.hr.frame.persistence.HrBatchService;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.psncalendar.PsnCalendarDAO;
import nc.impl.ta.psndoc.listener.TeamPsnChangeEventListener;
import nc.itf.ta.IMonthStatManageService;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.IPsnCalendarManageService;
import nc.itf.ta.ITBMPsndocManageMaintain;
import nc.itf.ta.ITBMPsndocQueryMaintain;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.rbac.IDataPermissionPubService;
import nc.uap.cpb.baseservice.util.BDVersionValidationUtil;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.team.team01.entity.TeamItemVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.log.TaBusilogUtil;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psndoc.AssignCardDescriptor;
import nc.vo.ta.psndoc.TBMPsndocCommonValue;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.tempcard.TempCardVO;
import nc.vo.uif2.LoginContext;
import nc.vo.util.SqlWhereUtil;

public class TBMPsndocMaintainImpl implements ITBMPsndocQueryMaintain,
		ITBMPsndocManageMaintain {
	private BaseDAO baseDAO = new BaseDAO();
	private final String DOC_NAME = "tbmpsndoc";
	private HrBatchService serviceTemplate;

	@Override
	public TBMPsndocVO[] assignCardNo(TBMPsndocVO[] vos,
			AssignCardDescriptor acd) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		String head = acd.getPrefix() == null ? "" : acd.getPrefix().trim();
		String tail = acd.getPostfix() == null ? "" : acd.getPostfix().trim();
		Integer intStart = Integer.valueOf(acd.getMiddleBegin());
		Integer intEnd = Integer.valueOf(acd.getMiddleEnd());
		;
		Integer len = intEnd - intStart + 1;

		// ���ݿ��ŷ����������ȡ�ÿ��ڿ���
		List<String> idList = new ArrayList<String>();
		for (int i = 0; i < len.intValue(); i++) {
			String vary = String.valueOf(intStart + i);
			StringBuffer stVary = new StringBuffer();
			stVary.append(head);
			// ǰ�β�λ0
			stVary.append(StringUtils.leftPad(vary, acd.getMiddleBegin()
					.length(), "0"));
			stVary.append(tail);
			idList.add(stVary.toString());
		}
		// ��ѯ�Ѵ�����Щ���ŵĿ��ڵ�������ת��ΪMap
		String inSql = new InSQLCreator().getInSQL(idList
				.toArray(new String[0]));
		String cardCondSql = " (timecardid in (" + inSql
				+ ") or secondcardid in (" + inSql + ") )";
		Map<String, List<TBMPsndocVO>> existMap = new HashMap<String, List<TBMPsndocVO>>();
		TBMPsndocVO[] existPsndocs = CommonUtils.retrieveByClause(
				TBMPsndocVO.class, cardCondSql);
		for (int i = 0, j = ArrayUtils.getLength(existPsndocs); i < j; i++) {
			if (StringUtils.isNotEmpty(existPsndocs[i].getTimecardid())) {
				List<TBMPsndocVO> cardList = existMap.get(existPsndocs[i]
						.getTimecardid());
				if (cardList == null) {
					cardList = new ArrayList<TBMPsndocVO>();
					existMap.put(existPsndocs[i].getTimecardid(), cardList);
				}
				cardList.add(existPsndocs[i]);
			}
			if (StringUtils.isNotEmpty(existPsndocs[i].getSecondcardid())) {
				List<TBMPsndocVO> cardList = existMap.get(existPsndocs[i]
						.getSecondcardid());
				if (cardList == null) {
					cardList = new ArrayList<TBMPsndocVO>();
					existMap.put(existPsndocs[i].getSecondcardid(), cardList);
				}
				cardList.add(existPsndocs[i]);
			}
		}
		// ���ÿ��ڿ���
		int index = 0; // ���ڵ������
		for (String id : idList) {
			String oldID = vos[index].getTimecardid();
			vos[index].setTimecardid(id);
			List<TBMPsndocVO> existPsnList = existMap.get(id);
			if (isCardIDHaveSame(vos[index], existPsnList)) {
				vos[index].setTimecardid(oldID); // ��������ʱ�����Ż�ԭΪԭ����
				continue;
			}
			index++; // ����������ã������¸����ڵ���
		}
		new BaseDAO().updateVOArray(vos,
				new String[] { TBMPsndocVO.TIMECARDID });
		return vos;
	}

	/**
	 * �Ƿ�����ظ���
	 *
	 * @param vo
	 *            ��У��VO
	 * @param voList
	 *            ���д˿��ŵĿ��ڵ���
	 * @return
	 */
	private boolean isCardIDHaveSame(TBMPsndocVO vo, List<TBMPsndocVO> voList) {
		if (CollectionUtils.isEmpty(voList))
			return false;
		for (TBMPsndocVO curVO : voList) {
			// ������֯��ͬ�����ظ�
			if (!vo.getPk_org().equals(curVO.getPk_org()))
				continue;
			// ͬһ����������ͬ�����ظ�
			if (curVO.getPk_psndoc().equals(vo.getPk_psndoc())
					&& !vo.getTimecardid().equals(curVO.getSecondcardid()))
				continue;
			// ���������ڿ�ʼ����֮ǰ�Ĳ����ظ�
			if (curVO.getEnddate().before(vo.getBegindate()))
				continue;
			// ����Ķ���Ϊ�����ظ�
			return true;
		}
		return false;
	}

	/**
	 * �޸�ǰУ�鵱ǰ�޸ĵĿ��ڵ����Ƿ�����
	 *
	 * @param vo
	 * @throws BusinessException
	 */
	// @SuppressWarnings("unchecked")
	// private void checkIsLatestBeforeUpdate(TBMPsndocVO vo) throws
	// BusinessException {
	// if(vo==null)
	// return;
	// // ȡ�������µĿ��ڵ�����¼
	// String condition = TBMPsndocVO.PK_PSNDOC +
	// " = ? and "+TBMPsndocVO.PK_TBM_PSNDOC+" <> ? order by "+TBMPsndocVO.ENDDATE+" desc ";
	// SQLParameter param = new SQLParameter();
	// param.addParam(vo.getEnddate().toString());
	// param.addParam(vo.getPk_tbm_psndoc());
	// Collection<TBMPsndocVO> vos = (Collection<TBMPsndocVO>)new
	// BaseDAO().retrieveByClause(TBMPsndocVO.class, condition, param);
	// // û���������ڵ�������ʾ������¼һ�������µ�
	// if(CollectionUtils.isEmpty(vos))
	// return;
	// TBMPsndocVO lastVO = vos.toArray(new TBMPsndocVO[0])[0];
	// // �����ѯ���������һ����¼�Ľ��������ڵ�ǰ��¼��ʼ����֮ǰ����ʾ��ǰ��¼�������µĿ��ڵ�����¼
	// if(vo.getBegindate().after(lastVO.getEnddate()))
	// return;
	// throw new
	// BusinessException(MessageFormat.format("{0}�Ŀ��ڵ����������µģ��������޸Ľ������ڣ�",
	// getPsnName(vo.getPk_psndoc())));
	// }

	@Override
	public void batchUpdate(String pk_hrorg, TBMPsndocVO[] vos,
			HashMap<String, Object> batchEditValue) throws BusinessException {
		String[] selFields = getBatchEditVOs(vos, batchEditValue);
		// ��鿪ʼ���ڡ����������Ƿ���Ч
		/*
		 * for (TBMPsndocVO vo : vos) { String checkInf =
		 * checkTBMPsndocDate(vo); if (checkInf == null) continue; throw new
		 * BusinessException(MessageFormat.format(checkInf,
		 * CommonUtils.getPsnName(vo.getPk_psndoc()))); }
		 */

		// boolean flag = false;
		// for (int i = 0; i < selFields.length; i++) {
		// if (TBMPsndocVO.BEGINDATE.equalsIgnoreCase(selFields[i])) {
		// flag = true;
		// break;
		// }
		// }
		//
		// // ��鿪ʼ���ڷ�Χ
		// if (flag) {
		// String errMsg = checkTBMPsndocDate(pk_hrorg, vos);
		// if (errMsg != null && errMsg.length() > 10) {
		// throw new BusinessException(errMsg);
		// }
		// }
		// ��鿼���ڼ�
		NCLocator.getInstance().lookup(IPeriodQueryService.class)
				.checkBeforeUpdateTBMPsndoc(pk_hrorg, vos);
		// ���������±�
		NCLocator.getInstance().lookup(IMonthStatManageService.class)
				.processBeforeUpdateTBMPsndoc(pk_hrorg, vos);

		new BaseDAO().updateVOArray(vos, selFields);
	}

	@Override
	public TBMPsndocVO[] checkCardNo(TBMPsndocVO[] vos)
			throws BusinessException {
		return null;
	}

	/**
	 * У�����ڵĺϷ���
	 *
	 * @return String
	 */
	@Override
	public String checkTBMPsndocDate(TBMPsndocVO vo) {
		if (vo.getEnddate() == null
				|| vo.getEnddate().toString()
						.startsWith(TBMPsndocCommonValue.END_DATA_PRE))
			return null;
		if (vo.getBegindate() == null) {
			return ResHelper.getString("6017psndoc", "06017psndoc0125")
			/* @res "{0}�Ŀ��ڵ����Ŀ�ʼ���ڲ���Ϊ�գ�" */;
		} else if (vo.getBegindate().compareTo(vo.getEnddate()) >= 0) {
			return ResHelper.getString("6017psndoc", "06017psndoc0126")
			/* @res "{0}�Ŀ��ڵ����Ŀ�ʼ���ڲ�С�ڽ������ڣ�" */;
		}
		return null;
	}

	@Override
	public void delete(TBMPsndocVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return;
		String pk_org = vos[0].getPk_org();
		delete(pk_org, vos);
	}

	@Override
	public void delete(TBMPsndocVO vo) throws BusinessException {
		delete(vo.getPk_org(), vo);
	}

	/**
	 * ɾ�����ڵ����������Ϣ
	 *
	 * @param psndocVOs
	 * @throws BusinessException
	 */
	private void delete(String pk_org, TBMPsndocVO... psndocVOs)
			throws BusinessException {
		// ɾ�����ڵ���У��
		checkDeleteTBMPsndoc(psndocVOs);
		// ɾ������������Ϣ
		NCLocator
				.getInstance()
				.lookup(IPsnCalendarManageService.class)
				.processBeforeDeleteTBMPsnodc(
						pk_org,
						StringPiecer.getStrArray(psndocVOs,
								TBMPsndocVO.PK_TBM_PSNDOC));
		// ɾ����ʱ����Ϣ
		deleteTempCard(StringPiecer.getStrArrayDistinct(psndocVOs,
				TBMPsndocVO.PK_PSNDOC));
		// ���������±�
		NCLocator
				.getInstance()
				.lookup(IMonthStatManageService.class)
				.processBeforeDeleteTBMPsndoc(
						pk_org,
						StringPiecer.getStrArray(psndocVOs,
								TBMPsndocVO.PK_TBM_PSNDOC));
		// ɾ�����ڵ���
		batchDelete(psndocVOs);
		// ɾ��������Ա
		for (TBMPsndocVO psndocVO : psndocVOs) {
			new TBMPsndocChangeProcessor()
					.processAfterTBMPsndocDelete(psndocVO);
		}
	}

	/**
	 * ɾ�����ڵ��� �����ݿ���ɾ��
	 *
	 * @param psndocVOs
	 * @throws BusinessException
	 */
	private void batchDelete(TBMPsndocVO[] psndocVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(psndocVOs))
			return;
		// ǰ�¼�֪ͨ
		if (getServiceTemplate().isDispatchEvent()) {
			BusinessEvent beforeEvent = new BusinessEvent(DOC_NAME,
					IEventType.TYPE_DELETE_BEFORE, psndocVOs);
			EventDispatcher.fireEvent(beforeEvent);
		}
		// ����//20151205��Ա�������޸���Ա����֯������ɾ����������ס,���Բ��ù��������
		// BDPKLockUtil.lockSuperVO(psndocVOs);
		// �汾У�飨ʱ���У�飩
		BDVersionValidationUtil.validateSuperVO(psndocVOs);

		// �־û�
		MDPersistenceService.lookupPersistenceService().deleteBillFromDB(
				psndocVOs);

		// ���¼�֪ͨ
		if (getServiceTemplate().isDispatchEvent()) {
			BusinessEvent afterEvent = new BusinessEvent(DOC_NAME,
					IEventType.TYPE_DELETE_AFTER, psndocVOs);
			EventDispatcher.fireEvent(afterEvent);
		}
		// ���»���
		CacheProxy.fireDataDeletedBatch(TBMPsndocVO.getDefaultTableName(),
				StringPiecer.getStrArray(psndocVOs, TBMPsndocVO.PK_TBM_PSNDOC));
	}

	/**
	 * У�鿼�ڵ���ɾ��
	 *
	 * @param psndocVOs
	 */
	private void checkDeleteTBMPsndoc(TBMPsndocVO... psndocVOs)
			throws BusinessException {
		// �ж��Ƿ����µĿ��ڵ��������������µģ�����ɾ��
		boolean[] isLatest = isLatestPsndoc(psndocVOs);
		List<String> notLatestPKList = new ArrayList<String>();// ��¼�������¿��ڵ�����pk_psndoc
		for (int i = 0; i < psndocVOs.length; i++) {
			if (!isLatest[i])
				notLatestPKList.add(psndocVOs[i].getPrimaryKey());
		}
		if (notLatestPKList.size() > 0) {
			// ����������µĿ��ڵ�����������ɾ��
			throw new BusinessException(ResHelper.getString("6017psndoc",
					"06017psndoc0127"
					/* @res "{0}�Ŀ��ڵ����������µģ�������ɾ����" */,
					CommonUtils.getPsnNames(notLatestPKList
							.toArray(new String[0]))));
		}
		String[] pks = StringPiecer.getStrArray(psndocVOs,
				TBMPsndocVO.PK_TBM_PSNDOC);
		// У�鵥����Ϣ
		new TBMPsndocDAO().checkTBMPsndocUsed4Bill(pks);
		// Map<String, TBMPsndocVO> psndocVOMap =
		// CommonUtils.toMap(TBMPsndocVO.PK_TBM_PSNDOC, psndocVOs);
		// String[] pks = psndocVOMap.keySet().toArray(new String[0]);
		// ɾ��ʱ��У���Ƿ��Ű� 2012-09-11�޸�
		// Map<String, Boolean> arrangeMap =
		// NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).existsCalendar(pks);
		// Set<String> pk_tbmpsndocs = arrangeMap.keySet();
		// List<String> arrangedPKList = new
		// ArrayList<String>();//��¼�Ѿ��Ű�Ŀ��ڵ�����pk_psndoc
		// for(String pk_tbmpsndoc : pk_tbmpsndocs) {
		// Boolean arranged = arrangeMap.get(pk_tbmpsndoc);
		// if(arranged==null||!arranged.booleanValue())
		// continue;
		// TBMPsndocVO vo = psndocVOMap.get(pk_tbmpsndoc);
		// arrangedPKList.add(vo.getPk_psndoc());
		// }
		// //������Ѿ��Ű�ģ�����ɾ��
		// if(arrangedPKList.size()>0){
		// throw new
		// BusinessException(MessageFormat.format(ResHelper.getString("6017psndoc","06017psndoc0128")
		// /*@res "{0}�ڿ��ڵ���ʱ�䷶Χ���Ѿ����Ű��¼������ɾ�����ڵ���"*/,
		// CommonUtils.getPsnNames(arrangedPKList.toArray(new String[0]))));
		//
		// }
		// У���ڼ䣬��Ҫ��У��ɾ���Ŀ��ڵ����Ƿ������˷����ڼ�����
		NCLocator.getInstance().lookup(IPeriodQueryService.class)
				.checkBeforeDeleteTBMPsnodc(psndocVOs[0].getPk_org(), pks);
	}

	/**
	 * ɾ����ʱ����Ϣ
	 *
	 * @param pk_psndoc
	 */
	private void deleteTempCard(String... pk_psndocs) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		String condition = TempCardVO.PK_PSNDOC + " in ("
				+ isc.getInSQL(pk_psndocs) + ") ";
		new BaseDAO().deleteByClause(TempCardVO.class, condition);
	}

	private String[] getBatchEditVOs(TBMPsndocVO[] vos,
			HashMap<String, Object> batchEditValue) {
		if (MapUtils.isEmpty(batchEditValue))
			return null;
		String[] keys = batchEditValue.keySet().toArray(new String[0]);
		for (TBMPsndocVO vo : vos) {
			for (String key : keys) {
				// ����޸��˽��������ҿ��ڵ����������µģ��򲻴�����������
				if (TBMPsndocVO.ENDDATE.equals(key) && !vo.isLatest())
					continue;
				vo.setAttributeValue(key, batchEditValue.get(key));
			}
		}
		return keys;
	}

	private HrBatchService getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new HrBatchService(DOC_NAME);
		}
		return serviceTemplate;
	}

	@Override
	public TBMPsndocVO insert(PsndocVO[] psndocvos, TBMPsndocVO vos,
			PsnJobVO psnjobVO, boolean isUpdatePsnCalendar, boolean isNew)
			throws BusinessException {
		return insert(psndocvos, vos, psnjobVO, isUpdatePsnCalendar, true,
				true, isNew);
	}

	@Override
	public TBMPsndocVO insert(TBMPsndocVO vo, boolean isUpdatePsnCalendar)
			throws BusinessException {
		return insert(null, vo, null, isUpdatePsnCalendar, true, true, false);
	}

	@SuppressWarnings("rawtypes")
	protected TBMPsndocVO insert(PsndocVO[] psndocvos, TBMPsndocVO vo,
			PsnJobVO psnjobVO, boolean isUpdatePsnCalendar,
			boolean checkPeriod, boolean processMonthstat, boolean isNew)
			throws BusinessException {
		if (!isNew)
			check(vo);
		else {
			// У�����
			DefaultValidationService vService = new DefaultValidationService();
			// ���ڿ���У��
			TBMCardIDValidator idValidator = new TBMCardIDValidator();
			vService.addValidator(idValidator);

			vService.validate(vo);
		}
		// У�鿼���ڼ䣬��Ҫ��У�������Ŀ��ڵ�����
		// 1.��ʼ���ڲ���������֯�ĵ�һ�������ڼ��ǰ��
		// 2.���ڵ����ķ�Χ���������ѷ����ڼ�����
		if (checkPeriod)
			NCLocator.getInstance().lookup(IPeriodQueryService.class)
					.checkBeforeInsertTBMPsndoc(psndocvos, vo);
		if (StringUtils.isBlank(vo.getPk_region())) {
			String regionSql = null;
			if (psnjobVO != null && psnjobVO.getPk_org() != null)
				regionSql = " select pk_region from tbm_regionorg rog where rog.pk_org='"
						+ psnjobVO.getPk_org() + "';";
			else
				regionSql = " select pk_region from tbm_regionorg rog inner join hi_psnjob pjob on rog.pk_org = pjob.pk_org where pk_psnjob = '"
						+ vo.getPk_psnjob() + "'";
			BaseDAO baseDAO = new BaseDAO();
			List pkList = (List) baseDAO.executeQuery(regionSql,
					new ArrayListProcessor());
			String pk_region = null;
			if (!CollectionUtils.isEmpty(pkList)) {
				Object[] array = (Object[]) pkList.get(0);
				pk_region = (String) array[0];
			}
			vo.setPk_region(pk_region);
		}

		// BEGIN �ź�{21481} ���ڵ���������ʱ�򣬽�Ա��ά�����������㷽ʽ��specialrest����ֵ�������ڵ�����
		// 2018/8/16
		String specialrestSql = " select specialrest from bd_psndoc where pk_psndoc = '"
				+ vo.getPk_psndoc() + "' ";
		String specialrest = baseDAO.executeQuery(specialrestSql,
				new ColumnProcessor()) == null ? "" : baseDAO.executeQuery(
				specialrestSql, new ColumnProcessor()).toString();
		vo.setSpecialrest(specialrest);
		// END �ź�{21481} ���ڵ���������ʱ�򣬽�Ա��ά�����������㷽ʽ��specialrest����ֵ�������ڵ����� 2018/8/16

		TBMPsndocVO psndocVO = getServiceTemplate().insert(vo)[0];
		// ���»���
		CacheProxy.fireDataInserted(TBMPsndocVO.getDefaultTableName(),
				psndocVO.getPk_tbm_psndoc());
		//Ares.Tank �����ֶε�ֵ��ʧ,�����ȥ 2018-10-6 22:22:19
		//���Ʊ�׼ƽ̨bug
		if(null != psnjobVO){
			psndocVO.setPk_joborg(psnjobVO.getPk_org());
		}
		

		// ����������Ա
		// new TBMPsndocChangeProcessor().processAfterTBMPsndocInsert(vo);
		// ���¹�������
		if (isUpdatePsnCalendar) {
			NCLocator
					.getInstance()
					.lookup(IPsnCalendarManageService.class)
					.autoArrange(vo.getPk_org(), vo.getPk_psndoc(),
							new TBMPsndocVO[] { psndocVO });
			// new
			// nc.impl.ta.psncalendar.PsnCalendarServiceImpl().autoArrange(vo.getPk_org(),
			// vo.getPk_psndoc(),new TBMPsndocVO[]{psndocVO});
		}
		if (processMonthstat)
			NCLocator.getInstance().lookup(IMonthStatManageService.class)
					.processAfterInsertTBMPsndoc(vo);
		// new
		// nc.impl.ta.monthstat.MonthStatServiceImpl().processAfterInsertTBMPsndoc(vo);
		return setLatestFlag(new TBMPsndocVO[] { psndocVO })[0];
	}

	/**
	 * ȡ�õ�ְ����
	 *
	 * @param pk_psnjob
	 * @param busLitDate
	 *            ����ǰҵ��ʱ��
	 * @param pk_org
	 *            ����ǰҵ��ʱ��
	 * @return
	 */
	@Override
	public UFLiteralDate getIndutyDate(String pk_psnjob, String pk_org) {
		// ȡ��ǰ����
		UFDate busDate = PubEnv.getServerDate();
		UFLiteralDate busLitDate = UFLiteralDate.getDate(busDate.toString()
				.substring(0, 10));
		// ���û��ѡ����Ա���򷵻ص�ǰҵ������
		if (StringUtils.isBlank(pk_psnjob))
			return busLitDate;
		UFLiteralDate ufdate = null;
		try {
			// ��ѯ��Ա��ְ����
			// ufdate =
			// NCLocator.getInstance().lookup(IPsndocQryService.class).queryIndutyDate(pk_psnjob);
			ufdate = queryBeginDateFromPsnjob(pk_psnjob);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		// ���û�е�ְ���ڣ����ÿ����ڼ�Ŀ�ʼ���ڣ����û�п����ڼ���߿����ڼ��Ӧ�Ŀ�ʼ���ڣ���ȡ��ǰҵ��ʱ��
		if (ufdate == null) {
			PeriodVO vo = null;
			try {
				vo = NCLocator.getInstance().lookup(IPeriodQueryService.class)
						.queryByDate(pk_org, busLitDate);
			} catch (Exception ex) {
				Logger.error(ex.getMessage(), ex);
			}
			return (vo == null || vo.getBegindate() == null) ? busLitDate : vo
					.getBegindate();
		}
		// ����е�ְ���ڣ���ȡ��ְ����
		return ufdate;
	}

	@Override
	public TBMPsndocVO[] queryByCondition(LoginContext context, String condition)
			throws BusinessException {
		return setLatestFlag(getServiceTemplate().queryByCondition(context,
				TBMPsndocVO.class, condition));
	}

	@Override
	public TBMPsndocVO[] queryByCondition(LoginContext context,
			FromWhereSQL fromWhereSQL) throws BusinessException {
		// ����Ȩ��
		fromWhereSQL = TBMPsndocSqlPiecer
				.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		return setLatestFlag(new TBMPsndocDAO().queryByCondition(
				context.getPk_org(), fromWhereSQL));
	}

	@Override
	public TBMPsndocVO[] update(TBMPsndocVO[] vos, boolean isUpdatePsnCalendar)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		// V65�޸ģ������ڵ����Ŀ�ʼ���������ڡ�������¼������ȶ�û�б仯��ֱ�Ӹ������ݿ⼴�ɣ�������������
		boolean isDirectUpdate = true;
		String[] pks = StringPiecer.getStrArray(vos, TBMPsndocVO.PK_TBM_PSNDOC);
		TBMPsndocVO[] oldVOs = getServiceTemplate().queryByPks(
				TBMPsndocVO.class, pks);
		Map<String, TBMPsndocVO> newMap = CommonUtils.toMap(
				TBMPsndocVO.PK_TBM_PSNDOC, vos);
		Map<String, TBMPsndocVO> oldMap = CommonUtils.toMap(
				TBMPsndocVO.PK_TBM_PSNDOC, oldVOs);
		for (String pk : pks) {
			TBMPsndocVO newVO = newMap.get(pk);
			TBMPsndocVO oldVO = oldMap.get(pk);
			if (newVO == null || oldVO == null) {
				isDirectUpdate = false;
				break;
			}
			if (!newVO.getBegindate().toString().trim()
					.equals(oldVO.getBegindate().toString().trim())) {
				isDirectUpdate = false;
				break;
			}
			if (!newVO.getEnddate().toString().trim()
					.equals(oldVO.getEnddate().toString().trim())) {
				isDirectUpdate = false;
				break;
			}
			if (!newVO.getPk_psnjob().equals(oldVO.getPk_psnjob())) {
				isDirectUpdate = false;
				break;
			}
			String pk_team = newVO.getPk_team();
			String pk_teamold = oldVO.getPk_team();
			if (StringUtils.isBlank(pk_team)
					&& StringUtils.isNotBlank(pk_teamold)) {
				isDirectUpdate = false;
				break;
			}
			if (StringUtils.isNotBlank(pk_team)
					&& StringUtils.isBlank(pk_teamold)) {
				isDirectUpdate = false;
				break;
			}
			if (StringUtils.isNotBlank(pk_team)
					&& StringUtils.isNotBlank(pk_teamold)
					&& !pk_team.equals(pk_teamold)) {
				isDirectUpdate = false;
				break;
			}

		}
		if (isDirectUpdate) {
			// У�����
			DefaultValidationService vService = new DefaultValidationService();
			vService.addValidator(new TBMCardIDValidator());
			vService.validate(vos);
			vos = getServiceTemplate().update(true, vos);
			return setLatestFlag(vos);
		}

		String pk_org = vos[0].getPk_org();
		IPsnCalendarManageService psncalendarService = NCLocator.getInstance()
				.lookup(IPsnCalendarManageService.class);
		if (isUpdatePsnCalendar) {
			psncalendarService.processBeforeUpdateTBMPsndoc(pk_org, vos);
		}
		// У��
		check(vos);
		// У���ڼ䣬��Ҫ�ǣ�1.�����ʼ������ǰ��������ǰ����֯�ĵ�һ���ڼ�֮ǰ�����ܱ䶯�ѷ�濼���ڼ��ڵ�����
		NCLocator.getInstance().lookup(IPeriodQueryService.class)
				.checkBeforeUpdateTBMPsndoc(pk_org, vos);
		NCLocator.getInstance().lookup(IMonthStatManageService.class)
				.processBeforeUpdateTBMPsndoc(pk_org, vos);
		// Map<String, TBMPsndocVO> oldMap =
		// CommonUtils.toMap(TBMPsndocVO.PK_TBM_PSNDOC, oldVOs);
		// ��Ա��ְ������޸Ŀ��ڵ�������ʾ��������������������޸ģ���ˢ�½���
		// vos = getServiceTemplate().update(true, vos);
		// �汾У�飨ʱ���У�飩
		BDVersionValidationUtil.validateSuperVO(vos);
		BaseDAO basedao = new BaseDAO();
		basedao.updateVOArray(vos);

		// ���»���
		CacheProxy.fireDataUpdated(TBMPsndocVO.getDefaultTableName());
		for (TBMPsndocVO vo : vos) {
			// ���°�����Ա
			new TBMPsndocChangeProcessor().processAfterTBMPsndocUpdate(
					oldMap.get(vo.getPk_tbm_psndoc()), vo);
		}
		if (isUpdatePsnCalendar) {
			// ���¹�������
			psncalendarService.autoArrange(pk_org,
					StringPiecer.getStrArray(vos, TBMPsndocVO.PK_PSNDOC));
		}
		return setLatestFlag(vos);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ArrayList<String>[] updateTbmCard(String pk_hrorg, GeneralVO[] vos,
			boolean isOverRide) throws BusinessException {
		String inSql = null;
		InSQLCreator isc = new InSQLCreator();
		try {
			int beginRowNum = 3;
			// ����У�����ݸ�ʽ����ȷ ����ͬһ�ļ�һ���Ƿ��ж�����¼ �����Ҳ�����Ӧ����Ա���ڵ��� ���������ѱ�ʹ��
			ArrayList<String> psnVec = new ArrayList<String>();
			ArrayList<String> psncodeVec = new ArrayList<String>();
			ArrayList<String> timecardVec = new ArrayList<String>();

			ArrayList<String> wrongFormatVec = new ArrayList<String>(); // ���ݸ�ʽ����ȷ�Ĵ��󼯺�
			ArrayList<String> psnNotFoundVec = new ArrayList<String>(); // �Ҳ�����Ա�����Ĵ��󼯺�
			ArrayList<String> samePsnVec = new ArrayList<String>(); // һ���ж�����¼�Ĵ��󼯺�
			ArrayList<String> sameCardVec = new ArrayList<String>(); // һ���ж�����¼�Ĵ��󼯺�
			ArrayList<String> timecardUsedVec = new ArrayList<String>(); // �����ѱ�ʹ�õĴ��󼯺�

			for (int i = 0; i < vos.length; i++) {
				String psncode = (String) vos[i]
						.getAttributeValue(TBMPsndocCommonValue.LISTCODE_PSNCODE);
				String psnname = (String) vos[i]
						.getAttributeValue(TBMPsndocCommonValue.LISTCODE_PSNNAME);
				String timecardid = (String) vos[i]
						.getAttributeValue(TBMPsndocVO.TIMECARDID);

				if ((psncode == null || psncode.trim().equals(""))
						&& (psnname == null || psnname.trim().equals(""))
						&& (timecardid == null || timecardid.trim().equals(""))) {
					psnVec.add("" + "_" + "");
					timecardVec.add("");
					continue;
				}
				// У�����ݸ�ʽ
				if (psncode == null || psncode.trim().equals("")) {
					wrongFormatVec.add((i + beginRowNum) + "_0");
				}

				if (psnname == null || psnname.trim().equals("")) {
					wrongFormatVec.add((i + beginRowNum) + "_1");
				}
				// У���ظ���¼
				if (psncode != null && !psncode.trim().equals("")
						&& psnname != null && !psnname.trim().equals("")) {
					if (psnVec.contains(psncode + "_" + psnname)) {
						int index = psnVec.indexOf(psncode + "_" + psnname);
						if (!samePsnVec.contains(String.valueOf(index
								+ beginRowNum))) {
							samePsnVec.add(String.valueOf(index + beginRowNum));
						}
						samePsnVec.add(String.valueOf((i + beginRowNum)));
					}
				}
				psnVec.add(psncode + "_" + psnname);

				if (timecardid != null && !timecardid.trim().equals("")) {
					if (timecardVec.contains(timecardid)) {
						int index = timecardVec.indexOf(timecardid);
						if (!sameCardVec.contains(String.valueOf(index
								+ beginRowNum))) {
							sameCardVec
									.add(String.valueOf(index + beginRowNum));
						}
						sameCardVec.add(String.valueOf((i + beginRowNum)));
					}
				}
				timecardVec.add(timecardid);

				if (psncode != null && !psncode.equals("")) {
					psncodeVec.add(psncode);
				}
			}

			inSql = isc.getInSQL(psncodeVec.toArray(new String[0]));

			String oriWhere = " bd_psndoc.code in(" + inSql + ")";
			TBMPsndocDAO dao = new TBMPsndocDAO();
			// ����漰��Ա�Ŀ��ڵ���
			ArrayList<GeneralVO> tmpPsndocVOs = dao.queryTaPsninfoForImport(
					pk_hrorg, oriWhere);

			Map<String, GeneralVO> psncode_tbmPsndocMap = new HashMap<String, GeneralVO>();

			if (tmpPsndocVOs != null && tmpPsndocVOs.size() > 0) {
				for (GeneralVO psndocVO : tmpPsndocVOs) {
					String psncode = (String) psndocVO
							.getAttributeValue(TBMPsndocCommonValue.LISTCODE_PSNCODE);
					String psnname = (String) psndocVO
							.getAttributeValue(TBMPsndocCommonValue.LISTCODE_PSNNAME);
					// �����һ�������������ڼ�¼����ȡ���µ�
					if (psncode_tbmPsndocMap.get(psncode + "_" + psnname) == null) {
						psncode_tbmPsndocMap.put(psncode + "_" + psnname,
								psndocVO);
					} else {
						UFLiteralDate begindatetime = UFLiteralDate
								.getDate((String) psncode_tbmPsndocMap.get(
										psncode + "_" + psnname)
										.getAttributeValue(
												TBMPsndocVO.BEGINDATE));
						if (begindatetime
								.compareTo(UFLiteralDate.getDate((String) psndocVO
										.getAttributeValue(TBMPsndocVO.BEGINDATE))) < 0) {
							psncode_tbmPsndocMap.put(psncode + "_" + psnname,
									psndocVO);
						}
					}
				}
			}

			// һ�β�ѯ�����漰���Ŀ��ڿ���¼ 2012-10-11�޸ģ��Ż�sql������
			String[] cardIDs = StringPiecer.getStrArrayDistinct(vos,
					TBMPsndocVO.TIMECARDID);
			Map<String, List<TBMPsndocVO>> existMap = new HashMap<String, List<TBMPsndocVO>>();
			if (!ArrayUtils.isEmpty(cardIDs)) {
				inSql = isc.getInSQL(cardIDs);
				String cardCondSql = " pk_org='" + pk_hrorg
						+ "' and (timecardid in (" + inSql
						+ ") or secondcardid in (" + inSql + ") )";
				TBMPsndocVO[] existPsndocs = CommonUtils.retrieveByClause(
						TBMPsndocVO.class, cardCondSql);
				for (int i = 0, j = ArrayUtils.getLength(existPsndocs); i < j; i++) {
					if (StringUtils.isNotEmpty(existPsndocs[i].getTimecardid())) {
						List<TBMPsndocVO> cardList = existMap
								.get(existPsndocs[i].getTimecardid());
						if (cardList == null) {
							cardList = new ArrayList<TBMPsndocVO>();
							existMap.put(existPsndocs[i].getTimecardid(),
									cardList);
						}
						cardList.add(existPsndocs[i]);
					}
					if (StringUtils.isNotEmpty(existPsndocs[i]
							.getSecondcardid())) {
						List<TBMPsndocVO> cardList = existMap
								.get(existPsndocs[i].getSecondcardid());
						if (cardList == null) {
							cardList = new ArrayList<TBMPsndocVO>();
							existMap.put(existPsndocs[i].getSecondcardid(),
									cardList);
						}
						cardList.add(existPsndocs[i]);
					}
				}
			}
			ArrayList<TBMPsndocVO> updateVOList = new ArrayList<TBMPsndocVO>();
			for (int i = 0; i < vos.length; i++) {
				String psncode = (String) vos[i]
						.getAttributeValue(TBMPsndocCommonValue.LISTCODE_PSNCODE);
				String psnname = (String) vos[i]
						.getAttributeValue(TBMPsndocCommonValue.LISTCODE_PSNNAME);
				String timecardid = (String) vos[i]
						.getAttributeValue(TBMPsndocVO.TIMECARDID);
				timecardid = "".equals(timecardid) ? null : timecardid;
				if (psncode == null || psncode.trim().equals("")) {
					continue;
				}

				if (psncode_tbmPsndocMap.get(psncode + "_" + psnname) == null) {
					psnNotFoundVec.add(String.valueOf((i + beginRowNum)));
				} else {
					GeneralVO tbmPsndocVO = psncode_tbmPsndocMap.get(psncode
							+ "_" + psnname);
					String timecardidTmp = (String) tbmPsndocVO
							.getAttributeValue(TBMPsndocVO.TIMECARDID);
					if (timecardidTmp != null
							&& !timecardidTmp.trim().equals("") && !isOverRide) {
						//
					} else {
						tbmPsndocVO.setAttributeValue(TBMPsndocVO.TIMECARDID,
								timecardid);
						TBMPsndocVO psndocVO = new TBMPsndocVO();
						psndocVO.setPk_tbm_psndoc((String) tbmPsndocVO
								.getAttributeValue(TBMPsndocVO.PK_TBM_PSNDOC));
						psndocVO.setTimecardid(timecardid);
						psndocVO.setPk_psnjob((String) tbmPsndocVO
								.getAttributeValue(TBMPsndocVO.PK_PSNJOB));
						psndocVO.setPk_psndoc((String) tbmPsndocVO
								.getAttributeValue(TBMPsndocVO.PK_PSNDOC));
						updateVOList.add(psndocVO);
					}

					if (timecardid == null || timecardid.trim().equals("")) {
						continue;
					}

					if (!psnNotFoundVec.contains(String
							.valueOf((i + beginRowNum)))
							&& !samePsnVec.contains(String
									.valueOf((i + beginRowNum)))
							&& !sameCardVec.contains(String
									.valueOf((i + beginRowNum)))) {
						// ��鿼�ڿ���
						if (timecardid != null
								&& timecardid.trim().indexOf(" ") >= 0) {
							wrongFormatVec.add((i + beginRowNum) + "_3");
						}
						List<TBMPsndocVO> existPsndocList = existMap
								.get(timecardid);
						if (CollectionUtils.isEmpty(existPsndocList))
							continue;
						for (TBMPsndocVO existPsndoc : existPsndocList) {
							// ͬһ����������ͬ�����ظ�
							if (existPsndoc
									.getPk_psndoc()
									.equals(tbmPsndocVO
											.getAttributeValue(TBMPsndocVO.PK_PSNDOC))
									&& !timecardid.equals(existPsndoc
											.getSecondcardid()))
								continue;
							// ���������ڿ�ʼ����֮ǰ�Ĳ����ظ�
							if (existPsndoc
									.getEnddate()
									.before(new UFLiteralDate(
											(String) tbmPsndocVO
													.getAttributeValue(TBMPsndocVO.BEGINDATE))))
								continue;
							// ����Ķ���Ϊ�����ظ�
							timecardUsedVec.add((i + beginRowNum) + "_3");
							break;
						}
						// boolean isIDRepeated =
						// dao.checkTACardIDForImport(pk_hrorg, timecardid, new
						// UFLiteralDate(
						// (String)
						// tbmPsndocVO.getAttributeValue(TBMPsndocVO.BEGINDATE)),
						// (String) tbmPsndocVO
						// .getAttributeValue(TBMPsndocVO.PK_PSNDOC));
						//
						// if (isIDRepeated) {
						// timecardUsedVec.add((i + beginRowNum) + "_2");
						// }
					}
				}

			}

			// ������쳣���򷵻��쳣��Ϣ
			if (samePsnVec.size() > 0 || psnNotFoundVec.size() > 0
					|| wrongFormatVec.size() > 0 || timecardUsedVec.size() > 0
					|| sameCardVec.size() > 0 || timecardUsedVec.size() > 0) {
				ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
				lists.add(wrongFormatVec);
				lists.add(psnNotFoundVec);
				lists.add(samePsnVec);
				lists.add(sameCardVec);
				lists.add(timecardUsedVec);
				return ArrayClassConvertUtil.convert(lists.toArray(),
						ArrayList.class);
			}
			if (CollectionUtils.isEmpty(updateVOList))
				return null;
			TBMPsndocVO[] updatevos = updateVOList.toArray(new TBMPsndocVO[0]);
			// ����ά��Ȩ�޽��й���
			// Map<Object, UFBoolean> operMap =
			// DataPermissionFacade.isUserHasPermissionByMetaDataOperation(InvocationInfoProxy.getInstance().getUserId(),
			// "60170psndoc", IActionCode.EDIT,
			// InvocationInfoProxy.getInstance().getGroupId(), updatevos);
			IDataPermissionPubService perimssionService = NCLocator
					.getInstance().lookup(IDataPermissionPubService.class);
			Map<String, UFBoolean> operMap = perimssionService
					.isUserhasPermissionByMetaDataOperation("60170psndoc",
							StringPiecer.getStrArray(updatevos,
									TBMPsndocVO.PK_TBM_PSNDOC),
							IActionCode.EDIT, InvocationInfoProxy.getInstance()
									.getGroupId(), InvocationInfoProxy
									.getInstance().getUserId());

			List<TBMPsndocVO> needUpdate = new ArrayList<TBMPsndocVO>();
			for (TBMPsndocVO vo : updatevos) {// ���˳���Ȩ�޵�
				if (operMap.get(vo.getPk_tbm_psndoc()).booleanValue())
					needUpdate.add(vo);
			}
			// new BaseDAO().updateVOArray(updatevos, new
			// String[]{TBMPsndocVO.TIMECARDID});
			if (CollectionUtils.isEmpty(needUpdate))
				return null;
			TBMPsndocVO[] needUpdateVOs = needUpdate
					.toArray(new TBMPsndocVO[0]);
			new BaseDAO().updateVOArray(needUpdateVOs,
					new String[] { TBMPsndocVO.TIMECARDID });
			// ҵ����־
			// updateVOList��ֻ����������ʱ���ţ����ѯȫ���ֶεļ�¼
			// TBMPsndocVO[] logvos = (TBMPsndocVO[])
			// queryPsndocVOByPks(StringPiecer.getStrArray(updatevos,
			// TBMPsndocVO.PK_TBM_PSNDOC));
			TBMPsndocVO[] logvos = (TBMPsndocVO[]) queryPsndocVOByPks(StringPiecer
					.getStrArray(needUpdateVOs, TBMPsndocVO.PK_TBM_PSNDOC));
			TaBusilogUtil.writeTbmPsndocImport(logvos);
			return null;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			isc.clear();
		}
	}

	@Override
	public TBMPsndocVO[] queryByPsnInfo(LoginContext context,
			FromWhereSQL fromWhereSQL) throws BusinessException {
		// ����Ȩ�޴���
		fromWhereSQL = TBMPsndocSqlPiecer
				.addPsnjobPermission2PsnjobQuerySQL(fromWhereSQL);
		return setLatestFlag(new TBMPsndocDAO().queryByPsnForBatchAdd(
				context.getPk_org(), fromWhereSQL));
	}

	@Override
	public TBMPsndocVO[] batchInsert(LoginContext context, TBMPsndocVO[] vos,
			int tbm_prop, UFLiteralDate beginDate, String pk_place,
			boolean isUpdatePsnCalendar) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		vos = convertTBMPsndocs(context, vos, tbm_prop, beginDate, pk_place);
		return insert(vos, isUpdatePsnCalendar);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public TBMPsndocVO[] insert(TBMPsndocVO[] vos, boolean isUpdatePsnCalendar)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		String pk_org = vos[0].getPk_org();
		// ��鿼���ڼ�
		NCLocator.getInstance().lookup(IPeriodQueryService.class)
				.checkBeforeInsertTBMPsndoc(pk_org, vos);
		// У�����
		DefaultValidationService vService = new DefaultValidationService();
		vService.addValidator(new TBMCardIDValidator());// ���ڿ���У��
		vService.addValidator(new TBMPsndocDateValidator());// ����У��
		vService.validate(vos);
		// �����ƶ�ǩ���Ŀ�������
		// �ҳ�û�������ƶ����������vo
		List<TBMPsndocVO> noRegionVOs = new ArrayList<TBMPsndocVO>();
		for (TBMPsndocVO vo : vos) {
			if (StringUtils.isBlank(vo.getPk_region())) {
				noRegionVOs.add(vo);
			}
		}
		if (!CollectionUtils.isEmpty(noRegionVOs)) {
			TBMPsndocVO[] rePsndocVOs = noRegionVOs.toArray(new TBMPsndocVO[0]);
			Map<String, TBMPsndocVO> jobMap = CommonUtils.toMap(
					TBMPsndocVO.PK_PSNJOB, rePsndocVOs);
			String[] pk_psnjobs = StringPiecer.getStrArray(rePsndocVOs,
					TBMPsndocVO.PK_PSNJOB);
			String insql = new InSQLCreator().getInSQL(pk_psnjobs);
			String regionSql = " select pjob.pk_psnjob, rog.pk_region from tbm_regionorg rog inner join hi_psnjob pjob on rog.pk_org = pjob.pk_org where pk_psnjob in("
					+ insql + ")";
			BaseDAO baseDAO = new BaseDAO();
			List pkList = (List) baseDAO.executeQuery(regionSql,
					new ArrayListProcessor());
			if (!CollectionUtils.isEmpty(pkList)) {
				for (Object pkl : pkList) {
					Object[] array = (Object[]) pkl;
					String pk_psnjob = (String) array[0];
					TBMPsndocVO psnvo = jobMap.get(pk_psnjob);
					if (psnvo != null
							&& StringUtils.isBlank(psnvo.getPk_region())) {
						String pk_region = (String) array[1];
						psnvo.setPk_region(pk_region);
					}
				}
			}
		}
		vos = getServiceTemplate().insert(vos);
		// ���»���
		CacheProxy.fireDataInserted(TBMPsndocVO.getDefaultTableName());
		if (isUpdatePsnCalendar) {
			// �����µĹ�������ǰ���������ʱ����ڵĹ�������
			new PsnCalendarDAO().deleteByTBMPsndocVOs(vos);
			// ���¹�������
			Map<String, TBMPsndocVO[]> tbmpsn = CommonUtils.group2ArrayByField(
					TBMPsndocVO.PK_ORG, vos);
			Iterator<String> iterator = tbmpsn.keySet().iterator();
			IPsnCalendarManageService psnCalenDarMS = NCLocator.getInstance()
					.lookup(IPsnCalendarManageService.class);
			if (iterator.hasNext()) {
				String curOrg = iterator.next();
				psnCalenDarMS.autoArrange(curOrg, StringPiecer.getStrArray(
						tbmpsn.get(curOrg), TBMPsndocVO.PK_PSNDOC));
			}
		}
		// ���������±�
		processAfterInsertTBMPsndoc(vos, pk_org);
		return setLatestFlag(vos);
	}

	private void processAfterInsertTBMPsndoc(final TBMPsndocVO[] vos,
			final String pk_org) throws BusinessException {
		final InvocationInfo invocationInfo = BDDistTokenUtil
				.getInvocationInfo();
		new Executor(new Runnable() {
			@Override
			public void run() {
				// �߳��л�����Ϣ�ᶪʧ������������һ��
				BDDistTokenUtil.setInvocationInfo(invocationInfo);
				try {
					NCLocator.getInstance()
							.lookup(IMonthStatManageService.class)
							.processAfterInsertTBMPsndoc(pk_org, vos);
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
				}
			}
		}).start();
	}

	/**
	 * ������������ҳ����Ϣ���TBMPsndocVO
	 *
	 * @param context
	 * @param vos
	 * @param tbm_prop
	 * @param beginDate
	 * @return
	 */
	private TBMPsndocVO[] convertTBMPsndocs(LoginContext context,
			TBMPsndocVO[] vos, int tbm_prop, UFLiteralDate beginDate,
			String pk_place) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		for (TBMPsndocVO vo : vos) {
			vo.setTbm_prop(tbm_prop);
			vo.setPk_place(pk_place);
			vo.setPk_adminorg(context.getPk_org());
			vo.setPk_org(context.getPk_org());
			vo.setPk_group(context.getPk_group());
			vo.setEnddate(UFLiteralDate.getDate(TBMPsndocCommonValue.END_DATA));
			vo.setStatus(VOStatus.NEW);

			// �������Ա�䶯����ʼ����Ϊ�䶯����(��ְ��ʼ��Ϊ��ְ��ʼ���ڣ�������¼��ʼ���ǹ�����¼��ʼ����)
			// �������Ա���н����Ŀ��ڵ��������ұ䶯����С��������Ա���һ���������ڵ����Ľ������ڣ�
			// ���ڵ�����ʼ����Ϊ���һ���������ڵ����������ڵ���һ�졣
			// ����ǵ�һ��¼�뿼�ڵ�������䶯������ui���Ѹ�ֵ�������ٲ�ѯ��ְ����
			if (vo.getBegindate() != null) {
				// ��ѯ����Ա�Ƿ���ڽ����Ŀ��ڵ�������
				if (getLastFnsPsndocEndDt(vo.getPk_psndoc()) != null) {
					// ����У���ȡ��һ��
					vo.setBegindate(getChgActionBgnDate(vo.getBegindate(),
							vo.getPk_psndoc()));
				}
				continue;
			}
			if (beginDate == null) {
				// �������Աû���ѽ����Ŀ��ڵ��������ڿ�ʼ����Ϊ��ְ�����㷨
				if (getLastFnsPsndocEndDt(vo.getPk_psndoc()) == null) {
					// ��������ҳ���������ʼ����Ϊ�գ���ʼ����Ϊ��ְ����
					vo.setBegindate(getIndutyDate(vo.getPk_psnjob(),
							context.getPk_org()));
					continue;
				}
				// ����У���ȡ��ְ���ں����һ�ν����Ŀ��ڵ��������ڵ���һ�����Ƚϣ�ȡ���
				vo.setBegindate(getChgActionBgnDate(
						getIndutyDate(vo.getPk_psnjob(), context.getPk_org()),
						vo.getPk_psndoc()));
				continue;
			}
			vo.setBegindate(beginDate);
		}
		return vos;
	}

	/**
	 * �������Ա�䶯����ʼ����Ϊ�䶯����(��ְ��ʼ��Ϊ��ְ��ʼ���ڣ�������¼��ʼ���ǹ�����¼��ʼ����)
	 * �������Ա���н����Ŀ��ڵ��������ұ䶯����С��������Ա���һ���������ڵ����Ľ������ڣ�
	 * ���ڵ�����ʼ����Ϊ���һ���������ڵ����������ڵ���һ�졣 ����ǵ�һ��¼�뿼�ڵ�������䶯������ui���Ѹ�ֵ�������ٲ�ѯ��ְ����
	 *
	 * @param trnDate
	 *            ���䶯����
	 * @param pk_psndoc
	 * @return
	 */
	private UFLiteralDate getChgActionBgnDate(UFLiteralDate trnDate,
			String pk_psndoc) throws BusinessException {
		if (trnDate == null)
			return null;
		return getLaterDate(trnDate, getLastFnsPsndocEndDt(pk_psndoc)
				.getDateAfter(1));
	}

	/**
	 * ȡ�����������н���������
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	private UFLiteralDate getLaterDate(UFLiteralDate date1, UFLiteralDate date2)
			throws BusinessException {
		if (date1 == null && date2 == null)
			return null;
		if (date1 == null)
			return date2;
		if (date2 == null)
			return date1;
		return date1.after(date2) ? date1 : date2;
	}

	/**
	 * ��ѯ��Ա���һ�������Ŀ��ڵ���ʱ��
	 *
	 * @param pk_psndoc
	 * @return
	 */
	private UFLiteralDate getLastFnsPsndocEndDt(String pk_psndoc)
			throws BusinessException {
		if (getLastFnsPsndoc(pk_psndoc) == null)
			return null;
		return getLastFnsPsndoc(pk_psndoc).getEnddate();
	}

	/**
	 * ��ѯ��Ա���һ�������Ŀ��ڵ���
	 *
	 * @param pk_psndoc
	 * @return
	 */
	private TBMPsndocVO getLastFnsPsndoc(String pk_psndoc)
			throws BusinessException {
		String condition = TBMPsndocVO.PK_PSNDOC + " = ? and "
				+ TBMPsndocVO.ENDDATE + " <> '" + TBMPsndocCommonValue.END_DATA
				+ "' order by " + TBMPsndocVO.ENDDATE + " desc ";
		SQLParameter params = new SQLParameter();
		params.addParam(pk_psndoc);
		TBMPsndocVO[] vos = queryByCondition(condition, params);
		if (ArrayUtils.isEmpty(vos))
			return null;
		return vos[0];
	}

	@SuppressWarnings("unchecked")
	@Override
	public TBMPsndocVO queryUnFinishPsnDoc(String pk_psndoc)
			throws BusinessException {
		String condition = TBMPsndocVO.PK_PSNDOC + " = ? and "
				+ TBMPsndocVO.ENDDATE + " = '" + TBMPsndocCommonValue.END_DATA
				+ "' ";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_psndoc);
		Collection<TBMPsndocVO> vos = new BaseDAO()
				.retrieveByClause(TBMPsndocVO.class, condition, param);
		if (CollectionUtils.isEmpty(vos))
			return null;
		return vos.toArray(new TBMPsndocVO[0])[0];
	}

	@Override
	public Map<String, TBMPsndocVO> queryUnFinishPsnDoc(String[] pk_psndocs)
			throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs))
			return null;
		String condition = TBMPsndocVO.PK_PSNDOC + " in ("
				+ new InSQLCreator().getInSQL(pk_psndocs) + ") and "
				+ TBMPsndocVO.ENDDATE + " = '" + TBMPsndocCommonValue.END_DATA
				+ "' ";
		TBMPsndocVO[] vos = CommonUtils.retrieveByClause(TBMPsndocVO.class,
				condition);
		return CommonUtils.toMap(TBMPsndocVO.PK_PSNJOB, vos);
	}

	@Override
	public TBMPsndocVO[] queryByCondition(String condition)
			throws BusinessException {
		return setLatestFlag(getServiceTemplate().queryByCondition(
				TBMPsndocVO.class, condition));
	}

	@SuppressWarnings("unchecked")
	@Override
	public TBMPsndocVO[] queryByCondition(String condition, SQLParameter params)
			throws BusinessException {
		Collection<TBMPsndocVO> vos = new BaseDAO()
				.retrieveByClause(TBMPsndocVO.class, condition, params);
		if (CollectionUtils.isEmpty(vos))
			return null;
		return setLatestFlag(vos.toArray(new TBMPsndocVO[0]));
	}

	/**
	 * �������±�ʶ
	 *
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	private TBMPsndocVO[] setLatestFlag(TBMPsndocVO[] vos)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		boolean[] result = isLatestPsndoc(vos);
		for (int i = 0; i < vos.length; i++) {
			vos[i].setIslatest(UFBoolean.valueOf(result[i]));
		}
		return vos;
	}

	/**
	 * ���ڵ����Ƿ������һ��
	 *
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private boolean[] isLatestPsndoc(TBMPsndocVO[] vos)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		// �������������9999��ͷ�������ֱ���ж������µġ��������9999����Ҫ�����ݿ��ѯ�Ƿ�������
		// �洢����ֱ���жϵ�vo��map,<pk_tbm_psndoc,TBMPsndocVO>
		Map<String, TBMPsndocVO> canNotDirectJudgeMap = new HashMap<String, TBMPsndocVO>();
		for (TBMPsndocVO vo : vos) {
			UFLiteralDate enddate = vo.getEnddate();
			// �������������9999-12-31����ʾ�˿��ڵ���Ϊ����
			if (enddate == null
					|| TBMPsndocCommonValue.END_DATA.equals(enddate.toString()))
				continue;
			canNotDirectJudgeMap.put(vo.getPrimaryKey(), vo);
		}
		boolean[] retArray = new boolean[vos.length];
		if (canNotDirectJudgeMap.size() == 0) {// ���mapΪ�գ���ʾ����9999���������µļ�¼�����ò����ݿ���
			for (int i = 0; i < retArray.length; i++) {
				retArray[i] = true;
			}
			return retArray;
		}
		InSQLCreator isc = null;
		BaseDAO dao = new BaseDAO();
		try {
			isc = new InSQLCreator();
			String inSQL = isc.getInSQL(
					canNotDirectJudgeMap.values().toArray(new TBMPsndocVO[0]),
					TBMPsndocVO.PK_TBM_PSNDOC);
			String sql = "select "
					+ TBMPsndocVO.PK_TBM_PSNDOC
					+ " from "
					+ TBMPsndocVO.getDefaultTableName()
					+ " psndoc1 where "
					+ TBMPsndocVO.PK_TBM_PSNDOC
					+ " in("
					+ inSQL
					+ ") and exists(select top 1 1 from "
					+ TBMPsndocVO.getDefaultTableName()
					+ " psndoc2 where psndoc1.pk_psndoc=psndoc2.pk_psndoc and psndoc2.begindate>psndoc1.enddate)";
			// ���Ϊ�գ���ʾ��Щ���ݶ������µģ����ݿ���û�б������µ�
			List<String> result = (List<String>) dao.executeQuery(sql,
					new ColumnListProcessor());
			if (CollectionUtils.isEmpty(result)) {
				for (int i = 0; i < retArray.length; i++) {
					retArray[i] = true;
				}
				return retArray;
			}
			// ������գ�����Ҫ�ж�
			Set<String> notLatestSet = new HashSet<String>(result);
			for (int i = 0; i < retArray.length; i++) {
				String pk_tbmpsndoc = vos[i].getPrimaryKey();
				if (!canNotDirectJudgeMap.containsKey(pk_tbmpsndoc)) {
					retArray[i] = true;
					continue;
				}
				retArray[i] = !notLatestSet.contains(pk_tbmpsndoc);
			}
			return retArray;
		} finally {
			if (isc != null)
				isc.clear();
		}
	}

	@Override
	public void check(TBMPsndocVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return;
		// У�����
		DefaultValidationService vService = new DefaultValidationService();
		vService.addValidator(new TBMCardIDValidator());
		vService.validate(vos);
		for (TBMPsndocVO vo : vos)
			check(vo);
	}

	@Override
	public void check(TBMPsndocVO vo) throws BusinessException {
		// У�����
		DefaultValidationService vService = new DefaultValidationService();
		// ���ڿ���У��
		TBMCardIDValidator idValidator = new TBMCardIDValidator();
		vService.addValidator(idValidator);
		// ����У��
		TBMPsndocDateValidator dateValidator = new TBMPsndocDateValidator();
		vService.addValidator(dateValidator);

		vService.validate(vo);
	}

	@Override
	public Object[] queryPsndocVOByPks(String[] pks) throws BusinessException {
		Object[] returns = MDPersistenceService.lookupPersistenceQueryService()
				.queryBillOfVOByPKsWithOrder(TBMPsndocVO.class, pks, true);
		if (ArrayUtils.isEmpty(returns))
			return null;
		List<TBMPsndocVO> vos = new ArrayList<TBMPsndocVO>();
		for (Object returnvo : returns)
			vos.add((TBMPsndocVO) returnvo);
		return setLatestFlag(vos.toArray(new TBMPsndocVO[0]));
	}

	@Override
	@SuppressWarnings("unchecked")
	public String[] queryPsndocPks(LoginContext context,
			String[] strSelectFields, String strFromPart, String strWhere,
			String strOrder, HashMap<String, String> hash, String resourceCode)
			throws BusinessException {
		strFromPart = strFromPart == null ? TBMPsndocVO.getDefaultTableName()
				: strFromPart;
		if (strSelectFields == null || strSelectFields.length == 0) {
			strSelectFields = new String[] { TBMPsndocVO.getDefaultTableName()
					+ "." + TBMPsndocVO.PK_TBM_PSNDOC };
		}
		String strSelectField = "";
		for (String strField : strSelectFields) {
			strSelectField += "," + strField;
		}
		String strSQL = "select " + strSelectField.substring(1) + " from "
				+ strFromPart + " where 1=1";
		SqlWhereUtil sqlWhereUtil = new SqlWhereUtil(strSQL);
		sqlWhereUtil.and(strWhere);
		String powerSql = TBMPsndocSqlPiecer.getPowerSql(resourceCode,
				"tbm_psndoc");
		if (!StringUtils.isBlank(powerSql)) {
			sqlWhereUtil.and(" ( " + powerSql + " ) ");
		}
		strSQL = sqlWhereUtil.getSQLWhere();

		if (!StringUtils.isBlank(strOrder)) {
			strSQL += " order by " + strOrder;
		}
		List<Object[]> list = (List<Object[]>) new BaseDAO().executeQuery(
				strSQL, new ArrayListProcessor());
		if (CollectionUtils.isEmpty(list))
			return null;
		String strPks[] = null;

		// ת��һ��,ȥ���ظ�������
		if (list != null && !list.isEmpty()) {
			ArrayList<String> al = new ArrayList<String>();
			for (int i = 0; i < list.size(); i++) {
				Object[] strPkPsnjobs = list.get(i);
				if (al.contains(strPkPsnjobs[0] + "")) {
					continue;
				}
				al.add(strPkPsnjobs[0] + "");
			}
			strPks = al.toArray(new String[0]);
		}
		return strPks;
	}

	@Override
	public String[] queryPsndocPks(LoginContext context,
			FromWhereSQL fromWhereSQL, String condition)
			throws BusinessException {
		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocCond2QuerySQL(condition,
				fromWhereSQL);
		TBMPsndocVO[] vos = queryByCondition(context, fromWhereSQL);
		return StringPiecer.getStrArray(vos, TBMPsndocVO.PK_TBM_PSNDOC);
	}

	/**
	 * �򵥵�ȡ�����UFDateTime��ǰ10λ��Ϊ���ڣ�Ȼ����ǰ��һ�죬������һ�죬Ȼ���ѯ��pk_psndoc�������췶Χ����Ч�Ŀ��ڵ�����¼��
	 * ���û�У�����null���еĻ����������µ�һ��
	 *
	 * @param pk_psndoc
	 * @param dateTime
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public TBMPsndocVO queryByPsndocAndDateTime(String pk_psndoc,
			UFDateTime dateTime) throws BusinessException {
		return new TBMPsndocDAO().queryByPsndocAndDateTime(pk_psndoc, dateTime);
	}

	@Override
	public void syncTeamPsn(TeamItemVO[] vos) throws BusinessException {
		new TeamPsnChangeEventListener().handleTeamPsnChange(vos, null, null,
				null);
	}

	@Override
	public UFLiteralDate queryBeginDateFromPsnjob(String pk_psnjob) {
		PsnJobVO jobvo = null;
		try {
			jobvo = (PsnJobVO) new nc.impl.hr.frame.persistence.PersistenceImpl()
					.retrieveByPk(null, PsnJobVO.class, pk_psnjob);
			// jobvo= (PsnJobVO)
			// NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByPk(null,
			// PsnJobVO.class, pk_psnjob);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		return jobvo.getBegindate();
	}

	@Override
	public TBMPsndocVO queryByPsndocAndDate(String pk_psndoc, UFLiteralDate date)
			throws BusinessException {
		return new TBMPsndocDAO().queryByPsndocAndDate(pk_psndoc, date);
	}

	@Override
	public int update(String date, String pk_psndoc, int type, String pk_org)
			throws BusinessException {
//		String defshift = new TBMPsndocDAO().queryDefshift(pk_org);
//		// pk_shift���Բ����� ��Ҫ��ʱ�����ɾ��pk_shift�ĸ���
//		String pk_shift = "";
//		switch (type) {
//		case 0:
//			pk_shift = defshift;
//			break;
//		case 1:
//			pk_shift = "0001Z7000000000000GX";
//			break;
//		case 2:
//			pk_shift = "0001Z7000000000000GX";
//			break;
//		case 4:
//			pk_shift = "0001Z7000000000012GX";
//			break;
//		default:
//			break;
//		}
//		// ����У�� ��������7���ڱ���һ���������� ���� 14���ڱ���������������
//		boolean oneOT = new TBMPsndocDAO().validateOneOT(date);
//		if (!oneOT) {
//			throw new BusinessException("��������7���ڱ���һ���������� ���� 14���ڱ���������������");
//		}
//		String sql = "update tbm_psncalendar set date_daytype=" + type
//				+ " where calendar='" + date + "' and pk_psndoc ='" + pk_psndoc
//				+ "'";
//		int i = new BaseDAO().executeUpdate(sql);
		return 0;
	}

	@Override
	public TBMPsndocVO[] batchInsert(LoginContext context, TBMPsndocVO[] vos,
			int tbm_prop, UFLiteralDate beginDate, String pk_place,
			int tbm_weekform, int tbm_otcontrol, boolean isUpdatePsnCalendar)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		vos = convertTBMPsndocs(context, vos, tbm_prop, beginDate,tbm_weekform, tbm_otcontrol, pk_place);
		return insert(vos, isUpdatePsnCalendar);
	}

	private TBMPsndocVO[] convertTBMPsndocs(LoginContext context,
			TBMPsndocVO[] vos, int tbm_prop, UFLiteralDate beginDate,
			int tbm_weekform, int tbm_otcontrol, String pk_place) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		for (TBMPsndocVO vo : vos) {
			vo.setTbm_prop(tbm_prop);
			vo.setWeekform(tbm_weekform);
			vo.setOvertimecontrol(tbm_otcontrol);
			vo.setPk_place(pk_place);
			vo.setPk_adminorg(context.getPk_org());
			vo.setPk_org(context.getPk_org());
			vo.setPk_group(context.getPk_group());
			vo.setEnddate(UFLiteralDate.getDate(TBMPsndocCommonValue.END_DATA));
			vo.setStatus(VOStatus.NEW);

			// �������Ա�䶯����ʼ����Ϊ�䶯����(��ְ��ʼ��Ϊ��ְ��ʼ���ڣ�������¼��ʼ���ǹ�����¼��ʼ����)
			// �������Ա���н����Ŀ��ڵ��������ұ䶯����С��������Ա���һ���������ڵ����Ľ������ڣ�
			// ���ڵ�����ʼ����Ϊ���һ���������ڵ����������ڵ���һ�졣
			// ����ǵ�һ��¼�뿼�ڵ�������䶯������ui���Ѹ�ֵ�������ٲ�ѯ��ְ����
			if (vo.getBegindate() != null) {
				// ��ѯ����Ա�Ƿ���ڽ����Ŀ��ڵ�������
				if (getLastFnsPsndocEndDt(vo.getPk_psndoc()) != null) {
					// ����У���ȡ��һ��
					vo.setBegindate(getChgActionBgnDate(vo.getBegindate(),
							vo.getPk_psndoc()));
				}
				continue;
			}
			if (beginDate == null) {
				// �������Աû���ѽ����Ŀ��ڵ��������ڿ�ʼ����Ϊ��ְ�����㷨
				if (getLastFnsPsndocEndDt(vo.getPk_psndoc()) == null) {
					// ��������ҳ���������ʼ����Ϊ�գ���ʼ����Ϊ��ְ����
					vo.setBegindate(getIndutyDate(vo.getPk_psnjob(),
							context.getPk_org()));
					continue;
				}
				// ����У���ȡ��ְ���ں����һ�ν����Ŀ��ڵ��������ڵ���һ�����Ƚϣ�ȡ���
				vo.setBegindate(getChgActionBgnDate(
						getIndutyDate(vo.getPk_psnjob(), context.getPk_org()),
						vo.getPk_psndoc()));
				continue;
			}
			vo.setBegindate(beginDate);
		}
		return vos;
	}
}