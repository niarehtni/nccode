/**
 * @(#)ICsvRowReader.java 1.0 2018��1��30��
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.itf.wa.datainterface;

/**
 * @author niehg
 * @since 6.3
 */
public interface ICsvRowReader {
	/**
	 * ��ȡCsv����Ϣ.
	 * 
	 * @param rowNo
	 * @param content
	 * @return Ϊtrue����ʾ������Ϊfalse����ʾ���ټ�����
	 */
	boolean readRow(int rowNo, String content) throws Exception;
}
