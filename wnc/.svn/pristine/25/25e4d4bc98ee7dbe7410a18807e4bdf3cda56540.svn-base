package nc.impl.wa.classitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.impl.wa.category.WaClassDAO;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author: wh
 * @date: 2009-12-14 上午09:27:05
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class ClassitemDAO extends BaseDAOManager {

	/**
	 * @author wh
	 * @date 2009-12-14 上午09:43:19
	 * @param pk_org
	 *            组织
	 * @param pk_wa_class
	 *            薪资类别主键
	 * @param period
	 *            期间
	 * @param year
	 *            年度
	 * @return
	 * @throws DAOException
	 */
	public WaClassItemVO[] queryItemInfoVO(String pk_org, String pk_wa_class, String year, String period)
			throws DAOException {
		return queryItemInfoVO(pk_org, pk_wa_class, year, period, null);
	}

	public WaClassItemVO[] queryItemInfoVO(String pk_org, String pk_wa_class, String year, String period,
			String condition) throws DAOException {
		String sql = getFileds();
		sql = sql + "  from wa_classitem inner join wa_item on wa_classitem.pk_wa_item = wa_item.pk_wa_item "
				+ "  where wa_classitem.pk_wa_class = ? and wa_classitem.pk_org = ? and wa_classitem.cyear = ? "
				+ " and wa_classitem.cperiod = ? ";
		if (!StringUtils.isBlank(condition)) {
			sql += " and (" + condition + ")";
		}
		sql = sql + "  order by  wa_classitem.idisplayseq  ";

		SQLParameter par = new SQLParameter();
		par.addParam(pk_wa_class);
		par.addParam(pk_org);
		par.addParam(year);
		par.addParam(period);
		Collection<WaClassItemVO> c = (Collection<WaClassItemVO>) new BaseDAO().executeQuery(sql, par,
				new BeanListProcessor(WaClassItemVO.class));
		if (c == null)
			return null;
		return c.toArray(new WaClassItemVO[0]);
	}

	public WaClassItemVO[] queryItemWithAccount(String pk_org, String pk_wa_class, String year, String period)
			throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_classitem.itemkey,wa_classitem.bankaccount ");
		sqlBuffer.append("from wa_classitem inner join wa_item on wa_item.pk_wa_item = wa_classitem.pk_wa_item  ");
		sqlBuffer.append("where   isnull(wa_classitem.bankaccount ,0)!=0 and wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append("and wa_classitem.cyear = ? and wa_classitem.cperiod = ? and wa_item.iitemtype  = 0");
		SQLParameter param = new SQLParameter();
		param.addParam(pk_wa_class);
		param.addParam(year);
		param.addParam(period);
		Collection<WaClassItemVO> c = (Collection<WaClassItemVO>) new BaseDAO().executeQuery(sqlBuffer.toString(),
				param, new BeanListProcessor(WaClassItemVO.class));
		if (c == null) {
			return null;
		}
		return c.toArray(new WaClassItemVO[0]);
	}

	public WaClassItemVO[] queryItemsByClassId(String pk_wa_class, String condition) throws DAOException {
		String sql = getFileds();

		sql = sql + "  from wa_classitem inner join wa_item on wa_classitem.pk_wa_item = wa_item.pk_wa_item "
				+ "  where wa_classitem.pk_wa_class = ? ";

		if (!StringUtils.isBlank(condition)) {
			sql += " and (" + condition + ")";
		}
		sql += " order by cyear||cperiod asc ";
		SQLParameter par = new SQLParameter();
		par.addParam(pk_wa_class);

		Collection<WaClassItemVO> c = (Collection<WaClassItemVO>) new BaseDAO().executeQuery(sql, par,
				new BeanListProcessor(WaClassItemVO.class));
		if (c == null)
			return null;
		return c.toArray(new WaClassItemVO[0]);
	}

	public WaClassItemVO[] queryItemsByClassIdForFormular(String pk_wa_class, String condition) throws DAOException {
		// String sql = getFileds();
		String sql = "select distinct  wa_item.clearflag,wa_item.code, wa_item.creationtime, wa_item.creator, wa_item.defaultflag, wa_item.dr, wa_classitem.iflddecimal, wa_item.ifldwidth, wa_item.ifromflag, wa_item.iitemtype, wa_item.intotalitem, wa_item.iprivil, wa_item.iproperty, wa_item.isinhi, wa_item.itemkey, wa_item.mid, wa_item.modifiedtime, wa_item.modifier, "
				+ " wa_item.name , wa_item.name2, wa_item.name3,wa_item.name4,wa_item.name5,wa_item.name6, wa_item.npsnceil, wa_item.npsnfloor, wa_item.nsumceil, wa_item.nsumfloor, wa_item.pk_budget_item, wa_item.pk_group, wa_item.pk_org, wa_item.pk_wa_item, wa_item.psnceilflag, wa_item.psnfloorflag, wa_item.sumceilflag, wa_item.sumfloorflag, wa_item.taxflag, wa_item.totalitem, wa_item.ts, wa_item.vformula, wa_item.vformulastr, wa_item.idisplayseq ";

		sql = sql + "  from wa_classitem inner join wa_item on wa_classitem.pk_wa_item = wa_item.pk_wa_item "
				+ "  where wa_classitem.pk_wa_class = ? ";

		if (!StringUtils.isBlank(condition)) {
			sql += " and (" + condition + ")";
		}
		SQLParameter par = new SQLParameter();
		par.addParam(pk_wa_class);

		Collection<WaClassItemVO> c = (Collection<WaClassItemVO>) new BaseDAO().executeQuery(sql, par,
				new BeanListProcessor(WaClassItemVO.class));
		if (c == null)
			return null;
		return c.toArray(new WaClassItemVO[0]);
	}

	/**
	 * 汇总薪资方案项目 不展现子方案中的项目 要想展现,需要手动添加
	 * 
	 * @param context
	 * @param typePk
	 * @return
	 * @throws BusinessException
	 */
	public WaClassItemVO[] queryByWaItemType(WaLoginContext context, String typePk) throws BusinessException {
		String sql = getFileds();
		StringBuilder sbd = new StringBuilder();
		sbd.append(sql);
		sbd.append(" from  wa_classitem  ");
		sbd.append(" inner join wa_item  on wa_item.pk_wa_item = wa_classitem.pk_wa_item ");
		sbd.append(" where wa_classitem.pk_wa_class = ? and wa_classitem.pk_group = ? and wa_classitem.pk_org = ? and wa_classitem.cyear = ? and wa_classitem.cperiod =? ");// +
		if (!StringUtils.isBlank(typePk)) {
			sbd.append(" and wa_classitem.category_id = ? ");
		}
		// 暂时不使用权限 xuanlt
		// sbd.append(" and wa_item.pk_wa_item in ("+ItemPowerUtil.getItemPower(context)+")");
		sbd.append(" order by wa_classitem.idisplayseq ");
		SQLParameter para = new SQLParameter();
		para.addParam(context.getPk_wa_class());
		para.addParam(context.getPk_group());
		para.addParam(context.getPk_org());
		para.addParam(context.getWaYear());
		para.addParam(context.getWaPeriod());
		if (!StringUtils.isBlank(typePk)) {
			para.addParam(typePk);
		}
		return executeQueryVOs(sbd.toString(), para, WaClassItemVO.class);

	}

	public WaClassItemVO queryWaClassItemVOByPk(String pk_waclassitem) throws DAOException {
		String sql = getFileds();
		StringBuilder sbd = new StringBuilder();
		sbd.append(sql);
		sbd.append(" from  wa_classitem  ");
		sbd.append(" inner join wa_item  on wa_item.pk_wa_item = wa_classitem.pk_wa_item ");
		sbd.append(" where wa_classitem.pk_wa_classitem = ? ");

		SQLParameter para = new SQLParameter();
		para.addParam(pk_waclassitem);
		WaClassItemVO[] vos = executeQueryVOs(sbd.toString(), para, WaClassItemVO.class);
		if (vos == null || vos.length == 0) {
			return null;
		}
		return vos[0];
	}

	/**
	 * 查看集团的子方案是否有已经审核的数据
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public WaClassVO[] subClassHasCheckedData(WaClassVO groupClassvo) throws BusinessException {

		StringBuilder sbd = new StringBuilder();
		sbd.append(" select  wa_waclass.pk_wa_class ,  "
				+ SQLHelper.getMultiLangNameColumn("wa_waclass.name")
				+ " ,wa_waclass.name2 ,wa_waclass.name3 ,wa_waclass.code,wa_waclass.cyear,wa_waclass.cperiod  "
				+ "from wa_data,wa_waclass where wa_data.pk_wa_class = wa_waclass.pk_wa_class and   wa_data.cyear = ? and wa_data.cperiod = ? and (wa_data.pk_wa_class in ( ");
		sbd.append(" select wa_inludeclass.pk_childclass  from wa_inludeclass,wa_assigncls where ");
		sbd.append(" wa_inludeclass.pk_parentclass = wa_assigncls.classid and wa_inludeclass.batch=1 ");
		sbd.append(" and wa_assigncls.pk_sourcecls = ? ");
		sbd.append("  ) or wa_data.pk_wa_class in (select classid from wa_assigncls where pk_sourcecls = ? ))   and wa_data.stopflag = 'N' and wa_data.checkflag = 'Y' ");

		SQLParameter para = new SQLParameter();
		para.addParam(groupClassvo.getCyear());
		para.addParam(groupClassvo.getCperiod());
		para.addParam(groupClassvo.getPk_wa_class());
		para.addParam(groupClassvo.getPk_wa_class());
		return executeQueryVOs(sbd.toString(), para, WaClassVO.class);

	}

	private final WaClassDAO dao = new WaClassDAO();

	public WaClassDAO getWaClassDAO() {
		return dao;
	}

	public WaClassVO[] queryGroupAssignedWaclass(WaClassVO vo) throws DAOException {
		String sql = " select wa_waclass.*  " + "from  wa_waclass,wa_assigncls "
				+ "where wa_waclass.pk_wa_class =  wa_assigncls.classid " + "and  wa_assigncls.pk_sourcecls =? "
				// 20150728 xiejie3
				// 补丁合并，NCdp205382570在薪资发放中，显示设置里面薪资项目有些不是显示内容，begin
				// add by zouj 2015/05/25
				+ "and stopflag = 'N'";
		// end
		SQLParameter para = new SQLParameter();
		para.addParam(vo.getPk_wa_class());

		List<WaClassVO> list = new ArrayList<WaClassVO>();

		WaClassVO[] assignedWaclass = executeQueryVOs(sql, para, WaClassVO.class); // 查询已分配方案

		if (!ArrayUtils.isEmpty(assignedWaclass)) {
			// list = java.util.Arrays.asList(assignedWaclass);
			for (WaClassVO waClassVO : assignedWaclass) {
				list.add(waClassVO);
				StringBuffer sqlB = new StringBuffer();
				sqlB.append(" pk_wa_class in(select wa_inludeclass.pk_childclass ");
				sqlB.append("  from wa_inludeclass  ");
				sqlB.append(" where wa_inludeclass.pk_parentclass ='" + waClassVO.getPk_wa_class() + "' ");
				sqlB.append("   and wa_inludeclass.cyear = '" + waClassVO.getCyear() + "' ");
				sqlB.append("	and wa_inludeclass.cperiod = '" + waClassVO.getCperiod() + "') ");

				WaClassVO[] subclasses = getWaClassDAO().queryWaClassByCondition(sqlB.toString(), UFBoolean.FALSE); // 查询子方案
				if (!ArrayUtils.isEmpty(subclasses)) {
					for (WaClassVO subclass : subclasses) {
						list.add(subclass);
					}
				}

			}
		}
		return list.toArray(new WaClassVO[0]);

	}

	public WaClassItemVO queryClassItemVO(String pk_wa_item, String cyear, String cperiod, String pk_wa_class)
			throws DAOException {
		String sql = " select  *  from wa_classitem where pk_wa_item = ? and cyear = ? and cperiod = ? and pk_wa_class = ? ";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_wa_item);
		para.addParam(cyear);
		para.addParam(cperiod);
		para.addParam(pk_wa_class);

		return executeQueryVO(sql, para, WaClassItemVO.class);

	}

	public WaClassItemVO[] queryByCondition(LoginContext context, String condition) throws BusinessException {
		String sql = getFileds();
		StringBuilder sbd = new StringBuilder();
		sbd.append(sql);
		sbd.append(" from  wa_classitem  ");
		sbd.append(" inner join wa_item  on wa_item.pk_wa_item = wa_classitem.pk_wa_item ");
		sbd.append(" where  wa_classitem.pk_group = ? and wa_classitem.pk_org = ? ");

		if (!StringUtils.isBlank(condition)) {
			sbd.append(" and " + condition);
		}

		// 暂时不使用权限 xuanlt
		// sbd.append(" and wa_item.pk_wa_item in ("+ItemPowerUtil.getItemPower(context)+")");
		sbd.append(" order by wa_classitem.idisplayseq ");

		SQLParameter para = new SQLParameter();
		para.addParam(context.getPk_group());
		para.addParam(context.getPk_org());
		return executeQueryVOs(sbd.toString(), para, WaClassItemVO.class);
	}

	/***************************************************************************
	 * WaClassItemVO sql <br>
	 * Created on 2012-7-5 8:54:09<br>
	 * 
	 * @return
	 * @author daicy
	 ***************************************************************************/
	// 2015-10-27 zhousze 新增wa_classitem.vformulastr begin
	private String getFileds() {
		String sql = "select   wa_item.clearflag,wa_item.code,  wa_item.defaultflag, wa_classitem.iflddecimal, wa_item.ifldwidth, wa_item.iitemtype, wa_item.intotalitem, wa_item.iprivil, wa_item.iproperty, wa_item.isinhi, wa_item.mid, wa_item.pk_budget_item,  wa_item.totalitem,"
				+ " wa_classitem.clearflag,wa_classitem.cperiod,wa_classitem.cyear,wa_classitem.destitemcol,wa_classitem.destitempk,wa_classitem.dr,wa_classitem.icomputeseq,wa_classitem.idisplayseq,wa_classitem.ifromflag,wa_classitem.issysformula,wa_classitem.istransfer,wa_classitem.itemkey, "
				+ "wa_classitem.name ,wa_classitem.name2 ,wa_classitem.name3 ,wa_classitem.name4 ,wa_classitem.name5 ,wa_classitem.name6 ,wa_classitem.npsnceil,wa_classitem.npsnfloor,wa_classitem.nsumceil,wa_classitem.nsumfloor,wa_classitem.pk_group,wa_classitem.pk_org,wa_classitem.pk_wa_class,"
				+ "wa_classitem.pk_wa_classitem,wa_classitem.pk_wa_item,wa_classitem.psnceilflag,wa_classitem.psnfloorflag,wa_classitem.showflag,wa_classitem.sumceilflag,wa_classitem.sumfloorflag,wa_classitem.taxflag,wa_classitem.ts,wa_classitem.vformula,wa_classitem.bankaccount," 
				+ "wa_classitem.inapproveditem,wa_classitem.creator,wa_classitem.creationtime,wa_classitem.modifier,wa_classitem.modifiedtime,wa_classitem.round_type,wa_classitem.category_id,wa_classitem.vformulastr,"
				
				+ "wa_classitem.trano,wa_classitem.debitentry,wa_classitem.debitentry_b,wa_classitem.creditsubject,wa_classitem.credisubject_b";

		return sql;
		// end
	}

	public void updateItemCaculateSeu(WaClassItemVO[] classitemVOs) throws BusinessException {
		PersistenceManager sessionManager = null;
		try {
			StringBuffer sqlB = new StringBuffer();
			sqlB.append("update wa_classitem set wa_classitem.icomputeseq = ? where wa_classitem.pk_wa_classitem = ?"); // 1
			sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			for (WaClassItemVO classitemVO : classitemVOs) {
				SQLParameter parameters = new SQLParameter();
				parameters.addParam(classitemVO.getIcomputeseq());
				parameters.addParam(classitemVO.getPk_wa_classitem());
				session.addBatch(sqlB.toString(), parameters);
			}
			session.executeBatch();
		} catch (DbException e) {
			throw new nc.vo.pub.BusinessException(ResHelper.getString("60130classpower", "060130classpower0165")/*
																												 * @
																												 * res
																												 * "更新项目计算顺序失败"
																												 */);
		} finally {
			if (sessionManager != null) {
				sessionManager.release();
			}
		}

	}

	/**
	 * 查找结转到指定目标项目的薪资项目
	 */
	public WaClassItemVO[] queryTransferClsItem(WaClassItemVO vo) throws DAOException {
		StringBuilder sbd = new StringBuilder();
		sbd.append("select ");
		sbd.append("  " + SQLHelper.getMultiLangNameColumn("wa_classitem.name")
				+ " ,wa_classitem.name2 ,wa_classitem.name3 ,wa_classitem.destitempk,wa_classitem.destitemcol ");
		sbd.append(" from  wa_classitem  ");
		sbd.append(" where  pk_wa_class = ? and cyear = ? and cperiod = ? and destitempk = ? ");
		if (vo.getPk_wa_classitem() != null) {
			sbd.append(" and  wa_classitem.pk_wa_classitem <>? ");
		}

		SQLParameter para = new SQLParameter();
		para.addParam(vo.getPk_wa_class());
		para.addParam(vo.getCyear());
		para.addParam(vo.getCperiod());
		para.addParam(vo.getDestitempk());
		if (vo.getPk_wa_classitem() != null) {
			para.addParam(vo.getPk_wa_classitem());
		}
		return executeQueryVOs(sbd.toString(), para, WaClassItemVO.class);
	}

	/**
	 * 判断一批项目是否存在某个薪资方案中
	 * 
	 * @author zhangg on 2009-7-1
	 * @param waclassvo
	 *            薪资方案
	 * @param 公共薪资项目vo数组
	 * @return
	 * @throws BusinessException
	 */
	public HashMap<String, String> isItemExist(WaClassVO waclassVO, WaClassItemVO[] vos) throws BusinessException {
		HashMap<String, String> map = new HashMap<String, String>();
		InSQLCreator isc = new InSQLCreator();
		String insql = null;
		insql = isc.getInSQL(vos, WaClassItemVO.ITEMKEY);
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("SELECT wa_item.itemkey"); // 1
		sqlB.append("  FROM wa_classitem, wa_item "); // 11
		sqlB.append(" WHERE wa_classitem.pk_wa_class = '" + waclassVO.getPk_wa_class() + "' "); // 12
		sqlB.append("   AND wa_classitem.pk_wa_item = wa_item.pk_wa_item "); // 13
		sqlB.append("   AND wa_classitem.cyear = '" + waclassVO.getCyear() + "' "); // 15
		sqlB.append("   AND wa_classitem.cperiod = '" + waclassVO.getCperiod() + "' "); // 16
		sqlB.append("   AND wa_classitem.dr = 0 "); // 17
		sqlB.append("   AND wa_item.itemkey in (");
		sqlB.append(insql);
		sqlB.append(")"); // 18
		String[] itemkeys = executeQueryVOs(sqlB.toString(), String.class);
		if (itemkeys != null && itemkeys.length > 0) {
			for (String key : itemkeys) {
				map.put(key, key);
			}
		}
		return map;
	}

	/**
	 * 判断一个项目是否存在改类别中
	 * 
	 * @author zhangg on 2009-7-1
	 * @param pk_class
	 * @param year
	 * @param period
	 * @param iitemid
	 * @return
	 * @throws BusinessException
	 */
	public boolean isItemExist(WaClassVO waclassVO, String itemKey) throws BusinessException {
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("SELECT wa_item.itemkey"); // 1
		sqlB.append("  FROM wa_classitem, wa_item "); // 11
		sqlB.append(" WHERE wa_classitem.pk_wa_class = '" + waclassVO.getPk_wa_class() + "' "); // 12
		sqlB.append("   AND wa_classitem.pk_wa_item = wa_item.pk_wa_item "); // 13
		sqlB.append("   AND wa_classitem.cyear = '" + waclassVO.getCyear() + "' "); // 15
		sqlB.append("   AND wa_classitem.cperiod = '" + waclassVO.getCperiod() + "' "); // 16
		sqlB.append("   AND wa_classitem.dr = 0 "); // 17
		sqlB.append("   AND wa_item.itemkey =  '" + itemKey + "'"); // 18

		return new BaseDAOManager().isValueExist(sqlB.toString());
	}

	/***************************************************************************
	 * 判断一个项目是否在统一方案其他期间存在 <br>
	 * Created on 2012-10-23 下午3:35:03<br>
	 * 
	 * @param waclassVO
	 * @param itemKey
	 * @return
	 * @throws BusinessException
	 * @author daicy
	 ***************************************************************************/
	public boolean isItemExistDifPeriod(WaClassItemVO vo) throws BusinessException {
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select 1 from wa_classitem where PK_WA_CLASS=? and ITEMKEY=? and (CYEAR<>? or CPERIOD<>?)"); // 18
		SQLParameter parameter = new SQLParameter();

		parameter.addParam(vo.getPk_wa_class());
		parameter.addParam(vo.getItemkey());
		parameter.addParam(vo.getCyear());
		parameter.addParam(vo.getCperiod());

		return new BaseDAOManager().isValueExist(sqlB.toString(), parameter);
	}

	public boolean isExistInOtherChildClass(String parentClassPK, String childClassPK, WaClassItemVO vo)
			throws BusinessException {
		StringBuffer sqlB = new StringBuffer();
		sqlB.append(" select 1 from wa_inludeclass,wa_classitem ");
		sqlB.append("  where pk_parentclass  = '" + parentClassPK + "'  and wa_classitem.cyear = '" + vo.getCyear()
				+ "' ");
		sqlB.append("  and wa_classitem.cperiod = '" + vo.getCperiod() + "' and wa_classitem.pk_wa_item = '"
				+ vo.getPk_wa_item() + "' ");
		sqlB.append("  and wa_classitem.pk_wa_class =  wa_inludeclass.pk_childclass and wa_classitem.pk_wa_class <> '"
				+ childClassPK + "' ");

		return new BaseDAOManager().isValueExist(sqlB.toString());
	}

	/**
	 * 核查薪资发放项目，被那些自定义公式的项目依赖 自定义公式的有： 来源公式、来源规则表、来源其他数据源（薪资）
	 * 
	 * 是否被薪资分摊引用。
	 * 
	 * @param itemKey
	 * @param pk_wa_class
	 * @param pk_wa_classitem
	 * @param cyear
	 * @param cperiod
	 * @return
	 * @throws BusinessException
	 */
	public boolean checkUsed(String itemKey, String pk_wa_class, String pk_wa_classitem, String cyear, String cperiod)
			throws BusinessException {

		// StringBuffer sqlB = new StringBuffer();
		// /*需要一个辅助类来配合检查“其他数据源薪资是否引用该项目”*/
		// sqlB.append("select name, vformula from wa_classitem where pk_wa_class = ? and ifromflag = 0 and cyear=? and cperiod = ?  and  issysformula = 'N'  ");
		//
		// SQLParameter parameter = new SQLParameter();
		// parameter.addParam(pk_wa_class);
		// parameter.addParam(cyear);
		// parameter.addParam(cperiod);
		//
		// WaClassItemVO[] list = new
		// BaseDAOManager().executeQueryVOs(sqlB.toString(), parameter,
		// WaClassItemVO.class);//
		// ////////////////////////(sqlB.toString(), parameter, new
		// BeanListProcessor(ClassitemVO.class));
		// if (list != null && list.length > 0) {
		// for (WaClassItemVO item : list) {
		// if (item.getVformula() == null || item.getVformula().length() == 0) {
		// continue;
		// }
		//
		// List<String> items = ItemSort.pattern(item.getVformula());
		// if (items.contains(itemKey)) {
		// throw new BusinessException("被薪资项目["+ item.getName()+"]公式引用！");
		// }
		// }
		// }

		// 薪资分摊是否被引用
		String sql = " select 1 from wa_amobase,wa_amoscheme  where wa_amobase.pk_amoscheme  = wa_amoscheme.pk_amoscheme  and wa_amoscheme.classid = '"
				+ pk_wa_class + "' and 	wa_amobase.pk_wa_item = '" + pk_wa_classitem + "'";
		Boolean isUserd = new BaseDAOManager().isValueExist(sql.toString());
		if (isUserd) {
			throw new BusinessException(ResHelper.getString("60130classpower", "060130classpower0166")/*
																									 * @
																									 * res
																									 * "薪资项目被薪资分摊引用！"
																									 */);
		}

		sql = "select 1 from wa_classitem where pk_wa_class = '" + pk_wa_class + "' and destitemcol='" + itemKey + "'";
		isUserd = new BaseDAOManager().isValueExist(sql.toString());
		if (isUserd) {
			throw new BusinessException(ResHelper.getString("60130classpower", "060130classpower0167")/*
																									 * @
																									 * res
																									 * "被薪资项目结转引用！"
																									 */);
		}

		return false;
	}

	public void deleteWaclassItem(WaClassItemVO vo) throws DAOException {
		// 清空该项对应的数据

		// 删除该项目
		String sql = " delete from wa_classitem where pk_wa_class = '" + vo.getPk_wa_class() + "' and cyear = '"
				+ vo.getCyear() + "' and cperiod = '" + vo.getCperiod() + "' and pk_wa_item = '" + vo.getPk_wa_item()
				+ "' ";
		getBaseDao().executeUpdate(sql);
	}

	// public void updateWaclassItem(WaClassItemVO vo,String[] attributeNames)
	// throws DAOException{
	// //清空该项对应的数据
	//
	// //删除该项目
	// StringBuilder sbd = new StringBuilder();
	// SQLParameter para = new SQLParameter();
	// for (int i = 0; i < attributeNames.length; i++) {
	// sbd.append(attributeNames[i] + " = ? ");
	// Object o = vo.getAttributeValue(attributeNames[i]);
	// if(o==null){
	// para.addNullParam(type)
	// }else{
	// para.addParam(o);
	// }
	//
	// if(i < attributeNames.length-1){
	// sbd.append(" , ");
	// }
	//
	// }
	// String sql = " update wa_classitem  set "+
	// sbd.toString()+"  where pk_wa_class = '"+ vo.getPk_wa_class()
	// +"' and cyear = '"+vo.getCyear()+"' and cperiod = '"+vo.getCperiod()+"' and pk_wa_item = '"+vo.getPk_wa_item()+"' "
	// ;
	// getBaseDao().executeUpdate(sql, para);
	//
	// }

	// public void updateParentItem(WaClassItemVO parentClassItem,WaClassItemVO
	// childClassItem,String[] attributeNames) throws DAOException{
	// //清空该项对应的数据
	//
	// StringBuilder sbd = new StringBuilder();
	// for (int i = 0; i < attributeNames.length; i++) {
	// sbd.append(attributeNames[i] + " = ? ");
	// Object o = vo.getAttributeValue(attributeNames[i]);
	// if(o==null){
	// para.addNullParam(type)
	// }else{
	// para.addParam(o);
	// }
	//
	// if(i < attributeNames.length-1){
	// sbd.append(" , ");
	// }
	//
	// }
	// String sql = " update wa_classitem  set "+
	// sbd.toString()+"  where pk_wa_class = '"+ vo.getPk_wa_class()
	// +"' and cyear = '"+vo.getCyear()+"' and cperiod = '"+vo.getCperiod()+"' and pk_wa_item = '"+vo.getPk_wa_item()+"' "
	// ;
	// getBaseDao().executeUpdate(sql, para);
	//
	// }

	public WaClassItemVO[] queryByCondition(LoginContext context, String strFromCond, String strWhereCond,
			String strOrderCond) throws BusinessException {
		String sql = getFileds();
		StringBuilder sbSQL = new StringBuilder(sql)
				.append(" from wa_classitem inner join wa_item  on wa_item.pk_wa_item = wa_classitem.pk_wa_item ");
		if (!StringUtils.isBlank(strFromCond)) {
			sbSQL.append(strFromCond);
		}
		sbSQL.append(" where  wa_classitem.pk_group = ? and wa_classitem.pk_org = ? ");
		if (!StringUtils.isBlank(strWhereCond)) {
			sbSQL.append(strWhereCond);
		}
		if (!StringUtils.isBlank(strOrderCond)) {
			sbSQL.append(strOrderCond);
		} else {
			sbSQL.append(" order by wa_classitem.idisplayseq ");
		}

		SQLParameter para = new SQLParameter();
		para.addParam(context.getPk_group());
		para.addParam(context.getPk_org());

		return executeQueryVOs(sbSQL.toString(), para, WaClassItemVO.class);
	}

	// 2014/05/23 shenliangc为解决薪资发放节点显示设置对话框中项目名称与本期间发放项目名称不同步问题而修改。
	// 名称不同步的原因是查询项目名称逻辑没有添加年度期间限制，全部取方案起始期间的发放项目名称。
	public WaClassItemVO[] queryItemsByPK_wa_class(String pk_wa_class, String cyear, String cperiod)
			throws DAOException {
		WaClassItemVO[] itemvos = this.retrieveByClause(WaClassItemVO.class, " pk_wa_class = '" + pk_wa_class
				+ "' and cyear = '" + cyear + "' and cperiod = '" + cperiod + "' ");
		return itemvos;
	}

	// shenliangc 20140823 本方案本期间内薪资发放项目名称已存在
	public boolean itemNameIsExist(String pk_wa_class, String cyear, String cperiod, WaClassItemVO vo)
			throws DAOException {
		String sql = "select name from wa_classitem where " + " pk_wa_class = '" + pk_wa_class + "' and "
				+ " cyear = '" + cyear + "' and " + " cperiod = '" + cperiod + "' and " + " name = '" + vo.getName()
				+ "'" + " and pk_wa_classitem not in ('" + vo.getPk_wa_classitem() + "')";

		WaClassItemVO[] classItemVOs = this.executeQueryVOs(sql, WaClassItemVO.class);

		return (!ArrayUtils.isEmpty(classItemVOs));
	}

}
