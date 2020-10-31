package nc.impl.twhr;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.InSQLCreator;
import nc.impl.pub.ace.AceTwhr_declarationPubServiceImpl;
import nc.itf.twhr.ITwhr_declarationMaintain;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pubitf.para.DeclarationUtils;
import nc.pubitf.para.SysInitQuery4TWHR;
import nc.pubitf.twhr.IBasedocPubQuery;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bm.util.SQLHelper;
import nc.vo.hi.psndoc.PTCostVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.logging.Debug;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.twhr.basedoc.BaseDocVO;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;
import nc.vo.twhr.twhr_declaration.BusinessBVO;
import nc.vo.twhr.twhr_declaration.CompanyAdjustBVO;
import nc.vo.twhr.twhr_declaration.CompanyBVO;
import nc.vo.twhr.twhr_declaration.DeclarationHVO;
import nc.vo.twhr.twhr_declaration.NonPartTimeBVO;
import nc.vo.twhr.twhr_declaration.PartTimeBVO;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.paydata.DataVO;

import org.apache.commons.lang.StringUtils;

public class Twhr_declarationMaintainImpl extends AceTwhr_declarationPubServiceImpl implements
		ITwhr_declarationMaintain {

	private BaseDAO basDAO;
	// �걨��ʽ:50н��:1 90AB:2 ����:0
	private static final int DECLAREFORM_50 = 1;
	private static final int DECLAREFORM_90AB = 2;
	private static final int DECLAREFORM_OTHER = 0;
	// �Ǽ�ְ��Ա����
	private static String DECLARATION_NONPARTTIME_TABLE_NAME = "declaration_nonparttime";
	// ��ְ��Ա����
	private static String DECLARATION_PARTTIME_TABLE_NAME = "declaration_parttime";
	// ִ��ҵ�����ñ���
	private static String DECLARATION_BUSINESS_TABLE_NAME = "declaration_business";
	// ��˾���䱣�ѱ���
	private static String DECLARATION_COMPANY_TABLE_NAME = "declaration_company";
	// ��˾�a�䱣�M�{������
	private static String DECLARATION_COMPANYADJ_TABLE_NAME = "declaration_companyadj";

	/**
	 * @return the basDAO
	 */
	private BaseDAO getBaseDAO() {
		if (null == basDAO) {
			basDAO = new BaseDAO();
		}
		return basDAO;
	}

	@Override
	public void delete(AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills) throws BusinessException {
		super.pubdeleteBills(clientFullVOs, originBills);
	}

	@Override
	public AggDeclarationVO[] insert(AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills)
			throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public AggDeclarationVO[] update(AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills)
			throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}

	@Override
	public AggDeclarationVO[] query(IQueryScheme queryScheme) throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggDeclarationVO[] save(AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills)
			throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggDeclarationVO[] unsave(AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills)
			throws BusinessException {
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggDeclarationVO[] approve(AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills)
			throws BusinessException {
		return super.pubapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggDeclarationVO[] unapprove(AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills)
			throws BusinessException {
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nc.itf.twhr.ITwhr_declarationMaintain#writeBack4HealthCaculate(nc.vo.
	 * twhr.twhr_declaration.AggDeclarationVO[],
	 * nc.vo.twhr.twhr_declaration.AggDeclarationVO[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void writeBack4HealthCaculate(AggDeclarationVO originBill) throws BusinessException {
		Map<String, List<ISuperVO>> saveBVOListMap = new HashMap<>();
		if (null == originBill) {
			return;
		}
		ISuperVO[] partTimeBVOs = originBill.getChildren(PartTimeBVO.class);
		// �������
		if (null != partTimeBVOs) {
			int count0 = generaRowNum(DECLARATION_PARTTIME_TABLE_NAME);
			for (ISuperVO vo : partTimeBVOs) {
				PartTimeBVO pvo = (PartTimeBVO) vo;
				pvo.setNum(++count0);
			}
			List<ISuperVO> tempList = new ArrayList<>();
			tempList.addAll(Arrays.asList(partTimeBVOs));
			saveBVOListMap.put(DECLARATION_PARTTIME_TABLE_NAME, tempList);
		}
		// ����ֻ����ʱ������,�ӱ��治��bp
		originBill.setChildren(PartTimeBVO.class, null);

		ISuperVO[] nonPartTimeBVOs = originBill.getChildren(NonPartTimeBVO.class);
		// �������
		if (null != nonPartTimeBVOs) {
			int count1 = generaRowNum(DECLARATION_NONPARTTIME_TABLE_NAME);
			for (ISuperVO vo : nonPartTimeBVOs) {
				NonPartTimeBVO pvo = (NonPartTimeBVO) vo;
				pvo.setNum(++count1);
			}
			List<ISuperVO> tempList = new ArrayList<>();
			tempList.addAll(Arrays.asList(nonPartTimeBVOs));
			saveBVOListMap.put(DECLARATION_NONPARTTIME_TABLE_NAME, tempList);
		}
		// ����ֻ����ʱ������,�ӱ��治��bp
		originBill.setChildren(NonPartTimeBVO.class, null);

		ISuperVO[] businessBVOs = originBill.getChildren(BusinessBVO.class);
		// �������
		if (null != businessBVOs) {
			int count2 = generaRowNum(DECLARATION_BUSINESS_TABLE_NAME);
			for (ISuperVO vo : businessBVOs) {
				BusinessBVO pvo = (BusinessBVO) vo;
				pvo.setNum(++count2);
			}
			List<ISuperVO> tempList = new ArrayList<>();
			tempList.addAll(Arrays.asList(businessBVOs));
			saveBVOListMap.put(DECLARATION_BUSINESS_TABLE_NAME, tempList);
		}
		// ����ֻ����ʱ������,�ӱ��治��bp
		originBill.setChildren(BusinessBVO.class, null);

		AggDeclarationVO[] saveVOs = { originBill };
		// �ȵ�������ȫ�µļ�¼
		AggDeclarationVO[] savedVO = insert(saveVOs, saveVOs);
		String pk_main = null;
		if (savedVO != null && savedVO.length > 0) {
			pk_main = savedVO[0].getPrimaryKey();
		}
		// insert �ӱ�
		if (saveBVOListMap.size() > 0 && pk_main != null) {
			for (List<ISuperVO> bvos : saveBVOListMap.values()) {
				for (ISuperVO bvo : bvos) {
					bvo.setAttributeValue("pk_declaration", pk_main);
					bvo.setAttributeValue("pk_group", originBill.getParentVO().getPk_group());
					bvo.setAttributeValue("pk_org", originBill.getParentVO().getPk_org());
					bvo.setAttributeValue("pk_org_v", originBill.getParentVO().getPk_org_v());
					bvo.setAttributeValue("dr", 0);
				}
				getBaseDAO().insertVOList(bvos);
			}
		}

		// ������֯��������,��fix
		dataFix(originBill.getParentVO().getPk_org());

	}

	/**
	 * ������� ������:�и����ɰ�num�����int,��������,Ԫ�����ֶ����͸Ĵ�һ��~
	 * 
	 * @return
	 * @author Ares.Tank
	 * @param class1
	 * @date 2018��10��8�� ����5:19:39
	 * @description
	 */
	private int generaRowNum(String tableName) {
		if (null == tableName) {
			return 0;
		}
		String sqlStr = "select max(num) as countsum from " + tableName;
		Integer sum = 0;
		List objs = null;
		try {
			objs = (List) getBaseDAO().executeQuery(sqlStr, new ColumnListProcessor());
		} catch (DAOException e) {
			e.printStackTrace();
		}
		if (null != objs) {
			for (Object obj : objs) {
				if (null != obj) {
					sum = (Integer) obj;
				}
			}
		}
		return sum;
	}

	/**
	 * �������ֱ�ļ�¼�����ó�Ψһһ������ļ�¼ (�˽���û������,�����ĸ��ֱ�,ͨ��һ��Ĭ���������������ӱ�)
	 * 
	 * @throws BusinessException
	 * @author Ares.Tank
	 * @date 2018��9��25�� ����4:05:42
	 * @description
	 */
	private void dataFix(String pk_org) throws BusinessException {
		// ������֯��������
		String sqlStr = "select pk_declaration from twhr_declaration " + "where twhr_declaration.pk_org = '" + pk_org
				+ "' and dr = 0";
		String keyOfDeclaration = null;
		List pk_declarations = (List) getBaseDAO().executeQuery(sqlStr, new ColumnListProcessor());
		if (null == pk_declarations) {
			// ����֯��ֱ�Ӵ�
			return;
		} else {
			for (Object obj : pk_declarations) {
				if (null == obj) {
					return;
				}
				keyOfDeclaration = obj.toString();

			}
		}

		sqlStr = "update declaration_parttime SET rowno = null,pk_declaration = '" + keyOfDeclaration + "' "
				+ " where pk_org = '" + pk_org + "' and dr = 0";
		getBaseDAO().executeUpdate(sqlStr);

		sqlStr = "update declaration_nonparttime SET rowno = null,pk_declaration = '" + keyOfDeclaration + "'"
				+ " where pk_org = '" + pk_org + "' and dr = 0";
		getBaseDAO().executeUpdate(sqlStr);

		sqlStr = "update declaration_business SET rowno = null,pk_declaration = '" + keyOfDeclaration + "'"
				+ " where pk_org = '" + pk_org + "' and dr = 0";
		getBaseDAO().executeUpdate(sqlStr);

		sqlStr = "update declaration_company SET rowno = null,pk_declaration = '" + keyOfDeclaration + "'"
				+ " where pk_org = '" + pk_org + "' and dr = 0";
		getBaseDAO().executeUpdate(sqlStr);

		// ɾ��������
		sqlStr = "delete from twhr_declaration " + " where (pk_declaration <> '" + keyOfDeclaration
				+ "' or dr = null) " + "and pk_org = '" + pk_org + "'";
		getBaseDAO().executeUpdate(sqlStr);

	}

	/**
	 * ��н�ʷ��ź�������������ɹ�˾���䱣�� dateMoth �������ڵ��·� pk_hrorg ������Դ��֯ pk_group ����
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void generatCompanyBVO(UFDate dateMonth, String pk_hrorg, String pkGroup) throws BusinessException {
		// н�ʷ�������map<н�ʷ���,map<н���ڼ�,map<��Ա,���ο�˰���>>>
		Map<String, Map<String, Map<String, Double>>> payMap = new HashMap<>();
		Set<String> orgs = LegalOrgUtilsEX.getOrgsByLegal(pk_hrorg, pkGroup);
		// ���
		String cyear = dateMonth.getYear() + "";
		// �·�
		String cperiod = dateMonth.getStrMonth();
		// ��ί����
		Set<String> ferryWaclassSet = getFerryWaclass();
		for (String pk_org : orgs) {
			UFDouble heathRate = SysInitQuery4TWHR.getParaDbl(pk_org, "TWEP0002");
			if (null == heathRate) {
				throw new BusinessException("�o���ҵ��a�䱣�U�M��(����)�O�ã�Ո�z���Զ��x�(TWEP0002)���O�����ݡ�");
			}
			// ������ڷ��ϵķǼ�ְ��Ա��Ϣ
			List<CompanyBVO> saveList = new ArrayList<>();
			AggDeclarationVO avo = DeclarationUtils.getAggDeclarationVO();
			avo.getParentVO().setPk_group(pkGroup);
			avo.getParentVO().setPk_org(pk_org);

			// �³�ʱ��
			String startDayOfMonth = getStartDay(dateMonth);
			// ���³�ʱ��
			String endDayOfMonth = getEndDay(dateMonth);
			// ɾ������֯���µļ�¼
			String sqlStr = " delete from declaration_company " + " where pay_date >= '" + startDayOfMonth + "' "
					+ " and pay_date < '" + endDayOfMonth + "' " + "and vbdef1 = '" + pk_org + "' and dr = 0 ";
			getBaseDAO().executeUpdate(sqlStr);
			// �ҳ���˾���䱣�ѵ�������
			sqlStr = "select max(num) as countsum from declaration_company ";
			Integer sum = 0;
			List objs = (List) getBaseDAO().executeQuery(sqlStr, new ColumnListProcessor());
			if (null != objs) {
				for (Object obj : objs) {
					if (null != obj) {
						sum = (Integer) obj;
					}
				}
			}
			if (sum == null || sum < 0) {
				sum = 0;
			}
			/**
			 * ��н�ʷ����в�ѯ�÷�����֯�ڸ������н��й��������������н�ʷ���������,��д����˾���䱣�ѵ�ҳǩ
			 **/
			// �Ӷ����������������÷�����֯�ڸø����·��Ѿ����й�������������� н�ʷ�����н���ڼ�
			sqlStr = " select pay_date,pk_wa_class,pk_wa_period from twhr_healthcaculateflag " + " where pay_date >= '"
					+ startDayOfMonth + "' and pay_date < '" + endDayOfMonth + "' " + " and pk_org = '" + pk_org + "' ";
			List<Map<String, String>> resultList = (List<Map<String, String>>) getBaseDAO().executeQuery(sqlStr,
					new MapListProcessor());
			if (resultList != null && resultList.size() > 0) {
				// ����н�ʷ���,н���ڼ�,������֯��wa_data���ҳ�������Ա����
				for (Map<String, String> rowMap : resultList) {
					if (rowMap == null || rowMap.size() <= 0 || null == rowMap.get("pk_wa_class")
							|| null == rowMap.get("pk_wa_period")) {
						continue;
					}
					String pk_wa_class = rowMap.get("pk_wa_class");
					String pk_wa_period = rowMap.get("pk_wa_period");
					String payDateStr = rowMap.get("pay_date");
					// tank ��ί���������� 2020��1��30��22:30:02
					if (pk_wa_class != null && ferryWaclassSet.contains(pk_wa_class)) {
						continue;
					}
					// ��ȡ���ڼ䱾н�ʷ���
					getPay("null", pk_org, pk_wa_class, pk_wa_period, payMap);
					if (payMap.get(pk_org + "-" + pk_wa_class) == null || null == payDateStr
							|| payMap.get(pk_org + "-" + pk_wa_class).get(pk_wa_period) == null) {
						continue;
					}
					UFDate payDate = new UFDate(payDateStr);
					// map<��Ա,���ο�˰���>
					Map<String, Double> psnDataMap = payMap.get(pk_org + "-" + pk_wa_class).get(pk_wa_period);
					for (String pkPsndoc : psnDataMap.keySet()) {
						// ѭ��wa_data����
						if (null == pkPsndoc || null == psnDataMap.get(pkPsndoc)) {
							continue;
						}
						CompanyBVO newVO = new CompanyBVO();
						// �к�
						newVO.setNum(++sum);
						// н�ʷ���
						newVO.setPk_wa_class(pk_wa_class);
						// ��������
						newVO.setPay_date(payDate);
						// �������(���ο۶�����)
						newVO.setPay_money(getPay(pkPsndoc, pk_org, pk_wa_class, pk_wa_period, payMap));
						// ��Ա
						newVO.setPk_psndoc(pkPsndoc);
						// ��ѯԱ���Ļ�����Ϣ:
						// ����,�Ƿ����,�Ƿ���ְ����Ͷ���������ڼ���,
						// Ͷ����ʼ����,Ա��Ͷ������,н���ڼ俪ʼ����,н�ʽ�������
						Map<String, Object> baseInfo = getPsnInfo(pkPsndoc, pk_org, payDate, pk_wa_period);
						if (baseInfo == null) {
							continue;
						}
						// ���Ա���ǹ���,�򼶾�Ϊ0
						if (baseInfo.get("ishirer") != null && baseInfo.get("ishirer").equals("Y")) {
							// Ͷ���ܶ� (����)
							newVO.setTotalinsure(UFDouble.ZERO_DBL);
						} else {
							// ���Ա����ѡ"������Ͷ����������Ӌ��"
							if (baseInfo.get("istonextmonth") != null && baseInfo.get("istonextmonth").equals("Y")) {
								newVO.setTotalinsure(UFDouble.ZERO_DBL);
								if (baseInfo.get("healthbegindate") != null && baseInfo.get("periodstartdate") != null
										&& baseInfo.get("periodenddate") != null) {
									UFLiteralDate healthBeginDate = null;
									UFLiteralDate periodStartDate = null;
									UFLiteralDate periodEndDate = null;
									try {
										healthBeginDate = new UFLiteralDate(String.valueOf(baseInfo
												.get("healthbegindate")));
										periodStartDate = new UFLiteralDate(String.valueOf(baseInfo
												.get("periodstartdate")));
										periodEndDate = new UFLiteralDate(String.valueOf(baseInfo.get("periodenddate")));
									} catch (Exception e) {
										Debug.debug(e.getMessage());
									}
									if (healthBeginDate != null && periodStartDate != null && periodEndDate != null) {
										if (healthBeginDate.compareTo(periodStartDate) >= 0
												&& healthBeginDate.compareTo(periodEndDate) <= 0) {
											// Ͷ����ʼ���ڽ��ڴ�н���ڼ�,����Ϊ0
											newVO.setTotalinsure(UFDouble.ZERO_DBL);
										} else if (healthBeginDate.getDateAfter(60).compareTo(periodStartDate) >= 0
												&& healthBeginDate.getDateAfter(60).compareTo(periodEndDate) <= 0) {
											// Ͷ����ʼ����+60������н���ڼ�,����Ϊ2������
											if (baseInfo.get("healthgrade") != null) {
												UFDouble graded = new UFDouble(String.valueOf(baseInfo
														.get("healthgrade")));
												newVO.setTotalinsure(graded.multiply(2));
											}
										} else {
											// Ͷ����ʼ����+60������н���ڼ�,����Ϊ2������
											if (baseInfo.get("healthgrade") != null) {
												newVO.setTotalinsure(new UFDouble(String.valueOf(baseInfo
														.get("healthgrade"))));
											}
										}
									}
								}
							} else {
								// ����pay_date��ѯԱ����Ͷ������,���������д,����Ϊ0;
								if (baseInfo.get("healthgrade") != null) {
									UFDouble grade = UFDouble.ZERO_DBL;
									try {
										grade = new UFDouble(String.valueOf(baseInfo.get("healthgrade")));
									} catch (NumberFormatException e) {
										Debug.debug(e.getMessage());
										newVO.setTotalinsure(UFDouble.ZERO_DBL);
									}
									newVO.setTotalinsure(grade);
								} else {
									newVO.setTotalinsure(UFDouble.ZERO_DBL);
								}
							}
						}
						// ����
						newVO.setPk_dept(baseInfo.get("pk_dept") != null ? String.valueOf(baseInfo.get("pk_dept"))
								: null);
						if (StringUtils.isEmpty(newVO.getPk_dept())) {
							setPsnJobDept(pkPsndoc, payDate.toString(), newVO);
						}
						// �a�䱣�M�M��
						newVO.setReplenis_base(newVO.getPay_money().sub(newVO.getTotalinsure()));
						// ���䱣��
						// ȡ�����������߼�
						newVO.setCompany_bear(newVO.getReplenis_base().multiply(heathRate).div(100));
						// TS
						newVO.setTs(new UFDateTime());
						// ������֯
						newVO.setVbdef1(pk_org);
						// н�ʷ���
						newVO.setVbdef2(pk_wa_class);
						// н���ڼ�
						newVO.setVbdef3(pk_wa_period);
						// ��Ա
						newVO.setVbdef4(pkPsndoc);

						saveList.add(newVO);
					}
				}
			}
			if (pk_org.equals(pk_hrorg)) {
				// ��������ýڵ�ץȡ 50н�ʵ�����
				sqlStr = "select * from hi_psndoc_ptcost "
						+ " left join bd_psndoc on bd_psndoc.pk_psndoc = hi_psndoc_ptcost.pk_psndoc "
						+ " where declareformat = (select top 1 pk_defdoc "
						+ " from bd_defdoc where pk_defdoclist = '1001ZZ1000000001NEF9' and code = '50') "
						+ " and paydate >= '" + startDayOfMonth + "' and paydate < '" + endDayOfMonth + "' "
						+ " and bd_psndoc.pk_org = '" + pk_org + "'";
				List<PTCostVO> qResult = (List<PTCostVO>) getDao().executeQuery(sqlStr,
						new BeanListProcessor(PTCostVO.class));
				if (null != qResult) {
					for (PTCostVO vo : qResult) {
						if (null == vo) {
							continue;
						}
						CompanyBVO newVO = new CompanyBVO();
						// �к�
						newVO.setNum(++sum);

						// ����
						setPsnJobDept(vo.getPk_psndoc(), vo.getPaydate().toString(), newVO);

						// н�ʷ���
						newVO.setPk_wa_class(null);
						// ��������
						newVO.setPay_date(new UFDate(vo.getPaydate().toDate()));
						// �������(���ο۶�����)
						newVO.setPay_money(vo.getPayamount());
						// ��Ա
						newVO.setPk_psndoc(vo.getPk_psndoc());
						// Ͷ���ܶ� (����)--��ְ��Ա��Ͷ������
						newVO.setTotalinsure(getPsndocGrande(newVO.getPk_psndoc(), newVO.getPay_date()));
						// �a�䱣�M�M��--��ְ��Ա�ѻ�
						newVO.setReplenis_base(newVO.getPay_money().sub(newVO.getTotalinsure()));
						// ���䱣��
						newVO.setCompany_bear(newVO.getReplenis_base().multiply(heathRate).div(100));
						// TS
						newVO.setTs(new UFDateTime());
						// ������֯
						newVO.setVbdef1(pk_org);
						// н�ʷ���
						newVO.setVbdef2(null);
						// н���ڼ�
						newVO.setVbdef3(null);
						// ��Ա
						newVO.setVbdef4(vo.getPk_psndoc());

						saveList.add(newVO);
					}
				}
			}
			// ����Ա���걨��ʽ���л���
			saveList = totalVO(saveList, pk_org);

			Collection<CompanyAdjustBVO> adjVOs = this.getBaseDAO().retrieveByClause(
					CompanyAdjustBVO.class,
					"pk_org='" + pk_org + "' and adjustdate >= '" + startDayOfMonth + "' and adjustdate < '"
							+ endDayOfMonth + "'");
			if (adjVOs != null && adjVOs.size() > 0) {
				for (CompanyAdjustBVO adjvo : adjVOs) {
					CompanyBVO newVO = new CompanyBVO();
					// �к�
					newVO.setNum(++sum);
					// ����
					setPsnJobDept(adjvo.getPk_psndoc(), adjvo.getAdjustdate().toString(), newVO);
					// н�ʷ���
					newVO.setPk_wa_class(null);
					// ��������
					newVO.setPay_date(new UFDate(adjvo.getAdjustdate().toDate()));
					// �������(���ο۶�����)
					newVO.setPay_money(UFDouble.ZERO_DBL);
					// ��Ա
					newVO.setPk_psndoc(adjvo.getPk_psndoc());
					// Ͷ���ܶ� (����)
					newVO.setTotalinsure(adjvo.getAdjustamount());
					// TS
					newVO.setTs(new UFDateTime());
					// ������֯
					newVO.setVbdef1(pk_org);
					// н�ʷ���
					newVO.setVbdef2(null);
					// н���ڼ�
					newVO.setVbdef3(null);
					// ��Ա
					newVO.setVbdef4(adjvo.getPk_psndoc());
					// �a�䱣�M�M��
					newVO.setReplenis_base(newVO.getTotalinsure());
					// ���䱣��
					// ȡ�����������߼�
					newVO.setCompany_bear(newVO.getReplenis_base().multiply(heathRate).div(100));

					saveList.add(newVO);
				}
			}

			avo.setChildren(CompanyBVO.class, saveList.toArray(new CompanyBVO[0]));
			AggDeclarationVO[] saveVOs = { avo };
			// �ȵ�������ȫ�µļ�¼
			insert(saveVOs, saveVOs);
			// ������֯��������,��fix
			dataFix(avo.getParentVO().getPk_org());
		}
	}

	private void setPsnJobDept(String pk_psndoc, String payDate, CompanyBVO newVO) throws DAOException {
		// ����
		// ssx added on 2020-06-12
		// Redmine #35830 ������д�����߼�
		Collection<PsnJobVO> psnjobvos = this.getBaseDAO().retrieveByClause(
				PsnJobVO.class,
				"pk_psndoc='" + pk_psndoc + "' and '" + payDate
						+ "' between begindate and isnull(enddate, '9999-12-31')");

		String pk_dept = null;
		if (psnjobvos != null && psnjobvos.size() > 0) {
			UFLiteralDate maxBeginDate = null;
			for (PsnJobVO psnjobvo : psnjobvos) {
				if (maxBeginDate == null) {
					maxBeginDate = psnjobvo.getBegindate();
					pk_dept = psnjobvo.getPk_dept();
				} else {
					if (maxBeginDate.before(psnjobvo.getBegindate())) {
						pk_dept = psnjobvo.getPk_dept();
					}
				}
			}
		}

		newVO.setPk_dept(pk_dept);
		// end
	}

	/**
	 * ����Ա���걨��ʽ���л���
	 * 
	 * @param saveList
	 * @return
	 * @throws BusinessException
	 */
	private List<CompanyBVO> totalVO(List<CompanyBVO> saveList, String pk_org) throws BusinessException {
		// ֻ��50н�ʺ�ͬһ�µ�����,����ֻ����Ա����
		// ���һ�����ж��,��ֻ����������������һ�ʵļ���,�����ļ�����Ϊ��
		List<CompanyBVO> resultList = new ArrayList<>();
		Map<String, List<CompanyBVO>> psnListMap = new HashMap<>();
		// ѭ��list,����Ա���л���
		for (CompanyBVO bvo : saveList) {
			if (bvo.getVbdef4() != null) {
				if (psnListMap.get(bvo.getVbdef4()) == null) {
					List<CompanyBVO> psnList = new ArrayList<>();
					psnList.add(bvo);
					psnListMap.put(bvo.getVbdef4(), psnList);
				} else {
					psnListMap.get(bvo.getVbdef4()).add(bvo);
				}
			}
		}
		UFDouble heathRate = SysInitQuery4TWHR.getParaDbl(pk_org, "TWEP0002");
		// ����ÿ����,�Ƚ�ÿ�ʼ�¼,ֻ�������µļ���
		for (String pk_psndoc : psnListMap.keySet()) {
			// ���¼�¼����vo
			CompanyBVO lastPayDateCompanyVO = null;
			for (CompanyBVO cbvo : psnListMap.get(pk_psndoc)) {
				if (lastPayDateCompanyVO == null) {
					lastPayDateCompanyVO = cbvo;
				} else {
					// �Ƚ�������¼�ĸ�������,�ÿվɼ�¼�ļ���.
					if (cbvo.getPay_date().after(lastPayDateCompanyVO.getPay_date())) {
						// �ÿվɼ�¼
						lastPayDateCompanyVO.setTotalinsure(UFDouble.ZERO_DBL);
						// �a�䱣�M�M��
						lastPayDateCompanyVO.setReplenis_base(lastPayDateCompanyVO.getPay_money().sub(
								lastPayDateCompanyVO.getTotalinsure()));
						lastPayDateCompanyVO.setCompany_bear(lastPayDateCompanyVO.getReplenis_base()
								.multiply(heathRate).div(100));

						lastPayDateCompanyVO = cbvo;
					} else {
						// �ÿվɼ�¼
						cbvo.setTotalinsure(UFDouble.ZERO_DBL);
						cbvo.setReplenis_base(cbvo.getPay_money().sub(cbvo.getTotalinsure()));
						cbvo.setCompany_bear(cbvo.getReplenis_base().multiply(heathRate).div(100));
					}
				}
			}
		}
		// �������,���·�װlist
		for (List<CompanyBVO> psnList : psnListMap.values()) {
			if (psnList != null) {
				for (CompanyBVO saveVO : psnList) {
					resultList.add(saveVO);
				}
			}
		}
		return resultList;
	}

	/**
	 * ��ȡ��Ա�Ľ�������
	 * 
	 * @return
	 * @author Ares.Tank
	 * @throws BusinessException
	 * @date 2018��9��26�� ����11:53:54
	 * @description
	 */
	private UFDouble getPsndocGrande(String pk_psndoc, UFDate payDate) throws BusinessException {
		UFDouble num = UFDouble.ZERO_DBL;
		// �������ڲ�����Ա�Ľ�����¼
		// mod ���� 2018��10��26��15:09:37
		String sqlStr = " select isnull( SALARY_DECRYPT(g2.glbdef16),0) " + " from "
				+ PsndocDefTableUtil.getPsnHealthTablename() + " g2" + " where g2.pk_psndoc = '" + pk_psndoc + "' "
				+ " and g2.begindate <= '" + payDate.toString() + "' and isnull(g2.enddate,'9999-12-31')>='"
				+ payDate.toString() + "'  and g2.glbdef2 = '����' and dr = 0";
		List qDataList = (List) getDao().executeQuery(sqlStr, new ColumnListProcessor());
		for (Object numObj : qDataList) {

			try {
				num = new UFDouble(numObj.toString());
				return num;

			} catch (NumberFormatException e) {
				num = UFDouble.ZERO_DBL;
				Debug.error("������Ϣ����������,��Ա��Ϣ:" + pk_psndoc);
			}
		}
		return num;
	}

	/**
	 * ������Ա,н�ʷ���,н���ڼ��н�ʷ����л�ȡ'���ο�˰���'
	 * 
	 * @param pk_psndoc
	 * @param pk_wa_class
	 * @param pk_wa_period
	 * @return
	 * @throws DAOException
	 * @other payMap н�ʷ������� map<��֯-н�ʷ���,map<н���ڼ�,map<��Ա,���ο�˰����>>>
	 */
	private UFDouble getPay(String pk_psndoc, String pk_org, String pk_wa_class, String pk_wa_period,
			Map<String, Map<String, Map<String, Double>>> payMap) throws DAOException {
		UFDouble result = UFDouble.ZERO_DBL;
		// �ȴӻ����л�ȡ
		if (payMap.get(pk_org + "-" + pk_wa_class) != null
				&& payMap.get(pk_org + "-" + pk_wa_class).get(pk_wa_period) != null) {

			if (payMap.get(pk_org + "-" + pk_wa_class).get(pk_wa_period).get(pk_psndoc) != null) {
				result = new UFDouble(payMap.get(pk_org + "-" + pk_wa_class).get(pk_wa_period).get(pk_psndoc));
			}
			return result;
		}
		// ��н�ʷ��ű��в�����н�ʷ������ڼ������˵ı��ο�˰����
		String sqlStr = " select wa.pk_psndoc pk_psndoc,SALARY_DECRYPT(wa.f_4)  f_4" + " from wa_period period "
				+ " left join wa_data wa on (wa.pk_org = '" + pk_org + "' and wa.cperiod = period.cperiod "
				+ " and wa.cyear = period.cyear and wa.pk_wa_class = '" + pk_wa_class + "' ) "
				+ " where period.pk_wa_period = '" + pk_wa_period + "' ";
		List<DataVO> qResult = (List<DataVO>) getBaseDAO().executeQuery(sqlStr, new BeanListProcessor(DataVO.class));

		if (null == result || qResult.size() <= 0) {
			// Ϊ��Ҳ���һ��map����ʶ
			Map<String, Map<String, Double>> map = new HashMap<>();
			map.put(pk_wa_period, new HashMap<String, Double>());
			payMap.put(pk_org + "-" + pk_wa_class, map);
			return result;
		} else {
			// ����ѯ����,�����´�ֱ��ʹ��
			Map<String, Double> psnDataMap = new HashMap<>();
			Map<String, Map<String, Double>> datePsnMap = new HashMap<>();
			payMap.put(pk_org + "-" + pk_wa_class, datePsnMap);
			datePsnMap.put(pk_wa_period, psnDataMap);
			for (DataVO dataVO : qResult) {
				if (null != dataVO && null != dataVO.getPk_psndoc() && null != dataVO.getAttributeValue("f_4")) {
					String value = String.valueOf(dataVO.getAttributeValue("f_4"));
					if (value != null && !value.equals("null")) {
						psnDataMap.put(dataVO.getPk_psndoc(), Double.valueOf(value));
						if (dataVO.getPk_psndoc().equals(pk_psndoc)) {
							result = new UFDouble(Double.valueOf(value));
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * ����ʱ��String
	 * 
	 * @param date
	 * @return
	 * @author Ares.Tank
	 * @date 2018��9��27�� ����6:08:23
	 * @description
	 */
	private String getEndDay(UFDate sDate) {
		// UFDate sDate = new UFDate(dateStr + "-01 00:00:00");
		if (null == sDate) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sDate.toDate());
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, 1);
		UFDate date = new UFDate(calendar.getTime());
		if (date.getMonth() >= 10) {
			return date.getYear() + "-" + date.getMonth() + "-" + "01";
		} else {
			return date.getYear() + "-0" + date.getMonth() + "-" + "01";
		}
	}

	/**
	 * ��ȡ���µĿ�ʼʱ�� string
	 * 
	 * @param date
	 * @return
	 * @author Ares.Tank
	 * @date 2018��9��27�� ����6:08:20
	 * @description
	 */
	private String getStartDay(UFDate date) {
		if (null == date) {
			return null;
		}
		if (date.getMonth() >= 10) {
			return date.getYear() + "-" + date.getMonth() + "-" + "01";
		} else {
			return date.getYear() + "-0" + date.getMonth() + "-" + "01";
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nc.itf.twhr.ITwhr_declarationMaintain#writeBack4PTCost(nc.vo.twhr.
	 * twhr_declaration.AggDeclarationVO)
	 */
	@Override
	public void writeBack4PTCost(PTCostVO ptvo, String pk_org, String pk_group) throws BusinessException {
		AggDeclarationVO avo = DeclarationUtils.getAggDeclarationVO();
		avo.getParentVO().setPk_group(pk_group);
		avo.getParentVO().setPk_org(pk_org);

		// �ж��걨��ʽ
		int type = checkDeclareform(ptvo.getDeclareformat());

		// �����ɾ��,��ֱ��ɾ��
		String tableName = null;
		if (ptvo.getStatus() == VOStatus.DELETED) {
			if (DECLAREFORM_50 == type) {
				tableName = "declaration_parttime";

			} else if (DECLAREFORM_90AB == type) {
				tableName = "declaration_business";

			}
			if (tableName != null) {
				String sqlStr = " delete from " + tableName + " where pay_date >= '" + ptvo.getPaydate().toString()
						+ "' " + " and pay_date < '" + ptvo.getPaydate().getDateAfter(1).toString() + "' "
						+ "and vbdef4 = '" + ptvo.getPk_psndoc() + "' and dr = 0 ";
				getBaseDAO().executeUpdate(sqlStr);
			}
			return;
		} else if (ptvo.getStatus() == VOStatus.UPDATED) {
			if (DECLAREFORM_50 == type) {
				tableName = "declaration_parttime";

			} else if (DECLAREFORM_90AB == type) {
				tableName = "declaration_business";

			}
			if (tableName != null) {
				String sqlStr = " delete from " + tableName + " where pay_date >= '" + ptvo.getPaydate().toString()
						+ "' " + " and pay_date < '" + ptvo.getPaydate().getDateAfter(1).toString() + "' "
						+ "and vbdef4 = '" + ptvo.getPk_psndoc() + "' and dr = 0 ";
				getBaseDAO().executeUpdate(sqlStr);
			}
		}
		// �ж��Ƿ��������ɵ�����,����ͬһ�������
		SuperVO superVO = isExistPTCostRecord(ptvo.getPk_psndoc(), ptvo.getPaydate(), type);
		if (DECLAREFORM_50 == type) {
			if (ptvo.getTaxamount() != null && ptvo.getExtendamount().intValue() != 0 && ptvo.getExtendamount() != null
					&& ptvo.getExtendamount().intValue() != 0) {
				// 50��ʽ
				SuperVO[] svos = new SuperVO[1];
				PartTimeBVO newVO = null;
				// svos[0] = DeclarationUtils.getNonPartTimeBVO();
				if (null == superVO) {
					newVO = DeclarationUtils.getPartTimeBVO();
				} else {
					newVO = (PartTimeBVO) (superVO.clone());
				}

				// ��������:
				newVO.setPay_date(new UFDate(ptvo.getPaydate().toDate()));

				PsndocVO psndocvo = (PsndocVO) this.getBaseDAO().retrieveByPK(PsndocVO.class, ptvo.getPk_psndoc());

				// ����������
				newVO.setBeneficiary_name(psndocvo.getName2());
				// ���������֤��
				newVO.setBeneficiary_id(psndocvo.getId());
				// ����
				newVO.setPk_dept(null);

				// ���θ������
				newVO.setSingle_pay(ptvo.getTaxamount());
				// ���ο۽ɲ��䱣�շѽ��
				newVO.setSingle_withholding(ptvo.getExtendamount());

				// д��н�ʷ�����н���ڼ䡢������֯ ����ȡ������ı�ʶ
				newVO.setVbdef1(pk_org);
				// newVO.setVbdef2(pk_wa_class);
				// newVO.setVbdef3(pk_wa_period);
				newVO.setVbdef4(ptvo.getPk_psndoc());

				newVO.setPk_org(pk_org);
				newVO.setPk_group(pk_group);

				BaseDocVO TWNHHI03 = NCLocator.getInstance().lookup(IBasedocPubQuery.class)
						.queryBaseDocByCode(pk_org, "TWNHHI03");
				newVO.setInsurance_unit_code(TWNHHI03.getTextvalue());

				svos[0] = newVO;
				avo.setChildren(PartTimeBVO.class, svos);
			}
		} else if (DECLAREFORM_90AB == type) {
			// 90AB��ʽ
			SuperVO[] svos = new SuperVO[1];
			BusinessBVO newVO = null;
			// svos[0] = DeclarationUtils.getNonPartTimeBVO();
			if (null == superVO) {
				newVO = DeclarationUtils.getBusinessBVO();
			} else {
				newVO = (BusinessBVO) (superVO.clone());
			}

			// ��������:
			newVO.setPay_date(new UFDate(ptvo.getPaydate().toDate()));

			Map<String, Object> baseInfo = getBaseInfo(ptvo.getPk_psndoc(), pk_org, newVO.getPay_date());

			// ����������
			newVO.setBeneficiary_name(baseInfo.get("beneficiary_name").toString());
			// ���������֤��
			newVO.setBeneficiary_id(baseInfo.get("beneficiary_id").toString());
			// ����
			newVO.setPk_dept(null);

			// ���θ������
			newVO.setSingle_pay(ptvo.getTaxamount());
			// ���ο۽ɲ��䱣�շѽ��
			newVO.setSingle_withholding(ptvo.getExtendamount());

			// д��н�ʷ�����н���ڼ䡢������֯ ����ȡ������ı�ʶ
			newVO.setVbdef1(pk_org);
			// newVO.setVbdef2(pk_wa_class);
			// newVO.setVbdef3(pk_wa_period);
			newVO.setVbdef4(ptvo.getPk_psndoc());

			svos[0] = newVO;
			avo.setChildren(BusinessBVO.class, svos);

		}
		if (null == avo) {
			return;
		}
		AggDeclarationVO[] saveVOs = { avo };
		// �ȵ�������ȫ�µļ�¼
		insert(saveVOs, saveVOs);
		// ������֯��������,��fix
		dataFix(avo.getParentVO().getPk_org());

	}

	// ��ȡ����������Ϣ
	private Map<String, Object> getBaseInfo(String psndoc, String pk_org, UFDate payDate) throws BusinessException {
		// Map<String,Object> resultMap = new HashMap<>();
		String sqlStr = "SELECT distinct "
				+ SQLHelper.getMultiLangNameColumn("psn.name")
				+ " as beneficiary_name, hi_psndoc_cert.id as beneficiary_id, "
				+ " org.glbdef40 as insurance_unit_code ,job.pk_dept as pk_dept from bd_psndoc psn "
				+ " inner join "
				+ PsndocDefTableUtil.getPsnHealthTablename()
				+ " heal on psn.pk_psndoc = heal.pk_psndoc and '"
				+ payDate.toStdString()
				+ "' between begindate and isnull(enddate,'9999-12-31') and glbdef2='����'"
				+ " left join hi_psndoc_cert on (hi_psndoc_cert.pk_psndoc = psn.pk_psndoc and hi_psndoc_cert.dr = 0 and hi_psndoc_cert.iseffect = 'Y')"
				+ " inner join bd_psnidtype on hi_psndoc_cert.idtype = bd_psnidtype.pk_identitype and "
				+ " 	( ( NVL(psn.glbdef7,'N')='Y' AND ( heal.glbdef5= ( SELECT  pk_defdoc FROM bd_defdoc WHERE code = 'HT01' "
				+ " AND pk_defdoclist =  ( SELECT pk_defdoclist  FROM bd_defdoclist WHERE  code = 'TWHR007')) AND bd_psnidtype.code='TW03')) or "
				// MOD by ssx on 2020-05-04
				// #34819, �����������ꑼ���ż���� = 'HT08' �Ğ� in ('HT08','HT09')
				+ "(heal.glbdef5 in (select pk_defdoc from bd_defdoc where code in ('HT08','HT09') and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'TWHR007')) and bd_psnidtype.code='TW03') or bd_psnidtype.code = 'TW01') "
				// end MOD
				+ " left join org_hrorg org on psn.pk_org = org.pk_hrorg "
				+ " left join hi_psnjob job on (psn.pk_psndoc = job.pk_psndoc and job.begindate <= '"
				+ payDate.toStdString() + "' and isnull(job.enddate,'9999-12-31') >= '" + payDate.toStdString()
				+ "' and job.dr = 0)  WHERE psn.pk_psndoc = '" + psndoc + "'" + " and org.pk_hrorg = '" + pk_org + "'"
				+ " and psn.dr = 0 and org.dr = 0 and job.dr = 0 ";
		List<Map> sqlResultMapList = (List<Map>) getBaseDAO().executeQuery(sqlStr, new MapListProcessor());
		if (null != sqlResultMapList) {

			for (Map sqlResultMap : sqlResultMapList) {
				if (null != sqlResultMap) {
					return sqlResultMap;
				}

			}

		}

		return new HashMap();
	}

	/**
	 * ��ѯԱ���Ļ�����Ϣ: ����,�Ƿ����,�Ƿ���ְ����Ͷ���������ڼ���, Ͷ����ʼ����,Ա��Ͷ������,н���ڼ俪ʼ����,н�ʽ�������
	 * 
	 * @param psndoc
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, Object> getPsnInfo(String psndoc, String pk_org, UFDate payDate, String pk_wa_period)
			throws BusinessException {
		// Map<String,Object> resultMap = new HashMap<>();
		String sqlStr = " select g2.begindate healthbegindate,isnull(SALARY_DECRYPT(g2.glbdef16),0) healthgrade, "
				+ " period.cstartdate periodstartdate,period.cenddate periodenddate, "
				+ " isnull(psn.istonextmonth,'N') istonextmonth,job.pk_dept pk_dept,  "
				+ " case g2.glbdef5 when '1001ZZ10000000001J5L' then 'Y' else 'N' end ishirer "
				+ " FROM bd_psndoc psn " + " LEFT JOIN hi_psndoc_glbdef2 g2 " + " ON ( psn.pk_psndoc = g2.pk_psndoc "
				+ " AND g2.begindate <= '" + payDate.toStdString() + "' " + " AND isnull(g2.enddate,'9999-12-31') >= '"
				+ payDate.toStdString() + "' " + " AND g2.glbdef2 = '����') "
				+ " left join wa_period period on (period.pk_wa_period = '" + pk_wa_period + "') "
				+ " left join hi_psnjob job on (psn.pk_psndoc = job.pk_psndoc  " + " and job.begindate <= '"
				+ payDate.toStdString() + "' and isnull(job.enddate,'9999-12-31') >= '" + payDate.toStdString()
				+ "' and job.dr = 0) " + " where psn.pk_psndoc ='" + psndoc + "' " + " AND period.dr = 0 ";
		List<Map> sqlResultMapList = (List<Map>) getBaseDAO().executeQuery(sqlStr, new MapListProcessor());
		if (null != sqlResultMapList) {

			for (Map sqlResultMap : sqlResultMapList) {
				if (null != sqlResultMap) {
					return sqlResultMap;
				}

			}

		}

		return new HashMap();
	}

	/**
	 * �ж��걨��ʽ�Ƿ�Ϊ50н��
	 * 
	 * @param pk_wa_class
	 * @return 50н��:1 90AB:2 ����:0
	 * @throws BusinessException
	 * @date 2018��9��22�� ����12:35:51
	 * @description
	 */
	private int checkDeclareform(String pk_declareform) throws BusinessException {
		if (null == pk_declareform) {
			return DECLAREFORM_OTHER;
		}
		int type = 0;
		String dbCode = null;
		String sqlStr = "select code from bd_defdoc " + " where bd_defdoc.pk_defdoc= '" + pk_declareform + "'";
		// �ж��걨��ʽ����
		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		List list = (List) iUAPQueryBS.executeQuery(sqlStr, new ColumnListProcessor());
		if (null != list && list.size() > 0) {
			for (Object obj : list) {
				if (null != obj) {
					dbCode = obj.toString();
					if (dbCode.equals("50")) {
						return DECLAREFORM_50;
					} else if (dbCode.equals("9A")) {
						return DECLAREFORM_90AB;
					} else if (dbCode.equals("9B")) {
						return DECLAREFORM_90AB;
					}
				}
			}
		}

		return DECLAREFORM_OTHER;
	}

	/**
	 * �ж����ݿ����Ƿ��Ѿ�����������õ����ɼ�¼,����оͷ�������
	 * 
	 * @param psndoc
	 *            ��Ա
	 * @param payDate
	 *            ������
	 * @param type
	 *            �걨���� DECLAREFORM_50,DECLAREFORM_90A DECLAREFORM_90B
	 * @return
	 * @author Ares.Tank
	 * @throws BusinessException
	 * @date 2018��9��26�� ����11:51:33
	 * @description
	 */
	private SuperVO isExistPTCostRecord(String psndoc, UFLiteralDate payDate, int type) throws BusinessException {
		if (null == psndoc || null == payDate) {
			return null;
		}

		String tableName;
		Class clazz = null;
		if (DECLAREFORM_50 == type) {
			tableName = "declaration_parttime";
			clazz = PartTimeBVO.class;
		} else if (DECLAREFORM_90AB == type) {
			tableName = "declaration_business";
			clazz = BusinessBVO.class;
		} else {
			return null;
		}
		String sqlStr = " select sub.*  from " + tableName + " sub " + " where sub.pay_date >= '" + payDate.toString()
				+ "' " + " and sub.pay_date < '" + payDate.getDateAfter(1).toString() + "' " + "and sub.vbdef4 = '"
				+ psndoc + "' and sub.dr = 0 ";
		List result = (List) getBaseDAO().executeQuery(sqlStr, new BeanListProcessor(clazz));
		String pkStringRtn = null;
		if (null != result) {
			for (Object obj : result) {
				if (null != obj) {
					return (SuperVO) obj;
				}
			}
		}
		return null;

	}

	/**
	 * ��������
	 * 
	 * @param rtn
	 * @return
	 */
	private UFDouble dealRtn(UFDouble rtn) {
		if (rtn == null) {
			return rtn;
		}
		double f = rtn.doubleValue();
		BigDecimal b = new BigDecimal(f);
		double f1 = b.setScale(0, RoundingMode.HALF_UP).doubleValue();
		return new UFDouble(f1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getPkByCode(String code, String flag, LoginContext context) throws BusinessException {
		if (flag == null) {
			return null;
		}
		String pk = null;
		switch (flag) {
		case "psn":
			List<Map<String, String>> psn_ret = (List<Map<String, String>>) this
					.getBaseDAO()
					.executeQuery(
							"select distinct "
									+ SQLHelper.getMultiLangNameColumn("psn.name")
									+ " name ,psn.pk_psndoc,hi_psndoc_cert.id  from bd_psndoc psn "
									+ " inner join "
									+ PsndocDefTableUtil.getPsnHealthTablename()
									+ " heal on psn.pk_psndoc = heal.pk_psndoc and heal.glbdef2='����' and begindate=(select max(begindate) from "
									+ PsndocDefTableUtil.getPsnHealthTablename()
									+ " where pk_psndoc=heal.pk_psndoc and glbdef2=heal.glbdef2)"
									+ " left join hi_psndoc_cert on (hi_psndoc_cert.pk_psndoc = psn.pk_psndoc and hi_psndoc_cert.dr = 0 and hi_psndoc_cert.iseffect = 'Y')"
									+ " inner join bd_psnidtype on hi_psndoc_cert.idtype = bd_psnidtype.pk_identitype and "
									+ " 	( ( isnull(psn.glbdef7,'N')='Y' AND ( heal.glbdef5= ( SELECT  pk_defdoc FROM bd_defdoc WHERE code = 'HT01' "
									+ " AND pk_defdoclist =  ( SELECT pk_defdoclist  FROM bd_defdoclist WHERE  code = 'TWHR007')) AND bd_psnidtype.code='TW03')) or "
									// MOD by ssx on 2020-05-04
									// #34819, �����������ꑼ���ż���� = 'HT08' �Ğ� in
									// ('HT08','HT09')
									+ " (heal.glbdef5 in (select pk_defdoc from bd_defdoc where code in ('HT08','HT09') and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'TWHR007')) and bd_psnidtype.code='TW03') or bd_psnidtype.code = 'TW01') "
									// end MOD
									+ "where psn.dr = 0", new MapListProcessor());
			Map<String, String> psnmap = new HashMap<String, String>();
			for (Map<String, String> map : psn_ret) {
				psnmap.put(map.get("id"), map.get("pk_psndoc") + "&" + map.get("name"));
			}
			return psnmap;
		case "corp":
			Collection<OrgVO> vos = (List<OrgVO>) this.getBaseDAO().retrieveByClause(OrgVO.class,
					" code = '" + code + "'");
			if (vos == null || vos.size() == 0) {
				return null;
			}
			return vos.toArray(new OrgVO[0])[0];
		case "dept":
			List<Map<String, String>> dept_ret = (List<Map<String, String>>) this.getBaseDAO().executeQuery(
					"select pk_dept from org_dept where code ='" + code + "'", new MapListProcessor());
			if (dept_ret == null || dept_ret.size() == 0) {
				return null;
			} else if (dept_ret.get(0) != null && dept_ret.get(0).get("pk_dept") != null) {
				pk = dept_ret.get(0).get("pk_dept");
			}
			return pk;
		case "waclass":
			List<Map<String, String>> wa_ret = (List<Map<String, String>>) this.getBaseDAO().executeQuery(
					"select pk_wa_class from wa_waclass where code = '" + code + "'", new MapListProcessor());
			if (wa_ret == null || wa_ret.size() == 0) {
				return null;
			} else if (wa_ret.get(0) != null && wa_ret.get(0).get("pk_wa_class") != null) {
				pk = wa_ret.get(0).get("pk_wa_class");
			}
			break;
		case "period":
			StringBuffer sb = new StringBuffer();
			sb.append("select pk_wa_period from wa_period per ")
					.append(" left join wa_waclass wa on wa.PK_PERIODSCHEME = per.PK_PERIODSCHEME ")
					.append(" where per.cyear = '").append(code.substring(0, 4)).append("' and per.cperiod = '")
					.append(code.substring(4, 6)).append("' and wa.code = '").append(code.substring(6)).append("'");
			List<Map<String, String>> period_ret = (List<Map<String, String>>) this.getBaseDAO().executeQuery(
					sb.toString(), new MapListProcessor());
			if (period_ret == null || period_ret.size() == 0) {
				return null;
			} else if (period_ret.get(0) != null && period_ret.get(0).get("pk_wa_period") != null) {
				pk = period_ret.get(0).get("pk_wa_period");
			}
			break;
		default:
			break;
		}
		return pk;
	}

	/**
	 * ��ѯ���и�ί����
	 * 
	 * @param pk_org
	 * @return
	 * @throws DAOException
	 */
	private Set<String> getFerryWaclass() throws DAOException {
		String sql = "select PK_WA_CLASS from WA_WACLASS where nvl(ISFERRY,'N') = 'Y'";
		@SuppressWarnings("unchecked")
		Set<String> FWSet = (Set<String>) getBaseDAO().executeQuery(sql, new ResultSetProcessor() {
			private Set<String> rsSet = new HashSet<>();

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while (rs.next()) {
					rsSet.add(rs.getString(1));
				}
				return rsSet;
			}
		});
		return FWSet;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String[] insertCompanyAdjustVOs(String pk_org, CompanyAdjustBVO[] vos) throws BusinessException {
		Collection<DeclarationHVO> hvos = this.getBaseDAO().retrieveByClause(DeclarationHVO.class,
				"pk_org='" + pk_org + "'");

		OrgVO orgvo = null;
		if (!StringUtils.isEmpty(pk_org)) {
			orgvo = (OrgVO) this.getBaseDAO().retrieveByPK(OrgVO.class, pk_org);
		}

		for (CompanyAdjustBVO vo : vos) {
			vo.setPk_declaration(hvos.toArray(new DeclarationHVO[0])[0].getPk_declaration());
			if (orgvo != null) {
				vo.setPk_org_v(orgvo.getPk_vid());
			}
		}

		String[] pks = this.getBaseDAO().insertVOArray(vos);

		this.getBaseDAO().executeUpdate(
				"update declaration_companyadj set dr=0 where pk_companyadj in (" + new InSQLCreator().getInSQL(pks)
						+ ")");

		return pks;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int updateCompanyAdjustVOs(String pk_org, CompanyAdjustBVO[] vos) throws BusinessException {
		Collection<DeclarationHVO> hvos = this.getBaseDAO().retrieveByClause(DeclarationHVO.class,
				"pk_org='" + pk_org + "'");

		for (CompanyAdjustBVO vo : vos) {
			vo.setPk_declaration(hvos.toArray(new DeclarationHVO[0])[0].getPk_declaration());
		}

		int count = this.getBaseDAO().updateVOArray(vos);

		for (CompanyAdjustBVO vo : vos) {
			this.getBaseDAO().executeUpdate(
					"update declaration_companyadj set dr=0 where pk_companyadj = '" + vo.getPk_companyadj() + "'");
		}

		return count;
	}

	@Override
	public void deleteCompanyAdjustVOs(CompanyAdjustBVO[] vos) throws BusinessException {
		this.getBaseDAO().deleteVOArray(vos);
	}

}
