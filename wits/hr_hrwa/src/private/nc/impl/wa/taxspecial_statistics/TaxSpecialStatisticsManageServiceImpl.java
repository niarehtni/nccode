package nc.impl.wa.taxspecial_statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.InSQLCreator;
import nc.itf.hr.wa.IPaydataQueryService;
import nc.itf.hr.wa.ITaxSpecialStatisticsManageService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.wa.paydata.AggPayDataVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.taxspecial_statistics.TaxSpecialStatisticsVO;
import nc.vo.wa_tax.TaxUpgradeHelper;
import nc.vo.wa_tax.TaxupgradeDef;

import org.apache.commons.lang.ArrayUtils;

/**
 * ר��ӿ۳����ܷ���
 * 
 * @author xuhw
 */
public class TaxSpecialStatisticsManageServiceImpl implements ITaxSpecialStatisticsManageService {

	private TaxSpecialStatisticsDAO dao;

	public TaxSpecialStatisticsDAO getDao() {
		if (dao == null) {
			dao = new TaxSpecialStatisticsDAO();
		}
		return dao;
	}

	@Override
	public void lockData(WaLoginContext context, String condition, String orderSQL) throws BusinessException {
		// TODO Auto-generated method stub
		// ֻҪ��һ��û�������Ϳ����ٴ�ʹ��
		// TODO Auto-generated method stub
		// 1�� ����н�����ݺ�������������ר��ӷ��ÿ۳��ۼ�Ӧ�ۣ��ۼ��ѿۣ����ڿ۳�
		// �ۼ�Ӧ�۵��߼�
		// ���Ա���ķ�н�ڼ䣬 ���� 201901�� 201902
		// �ж��Ƿ��Ѿ�������ˣ�������˱����������
		String pk_condtion = this.getConditionWaDataPks(context, condition);
		this.getDao().lockType(context, pk_condtion);
	}

