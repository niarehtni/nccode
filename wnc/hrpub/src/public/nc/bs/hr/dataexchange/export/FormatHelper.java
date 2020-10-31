package nc.bs.hr.dataexchange.export;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

import org.apache.commons.lang.StringUtils;

public class FormatHelper {
	public static String TEXTENCODING = "Big5-HKSCS";

	// modified for unknown Chinese character
	public static String getLengthName(String str, int lengthB) throws BusinessException {
		// Replace unknown Chinese character to double space
		str = getChtString(str, str.length());
		str = StringUtils.left(str, lengthB);
		if (str.length() * 2 < lengthB) {
			str = str + StringUtils.rightPad("", lengthB - str.length() * 2, " ");
		}
		// return StringUtils.replace(str, "��", "  ");
		return str;
	}

	public static String getLengthNameEx(String str, String appchar, int lengthB) throws BusinessException {
		// for CTBC new requirement.
		// If any word unknown, return blank string
		str = getChtString(str, str.length());
		int realLenB = 0;
		String strRtn = "";
		int i = 0;
		try {
			while (realLenB <= lengthB && !strRtn.equals(str)) {
				String strTmp = strRtn;
				strRtn += str.substring(i, i + 1).trim();
				i++;
				realLenB = strRtn.getBytes(TEXTENCODING).length;
				if (realLenB > lengthB) {
					strRtn = strTmp;
					break;
				}
			}

			if (!StringUtils.isEmpty(appchar) && strRtn.length() * 2 < lengthB) {
				strRtn = strRtn + StringUtils.rightPad("", lengthB - strRtn.getBytes(TEXTENCODING).length, appchar);
			}
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}

		// if (strRtn.contains("��"))
		// return StringUtils.rightPad("", lengthB, " ");
		// else
		return strRtn;
	}

