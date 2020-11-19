package org.pjia.wrvs.plugins.client;

/**
 * 插件应用
 * 
 * @author pjia
 *
 */
public interface PluginApp {
	
	/**
	 * 运行应用
	 * 
	 * @param args
	 * @param context
	 */
	void run(String[] args, PluginContext context);

}
