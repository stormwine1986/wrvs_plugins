package org.pjia.wrvs.plugins.ntp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.pjia.wrvs.plugins.client.WRVSLocalClient;
import org.pjia.wrvs.plugins.ntp.internal.MessageBuilder;
import org.pjia.wrvs.plugins.ntp.internal.SegmentBuilder;
import org.pjia.wrvs.plugins.ntp.internal.WorkbookBuilder;
import org.pjia.wrvs.plugins.ntp.model.DataSet;
import org.pjia.wrvs.plugins.ntp.model.Segment;

/**
 * 导出
 * 
 * @author pjia
 *
 */
public class ExportApp {
	
	private static WRVSLocalClient localClient;
	private static String issueId = "36949";
	
	public static void main(String[] args) {
		try {
			localClient = new WRVSLocalClient();
			// 读取文档条目结构
			Segment segment = SegmentBuilder.build(localClient, issueId);
			// 读取所有非 Heading 条目的全部信息，构建 DataSet
			DataSet dataSet = MessageBuilder.build(segment, localClient);
			Workbook workbook = WorkbookBuilder.build(dataSet);
			FileOutputStream outputStream = new FileOutputStream(new File("D:\\workspace\\wrvs.plugins\\ntp\\output.xls"));
			workbook.write(outputStream);
			workbook.close();
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(localClient != null) { localClient.release(); }
		}
	}
}
