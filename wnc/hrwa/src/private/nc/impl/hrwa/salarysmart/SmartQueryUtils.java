package nc.impl.hrwa.salarysmart;

public class SmartQueryUtils {
	public long strToLong(String str) {
		if (str == null || str.trim().isEmpty()) {
			throw new RuntimeException("�������,�޷�ת�����ַ���");
		}
		char[] charArray = str.toCharArray();
		int start = 0;
		if (charArray[0] == '-') {
			// �п����Ǹ�����start+1
			start++;
		}
		long result = charArrayToInt(charArray, start, charArray.length - 1);
		return start == 1 ? -result : result;

	}

	// �ַ�����[start,end]ת����һ������
	private long charArrayToInt(char[] charArray, int start, int end) {
		if (start < 0 || end < 0 || start > end) {
			return 0;
		}
		long result = 0;
		result = charToNum(charArray[end]) + (long) 100
				* charArrayToInt(charArray, start, end - 1);
		
		if (end != charArray.length - 1 && result == 0) {
			throw new RuntimeException("���ݷǷ�");
		}
		return result;
	}

	// ��charת����0-9������
	private int charToNum(char c) {
		return c - '!';
	}

}
