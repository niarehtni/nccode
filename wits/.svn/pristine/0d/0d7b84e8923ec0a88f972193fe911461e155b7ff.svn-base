package nc.impl.wa.end;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uap.oid.OidGenerator;
import nc.hr.frame.persistence.AppendBaseDAO;
import nc.hr.frame.persistence.ILocker;
import nc.hr.frame.persistence.SimpleDocLocker;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.impl.wa.classitem.ClassItemManageServiceImpl;
import nc.impl.wa.payfile.PayfileDAO;
import nc.impl.wa.pub.WapubDAO;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hr.wa.IClassItemManageService;
import nc.itf.hr.wa.IWaBmfileQueryService;
import nc.itf.hr.wa.IWaClass;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.md.data.access.NCObject;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.org.ICloseAccPubServicer;
import nc.pubitf.org.ICloseAccQryPubServicer;
import nc.vo.dataitem.pub.DataVOUtils;
import nc.vo.hr.datainterface.AggHrIntfaceVO;
import nc.vo.hr.datainterface.FormatItemVO;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.hr.datainterface.IfsettopVO;
import nc.vo.hr.pub.FormatVO;
import nc.vo.hr.pub.WaBmFileOrgVO;
import nc.vo.hr.tools.dbtool.util.db.DBUtil;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hr.tools.pub.GeneralVOProcessor;
import nc.vo.ml.MultiLangUtil;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.trade.sqlutil.IInSqlBatchCallBack;
import nc.vo.trade.sqlutil.InSqlBatchCaller;
import nc.vo.uif2.LoginContext;
import nc.vo.util.BDVersionValidationUtil;
import nc.vo.wa.amoscheme.AggAmoSchemeVO;
import nc.vo.wa.amoscheme.AmoCondVO;
import nc.vo.wa.amoscheme.AmoFactorVO;
import nc.vo.wa.amoscheme.AmoSchemeVO;
import nc.vo.wa.amoscheme.AmobaseVO;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.category.WaFiorgVO;
import nc.vo.wa.category.WaInludeclassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.classpower.ClassPowerUtil;
import nc.vo.wa.datainterface.DataIOconstant;
import nc.vo.wa.end.IMonthEndType;
import nc.vo.wa.end.WaClassEndVO;
import nc.vo.wa.item.WaItemConstant;
import nc.vo.wa.paydata.DataSVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.payslip.AggPayslipVO;
import nc.vo.wa.payslip.PayslipItemVO;
import nc.vo.wa.payslip.PayslipVO;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.pub.ParaConstant;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WACLASSTYPE;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.YearPeriodSeperatorVO;
import nc.vo.wa.unitclass.UnitClassVO;
import nc.vo.wabm.util.WaCacheUtils;

import org.apache.commons.lang.ArrayUtils;

