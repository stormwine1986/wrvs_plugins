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
		context.setHost("almprod.hq.faw.cn");
		context.setUser("administrator");
		context.setPort("7001");
		context.getSelectedIds().add("30203");
		app.run(args, context);
	}
}
