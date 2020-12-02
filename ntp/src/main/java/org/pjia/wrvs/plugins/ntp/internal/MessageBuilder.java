package org.pjia.wrvs.plugins.ntp.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pjia.wrvs.plugins.client.WRVSLocalClient;
import org.pjia.wrvs.plugins.event.Event;
import org.pjia.wrvs.plugins.event.PluginEventMgr;
import org.pjia.wrvs.plugins.ntp.model.Column;
import org.pjia.wrvs.plugins.ntp.model.ColumnConfig;
import org.pjia.wrvs.plugins.ntp.model.DataSet;
import org.pjia.wrvs.plugins.ntp.model.Message;
import org.pjia.wrvs.plugins.ntp.model.Node;
import org.pjia.wrvs.plugins.ntp.model.Segment;
import org.pjia.wrvs.plugins.ntp.model.Signal;
import org.pjia.wrvs.plugins.ntp.model.Structure;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mks.api.Command;
import com.mks.api.Option;
import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;

import lombok.extern.slf4j.Slf4j;

/**
 * Message 对象构造者
 * 
 * @author pjia
 *
 */
@Slf4j
public class MessageBuilder {

	/**
	 * 从 Structure 构造 DataSet
	 * 
	 * @param structure structure 结构
	 */
	public static DataSet build(Structure structure) {
		List<Message> messages = structure.getMessages();
		ColumnConfig config = structure.getConfig();
		for(Message message :messages) {
			build(message, config);
			List<Signal> signals = message.getSignals();
			for(Signal signal :signals) {
				build(signal, config);
			}
		}
		return new DataSet(structure.getMessages());
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
		// HQ 有 Invalid Value Remark 列，BT 只有 Invalid Value 列，这里适配了两种情况
		Column base = (columns.getColumnByName("Invalid Value Remark") == null? 
				columns.getColumnByName("Invalid Value"): columns.getColumnByName("Invalid Value Remark"));
		int startIndex = base.getIndex() + 1; // 信息区块开始的位置
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
	
	/**
	 * 从 Segment 构建 DataSet
	 * 
	 * @param segment segment
	 * @param event 
	 * @return DataSet
	 * @throws APIException 
	 */
	public static DataSet build(Segment segment, WRVSLocalClient localClient) {
		// 选择所有非 heading 条目
		List<Node> nodes = segment.getNodes().stream()
			.filter(node -> !"Heading".equals(node.getCategroty()))
			.collect(Collectors.toList());
		// event.updateProgress("正在读取内容", 0, nodes.size());
		PluginEventMgr.recordEvent(new Event("正在读取内容", 0, nodes.size()));
		AtomicInteger i = new AtomicInteger(0);
		List<Signal> signals = new ArrayList<>();
		for(Node node :nodes) {
			Signal signal = build(node, localClient);
			if(signal != null) {
				signals.add(signal);				
			}
			// event.updateProgress("正在读取内容", i.addAndGet(1), nodes.size());
			PluginEventMgr.recordEvent(new Event("正在读取内容", i.addAndGet(1), nodes.size()));
		}
		
		DataSet dataSet = buildDataSet(signals);
		dataSet.apply(segment);
		return dataSet;
	}

	private static DataSet buildDataSet(List<Signal> signals) {
		List<Message> messages = new ArrayList<>();
		for(Signal signal :signals) {
			// Group By message
			Message message = signal.getMessage();
			String messageName = message.getName();
			Optional<Message> optional = messages.stream().filter(m -> messageName.equals(m.getName())).findFirst();
			if(optional.isPresent()) {
				optional.get().addSignal(signal);
			} else {
				messages.add(message);
				message.addSignal(signal);
			} 
		}
		return new DataSet(messages);
	}

	private static Signal build(Node node, WRVSLocalClient localClient) {
		try {
			Command cmd = new Command("im", "viewissue");
			cmd.addOption(new Option("showRichContent"));
			cmd.addSelection(node.getId());
			Response response = localClient.execute(cmd);
			WorkItem workItem = response.getWorkItem(node.getId());
			// 组装 Signal 对象
			String signalName = getFieldStringValue(workItem.getField("Signal Name"));
			Signal signal = new Signal(signalName);
			signal.setByteNumber(getFieldStringValue(workItem.getField("Byte Number")));
			signal.setBitNumber(getFieldStringValue(workItem.getField("Bit Number")));
			signal.setSignalLength((getFieldStringValue(workItem.getField("Signal Length"))));
			signal.setStartBitNo((getFieldStringValue(workItem.getField("Start Bit No"))));
			signal.setEventOfSignal((getFieldStringValue(workItem.getField("Event of signal"))));
			signal.setSignalDescription((getFieldStringValue(workItem.getField("Signal Description"))));
			signal.setSignalInitial((getFieldStringValue(workItem.getField("Signal Initial"))));
			signal.setSignalInitialRemark((getFieldStringValue(workItem.getField("Signal Initial Remark"))));
			signal.setInvalidValue((getFieldStringValue(workItem.getField("Invalid Value"))));
			signal.setPhysicalRange((getFieldStringValue(workItem.getField("Physical Range"))));
			signal.setPhysicalResolution((getFieldStringValue(workItem.getField("Physical Resolution"))));
			signal.setExternalConditions((getFieldStringValue(workItem.getField("External Conditions"))));
			signal.setInvalidValueRemark((getFieldStringValue(workItem.getField("Invalid Value Remark"))));
			buildNormal(signal, workItem.getField("Normal"));
			signal.setRsInfo(buildRSInfo(workItem));
			// 组装 message 对象
			String messageName = getFieldStringValue(workItem.getField("Message Name"));
			Message message = new Message(messageName);
			signal.setMessage(message);
			message.setId(getFieldStringValue(workItem.getField("Message ID")));
			message.setCycleTime(getFieldStringValue(workItem.getField("Cycle time")));
			message.setSendType(getFieldStringValue(workItem.getField("Send Type")));
			message.setMessageLength(getFieldStringValue(workItem.getField("Message Length")));
			
			return signal;
		}catch (APIException e) {
			log.error(e.getResponse().toString());
			return null;
		}
	}

	private static JsonObject buildRSInfo(WorkItem workItem) {
		JsonObject object = new JsonObject();
		Field signalReceiver = workItem.getField("Signal Receiver");
		Field signalSender = workItem.getField("Signal Sender");
		List<String> r = getPickValue(signalReceiver);
		List<String> s = getPickValue(signalSender);
		r.stream().forEach(e -> {object.addProperty(e, "r");});
		s.stream().forEach(e -> {object.addProperty(e, "s");});
		return object;
	}

	@SuppressWarnings("unchecked")
	private static List<String> getPickValue(Field pickField) {
		List<String> picks = new ArrayList<>(10);
		if(pickField != null) {
			String dataType = pickField.getDataType();
			if("com.mks.api.response.ValueList".equals(dataType)) {
				picks.addAll(pickField.getList());
			} else if("java.lang.String".equals(dataType)) {
				picks.add(pickField.getString());
			}
		}
		return picks;
	}

	private static void buildNormal(Signal signal, Field field) {
		if(field != null && StringUtils.isNotBlank(field.getValueAsString())) {
			String content = field.getValueAsString();
			if(content.indexOf("</table>") > -1) {
				Document document = Jsoup.parse(content.replace("<!-- MKS HTML -->", ""));
				JsonArray matrix = htmlTableToMatrix(document);
				signal.setBitMatrix(matrix);
			} else {
				signal.setNormal(content.replace("<!-- MKS HTML -->", "").replace("<p>", "").replace("</p>", ""));
			}
		}
	}

	private static JsonArray htmlTableToMatrix(Document document) {
		Elements trs = document.getElementsByTag("tr");
		JsonArray matrix = new JsonArray();
		for(int i = 0; i < trs.size(); i++) {
			Element tr = trs.get(i);
			Elements tds = tr.getElementsByTag("td");
			JsonArray line = new JsonArray();
			for(int j = 0; j < tds.size(); j++) {
				Element td = tds.get(j);
				line.add(td.text().trim());
			}
			matrix.add(line);
		}
		return matrix;
	}

	private static String getFieldStringValue(Field field) {
		if(field == null) return "";
		if(field.getValueAsString() == null) return "";
		return field.getValueAsString();
	}
}
