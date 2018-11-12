package nc.bs.hrsms.hi.employ.lsnr;

import java.util.ArrayList;
import nc.bs.dao.BaseDAO;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.uap.ad.ref.NcAdapterGridRefModel;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.core.refnode.RefNode;
import nc.uap.lfw.reference.ILfwRefModel;
import nc.uap.lfw.reference.app.AppReferenceController;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.om.job.JobTypeVO;
import nc.vo.om.joblevelsys.LevelRelationVO;
import nc.vo.pub.BusinessException;
import org.apache.commons.lang.StringUtils;

public class JobGradeRefNodeController extends AppReferenceController {

	@Override
	protected void processSelfWherePart(Dataset ds, RefNode rfnode, String filterValue, ILfwRefModel refModel) {
		try {
			resetRefnode(rfnode, refModel);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void processTreeSelWherePart(Dataset ds, RefNode rfnode, ILfwRefModel refModel) {
		try {
			resetRefnode(rfnode, refModel);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 重新设置RefNode的值
	 * 
	 * @param refModel
	 * @throws BusinessException 
	 */
	private void resetRefnode(RefNode rfnode, ILfwRefModel refModel) throws BusinessException {

		Dataset dsEvaDetail = getParentDs(rfnode);
		Row row = dsEvaDetail.getSelectedRow();

		// 选中职务类别主键
		String job = (String) row.getValue(dsEvaDetail.nameToIndex(PsnJobVO.PK_JOB));
		ArrayList<LevelRelationVO> jobs=queryLevelRelationVOsBySQL(" PK_JOBTYPE in ( select PK_JOBTYPE from om_job where pk_job ='"+job+"')");
		if(jobs!=null&&jobs.size()>0){
			if (((NcAdapterGridRefModel) refModel).getNcModel() instanceof nc.ui.om.ref.JobGradeRefModel) {
				nc.ui.om.ref.JobGradeRefModel model = (nc.ui.om.ref.JobGradeRefModel) ((NcAdapterGridRefModel) refModel)
						.getNcModel();
				model.setWherePart(" pk_joblevel in (select pk_joblevel from OM_LEVELRELATION OM_LEVELRELATION where  PK_JOBTYPE = '"+jobs.get(0).getPk_jobtype()+"')");
			}
	}
	}
	public ArrayList<LevelRelationVO> queryLevelRelationVOsBySQL(String WhereSQL) throws BusinessException {
		  String sql = "select * from OM_LEVELRELATION OM_LEVELRELATION where   "+WhereSQL;
		  //sql=sql+"and hi_psnjob.pk_psndoc in ( select hi_entryapply.pk_psndoc from hi_entryapply where dr=0 and approve_state in (-1,3) ) ";
		  LfwLogger.info(sql);
		  ArrayList<LevelRelationVO> al = (ArrayList<LevelRelationVO>) new BaseDAO().executeQuery(sql, new BeanListProcessor(LevelRelationVO.class));
		  return al == null ? null : al;
	  }
	public JobTypeVO[] queryJobVOsBySQL(  String WhereSQL) throws BusinessException {
	    String sql = "select * from om_jobtype om_jobtype where "+WhereSQL;
		  //sql=sql+"and hi_psnjob.pk_psndoc in ( select hi_entryapply.pk_psndoc from hi_entryapply where dr=0 and approve_state in (-1,3) ) ";
		  LfwLogger.info(sql);
		  ArrayList<JobTypeVO> al = (ArrayList<JobTypeVO>) new BaseDAO().executeQuery(sql, new BeanListProcessor(JobTypeVO.class));
		  return al == null ? null : al.toArray(new JobTypeVO[0]);
	  }
	/**
	 * 获取参照所在数据集的当前选中行
	 * 
	 * @return
	 */
	private Dataset getParentDs(RefNode rfnode) {
		// 参照所在页面
		String parentPageId = LfwRuntimeEnvironment.getWebContext().getWebSession().getOriginalParameter("otherPageId");
		LfwWindow parentPm = null;
		if (!StringUtils.isEmpty(parentPageId)) {
			parentPm = AppLifeCycleContext.current().getApplicationContext().getWindowContext(parentPageId).getWindow();
		} else {
			parentPm = AppLifeCycleContext.current().getWindowContext().getWindow();
		}
		// 参照所在片段
		LfwView widget = parentPm.getView(rfnode.getView().getId());
		// 参照写入数据集
		String writeDsId = rfnode.getWriteDs();
		return widget.getViewModels().getDataset(writeDsId);
	}

}
