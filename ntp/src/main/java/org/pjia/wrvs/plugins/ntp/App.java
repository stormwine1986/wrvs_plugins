package org.pjia.wrvs.plugins.ntp;

import java.util.Arrays;

import org.pjia.wrvs.plugins.client.PluginApp;
import org.pjia.wrvs.plugins.client.PluginContext;

import lombok.extern.slf4j.Slf4j;

/**
 * App
 * 
 * @author pjia
 *
 */
@Slf4j
public class App implements PluginApp {
	
	@Override
    public void run(String[] args, PluginContext context) {
		log.debug("args = " + Arrays.toString(args));
		if(args.length > 0) {
			String appName = args[0];
			log.debug("appName = " + appName);
			if("import".equals(appName)) {
				ImportApp.run(args, context);
			}else if("export".equals(appName)) {
				ExportApp.run(args, context);
			}			
		}
    }
}
