package org.pjia.wrvs.plugins.ntp.ui;

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
import org.pjia.wrvs.plugins.ntp.model.Model;
import org.pjia.wrvs.plugins.ntp.model.Segment;
import org.pjia.wrvs.plugins.ntp.model.Structure;
import org.pjia.wrvs.plugins.ntp.utils.AssertUtil;

/**
 * 导入线程
 * 
 * @author pjia
 *
 */
public class ImportThread extends Thread {
	
	private ProgressEvent event;
	private WRVSLocalClient localClient;
	private PluginContext context;
	private File file;
	
	public ImportThread(ProgressEvent event, PluginContext context, File file) {
		this.event = event;
		this.context = context;
		this.file = file;
	}
	
	public ProgressEvent getEvent() {
		return event;
	}

	@Override
	public void run() {
		Workbook workbook = null;
    	try (InputStream is = new FileInputStream(file)) {
    		workbook = new HSSFWorkbook(is);
    		Sheet sheet = workbook.getSheet(Model.SHEET_NAME_CHASSIS);
    		AssertUtil.checkSheetMustNotNull(Model.SHEET_NAME_CHASSIS, sheet);
    		event.updateEvent("正在解析文件结构 ...");
    		Structure structure = StructureBuilder.build(sheet);
    		event.updateEvent("正在加载文件内容 ...");
    		DataSet dataSet = MessageBuilder.build(structure);
    		localClient = new WRVSLocalClient(context);
    		Segment segment = SegmentBuilder.build(localClient, context.getSelectedIds().get(0));
    		dataSet.apply(segment);
    		SegmentUpdater.create(localClient).update(dataSet, event);
    	}catch (Exception e) {
    		event.setEx(e);
		} finally {
			event.setStop(true);
			// 释放占用的资源
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
