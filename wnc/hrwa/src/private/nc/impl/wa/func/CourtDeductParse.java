package nc.impl.wa.func;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.InSQLCreator;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pubitf.twhr.IBasedocPubQuery;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.hi.psndoc.CourtDeductionSetting;
import nc.vo.hi.psndoc.DebtFileVO;
import nc.vo.hi.psndoc.DeductDetailsVO;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.hrwa.wadaysalary.MinimumLifeWays;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.twhr.basedoc.BaseDocVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang.StringUtils;

public class CourtDeductParse extends AbstractWAFormulaParse {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4429322514035828568L;
	private BaseDAO baseDao;
	public static final String TEMP_DEDUCT_TABLE_NAME = "hr_temp_deductdetails";

	public BaseDAO getBaseDao() {
		if (this.baseDao == null) {
			this.baseDao = new BaseDAO();
		}
		return this.baseDao;
	}

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		// ����Ք���
		String sql = "update wa_cacu_data set cacu_value = 0 where  " + "pk_wa_class = '" + context.getPk_wa_class()
				+ "' and creator = '" + context.getPk_loginUser() + "'";
		getBaseDao().executeUpdate(sql);
		//�����ʱ��
		sql = "delete from "+TEMP_DEDUCT_TABLE_NAME+" where waclass = '"+context.getPk_wa_class()
				+"' and salaryyearmonth = '"+context.getWaYear()+""+context.getWaPeriod()+"' ";
		getBaseDao().executeUpdate(sql);
		@SuppressWarnings("unchecked")
		List<Map<String, String>> custlist = (List<Map<String, String>>) this.getBaseDao().executeQuery(
				"select pk_psndoc from wa_cacu_data where pk_wa_class='" + context.getPk_wa_class()
						+ "' and creator = '" + context.getPk_loginUser() + "'", new MapListProcessor());
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr(" wa_cacu_data.cacu_value ");
		getdetails(getContext(), custlist);
		return fvo;

	}

	@SuppressWarnings("unchecked")
	private void getdetails(WaLoginContext context, List<Map<String, String>> custlist) throws BusinessException {
		// ͨ����Ա�����е���Ա�ķ��۲�ѯ����
		List<String> pk_psndocs = new ArrayList<String>();
		for (Map<String, String> pk_psndoc : custlist) {
			pk_psndocs.add(pk_psndoc.get("pk_psndoc"));
		}
		// Map<String, Map<String, Integer>> pkpsndoc_code = new HashMap<String,
		// Map<String, Integer>>();
		// ��ȡ�������
		Set<String> filenumberSet = new HashSet<>();
		InSQLCreator insql = new InSQLCreator();
		List<String> deflist = new ArrayList<String>();

		// ����н���ڼ�Ŀ�ʼ�ͽ���ʱ��
		List<PeriodVO> periodlist = (List<PeriodVO>) this.baseDao.retrieveByClause(
				PeriodVO.class,
				"pk_periodscheme = (select pk_periodscheme from WA_WACLASS " + " where pk_wa_class='"
						+ context.getPk_wa_class() + "') " + " and cyear='" + context.getCyear() + "' and cperiod='"
						+ context.getCperiod() + "'");
		//UFLiteralDate cbegindate = periodlist.get(0).getCstartdate();
		UFLiteralDate cenddate = (UFLiteralDate) (periodlist.get(0).getCenddate() == null ? "9999-12-31" : periodlist
				.get(0).getCenddate());
		String psnInsql = insql.getInSQL(pk_psndocs.toArray(new String[0]));
		// ��ѯ���������ķ�Ժ�ۿ���������
		List<CourtDeductionSetting> cdsettings = (List<CourtDeductionSetting>) this.baseDao.retrieveByClause(
				CourtDeductionSetting.class, "pk_psndoc in(" +psnInsql
				+ ") and dr=0 and isstop <> 'Y' and cstartdate < '" + cenddate.toString() + "'");
		if (cdsettings.size() <= 0) {
			return;
		}
		for (String pk_psndoc : pk_psndocs) {
			Map<String, Integer> fcode = new HashMap<String, Integer>();
			for (CourtDeductionSetting setting : cdsettings) {
				if (pk_psndoc.equals(setting.getAttributeValue("pk_psndoc"))) {
					fcode.put((String) setting.getAttributeValue("filenumber"),
							(Integer) setting.getAttributeValue("courtdeductways"));
					if (null != setting.getAttributeValue("mindeductcountry")) {
						deflist.add(setting.getAttributeValue("mindeductcountry").toString());
					}
					filenumberSet.add(String.valueOf(setting.getAttributeValue("filenumber")));
				}
			}
		}
		// ͨ��pk��ѯ������б�money
		List<DefdocVO> defvolist = null;
		if (deflist.size() > 0) {
			defvolist = (List<DefdocVO>) baseDao.retrieveByClause(DefdocVO.class,
					"pk_defdoc in(" + insql.getInSQL(deflist.toArray(new String[0])) + ")");
		}

		// �ڲ����趨����ȡ����Ҫ�ۿ��н����
		BaseDocVO[] basevos = NCLocator.getInstance().lookup(IBasedocPubQuery.class)
				.queryBaseDocByCodes(pk_org, new String[] { "TWEFCDDTN", "TWEFCDDTX", "TWEFCDDLF", "TWEFCDDHF","TECCD001" });
		String itemPks = "";
		//���ƈ��пۿ����
		String baseItem = null;
		//���ο�˰
		String taxItem = null;
		//�ͱ�
		String laborItem = null;
		//����
		String healthItem = null;
		//ʵ���ϼ�(����ǰ)
		String realPayBeforeItem = null;
		if (null != basevos && basevos.length > 0) {
			//���ƈ��пۿ���� code
			String pk_base = "";
			//���ο�˰ code
			String pk_tax = "";
			//�ͱ�code
			String pk_labor = "";
			//���� code
			String pk_health = "";
			//ʵ���ϼ�(����ǰ)code
			String pk_realPayBefore = "";
			
			for (BaseDocVO basevo : basevos) {
				if (basevo.getRefvalue() != null) {
					if (!StringUtils.isEmpty(itemPks)) {
						itemPks += ",'" + basevo.getRefvalue() + "'";
					} else {
						itemPks = "'" + basevo.getRefvalue() + "'";
					}

					switch (basevo.getCode()) {
					case "TWEFCDDTN":
						pk_base = basevo.getRefvalue();
						break;
					case "TWEFCDDTX":
						pk_tax = basevo.getRefvalue();
						break;
					case "TWEFCDDLF":
						pk_labor = basevo.getRefvalue();
						break;
					case "TWEFCDDHF":
						pk_health = basevo.getRefvalue();
						break;
					case "TECCD001":
						pk_realPayBefore = basevo.getRefvalue();
						break;
					}
				}
			}
			

			if (!StringUtils.isEmpty(itemPks)) {
				List<WaItemVO> waitems = (List<WaItemVO>) this.baseDao.retrieveByClause(WaItemVO.class,
						"pk_wa_item in (" + itemPks + ")");

				for (WaItemVO item : waitems) {
					if (item.getPk_wa_item().equals(pk_base)) {
						baseItem = item.getItemkey();
					} else if (item.getPk_wa_item().equals(pk_tax)) {
						taxItem = item.getItemkey();
					} else if (item.getPk_wa_item().equals(pk_labor)) {
						laborItem = item.getItemkey();
					} else if (item.getPk_wa_item().equals(pk_health)) {
						healthItem = item.getItemkey();
					} else if(item.getPk_wa_item().equals(pk_realPayBefore)){
						realPayBeforeItem = item.getItemkey();
					}
				}
			}
		}

		// ͨ��year,month��pk_psndocs ��ѯ��wa_data
		List<Map<String, String>> wadatalist = (List<Map<String, String>>) this.baseDao.executeQuery("select "
				+ baseItem + "," + taxItem + "," + laborItem + "," + healthItem+ "," + realPayBeforeItem
				+ ",f_1 , pk_psndoc from wa_data where pk_psndoc in(" + insql.getInSQL(pk_psndocs.toArray(new String[0]))
				+ ") and pk_wa_class='" + context.getPk_wa_class() + "' " + "and cyear='" + context.getCyear()
				+ "' and cperiod='" + context.getCperiod() + "'", new MapListProcessor());
		
		for (Map<String, String> payfile : wadatalist) {
			//ծȯ������Ӧ�Ŀ۷ѽ��
			Map<String,UFDouble> file2MoneyMap = new HashMap<>();

			for (CourtDeductionSetting settings : cdsettings) {
				//�������ݷ��۵Ľ��:
				UFDouble thisChargeMony = UFDouble.ZERO_DBL;
				//������ͽ��
				UFDouble minCountyFee = UFDouble.ZERO_DBL;
				if(defvolist!=null && defvolist.size() > 0){
					for (DefdocVO defvo : defvolist) {
						if (defvo.getPk_defdoc().equals((String) settings.getAttributeValue("mindeductcountry"))) {
							minCountyFee = new UFDouble(String.valueOf(defvo.getMemo()));
						}
					}	
				}
				

				if (payfile.get("pk_psndoc").equals(settings.getPk_psndoc())) {
					// �жϿۿ���ʽ
					if ((Integer) settings.getAttributeValue("courtdeductways") == 1) {
						//�����M��
						UFDouble ratio = settings.getAttributeValue("monthexecutrate") == null ? UFDouble.ZERO_DBL
								: new UFDouble((String.valueOf(settings.getAttributeValue("monthexecutrate"))));
						//���ƈ��пۿ�������
						UFDouble baseMoney = payfile.get(baseItem) == null ? UFDouble.ZERO_DBL : new UFDouble(
								String.valueOf(payfile.get(baseItem)));
						//���ο�˰���
						UFDouble taxMoney = payfile.get(taxItem) == null ? UFDouble.ZERO_DBL : new UFDouble(
								String.valueOf(payfile.get(taxItem)));
						//�ͱ���
						UFDouble laborMoney = payfile.get(laborItem) == null ? UFDouble.ZERO_DBL : new UFDouble(
								String.valueOf(payfile.get(laborItem)));
						//������
						UFDouble healthMoney = payfile.get(healthItem) == null ? UFDouble.ZERO_DBL : new UFDouble(
								String.valueOf(payfile.get(healthItem)));
						//������������M��ʽ
						Integer minimumlifeways = settings.getAttributeValue("minimumlifeways") == null ? null
								: Integer.parseInt((String.valueOf(settings.getAttributeValue("minimumlifeways"))));
						//Ӧ���ϼ�
						UFDouble shouldPayMoney = payfile.get("f_1") == null ? UFDouble.ZERO_DBL : new UFDouble(
								String.valueOf(payfile.get("f_1")));
						//ʵ�����(����ǰ)
						UFDouble realPayMoney = payfile.get(realPayBeforeItem) == null ? UFDouble.ZERO_DBL : new UFDouble(
								String.valueOf(payfile.get(realPayBeforeItem)));
						
						
						if(minimumlifeways == null){
							// ��Ѻ��� = ծ����Ӧ��н�ʣ�ȫн��- ������������ - ����˰ - �ͱ��� - ������
							thisChargeMony = baseMoney
									.sub(minCountyFee.multiply(1.2).setScale(0, UFDouble.ROUND_HALF_UP)).sub(taxMoney)
									.sub(laborMoney).sub(healthMoney);

							thisChargeMony = thisChargeMony.doubleValue() <= (baseMoney.multiply(ratio).setScale(0,
									UFDouble.ROUND_HALF_UP).doubleValue()) ? thisChargeMony : baseMoney.multiply(ratio)
									.setScale(0, UFDouble.ROUND_HALF_UP);
						}else if(minimumlifeways == MinimumLifeWays.TOTAL_PAY_OF_SHOULD.toIntValue()){
							//ʹ�Ñ��l��Ӌ������������M
							thisChargeMony = shouldPayMoney.sub(minCountyFee);
						}else if(minimumlifeways == MinimumLifeWays.TOTAL_PAY_OF_REAL.toIntValue()){
							//ʹ�Ì��l��ӋӋ�㱣����������M
							thisChargeMony = realPayMoney.sub(minCountyFee);
						}else if(minimumlifeways == MinimumLifeWays.ONE_THIRD_OF_REAL.toIntValue()){
							//����֮һ���l��Ӌ���^Ӌ��

							/**
							 * A=���l��Ӌ(ϵ�y�)�� 1/3(�Ē�����)�Ľ��~
							 * B=���l��Ӌ(ϵ�y�)�pȥ��Ϳۿ�h���Զ��x�n�����]ӛ�a�ϵĽ��~ 
							 * �� A>=B �r���Y����� A
							 * �� A<B �r���Y����� B���� B ��ؓ���r���Y����� 0
							 */
							UFDouble A = shouldPayMoney.div(3.0).setScale(0, UFDouble.ROUND_HALF_UP);
							UFDouble B = shouldPayMoney.sub(minCountyFee);
							if(A.sub(B).doubleValue() <= 0){
								thisChargeMony = A;
							}else{
								thisChargeMony = B;
								if(thisChargeMony.doubleValue() < 0){
									thisChargeMony = UFDouble.ZERO_DBL;
								}
							}

						}
					} else if ((Integer) settings.getAttributeValue("courtdeductways") == 2) {
						thisChargeMony = thisChargeMony
								.add(settings.getAttributeValue("monthexecutamount") == null ? UFDouble.ZERO_DBL
										: new UFDouble((String.valueOf(settings
												.getAttributeValue("monthexecutamount")))));

					} else {
						thisChargeMony = minCountyFee;
					}
				}
				//����С��0
				if(thisChargeMony.doubleValue() < 0){
					thisChargeMony = UFDouble.ZERO_DBL;
				}
				thisChargeMony = thisChargeMony.setScale(0, UFDouble.ROUND_HALF_UP);
				
				//ծȯ������Ӧ�Ľ��
				String fileNumber = String.valueOf(settings.getAttributeValue("filenumber"));
				if(fileNumber != null){
					UFDouble sumByFileNumber = file2MoneyMap.get(fileNumber);
					if(sumByFileNumber == null){
						sumByFileNumber = UFDouble.ZERO_DBL;
					}
					sumByFileNumber = sumByFileNumber.add(thisChargeMony);
					file2MoneyMap.put(fileNumber, sumByFileNumber);
				}
			}
			//ͨ����дȥ�жϿۿ���(���ܳ���ծȯ��������Ϳۿ���)
			UFDouble cacuvalue =  tempSaveWriteData(String.valueOf(payfile.get("pk_psndoc")),file2MoneyMap,context);;

			String strsql = "update wa_cacu_data set  cacu_value=" + cacuvalue + " " + "where pk_psndoc='"
					+ payfile.get("pk_psndoc") + "' and pk_wa_class= '" + context.getPk_wa_class()
					+ "' and creator = '" + context.getPk_loginUser() + "'";
			this.baseDao.executeUpdate(strsql);
			
		}

	}
	/**
	 * ��˵�ʱ��������ݻ�д���ۿ���ϸ�Ӽ���,�����Ƚ����ݻ��浽�м����,ʵ�����ǹ���һ���ۿ���ϸvo,
	 * ��д��ͬʱ,����ʣ��ۿ���,�ó����յķ��۽��
	 * @param valueOf
	 * @param file2MoneyMap
	 * @param context
	 * @throws BusinessException 
	 * @return ���۽��
	 */
	private UFDouble tempSaveWriteData(String pk_psndoc, Map<String, UFDouble> file2MoneyMap, WaLoginContext context) 
			throws BusinessException {
		//��д����
		List<DeductDetailsVO> rsList = new ArrayList<>();
		//���ջ�д�ķ��۽��
		UFDouble rs = UFDouble.ZERO_DBL;
		if(pk_psndoc!=null && file2MoneyMap!=null && file2MoneyMap.size() > 0){
			//����ծȯ����
			Set<String> fileNumberSet = file2MoneyMap.keySet();
			InSQLCreator insql = new InSQLCreator();
			String fileNumberInsql = insql.getInSQL(fileNumberSet.toArray(new String[0]));
			//��ѯծȯ��������Ϣ
			String sql = "select * from hi_psndoc_debtfile "
					+ " where pk_psndoc = '"+pk_psndoc+"' "
					+ " and dfilenumber in ("+fileNumberInsql+") "
					+ " and dr = 0 and isnull(stopflag,'N') = 'N'";
			@SuppressWarnings("unchecked")
			List<DebtFileVO> fileVOList = 
					(List<DebtFileVO>)getBaseDao().executeQuery(sql, new BeanListProcessor(DebtFileVO.class));
			if(fileVOList!=null && fileVOList.size() > 0){
				//fileNumber->ծȯ����list
				Map<String,List<DebtFileVO>> fileNumberListMap = new HashMap<>();
				for(DebtFileVO vo : fileVOList){
					String fileNumber = vo.getAttributeValue("dfilenumber")==null?null:
						String.valueOf(vo.getAttributeValue("dfilenumber"));
					if(vo!=null && fileNumber!=null){
						List<DebtFileVO> defList = fileNumberListMap.get(fileNumber);
						if(defList==null){
							defList = new ArrayList<>();
						}
						defList.add(vo);
						fileNumberListMap.put(fileNumber, defList);
					}
				}
				//���а������ۿ����
				for(String fileNumber : fileNumberSet){
					if(file2MoneyMap.get(fileNumber)!=null && file2MoneyMap.get(fileNumber).doubleValue() > 0 
							&& fileNumberListMap.get(fileNumber)!=null && fileNumberListMap.get(fileNumber).size() > 0){
						//���ļ�ȫ����ծȯ����
						List<DebtFileVO> debtFileList = fileNumberListMap.get(fileNumber);

						//���ļ����ܶ�
						UFDouble totalMoney = file2MoneyMap.get(fileNumber)==null?UFDouble.ZERO_DBL: file2MoneyMap.get(fileNumber);
						//��ѯ�ۿ���ϸ �ܿۿ��� key:��Ա::ծȯ����::ծȯ�� value:�ܿۿ���
						@SuppressWarnings("unchecked")
						Map<String,UFDouble> totalPayMap = (Map<String, UFDouble>) getBaseDao().executeQuery(
								"select pk_psndoc,dcreditor,filenumber,sum(deductmoney) from hi_psndoc_deductdetails "
								+ " where  pk_psndoc = '"+pk_psndoc+"' and dr = 0 "+ "and filenumber = '"+fileNumber+"'"
										+" GROUP by pk_psndoc,dcreditor,filenumber"
										, 
								new ResultSetProcessor() {
									private static final long serialVersionUID = 5149867114871700839L;
									Map<String,UFDouble> rsMap = new HashMap<>();
									@Override
									public Object handleResultSet(ResultSet rs) throws SQLException {
										while(rs.next()){
											String key = rs.getString(1)+"::"+rs.getString(3)+"::"+rs.getString(2);
											UFDouble sum = rsMap.get(key);
											if(sum == null){
												sum = UFDouble.ZERO_DBL;
											}
											sum = sum.add(rs.getDouble(4));
											rsMap.put(key, sum);
										}
										return rsMap;
									}
								});
						//��file�˴ο۵��ܶ�
						UFDouble fileSum = UFDouble.ZERO_DBL;
						for(int i = 0;i<debtFileList.size();i++){
							DeductDetailsVO rsVO = new DeductDetailsVO();
							String dfilenumber = String.valueOf(debtFileList.get(i).getAttributeValue("dfilenumber"));
							//�n����̖
							rsVO.setAttributeValue("filenumber",debtFileList.get(i).getAttributeValue("dfilenumber"));
							
							String creditor = String.valueOf(debtFileList.get(i).getAttributeValue("creditor"));
							//���෽
							rsVO.setAttributeValue("dcreditor",creditor);
							
							//н�Y����
							rsVO.setAttributeValue("waclass",context.getPk_wa_class());
							
							//н�Y����
							rsVO.setAttributeValue("salaryyearmonth",context.getCyear()+""+context.getCperiod());
							
							UFDouble rate = debtFileList.get(i).getAttributeValue("repaymentratio")==null?UFDouble.ZERO_DBL
									:new UFDouble(String.valueOf(debtFileList.get(i).getAttributeValue("repaymentratio")));
							UFDouble thisMoney = UFDouble.ZERO_DBL;
							if(i == debtFileList.size() -1){
								//���һ����,��Ҫ���д���,�����������
								thisMoney = totalMoney.sub(fileSum);
							}else{
								//�������һ����,������������
								thisMoney = totalMoney.multiply(rate).setScale(0, UFDouble.ROUND_HALF_UP);
							}
							
							//ʣ��Ŀۿ���
							UFDouble restMoney = (UFDouble)debtFileList.get(i).getAttributeValue("restmoney");
							if(restMoney==null){
								restMoney = UFDouble.ZERO_DBL;
							}
							//���ܳ���ʣ��ۿ���
							if(thisMoney.sub(restMoney).doubleValue() > 0){
								thisMoney = restMoney;
							}
							fileSum = fileSum.add(thisMoney);
							//�ۿ���~
							rsVO.setAttributeValue("deductmoney",thisMoney);
							
							//�Ѿ��ۿ�Ľ��
							UFDouble tempSum = totalPayMap.get(pk_psndoc+"::"+dfilenumber+"::"+creditor);
							if(tempSum == null){
								tempSum = UFDouble.ZERO_DBL;
							}
							tempSum = thisMoney.add(tempSum);
							totalPayMap.put(pk_psndoc+"::"+dfilenumber+"::"+creditor, tempSum);
							//��ծȯ�������ܽ��
							UFDouble creditamount  = debtFileList.get(i).getAttributeValue("creditamount")==null?UFDouble.ZERO_DBL
											:new UFDouble(String.valueOf(debtFileList.get(i).getAttributeValue("creditamount"))) ;
							//ʣ�N���~
							rsVO.setAttributeValue("remainmoney", creditamount.sub(tempSum));
							//��Ա��Ϣ
							rsVO.setPk_psndoc(pk_psndoc);
							rsList.add(rsVO);
						}
						rs = rs.add(fileSum);
					}
				}
				
			}
		}	
		if(rsList.size() > 0){
			saveRsList(rsList);
		}
		return rs;
	}
	/**
	 * �����д����ʱ��
	 * @param rsList
	 * @throws DAOException 
	 */
	private void saveRsList(List<DeductDetailsVO> rsList) throws DAOException {
		StringBuilder sb = new StringBuilder();
		for(DeductDetailsVO vo : rsList){
			if(vo.getAttributeValue("deductmoney")!=null && ((UFDouble)vo.getAttributeValue("deductmoney")).doubleValue()>=0){
				sb.delete(0, sb.length());
				sb.append("INSERT INTO "+TEMP_DEDUCT_TABLE_NAME
						+" (pk_psndoc, filenumber,dcreditor,waclass,salaryyearmonth,deductmoney,remainmoney,ts,pk_psndoc_sub) "
						+ " VALUES ('"+vo.getPk_psndoc()+"',"
								+"'"+vo.getAttributeValue("filenumber")+"', "
								+"'"+vo.getAttributeValue("dcreditor")+"', "
								+"'"+vo.getAttributeValue("waclass")+"', "
								+"'"+vo.getAttributeValue("salaryyearmonth")+"', "
								+"'"+vo.getAttributeValue("deductmoney")+"', "
								+"'"+vo.getAttributeValue("remainmoney")+"', "
								+"'"+new UFDateTime().toStdString()+"', "
								+"'"+UUID.randomUUID().toString().replace("-", "").substring(0, 20)+"' "
										+ " )");
				getBaseDao().executeUpdate(sb.toString());
			}
		}
	}
	
}
