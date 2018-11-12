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
 * ���ŷ���ʵ��<br>
 * 
 * @author zhangdd
 */
@SuppressWarnings("deprecation")
public class DeptServiceImpl extends TreeBaseServiceAggVOAdapter<AggHRDeptVO> implements IDeptManageService,
		IDeptQueryService {

	/** ����DAO */
	private DeptDao deptDao = null;

	private final String docName = IMetaDataIDConst.DEPT;

	private BillCodeContext billCodeContext;

	/**
	 * �ύ����
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	private void commitBillCode(HRDeptVO vo) throws BusinessException {
		// ������Զ����ɱ��룬���ύ
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
		// ������Զ����ɱ��룬�����
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
		// Ĭ�Ϲ��˷�沿�ţ�����ʾ��沿��
		// ��ѯʱ����displayorder����
		AggHRDeptVO[] aggVOs = queryByConditionWithoutVisibleWithOrder(condition, HRDeptVO.DISPLAYORDER + ","
				+ HRDeptVO.CODE, new String[] { "depthistory.effectdate" });

		// �Բ��ű����ʷ���д���
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
	 * * Ĭ��ѡ�и���֯��ѯ��Ҫ�Ĵ��룬ǰ̨��ѯ���ݻ��治�ܳ���200��ֱ�ӵ��ú�̨Ҳ����ջ����{@inheritDoc}<br>
	 * Created on 2014-3-28 16:05:26<br>
	 * 
	 * @see nc.bs.om.pub.TreeBaseServiceAggVOAdapter#queryByCondition(nc.vo.uif2.LoginContext,
	 *      java.lang.String, java.lang.String)
	 * @author wangxbd
	 *************************************************************************** */
	@Override
	public AggHRDeptVO[] queryByCondition(LoginContext context, String condition, String typePk)
			throws BusinessException {
		// ���conditionΪ�գ��鵱ǰ������Դ��֯����ҵ��Ԫ�����в���
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
		// Ĭ�Ϲ��˷�沿�ţ�����ʾ��沿��
		// ��ѯʱ����displayorder����
		AggHRDeptVO[] aggVOs = queryByConditionWithoutVisibleWithOrder(condition, HRDeptVO.DISPLAYORDER + ","
				+ HRDeptVO.CODE, new String[] { "depthistory.effectdate" });

		// �Բ��ű����ʷ���д���
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
		// �Բ��ű����ʷ���д���
		handleDeptHistory(deptVO);
		return deptVO;
	}

	@Override
	public AggHRDeptVO[] queryByPks(String[] pks) throws BusinessException {
		AggHRDeptVO[] aggVOs = super.queryByPks(pks);

		// �Բ��ű����ʷ���д���
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
	 * �Բ��ŵı����ʷ�ӱ����һЩ����Ҫ��֤�����ݿ�ĸ��¡���ѯ������ִ�д˲���<br>
	 * 
	 * @param deptVOs
	 */
	private void handleDeptHistory(AggHRDeptVO... deptVOs) throws BusinessException {
		// �Բ��ű����ʷ�ӱ���д���
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
						// MOD (���ӷǿ�У��)
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

				// ��ѯ��˱����ʷVO����������������ʷVO
				DeptHistoryVO[] relateVOs = historyVOMap.get(historyVO.getChangenum());

				if (ArrayUtils.isEmpty(relateVOs)) {
					continue;
				}
				if (DeptChangeType.MERGE.equalsIgnoreCase(historyVO.getChangetype())) {// ����Ǻϲ�����

					if (UFBoolean.TRUE.equals(historyVO.getIsreceived())) {// ����ǽ��ղ���
																			// �趨���ղ��ź���֯
						String[] srcAttrs = new String[] { DeptHistoryVO.NAME };
						String[] desAttrs = new String[] { DeptHistoryVO.RECEIVEDEPT };
						SuperVOHelper.copyMultiLangAttribute(historyVO, historyVO, srcAttrs, desAttrs);

						// �趨������֯
						historyVO.setReceiveorg(historyVO.getPk_org());

						// �ҵ����б��ϲ��Ĳ���
						for (DeptHistoryVO relateVO : relateVOs) {
							if ((null == relateVO.getIsreceived() || UFBoolean.FALSE.equals(relateVO.getIsreceived()))
									&& DeptChangeType.MERGE.equals(relateVO.getChangetype())) {
								multiLangText.appendText("[").appendText(relateVO.getName()).appendText("] ");
								multiLangText.appendText2("[").appendText2(relateVO.getName2()).appendText2("] ");
								multiLangText.appendText3("[").appendText3(relateVO.getName3()).appendText3("] ");
							}
						}

						SuperVOHelper.copyMultiLangAttribute(multiLangText, DeptHistoryVO.MERGEDDEPT, historyVO);
					} else {// ����Ǳ��ϲ�����
						multiLangText.appendText("[").appendText(historyVO.getName()).appendText("] ");
						multiLangText.appendText2("[").appendText2(historyVO.getName2()).appendText2("] ");
						multiLangText.appendText3("[").appendText3(historyVO.getName3()).appendText3("] ");
						SuperVOHelper.copyMultiLangAttribute(multiLangText, DeptHistoryVO.MERGEDDEPT, historyVO);

						// �ҵ����ղ���
						for (DeptHistoryVO relateVO : relateVOs) {
							// ����Ǳ��ϲ����ţ����������
							if (UFBoolean.TRUE.equals(relateVO.getIsreceived())) {
								String[] srcAttrs = new String[] { DeptHistoryVO.NAME };
								String[] desAttrs = new String[] { DeptHistoryVO.RECEIVEDEPT };
								SuperVOHelper.copyMultiLangAttribute(relateVO, historyVO, srcAttrs, desAttrs);
								break;
							}
						}
					}
				} else if (DeptChangeType.SHIFT.equalsIgnoreCase(historyVO.getChangetype())) {
					// �ҵ����ղ���
					for (DeptHistoryVO relateVO : relateVOs) {
						// �����Ԫ��ת�Ʋ��ţ����������
						if (UFBoolean.TRUE.equals(relateVO.getIsreceived())) {
							String[] srcAttrs = new String[] { DeptHistoryVO.NAME };
							String[] desAttrs = new String[] { DeptHistoryVO.RECEIVEDEPT };
							SuperVOHelper.copyMultiLangAttribute(relateVO, historyVO, srcAttrs, desAttrs);
							break;
						}
					}
				} else if (DeptChangeType.OUTERSHIFT.equalsIgnoreCase(historyVO.getChangetype())) {
					// �ҵ����ղ���
					for (DeptHistoryVO relateVO : relateVOs) {
						// ����ǿ絥Ԫת�Ʋ��ţ����������
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
	 * ��ѯ��ǰ������֯�µ����в��ţ��������¼���
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
	 * ����������ѯĳ��HR��֯������Χ�Ĳ��š���heqiaoa 2014-7-28
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
			// ����ʱҪ��¼�����ݿ��TS
			ts = orgVO.getTs();
		}

		SuperVOHelper.copySuperVOAttributes(deptVO, orgVO);
		if (ts != null) {
			orgVO.setTs((UFDateTime) ts.clone());
		}
		ts = null;
		// ���û�������
		orgVO.setPk_org(deptVO.getPk_dept());
		// ���ŵ�����֯Ϊҵ��Ԫ��Ϊ�˷��㽫ҵ��Ԫ+���Ž����������ݹ���(����Ȩ�޷���ȴ�)�������ŵ�������֯�������ϼ����ŷֱ����ҵ��Ԫ��������˾�������ϼ�ҵ��Ԫ��
		orgVO.setAttributeValue(OrgVO.PK_CORP, deptVO.getAttributeValue(DeptVO.PK_ORG));
		orgVO.setAttributeValue(OrgVO.PK_FATHERORG, deptVO.getAttributeValue(DeptVO.PK_FATHERORG));
		// ���ö�Ӧ����
		OrgTypeVO orgtypevo = OrgTypeManager.getInstance().getOrgTypeByID(IOrgConst.DEPTORGTYPE);
		orgVO.setAttributeValue(orgtypevo.getFieldname(), UFBoolean.TRUE);

		// �������ñ��
		orgVO.setEnablestate(deptVO.getEnablestate());

		// �����Ƿ�ҵ��Ԫ���ݱ��
		orgVO.setIsbusinessunit(UFBoolean.FALSE);

		orgVO.setPk_ownorg(deptVO.getPk_org());

		// �������š��˲����ṹ�ȷ�ҵ��Ԫ��������ͬ������֯��ʱ���汾����Ϊ�գ���������VIDֵ
		if (StringUtil.isEmpty(orgVO.getPk_vid())) {
			if (StringUtil.isEmpty((String) deptVO.getAttributeValue("pk_vid"))) {
				orgVO.setPk_vid(OidGenerator.getInstance().nextOid());
			} else {
				orgVO.setPk_vid((String) deptVO.getAttributeValue("pk_vid"));
			}
		}

		// �������ͣ���ҵ��Ԫ�ı���ְ��Ĭ��ѡ��
		orgVO.setAttributeValue(OrgTypeManager.getInstance().getOrgTypeByID(IOrgConst.REPORTORGTYPE).getFieldname(),
				UFBoolean.TRUE);

		return orgVO;
	}

	/**
	 * ��ȡΪ���빤��ʹ��
	 * 
	 * @param deptVO
	 * @return
	 */
	private OrgVO initImportToOrgVO(HRDeptVO deptVO) {
		OrgVO orgVO = new OrgVO();

		SuperVOHelper.copySuperVOAttributes(deptVO, orgVO);

		// ���û�������
		orgVO.setPk_org(deptVO.getPk_dept());
		// ���ŵ�����֯Ϊҵ��Ԫ��Ϊ�˷��㽫ҵ��Ԫ+���Ž����������ݹ���(����Ȩ�޷���ȴ�)�������ŵ�������֯�������ϼ����ŷֱ����ҵ��Ԫ��������˾�������ϼ�ҵ��Ԫ��
		orgVO.setAttributeValue(OrgVO.PK_CORP, deptVO.getAttributeValue(DeptVO.PK_ORG));
		orgVO.setAttributeValue(OrgVO.PK_FATHERORG, deptVO.getAttributeValue(DeptVO.PK_FATHERORG));
		// ���ö�Ӧ����
		OrgTypeVO orgtypevo = OrgTypeManager.getInstance().getOrgTypeByID(IOrgConst.DEPTORGTYPE);
		orgVO.setAttributeValue(orgtypevo.getFieldname(), UFBoolean.TRUE);

		// �������ñ��
		orgVO.setEnablestate(deptVO.getEnablestate());

		// �����Ƿ�ҵ��Ԫ���ݱ��
		orgVO.setIsbusinessunit(UFBoolean.FALSE);

		orgVO.setPk_ownorg(deptVO.getPk_org());

		// �������š��˲����ṹ�ȷ�ҵ��Ԫ��������ͬ������֯��ʱ���汾����Ϊ�գ���������VIDֵ
		if (StringUtil.isEmpty(orgVO.getPk_vid())) {
			if (StringUtil.isEmpty((String) deptVO.getAttributeValue("pk_vid"))) {
				orgVO.setPk_vid(OidGenerator.getInstance().nextOid());
			} else {
				orgVO.setPk_vid((String) deptVO.getAttributeValue("pk_vid"));
			}
		}

		// �������ͣ���ҵ��Ԫ�ı���ְ��Ĭ��ѡ��
		orgVO.setAttributeValue(OrgTypeManager.getInstance().getOrgTypeByID(IOrgConst.REPORTORGTYPE).getFieldname(),
				UFBoolean.TRUE);

		return orgVO;
	}

	@Override
	protected void beforeInsert(AggHRDeptVO aggVO) throws BusinessException {

		// �������Ű汾
		// ����ʱ�����ɳ�ʼ�汾��Ϣ���汾��Ϊ��ǰ���+01����Ч���ڼ�Ϊҵ��Ԫ����������
		HRDeptVO vo = (HRDeptVO) aggVO.getParentVO();
		vo.setVname(ResHelper.getString("6005dept", "06005dept0346")/*
																	 * @res
																	 * "��ʼ�汾"
																	 */);
		UFDate time = PubEnv.getServerDate();
		vo.setVno(time.getYear() + "01");
		vo.setPk_vid(OidGenerator.getInstance().nextOid());
		vo.setVstartdate(time);
		vo.setOrgtype13(UFBoolean.TRUE);
		// �Ƿ�Ԥ����֯ zhangqiano 2015-11-21
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

		// �����ű����ʷ
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

		// �����ӱ����ڽ�����ʾ
		// newVO.setDepthistory(new DeptHistoryVO[] { historyVO });
		newAggVO.setTableVO(AggHRDeptVO.HISTORY, new DeptHistoryVO[] { historyVO });
		// ���Ű汾��Ϣ
		HRDeptVersionVO deptVersionVO = SuperVOHelper.createSuperVOFromSuperVO(newVO, HRDeptVersionVO.class);
		BDPKLockUtil.lockSuperVO(deptVersionVO);
		HRDeptVersionVO oldDeptVersionVO = getOMCommonQueryService().queryByPK(HRDeptVersionVO.class,
				deptVersionVO.getPk_dept());
		if (oldDeptVersionVO == null) {
			// ���ݿ����
			getDeptDao().getBaseDAO().insertVOWithPK(deptVersionVO);
			// ֪ͨ����
			CacheProxy.fireDataInserted(new DefaultGetBizInfoByMDUtil().getTableName(deptVersionVO), null);
		} else {
			// ���ݿ����
			getDeptDao().getBaseDAO().updateVO(deptVersionVO);
			// ֪ͨ����
			CacheProxy.fireDataUpdated(new DefaultGetBizInfoByMDUtil().getTableName(deptVersionVO), null);
		}

		// ����ʱ�������ܶ�û��PK_ORG,��Ϊ�������ܵ�pk_orgΪ�ò���������������Ҫ�ڲ��벿����Ϣ�����¸��� zhangqiano
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
				// ��ѯ�����˶�Ӧ��ϵͳ�û�,��û�й����û���ͬ����������֯����
				insertPrincipal(newVO);
			} else {
				dupManagerVO.setPrincipalflag(UFBoolean.TRUE);
				BDPKLockUtil.lockSuperVO(dupManagerVO);// �ֹ���

				getDeptDao().getBaseDAO().updateVO(dupManagerVO);// ���ݿ����
			}

		}

		// ͬ������OrgVO
		OrgVO orgVO = initOrgVO(newVO);

		orgVO = getOrgUnitManageService().insertVO(orgVO);

		// ��汾�����OrgVO��ʼ�汾��Ϣ
		OrgVersionVO newOrgVersionVO = SuperVOHelper.createSuperVOFromSuperVO(orgVO, OrgVersionVO.class);
		BDPKLockUtil.lockSuperVO(newOrgVersionVO);// �ֹ���
		OrgVersionVO orgVersionVO = getOMCommonQueryService()
				.queryByPK(OrgVersionVO.class, newOrgVersionVO.getPk_vid());
		if (orgVersionVO == null) {
			// �����°汾
			getDeptDao().getBaseDAO().insertVOWithPK(newOrgVersionVO);// ���ݿ����
		} else {
			// ����
			getDeptDao().getBaseDAO().updateVO(newOrgVersionVO);// ���ݿ����
		}

		// ����ʱ��Ҫͬ�����ɱ�����֯��Ϣ
		DeptVO deptVO = new DeptVO();
		SuperVOHelper.copySuperVOAttributes(newVO, deptVO);
		ReportOrgVO reportOrgVO = new ReportOrgVO();
		SuperVOHelper.copySuperVOAttributes(newVO, deptVO);

		reportOrgVO.setEnablestate(IPubEnumConst.ENABLESTATE_INIT);
		reportOrgVO.setSourceorgtype(IOrgEnumConst.ENTITYORGTYPE_DEPT);
		reportOrgVO.setStatus(VOStatus.NEW);
		new DeptTypeServiceAdapterBasic().execEachDeptInsertOperate(deptVO, reportOrgVO);

		// ͬ������Ԥ����֯��Ϣ heqiaoa
		// PlanBudgetVO planBudgetVO = new PlanBudgetVO();
		// planBudgetVO.setEnablestate(IPubEnumConst.ENABLESTATE_INIT);
		// planBudgetVO.setSourceorgtype(IOrgEnumConst.ENTITYORGTYPE_DEPT);
		// planBudgetVO.setStatus(VOStatus.NEW);
		// new DeptTypeServiceAdapterBasic().execEachDeptInsertOperate(deptVO,
		// planBudgetVO);
	}

	/**
	 * Ϊ�˵��빤�ߵ�����ȡ����
	 * 
	 * @param oldAggVO
	 * @param newAggVO
	 * @throws BusinessException
	 */
	protected void afterInsertImportVO(AggHRDeptVO oldAggVO, AggHRDeptVO newAggVO) throws BusinessException {
		// HRDeptVO oldVO = (HRDeptVO)oldAggVO.getParentVO();
		HRDeptVO newVO = (HRDeptVO) newAggVO.getParentVO();
		// ��Ϊ���빤�����沿�ű����Ѿ����ڣ�����ط�����Ҫ�������ˡ�
		// commitBillCode(newVO);

		// �����ű����ʷ
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

		// �����ӱ����ڽ�����ʾ
		// newVO.setDepthistory(new DeptHistoryVO[] { historyVO });
		newAggVO.setTableVO(AggHRDeptVO.HISTORY, new DeptHistoryVO[] { historyVO });
		// ���Ű汾��Ϣ
		HRDeptVersionVO deptVersionVO = SuperVOHelper.createSuperVOFromSuperVO(newVO, HRDeptVersionVO.class);
		// Ϊ��Ч�ʰ�����Ĵ���ע�͵������빤�߿��Բ�����
		// BDPKLockUtil.lockSuperVO(deptVersionVO);
		// ��Ϊ��һ�β��룬�������в��Ű汾��ʷ
		// HRDeptVersionVO oldDeptVersionVO =
		// getOMCommonQueryService().queryByPK(HRDeptVersionVO.class,
		// deptVersionVO.getPk_dept());
		// if (oldDeptVersionVO == null)
		// {
		// ���ݿ����
		getDeptDao().getBaseDAO().insertVOWithPK(deptVersionVO);
		// ֪ͨ����
		// Ϊ�˵��빤��Ч��ȥ��
		// CacheProxy.fireDataInserted(new
		// DefaultGetBizInfoByMDUtil().getTableName(deptVersionVO), null);
		// }
		// else
		// {
		// // ���ݿ����
		// getDeptDao().getBaseDAO().updateVO(deptVersionVO);
		// // ֪ͨ����
		// // Ϊ�˵��빤��Ч��ȥ��
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
				// ��ѯ�����˶�Ӧ��ϵͳ�û�,��û�й����û���ͬ����������֯����
				insertPrincipal(newVO);
			} else {
				dupManagerVO.setPrincipalflag(UFBoolean.TRUE);
				// Ϊ�˵��빤��Ч�ʣ�ע�͵�
				// BDPKLockUtil.lockSuperVO(dupManagerVO);// �ֹ���
				getDeptDao().getBaseDAO().updateVO(dupManagerVO);// ���ݿ����
			}

		}

		// ͬ������OrgVO
		// OrgVO orgVO = initOrgVO(newVO);
		OrgVO orgVO = initImportToOrgVO(newVO);
		orgVO = getOrgUnitManageService().insertVO(orgVO);

		// ��汾�����OrgVO��ʼ�汾��Ϣ
		OrgVersionVO newOrgVersionVO = SuperVOHelper.createSuperVOFromSuperVO(orgVO, OrgVersionVO.class);
		// Ϊ�˵��빤��Ч�ʣ�ע�͵�
		// BDPKLockUtil.lockSuperVO(newOrgVersionVO);// �ֹ���
		// ��Ϊ��һ�β��룬�������в��Ű汾��ʷ
		// OrgVersionVO orgVersionVO =
		// getOMCommonQueryService().queryByPK(OrgVersionVO.class,
		// newOrgVersionVO.getPk_vid());
		// if (orgVersionVO == null)
		// {
		// �����°汾
		getDeptDao().getBaseDAO().insertVOWithPK(newOrgVersionVO);// ���ݿ����
		// }
		// else
		// {
		// // ����
		// getDeptDao().getBaseDAO().updateVO(newOrgVersionVO);// ���ݿ����
		// }
		// ����ʱ��Ҫͬ�����ɱ�����֯��Ϣ
		DeptVO deptVO = new DeptVO();
		SuperVOHelper.copySuperVOAttributes(newVO, deptVO);
		ReportOrgVO reportOrgVO = new ReportOrgVO();
		SuperVOHelper.copySuperVOAttributes(newVO, deptVO);

		reportOrgVO.setEnablestate(IPubEnumConst.ENABLESTATE_INIT);
		reportOrgVO.setSourceorgtype(IOrgEnumConst.ENTITYORGTYPE_DEPT);
		reportOrgVO.setStatus(VOStatus.NEW);
		new DeptTypeServiceAdapterBasic().execEachDeptInsertOperate(deptVO, reportOrgVO);
	}

	// ����֯�汾��������
	private OrgVersionVO getOrgVersionVO(String pk_group, String pk_org) throws BusinessException {
		CacheConfig config = new CacheConfig();
		config.setCacheType(CacheType.MEMORY);
		config.setFlushInterval(3600000);// 1Сʱˢ��һ��
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
			// �ڸ���ǰ����ͬ�����Ÿ������ֶ�
			StringBuilder condition = new StringBuilder();
			condition.append(DeptManager.PK_DEPT + " = '" + vo.getPk_dept() + "'");
			condition.append(" and ");
			condition.append(DeptManager.PRINCIPALFLAG + " = 'Y'");
			// ��ѯ��֯������Ĳ��Ÿ�������Ϣ
			DeptManager[] managers = getOMCommonQueryService()
					.queryByCondition(DeptManager.class, condition.toString());

			// ���Ϊ�գ��ж��Ƿ���Ҫ��������Ÿ���������
			if (!ArrayUtils.isEmpty(managers)) {// ����ҵ���¼
												// ɾ�����ݿ��¼
				getDeptDao().getBaseDAO().deleteVOArray(managers);
			}
		} else {// ��ò��Ÿ������ֶβ�Ϊ��
			StringBuilder condition = new StringBuilder();
			condition.append(DeptManager.PK_DEPT + " = '" + vo.getPk_dept() + "'");
			condition.append(" and (");
			condition.append(DeptManager.PK_PSNDOC + " = '" + vo.getPrincipal() + "'");
			condition.append(" or ");
			condition.append(DeptManager.PRINCIPALFLAG + " = 'Y' )");

			// ��ѯ��֯������Ĳ��Ÿ�������Ϣ
			DeptManager[] managers = getOMCommonQueryService()
					.queryByCondition(DeptManager.class, condition.toString());

			if (ArrayUtils.isEmpty(managers)) {// �������������û��¼�����Ÿ����ˣ�����һ����¼
				insertPrincipal(vo);
			} else {
				// �ж�ԭ�еĲ��Ÿ����˺����ڵ��Ƿ�һ����һ����ȥ����
				// ������߲�ͬ
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
			// �����ű����ʷ��"����"��¼�����²��Ŵ���ʱ��
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
	 * ��������ʷ��Ϣ<br>
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	@Override
	protected void reformDeptHistoryInfo(AggHRDeptVO aggVO) throws BusinessException {
		handleDeptHistory(aggVO);
	}

	/**
	 * ���벿�Ÿ��������ݵ��������ܱ�<br>
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
			getDeptDao().getBaseDAO().insertVO(deptManagerVO);// ���ݿ����
		}
	}

	@Override
	public AggHRDeptVO update(AggHRDeptVO vo, boolean blChangeAuditInfo) throws BusinessException {
		beforeUpdate(vo);

		if (vo == null) {
			// afterUpdate(beforeUpdateVO, null);
			return null;
		}
		// ����Ȩ��У��
		updatePowerValidate(vo);

		// ����������Ϊ���ڷ�֧�ĸ������������������޸ĵ���һ��֧����ҪΪ�·�֧�ĸ����������� Ϊ�Լ���ҵ������
		updateLockOperate(vo);

		// ����OldVO: oldVOֻ��������
		AggHRDeptVO oldVO = getMDQueryService().queryBillOfVOByPK(getEntityClass(), vo.getParentVO().getPrimaryKey(),
				false);

		// �汾У��
		updateVersionValidate(oldVO, vo);

		// �߼�У��
		updateValidateVO(oldVO, vo);

		// ���������Ϣ
		setUpdateAuditInfo(vo, blChangeAuditInfo);

		// �¼�ǰ֪ͨ
		fireBeforeUpdateEvent(oldVO, vo);

		// DB�����������޸ĵ�VO; �����ڲ�����; ���¼�����VO;
		AggHRDeptVO resultVO = dbUpdateVO(oldVO, vo);

		// ����֪ͨ
		notifyVersionChangeWhenDataUpdated(vo);

		// �¼���֪ͨ
		fireAfterUpdateEvent(oldVO, vo);

		// ҵ����־
		writeUpdatedBusiLog(vo);

		afterUpdate(oldVO, resultVO);

		resultVO = getServiceTemplate().updateTsAndKeys(resultVO);

		// ��������ʷ��Ϣ
		reformDeptHistoryInfo(resultVO);

		return resultVO;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void afterUpdate(AggHRDeptVO oldAggVO, AggHRDeptVO newAggVO) throws BusinessException {
		// HRDeptVO oldVO = (HRDeptVO)oldAggVO.getParentVO();
		HRDeptVO newVO = (HRDeptVO) newAggVO.getParentVO();

		// ��汾����¸ð汾��Ϣ
		HRDeptVersionVO deptVersionVO = SuperVOHelper.createSuperVOFromSuperVO(newVO, HRDeptVersionVO.class);
		BDPKLockUtil.lockSuperVO(deptVersionVO);
		// ���ݿ����
		getDeptDao().getBaseDAO().updateVO(deptVersionVO);
		// ֪ͨ����
		CacheProxy.fireDataUpdated(new DefaultGetBizInfoByMDUtil().getTableName(deptVersionVO), null);

		// ͬ������OrgVO
		OrgVO orgvo = initOrgVO(newVO);
		orgvo = getOrgUnitManageService().updateVO(orgvo);

		// ���±���VO
		ReportOrgVO reportOrgVO = getOMCommonQueryService().queryByPK(ReportOrgVO.class, newVO.getPk_dept());
		if (reportOrgVO != null) {
			new DeptTypeServiceAdapterBasic().execEachDeptUpdateOperate(newVO, reportOrgVO);
		}
		// �޸�ʱ�Բ��ű����ʷ�ӱ���д���-kongrf
		DeptHistoryVO[] historyVOs = (DeptHistoryVO[]) oldAggVO.getTableVO(AggHRDeptVO.HISTORY);
		// �����������������ʱ�򲻸�����ʷ��Ϣ
		if (ArrayUtils.isEmpty(historyVOs) || historyVOs.length > 1) {
			return;
		}
		// �ҳ�������������ʷ
		DeptHistoryVO historyVO = historyVOs[0];
		historyVO.setCode(newVO.getCode());
		historyVO.setName(newVO.getName());
		historyVO.setDeptlevel(newVO.getDeptlevel());
		historyVO.setMemo(newVO.getMemo());
		getDeptDao().getBaseDAO().updateVO(historyVO);
		// �����ӱ����ڽ�����ʾ
		newAggVO.setTableVO(AggHRDeptVO.HISTORY, new DeptHistoryVO[] { historyVO });
		// �Բ��ű����ʷ���д���
		// handleDeptHistory(newAggVO);
	}

	@Override
	protected void afterDelete(AggHRDeptVO aggVO) throws BusinessException {
		HRDeptVO vo = (HRDeptVO) aggVO.getParentVO();
		returnBillCodeOnDelete(vo);

		// �Ӱ汾��ɾ���ð汾��Ϣ
		HRDeptVersionVO deptVersionVO = SuperVOHelper.createSuperVOFromSuperVO(vo, HRDeptVersionVO.class);
		BDPKLockUtil.lockSuperVO(deptVersionVO);// �ֹ���
		getDeptDao().getBaseDAO().deleteVO(deptVersionVO);// ���ݿ����
		CacheProxy.fireDataDeleted(new DefaultGetBizInfoByMDUtil().getTableName(deptVersionVO),
				deptVersionVO.getPk_vid());// ֪ͨ����

		// ͬ��ɾ��OrgVO
		OrgVO orgVO = initOrgVO(vo);
		getOrgUnitManageService().deleteVO(orgVO);

		// ���UAP�Ĳ��Žڵ�Դ˲��Ž��й�������ôӦ��ͬ��ɾ����Ӧ�ı�����֯
		ReportOrgVO reportOrgVO = getOMCommonQueryService().queryByPK(ReportOrgVO.class, vo.getPk_dept());
		if (reportOrgVO != null) {
			getReportOrgManageService().deleteVO(reportOrgVO);
		}
	}

	@Override
	protected void afterDisable(AggHRDeptVO oldAggVO, AggHRDeptVO newAggVO) throws BusinessException {
		HRDeptVO newVO = (HRDeptVO) newAggVO.getParentVO();

		// ��汾����¸ð汾��Ϣ
		HRDeptVersionVO deptVersionVO = SuperVOHelper.createSuperVOFromSuperVO(newVO, HRDeptVersionVO.class);
		BDPKLockUtil.lockSuperVO(deptVersionVO);
		// ���ݿ����
		getDeptDao().getBaseDAO().updateVO(deptVersionVO);
		// ֪ͨ����
		CacheProxy.fireDataUpdated(new DefaultGetBizInfoByMDUtil().getTableName(deptVersionVO), null);

		// ͬ������OrgVO
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
		String pk_svid = OidGenerator.getInstance().nextOid();// �°汾����

		// step1:��ѯ��pk_org�����һ�����Žṹ�汾���ݣ����¾ɰ汾��venddate�ֶ�Ϊ�°汾��Ч����-1
		String condition = HRDeptStruVersionVO.PK_BUSIUNIT + " = '" + pk_org + "' and venddate >= '9999-12-31'";

		HRDeptStruVersionVO[] deptStruVersionVOs = getOMCommonQueryService().queryByCondition(
				HRDeptStruVersionVO.class, condition);
		if (!ArrayUtils.isEmpty(deptStruVersionVOs)) {
			HRDeptStruVersionVO oldDeptVersionVO = deptStruVersionVOs[0];
			BDPKLockUtil.lockSuperVO(oldDeptVersionVO);// �ֹ���
			oldDeptVersionVO.setVenddate(vstartdate.getDateBefore(1));// ���°汾ʱ��
			getDeptDao().getBaseDAO().updateVO(oldDeptVersionVO);// ���ݿ����
		}

		// step2:�������Žṹ�汾��Ϣ
		HRDeptStruVersionVO deptStruVersionVO = new HRDeptStruVersionVO();
		deptStruVersionVO.setPk_vid(pk_svid);
		deptStruVersionVO.setPk_busiunit(pk_org);
		deptStruVersionVO.setVname(vname);
		// ��ѯ��һ���汾��
		String vno = getOMCommonQueryService().queryNextVersionNO("org_deptstru_v", "pk_busiunit", pk_org);
		deptStruVersionVO.setVno(vstartdate.getYear() + vno);
		deptStruVersionVO.setVstartdate(vstartdate);
		deptStruVersionVO.setVenddate(new UFDate("9999-12-31", false));
		BDPKLockUtil.lockSuperVO(deptStruVersionVO);// �ֹ���
		getDeptDao().getBaseDAO().insertVOWithPK(deptStruVersionVO);// ���ݿ����

		// step3:��ѯ��pk_org�����в�������
		condition = HRDeptVO.PK_ORG + " = '" + pk_org + "'";
		HRDeptVO[] deptVOs = getOMCommonQueryService().queryByCondition(HRDeptVO.class, condition);

		// step4:�������Žṹ��Ա�汾��Ϣ
		HRDeptStruMemberVersionVO[] deptVersionVOs = new HRDeptStruMemberVersionVO[deptVOs.length];
		for (int i = 0; i < deptVOs.length; i++) {
			deptVersionVOs[i] = new HRDeptStruMemberVersionVO();
			deptVersionVOs[i].setPk_busiunit(pk_org);
			deptVersionVOs[i].setPk_svid(pk_svid);// ����Ա�Ľṹ�汾��Ϊ�ṹ�����°汾��
			deptVersionVOs[i].setPk_org(deptVOs[i].getPk_dept());
			deptVersionVOs[i].setPk_orgvid(deptVOs[i].getPk_vid());
			deptVersionVOs[i].setPk_fatherorg(deptVOs[i].getPk_fatherorg());
		}
		BDPKLockUtil.lockSuperVO(deptVersionVOs);// �ֹ���
		getDeptDao().getBaseDAO().insertVOArray(deptVersionVOs);// ���ݿ����
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
		// UFDate vstartdate = deptVO.getVstartdate();// �°汾ʱ��

		UFDate vstartdate = new UFDate(new Date());// �°汾��Чʱ��

		String pk_deptvid = OidGenerator.getInstance().nextOid();// �°汾����

		String condition = HRDeptVersionVO.PK_DEPT + " = '" + deptVO.getPrimaryKey() + "' and pk_vid = '"
				+ deptVO.getPk_vid() + "'";
		HRDeptVersionVO[] deptVersionVOs = getOMCommonQueryService().queryByCondition(HRDeptVersionVO.class, condition);

		if (ArrayUtils.isEmpty(deptVersionVOs)) {
			return null;
		}

		// ��ѯ����Ҫ�����°汾��deptVO�ľɰ汾���ݣ����¾ɰ汾��venddate�ֶ�Ϊ�°汾��Ч����-1
		HRDeptVersionVO oldDeptVersionVO = deptVersionVOs[0];
		BDPKLockUtil.lockSuperVO(oldDeptVersionVO);
		oldDeptVersionVO.setVenddate(vstartdate.getDateBefore(1));
		// �����Ϣ
		AuditInfoUtil.updateData(oldDeptVersionVO);
		// ���ݿ����
		BaseDAO baseDAO = getDeptDao().getBaseDAO();
		baseDAO.updateVO(oldDeptVersionVO);

		// ����dept���е�deptvoΪ���°汾
		deptVO.setPk_vid(pk_deptvid);
		String vno = getOMCommonQueryService().queryNextVersionNO("org_dept_v", "pk_dept", deptVO.getPk_dept());
		deptVO.setVno(vstartdate.getYear() + vno);
		deptVO.setVstartdate(vstartdate);
		deptVO.setVenddate(new UFDate("9999-12-31", false));
		BDPKLockUtil.lockSuperVO(deptVO);
		// �汾���
		BDVersionValidationUtil.validateSuperVO(deptVO);
		BizlockDataUtil.lockDataByBizlock(deptVO);
		// �����Ϣ
		AuditInfoUtil.updateData(deptVO);
		// ���ݿ����
		baseDAO.updateVO(deptVO);

		// �����°汾deptvo����deptversion��
		HRDeptVO newDeptVO = getOMCommonQueryService().queryByPK(HRDeptVO.class, deptVO.getPk_dept());
		// ����HRDeptVO�õ�HRDeptVersionVO
		HRDeptVersionVO deptVersionVO = SuperVOHelper.createSuperVOFromSuperVO(newDeptVO, HRDeptVersionVO.class);
		BDPKLockUtil.lockSuperVO(deptVersionVO);
		// ���ݿ����
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
					// ��ѯ����Ҫ�����°汾��deptvo�ľɰ汾���ݣ����¾ɰ汾��venddate�ֶ�Ϊ�°汾��Ч����-1
					SuperVO oldEachOrgTypeVersionVO = eachTypevc.toArray((SuperVO[]) Array.newInstance(czz,
							eachTypevc.size()))[0];
					setOldVersionVOInfo(oldEachOrgTypeVersionVO, vstartdate);
					new OrgTypeServiceAdapterBasic().execEachOrgVersionUpdateOperate(oldEachOrgTypeVersionVO);

					// �����°汾��֯�������ݲ�����֯���Ͱ汾��Ϣ��
					SuperVO newEachTypeOrgVersionvo = getNewVersionVO(eachorgTypeVO);// ����deptvo�õ�deptversionvo

					setNewVersionVOInfo(newEachTypeOrgVersionvo, pk_deptvid, newDeptVO.getVname(), vno, vstartdate);

					new OrgTypeServiceAdapterBasic().execEachOrgVersionInsertOperate(newEachTypeOrgVersionvo);

					// ���¸���֯���ͱ��е���֯��������Ϊ���°汾
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
	 * Ϊ�°汾VO������ذ汾�ֶ�ֵ
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
	 * ������������֯�汾��ʹ�ã�����CorpVO����CorpVersionVO��DeptVO����DeptVersionVO......
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
	 * Ϊ�ɰ汾VO������ذ汾�ֶ�ֵ
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
		// ��ȡ�����Ƹ�λ
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
	 * ͨ������PK�����PostAdjustVO<br>
	 * 
	 * @param isMergedDept
	 * @param pk_depts
	 * @return
	 * @throws BusinessException
	 */
	private PostAdjustVO[] getPostAdjustVOByDeptPK(UFBoolean isMergedDept, String... pk_depts) throws BusinessException {
		// ��ȡ�����µĸ�λ
		// ��ʼ���Ը���
		PostAdjustVO[] postAdjustVOs = getDeptDao().retrievePostVOByDeptPK(pk_depts);

		for (int i = 0; i < postAdjustVOs.length; i++) {
			postAdjustVOs[i].setMergedpost(isMergedDept);
		}
		return postAdjustVOs;
	}

	@Override
	public PersonAdjustVO[] queryPersonAdjustVOForDeptMerge(DeptHistoryVO[] mergedDept, DeptHistoryVO takeOverDept,
			PostAdjustVO[] adjustedPostVO, PostAdjustVO[] savedPostVO) throws BusinessException {
		// step 1. ��ѯ���ϲ������µ�������Ա
		String[] mergedDeptPKs = SuperVOHelper.getAttributeValueArray(mergedDept, "pk_dept", String.class);
		PsndocVO[] psndocVOs = getPersonQueryService().queryPsndocVOByDeptPK(mergedDeptPKs, true, false);

		if (ArrayUtils.isEmpty(psndocVOs)) {
			return new PersonAdjustVO[0];
		}

		// ����йز��ŵ�VO
		AggHRDeptVO[] aggVO4Merg = queryByPks(mergedDeptPKs);
		HRDeptVO[] mergedDeptVOs = (HRDeptVO[]) AggVOHelper.getParentVOArrayFromAggVOs(aggVO4Merg, HRDeptVO.class);

		PsnJobVO[] psnJobVOs = new PsnJobVO[psndocVOs.length];
		for (int i = 0; i < psndocVOs.length; i++) {
			psnJobVOs[i] = psndocVOs[i].getPsnJobVO();
		}

		// ����йظ�λ��VO
		String[] allPostPKs = SuperVOHelper.getNotNullDistinctAttributeValueArray(psnJobVOs, PsnJobVO.PK_POST,
				String.class);

		PostVO[] allPostVOs = null;

		if (ArrayUtils.isEmpty(allPostPKs)) {// ����˶�û�����ø�λ
			allPostVOs = new PostVO[0];
		} else {
			AggPostVO[] aggPostVOs = getPostQueryService().queryByPks(allPostPKs);
			allPostVOs = SuperVOHelper.getParentVOArrayFromAggVOs(aggPostVOs, PostVO.class);
		}

		// ����й���ְ���͵�VO
		String[] allJobModePKs = SuperVOHelper.getNotNullDistinctAttributeValueArray(psndocVOs, PsnJobVO.JOBMODE,
				String.class);
		DefdocVO[] allJobModes = getDefdocQueryService().queryDefdocByPk(allJobModePKs);

		PersonAdjustVO[] personAdjustVOs = initPersonAdjustVO(psndocVOs, mergedDeptVOs, allPostVOs, allJobModes);

		// step 2. ����Ա���²��ŵĸ�λ��������
		// FIXME ����һ�������������и�λ����ƥ����Բ�ȥִ��ְ��ƥ����
		Map<String, Integer> postCache = new HashMap<String, Integer>();
		String[] pk_posts = SuperVOHelper.getAttributeValueArray(adjustedPostVO, "pk_post", String.class);

		for (int i = 0; i < pk_posts.length; i++) {
			postCache.put(pk_posts[i], i);
		}

		for (int i = 0; i < personAdjustVOs.length; i++) {
			PersonAdjustVO personAdjustVO = personAdjustVOs[i];
			String pk_oldpost = personAdjustVO.getPk_oldpost();
			if (postCache.containsKey(pk_oldpost)) {// �����λ��Ǩ�ƹ�ȥ��
													// �����λPKҪ��ɸ��ƺ�ģ�����Ҫע�⣬һ��Ҫ����adjustedPostVO��savedPostVO��¼��һһ��Ӧ��
				int index = postCache.get(pk_oldpost);
				personAdjustVO.setPk_newpost(savedPostVO[index].getPk_post());
				personAdjustVO.setNewpostcode(savedPostVO[index].getPostcode());
				SuperVOHelper.copyMultiLangAttribute(savedPostVO[index], personAdjustVO, new String[] { "postname" },
						new String[] { "newpostname" });
			}
			// �趨������Ϣ
			personAdjustVO.setPk_newdept(takeOverDept.getPk_dept());
			HRDeptVO deptVO = getOMCommonQueryService().queryByPK(HRDeptVO.class, takeOverDept.getPk_dept());
			SuperVOHelper.copyMultiLangAttribute(deptVO, personAdjustVO, new String[] { HRDeptVO.NAME },
					new String[] { "newdeptname" });

		}

		// �� "����+ְ��" ��ƥ��ĸ�λ����ֻ�ҵ�һ��ʱ��Ϊ�ø�λ��
		// �������ǣ����ڵ�һ��ѭ����ʱ�����ÿ��û�ҵ��ĸ�λ��ѯְ�񣬻���ѭ�����Σ�ְ����һ��SQL�������һЩ��

		// �õ�ְ�������б�
		// FIXME ���Ż���Ӧ��ȡ������ûƥ�䵽�ĸ�λ��Ӧ��ְ��
		String[] pk_jobs = SuperVOHelper.getNotNullAttributeValueArray(personAdjustVOs, "pk_job", String.class);
		Map<String, Set<PostVO>> postMap = getDeptDao().retrievePostVOMapByJobDept(pk_jobs, takeOverDept.getPk_dept());

		if (postMap.isEmpty()) {
			return personAdjustVOs;
		}

		for (PersonAdjustVO personAdjustVO : personAdjustVOs) {
			if (personAdjustVO.getPk_newpost() == null) {// ��λ�Զ�ƥ��
				String pk_job = personAdjustVO.getPk_job();
				Set<PostVO> postVOs = postMap.get(pk_job);
				// ��ְ���Ӧ�ĸ�λ����ֻ��һ��ʱ
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
	 * ��ʼ�����źϲ�ʱʹ�õĵ�����Ա��Ϣ��VO
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

		// step 1. ������ԴVO����ò���"ģ��"VO
		HRDeptVO[] sourceDeptVOs = buildTemplateDepts(wrapperVO.getSourceDeptVOs());

		DeptCopyConfirmVO[] copyConfirmVOs = wrapperVO.getTargetDeptVOs();

		// step 2. ��ȡ���ݿ��еĸ�λ��Ϣ
		AggPostVO[] toBeCopiedAggPostVOs = null;
		if (wrapperVO.isCopyPosts()) {// �����Ƹ�λ�Ļ�ֱ�ӷ���
			toBeCopiedAggPostVOs = buildTemplatePostVOs(sourceDeptVOs);
		}

		for (int i = 0; i < copyConfirmVOs.length; i++) {
			// step 3. ���벿����Ϣ�����ݿ�
			saveToBeCopiedDeptVOs(wrapperVO, copyConfirmVOs[i], sourceDeptVOs, deptPKBackupMap);
			if (!wrapperVO.isCopyPosts()) {// �����Ƹ�λ�Ļ�ֱ�ӷ���
				continue;
			}
			// step 4. �����λ��Ϣ�����ݿ�
			if (!ArrayUtils.isEmpty(toBeCopiedAggPostVOs)) {
				saveToBeCopiedAggPostVOs(wrapperVO, copyConfirmVOs[i], toBeCopiedAggPostVOs, deptPKBackupMap);
			}
		}

	}

	/**
	 * ���Ÿ��Ʋ���1��������ԴVO����ò���"ģ��"VO<br>
	 * 
	 * @param wrapperVO
	 * @return
	 * @throws BusinessException
	 */
	private HRDeptVO[] buildTemplateDepts(HRDeptVO[] sourceDeptVOs) throws BusinessException {
		// ���ڴ��и����ڲ��룬��������
		SuperVOHelper.updateInnerCode(sourceDeptVOs, HRDeptVO.PK_DEPT, HRDeptVO.PK_FATHERORG);

		// ���ȶԲ��Ž���������������ά���ڲ���
		sourceDeptVOs = SuperVOHelper.sort(sourceDeptVOs, new Comparator<HRDeptVO>() {
			@Override
			public int compare(HRDeptVO o1, HRDeptVO o2) {// �ò��Ű������¼��Ĺ�ϵ���뵽���ݿ�
															// ע���е����¼�������ֱ�����¼����������еĲ㼶
				if (o1.getInnercode() == null) {// ��o1���ϼ�����Ϊ��ʱ��o2Ϊ����ô��ƽ����o2��Ϊ��Ϊ�¼�
					return o2.getInnercode() == null ? 0 : -1;
				}
				if (o2.getInnercode() == null) {// ��o1���ϼ����Ų�Ϊ��ʱ��o2Ϊ�գ���ôo1Ϊo2���¼�
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
	 * ���Ÿ��Ʋ���2�����벿����Ϣ�����ݿ�<br>
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
			// �����趨ҵ��Ԫ����
			deptVO.setPk_org(confirmVO.getPk_org());
			// �����趨���롢���ơ���������
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
			// ִ�в���
			HRDeptVO newDeptVO = (HRDeptVO) insert(aggHRDeptVO).getParentVO();
			JFQueryOrgLogUtils.writeQueryDeptCopyLog("b3dbf5bc-768b-4b1f-b923-f6e315c0636a", newDeptVO, "CopyDept");

			// �γ��ϲ���PK���²���PK��ӳ�䣬���Ƶ������֯��Ҫ������֯��־
			deptPKBackupMap.put(confirmVO.getPk_org() + pk_dept, newDeptVO.getPk_dept());
		}
	}

	/**
	 * ���Ÿ��Ʋ���3����ȡ���ݿ��еĸ�λ��Ϣ���������޸ĺ������<br>
	 * 
	 * @param confirmVO
	 * @return
	 * @throws BusinessException
	 */
	private AggPostVO[] buildTemplatePostVOs(HRDeptVO[] sourceDeptVOs) throws BusinessException {
		String[] deptPKs = SuperVOHelper.getAttributeValueArray(sourceDeptVOs, "pk_dept", String.class);
		// �õ�ԭʼ��PostVO�����˳�����λ
		AggPostVO[] sourcePostVOs = getPostQueryService().queryAggPostVOsByDeptPKArray(deptPKs,
				"  and " + PostVO.HRCANCELED + " = 'N'");

		if (ArrayUtils.isEmpty(sourcePostVOs)) {
			// �����Ƹ�λ��Ϊ��
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
	 * ����ӱ�����
	 * 
	 * @param sourcePostVOs
	 * @throws BusinessException
	 */
	private void clearChildPK(AggPostVO[] sourcePostVOs) throws BusinessException {
		for (AggPostVO sourcePostVO : sourcePostVOs) {
			sourcePostVO.setTableVO(AggPostVO.POSTHISTORY_SUB, null);
			// �������ӱ�����Ϊinsert״̬�������ӱ��ܲ������ݿ�
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
	 * ���Ÿ��Ʋ���4�������λ��Ϣ�����ݿ�<br>
	 * 
	 * @param wrapperVO
	 * @param sourcePostVOs
	 * @throws BusinessException
	 */
	private void saveToBeCopiedAggPostVOs(DeptCopyWrapperVO wrapperVO, DeptCopyConfirmVO confirmVO,
			AggPostVO[] sourcePostVOs, Map<String, String> deptPKBackupMap) throws BusinessException {
		// ���ȶԸ�λ����������������ά���ڲ���
		List<AggPostVO> postVOList = new ArrayList<AggPostVO>();
		Collections.addAll(postVOList, sourcePostVOs);
		Collections.sort(postVOList, new Comparator<AggPostVO>() {

			@Override
			public int compare(AggPostVO aggVO1, AggPostVO aggVO2) {// �ø�λ�������¼��Ĺ�ϵ��������
																	// ע���е����¼�������ֱ�����¼����������еĲ㼶
				PostVO postVO1 = (PostVO) aggVO1.getParentVO();
				PostVO postVO2 = (PostVO) aggVO2.getParentVO();
				if (postVO1.getInnercode() == null) {// ��o1���ϼ���λΪ��ʱ��o2Ϊ����ô��ƽ����o2��Ϊ��Ϊ�¼�
					return postVO2.getInnercode() == null ? 0 : -1;
				}
				if (postVO2.getInnercode() == null) {// ��o1���ϼ���λ��Ϊ��ʱ��o2Ϊ�գ���ôo1Ϊo2���¼�
					return 1;
				}
				return postVO1.getInnercode().compareTo(postVO2.getInnercode());
			}

		});
		boolean isAutoGenerateBillCode = isAutoGenerateBillCode("post", wrapperVO.getContext().getPk_group(),
				confirmVO.getPk_org());

		// ��������ĸ�λ
		sourcePostVOs = postVOList.toArray(new AggPostVO[0]);
		clearChildPK(sourcePostVOs);
		String prefix = "ZD" + PubEnv.getServerDate().toStdString();
		// ���Զ����ɵ��ݺ� /Ĭ�Ϲ������� "��������+yyyy-mm-dd+_��ˮ��"
		String flowCode = getFlowCode(prefix);
		Map<String, String> postPKBackupMap = new HashMap<String, String>();
		// List<String> newPostPK = new ArrayList<String>();
		for (int i = 0; i < sourcePostVOs.length; i++) {
			// AggPostVO sourcePostVO = AggVOHelper.clone(sourcePostVOs[i],
			// AggPostVO.class);
			// AggVOHelper�Ŀ�¡�����ڿ�¡���ӱ�ʱ�������⣬�ָ�Ϊֱ��ʹ�ã������п�¡
			AggPostVO sourcePostVO = sourcePostVOs[i];
			PostVO toBeCopiedPostVO = (PostVO) sourcePostVO.getParentVO();
			// �趨���ڵ��µ���֯��Ԫ
			toBeCopiedPostVO.setPk_org(confirmVO.getPk_org());
			//
			toBeCopiedPostVO.setPk_hrorg(confirmVO.getPk_org());
			// ���ø�λ��������Ϊ����Ч����
			toBeCopiedPostVO.setBuilddate(new UFLiteralDate());
			String pk_post = toBeCopiedPostVO.getPk_post();
			String postcode = toBeCopiedPostVO.getPostcode();
			String suporior = toBeCopiedPostVO.getSuporior();
			toBeCopiedPostVO.setPk_post(null);
			toBeCopiedPostVO.setInnercode(null);

			// ���¸�λ�Ĳ�������
			String pk_dept = toBeCopiedPostVO.getPk_dept();
			String pk_newdept = deptPKBackupMap.get(confirmVO.getPk_org() + pk_dept);
			toBeCopiedPostVO.setPk_dept(pk_newdept);
			// �ڲ���ǰ��Ҫ�޸Ķ����һЩ��Ϣ
			// step 1. �жϱ����Ƿ��ظ�
			boolean duplicated = havePostCode(toBeCopiedPostVO);
			// step 2. ����ظ�����ô��Ҫ�Զ�����һ����ˮ�ű���������
			if (isAutoGenerateBillCode) {
				String billcode = NCLocator
						.getInstance()
						.lookup(IBillcodeManage.class)
						.getBillCode_RequiresNew(IOrgBillCodeConst.BILL_CODE_POST,
								wrapperVO.getContext().getPk_group(), confirmVO.getPk_org(), toBeCopiedPostVO);
				toBeCopiedPostVO.setPostcode(billcode);
			} else {
				if (duplicated) {
					// ���Զ����ɵ��ݺ�
					toBeCopiedPostVO.setPostcode(prefix + "_" + getFlowCode(flowCode, i));
				}
			}

			// FIXME ��λ����ʱ��
			// toBeCopiedPostVO.setBuilddate();
			/*
			 * // ����Ŀ����֯���ɼ��ĸ�λ�������ó�null if
			 * (StringUtils.isNotBlank(toBeCopiedPostVO.getPk_postseries())) {
			 * LoginContext context = wrapperVO.getContext(); String pk_bu =
			 * context.getPk_org(); OrgVO org =
			 * NCLocator.getInstance().lookup(IAOSQueryService
			 * .class).queryHROrgByOrgPK(pk_bu); if (org == null) throw new
			 * IllegalStateException("��ѯ����bu��pk = " + pk_bu + " ����Ӧ��HR��֯��");
			 * context.setPk_org(org.getPk_org());
			 * CrossOrgCopyFilter.filterByIncludeAttributs
			 * ("039385a6-3e60-489c-b257-f48a1b7ab046", new String[]{
			 * PostVO.PK_POSTSERIES, PostVO.PK_JOB}, new
			 * Object[]{toBeCopiedPostVO}, context, false, false);
			 * context.setPk_org(pk_bu); }
			 */
			// �ϼ���λ��Ϊ�գ������ϼ���λ���ڸ��Ʒ�Χ��
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
			// ִ�в���

			AggPostVO newPostVO = getPostManageService().insert(sourcePostVO);
			String newpk_post = ((PostVO) newPostVO.getParentVO()).getPk_post();
			postPKBackupMap.put(confirmVO.getPk_org() + pk_post, newpk_post);
			// ����Ϊ��Դ����������Ŀ��ҵ����֯Ϊ���ʱ������Ҫʹ����Դ����������
			toBeCopiedPostVO.setPk_dept(pk_dept);
			toBeCopiedPostVO.setPostcode(postcode);
			toBeCopiedPostVO.setSuporior(suporior);
		}
	}

	/**
	 * �õ�����������ˮ��
	 * 
	 * @param prefix
	 * @param codeField
	 * @param className
	 * @return ��λ��ˮ�� ��:00001
	 * @throws BusinessException
	 */
	public static String getFlowCode(String prefix) throws BusinessException {
		// ��ָ��ǰ׺���ҳ���Ϊ22��ZDXXXX1111-11-11_00001
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
					// ÿ��Ӧ�ò��ᳬ��10�򵥾�
					return StringUtils.leftPad(value + 1 + "", 5, '0');
				}
			} catch (NumberFormatException ex) {
				continue;
			}
		}
		return "00001";
	}

	/**
	 * ��ȡ��ǰ������ˮ��
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
	 * �жϸ�λ�����Ƿ��ظ�
	 * 
	 * @param prefix
	 * @param i
	 * @return boolean
	 * @throws BusinessException
	 */
	private boolean havePostCode(PostVO toBeCopiedPostVO) {
		try {
			// Ψһ��У��
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
		// ��Ϊǰ̨�Ѿ������������ڸ���ǰҪ��ȥ����̨����
		ILocker locker = getServiceTemplate().getLocker();
		getServiceTemplate().setLocker(new DoNotLock());

		// ��¼���ű����ʷ
		// ��ȡһ�����������
		historyVO.setChangenum(OidGenerator.getInstance().nextOid(OidGenerator.GROUP_PK_CORP));
		// ����������֯�汾ID
		String conOrg = OrgVersionVO.PK_ORG + " = '" + ((HRDeptVO) aggdeptVO.getParentVO()).getPk_org() + "' order by "
				+ OrgVersionVO.VNO + " desc";
		OrgVersionVO[] orgVersionVOs = getOMCommonQueryService().queryByCondition(OrgVersionVO.class, conOrg);
		if (!ArrayUtils.isEmpty(orgVersionVOs)) {
			historyVO.setPk_org_v(orgVersionVOs[0].getPk_vid());
		}
		getDeptDao().getBaseDAO().insertVO(historyVO);

		// ���ȸ��²�������
		AggHRDeptVO updatedVO = update(aggdeptVO, true);
		// JFQueryOrgLogUtils.writeQueryDeptLog("9a09be67-0c5d-4d45-bc1b-30a5d8f8ce6d",
		// ((HRDeptVO)updatedVO.getParentVO()).getPk_org(), "RenameOpeDept");

		// �ָ���
		getServiceTemplate().setLocker(locker);

		DeptRenameInfoVO renameInfoVO = new DeptRenameInfoVO();
		renameInfoVO.setUpdateCareer(updateCareer);
		renameInfoVO.setEffectDate(historyVO.getEffectdate());
		renameInfoVO.setDeptVO((HRDeptVO) updatedVO.getParentVO());

		// ����ҵ���¼�
		BusinessEvent afterEvent = new BusinessEvent(docName, IOMEventType.DEPT_RENAME_AFTER, renameInfoVO);
		EventDispatcher.fireEvent(afterEvent);

		return updatedVO;
	}

	@Override
	public AggHRDeptVO[] shift(AggHRDeptVO aggDeptVO, DeptHistoryVO historyVO) throws BusinessException {
		// HRDeptVO deptVO = aggDeptVO.getParentVO();
		// FIXME
		// ����ò�������Ա�����ڵ���״̬Ϊ��
		// ��д�С����ύ������е���ְ���뵥��ת�����뵥���������뵥����ְ���뵥�����������뵥��
		// �������뵥���������뵥���ݼ����뵥���Ӱ����뵥������ִ�С�
		BusinessEvent beforeEvent = new BusinessEvent(docName, IOMEventType.DEPT_SHIFT_BEFORE, aggDeptVO.getParentVO());
		EventDispatcher.fireEvent(beforeEvent);

		// ά�����������������֯�汾ID���С�
		String conOrg = OrgVersionVO.PK_ORG + " = '" + historyVO.getPk_org() + "' order by " + OrgVersionVO.VNO
				+ " desc";
		OrgVersionVO[] orgVersionVOs = getOMCommonQueryService().queryByCondition(OrgVersionVO.class, conOrg);
		if (!ArrayUtils.isEmpty(orgVersionVOs)) {
			historyVO.setPk_org_v(orgVersionVOs[0].getPk_vid());
		}
		// ��¼���ű����ʷ
		// ��ȡһ�����������
		historyVO.setChangenum(OidGenerator.getInstance().nextOid(OidGenerator.GROUP_PK_CORP));
		// ��ת�Ʋ��ű����ʷ
		getDeptDao().getBaseDAO().insertVO(historyVO);

		String receivedeptPK = historyVO.getPk_receivedept();

		if (!StringUtils.isEmpty(receivedeptPK)) {
			HRDeptVO receiveDeptVO = getOMCommonQueryService().queryByPK(HRDeptVO.class, receivedeptPK);

			// ��������
			historyVO.setCode(receiveDeptVO.getCode());
			SuperVOHelper.copyMultiLangAttribute(receiveDeptVO, historyVO);
			historyVO.setIsreceived(UFBoolean.TRUE);
			historyVO.setDeptlevel(receiveDeptVO.getDeptlevel());
			historyVO.setPk_dept(receiveDeptVO.getPk_dept());
			// ���ղ��ű����ʷ
			getDeptDao().getBaseDAO().insertVO(historyVO);
		}

		// ���²�������
		AggHRDeptVO updatedVO = update(aggDeptVO, true);
		// JFQueryOrgLogUtils.writeQueryDeptLog("ec8ea9c2-81af-4b79-9d70-629f45b6cb30",
		// ((HRDeptVO)aggDeptVO.getParentVO()).getPk_org(), "ShiftOpeDept");

		List<AggHRDeptVO> deptList = new ArrayList<AggHRDeptVO>();
		deptList.add(updatedVO);

		// �����ӱ���Ϣ
		if (!StringUtils.isEmpty(receivedeptPK)) {
			AggHRDeptVO aggReceiveDeptVO = queryByPk(receivedeptPK);
			deptList.add(aggReceiveDeptVO);
			// �����ӱ�
			handleDeptHistory(deptList.toArray(new AggHRDeptVO[0]));
		}

		return deptList.toArray(new AggHRDeptVO[0]);
	}

	@Override
	public PostAdjustVO[] savePostForDeptMerge(PostAdjustVO[] alreadySavedPostVOs, PostAdjustVO[] backupPostVOs,
			PostAdjustVO[] postAdjustVOs) throws BusinessException {
		// ���Ȼع�����ֹ���ָ��µ����
		rollbackPostForDeptMerge(alreadySavedPostVOs, backupPostVOs);
		// FIXME ������Щ���ӣ�ѭ����Щ�࣬���Ż�
		if (ArrayUtils.isEmpty(postAdjustVOs)) {// ����б�Ϊ��
			return new PostAdjustVO[0];
		}

		Map<String, PostAdjustVO> postAdjustVOCache = SuperVOHelper
				.buildAttributeToVOMap(PostVO.PK_POST, postAdjustVOs);
		Map<String, Integer> postAdjustIndexCache = SuperVOHelper.buildIndexMap(PostVO.PK_POST, postAdjustVOs);

		PostAdjustVO[] savedPostVOs = new PostAdjustVO[postAdjustVOs.length];
		String[] postPKs = SuperVOHelper.getAttributeValueArray(postAdjustVOs, "pk_post", String.class);

		AggPostVO[] aggPostVOs = getPostQueryService().queryAggPostVOsByPostPKArray(postPKs, null);

		PostVO[] postVOs = SuperVOHelper.getParentVOArrayFromAggVOs(aggPostVOs, PostVO.class);

		// ��Ҫ���뵽���ݿ�ļ�¼
		Set<PostVO> mergedPosts = new HashSet<PostVO>();

		// ��Ҫ���µĸ�λ����
		List<PostVO> postUpdateList = new ArrayList<PostVO>();
		for (PostVO postVO : postVOs) {
			PostAdjustVO postAdjustVO = postAdjustVOCache.get(postVO.getPk_post());
			if (postAdjustVO == null) {
				continue;
			}

			// ���Ʊ�������
			postVO.setPostcode(postAdjustVO.getPostcode());
			SuperVOHelper.copyMultiLangAttribute(postAdjustVO, postVO, new String[] { PostVO.POSTNAME });

			if (UFBoolean.TRUE.equals(postAdjustVO.getMergedpost())) {// ����Ǵ����Ʋ��ŵĸ�λ
				postVO.setPk_dept(postAdjustVO.getPk_dept());
				// �����Ʋ��ŵĸ�λ������¼�
				postVO.setSuporior(null);
				postVO.setJunior(null);
				postVO.setBuilddate(postAdjustVO.getBuilddate());
				mergedPosts.add(postVO);
			} else {// �����ԭ�и�λ
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

		// ��������Ƹ�λ
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
		// ��������
		postVO.setPk_post(ids[offset]);
		// ����λ����Ϣ
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
		if (!ArrayUtils.isEmpty(savedPostVOs)) {// �Ѿ����������
			List<String> toBeDelPosts = new ArrayList<String>();
			for (int i = 0; i < savedPostVOs.length; i++) {
				if (UFBoolean.TRUE.equals(savedPostVOs[i].getMergedpost())) {// ����Ǵ��ϲ���
					toBeDelPosts.add(savedPostVOs[i].getPk_post());
				}
			}

			if (toBeDelPosts.size() > 0) {
				for (String postPk : toBeDelPosts) {
					getPostManageService().delete(postPk);
				}
			}
		}

		if (!ArrayUtils.isEmpty(backupPostVOs)) {// ԭ�еĸ�λ����
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

		// step 0. ������Ա�����Ϣ
		// ������Ա�ĸ�λ��ְ�񣬲��ţ���֯��Ϣ
		String[] mergedDeptPKs = SuperVOHelper.getAttributeValueArray(wrapperVO.getMergedDeptHistoryVOs(), "pk_dept",
				String.class);

		DeptMergeInfoVO deptMergeInfoVO = new DeptMergeInfoVO();
		AggHRDeptVO takeOverDeptVO = queryByPk(wrapperVO.getTakeOverDeptHistoryVO().getPk_dept());
		PersonAdjustVO[] personAdjustVOs = wrapperVO.getPersonAdjustVO();

		if (!ArrayUtils.isEmpty(personAdjustVOs)) {
			// ����ǽ��ղ��������ⲿ�����ܽ���Աת��
			if (((HRDeptVO) takeOverDeptVO.getParentVO()).getDepttype() == 1) {
				throw new BusinessException(ResHelper.getString("6005dept", "06005dept0378"/*
																							 * @
																							 * res
																							 * "���ⲿ��[{0}]�²�������Ա!"
																							 */,
						((HRDeptVO) takeOverDeptVO.getParentVO()).getMultilangName()));
			}
			// ѭ���в���ְ��¼���Ż�
			// ��ְ��¼����
			ArrayList<String> psnjobPks = new ArrayList<String>();
			for (PersonAdjustVO personAdjustVO : personAdjustVOs) {
				if (psnjobPks.contains(personAdjustVO.getPk_psnjob()))
					continue;
				psnjobPks.add(personAdjustVO.getPk_psnjob());
			}
			PsnJobVO[] tmpPsnjobVOs = getOMCommonQueryService().queryByPKs(PsnJobVO.class,
					psnjobPks.toArray(new String[0]));
			// ��ְ��¼������ӦVO��map
			HashMap<String, PsnJobVO> psnjobMap = new HashMap<String, PsnJobVO>();
			for (PsnJobVO tmpPsnjobVO : tmpPsnjobVOs) {
				psnjobMap.put(tmpPsnjobVO.getPk_psnjob(), tmpPsnjobVO);
			}

			// ѭ���в��λ�Ż�
			// ��λ����
			ArrayList<String> postPks = new ArrayList<String>();
			for (PersonAdjustVO personAdjustVO : personAdjustVOs) {
				if (null == personAdjustVO.getPk_newpost() || postPks.contains(personAdjustVO.getPk_newpost()))
					continue;

				postPks.add(personAdjustVO.getPk_newpost());
			}
			PostVO[] tmpPostVOs = getOMCommonQueryService().queryByPKs(PostVO.class, postPks.toArray(new String[0]));
			// ��λ������ӦVO��map
			HashMap<String, PostVO> postMap = new HashMap<String, PostVO>();
			if (tmpPostVOs != null) {
				for (PostVO tmpPostVO : tmpPostVOs) {
					postMap.put(tmpPostVO.getPk_post(), tmpPostVO);
				}

			}

			// ѭ���в�ְ���Ż������ݴ���pk_job���������λVO ��pk_job
			ArrayList<String> jobPks = new ArrayList<String>();
			if (tmpPostVOs != null) {
				for (PostVO tmpPostVO : tmpPostVOs) {
					if (StringUtils.isEmpty(tmpPostVO.getPk_job()) || jobPks.contains(tmpPostVO.getPk_job()))
						continue;
					jobPks.add(tmpPostVO.getPk_job());
				}
			}
			JobVO[] tmpJobVOs = getOMCommonQueryService().queryByPKs(JobVO.class, jobPks.toArray(new String[0]));
			// ְ��������ӦVO��map
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

				// ��������Ѿ��������춯���Ͳ�Ϊ�գ����춯�������
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

				if (pk_job == null) {// ��λ����Ϊ�գ�����û�б仯
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

		// TODO ִ�в��źϲ�
		// ִ�в��źϲ�

		// Step 1. ��¼���ű����ʷ
		// ���²��ű����ʷ
		// ��ȡһ�����������
		String changeNum = OidGenerator.getInstance().nextOid(OidGenerator.GROUP_PK_CORP);

		AggHRDeptVO[] aggVOs = queryByPks(mergedDeptPKs);
		HRDeptVO[] mergedDeptVOs = (HRDeptVO[]) AggVOHelper.getParentVOArrayFromAggVOs(aggVOs, HRDeptVO.class);

		Map<String, HRDeptVO> mergedDeptVOMap = SuperVOHelper.buildPrimaryKeyToVOMap(mergedDeptVOs);

		// ���ϲ����źͽ��ܲ��ŵı����ʷ����ͬһ�����������
		for (DeptHistoryVO mergedDeptHistoryVO : wrapperVO.getMergedDeptHistoryVOs()) {
			mergedDeptHistoryVO.setChangenum(changeNum);
			HRDeptVO deptVO = mergedDeptVOMap.get(mergedDeptHistoryVO.getPk_dept());
			if (deptVO == null) {
				continue;
			}
			// ����
			mergedDeptHistoryVO.setCode(deptVO.getCode());
			// ������
			SuperVOHelper.copyMultiLangAttribute(deptVO, mergedDeptHistoryVO);
			// ���ż���
			mergedDeptHistoryVO.setDeptlevel(deptVO.getDeptlevel());
			// ������
			mergedDeptHistoryVO.setPrincipal(deptVO.getPrincipal());
			// ��֯
			mergedDeptHistoryVO.setPk_org(deptVO.getPk_org());
		}

		HRDeptVO takeOverDept = (HRDeptVO) queryByPk(wrapperVO.getTakeOverDeptHistoryVO().getPk_dept()).getParentVO();
		wrapperVO.getTakeOverDeptHistoryVO().setChangenum(changeNum);
		// ���ż���
		wrapperVO.getTakeOverDeptHistoryVO().setDeptlevel(takeOverDept.getDeptlevel());
		// ������
		wrapperVO.getTakeOverDeptHistoryVO().setPrincipal(takeOverDept.getPrincipal());
		// ������
		SuperVOHelper.copyMultiLangAttribute(takeOverDept, wrapperVO.getTakeOverDeptHistoryVO());

		// ͨ������ʵ��
		// ����ò�������Ա�����ڵ���״̬Ϊ��
		// ��д�С����ύ������е���ְ���뵥��ת�����뵥���������뵥����ְ���뵥�����������뵥��
		// �������뵥���������뵥���ݼ����뵥���Ӱ����뵥������ִ�С�
		BusinessEvent beforeEvent = new BusinessEvent(docName, IOMEventType.DEPT_MERGE_BEFORE, wrapperVO);
		EventDispatcher.fireEvent(beforeEvent);

		// ��¼�����ʷ
		getDeptDao().getBaseDAO().insertVOArray(wrapperVO.getMergedDeptHistoryVOs());
		getDeptDao().getBaseDAO().insertVO(wrapperVO.getTakeOverDeptHistoryVO());

		Set<String> mergedDeptPKCache = new HashSet<String>();
		Collections.addAll(mergedDeptPKCache, mergedDeptPKs);

		List<AggHRDeptVO> resultList = new ArrayList<AggHRDeptVO>();

		// �б��еĵ�һ���ǽ��ղ���
		resultList.add(takeOverDeptVO);

		// ������ְ��¼,ͬ����������
		// �������ڵļ�ְ��Ա����ֹ��ְ��¼
		// ����ҵ���¼�
		BusinessEvent afterEvent = new BusinessEvent(docName, IOMEventType.DEPT_MERGE_AFTER, wrapperVO);
		EventDispatcher.fireEvent(afterEvent);
		// �ȳ�����Ҫ���������µĸ�λ���ٳ����ò��ţ���������λʱ����λУ��ʧ�ܡ� heqiaoa 20150504
		cancelPostsOfDept(mergedDeptPKs, wrapperVO.getTakeOverDeptHistoryVO().getEffectdate());

		// step 3. �����¼����ţ�step 4. �����ϲ�������ɳ���
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
				if (ArrayUtils.isEmpty(childDeptAggVOs)) { // ���б��ϲ����Ŷ�û���Ӳ��ţ�����ѭ��
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
			// TODO ��֪�Ƿ���ȷ
			AggHRDeptVO savedVO = update(aggVOs[i], false);
			mergedDeptVOs[i] = (HRDeptVO) savedVO.getParentVO();
			resultList.add(savedVO);

			// ��ѯ�����е�ֱ���¼�
			// **********��ʱ�Ȳ��Ż�
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

			// ҵ����־
			JFQueryOrgLogUtils.writeQueryDeptCopyLog("89788d4d-679b-42ac-b16f-e72381068f93", mergedDeptVOs[i],
					"MergerOpeDept");

			if (ArrayUtils.isEmpty(childDeptVOs)) {// û���¼�
				continue;
			}

			for (int j = 0; j < childDeptVOs.length; j++) {
				HRDeptVO childDeptVO = childDeptVOs[j];
				// �����ж���Щ�¼��������Ƿ�������ϲ�����
				if (mergedDeptPKCache.contains(childDeptVO.getPk_dept())) {
					continue;
				}
				// Ϊ�¼����Ŵ������ű����ʷ������Ϊת��

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
		// step 2. �����ϲ����ŵĸ�λ
		// �����ϲ����ŵĸ�λ��ɳ���

		// // ���ϲ����ŵĸ�λȫ�����������´����ݿ����
		// InSQLCreator isc = new InSQLCreator();
		//
		// String deptPks = isc.getInSQL(mergedDeptPKs);
		// String conditon =
		// PostVO.PK_DEPT + " in (" + deptPks + ") and (" + PostVO.HRCANCELED +
		// " = 'N' or " +
		// PostVO.HRCANCELED + " = '~' )";
		// // ���������ȳ����¼���λ
		// conditon += " order by " + PostVO.INNERCODE + " desc";
		// @SuppressWarnings("unchecked")
		// List<AggPostVO> postList = (List<AggPostVO>)
		// getMDQueryService().queryBillOfVOByCond(PostVO.class,
		// conditon, false);
		// if (postList != null && postList.size() > 0)
		// {
		// for (AggPostVO aggPostVO : postList)
		// {
		// // *********û���Ż�ò��********
		// getPostManageService()
		// .cancel((PostVO) aggPostVO.getParentVO(),
		// wrapperVO.getTakeOverDeptHistoryVO().getEffectdate(),
		// false);
		// }
		// }
		AggHRDeptVO[] resultVOs = resultList.toArray(new AggHRDeptVO[0]);

		// *********������ѭ����ѯ******
		// �����ӱ�
		handleDeptHistory(resultVOs);

		return resultVOs;

	}

	/**
	 * ���������µĸ�λ
	 * 
	 * @author heqiaoa
	 * @throws BusinessException
	 */
	private void cancelPostsOfDept(String[] mergedDeptPKs, UFLiteralDate effectiveDate) throws BusinessException {
		// ���ϲ����ŵĸ�λȫ�����������´����ݿ����
		InSQLCreator isc = new InSQLCreator();

		String deptPks = isc.getInSQL(mergedDeptPKs);
		String conditon = PostVO.PK_DEPT + " in (" + deptPks + ") and (" + PostVO.HRCANCELED + " = 'N' or "
				+ PostVO.HRCANCELED + " = '~' )";
		// ���������ȳ����¼���λ
		conditon += " order by " + PostVO.INNERCODE + " desc";
		@SuppressWarnings("unchecked")
		List<AggPostVO> postList = (List<AggPostVO>) getMDQueryService().queryBillOfVOByCond(PostVO.class, conditon,
				false);
		if (postList != null && postList.size() > 0) {
			for (AggPostVO aggPostVO : postList) {
				// *********û���Ż�ò��********
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
		// ͨ������ʵ��
		// ����ò�������Ա�����ڵ���״̬Ϊ��
		// ��д�С����ύ������е���ְ���뵥��ת�����뵥���������뵥����ְ���뵥�����������뵥��
		// �������뵥���������뵥���ݼ����뵥���Ӱ����뵥������ִ�С�
		// ���鵱ǰ���ź��¼������Ƿ������ְ��Ա�������������ô��������
		EventDispatcher.fireEvent(new BusinessEvent(docName, IOMEventType.DEPT_CANCEL_BEFORE, eventVOs));

		// ��ȡһ�����������
		historyVO.setChangenum(OidGenerator.getInstance().nextOid(OidGenerator.GROUP_PK_CORP));

		// ά�����������������֯�汾ID���С�
		String conOrg = OrgVersionVO.PK_ORG + " = '" + historyVO.getPk_org() + "' order by " + OrgVersionVO.VNO
				+ " desc";
		OrgVersionVO[] orgVersionVOs = getOMCommonQueryService().queryByCondition(OrgVersionVO.class, conOrg);
		if (!ArrayUtils.isEmpty(orgVersionVOs)) {
			historyVO.setPk_org_v(orgVersionVOs[0].getPk_vid());
		}
		// ���Ĳ�ѯ��־
		vo.setHrcanceled(UFBoolean.TRUE);
		// ��������
		vo.setDeptcanceldate(historyVO.getEffectdate());

		List<DeptHistoryVO> historyVOCache = new ArrayList<DeptHistoryVO>();
		List<HRDeptVO> deptVOCache = new ArrayList<HRDeptVO>();
		historyVOCache.add(historyVO);
		deptVOCache.add(vo);
		deptPkAggHRDeptVOMap.put(vo.getPrimaryKey(), aggvo);

		// step 1. ���������Ӳ��ŵı����ʷVO
		if (!ArrayUtils.isEmpty(childDeptVOs)) {
			for (HRDeptVO childDeptVO : childDeptVOs) {
				if (UFBoolean.TRUE.equals(childDeptVO.getHrcanceled())) {// ��������Ѿ�����������Ҫ����
					continue;
				}
				childDeptVO.setHrcanceled(UFBoolean.TRUE);
				childDeptVO.setDeptcanceldate(historyVO.getEffectdate());
				DeptHistoryVO childHistoryVO = SuperVOHelper.createSuperVOFromSuperVO(childDeptVO, DeptHistoryVO.class);

				// ��¼������֯�����°汾ID��
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

		// ��¼���ű����ʷ
		getDeptDao().getBaseDAO().insertVOArray(historyVOCache.toArray(new DeptHistoryVO[historyVOCache.size()]));

		// step 2. ������λ
		// ��ѯ�����и�λ
		Set<PostVO> postVOCache = new HashSet<PostVO>();
		for (HRDeptVO deptVO : deptVOCache) {
			PostVO[] postVOs = getPostQueryService().queryPostVOsByDeptPK(deptVO.getPk_dept(), false);
			Collections.addAll(postVOCache, postVOs);
		}
		// ��λ�����ȳ����¼���λ
		PostVO[] postVOs = postVOCache.toArray(new PostVO[0]);
		SuperVOHelper.sort(postVOs, new String[] { PostVO.INNERCODE }, new boolean[] { false }, true);

		if (!ArrayUtils.isEmpty(postVOs)) {
			for (PostVO postVO : postVOs) {
				if (UFBoolean.FALSE.equals(postVO.getHrcanceled())) {
					getPostManageService().cancel(postVO, historyVO.getEffectdate(), false);
				}
			}
		}

		// step 3. ִ�г���
		List<AggHRDeptVO> resultVO = new ArrayList<AggHRDeptVO>();

		HRDeptVO[] canceledDeptVOs = deptVOCache.toArray(new HRDeptVO[0]);
		SuperVOHelper.sort(canceledDeptVOs, new String[] { HRDeptVO.INNERCODE }, new boolean[] { false }, true);

		for (HRDeptVO deptVO : canceledDeptVOs) {
			// ��map��ȡֵȻ���޸ģ�����û�еģ����û����ô�����쳣��
			AggHRDeptVO updateDeptVO = update(deptPkAggHRDeptVOMap.get(deptVO.getPk_dept()), false);
			/*
			 * ����ʱ��ִ��ͣ�� // ���ִ��ͣ�� if (disableDept) { if (((HRDeptVO)
			 * updateDeptVO.getParentVO()).getEnablestate() ==
			 * IPubEnumConst.ENABLESTATE_ENABLE) { TreeBaseService<DeptVO>
			 * baseService = new
			 * TreeBaseService<DeptVO>(IOrgMetaDataIDConst.DEPT,
			 * ITreePersistenceUtil.METADATA_PERSISTENCE_STYLE);
			 * baseService.disableVO((DeptVO) updateDeptVO.getParentVO()); } }
			 */
			resultVO.add(updateDeptVO);
		}

		// step 4. �Լ�ְ��Ա���д�������ֹ��־����ֹ���ڸı�
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
		// �ж��ϼ������Ƿ��ڳ���״̬
		if (vo.getPk_fatherorg() != null) {
			HRDeptVO parentDeptVO = getOMCommonQueryService().queryByPK(HRDeptVO.class, vo.getPk_fatherorg());
			if (UFBoolean.TRUE.equals(parentDeptVO.getHrcanceled())) {
				throw new BusinessException(ResHelper.getString("6005dept", "06005dept0347")/*
																							 * @
																							 * res
																							 * "�ò����ϼ����ŵĵ�ǰ״̬�ǳ��������ȶ��ϼ����Ž���ȡ������������"
																							 */);
			}
		}

		Map<String, AggHRDeptVO> deptPkAggHRDeptVOMap = new HashMap<String, AggHRDeptVO>();

		// ��ȡһ�����������
		historyVO.setChangenum(OidGenerator.getInstance().nextOid(OidGenerator.GROUP_PK_CORP));

		// ά�����������������֯�汾ID���С�
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

		// step 1. ���������Ӳ��ŵı����ʷVO
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

					// ���²���������֯�İ汾ID��
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

		// step 3. ִ�в��ŷ�����
		List<AggHRDeptVO> resultVO = new ArrayList<AggHRDeptVO>();

		for (HRDeptVO deptVO : deptVOCache) {
			AggHRDeptVO updateDeptVO = update(deptPkAggHRDeptVOMap.get(deptVO.getPk_dept()), false);
			// ���ִ������
			if (enableDept) {
				if (((HRDeptVO) updateDeptVO.getParentVO()).getEnablestate() != IPubEnumConst.ENABLESTATE_ENABLE) {
					updateDeptVO = enable(updateDeptVO);
				}
			}
			resultVO.add(updateDeptVO);
		}

		// step 2. �������µ����и�λ
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
	 * ��ѯת�Ʋ�����Ϣ
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
			// ��ѯ��λ��Ϣ
			for (DeptTransDeptVO transDeptVO : transDeptVOs) {
				transDeptVO.setTransPostVOs(queryTransPostInf(transDeptVO, ruleVO, refNameVOs, context));
			}
		}

		return transDeptVOs;
	}

	/**
	 * ��ѯת�Ʋ�����Ϣ
	 * 
	 * @param ruleVO
	 * @param refAggVOs
	 * @return DeptTransDeptVO[]
	 * @throws BusinessException
	 */
	private DeptTransDeptVO[] queryTransDeptInf(DeptTransRuleVO ruleVO, DeptTransRefNameVO[] refNameVOs)
			throws BusinessException {
		// ת�Ʋ���������ʱ��
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
			// Ŀ�겿�ű��� = ������Ŀ�겿�ű���ǰ׺ + ԭ���ű���
			String prefixcode = getCodePrefix(ruleVO.getAimdeptcode(), hrOrgVO, supDeptVO);
			if (prefixcode != null)
				transDeptVO.setAimdeptcode(prefixcode + deptcode);
			else
				transDeptVO.setAimdeptcode(deptcode);

			String deptname = deptVO.getMultilangName();
			transDeptVO.setOlddeptname(deptname);

			// Ŀ�겿�ű��� = ������Ŀ�겿�ű���ǰ׺ + ԭ���ű���
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
	 * ��ñ���ǰ׺<br>
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
																	 * "Ŀ��ҵ��Ԫ����"
																	 */.equals(prefixcode)) {
			// Ŀ��ҵ��Ԫ����
			return hrOrgVO.getCode();
		} else if (ResHelper.getString("6005dept", "06005dept0181")/*
																	 * @res
																	 * "Ŀ��ҵ��Ԫ���ϼ����ű���"
																	 */.equals(prefixcode) && supDeptVO != null) {
			// Ŀ��ҵ��Ԫ���ϼ����ű���
			return supDeptVO.getCode();
		} else {
			// �Զ���ҵ��Ԫ����
			return prefixcode;
		}
	}

	/**
	 * ��������ǰ׺<br>
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
																	 * "Ŀ��ҵ��Ԫ����"
																	 */.equals(prefixname)) {
			// Ŀ��ҵ��Ԫ����
			transDeptVO.setAimdeptname(join(hrOrgVO.getName(), deptVO.getName()));
			transDeptVO.setAimdeptname2(join(hrOrgVO.getName2(), deptVO.getName2()));
			transDeptVO.setAimdeptname3(join(hrOrgVO.getName3(), deptVO.getName3()));
			transDeptVO.setAimdeptname4(join(hrOrgVO.getName4(), deptVO.getName4()));
			transDeptVO.setAimdeptname5(join(hrOrgVO.getName5(), deptVO.getName5()));
			transDeptVO.setAimdeptname6(join(hrOrgVO.getName6(), deptVO.getName6()));
		} else if (ResHelper.getString("6005dept", "06005dept0184")/*
																	 * @res
																	 * "Ŀ��ҵ��Ԫ���"
																	 */.equals(prefixname)) {
			// Ŀ��ҵ��Ԫ���
			transDeptVO.setAimdeptname(join(hrOrgVO.getShortname(), deptVO.getName()));
			transDeptVO.setAimdeptname2(join(hrOrgVO.getShortname2(), deptVO.getName2()));
			transDeptVO.setAimdeptname3(join(hrOrgVO.getShortname3(), deptVO.getName3()));
			transDeptVO.setAimdeptname4(join(hrOrgVO.getShortname4(), deptVO.getName4()));
			transDeptVO.setAimdeptname5(join(hrOrgVO.getShortname5(), deptVO.getName5()));
			transDeptVO.setAimdeptname6(join(hrOrgVO.getShortname6(), deptVO.getName6()));
		} else if (ResHelper.getString("6005dept", "06005dept0185")/*
																	 * @res
																	 * "Ŀ��ҵ��Ԫ���ϼ���������"
																	 */.equals(prefixname)) {
			// Ŀ��ҵ��Ԫ���ϼ���������
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
			// �Զ���Ŀ��ҵ��Ԫ����
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
	 * ��������ǰ׺<br>
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
	 * ��ѯת�Ƹ�λ��Ϣ
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
		// ����ϵͳ��������ѡ��Ŀ���λ�Ƿ�����Զ�����
		List<DeptTransPostVO> transPostVOs = new ArrayList<DeptTransPostVO>();
		for (PostVO postVO : postVOs) {
			transPostVO = new DeptTransPostVO();
			// if (isAutoGenCode) {// �Զ�����Ŀ���λ����
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
	 * ��ѯת�Ʋ���ʱ��Ҫƥ��Ĳ�����Ϣ
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
			// FIXME ��Ա��𡢲��ŵĲ��ż��𡢸�λ���С�������Ŀ���õ��Զ��嵵������Ϊȫ�ּ�&���ż����գ�����Ҫ���ö�Ӧ��ϵ

			// ְ��
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
	 * ͨ������������ȡ���ղ�������
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
	 * Ϊ���Ÿ���У�鲿�ŵı�������Ψһ��<br>
	 * 
	 * @param copyConfirmVOs
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public DeptCopyValidateVO validateDeptForCopy(DeptCopyConfirmVO[] copyConfirmVOs) throws BusinessException {
		// FIXME ͨ��ΨһУ�����ʵ��
		DeptCopyValidateVO validateVO = new DeptCopyValidateVO();
		for (DeptCopyConfirmVO copyConfirmVO : copyConfirmVOs) {
			DeptCopyAdjustVO[] copyAdjustVOs = copyConfirmVO.getTargetDeptVOs();

			// ҵ��Ԫ����
			String pk_org = copyConfirmVO.getPk_org();
			// ���ű���
			String[] deptcodes = SuperVOHelper.getAttributeValueArray(copyAdjustVOs, HRDeptVO.CODE, String.class);
			// ��������
			String[] deptnames = SuperVOHelper.getAttributeValueArray(copyAdjustVOs, HRDeptVO.NAME, String.class);
			// ��������
			String[] deptPKs = SuperVOHelper.getAttributeValueArray(copyAdjustVOs, HRDeptVO.PK_DEPT, String.class);

			// �жϱ��롢����Ψһ��
			String[] codeErrorPKs = getDeptDao().deptCodeUniqueValidate(pk_org, deptcodes, deptPKs);
			String[] nameErrorPKs = getDeptDao().deptNameUniqueValidate(pk_org, deptnames, deptPKs);

			// ��ǰҵ��Ԫ�Ƿ���Ψһ�Դ���
			boolean corpHasError = false;

			// ����У����VO
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
	 * �Կ�ҵ��Ԫ����ת��Ŀ�겿�ű����Ŀ�겿�������ظ���У��
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

		// �ظ���Ŀ�겿�ű����Ӧ��pk_depts
		List<String> errorDeptCodes = new ArrayList<String>();
		// �ظ���Ŀ�겿�����ƶ�Ӧ��pk_depts
		List<String> errorDeptNames = new ArrayList<String>();
		// ҳ��Ŀ������ظ�����֤
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
	 * �Կ�ҵ��Ԫ����ת��Ŀ���λ�����ظ���У��
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

		// �ظ���Ŀ���λ�����Ӧ��ԭpk_posts
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
	 * У��ѡ���ת�Ʋ������Ƿ����δ�뵵����Ա
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
																							 * "��ѡת�Ʋ���[{0}]����δ�뵵��Ա��"
																							 */);
		}

		String dept2 = "";
		// У���Ƿ����ڻ�еĵ��ݵ���
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
																		 * "��ת�Ʋ���[{0}]����Ա�д���δ��ɵĵ��ݣ�"
																		 */, dept2.substring(1)));
		}

		return message.toString();

	}

	/**
	 * ���ݼ���+��֯+�ƶ����Ͳ�ѯ������Ŀ
	 * 
	 * @param pk_group
	 *            ����
	 * @param pk_org
	 *            ��֯
	 * @param trnstype
	 *            �춯����
	 * @return DeptTransItemVO[]
	 * @throws BusinessException
	 */
	@Override
	public DeptTransItemVO[] queryItemSetByOrg(String pk_group, String pk_org, String pk_trnstype)
			throws BusinessException {
		// ��ѯ������Ŀ
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
	 * У��ѡ���ת�Ʋ������Ƿ����δ�뵵����Ա
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
																	 * "ԭ{0}"
																	 */, "{0}", deptTransItemVO.getItemname()));
		} else {
			deptTransItemVO.setItemname(StringUtil.replaceAllString(
					ResHelper.getString("6005dept", "06005dept0351")/*
																	 * @ res
																	 * "Ŀ��{0}"
																	 */, "{0}", deptTransItemVO.getItemname()));
		}

		RefInfoVO refInfoVO = (StringUtils.isBlank(ref_model_name) ? null : new SimpleDocServiceTemplate("refinfo")
				.queryByPk(RefInfoVO.class, ref_model_name));
		if (refInfoVO != null) {
			deptTransItemVO.setRefName(refInfoVO.getName());
		}

	}

	/**
	 * ��ѯת����Ա��Ϣ
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
		// ת�Ʋ���������ʱ��
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
					// ���ݵ�����Ŀ����
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
		// ����
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
	 * ִ�п�ҵ��Ԫ����ת��
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
			// 1.1 ���ݲ���ת�ƹ����ò���"ģ��"VO
			HRDeptVO[] sourceDeptVOs = buildTemplateDepts(ruleVO.getSourceDeptVOs());
			Map<String, String> deptPKBackupMap = new HashMap<String, String>();
			String fatherDeptPK = ruleVO.getAimdept();
			AggHRDeptVO[] newAggDeptVOs = new AggHRDeptVO[sourceDeptVOs.length];

			HRDeptVO deptVO;
			AggHRDeptVO aggHRDeptVO;
			// 1.2 ���벿����Ϣ�����ݿ�
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
				// ִ�в���
				newAggDeptVOs[i] = insert(aggHRDeptVO);
				HRDeptVO newDeptVO = (HRDeptVO) newAggDeptVOs[i].getParentVO();

				// �γ��ϲ���PK���²���PK��ӳ��
				deptPKBackupMap.put(pk_dept, newDeptVO.getPk_dept());

				// �޸����е�ǰ�Ѿ�����Ĳ��ŵ��Ӳ��ŵ�pk_fatherorgΪ���µ�
				for (int j = i + 1; j < sourceDeptVOs.length; j++) {
					String pk_fatherorg = sourceDeptVOs[j].getPk_fatherorg();
					if (pk_fatherorg != null && pk_fatherorg.equals(pk_dept)) {// �޸������Ӳ���
						sourceDeptVOs[j].setPk_fatherorg(newDeptVO.getPk_dept());
					}
				}

			}

			// 2.1 ��ȡ���ݿ��еĸ�λ��Ϣ
			// �¾�����ӳ��Map
			Map<String, String> old_newPostpkmap = new HashMap<String, String>();
			AggPostVO[] sourcePostVOs = buildTemplatePostVOs(sourceDeptVOs);
			reSetTransPostInf(sourcePostVOs, ruleVO, refNameVOs, transDeptVOs);
			// 2.2 �����λ��Ϣ�����ݿ�
			PostVO[] insertPostVOs = null;
			if (!ArrayUtils.isEmpty(sourcePostVOs)) {
				insertPostVOs = new PostVO[sourcePostVOs.length];

				savePostVOs(ruleVO.getNeworg(), ruleVO.getContext().getPk_group(), sourcePostVOs, deptPKBackupMap,
						old_newPostpkmap, insertPostVOs);
			}
			// �¾���Ա������¼����
			Map<String, String> psndocPKMap = new HashMap<String, String>();
			// �²��ŵ�����List
			List<String> newDeptPk = new ArrayList<String>();

			// 3.1 ת����ְ��Ա
			doTransPsnInf(ruleVO, insertPostVOs, deptPKBackupMap, old_newPostpkmap, transItemVOs, transPsnInfVOs,
					psndocPKMap);

			// 3.2 ��ְ��Ա����
			doTransPartPsnInf(ruleVO, insertPostVOs, deptPKBackupMap, old_newPostpkmap);

			// 3.3 ��ְ��Ա��������������ְ��Ա��������
			// doTransDimissionPsnInf(ruleVO, deptPKBackupMap);
			// 3.4 �����Ա����
			doTransPoiPsnInf(ruleVO, deptPKBackupMap);
			// ת�ƵĲ����и�λ�Ž���ת�ƺ��λ�ĸ��£����¸�λ���ϼ���λ��
			if (newDeptPk.size() > 0) {
			}

			// ���²��Ÿ����˵�����

			// 4.1����ԭҵ��Ԫ���š���λ
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
	 * �����ҵ��Ԫ������Ϣ
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
		// �����趨ҵ��Ԫ����
		deptdocVO.setPk_org(ruleVO.getNeworg());

		deptdocVO.setModifier(null);
		deptdocVO.setModifiedtime(null);
		deptdocVO.setCreator(ruleVO.getContext().getPk_loginUser());
		deptdocVO.setCreationtime(new UFDateTime());

		if (transDeptVOs == null) {
			String errMsg = ResHelper.getString("6001uif2", "06001uif20011"
			/* @res "û����Ҫ{0}�����ݣ�" */, ResHelper.getString("common", "UC001-0000001")
			/* @res "����" */);
			throw new BusinessException(errMsg);
		}

		// �����趨���롢���ơ���������
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
	 * �����ҵ��Ԫ������Ϣ
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

		// ����PostVO������
		for (PostVO postVO : postVOs) {
			for (DeptTransDeptVO transDeptVO : transDeptVOs) {
				DeptTransPostVO[] transPostVOs = transDeptVO.getTransPostVOs();
				// ԭ�������� ������ ��λ���ڲ��� ���� ������û�и�λʱ ����
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
	 * �����λ��Ϣ�����ݿ�<br>
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
		// ���ȶԸ�λ����������������ά���ڲ���
		List<AggPostVO> postVOList = new ArrayList<AggPostVO>();
		Collections.addAll(postVOList, sourcePostVOs);
		Collections.sort(postVOList, new Comparator<AggPostVO>() {

			@Override
			public int compare(AggPostVO aggVO1, AggPostVO aggVO2) {// �ø�λ�������¼��Ĺ�ϵ��������
																	// ע���е����¼�������ֱ�����¼����������еĲ㼶
				PostVO postVO1 = (PostVO) aggVO1.getParentVO();
				PostVO postVO2 = (PostVO) aggVO2.getParentVO();
				if (postVO1.getInnercode() == null) {// ��o1���ϼ���λΪ��ʱ��o2Ϊ����ô��ƽ����o2��Ϊ��Ϊ�¼�
					return postVO2.getInnercode() == null ? 0 : -1;
				}
				if (postVO2.getInnercode() == null) {// ��o1���ϼ���λ��Ϊ��ʱ��o2Ϊ�գ���ôo1Ϊo2���¼�
					return 1;
				}
				return postVO1.getInnercode().compareTo(postVO2.getInnercode());
			}

		});
		boolean isAutoGenerateBillCode = isAutoGenerateBillCode("post", pk_group, pk_org);

		// ��������ĸ�λ
		sourcePostVOs = postVOList.toArray(new AggPostVO[0]);
		String prefix = "ZD" + PubEnv.getServerDate().toStdString();
		// ���Զ����ɵ��ݺ� /Ĭ�Ϲ������� "��������+yyyy-mm-dd+_��ˮ��"
		String flowCode = getFlowCode(prefix);

		// �жϸ�λֱ���ϼ��ǲ���֮ǰ���ŵĸ�λ
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

			// �趨���ڵ��µ���֯��Ԫ
			toBeCopiedPostVO.setPk_org(pk_org);
			//
			toBeCopiedPostVO.setPk_hrorg(pk_org);
			//
			String pk_post = toBeCopiedPostVO.getPk_post();
			toBeCopiedPostPK[i] = pk_post;

			toBeCopiedPostVO.setPk_post(null);
			toBeCopiedPostVO.setInnercode(null);

			// ���¸�λ�Ĳ�������
			String pk_dept = toBeCopiedPostVO.getPk_dept();
			String pk_newdept = deptPKBackupMap.get(pk_dept);
			toBeCopiedPostVO.setPk_dept(pk_newdept);

			// �ڲ���ǰ��Ҫ�޸Ķ����һЩ��Ϣ
			// step 1. �жϱ����Ƿ��ظ�
			boolean duplicated = havePostCode(toBeCopiedPostVO);
			// step 2. ����ظ�����ô��Ҫ�Զ�����һ����ˮ�ű���������
			if (isAutoGenerateBillCode) {
				toBeCopiedPostVO.setPostcode(billcode[i]);
			} else {
				if (duplicated) {
					// ���Զ����ɵ��ݺ�
					toBeCopiedPostVO.setPostcode(prefix + "_" + getFlowCode(flowCode, i));
				}
			}

			// FIXME ��λ����ʱ��
			// toBeCopiedPostVO.setBuilddate();
			aggPostVOList.add(sourcePostVOs[i]);
		}

		// ִ�в���
		if (!aggPostVOList.isEmpty()) {
			AggPostVO[] newAggPostVOs = getPostManageService().insert(aggPostVOList.toArray(new AggPostVO[0]));

			for (int i = 0; i < newAggPostVOs.length; i++) {

				// toBeCopiedAggPostVOs�ǰ���suporior����õ�
				PostVO newPostVO = (PostVO) newAggPostVOs[i].getParentVO();
				insertPostVOs[i] = newPostVO;

				// �γ��ϸ�λPK���¸�λPK��ӳ��
				old_newPostpkmap.put(toBeCopiedPostPK[i], newPostVO.getPk_post());
				// �޸����е�ǰ�Ѿ�����ĸ�λ���Ӹ�λ��suporiorΪ���µ�
				for (int j = i + 1; j < sourcePostVOs.length; j++) {
					PostVO childPostVO = (PostVO) sourcePostVOs[j].getParentVO();
					String suporior = childPostVO.getSuporior();
					if (suporior != null && suporior.equals(toBeCopiedPostPK[i])) {// �޸������Ӹ�λ
						childPostVO.setSuporior(newPostVO.getPk_post());
					}
				}
			}
		}
	}

	/**
	 * ת����ְ��Ա
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
		// �Ƿ�ͬ����������
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

		// ��ѯ�����µ���Ա
		for (GeneralVO transPsnInfVO : transPsnInfVOs) {
			PsnJobVO psnjob = buildTransPsns(transPsnInfVO, ruleVO, insertPostVOs, deptPKBackupMap, old_newPostpkmap,
					transItemVOs, psnJobMap);
			oldPkPsnjob = (String) transPsnInfVO.getAttributeValue(PsnJobVO.PK_PSNJOB);
			// String pk_hrorg = psnjob.getPk_org();
			// // ����ҵ��Χ�����ж��Ƿ���Ҫ����ת�ƺ���֯����ʾί�� ������ ����ʽί�� ����
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
	 * ����ת����Ա"ģ��"VO<br>
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
		// �õ���һ����¼
		PsnJobVO psnjob = psnJobMap.get(transPsnInfVO.getAttributeValue(PsnJobVO.PK_PSNJOB));

		// FIXME ����ְ��ʼ����
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

		// update 2018-01-31 ���ſ���֯ת����Ա��ְ��¼��������Դ��֯ΪĿ��ҵ��Ԫ����������Դ��֯
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
		// ������Ϣ
		psnjob.setTrial_flag(UFBoolean.FALSE);
		psnjob.setTrial_type(null);
		psnjob.setRecordnum(0);
		psnjob.setTrnsevent((Integer) ((MDEnum) TrnseventEnum.TRANS).value());
		psnjob.setTrnstype(ruleVO.getTranstype());

		// ������Ϣ
		psnjob.setOribilltype(null);
		psnjob.setOribillpk(null);

		// Ա����Ҫʹ����һ����Ա����

		//
		if (!ArrayUtils.isEmpty(transItemVOs))
			for (DeptTransItemVO transItemVO : transItemVOs) {
				// ת�ƺ�
				if (transItemVO.getItemkey().startsWith("new") && transItemVO.getIsedit().booleanValue()) {
					psnjob.setAttributeValue(transItemVO.getItemkey().substring(3),
							transPsnInfVO.getAttributeValue(transItemVO.getItemkey()));
				}
			}
		// ������Ϣ
		psnjob.setPk_dept(deptPKBackupMap.get(psnjob.getPk_dept()));
		// ��λ��Ϣ
		psnjob.setPk_post(old_newPostpkmap.get(psnjob.getPk_post()));
		if (!ArrayUtils.isEmpty(insertPostVOs))
			for (PostVO insertPostVO : insertPostVOs) {
				// ��λ����
				if (insertPostVO.getPk_post().equals(psnjob.getPk_post())) {
					psnjob.setPk_postseries(insertPostVO.getPk_postseries());
					break;
				}
			}
		// ְ��ְ����ְ�ȵ���Ϣ���ֲ���
		return psnjob;
	}

	/**
	 * ת�Ƽ�ְ��Ա
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
		// ת�Ʋ���������ʱ��
		InSQLCreator isc = new InSQLCreator();
		String temptable = isc.getInSQL(ruleVO.getPk_depts());
		String cond = " ismainjob = 'N' and ( " + SQLHelper.getNullSql("endflag")
				+ " or endflag ='N' ) and assgid > 1 and " + PsnJobVO.PK_DEPT + " in (" + temptable + " )";
		// δ�����ļ�ְ��¼
		PartTimeVO[] partTimeVOs = getServiceTemplate().queryByCondition(PartTimeVO.class, cond);
		// �޼�ְ��Աֱ�ӷ���
		if (partTimeVOs == null || partTimeVOs.length < 1) {
			return;
		}
		// �Ƿ�ת�Ƽ�ְ��Ա
		UFBoolean isTransPart = ruleVO.isTransPart();
		// �Ƿ�ͬ����������
		UFBoolean isSysWork = ruleVO.isSysWork();
		if (isTransPart.booleanValue()) { // ��ְ��Ա���
			for (PartTimeVO partTimeVO : partTimeVOs) {
				if (ruleVO.getTransdata() != null) {
					partTimeVO.setBegindate(new UFLiteralDate(ruleVO.getTransdata().toStdString()));
				} else {
					partTimeVO.setBegindate(new UFLiteralDate(PubEnv.getServerDate().toStdString()));
				}

				partTimeVO.setEnddate(null);
				// ������Ϣ
				partTimeVO.setPk_org(ruleVO.getNeworg());
				partTimeVO.setPk_dept(deptPKBackupMap.get(partTimeVO.getPk_dept()));
				// ��λ��Ϣ
				partTimeVO.setPk_post(old_newPostpkmap.get(partTimeVO.getPk_post()));
				if (!ArrayUtils.isEmpty(insertPostVOs))
					for (PostVO insertPostVO : insertPostVOs) {
						// ��λ����
						if (insertPostVO.getPk_post().equals(partTimeVO.getPk_post())) {
							partTimeVO.setPk_postseries(insertPostVO.getPk_postseries());
							break;
						}
					}
				partTimeVO.setPk_psnjob(null);
				partTimeVO.setStatus(VOStatus.NEW);

			}
			// ��ְ��Ա���
			getRecordService().savePartchgInfo(partTimeVOs, isSysWork.booleanValue());
		} else {
			// ������ְ��Ϣ
			HiOrgBaseEventListener psnServer = new HiOrgBaseEventListener();
			psnServer.endPart(partTimeVOs, new UFLiteralDate(ruleVO.getTransdata().toStdString()));
		}
	}

	/**
	 * ��ְת��
	 * 
	 * @param ruleVO
	 * @param deptPKBackupMap
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unused")
	private void doTransDimissionPsnInf(DeptTransRuleVO ruleVO, Map<String, String> deptPKBackupMap)
			throws BusinessException {
		// ת�Ʋ���������ʱ��
		InSQLCreator isc = new InSQLCreator();
		String temptable = isc.getInSQL(ruleVO.getPk_depts());
		String cond = " ismainjob = 'Y' and  lastflag = 'Y' and endflag = 'Y'  and " + PsnJobVO.PK_DEPT + " in ("
				+ temptable + " )";
		// δ�����ļ�ְ��¼
		PsnJobVO[] psnJobVOs = getServiceTemplate().queryByCondition(PsnJobVO.class, cond);
		// ����ְ��Աֱ�ӷ���
		if (ArrayUtils.isEmpty(psnJobVOs)) {
			return;
		}

		for (PsnJobVO psnJobVO : psnJobVOs) {
			psnJobVO.setPk_org(ruleVO.getNeworg());
			// ������Ϣ
			psnJobVO.setPk_dept(deptPKBackupMap.get(psnJobVO.getPk_dept()));
			psnJobVO.setPk_psnjob(null);
			psnJobVO.setBegindate(ruleVO.getTransdata());
			psnJobVO.setEnddate(null);
			// ��ְ��Աת��
			getRecordManageService().doTransDimission(psnJobVO);
		}

	}

	/**
	 * �����Աת��
	 * 
	 * @param ruleVO
	 * @param deptPKBackupMap
	 * @return
	 * @throws BusinessException
	 */
	private void doTransPoiPsnInf(DeptTransRuleVO ruleVO, Map<String, String> deptPKBackupMap) throws BusinessException {
		// ת�Ʋ���������ʱ��
		InSQLCreator isc = new InSQLCreator();
		String temptable = isc.getInSQL(ruleVO.getPk_depts());
		String cond = " ismainjob = 'Y' and  lastflag = 'Y'  and ( " + SQLHelper.getNullSql("endflag")
				+ " or endflag ='N' ) and psntype = 1  and " + PsnJobVO.PK_DEPT + " in (" + temptable + " )";

		// δ�����ļ�ְ��¼
		PsnJobVO[] psnJobVOs = getServiceTemplate().queryByCondition(PsnJobVO.class, cond);
		// �������Աֱ�ӷ���
		if (ArrayUtils.isEmpty(psnJobVOs))
			return;

		for (PsnJobVO psnJobVO : psnJobVOs) {
			// ������Ϣ
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
		// �����Ա����һ��������¼
		getRecordService().addPoiPsnjobs(psnJobVOs);

	}

	/**
	 * ����ת��ǰ��˾�Ĳ��ű����ʷVO<br>
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
		// ���ϲ����źͽ��ܲ��ŵı����ʷ����ͬһ�����������
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

			// ����
			deptHistoryVO.setCode(sourceDeptVO.getCode());
			// ������
			SuperVOHelper.copyMultiLangAttribute(sourceDeptVO, deptHistoryVO);
			// ���ż���
			deptHistoryVO.setDeptlevel(sourceDeptVO.getDeptlevel());
			// ������
			deptHistoryVO.setPrincipal(sourceDeptVO.getPrincipal());
			// ��֯
			deptHistoryVO.setPk_org(sourceDeptVO.getPk_org());

			// ��¼Դ����������֯�İ汾����
			// HROrgVO hrorgVO =
			// getOMCommonQueryService().queryByPK(HROrgVO.class,
			// deptHistoryVO.getPk_org());
			if (hrorgMap.get(deptHistoryVO.getPk_org()) != null) {
				HROrgVO hrorgVO = hrorgMap.get(deptHistoryVO.getPk_org());
				deptHistoryVO.setPk_org_v(hrorgVO.getPk_vid());
			}

			// ���Ĳ�ѯ��־
			sourceDeptVO.setHrcanceled(UFBoolean.TRUE);
			// ��������
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
			// ��map��ȡֵȻ���޸ģ�����û�еģ����û����ô�����쳣��
			@SuppressWarnings("unused")
			AggHRDeptVO updateDeptVO = update(deptPkAggHRDeptVOMap.get(sourceDeptVO.getPk_dept()), false);
			/*
			 * ��ҵ��Ԫת��ʱ����ִ��ͣ�� // ִ��ͣ�� if (((HRDeptVO)
			 * updateDeptVO.getParentVO()).getEnablestate() ==
			 * IPubEnumConst.ENABLESTATE_ENABLE) { updateDeptVO =
			 * disable(updateDeptVO); }
			 */

		}

		// ��¼�����ʷ
		getDeptDao().getBaseDAO().insertVOArray(historyVOCache.toArray(new DeptHistoryVO[0]));

		// �²�����ʷ��¼���Ƿ���� = 'Y'
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
	 * ���������ݲ���Ȩ�޵Ĳ���
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
	 * ���ݲ���pk��ѯ�������������HR��֯
	 * 
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public String queryHrPkOrgByPkDept(String pk_dept) throws BusinessException {
		HRDeptVO hrDeptVO = getOMCommonQueryService().queryByPK(HRDeptVO.class, pk_dept);
		if (hrDeptVO != null) {
			// ��ѯ��������ҵ��Ԫ
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
		// ��ѯ��������������֯
		AdminOrgVO adminOrgVO = getOMCommonQueryService().queryByPK(AdminOrgVO.class, pk_org);

		// ��ѯ�ϼ�������֯����ҵ��Ԫ
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
		// ת��Ϊ��׼ʱ����ʱ��
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
	 * ���빤�߲��룬��������Ϣ<br>
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public AggHRDeptVO insertImportVO(AggHRDeptVO vo) throws BusinessException {
		TimerLogger timerLogger = new TimerLogger("���빤��Ч�ʲ��ԡ�������", null);
		if (vo == null) {
			return null;
		}
		AggHRDeptVO oldVO = AggVOHelper.clone(vo, getEntityClass());
		beforeInsert(vo);

		timerLogger.addLog("����Ȩ��У�鿪ʼ");
		// ����Ȩ��У��
		insertPowerValidate(vo);
		timerLogger.addLog("����Ȩ��У�����");

		// ����������Ϊ���ڷ�֧�ĸ�������������� Ϊ�Լ���ҵ������
		// Ϊ�˵��빤��Ч�ʣ�����ȥ��
		// insertLockOperate(vo);

		timerLogger.addLog("�߼�У�鿪ʼ");
		// �߼�У��
		// Ϊ�˵��빤��Ч�ʣ�ȡ�����������У�飬ǰ̨�Ѿ�����Ψһ��У��
		insertValidateVO(vo);
		timerLogger.addLog("�߼�У�����");
		timerLogger.addLog("��������Ϣ��ʼ");
		// ��������Ϣ
		setInsertAuditInfo(vo);
		timerLogger.addLog("��������Ϣ����");

		// �¼�ǰ֪ͨ
		// fireBeforeInsertEvent(vo);

		timerLogger.addLog("�����ڲ����뿪ʼ");
		// DB��������������VO; �����ڲ�����; ���¼�����VO;
		AggHRDeptVO resultVO = dbImportInsertVO(vo);
		timerLogger.addLog("�����ڲ��������");

		// ����֪ͨ
		// Ϊ�˵��빤��Ч��,ע�͵�
		// notifyVersionChangeWhenDataInserted(vo);

		// �¼���֪ͨ
		// fireAfterInsertEvent(vo);

		// ҵ����־
		// Ϊ�˵��빤��Ч��,ע�͵�
		// writeInsertBusiLog(vo);
		timerLogger.addLog("���沿�ſ�ʼ");
		afterInsertImportVO(oldVO, resultVO);
		timerLogger.addLog("���沿�Ž���");

		timerLogger.log();
		// Ϊ�˵��빤��Ч�ʣ������������ֻ�в�ѯû�и��£�����ע��
		// resultVO = getServiceTemplate().updateTsAndKeys(resultVO);
		return resultVO;
	}

	/**
	 * ���빤������ʱ���¼���֪ͨ
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	@Override
	public void fireAfterImportInsertEvent(AggHRDeptVO[] vos) throws BusinessException {
		getSystemTime("���¼���ʼ");
		BDCommonEventUtil eventUtil = new BDCommonEventUtil(IMetaDataIDConst.DEPT);
		List<AggHRDeptVO> list = Arrays.asList(vos);
		List<AggHRDeptVO> listFire = new ArrayList<AggHRDeptVO>();

		for (AggHRDeptVO vo : list) {
			if (vo.getParentVO().getPrimaryKey() != null) {
				listFire.add(vo);
			}

		}
		eventUtil.dispatchInsertAfterEvent(listFire.toArray(new AggHRDeptVO[0]));
		getSystemTime("���¼�����");
	}

	/**
	 * ���� miaoxiong add
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
				UFDate vstartdate = new UFDate(new Date());// �°汾��Чʱ��
				if (oldversionStartDate.isSameDate(vstartdate, Calendars.getGMTDefault())) {
					warningMsg = "����" + ResHelper.getString("org", "CreateVersionDialog-000002")/*
																								 * һ�����ֻ������һ���汾
																								 * .
																								 */;
					warningMsg += ResHelper.getString(
							"6005dept",
							"06005dept0366",
							new String[] { MultiLangHelper.getName(deptVO),
									VOUtils.getDocName(OrgVO.class, deptVO.getPk_org()) })
					/* @res "���ţ�[{0}]  ������֯��[{1}] �����°汾ʧ�ܣ�" */;
				} else if (!oldversionStartDate.isSameDate(vstartdate, Calendars.getGMTDefault())
						&& oldversionStartDate.compareTo(vstartdate) > 0) {
					warningMsg = "����" + ResHelper.getString("org", "CreateVersionDialog-000003")/*
																								 * �°汾����Ч���ڲ���������һ�汾����Ч����
																								 * .
																								 */;
					warningMsg += ResHelper.getString(
							"6005dept",
							"06005dept0366",
							new String[] { MultiLangHelper.getName(deptVO),
									VOUtils.getDocName(OrgVO.class, deptVO.getPk_org()) })
					/* @res "���ţ�[{0}]  ������֯��[{1}] �����°汾ʧ�ܣ�" */;
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
					UFDate vstartdate = new UFDate(new Date());// �°汾��Чʱ��
					if (oldversionStartDate.isSameDate(vstartdate, Calendars.getGMTDefault())) {
						warningMsg = "����" + ResHelper.getString("org", "CreateVersionDialog-000002")/*
																									 * һ�����ֻ������һ���汾
																									 * .
																									 */;
						warningMsg += ResHelper.getString(
								"6005dept",
								"06005dept0366",
								new String[] { MultiLangHelper.getName(mainVO),
										VOUtils.getDocName(OrgVO.class, mainVO.getPk_org()) })
						/* @res "���ţ�[{0}]  ������֯��[{1}] �����°汾ʧ�ܣ�" */;
					} else if (!oldversionStartDate.isSameDate(vstartdate, Calendars.getGMTDefault())
							&& oldversionStartDate.compareTo(vstartdate) > 0) {
						warningMsg = "����" + ResHelper.getString("org", "CreateVersionDialog-000003")/*
																									 * �°汾����Ч���ڲ���������һ�汾����Ч����
																									 * .
																									 */;
						warningMsg += ResHelper.getString(
								"6005dept",
								"06005dept0366",
								new String[] { MultiLangHelper.getName(mainVO),
										VOUtils.getDocName(OrgVO.class, mainVO.getPk_org()) })
						/* @res "���ţ�[{0}]  ������֯��[{1}] �����°汾ʧ�ܣ�" */;
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
					UFDate vstartdate = new UFDate(new Date());// �°汾��Чʱ��
					if (oldversionStartDate.isSameDate(vstartdate, Calendars.getGMTDefault())) {
						warningMsg = "����" + ResHelper.getString("org", "CreateVersionDialog-000002")/*
																									 * һ�����ֻ������һ���汾
																									 * .
																									 */;
						warningMsg += ResHelper.getString(
								"6005dept",
								"06005dept0366",
								new String[] { MultiLangHelper.getName(mainVO),
										VOUtils.getDocName(OrgVO.class, mainVO.getPk_org()) })
						/* @res "���ţ�[{0}]  ������֯��[{1}] �����°汾ʧ�ܣ�" */;
					} else if (!oldversionStartDate.isSameDate(vstartdate, Calendars.getGMTDefault())
							&& oldversionStartDate.compareTo(vstartdate) > 0) {
						warningMsg = "����" + ResHelper.getString("org", "CreateVersionDialog-000003")/*
																									 * �°汾����Ч���ڲ���������һ�汾����Ч����
																									 * .
																									 */;
						warningMsg += ResHelper.getString(
								"6005dept",
								"06005dept0366",
								new String[] { MultiLangHelper.getName(mainVO),
										VOUtils.getDocName(OrgVO.class, mainVO.getPk_org()) })
						/* @res "���ţ�[{0}]  ������֯��[{1}] �����°汾ʧ�ܣ�" */;
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
		// ����Ȩ��У��
		updatePowerValidate(vo);

		// ����������Ϊ���ڷ�֧�ĸ������������������޸ĵ���һ��֧����ҪΪ�·�֧�ĸ����������� Ϊ�Լ���ҵ������
		// Ϊ�˵��빤�߰Ѽ���ȥ��
		// updateLockOperate(vo);

		// ����OldVO: oldVOֻ��������
		AggHRDeptVO oldVO = getMDQueryService().queryBillOfVOByPK(getEntityClass(), vo.getParentVO().getPrimaryKey(),
				false);

		// �汾У��
		// ǰ̨��װ��ʱ������ݿ���ȡ��ÿ��VO��ts����������ط�����У����
		// updateVersionValidate(oldVO, vo);

		// �߼�У��
		updateValidateVO(oldVO, vo);

		// ���������Ϣ
		setUpdateAuditInfo(vo, blChangeAudltInfo);

		// �¼�ǰ֪ͨ
		fireBeforeUpdateEvent(oldVO, vo);

		// DB�����������޸ĵ�VO; �����ڲ�����; ���¼�����VO;
		AggHRDeptVO resultVO = dbUpdateImportVO(oldVO, vo);

		// ����֪ͨ
		// Ϊ�˵��빤��Ч�ʣ�ȥ������֪ͨ
		// notifyVersionChangeWhenDataUpdated(vo);

		// �¼���֪ͨ
		fireAfterUpdateEvent(oldVO, vo);

		// ҵ����־
		// Ϊ�˵��빤��Ч�ʣ�ȥ��д��־
		// writeUpdatedBusiLog(vo);

		afterUpdate(beforeUpdateVO, resultVO);

		// Ϊ�˵��빤�ߣ�������ֻ�в�ѯû�����ݴ�������ط���ѯ����ʡ��
		// resultVO = getServiceTemplate().updateTsAndKeys(resultVO);

		// ��������ʷ��Ϣ
		reformDeptHistoryInfo(resultVO);

		return resultVO;

	}

	/**
	 * ���µ��빤��VO
	 * 
	 * @param oldVO
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	protected AggHRDeptVO dbUpdateImportVO(AggHRDeptVO oldVO, AggHRDeptVO vo) throws BusinessException {
		// ����VO״̬
		vo.getParentVO().setStatus(VOStatus.UPDATED);
		// DB����
		getMDPersistenceService().saveBillWithRealDelete(vo);
		// ������¼���ϵ�����仯��������ڲ�����
		String idName = getBizInfoUtil().getFieldName((SuperVO) vo.getParentVO(), IBaseServiceConst.ID);
		String pidName = getBizInfoUtil().getFieldName((SuperVO) vo.getParentVO(), IBaseServiceConst.PID);
		String oldParentPK = (String) oldVO.getParentVO().getAttributeValue(pidName);
		String newParentPK = (String) vo.getParentVO().getAttributeValue(pidName);
		if (!StringUtils.equals(oldParentPK, newParentPK)) {
			InnerCodeUtil.generateInnerCodeAfterChangePosition(getBizInfoUtil()
					.getTableName((SuperVO) vo.getParentVO()), idName, pidName, vo.getParentVO().getPrimaryKey(),
					newParentPK);
		}

		// ���¼�����VO
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
	 * ��Ժ���룬���ɲ��ű���
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
