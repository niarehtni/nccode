package nc.pubimpl.wa.paydata.nhiservice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.InSQLCreator;
import nc.impl.wa.paydata.nhicalculate.TaiwanNHICalculator;
import nc.itf.twhr.ICalculateTWNHI;
import nc.itf.twhr.ITwhr_declarationMaintain;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.pubitf.para.DeclarationUtils;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.para.SysInitQuery4TWHR;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.vo.bm.util.SQLHelper;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;
import nc.vo.twhr.twhr_declaration.BusinessBVO;
import nc.vo.twhr.twhr_declaration.NonPartTimeBVO;
import nc.vo.twhr.twhr_declaration.PartTimeBVO;

import org.apache.commons.lang.StringUtils;

public class CalculateTWNHIImpl implements ICalculateTWNHI {

	private BaseDAO dao = new BaseDAO();

	private static final String PART_TIME_PSN_MAP_KEY = "partTimePsn";

	private static final String NOT_PART_TIME_PSN_MAP_KEY = "notPartTimePsn";
	// �걨��ʽ:50н��:1 90A:2 90B:3 ����:0
	private static final int DECLAREFORM_50 = 1;
	private static final int DECLAREFORM_90A = 2;
	private static final int DECLAREFORM_90B = 3;

	private static final int DECLAREFORM_OTHER = 0;

	// 90A/Bн�ʷ�����˰����
	private static final double WA_DATA_FOR_90A = -1;
	private static final double WA_DATA_FOR_90B = -1;

	@Override
	public void calculate(String pk_org, String acc_year, String acc_month) throws Exception {
		TaiwanNHICalculator calcBaseObj = new TaiwanNHICalculator(pk_org, acc_year, acc_month);

		calcBaseObj.Calculate();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map> getNHIClassMap() throws BusinessException {
		String strSQL = "SELECT  pk_infoset , infoset_code, vo_class_name ";
		strSQL += " FROM    hr_infoset ";
		strSQL += " WHERE infoset_code IN ( select code from bd_defdoc where pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'TWHR000') )";
		return (List<Map>) dao.executeQuery(strSQL, new MapListProcessor());
	}

	/**
	 * �����������
	 */
	@Override
	public void updateExtendNHIInfo(String pk_group, String pk_hrorg, String pk_wa_class, String pk_periodscheme,
			String pk_wa_period, UFDate payDate) throws BusinessException {
		// tank log
		UFDateTime startTime = new UFDateTime();
		Logger.error("-------�����������㿪ʼ,ʱ��:" + startTime.toStdString() + "-------");
		// ����������Դ�µ����з�����֯
		Set<String> legalOrgs = LegalOrgUtilsEX.getOrgsByLegal(pk_hrorg, pk_group);
		// ����֮ǰ,ͬ����֯��,ͬ��н�ʷ���,ͬ���ڼ�ļ�¼
		delExtendNHIInfo(pk_group, pk_hrorg, pk_wa_class, pk_wa_period);
		for (String pk_org : legalOrgs) {
			// ȡ�ͽ������ò���
			// ������Ϊ��ʱ�������÷���
			// ssx added for 2nd health ins on 2017-07-22
			if (null == SysInitQuery.getParaBoolean(pk_org, "TWHR01")
					|| !SysInitQuery.getParaBoolean(pk_org, "TWHR01").booleanValue()) {
				continue;
			}
			// ��ίн�ʷ��������� tank 2020��1��14��14:11:12
			if (isFerry(pk_wa_class, pk_org)) {
				// ��д�����־
				updateHeathCaculate(pk_org, pk_wa_class, pk_wa_period, payDate);
				return;
			}
			// ȡ�ˆT�б�
			String[] pk_psndocs = getWaDataPsndocs(pk_org, pk_wa_class, pk_periodscheme, pk_wa_period);
			// PeriodVO pvo = (PeriodVO) dao.retrieveByPK(PeriodVO.class,
			// pk_wa_period);// н���ڼ�

			// List<String> inserted_psndocs = new ArrayList<String>();
			// SimpleDocServiceTemplate service = new SimpleDocServiceTemplate(
			// "TWHRGLBDEF");
			// List<PsndocDefVO> psnLaborInfoVOs = new ArrayList<PsndocDefVO>();
			// List<String> updatedPsn = new ArrayList<String>();
			// ����н�ʷ���:
			int waclassType = checkWaClass(pk_wa_class);
			// �洢��д����
			Map<Class<? extends ISuperVO>, List<SuperVO>> writeBackListMap = new HashMap<>();
			// itemKey
			String itemKey = getItemKey(pk_wa_class, pk_org, pk_wa_period);
			if (DECLAREFORM_50 == waclassType) {
				// 50н��
				// ���ּ�ְ��Ա�ͷǼ�ְ��Ա
				Map<String, Set<String>> checkPartTimePsn = checkPartTimePsn(pk_psndocs, payDate, pk_wa_period);

				if (null != checkPartTimePsn.get(NOT_PART_TIME_PSN_MAP_KEY)) {
					// �Ǽ�ְ��Ա�ļ���
					Map<String, UFDouble> curAmountMap = getCurrentPeriodAmount4NotPartTimePsn(pk_group, pk_org,
							pk_wa_class, pk_wa_period,
							checkPartTimePsn.get(NOT_PART_TIME_PSN_MAP_KEY).toArray(new String[0]), payDate);

					for (String pk_psndoc : checkPartTimePsn.get(NOT_PART_TIME_PSN_MAP_KEY)) {
						if (null == pk_psndoc) {
							continue;
						}
						// �ҷǼ��ˆTÿ���˱�����Ҫ�۶���������н�ʽ��
						UFDouble curAmount = curAmountMap.get(pk_psndoc);
						curAmount = curAmount == null ? UFDouble.ZERO_DBL : curAmount;

						// �ų�����Ҫ�۶�����������Ա
						if (needUpdateExNHI(pk_group, pk_org, pk_wa_period, pk_psndoc, curAmount)) {
							// updatedPsn.add(pk_psndoc);
							// ���ϱ��ڵ�ȫ���ۼƽ��
							UFDouble annaAmount = curAmount.add(getLastPeriodAnnualSum(pk_group, pk_org, pk_wa_class,
									payDate, pk_psndoc));
							// ����Ǽ�ְ��Ա���䱣��
							UFDouble curInsAmount = getCurrentPeriodInsAmount4NotPartTimePsn(pk_group, pk_org,
									pk_wa_class, pk_wa_period, pk_psndoc, annaAmount, curAmount, payDate);
							// �Ǽ��ˆT�Ļ،�߉݋
							NonPartTimeBVO bvo = updateNotPartTimePsn(pk_group, pk_org, pk_wa_class, pk_wa_period,
									curInsAmount, pk_psndoc, payDate, annaAmount, curAmount, itemKey, pk_hrorg);
							// tank Ч���Ż� start 2019��12��15��14:04:25
							if (bvo != null) {
								List<SuperVO> tempList = writeBackListMap.get(NonPartTimeBVO.class);
								if (tempList == null) {
									tempList = new ArrayList<>();
								}
								tempList.add(bvo);
								writeBackListMap.put(NonPartTimeBVO.class, tempList);
							}
							// tank Ч���Ż� end
						}
					}
				}
				// List<PsndocDefVO> laborInfo4PartTimeInfoVOs = new
				// ArrayList<PsndocDefVO>();
				// ��ְ��Ա�ļ����߼�
				if (null != checkPartTimePsn.get(PART_TIME_PSN_MAP_KEY)) {
					Map<String, UFDouble> curAmountMap = getCurrentPeriodAmount4PartTimePsn(pk_group, pk_org,
							pk_wa_class, pk_wa_period,
							checkPartTimePsn.get(PART_TIME_PSN_MAP_KEY).toArray(new String[0]));
					for (String pk_psndoc : checkPartTimePsn.get(PART_TIME_PSN_MAP_KEY)) {

						if (null == pk_psndoc) {
							continue;
						}
						// �Ҽ��ˆTÿ���˱�����Ҫ�۶���������н�ʽ��
						UFDouble curAmount = curAmountMap.get(pk_psndoc);
						curAmount = curAmount == null ? UFDouble.ZERO_DBL : curAmount;
						// �ų�����Ҫ�۶�����������Ա
						if (needUpdateExNHI4PartTimePsn(pk_group, pk_org, pk_wa_period, pk_psndoc, curAmount)) {
							// updatedPsn.add(pk_psndoc);
							// ���ڲ��䱣�ѽ��
							UFDouble curInsAmount = getCurrentPeriodInsAmount4PartTimePsn(pk_group, pk_org,
									pk_wa_class, pk_wa_period, pk_psndoc, curAmount);
							// ���ˆT�Ļ،�߉݋
							PartTimeBVO bvo = updatePartTimePsn(pk_group, pk_org, pk_wa_class, pk_wa_period,
									curInsAmount, curAmount, pk_psndoc, payDate, itemKey, pk_hrorg);
							// tank Ч���Ż� start 2019��12��15��14:04:25
							if (bvo != null) {
								List<SuperVO> tempList = writeBackListMap.get(PartTimeBVO.class);
								if (tempList == null) {
									tempList = new ArrayList<>();
								}
								tempList.add(bvo);
								writeBackListMap.put(PartTimeBVO.class, tempList);
							}
							// tank Ч���Ż� end
						}
					}
				}
			} else if (DECLAREFORM_90A == waclassType || DECLAREFORM_90B == waclassType) {
				// 90 A/Bн��
				Map<String, UFDouble> curAmountMap = getCurrentPeriodAmount4PartTimePsn(pk_group, pk_org, pk_wa_class,
						pk_wa_period, pk_psndocs);
				for (String pk_psndoc : pk_psndocs) {
					if (null == pk_psndoc) {
						continue;
					}
					// �Ҽ��ˆT��90A/90Bÿ���˱�����Ҫ�۶���������н�ʽ��
					UFDouble curAmount = curAmountMap.get(pk_psndoc);
					curAmount = curAmount == null ? UFDouble.ZERO_DBL : curAmount;

					// �ų�����Ҫ�۶�����������Ա
					if (needUpdateExNHI4PartTimePsn(pk_group, pk_org, pk_wa_period, pk_psndoc, curAmount)) {
						// updatedPsn.add(pk_psndoc);
						// ���ڲ��䱣�ѽ��
						UFDouble curInsAmount = getCurrentPeriodInsAmountFor90AB(pk_group, pk_org, pk_wa_class,
								pk_wa_period, pk_psndoc, curAmount, waclassType);
						// 90�ˆT�Ļ،�߉݋
						BusinessBVO bvo = update90ABPsn(pk_group, pk_org, pk_wa_class, pk_wa_period, curInsAmount,
								curAmount, pk_psndoc, payDate, itemKey, pk_hrorg);
						// tank Ч���Ż� start 2019��12��15��14:04:25
						if (bvo != null) {
							List<SuperVO> tempList = writeBackListMap.get(BusinessBVO.class);
							if (tempList == null) {
								tempList = new ArrayList<>();
							}
							tempList.add(bvo);
							writeBackListMap.put(BusinessBVO.class, tempList);
						}
						// tank Ч���Ż� end
					}
				}

			}

			// ��д������������
			UFDateTime writeBackStTime = new UFDateTime();
			Logger.error("-------�����������������д�߼���ʼ,ʱ��:" + writeBackStTime.toStdString() + "-------");
			writeBackDeclaration(pk_group, pk_org, writeBackListMap);
			UFDateTime writeBackEdTime = new UFDateTime();
			Logger.error("-------�����������������д�߼�����,ʱ��:" + writeBackEdTime.toStdString() + "-------");
			Logger.error("-------�����������������д�߼�����,��ʱ:" + (writeBackEdTime.getMillis() - writeBackStTime.getMillis())
					/ 1000.0 + "s-------");
			// ��д�����־
			updateHeathCaculate(pk_org, pk_wa_class, pk_wa_period, payDate);
			// tank log
			UFDateTime endTime = new UFDateTime();

			Logger.error("-------���������������,ʱ��:" + endTime.toStdString() + "-------");
			Logger.error("-------���������������,��ʱ:" + (endTime.getMillis() - startTime.getMillis()) / 1000.0 + "s-------");
		}

	}

	private boolean isFerry(String pk_wa_class, String pk_org) throws DAOException {
		String sql = "select isnull(ISFERRY,'N') from WA_WACLASS " + " where PK_ORG = '" + pk_org + "' and dr = 0 "
				+ " and PK_WA_CLASS = '" + pk_wa_class + "'";
		String ifferry = (String) dao.executeQuery(sql, new ColumnProcessor());
		if (ifferry != null && ifferry.equals("Y")) {
			return true;
		}
		return false;
	}

	private String getItemKey(String pk_wa_class, String pk_org, String pk_wa_period) throws BusinessException {
		// ��ѯн�Y�Ŀ���a�䱣�M�Ŀ(TWEX0000�涨��)
		String strSQL = "select itemkey from wa_classitem where pk_org='" + pk_org + "' and pk_wa_class='"
				+ pk_wa_class + "' and cyear=((select cyear from wa_period where pk_wa_period='" + pk_wa_period
				+ "')) and cperiod=((select cperiod from wa_period where pk_wa_period='" + pk_wa_period
				+ "')) and pk_wa_item = (select refvalue from twhr_basedoc where pk_org='" + pk_org
				+ "' and code = 'TWEX0000' and dr=0) ";

		String itemkey = (String) dao.executeQuery(strSQL, new ColumnProcessor());
		if (StringUtils.isEmpty(itemkey)) {
			throw new BusinessException("Ӌ��ʧ��!δ�ҵ��a�䱣�M�ĿTWEX0000.");
		}
		return itemkey;
	}

	/**
	 * ��д������������
	 * 
	 * @author tank
	 * @param writeBackListMap
	 * @throws BusinessException
	 */
	private void writeBackDeclaration(String pk_group, String pk_org,
			Map<Class<? extends ISuperVO>, List<SuperVO>> writeBackListMap) throws BusinessException {
		if (writeBackListMap != null && writeBackListMap.size() > 0) {
			ITwhr_declarationMaintain service = NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class);
			Set<Class<? extends ISuperVO>> keySet = writeBackListMap.keySet();
			for (Class<? extends ISuperVO> clazz : keySet) {
				List<SuperVO> datas = writeBackListMap.get(clazz);
				if (datas != null && datas.size() > 0) {
					AggDeclarationVO avo = DeclarationUtils.getAggDeclarationVO();
					avo.getParentVO().setPk_group(pk_group);
					avo.getParentVO().setPk_org(pk_org);

					avo.setChildren(clazz, datas.toArray(new SuperVO[0]));
					service.writeBack4HealthCaculate(avo);
				}
			}
		}

	}