/**
 * 薪资类别期末处理类
 * 
 * @author: zhangg
 * @date: 2009-12-17 上午09:16:21
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class MonthEndDAO extends AppendBaseDAO implements IMonthEndType {

	/**
	 * 
	 * @author zhangg on 2010-7-15
	 * @param dataVO
	 * @param transferItemVOs
	 */
	private void transferValue(DataVO dataVO, WaClassItemVO[] transferItemVOs) {
		if (transferItemVOs == null || dataVO == null) {
			return;
		}
		Map<String, Object> oldeValueMap = new HashMap<String, Object>();
		for (WaClassItemVO waClassItemVO : transferItemVOs) {

			String fromItemKey = waClassItemVO.getItemkey();
			String toItemKey = waClassItemVO.getDestitemcol();

			Object oldValue = dataVO.getAttributeValue(toItemKey);
			oldeValueMap.put(toItemKey, oldValue);

			if (oldeValueMap.get(fromItemKey) != null) {
				dataVO.setAttributeValue(toItemKey,
						oldeValueMap.get(fromItemKey));
			} else {
				dataVO.setAttributeValue(toItemKey,
						dataVO.getAttributeValue(fromItemKey));
			}
		}
	}

	public WaClassItemVO[] getTransferItems(WaClassEndVO waLoginVO)
			throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer
				.append("select wa_classitem.itemkey, wa_classitem.destitemcol "); // 1
		sqlBuffer.append("  from wa_classitem ");
		sqlBuffer.append(" where wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append("   and wa_classitem.cyear = ? ");
		sqlBuffer.append("   and wa_classitem.cperiod = ? ");
		sqlBuffer.append("   and wa_classitem.istransfer = 'Y' ");
		sqlBuffer.append("   and wa_classitem.destitemcol is not null ");

		return executeQueryVOs(sqlBuffer.toString(),
				getCommonParameter(waLoginVO), WaClassItemVO.class);
	}

	private void createNextPeriodItems(WaClassVO sourceLoginVO,
			WaClassVO destLoginVO) throws nc.vo.pub.BusinessException {

		// 下一期间薪资项目
		WaClassItemVO[] nextPeriodItemVOs = null;
		// 如果是被分配类别查看原有类别在下一期间是否存在新版本项目
		// 该类别是否是分配的类别
		PeriodStateVO nextPeriodStateVO = getNextPeriodVO(destLoginVO);

		// 得到公司下薪资项目

		String condition = " pk_wa_class = '" + sourceLoginVO.getPk_wa_class()
				+ "'and cyear = '" + sourceLoginVO.getCyear()
				+ "' and cperiod = '" + sourceLoginVO.getCperiod() + "'";

		// 过滤掉在离职方案中，普通方案没有的薪资项目
		condition = condition
				+ " and itemkey not in(select distinct itemkey from WA_CLASSITEM where cyear = '"
				+ sourceLoginVO.getCyear()
				+ "' and cperiod = '"
				+ sourceLoginVO.getCperiod()
				+ "' and"
				+ " PK_WA_CLASS in(select PK_CHILDCLASS from WA_INLUDECLASS where pk_parentclass = '"
				+ sourceLoginVO.getPk_wa_class()
				+ "' and BATCH>100) "
				+ "and ITEMKEY not in(select itemkey from WA_CLASSITEM where cyear = '"
				+ sourceLoginVO.getCyear()
				+ "' and cperiod = '"
				+ sourceLoginVO.getCperiod()
				+ "' and  PK_WA_CLASS "
				+ "in(select PK_CHILDCLASS from WA_INLUDECLASS where pk_parentclass = '"
				+ sourceLoginVO.getPk_wa_class() + "' and BATCH<=100)))";

		WaClassItemVO[] corpClassItemVOs = retrieveByClause(
				WaClassItemVO.class, condition);

		// 20151124 xiejie3 NCdp205547959 集团方案引用组织发放项目后，进行补发时，补发计算应发合计、实发合计都为0
		// 原因：集团级方案分配到组织，期末处理后，会取将来自集团的发放项目替换为集团级。导致应发合计没有加上所有的增项。方案：期末处理，直接取组织级的，不区分是否来自集团。
		// AssignclsVO assignclsVO = new AssignclsVO();
		// assignclsVO.setClassid(sourceLoginVO.getPk_wa_class());
		// assignclsVO = retrieveSingle(assignclsVO);
		// if (assignclsVO != null) {// 是分配的类别
		// String pk_wa_class = assignclsVO.getPk_sourcecls();
		// String cyear = nextPeriodStateVO.getCyear();
		// String cperiod = nextPeriodStateVO.getCperiod();
		//
		// WaClassVO groupclassVO = new WaLoginVO();
		// groupclassVO.setPk_wa_class(pk_wa_class);
		// groupclassVO.setCyear(cyear);
		// groupclassVO.setCperiod(cperiod);
		//
		// // 得到集团下对应期间的薪资项目
		// WaClassItemVO[] groupClassItemVOs = getGroupClassItems(groupclassVO);
		//
		//
		//
		// // 下一期间薪资项目
		// nextPeriodItemVOs = getNextPeriodItems(groupClassItemVOs,
		// corpClassItemVOs);
		//
		// } else {

		nextPeriodItemVOs = corpClassItemVOs;
		// }
		// end
		// 对下一期的项目更新期间
		for (WaClassItemVO waClassItemVO : nextPeriodItemVOs) {
			waClassItemVO.setPk_wa_class(destLoginVO.getPk_wa_class());
			waClassItemVO.setCyear(nextPeriodStateVO.getCyear());
			waClassItemVO.setCperiod(nextPeriodStateVO.getCperiod());
			waClassItemVO.setPk_org(destLoginVO.getPk_org());
		}
		// 插入下一期
		getBaseDao().insertVOArray(nextPeriodItemVOs);
		// 20151208 shenliangc NCdp205556679 薪资补发多次发放薪资方案，只有一次发放的薪资项目值补发值没有累计。
		// 根本原因是期末处理生成下期间发放项目后系统项目公式没有包含多次方案子方案中添加的发放项目。
		// 20151209 修改后重新构造补丁
		ClassItemManageServiceImpl service = new ClassItemManageServiceImpl();
		service.regenerateSystemFormula(destLoginVO.getPk_org(),
				destLoginVO.getPk_wa_class(), nextPeriodStateVO.getCyear(),
				nextPeriodStateVO.getCperiod());
	}

	/**
	 * 
	 * @author zhangg on 2009-12-22
	 * @param groupClassItemVOs
	 * @param corpClassItemVOs
	 * @return
	 * @throws DAOException
	 */
	// 20151124 xiejie3 NCdp205547959 集团方案引用组织发放项目后，进行补发时，补发计算应发合计、实发合计都为0
	// 原因：集团级方案分配到组织，期末处理后，会取将来自集团的发放项目替换为集团级。导致应发合计没有加上所有的增项。方案：期末处理，直接取组织级的，不区分是否来自集团。
	/*
	 * private WaClassItemVO[] getNextPeriodItems(WaClassItemVO[]
	 * groupClassItemVOs, WaClassItemVO[] corpClassItemVOs) throws DAOException
	 * { HashMap<String, WaClassItemVO> classitemHashMap = new HashMap<String,
	 * WaClassItemVO>(); // 将集团的项目放到map中 for (WaClassItemVO waClassItemVO :
	 * groupClassItemVOs) { classitemHashMap.put(waClassItemVO.getPk_wa_item(),
	 * waClassItemVO); }
	 * 
	 * // 更新一下项目名称， 取公司的名称 for (WaClassItemVO waClassItemVO : corpClassItemVOs)
	 * { WaClassItemVO groupClassItemVO =
	 * classitemHashMap.get(waClassItemVO.getPk_wa_item()); if (groupClassItemVO
	 * != null) { groupClassItemVO.setName(waClassItemVO.getName());
	 * groupClassItemVO.setName2(waClassItemVO.getName2());
	 * groupClassItemVO.setName3(waClassItemVO.getName3());
	 * groupClassItemVO.setName4(waClassItemVO.getName4());
	 * groupClassItemVO.setName5(waClassItemVO.getName5());
	 * groupClassItemVO.setName6(waClassItemVO.getName6()); } } // 是否允许公司自己增加项目
	 * WaClassVO groupClassVO = retrieveByPK(WaClassVO.class,
	 * groupClassItemVOs[0].getPk_wa_class()); boolean isCorpAddable =
	 * groupClassVO.getAddflag().booleanValue(); if (isCorpAddable) { for
	 * (WaClassItemVO waClassItemVO : corpClassItemVOs) { if
	 * (!classitemHashMap.containsKey(waClassItemVO.getPk_wa_item())) {
	 * classitemHashMap.put(waClassItemVO.getPk_wa_item(), waClassItemVO); } } }
	 * return classitemHashMap.values().toArray(new WaClassItemVO[0]); }
	 */
	// end
	/**
	 * 
	 * @author zhangg on 2009-12-22
	 * @param groupclassVO
	 * @return
	 * @throws BusinessException
	 * @throws DAOException
	 */
	private WaClassItemVO[] getGroupClassItems(WaClassVO groupclassVO)
			throws BusinessException, DAOException {
		YearPeriodSeperatorVO periodSeperatorVO = getGroupPeriod(groupclassVO);
		if (periodSeperatorVO == null) {
			throw new BusinessException(ResHelper.getString("6013mend",
					"06013mend0026")/* @res "没有找到集团版本！" */);
		}

		String condition = " pk_wa_class = '" + groupclassVO.getPk_wa_class()
				+ "'and cyear = '" + periodSeperatorVO.getYear()
				+ "' and cperiod = '" + periodSeperatorVO.getPeriod() + "'";

		return retrieveByClause(WaClassItemVO.class, condition);
	}

	/**
	 * 创建下一期间的薪资发放条
	 * 
	 * @author zhangg on 2009-12-16
	 * @param waLoginVO
	 * @throws nc.vo.pub.BusinessException
	 */
	private void createNextPeriodPaySlip(WaClassEndVO waClassEndVO)
			throws nc.vo.pub.BusinessException {
		InSQLCreator inC = null;
		try {
			PeriodStateVO nextPeriodStateVO = getNextPeriodVO(waClassEndVO);
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append(" select * from wa_payslip where ");
			sqlBuffer.append(" pk_wa_class = ? and ");
			sqlBuffer.append("  accyear = ? and ");
			sqlBuffer.append("  accmonth = ? ");
			SQLParameter parameter = getCommonParameter(waClassEndVO);

			PayslipVO[] payslipVos = executeQueryVOs(sqlBuffer.toString(),
					parameter, PayslipVO.class);

			if (!ArrayUtils.isEmpty(payslipVos)) {
				sqlBuffer = new StringBuffer();
				inC = new InSQLCreator();
				sqlBuffer
						.append(" select * from wa_payslip_item where pk_payslip in (")
						.append(inC.getInSQL(payslipVos, PayslipVO.PK_PAYSLIP))
						.append(")");
				PayslipItemVO[] payitems = executeQueryVOs(
						sqlBuffer.toString(), PayslipItemVO.class);
				HashMap<Object, PayslipItemVO[]> payitemsMap = (HashMap<Object, PayslipItemVO[]>) CommonUtils
						.group2ArrayByField(PayslipItemVO.PK_PAYSLIP, payitems);
				List<AggPayslipVO> aggVOList = new ArrayList<AggPayslipVO>();
				for (int i = 0; i < payslipVos.length; i++) {

					AggPayslipVO aggVO = new AggPayslipVO();
					payslipVos[i].setAccyear(nextPeriodStateVO.getCyear());
					payslipVos[i].setAccmonth(nextPeriodStateVO.getCperiod());

					PayslipItemVO[] childrenVO = payitemsMap.get(payslipVos[i]
							.getPk_payslip());
					payslipVos[i].setPk_payslip(null);
					payslipVos[i].setStatus(VOStatus.NEW);
					aggVO.setParentVO(payslipVos[i]);
					if (!ArrayUtils.isEmpty(childrenVO)) {
						for (int j = 0; j < childrenVO.length; j++) {
							childrenVO[j].setPk_payslip(null);
							childrenVO[j].setPk_payslip_item(null);
							childrenVO[j].setStatus(VOStatus.NEW);
						}
						aggVO.setTableVO("item", childrenVO);
					}
					aggVOList.add(aggVO);
				}
				AggPayslipVO[] aggPayslipVOs = aggVOList
						.toArray(new AggPayslipVO[0]);
				IMDPersistenceService service = MDPersistenceService
						.lookupPersistenceService();
				NCObject[] ncObjs = new NCObject[aggPayslipVOs.length];
				for (int i = 0; i < aggPayslipVOs.length; i++) {
					ncObjs[i] = NCObject.newInstance(aggPayslipVOs[i]);
				}
				service.saveBill(ncObjs);
				/*
				 * for (int i = 0; i<payslipVos.length; i++) {
				 * payslipVos[i].setAccyear(nextPeriodStateVO.getCyear());
				 * payslipVos[i].setAccmonth(nextPeriodStateVO.getCperiod());
				 * sqlBuffer = new StringBuffer(); sqlBuffer.append(
				 * " select * from wa_payslip_item where pk_payslip = '"
				 * ).append(payslipVos[i].getPk_payslip()).append("'");
				 * PayslipItemVO[] payitems =
				 * executeQueryVOs(sqlBuffer.toString(), PayslipItemVO.class);
				 * payslipVos[i].setPk_payslip(null);
				 * 
				 * String key = getBaseDao().insertVO(payslipVos[i]); if
				 * (ArrayUtils.isEmpty(payitems)) { continue; }
				 * 
				 * for (int j = 0; j<payitems.length;j++) {
				 * payitems[j].setPk_payslip(key);
				 * payitems[j].setPk_payslip_item(null); }
				 * getBaseDao().insertVOArray(payitems); }
				 */
			}
		} finally {
			if (inC != null) {
				inC.clear();
			}
		}

	}

	private void createNextPeriodAmoScheme(WaClassEndVO waClassEndVO)
			throws nc.vo.pub.BusinessException {
		// TODO 为空，预留, 处理薪资条
		PeriodStateVO nextPeriodStateVO = getNextPeriodVO(waClassEndVO);
		createNextPeriodAmoScheme(waClassEndVO, nextPeriodStateVO);
	}

	public void createNextPeriodAmoScheme(WaClassEndVO waClassEndVO,
			PeriodStateVO nextPeriodStateVO) throws nc.vo.pub.BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select * from wa_amoscheme where ");
		sqlBuffer.append(" classid = ? and ");
		sqlBuffer.append("  cyear = ? and ");
		sqlBuffer.append("  cperiod = ? ");
		SQLParameter parameter = getCommonParameter(waClassEndVO);

		AmoSchemeVO[] amoSchemeVOs = executeQueryVOs(sqlBuffer.toString(),
				parameter, AmoSchemeVO.class);

		if (!ArrayUtils.isEmpty(amoSchemeVOs)) {
			InSQLCreator inC = new InSQLCreator();
			try {
				sqlBuffer = new StringBuffer();

				// 计提基数
				sqlBuffer
						.append(" select * from wa_amobase where pk_amoscheme in (")
						.append(inC.getInSQL(amoSchemeVOs,
								AmoSchemeVO.PK_AMOSCHEME)).append(")");
				AmobaseVO[] amobaseVOs = executeQueryVOs(sqlBuffer.toString(),
						AmobaseVO.class);
				HashMap<Object, AmobaseVO[]> amobaseVOsMap = (HashMap<Object, AmobaseVO[]>) CommonUtils
						.group2ArrayByField(AmobaseVO.PK_AMOSCHEME, amobaseVOs);

				// 分摊条件
				StringBuffer sqlBuffer2 = new StringBuffer();
				sqlBuffer2
						.append(" select * from wa_amocond where pk_amoscheme in (")
						.append(inC.getInSQL(amoSchemeVOs,
								AmoSchemeVO.PK_AMOSCHEME)).append(")");
				AmoCondVO[] amoCondVOs = executeQueryVOs(sqlBuffer2.toString(),
						AmoCondVO.class);
				HashMap<Object, AmoCondVO[]> amoCondVOsMap = (HashMap<Object, AmoCondVO[]>) CommonUtils
						.group2ArrayByField(AmobaseVO.PK_AMOSCHEME, amoCondVOs);

				// 分摊影响因素
				StringBuffer sqlBuffer3 = new StringBuffer();
				sqlBuffer3
						.append(" select * from wa_amofactor where pk_amoscheme in (")
						.append(inC.getInSQL(amoSchemeVOs,
								AmoSchemeVO.PK_AMOSCHEME)).append(")");
				AmoFactorVO[] amoFactorVOs = executeQueryVOs(
						sqlBuffer3.toString(), AmoFactorVO.class);
				HashMap<Object, AmoFactorVO[]> amoFactorVOsMap = (HashMap<Object, AmoFactorVO[]>) CommonUtils
						.group2ArrayByField(AmobaseVO.PK_AMOSCHEME,
								amoFactorVOs);

				List<AggAmoSchemeVO> aggVOList = new ArrayList<AggAmoSchemeVO>();
				AmobaseVO[] childrenBaseVO = null;
				AmoCondVO[] childrenCondVO = null;
				AmoFactorVO[] childrenFactorVO = null;
				for (int i = 0; i < amoSchemeVOs.length; i++) {
					AggAmoSchemeVO aggVO = new AggAmoSchemeVO();

					amoSchemeVOs[i].setCyear(nextPeriodStateVO.getCyear());
					amoSchemeVOs[i].setCperiod(nextPeriodStateVO.getCperiod());

					childrenBaseVO = amobaseVOsMap.get(amoSchemeVOs[i]
							.getPk_amoscheme());

					if (amoCondVOsMap != null) {
						childrenCondVO = amoCondVOsMap.get(amoSchemeVOs[i]
								.getPk_amoscheme());
					}
					if (amoFactorVOsMap != null) {
						childrenFactorVO = amoFactorVOsMap.get(amoSchemeVOs[i]
								.getPk_amoscheme());
					}
					amoSchemeVOs[i].setPk_amoscheme(null);
					amoSchemeVOs[i].setStatus(VOStatus.NEW);
					aggVO.setParentVO(amoSchemeVOs[i]);

					if (!ArrayUtils.isEmpty(childrenBaseVO)) {
						for (int j = 0; j < childrenBaseVO.length; j++) {
							childrenBaseVO[j].setPk_amoscheme(null);// (key);
							childrenBaseVO[j].setPk_amobase(null);
							childrenBaseVO[j].setStatus(VOStatus.NEW);
						}
						aggVO.setTableVO("base", childrenBaseVO);
					}

					if (!ArrayUtils.isEmpty(childrenCondVO)) {
						updateNextPeriodAmoCondItemPk(childrenCondVO,
								amoSchemeVOs[i].getCyear(),
								amoSchemeVOs[i].getCperiod());
						for (int j = 0; j < childrenCondVO.length; j++) {

							childrenCondVO[j].setPk_amoscheme(null);// (key);
							childrenCondVO[j].setPk_wa_amocond(null);
							childrenCondVO[j].setStatus(VOStatus.NEW);
						}
						aggVO.setTableVO("cond", childrenCondVO);
					}

					if (!ArrayUtils.isEmpty(childrenFactorVO)) {
						for (int j = 0; j < childrenFactorVO.length; j++) {
							childrenFactorVO[j].setPk_amoscheme(null);// (key);
							childrenFactorVO[j].setPk_amofactor(null);
							childrenFactorVO[j].setStatus(VOStatus.NEW);
						}
						aggVO.setTableVO("factor", childrenFactorVO);
					}
					aggVOList.add(aggVO);
				}

				AggAmoSchemeVO[] aggAmoSchemeVOs = aggVOList
						.toArray(new AggAmoSchemeVO[0]);
				IMDPersistenceService service = MDPersistenceService
						.lookupPersistenceService();
				NCObject[] ncObjs = new NCObject[aggAmoSchemeVOs.length];
				for (int i = 0; i < aggAmoSchemeVOs.length; i++) {
					ncObjs[i] = NCObject.newInstance(aggAmoSchemeVOs[i]);
				}
				service.saveBill(ncObjs);

			} finally {
				inC.clear();
			}

		}
	}

	private void updateNextPeriodAmoCondItemPk(AmoCondVO[] amoCondVOs,
			String cyear, String cperiod) throws BusinessException {
		InSQLCreator inC = new InSQLCreator();
		try {
			String sql = "select b.pk_wa_classitem oldPk,a.pk_wa_classitem newPk from wa_classitem  a inner join wa_classitem b on a.pk_wa_class=b.pk_wa_class and a.pk_wa_item =b.pk_wa_item where b.pk_wa_classitem in("
					+ inC.getInSQL(amoCondVOs, AmoCondVO.PK_WA_ITEM)
					+ ")  and a.cyear='"
					+ cyear
					+ "' and a.cperiod='"
					+ cperiod + "'";

			List<Object[]> nextPeriodItemPKList = (List<Object[]>) getBaseDao()
					.executeQuery(sql, new ArrayListProcessor());
			for (Iterator<Object[]> i = nextPeriodItemPKList.iterator(); i
					.hasNext();) {
				Object[] nextPeriodItemPk = i.next();
				if (ArrayUtils.isEmpty(nextPeriodItemPk)) {
					continue;
				}
				for (AmoCondVO amoCondVO : amoCondVOs) {
					if (amoCondVO.getPk_wa_item() != null
							&& amoCondVO.getPk_wa_item().equals(
									nextPeriodItemPk[0].toString())) {

						amoCondVO.setPk_wa_item(nextPeriodItemPk[1].toString());
					}
				}
			}

		} finally {
			inC.clear();
		}
	}

	/**
	 * 创建下一期间的数据接口
	 * 
	 * @param waLoginVO
	 * @throws nc.vo.pub.BusinessException
	 */
	private void createNextDataIO(WaClassEndVO waClassEndVO)
			throws nc.vo.pub.BusinessException {
		PeriodStateVO nextPeriodStateVO = getNextPeriodVO(waClassEndVO);
		createNextDataIO(waClassEndVO, nextPeriodStateVO);
	}

	/**
	 * 创建下一期间的数据接口
	 * 
	 * @param waLoginVO
	 * @throws nc.vo.pub.BusinessException
	 */
	public void createNextDataIO(WaClassEndVO waClassEndVO,
			PeriodStateVO nextPeriodStateVO) throws nc.vo.pub.BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select * from hr_dataio_intface where ");
		sqlBuffer.append(" classid = ? and ");
		sqlBuffer.append("  cyear = ? and ");
		sqlBuffer.append("  cperiod = ? ");
		SQLParameter parameter = getCommonParameter(waClassEndVO);
		HrIntfaceVO[] intfaceVos = executeQueryVOs(sqlBuffer.toString(),
				parameter, HrIntfaceVO.class);
		if (!ArrayUtils.isEmpty(intfaceVos)) {
			InSQLCreator inC = new InSQLCreator();
			try {
				sqlBuffer = new StringBuffer();
				sqlBuffer
						.append(" select * from hr_dataintface_b where ifid in (")
						.append(inC.getInSQL(intfaceVos,
								HrIntfaceVO.PK_DATAIO_INTFACE)).append(")");
				FormatItemVO[] formatItems = executeQueryVOs(
						sqlBuffer.toString(), FormatItemVO.class);
				HashMap<Object, FormatItemVO[]> formatItemsMap = (HashMap<Object, FormatItemVO[]>) CommonUtils
						.group2ArrayByField(FormatItemVO.IFID, formatItems);
				sqlBuffer = new StringBuffer();
				sqlBuffer
						.append(" select * from " + DataIOconstant.HR_IFSETTOP
								+ " where ifid in (")
						.append(inC.getInSQL(intfaceVos,
								HrIntfaceVO.PK_DATAIO_INTFACE)).append(")");
				IfsettopVO[] ifsetTops = executeQueryVOs(sqlBuffer.toString(),
						IfsettopVO.class);
				HashMap<Object, IfsettopVO[]> ifsetTopsMap = (HashMap<Object, IfsettopVO[]>) CommonUtils
						.group2ArrayByField(FormatItemVO.IFID, ifsetTops);

				List<AggHrIntfaceVO> aggVOList = new ArrayList<AggHrIntfaceVO>();
				for (int i = 0; i < intfaceVos.length; i++) {
					AggHrIntfaceVO aggVO = new AggHrIntfaceVO();
					intfaceVos[i].setCyear(nextPeriodStateVO.getCyear());
					intfaceVos[i].setCperiod(nextPeriodStateVO.getCperiod());
					FormatItemVO[] childrenItemVO = formatItemsMap
							.get(intfaceVos[i].getPk_dataio_intface());
					IfsettopVO[] childrenIstopVO = null;
					if (ifsetTopsMap != null) {
						childrenIstopVO = ifsetTopsMap.get(intfaceVos[i]
								.getPk_dataio_intface());
					}
					intfaceVos[i].setPk_dataio_intface(null);
					intfaceVos[i].setStatus(VOStatus.NEW);
					aggVO.setParentVO(intfaceVos[i]);
					if (!ArrayUtils.isEmpty(childrenItemVO)) {
						for (int j = 0; j < childrenItemVO.length; j++) {
							childrenItemVO[j].setIfid(null);
							childrenItemVO[j].setPk_dataintface_b(null);
							childrenItemVO[j].setStatus(VOStatus.NEW);
						}
						aggVO.setTableVO("hr_dataintface_b", childrenItemVO);
					}
					if (!ArrayUtils.isEmpty(childrenIstopVO)) {
						for (int j = 0; j < childrenIstopVO.length; j++) {
							childrenIstopVO[j].setIfid(null);
							childrenIstopVO[j].setPk_hr_ifsettop(null);
							childrenIstopVO[j].setStatus(VOStatus.NEW);
						}
						aggVO.setTableVO("hr_ifsettop", childrenIstopVO);
					}
					aggVOList.add(aggVO);
				}
				AggHrIntfaceVO[] aggHrIntfaceVOs = aggVOList
						.toArray(new AggHrIntfaceVO[0]);
				IMDPersistenceService service = MDPersistenceService
						.lookupPersistenceService();
				NCObject[] ncObjs = new NCObject[aggHrIntfaceVOs.length];
				for (int i = 0; i < aggHrIntfaceVOs.length; i++) {
					ncObjs[i] = NCObject.newInstance(aggHrIntfaceVOs[i]);
				}
				service.saveBill(ncObjs);
			} finally {
				inC.clear();
			}
		}
	}

	/**
	 * 处理薪资期间和薪资类别
	 * 
	 * @author zhangg on 2009-12-16
	 * @param waLoginVO
	 * @throws nc.vo.pub.BusinessException
	 */
	private void dealWithPeriodState(WaClassVO waLoginVO)
			throws nc.vo.pub.BusinessException {

		// 修改期间表本期间的记账状态
		PeriodStateVO periodStateVO = getPeriodVO(waLoginVO);
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update wa_periodstate  "); //
		sqlBuffer.append("   set accountmark  = 'Y' , enableflag = 'Y' "); //
		sqlBuffer.append(" where pk_periodstate = ? "); // 11

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(periodStateVO.getPk_periodstate());
		getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);

		// 修改期间子方案的记账状态
		sqlBuffer = new StringBuffer();
		sqlBuffer.append("update wa_periodstate  ");
		sqlBuffer.append("   set accountmark  = 'Y' , enableflag = 'Y' ");
		sqlBuffer
				.append(" where pk_periodstate in(select wa_periodstate.pk_periodstate ");
		sqlBuffer.append("						from wa_inludeclass,wa_periodstate,wa_period ");
		sqlBuffer.append("						where wa_inludeclass.pk_parentclass = ?");
		sqlBuffer
				.append("							and wa_periodstate.pk_wa_class = wa_inludeclass.pk_childclass ");
		sqlBuffer
				.append("							and wa_periodstate.pk_wa_period = wa_period.pk_wa_period ");
		sqlBuffer.append("							and wa_period.cyear = ? ");
		sqlBuffer.append("							and wa_period.cperiod = ? ) ");

		parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());
		getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);

		// 修改薪资类别的最新业务年度、业务期间
		PeriodStateVO nextPeriodStateVO = getNextPeriodVO(waLoginVO);

		sqlBuffer = new StringBuffer();
		sqlBuffer.append("update wa_periodstate  "); //
		sqlBuffer.append("   set  enableflag = 'Y' , isapporve= ?"); //
		sqlBuffer.append(" where pk_periodstate = ? "); // 11

		parameter = new SQLParameter();
		parameter.addParam(periodStateVO.getIsapporve());
		parameter.addParam(nextPeriodStateVO.getPk_periodstate());
		getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);
		// 修改薪资方案的最新期间
		sqlBuffer = new StringBuffer();
		sqlBuffer
				.append("update wa_waclass set  wa_waclass.leaveflag = 'N',wa_waclass.mutipleflag = 'N',wa_waclass.cyear = ?, wa_waclass.cperiod = ? where wa_waclass.pk_wa_class = ? "); // 1
		parameter = new SQLParameter();
		parameter.addParam(nextPeriodStateVO.getCyear());
		parameter.addParam(nextPeriodStateVO.getCperiod());
		parameter.addParam(nextPeriodStateVO.getPk_wa_class());
		getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);

	}

	/**
	 * 
	 * @author zhangg on 2009-12-15
	 * @param context
	 * @param selectClassEndVOs
	 * @return
	 * @throws BusinessException
	 */

	protected WaClassEndVO[] getClassItem2Class(LoginContext context,
			WaClassEndVO[] selectClassEndVOs) throws BusinessException {
		HashMap<String, ArrayList<WaClassItemVO>> needZeromap = new HashMap<String, ArrayList<WaClassItemVO>>();
		;
		HashMap<String, ArrayList<WaClassItemVO>> unZeromap = new HashMap<String, ArrayList<WaClassItemVO>>();
		;
		InSQLCreator isc = new InSQLCreator();
		String temtablename = HRWACommonConstants.WA_TEMP_CLASS;
		String[] fieldname = HRWACommonConstants.CLASS_COLUMN;
		temtablename = isc.insertValues(temtablename, fieldname, fieldname,
				selectClassEndVOs);
		StringBuffer oriZero = new StringBuffer();
		oriZero.append("select wa_classitem.pk_wa_class pkwaclass, wa_item.itemkey waitemkey, "
				+ SQLHelper.getMultiLangNameColumn("wa_classitem.name")
				+ " colname "); // 1
		oriZero.append(" from wa_classitem, wa_item , " + temtablename
				+ " as waclass_table");
		oriZero.append(" where wa_classitem.pk_wa_item = wa_item.pk_wa_item ");
		oriZero.append(" and wa_classitem.pk_wa_class = waclass_table.pk_wa_class");
		oriZero.append(" and wa_classitem.cyear = waclass_table.cyear");
		oriZero.append(" and wa_classitem.cperiod = waclass_table.cperiod");
		oriZero.append(" and wa_item.defaultflag = 'N'");
		// 清零sql
		StringBuffer needZero = new StringBuffer();
		needZero.append(oriZero);
		needZero.append(" and  wa_classitem.clearflag = 'Y' ");//
		needZero.append(" order by pkwaclass ");//
		GeneralVO[] needZeroarr = (GeneralVO[]) getBaseDao().executeQuery(
				needZero.toString(),
				new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
		if (needZeroarr != null && needZeroarr.length > 0) {
			String pk_wa_class = null;
			for (GeneralVO gvo : needZeroarr) {
				WaClassItemVO wcivo = new WaClassItemVO();
				wcivo.setPk_wa_class(gvo.getAttributeValue("pkwaclass")
						.toString());
				wcivo.setItemkey(gvo.getAttributeValue("waitemkey").toString());
				wcivo.setAttributeValue(
						"name" + MultiLangUtil.getCurrentLangSeqSuffix(), gvo
								.getAttributeValue("colname").toString());
				pk_wa_class = wcivo.getPk_wa_class();
				ArrayList<WaClassItemVO> list = needZeromap.get(pk_wa_class);
				if (list == null) {
					list = new ArrayList<WaClassItemVO>();
				}
				list.add(wcivo);
				needZeromap.put(pk_wa_class, list);
			}
		}

		// 非清零
		StringBuffer unZero = new StringBuffer();
		unZero.append(oriZero);
		unZero.append(" and  wa_classitem.clearflag = 'N' ");//
		unZero.append(" order by pkwaclass ");//
		GeneralVO[] unZeroarr = (GeneralVO[]) getBaseDao().executeQuery(
				unZero.toString(),
				new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
		if (unZeroarr != null && unZeroarr.length > 0) {
			String pk_wa_class = null;
			for (GeneralVO gvo : unZeroarr) {
				WaClassItemVO wcivo = new WaClassItemVO();
				wcivo.setPk_wa_class(gvo.getAttributeValue("pkwaclass")
						.toString());
				wcivo.setItemkey(gvo.getAttributeValue("waitemkey").toString());
				wcivo.setAttributeValue(
						"name" + MultiLangUtil.getCurrentLangSeqSuffix(), gvo
								.getAttributeValue("colname").toString());
				// if(gvo.getAttributeValue("pkwaclass").equals(pk_wa_class)){
				// unZClassvos = (WaClassItemVO[]) ArrayUtils.add(unZClassvos,
				// wcivo);
				// }else{
				// if(unZClassvos!=null){
				// unZeromap.put(pk_wa_class, unZClassvos);
				// }
				// unZClassvos = (WaClassItemVO[]) ArrayUtils.add(unZClassvos,
				// wcivo);
				// pk_wa_class = wcivo.getPk_wa_class();
				// }
				pk_wa_class = wcivo.getPk_wa_class();
				ArrayList<WaClassItemVO> list = unZeromap.get(pk_wa_class);
				if (list == null) {
					list = new ArrayList<WaClassItemVO>();
				}
				list.add(wcivo);
				unZeromap.put(pk_wa_class, list);
			}
		}
		for (WaClassEndVO waClassEndVO : selectClassEndVOs) {
			if (null != needZeromap.get(waClassEndVO.getPk_wa_class())) {
				waClassEndVO.setZeroClassItemVOs(needZeromap.get(
						waClassEndVO.getPk_wa_class()).toArray(
						new WaClassItemVO[0]));
			}

			if (null != unZeromap.get(waClassEndVO.getPk_wa_class())) {
				waClassEndVO.setUnZeroClassItemVOs(unZeromap.get(
						waClassEndVO.getPk_wa_class()).toArray(
						new WaClassItemVO[0]));
			}

		}
		return selectClassEndVOs;
		// //*********************************
		// 20130427 lyq
		// StringBuffer sqlBuffer = new StringBuffer();
		// sqlBuffer.append("select wa_item.itemkey, wa_classitem.pk_wa_class,  "+
		// SQLHelper.getMultiLangNameColumn("wa_classitem.name")+ " as name ");
		// // 1
		// sqlBuffer.append("  from wa_classitem, wa_item ");
		// sqlBuffer.append(" where wa_classitem.pk_wa_item = wa_item.pk_wa_item ");
		// sqlBuffer.append("   and wa_classitem.pk_wa_class = ? ");
		// sqlBuffer.append("   and wa_classitem.cyear = ? ");
		// sqlBuffer.append("   and wa_classitem.cperiod = ? ");
		// sqlBuffer.append("   and wa_item.defaultflag = 'N'");
		//
		// for (WaClassEndVO waClassEndVO : selectClassEndVOs) {
		// SQLParameter parameter = getCommonParameter(waClassEndVO);
		// // 清零
		// StringBuffer needZero = new StringBuffer();
		// needZero.append(sqlBuffer);
		// needZero.append("  and     wa_classitem.clearflag = 'Y' ");//
		// waClassEndVO.setZeroClassItemVOs(executeQueryVOs(needZero.toString(),
		// parameter,
		// WaClassItemVO.class));
		//
		// // 待设置清零
		// StringBuffer otherBuffer = new StringBuffer();
		// otherBuffer.append(sqlBuffer);
		// otherBuffer.append("   and wa_classitem.clearflag = 'N' ");
		// waClassEndVO.setUnZeroClassItemVOs(executeQueryVOs(otherBuffer.toString(),
		// parameter,
		// WaClassItemVO.class));
		// }
		// //*********************************
		// return selectClassEndVOs;
	}

	/**
	 * pk_wa_class year, period
	 * 
	 * @author zhangg on 2009-12-2
	 * @param waLoginVO
	 * @return
	 */
	private SQLParameter getCommonParameter(WaClassEndVO waLoginVO) {
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());
		return parameter;
	}

	/**
	 * 逻辑关系： 大于该期间的最小期间， 就是下一期间 Created on 2007-9-3
	 * 
	 * @author zhangg
	 * @param waGlobalVO
	 * @return
	 * @throws BusinessException
	 */
	private PeriodStateVO getNextPeriodVO(WaClassVO waClassEndVO)
			throws BusinessException {

		PeriodStateVO periodVO = null;

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select min(cyear || cperiod) yearperiod "); // 1
		sqlB.append("  from wa_periodstate ");
		sqlB.append(" inner join wa_period on (wa_periodstate.pk_wa_period = ");
		sqlB.append("                         wa_period.pk_wa_period) ");
		sqlB.append(" where pk_wa_class = '" + waClassEndVO.getPk_wa_class()
				+ "' ");
		sqlB.append("   and (cyear || cperiod) > '" + waClassEndVO.getCyear()
				+ waClassEndVO.getCperiod() + "' ");

		YearPeriodSeperatorVO seperator = executeQueryVO(sqlB.toString(),
				YearPeriodSeperatorVO.class);

		if (seperator != null && seperator.getYear() != null) {
			WaClassVO classVO = new WaClassVO();
			classVO.setPk_wa_class(waClassEndVO.getPk_wa_class());
			classVO.setCyear(seperator.getYear());
			classVO.setCperiod(seperator.getPeriod());
			periodVO = getPeriodVO(classVO);
		} else {
			throw new BusinessException(MessageFormat.format(
					ResHelper.getString("6013mend", "06013mend0027")/*
																	 * @res
																	 * "没有得到方案[{0}]的下一个期间！请核查"
																	 */,
					waClassEndVO.getMultilangName()));
		}
		return periodVO;
	}

	/**
	 * 逻辑关系： 小于该期间的最大期间， 就是集团最新版本期间 Created on 2007-9-3
	 * 
	 * @author zhangg
	 * @param waGlobalVO
	 * @return
	 * @throws BusinessException
	 */
	private YearPeriodSeperatorVO getGroupPeriod(WaClassVO groupclassVO)
			throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer
				.append("select max(wa_classitem.cyear || wa_classitem.cperiod) yearperiod "); // 1
		sqlBuffer.append("  from wa_classitem ");
		sqlBuffer.append(" where wa_classitem.pk_wa_class = ? ");
		sqlBuffer
				.append("   and (wa_classitem.cyear || wa_classitem.cperiod) <= ? ");

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(groupclassVO.getPk_wa_class());
		parameter.addParam(groupclassVO.getCyear() + groupclassVO.getCperiod());

		return executeQueryVO(sqlBuffer.toString(), parameter,
				YearPeriodSeperatorVO.class);
	}

	private PeriodStateVO getPeriodVO(WaClassVO waClassVO) throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(WapubDAO.getPeriodViewTable()); // 1
		sqlBuffer.append(" where pk_wa_class = ? ");
		sqlBuffer.append("   and cyear = ? ");
		sqlBuffer.append("   and cperiod = ? ");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waClassVO.getPk_wa_class());
		parameter.addParam(waClassVO.getCyear());
		parameter.addParam(waClassVO.getCperiod());

		return executeQueryVO(sqlBuffer.toString(), parameter,
				PeriodStateVO.class);
	}

	private PeriodStateVO getPeriodVO(String pk_wa_class, String cyear,
			String cperiod) throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(WapubDAO.getPeriodViewTable()); // 1
		sqlBuffer.append(" where pk_wa_class = ? ");
		sqlBuffer.append("   and cyear = ? ");
		sqlBuffer.append("   and cperiod = ? ");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_wa_class);
		parameter.addParam(cyear);
		parameter.addParam(cperiod);

		return executeQueryVO(sqlBuffer.toString(), parameter,
				PeriodStateVO.class);
	}

	/**
	 * 第一次发薪的明细对比 * 小于该期间的最大期间————————必须是已经启用的期间，并且与结账与否没有关系——————就是上一期间。
	 * 
	 * @param waGlobalVO
	 * @return
	 * @throws BusinessException
	 *             2007-12-4 上午11:27:27
	 * @author zhoucx
	 */
	public PeriodStateVO getSubclassPrePeriodVO(final WaClassVO WaClassVO)
			throws BusinessException {

		// 按照父方案查询最近的期间
		PeriodStateVO periodVO = null;
		String pk_prnt_class = NCLocator
				.getInstance()
				.lookup(IWaClass.class)
				.queryParentClasspk(WaClassVO.getPk_wa_class(),
						WaClassVO.getCyear(), WaClassVO.getCperiod());

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select max(cyear || cperiod) yearperiod "); // 1
		sqlB.append("  from wa_periodstate ");
		sqlB.append(" inner join wa_period on (wa_periodstate.pk_wa_period = ");
		sqlB.append("                         wa_period.pk_wa_period) ");
		sqlB.append(" where wa_periodstate.enableflag='Y' and pk_wa_class = '"
				+ pk_prnt_class + "' ");
		sqlB.append("   and (cyear || cperiod) < '" + WaClassVO.getCyear()
				+ WaClassVO.getCperiod() + "' ");

		YearPeriodSeperatorVO seperator = executeQueryVO(sqlB.toString(),
				YearPeriodSeperatorVO.class);

		if (seperator != null && seperator.getYear() != null
				&& seperator.getPeriod() != null) {
			// //不要要改变waClassEndVO 中的年与期间
			periodVO = getPeriodVO(WaClassVO.getPk_wa_class(),
					seperator.getYear(), seperator.getPeriod());
		} else {
			throw new BusinessException(ResHelper.getString("6013mend",
					"06013mend0029")/* @res "没有得到薪资的上一个期间" */);
		}
		return periodVO;
	}

	/**
	 * 查询出第几次（batch）的薪资发放状态
	 * 
	 * @param loginvo
	 * @return
	 * @throws DAOException
	 */
	public PeriodStateVO getChildPeriodVO(WaClassVO loginvo, int batch)
			throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(WapubDAO.getPeriodViewTable()); // 1
		sqlBuffer
				.append(" where pk_wa_class = ( select  pk_childclass from wa_inludeclass where pk_parentclass  = ? and cyear = ? and cperiod = ? and batch = ? )");
		sqlBuffer.append("   and cyear = ? ");
		sqlBuffer.append("   and cperiod = ? ");
		SQLParameter parameter = new SQLParameter();

		parameter.addParam(loginvo.getPk_prnt_class());
		/**
		 * 业务年与业务期间
		 */
		parameter.addParam(loginvo.getCyear());
		parameter.addParam(loginvo.getCperiod());
		parameter.addParam(batch);

		/**
		 * 业务年与业务期间
		 */
		parameter.addParam(loginvo.getCyear());
		parameter.addParam(loginvo.getCperiod());
		return executeQueryVO(sqlBuffer.toString(), parameter,
				PeriodStateVO.class);
	}

	/**
	 * 小于该期间的最大期间————————必须是已经启用的期间，并且与结账与否没有关系——————就是上一期间。
	 * 
	 * @param waGlobalVO
	 * @return
	 * @throws BusinessException
	 *             2007-12-4 上午11:27:27
	 * @author zhoucx
	 */
	public PeriodStateVO getPrePeriodVO(final WaClassVO waClassEndVO)
			throws BusinessException {

		PeriodStateVO periodVO = null;
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select max(cyear || cperiod) yearperiod "); // 1
		sqlB.append("  from wa_periodstate ");
		sqlB.append(" inner join wa_period on (wa_periodstate.pk_wa_period = ");
		sqlB.append("                         wa_period.pk_wa_period) ");
		sqlB.append(" where wa_periodstate.enableflag='Y' and pk_wa_class = '"
				+ waClassEndVO.getPk_wa_class() + "' ");
		sqlB.append("   and (cyear || cperiod) < '" + waClassEndVO.getCyear()
				+ waClassEndVO.getCperiod() + "' ");

		YearPeriodSeperatorVO seperator = executeQueryVO(sqlB.toString(),
				YearPeriodSeperatorVO.class);

		if (seperator != null && seperator.getYear() != null
				&& seperator.getPeriod() != null) {
			// //不要要改变waClassEndVO 中的年与期间
			periodVO = getPeriodVO(waClassEndVO.getPk_wa_class(),
					seperator.getYear(), seperator.getPeriod());
		} else {
			throw new BusinessException(ResHelper.getString("6013mend",
					"06013mend0029")/* @res "没有得到薪资的上一个期间" */);
		}
		return periodVO;
	}

	/**
	 * 
	 * @author zhangg on 2009-12-16
	 * @param context
	 * @param actionType
	 * @return
	 * @throws BusinessException
	 */
	protected WaClassEndVO[] getWaClassVOs(LoginContext context,
			String actionType) throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("    select distinct wa_waclass.pk_wa_class, "); // 1
		sqlBuffer.append("                     "
				+ SQLHelper.getMultiLangNameColumn("org_orgs.name")
				+ "  orgname, "); // 2
		sqlBuffer.append("                    wa_waclass.code, "); // 3
		sqlBuffer.append("                     "
				+ SQLHelper.getMultiLangNameColumn("wa_waclass.name")
				+ " name, "); // 2
		sqlBuffer.append("                    wa_waclass.cyear, "); // 3
		sqlBuffer.append("                    wa_waclass.cperiod, "); // 4
		sqlBuffer.append("                    wa_waclass.pk_org, "); // 4
		sqlBuffer.append("                    wa_waclass.mutipleflag , "); // 4
		sqlBuffer.append("                    wa_waclass.collectflag, "); // 5
		sqlBuffer.append("                    wa_waclass.currid, "); // 5
		sqlBuffer.append("                    wa_waclass.ts, "); // 5
		sqlBuffer.append("                    wa_waclass.taxcurrid "); // 5
		sqlBuffer
				.append("      from wa_waclass, wa_periodstate, wa_period, org_orgs ");
		sqlBuffer
				.append("     where wa_waclass.pk_wa_class = wa_periodstate.pk_wa_class ");
		sqlBuffer
				.append("       and wa_periodstate.pk_wa_period = wa_period.pk_wa_period ");
		sqlBuffer.append("       and wa_waclass.cyear = wa_period.cyear ");
		sqlBuffer.append("       and wa_waclass.pk_org = org_orgs.pk_org");
		sqlBuffer.append("       and wa_waclass.cperiod = wa_period.cperiod ");
		sqlBuffer.append("       and wa_waclass.showflag = 'Y' ");// 必须是用户自己定义的方案
		sqlBuffer.append("       and wa_periodstate.accountmark = 'N' ");

		if (actionType != null) {
			if (actionType.equals(JZ)) {
				sqlBuffer.append("       and wa_periodstate.payoffflag = 'Y' ");// 最新期间已经发放并且未结帐
			} else if (actionType.equals(FJ)) {
				// 根据登录的用户和时间获得本公司下有权限的可以反结帐的薪资类别主键 --本期间已经结帐， 下一个期间未审核<BR>
				// 逻辑关系： 本期间是最大的已经结帐的期间， 并且大于该期间的最小期间未审核 Created on 2007-9-4
				sqlBuffer
						.append("       and wa_periodstate.checkflag = 'N' and wa_waclass.stopflag='N' ");// 该期间未审核

				// 上一期间存在数据
				sqlBuffer
						.append("       and ((wa_waclass.cyear || wa_waclass.cperiod) > (wa_waclass.startyear || wa_waclass.startperiod))");
				sqlBuffer
						.append("      and not exists(select 1 from wa_inludeclass a,wa_periodstate b, wa_period  c ");
				sqlBuffer.append("	where a.pk_childclass = b.pk_wa_class ");
				sqlBuffer.append("	and b.pk_wa_period = c.pk_wa_period ");
				sqlBuffer.append("	and wa_waclass.cyear = c.cyear  ");
				sqlBuffer.append("	and wa_waclass.cperiod = c.cperiod ");
				sqlBuffer
						.append("	and a.pk_parentclass = wa_waclass.pk_wa_class ");
				sqlBuffer.append("	and b.checkflag = 'Y') ");

				// 20151021 xiejie3 测试要求当薪资方案下有审核数据时，不能进行反结账。begin
				sqlBuffer
						.append("  AND NOT EXISTS ( SELECT  1  FROM  wa_data   where wa_data.pk_wa_class=wa_waclass.pk_wa_class ");
				sqlBuffer.append(" and wa_data.cyear =wa_period.cyear ");
				sqlBuffer.append("  and wa_data.cperiod=wa_period.cperiod  ");
				sqlBuffer.append(" and wa_data.checkflag ='Y' ) ");
				// end

			}
		}

		// TODO 添加人员权限:数据本组织的，用户有权限查看的薪资方案
		sqlBuffer
				.append("   and wa_waclass.pk_group = '"
						+ context.getPk_group()
						+ "' and wa_waclass.pk_group <> wa_waclass.pk_org and wa_waclass.pk_wa_class in ("
						+ ClassPowerUtil.getClassower(context) + ")  ");
		sqlBuffer.append("       order by  orgname, wa_waclass.code");
		return executeQueryVOs(sqlBuffer.toString(), WaClassEndVO.class);
	}

	/**
	 * Created on 2007-9-3
	 * 
	 * @author zhangg
	 * @param waGlobalVO
	 * @return
	 * @throws BusinessException
	 */
	private boolean isMonthEndEnabled(WaClassVO waClassVO)
			throws BusinessException {
		PeriodStateVO periodStateVO = getPeriodVO(waClassVO);
		if (periodStateVO != null
				&& !periodStateVO.getAccountmark().booleanValue()
				&& periodStateVO.getPayoffflag().booleanValue()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * --本期间已经结帐， 下一个期间未审核<BR>
	 * 逻辑关系： 本期间是最大的已经结帐的期间， 并且大于该期间的最小期间未审核 Created on 2007-9-3
	 * 
	 * @author zhangg
	 * @param waGlobalVO
	 * @return
	 * @throws BusinessException
	 */
	private boolean isMonthEndReverseEnabled(WaClassVO waClassVO)
			throws BusinessException {
		// 不再区分多次与非多次，所有方案均做为多次处理
		// 如果是多次发薪，则查看最新业务年度里，第一次发薪是否已经审核
		// if(waClassVO.getMutipleflag().booleanValue()){
		// 业务期间第一次发薪是否存在审核的数据
		PeriodStateVO statevo = new PeriodStateVO();
		statevo.setCyear(waClassVO.getCyear());
		statevo.setCperiod(waClassVO.getCperiod());
		WaLoginVO waLoginVO = new WaLoginVO();
		waLoginVO.setPk_wa_class(waClassVO.getPk_wa_class());
		waLoginVO.setPk_group(waClassVO.getPk_group());
		waLoginVO.setPk_org(waClassVO.getPk_org());
		waLoginVO.setPeriodVO(statevo);
		return !(new WapubDAO().isChildClassExistCheckData(waLoginVO, 1));
		// }

		// //普通薪资类别
		// PeriodStateVO periodStateVO = getPeriodVO(waClassVO);
		// if (periodStateVO != null &&
		// !periodStateVO.getAccountmark().booleanValue()
		// && !periodStateVO.getCheckflag().booleanValue()) {
		// // 上一期间存在数据
		//
		// return true;
		// } else {
		// return false;
		// }

	}

	public void deleteNewLeaveClass(WaClassVO waClassVO) throws DAOException {
		String sql = "SELECT pk_wa_class " + "FROM wa_waclass "
				+ "WHERE NOT EXISTS (SELECT pk_wa_class "
				+ "					FROM wa_data "
				+ "					WHERE wa_data.pk_wa_class = wa_waclass.pk_wa_class)"
				+ "	AND pk_wa_class IN(	SELECT pk_childclass "
				+ "						FROM wa_inludeclass "
				+ "						WHERE pk_parentclass = ? " + "							AND cyear = ? "
				+ "							AND cperiod = ? )";
		SQLParameter param = new SQLParameter();
		param.addParam(waClassVO.getPk_wa_class());
		param.addParam(waClassVO.getCyear());
		param.addParam(waClassVO.getCperiod());
		Object result = getBaseDao().executeQuery(sql, param,
				new ColumnProcessor());
		if (result == null) {
			return;
		}
		String pk_wa_class = result.toString();
		param.clearParams();
		param.addParam(pk_wa_class);
		sql = "delete from wa_classitem where pk_wa_class = ?";
		getBaseDao().executeUpdate(sql, param);
		sql = "delete from wa_waclass where pk_wa_class = ?";
		getBaseDao().executeUpdate(sql, param);
	}

	/**
	 * 
	 * @author zhangg on 2009-12-16
	 * @param waLoginVO
	 * @return
	 * @throws nc.vo.pub.BusinessException
	 */
	private void monthEndReverseSingle(WaClassEndVO waLoginVO,
			boolean processPreperiod) throws nc.vo.pub.BusinessException {

		deleteKeyData(waLoginVO);

		deleteSecondaryData(waLoginVO);

		resetPeriodState(waLoginVO);
		if (processPreperiod) {
			updatePrePeriodstate(waLoginVO);
			// 完成月末结账 V5 遗留，暂不删除
			updateCreateCorp(waLoginVO);
		}

		// 处理集团方案的反结账不同步问题
		synchronizeGroupClass4Reverse(waLoginVO);

	}

	/**
	 * 清空当前期间的数据（防止重复结账， 重复启用停用，导致下一个期间出现多余数据）
	 * 目的是为了让下一个期间回复到初始状态。初始状态下：数据为空，期间状态为没有启用
	 * 
	 * @param waParentLoginVO
	 * @throws BusinessException
	 * @throws nc.vo.pub.BusinessException
	 */
	private void deleteKeyData(WaClassEndVO[] endVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(endVOs))
			return;
		String[] fields = new String[] { WaClassEndVO.PK_WA_CLASS,
				WaClassEndVO.CYEAR, WaClassEndVO.CPERIOD };
		String tableName = new InSQLCreator().insertValues("wa_waclass_tmp1",
				fields, fields, endVOs);
		BaseDAO dao = new BaseDAO();
		String delSql_datas = "delete from wa_datas where wa_datas.pk_wa_data in (select wa_data.pk_wa_data from wa_data where exists(select 1 from "
				+ tableName
				+ " tmptable where wa_data.pk_wa_class = tmptable.pk_wa_class and wa_data.cyear = tmptable.cyear and wa_data.cperiod = tmptable.cperiod))";
		String delSql_data = "delete from wa_data where exists(select 1 from "
				+ tableName
				+ " tmptable where wa_data.pk_wa_class = tmptable.pk_wa_class and wa_data.cyear = tmptable.cyear and wa_data.cperiod = tmptable.cperiod) ";
		String delSql_redata = "delete from wa_redata where exists(select 1 from "
				+ tableName
				+ " tmptable where wa_redata.pk_wa_class = tmptable.pk_wa_class and wa_redata.cyear = tmptable.cyear and wa_redata.cperiod = tmptable.cperiod) ";
		String delSql_item = "delete from wa_classitem where exists(select 1 from "
				+ tableName
				+ " tmptable where wa_classitem.pk_wa_class = tmptable.pk_wa_class and wa_classitem.cyear = tmptable.cyear and wa_classitem.cperiod = tmptable.cperiod) ";
		dao.executeUpdate(delSql_datas);
		dao.executeUpdate(delSql_data);
		dao.executeUpdate(delSql_redata);
		dao.executeUpdate(delSql_item);
	}

	private void deleteKeyData(WaClassEndVO waLoginVO) throws BusinessException {

		// 删除核心数据 wa_datas、wa_data、wa_classitem
		IPersistenceUpdate persistence = NCLocator.getInstance().lookup(
				IPersistenceUpdate.class);
		HashMap<String, SQLParameter> updateHashMap = new LinkedHashMap<String, SQLParameter>();

		String sqlwa_datas = "delete from wa_datas where wa_datas.pk_wa_data in (select wa_data.pk_wa_data from wa_data where wa_data.pk_wa_class = ? and wa_data.cyear = ? and wa_data.cperiod = ?)";
		updateHashMap.put(sqlwa_datas, getCommonParameter(waLoginVO));

		String sqlwa_data = "delete from wa_data where wa_data.pk_wa_class = ? and cyear= ? and cperiod = ? ";
		updateHashMap.put(sqlwa_data, getCommonParameter(waLoginVO));

		String sqlwa_redata = "delete from wa_redata where pk_wa_class = ? and cyear= ? and cperiod = ? ";
		updateHashMap.put(sqlwa_redata, getCommonParameter(waLoginVO));

		String sqlwa_classItem = "delete from wa_classitem where  pk_wa_class= ? and cyear=? and cperiod = ? ";
		updateHashMap.put(sqlwa_classItem, getCommonParameter(waLoginVO));

		persistence.executeSQLs(updateHashMap.keySet().toArray(new String[0]),
				updateHashMap.values().toArray(new SQLParameter[0]));

	}

	/**
	 * 删除次要的数据，如果产生错误，也不要抛出异常，仅仅做日志就可。
	 * 
	 * @param waLoginVO
	 */
	private void deleteSecondaryData(WaClassEndVO waLoginVO) {

		try {
			// 反结账处理薪资条数据
			monthEndReverse4Payslip(waLoginVO);
			// 反结账处理数据接口
			monthEndReverse4DataIO(waLoginVO);
			// 反结账处理分摊方案
			monthEndReverse4AmoScheme(waLoginVO);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 修改期间状态， 将该期间置为未计算，未启用
	 * 
	 * @param waParentLoginVO
	 * @throws BusinessException
	 * @throws nc.vo.pub.BusinessException
	 */
	private void resetPeriodState(WaClassEndVO[] endVOs)
			throws BusinessException {
		if (ArrayUtils.isEmpty(endVOs))
			return;
		String[] fields = new String[] { WaClassEndVO.PK_WA_CLASS,
				WaClassEndVO.CYEAR, WaClassEndVO.CPERIOD };
		String tableName = new InSQLCreator().insertValues("wa_waclass_tmp3",
				fields, fields, endVOs);
		StringBuffer updateSql = new StringBuffer();
		updateSql.append("update wa_periodstate "); //
		updateSql.append("   set caculateflag = 'N', "); //
		updateSql.append("       accountmark  = 'N', "); //
		updateSql.append("       checkflag    = 'N', "); //
		updateSql.append("       payoffflag   = 'N', "); //
		updateSql.append("       vpaycomment  = null, "); //
		updateSql.append("       cpaydate     = null, "); //
		updateSql.append("       cpreclassid  = null, "); //
		updateSql.append("       enableflag  = 'N', "); //
		updateSql.append("       isapproved   = 'N' "); //
		updateSql.append(" where wa_periodstate.pk_periodstate in (");
		updateSql
				.append(" select wa_periodstate.pk_periodstate from wa_periodstate inner join wa_period on wa_periodstate.pk_wa_period = wa_period.pk_wa_period");
		updateSql
				.append(" where exists(select 1 from "
						+ tableName
						+ " tmptable where wa_periodstate.pk_wa_class = tmptable.pk_wa_class and wa_period.cyear = tmptable.cyear and wa_period.cperiod = tmptable.cperiod))");
		new BaseDAO().executeUpdate(updateSql.toString());
	}

	private void resetPeriodState(WaClassEndVO waLoginVO)
			throws BusinessException {

		IPersistenceUpdate persistence = NCLocator.getInstance().lookup(
				IPersistenceUpdate.class);

		// 修改期间状态， 将该期间置为未计算
		PeriodStateVO periodStateVO = getPeriodVO(waLoginVO);
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update wa_periodstate "); //
		sqlBuffer.append("   set caculateflag = 'N', "); //
		sqlBuffer.append("       accountmark  = 'N', "); //
		sqlBuffer.append("       checkflag    = 'N', "); //
		sqlBuffer.append("       payoffflag   = 'N', "); //
		sqlBuffer.append("       vpaycomment  = null, "); //
		sqlBuffer.append("       cpaydate     = null, "); //
		sqlBuffer.append("       cpreclassid  = null, "); //
		sqlBuffer.append("       enableflag  = 'N', "); //
		sqlBuffer.append("       isapproved   = 'N' "); //
		sqlBuffer.append(" where pk_periodstate = ? "); //

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(periodStateVO.getPk_periodstate());

		persistence.executeSQLs(new String[] { sqlBuffer.toString() },
				new SQLParameter[] { parameter });
	}

	/**
	 * 删除子方案期间状态
	 * 
	 * @param waLoginVO
	 */
	private void deletePeriodState(WaClassEndVO[] endVOs)
			throws BusinessException {
		if (ArrayUtils.isEmpty(endVOs))
			return;
		String[] fields = new String[] { WaClassEndVO.PK_WA_CLASS,
				WaClassEndVO.CYEAR, WaClassEndVO.CPERIOD };
		String tableName = new InSQLCreator().insertValues("wa_waclass_tmp2",
				fields, fields, endVOs);
		String delSql = "delete from wa_periodstate where wa_periodstate.pk_periodstate in (";
		delSql += " select wa_periodstate.pk_periodstate from wa_periodstate inner join wa_period on wa_periodstate.pk_wa_period = wa_period.pk_wa_period";
		delSql += " where exists(select 1 from "
				+ tableName
				+ " tmptable where wa_periodstate.pk_wa_class = tmptable.pk_wa_class and wa_period.cyear = tmptable.cyear and wa_period.cperiod = tmptable.cperiod))";
		new BaseDAO().executeUpdate(delSql);
	}

	private void deletePeriodState(WaClassEndVO waLoginVO)
			throws BusinessException {
		IPersistenceUpdate persistence = NCLocator.getInstance().lookup(
				IPersistenceUpdate.class);

		// 删除子方案期间状态
		PeriodStateVO periodStateVO = getPeriodVO(waLoginVO);
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("delete from  wa_periodstate "); //
		sqlBuffer.append(" where pk_periodstate = ? "); //

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(periodStateVO.getPk_periodstate());

		persistence.executeSQLs(new String[] { sqlBuffer.toString() },
				new SQLParameter[] { parameter });
	}

	/**
	 * 修改上一期间的状态为尚未结账
	 * 
	 * @param waParentLoginVO
	 * @throws BusinessException
	 * @throws nc.vo.pub.BusinessException
	 */

	private void updatePrePeriodstate(WaClassEndVO waLoginVO)
			throws BusinessException {
		// 修改期间表上一期间的记账状态
		IPersistenceUpdate persistence = NCLocator.getInstance().lookup(
				IPersistenceUpdate.class);

		PeriodStateVO prePeriodStateVO = getPrePeriodVO(waLoginVO);
		String preCyear = prePeriodStateVO.getCyear();
		String prePeriod = prePeriodStateVO.getCperiod();

		HashMap<String, SQLParameter> updateHashMap = new LinkedHashMap<String, SQLParameter>();
		if (prePeriodStateVO != null) {
			// 恢复父方案期间状态
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("update wa_periodstate  "); //
			sqlBuffer.append("   set accountmark  = 'N' "); //
			sqlBuffer.append(" where pk_periodstate = ? "); // 11
			SQLParameter parameter = new SQLParameter();
			parameter.addParam(prePeriodStateVO.getPk_periodstate());
			getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);
			// 恢复子方案期间状态
			prePeriodStateVO.setApprovetype(UFBoolean.FALSE);
			prePeriodStateVO.setPayoffflag(UFBoolean.FALSE);
			sqlBuffer = new StringBuffer();
			sqlBuffer.append("update wa_periodstate  "); //
			sqlBuffer.append("   set  accountmark  = 'N' "); // 5
			sqlBuffer
					.append(" where pk_periodstate in(select wa_periodstate.pk_periodstate ");
			sqlBuffer
					.append("						from wa_inludeclass,wa_periodstate,wa_period ");
			sqlBuffer.append("						where wa_inludeclass.pk_parentclass = ?");
			sqlBuffer
					.append("							and wa_periodstate.pk_wa_class = wa_inludeclass.pk_childclass ");
			sqlBuffer
					.append("							and wa_periodstate.pk_wa_period = wa_period.pk_wa_period ");
			sqlBuffer.append("							and wa_period.cyear = ? ");
			sqlBuffer.append("							and wa_period.cperiod = ? ) ");
			parameter = new SQLParameter();
			parameter.addParam(waLoginVO.getPk_wa_class());
			parameter.addParam(preCyear);
			parameter.addParam(prePeriod);
			updateHashMap.put(sqlBuffer.toString(), parameter);

			StringBuffer flagSqlBuffer = new StringBuffer();
			if (prePeriodStateVO.getClasstype().equals(
					WACLASSTYPE.NORMALCLASS.getValue())) {
				flagSqlBuffer
						.append(" ,wa_waclass.leaveflag='N',wa_waclass.mutipleflag='N' ");
			} else if (prePeriodStateVO.getClasstype().equals(
					WACLASSTYPE.LEAVECLASS.getValue())) {
				flagSqlBuffer
						.append(" ,wa_waclass.leaveflag='Y',wa_waclass.mutipleflag='Y' ");
			} else if (prePeriodStateVO.getClasstype().equals(
					WACLASSTYPE.PARENTCLASS.getValue())) {
				flagSqlBuffer
						.append(" ,wa_waclass.leaveflag='N',wa_waclass.mutipleflag='Y' ");
			}
			// 修改薪资类别的最新业务年度、业务期间, 是否使用审批流标志
			sqlBuffer = new StringBuffer();
			sqlBuffer
					.append("update wa_waclass set wa_waclass.cyear = ?, wa_waclass.cperiod = ? "); // 1
			sqlBuffer.append(flagSqlBuffer);
			sqlBuffer.append("where wa_waclass.pk_wa_class = ? "); // 1
			parameter = new SQLParameter();
			parameter.addParam(preCyear);
			parameter.addParam(prePeriod);
			parameter.addParam(prePeriodStateVO.getPk_wa_class());
			updateHashMap.put(sqlBuffer.toString(), parameter);
		}
		persistence.executeSQLs(updateHashMap.keySet().toArray(new String[0]),
				updateHashMap.values().toArray(new SQLParameter[0]));

	}

	private void monthEndReverseMutiple(WaClassEndVO waParentLoginVO,
			boolean processPreperiod) throws nc.vo.pub.BusinessException {

		WaClassEndVO parentVO = (WaClassEndVO) waParentLoginVO.clone();
		WaClassEndVO backupvo = (WaClassEndVO) waParentLoginVO.clone();

		// 首先处理父方案
		monthEndReverseSingle(waParentLoginVO, processPreperiod);// (waParentLoginVO);

		WaClassEndVO[] ChildVOs = getFirstAndLeaveClass(parentVO);

		if (ChildVOs == null) {
			// 删除管理系表
			deleteChildRelation(backupvo);
			return;
		}
		/** 20150825 优化效率 --与delSubClassList相关的4行注释掉的代码验证后可启用优化效率 */
		List<WaClassEndVO> delSubClassList = new ArrayList<WaClassEndVO>();
		// 20150902 shenliangc 这个循环处理多次方案的子方案，解决SONAR问题。 begin
		// 第一次子方案如果没有可用的上一期间则删除，如果有这反结账处理；
		// 第二次以后的子方案直接删除。
		ArrayList<String> pkList = new ArrayList<String>();
		for (WaClassEndVO firstChildVO : ChildVOs) {
			if (firstChildVO.getBatch().equals(Integer.valueOf(1))) {
				pkList.add(firstChildVO.getPk_wa_class());
			}
		}
		HashMap<String, String> prePeriodMap = new HashMap<String, String>();
		if (!pkList.isEmpty()) {
			String inSQL = new InSQLCreator().getInSQL(pkList
					.toArray(new String[0]));
			prePeriodMap = this.getPrePeriodMap(inSQL);
		}
		for (WaClassEndVO firstChildVO : ChildVOs) {
			if (firstChildVO.getBatch().equals(Integer.valueOf(1))) {
				// YearPeriodSeperatorVO seperator =
				// getPrePeriodSeperatorVO(firstChildVO);
				// if (seperator == null || seperator.getYear() == null
				// || seperator.getPeriod() == null) {
				if (prePeriodMap.isEmpty()
						|| !prePeriodMap.containsKey(firstChildVO
								.getPk_wa_class())) {
					delSubClassList.add(firstChildVO);
					// deleteSubClass(firstChildVO);
				} else {
					monthEndReverseSingle(firstChildVO, processPreperiod);
				}
			} else {
				delSubClassList.add(firstChildVO);
				// deleteSubClass(firstChildVO);
			}
		}
		deleteSubClass(delSubClassList.toArray(new WaClassEndVO[0]));
		// 20150902 shenliangc 这个循环处理多次方案的子方案，解决SONAR问题。 end
		// 删除管理系表
		deleteChildRelation(backupvo);
	}

	@SuppressWarnings({ "unchecked", "serial" })
	private HashMap<String, String> getPrePeriodMap(String inSQL)
			throws BusinessException {
		String sql = "SELECT MAX(wa_period.cyear || wa_period.cperiod) yearperiod, WA_PERIODSTATE.PK_WA_CLASS "
				+ " FROM "
				+ " wa_period INNER JOIN WA_PERIODSTATE ON wa_period.PK_WA_PERIOD = WA_PERIODSTATE.PK_WA_PERIOD "
				+ " INNER JOIN WA_waclass on WA_PERIODSTATE.pk_wa_class = WA_waclass.PK_WA_CLASS "
				+ " where wa_periodstate.enableflag='Y' and (wa_period.cyear || wa_period.cperiod) <  (WA_waclass.CYEAR || WA_waclass.cperiod ) "
				+ " and WA_PERIODSTATE.pk_Wa_class in ( "
				+ inSQL
				+ " ) "
				+ " GROUP BY WA_PERIODSTATE.pk_Wa_class ";

		return (HashMap<String, String>) this.getBaseDao().executeQuery(sql,
				new ResultSetProcessor() {
					public Object handleResultSet(ResultSet rs)
							throws SQLException {
						HashMap<String, String> prePeriodMap = new HashMap<String, String>();
						while (rs.next()) {
							prePeriodMap.put(rs.getString(2), rs.getString(1));
						}
						return prePeriodMap;
					}
				});
	}

	public void deleteSubClass(WaClassEndVO[] waClassEndVOs)
			throws BusinessException {
		deleteKeyData(waClassEndVOs);
		deletePeriodState(waClassEndVOs);
		getBaseDao().deleteVOArray(waClassEndVOs);
	}

	public void deleteSubClass(WaClassEndVO waClassEndVO)
			throws BusinessException {
		deleteKeyData(waClassEndVO);
		deletePeriodState(waClassEndVO);
		getBaseDao().deleteVO(waClassEndVO);
	}

	public YearPeriodSeperatorVO getPrePeriodSeperatorVO(
			final WaClassVO waClassEndVO) throws BusinessException {

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select max(cyear || cperiod) yearperiod "); // 1
		sqlB.append("  from wa_periodstate ");
		sqlB.append(" inner join wa_period on (wa_periodstate.pk_wa_period = ");
		sqlB.append("                         wa_period.pk_wa_period) ");
		sqlB.append(" where wa_periodstate.enableflag='Y' and pk_wa_class = '"
				+ waClassEndVO.getPk_wa_class() + "' ");
		sqlB.append("   and (cyear || cperiod) < '" + waClassEndVO.getCyear()
				+ waClassEndVO.getCperiod() + "' ");

		return executeQueryVO(sqlB.toString(), YearPeriodSeperatorVO.class);
	}

	/**
	 * 反结账处理
	 * 
	 * @param waClassEndVO
	 * @throws nc.vo.pub.BusinessException
	 */
	private void monthEndReverse(WaClassEndVO waClassEndVO)
			throws nc.vo.pub.BusinessException {
		clearBuisinessData(waClassEndVO, true);

	}

	private void clearBuisinessData(WaClassEndVO waClassEndVO,
			boolean processPreperiod) throws nc.vo.pub.BusinessException {
		// if(waClassEndVO.getMutipleflag().booleanValue()){
		monthEndReverseMutiple(waClassEndVO, processPreperiod);
		// }else{
		// monthEndReverseSingle(waClassEndVO,processPreperiod);
		// }

	}

	/**
	 * 反结账处理薪资条数据
	 * 
	 * @param waClassEndVO
	 * @throws BusinessException
	 */
	private void monthEndReverse4Payslip(WaClassEndVO waClassEndVO)
			throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select * from wa_payslip where ");
		sqlBuffer.append(" pk_wa_class = ? and ");
		sqlBuffer.append("  accyear = ? and ");
		sqlBuffer.append("  accmonth = ? ");
		SQLParameter parameter = getCommonParameter(waClassEndVO);

		PayslipVO[] payslipVos = executeQueryVOs(sqlBuffer.toString(),
				parameter, PayslipVO.class);

		if (!ArrayUtils.isEmpty(payslipVos)) {
			String in = FormatVO.formatArrayToString(payslipVos,
					PayslipVO.PK_PAYSLIP, "'");
			getBaseDao().deleteVOArray(payslipVos);
			sqlBuffer = new StringBuffer();
			sqlBuffer.append(" pk_payslip in (").append(in).append(")");
			getBaseDao().deleteByClause(PayslipItemVO.class,
					sqlBuffer.toString());
		}
	}

	private void monthEndReverse4AmoScheme(WaClassEndVO waClassEndVO)
			throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select * from wa_amoscheme where ");
		sqlBuffer.append(" classid = ? and ");
		sqlBuffer.append("  cyear = ? and ");
		sqlBuffer.append("  cperiod = ? ");
		SQLParameter parameter = getCommonParameter(waClassEndVO);

		AmoSchemeVO[] amoSchemeVOs = executeQueryVOs(sqlBuffer.toString(),
				parameter, AmoSchemeVO.class);

		if (!ArrayUtils.isEmpty(amoSchemeVOs)) {
			String in = FormatVO.formatArrayToString(amoSchemeVOs,
					AmoSchemeVO.PK_AMOSCHEME, "'");
			getBaseDao().deleteVOArray(amoSchemeVOs);

			sqlBuffer = new StringBuffer();
			sqlBuffer.append(" pk_amoscheme in (").append(in).append(")");

			getBaseDao().deleteByClause(AmobaseVO.class, sqlBuffer.toString());

			getBaseDao().deleteByClause(AmoCondVO.class, sqlBuffer.toString());

			getBaseDao()
					.deleteByClause(AmoFactorVO.class, sqlBuffer.toString());

		}
	}

	/**
	 * 反结账处理数据接口
	 * 
	 * @param waClassEndVO
	 * @throws BusinessException
	 */
	private void monthEndReverse4DataIO(WaClassEndVO waClassEndVO)
			throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select * from hr_dataio_intface where ");
		sqlBuffer.append(" classid = ? and ");
		sqlBuffer.append("  cyear = ? and ");
		sqlBuffer.append("  cperiod = ? ");
		SQLParameter parameter = getCommonParameter(waClassEndVO);

		HrIntfaceVO[] intfaceVos = executeQueryVOs(sqlBuffer.toString(),
				parameter, HrIntfaceVO.class);

		if (!ArrayUtils.isEmpty(intfaceVos)) {
			String in = FormatVO.formatArrayToString(intfaceVos,
					HrIntfaceVO.PK_DATAIO_INTFACE, "'");
			sqlBuffer = new StringBuffer();
			sqlBuffer.append(" ifid in (").append(in).append(")");
			getBaseDao().deleteByClause(FormatItemVO.class,
					sqlBuffer.toString());
			getBaseDao().deleteByClause(IfsettopVO.class, sqlBuffer.toString());
			getBaseDao().deleteVOArray(intfaceVos);
		}
	}

	public static final String UPDATE = "update";

	/**
	 * 结帐
	 * 
	 * @author zhangg on 2009-12-16
	 * @param waLoginVO
	 * @return
	 * @throws nc.vo.pub.BusinessException
	 */
	private void monthEnd(WaClassEndVO waLoginVO)
			throws nc.vo.pub.BusinessException {

		// if(waLoginVO.getMutipleflag().booleanValue()){
		monthEndMutiple(waLoginVO);
		// }else{
		// monthEndSingle(waLoginVO);
		// }

	}

	private ILocker locker;

	public ILocker getLocker() {
		if (locker == null) {
			locker = new SimpleDocLocker();
		}

		return locker;
	}

	private void monthEndSingle(WaClassEndVO waLoginVO)
			throws nc.vo.pub.BusinessException {

		// 创建下一期间的wa_data, tax
		createNextPeriodDatas(waLoginVO, waLoginVO);

		// 创建下一期间的wa_classitem
		createNextPeriodItems(waLoginVO, waLoginVO);

		// 创建下一期间的特殊调整人员
		createNextPeriodDataSep(waLoginVO);

		// 创建下一期间的薪资发放条
		createNextPeriodPaySlip(waLoginVO);

		// 创建下一期间的数据接口
		createNextDataIO(waLoginVO);

		// 创建下一期间的薪资分摊方案数据
		createNextPeriodAmoScheme(waLoginVO);

		// 处理薪资期间
		dealWithPeriodState(waLoginVO);

		// 人员信息变化
		updatePsnInfo(waLoginVO);

		// 如果是集团分配的方案，则集团方案也进行期末处理。
		synchronizeGroupClass(waLoginVO);
		// guoqt 校验总账是否启用
		boolean isEnable = PubEnv.isModuleStarted(PubEnv.getPk_group(),
				PubEnv.MODULE_GL);
		// 20151102 xiejie3 增加业务参数控制 begin
		// 是否进行“总账校验”
		UFBoolean typevalue = SysinitAccessor.getInstance().getParaBoolean(
				waLoginVO.getPk_org(), ParaConstant.CHECK_BEFORE_CLOSEACCBOOK);

		// 总账没有启用，直接返回
		if (isEnable && null != typevalue && typevalue.booleanValue()) {
			try {
				// guoqt
				// 得到当前会计期间
				PeriodStateVO thisPeriodStateVO = getPeriodVO(waLoginVO);
				// 20151015 xiejie3
				// 判断此财务组织下的所有方案是否都已经结账,当所有方案都结账才会向org_closeacc表插入状态数据。begin
				// org_closeacc，财务系统结账状态查询会查这张表，条件是 moduleid 模块编号 closeorgpks
				// 财务组织主键 pk_accperiodmonth 会计期间主键，当isendacc为Y时，认为完全结账。
				// 每个 moduleid 模块编号 closeorgpks 财务组织主键 pk_accperiodmonth
				// 会计期间主键下只会插入一条数据。所以需要等待此条件下的所有方案全部结账才插入数据。
				// 现在先进行校验，但是有两种业务场景无法不能处理，1.当 01 期间
				// 财务组织A下有2个薪资方案，第一个方案期末处理时，会校验还有一个方案未结账，所以不会把org_closeacc中sendacc为Y，第二次校验通过，将sendacc为Y，但此后，又加了一个薪资方案，财务组织为A，但此时查询薪资状态还是为Y。
				// 2.条件和1相同，第一个方案期末处理时，会校验还有一个方案未结账，所以不会把org_closeacc中sendacc为Y，此时将第二个方案删除，此后sendacc将一直无值，或者为N。
				// 解决方案：这两种业务，和其类似的业务，目前用方案解决，将此财务组织下的方案的某一个反结账，再次期末处理即可。
				boolean isWaCloseAccBook = WaCloseAccBook(
						thisPeriodStateVO.getCaccyear(),
						thisPeriodStateVO.getCaccperiod(),
						waLoginVO.getPk_org());
				if (isWaCloseAccBook) {
					// 根据当前会计期间查找会计期间主键
					String[] pk_accperiodmonth = getPeriod(
							thisPeriodStateVO.getCaccyear(),
							thisPeriodStateVO.getCaccperiod());
					// 根据薪资方案的财务组织查找账簿
					WaFiorgVO[] fiorgvos = queryWaFiorgByClass(waLoginVO
							.getPk_wa_class());
					if (!ArrayUtils.isEmpty(fiorgvos)) {
						// 结账后，需要将结账信息传入uap的关帐模块
						NCLocator
								.getInstance()
								.lookup(ICloseAccPubServicer.class)
								.account("6013", waLoginVO.getPk_org(),
										fiorgvos[0].getAccountBookId(),
										pk_accperiodmonth[0]);
					}
				}
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}
		}

		// updateCreateCorp(waLoginVO);
	}

	// 20151015 xiejie3
	// 判断此财务组织下的所有方案是否都已经结账,当所有方案都结账才会向org_closeacc表插入状态数据。begin
	// org_closeacc，财务系统结账状态查询会查这张表，条件是 moduleid 模块编号 closeorgpks 财务组织主键
	// pk_accperiodmonth 会计期间主键，当isendacc为Y时，认为完全结账。
	// 每个 moduleid 模块编号 closeorgpks 财务组织主键 pk_accperiodmonth
	// 会计期间主键下只会插入一条数据。所以需要等待此条件下的所有方案全部结账才插入数据。
	// 此方法查询 会计期间内 ，与该财务组织相关的所有 薪资方案是否已经期末处理完毕
	// 现在先进行校验，但是有两种业务场景无法不能处理，1.当 01 期间
	// 财务组织A下有2个薪资方案，第一个方案期末处理时，会校验还有一个方案未结账，所以不会把org_closeacc中sendacc为Y，第二次校验通过，将sendacc为Y，但此后，又加了一个薪资方案，财务组织为A，但此时查询薪资状态还是为Y。
	// 2.条件和1相同，第一个方案期末处理时，会校验还有一个方案未结账，所以不会把org_closeacc中sendacc为Y，此时将第二个方案删除，此后sendacc将一直无值，或者为N。
	// 解决方案：这两种业务，和其类似的业务，目前用方案解决，将此财务组织下的方案的某一个反结账，再次期末处理即可。
	public boolean WaCloseAccBook(String caccyear, String caccperiod,
			String financeorg) throws BusinessException {
		// 查询 会计期间内 ，与该财务组织相关的所有 薪资方案是否已经期末处理完毕
		StringBuilder sbd = new StringBuilder();
		sbd.append(" select "
				+ SQLHelper.getMultiLangNameColumn("org_orgs.name")
				+ "  as orgname ,"
				+ SQLHelper.getMultiLangNameColumn("wa_waclass.name")
				+ " as waclassname  from org_orgs,wa_periodstate,wa_period, wa_waclass ,pub_sysinit ");
		sbd.append("  where wa_periodstate.pk_wa_class = wa_waclass.pk_wa_class ");
		sbd.append(" and wa_periodstate.pk_wa_period  = wa_period.pk_wa_period ");
		sbd.append("	and wa_waclass.pk_org = org_orgs.pk_org  and wa_waclass.pk_org= pub_sysinit.pk_org and pub_sysinit.initcode ='"
				+ ParaConstant.CHECK_BEFORE_CLOSEACCBOOK
				+ "' and pub_sysinit.VALUE = 'Y' ");
		sbd.append("	and  wa_period.caccyear = ? and wa_period.caccperiod = ? ");
		sbd.append(" and wa_periodstate.enableflag = 'Y' and wa_periodstate.accountmark = 'N' ");
		sbd.append(" and wa_waclass.pk_wa_class in (  select pk_wa_class   from wa_class_fiorg where pk_financeorg = ? ) ");
		SQLParameter param = new SQLParameter();
		param.addParam(caccyear);
		param.addParam(caccperiod);
		param.addParam(financeorg);
		GeneralVO[] vos = this.executeQueryVOs(sbd.toString(), param,
				GeneralVO.class);
		if (ArrayUtils.isEmpty(vos)) {
			Logger.error("薪资方案都期末处理完毕，可以关账了");
			return true;
		} else {
			sbd = new StringBuilder();
			for (int i = 0; i < vos.length; i++) {
				sbd.append(MessageFormat.format(ResHelper.getString(
						"60130paydata", "060130paydata0466")
				/* @res "人力资源组织:{0}-薪资方案:{1}," */, vos[i]
						.getAttributeValue("orgname"), vos[i]
						.getAttributeValue("waclassname")));
			}
			sbd.append(ResHelper.getString("60130paydata", "060130paydata0467")
			/* @res "尚未进行期末处理，无法关账" */);

			Logger.error(sbd.toString());
			return false;
		}

	}

	// end

	/**
	 * 根据薪资方案的财务组织查找账簿
	 * 
	 * @throws DAOException
	 */
	public WaFiorgVO[] queryWaFiorgByClass(String classpk) throws DAOException {
		String sql = "  select pk_class_fiorg,pk_wa_class,pk_financeorg,value as accountBookId from wa_class_fiorg inner join pub_sysinit on wa_class_fiorg.pk_financeorg = pub_sysinit.pk_org where  wa_class_fiorg.pk_wa_class = ? and initcode = '"
				+ ParaConstant.GL034
				+ "' and  ( pub_sysinit.value is not null and pub_sysinit.value <> '~') ";
		SQLParameter param2 = new SQLParameter();
		param2.addParam(classpk);
		WaFiorgVO[] tempvos = executeQueryVOs(sql, param2, WaFiorgVO.class);
		return tempvos;
	}

	/**
	 * 根据当前会计期间查找会计期间主键
	 * 
	 * @throws DAOException
	 */
	public String[] getPeriod(String accyear, String accperiod)
			throws DAOException {
		String sql = " select apm.pk_accperiodmonth from bd_accperiodmonth apm,"
				+ "bd_accperiod ap,bd_accperiodscheme scheme where apm.pk_accperiod=ap.pk_accperiod  "
				+ " and scheme.pk_accperiodscheme=ap.pk_accperiodscheme and apm.yearmth = ?  and scheme.code='0001'";
		String[] re = new String[1];
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(accyear + "-" + accperiod);
		re = (String[]) this.getBaseDao().executeQuery(sql, parameter,
				new ColumnProcessor() {
					public Object handleResultSet(ResultSet rs)
							throws SQLException {
						String[] re = new String[1];
						if (rs.next()) {
							re[0] = rs.getString(1);
						}
						return re;
					}
				});

		return re;
	}

	private void synchronizeGroupClass(WaClassEndVO waLoginVO)
			throws BusinessException {
		// 首先查看是否集团分配的方案
		IWaClass WaClass = NCLocator.getInstance().lookup(IWaClass.class);
		WaClassVO vo = WaClass.queryGroupClassByOrgClass(waLoginVO
				.getPk_wa_class());
		if (vo == null) {
			return;
		}

		// 如果是，则看集团的方案与当前子方案是不是同一个期间
		if (needMonthEndGroupClass(waLoginVO, vo)) {
			// 如果是,则集团的方案要进行月末处理
			monthEndGroupClassvo(vo);
		}

	}

	/**
	 * 集团方案进行期末处理
	 * 
	 * @throws BusinessException
	 */
	private void monthEndGroupClassvo(WaClassVO waLoginVO)
			throws BusinessException {

		// 创建下一期间的wa_classitem
		createNextPeriodItems(waLoginVO, waLoginVO);

		// 处理薪资期间
		dealWithPeriodState(waLoginVO);

	}

	private boolean needMonthEndGroupClass(WaClassVO orgvo, WaClassVO groupvo) {
		String orgPeriod = orgvo.getCyear() + orgvo.getCperiod();

		String groupPeriod = groupvo.getCyear() + groupvo.getCperiod();

		// 当前组织的薪资期间>= 集团的当前薪资期间
		if (orgPeriod.compareTo(groupPeriod) >= 0) {
			return true;
		} else {
			return false;
		}

	}

	private void monthEndMutiple(WaClassEndVO waParentLoginVO)
			throws nc.vo.pub.BusinessException {

		// WaLoginVO waLoginVO = ((IWaPub)
		// NCLocator.getInstance().lookup(IWaPub.class.getName())).getWaclassVOWithState(waParentLoginVO.convert2LoginVO());
		// 将数据重新汇总一遍
		// if(WaLoginVOHelper.isMultiClass(waParentLoginVO)){
		// NCLocator.getInstance().lookup(IPaydataManageService.class).collectWaTimesData(waLoginVO);
		// }
		// 首先处理父方案
		monthEndSingle(waParentLoginVO);

		/*
		 * if(WaLoginVOHelper.isParentClass(waParentLoginVO)){ //查询出第一个子方案
		 * WaClassEndVO firstChildVO = createFirstTimesClass(waParentLoginVO);
		 * //然后处理子方案 monthEndCrossClass(waParentLoginVO,firstChildVO);
		 * 
		 * //向 父子关系表中添加父子关系 addChildRelation(waParentLoginVO,firstChildVO);
		 * 
		 * //尝试为子方案设置系统项目的公式.即使失败，也不能抛出异常。
		 * regenerateSystemFormula(firstChildVO); }
		 * if(WaLoginVOHelper.isLeaveClass(waLoginVO)){
		 * changeMulti2Normalclass(waLoginVO); }
		 */

	}

	/*
	 * private void changeMulti2Normalclass(WaClassVO vo) throws DAOException{
	 * String sql =
	 * " update wa_waclass set leaveflag = 'N',mutipleflag = 'N' where"+
	 * " pk_wa_class = '"+vo.getPk_wa_class()+"'";
	 * getBaseDao().executeUpdate(sql); sql =
	 * " update wa_periodstate set classtype = "
	 * +WACLASSTYPE.NORMALCLASS.getValue()+" where"+
	 * " pk_wa_class = '"+vo.getPk_wa_class()+"' and exists " +
	 * "(select wa_period.pk_wa_period  from wa_period  " +
	 * "where wa_period.pk_wa_period = wa_periodstate.pk_wa_period" +
	 * " and wa_period.cyear =  '"
	 * +vo.getCyear()+"' and wa_period.cperiod =  '"+vo.getCperiod()+"' and  " +
	 * "wa_periodstate.pk_wa_class =  '"+vo.getPk_wa_class()+"')";
	 * getBaseDao().executeUpdate(sql); }
	 */

	private void regenerateSystemFormula(WaClassEndVO firstChildVO) {
		try {
			PeriodStateVO nextPeriodStateVO = getNextPeriodVO(firstChildVO);
			NCLocator
					.getInstance()
					.lookup(IClassItemManageService.class)
					.regenerateSystemFormula(firstChildVO.getPk_org(),
							firstChildVO.getPk_wa_class(),
							nextPeriodStateVO.getCyear(),
							nextPeriodStateVO.getCperiod());

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
	}

	private void addChildRelation(WaClassEndVO sourceLoginVO,
			WaClassEndVO destLoginVO) throws BusinessException {
		PeriodStateVO nextPeriodStateVO = getNextPeriodVO(destLoginVO);
		WaInludeclassVO WaInludeclassVO = new WaInludeclassVO();
		WaInludeclassVO.setStatus(VOStatus.NEW);
		WaInludeclassVO.setCyear(nextPeriodStateVO.getCyear());
		WaInludeclassVO.setCperiod(nextPeriodStateVO.getCperiod());
		WaInludeclassVO.setPk_group(destLoginVO.getPk_group());
		WaInludeclassVO.setPk_org(destLoginVO.getPk_org());
		WaInludeclassVO.setPk_parentclass(sourceLoginVO.getPk_wa_class());
		WaInludeclassVO.setPk_childclass(destLoginVO.getPk_wa_class());
		WaInludeclassVO.setBatch(1);

		getBaseDao().insertVO(WaInludeclassVO);

	}

	private void deleteChildRelation(WaClassEndVO sourceLoginVO)
			throws BusinessException {

		StringBuilder sbd = new StringBuilder();
		sbd.append(" delete  from wa_inludeclass where pk_parentclass = ? and cyear = ? and cperiod = ?");
		SQLParameter para = getCommonParameter(sourceLoginVO);
		getBaseDao().executeUpdate(sbd.toString(), para);

	}

	/**
	 * 查询出第一个子方案的薪资方案vo
	 * 
	 * @param waLoginVO
	 * @return
	 * @throws BusinessException
	 */
	private WaClassEndVO[] getFirstAndLeaveClass(WaClassEndVO waLoginVO)
			throws BusinessException {

		String sql = "select wa_waclass.*,wa_inludeclass.batch "
				+ " from wa_waclass inner join wa_inludeclass on wa_inludeclass.pk_childclass = wa_waclass.pk_wa_class "
				+ "where  wa_inludeclass.pk_parentclass = ? "
				+ "	and wa_inludeclass.cyear = ? "
				+ "  and wa_inludeclass.cperiod = ? ";

		SQLParameter param = new SQLParameter();
		param.addParam(waLoginVO.getPk_wa_class());
		param.addParam(waLoginVO.getCyear());
		param.addParam(waLoginVO.getCperiod());
		WaClassVO[] vos = executeQueryVOs(sql, param, WaClassVO.class);
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		List volist = new ArrayList<WaClassEndVO>();
		for (WaClassVO vo : vos) {
			WaClassEndVO endvo = new WaClassEndVO();

			String[] propertyNames = endvo.getAttributeNames();
			for (int i = 0; i < propertyNames.length; i++) {
				endvo.setAttributeValue(propertyNames[i],
						vo.getAttributeValue(propertyNames[i]));
			}
			endvo.setPk_prnt_class(waLoginVO.getPk_wa_class());
			volist.add(endvo);
		}
		// 复制
		return (WaClassEndVO[]) volist.toArray(new WaClassEndVO[volist.size()]);

	}

	/**
	 * 薪资方案跨类别结账（父方案到子方案）
	 * 
	 * @param waLoginVO
	 * @throws nc.vo.pub.BusinessException
	 */
	private void monthEndCrossClass(WaClassEndVO sourceLoginVO,
			WaClassEndVO destLoginVO) throws nc.vo.pub.BusinessException {

		// 薪资发放数据结转，从父方案结转到子方案
		createNextPeriodDatas(sourceLoginVO, destLoginVO);
		// 薪资发放项目结转，从父方案结转到子方案
		createNextPeriodItems(sourceLoginVO, destLoginVO);

		// 创建下一期间的特殊调整人员.多次发薪暂时不支持

		// 结转薪资条。 不需要

		// 处理子方案薪资期间
		dealWithPeriodState(destLoginVO);

		// 人员信息变化
		updatePsnInfo(destLoginVO);

		// 完成月末结账 ， 不需要
	}

	private void createNextPeriodDatas(WaClassEndVO sourceLoginVO,
			WaClassEndVO destLoginVO) throws nc.vo.pub.BusinessException {
		// 处理wa_data, dataz, dataf, tax
		// 从Wa_data得到所有的本月薪资数据VO
		/*
		 * DataVO dataVOCon = new DataVO();
		 * dataVOCon.setPk_wa_class(sourceLoginVO.getPk_wa_class());
		 * dataVOCon.setCyear(sourceLoginVO.getCyear());
		 * dataVOCon.setCperiod(sourceLoginVO.getCperiod());
		 */
		String pk_wa_class = sourceLoginVO.getPk_wa_class();
		String cyear = sourceLoginVO.getCyear();
		String cperiod = sourceLoginVO.getCperiod();
		String condition = " wa_data.pk_wa_class ='"
				+ pk_wa_class
				+ "' and  wa_data.cyear='"
				+ cyear
				+ "'  and wa_data.cperiod ='"
				+ cperiod
				+ "' "
				+ " and not exists(select 1 from wa_data wa_data2 "
				+ " where wa_data2.pk_psnjob = wa_data.pk_psnjob and wa_data2.cyear=wa_data.cyear and wa_data2.cperiod=wa_data.cperiod "
				+ " and wa_data2.pk_wa_class in(select pk_childclass from wa_inludeclass where pk_parentclass = '"
				+ pk_wa_class + "' " + " and cyear='" + cyear
				+ "' and cperiod ='" + cperiod + "'and batch >100)  )";
		DataVO[] dataVOs = retrieveAppendableVOsByClause(DataVO.class,
				condition);

		if (dataVOs == null) {
			// throw new
			// BusinessException(ResHelper.getString("6013mend","06013mend0032")/*@res
			// "类别名称为:"*/ + sourceLoginVO.getMultilangName() +
			// ResHelper.getString("6013mend","06013mend0033")/*@res
			// "的类别,没有添加人员, 不能够空结."*/);
			return;
		}

		//
		WaClassItemVO[] transferItemVOs = getTransferItems(sourceLoginVO);

		// 预形成主键， 为多个表引用
		String strOids[] = OidGenerator.getInstance().nextOid(
				OidGenerator.GROUP_PK_CORP, dataVOs.length);
		PeriodStateVO nextPeriodStateVO = getNextPeriodVO(destLoginVO);

		// boolean destvoMutipldFlag =
		// destLoginVO.getMutipleflag().booleanValue();

		for (int i = 0; i < dataVOs.length; i++) {
			DataVO dataVO = dataVOs[i];
			dataVO.setPk_wa_data(strOids[i]);
			// 设置转结 .目标方案是父方案不结转。
			// if(!destvoMutipldFlag){
			transferValue(dataVO, transferItemVOs);
			// }

			WaClassItemVO[] zeroItem = sourceLoginVO.getZeroClassItemVOs();

			// 清空用户自定义项目
			if (zeroItem != null) {
				for (WaClassItemVO waClassItemVO : zeroItem) {
					Object value = DataVOUtils.isDigitsAttribute(waClassItemVO
							.getItemkey()) ? UFDouble.ZERO_DBL : null;
					dataVO.setAttributeValue(waClassItemVO.getItemkey(), value);
				}
			}
			// 清空系统项目
			dataVO = clearSysitemValue(dataVO);

			// 更新期间
			dataVO.setCyear(nextPeriodStateVO.getCyear());
			dataVO.setCperiod(nextPeriodStateVO.getCperiod());
			dataVO.setCyearperiod(nextPeriodStateVO.getCyear()
					+ nextPeriodStateVO.getCperiod());

			// 更新薪资方案pk
			dataVO.setPk_wa_class(destLoginVO.getPk_wa_class());
			// 更新状态
			dataVO.setCheckflag(UFBoolean.FALSE);
			dataVO.setCaculateflag(UFBoolean.FALSE);
			// 更新银企直连标示
			dataVO.setFipendflag(UFBoolean.FALSE);
			// 20150729 xiejie3 补丁合并,NCdp205423540薪资期末处理清空发放时间发放原因,begin
			// 20150708 shenliangc 期末处理要将发放日期和发放原因字段清空
			dataVO.setCpaydate(null);
			dataVO.setVpaycomment(null);
			// end
		}
		// 形成下个月的数据
		getBaseDao().insertVOArrayWithPK(dataVOs);

		// 更新下个月银行帐户信息
		PayfileDAO dao = new PayfileDAO();
		dao.updateAccount(dataVOs);

	}

	private DataVO clearSysitemValue(DataVO dataVO) {
		String[] systemitemkey = WaItemConstant.SysItemKey;
		for (int i = 0; i < systemitemkey.length; i++) {
			dataVO.setAttributeValue(systemitemkey[i], UFDouble.ZERO_DBL);
		}
		return dataVO;

	}

	/**
	 * 
	 * @author zhangg on 2010-6-18
	 * @param waLoginVO
	 */
	private void createNextPeriodDataSep(WaClassEndVO waLoginVO)
			throws nc.vo.pub.BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_datas.caculatevalue, "); // 1
		sqlBuffer.append("       wa_datas.notes, "); // 2
		sqlBuffer.append("       wa_datas.to_next, "); // 3
		sqlBuffer.append("       wa_datas.value, "); // 4
		sqlBuffer.append("       next_wa_data.pk_wa_data, "); // 5
		sqlBuffer.append("       next_classitem.pk_wa_classitem "); // 6
		sqlBuffer.append("  from wa_datas, ");
		sqlBuffer.append("       wa_data, ");
		sqlBuffer.append("       wa_classitem, ");
		sqlBuffer.append("       wa_data next_wa_data, ");
		sqlBuffer.append("       wa_classitem next_classitem ");
		sqlBuffer
				.append(" where next_classitem.pk_wa_item = wa_classitem.pk_wa_item ");
		sqlBuffer
				.append("   and next_classitem.pk_wa_class = wa_classitem.pk_wa_class ");
		sqlBuffer.append("   and next_wa_data.pk_psndoc = wa_data.pk_psndoc ");
		sqlBuffer
				.append("   and next_wa_data.pk_wa_class = wa_data.pk_wa_class ");
		sqlBuffer
				.append("   and wa_classitem.pk_wa_classitem = wa_datas.pk_wa_classitem ");
		sqlBuffer.append("   and wa_datas.pk_wa_data = wa_data.pk_wa_data ");
		sqlBuffer.append("   and wa_datas.to_next = 'Y' ");
		sqlBuffer.append("   and wa_data.pk_wa_class = ? ");
		sqlBuffer.append("   and wa_data.cyear = ? ");
		sqlBuffer.append("   and wa_data.cperiod = ? ");
		sqlBuffer.append("   and wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append("   and wa_classitem.cyear = ? ");
		sqlBuffer.append("   and wa_classitem.cperiod = ? ");
		sqlBuffer.append("   and next_wa_data.pk_wa_class = ? ");
		sqlBuffer.append("   and next_wa_data.cyear = ? ");
		sqlBuffer.append("   and next_wa_data.cperiod = ? ");
		sqlBuffer.append("   and next_classitem.pk_wa_class = ? ");
		sqlBuffer.append("   and next_classitem.cyear = ? ");
		sqlBuffer.append("   and next_classitem.cperiod = ? ");

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());

		PeriodStateVO periodStateVO = getNextPeriodVO(waLoginVO);
		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(periodStateVO.getCyear());
		parameter.addParam(periodStateVO.getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(periodStateVO.getCyear());
		parameter.addParam(periodStateVO.getCperiod());

		DataSVO[] dataSVOs = executeQueryVOs(sqlBuffer.toString(), parameter,
				DataSVO.class);

		if (dataSVOs != null) {
			getBaseDao().insertVOArray(dataSVOs);
		}
	}

	/**
	 * 多类别结帐
	 * 
	 * @author zhangg on 2009-12-16
	 * @param classVOs
	 * @throws BusinessException
	 */
	protected void multMonthEnd(WaClassEndVO[] classVOs, String actionType)
			throws BusinessException {

		for (WaClassEndVO waClassEndVO : classVOs) {

			// 锁薪资方案
			new SimpleDocLocker().lock(UPDATE, waClassEndVO);

			// 版本校验（时间戳校验）
			BDVersionValidationUtil.validateSuperVO(waClassEndVO);

			if (actionType.equals(JZ)) {
				if (!isMonthEndEnabled(waClassEndVO)) {
					throw new BusinessException(waClassEndVO.getMultilangName()
							+ ResHelper.getString("6013mend", "06013mend0034")/*
																			 * @res
																			 * "薪资方案数据发生变化, 不允许结帐！"
																			 */);
				}

				// 首先清空下一个期间数据。
				WaClassEndVO waClassEndNextVO = (WaClassEndVO) waClassEndVO
						.clone();
				PeriodStateVO nextPeriodVO = getNextPeriodVO(waClassEndVO);
				waClassEndNextVO.setCyear(nextPeriodVO.getCyear());
				waClassEndNextVO.setCperiod(nextPeriodVO.getCperiod());

				clearBuisinessData(waClassEndNextVO, false);

				monthEnd(waClassEndVO);
			} else if (actionType.equals(FJ)) {
				if (!isMonthEndReverseEnabled(waClassEndVO)) {
					throw new BusinessException(waClassEndVO.getMultilangName()
							+ ResHelper.getString("6013mend", "06013mend0035")/*
																			 * @res
																			 * "薪资方案数据发生变化, 不允许反结帐！"
																			 */);
				}
				monthEndReverse(waClassEndVO);
			}
		}
		WaCacheUtils.synCache(WaClassVO.TABLE_NAME);
		WaCacheUtils.synCache(WaClassItemVO.TABLE_NAME);
	}

	/**
	 * 
	 * @author zhangg on 2009-12-16
	 * @param waLoginVO
	 */
	private void updateCreateCorp(WaClassEndVO waLoginVO)
			throws nc.vo.pub.BusinessException {

		Logger.error("开始反结账");
		// 总账是否安装
		boolean isEnable = PubEnv.isModuleStarted(PubEnv.getPk_group(),
				PubEnv.MODULE_GL);
		// 没有安装，直接返回
		if (!isEnable) {
			Logger.error("没有安装GL，直接返回");
			return;
		}

		// shenliangc 20141013 期末处理反结账，没有将结账状态修改，仍为“已结账”，
		// 原因是“总账校验”业务参数为“否”，到这里就直接返回了。
		// 与需求和测试确认，此处不需要校验“总账校验”业务参数。
		// 是否进行“总账校验”
		// UFBoolean typevalue =
		// SysinitAccessor.getInstance().getParaBoolean(waLoginVO.getPk_org(),
		// ParaConstant.CHECK_BEFORE_CLOSEACCBOOK);

		// if(!typevalue.booleanValue()){
		// Logger.error("HRWA015没有启用，直接返回");
		// return ;
		// }

		WaFiorgVO[] waFiorgVO = NCLocator.getInstance().lookup(IWaClass.class)
				.queryCheckedWaFiorgByClass(waLoginVO.getPk_wa_class());

		// 如果有账簿已经关账，则不允许反结账。
		if (ArrayUtils.isEmpty(waFiorgVO)) {
			Logger.error("该薪资方案关联的财务组织都没有设置账簿关账检查，可以反结账");
			return;
		}

		// 得到会计期间
		PeriodStateVO prePeriodStateVO = getPrePeriodVO(waLoginVO);
		String accyear = prePeriodStateVO.getCaccyear();
		String accperiod = prePeriodStateVO.getCaccperiod();

		boolean closed = checkAccountbookClosed(
				waFiorgVO[0].getAccountBookId(), accyear + '-' + accperiod);

		if (closed) {
			Logger.error("总账已经关账，不能进行反结账");
			throw new BusinessException(ResHelper.getString("60130paydata",
					"060130paydata0500")/* @res "总账已经关账，不能进行反结账" */);
		}

		Logger.error("所有校验都过了，可以反结账 。 ");
		try {
			// guoqt根据上一期间的会计期间查询上一期间的会计期间主键
			String[] pk_accperiodmonth = getPeriod(
					prePeriodStateVO.getCaccyear(),
					prePeriodStateVO.getCaccperiod());
			// 根据薪资方案的财务组织查找账簿
			WaFiorgVO[] fiorgvos = queryWaFiorgByClass(waLoginVO
					.getPk_wa_class());
			// shenliangc
			// 如果没有财务组织则返回
			if (ArrayUtils.isEmpty(fiorgvos)) {
				return;
			}
			String[] closeorgpks = new String[fiorgvos.length];
			for (int i = 0; i < fiorgvos.length; i++) {
				closeorgpks[i] = fiorgvos[i].getAccountBookId();
			}
			// 查询上一期间的结账状态
			CloseAccBookVO[] hasPKCabvos = this.queryVOsByCloseOrgPks(
					closeorgpks, pk_accperiodmonth[0], "6013");
			// 有数据则反结账，没数据证明是历史期间，不处理
			if (!ArrayUtils.isEmpty(hasPKCabvos)) {
				// 需要将结账信息传入uap的关帐模块
				NCLocator
						.getInstance()
						.lookup(ICloseAccPubServicer.class)
						.reAccount("6013", waLoginVO.getPk_org(),
								fiorgvos[0].getAccountBookId(),
								pk_accperiodmonth[0]);
			}

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}

	}

	/**
	 * 查询薪资总账结账状态数据
	 * 
	 * @throws BusinessException
	 */
	private CloseAccBookVO[] queryVOsByCloseOrgPks(String[] closeorgpks,
			final String pk_accperiodmonth, final String module)
			throws BusinessException {
		InSqlBatchCaller caller = new InSqlBatchCaller(closeorgpks);
		List<CloseAccBookVO> voList = new ArrayList<CloseAccBookVO>();
		try {
			voList = (List<CloseAccBookVO>) caller
					.execute(new IInSqlBatchCallBack() {
						List<CloseAccBookVO> result = new ArrayList<CloseAccBookVO>();

						@Override
						public Object doWithInSql(String inSql)
								throws BusinessException, SQLException {
							String condition = " closeorgpks in " + inSql
									+ " and pk_accperiodmonth = '"
									+ pk_accperiodmonth + "' and moduleid = '"
									+ module + "'";
							Collection<CloseAccBookVO> c = new BaseDAO()
									.retrieveByClause(CloseAccBookVO.class,
											condition);
							if (c != null && c.size() > 0)
								result.addAll(c);
							return result;
						}
					});
		} catch (SQLException e) {
			nc.bs.logging.Logger.debug(e.getMessage());
		}
		return voList.toArray(new CloseAccBookVO[voList.size()]);
	}

	/**
	 * 核查多个财务组织的总账账簿是否有关账；任何一个账簿关账，则返回true。 都没有关账才返回false
	 * 
	 * @param fiorgPKs
	 *            财务组织pks
	 * @param accyear
	 *            会计年度
	 * @param accperiod
	 *            会计期间
	 * @return
	 * @throws BusinessException
	 */
	private boolean checkAccountbookClosed(String accountBookId, String period)
			throws BusinessException {
		return NCLocator.getInstance().lookup(ICloseAccQryPubServicer.class)
				.isCloseByAccountBookId(accountBookId, period);

	}

	/**
	 * 发生组织关系调动的人员,应该删掉.
	 * 
	 * 期末处理时，根据最新变动更新下个期间人员
	 * 
	 * @author zhangg on 2009-12-16
	 * @param waLoginVO
	 */
	private void updatePsnInfo(WaClassEndVO waLoginVO)
			throws nc.vo.pub.BusinessException {

		/**
		 * 同一个人员,同种工作类型(主职,兼职类型),同一个组织关系,主职工作记录更新为最新的任职记录，
		 * 兼职的不处理——因为主职同一时间只有一个，兼职可以有多个
		 * 
		 * 同一个人员,同种工作类型(主职,兼职类型),最新的工作记录发生变动,不处理 ,让用户通过变动人员手动处理.
		 */

		PeriodStateVO nextPeriodStateVO = getNextPeriodVO(waLoginVO);
		SQLParameter param = new SQLParameter();
		String sql = "";

		sql = "update wa_data set wa_data.pk_psnjob = (select max(hi_psnjob.pk_psnjob) "

				+ "from hi_psnjob where hi_psnjob.pk_psndoc = wa_data.pk_psndoc and hi_psnjob.assgid = wa_data.assgid "
				// 同一个人员
				+ "and wa_data.pk_psnorg = hi_psnjob.pk_psnorg "
				// 根据期间查询工作记录
				+ " and hi_psnjob.begindate<=?"
				+ " and (hi_psnjob.enddate>=? or hi_psnjob.enddate is null) ) "
				+ "where wa_data.pk_wa_class=? and wa_data.cyear = ? and wa_data.cperiod = ?    "
				+ "and exists ( select 1 "
				// 期间内有新的工作记录。
				+ "from hi_psnjob where hi_psnjob.pk_psndoc = wa_data.pk_psndoc and hi_psnjob.assgid = wa_data.assgid "
				+ "and wa_data.pk_psnorg = hi_psnjob.pk_psnorg "

				// 根据期间查询工作记录
				+ " and hi_psnjob.begindate<=?"
				+ " and (hi_psnjob.enddate>=? or hi_psnjob.enddate is null) ) ";

		param = new SQLParameter();
		// param.addParam(nextPeriodStateVO.getCenddate());
		// guoqt 根据参数算出截止变化的日期，如0则表示永不变化，即结账到下期间时人员取本期间最新的工作记录；
		// 31则表示立即变化，即结账到下期间时取下个期间内最新的工作记录；参数为15，则结账到下期间时工作记录取下期间1号到15号内最新的工作记录
		Integer paramValue = SysinitAccessor.getInstance().getParaInt(
				waLoginVO.getPk_org(), ParaConstant.CHANGE_DATE);
		UFLiteralDate enddate = nextPeriodStateVO.getCstartdate().getDateAfter(
				paramValue - 1);
		if (enddate.after(nextPeriodStateVO.getCenddate())) {
			enddate = nextPeriodStateVO.getCenddate();
		}
		// guoqt本期间
		PeriodStateVO prePeriodStateVO = getPeriodVO(waLoginVO);
		param.addParam(enddate);
		// guoqt月末最后一天变动NCdp205255067
		param.addParam(prePeriodStateVO.getCstartdate());
		param.addParam(waLoginVO.getPk_wa_class());
		param.addParam(nextPeriodStateVO.getCyear());
		param.addParam(nextPeriodStateVO.getCperiod());
		param.addParam(nextPeriodStateVO.getCenddate());
		// guoqt 期间范围是从本期间开始日期开始
		param.addParam(prePeriodStateVO.getCstartdate());

		getBaseDao().executeUpdate(sql, param);
		GeneralVO gVO = new GeneralVO();
		gVO.setAttributeValue("type", "wa");
		gVO.setAttributeValue("pkClass", waLoginVO.getPk_wa_class());
		gVO.setAttributeValue("cyear", nextPeriodStateVO.getCyear());
		gVO.setAttributeValue("cperiod", nextPeriodStateVO.getCperiod());
		WaBmFileOrgVO[] vos = NCLocator.getInstance()
				.lookup(IWaBmfileQueryService.class).getFinanceOrgs(gVO);
		InSQLCreator isc = new InSQLCreator();

		String[] strAttrs = { WaBmFileOrgVO.PK_PSNJOB,
				WaBmFileOrgVO.PK_FINANCEORG, WaBmFileOrgVO.PK_FINANCEDEPT,
				WaBmFileOrgVO.FIPORGVID, WaBmFileOrgVO.FIPDEPTVID,
				WaBmFileOrgVO.PK_LIABILITYORG, WaBmFileOrgVO.PK_LIABILITYDEPT,
				WaBmFileOrgVO.LIBDEPTVID };
		String tablename = isc.insertValues("wabm_temp_wabmfileorg", strAttrs,
				strAttrs, vos);

		// 2015-09-28 zhousze 期末处理更新财务组织等字段 begin
		String sqlfi = " ";
		// 处理多版本
		if (getBaseDao().getDBType() == DBUtil.SQLSERVER) {
			// if(tablename !=null){
			// sqlfi=",pk_financeorg = (select pk_financeorg from "+tablename+" where wa_data.pk_psnjob = "+tablename+".pk_psnjob) "
			// +
			// ",pk_financedept = (select pk_financedept from "+tablename+" where wa_data.pk_psnjob = "+tablename+".pk_psnjob) "
			// +
			// ",fiporgvid = (select fiporgvid from "+tablename+" where wa_data.pk_psnjob = "+tablename+".pk_psnjob) "
			// +
			// ",fipdeptvid = (select fipdeptvid from "+tablename+" where wa_data.pk_psnjob = "+tablename+".pk_psnjob) "
			// +
			// ",pk_liabilityorg = (select pk_liabilityorg from "+tablename+" where wa_data.pk_psnjob = "+tablename+".pk_psnjob) "
			// +
			// ",pk_liabilitydept = (select pk_liabilitydept from "+tablename+" where wa_data.pk_psnjob = "+tablename+".pk_psnjob) "
			// +
			// ",libdeptvid = (select libdeptvid from "+tablename+" where wa_data.pk_psnjob = "+tablename+".pk_psnjob) ";
			// }else{ //shenliangc 20141013 若下期财务组织为空时，应该将财务组织等信息清空。
			// sqlfi=",pk_financeorg = null "
			// + ",pk_financedept = null "
			// + ",fiporgvid = null "
			// + ",fipdeptvid = null "
			// + ",pk_liabilityorg = null "
			// + ",pk_liabilitydept = null "
			// + ",libdeptvid = null ";
			// }
			sql = "update wa_data "
					+ "set workorg = org_orgs.pk_org , workorgvid = org_orgs.pk_vid, "
					+ "pk_liabilityorg=org_dept.pk_dept, workdept = org_dept.pk_dept , workdeptvid = org_dept.pk_vid "
					// guoqt更新财务组织
					// + sqlfi
					+ "from hi_psnjob,org_orgs,org_dept "
					+ "where wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "and hi_psnjob.pk_org = org_orgs.pk_org "
					+ "and hi_psnjob.pk_dept = org_dept.pk_dept "
					+ "and wa_data.pk_wa_class = ? " + "and wa_data.cyear = ? "
					+ "and wa_data.cperiod = ? ";

		} else {
			// if(tablename !=null){
			// sqlfi=
			// ",pk_financeorg = (select pk_financeorg from "+tablename+" where wa_data.pk_psnjob = "+tablename+".pk_psnjob) "
			// +
			// ",pk_financedept = (select pk_financedept from "+tablename+" where wa_data.pk_psnjob = "+tablename+".pk_psnjob) "
			// +
			// ",fiporgvid = (select fiporgvid from "+tablename+" where wa_data.pk_psnjob = "+tablename+".pk_psnjob) "
			// +
			// ",fipdeptvid = (select fipdeptvid from "+tablename+" where wa_data.pk_psnjob = "+tablename+".pk_psnjob) "
			// +
			// ",pk_liabilityorg = (select pk_liabilityorg from "+tablename+" where wa_data.pk_psnjob = "+tablename+".pk_psnjob) "
			// +
			// ",pk_liabilitydept = (select pk_liabilitydept from "+tablename+" where wa_data.pk_psnjob = "+tablename+".pk_psnjob) "
			// +
			// ",libdeptvid = (select libdeptvid from "+tablename+" where wa_data.pk_psnjob = "+tablename+".pk_psnjob) ";
			// }else{ //shenliangc 20141013 若下期财务组织为空时，应该将财务组织等信息清空。
			// sqlfi=",pk_financeorg = null "
			// + ",pk_financedept = null "
			// + ",fiporgvid = null "
			// + ",fipdeptvid = null "
			// + ",pk_liabilityorg = null "
			// + ",pk_liabilitydept = null "
			// + ",libdeptvid = null ";
			// }
			sql = "UPDATE wa_data " + " SET workorg = (SELECT org_orgs.pk_org "
					+ "				FROM hi_psnjob,org_orgs "
					+ "				WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "					AND hi_psnjob.pk_org = org_orgs.pk_org " + "	) "
					+ "	, workorgvid = (SELECT org_orgs.pk_vid "
					+ "					FROM hi_psnjob,org_orgs "
					+ "					WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "						AND hi_psnjob.pk_org = org_orgs.pk_org " + "	) "
					+ "	, pk_liabilityorg = (	SELECT org_dept.pk_dept "
					+ "					FROM hi_psnjob,org_dept "
					+ "					WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "						AND hi_psnjob.pk_dept = org_dept.pk_dept " + "	) "
					+ "	, workdept = (	SELECT org_dept.pk_dept "
					+ "					FROM hi_psnjob,org_dept "
					+ "					WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "						AND hi_psnjob.pk_dept = org_dept.pk_dept "
					+ "	) "
					+ "	, workdeptvid = (	SELECT org_dept.pk_vid "
					+ "						FROM hi_psnjob,org_dept "
					+ "						WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "							AND hi_psnjob.pk_dept = org_dept.pk_dept "
					+ "	) "
					// guoqt更新财务组织
					// + sqlfi
					+ " WHERE wa_data.pk_wa_class = ? "
					+ "	AND wa_data.cyear = ? " + "	AND wa_data.cperiod = ? ";

		}
		// end
		param.clearParams();
		param.addParam(waLoginVO.getPk_wa_class());
		param.addParam(nextPeriodStateVO.getCyear());
		param.addParam(nextPeriodStateVO.getCperiod());
		getBaseDao().executeUpdate(sql, param);
	}

	public UnitClassVO[] queryAllUnitClassVOs() throws BusinessException {
		String sql = "select "
				+ SQLHelper.getMultiLangNameColumn("wa_waclass.name")
				+ " unitclassname,wa_unitctg.* "
				+ " from wa_unitctg inner join wa_waclass on wa_unitctg.pk_wa_class = wa_waclass.pk_wa_class "
				+
				// 20151204 shenliangc NCdp205554542
				// 汇总方案子方案反结账，只有汇总方案最新期间和该子方案最新期间一致时才需要提示需要先把汇总方案反结账。
				" where (select wa_waclass.cyear || wa_waclass.cperiod from wa_waclass where wa_waclass.pk_wa_class = wa_unitctg.pk_wa_class) = "
				+ " 		(select wa_waclass.cyear || wa_waclass.cperiod from wa_waclass where wa_waclass.pk_wa_class = wa_unitctg.classedid)";
		return executeQueryVOs(sql, UnitClassVO.class);
	}

	public String[] isHaveUnAccountUnitChildClass(String sql)
			throws BusinessException {
		ArrayList<Map<String, String>> resultList = (ArrayList<Map<String, String>>) getBaseDao()
				.executeQuery(sql, new MapListProcessor());
		String[] results = null;
		if (resultList != null && resultList.size() > 0) {
			results = new String[resultList.size()];
			for (int i = 0; i < resultList.size(); i++) {
				Map<String, String> map = resultList.get(i);
				results[i] = map.get("pk_wa_class");
			}
		}
		return results;
	}

	private void synchronizeGroupClass4Reverse(WaClassEndVO waLoginVO)
			throws BusinessException {
		// 首先查看是否集团分配的方案
		IWaClass iWaClass = NCLocator.getInstance().lookup(IWaClass.class);
		WaClassVO vo = iWaClass.queryGroupClassByOrgClass(waLoginVO
				.getPk_wa_class());
		if (vo == null) {
			return;
		}
		String sql = "select max(cyear||cperiod) maxperiod from wa_periodstate inner join wa_period on wa_periodstate.pk_wa_period = wa_period.pk_wa_period"
				+ " where pk_wa_class in (select classid from wa_assigncls where pk_sourcecls = '"
				+ vo.getPk_wa_class()
				+ "') and wa_periodstate.enableflag = 'Y'";
		Map<String, String> map = (Map<String, String>) getBaseDao()
				.executeQuery(sql, new MapProcessor());
		String maxperiod = map.get("maxperiod");
		if ((vo.getCyear() + vo.getCperiod()).compareTo(maxperiod) > 0) {
			String pk_wa_class = vo.getPk_wa_class();
			/* 处理集团方案的反结问题 */
			// 删除薪资项目
			sql = "delete from wa_classitem where pk_wa_class = '"
					+ pk_wa_class + "'" + " and cyear = '" + vo.getCyear()
					+ "' and cperiod = '" + vo.getCperiod() + "'";
			getBaseDao().executeUpdate(sql);
			// 修改期间状态
			sql = "update wa_periodstate set enableflag='N' where pk_periodstate in "
					+ "(select pk_periodstate from  wa_periodstate inner join wa_period"
					+ " on wa_periodstate.pk_wa_period = wa_period.pk_wa_period"
					+ " where pk_wa_class = '"
					+ pk_wa_class
					+ "' and cyear = '"
					+ vo.getCyear()
					+ "' and cperiod = '"
					+ vo.getCperiod() + "')";
			getBaseDao().executeUpdate(sql);
			// 修改CLASSVO对应的CYEAR和CPERIOD
			sql = "update wa_waclass set " + "wa_waclass.cyear = '"
					+ maxperiod.substring(0, 4) + "', "
					+ "wa_waclass.cperiod = '" + maxperiod.substring(4, 6)
					+ "' " + "where PK_WA_CLASS='" + pk_wa_class + "'";
			getBaseDao().executeUpdate(sql);
		}
	}

}
