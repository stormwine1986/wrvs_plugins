package org.pjia.wrvs.plugins.event;

/**
 * 事件监视者接口
 * 
 * @author pjia
 *
 */
public interface IMonitor {
	/**
	 * Event 触发
	 * 
	 * @param event
	 */
	void fired(Event event);
}
