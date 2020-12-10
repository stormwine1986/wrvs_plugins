package org.pjia.wrvs.plugins.sample;

import java.util.Properties;

import org.junit.Test;
import org.pjia.wrvs.plugins.client.PluginContext;

/**
 * 应用测试类
 * 
 * @author pjia
 *
 */
public class TestMain {
	
	@Test
	public void runApp() {
		Properties properties = new Properties();
		properties.setProperty("MKSSI_HOST", "almprod.hq.faw.cn");
		properties.setProperty("MKSSI_PORT", "7001");
		properties.setProperty("MKSSI_USER", "administrator");
		properties.setProperty("MKSSI_NISSUE", "1");
		properties.setProperty("MKSSI_ISSUE0", "30203");
		try(PluginContext context = new PluginContext(properties);){
			App app = new App();
			app.run(new String[] {}, context);			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
