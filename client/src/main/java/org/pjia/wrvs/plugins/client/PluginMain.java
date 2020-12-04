package org.pjia.wrvs.plugins.client;

import java.util.ServiceLoader;

import lombok.extern.slf4j.Slf4j;

/**
 * 插件入口
 * 
 * @author pjia
 *
 */
@Slf4j
public class PluginMain {
	
	/**
	 * SPI 查找并启动
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try (PluginContext context = new PluginContext();) {
			ServiceLoader<PluginApp> appLoader = ServiceLoader.load(PluginApp.class);
			PluginApp app = appLoader.iterator().next();
			app.run(args, context);			
		}catch (Exception e) {
			log.error("", e);
		}
	}
}
