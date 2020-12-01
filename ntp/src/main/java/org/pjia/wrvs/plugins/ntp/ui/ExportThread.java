package org.pjia.wrvs.plugins.ntp.ui;

import java.io.File;
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

public class ExportThread extends Thread {
	
	private static WRVSLocalClient localClient;
	
	private ProgressEvent event;
	private PluginContext context;
	private File tempfile;

	public ExportThread(ProgressEvent event, PluginContext context, File tempfile) {
		this.event = event;
		this.context = context;
		this.tempfile = tempfile;
	}

	@Override
	public void run() {
		Workbook workbook = null;
		FileOutputStream outputStream = null;
		try {
			localClient = new WRVSLocalClient(context);
			// 读取文档条目结构
			event.updateEvent("正在分析文档结构 ... ");
			Segment segment = SegmentBuilder.build(localClient, context.getSelectedIds().get(0));
			// 读取所有非 Heading 条目的全部信息，构建 DataSet
			event.updateEvent("正在读取文档 ... ");
			DataSet dataSet = MessageBuilder.build(segment, localClient);
			workbook = WorkbookBuilder.build(dataSet, event);
			
			outputStream = new FileOutputStream(tempfile);
			workbook.write(outputStream);
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
			event.setEx(e);
		} finally {
			event.setStop(true);
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

}
