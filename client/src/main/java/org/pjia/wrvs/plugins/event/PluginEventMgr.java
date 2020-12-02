package org.pjia.wrvs.plugins.event;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.extern.slf4j.Slf4j;

/**
 * 插件应用事件管理者
 * 
 * @author pjia
 *
 */
@Slf4j
public class PluginEventMgr {
	
	private static ConcurrentLinkedQueue<Event> events = new ConcurrentLinkedQueue<>();
	private static ConcurrentHashMap<String, IMonitor> monitors = new ConcurrentHashMap<>(16);
	
	/**
	 * 记录事件
	 * 
	 * @param event
	 */
	public synchronized static void recordEvent(Event event) {
		events.add(event);
		log.debug("record event: " + event.toString());
		firedAll(event);
	}
	/**
	 * 注册监视者
	 * 
	 * @param monitor
	 */
	public static void addMonitor(IMonitor monitor) {
		monitors.put(monitor.toString(), monitor);
	}
	/**
	 * 移除监视者
	 * 
	 * @param monitor
	 */
	public static void removeMonitor(IMonitor monitor) {
		monitors.remove(monitor.toString());
	}

	private static void firedAll(Event event) {
		monitors.entrySet().stream().forEach(entry -> {entry.getValue().fired(event);});
	}
}
