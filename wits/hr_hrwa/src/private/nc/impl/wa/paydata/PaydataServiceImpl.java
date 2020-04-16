package nc.impl.wa.paydata;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.SimpleDocLocker;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.wa.log.WaBusilogUtil;
import nc.impl.wa.payfile.PayfileServiceImpl;
import nc.impl.wa.repay.RepayDAO;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.wa.IAmoSchemeQuery;
import nc.itf.hr.wa.IClassItemQueryService;
import nc.itf.hr.wa.IHRWAActionCode;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.IPaydataManageService;
import nc.itf.hr.wa.IPaydataQueryService;
import nc.itf.hr.wa.IUnitWaClassQuery;
import nc.itf.hr.wa.IWaClass;
import nc.itf.hr.wa.PaydataDspUtil;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.itf.hrwa.IWadaysalaryQueryService;
import nc.pubitf.rbac.IDataPermissionPubService;
import nc.ui.wa.pub.WADelegator;
import nc.vo.hi.psndoc.Attribute;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.append.AppendableVO;
import nc.vo.hr.caculate.CaculateTypeVO;
import nc.vo.hr.combinesort.SortVO;
import nc.vo.hr.combinesort.SortconVO;
import nc.vo.hr.datainterface.FormatItemVO;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.hr.tools.pub.Pair;
import nc.vo.hrp.budgetmgt.BudgetWarnMessageVo;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
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

