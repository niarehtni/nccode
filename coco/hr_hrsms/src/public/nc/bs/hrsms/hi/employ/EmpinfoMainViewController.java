package nc.bs.hrsms.hi.employ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.hrsms.hi.ShopPsnInfoUtil;
import nc.bs.hrsms.hi.hrsmsUtil;
import nc.bs.hrss.hi.psninfo.AlterationParse;
import nc.bs.hrss.hi.psninfo.PsninfoConsts;
import nc.bs.hrss.hi.psninfo.PsninfoUtil;
import nc.bs.hrss.pub.DialogSize;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.advpanel.cata.CatagoryPanel;
import nc.bs.hrss.pub.advpanel.cata.CatagorySelectorCtrl;
import nc.bs.hrss.pub.cmd.LineDownCmd;
import nc.bs.hrss.pub.cmd.LineUpCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hrss.hi.setalter.ISetalterService;
import nc.itf.rm.IRMPsndocQueryService;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.IVOPersistence;
import nc.uap.lfw.core.cache.LfwCacheManager;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifDatasetLoadCmd;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.ctx.ViewContext;
import nc.uap.lfw.core.ctx.WindowContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.data.RowData;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.event.TextEvent;
import nc.uap.lfw.core.exception.LfwBusinessException;
import nc.uap.lfw.core.model.plug.TranslatedRow;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.uap.lfw.core.uif.delegator.DefaultDataValidator;
import nc.uap.lfw.jsp.uimeta.UIControl;
import nc.uap.lfw.jsp.uimeta.UILayoutPanel;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.lfw.jsp.uimeta.UITabComp;
import nc.uap.lfw.jsp.uimeta.UITabItem;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.EduVO;
import nc.vo.hi.psndoc.NationDutyVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.TitleVO;
import nc.vo.hr.validator.HKIDCardValidator;
import nc.vo.hr.validator.IDCardValidator;
import nc.vo.hr.validator.IFieldValidator;
import nc.vo.hr.validator.ValidateWithLevelException;
import nc.vo.hrss.hi.psninfo.AlterationVO;
import nc.vo.hrss.hi.setalter.HrssSetalterVO;
import nc.vo.hrss.hi.setalter.SetConsts;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.rm.psndoc.AggRMPsndocVO;
import nc.vo.rm.psndoc.RMPsndocVO;
import nc.vo.rm.psndoc.common.RMApplyTypeEnum;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

public class EmpinfoMainViewController implements IController {

