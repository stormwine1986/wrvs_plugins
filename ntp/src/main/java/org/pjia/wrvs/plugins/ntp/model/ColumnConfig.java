package org.pjia.wrvs.plugins.ntp.model;

import java.util.List;
import java.util.Optional;

/**
 * 列配置模型
 * 
 * @author pjia
 *
 */
public class ColumnConfig {
	
	private List<Column> columns;

	public ColumnConfig(List<Column> result) {
		columns = result;
	}
	
	/**
	 * 获取指定属性名的列
	 * 
	 * @param name
	 * @return
	 */
	public Column getColumnByName(String name) {
		Optional<Column> optional = columns.stream().filter(col -> name.equals(col.getName())).findFirst();
		return optional.isPresent()? optional.get(): null;
	}
	
	public Column getColumnByIndex(int index) {
		Optional<Column> optional = columns.stream().filter(col -> index == col.getIndex()).findFirst();
		return optional.isPresent()? optional.get(): null;
	}

}
