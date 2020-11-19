package org.pjia.wrvs.plugins.ntp;

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

import lombok.extern.slf4j.Slf4j;

/**
 * 导出
 * 
 * @author pjia
 *
 */
@Slf4j
public class ExportApp {
	
	private static WRVSLocalClient localClient;

	public static void run(String[] args, PluginContext context) {
		Workbook workbook = null;
		FileOutputStream outputStream = null;
		try {
			localClient = new WRVSLocalClient(context);
			// 读取文档条目结构
			Segment segment = SegmentBuilder.build(localClient, context.getSelectedIds().get(0));
			// 读取所有非 Heading 条目的全部信息，构建 DataSet
			DataSet dataSet = MessageBuilder.build(segment, localClient);
			workbook = WorkbookBuilder.build(dataSet);
			outputStream = new FileOutputStream(new File("D:\\workspace\\wrvs.plugins\\ntp\\output.xls"));
			workbook.write(outputStream);
			outputStream.flush();
		} catch (Exception e) {
			log.error("", e);
		} finally {
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
