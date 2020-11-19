package org.pjia.wrvs.plugins.client;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PluginContext {
	@Getter
	private String host;
	@Getter
	private String port;
	@Getter
	private String user;
	@Getter
	private List<String> selectedIds = new ArrayList<>();

	/**
	 * MKSSI_NISSUE,MKSSI_PORT,MKSSI_ISSUE0,MKSSI_QUERY,MKSSI_USER,MKSSI_HOST
	 * 
	 * @param args
	 */
	public PluginContext(){
		host = System.getenv("MKSSI_HOST");
		port = System.getenv("MKSSI_PORT");
		user = System.getenv("MKSSI_USER");
		Integer n = Integer.valueOf(System.getenv("MKSSI_NISSUE")==null?"0":System.getenv("MKSSI_NISSUE"));
		for(int i = 0; i < n; i++) {
			selectedIds.add(System.getenv("MKSSI_ISSUE" + i));
		}
		log.debug("selectIds = " + selectedIds);
	}
}