	@Override
	public void unlockData(WaLoginContext context, String condition, String orderSQL) throws BusinessException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		// 1�� ����н�����ݺ�������������ר��ӷ��ÿ۳��ۼ�Ӧ�ۣ��ۼ��ѿۣ����ڿ۳�
		// �ۼ�Ӧ�۵��߼�
		// ���Ա���ķ�н�ڼ䣬 ���� 201901�� 201902
		// �ж��Ƿ��Ѿ�������ˣ�������˱����������
		String pk_condtion = this.getConditionWaDataPks(context, condition);
		// ֻҪ��һ���������Ϳ����ٴ�ʹ��
		this.getDao().unLockType(context, pk_condtion);
	}

	/**
	 * ����������������
	 * 
	 * @throws BusinessException
	 */
	@Override
	public void genData(WaLoginContext context, String condition, String orderSQL) throws BusinessException {

		// TODO Auto-generated method stub
		// 1�� ����н�����ݺ�������������ר��ӷ��ÿ۳��ۼ�Ӧ�ۣ��ۼ��ѿۣ����ڿ۳�
		// �ۼ�Ӧ�۵��߼�
		// ���Ա���ķ�н�ڼ䣬 ���� 201901�� 201902
		// �ж��Ƿ��Ѿ�������ˣ�������˱����������
		String pk_condtion = this.getConditionWaDataPks(context, condition);
		// н������
		DataVO[] datas = getDao().queryByPKSCondition(pk_condtion, null);

		DataVO[] datasTotalable = this.getDao().queryTotalableAmts(context, condition, orderSQL);
		Map<String, DataVO> datasTotalableMap = this.getDao().convertToMap(datasTotalable, PayfileVO.PK_PSNDOC);

		DataVO[] datasTotalableAll = this.getDao().queryTotalableAllAmts(context, condition, orderSQL);
		Map<String, DataVO> datasTotalableAllMap = this.getDao().convertToMap(datasTotalableAll, PayfileVO.PK_PSNDOC);
		DataVO[] datasTotalabled = this.getDao().queryTotaledAllAmts(context, condition, orderSQL);
		Map<String, DataVO> datasTotalabledMap = this.getDao().convertToMap(datasTotalabled, PayfileVO.PK_PSNDOC);

		// ��ɾ������
		this.getDao().deleteByCondition(context, pk_condtion);
		List<TaxSpecialStatisticsVO> inserVO = new ArrayList<TaxSpecialStatisticsVO>();
		DataVO dataTotalableVo = null;// �ۼ�Ӧ��
		DataVO dataTotalableAllVO = null;// �ۼ�����Ӧ��
		DataVO dataTotalabledVo = null;// �ۼ��ѿ�
		int cnt = 0;
		Integer intAble = 0;// Ӧ��
		Integer intAbled = 0;// �ѿ�
		Integer alltotalbleAmt = 0;//ȫ���ۼ�Ӧ��
		Integer alltotalbledAmt = 0;//ȫ���ۼ��ѿ�
		for (DataVO datavo : datas) {
			String strTaxType = datavo.getAttributeValue("tax_type")+"";
			if (strTaxType.equals(TaxupgradeDef.TAXTYPE_LOCKED+"")) {
				//�Ѿ������ķ���
				continue;
			}
			alltotalbleAmt = 0;//ȫ���ۼ�Ӧ��
			alltotalbledAmt = 0;//ȫ���ۼ��ѿ�
			TaxSpecialStatisticsVO vo = new TaxSpecialStatisticsVO();
			// dataTotalableVo = datasTotalableMap.get(datavo.getPk_psndoc());
			dataTotalableAllVO = datasTotalableAllMap.get(datavo.getPk_psndoc());
			dataTotalabledVo = datasTotalabledMap.get(datavo.getPk_psndoc());
			vo.setPk_wa_data(datavo.getPk_wa_data());
			vo.setPk_wa_class(datavo.getPk_wa_class());
			vo.setCyear(datavo.getCyear());
			vo.setCperiod(datavo.getCperiod());
			vo.setTaxyear(datavo.getAttributeValue(TaxSpecialStatisticsVO.TAXYEAR) + "");
			vo.setTaxperiod(datavo.getAttributeValue(TaxSpecialStatisticsVO.TAXPERIOD) + "");
			vo.setTax_type(TaxupgradeDef.TAXTYPE_GENED);// ������
			vo.setPk_psndoc(datavo.getPk_psndoc());// ��ԱpK��һ��
			// �����ۼ�Ӧ��
			dataTotalableVo = datasTotalableMap.get(datavo.getPk_psndoc()
					+ TaxUpgradeHelper.convertAdddeductionStrKey2IntKey(TaxSpecialStatisticsVO.PARENTFEE));
			alltotalbleAmt = objectToInt(dataTotalableVo, "amount");
			vo.setAttributeValue(TaxSpecialStatisticsVO.PARENTFEETOTAL, alltotalbleAmt);
			dataTotalableVo = datasTotalableMap.get(datavo.getPk_psndoc()
					+ TaxUpgradeHelper.convertAdddeductionStrKey2IntKey(TaxSpecialStatisticsVO.CHILDFEE));
			alltotalbleAmt = alltotalbleAmt + objectToInt(dataTotalableVo, "amount");
			vo.setAttributeValue(TaxSpecialStatisticsVO.CHILDFEETOTAL, objectToInt(dataTotalableVo, "amount"));
			dataTotalableVo = datasTotalableMap.get(datavo.getPk_psndoc()
					+ TaxUpgradeHelper.convertAdddeductionStrKey2IntKey(TaxSpecialStatisticsVO.HOURSEFEE));
			alltotalbleAmt = alltotalbleAmt + objectToInt(dataTotalableVo, "amount");
			vo.setAttributeValue(TaxSpecialStatisticsVO.HOURSEFEETOTAL, objectToInt(dataTotalableVo, "amount"));

			dataTotalableVo = datasTotalableMap.get(datavo.getPk_psndoc()
					+ TaxUpgradeHelper.convertAdddeductionStrKey2IntKey(TaxSpecialStatisticsVO.HOURSEZUFEE));
			alltotalbleAmt = alltotalbleAmt + objectToInt(dataTotalableVo, "amount");
			vo.setAttributeValue(TaxSpecialStatisticsVO.HOURSEZUFEETOTAL, objectToInt(dataTotalableVo, "amount"));

			dataTotalableVo = datasTotalableMap.get(datavo.getPk_psndoc()
					+ TaxUpgradeHelper.convertAdddeductionStrKey2IntKey(TaxSpecialStatisticsVO.EDUCATIONFEE));
			alltotalbleAmt = alltotalbleAmt + objectToInt(dataTotalableVo, "amount");
			vo.setAttributeValue(TaxSpecialStatisticsVO.EDUCATIONFEETOTAL, objectToInt(dataTotalableVo, "amount"));

			dataTotalableVo = datasTotalableMap.get(datavo.getPk_psndoc()
					+ TaxUpgradeHelper.convertAdddeductionStrKey2IntKey(TaxSpecialStatisticsVO.HEALTHYFEE));
			alltotalbleAmt = alltotalbleAmt + objectToInt(dataTotalableVo, "amount");
			vo.setAttributeValue(TaxSpecialStatisticsVO.HEALTHYFEETOTAL, objectToInt(dataTotalableVo, "amount"));

			// �����ۼ�Ӧ�úϼ�
			vo.setAttributeValue(TaxSpecialStatisticsVO.ALLFEETOTAL, alltotalbleAmt);

			// �����ۼ��Ѿ�
			alltotalbledAmt = objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.PARENTFEE);
			vo.setAttributeValue(TaxSpecialStatisticsVO.PARETNFEEDTOTAL,
					objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.PARENTFEE));
			alltotalbledAmt = alltotalbledAmt + objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.CHILDFEE);
			vo.setAttributeValue(TaxSpecialStatisticsVO.CHILDFEEDTOTAL,
					objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.CHILDFEE));
			alltotalbledAmt = alltotalbledAmt + objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.HOURSEFEE);
			vo.setAttributeValue(TaxSpecialStatisticsVO.HOURSEFEEDTOTAL,
					objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.HOURSEFEE));
			alltotalbledAmt = alltotalbledAmt + objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.HOURSEZUFEE);
			vo.setAttributeValue(TaxSpecialStatisticsVO.HOURSEZUFEEDTOTAL,
					objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.HOURSEZUFEE));
			alltotalbledAmt = alltotalbledAmt + objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.EDUCATIONFEE);
			vo.setAttributeValue(TaxSpecialStatisticsVO.EDUCATIONFEEDTOTAL,
					objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.EDUCATIONFEE));
			alltotalbledAmt = alltotalbledAmt + objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.HEALTHYFEE);
			vo.setAttributeValue(TaxSpecialStatisticsVO.HEALTHYFEEDTOTAL,
					objectToInt(dataTotalabledVo, TaxSpecialStatisticsVO.HEALTHYFEE));
			vo.setAttributeValue(TaxSpecialStatisticsVO.ALLFEEDTOTAL, alltotalbledAmt);

			// ������ �ۼ�Ӧ�� - �ۼ��ѿ�
			intAble = objectToInt(vo, TaxSpecialStatisticsVO.PARENTFEETOTAL);
			intAbled = objectToInt(vo, TaxSpecialStatisticsVO.PARETNFEEDTOTAL);
			vo.setAttributeValue(TaxSpecialStatisticsVO.PARENTFEE, intAble - intAbled);

			intAble = objectToInt(vo, TaxSpecialStatisticsVO.CHILDFEETOTAL);
			intAbled = objectToInt(vo, TaxSpecialStatisticsVO.CHILDFEEDTOTAL);
			vo.setAttributeValue(TaxSpecialStatisticsVO.CHILDFEE, intAble - intAbled);

			intAble = objectToInt(vo, TaxSpecialStatisticsVO.HOURSEFEETOTAL);
			intAbled = objectToInt(vo, TaxSpecialStatisticsVO.HOURSEFEEDTOTAL);
			vo.setAttributeValue(TaxSpecialStatisticsVO.HOURSEFEE, intAble - intAbled);

			intAble = objectToInt(vo, TaxSpecialStatisticsVO.HOURSEZUFEETOTAL);
			intAbled = objectToInt(vo, TaxSpecialStatisticsVO.HOURSEZUFEEDTOTAL);
			vo.setAttributeValue(TaxSpecialStatisticsVO.HOURSEZUFEE, intAble - intAbled);

			intAble = objectToInt(vo, TaxSpecialStatisticsVO.EDUCATIONFEETOTAL);
			intAbled = objectToInt(vo, TaxSpecialStatisticsVO.EDUCATIONFEEDTOTAL);
			vo.setAttributeValue(TaxSpecialStatisticsVO.EDUCATIONFEE, intAble - intAbled);

			intAble = objectToInt(vo, TaxSpecialStatisticsVO.HEALTHYFEETOTAL);
			intAbled = objectToInt(vo, TaxSpecialStatisticsVO.HEALTHYFEEDTOTAL);
			vo.setAttributeValue(TaxSpecialStatisticsVO.HEALTHYFEE, intAble - intAbled);

			intAble = objectToInt(vo, TaxSpecialStatisticsVO.ALLFEETOTAL);
			intAbled = objectToInt(vo, TaxSpecialStatisticsVO.ALLFEEDTOTAL);
			vo.setAttributeValue(TaxSpecialStatisticsVO.ALLFEE, intAble - intAbled);
			inserVO.add(vo);
			cnt++;
			if (cnt == 500) {
				this.getDao().insertVOs(inserVO);
				inserVO.clear();
			}
		}

		if (inserVO.size() > 0) {
			this.getDao().insertVOs(inserVO);
		}
	}

	private int objectToInt(SuperVO superVO, String key) {
		if (superVO == null) {
			return 0;
		}
		if (key == null) {
			return 0;
		}
		return Integer.valueOf(superVO.getAttributeValue(key) + "");
	}

	/**
	 * ��ȡҵ�������Χ
	 * 
	 * @param context
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	private String getConditionWaDataPks(WaLoginContext context, String condition) throws BusinessException {
		IPaydataQueryService queryService = NCLocator.getInstance().lookup(IPaydataQueryService.class);
		AggPayDataVO aggDataVO;
		try {
			aggDataVO = queryService.queryAggPayDataVOByCondition(context, condition, null);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			return null;
		}
		if (ArrayUtils.isEmpty(aggDataVO.getDataPKs())) {
			return null;
		}
		InSQLCreator inSQLCreator = new InSQLCreator();
		String insql = inSQLCreator.getInSQL(aggDataVO.getDataPKs());
		return insql;
	}
}
