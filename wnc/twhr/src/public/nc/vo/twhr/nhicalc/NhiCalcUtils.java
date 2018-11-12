package nc.vo.twhr.nhicalc;

import nc.vo.pub.lang.UFLiteralDate;

public class NhiCalcUtils {

	/**
	 * 勞保勞退加密屬性
	 * 
	 * @return
	 */
	public static String[] getLaborInsEncryptionAttributes() {
		return new String[] { "glbdef2", "glbdef3", "glbdef4", "glbdef7", "glbdef8" };
	}

	/**
	 * 健保加密屬性
	 * 
	 * @return
	 */
	public static String[] getHealthInsEncryptionAttributes() {
		return new String[] { "glbdef6", "glbdef7", "glbdef16" };
	}

	/**
	 * 勞健保計算明細加密屬性
	 * 
	 * @return
	 */
	public static String[] getNhiDetailEncryptionAttributes() {
		return new String[] { "glbdef3", "glbdef4", "glbdef5", "glbdef6", "glbdef7", "glbdef8", "glbdef9", "glbdef10",
				"glbdef11", "glbdef12", "glbdef13", "glbdef14", "glbdef15", "glbdef16", "glbdef17", "glbdef18",
				"glbdef19", "glbdef20", "glbdef21", "glbdef22", "glbdef23", "glbdef24", "glbdef25", "glbdef44",
				"glbdef27", "glbdef28", "glbdef29", "glbdef30", "glbdef31", "glbdef32", "glbdef33", "glbdef34",
				"glbdef35", "glbdef36", "glbdef45", "glbdef39" };
	}

	/**
	 * 勞健保計算彙總加密屬性
	 * 
	 * @return
	 */
	public static String[] getNhiSumEncryptionAttributes() {
		return new String[] { "glbdef3", "glbdef4", "glbdef5", "glbdef6", "glbdef7", "glbdef8", "glbdef9", "glbdef10",
				"glbdef11", "glbdef12", "glbdef13", "glbdef15", "glbdef16", "glbdef26", "glbdef17", "glbdef19",
				"glbdef20", "glbdef21", "glbdef22", "glbdef14" };
	}

	/**
	 * 團保加保加密屬性
	 * 
	 * @return
	 */
	public static String[] getGroupInsEncryptionAttributes() {
		return new String[] { "glbdef6" };
	}

	/**
	 * 定調資加密屬性
	 * 
	 * @return
	 */
	public static String[] getWaDocEncryptionAttributes() {
		return new String[] { "nmoney" };
	}

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
