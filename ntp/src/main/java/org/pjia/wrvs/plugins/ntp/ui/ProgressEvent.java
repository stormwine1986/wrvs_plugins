package org.pjia.wrvs.plugins.ntp.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lombok.Data;

/**
 * 导入事件
 * 
 * @author pjia
 *
 */
@Data
public class ProgressEvent {
	
	private Exception ex;
	private List<String> events = new ArrayList<>();
	private boolean stop;
	
	/**
	 * 获取最后的事件
	 * 
	 * @return
	 */
	public String getLatestEvent() {
		return events.size() > 0 ? events.get(events.size() - 1): "";
	}
	
	/**
	 * 汇报事件
	 * 
	 * @param event
	 */
	public void updateEvent(String event) {
		System.out.println(event);
		events.add(event);
	}

	/**
	 * 更新导入进度
	 * 
	 * @param finished
	 * @param totalAmount
	 */
	public void updateProgress(Integer finished, Integer totalAmount) {
		String message = String.format(Locale.ROOT, "%s/%s 正在写入 ... ", finished, totalAmount);
		System.out.println(message);
		events.add(message);
	}
}
