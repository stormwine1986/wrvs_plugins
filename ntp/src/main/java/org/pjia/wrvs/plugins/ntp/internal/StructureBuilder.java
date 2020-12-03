package org.pjia.wrvs.plugins.ntp.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.pjia.wrvs.plugins.ntp.model.Column;
import org.pjia.wrvs.plugins.ntp.model.ColumnConfig;
import org.pjia.wrvs.plugins.ntp.model.Message;
import org.pjia.wrvs.plugins.ntp.model.Model;
import org.pjia.wrvs.plugins.ntp.model.Signal;
import org.pjia.wrvs.plugins.ntp.model.Structure;

/**
 * 文档结构构造者
 * 
 * @author pjia
 *
 */
public class StructureBuilder {

	/**
	 * 构造结构
	 * 
	 * @param sheet
	 * @return
	 */
	public Structure build(Sheet sheet) {
		// 构建列配置
		Row headerRow = sheet.getRow(0);
		ColumnConfig config = analysisColumns(headerRow);
		// 构建 message - signal 结构
		List<Message> result = new ArrayList<>(10);
    	Column colMessageName = config.getColumnByName(Model.STRUCTURE_MESSAGE);
    	Column colSignalName = config.getColumnByName(Model.STRUCTURE_SIGNAL);
    	for(int i = 1; i <= sheet.getLastRowNum(); i++) {
    		Row row = sheet.getRow(i);
    		String messageName = getColumnValue(row, colMessageName);
    		String signalName = getColumnValue(row, colSignalName);
    		if(!"".equals(messageName)) {
    			// 发现新的 message 
    			Message message = new Message(messageName);
    			message.setRow(row);
    			result.add(message);
    		}
    		// 获取最后一个 message 作为当前的 message
    		Message message = result.get(result.size() - 1);
    		if(!"".equals(signalName)) {
    			// 发现新的 signal 
    			Signal signal = new Signal(signalName);
    			message.addSignal(signal);
    		}
    		// 获取最后一个 signal 获取当前的 signal
    		Signal signal = message.getLastestSignal();
    		signal.addRow(row);
    	}
		return new Structure(result, config);
	}
	
	private static String getColumnValue(Row row, Column column) {
		Cell cell = row.getCell(column.getIndex());
		if(cell == null) return ""; // 空值直接返回
		if(CellType.BLANK.equals(cell.getCellType())) {
			// 返回空字符串
			return "";
		} else if(CellType.NUMERIC.equals(cell.getCellType())) {
			// 数字类型，转换成文本类型
			return String.valueOf(cell.getNumericCellValue());
		} else {
			// 默认按照文本类型获取
			return cell.getStringCellValue();
		}
	}
    
    private static ColumnConfig analysisColumns(Row headerRow) {
    	List<Column> result = new ArrayList<>(10);
    	for(int i = 0; i < headerRow.getLastCellNum(); i++) {
    		Cell cell = headerRow.getCell(i);
    		if(CellType.STRING.equals(cell.getCellType())){
    			String attrName = cell.getStringCellValue();
    			result.add(new Column(attrName.trim(), cell.getColumnIndex()));
    		}
    	}
		return new ColumnConfig(result);
	}
    
    /**
     * 工厂方法
     * @return
     */
    public static StructureBuilder create() {
    	return new StructureBuilder();
    }
}
