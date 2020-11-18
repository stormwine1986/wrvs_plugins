package org.pjia.wrvs.plugins.ntp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.pjia.wrvs.plugins.client.WRVSLocalClient;
import org.pjia.wrvs.plugins.ntp.internal.MessageBuilder;
import org.pjia.wrvs.plugins.ntp.internal.SegmentBuilder;
import org.pjia.wrvs.plugins.ntp.internal.SegmentUpdater;
import org.pjia.wrvs.plugins.ntp.internal.StructureBuilder;
import org.pjia.wrvs.plugins.ntp.model.Message;
import org.pjia.wrvs.plugins.ntp.model.Node;
import org.pjia.wrvs.plugins.ntp.model.Segment;
import org.pjia.wrvs.plugins.ntp.model.Structure;

/**
 * 网络技术协议
 * 
 * @author pjia
 *
 */
public class App {
	
	private static File file = new File("D:\\workspace\\wrvs.plugins\\ntp\\sample.xls");
	private static WRVSLocalClient localClient;
	private static String issueId = "36949";
	
    public static void main(String[] args) {
    	Workbook workbook = null;
    	try (InputStream is = new FileInputStream(file)) {
    		workbook = new HSSFWorkbook(is);
    		Sheet sheet = workbook.getSheet("PT");
    		Structure structure = StructureBuilder.build(sheet);
    		List<Message> messages = MessageBuilder.build(structure);
    		localClient = new WRVSLocalClient();
    		Segment segment = SegmentBuilder.build(localClient, issueId);
    		segment.apply(messages);
    		SegmentUpdater.create(localClient).update(segment);
    	}catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放本地 Session
			if(localClient != null) {
				localClient.release();
			}
			if(workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
				}
			}
		}
    }

	
    
    
}
