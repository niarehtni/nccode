package nc.bs.hrsms.ta.sss.ShopAttendance.lsnr;

import nc.uap.lfw.core.comp.IWebPartContentFetcher;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;

/**
 * ���������������ɫ˵��
 * 
 * @author mayif
 * 
 */
public class MachineContentFetcher implements IWebPartContentFetcher {

	@Override
	public String fetchHtml(UIMeta um, LfwWindow pm, LfwView view) {
		/* ��ɫ˵�� */
		return ShopAttendanceUtil.getColorBrief(true);
	}

	@Override
	public String fetchBodyScript(UIMeta um, LfwWindow pm, LfwView view) {
		return "";
	}

}
