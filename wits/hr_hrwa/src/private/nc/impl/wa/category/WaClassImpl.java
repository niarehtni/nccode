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
 * @date: 2009-11-19 ����11:24:10
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
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
//	20151016 xiejie3 NCdp205509857  ���⣺�û�û�и��Ƶ�н�ʷ�������ĿȨ��
//	ItemPowerQuery������ѯн�ʷ�����ĿȨ�ޡ�
	ClassItemPowerQueryServiceImpl ItemPowerQuery=new ClassItemPowerQueryServiceImpl();
//	end
	// ��δ���У�鹤������Ҫ��һ������
	private final IValidatorFactory docValidatorFactory = new WaClassValidatorFactory();
	private final SimpleDocServiceTemplate docService = new SimpleDocServiceTemplate(
			WaBusinessDocName.WACLASS_DOC_NAME);
	private final WaClassDAO dao = new WaClassDAO();

	public WaClassDAO getWaClassDAO() {
		return dao;
	}

	public SimpleDocServiceTemplate getDocService() {
		/**
		 * �趨�� �趨У�鹤��
		 */
		docService.setLocker(new WaClassDocLocker());
		docService.setValidatorFactory(docValidatorFactory);
		docService.setLazyLoad(true);
		return docService;

	}

	/**
	 * ����ָ����pk_child_class ��ѯ��Ӧ�ĸ�����pk ���û�У�˵��pk_child_class������Ǹ���������������
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
					"060130waclass0068")/* @res "��ѯ����н�ʷ����Ļ���н�ʷ���ʧ��!" */);
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
					"060130waclass0069")/* @res "��ѯн�ʷ���ʧ��!" */);
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
					"060130waclass0069")/* @res "��ѯн�ʷ���ʧ��!" */);
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
					"060130waclass0069")/* @res "��ѯн�ʷ���ʧ��!" */);
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
					"060130waclass0069")/* @res "��ѯн�ʷ���ʧ��!" */);
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
					"060130waclass0070")/* @res "��ѯ��н����ʧ��!" */);
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
					"060130waclass0069")/* @res "��ѯн�ʷ���ʧ��!" */);
		}
		return vos;
	}

	@Override
	public WaClassVO[] queryGroupAssignedWaclass(WaClassVO vo)
			throws BusinessException {
		return getWaClassDAO().queryGroupAssignedWaclass(vo);
	}

	/**
	 * ��α�֤����һ���� ����ʲôʱ�� commmit? ������� (1)�������� (2)����˰�� (3)У�����ڼ䣺 (4)У�������ظ���
	 * �ı�У�鷽ʽ�� (5)����н�ʷ��� (6)�����ʼ����н����Ŀ�� (7)�����������ĿȨ�� (8)���䲿��Ȩ��
	 */
	@Override
	public WaClassVO insertWaClass(WaClassVO vo) throws BusinessException {
		// BDPKLockUtil.lockString(vo.getPk_wa_class());
		// ����н�ʷ���
		WaClassVO tempVO = getDocService().insert(vo);

		// �����н����
		insertRangeRule(vo);

		// //�����н��Χ
		insertWaRange(vo);

		// ���������֯
		insertWaFiorg(vo);

		// �����ڼ�״̬
		insertPeriodState(tempVO);
		// �����ʼ����н����Ŀ��
		insertWaClassitem(vo);

		//
		// �����������ĿȨ��

		// ����Ƕ�η�н����Ҫ������һ�������
		// if(vo.getMutipleflag().booleanValue()){
		// �Ǽ��ŷ����򴴽���һ���ӷ���
		if (vo.getMutipleflag().equals(UFBoolean.TRUE)) {
			WaClassVO childVO = createChildClass(vo);
			// �����ϲ���˰��-->��Ϊʹ�����ӹ�ϵ�ϲ���˰
			// createTaxGroup(tempVO,childVO);
		}

		
		// н�ʷ���Ĭ����Ȩ
		insertWaClassPower(vo);
		

		//�����������˰�걨��Ŀ
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
		// ����н�ʷ���
		WaClassVO tempVO = getDocService().insert(vo);

		// �����ڼ�״̬
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
		// ���� ��Ϊ�����ӷ���������ӷ�������Ҫͬ���ġ�
		BDPKLockUtil.lockSuperVO(parentVO);
		// �汾У�飨ʱ���У�飩������+ʱ�������֤�汾һ����
		BDVersionValidationUtil.validateSuperVO(parentVO);

		if(WaLoginVOHelper.isNormalClass(parentVO)){
			//������н������Ҫת���ɶ�η��ŷ���
			changeNormal2Leaveclass(parentVO);
		}
		// ����н�����
		WaClassVO childVO = copyParentVO(parentVO, paras);

		// �����ӷ���
		childVO = insertChildClass(childVO, paras);

		// ��Ӹ��ӹ�ϵ
		insertRelation(parentVO, childVO, paras);

		// ���뷢����Ŀ�뷢�ŵ���
		insertChildItemAndPsn(parentVO, childVO, paras);
		// 20150728 xiejie3,�����ϲ���NCdp205367162  ���Ÿ���عɼ������޹�˾  ��η������б��̵ڶ��α����޷��ɹ�����
		//20150512 shenliangc �޸ĸ���������ֱ�������Ƶ�״̬
		updateFipendflag(childVO);

		// �ϲ���˰��������³�Ա-->��Ϊʹ�����ӹ�ϵ�ϲ���˰
		// insertMemberVO2Group(parentVO, childVO);

		// ���¸������ķ���״̬Ϊ-δ����
		getWaClassDAO().updateParentClassPayOff(parentVO, "N");
		if(parentVO.getMutipleflag().equals(UFBoolean.FALSE)){
			//������н������Ҫת���ɶ�η��ŷ���
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

		// ����н�����
		WaClassVO childVO = copyParentVO(parentVO);

		// �����ӷ���
		childVO = insertChildClass(childVO, null);

		// ��Ӹ��ӹ�ϵ
		WaInludeclassVO invo = insertRelation(parentVO, childVO);

		// ���뷢����Ŀ�뷢�ŵ���
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
		// ����н�����
		WaClassVO childVO = copyParentVO(vo);

		// �����ӷ���
		childVO = insertFirstChildClass4Leave(vo,childVO);

		// ��Ӹ��ӹ�ϵ
		insertRelation(vo, childVO);

		// ���뷢����Ŀ�뷢�ŵ���
		insertFirstChildItemAndPsn4Leave(vo, childVO);
		//20150728 xiejie3 �����ϲ���NCdp205367162  ���Ÿ���عɼ������޹�˾  ��η������б��̵ڶ��α����޷��ɹ�����begin
		//20150512 shenliangc �޸ĸ���������ֱ�������Ƶ�״̬
		updateFipendflag(vo);
        //end
		// {MOD:�¸�˰����}
		// begin
		// 2016-1-11 NCdp205570938 zhousze ����н�ʷ���δ���Ӵ���ʱ���Ѿ�����ֱ���������ݵ������嵥��Ȼ�������ӷ��Ŵ���ʱ��������wa_cashcard�д�ŵ�pk_wa_class�Ƕ�Ӧ��
		// ���ܷ�����pk_wa_class��������ȥȡ����һ�εĴ���ʱ�޷�ȡ��������Ĵ����Ǹ���wa_cashcard�е�pk_wa_classΪ�ӷ�����pk������Ӧ����
		// �ķ���
		updateWaCashcardPkwaclass(vo, childVO);
		// end
		//���¸������ڼ�״̬
		updatePeriodState4Leave(vo);

		//�����������뵥��Ӧ����
		updatePayroll4Leave(vo, childVO);

		//���ڲμӺϲ���˰�ķ����� wa_data��������������prewadata����Ϊ�����ӷ���.   pk_wa_class,pk_psndoc, cyear,cperiod
		updataPrewadata(vo,childVO);

		//���¸�������˰����
		updatePsnTaxdata(vo,childVO);

		//���¸������
		updateWaDatas(vo,childVO);
		
		//20150728 xiejie3 �����ϲ���NCdp205367162  ���Ÿ���عɼ������޹�˾  ��η������б��̵ڶ��α����޷��ɹ�����begin
		//20150512 shenliangc �޸ĸ���������ֱ�������Ƶ�״̬
			}
			
			private void updateFipendflag(WaClassVO vo)throws DAOException{
				getWaClassDAO().updateFipendflag(vo);
		//end
	}
	
// {MOD:�¸�˰����}
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
		// �õ� ��һ�� ��н��classvo
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
				//���²������ݵ��ӷ�����
			}

		}

		if (para != null && para.getSynClassItem().booleanValue()) {
			// ��Ҫͬ����Ŀ
			NCLocator.getInstance().lookup(IClassItemManageService.class)
			.copyClassItems(srcvo, childVO);

		} else {
			// �����ʼ����н����Ŀ��
			insertWaClassitem(childVO);
		}

		if (para != null && para.getSynPsnData().booleanValue()) {
			// ͬ������
			// ����waloginvo
			WaLoginVO fromWaClass = new WaLoginVO();
			fromWaClass.setPk_wa_class(srcvo.getPk_wa_class());
			fromWaClass.setCyear(srcvo.getCyear());
			fromWaClass.setCperiod(srcvo.getCperiod());
			fromWaClass.setPk_org(srcvo.getPk_org());

			/**
			 * Ŀ����Ҫ periodvo
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

		// ȷ�� ��������ȷ�ġ�
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
					"060130waclass0072")/* @res "ɾ�����ӷ�����ϵ��ʧ��" */);
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
					"060130waclass0072")/* @res "ɾ�����ӷ�����ϵ��ʧ��" */);
		}
	}

	@Override
	public void deleteWaClassTimes(WaClassVO vo,boolean isfirst)
			throws nc.vo.pub.BusinessException {
		// ����
		BDPKLockUtil.lockSuperVO(vo);
		// �汾У�飨ʱ���У�飩������+ʱ�������֤�汾һ����
		BDVersionValidationUtil.validateSuperVO(vo);
		// У���Ƿ���ˣ��Ƿ����һ������
		if(!isfirst){
			validateDelWaClassTimes(vo);
		}

		// ɾ�� ��н������Ӧ�������
		WaClassVO childvo = getWaClassDAO().queryWaClassByParentClass(
				vo.getPk_wa_class());

		if (getWaClassDAO().isHaveCheckPsn(childvo)) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0073")/* @res "����������Ա����ˣ�����ɾ����" */);
		}

		IPayfileManageService payfileService = NCLocator.getInstance().lookup(
				IPayfileManageService.class);
		childvo.setPk_prnt_class(vo.getPk_wa_class());
		payfileService.deleteFromTotal(childvo);

		deleteCore(childvo.getPk_wa_class());

		// ɾ����ϵ��
		deleteRelation(childvo.getPk_wa_class());

		// ɾ���ϲ���˰���Ա-->��Ϊʹ�����ӹ�ϵ�ϲ���˰
		// deleteGroupMember(childvo.getPk_wa_class());

		// ɾ�����Ŵ�����Ӧ��н�����
		deleteTimesClass(childvo.getPk_wa_class());

		// ��������ӷ����������꣬���޸ĸ�����״̬
		if (getWaClassDAO().isChildPayoff(vo))
			getWaClassDAO().updateParentClassPayOff(vo, "Y");

		CacheProxy.fireDataDeleted(WaClassVO.TABLE_NAME,
				childvo.getPk_wa_class());

	}

	/**
	 * У���Ƿ�ֻʣһ�����Ŵ���
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
					"060130waclass0074")/* @res "��н�ʷ����Ѿ�û�з��Ŵ�������ɾ����" */);
		} else if (vos.length == 1) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0075")/* @res "����ɾ����һ�η�н" */);
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
					"060130waclass0077")/* @res "ɾ�����Ŵ�����Ӧ��н�����ʧ��" */);
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
		// ����н�ʷ���
		WaClassVO tempVO = getDocService().insert(vo);

		// �����н����
		insertRangeRule(vo);

		// //�����н��Χ
		insertWaRange(vo);
		
		// ���������֯
        insertWaFiorg(vo);

		// �����ڼ�״̬
		insertPeriodState(tempVO);
		// �����ʼ����н����Ŀ��
		insertWaClassitem(vo, copyed);

		// ����Ƕ�η�н����Ҫ������һ�������
		if(vo.getMutipleflag().equals(UFBoolean.TRUE)){
			copyChildClass(vo);
		}
		// �����ϲ���˰��-->��Ϊʹ�����ӹ�ϵ�ϲ���˰
		// createTaxGroup(tempVO,childVO);


		// н�ʷ���Ĭ����Ȩ
		insertWaClassPower(vo);
		
		// 	20151016 xiejie3 NCdp205509857  ���⣺�û�û�и��Ƶ�н�ʷ�������ĿȨ�ޣ�begin
		insertWaClassItemPower(vo,copyed);
		//end
		//
		// �����������ĿȨ��
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
	 * �����ӷ���
	 * 
	 * @param parentVO
	 *            WaClassVO
	 * @return WaClassVO
	 * @throws BusinessException
	 */
	private WaClassVO copyChildClass(WaClassVO parentVO)
			throws BusinessException {

		// ����н�����
		WaClassVO childVO = copyParentVO(parentVO);

		// �����ӷ���
		childVO = insertChildClass(childVO, null);

		// ��Ӹ��ӹ�ϵ
		insertRelation(parentVO, childVO);

		// �����ʼ����н����Ŀ��
		insertWaClassitem(childVO, parentVO);

		return childVO;
	}

	/**
	 * ����н�ʷ��� (2)�˲�����Ƿ��Ѿ���� (3)�������� ����������ǰн�ʷ��� (4)�μ�dmo
	 * 
	 * @author xuanlt on 2009-11-27
	 * @see nc.itf.hr.wa.IWaClass#insertWaClass(nc.vo.wa.category.WaClassVO)
	 */
	@Override
	public WaClassVO updateWaClass(WaClassVO vo) throws BusinessException {
		// �˲�����Ƿ��Ѿ���ˣ��Ѿ�����������޸ģ�
		if (currentPeriodHasCheckedData(vo)) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0078")/* @res "��ǰн�ʷ��������Ѿ���˵����ݣ��������޸�" */);

		}
		BDPKLockUtil.lockString(vo.getPk_wa_class());
		// �õ��ɵ�н�ʷ���
		WaClassVO oldvo = queryWaClassByPK(vo.getPk_wa_class());
		// ���ŷ����н�ʷ�����Ϊ��Ҫ��ʾ������֯Ϊ���ţ����Դ˴���ԭPK_ORGΪ��֯PK
		String tempOrg = vo.getPk_org();
		vo.setPk_org(oldvo.getPk_org());
		// ����н�ʷ���
		WaClassVO tempVO = updateWaClassInfo(vo, oldvo);

		// �Ǽ��ŷ��������ӷ���
		if (WaLoginVOHelper.isMultiClass(vo)) {
			// ����Ƕ�η��ŷ�����������ӷ�������Ϊ����������ݾͲ��ܸ��£������ܹ����µķ����϶�ֻ��һ���ӷ�����
			// ����oldvo��ѯ��ԭ�����ӷ���
			WaClassVO oldChildVO = queryChildClass(oldvo.getPk_wa_class(),
					oldvo.getCyear(), oldvo.getCperiod(), 1);
			WaClassVO newChildVO = copyParentVO(vo);
			newChildVO.setPk_wa_class(oldChildVO.getPk_wa_class());
			newChildVO.setTs(oldChildVO.getTs());
			updateWaClassInfo(newChildVO, oldChildVO);
			//			// ���¹�ϵ��
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
			// ���¹�ϵ��
			WaInludeclassVO[] vos = getWaClassDAO().querySubClasses(oldvo.getPk_wa_class(),
					oldvo.getCyear(), oldvo.getCperiod(),true);
			for(int i=0;null!=vos&&i<vos.length;i++){
				vos[i].setCyear(tempVO.getCyear());
				vos[i].setCperiod(tempVO.getCperiod());
			}
			getWaClassDAO().getBaseDao().updateVOArray(vos);
		}

		// �����н����
		insertRangeRule(vo);
		// �����н��Χ
		insertWaRange(vo);

		// ���������֯
		insertWaFiorg(vo);

		WaClassVO resultVO = getWaClassDAO().completeRange(tempVO);
		// ��ԭ��ʾ��PK_ORG
		resultVO.setPk_org(tempOrg);
		WaCacheUtils.synCache(WaClassVO.TABLE_NAME);
		return resultVO;
	}

	/**
	 * ����н�ʷ�����Ϣ
	 */
	private WaClassVO updateWaClassInfo(WaClassVO vo, WaClassVO oldvo)
			throws BusinessException {
		// ����н�ʷ���
		WaClassVO tempVO = getDocService().update(vo, true);
		if (vo.getTaxmode().intValue() != oldvo.getTaxmode().intValue()) {
			updateTaxmode(vo);

		}
		// ҵ���ڼ�����ʼ�ڼ䣬����û���Ѿ���˵����ݣ��������޸��ڼ�״̬
		// ��������£����������޸��ڼ�״̬
		if (needResetPeriodState(vo, oldvo)) {
			//�ڼ䷽���ı䣬������ʼ�ڼ�ı���
			// ��ɾ���ɵ��ڼ䷽�������²����ڼ䷽��
			updatePeriodState(vo);
			// ��ɾ���ɵ�н�ʷ�����Ŀ�����²���н�ʷ�����Ŀ
//			deleteClassitem(vo);
//			insertWaClassitem(vo);
			//guoqt ����޸�н�ʷ������ڼ䣬����Ҫ��֮ǰ��н�ʷ�����Ŀ���ڼ�ͬ���޸�
			if(oldvo.getCollectflag().booleanValue()&&oldvo.getCperiod()==null){
				// ����ǻ��ܷ�����һ��������ʼ�ڼ䣬����Ҫ����ϵͳ��Ŀ
				deleteClassitem(vo);
				insertWaClassitem(vo);	
			}else{
				//guoqt����н�ʷ�����Ŀ���ڼ�
				updateWaClassItem(vo);
				// ɾ����������Ա����Ϊ��Ա��һ�������ڵ�ǰ�ڼ�
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
	//guoqt����н�ʷ�����Ŀ���ڼ�
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
	 * ����н�ʷ�����Ŀ�в�����˰�Ĺ�ʽ����
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
			// �ڼ䷽��û�иı�
			if (stringEquals(oldvo.getStartyearperiod(),
					newVO.getStartyearperiod())) {
				// ��ʼ�ڼ�û�иı�
				return false;
			} else {
				// ��ʼ�ڼ�ı�
				return true;
			}
		} else {
			// �ڼ䷽���ı�
			return true;
		}
	}
	
	

	private boolean stringEquals(String str1, String str2) {
		if (str1 == null && str2 == null) {
			// ��Ϊ��
			return true;
		} else {
			if (str1 != null && str2 != null) {
				// ����Ϊ��
				return str1.equals(str2);
			} else {
				// ����һ��Ϊ��
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
			// ҵ���ڼ�����ʼ�ڼ�
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
		// ��ѯ�����ŵĹ���н����Ŀ
		WaItemVO[] vos = waitemServiceImpl.querySystemItems();// (vo.getPk_periodscheme(),vo.getCyear()+vo.getCperiod());
		insertWaClassitem(vo, vos);
	}


	private void insertWaTaxitem(WaClassVO vo) throws BusinessException {
		// ��ѯ�����ŵĹ���н����Ŀ
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
		// ��ѯ�����ŵĹ���н����Ŀ
		// ����Ǽ��ŷ��䷽������ʱ��copyedvo��pk_org��ʾ���Ǽ��ţ���������pk_org��������pk_orgӦ�ú�vo����ȡ����Դ˴���vo.getPk_org()
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

// 	20151016 xiejie3 NCdp205509857  ���⣺�û�û�и��Ƶ�н�ʷ�������ĿȨ�ޣ�begin
	private void insertWaClassItemPower( WaClassVO vo,WaClassVO copyed) throws BusinessException{
//		��ȡ��ǰ�������ֵ�н����ĿȨ��
//		 ����Ǽ��ŷ��䷽������ʱ��copyedvo��pk_org��ʾ���Ǽ��ţ���������pk_org��������pk_orgӦ�ú�vo����ȡ����Դ˴���vo.getPk_org()
		ItemPowerVO[] itempowerVO=ItemPowerQuery.queryItemPowerByWaClass(copyed.getPk_wa_class(), vo.getPk_org(),vo.getCreator());
		if(null!=itempowerVO&&0!=itempowerVO.length){
			for(ItemPowerVO itempower :itempowerVO){
//			���뵱ǰ�ķ�������
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
			// ����Ĭ��ֵ
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
// {MOD:�¸�˰����}
// begin
			if (waItemVO.getCode().equals("f_5")) {
				tempVO.setIssysformula(UFBoolean.FALSE);
			}
// end
			tempVO.setRound_type(RoundTypeEnum.ROUND.value());
			tempVO.setPk_wa_classitem(null);

			// �����f_9 ������˰�� ����Ҫ���� н�ʷ����Ĳ�����˰��ʽȷ����ʽ����
			if ("f_9".equals(tempVO.getItemkey())) {
				tempVO.setVformula(vo.getTaxmode().toString());
				tempVO.setVformulastr(getTaxmodeFormularStr(vo.getTaxmode()));
			}

			classItemVOs.add(tempVO);
		}
		// �����ڼ�״̬
		WaClassItemVO[] vos2 = new WaClassItemVO[classItemVOs.size()];
		getMDPersistenceService().saveBill(classItemVOs.toArray(vos2));
		// ������ĿĬ����Ȩ��
		if(vo.getShowflag().booleanValue())
			new ClassItemPowerServiceImpl().insertItemPower(classItemVOs.toArray(vos2));
	}

	private String getTaxmodeFormularStr(Integer taxmode) {

		return MDEnum.valueOf(nc.vo.wa.category.TaxMode.class, taxmode)
				.getName();

	}

	private void insertPeriodState(WaClassVO vo) throws BusinessException {
		// ��ѯ������������н���ڼ�
		PeriodVO[] vos = waPeriodQuery.getPeriodsByChemeAndStartDate(
				vo.getPk_periodscheme(), vo.getCyear() + vo.getCperiod());
		if(WaLoginVOHelper.isSubClass(vo)){
			insertPeriodStateVOS(vo, new PeriodVO[]{vos[0]});
		}else{
			insertPeriodStateVOS(vo, vos);
		}
	}

	/**
	 * �����ƶ����¶�Ӧ��н���ڼ�״̬
	 * 
	 * @param vo
	 * @param cyear
	 * @param cperiod
	 * @throws BusinessException
	 */
	private void insertPeriodState(WaClassVO vo, String cyear, String cperiod)
			throws BusinessException {
		// ��ѯ������������н���ڼ�

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
			// ����Ĭ��ֵ
			// ��ʼ�ڼ�Ӧ���ǿ��õģ���Ϊ�ڼ��ǰ���
			if (vos[index].getCyear().equals(vo.getCyear())
					&& vos[index].getCperiod().equals(vo.getCperiod())) {
				tempVO.setEnableflag(UFBoolean.TRUE);
			} else {
				tempVO.setEnableflag(UFBoolean.FALSE);
			}

			tempVO.setIsapproved(UFBoolean.FALSE);
			// tempVO.setCheckflag(vo.getCheckflowflag());
			//guoqt н�ʷ������ӷ�������������ѡ��
			tempVO.setIsapporve(vo.getIsapporve());
			/**
			 * �Ƿ������
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
		// �����ڼ�״̬
		WaPeriodstateVO[] vos2 = new WaPeriodstateVO[statevos.size()];
		getMDPersistenceService().saveBill(statevos.toArray(vos2));
	}

	private void insertRangeRule(WaClassVO vo) throws BusinessException {

		// ɾ�����е�
		deleteRangeRule(vo);

		// ����
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

		// ɾ�����е�
		deleteFiOrgvo(vo);

		// ����
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

		// ɾ�����е�
		deleteWaRange(vo);
		// ����
		WaPsnhiBVO[] vos = vo.getWaPsnhiBVOs();
		if (ArrayUtils.isEmpty(vos)) {
			return;
		}
		for (int i = 0; i < vos.length; i++) {
			/**
			 * ����������н�ʷ���
			 */
			vos[i].setPk_wa_grdlv(vo.getPk_wa_class());
			vos[i].setStatus(VOStatus.NEW);
		}

		getWaClassDAO().getBaseDao().insertVOArray(vos);
	}

	private void updatePeriodState(WaClassVO vo) throws BusinessException {
		// ��ɾ��ԭ�еģ��ٲ����µġ���Ϊ����������������ݿ�����Ψһ�ġ�����ֱ�Ӱ���н�ʷ�����������ɾ��
		try {
			// ��ɾ�����еķ������ݡ�undo��������

			deletePeriodState(vo);
			insertPeriodState(vo);

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0079")/* @res "н�ʷ������ڼ�״̬�޸�ʧ��" */);
		}

	}
	
	private void updatePeriodState4IsApporve(WaClassVO vo) throws BusinessException {
		try {
			getWaClassDAO().updatePeriodState4IsApporve(vo);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0079")/* @res "н�ʷ������ڼ�״̬�޸�ʧ��" */);
		}

	}
	/**
	 * �޸ģ��ӷ�������ְ��н���Ƿ���Ҫ������־
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
					"060130waclass0079")/* @res "н�ʷ������ڼ�״̬�޸�ʧ��" */);
		}

	}
	private void deletePeriodState(WaClassVO vo) throws BusinessException {
		// ��ɾ��ԭ�еģ��ٲ����µġ���Ϊ����������������ݿ�����Ψһ�ġ�����ֱ�Ӱ���н�ʷ�����������ɾ��
		deletePeriodState(vo.getPk_wa_class());

	}
	
	private void deletePeriodState(WaClassVO[] vos) throws BusinessException {
		if(vos == null || vos.length<=0){
			return ; 
		}
		// ��ɾ��ԭ�еģ��ٲ����µġ���Ϊ����������������ݿ�����Ψһ�ġ�����ֱ�Ӱ���н�ʷ�����������ɾ��
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
					"060130waclass0080")/* @res "н�ʷ������ڼ�״̬ɾ��ʧ��" */);
		}

	}

	private void deletePeriodState(String pk_wa_class) throws BusinessException {
		// ��ɾ��ԭ�еģ��ٲ����µġ���Ϊ����������������ݿ�����Ψһ�ġ�����ֱ�Ӱ���н�ʷ�����������ɾ��
		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate(" delete from  wa_periodstate   where pk_wa_class= '"
					+ pk_wa_class + "'");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0080")/* @res "н�ʷ������ڼ�״̬ɾ��ʧ��" */);
		}

	}

	private void deleteRangeRule(WaClassVO vo) throws BusinessException {
		// ��ɾ��ԭ�еģ��ٲ����µġ���Ϊ����������������ݿ�����Ψһ�ġ�����ֱ�Ӱ���н�ʷ�����������ɾ��
		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate(" delete from  wa_psnhi   where pk_wa_grd= '"
					+ vo.getPk_wa_class() + "'");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0081")/* @res "н�ʷ����ļ�н����ɾ��ʧ��" */);
		}

	}

	private void deleteFiOrgvo(WaClassVO vo) throws BusinessException {
		// ��ɾ��ԭ�еģ��ٲ����µġ���Ϊ����������������ݿ�����Ψһ�ġ�����ֱ�Ӱ���н�ʷ�����������ɾ��
		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate(" delete from  wa_class_fiorg   where pk_wa_class= '"
					+ vo.getPk_wa_class() + "'");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0108")/* @res "н�ʷ����Ĳ�����֯ɾ��ʧ��" */);
		}

	}

	private void deleteWaRange(WaClassVO vo) throws BusinessException {
		// ��ɾ��ԭ�еģ��ٲ����µġ���Ϊ����������������ݿ�����Ψһ�ġ�����ֱ�Ӱ���н�ʷ�����������ɾ��
		try {
			PersistenceManager sessionManager = PersistenceManager
					.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate(" delete from  wa_psnhi_b   where pk_wa_grdlv= '"
					+ vo.getPk_wa_class() + "'");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0082")/* @res "н�ʷ����ļ�н��Χɾ��ʧ��" */);
		}

	}

	private void deleteClassitem(WaClassVO vo) throws BusinessException {
		// ��ɾ��ԭ�еģ��ٲ����µġ���Ϊ����������������ݿ�����Ψһ�ġ�����ֱ�Ӱ���н�ʷ�����������ɾ��

		waPeriodImpl.deleteClassitem(vo.getPk_wa_class());

	}
	
	private void deleteClassitem(WaClassVO[] vos) throws BusinessException {
		if(vos == null || vos.length<=0){
			return ; 
		}
		// ��ɾ��ԭ�еģ��ٲ����µġ���Ϊ����������������ݿ�����Ψһ�ġ�����ֱ�Ӱ���н�ʷ�����������ɾ��
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
//					"060130waclass0083")/* @res "н�ʷ����ķ�����Ŀɾ��ʧ��" */);
		}

	}

	private void deleteUnitCtg(WaClassVO vo) throws BusinessException {
		// ��ɾ��ԭ�еģ��ٲ����µġ���Ϊ����������������ݿ�����Ψһ�ġ�����ֱ�Ӱ���н�ʷ�����������ɾ��
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
					"060130waclass0084")/* @res "����н�ʷ�����ϵɾ��ʧ��" */);
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
					"060130waclass0069")/* @res "��ѯн�ʷ���ʧ��!" */);
		}
		if (vos.length == 1) {
			return vos[0];
		} else {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0085")/* @res "н�ʷ��������仯����ˢ�º�����!" */);
		}

	}

	@Override
	public void deleteWaClass(WaClassVO vo) throws BusinessException {

		// ��ѯ�Ƿ����������Ա���еĻ�������ɾ��
		if (currentPeriodHasCheckedData(vo)) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0073")/* @res "����������Ա����ˣ�����ɾ����" */);
		}
		BDPKLockUtil.lockString(vo.getPk_wa_class());
		// ɾ����η����ӷ���
		if (WaLoginVOHelper.isMultiClass(vo)) {
			String condition = " pk_wa_class in (select pk_childclass from wa_inludeclass where pk_parentclass = '"
					+ vo.getPk_wa_class() + "')";
			WaClassVO[] childVOs = getDocService().queryByCondition(
					WaClassVO.class, condition);
			if (childVOs != null && childVOs.length > 0) {
				deleteIncludeClass(vo.getPk_wa_class());
				for (WaClassVO childVO : childVOs) {
					deleteWaClass(childVO);
					// -->��Ϊʹ�����ӹ�ϵ�ϲ���˰
					// deleteTaxGroup(childVO.getPk_wa_class());
				}
			}
		}

		deleteCore(vo.getPk_wa_class());

		// ����ǻ���н�����ɾ�����ܹ�ϵ
		deleteUnitCtg(vo);

		// ɾ����������ӱ�
		// ɾ��������֯
		deleteFiOrgvo(vo);

		// ɾ����н����
		deleteRangeRule(vo);

		// ɾ����н��Χ
		deleteWaRange(vo);//

		// ɾ����ĿȨ��
		new ClassItemPowerServiceImpl().deleteItemPower(vo.getPk_wa_class());

		// ɾ��н�������ݷ���
		deletePayslipByWaClass(vo);

		getDocService().delete(vo);

		// ��η�ֹ�ظ�ͬ����
		CacheProxy.fireDataDeleted(WaClassVO.TABLE_NAME, vo.getPk_wa_class());

	}


	/**
	 * ɾ��н�������ݷ���
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

	// ɾ���������ԡ���н����Ŀ���ڼ�״̬ ����н��Ա
	private void deleteCore(String pk_wa_class) throws BusinessException {
		// ɾ����������Ա
		IPayfileManageService payfileService = NCLocator.getInstance().lookup(
				IPayfileManageService.class);
		payfileService.delPsnbyWaClass(pk_wa_class);

		// ɾ���ڼ�״̬
		deletePeriodState(pk_wa_class);
		// ɾ�������Ŀ
// {MOD:�¸�˰����}
// begin
//		waPeriodImpl.deleteClassitem(pk_wa_class);
		//20160113 shenliangc NCdp205572656  ��ְ��н�ڵ�ɾ��������Ա���ݺ󣬷�����Ŀ�����»��б�ɾ������ְ��н�����µ�������Ŀ
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
		// ����
		BDPKLockUtil.lockSuperVO(vo);
		// �汾У�飨ʱ���У�飩������+ʱ�������֤�汾һ����
		BDVersionValidationUtil.validateSuperVO(vo);
		// ���÷���ʾ
		vo = setseal(vo);

		vo = updateSealFlagAndState(vo);

		// ͣ���ڼ�������0
		clearWaData(vo);

		//ͣ�����е��ӷ���
		//		setSubClassSealFlag(vo,true);
		return getWaClassDAO().completeRange(vo);

	}

	/**
	 * н�����ͣ��ʱ�������
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
		// �õ�У�鹤�� ���У������ִ��У��
		customValidatorForSeal(IValidatorFactory.SEAL, vo);
		// ����н�ʷ���
		vo.setStopflag(UFBoolean.TRUE);
		return vo;
	}

	public WaClassVO setunSeal(WaClassVO vo, UFDate enableDate)
			throws BusinessException {
		// �õ�У�鹤�� ���У������ִ��У��
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("WaClassVO", vo);
		map.put("UFDate", enableDate);
		customValidatorForSeal(IValidatorFactory.UNSEAL, map);
		// ����н�ʷ���
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
	 * ������֯���� Created on 2012-7-17 ����4:31:38<br>
	 * 
	 * @see nc.itf.hr.wa.IWaClass#unsealWaClass(nc.vo.wa.category.WaClassVO,
	 *      nc.vo.pub.lang.UFDate)
	 * @author daicy
	 ****************************************************************************/
	@Override
	public WaClassVO unsealWaClass(WaClassVO vo, UFDate enableDate)
			throws nc.vo.pub.BusinessException {

		// ����
		BDPKLockUtil.lockSuperVO(vo);
		// �汾У�飨ʱ���У�飩������+ʱ�������֤�汾һ����
		BDVersionValidationUtil.validateSuperVO(vo);
		// ���÷��״̬���Զ���У��������У�飩

		vo = setunSeal(vo, enableDate);

		String lastYear = vo.getCyear();
		String lastPeriod = vo.getCperiod();
		// ���档��Ϊvo�����������ڶ�Ӧ��cyear �� cperiod
		vo = updateUnSealFlagAndState(vo, enableDate);


		if(!(lastYear.equals(vo.getCyear()) && lastPeriod.equals(vo
				.getCperiod()))){
			if(WaLoginVOHelper.isLeaveClass(vo)){
				//��ְ��н�����䶯
				unsealLeaveeClass(vo, lastYear, lastPeriod);
			}else{
				// ��ǰ�ڼ����Ƿ�����Ŀ�����û���������Ŀ
				if (!waPeriodQuery.hasWaclassItem(vo)) {
					waPeriodImpl.copyWaClassitem(vo, lastYear, lastPeriod);
				}
				// ��������ڼ�û����Ա���������Ա
				if (!WaLoginVOHelper.isGroupClass(vo)&&!getWaClassDAO().isHavePsn(vo)) {
					getWaClassDAO().updatePayfileForUnseal(vo);
				}
				//�������������ڼ䲻��ͣ���ڼ䣬�޸ĵ�һ���ӷ�����Ϣ
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

			//������һ�ڼ�����ݽӿ�
			NCLocator.getInstance().lookup(IMonthEndManageService.class).createNextDataIO(waClassEndVO, nextPeriodStateVO);

			//������һ�ڼ��н�ʷ�̯��������
			NCLocator.getInstance().lookup(IMonthEndManageService.class).createNextPeriodAmoScheme(waClassEndVO, nextPeriodStateVO);
		}
		// ���ŷ����������
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
	 * ������ְ��н����
	 * 
	 * @param vo
	 * @param lastYear
	 * @param lastPeriod
	 * @throws BusinessException
	 */
	private void unsealLeaveeClass(WaClassVO vo, String lastYear,
			String lastPeriod) throws BusinessException {
		//�¸��±��������н
		vo.setLeaveflag(UFBoolean.FALSE);
		vo.setMutipleflag(UFBoolean.FALSE);
		getWaClassDAO().getBaseDao().updateVO(vo, new String[]{WaClassVO.LEAVEFLAG,WaClassVO.MUTIPLEFLAG});


		WaClassVO childVO = queryChildClass(vo.getPk_wa_class(), lastYear,
				lastPeriod, 1);
		//����һ�ڼ��ӷ����ķ�����Ŀ��Ϊ���ڼ�ķ�����Ŀ
		String condition = " pk_wa_class = '" + childVO.getPk_wa_class()
				+ "' and cyear = '" + lastYear + "' and cperiod = '" + lastPeriod
				+ "'";
		WaClassItemVO[] nextPeriodItemVOs = getWaClassDAO().retrieveByClause(
				WaClassItemVO.class, condition);
		// ����һ�ڵ���Ŀ�����ڼ�
		for (WaClassItemVO waClassItemVO : nextPeriodItemVOs) {
			waClassItemVO.setPk_wa_classitem(null);
			waClassItemVO.setPk_wa_class(vo.getPk_wa_class());
			waClassItemVO.setCyear(vo.getCyear());
			waClassItemVO.setCperiod(vo.getCperiod());
		}
		// ������һ��
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

		// Ԥ�γ������� Ϊ���������
		String strOids[] = OidGenerator.getInstance().nextOid(
				OidGenerator.GROUP_PK_CORP, dataVOs.length);

		for (int i = 0; i < dataVOs.length; i++) {
			DataVO dataVO = dataVOs[i];
			dataVO.setPk_wa_data(strOids[i]);
			dataVO.setPk_wa_class(vo.getPk_wa_class());
			// �����ڼ�
			dataVO.setCyear(childVO.getCyear());
			dataVO.setCperiod(childVO.getCperiod());
			// ����״̬
			dataVO.setCheckflag(UFBoolean.FALSE);
			dataVO.setCaculateflag(UFBoolean.FALSE);
		}

		// �γ��¸��µ�����
		getWaClassDAO().getBaseDao().insertVOArrayWithPK(dataVOs);
	}

	/**
	 * �����η�н����
	 * 
	 * @param vo
	 * @param lastYear
	 * @param lastPeriod
	 * @throws BusinessException
	 */
	private void unsealMutipleClass(WaClassVO vo, String lastYear,
			String lastPeriod) throws BusinessException {
		// ����Ƕ�η�н����Ҫ������һ�������
		WaClassVO childVO = queryChildClass(vo.getPk_wa_class(), lastYear,
				lastPeriod, 1);
		childVO.setCyear(vo.getCyear());
		childVO.setCperiod(vo.getCperiod());
		getWaClassDAO().getBaseDao().updateVO(childVO);
		insertRelation(vo, childVO);
		// �����ʼ����н����Ŀ��
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

		// Ԥ�γ������� Ϊ���������
		String strOids[] = OidGenerator.getInstance().nextOid(
				OidGenerator.GROUP_PK_CORP, dataVOs.length);

		for (int i = 0; i < dataVOs.length; i++) {
			DataVO dataVO = dataVOs[i];
			dataVO.setPk_wa_data(strOids[i]);
			dataVO.setPk_wa_class(childVO.getPk_wa_class());
			// �����ڼ�
			dataVO.setCyear(childVO.getCyear());
			dataVO.setCperiod(childVO.getCperiod());
			// ����״̬
			dataVO.setCheckflag(UFBoolean.FALSE);
			dataVO.setCaculateflag(UFBoolean.FALSE);
		}
		// �γ��¸��µ�����
		getWaClassDAO().getBaseDao().insertVOArrayWithPK(dataVOs);

	}

	public WaClassVO unsealAssignedWaClass(WaClassVO vo, UFDate enableDate,
			WaClassItemVO[] vos) throws nc.vo.pub.BusinessException {

		// ����
		BDPKLockUtil.lockSuperVO(vo);
		// �汾У�飨ʱ���У�飩������+ʱ�������֤�汾һ����
		BDVersionValidationUtil.validateSuperVO(vo);
		// ���÷��״̬���Զ���У��������У�飩
		vo = setunSeal(vo, enableDate);

		// ����
		vo = updateUnSealFlagAndState(vo, enableDate);

		// ��ǰ�ڼ����Ƿ�����Ŀ�����û������Ӹ�������������Ŀ
		if (!waPeriodQuery.hasWaclassItem(vo)) {
			insertWaClassitemForAssigned(vo, vos);
		}

		// ��ӵ���
		// ��������ڼ�û����Ա���������Ա
		if (!getWaClassDAO().isHavePsn(vo)) {
			getWaClassDAO().updatePayfileForUnseal(vo);
		}

		return getWaClassDAO().completeRange(vo);
	}



	private WaClassVO updateSealFlagAndState(WaClassVO vo)
			throws BusinessException {

		// ǰ�¼�֪ͨ
		BusinessEvent beforeEvent = new BusinessEvent(
				WaBusinessDocName.WACLASS_DOC_NAME,
				IEventType.TYPE_UPDATE_BEFORE, vo);
		EventDispatcher.fireEvent(beforeEvent);

		// ���������Ϣ
		vo.setStatus(VOStatus.UPDATED);
		SimpleDocServiceTemplate.setAuditInfoAndTs(vo, getDocService()
				.isProcessAuditInfo());

		// �����µ�ҵ���ڼ���
		sealPeriodState(vo);
		
		// ����
		getMDPersistenceService().saveBillWithRealDelete(vo);
		

		// ���¼�֪ͨ
		BusinessEvent afterEvent = new BusinessEvent(
				WaBusinessDocName.WACLASS_DOC_NAME,
				IEventType.TYPE_UPDATE_AFTER, vo.getPrimaryKey());
		EventDispatcher.fireEvent(afterEvent);
		
		fireCacheDataChanged(vo);
		
		return getDocService().updateTsAndKeys(vo);
	}

	/**************************************************************
     * ���µ���֮��֪ͨͬ������ <br>
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
					"060130waclass0079")/* @res "н�ʷ������ڼ�״̬�޸�ʧ��" */);
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
			//�����¼�
			fireCacheDataChanged(vo);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0079")/* @res "н�ʷ������ڼ�״̬�޸�ʧ��" */);
		}

	}

	private WaClassVO updateUnSealFlagAndState(WaClassVO vo, UFDate enableDate)
			throws BusinessException {

		// ǰ�¼�֪ͨ
		BusinessEvent beforeEvent = new BusinessEvent(
				WaBusinessDocName.WACLASS_DOC_NAME,
				IEventType.TYPE_UPDATE_BEFORE, vo);
		EventDispatcher.fireEvent(beforeEvent);

		// ���������Ϣ
		vo.setStatus(VOStatus.UPDATED);
		SimpleDocServiceTemplate.setAuditInfoAndTs(vo, getDocService()
				.isProcessAuditInfo());

		// ����н������������� ����н���ڼ䡣

		PeriodVO pvo = waPeriodQuery.queryWaPeriod(vo.getPk_wa_class(), enableDate);
		if (pvo == null) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0088")/* @res "�������ڲ���ȷ��û�в��ҵ���Ӧ��н���ڼ�" */);
		}
		// �ж������ڼ��Ƿ���ڵ���ͣ���ڼ�
		boolean isenable = getWaClassDAO().isEnablePeriod(vo.getPk_wa_class(),
				pvo);
		if (!isenable) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0089")/* @res "�����ڼ������ڵ���ͣ���ڼ䣡" */);
		}
		// �Ƿ��ŷ���ķ��������Ҽ��ŷ�����ǰ�ڼ䱻ͣ��

		// �������µ�ҵ���ڼ�
		vo.setCyear(pvo.getCyear());
		vo.setCperiod(pvo.getCperiod());
		// ���ұ���
		getMDPersistenceService().saveBillWithRealDelete(vo);

		// �����µ�ҵ���ڼ�����
		unsealPeriodState(vo, enableDate);



		// ���¼�֪ͨ
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

		//fengwei 2012-07-31 ��ѯHR��֯ʱ��Ҫ����org_hrorg�����Ѿ�ͣ�õ�HR��֯���˵�
		//20150716  shenliangc �������������������⣬��ѯHR��֯û��Ҫ�����ֶζ��������
		String sql = "select org_orgs.pk_org, org_orgs.innercode, org_orgs.code, org_orgs.name from org_orgs inner join org_hrorg on org_hrorg.pk_hrorg = org_orgs.pk_org where org_orgs.pk_group = '"
				+ vo.getPk_group()
				+ "' and org_orgs.orgtype4 = 'Y' and org_orgs.pk_group <> org_orgs.pk_org and org_hrorg.enablestate = 2 "
				+ " and org_orgs.enablestate = 2";

		OrgVO[] childrenOrgs = new BaseDAOManager().executeQueryVOs(sql, OrgVO.class);

		return getWaClassDAO().queryWaClsUnassignedOrg(vo, childrenOrgs);
	}

	/**
	 * ��֯��н�ʷ����Ƿ����Ѿ���˵�ҵ������
	 */
	@Override
	public boolean WaClsHasBusinessData(WaClassVO vo)
			throws nc.vo.pub.BusinessException {

		return currentPeriodHasCheckedData(vo);

	}

	@Override
	public void assignWaClass(WaClassVO waClassvo, AssignclsVO[] vos)
			throws BusinessException {
		// �õ������ӵķ����ϵ ����Ҫɾ���ķ����ϵ
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

		// ����ɾ����
		deleteAssignedWaclass(waClassvo, deleteVOs);

		// �����޸���ʼ�ڼ��
		updateAssignedWaclass(waClassvo, updateVOs);

		// ����������
		insertAssignedWaclass(waClassvo, assignVOs);

	}

	/**
	 * ����н�ʷ���ʱ������������н�ʷ���
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
		// �õ���������°汾�Ĺ���н����Ŀ
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
			// �������code
			classvo.setCode(waClassvo.getCode() + assignclsVO.getCode());
			classvo.setPk_org(assignclsVO.getPk_org());
			WaClassVO newWaclassvo = getDocService().insert(classvo);

			// ���ԸĽ�Ч����
			WaClassItemVO[] classitemVOs = classitemQuery.queryVersionItems(waClassvo, assignclsVO.getStartyear(),
					assignclsVO.getStartperiod());

			// Ϊ���������𴴽�������Ŀ
			insertWaClassitemForAssigned(newWaclassvo, classitemVOs);

			// Ϊ���������𴴽��ڼ���״̬
			insertPeriodState(newWaclassvo);

			// ������һ���ӷ���
			//			createChildClass(newWaclassvo);
			// ��������ϵ
			assignclsVO.setPk_sourcecls(waClassvo.getPk_wa_class());
			assignclsVO.setClassid(newWaclassvo.getPk_wa_class());
			assignclsVO.setModuleflag(0);// н��ģ��
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
					"060130waclass0090")/* @res "Ϊ�ӷ������н�ʷ�����Ŀʧ��" */);
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
	 * ����н�ʷ���ʱ�������޸ĵĵ�н�ʷ���
	 * 
	 * @author xuanlt on 2009-12-22
	 * @param waClassvo
	 * @param assignVOs
	 * @throws BusinessException
	 */
	private void deleteAssignedWaclass(WaClassVO waClassvo,
			ArrayList<AssignclsVO> assignVOs) throws BusinessException {

		WaClassVO[] classVOs = new WaClassVO[assignVOs.size()];
		// �õ���������°汾�Ĺ���н����Ŀ
		for (int index = 0; index < assignVOs.size(); index++) {
			AssignclsVO assignclsVO = assignVOs.get(index);

			WaClassVO classvo = new WaClassVO();
			classvo.setPk_wa_class(assignclsVO.getClassid());
			classvo.setStatus(VOStatus.DELETED);
			classvo.setCyear(assignclsVO.getStartyear());
			classvo.setCperiod(assignclsVO.getStartperiod());
			classvo.setStartyear(assignclsVO.getStartyear());
			classvo.setStartperiod(assignclsVO.getStartperiod());
			// ɾ����η����ӷ���
			if (WaLoginVOHelper.isMultiClass(classvo)) {
				String condition = " pk_wa_class in (select pk_childclass from wa_inludeclass where pk_parentclass = '"
						+ classvo.getPk_wa_class() + "')";
				WaClassVO[] childVOs = getDocService().queryByCondition(
						WaClassVO.class, condition);
				if (childVOs != null && childVOs.length > 0) {
					deleteIncludeClass(classvo.getPk_wa_class());
					for (WaClassVO childVO : childVOs) {
						deleteWaClass(childVO);
						// -->��Ϊʹ�����ӹ�ϵ�ϲ���˰
						// deleteTaxGroup(childVO.getPk_wa_class());
					}
				}
			}
			classVOs[index] = classvo; //by wangqim
			// ɾ���ڼ�״̬
//			deletePeriodState(classvo);
			// ɾ�������Ŀ
//			deleteClassitem(classvo);

			//zhoumxc  20140829      ������ԣ�н�ʷ�����
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
		// ɾ�����еķ����ϵ
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
	 * ����н�ʷ���ʱ�������޸���ʼ�ڼ��
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
		// �����ж�н�ʷ����Ƿ���Է��
		WaClassDAO dao = new WaClassDAO();
		if (!dao.groupClassSealable(vo)) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0091")/* @res "����ķ����д��Ѿ���˵����ݣ�������ͣ��" */);
		}
		// ������е������
		// ˵���� ������п����뱾�����ͬһ���ڼ�
		dao.sealAssignedWaClass(vo);

		// ��� ��н�ʷ���
		return sealWaClass(vo);

	}

	/**
	 * @author xuanlt on 2010-1-26
	 * @see nc.itf.hr.wa.IWaClass#unsealGroupWaClass(nc.vo.wa.category.WaClassVO)
	 */
	@Override
	public WaClassVO unsealGroupWaClass(WaClassVO vo, UFDate enableDate)
			throws BusinessException {
		// �������ģ������
		// �鿴���е�������Ƿ���������
		// ��������µ� ҵ���ڼ��Ӧ�Ļ���ڼ��Ƿ��Ѿ����ˣ� �������������� ��û�н��ˣ���������

		// ���Ȳ鿴����������Ƿ�Ϸ����ҵ���Ӧ��н���ڼ䣩
		PeriodVO pvo = waPeriodQuery.queryWaPeriod(vo.getPk_wa_class(), enableDate);

		if (pvo == null) {
			throw new BusinessException(ResHelper.getString("60130waclass",
					"060130waclass0088")/* @res "�������ڲ���ȷ��û�в��ҵ���Ӧ��н���ڼ�" */);
		}

		// �ж����������Ƿ� >= ���������ķ���ڼ䣡
		// ������ڲ��������н�ʷ��� ��������ʾ��
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
					"060130waclass0092")/* @res "н�ʷ���:" */
					+ names
					+ ResHelper.getString("60130waclass", "060130waclass0093")/*
					 * @res
					 * "ͣ���ڼ������������"
					 */);
		}

		/**
		 * ���⼯��н�ʷ���
		 */
		vo = unsealWaClass(vo, enableDate);
		WaClassItemVO[] classitemVOs = classitemQuery.queryMaxversionItems(vo);

		// ����������н�ʷ��� ����Ҫ������������
		unSealAssignedWaClass(vo, enableDate, classitemVOs);

		return vo;

	}

	private void unSealAssignedWaClass(WaClassVO vo, UFDate enableDate,
			WaClassItemVO[] currClassItems) throws BusinessException {
		try {
			// ������н�ʷ���
			WaClassVO[] subVos = getWaClassDAO().queryGroupAssignedWaclass(vo);
			// �������.н����Ŀ��ζ�����
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
					"060130waclass0094")/* @res "������н�ʷ���ʧ��" */);
		}

	}

	/**
	 * ��ѯ����ͬ����н�ʷ���
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
					"060130waclass0095")/* @res "��ѯ��н��Χʧ�ܣ�" */);
		}

	}

	@Override
	public boolean WaClsHasBusinessData(String pk_waclass, String cyear,
			String cperiod) throws BusinessException {
		return currentPeriodHasCheckedData(pk_waclass, cyear, cperiod);
	}

	/**
	 * ���ݸ������ڼ��ѯ�����ӷ���
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

	// 20150806 xiejie3 �����ϲ���NCdp205398612н�ʷ���Ȩ�޷���ʱ����ѡ������ְ����ͣ�õ��û�begin
	@Override
	public UserVO[] queryUserVObyOrg(String pkorg) throws BusinessException {
		return getWaClassDAO().queryUserVO(pkorg);
	}
	//end
	
}