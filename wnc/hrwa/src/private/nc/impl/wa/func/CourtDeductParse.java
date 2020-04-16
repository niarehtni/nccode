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
		// 先清空數據
		String sql = "update wa_cacu_data set cacu_value = 0 where  " + "pk_wa_class = '" + context.getPk_wa_class()
				+ "' and creator = '" + context.getPk_loginUser() + "'";
		getBaseDao().executeUpdate(sql);
		//清空临时表
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
		// 通过人员将所有的人员的法扣查询出来
		List<String> pk_psndocs = new ArrayList<String>();
		for (Map<String, String> pk_psndoc : custlist) {
			pk_psndocs.add(pk_psndoc.get("pk_psndoc"));
		}
		// Map<String, Map<String, Integer>> pkpsndoc_code = new HashMap<String,
		// Map<String, Integer>>();
		// 获取档案编号
		Set<String> filenumberSet = new HashSet<>();
		InSQLCreator insql = new InSQLCreator();
		List<String> deflist = new ArrayList<String>();

		// 查找薪资期间的开始和结束时间
		List<PeriodVO> periodlist = (List<PeriodVO>) this.baseDao.retrieveByClause(
				PeriodVO.class,
				"pk_periodscheme = (select pk_periodscheme from WA_WACLASS " + " where pk_wa_class='"
						+ context.getPk_wa_class() + "') " + " and cyear='" + context.getCyear() + "' and cperiod='"
						+ context.getCperiod() + "'");
		//UFLiteralDate cbegindate = periodlist.get(0).getCstartdate();
		UFLiteralDate cenddate = (UFLiteralDate) (periodlist.get(0).getCenddate() == null ? "9999-12-31" : periodlist
				.get(0).getCenddate());
		String psnInsql = insql.getInSQL(pk_psndocs.toArray(new String[0]));
		// 查询符合条件的法院扣款设置数据
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
		// 通过pk查询最低限市别money
		List<DefdocVO> defvolist = null;
		if (deflist.size() > 0) {
			defvolist = (List<DefdocVO>) baseDao.retrieveByClause(DefdocVO.class,
					"pk_defdoc in(" + insql.getInSQL(deflist.toArray(new String[0])) + ")");
		}

		// 在参数设定里面取出需要扣款的薪资项
		BaseDocVO[] basevos = NCLocator.getInstance().lookup(IBasedocPubQuery.class)
				.queryBaseDocByCodes(pk_org, new String[] { "TWEFCDDTN", "TWEFCDDTX", "TWEFCDDLF", "TWEFCDDHF","TECCD001" });
		String itemPks = "";
		//強制執行扣款基数
		String baseItem = null;
		//本次扣税
		String taxItem = null;
		//劳保
		String laborItem = null;
		//健保
		String healthItem = null;
		//实发合计(法扣前)
		String realPayBeforeItem = null;
		if (null != basevos && basevos.length > 0) {
			//強制執行扣款基数 code
			String pk_base = "";
			//本次扣税 code
			String pk_tax = "";
			//劳保code
			String pk_labor = "";
			//健保 code
			String pk_health = "";
			//实发合计(法扣前)code
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

		// 通过year,month和pk_psndocs 查询出wa_data
		List<Map<String, String>> wadatalist = (List<Map<String, String>>) this.baseDao.executeQuery("select "
				+ baseItem + "," + taxItem + "," + laborItem + "," + healthItem+ "," + realPayBeforeItem
				+ ",f_1 , pk_psndoc from wa_data where pk_psndoc in(" + insql.getInSQL(pk_psndocs.toArray(new String[0]))
				+ ") and pk_wa_class='" + context.getPk_wa_class() + "' " + "and cyear='" + context.getCyear()
				+ "' and cperiod='" + context.getCperiod() + "'", new MapListProcessor());
		
		for (Map<String, String> payfile : wadatalist) {
			//债券档案对应的扣费金额
			Map<String,UFDouble> file2MoneyMap = new HashMap<>();

			for (CourtDeductionSetting settings : cdsettings) {
				//本条数据法扣的金额:
				UFDouble thisChargeMony = UFDouble.ZERO_DBL;
				//县市最低金额
				UFDouble minCountyFee = UFDouble.ZERO_DBL;
				if(defvolist!=null && defvolist.size() > 0){
					for (DefdocVO defvo : defvolist) {
						if (defvo.getPk_defdoc().equals((String) settings.getAttributeValue("mindeductcountry"))) {
							minCountyFee = new UFDouble(String.valueOf(defvo.getMemo()));
						}
					}	
				}
				

				if (payfile.get("pk_psndoc").equals(settings.getPk_psndoc())) {
					// 判断扣款形式
					if ((Integer) settings.getAttributeValue("courtdeductways") == 1) {
						//月執行費率
						UFDouble ratio = settings.getAttributeValue("monthexecutrate") == null ? UFDouble.ZERO_DBL
								: new UFDouble((String.valueOf(settings.getAttributeValue("monthexecutrate"))));
						//強制執行扣款基数金额
						UFDouble baseMoney = payfile.get(baseItem) == null ? UFDouble.ZERO_DBL : new UFDouble(
								String.valueOf(payfile.get(baseItem)));
						//本次扣税金额
						UFDouble taxMoney = payfile.get(taxItem) == null ? UFDouble.ZERO_DBL : new UFDouble(
								String.valueOf(payfile.get(taxItem)));
						//劳保费
						UFDouble laborMoney = payfile.get(laborItem) == null ? UFDouble.ZERO_DBL : new UFDouble(
								String.valueOf(payfile.get(laborItem)));
						//健保费
						UFDouble healthMoney = payfile.get(healthItem) == null ? UFDouble.ZERO_DBL : new UFDouble(
								String.valueOf(payfile.get(healthItem)));
						//保留最低生活費方式
						Integer minimumlifeways = settings.getAttributeValue("minimumlifeways") == null ? null
								: Integer.parseInt((String.valueOf(settings.getAttributeValue("minimumlifeways"))));
						//应发合计
						UFDouble shouldPayMoney = payfile.get("f_1") == null ? UFDouble.ZERO_DBL : new UFDouble(
								String.valueOf(payfile.get("f_1")));
						//实发金额(法扣前)
						UFDouble realPayMoney = payfile.get(realPayBeforeItem) == null ? UFDouble.ZERO_DBL : new UFDouble(
								String.valueOf(payfile.get(realPayBeforeItem)));
						
						
						if(minimumlifeways == null){
							// 扣押金额 = 债务人应领薪资（全薪）- 县市最低生活费 - 所得税 - 劳保费 - 健保费
							thisChargeMony = baseMoney
									.sub(minCountyFee.multiply(1.2).setScale(0, UFDouble.ROUND_HALF_UP)).sub(taxMoney)
									.sub(laborMoney).sub(healthMoney);

							thisChargeMony = thisChargeMony.doubleValue() <= (baseMoney.multiply(ratio).setScale(0,
									UFDouble.ROUND_HALF_UP).doubleValue()) ? thisChargeMony : baseMoney.multiply(ratio)
									.setScale(0, UFDouble.ROUND_HALF_UP);
						}else if(minimumlifeways == MinimumLifeWays.TOTAL_PAY_OF_SHOULD.toIntValue()){
							//使用應發合計保留最低生活費
							thisChargeMony = shouldPayMoney.sub(minCountyFee);
						}else if(minimumlifeways == MinimumLifeWays.TOTAL_PAY_OF_REAL.toIntValue()){
							//使用實發合計計算保留最低生活費
							thisChargeMony = realPayMoney.sub(minCountyFee);
						}else if(minimumlifeways == MinimumLifeWays.ONE_THIRD_OF_REAL.toIntValue()){
							//三分之一應發合計比較計算

							/**
							 * A=應發合計(系統項)乘 1/3(四捨五入)的金額
							 * B=應發合計(系統項)減去最低扣款縣市自定義檔案中註記碼上的金額 
							 * 當 A>=B 時，結果等於 A
							 * 當 A<B 時，結果等於 B，當 B 為負數時，結果等於 0
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
				//不能小于0
				if(thisChargeMony.doubleValue() < 0){
					thisChargeMony = UFDouble.ZERO_DBL;
				}
				thisChargeMony = thisChargeMony.setScale(0, UFDouble.ROUND_HALF_UP);
				
				//债券档案对应的金额
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
			//通过回写去判断扣款金额(不能超过债券档案的最低扣款金额)
			UFDouble cacuvalue =  tempSaveWriteData(String.valueOf(payfile.get("pk_psndoc")),file2MoneyMap,context);;

			String strsql = "update wa_cacu_data set  cacu_value=" + cacuvalue + " " + "where pk_psndoc='"
					+ payfile.get("pk_psndoc") + "' and pk_wa_class= '" + context.getPk_wa_class()
					+ "' and creator = '" + context.getPk_loginUser() + "'";
			this.baseDao.executeUpdate(strsql);
			
		}

	}
	/**
	 * 审核的时候才能数据回写到扣款明细子集上,所有先将数据缓存到中间表里,实际上是构建一个扣款明细vo,
	 * 回写的同时,根据剩余扣款金额,得出最终的法扣金额
	 * @param valueOf
	 * @param file2MoneyMap
	 * @param context
	 * @throws BusinessException 
	 * @return 法扣金额
	 */
	private UFDouble tempSaveWriteData(String pk_psndoc, Map<String, UFDouble> file2MoneyMap, WaLoginContext context) 
			throws BusinessException {
		//回写缓存
		List<DeductDetailsVO> rsList = new ArrayList<>();
		//最终回写的法扣金额
		UFDouble rs = UFDouble.ZERO_DBL;
		if(pk_psndoc!=null && file2MoneyMap!=null && file2MoneyMap.size() > 0){
			//所有债券档案
			Set<String> fileNumberSet = file2MoneyMap.keySet();
			InSQLCreator insql = new InSQLCreator();
			String fileNumberInsql = insql.getInSQL(fileNumberSet.toArray(new String[0]));
			//查询债券档案的信息
			String sql = "select * from hi_psndoc_debtfile "
					+ " where pk_psndoc = '"+pk_psndoc+"' "
					+ " and dfilenumber in ("+fileNumberInsql+") "
					+ " and dr = 0 and isnull(stopflag,'N') = 'N'";
			@SuppressWarnings("unchecked")
			List<DebtFileVO> fileVOList = 
					(List<DebtFileVO>)getBaseDao().executeQuery(sql, new BeanListProcessor(DebtFileVO.class));
			if(fileVOList!=null && fileVOList.size() > 0){
				//fileNumber->债券档案list
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
				//进行按比例扣款操作
				for(String fileNumber : fileNumberSet){
					if(file2MoneyMap.get(fileNumber)!=null && file2MoneyMap.get(fileNumber).doubleValue() > 0 
							&& fileNumberListMap.get(fileNumber)!=null && fileNumberListMap.get(fileNumber).size() > 0){
						//此文件全部的债券档案
						List<DebtFileVO> debtFileList = fileNumberListMap.get(fileNumber);

						//此文件的总额
						UFDouble totalMoney = file2MoneyMap.get(fileNumber)==null?UFDouble.ZERO_DBL: file2MoneyMap.get(fileNumber);
						//查询扣款明细 总扣款金额 key:人员::债券档案::债券方 value:总扣款金额
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
						//此file此次扣的总额
						UFDouble fileSum = UFDouble.ZERO_DBL;
						for(int i = 0;i<debtFileList.size();i++){
							DeductDetailsVO rsVO = new DeductDetailsVO();
							String dfilenumber = String.valueOf(debtFileList.get(i).getAttributeValue("dfilenumber"));
							//檔案編號
							rsVO.setAttributeValue("filenumber",debtFileList.get(i).getAttributeValue("dfilenumber"));
							
							String creditor = String.valueOf(debtFileList.get(i).getAttributeValue("creditor"));
							//債權方
							rsVO.setAttributeValue("dcreditor",creditor);
							
							//薪資方案
							rsVO.setAttributeValue("waclass",context.getPk_wa_class());
							
							//薪資年月
							rsVO.setAttributeValue("salaryyearmonth",context.getCyear()+""+context.getCperiod());
							
							UFDouble rate = debtFileList.get(i).getAttributeValue("repaymentratio")==null?UFDouble.ZERO_DBL
									:new UFDouble(String.valueOf(debtFileList.get(i).getAttributeValue("repaymentratio")));
							UFDouble thisMoney = UFDouble.ZERO_DBL;
							if(i == debtFileList.size() -1){
								//最后一个数,需要进行处理,让其等于总数
								thisMoney = totalMoney.sub(fileSum);
							}else{
								//不是最后一个数,进行四舍五入
								thisMoney = totalMoney.multiply(rate).setScale(0, UFDouble.ROUND_HALF_UP);
							}
							
							//剩余的扣款金额
							UFDouble restMoney = (UFDouble)debtFileList.get(i).getAttributeValue("restmoney");
							if(restMoney==null){
								restMoney = UFDouble.ZERO_DBL;
							}
							//不能超过剩余扣款金额
							if(thisMoney.sub(restMoney).doubleValue() > 0){
								thisMoney = restMoney;
							}
							fileSum = fileSum.add(thisMoney);
							//扣款金額
							rsVO.setAttributeValue("deductmoney",thisMoney);
							
							//已经扣款的金额
							UFDouble tempSum = totalPayMap.get(pk_psndoc+"::"+dfilenumber+"::"+creditor);
							if(tempSum == null){
								tempSum = UFDouble.ZERO_DBL;
							}
							tempSum = thisMoney.add(tempSum);
							totalPayMap.put(pk_psndoc+"::"+dfilenumber+"::"+creditor, tempSum);
							//此债券档案的总金额
							UFDouble creditamount  = debtFileList.get(i).getAttributeValue("creditamount")==null?UFDouble.ZERO_DBL
											:new UFDouble(String.valueOf(debtFileList.get(i).getAttributeValue("creditamount"))) ;
							//剩餘金額
							rsVO.setAttributeValue("remainmoney", creditamount.sub(tempSum));
							//人员信息
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
	 * 将结果写入临时表
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
