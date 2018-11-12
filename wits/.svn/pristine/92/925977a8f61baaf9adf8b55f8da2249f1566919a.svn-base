package nc.impl.wa.item;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.bd.cache.CacheProxy;
import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.businessevent.IEventType;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.impl.wa.common.WaCommonImpl;
import nc.itf.bd.defdoc.IDefdocQryService;
import nc.itf.bd.defdoc.IDefdoclistQryService;
import nc.itf.hr.managescope.ManagescopeFacade;
import nc.itf.hr.wa.IItemManageService;
import nc.itf.hr.wa.IItemQueryService;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.defdoc.DefdoclistVO;
import nc.vo.bd.psn.PsnClVO;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.hr.datatable.FreefldVO;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.hr.managescope.ManagescopeBusiregionEnum;
import nc.vo.hrp.budgetitem.BudgetSubVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.formulaset.ContentVO;
import nc.vo.pub.formulaset.ItemVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.uif2.LoginContext;
import nc.vo.util.BDPKLockUtil;
import nc.vo.util.VisibleUtil;
import nc.vo.wa.adjust.WaAdjustParaTool;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.WaItemDefdocCompseVO;
import nc.vo.wa.item.WaItemDspVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wabm.util.WaCacheUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author: wh
 * @date: 2009-11-23 下午03:36:06
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:ssx
 * @修改日期: 2017-05-16
 * @version V6.5
 */
