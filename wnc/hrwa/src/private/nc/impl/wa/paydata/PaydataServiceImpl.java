package nc.impl.wa.paydata;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.hr.frame.persistence.SimpleDocLocker;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.wa.log.WaBusilogUtil;
import nc.impl.hrwa.DaySalaryCache;
import nc.impl.wa.payfile.PayfileServiceImpl;
import nc.impl.wa.repay.RepayDAO;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.wa.IAmoSchemeQuery;
import nc.itf.hr.wa.IClassItemQueryService;
import nc.itf.hr.wa.IDeductDetailService;
import nc.itf.hr.wa.IHRWAActionCode;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.IPaydataManageService;
import nc.itf.hr.wa.IPaydataQueryService;
import nc.itf.hr.wa.IUnitWaClassQuery;
import nc.itf.hr.wa.IWaClass;
import nc.itf.hr.wa.PaydataDspUtil;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.itf.hrwa.IWadaysalaryService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.rbac.IDataPermissionPubService;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.vo.hi.psndoc.Attribute;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.append.AppendableVO;
import nc.vo.hr.caculate.CaculateTypeVO;
import nc.vo.hr.combinesort.SortVO;
import nc.vo.hr.combinesort.SortconVO;
import nc.vo.hr.datainterface.FormatItemVO;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.hr.tools.pub.Pair;
import nc.vo.hrp.budgetmgt.BudgetWarnMessageVo;
import nc.vo.logging.Debug;
import nc.vo.ml.MultiLangUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;
import nc.vo.util.BDPKLockUtil;
import nc.vo.util.BDVersionValidationUtil;
import nc.vo.wa.category.WaInludeclassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.paydata.AggPayDataVO;
import nc.vo.wa.paydata.ClassitemDisplayVO;
import nc.vo.wa.paydata.DataSVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.paydata.ICommonAlterName;
import nc.vo.wa.paydata.PsncomputeVO;
import nc.vo.wa.paydata.PsndocWaVO;
import nc.vo.wa.paydata.WaClassItemShowInfVO;
import nc.vo.wa.paydata.WaPaydataDspVO;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wa.pub.WaState;
import nc.vo.wa.repay.ReDataVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

