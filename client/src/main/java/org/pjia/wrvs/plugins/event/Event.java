package org.pjia.wrvs.plugins.event;

import java.util.Date;
import java.util.Locale;

import lombok.Getter;

/**
 * 插件事件
 * 
 * @author pjia
 *
 */
public class Event {
	
	@Getter
	private Date date = new Date();
	@Getter
	private String message;
	@Getter
	private Exception ex;
	
	private Thread thread;
	
	public Event(String message) {
		thread = Thread.currentThread();
		this.message = message;
	}
	
	public Event(String task, Integer finished, Integer total) {
		thread = Thread.currentThread();
		this.message = String.format(Locale.ROOT, "%s/%s %s ...", finished, total, task);
	}
	
	public Event(Exception ex) {
		thread = Thread.currentThread();
		this.ex = ex;
	}
	
	@Override
	public String toString() {
		return String.format(message);
	}
	/**
	 * 是否是异常事件
	 * 
	 * @return
	 */
	public boolean isExEvent() {
		return ex!=null;
	}
	/**
	 * 获取事件发生的 Thread ID
	 * 
	 * @return
	 */
	public long getThreadId() {
		return thread.getId();
	}
}
