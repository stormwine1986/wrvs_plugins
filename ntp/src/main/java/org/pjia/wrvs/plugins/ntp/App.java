package org.pjia.wrvs.plugins.ntp;

import org.pjia.wrvs.plugins.client.PluginApp;
import org.pjia.wrvs.plugins.client.PluginContext;

/**
 * App
 * 
 * @author pjia
 *
 */
public class App implements PluginApp {
	
	@Override
    public void run(String[] args, PluginContext context) {
    	String appName = args[0];
    	if("import".equals(appName)) {
    		ImportApp.run(args, context);
    	}else if("export".equals(appName)) {
    		ExportApp.run(args, context);
    	}
    }
}
