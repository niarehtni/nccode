/**
 * @(#)ITxtRowReader.java 1.0 2017-09-15
 *
 * Copyright (c) 2013, YONYOU. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.vo.wa.pub.util;

/**
 * 读取文件中的一行数据
 * 
 * @author kevin.nie
 * @since 6.1
 */
public interface ITxtRowReader {
	boolean readRow(int rowNo, String content);
}
