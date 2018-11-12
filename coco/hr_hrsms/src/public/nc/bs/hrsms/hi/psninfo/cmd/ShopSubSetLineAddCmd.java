package nc.bs.hrsms.hi.psninfo.cmd;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrsms.hi.psndoc.qry.IPsndocQryservice;
import nc.pub.tools.HRCMCommonValue;
import nc.uap.lfw.core.cmd.UifAddCmd;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.log.LfwLogger;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
/**
 * 子集增加行Delegator
 * @author lihha
 *
 */
public class ShopSubSetLineAddCmd extends UifAddCmd{

	private String dsId;
	
	private String pk_psndoc;
	
	
	public ShopSubSetLineAddCmd(String dsId,String pk_psndoc) {
		super(dsId);
		this.dsId = dsId;
		this.pk_psndoc = pk_psndoc;
	}

	@Override
    protected void onAfterRowAdd(Row row) {
		Dataset ds = getLifeCycleContext().getViewContext().getView().getViewModels().getDataset(dsId);
		PsndocVO psndocVO = null;
		try {
			psndocVO = NCLocator.getInstance().lookup(IPsndocQryservice.class).queryPsndocByPk(pk_psndoc);
		} catch (BusinessException e) {
			LfwLogger.error(e.getMessage(),e.getCause());
			throw new LfwRuntimeException(e.getMessage());
		}
		if(psndocVO == null){
			throw new LfwRuntimeException("当前人员有问题，请联系管理员");
		}
		// 集团
		if(ds.nameToIndex("pk_group")>-1)
			row.setValue(ds.nameToIndex("pk_group"), psndocVO.getPk_group());
		// 组织
		if(ds.nameToIndex("pk_org")>-1)
			row.setValue(ds.nameToIndex("pk_org"), psndocVO.getPk_org());
		// 基本信息主键
		if(ds.nameToIndex("pk_psndoc")>-1)
			row.setValue(ds.nameToIndex("pk_psndoc"), psndocVO.getPk_psndoc());
		// 工作记录主键
		if(ds.nameToIndex("pk_psnjob")>-1)
		    row.setValue(ds.nameToIndex("pk_psnjob"), psndocVO.getPsnJobVO().getPk_psnjob());
		// 组织关系主键
		if(ds.nameToIndex("pk_psnorg")>-1)
		    row.setValue(ds.nameToIndex("pk_psnorg"), psndocVO.getPsnJobVO().getPk_psnorg());
		if("hi_psndoc_ctrt".equals(dsId)){
			// 合同信息新增时，需要把签订类型设置成签订
			if(ds.nameToIndex("conttype")>-1)
				row.setValue(ds.nameToIndex("conttype"), HRCMCommonValue.CONTTYPE_MAKE);
		}
	}

}
