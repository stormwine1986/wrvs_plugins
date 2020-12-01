package org.pjia.wrvs.plugins.ntp.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 导入事件
 * 
 * @author pjia
 *
 */
@Slf4j
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
		log.debug(event);
		events.add(event);
	}

	/**
	 * 更新导入进度
	 * @param title 
	 * 
	 * @param finished
	 * @param totalAmount
	 */
	public void updateProgress(String title, Integer finished, Integer totalAmount) {
		String message = String.format(Locale.ROOT, "%s/%s %s ... ", finished, totalAmount, title);
		log.debug(message);
		events.add(message);
	}
}
