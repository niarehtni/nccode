package nc.bs.hrss.ta.signcard;

import nc.bs.hrss.ta.common.pagemode.TaListBasePageMode;

public class SignCardListPageModel extends TaListBasePageMode {

	@Override
	public String getBusinessEtag() {
		// 重写父类方法，启用缓存加载，减小下行流量
		return "E20200917";
	}

}
