package org.pjia.wrvs.plugins.ntp.model;

import java.util.List;

import lombok.Data;

/**
 * 结构模型
 * 
 * @author pjia
 *
 */
@Data
public class Structure {
	
	private ColumnConfig config;
	private List<Message> messages;
	
	public Structure(List<Message> messages, ColumnConfig config) {
		this.messages = messages;
		this.config = config;
	}

}
