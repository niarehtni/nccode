package nc.bs.hrsms.ta.sss.calendar.lsnr;

import nc.uap.lfw.core.comp.IWebPartContentFetcher;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;

public class WorkCalendarHtmlProvider implements IWebPartContentFetcher {
	/**
	 * 日历显示的DIV
	 */
	@Override
	public String fetchHtml(UIMeta um, LfwWindow pm, LfwView view) {

		StringBuffer html = new StringBuffer("<div id=\"calendar\" style=\"margin-top:0px;margin-left:10px;\">");
		/** 日历标题 */
		html.append("<table width=\"95%\" height=\"50px\">");
		html.append("<tr>");
		html.append("<td width=\"30%\"></td>");
		
		html.append("<td align=\"center\">");
		html.append("  <table width=\"100%\">");
		html.append("  <tr>");
		html.append("  <td align=\"right\"><div id=\"lastMonth\" onclick=\"lastMonthClick()\"><</div></td>");//上个月
		html.append("  <td id=\"yearMonthInfo\" width=\"150px\" align=\"center\"></td>");// 当前年月
		html.append("  <td align=\"left\"><div id=\"nextMonth\" onclick=\"nextMonthClick()\">></div></td>");//下个月 
		html.append("  </tr>");
		html.append("  </table>");
		html.append("</td>");
		
		html.append("<td width=\"30%\" align=\"right\"><div id=\"today\" onclick=\"todayClick()\">");
		html.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0130")/*@res "今天"*/);
		html.append("</div></td>");
		
		html.append("</tr>");
		html.append("</table>");
		
		/** 日历表格开始 */
		html.append("<table id=\"calTable\" width=\"95%\">");
		/** 主题行 */
		html.append("<tr>");
		html.append("<td class=\"weekTitle\">");
		html.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0131")/*@res "周日"*/);
		html.append("</td>");
		html.append("<td class=\"weekTitle\">");
		html.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0132")/*@res "周一"*/);
		html.append("</td>");
		html.append("<td class=\"weekTitle\">");
		html.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0133")/*@res "周二"*/);
		html.append("</td>");
		html.append("<td class=\"weekTitle\">");
		html.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0134")/*@res "周三"*/);
		html.append("</td>");
		html.append("<td class=\"weekTitle\">");
		html.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0135")/*@res "周四"*/);
		html.append("</td>");
		html.append("<td class=\"weekTitle\">");
		html.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0136")/*@res "周五"*/);
		html.append("</td>");
		html.append("<td class=\"weekTitle\">");
		html.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0137")/*@res "周六"*/);
		html.append("</td>");
		html.append("</tr>");
		
		int row = 6;
		int col = 7;
		int cellIndex = 0;
		for (int i = 0; i < row; i++) {
			html.append("<tr>");
			for (int j = 0; j < col; j++) {
				html.append("<td class=\"calBox\" width=\"14%\" id=\"calBox").append(cellIndex).append("\">");
				if (j == 0 || j == 6) {// 周末
					html.append("  <table id=\"weekend").append(cellIndex).append("\" border='0' width=\"100%\" height=\"100%\">");
				}else {
					html.append("  <table id=\"week").append(cellIndex).append("\" border='0' width=\"100%\" height=\"100%\">");
				}
				html.append("	<tr>");
				html.append("	  <td id=\"date").append(cellIndex).append("\" class=\"date\" height=\"40%\" width=\"50%\"></td>");
				html.append("	  <td id=\"lunar").append(cellIndex).append("\" class=\"lunar\"></td>");
				html.append("	</tr>");
				html.append("	<tr>");
				html.append("	  <td id=\"shift").append(cellIndex).append("\" class=\"shift\" align=\"center\" colspan=\"2\"></td>");
				html.append("	</tr>");
				html.append("  </table>");
				html.append("</td>");
				cellIndex++;
			}
			html.append("</tr>");
		}
		/** 日历表格结束 */
		html.append("</table>");
		html.append("</div>");
		return html.toString();
	}

	@Override
	public String fetchBodyScript(UIMeta um, LfwWindow pm, LfwView view) {
		return "";
	}

}
