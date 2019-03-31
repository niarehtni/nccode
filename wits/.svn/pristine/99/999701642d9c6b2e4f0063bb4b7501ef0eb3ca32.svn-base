package nc.impl.wa.category;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nc.bs.bd.cache.CacheProxy;
import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.businessevent.IEventType;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uap.oid.OidGenerator;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.bs.uif2.validation.Validator;
import nc.hr.frame.persistence.AppendBaseDAO;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.frame.persistence.IValidatorFactory;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.impl.wa.classitem.ClassItemManageServiceImpl;
import nc.impl.wa.classitempower.ClassItemPowerQueryServiceImpl;
import nc.impl.wa.classitempower.ClassItemPowerServiceImpl;
import nc.impl.wa.item.ItemServiceImpl;
import nc.impl.wa.period.WaPeriodImpl;
import nc.impl.wa.period.WaPeriodQueryImpl;
import nc.impl.wa.psntax.PsnTaxImpl;
import nc.impl.wa.pub.WapubDAO;
import nc.itf.hr.wa.IClassItemManageService;
import nc.itf.hr.wa.IClassItemQueryService;
import nc.itf.hr.wa.IItemQueryService;
import nc.itf.hr.wa.IMonthEndManageService;
import nc.itf.hr.wa.IPaydataManageService;
import nc.itf.hr.wa.IPayfileManageService;
import nc.itf.hr.wa.IPayrollQueryService;
import nc.itf.hr.wa.IPayslipService;
import nc.itf.hr.wa.IWaClass;
import nc.itf.hr.wa.IWaSalaryctymgtConstant;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.md.data.access.NCObject;
import nc.md.model.MetaDataException;
import nc.md.model.impl.MDEnum;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.hr.pub.FormatVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ExtendedAggregatedValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.sm.UserVO;
import nc.vo.uif2.LoginContext;
import nc.vo.util.BDPKLockUtil;
import nc.vo.util.BDVersionValidationUtil;
import nc.vo.wa.category.AssignclsVO;
import nc.vo.wa.category.ChildClassParas;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.category.WaFiorgVO;
import nc.vo.wa.category.WaInludeclassVO;
import nc.vo.wa.classitem.RoundTypeEnum;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.classitempower.ItemPowerVO;
import nc.vo.wa.classpower.ClassPowerVO;
import nc.vo.wa.end.WaClassEndVO;
import nc.vo.wa.grade.WaPsnhiBVO;
import nc.vo.wa.grade.WaPsnhiVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.payroll.AggPayrollVO;
import nc.vo.wa.payroll.PayrollVO;
import nc.vo.wa.payslip.AggPayslipVO;
import nc.vo.wa.payslip.PayslipItemVO;
import nc.vo.wa.payslip.PayslipVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.periodsate.WaPeriodstateVO;
import nc.vo.wa.psntax.TaxItemVO;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaBusinessDocName;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wa_tax.TaxupgradeDef;
import nc.vo.wabm.util.WaCacheUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * -
 * 
 * @author: xuanlt
 * @date: 2009-11-19 上午11:24:10
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WaClassImpl implements IWaClass, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6981411481065237057L;
	//
	WaPeriodQueryImpl waPeriodQuery = new WaPeriodQueryImpl();

	WaPeriodImpl waPeriodImpl = new WaPeriodImpl();

	IItemQueryService waitemServiceImpl = new ItemServiceImpl();
	ClassItemManageServiceImpl classitemQuery = new ClassItemManageServiceImpl();
//	20151016 xiejie3 NCdp205509857  问题：用户没有复制的薪资方案的项目权限
//	ItemPowerQuery用来查询薪资发放项目权限。
	ClassItemPowerQueryServiceImpl ItemPowerQuery=new ClassItemPowerQueryServiceImpl();
//	end
	// 如何处理校验工厂，需要进一步讨论
	private final IValidatorFactory docValidatorFactory = new WaClassValidatorFactory();
	private final SimpleDocServiceTemplate docService = new SimpleDocServiceTemplate(
			WaBusinessDocName.WACLASS_DOC_NAME);
	private final WaClassDAO dao = new WaClassDAO();

	public WaClassDAO getWaClassDAO() {
		return dao;
	}

	public SimpleDocServiceTemplate getDocService() {
		/**
		 * 设定锁 设定校验工厂
		 */
		docService.setLocker(new WaClassDocLocker());
		docService.setValidatorFactory(docValidatorFactory);
		docService.setLazyLoad(true);
		return docService;

	}

	/**
	 * 根据指定的pk_child_class 查询对应的附方案pk 如果没有，说明pk_child_class本身就是父方案。返回自身。
	 */
	public String queryParentClasspk(String pk_child_class, String cyear,
			String cperiod) throws BusinessException {
		String condition = " pk_childclass = '" + pk_child_class
				+ "' and cyear = '" + cyear + "' and cperiod = '" + cperiod
				+ "' ";
		WaInludeclassVO[] vos = getDocService()
				.queryByCondition(WaInludeclassVO.class, condition);

		if (ArrayUtils.isEmpty(vos)) {
			return pk_child_class;
		} else {
			return vos[0].getPk_parentclass();
		}

	}

	/**
	 * @author xuanlt on 2009-11-19
	 * @see nc.itf.hr.wa.IWaClass#queryWaClassByConditon(javax.security.auth.login.LoginContext,
	 *      java.lang.String)
	 */
	@Override
	public WaClassVO[] queryWaClassByConditon(LoginContext context,
			String condition) throws BusinessException {
		LoginContext waLoginContext = new LoginContext();
		waLoginContext.setPk_group(context.getPk_group());
		waLoginContext.setPk_loginUser(context.getPk_loginUser());
		waLoginContext.setPk_org(context.getPk_org());

		return queryWaClass(waLoginContext, condition);

	}

	/**
	 * @author xuanlt on 2010-5-17
	 * @see nc.itf.hr.wa.IWaClass#queryWaClassByUnitClass(java.lang.String)
	 */
	@Override
	public WaClassVO[] queryWaClassByUnitClass(String uintclasspk)
			throws BusinessException {
		WaClassVO[] vos = new WaClassVO[0];
		try {
			vos = getWaClassDAO().queryWaClassByUnitClass(uintclasspk);
		} catch (DAOException de) {
			Logger.error(de.getMessage(), de);
			throw new BusinessException(de.getMessage());

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0068")/* @res "查询汇总薪资方案的基础薪资方案失败!" */);
		}

		return vos;

	}

	/**
	 * @author xuanlt on 2009-11-19
	 * @see nc.itf.hr.wa.IWaClass#queryWaClassByGroup(javax.security.auth.login.LoginContext)
	 */
	@Override
	public WaClassVO[] queryWaClassByGroup(LoginContext context)
			throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @author xuanlt on 2009-11-19
	 * @see nc.itf.hr.wa.IWaClass#queryWaClassByOrg(javax.security.auth.login.LoginContext)
	 */
	@Override
	public WaClassVO[] queryWaClassByOrg(LoginContext context)
			throws BusinessException {
		WaClassVO[] vos = new WaClassVO[0];
		try {
			vos = getWaClassDAO().queryWaClassByOrg(context);
		} catch (DAOException de) {
			Logger.error(de.getMessage(), de);
			throw new BusinessException(de.getMessage());

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0069")/* @res "查询薪资方案失败!" */);
		}

		return vos;
	}

	@Override
	public WaClassVO[] queryWaClassByOrg(LoginContext context,
			boolean withRangeRule, boolean withStopClass)
					throws BusinessException {
		WaClassVO[] vos = new WaClassVO[0];
		try {
			vos = getWaClassDAO().queryWaClassByOrg(context, withRangeRule,
					withStopClass);
		} catch (DAOException de) {
			Logger.error(de.getMessage(), de);
			throw new BusinessException(de.getMessage());

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0069")/* @res "查询薪资方案失败!" */);
		}

		return vos;
	}

	@Override
	public WaClassVO queryWaclassBypk(String pk_waclass)
			throws BusinessException {
		return getWaClassDAO().queryWaclassBypk(pk_waclass);
	}

	@Override
	public WaClassVO[] queryWaClassByOrgWithStopclass(LoginContext context)
			throws BusinessException {
		WaClassVO[] vos = new WaClassVO[0];
		try {
			vos = getWaClassDAO().queryWaClassByOrgWtihStopclass(context);
		} catch (DAOException de) {
			Logger.error(de.getMessage(), de);
			throw new BusinessException(de.getMessage());

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0069")/* @res "查询薪资方案失败!" */);
		}

		return vos;
	}

	@Override
	public WaClassVO[] queryWaClassByOrg(LoginContext context,
			boolean withRangeRule) throws BusinessException {
		WaClassVO[] vos = new WaClassVO[0];
		try {
			vos = getWaClassDAO().queryWaClassByOrg(context, withRangeRule);
		} catch (DAOException de) {
			Logger.error(de.getMessage(), de);
			throw new BusinessException(de.getMessage());

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0069")/* @res "查询薪资方案失败!" */);
		}

		return vos;
	}

	@Override
	public WaPsnhiVO[] queryRangRuleByClass(String classPK)
			throws BusinessException {
		WaPsnhiVO[] vos = new WaPsnhiVO[0];
		try {
			vos = getWaClassDAO().queryRangRuleByClass(classPK);
		} catch (DAOException de) {
			Logger.error(de.getMessage(), de);
			throw new BusinessException(de.getMessage());

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0070")/* @res "查询计薪规则失败!" */);
		}

		return vos;
	}

	@Override
	public WaClassVO[] queryWaClass(LoginContext context)
			throws nc.vo.pub.BusinessException {
		return queryWaClass(context, null);
	}

	@Override
	public WaClassVO[] queryWaClass(LoginContext context, String condition)
			throws BusinessException {

		WaClassVO[] vos = new WaClassVO[0];
		try {
			if (StringUtils.isEmpty(condition)) {
				return queryWaClassByOrg(context);
			}
			if (condition.trim().startsWith("and")) {
				condition = condition.trim().substring(3);
			}
			String wheresql = " pk_group = '" + context.getPk_group()
					+ "' and pk_org= '" + context.getPk_org() + "' and "
					+ condition;

			vos = getWaClassDAO().queryWaClassByCondition(wheresql,
					UFBoolean.FALSE);
		} catch (DAOException de) {
			Logger.error(de.getMessage(), de);
			throw new BusinessException(de.getMessage());

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0069")/* @res "查询薪资方案失败!" */);
		}
		return vos;
	}

	@Override
	public WaClassVO[] queryGroupAssignedWaclass(WaClassVO vo)
			throws BusinessException {
		return getWaClassDAO().queryGroupAssignedWaclass(vo);
	}

	/**
	 * 如何保证事务一致性 事务什么时候 commmit? 插入类别 (1)锁定币种 (2)锁定税率 (3)校验会计期间： (4)校验名称重复！
	 * 改变校验方式！ (5)保存薪资方案 (6)插入初始化的薪资项目！ (7)分派类别与项目权限 (8)分配部门权限
	 */
	@Override
	public WaClassVO insertWaClass(WaClassVO vo) throws BusinessException {
		// BDPKLockUtil.lockString(vo.getPk_wa_class());
		// 保存薪资方案
		WaClassVO tempVO = getDocService().insert(vo);

		// 插入计薪规则
		insertRangeRule(vo);

		// //插入计薪范围
		insertWaRange(vo);

		// 插入财务组织
		insertWaFiorg(vo);

		// 插入期间状态
		insertPeriodState(tempVO);
		// 插入初始化的薪资项目！
		insertWaClassitem(vo);

		//
		// 分派类别与项目权限

		// 如果是多次发薪，需要创建第一个子类别
		// if(vo.getMutipleflag().booleanValue()){
		// 非集团方案则创建第一个子方案
		if (vo.getMutipleflag().equals(UFBoolean.TRUE)) {
			WaClassVO childVO = createChildClass(vo);
			// 创建合并计税组-->改为使用主子关系合并计税
			// createTaxGroup(tempVO,childVO);
		}

		
		// 薪资方案默认授权
		insertWaClassPower(vo);
		

		//插入个人所得税申报项目
		insertWaTaxitem(vo);

		WaCacheUtils.synCache(WaClassVO.TABLE_NAME, WaClassItemVO.TABLE_NAME);

		return getWaClassDAO().completeRange(tempVO); // ;
	}


	/**
	 * 
	 * @param classpk
	 * @return
	 * @throws DAOException
	 */
	@Override
	public WaFiorgVO[] queryWaFiorgByClass(String classpk) throws DAOException {
		return new WaClassDAO().queryWaFiorgByClass(classpk);
	}



	public WaFiorgVO[] queryCheckedWaFiorgByClass(String classpk) throws DAOException{
		return new WaClassDAO().queryCheckedWaFiorgByClass(classpk);
	}

	public WaClassVO insertChildClass(WaClassVO vo, ChildClassParas para)
			throws BusinessException {
		// 保存薪资方案
		WaClassVO tempVO = getDocService().insert(vo);

		// 插入期间状态
		if (para == null) {
			insertPeriodState(tempVO);
		} else {
			insertPeriodState(tempVO, para.getCyear(), para.getCperiod());
		}

		WaCacheUtils.synCache(WaClassVO.TABLE_NAME, WaClassItemVO.TABLE_NAME);
		return tempVO; // ;
	}

	/**
	 * 
	 * @param parentVO
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public WaClassVO createChildClass(WaClassVO parentVO, ChildClassParas paras)
			throws BusinessException {
		// 加锁 因为创建子方案与添加子方案是需要同步的。
		BDPKLockUtil.lockSuperVO(parentVO);
		// 版本校验（时间戳校验）。加锁+时间戳，保证版本一致性
		BDVersionValidationUtil.validateSuperVO(parentVO);

		if(WaLoginVOHelper.isNormalClass(parentVO)){
			//正常结薪方案需要转换成多次发放方案
			changeNormal2Leaveclass(parentVO);
		}
		// 复制薪资类别
		WaClassVO childVO = copyParentVO(parentVO, paras);

		// 插入子方案
		childVO = insertChildClass(childVO, paras);

		// 添加父子关系
		insertRelation(parentVO, childVO, paras);

		// 插入发放项目与发放档案
		insertChildItemAndPsn(parentVO, childVO, paras);
		// 20150728 xiejie3,补丁合并，NCdp205367162  厦门港务控股集团有限公司  多次发放银行报盘第二次报盘无法成功报错
		//20150512 shenliangc 修改父方案银企直连数据制单状态
		updateFipendflag(childVO);

		// 合并计税组中添加新成员-->改为使用主子关系合并计税
		// insertMemberVO2Group(parentVO, childVO);

		// 更新父方案的发放状态为-未发放
		getWaClassDAO().updateParentClassPayOff(parentVO, "N");
		if(parentVO.getMutipleflag().equals(UFBoolean.FALSE)){
			//正常结薪方案需要转换成多次发放方案
			getWaClassDAO().updateMutipleFlag(parentVO, true);
		}
		return childVO;
	}

	/**
	 * 
	 * @param parentVO
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public WaClassVO createChildClass(WaClassVO parentVO)
			throws BusinessException {

		// 复制薪资类别
		WaClassVO childVO = copyParentVO(parentVO);

		// 插入子方案
		childVO = insertChildClass(childVO, null);

		// 添加父子关系
		WaInludeclassVO invo = insertRelation(parentVO, childVO);

		// 插入发放项目与发放档案
		insertChildItemAndPsn(parentVO, childVO, null);

		return childVO;
	}

	@Override
	public void changeNormal2Leaveclass(WaClassVO vo)
			throws BusinessException {

		if(!WaLoginVOHelper.isNormalClass(vo)){
			return ;
		}
		/*   	WaClassVO subvo =  NCLocator.getInstance().lookup(IWaClass.class).queryChildClass(vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod(), 1);
    	if(subvo!=null){
    		deleteWaClassTimes(vo,true);
    	}*/
		// 复制薪资类别
		WaClassVO childVO = copyParentVO(vo);

		// 插入子方案
		childVO = insertFirstChildClass4Leave(vo,childVO);

		// 添加父子关系
		insertRelation(vo, childVO);

		// 插入发放项目与发放档案
		insertFirstChildItemAndPsn4Leave(vo, childVO);
		//20150728 xiejie3 补丁合并，NCdp205367162  厦门港务控股集团有限公司  多次发放银行报盘第二次报盘无法成功报错，begin
		//20150512 shenliangc 修改父方案银企直连数据制单状态
		updateFipendflag(vo);
        //end
		// {MOD:新个税补丁}
		// begin
		// 2016-1-11 NCdp205570938 zhousze 用于薪资方案未增加次数时，已经银企直联传递数据到工资清单，然后再增加发放次数时，这里在wa_cashcard中存放的pk_wa_class是对应的
		// 汇总方案的pk_wa_class，所以再去取消第一次的传递时无法取消。这里的处理是更新wa_cashcard中的pk_wa_class为子方案的pk，即对应数据
		// 的方案
		updateWaCashcardPkwaclass(vo, childVO);
		// end
		//更新父方案期间状态
		updatePeriodState4Leave(vo);

		//更换发放申请单对应方案
		updatePayroll4Leave(vo, childVO);

		//对于参加合并计税的方案。 wa_data中依赖父方案的prewadata，改为依赖子方案.   pk_wa_class,pk_psndoc, cyear,cperiod
		updataPrewadata(vo,childVO);

		//更新个人所得税数据
		updatePsnTaxdata(vo,childVO);

		//更新个别调整
		updateWaDatas(vo,childVO);
		
		//20150728 xiejie3 补丁合并，NCdp205367162  厦门港务控股集团有限公司  多次发放银行报盘第二次报盘无法成功报错，begin
		//20150512 shenliangc 修改父方案银企直连数据制单状态
			}
			
			private void updateFipendflag(WaClassVO vo)throws DAOException{
				getWaClassDAO().updateFipendflag(vo);
		//end
	}
	
