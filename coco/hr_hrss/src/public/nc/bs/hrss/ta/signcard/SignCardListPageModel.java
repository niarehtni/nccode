package nc.bs.hrss.ta.signcard;

import nc.bs.hrss.ta.common.pagemode.TaListBasePageMode;

public class SignCardListPageModel extends TaListBasePageMode {

	@Override
	public String getBusinessEtag() {
		// ��д���෽�������û�����أ���С��������
		return "E20200917";
	}

}
