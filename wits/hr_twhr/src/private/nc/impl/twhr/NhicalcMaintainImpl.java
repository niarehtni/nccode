package nc.impl.twhr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.ml.NCLangResOnserver;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.impl.pub.ace.AceNhicalcPubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.itf.hi.PsndocDefUtil;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.hi.psndoc.PsndocDefVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.twhr.nhicalc.NhiCalcVO;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;

public class NhicalcMaintainImpl extends AceNhicalcPubServiceImpl implements
		nc.itf.twhr.INhicalcMaintain {
	private BaseDAO baseDao;

	private String pk_org;
	private String calcYear;
	private String calcMonth;
	private List<NhiCalcVO> nhiDataList;

	@Override
	public NhiCalcVO[] query(IQueryScheme queryScheme) throws BusinessException {
		return super.pubquerybasedoc(queryScheme);
	}

	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO)
			throws BusinessException {
		BatchSaveAction<NhiCalcVO> saveAction = new BatchSaveAction<NhiCalcVO>();
		BatchOperateVO retData = saveAction.batchSave(batchVO);
		return retData;
	}

	@Override
	public void audit(String pk_org, String cyear, String cperiod)
			throws BusinessException {
		if (isAudit(pk_org, cyear, cperiod)) {
			throw new BusinessException(NCLangResOnserver.getInstance()
					.getStrByID("68861705", "NhicalcMaintainImpl-0001")/*
																		 * ѡ���ڼ��Ѿ���ˡ�
																		 */);
		}

		this.setPk_org(pk_org);
		this.setCalcYear(cyear);
		this.setCalcMonth(cperiod);

		// ���d�ڽ���Ӌ��Y��
		loadNhiCalcResults();

		// ���T���n���ڽ����Զ��x��
		updatePsnDefs();

		String strSQL = "UPDATE twhr_nhicalc SET isaudit='Y' WHERE pk_org='"
				+ pk_org + "' AND cyear='" + cyear + "' AND cperiod='"
				+ cperiod + "' AND dr=0";
		this.getBaseDao().executeUpdate(strSQL);
	}

	private void loadNhiCalcResults() throws BusinessException {
		String strSQL = " (pk_org = '" + this.getPk_org() + "') AND (cyear = '"
				+ this.getCalcYear() + "') AND (cperiod = '"
				+ this.getCalcMonth() + "') AND (dr = 0) AND isaudit='N'";

		this.setNhiDataList((List<NhiCalcVO>) this.getBaseDao()
				.retrieveByClause(NhiCalcVO.class, strSQL));
	}

	private void updatePsnDefs() throws BusinessException {
		// ���T���n�����ڽ���Ӌ��������
		SaveNHIDetail();
		// ���T���n�����ڽ���Ӌ�㏡����
		SaveNHITotal();
	}

	// ����������
	private void SaveNHIDetail() throws BusinessException {
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate(
				"TWHRGLBDEF");
		List<PsndocDefVO> nhiTotalVOs = getNHIDetailSaveVOs();
		if (nhiTotalVOs != null && nhiTotalVOs.size() > 0) {
			for (PsndocDefVO vo : nhiTotalVOs) {
				service.insert(vo);
			}
		}
	}

	/**
	 * @return
	 * @throws BusinessException
	 */
	private List<PsndocDefVO> getNHIDetailSaveVOs() throws BusinessException {
		List<PsndocDefVO> psnLaborInfoVOs = new ArrayList<PsndocDefVO>();
		if (this.getNhiDataList() != null && this.getNhiDataList().size() > 0) {
			for (NhiCalcVO vo : this.getNhiDataList()) {
				PsndocDefVO newVO = PsndocDefUtil.getPsnNHIDetailVO();
				newVO.setBegindate(UFLiteralDate.getDate(vo.getBegindate()
						.toString()));
				newVO.setEnddate(UFLiteralDate.getDate(vo.getEnddate()
						.toString()));
				newVO.setPk_psndoc(vo.getPk_psndoc());
				newVO.setDr(0);

				// glbdef1,���Ě��ϳ̶�
				newVO.setAttributeValue("glbdef1", vo.getDisablegrade());
				// glbdef2,�ڱ����עӛ
				newVO.setAttributeValue("glbdef2", vo.getLabortype());
				// glbdef3,�ڱ�����
				newVO.setAttributeValue("glbdef3", vo.getOldlaborrange());
				// glbdef4,��ͨ�¹ʱ��U�M��
				newVO.setAttributeValue("glbdef4", vo.getComrate());
				// glbdef5,��ͨ�¹ʱ��U�M�Г�����(����)
				newVO.setAttributeValue("glbdef5", vo.getComstuffrate());
				// glbdef6,��ͨ�¹ʱ��U�M�Г����~(����)
				newVO.setAttributeValue("glbdef6", vo.getComstuff());
				// glbdef7,��ͨ�¹ʱ��U�M�Г�����(�l��)
				newVO.setAttributeValue("glbdef7", vo.getComhirerrate());
				// glbdef8,��ͨ�¹ʱ��U�M�Г����~(�l��)
				newVO.setAttributeValue("glbdef8", vo.getComhirer());
				// glbdef9,�I�ĺ����U�M��
				newVO.setAttributeValue("glbdef9", vo.getDisrate());
				// glbdef10,�I�ĺ����U�M�Г�����(����)
				newVO.setAttributeValue("glbdef10", vo.getDisstuffrate());
				// glbdef11,�I�ĺ����U�M�Г����~(����)
				newVO.setAttributeValue("glbdef11", vo.getDisstuff());
				// glbdef12,�I�ĺ����U�M�Г�����(�l��)
				newVO.setAttributeValue("glbdef12", vo.getDishirerrate());
				// glbdef13,�I�ĺ����U�M�Г����~(�l��)
				newVO.setAttributeValue("glbdef13", vo.getDishirer());
				// glbdef14,�͘I���U�M��
				newVO.setAttributeValue("glbdef14", vo.getEmprate());
				// glbdef15,�͘I���U�M�Г�����(����)
				newVO.setAttributeValue("glbdef15", vo.getEmpstuffrate());
				// glbdef16,�͘I���U�M�Г����~(����)
				newVO.setAttributeValue("glbdef16", vo.getEmpstuff());
				// glbdef17,�͘I���U�M�Г�����(�l��)
				newVO.setAttributeValue("glbdef17", vo.getEmphirerrate());
				// glbdef18,�͘I���U�M�Г����~(�l��)
				newVO.setAttributeValue("glbdef18", vo.getEmphirer());
				// glbdef19,�ڱ��Г����~(����)
				newVO.setAttributeValue("glbdef19", vo.getLaborstuff());
				// glbdef20,�ڱ��Г����~(�l��)
				newVO.setAttributeValue("glbdef20", vo.getLaborhirer());
				// glbdef21,���˼���
				newVO.setAttributeValue("glbdef21", vo.getOldretirerange());
				// glbdef22,���˽����U����(����)
				newVO.setAttributeValue("glbdef22", vo.getRetirestuffrate());
				// glbdef23,���˽����U���~(����)
				newVO.setAttributeValue("glbdef23", vo.getRetirestuff());
				// glbdef24,���˽����U����(�l��)
				newVO.setAttributeValue("glbdef24", vo.getRetirehirerrate());
				// glbdef25,���˽����U���~(�l��)
				newVO.setAttributeValue("glbdef25", vo.getRetirehirer());
				// glbdef26,�ڱ���Ч�씵
				newVO.setAttributeValue("glbdef26", vo.getLabordays());
				// glbdef27,������ͨ�¹ʱ��U�M�Г����~(����)
				newVO.setAttributeValue("glbdef27", vo.getLastmonthcomstuff());
				// glbdef28,������ͨ�¹ʱ��U�M�Г����~(�l��)
				newVO.setAttributeValue("glbdef28", vo.getLastmonthcomhirer());
				// glbdef29,�����I�ĺ����U�M�Г����~(����)
				newVO.setAttributeValue("glbdef29", vo.getLastmonthdisstuff());
				// glbdef30,�����I�ĺ����U�M�Г����~(�l��)
				newVO.setAttributeValue("glbdef30", vo.getLastmonthdishirer());
				// glbdef31,���¾͘I���U�M�Г����~(����)
				newVO.setAttributeValue("glbdef31", vo.getLastmonthempstuff());
				// glbdef32,���¾͘I���U�M�Г����~(�l��)
				newVO.setAttributeValue("glbdef32", vo.getLastmonthemphirer());
				// glbdef33,���ڱ��Г����~(����)
				newVO.setAttributeValue("glbdef33",
						vo.getLastmonthllaborstuff());
				// glbdef34,���ڱ��Г����~(�l��)
				newVO.setAttributeValue("glbdef34", vo.getLastmonthlaborhirer());
				// glbdef35,�����˽����U���~(����)
				newVO.setAttributeValue("glbdef35",
						vo.getLastmonthretirestuff());
				// glbdef36,�����˽����U���~(�l��)
				newVO.setAttributeValue("glbdef36",
						vo.getLastmonthretirehirer());
				// glbdef37,���ڱ���Ч�씵
				newVO.setAttributeValue("glbdef37", vo.getLastmonthlabordays());
				// glbdef38,�Ƿ��������
				newVO.setAttributeValue("glbdef38", vo.getIncludelastmonth());
				// glbdef39,����н�Y
				UFDouble baseSalary = vo.getLaborsalary();
				if (baseSalary == null || baseSalary.equals(UFDouble.ZERO_DBL)) {
					baseSalary = vo.getHealthsalary();
				}
				newVO.setAttributeValue("glbdef39", baseSalary);
				// glbdef40,���
				newVO.setAttributeValue("glbdef40", vo.getCyear());
				// glbdef41,�·�
				newVO.setAttributeValue("glbdef41", vo.getCperiod());
				// glbdef42,�����YԴ�M��
				newVO.setAttributeValue("glbdef42", vo.getPk_org());
				// glbdef43 ������Ч�씵
				newVO.setAttributeValue("glbdef43", vo.getRetiredays());
				// glbdef44,�|��н�Y������~
				newVO.setAttributeValue("glbdef44", vo.getRepayfund());
				// glbdef45,���|��н�Y������~
				newVO.setAttributeValue("glbdef45", vo.getLastmonthrepayfund());
				psnLaborInfoVOs.add(newVO);
			}
		}

		return psnLaborInfoVOs;
	}

	// ���握����
	private void SaveNHITotal() throws BusinessException {
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate(
				"TWHRGLBDEF");
		List<PsndocDefVO> nhiTotalVOs = getNHITotalSaveVOs();
		if (nhiTotalVOs != null && nhiTotalVOs.size() > 0) {
			for (PsndocDefVO vo : nhiTotalVOs) {
				PsndocDefVO[] existsVOs = service.queryByCondition(
						vo.getClass(), " pk_psndoc='" + vo.getPk_psndoc()
								+ "' and dr=0 ");
				if (existsVOs != null && existsVOs.length > 0) {
					// �����Ѵ����Y��
					for (PsndocDefVO exvo : existsVOs) {
						exvo.setRecordnum(exvo.getRecordnum() + 1);
						exvo.setLastflag(UFBoolean.FALSE);
						service.update(exvo, true);
					}
				}
				service.insert(vo);
			}
		}
	}

	private List<PsndocDefVO> getNHITotalSaveVOs() throws BusinessException {
		List<PsndocDefVO> psnLaborInfoVOs = new ArrayList<PsndocDefVO>();
		if (this.getNhiDataList() != null && this.getNhiDataList().size() > 0) {
			for (NhiCalcVO vo : this.getNhiDataList()) {
				PsndocDefVO newVO = PsndocDefUtil.getPsnNHISumVO();
				newVO.setBegindate(UFLiteralDate.getDate(vo.getBegindate()
						.toString()));
				newVO.setEnddate(UFLiteralDate.getDate(vo.getEnddate()
						.toString()));
				newVO.setPk_psndoc(vo.getPk_psndoc());
				newVO.setDr(0);
				newVO.setLastflag(UFBoolean.TRUE);
				newVO.setRecordnum(0);

				// glbdef1,Ͷ�����
				newVO.setAttributeValue("glbdef1", vo.getCyear());
				// glbdef2,Ͷ���·�
				newVO.setAttributeValue("glbdef2", vo.getCperiod());
				// glbdef3,����н��
				UFDouble baseSalary = vo.getLaborsalary();
				if (baseSalary == null || baseSalary.equals(UFDouble.ZERO_DBL)) {
					baseSalary = vo.getHealthsalary();
				}
				newVO.setAttributeValue("glbdef3", baseSalary);
				// glbdef4,�ͱ�����
				newVO.setAttributeValue("glbdef4", vo.getOldlaborrange());
				// glbdef5,�ͱ�Ͷ������
				newVO.setAttributeValue(
						"glbdef5",
						(vo.getLabordays() == null ? 0 : vo.getLabordays())
								+ (vo.getLastmonthlabordays() == null ? 0 : vo
										.getLastmonthlabordays()));
				// glbdef6,��ͨ�¹ʱ��շѳе����(����)
				newVO.setAttributeValue(
						"glbdef6",
						vo.getComstuff()
								.add(vo.getLastmonthcomstuff() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthcomstuff()));
				// glbdef7,��ͨ�¹ʱ��շѳе����(����)
				newVO.setAttributeValue(
						"glbdef7",
						vo.getComhirer()
								.add(vo.getLastmonthcomhirer() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthcomhirer()));
				// glbdef8,ְҵ�ֺ����շѳе����(����)
				newVO.setAttributeValue(
						"glbdef8",
						vo.getDisstuff()
								.add(vo.getLastmonthdisstuff() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthdisstuff()));
				// glbdef9,ְҵ�ֺ����շѳе����(����)
				newVO.setAttributeValue(
						"glbdef9",
						vo.getDishirer()
								.add(vo.getLastmonthdishirer() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthdishirer()));
				// glbdef10,��ҵ���շѳе����(����)
				newVO.setAttributeValue(
						"glbdef10",
						vo.getEmpstuff()
								.add(vo.getLastmonthempstuff() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthempstuff()));
				// glbdef11,��ҵ���շѳе����(����)
				newVO.setAttributeValue(
						"glbdef11",
						vo.getEmphirer()
								.add(vo.getLastmonthemphirer() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthemphirer()));
				// glbdef12,�ͱ��е����(����)
				newVO.setAttributeValue(
						"glbdef12",
						vo.getLaborstuff()
								.add(vo.getLastmonthllaborstuff() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthllaborstuff()));
				// glbdef13,�ͱ��е����(����)
				newVO.setAttributeValue(
						"glbdef13",
						vo.getLaborhirer()
								.add(vo.getLastmonthlaborhirer() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthlaborhirer()));
				// glbdef14,���˼���
				newVO.setAttributeValue("glbdef14", vo.getOldretirerange());
				// glbdef15,���˽���ɽ��(����)
				newVO.setAttributeValue(
						"glbdef15",
						vo.getRetirestuff()
								.add(vo.getLastmonthretirestuff() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthretirestuff()));
				// glbdef16,���˽���ɽ��(����)
				newVO.setAttributeValue(
						"glbdef16",
						vo.getRetirehirer()
								.add(vo.getLastmonthretirehirer() == null ? UFDouble.ZERO_DBL
										: vo.getLastmonthretirehirer()));
				if (vo.getHealthstuff() != null && vo.getHealthhirer() != null) {
					// glbdef17,��������
					newVO.setAttributeValue("glbdef17", vo.getOldhealthrange());
					// glbdef18,��������(������)
					newVO.setAttributeValue("glbdef18", vo.getDependentcount());
					// glbdef19,�����ѳе����(����)
					newVO.setAttributeValue(
							"glbdef19",
							vo.getHealthstuff()
									.add(vo.getLastmonthhealthstuff() == null ? UFDouble.ZERO_DBL
											: vo.getLastmonthhealthstuff()));
					// glbdef20,�����ѳе����(����)
					newVO.setAttributeValue(
							"glbdef20",
							vo.getHealthhirer()
									.add(vo.getLastmonthhealthhirer() == null ? UFDouble.ZERO_DBL
											: vo.getLastmonthhealthhirer()));
					// glbdef21,��������������
					newVO.setAttributeValue(
							"glbdef21",
							vo.getHealthgov()
									.add(vo.getLastmonthhealthgov() == null ? UFDouble.ZERO_DBL
											: vo.getLastmonthhealthgov()));
					// glbdef22,������Ӧ�ɽ��
					newVO.setAttributeValue(
							"glbdef22",
							vo.getHealthstuffact()
									.add(vo.getLastmonthhealthstuffact() == null ? UFDouble.ZERO_DBL
											: vo.getLastmonthhealthstuffact()));
					// glbdef24,�Ƿ�������½���
					newVO.setAttributeValue("glbdef24",
							vo.getIncludelastmonth());
				}
				// glbdef23,�Ƿ���������ͱ�
				newVO.setAttributeValue("glbdef23", vo.getIncludelastmonth());
				// glbdef25,����Ͷ������
				newVO.setAttributeValue(
						"glbdef25",
						(vo.getRetiredays() == null ? 0 : vo.getRetiredays())
								+ (vo.getLastmonthretiredays() == null ? 0 : vo
										.getLastmonthretiredays()));
				// glbdef26,��Ƿн�ʵ泥������
				newVO.setAttributeValue("glbdef26", vo.getRepayfund());

				psnLaborInfoVOs.add(newVO);
			}
		}

		return psnLaborInfoVOs;
	}

	private String getPsndocWherePart() {
		String strWherePart = " where  def.pk_psndoc in ( select  distinct pk_psndoc ";
		strWherePart += "                        from    twhr_nhicalc where dr=0 and pk_org='"
				+ this.getPk_org()
				+ "' and cyear='"
				+ this.getCalcYear()
				+ "' and cperiod='"
				+ this.getCalcMonth()
				+ "' and isaudit='Y' ) ";
		strWherePart += "         and def.dr = 0 ";
		strWherePart += "         and cast(def.enddate as date) >= '"
				+ this.getFirstDayOfMonth(this.getCalcYear(),
						this.getCalcMonth()).toString() + "' ";
		strWherePart += "         and def.begindate <= '"
				+ this.getLastDayOfMonth(this.getCalcYear(),
						this.getCalcMonth()).toString() + "' ";
		return strWherePart;
	}

	private UFDate getFirstDayOfMonth(String year, String month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(year), Integer.valueOf(month) - 1, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return new UFDate(calendar.getTime()).asBegin();
	}

	private UFDate getLastDayOfMonth(String year, String month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(year), Integer.valueOf(month) - 1, 1);
		int lastday = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, lastday);
		return new UFDate(calendar.getTime()).asEnd();
	}

	@Override
	public void unAudit(String pk_org, String cyear, String cperiod)
			throws BusinessException {
		if (!isAudit(pk_org, cyear, cperiod)) {
			throw new BusinessException(NCLangResOnserver.getInstance()
					.getStrByID("68861705", "NhicalcMaintainImpl-0002")/*
																		 * ѡ���ڼ���δ��ˡ�
																		 */);
		}

		this.setPk_org(pk_org);
		this.setCalcYear(cyear);
		this.setCalcMonth(cperiod);

		String strSQL = " delete def from "
				+ PsndocDefTableUtil.getPsnNHISumTablename() + " def ";
		strSQL += getPsndocWherePart();
		this.getBaseDao().executeUpdate(strSQL);

		strSQL = " delete def from "
				+ PsndocDefTableUtil.getPsnNHIDetailTablename() + " def ";
		strSQL += getPsndocWherePart();
		strSQL += " AND def.glbdef42='" + pk_org + "'";
		this.getBaseDao().executeUpdate(strSQL);

		// strSQL = " UPDATE def ";
		// strSQL += " SET def.recordnum = def.recordnum - 1 ";
		// strSQL += " FROM " + PsndocDefTableUtil.getPsnNHISumTablename()
		// + " def ";
		// this.getBaseDao().executeUpdate(strSQL);
		//
		// strSQL = " UPDATE def ";
		// strSQL +=
		// " SET lastflag = (CASE WHEN recordnum = 0 THEN 'Y' ELSE 'N' END) ";
		// strSQL += " FROM " + PsndocDefTableUtil.getPsnNHISumTablename()
		// + " def ";
		strSQL = " WITH UpdateData ";
		strSQL += " AS ( ";
		strSQL += " 	SELECT ROW_NUMBER() OVER ( ";
		strSQL += " 			PARTITION BY pk_psndoc ORDER BY begindate DESC ";
		strSQL += " 				,ISNULL(enddate, '9999-12-31') DESC ";
		strSQL += " 			) - 1 AS recnumber ";
		strSQL += " 		,pk_psndoc_sub ";
		strSQL += " 	FROM " + PsndocDefTableUtil.getPsnNHISumTablename()
				+ " def ";
		strSQL += getPsndocWherePart();
		strSQL += " 	) ";
		strSQL += " UPDATE def ";
		strSQL += " SET recordnum = UpdateData.recnumber ";
		strSQL += " FROM " + PsndocDefTableUtil.getPsnNHISumTablename()
				+ " def ";
		strSQL += " INNER JOIN UpdateData ON UpdateData.pk_psndoc_sub = def.pk_psndoc_sub ";
		strSQL += getPsndocWherePart();
		this.getBaseDao().executeUpdate(strSQL);

		strSQL = " UPDATE def ";
		strSQL += " SET lastflag = ( ";
		strSQL += " 		CASE  ";
		strSQL += " 			WHEN recordnum = 0 ";
		strSQL += " 				THEN 'Y' ";
		strSQL += " 			ELSE 'N' ";
		strSQL += " 			END ";
		strSQL += " 		) ";
		strSQL += " FROM " + PsndocDefTableUtil.getPsnNHISumTablename()
				+ " def ";
		strSQL += getPsndocWherePart();
		this.getBaseDao().executeUpdate(strSQL);

		strSQL = "UPDATE twhr_nhicalc SET isaudit='N' WHERE pk_org='" + pk_org
				+ "' AND cyear='" + cyear + "' AND cperiod='" + cperiod
				+ "' AND dr=0";
		this.getBaseDao().executeUpdate(strSQL);
	}

	@Override
	public boolean isAudit(String pk_org, String cyear, String cperiod)
			throws BusinessException {
		String strSQL = "SELECT COUNT(*) FROM twhr_nhicalc WHERE pk_org='"
				+ pk_org + "' AND cyear='" + cyear + "' AND cperiod='"
				+ cperiod + "' AND dr=0 AND isaudit='Y'";
		int count = (Integer) this.getBaseDao().executeQuery(strSQL,
				new ColumnProcessor());
		return count > 0;
	}

	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

	public String getCalcYear() {
		return calcYear;
	}

	public void setCalcYear(String calcYear) {
		this.calcYear = calcYear;
	}

	public String getCalcMonth() {
		return calcMonth;
	}

	public void setCalcMonth(String calcMonth) {
		this.calcMonth = calcMonth;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public List<NhiCalcVO> getNhiDataList() {
		return nhiDataList;
	}

	public void setNhiDataList(List<NhiCalcVO> nhiDataList) {
		this.nhiDataList = nhiDataList;
	}

}
