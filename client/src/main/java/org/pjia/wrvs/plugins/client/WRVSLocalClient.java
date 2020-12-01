package org.pjia.wrvs.plugins.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

import com.mks.api.CmdRunner;
import com.mks.api.Command;
import com.mks.api.IntegrationPoint;
import com.mks.api.IntegrationPointFactory;
import com.mks.api.Session;
import com.mks.api.response.APIException;
import com.mks.api.response.Response;
import com.mks.api.util.APIVersion;

public class WRVSLocalClient {
	
	private Session session;

	public WRVSLocalClient(PluginContext context) throws APIException {
		IntegrationPoint point = IntegrationPointFactory.getInstance().createLocalIntegrationPoint(APIVersion.API_4_16);
		point.setAutoStartIntegrityClient(true);
		session = point.getCommonSession();
		session.setDefaultHostname(context.getHost());
		session.setDefaultPort(Integer.valueOf(context.getPort()));
		session.setDefaultUsername(context.getUser());
	}
	
	/**
	 * 释放 Session
	 */
	public void release() {
		if(session != null) {
			try {
				session.release();
			} catch (IOException | APIException e) {
				e.printStackTrace();
			} 
		}
	}
	
	/**
	 * 执行 Command
	 * 
	 * @param cmd
	 * @return
	 * @throws APIException
	 */
	public Response execute(Command cmd) throws APIException {
		CmdRunner cmdRunner = null;
		try {
			cmdRunner = session.createCmdRunner();
			return cmdRunner.execute(cmd);			
		} finally {
			if(cmdRunner != null) {
				cmdRunner.release();
			}
		}
	}
	
	/**
	 * 获取服务器部署的资源
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public byte[] getResource(String path) throws IOException {
		InputStream is = null;
		try {
			String hostname = session.getDefaultHostname();
			int port = session.getDefaultPort();
			URL url = new URL(String.format("http://%s:%s%s", hostname, port, path));
			URLConnection conn = url.openConnection();
			is = conn.getInputStream();
			return IOUtils.toByteArray(is);
		}finally {
			if(is != null) {
				try {
					is.close();				
				}catch (IOException e) {
				}
			}
		}
	}
}
