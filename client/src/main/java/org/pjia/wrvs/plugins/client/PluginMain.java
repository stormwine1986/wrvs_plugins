package org.pjia.wrvs.plugins.client;

import java.util.ServiceLoader;

/**
 * 插件入口
 * 
 * @author pjia
 *
 */
public class PluginMain {
	
	/**
	 * SPI 查找并启动
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ServiceLoader<PluginApp> appLoader = ServiceLoader.load(PluginApp.class);
		PluginApp app = appLoader.iterator().next();
		app.run(args, new PluginContext());
	}
}
