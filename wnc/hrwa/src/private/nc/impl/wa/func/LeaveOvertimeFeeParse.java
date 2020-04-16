package nc.impl.wa.func;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.impl.wa.paydata.PaydataDAO;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.pub.WaLoginContext;

/**
 * #21266 ���պϼ���ְ�Ӱ�Ѻ���������
 * 
 * @author yejk
 * @date 2018-9-7
 */
@SuppressWarnings({ "serial" })
public class LeaveOvertimeFeeParse extends AbstractPreExcutorFormulaParse {

	/**
	 * @Description: ִ�н���
	 * @author yejk
	 * @date 2018-9-7
	 * @param formula
	 * @param waLoginContext
	 * @throws BusinessException
	 * @return
	 */
	@SuppressWarnings("unchecked")
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

		int intComp = arguments[0].equals("0") ? -3 : (arguments[0].equals("1") ? -2 : -1); // MOD
																							// by
																							// ssx
																							// on
																							// 2020-03-09���ĺ�Ӌ�鰴����

		// н����Ŀ����
		String pk_group_item = String.valueOf(arguments[2]).replaceAll("\'", "");

		// add by ssx on 2019-04-07
		UFLiteralDate leaveBeginDate = endDate.getDateAfter(1);
		UFLiteralDate leaveEndDate = startDate.getDateAfter(startDate.getDaysMonth() - startDate.getDay()); // ��ĩ����һ��
		// MOD end

		/* ��ȡ������Ա���� start */
		String psnlistsql = PaydataDAO.getLeavePsndocSQL(waLoginContext.getPk_org(), waLoginContext.getPk_wa_class(),
				waLoginContext.getCyear(), waLoginContext.getCperiod(), leaveBeginDate, leaveEndDate, this.getContext()
						.getPk_loginUser());

		List<String> psndocList = (List<String>) basedao.executeQuery(psnlistsql, new ColumnListProcessor());
		/* ��ȡ������Ա���� end */

		int rows = (int) basedao.executeQuery(
				"select count(*) from wa_cacu_overtimefee where pk_wa_class='" + pk_wa_class + "' and creator='"
						+ this.getContext().getPk_loginUser() + "' and intcomp=" + String.valueOf(intComp),
				new ColumnProcessor());
		if (rows == 0 && psndocList != null && psndocList.size() > 0) {
			// ���ýӿڷ���Ӧ˰����˰�Ӱ��
			ISegDetailService segDetailService = NCLocator.getInstance().lookup(ISegDetailService.class);
			// ����Ӱ��,����������
			Map<String, UFDouble[]> ovtFeeResult = segDetailService.calculateOvertimeFeeByDate(pk_org,
					psndocList.toArray(new String[0]), startDate, endDate, null, null, pk_group_item, true);
			if (null == ovtFeeResult || ovtFeeResult.size() == 0) {
				throw new BusinessException("���ýӿ�ISegDetailService��ȡӦ˰(��˰)�Ӱ��Ϊ��");
			} else {
				writeToWaOTTempData(pk_wa_class, this.getContext().getPk_loginUser(), ovtFeeResult, true);
			}
		}
	}

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {

		excute(formula, getContext());

		FunctionReplaceVO fvo = new FunctionReplaceVO();
		// �������
		String[] arguments = getArguments(formula.toString());
		Pattern p = Pattern.compile("\\d");
		Matcher m = p.matcher(String.valueOf(arguments[0]).replaceAll("\'", ""));

		int intComp = arguments[0].equals("0") ? -2 : (arguments[0].equals("1") ? -3 : -1); // MOD
																							// by
																							// ssx
																							// on
																							// 2020-03-09���ĺ�Ӌ����D�{��
		// �Ƿ���˰ 0�� 1��
		int flag = Integer.valueOf(arguments[1]);

		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr("coalesce(" + "(select " + (flag == 0 ? "amounttaxable" : "amounttaxfree")
				+ " from wa_cacu_overtimefee where pk_wa_class=wa_cacu_data.pk_wa_class and creator='"
				+ this.getContext().getPk_loginUser() + "' and intcomp=" + String.valueOf(intComp)
				+ " and pk_psndoc=wa_cacu_data.pk_psndoc)" + ", 0)");
		return fvo;
	}
}
