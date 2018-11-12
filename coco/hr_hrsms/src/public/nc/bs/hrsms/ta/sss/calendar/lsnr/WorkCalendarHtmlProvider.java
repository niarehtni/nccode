package nc.bs.hrsms.ta.sss.calendar.lsnr;

import nc.uap.lfw.core.comp.IWebPartContentFetcher;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;

public class WorkCalendarHtmlProvider implements IWebPartContentFetcher {
	/**
	 * ������ʾ��DIV
	 */
	@Override
	public String fetchHtml(UIMeta um, LfwWindow pm, LfwView view) {

		StringBuffer html = new StringBuffer("<div id=\"calendar\" style=\"margin-top:0px;margin-left:10px;\">");
		/** �������� */
		html.append("<table width=\"95%\" height=\"50px\">");
		html.append("<tr>");
		html.append("<td width=\"30%\"></td>");
		
		html.append("<td align=\"center\">");
		html.append("  <table width=\"100%\">");
		html.append("  <tr>");
		html.append("  <td align=\"right\"><div id=\"lastMonth\" onclick=\"lastMonthClick()\"><</div></td>");//�ϸ���
		html.append("  <td id=\"yearMonthInfo\" width=\"150px\" align=\"center\"></td>");// ��ǰ����
		html.append("  <td align=\"left\"><div id=\"nextMonth\" onclick=\"nextMonthClick()\">></div></td>");//�¸��� 
		html.append("  </tr>");
		html.append("  </table>");
		html.append("</td>");
		
		html.append("<td width=\"30%\" align=\"right\"><div id=\"today\" onclick=\"todayClick()\">");
		html.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0130")/*@res "����"*/);
		html.append("</div></td>");
		
		html.append("</tr>");
		html.append("</table>");
		
		/** �������ʼ */
		html.append("<table id=\"calTable\" width=\"95%\">");
		/** ������ */
		html.append("<tr>");
		html.append("<td class=\"weekTitle\">");
		html.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0131")/*@res "����"*/);
		html.append("</td>");
		html.append("<td class=\"weekTitle\">");
		html.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0132")/*@res "��һ"*/);
		html.append("</td>");
		html.append("<td class=\"weekTitle\">");
		html.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0133")/*@res "�ܶ�"*/);
		html.append("</td>");
		html.append("<td class=\"weekTitle\">");
		html.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0134")/*@res "����"*/);
		html.append("</td>");
		html.append("<td class=\"weekTitle\">");
		html.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0135")/*@res "����"*/);
		html.append("</td>");
		html.append("<td class=\"weekTitle\">");
		html.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0136")/*@res "����"*/);
		html.append("</td>");
		html.append("<td class=\"weekTitle\">");
		html.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0137")/*@res "����"*/);
		html.append("</td>");
		html.append("</tr>");
		
		int row = 6;
		int col = 7;
		int cellIndex = 0;
		for (int i = 0; i < row; i++) {
			html.append("<tr>");
			for (int j = 0; j < col; j++) {
				html.append("<td class=\"calBox\" width=\"14%\" id=\"calBox").append(cellIndex).append("\">");
				if (j == 0 || j == 6) {// ��ĩ
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
		/** ���������� */
		html.append("</table>");
		html.append("</div>");
		return html.toString();
	}

	@Override
	public String fetchBodyScript(UIMeta um, LfwWindow pm, LfwView view) {
		return "";
	}

}
