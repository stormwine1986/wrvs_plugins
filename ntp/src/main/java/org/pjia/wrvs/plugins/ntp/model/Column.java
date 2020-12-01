package org.pjia.wrvs.plugins.ntp.model;

import lombok.Data;

/**
 * 列模型
 * 
 * @author pjia
 *
 */
@Data
public class Column {
	
	/**
	 * 属性名
	 */
	private String name;
	/**
	 * 列索引
	 */
	private int index;
	
	public Column(String attrName, int columnIndex) {
		this.name = attrName;
		this.index = columnIndex;
	}
	
	public Column(String attrName) {
		this.name = attrName;
	}
}
