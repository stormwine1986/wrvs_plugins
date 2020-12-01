package org.pjia.wrvs.plugins.ntp.model;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;

import lombok.Getter;
import lombok.NonNull;

/**
 * 列配置模型
 * 
 * @author pjia
 *
 */
public class ColumnConfig {
	
	@Getter
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
	/**
	 * 获取指定号位的列
	 * 
	 * @param index
	 * @return
	 */
	public Column getColumnByIndex(int index) {
		Optional<Column> optional = columns.stream().filter(col -> index == col.getIndex()).findFirst();
		return optional.isPresent()? optional.get(): null;
	}
	/**
	 * 获取最后一列
	 * 
	 * @return
	 */
	public Column getEndColumn() {
		if(CollectionUtils.isEmpty(columns)) { return null; }
		return columns.get(columns.size() - 1);
	}
	
	/**
	 * 定义列配置
	 * 
	 * @param col
	 * @param offset
	 */
	public void addColumn(Column col, Integer offset) {
		Integer i = offset == null?1:offset;
		int lastestIndex = lastestIndex();
		col.setIndex(lastestIndex + i);
		columns.add(col);
	}
	
	/**
	 * 定义列配置
	 * 
	 * @param col
	 * @param offset
	 */
	public void addColumn(Column col) {
		int lastestIndex = lastestIndex();
		col.setIndex(lastestIndex + 1);
		columns.add(col);
	}
	
	private int lastestIndex() {
		if(columns.size() == 0) {
			return -1;
		}
		return columns.get(columns.size() - 1).getIndex();
	}
}
