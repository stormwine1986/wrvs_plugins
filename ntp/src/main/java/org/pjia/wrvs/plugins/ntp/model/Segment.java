package org.pjia.wrvs.plugins.ntp.model;

import java.util.List;
import java.util.Optional;

import lombok.Data;

/**
 * 文档模型
 * 
 * @author pjia
 *
 */
@Data
public class Segment {
	
	private String issueId;
	private List<Node> nodes;
	private List<Message> messages;
	
	public Segment(List<Node> nodes) {
		this.nodes = nodes;
	}

	/**
	 * 接收来自文件的值
	 * 
	 * @param messages
	 */
	public void apply(List<Message> messages) {
		this.messages = messages;
		// 所有 node 暂时记为即将删除
		markAllDelete();
		for(Node node :nodes) {
			String categroty = node.getCategroty();
			if("Heading".equals(categroty)) {
				Message message = getMessage(node);
				// 找到对应的 message 意味着条目继续存在，取消 delete 标记，并将条目 id 关联到 message 对象。
				if(message != null) {
					message.setIssueId(node.getId());
					node.setDelete(false);
				}
			} else {
				Signal signal = getSigal(node);
				// 找到对应的 signal 意味着条目继续存在，取消 delete 标记，并将条目 id 关联到 signal 对象。
				if(signal != null) {
					signal.setIssueId(node.getId());
					node.setDelete(false);
				}
			}
		}
	}

	private Signal getSigal(Node node) {
		Signal result = null;
		final String messageName = node.getMessageName();
		Optional<Message> optional = messages.stream().filter(message -> messageName.equals(message.getName())).findFirst();
		if(optional.isPresent()) {
			Message message = optional.get();
			final String bitNumber = node.getBitNumber();
			Optional<Signal> optional2 = message.getSignals().stream().filter(signal -> bitNumber.equals(signal.getBitNumber())).findFirst();
			if(optional2.isPresent()) {
				result = optional2.get();
			}
		}
		return result;
	}

	private Message getMessage(Node node) {
		final String messageName = node.getMessageName();
		Optional<Message> optional = messages.stream().filter(message -> messageName.equals(message.getName())).findFirst();
		return optional.isPresent()?optional.get():null;
	}
	
	private void markAllDelete() {
		nodes.stream().forEach(node -> { node.setDelete(true); });
	}
}
