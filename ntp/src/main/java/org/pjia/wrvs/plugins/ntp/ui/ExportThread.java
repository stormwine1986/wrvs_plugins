package org.pjia.wrvs.plugins.ntp.ui;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.pjia.wrvs.plugins.client.PluginContext;
import org.pjia.wrvs.plugins.client.WRVSLocalClient;
import org.pjia.wrvs.plugins.ntp.internal.MessageBuilder;
import org.pjia.wrvs.plugins.ntp.internal.SegmentBuilder;
import org.pjia.wrvs.plugins.ntp.internal.WorkbookBuilder;
import org.pjia.wrvs.plugins.ntp.model.DataSet;
import org.pjia.wrvs.plugins.ntp.model.Segment;
import org.pjia.wrvs.plugins.ntp.model.Template;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExportThread extends Thread {
	
	private static WRVSLocalClient localClient;
	
	private ProgressEvent event;
	private PluginContext context;
	private Template template;

	public ExportThread(ProgressEvent event, PluginContext context, Template template) {
		this.event = event;
		this.context = context;
		this.template = template;
	}

	@Override
	public void run() {
		Workbook workbook = null;
		FileOutputStream outputStream = null;
		try {
			localClient = new WRVSLocalClient(context);
			loadTemplate();
			// 读取文档条目结构
			event.updateEvent("正在分析文档结构 ... ");
			Segment segment = SegmentBuilder.build(localClient, context.getSelectedIds().get(0));
			// 读取所有非 Heading 条目的全部信息，构建 DataSet
			DataSet dataSet = MessageBuilder.build(segment, localClient, event);
			workbook = WorkbookBuilder.build(dataSet, template, event);
			outputStream = new FileOutputStream(template.getTempFile());
			workbook.write(outputStream);
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
			event.setEx(e);
		} finally {
			event.setStop(true);
			event.updateEvent("导出完成 ... ");
			if(localClient != null) { localClient.release(); }
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

	private void loadTemplate() throws IOException {
		byte[] resource = localClient.getResource("/plugins/templates/" + template.getFileName());
		log.debug("从服务器加载模板文件大小(B): " + resource.length);
		template.createTempFile(resource);
		log.debug("创建临时文件: " + template.getTempFile().getAbsolutePath());
	}

}