public class ItemServiceImpl implements IItemManageService, IItemQueryService,
		Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8567838096014878236L;

	private final String DOC_NAME = "Waitem";

	private SimpleDocServiceTemplate serviceTemplate;

	private final WaCommonImpl waCommonImpl = new WaCommonImpl();

	private SimpleDocServiceTemplate getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new SimpleDocServiceTemplate(DOC_NAME);
		}
		return serviceTemplate;
	}

	@Override
	public WaItemVO[] queryWaItemVOsByCondition(LoginContext context,
			String condition) throws BusinessException {
		WaItemVO[] vos = getServiceTemplate().queryByCondition(context,
				WaItemVO.class, condition, "  idisplayseq ,code ");
		return fillTotalItems(vos);
	}

	/**
	 * 根据条查询,并且关联用户设置的显示顺序
	 * 
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public WaItemVO[] queryByConditionWithDispOrder(LoginContext context,
			String condition) throws BusinessException {

		String visibleWhere = null;

		try {
			visibleWhere = VisibleUtil.getVisibleCondition(context,
					WaItemVO.class);
		} catch (Exception e) {
			Logger.error(e);
		}

		if (!StringUtil.isEmptyWithTrim(condition)) {
			if (visibleWhere == null) {
				visibleWhere = condition;
			} else {
				visibleWhere += "and(" + condition + ")";
			}
		}

		/**
		 * 首先查询出有顺序的
		 */
		StringBuilder sbd1 = new StringBuilder();
		sbd1.append(" select wa_item.* ");
		sbd1.append(" from wa_item inner  join wa_itemdsp  ");
		sbd1.append(" on wa_item.pk_wa_item = wa_itemdsp.pk_wa_item and wa_itemdsp.pk_group = ? and wa_itemdsp.pk_org = ? ");
		sbd1.append(" and wa_itemdsp.pk_user = ?  ");
		sbd1.append(" where "
				+ visibleWhere.replaceAll("pk_org", "wa_item.pk_org"));
		sbd1.append(" order by  wa_itemdsp.displayseq ");

		/**
		 * 查询出没有顺序的
		 */

		StringBuilder sbd2 = new StringBuilder();
		sbd2.append("  select wa_item.* from wa_item where not exists ( ");
		sbd2.append(" 	   SELECT  ");
		sbd2.append(" 	           wa_itemdsp.pk_wa_item ");
		sbd2.append(" 	       FROM ");
		sbd2.append("           wa_itemdsp  ");
		sbd2.append("            where wa_item.pk_wa_item = wa_itemdsp.pk_wa_item ");
		sbd2.append(" 	           AND wa_itemdsp.pk_group = ? ");
		sbd2.append(" 	           AND wa_itemdsp.pk_org = ? ");
		sbd2.append(" 	           AND wa_itemdsp.pk_user =? ");
		sbd2.append(" 	   ) ");
		sbd2.append(" and  "
				+ visibleWhere.replaceAll("pk_org", "wa_item.pk_org"));
		sbd2.append(" order by  wa_item.idisplayseq,wa_item.code ");

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(context.getPk_group());
		parameter.addParam(context.getPk_org());
		parameter.addParam(context.getPk_loginUser());
		WaItemVO[] vos1 = new BaseDAOManager().executeQueryVOs(sbd1.toString(),
				parameter, WaItemVO.class);
		WaItemVO[] vos2 = null;
		if (vos1 == null && context.getNodeType() == NODE_TYPE.ORG_NODE) {
			parameter = new SQLParameter();
			parameter.addParam(context.getPk_group());
			parameter.addParam(context.getPk_group());
			parameter.addParam(context.getPk_loginUser());
			vos1 = new BaseDAOManager().executeQueryVOs(sbd1.toString(),
					parameter, WaItemVO.class);
			vos2 = new BaseDAOManager().executeQueryVOs(sbd2.toString(),
					parameter, WaItemVO.class);
		} else {
			parameter = new SQLParameter();
			parameter.addParam(context.getPk_group());
			parameter.addParam(context.getPk_org());
			parameter.addParam(context.getPk_loginUser());
			vos2 = new BaseDAOManager().executeQueryVOs(sbd2.toString(),
					parameter, WaItemVO.class);
		}

		if (ArrayUtils.isEmpty(vos1)) {
			vos1 = new WaItemVO[0];
		}

		if (ArrayUtils.isEmpty(vos2)) {
			vos2 = new WaItemVO[0];
		}

		WaItemVO[] result = new WaItemVO[vos1.length + vos2.length];
		System.arraycopy(vos1, 0, result, 0, vos1.length);
		System.arraycopy(vos2, 0, result, vos1.length, vos2.length);
		fillTotalItems(result);

		return result;

	}

	@Override
	public WaItemVO[] querySystemItems() throws BusinessException {
		String where = " defaultflag = 'Y' ";
		return fillTotalItems(getServiceTemplate().queryByCondition(
				WaItemVO.class, where));
	}

	@Override
	public WaItemVO queryWaItemVOByPk(String pk) throws BusinessException {
		return fillTotalItem(getServiceTemplate().queryByPk(WaItemVO.class, pk));
	}

	@Override
	public WaItemVO[] queryWaItemVOByPks(String[] pks) throws BusinessException {

		InSQLCreator inSQLCreator = new InSQLCreator();

		try {
			String condition = " pk_wa_item in(" + inSQLCreator.getInSQL(pks)
					+ ") ";
			Collection<WaItemVO> items = new BaseDAO().retrieveByClause(
					WaItemVO.class, condition, "pk_wa_item");
			return fillTotalItems(items.toArray(new WaItemVO[items.size()]));

		} finally {
			inSQLCreator.clear();
		}

	}

	@Override
	public WaItemVO[] queryWaItemVOForWadoc(LoginContext context)
			throws BusinessException {
		StringBuffer sbSql = new StringBuffer();
		// 没有选择组织 就不会显示项目信息
		if (context.getPk_org() == null) {
			return null;
		} else {
			// guoqt HRWA007
			// 定调资管理的业务处理组织如果是人事组织，但人员不归该人力资源组织管理，所以查询定调资显示项目的时候需要加过滤
			ManagescopeBusiregionEnum busiregionEnum = ManagescopeBusiregionEnum.salary;
			Integer allowed = WaAdjustParaTool.getWaOrg(context.getPk_group());
			if (allowed.equals(1)) {
				busiregionEnum = ManagescopeBusiregionEnum.psndoc;
			}
			sbSql.append("select pk_wa_item from wa_item  where ");
			sbSql.append(VisibleUtil.getVisibleCondition(context,
					WaItemVO.class));
			sbSql.append(" and ( isinhi = 'Y' or isinhi = 'y')");
			sbSql.append(" and exists( select 1 from hi_psndoc_wadoc where wa_item.pk_wa_item=hi_psndoc_wadoc.pk_wa_item");
			sbSql.append(" and hi_psndoc_wadoc.pk_psnjob in "
					+ ManagescopeFacade.queryPsnjobPksSQLByHrorgAndBusiregion(
							context.getPk_org(), busiregionEnum, true) + ")");
		}
		BaseDAO dao = new BaseDAO();
		String[] pks = null;
		Object itemPks = null;
		itemPks = dao.executeQuery(sbSql.toString(), new ResultSetProcessor() {
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				List<String> items = new ArrayList<String>();
				while (rs.next()) {
					items.add(rs.getString(1));
				}
				return items.toArray(new String[items.size()]);
			}
		});
		if (itemPks == null) {
			return null;
		} else {
			pks = (String[]) itemPks;
		}
		return fillTotalItems(getServiceTemplate().queryByPks(WaItemVO.class,
				pks));
	}

	@Override
	public void deleteWaItemVO(WaItemVO vo) throws BusinessException {
		// 先调用校验器
		DefaultValidationService vService = new DefaultValidationService();
		vService.addValidator(new ItemDeleteValidator());
		vService.validate(vo);

		BaseDAO dao = new BaseDAO();
		// 首先删除总额项目

		StringBuilder deleteSQL = new StringBuilder();
		deleteSQL
				.append(" delete from hrp_budget_item_b where  pk_itemid = '"
						+ vo.getPk_wa_item()
						+ "' and (pk_budget_item in(select pk_budget_item from HRP_BUDGET_ITEM where PK_GROUP = '"
						+ PubEnv.getPk_group() + "'))");
		dao.executeUpdate(deleteSQL.toString());

		// 删除已分配的权限
		dao.executeUpdate(" delete from wa_itempower where pk_wa_item = '"
				+ vo.getPk_wa_item() + "'");

		// 删除公共薪资项目
		getServiceTemplate().delete(vo);

		EventDispatcher.fireEvent(new BusinessEvent(
				HRWACommonConstants.WAITEMSOURCEID,
				IEventType.TYPE_DELETE_AFTER, vo));

		CacheProxy.fireDataDeleted(vo.getTableName(), vo.getPk_wa_item());
		CacheProxy.fireDataDeletedByWhereClause(BudgetSubVO.TABLE_NAME,
				BudgetSubVO.PK_BUDGET_ITEM_B,
				"pk_wa_item = '" + vo.getPk_wa_item() + "'");

	}

	@Override
	public WaItemDefdocCompseVO getWaItemDefdocCompseVO(String pk_defdoclist,
			LoginContext context, String wheresql) throws BusinessException {

		DefdoclistVO vo = NCLocator.getInstance()
				.lookup(IDefdoclistQryService.class)
				.queryDefdoclistVOByPk(pk_defdoclist);

		DefdocVO[] vos = NCLocator
				.getInstance()
				.lookup(IDefdocQryService.class)
				.queryDefdocVOsByDoclistPkAndWhereSql(pk_defdoclist,
						context.getPk_org(), context.getPk_group(), wheresql);

		WaItemDefdocCompseVO comvo = new WaItemDefdocCompseVO();
		comvo.setDefdoclistVO(vo);
		comvo.setDefdocVOs(vos);

		return comvo;

	}

	@Override
	public WaItemVO insertWaItemVO(WaItemVO vo) throws BusinessException {
		TypeEnumVO type = vo.getTypeEnumVO();
		Integer itemId = requestItemId(vo);
		if (StringUtils.isBlank(type.getPrefix())) {
			throw new BusinessException(ResHelper.getString("60130glbitem",
					"060130glbitem0034")/* @res "数据类型不能为空" */);
		}
		if (itemId == null) {
			// 对项目进行扩展
			// NCLocator.getInstance().lookup(clazz)
			throw new BusinessException(ResHelper.getString("60130glbitem",
					"060130glbitem0035")/* @res "该类型的项目已达到最多数不能再添加" */);
		}

		vo.setItemkey(type.getPrefix() + itemId);
		if (vo.getCode().equals("unknown")) {
			vo.setCode(vo.getItemkey());
		}
		String totalitems = vo.getTotalitem();
		vo.setTotalitem(null);

		vo = getServiceTemplate().insert(vo);

		// 向总额表中插入项目
		if (!StringUtils.isBlank(totalitems)) {
			String[] totalitem = totalitems.split(",");
			BudgetSubVO[] subvos = new BudgetSubVO[totalitem.length];
			for (int index = 0; index < totalitem.length; index++) {
				subvos[index] = new BudgetSubVO();
				subvos[index].setPk_itemid(vo.getPk_wa_item());
				subvos[index].setPk_budget_item(totalitem[index].trim());

			}

			BaseDAO dao = new BaseDAO();
			dao.insertVOArray(subvos);
			WaCacheUtils.synCache(BudgetSubVO.TABLE_NAME);

		}
		// totalitem ,字表 。
		vo.setTotalitem(totalitems);

		EventDispatcher.fireEvent(new BusinessEvent(
				HRWACommonConstants.WAITEMSOURCEID,
				IEventType.TYPE_INSERT_AFTER, vo));

		WaCacheUtils.synCache(vo.getTableName());
		return vo;
	}

	@Override
	public boolean itemNumIsEnough(WaItemVO vo) throws BusinessException {

		Integer itemId = requestItemId(vo);

		if (itemId == null) {
			return false;
		}

		return true;
	}

	private WaItemVO[] fillTotalItems(WaItemVO[] itemVOs)
			throws BusinessException {
		if (ArrayUtils.isEmpty(itemVOs)) {
			return itemVOs;
		}

		return batchFillTotalItem(itemVOs);
	}

	private WaItemVO[] batchFillTotalItem(WaItemVO[] itemVOs)
			throws BusinessException {
		ArrayList<String> pkList = new ArrayList();
		HashMap<String, ArrayList<String>> map = new HashMap();
		BaseDAOManager bsdao = new BaseDAOManager();
		for (WaItemVO itemVO : itemVOs) {
			pkList.add(itemVO.getPk_wa_item());
			map.put(itemVO.getPk_wa_item(), new ArrayList());
		}
		String inSql = new InSQLCreator().getInSQL((String[]) pkList
				.toArray(new String[0]));

		StringBuffer sbSql = new StringBuffer();
		sbSql.append("  select ");
		sbSql.append(" hrp_budget_item_b.pk_budget_item, hrp_budget_item_b.pk_itemid ");
		sbSql.append("  from ");
		sbSql.append(" hrp_budget_item ");
		sbSql.append(" inner join hrp_budget_item_b ");
		sbSql.append(" on hrp_budget_item_b.pk_budget_item = hrp_budget_item.pk_budget_item ");
		sbSql.append(" where ");
		sbSql.append(" hrp_budget_item_b.pk_itemid in (" + inSql + ") and ");
		sbSql.append(" hrp_budget_item.pk_group = '" + PubEnv.getPk_group()
				+ "'");

		BudgetSubVO[] vos = (BudgetSubVO[]) new BaseDAOManager()
				.executeQueryVOs(sbSql.toString(), BudgetSubVO.class);
		if (!ArrayUtils.isEmpty(vos)) {
			for (BudgetSubVO vo : vos) {
				((ArrayList) map.get(vo.getPk_itemid())).add(vo
						.getPk_budget_item());
			}
		}

		for (WaItemVO itemVO : itemVOs) {
			ArrayList<String> budgetpks = (ArrayList) map.get(itemVO
					.getPk_wa_item());
			StringBuffer pks = new StringBuffer();
			for (String pk : budgetpks) {
				if (pks.length() > 0) {
					pks.append(",");
				}
				pks.append(pk);
			}

			itemVO.setTotalitem(pks.toString());

			// ssx added for Taiwan Localization
			// ssx added ishealthinsexsum_30 on 2017-06-22
			sbSql = new StringBuffer();
			sbSql.append("select isnhiitem_30, ishealthinsexsum_30 from twhr_waitem_30 where pk_wa_item='"
					+ itemVO.getPk_wa_item() + "'");
			List<Map> values = (List<Map>) new BaseDAO().executeQuery(
					sbSql.toString(), new MapListProcessor());
			itemVO.setIsnhiitem_30(UFBoolean.valueOf((String) values.get(0)
					.get("isnhiitem_30")));
			itemVO.setIshealthinsexsum_30(UFBoolean.valueOf((String) values
					.get(0).get("ishealthinsexsum_30")));
			// end
		}

		return itemVOs;
	}

	private WaItemVO fillTotalItem(WaItemVO itemVO) throws BusinessException {
		if (itemVO == null) {
			return itemVO;
		}
		// 查询总额项目
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("  select ");
		sbSql.append(" 	hrp_budget_item_b.* ");
		sbSql.append("  from ");
		sbSql.append(" 	hrp_budget_item ");
		sbSql.append(" 	inner join hrp_budget_item_b ");
		sbSql.append(" 	on hrp_budget_item_b.pk_budget_item = hrp_budget_item.pk_budget_item ");
		sbSql.append("  where ");
		sbSql.append(" 	hrp_budget_item_b.pk_itemid = '"
				+ itemVO.getPk_wa_item() + "' and ");
		sbSql.append(" 	hrp_budget_item.pk_group = '" + PubEnv.getPk_group()
				+ "'");
		BudgetSubVO[] vos = new BaseDAOManager().executeQueryVOs(
				sbSql.toString(), BudgetSubVO.class);
		StringBuilder sbd = new StringBuilder();
		if (!ArrayUtils.isEmpty(vos)) {
			for (int i = 0; i < vos.length; i++) {
				sbd.append(vos[i].getPk_budget_item());
				if (i != vos.length - 1) {
					sbd.append(",");
				}
			}
		}

		itemVO.setTotalitem(sbd.toString());
		// ssx added for Taiwan Localization
		// ssx added ishealthinsexsum_30 on 2017-06-22
		sbSql = new StringBuffer();
		sbSql.append("select isnhiitem_30, ishealthinsexsum_30 from twhr_waitem_30 where pk_wa_item='"
				+ itemVO.getPk_wa_item() + "'");
		List<Map> values = (List<Map>) new BaseDAO().executeQuery(
				sbSql.toString(), new MapListProcessor());
		itemVO.setIsnhiitem_30(UFBoolean.valueOf((String) values.get(0).get(
				"isnhiitem_30")));
		itemVO.setIshealthinsexsum_30(UFBoolean.valueOf((String) values.get(0)
				.get("ishealthinsexsum_30")));
		// end

		return itemVO;
	}

	@Override
	public WaItemVO updateWaItemVO(WaItemVO vo) throws BusinessException {
		String totalitems = vo.getTotalitem();
		vo.setTotalitem(null);
		vo = getServiceTemplate().update(vo, true);

		// 首先删除总额项目
		StringBuilder deleteSQL = new StringBuilder();
		deleteSQL
				.append(" delete from hrp_budget_item_b where  pk_itemid = '"
						+ vo.getPk_wa_item()
						+ "' and (pk_budget_item in(select pk_budget_item from HRP_BUDGET_ITEM where PK_GROUP = '"
						+ PubEnv.getPk_group() + "'))");
		BaseDAO dao = new BaseDAO();
		// 然后插入
		// 向总额表中插入项目
		if (!StringUtils.isBlank(totalitems)) {
			String[] totalitem = totalitems.split(",");
			BudgetSubVO[] subvos = new BudgetSubVO[totalitem.length];
			for (int index = 0; index < totalitem.length; index++) {
				subvos[index] = new BudgetSubVO();
				subvos[index].setPk_itemid(vo.getPk_wa_item());
				subvos[index].setPk_budget_item(totalitem[index].trim());

			}

			dao.executeUpdate(deleteSQL.toString());
			dao.insertVOArray(subvos);

			// 同步缓存
			WaCacheUtils.synCache(BudgetSubVO.TABLE_NAME);
		} else {
			dao.executeUpdate(deleteSQL.toString());

			// 同步缓存
			WaCacheUtils.synCache(BudgetSubVO.TABLE_NAME);
		}
		// totalitem ,字表 。
		vo.setTotalitem(totalitems);
		// 同步缓存
		WaCacheUtils.synCache(vo.getTableName());

		return vo;
	}

	@SuppressWarnings("serial")
	private Integer requestItemId(WaItemVO itemvo) throws BusinessException {
		BDPKLockUtil.lockString("requestItemId");
		TypeEnumVO type = itemvo.getTypeEnumVO();
		// 是集团添加，还是组织添加
		String condition = getRequestItemIdCondition(itemvo.getPk_group(),
				itemvo.getPk_org());

		final int iType = type.value();
		/**
		 * max 默认值取1 不能取0
		 */
		String sql = "select isnull(max(cast(substring(itemkey,3,len(itemkey)) as integer))+1,1) maxid from wa_item "
				+ " where iitemtype = ?   and "
				+ condition
				+ " having  isnull(max(cast(substring(itemkey,3,len(itemkey)) as integer))+1,1) <= (select lmaxno from "
				+ FreefldVO.getDefaultTableName()
				+ " where iproducttype = 0 and ifieldtype = ?)";
		BaseDAO dao = new BaseDAO();
		SQLParameter par = new SQLParameter();
		par.addParam(iType);
		par.addParam(iType);

		try {
			return (Integer) dao.executeQuery(sql, par,
					new ResultSetProcessor() {

						@Override
						public Object handleResultSet(ResultSet rs)
								throws SQLException {
							if (!rs.next()) {
								return null;
							}
							// 取已被占用的最大数
							Integer no = rs.getInt(1);
							// 数值型项目，预留0-14个最为系统项目
							if (iType == TypeEnumVO.FLOATTYPE.value()
									&& no != null && no.intValue() < 15) {
								no = 15;
							}
							return no;
						}

					});
		} catch (DAOException e) {
			Logger.error(e);
			throw new BusinessRuntimeException(ResHelper.getString(
					"60130glbitem", "060130glbitem0036")/* @res "请求新的公共项目字段失败" */);
		}
	}

	/**
	 * 要增加的项目是集团项目还是组织项目
	 * 
	 * @param vo
	 * @return
	 */
	private boolean IsGroupItem(String pk_group, String pk_org) {
		return pk_group.equals(pk_org);
	}

	/**
	 * （1）集团内部不能重叠 ，集团之间可以重叠 （公共项目-集团可以确定itemid ( pk_group = '集团pk' or pk_org =
	 * 'GLOBLE00000000000000' ） （2） 公司不能与上属集团重复，同一个集团内的各个公司之间可以重叠（pk_org =
	 * '公司pk' or pk_org = '集团pk' or pk_org = 'GLOBLE00000000000000'）
	 */

	private String getRequestItemIdCondition(String pk_group, String pk_org) {
		if (IsGroupItem(pk_group, pk_org)) {
			return "(" + WaItemVO.PK_GROUP + "='" + pk_group + "' or  "
					+ WaItemVO.PK_ORG + " = 'GLOBLE00000000000000' )";
		} else {
			return "(" + WaItemVO.PK_ORG + "='" + pk_org + "' or "
					+ WaItemVO.PK_ORG + "='" + pk_group + "' or  "
					+ WaItemVO.PK_ORG + " = 'GLOBLE00000000000000' )";
		}

	}

	public void initItemByPsncl(LoginContext context, ArrayList<ItemVO> itemList)
			throws BusinessException {
		PsnClVO[] psncls = waCommonImpl.getPsnclVOS(context);
		ContentVO[] contentVOs = new ContentVO[psncls.length];
		for (int i = 0; i < contentVOs.length; i++) {
			contentVOs[i] = buildByPsnclVO(psncls[i]);
		}
		ItemVO psnclItem = new ItemVO(NCLangResOnserver.getInstance()
				.getStrByID("common", "UC000-0000140")/*
													 * @res "人员类别"
													 */);
		psnclItem.setContent(contentVOs);
		itemList.add(psnclItem);

	}

	public void initItemByDept(LoginContext context, ArrayList<ItemVO> itemList)
			throws BusinessException {
		DeptVO[] depts = waCommonImpl.getDeptVOS(context);
		ContentVO[] contentVOs = new ContentVO[depts.length];
		for (int i = 0; i < depts.length; i++) {
			contentVOs[i] = buildByDeptVO(depts[i]);
		}
		ItemVO deptItem = new ItemVO(NCLangResOnserver.getInstance()
				.getStrByID("common", "UC000-0001975")/*
													 * @res "所在部门"
													 */);
		deptItem.setContent(contentVOs);
		itemList.add(deptItem);
	}

	// private ContentVO buildByWaItemVO(WaItemVO item){
	// ContentVO content = new ContentVO(item.getName());
	// content.setColNameFlag(true); //
	// content.setTableName("wa_data");
	// content.setDigitFlag(item.getTypeEnumVO() == TypeEnumVO.FLOATTYPE);
	// content.setColName(item.getItemkey());
	// return content;
	// }

	private ContentVO buildByPsnclVO(PsnClVO psncl) {
		ContentVO content = new ContentVO(psncl.getName());
		content.setTrueValue(psncl.getPk_psncl());
		content.setColNameFlag(false);
		content.setTableName("wa_data");
		content.setColName("psnclid");
		content.setDigitFlag(false);
		return content;
	}

	private ContentVO buildByDeptVO(DeptVO dept) {
		ContentVO content = new ContentVO(dept.getName());
		content.setTrueValue(dept.getPk_dept());
		content.setColNameFlag(false);
		content.setTableName("wa_data");
		content.setColName("deptid");
		content.setDigitFlag(false);
		return content;
	}

	@Override
	public WaItemVO[] queryWaItemVOsByGroup(String pk_group,
			String otherCondition) throws BusinessException {
		String where = "pk_group = '"
				+ pk_group
				+ "'"
				+ (otherCondition == null ? "" : " and (" + otherCondition
						+ " )");
		return fillTotalItems(getServiceTemplate().queryByCondition(
				WaItemVO.class, where));
	}

	@Override
	public WaItemVO[] queryWaItemVOsByOrg(String pk_group, String pk_org,
			String otherCondition) throws BusinessException {
		String where = " pk_group = '"
				+ pk_group
				+ "' and pk_org = '"
				+ pk_org
				+ "'"
				+ (otherCondition == null ? "" : " and (" + otherCondition
						+ " )");
		return fillTotalItems(getServiceTemplate().queryByCondition(
				WaItemVO.class, where));
	}

	/**
	 * @author xuanlt on 2010-4-1
	 * @see nc.itf.hr.wa.IItemQueryService#getFormulaInitVO(nc.vo.uif2.LoginContext)
	 */
	@Override
	public ItemVO[] getFormulaInitVO(LoginContext context)
			throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @author xuanlt on 2010-5-13
	 * @see nc.itf.hr.wa.IItemQueryService#querySelfDocPK(java.lang.String)
	 */
	@Override
	public String querySelfDocPK(String code) throws BusinessException {
		BaseDAO baseDao = new BaseDAO();
		Collection<DefdoclistVO> c = baseDao.retrieveByClause(
				DefdoclistVO.class, " code = '" + code + "'");
		for (Iterator iterator = c.iterator(); iterator.hasNext();) {
			DefdoclistVO defdocVO = (DefdoclistVO) iterator.next();
			return defdocVO.getPk_defdoclist();
		}
		throw new BusinessException(ResHelper.getString("60130glbitem",
				"060130glbitem0037")/* @res "没有找到自定义档案：薪资项目分类" */);
	}

	@Override
	public WaItemVO queryByItemkey(String itemkey) throws BusinessException {
		BaseDAO baseDao = new BaseDAO();
		Collection<WaItemVO> c = baseDao.retrieveByClause(WaItemVO.class,
				" ITEMKEY = '" + itemkey + "'");
		Iterator iterator = c.iterator();
		if (iterator.hasNext()) {
			return (WaItemVO) iterator.next();
		}
		return null;
	}

	@Override
	public WaItemVO queryByItemkeyAndPkorg(String pk_org, String itemkey)
			throws BusinessException {
		BaseDAO baseDao = new BaseDAO();
		Collection<WaItemVO> c = baseDao.retrieveByClause(WaItemVO.class,
				" ITEMKEY = '" + itemkey + "' and pk_org = '" + pk_org + "' ");
		Iterator iterator = c.iterator();
		if (iterator.hasNext()) {
			return (WaItemVO) iterator.next();
		}
		return null;
	}

	@Override
	public WaItemVO queryByClassItemkeyAndPkorg(String pk_org, String itemkey)
			throws BusinessException {
		BaseDAO baseDao = new BaseDAO();
		Collection<WaItemVO> c = baseDao.retrieveByClause(WaClassItemVO.class,
				" ITEMKEY = '" + itemkey + "' and pk_org = '" + pk_org + "' ");
		Iterator iterator = c.iterator();
		if (iterator.hasNext()) {
			return (WaItemVO) iterator.next();
		}
		return null;
	}

	public WaItemVO queryByItemkeyAndPkorg(String itemkey)
			throws BusinessException {
		BaseDAO baseDao = new BaseDAO();
		Collection<WaItemVO> c = baseDao.retrieveByClause(WaItemVO.class,
				" ITEMKEY = '" + itemkey + "'");
		Iterator iterator = c.iterator();
		if (iterator.hasNext()) {
			return (WaItemVO) iterator.next();
		}
		return null;
	}

	@Override
	public WaItemVO[] queryByWaClass(String pk_prnt_class, String pk_wa_class)
			throws BusinessException {
		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append(
				"select distinct wa_item.category_id, wa_item.clearflag, wa_item.code, wa_item.creationtime, wa_item.creator, wa_item.defaultflag, "
						+ "wa_item.dr, wa_item.idisplayseq, wa_item.iflddecimal, wa_item.ifldwidth, wa_item.ifromflag, wa_item.iitemtype, wa_item.intotalitem, wa_item.iprivil, "
						+ "wa_item.iproperty, wa_item.isinhi, wa_item.itemkey, wa_item.mid, wa_item.modifiedtime, wa_item.modifier,wa_item.npsnceil, wa_item.npsnfloor, "
						+ "wa_item.nsumceil, wa_item.nsumfloor, wa_item.pk_budget_item, wa_item.pk_group, wa_item.pk_org, wa_item.pk_wa_item, wa_item.psnceilflag, "
						+ "wa_item.psnfloorflag, wa_item.sumceilflag, wa_item.sumfloorflag, wa_item.taxflag, wa_item.totalitem, wa_item.ts, wa_item.vformula, "
						+ "wa_item.vformulastr,wa_classitem.name,wa_classitem.name2,wa_classitem.name3,wa_classitem.name4,wa_classitem.name5,wa_classitem.name6 "
						+ "  from wa_item inner join wa_classitem on wa_item.pk_wa_item=wa_classitem.pk_wa_item")
				.append(" inner join wa_waclass on wa_classitem.pk_wa_class=wa_waclass.pk_wa_class")
				.append(" where wa_waclass.pk_wa_class='")
				.append(pk_wa_class)
				.append("'")
				.append("and wa_classitem.itemkey IN(SELECT wa_classitem.ITEMKEY ")
				.append("   FROM wa_itempower inner join wa_classitem ON wa_classitem.pk_wa_item=wa_itempower.pk_wa_item and wa_classitem.PK_WA_CLASS=wa_itempower.PK_WA_CLASS ")
				.append("  WHERE wa_itempower.pk_wa_class ='")
				.append(pk_prnt_class)
				.append("'")
				.append("    AND ( wa_itempower.pk_subject IN(SELECT pk_role ")
				.append("				       FROM sm_user_role ")
				.append("				      WHERE cuserid = '" + PubEnv.getPk_user()
						+ "'")
				.append("                   ) or wa_itempower.pk_subject = '"
						+ PubEnv.getPk_user() + "')) ");
		return new BaseDAOManager().executeQueryVOs(sqlStr.toString(),
				WaItemVO.class);

	}

	@Override
	public void saveItemDisplayOrder(LoginContext context, WaItemVO[] items)
			throws BusinessException {
		if (!ArrayUtils.isEmpty(items)) {
			// 先删除，用户该组织下的显示顺序
			deleteItemDspVOs(context);
			// 封装显示顺序vo
			WaItemDspVO[] dspVOs = new WaItemDspVO[items.length];
			for (int i = 0; i < items.length; i++) {
				WaItemVO itemVo = items[i];
				WaItemDspVO dspVO = new WaItemDspVO();
				dspVO.setPk_group(context.getPk_group());
				dspVO.setPk_org(context.getPk_org());
				dspVO.setPk_user(context.getPk_loginUser());
				dspVO.setPk_wa_item(itemVo.getPk_wa_item());
				dspVO.setDisplayseq(i);
				dspVOs[i] = dspVO;
			}
			// 保存显示顺序vo
			new BaseDAO().insertVOArray(dspVOs);
		}
	}

	private void deleteItemDspVOs(LoginContext context) throws DAOException {
		String wherestr = " pk_group=? and pk_org=? and pk_user=?";
		SQLParameter params = new SQLParameter();
		params.addParam(context.getPk_group());
		params.addParam(context.getPk_org());
		params.addParam(context.getPk_loginUser());
		new BaseDAO().deleteByClause(WaItemDspVO.class, wherestr, params);
	}

	@Override
	public List<WaItemDspVO> queryItemDspVOs(LoginContext context)
			throws BusinessException {
		String condition = " pk_group=? and pk_org=? and pk_user=?";
		SQLParameter params = new SQLParameter();
		params.addParam(context.getPk_group());
		params.addParam(context.getPk_org());
		params.addParam(context.getPk_loginUser());
		return (List<WaItemDspVO>) new BaseDAO().retrieveByClause(
				WaItemDspVO.class, condition, params);
	}

	@Override
	public WaItemVO[] queryWaItemVOsForGrade(LoginContext context)
			throws BusinessException {
		String where = " (pk_org = 'GLOBLE00000000000000'  or pk_org = '"
				+ context.getPk_group() + "' or pk_org = '"
				+ context.getPk_org()
				+ "' ) and ( isinhi = 'Y' or isinhi = 'y' ) "
				+ " and ( isnull ( iprivil , 0 ) = 0 )";
		return getServiceTemplate().queryByCondition(WaItemVO.class, where);
	}

}