	// �������������־��д-���
	private void updateHeathCaculate(String pk_org, String pk_wa_class, String pk_wa_period, UFDate payDate)
			throws DAOException {
		UUID uuid = UUID.randomUUID();
		String sqlStr = "insert into twhr_healthcaculateflag (pk_healthcaculateflag,pk_org,pk_wa_class,pk_wa_period,pay_date) "
				+ " values( '"
				+ uuid.toString().replace("-", "").substring(0, 19)
				+ "','"
				+ pk_org
				+ "','"
				+ pk_wa_class + "','" + pk_wa_period + "','" + payDate + "') ";
		dao.executeUpdate(sqlStr);
	}

	// �������������־��д-ȡ��
	private void updateHealthFlag(String pk_org, String pk_wa_class, String pk_wa_period) throws DAOException {
		String sqlStr = " delete from twhr_healthcaculateflag " + " where pk_org = '" + pk_org
				+ "' and pk_wa_class = '" + pk_wa_class + "' and pk_wa_period = '" + pk_wa_period + "' ";
		dao.executeUpdate(sqlStr);
	}

	/**
	 * �ж�н�ʷ����Ƿ�Ϊ50н��
	 * 
	 * @param pk_wa_class
	 * @return 50н��:1 90AB:2 ����:0
	 * @throws BusinessException
	 * @date 2018��9��22�� ����12:35:51
	 * @description
	 */
	@SuppressWarnings("rawtypes")
	private int checkWaClass(String pk_wa_class) throws BusinessException {
		String dbCode = null;
		String sqlStr = "select code from bd_defdoc " + " where bd_defdoc.pk_defdoc= ("
				+ " select declareform from WA_WACLASS where pk_wa_class='" + pk_wa_class + "' )";
		// �ж�н�ʷ�������
		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		List list = (List) iUAPQueryBS.executeQuery(sqlStr, new ColumnListProcessor());
		if (null != list && list.size() > 0) {
			for (Object obj : list) {
				if (null != obj) {
					dbCode = obj.toString();
					if (dbCode.equals("50")) {
						return DECLAREFORM_50;
					} else if (dbCode.equals("9A")) {
						return DECLAREFORM_90A;
					} else if (dbCode.equals("9B")) {
						return DECLAREFORM_90B;
					}
				}
			}
		}

		return DECLAREFORM_OTHER;
	}

