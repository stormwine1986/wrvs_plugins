package org.pjia.wrvs.plugins.ntp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.pjia.wrvs.plugins.client.PluginContext;
import org.pjia.wrvs.plugins.client.WRVSLocalClient;
import org.pjia.wrvs.plugins.ntp.internal.MessageBuilder;
import org.pjia.wrvs.plugins.ntp.internal.SegmentBuilder;
import org.pjia.wrvs.plugins.ntp.internal.SegmentUpdater;
import org.pjia.wrvs.plugins.ntp.internal.StructureBuilder;
import org.pjia.wrvs.plugins.ntp.model.DataSet;
import org.pjia.wrvs.plugins.ntp.model.Segment;
import org.pjia.wrvs.plugins.ntp.model.Structure;

import lombok.extern.slf4j.Slf4j;

/**
 * 导入
 * 
 * @author pjia
 *
 */
@Slf4j
public class ImportApp {
	
	private static File file = new File("D:\\workspace\\wrvs.plugins\\ntp\\sample.xls");
	private static WRVSLocalClient localClient;

	/**
	 * 主方法
	 * 
	 * @param args
	 * @param context
	 */
	public static void run(String[] args, PluginContext context) {
		Workbook workbook = null;
    	try (InputStream is = new FileInputStream(file)) {
    		workbook = new HSSFWorkbook(is);
    		Sheet sheet = workbook.getSheet("PT");
    		Structure structure = StructureBuilder.build(sheet);
    		DataSet dataSet = MessageBuilder.build(structure);
    		localClient = new WRVSLocalClient(context);
    		Segment segment = SegmentBuilder.build(localClient, context.getSelectedIds().get(0));
    		dataSet.apply(segment);
    		SegmentUpdater.create(localClient).update(dataSet);
    	}catch (Exception e) {
    		log.error("", e);
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
