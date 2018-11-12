package nc.impl.om.hrdept;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import nc.bs.bd.baseservice.DefaultGetBizInfoByMDUtil;
import nc.bs.bd.baseservice.IBaseServiceConst;
import nc.bs.bd.cache.CacheProxy;
import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.businessevent.bd.BDCommonEventUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.om.pub.AggBDRefenrenceChecker;
import nc.bs.om.pub.AggBDTreeUpdateLoopValidator;
import nc.bs.om.pub.AggBDUniqueRuleValidate;
import nc.bs.om.pub.JFQueryOrgLogUtils;
import nc.bs.om.pub.TreeBaseServiceAggVOAdapter;
import nc.bs.uap.oid.OidGenerator;
import nc.bs.uif2.BusinessExceptionAdapter;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.bs.uif2.validation.Validator;
import nc.event.om.IOMEventType;
import nc.hr.frame.persistence.DoNotLock;
import nc.hr.frame.persistence.ILocker;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.TimerLogger;
import nc.itf.bd.defdoc.IDefdocQryService;
import nc.itf.hi.IPersonRecordService;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.IPsndocService;
import nc.itf.hr.frame.IHrBillCode;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hr.infoset.IInfoSetQry;
import nc.itf.hr.managescope.IManagescopeFacade;
import nc.itf.hr.managescope.ManagescopeFacade;
import nc.itf.om.IAOSQueryService;
import nc.itf.om.IDeptManageService;
import nc.itf.om.IDeptQueryService;
import nc.itf.om.INaviQueryService;
import nc.itf.om.IOMCommonQueryService;
import nc.itf.om.IPostManageService;
import nc.itf.om.IPostQueryService;
import nc.itf.org.IOrgBillCodeConst;
import nc.itf.org.IOrgConst;
import nc.itf.org.IOrgEnumConst;
import nc.itf.org.IOrgUnitManageService;
import nc.itf.org.IOrgVersionConst;
import nc.itf.org.IReportOrgManageService;
import nc.itf.trn.TrnDelegator;
import nc.itf.trn.rds.IRdsManageService;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.jdbc.framework.generator.SequenceGenerator;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.listener.hi.psndoc.HiOrgBaseEventListener;
import nc.md.model.impl.MDEnum;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.pub.billcode.vo.BillCodeContext;
import nc.pub.tools.VOUtils;
import nc.pubitf.eaa.InnerCodeUtil;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.bd.ref.RefInfoVO;
import nc.vo.cache.CacheManager;
import nc.vo.cache.ICache;
import nc.vo.cache.config.CacheConfig;
import nc.vo.cache.config.CacheConfig.CacheType;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.enumeration.TrnseventEnum;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hr.utils.md.CrossOrgCopyFilter;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.DeptCancelInfoVO;
import nc.vo.om.hrdept.DeptChangeType;
import nc.vo.om.hrdept.DeptCopyAdjustVO;
import nc.vo.om.hrdept.DeptCopyConfirmVO;
import nc.vo.om.hrdept.DeptCopyValidateVO;
import nc.vo.om.hrdept.DeptCopyWrapperVO;
import nc.vo.om.hrdept.DeptHistoryVO;
import nc.vo.om.hrdept.DeptManager;
import nc.vo.om.hrdept.DeptMergeInfoVO;
import nc.vo.om.hrdept.DeptMergeWrapperVO;
import nc.vo.om.hrdept.DeptRenameInfoVO;
import nc.vo.om.hrdept.DeptTransDeptVO;
import nc.vo.om.hrdept.DeptTransItemVO;
import nc.vo.om.hrdept.DeptTransPostVO;
import nc.vo.om.hrdept.DeptTransRefInfVO;
import nc.vo.om.hrdept.DeptTransRefNameVO;
import nc.vo.om.hrdept.DeptTransRuleVO;
import nc.vo.om.hrdept.DeptTransValidateVO;
import nc.vo.om.hrdept.HRDeptStruMemberVersionVO;
import nc.vo.om.hrdept.HRDeptStruVersionVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.om.hrdept.HRDeptVersionVO;
import nc.vo.om.hrdept.PersonAdjustVO;
import nc.vo.om.hrdept.PostAdjustVO;
import nc.vo.om.job.JobVO;
import nc.vo.om.orginfo.HRAdminOrgVO;
import nc.vo.om.orginfo.HROrgManagerVO;
import nc.vo.om.orginfo.HROrgVO;
import nc.vo.om.post.AggPostVO;
import nc.vo.om.post.PostVO;
import nc.vo.om.pub.AggVOHelper;
import nc.vo.om.pub.IMetaDataIDConst;
import nc.vo.om.pub.JFCommonValue;
import nc.vo.om.pub.MultiLangTextBuffer;
import nc.vo.om.pub.SuperVOHelper;
import nc.vo.org.AdminOrgVO;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgManagerVO;
import nc.vo.org.OrgVO;
import nc.vo.org.ReportOrgVO;
import nc.vo.org.orgmodel.OrgTypeVO;
import nc.vo.org.util.DeptTypeServiceAdapterBasic;
import nc.vo.org.util.OrgTypeManager;
import nc.vo.org.util.OrgTypeServiceAdapterBasic;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.Calendars;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.sm.UserVO;
import nc.vo.trn.pub.TRNConst;
import nc.vo.trn.transitem.TrnTransItemVO;
import nc.vo.uif2.LoginContext;
import nc.vo.util.AuditInfoUtil;
import nc.vo.util.BDPKLockUtil;
import nc.vo.util.BDUniqueRuleValidate;
import nc.vo.util.BDVersionValidationUtil;
import nc.vo.util.bizlock.BizlockDataUtil;
import nc.vo.vorg.OrgVersionVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 部门服务实现<br>
 * 
 * @author zhangdd
 */
