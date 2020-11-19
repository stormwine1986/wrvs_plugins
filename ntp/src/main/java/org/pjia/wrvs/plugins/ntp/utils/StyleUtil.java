package org.pjia.wrvs.plugins.ntp.utils;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.pjia.wrvs.plugins.ntp.model.Column;
import org.pjia.wrvs.plugins.ntp.model.ColumnConfig;
import org.pjia.wrvs.plugins.ntp.model.DataSet;
import org.pjia.wrvs.plugins.ntp.model.Message;
import org.pjia.wrvs.plugins.ntp.model.Signal;

/**
 * Excel 样式
 * 
 * @author pjia
 *
 */
public class StyleUtil {

	/**
	 * 应用表头样式
	 * 
	 * @param row
	 * @param config 
	 */
	public static void applyHeaderStyle(Row row, ColumnConfig config) {
		Workbook workbook = row.getSheet().getWorkbook();
		// 边框样式
		CellStyle bg = workbook.createCellStyle();
		bg.setBorderBottom(BorderStyle.THIN);
		//// 字体：加粗，9号，Calibri
		Font font = workbook.createFont();
		font.setBold(true);
        font.setFontHeightInPoints((short) 9);
        font.setFontName("Calibri");
        bg.setFont(font);
        
        int endIndex = config.getEndColumn().getIndex();
        for(int i = 0; i <= endIndex; i++) {
        	Cell cell = row.getCell(i);
        	if(cell == null) {
        		// 空单元格填充，为了应用样式
        		cell = row.createCell(i);
        	}
        	cell.setCellStyle(bg);
        }
	}

	/**
	 * 应用 Message 样式
	 * 
	 * @param message
	 * @param config
	 * @param sheet 
	 */
	public static void applyMessageStyle(Message message, ColumnConfig config, Sheet sheet) {
		Workbook workbook = sheet.getWorkbook();
		// 底部细边框
		CellStyle bg = workbook.createCellStyle();
		bg.setBorderBottom(BorderStyle.THIN);
		
		Row row = message.getLastestSignal().getLastestRow();
		int endIndex = config.getEndColumn().getIndex();
        for(int i = 0; i <= endIndex; i++) {
        	Cell cell = row.getCell(i);
        	if(cell == null) {
        		// 空单元格填充，为了应用样式
        		cell = row.createCell(i);
        	}
        	cell.setCellStyle(bg);
        }
	}

	/**
	 * 应用 Signal 样式
	 * 
	 * @param signal
	 * @param config
	 * @param sheet
	 */
	public static void applySignalStyle(Signal signal, ColumnConfig config, Sheet sheet) {
		Workbook workbook = sheet.getWorkbook();
		// 底部细边框
		CellStyle bg = workbook.createCellStyle();
		bg.setBorderBottom(BorderStyle.HAIR);
		
		Row row = signal.getLastestRow();
		Column startColumn = config.getColumnByName("Byte Number");
		Column endColumn = config.getEndColumn();
		for(int i = startColumn.getIndex(); i <= endColumn.getIndex(); i++) {
			Cell cell = row.getCell(i);
        	if(cell == null) {
        		// 空单元格填充，为了应用样式
        		cell = row.createCell(i);
        	}
        	cell.setCellStyle(bg);
		}
	}

	/**
	 * 清除边框
	 * 
	 * @param sheet
	 * @param dataSet
	 * @param config
	 */
	public static void clearBorder(Sheet sheet, DataSet dataSet, ColumnConfig config) {
		Workbook workbook = sheet.getWorkbook();
		CellStyle bg = workbook.createCellStyle();
		bg.setBorderRight(BorderStyle.HAIR);
		
		Column latestCol = config.getEndColumn();
		Row lastestRow = dataSet.getLastestMessage().getLastestSignal().getLastestRow();
		
		for(int r = 0; r <= lastestRow.getRowNum(); r++) {
			Row rr = sheet.getRow(r);
			Cell cell = rr.getCell(latestCol.getIndex());
			if(cell == null) {
				// 空单元格用来应用样式
				cell = rr.createCell(latestCol.getIndex());
			}
			cell.setCellStyle(bg);
			cell.setCellValue("1");
		}
	}

}
