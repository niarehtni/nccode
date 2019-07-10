package nc.bs.hr.dataexchange.export;

import java.util.HashMap;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

import org.apache.commons.lang.StringUtils;

public class FormatHelper {
	// modified for unknown Chinese character
	public static String getLengthName(String str, int lengthB) throws BusinessException {
		// Replace unknown Chinese character to double space
		str = getChtString(str, str.length());
		str = StringUtils.left(str, lengthB);
		if (str.length() * 2 < lengthB) {
			str = str + StringUtils.rightPad("", lengthB - str.length() * 2, " ");
		}
		// return StringUtils.replace(str, "£¿", "  ");
		return str;
	}

	public static String getLengthNameEx(String str, int lengthB) throws BusinessException {
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
				realLenB = strRtn.getBytes("Big5-HKSCS").length;
				if (realLenB > lengthB) {
					strRtn = strTmp;
					break;
				}
			}

			if (strRtn.length() * 2 < lengthB) {
				strRtn = strRtn + StringUtils.rightPad("", lengthB - strRtn.getBytes("Big5-HKSCS").length, " ");
			}
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}

		if (strRtn.contains("£¿"))
			return StringUtils.rightPad("", lengthB, " ");
		else
			return strRtn;
	}

	public static String getChtString(String str, int lengthW) throws BusinessException {
		HashMap<String, String> convertMap = new HashMap<String, String>();
		convertMap.put("A", "£Á");
		convertMap.put("B", "£Â");
		convertMap.put("C", "£Ã");
		convertMap.put("D", "£Ä");
		convertMap.put("E", "£Å");
		convertMap.put("F", "£Æ");
		convertMap.put("G", "£Ç");
		convertMap.put("H", "£È");
		convertMap.put("I", "£É");
		convertMap.put("J", "£Ê");
		convertMap.put("K", "£Ë");
		convertMap.put("L", "£Ì");
		convertMap.put("M", "£Í");
		convertMap.put("N", "£Î");
		convertMap.put("O", "£Ï");
		convertMap.put("P", "£Ð");
		convertMap.put("Q", "£Ñ");
		convertMap.put("R", "£Ò");
		convertMap.put("S", "£Ó");
		convertMap.put("T", "£Ô");
		convertMap.put("U", "£Õ");
		convertMap.put("V", "£Ö");
		convertMap.put("W", "£×");
		convertMap.put("X", "£Ø");
		convertMap.put("Y", "£Ù");
		convertMap.put("Z", "£Ú");
		convertMap.put("-", "£­");
		convertMap.put("~", "¡«");
		convertMap.put("/", "£¯");
		convertMap.put("(", "£¨");
		convertMap.put(")", "£©");
		convertMap.put(":", "£º");
		convertMap.put(",", "£¬");
		convertMap.put(".", "¡£");
		convertMap.put("0", "£°");
		convertMap.put("1", "£±");
		convertMap.put("2", "£²");
		convertMap.put("3", "£³");
		convertMap.put("4", "£´");
		convertMap.put("5", "£µ");
		convertMap.put("6", "£¶");
		convertMap.put("7", "£·");
		convertMap.put("8", "£¸");
		convertMap.put("9", "£¹");
		convertMap.put("¡ª", "£­");
		convertMap.put("¡ù", "£ª");
		convertMap.put(String.valueOf((char) 63), "£ß");

		try {
			byte[] bytStr = str.getBytes("Big5-HKSCS");
			String v = new String(bytStr, "Big5-HKSCS");
			v = v.replaceAll(" ", "¡¡").toUpperCase();
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
			// v = StringUtils.rightPad(v, lengthW, "¡¡");
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

	public static String getLengthString(String str, int length, String appchar, String left_right, boolean forceChinese)
			throws BusinessException {

		try {
			String v = StringUtils.left(str, length);
			if (v == null) {
				v = "";
			} else {
				if (v.getBytes("Big5-HKSCS").length != v.length() || forceChinese) {
					v = getLengthNameEx(v, length);
				} else {
					if (v.length() < length) {
						if ("L".equals(left_right)) {
							v = StringUtils.leftPad(v, length, appchar);
						} else if ("R".equals(left_right)) {
							v = StringUtils.rightPad(v, length, appchar);
						}
					}
				}
			}

			return v;
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}
}
