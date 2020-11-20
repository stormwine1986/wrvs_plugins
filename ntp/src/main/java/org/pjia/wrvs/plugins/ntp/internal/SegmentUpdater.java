package org.pjia.wrvs.plugins.ntp.internal;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pjia.wrvs.plugins.client.WRVSLocalClient;
import org.pjia.wrvs.plugins.ntp.model.DataSet;
import org.pjia.wrvs.plugins.ntp.model.Identifiable;
import org.pjia.wrvs.plugins.ntp.model.Message;
import org.pjia.wrvs.plugins.ntp.model.Node;
import org.pjia.wrvs.plugins.ntp.model.Segment;
import org.pjia.wrvs.plugins.ntp.model.Signal;

import com.mks.api.Command;
import com.mks.api.Option;
import com.mks.api.response.APIException;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;

/**
 * 文档更新者
 * 
 * @author pjia
 *
 */
public class SegmentUpdater {
	
	public static SegmentUpdater create(WRVSLocalClient localClient) {
		return new SegmentUpdater(localClient);
	}
	
	private WRVSLocalClient localClient;
	private DataSet dataSet;
	
	private SegmentUpdater(WRVSLocalClient localClient) {
		this.localClient = localClient;
	}

	/**
	 * 更新
	 * 
	 * @param localClient
	 * @param dataSet
	 */
	public void update(DataSet dataSet) {
		this.dataSet = dataSet; 
		List<Message> messages = dataSet.getMessages();
		Segment segment = dataSet.getSegment();
		for(Message message: messages) {
			// 返回 message heading item id
			String mid = saveMessage(message, segment);
			message.setIssueId(mid);
			List<Signal> signals = message.getSignals();
			for(Signal signal :signals) {
				// 返回 Signal item id
				String sid = saveSignal(signal, message);
				signal.setIssueId(sid);
			}
		}
		doDeleteAction(dataSet);
	}

	private void doDeleteAction(DataSet dataSet) {
		List<Node> nodes = dataSet.getDeletingNodes();
		for(Node node :nodes) {
			deleteNode(node);
		}
	}

	private void deleteNode(Node node) {
		try {
			Command cmd = new Command("im", "editissue");
			cmd.addSelection(node.getId());
			// 状态改为 inactive
			cmd.addOption(new Option("field", FieldValue.create("State", "Inactive").toString()));
			localClient.execute(cmd);
		}catch (APIException e) {
			System.out.println(e.getResponse().toString());
		}
	}