// {MOD:新个税补丁}
// begin		
	private void  updateWaCashcardPkwaclass(WaClassVO vo, WaClassVO childvo) throws DAOException{
		getWaClassDAO().updateWaCashcardPkwaclass(vo, childvo);
	}
// end

	private void updataPrewadata(WaClassVO parentVO, WaClassVO childVO) throws DAOException{
		getWaClassDAO().updataPrewadata(parentVO, childVO);
	}
	private WaClassVO insertFirstChildClass4Leave(WaClassVO parentVO, WaClassVO childVO)
			throws BusinessException {
		WaClassVO tempVO = getDocService().insert(childVO);
		getWaClassDAO().insertPeriodState4Leave(parentVO, childVO);
		WaCacheUtils.synCache(WaClassVO.TABLE_NAME);
		return tempVO; // ;
	}
	private void insertFirstChildItemAndPsn4Leave(WaClassVO parentVO, WaClassVO childVO) throws BusinessException {
		NCLocator.getInstance().lookup(IClassItemManageService.class)
		.copyClassItems(parentVO, childVO);

		getWaClassDAO().insertFirstChildPsn4Leave(parentVO, childVO);
	}
	private void updatePeriodState4Leave(WaClassVO vo) throws BusinessException{

		getWaClassDAO().updatePeriodState4Leave(vo);
	}
	private void updatePayroll4Leave(WaClassVO parentVO, WaClassVO childVO) throws BusinessException{
		AggPayrollVO  aggPayrollVO = 	NCLocator.getInstance().lookup(IPayrollQueryService.class).getPayroll(parentVO.getPk_wa_class(), parentVO.getCyear(), parentVO.getCperiod());
		if(aggPayrollVO==null){
			return;
		}
		PayrollVO payrollVO = (PayrollVO)aggPayrollVO.getParentVO();
		payrollVO.setPk_wa_class(childVO.getPk_wa_class());
		payrollVO.setBatch(1);
		getWaClassDAO().getBaseDao().updateVO(payrollVO, new String[]{PayrollVO.PK_WA_CLASS,PayrollVO.BATCH});
	}


	private void insertChildItemAndPsn(WaClassVO parentVO, WaClassVO childVO,
			ChildClassParas para) throws BusinessException {
		// 得到 上一次 发薪的classvo
		Integer preTimes = 1;
		WaClassVO srcvo = null;

		if (para != null) {
			preTimes = para.getBatch() - 1;
			srcvo = queryChildClass(parentVO.getPk_wa_class(),
					childVO.getCyear(), childVO.getCperiod(), preTimes);

			if(preTimes==1){
				String updateSql = "update wa_redata  set pk_wa_class_z='" +srcvo.getPk_wa_class()+"' " +
						"where pk_wa_class_z = '"+parentVO.getPk_wa_class()+"' and cyear = '"+srcvo.getCyear()+"' and cperiod = '"+srcvo.getCperiod()+"' ";
				getWaClassDAO().getBaseDao().executeUpdate(updateSql);
				//更新补发数据到子方案。
			}

		}

		if (para != null && para.getSynClassItem().booleanValue()) {
			// 需要同步项目
			NCLocator.getInstance().lookup(IClassItemManageService.class)
			.copyClassItems(srcvo, childVO);

		} else {
			// 插入初始化的薪资项目！
			insertWaClassitem(childVO);
		}

		if (para != null && para.getSynPsnData().booleanValue()) {
			// 同步档案
			// 创建waloginvo
			WaLoginVO fromWaClass = new WaLoginVO();
			fromWaClass.setPk_wa_class(srcvo.getPk_wa_class());
			fromWaClass.setCyear(srcvo.getCyear());
			fromWaClass.setCperiod(srcvo.getCperiod());
			fromWaClass.setPk_org(srcvo.getPk_org());

			/**
			 * 目标需要 periodvo
			 */
			WaLoginVO destWaClass = new WaLoginVO();
			destWaClass.setPk_wa_class(childVO.getPk_wa_class());
			destWaClass.setCyear(childVO.getCyear());
			destWaClass.setCperiod(childVO.getCperiod());
			destWaClass.setPk_org(childVO.getPk_org());

			PeriodStateVO periodStateVO = new PeriodStateVO();
			periodStateVO.setPk_wa_class(childVO.getPk_wa_class());
			periodStateVO.setPk_org(childVO.getPk_org());
			periodStateVO.setCyear(childVO.getCyear());
			periodStateVO.setCperiod(childVO.getCperiod());
			destWaClass.setPeriodVO(periodStateVO);

			//
			NCLocator.getInstance().lookup(IPayfileManageService.class)
			.copyWaPsn(fromWaClass, destWaClass, false, false);// (parentVO,
			// childVO);
		}
	}

	/**
	 * 
	 * @param parentVO
	 * @param childVO
	 * @throws BusinessException
	 */
	private WaInludeclassVO insertRelation(WaClassVO parentVO, WaClassVO childVO)
			throws BusinessException {

		WaInludeclassVO vo = constructRelationVO(parentVO, childVO);
		getMDPersistenceService().saveBill(vo);

		return vo;
	}

	private WaInludeclassVO insertRelation(WaClassVO parentVO,
			WaClassVO childVO, ChildClassParas para) throws BusinessException {

		WaInludeclassVO vo = constructRelationVO(parentVO, childVO);

		// 确保 次数是正确的。
		para.setBatch(vo.getBatch());

		vo.setMemo(para.getMemo());

		getMDPersistenceService().saveBill(vo);

		return vo;
	}

	private WaInludeclassVO constructRelationVO(WaClassVO parentVO,
			WaClassVO childVO) throws BusinessException {
		WaInludeclassVO includeClassvo = new WaInludeclassVO();
		includeClassvo.setStatus(VOStatus.NEW);
		includeClassvo.setPk_parentclass(parentVO.getPk_wa_class());
		includeClassvo.setCyear(parentVO.getCyear());
		includeClassvo.setCperiod(parentVO.getCperiod());

		includeClassvo.setBatch(getNextBatchTimes(parentVO));

		includeClassvo.setPk_childclass(childVO.getPk_wa_class());
		includeClassvo.setPk_group(childVO.getPk_group());
		includeClassvo.setPk_org(childVO.getPk_org());

		return includeClassvo;
	}

	@Override
	public Integer getNextBatchTimes(WaClassVO waclassvo)
			throws BusinessException {

		return getWaClassDAO().getBatchTimes(waclassvo);

	}

	private void deleteIncludeClass(String pk_waclass) throws BusinessException {
		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate("  delete from wa_inludeclass where pk_parentclass = '"
					+ pk_waclass + "'");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0072")/* @res "删除父子方案关系表失败" */);
		}
	}

	private void deleteRelation(String pk_childclass) throws BusinessException {
		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate("  delete from wa_inludeclass where pk_childclass = '"
					+ pk_childclass + "'");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0072")/* @res "删除父子方案关系表失败" */);
		}
	}

	@Override
	public void deleteWaClassTimes(WaClassVO vo,boolean isfirst)
			throws nc.vo.pub.BusinessException {
		// 加锁
		BDPKLockUtil.lockSuperVO(vo);
		// 版本校验（时间戳校验）。加锁+时间戳，保证版本一致性
		BDVersionValidationUtil.validateSuperVO(vo);
		// 校验是否审核，是否最后一个次数
		if(!isfirst){
			validateDelWaClassTimes(vo);
		}

		// 删除 发薪次数对应的子类别
		WaClassVO childvo = getWaClassDAO().queryWaClassByParentClass(
				vo.getPk_wa_class());

		if (getWaClassDAO().isHaveCheckPsn(childvo)) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0073")/* @res "方案下有人员已审核，不能删除！" */);
		}

		IPayfileManageService payfileService = NCLocator.getInstance().lookup(
				IPayfileManageService.class);
		childvo.setPk_prnt_class(vo.getPk_wa_class());
		payfileService.deleteFromTotal(childvo);

		deleteCore(childvo.getPk_wa_class());

		// 删除关系表
		deleteRelation(childvo.getPk_wa_class());

		// 删除合并计税组成员-->改为使用主子关系合并计税
		// deleteGroupMember(childvo.getPk_wa_class());

		// 删除发放次数对应的薪资类别
		deleteTimesClass(childvo.getPk_wa_class());

		// 如果所有子方案都发放完，则修改父方案状态
		if (getWaClassDAO().isChildPayoff(vo))
			getWaClassDAO().updateParentClassPayOff(vo, "Y");

		CacheProxy.fireDataDeleted(WaClassVO.TABLE_NAME,
				childvo.getPk_wa_class());

	}

	/**
	 * 校验是否只剩一个发放次数
	 * 
	 * @param vo
	 * @throws nc.vo.pub.BusinessException
	 */
	public void validateDelWaClassTimes(WaClassVO vo)
			throws nc.vo.pub.BusinessException {
		WaInludeclassVO[] vos = getWaClassDAO().querySubClasses(vo.getPk_wa_class(),
				vo.getCyear(), vo.getCperiod(),true);
		if (ArrayUtils.isEmpty(vos)) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0074")/* @res "该薪资方案已经没有发放次数可以删除了" */);
		} else if (vos.length == 1) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0075")/* @res "不能删除第一次发薪" */);
		}

	}


	private void deleteTimesClass(String pk_childclass)
			throws BusinessException {
		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate(" delete from wa_waclass where pk_wa_class = '"
					+ pk_childclass + "' ");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0077")/* @res "删除发放次数对应的薪资类别失败" */);
		}
	}

	private WaClassVO copyParentVO(WaClassVO parentVO) {

		return copyParentVO(parentVO, null);
	}

	private WaClassVO copyParentVO(WaClassVO parentVO, ChildClassParas paras) {
		//
		WaClassVO childVO = (WaClassVO) parentVO.clone();
		//
		int batch = 1;
		if (paras != null) {
			batch = paras.getBatch();
		}
		childVO.setName(parentVO.getMultilangName());
		childVO.setCode(getChildClassCode(parentVO, batch));
		childVO.setMutipleflag(UFBoolean.FALSE);
		childVO.setLeaveflag(UFBoolean.FALSE);
		childVO.setShowflag(UFBoolean.FALSE);
		childVO.setWaPsnhiVOs(null);
		childVO.setWaPsnhiBVOs(null);
		childVO.setPk_wa_class(null);
		childVO.setWaclassFiorgvo(null);

		return childVO;
	}

	private String getChildClassCode(WaClassVO parentVO, int batch) {
		return parentVO.getCode() + parentVO.getCyear() + parentVO.getCperiod()
				+ "_times_" + batch;
	}

	@Override
	public WaClassVO copyInsertWaClass(WaClassVO vo, WaClassVO copyed)
			throws nc.vo.pub.BusinessException {
		// 保存薪资方案
		WaClassVO tempVO = getDocService().insert(vo);

		// 插入计薪规则
		insertRangeRule(vo);

		// //插入计薪范围
		insertWaRange(vo);
		
		// 插入财务组织
        insertWaFiorg(vo);

		// 插入期间状态
		insertPeriodState(tempVO);
		// 插入初始化的薪资项目！
		insertWaClassitem(vo, copyed);

		// 如果是多次发薪，需要创建第一个子类别
		if(vo.getMutipleflag().equals(UFBoolean.TRUE)){
			copyChildClass(vo);
		}
		// 创建合并计税组-->改为使用主子关系合并计税
		// createTaxGroup(tempVO,childVO);


		// 薪资方案默认授权
		insertWaClassPower(vo);
		
		// 	20151016 xiejie3 NCdp205509857  问题：用户没有复制的薪资方案的项目权限，begin
		insertWaClassItemPower(vo,copyed);
		//end
		//
		// 分派类别与项目权限
		return getWaClassDAO().completeRange(tempVO); // ;
	}

	private void insertWaClassPower(WaClassVO waClass) throws DAOException {
		ClassPowerVO clsPowerVO = new ClassPowerVO();
		clsPowerVO.setClassid(waClass.getPk_wa_class());
		clsPowerVO.setModuleflag(Integer.valueOf(0));
		clsPowerVO.setPk_group(waClass.getPk_group());
		clsPowerVO.setPk_org(waClass.getPk_org());
		clsPowerVO.setPk_subject(waClass.getCreator());
		clsPowerVO.setSubject_type(IWaSalaryctymgtConstant.SUB_TYPE_USER);
		getWaClassDAO().getBaseDao().insertVO(clsPowerVO);
	}
	
	

	/**
	 * 复制子方案
	 * 
	 * @param parentVO
	 *            WaClassVO
	 * @return WaClassVO
	 * @throws BusinessException
	 */
	private WaClassVO copyChildClass(WaClassVO parentVO)
			throws BusinessException {

		// 复制薪资类别
		WaClassVO childVO = copyParentVO(parentVO);

		// 插入子方案
		childVO = insertChildClass(childVO, null);

		// 添加父子关系
		insertRelation(parentVO, childVO);

		// 插入初始化的薪资项目！
		insertWaClassitem(childVO, parentVO);

		return childVO;
	}

	/**
	 * 更新薪资方案 (2)核查类别是否已经审核 (3)锁定币种 ，方案表，当前薪资方案 (4)参见dmo
	 * 
	 * @author xuanlt on 2009-11-27
	 * @see nc.itf.hr.wa.IWaClass#insertWaClass(nc.vo.wa.category.WaClassVO)
	 */
	@Override
	public WaClassVO updateWaClass(WaClassVO vo) throws BusinessException {
		// 核查类别是否已经审核，已经审核则不允许修改！
		if (currentPeriodHasCheckedData(vo)) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0078")/* @res "当前薪资方案存在已经审核的数据，不允许修改" */);

		}
		BDPKLockUtil.lockString(vo.getPk_wa_class());
		// 得到旧的薪资方案
		WaClassVO oldvo = queryWaClassByPK(vo.getPk_wa_class());
		// 集团分配的薪资方案因为需要显示所属组织为集团，所以此处还原PK_ORG为组织PK
		String tempOrg = vo.getPk_org();
		vo.setPk_org(oldvo.getPk_org());
		// 保存薪资方案
		WaClassVO tempVO = updateWaClassInfo(vo, oldvo);

		// 非集团方案更新子方案
		if (WaLoginVOHelper.isMultiClass(vo)) {
			// 如果是多次发放方案，则更新子方案，因为存在审核数据就不能更新，所以能够更新的方案肯定只有一个子方案。
			// 根据oldvo查询出原来的子方案
			WaClassVO oldChildVO = queryChildClass(oldvo.getPk_wa_class(),
					oldvo.getCyear(), oldvo.getCperiod(), 1);
			WaClassVO newChildVO = copyParentVO(vo);
			newChildVO.setPk_wa_class(oldChildVO.getPk_wa_class());
			newChildVO.setTs(oldChildVO.getTs());
			updateWaClassInfo(newChildVO, oldChildVO);
			//			// 更新关系表
			//			WaInludeclassVO[] vos = queryIncludeClasses(oldvo.getPk_wa_class(),
			//					oldvo.getCyear(), oldvo.getCperiod());
			//			vos[0].setCyear(tempVO.getCyear());
			//			vos[0].setCperiod(tempVO.getCperiod());
			//			getWaClassDAO().getBaseDao().updateVO(vos[0]);

			if (WaLoginVOHelper.isLeaveClass(vo)) {
				WaClassVO oldLeaveChildVO = queryChildClass(
						oldvo.getPk_wa_class(), oldvo.getCyear(),
						oldvo.getCperiod(), 101);
				ChildClassParas tempvo = new ChildClassParas();
				tempvo.setBatch(101);
				WaClassVO newLeaveChildVO = copyParentVO(vo, tempvo);
				newLeaveChildVO
				.setPk_wa_class(oldLeaveChildVO.getPk_wa_class());
				newLeaveChildVO.setTs(oldLeaveChildVO.getTs());
				updateWaClassInfo(newLeaveChildVO, oldLeaveChildVO);
			}
			// 更新关系表
			WaInludeclassVO[] vos = getWaClassDAO().querySubClasses(oldvo.getPk_wa_class(),
					oldvo.getCyear(), oldvo.getCperiod(),true);
			for(int i=0;null!=vos&&i<vos.length;i++){
				vos[i].setCyear(tempVO.getCyear());
				vos[i].setCperiod(tempVO.getCperiod());
			}
			getWaClassDAO().getBaseDao().updateVOArray(vos);
		}

		// 插入计薪规则
		insertRangeRule(vo);
		// 插入计薪范围
		insertWaRange(vo);

		// 插入财务组织
		insertWaFiorg(vo);

		WaClassVO resultVO = getWaClassDAO().completeRange(tempVO);
		// 还原显示的PK_ORG
		resultVO.setPk_org(tempOrg);
		WaCacheUtils.synCache(WaClassVO.TABLE_NAME);
		return resultVO;
	}

	/**
	 * 更新薪资方案信息
	 */
	private WaClassVO updateWaClassInfo(WaClassVO vo, WaClassVO oldvo)
			throws BusinessException {
		// 保存薪资方案
		WaClassVO tempVO = getDocService().update(vo, true);
		if (vo.getTaxmode().intValue() != oldvo.getTaxmode().intValue()) {
			updateTaxmode(vo);

		}
		// 业务期间是起始期间，并且没有已经审核的数据，则允许修改期间状态
		// 其他情况下，都不允许修改期间状态
		if (needResetPeriodState(vo, oldvo)) {
			//期间方案改变，或者起始期间改变了
			// 则删除旧的期间方案，重新插入期间方案
			updatePeriodState(vo);
			// 则删除旧的薪资发放项目，重新插入薪资发放项目
//			deleteClassitem(vo);
//			insertWaClassitem(vo);
			//guoqt 如果修改薪资方案的期间，则需要将之前的薪资发放项目的期间同步修改
			if(oldvo.getCollectflag().booleanValue()&&oldvo.getCperiod()==null){
				// 如果是汇总方案第一次输入起始期间，则需要插入系统项目
				deleteClassitem(vo);
				insertWaClassitem(vo);	
			}else{
				//guoqt更新薪资发放项目的期间
				updateWaClassItem(vo);
				// 删除方案下人员，因为人员不一定归属于当前期间
				IPayfileManageService payfileService = NCLocator.getInstance().lookup(
						IPayfileManageService.class);
				payfileService.delPsnbyWaClass(vo.getPk_wa_class());
			}
			
		}else if(!vo.getIsapporve().equals(oldvo.getIsapporve())){
			updatePeriodState4IsApporve(vo);
			updateWaClass4IsApporve(vo);
		}
		return tempVO;
	}
	//guoqt更新薪资发放项目的期间
	public void updateWaClassItem(WaClassVO vo) throws BusinessException {
		String sql = "update wa_classitem set cyear =? , cperiod = ? "
			+ "where pk_wa_class = ?  ";
		SQLParameter param = new SQLParameter();
		param.addParam(vo.getCyear());
		param.addParam(vo.getCperiod());
		param.addParam(vo.getPk_wa_class());
		getWaClassDAO().getBaseDao().executeUpdate(sql, param);
	}
	
	/**
	 * 更新薪资发放项目中补发扣税的公式内容
	 * 
	 * @param vo
	 *            WaClassVO
	 * @throws BusinessException
	 */
	private void updateTaxmode(WaClassVO vo) throws BusinessException {
		String sql = "update wa_classitem set vformula =? , vformulastr = ? "
				+ "where pk_wa_class = ? and cyear = ? and cperiod = ? and itemkey = 'f_9' ";
		SQLParameter param = new SQLParameter();
		param.addParam(vo.getTaxmode().toString());
		param.addParam(getTaxmodeFormularStr(vo.getTaxmode()));
		param.addParam(vo.getPk_wa_class());
		param.addParam(vo.getCyear());
		param.addParam(vo.getCperiod());
		getWaClassDAO().getBaseDao().executeUpdate(sql, param);
	}

	private boolean needResetPeriodState(WaClassVO newVO, WaClassVO oldvo) {
		if (stringEquals(oldvo.getPk_periodscheme(), newVO.getPk_periodscheme())) {
			// 期间方案没有改变
			if (stringEquals(oldvo.getStartyearperiod(),
					newVO.getStartyearperiod())) {
				// 起始期间没有改变
				return false;
			} else {
				// 起始期间改变
				return true;
			}
		} else {
			// 期间方案改变
			return true;
		}
	}
	
	

	private boolean stringEquals(String str1, String str2) {
		if (str1 == null && str2 == null) {
			// 都为空
			return true;
		} else {
			if (str1 != null && str2 != null) {
				// 都不为空
				return str1.equals(str2);
			} else {
				// 仅有一个为空
				return false;
			}
		}
	}


	public boolean currentPeriodHasCheckedData(WaClassVO vo) {
		return currentPeriodHasCheckedData(vo.getPk_wa_class(), vo.getCyear(),
				vo.getCperiod());
	}

	public boolean currentPeriodHasCheckedData(String pk_waclass, String cyear,
			String cperiod) {

		try {
			// 业务期间是起始期间
			String sql = "select  1  from wa_data where " +
					"pk_wa_class = '"+ pk_waclass + "'  and cyear = '" + cyear + "'   and cperiod = '" + cperiod + "'   and checkflag = 'Y' " +
					"union select  1 from wa_data where  " +
					" pk_wa_class in (select pk_childclass from wa_inludeclass" +
					" where pk_parentclass = '"+ pk_waclass + "'  and cyear = '" + cyear + "' and cperiod = '" + cperiod + "')" +
					" and cyear = '" + cyear + "'   and cperiod = '" + cperiod + "'  and checkflag = 'Y'";
			//			String sql = "select 1  from wa_data where (pk_wa_class = '"+ pk_waclass + "' or pk_wa_class in(select pk_childclass from wa_inludeclass  where pk_parentclass = '"
			//					+ pk_waclass + "' and cyear = '" + cyear + "' and cperiod = '" + cperiod + "')) and cyear = '"
			//					+ cyear + "' and cperiod = '" + cperiod
			//					+ "' and checkflag = 'Y'";

			return getWaClassDAO().isValueExist(sql);

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return false;
		}

	}

	private void insertWaClassitem(WaClassVO vo) throws BusinessException {
		// 查询出集团的公共薪资项目
		WaItemVO[] vos = waitemServiceImpl.querySystemItems();// (vo.getPk_periodscheme(),vo.getCyear()+vo.getCperiod());
		insertWaClassitem(vo, vos);
	}


	private void insertWaTaxitem(WaClassVO vo) throws BusinessException {
		// 查询出集团的公共薪资项目
		TaxItemVO[] vos = new PsnTaxImpl().querySystemTaxItemVOs(vo.getPk_country());// (vo.getPk_periodscheme(),vo.getCyear()+vo.getCperiod());


		if (!ArrayUtils.isEmpty(vos)) {
			String pk_org = vo.getPk_org();
			String pk_group = vo.getPk_group();
			String pk_wa_class =vo.getPk_wa_class();
			IMDPersistenceService servicePersistence = MDPersistenceService.lookupPersistenceService();
			NCObject[] ncObjs = new NCObject[vos.length];
			for(int i=0;i<vos.length;i++){
				vos[i].setPk_org(pk_org);
				vos[i].setPk_group(pk_group);
				vos[i].setPk_wa_class(pk_wa_class);
				vos[i].setPk_wa_taxitem(null);
				vos[i].setStatus(VOStatus.NEW);
				ncObjs[i]=NCObject.newInstance(vos[i]);
			}
			servicePersistence.saveBill(ncObjs);
		}


	}


	private void insertWaClassitem(WaClassVO vo, WaClassVO copyedvo)
			throws BusinessException {
		// 查询出集团的公共薪资项目
		// 如果是集团分配方案复制时，copyedvo的pk_org显示的是集团，非真正的pk_org。真正的pk_org应该和vo的相等。所以此处用vo.getPk_org()
		WaClassItemVO[] vos = classitemQuery.queryAllClassItemInfos(vo.getPk_org(),
				copyedvo.getPk_wa_class(), copyedvo.getCyear(),
				copyedvo.getCperiod());

		for (int index = 0; index < vos.length; index++) {
			vos[index].setPk_wa_classitem(null);
			vos[index].setPk_group(vo.getPk_group());
			vos[index].setPk_org(vo.getPk_org());
			vos[index].setPk_wa_class(vo.getPk_wa_class());
			vos[index].setCyear(vo.getCyear());
			vos[index].setCperiod(vo.getCperiod());
			vos[index].setStatus(VOStatus.NEW);

		}

		getMDPersistenceService().saveBill(vos);
	}