	/**
	 * ����90A/Bн�ʷ�����Ա�Ĳ��䱣��
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @param pk_wa_class
	 * @param pk_wa_period
	 * @param pk_psndoc
	 * @param curAmount
	 *            ���ڹ�ѡ��ְ��Աн����Ŀ�ۼӽ��
	 * @parm declareform �걨��ʽ:50н��:1 90A:2 90B:3 ����:0
	 * @return
	 * @throws BusinessException
	 * @date 2018��9��22�� ����12:23:58
	 * @description
	 */
	private UFDouble getCurrentPeriodInsAmountFor90AB(String pk_group, String pk_org, String pk_wa_class,
			String pk_wa_period, String pk_psndoc, UFDouble curAmount, int declareform) throws BusinessException {
		UFDouble rtn = UFDouble.ZERO_DBL;
		// ��ȡ����
		String initCode = null;
		if (DECLAREFORM_90A == declareform) {
			initCode = "TWEP0004";
		}
		if (DECLAREFORM_90B == declareform) {
			initCode = "TWEP0005";
		}
		UFDouble grand = SysInitQuery4TWHR.getParaDbl(pk_org, initCode);
		if (null == grand) {
			throw new BusinessException("����[TWEP0004]��[TWEP0005]δ�ҵ�,Ո�z�酢���O��!");
		}
		if (curAmount.div(grand).doubleValue() > 0.000001) {
			UFDouble heathRate = SysInitQuery4TWHR.getParaDbl(pk_org, "TWEP0001");
			if (null == heathRate) {
				throw new BusinessException("�o���ҵ��a�䱣�U�M��(����)�O�ã�Ո�z���Զ��x�(TWEP0001)���O�����ݡ�");
			}
			rtn = curAmount.multiply(heathRate).div(100);

		}
		return dealRtn(rtn);
	}

	/**
	 * Ӌ���ְ��Ա�����a�䱣�M���~
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @param pk_wa_class
	 * @param pk_wa_period
	 * @param pk_psndoc
	 * @param curAmount
	 *            ���ڼ��ˆT��Ҫ�۳��Ķ�������н�Y헵ąR��
	 * @return
	 * @throws BusinessException
	 * @date 2018��9��21�� ����2:50:52
	 * @description
	 */
	private UFDouble getCurrentPeriodInsAmount4PartTimePsn(String pk_group, String pk_org, String pk_wa_class,
			String pk_wa_period, String pk_psndoc, UFDouble curAmount) throws BusinessException {
		UFDouble rtn = UFDouble.ZERO_DBL;
		UFDouble baseSalary = SysInitQuery4TWHR.getParaDbl(pk_org, "TWSP0009");
		if (null == baseSalary) {
			throw new BusinessException("�o���ҵ��������Y(��)�O�ã�Ո�z���Զ��x�(TWSP0009)���O�����ݡ�");
		}
		UFDouble heathRate = SysInitQuery4TWHR.getParaDbl(pk_org, "TWEP0001");
		if (null == heathRate) {
			throw new BusinessException("�o���ҵ��a�䱣�U�M��(����)�O�ã�Ո�z���Զ��x�(TWEP0001)���O�����ݡ�");
		}
		if (curAmount.sub(baseSalary).doubleValue() > 0) {
			rtn = curAmount.multiply(heathRate).div(100);
		}
		return dealRtn(rtn);
	}

	/**
	 * �Ǽ��ˆT�Ļ،�߉݋
	 * 
	 * @param pk_org
	 * @param pk_wa_class
	 * @param pk_wa_period
	 * @param curInsAmount
	 *            ���䱣��
	 * 
	 * @throws BusinessException
	 * @date 2018��9��21�� ����12:12:35
	 * @description �،���н�Y�Ŀ�ϺͶ�����������c
	 */
	private NonPartTimeBVO updateNotPartTimePsn(String pk_group, String pk_org, String pk_wa_class,
			String pk_wa_period, UFDouble curInsAmount, String pk_psndoc, UFDate payDate, UFDouble annaAmount,
			UFDouble curAmount, String itemkey, String pk_hrorg) throws BusinessException {
		// List<Map> curInsAmountPSN = new ArrayList<Map>();

		// ���Ƿ����������Ӌ�Ŀ���ϲ�����Ġ�r�،�
		// mod ����Ҳ�،�
		if (null != curInsAmount) {

			// mod Ares.Tank ���ݼ���
			String strSQL = "update wa_data set " + itemkey + " = "
					+ SalaryEncryptionUtil.encryption(curInsAmount.doubleValue()) + " where pk_org='" + pk_org
					+ "' AND pk_wa_class='" + pk_wa_class + "' " + " AND cyear=(( "
					+ " SELECT cyear FROM wa_period WHERE pk_wa_period='" + pk_wa_period + "'))" + " AND cperiod=(( "
					+ " SELECT cperiod FROM wa_period WHERE pk_wa_period='" + pk_wa_period + "'))"
					+ " AND pk_psndoc = '" + pk_psndoc + "'";
			dao.executeUpdate(strSQL);
			// ��дǰ,�Ѹ���Ա��,����֯��,ȫ��н�ʷ�����,���ڼ������ۼƸ���Ϊ���µ�
			strSQL = "update declaration_nonparttime set totalbonusforyear = " + annaAmount + " where vbdef1 = '"
					+ pk_org + "' and vbdef3 = '" + pk_wa_period + "' and vbdef4 = '" + pk_psndoc + "' ";
			dao.executeUpdate(strSQL);
			// ITwhr_declarationMaintain service =
			// NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class);
			// ��д����������퓺� '�Ǽ�퓻`'
			// AggDeclarationVO avo = DeclarationUtils.getAggDeclarationVO();
			// avo.getParentVO().setPk_group(pk_group);
			// avo.getParentVO().setPk_org(pk_org);

			// SuperVO[] svos = new SuperVO[1];
			// svos[0] = DeclarationUtils.getNonPartTimeBVO();
			NonPartTimeBVO newVO = DeclarationUtils.getNonPartTimeBVO();
			// ��������:
			newVO.setPay_date(payDate);

			Map<String, Object> baseInfo = getBaseInfo(pk_psndoc, pk_org, payDate, pk_hrorg, pk_wa_class, pk_wa_period);

			// ����������
			newVO.setBeneficiary_name(null != baseInfo.get("beneficiary_name") ? baseInfo.get("beneficiary_name")
					.toString() : null);
			// ���������֤��
			newVO.setBeneficiary_id(null != baseInfo.get("beneficiary_id") ? baseInfo.get("beneficiary_id").toString()
					: null);
			// Ͷ����λ����
			newVO.setInsurance_unit_code(null != baseInfo.get("insurance_unit_code") ? baseInfo.get(
					"insurance_unit_code").toString() : null);
			// ����
			newVO.setPk_dept(null != baseInfo.get("pk_dept") ? baseInfo.get("pk_dept").toString() : null);

			// ���θ������
			newVO.setSingle_pay(curAmount);
			// ���ο۽ɲ��䱣�շѽ��
			newVO.setSingle_withholding(curInsAmount);
			// �۷ѵ���Ͷ�����(����)
			newVO.setDeductions_month_insure(getPsndocGrande(pk_psndoc, payDate));
			// ͬ����ۼƽ�����
			newVO.setTotalbonusforyear(annaAmount);
			// д��н�ʷ�����н���ڼ䡢������֯ ����ȡ������ı�ʶ
			newVO.setVbdef1(pk_org);
			newVO.setVbdef2(pk_wa_class);
			newVO.setVbdef3(pk_wa_period);
			newVO.setVbdef4(pk_psndoc);

			// Ч���Ż� tank 2019��12��15��14:00:10
			// svos[0] = newVO;
			// avo.setChildren(NonPartTimeBVO.class, svos);
			// service.writeBack4HealthCaculate(avo);
			return newVO;

		}
		return null;

	}

