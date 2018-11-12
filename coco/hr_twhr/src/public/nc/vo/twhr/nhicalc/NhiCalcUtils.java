package nc.vo.twhr.nhicalc;

import nc.vo.pub.lang.UFLiteralDate;

public class NhiCalcUtils {
    public static boolean isInScope(UFLiteralDate scopeBegin, UFLiteralDate scopeEnd, UFLiteralDate checkBegin,
	    UFLiteralDate checkEnd, boolean isHealth) {
	scopeBegin = scopeBegin == null ? new UFLiteralDate("9999-12-30") : scopeBegin;
	scopeEnd = scopeEnd == null ? new UFLiteralDate("9999-12-31") : scopeEnd;
	checkBegin = checkBegin == null ? new UFLiteralDate("9999-12-30") : checkBegin;
	checkEnd = checkEnd == null ? new UFLiteralDate("9999-12-31") : checkEnd;
	boolean ret = (scopeEnd.after(checkBegin) || scopeEnd.isSameDate(checkBegin))
		&& (scopeBegin.before(checkEnd) || scopeBegin.isSameDate(checkEnd));

	// ����ǽ���߀��Ҫ���һ���Д�
	// ���z�����g�Ƿ�����������g������һ��
	// �������O��������Ѓɗl�Y�϶������ˮ����O����
	// ��ֻ��һ�l���԰�����������һ��
	// ��춽�����ȫ���ڱ�����������һ���ڱ��Ş���Ч�O��
	if (isHealth) {
	    ret = (scopeEnd.isSameDate(checkEnd) || scopeEnd.before(checkEnd))
		    && (scopeEnd.isSameDate(checkBegin) || scopeEnd.after(checkBegin));
	}

	return ret;
    }
}
