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

	// 如果是健保還需要多加一層判斷
	// 即檢查期間是否包含範圍期間的最後一天
	// 即健保設定中如果有兩條資料都包含了當月設定，
	// 但只有一條可以包含本月最後一天
	// 由於健保是全月在保，所以最後一天在保才為有效設定
	if (isHealth) {
	    ret = (scopeEnd.isSameDate(checkEnd) || scopeEnd.before(checkEnd))
		    && (scopeEnd.isSameDate(checkBegin) || scopeEnd.after(checkBegin));
	}

	return ret;
    }
}
