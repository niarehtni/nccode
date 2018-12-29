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
	 * 根据开始日期得到最新工作记录 2013-4-27 下午03:14:18 yunana
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
	 * 根据开始日期得到最新的组织关系记录 2013-4-27 下午03:11:26 yunana
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
	 * 返回新的数组，并且按照开始日期升序排列 2013-5-30 上午10:45:40 yunana
	 * 
	 * @param supervos
	 * @return
	 */
	private void sortByBeginDate(SuperVO[] vos) {
		SuperVOUtil.sortByAttributeName(vos, "begindate", true);
	}

	/**
	 * 为recordnum，lastflag赋值 2013-5-30 上午11:00:24 yunana
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
	 * 用Excel中的数据更新数据库中的数据,并设置相应VO状态 2013-5-30 上午10:32:46 yunana
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
				// 当dbChildVO为空时，对此childVO的操作只能是新增，
				childVO.setStatus(VOStatus.NEW);
				dbChildVOMap.put(key, childVO);
			} else {
				// 当dbChildVO不为空时，对此childVO的操作可能是删除或更新，需要动态配置VO状态
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
	 * 追加 2013-5-27 下午02:44:18 yunana
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
		// 保存之前，先把组织关系和工作记录添加到人员基本信息VO中
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
		PsndocVO psndocVO = psndocAggVO2.getParentVO();// 父VO
		String pk_psndoc = psndocAggVO2.getParentVO().getPk_psndoc();
		// 工作记录只有最后一条的结束日期为null，其它的都是值
		if (!ArrayUtils.isEmpty(jobs)) {
			sortByBeginDate(jobs);
			for (int i = 0; i < jobs.length; i++) {
				if (jobs[i].getEnddate() == null && i != jobs.length - 1) {
					// 最后一条不需要处理
					jobs[i].setEnddate(jobs[i + 1].getBegindate().getDateBefore(1));
				}
			}

		}

		// 如果组织关系子集为空，则计算组织关系子集
		if (ArrayUtils.isEmpty(orgs)) {
			sortByBeginDate(jobs);
			orgs = new PsnOrgVO[1];
			orgs[0] = new PsnOrgVO();
			orgs[0].setStatus(VOStatus.NEW);
			provideForPsnOrgByPsndocVOAndPsnjobs(orgs, psndocVO, jobs);
			psndocAggVO2.setTableVO("hi_psnorg", orgs);
		}

		// 如果身份证件子集为空，则计算身份证件子集
		if (ArrayUtils.isEmpty(certVOs)) {
			certVOs = new CertVO[1];
			certVOs[0] = new CertVO();
			certVOs[0].setStatus(VOStatus.NEW);
			provideForPsnCertByPsndocVO(certVOs, psndocVO);
			psndocAggVO2.setTableVO("hi_psndoc_cert", certVOs);
		}

		// 最新组织关系中转入人员档案为真，则计算流动情况
		if (ArrayUtils.isEmpty(psnChgVOs) && orgs[orgs.length - 1].getIndocflag().booleanValue()) {
			sortByBeginDate(jobs);
			psnChgVOs = new PsnChgVO[1];
			psnChgVOs[0] = new PsnChgVO();
			psnChgVOs[0].setStatus(VOStatus.NEW);
			providForPsnChgByPsnjobVO(psnChgVOs, jobs[jobs.length - 1]);
			psndocAggVO2.setTableVO("hi_psndoc_psnchg", psnChgVOs);
		}
		/** 导入状态 */
		int importStatus = dataIOConfigVO.getImportMode();
		if (DataIOConst.IMPORT_MODE_ADD == importStatus) {
			// 追加
			insertImport(psndocVO, orgs, jobs);
		}

		else if (DataIOConst.IMPORT_MODE_UPDATE == importStatus) {
			// 更新
			if (psndocVO.getStatus() == VOStatus.NEW) {
				insertImport(psndocVO, orgs, jobs);
			}
			psndocVO.setPsnOrgVO(orgs[orgs.length - 1]);
			psndocVO.setPsnJobVO(jobs[jobs.length - 1]);
		} else {
			// 删除+追加
			if (psndocVO.getStatus() == VOStatus.NEW) {
				insertImport(psndocVO, orgs, jobs);
			}
			psndocVO.setPsnOrgVO(orgs[orgs.length - 1]);
			psndocVO.setPsnJobVO(jobs[jobs.length - 1]);
		}
		String pk_dept = psndocAggVO2.getParentVO().getPsnJobVO().getPk_dept();
		// 对于没有显示委托的部门，此方法查不到人力资源组织，故注掉此行代码
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
		defaultValueForOtherVOs(psndocAggVO2, pk_hrorg);// 同时设置家庭地址

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
							/* @res "联合唯一对应的数据不存在！" */));
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
			// 人员编码重复
			dataIOResult.addValidationFailure(dataIOConfigVO.getExcelVO().getSheetVO("bd_psndoc").getSheetName(),
					dataIOConfigVO.getVOIndex().get(psndocAggVO.getParentVO()), PsndocVO.CODE,
					new ValidationFailure(e.getMessage()));

		} else if ("clerkcoderepeat".equals(errorCode)) {
			// 员工号重复
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
	 * 通过部门主键获得所属人力资源组织 2013-6-5 下午04:17:03 yunana
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
			// 追加
			return;
		}

		// 删除+添加方式时，主集做更新操作。
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
		// 更新VO对象在excel中的行号
		if (mapVoIndex.containsKey(excelParentVO)) {
			mapVoIndex.put(dbParentVO, mapVoIndex.get(excelParentVO));
			mapVoIndex.remove(excelParentVO);
		}

		aggVO.setParentVO(dbParentVO);
		/** 删除+追加单个子集时，为必输子集付默认值 */
		valueForDefaultSheets(sheetCodes, aggVO, dbAggVOs);

		for (String sheetCode : sheetCodes) {
			String[] unionKey = configVO.getExcelVO().getSheetVO(sheetCode).getUnionKeies();

			if (!sheetCode.equals(configVO.getExcelVO().getMainSheetCode())) {
				SuperVO[] childVOs = (SuperVO[]) aggVO.getTableVO(sheetCode);
				if (sheetCode.equals("hi_psnjob") || sheetCode.equals("hi_psnorg")) {

					SuperVO[] dbChildVOs = (SuperVO[]) dbAggVOs[0].getTableVO(sheetCode);
					if (dbChildVOs == null || dbChildVOs.length < 1) {
						// 若子表中不存在数据则直接追加
						continue;
					}

					SuperVO[] tempVO = null;
					// 两个主表只进行更新操作
					try {
						// 更新业务子集
						if (sheetCode.equals("hi_psnjob")) {
							// 因为工作记录和兼职记录用的是同一张表hi_psnjob,所以要单独处理
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
				 * //删除业务子集 SuperVO[] tempVO = (SuperVO[])
				 * Array.newInstance(configVO.getVOClassByTabCode(sheetCode),
				 * 0); try { if(sheetCode.equals("hi_psndoc_parttime")){
				 * //因为工作记录和兼职记录用的是同一张表hi_psnjob,所以要单独处理 OperPsnjobVO(sheetCode,
				 * childVOs, dbChildVOs, tempVO, aggVO,
				 * unionKey,VOStatus.DELETED); }else{ ArrayList<SuperVO> voList
				 * = deleteBusi(sheetCode, childVOs, dbChildVOs); SuperVO[]
				 * superVOs = voList.toArray(new SuperVO[0]);
				 *//** 根据开始日期升序排序 */
				/*
				 * if(!"hi_psndoc_ctrt".equals(sheetCode)){ //合同开始日期都是一样的 不能排序
				 * sortByBeginDate(superVOs); }
				 *//** 为lastflay，recordnum赋值 */
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
	 * 以删除加追加方式进行导入时，数据库中查出的VO赋予delete状态， 界面上的vo赋予new状态 2013-7-17 下午12:59:08
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
			/* @res "联合唯一对应的数据不存在！" */);
		}

		SuperVO dbParentVO = (SuperVO) dbAggVOs[0].getParentVO();
		SuperVO excelParentVO = (SuperVO) aggVO.getParentVO();

		List<String> excludeColumn = hookPublic.getExcludeColumn(excelParentVO.getClass().getName());

		for (String name : excelParentVO.getAttributeNames()) {
			// 排除掉的属性不更新
			if (excludeColumn != null && excludeColumn.contains(name)) {
				continue;
			}
			dbParentVO.setAttributeValue(name, excelParentVO.getAttributeValue(name));
		}

		dbParentVO.setStatus(VOStatus.UPDATED);

		// 更新VO对象在excel中的行号
		if (mapVoIndex.containsKey(excelParentVO)) {
			mapVoIndex.put(dbParentVO, mapVoIndex.get(excelParentVO));
			mapVoIndex.remove(excelParentVO);
		}

		aggVO.setParentVO(dbParentVO);

		/** 更新单个子集时，为必输子集付默认值 */
		valueForDefaultSheets(sheetCodes, aggVO, dbAggVOs);

		for (String sheetCode : sheetCodes) {
			if (!sheetCode.equals(configVO.getExcelVO().getMainSheetCode())) {
				String[] unionKey = configVO.getExcelVO().getSheetVO(sheetCode).getUnionKeies();

				// 子表没有设置联合唯一则按追加处理。
				if (ArrayUtils.isEmpty(unionKey)) {
					continue;
				}

				SuperVO[] childVOs = (SuperVO[]) aggVO.getTableVO(sheetCode);
				SuperVO[] dbChildVOs = (SuperVO[]) dbAggVOs[0].getTableVO(sheetCode);

				if (ArrayUtils.isEmpty(childVOs) && !ArrayUtils.isEmpty(dbChildVOs)) {
					aggVO.setTableVO(sheetCode, dbChildVOs);
					// 子表为空时，不需要更新
					continue;
				}

				if (dbChildVOs == null || dbChildVOs.length < 1) {
					// 若子表中不存在数据则不更新
					aggVO.setTableVO(sheetCode, null);
					continue;
				}

				List<String> excludeChildColumn = hookPublic.getExcludeColumn(dbChildVOs[0].getClass().getName());

				if (sheetCode.equals("hi_psnjob") || sheetCode.equals("hi_psnorg")
						|| sheetCode.equals("hi_psndoc_psnchg") || sheetCode.equals("hi_psndoc_parttime")) {
					SuperVO[] tempVO = null;
					try {
						// 更新业务子集
						if (sheetCode.equals("hi_psnjob") || sheetCode.equals("hi_psndoc_parttime")) {
							// 因为工作记录和兼职记录用的是同一张表hi_psnjob,所以要单独处理
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
					// 合同子集不支持更新，维持原样
					aggVO.setTableVO(sheetCode, dbChildVOs);
				} else {
					SuperVO[] afterCombineVOs = updateUnBusi(dbChildVOs, childVOs, unionKey, excludeChildColumn,
							mapVoIndex);
					/** 根据页签名称用反射机制生成相应类型数组 */
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
	 * 当导入方式为更新，或者更新加追加时，如果没有填写四个默认子集， 则为子集赋予数据库中的值 2013-7-11 下午02:07:15 yunana
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
	 * 为单个子集赋值 2013-7-11 下午02:08:54 yunana
	 * 
	 * @param sheetCode
	 * @param aggVOForUpdate
	 * @param dbAggVOs
	 */
	private void provideValueForSingleSheet(String sheetCode, IExAggVO aggVOForUpdate, IExAggVO[] dbAggVOs) {
		SuperVO[] dbChildVOs = (SuperVO[]) dbAggVOs[0].getTableVO(sheetCode);

		if (ArrayUtils.isEmpty(dbChildVOs) && "hi_psndoc_psnchg".equals(sheetCode)) {
			/** 如果流动情况表为空，并且组织关系表中indocflag为Y时，为流动情况赋值 */
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
	 * 操作工作记录或者兼职记录 2013-7-4 下午01:50:19 yunana
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
			// 兼职记录
			SuperVO[] dbPartTimeChildVOs = getPartTimeJobVOs(dbChildVOs);
			for (int i = 0; i < childVOs.length; i++) {
				SuperVO superVO = childVOs[i];
				superVO.setAttributeValue("ismainjob", UFBoolean.FALSE);
			}
			if (VOStatus.DELETED == voStatus) {
				// 兼职记录
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
	 * 用以以下场景: 更新 更新+追加 删除+追加 2013-5-30 下午01:18:05 yunana
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
		/** 以Excel中数据为基础数据，联合唯一字段String相加后为KEY,superVO为VALUE */
		LinkedHashMap<String, SuperVO> childVOMap = makeUnionKeyMap(unionKey, childVOs, VOStatus.UPDATED);// VO状态参数不起作用
		/** 以数据库中查到的数据为基础数据，联合唯一字段String相加后为KEY,superVO为VALUE */
		LinkedHashMap<String, SuperVO> dbChildVOMap = makeUnionKeyMap(unionKey, dbChildVOs, VOStatus.UPDATED);
		/** 用界面上的需要跟新的字段替换数据库中的数据 */
		SuperVO[] dbVOsAfterReplace = combineMapsByUnionKey(childVOMap, dbChildVOMap, vostatus);
		/** 根据开始日期升序排序 */
		sortByBeginDate(dbVOsAfterReplace);
		if (!"hi_psnjob".equals(sheetCode)) // 如果是工作记录在之后对lastflag、recordnum赋值
		{
			/** 为lastflay，recordnum赋值 */
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
			// 合同
			CtrtVO[] ctrtVOs = new CtrtVO[dbVOsAfterReplace.length];
			for (int i = 0; i < ctrtVOs.length; i++) {
				ctrtVOs[i] = (CtrtVO) dbVOsAfterReplace[i];
			}
			return ctrtVOs;
		} else {
			// 兼职记录
			PartTimeVO[] partTimeVOs = new PartTimeVO[dbVOsAfterReplace.length];
			for (int i = 0; i < partTimeVOs.length; i++) {
				partTimeVOs[i] = (PartTimeVO) dbVOsAfterReplace[i];
			}
			return partTimeVOs;
		}
		// return dbVOsAfterReplace;
	}

	/**
	 * 更新非业务子集 2013-5-30 上午09:48:52 yunana
	 * 
	 * @param dbChildVOs
	 * @param childVOs
	 * @param unionKey
	 * @param excludeChildColumn
	 * @param mapVoIndex
	 *            return 组合后的supervo
	 */
	private SuperVO[] updateUnBusi(SuperVO[] dbChildVOs, SuperVO[] childVOs, String[] unionKey,
			List<String> excludeChildColumn, Map<SuperVO, Integer> mapVoIndex) {

		/** 以Excel中数据为基础数据，联合唯一字段String相加后为KEY,superVO为VALUE */
		LinkedHashMap<String, SuperVO> childVOMap = makeUnionKeyMap(unionKey, childVOs, VOStatus.UPDATED);// VO状态参数不起作用
		/** 以数据库中查到的数据为基础数据，联合唯一字段String相加后为KEY,superVO为VALUE */
		LinkedHashMap<String, SuperVO> dbChildVOMap = makeUnionKeyMap(unionKey, dbChildVOs, VOStatus.UPDATED);
		/** 用界面上的需要跟新的字段替换数据库中的数据 */
		SuperVO[] dbVOsAfterReplace = combineMapsByUnionKey(childVOMap, dbChildVOMap, VOStatus.UPDATED);
		// for (int i = 0; i < dbVOsAfterReplace.length; i++) {
		// tempVO[i] = dbVOsAfterReplace[i];
		// }
		return dbVOsAfterReplace;

		// for (SuperVO childVO : childVOs) {
		// // 子表中存在数据，则把存在的数据更新。
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
		// // 排除掉的属性不更新
		// if (excludeChildColumn != null
		// && excludeChildColumn.contains(name)) {
		// continue;
		// }
		// dbChildVO.setAttributeValue(name,
		// childVO.getAttributeValue(name));
		// }
		// // 更新VO对象在excel中的行号
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
							// 根据表名去xml文件中查找对应的表，进行相应的字段解密
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
			// 追加
			return;
		}
		// 电信研究院导入自定义子集最新记录问题
		PsndocAggVO psnAggVo = (PsndocAggVO) dbAggVOs[0];
		PsndocAggVO psnAggVoimport = (PsndocAggVO) aggVO;
		List<SuperVO> importlist = new ArrayList<SuperVO>();

		PsndocVO mainvo = psnAggVo.getParentVO();
		if (StringUtils.isEmpty(mainvo.getId()) || StringUtils.isEmpty(mainvo.getIdtype())) {
			throw new BusinessException(ResHelper.getString("6007psn", "06007psn0521")/*
																					 * @
																					 * res
																					 * "所导入人员的证件信息为空，请去业务节点补齐"
																					 */);
		}

		for (int i = 1; i < 9; i++) {
			SuperVO[] importchildVOs = psnAggVoimport.getTableVO("hi_psndoc_glbdef" + i);
			// 如果导入的叶签中没有数据就不用修改数据
			if (importchildVOs == null || importchildVOs.length == 0) {
				continue;
			}
			// 按照开始日期排序升序
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
			// 排除掉的属性不更新
			if (excludeColumn != null && excludeColumn.contains(name)) {
				continue;
			}
			dbParentVO.setAttributeValue(name, excelParentVO.getAttributeValue(name));
		}

		dbParentVO.setStatus(VOStatus.UPDATED);
		// 更新VO对象在excel中的行号
		if (mapVoIndex.containsKey(excelParentVO)) {
			mapVoIndex.put(dbParentVO, mapVoIndex.get(excelParentVO));
			mapVoIndex.remove(excelParentVO);
		}
		aggVO.setParentVO(dbParentVO);

		/** 更新+追加单个子集时，为必输子集付默认值 */
		valueForDefaultSheets(sheetCodes, aggVO, dbAggVOs);

		for (String sheetCode : sheetCodes) {

			if (!sheetCode.equals(configVO.getExcelVO().getMainSheetCode())) {
				String[] unionKey = configVO.getExcelVO().getSheetVO(sheetCode).getUnionKeies();
				// 子表没有设置联合唯一则按追加处理。
				if (ArrayUtils.isEmpty(unionKey)) {
					continue;
				}
				SuperVO[] childVOs = (SuperVO[]) aggVO.getTableVO(sheetCode);
				SuperVO[] dbChildVOs = (SuperVO[]) dbAggVOs[0].getTableVO(sheetCode);

				if (ArrayUtils.isEmpty(childVOs) && !ArrayUtils.isEmpty(dbChildVOs)) {
					aggVO.setTableVO(sheetCode, dbChildVOs);
					// 子表为空时，不需要更新
					continue;
				}

				if (dbChildVOs == null || dbChildVOs.length < 1) {
					// 若子表中不存在数据则追加,合同除外
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
						// 更新业务子集
						if (sheetCode.equals("hi_psnjob") || sheetCode.equals("hi_psndoc_parttime")) {
							// 因为工作记录和兼职记录用的是同一张表hi_psnjob,所以要单独处理
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
					// 合同子集不支持更新+追加
					aggVO.setTableVO(sheetCode, dbChildVOs);
				} else {
					SuperVO[] afterCombineVOs = updateUnBusi(dbChildVOs, childVOs, unionKey, excludeChildColumn,
							mapVoIndex);
					/** 根据页签名称用反射机制生成相应类型数组 */
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
	 * 为组织关系提供默认值 2013-5-27 下午03:42:34 yunana 如果为多组织导入需要录入组织关系的所属人力资源组织
	 * 
	 * @param orgvos
	 * @param pk_hrorg
	 * @param pk_psndoc
	 */
	private void defaultValueForPsnorgs(PsnOrgVO[] orgs, String pk_hrorg, String pk_psndoc) {
		// 如果只有一条组织关系，则对应当前的人力资源组织
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
	 * 为工作记录提供默认值 2013-5-27 下午03:41:40 yunana
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
				jobs[i - 1].setLastflag(UFBoolean.valueOf(true)); // 工作记录所属组织的最后一条（最新）工作记录的最新标识
			}

			if (jobs[i].getLastflag() != null && jobs[i].getLastflag().booleanValue()) {
				if (jobs[i].getTrnsevent() == 4/* 异动事件为离职 */) {
					jobs[i].setEndflag(UFBoolean.TRUE);
				}
			} else {
				jobs[i].setLastflag(UFBoolean.valueOf(false));
				jobs[i].setRecordnum(recordnum);
				jobs[i].setEndflag(UFBoolean.TRUE);// 如果不是最新的工作记录，则说明已经结束
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
		int contid = 1;// 合同id
		int continuetime = 1;
		Integer conttype;
		// 按EXCEL界面的顺序为合同id赋值，每当遇到签订时合同id加1
		for (int i = 0; i < ctrtVOs.length; i++) {
			// 此条合同记录的合同类型
			conttype = ctrtVOs[i].getConttype();
			/*
			 * continuetime的规则是， （1）签订时为1； （2）变更为上一条的值，如果为第一条则为1， （3）终止、解除设置为0。
			 * （4）续签，如果为空或则为上一条+1，如果为第一条记录则值为2；如果有值并且不为第一条，则按当前值为准
			 */
			if (conttype == HRCMCommonValue.CONTTYPE_MAKE) {
				// 签订
				continuetime = 1;
				ctrtVOs[i].setContinuetime(continuetime);
			} else if (conttype == HRCMCommonValue.CONTTYPE_RELEASE || conttype == HRCMCommonValue.CONTTYPE_FINISH) {
				// 终止解除
				continuetime = 0;
				ctrtVOs[i].setContinuetime(continuetime);
			} else if (conttype == HRCMCommonValue.CONTTYPE_CHANGE) {
				// 变更
				ctrtVOs[i].setContinuetime(continuetime);
			} else {
				// 续签
				if (ctrtVOs[i].getContinuetime() == null || i == 0) {

					if (conttype == HRCMCommonValue.CONTTYPE_EXTEND) {
						continuetime++;
					}
					ctrtVOs[i].setContinuetime(continuetime);

				} else {
					continuetime = ctrtVOs[i].getContinuetime();
				}
			}
			// 特殊处理第一条数据不是签订类型的情况
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
	 * 为兼职记录提供默认值 2013-5-27 下午03:53:05 yunana
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
	 * 为除工作记录，组织关系，兼职记录以外的子表添加默认值 设置家庭地址 2013-5-27 下午04:14:42 yunana
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

			// 返聘再聘设置pk_psndoc
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

		/** 设置家庭地址 **/
		SuperVO[] addvos = psndocAggVO2.getTableVO("PsnAdd");
		if (!ArrayUtils.isEmpty(addvos)) {
			String pk = NCLocator.getInstance().lookup(IPersistenceUpdate.class).insertVO(null, addvos[0], null);
			PsndocVO psndocVO = psndocAggVO2.getParentVO();
			psndocVO.setAddr(pk);
		}

	}

}