// 	20151016 xiejie3 NCdp205509857  问题：用户没有复制的薪资方案的项目权限，begin
	private void insertWaClassItemPower( WaClassVO vo,WaClassVO copyed) throws BusinessException{
//		获取当前复制险种的薪资项目权限
//		 如果是集团分配方案复制时，copyedvo的pk_org显示的是集团，非真正的pk_org。真正的pk_org应该和vo的相等。所以此处用vo.getPk_org()
		ItemPowerVO[] itempowerVO=ItemPowerQuery.queryItemPowerByWaClass(copyed.getPk_wa_class(), vo.getPk_org(),vo.getCreator());
		if(null!=itempowerVO&&0!=itempowerVO.length){
			for(ItemPowerVO itempower :itempowerVO){
//			放入当前的方案主键
				itempower.setPk_wa_class(vo.getPk_wa_class());
				itempower.setPk_group(vo.getPk_group());
				itempower.setPk_org(vo.getPk_org());
			
			}
//			getMDPersistenceService().saveBill(itempowerVO);
//			ItemPowerService.insertItemPowerVOs(itempowerVO, itempowerVO);
			getWaClassDAO().getBaseDao().insertVOArray(itempowerVO);
		}
	}
//	end
	
	
	
	private void insertWaClassitem(WaClassVO vo, WaItemVO[] vos)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return;
		}
		ArrayList<WaClassItemVO> classItemVOs = new ArrayList<WaClassItemVO>();
		for (int index = 0; index < vos.length; index++) {
			WaItemVO waItemVO = vos[index];
			WaClassItemVO tempVO = new WaClassItemVO();
			// 采用默认值
			tempVO.merge(waItemVO);
			tempVO.setPk_group(vo.getPk_group());
			tempVO.setPk_org(vo.getPk_org());
			tempVO.setPk_wa_class(vo.getPk_wa_class());
			tempVO.setCyear(vo.getCyear());
			tempVO.setCperiod(vo.getCperiod());

			tempVO.setStatus(VOStatus.NEW);
			tempVO.setCode(vo.getCode() + waItemVO.getCode());

			tempVO.setIdisplayseq(index);
			tempVO.setIcomputeseq(0);
			tempVO.setIssysformula(UFBoolean.TRUE);
// {MOD:新个税补丁}
// begin
			if (waItemVO.getCode().equals("f_5")) {
				tempVO.setIssysformula(UFBoolean.FALSE);
			}
// end
			tempVO.setRound_type(RoundTypeEnum.ROUND.value());
			tempVO.setPk_wa_classitem(null);

			// 如果是f_9 补发扣税。 则需要根据 薪资方案的补发扣税方式确定公式类型
			if ("f_9".equals(tempVO.getItemkey())) {
				tempVO.setVformula(vo.getTaxmode().toString());
				tempVO.setVformulastr(getTaxmodeFormularStr(vo.getTaxmode()));
			}

			classItemVOs.add(tempVO);
		}
		// 保存期间状态
		WaClassItemVO[] vos2 = new WaClassItemVO[classItemVOs.size()];
		getMDPersistenceService().saveBill(classItemVOs.toArray(vos2));
		// 发放项目默认有权限
		if(vo.getShowflag().booleanValue())
			new ClassItemPowerServiceImpl().insertItemPower(classItemVOs.toArray(vos2));
	}

	private String getTaxmodeFormularStr(Integer taxmode) {

		return MDEnum.valueOf(nc.vo.wa.category.TaxMode.class, taxmode)
				.getName();

	}

	private void insertPeriodState(WaClassVO vo) throws BusinessException {
		// 查询出符合条件的薪资期间
		PeriodVO[] vos = waPeriodQuery.getPeriodsByChemeAndStartDate(
				vo.getPk_periodscheme(), vo.getCyear() + vo.getCperiod());
		if(WaLoginVOHelper.isSubClass(vo)){
			insertPeriodStateVOS(vo, new PeriodVO[]{vos[0]});
		}else{
			insertPeriodStateVOS(vo, vos);
		}
	}

	/**
	 * 插入制定年月对应的薪资期间状态
	 * 
	 * @param vo
	 * @param cyear
	 * @param cperiod
	 * @throws BusinessException
	 */
	private void insertPeriodState(WaClassVO vo, String cyear, String cperiod)
			throws BusinessException {
		// 查询出符合条件的薪资期间

		PeriodVO[] vos = waPeriodQuery.getPeriodsByChemeAndDate(
				vo.getPk_periodscheme(), cyear, cperiod);

		if(WaLoginVOHelper.isSubClass(vo)){
			insertPeriodStateVOS(vo, new PeriodVO[]{vos[0]});
		}else{
			insertPeriodStateVOS(vo, vos);
		}
	}

	private void insertPeriodStateVOS(WaClassVO vo, PeriodVO[] vos)
			throws MetaDataException {
		if (ArrayUtils.isEmpty(vos)) {
			return;
		}
		ArrayList<WaPeriodstateVO> statevos = new ArrayList<WaPeriodstateVO>();
		for (int index = 0; index < vos.length; index++) {
			PeriodVO periodVO = vos[index];
			WaPeriodstateVO tempVO = new WaPeriodstateVO();
			// 采用默认值
			// 起始期间应该是可用的，因为期间是按照
			if (vos[index].getCyear().equals(vo.getCyear())
					&& vos[index].getCperiod().equals(vo.getCperiod())) {
				tempVO.setEnableflag(UFBoolean.TRUE);
			} else {
				tempVO.setEnableflag(UFBoolean.FALSE);
			}

			tempVO.setIsapproved(UFBoolean.FALSE);
			// tempVO.setCheckflag(vo.getCheckflowflag());
			//guoqt 薪资方案增加发放数据需审批选项
			tempVO.setIsapporve(vo.getIsapporve());
			/**
			 * 是否已审核
			 */
			tempVO.setCheckflag(UFBoolean.FALSE);
			tempVO.setCaculateflag(UFBoolean.FALSE);
			tempVO.setPayoffflag(UFBoolean.FALSE);
			tempVO.setAccountmark(UFBoolean.FALSE);
			tempVO.setPk_group(vo.getPk_group());
			tempVO.setPk_org(vo.getPk_org());
			tempVO.setPk_wa_class(vo.getPk_wa_class());
			tempVO.setPk_wa_period(periodVO.getPk_wa_period());
			tempVO.setClasstype(WaLoginVOHelper.getClassType(vo).getValue());
			tempVO.setStatus(VOStatus.NEW);
			statevos.add(tempVO);
		}
		// 保存期间状态
		WaPeriodstateVO[] vos2 = new WaPeriodstateVO[statevos.size()];
		getMDPersistenceService().saveBill(statevos.toArray(vos2));
	}

	private void insertRangeRule(WaClassVO vo) throws BusinessException {

		// 删除已有的
		deleteRangeRule(vo);

		// 构建
		WaPsnhiVO[] vos = vo.getWaPsnhiVOs();
		if (ArrayUtils.isEmpty(vos)) {
			return;
		}
		String pkwaclass = vo.getPk_wa_class();

		for (int index = 0; index < vos.length; index++) {
			vos[index].setPk_wa_grd(pkwaclass);
			vos[index].setStatus(VOStatus.NEW);
		}
		if (!ArrayUtils.isEmpty(vos)) {
			getMDPersistenceService().saveBill(vos);
		}
	}

	private void insertWaFiorg(WaClassVO vo) throws BusinessException {

		// 删除已有的
		deleteFiOrgvo(vo);

		// 构建
		WaFiorgVO[] vos = vo.getWaclassFiorgvo();
		if (ArrayUtils.isEmpty(vos)) {
			return;
		}
		String pkwaclass = vo.getPk_wa_class();

		for (int index = 0; index < vos.length; index++) {
			vos[index].setPk_wa_class(pkwaclass);
			vos[index].setStatus(VOStatus.NEW);
		}
		if (!ArrayUtils.isEmpty(vos)) {
			getMDPersistenceService().saveBill(vos);
		}
	}

	private void insertWaRange(WaClassVO vo) throws BusinessException {

		// 删除已有的
		deleteWaRange(vo);
		// 构建
		WaPsnhiBVO[] vos = vo.getWaPsnhiBVOs();
		if (ArrayUtils.isEmpty(vos)) {
			return;
		}
		for (int i = 0; i < vos.length; i++) {
			/**
			 * 设置所属的薪资方案
			 */
			vos[i].setPk_wa_grdlv(vo.getPk_wa_class());
			vos[i].setStatus(VOStatus.NEW);
		}

		getWaClassDAO().getBaseDao().insertVOArray(vos);
	}

	private void updatePeriodState(WaClassVO vo) throws BusinessException {
		// 先删除原有的，再插入新的。因为类别主键在整个数据库中是唯一的。所以直接按照薪资方案主键进行删除
		try {
			// 先删除已有的发放数据。undo！！！！

			deletePeriodState(vo);
			insertPeriodState(vo);

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0079")/* @res "薪资方案的期间状态修改失败" */);
		}

	}
	
	private void updatePeriodState4IsApporve(WaClassVO vo) throws BusinessException {
		try {
			getWaClassDAO().updatePeriodState4IsApporve(vo);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0079")/* @res "薪资方案的期间状态修改失败" */);
		}

	}
	/**
	 * 修改（子方案）离职结薪的是否需要审批标志
	 * @param guoqt
	 * @param IsApporve
	 * @throws BusinessException
	 */
	private void updateWaClass4IsApporve(WaClassVO vo) throws BusinessException {
		try {
			getWaClassDAO().updateWaClass4IsApporve(vo);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0079")/* @res "薪资方案的期间状态修改失败" */);
		}

	}
	private void deletePeriodState(WaClassVO vo) throws BusinessException {
		// 先删除原有的，再插入新的。因为类别主键在整个数据库中是唯一的。所以直接按照薪资方案主键进行删除
		deletePeriodState(vo.getPk_wa_class());

	}
	
	private void deletePeriodState(WaClassVO[] vos) throws BusinessException {
		if(vos == null || vos.length<=0){
			return ; 
		}
		// 先删除原有的，再插入新的。因为类别主键在整个数据库中是唯一的。所以直接按照薪资方案主键进行删除
		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			StringBuffer inSql = new StringBuffer();
			for(WaClassVO vo : vos){
				inSql.append(',').append("'").append(vo.getPk_wa_class()).append("'");
			}
			session.executeUpdate(" delete from  wa_periodstate   where pk_wa_class in ("
					+ inSql.substring(1).toString() + ")");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0080")/* @res "薪资方案的期间状态删除失败" */);
		}

	}

	private void deletePeriodState(String pk_wa_class) throws BusinessException {
		// 先删除原有的，再插入新的。因为类别主键在整个数据库中是唯一的。所以直接按照薪资方案主键进行删除
		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate(" delete from  wa_periodstate   where pk_wa_class= '"
					+ pk_wa_class + "'");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0080")/* @res "薪资方案的期间状态删除失败" */);
		}

	}

	private void deleteRangeRule(WaClassVO vo) throws BusinessException {
		// 先删除原有的，再插入新的。因为类别主键在整个数据库中是唯一的。所以直接按照薪资方案主键进行删除
		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate(" delete from  wa_psnhi   where pk_wa_grd= '"
					+ vo.getPk_wa_class() + "'");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0081")/* @res "薪资方案的计薪规则删除失败" */);
		}

	}

	private void deleteFiOrgvo(WaClassVO vo) throws BusinessException {
		// 先删除原有的，再插入新的。因为类别主键在整个数据库中是唯一的。所以直接按照薪资方案主键进行删除
		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate(" delete from  wa_class_fiorg   where pk_wa_class= '"
					+ vo.getPk_wa_class() + "'");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0108")/* @res "薪资方案的财务组织删除失败" */);
		}

	}

	private void deleteWaRange(WaClassVO vo) throws BusinessException {
		// 先删除原有的，再插入新的。因为类别主键在整个数据库中是唯一的。所以直接按照薪资方案主键进行删除
		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate(" delete from  wa_psnhi_b   where pk_wa_grdlv= '"
					+ vo.getPk_wa_class() + "'");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0082")/* @res "薪资方案的计薪范围删除失败" */);
		}

	}

	private void deleteClassitem(WaClassVO vo) throws BusinessException {
		// 先删除原有的，再插入新的。因为类别主键在整个数据库中是唯一的。所以直接按照薪资方案主键进行删除

		waPeriodImpl.deleteClassitem(vo.getPk_wa_class());

	}
	
	private void deleteClassitem(WaClassVO[] vos) throws BusinessException {
		if(vos == null || vos.length<=0){
			return ; 
		}
		// 先删除原有的，再插入新的。因为类别主键在整个数据库中是唯一的。所以直接按照薪资方案主键进行删除
		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			StringBuffer inSql = new StringBuffer();
			for(WaClassVO vo : vos){
				inSql.append(',').append("'").append(vo.getPk_wa_class()).append("'");
			}
			session.executeUpdate(" delete from  wa_classitem   where pk_wa_class in ("
					+ inSql.substring(1).toString() + ")");
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(/*pk_wa_class+*/"=="+e.getMessage());
//			throw new BusinessException(ResHelper.getString("60130waclass",
//					"060130waclass0083")/* @res "薪资方案的发放项目删除失败" */);
		}

	}

	private void deleteUnitCtg(WaClassVO vo) throws BusinessException {
		// 先删除原有的，再插入新的。因为类别主键在整个数据库中是唯一的。所以直接按照薪资方案主键进行删除
		try {
			if (vo.getCollectflag().booleanValue()) {
				PersistenceManager sessionManager = PersistenceManager
						.getInstance();
				JdbcSession session = sessionManager.getJdbcSession();
				session.executeUpdate(" delete from  wa_unitctg   where pk_wa_class= '"
						+ vo.getPk_wa_class() + "'");
			}

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0084")/* @res "汇总薪资方案关系删除失败" */);
		}

	}

	private static IMDPersistenceService getMDPersistenceService() {
		return MDPersistenceService.lookupPersistenceService();
	}


	@Override
	public WaClassVO queryWaClassByPK(String pk) throws BusinessException {

		WaClassVO[] vos = new WaClassVO[0];
		String condition = " pk_wa_class = '" + pk + "'";
		try {
			vos = getWaClassDAO().queryWaClassByCondition(condition,
					UFBoolean.FALSE);
		} catch (DAOException de) {
			Logger.error(de.getMessage(), de);
			throw new BusinessException(de.getMessage());

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0069")/* @res "查询薪资方案失败!" */);
		}
		if (vos.length == 1) {
			return vos[0];
		} else {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0085")/* @res "薪资方案发生变化，请刷新后再试!" */);
		}

	}

	@Override
	public void deleteWaClass(WaClassVO vo) throws BusinessException {

		// 查询是否有已审核人员，有的话不允许删除
		if (currentPeriodHasCheckedData(vo)) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0073")/* @res "方案下有人员已审核，不能删除！" */);
		}
		BDPKLockUtil.lockString(vo.getPk_wa_class());
		// 删除多次发放子方案
		if (WaLoginVOHelper.isMultiClass(vo)) {
			String condition = " pk_wa_class in (select pk_childclass from wa_inludeclass where pk_parentclass = '"
					+ vo.getPk_wa_class() + "')";
			WaClassVO[] childVOs = getDocService().queryByCondition(
					WaClassVO.class, condition);
			if (childVOs != null && childVOs.length > 0) {
				deleteIncludeClass(vo.getPk_wa_class());
				for (WaClassVO childVO : childVOs) {
					deleteWaClass(childVO);
					// -->改为使用主子关系合并计税
					// deleteTaxGroup(childVO.getPk_wa_class());
				}
			}
		}

		deleteCore(vo.getPk_wa_class());

		// 如果是汇总薪资类别，删除汇总关系
		deleteUnitCtg(vo);

		// 删除其他相关子表
		// 删除财务组织
		deleteFiOrgvo(vo);

		// 删除计薪规则
		deleteRangeRule(vo);

		// 删除计薪范围
		deleteWaRange(vo);//

		// 删除项目权限
		new ClassItemPowerServiceImpl().deleteItemPower(vo.getPk_wa_class());

		// 删除薪资条根据方案
		deletePayslipByWaClass(vo);

		getDocService().delete(vo);

		// 如何防止重复同步？
		CacheProxy.fireDataDeleted(WaClassVO.TABLE_NAME, vo.getPk_wa_class());

	}


	/**
	 * 删除薪资条根据方案
	 * 
	 * @param vo
	 * @throws BusinessException
	 * @author xuhw
	 */
	private void deletePayslipByWaClass(WaClassVO vo) throws BusinessException {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("select * from wa_payslip where ");
		sbSql.append(PayslipVO.ACCYEAR).append(" = '").append(vo.getCyear())
		.append("' and ");
		sbSql.append(PayslipVO.ACCMONTH).append(" = '").append(vo.getCperiod())
		.append("' and ");
		sbSql.append(PayslipVO.PK_WA_CLASS).append(" = '")
		.append(vo.getPk_wa_class()).append("' ");
		PayslipVO[] payslipvos = new BaseDAOManager().executeQueryVOs(
				sbSql.toString(), PayslipVO.class);
		if (ArrayUtils.isEmpty(payslipvos)) {
			return;
		}

		String sqlIn = FormatVO.formatArrayToString(payslipvos,
				PayslipVO.PK_PAYSLIP, "'");

		getWaClassDAO().getBaseDao().deleteByClause(PayslipItemVO.class,
				PayslipItemVO.PK_PAYSLIP + " in (" + sqlIn + ")");

		getWaClassDAO().getBaseDao().deleteVOArray(payslipvos);
	}

	// 删除基本属性——薪资项目，期间状态 ，发薪人员
	private void deleteCore(String pk_wa_class) throws BusinessException {
		// 删除方案下人员
		IPayfileManageService payfileService = NCLocator.getInstance().lookup(
				IPayfileManageService.class);
		payfileService.delPsnbyWaClass(pk_wa_class);

		// 删除期间状态
		deletePeriodState(pk_wa_class);
		// 删除相关项目
// {MOD:新个税补丁}
// begin
//		waPeriodImpl.deleteClassitem(pk_wa_class);
		//20160113 shenliangc NCdp205572656  离职结薪节点删除所有人员数据后，发放项目汇总下还有被删除的离职结薪次数下的新增项目
		IClassItemQueryService classitemQueryService = NCLocator.getInstance().lookup(
				IClassItemQueryService.class);
		WaClassItemVO[] vos = classitemQueryService.queryAllClassItems(pk_wa_class);
		
		IClassItemManageService classitemManageService = NCLocator.getInstance().lookup(
				IClassItemManageService.class);
		classitemManageService.deleteWaClassItemVOs(vos);
// end
	}

	@Override
	public WaClassVO sealWaClass(WaClassVO vo)
			throws nc.vo.pub.BusinessException {
		// 加锁
		BDPKLockUtil.lockSuperVO(vo);
		// 版本校验（时间戳校验）。加锁+时间戳，保证版本一致性
		BDVersionValidationUtil.validateSuperVO(vo);
		// 设置封存标示
		vo = setseal(vo);

		vo = updateSealFlagAndState(vo);

		// 停用期间数据清0
		clearWaData(vo);

		//停用所有的子方案
		//		setSubClassSealFlag(vo,true);
		return getWaClassDAO().completeRange(vo);

	}

	/**
	 * 薪资类别停用时清空数据
	 * 
	 * @author liangxr on 2010-8-5
	 * @param vo
	 * @throws BusinessException
	 */
	private void clearWaData(WaClassVO vo) throws BusinessException {
		WaClassItemVO[] classitems = classitemQuery.queryAllClassItemInfos(
				vo.getPk_org(), vo.getPk_wa_class(), vo.getCyear(),
				vo.getCperiod());

		IPaydataManageService paydataquery = NCLocator.getInstance().lookup(
				IPaydataManageService.class);

		for (WaClassItemVO classitem : classitems) {
			paydataquery.clearClassItemData(classitem);
		}
		paydataquery.updatePaydataFlag(vo.getPk_wa_class(), vo.getCyear(),
				vo.getCperiod());
	}

	public WaClassVO setseal(WaClassVO vo) throws BusinessException {
		// 得到校验工厂 里的校验器，执行校验
		customValidatorForSeal(IValidatorFactory.SEAL, vo);
		// 保存薪资方案
		vo.setStopflag(UFBoolean.TRUE);
		return vo;
	}

	public WaClassVO setunSeal(WaClassVO vo, UFDate enableDate)
			throws BusinessException {
		// 得到校验工厂 里的校验器，执行校验
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("WaClassVO", vo);
		map.put("UFDate", enableDate);
		customValidatorForSeal(IValidatorFactory.UNSEAL, map);
		// 保存薪资方案
		vo.setStopflag(UFBoolean.FALSE);

		return vo;
	}

	private void customValidatorForSeal(String type, Object obj)
			throws ValidationException {
		if (getDocService().getValidatorFactory() != null) {
			Validator[] validators = getDocService().getValidatorFactory()
					.createValidator(type);
			if (validators != null && validators.length > 0) {
				DefaultValidationService vService = getDocService()
						.createValidationService(validators);
				vService.validate(obj);
			}
		}
	}

	/****************************************************************************
	 * 启用组织方案 Created on 2012-7-17 下午4:31:38<br>
	 * 
	 * @see nc.itf.hr.wa.IWaClass#unsealWaClass(nc.vo.wa.category.WaClassVO,
	 *      nc.vo.pub.lang.UFDate)
	 * @author daicy
	 ****************************************************************************/
	@Override
	public WaClassVO unsealWaClass(WaClassVO vo, UFDate enableDate)
			throws nc.vo.pub.BusinessException {

		// 加锁
		BDPKLockUtil.lockSuperVO(vo);
		// 版本校验（时间戳校验）。加锁+时间戳，保证版本一致性
		BDVersionValidationUtil.validateSuperVO(vo);
		// 设置封存状态（自定义校验器进行校验）

		vo = setunSeal(vo, enableDate);

		String lastYear = vo.getCyear();
		String lastPeriod = vo.getCperiod();
		// 保存。并为vo设置启用日期对应的cyear 与 cperiod
		vo = updateUnSealFlagAndState(vo, enableDate);


		if(!(lastYear.equals(vo.getCyear()) && lastPeriod.equals(vo
				.getCperiod()))){
			if(WaLoginVOHelper.isLeaveClass(vo)){
				//离职发薪方案变动
				unsealLeaveeClass(vo, lastYear, lastPeriod);
			}else{
				// 当前期间下是否有项目，如果没有则添加项目
				if (!waPeriodQuery.hasWaclassItem(vo)) {
					waPeriodImpl.copyWaClassitem(vo, lastYear, lastPeriod);
				}
				// 如果当下期间没有人员，则添加人员
				if (!WaLoginVOHelper.isGroupClass(vo)&&!getWaClassDAO().isHavePsn(vo)) {
					getWaClassDAO().updatePayfileForUnseal(vo);
				}
				//父方案且启用期间不是停用期间，修改第一次子方案信息
				if (WaLoginVOHelper.isParentClass(vo)) {
					unsealMutipleClass(vo, lastYear, lastPeriod);
				}
			}
			LoginContext context = new LoginContext();
			context.setPk_org(vo.getPk_org());
			context.setPk_group(vo.getPk_group());
			AggPayslipVO aggVO = NCLocator.getInstance().lookup(IPayslipService.class).queryAggPayslipVOByCon("accmonth = '"+lastPeriod+"' and accyear = '"+lastYear+"' and pk_wa_class = '"+vo.getPk_wa_class()+"'" , context);
			if (aggVO != null) {
				PayslipVO parentVO = (PayslipVO)aggVO.getParentVO();
				parentVO.setPk_payslip(null);
				parentVO.setAccmonth(vo.getCperiod());
				parentVO.setAccyear(vo.getCyear());
				parentVO.setStatus(VOStatus.NEW);
				PayslipItemVO[] childVOs = (PayslipItemVO[])aggVO.getTableVO(AggPayslipVO.ITEM);
				if (!ArrayUtils.isEmpty(childVOs)) {
					for (int i = 0; i < childVOs.length; i++) {
						childVOs[i].setPk_payslip(null);
						childVOs[i].setPk_payslip_item(null);
						childVOs[i].setStatus(VOStatus.NEW);
					}
				}
				NCLocator.getInstance().lookup(IPayslipService.class).insertAggPayslipVO(aggVO);
			}



			WaClassEndVO waClassEndVO = new WaClassEndVO();
			waClassEndVO.setPk_wa_class(vo.getPk_wa_class());
			waClassEndVO.setCyear(lastYear);
			waClassEndVO.setCperiod(lastPeriod);

			PeriodStateVO nextPeriodStateVO=  getPeriodVO(vo);

			//创建下一期间的数据接口
			NCLocator.getInstance().lookup(IMonthEndManageService.class).createNextDataIO(waClassEndVO, nextPeriodStateVO);

			//创建下一期间的薪资分摊方案数据
			NCLocator.getInstance().lookup(IMonthEndManageService.class).createNextPeriodAmoScheme(waClassEndVO, nextPeriodStateVO);
		}
		// 集团方案处理结束
		if (WaLoginVOHelper.isGroupClass(vo)) {
			return vo;
		}
		return getWaClassDAO().completeRange(vo);
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
		return getWaClassDAO().executeQueryVO(sqlBuffer.toString(), parameter, PeriodStateVO.class);
	}


	/**
	 * 处理离职发薪启用
	 * 
	 * @param vo
	 * @param lastYear
	 * @param lastPeriod
	 * @throws BusinessException
	 */
	private void unsealLeaveeClass(WaClassVO vo, String lastYear,
			String lastPeriod) throws BusinessException {
		//下个月变成正常发薪
		vo.setLeaveflag(UFBoolean.FALSE);
		vo.setMutipleflag(UFBoolean.FALSE);
		getWaClassDAO().getBaseDao().updateVO(vo, new String[]{WaClassVO.LEAVEFLAG,WaClassVO.MUTIPLEFLAG});


		WaClassVO childVO = queryChildClass(vo.getPk_wa_class(), lastYear,
				lastPeriod, 1);
		//用上一期间子方案的发放项目作为新期间的发放项目
		String condition = " pk_wa_class = '" + childVO.getPk_wa_class()
				+ "' and cyear = '" + lastYear + "' and cperiod = '" + lastPeriod
				+ "'";
		WaClassItemVO[] nextPeriodItemVOs = getWaClassDAO().retrieveByClause(
				WaClassItemVO.class, condition);
		// 对下一期的项目更新期间
		for (WaClassItemVO waClassItemVO : nextPeriodItemVOs) {
			waClassItemVO.setPk_wa_classitem(null);
			waClassItemVO.setPk_wa_class(vo.getPk_wa_class());
			waClassItemVO.setCyear(vo.getCyear());
			waClassItemVO.setCperiod(vo.getCperiod());
		}
		// 插入下一期
		getWaClassDAO().insertVOArrayReturnVOArray(nextPeriodItemVOs);

		DataVO dataVOCon = new DataVO();
		dataVOCon.setPk_wa_class(childVO.getPk_wa_class());
		dataVOCon.setCyear(lastYear);
		dataVOCon.setCperiod(lastPeriod);
		dataVOCon.setCyearperiod(lastYear + lastPeriod);
		DataVO[] dataVOs = new AppendBaseDAO().retrieveAppendableVOs(dataVOCon,
				DataVO.PK_WA_CLASS, DataVO.CYEAR, DataVO.CPERIOD);
		if (dataVOs == null) {
			return;
		}

		// 预形成主键， 为多个表引用
		String strOids[] = OidGenerator.getInstance().nextOid(
				OidGenerator.GROUP_PK_CORP, dataVOs.length);

		for (int i = 0; i < dataVOs.length; i++) {
			DataVO dataVO = dataVOs[i];
			dataVO.setPk_wa_data(strOids[i]);
			dataVO.setPk_wa_class(vo.getPk_wa_class());
			// 更新期间
			dataVO.setCyear(childVO.getCyear());
			dataVO.setCperiod(childVO.getCperiod());
			// 更新状态
			dataVO.setCheckflag(UFBoolean.FALSE);
			dataVO.setCaculateflag(UFBoolean.FALSE);
		}

		// 形成下个月的数据
		getWaClassDAO().getBaseDao().insertVOArrayWithPK(dataVOs);
	}

	/**
	 * 处理多次发薪启用
	 * 
	 * @param vo
	 * @param lastYear
	 * @param lastPeriod
	 * @throws BusinessException
	 */
	private void unsealMutipleClass(WaClassVO vo, String lastYear,
			String lastPeriod) throws BusinessException {
		// 如果是多次发薪，需要创建第一个子类别
		WaClassVO childVO = queryChildClass(vo.getPk_wa_class(), lastYear,
				lastPeriod, 1);
		childVO.setCyear(vo.getCyear());
		childVO.setCperiod(vo.getCperiod());
		getWaClassDAO().getBaseDao().updateVO(childVO);
		insertRelation(vo, childVO);
		// 插入初始化的薪资项目！
		insertWaClassitem(childVO, vo);

		DataVO dataVOCon = new DataVO();
		dataVOCon.setPk_wa_class(vo.getPk_wa_class());
		dataVOCon.setCyear(lastYear);
		dataVOCon.setCperiod(lastPeriod);
		dataVOCon.setCyearperiod(lastYear + lastPeriod);
		DataVO[] dataVOs = new AppendBaseDAO().retrieveAppendableVOs(dataVOCon,
				DataVO.PK_WA_CLASS, DataVO.CYEAR, DataVO.CPERIOD);

		if (dataVOs == null) {
			return;
		}

		// 预形成主键， 为多个表引用
		String strOids[] = OidGenerator.getInstance().nextOid(
				OidGenerator.GROUP_PK_CORP, dataVOs.length);

		for (int i = 0; i < dataVOs.length; i++) {
			DataVO dataVO = dataVOs[i];
			dataVO.setPk_wa_data(strOids[i]);
			dataVO.setPk_wa_class(childVO.getPk_wa_class());
			// 更新期间
			dataVO.setCyear(childVO.getCyear());
			dataVO.setCperiod(childVO.getCperiod());
			// 更新状态
			dataVO.setCheckflag(UFBoolean.FALSE);
			dataVO.setCaculateflag(UFBoolean.FALSE);
		}
		// 形成下个月的数据
		getWaClassDAO().getBaseDao().insertVOArrayWithPK(dataVOs);

	}

	public WaClassVO unsealAssignedWaClass(WaClassVO vo, UFDate enableDate,
			WaClassItemVO[] vos) throws nc.vo.pub.BusinessException {

		// 加锁
		BDPKLockUtil.lockSuperVO(vo);
		// 版本校验（时间戳校验）。加锁+时间戳，保证版本一致性
		BDVersionValidationUtil.validateSuperVO(vo);
		// 设置封存状态（自定义校验器进行校验）
		vo = setunSeal(vo, enableDate);

		// 保存
		vo = updateUnSealFlagAndState(vo, enableDate);

		// 当前期间下是否有项目，如果没有则添加父方案的所有项目
		if (!waPeriodQuery.hasWaclassItem(vo)) {
			insertWaClassitemForAssigned(vo, vos);
		}

		// 添加档案
		// 如果当下期间没有人员，则添加人员
		if (!getWaClassDAO().isHavePsn(vo)) {
			getWaClassDAO().updatePayfileForUnseal(vo);
		}

		return getWaClassDAO().completeRange(vo);
	}



	private WaClassVO updateSealFlagAndState(WaClassVO vo)
			throws BusinessException {

		// 前事件通知
		BusinessEvent beforeEvent = new BusinessEvent(
				WaBusinessDocName.WACLASS_DOC_NAME,
				IEventType.TYPE_UPDATE_BEFORE, vo);
		EventDispatcher.fireEvent(beforeEvent);

		// 设置审计信息
		vo.setStatus(VOStatus.UPDATED);
		SimpleDocServiceTemplate.setAuditInfoAndTs(vo, getDocService()
				.isProcessAuditInfo());

		// 将最新的业务期间封存
		sealPeriodState(vo);
		
		// 保存
		getMDPersistenceService().saveBillWithRealDelete(vo);
		

		// 后事件通知
		BusinessEvent afterEvent = new BusinessEvent(
				WaBusinessDocName.WACLASS_DOC_NAME,
				IEventType.TYPE_UPDATE_AFTER, vo.getPrimaryKey());
		EventDispatcher.fireEvent(afterEvent);
		
		fireCacheDataChanged(vo);
		
		return getDocService().updateTsAndKeys(vo);
	}

	/**************************************************************
     * 更新档案之后通知同步缓存 <br>
     * Created on 2013-4-1 15:25:42<br>
     * @param vo
     * @author Rocex Wang
     **************************************************************/
    public void fireCacheDataChanged(Object vo)
    {
        if (vo instanceof SuperVO)
        {
            CacheProxy.fireDataUpdated(((SuperVO) vo).getTableName());
        }
        else if (vo instanceof AggregatedValueObject)
        {
            AggregatedValueObject aggVO = (AggregatedValueObject) vo;
            
            CacheProxy.fireDataUpdated(((SuperVO) aggVO.getParentVO()).getTableName());
            
            CircularlyAccessibleValueObject[] childrenVO = aggVO.getChildrenVO();
            
            if (childrenVO != null && childrenVO.length > 0)
            {
                CacheProxy.fireDataUpdated(((SuperVO) childrenVO[0]).getTableName());
            }
        }
        else if (vo instanceof ExtendedAggregatedValueObject)
        {
            ExtendedAggregatedValueObject aggVO = (ExtendedAggregatedValueObject) vo;
            
            CacheProxy.fireDataUpdated(((SuperVO) aggVO.getParentVO()).getTableName());
            
            String strTableCodes[] = aggVO.getTableCodes();
            if (strTableCodes != null)
            {
                for (String strTableCode : strTableCodes)
                {
                    CircularlyAccessibleValueObject[] childrenVO = aggVO.getTableVO(strTableCode);
                    
                    if (childrenVO != null && childrenVO.length > 0)
                    {
                        CacheProxy.fireDataUpdated(((SuperVO) childrenVO[0]).getTableName());
                    }
                }
            }
        }
    }

	private void sealPeriodState(WaClassVO vo) throws BusinessException {

		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate("update  wa_periodstate  set enableflag = 'N' where pk_wa_class = '"
					+ vo.getPk_wa_class()
					+ "' and pk_wa_period = (select wa_period.pk_wa_period from wa_period where wa_period.pk_wa_period = wa_periodstate.pk_wa_period and wa_period.cyear = '"
					+ vo.getCyear()
					+ "' and  wa_period.cperiod = '"
					+ vo.getCperiod() + "')");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0079")/* @res "薪资方案的期间状态修改失败" */);
		}
	}

	private void unsealPeriodState(WaClassVO vo, UFDate enableDate)
			throws BusinessException {
		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate("update  wa_periodstate  set enableflag = 'Y' where pk_wa_class = '"
					+ vo.getPk_wa_class()
					+ "' and pk_wa_period = (select wa_period.pk_wa_period from wa_period where wa_period.pk_wa_period = wa_periodstate.pk_wa_period and wa_period.cstartdate  <='"
					+ enableDate.toStdString()
					+ "' and wa_period.cenddate >='"
					+ enableDate.toStdString() + "')");
			//缓存事件
			fireCacheDataChanged(vo);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0079")/* @res "薪资方案的期间状态修改失败" */);
		}

	}

	private WaClassVO updateUnSealFlagAndState(WaClassVO vo, UFDate enableDate)
			throws BusinessException {

		// 前事件通知
		BusinessEvent beforeEvent = new BusinessEvent(
				WaBusinessDocName.WACLASS_DOC_NAME,
				IEventType.TYPE_UPDATE_BEFORE, vo);
		EventDispatcher.fireEvent(beforeEvent);

		// 设置审计信息
		vo.setStatus(VOStatus.UPDATED);
		SimpleDocServiceTemplate.setAuditInfoAndTs(vo, getDocService()
				.isProcessAuditInfo());

		// 根据薪资类别，启封日期 查找薪资期间。

		PeriodVO pvo = waPeriodQuery.queryWaPeriod(vo.getPk_wa_class(), enableDate);
		if (pvo == null) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0088")/* @res "启用日期不正确，没有查找到对应的薪资期间" */);
		}
		// 判断启用期间是否大于等于停用期间
		boolean isenable = getWaClassDAO().isEnablePeriod(vo.getPk_wa_class(),
				pvo);
		if (!isenable) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0089")/* @res "启用期间必须大于等于停用期间！" */);
		}
		// 是否集团分配的方案，并且集团方案当前期间被停用

		// 设置最新的业务期间
		vo.setCyear(pvo.getCyear());
		vo.setCperiod(pvo.getCperiod());
		// 并且保存
		getMDPersistenceService().saveBillWithRealDelete(vo);

		// 将最新的业务期间启封
		unsealPeriodState(vo, enableDate);



		// 后事件通知
		BusinessEvent afterEvent = new BusinessEvent(
				WaBusinessDocName.WACLASS_DOC_NAME,
				IEventType.TYPE_UPDATE_AFTER, vo.getPrimaryKey());
		EventDispatcher.fireEvent(afterEvent);

		return getDocService().updateTsAndKeys(vo);
	}

	public void setSubClassSealFlag(WaClassVO parentvo,boolean isSeal) throws DAOException{
		PersistenceManager sessionManager = null;
		String stopflag = isSeal?"Y":"N";
		String enableflag = isSeal?"N":"Y";
		try {
			sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();

			StringBuilder sbd = new StringBuilder();
			sbd.append(" update wa_waclass set stopflag = '"+stopflag+"' where  pk_wa_class  in( ");
			sbd.append(" select pk_childclass  from wa_inludeclass where pk_parentclass = '"+parentvo.getPk_wa_class()+"' and cyear = '"+parentvo.getCyear()+"' and cperiod = '"+parentvo.getCperiod()+"' ");
			sbd.append(" ) ");
			session.addBatch(sbd.toString());
			sbd = new StringBuilder();

			sbd.append("  update wa_periodstate   set enableflag = '"+enableflag+"' where wa_periodstate.pk_wa_class  in(  ");
			sbd.append("  		 select pk_childclass  from wa_inludeclass where pk_parentclass ='"+parentvo.getPk_wa_class()+"'  and cyear = '"+parentvo.getCyear()+"' and cperiod = '"+parentvo.getCperiod()+"' ");
			sbd.append("  		 ) ");
			sbd.append("  		 and exists ( ");
			sbd.append("  		 select 1 from wa_period where  ");
			sbd.append("  		 wa_periodstate.pk_wa_period = wa_period.pk_wa_period and wa_period.cyear = '"+parentvo.getCyear()+"' and wa_period.cperiod ='"+parentvo.getCperiod()+"' ");
			sbd.append("  		 ) ");

			session.addBatch(sbd.toString());




			session.executeBatch();

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (sessionManager != null) {
				sessionManager.release();
			}
		}
	}

	@Override
	public AssignclsVO[] queryWaClsAssignedOrg(WaClassVO vo)
			throws BusinessException {
		return getWaClassDAO().queryWaClsAssignedOrg(vo);

	}

	/**
	 * 
	 * @param vo
	 * @return
	 * @throws nc.vo.pub.BusinessException
	 */
	@Override
	public boolean groupClsHasAssigned(WaClassVO vo)
			throws nc.vo.pub.BusinessException {

		AssignclsVO[] vos = getWaClassDAO().queryWaClsAssignedOrg(vo);
		if (ArrayUtils.isEmpty(vos)) {
			return false;
		}

		return true;
	}

	@Override
	public OrgVO[] queryWaClsUnassignedOrg(WaClassVO vo)
			throws BusinessException {
		//		IOrgUnitQryService service = NCLocator.getInstance().lookup(
		//				IOrgUnitQryService.class);
		//		OrgVO[] childrenOrgs = service
		//				.getAllOrgVOSByGroupIDAndOrgTypes(vo.getPk_group(),
		//						new String[] { IOrgConst.HRORGORGTYPE }, true);

		//fengwei 2012-07-31 查询HR组织时，要关联org_hrorg，将已经停用的HR组织过滤掉
		//20150716  shenliangc 处理下行流量超标问题，查询HR组织没必要所有字段都查出来。
		String sql = "select org_orgs.pk_org, org_orgs.innercode, org_orgs.code, org_orgs.name from org_orgs inner join org_hrorg on org_hrorg.pk_hrorg = org_orgs.pk_org where org_orgs.pk_group = '"
				+ vo.getPk_group()
				+ "' and org_orgs.orgtype4 = 'Y' and org_orgs.pk_group <> org_orgs.pk_org and org_hrorg.enablestate = 2 "
				+ " and org_orgs.enablestate = 2";

		OrgVO[] childrenOrgs = new BaseDAOManager().executeQueryVOs(sql, OrgVO.class);

		return getWaClassDAO().queryWaClsUnassignedOrg(vo, childrenOrgs);
	}

	/**
	 * 组织的薪资方案是否有已经审核的业务数据
	 */
	@Override
	public boolean WaClsHasBusinessData(WaClassVO vo)
			throws nc.vo.pub.BusinessException {

		return currentPeriodHasCheckedData(vo);

	}

	@Override
	public void assignWaClass(WaClassVO waClassvo, AssignclsVO[] vos)
			throws BusinessException {
		// 得到新增加的分配关系 与需要删除的分配关系
		ArrayList<AssignclsVO> assignVOs = new ArrayList<AssignclsVO>();
		ArrayList<AssignclsVO> deleteVOs = new ArrayList<AssignclsVO>();
		ArrayList<AssignclsVO> updateVOs = new ArrayList<AssignclsVO>();
		for (int index = 0; index < vos.length; index++) {
			if (vos[index].getStatus() == VOStatus.NEW) {
				assignVOs.add(vos[index]);
			} else if (vos[index].getStatus() == VOStatus.DELETED) {
				deleteVOs.add(vos[index]);
			} else if (vos[index].getStatus() == VOStatus.UPDATED) {
				updateVOs.add(vos[index]);
			}
		}

		// 处理删除的
		deleteAssignedWaclass(waClassvo, deleteVOs);

		// 处理修改起始期间的
		updateAssignedWaclass(waClassvo, updateVOs);

		// 处理新增的
		insertAssignedWaclass(waClassvo, assignVOs);

	}

	/**
	 * 分配薪资方案时，处理新增的薪资方案
	 * 
	 * @author xuanlt on 2009-12-22
	 * @param waClassvo
	 * @param assignVOs
	 * @throws BusinessException
	 */
	private void insertAssignedWaclass(WaClassVO waClassvo,
			ArrayList<AssignclsVO> assignVOs) throws BusinessException {

		if (assignVOs.isEmpty()) {
			return;
		}
		// 得到该类别最新版本的公共薪资项目
		// WaClassItemVO[] classitemVOs =
		// NCLocator.getInstance().lookup(IClassItemQueryService.class)
		// .queryMaxversionItems(waClassvo);

		for (int index = 0; index < assignVOs.size(); index++) {
			AssignclsVO assignclsVO = assignVOs.get(index);
			WaClassVO classvo = (WaClassVO) waClassvo.clone();
			classvo.setPk_wa_class(null);
			classvo.setStatus(VOStatus.NEW);
			classvo.setStartyear(assignclsVO.getStartyear());
			classvo.setStartperiod(assignclsVO.getStartperiod());
			classvo.setCyear(assignclsVO.getStartyear());
			classvo.setCperiod(assignclsVO.getStartperiod());
			// 如何生成code
			classvo.setCode(waClassvo.getCode() + assignclsVO.getCode());
			classvo.setPk_org(assignclsVO.getPk_org());
			WaClassVO newWaclassvo = getDocService().insert(classvo);

			// 可以改进效率吗
			WaClassItemVO[] classitemVOs = classitemQuery.queryVersionItems(waClassvo, assignclsVO.getStartyear(),
					assignclsVO.getStartperiod());

			// 为分配的子类别创建发放项目
			insertWaClassitemForAssigned(newWaclassvo, classitemVOs);

			// 为分配的子类别创建期间在状态
			insertPeriodState(newWaclassvo);

			// 创建第一个子方案
			//			createChildClass(newWaclassvo);
			// 插入分配关系
			assignclsVO.setPk_sourcecls(waClassvo.getPk_wa_class());
			assignclsVO.setClassid(newWaclassvo.getPk_wa_class());
			assignclsVO.setModuleflag(0);// 薪酬模块
			assignclsVO.setStatus(VOStatus.NEW);
			getMDPersistenceService().saveBill(assignclsVO);

		}
	}

	private void insertWaClassitemForAssigned(WaClassVO newWaclassvo,
			WaClassItemVO[] classitemVOs) throws BusinessException {
		try {
			if (ArrayUtils.isEmpty(classitemVOs)) {
				return;
			}
			for (int i = 0; i < classitemVOs.length; i++) {
				classitemVOs[i].setPk_group(newWaclassvo.getPk_group());
				classitemVOs[i].setPk_org(newWaclassvo.getPk_org());
				classitemVOs[i].setPk_wa_class(newWaclassvo.getPk_wa_class());
				classitemVOs[i].setPk_wa_classitem(null);
				classitemVOs[i].setCyear(newWaclassvo.getCyear());
				classitemVOs[i].setCperiod(newWaclassvo.getCperiod());
				classitemVOs[i].setStatus(VOStatus.NEW);
			}

			getMDPersistenceService().saveBill(classitemVOs);

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0090")/* @res "为子方案添加薪资发放项目失败" */);
		}
	}

	@Override
	public boolean parentClsAllowAddItem(String childrenClsPK)
			throws BusinessException {

		String sql = "select wa_waclass.addflag "
				+ "from wa_assigncls ,wa_waclass  where  wa_assigncls.pk_sourcecls=wa_waclass.pk_wa_class and   wa_assigncls.classid = '"
				+ childrenClsPK + "' ";
		Object o = getWaClassDAO().getBaseDao().executeQuery(sql,
				new ColumnProcessor());
		if (o == null) {
			return true;
		} else {
			return UFBoolean.valueOf(o.toString()).booleanValue();
		}
	}

	/**
	 * 分配薪资方案时，处理修改的的薪资方案
	 * 
	 * @author xuanlt on 2009-12-22
	 * @param waClassvo
	 * @param assignVOs
	 * @throws BusinessException
	 */
	private void deleteAssignedWaclass(WaClassVO waClassvo,
			ArrayList<AssignclsVO> assignVOs) throws BusinessException {

		WaClassVO[] classVOs = new WaClassVO[assignVOs.size()];
		// 得到该类别最新版本的公共薪资项目
		for (int index = 0; index < assignVOs.size(); index++) {
			AssignclsVO assignclsVO = assignVOs.get(index);

			WaClassVO classvo = new WaClassVO();
			classvo.setPk_wa_class(assignclsVO.getClassid());
			classvo.setStatus(VOStatus.DELETED);
			classvo.setCyear(assignclsVO.getStartyear());
			classvo.setCperiod(assignclsVO.getStartperiod());
			classvo.setStartyear(assignclsVO.getStartyear());
			classvo.setStartperiod(assignclsVO.getStartperiod());
			// 删除多次发放子方案
			if (WaLoginVOHelper.isMultiClass(classvo)) {
				String condition = " pk_wa_class in (select pk_childclass from wa_inludeclass where pk_parentclass = '"
						+ classvo.getPk_wa_class() + "')";
				WaClassVO[] childVOs = getDocService().queryByCondition(
						WaClassVO.class, condition);
				if (childVOs != null && childVOs.length > 0) {
					deleteIncludeClass(classvo.getPk_wa_class());
					for (WaClassVO childVO : childVOs) {
						deleteWaClass(childVO);
						// -->改为使用主子关系合并计税
						// deleteTaxGroup(childVO.getPk_wa_class());
					}
				}
			}
			classVOs[index] = classvo; //by wangqim
			// 删除期间状态
//			deletePeriodState(classvo);
			// 删除相关项目
//			deleteClassitem(classvo);

			//zhoumxc  20140829      缓存测试：薪资方案表
//			CacheProxy.fireDataDeleted("wa_waclass", classvo.getPk_wa_class());
//			getMDPersistenceService().deleteBillFromDB(classvo);
		}
		deletePeriodState(classVOs);
		deleteClassitem(classVOs);
		if(classVOs != null && classVOs.length>0){
			String[] pks = new String[classVOs.length];
			for(int i=0;i<classVOs.length;i++){
				pks[i] = classVOs[i].getPk_wa_class();
			}
			CacheProxy.fireDataDeletedBatch("wa_waclass", pks);
			getMDPersistenceService().deleteBillFromDB(classVOs);
		}
		// 删除所有的分配关系
		if (assignVOs.size() >= 0) {
			InSQLCreator insc = new InSQLCreator();
			try {

				String in = insc.getInSQL(
						assignVOs.toArray(new AssignclsVO[0]),
						AssignclsVO.PK_ASSIGNCLS);
				String sql = " delete  from  wa_assigncls where pk_assigncls in("
						+ in + ")";
				getWaClassDAO().getBaseDao().executeUpdate(sql);
			} finally {
				insc.clear();
			}
		}

	}



	/**
	 * 分配薪资方案时，处理修改起始期间的
	 * 
	 * @author xuanlt on 2009-12-22
	 * @param waClassvo
	 * @param assignVOs
	 * @throws BusinessException
	 */
	private void updateAssignedWaclass(WaClassVO waClassvo,
			ArrayList<AssignclsVO> assignVOs) throws BusinessException {
		deleteAssignedWaclass(waClassvo, assignVOs);
		insertAssignedWaclass(waClassvo, assignVOs);
	}

	/**
	 * @author xuanlt on 2010-1-14
	 * @see nc.itf.hr.wa.IWaClass#getOrgCurrTypePK(nc.vo.uif2.LoginContext)
	 */
	@Override
	public String getOrgCurrTypePK(LoginContext context)
			throws BusinessException {
		String sql = "select pk_currtype from org_orgs where pk_group = '"
				+ context.getPk_group() + "' and pk_org = '"
				+ context.getPk_org() + "'";
		Object obj = getWaClassDAO().getBaseDao().executeQuery(sql,
				new ColumnProcessor());
		return (obj == null) ? "" : obj.toString();
	}

	/**
	 * @author xuanlt on 2010-1-26
	 * @see nc.itf.hr.wa.IWaClass#sealGroupWaClass(nc.vo.wa.category.WaClassVO)
	 */
	@Override
	public WaClassVO sealGroupWaClass(WaClassVO vo) throws BusinessException {
		// 首先判断薪资方案是否可以封存
		WaClassDAO dao = new WaClassDAO();
		if (!dao.groupClassSealable(vo)) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0091")/* @res "分配的方案中存已经审核的数据，不允许停用" */);
		}
		// 封存所有的子类别
		// 说明： 子类别有可能与本类别不是同一个期间
		dao.sealAssignedWaClass(vo);

		// 封存 本薪资方案
		return sealWaClass(vo);

	}

	/**
	 * @author xuanlt on 2010-1-26
	 * @see nc.itf.hr.wa.IWaClass#unsealGroupWaClass(nc.vo.wa.category.WaClassVO)
	 */
	@Override
	public WaClassVO unsealGroupWaClass(WaClassVO vo, UFDate enableDate)
			throws BusinessException {
		// 如果财务模块启用
		// 查看所有的子类别是否允许启封
		// 子类别最新的 业务期间对应的会计期间是否已经结账？ 结账择不允许启封 。没有结账，允许启封

		// 首先查看输入的日期是否合法（找到对应的薪资期间）
		PeriodVO pvo = waPeriodQuery.queryWaPeriod(vo.getPk_wa_class(), enableDate);

		if (pvo == null) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0088")/* @res "启用日期不正确，没有查找到对应的薪资期间" */);
		}

		// 判断输入日期是否 >= 所有子类别的封存期间！
		// 如果存在不满足的子薪资方案 。给与提示！
		StringBuilder sbd = new StringBuilder();
		sbd.append("select  "
				+ SQLHelper.getMultiLangNameColumn("wa_waclass.name")
				+ " ,wa_waclass.code, wa_waclass.pk_wa_class ,wa_waclass.cyear,wa_waclass.cperiod from wa_waclass ,wa_assigncls where wa_waclass.pk_wa_class = wa_assigncls.classid ");
		sbd.append(" and wa_assigncls.pk_sourcecls =? ");
		sbd.append(" and  (wa_waclass.cyear||wa_waclass.cperiod)>( ");
		sbd.append(" 	      select wa_period.cyear||cperiod from wa_period,wa_periodstate where wa_period.pk_wa_period = wa_periodstate.pk_wa_period and  pk_wa_class = ? and  wa_period.cstartdate <= ? and wa_period.cenddate >= ? ");
		sbd.append(" )");
		SQLParameter para = new SQLParameter();
		para.addParam(vo.getPk_wa_class());
		para.addParam(vo.getPk_wa_class());
		para.addParam(enableDate.toString().substring(0, 10));
		para.addParam(enableDate.toString().substring(0, 10));

		WaClassVO[] vos = getWaClassDAO().executeQueryVOs(sbd.toString(), para,
				WaClassVO.class);
		if (!ArrayUtils.isEmpty(vos)) {
			String names = FormatVO.formatArrayToString(vos, WaClassVO.NAME);

			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0092")/* @res "薪资方案:" */
					+ names
					+ ResHelper.getString("60130waclass", "060130waclass0093")/*
					 * @res
					 * "停用期间大于启用日期"
					 */);
		}

		/**
		 * 启封集团薪资方案
		 */
		vo = unsealWaClass(vo, enableDate);
		WaClassItemVO[] classitemVOs = classitemQuery.queryMaxversionItems(vo);

		// 启封分配的子薪资方案 ，需要传入启用日期
		unSealAssignedWaClass(vo, enableDate, classitemVOs);

		return vo;

	}

	private void unSealAssignedWaClass(WaClassVO vo, UFDate enableDate,
			WaClassItemVO[] currClassItems) throws BusinessException {
		try {
			// 检索子薪资方案
			WaClassVO[] subVos = getWaClassDAO().queryGroupAssignedWaclass(vo);
			// 逐个启封.薪资项目如何定？？
			if (!ArrayUtils.isEmpty(subVos)) {
				for (WaClassVO waClassVO : subVos) {
					// unsealAssignedWaClass(waClassVO, enableDate,
					// currClassItems);
					unsealWaClass(waClassVO, enableDate);
				}
			}

			fireCacheDataChanged(vo);
		} catch (DAOException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0094")/* @res "启用子薪资方案失败" */);
		}

	}

	/**
	 * 查询可以同步的薪资方案
	 * 
	 * @author liangxr on 2010-4-22
	 * @see nc.itf.hr.wa.IWaClass#queryAllClassidForRollIn(nc.vo.wa.pub.WaLoginContext,
	 *      nc.vo.pub.lang.UFDate, java.lang.String)
	 */

	@Override
	public WaClassVO[] queryAllClassidForRollIn(WaLoginContext context)
			throws BusinessException {
		return getWaClassDAO().queryAllClassidForRollIn(context);

	}

	@Override
	public String queryWaRangeRule(String pk_waclass) throws BusinessException {
		try {
			return getWaClassDAO().queryWaRangeRule(pk_waclass);
		} catch (DAOException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0095")/* @res "查询计薪范围失败！" */);
		}

	}

	@Override
	public boolean WaClsHasBusinessData(String pk_waclass, String cyear,
			String cperiod) throws BusinessException {
		return currentPeriodHasCheckedData(pk_waclass, cyear, cperiod);
	}

	/**
	 * 根据父方案期间查询所有子方案
	 * 
	 * @param pk_waclass
	 * @param cyear
	 * @param cperiod
	 * @return
	 * @throws DAOException
	 */
	@Override
	public WaInludeclassVO[] queryIncludeClasses(String pk_waclass,
			String cyear, String cperiod) throws BusinessException {
		return getWaClassDAO().querySubClasses(pk_waclass, cyear, cperiod,false);
	}

	@Override
	public WaClassVO queryChildClass(String pk_waclass, String cyear,
			String cperiod, int times) throws BusinessException {
		String condition = " pk_wa_class = (select pk_childclass from wa_inludeclass where pk_parentclass = '"
				+ pk_waclass
				+ "' and batch = "
				+ times
				+ " and cyear = '"
				+ cyear + "' and cperiod = '" + cperiod + "') ";

		WaClassVO[] childvos = getWaClassDAO().queryWaClassByCondition(
				condition, UFBoolean.FALSE);

		if (childvos.length == 0) {
			return null;
		}

		return childvos[0];
	}

	@Override
	public WaClassVO queryParentClass(String pk_waclass, String cyear,
			String cperiod) throws BusinessException {
		return getWaClassDAO().queryParentClass(pk_waclass, cyear, cperiod);
	}

	@Override
	public WaInludeclassVO[] queryAllPayOffChildClasses(String pk_waclass,
			String cyear, String cperiod) throws BusinessException {

		return getWaClassDAO().queryAllPayOffChildClasses(pk_waclass, cyear,
				cperiod);

	}

	@Override
	public WaInludeclassVO[] queryAllCheckedChildClasses(String pk_waclass,
			String cyear, String cperiod) throws BusinessException {

		return getWaClassDAO().queryAllCheckedChildClasses(pk_waclass, cyear,
				cperiod);

	}

	public WaInludeclassVO queryInludeclassvo(String pk_childclass,
			String cyear, String cperiod) throws BusinessException {
		return getWaClassDAO().queryInludeclassvo( pk_childclass,
				cyear,  cperiod);
	}

	@Override
	public WaInludeclassVO[] queryNewPeriodIncludeClasses(String pk_waclass)
			throws BusinessException {
		return getWaClassDAO().queryNewPeriodSubClasses(pk_waclass);
	}


	@Override
	public WaClassVO queryGroupClassByOrgClass(String pk_waclass)
			throws BusinessException {

		return getWaClassDAO().queryGroupClassByOrgClass(pk_waclass);

	}


	private void updatePsnTaxdata(WaClassVO parentVO, WaClassVO childVO) throws BusinessException{
		getWaClassDAO().updatePsnTaxdata(parentVO, childVO);
	}

	private void updateWaDatas(WaClassVO parentVO, WaClassVO childVO) throws BusinessException{
		getWaClassDAO().updateWaDatas(parentVO, childVO);
	}

	public WaClassVO queryPayrollClassbyPK(String pk) throws BusinessException {
		return getWaClassDAO().queryPayrollClassbyPK(pk);
	}

	// 20150806 xiejie3 补丁合并，NCdp205398612薪资方案权限分配时还能选到已离职并且停用的用户begin
	@Override
	public UserVO[] queryUserVObyOrg(String pkorg) throws BusinessException {
		return getWaClassDAO().queryUserVO(pkorg);
	}
	//end
	
}