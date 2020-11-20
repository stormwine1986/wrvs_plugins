package org.pjia.wrvs.plugins.ntp;

import org.pjia.wrvs.plugins.client.PluginContext;

/**
 * 应用测试类
 * 
 * @author pjia
 *
 */
public class TestMain {
	
	public static void main(String[] args) {
		App app = new App();
		PluginContext context = new PluginContext();
		context.setHost("10.112.6.226");
		context.setUser("administrator");
		context.getSelectedIds().add("36949");
		app.run(args, context);
	}
}
