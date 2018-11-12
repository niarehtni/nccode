package nc.bs.hrsms.hi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import nc.bs.dao.DAOException;
import nc.bs.hrss.hi.psninfo.AlterationParse;
import nc.bs.hrss.hi.psninfo.PsninfoConsts;
import nc.bs.hrss.hi.psninfo.PsninfoUtil;
import nc.bs.hrss.pub.Logger;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.itf.hi.IPsndocQryService;
import nc.uap.cpb.persist.dao.PtBaseDAO;
import nc.uap.lfw.core.bm.ButtonStateManager;
import nc.uap.lfw.core.common.EditorTypeConst;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.comp.GridColumn;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.ImageComp;
import nc.uap.lfw.core.comp.LabelComp;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.WindowContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.exception.LfwBusinessException;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.uap.lfw.jsp.uimeta.UIFlowvPanel;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.ui.format.NCFormater;
import nc.vo.bd.address.AddressFormatVO;
import nc.vo.bd.address.AddressVO;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.WorkVO;
import nc.vo.hr.psnclrule.PsnclinfosetVO;
import nc.vo.hr.psnclrule.PsnclruleVO;
import nc.vo.hrss.hi.psninfo.AlterationVO;
import nc.vo.hrss.hi.psninfo.FieldInfo;
import nc.vo.hrss.hi.psninfo.RecordIndex;
import nc.vo.hrss.hi.setalter.HrssSetalterVO;
import nc.vo.hrss.hi.setalter.SetConsts;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.om.pub.SuperVOHelper;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.format.FormatResult;
import nc.vo.pub.format.exception.FormatException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

/**
 * 
 * 仿hrss 模块的psnINfoUtil，但又有差别 不大
 * 
 * 
 * @author changleia
 * 
 */
public class ShopPsnInfoUtil {
	