/**
 * 薪资发放实现类
 * 
 * @author: zhangg
 * @date: 2009-11-23 下午01:19:19
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class PaydataServiceImpl implements IPaydataManageService, IPaydataQueryService {
	private static final String PART_TIME_PSN_MAP_KEY = "partTimePsn";

	private static final String NOT_PART_TIME_PSN_MAP_KEY = "notPartTimePsn";
	// 申报格式:50薪资:1 90AB:2 其他:0
	private static final int DECLAREFORM_50 = 1;
	private static final int DECLAREFORM_90AB = 2;
	private static final int DECLAREFORM_OTHER = 0;
	private PaydataDAO queryService;

	private BaseDAO dao = new BaseDAO();

	private PaydataDAO getService() throws DAOException {
		if (queryService == null) {
			queryService = new PaydataDAO();
		}
		return queryService;
	}

	@Override
	public void update(Object vo, WaLoginVO waLoginVO) throws BusinessException {
		BDPKLockUtil.lockString(waLoginVO.getPk_wa_class());

		// 得到被修改的项目。
		getService().update(vo, waLoginVO);
	}

	/**
	 * 审核
	 * 
	 * @author zhangg on 2009-12-2
	 * @see nc.itf.hr.wa.IPaydataManageService#onCheck(nc.vo.wa.pub.WaLoginVO,
	 *      java.lang.String, java.lang.Boolean)
	 */
	@Override
	public void onCheck(WaLoginVO waLoginVO, String whereCondition, Boolean isRangeAll)
			throws nc.vo.pub.BusinessException {

		// 先检查类别状态
		getService().checkWaClassStateChange(waLoginVO, whereCondition);
		// 更新审核标准
		boolean isAllChecked = getService().onCheck(waLoginVO, whereCondition, isRangeAll);

		if (WaLoginVOHelper.isSubClass(waLoginVO) && isAllChecked) {
			collectWaTimesData(waLoginVO);
		}
		// 审核完成回写到扣款明细子集
		NCLocator.getInstance().lookup(IDeductDetailService.class)
				.rollbacktodeductdetail(waLoginVO, whereCondition, isRangeAll);
	}

	/**
	 * 取消审核
	 * 
	 * @author zhangg on 2009-12-2
	 * @see nc.itf.hr.wa.IPaydataManageService#onUnCheck(nc.vo.wa.pub.WaLoginVO,
	 *      java.lang.String)
	 */
	@Override
	public void onUnCheck(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll)
			throws nc.vo.pub.BusinessException {
		// 先检查类别状态
		getService().checkWaClassStateChange(waLoginVO, whereCondition);

		// 更新审核标准
		getService().onUnCheck(waLoginVO, whereCondition, isRangeAll);

		if (WaLoginVOHelper.isSubClass(waLoginVO)) {
			collectWaTimesData(waLoginVO);

			// 20160125 shenliangc NCdp205577867 薪资多次发放, 发放次数（汇总）数据错误 ,
			// 取消审核需要单独处理本次添加的档案人员
			updateCollectDataForThisTime(waLoginVO);
		}
		// 取消审核之后，删除回写到扣款明细子集里面的数据 by he
		// unbackdeductdetails(waLoginVO, whereCondition, isRangeAll);
		NCLocator.getInstance().lookup(IDeductDetailService.class)
				.cancelDeductdetail(waLoginVO.getPk_wa_class(), waLoginVO.getCyear() + "" + waLoginVO.getCperiod());
	}

	/**
	 * 获取用户有权限的薪资项目（全部）
	 * 
	 * @author liangxr on 2010-5-13
	 * @see nc.itf.hr.wa.IPaydataQueryService#getUserClassItemVOs(nc.vo.wa.pub.WaLoginContext)
	 */
	@Override
	public WaClassItemVO[] getUserClassItemVOs(WaLoginContext loginVO) throws BusinessException {
		return getService().getUserClassItemVOs(loginVO);
	}

	/**
	 * 获取用户有权限的薪资项目（可见）
	 * 
	 * @author liangxr on 2010-5-13
	 * @see nc.itf.hr.wa.IPaydataQueryService#getUserClassItemVOs(nc.vo.wa.pub.WaLoginContext)
	 */
	@Override
	public WaClassItemVO[] getUserShowClassItemVOs(WaLoginContext loginVO) throws BusinessException {
		return getService().getUserShowClassItemVOs(loginVO);
	}

	public WaClassItemVO[] getRepayUserShowClassItemVOs(WaLoginContext loginContext) throws BusinessException {
		return getService().getRepayUserShowClassItemVOs(loginContext);

	}

	/**
	 * 为减少连接数专门提供的方法 同时查找 薪资发放项目,薪资项目展现信息
	 * 
	 * @param loginContext
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public WaClassItemShowInfVO getWaClassItemShowInfVO(WaLoginContext loginContext) throws BusinessException {
		WaClassItemVO[] vos1 = getUserShowClassItemVOs(loginContext);

		List<WaPaydataDspVO> list = queryPaydataDisplayInfo(loginContext);
		WaClassItemShowInfVO info = new WaClassItemShowInfVO();
		info.setWaClassItemVO(vos1);
		info.setWaPaydataDspVO(list.toArray(new WaPaydataDspVO[list.size()]));

		return info;

	}

	@Override
	public int updateClassItemVOsDisplayFlg(WaClassItemVO[] classItemVOs) throws BusinessException {

		if (ArrayUtils.isEmpty(classItemVOs)) {

			return 0;
		}

		String PKclass = classItemVOs[0].getPk_wa_class();
		String userpk = PubEnv.getPk_user();
		String waYear = classItemVOs[0].getCyear();
		String waPeriod = classItemVOs[0].getCperiod();
		List<ClassitemDisplayVO> lisDisplay = new ArrayList<ClassitemDisplayVO>();
		for (WaClassItemVO classitem : classItemVOs) {
			ClassitemDisplayVO displayVO = new ClassitemDisplayVO();
			displayVO.setPk_wa_classitem(classitem.getPk_wa_classitem());
			displayVO.setPk_wa_class(classitem.getPk_wa_class());
			displayVO.setPk_user(PubEnv.getPk_user());
			displayVO.setCyear(classitem.getCyear());
			displayVO.setCperiod(classitem.getCperiod());
			displayVO.setBshow(classitem.getShowflag());
			displayVO.setDisplayseq(classitem.getIdisplayseq());
			displayVO.setStatus(VOStatus.NEW);
			lisDisplay.add(displayVO);
		}
		getService().getBaseDao().deleteByClause(
				ClassitemDisplayVO.class,
				"pk_wa_class = '" + PKclass + "' and pk_user = '" + userpk + "' and cyear = '" + waYear
						+ "' and cperiod = '" + waPeriod + "'");
		getService().getBaseDao().insertVOList(lisDisplay);
		return 0;
	}

	/**
	 * 发放
	 * 
	 * @author zhangg on 2009-12-4
	 * @see nc.itf.hr.wa.IPaydataManageService#onPay(nc.vo.wa.pub.WaLoginVO)
	 */
	@Override
	public void onPay(WaLoginContext loginContext) throws nc.vo.pub.BusinessException {
		// 锁定子方案
		BDPKLockUtil.lockString(loginContext.getPk_wa_class());

		// 先检查类别状态
		getService().checkWaClassStateChange(loginContext.getWaLoginVO(), null);
		// 更新发放
		getService().onPay(loginContext);
		// if(WaLoginVOHelper.isSubClass(loginContext.getWaLoginVO())){
		// collectWaTimesData(loginContext.getWaLoginVO());
		// }
	}

	/**
	 * 取消发放
	 * 
	 * @author liangxr on 2010-5-24
	 * @see nc.itf.hr.wa.IPaydataManageService#onUnPay(nc.vo.wa.pub.WaLoginVO)
	 */
	@Override
	public void onUnPay(WaLoginVO waLoginVO) throws nc.vo.pub.BusinessException {
		// 锁定子方案

		BDPKLockUtil.lockString(waLoginVO.getPk_wa_class());
		// 先检查类别状态
		getService().checkWaClassStateChange(waLoginVO, null);
		// 检查薪资方案父方案是否已经分摊制单

		if (waLoginVO.getPk_prnt_class() != null && waLoginVO.getPk_prnt_class() != waLoginVO.getPk_wa_class()) {
			WaLoginVO subLoginVO = (WaLoginVO) waLoginVO.clone();
			subLoginVO.setPk_wa_class(waLoginVO.getPk_prnt_class());
			checkIsApportion(subLoginVO);
		}
		// 2015-11-5 zhousze 加版本校验，通过后台查询期间状态表的pk与ts begin
		String cperiod = waLoginVO.getCperiod();
		String pk_periodscheme = waLoginVO.getPk_periodscheme();
		String cyear = waLoginVO.getCyear();
		String pk_wa_class = waLoginVO.getPk_wa_class();
		PeriodStateVO periodstateVO = new PaydataDAO().queryPeriodStateVOByPk(pk_wa_class, pk_periodscheme, cperiod,
				cyear);
		if (periodstateVO != null) {
			BDVersionValidationUtil.validateSuperVO(periodstateVO);
		}
		// end
		// 更新发放
		getService().onUnPay(waLoginVO);
	}

	/**
	 * 检查薪资方案是否已经分摊制单
	 * 
	 * @param waLoginVO
	 * @throws BusinessException
	 */
	private void checkIsApportion(WaLoginVO waLoginVO) throws BusinessException {
		if (NCLocator.getInstance().lookup(IAmoSchemeQuery.class).isApportion(waLoginVO)) {
			if (WaLoginVOHelper.isSubClass(waLoginVO)) {
				throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0516")/*
																									 * @
																									 * res
																									 * "选择的薪资方案父方案已经分摊制单,不能取消发放！"
																									 */);
			} else {
				throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0458")/*
																									 * @
																									 * res
																									 * "选择的薪资方案已经分摊制单,不能取消发放！"
																									 */);
			}
		}
	}

	/**
	 * 替换
	 * 
	 * @author liangxr on 2010-5-24
	 * @see nc.itf.hr.wa.IPaydataManageService#onReplace(nc.vo.wa.pub.WaLoginVO,
	 *      java.lang.String, nc.vo.wa.classitem.WaClassItemVO,
	 *      java.lang.String)
	 */
	@Override
	public void onReplace(WaLoginVO waLoginVO, String whereCondition, WaClassItemVO replaceItem, String formula,
			SuperVO... superVOs) throws BusinessException {
		BDPKLockUtil.lockString(waLoginVO.getPk_wa_class());
		// 添加数据维护权
		// 20150924 shenliangc “替换”报错，原因是平台修改了取数据权限的工具方法。现在改为用我们自己的。
		// String operateConditon = WaPowerSqlHelper.getWaPowerSql(
		// PubEnv.getPk_group(), IHRWADataResCode.WADATA,
		// IHRWADataResCode.WADEFAULT,"wa_data" );
		// if (StringUtils.isBlank(whereCondition)) {
		// whereCondition = operateConditon;
		// } else {
		// whereCondition = whereCondition
		// + WherePartUtil.formatAddtionalWhere(operateConditon);
		// }
		// 添加数据维护权 NCdp205509858 薪资发放维护权全部无权/新规则无权，无权数据可替换 mizhl 2015-10-27
		// begin
		String poweConditon = NCLocator
				.getInstance()
				.lookup(IDataPermissionPubService.class)
				.getDataPermissionSQLWherePartByMetaDataOperation(PubEnv.getPk_user(), IHRWADataResCode.WADATA,
						IHRWAActionCode.REPLACE, PubEnv.getPk_group());
		if (StringUtils.isBlank(whereCondition)) {
			whereCondition = poweConditon;
		} else {
			whereCondition = whereCondition + WherePartUtil.formatAddtionalWhere(poweConditon);
		}
		// 添加数据使用权
		String userConditon = WaPowerSqlHelper.getWaPowerSql(PubEnv.getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB, IHRWADataResCode.WADEFAULT, "wa_data");
		if (StringUtils.isBlank(whereCondition)) {
			whereCondition = userConditon;
		} else {
			whereCondition = whereCondition + WherePartUtil.formatAddtionalWhere(userConditon);
		}
		// 添加数据维护权 NCdp205509858 薪资发放维护权全部无权/新规则无权，无权数据可替换 mizhl 2015-10-27 end
		// 替换记录日志
		if (null != superVOs) {
			// 版本校验（时间戳校验）
			BDVersionValidationUtil.validateSuperVO(superVOs);
		}

		getService().onReplace(waLoginVO, whereCondition, replaceItem, formula);

	}

	@Override
	public void onSaveDataSVOs(WaLoginVO waLoginVO, DataSVO[] dataSVOs) throws BusinessException {

		getService().onSaveDataSVOs(waLoginVO, dataSVOs);
	}

	@Override
	public AggPayDataVO queryAggPayDataVOByCondition(WaLoginContext loginContext, String condition, String orderCondtion)
			throws BusinessException {

		WaBusilogUtil.writePaydataQueryBusiLog(loginContext);
		if (StringUtils.isBlank(orderCondtion)) {
			// 如果没有排序字段 先到数据库中查询有没有当前用户的排序设置 by wangqim
			SortVO sortVOs[] = null;
			SortconVO sortconVOs[] = null;
			String strCondition = " func_code='" + loginContext.getNodeCode() + "'"
					+ " and group_code= 'TableCode' and ((pk_corp='" + PubEnv.getPk_group() + "' and pk_user='"
					+ PubEnv.getPk_user() + "') or pk_corp ='@@@@') order by pk_corp";

			sortVOs = (SortVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
					.retrieveByClause(null, SortVO.class, strCondition);
			Vector<Attribute> vectSortField = new Vector<Attribute>();
			if (sortVOs != null && sortVOs.length > 0) {
				strCondition = "pk_hr_sort='" + sortVOs[0].getPrimaryKey() + "' order by field_seq ";
				sortconVOs = (SortconVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
						.retrieveByClause(null, SortconVO.class, strCondition);
				for (int i = 0; sortconVOs != null && i < sortconVOs.length; i++) {
					Pair<String> field = new Pair<String>(sortconVOs[i].getField_name(), sortconVOs[i].getField_code());
					Attribute attribute = new Attribute(field, sortconVOs[i].getAscend_flag().booleanValue());
					vectSortField.addElement(attribute);
				}
				orderCondtion = getOrderby(vectSortField);
			}
			if (StringUtils.isBlank(orderCondtion)) {
				orderCondtion = " org_dept_v.code , hi_psnjob.clerkcode ";
			}
		}
		return getService().queryAggPayDataVOByCondition(loginContext, condition, orderCondtion);
	}

	public static String getOrderby(Vector<Attribute> vectSortField) {
		if (vectSortField == null || vectSortField.size() == 0) {
			return "";
		}
		String strOrderBy = "";
		for (Attribute attr : vectSortField) {
			String strFullCode = attr.getAttribute().getValue();
			strOrderBy = strOrderBy + "," + strFullCode + (attr.isAscend() ? "" : " desc");
		}
		return strOrderBy.length() > 0 ? strOrderBy.substring(1) : "";
	}

	@Override
	public DataVO getDataVOByPk(String pk_wa_data) throws BusinessException {
		DataVO[] dataVOs = getService()
				.retrieveAppendableVOsByClause(DataVO.class, "pk_wa_data = '" + pk_wa_data + "'");
		if (dataVOs != null) {
			return dataVOs[0];
		} else {
			return null;
		}
	}

	/**
	 * 判断一个方案（无论是单次还是多次，还是多次中的一次）是否已经发放过（一次）
	 * 
	 * @param pk_wa_data
	 * @return
	 * @throws BusinessException
	 */
	public boolean isAnyTimesPayed(String pk_wa_class, String cyear, String cperiod) throws BusinessException {

		return getService().isAnyTimesPayed(pk_wa_class, cyear, cperiod);
	}

	@Override
	public DataVO[] queryDataVOByPks(String[] pk_wa_data) throws BusinessException {

		if (ArrayUtils.isEmpty(pk_wa_data)) {
			return new DataVO[0];
		}

		InSQLCreator inSQLCreator = new InSQLCreator();
		try {
			String conditon = inSQLCreator.getInSQL(pk_wa_data);
			DataVO[] dataVOArrays = getService().queryByPKSCondition(conditon, "");
			// 2016-11-22 zhousze 薪资加密：此处解密查询出来的所有DATAVO begin
			dataVOArrays = SalaryDecryptUtil.decrypt4Array(dataVOArrays);
			// end

			// autor erl 2011-6-10 11:40 按传进来的主键的顺序排序
			List<DataVO> dataVOList = new ArrayList<DataVO>();
			Map<String, DataVO> dataVOMap = new HashMap<String, DataVO>();
			for (DataVO dataVO : dataVOArrays) {
				dataVOMap.put(dataVO.getPk_wa_data(), dataVO);
			}
			for (String str_pk_wa_data : pk_wa_data) {
				dataVOList.add(dataVOMap.get(str_pk_wa_data));
			}

			return dataVOList.toArray(new DataVO[0]);
		} finally {
			inSQLCreator.clear();
		}
	}

	@Override
	public DataSVO[] getDataSVOs(WaLoginContext loginContext) throws BusinessException {
		return getService().getDataSVOs(loginContext);
	}

	@Override
	public DataVO[] queryDataVOsByCond(WaLoginContext loginContext, String condition, String orderCondtion)
			throws BusinessException {
		return getService().queryByCondition(loginContext, condition, orderCondtion);
	}

	public Map<String, AppendableVO> campareDateVOsByCond(WaLoginContext loginContext, HrIntfaceVO vo,
			AppendableVO[] appendVOs, FormatItemVO[] itemVOs) throws Exception {
		IPaydataQueryService queryService = NCLocator.getInstance().lookup(IPaydataQueryService.class);
		IPaydataManageService mgrService = NCLocator.getInstance().lookup(IPaydataManageService.class);
		DataVO[] dbVOs = queryService.queryDataVOsByCond(loginContext, null, null);
		Map<String, AppendableVO> map = new HashMap<String, AppendableVO>();
		if (dbVOs == null) {
			return map;
		}
		List<DataVO> list = new ArrayList<DataVO>();
		String colAsName = vo.getVcol().equals("id") ? "psnid" : "psncode";
		for (int j = 0; appendVOs != null && j < appendVOs.length; j++) {
			if (appendVOs[j].getAttributeValue("bd_psndoc" + vo.getVcol()) != null) {
				for (int i = 0; dbVOs != null && i < dbVOs.length; i++) {

					if (dbVOs[i].getAttributeValue(colAsName) == null) {
						continue;
					}
					if (dbVOs[i].getAttributeValue(colAsName).equals(
							appendVOs[j].getAttributeValue("bd_psndoc" + vo.getVcol()))) {
						if (map.containsKey(appendVOs[j].getAttributeValue("bd_psndoc" + vo.getVcol()))) {
							throw new BusinessException(ResHelper.getString("6013bnkitf", "06013bnkitf0040")/*
																											 * @
																											 * res
																											 * "数据中存在重复的人员，导入出错！"
																											 */);
						} else {
							map.put((String) appendVOs[j].getAttributeValue("bd_psndoc" + vo.getVcol()), appendVOs[j]);
						}
						DataVO datavo = new DataVO();
						datavo.setPk_wa_data(dbVOs[i].getPk_wa_data());
						datavo.setTs(dbVOs[i].getTs());
						datavo.setCheckflag(dbVOs[i].getCheckflag());
						datavo.setCpaydate(dbVOs[i].getCpaydate());
						datavo.setVpaycomment(dbVOs[i].getVpaycomment());
						for (int k = 0; itemVOs != null && k < itemVOs.length; k++) {

							Object value = appendVOs[j].getAttributeValue(itemVOs[k].getVcontent().replace(".", ""));
							int beginIndex = itemVOs[k].getVcontent().indexOf(".");
							String name = itemVOs[k].getVcontent().substring(beginIndex + 1);
							datavo.setAttributeValue(name, value);
						}
						list.add(datavo);
						break;
					}
				}
			} else {
				throw new BusinessException(ResHelper.getString("6013bnkitf", "06013bnkitf0041")/*
																								 * @
																								 * res
																								 * "没有设置关联项目或者关联项目的值为空，不能导入！"
																								 */);
			}
		}
		mgrService.update(list.toArray(), loginContext.getWaLoginVO());
		return map;

	}

	@Override
	public DataVO[] getContractDataVOs(WaLoginContext loginVO, String whereCondition, String orderCondition)
			throws BusinessException {
		return getService().getContractDataVOs(loginVO, whereCondition, orderCondition);
	}

	/**
	 * @author zhangg on 2010-5-14
	 * @see nc.itf.hr.wa.IPaydataManageService#onCaculate(nc.vo.wa.pub.WaLoginContext,
	 *      java.lang.String)
	 */
	@Override
	public void onCaculate(WaLoginContext loginContext, CaculateTypeVO caculateTypeVO, String condition,
			SuperVO... superVOs) throws BusinessException {

		// 锁定薪资方案(锁父方案)
		if (!StringUtils.isBlank(loginContext.getPk_prnt_class())) {
			BDPKLockUtil.lockString(loginContext.getPk_prnt_class());
		}

		// 锁定子方案
		new SimpleDocLocker().lock(SimpleDocServiceTemplate.UPDATE, loginContext.getWaLoginVO());

		// 校验子方案的版本一致性
		// 版本校验（时间戳校验）
		BDVersionValidationUtil.validateSuperVO(loginContext.getWaLoginVO());

		if (null != superVOs) {
			// 版本校验（时间戳校验）
			BDVersionValidationUtil.validateSuperVO(superVOs);
		}
		// 进行日薪的计算
		// 废弃 tank 2019年10月15日18:08:08 不再使用时点薪资的方式,改为本地化标准版
		// tank 日薪重构,拉倒内存运算数据 2019年10月17日20:06:42
		DaySalaryCache.getInstance().setCaculateTime(loginContext.getPk_loginUser(), 0.0d);
		UFDateTime starttime = new UFDateTime();
		try {
			doDaySalary(loginContext, caculateTypeVO, superVOs);
		} catch (BusinessException ex) {
			throw ex;
		} catch (Exception e) {
			throw new BusinessException("日薪計算異常！");
		}

		UFDateTime endtime = new UFDateTime();
		long calcutime = (endtime.getMillis() - starttime.getMillis()) / 1000;

		// 判断补发
		checkReData(loginContext.getWaLoginVO());

		DataCaculateService caculateService = new DataCaculateService(loginContext, caculateTypeVO, condition);
		caculateService.doCaculate();

		// 日薪计算结束操作
		clearDaySalary(loginContext, calcutime);
	}

	private void clearDaySalary(WaLoginContext loginContext, double calcutime) {
		Double useTime = DaySalaryCache.getInstance().getCaculateTime(loginContext.getPk_loginUser());
		if (useTime == null) {
			useTime = 0.0d;
		}
		DaySalaryCache.getInstance().setCaculateTime(loginContext.getPk_loginUser(), calcutime);
		DaySalaryCache.getInstance().clearByUser(loginContext.getPk_loginUser());
	}

	private List<String> getCalculatePsnList(WaLoginContext waLoginContext, CaculateTypeVO caculateTypeVO,
			List<SuperVO> payfileVos) {
		List<String> pk_psndoclist = new ArrayList<String>();
		String where = null;
		// 计算范围
		boolean all = caculateTypeVO.getRange().booleanValue();
		if (where == null || all) {
			where = null;
		}

		// 计算方式
		boolean type = caculateTypeVO.getType().booleanValue();
		if (type) {
			String addWhere = "  wa_data.checkflag = 'N'  ";
			where = where == null ? addWhere : (where + " and   " + addWhere);
		} else {
			String addWhere = "  wa_data.checkflag = 'N' and wa_data.caculateflag = 'N'   ";
			where = where == null ? addWhere : (where + " and   " + addWhere);
		}
		if (StringUtils.isEmpty(where)) {
			where = WherePartUtil.getCommonWhereCondtion4Data(waLoginContext.getWaLoginVO());
		} else {
			where = where + " and " + WherePartUtil.getCommonWhereCondtion4Data(waLoginContext.getWaLoginVO());
		}

		if (caculateTypeVO.getRange().booleanValue()) {
			String sql = "select pk_psndoc from wa_data where " + where;
			IUAPQueryBS queryPsn = NCLocator.getInstance().lookup(IUAPQueryBS.class);
			try {
				List<String> psns = (List<String>) queryPsn.executeQuery(sql, new ColumnListProcessor());
				if (psns != null && psns.size() > 0) {
					pk_psndoclist.addAll(psns);
				}
			} catch (BusinessException e) {
				ExceptionUtils.wrappBusinessException(e.getMessage());
			}
		} else {
			if (!ArrayUtils.isEmpty(payfileVos.toArray(new SuperVO[0]))) {
				for (int i = 0; i < payfileVos.size(); i++) {
					pk_psndoclist.add(((DataVO) payfileVos.get(i)).getPk_psndoc());
				}
			}
		}
		return pk_psndoclist;
	}

	private void doDaySalary(WaLoginContext waLoginContext, CaculateTypeVO caculateTypeVO, SuperVO... superVOs)
			throws BusinessException {
		// 前方高能
		System.gc();
		// 清零時間
		DaySalaryCache.getInstance().setCaculateTime(waLoginContext.getPk_loginUser(), null);

		List<SuperVO> payList = new ArrayList<SuperVO>(superVOs.length);
		Collections.addAll(payList, superVOs);
		List<String> psnList = getCalculatePsnList(waLoginContext, caculateTypeVO, payList);

		IWadaysalaryService daySalCalService = NCLocator.getInstance().lookup(IWadaysalaryService.class);
		// 按薪资方案和人员计算日薪
		daySalCalService.calculDaySalaryWithWaClass(waLoginContext.getPk_wa_class(), psnList.toArray(new String[0]),
				waLoginContext.getCyear(), waLoginContext.getCperiod());
		System.gc();
	}

	@Override
	public void reCalculateRelationWaItem(WaLoginContext loginContext, CaculateTypeVO caculateTypeVO, String condition,
			SuperVO... superVOs) throws BusinessException {
		// 锁定薪资方案(锁父方案)
		if (!StringUtils.isBlank(loginContext.getPk_prnt_class())) {
			BDPKLockUtil.lockString(loginContext.getPk_prnt_class());
		}

		// 锁定子方案
		new SimpleDocLocker().lock(SimpleDocServiceTemplate.UPDATE, loginContext.getWaLoginVO());

		// 校验子方案的版本一致性
		// 版本校验（时间戳校验）
		BDVersionValidationUtil.validateSuperVO(loginContext.getWaLoginVO());

		// 进行日薪的计算
		// doDaySalary(loginContext, caculateTypeVO, superVOs);
		// 判断补发
		checkReData(loginContext.getWaLoginVO());

		HealthRelationDataCaculateService healthRelationDataCaculateService = new HealthRelationDataCaculateService(
				loginContext, caculateTypeVO, condition);

		UFDateTime starttime = new UFDateTime();
		try {
			doDaySalary(loginContext, caculateTypeVO, superVOs);
		} catch (BusinessException ex) {
			throw ex;
		} catch (Exception e) {
			throw new BusinessException("日薪計算異常！");
		}

		UFDateTime endtime = new UFDateTime();
		long calcutime = (endtime.getMillis() - starttime.getMillis()) / 1000;

		healthRelationDataCaculateService.doCaculate();

		// 日薪计算结束操作
		clearDaySalary(loginContext, calcutime);
	}

	/*
	 * private void doDaySalary(WaLoginContext waLoginContext, CaculateTypeVO
	 * caculateTypeVO, SuperVO... superVOs) throws BusinessException { WaLoginVO
	 * waLoginVO = waLoginContext.getWaLoginVO(); PsncomputeVO[] vos =
	 * WADelegator.getPsndocWa().queryAllShowResult( waLoginVO,
	 * waLoginVO.getDeptpower(), waLoginVO.getPsnclpower()); String[] psndocs =
	 * new String[0]; if (caculateTypeVO != null) { // 是否全部人员计算 boolean all =
	 * caculateTypeVO.getRange().booleanValue(); // 是否只计算没有计算过得人 boolean type =
	 * caculateTypeVO.getType().booleanValue(); if (all) { // 获取所有的人员 psndocs =
	 * getPsndocs(vos); } else { // 只获取部分人员 psndocs = getByType(superVOs, type);
	 * } } else { // 获取所有的人员 psndocs = getPsndocs(vos); } if (psndocs == null ||
	 * psndocs.length <= 0) { return; } // 获取所有的薪资项目 String[] pk_wa_items =
	 * getWaItems(vos); for (String pk_wa_item : pk_wa_items) { Map<String,
	 * UFDouble> psnSalaryMap = NCLocator .getInstance()
	 * .lookup(IWadaysalaryQueryService.class)
	 * .getTotalDaySalaryMapByWaItem(psndocs, waLoginContext.getPk_wa_class(),
	 * waLoginContext.getCyear(), waLoginContext.getCperiod(), pk_wa_item); if
	 * (psnSalaryMap == null || psnSalaryMap.size() == 0) { continue; }
	 * 
	 * PsndocWaVO[] psndocWaVOs = convertComputeVO(vos, psnSalaryMap,
	 * pk_wa_item, waLoginVO);
	 * 
	 * WADelegator.getPsndocWa().updatePsndocWas(psndocWaVOs); // shenliangc
	 * 20140826 时点薪资计算保存之后清除主界面对应人员的发放数据计算标志
	 * WADelegator.getPaydata().updateCalFlag4OnTime(psndocWaVOs); }
	 * 
	 * }
	 */

	// 获取人员通过类别 type: true 全部计算 false :只计算没有进行薪资计算的人员
	private String[] getByType(SuperVO[] superVOs, boolean type) {

		Set<String> result = new HashSet<>();

		if (type && superVOs != null) {
			// 全部人员
			for (SuperVO vo : superVOs) {
				DataVO tempVO = null;
				try {
					tempVO = (DataVO) vo;
				} catch (Exception e) {
					tempVO = null;
				}
				if (tempVO != null) {
					result.add(tempVO.getPk_psndoc());
				}

			}

		} else if (superVOs != null) {
			// 未计算的人员
			for (SuperVO vo : superVOs) {
				DataVO tempVO = null;
				try {
					tempVO = (DataVO) vo;
				} catch (Exception e) {
					tempVO = null;
				}
				if (tempVO != null) {
					if (!tempVO.getCaculateflag().booleanValue()) {
						result.add(tempVO.getPk_psndoc());
					}
				}
			}
		}
		return result.toArray(new String[0]);
	}

	/**
	 * 封装vo存储
	 * 
	 * @param vos
	 * @param psnSalaryMap
	 * @param pk_wa_item
	 * @return
	 * @throws BusinessException
	 */
	/*
	 * private PsndocWaVO[] convertComputeVO(PsncomputeVO[] psncomputeVOs,
	 * Map<String, UFDouble> psnSalaryMap, String pk_wa_item, WaLoginVO
	 * waLoginVO) throws BusinessException {
	 * 
	 * List<PsndocWaVO> returnList = new ArrayList<>(); for (int i = 0; i <
	 * psncomputeVOs.length; i++) { PsncomputeVO result = psncomputeVOs[i]; if
	 * (null == psnSalaryMap ||
	 * !psnSalaryMap.keySet().contains(result.getPk_psndoc())) { continue; }
	 * PsndocWaVO psndocWa = null; if (result.getPk_wa_item() == null ||
	 * result.getPk_wa_item().equals(pk_wa_item)) { psndocWa =
	 * result.getPsndocwavo(); } else { continue; }
	 * 
	 * if (null == psndocWa) { psndocWa = new PsndocWaVO(); }
	 * psndocWa.setPk_psndoc_sub(result.getPk_psndoc_sub());
	 * psndocWa.setPsnname(result.getPsnname());
	 * psndocWa.setPsncode(result.getPsncode());
	 * psndocWa.setPk_psndoc(result.getPk_psndoc());
	 * psndocWa.setPk_psndoc_wa(result.getPk_psncompute());
	 * psndocWa.setPk_wa_item(result.getPk_wa_item());
	 * psndocWa.setItemname(result.getItemvname());
	 * psndocWa.setOldmoney(UFDouble.ZERO_DBL);
	 * psndocWa.setNowmoney(psnSalaryMap.get(result.getPk_psndoc()) == null ?
	 * UFDouble.ZERO_DBL : psnSalaryMap .get(result.getPk_psndoc()));
	 * psndocWa.setNmoney(psnSalaryMap.get(result.getPk_psndoc()) == null ?
	 * UFDouble.ZERO_DBL : psnSalaryMap .get(result.getPk_psndoc()));
	 * psndocWa.setCyear(waLoginVO.getCyear());
	 * psndocWa.setCperiod(waLoginVO.getCperiod());
	 * psndocWa.setPk_wa_class(waLoginVO.getPeriodVO().getPk_wa_class());
	 * psndocWa.setWaclassname(waLoginVO.getName()); UFLiteralDate startDate =
	 * waLoginVO.getPeriodVO().getCstartdate();
	 * 
	 * UFLiteralDate endDate = waLoginVO.getPeriodVO().getCenddate();
	 * 
	 * psndocWa.setBegindate(startDate); psndocWa.setEnddate(endDate);
	 * 
	 * psndocWa.setNceforedays(UFDouble.ZERO_DBL);
	 * psndocWa.setNbeforemoney(UFDouble.ZERO_DBL);
	 * psndocWa.setNafterdays(UFDouble.ZERO_DBL);
	 * psndocWa.setNaftermoney(UFDouble.ZERO_DBL);
	 * psndocWa.setDaywage(Integer.valueOf(0));
	 * psndocWa.setIwatype(Integer.valueOf(0)); psndocWa.setTs(new
	 * UFDateTime()); psndocWa.setBasedays(UFDouble.ZERO_DBL);
	 * psndocWa.setSub_ts(result.getSub_ts());
	 * psndocWa.setPre_sub_id(result.getPre_sub_id());
	 * psndocWa.setPre_sub_ts(result.getPre_sub_ts());
	 * psndocWa.setAssgid(result.getAssgid()); result.setPsndocwavo(psndocWa);
	 * 
	 * returnList.add(psndocWa); } return returnList.toArray(new PsndocWaVO[0]);
	 * }
	 */

	/**
	 * 获取所有的薪资项目
	 * 
	 * @param vos
	 * @return
	 */
	/*
	 * private String[] getWaItems(PsncomputeVO[] vos) { Set<String> resultSet =
	 * new HashSet<>(); if (null == vos || vos.length <= 0) { return
	 * resultSet.toArray(new String[0]); } for (PsncomputeVO vo : vos) { if (vo
	 * != null && vo.getPk_wa_item() != null) {
	 * resultSet.add(vo.getPk_wa_item()); } } return resultSet.toArray(new
	 * String[0]); }
	 */

	/**
	 * 获取所有的人员
	 * 
	 * @param vos
	 * @return
	 */
	private String[] getPsndocs(PsncomputeVO[] vos) {
		Set<String> resultSet = new HashSet<>();
		if (null == vos || vos.length <= 0) {
			return resultSet.toArray(new String[0]);
		}
		for (PsncomputeVO vo : vos) {
			if (vo != null && vo.getPk_psndoc() != null) {
				resultSet.add(vo.getPk_psndoc());
			}
		}
		return resultSet.toArray(new String[0]);
	}

	/**
	 * 判断补发
	 * 
	 * @author liangxr on 2010-6-24
	 * @param loginContext
	 * @throws BusinessException
	 */
	private void checkReData(WaLoginVO waLoginVO) throws BusinessException {
		boolean b = haveMakeRedata(waLoginVO, null);
		if (!b) {
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0459")/*
																								 * @
																								 * res
																								 * "补发数据尚未传递过来，无法继续！"
																								 */);
		}

	}

	private boolean haveMakeRedata(WaLoginVO waLoginVO, String power) throws nc.vo.pub.BusinessException {
		boolean b = false;
		RepayDAO dao = new RepayDAO();
		ReDataVO[] dataVo = dao.queryAllAt(waLoginVO, null, null, power);
		// 没有补发
		if (ArrayUtils.isEmpty(dataVo)) {
			b = true;
		} else {
			// 补发
			dataVo = dao.queryAllAt(waLoginVO, "-1", "-1", power);

			// 已生成汇总表
			if (!ArrayUtils.isEmpty(dataVo)) {
				dataVo = dao.queryAllAt(waLoginVO, "-1", "-1", "  wa_redata.reflag='N' ");
				// 不存在没有提交的人员
				if (ArrayUtils.isEmpty(dataVo)) {
					b = true;
				}
			}
		}
		return b;
	}

	@Override
	public void reTotal(WaLoginVO waLoginVO) throws BusinessException {

		WaState state = waLoginVO.getState();

		if (state == WaState.CLASS_WITHOUT_RECACULATED || state == WaState.CLASS_RECACULATED_WITHOUT_CHECK) {

			IUnitWaClassQuery unitClassQuery = NCLocator.getInstance().lookup(IUnitWaClassQuery.class);
			if (!unitClassQuery.isUnitAllCheckOut(waLoginVO)) {
				throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0460")/*
																									 * @
																									 * res
																									 * "参与汇总的方案有的尚未结帐！"
																									 */);
			}

			PaydataDAO dao = new PaydataDAO();
			WaItemVO[] itemVOs = dao.getUnitDigitItem(waLoginVO);

			sumWaData(waLoginVO, itemVOs);

			// 更新计算标志
			dao.updateStateforTotal(waLoginVO);

		} else {
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0461")/*
																								 * @
																								 * res
																								 * "当前状态 不能汇总！"
																								 */);// new
		}

	}

	// @Override
	// public void collectWaTimesData(WaLoginVO waLoginVO) throws
	// BusinessException{
	// //汇总数值型项目
	// PaydataCollectDAO dao = new PaydataCollectDAO();
	// String pk_waclass = waLoginVO.getPk_prnt_class();
	// String cyear = waLoginVO.getPeriodVO().getCyear();
	// String cperiod = waLoginVO.getPeriodVO().getCperiod();
	//
	// WaInludeclassVO[] childClasses = NCLocator.getInstance()
	// .lookup(IWaClass.class)
	// .queryAllPayOffChildClasses(pk_waclass, cyear, cperiod);
	//
	// dao.collectWaTimesDigitData(waLoginVO, childClasses);
	//
	// //汇总字符日期型项目
	// dao.collectWaTimesNODigitData(waLoginVO, childClasses);
	//
	// //税率的相关信息（本次扣税基数、应纳税所得额taxable_income、税率taxrate、速算扣除数nquickdebuct、费用扣除标准expense_deduction）应该是最后一次发放的扣税基数。
	// dao.collectTaxBase(waLoginVO, childClasses);
	// //已扣税与已扣税基数是0
	// dao.collectTaxedAndTaxedBase(waLoginVO, childClasses);
	// //汇总停发标示
	// dao.collectStopflag(waLoginVO);
	// //更新父方案的状态
	//
	// // 汇总完数据以后，同步月度个人所得税
	// WaLoginContext context = waLoginVO.toWaLoginContext();
	//
	// new ParentClassPsntaxService().doPsnTax(context, childClasses);
	//
	// }

	// 20160125 shenliangc NCdp205577867 薪资多次发放, 发放次数（汇总）数据错误 ,
	// 取消审核需要单独处理本次添加的档案人员
	public void updateCollectDataForThisTime(WaLoginVO waLoginVO) throws BusinessException {
		// 汇总数值型项目
		PaydataCollectDAO dao = new PaydataCollectDAO();
		String pk_waclass = waLoginVO.getPk_prnt_class();
		String cyear = waLoginVO.getPeriodVO().getCyear();
		String cperiod = waLoginVO.getPeriodVO().getCperiod();

		WaInludeclassVO[] childClasses = NCLocator.getInstance().lookup(IWaClass.class)
				.queryAllCheckedChildClasses(pk_waclass, cyear, cperiod);

		WaClassItemVO[] itemVOs = NCLocator.getInstance().lookup(IClassItemQueryService.class)
				.queryItemsByPK_wa_class(pk_waclass, cyear, cperiod);

		dao.updateCollectDataForThisTime(waLoginVO, childClasses, itemVOs);

	}

	/**
	 * * 对发放次数数据进行汇总。所有已经发放的数据都需要汇总
	 * 
	 * @throws BusinessException
	 * 
	 */
	@Override
	public void collectWaTimesData(WaLoginVO waLoginVO) throws BusinessException {
		// 汇总数值型项目
		PaydataCollectDAO dao = new PaydataCollectDAO();
		String pk_waclass = waLoginVO.getPk_prnt_class();
		String cyear = waLoginVO.getPeriodVO().getCyear();
		String cperiod = waLoginVO.getPeriodVO().getCperiod();

		WaInludeclassVO[] childClasses = NCLocator.getInstance().lookup(IWaClass.class)
				.queryAllCheckedChildClasses(pk_waclass, cyear, cperiod);

		dao.collectWaTimesDigitData(waLoginVO, childClasses);

		// 汇总字符日期型项目
		dao.collectWaTimesNODigitData(waLoginVO, childClasses);

		// 税率的相关信息（本次扣税基数、应纳税所得额taxable_income、税率taxrate、速算扣除数nquickdebuct、费用扣除标准expense_deduction）应该是最后一次发放的扣税基数。
		dao.collectTaxBase(waLoginVO, childClasses);
		// 已扣税与已扣税基数是0
		dao.collectTaxedAndTaxedBase(waLoginVO, childClasses);
		// 汇总停发标示
		dao.collectStopflag(waLoginVO);
		// 更新父方案的状态

		// 汇总完数据以后，同步月度个人所得税
		WaLoginContext context = waLoginVO.toWaLoginContext();

		new ParentClassPsntaxService().doPsnTax(context, childClasses);

	}

	public void sumWaData(WaLoginVO waLoginVO, WaItemVO[] itemVOs) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		// 将汇总类别的数据删除掉
		dao.deleteUnitClassPsn(waLoginVO);
		// 得到需要增加的汇总的人员
		PayfileVO[] addPsn = getUnitPsnVOs(waLoginVO);
		// 插入汇总人员
		if (!ArrayUtils.isEmpty(addPsn)) {
			PayfileServiceImpl payfileImpl = new PayfileServiceImpl();
			payfileImpl.addPsnVOs(addPsn);
		}
		// 更新DATA数据
		dao.updateData(waLoginVO, itemVOs);

		// 重新汇总时删除相关表多余人员信息
		dao.deleteUnitRelation(waLoginVO);
	}

	private PayfileVO[] getUnitPsnVOs(WaLoginVO waLoginVO) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		PayfileVO[] datavos = dao.getUnitPsnVOs(waLoginVO);
		if (datavos == null) {
			return null;
		}
		// 去掉重复的， 如果有主职和兼职， 取主职
		HashMap<String, PayfileVO> psnVector = new HashMap<String, PayfileVO>();
		for (PayfileVO vo : datavos) {
			if (!psnVector.containsKey(vo.getPk_psndoc()) || !vo.getPartflag().booleanValue()) {
				PayfileVO psnVO = new PayfileVO();
				psnVO.setPk_psnjob(vo.getPk_psnjob());
				psnVO.setPk_psndoc(vo.getPk_psndoc());
				psnVO.setPk_psnorg(vo.getPk_psnorg());
				psnVO.setTaxtype(vo.getTaxtype());
				psnVO.setTaxtableid(vo.getTaxtableid());
				psnVO.setIsderate(vo.getIsderate());
				psnVO.setDerateptg(vo.getDerateptg());
				psnVO.setIsndebuct(vo.getIsndebuct());

				// guoqt任职组织、任职部门、财务组织、财务部门、成本中心、成本部门
				psnVO.setPk_org(vo.getPk_org());

				psnVO.setWorkorg(vo.getWorkorg());
				psnVO.setWorkorgvid(vo.getWorkorgvid());
				psnVO.setWorkdept(vo.getWorkdept());
				psnVO.setWorkdeptvid(vo.getWorkdeptvid());

				psnVO.setPk_financeorg(vo.getPk_financeorg());
				psnVO.setFiporgvid(vo.getFiporgvid());
				psnVO.setPk_financedept(vo.getPk_financedept());
				psnVO.setFipdeptvid(vo.getFipdeptvid());

				psnVO.setPk_liabilityorg(vo.getPk_liabilityorg());
				psnVO.setPk_liabilitydept(vo.getPk_liabilitydept());
				psnVO.setLibdeptvid(vo.getLibdeptvid());

				psnVO.setPartflag(vo.getPartflag());
				psnVO.setStopflag(vo.getStopflag());

				psnVO.setPk_bankaccbas1(vo.getPk_bankaccbas1());
				psnVO.setPk_bankaccbas2(vo.getPk_bankaccbas2());
				psnVO.setPk_bankaccbas3(vo.getPk_bankaccbas3());

				psnVO.setPk_wa_class(waLoginVO.getPk_wa_class());
				psnVO.setCyear(waLoginVO.getPeriodVO().getCyear());
				psnVO.setCperiod(waLoginVO.getPeriodVO().getCperiod());
				psnVO.setCyearperiod(waLoginVO.getCyear() + waLoginVO.getCperiod());

				psnVO.setPk_group(waLoginVO.getPk_group());
				psnVO.setPk_org(waLoginVO.getPk_org());

				psnVector.put(psnVO.getPk_psndoc(), psnVO);
			}
		}
		return (psnVector.size() == 0) ? null : psnVector.values().toArray(new PayfileVO[psnVector.size()]);
	}

	@Override
	public boolean isPayrollSubmit(WaLoginVO waLoginVO) throws BusinessException {
		return new PaydataDAO().isPayrollSubmit(waLoginVO);
	}

	@Override
	public boolean isPayrollFree(WaLoginVO waLoginVO) throws BusinessException {
		return new PaydataDAO().isPayrollFree(waLoginVO);
	}

	@Override
	public AggPayDataVO queryAggPayDataVOForroll(WaLoginContext loginContext) throws BusinessException {
		return getService().queryAggPayDataVOForroll(loginContext);
	}

	@Override
	public Map<String, AggPayDataVO> queryItemAndSumDataVOForroll(WaLoginContext loginContext) throws BusinessException {
		Map<String, AggPayDataVO> aggvomap = new HashMap<String, AggPayDataVO>();
		aggvomap.put("itemdata", getService().queryAggPayDataVOForroll(loginContext));
		aggvomap.put("sumdata", getService().querySumDataVOForroll(loginContext));
		// guoqt 薪资发放申请及审批增加合计行
		aggvomap.put("sumdataall", getService().querySumDataVOForrollAll(loginContext));

		return aggvomap;
	}

	@Override
	public void updatePaydataFlag(String pk_wa_class, String cyear, String cperiod) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		dao.updatePaydataFlag(pk_wa_class, cyear, cperiod);

	}

	@Override
	public void clearClassItemData(WaClassItemVO vo) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		dao.clearClassItemData(vo);

	}

	@Override
	public BigDecimal getOrgTmSelected(String cacuItem, String whereStr) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		return dao.getOrgTmSelected(cacuItem, whereStr);
	}

	/**
	 * 按组织计算工资总值
	 * 
	 * @author liangxr on 2010-7-12
	 * @param cacuItem
	 * @param waCorpPk
	 * @param accYear
	 * @param accPeriod
	 * @param gzlbId
	 * @return
	 * @throws SQLException
	 */
	@Override
	public BigDecimal getOrgTm(String cacuItem, String pk_org, String accYear, String accPeriod, String pk_wa_class,
			int sumType) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		return dao.getOrgTm(cacuItem, pk_org, accYear, accPeriod, pk_wa_class, sumType);
	}

	@Override
	public Map<String, BigDecimal> getDeptTm(String cacuItem, String pk_org, String accYear, String accPeriod,
			String pk_wa_class, int sumType) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		return dao.getDeptTm(cacuItem, pk_org, accYear, accPeriod, pk_wa_class, sumType);
	}

	@Override
	public BudgetWarnMessageVo budgetAlarm4Pay(WaLoginContext context, String whereStr) throws BusinessException {
		PaydataBudgetAlarmTool tool = new PaydataBudgetAlarmTool();
		return tool.budgetAlarm4Pay(context, whereStr);
	}

	@Override
	public Map<String, BigDecimal> getDeptTmSelected(String cacuItem, String whereStr) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		return dao.getDeptTmSelected(cacuItem, whereStr);
	}

	@Override
	public WaClassItemVO[] getApprovalClassItemVOs(WaLoginContext loginContext) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		return dao.getApprovedClassItemVOs(loginContext);
	}

	/**
	 * 2014/05/23 shenliangc为解决薪资发放节点显示设置对话框中项目名称与本期间发放项目名称不同步问题而修改。
	 */
	@Override
	public List<WaPaydataDspVO> queryPaydataDisplayInfo(WaLoginContext context) throws BusinessException {
		// IItemQueryService itemService =
		// NCLocator.getInstance().lookup(IItemQueryService.class);
		IClassItemQueryService citemService = NCLocator.getInstance().lookup(IClassItemQueryService.class);
		// 查找方案下使用到得所有公共薪资项目
		// WaItemVO[] itemArray =
		// itemService.queryByWaClass(context.getPk_wa_class());

		// 查询当前薪资类别、薪资期间所有的薪资发放项目
		WaItemVO[] itemArray = citemService.queryAllClassItemInfosByPower(context);

		// 查找个人显示设置
		List<WaPaydataDspVO> dspList = this.queryPaydataPersonalDsp(context);
		if (dspList == null || dspList.isEmpty()) {
			// 没有配置个人显示设置，那么按通用显示
			dspList = this.queryPaydataCommonDsp(context);
		}

		if (dspList == null || dspList.isEmpty()) {
			// 既没有个人显示设置，也没有通用显示设置，就按默认显示
			dspList = PaydataDspUtil.queryDefaultDsp();
			if (!ArrayUtils.isEmpty(itemArray)) {
				// 取薪资项目中设置的显示顺序
				WaClassItemVO[] classItemVOs = getUserShowClassItemVOs(context);
				Map<String, WaItemVO> itemMap = CommonUtils.toMap(WaItemVO.PK_WA_ITEM, itemArray);
				List<WaItemVO> itemList = new ArrayList<WaItemVO>();
				for (int i = 0, j = ArrayUtils.getLength(classItemVOs); i < j; i++) {
					WaItemVO itemVO = itemMap.get(classItemVOs[i].getPk_wa_item());
					if (itemVO == null)
						continue;
					itemList.add(itemVO);
				}
				// 转换公共薪资项目并添加到显示项目
				List<WaPaydataDspVO> waItemsDspList = PaydataDspUtil
						.convertWaItemVO(CollectionUtils.isEmpty(itemList) ? null : itemList.toArray(new WaItemVO[0]));
				dspList.addAll(waItemsDspList);
			}

		} else {
			// 设置item_name
			// 2014/05/23 shenliangc为解决薪资发放节点显示设置对话框中项目名称与本期间发放项目名称不同步问题而修改。
			// 名称不同步的原因是查询项目名称逻辑没有添加年度期间限制，全部取方案起始期间的发放项目名称。
			PaydataDspUtil.setPaydataDisplayName(dspList, context);
			// 已设置显示顺序之后，方案若有新加的项目，需要添加
			PaydataDspUtil.addNewlyDsiplayItem(dspList, itemArray);
		}
		return dspList;
	}

	/**
	 * 查找通用显示设置
	 * 
	 * @param context
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private List<WaPaydataDspVO> queryPaydataCommonDsp(WaLoginContext context) throws DAOException {
		StringBuffer condtion = new StringBuffer();
		condtion.append(" pk_wa_class='")
				.append(context.getPk_prnt_class())
				.append("' and type='")
				.append(PaydataDspUtil.commonDsp)
				.append("'  and ( exists ( select 1 from wa_classitem where pk_wa_class = '" + context.getPk_wa_class()
						+ "' and cyear = '" + context.getCyear() + "' and cperiod = '" + context.getCperiod()
						+ "' and itemkey = wa_paydatadsp.item_key) " + "or wa_paydatadsp.item_key in ('"
						+ ICommonAlterName.PSNCODE + "','" + ICommonAlterName.CLERKCODE + "','"
						+ ICommonAlterName.PLSNAME + "','" + ICommonAlterName.ORGNAME + "','"
						+ ICommonAlterName.DEPTNAME + "','" + ICommonAlterName.POSTNAME
						+ "','"
						// 20151103 xiejie3 薪资发放界面添加财务组织财务部门,显示设置进行同步。
						+ ICommonAlterName.FINANCEORG + "','" + ICommonAlterName.FINANCEDEPT + "','"
						+ ICommonAlterName.LIABILITYORG + "','" + ICommonAlterName.LIABILITYDEPT + "','"
						// end
						+ DataVO.TAXTYPE + "','" + DataVO.CACULATEFLAG + "','" + DataVO.CHECKFLAG + "','"
						+ DataVO.CYEAR + "','" + DataVO.CPERIOD + "','" + DataVO.PSNNAME + "') ) ")
				.append("and (item_key IN(SELECT wa_classitem.ITEMKEY ")
				.append("   FROM wa_itempower inner join wa_classitem ON wa_classitem.pk_wa_item=wa_itempower.pk_wa_item and wa_classitem.PK_WA_CLASS=wa_itempower.PK_WA_CLASS ")
				.append("  WHERE wa_itempower.pk_wa_class = '" + context.getPk_prnt_class() + "'")
				.append("    AND wa_itempower.pk_group ='" + context.getPk_group() + "'")
				.append("    AND wa_itempower.pk_org = '" + context.getPk_org() + "'")
				.append("    AND ( wa_itempower.pk_subject IN(SELECT pk_role ")
				.append("				       FROM sm_user_role ")
				.append("				      WHERE cuserid = '" + PubEnv.getPk_user() + "'")
				.append("                   ) or wa_itempower.pk_subject = '" + PubEnv.getPk_user() + "')) ")
				.append(" or wa_paydatadsp.item_key in ('" + ICommonAlterName.PSNCODE + "','"
						+ ICommonAlterName.CLERKCODE + "','" + ICommonAlterName.PLSNAME + "','"
						+ ICommonAlterName.ORGNAME + "','" + ICommonAlterName.DEPTNAME + "','"
						+ ICommonAlterName.POSTNAME
						+ "','"
						// 20151103 xiejie3 薪资发放界面添加财务组织财务部门,显示设置进行同步。
						+ ICommonAlterName.FINANCEORG + "','" + ICommonAlterName.FINANCEDEPT + "','"
						+ ICommonAlterName.LIABILITYORG + "','" + ICommonAlterName.LIABILITYDEPT + "','"
						// end
						+ DataVO.TAXTYPE + "','" + DataVO.CACULATEFLAG + "','" + DataVO.CHECKFLAG + "','"
						+ DataVO.CYEAR + "','" + DataVO.CPERIOD + "','" + DataVO.PSNNAME + "') ) ");
		return (List<WaPaydataDspVO>) queryService.getBaseDao().retrieveByClause(WaPaydataDspVO.class,
				condtion.toString(), "displayseq");
	}

	/**
	 * 查找分人显示设置
	 * 
	 * @param context
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private List<WaPaydataDspVO> queryPaydataPersonalDsp(WaLoginContext context) throws DAOException {
		StringBuffer condtion = new StringBuffer();
		condtion.append(" pk_user = '")
				.append(context.getPk_loginUser())
				.append("' and pk_wa_class='")
				.append(context.getPk_prnt_class())
				.append("' and type='")
				.append(PaydataDspUtil.personalDsp)
				.append("'  and ( exists ( select 1 from wa_classitem where pk_wa_class = '" + context.getPk_wa_class()
						+ "' and cyear = '" + context.getCyear() + "' and cperiod = '" + context.getCperiod()
						+ "' and itemkey = wa_paydatadsp.item_key) or wa_paydatadsp.item_key in ('"
						+ ICommonAlterName.PSNCODE + "','" + ICommonAlterName.CLERKCODE
						+ "','"
						+ ICommonAlterName.PLSNAME
						+ "','"
						+ ICommonAlterName.ORGNAME
						+ "','"
						+ ICommonAlterName.DEPTNAME
						+ "','"
						+ ICommonAlterName.POSTNAME
						+ "','"

						// shenliangc 20140702 银行报盘增加户名
						// 2015-07-30 zhosuze NCdp205099799 薪资发放界面添加财务组织财务部门
						// begin
						// 20151026 xiejie3 解决界面不展示财务组织财务部门
						+ ICommonAlterName.FINANCEORG + "','" + ICommonAlterName.FINANCEDEPT + "','"
						+ ICommonAlterName.LIABILITYORG + "','" + ICommonAlterName.LIABILITYDEPT + "','"
						// end

						+ DataVO.TAXTYPE + "','" + DataVO.CACULATEFLAG + "','" + DataVO.CHECKFLAG + "','"
						+ DataVO.CYEAR + "','" + DataVO.CPERIOD + "','" + DataVO.PSNNAME + "') ) ");

		return (List<WaPaydataDspVO>) getService().getBaseDao().retrieveByClause(WaPaydataDspVO.class,
				condtion.toString(), "displayseq");
	}

	/**
	 * 查找离职结薪通用显示设置
	 * 
	 * @param context
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private List<WaPaydataDspVO> queryPaydataCommonDsp4payleave(WaLoginContext context) throws DAOException {
		StringBuffer condtion = new StringBuffer();
		condtion.append(" pk_wa_class='")
				.append(context.getPk_prnt_class())
				.append("' and type='")
				.append(PaydataDspUtil.commonDsp4payleave)
				.append("'  and ( exists ( select 1 from wa_classitem where pk_wa_class = '"
						+ context.getPk_wa_class()
						+ "' and cyear = '"
						+ context.getCyear()
						+ "' and cperiod = '"
						+ context.getCperiod()
						+ "' and itemkey = wa_paydatadsp.item_key) or wa_paydatadsp.item_key in ('"
						// 20151103 xiejie3 测试要求 离职结薪界面增加人员编码
						+ ICommonAlterName.PSNCODE
						+ "','"
						// end
						+ ICommonAlterName.CLERKCODE
						+ "','"
						+ ICommonAlterName.PLSNAME
						+ "','"
						+ ICommonAlterName.ORGNAME
						+ "','"
						+ ICommonAlterName.DEPTNAME
						+ "','"
						+ ICommonAlterName.POSTNAME
						+ "','"
						// 20151103 xiejie3 薪资发放界面添加财务组织财务部门,显示设置进行同步。
						+ ICommonAlterName.FINANCEORG
						+ "','"
						+ ICommonAlterName.FINANCEDEPT
						+ "','"
						+ ICommonAlterName.LIABILITYORG
						+ "','"
						+ ICommonAlterName.LIABILITYDEPT
						+ "','"

						// end

						// 20151113 xiejie3 NCdp205541370
						// 离职结薪显示设置不显示离职日期、发放日期、发放原因，再次打开显示设置时，这几个字段丢失
						+ DataVO.LEAVEDATE
						// /*离职日期*/
						+ "','"
						+ DataVO.CPAYDATE
						// /*发放日期*/
						+ "','"
						+ DataVO.VPAYCOMMENT
						// /*发放原因*/
						// end
						// 20151127 lizt NCdp205550515
						// 3、离职结薪的显示设置，全不选，卡片界面还显示财务组织，发放标志和发放日期，见附件
						/* 发放标志 */
						+ "','" + DataVO.PAYFLAG

						+ "','" + DataVO.TAXTYPE + "','" + DataVO.CACULATEFLAG + "','" + DataVO.CHECKFLAG + "','"
						+ DataVO.CYEAR + "','" + DataVO.CPERIOD + "','" + DataVO.PSNNAME + "','" + DataVO.PAYFLAG
						+ "','" + DataVO.LEAVEDATE + "','" + DataVO.CPAYDATE + "','" + DataVO.VPAYCOMMENT + "') ) ")
				.append("and (item_key IN(SELECT wa_classitem.ITEMKEY ")
				.append("   FROM wa_itempower inner join wa_classitem ON wa_classitem.pk_wa_item=wa_itempower.pk_wa_item and wa_classitem.PK_WA_CLASS=wa_itempower.PK_WA_CLASS ")
				.append("  WHERE wa_itempower.pk_wa_class = '" + context.getPk_prnt_class() + "'")
				.append("    AND wa_itempower.pk_group ='" + context.getPk_group() + "'")
				.append("    AND wa_itempower.pk_org = '" + context.getPk_org() + "'")
				.append("    AND ( wa_itempower.pk_subject IN(SELECT pk_role ")
				.append("				       FROM sm_user_role ")
				.append("				      WHERE cuserid = '" + PubEnv.getPk_user() + "'")
				.append("                   ) or wa_itempower.pk_subject = '" + PubEnv.getPk_user() + "')) ")
				.append(" or wa_paydatadsp.item_key in ('"
						+ ICommonAlterName.PSNCODE
						+ "','"
						+ ICommonAlterName.CLERKCODE
						+ "','"
						+ ICommonAlterName.PLSNAME
						+ "','"
						+ ICommonAlterName.ORGNAME
						+ "','"
						+ ICommonAlterName.DEPTNAME
						+ "','"
						+ ICommonAlterName.POSTNAME
						+ "','"

						// 20151113 xiejie3 NCdp205541370
						// 离职结薪显示设置不显示离职日期、发放日期、发放原因，再次打开显示设置时，这几个字段丢失
						+ DataVO.LEAVEDATE
						// /*离职日期*/
						+ "','"
						+ DataVO.CPAYDATE
						// /*发放日期*/
						+ "','"
						+ DataVO.VPAYCOMMENT
						// /*发放原因*/
						// end

						// 20151127 lizt NCdp205550515
						// 3、离职结薪的显示设置，全不选，卡片界面还显示财务组织，发放标志和发放日期，见附件
						/* 发放标志 */
						+ "','"
						+ DataVO.PAYFLAG
						// /*发放原因*/

						+ "','"
						// 20151103 xiejie3 薪资发放界面添加财务组织财务部门,显示设置进行同步。
						+ ICommonAlterName.FINANCEORG + "','" + ICommonAlterName.FINANCEDEPT + "','"
						+ ICommonAlterName.LIABILITYORG + "','" + ICommonAlterName.LIABILITYDEPT + "','"
						// end
						+ DataVO.TAXTYPE + "','" + DataVO.CACULATEFLAG + "','" + DataVO.CHECKFLAG + "','"
						+ DataVO.CYEAR + "','" + DataVO.CPERIOD + "','" + DataVO.PSNNAME + "') ) ");
		return (List<WaPaydataDspVO>) queryService.getBaseDao().retrieveByClause(WaPaydataDspVO.class,
				condtion.toString(), "displayseq");
	}

	/**
	 * 查找离职结薪个人显示设置
	 * 
	 * @param context
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private List<WaPaydataDspVO> queryPaydataPersonalDsp4payleave(WaLoginContext context) throws DAOException {
		StringBuffer condtion = new StringBuffer();
		condtion.append(" pk_user = '")
				.append(context.getPk_loginUser())
				.append("' and pk_wa_class='")
				.append(context.getPk_prnt_class())
				.append("' and type='")
				.append(PaydataDspUtil.PersonalDsp4payleave)
				.append("'  and ( exists ( select 1 from wa_classitem where pk_wa_class = '"
						+ context.getPk_wa_class()
						+ "' and cyear = '"
						+ context.getCyear()
						+ "' and cperiod = '"
						+ context.getCperiod()
						+ "' and itemkey = wa_paydatadsp.item_key) or wa_paydatadsp.item_key in ('"
						// 20151103 xiejie3 测试要求 离职结薪界面增加人员编码
						+ ICommonAlterName.PSNCODE
						+ "','"
						// end
						+ ICommonAlterName.CLERKCODE + "','" + ICommonAlterName.PLSNAME + "','"
						+ ICommonAlterName.ORGNAME + "','" + ICommonAlterName.DEPTNAME
						+ "','"
						+ ICommonAlterName.POSTNAME
						+ "','"
						// 20151103 xiejie3 薪资发放界面添加财务组织财务部门,显示设置进行同步。
						+ ICommonAlterName.FINANCEORG + "','" + ICommonAlterName.FINANCEDEPT + "','"
						+ ICommonAlterName.LIABILITYORG + "','" + ICommonAlterName.LIABILITYDEPT
						+ "','"
						// end
						+ DataVO.TAXTYPE + "','" + DataVO.CACULATEFLAG + "','" + DataVO.CHECKFLAG + "','"
						+ DataVO.CYEAR + "','" + DataVO.CPERIOD + "','" + DataVO.PSNNAME + "','" + DataVO.PAYFLAG
						+ "','" + DataVO.LEAVEDATE + "','" + DataVO.CPAYDATE + "','" + DataVO.VPAYCOMMENT + "') ) ");

		return (List<WaPaydataDspVO>) getService().getBaseDao().retrieveByClause(WaPaydataDspVO.class,
				condtion.toString(), "displayseq");
	}

	@Override
	public void deleteDisplayInfo(WaLoginContext context, String type) throws BusinessException {
		StringBuffer whereStr = new StringBuffer();
		whereStr.append(" pk_wa_class='").append(context.getPk_prnt_class()).append("' and type='").append(type)
				.append("'");
		if (PaydataDspUtil.personalDsp.equals(type)) {
			whereStr.append(" and pk_user='").append(context.getPk_loginUser()).append("'");
		}
		getService().getBaseDao().deleteByClause(WaPaydataDspVO.class, whereStr.toString());
	}

	// shenliangc 20140830 合并计税方案部分审核只计算界面上查询出来的人员数据，需要传入过滤条件。
	@Override
	public void reCaculate(WaLoginContext loginContext, String whereCondition) throws BusinessException {
		// 校验范围内的人的二代健保是否都计算完成
		validateHealth(loginContext, whereCondition);
		// 锁定薪资方案(锁父方案)
		if (!StringUtils.isBlank(loginContext.getPk_prnt_class())) {
			BDPKLockUtil.lockString(loginContext.getPk_prnt_class());
		}
		// 锁定子方案
		new SimpleDocLocker().lock(SimpleDocServiceTemplate.UPDATE, loginContext.getWaLoginVO());
		// 校验子方案的版本一致性
		// 版本校验（时间戳校验）
		BDVersionValidationUtil.validateSuperVO(loginContext.getWaLoginVO());

		UFDateTime starttime = new UFDateTime();
		try {
			CaculateTypeVO caculateTypeVO = new CaculateTypeVO();
			caculateTypeVO.setRange(UFBoolean.TRUE);
			caculateTypeVO.setType(UFBoolean.TRUE);
			List<DataVO> superVOs = new ArrayList<DataVO>();
			doDaySalary(loginContext, caculateTypeVO, superVOs.toArray(new DataVO[0]));
		} catch (BusinessException ex) {
			throw ex;
		} catch (Exception e) {
			throw new BusinessException("日薪計算異常！");
		}

		UFDateTime endtime = new UFDateTime();
		long calcutime = (endtime.getMillis() - starttime.getMillis()) / 1000;

		getService().reCaculate(loginContext, whereCondition);

		// 日薪计算结束操作
		clearDaySalary(loginContext, calcutime);
	}

	/**
	 * 校验范围内的人的二代健保是否都计算完成
	 * 
	 * @param loginContext
	 * @param whereCondition
	 * @throws BusinessException
	 */
	private void validateHealth(WaLoginContext loginContext, String whereCondition) throws BusinessException {
		String pk_org = loginContext.getWaLoginVO().getPk_org();
		String pk_wa_class = loginContext.getWaLoginVO().getPk_wa_class();
		String cyear = loginContext.getWaLoginVO().getCyear();
		String cperiod = loginContext.getWaLoginVO().getCperiod();
		String pk_wa_period = loginContext.getWaLoginVO().getPeriodVO().getPk_wa_period();
		// 模拟计算的逻辑得出所有的人员,查询需要进二代健保计算的人
		simulaList(loginContext, whereCondition);
	}

	/**
	 * 1.0 模拟计算的逻辑得出所有的人员,查询需要进二代健保计算的人,并过滤出本次真正需要进行计算的人员 2.0 青春版:
	 * 只進行法人組織的校驗,二代健保會按照法人組織進行全部運算,所以不需要根據人員進行計算.
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private void simulaList(WaLoginContext loginContext, String whereCondition) throws BusinessException {
		Set<String> psndocNeedCaculateSet = new HashSet<>();
		String pk_hrorg = loginContext.getWaLoginVO().getPk_org();
		String pk_wa_class = loginContext.getWaLoginVO().getPk_wa_class();
		String pk_wa_period = loginContext.getWaLoginVO().getPeriodVO().getPk_wa_period();
		String pk_group = loginContext.getWaLoginVO().getPk_group();
		// 计算人力资源下的所有法人组织
		Set<String> legalOrgs = LegalOrgUtilsEX.getOrgsByLegal(pk_hrorg, pk_group);
		for (String pk_org : legalOrgs) {
			// 取劳健保启用参数
			// 当参数为否时，跳出该方法
			// ssx added for 2nd health ins on 2017-07-22
			if (null == SysInitQuery.getParaBoolean(pk_org, "TWHR01")
					|| !SysInitQuery.getParaBoolean(pk_org, "TWHR01").booleanValue()) {
				continue;
			}
			// 到flag表中查找是否已经进行了二代健保计算
			UFDate payDate = findPayDateInDeclaration(pk_wa_class, pk_org, pk_wa_period);
			if (null == payDate) {
				String sqlstrt = "select name from org_orgs where pk_org = '" + pk_org + "'";
				String value = (String) dao.executeQuery(sqlstrt, new ColumnProcessor());
				throw new BusinessException("本人力資源下的法人組織:" + value + ",還未進行二代健保計算,請先點擊二代健保計算,再進行審核!");
			}
		}

	}

	/**
	 * 到二代健保申报节点去查询这个薪资方案,组织,薪资期间的发放日期,如果没找到,则说明没进行过二代健保计算
	 * 
	 * @param pk_wa_class
	 * @param pk_org
	 * @param pk_wa_period
	 * @return
	 * @throws DAOException
	 */
	private UFDate findPayDateInDeclaration(String pk_wa_class, String pk_org, String pk_wa_period) throws DAOException {
		// 查詢三張表中的是否有值,只要有一条即可
		UFDate result = null;
		String sql = "select top 1 pay_date FROM twhr_healthcaculateflag " + " where pk_org = '" + pk_org
				+ "' and pk_wa_class = '" + pk_wa_class + "' and pk_wa_period = '" + pk_wa_period + "'";
		String value = (String) dao.executeQuery(sql, new ColumnProcessor());
		try {
			result = new UFDate(value);
		} catch (Exception e) {
			Debug.debug(e.getMessage());
		}

		return result;
	}

	/**
	 * 用于兼职人员或90A/90B计算所有薪资项的二代健保金额
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @param pk_wa_class
	 * @param pk_wa_period
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	private UFDouble getCurrentPeriodAmount4PartTimePsn(String pk_group, String pk_org, String pk_wa_class,
			String pk_wa_period, String pk_psndoc) throws BusinessException {
		UFDouble rtn = UFDouble.ZERO_DBL;
		List<Map> curAmountPSN = new ArrayList<Map>();
		String strSQL = "select itemkey from wa_classitem cls inner join twhr_waitem_30 tw on cls.pk_wa_item = tw.pk_wa_item where cls.pk_org='"
				+ pk_org
				+ "' and pk_wa_class='"
				+ pk_wa_class
				+ "' and cyear=((select cyear from wa_period where pk_wa_period='"
				+ pk_wa_period
				+ "')) and cperiod=((select cperiod from wa_period where pk_wa_period='"
				+ pk_wa_period
				+ "')) and  tw.ishealthinsparttime = 'Y' ";

		// 把這些薪資項目進行匯總
		List itemKeys = (List) dao.executeQuery(strSQL, new ColumnListProcessor());
		if (itemKeys != null && itemKeys.size() > 0) {
			strSQL = "";
			for (Object itemkey : itemKeys) {
				if (!StringUtils.isEmpty(strSQL)) {
					strSQL = strSQL + "+";
				}
				strSQL = strSQL + "isnull(" + (String) itemkey + ",0)";
			}
			strSQL = "select (" + strSQL + ") curamount, pk_psndoc from wa_data where pk_org='" + pk_org
					+ "' and pk_wa_class='" + pk_wa_class
					+ "' and cyear=((select cyear from wa_period where pk_wa_period='" + pk_wa_period
					+ "')) and cperiod=((select cperiod from wa_period where pk_wa_period='" + pk_wa_period + "')) ";
			curAmountPSN = (List<Map>) dao.executeQuery(strSQL, new MapListProcessor());
		}

		if (curAmountPSN != null && curAmountPSN.size() > 0) {
			for (Map curAmount : curAmountPSN) {
				if (curAmount.containsKey("pk_psndoc")) {
					if (curAmount.get("pk_psndoc").equals(pk_psndoc)) {
						rtn = new UFDouble(Double.parseDouble(curAmount.get("curamount").toString()));
					}
				}
			}
		}

		return rtn;
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
	 * @date 2018年9月21日 上午11:34:42
	 * @description
	 */
	private UFDouble getCurrentPeriodAmount4NotPartTimePsn(String pk_group, String pk_org, String pk_wa_class,
			String pk_wa_period, String pk_psndoc) throws BusinessException {
		UFDouble rtn = UFDouble.ZERO_DBL;
		List<Map> curAmountPSN = new ArrayList<Map>();
		// 找出所有勾选'二代健保累计项目'的薪资项
		String strSQL = "select itemkey from wa_classitem cls inner join twhr_waitem_30 tw on cls.pk_wa_item = tw.pk_wa_item where cls.pk_org='"
				+ pk_org
				+ "' and pk_wa_class='"
				+ pk_wa_class
				+ "' and cyear=((select cyear from wa_period where pk_wa_period='"
				+ pk_wa_period
				+ "')) and cperiod=((select cperiod from wa_period where pk_wa_period='"
				+ pk_wa_period
				+ "')) and  tw.ishealthinsexsum_30 = 'Y' ";

		List itemKeys = (List) dao.executeQuery(strSQL, new ColumnListProcessor());
		if (itemKeys != null && itemKeys.size() > 0) {
			strSQL = "";
			for (Object itemkey : itemKeys) {
				if (!StringUtils.isEmpty(strSQL)) {
					strSQL = strSQL + "+";
				}
				strSQL = strSQL + "isnull( SALARY_DECRYPT(" + (String) itemkey + "),0)";
			}
			// 对所有的勾选项目进行累加,(选计算所有人的,下次查询缓存读取???????? 问下这个人@ssx )
			strSQL = "select (" + strSQL + ") curamount, pk_psndoc from wa_data where pk_org='" + pk_org
					+ "' and pk_wa_class='" + pk_wa_class
					+ "' and cyear=((select cyear from wa_period where pk_wa_period='" + pk_wa_period
					+ "')) and cperiod=((select cperiod from wa_period where pk_wa_period='" + pk_wa_period + "')) ";
			curAmountPSN = (List<Map>) dao.executeQuery(strSQL, new MapListProcessor());
		}

		if (curAmountPSN != null && curAmountPSN.size() > 0) {
			for (Map curAmount : curAmountPSN) {
				if (curAmount.containsKey("pk_psndoc")) {
					if (curAmount.get("pk_psndoc").equals(pk_psndoc)) {
						rtn = new UFDouble(Double.parseDouble(curAmount.get("curamount").toString()));
					}
				}
			}
		}

		return rtn;
	}

	/**
	 * 不需要扣二代健保的人员(兼职人员用)
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
		// 1. 勾選「免扣補充保費」
		String fieldname = PsndocDefTableUtil.getPsnNoPayExtendNHIFieldname(pk_group, pk_org);
		strSQL = "select " + fieldname + " from bd_psndoc where pk_psndoc='" + pk_psndoc + "'";
		String value = (String) dao.executeQuery(strSQL, new ColumnProcessor());
		if (!StringUtils.isEmpty(value) && (new UFBoolean(value)).booleanValue()) {
			needs = false;
		}

		// 3. 二代健保累計項目為0或是所發放的薪資項無二代健保項目
		//
		if (curAmount.equals(UFDouble.ZERO_DBL)) {
			needs = false;
		}

		return needs;
	}

	/**
	 * 不需要扣二代健保的人员
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
		// 1. 勾選「免扣補充保費」
		String fieldname = PsndocDefTableUtil.getPsnNoPayExtendNHIFieldname(pk_group, pk_org);
		strSQL = "select " + fieldname + " from bd_psndoc where pk_psndoc='" + pk_psndoc + "'";
		String value = (String) dao.executeQuery(strSQL, new ColumnProcessor());
		if (!StringUtils.isEmpty(value) && (new UFBoolean(value)).booleanValue()) {
			needs = false;
		}

		// 2. 發放之薪資年月沒有健保投保級距（指薪資計算的薪資期間）
		strSQL = "select isnull(def.glbdef16, 0) healgrade  from " + PsndocDefTableUtil.getPsnHealthTablename()
				+ " def" + " inner join bd_psndoc psn on def.pk_psndoc = psn.pk_psndoc" + " where def.pk_psndoc = '"
				+ pk_psndoc + "' and (def.glbdef3 is null or def.glbdef3=psn.id)"
				+ " and begindate<=(select cenddate from wa_period where pk_wa_period='" + pk_wa_period + "')"
				+ " and isnull(enddate, '9999-12-31')>=(select cstartdate from wa_period where pk_wa_period='"
				+ pk_wa_period + "')" + " and def.glbdef14='Y' and def.dr=0";
		Object rtn = dao.executeQuery(strSQL, new ColumnProcessor());
		if (rtn == null || StringUtils.isEmpty(rtn.toString()) || rtn.toString().equals("0E-8")
				|| (Double.valueOf(rtn.toString())) == 0) {
			needs = false;
		}

		// 3. 二代健保累計項目為0或是所發放的薪資項無二代健保項目
		if (curAmount.equals(UFDouble.ZERO_DBL)) {
			needs = false;
		}

		return needs;
	}

	@Override
	public PayfileVO[] getInPayLeavePsn(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll)
			throws BusinessException {
		return getService().getInPayLeavePsn(waLoginVO, whereCondition, isRangeAll);
	}

	/**
	 * 2014/05/23 shenliangc为解决离职结薪节点显示设置对话框中项目名称与本期间发放项目名称不同步问题而修改。
	 */
	@Override
	public List<WaPaydataDspVO> queryPaydataDisplayInfo4Payleave(WaLoginContext context) throws BusinessException {
		// Map<String,Object> map = new HashMap<String,Object>();
		// IItemQueryService itemService = NCLocator.getInstance().lookup(
		// IItemQueryService.class);
		IClassItemQueryService citemService = NCLocator.getInstance().lookup(IClassItemQueryService.class);
		// 查找方案下使用到得所有公共薪资项目
		// WaItemVO[] itemArray = itemService.queryByWaClass(context
		// .getPk_wa_class());
		// 查询当前薪资类别、薪资期间所有的薪资发放项目
		WaItemVO[] itemArray = citemService.queryAllClassItemInfosByPower(context);

		// boolean flag = false;
		// 查找个人显示设置
		List<WaPaydataDspVO> dspList = this.queryPaydataPersonalDsp4payleave(context);
		if (dspList == null || dspList.isEmpty()) {
			// 没有配置个人显示设置，那么按通用显示
			dspList = this.queryPaydataCommonDsp4payleave(context);
		}
		if (dspList == null || dspList.isEmpty()) {
			// 既没有个人显示设置，也没有通用显示设置，就按默认显示
			dspList = PaydataDspUtil.queryDefaultDsp4PayLeave();
			if (!ArrayUtils.isEmpty(itemArray)) {
				// 取薪资项目中设置的显示顺序
				WaClassItemVO[] classItemVOs = getUserShowClassItemVOs(context);
				Map<String, WaItemVO> itemMap = CommonUtils.toMap(WaItemVO.PK_WA_ITEM, itemArray);
				List<WaItemVO> itemList = new ArrayList<WaItemVO>();
				for (int i = 0, j = ArrayUtils.getLength(classItemVOs); i < j; i++) {
					WaItemVO itemVO = itemMap.get(classItemVOs[i].getPk_wa_item());
					if (itemVO == null)
						continue;
					itemList.add(itemVO);
				}
				// 转换公共薪资项目并添加到显示项目
				List<WaPaydataDspVO> waItemsDspList = PaydataDspUtil
						.convertWaItemVO(CollectionUtils.isEmpty(itemList) ? null : itemList.toArray(new WaItemVO[0]));
				dspList.addAll(waItemsDspList);
			}

		} else {
			// 设置item_name
			PaydataDspUtil.setPaydataDisplayName4PayLeave(dspList, context);
			// 已设置显示顺序之后，方案若有新加的项目，需要添加
			PaydataDspUtil.addNewlyDsiplayItem(dspList, itemArray);
		}
		return dspList;
		/*
		 * if(dspList == null || dspList.isEmpty()){
		 * //既没有个人显示设置，也没有通用显示设置，就按默认显示 dspList =
		 * PaydataDspUtil.queryDefaultDsp4PayLeave(); flag = true; }else{
		 * //设置item_name PaydataDspUtil.setPaydataDisplayName4PayLeave(dspList);
		 * 
		 * } map.put("flag", flag); map.put("list", dspList); return map;
		 */

	}

	@Override
	public WaClassItemShowInfVO getWaClassItemShowInfVO4PayLeave(WaLoginContext loginContext) throws BusinessException {
		WaClassItemVO[] vos1 = getUserShowClassItemVOs(loginContext);

		List<WaPaydataDspVO> list = queryPaydataDisplayInfo4Payleave(loginContext);
		WaClassItemShowInfVO info = new WaClassItemShowInfVO();
		info.setWaClassItemVO(vos1);
		info.setWaPaydataDspVO(list.toArray(new WaPaydataDspVO[list.size()]));

		return info;
	}

	// shenliangc 20140826 时点薪资计算保存之后清除主界面对应人员的发放数据计算标志
	@Override
	public void updateCalFlag4OnTime(PsndocWaVO[] psndocWaVOs) throws BusinessException {
		ArrayList<String> pk_psndocList = new ArrayList<String>();
		String pk_wa_class = null;
		String cyear = null;
		String cperiod = null;
		if (!ArrayUtils.isEmpty(psndocWaVOs)) {
			for (int i = 0; i < psndocWaVOs.length; i++) {
				pk_psndocList.add(psndocWaVOs[i].getPk_psndoc());
			}
			pk_wa_class = psndocWaVOs[0].getPk_wa_class();
			cyear = psndocWaVOs[0].getCyear();
			cperiod = psndocWaVOs[0].getCperiod();
		}
		if (!pk_psndocList.isEmpty() && cyear != null && cperiod != null) {
			this.getService().updateCalFlag4OnTime(pk_wa_class, cyear, cperiod, pk_psndocList.toArray(new String[0]));
		}
	}

	// 20151031 shenliangc 薪资发放项目修改数据来源保存的同时更新发放项目权限数据。
	// 手工输入——》手工输入，非手工输入——》非手工输入，项目权限数据保持不变；
	// 非手工输入——》手工输入，手工输入——》非手工输入，如果修改后为手工输入，则可编辑权更新为Y，否则更新为N。
	// 非手工输入——》手工输入，还要将wa_data中的明细数据更新为初始值。
	public void clearPaydataByClassitem(WaClassItemVO vo) throws BusinessException {
		this.getService().clearPaydataByClassitem(vo);
	}

	/**
	 * 判断薪资方案是否为50薪资
	 * 
	 * @param pk_wa_class
	 * @return 50薪资:1 90AB:2 其他:0
	 * @throws BusinessException
	 * @date 2018年9月22日 下午12:35:51
	 * @description
	 */
	private int checkWaClass(String pk_wa_class) throws BusinessException {
		int type = 0;
		String dbCode = null;
		String sqlStr = "select code from bd_defdoc " + " where bd_defdoc.pk_defdoc= ("
				+ " select declareform from WA_WACLASS where pk_wa_class='" + pk_wa_class + "' )";
		// 判断薪资方案类型
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
	 * 取指定組織、薪資方案、薪資期間的人員列表
	 * 
	 * @param pk_org
	 *            組織
	 * @param pk_wa_class
	 *            薪資方案
	 * @param yearperiod
	 *            薪資期間
	 * @return
	 * @throws BusinessException
	 */
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

	/**
	 * 区分兼职和非兼职人员
	 * 
	 * @param pk_psndocs
	 * @return Map<psnType,Set<pk_psndoc>>
	 * @param payDate
	 * @throws BusinessException
	 * @date 2018年9月21日 上午9:56:57
	 * @description
	 */
	private Map<String, Set<String>> checkPartTimePsn(String[] pk_psndocs, UFDate payDate) throws BusinessException {
		Set<String> allPsndoc = new HashSet<>();// 所有人去重
		allPsndoc.addAll(Arrays.asList(pk_psndocs));

		Map<String, Set<String>> resultSetMap = new HashMap<>();
		resultSetMap.put(PART_TIME_PSN_MAP_KEY, new HashSet<String>());
		resultSetMap.put(NOT_PART_TIME_PSN_MAP_KEY, new HashSet<String>());

		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = insql.getInSQL(pk_psndocs);

		// 取员工最新的健保记录
		String sqlStr = "select g2.pk_psndoc,g2.enddate  from  "
				+ " (select pk_psndoc,max(nvl(enddate,'9999-12-31')) enddateN,max(recordnum) lastnum  "
				+ " from "
				+ PsndocDefTableUtil.getPsnHealthTablename()
				+ " g2 where pk_psndoc in("
				+ psndocsInSQL
				+ ") group by pk_psndoc) c "
				+ " left join  "
				+ PsndocDefTableUtil.getPsnHealthTablename()
				+ " g2 "
				+ " on c.lastnum = g2.recordnum and c.pk_psndoc = g2.pk_psndoc and c.enddateN = nvl(g2.enddate,'9999-12-31') ";
		List<Map> mapList = (List<Map>) dao.executeQuery(sqlStr, new MapListProcessor());
		for (Map map : mapList) {

			if (null != map && null != map.get("pk_psndoc")) {
				// endDate为空,或为9999
				if (null == map.get("enddate") || map.get("enddate").equals("9999-12-31")) {
					// 非兼职人员
					resultSetMap.get(NOT_PART_TIME_PSN_MAP_KEY).add((String) map.get("pk_psndoc"));
					allPsndoc.remove(map.get("pk_psndoc"));
				} else {
					UFLiteralDate enddate = null;
					UFLiteralDate payDateL = null;
					try {
						enddate = new UFLiteralDate((String) map.get("enddate"));
					} catch (Exception e) {
						continue;
					}
					// 同一年发放日期和enddate在同一年
					if (enddate.getYear() == payDate.getYear()) {
						// 非兼职人员
						resultSetMap.get(NOT_PART_TIME_PSN_MAP_KEY).add((String) map.get("pk_psndoc"));
						allPsndoc.remove(map.get("pk_psndoc"));
					}
				}
			}
		}
		// 其余为兼职人员
		resultSetMap.put(PART_TIME_PSN_MAP_KEY, allPsndoc);
		return resultSetMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCalculateCheck(WaLoginContext loginContext, CaculateTypeVO caculateTypeVO, String condition,
			SuperVO... superVOs) throws BusinessException {
		String strSQL = "SELECT pk_psndoc FROM wa_data WHERE wa_data.checkflag = 'N' AND wa_data.pk_wa_class = '"
				+ loginContext.getPk_wa_class() + "' AND wa_data.cyear = '" + loginContext.getCyear()
				+ "' AND wa_data.cperiod = '" + loginContext.getCperiod() + "' AND wa_data.stopflag = 'N'";

		BaseDAO dao = new BaseDAO();
		List<String> psndocs = (List<String>) dao.executeQuery(strSQL, new ColumnListProcessor());

		// 檢查已離職員工是否在薪資方案中
		Collection<PsnJobVO> psnjobvos = dao.retrieveByClause(PsnJobVO.class,
				"endflag='Y' and lastflag='Y' and pk_psnorg = (select pk_psnorg from hi_psnorg where pk_psndoc in ("
						+ strSQL
						+ ") and endflag='Y' and lastflag='Y' and pk_psndoc = hi_psnjob.pk_psndoc and begindate <= '"
						+ loginContext.getWaLoginVO().getPeriodVO().getCenddate().toString()
						+ "' and isnull(enddate, '9999-12-31') >= '"
						+ loginContext.getWaLoginVO().getPeriodVO().getCstartdate().toString() + "')");
		if (psnjobvos != null && psnjobvos.size() > 0) {
			for (PsnJobVO vo : psnjobvos) {
				if (vo.getTrnsevent() == 4
						&& vo.getBegindate().before(loginContext.getWaLoginVO().getPeriodVO().getCstartdate())) {
					PsndocVO psndoc = (PsndocVO) dao.retrieveByPK(PsndocVO.class, vo.getPk_psndoc());
					throw new BusinessException("員工 [" + psndoc.getCode() + "] 已於本期間之前離職，無法進行薪資計算。");
				}
			}
		}

		// MOD by ssx on 2020-04-29
		// 先檢查月薪天數公式在本次計薪是否被引用，如果引用了就不再做檢查
		int cntWageDays = (int) dao.executeQuery(
				"select count(pk_wa_classitem) from wa_classitem where vformula like '%wageDays%' and pk_wa_class='"
						+ loginContext.getPk_wa_class() + "'  and cyear='" + loginContext.getCyear()
						+ "' and cperiod='" + loginContext.getCperiod() + "'  and dr=0;", new ColumnProcessor());

		if (cntWageDays > 0) {
			// ssx added on 2020-02-29
			// 定調資項目檢查
			String sql = "SELECT psn.code  FROM    wa_data wd "
					+ "INNER JOIN    hi_psndoc_wadoc wadoc ON    wadoc.pk_psndoc = wd.pk_psndoc "
					+ " INNER JOIN bd_psndoc psn on wd.pk_psndoc = psn.pk_psndoc "
					+ "WHERE  "
					+ (!StringUtils.isEmpty(condition) && !caculateTypeVO.getRange().booleanValue() ? ("wd."
							+ (condition.contains("wa_data.") ? condition.replace("wa_data.", "") : condition) + " and")
							: "") + " wd.checkflag = 'N' AND wd.pk_wa_class = '" + loginContext.getPk_wa_class()
					+ "' AND wd.cyear = '" + loginContext.getCyear() + "' AND wd.cperiod = '"
					+ loginContext.getCperiod()
					+ "' AND wd.stopflag = 'N' GROUP BY psn.code having sum(case when least('"
					+ loginContext.getWaLoginVO().getPeriodVO().getCenddate().toString()
					+ "',NVL(enddate,'9999-12-31')) >= greatest('"
					+ loginContext.getWaLoginVO().getPeriodVO().getCstartdate().toString()
					+ "',begindate) AND pk_wa_item = '1001A110000000000GEF'  then 1 else 0 end) = 0";
			List<String> psncodes = (List<String>) dao.executeQuery(sql, new ColumnListProcessor());
			if (psncodes != null && psncodes.size() > 0) {
				String message = "";
				for (String psncode : psncodes) {
					if (StringUtils.isEmpty(message)) {
						message += psncode;
					} else {
						message += "," + psncode;
					}
				}
				throw new BusinessException("下列人員月薪天數為0，請檢查人員【本薪】定調資數據。\r\n" + message);
			} // end
		}
		// end MOD

		// ssx remarked on 2019-11-05
		// 为了福委历史数据，暂时不检查年资起算日
		// // 檢查在職員工是否有考勤檔案
		// String sql =
		// "select begindate, enddate from tbm_period where pk_org = '" +
		// loginContext.getPk_org()
		// + "' and accmonth = '" + loginContext.getCperiod() +
		// "' and accyear = '" + loginContext.getCyear()
		// + "'";
		// Map tbmPeriod = (Map) dao.executeQuery(sql, new MapProcessor());
		// // 取年資起算日要根據考勤期間與薪資項目疊加後的最大日期範圍查找組織關係
		// // 26號~31號入職的人，如果只按考勤期間取會取不到組織關係
		// UFLiteralDate maxBeginDate = new
		// UFLiteralDate(String.valueOf(tbmPeriod.get("begindate"))).before(loginContext
		// .getWaLoginVO().getPeriodVO().getCstartdate()) ? new
		// UFLiteralDate(String.valueOf(tbmPeriod
		// .get("begindate"))) :
		// loginContext.getWaLoginVO().getPeriodVO().getCstartdate();
		// UFLiteralDate maxEndDate = new
		// UFLiteralDate(String.valueOf(tbmPeriod.get("enddate"))).after(loginContext
		// .getWaLoginVO().getPeriodVO().getCenddate()) ? new
		// UFLiteralDate(String.valueOf(tbmPeriod
		// .get("enddate"))) :
		// loginContext.getWaLoginVO().getPeriodVO().getCenddate();
		//
		// Collection<PsnOrgVO> psnorgs = dao.retrieveByClause(PsnOrgVO.class,
		// "pk_psndoc in (" + strSQL
		// + ") and begindate <= '" + maxEndDate.toString() +
		// "' and isnull(enddate, '9999-12-31') >= '"
		// + maxBeginDate.toString() + "'");
		// Map<String, UFLiteralDate> psnWorkStartDate = new HashMap<String,
		// UFLiteralDate>();
		// for (PsnOrgVO psnorg : psnorgs) {
		// psnWorkStartDate.put(psnorg.getPk_psndoc(), (UFLiteralDate)
		// psnorg.getAttributeValue("workagestartdate"));
		// }
		//
		// for (String pk_psndoc : psndocs) {
		// if (!psnWorkStartDate.containsKey(pk_psndoc) ||
		// psnWorkStartDate.get(pk_psndoc) == null) {
		// PsndocVO psndoc = (PsndocVO) dao.retrieveByPK(PsndocVO.class,
		// pk_psndoc);
		// throw new BusinessException("員工  [" + psndoc.getCode() +
		// "] 未找到有效的年資起算日，無法進行薪資計算。");
		// }
		// }
		// end
	}

	@Override
	public void doEncryptEx(WaLoginContext loginContext) throws BusinessException {
		DataCaculateService caculateService = new DataCaculateService(loginContext);
		caculateService.doEncryptBySQL();
	}

	@Override
	public boolean sendPayslipByEmail(SuperVO[] payFileVOs, String pk_itemgroup, boolean showZeroItems)
			throws BusinessException {
		try {
			String filePath = RuntimeEnv.getInstance().getNCHome() + "/resources/hr/wa/";
			File file = new File(filePath + "paysliptemplate.tpl");
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader read = new BufferedReader(isr);
			String strBuffer;
			StringBuilder templateContent = new StringBuilder();
			while ((strBuffer = read.readLine()) != null) { // 若檔案內容不為null就執行
				templateContent.append(strBuffer);
			}
			read.close();
			templateContent = applyDataToTemplate(payFileVOs, pk_itemgroup, templateContent, showZeroItems);

			generatePDFDocument(templateContent.toString(), filePath, "123qwe");
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private StringBuilder applyDataToTemplate(SuperVO[] payFileVOs, String pk_itemgroup, StringBuilder templateContent,
			boolean showZeroItems) throws BusinessException {
		Set<String> wadataPKs = new HashSet<String>();
		for (SuperVO vo : payFileVOs) {
			wadataPKs.add(vo.getPrimaryKey());
		}
		List<Map<String, String>> itemkeys = (List<Map<String, String>>) getService().getBaseDao().executeQuery(
				"SELECT it.itemkey, it.name" + MultiLangUtil.getCurrentLangSeqSuffix()
						+ " name FROM wa_itemgroupmember mb "
						+ "INNER JOIN wa_item it ON it.pk_wa_item = mb.pk_waitem WHERE mb.pk_itemgroup='"
						+ pk_itemgroup + "' ORDER BY mb.orderno", new MapListProcessor());

		String strGroupItems = "";
		if (itemkeys != null && itemkeys.size() > 0) {
			for (Map<String, String> itemkey : itemkeys) {
				strGroupItems += ", SALARY_DECRYPT(isnull(" + itemkey.get("itemkey") + ",0)) " + itemkey.get("name");
			}
		} else {
			throw new BusinessException("未發現指定薪資項目分組的已選取薪資項目。");
		}

		String strSQL = "select wd.pk_psndoc, wc.name WACLASSNAME, left(wd.cpaydate,10) PAYDATE, dp.code DEPTNAME, psn.code PSNCODE, "
				+ "psn.name PSNNAME, wd.pk_bankaccbas1 ACCOUNTID, SALARY_DECRYPT(wd.f_5) incometax,  SALARY_DECRYPT(wd.f_49) extrains, "
				+ "SALARY_DECRYPT(f_1) payable, SALARY_DECRYPT(f_3) paid, SALARY_DECRYPT(f_2) deductable "
				+ strGroupItems
				+ " from wa_data wd "
				+ "inner join wa_waclass wc on wd.pk_wa_class = wc.pk_wa_class "
				+ "inner join org_dept dp on wd.workdept = dp.pk_dept "
				+ "inner join bd_psndoc psn on wd.pk_psndoc = psn.pk_psndoc "
				+ "where wd.cpaydate is not null"
				+ " and wd.pk_wa_data in (" + new InSQLCreator().getInSQL(wadataPKs.toArray(new String[0])) + ")";

		List<Map<String, Object>> result = (List<Map<String, Object>>) this.getService().getBaseDao()
				.executeQuery(strSQL, new MapListProcessor());
		if (result == null || result.size() == 0) {
			throw new BusinessException("未發現員工薪資資料，請檢查後重試。");
		} else {
			String headHTML = templateContent.substring(0, templateContent.indexOf("$FOREACH:PK_PSNDOC$"));
			String contentHTMLSeed = templateContent.substring(templateContent.indexOf("$FOREACH:PK_PSNDOC$")
					+ "$FOREACH:PK_PSNDOC$".length(), templateContent.indexOf("$ENDFOR:PK_PSNDOC$"));
			String tailHTML = templateContent.substring(templateContent.indexOf("$ENDFOR:PK_PSNDOC$")
					+ "$ENDFOR:PK_PSNDOC$".length());

			templateContent = new StringBuilder();
			templateContent.append(headHTML);
			for (Map<String, Object> psnRec : result) {
				String contentPsn = contentHTMLSeed;
				for (Entry<String, Object> rec : psnRec.entrySet()) {
					contentPsn = contentPsn.replace("$" + rec.getKey().toUpperCase() + "$",
							String.valueOf(rec.getValue()));
				}

				String innerHeadHTML = contentPsn.substring(0, contentPsn.indexOf("$FOREACH:PK_WA_ITEM$"));
				String innerHTMLSeed = contentPsn.substring(contentPsn.indexOf("$FOREACH:PK_WA_ITEM$")
						+ "$FOREACH:PK_WA_ITEM$".length(), contentPsn.indexOf("$ENDFOR:PK_WA_ITEM$"));
				String innerTailHTML = contentPsn.substring(contentPsn.indexOf("$ENDFOR:PK_WA_ITEM$")
						+ "$ENDFOR:PK_WA_ITEM$".length());

				StringBuilder innerPsnContent = new StringBuilder();
				for (Map<String, String> itemkey : itemkeys) {
					if (showZeroItems || Double.valueOf(String.valueOf(psnRec.get(itemkey.get("name")))) != 0.0D) {
						innerPsnContent = innerPsnContent.append(innerHTMLSeed.replace("$WAITEMNAME$",
								itemkey.get("name")).replace("$WAITEMVALUE$",
								String.valueOf(psnRec.get(itemkey.get("name")))));
					}
				}

				if (innerPsnContent.length() == 0) {
					innerPsnContent = innerPsnContent.append(innerHTMLSeed.replace("$WAITEMNAME$", "").replace(
							"$WAITEMVALUE$", ""));
				}

				templateContent.append(innerHeadHTML);
				templateContent.append(innerPsnContent);
				templateContent.append(innerTailHTML);
			}
			templateContent.append(tailHTML);
		}
		return templateContent;
	}

	private void generatePDFDocument(String content, String filePath, String password) throws Exception {
		// 自訂字型物件
		// XMLWorkerFontProvider fontImp = new XMLWorkerFontProvider(filePath);
		// FontFactory.setFontImp(fontImp);
		// // 註冊FontFactory，定義Font Alias Name = mingliu
		// FontFactory.register(filePath + "mingliu.ttc", "mingliu");
		Document document = new Document(PageSize.A4, 0, 0, 0, 0);
		PdfWriter pw = PdfWriter.getInstance(document, new FileOutputStream(filePath + "payslip.pdf"));
		if (!StringUtils.isEmpty(password)) {
			pw.setEncryption(password.getBytes(), password.getBytes(), PdfWriter.ALLOW_PRINTING,
					PdfWriter.ENCRYPTION_AES_128);
		}
		document.open();
		// Initial HTML轉換物件
		XMLWorkerHelper xmlWorker = XMLWorkerHelper.getInstance();
		// Parse HTML字串，要把自訂的FontProvider傳入
		xmlWorker.parseXHtml(pw, document, new ByteArrayInputStream(content.getBytes("UTF-8")),
				Charset.forName("UTF-8"));
		if (document != null) {
			document.close();
		}
	}

}