@SuppressWarnings("deprecation")
public class DeptServiceImpl extends TreeBaseServiceAggVOAdapter<AggHRDeptVO> implements IDeptManageService,
		IDeptQueryService {

	/** 部门DAO */
	private DeptDao deptDao = null;

	private final String docName = IMetaDataIDConst.DEPT;

	private BillCodeContext billCodeContext;

	/**
	 * 提交编码
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	private void commitBillCode(HRDeptVO vo) throws BusinessException {
		// 如果是自动生成编码，则提交
		if (!isAutoGenerateBillCode(vo)) {
			return;
		}
		NCLocator.getInstance().lookup(IHrBillCode.class)
				.commitPreBillCodes(JFCommonValue.NBCR_DEPT, vo.getPk_group(), vo.getPk_org(), vo.getCode());
	}

	public boolean isAutoGenerateBillCode(HRDeptVO vo) {
		return getBillCodeContext(vo) != null;
	}

	private BillCodeContext getBillCodeContext(HRDeptVO vo) {
		if (billCodeContext == null) {
			try {
				billCodeContext = NCLocator.getInstance().lookup(IBillcodeManage.class)
						.getBillCodeContext(JFCommonValue.NBCR_DEPT, vo.getPk_group(), vo.getPk_org());
			} catch (BusinessException e) {
				// Logger.error(e.getMessage(), e);
			}
		}
		return billCodeContext;
	}

	private void returnBillCodeOnDelete(HRDeptVO vo) throws BusinessException {
		// 如果是自动生成编码，则回退
		if (!isAutoGenerateBillCode(vo)) {
			return;
		}
		NCLocator.getInstance().lookup(IBillcodeManage.class)
				.returnBillCodeOnDelete(JFCommonValue.NBCR_DEPT, vo.getPk_group(), vo.getPk_org(), vo.getCode(), vo);
	}

	public DeptServiceImpl() {
		super(IMetaDataIDConst.DEPT);
	}

	private DeptDao getDeptDao() {
		if (deptDao == null) {
			deptDao = new DeptDao();
		}
		return deptDao;
	}

	@Override
	protected Validator[] getInsertValidator() {
		return new Validator[] { new AggBDUniqueRuleValidate(), new AggBDTreeUpdateLoopValidator(),
				new DeptInsertValidator() };
	}

	@Override
	protected Validator[] getUpdateValidator(AggHRDeptVO oldVO) {
		return new Validator[] { new AggBDUniqueRuleValidate(), new AggBDTreeUpdateLoopValidator(),
				new DeptUpdateValidator() };
	}

	@Override
	protected Validator[] getDeleteValidator() {
		return new Validator[] {
				new AggBDRefenrenceChecker(new String[] { "org_dept_v", "om_depthistory", "hr_relation_dept" }),
				new DeptDeleteValidator() };
	}

	@Override
	public AggHRDeptVO[] queryByCondition(LoginContext context, String condition) throws BusinessException {
		// 默认过滤封存部门，不显示封存部门
		// 查询时按照displayorder排序
		AggHRDeptVO[] aggVOs = queryByConditionWithoutVisibleWithOrder(condition, HRDeptVO.DISPLAYORDER + ","
				+ HRDeptVO.CODE, new String[] { "depthistory.effectdate" });

		// 对部门变更历史进行处理
		handleDeptHistory(aggVOs);
		return aggVOs;
	}

	private void getSystemTime(String str) {
		Date nowTime = new Date(System.currentTimeMillis());
		SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
		String retStrFormatNowDate = sdFormatter.format(nowTime);

		Logger.error(str + retStrFormatNowDate);

	}

	/**
	 * *************************************************************************
	 * * 默认选中根组织查询需要的代码，前台查询数据缓存不能超过200，直接调用后台也会清空缓存表{@inheritDoc}<br>
	 * Created on 2014-3-28 16:05:26<br>
	 * 
	 * @see nc.bs.om.pub.TreeBaseServiceAggVOAdapter#queryByCondition(nc.vo.uif2.LoginContext,
	 *      java.lang.String, java.lang.String)
	 * @author wangxbd
	 *************************************************************************** */
	@Override
	public AggHRDeptVO[] queryByCondition(LoginContext context, String condition, String typePk)
			throws BusinessException {
		// 如果condition为空，查当前人力资源组织下属业务单元下所有部门
		if (StringUtils.isEmpty(typePk)) {
			INaviQueryService s = NCLocator.getInstance().lookup(INaviQueryService.class);
			Object[] treeObj = s.queryAOSMembersByHROrgPK(context.getPk_org(), false, false, false, null, null, false);
			ArrayList<Object> returnList = new ArrayList<Object>();

			for (int i = 0; treeObj != null && i < treeObj.length; i++) {
				if (treeObj[i] instanceof OrgVO) {
					returnList.add(treeObj[i]);
				}
			}
			ArrayList<String> leftPKs = new ArrayList<String>();
			for (Object Obj : treeObj) {
				SuperVO vo = (SuperVO) Obj;
				leftPKs.add(vo.getPrimaryKey());
			}
			String orgSql = NCLocator.getInstance().lookup(IPostQueryService.class)
					.getInSQL(leftPKs.toArray(new String[0]));
			if (StringUtils.isEmpty(condition)) {
				condition = HRDeptVO.PK_ORG + " in ( " + orgSql + " ) ";
			} else {
				condition += " and " + HRDeptVO.PK_ORG + " in ( " + orgSql + " ) ";
			}
		}
		// 默认过滤封存部门，不显示封存部门
		// 查询时按照displayorder排序
		AggHRDeptVO[] aggVOs = queryByConditionWithoutVisibleWithOrder(condition, HRDeptVO.DISPLAYORDER + ","
				+ HRDeptVO.CODE, new String[] { "depthistory.effectdate" });

		// 对部门变更历史进行处理
		handleDeptHistory(aggVOs);

		if (ArrayUtils.isEmpty(aggVOs)) {
			return aggVOs;
		}

		for (AggHRDeptVO aggVO : aggVOs) {
			HRDeptVO mainVO = (HRDeptVO) aggVO.getParentVO();
			if (HRDeptVO.DEFAULT_DISPLAY_ORDER.equals(mainVO.getDisplayorder())) {
				mainVO.setDisplayorder(null);
			}
		}

		return aggVOs;
	}

	@Override
	public AggHRDeptVO queryByPk(String pk) throws BusinessException {
		AggHRDeptVO deptVO = super.queryByPk(pk);
		// 对部门变更历史进行处理
		handleDeptHistory(deptVO);
		return deptVO;
	}

	@Override
	public AggHRDeptVO[] queryByPks(String[] pks) throws BusinessException {
		AggHRDeptVO[] aggVOs = super.queryByPks(pks);

		// 对部门变更历史进行处理
		handleDeptHistory(aggVOs);
		return aggVOs;
	}

	@Override
	public HRDeptVersionVO[] queryFatherDeptVersionVOs(String[] pk_vids) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_vids)) {
			return new HRDeptVersionVO[0];
		}

		Object[] obj = getMDQueryService().queryBillOfVOByPKsWithOrder(HRDeptVersionVO.class, pk_vids, false);
		if (ArrayUtils.isEmpty(obj)) {
			return new HRDeptVersionVO[0];
		}

		HRDeptVersionVO[] versionVOs = SuperVOHelper.convertObjectArray(obj, HRDeptVersionVO.class);
		if (ArrayUtils.isEmpty(versionVOs)) {
			return new HRDeptVersionVO[0];
		}

		String[] pk_fatherorgs = SuperVOHelper.getAttributeValueArray(versionVOs, HRDeptVersionVO.PK_FATHERORG,
				String.class);

		obj = getMDQueryService().queryBillOfVOByPKsWithOrder(HRDeptVO.class, pk_fatherorgs, false);
		if (ArrayUtils.isEmpty(obj)) {
			return new HRDeptVersionVO[0];
		}
		HRDeptVO[] fatherDeptVOs = SuperVOHelper.convertObjectArray(obj, HRDeptVO.class);
		if (ArrayUtils.isEmpty(fatherDeptVOs)) {
			return new HRDeptVersionVO[0];
		}

		String[] father_vids = SuperVOHelper.getAttributeValueArray(fatherDeptVOs, HRDeptVO.PK_VID, String.class);
		if (ArrayUtils.isEmpty(father_vids)) {
			return new HRDeptVersionVO[0];
		}

		obj = getMDQueryService().queryBillOfVOByPKsWithOrder(HRDeptVersionVO.class, father_vids, false);
		if (ArrayUtils.isEmpty(obj)) {
			return new HRDeptVersionVO[0];
		}

		return SuperVOHelper.convertObjectArray(obj, HRDeptVersionVO.class);
	}

	/**
	 * 对部门的变更历史子表进行一些处理，要保证对数据库的更新、查询操作后执行此操作<br>
	 * 
	 * @param deptVOs
	 */
	private void handleDeptHistory(AggHRDeptVO... deptVOs) throws BusinessException {
		// 对部门变更历史子表进行处理
		if (ArrayUtils.isEmpty(deptVOs)) {
			return;
		}

		List<String> changenumList = new ArrayList<String>();
		for (int i = 0; i < deptVOs.length; i++) {
			DeptHistoryVO[] historyVOs = (DeptHistoryVO[]) deptVOs[i].getTableVO(AggHRDeptVO.HISTORY);
			if (ArrayUtils.isEmpty(historyVOs)) {
				continue;
			}

			for (DeptHistoryVO historyVO : historyVOs) {
				changenumList.add(historyVO.getChangenum());
			}
		}

		Map<String, DeptHistoryVO[]> historyVOMap = new HashMap<String, DeptHistoryVO[]>();
		if (!changenumList.isEmpty()) {
			InSQLCreator isc = new InSQLCreator();
			String insql = isc.getInSQL(changenumList.toArray(new String[0]));
			String where = DeptHistoryVO.CHANGENUM + " in (" + insql + ")";
			DeptHistoryVO[] relateVOs = getOMCommonQueryService().queryByCondition(DeptHistoryVO.class, where);

			if (!ArrayUtils.isEmpty(relateVOs)) {
				for (int i = 0; i < changenumList.size(); i++) {
					List<DeptHistoryVO> histroyVOList = new ArrayList<DeptHistoryVO>();
					for (int j = 0; j < relateVOs.length; j++) {
						// MOD (增加非空校验)
						// ssx added on 2018-05-29
						if (changenumList.get(i) != null) {
							//
							if (changenumList.get(i).equals(relateVOs[j].getChangenum())) {
								histroyVOList.add(relateVOs[j]);
							}
						}
					}
					historyVOMap.put(changenumList.get(i), histroyVOList.toArray(new DeptHistoryVO[0]));
				}
			}
		}

		for (AggHRDeptVO aggDeptVO : deptVOs) {
			DeptHistoryVO[] historyVOs = (DeptHistoryVO[]) aggDeptVO.getTableVO(AggHRDeptVO.HISTORY);
			if (ArrayUtils.isEmpty(historyVOs)) {
				continue;
			}

			for (DeptHistoryVO historyVO : historyVOs) {
				MultiLangTextBuffer multiLangText = new MultiLangTextBuffer();
				multiLangText.setText(" ");
				multiLangText.setText2(" ");
				multiLangText.setText3(" ");
				SuperVOHelper.copyMultiLangAttribute(multiLangText, DeptHistoryVO.MERGEDDEPT, historyVO);
				SuperVOHelper.copyMultiLangAttribute(multiLangText, DeptHistoryVO.RECEIVEDEPT, historyVO);

				// 查询与此变更历史VO相关联的其他变更历史VO
				DeptHistoryVO[] relateVOs = historyVOMap.get(historyVO.getChangenum());

				if (ArrayUtils.isEmpty(relateVOs)) {
					continue;
				}
				if (DeptChangeType.MERGE.equalsIgnoreCase(historyVO.getChangetype())) {// 如果是合并类型

					if (UFBoolean.TRUE.equals(historyVO.getIsreceived())) {// 如果是接收部门
																			// 设定接收部门和组织
						String[] srcAttrs = new String[] { DeptHistoryVO.NAME };
						String[] desAttrs = new String[] { DeptHistoryVO.RECEIVEDEPT };
						SuperVOHelper.copyMultiLangAttribute(historyVO, historyVO, srcAttrs, desAttrs);

						// 设定接收组织
						historyVO.setReceiveorg(historyVO.getPk_org());

						// 找到所有被合并的部门
						for (DeptHistoryVO relateVO : relateVOs) {
							if ((null == relateVO.getIsreceived() || UFBoolean.FALSE.equals(relateVO.getIsreceived()))
									&& DeptChangeType.MERGE.equals(relateVO.getChangetype())) {
								multiLangText.appendText("[").appendText(relateVO.getName()).appendText("] ");
								multiLangText.appendText2("[").appendText2(relateVO.getName2()).appendText2("] ");
								multiLangText.appendText3("[").appendText3(relateVO.getName3()).appendText3("] ");
							}
						}

						SuperVOHelper.copyMultiLangAttribute(multiLangText, DeptHistoryVO.MERGEDDEPT, historyVO);
					} else {// 如果是被合并部门
						multiLangText.appendText("[").appendText(historyVO.getName()).appendText("] ");
						multiLangText.appendText2("[").appendText2(historyVO.getName2()).appendText2("] ");
						multiLangText.appendText3("[").appendText3(historyVO.getName3()).appendText3("] ");
						SuperVOHelper.copyMultiLangAttribute(multiLangText, DeptHistoryVO.MERGEDDEPT, historyVO);

						// 找到接收部门
						for (DeptHistoryVO relateVO : relateVOs) {
							// 如果是被合并部门，则加入名称
							if (UFBoolean.TRUE.equals(relateVO.getIsreceived())) {
								String[] srcAttrs = new String[] { DeptHistoryVO.NAME };
								String[] desAttrs = new String[] { DeptHistoryVO.RECEIVEDEPT };
								SuperVOHelper.copyMultiLangAttribute(relateVO, historyVO, srcAttrs, desAttrs);
								break;
							}
						}
					}
				} else if (DeptChangeType.SHIFT.equalsIgnoreCase(historyVO.getChangetype())) {
					// 找到接收部门
					for (DeptHistoryVO relateVO : relateVOs) {
						// 如果单元内转移部门，则加入名称
						if (UFBoolean.TRUE.equals(relateVO.getIsreceived())) {
							String[] srcAttrs = new String[] { DeptHistoryVO.NAME };
							String[] desAttrs = new String[] { DeptHistoryVO.RECEIVEDEPT };
							SuperVOHelper.copyMultiLangAttribute(relateVO, historyVO, srcAttrs, desAttrs);
							break;
						}
					}
				} else if (DeptChangeType.OUTERSHIFT.equalsIgnoreCase(historyVO.getChangetype())) {
					// 找到接收部门
					for (DeptHistoryVO relateVO : relateVOs) {
						// 如果是跨单元转移部门，则加入名称
						if (UFBoolean.TRUE.equals(relateVO.getIsreceived())) {
							String[] srcAttrs = new String[] { DeptHistoryVO.NAME, DeptHistoryVO.PK_ORG };
							String[] desAttrs = new String[] { DeptHistoryVO.RECEIVEDEPT, DeptHistoryVO.RECEIVEDORG };
							SuperVOHelper.copyMultiLangAttribute(relateVO, historyVO, srcAttrs, desAttrs);
							break;
						}
					}
				}
			}
		}
	}

	// @Override
	public AggHRDeptVO[] queryChildDeptVOs(String pk_dept, String orderBy) throws BusinessException {
		AggHRDeptVO aggFatherDeptVO = queryByPk(pk_dept);
		HRDeptVO fatherDeptVO = (HRDeptVO) aggFatherDeptVO.getParentVO();
		String innerCode = fatherDeptVO.getInnercode();
		String condition = " innercode like '" + innerCode + "%' and innercode <> '" + innerCode + "'";

		/*
		 * String condition = " innercode like ? and innercode <> ?";
		 * SQLParameter parameter = new SQLParameter();
		 * parameter.addParam(innerCode + "%"); parameter.addParam(innerCode);
		 */

		return queryByCondition(null, condition, orderBy);
		/*
		 * @SuppressWarnings("unchecked") Collection<HRDeptVO> c =
		 * getDeptDao().getBaseDAO().retrieveByClause(HRDeptVO.class, condition,
		 * orderBy, parameter); if (null == c || c.isEmpty()) { return new
		 * HRDeptVO[0]; } return c.toArray(new HRDeptVO[c.size()]);
		 */
	}

	@Override
	public HRDeptVO[] queryDeptVOsByOrgPK(String pk_org, boolean includeCanceled) throws BusinessException {
		boolean isHROrg = getAOSQueryService().checkIfHROrgByPK(pk_org);
		if (isHROrg) {
			String innerCode = getAOSQueryService().queryInnerCodeByOrgPK(pk_org);
			AggHRDeptVO[] aggVOs = getDeptDao().retrieveDeptVOByAOSInnerCode(innerCode, includeCanceled);
			return (HRDeptVO[]) AggVOHelper.getParentVOArrayFromAggVOs(aggVOs, HRDeptVO.class);
		}

		String condition = HRDeptVO.PK_ORG + " = '" + pk_org + "' and " + HRDeptVO.ENABLESTATE + " = "
				+ IPubEnumConst.ENABLESTATE_ENABLE;
		if (!includeCanceled) {
			condition += " and " + HRDeptVO.HRCANCELED + " = 'N'";
		}
		AggHRDeptVO[] aggVOs = queryByConditionWithoutVisible(condition, null);
		return (HRDeptVO[]) AggVOHelper.getParentVOArrayFromAggVOs(aggVOs, HRDeptVO.class);
	}

	/**
	 * 查询当前行政组织下的所有部门（不包含下级）
	 * 
	 * @param pk_org
	 * @param includeCanceled
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public HRDeptVO[] queryDeptVOsByCurOrgPK(String pk_org, boolean includeCanceled) throws BusinessException {
		AggHRDeptVO[] aggVOs = getDeptDao().retrieveDeptVOByCurrAOSInnerCode(pk_org, includeCanceled);
		return (HRDeptVO[]) AggVOHelper.getParentVOArrayFromAggVOs(aggVOs, HRDeptVO.class);
	}

	/**
	 * 根据条件查询某个HR组织所管理范围的部门——heqiaoa 2014-7-28
	 * 
	 * @param pk_org
	 * @param includeCanceled
	 * @return HRDeptVO[]
	 * @throws BusinessException
	 */
	public AggHRDeptVO[] queryDeptInfoByOrgAdminPKandCondition(String pk_orgadmin, boolean includeCanceled,
			String wherePart) throws BusinessException {
		AggHRDeptVO[] rets = getDeptDao()
				.queryDeptInfoByOrgAdminPKandCondition(pk_orgadmin, includeCanceled, wherePart);
		return rets;
	}

	@Override
	public boolean checkIfDeptExistByOrgPK(String pk_org) throws BusinessException {
		int count = getDeptDao().retrieveDeptCountByOrgPK(pk_org);
		return count > 0;
	}

	private OrgVO initOrgVO(HRDeptVO deptVO) {
		OrgVO orgVO = null;
		if (!StringUtil.isEmptyWithTrim(deptVO.getPk_dept())) {
			try {
				orgVO = getOMCommonQueryService().queryByPK(OrgVO.class, deptVO.getPk_dept());
			} catch (BusinessException e) {
				throw new BusinessExceptionAdapter(e);
			}
		}
		UFDateTime ts = null;

		if (orgVO == null) {
			orgVO = new OrgVO();
		} else {
			// 更新时要记录下数据库的TS
			ts = orgVO.getTs();
		}

		SuperVOHelper.copySuperVOAttributes(deptVO, orgVO);
		if (ts != null) {
			orgVO.setTs((UFDateTime) ts.clone());
		}
		ts = null;
		// 设置基本属性
		orgVO.setPk_org(deptVO.getPk_dept());
		// 部门的主组织为业务单元，为了方便将业务单元+部门进行树型数据构建(功能权限分配等处)，将部门的所属组织、所属上级部门分别存在业务单元的所属公司、所属上级业务单元中
		orgVO.setAttributeValue(OrgVO.PK_CORP, deptVO.getAttributeValue(DeptVO.PK_ORG));
		orgVO.setAttributeValue(OrgVO.PK_FATHERORG, deptVO.getAttributeValue(DeptVO.PK_FATHERORG));
		// 设置对应类型
		OrgTypeVO orgtypevo = OrgTypeManager.getInstance().getOrgTypeByID(IOrgConst.DEPTORGTYPE);
		orgVO.setAttributeValue(orgtypevo.getFieldname(), UFBoolean.TRUE);

		// 设置启用标记
		orgVO.setEnablestate(deptVO.getEnablestate());

		// 设置是否业务单元数据标记
		orgVO.setIsbusinessunit(UFBoolean.FALSE);

		orgVO.setPk_ownorg(deptVO.getPk_org());

		// 新增部门、账簿、结构等非业务单元类型数据同步至组织表时，版本主键为空，需新生成VID值
		if (StringUtil.isEmpty(orgVO.getPk_vid())) {
			if (StringUtil.isEmpty((String) deptVO.getAttributeValue("pk_vid"))) {
				orgVO.setPk_vid(OidGenerator.getInstance().nextOid());
			} else {
				orgVO.setPk_vid((String) deptVO.getAttributeValue("pk_vid"));
			}
		}

		// 部门类型，其业务单元的报表职能默认选中
		orgVO.setAttributeValue(OrgTypeManager.getInstance().getOrgTypeByID(IOrgConst.REPORTORGTYPE).getFieldname(),
				UFBoolean.TRUE);

		return orgVO;
	}

	/**
	 * 提取为导入工具使用
	 * 
	 * @param deptVO
	 * @return
	 */
	private OrgVO initImportToOrgVO(HRDeptVO deptVO) {
		OrgVO orgVO = new OrgVO();

		SuperVOHelper.copySuperVOAttributes(deptVO, orgVO);

		// 设置基本属性
		orgVO.setPk_org(deptVO.getPk_dept());
		// 部门的主组织为业务单元，为了方便将业务单元+部门进行树型数据构建(功能权限分配等处)，将部门的所属组织、所属上级部门分别存在业务单元的所属公司、所属上级业务单元中
		orgVO.setAttributeValue(OrgVO.PK_CORP, deptVO.getAttributeValue(DeptVO.PK_ORG));
		orgVO.setAttributeValue(OrgVO.PK_FATHERORG, deptVO.getAttributeValue(DeptVO.PK_FATHERORG));
		// 设置对应类型
		OrgTypeVO orgtypevo = OrgTypeManager.getInstance().getOrgTypeByID(IOrgConst.DEPTORGTYPE);
		orgVO.setAttributeValue(orgtypevo.getFieldname(), UFBoolean.TRUE);

		// 设置启用标记
		orgVO.setEnablestate(deptVO.getEnablestate());

		// 设置是否业务单元数据标记
		orgVO.setIsbusinessunit(UFBoolean.FALSE);

		orgVO.setPk_ownorg(deptVO.getPk_org());

		// 新增部门、账簿、结构等非业务单元类型数据同步至组织表时，版本主键为空，需新生成VID值
		if (StringUtil.isEmpty(orgVO.getPk_vid())) {
			if (StringUtil.isEmpty((String) deptVO.getAttributeValue("pk_vid"))) {
				orgVO.setPk_vid(OidGenerator.getInstance().nextOid());
			} else {
				orgVO.setPk_vid((String) deptVO.getAttributeValue("pk_vid"));
			}
		}

		// 部门类型，其业务单元的报表职能默认选中
		orgVO.setAttributeValue(OrgTypeManager.getInstance().getOrgTypeByID(IOrgConst.REPORTORGTYPE).getFieldname(),
				UFBoolean.TRUE);

		return orgVO;
	}

	@Override
	protected void beforeInsert(AggHRDeptVO aggVO) throws BusinessException {

		// 新增部门版本
		// 新增时即生成初始版本信息，版本号为当前年份+01，生效日期即为业务单元的启用日期
		HRDeptVO vo = (HRDeptVO) aggVO.getParentVO();
		vo.setVname(ResHelper.getString("6005dept", "06005dept0346")/*
																	 * @res
																	 * "初始版本"
																	 */);
		UFDate time = PubEnv.getServerDate();
		vo.setVno(time.getYear() + "01");
		vo.setPk_vid(OidGenerator.getInstance().nextOid());
		vo.setVstartdate(time);
		vo.setOrgtype13(UFBoolean.TRUE);
		// 是否预算组织 zhangqiano 2015-11-21
		// vo.setOrgtype17(UFBoolean.TRUE);
		vo.setVenddate(new UFDate("9999-12-31", false));
		vo.setIslastversion(UFBoolean.TRUE);
		if (vo.getDisplayorder() == null) {
			vo.setDisplayorder(HRDeptVO.DEFAULT_DISPLAY_ORDER);
		} else if (vo.getDisplayorder() > HRDeptVO.DEFAULT_DISPLAY_ORDER || vo.getDisplayorder() < 0) {
			vo.setDisplayorder(HRDeptVO.DEFAULT_DISPLAY_ORDER);
		}

		setDeptCode(vo);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void afterInsert(AggHRDeptVO oldAggVO, AggHRDeptVO newAggVO) throws BusinessException {
		HRDeptVO oldVO = (HRDeptVO) oldAggVO.getParentVO();
		HRDeptVO newVO = (HRDeptVO) newAggVO.getParentVO();
		commitBillCode(newVO);

		// 处理部门变更历史
		DeptHistoryVO historyVO = SuperVOHelper.createSuperVOFromSuperVO(newVO, DeptHistoryVO.class);
		historyVO.setChangenum(OidGenerator.getInstance().nextOid());
		historyVO.setChangetype(DeptChangeType.ESTABLISH);
		historyVO.setIsreceived(UFBoolean.FALSE);
		historyVO.setApprovenum(oldVO.getApprovenum());
		historyVO.setApprovedept(oldVO.getApprovedept());
		UFLiteralDate effectDate = newVO.getCreatedate();
		if (effectDate == null) {
			effectDate = new UFLiteralDate(new Date());
		}
		historyVO.setEffectdate(effectDate);

		String conOrg = OrgVersionVO.PK_ORG + " = '" + newVO.getPk_org() + "' order by " + OrgVersionVO.VNO + " desc";
		OrgVersionVO[] orgVersionVOs = getOMCommonQueryService().queryByCondition(OrgVersionVO.class, conOrg);
		if (!ArrayUtils.isEmpty(orgVersionVOs)) {
			historyVO.setPk_org_v(orgVersionVOs[0].getPk_vid());
		}

		getDeptDao().getBaseDAO().insertVO(historyVO);

		// 更新子表，用于界面显示
		// newVO.setDepthistory(new DeptHistoryVO[] { historyVO });
		newAggVO.setTableVO(AggHRDeptVO.HISTORY, new DeptHistoryVO[] { historyVO });
		// 部门版本信息
		HRDeptVersionVO deptVersionVO = SuperVOHelper.createSuperVOFromSuperVO(newVO, HRDeptVersionVO.class);
		BDPKLockUtil.lockSuperVO(deptVersionVO);
		HRDeptVersionVO oldDeptVersionVO = getOMCommonQueryService().queryByPK(HRDeptVersionVO.class,
				deptVersionVO.getPk_dept());
		if (oldDeptVersionVO == null) {
			// 数据库操作
			getDeptDao().getBaseDAO().insertVOWithPK(deptVersionVO);
			// 通知缓存
			CacheProxy.fireDataInserted(new DefaultGetBizInfoByMDUtil().getTableName(deptVersionVO), null);
		} else {
			// 数据库操作
			getDeptDao().getBaseDAO().updateVO(deptVersionVO);
			// 通知缓存
			CacheProxy.fireDataUpdated(new DefaultGetBizInfoByMDUtil().getTableName(deptVersionVO), null);
		}

		// 新增时部门主管都没有PK_ORG,因为部门主管的pk_org为该部门主键，所以需要在插入部门信息后重新更新 zhangqiano
		// 2015-11-21
		OrgManagerVO[] managers = (OrgManagerVO[]) newAggVO.getTableVO(AggHRDeptVO.MANAGER);
		if (managers != null && managers.length > 0) {
			for (OrgManagerVO manager : managers) {
				manager.setPk_org(newVO.getPk_dept());
			}
			getDeptDao().getBaseDAO().updateVOArray(managers);
		}

		if (!StringUtil.isEmptyWithTrim(newVO.getPrincipal())) {
			StringBuilder condition = new StringBuilder();
			condition.append(DeptManager.PK_DEPT + " = '" + newVO.getPk_dept() + "'");
			DeptManager[] deptManagers = getOMCommonQueryService().queryByCondition(DeptManager.class,
					condition.toString());
			DeptManager dupManagerVO = SuperVOHelper.getSuperVOByValue(deptManagers, DeptManager.PK_PSNDOC,
					newVO.getPrincipal());
			if (dupManagerVO == null) {
				// 查询负责人对应的系统用户,如没有关联用户则不同步数据至组织主管
				insertPrincipal(newVO);
			} else {
				dupManagerVO.setPrincipalflag(UFBoolean.TRUE);
				BDPKLockUtil.lockSuperVO(dupManagerVO);// 乐观锁

				getDeptDao().getBaseDAO().updateVO(dupManagerVO);// 数据库操作
			}

		}

		// 同步新增OrgVO
		OrgVO orgVO = initOrgVO(newVO);

		orgVO = getOrgUnitManageService().insertVO(orgVO);

		// 向版本表插入OrgVO初始版本信息
		OrgVersionVO newOrgVersionVO = SuperVOHelper.createSuperVOFromSuperVO(orgVO, OrgVersionVO.class);
		BDPKLockUtil.lockSuperVO(newOrgVersionVO);// 乐观锁
		OrgVersionVO orgVersionVO = getOMCommonQueryService()
				.queryByPK(OrgVersionVO.class, newOrgVersionVO.getPk_vid());
		if (orgVersionVO == null) {
			// 插入新版本
			getDeptDao().getBaseDAO().insertVOWithPK(newOrgVersionVO);// 数据库操作
		} else {
			// 更新
			getDeptDao().getBaseDAO().updateVO(newOrgVersionVO);// 数据库操作
		}

		// 新增时，要同步生成报表组织信息
		DeptVO deptVO = new DeptVO();
		SuperVOHelper.copySuperVOAttributes(newVO, deptVO);
		ReportOrgVO reportOrgVO = new ReportOrgVO();
		SuperVOHelper.copySuperVOAttributes(newVO, deptVO);

		reportOrgVO.setEnablestate(IPubEnumConst.ENABLESTATE_INIT);
		reportOrgVO.setSourceorgtype(IOrgEnumConst.ENTITYORGTYPE_DEPT);
		reportOrgVO.setStatus(VOStatus.NEW);
		new DeptTypeServiceAdapterBasic().execEachDeptInsertOperate(deptVO, reportOrgVO);

		// 同步生成预算组织信息 heqiaoa
		// PlanBudgetVO planBudgetVO = new PlanBudgetVO();
		// planBudgetVO.setEnablestate(IPubEnumConst.ENABLESTATE_INIT);
		// planBudgetVO.setSourceorgtype(IOrgEnumConst.ENTITYORGTYPE_DEPT);
		// planBudgetVO.setStatus(VOStatus.NEW);
		// new DeptTypeServiceAdapterBasic().execEachDeptInsertOperate(deptVO,
		// planBudgetVO);
	}

	/**
	 * 为了导入工具单独提取方法
	 * 
	 * @param oldAggVO
	 * @param newAggVO
	 * @throws BusinessException
	 */
	protected void afterInsertImportVO(AggHRDeptVO oldAggVO, AggHRDeptVO newAggVO) throws BusinessException {
		// HRDeptVO oldVO = (HRDeptVO)oldAggVO.getParentVO();
		HRDeptVO newVO = (HRDeptVO) newAggVO.getParentVO();
		// 因为导入工具里面部门编码已经存在，这个地方不需要在生产了。
		// commitBillCode(newVO);

		// 处理部门变更历史
		DeptHistoryVO historyVO = SuperVOHelper.createSuperVOFromSuperVO(newVO, DeptHistoryVO.class);
		historyVO.setChangenum(OidGenerator.getInstance().nextOid());
		historyVO.setChangetype(DeptChangeType.ESTABLISH);
		historyVO.setIsreceived(UFBoolean.FALSE);
		UFLiteralDate effectDate = newVO.getCreatedate();
		if (effectDate == null) {
			effectDate = new UFLiteralDate(new Date());
		}
		historyVO.setEffectdate(effectDate);

		OrgVersionVO orgVersVO = getOrgVersionVO(newVO.getPk_group(), newVO.getPk_org());
		if (orgVersVO != null) {
			historyVO.setPk_org_v(orgVersVO.getPk_vid());
		}

		getDeptDao().getBaseDAO().insertVO(historyVO);

		// 更新子表，用于界面显示
		// newVO.setDepthistory(new DeptHistoryVO[] { historyVO });
		newAggVO.setTableVO(AggHRDeptVO.HISTORY, new DeptHistoryVO[] { historyVO });
		// 部门版本信息
		HRDeptVersionVO deptVersionVO = SuperVOHelper.createSuperVOFromSuperVO(newVO, HRDeptVersionVO.class);
		// 为了效率把锁表的代码注释掉，导入工具可以不锁表
		// BDPKLockUtil.lockSuperVO(deptVersionVO);
		// 因为第一次插入，不可能有部门版本历史
		// HRDeptVersionVO oldDeptVersionVO =
		// getOMCommonQueryService().queryByPK(HRDeptVersionVO.class,
		// deptVersionVO.getPk_dept());
		// if (oldDeptVersionVO == null)
		// {
		// 数据库操作
		getDeptDao().getBaseDAO().insertVOWithPK(deptVersionVO);
		// 通知缓存
		// 为了导入工具效率去掉
		// CacheProxy.fireDataInserted(new
		// DefaultGetBizInfoByMDUtil().getTableName(deptVersionVO), null);
		// }
		// else
		// {
		// // 数据库操作
		// getDeptDao().getBaseDAO().updateVO(deptVersionVO);
		// // 通知缓存
		// // 为了导入工具效率去掉
		// // CacheProxy.fireDataUpdated(new
		// DefaultGetBizInfoByMDUtil().getTableName(deptVersionVO), null);
		// }
		//
		if (!StringUtil.isEmptyWithTrim(newVO.getPrincipal())) {
			StringBuilder condition = new StringBuilder();
			condition.append(DeptManager.PK_DEPT + " = '" + newVO.getPk_dept() + "'");
			DeptManager[] deptManagers = getOMCommonQueryService().queryByCondition(DeptManager.class,
					condition.toString());
			DeptManager dupManagerVO = SuperVOHelper.getSuperVOByValue(deptManagers, DeptManager.PK_PSNDOC,
					newVO.getPrincipal());
			if (dupManagerVO == null) {
				// 查询负责人对应的系统用户,如没有关联用户则不同步数据至组织主管
				insertPrincipal(newVO);
			} else {
				dupManagerVO.setPrincipalflag(UFBoolean.TRUE);
				// 为了导入工具效率，注释掉
				// BDPKLockUtil.lockSuperVO(dupManagerVO);// 乐观锁
				getDeptDao().getBaseDAO().updateVO(dupManagerVO);// 数据库操作
			}

		}

		// 同步新增OrgVO
		// OrgVO orgVO = initOrgVO(newVO);
		OrgVO orgVO = initImportToOrgVO(newVO);
		orgVO = getOrgUnitManageService().insertVO(orgVO);

		// 向版本表插入OrgVO初始版本信息
		OrgVersionVO newOrgVersionVO = SuperVOHelper.createSuperVOFromSuperVO(orgVO, OrgVersionVO.class);
		// 为了导入工具效率，注释点
		// BDPKLockUtil.lockSuperVO(newOrgVersionVO);// 乐观锁
		// 因为第一次插入，不可能有部门版本历史
		// OrgVersionVO orgVersionVO =
		// getOMCommonQueryService().queryByPK(OrgVersionVO.class,
		// newOrgVersionVO.getPk_vid());
		// if (orgVersionVO == null)
		// {
		// 插入新版本
		getDeptDao().getBaseDAO().insertVOWithPK(newOrgVersionVO);// 数据库操作
		// }
		// else
		// {
		// // 更新
		// getDeptDao().getBaseDAO().updateVO(newOrgVersionVO);// 数据库操作
		// }
		// 新增时，要同步生成报表组织信息
		DeptVO deptVO = new DeptVO();
		SuperVOHelper.copySuperVOAttributes(newVO, deptVO);
		ReportOrgVO reportOrgVO = new ReportOrgVO();
		SuperVOHelper.copySuperVOAttributes(newVO, deptVO);

		reportOrgVO.setEnablestate(IPubEnumConst.ENABLESTATE_INIT);
		reportOrgVO.setSourceorgtype(IOrgEnumConst.ENTITYORGTYPE_DEPT);
		reportOrgVO.setStatus(VOStatus.NEW);
		new DeptTypeServiceAdapterBasic().execEachDeptInsertOperate(deptVO, reportOrgVO);
	}

	// 把组织版本缓存起来
	private OrgVersionVO getOrgVersionVO(String pk_group, String pk_org) throws BusinessException {
		CacheConfig config = new CacheConfig();
		config.setCacheType(CacheType.MEMORY);
		config.setFlushInterval(3600000);// 1小时刷新一次
		config.setRegionName(PubEnv.getDataSource() + "_" + DeptServiceImpl.class.getName() + "_OrgVersionVO");
		ICache cache = CacheManager.getInstance().getCache(config);

		if (cache.getKeys().isEmpty()) {
			String conOrg = OrgVersionVO.PK_ORG + " = '" + pk_org + "' order by " + OrgVersionVO.VNO + " desc";
			OrgVersionVO[] orgVersionVOs = getOMCommonQueryService().queryByCondition(OrgVersionVO.class, conOrg);
			for (OrgVersionVO vo : orgVersionVOs) {
				if (cache.get(vo.getPk_org()) != null) {
					continue;
				}
				cache.put(vo.getPk_org(), vo);
			}
		}
		return (OrgVersionVO) cache.get(pk_org);
	}

	@Override
	protected void beforeUpdate(AggHRDeptVO aggVO) throws BusinessException {
		HRDeptVO vo = (HRDeptVO) aggVO.getParentVO();
		if (StringUtil.isEmptyWithTrim(vo.getPrincipal())) {
			// 在更新前首先同步部门负责人字段
			StringBuilder condition = new StringBuilder();
			condition.append(DeptManager.PK_DEPT + " = '" + vo.getPk_dept() + "'");
			condition.append(" and ");
			condition.append(DeptManager.PRINCIPALFLAG + " = 'Y'");
			// 查询组织主管里的部门负责人信息
			DeptManager[] managers = getOMCommonQueryService()
					.queryByCondition(DeptManager.class, condition.toString());

			// 如果为空，判断是否是要清除掉部门负责人数据
			if (!ArrayUtils.isEmpty(managers)) {// 如果找到记录
												// 删除数据库记录
				getDeptDao().getBaseDAO().deleteVOArray(managers);
			}
		} else {// 如该部门负责人字段不为空
			StringBuilder condition = new StringBuilder();
			condition.append(DeptManager.PK_DEPT + " = '" + vo.getPk_dept() + "'");
			condition.append(" and (");
			condition.append(DeptManager.PK_PSNDOC + " = '" + vo.getPrincipal() + "'");
			condition.append(" or ");
			condition.append(DeptManager.PRINCIPALFLAG + " = 'Y' )");

			// 查询组织主管里的部门负责人信息
			DeptManager[] managers = getOMCommonQueryService()
					.queryByCondition(DeptManager.class, condition.toString());

			if (ArrayUtils.isEmpty(managers)) {// 如果部门主管中没记录过部门负责人，插入一条记录
				insertPrincipal(vo);
			} else {
				// 判断原有的部门负责人和现在的是否一样，一样则不去更新
				// 如果两者不同
				getDeptDao().getBaseDAO().deleteVOArray(managers);
				insertPrincipal(vo);
			}
		}

		SuperVO[] deptManagerVOs = (SuperVO[]) aggVO.getAllChildrenVO();
		if (!ArrayUtils.isEmpty(deptManagerVOs)) {
			for (int i = 0; i < deptManagerVOs.length; i++) {
				if (deptManagerVOs[0] instanceof DeptManager) {
					DeptManager deptManagerVO = (DeptManager) deptManagerVOs[0];
					if (deptManagerVO.getStatus() == VOStatus.NEW) {
						deptManagerVO.setPk_org(vo.getPk_dept());
					}
				}
			}
		}

		UFLiteralDate createDate = vo.getCreatedate();
		if (createDate != null) {
			// 处理部门变更历史中"设立"记录，更新部门创建时间
			StringBuilder condition = new StringBuilder();
			condition.append(DeptHistoryVO.PK_DEPT + " = '" + vo.getPk_dept() + "' and ");
			condition.append(DeptHistoryVO.CHANGETYPE + " = '" + DeptChangeType.ESTABLISH + "'");
			DeptHistoryVO[] historyVOs = getOMCommonQueryService().queryByCondition(DeptHistoryVO.class,
					condition.toString());
			if (!ArrayUtils.isEmpty(historyVOs)) {
				DeptHistoryVO historyVO = historyVOs[0];
				historyVO.setEffectdate(createDate);
				getDeptDao().getBaseDAO().updateVO(historyVO);
			}
		}

		if (vo.getDisplayorder() == null) {
			vo.setDisplayorder(HRDeptVO.DEFAULT_DISPLAY_ORDER);
		} else if (vo.getDisplayorder() > HRDeptVO.DEFAULT_DISPLAY_ORDER || vo.getDisplayorder() < 0) {
			vo.setDisplayorder(HRDeptVO.DEFAULT_DISPLAY_ORDER);
		}
		vo.setIslastversion(UFBoolean.TRUE);
	}

	/**
	 * 处理部门历史信息<br>
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	@Override
	protected void reformDeptHistoryInfo(AggHRDeptVO aggVO) throws BusinessException {
		handleDeptHistory(aggVO);
	}

	/**
	 * 插入部门负责人数据到部门主管表<br>
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	private void insertPrincipal(HRDeptVO vo) throws BusinessException {
		UserVO uservo = getUserManageQuery().queryUserVOByPsnDocID(vo.getPrincipal());
		if (uservo != null) {
			DeptManager deptManagerVO = new DeptManager();
			deptManagerVO.setPk_dept(vo.getPk_dept());
			deptManagerVO.setPk_psndoc(vo.getPrincipal());
			deptManagerVO.setCuserid(uservo.getPrimaryKey());
			deptManagerVO.setPrincipalflag(UFBoolean.TRUE);

			if (deptManagerVO.getPk_group() == null) {
				deptManagerVO.setPk_group(vo.getPk_group());
			}

			if (deptManagerVO.getPk_org() == null) {
				deptManagerVO.setPk_org(vo.getPk_dept());
			}
			BDPKLockUtil.lockSuperVO(deptManagerVO);
			getDeptDao().getBaseDAO().insertVO(deptManagerVO);// 数据库操作
		}
	}

	@Override
	public AggHRDeptVO update(AggHRDeptVO vo, boolean blChangeAuditInfo) throws BusinessException {
		beforeUpdate(vo);

		if (vo == null) {
			// afterUpdate(beforeUpdateVO, null);
			return null;
		}
		// 数据权限校验
		updatePowerValidate(vo);

		// 加锁操作：为所在分支的根编码加主键锁；如果修改到另一分支，还要为新分支的根主键加锁； 为自己加业务锁；
		updateLockOperate(vo);

		// 检索OldVO: oldVO只关心主表
		AggHRDeptVO oldVO = getMDQueryService().queryBillOfVOByPK(getEntityClass(), vo.getParentVO().getPrimaryKey(),
				false);

		// 版本校验
		updateVersionValidate(oldVO, vo);

		// 逻辑校验
		updateValidateVO(oldVO, vo);

		// 更新审计信息
		setUpdateAuditInfo(vo, blChangeAuditInfo);

		// 事件前通知
		fireBeforeUpdateEvent(oldVO, vo);

		// DB操作：保存修改的VO; 更新内部编码; 重新检索新VO;
		AggHRDeptVO resultVO = dbUpdateVO(oldVO, vo);

		// 缓存通知
		notifyVersionChangeWhenDataUpdated(vo);

		// 事件后通知
		fireAfterUpdateEvent(oldVO, vo);

		// 业务日志
		writeUpdatedBusiLog(vo);

		afterUpdate(oldVO, resultVO);

		resultVO = getServiceTemplate().updateTsAndKeys(resultVO);

		// 处理部门历史信息
		reformDeptHistoryInfo(resultVO);

		return resultVO;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void afterUpdate(AggHRDeptVO oldAggVO, AggHRDeptVO newAggVO) throws BusinessException {
		// HRDeptVO oldVO = (HRDeptVO)oldAggVO.getParentVO();
		HRDeptVO newVO = (HRDeptVO) newAggVO.getParentVO();

		// 向版本表更新该版本信息
		HRDeptVersionVO deptVersionVO = SuperVOHelper.createSuperVOFromSuperVO(newVO, HRDeptVersionVO.class);
		BDPKLockUtil.lockSuperVO(deptVersionVO);
		// 数据库操作
		getDeptDao().getBaseDAO().updateVO(deptVersionVO);
		// 通知缓存
		CacheProxy.fireDataUpdated(new DefaultGetBizInfoByMDUtil().getTableName(deptVersionVO), null);

		// 同步更新OrgVO
		OrgVO orgvo = initOrgVO(newVO);
		orgvo = getOrgUnitManageService().updateVO(orgvo);

		// 更新报表VO
		ReportOrgVO reportOrgVO = getOMCommonQueryService().queryByPK(ReportOrgVO.class, newVO.getPk_dept());
		if (reportOrgVO != null) {
			new DeptTypeServiceAdapterBasic().execEachDeptUpdateOperate(newVO, reportOrgVO);
		}
		// 修改时对部门变更历史子表进行处理-kongrf
		DeptHistoryVO[] historyVOs = (DeptHistoryVO[]) oldAggVO.getTableVO(AggHRDeptVO.HISTORY);
		// 当部门做过变更操作时则不更新历史信息
		if (ArrayUtils.isEmpty(historyVOs) || historyVOs.length > 1) {
			return;
		}
		// 找出部门设立的历史
		DeptHistoryVO historyVO = historyVOs[0];
		historyVO.setCode(newVO.getCode());
		historyVO.setName(newVO.getName());
		historyVO.setDeptlevel(newVO.getDeptlevel());
		historyVO.setMemo(newVO.getMemo());
		getDeptDao().getBaseDAO().updateVO(historyVO);
		// 更新子表，用于界面显示
		newAggVO.setTableVO(AggHRDeptVO.HISTORY, new DeptHistoryVO[] { historyVO });
		// 对部门变更历史进行处理
		// handleDeptHistory(newAggVO);
	}

	@Override
	protected void afterDelete(AggHRDeptVO aggVO) throws BusinessException {
		HRDeptVO vo = (HRDeptVO) aggVO.getParentVO();
		returnBillCodeOnDelete(vo);

		// 从版本表删除该版本信息
		HRDeptVersionVO deptVersionVO = SuperVOHelper.createSuperVOFromSuperVO(vo, HRDeptVersionVO.class);
		BDPKLockUtil.lockSuperVO(deptVersionVO);// 乐观锁
		getDeptDao().getBaseDAO().deleteVO(deptVersionVO);// 数据库操作
		CacheProxy.fireDataDeleted(new DefaultGetBizInfoByMDUtil().getTableName(deptVersionVO),
				deptVersionVO.getPk_vid());// 通知缓存

		// 同步删除OrgVO
		OrgVO orgVO = initOrgVO(vo);
		getOrgUnitManageService().deleteVO(orgVO);

		// 如果UAP的部门节点对此部门进行过处理，那么应该同步删除对应的报表组织
		ReportOrgVO reportOrgVO = getOMCommonQueryService().queryByPK(ReportOrgVO.class, vo.getPk_dept());
		if (reportOrgVO != null) {
			getReportOrgManageService().deleteVO(reportOrgVO);
		}
	}

	@Override
	protected void afterDisable(AggHRDeptVO oldAggVO, AggHRDeptVO newAggVO) throws BusinessException {
		HRDeptVO newVO = (HRDeptVO) newAggVO.getParentVO();

		// 向版本表更新该版本信息
		HRDeptVersionVO deptVersionVO = SuperVOHelper.createSuperVOFromSuperVO(newVO, HRDeptVersionVO.class);
		BDPKLockUtil.lockSuperVO(deptVersionVO);
		// 数据库操作
		getDeptDao().getBaseDAO().updateVO(deptVersionVO);
		// 通知缓存
		CacheProxy.fireDataUpdated(new DefaultGetBizInfoByMDUtil().getTableName(deptVersionVO), null);

		// 同步更新OrgVO
		OrgVO orgvo = initOrgVO(newVO);
		orgvo = getOrgUnitManageService().updateVO(orgvo);
	}

	@Override
	protected void afterEnable(AggHRDeptVO oldAggVO, AggHRDeptVO newAggVO) throws BusinessException {
		afterDisable(oldAggVO, newAggVO);
	}

	@Override
	public int hasDeptPower(String userPK, String[] deptPKs) throws BusinessException {
		return -1;
	}

	private boolean isAutoGenerateBillCode(String billType, String pk_group, String pk_org) throws BusinessException {
		BillCodeContext billCodeContext = NCLocator.getInstance().lookup(IBillcodeManage.class)
				.getBillCodeContext(billType, pk_group, pk_org);
		return billCodeContext != null;
	}

	@Override
	public String queryUserPowerDeptSQL() throws BusinessException {
		return null;
	}

	@Override
	public void createDeptStruVersion(String pk_org, String vname, UFDate vstartdate) throws BusinessException {
		String pk_svid = OidGenerator.getInstance().nextOid();// 新版本主键

		// step1:查询出pk_org的最后一个部门结构版本数据，更新旧版本的venddate字段为新版本生效日期-1
		String condition = HRDeptStruVersionVO.PK_BUSIUNIT + " = '" + pk_org + "' and venddate >= '9999-12-31'";

		HRDeptStruVersionVO[] deptStruVersionVOs = getOMCommonQueryService().queryByCondition(
				HRDeptStruVersionVO.class, condition);
		if (!ArrayUtils.isEmpty(deptStruVersionVOs)) {
			HRDeptStruVersionVO oldDeptVersionVO = deptStruVersionVOs[0];
			BDPKLockUtil.lockSuperVO(oldDeptVersionVO);// 乐观锁
			oldDeptVersionVO.setVenddate(vstartdate.getDateBefore(1));// 最新版本时间
			getDeptDao().getBaseDAO().updateVO(oldDeptVersionVO);// 数据库操作
		}

		// step2:新增部门结构版本信息
		HRDeptStruVersionVO deptStruVersionVO = new HRDeptStruVersionVO();
		deptStruVersionVO.setPk_vid(pk_svid);
		deptStruVersionVO.setPk_busiunit(pk_org);
		deptStruVersionVO.setVname(vname);
		// 查询下一个版本号
		String vno = getOMCommonQueryService().queryNextVersionNO("org_deptstru_v", "pk_busiunit", pk_org);
		deptStruVersionVO.setVno(vstartdate.getYear() + vno);
		deptStruVersionVO.setVstartdate(vstartdate);
		deptStruVersionVO.setVenddate(new UFDate("9999-12-31", false));
		BDPKLockUtil.lockSuperVO(deptStruVersionVO);// 乐观锁
		getDeptDao().getBaseDAO().insertVOWithPK(deptStruVersionVO);// 数据库操作

		// step3:查询出pk_org下所有部门数据
		condition = HRDeptVO.PK_ORG + " = '" + pk_org + "'";
		HRDeptVO[] deptVOs = getOMCommonQueryService().queryByCondition(HRDeptVO.class, condition);

		// step4:新增部门结构成员版本信息
		HRDeptStruMemberVersionVO[] deptVersionVOs = new HRDeptStruMemberVersionVO[deptVOs.length];
		for (int i = 0; i < deptVOs.length; i++) {
			deptVersionVOs[i] = new HRDeptStruMemberVersionVO();
			deptVersionVOs[i].setPk_busiunit(pk_org);
			deptVersionVOs[i].setPk_svid(pk_svid);// 各成员的结构版本号为结构的最新版本号
			deptVersionVOs[i].setPk_org(deptVOs[i].getPk_dept());
			deptVersionVOs[i].setPk_orgvid(deptVOs[i].getPk_vid());
			deptVersionVOs[i].setPk_fatherorg(deptVOs[i].getPk_fatherorg());
		}
		BDPKLockUtil.lockSuperVO(deptVersionVOs);// 乐观锁
		getDeptDao().getBaseDAO().insertVOArray(deptVersionVOs);// 数据库操作
		JFQueryOrgLogUtils.writeQueryDeptLog("5b25ee62-7481-4f42-a869-ace037ecf44c", pk_org, "DeptStuVersion");
	}

	@Override
	public AggHRDeptVO[] createDeptVersion(AggHRDeptVO[] deptVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(deptVOs)) {
			return new AggHRDeptVO[0];
		}
		AggHRDeptVO[] resultVOs = new AggHRDeptVO[deptVOs.length];
		for (int i = 0; i < deptVOs.length; i++) {
			resultVOs[i] = createDeptVersion(deptVOs[i]);
		}
		return resultVOs;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public AggHRDeptVO createDeptVersion(AggHRDeptVO aggVO) throws BusinessException {
		HRDeptVO deptVO = (HRDeptVO) aggVO.getParentVO();
		// UFDate vstartdate = deptVO.getVstartdate();// 新版本时间

		UFDate vstartdate = new UFDate(new Date());// 新版本生效时间

		String pk_deptvid = OidGenerator.getInstance().nextOid();// 新版本主键

		String condition = HRDeptVersionVO.PK_DEPT + " = '" + deptVO.getPrimaryKey() + "' and pk_vid = '"
				+ deptVO.getPk_vid() + "'";
		HRDeptVersionVO[] deptVersionVOs = getOMCommonQueryService().queryByCondition(HRDeptVersionVO.class, condition);

		if (ArrayUtils.isEmpty(deptVersionVOs)) {
			return null;
		}

		// 查询出需要生成新版本的deptVO的旧版本数据，更新旧版本的venddate字段为新版本生效日期-1
		HRDeptVersionVO oldDeptVersionVO = deptVersionVOs[0];
		BDPKLockUtil.lockSuperVO(oldDeptVersionVO);
		oldDeptVersionVO.setVenddate(vstartdate.getDateBefore(1));
		// 审计信息
		AuditInfoUtil.updateData(oldDeptVersionVO);
		// 数据库操作
		BaseDAO baseDAO = getDeptDao().getBaseDAO();
		baseDAO.updateVO(oldDeptVersionVO);

		// 更新dept表中的deptvo为最新版本
		deptVO.setPk_vid(pk_deptvid);
		String vno = getOMCommonQueryService().queryNextVersionNO("org_dept_v", "pk_dept", deptVO.getPk_dept());
		deptVO.setVno(vstartdate.getYear() + vno);
		deptVO.setVstartdate(vstartdate);
		deptVO.setVenddate(new UFDate("9999-12-31", false));
		BDPKLockUtil.lockSuperVO(deptVO);
		// 版本检查
		BDVersionValidationUtil.validateSuperVO(deptVO);
		BizlockDataUtil.lockDataByBizlock(deptVO);
		// 审计信息
		AuditInfoUtil.updateData(deptVO);
		// 数据库操作
		baseDAO.updateVO(deptVO);

		// 将最新版本deptvo插入deptversion表
		HRDeptVO newDeptVO = getOMCommonQueryService().queryByPK(HRDeptVO.class, deptVO.getPk_dept());
		// 根据HRDeptVO得到HRDeptVersionVO
		HRDeptVersionVO deptVersionVO = SuperVOHelper.createSuperVOFromSuperVO(newDeptVO, HRDeptVersionVO.class);
		BDPKLockUtil.lockSuperVO(deptVersionVO);
		// 数据库操作
		baseDAO.insertVOWithPK(deptVersionVO);
		aggVO.setParentVO(newDeptVO);

		OrgTypeVO[] orgtypevos = OrgTypeManager.getInstance().getAllDeptType();
		for (int i = 0; i < orgtypevos.length; i++) {
			if (!OrgTypeManager.getInstance().isTypeOf(newDeptVO, orgtypevos[i].getPrimaryKey())) {
				continue;
			}
			SuperVO eachorgTypeVO = (SuperVO) newDeptVO.getBusinessOrgVOByTypeID(orgtypevos[i].getPk_orgtype());
			if (eachorgTypeVO == null) {
				continue;
			}
			String conditionVersion = eachorgTypeVO.getPKFieldName() + " = '" + eachorgTypeVO.getPrimaryKey()
					+ "' and " + IOrgVersionConst.PK_VID + " = '"
					+ eachorgTypeVO.getAttributeValue(IOrgVersionConst.PK_VID) + "'";
			Class czz = null;
			Collection<SuperVO> eachTypevc = new ArrayList<SuperVO>();
			try {
				String fullclassname = orgtypevos[i].getFullclassname();
				String newfullclassname = fullclassname.replaceAll(".org.", ".vorg.");
				newfullclassname = newfullclassname.replaceAll("VO", "VersionVO");

				czz = Class.forName(newfullclassname);
				eachTypevc = baseDAO.retrieveByClause(czz, conditionVersion);

				if (eachTypevc != null && eachTypevc.size() > 0) {
					// 查询出需要生成新版本的deptvo的旧版本数据，更新旧版本的venddate字段为新版本生效日期-1
					SuperVO oldEachOrgTypeVersionVO = eachTypevc.toArray((SuperVO[]) Array.newInstance(czz,
							eachTypevc.size()))[0];
					setOldVersionVOInfo(oldEachOrgTypeVersionVO, vstartdate);
					new OrgTypeServiceAdapterBasic().execEachOrgVersionUpdateOperate(oldEachOrgTypeVersionVO);

					// 将最新版本组织类型数据插入组织类型版本信息表
					SuperVO newEachTypeOrgVersionvo = getNewVersionVO(eachorgTypeVO);// 根据deptvo得到deptversionvo

					setNewVersionVOInfo(newEachTypeOrgVersionvo, pk_deptvid, newDeptVO.getVname(), vno, vstartdate);

					new OrgTypeServiceAdapterBasic().execEachOrgVersionInsertOperate(newEachTypeOrgVersionvo);

					// 更新各组织类型表中的组织类型数据为最新版本
					eachorgTypeVO = new DeptTypeServiceAdapterBasic().execEachDeptUpdateOperate(newDeptVO,
							eachorgTypeVO);

					newDeptVO.syncOrgTypeData(eachorgTypeVO);
				}
			} catch (ClassNotFoundException e) {
				throw new BusinessException(e);
			}
		}
		return aggVO;
	}

	/**
	 * 为新版本VO设置相关版本字段值
	 * 
	 * @param vo
	 * @param pk_vid
	 * @param vname
	 * @param newVersionVstartDate
	 * @throws BusinessException
	 */
	private void setNewVersionVOInfo(SuperVO vo, String pk_vid, String vname, String vno, UFDate newVersionVstartDate)
			throws BusinessException {
		vo.setAttributeValue(IOrgVersionConst.PK_VID, pk_vid);
		vo.setAttributeValue(IOrgVersionConst.VNAME, vname);
		vo.setAttributeValue(IOrgVersionConst.VNO, vno);
		vo.setAttributeValue(IOrgVersionConst.VSTARTDATE, newVersionVstartDate);
		vo.setAttributeValue(IOrgVersionConst.VENDDATE, new UFDate(IOrgVersionConst.DEFAULTVENDDATE, false));
		vo.setAttributeValue(IOrgVersionConst.ISLASTVERSION, UFBoolean.TRUE);
	}

	/**
	 * 各具体类型组织版本化使用，例从CorpVO构造CorpVersionVO、DeptVO构造DeptVersionVO......
	 * 
	 * @param vo
	 * @return
	 */
	private SuperVO getNewVersionVO(SuperVO vo) {
		String[] attrs = vo.getAttributeNames();
		if (attrs == null || attrs.length == 0)
			return null;

		String fullclassname = vo.getClass().getName();
		String newfullclassname = "";
		if (fullclassname.indexOf(".org.") != -1) {
			newfullclassname = fullclassname.replaceAll(".org.", ".vorg.");
		} else if (fullclassname.indexOf(".corg.") != -1) {
			newfullclassname = fullclassname.replaceAll(".corg.", ".vorg.");
		}
		newfullclassname = newfullclassname.replaceAll("VO", "VersionVO");

		try {
			@SuppressWarnings("rawtypes")
			Class czz = Class.forName(newfullclassname);
			SuperVO eachtypeorgversionvo = (SuperVO) czz.newInstance();
			for (int i = 0; i < attrs.length; i++) {
				eachtypeorgversionvo.setAttributeValue(attrs[i], vo.getAttributeValue(attrs[i]));
			}
			return eachtypeorgversionvo;
		} catch (InstantiationException e) {
			Logger.debug(e.getMessage());
		} catch (IllegalAccessException e) {
			Logger.debug(e.getMessage());
		} catch (ClassNotFoundException e) {
			Logger.debug(e.getMessage());
		}
		return null;
	}

	/**
	 * 为旧版本VO设置相关版本字段值
	 * 
	 * @param vo
	 * @param oldVersionVendDate
	 * @throws BusinessException
	 */
	private void setOldVersionVOInfo(SuperVO vo, UFDate oldVersionVendDate) throws BusinessException {
		vo.setAttributeValue(IOrgVersionConst.VENDDATE, oldVersionVendDate);
		vo.setAttributeValue(IOrgVersionConst.ISLASTVERSION, UFBoolean.FALSE);
	}

	@Override
	public PostVO[] queryPostAdjustVOMapForDeptCopy(LoginContext context, String[] deptPKs, String pk_org)
			throws BusinessException {
		// 获取待复制岗位
		PostVO[] toBeCopiedPostVOs = null;
		AggPostVO[] aggPostVOs = getPostQueryService().queryAggPostVOsByDeptPKArray(deptPKs,
				" order by pk_dept, postcode");
		if (!ArrayUtils.isEmpty(aggPostVOs)) {
			toBeCopiedPostVOs = SuperVOHelper.getParentVOArrayFromAggVOs(aggPostVOs, PostVO.class);
		} else {
			toBeCopiedPostVOs = new PostVO[0];
		}

		CrossOrgCopyFilter.filterByExcludeAttributs(IMetaDataIDConst.POST, new String[] { "pk_dept" },
				toBeCopiedPostVOs, context, false, false);
		return toBeCopiedPostVOs;
	}

	@Override
	public Map<String, PostAdjustVO[]> queryPostAdjustVOMapForDeptMerge(String[] mergedDeptPKs, String takeOverDeptPK)
			throws BusinessException {
		Map<String, PostAdjustVO[]> resultMap = new HashMap<String, PostAdjustVO[]>();

		PostAdjustVO[] mergedPosts = getPostAdjustVOByDeptPK(UFBoolean.TRUE, mergedDeptPKs);
		PostAdjustVO[] takeOverPosts = getPostAdjustVOByDeptPK(UFBoolean.FALSE, takeOverDeptPK);

		resultMap.put(TO_BE_COPIED, mergedPosts);
		resultMap.put(EXISTS, takeOverPosts);

		return resultMap;
	}

	/**
	 * 通过部门PK来获得PostAdjustVO<br>
	 * 
	 * @param isMergedDept
	 * @param pk_depts
	 * @return
	 * @throws BusinessException
	 */
	private PostAdjustVO[] getPostAdjustVOByDeptPK(UFBoolean isMergedDept, String... pk_depts) throws BusinessException {
		// 获取部门下的岗位
		// 开始属性复制
		PostAdjustVO[] postAdjustVOs = getDeptDao().retrievePostVOByDeptPK(pk_depts);

		for (int i = 0; i < postAdjustVOs.length; i++) {
			postAdjustVOs[i].setMergedpost(isMergedDept);
		}
		return postAdjustVOs;
	}

	@Override
	public PersonAdjustVO[] queryPersonAdjustVOForDeptMerge(DeptHistoryVO[] mergedDept, DeptHistoryVO takeOverDept,
			PostAdjustVO[] adjustedPostVO, PostAdjustVO[] savedPostVO) throws BusinessException {
		// step 1. 查询被合并部门下的所有人员
		String[] mergedDeptPKs = SuperVOHelper.getAttributeValueArray(mergedDept, "pk_dept", String.class);
		PsndocVO[] psndocVOs = getPersonQueryService().queryPsndocVOByDeptPK(mergedDeptPKs, true, false);

		if (ArrayUtils.isEmpty(psndocVOs)) {
			return new PersonAdjustVO[0];
		}

		// 获得有关部门的VO
		AggHRDeptVO[] aggVO4Merg = queryByPks(mergedDeptPKs);
		HRDeptVO[] mergedDeptVOs = (HRDeptVO[]) AggVOHelper.getParentVOArrayFromAggVOs(aggVO4Merg, HRDeptVO.class);

		PsnJobVO[] psnJobVOs = new PsnJobVO[psndocVOs.length];
		for (int i = 0; i < psndocVOs.length; i++) {
			psnJobVOs[i] = psndocVOs[i].getPsnJobVO();
		}

		// 获得有关岗位的VO
		String[] allPostPKs = SuperVOHelper.getNotNullDistinctAttributeValueArray(psnJobVOs, PsnJobVO.PK_POST,
				String.class);

		PostVO[] allPostVOs = null;

		if (ArrayUtils.isEmpty(allPostPKs)) {// 如果人都没有设置岗位
			allPostVOs = new PostVO[0];
		} else {
			AggPostVO[] aggPostVOs = getPostQueryService().queryByPks(allPostPKs);
			allPostVOs = SuperVOHelper.getParentVOArrayFromAggVOs(aggPostVOs, PostVO.class);
		}

		// 获得有关任职类型的VO
		String[] allJobModePKs = SuperVOHelper.getNotNullDistinctAttributeValueArray(psndocVOs, PsnJobVO.JOBMODE,
				String.class);
		DefdocVO[] allJobModes = getDefdocQueryService().queryDefdocByPk(allJobModePKs);

		PersonAdjustVO[] personAdjustVOs = initPersonAdjustVO(psndocVOs, mergedDeptVOs, allPostVOs, allJobModes);

		// step 2. 对人员在新部门的岗位进行适配
		// FIXME 加入一个计数，当所有岗位都被匹配可以不去执行职务匹配了
		Map<String, Integer> postCache = new HashMap<String, Integer>();
		String[] pk_posts = SuperVOHelper.getAttributeValueArray(adjustedPostVO, "pk_post", String.class);

		for (int i = 0; i < pk_posts.length; i++) {
			postCache.put(pk_posts[i], i);
		}

		for (int i = 0; i < personAdjustVOs.length; i++) {
			PersonAdjustVO personAdjustVO = personAdjustVOs[i];
			String pk_oldpost = personAdjustVO.getPk_oldpost();
			if (postCache.containsKey(pk_oldpost)) {// 如果岗位被迁移过去了
													// 这里岗位PK要设成复制后的，这里要注意，一定要保持adjustedPostVO和savedPostVO记录的一一对应性
				int index = postCache.get(pk_oldpost);
				personAdjustVO.setPk_newpost(savedPostVO[index].getPk_post());
				personAdjustVO.setNewpostcode(savedPostVO[index].getPostcode());
				SuperVOHelper.copyMultiLangAttribute(savedPostVO[index], personAdjustVO, new String[] { "postname" },
						new String[] { "newpostname" });
			}
			// 设定部门信息
			personAdjustVO.setPk_newdept(takeOverDept.getPk_dept());
			HRDeptVO deptVO = getOMCommonQueryService().queryByPK(HRDeptVO.class, takeOverDept.getPk_dept());
			SuperVOHelper.copyMultiLangAttribute(deptVO, personAdjustVO, new String[] { HRDeptVO.NAME },
					new String[] { "newdeptname" });

		}

		// 按 "部门+职务" 找匹配的岗位，当只找到一个时，为该岗位；
		// 经过考虑，不在第一次循环的时候针对每条没找到的岗位查询职务，还是循环两次，职务用一个SQL查出来快一些。

		// 得到职务主键列表
		// FIXME 待优化，应该取出所有没匹配到的岗位对应的职务
		String[] pk_jobs = SuperVOHelper.getNotNullAttributeValueArray(personAdjustVOs, "pk_job", String.class);
		Map<String, Set<PostVO>> postMap = getDeptDao().retrievePostVOMapByJobDept(pk_jobs, takeOverDept.getPk_dept());

		if (postMap.isEmpty()) {
			return personAdjustVOs;
		}

		for (PersonAdjustVO personAdjustVO : personAdjustVOs) {
			if (personAdjustVO.getPk_newpost() == null) {// 岗位自动匹配
				String pk_job = personAdjustVO.getPk_job();
				Set<PostVO> postVOs = postMap.get(pk_job);
				// 当职务对应的岗位有且只有一个时
				if (postVOs == null || postVOs.size() == 0 || postVOs.size() > 1) {
					continue;
				}

				PostVO postVO = postVOs.toArray(new PostVO[0])[0];
				personAdjustVO.setPk_newpost(postVO.getPk_post());
				personAdjustVO.setNewpostcode(postVO.getPostcode());
				personAdjustVO.setNewpostname(postVO.getPostname());
			}
		}

		return personAdjustVOs;
	}

	/**
	 * 初始化部门合并时使用的调节人员信息的VO
	 * 
	 * @param psndocVOs
	 * @param mergedDeptVOs
	 * @param allPostVOs
	 * @param allJobModes
	 * @return
	 */
	private PersonAdjustVO[] initPersonAdjustVO(PsndocVO[] psndocVOs, HRDeptVO[] mergedDeptVOs, PostVO[] allPostVOs,
			DefdocVO[] allJobModes) {
		Map<String, HRDeptVO> deptVOMap = SuperVOHelper.buildPrimaryKeyToVOMap(mergedDeptVOs);
		Map<String, PostVO> postVOMap = SuperVOHelper.buildPrimaryKeyToVOMap(allPostVOs);
		Map<String, DefdocVO> defdocVOMap = SuperVOHelper.buildPrimaryKeyToVOMap(allJobModes);

		PersonAdjustVO[] personAdjustVOs = new PersonAdjustVO[psndocVOs.length];

		for (int i = 0; i < psndocVOs.length; i++) {
			personAdjustVOs[i] = new PersonAdjustVO();
			personAdjustVOs[i].setPk_psnjob(psndocVOs[i].getPsnJobVO().getPk_psnjob());
			personAdjustVOs[i].setPsncode(psndocVOs[i].getCode());
			personAdjustVOs[i].setPk_job(psndocVOs[i].getPsnJobVO().getPk_job());
			SuperVOHelper.copyMultiLangAttribute(psndocVOs[i], personAdjustVOs[i], new String[] { PsndocVO.NAME },
					new String[] { "psnname" });
			HRDeptVO deptVO = deptVOMap.get(psndocVOs[i].getPsnJobVO().getPk_dept());
			if (deptVO != null) {
				personAdjustVOs[i].setPk_olddept(deptVO.getPk_dept());
				SuperVOHelper.copyMultiLangAttribute(deptVO, personAdjustVOs[i], new String[] { HRDeptVO.NAME },
						new String[] { "olddeptname" });
			}

			PostVO postVO = postVOMap.get(psndocVOs[i].getPsnJobVO().getPk_post());
			if (postVO != null) {
				personAdjustVOs[i].setPk_oldpost(postVO.getPk_post());
				personAdjustVOs[i].setOldpostcode(postVO.getPostcode());
				SuperVOHelper.copyMultiLangAttribute(postVO, personAdjustVOs[i], new String[] { PostVO.POSTNAME },
						new String[] { "oldpostname" });
			}
			DefdocVO jobMode = defdocVOMap.get(psndocVOs[i].getPsnJobVO().getJobmode());
			if (jobMode != null) {
				SuperVOHelper.copyMultiLangAttribute(jobMode, personAdjustVOs[i], new String[] { DefdocVO.NAME },
						new String[] { "jobmode" });
			}

		}

		return personAdjustVOs;
	}

	@Override
	public void copy(DeptCopyWrapperVO wrapperVO) throws BusinessException {
		Map<String, String> deptPKBackupMap = new HashMap<String, String>();

		// step 1. 根据来源VO来获得部门"模板"VO
		HRDeptVO[] sourceDeptVOs = buildTemplateDepts(wrapperVO.getSourceDeptVOs());

		DeptCopyConfirmVO[] copyConfirmVOs = wrapperVO.getTargetDeptVOs();

		// step 2. 获取数据库中的岗位信息
		AggPostVO[] toBeCopiedAggPostVOs = null;
		if (wrapperVO.isCopyPosts()) {// 不复制岗位的话直接返回
			toBeCopiedAggPostVOs = buildTemplatePostVOs(sourceDeptVOs);
		}

		for (int i = 0; i < copyConfirmVOs.length; i++) {
			// step 3. 插入部门信息到数据库
			saveToBeCopiedDeptVOs(wrapperVO, copyConfirmVOs[i], sourceDeptVOs, deptPKBackupMap);
			if (!wrapperVO.isCopyPosts()) {// 不复制岗位的话直接返回
				continue;
			}
			// step 4. 插入岗位信息到数据库
			if (!ArrayUtils.isEmpty(toBeCopiedAggPostVOs)) {
				saveToBeCopiedAggPostVOs(wrapperVO, copyConfirmVOs[i], toBeCopiedAggPostVOs, deptPKBackupMap);
			}
		}

	}

	/**
	 * 部门复制步骤1：根据来源VO来获得部门"模板"VO<br>
	 * 
	 * @param wrapperVO
	 * @return
	 * @throws BusinessException
	 */
	private HRDeptVO[] buildTemplateDepts(HRDeptVO[] sourceDeptVOs) throws BusinessException {
		// 在内存中更新内部码，用于排序
		SuperVOHelper.updateInnerCode(sourceDeptVOs, HRDeptVO.PK_DEPT, HRDeptVO.PK_FATHERORG);

		// 首先对部门进行排序，这样才能维护内部码
		sourceDeptVOs = SuperVOHelper.sort(sourceDeptVOs, new Comparator<HRDeptVO>() {
			@Override
			public int compare(HRDeptVO o1, HRDeptVO o2) {// 让部门按照上下级的关系插入到数据库
															// 注释中的上下级不代表直接上下级，是在树中的层级
				if (o1.getInnercode() == null) {// 当o1的上级部门为空时，o2为空那么是平级，o2不为空为下级
					return o2.getInnercode() == null ? 0 : -1;
				}
				if (o2.getInnercode() == null) {// 当o1的上级部门不为空时，o2为空，那么o1为o2的下级
					return 1;
				}
				return o1.getInnercode().compareTo(o2.getInnercode());
			}
		});
		//
		String[] clearAttributes = new String[] { HRDeptVO.PRINCIPAL, HRDeptVO.ADDRESS, HRDeptVO.TEL, HRDeptVO.CREATOR,
				HRDeptVO.CREATIONTIME, HRDeptVO.MODIFIER, HRDeptVO.MODIFIEDTIME };
		Object[] nullValues = new Object[] { null, null, null, null, null, null, null };
		SuperVOHelper.copySuperVOAttributes(sourceDeptVOs, clearAttributes, nullValues);

		return sourceDeptVOs;
	}

	/**
	 * 部门复制步骤2：插入部门信息到数据库<br>
	 * 
	 * @param wrapperVO
	 * @return
	 * @throws BusinessException
	 */
	private void saveToBeCopiedDeptVOs(DeptCopyWrapperVO wrapperVO, DeptCopyConfirmVO confirmVO,
			HRDeptVO[] sourceDeptVOs, Map<String, String> deptPKBackupMap) throws BusinessException {
		DeptCopyAdjustVO[] adjustVOs = confirmVO.getTargetDeptVOs();
		String fatherDeptPK = confirmVO.getPk_dept();
		HRDeptVO deptVO;
		AggHRDeptVO aggHRDeptVO;
		//
		for (int i = 0; i < sourceDeptVOs.length; i++) {
			deptVO = (HRDeptVO) sourceDeptVOs[i].clone();
			//
			DeptCopyAdjustVO adjustVO = SuperVOHelper.getSuperVOByValue(adjustVOs, HRDeptVO.PK_DEPT,
					sourceDeptVOs[i].getPk_dept());
			if (adjustVO == null) {
				continue;
			}
			// 重新设定业务单元主键
			deptVO.setPk_org(confirmVO.getPk_org());
			// 重新设定编码、名称、建立日期
			deptVO.setCode(adjustVO.getCode());
			deptVO.setName(adjustVO.getName());
			deptVO.setName2(adjustVO.getName2());
			deptVO.setName3(adjustVO.getName3());
			deptVO.setName4(adjustVO.getName4());
			deptVO.setName5(adjustVO.getName5());
			deptVO.setName6(adjustVO.getName6());
			deptVO.setCreatedate(adjustVO.getCreatedate());

			deptVO.setModifier(null);
			deptVO.setModifiedtime(null);
			deptVO.setCreator(wrapperVO.getContext().getPk_loginUser());
			deptVO.setCreationtime(new UFDateTime());

			deptVO.setPk_dept(null);
			deptVO.setInnercode(null);
			String pk_dept = sourceDeptVOs[i].getPk_dept();
			//
			if (deptPKBackupMap.containsKey(confirmVO.getPk_org() + deptVO.getPk_fatherorg()))
				deptVO.setPk_fatherorg(deptPKBackupMap.get(confirmVO.getPk_org() + deptVO.getPk_fatherorg()));
			else
				deptVO.setPk_fatherorg(fatherDeptPK);

			aggHRDeptVO = new AggHRDeptVO();
			aggHRDeptVO.setParentVO(deptVO);
			// 执行插入
			HRDeptVO newDeptVO = (HRDeptVO) insert(aggHRDeptVO).getParentVO();
			JFQueryOrgLogUtils.writeQueryDeptCopyLog("b3dbf5bc-768b-4b1f-b923-f6e315c0636a", newDeptVO, "CopyDept");

			// 形成老部门PK与新部门PK的映射，复制到多个组织，要增加组织标志
			deptPKBackupMap.put(confirmVO.getPk_org() + pk_dept, newDeptVO.getPk_dept());
		}
	}

	/**
	 * 部门复制步骤3：获取数据库中的岗位信息，并复制修改后的属性<br>
	 * 
	 * @param confirmVO
	 * @return
	 * @throws BusinessException
	 */
	private AggPostVO[] buildTemplatePostVOs(HRDeptVO[] sourceDeptVOs) throws BusinessException {
		String[] deptPKs = SuperVOHelper.getAttributeValueArray(sourceDeptVOs, "pk_dept", String.class);
		// 得到原始的PostVO、过滤撤销岗位
		AggPostVO[] sourcePostVOs = getPostQueryService().queryAggPostVOsByDeptPKArray(deptPKs,
				"  and " + PostVO.HRCANCELED + " = 'N'");

		if (ArrayUtils.isEmpty(sourcePostVOs)) {
			// 待复制岗位不为空
			return null;
		}
		PostVO[] toBeCopiedPostVOs = SuperVOHelper.getParentVOArrayFromAggVOs(sourcePostVOs, PostVO.class);

		String[] clearAttributes = new String[] { PostVO.CREATOR, PostVO.CREATIONTIME, PostVO.MODIFIER,
				PostVO.MODIFIEDTIME, PostVO.HRCANCELDATE };
		Object[] nullValues = new Object[] { null, null, null, null, null };
		SuperVOHelper.copySuperVOAttributes(toBeCopiedPostVOs, clearAttributes, nullValues);

		clearChildPK(sourcePostVOs);
		return sourcePostVOs;

	}

	/**
	 * 清空子表主键
	 * 
	 * @param sourcePostVOs
	 * @throws BusinessException
	 */
	private void clearChildPK(AggPostVO[] sourcePostVOs) throws BusinessException {
		for (AggPostVO sourcePostVO : sourcePostVOs) {
			sourcePostVO.setTableVO(AggPostVO.POSTHISTORY_SUB, null);
			// 将所有子表都设置为insert状态，否则子表不能插入数据库
			CircularlyAccessibleValueObject[] allChildren = sourcePostVO.getAllChildrenVO();
			if (!ArrayUtils.isEmpty(allChildren))
				for (CircularlyAccessibleValueObject child : allChildren) {
					child.setStatus(VOStatus.NEW);
					child.setAttributeValue(PostVO.PK_POST, null);
					child.setPrimaryKey(null);
				}
		}
	}

	/**
	 * 部门复制步骤4：插入岗位信息到数据库<br>
	 * 
	 * @param wrapperVO
	 * @param sourcePostVOs
	 * @throws BusinessException
	 */
	private void saveToBeCopiedAggPostVOs(DeptCopyWrapperVO wrapperVO, DeptCopyConfirmVO confirmVO,
			AggPostVO[] sourcePostVOs, Map<String, String> deptPKBackupMap) throws BusinessException {
		// 首先对岗位进行排序，这样才能维护内部码
		List<AggPostVO> postVOList = new ArrayList<AggPostVO>();
		Collections.addAll(postVOList, sourcePostVOs);
		Collections.sort(postVOList, new Comparator<AggPostVO>() {

			@Override
			public int compare(AggPostVO aggVO1, AggPostVO aggVO2) {// 让岗位按照上下级的关系重新排列
																	// 注释中的上下级不代表直接上下级，是在树中的层级
				PostVO postVO1 = (PostVO) aggVO1.getParentVO();
				PostVO postVO2 = (PostVO) aggVO2.getParentVO();
				if (postVO1.getInnercode() == null) {// 当o1的上级岗位为空时，o2为空那么是平级，o2不为空为下级
					return postVO2.getInnercode() == null ? 0 : -1;
				}
				if (postVO2.getInnercode() == null) {// 当o1的上级岗位不为空时，o2为空，那么o1为o2的下级
					return 1;
				}
				return postVO1.getInnercode().compareTo(postVO2.getInnercode());
			}

		});
		boolean isAutoGenerateBillCode = isAutoGenerateBillCode("post", wrapperVO.getContext().getPk_group(),
				confirmVO.getPk_org());

		// 获得排序后的岗位
		sourcePostVOs = postVOList.toArray(new AggPostVO[0]);
		clearChildPK(sourcePostVOs);
		String prefix = "ZD" + PubEnv.getServerDate().toStdString();
		// 不自动生成单据号 /默认规则生成 "单据类型+yyyy-mm-dd+_流水号"
		String flowCode = getFlowCode(prefix);
		Map<String, String> postPKBackupMap = new HashMap<String, String>();
		// List<String> newPostPK = new ArrayList<String>();
		for (int i = 0; i < sourcePostVOs.length; i++) {
			// AggPostVO sourcePostVO = AggVOHelper.clone(sourcePostVOs[i],
			// AggPostVO.class);
			// AggVOHelper的克隆方法在克隆多子表时出现问题，现改为直接使用，不进行克隆
			AggPostVO sourcePostVO = sourcePostVOs[i];
			PostVO toBeCopiedPostVO = (PostVO) sourcePostVO.getParentVO();
			// 设定属于的新的组织单元
			toBeCopiedPostVO.setPk_org(confirmVO.getPk_org());
			//
			toBeCopiedPostVO.setPk_hrorg(confirmVO.getPk_org());
			// 设置岗位成立日期为是生效日期
			toBeCopiedPostVO.setBuilddate(new UFLiteralDate());
			String pk_post = toBeCopiedPostVO.getPk_post();
			String postcode = toBeCopiedPostVO.getPostcode();
			String suporior = toBeCopiedPostVO.getSuporior();
			toBeCopiedPostVO.setPk_post(null);
			toBeCopiedPostVO.setInnercode(null);

			// 更新岗位的部门主键
			String pk_dept = toBeCopiedPostVO.getPk_dept();
			String pk_newdept = deptPKBackupMap.get(confirmVO.getPk_org() + pk_dept);
			toBeCopiedPostVO.setPk_dept(pk_newdept);
			// 在插入前还要修改额外的一些信息
			// step 1. 判断编码是否重复
			boolean duplicated = havePostCode(toBeCopiedPostVO);
			// step 2. 如果重复，那么需要自动生成一个流水号编码来代替
			if (isAutoGenerateBillCode) {
				String billcode = NCLocator
						.getInstance()
						.lookup(IBillcodeManage.class)
						.getBillCode_RequiresNew(IOrgBillCodeConst.BILL_CODE_POST,
								wrapperVO.getContext().getPk_group(), confirmVO.getPk_org(), toBeCopiedPostVO);
				toBeCopiedPostVO.setPostcode(billcode);
			} else {
				if (duplicated) {
					// 不自动生成单据号
					toBeCopiedPostVO.setPostcode(prefix + "_" + getFlowCode(flowCode, i));
				}
			}

			// FIXME 岗位创建时间
			// toBeCopiedPostVO.setBuilddate();
			/*
			 * // 把在目标组织不可见的岗位序列设置成null if
			 * (StringUtils.isNotBlank(toBeCopiedPostVO.getPk_postseries())) {
			 * LoginContext context = wrapperVO.getContext(); String pk_bu =
			 * context.getPk_org(); OrgVO org =
			 * NCLocator.getInstance().lookup(IAOSQueryService
			 * .class).queryHROrgByOrgPK(pk_bu); if (org == null) throw new
			 * IllegalStateException("查询不到bu：pk = " + pk_bu + " 所对应的HR组织！");
			 * context.setPk_org(org.getPk_org());
			 * CrossOrgCopyFilter.filterByIncludeAttributs
			 * ("039385a6-3e60-489c-b257-f48a1b7ab046", new String[]{
			 * PostVO.PK_POSTSERIES, PostVO.PK_JOB}, new
			 * Object[]{toBeCopiedPostVO}, context, false, false);
			 * context.setPk_org(pk_bu); }
			 */
			// 上级岗位不为空，且其上级岗位不在复制范围内
			if (StringUtils.isNotBlank(toBeCopiedPostVO.getSuporior())) {
				if (postPKBackupMap.containsKey(confirmVO.getPk_org() + toBeCopiedPostVO.getSuporior()))
					toBeCopiedPostVO.setSuporior(postPKBackupMap.get(confirmVO.getPk_org()
							+ toBeCopiedPostVO.getSuporior()));
				else {
					PostVO supPostVO = getOMCommonQueryService()
							.queryByPK(PostVO.class, toBeCopiedPostVO.getSuporior());
					if (!supPostVO.getPk_org().equals(confirmVO.getPk_org()))
						toBeCopiedPostVO.setSuporior(null);
				}
			}
			// 执行插入

			AggPostVO newPostVO = getPostManageService().insert(sourcePostVO);
			String newpk_post = ((PostVO) newPostVO.getParentVO()).getPk_post();
			postPKBackupMap.put(confirmVO.getPk_org() + pk_post, newpk_post);
			// 重置为来源部门主键（目标业务组织为多个时，还需要使用来源部门主键）
			toBeCopiedPostVO.setPk_dept(pk_dept);
			toBeCopiedPostVO.setPostcode(postcode);
			toBeCopiedPostVO.setSuporior(suporior);
		}
	}

	/**
	 * 得到今天最大的流水号
	 * 
	 * @param prefix
	 * @param codeField
	 * @param className
	 * @return 五位流水号 如:00001
	 * @throws BusinessException
	 */
	public static String getFlowCode(String prefix) throws BusinessException {
		// 有指定前缀并且长度为22的ZDXXXX1111-11-11_00001
		String whereSql = PostVO.POSTCODE + " like '" + prefix + "%' and len(" + PostVO.POSTCODE + ") = 18 order by "
				+ PostVO.POSTCODE + " desc";
		SuperVO[] vos = NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, PostVO.class, whereSql);
		if (vos == null || vos.length == 0) {
			return "00001";
		}
		for (SuperVO vo : vos) {
			String code = ((String) vo.getAttributeValue(PostVO.POSTCODE)).substring(prefix.length() + 1);
			try {
				Integer value = Integer.valueOf(code);
				if (value != null) {
					// 每天应该不会超过10万单据
					return StringUtils.leftPad(value + 1 + "", 5, '0');
				}
			} catch (NumberFormatException ex) {
				continue;
			}
		}
		return "00001";
	}

	/**
	 * 获取当前最大的流水号
	 * 
	 * @param prefix
	 * @param i
	 * @return String
	 * @throws BusinessException
	 */
	private String getFlowCode(String code, int i) throws BusinessException {
		Integer value = Integer.valueOf(code);
		return org.apache.commons.lang.StringUtils.leftPad(value + i + "", 5, '0');
	}

	/**
	 * 判断岗位编码是否重复
	 * 
	 * @param prefix
	 * @param i
	 * @return boolean
	 * @throws BusinessException
	 */
	private boolean havePostCode(PostVO toBeCopiedPostVO) {
		try {
			// 唯一性校验
			BDUniqueRuleValidate ruleValidate = new BDUniqueRuleValidate();
			DefaultValidationService vService = SimpleDocServiceTemplate.createValidationService(ruleValidate);
			vService.validate(toBeCopiedPostVO);
		} catch (BusinessException e) {
			return true;
		}
		return false;
	}

	@Override
	public AggHRDeptVO rename(AggHRDeptVO aggdeptVO, DeptHistoryVO historyVO, boolean updateCareer)
			throws BusinessException {
		// HRDeptVO deptVO = aggdeptVO.getParentVO();
		// 因为前台已经上锁，所以在更新前要先去掉后台的锁
		ILocker locker = getServiceTemplate().getLocker();
		getServiceTemplate().setLocker(new DoNotLock());

		// 记录部门变更历史
		// 获取一个变更操作号
		historyVO.setChangenum(OidGenerator.getInstance().nextOid(OidGenerator.GROUP_PK_CORP));
		// 重置所属组织版本ID
		String conOrg = OrgVersionVO.PK_ORG + " = '" + ((HRDeptVO) aggdeptVO.getParentVO()).getPk_org() + "' order by "
				+ OrgVersionVO.VNO + " desc";
		OrgVersionVO[] orgVersionVOs = getOMCommonQueryService().queryByCondition(OrgVersionVO.class, conOrg);
		if (!ArrayUtils.isEmpty(orgVersionVOs)) {
			historyVO.setPk_org_v(orgVersionVOs[0].getPk_vid());
		}
		getDeptDao().getBaseDAO().insertVO(historyVO);

		// 首先更新部门名称
		AggHRDeptVO updatedVO = update(aggdeptVO, true);
		// JFQueryOrgLogUtils.writeQueryDeptLog("9a09be67-0c5d-4d45-bc1b-30a5d8f8ce6d",
		// ((HRDeptVO)updatedVO.getParentVO()).getPk_org(), "RenameOpeDept");

		// 恢复锁
		getServiceTemplate().setLocker(locker);

		DeptRenameInfoVO renameInfoVO = new DeptRenameInfoVO();
		renameInfoVO.setUpdateCareer(updateCareer);
		renameInfoVO.setEffectDate(historyVO.getEffectdate());
		renameInfoVO.setDeptVO((HRDeptVO) updatedVO.getParentVO());

		// 发送业务事件
		BusinessEvent afterEvent = new BusinessEvent(docName, IOMEventType.DEPT_RENAME_AFTER, renameInfoVO);
		EventDispatcher.fireEvent(afterEvent);

		return updatedVO;
	}

	@Override
	public AggHRDeptVO[] shift(AggHRDeptVO aggDeptVO, DeptHistoryVO historyVO) throws BusinessException {
		// HRDeptVO deptVO = aggDeptVO.getParentVO();
		// FIXME
		// 如果该部门下人员正处于单据状态为：
		// 编写中、已提交、审核中的入职申请单、转正申请单、调配申请单、离职申请单、定调资申请单、
		// 调班申请单、出差申请单、休假申请单、加班申请单，则不能执行。
		BusinessEvent beforeEvent = new BusinessEvent(docName, IOMEventType.DEPT_SHIFT_BEFORE, aggDeptVO.getParentVO());
		EventDispatcher.fireEvent(beforeEvent);

		// 维护变更操作后所属组织版本ID更行。
		String conOrg = OrgVersionVO.PK_ORG + " = '" + historyVO.getPk_org() + "' order by " + OrgVersionVO.VNO
				+ " desc";
		OrgVersionVO[] orgVersionVOs = getOMCommonQueryService().queryByCondition(OrgVersionVO.class, conOrg);
		if (!ArrayUtils.isEmpty(orgVersionVOs)) {
			historyVO.setPk_org_v(orgVersionVOs[0].getPk_vid());
		}
		// 记录部门变更历史
		// 获取一个变更操作号
		historyVO.setChangenum(OidGenerator.getInstance().nextOid(OidGenerator.GROUP_PK_CORP));
		// 被转移部门变更历史
		getDeptDao().getBaseDAO().insertVO(historyVO);

		String receivedeptPK = historyVO.getPk_receivedept();

		if (!StringUtils.isEmpty(receivedeptPK)) {
			HRDeptVO receiveDeptVO = getOMCommonQueryService().queryByPK(HRDeptVO.class, receivedeptPK);

			// 更新属性
			historyVO.setCode(receiveDeptVO.getCode());
			SuperVOHelper.copyMultiLangAttribute(receiveDeptVO, historyVO);
			historyVO.setIsreceived(UFBoolean.TRUE);
			historyVO.setDeptlevel(receiveDeptVO.getDeptlevel());
			historyVO.setPk_dept(receiveDeptVO.getPk_dept());
			// 接收部门变更历史
			getDeptDao().getBaseDAO().insertVO(historyVO);
		}

		// 更新部门属性
		AggHRDeptVO updatedVO = update(aggDeptVO, true);
		// JFQueryOrgLogUtils.writeQueryDeptLog("ec8ea9c2-81af-4b79-9d70-629f45b6cb30",
		// ((HRDeptVO)aggDeptVO.getParentVO()).getPk_org(), "ShiftOpeDept");

		List<AggHRDeptVO> deptList = new ArrayList<AggHRDeptVO>();
		deptList.add(updatedVO);

		// 更新子表信息
		if (!StringUtils.isEmpty(receivedeptPK)) {
			AggHRDeptVO aggReceiveDeptVO = queryByPk(receivedeptPK);
			deptList.add(aggReceiveDeptVO);
			// 处理子表
			handleDeptHistory(deptList.toArray(new AggHRDeptVO[0]));
		}

		return deptList.toArray(new AggHRDeptVO[0]);
	}

	@Override
	public PostAdjustVO[] savePostForDeptMerge(PostAdjustVO[] alreadySavedPostVOs, PostAdjustVO[] backupPostVOs,
			PostAdjustVO[] postAdjustVOs) throws BusinessException {
		// 首先回滚，防止出现更新的情况
		rollbackPostForDeptMerge(alreadySavedPostVOs, backupPostVOs);
		// FIXME 流程有些复杂，循环有些多，待优化
		if (ArrayUtils.isEmpty(postAdjustVOs)) {// 如果列表为空
			return new PostAdjustVO[0];
		}

		Map<String, PostAdjustVO> postAdjustVOCache = SuperVOHelper
				.buildAttributeToVOMap(PostVO.PK_POST, postAdjustVOs);
		Map<String, Integer> postAdjustIndexCache = SuperVOHelper.buildIndexMap(PostVO.PK_POST, postAdjustVOs);

		PostAdjustVO[] savedPostVOs = new PostAdjustVO[postAdjustVOs.length];
		String[] postPKs = SuperVOHelper.getAttributeValueArray(postAdjustVOs, "pk_post", String.class);

		AggPostVO[] aggPostVOs = getPostQueryService().queryAggPostVOsByPostPKArray(postPKs, null);

		PostVO[] postVOs = SuperVOHelper.getParentVOArrayFromAggVOs(aggPostVOs, PostVO.class);

		// 需要插入到数据库的记录
		Set<PostVO> mergedPosts = new HashSet<PostVO>();

		// 需要更新的岗位集合
		List<PostVO> postUpdateList = new ArrayList<PostVO>();
		for (PostVO postVO : postVOs) {
			PostAdjustVO postAdjustVO = postAdjustVOCache.get(postVO.getPk_post());
			if (postAdjustVO == null) {
				continue;
			}

			// 复制编码名称
			postVO.setPostcode(postAdjustVO.getPostcode());
			SuperVOHelper.copyMultiLangAttribute(postAdjustVO, postVO, new String[] { PostVO.POSTNAME });

			if (UFBoolean.TRUE.equals(postAdjustVO.getMergedpost())) {// 如果是待复制部门的岗位
				postVO.setPk_dept(postAdjustVO.getPk_dept());
				// 待复制部门的岗位清空上下级
				postVO.setSuporior(null);
				postVO.setJunior(null);
				postVO.setBuilddate(postAdjustVO.getBuilddate());
				mergedPosts.add(postVO);
			} else {// 如果是原有岗位
				Integer index = postAdjustIndexCache.get(postVO.getPk_post());
				// getDeptDao().getBaseDAO().updateVO(postVO);
				postUpdateList.add(postVO);
				savedPostVOs[index] = postAdjustVO;
				savedPostVOs[index].setMergedpost(UFBoolean.FALSE);
			}
		}

		if (!postUpdateList.isEmpty()) {
			getDeptDao().getBaseDAO().updateVOArray(postUpdateList.toArray(new PostVO[0]));
		}

		if (mergedPosts.isEmpty()) {
			return savedPostVOs;
		}

		SequenceGenerator generator = new SequenceGenerator();
		String[] ids = generator.generate(mergedPosts.size());
		int offset = 0;

		// 处理待复制岗位
		DefaultMutableTreeNode[] roots = SuperVOHelper.buildMultiTreeWithSuperVOs(mergedPosts.toArray(new PostVO[0]),
				PostVO.PK_POST, PostVO.SUPORIOR);

		for (DefaultMutableTreeNode root : roots) {
			offset = updateIDAndPID(root, ids, offset, postAdjustIndexCache);
		}

		for (PostVO postVO : mergedPosts) {
			Integer index = postAdjustIndexCache.get(postVO.getPk_post());
			AggPostVO aggVO = new AggPostVO();
			aggVO.setParentVO(postVO);
			AggPostVO resultVO = getPostManageService().insert(aggVO);
			savedPostVOs[index] = SuperVOHelper.createSuperVOFromSuperVO((PostVO) resultVO.getParentVO(),
					PostAdjustVO.class);
			savedPostVOs[index].setMergedpost(UFBoolean.TRUE);
		}

		return savedPostVOs;
	}

	private int updateIDAndPID(DefaultMutableTreeNode root, String[] ids, int offset,
			Map<String, Integer> postAdjustIndexCache) throws BusinessException {
		PostVO postVO = (PostVO) root.getUserObject();
		String pk_post = postVO.getPk_post();
		Integer index = postAdjustIndexCache.get(pk_post);
		postAdjustIndexCache.remove(pk_post);
		// 更新主键
		postVO.setPk_post(ids[offset]);
		// 更新位置信息
		postAdjustIndexCache.put(postVO.getPk_post(), index);
		offset++;
		int count = root.getChildCount();
		if (count == 0) {
			return offset;
		}
		for (int i = 0; i < count; i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
			PostVO childPostVO = (PostVO) child.getUserObject();
			childPostVO.setSuporior(postVO.getPk_post());
			offset = updateIDAndPID(child, ids, offset, postAdjustIndexCache);
		}

		return offset;

	}

	@Override
	public void rollbackPostForDeptMerge(PostAdjustVO[] savedPostVOs, PostAdjustVO[] backupPostVOs)
			throws BusinessException {
		if (!ArrayUtils.isEmpty(savedPostVOs)) {// 已经保存的数据
			List<String> toBeDelPosts = new ArrayList<String>();
			for (int i = 0; i < savedPostVOs.length; i++) {
				if (UFBoolean.TRUE.equals(savedPostVOs[i].getMergedpost())) {// 如果是待合并的
					toBeDelPosts.add(savedPostVOs[i].getPk_post());
				}
			}

			if (toBeDelPosts.size() > 0) {
				for (String postPk : toBeDelPosts) {
					getPostManageService().delete(postPk);
				}
			}
		}

		if (!ArrayUtils.isEmpty(backupPostVOs)) {// 原有的岗位数据
			PostVO[] post = new PostVO[backupPostVOs.length];
			for (int i = 0; i < post.length; i++) {
				post[i] = new PostVO();
				post[i].setPk_post(backupPostVOs[i].getPk_post());
				post[i].setPostcode(backupPostVOs[i].getPostcode());
				post[i].setPostname(backupPostVOs[i].getPostname());
			}
			NCLocator.getInstance().lookup(IPersistenceUpdate.class)
					.updateVOArray(null, post, new String[] { PostVO.POSTCODE, PostVO.POSTNAME }, null);

		}

	}

	@Override
	public AggHRDeptVO[] merge(DeptMergeWrapperVO wrapperVO) throws BusinessException {

		// step 0. 处理人员相关信息
		// 更新人员的岗位，职务，部门，组织信息
		String[] mergedDeptPKs = SuperVOHelper.getAttributeValueArray(wrapperVO.getMergedDeptHistoryVOs(), "pk_dept",
				String.class);

		DeptMergeInfoVO deptMergeInfoVO = new DeptMergeInfoVO();
		AggHRDeptVO takeOverDeptVO = queryByPk(wrapperVO.getTakeOverDeptHistoryVO().getPk_dept());
		PersonAdjustVO[] personAdjustVOs = wrapperVO.getPersonAdjustVO();

		if (!ArrayUtils.isEmpty(personAdjustVOs)) {
			// 如果是接收部门是虚拟部门则不能将人员转移
			if (((HRDeptVO) takeOverDeptVO.getParentVO()).getDepttype() == 1) {
				throw new BusinessException(ResHelper.getString("6005dept", "06005dept0378"/*
																							 * @
																							 * res
																							 * "虚拟部门[{0}]下不能有人员!"
																							 */,
						((HRDeptVO) takeOverDeptVO.getParentVO()).getMultilangName()));
			}
			// 循环中查任职记录的优化
			// 任职记录主键
			ArrayList<String> psnjobPks = new ArrayList<String>();
			for (PersonAdjustVO personAdjustVO : personAdjustVOs) {
				if (psnjobPks.contains(personAdjustVO.getPk_psnjob()))
					continue;
				psnjobPks.add(personAdjustVO.getPk_psnjob());
			}
			PsnJobVO[] tmpPsnjobVOs = getOMCommonQueryService().queryByPKs(PsnJobVO.class,
					psnjobPks.toArray(new String[0]));
			// 任职记录主键对应VO的map
			HashMap<String, PsnJobVO> psnjobMap = new HashMap<String, PsnJobVO>();
			for (PsnJobVO tmpPsnjobVO : tmpPsnjobVOs) {
				psnjobMap.put(tmpPsnjobVO.getPk_psnjob(), tmpPsnjobVO);
			}

			// 循环中查岗位优化
			// 岗位主键
			ArrayList<String> postPks = new ArrayList<String>();
			for (PersonAdjustVO personAdjustVO : personAdjustVOs) {
				if (null == personAdjustVO.getPk_newpost() || postPks.contains(personAdjustVO.getPk_newpost()))
					continue;

				postPks.add(personAdjustVO.getPk_newpost());
			}
			PostVO[] tmpPostVOs = getOMCommonQueryService().queryByPKs(PostVO.class, postPks.toArray(new String[0]));
			// 岗位主键对应VO的map
			HashMap<String, PostVO> postMap = new HashMap<String, PostVO>();
			if (tmpPostVOs != null) {
				for (PostVO tmpPostVO : tmpPostVOs) {
					postMap.put(tmpPostVO.getPk_post(), tmpPostVO);
				}

			}

			// 循环中查职务优化，根据代码pk_job就是上面岗位VO 的pk_job
			ArrayList<String> jobPks = new ArrayList<String>();
			if (tmpPostVOs != null) {
				for (PostVO tmpPostVO : tmpPostVOs) {
					if (StringUtils.isEmpty(tmpPostVO.getPk_job()) || jobPks.contains(tmpPostVO.getPk_job()))
						continue;
					jobPks.add(tmpPostVO.getPk_job());
				}
			}
			JobVO[] tmpJobVOs = getOMCommonQueryService().queryByPKs(JobVO.class, jobPks.toArray(new String[0]));
			// 职务主键对应VO的map
			HashMap<String, JobVO> jobMap = new HashMap<String, JobVO>();
			if (tmpJobVOs != null) {
				for (JobVO tmpJobVO : tmpJobVOs) {
					jobMap.put(tmpJobVO.getPk_job(), tmpJobVO);
				}
			}

			PsnJobVO[] psnJobVOs = new PsnJobVO[personAdjustVOs.length];
			int counter = 0;
			for (PersonAdjustVO personAdjustVO : personAdjustVOs) {
				// **********
				// PsnJobVO psnjobVO =
				// getOMCommonQueryService().queryByPK(PsnJobVO.class,
				// personAdjustVO.getPk_psnjob());
				// **********
				PsnJobVO psnjobVO = psnjobMap.get(personAdjustVO.getPk_psnjob());

				psnjobVO.setPk_psnjob(null);
				psnJobVOs[counter++] = psnjobVO;
				psnjobVO.setPk_dept(wrapperVO.getTakeOverDeptHistoryVO().getPk_dept());
				String pk_post = personAdjustVO.getPk_newpost();
				String pk_job = null;

				// 如果试用已经结束，异动类型不为空，则将异动类型清空
				// if(!psnjobVO.getTrial_flag().booleanValue() &&
				// !StringUtils.isEmpty(psnjobVO.getTrnstype()))
				// {
				psnjobVO.setTrnstype(null);
				// }

				if (pk_post == null) {
					setPostToNull(psnjobVO);
				} else {
					// *************
					// PostVO postVO =
					// getOMCommonQueryService().queryByPK(PostVO.class,
					// pk_post);
					// ************
					PostVO postVO = postMap.get(pk_post);

					if (postVO == null) {
						setPostToNull(psnjobVO);
					} else {
						psnjobVO.setPk_post(pk_post);
						psnjobVO.setPk_postseries(postVO.getPk_postseries());
						pk_job = postVO.getPk_job();
					}
				}

				if (pk_job == null) {// 岗位主键为空，代表没有变化
					continue;
				}
				// ************
				// JobVO jobVO =
				// getOMCommonQueryService().queryByPK(JobVO.class, pk_job);
				// ************
				JobVO jobVO = jobMap.get(pk_job);

				if (jobVO == null) {
					setJobToNull(psnjobVO);
				} else if (jobVO.getPk_job().equals(psnjobVO.getPk_job())) {
					continue;
				} else {
					psnjobVO.setPk_job(pk_job);
					psnjobVO.setPk_jobgrade(null);
					psnjobVO.setPk_jobrank(jobVO.getPk_jobrank());
					psnjobVO.setSeries(jobVO.getPk_jobtype());
				}

			}

			deptMergeInfoVO.setMergedPsnJobVOs(psnJobVOs);
		}

		deptMergeInfoVO.setMergedDeptPKs(mergedDeptPKs);
		deptMergeInfoVO.setEffectDate(wrapperVO.getTakeOverDeptHistoryVO().getEffectdate());
		deptMergeInfoVO.setMergeDeptPK(wrapperVO.getTakeOverDeptHistoryVO().getPk_dept());
		deptMergeInfoVO.setUpdateCareer(wrapperVO.isUpdateCareer());
		wrapperVO.setDeptMergeInfoVO(deptMergeInfoVO);

		// TODO 执行部门合并
		// 执行部门合并

		// Step 1. 记录部门变更历史
		// 更新部门变更历史
		// 获取一个变更操作号
		String changeNum = OidGenerator.getInstance().nextOid(OidGenerator.GROUP_PK_CORP);

		AggHRDeptVO[] aggVOs = queryByPks(mergedDeptPKs);
		HRDeptVO[] mergedDeptVOs = (HRDeptVO[]) AggVOHelper.getParentVOArrayFromAggVOs(aggVOs, HRDeptVO.class);

		Map<String, HRDeptVO> mergedDeptVOMap = SuperVOHelper.buildPrimaryKeyToVOMap(mergedDeptVOs);

		// 被合并部门和接受部门的变更历史共享同一个变更操作号
		for (DeptHistoryVO mergedDeptHistoryVO : wrapperVO.getMergedDeptHistoryVOs()) {
			mergedDeptHistoryVO.setChangenum(changeNum);
			HRDeptVO deptVO = mergedDeptVOMap.get(mergedDeptHistoryVO.getPk_dept());
			if (deptVO == null) {
				continue;
			}
			// 编码
			mergedDeptHistoryVO.setCode(deptVO.getCode());
			// 部门名
			SuperVOHelper.copyMultiLangAttribute(deptVO, mergedDeptHistoryVO);
			// 部门级别
			mergedDeptHistoryVO.setDeptlevel(deptVO.getDeptlevel());
			// 负责人
			mergedDeptHistoryVO.setPrincipal(deptVO.getPrincipal());
			// 组织
			mergedDeptHistoryVO.setPk_org(deptVO.getPk_org());
		}

		HRDeptVO takeOverDept = (HRDeptVO) queryByPk(wrapperVO.getTakeOverDeptHistoryVO().getPk_dept()).getParentVO();
		wrapperVO.getTakeOverDeptHistoryVO().setChangenum(changeNum);
		// 部门级别
		wrapperVO.getTakeOverDeptHistoryVO().setDeptlevel(takeOverDept.getDeptlevel());
		// 负责人
		wrapperVO.getTakeOverDeptHistoryVO().setPrincipal(takeOverDept.getPrincipal());
		// 部门名
		SuperVOHelper.copyMultiLangAttribute(takeOverDept, wrapperVO.getTakeOverDeptHistoryVO());

		// 通过监听实现
		// 如果该部门下人员正处于单据状态为：
		// 编写中、已提交、审核中的入职申请单、转正申请单、调配申请单、离职申请单、定调资申请单、
		// 调班申请单、出差申请单、休假申请单、加班申请单，则不能执行。
		BusinessEvent beforeEvent = new BusinessEvent(docName, IOMEventType.DEPT_MERGE_BEFORE, wrapperVO);
		EventDispatcher.fireEvent(beforeEvent);

		// 记录变更历史
		getDeptDao().getBaseDAO().insertVOArray(wrapperVO.getMergedDeptHistoryVOs());
		getDeptDao().getBaseDAO().insertVO(wrapperVO.getTakeOverDeptHistoryVO());

		Set<String> mergedDeptPKCache = new HashSet<String>();
		Collections.addAll(mergedDeptPKCache, mergedDeptPKs);

		List<AggHRDeptVO> resultList = new ArrayList<AggHRDeptVO>();

		// 列表中的第一条是接收部门
		resultList.add(takeOverDeptVO);

		// 增加任职记录,同步工作履历
		// 处理部门内的兼职人员，终止兼职记录
		// 发送业务事件
		BusinessEvent afterEvent = new BusinessEvent(docName, IOMEventType.DEPT_MERGE_AFTER, wrapperVO);
		EventDispatcher.fireEvent(afterEvent);
		// 先撤销需要撤销部门下的岗位，再撤销该部门，否则撤销岗位时，岗位校验失败。 heqiaoa 20150504
		cancelPostsOfDept(mergedDeptPKs, wrapperVO.getTakeOverDeptHistoryVO().getEffectdate());

		// step 3. 处理下级部门，step 4. 将被合并部门设成撤销
		Map<String, AggHRDeptVO[]> hrDeptVOMap = new HashMap<String, AggHRDeptVO[]>();
		Map<String, OrgVersionVO[]> orgVersionVOMap = new HashMap<String, OrgVersionVO[]>();
		List<String> deptPKList = new ArrayList<String>();
		for (int i = 0; i < mergedDeptVOs.length; i++) {
			deptPKList.add(mergedDeptVOs[i].getPk_dept());
		}
		if (!deptPKList.isEmpty()) {
			InSQLCreator isc = new InSQLCreator();
			String insql = isc.getInSQL(deptPKList.toArray(new String[0]));
			String condition = HRDeptVO.PK_FATHERORG + " in (" + insql + ")";
			AggHRDeptVO[] childDeptAggVOs = queryByCondition(null, condition);
			for (int i = 0; i < deptPKList.size(); i++) {
				if (ArrayUtils.isEmpty(childDeptAggVOs)) { // 所有被合并部门都没有子部门，跳出循环
					break;
				}
				List<AggHRDeptVO> aggDeptVOList = new ArrayList<AggHRDeptVO>();
				for (int j = 0; j < childDeptAggVOs.length; j++) {
					if (deptPKList.get(i).equals(((HRDeptVO) childDeptAggVOs[j].getParentVO()).getPk_fatherorg())) {
						aggDeptVOList.add(childDeptAggVOs[j]);
					}
				}
				hrDeptVOMap.put(deptPKList.get(i), aggDeptVOList.toArray(new AggHRDeptVO[0]));
			}

			List<String> orgPKList = new ArrayList<String>();
			for (int i = 0; i < mergedDeptVOs.length; i++) {
				AggHRDeptVO[] childDeptVOs = hrDeptVOMap.get(mergedDeptVOs[i].getPk_dept());
				if (ArrayUtils.isEmpty(childDeptVOs))
					continue;

				for (int j = 0; j < childDeptVOs.length; j++) {
					HRDeptVO hrDeptVO = (HRDeptVO) childDeptVOs[j].getParentVO();
					orgPKList.add(hrDeptVO.getPk_org());
				}
			}
			if (!orgPKList.isEmpty()) {
				String sql = isc.getInSQL(orgPKList.toArray(new String[0]));
				String conOrg = OrgVersionVO.PK_ORG + " in (" + sql + ") order by " + OrgVersionVO.VNO + " desc";
				OrgVersionVO[] orgVersionVOs = getOMCommonQueryService().queryByCondition(OrgVersionVO.class, conOrg);
				for (int i = 0; i < orgPKList.size(); i++) {
					List<OrgVersionVO> orgVersionList = new ArrayList<OrgVersionVO>();
					for (int j = 0; j < orgVersionVOs.length; j++) {
						if (orgPKList.get(i).equals(orgVersionVOs[j].getPk_org())) {
							orgVersionList.add(orgVersionVOs[j]);
						}
					}
					orgVersionVOMap.put(orgPKList.get(i), orgVersionList.toArray(new OrgVersionVO[0]));
				}
			}
		}
		List<DeptHistoryVO> insertvoList = new ArrayList<DeptHistoryVO>();
		for (int i = 0; i < mergedDeptVOs.length; i++) {
			mergedDeptVOs[i].setHrcanceled(UFBoolean.TRUE);
			mergedDeptVOs[i].setDeptcanceldate(wrapperVO.getTakeOverDeptHistoryVO().getEffectdate());
			// TODO 不知是否正确
			AggHRDeptVO savedVO = update(aggVOs[i], false);
			mergedDeptVOs[i] = (HRDeptVO) savedVO.getParentVO();
			resultList.add(savedVO);

			// 查询出所有的直接下级
			// **********暂时先不优化
			AggHRDeptVO[] childDeptAggVOs = hrDeptVOMap.get(mergedDeptVOs[i].getPk_dept());
			// **********

			/*
			 * HRDeptVO[] childDeptVOs =
			 * getOMCommonQueryService().queryByCondition(HRDeptVO.class,
			 * HRDeptVO.PK_FATHERORG + " = '" + mergedDeptVOs[i].getPk_dept() +
			 * "'");
			 */
			HRDeptVO[] childDeptVOs = (HRDeptVO[]) AggVOHelper.getParentVOArrayFromAggVOs(childDeptAggVOs,
					HRDeptVO.class);

			// 业务日志
			JFQueryOrgLogUtils.writeQueryDeptCopyLog("89788d4d-679b-42ac-b16f-e72381068f93", mergedDeptVOs[i],
					"MergerOpeDept");

			if (ArrayUtils.isEmpty(childDeptVOs)) {// 没有下级
				continue;
			}

			for (int j = 0; j < childDeptVOs.length; j++) {
				HRDeptVO childDeptVO = childDeptVOs[j];
				// 首先判断这些下级部门中是否包含被合并部门
				if (mergedDeptPKCache.contains(childDeptVO.getPk_dept())) {
					continue;
				}
				// 为下级部门创建部门变更历史，类型为转移

				DeptHistoryVO childHistoryVO = SuperVOHelper.createSuperVOFromSuperVO(childDeptVO, DeptHistoryVO.class);
				childHistoryVO.setChangetype(DeptChangeType.SHIFT);
				childHistoryVO.setEffectdate(wrapperVO.getTakeOverDeptHistoryVO().getEffectdate());
				childHistoryVO.setChangenum(changeNum);
				childHistoryVO.setIsreceived(UFBoolean.FALSE);
				childHistoryVO.setApprovenum(wrapperVO.getTakeOverDeptHistoryVO().getApprovenum());
				childHistoryVO.setApprovedept(wrapperVO.getTakeOverDeptHistoryVO().getApprovedept());
				childHistoryVO.setMemo(wrapperVO.getTakeOverDeptHistoryVO().getMemo());
				OrgVersionVO[] orgVersionVOs = orgVersionVOMap.get(childDeptVO.getPk_org());

				if (!ArrayUtils.isEmpty(orgVersionVOs)) {
					childHistoryVO.setPk_org_v(orgVersionVOs[0].getPk_vid());
				}
				insertvoList.add(childHistoryVO);
				// **********
				// **********
				childDeptVO.setPk_fatherorg(wrapperVO.getTakeOverDeptHistoryVO().getPk_dept());
				// **********
				AggHRDeptVO childUpdatedVO = update(childDeptAggVOs[j], false);
				// **********

				resultList.add(childUpdatedVO);
			}
		}
		getDeptDao().getBaseDAO().insertVOArray(insertvoList.toArray(new DeptHistoryVO[0]));
		// step 2. 处理被合并部门的岗位
		// 将被合并部门的岗位设成撤销

		// // 被合并部门的岗位全部撤销，重新从数据库查找
		// InSQLCreator isc = new InSQLCreator();
		//
		// String deptPks = isc.getInSQL(mergedDeptPKs);
		// String conditon =
		// PostVO.PK_DEPT + " in (" + deptPks + ") and (" + PostVO.HRCANCELED +
		// " = 'N' or " +
		// PostVO.HRCANCELED + " = '~' )";
		// // 增加排序，先撤销下级岗位
		// conditon += " order by " + PostVO.INNERCODE + " desc";
		// @SuppressWarnings("unchecked")
		// List<AggPostVO> postList = (List<AggPostVO>)
		// getMDQueryService().queryBillOfVOByCond(PostVO.class,
		// conditon, false);
		// if (postList != null && postList.size() > 0)
		// {
		// for (AggPostVO aggPostVO : postList)
		// {
		// // *********没法优化貌似********
		// getPostManageService()
		// .cancel((PostVO) aggPostVO.getParentVO(),
		// wrapperVO.getTakeOverDeptHistoryVO().getEffectdate(),
		// false);
		// }
		// }
		AggHRDeptVO[] resultVOs = resultList.toArray(new AggHRDeptVO[0]);

		// *********里面有循环查询******
		// 处理子表
		handleDeptHistory(resultVOs);

		return resultVOs;

	}

	/**
	 * 撤消部门下的岗位
	 * 
	 * @author heqiaoa
	 * @throws BusinessException
	 */
	private void cancelPostsOfDept(String[] mergedDeptPKs, UFLiteralDate effectiveDate) throws BusinessException {
		// 被合并部门的岗位全部撤销，重新从数据库查找
		InSQLCreator isc = new InSQLCreator();

		String deptPks = isc.getInSQL(mergedDeptPKs);
		String conditon = PostVO.PK_DEPT + " in (" + deptPks + ") and (" + PostVO.HRCANCELED + " = 'N' or "
				+ PostVO.HRCANCELED + " = '~' )";
		// 增加排序，先撤销下级岗位
		conditon += " order by " + PostVO.INNERCODE + " desc";
		@SuppressWarnings("unchecked")
		List<AggPostVO> postList = (List<AggPostVO>) getMDQueryService().queryBillOfVOByCond(PostVO.class, conditon,
				false);
		if (postList != null && postList.size() > 0) {
			for (AggPostVO aggPostVO : postList) {
				// *********没法优化貌似********
				getPostManageService().cancel((PostVO) aggPostVO.getParentVO(), effectiveDate, false);
			}
		}
	}

	private void setPostToNull(PsnJobVO psnjobVO) {
		psnjobVO.setPk_post(null);
		psnjobVO.setPk_postseries(null);
	}

	private void setJobToNull(PsnJobVO psnjobVO) {
		psnjobVO.setPk_job(null);
		psnjobVO.setPk_jobgrade(null);
		psnjobVO.setPk_jobrank(null);
		psnjobVO.setSeries(null);
	}

	@Override
	public AggHRDeptVO[] cancel(AggHRDeptVO aggvo, DeptHistoryVO historyVO, boolean disableDept)
			throws BusinessException {
		HRDeptVO vo = (HRDeptVO) aggvo.getParentVO();

		Map<String, AggHRDeptVO> deptPkAggHRDeptVOMap = new HashMap<String, AggHRDeptVO>();
		AggHRDeptVO[] childAggDeptVOs = queryChildDeptVOs(vo.getPk_dept(), HRDeptVO.INNERCODE);
		if (!ArrayUtils.isEmpty(childAggDeptVOs)) {
			for (AggHRDeptVO aggHRDeptVO : childAggDeptVOs) {
				deptPkAggHRDeptVOMap.put(aggHRDeptVO.getParentVO().getPrimaryKey(), aggHRDeptVO);
			}
		}
		HRDeptVO[] childDeptVOs = (HRDeptVO[]) AggVOHelper.getParentVOArrayFromAggVOs(childAggDeptVOs, HRDeptVO.class);
		HRDeptVO[] eventVOs = null;
		if (ArrayUtils.isEmpty(childDeptVOs)) {
			eventVOs = new HRDeptVO[] { vo };
		} else {
			List<HRDeptVO> eventDeptVOs = new ArrayList<HRDeptVO>();
			eventDeptVOs.add(vo);
			for (int i = 0; i < childDeptVOs.length; i++) {
				if (UFBoolean.TRUE.equals(childDeptVOs[i].getHrcanceled())) {
					continue;
				}
				eventDeptVOs.add(childDeptVOs[i]);
			}

			eventVOs = eventDeptVOs.toArray(new HRDeptVO[0]);
		}
		// 通过监听实现
		// 如果该部门下人员正处于单据状态为：
		// 编写中、已提交、审核中的入职申请单、转正申请单、调配申请单、离职申请单、定调资申请单、
		// 调班申请单、出差申请单、休假申请单、加班申请单，则不能执行。
		// 检验当前部门和下级部门是否包含在职人员，如果包含，那么不允许撤销
		EventDispatcher.fireEvent(new BusinessEvent(docName, IOMEventType.DEPT_CANCEL_BEFORE, eventVOs));

		// 获取一个变更操作号
		historyVO.setChangenum(OidGenerator.getInstance().nextOid(OidGenerator.GROUP_PK_CORP));

		// 维护变更操作后所属组织版本ID更行。
		String conOrg = OrgVersionVO.PK_ORG + " = '" + historyVO.getPk_org() + "' order by " + OrgVersionVO.VNO
				+ " desc";
		OrgVersionVO[] orgVersionVOs = getOMCommonQueryService().queryByCondition(OrgVersionVO.class, conOrg);
		if (!ArrayUtils.isEmpty(orgVersionVOs)) {
			historyVO.setPk_org_v(orgVersionVOs[0].getPk_vid());
		}
		// 更改查询标志
		vo.setHrcanceled(UFBoolean.TRUE);
		// 撤销日期
		vo.setDeptcanceldate(historyVO.getEffectdate());

		List<DeptHistoryVO> historyVOCache = new ArrayList<DeptHistoryVO>();
		List<HRDeptVO> deptVOCache = new ArrayList<HRDeptVO>();
		historyVOCache.add(historyVO);
		deptVOCache.add(vo);
		deptPkAggHRDeptVOMap.put(vo.getPrimaryKey(), aggvo);

		// step 1. 构造所有子部门的变更历史VO
		if (!ArrayUtils.isEmpty(childDeptVOs)) {
			for (HRDeptVO childDeptVO : childDeptVOs) {
				if (UFBoolean.TRUE.equals(childDeptVO.getHrcanceled())) {// 如果部门已经撤销，不需要处理
					continue;
				}
				childDeptVO.setHrcanceled(UFBoolean.TRUE);
				childDeptVO.setDeptcanceldate(historyVO.getEffectdate());
				DeptHistoryVO childHistoryVO = SuperVOHelper.createSuperVOFromSuperVO(childDeptVO, DeptHistoryVO.class);

				// 记录所属组织的最新版本ID。
				String conOrgCancel = OrgVersionVO.PK_ORG + " = '" + childDeptVO.getPk_org() + "' order by "
						+ OrgVersionVO.VNO + " desc";
				OrgVersionVO[] orgVersionCanVOs = getOMCommonQueryService().queryByCondition(OrgVersionVO.class,
						conOrgCancel);
				if (!ArrayUtils.isEmpty(orgVersionVOs)) {
					childHistoryVO.setPk_org_v(orgVersionCanVOs[0].getPk_vid());
				}
				childHistoryVO.setChangetype(DeptChangeType.HRCANCELED);
				childHistoryVO.setIsreceived(UFBoolean.FALSE);
				String[] attrs = new String[] { DeptHistoryVO.CHANGENUM, DeptHistoryVO.APPROVENUM,
						DeptHistoryVO.APPROVEDEPT, DeptHistoryVO.EFFECTDATE, DeptHistoryVO.MEMO };
				SuperVOHelper.batchSyncCopyAttributes(new SuperVO[] { historyVO }, new SuperVO[] { childHistoryVO },
						attrs);
				historyVOCache.add(childHistoryVO);
				deptVOCache.add(childDeptVO);
			}
		}

		// 记录部门变更历史
		getDeptDao().getBaseDAO().insertVOArray(historyVOCache.toArray(new DeptHistoryVO[historyVOCache.size()]));

		// step 2. 撤销岗位
		// 查询出所有岗位
		Set<PostVO> postVOCache = new HashSet<PostVO>();
		for (HRDeptVO deptVO : deptVOCache) {
			PostVO[] postVOs = getPostQueryService().queryPostVOsByDeptPK(deptVO.getPk_dept(), false);
			Collections.addAll(postVOCache, postVOs);
		}
		// 岗位排序，先撤销下级岗位
		PostVO[] postVOs = postVOCache.toArray(new PostVO[0]);
		SuperVOHelper.sort(postVOs, new String[] { PostVO.INNERCODE }, new boolean[] { false }, true);

		if (!ArrayUtils.isEmpty(postVOs)) {
			for (PostVO postVO : postVOs) {
				if (UFBoolean.FALSE.equals(postVO.getHrcanceled())) {
					getPostManageService().cancel(postVO, historyVO.getEffectdate(), false);
				}
			}
		}

		// step 3. 执行撤销
		List<AggHRDeptVO> resultVO = new ArrayList<AggHRDeptVO>();

		HRDeptVO[] canceledDeptVOs = deptVOCache.toArray(new HRDeptVO[0]);
		SuperVOHelper.sort(canceledDeptVOs, new String[] { HRDeptVO.INNERCODE }, new boolean[] { false }, true);

		for (HRDeptVO deptVO : canceledDeptVOs) {
			// 从map中取值然后修改，不会没有的，如果没有那么就抛异常吧
			AggHRDeptVO updateDeptVO = update(deptPkAggHRDeptVOMap.get(deptVO.getPk_dept()), false);
			/*
			 * 撤销时不执行停用 // 如果执行停用 if (disableDept) { if (((HRDeptVO)
			 * updateDeptVO.getParentVO()).getEnablestate() ==
			 * IPubEnumConst.ENABLESTATE_ENABLE) { TreeBaseService<DeptVO>
			 * baseService = new
			 * TreeBaseService<DeptVO>(IOrgMetaDataIDConst.DEPT,
			 * ITreePersistenceUtil.METADATA_PERSISTENCE_STYLE);
			 * baseService.disableVO((DeptVO) updateDeptVO.getParentVO()); } }
			 */
			resultVO.add(updateDeptVO);
		}

		// step 4. 对兼职人员进行处理，将终止标志和终止日期改变
		DeptCancelInfoVO cancelInfoVO = new DeptCancelInfoVO();
		cancelInfoVO.setEffectDate(historyVO.getEffectdate());
		cancelInfoVO.setDeptVOs(eventVOs);
		BusinessEvent afterEvent = new BusinessEvent(docName, IOMEventType.DEPT_CANCEL_AFTER, cancelInfoVO);
		EventDispatcher.fireEvent(afterEvent);

		return resultVO.toArray(new AggHRDeptVO[resultVO.size()]);
	}

	@Override
	public AggHRDeptVO[] uncancel(AggHRDeptVO aggVO, DeptHistoryVO historyVO, boolean includeChildDept,
			boolean includePost, boolean enableDept) throws BusinessException {
		HRDeptVO vo = (HRDeptVO) aggVO.getParentVO();
		// 判断上级部门是否处于撤销状态
		if (vo.getPk_fatherorg() != null) {
			HRDeptVO parentDeptVO = getOMCommonQueryService().queryByPK(HRDeptVO.class, vo.getPk_fatherorg());
			if (UFBoolean.TRUE.equals(parentDeptVO.getHrcanceled())) {
				throw new BusinessException(ResHelper.getString("6005dept", "06005dept0347")/*
																							 * @
																							 * res
																							 * "该部门上级部门的当前状态是撤销，请先对上级部门进行取消撤销操作！"
																							 */);
			}
		}

		Map<String, AggHRDeptVO> deptPkAggHRDeptVOMap = new HashMap<String, AggHRDeptVO>();

		// 获取一个变更操作号
		historyVO.setChangenum(OidGenerator.getInstance().nextOid(OidGenerator.GROUP_PK_CORP));

		// 维护变更操作后所属组织版本ID更行。
		String conOrg = OrgVersionVO.PK_ORG + " = '" + historyVO.getPk_org() + "' order by " + OrgVersionVO.VNO
				+ " desc";
		OrgVersionVO[] orgVersionVOs = getOMCommonQueryService().queryByCondition(OrgVersionVO.class, conOrg);
		if (!ArrayUtils.isEmpty(orgVersionVOs)) {
			historyVO.setPk_org_v(orgVersionVOs[0].getPk_vid());
		}
		List<DeptHistoryVO> historyVOCache = new ArrayList<DeptHistoryVO>();
		List<HRDeptVO> deptVOCache = new ArrayList<HRDeptVO>();
		historyVOCache.add(historyVO);
		vo.setHrcanceled(UFBoolean.FALSE);
		vo.setDeptcanceldate(null);
		deptVOCache.add(vo);
		deptPkAggHRDeptVOMap.put(vo.getPrimaryKey(), aggVO);

		// step 1. 构造所有子部门的变更历史VO
		if (includeChildDept) {
			AggHRDeptVO[] childAggDeptVOs = queryChildDeptVOs(vo.getPk_dept(), HRDeptVO.INNERCODE);
			if (!ArrayUtils.isEmpty(childAggDeptVOs)) {
				for (AggHRDeptVO aggHRDeptVO : childAggDeptVOs) {
					deptPkAggHRDeptVOMap.put(aggHRDeptVO.getParentVO().getPrimaryKey(), aggHRDeptVO);
				}
				HRDeptVO[] childDeptVOs = (HRDeptVO[]) AggVOHelper.getParentVOArrayFromAggVOs(childAggDeptVOs,
						HRDeptVO.class);

				for (HRDeptVO childDeptVO : childDeptVOs) {
					childDeptVO.setHrcanceled(UFBoolean.FALSE);
					childDeptVO.setDeptcanceldate(null);
					DeptHistoryVO childHistoryVO = SuperVOHelper.createSuperVOFromSuperVO(childDeptVO,
							DeptHistoryVO.class);

					// 更新部门所属组织的版本ID。
					String conOrgUncan = OrgVersionVO.PK_ORG + " = '" + childHistoryVO.getPk_org() + "' order by "
							+ OrgVersionVO.VNO + " desc";
					OrgVersionVO[] orgVersionUncanVOs = getOMCommonQueryService().queryByCondition(OrgVersionVO.class,
							conOrgUncan);
					if (!ArrayUtils.isEmpty(orgVersionVOs)) {
						childHistoryVO.setPk_org_v(orgVersionUncanVOs[0].getPk_vid());
					}
					childHistoryVO.setChangetype(DeptChangeType.HRUNCANCELED);
					childHistoryVO.setIsreceived(UFBoolean.FALSE);
					String[] attrs = new String[] { DeptHistoryVO.CHANGENUM, DeptHistoryVO.APPROVENUM,
							DeptHistoryVO.APPROVEDEPT, DeptHistoryVO.EFFECTDATE, DeptHistoryVO.MEMO };
					SuperVOHelper.batchSyncCopyAttributes(new SuperVO[] { historyVO },
							new SuperVO[] { childHistoryVO }, attrs);
					historyVOCache.add(childHistoryVO);
					deptVOCache.add(childDeptVO);
				}
			}
		}

		getDeptDao().getBaseDAO().insertVOArray(historyVOCache.toArray(new DeptHistoryVO[0]));

		// step 3. 执行部门反撤销
		List<AggHRDeptVO> resultVO = new ArrayList<AggHRDeptVO>();

		for (HRDeptVO deptVO : deptVOCache) {
			AggHRDeptVO updateDeptVO = update(deptPkAggHRDeptVOMap.get(deptVO.getPk_dept()), false);
			// 如果执行启用
			if (enableDept) {
				if (((HRDeptVO) updateDeptVO.getParentVO()).getEnablestate() != IPubEnumConst.ENABLESTATE_ENABLE) {
					updateDeptVO = enable(updateDeptVO);
				}
			}
			resultVO.add(updateDeptVO);
		}

		// step 2. 处理部门下的所有岗位
		if (includePost) {
			PostVO[] postVOs = getPostQueryService().queryPostVOsByDeptPK(vo.getPk_dept(), includeChildDept);
			if (!ArrayUtils.isEmpty(postVOs)) {
				for (PostVO postVO : postVOs) {
					if (UFBoolean.TRUE.equals(postVO.getHrcanceled())) {
						getPostManageService().unCancel(postVO, historyVO.getEffectdate(), false);
					}
				}
			}
		}

		return resultVO.toArray(new AggHRDeptVO[resultVO.size()]);
	}

	/**
	 * 查询转移部门信息
	 * 
	 * @param ruleVO
	 * @param refNameVOs
	 * @param context
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public DeptTransDeptVO[] queryTransDeptInf(DeptTransRuleVO ruleVO, DeptTransRefNameVO[] refNameVOs,
			LoginContext context) throws BusinessException {
		//
		if (ruleVO == null || StringUtils.isEmpty(ruleVO.getOldorg()) || StringUtils.isEmpty(ruleVO.getNeworg()))
			return null;
		//
		DeptTransDeptVO[] transDeptVOs = null;

		transDeptVOs = queryTransDeptInf(ruleVO, refNameVOs);
		if (transDeptVOs != null) {
			// 查询岗位信息
			for (DeptTransDeptVO transDeptVO : transDeptVOs) {
				transDeptVO.setTransPostVOs(queryTransPostInf(transDeptVO, ruleVO, refNameVOs, context));
			}
		}

		return transDeptVOs;
	}

	/**
	 * 查询转移部门信息
	 * 
	 * @param ruleVO
	 * @param refAggVOs
	 * @return DeptTransDeptVO[]
	 * @throws BusinessException
	 */
	private DeptTransDeptVO[] queryTransDeptInf(DeptTransRuleVO ruleVO, DeptTransRefNameVO[] refNameVOs)
			throws BusinessException {
		// 转移部门主键临时表
		InSQLCreator isc = new InSQLCreator();
		String temptable = isc.getInSQL(ruleVO.getPk_depts());
		HRDeptVO[] deptVOs;
		AggHRDeptVO[] aggDeptVOs = queryByCondition(null, " pk_dept in (" + temptable + " )");

		if (aggDeptVOs == null || aggDeptVOs.length < 1) {
			return null;
		}
		HROrgVO hrOrgVO = getServiceTemplate().queryByPk(HROrgVO.class, ruleVO.getNeworg());
		HRDeptVO supDeptVO = null;
		if (!StringUtils.isEmpty(ruleVO.getAimdept()))
			supDeptVO = getServiceTemplate().queryByPk(HRDeptVO.class, ruleVO.getAimdept());
		deptVOs = (HRDeptVO[]) AggVOHelper.getParentVOArrayFromAggVOs(aggDeptVOs, HRDeptVO.class);
		DeptTransDeptVO transDeptVO = null;
		List<DeptTransDeptVO> transDeptVOs = new ArrayList<DeptTransDeptVO>();
		for (HRDeptVO deptVO : deptVOs) {
			transDeptVO = new DeptTransDeptVO();
			String deptcode = deptVO.getCode();
			transDeptVO.setOlddeptcode(deptcode);
			// 目标部门编码 = 规则定义目标部门编码前缀 + 原部门编码
			String prefixcode = getCodePrefix(ruleVO.getAimdeptcode(), hrOrgVO, supDeptVO);
			if (prefixcode != null)
				transDeptVO.setAimdeptcode(prefixcode + deptcode);
			else
				transDeptVO.setAimdeptcode(deptcode);

			String deptname = deptVO.getMultilangName();
			transDeptVO.setOlddeptname(deptname);

			// 目标部门编码 = 规则定义目标部门编码前缀 + 原部门编码
			transDeptVO = getNamePrefix(ruleVO.getAimdeptname(), hrOrgVO, supDeptVO, transDeptVO, deptVO);
			String pk_dept = deptVO.getPk_dept();
			transDeptVO.setOlddeptpk(pk_dept == null ? null : pk_dept.trim());

			//
			transDeptVO.setAimdept(ruleVO.getAimdept());
			//
			transDeptVO.setTransdata(ruleVO.getTransdata());

			transDeptVOs.add(transDeptVO);
		}

		return transDeptVOs.toArray(new DeptTransDeptVO[0]);
	}

	/**
	 * 获得编码前缀<br>
	 * 
	 * @param prefixcode
	 * @param hrOrgVO
	 * @param hrDeptVO
	 * @return
	 */
	private String getCodePrefix(String prefixcode, HROrgVO hrOrgVO, HRDeptVO supDeptVO) {
		//
		if (prefixcode == null || StringUtils.isEmpty(prefixcode.toString())) {
			return StringUtils.EMPTY;
		} else if (ResHelper.getString("6005dept", "06005dept0180")/*
																	 * @res
																	 * "目标业务单元编码"
																	 */.equals(prefixcode)) {
			// 目标业务单元编码
			return hrOrgVO.getCode();
		} else if (ResHelper.getString("6005dept", "06005dept0181")/*
																	 * @res
																	 * "目标业务单元中上级部门编码"
																	 */.equals(prefixcode) && supDeptVO != null) {
			// 目标业务单元中上级部门编码
			return supDeptVO.getCode();
		} else {
			// 自定义业务单元编码
			return prefixcode;
		}
	}

	/**
	 * 处理名称前缀<br>
	 * 
	 * @param wizardModel
	 * @param unitname
	 * @param shortname
	 * @return
	 */
	private DeptTransDeptVO getNamePrefix(String prefixname, HROrgVO hrOrgVO, HRDeptVO supDeptVO,
			DeptTransDeptVO transDeptVO, HRDeptVO deptVO) {
		//
		if (prefixname == null || StringUtils.isEmpty(prefixname.toString())) {
			String[] srcAttrs = new String[] { HRDeptVO.NAME };
			String[] desAttrs = new String[] { DeptTransDeptVO.AIMDEPTNAME };
			SuperVOHelper.copyMultiLangAttribute(deptVO, transDeptVO, srcAttrs, desAttrs);
			return transDeptVO;
		} else if (ResHelper.getString("6005dept", "06005dept0183")/*
																	 * @res
																	 * "目标业务单元名称"
																	 */.equals(prefixname)) {
			// 目标业务单元名称
			transDeptVO.setAimdeptname(join(hrOrgVO.getName(), deptVO.getName()));
			transDeptVO.setAimdeptname2(join(hrOrgVO.getName2(), deptVO.getName2()));
			transDeptVO.setAimdeptname3(join(hrOrgVO.getName3(), deptVO.getName3()));
			transDeptVO.setAimdeptname4(join(hrOrgVO.getName4(), deptVO.getName4()));
			transDeptVO.setAimdeptname5(join(hrOrgVO.getName5(), deptVO.getName5()));
			transDeptVO.setAimdeptname6(join(hrOrgVO.getName6(), deptVO.getName6()));
		} else if (ResHelper.getString("6005dept", "06005dept0184")/*
																	 * @res
																	 * "目标业务单元简称"
																	 */.equals(prefixname)) {
			// 目标业务单元简称
			transDeptVO.setAimdeptname(join(hrOrgVO.getShortname(), deptVO.getName()));
			transDeptVO.setAimdeptname2(join(hrOrgVO.getShortname2(), deptVO.getName2()));
			transDeptVO.setAimdeptname3(join(hrOrgVO.getShortname3(), deptVO.getName3()));
			transDeptVO.setAimdeptname4(join(hrOrgVO.getShortname4(), deptVO.getName4()));
			transDeptVO.setAimdeptname5(join(hrOrgVO.getShortname5(), deptVO.getName5()));
			transDeptVO.setAimdeptname6(join(hrOrgVO.getShortname6(), deptVO.getName6()));
		} else if (ResHelper.getString("6005dept", "06005dept0185")/*
																	 * @res
																	 * "目标业务单元中上级部门名称"
																	 */.equals(prefixname)) {
			// 目标业务单元中上级部门名称
			if (supDeptVO == null) {
				String[] srcAttrs = new String[] { HRDeptVO.NAME };
				String[] desAttrs = new String[] { DeptTransDeptVO.AIMDEPTNAME };
				SuperVOHelper.copyMultiLangAttribute(deptVO, transDeptVO, srcAttrs, desAttrs);
			} else {
				transDeptVO.setAimdeptname(join(supDeptVO.getName(), deptVO.getName()));
				transDeptVO.setAimdeptname2(join(supDeptVO.getName2(), deptVO.getName2()));
				transDeptVO.setAimdeptname3(join(supDeptVO.getName3(), deptVO.getName3()));
				transDeptVO.setAimdeptname4(join(supDeptVO.getName4(), deptVO.getName4()));
				transDeptVO.setAimdeptname5(join(supDeptVO.getName5(), deptVO.getName5()));
				transDeptVO.setAimdeptname6(join(supDeptVO.getName6(), deptVO.getName6()));
			}
		} else {
			// 自定义目标业务单元名称
			transDeptVO.setAimdeptname(join(prefixname, deptVO.getName()));
			transDeptVO.setAimdeptname2(join(prefixname, deptVO.getName2()));
			transDeptVO.setAimdeptname3(join(prefixname, deptVO.getName3()));
			transDeptVO.setAimdeptname4(join(prefixname, deptVO.getName4()));
			transDeptVO.setAimdeptname5(join(prefixname, deptVO.getName5()));
			transDeptVO.setAimdeptname6(join(prefixname, deptVO.getName6()));
		}

		return transDeptVO;
	}

	/**
	 * 处理名称前缀<br>
	 * 
	 * @param wizardModel
	 * @param unitname
	 * @param shortname
	 * @return
	 */
	private String join(String prefixname, String name) {
		String retName = null;
		if (StringUtils.isEmpty(prefixname)) {
			return name;
		} else {
			retName = prefixname;
			if (!StringUtils.isEmpty(name)) {
				retName += name;
			}
		}
		return retName;
	}

	/**
	 * 查询转移岗位信息
	 * 
	 * @param transDeptVO
	 * @param ruleVO
	 * @param refAggVOs
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
	public DeptTransPostVO[] queryTransPostInf(DeptTransDeptVO transDeptVO, DeptTransRuleVO ruleVO,
			DeptTransRefNameVO[] refNameVOs, LoginContext context) throws BusinessException {
		//
		PostVO[] postVOs;
		AggPostVO[] aggPostVOs = getServiceTemplate().queryByCondition(AggPostVO.class,
				" pk_dept =  '" + transDeptVO.getOlddeptpk() + "' and " + PostVO.HRCANCELED + " = 'N'");

		if (aggPostVOs == null || aggPostVOs.length < 1) {
			return null;
		}
		postVOs = (PostVO[]) AggVOHelper.getParentVOArrayFromAggVOs(aggPostVOs, PostVO.class);

		DeptTransPostVO transPostVO = null;
		// 根据系统参数配置选择目标岗位是否采用自动编码
		List<DeptTransPostVO> transPostVOs = new ArrayList<DeptTransPostVO>();
		for (PostVO postVO : postVOs) {
			transPostVO = new DeptTransPostVO();
			// if (isAutoGenCode) {// 自动生成目标岗位编码
			// IHrBillCode IHrBillCode =
			// NCLocator.getInstance().lookup(IHrBillCode.class);
			// String autoJobCode = IHrBillCode.getBillCode("STA",
			// transDeptVO.getOlddeptpk(), userId);
			// transPostVO.setAimPostCode(autoJobCode);
			// } else {
			// transPostVO.setAimPostCode(postcode);
			// }
			transPostVO.setAimPostCode(postVO.getPostcode());

			transPostVO.setAimPostName(postVO.getMultilangName());
			transPostVO.setOldPostpk(postVO.getPk_post());

			transPostVO.setPostseries(postVO.getPk_postseries());
			//
			transPostVO.setOlddeptpk(transDeptVO.getOlddeptpk());
			//
			transPostVO.setNewdeptname(transDeptVO.getAimdeptname());
			//
			transPostVO.setTransdata(ruleVO.getTransdata());

			transPostVOs.add(transPostVO);
		}
		return transPostVOs.toArray(new DeptTransPostVO[0]);

	}

	/**
	 * 查询转移部门时需要匹配的参照信息
	 * 
	 * @param ruleVO
	 * @return DeptTransRefNameVO[]
	 * @throws BusinessException
	 */
	@Override
	public DeptTransRefNameVO[] queryDeptTransRefInf(DeptTransRuleVO ruleVO) throws BusinessException {

		//
		if (ruleVO == null || StringUtils.isEmpty(ruleVO.getOldorg()) || StringUtils.isEmpty(ruleVO.getNeworg()))
			return null;
		//
		DeptTransRefNameVO[] refNameVOs = null;
		List<DeptTransRefNameVO> listRefNameVOs = new ArrayList<DeptTransRefNameVO>();
		try {
			DeptTransRefNameVO refNameVO = null;
			// FIXME 人员类别、部门的部门级别、岗位序列、调配项目设置的自定义档案参照为全局级&集团级参照，不需要设置对应关系

			// 职务
			refNameVO = getDeptDao().queryTransDutyRefNameVO(ruleVO);
			if (refNameVO != null)
				listRefNameVOs.add(refNameVO);

		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		if (listRefNameVOs.size() > 0)
			refNameVOs = listRefNameVOs.toArray(new DeptTransRefNameVO[0]);
		return refNameVOs;
	}

	/**
	 * 通过参照主键获取对照参照主键
	 */
	@SuppressWarnings("unused")
	private String getDefdocPk(DeptTransRefNameVO[] refNameVOs, String defdoclist, String defdocPk) {
		if (StringUtils.isEmpty(defdocPk) || StringUtils.isEmpty(defdoclist) || refNameVOs == null)
			return defdocPk;
		for (DeptTransRefNameVO refNameVO : refNameVOs) {
			if (defdoclist.equals(refNameVO.getPkDoc())) {
				DeptTransRefInfVO[] refInfVOs = refNameVO.getTransRefInfVOs();
				for (DeptTransRefInfVO refInfVO : refInfVOs) {
					if (defdocPk.equals(refInfVO.getOldPK())) {
						return refInfVO.getNewPK();
					}
				}
			}
		}

		return defdocPk;
	}

	/**
	 * 为部门复制校验部门的编码名称唯一性<br>
	 * 
	 * @param copyConfirmVOs
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public DeptCopyValidateVO validateDeptForCopy(DeptCopyConfirmVO[] copyConfirmVOs) throws BusinessException {
		// FIXME 通过唯一校验规则实现
		DeptCopyValidateVO validateVO = new DeptCopyValidateVO();
		for (DeptCopyConfirmVO copyConfirmVO : copyConfirmVOs) {
			DeptCopyAdjustVO[] copyAdjustVOs = copyConfirmVO.getTargetDeptVOs();

			// 业务单元主键
			String pk_org = copyConfirmVO.getPk_org();
			// 部门编码
			String[] deptcodes = SuperVOHelper.getAttributeValueArray(copyAdjustVOs, HRDeptVO.CODE, String.class);
			// 部门名称
			String[] deptnames = SuperVOHelper.getAttributeValueArray(copyAdjustVOs, HRDeptVO.NAME, String.class);
			// 部门主键
			String[] deptPKs = SuperVOHelper.getAttributeValueArray(copyAdjustVOs, HRDeptVO.PK_DEPT, String.class);

			// 判断编码、名称唯一性
			String[] codeErrorPKs = getDeptDao().deptCodeUniqueValidate(pk_org, deptcodes, deptPKs);
			String[] nameErrorPKs = getDeptDao().deptNameUniqueValidate(pk_org, deptnames, deptPKs);

			// 当前业务单元是否有唯一性错误
			boolean corpHasError = false;

			// 构建校验结果VO
			if (!ArrayUtils.isEmpty(codeErrorPKs)) {
				corpHasError = true;
				validateVO.addDuplicateCode(pk_org, codeErrorPKs);

			}
			if (!ArrayUtils.isEmpty(nameErrorPKs)) {
				corpHasError = true;
				validateVO.addDuplicateName(pk_org, nameErrorPKs);
			}

			if (corpHasError) {
				validateVO.addErrorCorp(copyConfirmVO.getUnitname());
			}

		}
		return validateVO;
	}

	/**
	 * 对跨业务单元部门转移目标部门编码和目标部门名称重复性校验
	 * 
	 * @param transDeptVos
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public DeptTransValidateVO validateDeptForTrans(DeptTransDeptVO[] transDeptVos, String pk_org)
			throws BusinessException {
		DeptTransValidateVO validateVO = new DeptTransValidateVO();

		HRDeptVO[] deptVOs;
		AggHRDeptVO[] aggDeptVOs = queryByCondition(null, "pk_org = '" + pk_org
				+ "' and enablestate = 2 and hrcanceled = 'N'");
		if (aggDeptVOs == null || aggDeptVOs.length < 1) {
			return validateVO;
		}
		deptVOs = (HRDeptVO[]) AggVOHelper.getParentVOArrayFromAggVOs(aggDeptVOs, HRDeptVO.class);

		// 重复的目标部门编码对应的pk_depts
		List<String> errorDeptCodes = new ArrayList<String>();
		// 重复的目标部门名称对应的pk_depts
		List<String> errorDeptNames = new ArrayList<String>();
		// 页面目标编码重复性验证
		for (int i = 0; i < transDeptVos.length; i++) {
			for (int j = 0; j < deptVOs.length; j++) {
				if (transDeptVos[i].getAimdeptcode().equals(deptVOs[j].getCode())) {
					errorDeptCodes.add(transDeptVos[i].getOlddeptpk());
					validateVO.addErrorDept(transDeptVos[i].getOlddeptname());
				}
				if (transDeptVos[i].getAimdeptname().equals(deptVOs[j].getName())) {
					errorDeptNames.add(transDeptVos[i].getOlddeptpk());
					validateVO.addErrorDept(transDeptVos[i].getOlddeptname());
				}
			}
		}
		if (!errorDeptCodes.isEmpty()) {
			validateVO.addDuplicateDeptCode(errorDeptCodes.toArray(new String[0]));
		}
		if (!errorDeptNames.isEmpty()) {
			validateVO.addDuplicateDeptName(errorDeptNames.toArray(new String[0]));
		}

		return validateVO;
	}

	/**
	 * 对跨业务单元部门转移目标岗位编码重复性校验
	 * 
	 * @param transDeptVos
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public DeptTransValidateVO validatePostForTrans(DeptTransDeptVO[] transDeptVos, String pk_org)
			throws BusinessException {
		DeptTransValidateVO validateVO = new DeptTransValidateVO();

		PostVO[] postVOs;
		AggPostVO[] aggPostVOs = getServiceTemplate().queryByCondition(
				AggPostVO.class,
				PostVO.PK_ORG + " = '" + pk_org + "' and " + PostVO.HRCANCELED + " = 'N'" + " order by len("
						+ PostVO.INNERCODE + ")," + PostVO.POSTCODE);

		if (aggPostVOs == null || aggPostVOs.length < 1) {
			return validateVO;
		}
		postVOs = (PostVO[]) AggVOHelper.getParentVOArrayFromAggVOs(aggPostVOs, PostVO.class);

		// 重复的目标岗位编码对应的原pk_posts
		List<String> errorJobCodes = new ArrayList<String>();

		for (int i = 0; i < transDeptVos.length; i++) {
			DeptTransPostVO[] jobvos = transDeptVos[i].getTransPostVOs();
			if (jobvos == null || jobvos.length < 1) {
				continue;
			}
			for (int j = 0; j < jobvos.length; j++) {
				for (int k = 0; k < postVOs.length; k++) {
					if (jobvos[j].getAimPostCode().equals(postVOs[k].getPostcode())) {
						errorJobCodes.add(jobvos[j].getOldPostpk());
						validateVO.addErrorPost(jobvos[j].getAimPostName());
					}
				}
			}
			if (!errorJobCodes.isEmpty()) {
				validateVO.addDuplicatePostCode(transDeptVos[i].getOlddeptpk(), errorJobCodes.toArray(new String[0]));
			}
		}

		return validateVO;
	}

	/**
	 * 校验选择的转移部门下是否存在未入档的人员
	 * 
	 * @param deptPks
	 * @return
	 * @throws Exception
	 */
	@Override
	public String validateTransDeptPsn(List<String> deptPks) throws BusinessException {
		StringBuilder message = new StringBuilder();
		List<String> errDeptNameList = getDeptDao().getNotIndocDeptPks(deptPks.toArray(new String[0]));

		String dept = "";
		for (String deptName : errDeptNameList) {
			dept += "," + deptName;
		}
		if (dept.length() > 1) {
			message.append(ResHelper.getString("6005dept", "06005dept0349", dept.substring(1))/*
																							 * @
																							 * res
																							 * "所选转移部门[{0}]存在未入档人员！"
																							 */);
		}

		String dept2 = "";
		// 校验是否有在活动中的单据的人
		List<String> deptNameList = getDeptDao().getBillCount(deptPks.toArray(new String[0]));
		for (String deptName : deptNameList) {
			dept2 += "," + deptName;
		}

		if (dept2.length() > 1) {
			if (message.length() > 0) {
				message.append('\n');
			}
			message.append(ResHelper.getString("6005dept", "06005dept0377"/*
																		 * @res
																		 * "被转移部门[{0}]的人员中存在未完成的单据！"
																		 */, dept2.substring(1)));
		}

		return message.toString();

	}

	/**
	 * 根据集团+组织+移动类型查询调配项目
	 * 
	 * @param pk_group
	 *            集团
	 * @param pk_org
	 *            组织
	 * @param trnstype
	 *            异动类型
	 * @return DeptTransItemVO[]
	 * @throws BusinessException
	 */
	@Override
	public DeptTransItemVO[] queryItemSetByOrg(String pk_group, String pk_org, String pk_trnstype)
			throws BusinessException {
		// 查询调配项目
		TrnTransItemVO[] trnTransItemVOs = TrnDelegator.getIItemSetQueryService().queryItemSetByOrg(
				TRNConst.TRNSITEM_BEANID, pk_group, pk_org, pk_trnstype);
		if (ArrayUtils.isEmpty(trnTransItemVOs))
			return null;
		List<DeptTransItemVO> transItemVOs = new ArrayList<DeptTransItemVO>();
		DeptTransItemVO deptTransItemVO;

		InfoSetVO infoSetVO = NCLocator.getInstance().lookup(IInfoSetQry.class)
				.queryInfoSetByPk(trnTransItemVOs[0].getPk_infoset());
		InfoItemVO[] itemVOs = infoSetVO.getInfo_item();
		for (TrnTransItemVO trnTransItemVO : trnTransItemVOs) {
			String itemkey = trnTransItemVO.getItemkey();
			if ("oldpk_org".equals(itemkey) || "newpk_org".equals(itemkey) || "oldpk_psncl".equals(itemkey)
					|| "newpk_psncl".equals(itemkey) || "oldpk_dept".equals(itemkey) || "newpk_dept".equals(itemkey)
					|| "oldpk_job".equals(itemkey) || "newpk_job".equals(itemkey) || "oldseries".equals(itemkey)
					|| "newseries".equals(itemkey) || "oldpk_post".equals(itemkey) || "newpk_post".equals(itemkey)
					|| "oldpk_postseries".equals(itemkey) || "newpk_postseries".equals(itemkey)
					|| "oldpk_jobrank".equals(itemkey) || "newpk_jobrank".equals(itemkey)
					|| "oldpk_jobgrade".equals(itemkey) || "newpk_jobgrade".equals(itemkey))
				continue;
			deptTransItemVO = SuperVOHelper.createSuperVOFromSuperVO(trnTransItemVO, DeptTransItemVO.class);
			dealTransItemInf(deptTransItemVO, itemVOs);
			transItemVOs.add(deptTransItemVO);
		}
		return transItemVOs.toArray(new DeptTransItemVO[0]);
	}

	/**
	 * 校验选择的转移部门下是否存在未入档的人员
	 * 
	 * @param deptPks
	 * @return
	 * @throws Exception
	 */
	private void dealTransItemInf(DeptTransItemVO deptTransItemVO, InfoItemVO[] itemVOs) throws BusinessException {
		InfoItemVO item = null;
		for (InfoItemVO itemVO : itemVOs) {
			if (deptTransItemVO.getPk_infoset_item().equals(itemVO.getPk_infoset_item())) {
				item = itemVO;
				break;
			}
		}

		if (item == null) {
			return;
		}

		deptTransItemVO.setDataType(item.getData_type());
		deptTransItemVO.setDisorder(item.getShoworder());
		String ref_model_name = item.getRef_model_name();
		String itemkey = deptTransItemVO.getItemkey();

		if (itemkey.startsWith("old")) {
			deptTransItemVO.setItemname(StringUtil.replaceAllString(
					ResHelper.getString("6005dept", "06005dept0350")/*
																	 * @ res
																	 * "原{0}"
																	 */, "{0}", deptTransItemVO.getItemname()));
		} else {
			deptTransItemVO.setItemname(StringUtil.replaceAllString(
					ResHelper.getString("6005dept", "06005dept0351")/*
																	 * @ res
																	 * "目标{0}"
																	 */, "{0}", deptTransItemVO.getItemname()));
		}

		RefInfoVO refInfoVO = (StringUtils.isBlank(ref_model_name) ? null : new SimpleDocServiceTemplate("refinfo")
				.queryByPk(RefInfoVO.class, ref_model_name));
		if (refInfoVO != null) {
			deptTransItemVO.setRefName(refInfoVO.getName());
		}

	}

	/**
	 * 查询转移人员信息
	 * 
	 * @param ruleVO
	 * @param refNameVOs
	 * @param transDeptVOs
	 * @param transItemVOs
	 * @return GeneralVO[]
	 * @throws BusinessException
	 */
	@Override
	public GeneralVO[] queryTransPsnInf(DeptTransRuleVO ruleVO, DeptTransRefNameVO[] refNameVOs,
			DeptTransDeptVO[] transDeptVOs, DeptTransItemVO[] transItemVOs) throws BusinessException {
		// 转移部门主键临时表
		InSQLCreator isc = new InSQLCreator();
		String temptable = isc.getInSQL(ruleVO.getPk_depts());
		String cond = " ismainjob = 'Y' and ( " + SQLHelper.getNullSql("endflag")
				+ " or endflag ='N' ) and psntype = 0 and " + PsnJobVO.PK_DEPT + " in (" + temptable + " )";

		PsnJobVO[] psnJobVOs = getServiceTemplate().queryByCondition(PsnJobVO.class, cond);

		if (ArrayUtils.isEmpty(psnJobVOs)) {
			return null;
		}
		GeneralVO transPsnJobVO;
		List<GeneralVO> psnInfVOs = new ArrayList<GeneralVO>();
		for (PsnJobVO psnJobVO : psnJobVOs) {
			transPsnJobVO = new GeneralVO();
			transPsnJobVO.setAttributeValue(PsnJobVO.PK_PSNJOB, psnJobVO.getPk_psnjob());
			transPsnJobVO.setAttributeValue(PsnJobVO.PK_PSNDOC, psnJobVO.getPk_psndoc());
			transPsnJobVO.setAttributeValue(DeptTransDeptVO.OLDDEPTPK, psnJobVO.getPk_dept());
			if (!ArrayUtils.isEmpty(transItemVOs))
				for (DeptTransItemVO transItemVO : transItemVOs) {
					// 根据调配项目设置
					if (transItemVO.getItemkey().startsWith("old") || transItemVO.getIsdefault().booleanValue())
						transPsnJobVO.setAttributeValue(transItemVO.getItemkey(),
								psnJobVO.getAttributeValue(transItemVO.getItemkey().substring(3)));

				}
			reSetPsnInVO(transPsnJobVO, psnJobVO, transDeptVOs);
			transPsnJobVO.setAttributeValue("oldpsncl", psnJobVO.getPk_psncl());
			transPsnJobVO.setAttributeValue("newpsncl", psnJobVO.getPk_psncl());
			transPsnJobVO.setAttributeValue(DeptTransPostVO.OLDPOSTPK, psnJobVO.getPk_post());

			psnInfVOs.add(transPsnJobVO);
		}
		return psnInfVOs.toArray(new GeneralVO[0]);
	}

	/**
     *
     */
	public void reSetPsnInVO(GeneralVO transPsnJobVO, PsnJobVO psnJobVO, DeptTransDeptVO[] transDeptVOs) {
		// 部门
		for (DeptTransDeptVO transDeptVO : transDeptVOs) {
			if (transDeptVO.getOlddeptpk().equals(psnJobVO.getPk_dept())) {
				transPsnJobVO.setAttributeValue(DeptTransDeptVO.AIMDEPTNAME, transDeptVO.getAimdeptname());
				DeptTransPostVO[] transPostVOs = transDeptVO.getTransPostVOs();
				if (ArrayUtils.isEmpty(transPostVOs))
					continue;
				for (DeptTransPostVO transPostVO : transPostVOs) {
					if (transPostVO.getOldPostpk().equals(psnJobVO.getPk_post())) {
						transPsnJobVO.setAttributeValue(DeptTransPostVO.AIMPOSTNAME, transPostVO.getMultilangName());
						break;
					}
				}
				break;
			}
		}
	}

	/**
	 * 执行跨业务单元部门转移
	 * 
	 * @param ruleVO
	 * @param refNameVOs
	 * @param transDeptVOs
	 * @param transItemVOs
	 * @param transPsnInfVOs
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public void doTransDeptInf(DeptTransRuleVO ruleVO, DeptTransRefNameVO[] refNameVOs, DeptTransDeptVO[] transDeptVOs,
			DeptTransItemVO[] transItemVOs, GeneralVO[] transPsnInfVOs) throws BusinessException {
		try {
			// 1.1 根据部门转移规则获得部门"模板"VO
			HRDeptVO[] sourceDeptVOs = buildTemplateDepts(ruleVO.getSourceDeptVOs());
			Map<String, String> deptPKBackupMap = new HashMap<String, String>();
			String fatherDeptPK = ruleVO.getAimdept();
			AggHRDeptVO[] newAggDeptVOs = new AggHRDeptVO[sourceDeptVOs.length];

			HRDeptVO deptVO;
			AggHRDeptVO aggHRDeptVO;
			// 1.2 插入部门信息到数据库
			for (int i = 0; i < sourceDeptVOs.length; i++) {
				String pk_dept = sourceDeptVOs[i].getPk_dept();
				deptVO = (HRDeptVO) sourceDeptVOs[i].clone();
				//
				reSetTransDeptInf(deptVO, ruleVO, refNameVOs, transDeptVOs);
				aggHRDeptVO = new AggHRDeptVO();
				aggHRDeptVO.setParentVO(deptVO);
				//
				if (deptVO.getPk_fatherorg() == null || !deptPKBackupMap.containsValue(deptVO.getPk_fatherorg()))
					deptVO.setPk_fatherorg(fatherDeptPK);
				// 执行插入
				newAggDeptVOs[i] = insert(aggHRDeptVO);
				HRDeptVO newDeptVO = (HRDeptVO) newAggDeptVOs[i].getParentVO();

				// 形成老部门PK与新部门PK的映射
				deptPKBackupMap.put(pk_dept, newDeptVO.getPk_dept());

				// 修改所有当前已经插入的部门的子部门的pk_fatherorg为最新的
				for (int j = i + 1; j < sourceDeptVOs.length; j++) {
					String pk_fatherorg = sourceDeptVOs[j].getPk_fatherorg();
					if (pk_fatherorg != null && pk_fatherorg.equals(pk_dept)) {// 修改所有子部门
						sourceDeptVOs[j].setPk_fatherorg(newDeptVO.getPk_dept());
					}
				}

			}

			// 2.1 获取数据库中的岗位信息
			// 新旧主键映射Map
			Map<String, String> old_newPostpkmap = new HashMap<String, String>();
			AggPostVO[] sourcePostVOs = buildTemplatePostVOs(sourceDeptVOs);
			reSetTransPostInf(sourcePostVOs, ruleVO, refNameVOs, transDeptVOs);
			// 2.2 插入岗位信息到数据库
			PostVO[] insertPostVOs = null;
			if (!ArrayUtils.isEmpty(sourcePostVOs)) {
				insertPostVOs = new PostVO[sourcePostVOs.length];

				savePostVOs(ruleVO.getNeworg(), ruleVO.getContext().getPk_group(), sourcePostVOs, deptPKBackupMap,
						old_newPostpkmap, insertPostVOs);
			}
			// 新旧人员工作记录主键
			Map<String, String> psndocPKMap = new HashMap<String, String>();
			// 新部门的主键List
			List<String> newDeptPk = new ArrayList<String>();

			// 3.1 转移在职人员
			doTransPsnInf(ruleVO, insertPostVOs, deptPKBackupMap, old_newPostpkmap, transItemVOs, transPsnInfVOs,
					psndocPKMap);

			// 3.2 兼职人员处理
			doTransPartPsnInf(ruleVO, insertPostVOs, deptPKBackupMap, old_newPostpkmap);

			// 3.3 离职人员处理，需求变更，离职人员不做处理
			// doTransDimissionPsnInf(ruleVO, deptPKBackupMap);
			// 3.4 相关人员处理
			doTransPoiPsnInf(ruleVO, deptPKBackupMap);
			// 转移的部门有岗位才进行转移后岗位的更新（更新岗位的上级岗位）
			if (newDeptPk.size() > 0) {
			}

			// 更新部门负责人的主键

			// 4.1撤销原业务单元部门、岗位
			cancelOldDeptHistoryVOs(ruleVO, newAggDeptVOs, deptPKBackupMap);
			// JFQueryOrgLogUtils.writeQueryDeptLog("780de20d-fac5-43f8-bf88-606eef74a02e",
			// sourceDeptVOs[0].getPk_org(), "OuterShiftOpeDept");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			if (e instanceof BusinessException)
				throw (BusinessException) e;
			throw new BusinessException(e.getMessage(), e);
		}
	}

	/**
	 * 获得新业务单元部门信息
	 * 
	 * @param deptdocVO
	 * @param ruleVO
	 * @param refAggVOs
	 * @param transDeptVOs
	 * @return
	 * @throws BusinessException
	 */
	public void reSetTransDeptInf(HRDeptVO deptdocVO, DeptTransRuleVO ruleVO, DeptTransRefNameVO[] refNameVOs,
			DeptTransDeptVO[] transDeptVOs) throws BusinessException {
		// 重新设定业务单元主键
		deptdocVO.setPk_org(ruleVO.getNeworg());

		deptdocVO.setModifier(null);
		deptdocVO.setModifiedtime(null);
		deptdocVO.setCreator(ruleVO.getContext().getPk_loginUser());
		deptdocVO.setCreationtime(new UFDateTime());

		if (transDeptVOs == null) {
			String errMsg = ResHelper.getString("6001uif2", "06001uif20011"
			/* @res "没有需要{0}的数据！" */, ResHelper.getString("common", "UC001-0000001")
			/* @res "保存" */);
			throw new BusinessException(errMsg);
		}

		// 重新设定编码、名称、建立日期
		for (DeptTransDeptVO transDeptVO : transDeptVOs) {
			if (transDeptVO.getOlddeptpk().equals(deptdocVO.getPk_dept())) {
				deptdocVO.setCode(transDeptVO.getAimdeptcode());
				SuperVOHelper.copyMultiLangAttribute(transDeptVO, deptdocVO,
						new String[] { DeptTransDeptVO.AIMDEPTNAME }, new String[] { HRDeptVO.NAME });
				deptdocVO.setCreatedate(transDeptVO.getTransdata());
				break;
			}
		}
		deptdocVO.setPk_dept(null);
		deptdocVO.setInnercode(null);
	}

	/**
	 * 获得新业务单元部门信息
	 * 
	 * @param deptdocVO
	 * @param ruleVO
	 * @param refAggVOs
	 * @param transDeptVOs
	 * @return
	 * @throws BusinessException
	 */
	public void reSetTransPostInf(AggPostVO[] sourcePostVOs, DeptTransRuleVO ruleVO, DeptTransRefNameVO[] refNameVOs,
			DeptTransDeptVO[] transDeptVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(sourcePostVOs))
			return;
		PostVO[] postVOs = SuperVOHelper.getParentVOArrayFromAggVOs(sourcePostVOs, PostVO.class);

		// 处理PostVO的属性
		for (PostVO postVO : postVOs) {
			for (DeptTransDeptVO transDeptVO : transDeptVOs) {
				DeptTransPostVO[] transPostVOs = transDeptVO.getTransPostVOs();
				// 原部门主键 不等于 岗位所在部门 或者 部门内没有岗位时 继续
				if (!transDeptVO.getOlddeptpk().equals(postVO.getPk_dept()) || ArrayUtils.isEmpty(transPostVOs))
					continue;

				for (DeptTransPostVO transPostVO : transPostVOs) {
					if (transPostVO.getOldPostpk().equals(postVO.getPk_post())) {
						postVO.setPostcode(transPostVO.getAimPostCode());
						SuperVOHelper.copyMultiLangAttribute(transPostVO, postVO,
								new String[] { DeptTransPostVO.AIMPOSTNAME }, new String[] { PostVO.POSTNAME });
						postVO.setPk_postseries(transPostVO.getPostseries());
						postVO.setBuilddate(transPostVO.getTransdata());
						break;
					}
				}
			}
		}
	}

	/**
	 * 插入岗位信息到数据库<br>
	 * 
	 * @param pk_org
	 * @param pk_group
	 * @param sourcePostVOs
	 * @param deptPKBackupMap
	 * @throws BusinessException
	 */
	private void savePostVOs(String pk_org, String pk_group, AggPostVO[] sourcePostVOs,
			Map<String, String> deptPKBackupMap, Map<String, String> old_newPostpkmap, PostVO[] insertPostVOs)
			throws BusinessException {
		// 首先对岗位进行排序，这样才能维护内部码
		List<AggPostVO> postVOList = new ArrayList<AggPostVO>();
		Collections.addAll(postVOList, sourcePostVOs);
		Collections.sort(postVOList, new Comparator<AggPostVO>() {

			@Override
			public int compare(AggPostVO aggVO1, AggPostVO aggVO2) {// 让岗位按照上下级的关系重新排列
																	// 注释中的上下级不代表直接上下级，是在树中的层级
				PostVO postVO1 = (PostVO) aggVO1.getParentVO();
				PostVO postVO2 = (PostVO) aggVO2.getParentVO();
				if (postVO1.getInnercode() == null) {// 当o1的上级岗位为空时，o2为空那么是平级，o2不为空为下级
					return postVO2.getInnercode() == null ? 0 : -1;
				}
				if (postVO2.getInnercode() == null) {// 当o1的上级岗位不为空时，o2为空，那么o1为o2的下级
					return 1;
				}
				return postVO1.getInnercode().compareTo(postVO2.getInnercode());
			}

		});
		boolean isAutoGenerateBillCode = isAutoGenerateBillCode("post", pk_group, pk_org);

		// 获得排序后的岗位
		sourcePostVOs = postVOList.toArray(new AggPostVO[0]);
		String prefix = "ZD" + PubEnv.getServerDate().toStdString();
		// 不自动生成单据号 /默认规则生成 "单据类型+yyyy-mm-dd+_流水号"
		String flowCode = getFlowCode(prefix);

		// 判断岗位直接上级是不是之前部门的岗位
		List<String> postPKs = new ArrayList<String>();
		for (AggPostVO aggPostVO : sourcePostVOs) {
			PostVO postVO = (PostVO) aggPostVO.getParentVO();
			postPKs.add(postVO.getPk_post());
		}
		for (AggPostVO aggPostVO : sourcePostVOs) {
			PostVO postVO = (PostVO) aggPostVO.getParentVO();
			if (!StringUtil.isEmpty(postVO.getSuporior()) && !postPKs.contains(postVO.getSuporior())) {
				postVO.setSuporior(null);
			}
		}

		String[] billcode = new String[sourcePostVOs.length];
		if (isAutoGenerateBillCode) {
			BillCodeContext billCodeContext = NCLocator.getInstance().lookup(IBillcodeManage.class)
					.getBillCodeContext("post", pk_group, pk_org);
			if (billCodeContext.isPrecode()) {
				billcode = NCLocator.getInstance().lookup(IHrBillCode.class)
						.getBillCode(IOrgBillCodeConst.BILL_CODE_POST, pk_group, pk_org, sourcePostVOs.length);
			} else {
				billcode = NCLocator
						.getInstance()
						.lookup(IHrBillCode.class)
						.getLeveledBillCode("post", pk_group, pk_org, (PostVO) sourcePostVOs[0].getParentVO(),
								sourcePostVOs.length);
			}
		}

		List<AggPostVO> aggPostVOList = new ArrayList<AggPostVO>();
		String[] toBeCopiedPostPK = new String[sourcePostVOs.length];
		for (int i = 0; i < sourcePostVOs.length; i++) {
			PostVO toBeCopiedPostVO = (PostVO) sourcePostVOs[i].getParentVO();

			// 设定属于的新的组织单元
			toBeCopiedPostVO.setPk_org(pk_org);
			//
			toBeCopiedPostVO.setPk_hrorg(pk_org);
			//
			String pk_post = toBeCopiedPostVO.getPk_post();
			toBeCopiedPostPK[i] = pk_post;

			toBeCopiedPostVO.setPk_post(null);
			toBeCopiedPostVO.setInnercode(null);

			// 更新岗位的部门主键
			String pk_dept = toBeCopiedPostVO.getPk_dept();
			String pk_newdept = deptPKBackupMap.get(pk_dept);
			toBeCopiedPostVO.setPk_dept(pk_newdept);

			// 在插入前还要修改额外的一些信息
			// step 1. 判断编码是否重复
			boolean duplicated = havePostCode(toBeCopiedPostVO);
			// step 2. 如果重复，那么需要自动生成一个流水号编码来代替
			if (isAutoGenerateBillCode) {
				toBeCopiedPostVO.setPostcode(billcode[i]);
			} else {
				if (duplicated) {
					// 不自动生成单据号
					toBeCopiedPostVO.setPostcode(prefix + "_" + getFlowCode(flowCode, i));
				}
			}

			// FIXME 岗位创建时间
			// toBeCopiedPostVO.setBuilddate();
			aggPostVOList.add(sourcePostVOs[i]);
		}

		// 执行插入
		if (!aggPostVOList.isEmpty()) {
			AggPostVO[] newAggPostVOs = getPostManageService().insert(aggPostVOList.toArray(new AggPostVO[0]));

			for (int i = 0; i < newAggPostVOs.length; i++) {

				// toBeCopiedAggPostVOs是按照suporior排序好的
				PostVO newPostVO = (PostVO) newAggPostVOs[i].getParentVO();
				insertPostVOs[i] = newPostVO;

				// 形成老岗位PK与新岗位PK的映射
				old_newPostpkmap.put(toBeCopiedPostPK[i], newPostVO.getPk_post());
				// 修改所有当前已经插入的岗位的子岗位的suporior为最新的
				for (int j = i + 1; j < sourcePostVOs.length; j++) {
					PostVO childPostVO = (PostVO) sourcePostVOs[j].getParentVO();
					String suporior = childPostVO.getSuporior();
					if (suporior != null && suporior.equals(toBeCopiedPostPK[i])) {// 修改所有子岗位
						childPostVO.setSuporior(newPostVO.getPk_post());
					}
				}
			}
		}
	}

	/**
	 * 转移在职人员
	 * 
	 * @param ruleVO
	 * @param insertPostVOs
	 * @param deptPKBackupMap
	 * @param old_newPostpkmap
	 * @param transItemVOs
	 * @param transPsnInfVOs
	 * @param psndocPKMap
	 * @return
	 * @throws BusinessException
	 */
	private void doTransPsnInf(DeptTransRuleVO ruleVO, PostVO[] insertPostVOs, Map<String, String> deptPKBackupMap,
			Map<String, String> old_newPostpkmap, DeptTransItemVO[] transItemVOs, GeneralVO[] transPsnInfVOs,
			Map<String, String> psndocPKMap) throws BusinessException {
		if (ArrayUtils.isEmpty(transPsnInfVOs))
			return;
		String oldPkPsnjob;
		// 是否同步工作履历
		Boolean isSysWork = ruleVO.isSysWork().booleanValue();
		ArrayList<PsnJobVO> jobList = new ArrayList<PsnJobVO>();
		// ArrayList<String> orgList = new ArrayList<String>();

		ArrayList<String> psnjobList = new ArrayList<String>();
		ArrayList<String> deptList = new ArrayList<String>();

		List<String> psnJobPkList = new ArrayList<String>();
		for (int i = 0; i < transPsnInfVOs.length; i++) {
			psnJobPkList.add((String) transPsnInfVOs[i].getAttributeValue(PsnJobVO.PK_PSNJOB));
		}
		PsnJobVO[] psnjobVOs = getServiceTemplate().queryByPks(PsnJobVO.class, psnJobPkList.toArray(new String[0]));
		Map<String, PsnJobVO> psnJobMap = new HashMap<String, PsnJobVO>();
		for (int i = 0; i < psnjobVOs.length; i++) {
			psnJobMap.put(psnjobVOs[i].getPk_psnjob(), psnjobVOs[i]);
		}

		// 查询部门下的人员
		for (GeneralVO transPsnInfVO : transPsnInfVOs) {
			PsnJobVO psnjob = buildTransPsns(transPsnInfVO, ruleVO, insertPostVOs, deptPKBackupMap, old_newPostpkmap,
					transItemVOs, psnJobMap);
			oldPkPsnjob = (String) transPsnInfVO.getAttributeValue(PsnJobVO.PK_PSNJOB);
			// String pk_hrorg = psnjob.getPk_org();
			// // 根据业务范围设置判断是否需要更新转移后组织：显示委托 不更新 ：隐式委托 更新
			// if
			// (getIManagescopeFacade().isEntityUsed(ManagescopeTypeEnum.psnMajorjob,
			// oldPkPsnjob)) {
			// String[] pkHrorgs =
			// getIManagescopeFacade().queryHrorgsByAssgidPsnorgAndBusiregion(
			// psnjob.getPk_psnorg(), psnjob.getAssgid(),
			// ManagescopeBusiregionEnum.psndoc);
			// pk_hrorg = pkHrorgs[0];
			// } else {
			// String[] pkHrorgs =
			// getIManagescopeFacade().queryHrOrgsByPostDeptAndBusiregion(psnjob.getPk_post(),
			// psnjob.getPk_dept(), ManagescopeBusiregionEnum.psndoc);
			// pk_hrorg = pkHrorgs[0];
			// }

			psnjobList.add(oldPkPsnjob);
			deptList.add(psnjob.getPk_dept());
			//
			jobList.add(psnjob);
			// orgList.add(pk_hrorg);
		}

		Map<String, String> map = ManagescopeFacade.queryHrOrgByPsnjobAndDept(psnjobList.toArray(new String[0]),
				deptList.toArray(new String[0]));
		ArrayList<String> orgList = new ArrayList<String>();
		for (int i = 0; i < jobList.size(); i++) {
			if (map.get(psnjobList.get(i)) != null) {
				orgList.add(map.get(psnjobList.get(i)));
			} else {
				orgList.add(jobList.get(i).getPk_hrorg());
			}
		}

		// for (PsnJobVO vo : jobList) {
		// if (map.get(psnjobList.get(index)) != null) {
		// orgList.add(map.get(vo.getPk_psnorg()));
		// } else {
		// orgList.add(vo.getPk_hrorg());
		// }
		// }

		getRecordService().addNewPsnjobs(jobList.toArray(new PsnJobVO[0]), isSysWork, orgList.toArray(new String[0]));
	}

	/**
	 * 构建转移人员"模板"VO<br>
	 * 
	 * @param transPsnInfVO
	 * @param ruleVO
	 * @param insertPostVOs
	 * @param deptPKBackupMap
	 * @param old_newPostpkmap
	 * @param transItemVOs
	 * @return PsnJobVO
	 * @throws BusinessException
	 */
	private PsnJobVO buildTransPsns(GeneralVO transPsnInfVO, DeptTransRuleVO ruleVO, PostVO[] insertPostVOs,
			Map<String, String> deptPKBackupMap, Map<String, String> old_newPostpkmap, DeptTransItemVO[] transItemVOs,
			Map<String, PsnJobVO> psnJobMap) throws BusinessException {
		// 得到上一条记录
		PsnJobVO psnjob = psnJobMap.get(transPsnInfVO.getAttributeValue(PsnJobVO.PK_PSNJOB));

		// FIXME 新任职开始日期
		if (ruleVO.getTransdata() != null) {
			psnjob.setBegindate(new UFLiteralDate(ruleVO.getTransdata().toStdString()));
		} else {
			psnjob.setBegindate(new UFLiteralDate(PubEnv.getServerDate().toStdString()));
		}
		psnjob.setEnddate(null);
		psnjob.setEndflag(UFBoolean.FALSE);
		psnjob.setIsmainjob(UFBoolean.TRUE);
		psnjob.setLastflag(UFBoolean.TRUE);
		psnjob.setPk_hrgroup(ruleVO.getContext().getPk_group());
		psnjob.setPk_group(ruleVO.getContext().getPk_group());

		// update 2018-01-31 部门跨组织转移人员任职记录的人力资源组织为目标业务单元所属人力资源组织
		String condition = " pk_org in (select distinct pk_hrorg from  hr_relation_org where  pk_org = '"
				+ ruleVO.getNeworg() + "')";
		HROrgVO[] orgVOs = (HROrgVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, HROrgVO.class, condition);
		if (orgVOs != null && orgVOs.length > 0) {
			psnjob.setPk_hrorg(orgVOs[0].getPk_org());
		} else {
			psnjob.setPk_hrorg(ruleVO.getNeworg());
		}
		// end 2018-01-31

		psnjob.setPk_org(ruleVO.getNeworg());
		psnjob.setPk_psnjob(null);
		// 试用信息
		psnjob.setTrial_flag(UFBoolean.FALSE);
		psnjob.setTrial_type(null);
		psnjob.setRecordnum(0);
		psnjob.setTrnsevent((Integer) ((MDEnum) TrnseventEnum.TRANS).value());
		psnjob.setTrnstype(ruleVO.getTranstype());

		// 单据信息
		psnjob.setOribilltype(null);
		psnjob.setOribillpk(null);

		// 员工号要使用上一条的员工号

		//
		if (!ArrayUtils.isEmpty(transItemVOs))
			for (DeptTransItemVO transItemVO : transItemVOs) {
				// 转移后
				if (transItemVO.getItemkey().startsWith("new") && transItemVO.getIsedit().booleanValue()) {
					psnjob.setAttributeValue(transItemVO.getItemkey().substring(3),
							transPsnInfVO.getAttributeValue(transItemVO.getItemkey()));
				}
			}
		// 部门信息
		psnjob.setPk_dept(deptPKBackupMap.get(psnjob.getPk_dept()));
		// 岗位信息
		psnjob.setPk_post(old_newPostpkmap.get(psnjob.getPk_post()));
		if (!ArrayUtils.isEmpty(insertPostVOs))
			for (PostVO insertPostVO : insertPostVOs) {
				// 岗位序列
				if (insertPostVO.getPk_post().equals(psnjob.getPk_post())) {
					psnjob.setPk_postseries(insertPostVO.getPk_postseries());
					break;
				}
			}
		// 职务、职级、职等等信息保持不变
		return psnjob;
	}

	/**
	 * 转移兼职人员
	 * 
	 * @param ruleVO
	 * @param insertPostVOs
	 * @param deptPKBackupMap
	 * @param old_newPostpkmap
	 * @return
	 * @throws BusinessException
	 */
	private void doTransPartPsnInf(DeptTransRuleVO ruleVO, PostVO[] insertPostVOs, Map<String, String> deptPKBackupMap,
			Map<String, String> old_newPostpkmap) throws BusinessException {
		// 转移部门主键临时表
		InSQLCreator isc = new InSQLCreator();
		String temptable = isc.getInSQL(ruleVO.getPk_depts());
		String cond = " ismainjob = 'N' and ( " + SQLHelper.getNullSql("endflag")
				+ " or endflag ='N' ) and assgid > 1 and " + PsnJobVO.PK_DEPT + " in (" + temptable + " )";
		// 未结束的兼职记录
		PartTimeVO[] partTimeVOs = getServiceTemplate().queryByCondition(PartTimeVO.class, cond);
		// 无兼职人员直接返回
		if (partTimeVOs == null || partTimeVOs.length < 1) {
			return;
		}
		// 是否转移兼职人员
		UFBoolean isTransPart = ruleVO.isTransPart();
		// 是否同步工作履历
		UFBoolean isSysWork = ruleVO.isSysWork();
		if (isTransPart.booleanValue()) { // 兼职人员变更
			for (PartTimeVO partTimeVO : partTimeVOs) {
				if (ruleVO.getTransdata() != null) {
					partTimeVO.setBegindate(new UFLiteralDate(ruleVO.getTransdata().toStdString()));
				} else {
					partTimeVO.setBegindate(new UFLiteralDate(PubEnv.getServerDate().toStdString()));
				}

				partTimeVO.setEnddate(null);
				// 部门信息
				partTimeVO.setPk_org(ruleVO.getNeworg());
				partTimeVO.setPk_dept(deptPKBackupMap.get(partTimeVO.getPk_dept()));
				// 岗位信息
				partTimeVO.setPk_post(old_newPostpkmap.get(partTimeVO.getPk_post()));
				if (!ArrayUtils.isEmpty(insertPostVOs))
					for (PostVO insertPostVO : insertPostVOs) {
						// 岗位序列
						if (insertPostVO.getPk_post().equals(partTimeVO.getPk_post())) {
							partTimeVO.setPk_postseries(insertPostVO.getPk_postseries());
							break;
						}
					}
				partTimeVO.setPk_psnjob(null);
				partTimeVO.setStatus(VOStatus.NEW);

			}
			// 兼职人员变更
			getRecordService().savePartchgInfo(partTimeVOs, isSysWork.booleanValue());
		} else {
			// 结束兼职信息
			HiOrgBaseEventListener psnServer = new HiOrgBaseEventListener();
			psnServer.endPart(partTimeVOs, new UFLiteralDate(ruleVO.getTransdata().toStdString()));
		}
	}

	/**
	 * 离职转移
	 * 
	 * @param ruleVO
	 * @param deptPKBackupMap
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unused")
	private void doTransDimissionPsnInf(DeptTransRuleVO ruleVO, Map<String, String> deptPKBackupMap)
			throws BusinessException {
		// 转移部门主键临时表
		InSQLCreator isc = new InSQLCreator();
		String temptable = isc.getInSQL(ruleVO.getPk_depts());
		String cond = " ismainjob = 'Y' and  lastflag = 'Y' and endflag = 'Y'  and " + PsnJobVO.PK_DEPT + " in ("
				+ temptable + " )";
		// 未结束的兼职记录
		PsnJobVO[] psnJobVOs = getServiceTemplate().queryByCondition(PsnJobVO.class, cond);
		// 无离职人员直接返回
		if (ArrayUtils.isEmpty(psnJobVOs)) {
			return;
		}

		for (PsnJobVO psnJobVO : psnJobVOs) {
			psnJobVO.setPk_org(ruleVO.getNeworg());
			// 部门信息
			psnJobVO.setPk_dept(deptPKBackupMap.get(psnJobVO.getPk_dept()));
			psnJobVO.setPk_psnjob(null);
			psnJobVO.setBegindate(ruleVO.getTransdata());
			psnJobVO.setEnddate(null);
			// 离职人员转移
			getRecordManageService().doTransDimission(psnJobVO);
		}

	}

	/**
	 * 相关人员转移
	 * 
	 * @param ruleVO
	 * @param deptPKBackupMap
	 * @return
	 * @throws BusinessException
	 */
	private void doTransPoiPsnInf(DeptTransRuleVO ruleVO, Map<String, String> deptPKBackupMap) throws BusinessException {
		// 转移部门主键临时表
		InSQLCreator isc = new InSQLCreator();
		String temptable = isc.getInSQL(ruleVO.getPk_depts());
		String cond = " ismainjob = 'Y' and  lastflag = 'Y'  and ( " + SQLHelper.getNullSql("endflag")
				+ " or endflag ='N' ) and psntype = 1  and " + PsnJobVO.PK_DEPT + " in (" + temptable + " )";

		// 未结束的兼职记录
		PsnJobVO[] psnJobVOs = getServiceTemplate().queryByCondition(PsnJobVO.class, cond);
		// 无相关人员直接返回
		if (ArrayUtils.isEmpty(psnJobVOs))
			return;

		for (PsnJobVO psnJobVO : psnJobVOs) {
			// 部门信息
			psnJobVO.setPk_org(ruleVO.getNeworg());
			psnJobVO.setPk_dept(deptPKBackupMap.get(psnJobVO.getPk_dept()));
			psnJobVO.setPk_psnjob(null);
			psnJobVO.setBegindate(ruleVO.getTransdata());
			psnJobVO.setEnddate(null);
			psnJobVO.setPk_post(null);
			psnJobVO.setPk_postseries(null);
			psnJobVO.setPk_job(null);
			psnJobVO.setPk_jobrank(null);
			psnJobVO.setSeries(null);
			psnJobVO.setPk_jobgrade(null);
			psnJobVO.setLastflag(UFBoolean.TRUE);
			psnJobVO.setRecordnum(0);
		}
		// 相关人员增加一条工作记录
		getRecordService().addPoiPsnjobs(psnJobVOs);

	}

	/**
	 * 构建转移前公司的部门变更历史VO<br>
	 * 
	 * @param ruleVO
	 * @param newAggDeptVOs
	 * @param deptPKBackupMap
	 * @return
	 */
	private void cancelOldDeptHistoryVOs(DeptTransRuleVO ruleVO, AggHRDeptVO[] newAggDeptVOs,
			Map<String, String> deptPKBackupMap) throws BusinessException {

		AggHRDeptVO[] aggHRDeptVOs = queryByPks(ruleVO.getPk_depts());
		Map<String, AggHRDeptVO> deptPkAggHRDeptVOMap = new HashMap<String, AggHRDeptVO>();
		List<String> pkorgList = new ArrayList<String>();
		if (!ArrayUtils.isEmpty(aggHRDeptVOs)) {
			for (AggHRDeptVO aggHRDeptVO : aggHRDeptVOs) {
				pkorgList.add((String) aggHRDeptVO.getParentVO().getAttributeValue(HRDeptVO.PK_ORG));
				deptPkAggHRDeptVOMap.put(aggHRDeptVO.getParentVO().getPrimaryKey(), aggHRDeptVO);
			}
		}
		HRDeptVO[] sourceDeptVOs = (HRDeptVO[]) AggVOHelper.getParentVOArrayFromAggVOs(aggHRDeptVOs, HRDeptVO.class);
		SuperVOHelper.sort(sourceDeptVOs, new String[] { HRDeptVO.INNERCODE }, new boolean[] { false }, true);
		List<DeptHistoryVO> historyVOCache = new ArrayList<DeptHistoryVO>();
		//

		Map<String, HROrgVO> hrorgMap = new HashMap<String, HROrgVO>();
		HROrgVO[] hrorgVOs = getOMCommonQueryService().queryByPKs(HROrgVO.class, pkorgList.toArray(new String[0]));
		for (int i = 0; i < hrorgVOs.length; i++) {
			hrorgMap.put(hrorgVOs[i].getPk_org(), hrorgVOs[i]);
		}

		List<DeptHistoryVO> sourceHistoryVOCache = new ArrayList<DeptHistoryVO>();
		HRDeptVO sourceDeptVO;
		// 被合并部门和接受部门的变更历史共享同一个变更操作号
		for (int i = 0; i < sourceDeptVOs.length; i++) {
			sourceDeptVO = sourceDeptVOs[i];
			DeptHistoryVO historyVO = null;
			for (AggHRDeptVO newAggDeptVO : newAggDeptVOs) {
				HRDeptVO deptVO = (HRDeptVO) newAggDeptVO.getParentVO();
				if (ObjectUtils.equals(deptPKBackupMap.get(sourceDeptVO.getPk_dept()), deptVO.getPk_dept())) {
					DeptHistoryVO[] historyVOs = (DeptHistoryVO[]) newAggDeptVO.getTableVO(AggHRDeptVO.HISTORY);
					historyVO = historyVOs[0];
					historyVO.setIsreceived(UFBoolean.TRUE);
					sourceHistoryVOCache.add(historyVO);
					break;
				}
			}
			DeptHistoryVO deptHistoryVO = SuperVOHelper.createSuperVOFromSuperVO(sourceDeptVO, DeptHistoryVO.class);
			deptHistoryVO.setChangetype(DeptChangeType.OUTERSHIFT);
			deptHistoryVO.setIsreceived(UFBoolean.FALSE);
			String[] attrs = new String[] { DeptHistoryVO.CHANGENUM, DeptHistoryVO.APPROVENUM,
					DeptHistoryVO.APPROVEDEPT, DeptHistoryVO.EFFECTDATE, DeptHistoryVO.MEMO };
			SuperVOHelper.batchSyncCopyAttributes(new SuperVO[] { historyVO }, new SuperVO[] { deptHistoryVO }, attrs);

			// 编码
			deptHistoryVO.setCode(sourceDeptVO.getCode());
			// 部门名
			SuperVOHelper.copyMultiLangAttribute(sourceDeptVO, deptHistoryVO);
			// 部门级别
			deptHistoryVO.setDeptlevel(sourceDeptVO.getDeptlevel());
			// 负责人
			deptHistoryVO.setPrincipal(sourceDeptVO.getPrincipal());
			// 组织
			deptHistoryVO.setPk_org(sourceDeptVO.getPk_org());

			// 记录源部门所属组织的版本主键
			// HROrgVO hrorgVO =
			// getOMCommonQueryService().queryByPK(HROrgVO.class,
			// deptHistoryVO.getPk_org());
			if (hrorgMap.get(deptHistoryVO.getPk_org()) != null) {
				HROrgVO hrorgVO = hrorgMap.get(deptHistoryVO.getPk_org());
				deptHistoryVO.setPk_org_v(hrorgVO.getPk_vid());
			}

			// 更改查询标志
			sourceDeptVO.setHrcanceled(UFBoolean.TRUE);
			// 撤销日期
			sourceDeptVO.setDeptcanceldate(historyVO.getEffectdate());

			historyVOCache.add(deptHistoryVO);
			//
			PostVO[] postVOs = getPostQueryService().queryPostVOsByDeptPK(sourceDeptVO.getPk_dept(), false);
			if (!ArrayUtils.isEmpty(postVOs)) {
				SuperVOHelper.sort(postVOs, new String[] { PostVO.INNERCODE }, new boolean[] { false }, true);
				for (PostVO postVO : postVOs) {
					if (UFBoolean.FALSE.equals(postVO.getHrcanceled())) {
						getPostManageService().cancel(postVO, historyVO.getEffectdate(), false);
					}
				}
			}
			// 从map中取值然后修改，不会没有的，如果没有那么就抛异常吧
			@SuppressWarnings("unused")
			AggHRDeptVO updateDeptVO = update(deptPkAggHRDeptVOMap.get(sourceDeptVO.getPk_dept()), false);
			/*
			 * 跨业务单元转移时，不执行停用 // 执行停用 if (((HRDeptVO)
			 * updateDeptVO.getParentVO()).getEnablestate() ==
			 * IPubEnumConst.ENABLESTATE_ENABLE) { updateDeptVO =
			 * disable(updateDeptVO); }
			 */

		}

		// 记录变更历史
		getDeptDao().getBaseDAO().insertVOArray(historyVOCache.toArray(new DeptHistoryVO[0]));

		// 新部门历史记录的是否结束 = 'Y'
		getDeptDao().getBaseDAO().updateVOArray(sourceHistoryVOCache.toArray(new DeptHistoryVO[0]),
				new String[] { DeptHistoryVO.ISRECEIVED });

	}

	@Override
	public HRDeptVO[] queryManagedDeptsByPersonPK(String pk_psndoc, boolean isPrincipal) throws BusinessException {
		String condition = "pk_psndoc = '" + pk_psndoc + "' and pk_dept <> '~'";
		if (isPrincipal) {
			condition += " and principalflag = 'Y' ";
		}
		HROrgManagerVO[] managerVOs = getOMCommonQueryService().queryByCondition(HROrgManagerVO.class,
				new String[] { HRDeptVO.PK_DEPT }, condition, null);
		String[] deptPKs = SuperVOHelper.getAttributeValueArray(managerVOs, "pk_dept", String.class);

		if (ArrayUtils.isEmpty(deptPKs)) {
			return null;
		}

		return getDeptDao().queryManagedDeptsByPersonPK(deptPKs);
	}

	/**
	 * 返回有数据操作权限的部门
	 * 
	 * @param deptVOs
	 * @param wherePower
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public HRDeptVO[] hasPowerDepts(HRDeptVO[] deptVOs, String wherePower) throws BusinessException {
		return getDeptDao().hasPowerDepts(deptVOs, wherePower);
	}

	/**
	 * 根据部门pk查询这个部门所属的HR组织
	 * 
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public String queryHrPkOrgByPkDept(String pk_dept) throws BusinessException {
		HRDeptVO hrDeptVO = getOMCommonQueryService().queryByPK(HRDeptVO.class, pk_dept);
		if (hrDeptVO != null) {
			// 查询部门所属业务单元
			OrgVO orgVO = getOMCommonQueryService().queryByPK(OrgVO.class, hrDeptVO.getPk_org());

			if (orgVO.getOrgtype4().booleanValue()) {
				return orgVO.getPk_org();
			} else {
				return queryHrOrgVObyPkOrg(orgVO.getPk_org());
			}
		}
		return null;
	}

	private String queryHrOrgVObyPkOrg(String pk_org) throws BusinessException {
		// 查询部门所属行政组织
		AdminOrgVO adminOrgVO = getOMCommonQueryService().queryByPK(AdminOrgVO.class, pk_org);

		// 查询上级行政组织所属业务单元
		OrgVO fatherOrgVO = getOMCommonQueryService().queryByPK(OrgVO.class, adminOrgVO.getPk_fatherorg());
		if (fatherOrgVO.getOrgtype4().booleanValue()) {
			return fatherOrgVO.getPk_org();
		} else {
			return queryHrOrgVObyPkOrg(fatherOrgVO.getPk_org());
		}
	}

	private IPostQueryService getPostQueryService() {
		return NCLocator.getInstance().lookup(IPostQueryService.class);
	}

	private IPostManageService getPostManageService() {
		return NCLocator.getInstance().lookup(IPostManageService.class);
	}

	private IAOSQueryService getAOSQueryService() {
		return NCLocator.getInstance().lookup(IAOSQueryService.class);
	}

	private IOrgUnitManageService getOrgUnitManageService() {
		return NCLocator.getInstance().lookup(IOrgUnitManageService.class);
	}

	private IUserManageQuery getUserManageQuery() {
		return NCLocator.getInstance().lookup(IUserManageQuery.class);
	}

	private IReportOrgManageService getReportOrgManageService() {
		return NCLocator.getInstance().lookup(IReportOrgManageService.class);
	}

	private IPsndocQryService getPersonQueryService() {
		return NCLocator.getInstance().lookup(IPsndocQryService.class);
	}

	private IDefdocQryService getDefdocQueryService() {
		return NCLocator.getInstance().lookup(IDefdocQryService.class);
	}

	private IOMCommonQueryService getOMCommonQueryService() {
		return NCLocator.getInstance().lookup(IOMCommonQueryService.class);
	}

	protected IPersonRecordService getRecordService() {

		return NCLocator.getInstance().lookup(IPersonRecordService.class);
	}

	protected IRdsManageService getRecordManageService() {

		return NCLocator.getInstance().lookup(IRdsManageService.class);
	}

	protected IPsndocService getPsndocService() {
		return NCLocator.getInstance().lookup(IPsndocService.class);
	}

	@SuppressWarnings("unused")
	private IManagescopeFacade getIManagescopeFacade() {

		return NCLocator.getInstance().lookup(IManagescopeFacade.class);

	}

	@Override
	public String getDeptVid(String pk_dept, UFDate date) throws BusinessException {
		// 转化为标准时区的时间
		String strDate = date.toString();

		String querySql = " select pk_vid from org_dept_v where pk_dept ='" + pk_dept + "' and '" + strDate
				+ "' < venddate and '" + strDate + "'>= vstartdate; ";

		String pk_vid = (String) new BaseDAO().executeQuery(querySql, new ColumnProcessor());

		return pk_vid;
	}

	@Override
	public DeptManager queryOrgManagerVOByOrgIDAndCuserID(String pk_org, String cuserid) throws BusinessException {
		String condition = DeptManager.PK_DEPT + " = '" + pk_org + "' and " + OrgManagerVO.CUSERID + " ='" + cuserid
				+ "'";
		@SuppressWarnings("unchecked")
		Collection<DeptManager> c = new BaseDAO().retrieveByClause(DeptManager.class, condition);
		if (c != null && c.size() > 0)
			return c.toArray(new DeptManager[0])[0];

		return null;
	}

	/**
	 * 导入工具插入，不发送消息<br>
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public AggHRDeptVO insertImportVO(AggHRDeptVO vo) throws BusinessException {
		TimerLogger timerLogger = new TimerLogger("导入工具效率测试——部门", null);
		if (vo == null) {
			return null;
		}
		AggHRDeptVO oldVO = AggVOHelper.clone(vo, getEntityClass());
		beforeInsert(vo);

		timerLogger.addLog("数据权限校验开始");
		// 数据权限校验
		insertPowerValidate(vo);
		timerLogger.addLog("数据权限校验结束");

		// 加锁操作：为所在分支的根编码加主键锁； 为自己加业务锁；
		// 为了导入工具效率，加锁去掉
		// insertLockOperate(vo);

		timerLogger.addLog("逻辑校验开始");
		// 逻辑校验
		// 为了导入工具效率，取消编码和名称校验，前台已经进行唯一性校验
		insertValidateVO(vo);
		timerLogger.addLog("逻辑校验结束");
		timerLogger.addLog("添加审计信息开始");
		// 添加审计信息
		setInsertAuditInfo(vo);
		timerLogger.addLog("添加审计信息结束");

		// 事件前通知
		// fireBeforeInsertEvent(vo);

		timerLogger.addLog("更新内部编码开始");
		// DB操作：保存新增VO; 更新内部编码; 重新检索新VO;
		AggHRDeptVO resultVO = dbImportInsertVO(vo);
		timerLogger.addLog("更新内部编码结束");

		// 缓存通知
		// 为了导入工具效率,注释掉
		// notifyVersionChangeWhenDataInserted(vo);

		// 事件后通知
		// fireAfterInsertEvent(vo);

		// 业务日志
		// 为了导入工具效率,注释掉
		// writeInsertBusiLog(vo);
		timerLogger.addLog("保存部门开始");
		afterInsertImportVO(oldVO, resultVO);
		timerLogger.addLog("保存部门结束");

		timerLogger.log();
		// 为了导入工具效率，发现这个方法只有查询没有更新，所以注释
		// resultVO = getServiceTemplate().updateTsAndKeys(resultVO);
		return resultVO;
	}

	/**
	 * 导入工具新增时的事件后通知
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	@Override
	public void fireAfterImportInsertEvent(AggHRDeptVO[] vos) throws BusinessException {
		getSystemTime("后事件开始");
		BDCommonEventUtil eventUtil = new BDCommonEventUtil(IMetaDataIDConst.DEPT);
		List<AggHRDeptVO> list = Arrays.asList(vos);
		List<AggHRDeptVO> listFire = new ArrayList<AggHRDeptVO>();

		for (AggHRDeptVO vo : list) {
			if (vo.getParentVO().getPrimaryKey() != null) {
				listFire.add(vo);
			}

		}
		eventUtil.dispatchInsertAfterEvent(listFire.toArray(new AggHRDeptVO[0]));
		getSystemTime("后事件结束");
	}

	/**
	 * 批改 miaoxiong add
	 */
	@Override
	public AggHRDeptVO[] batchUpdateDeptMain(SuperVO updateVO, String strFieldCode, String[] strPk_depts)
			throws BusinessException {

		Object objAttrValue = updateVO.getAttributeValue(strFieldCode);
		AggHRDeptVO[] aggVOs = queryByPks(strPk_depts);
		for (AggHRDeptVO aggVO : aggVOs) {
			aggVO.getParentVO().setAttributeValue(strFieldCode, objAttrValue);
			aggVO = update(aggVO, true);
		}

		return aggVOs;

		// return
		// getDeptDao().batchUpdateDeptMain(updateVO,strFieldCode,strPk_depts);
	}

	/**
	 * 
	 * @param deptMap
	 * @param updateCareer
	 * @param needCreateVersion
	 * @param versionMemo
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public HashMap<AggHRDeptVO, String> rename(HashMap<AggHRDeptVO, DeptHistoryVO> deptMap, boolean updateCareer,
			boolean needCreateVersion, String versionMemo) throws BusinessException {
		AggHRDeptVO[] aggDeptVOs = deptMap.keySet().toArray(new AggHRDeptVO[0]);

		HashMap<AggHRDeptVO, String> returnMap = new HashMap<AggHRDeptVO, String>();

		for (AggHRDeptVO aggDeptVO : aggDeptVOs) {
			HRDeptVO deptVO = (HRDeptVO) aggDeptVO.getParentVO();
			DeptHistoryVO historyVO = deptMap.get(aggDeptVO);
			String warningMsg = "";
			if (needCreateVersion) {
				UFDate oldversionStartDate = deptVO.getVstartdate();
				UFDate vstartdate = new UFDate(new Date());// 新版本生效时间
				if (oldversionStartDate.isSameDate(vstartdate, Calendars.getGMTDefault())) {
					warningMsg = "由于" + ResHelper.getString("org", "CreateVersionDialog-000002")/*
																								 * 一天最多只能生成一个版本
																								 * .
																								 */;
					warningMsg += ResHelper.getString(
							"6005dept",
							"06005dept0366",
							new String[] { MultiLangHelper.getName(deptVO),
									VOUtils.getDocName(OrgVO.class, deptVO.getPk_org()) })
					/* @res "部门：[{0}]  所属组织：[{1}] 生成新版本失败！" */;
				} else if (!oldversionStartDate.isSameDate(vstartdate, Calendars.getGMTDefault())
						&& oldversionStartDate.compareTo(vstartdate) > 0) {
					warningMsg = "由于" + ResHelper.getString("org", "CreateVersionDialog-000003")/*
																								 * 新版本的生效日期不能早于上一版本的生效日期
																								 * .
																								 */;
					warningMsg += ResHelper.getString(
							"6005dept",
							"06005dept0366",
							new String[] { MultiLangHelper.getName(deptVO),
									VOUtils.getDocName(OrgVO.class, deptVO.getPk_org()) })
					/* @res "部门：[{0}]  所属组织：[{1}] 生成新版本失败！" */;
				} else {
					aggDeptVO = createDeptVersion(aggDeptVO);
				}

			}
			aggDeptVO = rename(aggDeptVO, historyVO, updateCareer);
			returnMap.put(aggDeptVO, warningMsg);
		}

		return returnMap;
	}

	/**
	 * 
	 * @param deptMap
	 * @param disableDept
	 * @param needCreateVersion
	 * @param versionMemo
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public HashMap<AggHRDeptVO, String> cancel(HashMap<AggHRDeptVO, DeptHistoryVO> deptMap, boolean disableDept,
			boolean needCreateVersion, String versionMemo) throws BusinessException {
		AggHRDeptVO[] aggDeptVOs = deptMap.keySet().toArray(new AggHRDeptVO[0]);

		HashMap<AggHRDeptVO, String> returnMap = new HashMap<AggHRDeptVO, String>();

		for (AggHRDeptVO aggDeptVO : aggDeptVOs) {
			HRDeptVO deptVO = (HRDeptVO) aggDeptVO.getParentVO();
			DeptHistoryVO historyVO = deptMap.get(aggDeptVO);
			AggHRDeptVO[] canceledVOs = cancel(aggDeptVO, historyVO, disableDept);
			if (needCreateVersion) {
				for (AggHRDeptVO canceledVO : canceledVOs) {
					String warningMsg = "";
					HRDeptVO mainVO = (HRDeptVO) canceledVO.getParentVO();
					UFDate oldversionStartDate = mainVO.getVstartdate();
					UFDate vstartdate = new UFDate(new Date());// 新版本生效时间
					if (oldversionStartDate.isSameDate(vstartdate, Calendars.getGMTDefault())) {
						warningMsg = "由于" + ResHelper.getString("org", "CreateVersionDialog-000002")/*
																									 * 一天最多只能生成一个版本
																									 * .
																									 */;
						warningMsg += ResHelper.getString(
								"6005dept",
								"06005dept0366",
								new String[] { MultiLangHelper.getName(mainVO),
										VOUtils.getDocName(OrgVO.class, mainVO.getPk_org()) })
						/* @res "部门：[{0}]  所属组织：[{1}] 生成新版本失败！" */;
					} else if (!oldversionStartDate.isSameDate(vstartdate, Calendars.getGMTDefault())
							&& oldversionStartDate.compareTo(vstartdate) > 0) {
						warningMsg = "由于" + ResHelper.getString("org", "CreateVersionDialog-000003")/*
																									 * 新版本的生效日期不能早于上一版本的生效日期
																									 * .
																									 */;
						warningMsg += ResHelper.getString(
								"6005dept",
								"06005dept0366",
								new String[] { MultiLangHelper.getName(mainVO),
										VOUtils.getDocName(OrgVO.class, mainVO.getPk_org()) })
						/* @res "部门：[{0}]  所属组织：[{1}] 生成新版本失败！" */;
					} else {
						mainVO.setVname(deptVO.getVname());
						canceledVO = createDeptVersion(canceledVO);
					}
					returnMap.put(canceledVO, warningMsg);
				}
			} else {
				for (AggHRDeptVO canceledVO : canceledVOs) {
					returnMap.put(canceledVO, "");
				}
			}
		}

		return returnMap;
	}

	/**
	 * 
	 * @param deptMap
	 * @param includeChildDept
	 * @param includePost
	 * @param enableDept
	 * @param disableDept
	 * @param needCreateVersion
	 * @param versionMemo
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public HashMap<AggHRDeptVO, String> uncancel(HashMap<AggHRDeptVO, DeptHistoryVO> deptMap, boolean includeChildDept,
			boolean includePost, boolean enableDept, boolean needCreateVersion, String versionMemo)
			throws BusinessException {
		AggHRDeptVO[] aggDeptVOs = deptMap.keySet().toArray(new AggHRDeptVO[0]);

		HashMap<AggHRDeptVO, String> returnMap = new HashMap<AggHRDeptVO, String>();

		for (AggHRDeptVO aggDeptVO : aggDeptVOs) {
			HRDeptVO deptVO = (HRDeptVO) aggDeptVO.getParentVO();
			DeptHistoryVO historyVO = deptMap.get(aggDeptVO);
			AggHRDeptVO[] uncanceledVOs = uncancel(aggDeptVO, historyVO, includeChildDept, includePost, enableDept);
			if (needCreateVersion) {
				for (AggHRDeptVO uncanceledVO : uncanceledVOs) {
					String warningMsg = "";
					HRDeptVO mainVO = (HRDeptVO) uncanceledVO.getParentVO();
					UFDate oldversionStartDate = mainVO.getVstartdate();
					UFDate vstartdate = new UFDate(new Date());// 新版本生效时间
					if (oldversionStartDate.isSameDate(vstartdate, Calendars.getGMTDefault())) {
						warningMsg = "由于" + ResHelper.getString("org", "CreateVersionDialog-000002")/*
																									 * 一天最多只能生成一个版本
																									 * .
																									 */;
						warningMsg += ResHelper.getString(
								"6005dept",
								"06005dept0366",
								new String[] { MultiLangHelper.getName(mainVO),
										VOUtils.getDocName(OrgVO.class, mainVO.getPk_org()) })
						/* @res "部门：[{0}]  所属组织：[{1}] 生成新版本失败！" */;
					} else if (!oldversionStartDate.isSameDate(vstartdate, Calendars.getGMTDefault())
							&& oldversionStartDate.compareTo(vstartdate) > 0) {
						warningMsg = "由于" + ResHelper.getString("org", "CreateVersionDialog-000003")/*
																									 * 新版本的生效日期不能早于上一版本的生效日期
																									 * .
																									 */;
						warningMsg += ResHelper.getString(
								"6005dept",
								"06005dept0366",
								new String[] { MultiLangHelper.getName(mainVO),
										VOUtils.getDocName(OrgVO.class, mainVO.getPk_org()) })
						/* @res "部门：[{0}]  所属组织：[{1}] 生成新版本失败！" */;
					} else {
						mainVO.setVname(deptVO.getVname());
						uncanceledVO = createDeptVersion(uncanceledVO);
					}
					returnMap.put(uncanceledVO, warningMsg);
				}
			} else {
				for (AggHRDeptVO uncanceledVO : uncanceledVOs) {
					returnMap.put(uncanceledVO, "");
				}
			}
		}

		return returnMap;
	}

	@Override
	public AggHRDeptVO updateImportVO(AggHRDeptVO vo, boolean blChangeAudltInfo) throws BusinessException {

		AggHRDeptVO beforeUpdateVO = AggVOHelper.clone(vo, getEntityClass());
		beforeUpdate(vo);

		if (vo == null) {
			return null;
		}
		// 数据权限校验
		updatePowerValidate(vo);

		// 加锁操作：为所在分支的根编码加主键锁；如果修改到另一分支，还要为新分支的根主键加锁； 为自己加业务锁；
		// 为了导入工具把加锁去掉
		// updateLockOperate(vo);

		// 检索OldVO: oldVO只关心主表
		AggHRDeptVO oldVO = getMDQueryService().queryBillOfVOByPK(getEntityClass(), vo.getParentVO().getPrimaryKey(),
				false);

		// 版本校验
		// 前台组装的时候从数据库中取了每个VO的ts，所以这个地方不用校验了
		// updateVersionValidate(oldVO, vo);

		// 逻辑校验
		updateValidateVO(oldVO, vo);

		// 更新审计信息
		setUpdateAuditInfo(vo, blChangeAudltInfo);

		// 事件前通知
		fireBeforeUpdateEvent(oldVO, vo);

		// DB操作：保存修改的VO; 更新内部编码; 重新检索新VO;
		AggHRDeptVO resultVO = dbUpdateImportVO(oldVO, vo);

		// 缓存通知
		// 为了导入工具效率，去掉缓存通知
		// notifyVersionChangeWhenDataUpdated(vo);

		// 事件后通知
		fireAfterUpdateEvent(oldVO, vo);

		// 业务日志
		// 为了导入工具效率，去掉写日志
		// writeUpdatedBusiLog(vo);

		afterUpdate(beforeUpdateVO, resultVO);

		// 为了导入工具：这里面只有查询没有数据处理，这个地方查询可以省略
		// resultVO = getServiceTemplate().updateTsAndKeys(resultVO);

		// 处理部门历史信息
		reformDeptHistoryInfo(resultVO);

		return resultVO;

	}

	/**
	 * 更新导入工具VO
	 * 
	 * @param oldVO
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	protected AggHRDeptVO dbUpdateImportVO(AggHRDeptVO oldVO, AggHRDeptVO vo) throws BusinessException {
		// 设置VO状态
		vo.getParentVO().setStatus(VOStatus.UPDATED);
		// DB操作
		getMDPersistenceService().saveBillWithRealDelete(vo);
		// 如果上下级关系发生变化，则更新内部编码
		String idName = getBizInfoUtil().getFieldName((SuperVO) vo.getParentVO(), IBaseServiceConst.ID);
		String pidName = getBizInfoUtil().getFieldName((SuperVO) vo.getParentVO(), IBaseServiceConst.PID);
		String oldParentPK = (String) oldVO.getParentVO().getAttributeValue(pidName);
		String newParentPK = (String) vo.getParentVO().getAttributeValue(pidName);
		if (!StringUtils.equals(oldParentPK, newParentPK)) {
			InnerCodeUtil.generateInnerCodeAfterChangePosition(getBizInfoUtil()
					.getTableName((SuperVO) vo.getParentVO()), idName, pidName, vo.getParentVO().getPrimaryKey(),
					newParentPK);
		}

		// 重新检索新VO
		return vo;
	}

	@Override
	public String getDisabledOrgDeptnames(HRDeptVO[] deptVOs) throws BusinessException {
		String names = "";

		ArrayList<String> deptPks = new ArrayList<String>();

		for (HRDeptVO deptVO : deptVOs) {
			deptPks.add(deptVO.getPk_org());
		}

		InSQLCreator inSqlCreator = new InSQLCreator();
		String inSql = inSqlCreator.getInSQL(deptPks.toArray(new String[0]));
		HRAdminOrgVO[] adminOrgVOs = (HRAdminOrgVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, HRAdminOrgVO.class, " pk_adminorg in (  " + inSql + " )");
		HashMap<String, HRAdminOrgVO> adminOrgMap = new HashMap<String, HRAdminOrgVO>();
		for (HRAdminOrgVO adminOrgVO : adminOrgVOs) {
			adminOrgMap.put(adminOrgVO.getPk_adminorg(), adminOrgVO);
		}

		for (int i = 0; i < deptVOs.length; i++) {
			String pk_org = deptVOs[i].getPk_org();
			HRAdminOrgVO adminOrgVO = adminOrgMap.get(pk_org);
			if (IPubEnumConst.ENABLESTATE_DISABLE == adminOrgVO.getEnablestate()) {
				names += "[" + deptVOs[i].getName() + "]";
			}
		}
		return names;
	}

	/**
	 * 针对后编码，生成部门编码
	 * 
	 * @author heqiaoa
	 * @throws BusinessException
	 */
	private void setDeptCode(HRDeptVO vo) throws BusinessException {
		if (isAutoGenerateBillCode(vo) && !getBillCodeContext(vo).isPrecode()
				&& JFCommonValue.LEVELED_CODE_TMP.equals(vo.getCode())) {
			IHrBillCode iBillcode = NCLocator.getInstance().lookup(IHrBillCode.class);
			String[] codes = iBillcode.getLeveledBillCode(JFCommonValue.NBCR_DEPT, vo.getPk_group(), vo.getPk_org(),
					vo, 1);
			if (!ArrayUtils.isEmpty(codes)) {
				vo.setCode(codes[0]);
			} else {
				throw new BusinessException("Can not get Dept Code by Suffix Coding Rule!");
			}
		}
	}

}
