package org.pjia.wrvs.plugins.ntp.model;

/**
 * Message 和 Signal 的基类，可标识的对象
 * 
 * @author pjia
 *
 */
public interface Identifiable {
	/**
	 * 获取标识
	 * 
	 * @return
	 */
	String getIssueId();
}
