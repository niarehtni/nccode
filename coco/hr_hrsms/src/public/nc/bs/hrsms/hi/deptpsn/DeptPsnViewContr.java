package nc.bs.hrsms.hi.deptpsn;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.ui.hr.pub.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import uap.web.bd.pub.AppUtil;
public class DeptPsnViewContr implements IController {
  @SuppressWarnings("unused")
private static final long serialVersionUID=1L;
  
    public void onDataLoad(DataLoadEvent dataLoadEvent) {
		try {
			PsnJobVO[] psnJobVOs = null;
			// 获得选择的管理部门
			String pk_mng_dept = SessionUtil.getPk_mng_dept();
			boolean isIncludeSubDept = SessionUtil.isIncludeSubDept();
			String psnScopeSqlPart = SessionUtil.getSessionBean().getPsnScopeSqlPart();
			if (StringUtil.isEmptyWithTrim(pk_mng_dept)) {
				return;
			}
			// 取到session中的查询条件
			FromWhereSQL hrFromWhereSQL = (FromWhereSQL)AppUtil.getAppAttr("hrFromWhereSQL");
			if(hrFromWhereSQL == null){
				// 首次进入
				FromWhereSQLImpl fromWhereSQL = new FromWhereSQLImpl();
				fromWhereSQL.setWhere(" hi_psnjob.pk_psndoc in (" + psnScopeSqlPart + ") ");
				hrFromWhereSQL = new FromWhereSQL(
						fromWhereSQL.getFrom(), fromWhereSQL.getWhere(),fromWhereSQL, fromWhereSQL.getAttrpath_alias_map());
				
			}
			psnJobVOs = ServiceLocator.lookup(IPsndocQryService.class)
					.queryPsndocVOsByDeptPK(pk_mng_dept, isIncludeSubDept, hrFromWhereSQL);
			//去掉多余的工作记录
			psnJobVOs = distinctPsnJobVO(psnJobVOs);
			if(psnJobVOs!=null&&psnJobVOs.length>0){
			for(int i=0;i<psnJobVOs.length;i++){
				String pk_psnorg=psnJobVOs[i].getPk_psnorg();
				SuperVO hrorgVO=NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByPk(null,PsnOrgVO.class, pk_psnorg);
				UFLiteralDate beginTime= (UFLiteralDate) hrorgVO.getAttributeValue("joinsysdate");
				psnJobVOs[i].setBegindate(beginTime);
			}
			}
//			String pk_jobgrade=aggVO.getParentVO().getPsnJobVO().getPk_jobgrade();
//			SuperVO[] queryVOs=new SuperVO[] { aggVO.getParentVO().getPsnJobVO() };
//			if(null!=pk_jobgrade&&!pk_jobgrade.equals("")){
//				SuperVO jobLevelVO=NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByPk(null,JobLevelVO.class, pk_jobgrade);
//				if(null!=jobLevelVO){
//					String name=(String)jobLevelVO.getAttributeValue("name");	
//					MultiLangHelper.getName ( NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByPk(null,JobLevelVO.class, pk_jobgrade),"name");
//					queryVOs[0].setAttributeValue("pk_jobgrade", name);
//				}
//			}
//			return queryVOs;
			LfwView mainWidget = AppLifeCycleContext.current().getViewContext().getView();
			Dataset ds = mainWidget.getViewModels().getDataset("DsDeptPsn");
			// 分页处理
			SuperVO[] svos = DatasetUtil.paginationMethod(ds, psnJobVOs);
			new SuperVO2DatasetSerializer().serialize(svos, ds, Row.STATE_NORMAL);
		}
	    catch (HrssException e) {
	    	new HrssException(e).alert();
	    }
	    catch (BusinessException e) {
	    	new HrssException(e).alert();
	    }
	  }

