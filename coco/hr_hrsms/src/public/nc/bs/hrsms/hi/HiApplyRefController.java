package nc.bs.hrsms.hi;

import nc.bs.hrss.pub.HrssConsts;
import nc.vo.trn.transmng.StapplyVO;

public class HiApplyRefController extends HiApproveRefController {
	@Override
	protected String getRefNodeId(String dsId, String filedId) {
		String ref_suffix = HrssConsts.REF_SUFFIX;
		if(StapplyVO.NEWPK_POST.equals(filedId)){
			ref_suffix =  "_postname";
		}
		return "refnode_" + dsId + "_" + filedId + ref_suffix;
	}
}
