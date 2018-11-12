package nc.impl.wa.repay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.impl.wa.paydata.AbstractCaculateService;
import nc.impl.wa.paydata.PaydataDAO;
import nc.impl.wa.payfile.PayfileServiceImpl;
import nc.impl.wa.pub.WapubImpl;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.wa.IClassItemQueryService;
import nc.itf.hr.wa.IHRWAActionCode;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.IRepayManageService;
import nc.itf.hr.wa.IRepayQueryService;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pubitf.rbac.IDataPermissionPubService;
import nc.vo.hi.psndoc.Attribute;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.caculate.CaculateTypeVO;
import nc.vo.hr.combinesort.SortVO;
import nc.vo.hr.combinesort.SortconVO;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.hr.tools.pub.Pair;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaClassStateHelper;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wa.pub.WaState;
import nc.vo.wa.repay.AggRepayDataVO;
import nc.vo.wa.repay.ReDataVO;
import nc.vo.wa.repay.RepaydataVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class RepayServiceImpl implements IRepayQueryService,
		IRepayManageService {
	private RepayDAO dao = null;

	private RepayDAO getRepayDAO() {
		if (dao == null) {
			dao = new RepayDAO();
		}
		return dao;
	}

	@Override
	public PeriodStateVO[] getAccountPeriods(String pk_wa_class)
			throws BusinessException {
		return getRepayDAO().getAccountPeriods(pk_wa_class);
	}

	@Override
	public RepaydataVO[] getRepayPeriods(WaLoginVO waLoginVO, String deptpower)
			throws BusinessException {
		return getRepayDAO().getRepayPeriods(waLoginVO, deptpower);
	}

	/**
	 * 重新补发
	 * 
	 * @author liangxr on 2010-6-28
	 * @see nc.itf.hr.wa.IRepayManageService#newRePay(nc.vo.wa.pub.WaLoginVO,
	 *      nc.vo.wa.pub.PeriodStateVO[], java.lang.String, java.lang.String)
	 */
	@Override
	public void newRePay(WaLoginVO waLoginVo, PeriodStateVO[] periodvos,
			String where, String deptpower) throws BusinessException {
		// 如果当前期间未审核，如果已审核 return ;
		checkState(waLoginVo);
		// 先删除原始数据
		getRepayDAO().deleteAll(waLoginVo, deptpower);

		// 重新插入数据
		onAddBufa(waLoginVo, periodvos, where, deptpower);

	}

	private void checkState(WaLoginVO waLoginVO) throws BusinessException {
		WaState state = new WapubImpl().checkState(waLoginVO);
		if (WaState.CLASS_PART_CHECKED.equals(state)) {
			throw new BusinessException(ResHelper.getString("60130repaydata",
					"060130repaydata0492")/* @res "该方案数据已部分审核，不能操作。请刷新后重试！" */);
		}
		if (WaState.CLASS_CHECKED_WITHOUT_PAY.equals(state)
				|| WaState.CLASS_CHECKED_WITHOUT_APPROVE.equals(state)
				|| WaState.CLASS_IN_APPROVEING.equals(state)
				|| WaState.CLASS_IS_APPROVED.equals(state)) {
			throw new BusinessException(ResHelper.getString("60130repaydata",
					"060130repaydata0493")/* @res "该方案已审核，不能操作。请刷新后重试！" */);
		}
		if (WaState.CLASS_ALL_PAY.equals(state)) {
			throw new BusinessException(ResHelper.getString("60130repaydata",
					"060130repaydata0494")/* @res "该方案已发放，不能操作。请刷新后重试！" */);
		}
		if (WaState.CLASS_MONTH_END.equals(state)) {
			throw new BusinessException(ResHelper.getString("60130repaydata",
					"060130repaydata0495")/* @res "该方案已结帐，不能操作。请刷新后重试！" */);
		}
	}

	@Override
	public void deleteAll(WaLoginVO waLoginVo, String deptpower)
			throws BusinessException {
		checkState(waLoginVo);
		// 删掉所有数据
		getRepayDAO().deleteAll(waLoginVo, deptpower);
		// 重置wa_data中补发数据
		getRepayDAO().resetWaData(waLoginVo, deptpower);
		getRepayDAO().updatePeriodState(waLoginVo, "caculateflag",
				UFBoolean.FALSE);
	}

	@Override
	public void onAddBufa(WaLoginVO waLoginVO, PeriodStateVO[] periodvos,
			String where, String deptpower) throws BusinessException {

		// 先删除原始数据
		WaLoginContext context = null;
		// addStopflag;
		String condition = addStopFlag(where);
		condition += " and wa_data.pk_psndoc in(select pk_psndoc from wa_data "
				+ "where pk_wa_class = '" + waLoginVO.getPk_wa_class()
				+ "' and cyear = '" + waLoginVO.getCyear()
				+ "' and cperiod = '" + waLoginVO.getCperiod() + "' "
				// 20151106 shenliangc NCdp205536216
				// 薪资补发生成补发期间的档案没有对当前期间停发人员进行过滤。
				+ " and wa_data.stopflag = 'N')";
		PayfileServiceImpl dataImpl = new PayfileServiceImpl();

		// 循环每个期间。
		// vRedata中记录需要更新的数据
		Vector<RepaydataVO> vRedata = new Vector<RepaydataVO>();
		for (PeriodStateVO vo : periodvos) {
			// 查出人员\
			context = createContext(vo.getPk_wa_class(), vo.getCyear(),
					vo.getCperiod(), vo.getPk_org());

			// @autor erl 2011-6-10 9:06 多加了参数，传了null,因为和薪资档案公用了一个方法，而薪资档案需要修改。
			PayfileVO[] datavos = dataImpl.queryPayfileVOByCondition(context,
					condition, null);

			if (!ArrayUtils.isEmpty(datavos)) {

				RepaydataVO repayvo = null;
				for (PayfileVO datavo : datavos) {
					// 转换成补发数据---已经停发的人员不进行转换
					if (!datavo.getStopflag().booleanValue()) {
						repayvo = new RepaydataVO();
						repayvo.setPk_wa_class_z(getZpk_wa_class(waLoginVO));
						repayvo.setCyear(waLoginVO.getPeriodVO().getCyear());
						repayvo.setCperiod(waLoginVO.getPeriodVO().getCperiod());

						repayvo.setPk_wa_class(getYpk_wa_class(waLoginVO));
						repayvo.setCreyear(vo.getCyear());
						repayvo.setCreperiod(vo.getCperiod());
						repayvo.setPk_group(datavo.getPk_group());
						repayvo.setPk_org(datavo.getPk_org());
						repayvo.setPk_psndoc(datavo.getPk_psndoc());
						repayvo.setPk_psnjob(datavo.getPk_psnjob());
						repayvo.setPk_psnorg(datavo.getPk_psnorg());
						repayvo.setWorkorg(datavo.getWorkorg());
						repayvo.setWorkorgvid(datavo.getWorkorgvid());
						repayvo.setWorkdept(datavo.getWorkdept());
						repayvo.setWorkdeptvid(datavo.getWorkdeptvid());
						repayvo.setAssgid(datavo.getAssgid());
						repayvo.setStatus(VOStatus.NEW);
						vRedata.add(repayvo);
					}
				}
			}
		}

		// vRedata中记录所有需要更新的数据
		if (!vRedata.isEmpty()) {
			RepaydataVO[] repayvos = new RepaydataVO[vRedata.size()];
			vRedata.copyInto(repayvos);
			// 存储
			insertArray(repayvos);
		}

		// 删掉汇总
		getRepayDAO().deleteTotalData(waLoginVO, deptpower);
		// 重置wa_data中补发数据
		getRepayDAO().resetWaData(waLoginVO, deptpower);
		getRepayDAO().updatePeriodState(waLoginVO, "caculateflag",
				UFBoolean.FALSE);
	}

	/**
	 * 停发人员不进行薪资补发。
	 * 
	 * @param where
	 * @return
	 */
	private String addStopFlag(String where) {
		// addStopflag;
		String stopFlagConditon = "  wa_data.stopflag = 'N' ";
		return WherePartUtil.concatConditon(where, stopFlagConditon);
	}

	private void insertArray(RepaydataVO[] billVos) throws DAOException {
		getRepayDAO().getBaseDao().insertVOArray(billVos);// insertVOArrayReturnVOArray(vos)saveBill(billVos);
	}

	private String getYpk_wa_class(WaLoginVO waLoginVO) {
		return WaLoginVOHelper.getParentClassPK(waLoginVO);
	}

	private String getZpk_wa_class(WaLoginVO waLoginVO) {
		return WaLoginVOHelper.getChildClassPK(waLoginVO);
	}

	/**
	 * 返回元数据持久化服务对象
	 */
	private static IMDPersistenceService getMDPersistenceService() {
		return MDPersistenceService.lookupPersistenceService();
	}

	/**
	 * 构造wacontext
	 * 
	 * @author liangxr on 2010-5-17
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @return
	 */
	private WaLoginContext createContext(String pk_wa_class, String cyear,
			String cperiod, String pk_org) {
		WaLoginContext context = new WaLoginContext();
		context.setPk_org(pk_org);
		WaLoginVO waLoginVO = new WaLoginVO();
		waLoginVO.setPk_wa_class(pk_wa_class);
		waLoginVO.setPk_org(pk_org);
		PeriodStateVO periodStateVO = new PeriodStateVO();
		periodStateVO.setCyear(cyear);
		periodStateVO.setCperiod(cperiod);
		waLoginVO.setPeriodVO(periodStateVO);
		context.setWaLoginVO(waLoginVO);
		return context;
	}

	@Override
	public AggRepayDataVO queryAggRepayDataVOByCondition(
			WaLoginContext loginContext, String condition, String orderCondtion)
			throws BusinessException {
		AggRepayDataVO aggRepaydataVO = new AggRepayDataVO();

		// 薪资类别信息.
		// 普通薪资类别，查询当前起期间
		WaLoginVO loginVO = loginContext.getWaLoginVO();
		WaLoginVO newLoginVO = WaClassStateHelper
				.getWaclassVOWithState(loginVO);
		newLoginVO.setReyear(loginVO.getReyear());
		newLoginVO.setReperiod(loginVO.getReperiod());
		aggRepaydataVO.setLoginVO(newLoginVO);

		// 有权限的薪资项目
		// 普通薪资类别，查询被补发期间的有权限项目
		// 多次发薪的薪资类别，查询 子类别当前薪资期间的有权限项目。 需要需求确认一下。注意： 程序还没有这样做啊
		WaClassItemVO[] classItemVOs = new PaydataDAO()
				.getUserClassItemVOs(loginContext);
		aggRepaydataVO.setClassItemVOs(classItemVOs);
		// 2015-11-5 zhousze 查询当前界面有几条数据，用于查询提示信息。2015-11-17 zhousze
		// 这里不需要传入orderby
		// 的条件，这样反而会引起排序时报错。2015-11-25 NCdp205549295 zhousze
		// 不能用PaydataDAO查询，表不对应，现在修改如下 begin
		String[] pks = getRepayDAO().queryPKSByCondition(loginContext,
				condition, orderCondtion);
		aggRepaydataVO.setDataPKs(pks);
		// end

		// 薪资发放项目
		ReDataVO[] dataVOs = queryByCondition(loginContext, condition,
				orderCondtion);

		// 2016-12-12 zhousze 薪资加密：这里处理薪资补发数据查询，数据解密 begin
		for (ReDataVO vo : dataVOs) {
			HashMap<String, Object> map = vo.appValueHashMap;
			Object[] pkArray = map.keySet().toArray();
			for (int i = 0; i < pkArray.length; i++) {
				if (pkArray[i].toString().startsWith("f_")) {
					UFDouble value = (UFDouble) map.get(pkArray[i]);
					value = new UFDouble(SalaryDecryptUtil.decrypt(value
							.toDouble()));
					vo.setAttributeValue(pkArray[i].toString(), value);
				}
			}
		}
		// end

		aggRepaydataVO.setReDataVOs(dataVOs);
		return aggRepaydataVO;

	}

	public ReDataVO[] queryByCondition(WaLoginContext context,
			String condition, String orderCondtion) throws BusinessException {
		if (StringUtils.isBlank(orderCondtion)) {
			// 如果没有排序字段 先到数据库中查询有没有当前用户的排序设置 by wangqim
			SortVO sortVOs[] = null;
			SortconVO sortconVOs[] = null;
			String strCondition = " func_code='" + context.getNodeCode() + "'"
					+ " and group_code= 'TableCode' and ((pk_corp='"
					+ PubEnv.getPk_group() + "' and pk_user='"
					+ PubEnv.getPk_user()
					+ "') or pk_corp ='@@@@') order by pk_corp";

			sortVOs = (SortVO[]) NCLocator.getInstance()
					.lookup(IPersistenceRetrieve.class)
					.retrieveByClause(null, SortVO.class, strCondition);
			Vector<Attribute> vectSortField = new Vector<Attribute>();
			if (sortVOs != null && sortVOs.length > 0) {
				strCondition = "pk_hr_sort='" + sortVOs[0].getPrimaryKey()
						+ "' order by field_seq ";
				sortconVOs = (SortconVO[]) NCLocator.getInstance()
						.lookup(IPersistenceRetrieve.class)
						.retrieveByClause(null, SortconVO.class, strCondition);
				for (int i = 0; sortconVOs != null && i < sortconVOs.length; i++) {
					Pair<String> field = new Pair<String>(
							sortconVOs[i].getField_name(),
							sortconVOs[i].getField_code());
					Attribute attribute = new Attribute(field, sortconVOs[i]
							.getAscend_flag().booleanValue());
					vectSortField.addElement(attribute);
				}
				orderCondtion = getOrderby(vectSortField);
			}
		}
		return getRepayDAO()
				.queryByCondition(context, condition, orderCondtion);
	}

	// wangqim
	public static String getOrderby(Vector<Attribute> vectSortField) {
		if (vectSortField == null || vectSortField.size() == 0) {
			return "";
		}
		String strOrderBy = "";
		for (Attribute attr : vectSortField) {
			String strFullCode = attr.getAttribute().getValue();
			strOrderBy = strOrderBy + "," + strFullCode
					+ (attr.isAscend() ? "" : " desc");
		}
		return strOrderBy.length() > 0 ? strOrderBy.substring(1) : "";
	}

	@Override
	public void delete(Object object, WaLoginVO waLoginVO)
			throws BusinessException {
		checkState(waLoginVO);
		getRepayDAO().delete(object, waLoginVO);
	}

	@Override
	public void update(Object object, WaLoginVO waLoginVO)
			throws BusinessException {
		checkState(waLoginVO);
		getRepayDAO().update(object, waLoginVO);
	}

	@Override
	public void onReplace(WaLoginVO waLoginVO, String whereCondition,
			WaClassItemVO replaceItem, String formula) throws BusinessException {
		checkState(waLoginVO);

		// 添加数据维护权
		String poweConditon = NCLocator
				.getInstance()
				.lookup(IDataPermissionPubService.class)
				.getDataPermissionSQLWherePartByMetaDataOperation(
						PubEnv.getPk_user(), IHRWADataResCode.REPAYDATA,
						IHRWAActionCode.REPLACE, PubEnv.getPk_group());
		if (StringUtils.isBlank(whereCondition)) {
			whereCondition = poweConditon;
		} else {
			whereCondition = whereCondition
					+ WherePartUtil.formatAddtionalWhere(poweConditon);
		}
		// 添加数据使用权
		String userConditon = WaPowerSqlHelper.getWaPowerSql(
				PubEnv.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_redata");
		if (StringUtils.isBlank(whereCondition)) {
			whereCondition = userConditon;
		} else {
			whereCondition = whereCondition
					+ WherePartUtil.formatAddtionalWhere(userConditon);
		}
		getRepayDAO()
				.onReplace(waLoginVO, whereCondition, replaceItem, formula);
	}

	@Override
	public void copy(WaLoginVO waLoginVO, String nextyear, String nextperiod)
			throws BusinessException {
		checkState(waLoginVO);
		// 查询所有复制 的项目
		String pk_org = waLoginVO.getPk_org();
		String pk_wa_class = waLoginVO.getPk_wa_class();
		String cyear = waLoginVO.getPeriodVO().getCyear();
		String cperiod = waLoginVO.getPeriodVO().getCperiod();

		IClassItemQueryService itemQuery = NCLocator.getInstance().lookup(
				IClassItemQueryService.class);
		WaClassItemVO[] waItems = itemQuery.queryCustomItemInfos(pk_org,
				pk_wa_class, cyear, cperiod);
		getRepayDAO().copy(waLoginVO, nextyear, nextperiod, waItems);
		// 删掉汇总
		getRepayDAO().deleteTotalData(waLoginVO, null);
		// 重置wa_data中补发数据
		getRepayDAO().resetWaData(waLoginVO, null);
		getRepayDAO().updatePeriodState(waLoginVO, "caculateflag",
				UFBoolean.FALSE);
	}

	@Override
	public void total(WaLoginVO waLoginVO) throws BusinessException {
		checkState(waLoginVO);
		// 判断是否可以汇总
		// 加锁
		String pk_org = waLoginVO.getPk_org();
		String pk_wa_class = getYpk_wa_class(waLoginVO);
		String pk_wa_class_z = getZpk_wa_class(waLoginVO);// .getPk_wa_class();

		String waYear = waLoginVO.getPeriodVO().getCyear();
		String waPeriod = waLoginVO.getPeriodVO().getCperiod();
		String deptpower = null;
		mayTotal(waLoginVO, deptpower);
		// 未审核
		// 将汇总数据删掉
		getRepayDAO().deleteTotalData(waLoginVO, deptpower);

		// 得到所有人员
		RepaydataVO[] allUnitPsnId = getRepayDAO().getAllUnitPsn(waLoginVO,
				deptpower);
		// 按照 pk_psndoc 过滤调重复的人员（如果人员发生任职关系改变）

		allUnitPsnId = groupByPsndoc(allUnitPsnId);

		// 得到所有数字型薪资项目
		IClassItemQueryService itemQuery = NCLocator.getInstance().lookup(
				IClassItemQueryService.class);
		String where = " wa_item.iitemtype = '" + TypeEnumVO.FLOATTYPE.value()
				+ "'";
		WaClassItemVO[] unitDigitItem = itemQuery.queryItemInfoVO(pk_org,
				pk_wa_class, waYear, waPeriod, where);

		// 重新汇总(get人时已经有部门权限)
		getRepayDAO().reTotal(pk_wa_class, pk_wa_class_z, waYear, waPeriod,
				allUnitPsnId, unitDigitItem);

		// 重置wa_data中补发数据
		getRepayDAO().resetWaData(waLoginVO, deptpower);

		getRepayDAO().updatePeriodState(waLoginVO, "caculateflag",
				UFBoolean.FALSE);

	}

	RepaydataVO[] groupByPsndoc(RepaydataVO[] allUnitPsnId) {
		Map<String, RepaydataVO> map = new HashMap<String, RepaydataVO>();
		for (int i = 0; i < allUnitPsnId.length; i++) {
			map.put(allUnitPsnId[i].getPk_psndoc(), allUnitPsnId[i]);
		}

		return map.values().toArray(new RepaydataVO[0]);
	}

	public void mayTotal(WaLoginVO waLoginVO, String deptpower)
			throws BusinessException {
		ReDataVO[] dataVo = null;
		String wherepart = " rtrim(wa_redata.creyear) <> '-1' and rtrim(wa_redata.creperiod) <> '-1' and wa_redata.caculateflag ='N' ";
		wherepart += WherePartUtil.formatAddtionalWhere(deptpower);

		dataVo = getRepayDAO().queryAllAt(waLoginVO, null, null, wherepart);

		// 存在没有计算的人
		if (!ArrayUtils.isEmpty(dataVo)) {
			throw new BusinessException(ResHelper.getString("60130repaydata",
					"060130repaydata0496")/* @res "存在未计算的人员，不能汇总！" */);
		}

	}

	/**
	 * 总额补发
	 * 
	 * @author liangxr on 2010-6-12
	 * @see nc.itf.hr.wa.IRepayManageService#updateArrayTotalRe(nc.vo.wa.repay.ReDataVO,
	 *      java.lang.String[][], nc.vo.wa.pub.WaLoginVO, java.lang.String,
	 *      java.lang.String[])
	 */
	@Override
	public void updateArrayTotalRe(ReDataVO[] datavos, WaLoginVO waLoginVO)
			throws BusinessException {
		checkState(waLoginVO);
		String pk_wa_class = waLoginVO.getPk_wa_class();
		String pk_org = waLoginVO.getPk_org();
		String cyear = waLoginVO.getPeriodVO().getCyear();
		String cperiod = waLoginVO.getPeriodVO().getCperiod();

		IClassItemQueryService itemQuery = NCLocator.getInstance().lookup(
				IClassItemQueryService.class);

		// 20151111 shenliangc 总额补发可以操作所有数值型项目。
		// String cond = " wa_classitem.ifromflag = 2";// 手工输入项目
		// 20151125 shenliangc NCdp205548947 进行薪资总额补发多期间后，系统项目的值也被总额补发了。
		String cond = " wa_item.defaultflag <> 'Y' ";

		WaClassItemVO[] editItems = itemQuery.queryItemInfoVO(pk_org,
				pk_wa_class, cyear, cperiod, cond);

		// 仅仅选择数值型
		ArrayList<WaClassItemVO> itemlist = new ArrayList<WaClassItemVO>();
		for (int i = 0; i < editItems.length; i++) {
			if (editItems[i].getIitemtype() != null
					&& editItems[i].getIitemtype().equals(
							TypeEnumVO.FLOATTYPE.value())) {
				itemlist.add(editItems[i]);
			}
		}
		editItems = itemlist.toArray(new WaClassItemVO[0]);

		for (ReDataVO datavo : datavos) {
			getRepayDAO().updateTotalRe(datavo, editItems, waLoginVO);
		}

		// 删掉汇总
		// getRepayDAO().deleteTotalData(waLoginVO, null);
		getRepayDAO().updatePeriodState(waLoginVO, "caculateflag",
				UFBoolean.FALSE);
		// 重置wa_data中补发数据
		getRepayDAO().resetWaData(waLoginVO, null);

	}

	@Override
	public boolean isAllCaculated(WaLoginVO waLoginVO) throws BusinessException {
		return getRepayDAO().isAllCaculated(waLoginVO);
	}

	@Override
	public void addToWadata(WaLoginVO waLoginVO) throws BusinessException {
		checkState(waLoginVO);
		if (!mayAddToWadata(waLoginVO)) {
			throw new BusinessException(ResHelper.getString("60130repaydata",
					"060130repaydata0497")/* @res "各补发期间尚未重新汇总，无法继续！" */);
		}
		addToChange(waLoginVO);

	}

	/**
	 * 判断是否可以累加到薪资发放
	 * 
	 * @author liangxr on 2010-6-21
	 * @param waLoginVO
	 * @return
	 * @throws DAOException
	 */
	public boolean mayAddToWadata(WaLoginVO waLoginVO) throws BusinessException {

		mayTotal(waLoginVO, null);
		ReDataVO[] dataVo = getRepayDAO().queryAllAt(waLoginVO, "-1", "-1",
				" caculateflag='N' ");

		// 存在没有计算的人
		if (!ArrayUtils.isEmpty(dataVo)) {
			return false;
		}
		return true;
	}

	/**
	 * 将补发数据累加到薪资发放
	 * 
	 * @author liangxr on 2010-6-22
	 * @param waLoginVO
	 * @throws BusinessException
	 */
	private void addToChange(WaLoginVO waLoginVO) throws BusinessException {

		// 未审核
		// String deptpower = null;
		// deptpower = nc.vo.jcom.lang.StringUtil.replaceAllString(deptpower,
		// "wa_redata", "redata");
		String pk_org = waLoginVO.getPk_org();
		String pk_wa_class = waLoginVO.getPk_wa_class();
		String cyear = waLoginVO.getCyear();
		String cperiod = waLoginVO.getCperiod();

		IClassItemQueryService itemQuery = NCLocator.getInstance().lookup(
				IClassItemQueryService.class);
		String where = " wa_item.iitemtype = '" + TypeEnumVO.FLOATTYPE.value()
				+ "'";
		WaClassItemVO[] itemvos = itemQuery.queryItemInfoVO(pk_org,
				pk_wa_class, cyear, cperiod, where);

		String spart = "";
		if (itemvos != null && itemvos.length > 0) {
			int nsize = itemvos.length;
			for (int i = 0; i < nsize; i++) {
				spart += " redata." + itemvos[i].getItemkey() + " +";
			}

			spart = spart.substring(0, spart.length() - 2);
		} else {
			spart = "0";
		}

		getRepayDAO().toChange(waLoginVO, "wa_data", "", spart);
		getRepayDAO().updateReFlag(waLoginVO, "");

		getRepayDAO().updatePeriodState(waLoginVO, "caculateflag",
				UFBoolean.FALSE);

	}

	/**
	 * 自动补发
	 * 
	 * @author liangxr on 2010-6-22
	 * @see nc.itf.hr.wa.IRepayManageService#autoRepay(nc.vo.wa.pub.WaLoginVO,
	 *      nc.vo.wa.classitem.WaClassItemVO[], java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void autoRepay(WaLoginVO waLoginVO, WaClassItemVO[] items,
			String inputyear, String inputperiod, String whereSql)
			throws BusinessException {
		checkState(waLoginVO);
		// 添加数据维护权
		String operateConditon = NCLocator
				.getInstance()
				.lookup(IDataPermissionPubService.class)
				.getDataPermissionSQLWherePartByMetaDataOperation(
						PubEnv.getPk_user(), IHRWADataResCode.REPAYDATA,
						IHRWAActionCode.EDIT, PubEnv.getPk_group());
		if (StringUtils.isBlank(whereSql)) {
			whereSql = operateConditon;
		} else {
			whereSql = whereSql
					+ WherePartUtil.formatAddtionalWhere(operateConditon);
		}
		// 添加数据使用权
		String userConditon = WaPowerSqlHelper.getWaPowerSql(
				PubEnv.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_redata");
		if (StringUtils.isBlank(whereSql)) {
			whereSql = userConditon;
		} else {
			whereSql = whereSql
					+ WherePartUtil.formatAddtionalWhere(userConditon);
		}

		getRepayDAO().autoRepay(waLoginVO, items, inputyear, inputperiod,
				whereSql);

		getRepayDAO().updatePeriodState(waLoginVO, "caculateflag",
				UFBoolean.FALSE);
		// 重置wa_data中补发数据
		getRepayDAO().resetWaData(waLoginVO, whereSql);
	}

	@Override
	public void onCaculate(WaLoginContext loginContext,
			CaculateTypeVO caculateTypeVO, String condition)
			throws BusinessException {
		checkState(loginContext.getWaLoginVO());
		AbstractCaculateService caculateService = new ReDataCaculateService(
				loginContext, caculateTypeVO, condition);
		caculateService.doCaculate();

		// getRepayDAO().updateCacuflag(loginContext.getWaLoginVO());

	}

	@Override
	public boolean isDataExist(String pk_wa_class, String inputyear,
			String inputperiod) throws BusinessException {
		return getRepayDAO().isValueExist(pk_wa_class, inputyear, inputperiod);

	}

	@Override
	public PeriodStateVO getRepayLastPeriod(WaLoginVO waLoginVo)
			throws BusinessException {

		return getRepayDAO().getRepayLastPeriod(waLoginVo);
	}
}
