package nc.bs.hrsms.hi.employ.state;

import java.util.ArrayList;

import nc.bs.hrss.hi.psninfo.PsninfoConsts;
import nc.bs.hrss.hi.psninfo.PsninfoUtil;

import org.apache.commons.collections.CollectionUtils;

import uap.web.bd.pub.AppUtil;

/**
 * 新增、删除：基本信息、工作信息子集隐藏
 * @author lihha
 *
 */
public class AddAndDelLine_StateManager extends Psninfo_Base_StateManager{
	@Override
	protected State getStateByItem(String infoset, boolean needAudit, int data_status) {
		 // 可编辑的数据集
		 @SuppressWarnings("unchecked")
		ArrayList<String> editDatasetlist = (ArrayList<String>) AppUtil.getAppAttr("editDatasetlist");
		if(PsninfoConsts.INFOSET_CODE_PSNDOC.equals(infoset))
			return State.HIDDEN;
		if(!(CollectionUtils.isNotEmpty(editDatasetlist) && editDatasetlist.contains(infoset)))
			return State.HIDDEN;
		if(!PsninfoUtil.isEdit(data_status))
			return State.HIDDEN;
		return State.VISIBLE;
	}
}
