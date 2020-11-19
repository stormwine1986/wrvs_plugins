package org.pjia.wrvs.plugins.client;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PluginContext {
	
	public static void main(String[] args) {
		Map<String, String> map = System.getenv();
		log.debug(map.toString());
	}
}
