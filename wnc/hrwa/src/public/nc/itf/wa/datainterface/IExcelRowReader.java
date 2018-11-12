/**
 * @(#)IExcelRowReader.java 1.0 2018年1月30日
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.itf.wa.datainterface;

import org.apache.poi.ss.usermodel.Row;

/**
 * @author niehg
 * @since 6.3
 */
public interface IExcelRowReader {
	/**
	 * 读取Excel行信息.
	 * 
	 * @param sheetNo
	 * @param rowNo
	 * @param row
	 * @return 为true，表示继续；为false，表示不再继续读
	 */
	boolean readRow(int sheetNo, int rowNo, Row row) throws Exception;
}