  public void plugininMain(Map<String, Object> keys){
		try {
			PsnJobVO[] psnJobVOs = null;
			// 获得选择的管理部门
			String pk_mng_dept = SessionUtil.getPk_mng_dept();
			boolean isIncludeSubDept = SessionUtil.isIncludeSubDept();
			String psnScopeSqlPart = SessionUtil.getSessionBean().getPsnScopeSqlPart();
			if (StringUtil.isEmptyWithTrim(pk_mng_dept)) {
				return;
			}
			FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) CommonUtil
					.getUAPFromWhereSQL((nc.uap.lfw.core.cmd.base.FromWhereSQL) keys.get("whereSql"));
			fromWhereSQL.setWhere(fromWhereSQL.getWhere() + " and hi_psnjob.pk_psndoc in (" + psnScopeSqlPart + ") ");
			FromWhereSQL hrFromWhereSQL = new FromWhereSQL(
					fromWhereSQL.getFrom(), fromWhereSQL.getWhere(),fromWhereSQL, fromWhereSQL.getAttrpath_alias_map());
			psnJobVOs = ServiceLocator.lookup(IPsndocQryService.class)
					.queryPsndocVOsByDeptPK(pk_mng_dept, isIncludeSubDept, hrFromWhereSQL);
			// 把查询条件放在session中以便分页的时候使用
			AppUtil.addAppAttr("hrFromWhereSQL", hrFromWhereSQL);
			
			//去掉多余的工作记录
			psnJobVOs = distinctPsnJobVO(psnJobVOs);
			if(psnJobVOs!=null&&psnJobVOs.length>0){
				for(int i=0;i<psnJobVOs.length;i++){
					String pk_psnorg=psnJobVOs[i].getPk_psnorg();
					SuperVO hrorgVO=NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByPk(null,PsnOrgVO.class, pk_psnorg);
					UFLiteralDate beginTime= (UFLiteralDate) hrorgVO.getAttributeValue("joinsysdate");
					psnJobVOs[i].setBegindate(beginTime);
				}
			}
			LfwView mainWidget = AppLifeCycleContext.current().getViewContext().getView();
			Dataset ds = mainWidget.getViewModels().getDataset("DsDeptPsn");
			DatasetUtil.clearData(ds);
			SuperVO[] svos = DatasetUtil.paginationMethod(ds, psnJobVOs);
			new SuperVO2DatasetSerializer().serialize(svos, ds, Row.STATE_NORMAL);
		}
	    catch (HrssException e) {
	    	new HrssException(e).alert();
	    }
	    catch (BusinessException e) {
	    	new HrssException(e).alert();
	    }
	  }
  
    /**
	 * 管理部门变更
	 * 
	 * @param keys
	 */
	public void pluginDeptChange(Map<String, Object> keys) {
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	
	/**
	 * 对于同一个pk_psndoc在psnJobVOs中有多条记录，如果这多条记录都是兼职的，就随机留下一条，其余的去掉；
	 * 如果这多条记录中有一条是主职的，就把主职的留下，其余的去掉。
	 * @param psnJobVOs
	 * @return
	 */
	public PsnJobVO[] distinctPsnJobVO(PsnJobVO[] psnJobVOs){
		if(ArrayUtils.isEmpty(psnJobVOs)){
			return null;
		}
		Map<String,ArrayList<PsnJobVO>> map = new HashMap<String,ArrayList<PsnJobVO>>();
		List<PsnJobVO> listPsnJobVO = new ArrayList<PsnJobVO>();
		for(PsnJobVO psnJobVO : psnJobVOs){
			ArrayList<PsnJobVO> list = null;
			list = map.get(psnJobVO.getPk_psndoc());
			if(CollectionUtils.isEmpty(list)){
				list = new ArrayList<PsnJobVO>();
				list.add(psnJobVO);
				map.put(psnJobVO.getPk_psndoc(),list);
			} else {
				if(psnJobVO.getIsmainjob().booleanValue()){
					list.clear();
					list.add(psnJobVO);
				}
			}
		}
		
		for(String pk_psndoc : map.keySet()){
			listPsnJobVO.addAll(map.get(pk_psndoc));
		}
		return listPsnJobVO.toArray(new PsnJobVO[0]);
	}
  
}
