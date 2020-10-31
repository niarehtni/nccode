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
		// return StringUtils.replace(str, "？", "  ");
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

		// if (strRtn.contains("？"))
		// return StringUtils.rightPad("", lengthB, " ");
		// else
		return strRtn;
	}

	public static String getChtString(String str, int lengthW) throws BusinessException {
		HashMap<String, String> convertMap = new HashMap<String, String>();
		convertMap.put("A", "Ａ");
		convertMap.put("B", "Ｂ");
		convertMap.put("C", "Ｃ");
		convertMap.put("D", "Ｄ");
		convertMap.put("E", "Ｅ");
		convertMap.put("F", "Ｆ");
		convertMap.put("G", "Ｇ");
		convertMap.put("H", "Ｈ");
		convertMap.put("I", "Ｉ");
		convertMap.put("J", "Ｊ");
		convertMap.put("K", "Ｋ");
		convertMap.put("L", "Ｌ");
		convertMap.put("M", "Ｍ");
		convertMap.put("N", "Ｎ");
		convertMap.put("O", "Ｏ");
		convertMap.put("P", "Ｐ");
		convertMap.put("Q", "Ｑ");
		convertMap.put("R", "Ｒ");
		convertMap.put("S", "Ｓ");
		convertMap.put("T", "Ｔ");
		convertMap.put("U", "Ｕ");
		convertMap.put("V", "Ｖ");
		convertMap.put("W", "Ｗ");
		convertMap.put("X", "Ｘ");
		convertMap.put("Y", "Ｙ");
		convertMap.put("Z", "Ｚ");
		convertMap.put("-", "－");
		convertMap.put("~", "～");
		convertMap.put("/", "／");
		convertMap.put("(", "（");
		convertMap.put(")", "）");
		convertMap.put(":", "：");
		convertMap.put(",", "，");
		convertMap.put(".", "。");
		convertMap.put("0", "０");
		convertMap.put("1", "１");
		convertMap.put("2", "２");
		convertMap.put("3", "３");
		convertMap.put("4", "４");
		convertMap.put("5", "５");
		convertMap.put("6", "６");
		convertMap.put("7", "７");
		convertMap.put("8", "８");
		convertMap.put("9", "９");
		convertMap.put("—", "－");
		convertMap.put("※", "＊");
		convertMap.put(String.valueOf((char) 63), "？");

		try {
			byte[] bytStr = str.getBytes(TEXTENCODING);
			String v = new String(bytStr, TEXTENCODING);
			v = v.replaceAll(" ", "　").toUpperCase();
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
			// v = StringUtils.rightPad(v, lengthW, "　");
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
	 * 按指定长度获得字符串，含有中文的将英文数字及符号均转为全角
	 * 
	 * @param str
	 *            源字符串
	 * @param length
	 *            总长度
	 * @param appchar
	 *            补充字符
	 * @param left_right
	 *            左右补齐标识
	 * @param forceChinese
	 *            强制全中文标识（为TRUE时，纯英文字符也将转为全角）
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
	 * 按指定长度获得字符串 仅支持ASCII字符（不支持中文）
	 * 
	 * @param str
	 *            源字符串
	 * @param length
	 *            总长度
	 * @param appchar
	 *            补充字符
	 * @param left_right
	 *            左右补齐标识
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
	 * 按指定长度获得字符串 中文按2字节识别，ASCII字符按1字节识别
	 * 
	 * @param str
	 *            源字符串
	 * @param length
	 *            总长度
	 * @param appchar
	 *            补充字符
	 * @param left_right
	 *            左右补齐标识
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
	 * 判断是否是一个中文汉字
	 * 
	 * @param c
	 *            字符
	 * @return true表示是中文汉字，false表示是英文字母
	 * @throws UnsupportedEncodingException
	 *             使用了JAVA不支持的编码格式
	 */
	public static boolean isChineseChar(char c) throws BusinessException {

		// 如果字节数大于1，是汉字
		// 以这种方式区别英文字母和中文汉字并不是十分严谨，但在这个题目中，这样判断已经足够了
		try {
			return String.valueOf(c).getBytes(TEXTENCODING).length > 1;
		} catch (UnsupportedEncodingException e) {
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * 计算当前String字符串所占的总Byte长度
	 * 
	 * @param args
	 *            要截取的字符串
	 * @return 返回值int型，字符串所占的字节长度，如果args为空或者“”则返回0
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
	 * 获取与字符串每一个char对应的字节长度数组
	 * 
	 * @param args
	 *            要计算的目标字符串
	 * @return int[] 数组类型，返回与字符串每一个char对应的字节长度数组
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
	 * 按字节截取字符串 ，指定截取起始字节位置与截取字节长度
	 * 
	 * @param orignal
	 *            要截取的字符串
	 * @param offset
	 *            截取Byte长度；
	 * @return 截取后的字符串
	 * @throws BusinessException
	 */
	public static String substringByte(String orignal, int start, int count) throws BusinessException {

		// 如果目标字符串为空，则直接返回，不进入截取逻辑；
		if (orignal == null || "".equals(orignal))
			return orignal;

		// 截取Byte长度必须>0
		if (count <= 0)
			return orignal;

		// 截取的起始字节数必须比
		if (start < 0)
			start = 0;

		// 目标char Pull buff缓存区间；
		StringBuffer buff = new StringBuffer();

		try {

			// 截取字节起始字节位置大于目标String的Byte的length则返回空值
			if (start >= getStringByteLenths(orignal))
				return null;

			// int[] arrlen=getByteLenArrays(orignal);
			int len = 0;

			char c;

			// 遍历String的每一个Char字符，计算当前总长度
			// 如果到当前Char的的字节长度大于要截取的字符总长度，则跳出循环返回截取的字符串。
			for (int i = 0; i < orignal.toCharArray().length; i++) {

				c = orignal.charAt(i);

				// 当起始位置为0时候
				if (start == 0) {

					len += String.valueOf(c).getBytes(TEXTENCODING).length;
					if (len <= count)
						buff.append(c);
					else
						break;

				} else {

					// 截取字符串从非0位置开始
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
		// 返回最终截取的字符结果;
		// 创建String对象，传入目标char Buff对象
		return new String(buff);
	}

	/**
	 * 截取指定长度字符串
	 * 
	 * @param orignal
	 *            要截取的目标字符串
	 * @param count
	 *            指定截取长度
	 * @return 返回截取后的字符串
	 * @throws BusinessException
	 */
	public static String substringByte(String orignal, int count) throws BusinessException {
		return substringByte(orignal, 0, count);
	}

}
