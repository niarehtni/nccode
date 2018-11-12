package nc.impl.hi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.InSQLCreator;
import nc.impl.hi.utils.LegalOrgUtils;
import nc.impl.hr.pub.Calculator;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.itf.hi.PsndocDefUtil;
import nc.itf.hr.hi.InsuranceTypeEnum;
import nc.itf.twhr.IGroupinsuranceMaintain;
import nc.itf.twhr.INhiCalcGenerateSrv;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.twhr.IRangetablePubQuery;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.psn.PsnjobVO;
import nc.vo.hi.psndoc.GroupInsuranceDetailVO;
import nc.vo.hi.psndoc.GroupInsuranceRecordVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocDefVO;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.twhr.basedoc.BaseDocVO;
import nc.vo.twhr.groupinsurance.CalcModelEnum;
import nc.vo.twhr.groupinsurance.GroupInsuranceSettingVO;
import nc.vo.twhr.nhicalc.NhiCalcVO;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;
import nc.vo.twhr.rangetable.RangeLineVO;
import nc.vo.twhr.rangetable.RangeTableAggVO;
import nc.vo.twhr.rangetable.RangeTableTypeEnum;
import nc.vo.wa.paydata.PsndocWaVO;

public class PsndocSubInfoService4JFSImpl implements IPsndocSubInfoService4JFS {
    private BaseDAO baseDao;
    // �����
    private RangeTableAggVO[] rangeTables = null;
    private boolean baseOnAverageSalary = false;

    // Ares.Tank ������֯
    private String pkLegalOrg = null;

    // Ares.Tank
    private final int ADD_FORM = 1;// Ͷ����̬-�ӱ�
    private final int DEL_FORM = 2;// Ͷ����̬-�˱�
    private final int ADJ_FORM = 3;// Ͷ����̬-��н����

    private RangeTableAggVO[] getRangeTables(String pk_org, String cyear, String cperiod) throws BusinessException {
	if (rangeTables == null) {
	    IRangetablePubQuery qry = NCLocator.getInstance().lookup(IRangetablePubQuery.class);

	    rangeTables = qry.queryRangetableByType(pk_org, -1, this.getFirstDayOfMonth(cyear, cperiod));
	}
	return rangeTables;
    }

    private RangeTableAggVO[] getRangeTables(String pk_org, UFDate date) throws BusinessException {
	if (rangeTables == null) {
	    IRangetablePubQuery qry = NCLocator.getInstance().lookup(IRangetablePubQuery.class);

	    rangeTables = qry.queryRangetableByType(pk_org, -1, date);
	}
	return rangeTables;
    }

    private UFDate getFirstDayOfMonth(String year, String month) {
	Calendar calendar = Calendar.getInstance();
	calendar.set(Integer.valueOf(year), Integer.valueOf(month) - 1, 1);
	calendar.set(Calendar.DAY_OF_MONTH, 1);
	return new UFDate(calendar.getTime()).asBegin();
    }

    private RangeLineVO findRangeLine(RangeTableAggVO[] rtAggVOs, int rangeType, UFDouble salAmount) {
	for (RangeTableAggVO agg : rtAggVOs) {
	    if (agg.getParentVO().getTabletype().equals(rangeType)) {
		RangeLineVO[] lines = (RangeLineVO[]) agg.getChildren(RangeLineVO.class);
		for (RangeLineVO line : lines) {
		    UFDouble stdUpperValue = line.getRangeupper();
		    if (stdUpperValue == null || stdUpperValue.equals(UFDouble.ZERO_DBL)) {
			stdUpperValue = new UFDouble(Double.MAX_VALUE);
		    }
		    UFDouble stdLowerValue = line.getRangelower();
		    if (stdLowerValue == null) {
			stdLowerValue = UFDouble.ZERO_DBL;
		    }
		    if (salAmount.doubleValue() >= stdLowerValue.doubleValue()
			    && salAmount.doubleValue() <= (stdUpperValue.toDouble() == 0 ? Double.MAX_VALUE
				    : stdUpperValue.doubleValue())) {
			if ((RangeTableTypeEnum.LABOR_RANGETABLE.toIntValue() == rangeType && Integer.valueOf(line
				.getRangegrade()) == 30)
				|| RangeTableTypeEnum.LABOR_RANGETABLE.toIntValue() != rangeType) {
			    return line;
			}
		    }
		}
	    }
	}
	return null;
    }

    @Override
    public void calculateGroupIns(String pk_org, String pk_wa_class, String cYear, String cPeriod)
	    throws BusinessException {
	// ȡ�ü������£�δ��˵�ǰ�ڼ䣩
	Map periodMap = getCalculatePeriod(pk_org, cYear, cPeriod);

	String cyear = (String) periodMap.get("cyear");
	String cperiod = (String) periodMap.get("cperiod");
	String cstartdate = (String) periodMap.get("cstartdate");
	String cenddate = (String) periodMap.get("cenddate");

	// ���M�������gȡ���������ˆT�б�
	String pk_psndocs = getGroupInsurancePsnList(pk_org, cyear, cperiod);

	// ȡ��Ա�Ӽ��趨
	Map<String, Map<String, GroupInsSettingVO>> groupSettings = getGroupSettings(cstartdate, cenddate, pk_psndocs,
		pk_org, cyear, cperiod);

	Map<String, String[]> groupInsRatePair = new HashMap<String, String[]>();
	// ȡ�ű����ʱ�
	getGroupInsuranceSettings(groupSettings, groupInsRatePair);

	// Ӌ��F�� <pk_psndoc, GroupInsDetailVO>
	Map<String, GroupInsDetailVO> calcRst = calculateGroupInsurance(pk_org, cstartdate, cenddate, groupSettings,
		groupInsRatePair);
	// Ӌ�dÿ��ÿ���T���c��ٿ�����ؓ��֮�F���M�������ԑ�Ӌ��Ąt��ƽ�֣������M��������һ�������N�~
	Map<String, List<FamEmpGroupInsDetailVO>> calcFEdetails = calcFEdetailGroupIns(pk_org, cstartdate, cenddate,
		groupSettings, groupInsRatePair);
	// ��д�ű���ϸ�Ӽ�
	writeBackPsndoc(calcRst);
	// ��д������Ա���ű�Ͷ����ϸ by he
	writeBackfamempdetail(calcFEdetails);
	// ���н�Y�l����Ӌ���ӛ
	clearWaDataCalculateFlag(pk_org, pk_wa_class, cyear, cperiod, calcRst);
    }

    private void writeBackfamempdetail(Map<String, List<FamEmpGroupInsDetailVO>> calcFEdetails)
	    throws BusinessException, DAOException {
	if (calcFEdetails.size() > 0) {
	    SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
	    service.setLazyLoad(true);
	    for (String rst : calcFEdetails.keySet()) {
		for (FamEmpGroupInsDetailVO famempvo : calcFEdetails.get(rst)) {
		    String strSQL = "";
		    strSQL += "delete from groupinsurance_detail where pk_psndoc='" + famempvo.getPk_psndoc()
			    + "' and begindate='" + famempvo.getdStartDate();
		    if (null == famempvo.getdEndDate()) {
			strSQL += "' and (enddate is null or enddate = 'null' or enddate ='~')";
		    } else {
			strSQL += "' and enddate='" + famempvo.getdEndDate() + "'";
		    }
		    strSQL += "and name = '" + famempvo.getName() + "'";
		    strSQL += "and insurancecode = '" + getdefname(famempvo.getInsurancecode()) + "'";
		    strSQL += "and identitycode = '" + getdefname(famempvo.getIdentitycode()) + "'";
		    this.getBaseDao().executeUpdate(strSQL);
		    strSQL = "update groupinsurance_detail set recordnum = recordnum +1 "
			    + " where pk_psndoc='"
			    + famempvo.getPk_psndoc()
			    + "' and exists(select * from groupinsurance_detail def where groupinsurance_detail.pk_psndoc = def.pk_psndoc and def.recordnum = 1)";
		    this.getBaseDao().executeUpdate(strSQL);

		    // PsndocDefVO defVO =
		    // PsndocDefUtil.getFamEmpInsuranceDetailVO();
		    GroupInsuranceDetailVO defVO = new GroupInsuranceDetailVO();

		    defVO.setBegindate(famempvo.getdStartDate());
		    defVO.setEnddate(famempvo.getdEndDate());
		    defVO.setPk_psndoc(rst);
		    defVO.setRecordnum(0);
		    defVO.setLastflag(UFBoolean.TRUE);
		    defVO.setDr(0);
		    defVO.setAttributeValue("name", famempvo.getName());
		    defVO.setAttributeValue("insurancecode", getdefname(famempvo.getInsurancecode()));
		    defVO.setAttributeValue("identitycode", getdefname(famempvo.getIdentitycode()));
		    defVO.setAttributeValue("ishousehold", famempvo.getIshousehold());
		    defVO.setAttributeValue("insuranceamount", famempvo.getInsuranceamount());
		    this.getBaseDao().insertVO(defVO);
		}
	    }
	}

    }

    private String getdefname(String insurancecode) {
	List<DefdocVO> inlist = null;
	try {
	    inlist = (List<DefdocVO>) this.getBaseDao().retrieveByClause(DefdocVO.class,
		    "pk_defdoc='" + insurancecode + "'");
	} catch (DAOException e) {
	    e.printStackTrace();
	}
	return inlist.get(0).getName();
    }

