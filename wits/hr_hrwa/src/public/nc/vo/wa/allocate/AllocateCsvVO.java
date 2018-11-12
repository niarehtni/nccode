/**
 * @(#)AllocateCsvVO.java 1.0 2017Äê9ÔÂ22ÈÕ
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.vo.wa.allocate;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings("serial")
public class AllocateCsvVO extends SuperVO {

	private String account;
	private UFDouble vouchmny;
	private String costcenter;
	private String orderno;
	private String share;
	private String projtext;
	private String venderno;

	public AllocateCsvVO() {

	}

	public AllocateCsvVO(String account, UFDouble vouchmny, String costcenter, String orderno, String share,
			String projtext, String venderno) {
		this.account = account;
		this.vouchmny = vouchmny;
		this.costcenter = costcenter;
		this.orderno = orderno;
		this.share = share;
		this.projtext = projtext;
		this.venderno = venderno;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public UFDouble getVouchmny() {
		return vouchmny;
	}

	public void setVouchmny(UFDouble vouchmny) {
		this.vouchmny = vouchmny;
	}

	public String getCostcenter() {
		return costcenter;
	}

	public void setCostcenter(String costcenter) {
		this.costcenter = costcenter;
	}

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public String getShare() {
		return share;
	}

	public void setShare(String share) {
		this.share = share;
	}

	public String getProjtext() {
		return projtext;
	}

	public void setProjtext(String projtext) {
		this.projtext = projtext;
	}

	public String getVenderno() {
		return venderno;
	}

	public void setVenderno(String venderno) {
		this.venderno = venderno;
	}

	public String getUnionID() {
		StringBuilder unionID = new StringBuilder(account);
		unionID.append(costcenter).append(orderno).append(share).append(projtext).append(venderno);
		return unionID.toString();
	}
}
