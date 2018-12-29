package nc.vo.hi.pub;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.validation.ValidationFailure;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.IPsndocService;
import nc.itf.hr.dataio.IDataIOHookPublic;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pub.tools.HRCMCommonValue;
import nc.pub.tools.HiSQLHelper;
import nc.pub.xml.utils.XmlMatchUtils;
import nc.vo.cp.cpindi.CPIndiGradeVO;
import nc.vo.hi.psndoc.CapaVO;
import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.EduVO;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnChgVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.TrialVO;
import nc.vo.hi.psndoc.WorkVO;
import nc.vo.hr.dataio.DataIOConfigVO;
import nc.vo.hr.dataio.DataIOConst;
import nc.vo.hr.dataio.DataIOResult;
import nc.vo.hr.dataio.DefaultHookPrivate;
import nc.vo.hr.dataio.DefaultHookPublic;
import nc.vo.hr.managescope.HrRelationDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.SuperVOUtil;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.trade.pub.IExAggVO;
import nc.vo.twhr.nhicalc.NhiCalcUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class PsnInfoDataIOHookPrivate extends DefaultHookPrivate {

	/**
	 * ���ݿ�ʼ���ڵõ����¹�����¼ 2013-4-27 ����03:14:18 yunana
	 * 
	 * @param psnjobvos
	 * @return
	 */
	private PsnJobVO getLastJobVO(PsnJobVO[] psnjobvos) {
		PsnJobVO lastJobVO = psnjobvos[0];
		UFLiteralDate begindate = psnjobvos[0].getBegindate();
		for (PsnJobVO psnJobVO : psnjobvos) {
			UFLiteralDate begindateNext = psnJobVO.getBegindate();
			if (begindateNext.after(begindate)) {
				lastJobVO = psnJobVO;
				begindate = begindateNext;
			}
		}
		lastJobVO.setRecordnum(0);
		lastJobVO.setLastflag(UFBoolean.valueOf(true));
		return lastJobVO;
	}

	/**
	 * ���ݿ�ʼ���ڵõ����µ���֯��ϵ��¼ 2013-4-27 ����03:11:26 yunana
	 * 
	 * @param orgs
	 * @return
	 */
	private PsnOrgVO getLastOrgVO(PsnOrgVO[] orgs) {
		PsnOrgVO lastOrgVO = orgs[0];
		UFLiteralDate begindate = orgs[0].getBegindate();
		for (PsnOrgVO orgVO : orgs) {
			UFLiteralDate begindateNext = orgVO.getBegindate();
			if (begindateNext.after(begindate)) {
				lastOrgVO = orgVO;
				begindate = begindateNext;
			}
		}
		lastOrgVO.setLastflag(UFBoolean.valueOf(true));
		return lastOrgVO;
	}

	private ArrayList<SuperVO> removeVOForDelete(SuperVO[] supervos) {
		ArrayList<SuperVO> list = new ArrayList<SuperVO>();
		for (int i = 0; i < supervos.length; i++) {
			if (VOStatus.DELETED != supervos[i].getStatus()) {
				list.add(supervos[i]);
			}
		}
		return list;
	}

	private ArrayList<PsnJobVO> removeJobForDelete(PsnJobVO[] jobs) {
		ArrayList<PsnJobVO> list = new ArrayList<PsnJobVO>();
		for (int i = 0; i < jobs.length; i++) {
			if (VOStatus.DELETED != jobs[i].getStatus()) {
				list.add(jobs[i]);
			}
		}
		return list;
	}

	private ArrayList<PsnOrgVO> removeOrgForDelete(PsnOrgVO[] orgs) {
		ArrayList<PsnOrgVO> list = new ArrayList<PsnOrgVO>();
		for (int i = 0; i < orgs.length; i++) {
			if (VOStatus.DELETED != orgs[i].getStatus()) {
				list.add(orgs[i]);
			}
		}
		return list;
	}

	/**
	 * �����µ����飬���Ұ��տ�ʼ������������ 2013-5-30 ����10:45:40 yunana
	 * 
	 * @param supervos
	 * @return
	 */
	private void sortByBeginDate(SuperVO[] vos) {
		SuperVOUtil.sortByAttributeName(vos, "begindate", true);
	}

	/**
	 * Ϊrecordnum��lastflag��ֵ 2013-5-30 ����11:00:24 yunana
	 * 
	 * @param superVOs
	 */
	private void dealBusiColum(SuperVO[] superVOs) {

		String[] columNames = superVOs[0].getAttributeNames();
		String recordnumStr = "recordnum";
		String lastflag = "lastflag";
		int recordnumInt = 1;
		for (int i = superVOs.length - 1; i >= 0; i--) {

			superVOs[i].setAttributeValue(lastflag, UFBoolean.FALSE);

			if (ArrayUtils.contains(columNames, recordnumStr)) {
				if (superVOs[i].getStatus() == VOStatus.DELETED) {
					continue;
				}
				superVOs[i].setAttributeValue(recordnumStr, recordnumInt);
				recordnumInt++;
			}
		}
		for (int i = superVOs.length - 1; i >= 0; i--) {
			if (superVOs[i].getStatus() != VOStatus.DELETED) {
				superVOs[i].setAttributeValue(lastflag, UFBoolean.TRUE);
			}
		}

	}

	/**
	 * ��Excel�е����ݸ������ݿ��е�����,��������ӦVO״̬ 2013-5-30 ����10:32:46 yunana
	 * 
	 * @param childVOMap
	 * @param dbChildVOMap
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private SuperVO[] combineMapsByUnionKey(LinkedHashMap<String, SuperVO> childVOMap,
			LinkedHashMap<String, SuperVO> dbChildVOMap, int voStatus) {
		Iterator iter = childVOMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Entry) iter.next();
			String key = (String) entry.getKey();
			SuperVO childVO = (SuperVO) entry.getValue();
			SuperVO dbChildVO = dbChildVOMap.get(key);

			if (dbChildVO == null) {
				// ��dbChildVOΪ��ʱ���Դ�childVO�Ĳ���ֻ����������
				childVO.setStatus(VOStatus.NEW);
				dbChildVOMap.put(key, childVO);
			} else {
				// ��dbChildVO��Ϊ��ʱ���Դ�childVO�Ĳ���������ɾ������£���Ҫ��̬����VO״̬
				dbChildVO.setStatus(voStatus);
				for (String name : childVO.getAttributeNames()) {
					Object columValue = childVO.getAttributeValue(name);
					if ("pk_psnorg".equals(name) || "pk_psnjob".equals(name) || "pk_psndoc_sub".equals(name)
							|| "showorder".equals(name)) {
						continue;
					}
					dbChildVO.setAttributeValue(name, columValue);

				}
			}
		}
		Collection<SuperVO> supervos = dbChildVOMap.values();
		return supervos.toArray(new SuperVO[0]);
	}

	/**
	 * ׷�� 2013-5-27 ����02:44:18 yunana
	 * 
	 * @throws BusinessException
	 */
	private void insertImport(PsndocVO psndocVO, PsnOrgVO[] orgs, PsnJobVO[] jobs) throws BusinessException {
		PsnOrgVO[] orgvos = removeOrgForDelete(orgs).toArray(new PsnOrgVO[0]);
		psndocVO.setPsnOrgVO(getLastOrgVO(orgvos));
		PsnJobVO[] jobVOs = removeJobForDelete(jobs).toArray(new PsnJobVO[0]);
		psndocVO.setPsnJobVO(getLastJobVO(jobVOs));
	}

	@Override
	public void saveImportAggVO(DataIOConfigVO dataIOConfigVO, DataIOResult dataIOResult, IExAggVO aggVO)
			throws BusinessException {
		// ����֮ǰ���Ȱ���֯��ϵ�͹�����¼��ӵ���Ա������ϢVO��
		PsndocAggVO psndocAggVO2 = (PsndocAggVO) aggVO;
		if (psndocAggVO2 == null) {
			return;
		}

		PsnJobVO[] jobs = (PsnJobVO[]) psndocAggVO2.getTableVO(PsnJobVO.getDefaultTableName());
		PsnOrgVO[] orgs = (PsnOrgVO[]) psndocAggVO2.getTableVO(PsnOrgVO.getDefaultTableName());
		PartTimeVO[] partTimeVOs = (PartTimeVO[]) psndocAggVO2.getTableVO(PartTimeVO.getDefaultTableName());
		CapaVO[] capaVOs = (CapaVO[]) psndocAggVO2.getTableVO(CapaVO.getDefaultTableName());
		EduVO[] eduVOs = (EduVO[]) psndocAggVO2.getTableVO(EduVO.getDefaultTableName());
		CtrtVO[] ctrtVOs = (CtrtVO[]) psndocAggVO2.getTableVO(CtrtVO.getDefaultTableName());
		CertVO[] certVOs = (CertVO[]) psndocAggVO2.getTableVO(CertVO.getDefaultTableName());
		PsnChgVO[] psnChgVOs = (PsnChgVO[]) psndocAggVO2.getTableVO(PsnChgVO.getDefaultTableName());
		TrialVO[] trialVOs = (TrialVO[]) psndocAggVO2.getTableVO(TrialVO.getDefaultTableName());

		psndocAggVO2.getParentVO().setIscadre(UFBoolean.FALSE);
		psndocAggVO2.getParentVO().setIshisleader(UFBoolean.FALSE);
		PsndocVO psndocVO = psndocAggVO2.getParentVO();// ��VO
		String pk_psndoc = psndocAggVO2.getParentVO().getPk_psndoc();
		// ������¼ֻ�����һ���Ľ�������Ϊnull�������Ķ���ֵ
		if (!ArrayUtils.isEmpty(jobs)) {
			sortByBeginDate(jobs);
			for (int i = 0; i < jobs.length; i++) {
				if (jobs[i].getEnddate() == null && i != jobs.length - 1) {
					// ���һ������Ҫ����
					jobs[i].setEnddate(jobs[i + 1].getBegindate().getDateBefore(1));
				}
			}

		}

		// �����֯��ϵ�Ӽ�Ϊ�գ��������֯��ϵ�Ӽ�
		if (ArrayUtils.isEmpty(orgs)) {
			sortByBeginDate(jobs);
			orgs = new PsnOrgVO[1];
			orgs[0] = new PsnOrgVO();
			orgs[0].setStatus(VOStatus.NEW);
			provideForPsnOrgByPsndocVOAndPsnjobs(orgs, psndocVO, jobs);
			psndocAggVO2.setTableVO("hi_psnorg", orgs);
		}

		// ������֤���Ӽ�Ϊ�գ���������֤���Ӽ�
		if (ArrayUtils.isEmpty(certVOs)) {
			certVOs = new CertVO[1];
			certVOs[0] = new CertVO();
			certVOs[0].setStatus(VOStatus.NEW);
			provideForPsnCertByPsndocVO(certVOs, psndocVO);
			psndocAggVO2.setTableVO("hi_psndoc_cert", certVOs);
		}

		// ������֯��ϵ��ת����Ա����Ϊ�棬������������
		if (ArrayUtils.isEmpty(psnChgVOs) && orgs[orgs.length - 1].getIndocflag().booleanValue()) {
			sortByBeginDate(jobs);
			psnChgVOs = new PsnChgVO[1];
			psnChgVOs[0] = new PsnChgVO();
			psnChgVOs[0].setStatus(VOStatus.NEW);
			providForPsnChgByPsnjobVO(psnChgVOs, jobs[jobs.length - 1]);
			psndocAggVO2.setTableVO("hi_psndoc_psnchg", psnChgVOs);
		}
		/** ����״̬ */
		int importStatus = dataIOConfigVO.getImportMode();
		if (DataIOConst.IMPORT_MODE_ADD == importStatus) {
			// ׷��
			insertImport(psndocVO, orgs, jobs);
		}

		else if (DataIOConst.IMPORT_MODE_UPDATE == importStatus) {
			// ����
			if (psndocVO.getStatus() == VOStatus.NEW) {
				insertImport(psndocVO, orgs, jobs);
			}
			psndocVO.setPsnOrgVO(orgs[orgs.length - 1]);
			psndocVO.setPsnJobVO(jobs[jobs.length - 1]);
		} else {
			// ɾ��+׷��
			if (psndocVO.getStatus() == VOStatus.NEW) {
				insertImport(psndocVO, orgs, jobs);
			}
			psndocVO.setPsnOrgVO(orgs[orgs.length - 1]);
			psndocVO.setPsnJobVO(jobs[jobs.length - 1]);
		}
		String pk_dept = psndocAggVO2.getParentVO().getPsnJobVO().getPk_dept();
		// ����û����ʾί�еĲ��ţ��˷����鲻��������Դ��֯����ע�����д���
		// String pk_hrorg =
		// ManagescopeFacade.queryHrOrgsByDeptAndBusiregion(pk_dept,
		// ManagescopeBusiregionEnum.psndoc)[0];
		String pk_hrorg = getPk_hrorgbydeptPk(pk_dept);
		if (DataIOConst.IMPORT_MODE_ADD == importStatus) {
			psndocAggVO2.getParentVO().setPk_org(psndocAggVO2.getParentVO().getPsnJobVO().getPk_org());
			psndocAggVO2.getParentVO().setPk_hrorg(pk_hrorg);
		}
		defaultValueForPsnorgs(orgs, pk_hrorg, pk_psndoc);
		defaultValueForPsnjobs(jobs, pk_hrorg, pk_psndoc, importStatus);
		defaultValueForPartimes(psndocAggVO2, partTimeVOs, pk_hrorg, pk_psndoc);
		defaultValueForCapas(capaVOs);
		defaultValueForEdus(eduVOs);
		defaultValueForCtrts(ctrtVOs);
		defaultValueForTrials(trialVOs);
		defaultValueForOtherVOs(psndocAggVO2, pk_hrorg);// ͬʱ���ü�ͥ��ַ

		// DataIOResult dataIOResult = null;
		try {
			if (DataIOConst.IMPORT_MODE_UPDATE == importStatus || DataIOConst.IMPORT_MODE_COVER == importStatus) {
				PsndocAggVO psndocinfo = NCLocator.getInstance().lookup(IPsndocQryService.class)
						.queryPsndocVOByPk(psndocAggVO2.getParentVO().getPk_psndoc());
				if (psndocinfo == null) {
					dataIOResult.addValidationFailure(dataIOConfigVO.getExcelVO().getSheetVO("bd_psndoc")
							.getSheetName(), dataIOConfigVO.getVOIndex().get(psndocAggVO2.getParentVO()),
							PsndocVO.CODE,
							new ValidationFailure(ResHelper.getString("6001dataimport", "06001dataimport0090")
							/* @res "����Ψһ��Ӧ�����ݲ����ڣ�" */));
					return;
				}
				Object photo = psndocinfo.getParentVO().getPhoto();
				Object previewphoto = psndocinfo.getParentVO().getPreviewphoto();
				psndocAggVO2.getParentVO().setPhoto(photo);
				psndocAggVO2.getParentVO().setPreviewphoto(previewphoto);
			}
			// NCLocator.getInstance().lookup(IPsndocService.class).savePsndoc(psndocAggVO2,
			// true);
			NCLocator.getInstance().lookup(IPsndocService.class).savePsndocForImport(psndocAggVO2);
		} catch (BusinessException e) {
			getExceptionInfo(e, dataIOConfigVO, psndocAggVO2, dataIOResult);
		}

	}

	private void getExceptionInfo(BusinessException e, DataIOConfigVO dataIOConfigVO, PsndocAggVO psndocAggVO,
			DataIOResult dataIOResult) {
		String errorCode = e.getErrorCodeString();
		if ("coderepeat".equals(errorCode)) {
			// ��Ա�����ظ�
			dataIOResult.addValidationFailure(dataIOConfigVO.getExcelVO().getSheetVO("bd_psndoc").getSheetName(),
					dataIOConfigVO.getVOIndex().get(psndocAggVO.getParentVO()), PsndocVO.CODE,
					new ValidationFailure(e.getMessage()));

		} else if ("clerkcoderepeat".equals(errorCode)) {
			// Ա�����ظ�
			dataIOResult.addValidationFailure(dataIOConfigVO.getExcelVO().getSheetVO("bd_psndoc").getSheetName(),
					dataIOConfigVO.getVOIndex().get(psndocAggVO.getParentVO().getPsnJobVO()), PsnJobVO.CLERKCODE,
					new ValidationFailure(e.getMessage()));
		} else {
			int rowIndex = dataIOConfigVO.getVOIndex().get(psndocAggVO.getParentVO()) == null ? 5 : dataIOConfigVO
					.getVOIndex().get(psndocAggVO.getParentVO());
			dataIOResult.addValidationFailure(dataIOConfigVO.getExcelVO().getSheetVO("bd_psndoc").getSheetName(),
					rowIndex, PsndocVO.CODE, new ValidationFailure(e.getMessage()));
		}
	}

	private void privideForDefaultVOs(PsndocVO psndocVO, PsnOrgVO[] orgs, PsnJobVO[] jobs, CertVO[] certVOs,
			PsnChgVO[] psnChgVOs) {
		if (ArrayUtils.isEmpty(orgs)) {
			sortByBeginDate(jobs);
			provideForPsnOrgByPsndocVOAndPsnjobs(orgs, psndocVO, jobs);
		}
		if (ArrayUtils.isEmpty(certVOs)) {
			provideForPsnCertByPsndocVO(certVOs, psndocVO);
		}
		if (ArrayUtils.isEmpty(psnChgVOs)) {
			sortByBeginDate(jobs);
			providForPsnChgByPsnjobVO(psnChgVOs, jobs[jobs.length - 1]);
		}
	}

	private void provideForPsnOrgByPsndocVOAndPsnjobs(PsnOrgVO[] orgs, PsndocVO psndocVO, PsnJobVO[] jobs) {
		PsnJobVO job = jobs[jobs.length - 1];
		PsnJobVO firstJob = jobs[0];

		orgs[0].setBegindate(firstJob.getBegindate());
		orgs[0].setPk_org(job.getPk_org());
		orgs[0].setPk_group(job.getPk_group());
		orgs[0].setIndocflag(UFBoolean.valueOf(true));
		orgs[0].setPsntype(job.getPsntype());
		if (job.getTrnsevent() == 4) {
			orgs[0].setEndflag(UFBoolean.TRUE);
			UFLiteralDate orgEndDate = job.getBegindate().getDateBefore(1);
			orgs[0].setEnddate(orgEndDate);
		}

	}

	private void provideForPsnCertByPsndocVO(CertVO[] certVOs, PsndocVO psndocVO) {

		certVOs[0].setIdtype(psndocVO.getIdtype());
		certVOs[0].setId(psndocVO.getId());
		certVOs[0].setIseffect(UFBoolean.valueOf(true));
		certVOs[0].setIsstart(UFBoolean.valueOf(true));
		certVOs[0].setPk_org(psndocVO.getPk_org());
		certVOs[0].setPk_group(psndocVO.getPk_group());
		certVOs[0].setStatus(VOStatus.NEW);
	}

	private void providForPsnChgByPsnjobVO(PsnChgVO[] psnChgVOs, PsnJobVO job) {

		psnChgVOs[0].setBegindate(job.getBegindate());
		psnChgVOs[0].setPk_org(job.getPk_org());
		psnChgVOs[0].setPk_group(job.getPk_group());
		String pk_corp = null;
		try {
			pk_corp = HiSQLHelper.getPkCorpByPkOrg(job.getPk_org());
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}
		psnChgVOs[0].setPk_corp(pk_corp);
		psnChgVOs[0].setStatus(VOStatus.NEW);
	}

	/**
	 * ͨ�����������������������Դ��֯ 2013-6-5 ����04:17:03 yunana
	 * 
	 * @param pk_dept
	 * @return
	 * @throws BusinessException
	 */
	private String getPk_hrorgbydeptPk(String pk_dept) throws BusinessException {
		String condition = " pk_dept='" + pk_dept + "' and psndoc_busi = 'Y'";
		HrRelationDeptVO[] hrRelationDeptVO = (HrRelationDeptVO[]) NCLocator.getInstance()
				.lookup(IPersistenceRetrieve.class).retrieveByClause(null, HrRelationDeptVO.class, condition);
		return hrRelationDeptVO[0].getPk_hrorg();
	}

	@Override
	protected void importByCover(DataIOConfigVO configVO, IExAggVO aggVO) throws BusinessException {
		// TODO Auto-generated method stub
		// super.importByDeleteAndAppend(configVO, aggVO);
		IDataIOHookPublic hookPublic = DefaultHookPublic.getInstance(configVO.getFunccode());

		Map<SuperVO, Integer> mapVoIndex = configVO.getVOIndex();

		String[] sheetCodes = configVO.getImportSheetCodes();

		IExAggVO[] dbAggVOs = queryAggVOs(configVO,
				configVO.getExcelVO().getSheetVO(configVO.getExcelVO().getMainSheetCode()).getUnionKeies(),
				(SuperVO) aggVO.getParentVO());

		if (ArrayUtils.isEmpty(dbAggVOs)) {
			// ׷��
			return;
		}

		// ɾ��+��ӷ�ʽʱ�����������²�����
		SuperVO dbParentVO = (SuperVO) dbAggVOs[0].getParentVO();
		SuperVO excelParentVO = (SuperVO) aggVO.getParentVO();

		List<String> excludeColumn = hookPublic.getExcludeColumn(excelParentVO.getClass().getName());
		for (String name : excelParentVO.getAttributeNames()) {
			if (excludeColumn != null && excludeColumn.contains(name)) {
				continue;
			}

			dbParentVO.setAttributeValue(name, excelParentVO.getAttributeValue(name));
		}

		dbParentVO.setStatus(VOStatus.UPDATED);
		// ����VO������excel�е��к�
		if (mapVoIndex.containsKey(excelParentVO)) {
			mapVoIndex.put(dbParentVO, mapVoIndex.get(excelParentVO));
			mapVoIndex.remove(excelParentVO);
		}

		aggVO.setParentVO(dbParentVO);
		/** ɾ��+׷�ӵ����Ӽ�ʱ��Ϊ�����Ӽ���Ĭ��ֵ */
		valueForDefaultSheets(sheetCodes, aggVO, dbAggVOs);

		for (String sheetCode : sheetCodes) {
			String[] unionKey = configVO.getExcelVO().getSheetVO(sheetCode).getUnionKeies();

			if (!sheetCode.equals(configVO.getExcelVO().getMainSheetCode())) {
				SuperVO[] childVOs = (SuperVO[]) aggVO.getTableVO(sheetCode);
				if (sheetCode.equals("hi_psnjob") || sheetCode.equals("hi_psnorg")) {

					SuperVO[] dbChildVOs = (SuperVO[]) dbAggVOs[0].getTableVO(sheetCode);
					if (dbChildVOs == null || dbChildVOs.length < 1) {
						// ���ӱ��в�����������ֱ��׷��
						continue;
					}

					SuperVO[] tempVO = null;
					// ��������ֻ���и��²���
					try {
						// ����ҵ���Ӽ�
						if (sheetCode.equals("hi_psnjob")) {
							// ��Ϊ������¼�ͼ�ְ��¼�õ���ͬһ�ű�hi_psnjob,����Ҫ��������
							OperPsnjobVO(sheetCode, childVOs, dbChildVOs, tempVO, aggVO, unionKey, VOStatus.UPDATED);
						} else {
							tempVO = updateBusi(sheetCode, childVOs, dbChildVOs, unionKey, VOStatus.UPDATED);
							aggVO.setTableVO(sheetCode, tempVO);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Logger.error(e.getMessage());
					}
				} else {
					String pk_psndoc = (String) dbParentVO.getAttributeValue("pk_psndoc");
					String deleteSql;
					if (sheetCode.equals("hi_psndoc_parttime")) {
						deleteSql = "delete from hi_psnjob where pk_psndoc = '" + pk_psndoc + "' and ismainjob = 'N'";
					} else {
						deleteSql = "delete from " + sheetCode + " where pk_psndoc = '" + pk_psndoc + "'";

					}
					NCLocator.getInstance().lookup(IPersistenceUpdate.class).executeSQLs(new String[] { deleteSql });
				}
				/*
				 * else
				 * if(sheetCode.equals("hi_psndoc_psnchg")||sheetCode.equals
				 * ("hi_psndoc_ctrt")||sheetCode. equals("hi_psndoc_parttime")){
				 * //ɾ��ҵ���Ӽ� SuperVO[] tempVO = (SuperVO[])
				 * Array.newInstance(configVO.getVOClassByTabCode(sheetCode),
				 * 0); try { if(sheetCode.equals("hi_psndoc_parttime")){
				 * //��Ϊ������¼�ͼ�ְ��¼�õ���ͬһ�ű�hi_psnjob,����Ҫ�������� OperPsnjobVO(sheetCode,
				 * childVOs, dbChildVOs, tempVO, aggVO,
				 * unionKey,VOStatus.DELETED); }else{ ArrayList<SuperVO> voList
				 * = deleteBusi(sheetCode, childVOs, dbChildVOs); SuperVO[]
				 * superVOs = voList.toArray(new SuperVO[0]);
				 *//** ���ݿ�ʼ������������ */
				/*
				 * if(!"hi_psndoc_ctrt".equals(sheetCode)){ //��ͬ��ʼ���ڶ���һ���� ��������
				 * sortByBeginDate(superVOs); }
				 *//** Ϊlastflay��recordnum��ֵ */
				/*
				 * dealBusiColum(superVOs); tempVO = (SuperVO[])
				 * ArrayUtils.addAll(tempVO, superVOs); //tempVO =
				 * updateBusi(sheetCode, childVOs, dbChildVOs,
				 * unionKey,VOStatus.DELETED);
				 * aggVO.setTableVO(sheetCode,tempVO); } } catch (Exception e) {
				 * // TODO Auto-generated catch block
				 * Logger.error(e.getMessage()); } }else{ SuperVO[] tempVO =
				 * (SuperVO[])
				 * Array.newInstance(configVO.getVOClassByTabCode(sheetCode),
				 * 0); ArrayList<SuperVO> voList = deleteBusi(sheetCode,
				 * childVOs, dbChildVOs); SuperVO[] superVOs =
				 * voList.toArray(new SuperVO[0]); tempVO = (SuperVO[])
				 * ArrayUtils.addAll(tempVO, superVOs);
				 * aggVO.setTableVO(sheetCode, tempVO); }
				 */

			}
		}
	}

	/**
	 * ��ɾ����׷�ӷ�ʽ���е���ʱ�����ݿ��в����VO����delete״̬�� �����ϵ�vo����new״̬ 2013-7-17 ����12:59:08
	 * yunana
	 * 
	 * @param sheetCode
	 * @param childVOs
	 * @param dbChildVOs
	 * @return
	 */
	private ArrayList<SuperVO> deleteBusi(String sheetCode, SuperVO[] childVOs, SuperVO[] dbChildVOs) {
		;
		ArrayList<SuperVO> voList = new ArrayList<SuperVO>();
		if (!ArrayUtils.isEmpty(childVOs)) {
			for (int i = 0; i < childVOs.length; i++) {
				childVOs[i].setStatus(VOStatus.NEW);
				voList.add(childVOs[i]);
			}
		}
		if (!ArrayUtils.isEmpty(dbChildVOs)) {
			for (int i = 0; i < dbChildVOs.length; i++) {
				dbChildVOs[i].setStatus(VOStatus.DELETED);
				voList.add(dbChildVOs[i]);
			}
		}

		return voList;

	}

	protected void importByUpdate2(DataIOConfigVO configVO, IExAggVO aggVO) throws BusinessException {
		// TODO Auto-generated method stub
		// super.importByUpdate(configVO, aggVO);
		IDataIOHookPublic hookPublic = DefaultHookPublic.getInstance(configVO.getFunccode());

		Map<SuperVO, Integer> mapVoIndex = configVO.getVOIndex();

		String[] sheetCodes = configVO.getImportSheetCodes();

		IExAggVO[] dbAggVOs = queryAggVOs(configVO,
				configVO.getExcelVO().getSheetVO(configVO.getExcelVO().getMainSheetCode()).getUnionKeies(),
				(SuperVO) aggVO.getParentVO());

		if (ArrayUtils.isEmpty(dbAggVOs)) {
			throw new BusinessException(ResHelper.getString("6001dataimport", "06001dataimport0090")
			/* @res "����Ψһ��Ӧ�����ݲ����ڣ�" */);
		}

		SuperVO dbParentVO = (SuperVO) dbAggVOs[0].getParentVO();
		SuperVO excelParentVO = (SuperVO) aggVO.getParentVO();

		List<String> excludeColumn = hookPublic.getExcludeColumn(excelParentVO.getClass().getName());

		for (String name : excelParentVO.getAttributeNames()) {
			// �ų��������Բ�����
			if (excludeColumn != null && excludeColumn.contains(name)) {
				continue;
			}
			dbParentVO.setAttributeValue(name, excelParentVO.getAttributeValue(name));
		}

		dbParentVO.setStatus(VOStatus.UPDATED);

		// ����VO������excel�е��к�
		if (mapVoIndex.containsKey(excelParentVO)) {
			mapVoIndex.put(dbParentVO, mapVoIndex.get(excelParentVO));
			mapVoIndex.remove(excelParentVO);
		}

		aggVO.setParentVO(dbParentVO);

		/** ���µ����Ӽ�ʱ��Ϊ�����Ӽ���Ĭ��ֵ */
		valueForDefaultSheets(sheetCodes, aggVO, dbAggVOs);

		for (String sheetCode : sheetCodes) {
			if (!sheetCode.equals(configVO.getExcelVO().getMainSheetCode())) {
				String[] unionKey = configVO.getExcelVO().getSheetVO(sheetCode).getUnionKeies();

				// �ӱ�û����������Ψһ��׷�Ӵ���
				if (ArrayUtils.isEmpty(unionKey)) {
					continue;
				}

				SuperVO[] childVOs = (SuperVO[]) aggVO.getTableVO(sheetCode);
				SuperVO[] dbChildVOs = (SuperVO[]) dbAggVOs[0].getTableVO(sheetCode);

				if (ArrayUtils.isEmpty(childVOs) && !ArrayUtils.isEmpty(dbChildVOs)) {
					aggVO.setTableVO(sheetCode, dbChildVOs);
					// �ӱ�Ϊ��ʱ������Ҫ����
					continue;
				}

				if (dbChildVOs == null || dbChildVOs.length < 1) {
					// ���ӱ��в����������򲻸���
					aggVO.setTableVO(sheetCode, null);
					continue;
				}

				List<String> excludeChildColumn = hookPublic.getExcludeColumn(dbChildVOs[0].getClass().getName());

				if (sheetCode.equals("hi_psnjob") || sheetCode.equals("hi_psnorg")
						|| sheetCode.equals("hi_psndoc_psnchg") || sheetCode.equals("hi_psndoc_parttime")) {
					SuperVO[] tempVO = null;
					try {
						// ����ҵ���Ӽ�
						if (sheetCode.equals("hi_psnjob") || sheetCode.equals("hi_psndoc_parttime")) {
							// ��Ϊ������¼�ͼ�ְ��¼�õ���ͬһ�ű�hi_psnjob,����Ҫ��������
							OperPsnjobVO(sheetCode, childVOs, dbChildVOs, tempVO, aggVO, unionKey, VOStatus.UPDATED);
						} else {
							tempVO = updateBusi(sheetCode, childVOs, dbChildVOs, unionKey, VOStatus.UPDATED);
							aggVO.setTableVO(sheetCode, tempVO);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Logger.error(e.getMessage(), e);
					}
				} else if ("hi_psndoc_ctrt".equals(sheetCode)) {
					// ��ͬ�Ӽ���֧�ָ��£�ά��ԭ��
					aggVO.setTableVO(sheetCode, dbChildVOs);
				} else {
					SuperVO[] afterCombineVOs = updateUnBusi(dbChildVOs, childVOs, unionKey, excludeChildColumn,
							mapVoIndex);
					/** ����ҳǩ�����÷������������Ӧ�������� */
					SuperVO[] tempVO = (SuperVO[]) Array.newInstance(configVO.getVOClassByTabCode(sheetCode),
							afterCombineVOs.length);
					for (int i = 0; i < afterCombineVOs.length; i++) {
						tempVO[i] = afterCombineVOs[i];
					}
					aggVO.setTableVO(sheetCode, tempVO);
				}

			}
		}
	}

	/**
	 * �����뷽ʽΪ���£����߸��¼�׷��ʱ�����û����д�ĸ�Ĭ���Ӽ��� ��Ϊ�Ӽ��������ݿ��е�ֵ 2013-7-11 ����02:07:15 yunana
	 * 
	 * @param sheetCodes
	 * @param aggVOForUpdate
	 * @param dbAggVOs
	 */
	private void valueForDefaultSheets(String[] sheetCodes, IExAggVO aggVOForUpdate, IExAggVO[] dbAggVOs) {
		String[] defaultSheetCodes = { "hi_psnjob", "hi_psnorg", "hi_psndoc_cert", "hi_psndoc_psnchg" };
		for (String sheetCode : defaultSheetCodes) {
			if (!ArrayUtils.contains(sheetCodes, sheetCode)) {
				provideValueForSingleSheet(sheetCode, aggVOForUpdate, dbAggVOs);
			}
		}

	}

	/**
	 * Ϊ�����Ӽ���ֵ 2013-7-11 ����02:08:54 yunana
	 * 
	 * @param sheetCode
	 * @param aggVOForUpdate
	 * @param dbAggVOs
	 */
	private void provideValueForSingleSheet(String sheetCode, IExAggVO aggVOForUpdate, IExAggVO[] dbAggVOs) {
		SuperVO[] dbChildVOs = (SuperVO[]) dbAggVOs[0].getTableVO(sheetCode);

		if (ArrayUtils.isEmpty(dbChildVOs) && "hi_psndoc_psnchg".equals(sheetCode)) {
			/** ������������Ϊ�գ�������֯��ϵ����indocflagΪYʱ��Ϊ���������ֵ */
			PsnOrgVO[] orgs = (PsnOrgVO[]) ((PsndocAggVO) aggVOForUpdate).getTableVO("hi_psnorg");
			PsnJobVO[] jobs = (PsnJobVO[]) ((PsndocAggVO) aggVOForUpdate).getTableVO("hi_psnjob");
			if (orgs[orgs.length - 1].getIndocflag().booleanValue()) {
				sortByBeginDate(jobs);
				PsnChgVO[] psnChgVOs = new PsnChgVO[1];
				psnChgVOs[0] = new PsnChgVO();
				psnChgVOs[0].setStatus(VOStatus.NEW);
				providForPsnChgByPsnjobVO(psnChgVOs, jobs[jobs.length - 1]);
				aggVOForUpdate.setTableVO("hi_psndoc_psnchg", psnChgVOs);
			}
			return;
		}
		for (int i = 0; i < dbChildVOs.length; i++) {
			dbChildVOs[i].setStatus(VOStatus.UNCHANGED);
		}
		if ("hi_psnjob".equals(sheetCode)) {
			ArrayList<SuperVO> superVOList = new ArrayList<SuperVO>();
			for (int i = 0; i < dbChildVOs.length; i++) {
				UFBoolean isMainJob = (UFBoolean) dbChildVOs[i].getAttributeValue("ismainjob");
				if (isMainJob.booleanValue()) {
					superVOList.add(dbChildVOs[i]);
				}
			}
			SuperVO[] supervos = superVOList.toArray(new PsnJobVO[0]);
			sortByBeginDate(supervos);
			aggVOForUpdate.setTableVO(sheetCode, supervos);
		} else {
			if ("hi_psnorg".equals(sheetCode) || "hi_psndoc_psnchg".equals(sheetCode)) {
				sortByBeginDate(dbChildVOs);
			}
			aggVOForUpdate.setTableVO(sheetCode, dbChildVOs);
		}
	}

	/**
	 * ����������¼���߼�ְ��¼ 2013-7-4 ����01:50:19 yunana
	 * 
	 * @param sheetCode
	 * @param childVOs
	 * @param dbChildVOs
	 * @param tempVO
	 * @param aggVO
	 * @param unionKey
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void OperPsnjobVO(String sheetCode, SuperVO[] childVOs, SuperVO[] dbChildVOs, SuperVO[] tempVO,
			IExAggVO aggVO, String[] unionKey, int voStatus) throws InstantiationException, IllegalAccessException {
		String[] unionKeyPlus = new String[unionKey.length + 1];
		for (int i = 0; i < unionKey.length; i++) {
			unionKeyPlus[i] = unionKey[i];
		}
		unionKeyPlus[unionKeyPlus.length - 1] = "ismainjob";
		if (sheetCode.equals("hi_psnjob")) {
			SuperVO[] dbMainChildVOs = getMainJobVOs(dbChildVOs);
			tempVO = updateBusi(sheetCode, childVOs, dbMainChildVOs, unionKeyPlus, VOStatus.UPDATED);
			aggVO.setTableVO(sheetCode, tempVO);
		} else {
			// ��ְ��¼
			SuperVO[] dbPartTimeChildVOs = getPartTimeJobVOs(dbChildVOs);
			for (int i = 0; i < childVOs.length; i++) {
				SuperVO superVO = childVOs[i];
				superVO.setAttributeValue("ismainjob", UFBoolean.FALSE);
			}
			if (VOStatus.DELETED == voStatus) {
				// ��ְ��¼
				ArrayList<SuperVO> superVOList = deleteBusi(sheetCode, childVOs, dbPartTimeChildVOs);
				PartTimeVO[] partTimeVOs = new PartTimeVO[superVOList.size()];
				for (int i = 0; i < partTimeVOs.length; i++) {
					partTimeVOs[i] = (PartTimeVO) superVOList.get(i);
				}
				aggVO.setTableVO(sheetCode, partTimeVOs);
			} else {
				tempVO = updateBusi(sheetCode, childVOs, dbPartTimeChildVOs, unionKeyPlus, voStatus);
				aggVO.setTableVO(sheetCode, tempVO);
			}
		}
	}

	private SuperVO[] getMainJobVOs(SuperVO[] dbChildVOs) {
		ArrayList<SuperVO> list = new ArrayList<SuperVO>();
		for (int i = 0; i < dbChildVOs.length; i++) {
			UFBoolean ismainjob = (UFBoolean) dbChildVOs[i].getAttributeValue("ismainjob");
			if (ismainjob.booleanValue()) {
				list.add(dbChildVOs[i]);
			}
		}
		return list.toArray(new SuperVO[0]);
	}

	private SuperVO[] getPartTimeJobVOs(SuperVO[] dbChildVOs) {
		ArrayList<SuperVO> list = new ArrayList<SuperVO>();
		for (int i = 0; i < dbChildVOs.length; i++) {
			UFBoolean ismainjob = (UFBoolean) dbChildVOs[i].getAttributeValue("ismainjob");
			if (!ismainjob.booleanValue()) {
				list.add(dbChildVOs[i]);
			}
		}
		return list.toArray(new SuperVO[0]);
	}

	/**
	 * �������³���: ���� ����+׷�� ɾ��+׷�� 2013-5-30 ����01:18:05 yunana
	 * 
	 * @param sheetCode
	 * @param childVOs
	 * @param dbChildVOs
	 * @param unionKey
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private SuperVO[] updateBusi(String sheetCode, SuperVO[] childVOs, SuperVO[] dbChildVOs, String[] unionKey,
			int vostatus) throws InstantiationException, IllegalAccessException {
		/** ��Excel������Ϊ�������ݣ�����Ψһ�ֶ�String��Ӻ�ΪKEY,superVOΪVALUE */
		LinkedHashMap<String, SuperVO> childVOMap = makeUnionKeyMap(unionKey, childVOs, VOStatus.UPDATED);// VO״̬������������
		/** �����ݿ��в鵽������Ϊ�������ݣ�����Ψһ�ֶ�String��Ӻ�ΪKEY,superVOΪVALUE */
		LinkedHashMap<String, SuperVO> dbChildVOMap = makeUnionKeyMap(unionKey, dbChildVOs, VOStatus.UPDATED);
		/** �ý����ϵ���Ҫ���µ��ֶ��滻���ݿ��е����� */
		SuperVO[] dbVOsAfterReplace = combineMapsByUnionKey(childVOMap, dbChildVOMap, vostatus);
		/** ���ݿ�ʼ������������ */
		sortByBeginDate(dbVOsAfterReplace);
		if (!"hi_psnjob".equals(sheetCode)) // ����ǹ�����¼��֮���lastflag��recordnum��ֵ
		{
			/** Ϊlastflay��recordnum��ֵ */
			dealBusiColum(dbVOsAfterReplace);
		}
		if (sheetCode.equals("hi_psnjob")) {
			PsnJobVO[] psnJobvos = new PsnJobVO[dbVOsAfterReplace.length];
			for (int i = 0; i < dbVOsAfterReplace.length; i++) {
				psnJobvos[i] = (PsnJobVO) dbVOsAfterReplace[i];
			}
			return psnJobvos;
		} else if (sheetCode.equals("hi_psnorg")) {
			PsnOrgVO[] psnOrgvos = new PsnOrgVO[dbVOsAfterReplace.length];
			for (int i = 0; i < psnOrgvos.length; i++) {
				psnOrgvos[i] = (PsnOrgVO) dbVOsAfterReplace[i];
			}
			return psnOrgvos;
		} else if (sheetCode.equals("hi_psndoc_psnchg")) {
			PsnChgVO[] psnChgVOs = new PsnChgVO[dbVOsAfterReplace.length];
			for (int i = 0; i < psnChgVOs.length; i++) {
				psnChgVOs[i] = (PsnChgVO) dbVOsAfterReplace[i];
			}
			return psnChgVOs;
		} else if (sheetCode.equals("hi_psndoc_ctrt")) {
			// ��ͬ
			CtrtVO[] ctrtVOs = new CtrtVO[dbVOsAfterReplace.length];
			for (int i = 0; i < ctrtVOs.length; i++) {
				ctrtVOs[i] = (CtrtVO) dbVOsAfterReplace[i];
			}
			return ctrtVOs;
		} else {
			// ��ְ��¼
			PartTimeVO[] partTimeVOs = new PartTimeVO[dbVOsAfterReplace.length];
			for (int i = 0; i < partTimeVOs.length; i++) {
				partTimeVOs[i] = (PartTimeVO) dbVOsAfterReplace[i];
			}
			return partTimeVOs;
		}
		// return dbVOsAfterReplace;
	}

	/**
	 * ���·�ҵ���Ӽ� 2013-5-30 ����09:48:52 yunana
	 * 
	 * @param dbChildVOs
	 * @param childVOs
	 * @param unionKey
	 * @param excludeChildColumn
	 * @param mapVoIndex
	 *            return ��Ϻ��supervo
	 */
	private SuperVO[] updateUnBusi(SuperVO[] dbChildVOs, SuperVO[] childVOs, String[] unionKey,
			List<String> excludeChildColumn, Map<SuperVO, Integer> mapVoIndex) {

		/** ��Excel������Ϊ�������ݣ�����Ψһ�ֶ�String��Ӻ�ΪKEY,superVOΪVALUE */
		LinkedHashMap<String, SuperVO> childVOMap = makeUnionKeyMap(unionKey, childVOs, VOStatus.UPDATED);// VO״̬������������
		/** �����ݿ��в鵽������Ϊ�������ݣ�����Ψһ�ֶ�String��Ӻ�ΪKEY,superVOΪVALUE */
		LinkedHashMap<String, SuperVO> dbChildVOMap = makeUnionKeyMap(unionKey, dbChildVOs, VOStatus.UPDATED);
		/** �ý����ϵ���Ҫ���µ��ֶ��滻���ݿ��е����� */
		SuperVO[] dbVOsAfterReplace = combineMapsByUnionKey(childVOMap, dbChildVOMap, VOStatus.UPDATED);
		// for (int i = 0; i < dbVOsAfterReplace.length; i++) {
		// tempVO[i] = dbVOsAfterReplace[i];
		// }
		return dbVOsAfterReplace;

		// for (SuperVO childVO : childVOs) {
		// // �ӱ��д������ݣ���Ѵ��ڵ����ݸ��¡�
		// for (SuperVO dbChildVO : dbChildVOs) {
		// int index;
		// for (index = 0; index < unionKey.length; index++) {
		// if (!childVO.getAttributeValue(unionKey[index]).equals(
		// dbChildVO.getAttributeValue(unionKey[index]))) {
		// break;
		// }
		// }
		// if (index == unionKey.length) {
		//
		// for (String name : childVO.getAttributeNames()) {
		// // �ų��������Բ�����
		// if (excludeChildColumn != null
		// && excludeChildColumn.contains(name)) {
		// continue;
		// }
		// dbChildVO.setAttributeValue(name,
		// childVO.getAttributeValue(name));
		// }
		// // ����VO������excel�е��к�
		// if (mapVoIndex.containsKey(childVO)) {
		// mapVoIndex.put(dbChildVO, mapVoIndex.get(childVO));
		// mapVoIndex.remove(childVO);
		// }
		// dbChildVO.setStatus(VOStatus.UPDATED);
		// tempVO = (SuperVO[]) ArrayUtils.add(tempVO, dbChildVO);
		// break;
		// }
		// }
		// }
		// return tempVO;
	}

	@Override
	protected IExAggVO[] queryAggVOs(DataIOConfigVO configVO, String[] strUnionKeys, SuperVO parentVO)
			throws BusinessException {
		IExAggVO[] retVOs = super.queryAggVOs(configVO, strUnionKeys, parentVO);
		if (retVOs != null) {
			String[] LABORINS = NhiCalcUtils.getLaborInsEncryptionAttributes();
			String[] HEALTHINS = NhiCalcUtils.getHealthInsEncryptionAttributes();
			String[] LABORDETAIL = NhiCalcUtils.getNhiDetailEncryptionAttributes();
			String[] LABORSUM = NhiCalcUtils.getNhiSumEncryptionAttributes();
			String[] GROUPINS = NhiCalcUtils.getGroupInsEncryptionAttributes();
			String[] EXTHEALTHINS = NhiCalcUtils.getExtHealthEncryptionAttributes();

			for (IExAggVO retvo : retVOs) {
				if (retvo != null && retvo.getAllChildrenVO() != null) {
					for (CircularlyAccessibleValueObject vo : retvo.getAllChildrenVO()) {
						if (vo instanceof SuperVO) {
							String tableName = ((SuperVO) vo).getTableName();
							// ���ݱ���ȥxml�ļ��в��Ҷ�Ӧ�ı�������Ӧ���ֶν���
							String xmlInfo = XmlMatchUtils.getXmlInfo(tableName);
							String[] fieldNames = null;
							if (xmlInfo.equals("LABORINS")) {
								fieldNames = LABORINS;
							} else if (xmlInfo.equals("HEALTHINS")) {
								fieldNames = HEALTHINS;
							} else if (xmlInfo.equals("LABORDETAIL")) {
								fieldNames = LABORDETAIL;
							} else if (xmlInfo.equals("LABORSUM")) {
								fieldNames = LABORSUM;
							} else if (xmlInfo.equals("GROUPINS")) {
								fieldNames = GROUPINS;
							} else if (xmlInfo.equals("EXTHEALTHINS")) {
								fieldNames = EXTHEALTHINS;
							}

							if (fieldNames != null) {
								for (String glbdef : fieldNames) {
									decryptSubInfoData((SuperVO) vo, glbdef);
								}
							}
						}
					}
				}
			}
		}
		return retVOs;
	}

	private void decryptSubInfoData(SuperVO vo, String glbdef) {
		double dv = 0;
		if (vo.getAttributeValue(glbdef) != null) {
			UFDouble value = (UFDouble) vo.getAttributeValue(glbdef);
			dv = value.doubleValue();
		}
		double decrypt = (dv == 0 ? 0.0 : SalaryDecryptUtil.decrypt(dv));
		vo.setAttributeValue(glbdef, decrypt);
	}

	@Override
	protected void importByUpdate(DataIOConfigVO configVO, IExAggVO aggVO) throws BusinessException {
		// TODO Auto-generated method stub
		IDataIOHookPublic hookPublic = DefaultHookPublic.getInstance(configVO.getFunccode());

		Map<SuperVO, Integer> mapVoIndex = configVO.getVOIndex();

		String[] sheetCodes = configVO.getImportSheetCodes();

		IExAggVO[] dbAggVOs = queryAggVOs(configVO,
				configVO.getExcelVO().getSheetVO(configVO.getExcelVO().getMainSheetCode()).getUnionKeies(),
				(SuperVO) aggVO.getParentVO());

		if (dbAggVOs == null || dbAggVOs.length < 1) {
			// ׷��
			return;
		}
		// �����о�Ժ�����Զ����Ӽ����¼�¼����
		PsndocAggVO psnAggVo = (PsndocAggVO) dbAggVOs[0];
		PsndocAggVO psnAggVoimport = (PsndocAggVO) aggVO;
		List<SuperVO> importlist = new ArrayList<SuperVO>();

		PsndocVO mainvo = psnAggVo.getParentVO();
		if (StringUtils.isEmpty(mainvo.getId()) || StringUtils.isEmpty(mainvo.getIdtype())) {
			throw new BusinessException(ResHelper.getString("6007psn", "06007psn0521")/*
																					 * @
																					 * res
																					 * "��������Ա��֤����ϢΪ�գ���ȥҵ��ڵ㲹��"
																					 */);
		}

		for (int i = 1; i < 9; i++) {
			SuperVO[] importchildVOs = psnAggVoimport.getTableVO("hi_psndoc_glbdef" + i);
			// ��������Ҷǩ��û�����ݾͲ����޸�����
			if (importchildVOs == null || importchildVOs.length == 0) {
				continue;
			}
			// ���տ�ʼ������������
			SuperVOUtil.sortByAttributeName(importchildVOs, "begindate", true);
			importlist.clear();
			SuperVO[] dbchildVOs = psnAggVo.getTableVO("hi_psndoc_glbdef" + i);
			if (!ArrayUtils.isEmpty(dbchildVOs)) {
				for (int j = 0; j < dbchildVOs.length; j++) {
					SuperVO superVO = dbchildVOs[j];
					superVO.setAttributeValue("lastflag", "N");
					importlist.add(superVO);
				}
			}

			for (int k = 0; k < importchildVOs.length; k++) {
				SuperVO superVO = importchildVOs[k];
				if (k == importchildVOs.length - 1) {
					superVO.setAttributeValue("lastflag", "Y");
				} else {
					superVO.setAttributeValue("lastflag", "N");
				}
				importlist.add(superVO);
			}
			psnAggVoimport.setTableVO("hi_psndoc_glbdef" + i, importlist.toArray(new SuperVO[0]));
		}
		aggVO = psnAggVoimport;

		SuperVO dbParentVO = (SuperVO) dbAggVOs[0].getParentVO();
		SuperVO excelParentVO = (SuperVO) aggVO.getParentVO();
		List<String> excludeColumn = hookPublic.getExcludeColumn(excelParentVO.getClass().getName());
		for (String name : excelParentVO.getAttributeNames()) {
			// �ų��������Բ�����
			if (excludeColumn != null && excludeColumn.contains(name)) {
				continue;
			}
			dbParentVO.setAttributeValue(name, excelParentVO.getAttributeValue(name));
		}

		dbParentVO.setStatus(VOStatus.UPDATED);
		// ����VO������excel�е��к�
		if (mapVoIndex.containsKey(excelParentVO)) {
			mapVoIndex.put(dbParentVO, mapVoIndex.get(excelParentVO));
			mapVoIndex.remove(excelParentVO);
		}
		aggVO.setParentVO(dbParentVO);

		/** ����+׷�ӵ����Ӽ�ʱ��Ϊ�����Ӽ���Ĭ��ֵ */
		valueForDefaultSheets(sheetCodes, aggVO, dbAggVOs);

		for (String sheetCode : sheetCodes) {

			if (!sheetCode.equals(configVO.getExcelVO().getMainSheetCode())) {
				String[] unionKey = configVO.getExcelVO().getSheetVO(sheetCode).getUnionKeies();
				// �ӱ�û����������Ψһ��׷�Ӵ���
				if (ArrayUtils.isEmpty(unionKey)) {
					continue;
				}
				SuperVO[] childVOs = (SuperVO[]) aggVO.getTableVO(sheetCode);
				SuperVO[] dbChildVOs = (SuperVO[]) dbAggVOs[0].getTableVO(sheetCode);

				if (ArrayUtils.isEmpty(childVOs) && !ArrayUtils.isEmpty(dbChildVOs)) {
					aggVO.setTableVO(sheetCode, dbChildVOs);
					// �ӱ�Ϊ��ʱ������Ҫ����
					continue;
				}

				if (dbChildVOs == null || dbChildVOs.length < 1) {
					// ���ӱ��в�����������׷��,��ͬ����
					if ("hi_psndoc_ctrt".equals(sheetCode)) {
						aggVO.setTableVO(sheetCode, null);
					}
					continue;
				}
				List<String> excludeChildColumn = hookPublic.getExcludeColumn(dbChildVOs[0].getClass().getName());

				if (sheetCode.equals("hi_psnjob") || sheetCode.equals("hi_psnorg")
						|| sheetCode.equals("hi_psndoc_psnchg") || sheetCode.equals("hi_psndoc_parttime")) {
					SuperVO[] tempVO = null;
					try {
						// ����ҵ���Ӽ�
						if (sheetCode.equals("hi_psnjob") || sheetCode.equals("hi_psndoc_parttime")) {
							// ��Ϊ������¼�ͼ�ְ��¼�õ���ͬһ�ű�hi_psnjob,����Ҫ��������
							OperPsnjobVO(sheetCode, childVOs, dbChildVOs, tempVO, aggVO, unionKey, VOStatus.UPDATED);
						} else {
							tempVO = updateBusi(sheetCode, childVOs, dbChildVOs, unionKey, VOStatus.UPDATED);
							aggVO.setTableVO(sheetCode, tempVO);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Logger.error(e.getMessage(), e);
					}
				} else if ("hi_psndoc_ctrt".equals(sheetCode)) {
					// ��ͬ�Ӽ���֧�ָ���+׷��
					aggVO.setTableVO(sheetCode, dbChildVOs);
				} else {
					SuperVO[] afterCombineVOs = updateUnBusi(dbChildVOs, childVOs, unionKey, excludeChildColumn,
							mapVoIndex);
					/** ����ҳǩ�����÷������������Ӧ�������� */
					SuperVO[] tempVO = (SuperVO[]) Array.newInstance(configVO.getVOClassByTabCode(sheetCode),
							afterCombineVOs.length);
					for (int i = 0; i < afterCombineVOs.length; i++) {
						tempVO[i] = afterCombineVOs[i];
					}
					aggVO.setTableVO(sheetCode, tempVO);
				}

			}
		}

	}

	private LinkedHashMap<String, SuperVO> makeUnionKeyMap(String[] unionKeys, SuperVO[] supervos, int voStatus) {
		LinkedHashMap<String, SuperVO> map = new LinkedHashMap<String, SuperVO>();
		for (SuperVO superVO : supervos) {
			StringBuilder sb = new StringBuilder();
			for (String key : unionKeys) {
				Object obj = superVO.getAttributeValue(key);
				sb.append(obj.toString());
			}
			String key = sb.toString();
			superVO.setStatus(voStatus);
			map.put(key, superVO);
		}
		return map;
	}

	/**
	 * Ϊ��֯��ϵ�ṩĬ��ֵ 2013-5-27 ����03:42:34 yunana ���Ϊ����֯������Ҫ¼����֯��ϵ������������Դ��֯
	 * 
	 * @param orgvos
	 * @param pk_hrorg
	 * @param pk_psndoc
	 */
	private void defaultValueForPsnorgs(PsnOrgVO[] orgs, String pk_hrorg, String pk_psndoc) {
		// ���ֻ��һ����֯��ϵ�����Ӧ��ǰ��������Դ��֯
		if (orgs.length == 1) {
			orgs[0].setPk_hrorg(pk_hrorg);
		}

		int orgrelaid = 1;
		for (int i = 0; i < orgs.length; i++) {
			// orgs[i].setPk_hrorg(pk_hrorg);
			orgs[i].setPk_psndoc(pk_psndoc);
			orgs[i].setOrgrelaid(orgrelaid);
			orgs[i].setPsntype(0);
			if (i != (orgs.length - 1)) {
				orgs[i].setLastflag(UFBoolean.FALSE);
			}
			orgrelaid++;
		}
	}

	/**
	 * Ϊ������¼�ṩĬ��ֵ 2013-5-27 ����03:41:40 yunana
	 * 
	 * @param jobs
	 * @param pk_hrorg
	 * @param pk_psndoc
	 */
	private void defaultValueForPsnjobs(PsnJobVO[] jobs, String pk_hrorg, String pk_psndoc, int importStatus)
			throws BusinessException {
		jobs[jobs.length - 1].setLastflag(UFBoolean.TRUE);

		int recordnum = 1;
		for (int i = 0; i < jobs.length; i++) {
			jobs[i].setPk_hrorg(getPk_hrorgbydeptPk(jobs[i].getPk_dept()));
			jobs[i].setAssgid(1);
			if (DataIOConst.IMPORT_MODE_ADD == importStatus) {
				jobs[i].setShoworder(9999999);
			}
			if (DataIOConst.IMPORT_MODE_UPDATE == importStatus && jobs[i].getStatus() == VOStatus.NEW) {
				jobs[i].setShoworder(9999999);
			}
			jobs[i].setPk_psndoc(pk_psndoc);
			jobs[i].setPsntype(0);
			jobs[i].setPk_hrgroup(jobs[i].getPk_group());
			if (jobs[i].getPoststat() == null) {
				jobs[i].setPoststat(UFBoolean.TRUE);
			}

			if (i > 0 && !jobs[i].getPk_org().equals(jobs[i - 1].getPk_org())) {
				jobs[i - 1].setLastflag(UFBoolean.valueOf(true)); // ������¼������֯�����һ�������£�������¼�����±�ʶ
			}

			if (jobs[i].getLastflag() != null && jobs[i].getLastflag().booleanValue()) {
				if (jobs[i].getTrnsevent() == 4/* �춯�¼�Ϊ��ְ */) {
					jobs[i].setEndflag(UFBoolean.TRUE);
				}
			} else {
				jobs[i].setLastflag(UFBoolean.valueOf(false));
				jobs[i].setRecordnum(recordnum);
				jobs[i].setEndflag(UFBoolean.TRUE);// ����������µĹ�����¼����˵���Ѿ�����
				recordnum++;
			}
		}
	}

	private void defaultValueForTrials(TrialVO[] trialVOs) {
		if (ArrayUtils.isEmpty(trialVOs)) {
			return;
		}
		for (int i = 0; i < trialVOs.length; i++) {
			TrialVO trialVO = trialVOs[i];
			Integer result = trialVO.getTrialresult();
			if (result == null || result == 2) {
				trialVO.setEndflag(UFBoolean.FALSE);
			} else {
				trialVO.setEndflag(UFBoolean.TRUE);
			}

		}
	}

	private void defaultValueForCtrts(CtrtVO[] ctrtVOs) {
		if (ArrayUtils.isEmpty(ctrtVOs)) {
			return;
		}
		int contid = 1;// ��ͬid
		int continuetime = 1;
		Integer conttype;
		// ��EXCEL�����˳��Ϊ��ͬid��ֵ��ÿ������ǩ��ʱ��ͬid��1
		for (int i = 0; i < ctrtVOs.length; i++) {
			// ������ͬ��¼�ĺ�ͬ����
			conttype = ctrtVOs[i].getConttype();
			/*
			 * continuetime�Ĺ����ǣ� ��1��ǩ��ʱΪ1�� ��2�����Ϊ��һ����ֵ�����Ϊ��һ����Ϊ1�� ��3����ֹ���������Ϊ0��
			 * ��4����ǩ�����Ϊ�ջ���Ϊ��һ��+1�����Ϊ��һ����¼��ֵΪ2�������ֵ���Ҳ�Ϊ��һ�����򰴵�ǰֵΪ׼
			 */
			if (conttype == HRCMCommonValue.CONTTYPE_MAKE) {
				// ǩ��
				continuetime = 1;
				ctrtVOs[i].setContinuetime(continuetime);
			} else if (conttype == HRCMCommonValue.CONTTYPE_RELEASE || conttype == HRCMCommonValue.CONTTYPE_FINISH) {
				// ��ֹ���
				continuetime = 0;
				ctrtVOs[i].setContinuetime(continuetime);
			} else if (conttype == HRCMCommonValue.CONTTYPE_CHANGE) {
				// ���
				ctrtVOs[i].setContinuetime(continuetime);
			} else {
				// ��ǩ
				if (ctrtVOs[i].getContinuetime() == null || i == 0) {

					if (conttype == HRCMCommonValue.CONTTYPE_EXTEND) {
						continuetime++;
					}
					ctrtVOs[i].setContinuetime(continuetime);

				} else {
					continuetime = ctrtVOs[i].getContinuetime();
				}
			}
			// ���⴦���һ�����ݲ���ǩ�����͵����
			if (i >= 1 && conttype == HRCMCommonValue.CONTTYPE_MAKE) {
				contid++;
			}

			ctrtVOs[i].setContid(contid);
			ctrtVOs[i].setIfwrite(UFBoolean.valueOf(false));
		}
	}

	private void defaultValueForEdus(EduVO[] eduVOs) {
		if (ArrayUtils.isEmpty(eduVOs)) {
			return;
		}
		for (int i = 0; i < eduVOs.length; i++) {
			if (eduVOs[i].getLasteducation() == null) {
				eduVOs[i].setLasteducation(UFBoolean.FALSE);
			}
		}
	}

	/**
	 * Ϊ��ְ��¼�ṩĬ��ֵ 2013-5-27 ����03:53:05 yunana
	 * 
	 * @param psndocAggVO2
	 * @param partTimeVOs
	 * @param pk_hrorg
	 * @param pk_psndoc
	 */
	private void defaultValueForPartimes(PsndocAggVO psndocAggVO2, PartTimeVO[] partTimeVOs, String pk_hrorg,
			String pk_psndoc) {
		if (partTimeVOs != null) {
			for (PartTimeVO partTimeVO : partTimeVOs) {
				partTimeVO.setPk_psndoc(pk_psndoc);
				partTimeVO.setPsntype(psndocAggVO2.getParentVO().getPsnOrgVO().getPsntype());
				partTimeVO.setPk_hrorg(pk_hrorg);
				partTimeVO.setIsmainjob(UFBoolean.valueOf(false));
				partTimeVO.setShoworder(9999999);
				partTimeVO.setAssgid(2);
			}
		}
	}

	private void defaultValueForCapas(CapaVO[] capaVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(capaVOs)) {
			return;
		}
		for (int i = 0; i < capaVOs.length; i++) {
			String pk_pe_scogrditem = capaVOs[i].getPk_pe_scogrditem();
			CPIndiGradeVO grade = (CPIndiGradeVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
					.retrieveByPk(null, CPIndiGradeVO.class, pk_pe_scogrditem);
			capaVOs[i].setScore(new UFDouble(grade.getGradeseq()));
		}

	}

	/**
	 * Ϊ��������¼����֯��ϵ����ְ��¼������ӱ����Ĭ��ֵ ���ü�ͥ��ַ 2013-5-27 ����04:14:42 yunana
	 * 
	 * @param psndocAggVO2
	 * @param pk_hrorg
	 * @throws BusinessException
	 */
	private void defaultValueForOtherVOs(PsndocAggVO psndocAggVO2, String pk_hrorg) throws BusinessException {
		for (SuperVO superVO : psndocAggVO2.getAllChildrenVO()) {
			if (superVO.getAttributeValue(PsndocVO.PK_GROUP) == null) {
				superVO.setAttributeValue(PsndocVO.PK_GROUP, PubEnv.getPk_group());
			}
			if (superVO.getAttributeValue(PsndocVO.PK_ORG) == null) {
				superVO.setAttributeValue(PsndocVO.PK_ORG, pk_hrorg);
			}

			// ��Ƹ��Ƹ����pk_psndoc
			if (superVO.getAttributeValue(PsnOrgVO.PK_PSNDOC) == null) {
				superVO.setAttributeValue(PsnOrgVO.PK_PSNDOC, psndocAggVO2.getParentVO().getPk_psndoc());
			}

			if (!(superVO instanceof PsnOrgVO) && superVO.getAttributeValue(PsnOrgVO.PK_PSNORG) == null) {
				superVO.setAttributeValue(PsnOrgVO.PK_PSNORG, psndocAggVO2.getParentVO().getPsnOrgVO().getPrimaryKey());
			}
			if (!(superVO instanceof PsnJobVO) && !(superVO instanceof WorkVO)
					&& superVO.getAttributeValue(PsnJobVO.PK_PSNJOB) == null) {
				superVO.setAttributeValue(PsnJobVO.PK_PSNJOB, psndocAggVO2.getParentVO().getPsnJobVO().getPrimaryKey());
			}
			if (superVO.getAttributeValue(PsnJobVO.ASSGID) == null) {
				superVO.setAttributeValue(PsnJobVO.ASSGID, psndocAggVO2.getParentVO().getPsnJobVO().getAssgid());
			}
		}

		/** ���ü�ͥ��ַ **/
		SuperVO[] addvos = psndocAggVO2.getTableVO("PsnAdd");
		if (!ArrayUtils.isEmpty(addvos)) {
			String pk = NCLocator.getInstance().lookup(IPersistenceUpdate.class).insertVO(null, addvos[0], null);
			PsndocVO psndocVO = psndocAggVO2.getParentVO();
			psndocVO.setAddr(pk);
		}

	}

}