    private Map<String, List<FamEmpGroupInsDetailVO>> calcFEdetailGroupIns(String pk_org, String cstartdate,
	    String cenddate, Map<String, Map<String, GroupInsSettingVO>> groupSettings,
	    Map<String, String[]> groupInsRatePair) throws BusinessException {
	IGroupinsuranceMaintain srv = NCLocator.getInstance().lookup(IGroupinsuranceMaintain.class);
	GroupInsuranceSettingVO[] setVOs = srv.queryByCondition(pk_org, groupInsRatePair.values());
	// �����ű�����
	Map<String, List<FamEmpGroupInsDetailVO>> calcFEdetails = new HashMap<String, List<FamEmpGroupInsDetailVO>>();
	if (groupSettings != null && groupSettings.size() > 0 && setVOs != null && setVOs.length > 0) {
	    for (Entry<String, Map<String, GroupInsSettingVO>> grpSets : groupSettings.entrySet()) {
		String pk_psndoc = grpSets.getKey();
		// ��list������Ա���;���������-����
		List<String> idinslists = new ArrayList<String>();
		List<FamEmpGroupInsDetailVO> famemplist = new ArrayList<FamEmpGroupInsDetailVO>();
		for (Entry<String, GroupInsSettingVO> psnSet : grpSets.getValue().entrySet()) {
		    FamEmpGroupInsDetailVO famempvo = new FamEmpGroupInsDetailVO();
		    GroupInsuranceSettingVO setting = getGroupInsSetting(setVOs, psnSet.getValue()
			    .getPk_GroupInsurance(), psnSet.getValue().getPk_RelationType());

		    UFDouble psn_sub_amount = UFDouble.ZERO_DBL;
		    // �������˱������Д��Ƿ�Ӌ��
		    // UFLiteralDate curMonthStart = new
		    // UFLiteralDate(cstartdate);
		    UFLiteralDate curMonthEnd = new UFLiteralDate(cenddate);
		    // 2017-12��Ҏ��ֻҪ������������һ���Ӌ��ȫ�±��M
		    boolean isCalc = (psnSet.getValue().getdStartDate().isSameDate(curMonthEnd) || psnSet.getValue()
			    .getdStartDate().before(curMonthEnd))
			    && (psnSet.getValue().getdEndDate().isSameDate(curMonthEnd) || psnSet.getValue()
				    .getdEndDate().after(curMonthEnd));

		    if (isCalc) // Ӌ��ȫ��start
		    {
			if (setting != null) // ���F���U�NӋ��start
			{
			    UFDouble salaryBase = (psnSet.getValue().getiSalaryBase() == null || psnSet.getValue()
				    .getiSalaryBase().equals(UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL : psnSet
				    .getValue().getiSalaryBase();
			    UFDouble times = (setting.getDtimes() == null || setting.getDtimes().equals(
				    UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL : setting.getDtimes();
			    UFDouble upper = (setting.getDupper() == null || setting.getDupper().equals(
				    UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL : setting.getDupper();
			    UFDouble rate = (setting.getDrate() == null || setting.getDrate().equals(UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL
				    : setting.getDrate();
			    if (CalcModelEnum.FIXAMOUNT.equalsValue(setting.getIcalmode())) // Ӌ�㷽ʽstart
			    {
				// ���~
				// ������ӆ֮���~
				psn_sub_amount = (setting.getDfixamount() == null || setting.getDfixamount().equals(
					UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL : setting.getDfixamount();
			    } else if (CalcModelEnum.SALARYBASE.equalsValue(setting.getIcalmode())) {
				// ��н����
				// Ӌ�㷽ʽ�鱣н����*��������С춵�����ޣ��tʹ��(��н����*����)*�M��
				psn_sub_amount = salaryBase.multiply(times).doubleValue() > upper.doubleValue() ? upper
					.multiply(rate) : salaryBase.multiply(times).multiply(rate);
			    } else if (CalcModelEnum.FORMULAR.equalsValue(setting.getIcalmode())) {
				// ��ʽ
				String formular = setting.getCformularstr();
				// iif( glbdef6 <= 200000 , 200000 , glbdef6 *
				// 200000)
				formular = formular.replace("glbdef6", salaryBase.toString());
				psn_sub_amount = Calculator.evalExp(formular);
			    } // Ӌ�㷽ʽend

			    if (!calcFEdetails.containsKey(pk_psndoc)) {
				famempvo.setPk_org(pk_org);
				famempvo.setPk_psndoc(pk_psndoc);
				famempvo.setdStartDate(new UFLiteralDate(cstartdate));
				famempvo.setdStartDate(new UFLiteralDate(cenddate));
				famempvo.setInsurancecode(psnSet.getValue().getPk_GroupInsurance());
				famempvo.setIdentitycode(psnSet.getValue().getPk_RelationType());
				famempvo.setIshousehold(setting.getIshousehold());
				famempvo.setName(psnSet.getValue().getsName());
				// ����-����
				idinslists.add(famempvo.getInsurancecode() + famempvo.getIdentitycode());
			    }
			    if (setting.getBselfpay().booleanValue()) {
				famempvo.setInsuranceamount(psn_sub_amount);

			    } else {
				famempvo.setInsuranceamount(UFDouble.ZERO_DBL);
			    }
			    famemplist.add(famempvo);

			} else {
			    throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
				    "twhr_personalmgt", "068J61035-0013")/*
									  * @res
									  * δ�ҵ��F���U�N�n��
									  */);
			}// ���F���U�NӋ��end
		    }// Ӌ��ȫ��end
		}
		
		// ȡ����ͬ����+���ֵ���Ա��������޸�
		for (GroupInsuranceSettingVO setvo : setVOs) {
		    // �����������-���ݵ����ݾ͸��½��
		    List<FamEmpGroupInsDetailVO> hlist = new ArrayList<FamEmpGroupInsDetailVO>();
		    List<FamEmpGroupInsDetailVO> hnlist = new ArrayList<FamEmpGroupInsDetailVO>();
		    for(FamEmpGroupInsDetailVO famempvo : famemplist){
		    	if((famempvo.getInsurancecode() + famempvo.getIdentitycode()).equals(setvo.getCgrpinsid()
						+ setvo.getCgrpinsrelid())){
		    		hlist.add(famempvo);
		    	}else{
		    		hnlist.add(famempvo);
		    	}
		    }
		   
		    int i =1;
		    if(hlist.size() > 0){
		    	for (FamEmpGroupInsDetailVO famempvo : hlist) {
		    		// rem(3,2)=1
		    		
		    		int insuranceamount = (int) famempvo.getInsuranceamount().doubleValue();
		    		if (i==1) {
		    			famempvo.setInsuranceamount(new UFDouble((insuranceamount / hlist.size())
		    					+ (insuranceamount % (hlist.size()))));
		    		} else {
		    			
		    			famempvo.setInsuranceamount(new UFDouble((insuranceamount / hlist.size())));
		    		}
		    		i++;
		    	}
		    	famemplist.clear();
		    	famemplist.addAll(hlist);
		    	famemplist.addAll(hnlist);
		    }
		   }
		calcFEdetails.put(pk_psndoc, famemplist);
		}
	}
	return calcFEdetails;
    }

    private void clearWaDataCalculateFlag(String pk_org, String pk_wa_class, String cyear, String cperiod,
	    Map<String, GroupInsDetailVO> calcRst) throws DAOException {
	if (calcRst.size() > 0) {
	    for (Entry<String, GroupInsDetailVO> rst : calcRst.entrySet()) {
		String pk_psndoc = rst.getKey();
		String strSQL = "update wa_data set caculateflag='N' where pk_org='" + pk_org + "' and pk_psndoc='"
			+ pk_psndoc + "' and pk_wa_class = '" + pk_wa_class + "' and cyear='" + cyear
			+ "' and cperiod='" + cperiod + "' and dr=0";
		this.getBaseDao().executeUpdate(strSQL);
	    }
	}
    }

    private Map getCalculatePeriod(String pk_org, String cYear, String cPeriod) throws DAOException, BusinessException {
	String strSQL = "select cyear, cperiod, cstartdate, cenddate from wa_period where pk_wa_period in"
		+ " (select pk_wa_period from wa_periodstate where " + " enableflag='Y' and isapproved='N' "
		+ " and pk_org = '" + pk_org + "') and cyear = '" + cYear + "' and cperiod = '" + cPeriod + "'";

	Map periodMap = (Map) this.getBaseDao().executeQuery(strSQL, new MapProcessor());
	if (periodMap == null || periodMap.size() == 0) {
	    throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
		    "068J61035-0011")/* @res ������Ҫ�����ű���н���ڼ� */);
	}
	return periodMap;
    }

    private String getGroupInsurancePsnList(String pk_org, String cyear, String cperiod) throws BusinessException {
	List<String> psnList = findPersonList(pk_org, cyear, cperiod);
	if (psnList == null || psnList.size() == 0) {
	    throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
		    "068J61035-0012")/* @res ������Ҫ�����ű���Ա�� */);
	}
	String pk_psndocs = "";
	for (String pk_psndoc : psnList) {
	    if (StringUtils.isEmpty(pk_psndocs)) {
		pk_psndocs = "'" + pk_psndoc + "'";
	    } else {
		pk_psndocs = pk_psndocs + ",'" + pk_psndoc + "'";
	    }
	}
	return pk_psndocs;
    }

    private void getGroupInsuranceSettings(Map<String, Map<String, GroupInsSettingVO>> groupSettings,
	    Map<String, String[]> groupInsRatePair) {
	if (groupSettings.size() > 0) {
	    for (Entry<String, Map<String, GroupInsSettingVO>> psnSet : groupSettings.entrySet()) {
		if (psnSet.getValue().size() > 0) {
		    for (Entry<String, GroupInsSettingVO> psnSubSet : psnSet.getValue().entrySet()) {
			String[] pair = new String[2]; // �U�N:���ݽM��
			pair[0] = psnSubSet.getValue().getPk_GroupInsurance();
			pair[1] = psnSubSet.getValue().getPk_RelationType();
			if (!groupInsRatePair.containsKey(pair[0] + pair[1])) {
			    groupInsRatePair.put(pair[0] + pair[1], pair);
			}
		    }
		}
	    }
	}
    }

    private void writeBackPsndoc(Map<String, GroupInsDetailVO> calcRst) throws BusinessException, DAOException {
	if (calcRst.size() > 0) {
	    SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
	    service.setLazyLoad(true);
	    for (Entry<String, GroupInsDetailVO> rst : calcRst.entrySet()) {
		String strSQL = "delete from " + "hi_psndoc_groupinsrecord" + " where pk_psndoc='"
			+ rst.getValue().getPk_psndoc() + "' and begindate='" + rst.getValue().getdStartDate()
			+ "' and enddate='" + rst.getValue().getdEndDate() + "'";
		this.getBaseDao().executeUpdate(strSQL);
		strSQL = "update hi_psndoc_groupinsrecord set recordnum = recordnum +1 " + " where pk_psndoc='"
			+ rst.getValue().getPk_psndoc() + "' and exists(select * from "
			+ "hi_psndoc_groupinsrecord def where hi_psndoc_groupinsrecord"
			+ ".pk_psndoc = def.pk_psndoc and def.recordnum = 1)";
		this.getBaseDao().executeUpdate(strSQL);

		PsndocDefVO defVO = new GroupInsuranceRecordVO();
		defVO.setBegindate(rst.getValue().getdStartDate());
		defVO.setEnddate(rst.getValue().getdEndDate());
		defVO.setPk_psndoc(rst.getKey());
		defVO.setRecordnum(0);
		defVO.setLastflag(UFBoolean.TRUE);
		defVO.setDr(0);
		defVO.setAttributeValue("stuffpay", rst.getValue().getiStuffPay());
		defVO.setAttributeValue("companypay", rst.getValue().getiCompanyPay());
		defVO.setAttributeValue("empinsurancemoney", rst.getValue().getEmpinsurancemoney());
		defVO.setAttributeValue("fayinsurancemoney", rst.getValue().getFayinsurancemoney());
		service.insert(defVO);
	    }
	}
    }

    private Map<String, GroupInsDetailVO> calculateGroupInsurance(String pk_org, String cstartdate, String cenddate,
	    Map<String, Map<String, GroupInsSettingVO>> groupSettings, Map<String, String[]> groupInsRatePair)
	    throws BusinessException {
	IGroupinsuranceMaintain srv = NCLocator.getInstance().lookup(IGroupinsuranceMaintain.class);
	GroupInsuranceSettingVO[] setVOs = srv.queryByCondition(pk_org, groupInsRatePair.values());
	// �����ű�����
	Map<String, GroupInsDetailVO> calcRst = new HashMap<String, GroupInsDetailVO>();
	if (groupSettings != null && groupSettings.size() > 0 && setVOs != null && setVOs.length > 0) {
	    for (Entry<String, Map<String, GroupInsSettingVO>> grpSets : groupSettings.entrySet()) {
		// �Ƿ��Ի�����
		Map<String, String> ishouse = new HashMap<String, String>();
		String pk_psndoc = grpSets.getKey();
		for (Entry<String, GroupInsSettingVO> psnSet : grpSets.getValue().entrySet()) {
		    GroupInsuranceSettingVO setting = getGroupInsSetting(setVOs, psnSet.getValue()
			    .getPk_GroupInsurance(), psnSet.getValue().getPk_RelationType());
		    if (null == ishouse || ishouse.size() < 0
			    || !ishouse.containsKey(setting.getCgrpinsid() + "-" + setting.getCgrpinsrelid())) {

			UFDouble psn_sub_amount = UFDouble.ZERO_DBL;
			// �������˱������Д��Ƿ�Ӌ��
			// UFLiteralDate curMonthStart = new
			// UFLiteralDate(cstartdate);
			UFLiteralDate curMonthEnd = new UFLiteralDate(cenddate);
			// 2017-12��Ҏ��ֻҪ������������һ���Ӌ��ȫ�±��M
			boolean isCalc = (psnSet.getValue().getdStartDate().isSameDate(curMonthEnd) || psnSet
				.getValue().getdStartDate().before(curMonthEnd))
				&& (psnSet.getValue().getdEndDate().isSameDate(curMonthEnd) || psnSet.getValue()
					.getdEndDate().after(curMonthEnd));

			if (isCalc) // Ӌ��ȫ��start
			{
			    if (setting != null) // ���F���U�NӋ��start
			    {
				UFDouble salaryBase = (psnSet.getValue().getiSalaryBase() == null || psnSet.getValue()
					.getiSalaryBase().equals(UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL : psnSet
					.getValue().getiSalaryBase();
				UFDouble times = (setting.getDtimes() == null || setting.getDtimes().equals(
					UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL : setting.getDtimes();
				UFDouble upper = (setting.getDupper() == null || setting.getDupper().equals(
					UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL : setting.getDupper();
				UFDouble rate = (setting.getDrate() == null || setting.getDrate().equals(
					UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL : setting.getDrate();
				if (CalcModelEnum.FIXAMOUNT.equalsValue(setting.getIcalmode())) // Ӌ�㷽ʽstart
				{
				    // ���~
				    // ������ӆ֮���~
				    psn_sub_amount = (setting.getDfixamount() == null || setting.getDfixamount()
					    .equals(UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL : setting.getDfixamount();
				} else if (CalcModelEnum.SALARYBASE.equalsValue(setting.getIcalmode())) {
				    // ��н����
				    // Ӌ�㷽ʽ�鱣н����*��������С춵�����ޣ��tʹ��(��н����*����)*�M��
				    psn_sub_amount = salaryBase.multiply(times).doubleValue() > upper.doubleValue() ? upper
					    .multiply(rate) : salaryBase.multiply(times).multiply(rate);
				} else if (CalcModelEnum.FORMULAR.equalsValue(setting.getIcalmode())) {
				    // ��ʽ
				    String formular = setting.getCformularstr();
				    // iif( glbdef6 <= 200000 , 200000 , glbdef6
				    // *
				    // 200000)
				    formular = formular.replace("glbdef6", salaryBase.toString());
				    psn_sub_amount = Calculator.evalExp(formular);
				} // Ӌ�㷽ʽend

				if (!calcRst.containsKey(pk_psndoc)) {
				    calcRst.put(pk_psndoc, new GroupInsDetailVO());
				    calcRst.get(pk_psndoc).setPk_org(pk_org);
				    calcRst.get(pk_psndoc).setPk_psndoc(pk_psndoc);
				    calcRst.get(pk_psndoc).setdStartDate(new UFLiteralDate(cstartdate));
				    calcRst.get(pk_psndoc).setdEndDate(new UFLiteralDate(cenddate));
				    calcRst.get(pk_psndoc).setiStuffPay(UFDouble.ZERO_DBL);
				    calcRst.get(pk_psndoc).setiCompanyPay(UFDouble.ZERO_DBL);
				    calcRst.get(pk_psndoc).setEmpinsurancemoney(UFDouble.ZERO_DBL);
				    calcRst.get(pk_psndoc).setFayinsurancemoney(UFDouble.ZERO_DBL);
				}

				if (setting.getBselfpay().booleanValue()) {
				    calcRst.get(pk_psndoc).setiStuffPay(
					    calcRst.get(pk_psndoc).getiStuffPay().add(psn_sub_amount)); // ����ؓ��
				    if (null != setting.getFamilygroupinsurance()) {
					if (setting.getFamilygroupinsurance().booleanValue()) {
					    calcRst.get(pk_psndoc).setFayinsurancemoney(
						    calcRst.get(pk_psndoc).getFayinsurancemoney().add(psn_sub_amount));
					} else {
					    calcRst.get(pk_psndoc).setEmpinsurancemoney(
						    calcRst.get(pk_psndoc).getEmpinsurancemoney().add(psn_sub_amount));
					}
				    }
				} else {
				    calcRst.get(pk_psndoc).setiCompanyPay(
					    calcRst.get(pk_psndoc).getiCompanyPay().add(psn_sub_amount)); // ��˾ؓ��
				}
			    } else {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"twhr_personalmgt", "068J61035-0013")/*
									      * @res
									      * δ�ҵ��F���U�N�n��
									      */);
			    }// ���F���U�NӋ��end
			}// Ӌ��ȫ��end
			if (null != setting.getIshousehold() && setting.getIshousehold().booleanValue()) {
			    ishouse.put(setting.getCgrpinsid() + "-" + setting.getCgrpinsrelid(), null);
			}
		    }
		}
	    }
	}
	return calcRst;
    }

    private GroupInsuranceSettingVO getGroupInsSetting(GroupInsuranceSettingVO[] setVOs, String pk_GroupInsurance,
	    String pk_PsnType) {
	for (GroupInsuranceSettingVO vo : setVOs) {
	    if (vo.getCgrpinsid().equals(pk_GroupInsurance) && vo.getCgrpinsrelid().equals(pk_PsnType)) {
		return vo;
	    }
	}

	return null;
    }

    private Map<String, Map<String, GroupInsSettingVO>> getGroupSettings(String cstartdate, String cenddate,
	    String pk_psndocs, String pk_org, String cYear, String cPeriod) throws BusinessException {
	Map<String, Map<String, GroupInsSettingVO>> groupSettings = new HashMap<String, Map<String, GroupInsSettingVO>>();

	String strSQL = "select begindate, isnull(enddate, '9999-12-31 23:59:59') enddate, pk_psndoc, pk_psndoc_sub,"
		+ "glbdef1 name, glbdef2 id, glbdef3 birthday, glbdef4 pk_reltype, "
		+ "glbdef5 pk_groupins, isnull(glbdef6, 0) salarybase, glbdef7 isend from "
		+ PsndocDefTableUtil.getGroupInsuranceTablename()
		+ " where begindate <= '"
		+ cenddate
		+ "' "
		+ " and isnull(enddate, '9999-12-31 23:59:59') >= '"
		+ cstartdate
		+ "' and glbdef1 is not null "
		+ " and glbdef2 is not null "
		+ " and glbdef4 is not null "
		+ " and glbdef5 is not null "
		+ " and glbdef7 <> 'Y'"
		+ " and pk_psndoc in ("
		+ pk_psndocs
		+ ")"
		+ " and pk_psndoc not in ( select distinct pk_psndoc from wa_data wd "
		+ " inner join wa_waclass wc on wd.pk_wa_class = wc.pk_wa_class "
		+ "  inner join wa_periodstate  wp on wc.pk_wa_class = wp.pk_wa_class "
		+ " where wc.isapporve = 'Y' and wc.pk_org = '"
		+ pk_org
		+ "' and wp.isapproved='Y' and wd.cyear = '"
		+ cYear + "' and wd.cperiod = '" + cPeriod + "' )";
	List<Map> grpinsSetting = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());

	if (grpinsSetting != null && grpinsSetting.size() != 0) {
	    for (Map psnSetting : grpinsSetting) {
		String pk_psndoc = (String) psnSetting.get("pk_psndoc");
		String pk_psndoc_sub = (String) psnSetting.get("pk_psndoc_sub");
		if (!groupSettings.containsKey(pk_psndoc)) {
		    groupSettings.put(pk_psndoc, new HashMap<String, GroupInsSettingVO>());
		}

		if (!groupSettings.get(pk_psndoc).containsKey(pk_psndoc_sub)) {
		    groupSettings.get(pk_psndoc).put(pk_psndoc_sub, new GroupInsSettingVO());
		}

		GroupInsSettingVO vo = groupSettings.get(pk_psndoc).get(pk_psndoc_sub);
		vo.setdStartDate(new UFLiteralDate((String) psnSetting.get("begindate")));
		vo.setdEndDate(new UFLiteralDate((String) psnSetting.get("enddate")));
		vo.setsName((String) psnSetting.get("name"));
		vo.setsID((String) psnSetting.get("id"));
		vo.setdBirthday(new UFLiteralDate((String) psnSetting.get("birthday")));
		vo.setPk_GroupInsurance((String) psnSetting.get("pk_groupins"));
		vo.setPk_RelationType((String) psnSetting.get("pk_reltype"));
		vo.setiSalaryBase(new UFDouble(String.valueOf(psnSetting.get("salarybase"))));
		vo.setbIsEnd(UFBoolean.valueOf((String) psnSetting.get("isend")));
	    }
	}
	return groupSettings;
    }

    @Override
    public void renewRange(String pk_org, String cyear, String cperiod) throws BusinessException {
	// ���M�������gȡ���������ˆT�б�
	List<String> psnList = findPersonList(pk_org, cyear, cperiod);

	// ȡ�����
	this.getRangeTables(pk_org, cyear, cperiod);

	// ���M�����gȡ���{н�c�ڽ����O�������Y��
	INhiCalcGenerateSrv srv = NCLocator.getInstance().lookup(INhiCalcGenerateSrv.class);
	NhiCalcVO[] nhiFinalVOs = srv.getAdjustNHIData(psnList, pk_org, cyear, cperiod);
	// �з��в�����{�Y����0�Ąڽ���ӛ�
	StringBuilder strSQL = getUpdateSQL(nhiFinalVOs);

	// �½��ڽ����Y�� Ares.Tank
	PsndocDefVO[] savedVOs = getNewVOs(nhiFinalVOs, pk_org, cyear, cperiod, false);

	this.getBaseDao().executeUpdate(strSQL.toString());
	SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
	if (savedVOs != null && savedVOs.length > 0) {
	    for (PsndocDefVO vo : savedVOs) {
		service.insert(vo);
	    }
	}
    }

    /**
     *
     * @param nhiFinalVOs
     * @param pk_org
     * @param cyear
     * @param cperiod
     * @param state
     *            �Ƿ�����ְ������--true Ϊ�ӱ� false:��н����
     * @return
     * @throws BusinessException
     */
    private PsndocDefVO[] getNewVOs(NhiCalcVO[] nhiFinalVOs, String pk_org, String cyear, String cperiod, boolean state)
	    throws BusinessException {
	List<PsndocDefVO> psnNhiVOs = new ArrayList<PsndocDefVO>();

	if (nhiFinalVOs != null && nhiFinalVOs.length > 0) {
	    for (NhiCalcVO vo : nhiFinalVOs) {
		Map<String, Object> originValues = getOriginValues(vo);
		if ((vo.getLaborrange() != null && !vo.getLaborrange().equals(vo.getOldlaborrange()) && !UFDouble.ZERO_DBL
			.equals(vo.getLaborrange()))
			|| (vo.getRetirerange() != null && !vo.getRetirerange().equals(vo.getOldretirerange()) && !UFDouble.ZERO_DBL
				.equals(vo.getRetirerange()))) {
		    PsndocDefVO newLaborVO = PsndocDefUtil.getPsnLaborVO();
		    newLaborVO.setPk_psndoc(vo.getPk_psndoc());
		    newLaborVO.setLastflag(UFBoolean.TRUE);
		    newLaborVO.setBegindate(new UFLiteralDate(vo.getBegindate().toString()));
		    newLaborVO.setEnddate(originValues.get("labor_enddate") == null ? null : new UFLiteralDate(
			    (String) originValues.get("labor_enddate")));
		    newLaborVO.setAttributeValue("glbdef1", originValues.get("labor_glbdef1"));
		    if ((vo.getLaborrange() != null && !vo.getLaborrange().equals(vo.getOldlaborrange()) && !UFDouble.ZERO_DBL
			    .equals(vo.getLaborrange()))) {
			newLaborVO.setAttributeValue("glbdef2", vo.getLaborsalary());
			newLaborVO.setAttributeValue("glbdef3", UFDouble.ZERO_DBL);
			newLaborVO.setAttributeValue("glbdef4", vo.getLaborrange());
		    } else {
			newLaborVO.setAttributeValue("glbdef2", originValues.get("labor_glbdef2"));
			newLaborVO.setAttributeValue("glbdef3", originValues.get("labor_glbdef3"));
			newLaborVO.setAttributeValue("glbdef4", originValues.get("labor_glbdef4"));
		    }
		    newLaborVO.setAttributeValue("glbdef5", originValues.get("labor_glbdef5"));
		    newLaborVO.setAttributeValue("glbdef6", UFBoolean.FALSE);
		    // glbdef7 ���˼���
		    if (vo.getOldretirerange() == null) {
			vo.setOldretirerange(UFDouble.ZERO_DBL);
		    }
		    boolean isRetireRangeChanged = false;
		    if (vo.getRetirerange() != null && !vo.getRetirerange().equals(vo.getOldretirerange())
			    && !UFDouble.ZERO_DBL.equals(vo.getRetirerange())) {
			newLaborVO.setAttributeValue("glbdef7", vo.getRetirerange());
			isRetireRangeChanged = true;
		    } else {
			newLaborVO.setAttributeValue("glbdef7", originValues.get("labor_glbdef7"));
		    }
		    // glbdef8 ����������~
		    if ((new UFDouble(String.valueOf(originValues.get("labor_glbdef8")))).doubleValue() > 0
			    && isRetireRangeChanged) {
			RangeLineVO laborLine = findRangeLine(this.getRangeTables(pk_org, cyear, cperiod),
				RangeTableTypeEnum.RETIRE_RANGETABLE.toIntValue(),
				(UFDouble) newLaborVO.getAttributeValue("glbdef7"));
			newLaborVO.setAttributeValue("glbdef8", laborLine.getEmployeeamount());
		    } else {
			newLaborVO.setAttributeValue("glbdef8", originValues.get("labor_glbdef8"));
		    }
		    newLaborVO.setAttributeValue("glbdef9", originValues.get("labor_glbdef9"));
		    newLaborVO.setAttributeValue("glbdef10", originValues.get("labor_glbdef10"));
		    newLaborVO.setAttributeValue("glbdef11", originValues.get("labor_glbdef11"));
		    newLaborVO.setAttributeValue("glbdef12", originValues.get("labor_glbdef12"));
		    newLaborVO.setAttributeValue("glbdef13", originValues.get("labor_glbdef13"));
		    // ����Ͷ���_ʼ����
		    newLaborVO.setAttributeValue("glbdef14", newLaborVO.getBegindate());
		    // ����Ͷ���_ʼ����
		    // Ares.Tank 2018-9-14 14:36:29 �޸�һ������װ������
		    /*
		     * newLaborVO.setAttributeValue("glbdef14", new
		     * UFDate(newLaborVO.getBegindate().toDate()));
		     */
		    // Ares.Tank �ͱ�������֯��Ͷ����̬������
		    newLaborVO.setAttributeValue("legalpersonorg", pkLegalOrg);
		    // ������˵,�˴�Ӧ����ö����setֵ��,Ϊʲô��������?
		    // 1.��Ϊû��ö���� 2.hrtaovertimereg.bmf���Ԫ�������е�ë�� -->3.���ֱ����ĸ��ʺϱ���
		    // ������--�ӱ�,ͬ���ͱ�����--��н����
		    if (state) {
			newLaborVO.setAttributeValue("insuranceform", ADD_FORM);
		    } else {
			newLaborVO.setAttributeValue("insuranceform", ADJ_FORM);
		    }
		    psnNhiVOs.add(newLaborVO);
		}
		if (vo.getHealthrange() != null && !vo.getHealthrange().equals(vo.getOldhealthrange())
			&& !UFDouble.ZERO_DBL.equals(vo.getHealthrange())) {
		    PsndocDefVO newHealVO = PsndocDefUtil.getPsnHealthVO();
		    newHealVO.setPk_psndoc(vo.getPk_psndoc());
		    newHealVO.setLastflag(UFBoolean.TRUE);
		    newHealVO.setBegindate(new UFLiteralDate(vo.getBegindate().toString()));
		    newHealVO.setEnddate(originValues.get("heal_enddate") == null ? null : new UFLiteralDate(
			    (String) originValues.get("heal_enddate")));
		    newHealVO.setAttributeValue("glbdef1", originValues.get("heal_glbdef1"));
		    newHealVO.setAttributeValue("glbdef2", originValues.get("heal_glbdef2"));
		    newHealVO.setAttributeValue("glbdef3", originValues.get("heal_glbdef3"));
		    newHealVO.setAttributeValue("glbdef4", originValues.get("heal_glbdef4"));
		    newHealVO.setAttributeValue("glbdef5", originValues.get("heal_glbdef5"));
		    newHealVO.setAttributeValue("glbdef6", vo.getHealthsalary());
		    // glbdef16 ��������
		    newHealVO.setAttributeValue("glbdef16", vo.getHealthrange());
		    newHealVO.setAttributeValue("glbdef7", UFDouble.ZERO_DBL);
		    newHealVO.setAttributeValue("glbdef8", originValues.get("heal_glbdef8"));
		    newHealVO.setAttributeValue("glbdef9", originValues.get("heal_glbdef9"));
		    newHealVO.setAttributeValue("glbdef10", originValues.get("heal_glbdef10"));
		    newHealVO.setAttributeValue("glbdef11", originValues.get("heal_glbdef11"));
		    newHealVO.setAttributeValue("glbdef12", originValues.get("heal_glbdef12"));
		    newHealVO.setAttributeValue("glbdef13", UFBoolean.FALSE);
		    newHealVO.setAttributeValue("glbdef14", originValues.get("heal_glbdef14"));
		    newHealVO.setAttributeValue("glbdef15", originValues.get("heal_glbdef15"));

		    // Ares.Tank �ͱ�������֯��Ͷ����̬������
		    newHealVO.setAttributeValue("legalpersonorg", pkLegalOrg);
		    // ������˵,�˴�Ӧ����ö����setֵ��,Ϊʲô��������?
		    // 1.��Ϊû��ö���� 2.hrtaovertimereg.bmf���Ԫ�������е�ë�� -->3.���ֱ����ĸ��ʺϱ���
		    // // ������--�ӱ�,ͬ���ͱ�����--��н����
		    if (state) {
			newHealVO.setAttributeValue("insuranceform", ADD_FORM);
		    } else {
			newHealVO.setAttributeValue("insuranceform", ADJ_FORM);
		    }
		    psnNhiVOs.add(newHealVO);
		}
	    }
	}
	return psnNhiVOs.toArray(new PsndocDefVO[0]);
    }

    private Map<String, Object> getOriginValues(NhiCalcVO vo) throws BusinessException {
	StringBuilder strSQL = new StringBuilder();
	strSQL.append("SELECT labor.enddate labor_enddate, labor.glbdef1 labor_glbdef1, labor.glbdef5 labor_glbdef5, "
		+ "		 labor.glbdef2 labor_glbdef2,labor.glbdef3 labor_glbdef3,labor.glbdef4 labor_glbdef4,labor.glbdef7 labor_glbdef7, "
		+ "       isnull(labor.glbdef8, 0) labor_glbdef8, labor.glbdef9 labor_glbdef9, labor.glbdef10 labor_glbdef10, labor.glbdef11 labor_glbdef11, "
		+ "		 labor.glbdef12 labor_glbdef12, labor.glbdef13 labor_glbdef13, heal.enddate heal_enddate, heal.glbdef1 heal_glbdef1,  "
		+ "       heal.glbdef2 heal_glbdef2, heal.glbdef3 heal_glbdef3, heal.glbdef4 heal_glbdef4,  "
		+ "       heal.glbdef5 heal_glbdef5, heal.glbdef8 heal_glbdef8, heal.glbdef9 heal_glbdef9,  "
		+ "       heal.glbdef10 heal_glbdef10, heal.glbdef11 heal_glbdef11, heal.glbdef12 heal_glbdef12, "
		+ "       heal.glbdef14 heal_glbdef14, heal.glbdef15 heal_glbdef15, heal.glbdef16 heal_glbdef16 "
		+ "FROM bd_psndoc psn LEFT OUTER JOIN " + PsndocDefTableUtil.getPsnLaborTablename()
		+ " labor ON psn.pk_psndoc = labor.pk_psndoc AND labor.begindate < '" + vo.getEnddate()
		+ "' AND isnull(labor.enddate, '9999-12-31') > '" + vo.getBegindate() + "' LEFT OUTER JOIN "
		+ PsndocDefTableUtil.getPsnHealthTablename()
		+ " heal ON psn.pk_psndoc = heal.pk_psndoc AND heal.begindate < '" + vo.getEnddate()
		+ "' AND isnull(heal.enddate, '9999-12-31') > '" + vo.getBegindate()
		+ "' AND psn.id = heal.glbdef3 WHERE (psn.pk_psndoc = '" // �����C��̖���_�Jֻȡ�T���Լ�
		+ vo.getPk_psndoc() + "')");
	return (Map<String, Object>) this.getBaseDao().executeQuery(strSQL.toString(), new MapProcessor());
    }

    private StringBuilder getUpdateSQL(NhiCalcVO[] nhiFinalVOs) throws BusinessException {
	StringBuilder strSQL = new StringBuilder();
	if (nhiFinalVOs != null && nhiFinalVOs.length > 0) {
	    for (NhiCalcVO vo : nhiFinalVOs) {
		if (vo.getOldlaborrange() == null) {
		    vo.setOldlaborrange(UFDouble.ZERO_DBL);
		}
		if ((!vo.getOldlaborrange().equals(vo.getLaborrange()) && !UFDouble.ZERO_DBL.equals(vo.getLaborrange()))
			|| (!vo.getOldretirerange().equals(vo.getRetirerange()) && !UFDouble.ZERO_DBL.equals(vo
				.getRetirerange()))) {
		    strSQL.append(" UPDATE " + PsndocDefTableUtil.getPsnLaborTablename() + " SET enddate = '"
			    + vo.getBegindate().getDateBefore(1).asEnd().toStdString()
			    + "', modifiedtime=CONVERT([nchar](19), GETDATE(), 20), modifier='"
			    + InvocationInfoProxy.getInstance().getUserId()
			    + "', ts = CONVERT([nchar](19), GETDATE(), 20), lastflag = 'N' " + "WHERE (pk_psndoc = '"
			    + vo.getPk_psndoc() + "') AND (begindate < '" + vo.getEnddate()
			    + "') AND (isnull(enddate, '9999-12-31') > '" + vo.getBegindate() + "'); ");
		}
		if (vo.getOldhealthrange() == null) {
		    vo.setOldhealthrange(UFDouble.ZERO_DBL);
		}
		if (!vo.getOldhealthrange().equals(vo.getHealthrange())
			&& !UFDouble.ZERO_DBL.equals(vo.getHealthrange())) {
		    strSQL.append(" UPDATE heal "
			    + " SET  enddate='"
			    + vo.getBegindate().getDateBefore(1).asEnd().toStdString()
			    + "', lastflag='N', modifier='"
			    + InvocationInfoProxy.getInstance().getUserId()
			    + "', modifiedtime=CONVERT([nchar](19),getdate(),(20)),ts=CONVERT([nchar](19),getdate(),(20)) "
			    + " FROM "
			    + PsndocDefTableUtil.getPsnHealthTablename()
			    + " heal "
			    + " INNER JOIN bd_psndoc psn ON heal.pk_psndoc = psn.pk_psndoc AND (heal.glbdef1 = psn.name OR heal.glbdef3 = psn.id) "
			    + " WHERE heal.pk_psndoc='" + vo.getPk_psndoc() + "' AND heal.begindate < '"
			    + vo.getEnddate() + "' AND isnull(heal.enddate, '9999-12-31') > '" + vo.getBegindate()
			    + "'; ");
		}
	    }
	}

	strSQL.append(" DELETE FROM " + PsndocDefTableUtil.getPsnLaborTablename()
		+ " WHERE  isnull(enddate, '9999-12-31') < begindate; ");

	strSQL.append(" DELETE FROM " + PsndocDefTableUtil.getPsnHealthTablename()
		+ " WHERE isnull(enddate, '9999-12-31') < begindate; ");
	return strSQL;
    }

    private List<String> findPersonList(String pk_org, String cYear, String cPeriod) throws BusinessException {
	String strSQL = "";
	strSQL += " SELECT distinct b.pk_psndoc ";
	strSQL += " FROM bd_psndoc b ";
	strSQL += " INNER JOIN hi_psnorg e ON b.pk_psndoc = e.pk_psndoc ";
	strSQL += " INNER JOIN ( ";
	strSQL += " 	SELECT MAX(orgrelaid) orgrelaid ";
	strSQL += " 		,pk_psndoc ";
	strSQL += " 	FROM hi_psnorg ";
	strSQL += " 	WHERE (indocflag = 'Y') ";
	strSQL += " 	GROUP BY pk_psndoc ";
	strSQL += " 	)  tmp ON e.pk_psndoc = tmp.pk_psndoc ";
	strSQL += " 	AND e.orgrelaid = tmp.orgrelaid ";
	strSQL += " INNER JOIN hi_psnjob a ON a.pk_psnorg = e.pk_psnorg ";
	strSQL += " 	AND a.lastflag = 'Y' ";
	strSQL += " 	AND a.ismainjob = 'Y' ";
	strSQL += " INNER JOIN org_admin_enable o ON o.pk_adminorg = a.pk_org ";
	// strSQL +=
	// " INNER JOIN om_jobrank jr ON a.jobglbdef1 = jr.pk_jobrank ";
	strSQL += " WHERE (e.indocflag = 'Y') ";
	strSQL += " 	AND (e.psntype = 0) ";
	strSQL += " 	AND (e.endflag = 'N') ";
	strSQL += " 	AND (a.endflag = 'N') ";
	strSQL += " 	AND (a.pk_hrorg = '" + pk_org + "') ";
	List psnlist = (List) this.getBaseDao().executeQuery(strSQL, new ColumnListProcessor("pk_psndoc"));
	return psnlist;
    }

    private BaseDAO getBaseDao() {
	if (baseDao == null) {
	    baseDao = new BaseDAO();
	}
	return baseDao;
    }

    @Override
    public void dismissPsnNHI(String pk_org, String pk_psndoc, UFLiteralDate enddate) throws BusinessException {
	if (null == enddate) {
	    return;
	}
	String strSQL = "UPDATE " + PsndocDefTableUtil.getPsnLaborTablename() + " SET enddate='" + enddate.toString()
		+ "' , insuranceform = " + DEL_FORM + " WHERE ISNULL(enddate, '9999-12-31') > '" + enddate.toString()
		+ "' AND dr=0 AND pk_psndoc = '" + pk_psndoc + "'";
	this.getBaseDao().executeUpdate(strSQL);

	strSQL = "UPDATE " + PsndocDefTableUtil.getPsnLaborTablename() + " SET glbdef15='" + enddate.toString()
		+ "' WHERE ISNULL(glbdef15, '9999-12-31') > '" + enddate.toString() + "' AND dr=0 AND pk_psndoc = '"
		+ pk_psndoc + "'" + " and lastflag = 'Y'";
	this.getBaseDao().executeUpdate(strSQL);

	// TODO: �a������������λ���،�߉݋
	// ssx modified on 2018-03-19
	// for changes of psn leave
	// �x�Ԅ��˱��r�A�u�˱�ԭ��e�飺2.�D��

	// Ares.Tank mod on 2018-9-15 22:55:16
	// ���߼�:Ͷ����B���˱��������˱�ԭ��e���˱��������˱�ԭ���f���e���x��--Jessie

	// �˱��f���e�飺1.�x
	Collection defvos = this.getBaseDao().retrieveByClause(DefdocVO.class,
		"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI005')");
	String glbdef18 = (defvos == null || defvos.size() == 0) ? "~" : ((DefdocVO) defvos.toArray()[0])
		.getPk_defdoc();
	defvos = this.getBaseDao().retrieveByClause(DefdocVO.class,
		"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI006')");
	String glbdef19 = (defvos == null || defvos.size() == 0) ? "~" : ((DefdocVO) defvos.toArray()[0])
		.getPk_defdoc();
	// Ares.Tank
	strSQL = "UPDATE " + PsndocDefTableUtil.getPsnHealthTablename() + " SET insuranceform = " + DEL_FORM
		+ ", enddate='" + enddate.toString() + "', glbdef18='" + glbdef18 + "', glbdef19='" + glbdef19
		+ "' WHERE ISNULL(enddate, '9999-12-31') > '" + enddate.toString() + "' AND dr=0 AND pk_psndoc = '"
		+ pk_psndoc + "'";
	this.getBaseDao().executeUpdate(strSQL);

    }

    @Override
    public boolean isExistsGroupInsCalculateResults(String pk_psndoc, String pk_psndoc_sub) throws BusinessException {
	String strSQL = "select * from "
		+ "hi_psndoc_groupinsrecord where pk_psndoc = '"
		+ pk_psndoc
		+ "' and begindate <= (select isnull(enddate, '9999-12-31') from hi_psndoc_groupinsrecord where pk_psndoc =  hi_psndoc_groupinsrecord.pk_psndoc and pk_psndoc_sub = '"
		+ pk_psndoc_sub
		+ "') and enddate >= (select begindate from hi_psndoc_groupinsrecord where pk_psndoc =  hi_psndoc_groupinsrecord"
		+ ".pk_psndoc and pk_psndoc_sub = '" + pk_psndoc_sub + "')";
	IRowSet rowSet = new DataAccessUtils().query(strSQL);

	return rowSet.size() > 0;
    }

    private void checkExistDateValid(String[] pk_psndocs, UFDate effectivedate) throws BusinessException {
	IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
	String strSQL = "";

	for (String pk_psndoc : pk_psndocs) {
	    if (StringUtils.isEmpty(strSQL)) {
		strSQL = "'" + pk_psndoc + "'";
	    } else {
		strSQL += ", '" + pk_psndoc + "'";
	    }
	}

	strSQL = "select pk_psndoc from " + PsndocDefTableUtil.getPsnLaborTablename() + " where pk_psndoc in ("
		+ strSQL + ") and begindate >= '" + effectivedate.toDate().toString() + "'";
	String existsPsn = (String) this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());
	if (!StringUtils.isEmpty(existsPsn)) {
	    PsndocVO[] vos = psnQry.queryPsndocByPks(new String[] { existsPsn });
	    throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
		    "068J61035-0029", null, new String[] { vos[0].getCode() })/*
									       * �T��
									       * [
									       * {
									       * 0
									       * }
									       * ]
									       * �Ąڱ�������Ϣ���Ѵ�����Ч����֮�����Ч�Y��
									       * ��
									       * �o���M��ͬ�����I
									       * ��
									       */);
	}
	strSQL = strSQL.replace(PsndocDefTableUtil.getPsnLaborTablename(), PsndocDefTableUtil.getPsnHealthTablename());
	existsPsn = (String) this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());
	if (!StringUtils.isEmpty(existsPsn)) {
	    PsndocVO[] vos = psnQry.queryPsndocByPks(new String[] { existsPsn });
	    throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
		    "068J61035-0030", null, new String[] { vos[0].getCode() })/*
									       * �T��
									       * [
									       * {
									       * 0
									       * }
									       * ]
									       * �Ľ�����Ϣ���Ѵ�����Ч����֮�����Ч�Y��
									       * ��
									       * �o���M��ͬ�����I
									       * ��
									       */);
	}
    }

    private UFDate checkPeriodValid(String[] pk_psndocs, UFDate cBaseDate, String avgmoncount, UFDate effectivedate)
	    throws BusinessException {
	// У�н�Y���g��Ч��
	UFDate startdate = calendarclac(cBaseDate, avgmoncount);
	if (cBaseDate.after(effectivedate)) {
	    throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
		    "068J61035-0028"));
	} else {
	    for (String pk_psndoc : pk_psndocs) {
		Collection<PsnjobVO> psnjobs = this.getBaseDao().retrieveByClause(PsnjobVO.class,
			"pk_psndoc='" + pk_psndoc + "'");
		if (psnjobs == null || psnjobs.size() != 1) {
		    throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
			    "twhr_personalmgt", "068J61035-0028"));
		} else {
		    for (PsnjobVO psnjob : psnjobs) {
			if (null == psnjob.getEnddutydate()) {
			    UFDate begindate = new UFDate(psnjob.getIndutydate().toDate());

			    if (begindate.after(startdate)) {
				startdate = begindate;
			    }
			}

		    }
		}

	    }
	}
	return startdate;

    }

    private UFDate calendarclac(UFDate cBaseDate, String avgmoncount) {
	UFDate startdate = null;
	try {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    Date dt = sdf.parse(cBaseDate.toString());

	    Calendar rightNow = Calendar.getInstance();
	    rightNow.setTime(dt);// ʹ�ø����� Date ���ô� Calendar ��ʱ�䡣
	    Integer avgmonday = Integer.parseInt(avgmoncount);
	    rightNow.add(Calendar.MONTH, (0 - avgmonday));// ���ڼ�avgmonday����
	    Date dt1 = rightNow.getTime();
	    startdate = new UFDate(dt1);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	return startdate;
    }

    private void dealHealInfoset(UFDate effectivedate, String pk_psndoc, UFDouble avgSalary, List<String> updateSQLs,
	    List<PsndocDefVO> subInfoLines) throws BusinessException, DAOException {
	String strSQL;
	strSQL = "select * from " + PsndocDefTableUtil.getPsnHealthTablename() + " where pk_psndoc='" + pk_psndoc
		+ "' and dr=0 and begindate <= '" + effectivedate.toString()
		+ "' and isnull(enddate, '9999-12-31') >='" + effectivedate.toString() + "' and glbdef14='Y'";
	List<Map> healResult = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());

	if (healResult != null && healResult.size() > 0) {
	    List<PsndocDefVO> newSubLines = new ArrayList<PsndocDefVO>();
	    for (Map healset : healResult) {
		UFDouble originHealLevel = healset.get("glbdef16") == null ? UFDouble.ZERO_DBL : new UFDouble(
			String.valueOf(healset.get("glbdef16")));
		RangeLineVO newRangeLine = findRangeLine(rangeTables, RangeTableTypeEnum.NHI_RANGETABLE.toIntValue(),
			avgSalary);
		UFDouble newHealLevel = newRangeLine.getRangeupper().equals(UFDouble.ZERO_DBL) ? newRangeLine
			.getRangelower().sub(1) : newRangeLine.getRangeupper();
		if (!originHealLevel.equals(newHealLevel)) {
		    // ����ԭ�н�����
		    updateSQLs.add("update " + PsndocDefTableUtil.getPsnHealthTablename() + " set enddate = '"
			    + effectivedate.getDateBefore(1).toLocalString()
			    + "', lastflag = 'N' where pk_psndoc_sub='" + (String) healset.get("pk_psndoc_sub") + "'");
		    updateSQLs.add("update " + PsndocDefTableUtil.getPsnHealthTablename()
			    + " set recordnum=isnull(recordnum,0)+1 where pk_psndoc = '" + pk_psndoc + "'");
		    // �����µĽ�����
		    PsndocDefVO newVO = getNewSubInfoLineByOrigin(healset, effectivedate, avgSalary, newHealLevel, null);
		    newSubLines.add(newVO);
		}
	    }
	    int rowno = newSubLines.size() - 1;
	    for (PsndocDefVO vo : newSubLines) {
		vo.setRecordnum(rowno--);
	    }
	    subInfoLines.addAll(newSubLines);
	}
    }

    private List<PsndocDefVO> dealLaborInfoset(UFDate effectivedate, String pk_psndoc, UFDouble avgSalary,
	    List<String> updateSQLs) throws BusinessException, DAOException {
	String strSQL = "select * from " + PsndocDefTableUtil.getPsnLaborTablename() + " where pk_psndoc = '"
		+ pk_psndoc + "' and dr=0 and begindate <= '" + effectivedate.toString()
		+ "' and isnull(enddate, '9999-12-31') >='" + effectivedate.toString()
		+ "' and (glbdef10 = 'Y' or glbdef11 = 'Y')";
	Map<String, Object> laborResult = (Map<String, Object>) this.getBaseDao().executeQuery(strSQL,
		new MapProcessor());

	List<PsndocDefVO> subInfoLines = new ArrayList<PsndocDefVO>();

	if (laborResult != null && !laborResult.isEmpty()) {
	    UFDouble originLaborLevel = laborResult.get("glbdef4") == null ? UFDouble.ZERO_DBL : new UFDouble(
		    String.valueOf(laborResult.get("glbdef4")));
	    UFDouble originRetireLevel = laborResult.get("glbdef7") == null ? UFDouble.ZERO_DBL : new UFDouble(
		    String.valueOf(laborResult.get("glbdef7")));
	    RangeLineVO newRangeLine = findRangeLine(rangeTables, RangeTableTypeEnum.LABOR_RANGETABLE.toIntValue(),
		    avgSalary);
	    UFDouble newLaborLevel = newRangeLine.getRangeupper().equals(UFDouble.ZERO_DBL) ? newRangeLine
		    .getRangelower().sub(1) : newRangeLine.getRangeupper();
	    newRangeLine = findRangeLine(rangeTables, RangeTableTypeEnum.RETIRE_RANGETABLE.toIntValue(), avgSalary);
	    UFDouble newRetireLevel = newRangeLine.getRangeupper().equals(UFDouble.ZERO_DBL) ? newRangeLine
		    .getRangelower().sub(1) : newRangeLine.getRangeupper();
	    if (!originLaborLevel.equals(newLaborLevel) || !originRetireLevel.equals(newRetireLevel)) {
		// ����ԭ���ͱ�������
		updateSQLs.add("update " + PsndocDefTableUtil.getPsnLaborTablename() + " set enddate = '"
			+ effectivedate.getDateBefore(1).toLocalString() + "', glbdef15='"
			+ effectivedate.getDateBefore(1).toLocalString() + "', lastflag = 'N' where pk_psndoc_sub='"
			+ (String) laborResult.get("pk_psndoc_sub") + "'");
		updateSQLs.add("update " + PsndocDefTableUtil.getPsnLaborTablename()
			+ " set recordnum=isnull(recordnum,0)+1 where pk_psndoc = '" + pk_psndoc + "'");
		// �����µ��ͱ�������
		subInfoLines.add(getNewSubInfoLineByOrigin(laborResult, effectivedate, avgSalary, newLaborLevel,
			newRetireLevel));
	    }
	}
	return subInfoLines;
    }

    private int getAvgSalary(String pk_wa_class, String startdate, String cbaseDate, String pk_psndoc,
	    Map<String, List<String>> periodItem, List<String> waperioddates) throws DAOException {
	// ��ȡ��һ������ְ����
	List<PsnJobVO> psnjobs = (List<PsnJobVO>) this.getBaseDao().retrieveByClause(PsnJobVO.class,
		"pk_psndoc='" + pk_psndoc + "' order by begindate");
	UFDate begindate = new UFDate(psnjobs.get(0).getBegindate().toDate());

	ComdateUtils comutils = new ComdateUtils();
	String strSQL;
	String strTotelSQL = "";
	UFDouble sumRst = UFDouble.ZERO_DBL;
	if (periodItem.size() > 0) {
	    for (Entry<String, List<String>> entry : periodItem.entrySet()) {
		String keys = "";
		if (entry.getValue() != null && entry.getValue().size() > 0) {
		    for (String key : entry.getValue()) {
			if (StringUtils.isEmpty(keys)) {
			    keys = key;
			} else {
			    keys += "+" + key;
			}
		    }
		}

		strSQL = "select " + keys + " nhisalary from wa_data " + " where cyear||'-'||cperiod = '"
			+ entry.getKey() + "' and pk_psndoc = '" + pk_psndoc + "' and pk_wa_class = '" + pk_wa_class
			+ "' and dr=0 ";

		if (StringUtils.isEmpty(strTotelSQL)) {
		    strTotelSQL = strSQL;
		} else {
		    strTotelSQL += " UNION ALL " + strSQL;
		}
	    }
	    Object rstObj = this.getBaseDao().executeQuery(
		    "select sum(nhisalary) nhisalary from (" + strTotelSQL + ") A", new ColumnProcessor());
	    if (rstObj != null) {
		sumRst = new UFDouble(String.valueOf(rstObj));
	    } else {
		Logger.error("SKIP RENEW: psn group insurance salary sum is null: " + pk_psndoc);
		return 0;
	    }

	}
	// ƽ��н��
	/*
	 * UFDouble avgSalary = new UFDouble(Math.round(sumRst.div(
	 * periodItem.size()).toDouble()));
	 */
	int avgSalarys = 0;
	for (String waperioddate : waperioddates) {
	    UFDate wabegindate = new UFDate(waperioddate.split(":")[0]);
	    UFDate waenddate = new UFDate(waperioddate.split(":")[1]);
	    if ((wabegindate.before(new UFDate(startdate)) || wabegindate.isSameDate(new UFDate(startdate)))
		    && (waenddate.after(new UFDate(cbaseDate)) || waenddate.isSameDate(new UFDate(cbaseDate)))) {
		int shortdate = comutils.daysOfTwo(startdate, cbaseDate);
		// ��ʼ���ں���ְ����ͬһ����
		int longdate = 0;

		if (new UFDate(startdate).getYear() == begindate.getYear()
			&& new UFDate(startdate).getMonth() == begindate.getMonth()) {
		    longdate = comutils.daysOfTwo(begindate.toString(), waenddate.toString());
		} else {
		    longdate = comutils.daysOfTwo(wabegindate.toString(), waenddate.toString());
		}
		int avgSalary = (int) Math.round(sumRst.div(longdate).multiply(shortdate).doubleValue());
		avgSalarys += avgSalary;
	    }
	}

	return avgSalarys;
    }

    private void getValidWaItemByPeriod(String pk_wa_class, String begindate, String endate,
	    Map<String, List<String>> periodItem) throws DAOException {
	UFDate startdate = new UFDate(begindate);
	UFDate basedate = new UFDate(endate);
	String startperiod = startdate.getYear() + "-" + begindate.split("-")[1];
	String endperiod = basedate.getYear() + "-" + endate.split("-")[1];

	String strSQL = "select cyear||'-'||cperiod cyearperiod, itemkey from wa_classitem where pk_wa_item in ("
		+ " select itm.pk_wa_item from wa_item itm"
		+ " inner join twhr_waitem_30 tw on itm.pk_wa_item = tw.pk_wa_item"
		+ " where tw.isnhiitem_30 = 'Y' and itm.avgcalcsalflag = 'Y')and pk_wa_class = '" + pk_wa_class
		+ "' and cyear||'-'||cperiod >= '" + startperiod + "' and cyear||'-'||cperiod <= '" + endperiod + "' ";
	List<Map> rst = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());

	if (rst != null && rst.size() > 0) {
	    String cyearperiod = "";
	    for (Map rstMap : rst) {
		cyearperiod = (String) rstMap.get("cyearperiod");
		if (!periodItem.containsKey(cyearperiod)) {
		    periodItem.put(cyearperiod, new ArrayList<String>());
		}

		periodItem.get(cyearperiod).add((String) rstMap.get("itemkey"));
	    }
	}
    }

