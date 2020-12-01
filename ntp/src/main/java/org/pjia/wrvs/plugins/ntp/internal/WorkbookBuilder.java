package org.pjia.wrvs.plugins.ntp.internal;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.pjia.wrvs.plugins.ntp.model.Column;
import org.pjia.wrvs.plugins.ntp.model.ColumnConfig;
import org.pjia.wrvs.plugins.ntp.model.DataSet;
import org.pjia.wrvs.plugins.ntp.model.Message;
import org.pjia.wrvs.plugins.ntp.model.Model;
import org.pjia.wrvs.plugins.ntp.model.Signal;
import org.pjia.wrvs.plugins.ntp.model.Template;
import org.pjia.wrvs.plugins.ntp.model.Templates;
import org.pjia.wrvs.plugins.ntp.ui.ProgressEvent;
import org.pjia.wrvs.plugins.ntp.utils.StyleUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Excel 表格构造者
 * 
 * @author pjia
 *
 */
public class WorkbookBuilder {
	
	/**
	 * 从 DataSet 构造 Workbook
	 * 
	 * @param template 
	 * @param event 
	 * @return
	 * @throws IOException 
	 */
	public static Workbook build(DataSet dataSet, Template template, ProgressEvent event) throws IOException {
		ColumnConfig config = createColumnConfig(dataSet, template);
		event.updateEvent("正在装载模板 ...");
		Workbook wb = loadTemplateFromServer(template);
		event.updateEvent("正在生成文件 ...");
		buildHistory(wb, dataSet);
		buildChassis(dataSet, config, wb);
		buildNMMessageSheet(wb, config);
		return wb;
	}

	private static void buildChassis(DataSet dataSet, ColumnConfig config, Workbook wb) {
		Sheet ptSheet = initChassisSheet(dataSet, wb);
		writeDataToChassisSheet(dataSet, config, ptSheet);
		renderChassisSheet(ptSheet, dataSet, config);
	}

	private static Workbook loadTemplateFromServer(Template template) throws IOException {
		try(InputStream is = new FileInputStream(template.getTempFile())){
			return new HSSFWorkbook(is);		
		}
	}

	private static void buildHistory(Workbook wb, DataSet dataSet) {
		Sheet sheet = wb.getSheet("History");
		if(sheet != null) {
			HistoryBuilder.create().build(dataSet.getSegment().getHistory(), sheet);			
		}
	}

	private static void buildNMMessageSheet(Workbook wb, ColumnConfig config) {
		Sheet sheet = wb.getSheet("NM Message");
		Iterator<Column> iter = config.getColumns().iterator();
		while(iter.hasNext()) {
			Column column = iter.next();
			if(isHeaderColumn(column)) {
				Row row = sheet.createRow(column.getIndex());
				Cell cell = row.createCell(0, CellType.STRING);
				cell.setCellValue(column.getName());
			}
		}
	}

	private static void renderChassisSheet(Sheet sheet, DataSet dataSet, ColumnConfig config) {
		StyleUtil.applyHeaderStyle(sheet.getRow(0), config);
		// 冻结首行
		sheet.createFreezePane( 0, 1, 0, 1 );
		// 激活 PT Sheet
		Workbook workbook = sheet.getWorkbook();
		workbook.setActiveSheet(workbook.getSheetIndex(sheet));
		/**
		List<Message> messages = dataSet.getMessages();
		for(Message message :messages) {
			// 先渲染 Signal 样式
			List<Signal> signals = message.getSignals();
			for(Signal signal :signals) {
				StyleUtil.applySignalStyle(signal, config, sheet);
			}
			// 再渲染 message 样式
			StyleUtil.applyMessageStyle(message, config, sheet);
		}
		**/
	}

	private static void writeDataToChassisSheet(DataSet dataSet, ColumnConfig config, Sheet ptSheet) {
		buildHeader(ptSheet, config);
		List<Message> messages = dataSet.getMessages();
		for(Message message :messages) {
			// message 数据
			setValue(message.getRow(), config.getColumnByName("Message ID"), message.getId());
			setValue(message.getRow(), config.getColumnByName("Message Name"), message.getName());
			setValue(message.getRow(), config.getColumnByName("Cycle time [ms]"), message.getCycleTime());
			setValue(message.getRow(), config.getColumnByName("Send Type"), message.getSendType());
			setValue(message.getRow(), config.getColumnByName("Message Length [Byte]"), message.getMessageLength());
			List<Signal> signals = message.getSignals();
			for(Signal signal :signals) {
				// signal 数据
				buildSignal(signal, config);
			}
		}
		
	}

