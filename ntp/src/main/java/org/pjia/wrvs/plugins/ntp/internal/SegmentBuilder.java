package org.pjia.wrvs.plugins.ntp.internal;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pjia.wrvs.plugins.client.WRVSLocalClient;
import org.pjia.wrvs.plugins.ntp.model.Node;
import org.pjia.wrvs.plugins.ntp.model.Segment;

import com.mks.api.Command;
import com.mks.api.MultiValue;
import com.mks.api.Option;
import com.mks.api.response.APIException;
import com.mks.api.response.Item;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;

/**
 * 文档构建者
 * 
 * @author pjia
 *
 */
public class SegmentBuilder {
	
	private WRVSLocalClient localClient;

	public SegmentBuilder(WRVSLocalClient localClient) {
		this.localClient = localClient;
	}

	/**
	 * 构造
	 * 
	 * @param client
	 * @param issueId
	 * @return
	 * @throws APIException
	 */
	public Segment build(String issueId) throws APIException {
		Segment segment = getSegment(issueId);
		segment.setIssueId(issueId);
		buildHistory(segment);
		return segment;
	}
	
	private void buildHistory(Segment segment) throws APIException {
		String issueId = segment.getIssueId();
		Command cmd = new Command("im", "viewissue");
		cmd.addSelection(issueId);
		cmd.addOption(new Option("showRichContent"));
		Response response = localClient.execute(cmd);
		WorkItem workItem = response.getWorkItem(issueId);
		String content = workItem.getField("Document History").getValueAsString();
		Document history = Jsoup.parse(content);
		segment.setHistory(history);
	}

	@SuppressWarnings("unchecked")
	private Segment getSegment(String docId) throws APIException {
		Command cmd = new Command("im", "issues");
		cmd.addSelection(docId);
		cmd.addOption(new Option("fields", "Contains"));
		Response response = localClient.execute(cmd);
		WorkItem workItem = response.getWorkItem(docId);
		List<Item> contents = workItem.getField("Contains").getList();
		List<String> contentids = contents.stream().map(item->item.getId()).collect(Collectors.toList());
		if(CollectionUtils.isNotEmpty(contentids)) {}
		List<Node> nodes = getContents(contentids);
		return new Segment(nodes);
	}

	@SuppressWarnings("unchecked")
	private List<Node> getContents(List<String> contentids) throws APIException {
		List<Node> nodes = new LinkedList<>();
		if(CollectionUtils.isEmpty(contentids)) {
			// 空文档，不需要进一步加载条目
			return nodes; 
		}
		Command cmd = new Command("im", "issues");
		contentids.stream().forEach(id->{cmd.addSelection(id);});
		MultiValue mv = new MultiValue(",");
		mv.add("ID");
		mv.add("Category");
		mv.add("Message Name");
		mv.add("Signal Name");
		mv.add("Bit Number");
		mv.add("Contains");
		cmd.addOption(new Option("fields", mv));
		Response response = localClient.execute(cmd);
		WorkItemIterator workItems = response.getWorkItems();
		while(workItems.hasNext()) {
			// 获取条目
			WorkItem workItem = workItems.next();
			String id = workItem.getField("ID").getValueAsString();
			String category = workItem.getField("Category").getValueAsString();
			String messageName = workItem.getField("Message Name").getValueAsString();
			String signalName = workItem.getField("Signal Name").getValueAsString();
			String bitNumber = workItem.getField("Bit Number").getValueAsString();
			List<Item> contents = workItem.getField("Contains").getList();
			nodes.add(new Node(id, category, messageName, signalName, bitNumber));
			List<String> subids = contents.stream().map(item->item.getId()).collect(Collectors.toList());
			// 获取子条目
			nodes.addAll(getContents(subids));
		}
		return nodes;
	}

	/**
	 * 工厂方法
	 * 
	 * @param localClient
	 * @return
	 */
	public static SegmentBuilder create(WRVSLocalClient localClient) {
		return new SegmentBuilder(localClient);
	}
}