	/**
	 * 90A/B н�ʷ����Ļ�д
	 * 
	 * @param pk_org
	 * @param pk_wa_class
	 * @param pk_wa_period
	 * @param curInsAmount
	 * @return
	 * @throws BusinessException
	 * @date 2018��9��22�� ����12:31:58
	 * @description
	 */
	private BusinessBVO update90ABPsn(String pk_group, String pk_org, String pk_wa_class, String pk_wa_period,
			UFDouble curInsAmount, UFDouble curAmount, String pk_psndoc, UFDate payDate, String itemkey, String pk_hrorg)
			throws BusinessException {

		// ���Ƿ����������Ӌ�Ŀ���ϲ�����Ġ�r�،�
		// С����ҲҪ��д
		if (null != curInsAmount) {
			String strSQL = "update wa_data set " + itemkey + " = "
					+ SalaryEncryptionUtil.encryption(curInsAmount.doubleValue()) + " where pk_org='" + pk_org
					+ "' AND pk_wa_class='" + pk_wa_class + "' " + " AND cyear=(( "
					+ " SELECT cyear FROM wa_period WHERE pk_wa_period='" + pk_wa_period + "'))" + " AND cperiod=(( "
					+ " SELECT cperiod FROM wa_period WHERE pk_wa_period='" + pk_wa_period + "'))"
					+ " AND pk_psndoc = '" + pk_psndoc + "'";
			dao.executeUpdate(strSQL);

			// ��д����������퓺�'���ИI������'
			// ITwhr_declarationMaintain service =
			// NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class);

			// AggDeclarationVO avo = DeclarationUtils.getAggDeclarationVO();
			// avo.getParentVO().setPk_group(pk_group);
			// avo.getParentVO().setPk_org(pk_org);

			// SuperVO[] svos = new SuperVO[1];
			// svos[0] = DeclarationUtils.getNonPartTimeBVO();
			BusinessBVO newVO = DeclarationUtils.getBusinessBVO();
			// ��������:
			newVO.setPay_date(payDate);

			Map<String, Object> baseInfo = getBaseInfo(pk_psndoc, pk_org, payDate, pk_hrorg, pk_wa_class, pk_wa_period);

			// ����������
			newVO.setBeneficiary_name(baseInfo.get("beneficiary_name") == null ? null : baseInfo
					.get("beneficiary_name").toString());
			// ���������֤��
			newVO.setBeneficiary_id(baseInfo.get("beneficiary_id") == null ? null : baseInfo.get("beneficiary_id")
					.toString());
			// ����
			newVO.setPk_dept(baseInfo.get("pk_dept") == null ? null : baseInfo.get("pk_dept").toString());
			// Ͷ������
			newVO.setInsurance_unit_code(baseInfo.get("insurance_unit_code") == null ? null : baseInfo.get(
					"insurance_unit_code").toString());

			// ���θ������
			newVO.setSingle_pay(curAmount);
			// ���ο۽ɲ��䱣�շѽ��
			newVO.setSingle_withholding(curInsAmount);

			// д��н�ʷ�����н���ڼ䡢������֯ ����ȡ������ı�ʶ
			newVO.setVbdef1(pk_org);
			newVO.setVbdef2(pk_wa_class);
			newVO.setVbdef3(pk_wa_period);
			newVO.setVbdef4(pk_psndoc);

			// Ч���Ż� tank 2019��12��15��14:00:10
			// svos[0] = newVO;
			// avo.setChildren(BusinessBVO.class, svos);
			// service.writeBack4HealthCaculate(avo);
			return newVO;
			/*
			 * PsndocDefVO newVO = PsndocDefUtil.getPsnNHIExtendVO();
			 * newVO.setPk_psndoc(pk_psndoc); newVO.setDr(0);
			 * newVO.setCreator("NC_USER0000000000000"); if (pvo != null) {
			 * newVO.setBegindate(pvo.getCstartdate());
			 * newVO.setEnddate(pvo.getCenddate()); } // glbdef1,н�Y���g
			 * newVO.setAttributeValue("glbdef1", pk_wa_period); // glbdef2,н�Y����
			 * newVO.setAttributeValue("glbdef2", pk_wa_class); // glbdef3,���ڽ��~
			 * newVO.setAttributeValue("glbdef3", curAmount); // glbdef4,�l������
			 * newVO.setAttributeValue("glbdef4", payDate); // glbdef5,���ꪄ���nӋ
			 * newVO.setAttributeValue("glbdef5", annaAmount); // glbdef6,�����a�䱣�M
			 * newVO.setAttributeValue("glbdef6", curInsAmount); // recordnum
			 * newVO.setAttributeValue("recordnum", -1);
			 * inserted_psndocs.add(pk_psndoc); psnLaborInfoVOs.add(newVO);
			 */

		}
		return null;
	}

	/**
	 * ���ˆT�Ļ،�߉݋
	 * 
	 * @param pk_org
	 * @param pk_wa_class
	 * @param pk_wa_period
	 * @param curInsAmount
	 *            ���ڵ�н�Y헵ąR�����~
	 * @return
	 * @throws BusinessException
	 * @date 2018��9��21�� ����3:36:48
	 * @description
	 */
	private PartTimeBVO updatePartTimePsn(String pk_group, String pk_org, String pk_wa_class, String pk_wa_period,
			UFDouble curInsAmount, UFDouble curAmount, String pk_psndoc, UFDate payDate, String itemkey, String pk_hrorg)
			throws BusinessException {

		// ���Ƿ����������Ӌ�Ŀ���ϲ�����Ġ�r�،�
		// mod ��0Ҳ���،� 2018��10��26��13:24:09
		if (null != curInsAmount) {
			String strSQL = "update wa_data set " + itemkey + " = "
					+ SalaryEncryptionUtil.encryption(curInsAmount.doubleValue()) + " where pk_org='" + pk_org
					+ "' AND pk_wa_class='" + pk_wa_class + "' " + " AND cyear=(( "
					+ " SELECT cyear FROM wa_period WHERE pk_wa_period='" + pk_wa_period + "'))" + " AND cperiod=(( "
					+ " SELECT cperiod FROM wa_period WHERE pk_wa_period='" + pk_wa_period + "'))"
					+ " AND pk_psndoc = '" + pk_psndoc + "'";
			dao.executeUpdate(strSQL);

			// ITwhr_declarationMaintain service =
			// NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class);

			// AggDeclarationVO avo = DeclarationUtils.getAggDeclarationVO();
			// avo.getParentVO().setPk_group(pk_group);
			// avo.getParentVO().setPk_org(pk_org);

			// SuperVO[] svos = new SuperVO[1];
			// svos[0] = DeclarationUtils.getNonPartTimeBVO();
			PartTimeBVO newVO = DeclarationUtils.getPartTimeBVO();
			// ��������:
			newVO.setPay_date(payDate);

			Map<String, Object> baseInfo = getBaseInfo(pk_psndoc, pk_org, payDate, pk_hrorg, pk_wa_class, pk_wa_period);

			// ����������
			newVO.setBeneficiary_name(baseInfo.get("beneficiary_name") == null ? null : baseInfo
					.get("beneficiary_name").toString());
			// ���������֤��
			newVO.setBeneficiary_id(baseInfo.get("beneficiary_id") == null ? null : baseInfo.get("beneficiary_id")
					.toString());
			// ����
			newVO.setPk_dept(baseInfo.get("pk_dept") == null ? null : baseInfo.get("pk_dept").toString());
			// Ͷ������
			newVO.setInsurance_unit_code(baseInfo.get("insurance_unit_code") == null ? null : baseInfo.get(
					"insurance_unit_code").toString());

			// ���θ������
			newVO.setSingle_pay(curAmount);
			// ���ο۽ɲ��䱣�շѽ��
			newVO.setSingle_withholding(curInsAmount);

			// д��н�ʷ�����н���ڼ䡢������֯ ����ȡ������ı�ʶ
			newVO.setVbdef1(pk_org);
			newVO.setVbdef2(pk_wa_class);
			newVO.setVbdef3(pk_wa_period);
			newVO.setVbdef4(pk_psndoc);

			// Ч���Ż� tank 2019��12��15��14:00:10
			// svos[0] = newVO;
			// avo.setChildren(PartTimeBVO.class, svos);
			// service.writeBack4HealthCaculate(avo);
			return newVO;
			/*
			 * PsndocDefVO newVO = PsndocDefUtil.getPsnNHIExtendVO();
			 * newVO.setPk_psndoc(pk_psndoc); newVO.setDr(0);
			 * newVO.setCreator("NC_USER0000000000000"); if (pvo != null) {
			 * newVO.setBegindate(pvo.getCstartdate());
			 * newVO.setEnddate(pvo.getCenddate()); } // glbdef1,н�Y���g
			 * newVO.setAttributeValue("glbdef1", pk_wa_period); // glbdef2,н�Y����
			 * newVO.setAttributeValue("glbdef2", pk_wa_class); // glbdef3,���ڽ��~
			 * newVO.setAttributeValue("glbdef3", curAmount); // glbdef4,�l������
			 * newVO.setAttributeValue("glbdef4", payDate); // glbdef5,���ꪄ���nӋ
			 * newVO.setAttributeValue("glbdef5", annaAmount); // glbdef6,�����a�䱣�M
			 * newVO.setAttributeValue("glbdef6", curInsAmount); // recordnum
			 * newVO.setAttributeValue("recordnum", -1);
			 * inserted_psndocs.add(pk_psndoc); psnLaborInfoVOs.add(newVO);
			 */

		}
		return null;
	}

