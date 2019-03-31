package nc.ui.wa.taxaddtional.action;

import java.io.IOException;
import java.util.Vector;

import nc.hr.utils.ResHelper;
import nc.ui.hr.datainterface.XlsDefaultExporter;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.wa_tax.TaxupgradeDef;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

/**
 * 定调资信息模板导出工具类<BR>
 * <BR>
 *
 * @author: xuhw
 * @date: 2010-4-8 下午04:23:45
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class TaxXlsExporter extends XlsDefaultExporter
{
	/** 单据模板上项目信息 */
	private BillItem[] inputItems;
	
	private boolean isPartShow;


	/***************************************************************************
	 * Created on 2012-8-1 下午2:16:45<br>
	 * @author daicy
	 ***************************************************************************/
	public TaxXlsExporter()
	{
		this.fileOut = null;
	}

	/**
	 * 核查单元值是否合法 如果不合法，则将相应的单元cell 变为红色
	 *
	 * @param item
	 * @param aValue
	 * @return
	 */
	private String getCommentContent(final BillItem item)
	{
		// 当心类型不统一
		final int datatype = item.getDataType();
		final int maxinputlength = item.getLength();
		final StringBuffer sbMessage = new StringBuffer();
		switch (datatype)
		{
			case IBillItem.DECIMAL:// 数值型
				sbMessage.append(ResHelper.getString("60130adjmtc","060130adjmtc0150")/*@res "输入类型：数值类型(31.8)\n"*/);
				sbMessage.append(ResHelper.getString("60130adjmtc","060130adjmtc0151")/*@res "输入举例：256.123\n"*/);
				sbMessage.append(ResHelper.getString("60130adjmtc","060130adjmtc0152")/*@res "最大长度："*/ + maxinputlength);
				break;
			case IBillItem.BOOLEAN:
				sbMessage.append(ResHelper.getString("60130adjmtc","060130adjmtc0153")/*@res "输入类型：布尔类型\n"*/);
				sbMessage.append(ResHelper.getString("60130adjmtc","060130adjmtc0154")/*@res "输入举例：{N}或者{Y}\n"*/);
				sbMessage.append(ResHelper.getString("60130adjmtc","060130adjmtc0152")/*@res "最大长度："*/ + maxinputlength);
				break;
			case IBillItem.DATE:
				sbMessage.append(ResHelper.getString("60130adjmtc","060130adjmtc0155")/*@res "输入类型：日期类型\n"*/);
				sbMessage.append(ResHelper.getString("60130adjmtc","060130adjmtc0156")/*@res "输入举例：2012-12-12\n"*/);
				sbMessage.append(ResHelper.getString("60130adjmtc","060130adjmtc0152")/*@res "最大长度："*/ + maxinputlength);
			case IBillItem.COMBO:
				String reftype = item.getRefType();
				sbMessage.append(reftype);
		}
		return sbMessage.toString();
	}

	public BillItem[] getInputItems()
	{
		return this.inputItems;
	}

	@Override
	protected void outPutBody() throws IOException
	{
		int nRowIdx = 0;
		if (this.getParas().isOutputHead())
		{
			nRowIdx++;
		}
		this.sheet.setDefaultColumnWidth((short) 12);
		CellStyle titleCellStyle= wb.createCellStyle();
		
		for (int index = 0; index < this.getBillmodel().getRowCount(); index++)
		{
			final Row row = this.sheet.createRow(index + nRowIdx);
			int nLineNo = 0;
			int errorPos = 99;
			if (this.getParas().isOutPutLineNo())
			{
				final Cell cell = row.createCell((short) nLineNo);
				cell.setCellValue(index + nRowIdx + "");
				this.setCellStyle4Body(titleCellStyle, cell, this.wb, errorPos, 0, (short) 3);
				nLineNo++;
			}
			int nLineNo2 = nLineNo - 2 ;
			final CircularlyAccessibleValueObject rowVO = this.getBillmodel().getBodyValueRowVO(index, this.getBodyVOName());

			final Object intErrorPso = rowVO.getAttributeValue(PsndocWadocVO.ERROR_FLAG);
			if (intErrorPso != null)
			{
				errorPos = (Integer) intErrorPso;
			}
			final BillItem[] vos = this.getInputItems();
			String value = "";
			for (int temp = 0; temp <= TaxupgradeDef.SHOW_ITEM_CNT - 1; temp++)
			{
				short shtAlignment = 1;
				final BillItem formatItemVO = vos[temp];
				Cell cell = null;
				value = "";
				final Object obj = rowVO.getAttributeValue(formatItemVO.getKey());
				if (obj != null)
				{
					value = obj.toString();
				}
				
				
				// 根据数据类型定义单元格格式
			   if (vos[temp].getDataType() == IBillItem.DECIMAL)
				{// 数字型
					cell = row.createCell((short) (temp + nLineNo), Cell.CELL_TYPE_NUMERIC);
					if (!StringUtils.isBlank(value))
					{
						cell.setCellValue(Double.parseDouble(value));
					}
					shtAlignment = 3;
				}
				else if (vos[temp].getDataType() == IBillItem.DATE && !StringUtils.isBlank(value))
				{
					// 其他的按字符类型创建
					cell = row.createCell((short) (temp + nLineNo));
					cell.setCellValue(String.valueOf(value).replace(" 00:00:00", ""));
					shtAlignment = 3;
				}else if (vos[temp].getDataType() == IBillItem.UFREF && !StringUtils.isBlank(value)){
					UIRefPane refpane = (UIRefPane) formatItemVO.getComponent();
					Vector data = refpane.getRefModel().getData();
					refpane.getRefModel().matchData(refpane.getRefModel().getPkFieldCode(), (String) value);
					if(null != refpane.getRefModel().getPkValue()){
						value = refpane.getRefModel().getRefNameValue();
					}
					
					// 其他的按字符类型创建
					cell = row.createCell((short) (temp + nLineNo));
					cell.setCellValue(String.valueOf(value));
					
				}else
				{
					// 其他的按字符类型创建
					cell = row.createCell((short) (temp + nLineNo));
					cell.setCellValue(String.valueOf(value));
				}
			   this.setCellStyle4Body(titleCellStyle,cell, this.wb, errorPos, nLineNo, shtAlignment);
			}
		}
		this.sheet.autoSizeColumn((short) 25);
		this.wb.write(this.fileOut);
	}

	/**
	 * 设置某些列的值只能输入预制的数据,显示下拉框.
	 *
	 * @param sheet  要设置的sheet.
	 * @param textlist  下拉框显示的内容
	 * @param firstRow  开始行
	 * @param endRow  结束行
	 * @param firstCol  开始列
	 * @param endCol  结束列
	 * @return 设置好的sheet.
	 */
	private static org.apache.poi.ss.usermodel.Sheet setHSSFValidation(org.apache.poi.ss.usermodel.Sheet sheet, String[] textlist, int firstRow, int endRow, int firstCol, int endCol) {
		// 加载下拉列表内容
		DVConstraint constraint = DVConstraint.createExplicitListConstraint(textlist);
		// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
		CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
		// 数据有效性对象
		HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);
		sheet.addValidationData(data_validation_list);
		return sheet;
	}
	/**
	 * 输入模板带人员编码和姓名
	 *
	 * @author xuhw on 2010-5-21
	 * @throws IOException
	 */
	protected void outPutTempletHavePsnCodeAndName() throws IOException
	{
		int nRowIdx = 0;
		if (this.getParas().isOutputHead())
		{
			nRowIdx++;
		}
		this.sheet.setDefaultColumnWidth((short) 12);
		for (int index = 0; index < this.getBillmodel().getRowCount(); index++)
		{
			final Row row = this.sheet.createRow(index + nRowIdx);
			int nLineNo = 0;
			int errorPos = 99;
			if (this.getParas().isOutPutLineNo())
			{
				final Cell cell = row.createCell((short) nLineNo);
				cell.setCellValue(index + nRowIdx + "");
				nLineNo++;
			}
			CircularlyAccessibleValueObject rowVO = this.getBillmodel().getBodyValueRowVO(index, this.getBodyVOName());

			Object intErrorPso = rowVO.getAttributeValue(PsndocWadocVO.ERROR_FLAG);
			if (intErrorPso != null)
			{
				errorPos = (Integer) intErrorPso;
			}
			BillItem[] vos = this.getInputItems();
			CellStyle titleCellStyle= wb.createCellStyle();
			for (int temp = 0; temp <= TaxupgradeDef.SHOW_ITEM_CNT ; temp++)
			{
				final BillItem formatItemVO = vos[temp];
				short shtAlignment = 1;
				Cell cell;
				String value = "";
				final Object obj = rowVO.getAttributeValue(formatItemVO.getKey());
				if (obj != null)
				{
					value = obj.toString();
				}

				// 根据数据类型定义单元格格式
				if (vos[temp].getDataType() == IBillItem.INTEGER || vos[temp].getDataType() == IBillItem.DECIMAL)
				{// 数字型
					cell = row.createCell((short) (temp + nLineNo), Cell.CELL_TYPE_NUMERIC);
					if (!StringUtils.isBlank(value))
					{
						cell.setCellValue(Double.parseDouble(value));
					}

					shtAlignment = 3;
				}
				else if (vos[temp].getDataType() == IBillItem.DATE && !StringUtils.isBlank(value))
				{
					// 其他的按字符类型创建
					cell = row.createCell((short) (temp + nLineNo));
					cell.setCellValue(String.valueOf(value).replace(" 00:00:00", ""));
					shtAlignment = 3;
				}
				else
				{
					// 其他的按字符类型创建
					cell = row.createCell((short) (temp + nLineNo));
					cell.setCellValue(String.valueOf(value));
				}
				this.setCellStyle4Body(titleCellStyle, cell, this.wb, errorPos, temp, shtAlignment);
			}
		}
		this.sheet.autoSizeColumn((short) 25);
		this.wb.write(this.fileOut);
	}

	@Override
	protected void outPutHead() throws IOException
	{
		Cell cell = null;
		Row row = null;
		if (!this.getParas().isOutputHead())
		{
			return;
		}
		row = this.sheet.createRow(0);// 表头行，也就是首行
		int nLineNo = 0;
		// 是否输出序号
		if (this.getParas().isOutPutLineNo())
		{
			cell = row.createCell((short) nLineNo);// 首个单元格
//			cell.setEncoding(Cell.ENCODING_UTF_16);
			cell.setCellValue(ResHelper.getString("common","UC000-0001821")/*@res "序号"*/);
			this.setCellStyle4Head(cell, this.wb);
			nLineNo++;
		}

		if (this.getInputItems() == null || this.getInputItems().length == 0)
		{
			return;
		}
		for (int j = 0; j < TaxupgradeDef.SHOW_ITEM_CNT; j++)
		{
			cell = row.createCell((short) (j + nLineNo));
//			cell.setEncoding(Cell.ENCODING_UTF_16);
			cell.setCellValue((this.getInputItems()[j]).getName());
			this.setCellCommentStyle(cell, (this.getInputItems()[j]));
			// 如果设置了combo属性则本列只能选择不能输入
			String refType = this.getInputItems()[j].getRefType();
			if (!StringUtils.isEmpty(refType)) {
//				IX,子女教育=1,继续教育=2,住房贷款利息=3,住房租金=4,赡养老人=5,大病医疗=6
//				S,子女教育,继续教育,住房贷款利息,住房租金,赡养老人,大病医疗
				refType = refType.replaceAll("S,", "");
				String[] attr = refType.split(",");
					setHSSFValidation(sheet, attr, 1, 1000, j+1, j+1);// 这里默认设了2-101列只能选择不能输入.
			}
			
			this.setCellStyle4Head(cell, this.wb);
		}
		
		
	}

	/**
	 * 设定批注
	 *
	 * @author xuhw on 2010-4-20
	 * @param cell
	 * @param wb
	 * @return
	 */
	private void setCellCommentStyle(Cell cell, BillItem bodyVO)
	{
	    Drawing p = this.sheet.createDrawingPatriarch();
		// 创建绘图对象
		Comment comment = null;
		String conmmntStr = this.getCommentContent(bodyVO);
		if (StringUtils.isEmpty(conmmntStr)) {
			return;
		}
		if(null != version && version.equals("2003")) {
		    comment = p.createCellComment(new HSSFClientAnchor(1, 2, 3, 4, (short) 3, 3, (short) 5, 6));
		 // 输入批注信息
	        comment.setString(new HSSFRichTextString(this.getCommentContent(bodyVO)));
	        // 添加作者,选中B5单元格,看状态栏
	        comment.setAuthor("toad");
		}else if(null != version && version.equals("2007")) {
		    comment = p.createCellComment(new XSSFClientAnchor(1, 2, 3, 4, (short) 3, 3, (short) 5, 6));
		 // 输入批注信息
	        comment.setString(new XSSFRichTextString(this.getCommentContent(bodyVO)));
	        // 添加作者,选中B5单元格,看状态栏
	        comment.setAuthor("toad");
        }
		
		// 将批注添加到单元格对象中
		cell.setCellComment(comment);
	}

