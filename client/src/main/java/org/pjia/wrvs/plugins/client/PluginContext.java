package org.pjia.wrvs.plugins.client;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.mks.api.response.APIException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PluginContext implements Closeable {
	@Getter
	private String host;
	@Getter
	private String port;
	@Getter
	private String user;
	@Getter
	private List<String> selectedIds = new ArrayList<>();
	@Getter
	private WRVSLocalClient localClient;

	/**
	 * MKSSI_NISSUE,MKSSI_PORT,MKSSI_ISSUE0,MKSSI_QUERY,MKSSI_USER,MKSSI_HOST
	 * 
	 * @param args
	 * @throws APIException  
	 */
	public PluginContext() throws APIException {
		host = System.getenv("MKSSI_HOST");
		port = System.getenv("MKSSI_PORT");
		user = System.getenv("MKSSI_USER");
		Integer n = Integer.valueOf(System.getenv("MKSSI_NISSUE")==null?"0":System.getenv("MKSSI_NISSUE"));
		for(int i = 0; i < n; i++) {
			selectedIds.add(System.getenv("MKSSI_ISSUE" + i));
		}
		log.debug("selectIds = " + selectedIds);
		// 构建 local client
		buildLocalClient();
	}
	
	/**
	 * 用于测试
	 * 
	 * @param properties
	 * @throws APIException
	 */
	public PluginContext(Properties properties) throws APIException {
		host = properties.getProperty("MKSSI_HOST");
		port = properties.getProperty("MKSSI_PORT");
		user = properties.getProperty("MKSSI_USER");
		Integer n = Integer.valueOf(properties.getProperty("MKSSI_NISSUE")==null?"0":properties.getProperty("MKSSI_NISSUE"));
		for(int i = 0; i < n; i++) {
			selectedIds.add(properties.getProperty("MKSSI_ISSUE" + i));
		}
		log.debug("selectIds = " + selectedIds);
		// 构建 local client
		buildLocalClient();
	}

	private void buildLocalClient() throws APIException {
		localClient = new WRVSLocalClient(this);
	}

	@Override
	public void close() throws IOException {
		if(localClient != null) localClient.release();
		log.debug("local client is released");
	}
}
