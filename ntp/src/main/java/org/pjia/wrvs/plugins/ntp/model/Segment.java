package org.pjia.wrvs.plugins.ntp.model;

import java.util.List;

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
	
	public Segment(List<Node> nodes) {
		this.nodes = nodes;
	}
}