//	/**
//	 * 设置单元格的式样<BR>
//	 * <BR>
//	 *
//	 * @author xuhw on 2010-4-10
//	 * @param cell
//	 * @param wb
//	 * @return
//	 */
//	private Cell setCellStyle4Body(CellStyle titleCellStyle, final Cell cell, final Workbook wb, final int intErrorPos, final int curentLineNO, short alignment)
//	{
////		CellStyle titleCellStyle = wb.createCellStyle();
//		titleCellStyle.setBorderBottom((short) 1);
//		titleCellStyle.setBorderLeft((short) 1);
//		titleCellStyle.setBorderRight((short) 1);
//		titleCellStyle.setBorderTop((short) 1);
//
//		titleCellStyle.setAlignment(alignment);
////		cell.setEncoding(Cell.ENCODING_UTF_16);
//
//		if (curentLineNO == intErrorPos)
//		{
//			final Font font = wb.createFont();
//			font.setColor(Font.COLOR_RED);
//			titleCellStyle.setFont(font);
//		}
//		cell.setCellStyle(titleCellStyle);
//		return cell;
//	}
	/*
	 * 重载setCellStyle4Body  超过4000条,会报错
	 *   修改NCdp205462957
	 */
	private Cell setCellStyle4Body(CellStyle titleCellStyle,final Cell cell, final Workbook wb, final int intErrorPos, final int curentLineNO, short alignment)
	{
		titleCellStyle.setBorderBottom((short) 1);
		titleCellStyle.setBorderLeft((short) 1);
		titleCellStyle.setBorderRight((short) 1);
		titleCellStyle.setBorderTop((short) 1);

		titleCellStyle.setAlignment(alignment);
//		cell.setEncoding(Cell.ENCODING_UTF_16);

		if (curentLineNO == intErrorPos)
		{
			final Font font = wb.createFont();
			font.setColor(Font.COLOR_RED);
			titleCellStyle.setFont(font);
		}
		cell.setCellStyle(titleCellStyle);
		return cell;
	}
	/**
	 * 设置单元格的式样<BR>
	 * <BR>
	 *
	 * @author xuhw on 2010-4-10
	 * @param cell
	 * @param wb
	 * @return
	 */
	private Cell setCellStyle4Head(Cell cell, Workbook wb)
	{
		CellStyle titleCellStyle = wb.createCellStyle();
		titleCellStyle.setBorderBottom((short) 1);
		titleCellStyle.setBorderLeft((short) 1);
		titleCellStyle.setBorderRight((short) 1);
		titleCellStyle.setBorderTop((short) 1);
//		cell.setEncoding(Cell.ENCODING_UTF_16);
		cell.setCellStyle(titleCellStyle);
		return cell;
	}

	public void setInputItems(final BillItem[] inputItems)
	{
		this.inputItems = inputItems;
	}

	public boolean isPartShow() {
		return isPartShow;
	}

	public void setPartShow(boolean isPartShow) {
		this.isPartShow = isPartShow;
	}
	
	
}