/**
 * н�ʷ���ʵ����
 * 
 * @author: zhangg
 * @date: 2009-11-23 ����01:19:19
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class PaydataServiceImpl implements IPaydataManageService, IPaydataQueryService {

	private PaydataDAO queryService;

	private PaydataDAO getService() throws DAOException {
		if (queryService == null) {
			queryService = new PaydataDAO();
		}
		return queryService;
	}

	@Override
	public void update(Object vo, WaLoginVO waLoginVO) throws BusinessException {
		BDPKLockUtil.lockString(waLoginVO.getPk_wa_class());

		// �õ����޸ĵ���Ŀ��
		getService().update(vo, waLoginVO);
	}

	/**
	 * ���
	 * 
	 * @author zhangg on 2009-12-2
	 * @see nc.itf.hr.wa.IPaydataManageService#onCheck(nc.vo.wa.pub.WaLoginVO,
	 *      java.lang.String, java.lang.Boolean)
	 */
	@Override
	public void onCheck(WaLoginVO waLoginVO, String whereCondition, Boolean isRangeAll)
			throws nc.vo.pub.BusinessException {

		// �ȼ�����״̬
		getService().checkWaClassStateChange(waLoginVO, whereCondition);
		// ������˱�׼
		boolean isAllChecked = getService().onCheck(waLoginVO, whereCondition, isRangeAll);

		if (WaLoginVOHelper.isSubClass(waLoginVO) && isAllChecked) {
			collectWaTimesData(waLoginVO);
		}
	}

	/**
	 * ȡ�����
	 * 
	 * @author zhangg on 2009-12-2
	 * @see nc.itf.hr.wa.IPaydataManageService#onUnCheck(nc.vo.wa.pub.WaLoginVO,
	 *      java.lang.String)
	 */
	@Override
	public void onUnCheck(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll)
			throws nc.vo.pub.BusinessException {
		// �ȼ�����״̬
		getService().checkWaClassStateChange(waLoginVO, whereCondition);

		// ������˱�׼
		getService().onUnCheck(waLoginVO, whereCondition, isRangeAll);

		if (WaLoginVOHelper.isSubClass(waLoginVO)) {
			collectWaTimesData(waLoginVO);

			// 20160125 shenliangc NCdp205577867 н�ʶ�η���, ���Ŵ��������ܣ����ݴ��� ,
			// ȡ�������Ҫ������������ӵĵ�����Ա
			updateCollectDataForThisTime(waLoginVO);
		}

	}

	/**
	 * ��ȡ�û���Ȩ�޵�н����Ŀ��ȫ����
	 * 
	 * @author liangxr on 2010-5-13
	 * @see nc.itf.hr.wa.IPaydataQueryService#getUserClassItemVOs(nc.vo.wa.pub.WaLoginContext)
	 */
	@Override
	public WaClassItemVO[] getUserClassItemVOs(WaLoginContext loginVO) throws BusinessException {
		return getService().getUserClassItemVOs(loginVO);
	}

	/**
	 * ��ȡ�û���Ȩ�޵�н����Ŀ���ɼ���
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
	 * Ϊ����������ר���ṩ�ķ��� ͬʱ���� н�ʷ�����Ŀ,н����Ŀչ����Ϣ
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
	 * ����
	 * 
	 * @author zhangg on 2009-12-4
	 * @see nc.itf.hr.wa.IPaydataManageService#onPay(nc.vo.wa.pub.WaLoginVO)
	 */
	@Override
	public void onPay(WaLoginContext loginContext) throws nc.vo.pub.BusinessException {
		// �����ӷ���
		BDPKLockUtil.lockString(loginContext.getPk_wa_class());

		// �ȼ�����״̬
		getService().checkWaClassStateChange(loginContext.getWaLoginVO(), null);
		// ���·���
		getService().onPay(loginContext);
		// if(WaLoginVOHelper.isSubClass(loginContext.getWaLoginVO())){
		// collectWaTimesData(loginContext.getWaLoginVO());
		// }
	}

	/**
	 * ȡ������
	 * 
	 * @author liangxr on 2010-5-24
	 * @see nc.itf.hr.wa.IPaydataManageService#onUnPay(nc.vo.wa.pub.WaLoginVO)
	 */
	@Override
	public void onUnPay(WaLoginVO waLoginVO) throws nc.vo.pub.BusinessException {
		// �����ӷ���

		BDPKLockUtil.lockString(waLoginVO.getPk_wa_class());
		// �ȼ�����״̬
		getService().checkWaClassStateChange(waLoginVO, null);
		// ���н�ʷ����������Ƿ��Ѿ���̯�Ƶ�

		if (waLoginVO.getPk_prnt_class() != null && waLoginVO.getPk_prnt_class() != waLoginVO.getPk_wa_class()) {
			WaLoginVO subLoginVO = (WaLoginVO) waLoginVO.clone();
			subLoginVO.setPk_wa_class(waLoginVO.getPk_prnt_class());
			checkIsApportion(subLoginVO);
		}
		// 2015-11-5 zhousze �Ӱ汾У�飬ͨ����̨��ѯ�ڼ�״̬���pk��ts begin
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
		// ���·���
		getService().onUnPay(waLoginVO);

		// MOD {ȡ�����ţ�ɾ��Allocate���ɵ���ϸ��} kevin.nie 2017-09-22 start
		NCLocator.getInstance().lookup(nc.itf.hrwa.IAllocateMaintain.class).deleteByContext(waLoginVO);
		// {ȡ�����ţ�ɾ��Allocate���ɵ���ϸ��} kevin.nie 2017-09-22 end
	}

	/**
	 * ���н�ʷ����Ƿ��Ѿ���̯�Ƶ�
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
																									 * "ѡ���н�ʷ����������Ѿ���̯�Ƶ�,����ȡ�����ţ�"
																									 */);
			} else {
				throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0458")/*
																									 * @
																									 * res
																									 * "ѡ���н�ʷ����Ѿ���̯�Ƶ�,����ȡ�����ţ�"
																									 */);
			}
		}
	}

	/**
	 * �滻
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
		// �������ά��Ȩ
		// 20150924 shenliangc ���滻������ԭ����ƽ̨�޸���ȡ����Ȩ�޵Ĺ��߷��������ڸ�Ϊ�������Լ��ġ�
		// String operateConditon = WaPowerSqlHelper.getWaPowerSql(
		// PubEnv.getPk_group(), IHRWADataResCode.WADATA,
		// IHRWADataResCode.WADEFAULT,"wa_data" );
		// if (StringUtils.isBlank(whereCondition)) {
		// whereCondition = operateConditon;
		// } else {
		// whereCondition = whereCondition
		// + WherePartUtil.formatAddtionalWhere(operateConditon);
		// }
		// �������ά��Ȩ NCdp205509858 н�ʷ���ά��Ȩȫ����Ȩ/�¹�����Ȩ����Ȩ���ݿ��滻 mizhl 2015-10-27
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
		// �������ʹ��Ȩ
		String userConditon = WaPowerSqlHelper.getWaPowerSql(PubEnv.getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB, IHRWADataResCode.WADEFAULT, "wa_data");
		if (StringUtils.isBlank(whereCondition)) {
			whereCondition = userConditon;
		} else {
			whereCondition = whereCondition + WherePartUtil.formatAddtionalWhere(userConditon);
		}
		// �������ά��Ȩ NCdp205509858 н�ʷ���ά��Ȩȫ����Ȩ/�¹�����Ȩ����Ȩ���ݿ��滻 mizhl 2015-10-27 end
		// �滻��¼��־
		if (null != superVOs) {
			// �汾У�飨ʱ���У�飩
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
			// ���û�������ֶ� �ȵ����ݿ��в�ѯ��û�е�ǰ�û����������� by wangqim
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
	 * �ж�һ�������������ǵ��λ��Ƕ�Σ����Ƕ���е�һ�Σ��Ƿ��Ѿ����Ź���һ�Σ�
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

		// {MOD:�¸�˰����}
		// begin
		InSQLCreator inSQLCreator = new InSQLCreator();
		try {
			String conditon = inSQLCreator.getInSQL(pk_wa_data);
			DataVO[] dataVOArrays = getService().queryByPKSCondition(conditon, "");
			// autor erl 2011-6-10 11:40 ����������������˳������
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

		/*
		 * DataVO[] dataVOArrays = getService().queryByPKSCondition( new
		 * InSQLCreator().getInSQL(pk_wa_data), ""); // autor erl 2011-6-10
		 * 11:40 ����������������˳������ List<DataVO> dataVOList = new ArrayList<DataVO>();
		 * Map<String, DataVO> dataVOMap = new HashMap<String, DataVO>(); for
		 * (DataVO dataVO : dataVOArrays) {
		 * dataVOMap.put(dataVO.getPk_wa_data(), dataVO); } for (String
		 * str_pk_wa_data : pk_wa_data) {
		 * dataVOList.add(dataVOMap.get(str_pk_wa_data)); return
		 * dataVOList.toArray(new DataVO[0]);
		 */
		// end
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
																											 * "�����д����ظ�����Ա���������"
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
																								 * "û�����ù�����Ŀ���߹�����Ŀ��ֵΪ�գ����ܵ��룡"
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

		// ����н�ʷ���(��������)
		if (!StringUtils.isBlank(loginContext.getPk_prnt_class())) {
			BDPKLockUtil.lockString(loginContext.getPk_prnt_class());
		}

		// �����ӷ���
		new SimpleDocLocker().lock(SimpleDocServiceTemplate.UPDATE, loginContext.getWaLoginVO());

		// У���ӷ����İ汾һ����
		// �汾У�飨ʱ���У�飩
		BDVersionValidationUtil.validateSuperVO(loginContext.getWaLoginVO());

		if (null != superVOs) {
			// �汾У�飨ʱ���У�飩
			BDVersionValidationUtil.validateSuperVO(superVOs);
		}
		// ������н�ļ���
		doDaySalary(loginContext, caculateTypeVO, superVOs);

		// �жϲ���
		checkReData(loginContext.getWaLoginVO());

		DataCaculateService caculateService = new DataCaculateService(loginContext, caculateTypeVO, condition);
		caculateService.doCaculate();
	}

	private void doDaySalary(WaLoginContext waLoginContext, CaculateTypeVO caculateTypeVO, SuperVO... superVOs)
			throws BusinessException {
		WaLoginVO waLoginVO = waLoginContext.getWaLoginVO();
		PsncomputeVO[] vos = WADelegator.getPsndocWa().queryAllShowResult(waLoginVO, waLoginVO.getDeptpower(),
				waLoginVO.getPsnclpower());
		String[] psndocs = new String[0];
		if (caculateTypeVO != null) {
			// �Ƿ�ȫ����Ա����
			boolean all = caculateTypeVO.getRange().booleanValue();
			// �Ƿ�ֻ����û�м��������
			boolean type = caculateTypeVO.getType().booleanValue();
			if (all) {
				// ��ȡ���е���Ա
				psndocs = getPsndocs(vos);
			} else {
				// ֻ��ȡ������Ա
				psndocs = getByType(superVOs, type);
			}
		} else {
			// ��ȡ���е���Ա
			psndocs = getPsndocs(vos);
		}
		if (psndocs == null || psndocs.length <= 0) {
			return;
		}
		// ��ȡ���е�н����Ŀ
		String[] pk_wa_items = getWaItems(vos);
		for (String pk_wa_item : pk_wa_items) {
			Map<String, UFDouble> psnSalaryMap = NCLocator
					.getInstance()
					.lookup(IWadaysalaryQueryService.class)
					.getTotalDaySalaryMapByWaItem(psndocs, waLoginContext.getPk_wa_class(), waLoginContext.getCyear(),
							waLoginContext.getCperiod(), pk_wa_item);
			if (psnSalaryMap == null || psnSalaryMap.size() == 0) {
				continue;
			}
			// �r�cн�YӋ�㆖�}  by George 20190705 ȱ��Bug #28250
			for (String psndoc : psndocs) {
				// �h���T����н�Y�Ŀ��Ӌ���Ա����н�������� 
				// ��нӋ�㹫ʽ update wa_data f_15 = coalesce (��н����н��0)
				// ��н����н���]�Єh����н�Y�ϣ����ҵ���н������Ҫ�Ȅh�����ٱȌ���н�Ƿ�����н������н
			    String strSQL = "delete from hi_psndoc_wa where hi_psndoc_wa.dr = 0 and"
					    + " hi_psndoc_wa.pk_wa_item = '" + pk_wa_item + "' and"
					    + " hi_psndoc_wa.pk_wa_class = '" + waLoginContext.getPk_wa_class() +"' and"
					    + " hi_psndoc_wa.cyear = '" + waLoginContext.getCyear() +"' and"
					    + " hi_psndoc_wa.cperiod = '" + waLoginContext.getCperiod() + "' and"
					    + " pk_psndoc = '" + psndoc + "'";

			    getService().executeSQLs(strSQL);
			}
			
			PsndocWaVO[] psndocWaVOs = convertComputeVO(vos, psnSalaryMap, pk_wa_item, waLoginVO);

			WADelegator.getPsndocWa().updatePsndocWas(psndocWaVOs);
			// shenliangc 20140826 ʱ��н�ʼ��㱣��֮������������Ӧ��Ա�ķ������ݼ����־
			WADelegator.getPaydata().updateCalFlag4OnTime(psndocWaVOs);
		}

	}

	// ��ȡ��Աͨ����� type: true ȫ������ false :ֻ����û�н���н�ʼ������Ա
	private String[] getByType(SuperVO[] superVOs, boolean type) {

		Set<String> result = new HashSet<>();

		if (type && superVOs != null) {
			// ȫ����Ա
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
			// δ�������Ա
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
	 * ��װvo�洢
	 * 
	 * @param vos
	 * @param psnSalaryMap
	 * @param pk_wa_item
	 * @return
	 * @throws BusinessException
	 */
	private PsndocWaVO[] convertComputeVO(PsncomputeVO[] psncomputeVOs, Map<String, UFDouble> psnSalaryMap,
			String pk_wa_item, WaLoginVO waLoginVO) throws BusinessException {

		List<PsndocWaVO> returnList = new ArrayList<>();
		for (int i = 0; i < psncomputeVOs.length; i++) {
			PsncomputeVO result = psncomputeVOs[i];
			if (null == psnSalaryMap || !psnSalaryMap.keySet().contains(result.getPk_psndoc())) {
				continue;
			}
			PsndocWaVO psndocWa = null;
			if (result.getPk_wa_item() == null || result.getPk_wa_item().equals(pk_wa_item)) {
				psndocWa = result.getPsndocwavo();
			} else {
				continue;
			}

			if (null == psndocWa) {
				psndocWa = new PsndocWaVO();
			}
			psndocWa.setPk_psndoc_sub(result.getPk_psndoc_sub());
			psndocWa.setPsnname(result.getPsnname());
			psndocWa.setPsncode(result.getPsncode());
			psndocWa.setPk_psndoc(result.getPk_psndoc());
			psndocWa.setPk_psndoc_wa(result.getPk_psncompute());
			psndocWa.setPk_wa_item(result.getPk_wa_item());
			psndocWa.setItemname(result.getItemvname());
			psndocWa.setOldmoney(UFDouble.ZERO_DBL);
			psndocWa.setNowmoney(psnSalaryMap.get(result.getPk_psndoc()) == null ? UFDouble.ZERO_DBL : psnSalaryMap
					.get(result.getPk_psndoc()));
			psndocWa.setNmoney(psnSalaryMap.get(result.getPk_psndoc()) == null ? UFDouble.ZERO_DBL : psnSalaryMap
					.get(result.getPk_psndoc()));
			psndocWa.setCyear(waLoginVO.getCyear());
			psndocWa.setCperiod(waLoginVO.getCperiod());
			psndocWa.setPk_wa_class(waLoginVO.getPeriodVO().getPk_wa_class());
			psndocWa.setWaclassname(waLoginVO.getName());
			UFLiteralDate startDate = waLoginVO.getPeriodVO().getCstartdate();

			UFLiteralDate endDate = waLoginVO.getPeriodVO().getCenddate();

			psndocWa.setBegindate(startDate);
			psndocWa.setEnddate(endDate);

			psndocWa.setNceforedays(UFDouble.ZERO_DBL);
			psndocWa.setNbeforemoney(UFDouble.ZERO_DBL);
			psndocWa.setNafterdays(UFDouble.ZERO_DBL);
			psndocWa.setNaftermoney(UFDouble.ZERO_DBL);
			psndocWa.setDaywage(Integer.valueOf(0));
			psndocWa.setIwatype(Integer.valueOf(0));
			psndocWa.setTs(new UFDateTime());
			psndocWa.setBasedays(UFDouble.ZERO_DBL);
			psndocWa.setSub_ts(result.getSub_ts());
			psndocWa.setPre_sub_id(result.getPre_sub_id());
			psndocWa.setPre_sub_ts(result.getPre_sub_ts());
			psndocWa.setAssgid(result.getAssgid());
			result.setPsndocwavo(psndocWa);

			returnList.add(psndocWa);
		}
		return returnList.toArray(new PsndocWaVO[0]);
	}

	/**
	 * ��ȡ���е�н����Ŀ
	 * 
	 * @param vos
	 * @return
	 */
	private String[] getWaItems(PsncomputeVO[] vos) {
		Set<String> resultSet = new HashSet<>();
		if (null == vos || vos.length <= 0) {
			return resultSet.toArray(new String[0]);
		}
		for (PsncomputeVO vo : vos) {
			if (vo != null && vo.getPk_wa_item() != null) {
				resultSet.add(vo.getPk_wa_item());
			}
		}
		return resultSet.toArray(new String[0]);
	}

	/**
	 * ��ȡ���е���Ա
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
	 * �жϲ���
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
																								 * "����������δ���ݹ������޷�������"
																								 */);
		}

	}

	private boolean haveMakeRedata(WaLoginVO waLoginVO, String power) throws nc.vo.pub.BusinessException {
		boolean b = false;
		RepayDAO dao = new RepayDAO();
		ReDataVO[] dataVo = dao.queryAllAt(waLoginVO, null, null, power);
		// û�в���
		if (ArrayUtils.isEmpty(dataVo)) {
			b = true;
		} else {
			// ����
			dataVo = dao.queryAllAt(waLoginVO, "-1", "-1", power);

			// �����ɻ��ܱ�
			if (!ArrayUtils.isEmpty(dataVo)) {
				dataVo = dao.queryAllAt(waLoginVO, "-1", "-1", "  wa_redata.reflag='N' ");
				// ������û���ύ����Ա
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
																									 * "������ܵķ����е���δ���ʣ�"
																									 */);
			}

			PaydataDAO dao = new PaydataDAO();
			WaItemVO[] itemVOs = dao.getUnitDigitItem(waLoginVO);

			sumWaData(waLoginVO, itemVOs);

			// ���¼����־
			dao.updateStateforTotal(waLoginVO);

		} else {
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0461")/*
																								 * @
																								 * res
																								 * "��ǰ״̬ ���ܻ��ܣ�"
																								 */);// new
		}

	}

	// @Override
	// public void collectWaTimesData(WaLoginVO waLoginVO) throws
	// BusinessException{
	// //������ֵ����Ŀ
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
	// //�����ַ���������Ŀ
	// dao.collectWaTimesNODigitData(waLoginVO, childClasses);
	//
	// //˰�ʵ������Ϣ�����ο�˰������Ӧ��˰���ö�taxable_income��˰��taxrate������۳���nquickdebuct�����ÿ۳���׼expense_deduction��Ӧ�������һ�η��ŵĿ�˰������
	// dao.collectTaxBase(waLoginVO, childClasses);
	// //�ѿ�˰���ѿ�˰������0
	// dao.collectTaxedAndTaxedBase(waLoginVO, childClasses);
	// //����ͣ����ʾ
	// dao.collectStopflag(waLoginVO);
	// //���¸�������״̬
	//
	// // �����������Ժ�ͬ���¶ȸ�������˰
	// WaLoginContext context = waLoginVO.toWaLoginContext();
	//
	// new ParentClassPsntaxService().doPsnTax(context, childClasses);
	//
	// }

	// 20160125 shenliangc NCdp205577867 н�ʶ�η���, ���Ŵ��������ܣ����ݴ��� ,
	// ȡ�������Ҫ������������ӵĵ�����Ա
	public void updateCollectDataForThisTime(WaLoginVO waLoginVO) throws BusinessException {
		// ������ֵ����Ŀ
		PaydataCollectDAO dao = new PaydataCollectDAO();
		String pk_waclass = waLoginVO.getPk_prnt_class();
		String cyear = waLoginVO.getPeriodVO().getCyear();
		String cperiod = waLoginVO.getPeriodVO().getCperiod();
		WaInludeclassVO[] childClasses = ((IWaClass) NCLocator.getInstance().lookup(IWaClass.class))
				.queryAllCheckedChildClasses(pk_waclass, cyear, cperiod);
		WaClassItemVO[] itemVOs = ((IClassItemQueryService) NCLocator.getInstance()
				.lookup(IClassItemQueryService.class)).queryItemsByPK_wa_class(pk_waclass, cyear, cperiod);
		dao.updateCollectDataForThisTime(waLoginVO, childClasses, itemVOs);
	}

	/**
	 * * �Է��Ŵ������ݽ��л��ܡ������Ѿ����ŵ����ݶ���Ҫ����
	 * 
	 * @throws BusinessException
	 * 
	 */
	@Override
	public void collectWaTimesData(WaLoginVO waLoginVO) throws BusinessException {
		// ������ֵ����Ŀ
		// {MOD:�¸�˰����}
		// begin
		NewTaxPaydataCollectDAO dao = new NewTaxPaydataCollectDAO();
		// PaydataCollectDAO dao = new PaydataCollectDAO();
		// end
		String pk_waclass = waLoginVO.getPk_prnt_class();
		String cyear = waLoginVO.getPeriodVO().getCyear();
		String cperiod = waLoginVO.getPeriodVO().getCperiod();

		WaInludeclassVO[] childClasses = NCLocator.getInstance().lookup(IWaClass.class)
				.queryAllCheckedChildClasses(pk_waclass, cyear, cperiod);

		dao.collectWaTimesDigitData(waLoginVO, childClasses);
		// {MOD:�¸�˰����}
		// begin
		// ��˰��
		dao.collectWaTimesNewTaxDigitData(waLoginVO, childClasses);
		// end
		// �����ַ���������Ŀ
		dao.collectWaTimesNODigitData(waLoginVO, childClasses);

		// ˰�ʵ������Ϣ�����ο�˰������Ӧ��˰���ö�taxable_income��˰��taxrate������۳���nquickdebuct�����ÿ۳���׼expense_deduction��Ӧ�������һ�η��ŵĿ�˰������
		dao.collectTaxBase(waLoginVO, childClasses);
		// �ѿ�˰���ѿ�˰������0
		dao.collectTaxedAndTaxedBase(waLoginVO, childClasses);
		// ����ͣ����ʾ
		dao.collectStopflag(waLoginVO);
		// ���¸�������״̬

		// �����������Ժ�ͬ���¶ȸ�������˰
		WaLoginContext context = waLoginVO.toWaLoginContext();

		new ParentClassPsntaxService().doPsnTax(context, childClasses);

	}

	public void sumWaData(WaLoginVO waLoginVO, WaItemVO[] itemVOs) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		// ��������������ɾ����
		dao.deleteUnitClassPsn(waLoginVO);
		// �õ���Ҫ���ӵĻ��ܵ���Ա
		PayfileVO[] addPsn = getUnitPsnVOs(waLoginVO);
		// ���������Ա
		if (!ArrayUtils.isEmpty(addPsn)) {
			PayfileServiceImpl payfileImpl = new PayfileServiceImpl();
			payfileImpl.addPsnVOs(addPsn);
		}
		// ����DATA����
		dao.updateData(waLoginVO, itemVOs);
		// {MOD:�¸�˰����}
		// begin
		// ��˰�Ļ���н�ʷ��������޸�
		NewTaxPaydataCollectDAO newTaxDao = new NewTaxPaydataCollectDAO();
		newTaxDao.updateDataNewTax(waLoginVO, itemVOs);
		// end
		// ���»���ʱɾ����ر������Ա��Ϣ
		dao.deleteUnitRelation(waLoginVO);
	}

	private PayfileVO[] getUnitPsnVOs(WaLoginVO waLoginVO) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		PayfileVO[] datavos = dao.getUnitPsnVOs(waLoginVO);
		if (datavos == null) {
			return null;
		}
		// ȥ���ظ��ģ� �������ְ�ͼ�ְ�� ȡ��ְ
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

				// guoqt��ְ��֯����ְ���š�������֯�������š��ɱ����ġ��ɱ�����
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
		// guoqt н�ʷ������뼰�������Ӻϼ���
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
	 * ����֯���㹤����ֵ
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
	 * 2014/05/23 shenliangcΪ���н�ʷ��Žڵ���ʾ���öԻ�������Ŀ�����뱾�ڼ䷢����Ŀ���Ʋ�ͬ��������޸ġ�
	 */
	@Override
	public List<WaPaydataDspVO> queryPaydataDisplayInfo(WaLoginContext context) throws BusinessException {
		// IItemQueryService itemService =
		// NCLocator.getInstance().lookup(IItemQueryService.class);
		IClassItemQueryService citemService = NCLocator.getInstance().lookup(IClassItemQueryService.class);
		// ���ҷ�����ʹ�õ������й���н����Ŀ
		// WaItemVO[] itemArray =
		// itemService.queryByWaClass(context.getPk_wa_class());

		// ��ѯ��ǰн�����н���ڼ����е�н�ʷ�����Ŀ
		WaItemVO[] itemArray = citemService.queryAllClassItemInfosByPower(context);

		// ���Ҹ�����ʾ����
		List<WaPaydataDspVO> dspList = this.queryPaydataPersonalDsp(context);
		if (dspList == null || dspList.isEmpty()) {
			// û�����ø�����ʾ���ã���ô��ͨ����ʾ
			dspList = this.queryPaydataCommonDsp(context);
		}

		if (dspList == null || dspList.isEmpty()) {
			// ��û�и�����ʾ���ã�Ҳû��ͨ����ʾ���ã��Ͱ�Ĭ����ʾ
			dspList = PaydataDspUtil.queryDefaultDsp();
			if (!ArrayUtils.isEmpty(itemArray)) {
				// ȡн����Ŀ�����õ���ʾ˳��
				WaClassItemVO[] classItemVOs = getUserShowClassItemVOs(context);
				Map<String, WaItemVO> itemMap = CommonUtils.toMap(WaItemVO.PK_WA_ITEM, itemArray);
				List<WaItemVO> itemList = new ArrayList<WaItemVO>();
				for (int i = 0, j = ArrayUtils.getLength(classItemVOs); i < j; i++) {
					WaItemVO itemVO = itemMap.get(classItemVOs[i].getPk_wa_item());
					if (itemVO == null)
						continue;
					itemList.add(itemVO);
				}
				// ת������н����Ŀ����ӵ���ʾ��Ŀ
				List<WaPaydataDspVO> waItemsDspList = PaydataDspUtil
						.convertWaItemVO(CollectionUtils.isEmpty(itemList) ? null : itemList.toArray(new WaItemVO[0]));
				dspList.addAll(waItemsDspList);
			}

		} else {
			// ����item_name
			// 2014/05/23 shenliangcΪ���н�ʷ��Žڵ���ʾ���öԻ�������Ŀ�����뱾�ڼ䷢����Ŀ���Ʋ�ͬ��������޸ġ�
			// ���Ʋ�ͬ����ԭ���ǲ�ѯ��Ŀ�����߼�û���������ڼ����ƣ�ȫ��ȡ������ʼ�ڼ�ķ�����Ŀ���ơ�
			PaydataDspUtil.setPaydataDisplayName(dspList, context);
			// ��������ʾ˳��֮�󣬷��������¼ӵ���Ŀ����Ҫ���
			PaydataDspUtil.addNewlyDsiplayItem(dspList, itemArray);
		}
		return dspList;
	}

	/**
	 * ����ͨ����ʾ����
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
						+ ICommonAlterName.DEPTNAME
						+ "','"
						+ "taxorgname"
						+ "','"
						+ ICommonAlterName.POSTNAME // (Combined by ssx for WITS
													// on 2019-04-15)
						+ "','"
						// 20151103 xiejie3 н�ʷ��Ž�����Ӳ�����֯������,��ʾ���ý���ͬ����
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
						+ "taxorgname"
						+ "','" // (Combined by ssx for WITS on 2019-04-15)
						+ ICommonAlterName.POSTNAME
						+ "','"
						// 20151103 xiejie3 н�ʷ��Ž�����Ӳ�����֯������,��ʾ���ý���ͬ����
						+ ICommonAlterName.FINANCEORG + "','" + ICommonAlterName.FINANCEDEPT + "','"
						+ ICommonAlterName.LIABILITYORG + "','" + ICommonAlterName.LIABILITYDEPT + "','"
						// end
						+ DataVO.TAXTYPE + "','" + DataVO.CACULATEFLAG + "','" + DataVO.CHECKFLAG + "','"
						+ DataVO.CYEAR + "','" + DataVO.CPERIOD + "','" + DataVO.PSNNAME + "') ) ");
		return (List<WaPaydataDspVO>) queryService.getBaseDao().retrieveByClause(WaPaydataDspVO.class,
				condtion.toString(), "displayseq");
	}

	/**
	 * ���ҷ�����ʾ����
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
						+ "taxorgname" // (Combined by ssx for WITS on
										// 2019-04-15)
						+ "','"
						+ ICommonAlterName.POSTNAME
						+ "','"

						// shenliangc 20140702 ���б������ӻ���
						// 2015-07-30 zhosuze NCdp205099799 н�ʷ��Ž�����Ӳ�����֯������
						// begin
						// 20151026 xiejie3 ������治չʾ������֯������
						+ ICommonAlterName.FINANCEORG + "','" + ICommonAlterName.FINANCEDEPT + "','"
						+ ICommonAlterName.LIABILITYORG + "','" + ICommonAlterName.LIABILITYDEPT + "','"
						// end

						+ DataVO.TAXTYPE + "','" + DataVO.CACULATEFLAG + "','" + DataVO.CHECKFLAG + "','"
						+ DataVO.CYEAR + "','" + DataVO.CPERIOD + "','" + DataVO.PSNNAME + "') ) ");

		return (List<WaPaydataDspVO>) getService().getBaseDao().retrieveByClause(WaPaydataDspVO.class,
				condtion.toString(), "displayseq");
	}

	/**
	 * ������ְ��нͨ����ʾ����
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
						// 20151103 xiejie3 ����Ҫ�� ��ְ��н����������Ա����
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
						+ "taxorgname" // (Combined by ssx for WITS on
										// 2019-04-15)
						+ "','"
						+ ICommonAlterName.POSTNAME
						+ "','"
						// 20151103 xiejie3 н�ʷ��Ž�����Ӳ�����֯������,��ʾ���ý���ͬ����
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
						// ��ְ��н��ʾ���ò���ʾ��ְ���ڡ��������ڡ�����ԭ���ٴδ���ʾ����ʱ���⼸���ֶζ�ʧ
						+ DataVO.LEAVEDATE
						// /*��ְ����*/
						+ "','"
						+ DataVO.CPAYDATE
						// /*��������*/
						+ "','"
						+ DataVO.VPAYCOMMENT
						// /*����ԭ��*/
						// end
						// 20151127 lizt NCdp205550515
						// 3����ְ��н����ʾ���ã�ȫ��ѡ����Ƭ���滹��ʾ������֯�����ű�־�ͷ������ڣ�������
						/* ���ű�־ */
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
						+ "taxorgname" // (Combined by ssx for WITS on
										// 2019-04-15)
						+ "','"
						+ ICommonAlterName.POSTNAME
						+ "','"

						// 20151113 xiejie3 NCdp205541370
						// ��ְ��н��ʾ���ò���ʾ��ְ���ڡ��������ڡ�����ԭ���ٴδ���ʾ����ʱ���⼸���ֶζ�ʧ
						+ DataVO.LEAVEDATE
						// /*��ְ����*/
						+ "','"
						+ DataVO.CPAYDATE
						// /*��������*/
						+ "','"
						+ DataVO.VPAYCOMMENT
						// /*����ԭ��*/
						// end

						// 20151127 lizt NCdp205550515
						// 3����ְ��н����ʾ���ã�ȫ��ѡ����Ƭ���滹��ʾ������֯�����ű�־�ͷ������ڣ�������
						/* ���ű�־ */
						+ "','"
						+ DataVO.PAYFLAG
						// /*����ԭ��*/

						+ "','"
						// 20151103 xiejie3 н�ʷ��Ž�����Ӳ�����֯������,��ʾ���ý���ͬ����
						+ ICommonAlterName.FINANCEORG + "','" + ICommonAlterName.FINANCEDEPT + "','"
						+ ICommonAlterName.LIABILITYORG + "','" + ICommonAlterName.LIABILITYDEPT + "','"
						// end
						+ DataVO.TAXTYPE + "','" + DataVO.CACULATEFLAG + "','" + DataVO.CHECKFLAG + "','"
						+ DataVO.CYEAR + "','" + DataVO.CPERIOD + "','" + DataVO.PSNNAME + "') ) ");
		return (List<WaPaydataDspVO>) queryService.getBaseDao().retrieveByClause(WaPaydataDspVO.class,
				condtion.toString(), "displayseq");
	}

	/**
	 * ������ְ��н������ʾ����
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
						// 20151103 xiejie3 ����Ҫ�� ��ְ��н����������Ա����
						+ ICommonAlterName.PSNCODE
						+ "','"
						// end
						+ ICommonAlterName.CLERKCODE + "','"
						+ ICommonAlterName.PLSNAME
						+ "','"
						+ ICommonAlterName.ORGNAME
						+ "','"
						+ ICommonAlterName.DEPTNAME
						+ "','"
						+ "taxorgname" // (Combined by ssx for WITS on
										// 2019-04-15)
						+ "','"
						+ ICommonAlterName.POSTNAME
						+ "','"
						// 20151103 xiejie3 н�ʷ��Ž�����Ӳ�����֯������,��ʾ���ý���ͬ����
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

	// shenliangc 20140830 �ϲ���˰�����������ֻ��������ϲ�ѯ��������Ա���ݣ���Ҫ�������������
	@Override
	public void reCaculate(WaLoginContext loginContext, String whereCondition) throws BusinessException {
		// ����н�ʷ���(��������)
		if (!StringUtils.isBlank(loginContext.getPk_prnt_class())) {
			BDPKLockUtil.lockString(loginContext.getPk_prnt_class());
		}
		// �����ӷ���
		new SimpleDocLocker().lock(SimpleDocServiceTemplate.UPDATE, loginContext.getWaLoginVO());
		// У���ӷ����İ汾һ����
		// �汾У�飨ʱ���У�飩
		BDVersionValidationUtil.validateSuperVO(loginContext.getWaLoginVO());
		getService().reCaculate(loginContext, whereCondition);
	}

	@Override
	public PayfileVO[] getInPayLeavePsn(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll)
			throws BusinessException {
		return getService().getInPayLeavePsn(waLoginVO, whereCondition, isRangeAll);
	}

	/**
	 * 2014/05/23 shenliangcΪ�����ְ��н�ڵ���ʾ���öԻ�������Ŀ�����뱾�ڼ䷢����Ŀ���Ʋ�ͬ��������޸ġ�
	 */
	@Override
	public List<WaPaydataDspVO> queryPaydataDisplayInfo4Payleave(WaLoginContext context) throws BusinessException {
		// Map<String,Object> map = new HashMap<String,Object>();
		// IItemQueryService itemService = NCLocator.getInstance().lookup(
		// IItemQueryService.class);
		IClassItemQueryService citemService = NCLocator.getInstance().lookup(IClassItemQueryService.class);
		// ���ҷ�����ʹ�õ������й���н����Ŀ
		// WaItemVO[] itemArray = itemService.queryByWaClass(context
		// .getPk_wa_class());
		// ��ѯ��ǰн�����н���ڼ����е�н�ʷ�����Ŀ
		WaItemVO[] itemArray = citemService.queryAllClassItemInfosByPower(context);

		// boolean flag = false;
		// ���Ҹ�����ʾ����
		List<WaPaydataDspVO> dspList = this.queryPaydataPersonalDsp4payleave(context);
		if (dspList == null || dspList.isEmpty()) {
			// û�����ø�����ʾ���ã���ô��ͨ����ʾ
			dspList = this.queryPaydataCommonDsp4payleave(context);
		}
		if (dspList == null || dspList.isEmpty()) {
			// ��û�и�����ʾ���ã�Ҳû��ͨ����ʾ���ã��Ͱ�Ĭ����ʾ
			dspList = PaydataDspUtil.queryDefaultDsp4PayLeave();
			if (!ArrayUtils.isEmpty(itemArray)) {
				// ȡн����Ŀ�����õ���ʾ˳��
				WaClassItemVO[] classItemVOs = getUserShowClassItemVOs(context);
				Map<String, WaItemVO> itemMap = CommonUtils.toMap(WaItemVO.PK_WA_ITEM, itemArray);
				List<WaItemVO> itemList = new ArrayList<WaItemVO>();
				for (int i = 0, j = ArrayUtils.getLength(classItemVOs); i < j; i++) {
					WaItemVO itemVO = itemMap.get(classItemVOs[i].getPk_wa_item());
					if (itemVO == null)
						continue;
					itemList.add(itemVO);
				}
				// ת������н����Ŀ����ӵ���ʾ��Ŀ
				List<WaPaydataDspVO> waItemsDspList = PaydataDspUtil
						.convertWaItemVO(CollectionUtils.isEmpty(itemList) ? null : itemList.toArray(new WaItemVO[0]));
				dspList.addAll(waItemsDspList);
			}

		} else {
			// ����item_name
			PaydataDspUtil.setPaydataDisplayName4PayLeave(dspList, context);
			// ��������ʾ˳��֮�󣬷��������¼ӵ���Ŀ����Ҫ���
			PaydataDspUtil.addNewlyDsiplayItem(dspList, itemArray);
		}
		return dspList;
		/*
		 * if(dspList == null || dspList.isEmpty()){
		 * //��û�и�����ʾ���ã�Ҳû��ͨ����ʾ���ã��Ͱ�Ĭ����ʾ dspList =
		 * PaydataDspUtil.queryDefaultDsp4PayLeave(); flag = true; }else{
		 * //����item_name PaydataDspUtil.setPaydataDisplayName4PayLeave(dspList);
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

	// shenliangc 20140826 ʱ��н�ʼ��㱣��֮������������Ӧ��Ա�ķ������ݼ����־
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

	// 20151031 shenliangc н�ʷ�����Ŀ�޸�������Դ�����ͬʱ���·�����ĿȨ�����ݡ�
	// �ֹ����롪�����ֹ����룬���ֹ����롪�������ֹ����룬��ĿȨ�����ݱ��ֲ��䣻
	// ���ֹ����롪�����ֹ����룬�ֹ����롪�������ֹ����룬����޸ĺ�Ϊ�ֹ����룬��ɱ༭Ȩ����ΪY���������ΪN��
	// ���ֹ����롪�����ֹ����룬��Ҫ��wa_data�е���ϸ���ݸ���Ϊ��ʼֵ��
	public void clearPaydataByClassitem(WaClassItemVO vo) throws BusinessException {
		this.getService().clearPaydataByClassitem(vo);
	}
}
