package nc.ui.ta.psndoc.view;

import java.lang.reflect.Field;
import java.util.Vector;

import javax.swing.SwingUtilities;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.org.IPrimaryOrgQry;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.ITBMPsndocQueryMaintain;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hi.ref.PsnjobAOSRefTreeModel2;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.hr.uif2.view.PrimaryOrgPanel;
import nc.ui.org.ref.OrgVOsDefaultRefModel;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.ta.psndoc.model.TbmPsndocAppModel;
import nc.ui.ta.region.ref.TBMRegionRefModel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.cache.CacheManager;
import nc.vo.cache.ICache;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.om.aos.AOSSQLHelper;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psndoc.TBMPsndocCommonValue;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.psndoc.TbmPropEnum;
import nc.vo.ta.pub.PubPermissionUtils;
import nc.vo.ta.timeregion.RegionOrgVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class TbmPsndocFormEditor extends HrBillFormEditor implements BillCardBeforeEditListener {
	private static final long serialVersionUID = 1L;
	private OrgVOsDefaultRefModel refModel;

	@Override
	public void afterEdit(BillEditEvent e) {
		//
		if (TBMPsndocCommonValue.ITEMCODE_ISINORGDATA.equals(e.getKey()) || TBMPsndocVO.PK_PSNJOB.equals(e.getKey())) {
			if (TBMPsndocVO.PK_PSNJOB.equals(e.getKey())) {
				// ssx added on 2020-04-07
				// 新增考勤档案时将上一笔考勤的班组带入
				IUAPQueryBS service = NCLocator.getInstance().lookup(IUAPQueryBS.class);
				String pk_team;
				try {
					pk_team = (String) service
							.executeQuery(
									"select pk_team from tbm_psndoc doc where begindate = (select max(begindate) from tbm_psndoc where pk_psndoc = doc.pk_psndoc) and pk_psndoc = '"
											+ ((UIRefPane) e.getSource()).getRefValue("bd_psndoc.pk_psndoc") + "'",
									new ColumnProcessor());
					BillItem item = getBillCardPanel().getHeadItem(TBMPsndocVO.PK_TEAM);
					item.setValue(pk_team);
				} catch (BusinessException e1) {
					e1.printStackTrace();
				}
				// ssx end

				setRegionDefaultValue(e);
				getBillCardPanel().getHeadItem(TBMPsndocVO.PK_PSNDOC).setValue(
						((UIRefPane) e.getSource()).getRefValue("bd_psndoc.pk_psndoc"));
				getBillCardPanel().getHeadItem(TBMPsndocVO.PK_PSNORG).setValue(null);
				getBillCardPanel().getHeadItem(TBMPsndocVO.PK_PSNORG).setValue(
						((UIRefPane) e.getSource()).getRefValue("hi_psnjob.pk_psnorg"));
				getBillCardPanel().getBillModel().loadLoadRelationItemValue();
			}
			UICheckBox checkBox = (UICheckBox) getBillCardPanel()
					.getHeadItem(TBMPsndocCommonValue.ITEMCODE_ISINORGDATA).getComponent();
			// 取当前日期
			UFDate busDate = WorkbenchEnvironment.getInstance().getBusiDate();
			UFLiteralDate busLitDate = UFLiteralDate.getDate(busDate.toString().substring(0, 10));
			// 按到职日期指定开始日期
			String pk_psnjob = (String) getBillCardPanel().getHeadItem(TBMPsndocVO.PK_PSNJOB).getValueObject();
			// 选择到职日期，开始日期不能编辑
			getBillCardPanel().getHeadItem(TBMPsndocVO.BEGINDATE).setEnabled(!checkBox.isSelected());
			getBillCardPanel().getHeadItem(TBMPsndocVO.BEGINDATE).setValue(
					checkBox.isSelected() ? getIndutyDate(pk_psnjob, busLitDate) : busLitDate);
		}
		super.afterEdit(e);
	}

	/**
	 * 取得到职日期
	 * 
	 * @param pk_psnjob
	 * @param busLitDate
	 *            ：当前业务时间
	 * @return
	 */
	private UFLiteralDate getIndutyDate(String pk_psnjob, UFLiteralDate busLitDate) {
		// 如果没有选择人员，则返回当前业务日期
		if (StringUtils.isBlank(pk_psnjob))
			return busLitDate;
		UFLiteralDate ufdate = null;
		try {
			// 查询人员到职日期
			// 以前的接口是查询工作记录的pk_psnorg是否在流动关系表里出现过,这种不符合时间管理的要求
			// 因为人员调配时候 在调配记录上新增一条数据在流动关系表里是不存记录的
			// ufdate =
			// NCLocator.getInstance().lookup(IPsndocQryService.class).queryIndutyDate(pk_psnjob);
			ufdate = NCLocator.getInstance().lookup(ITBMPsndocQueryMaintain.class).queryBeginDateFromPsnjob(pk_psnjob);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		// 如果没有到职日期，则用考勤期间的开始日期，如果没有考勤期间或者考勤期间对应的开始日期，则取当前业务时间
		if (ufdate == null) {
			PeriodVO vo = null;
			try {
				vo = NCLocator.getInstance().lookup(IPeriodQueryService.class)
						.queryByDate(getModel().getContext().getPk_org(), busLitDate);
			} catch (Exception ex) {
				Logger.error(ex.getMessage(), ex);
			}
			return (vo == null || vo.getBegindate() == null) ? busLitDate : vo.getBegindate();
		}
		// 如果有到职日期，则取到职日期
		return UFLiteralDate.getDate(ufdate.toDate());
	}

	@Override
	public void handleEvent(AppEvent event) {
		if (AppEventConst.MODEL_INITIALIZED.equalsIgnoreCase(event.getType())) {
			resetBillData();
		}
		if (TBMPsndocCommonValue.BATCH_ADD.equalsIgnoreCase(event.getType())) {
			onBatchAdd();
			showMeUp();
		}
		super.handleEvent(event);
	}

	private void onBatchAdd() {
		setEditable(true);
		billCardPanel.addNew();
		setDefaultValue();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				billCardPanel.requestFocusInWindow();
				billCardPanel.transferFocusToFirstEditItem();
			}
		});
	}

	/**
	 * 卡片中，如果结束日期时9999-12-9，则不显示结束日期
	 */
	@Override
	public void setValue(Object object) {
		if (object == null)
			super.setValue(object);
		TBMPsndocVO vo = (TBMPsndocVO) object;
		if (vo == null)
			return;
		if (UFLiteralDate.getDate(TBMPsndocCommonValue.END_DATA).equals(vo.getEnddate())) {
			vo.setEnddate(null);
		}
		super.setValue(object);
	}

	@Override
	public TbmPsndocAppModel getModel() {
		return (TbmPsndocAppModel) super.getModel();
	}

	public Object getValue() {
		Object value = super.getValue();
		if (value == null)
			return null;
		// 结束日期为空时，设置默认值
		TBMPsndocVO vo = (TBMPsndocVO) value;
		if (StringUtils.isBlank(vo.getPk_psnorg())) {// psnorg丢失
			vo.setPk_psnorg((String) (((UIRefPane) getBillCardPanel().getHeadItem(TBMPsndocVO.PK_PSNJOB).getComponent())
					.getRefValue("hi_psnjob.pk_psnorg")));
		}
		// 目前不会出现结束日期不为空的情况下，因为结束日期不可编辑,因此任何情况下vo.getEnddate() == null
		if (vo.getEnddate() == null) {
			vo.setEnddate(UFLiteralDate.getDate(TBMPsndocCommonValue.END_DATA));
		}
		return vo;
	}

	@Override
	public void initUI() {
		super.initUI();
		resetBillData();
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
		resetRefInf();
		resetRegionInf();
	}

	/**
	 * 重新设置参照信息 通常来说这么设置完之后参照就会变成左树右表型，但由于平台的单据模板设置这块有点问题，
	 * 在参照选择定制对话框中的参照类型它只提供默认类型和下拉类型， 而实际上我们的RefModel采用的是GRIDTREE型，
	 * 加载单据模板后它会将TBMPsndocRefModel的参照类型设置为默认型，从而使得参照变成列表型而不是左树右表型，
	 * 目前我们采用的临时解决方案是在相应的CardForm类里面重置一下该参照的参照类型，
	 * psnRef.setRefType(IRefConst.GRIDTREE)
	 */
	protected void resetRefInf() {
		UIRefPane psnRef = (UIRefPane) getBillCardPanel().getHeadItem(TBMPsndocVO.PK_PSNJOB).getComponent();
		// psnRef.getRefModel().addWherePart(PubPermissionUtils.getPsnjobPermission());
		psnRef.setRefType(IRefConst.GRIDTREE);
		UIRefPane adminorg = (UIRefPane) getBillCardPanel().getHeadItem(TBMPsndocVO.PK_ADMINORG).getComponent();
		adminorg.setRefModel(getRefModel());
	}

	public TbmPsndocFormEditor() {
		super();
	}

	/*
	 * 此节点的特殊情况：修改需要在列表界面上进行，不能在卡片界面上进行。 v63--- ue要求卡片可修改
	 */
	@Override
	protected void onEdit() {
		super.onEdit();
		// 和列表保存一致，人员和开始日期都不可修改
		getBillCardPanel().getHeadItem(TBMPsndocVO.PK_PSNJOB).setEnabled(false);
		getBillCardPanel().getHeadItem(TBMPsndocVO.BEGINDATE).setEnabled(false);
		getBillCardPanel().getHeadItem(TBMPsndocCommonValue.ITEMCODE_ISINORGDATA).setEnabled(false);
		resetRegionInf();
	}

	/**
	 * 重画单据界面
	 * 
	 * @param billdata
	 */
	private void resetBillData() {
		BillData billdata = getBillCardPanel().getBillData();
		// 如果考勤规则为非门禁，则隐藏考勤地点列
		billdata.getHeadItem(TBMPsndocVO.PK_PLACE).setShow(getModel().isShowWorkPlace());
		getBillCardPanel().setBillData(billdata);
		getBillCardPanel().updateUI();
	}

	@Override
	protected void setDefaultValue() {
		TBMPsndocVO vo = new TBMPsndocVO();
		UFDate busDate = WorkbenchEnvironment.getInstance().getBusiDate();
		vo.setBegindate(UFLiteralDate.getDate(busDate.toString().substring(0, 10)));
		vo.setTbm_prop(TbmPropEnum.MACHINE_CHECK.toIntValue());
		vo.setPk_group(getModel().getContext().getPk_group());
		vo.setPk_org(getModel().getContext().getPk_org());
		vo.setPk_adminorg(getModel().getContext().getPk_org());
		getBillCardPanel().getBillData().setHeaderValueVO(vo);
	}

	/**
	 * 
	 */
	public void setModel(TbmPsndocAppModel model) {
		super.setModel(model);
	}

	@Override
	protected void onAdd() {
		// TODO Auto-generated method stub
		super.onAdd();
		// 结束日期在新增时不能编辑
		getBillCardPanel().getHeadItem(TBMPsndocVO.ENDDATE).setEnabled(false);
		resetRegionInf();
	}

	@Override
	public boolean beforeEdit(BillItemEvent e) {
		// 员工号的参照范围
		if (TBMPsndocVO.PK_PSNJOB.equals(e.getItem().getKey())) {
			UIRefPane psnRef = (UIRefPane) getBillCardPanel().getHeadItem(TBMPsndocVO.PK_PSNJOB).getComponent();
			if (psnRef != null && psnRef.getRefModel() != null) {
				// 设置不包含相关人员以及离职人员
				psnRef.getRefModel().addWherePart(
						" and hi_psnorg.psntype = 0 and hi_psnjob.endflag<>'Y' and "
								// 某些公司希望补录离职人员的信息，因此放开该限制
								// + " hi_psnjob.endflag<>'Y' and "
								+ " hi_psnjob.pk_org in ("
								+ AOSSQLHelper.getChildrenBUInSQLByHROrgPK(getModel().getContext().getPk_org())
								+ " ) and "// 过滤掉委托的
								+ PsndocVO.getDefaultTableName() + "." + PsndocVO.PK_PSNDOC
								+ " not in (select tbm_psndoc.pk_psndoc from tbm_psndoc "
								+ " where tbm_psndoc.enddate='" + TBMPsndocCommonValue.END_DATA
								+ "' and  tbm_psndoc.pk_org='" + getModel().getContext().getPk_org() + "') "
								+ PubPermissionUtils.getPsnjobPermission());

				int hasGlbdef8 = -1;
				IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
				try {
					hasGlbdef8 = (int) query.executeQuery(
							"select count(glbdef1) from HI_PSNDOC_GLBDEF8 where pk_psndoc = (select pk_psndoc from sm_user where cuserid = '"
									+ getModel().getContext().getPk_loginUser() + "')", new ColumnProcessor());
				} catch (BusinessException ex) {
					ex.printStackTrace();
				}

				if (hasGlbdef8 > 0) {
					String deptWherePart = "#DEPT_PK# in (select glbdef1 from HI_PSNDOC_GLBDEF8 where pk_psndoc = (select pk_psndoc from sm_user where cuserid = '"
							+ getModel().getContext().getPk_loginUser()
							+ "') and '"
							+ new UFLiteralDate().toString()
							+ "' between BEGINDATE and nvl(ENDDATE, '9999-12-31')) and (select count(pk_dept) from org_dept where pk_dept=#DEPT_PK# and isnull(HRCANCELED, 'N')='N') > 0";
					PsnjobAOSRefTreeModel2 refModel = (PsnjobAOSRefTreeModel2) psnRef.getRefModel();
					if (refModel != null) {
						refModel.setClassWherePart(deptWherePart.replace("#DEPT_PK#", "orgdept.pk_orgdept"));
					}
				}
			}
		} else if (TBMPsndocVO.PK_REGION.equals(e.getItem().getKey())) {
			// 只显示当前人力资源组织下已启用的考勤区域 heqiaoa
			UIRefPane psnRef = (UIRefPane) getBillCardPanel().getHeadItem(TBMPsndocVO.PK_REGION).getComponent();
			TBMRegionRefModel model = (TBMRegionRefModel) psnRef.getRefModel();
			model.setPk_hrorg(getModel().getContext().getPk_org());
			model.setEnabled(true);
			model.reloadData(); // 重新載入數據，避免刷新
		}
		// 结束日期在新增时不能编辑 onAdd是控制
		// if(TBMPsndocVO.ENDDATE.equals(e.getItem().getKey()))
		// return false;
		return true;
	}

	/***************************************************************************
	 * 得到有权限的组织<br>
	 * Created on 2011-5-11 16:52:15<br>
	 * 
	 * @return OrgVO[]
	 * @author Rocex Wang
	 ***************************************************************************/
	protected OrgVO[] getPermOrgVOs() {
		String strCacheKey = PubEnv.getPk_group() + "." + PubEnv.getPk_user() + "."
				+ getModel().getContext().getNodeCode();

		ICache cache = CacheManager.getInstance().getCache(PrimaryOrgPanel.class.getName());

		OrgVO[] orgVOs = (OrgVO[]) cache.get(strCacheKey);

		if (orgVOs == null || orgVOs.length == 0) {
			try {
				orgVOs = NCLocator
						.getInstance()
						.lookup(IPrimaryOrgQry.class)
						.queryPrimaryOrgVOs(IPrimaryOrgQry.CONTROLTYPE_HRADMINORG,
								getModel().getContext().getFuncInfo().getFuncPermissionPkorgs());

				cache.put(strCacheKey, orgVOs);
			} catch (BusinessException e) {
				Logger.error(e);
			}
		}

		return orgVOs;
	}

	/**
	 * 管理组织参照，为了和主组织参照保持一致
	 * 
	 * @return
	 */
	public OrgVOsDefaultRefModel getRefModel() {
		if (refModel != null) {
			return refModel;
		}

		OrgVO funcPermOrgVOs[] = getPermOrgVOs();

		refModel = new OrgVOsDefaultRefModel(funcPermOrgVOs) {
			@SuppressWarnings("rawtypes")
			@Override
			public Vector reloadData() {
				ICache cache = CacheManager.getInstance().getCache(PrimaryOrgPanel.class.getName());

				cache.flush();

				try {
					Field field = OrgVOsDefaultRefModel.class.getDeclaredField("vos");
					field.setAccessible(true);

					field.set(this, getPermOrgVOs());
				} catch (Exception ex) {
					Logger.error(ex.getMessage(), ex);
				}

				return super.reloadData();
			}
		};

		refModel.setUseDataPower(true);
		refModel.setCacheEnabled(false);
		refModel.setRefNodeName(ResHelper.getString("6001uif2", "06001uif20053")
		/* @res "人力资源组织" */);

		return refModel;
	}

	/**
	 * 设置考勤档案中考勤区域字段是否可编辑
	 * 
	 * @author heqiaoa
	 */
	private void resetRegionInf() {
		// 设置考勤区域字段是否可用
		BillItem regionItem = getBillCardPanel().getHeadItem(TBMPsndocVO.PK_REGION);
		if (null != regionItem) {
			// 编辑，新增时考勤区域才能编辑
			boolean enabled = UIState.ADD == getModel().getUiState() || UIState.EDIT == getModel().getUiState();
			regionItem.setEnabled(enabled && ((TbmPsndocAppModel) getModel()).getUseMobile().booleanValue());
		}
	}

	/**
	 * 员工加入档案时默认取员工所属组织对应的考勤区域
	 * 
	 * @throws BusinessException
	 */
	private void setRegionDefaultValue(BillEditEvent e) {
		// 获取所选中人员的所属业务单元
		String pk_org = (String) ((UIRefPane) e.getSource()).getRefValue("hi_psnjob.pk_org");
		if (StringUtils.isBlank(pk_org)) {
			return;
		}
		String cond = " pk_org = '" + pk_org + "' ";
		RegionOrgVO[] regionOrgVOs = null;
		try {
			regionOrgVOs = (RegionOrgVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
					.retrieveByClause(null, RegionOrgVO.class, cond);
		} catch (BusinessException e1) {
			Logger.error(e1.toString(), e1);
		}
		if (!ArrayUtils.isEmpty(regionOrgVOs)) {
			// 正常情况下，一个业务单元只能对应一个考勤区域，所以查出来应该只有一个
			String pk_region = regionOrgVOs[0].getPk_region();
			BillItem regionItem = getBillCardPanel().getHeadItem(TBMPsndocVO.PK_REGION);
			regionItem.setValue(pk_region);
		}
	}
}
