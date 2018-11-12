/**
 * @(#)IExcelRowReader.java 1.0 2017-09-15
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.vo.wa.pub.util;

import org.apache.poi.ss.usermodel.Row;

/**
 * 读取Excel中的一行。
 * 
 * @author kevin.nie
 * @since 6.1
 */
public interface IExcelRowReader {

	/**
	 * 
	 * @param sheetNo
	 * @param rowNo
	 * @param row
	 * @return 为true，表示继续；为false，表示不再继续读
	 */
	boolean readRow(int sheetNo, int rowNo, Row row);
}
