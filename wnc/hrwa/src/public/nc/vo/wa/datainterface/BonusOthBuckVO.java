/**
 * @(#)BonusOthBuckVO.java 1.0 2018Äê1ÔÂ30ÈÕ
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.vo.wa.datainterface;

import nc.pub.wa.datainterface.DataItfConst;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings("serial")
public class BonusOthBuckVO extends DataItfFileVO {
	private String schemecode;
	private String schemename;

	public String getSchemecode() {
		return schemecode;
	}

	public void setSchemecode(String schemecode) {
		this.schemecode = schemecode;
	}

	public String getSchemename() {
		return schemename;
	}

	public void setSchemename(String schemename) {
		this.schemename = schemename;
	}

	@Override
	public String getTableName() {
		return DataItfConst.TAB_BOD;
	}
}