	private static void buildSignal(Signal signal, ColumnConfig config) {
		Row row = signal.getRowScope().get(0);
		setValue(row, config.getColumnByName("Byte Number"), signal.getByteNumber());
		setValue(row, config.getColumnByName("Bit Number"), signal.getBitNumber());
		setValue(row, config.getColumnByName("Signal Length [Bit]"), signal.getSignalLength());
		setValue(row, config.getColumnByName("Start Bit-No"), signal.getStartBitNo());
		setValue(row, config.getColumnByName("Event of signal"), signal.getEventOfSignal());
		setValue(row, config.getColumnByName("External Conditions"), signal.getExternalConditions());
		setValue(row, config.getColumnByName("Signal Name"), signal.getName());
		setValue(row, config.getColumnByName("Signal Description"), signal.getSignalDescription());
		setValue(row, config.getColumnByName("Signal Initial"), signal.getSignalInitial());
		setValue(row, config.getColumnByName("Signal Initial Remark"), signal.getSignalInitialRemark());
		setValue(row, config.getColumnByName("Invalid Value"), signal.getInvalidValue());
		setValue(row, config.getColumnByName("Invalid Value Remark"), signal.getInvalidValueRemark());
		setRsInfoValue(row, config, signal.getRsInfo());
		setValue(row, config.getColumnByName("Physical Range"), signal.getPhysicalRange());
		setValue(row, config.getColumnByName("Normal"), signal.getNormal());
		setValue(row, config.getColumnByName("Physical Resolution"), signal.getPhysicalResolution());
		setMatrixValue(signal, config);
	}

	private static void setMatrixValue(Signal signal, ColumnConfig config) {
		JsonArray bitMatrix = signal.getBitMatrix();
		if(bitMatrix != null) {
			List<Column> cols = buildMatrixHeader(config, bitMatrix.get(0).getAsJsonArray(), signal.getRowScope().get(1));
			for(int i = 1; i < bitMatrix.size(); i++) {
				// matrix 行
				Row row = signal.getRowScope().get(i + 1);
				JsonArray line = bitMatrix.get(i).getAsJsonArray();
				for(int j = 0; j < line.size(); j++) {
					// matrix 每行的列
					Column column = cols.get(j);
					Cell cell = row.createCell(column.getIndex(), CellType.STRING);
					cell.setCellValue(line.get(j).getAsString());
				}
			}
		}
		
	}

	private static List<Column> buildMatrixHeader(ColumnConfig config, JsonArray header, Row row) {
		List<Column> result = new ArrayList<>(10);
		for(int i = 0; i < header.size(); i++) {
			String title = header.get(i).getAsString();
			Column column = config.getColumnByName(title);
			result.add(column);
			Cell cell = row.createCell(column.getIndex(), CellType.STRING);
			cell.setCellValue(title);
		}
		// 返回输出 Matrix 需要的列配置
		return result;
	}

	private static void setRsInfoValue(Row row, ColumnConfig config, JsonObject rsInfo) {
		if(rsInfo != null) {
			Set<String> keySet = rsInfo.keySet();
			for(String key :keySet) {
				Column column = config.getColumnByName(key);
				Cell cell = row.createCell(column.getIndex(), CellType.STRING);
				cell.setCellValue(rsInfo.get(key).getAsString());
			}
		}
	}

	private static void buildHeader(Sheet ptSheet, ColumnConfig config) {
		Row headerRow = ptSheet.createRow(0);
		Iterator<Column> iter = config.getColumns().iterator();
		while(iter.hasNext()) {
			Column column = iter.next();
			if(isHeaderColumn(column)) {
				Cell cell = headerRow.createCell(column.getIndex(), CellType.STRING);
				cell.setCellValue(column.getName());					
			}
		}
	}

	private static boolean isHeaderColumn(Column column) {
		String name = column.getName();
		Pattern pattern = Pattern.compile("^b[\\d+]$");
		return !name.equals("Function") && !pattern.matcher(name).matches();
	}

