package org.pjia.wrvs.plugins.ntp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.CollectionUtils;

import lombok.Getter;

/**
 * 数据集
 * 
 * @author pjia
 *
 */
public class DataSet {
	@Getter
	private List<Message> messages;
	@Getter
	private Segment segment;
	@Getter
	private Integer totalAmount;
	
	public DataSet(List<Message> messages) {
		this.messages = messages;
		totalAmount = getTotal();
	}

	/**
	 * 与现有条目联结
	 * 
	 * @param segment
	 */
	public void apply(Segment segment) {
		this.segment = segment;
		// 所有 node 暂时记为即将删除
		markAllDelete(segment);
		List<Node> nodes = segment.getNodes();
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
	
	/**
	 * 获取即将删除的字段
	 * 
	 * @return
	 */
	public List<Node> getDeletingNodes(){
		List<Node> nodes = segment.getNodes();
		List<Node> result = new ArrayList<Node>();
		for(Node node :nodes) {
			if(node.isDelete()) {
				result.add(node);
			}
		}
		return result;
	}
	
	/**
	 * 获取最后一个 message
	 * 
	 * @return
	 */
	public Message getLastestMessage() {
		if(CollectionUtils.isEmpty(messages)) return null;
		return messages.get(messages.size() - 1);
	}
	
	private void markAllDelete(Segment segment) {
		segment.getNodes().stream().forEach(node -> { node.setDelete(true); });
	}
	
	private Message getMessage(Node node) {
		final String messageName = node.getMessageName();
		Optional<Message> optional = messages.stream().filter(message -> messageName.equals(message.getName())).findFirst();
		return optional.isPresent()?optional.get():null;
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

	private Integer getTotal() {
		final AtomicInteger result = new AtomicInteger(0);
		messages.stream().forEach(message -> {
			result.addAndGet(message.getSignals().size());
		});
		return result.addAndGet(messages.size());
	}

}
