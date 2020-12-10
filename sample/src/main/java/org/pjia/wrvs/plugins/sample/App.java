package org.pjia.wrvs.plugins.sample;

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
    }
}
