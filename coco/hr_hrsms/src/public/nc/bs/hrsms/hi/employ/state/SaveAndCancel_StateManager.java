package nc.bs.hrsms.hi.employ.state;

import java.util.ArrayList;

import nc.bs.hrss.hi.psninfo.PsninfoConsts;
import nc.bs.hrss.hi.psninfo.PsninfoUtil;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;

import org.apache.commons.collections.CollectionUtils;

import uap.web.bd.pub.AppUtil;


/**
 * 保存、取消：只在基本信息显示，需要审核且为提交时隐藏
 * @author lihha
 *
 */
public class SaveAndCancel_StateManager extends Psninfo_Base_StateManager{
	@Override
	protected State getStateByItem(String infoset, boolean needAudit, int data_status) {
		Dataset ds = AppLifeCycleContext.current().getViewContext().getView().getViewModels().getDataset(infoset);
		ds.setEnabled(false);
		// 可编辑的数据集
		@SuppressWarnings("unchecked")
		ArrayList<String> editDatasetlist = (ArrayList<String>) AppUtil.getAppAttr("editDatasetlist");
		if(!(PsninfoConsts.INFOSET_CODE_PSNDOC.equals(infoset)))
			return State.HIDDEN;
		if(!(CollectionUtils.isNotEmpty(editDatasetlist) && editDatasetlist.contains(infoset)))
			return State.HIDDEN;
		if(!PsninfoUtil.isEdit(data_status))
			return State.HIDDEN;
		ds.setEnabled(true);
		return State.VISIBLE;
	}
}