	/**
	 * 保存（适用于所有卡片）  
	 */
	public void save(MouseEvent<MenuItem> mouseEvent) {
		SuperVO currVO = getCurrSuperVO();
		String pk_psndoc = (String) currVO.getAttributeValue(HrssSetalterVO.PK_PSNDOC);

		try {
			// 从session中取到人员信息中需要审核的字段的list
			@SuppressWarnings("unchecked")
			ArrayList<String> checkFiledList = (ArrayList<String>) AppUtil.getAppAttr("checkFiledList");
			// 变化的字段
			ArrayList<String> changeFiledList = new ArrayList<String>();
			// 需要更新的字段
			ArrayList<String> needUpdateFiledList = new ArrayList<String>();
			IUAPQueryBS uapQry = null;
			boolean isNeedAudit = false;
			try {
				String pk = currVO.getPrimaryKey();
				uapQry = ServiceLocator.lookup(IUAPQueryBS.class);
				SuperVO oldVO = (SuperVO) uapQry.retrieveByPK(currVO.getClass(), pk);
				findChangeFiledList(currVO, changeFiledList, oldVO);

				for (String filed : changeFiledList) {
					if (!CollectionUtils.isEmpty(checkFiledList) && checkFiledList.contains(filed)) {
						isNeedAudit = true;
					} else {
						// 把变化的字段并且是不需要审核的字段的值设置到人员信息表中对应的Vo
						oldVO.setAttributeValue(filed, currVO.getAttributeValue(filed));
						needUpdateFiledList.add(filed);
					}
				}
				if (!CollectionUtils.isEmpty(needUpdateFiledList)) {
					// 把需要更新的字段插入到数据库中
					ServiceLocator.lookup(IPersistenceUpdate.class).updateVO(null, oldVO,
							needUpdateFiledList.toArray(new String[0]), null);
				}
			} catch (HrssException e) {
				e.alert();
			} catch (BusinessException e) {
				new HrssException(e).deal();
			}

			if (isNeedAudit) {
				// 需审核时
				HrssSetalterVO alterVO = getNoSubmitHrssSetalterVO(pk_psndoc);
				ISetalterService service = ServiceLocator.lookup(ISetalterService.class);
				String pk_infoset = PsninfoUtil.getInfosetPKByCode(getCurrDatasetId());
				String xml = PsninfoUtil.updSuperVOToXML(currVO);
				if (xml == null) {
				} else if (alterVO == null) {
					alterVO = getNewHrssSetalterVO(pk_psndoc, pk_infoset, xml);
					service.insertVO(alterVO);
				} else {
					alterVO.setAlter_context(xml);
					alterVO.setAlter_date(new UFDate());
					service.updateVO(alterVO);
				}

			}
			// 设置提示信息
			PsninfoUtil.querySetVOs(pk_psndoc);
			
			//改变
			ShopPsnInfoUtil.SetCompState(getCurrDatasetId());
			
			
			CommonUtil.showShortMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pe-res",
					"0c_pe-res0093")/* @res "保存成功！" */);
		} catch (BusinessException e1) {
			new HrssException(e1).deal();
		} catch (HrssException e1) {
			e1.alert();
		} catch (Exception e1) {
			new HrssException(e1).deal();
		}
	}

	/**
	 * 组织关系的修改按钮
	 * 
	 * @param mouseEvent
	 */
	public void update(MouseEvent<MenuItem> mouseEvent) {
		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		Dataset ds = widget.getViewModels().getDataset(getCurrDatasetId());
		ds.setEnabled(true);

	}

	/**
	 * 校验身份证是否合法
	 * 
	 * @param value
	 * @return
	 * @throws ValidateWithLevelException
	 * @throws BusinessException
	 */
	private boolean validateId(PsndocVO psndocVO) {
		String id = psndocVO.getId();
		String idtype = psndocVO.getIdtype();

		String idtype_cn = "1001Z01000000000AI36";
		String idtype_hk = "1001Z01000000000CHUK";

		if (StringUtils.isEmpty(idtype) || idtype_cn.equals(idtype) || idtype_hk.equals(idtype)) {
			// 不是身份证或香港身份证不校验
			return true;
		}

		IFieldValidator v = null;
		if (idtype_cn.equals(idtype)) {
			v = new IDCardValidator();
		} else {
			v = new HKIDCardValidator();
		}

		try {
			v.validate(id);
		} catch (ValidateWithLevelException ex) {
			String msg = ex.getMessage() + ", "
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_trn-res0052")/*
																										 * @
																										 * res
																										 * "确认继续吗?"
																										 */;
			return CommonUtil.showConfirmDialog(
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0019")/*
																									 * @
																									 * res
																									 * "确认继续"
																									 */, msg);
		}

		return true;
	}

	/**
	 * 获取当前数据集反序列化的VO
	 * 
	 * @return
	 */
	private SuperVO getCurrSuperVO() {
		Dataset2SuperVOSerializer<SuperVO> ser = new Dataset2SuperVOSerializer<SuperVO>();
		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		Dataset masterDs = widget.getViewModels().getDataset(getCurrDatasetId());
		new DefaultDataValidator().validate(masterDs, new LfwView());
		SuperVO[] superVOs = ser.serialize(masterDs);
		return superVOs[0];
	}

	private String getCurrDatasetId() {
		return PsninfoUtil.getCurrDataset();
	}

	private HrssSetalterVO getNewHrssSetalterVO(String pk_psndoc, String pk_infoset, String xml) {
		SessionBean session = SessionUtil.getSessionBean();
		HrssSetalterVO vo = new HrssSetalterVO();
		vo.setPk_psndoc(pk_psndoc);
		vo.setPk_infoset(pk_infoset);
		vo.setData_status(PsninfoConsts.STATUS_NOSUMIT);
		vo.setAlter_context(xml);
		vo.setPk_operator(session.getUserVO().getCuserid());
		vo.setAlter_date(new UFDate());
		vo.setPk_group(SessionUtil.getPk_group());
		vo.setPk_org(SessionUtil.getPsndocVO().getPsnJobVO().getPk_hrorg());
		vo.setPk_psnjob(session.getPsnjobVO().getPk_psnjob());
		vo.setPk_dept(session.getPk_dept());
		vo.setConfirm_flag(UFBoolean.FALSE);
		return vo;
	}

	/**
	 * 个人信息卡片  取消操作
	 * 
	 * @param mouseEvent
	 */
	public void CancelLisn(MouseEvent<MenuItem> mouseEvent) {
		// 执行左侧快捷查询
		CmdInvoker.invoke(new UifPlugoutCmd(CatagoryPanel.WIDGET_ID, CatagorySelectorCtrl.PO_CATAGORY_CHANGED));
	}

	/**
	 * 重新查询
	 * 
	 * @param keys
	 */
	public void pluginReSearch(Map<String, Object> keys) {
		// 执行左侧快捷查询
		CmdInvoker.invoke(new UifPlugoutCmd(CatagoryPanel.WIDGET_ID, CatagorySelectorCtrl.PO_CATAGORY_CHANGED));
	}

	/**
	 * 还原
	 * 
	 * @param mouseEvent
	 */
	public void RevertLisn(MouseEvent<MenuItem> mouseEvent) {
		SessionBean session = SessionUtil.getSessionBean();
		/* String pk_psndoc = session.getPsndocVO().getPk_psndoc(); */
		String pk_psndoc = (String) CommonUtil.getCacheValue("pk_psndoc");
		try {
			ISetalterService service = ServiceLocator.lookup(ISetalterService.class);
			String pk_infoset = PsninfoUtil.getInfosetPKByCode(getCurrDatasetId());
			HrssSetalterVO vo = service.queryNoSubmitHrssSetalterVO(session.getPsndocVO().getPk_psndoc(), pk_infoset);
			if (vo == null) {
				new HrssException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0003")/*
																													 * @
																													 * res
																													 * "不存在待提交记录!"
																													 */)
						.alert();
			}
			service.deleteVO(vo);
			SuperVO[] superVOs = queryVOs(pk_psndoc, getCurrDatasetId());
			LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
			Dataset ds = widget.getViewModels().getDataset(getCurrDatasetId());
			new SuperVO2DatasetSerializer().serialize(superVOs, ds, Row.STATE_NORMAL);
			if (!ArrayUtils.isEmpty(superVOs))
				ds.setRowSelectIndex(0);
			if (PsninfoConsts.INFOSET_CODE_PSNDOC.equals(getCurrDatasetId())) {
				ds.setValue("patha", "pt/psnImage/download?pk_psndoc=" + pk_psndoc + "&random=" + Math.random());
			}
			PsninfoUtil.querySetVOs(pk_psndoc);
			ShopPsnInfoUtil.SetCompState(getCurrDatasetId()); // // 设置提示信息
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
		} catch (HrssException e1) {
			Logger.error(e1.getMessage(), e1);
		}
	}

	/**
	 * 提交操作（适用于多个卡片）
	 * 
	 * @param mouseEvent
	 */
	public void CommitLisn(MouseEvent<MenuItem> mouseEvent) {
		// 取pk，该pk来自数据加载，这样存进去的pk位置才是正确的位置
		String temp_psndoc = (String) CommonUtil.getCacheValue("pk_psndoc");
		// String temp_infoset = (String)CommonUtil.getCacheValue("t");
		try {
			ISetalterService service = ServiceLocator.lookup(ISetalterService.class);
			String pk_infoset = PsninfoUtil.getInfosetPKByCode(getCurrDatasetId());
			HrssSetalterVO vo = service.queryNoSubOrAudOrConfirmHrssSetalterVO(
			/* pk_psndoc */temp_psndoc, pk_infoset);
			if (vo == null) {
				CommonUtil.showErrorDialog(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0020")/*
																										 * @
																										 * res
																										 * "提交失败"
																										 */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0004")/*
																										 * @
																										 * res
																										 * "该记录提交失败"
																										 */);
				// new
				// HrssException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res",
				// "0c_hi-res0004")/*
				// * @
				// * res
				// * "该记录提交失败"
				// */)
				// .alert();
			}
			vo.setAttributeValue(HrssSetalterVO.DATA_STATUS, SetConsts.NOAUDIT);
			vo.setAlter_date(new UFDate());
			service.commitVO(vo);
			// 设置提示信息
			PsninfoUtil.querySetVOs(temp_psndoc);// pk_psndoc);
			ShopPsnInfoUtil.SetCompState(getCurrDatasetId());
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
		} catch (HrssException e1) {
			Logger.error(e1.getMessage(), e1);
		}
	}

	/**
	 * 个人信息修改需要审核，未审核时收回
	 * 
	 * @param mouseEvent
	 */
	public void CallbackLisn(MouseEvent<MenuItem> mouseEvent) {
		String pk_psndoc = (String) CommonUtil.getCacheValue("pk_psndoc");
		try {
			ISetalterService service = ServiceLocator.lookup(ISetalterService.class);
			String pk_infoset = PsninfoUtil.getInfosetPKByCode(getCurrDatasetId());
			HrssSetalterVO vo = service.queryNoAuditHrssSetalterVO(pk_psndoc, pk_infoset);
			if (vo == null) {
				CommonUtil.showErrorDialog(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0021")/*
																										 * @
																										 * res
																										 * "提交失败"
																										 */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0005")/*
																										 * @
																										 * res
																										 * "该记录已修改，请刷新后操作！"
																										 */);

			}
			vo.setAttributeValue(HrssSetalterVO.DATA_STATUS, SetConsts.NOSUBMIT);
			vo.setAlter_date(new UFDate());
			service.commitVO(vo);
			// 设置提示信息
			PsninfoUtil.querySetVOs(pk_psndoc);
			ShopPsnInfoUtil.SetCompState(getCurrDatasetId());
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
		} catch (HrssException e1) {
			Logger.error(e1.getMessage(), e1);
		}
	}

	/**
	 * 继续修改
	 * 
	 * @param mouseEvent
	 */
	public void GoonUpdateLisn(MouseEvent<MenuItem> mouseEvent) {
		SessionBean session = SessionUtil.getSessionBean();
		/* String pk_psndoc = session.getPsndocVO().getPk_psndoc(); */
		String pk_psndoc = (String) CommonUtil.getCacheValue("pk_psndoc");
		// 设置为可编辑
		String mainDs = PsninfoConsts.PSNDOC_DS_ID;
		Dataset ds = getView().getViewModels().getDataset(mainDs);
		ds.setEnabled(true);
		try {
			ISetalterService service = ServiceLocator.lookup(ISetalterService.class);
			String pk_infoset = PsninfoUtil.getInfosetPKByCode(getCurrDatasetId());
			HrssSetalterVO vo = service.queryNoSubOrAudOrConfirmHrssSetalterVO(session.getPsndocVO().getPk_psndoc(),
					pk_infoset);
			if (vo == null) {
				CommonUtil.showErrorDialog(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0022")/*
																										 * @
																										 * res
																										 * "继续修改失败"
																										 */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0005")/*
																										 * @
																										 * res
																										 * "该记录已修改，请刷新后操作！"
																										 */);
			}
			service.goonUpdate(vo);
			// 设置提示信息
			PsninfoUtil.querySetVOs(pk_psndoc);
			ShopPsnInfoUtil.SetCompState(getCurrDatasetId());
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
		} catch (HrssException e1) {
			Logger.error(e1.getMessage(), e1);
		}
	}

	/**
	 * 放弃修改
	 * 
	 * @param mouseEvent
	 */
	public void NoUpdateLisn(MouseEvent<MenuItem> mouseEvent) {
		String pk_psndoc = (String) CommonUtil.getCacheValue("pk_psndoc");
		try {
			ISetalterService service = ServiceLocator.lookup(ISetalterService.class);
			String pk_infoset = PsninfoUtil.getInfosetPKByCode(getCurrDatasetId());
			HrssSetalterVO vo = service.queryNoSubOrAudOrConfirmHrssSetalterVO(pk_psndoc, pk_infoset);
			if (vo == null) {

				CommonUtil.showErrorDialog(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0023")/*
																										 * @
																										 * res
																										 * "放弃修改失败"
																										 */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0005")/*
																										 * @
																										 * res
																										 * "该记录已修改，请刷新后操作！"
																										 */);
				// new
				// HrssException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res",
				// "0c_hi-res0005")/*
				// * @
				// * res
				// * "该记录已修改，请刷新后操作！"
				// */)
				// .alert();
			}
			service.confirmAudit(vo);
			SuperVO[] superVOs = queryVOs(pk_psndoc, getCurrDatasetId());
			LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
			Dataset ds = widget.getViewModels().getDataset(getCurrDatasetId());
			new SuperVO2DatasetSerializer().serialize(superVOs, ds, Row.STATE_NORMAL);
			if (!ArrayUtils.isEmpty(superVOs))
				ds.setRowSelectIndex(0);
			if (PsninfoConsts.INFOSET_CODE_PSNDOC.equals(getCurrDatasetId())) {
				ds.setValue("patha", "pt/psnImage/download?pk_psndoc=" + pk_psndoc + "&random=" + Math.random());
			}
			// 设置提示信息
			PsninfoUtil.querySetVOs(pk_psndoc);
			ShopPsnInfoUtil.SetCompState(getCurrDatasetId());
		} catch (BusinessException e1) {
			new HrssException(e1).deal();
		} catch (HrssException e1) {
			e1.alert();
		}
	}

	/**
	 * 新增add事件
	 * 
	 * @param mouseEvent
	 */
	public void AddLineLisn(MouseEvent<MenuItem> mouseEvent) {
		ApplicationContext applicationContext = AppLifeCycleContext.current().getApplicationContext();
		applicationContext.addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, HrssConsts.POPVIEW_OPERATE_ADD);
		String dataset = getCurrDatasetId();
		LfwView widget = AppLifeCycleContext.current().getWindowContext().getViewContext(HrssConsts.PAGE_MAIN_WIDGET)
				.getView();
		Dataset ds = widget.getViewModels().getDataset(dataset);
		DialogSize size = PsninfoUtil.getDatasetWidth(dataset);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(PsninfoConsts.CURR_DATASET, ds.getVoMeta());
		paramMap.put("nodecode", PsninfoConsts.PSNINFO_NODECODE);
		String title = NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0006")/*
																								 * @
																								 * res
																								 * "详细信息"
																								 */;
		
		CommonUtil.showWindowDialog("ShopPsnInfoDetail", title, String.valueOf(size.getWidth()),
				String.valueOf(size.getHeight()), paramMap, ApplicationContext.TYPE_DIALOG, false, false);

	}

	public void DeleteLineLisn(MouseEvent<MenuItem> mouseEvent) {
		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		Dataset ds = widget.getViewModels().getDataset(getCurrDatasetId());
		RowData rd = ds.getCurrentRowData();
		Row[] rows = rd.getRows();
		if (ArrayUtils.isEmpty(rows))
			return;
		Dataset2SuperVOSerializer<SuperVO> ser = new Dataset2SuperVOSerializer<SuperVO>();
		Row delRow = ds.getCurrentRowSet().getCurrentRowData().getSelectedRow();
		int index = ds.getCurrentRowSet().getCurrentRowData().getRowIndex(delRow);
		SuperVO[] delVOs = ser.serialize(ds, delRow);
		SuperVO superVO = delVOs[0];
		try {
			boolean isNeedAudit = PsninfoUtil.isNeedAudit(getCurrDatasetId());
			if (!isNeedAudit) {
				ServiceLocator.lookup(IVOPersistence.class).deleteVO(superVO);
				if (EduVO.getDefaultTableName().equals(superVO.getTableName())) {
					// 学历信息
					UFBoolean lasteducation = (UFBoolean) (superVO.getAttributeValue("lasteducation"));
					if (lasteducation.booleanValue()) {
						// 当删除的学历信息是最高学历时，需要把个人信息中的学历和学位清空
						PsndocVO psnVO = SessionUtil.getPsndocVO();
						psnVO.setEdu(null);
						psnVO.setPk_degree(null);
						ServiceLocator.lookup(IVOPersistence.class).updateVO(psnVO);
					}

				}

				if (NationDutyVO.getDefaultTableName().equals(superVO.getTableName())) {
					// 职业资格
					UFBoolean istop = (UFBoolean) (superVO.getAttributeValue("istop"));
					if (istop.booleanValue()) {
						// 当删除的职业资格是最高时，需要把个人信息中的职业资格清空
						PsndocVO psnVO = SessionUtil.getPsndocVO();
						psnVO.setProf(null);
						ServiceLocator.lookup(IVOPersistence.class).updateVO(psnVO);
					}
				}

				if (TitleVO.getDefaultTableName().equals(superVO.getTableName())) {
					// 职称信息
					UFBoolean tiptop_flag = (UFBoolean) (superVO.getAttributeValue("tiptop_flag"));
					if (tiptop_flag.booleanValue()) {
						// 当删除的职称信息是最高时，需要把个人信息中的专业技术职务清空
						PsndocVO psnVO = SessionUtil.getPsndocVO();
						psnVO.setTitletechpost(null);
						ServiceLocator.lookup(IVOPersistence.class).updateVO(psnVO);
					}
				}
			} else {
				delAuditInfo(getCurrDatasetId(), index, superVO);
			}
		} catch (BusinessException e) {
			new HrssException(e).alert();
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (Exception e) {
			new HrssException(e).alert();
		}
		// 执行左侧快捷查询
		CmdInvoker.invoke(new UifPlugoutCmd(CatagoryPanel.WIDGET_ID, CatagorySelectorCtrl.PO_CATAGORY_CHANGED));
	}

	/**
	 * 需审核时删除处理
	 * 
	 * @param superVO
	 * @param dataset
	 * @throws HrssException
	 * @throws Exception
	 */
	private void delAuditInfo(String dataset, int row, SuperVO superVO) throws HrssException, Exception {
		String BeOperatedPk_psndoc = hrsmsUtil.getBoOperatePsndocPK();
		ISetalterService service = ServiceLocator.lookup(ISetalterService.class);
		String pk_infoset = PsninfoUtil.getInfosetPKByCode(dataset);
		// 查询未提交的修改记录
		HrssSetalterVO vo = service.queryNoSubmitHrssSetalterVO(BeOperatedPk_psndoc, pk_infoset);
		AlterationVO alterVO = null;
		if (vo != null) {
			alterVO = AlterationParse.parseXML(vo.getAlter_context());
		}
		SuperVO[] allSuperVOs = ShopPsnInfoUtil.querySubSet(dataset);
		allSuperVOs[row].setStatus(Row.STATE_DELETED);
		AlterationVO afterVO = PsninfoUtil.updSuperVOsToXML(allSuperVOs, alterVO);
		String xml = AlterationParse.generateXML(afterVO);
		if (vo == null) { // 不存在未提交记录时新增
			vo = getNewHrssSetalterVO(BeOperatedPk_psndoc, pk_infoset, xml);
			service.insertVO(vo);
		} else { // 存在未提交记录时修改
			vo.setAlter_date(new UFDate());
			vo.setAlter_context(xml);
			service.updateVO(vo);
		}
	}

	public void PsndocDSLoad(DataLoadEvent dataLoadEvent) {
		String execScript = "pageUI.getWidget('main').getTab('tag2905').hideTabHead();";
		AppLifeCycleContext.current().getApplicationContext().addExecScript(execScript);
		WindowContext windowContext = AppLifeCycleContext.current().getWindowContext();
		windowContext.addAppAttribute(PsninfoConsts.CURR_DATASET, PsninfoConsts.INFOSET_CODE_PSNDOC);
		windowContext.addAppAttribute(PsninfoConsts.CURR_COMP_ID, PsninfoConsts.PSNDOC_FORM_ID);
		// 清空缓存的数据集需审核信息
		windowContext.addAppAttribute(PsninfoConsts.HRSSSETS, null);
		String BeOperatedPk_psndoc = hrsmsUtil.getBoOperatePsndocPK();
		// 存pk，由于后面提交时对数据库进行操作取的是登录人的pk，调用这里的pk可以让提交时数据库存的位置正确
		try {
			String pk_infoset = PsninfoUtil.getInfosetPKByCode(getCurrDatasetId());
			PsninfoUtil.querySetVOs(BeOperatedPk_psndoc);
			Dataset dataset = AppLifeCycleContext.current().getWindowContext().getViewContext(CatagoryPanel.WIDGET_ID)
					.getView().getViewModels().getDataset(CatagorySelectorCtrl.CID_DS_CATAGORY);
			// dataset.getSelectedRow().getValue(dataset.nameToIndex(field))
			// String
			// pk_psndoc=(String)dataset.getSelectedRow().getValue(dataset.nameToIndex("pk_psndoc"));
			CommonUtil.setCacheValue("pk_psndoc", BeOperatedPk_psndoc);

			if (dataset.getCurrentRowData() != null && !ArrayUtils.isEmpty(dataset.getCurrentRowData().getRows())) {
				dataset.setRowSelectIndex(0);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		} catch (HrssException e) {
			e.alert();
		}

		/*
		 * String execScript =
		 * "pageUI.getWidget('main').getTab('tag2905').hideTabHead();";
		 * AppLifeCycleContext.current().getApplicationContext()
		 * .addExecScript(execScript); WindowContext windowContext =
		 * AppLifeCycleContext.current() .getWindowContext();
		 * windowContext.addAppAttribute(PsninfoConsts.CURR_DATASET,
		 * PsninfoConsts.INFOSET_CODE_PSNDOC);
		 * windowContext.addAppAttribute(PsninfoConsts.CURR_COMP_ID,
		 * PsninfoConsts.PSNDOC_FORM_ID); // 清空缓存的数据集需审核信息
		 * windowContext.addAppAttribute(PsninfoConsts.HRSSSETS, null); String
		 * pk_psndoc = PsninfoUtil.getPsndocPK(); try {
		 * CommonUtil.setCacheValue("pk_psndoc", pk_psndoc);
		 * PsninfoUtil.querySetVOs(pk_psndoc); Dataset dataset =
		 * AppLifeCycleContext.current().getWindowContext()
		 * .getViewContext(CatagoryPanel.WIDGET_ID).getView() .getViewModels()
		 * .getDataset(CatagorySelectorCtrl.CID_DS_CATAGORY); if
		 * (dataset.getCurrentRowData() != null &&
		 * !ArrayUtils.isEmpty(dataset.getCurrentRowData() .getRows())) {
		 * dataset.setRowSelectIndex(0); } } catch (Exception e) {
		 * Logger.error(e.getMessage(), e); } catch (HrssException e) {
		 * e.alert(); }
		 */
	}

	/**
	 * 根据点击的左侧子集切换右侧页签
	 * 
	 * @param keys
	 */
	public void pluginmainIn(Map<String, Object> keys) {
		TranslatedRow transRow = (TranslatedRow) keys.get("inTabId");
		String tabId = (String) transRow.getValue("param");
		ViewContext viewContext = AppLifeCycleContext.current().getWindowContext().getCurrentViewContext();
		WindowContext windowContext = AppLifeCycleContext.current().getWindowContext();
		UIMeta uiMeta = (UIMeta) viewContext.getUIMeta();
		UITabComp tabComp = (UITabComp) uiMeta.findChildById(PsninfoConsts.TABLAYOUT_MAIN_ID);
		List<UILayoutPanel> itemList = tabComp.getPanelList();
		String beOpeatedPsndocPk = hrsmsUtil.getBoOperatePsndocPK();
		try {
			for (int i = 0; i < itemList.size(); i++) {
				UITabItem tab = (UITabItem) itemList.get(i);
				if (!tab.getId().equals(tabId)) {
					tab.setVisible(false);
					continue;
				}
				String datasetId = "";
				String componentId = null;
				if (tab.getElement() instanceof UIControl) {
					componentId = (String) tab.getElement().getAttribute("id");
					windowContext.addAppAttribute(PsninfoConsts.CURR_COMP_ID, componentId);
					WebComponent comp = viewContext.getView().getViewComponents().getComponent(componentId);
					if (comp != null) {
						if (comp instanceof FormComp) {
							datasetId = ((FormComp) comp).getDataset();
							// 解决切换页签，界面混乱添加
							((FormComp) comp).setFocus(true);
						} else if (comp instanceof GridComp) {
							WebComponent compForm = viewContext.getView().getViewComponents()
									.getComponent("psnjobForm");
							if (compForm == null) {
								compForm = viewContext.getView().getViewComponents().getComponent("psninfoForm");
							}
							if (compForm != null) {
								((FormComp) compForm).setFocus(false);
							}
							datasetId = ((GridComp) comp).getDataset();
							LfwCacheManager.getSessionCache().put(beOpeatedPsndocPk, new ArrayList<SuperVO>());
						}
					}
				} else {
					datasetId = PsninfoConsts.INFOSET_CODE_RELATION;
					WebComponent compForm = viewContext.getView().getViewComponents().getComponent("psnjobForm");
					if (compForm == null) {
						compForm = viewContext.getView().getViewComponents().getComponent("psninfoForm");
					}
					if (compForm != null) {
						((FormComp) compForm).setFocus(false);
					}
				}
				// // 动态给【上移】【下移】【(上移、下移的)保存】菜单添加提交规则
				// addSubmitRuleForMenuItem(datasetId, menubar);

				// 设置当前页面可见
				tab.setVisible(true);
				windowContext.addAppAttribute(PsninfoConsts.CURR_DATASET, datasetId);

				// 查询当前数据集的数据
				SuperVO[] superVOs = queryVOs(beOpeatedPsndocPk, datasetId);

				// 把数据进行序列化
				ShopPsnInfoUtil.pluginDSLoad(datasetId, superVOs);

				// 设置当前选中的页签
				tabComp.setCurrentItem(Integer.toString(i));
				break;
			}
			// 设置页面组件状态
			ShopPsnInfoUtil.SetCompState(getCurrDatasetId());
		} catch (LfwBusinessException e) {
			new HrssException(e).alert();
		}
	}

	private SuperVO[] queryVOs(String pk_psndoc, String dataset) throws LfwBusinessException {
		AppLifeCycleContext.current().getApplicationContext().getClientSession().setAttribute("dataset", dataset);
		try {
			PsndocAggVO aggVO = ServiceLocator.lookup(IPsndocQryService.class).queryPsndocVOByPk(pk_psndoc);
			if (dataset.equals(PsninfoConsts.INFOSET_CODE_PSNDOC)
					|| dataset.equals(PsninfoConsts.INFOSET_CODE_PSNJOB_CURR)) {
				boolean isNeedAudit = ShopPsnInfoUtil.isNeedAudit(dataset);
				if (isNeedAudit) {
					PsninfoUtil.querySetVOs(pk_psndoc);
				}
			}
			if (dataset.equals(PsninfoConsts.INFOSET_CODE_PSNDOC)) {
				return new SuperVO[] { aggVO.getParentVO() };
			}
			if (dataset.equals(PsninfoConsts.INFOSET_CODE_PSNJOB_CURR)) {
				return new SuperVO[] { aggVO.getParentVO().getPsnJobVO() };
			}
			if (dataset.equals(PsninfoConsts.INFOSET_CODE_PARTTIME)) {
				// 当由部门人员信息节点进入我的档案节点，并且该工作记录不是主职时
				SessionBean session = SessionUtil.getSessionBean();
				String ismainjob = (String) session.getExtendAttributeValue("ismainjob");
				if (!StringUtils.isEmpty(ismainjob) && (ismainjob.equals("N") || (ismainjob.equals("n")))) {
					return PsninfoUtil.queryParttime(pk_psndoc);
				}

			}
			if (dataset.equals(PsninfoConsts.INFOSET_CODE_RELATION)) {
				return PsninfoUtil.queryRelationVOs(pk_psndoc);
			}
			return ShopPsnInfoUtil.querySubSet(dataset);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		} catch (HrssException e) {
			new HrssException(e).alert();
		}
		return null;
	}

	/**
	 * 查询待提交记录
	 * 
	 * @param session
	 * @return
	 * @throws BusinessException
	 * @throws HrssException
	 */
	private HrssSetalterVO getNoSubmitHrssSetalterVO(String pk_psndoc) throws BusinessException, HrssException {
		HrssSetalterVO alterVO = null;
		boolean isNeedAudit = ShopPsnInfoUtil.isNeedAudit(getCurrDatasetId());
		if (isNeedAudit) {
			ISetalterService service = ServiceLocator.lookup(ISetalterService.class);
			String pk_infoset = PsninfoUtil.getInfosetPKByCode(getCurrDatasetId());
			alterVO = service.queryNoSubmitHrssSetalterVO(pk_psndoc, pk_infoset);
		}
		return alterVO;
	}

	public void selValueChange(TextEvent textEvent) {
		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		Dataset ds = widget.getViewModels().getDataset(PsninfoConsts.INFOSET_CODE_RELATION);
		String pk_relation = textEvent.getSource().getValue();
		String BeOperatedPk_psndoc = hrsmsUtil.getBoOperatePsndocPK();
		SuperVO[] vos = PsninfoUtil.getRelation(pk_relation, BeOperatedPk_psndoc);
		new SuperVO2DatasetSerializer().serialize(vos, ds, Row.STATE_NORMAL);
	}

	/**
	 * 查看详细操作(js事件)
	 * 
	 * @param mouseEvent
	 */
	public void showDetail(ScriptEvent scriptEvent) {
		LfwView widget = AppLifeCycleContext.current().getWindowContext().getViewContext(HrssConsts.PAGE_MAIN_WIDGET)
				.getView();
		String dataset = getCurrDatasetId();
		ApplicationContext applicationContext = AppLifeCycleContext.current().getApplicationContext();
		applicationContext.addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, HrssConsts.POPVIEW_OPERATE_VIEW);
		Dataset ds = widget.getViewModels().getDataset(dataset);
		Dataset2SuperVOSerializer<SuperVO> ser = new Dataset2SuperVOSerializer<SuperVO>();
		SuperVO superVO = ser.serialize(ds, ds.getSelectedRow())[0];
		if (ds.getVoMeta() != null && CtrtVO.class.getName().equals(ds.getVoMeta())) {
			// 表明当前数据集是合同信息，需要做特殊处理，需要把业务类型放在session中
			SessionUtil.setAttribute(CtrtVO.CONTTYPE, ((CtrtVO) superVO).getConttype());
		}
		String primaryKey = AppLifeCycleContext.current().getParameter("primaryKey");
		String rowIndex = AppLifeCycleContext.current().getParameter("rowIndex");
		applicationContext.addAppAttribute("primaryKey", primaryKey);
		applicationContext.addAppAttribute("rowIndex", rowIndex);
		applicationContext.addAppAttribute(PsninfoConsts.SUPERVO_DETAIL, superVO);
		DialogSize size = PsninfoUtil.getDatasetWidth(dataset);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(PsninfoConsts.CURR_DATASET, ds.getVoMeta());
		paramMap.put("nodecode", PsninfoConsts.PSNINFO_NODECODE);
		String title = NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0006")/*
																								 * @
																								 * res
																								 * "详细信息"
																								 */;
		CommonUtil.showWindowDialog("ShopPsnInfoDetail", title, String.valueOf(size.getWidth()),
				String.valueOf(size.getHeight()), paramMap, ApplicationContext.TYPE_DIALOG, false, false);
	}

	/**
	 * 【上移】菜单对应方法
	 * 
	 * @param mouseEvent
	 */
	public void removeUp(MouseEvent<MenuItem> mouseEvent) {
		// 判断当前数据集是否存在未生效的数据待提交/待审核记录
		boolean flag = isUnOperativeRecord();
		if (flag) {
			CommonUtil.showMessageDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res",
					"0c_hi-res0027")/*
									 * @res "存在未生效的数据，请先提交记录或者等待HR人员审核已提交的记录"
									 */);
			return;
		}

		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		Dataset ds = widget.getViewModels().getDataset(getCurrDatasetId());
		if (ds.getSelectedIndex() == null) {
			return;
		}
		int index = ds.getSelectedIndex();

		// 此为第一行数据，不可进行上移操作！
		if (index <= 0) {
			return;
		}
		CmdInvoker.invoke(new LineUpCmd(ds.getId(), null));
	}

	/**
	 * 判断当前数据集是否存在未生效的数据
	 */
	@SuppressWarnings("unchecked")
	private boolean isUnOperativeRecord() {
		try {
			String pk_infoset;
			pk_infoset = PsninfoUtil.getInfosetPKByCode(getCurrDatasetId());
			// 查询待提交/待审核/审核不通过且用户未确认记录
			HashMap<String, HrssSetalterVO> setsVOs = new HashMap<String, HrssSetalterVO>();
			if (AppLifeCycleContext.current().getWindowContext().getAppAttribute(PsninfoConsts.SET_ALTER_MAP) != null) {
				setsVOs = (HashMap<String, HrssSetalterVO>) AppLifeCycleContext.current().getWindowContext()
						.getAppAttribute(PsninfoConsts.SET_ALTER_MAP);
			}
			HrssSetalterVO alterVO = setsVOs.get(pk_infoset);
			if (alterVO != null) {
				return true;
			}
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			e.alert();
		}
		return false;
	}

	/**
	 * 【下移】菜单对应方法
	 * 
	 * @param mouseEvent
	 */
	public void removeDown(MouseEvent<MenuItem> mouseEvent) {
		// 判断当前数据集是否存在未生效的数据待提交/待审核记录
		boolean flag = isUnOperativeRecord();
		if (flag) {
			CommonUtil.showMessageDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res",
					"0c_hi-res0027")/*
									 * @res "存在未生效的数据，请先提交记录或者等待HR人员审核已提交的记录"
									 */);
			return;
		}

		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		Dataset ds = widget.getViewModels().getDataset(getCurrDatasetId());
		if (ds.getSelectedIndex() == null) {
			return;
		}
		int index = ds.getSelectedIndex();
		// 在这里判断下标，是因为子表默认选中-1，如果没有数据，需要根据Index来判断
		if (index < 0) {
			return;
		}
		int maxIndex = ds.getCurrentRowData().getRows().length;
		// 此为最后一行数据，不可进行下移操作！
		if (index == maxIndex - 1) {
			return;
		}
		CmdInvoker.invoke(new LineDownCmd(ds.getId(), null));
	}

	/**
	 * 【列表的保存】菜单对应方法
	 * 
	 * @param mouseEvent
	 */
	public void removeSave(MouseEvent<MenuItem> mouseEvent) {
		// 判断当前数据集是否存在未生效的数据待提交/待审核记录
		boolean flag = isUnOperativeRecord();
		if (flag) {
			CommonUtil.showMessageDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res",
					"0c_hi-res0027")/*
									 * @res "存在未生效的数据，请先提交记录或者等待HR人员审核已提交的记录"
									 */);
			return;
		}

		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		Dataset ds = widget.getViewModels().getDataset(getCurrDatasetId());
		Row[] row = ds.getAllRow();
		if (row == null || row.length < 1) {
			return;
		}
		// 为每行数据的recordnum赋值
		for (int i = 0; i < row.length; i++) {
			if (ds.nameToIndex("recordnum") != -1)
				row[i].setValue(ds.nameToIndex("recordnum"), i);
		}

		// 把表格中的数据转换成SuperVO
		Dataset2SuperVOSerializer<SuperVO> ser = new Dataset2SuperVOSerializer<SuperVO>();
		SuperVO[] superVOs = ser.serialize(ds);

		try {
			// 调用接口保存数据
			ServiceLocator.lookup(IVOPersistence.class).updateVOArray(superVOs);
			CommonUtil.showShortMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pe-res",
					"0c_pe-res0093")/* @res "保存成功！" */);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			e.alert();
		}
	}

	/**
	 * 通过对比找出变化的字段
	 * 
	 * @param currVO
	 * @param changeFiledList
	 * @param oldVO
	 */
	private void findChangeFiledList(SuperVO vo, ArrayList<String> changeFiledList, SuperVO oldVO) {
		String[] attrs = vo.getAttributeNames();
		for (String attr : attrs) {
			if ("ts".equals(attr) || PsndocVO.PHOTO.equals(attr) || PsndocVO.PREVIEWPHOTO.equals(attr)
					|| PsndocVO.CREATIONTIME.equals(attr) || PsndocVO.MODIFIEDTIME.equals(attr)) {
				continue;
			}
			Object objValue = vo.getAttributeValue(attr);
			Object oldObjValue = oldVO.getAttributeValue(attr);
			if ((objValue == null || StringUtils.isEmpty(objValue.toString()))
					&& (oldObjValue == null || StringUtils.isEmpty(oldObjValue.toString()))) {
				continue;
			}
			if (!((objValue == null && oldObjValue == null) || (objValue != null && oldObjValue != null && objValue
					.equals(oldObjValue)))) {
				changeFiledList.add(attr);
				continue;
			}

			if ((objValue != null && objValue.equals(oldObjValue))
					|| (oldObjValue != null && oldObjValue.equals(objValue))) {
				continue;
			}
			changeFiledList.add(attr);
		}
	}

	public void onAfterPageChange(DatasetEvent datasetEvent) {

	}

	public void onDataLoad_hi_psnjob(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		CmdInvoker.invoke(new UifDatasetLoadCmd(ds.getId()));
	}

	public void onDataLoad_hi_psndoc_family(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		ds.setEnabled(true);
		CmdInvoker.invoke(new UifDatasetLoadCmd(ds.getId()));
	}

	// add
	private AggRMPsndocVO queryAggRMPsndocVO(String pk_psndoc) {
		try {
			AggRMPsndocVO aggRMPsndocVO = null;

			if (PsninfoUtil.isDeptPsnNode()) {
				aggRMPsndocVO = ServiceLocator.lookup(IRMPsndocQueryService.class).queryByPK(pk_psndoc);
				return aggRMPsndocVO;
			} else {
				PsndocAggVO psndocAggVO = ServiceLocator.lookup(IPsndocQryService.class).queryPsndocVOByPk(pk_psndoc);
				RMPsndocVO rmPsnVO = new RMPsndocVO();
				rmPsnVO.setName(psndocAggVO.getParentVO().getName());
				rmPsnVO.setName2(psndocAggVO.getParentVO().getName2());
				rmPsnVO.setName3(psndocAggVO.getParentVO().getName3());
				rmPsnVO.setName4(psndocAggVO.getParentVO().getName4());
				rmPsnVO.setName5(psndocAggVO.getParentVO().getName5());
				rmPsnVO.setName6(psndocAggVO.getParentVO().getName6());
				rmPsnVO.setIdtype(psndocAggVO.getParentVO().getIdtype());
				rmPsnVO.setId(psndocAggVO.getParentVO().getId());
				aggRMPsndocVO = ServiceLocator.lookup(IRMPsndocQueryService.class).queryPsndocByUniqueRule(rmPsnVO);
			}
			if (aggRMPsndocVO != null) {
				return aggRMPsndocVO;
			}
			PsndocAggVO aggVO = ServiceLocator.lookup(IPsndocQryService.class).queryPsndocVOByPk(
					SessionUtil.getPk_psndoc());
			PsndocVO psndocVO = aggVO.getParentVO();
			PsndocAggVO psndocAggVO = ServiceLocator.lookup(IPsndocQryService.class).queryPsndocByNameID(
					psndocVO.getMultiLangName(), psndocVO.getIdtype(), psndocVO.getId());
			if (psndocAggVO == null) {
				CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0168"),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_rm-res", "0c_rm-res0069")/**
				 * @res
				 *      请到业务系统维护身份证件子集信息！
				 */
				);
			}
			aggRMPsndocVO = ServiceLocator.lookup(IRMPsndocQueryService.class).castHIPsndocToRM(
					SessionUtil.getLoginContext(), psndocAggVO)[0];
			setDefaultValue(psndocAggVO.getParentVO(), aggRMPsndocVO);
			return aggRMPsndocVO;
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			new HrssException(e).alert();
		}
		return null;
	}

	private void setDefaultValue(PsndocVO psndocVO, AggRMPsndocVO aggRMPsndocVO) {
		RMPsndocVO rmPsndocVO = aggRMPsndocVO.getPsndocVO();
		if (rmPsndocVO == null) {
			rmPsndocVO = new RMPsndocVO();
			rmPsndocVO.setName(psndocVO.getName());
			rmPsndocVO.setName2(psndocVO.getName2());
			rmPsndocVO.setName3(psndocVO.getName3());
			rmPsndocVO.setName4(psndocVO.getName4());
			rmPsndocVO.setName5(psndocVO.getName5());
			rmPsndocVO.setName6(psndocVO.getName6());
			rmPsndocVO.setBirthdate(psndocVO.getBirthdate());
			rmPsndocVO.setSex(psndocVO.getSex());
			rmPsndocVO.setUsedname(psndocVO.getUsedname());
		}
		rmPsndocVO.setPk_hipsndoc(psndocVO.getPk_psndoc());
		rmPsndocVO.setPk_group(psndocVO.getPk_group());
		rmPsndocVO.setPk_org(psndocVO.getPk_org());
		rmPsndocVO.setIdtype(psndocVO.getIdtype());
		rmPsndocVO.setId(psndocVO.getId());
		rmPsndocVO.setEdu(psndocVO.getEdu());
		rmPsndocVO.setPk_degree(psndocVO.getPk_degree());
		rmPsndocVO.setProf(psndocVO.getProf());
		rmPsndocVO.setApplytype(RMApplyTypeEnum.INAPPLY.toIntValue());
		rmPsndocVO.setIsacceptreplace(UFBoolean.TRUE);
		rmPsndocVO.setEnablestate(IPubEnumConst.ENABLESTATE_ENABLE); // 启用状态
		aggRMPsndocVO.setParentVO(rmPsndocVO);
		CircularlyAccessibleValueObject[] childVOs = aggRMPsndocVO.getAllChildrenVO();
		for (CircularlyAccessibleValueObject childVO : childVOs) {
			childVO.setAttributeValue(RMPsndocVO.PK_GROUP, psndocVO.getPk_group());
			childVO.setAttributeValue(RMPsndocVO.PK_ORG, psndocVO.getPk_org());
		}
	}

	private LfwView getView() {
		return AppLifeCycleContext.current().getViewContext().getView();
	}

	
}
