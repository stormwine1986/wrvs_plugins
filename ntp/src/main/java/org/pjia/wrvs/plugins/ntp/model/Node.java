package org.pjia.wrvs.plugins.ntp.model;

import lombok.Data;

/**
 * 文档条目模型
 * 
 * @author pjia
 *
 */
@Data
public class Node {
	
	private String id;
	private String categroty;
	private String messageName;
	private String signalName;
	private String bitNumber;
	private boolean delete = false;
	
	public Node(String id, String category, String messageName, String signalName, String bitNumber) {
		this.id = id;
		this.categroty = category;
		this.messageName = messageName;
		this.signalName = signalName;
		this.bitNumber = bitNumber;
	}
}
