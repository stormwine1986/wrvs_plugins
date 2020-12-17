package org.pjia.wrvs.plugins.ntp.ui;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.pjia.wrvs.plugins.client.PluginContext;
import org.pjia.wrvs.plugins.client.WRVSLocalClient;
import org.pjia.wrvs.plugins.event.Event;
import org.pjia.wrvs.plugins.event.PluginEventMgr;
import org.pjia.wrvs.plugins.ntp.internal.MessageBuilder;
import org.pjia.wrvs.plugins.ntp.internal.SegmentBuilder;
import org.pjia.wrvs.plugins.ntp.internal.WorkbookBuilder;
import org.pjia.wrvs.plugins.ntp.model.DataSet;
import org.pjia.wrvs.plugins.ntp.model.Segment;
import org.pjia.wrvs.plugins.ntp.model.Template;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExportThread extends Thread {
	
	private PluginContext context;
	private Template template;

	public ExportThread(PluginContext context, Template template) {
		this.context = context;
		this.template = template;
	}

	@Override
	public void run() {
		Workbook workbook = null;
		FileOutputStream outputStream = null;
		try {
			WRVSLocalClient localClient = context.getLocalClient();
			loadTemplate(localClient);
			// 读取文档条目结构
			PluginEventMgr.recordEvent(new Event("正在分析文档结构 ... "));
			Segment segment = SegmentBuilder.create(localClient).build(context.getSelectedIds().get(0));
			// 读取所有信号条目的全部信息，构建 DataSet
			DataSet dataSet = MessageBuilder.create(localClient).build(segment);
			workbook = WorkbookBuilder.create().build(dataSet, template);
			outputStream = new FileOutputStream(template.getTempFile());
			workbook.write(outputStream);
			outputStream.flush();
		} catch (Exception e) {
			log.error("", e);
			PluginEventMgr.recordEvent(new Event(e));
		} finally {
			PluginEventMgr.recordEvent(new Event("导出完成"));
			if(workbook != null) {  
				try {
					workbook.close();
				} catch (IOException e) {
				} 
			}
			if(outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void loadTemplate(WRVSLocalClient localClient) throws IOException {
		byte[] resource = localClient.getResource("/plugins/templates/" + template.getFileName());
		log.debug("从服务器加载模板文件大小(B): " + resource.length);
		template.createTempFile(resource);
		log.debug("创建临时文件: " + template.getTempFile().getAbsolutePath());
	}

}