	/**
	 * ���ּ�ְ�ͷǼ�ְ��Ա
	 * 
	 * @param pk_psndocs
	 * @return Map<psnType,Set<pk_psndoc>>
	 * @param payDate
	 * @throws BusinessException
	 * @date 2018��9��21�� ����9:56:57
	 * @description
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Set<String>> checkPartTimePsn(String[] pk_psndocs, UFDate payDate, String pk_wa_period)
			throws BusinessException {
		Set<String> allPsndoc = new HashSet<>();// ������ȥ��
		allPsndoc.addAll(Arrays.asList(pk_psndocs));

		Map<String, Set<String>> resultSetMap = new HashMap<>();
		resultSetMap.put(PART_TIME_PSN_MAP_KEY, new HashSet<String>());
		resultSetMap.put(NOT_PART_TIME_PSN_MAP_KEY, new HashSet<String>());

		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = insql.getInSQL(pk_psndocs);

		// ����Ͷ������ 2019��12��߀�ڱ� ����Ӌ����a�䱣�M by George 20200212 ȱ��Bug #33125
		// ͬ����Ƿ���Ͷ��������ȡԱ�����µĽ�����¼����Ϣ
		String sqlStr = " select pk_psndoc, max(ISNULL(enddate,'9999-12-31')) enddate " + " from "
				+ PsndocDefTableUtil.getPsnHealthTablename() + " g2 " + " where g2.pk_psndoc in  (" + psndocsInSQL
				+ ") " + " and g2.begindate <= '" + Integer.toString(payDate.getYear()) + "-12-31' "
				+ " and isnull(g2.enddate,'9999-12-31') >= '" + Integer.toString(payDate.getYear()) + "-01-01' "
				+ " and g2.glbdef2 = '����' and g2.dr = 0 group by pk_psndoc ";
		List<Map> mapList = (List<Map>) dao.executeQuery(sqlStr, new MapListProcessor());
		for (Map map : mapList) {
			if (null != map && null != map.get("pk_psndoc")) {
				resultSetMap.get(NOT_PART_TIME_PSN_MAP_KEY).add((String) map.get("pk_psndoc"));
				allPsndoc.remove(map.get("pk_psndoc"));
			}
		}

		// ����Ϊ��ְ��Ա
		resultSetMap.put(PART_TIME_PSN_MAP_KEY, allPsndoc);
		return resultSetMap;
	}

	/**
	 * ����Ҫ�۶�����������Ա
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @param pk_wa_period
	 * @param pk_psndoc
	 * @param curAmount
	 * @return
	 * @throws BusinessException
	 */
	private boolean needUpdateExNHI(String pk_group, String pk_org, String pk_wa_period, String pk_psndoc,
			UFDouble curAmount) throws BusinessException {
		boolean needs = true;
		String strSQL = "";
		// 1. ���x������a�䱣�M��
		String fieldname = PsndocDefTableUtil.getPsnNoPayExtendNHIFieldname(pk_group, pk_org);
		strSQL = "select " + fieldname + " from bd_psndoc where pk_psndoc='" + pk_psndoc + "'";
		String value = (String) dao.executeQuery(strSQL, new ColumnProcessor());
		if (!StringUtils.isEmpty(value) && (new UFBoolean(value)).booleanValue()) {
			needs = false;
		}

		// 2. �l��֮н�Y���]�н���Ͷ�����ָࣨн�YӋ���н�Y���g��
		strSQL = "select isnull(def.glbdef16, 0) healgrade  from " + PsndocDefTableUtil.getPsnHealthTablename()
				+ " def" + " inner join bd_psndoc psn on def.pk_psndoc = psn.pk_psndoc" + " where def.pk_psndoc = '"
				+ pk_psndoc + "' and def.glbdef2 = '����' "
				+ " and def.begindate<=(select cenddate from wa_period where pk_wa_period='" + pk_wa_period + "')"
				+ " and isnull(enddate, '9999-12-31')>=(select cstartdate from wa_period where pk_wa_period='"
				+ pk_wa_period + "')" + " and def.glbdef14='Y' and def.dr=0";
		Object rtn = dao.executeQuery(strSQL, new ColumnProcessor());
		if (rtn == null || StringUtils.isEmpty(rtn.toString()) || rtn.toString().equals("0E-8")
				|| (Double.valueOf(rtn.toString())) == 0) {
			needs = false;
		}

		// 3. ����������Ӌ�Ŀ��0�������l�ŵ�н�Y헟o���������Ŀ
		if (curAmount.equals(UFDouble.ZERO_DBL)) {
			needs = false;
		}

		return needs;
	}

	/**
	 * ����Ҫ�۶�����������Ա(��ְ��Ա��)
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @param pk_wa_period
	 * @param pk_psndoc
	 * @param curAmount
	 * @return
	 * @throws BusinessException
	 */
	private boolean needUpdateExNHI4PartTimePsn(String pk_group, String pk_org, String pk_wa_period, String pk_psndoc,
			UFDouble curAmount) throws BusinessException {
		boolean needs = true;
		String strSQL = "";
		// 1. ���x������a�䱣�M��
		String fieldname = PsndocDefTableUtil.getPsnNoPayExtendNHIFieldname(pk_group, pk_org);
		strSQL = "select " + fieldname + " from bd_psndoc where pk_psndoc='" + pk_psndoc + "'";
		String value = (String) dao.executeQuery(strSQL, new ColumnProcessor());
		if (!StringUtils.isEmpty(value) && (new UFBoolean(value)).booleanValue()) {
			needs = false;
		}

		// 3. ����������Ӌ�Ŀ��0�������l�ŵ�н�Y헟o���������Ŀ
		if (curAmount.equals(UFDouble.ZERO_DBL)) {
			needs = false;
		}

		return needs;
	}