	private static void setValue(Row row, Column column, String value) {
		try {
			if(column == null) return; // 无效列直接忽略
			Cell cell = row.createCell(column.getIndex(), CellType.STRING);
			cell.setCellValue(value);			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Sheet initChassisSheet(DataSet dataSet, Workbook wb) {
		Sheet ptSheet = wb.getSheet(Model.SHEET_NAME_CHASSIS);
		int rIndex = 1;
		List<Message> messages = dataSet.getMessages();
		for(Message message :messages) {
			List<Signal> signals = message.getSignals();
			for(Signal signal :signals) {
				// 计算信号占用的行数
				int amount = getRowAmount(signal);
				for(int i = 0; i < amount; i++) {
					Row row = ptSheet.createRow(rIndex++);
					// 为信号分配 row
					signal.addRow(row);
				}
			}
			// 设置 message 的首行
			Row row = message.getSignals().get(0).getRowScope().get(0);
			message.setRow(row);
		}
		return ptSheet;
	}

	private static int getRowAmount(Signal signal) {
		JsonArray bitMatrix = signal.getBitMatrix();
		// 没有矩阵的信号占一行
		if(bitMatrix == null) { return 1; }
		// 有矩阵的信号，占 2 + 矩阵行数
		return 2 + bitMatrix.size();
	}

	private static ColumnConfig createColumnConfig(DataSet dataSet, Template template) {
		List<Column> columns = new ArrayList<>(10);
		ColumnConfig config = new ColumnConfig(columns);
		// 组装 Excel 列配置
		config.addColumn(new Column("Message ID"));
		config.addColumn(new Column("Message Name"));
		config.addColumn(new Column("Cycle time [ms]"));
		config.addColumn(new Column("Send Type"));
		config.addColumn(new Column("Message Length [Byte]"));
		config.addColumn(new Column("Byte Number"));
		config.addColumn(new Column("Bit Number"));
		config.addColumn(new Column("Signal Length [Bit]"));
		config.addColumn(new Column("Start Bit-No"));
		config.addColumn(new Column("Event of signal"));
		if(Templates.HQ.equals(template.getDisplay())) {
			config.addColumn(new Column("External Conditions"));		
		}
		config.addColumn(new Column("Signal Name"));
		config.addColumn(new Column("Signal Description"));
		config.addColumn(new Column("Signal Initial"));
		config.addColumn(new Column("Signal Initial Remark"));
		config.addColumn(new Column("Invalid Value"));
		if(Templates.HQ.equals(template.getDisplay())) {
			config.addColumn(new Column("Invalid Value Remark"));
		}
		buildRSCols(dataSet, config);
		config.addColumn(new Column("Physical Range"), 5);
		buildMatrixCols(dataSet, config);
		config.addColumn(new Column("Normal"), 4);
		config.addColumn(new Column("Physical Resolution"), 9);
		config.addColumn(new Column(""), 4);
		return config;
	}

	private static void buildMatrixCols(DataSet dataSet, ColumnConfig config) {
		List<String> keys = getMatrixKeys(dataSet);
		for(String key: keys) {
			config.addColumn(new Column(key));
		}
	}

	private static List<String> getMatrixKeys(DataSet dataSet) {
		List<String> matrixkeys = new ArrayList<>();
		// 计算出 matrix 表格最大宽度
		List<Message> messages = dataSet.getMessages();
		for(Message message :messages) {
			List<Signal> signals = message.getSignals();
			for(Signal signal :signals) {
				JsonArray bitMatrix = signal.getBitMatrix();
				if(bitMatrix != null && bitMatrix.size() > 0) {
					int size = bitMatrix.get(0).getAsJsonArray().size();
					if(!matrixkeys.contains("Function")) matrixkeys.add("Function");
					for(int i = 0; i < size - 1; i++) {
						String flag = "b" + i;
						if(!matrixkeys.contains(flag)) matrixkeys.add(flag);
					}
				}
			}
		}
		
		Collections.reverse(matrixkeys);
		return matrixkeys;
	}

	private static void buildRSCols(DataSet dataSet, ColumnConfig config) {
		Set<String> rsKeys = totalRSKey(dataSet);
		for(String rskey :rsKeys) {
			config.addColumn(new Column(rskey));
		}
	}

	private static Set<String> totalRSKey(DataSet dataSet) {
		List<Message> messages = dataSet.getMessages();
		Set<String> rsKeys = new HashSet<>();
		for(Message message :messages) {
			List<Signal> signals = message.getSignals();
			for(Signal signal :signals) {
				JsonObject rsInfo = signal.getRsInfo();
				if(rsInfo != null) {
					rsKeys.addAll(rsInfo.keySet());
				}
			}
		}
		
		return rsKeys;
	}

}