	private String saveSignal(Signal signal, Message message) {
		try {
			String issueId = signal.getIssueId();
			Command command = new Command();
			if(StringUtils.isNotBlank(issueId)) {
				// 条目存在，使用 editissue 更新条目
				command.setApp("im");
				command.setCommandName("editissue");
				command.addSelection(issueId);
			} else {
				// 条目不存在，使用 createcontent 创建条目
				command.setApp("im");
				command.setCommandName("createcontent");
				command.addOption(new Option("type", "Network Communication"));
				command.addOption(new Option("parentID", message.getIssueId()));
				Identifiable item = dataSet.getPrev(signal);
				if(item != null) {
					// 设置插入顺序
					command.addOption(new Option("insertLocation", "after:" + item.getIssueId()));
				}
			}
			command.addOption(new Option("field", FieldValue.create("Category", "Functional Requirement").toString()));
			command.addOption(new Option("field", FieldValue.create("State", "Active").toString()));
			// Message 部分属性
			command.addOption(new Option("field", FieldValue.create("Message Name", message.getName()).toString()));
			command.addOption(new Option("field", FieldValue.create("Message ID", message.getId()).toString()));
			command.addOption(new Option("field", FieldValue.create("Cycle time", message.getCycleTime()).toString()));
			command.addOption(new Option("field", FieldValue.create("Send Type", message.getSendType()).toString()));
			command.addOption(new Option("field", FieldValue.create("Message Length", message.getMessageLength()).toString()));
			// Signal 部分属性
			command.addOption(new Option("field", FieldValue.create("Signal Name", signal.getName()).toString()));
			command.addOption(new Option("field", FieldValue.create("Byte Number", signal.getByteNumber()).toString()));
			command.addOption(new Option("field", FieldValue.create("Bit Number", signal.getBitNumber()).toString()));
			command.addOption(new Option("field", FieldValue.create("Signal Length", signal.getSignalLength()).toString()));
			command.addOption(new Option("field", FieldValue.create("Start Bit No", signal.getStartBitNo()).toString()));
			command.addOption(new Option("field", FieldValue.create("Event of signal", signal.getEventOfSignal()).toString()));
			command.addOption(new Option("field", FieldValue.create("External Conditions", signal.getExternalConditions()).toString()));
			command.addOption(new Option("field", FieldValue.create("Signal Description", signal.getSignalDescription()).toString()));
			command.addOption(new Option("field", FieldValue.create("Signal Initial", signal.getSignalInitial()).toString()));
			command.addOption(new Option("field", FieldValue.create("Signal Initial Remark", signal.getSignalInitialRemark()).toString()));
			command.addOption(new Option("field", FieldValue.create("Invalid Value", signal.getInvalidValue()).toString()));
			command.addOption(new Option("field", FieldValue.create("Invalid Value Remark", signal.getInvalidValueRemark()).toString()));
			command.addOption(new Option("field", FieldValue.create("Physical Range", signal.getPhysicalRange()).toString()));
			command.addOption(new Option("field", FieldValue.create("Physical Resolution", signal.getPhysicalResolution()).toString()));
			command.addOption(new Option("field", FieldValue.create("Signal Sender", signal.getSignalSender()).toString()));
			command.addOption(new Option("field", FieldValue.create("Signal Receiver", signal.getSignalReceiver()).toString()));
			if(StringUtils.isNotBlank(signal.getNormal())) {
				// Normal 列有普通文本值
				command.addOption(new Option("field", FieldValue.create("Normal", signal.getNormal()).toString()));
			} else if(signal.getBitMatrix() != null){
				// Normal 列是表格矩阵
				command.addOption(
						new Option(
								"richContentField", 
								FieldValue.create(
										"Normal", 
										BitMatrixTableBuilder.build(signal.getBitMatrix())
								).toString()
						)
				);
			}
			Response response = localClient.execute(command);
			if(response.getWorkItemListSize() > 0) {
				// 条目更新场景，从 workitem 中取值
				WorkItem workItem = response.getWorkItems().next();
				return workItem.getId();
			} else {
				// 条目新建场景，从 result 中取值
				return response.getResult().getField("resultant").getValueAsString();
			}
		}catch (APIException e) {
			System.out.println(e.getResponse().toString());
			return null;
		}
	}

	private String saveMessage(Message message, Segment segment) {
		try {
			String issueId = message.getIssueId();
			Command command = new Command();
			if(StringUtils.isNotBlank(issueId)) {
				// 条目存在，使用 editissue 更新条目
				command.setApp("im");
				command.setCommandName("editissue");
				command.addSelection(issueId);
			} else {
				// 条目不存在，使用 createcontent 创建条目
				command.setApp("im");
				command.setCommandName("createcontent");
				command.addOption(new Option("type", "Network Communication"));
				command.addOption(new Option("parentID", segment.getIssueId()));
				Identifiable item = dataSet.getPrev(message);
				if(item != null) {
					// 设置插入顺序
					command.addOption(new Option("insertLocation", "after:" + item.getIssueId()));
				}
			}
			command.addOption(new Option("field", FieldValue.create("State", "Active").toString()));
			command.addOption(new Option("field", FieldValue.create("Category", "Heading").toString()));
			// message 部分
			command.addOption(new Option("field", FieldValue.create("Message Name", message.getName()).toString()));
			command.addOption(new Option("field", FieldValue.create("Message ID", message.getId()).toString()));
			command.addOption(new Option("field", FieldValue.create("Cycle time", message.getCycleTime()).toString()));
			command.addOption(new Option("field", FieldValue.create("Send Type", message.getSendType()).toString()));
			command.addOption(new Option("field", FieldValue.create("Message Length", message.getMessageLength()).toString()));
			Response response = localClient.execute(command);
			if(response.getWorkItemListSize() > 0) {
				// 条目更新场景，从 workitem 中取值
				WorkItem workItem = response.getWorkItems().next();
				return workItem.getId();
			} else {
				// 条目新建场景，从 result 中取值
				return response.getResult().getField("resultant").getValueAsString();
			}
		}catch (APIException e) {
			System.out.println(e.getResponse().toString());
			return null;
		}
	}

	/**
	 * 字段值模型
	 * 
	 * @author pjia
	 *
	 */
	static class FieldValue {
		
		static FieldValue create(String name, String value) {
			return new FieldValue(name, value);
		}
		
		private String name;
		private String value;
		
		FieldValue(String name, String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public String toString() {
			return name + "=" + value;
		}
	}
}


