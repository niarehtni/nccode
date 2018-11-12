package nc.bs.hrsms.hi.employ.ctrl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import nc.bs.hrss.pub.tool.ViewUtil;
import nc.pub.tools.VOUtils;
import nc.pubitf.para.SysInitQuery;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.vo.bd.psnid.PsnIdtypeVO;
import nc.vo.bd.pub.EnableStateEnum;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.IPsndocService;
import nc.uap.lfw.core.comp.WebElement;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.vo.hi.psndoc.PsndocAggVO;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.uap.lfw.core.page.LfwView;
import nc.bs.hrss.ta.common.ctrl.BaseController;
import nc.uap.lfw.core.data.Row;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.uap.lfw.core.data.Dataset;
import nc.bs.bd.psn.validator.PsnIdtypeQuery;
import nc.bs.framework.common.NCLocator;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.pub.BusinessException;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.enumeration.PsnType;
import nc.vo.hr.validator.ValidateWithLevelException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.logging.Logger;
import nc.bs.uif2.validation.ValidationFailure;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;

public class OnlyinfoViewController extends BaseController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static final long ID = 5L;

	private static final String DS_PSNDOC_DSID = "ds_psndoc";
	private static final String DS_PSNJOB_DSID = "ds_psnjob";
	private static final String ONLY_INFO_CARD_ID = "onlyinfo";
	private static final String PSN_EMPLOY_CARD_ID = "psn_employ";

	public void doOK(MouseEvent<WebElement> mouseEvent) throws Exception {
		LfwView viewMain = ViewUtil.getCurrentView();
		Dataset ds = ViewUtil.getDataset(viewMain, DS_PSNDOC_DSID);
		Dataset bd_psnjob = ViewUtil.getDataset(viewMain, DS_PSNJOB_DSID);
		Row row = ds.getSelectedRow();
		PsndocVO psndocVO = new PsndocVO();
		BeanUtils.copyProperties(new Dataset2SuperVOSerializer<PsndocVO>().serialize(ds, row)[0], psndocVO);
		PsnJobVO jobvo = new PsnJobVO();
		BeanUtils.copyProperties(
				new Dataset2SuperVOSerializer<PsnJobVO>().serialize(bd_psnjob, bd_psnjob.getSelectedRow())[0], jobvo);
		checkNull(psndocVO, jobvo.getPk_psncl());// 检查输入为空

		try {
			// 首先收集数据
			String pk_org = SessionUtil.getPk_org();
			String pk_group = SessionUtil.getPk_group();
			psndocVO.setPk_org(pk_org);
			psndocVO.setPk_group(pk_group);

			// 澳门身份证号去括号
			resetMacaId(psndocVO);
			// 校验id格式
			checkID(psndocVO.getIdtype(), psndocVO);
		} catch (ValidateWithLevelException ex) {
			if (ex.getLevel() == ValidateWithLevelException.LEVEL_SERIOUS) {
				AppInteractionUtil.showErrorDialog(ex.getMessage());
				return;
			} else if (ex.getLevel() == ValidateWithLevelException.LEVEL_DECIDEBYUSER) {
				boolean result = AppInteractionUtil.showConfirmDialog(
						ResHelper.getString("6007psn", "06007psn0475")/*
																	 * @res
																	 * "确认继续"
																	 */,
						ex.getMessage() + ResHelper.getString("6007psn", "06007psn0130")/*
																						 * @
																						 * res
																						 * "，是否继续？"
																						 */);
				if (!result) {
					return;
				}
			} else if (ex.getLevel() == ValidateWithLevelException.LEVEL_CANACCEPT) {
				AppInteractionUtil.showMessageDialog(ex.getMessage());
			}
		}

		IPsndocService psndocService = NCLocator.getInstance().lookup(IPsndocService.class);
		PsndocAggVO psndocAggVO;
		try {
			psndocAggVO = psndocService.checkPsnUnique(psndocVO, true);
		} catch (BusinessException ex) {
			AppInteractionUtil.showErrorDialog(ex.getMessage());
			return;
		}
		if (psndocAggVO == null || psndocAggVO.getParentVO() == null)// 没有组织关系，直接新增人员
		{
			psndocAggVO = new PsndocAggVO();
			psndocAggVO.setParentVO(psndocVO);
			// 根据id生成辅助信息
			generateGenderAndBirthdayFromID(psndocVO);
			psndocVO.getPsnOrgVO().setOrgrelaid(1);
			redictNewPsndoc(psndocVO, jobvo);
			return;
		}

		if (psndocAggVO.getParentVO().getDie_date() != null && psndocAggVO.getParentVO().getDie_remark() != null) {
			AppInteractionUtil.showErrorDialog(ResHelper.getString("6007psn", "06007psn0346")/* "当前录入人员已身故,不能继续增加." */);
			return;
		}

		// 如果人员已存在，则首先判断是否为UAP管理人员
		UFBoolean isuapmanage = psndocAggVO.getParentVO().getIsuapmanage();
		if (isuapmanage.booleanValue()) {
			// 启用状态
			int enablestate = psndocAggVO.getParentVO().getEnablestate();
			if (enablestate == IPubEnumConst.ENABLESTATE_DISABLE) {// 已停用/*
																	// "该人员为UAP管理的停用人员，是否继续？"
																	// */
				boolean result = AppInteractionUtil
						.showConfirmDialog(ResHelper.getString("6007psn", "06007psn0475")/*
																						 * @
																						 * res
																						 * "确认继续"
																						 */,
								ResHelper.getString("6007psn", "06007psn0456")/* "该人员为UAP管理的停用人员，是否继续？" */);
				if (result) {
					setSomeValue(jobvo, psndocVO, psndocAggVO);
					redictNewPsndoc(psndocVO, jobvo);
				} 
				return;
				
			}
		}

		if (!psndocAggVO.getParentVO().getPsnOrgVO().getEndflag().booleanValue()) // 有组织关系，并且尚未结束。
		{
			String strMsg = ResHelper.getString("6007psn", "06007psn0145")/*
																		 * @res
																		 * "集团中已存在相同人员，不能再次采集！"
																		 */
					+ "\n";
			strMsg += generateUniqueMsg(psndocAggVO.getParentVO(), psndocVO);
			AppInteractionUtil.showErrorDialog(strMsg);
			return;
		} else if (psndocAggVO.getParentVO().getPsnOrgVO().getEndflag().booleanValue()) {
			// 曾有过组织关系，现已结束
			// String strMsg = "此人曾有过组织关系，是否继续(返聘、再聘) ?\n{0}";
			String strMsg = ResHelper.getString("6007psn", "06007psn0146")/*
																		 * @res
																		 * "此人曾有过组织关系，是否继续 ?"
																		 */
					+ "\n";
			if (psndocAggVO.getParentVO().getPsnOrgVO().getPsntype().intValue() == ((Integer) PsnType.POI.value())
					.intValue()) {
				// 相关人员提示信息不同
				strMsg = ResHelper.getString("6007psn", "06007psn0147")/*
																		 * @res
																		 * "此人为集团历史相关人员，信息如下，是否继续?"
																		 */
						+ "\n";
			}
			strMsg += generateUniqueMsg(psndocAggVO.getParentVO(), psndocVO);
			boolean result = AppInteractionUtil.showConfirmDialog(ResHelper.getString("6007psn", "06007psn0475")/*
																												 * @
																												 * res
																												 * "确认继续"
																												 */,
					strMsg);
			if (result) {
				setSomeValue(jobvo, psndocAggVO.getParentVO(), psndocAggVO);
				redictNewPsndoc(psndocAggVO.getParentVO(), jobvo);
			}
			return;
		}

		try {
			psndocAggVO = NCLocator.getInstance().lookup(nc.itf.hi.IPsndocService.class)
					.checkPsnUnique(psndocVO, false);
		} catch (BusinessException e) {
			e.printStackTrace();
			CommonUtil.showErrorDialog("该员工已被列入黑名单，不允许入职登记！");
		}
		if (psndocAggVO == null) {// 新入职人员 CommonUtil.showShortMessage("新入职人员");
			redictNewPsndoc(psndocVO, jobvo);
		} else {// 辞职返聘人员
			if (validateRegDate(psndocAggVO) && validateReReg(psndocAggVO)) {
				redictOldPsndoc(psndocAggVO);
			}
		}

	}

	void redictNewPsndoc(PsndocVO psndocVO, PsnJobVO jobvo) {
		CommonUtil.closeViewDialog(ONLY_INFO_CARD_ID);
		CommonUtil.setCacheValue(AppPsndocConstant.PSN_EMPLOY_INFO, psndocVO);
		CommonUtil.setCacheValue(AppPsndocConstant.PSN_EMPLOY_JOB, jobvo);
		CommonUtil.showViewDialog(PSN_EMPLOY_CARD_ID, "入职登记单", 802, 1227);
	}

	void redictOldPsndoc(PsndocAggVO psndocAggVO) {
		CommonUtil.closeViewDialog(ONLY_INFO_CARD_ID);
		CommonUtil.showShortMessage("辞职返聘人员");
		CommonUtil.setCacheValue(AppPsndocConstant.PSN_EMPLOY_INFO, psndocAggVO.getParentVO());
		CommonUtil.setCacheValue(AppPsndocConstant.PSN_EMPLOY_JOB, psndocAggVO.getParentVO().getPsnJobVO());
		CommonUtil.showViewDialog(PSN_EMPLOY_CARD_ID, "入职登记单", 802, 1227);
	}

	private String generateUniqueMsg(PsndocVO dbVO, PsndocVO clientVO) {
		String strFieldCodes[] = { PsndocVO.IDTYPE, PsndocVO.ID, PsndocVO.NAME, PsnJobVO.PSNTYPE };
		String strMsg = "";
		String idtype = dbVO.getIdtype();
		PsnIdtypeVO psnIdtypeVO = null;
		try {
			psnIdtypeVO = (PsnIdtypeVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
					.retrieveByPk(null, PsnIdtypeVO.class, idtype);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		for (String strFieldCode : strFieldCodes) {
			String strFieldChnName = strFieldCode;
			// if (PsndocVO.ID.equalsIgnoreCase(strFieldCode))
			// {
			// strMsg += strFieldChnName + ": " + clientVO.getId() + "\n";
			// }
			if (PsndocVO.IDTYPE.equalsIgnoreCase(strFieldCode)) {
				strMsg += strFieldChnName + ": " + VOUtils.getNameByVO(psnIdtypeVO) + "\n";
			} else if (strFieldCode.startsWith(PsndocVO.NAME)) {
				String fieldValue = MultiLangHelper.getName(dbVO);
				fieldValue = StringUtils.isBlank(fieldValue) ? ResHelper.getString("6007psn", "06007psn0148")/*
																											 * @
																											 * res
																											 * "无"
																											 */
				: fieldValue;
				strMsg += strFieldChnName + ": " + fieldValue + "\n";
			} else {
				String fieldValue = (String) dbVO.getAttributeValue(strFieldCode);
				// 处理提示中的null值
				fieldValue = StringUtils.isBlank(fieldValue) ? ResHelper.getString("6007psn", "06007psn0148")/*
																											 * @
																											 * res
																											 * "无"
																											 */
				: fieldValue;
				strMsg += strFieldChnName + ": " + fieldValue + "\n";
			}
		}
		if (dbVO.getPsnJobVO() == null) {
			return strMsg;
		}

		String orgname = dbVO.getPsnJobVO().getOrgname() == null ? ResHelper.getString("6007psn", "06007psn0148")/*
																												 * @
																												 * res
																												 * "无"
																												 */
		: (String) dbVO.getPsnJobVO().getOrgname();
		strMsg += ResHelper.getString("6007psn", "06007psn0149")/* @res "所在组织: " */
				+ orgname + "\n";

		String deptname = dbVO.getPsnJobVO().getDeptname() == null ? ResHelper.getString("6007psn", "06007psn0148")/*
																													 * @
																													 * res
																													 * "无"
																													 */
		: (String) dbVO.getPsnJobVO().getDeptname();
		strMsg += ResHelper.getString("6007psn", "06007psn0150")/* @res "所在部门: " */
				+ deptname + "\n";

		if (dbVO.getPsnOrgVO().getPsntype().intValue() != ((Integer) PsnType.POI.value()).intValue()) {
			// 相关人员没有岗位
			String jobname = dbVO.getPsnJobVO().getJobname() == null ? ResHelper.getString("6007psn", "06007psn0148")/*
																													 * @
																													 * res
																													 * "无"
																													 */
			: (String) dbVO.getPsnJobVO().getJobname();
			strMsg += ResHelper.getString("6007psn", "06007psn0151")/*
																	 * @res
																	 * "所在岗位: "
																	 */
					+ jobname + "";
		}
		return strMsg;
	}

	private void setSomeValue(PsnJobVO psnjobvo, PsndocVO psndocVO, PsndocAggVO psndocAggVO) {

		PsnOrgVO org = new PsnOrgVO();
		org.setOrgrelaid(psndocAggVO.getParentVO().getPsnOrgVO().getOrgrelaid() + 1);

		// modify for 港华 新增时 不提供开始日期的默认值//modify by 原型用户验证版,相关人员还是要有默认值
		// 设置默认开始日期
		if (psnjobvo.getPsntype() == (Integer) PsnType.POI.value()) {
			UFLiteralDate orgBegin = null;
			UFLiteralDate orgEnddate = psndocAggVO.getParentVO().getPsnOrgVO().getEnddate();
			if (orgEnddate == null) {
				orgEnddate = psndocAggVO.getParentVO().getPsnOrgVO().getBegindate();
			}
			UFLiteralDate jobEnd = psndocAggVO.getParentVO().getPsnJobVO().getEnddate();
			if (jobEnd == null) {
				jobEnd = psndocAggVO.getParentVO().getPsnJobVO().getBegindate();
			}

			if (orgEnddate.afterDate(jobEnd)) {
				orgBegin = orgEnddate.getDateAfter(1);
			} else {
				orgBegin = jobEnd.getDateAfter(1);
			}
			org.setBegindate(orgBegin);
		}
		psndocAggVO.getParentVO().setPsnOrgVO(org);

		PsnJobVO job = new PsnJobVO();

		// modify for 港华 新增时 不提供开始日期的默认值//modify by 原型用户验证版,相关人员还是要有默认值
		// 设置默认开始日期
		if (psnjobvo.getPsntype() == (Integer) PsnType.POI.value()) {
			UFLiteralDate jobBegin = null;
			UFLiteralDate jobEnddate = psndocAggVO.getParentVO().getPsnJobVO().getEnddate();
			if (jobEnddate != null) {
				jobBegin = jobEnddate.getDateAfter(1);
			} else {
				UFLiteralDate jobBegindate = psndocAggVO.getParentVO().getPsnJobVO().getBegindate();
				if (jobBegindate != null) {
					jobBegin = jobBegindate.getDateAfter(1);
				}
			}
			job.setBegindate(jobBegin);
		}
		job.setClerkcode(psndocAggVO.getParentVO().getPsnJobVO().getClerkcode());
		psndocAggVO.getParentVO().setPsnJobVO(job);
		if (psndocVO != null) {
			psndocAggVO.getParentVO().getPsnJobVO().setPk_psncl(psndocVO.getPsnJobVO().getPk_psncl());
		}
		// 将启用状态设为已启用
		psndocAggVO.getParentVO().setEnablestate(EnableStateEnum.ENABLESTATE_ENABLE.toIntValue());
	}

	private void generateGenderAndBirthdayFromID(PsndocVO psndocVO) {
		if (psndocVO == null) {
			return;
		}
		String id = psndocVO.getId();
		String idtype = psndocVO.getIdtype();
		if (idtype == null) {
			idtype = "1001Z01000000000AI36";
		}
		if (id == null || id.length() < 1 || !idtype.equals("1001Z01000000000AI36")) {
			return;
		}
		psndocVO.setSex(getSex(id));
		UFLiteralDate birthday = null;
		try {
			birthday = getBirthdate(id) == null ? null : UFLiteralDate.getDate(getBirthdate(id));
		} catch (Exception e) {
			birthday = null;
		}
		// 当用户输入了出生日期，就不根据身份证计算日期
		if (null == psndocVO.getBirthdate()) {
			psndocVO.setBirthdate(birthday);
		}
	}

	private String getBirthdate(String ID) {
		if (ID.length() != 15 && ID.length() != 18) {
			// 不是15位或18位返回null
			return null;
		}
		String birth = ID.length() == 15 ? "19" + ID.substring(6, 12) : ID.substring(6, 14);
		String year = birth.substring(0, 4);
		String month = birth.substring(4, 6);
		String date = birth.substring(6);
		return year + "-" + month + "-" + date;
	}

	/*
	 * 保存前去掉澳门身份证后增加的特殊符号
	 * 
	 * @param psndocVO
	 */
	private void resetMacaId(PsndocVO psndocVO) {

		String pid = psndocVO.getId();
		if (HICommonValue.IDTYPE_MACAU.equals(psndocVO.getIdtype()) && pid.endsWith(")")
				&& "(".equals(String.valueOf(pid.charAt(pid.length() - 3)))) {
			psndocVO.setId(pid.substring(0, pid.length() - 3) + pid.charAt(pid.length() - 2));
		}
	}

	/**
	 * 
	 * 检查输入参数是否为空
	 * 
	 * @param vo
	 *            人员
	 * @param pk_psncl
	 *            人员类别
	 */
	private void checkNull(PsndocVO vo, String pk_psncl) {
		if (StringUtils.isEmpty(vo.getName())) {
			throw new LfwRuntimeException("人员姓名不能为空！");
		}
		if (StringUtils.isEmpty(vo.getIdtype())) {
			throw new LfwRuntimeException("证件类型不能为空！");
		}
		if (StringUtils.isEmpty(vo.getId())) {
			throw new LfwRuntimeException("证件号码不能为空！");
		}
		if (StringUtils.isEmpty(pk_psncl)) {
			throw new LfwRuntimeException("人员类别不能为空！");
		}

	}

	/**
	 * 
	 * 验证证件类型及身份证简单规范
	 * 
	 * @param vo
	 * @return
	 */
	private boolean valdateIDCard(PsndocVO vo) {
		boolean result = true;
		if (AppPsndocConstant.ID_CARD_TYPE.equals(vo.getIdtype()) && vo.getId() != null) {
			if (vo.getId().length() != 15 && vo.getId().length() != 18) {
				result = CommonUtil.showConfirmDialog("身份证不合法，是否继续？");
			}
		}
		return result;
	}

	/**
	 * 加载人员数据集
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad(DataLoadEvent dataLoadEvent) {
		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		NCRefNode idtypeRefNode = (NCRefNode) widget.getViewModels().getRefNode("refnode_ds_psndoc_idtype_name");
		idtypeRefNode.setRefcode("证件类型");
		Dataset ds = dataLoadEvent.getSource();
		DatasetUtil.initWithEmptyRow(ds, true, Row.STATE_NORMAL);
	}

	/**
	 * 加载工作记录数据集
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad_ds_psnjob(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		DatasetUtil.initWithEmptyRow(ds, true, Row.STATE_NORMAL);
	}

	/**
	 * 校验身份证年龄是否合法
	 * 
	 * @param value
	 * @return PsndocVO
	 * @throws ValidateWithLevelException
	 * @throws BusinessException
	 */
	private void validatePsndoc(Object value) throws ValidateWithLevelException, BusinessException {
		// 保存校验，应该提出来一个bean
		PsndocVO psndocVO = (PsndocVO) value;
		// 校验当前人员是否已到16周岁
		UFLiteralDate date = psndocVO.getBirthdate();
		if (date != null) {
			UFLiteralDate today = PubEnv.getServerLiteralDate();
			int year = date.getYear();
			int year16 = year + 16;
			UFLiteralDate date16 = new UFLiteralDate(year16 + "-" + date.getMonth() + "-" + date.getDay());
			if (!date16.beforeDate(today)) {
				CommonUtil.showErrorDialog("该人员未满16周岁，根据《中华人民共和国劳动法》第十五条规定，禁止用人单位招用未满十六周岁的未成年人！ ");
			}

		} else {
			// return true;
		}
	}

	/**
	 * 离职后再入职时间间隔校验校验
	 * 
	 * @return boolean 成功为true，不成功为false
	 * @author yangshuo
	 * @throws Exception
	 * @Created on 2012-12-3 上午11:21:37
	 */
	private boolean validateRegDate(PsndocAggVO psndocAggVO) throws Exception {
		// 现在的入职日期
		UFLiteralDate currBegindate = psndocAggVO.getParentVO().getPsnOrgVO().getBegindate();
		String pk_org = SessionUtil.getPk_org();
		String pk_psndoc = psndocAggVO.getParentVO().getPk_psndoc();
		HashMap<String, String> org = new HashMap<String, String>();
		HashMap<String, UFLiteralDate> date = new HashMap<String, UFLiteralDate>();
		org.put(pk_psndoc, pk_org);
		date.put(pk_psndoc, currBegindate);
		HashMap<String, String> name = NCLocator.getInstance().lookup(IPsndocQryService.class)
				.validateRegDate(org, date);

		if (name == null) {
			return true;
		}
		Integer para = SysInitQuery.getParaInt(pk_org, HICommonValue.PARA_7);
		return CommonUtil.showConfirmDialog("人员" + name.values().toArray(new String[0])[0] + "上一次离职至今不满"
				+ para.toString() + "个月，是否继续入职？");
	}

	private boolean validateReReg(PsndocAggVO psndocAggVO) throws BusinessException {
		String pkString = psndocAggVO.getParentVO().getPk_psndoc();
		// 查询人员最新离职日期
		HashMap<String, UFLiteralDate> dimiDateMap = getEnddateOfDimStaff(new String[] { pkString });
		if (dimiDateMap == null) {
			return CommonUtil.showConfirmDialog("当前入职人员身份证号与" + psndocAggVO.getParentVO().getCode()
					+ psndocAggVO.getParentVO().getName() + "相同，是否允许入职？");
		} else {
			return pkString != null ? CommonUtil.showConfirmDialog("该人员于" + dimiDateMap.get(pkString)
					+ "离职,目前属于重新入职,继续办理入职吗?") : true;
		}

	}

	/*	*//**
	 * 通过身份证号获取出生日期
	 * 
	 * @param ID
	 *            身份证号
	 * @return
	 */
	/*
	 * private UFLiteralDate getBirthdate(String ID) throws
	 * InvocationTargetException { if (ID == null) return null; if (ID.length()
	 * != 15 && ID.length() != 18) { // 不是15位或18位返回null return null; } String
	 * birth = ID.length() == 15 ? "19" + ID.substring(6, 12) : ID.substring(6,
	 * 14); String year = birth.substring(0, 4); String month =
	 * birth.substring(4, 6); String date = birth.substring(6); UFLiteralDate
	 * ufDate = null;
	 * 
	 * Date d = new Date(year + "-" + month + "-" + date); ufDate = new
	 * UFLiteralDate(year + "-" + month + "-" + date); return ufDate; }
	 */

	private Integer getSex(String ID) {
		if (ID.length() != 15 && ID.length() != 18) {
			return null;
		}
		int isex = 2;
		isex = ID.length() == 15 ? Integer.parseInt(ID.substring(14)) : Integer.parseInt(ID.substring(16, 17));
		return isex % 2 == 0 ? 2 : 1;
	}

	/**
	 * 根据离职人员pk得到已离职人员的离职日期，仅在返聘再聘时调用，查不到则返回null
	 * 
	 * @author yangshuo
	 * @throws BusinessException
	 * @Created on 2012-12-3 下午03:52:54
	 */
	private HashMap<String, UFLiteralDate> getEnddateOfDimStaff(String... pk_psndoc) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();

		PsnOrgVO[] psnorg = (PsnOrgVO[]) NCLocator
				.getInstance()
				.lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, PsnOrgVO.class,
						" lastflag = 'Y' and endflag = 'Y' and pk_psndoc in  (" + isc.getInSQL(pk_psndoc) + ") ");
		if (psnorg == null || psnorg.length == 0) {
			return null;
		}

		HashMap<String, UFLiteralDate> map = new HashMap<String, UFLiteralDate>();
		for (PsnOrgVO org : psnorg) {
			map.put(org.getPk_psndoc(), org.getEnddate());
		}
		return map;

	}

	/**
	 * 取消按钮
	 * 
	 * @param mouseEvent
	 */
	public void Cancelonclick(MouseEvent<?> mouseEvent) {
		CommonUtil.closeViewDialog(ONLY_INFO_CARD_ID);
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-3-15 16:15:21<br>
	 * 
	 * @param iIdType
	 *            证件类型
	 * @param strId
	 *            证件号码
	 * @throws ValidateWithLevelException
	 * @author Rocex Wang
	 ***************************************************************************/
	public void checkID(String iIdType, PsndocVO psndocVO) throws ValidateWithLevelException {
		String strId = psndocVO.getId();
		if (strId == null || strId.trim().length() == 0) {
			return;
		}
		// Map idValidator = this.idValidatorConfig.getIdValidator();
		// if (!idValidator.containsKey(String.valueOf(iIdType)))
		// {
		// return;
		// }
		// String strClassName = (String)
		// idValidator.get(String.valueOf(iIdType));
		String strClassName = PsnIdtypeQuery.getPsnIdtypeVo(iIdType).getIdtypevalidat();
		if (StringUtils.isEmpty(strClassName))
			return;
		try {
			// ((IFieldValidator)
			// Class.forName(strClassName).newInstance()).validate(strId);
			nc.vo.bd.psn.PsndocVO psndocUapVO = new nc.vo.bd.psn.PsndocVO();
			org.apache.commons.beanutils.BeanUtils.copyProperties(psndocUapVO, psndocVO);
			ValidationFailure failure = ((nc.bs.uif2.validation.Validator) Class.forName(strClassName).newInstance())
					.validate(psndocUapVO);
			if (failure != null) {
				throw new ValidateWithLevelException(failure.getMessage(),
						ValidateWithLevelException.LEVEL_DECIDEBYUSER);
			}
		} catch (InstantiationException ex) {
			Logger.error(ex.getMessage(), ex);
			throw new LfwRuntimeException(ex.getMessage());
		} catch (InvocationTargetException e) {
			Logger.error(e.getMessage(), e);
			throw new LfwRuntimeException(e.getMessage());
		}

		catch (IllegalAccessException ex) {
			Logger.error(ex.getMessage(), ex);
		} catch (ClassNotFoundException ex) {
			Logger.error(ex.getMessage(), ex);
		}
	}
}
