package nc.bs.hrsms.ta.sss.overtime.ctrl;

import java.util.TimeZone;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.logging.Logger;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.algorithm.ITimeScope;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
/**
 * ��ȡĬ�ϵĿ�ʼ����ʱ��
 * Ҳ���Ը��ݿ�ʼʱ���ȡĬ�ϵĽ���ʱ��
 * 
 * @author shaochj
 *
 */
public class ShopDefaultTimeScope {

	/**
	 * 
	 * @param pk_org
	 * @param begintime
	 * @param endtime
	 * @param timeZone
	 * @return
	 */
	public static ITimeScope getDefaultTimeScope(String pk_org, UFDateTime begintime, UFDateTime endtime, TimeZone timeZone){
		ITimeScope defaultScope = null;
		try {
			if(timeZone == null){
				timeZone = TimeZone.getDefault();
			}
			defaultScope = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class)
			.calculateOrgDefaultOvertimeBeginEndTime(SessionUtil.getPk_org(), null, null, timeZone);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		return defaultScope;
	}
}
