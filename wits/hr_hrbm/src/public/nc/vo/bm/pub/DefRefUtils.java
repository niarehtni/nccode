/**
 * @(#)DefRefUtils.java 1.0 2018Äê1ÔÂ22ÈÕ
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.vo.bm.pub;

import nc.bs.logging.Logger;
import nc.bs.trade.business.HYPubBO;
import nc.vo.bd.ref.RefInfoVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author niehg
 * @since 6.3
 */
public class DefRefUtils {
	public static final String DEF_AGENTCOMP = "HRAL005";
	public static final String DEF_VENDERCODE = "HRAL006";

	public static String qryRefNameByDefCode(String defCode) {
		String condition = " para1 in (select pk_defdoclist from bd_defdoclist where code = '" + defCode + "' ) ";
		HYPubBO bo = new HYPubBO();
		RefInfoVO[] vos = null;
		try {
			vos = (RefInfoVO[]) bo.queryByCondition(RefInfoVO.class, condition);
		} catch (BusinessException e) {
			Logger.error(e);
		}
		if (!ArrayUtils.isEmpty(vos)) {
			return vos[0].getName();
		}
		return null;
	}
}