	/**
	 * 查询子集数据
	 * 
	 * @param dataset
	 * @return
	 */
	public static SuperVO[] querySubSet(String dataset) {
		try {
			Dataset ds = AppLifeCycleContext.current().getViewContext().getView().getViewModels().getDataset(dataset);
			String BeOperatedPk_psndoc = hrsmsUtil.getBoOperatePsndocPK();
			IPsndocQryService service = ServiceLocator.lookup(IPsndocQryService.class);
			PsndocAggVO aggVO = service.queryPsndocVOByPk(BeOperatedPk_psndoc);
			String pk_psnorg = aggVO.getParentVO().getPsnOrgVO().getPk_psnorg();
			SuperVO[] dbVOs = null;// 存放查询结果
			Class<?> proClass = Class.forName(ds.getVoMeta());
			String pkName = null;
			SuperVO supVO = null;
			String defaultTableName = null;
			if (proClass != null) {
				supVO = (SuperVO) proClass.newInstance();
				if (supVO instanceof PartTimeVO) {
					defaultTableName = PartTimeVO.getDefaultTableName();
				} else {
					defaultTableName = supVO.getTableName();
				}
				pkName = supVO.getPKFieldName();
				String order = null;
				if (PsnOrgVO.getDefaultTableName().equals(defaultTableName)) {
					order = PsnOrgVO.BEGINDATE;
				} else {
					order = WorkVO.RECORDNUM;
				}
				if (WorkVO.getDefaultTableName().equals(defaultTableName))
					order += "," + WorkVO.BEGINDATE;
				Object[] objvos = service.querySubVO(proClass, PsninfoUtil.getWhereStr(defaultTableName, BeOperatedPk_psndoc, pk_psnorg),
						order);
				if (!ArrayUtils.isEmpty(objvos)) {
					dbVOs = SuperVOHelper.convertObjectArray(objvos);
				}
			}
			boolean isNeedAudit = isNeedAudit(dataset);
			// 部门人员模块调用此页面或不需要审核时直接返回
			if (!isNeedAudit) {
				return dbVOs;
			}
			HashMap<String, HrssSetalterVO> setMap = PsninfoUtil.querySetVOs(BeOperatedPk_psndoc);
			String pk_infoset = PsninfoUtil.getInfosetPKByCode(defaultTableName);
			HrssSetalterVO vo = setMap.get(pk_infoset);
			if (vo == null) {
				return dbVOs;
			}
			// 存在修改记录时需要把未变化的记录与变化的记录组装在一起显示
			AlterationVO alterVO = AlterationParse.parseXML(vo.getAlter_context());
			List<RecordIndex> uncList = alterVO.getUncList();
			List<RecordIndex> newList = alterVO.getNewList();
			List<RecordIndex> updList = alterVO.getUpdList();
			int len = uncList.size() + newList.size() + updList.size();
			SuperVO[] updAfterVOs = new SuperVO[len];
			for (RecordIndex record : uncList) {
				String pk = PsninfoUtil.getPk(pkName, record);
				for (SuperVO dbVO : dbVOs) {
					if (pk.equals(dbVO.getPrimaryKey())) {
						updAfterVOs[record.getIndex()] = dbVO;
						break;
					}
				}
			}
			buildUpdAfterVOs(supVO, newList, updAfterVOs);
			buildUpdAfterVOs(supVO, updList, updAfterVOs);
			dbVOs = updAfterVOs;
			return dbVOs;
		} catch (HrssException e1) {
			Logger.error(e1.getMessage(), e1);
		} catch (BusinessException e2) {
			Logger.error(e2.getMessage(), e2);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 把变更记录信息转换成VO对象
	 * 
	 * @param supVO
	 * @param newList
	 * @param updAfterVOs
	 */
	private static void buildUpdAfterVOs(SuperVO supVO, List<RecordIndex> newList, SuperVO[] updAfterVOs) {
		for (RecordIndex record : newList) {
			int index = record.getIndex();
			SuperVO newVO = (SuperVO) supVO.clone();
			List<FieldInfo> fields = record.getFields();
			for (FieldInfo field : fields) {
				try {
					Object value = field.getValue();
					if (PsninfoConsts.NULL_STR.equals(field.getValue())) {
						value = null;
					}
					newVO.setAttributeValue(field.getId(), value);
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
			}
			updAfterVOs[index] = newVO;
		}
	}
	
	private static ArrayList<String> queryNeedCheckSets() {
		// 需要审核的数据集的表名
		ArrayList<String> datasetId = new ArrayList<String>();
		PsnJobVO jobVO = null;
		try {
			PsndocAggVO aggVO = null;
			aggVO = ServiceLocator.lookup(IPsndocQryService.class).queryPsndocVOByPk(PsninfoUtil.getPsndocPK());
			jobVO = aggVO.getParentVO().getPsnJobVO();
			String pk_hrorg = jobVO.getPk_hrorg();
			String pk_psncl = jobVO.getPk_psncl();
			PsnclruleVO psnclVO = PsninfoUtil.getPsnclRule(pk_psncl, pk_hrorg);
			if (psnclVO != null) {
				PsnclinfosetVO[] clsetVOs = psnclVO.getInfosets();
				if (!ArrayUtils.isEmpty(clsetVOs)) {
					for (PsnclinfosetVO clsetVO : clsetVOs) {
						String table = null;
						String metadata = clsetVO.getMetadata();
						if (metadata.indexOf(".") == -1 || metadata.indexOf(".") == metadata.lastIndexOf(".")) {
							table = metadata.split("\\.")[1];
							if (clsetVO.getCheckFlag() != null && clsetVO.getCheckFlag().toString().equals("Y")) {
								datasetId.add(table);
							}
						} else {
							table = clsetVO.getMetadata().substring(metadata.indexOf(".") + 1,
									metadata.lastIndexOf("."));
							if (clsetVO.getCheckFlag() != null && clsetVO.getCheckFlag().toString().equals("Y")) {
								datasetId.add(table);
							}
						}

					}
				}
			}
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			e.alert();
		}

		return datasetId;
	}
	
	/**
	 * 根据人员类别和数据集id获取该人员类别下当前数据集的有关设置
	 * 
	 * @param infoset
	 * @return
	 * @throws BusinessException
	 * @throws HrssException
	 */
	private static ArrayList<PsnclinfosetVO> getCurPsnclinfosetVOs(String infoset) throws BusinessException,
			HrssException {
		ArrayList<PsnclinfosetVO> listInfosetVO = new ArrayList<PsnclinfosetVO>();
		if (PsninfoConsts.INFOSET_CODE_RELATION.equals(infoset)) {
			return listInfosetVO;
		}
		SessionBean session = SessionUtil.getSessionBean();
		PsnJobVO jobVO = null;
			// 是否是主职人员的标志，N或n表示是兼职人员
			String ismainjob = (String) session.getExtendAttributeValue("ismainjob");
			String pk_psnjob = (String) session.getExtendAttributeValue("pk_psnjob");

			// 如果是从部门人员信息节点进来的，并且是兼职人员，它的人员类别规则是取兼职所在的组织的人员类别规则
			if (!StringUtils.isEmpty(ismainjob) && (ismainjob.equals("N") || (ismainjob.equals("n")))) {
				jobVO = ServiceLocator.lookup(IPsndocQryService.class).queryPsninfoByPks(new String[] { pk_psnjob })
						.get(0);

			} else {
				PsndocAggVO aggVO = ServiceLocator.lookup(IPsndocQryService.class).queryPsndocVOByPk( hrsmsUtil.getBoOperatePsndocPK());
				jobVO = aggVO.getParentVO().getPsnJobVO();
			}

		String pk_hrorg = jobVO.getPk_hrorg();
		String pk_psncl = jobVO.getPk_psncl();
		PsnclruleVO psnclVO = PsninfoUtil.getPsnclRule(pk_psncl, pk_hrorg);
		if (psnclVO != null) {
			PsnclinfosetVO[] clsetVOs = psnclVO.getInfosets();
			// 取到和当前数据集有关的设置
			if (!ArrayUtils.isEmpty(clsetVOs)) {
				for (PsnclinfosetVO clsetVO : clsetVOs) {
					if (clsetVO.getMetadata().contains(infoset)) {
						listInfosetVO.add(clsetVO);
					}
				}
			}

		}
		return listInfosetVO;
	}

	/**
	 * 设置页面组件状态
	 * 
	 * @param pageMeta
	 */
	@SuppressWarnings("unchecked")
	public static void SetCompState(String infoset) {
		WindowContext windowContext = AppLifeCycleContext.current().getWindowContext();
		String componentId = (String) windowContext.getAppAttribute(PsninfoConsts.CURR_COMP_ID);
		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		WebComponent comp = widget.getViewComponents().getComponent(componentId);
		try {
			String pk_infoset = PsninfoUtil.getInfosetPKByCode(infoset);
			HrssSetalterVO alterVO = null;
			// 查询待提交/待审核/审核不通过且用户未确认记录
			HashMap<String, HrssSetalterVO> setsVOs = new HashMap<String, HrssSetalterVO>();
			if (windowContext.getAppAttribute(PsninfoConsts.SET_ALTER_MAP) != null) {
				setsVOs = (HashMap<String, HrssSetalterVO>) windowContext.getAppAttribute(PsninfoConsts.SET_ALTER_MAP);
			}
			alterVO = setsVOs.get(pk_infoset);
			// 设置菜单可见性
			ButtonStateManager.updateButtons();

			// 设置需审核子集的提示信息,============这是与原psninfoutil 的差别
			setAlterInfo(setsVOs);

			// 设置变更信息项颜色
			setInfoItemColor(alterVO, comp);
			// 按人员类别控制员工信息相关子集的信息项
			PsninfoUtil.setPsnclrule(infoset, comp);
		} catch (BusinessException e) {
			Logger.error(e);
		} catch (HrssException e) {
			Logger.error(e.getMessage(), e);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 设置变更信息项颜色
	 * 
	 * @throws Exception
	 */
	private static void setInfoItemColor(HrssSetalterVO alterVO, WebComponent comp) throws Exception {

		// 店长自助，去掉对经理的限制
		/*
		 * if (PsninfoUtil.isDeptPsnNode()) return;
		 */
		if (!(comp instanceof FormComp))
			return;
		FormComp form = (FormComp) comp;
		List<FormElement> list = form.getElementList();
		for (FormElement elem : list) {
			// 不替换用户自己设置的颜色 add by zhangqian 2015-06-30
			if (elem.isVisible() && null == elem.getLabelColor()) {
				// #666666为默认颜色
				elem.setLabelColor("#666666");
			}
		}
		if (alterVO == null) {
			return;
		}

		List<FieldInfo> fields = PsninfoUtil.getUpdFieldsByHrssSetalterVO(alterVO);
		for (FieldInfo field : fields) {
			try {
				FormElement elem = form.getElementById(field.getId());
				if (!elem.isVisible()) {
					elem = form.getElementById(field.getId() + PsninfoUtil.REF_SUFFIX);
					if (elem == null)
						continue;
					if (!(EditorTypeConst.REFERENCE.equals(elem.getEditorType()))) // 处理参照时的情况
						continue;
				}
				if (elem != null) {
					elem.setLabelColor("#FF0000"); // 设置修改字段为红色
				}
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 设置需审核子集的提示信息
	 * 
	 * @throws HrssException
	 * @throws BusinessException
	 */
	private static void setAlterInfo(HashMap<String, HrssSetalterVO> setsVOs) throws HrssException, BusinessException {
		StringBuffer buf = new StringBuffer("");
		Collection<HrssSetalterVO> collection = setsVOs.values();
		for (HrssSetalterVO alerVO : collection) {
			int data_status = alerVO.getData_status();
			String pk_infoset = alerVO.getPk_infoset();
			String infoset_code = PsninfoUtil.getInfosetCodeByPK(pk_infoset);
			if (PsninfoConsts.INFOSET_CODE_PSNJOB.equals(infoset_code)) {
				infoset_code = PsninfoConsts.INFOSET_CODE_PSNJOB_CURR;
			}
			String infoset_name = PsninfoUtil.getInfosetNameByPK(pk_infoset);
			boolean isNeedAudit = isNeedAudit(infoset_code);
			if (!isNeedAudit) {

			} else if (SetConsts.NOSUBMIT == data_status) {
				buf.append("[" + infoset_name
						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0008")/*
																											 * @
																											 * res
																											 * "]修改并未生效，请提交审核   "
																											 */);
			} else if (SetConsts.NOAUDIT == data_status) {
				buf.append("[" + infoset_name
						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0009")/*
																											 * @
																											 * res
																											 * "]修改尚未生效  "
																											 */);
			} else if (SetConsts.AUDITNOPASS == data_status) {
				buf.append("[" + infoset_name
						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0026")/*
																											 * @
																											 * res
																											 * "]审核未通过！  "
																											 */);
			}
		}

		// ============这是与原psninfoutil 的差别,将下面的代码注释掉，
		// 店长自助主要给店长用，员工基本不使用，所以不用是否是部门
		/*
		 * if (PsninfoUtil.isDeptPsnNode()) { buf = new StringBuffer(""); }
		 */
		LabelComp label = (LabelComp) AppLifeCycleContext.current().getViewContext().getView().getViewComponents()
				.getComponent("editlabel");
		if (label != null) {
			label.setText(buf.toString());
		}
		boolean noMsgFlag = true; // 没有提示信息标志
		if (StringUtils.isNotEmpty(buf.toString()))
			noMsgFlag = false;
		ImageComp img = (ImageComp) AppLifeCycleContext.current().getViewContext().getView().getViewComponents()
				.getComponent("msg_image");
		img.setVisible(noMsgFlag ? false : true);
		UIMeta um = AppLifeCycleContext.current().getViewContext().getUIMeta();
		String menuHeight = "16"; // 菜单高度
		boolean msgVisible = false; // 隐藏消息布局
		String msgHeight = "0"; // 消息布局高度
		boolean needLine = false; // 需要横线标志
		if (PsninfoConsts.PSNDOC_DS_ID.equals(PsninfoUtil.getCurrDataset())
				|| PsninfoConsts.INFOSET_CODE_PSNJOB_CURR.equals(PsninfoUtil.getCurrDataset())) {
			if (noMsgFlag) {
				menuHeight = "36";
				msgVisible = true;
				msgHeight = "36";
				needLine = false;
			} else {
				menuHeight = "36";
				msgVisible = true;
				msgHeight = "36";
				needLine = false;
			}
		} else {
			if (noMsgFlag) {
				menuHeight = "38";
				msgVisible = false;
				needLine = true;
			} else {
				menuHeight = "36";
				msgVisible = true;
				msgHeight = "36";
				needLine = true;
			}
		}
		((UIFlowvPanel) um.findChildById("panelv05126")).setHeight(menuHeight);
		((UIFlowvPanel) um.findChildById("panelv15126")).setVisible(msgVisible);
		((UIFlowvPanel) um.findChildById("panelv15126")).setHeight(msgHeight);
		((UIFlowvPanel) um.findChildById("panelv25126")).setTopBorder(needLine ? "#" : "");
	}

	public static void pluginDSLoad(String infoset_code, SuperVO[] superVOs) {
		String BeOperatedPk_psndoc = hrsmsUtil.getBoOperatePsndocPK();
		LfwView widget = AppLifeCycleContext.current().getWindowContext().getViewContext("main").getView();
		Dataset ds = widget.getViewModels().getDataset(infoset_code);
		ds.setCurrentKey(Dataset.MASTER_KEY);
		try {
			boolean isNeedAudit = isNeedAudit(infoset_code);
			if (isNeedAudit && PsninfoConsts.INFOSET_CODE_PSNDOC.equals(infoset_code)
					|| PsninfoConsts.INFOSET_CODE_PSNJOB_CURR.equals(infoset_code)) {
				String pk_infoset = PsninfoUtil.getInfosetPKByCode(infoset_code);
				WindowContext windowContext = AppLifeCycleContext.current().getWindowContext();
				@SuppressWarnings("unchecked")
				HashMap<String, HrssSetalterVO> setsVOs = (HashMap<String, HrssSetalterVO>) windowContext
						.getAppAttribute(PsninfoConsts.SET_ALTER_MAP);
				if (setsVOs != null && setsVOs.get(pk_infoset) != null) {
					List<FieldInfo> fields = PsninfoUtil.getUpdFieldsByHrssSetalterVO(setsVOs.get(pk_infoset));
					for (FieldInfo field : fields) {
						if ("null".equals(field.getValue())) {
							superVOs[0].setAttributeValue(field.getId(), null);
						} else {
							superVOs[0].setAttributeValue(field.getId(), field.getValue());
						}
					}
				}
			}

			new SuperVO2DatasetSerializer().serialize(superVOs == null ? new SuperVO[] {} : superVOs, ds,
					Row.STATE_NORMAL);
			ds.setRowSelectIndex(0);
			if (PsninfoConsts.INFOSET_CODE_PSNDOC.equals(infoset_code)) {
				ds.setValue("patha",
						"pt/psnImage/download?pk_psndoc=" + BeOperatedPk_psndoc + "&random=" + Math.random());
			}
			// 63增加，人员信息中的家庭地址引用的地址簿参照发生变化
			if (ds.getId().equals("bd_psndoc") && superVOs[0] != null) {
				String pk_addr = ((PsndocVO) superVOs[0]).getAddr();
				if (pk_addr != null) {
					AddressVO addr = null;
					FormatResult result = null;
					try {
						addr = (AddressVO) new PtBaseDAO().retrieveByPK(AddressVO.class, pk_addr);
						if (addr != null)
							result = NCFormater.formatAddress(new AddressFormatVO(addr));
					} catch (DAOException e) {
						LfwLogger.error(e);
					} catch (FormatException e) {
						LfwLogger.error(e);
					}
					if (result != null) {
						ds.getSelectedRow().setValue(ds.nameToIndex("addr_pk_address"), result.getValue());
					}
				}
			}

			if (PsninfoUtil.isDeptPsnNode()) {
				ds.setEnabled(false);
			}
			// 63增加，main片段只有bd_psndoc才能编辑(在人员类别设置中设置了bd_psndoc可编辑的前提下，否则一个数据集都不能编辑)
			else {
				// 取到人员类别设置中可编辑的数据集
				@SuppressWarnings("unchecked")
				ArrayList<String> editDatasetlist = (ArrayList<String>) AppUtil.getAppAttr("editDatasetlist");
				if (ds.getId().equals("bd_psndoc") && CollectionUtils.isNotEmpty(editDatasetlist)
						&& editDatasetlist.contains("bd_psndoc"))
					ds.setEnabled(true);
				else
					ds.setEnabled(false);
			}
			ds.setEnabled(true);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		} catch (HrssException e) {
			Logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 查询信息集是否需审核 2011-3-23 下午03:29:41
	 * 
	 * @param infoset_code
	 * @return
	 * @throws HrssException
	 * @throws LfwBusinessException
	 */
	public static boolean isNeedAudit(String infoset_code) throws HrssException, BusinessException {
		if (PsninfoUtil.isBusinessSet(infoset_code))
			return false;
		if (PsninfoConsts.INFOSET_CODE_PSNJOB_CURR.equals(infoset_code))
			infoset_code = PsninfoConsts.INFOSET_CODE_PSNJOB;
		WindowContext windowContext = AppLifeCycleContext.current().getWindowContext();
		if (windowContext == null)
			return true;

		@SuppressWarnings("unchecked")
		ArrayList<String> setsVOs = (ArrayList<String>) windowContext.getAppAttribute(PsninfoConsts.HRSSSETS);
		if (CollectionUtils.isEmpty(setsVOs)) {
			setsVOs = queryNeedCheckSets();
			windowContext.addAppAttribute(PsninfoConsts.HRSSSETS, setsVOs);
		}
		for (int i = 0; i < setsVOs.size(); i++) {
			if (infoset_code.equals(setsVOs.get(i))) {
				return true;
			}
		}
		return false;

	}
	
	/**
	 * 按人员类别控制员工信息相关子集的信息项 信息项是否可见取自助的模板设置与业务系统中的人员类别设置的交集
	 * 
	 * @param session
	 * @param comp
	 * @throws BusinessException
	 * @throws HrssException
	 */
	public static void setPsnclrule(String infoset, WebComponent comp) throws BusinessException, HrssException {
		// 获取和当前数据集有关的设置
		ArrayList<PsnclinfosetVO> listInfosetVO = getCurPsnclinfosetVOs(infoset);
		// 子集项是否可编辑
		boolean editAble = false;
		if (!CollectionUtils.isEmpty(listInfosetVO)) {
			for (PsnclinfosetVO clsetVO : listInfosetVO) {
				String metadata = clsetVO.getMetadata();
				if (metadata.indexOf(".") == -1 || metadata.indexOf(".") == metadata.lastIndexOf(".")) {
					editAble = clsetVO.getCanEditFlag().booleanValue();
					continue;
				}
				String field = clsetVO.getMetadata().substring(metadata.lastIndexOf(".") + 1);
				if (comp instanceof FormComp) {
					FormElement ele = null;
					// 处理参照情况,普通参照得id是在pk后面加_name
					ele = ((FormComp) comp).getElementById(field + PsninfoUtil.REF_SUFFIX);
					if (ele == null) {
						// 家庭地址比较特殊 addr_pk_address（addr）
						ele = ((FormComp) comp).getElementById(field + "_pk_address");
					}

					if (ele == null) {
						ele = ((FormComp) comp).getElementById(field);
					}
					if (ele == null) {
						continue;
					}
					if (ele.isVisible()) { // 是否显示
						if (clsetVO.getShowFlag() != null) {
							ele.setVisible(clsetVO.getShowFlag().booleanValue());
						} else {
							ele.setVisible(false);
						}

					}
					if ("bd_psndoc".equals(infoset)
							&& ("prof_name".equals(ele.getId()) || "edu_name".equals(ele.getId())
									|| "pk_degree_name".equals(ele.getId()) || "titletechpost_name".equals(ele.getId()))) {
						// 人员基本信息中学历，学位，职业资格,专业技术职务这四项不受业务系统可编辑的控制
						continue;
					}

					if ("pk_conttext_vmodelname".equals(ele.getId())) {
						// 合同子集中的合同模板不受业务系统可编辑的控制
						continue;
					}

					if (metadata.contains("bd_psndoc") && clsetVO.getCanEditFlag() != null) {
						// 人员信息是否可编辑
						ele.setEnabled(clsetVO.getCanEditFlag().booleanValue());
					}

					if (!metadata.contains("bd_psndoc")) {
						// 人员子集项是否可编辑
						ele.setEnabled(editAble);
					}

				} else if (comp instanceof GridComp) {
					GridColumn ele = null;
					// 处理参照情况,参照id是在pk后面加_name
					ele = (GridColumn) (((GridComp) comp).getColumnByField(field + PsninfoUtil.REF_SUFFIX));
					if (ele == null) {
						ele = (GridColumn) ((GridComp) comp).getColumnByField(field);
					}
					if (ele.isVisible()) { // 是否显示
						if (clsetVO.getShowFlag() != null) {
							ele.setVisible(clsetVO.getShowFlag().booleanValue());
						} else {
							ele.setVisible(false);
						}

					}
				}
			}
		}

	}


}