	/**
	 * �õ�����һ�ڵ�ȫ���ۼ�
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @param pk_wa_class
	 * @param payDate
	 * @param infosetCode
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	private UFDouble getLastPeriodAnnualSum(String pk_group, String pk_org, String pk_wa_class, UFDate payDate,
			String pk_psndoc) throws BusinessException {
		UFDouble rtn = UFDouble.ZERO_DBL;
		// glbdef5 ��һ�ڵĵ����ۼ�,ͬһ���µĕ��ж���н�Y����,ȡ���µ��ǂ�
		// (��ʵȫ����һ��,��д��ʱ����ȫ��н�ʷ���������ۼƶ���Ϊ���µ�)
		String strSQL = " SELECT nonpt.totalbonusforyear FROM declaration_nonparttime nonpt "
				+ " left join wa_period wap on wap.pk_wa_period = nonpt.vbdef3 " + " WHERE wap.cyear = '"
				+ String.valueOf(payDate.getYear()) + "' and wap.cperiod = ( "
				+ " select max(wap.cperiod) from declaration_nonparttime subnonpt "
				+ " left join wa_period wap on wap.pk_wa_period = subnonpt.vbdef3 " + " where wap.cyear = '"
				+ String.valueOf(payDate.getYear()) + "' and subnonpt.dr = 0 AND vbdef4 = '" + pk_psndoc
				+ "' and vbdef1 = '" + pk_org + "'" + ") " + " and vbdef1 = '" + pk_org + "'" + " and vbdef4 = '"
				+ pk_psndoc + "' order by nonpt.ts";
		String lastPeriodSumPSN = String.valueOf(dao.executeQuery(strSQL, new ColumnProcessor()));
		if (lastPeriodSumPSN != null && !lastPeriodSumPSN.equals("null")) {
			rtn = new UFDouble(lastPeriodSumPSN);
		}
		return rtn;
	}

	/**
	 * ����Ǽ�ְ��Ա���ڵĲ��䱣��
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @param pk_wa_class
	 * @param pk_wa_period
	 * @param pk_psndoc
	 * @param annaAmount
	 *            ���ϱ��ڵ�ȫ���ۼƽ��
	 * @param curAmount
	 *            ����н�Y�
	 * @return �a�䱣�M
	 * @throws BusinessException
	 * @description ��������Ӌ�Ľ��~���c����һ�P������Ϣ��4������������^��
	 *              �����^4��Ͷ�����~���t�����^��Ľ��~�c�����۷e�Ľ��~���^��ȡС��* TWEP0001�a�䱣�U�M��(����)���a�䱣�M
	 *              ��������4����Ͷ�����,�򲹳䱣��Ϊ0
	 */
	private UFDouble getCurrentPeriodInsAmount4NotPartTimePsn(String pk_group, String pk_org, String pk_wa_class,
			String pk_wa_period, String pk_psndoc, UFDouble annaAmount, UFDouble curAmount, UFDate payDate)
			throws BusinessException {
		UFDouble rtn = UFDouble.ZERO_DBL;

		UFDouble num = getPsndocGrande(pk_psndoc, payDate);
		if (null != num) {
			// ���ͬ�����Ӌ>=�ı�Ͷ�����~
			if (annaAmount.div(4).sub(num).doubleValue() > 0) {
				// ���^4��Ͷ�����~��߉݋
				// ���^�Ľ��~:
				UFDouble beyondNum = annaAmount.sub(num.multiply(4));

				// (ʹ��ͬ�����Ӌ-�ı�Ͷ�����~)>=�δνo��
				if (beyondNum.doubleValue() - curAmount.doubleValue() > 0.0) {
					rtn = curAmount;
				} else {
					rtn = beyondNum;
				}
				UFDouble heathRate = SysInitQuery4TWHR.getParaDbl(pk_org, "TWEP0001");
				if (null == heathRate) {
					throw new BusinessException("�o���ҵ��a�䱣�U�M��(����)�O�ã�Ո�z���Զ��x�(TWEP0001)���O�����ݡ�");
				}
				rtn = rtn.multiply(heathRate).div(100);

			} else {
				// �����^�t��0
				rtn = UFDouble.ZERO_DBL;
			}
		}
		// ��������
		return dealRtn(rtn);
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

	/**
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @param pk_wa_class
	 * @param pk_wa_period
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 * @date 2018��9��21�� ����11:34:42
	 * @description
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, UFDouble> getCurrentPeriodAmount4NotPartTimePsn(String pk_group, String pk_org,
			String pk_wa_class, String pk_wa_period, String[] pk_psndocs, UFDate payDate) throws BusinessException {
		Map<String, UFDouble> rtn = new HashMap<String, UFDouble>();
		List<Map> curAmountPSN = new ArrayList<Map>();
		List<String> noNhiPsns = new ArrayList<String>(Arrays.asList(pk_psndocs));

		String sqlStr = " select distinct pk_psndoc from " + PsndocDefTableUtil.getPsnHealthTablename() + " g2"
				+ " where g2.pk_psndoc in (" + new InSQLCreator().getInSQL(pk_psndocs) + ") "
				+ " and g2.begindate <= '" + payDate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).toString()
				+ "' and isnull(g2.enddate,'9999-12-31')>='"
				+ payDate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).toString()
				+ "'  and g2.glbdef2 = '����' and dr = 0";
		List psnList = (List) dao.executeQuery(sqlStr, new ColumnListProcessor());

		if (psnList != null && psnList.size() > 0) {
			// �ҳ����й�ѡ'���������ۼ���Ŀ'��н����
			String strSQL = "select cls.itemkey, wi.iproperty from wa_classitem cls inner join twhr_waitem_30 tw on cls.pk_wa_item = tw.pk_wa_item "
					+ " inner join wa_item wi on cls.pk_wa_item = wi.pk_wa_item "
					+ " where cls.pk_org='"
					+ pk_org
					+ "' and pk_wa_class='"
					+ pk_wa_class
					+ "' and cyear=((select cyear from wa_period where pk_wa_period='"
					+ pk_wa_period
					+ "')) and cperiod=((select cperiod from wa_period where pk_wa_period='"
					+ pk_wa_period
					+ "')) and  tw.ishealthinsexsum_30 = 'Y' ";

			List<Map> itemKeys = (List<Map>) dao.executeQuery(strSQL, new MapListProcessor());
			if (itemKeys != null && itemKeys.size() > 0) {
				strSQL = "";
				for (Map<String, Object> itemkey : itemKeys) {
					if (!StringUtils.isEmpty(strSQL)) {
						strSQL = strSQL + "+";
					}
					strSQL = strSQL + ((Integer) itemkey.get("iproperty") == 1 ? "(-1)*" : "")
							+ "isnull( SALARY_DECRYPT(" + (String) itemkey.get("itemkey") + "),0)";
				}

				// ����ȡ���Ϊ�����~
				strSQL = "select (" + strSQL + ") curamount, pk_psndoc from wa_data where pk_psndoc in ("
						+ new InSQLCreator().getInSQL((String[]) psnList.toArray(new String[0])) + ") and pk_org='"
						+ pk_org + "' and pk_wa_class='" + pk_wa_class
						+ "' and cyear=((select cyear from wa_period where pk_wa_period='" + pk_wa_period
						+ "')) and cperiod=((select cperiod from wa_period where pk_wa_period='" + pk_wa_period
						+ "')) ";
				curAmountPSN = (List<Map>) dao.executeQuery(strSQL, new MapListProcessor());
			}

			if (curAmountPSN != null && curAmountPSN.size() > 0) {
				for (Map curAmount : curAmountPSN) {
					rtn.put((String) curAmount.get("pk_psndoc"),
							new UFDouble(Double.parseDouble(curAmount.get("curamount").toString())));

					noNhiPsns.remove(curAmount.get("pk_psndoc"));
				}
			}
		}

		if (noNhiPsns.size() > 0) {
			Map<String, UFDouble> partTimeAmountMap = getCurrentPeriodAmount4PartTimePsn(pk_group, pk_org, pk_wa_class,
					pk_wa_period, noNhiPsns.toArray(new String[0]));
			rtn.putAll(partTimeAmountMap);
		}

		return rtn;
	}

	/**
	 * ���ڼ�ְ��Ա��90A/90B��������н����Ķ����������
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @param pk_wa_class
	 * @param pk_wa_period
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, UFDouble> getCurrentPeriodAmount4PartTimePsn(String pk_group, String pk_org,
			String pk_wa_class, String pk_wa_period, String[] pk_psndocs) throws BusinessException {
		Map<String, UFDouble> rtn = new HashMap<String, UFDouble>();
		List<Map> curAmountPSN = new ArrayList<Map>();
		String strSQL = "select cls.itemkey, wi.iproperty from wa_classitem cls inner join twhr_waitem_30 tw on cls.pk_wa_item = tw.pk_wa_item "
				+ " inner join wa_item wi on cls.pk_wa_item = wi.pk_wa_item "
				+ " where cls.pk_org='"
				+ pk_org
				+ "' and pk_wa_class='"
				+ pk_wa_class
				+ "' and cyear=((select cyear from wa_period where pk_wa_period='"
				+ pk_wa_period
				+ "')) and cperiod=((select cperiod from wa_period where pk_wa_period='"
				+ pk_wa_period
				+ "')) and  tw.ishealthinsparttime = 'Y' ";

		// ���@Щн�Y�Ŀ�M�ЅR��
		List<Map> itemKeys = (List<Map>) dao.executeQuery(strSQL, new MapListProcessor());
		if (itemKeys != null && itemKeys.size() > 0) {
			strSQL = "";
			for (Map<String, Object> itemkey : itemKeys) {
				if (!StringUtils.isEmpty(strSQL)) {
					strSQL = strSQL + "+";
				}
				strSQL = strSQL + ((Integer) itemkey.get("iproperty") == 1 ? "(-1)*" : "") + "isnull( SALARY_DECRYPT("
						+ (String) itemkey.get("itemkey") + "),0)";
			}

			strSQL = "select (" + strSQL + ") curamount, pk_psndoc from wa_data where pk_psndoc in ("
					+ new InSQLCreator().getInSQL(pk_psndocs) + ") and pk_org='" + pk_org + "' and pk_wa_class='"
					+ pk_wa_class + "' and cyear=((select cyear from wa_period where pk_wa_period='" + pk_wa_period
					+ "')) and cperiod=((select cperiod from wa_period where pk_wa_period='" + pk_wa_period + "')) ";
			curAmountPSN = (List<Map>) dao.executeQuery(strSQL, new MapListProcessor());
		}

		if (curAmountPSN != null && curAmountPSN.size() > 0) {
			for (Map curAmount : curAmountPSN) {
				rtn.put((String) curAmount.get("pk_psndoc"),
						new UFDouble(Double.parseDouble(curAmount.get("curamount").toString())));
			}
		}

		return rtn;
	}

	/**
	 * ȡָ���M����н�Y������н�Y���g���ˆT�б�
	 * 
	 * @param pk_org
	 *            �M��
	 * @param pk_wa_class
	 *            н�Y����
	 * @param yearperiod
	 *            н�Y���g
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String[] getWaDataPsndocs(String pk_org, String pk_wa_class, String pk_periodscheme, String pk_wa_period)
			throws BusinessException {
		String strSQL = "select cyear, cperiod from wa_period where pk_periodscheme = '" + pk_periodscheme
				+ "' and pk_wa_period='" + pk_wa_period + "'";
		Map periodRs = (Map) dao.executeQuery(strSQL, new MapProcessor());
		List<String> psndocs = null;

		if (periodRs != null && periodRs.size() > 0) {
			strSQL = "select distinct pk_psndoc from wa_data where pk_org='" + pk_org + "' and pk_wa_class='"
					+ pk_wa_class + "' and cyear='" + periodRs.get("cyear") + "' and cperiod='"
					+ periodRs.get("cperiod") + "'";

			psndocs = (List<String>) dao.executeQuery(strSQL, new ColumnListProcessor());
		}

		if (psndocs == null || psndocs.size() == 0) {
			return null;
		} else {
			return psndocs.toArray(new String[0]);
		}
	}

	@Override
	public void deleteExtendNHIInfo(String pk_group, String pk_org, String pk_wa_class, String pk_periodscheme,
			String pk_wa_period, UFDate payDate) throws BusinessException {
		// ȡ�ͽ������ò���
		// ������Ϊ��ʱ�������÷���
		// ssx added for 2nd health ins on 2017-07-22
		if (!SysInitQuery.getParaBoolean(pk_org, "TWHR01").booleanValue()) {
			return;
		}

		// ȡ���������Ӽ��O��
		String infosetCode = PsndocDefTableUtil.getPsnHealthInsExTablename(pk_group, pk_org);

		if (StringUtils.isEmpty(infosetCode)) {
			throw new BusinessException("�o���ҵ����������Ӽ��O�ã�Ո�z���Զ��x�(TWHR000)���O�����ݡ�");
		}

		// ȡ�ˆT�б�
		String[] pk_psndocs = getWaDataPsndocs(pk_org, pk_wa_class, pk_periodscheme, pk_wa_period);

		// ����Ƿ��и��µ����ϣ�����������ȡ������
		checkNewRecord(pk_wa_class, pk_wa_period, infosetCode, pk_psndocs);

		String strSQL = "";
		for (String pk_psndoc : pk_psndocs) {
			strSQL = "delete from " + infosetCode + " where glbdef1='" + pk_wa_period + "' and glbdef2='" + pk_wa_class
			// + "' and glbdef4='" + payDate.toString();
			// ͬһ�ڼ䣬ͬһ������ֻ�ܳ���һ�������Բ���Ҫ��������������
					+ "' and pk_psndoc='" + pk_psndoc + "'";
			dao.executeUpdate(strSQL);
		}

		for (String pk_psndoc : pk_psndocs) {
			strSQL = "update " + infosetCode + " set recordnum=recordnum-1 where pk_psndoc='" + pk_psndoc + "'";
			dao.executeUpdate(strSQL);
		}

		updateLastFlag(infosetCode, pk_psndocs);
	}

	@SuppressWarnings("rawtypes")
	private void checkNewRecord(String pk_wa_class, String pk_wa_period, String infosetCode, String[] pk_psndocs)
			throws DAOException, BusinessException {
		String strSQL = "";
		// ����Ƿ��и��µ����ϣ�����������ȡ������
		for (String pk_psndoc : pk_psndocs) {
			strSQL = "select * from " + infosetCode + " where pk_psndoc='" + pk_psndoc + "' and glbdef1='"
					+ pk_wa_period + "' and glbdef2='" + pk_wa_class + "' and lastflag='N'";
			Map rst = (Map) dao.executeQuery(strSQL, new MapProcessor());

			if (rst != null && rst.size() > 0) {
				throw new BusinessException("�˷����K�Ƕ��������Y������һ�P���o���M�г��N��");
			}
		}
	}

	private void updateLastFlag(String infosetCode, String[] pk_psndocs) throws BusinessException {
		String strSQL = "";
		for (String pk_psndoc : pk_psndocs) {
			strSQL = "update "
					+ infosetCode
					+ " set recordnum = (select rowno from (select pk_psndoc_sub, (select count(*) from "
					+ infosetCode
					+ " where pk_psndoc=def.pk_psndoc group by pk_psndoc)-(row_number() over (partition BY def.pk_psndoc ORDER BY def.begindate, def.enddate)) rowno from  "
					+ infosetCode + " def where def.pk_psndoc=" + infosetCode + ".pk_psndoc) TMP where pk_psndoc_sub="
					+ infosetCode + ".pk_psndoc_sub) where pk_psndoc = '" + pk_psndoc + "'";
			dao.executeUpdate(strSQL);

			strSQL = "update " + infosetCode + " set lastflag = case when recordnum = (select MIN(recordnum) from "
					+ infosetCode + " def where " + infosetCode
					+ ".pk_psndoc = def.pk_psndoc) then 'Y' else 'N' end where pk_psndoc = '" + pk_psndoc + "'";
			dao.executeUpdate(strSQL);
		}

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
	@SuppressWarnings("rawtypes")
	private UFDouble getPsndocGrande(String pk_psndoc, UFDate payDate) throws BusinessException {
		UFDouble num = UFDouble.ZERO_DBL;
		// �������ڲ�����Ա�Ľ�����¼
		// mod ���� 2018��10��26��15:09:37
		String sqlStr = " select isnull( SALARY_DECRYPT(g2.glbdef16),0) " + " from "
				+ PsndocDefTableUtil.getPsnHealthTablename() + " g2" + " where g2.pk_psndoc = '" + pk_psndoc + "' "
				+ " and g2.begindate <= '" + payDate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).toString()
				+ "' and isnull(g2.enddate,'9999-12-31')>='"
				+ payDate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).toString()
				+ "'  and g2.glbdef2 = '����' and dr = 0";
		List qDataList = (List) dao.executeQuery(sqlStr, new ColumnListProcessor());

		if (qDataList == null || qDataList.size() == 0) {
			String firstDayOfYear = payDate.toString().substring(0, 4) + "-01-01";
			sqlStr = " SELECT isnull( SALARY_DECRYPT(g2.glbdef16),0) FROM "
					+ PsndocDefTableUtil.getPsnHealthTablename() + " g2 " + "WHERE g2.pk_psndoc = '" + pk_psndoc
					+ "' AND g2.begindate <= '" + payDate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).toString()
					+ "' AND isnull(g2.enddate, '9999-12-31') >= '" + firstDayOfYear + "' AND recordnum = "
					+ " (SELECT MAX(recordnum) FROM " + PsndocDefTableUtil.getPsnHealthTablename()
					+ " WHERE pk_psndoc=g2.pk_psndoc AND begindate <= '"
					+ payDate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).toString()
					+ "' AND isnull(enddate, '9999-12-31')>='" + firstDayOfYear + "' AND glbdef2='����' AND dr=0) "
					+ "AND g2.glbdef2 = '����' AND dr = 0";
			qDataList = (List) dao.executeQuery(sqlStr, new ColumnListProcessor());
		}

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

	// ��ȡ����������Ϣ
	/**
	 * 
	 * @param psndoc
	 * @param pk_org
	 *            ������֯
	 * @param payDate
	 * @param pk_hrorg
	 *            ������Դ��֯
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Object> getBaseInfo(String psndoc, String pk_org, UFDate payDate, String pk_hrorg,
			String pk_wa_class, String pk_wa_period) throws BusinessException {
		// Map<String,Object> resultMap = new HashMap<>();
		String sqlStr = "SELECT "
				+ SQLHelper.getMultiLangNameColumn("psn.name")
				+ " as beneficiary_name, hi_psndoc_cert.id as beneficiary_id, "
				+ " doc.textvalue as insurance_unit_code ,job.pk_dept as pk_dept"
				+ "	FROM bd_psndoc psn "
				+ " inner join "
				+ PsndocDefTableUtil.getPsnHealthTablename()
				+ " heal on psn.pk_psndoc = heal.pk_psndoc and '"
				+ payDate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).toStdString()
				+ "' between begindate and isnull(enddate,'9999-12-31') and heal.glbdef2='����'"
				+ " left join hi_psndoc_cert on (hi_psndoc_cert.pk_psndoc = psn.pk_psndoc and hi_psndoc_cert.dr = 0 and hi_psndoc_cert.iseffect = 'Y')"
				+ " inner join bd_psnidtype on hi_psndoc_cert.idtype = bd_psnidtype.pk_identitype and "
				+ " 	( ( isnull(psn.glbdef7,'N')='Y' AND ( heal.glbdef5= ( SELECT  pk_defdoc FROM bd_defdoc WHERE code = 'HT01' "
				+ " AND pk_defdoclist =  ( SELECT pk_defdoclist  FROM bd_defdoclist WHERE  code = 'TWHR007')) AND bd_psnidtype.code='TW03')) or"
				+ " (heal.glbdef5=(select pk_defdoc from bd_defdoc where code = 'HT08' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'TWHR007')) and bd_psnidtype.code='TW03') or bd_psnidtype.code = 'TW01') "
				+ " left join twhr_basedoc doc on (doc.pk_org = '" + pk_org
				+ "' and doc.code = 'TWNHHI03' and doc.dr = 0) " + " left join hi_psnjob job on job.pk_psnjob in "
				+ " ( SELECT  wa_data.pk_psnjob " + " FROM wa_data " + " LEFT JOIN wa_period ON pk_wa_period = '"
				+ pk_wa_period + "' " + " WHERE " + "  wa_data.pk_wa_class = N'" + pk_wa_class + "' "
				+ " AND wa_data.cyear = wa_period.cyear " + " AND wa_data.cperiod = wa_period.cperiod "
				+ " AND stopflag = 'N' " + " AND wa_data.pk_psndoc = '" + psndoc + "') " + " WHERE psn.pk_psndoc = '"
				+ psndoc + "'" + " AND (psn.dr = 0 or psn.dr is null) AND (job.dr = 0 or job.dr is NULL) ";
		List<Map> sqlResultMapList = (List<Map>) dao.executeQuery(sqlStr, new MapListProcessor());

		if (sqlResultMapList == null || sqlResultMapList.size() == 0) {
			sqlStr = "SELECT "
					+ SQLHelper.getMultiLangNameColumn("psn.name")
					+ " as beneficiary_name, hi_psndoc_cert.id as beneficiary_id, "
					+ " doc.textvalue as insurance_unit_code ,job.pk_dept as pk_dept"
					+ "	FROM bd_psndoc psn "
					+ " left join "
					+ PsndocDefTableUtil.getPsnHealthTablename()
					+ " heal on psn.pk_psndoc = heal.pk_psndoc and isnull(enddate,'9999-12-31') >= '"
					+ payDate.toString().substring(0, 4)
					+ "-01-01"
					+ "' and begindate <= '"
					+ payDate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).toStdString()
					+ "' and heal.glbdef2='����'"
					+ " left join hi_psndoc_cert on (hi_psndoc_cert.pk_psndoc = psn.pk_psndoc and hi_psndoc_cert.dr = 0 and hi_psndoc_cert.iseffect = 'Y')"
					+ " inner join bd_psnidtype on hi_psndoc_cert.idtype = bd_psnidtype.pk_identitype and "
					+ " 	( ( isnull(psn.glbdef7,'N')='Y' AND ( heal.glbdef5 is null or heal.glbdef5= ( SELECT  pk_defdoc FROM bd_defdoc WHERE code = 'HT01' "
					+ " AND pk_defdoclist =  ( SELECT pk_defdoclist  FROM bd_defdoclist WHERE  code = 'TWHR007')) AND bd_psnidtype.code='TW03')) or"
					+ " (heal.glbdef5=(select pk_defdoc from bd_defdoc where code = 'HT08' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'TWHR007')) and bd_psnidtype.code='TW03') or bd_psnidtype.code = 'TW01') "
					+ " left join twhr_basedoc doc on (doc.pk_org = '" + pk_org
					+ "' and doc.code = 'TWNHHI03' and doc.dr = 0) " + " left join hi_psnjob job on job.pk_psnjob in "
					+ " ( SELECT  wa_data.pk_psnjob " + " FROM wa_data " + " LEFT JOIN wa_period ON pk_wa_period = '"
					+ pk_wa_period + "' " + " WHERE " + "  wa_data.pk_wa_class = N'" + pk_wa_class + "' "
					+ " AND wa_data.cyear = wa_period.cyear " + " AND wa_data.cperiod = wa_period.cperiod "
					+ " AND stopflag = 'N' " + " AND wa_data.pk_psndoc = '" + psndoc + "') "
					+ " WHERE psn.pk_psndoc = '" + psndoc + "'"
					+ " AND (psn.dr = 0 or psn.dr is null) AND (job.dr = 0 or job.dr is NULL) ";
			sqlResultMapList = (List<Map>) dao.executeQuery(sqlStr, new MapListProcessor());
		}

		if (null != sqlResultMapList) {

			for (Map sqlResultMap : sqlResultMapList) {
				if (null != sqlResultMap) {
					return sqlResultMap;
				}

			}

		}

		return new HashMap<>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nc.itf.twhr.ICalculateTWNHI#delExtendNHIInfo(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void delExtendNHIInfo(String pk_group, String pk_orgs, String pk_wa_class, String pk_wa_period)
			throws BusinessException {
		// ɾ���ӱ���������Ϣ
		// ����������Դ�µ����з�����֯
		Set<String> legalOrgs = LegalOrgUtilsEX.getOrgsByLegal(pk_orgs, pk_group);
		for (String pk_org : legalOrgs) {
			// ȡ��ʱ,�Ȱ����±����н�ʷ����󷢷ŵķ���������ۼƽ���ȥ���εĽ��
			// ��ѯ������Ա,���н�ʷ������н���ڼ�ĵĸ������
			String sqlStr = "select vbdef4,single_pay from declaration_nonparttime where  vbdef1 = '" + pk_org
					+ "' and vbdef2 = '" + pk_wa_class + "' and vbdef3 = '" + pk_wa_period + "'";
			Map<String, UFDouble> psndocMap = new HashMap<>();
			try {
				List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) dao.executeQuery(sqlStr,
						new MapListProcessor());
				if (null != resultMapList && resultMapList.size() > 0) {
					for (Map<String, Object> rowMap : resultMapList) {
						String key = null;
						UFDouble value = null;
						try {
							key = String.valueOf(rowMap.get("vbdef4"));
							value = new UFDouble(String.valueOf(rowMap.get("single_pay")));
						} catch (Exception e) {
							ExceptionUtils.wrappException(e);
						}
						if (key != null && value != null) {
							psndocMap.put(key, value);
						}
					}
				}
			} catch (BusinessException e) {
				ExceptionUtils.wrappException(e);
			}

			// ɾ��ǰ,�Ѹ���Ա��,����֯��,ȫ��н�ʷ�����,���ڼ������ۼƸ���Ϊ���µ�(Ҫ��ȥɾ�����Ǳ�)
			for (String pk_psndoc : psndocMap.keySet()) {

				sqlStr = " update declaration_nonparttime " + " set totalbonusforyear = totalbonusforyear- "
						+ psndocMap.get(pk_psndoc).doubleValue() + " where  vbdef1 = '" + pk_org + "' and vbdef3 ='"
						+ pk_wa_period + "'" + " and vbdef4 ='" + pk_psndoc + "'";
				try {
					dao.executeUpdate(sqlStr);
				} catch (BusinessException e) {
					ExceptionUtils.wrappException(e);
				}
			}
			sqlStr = "delete from  declaration_business " + " where vbdef1 = '" + pk_org + "'" + " and vbdef2 = '"
					+ pk_wa_class + "'" + " and vbdef3 = '" + pk_wa_period + "' and dr = 0";

			try {
				dao.executeUpdate(sqlStr);
			} catch (BusinessException e) {
				ExceptionUtils.wrappException(e);
			}

			sqlStr = "delete from  declaration_nonparttime " + " where vbdef1 = '" + pk_org + "'" + " and vbdef2 = '"
					+ pk_wa_class + "'" + " and vbdef3 = '" + pk_wa_period + "' and dr = 0";

			try {
				dao.executeUpdate(sqlStr);
			} catch (BusinessException e) {
				ExceptionUtils.wrappException(e);

			}

			sqlStr = "delete from  declaration_parttime " + " where vbdef1 = '" + pk_org + "'" + " and vbdef2 = '"
					+ pk_wa_class + "'" + " and vbdef3 = '" + pk_wa_period + "' and dr = 0";

			try {
				dao.executeUpdate(sqlStr);
			} catch (BusinessException e) {
				ExceptionUtils.wrappException(e);

			}

			// �ÿն���������н����
			// ��ѯн�Y�Ŀ���a�䱣�M�Ŀ(TWEX0000�涨��)
			String strSQL = "select itemkey from wa_classitem where pk_org='" + pk_org + "' and pk_wa_class='"
					+ pk_wa_class + "' and cyear=((select cyear from wa_period where pk_wa_period='" + pk_wa_period
					+ "')) and cperiod=((select cperiod from wa_period where pk_wa_period='" + pk_wa_period
					+ "')) and pk_wa_item = (select refvalue from twhr_basedoc where pk_org='" + pk_org
					+ "' and code = 'TWEX0000' and dr=0) ";

			String itemkey = (String) dao.executeQuery(strSQL, new ColumnProcessor());

			if (!StringUtils.isEmpty(itemkey)) {
				strSQL = "update wa_data set " + itemkey + " = 0" + " where pk_org='" + pk_org + "' AND pk_wa_class='"
						+ pk_wa_class + "' " + " AND cyear=(( " + " SELECT cyear FROM wa_period WHERE pk_wa_period='"
						+ pk_wa_period + "'))" + " AND cperiod=(( "
						+ " SELECT cperiod FROM wa_period WHERE pk_wa_period='" + pk_wa_period + "')) ";
				dao.executeUpdate(strSQL);
			}
			updateHealthFlag(pk_org, pk_wa_class, pk_wa_period);
		}
	}

}
