/**
 * @(#)MappingFieldVO.java 1.0 2018Äê2ÔÂ8ÈÕ
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.vo.wa.datainterface;

import nc.vo.pub.SuperVO;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings("serial")
public class MappingFieldVO extends SuperVO {
	private Integer id;
	private Integer imptype;
	private Integer colindex;
	private String itemkey;
	private String code;

	public static final String TABNAME = "wa_imp_fieldmapping";
	public static final String PK = "id";
	public static final String IMPTYPE = "imptype";
	public static final String COLINDEX = "colindex";
	public static final String ITEMKEY = "itemkey";
	public static final String CODE = "code";
	public static final Integer TYPE_SD = 0;
	public static final Integer TYPE_SOD = 1;
	public static final Integer TYPE_BD = 2;
	public static final Integer TYPE_BOD = 3;
	public static final Integer TYPE_BATCH = 4;
	public static final String BATCH_CODE = "batchnum";

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getImptype() {
		return imptype;
	}

	public void setImptype(Integer imptype) {
		this.imptype = imptype;
	}

	public Integer getColindex() {
		return colindex;
	}

	public void setColindex(Integer colindex) {
		this.colindex = colindex;
	}

	public String getItemkey() {
		return itemkey;
	}

	public void setItemkey(String itemkey) {
		this.itemkey = itemkey;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String getPKFieldName() {
		return PK;
	}

	@Override
	public String getTableName() {
		return TABNAME;
	}

}
