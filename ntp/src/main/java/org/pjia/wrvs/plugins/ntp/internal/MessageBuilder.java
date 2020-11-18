package org.pjia.wrvs.plugins.ntp.internal;

import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.pjia.wrvs.plugins.ntp.model.Column;
import org.pjia.wrvs.plugins.ntp.model.ColumnConfig;
import org.pjia.wrvs.plugins.ntp.model.Message;
import org.pjia.wrvs.plugins.ntp.model.Signal;
import org.pjia.wrvs.plugins.ntp.model.Structure;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Message 对象构造者
 * 
 * @author pjia
 *
 */
public class MessageBuilder {

	/**
	 * 构造
	 * 
	 * @param messages messages 结构
	 * @param columnConfig 列配置
	 */
	public static List<Message> build(Structure structure) {
		List<Message> messages = structure.getMessages();
		ColumnConfig config = structure.getConfig();
		for(Message message :messages) {
			build(message, config);
			List<Signal> signals = message.getSignals();
			for(Signal signal :signals) {
				build(signal, config);
			}
		}
		return structure.getMessages();
	}
	
	private static void build(Message message, ColumnConfig columns) {
		Row row = message.getRow();
		message.setId(getColumnValue(row, columns.getColumnByName("Message ID")));
		message.setCycleTime(getColumnValue(row, columns.getColumnByName("Cycle time [ms]")));
		message.setSendType(getColumnValue(row, columns.getColumnByName("Send Type")));
		message.setMessageLength(getColumnValue(row, columns.getColumnByName("Message Length [Byte]")));
	}

	private static void build(Signal signal, ColumnConfig columns) {
		Row row = signal.getRowScope().get(0);
		signal.setByteNumber(getColumnValue(row, columns.getColumnByName("Byte Number")));
		signal.setBitNumber(getColumnValue(row, columns.getColumnByName("Bit Number")));
		signal.setSignalLength(getColumnValue(row, columns.getColumnByName("Signal Length [Bit]")));
		signal.setStartBitNo(getColumnValue(row, columns.getColumnByName("Start Bit-No")));
		signal.setEventOfSignal(getColumnValue(row, columns.getColumnByName("Event of signal")));
		signal.setExternalConditions(getColumnValue(row, columns.getColumnByName("External Conditions")));
		signal.setSignalDescription(getColumnValue(row, columns.getColumnByName("Signal Description")));
		signal.setSignalInitial(getColumnValue(row, columns.getColumnByName("Signal Initial")));
		signal.setSignalInitialRemark(getColumnValue(row, columns.getColumnByName("Signal Initial Remark")));
		signal.setInvalidValue(getColumnValue(row, columns.getColumnByName("Invalid Value")));
		signal.setInvalidValueRemark(getColumnValue(row, columns.getColumnByName("Invalid Value Remark")));
		signal.setPhysicalRange(getColumnValue(row, columns.getColumnByName("Physical Range")));
		signal.setNormal(getColumnValue(row, columns.getColumnByName("Normal")));
		signal.setPhysicalResolution(getColumnValue(row, columns.getColumnByName("Physical Resolution")));
		// 构建发送方/接收方信息
		buildRSInfo(signal, columns);
		// 构建 bit 矩阵
		if(signal.getRowScope().size() > 1) {
			buildMatrix(signal);
		}
	}
	
	private static void buildRSInfo(Signal signal, ColumnConfig columns) {
		JsonObject jsonObject = new JsonObject();
		int startIndex = columns.getColumnByName("Invalid Value Remark").getIndex() + 1; // 信息区块开始的位置
		int endIndex = columns.getColumnByName("Physical Range").getIndex() - 1; // 信息区块结束的位置
		Row row = signal.getRowScope().get(0);
		for(int i = startIndex; i <= endIndex; i++) {
			Column column = columns.getColumnByIndex(i);
			if(column != null) {
				String value = getColumnValue(row, column);
				jsonObject.addProperty(column.getName(), value);				
			}
		}
		signal.setRsInfo(jsonObject);
	}

	private static void buildMatrix(Signal signal) {
		Row row = signal.getRowScope().get(1);
		// 找到 Function 所在的单元格作为 end
		int endIndex = getFunctionCellIndex(row);
		// Function 的位置 - 信号长度就是矩阵的起始位置
		int startIndex = endIndex - Integer.valueOf(signal.getSignalLength());
		if(endIndex > 0) {
			JsonArray matrix = new JsonArray();
			for(int i = 1; i <= signal.getRowScope().size() - 2; i++) {
				Row matrixRow = signal.getRowScope().get(i);
				JsonArray jsonArray = new JsonArray();
				for(int j = startIndex; j <= endIndex; j++) {
					jsonArray.add(getStringValue(matrixRow.getCell(j)));
				}
				matrix.add(jsonArray);
			}
			signal.setBitMatrix(matrix);
		}
	}

	private static String getStringValue(Cell cell) {
		if(cell == null) { return ""; }
		CellType cellType = cell.getCellType();
		if(CellType.BLANK.equals(cellType)) {
			// 返回空字符串
			return "";
		}else if(CellType.NUMERIC.equals(cellType)) {
			// 数字类型，转换成文本类型
			return String.valueOf(cell.getNumericCellValue());
		}else {
			// 默认按照文本类型获取
			return cell.getStringCellValue();
		}
	}

	private static int getFunctionCellIndex(Row row) {
		Iterator<Cell> cellIterator = row.cellIterator();
		while(cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			if(!cell.getCellType().equals(CellType.BLANK) && "Function".equals(cell.getStringCellValue())) {
				return cell.getColumnIndex();
			}
		}
		return -1;
	}

	private static String getColumnValue(Row row, Column column) {
		if(column == null) {
			// 不存在的列，返回空字符串
			return "";
		}
		Cell cell = row.getCell(column.getIndex());
		return getStringValue(cell);
	}
}