    private void getValidWaItemByPeriod4GroupIns(String pk_wa_class, String pk_org, String begindate, String endate,
	    Map<String, List<String>> periodItem) throws DAOException {
	UFDate startdate = new UFDate(begindate);
	UFDate basedate = new UFDate(endate);
	String startperiod = startdate.getYear() + "-" + begindate.split("-")[1];
	String endperiod = basedate.getYear() + "-" + endate.split("-")[1];
	String strSQL = "select cyear||'-'||cperiod cyearperiod, itemkey from wa_classitem where pk_wa_item in ("
		+ " select itm.pk_wa_item from wa_item itm"
		+ " inner join twhr_basedoc tw on itm.pk_wa_item = tw.waitemvalue and tw.code like 'GRPSUM%'"
		+ " where tw.pk_org = '" + pk_org + "') and pk_wa_class = '" + pk_wa_class
		+ "' and cyear||'-'||cperiod >= '" + startperiod + "' and cyear||'-'||cperiod <= '" + endperiod + "' ";
	List<Map> rst = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());

	if (rst != null && rst.size() > 0) {
	    String cyearperiod = "";
	    for (Map rstMap : rst) {
		cyearperiod = (String) rstMap.get("cyearperiod");
		if (!periodItem.containsKey(cyearperiod)) {
		    periodItem.put(cyearperiod, new ArrayList<String>());
		}

		periodItem.get(cyearperiod).add((String) rstMap.get("itemkey"));
	    }
	}
    }

    private PsndocDefVO getNewSubInfoLineByOrigin(Map<String, Object> originValues, UFDate effectivedate,
	    UFDouble avgSalary, UFDouble newMainLevel, UFDouble newRetireLevel) throws BusinessException {
	if (newRetireLevel != null) {
	    PsndocDefVO newLaborVO = PsndocDefUtil.getPsnLaborVO();
	    newLaborVO.setPk_psndoc((String) originValues.get("pk_psndoc"));
	    newLaborVO.setLastflag(UFBoolean.TRUE);
	    newLaborVO.setRecordnum(0);
	    newLaborVO.setBegindate(new UFLiteralDate(effectivedate.asBegin().toString()));
	    newLaborVO.setEnddate(originValues.get("enddate") == null ? null : new UFLiteralDate(originValues.get(
		    "enddate").toString()));
	    newLaborVO.setAttributeValue("glbdef1", originValues.get("glbdef1"));
	    newLaborVO.setAttributeValue("glbdef2", avgSalary);
	    newLaborVO.setAttributeValue("glbdef3", UFDouble.ZERO_DBL);
	    newLaborVO.setAttributeValue("glbdef4", newMainLevel);
	    newLaborVO.setAttributeValue("glbdef5", originValues.get("glbdef5"));
	    newLaborVO.setAttributeValue("glbdef6", UFBoolean.FALSE);
	    newLaborVO.setAttributeValue("glbdef7", newRetireLevel);
	    newLaborVO.setAttributeValue("glbdef8", originValues.get("glbdef8"));
	    newLaborVO.setAttributeValue("glbdef9", originValues.get("glbdef9"));
	    newLaborVO.setAttributeValue("glbdef10", originValues.get("glbdef10"));
	    newLaborVO.setAttributeValue("glbdef11", originValues.get("glbdef11"));
	    newLaborVO.setAttributeValue("glbdef12", originValues.get("glbdef12"));
	    newLaborVO.setAttributeValue("glbdef13", originValues.get("glbdef13"));
	    // �����_ʼ����̎��
	    UFDate originStartDate = originValues.get("glbdef14") == null ? null : new UFDate(
		    (String) originValues.get("glbdef14"));
	    UFDate originEndDate = originValues.get("glbdef15") == null ? null : new UFDate(
		    (String) originValues.get("glbdef15"));
	    if (originStartDate == null || originStartDate.after(effectivedate)
		    || (originEndDate != null && originEndDate.before(effectivedate))) {
		newLaborVO.setAttributeValue("glbdef14", originStartDate);
	    } else {
		newLaborVO.setAttributeValue("glbdef14", effectivedate.asBegin());
	    }
	    // ���˽Y������
	    newLaborVO.setAttributeValue("glbdef15", originValues.get("glbdef15"));
	    //
	    // Ares.Tank �ͱ�������֯��Ͷ����̬������
	    newLaborVO.setAttributeValue("legalpersonorg", pkLegalOrg);
	    // ������˵,�˴�Ӧ����ö����setֵ��,Ϊʲô��������?
	    // 1.��Ϊû��ö���� 2.hrtaovertimereg.bmf���Ԫ�������е�ë�� -->3.���ֱ����ĸ��ʺϱ���
	    // ������--�ӱ�,ͬ���ͱ�����--��н����

	    newLaborVO.setAttributeValue("insuranceform", ADJ_FORM);
	    return newLaborVO;
	} else {
	    PsndocDefVO newHealVO = PsndocDefUtil.getPsnHealthVO();
	    newHealVO.setPk_psndoc((String) originValues.get("pk_psndoc"));
	    newHealVO.setLastflag(UFBoolean.TRUE);
	    newHealVO.setBegindate(new UFLiteralDate(effectivedate.toString()));
	    newHealVO.setEnddate(originValues.get("enddate") == null ? null : new UFLiteralDate(originValues.get(
		    "enddate").toString()));
	    newHealVO.setAttributeValue("glbdef1", originValues.get("glbdef1"));
	    newHealVO.setAttributeValue("glbdef2", originValues.get("glbdef2"));
	    newHealVO.setAttributeValue("glbdef3", originValues.get("glbdef3"));
	    newHealVO.setAttributeValue("glbdef4", originValues.get("glbdef4"));
	    newHealVO.setAttributeValue("glbdef5", originValues.get("glbdef5"));
	    newHealVO.setAttributeValue("glbdef6", avgSalary);
	    // glbdef16 ��������
	    newHealVO.setAttributeValue("glbdef16", newMainLevel);
	    newHealVO.setAttributeValue("glbdef7", UFDouble.ZERO_DBL);
	    newHealVO.setAttributeValue("glbdef8", originValues.get("glbdef8"));
	    newHealVO.setAttributeValue("glbdef9", originValues.get("glbdef9"));
	    newHealVO.setAttributeValue("glbdef10", originValues.get("glbdef10"));
	    newHealVO.setAttributeValue("glbdef11", originValues.get("glbdef11"));
	    newHealVO.setAttributeValue("glbdef12", originValues.get("glbdef12"));
	    newHealVO.setAttributeValue("glbdef13", UFBoolean.FALSE);
	    newHealVO.setAttributeValue("glbdef14", originValues.get("glbdef14"));
	    newHealVO.setAttributeValue("glbdef15", originValues.get("glbdef15"));
	    // Ares.Tank �ͱ�������֯��Ͷ����̬������
	    newHealVO.setAttributeValue("legalpersonorg", pkLegalOrg);
	    // ������˵,�˴�Ӧ����ö����setֵ��,Ϊʲô��������?
	    // 1.��Ϊû��ö���� 2.hrtaovertimereg.bmf���Ԫ�������е�ë�� -->3.���ֱ����ĸ��ʺϱ���
	    // ������--�ӱ�,ͬ���ͱ�����--��н����

	    newHealVO.setAttributeValue("insuranceform", ADJ_FORM);
	    return newHealVO;
	}
    }

    @Override
    public void generatePsnNHI(String pk_org, String[] pk_psndocs) throws BusinessException {
	// ��ԃ���μӱ���֯�ķ�����֯ Ares.Tank
	String[] pkLegalOrgs = { pk_org };
	pkLegalOrg = LegalOrgUtils.getLegalOrgByOrgs(pkLegalOrgs).get(pk_org);
	// �������
	if (StringUtils.isEmpty(pk_org)) {
	    throw new BusinessException("�M�����ܞ��");
	}

	if (pk_psndocs == null || pk_psndocs.length == 0) {
	    throw new BusinessException("δ�xȡ���Ʉڽ����O���ĆT��");
	}
	// �����Ա�Ƿ����ͽ����趨
	SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
	for (String pk_psndoc : pk_psndocs) {
	    // Ares.Tank 2018-9-14 15:59:03
	    // ��Ŀǰ����Ͷ���o�Ͷ���͑B(���˱�)�c���ζ�н�ķ��˹�˾��ͬ���t��ʹ�ô˰��oͬ������
	    PsndocDefVO[] vos = service.queryByCondition(PsndocDefUtil.getPsnLaborVO().getClass(), " pk_psndoc='"
		    + pk_psndoc + "' ");
	    if (vos != null && vos.length > 0) {
		for (PsndocDefVO vo : vos) {
		    if (vo == null || !vo.getLastflag().booleanValue()) {
			continue;// Ѱ�����µ�Ͷ����¼
		    }
		    if (vo.getAttributeValue("insuranceform") != null
			    && Integer.parseInt(vo.getAttributeValue("insuranceform").toString()) != DEL_FORM
			    && vo.getAttributeValue("legalpersonorg") != null
			    && !vo.getAttributeValue("legalpersonorg").equals(pkLegalOrg)) {
			;
		    } else {
			throw new BusinessException("�Ѵ�����ͬ���˽M���Ąڱ�Ͷ���O��");
		    }
		}

	    }

	    vos = service
		    .queryByCondition(PsndocDefUtil.getPsnHealthVO().getClass(), " pk_psndoc='" + pk_psndoc + "' ");
	    if (vos != null && vos.length > 0) {
		for (PsndocDefVO vo : vos) {
		    if (vo == null || !vo.getLastflag().booleanValue()) {
			continue;// Ѱ�����µ�Ͷ����¼
		    }
		    if (vo.getAttributeValue("insuranceform") != null
			    && Integer.parseInt(vo.getAttributeValue("insuranceform").toString()) != DEL_FORM
			    && vo.getAttributeValue("legalpersonorg") != null
			    && !vo.getAttributeValue("legalpersonorg").equals(pkLegalOrg)) {
			;
		    } else {
			throw new BusinessException("�Ѵ�����ͬ���˽M����Ͷ�������O��");
		    }
		}

	    }
	}
	// ȡ��Ա��ְ����
	IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
	// Psndoc
	PsndocVO[] psndocVOs = psnQry.queryPsndocByPks(pk_psndocs);

	for (String pk_psndoc : pk_psndocs) {
	    PsnjobVO[] psnjobVos = psnQry.getOldPsnjobVO(pk_psndoc);
	    UFLiteralDate earlist = new UFLiteralDate("9999-12-31");
	    for (PsnjobVO psnvo : psnjobVos) {
		if (psnvo.getIndutydate().before(earlist) && psnvo.getEnddutydate() == null) {
		    earlist = psnvo.getIndutydate();
		}
	    }
	    String cyear = String.valueOf(earlist.getYear()); // ��ְ����������
	    String cperiod = String.valueOf(earlist.getMonth()); // ��ְ���������ڼ�
	    // ȡ�����
	    this.getRangeTables(pk_org, cyear, cperiod);

	    // ���M�����gȡ���{н�c�ڽ����O�������Y��
	    INhiCalcGenerateSrv srv = NCLocator.getInstance().lookup(INhiCalcGenerateSrv.class);
	    NhiCalcVO[] nhiFinalVOs = srv.getAdjustNHIData(pk_psndoc, pk_org, earlist);

	    // �½��ڽ����Y�� Ares.Tank
	    PsndocDefVO[] savedVOs = getNewVOs(nhiFinalVOs, pk_org, cyear, cperiod, true);

	    PsndocVO psnVO = null;
	    for (PsndocVO psnvo : psndocVOs) {
		if (psnvo.getPk_psndoc().equals(pk_psndoc)) {
		    psnVO = psnvo;
		}
	    }

	    if (savedVOs != null && savedVOs.length > 0) {
		for (PsndocDefVO vo : savedVOs) {
		    if (vo.getClass().equals(PsndocDefUtil.getPsnLaborVO().getClass())) {
			// �ͱ�
			// �Ƿ��ͱ�Ͷ�� glbdef10
			vo.setAttributeValue("glbdef10", UFBoolean.TRUE);
			// �ͱ����� glbdef1
			vo.setAttributeValue("glbdef1", SysInitQuery.getParaString(pk_org, "TWHR03"));
			// �Ƿ�����Ͷ�� glbdef11
			vo.setAttributeValue("glbdef11", UFBoolean.TRUE);
			// ssx added for �ڽ������
			// �������݄e��հס����U���քe��1.�������U
			DefdocVO defvo = getHIPsnTypeDefVO((String) vo.getAttributeValue("glbdef1"));
			vo.setAttributeValue("glbdef16", null);
			vo.setAttributeValue("glbdef17", defvo != null ? defvo.getPk_defdoc() : null);
			//vo.setAttributeValue("lastflag", UFBoolean.TRUE);

		    } else if (vo.getClass().equals(PsndocDefUtil.getPsnHealthVO().getClass())) {
			// ����
			// Ͷ���˻�������� glbdef1
			vo.setAttributeValue("glbdef1", psnVO.getName());
			// ��ν glbdef2
			vo.setAttributeValue("glbdef2", "����");
			// ����֤���� glbdef3
			vo.setAttributeValue("glbdef3", psnVO.getId());
			// �������� glbdef4
			if(null != psnVO.getBirthdate()){
				vo.setAttributeValue("glbdef4", new UFDate(psnVO.getBirthdate().toDate()));
			}else{
				vo.setAttributeValue("glbdef4", null);
				//throw new BusinessException("�������ڲ���Ϊ��");
			}
			// �������� glbdef5
			vo.setAttributeValue("glbdef5", SysInitQuery.getParaString(pk_org, "TWHR05"));
			// �Ƿ�Ͷ�� glbdef14
			vo.setAttributeValue("glbdef14", UFBoolean.TRUE);
			// �Ƿ�����˰������ glbdef15
			vo.setAttributeValue("glbdef15", UFBoolean.TRUE);
			//vo.setAttributeValue("lastflag", UFBoolean.TRUE);
			// ssx added for �ڽ������
			// �����YӍ�ԄӼ��뽡���ӱ�ԭ���"����н"
			Collection defvos = this
				.getBaseDao()
				.retrieveByClause(DefdocVO.class,
					"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI004')");
			vo.setAttributeValue("glbdef17", (defvos == null || defvos.size() == 0) ? "~"
				: ((DefdocVO) defvos.toArray()[0]).getPk_defdoc());
		    }
		}
	    }

	    if (savedVOs != null && savedVOs.length > 0) {
		for (PsndocDefVO vo : savedVOs) {
		    vo.setBegindate(earlist);
		    vo.setEnddate(new UFLiteralDate("9999-12-31"));
		    vo.setRecordnum(0);
		    service.insert(vo);
		}
	    }
	}
    }

    Map<String, DefdocVO> defMap = new HashMap<String, DefdocVO>();

    private DefdocVO getHIPsnTypeDefVO(String pk_psntype) throws BusinessException {
	// ssx added for �ڽ������
	// ���U���݄e
	if (!defMap.containsKey(pk_psntype)) {
	    Collection defvos = this.getBaseDao().retrieveByClause(DefdocVO.class,
		    "code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI003')");

	    if (defvos != null && defvos.size() > 0) {
		defMap.put(pk_psntype, ((DefdocVO) defvos.toArray()[0]));
	    }
	}

	return defMap.get(pk_psntype);
    }

    @Override
    public void generateGroupIns(String pk_org, String[] pk_psndocs) throws BusinessException {
	// �������
	if (StringUtils.isEmpty(pk_org)) {
	    throw new BusinessException("�M�����ܞ��");
	}

	if (pk_psndocs == null || pk_psndocs.length == 0) {
	    throw new BusinessException("δ�xȡ���ɈF���O���ĆT��");
	}

	// �����Ա�Ƿ��ЈF���趨
	SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
	for (String pk_psndoc : pk_psndocs) {
	    PsndocDefVO[] vos = service.queryByCondition(PsndocDefUtil.getGroupInsuranceVO().getClass(), " pk_psndoc='"
		    + pk_psndoc + "' ");
	    if (vos != null && vos.length > 0) {
		throw new BusinessException("�Ѵ��ڈF��Ͷ���O��");
	    }
	}

	// ȡ�F���M�ʱ�
	IGroupinsuranceMaintain settingSrv = NCLocator.getInstance().lookup(IGroupinsuranceMaintain.class);
	GroupInsuranceSettingVO[] setting = settingSrv.queryOnDuty(pk_org);

	// ȡ��Ա��ְ����
	IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
	// Psndoc
	PsndocVO[] psndocVOs = psnQry.queryPsndocByPks(pk_psndocs);

	for (String pk_psndoc : pk_psndocs) {
	    PsnjobVO[] psnjobVos = psnQry.getOldPsnjobVO(pk_psndoc);
	    UFLiteralDate earlist = new UFLiteralDate("9999-12-31");
	    for (PsnjobVO psnvo : psnjobVos) {
		if (psnvo.getIndutydate().before(earlist) && psnvo.getEnddutydate() == null) {
		    earlist = psnvo.getIndutydate();
		}
	    }

	    PsndocVO psnVO = null;
	    for (PsndocVO psnvo : psndocVOs) {
		if (psnvo.getPk_psndoc().equals(pk_psndoc)) {
		    psnVO = psnvo;
		}
	    }

	    UFDouble grpSum = getGroupInsWadocBaseSalaryByPsnDate(pk_org, pk_psndoc, earlist);

	    // ��н����
	    UFDouble psnSum = grpSum.div(1000).setScale(0, UFDouble.ROUND_HALF_UP).multiply(1000);

	    // �½��F���Y��
	    PsndocDefVO[] savedVOs = getNewGroupInsVO(pk_psndoc, pk_org, psnSum, setting, psnVO);

	    if (savedVOs != null && savedVOs.length > 0) {
		for (PsndocDefVO vo : savedVOs) {
		    vo.setBegindate(earlist);
		    vo.setEnddate(null);
		    service.insert(vo);
		}
	    }
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    public UFDouble getGroupInsWadocBaseSalaryByPsnDate(String pk_org, String pk_psndoc, UFLiteralDate salaryDate)
	    throws BusinessException {
	// �z��ڽ��������Ƿ��ЈF����н�����ӿ��Ŀ
	List<BaseDocVO> grpWaitems = (List<BaseDocVO>) this.getBaseDao().retrieveByClause(BaseDocVO.class,
		"code like 'GRPSUM%' and pk_org='" + pk_org + "'", "code");
	if (grpWaitems == null || grpWaitems.size() == 0) {
	    throw new BusinessException("Ո�څ����O�������Ӿ��a��GRPSUMnn��н�Y����");
	}

	String waitems = "";
	for (BaseDocVO bdVO : grpWaitems) {
	    if (StringUtils.isEmpty(waitems)) {
		waitems += "'" + bdVO.getRefvalue() + "'";
	    } else {
		waitems += ",'" + bdVO.getRefvalue() + "'";
	    }
	}

	String strSQL = "select sum(nmoney) grpsum from hi_psndoc_wadoc where pk_wa_item in (" + waitems
		+ ")  and pk_org= '" + pk_org + "' and pk_psndoc = '" + pk_psndoc + "' and '" + salaryDate.toString()
		+ "' between begindate and isnull(enddate, '9999-12-31') and waflag='Y' and lastflag='Y'";
	UFDouble grpSum = UFDouble.ZERO_DBL;
	if (null != this.getBaseDao().executeQuery(strSQL, new ColumnProcessor())) {
	    grpSum = new UFDouble(String.valueOf(this.getBaseDao().executeQuery(strSQL, new ColumnProcessor())));
	}
	return grpSum;
    }

    private PsndocDefVO[] getNewGroupInsVO(String pk_psndoc, String pk_org, UFDouble psnSum,
	    GroupInsuranceSettingVO[] setting, PsndocVO psndocVO) throws BusinessException {
	List<PsndocDefVO> psnNhiVOs = new ArrayList<PsndocDefVO>();
	int i = 0;
	for (GroupInsuranceSettingVO setvo : setting) {
	    PsndocDefVO newGrpInsVO = PsndocDefUtil.getGroupInsuranceVO();
	    newGrpInsVO.setPk_psndoc(pk_psndoc);
	    newGrpInsVO.setLastflag(UFBoolean.TRUE);
	    newGrpInsVO.setRecordnum(i++);
	    newGrpInsVO.setLastflag(i == setting.length ? UFBoolean.TRUE : UFBoolean.FALSE);
	    newGrpInsVO.setAttributeValue("glbdef1", psndocVO.getName());
	    newGrpInsVO.setAttributeValue("glbdef2", psndocVO.getId());
	    if (null != psndocVO.getBirthdate()) {
		newGrpInsVO.setAttributeValue("glbdef3", new UFDate(psndocVO.getBirthdate().toDate()));
	    }
	    newGrpInsVO.setAttributeValue("glbdef4", setvo.getCgrpinsrelid());
	    newGrpInsVO.setAttributeValue("glbdef5", setvo.getCgrpinsid());
	    newGrpInsVO.setAttributeValue("glbdef6", psnSum);
	    newGrpInsVO.setAttributeValue("glbdef7", "N");
	    psnNhiVOs.add(newGrpInsVO);
	}
	return psnNhiVOs.toArray(new PsndocDefVO[0]);
    }

    @Override
    public void dismissPsnGroupIns(String pk_org, String pk_psndoc, UFLiteralDate enddate) throws BusinessException {
	String strSQL = "UPDATE " + PsndocDefTableUtil.getGroupInsuranceTablename() + " SET enddate='"
		+ enddate.toString() + "' WHERE ISNULL(enddate, '9999-12-31') > '" + enddate.toString()
		+ "' AND dr=0 AND pk_psndoc = '" + pk_psndoc + "'";
	this.getBaseDao().executeUpdate(strSQL);
    }

    private void checkExistDateValid4GroupIns(String[] pk_psndocs, UFDate effectivedate) throws BusinessException {
	IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
	String strSQL = "";

	for (String pk_psndoc : pk_psndocs) {
	    if (StringUtils.isEmpty(strSQL)) {
		strSQL = "'" + pk_psndoc + "'";
	    } else {
		strSQL += ", '" + pk_psndoc + "'";
	    }
	}

	strSQL = "select pk_psndoc from " + PsndocDefTableUtil.getGroupInsuranceTablename() + " where pk_psndoc in ("
		+ strSQL + ") and begindate >= '" + effectivedate.toDate().toString() + "'";
	String existsPsn = (String) this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());
	if (!StringUtils.isEmpty(existsPsn)) {
	    PsndocVO[] vos = psnQry.queryPsndocByPks(new String[] { existsPsn });
	    throw new BusinessException("�T�� [" + vos[0].getCode() + "] �ĈF����Ϣ���Ѵ�����Ч����֮�����Ч�Y�ϣ��o���M��ͬ�����I��");
	}
    }

    private List<PsndocDefVO> dealGroupInsInfoset(UFDate effectivedate, String pk_psndoc, UFDouble avgSalary,
	    List<String> updateSQLs) throws BusinessException {
	String strSQL;
	List<PsndocDefVO> subInfoLines = new ArrayList<PsndocDefVO>();

	strSQL = "select * from " + PsndocDefTableUtil.getGroupInsuranceTablename() + " where pk_psndoc='" + pk_psndoc
		+ "' and dr=0 and begindate <= '" + effectivedate.toString()
		+ "' and isnull(enddate, '9999-12-31') >='" + effectivedate.toString() + "' and glbdef7='N'";
	List<Map> groupInsResult = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());

	if (groupInsResult != null && groupInsResult.size() > 0) {
	    List<PsndocDefVO> newSubLines = new ArrayList<PsndocDefVO>();
	    for (Map groupSet : groupInsResult) {
		UFDouble originSalaryBase = groupSet.get("glbdef6") == null ? UFDouble.ZERO_DBL : new UFDouble(
			String.valueOf(groupSet.get("glbdef6")));
		UFDouble newSalaryBase = avgSalary.div(1000).setScale(0, UFDouble.ROUND_HALF_UP).multiply(1000);
		if (!originSalaryBase.equals(newSalaryBase)) {
		    // ����ԭ�ЈF���O����
		    updateSQLs.add("update " + PsndocDefTableUtil.getGroupInsuranceTablename() + " set enddate = '"
			    + effectivedate.getDateBefore(1).toLocalString()
			    + "', lastflag = 'N' where pk_psndoc_sub='" + (String) groupSet.get("pk_psndoc_sub") + "'");
		    updateSQLs.add("update " + PsndocDefTableUtil.getGroupInsuranceTablename()
			    + " set recordnum=isnull(recordnum,0)+1 where pk_psndoc = '" + pk_psndoc + "'");
		    // �����µĈF���O����
		    PsndocDefVO newVO = getNewSubInfoLineByOrigin4GroupIns(groupSet, effectivedate, newSalaryBase);
		    newSubLines.add(newVO);
		}
	    }
	    int rowno = newSubLines.size() - 1;
	    for (PsndocDefVO vo : newSubLines) {
		vo.setRecordnum(rowno--);
	    }
	    subInfoLines.addAll(newSubLines);
	}
	return subInfoLines;
    }

    private PsndocDefVO getNewSubInfoLineByOrigin4GroupIns(Map<String, Object> originValues, UFDate effectivedate,
	    UFDouble avgSalary) throws BusinessException {
	PsndocDefVO newHealVO = PsndocDefUtil.getGroupInsuranceVO();
	newHealVO.setPk_psndoc((String) originValues.get("pk_psndoc"));
	newHealVO.setLastflag(UFBoolean.TRUE);
	newHealVO.setBegindate(new UFLiteralDate(effectivedate.toString()));
	newHealVO.setEnddate(originValues.get("enddate") == null ? null : new UFLiteralDate(originValues.get("enddate")
		.toString()));
	newHealVO.setAttributeValue("glbdef1", originValues.get("glbdef1"));
	newHealVO.setAttributeValue("glbdef2", originValues.get("glbdef2"));
	newHealVO.setAttributeValue("glbdef3", originValues.get("glbdef3"));
	newHealVO.setAttributeValue("glbdef4", originValues.get("glbdef4"));
	newHealVO.setAttributeValue("glbdef5", originValues.get("glbdef5"));
	newHealVO.setAttributeValue("glbdef6", avgSalary);
	newHealVO.setAttributeValue("glbdef7", originValues.get("glbdef7"));

	return newHealVO;

    }

    @SuppressWarnings("all")
    @Override
    public void renewRangeEx(String pk_org, String[] pk_psndocs, String[] pk_wa_class, UFDate cbaseDate,
	    String avgmoncount, UFDate effectivedate) throws BusinessException {
	// ��ԃ���μӱ���֯�ķ�����֯ Ares.Tank
	String[] pkLegalOrgs = { pk_org };
	pkLegalOrg = LegalOrgUtils.getLegalOrgByOrgs(pkLegalOrgs).get(pk_org);
	InSQLCreator insql = new InSQLCreator();
	String waclassInSQL = insql.getInSQL(pk_wa_class);
	// ����н�ʷ���ɸѡ������н����Ŀ�й�ѡ��ƽ��н�����н��н����
	List<Map<String, String>> walist = (List<Map<String, String>>) this.getBaseDao().executeQuery(
		"SELECT wa_item.itemkey as itemkey from WA_ITEM INNER JOIN TWHR_WAITEM_30 on "
			+ "WA_ITEM.PK_WA_ITEM= TWHR_WAITEM_30.PK_WA_ITEM where WA_ITEM.PK_WA_ITEM IN ("
			+ "select distinct PK_WA_ITEM from WA_CLASSITEM where PK_WA_CLASS in (" + waclassInSQL + "))"
			+ "and AVGCALCSALFLAG='Y' and TWHR_WAITEM_30.isnhiitem_30 = 'Y' ", new MapListProcessor());
	if (walist.size() == 0) {
	    throw new BusinessException("û���Ƿ�����ͱ�������н����");
	}
	String cumn = "";
	for (Map<String, String> waitem : walist) {
	    cumn += waitem.get("itemkey") + "+";
	}
	// ȡ�����˵�ƽ����н
	if (pk_psndocs != null && pk_psndocs.length > 0) {
	    for (String pk_psndoc : pk_psndocs) {
		// ȡ�����
		this.getRangeTables(pk_org, effectivedate);
		// У�н�Y���g��Ч��
		// avgmoncount, effectivedate);
		// �z���Ƿ������Ч�����������ʼ����
		checkExistDateValid(pk_psndocs, effectivedate);
		// �Ȼ�ȡ��׼���ڣ��ٸ��ݻ�׼���ں�ƽ��������ȡ��ʼ����
		// ��ȡ��׼������ǰ�Ƶ�����,���ص����ڼ�Ϊ��Ҫ����Ŀ�ʼ����
		UFDate basemonthdate = ComdateUtils.calendarclac(cbaseDate, avgmoncount);
		// �ж���Ա�Ƿ���н(�Ի�׼����Ϊ׼)
		// ��ȡ���Ա���ڴ�н�ʷ����µ�н���ڼ��wa_dataֵ
		List<Map<String, Object>> waperiodlist = (List<Map<String, Object>>) this.getBaseDao().executeQuery(
			"select WA_PERIOD.CSTARTDATE, WA_PERIOD.CENDDATE,(" + cumn.substring(0, cumn.length() - 1)
				+ ") as f_itemkey "
				+ "from wa_data,WA_WACLASS,WA_PERIOD where WA_DATA.PK_WA_CLASS=WA_WACLASS.PK_WA_CLASS "
				+ " and WA_WACLASS.PK_PERIODSCHEME=WA_PERIOD.PK_PERIODSCHEME "
				+ " and WA_DATA.CYEAR=WA_PERIOD.CYEAR " + " and WA_DATA.CPERIOD=WA_PERIOD.CPERIOD"
				+ " and WA_WACLASS.PK_WA_CLASS in (" + waclassInSQL + ")" + " and WA_DATA.PK_PSNDOC='"
				+ pk_psndoc + "';", new MapListProcessor());
		UFBoolean flag = ismonthsalary(pk_psndoc, cbaseDate);
		//UFDouble avgSalary = UFDouble.ZERO_DBL;
		 UFDouble salary = UFDouble.ZERO_DBL;
		if (flag.booleanValue()) {
		    // �ж����Ա�������Ƿ���������,�����Ƿ��������£�����㷽ʽ��һ��
		    if (sixmonth(pk_psndoc, pk_org).booleanValue()) {
		    	salary = getavgsalary(cbaseDate, basemonthdate, waperiodlist).multiply(30);
		    } else {
		    	salary = getavgsalary(cbaseDate, basemonthdate, waperiodlist).multiply(30);
		    }
		} else {
		    // ����н
			UFDouble avgSalary = getavgsalary(cbaseDate, basemonthdate, waperiodlist);
		    UFDouble actavgSalary = getnoavgsalary(pk_psndoc, cbaseDate, basemonthdate, waperiodlist);
		    /**
		     * �tʹ�ù������g�����Y�����Ք���ȡ����н�����30��ƽ�����Y��
		     * ����˹��Y��춹��Y���~�����H�����Ք���30����֮ƽ�����Y�İٷ�֮��ʮ�� �tʹ������Ӌ���֮���~���錍�H��ƽ�����Y
		     */
		    UFDouble avgmonsalary = avgSalary.multiply(30);
		    UFDouble actmontsalary = actavgSalary.multiply(30);
		   
		    if (avgmonsalary.doubleValue() - (actmontsalary.multiply(0.6).doubleValue()) > 0) {
		    	salary = actmontsalary.multiply(0.6);
		    } else {
		    	salary = actmontsalary;
		    }
		}
		if (salary == null) {
		    continue;
		}

		if (salary.equals(UFDouble.ZERO_DBL)) {
		    IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
		    PsndocVO[] vos = psnQry.queryPsndocByPks(new String[] { pk_psndoc });
		    throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
			    "twhr_personalmgt", "068J61035-0031", null, new String[] { vos[0].getCode() })
		    /*
		     * * �T�� [ { 0 } ] ��ָ�������g�����ț]��н�Y�Y�� �� �o��Ӌ��ƽ��н�Y ��
		     */
		    );
		}

		List<String> updateSQLs = new ArrayList<String>();

		// ̎���ͱ�����
		List<PsndocDefVO> subInfoLines = dealLaborInfoset(effectivedate, pk_psndoc, salary, updateSQLs);

		// ̎������
		dealHealInfoset(effectivedate, pk_psndoc, salary, updateSQLs, subInfoLines);

		// �����Ѵ���ӛ�
		if (updateSQLs.size() > 0) {
		    for (String sql : updateSQLs) {
			this.getBaseDao().executeUpdate(sql);
		    }
		}

		// �،��Ӽ�
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
		if (subInfoLines != null && subInfoLines.size() > 0) {
		    for (PsndocDefVO vo : subInfoLines) {
			service.insert(vo);
		    }
		}
	    }
	}

    }

    @SuppressWarnings("all")
    private List<String> getnewdates(List<String> datelists, List<String> waperioddates) {
	List<String> newdates = new ArrayList<String>();
	for (String datelist : datelists) {
	    for (String waperioddate : waperioddates) {
		String insertdates = new ComdateUtils().getAlphalDate(datelist.toString().split(":")[0],
			datelist.split(":")[1], waperioddate.split(":")[0], waperioddate.split(":")[1]);
		newdates.add(insertdates);
	    }
	}
	if (null != newdates && newdates.size() != 0) {
	    for (int i = 0; i < newdates.size(); i++) {
		if (newdates.get(i).toString().equals("0:0")) {
		    newdates.remove(i);
		}
	    }
	}
	return newdates;
    }

    /**
     * Ա������ʱ�ͽ�������
     *
     * @param ufLiteralDate
     *            �˱�����
     * @param psnJobVO
     *            �¹���
     * @param is2LegalOrg
     *            �Ƿ�ӱ�(ͬ��Ͷ����¼������֯)
     * @author Ares.Tank 2018-9-16 11:20:53
     */
    @Override
    public void redeployPsnNHI(UFLiteralDate endDate, PsnJobVO psnJobVO, boolean is2LegalOrg) throws BusinessException {

	// ��ԃ���μӱ���֯�ķ�����֯,�����Ϊʲô�ڵ����ʱ��Ҫ�¿�һ������ȥ���¹�����¼��ԭ��,
    //�����Ѿ����ԴӺ�ֱ̨�Ӳ�ѯ�µĹ�����¼
    String sqlStr = "select PK_ORG from hi_psnjob where pk_psndoc = '"
    +psnJobVO.getPk_psndoc()+"' and lastflag = 'Y' and endflag = 'N' and dr = 0";
    String pk_org = null;
    try{
    	pk_org = (String)this.getBaseDao().executeQuery(sqlStr, new ColumnProcessor());
    }catch(Exception e){
    	return ;
    }

    if(null == pk_org){
    	return;
    }
    String[] pkLegalOrgs = { pk_org };

	pkLegalOrg = LegalOrgUtils.getLegalOrgByOrgs(pkLegalOrgs).get(pk_org);
	// �½��ڽ����Y��
	List<PsndocDefVO> savedVOList = new ArrayList<>();
	// �������µļ�¼
	SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
	// �����ʴ���
	// dealPsnWaDoc(psnJobVOs);
	List<String> lastLaborPkList = new ArrayList<>();
	List<String> lastHealthPkList = new ArrayList<>();
	if (is2LegalOrg) {
	    /*
	     * һ.ͬ����ͬ�����Ͷ���Y��: 1.�_ʼ����:delLarbolDate��һ�� done 2.Ͷ���͑B:�ӱ� done
	     * 3.Ͷ���M��:�µķ��˽M�� done
	     */

	    PsndocDefVO[] vos = service.queryByCondition(PsndocDefUtil.getPsnLaborVO().getClass(), " pk_psndoc='"
		    + psnJobVO.getPk_psndoc() + "' ");

	    if (vos != null && vos.length > 0) {

		for (PsndocDefVO vo : vos) {
		    if (vo == null || !vo.getLastflag().booleanValue()) {
			continue;// Ѱ�����µ�Ͷ����¼
		    }
		    PsndocDefVO newLaborVO = (PsndocDefVO) vo.clone();
		    newLaborVO.setBegindate(endDate.getDateAfter(1));
		    newLaborVO.setEnddate(new UFLiteralDate("9999-12-31"));
		    // ���˿�ʼ�ͽ���ʱ��
		    newLaborVO.setAttributeValue("glbdef14", endDate.getDateAfter(1));
		    newLaborVO.setAttributeValue("glbdef15", new UFLiteralDate("9999-12-31"));

		    newLaborVO.setAttributeValue("legalpersonorg", pkLegalOrg);
		    newLaborVO.setAttributeValue("insuranceform", ADD_FORM);
		    if (savedVOList.size() <= 0) {
			savedVOList.add(newLaborVO);
		    }
		}

	    }
	    vos = service.queryByCondition(PsndocDefUtil.getPsnHealthVO().getClass(),
		    " pk_psndoc='" + psnJobVO.getPk_psndoc() + "' ");
	    if (vos != null && vos.length > 0) {
		for (PsndocDefVO vo : vos) {
		    if (vo == null || !vo.getLastflag().booleanValue()) {
			continue;// Ѱ�����µ�Ͷ����¼
		    }
		    PsndocDefVO newLaborVO = (PsndocDefVO) vo.clone();
		    newLaborVO.setBegindate(endDate.getDateAfter(1));
		    newLaborVO.setEnddate(new UFLiteralDate("9999-12-31"));
		    newLaborVO.setAttributeValue("legalpersonorg", pkLegalOrg);
		    newLaborVO.setAttributeValue("insuranceform", ADD_FORM);
		    if (savedVOList.size() <= 1) {
			savedVOList.add(newLaborVO);
		    }

		}

	    }

	}
	//
	/*
	 * һ:�،�����һ�PͶ���o� :�˱� 1.�Y������:delLarbolDate done 2.Ͷ����B:�˱� done
	 * 3.�����˱�ԭ��e:�˱� done 4.�����˱�ԭ���f���e:�x�� done
	 */
	// �˱�
	delPNI(psnJobVO.getPk_psndoc(), endDate);

	// �ӱ�
	if (savedVOList != null && savedVOList.size() > 0) {
	    for (PsndocDefVO vo : savedVOList) {
		vo.setPk_psndoc_sub(null);
		service.insert(vo);
	    }
	}

	// ��д�����I--���U��Ҋ,�،���ǰ��ȫ����n,ֻ����һ�l������
	StringBuilder strSQLSB = new StringBuilder();
	for (String lastLaborPk : lastLaborPkList) {
	    strSQLSB.delete(0, strSQLSB.length());
	    strSQLSB.append("UPDATE ").append(PsndocDefTableUtil.getPsnLaborTablename())
		    .append(" SET  lastflag = 'N' ").append(" WHERE PK_PSNDOC_SUB = '").append(lastLaborPk).append("'");
	    this.getBaseDao().executeUpdate(strSQLSB.toString());
	}

	for (String lastHealthPk : lastHealthPkList) {
	    strSQLSB.delete(0, strSQLSB.length());
	    strSQLSB.append("UPDATE ").append(PsndocDefTableUtil.getPsnHealthTablename())
		    .append(" SET  lastflag = 'N' ").append(" WHERE PK_PSNDOC_SUB = '").append(lastHealthPk)
		    .append("'");
	    this.getBaseDao().executeUpdate(strSQLSB.toString());
	}

    }

    /**
     * Ա����ͣ�}ʱ�ͽ�������
     *
     * @param ufLiteralDate
     *            �˱�����
     * @param psnJobVO
     *            �¹���
     * @author Ares.Tank 2018-9-16 11:20:53
     * @throws BusinessException
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void returnPsnNHI(UFLiteralDate startDate, PsnJobVO psnJobVO) throws BusinessException {

	// mod ��д�߼��޸� Ares.Tank 2018-10-2 10:06:23

	/*
	 * Ares.Tank 2018-9-22 10:55:54 �،�ԭ��Ͷ���o�ӛ�: �_ʼ����:startDate done done
	 * �Y������:9999 done done Ͷ���͑B:�ӱ� done done �����ӱ�ԭ��e:����н done mod Ares.Tank
	 * in 2018-10-2 10:56:14 ҵ���߼����:����дԭ�е�Ͷ����¼,���������µ�Ͷ����¼
	 */
	// �������µļ�¼
	SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
	// �½��ڽ����Y��
	List<PsndocDefVO> savedVOList = new ArrayList<>();
	PsndocDefVO[] vos = service.queryByCondition(PsndocDefUtil.getPsnLaborVO().getClass(), " pk_psndoc='"
		+ psnJobVO.getPk_psndoc() + "' ");
	List<String> lastLaborPkList = new ArrayList<>();
	if (vos != null && vos.length > 0) {
	    for (PsndocDefVO vo : vos) {
		if (vo == null || !vo.getLastflag().booleanValue()) {
		    continue;// Ѱ�����µ�Ͷ����¼
		}
		lastLaborPkList.add(vo.getPk_psndoc_sub());
		PsndocDefVO newLaborVO = (PsndocDefVO) vo.clone();
		newLaborVO.setBegindate(startDate);
		newLaborVO.setEnddate(new UFLiteralDate("9999-12-31"));

		// ���˿�ʼ�ͽ���ʱ��
		newLaborVO.setAttributeValue("glbdef14", startDate);
		newLaborVO.setAttributeValue("glbdef15", new UFLiteralDate("9999-12-31"));

		// newLaborVO.setAttributeValue("legalpersonorg", pkLegalOrg);
		newLaborVO.setAttributeValue("insuranceform", ADD_FORM);
		if (savedVOList.size() <= 0) {
		    // �п��ܳ��F�K����,���Cֻ��һ�lн�Y

		    savedVOList.add(newLaborVO);
		}

	    }

	}
	Collection defvos = this.getBaseDao().retrieveByClause(DefdocVO.class,
		"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI004')");
	String glbdef17 = (defvos == null || defvos.size() == 0) ? "~" : ((DefdocVO) defvos.toArray()[0])
		.getPk_defdoc();
	vos = service.queryByCondition(PsndocDefUtil.getPsnHealthVO().getClass(),
		" pk_psndoc='" + psnJobVO.getPk_psndoc() + "' ");
	List<String> lastHealthPkList = new ArrayList<>();
	if (vos != null && vos.length > 0) {
	    for (PsndocDefVO vo : vos) {
		if (vo == null || !vo.getLastflag().booleanValue()) {
		    continue;// Ѱ�����µ�Ͷ����¼
		}
		lastHealthPkList.add(vo.getPk_psndoc_sub());
		PsndocDefVO newLaborVO = (PsndocDefVO) vo.clone();
		newLaborVO.setBegindate(startDate);
		newLaborVO.setEnddate(new UFLiteralDate("9999-12-31"));

		// newLaborVO.setAttributeValue("legalpersonorg", pkLegalOrg);
		newLaborVO.setAttributeValue("insuranceform", ADD_FORM);
		newLaborVO.setAttributeValue("glbdef17", glbdef17);
		if (savedVOList.size() <= 1) {
		    savedVOList.add(newLaborVO);
		}

	    }

	}

	// �ӱ�
	if (savedVOList != null && savedVOList.size() > 0) {
	    for (PsndocDefVO vo : savedVOList) {
		vo.setPk_psndoc_sub(null);
		service.insert(vo);
	    }
	} else {
	    throw new BusinessException("�ˆT���]��Ͷ���o�");
	}
	// ��д�����I--���U��Ҋ,�،���ǰ��ȫ����n,ֻ����һ�l������
	StringBuilder strSQLSB = new StringBuilder();
	for (String lastLaborPk : lastLaborPkList) {
	    strSQLSB.delete(0, strSQLSB.length());
	    strSQLSB.append("UPDATE ").append(PsndocDefTableUtil.getPsnLaborTablename()).append(" SET lastflag = 'N' ")
		    .append(" WHERE PK_PSNDOC_SUB = '").append(lastLaborPk).append("'");
	    this.getBaseDao().executeUpdate(strSQLSB.toString());
	}

	for (String lastHealthPk : lastHealthPkList) {
	    strSQLSB.delete(0, strSQLSB.length());
	    strSQLSB.append("UPDATE ").append(PsndocDefTableUtil.getPsnHealthTablename())
		    .append(" SET lastflag = 'N' ").append(" WHERE PK_PSNDOC_SUB = '").append(lastHealthPk).append("'");
	    this.getBaseDao().executeUpdate(strSQLSB.toString());
	}

    }

    /**
     * Ա����ͣнʱ�ͽ�������
     *
     * @param ufLiteralDate
     *            �˱�����
     * @param psnJobVO
     *            �¹���
     * @author Ares.Tank 2018-9-16 11:20:53
     * @throws BusinessException
     */
    @Override
    public void transPsnNHI(UFLiteralDate endDate, PsnJobVO psnJobVO) throws BusinessException {
	/*
	 * �،�ԭ��Ͷ���o�ӛ� 1.�Y������:endDate 2.Ͷ���͑B:�˱� 3.�����˱�ԭ��e:�˱� 4.�����˱�ԭ���f���e:�x��
	 */

	// �˱�
	delPNI(psnJobVO.getPk_psndoc(), endDate);
    }

    /**
     * �{��/�xӛ䛕r���˱�
     *
     * @param pk_psndoc
     * @param endDate
     *            �˱�����
     * @throws BusinessException
     */
    private void delPNI(String pk_psndoc, UFLiteralDate endDate) throws BusinessException {
	// �޸ĵ�����,���û�и�����˵��û��Ͷ����¼
	int resultRow = 0;

	// ��д�������ں�Ͷ����̬--�ͱ�
	String strSQL = "UPDATE " + PsndocDefTableUtil.getPsnLaborTablename() + " SET enddate='" + endDate.toString()
		+ "' , insuranceform = " + DEL_FORM + " WHERE ISNULL(enddate, '9999-12-31') > '" + endDate.toString()
		+ "' AND dr=0 AND pk_psndoc = '" + pk_psndoc + "'" + " and lastflag = 'Y'";
	resultRow = this.getBaseDao().executeUpdate(strSQL);

	strSQL = "UPDATE " + PsndocDefTableUtil.getPsnLaborTablename() + " SET glbdef15='" + endDate.toString()
		+ "' WHERE ISNULL(glbdef15, '9999-12-31') > '" + endDate.toString() + "' AND dr=0 AND pk_psndoc = '"
		+ pk_psndoc + "'" + " and lastflag = 'Y'";
	resultRow += this.getBaseDao().executeUpdate(strSQL);

	// : �a������������λ���،�߉݋
	// ssx modified on 2018-03-19
	// for changes of psn leave
	// �x�Ԅ��˱��r�A�u�˱�ԭ��e�飺2.�D�� ��

	// Ares.Tank mod on 2018-9-15 22:55:16
	// ���߼�:Ͷ����B���˱��������˱�ԭ��e��1.�˱��������˱�ԭ���f���e��1.�x��--Jessie

	// �˱��f���e�飺1.�x
	Collection defvos = this.getBaseDao().retrieveByClause(DefdocVO.class,
		"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI005')");
	String glbdef18 = (defvos == null || defvos.size() == 0) ? "~" : ((DefdocVO) defvos.toArray()[0])
		.getPk_defdoc();
	defvos = this.getBaseDao().retrieveByClause(DefdocVO.class,
		"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI006')");
	String glbdef19 = (defvos == null || defvos.size() == 0) ? "~" : ((DefdocVO) defvos.toArray()[0])
		.getPk_defdoc();
	String updatesql = "update "+ PsndocDefTableUtil.getPsnHealthTablename() +" set lastflag='Y' WHERE dr=0 AND "
			+ "glbdef3=( SELECT ID FROM BD_PSNDOC "
			+ "WHERE PK_PSNDOC='"+pk_psndoc+"') and "
			+ "(enddate is null or enddate='9999-12-31' or enddate = '~')";
	this.getBaseDao().executeUpdate(updatesql);
	
	String updatesqls = "update "+ PsndocDefTableUtil.getPsnHealthTablename() +" set lastflag='N' WHERE dr=0 AND "
			+ "glbdef3 !=( SELECT ID FROM BD_PSNDOC "
			+ "WHERE PK_PSNDOC='"+pk_psndoc+"') and "
			+ "(enddate is null or enddate='9999-12-31' or enddate = '~')";
	this.getBaseDao().executeUpdate(updatesqls);
	strSQL = "UPDATE " + PsndocDefTableUtil.getPsnHealthTablename() + " SET enddate='" + endDate.toString()
		+ "', glbdef18='" + glbdef18 + "', glbdef19='" + glbdef19 + "' , insuranceform = " + DEL_FORM
		+ " WHERE ISNULL(enddate, '9999-12-31') > '" + endDate.toString() + "' AND dr=0 AND pk_psndoc = '"
		+ pk_psndoc + "'" + " and (enddate is null or enddate='9999-12-31' or enddate = '~') ";
	resultRow += this.getBaseDao().executeUpdate(strSQL);
	String clearsql = "update "+ PsndocDefTableUtil.getPsnHealthTablename() +"set glbdef18='~', "
			+ "glbdef19='~' WHERE dr=0 AND "
			+ "glbdef3 !=( SELECT ID FROM BD_PSNDOC "
			+ "WHERE PK_PSNDOC='"+pk_psndoc+"') and "
			+ "enddate='" + endDate.toString()+"'";
	this.getBaseDao().executeUpdate(clearsql);
	if (resultRow <= 0) {
	    throw new BusinessException("�ˆT���]��Ͷ���o�");
	}

    }

    /**
     * ��ȡ�Ƿ���н
     *
     * @param pk_psndoc
     * @param context
     * @param basedate
     * @return
     * @throws BusinessException
     */
    public UFBoolean ismonthsalary(String pk_psndoc, UFDate basedate) throws BusinessException {
	List<PsnJobVO> psnjobvos = (List<PsnJobVO>) this.getBaseDao().retrieveByClause(PsnJobVO.class,
		"pk_psndoc='" + pk_psndoc + "'");
	UFBoolean ismonthsalary = UFBoolean.TRUE;
	for (PsnJobVO psnjob : psnjobvos) {
	    // ȡ��׼��������ְ��¼������
	    if (psnjob.getBegindate().before(new UFLiteralDate(basedate.toString()))
		    || psnjob.getBegindate().isSameDate(new UFLiteralDate(basedate.toString()))
		    && (psnjob.getEnddate() == null || psnjob.getEnddate().equals("~")
			    || psnjob.getEnddate().isSameDate(new UFLiteralDate(basedate.toString())) || psnjob
			    .getEnddate().after(new UFLiteralDate(basedate.toString())))) {
		return (UFBoolean) psnjob.getAttributeValue("ismonsalary");
	    }
	}
	return ismonthsalary;
    }

    /**
     * �ж����Ա����ְ�Ƿ���������
     *
     * @param pk_psndoc
     * @param context
     * @return
     * @throws BusinessException
     */
    public UFBoolean sixmonth(String pk_psndoc, String pk_org) throws BusinessException {
	List<PsnJobVO> joblist = (List<PsnJobVO>) this.getBaseDao().retrieveByClause(PsnJobVO.class,
		"pk_psndoc='" + pk_psndoc + "' and pk_org='" + pk_org + "'");
	UFBoolean sixmonthflag = UFBoolean.TRUE;
	return sixmonthflag;
    }

    /**
     * ������н(��׼ʱ���--ȡ��ȡ�ң�н���ڼ��--��ȡ��Ҳȡ��)
     *
     * @param basedateperiod
     * @param waperiodlist
     * @return
     */
    private UFDouble getavgsalary(UFDate basedate, UFDate basemonthdate, List<Map<String, Object>> waperiodlist) {
	// ȡн���ڼ�ͻ�׼���ڶε�ʱ�佻��
	int sumSalary = 0;
	int sumDays = 0;
	for (Map<String, Object> waperiod : waperiodlist) {
	    String waperiodstartdate = (String) waperiod.get("cstartdate");
	    String waperiodenddate = (String) waperiod.get("cenddate");
	    String periodtime = ComdateUtils.getAlphalDate(basemonthdate.toString(),
		    ComdateUtils.getcheckdate(basedate.toString(), -1), waperiodstartdate, waperiodenddate);
	    // ���ڽ���������
	    int days = 0;
	    if (null != periodtime) {
		days = ComdateUtils.daysOfTwo(periodtime.split(":")[0], periodtime.split(":")[1]);
	    }
	    sumDays += days;
	    // ��ȡн���ڼ������
	    int periodays = ComdateUtils.daysOfTwo(waperiodstartdate, waperiodenddate);
	    sumSalary += (Integer.parseInt(waperiod.get("f_itemkey").toString()) / periodays) * days;
	}
	if (sumDays == 0) {
	    return UFDouble.ZERO_DBL;
	}

	return (new UFDouble(sumSalary + "").div(new UFDouble(sumDays + "")));
    }

    /**
     * ����н����Ա����
     *
     * @param pk_psndoc
     * @param basedate
     * @param basemonthdate
     * @param waperiodlist
     * @return
     * @throws BusinessException
     */
    public UFDouble getnoavgsalary(String pk_psndoc, UFDate basedate, UFDate basemonthdate,
	    List<Map<String, Object>> waperiodlist) throws BusinessException {
	// ȡн���ڼ�ͻ�׼���ڶε�ʱ�佻��
	long sumSalary = 0;
	long sumDays = 0;
	for (Map<String, Object> waperiod : waperiodlist) {
	    String waperiodstartdate = (String) waperiod.get("cstartdate");
	    String waperiodenddate = (String) waperiod.get("cenddate");
	    String periodtime = ComdateUtils.getAlphalDate(basemonthdate.toString(),
		    ComdateUtils.getcheckdate(basedate.toString(), -1), waperiodstartdate, waperiodenddate);
	    // ���ڽ���������
	    long days = 0;
	    if(null != periodtime){
	    	days = ComdateUtils.daysOfTwo(periodtime.split(":")[0], periodtime.split(":")[1]);
	    }else {
	    	days = 0;
	    }
	    sumDays += days;
	    // ��ȡн���ڼ������
	    long periodays = ComdateUtils.daysOfTwo(waperiodstartdate, waperiodenddate);
	    sumSalary += ((Long.parseLong(waperiod.get("f_itemkey").toString())) / periodays) * days;
	}
	if (sumDays == 0) {
	    return UFDouble.ZERO_DBL;
	}
	// �������н��Ա��ʵ����н
	List<PsnCalendarVO> psncalendarlist = (List<PsnCalendarVO>) this.getBaseDao().retrieveByClause(
		PsnCalendarVO.class,
		"pk_psndoc='" + pk_psndoc + "'  and " + "calendar BETWEEN '" + basemonthdate + "' and '"
			+ ComdateUtils.getcheckdate(basedate.toString(), -1) + "'");
	int actdays = 0;
	for (PsnCalendarVO psnvo : psncalendarlist) {
	    if (null != psnvo.getGzsj() && psnvo.getGzsj().getDouble() > 0) {
		actdays++;
	    }
	}
	// ʵ����нΪ
	UFDouble actdaysalary = UFDouble.ZERO_DBL;
	if (0 != actdays) {
	    actdaysalary = new UFDouble(sumSalary + "").div(new UFDouble(actdays + ""));
	}
	return actdaysalary;
    }

    /**
     * ͬ���ű�����
     */

    @Override
    public void renewGroupIns(String pk_org, String[] pk_psndocs, UFDate effectivedate) throws BusinessException {
	// ��ԃ���μӱ���֯�ķ�����֯ Ares.Tank
	String[] pkLegalOrgs = { pk_org };
	pkLegalOrg = LegalOrgUtils.getLegalOrgByOrgs(pkLegalOrgs).get(pk_org);
	// ��ѯԱ���Ķ�������Ϣ
	if (pk_psndocs != null && pk_psndocs.length > 0) {
	    for (String pk_psndoc : pk_psndocs) {
		// ȡ�����
		this.getRangeTables(pk_org, effectivedate);
		// У�н�Y���g��Ч��
		// �z���Ƿ������Ч�����������ʼ����
		checkExistDateValid(pk_psndocs, effectivedate);

		// ��ѯԱ���ڲ����趨�е�н����
		// ��ȡ������֯�µ�н����
		List<String> salaryitems = new ArrayList<String>();
		InSQLCreator insql = new InSQLCreator();
		List<BaseDocVO> basedocvos = (List<BaseDocVO>) this.getBaseDao().retrieveByClause(BaseDocVO.class,
			"pk_org='" + pkLegalOrg + "' and code like 'GRPSUM%'");
		for (BaseDocVO basedocvo : basedocvos) {
		    if (null != basedocvo.getRefvalue()) {
			salaryitems.add(basedocvo.getRefvalue());
		    }
		}
		String waitemInSQL = insql.getInSQL(salaryitems.toArray(new String[0]));
		if (null == salaryitems || salaryitems.size() == 0) {
		    throw new BusinessException("�����趨��û����Ҫ�����н����");
		}
		// ͨ��н����ȥ�����ʱ�������ҳ����е�����һ����¼
		List<PsndocWadocVO> wavos = (List<PsndocWadocVO>) this.getBaseDao().retrieveByClause(PsndocWadocVO.class,
			"pk_psndoc='" + pk_psndoc + "' and pk_wa_item in (" + waitemInSQL + ") and enddate is null");
		UFDouble sumSalary = UFDouble.ZERO_DBL;
		for (PsndocWadocVO psndocwa : wavos) {
		    sumSalary = sumSalary.add(psndocwa.getNmoney());
		}
		if (baseOnAverageSalary) {
		    if (sumSalary == null) {
			continue;
		    }

		    if (sumSalary.equals(UFDouble.ZERO_DBL)) {
			IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
			PsndocVO[] vos = psnQry.queryPsndocByPks(new String[] { pk_psndoc });
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
				"twhr_personalmgt", "068J61035-0031", null, new String[] { vos[0].getCode() })
			/*
			 * �T�� [{0}] ��ָ�������g�����ț]��н�Y�Y�� �� �o��Ӌ��ƽ��н�Y ��
			 */);
		    }
		} /*
		   * else { // ȡ������ IPsndocSubInfoService4JFS salaryQry =
		   * NCLocator.getInstance().lookup(
		   * IPsndocSubInfoService4JFS.class); sumSalary =
		   * salaryQry.getGroupInsWadocBaseSalaryByPsnDate(pk_org,
		   * pk_psndoc, new UFLiteralDate( effectivedate.toString())); }
		   */
		List<String> updateSQLs = new ArrayList<String>();

		// ̎���ͱ�����
		List<PsndocDefVO> subInfoLines = dealGroupInsInfoset(effectivedate, pk_psndoc, sumSalary, updateSQLs);

		// �����Ѵ���ӛ�
		if (updateSQLs.size() > 0) {
		    for (String sql : updateSQLs) {
			this.getBaseDao().executeUpdate(sql);
		    }
		}

		// �،��Ӽ�
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
		if (subInfoLines != null && subInfoLines.size() > 0) {
		    for (PsndocDefVO vo : subInfoLines) {
			service.insert(vo);
		    }
		}
	    }
	}

    }
    /**
	 * ���Ʉڽ����O��
	 * 
	 * @param pk_org
	 *            �M��
	 * @param <pk_psndoc,Set<�ӱ�����>> psndocsAndInsuranceTypeMap
	 *            �ˆT�б�
	 * @param NHIInsMap 
	 * 			�ӱ���Ϣ map key ����:nc.itf.hr.hi.BatchInsuranceFieldDeclaration
	 * @throws BusinessException
	 */
	@Override
	public void generatePsnNHI(String pk_org, Map<String,Set<InsuranceTypeEnum>> psndocsAndInsuranceTypeMap,
			Map<String, String> NHIInsMap) throws BusinessException {
		// TODO Auto-generated method stub
		
	}
	/**
	 * ���ɈF���O��
	 * 
	 * @param pk_org
	 *            �M��
	 * @param pk_psndocs
	 *            �ˆT�б�
	 * @throws BusinessException
	 */
	@Override
	public void generateGroupIns(String pk_org, String[] pk_psndocs,
			Map<String, String> groupInsMap) throws BusinessException {
		// TODO Auto-generated method stub
		
	}
	/**
	 * �ͽ��������˱�
	 * @param pk_org
	 * @param @param <pk_psndoc,Set<�ӱ�����>> psndocsAndInsuranceTypeMap
	 * @param outMap �˱���Ϣ map key ����:nc.itf.hr.hi.BatchInsuranceFieldDeclaration
	 * @throws BusinessException
	 */
	@Override
	public void delPsnNHI(String pk_org, Map<String,Set<InsuranceTypeEnum>> psndocsAndInsuranceTypeMap,
			Map<String, String> outMap) throws BusinessException {
		// TODO Auto-generated method stub
		
	}
	/**
	 * �ű������˱�
	 * @param pk_org
	 * @param pk_psndocs
	 * @param outMap �˱���Ϣ mapKey map key ����:nc.itf.hr.hi.BatchInsuranceFieldDeclaration
	 * @throws BusinessException
	 */
	@Override
	public void delGroupIns(String pk_org, String[] pk_psndocs,
			Map<String, String> outMap) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

}