	public static String getChtString(String str, int lengthW) throws BusinessException {
		HashMap<String, String> convertMap = new HashMap<String, String>();
		convertMap.put("A", "��");
		convertMap.put("B", "��");
		convertMap.put("C", "��");
		convertMap.put("D", "��");
		convertMap.put("E", "��");
		convertMap.put("F", "��");
		convertMap.put("G", "��");
		convertMap.put("H", "��");
		convertMap.put("I", "��");
		convertMap.put("J", "��");
		convertMap.put("K", "��");
		convertMap.put("L", "��");
		convertMap.put("M", "��");
		convertMap.put("N", "��");
		convertMap.put("O", "��");
		convertMap.put("P", "��");
		convertMap.put("Q", "��");
		convertMap.put("R", "��");
		convertMap.put("S", "��");
		convertMap.put("T", "��");
		convertMap.put("U", "��");
		convertMap.put("V", "��");
		convertMap.put("W", "��");
		convertMap.put("X", "��");
		convertMap.put("Y", "��");
		convertMap.put("Z", "��");
		convertMap.put("-", "��");
		convertMap.put("~", "��");
		convertMap.put("/", "��");
		convertMap.put("(", "��");
		convertMap.put(")", "��");
		convertMap.put(":", "��");
		convertMap.put(",", "��");
		convertMap.put(".", "��");
		convertMap.put("0", "��");
		convertMap.put("1", "��");
		convertMap.put("2", "��");
		convertMap.put("3", "��");
		convertMap.put("4", "��");
		convertMap.put("5", "��");
		convertMap.put("6", "��");
		convertMap.put("7", "��");
		convertMap.put("8", "��");
		convertMap.put("9", "��");
		convertMap.put("��", "��");
		convertMap.put("��", "��");
		convertMap.put(String.valueOf((char) 63), "��");

		try {
			byte[] bytStr = str.getBytes(TEXTENCODING);
			String v = new String(bytStr, TEXTENCODING);
			v = v.replaceAll(" ", "��").toUpperCase();
			for (int i = 0; i < v.length(); i++) {
				String s = String.valueOf(v.charAt(i));
				String vstr = convertMap.get(s);
				if (vstr != null) {
					if (s.matches("[\\(\\)\\.\\?]")) {
						v = v.replaceAll("\\" + s, vstr);
					} else {
						v = v.replaceAll(s, vstr);
					}
				}
			}
			// v = StringUtils.left(v, lengthW);
			// if (v.length() < lengthW) {
			// v = StringUtils.rightPad(v, lengthW, "��");
			// }
			return v;
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}

	public static String getTWYear(String str) {
		String v = "xxx";

		v = StringUtils.leftPad(String.valueOf(Integer.valueOf(str) - 1911), 3, "0");

		return v;
	}

	public static String getTWPeriod(String str) {
		String v = "xxxxx";
		v = getTWYear(str.substring(0, 4)) + str.substring(4);
		return v;
	}

	public static String getTWDate(UFDate date) {
		String v = "xxxxxxx";
		v = getTWYear(String.valueOf(date.getYear())) + StringUtils.leftPad(String.valueOf(date.getMonth()), 2, "0")
				+ StringUtils.leftPad(String.valueOf(date.getDay()), 2, "0");
		return v;
	}

	/**
	 * ��ָ�����Ȼ���ַ������������ĵĽ�Ӣ�����ּ����ž�תΪȫ��
	 * 
	 * @param str
	 *            Դ�ַ���
	 * @param length
	 *            �ܳ���
	 * @param appchar
	 *            �����ַ�
	 * @param left_right
	 *            ���Ҳ����ʶ
	 * @param forceChinese
	 *            ǿ��ȫ���ı�ʶ��ΪTRUEʱ����Ӣ���ַ�Ҳ��תΪȫ�ǣ�
	 * @return
	 * @throws BusinessException
	 */
	public static String getLengthString(String str, int length, String appchar, String left_right, boolean forceChinese)
			throws BusinessException {

		try {
			String v = StringUtils.left(str, length);
			if (v == null) {
				v = "";
			} else {
				if (v.getBytes(TEXTENCODING).length != v.length() || forceChinese) {
					v = getLengthNameEx(v, appchar, length);
				} else {
					if (v.length() < length) {
						v = getLengthString(v, length, appchar, left_right);
					}
				}
			}

			return v;
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * ��ָ�����Ȼ���ַ��� ��֧��ASCII�ַ�����֧�����ģ�
	 * 
	 * @param str
	 *            Դ�ַ���
	 * @param length
	 *            �ܳ���
	 * @param appchar
	 *            �����ַ�
	 * @param left_right
	 *            ���Ҳ����ʶ
	 * @return
	 */
	private static String getLengthString(String str, int length, String appchar, String left_right) {
		if ("L".equals(left_right)) {
			str = StringUtils.leftPad(str, length, appchar);
		} else if ("R".equals(left_right)) {
			str = StringUtils.rightPad(str, length, appchar);
		}
		return str;
	}

	/**
	 * ��ָ�����Ȼ���ַ��� ���İ�2�ֽ�ʶ��ASCII�ַ���1�ֽ�ʶ��
	 * 
	 * @param str
	 *            Դ�ַ���
	 * @param length
	 *            �ܳ���
	 * @param appchar
	 *            �����ַ�
	 * @param left_right
	 *            ���Ҳ����ʶ
	 * @return
	 * @throws BusinessException
	 */
	public static String getLengthStringEx(String str, int length, String appchar, String left_right)
			throws BusinessException {

		try {
			String v = substringByte(str, 0, length);
			if (v == null) {
				v = "";
			} else {
				if (v.getBytes(TEXTENCODING).length != v.length()) {
					if (v.getBytes(TEXTENCODING).length < length) {
						str = getLengthString("", length - v.getBytes(TEXTENCODING).length, appchar, left_right);
						if ("L".equals(left_right)) {
							v = str + v;
						} else if ("R".equals(left_right)) {
							v = v + str;
						}
					}
				} else {
					if (v.length() < length) {
						v = getLengthString(v, length, appchar, left_right);
					}
				}
			}

			return v;
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * �ж��Ƿ���һ�����ĺ���
	 * 
	 * @param c
	 *            �ַ�
	 * @return true��ʾ�����ĺ��֣�false��ʾ��Ӣ����ĸ
	 * @throws UnsupportedEncodingException
	 *             ʹ����JAVA��֧�ֵı����ʽ
	 */
	public static boolean isChineseChar(char c) throws BusinessException {

		// ����ֽ�������1���Ǻ���
		// �����ַ�ʽ����Ӣ����ĸ�����ĺ��ֲ�����ʮ���Ͻ������������Ŀ�У������ж��Ѿ��㹻��
		try {
			return String.valueOf(c).getBytes(TEXTENCODING).length > 1;
		} catch (UnsupportedEncodingException e) {
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * ���㵱ǰString�ַ�����ռ����Byte����
	 * 
	 * @param args
	 *            Ҫ��ȡ���ַ���
	 * @return ����ֵint�ͣ��ַ�����ռ���ֽڳ��ȣ����argsΪ�ջ��ߡ����򷵻�0
	 * @throws UnsupportedEncodingException
	 */
	public static int getStringByteLenths(String args) throws BusinessException {
		try {
			return args != null && args != "" ? args.getBytes(TEXTENCODING).length : 0;
		} catch (UnsupportedEncodingException e) {
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * ��ȡ���ַ���ÿһ��char��Ӧ���ֽڳ�������
	 * 
	 * @param args
	 *            Ҫ�����Ŀ���ַ���
	 * @return int[] �������ͣ��������ַ���ÿһ��char��Ӧ���ֽڳ�������
	 * @throws UnsupportedEncodingException
	 */
	public static int[] getByteLenArrays(String args) throws BusinessException {

		char[] strlen = args.toCharArray();
		int[] charlen = new int[strlen.length];
		try {
			for (int i = 0; i < strlen.length; i++) {

				charlen[i] = String.valueOf(strlen[i]).getBytes(TEXTENCODING).length;

			}
		} catch (UnsupportedEncodingException e) {
			throw new BusinessException(e.getMessage());
		}
		return charlen;
	}

	/**
	 * ���ֽڽ�ȡ�ַ��� ��ָ����ȡ��ʼ�ֽ�λ�����ȡ�ֽڳ���
	 * 
	 * @param orignal
	 *            Ҫ��ȡ���ַ���
	 * @param offset
	 *            ��ȡByte���ȣ�
	 * @return ��ȡ����ַ���
	 * @throws BusinessException
	 */
	public static String substringByte(String orignal, int start, int count) throws BusinessException {

		// ���Ŀ���ַ���Ϊ�գ���ֱ�ӷ��أ��������ȡ�߼���
		if (orignal == null || "".equals(orignal))
			return orignal;

		// ��ȡByte���ȱ���>0
		if (count <= 0)
			return orignal;

		// ��ȡ����ʼ�ֽ��������
		if (start < 0)
			start = 0;

		// Ŀ��char Pull buff�������䣻
		StringBuffer buff = new StringBuffer();

		try {

			// ��ȡ�ֽ���ʼ�ֽ�λ�ô���Ŀ��String��Byte��length�򷵻ؿ�ֵ
			if (start >= getStringByteLenths(orignal))
				return null;

			// int[] arrlen=getByteLenArrays(orignal);
			int len = 0;

			char c;

			// ����String��ÿһ��Char�ַ������㵱ǰ�ܳ���
			// �������ǰChar�ĵ��ֽڳ��ȴ���Ҫ��ȡ���ַ��ܳ��ȣ�������ѭ�����ؽ�ȡ���ַ�����
			for (int i = 0; i < orignal.toCharArray().length; i++) {

				c = orignal.charAt(i);

				// ����ʼλ��Ϊ0ʱ��
				if (start == 0) {

					len += String.valueOf(c).getBytes(TEXTENCODING).length;
					if (len <= count)
						buff.append(c);
					else
						break;

				} else {

					// ��ȡ�ַ����ӷ�0λ�ÿ�ʼ
					len += String.valueOf(c).getBytes(TEXTENCODING).length;
					if (len >= start && len <= start + count) {
						buff.append(c);
					}
					if (len > start + count)
						break;

				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// �������ս�ȡ���ַ����;
		// ����String���󣬴���Ŀ��char Buff����
		return new String(buff);
	}

	/**
	 * ��ȡָ�������ַ���
	 * 
	 * @param orignal
	 *            Ҫ��ȡ��Ŀ���ַ���
	 * @param count
	 *            ָ����ȡ����
	 * @return ���ؽ�ȡ����ַ���
	 * @throws BusinessException
	 */
	public static String substringByte(String orignal, int count) throws BusinessException {
		return substringByte(orignal, 0, count);
	}

}
