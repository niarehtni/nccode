package nc.impl.wa.func;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.pub.WaLoginContext;

/**
 * #21266 ���պϼƼӰ�Ѻ���������
 * 
 * @author yejk
 * @date 2018-9-7
 */
@SuppressWarnings({ "serial" })
public class OvertimeFeeParse extends AbstractPreExcutorFormulaParse {

	/**
	 * @Description: ִ�н���
	 * @author yejk
	 * @date 2018-9-7
	 * @param formula
	 * @param waLoginContext
	 * @throws BusinessException
	 * @return
	 */
	@Override
	public void excute(Object formula, WaLoginContext waLoginContext) throws BusinessException {
		BaseDAO basedao = new BaseDAO();
		// н�ʷ�������
		String pk_wa_class = waLoginContext.getWaLoginVO().getPk_wa_class();
		// ��֯
		String pk_org = waLoginContext.getPk_org();
		// н���ڼ�
		// UFLiteralDate startDate =
		// waLoginContext.getWaLoginVO().getPeriodVO().getCstartdate();
		// UFLiteralDate endDate =
		// waLoginContext.getWaLoginVO().getPeriodVO().getCenddate();
		// н���ڼ����
		String caccyear = waLoginContext.getWaLoginVO().getPeriodVO().getCaccyear();
		// н���ڼ��·�
		String cperiod = waLoginContext.getWaLoginVO().getPeriodVO().getCperiod();

		/* ͨ��н���ڼ��ȡ�����ڼ����ֹ���� start */
		String queryDateSql = "select tbm_period.begindate,tbm_period.enddate from tbm_period where tbm_period.accyear = ?  and tbm_period.accmonth  = ? and tbm_period.pk_org = ?";
		SQLParameter params = new SQLParameter();
		params.addParam(caccyear);
		params.addParam(cperiod);
		params.addParam(pk_org);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> dateListMap = (List<Map<String, Object>>) basedao.executeQuery(queryDateSql, params,
				new MapListProcessor());
		if (null == dateListMap) {
			throw new BusinessException("ͨ��н���ڼ��ȡ�����ڼ���ֹ����Ϊ��");
		}
		UFLiteralDate startDate = new UFLiteralDate(dateListMap.get(0).get("begindate").toString());
		UFLiteralDate endDate = new UFLiteralDate(dateListMap.get(0).get("enddate").toString());
		/* ͨ��н���ڼ��ȡ�����ڼ����ֹ���� end */

		// �������
		String[] arguments = getArguments(formula.toString());
		Pattern p = Pattern.compile("\\d");

		Matcher m = p.matcher(String.valueOf(arguments[0]).replaceAll("\'", ""));
		int intComp = 0;
		if (m.find()) {
			intComp = Integer.valueOf(m.group());
		}

		m = p.matcher(String.valueOf(arguments[1]).replaceAll("\'", ""));
		// �Ƿ���˰ 0�� 1��
		int isTaxFreeFlag = 0;
		if (m.find()) {
			isTaxFreeFlag = Integer.valueOf(m.group());
		}

		// н����Ŀ����
		String pk_group_item = String.valueOf(arguments[2]).replaceAll("\'", "");

		/* ��ȡ������Ա���� start */
		String psndocsSql = "select wa_cacu_data.pk_psndoc from wa_cacu_data where wa_cacu_data.pk_wa_class = '"
				+ pk_wa_class + "'";

		List<String> psndocList = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>) basedao.executeQuery(psndocsSql,
				new MapListProcessor());
		if (null == result) {
			throw new BusinessException("Ӧ˰(��˰)�Ӱ�Ѽ���-��ȡ��ԱpkΪ��");
		}
		for (int i = 0; i < result.size(); i++) {
			Map<String, Object> map = result.get(i);
			String pk_psndoc1 = map.get("pk_psndoc").toString();
			psndocList.add(pk_psndoc1);
		}
		String[] psndocArr = psndocList.toArray(new String[0]);
		/* ��ȡ������Ա���� end */

		int rows = (int) basedao.executeQuery("select count(*) from wa_cacu_overtimefee where pk_wa_class='"
				+ pk_wa_class + "' and creator='" + this.getContext().getWaLoginVO().getCreator() + "' and intComp="
				+ String.valueOf(intComp), new ColumnProcessor());
		if (rows == 0) {
			// ���ýӿڷ���Ӧ˰����˰�Ӱ��
			ISegDetailService segDetailService = NCLocator.getInstance().lookup(ISegDetailService.class);
			// ����Ӱ��,����������
			/*
			 * Map<String, UFDouble[]> ovtFeeResult =
			 * segDetailService.calculateTaxableByDate(pk_org, psndocArr,
			 * startDate, endDate, null, null,pk_group_item);
			 */
			// ssx modified for Multi-Thread Calculation
			Map<String, UFDouble[]> ovtFeeResult = segDetailService.calculateOvertimeFeeByDate_MT(pk_org, psndocArr,
					startDate, endDate, null, null, pk_group_item, false);
			// end
			if (null == ovtFeeResult || ovtFeeResult.size() == 0) {
				throw new BusinessException("���ýӿ�ISegDetailService��ȡӦ˰(��˰)�Ӱ��Ϊ��");
			} else {
				writeToTempData(pk_wa_class, this.getContext().getWaLoginVO().getCreator(), ovtFeeResult);
			}
		}
		// ��������
		PersistenceManager sessionManager = null;
		try {
			sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			for (int i = 0; i < psndocArr.length; i++) {
				String updateSql = "update wa_cacu_data set cacu_value = (select ";
				if (isTaxFreeFlag == 1) {// 1����˰ �Ӱ��
					updateSql += "amounttaxfree";
				} else {// ���� Ӧ˰�Ӱ��
					updateSql += "amounttaxable";
				}
				updateSql += " from wa_cacu_overtimefee where pk_wa_class='" + pk_wa_class + "' and creator='"
						+ this.getContext().getWaLoginVO().getCreator() + "' and intcomp=" + String.valueOf(intComp)
						+ " and pk_psndoc='" + psndocArr[i] + "') where pk_wa_class = '" + pk_wa_class
						+ "' and pk_psndoc = '" + psndocArr[i] + "'";
				session.addBatch(updateSql);
			}
			session.executeBatch();
		} catch (DbException e) {
			e.printStackTrace();
		} finally {
			if (sessionManager != null) {
				sessionManager.release();
			}
		}
	}
}
