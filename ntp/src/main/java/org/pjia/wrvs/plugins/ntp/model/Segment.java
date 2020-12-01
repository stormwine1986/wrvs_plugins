package org.pjia.wrvs.plugins.ntp.model;

import java.util.List;

import org.jsoup.nodes.Document;

import lombok.Data;

/**
 * 文档模型
 * 
 * @author pjia
 *
 */
@Data
public class Segment {
	/**
	 * 文档ID
	 */
	private String issueId;
	/**
	 * 文档条目，扁平组织
	 */
	private List<Node> nodes;
	/**
	 * 文档历史记录
	 */
	private Document history;
	
	public Segment(List<Node> nodes) {
		this.nodes = nodes;
	}
}
