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
import org.pjia.wrvs.plugins.event.Event;
import org.pjia.wrvs.plugins.event.PluginEventMgr;
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
	
	private WRVSLocalClient localClient;
	private PluginContext context;
	private File file;
	
	public ImportThread(PluginContext context, File file) {
		this.context = context;
		this.file = file;
	}

	@Override
	public void run() {
		Workbook workbook = null;
    	try (InputStream is = new FileInputStream(file)) {
    		workbook = new HSSFWorkbook(is);
    		Sheet sheet = workbook.getSheet(Model.SHEET_NAME_CHASSIS);
    		AssertUtil.checkSheetMustNotNull(Model.SHEET_NAME_CHASSIS, sheet);
    		PluginEventMgr.recordEvent(new Event("正在解析文件结构 ... "));
    		Structure structure = StructureBuilder.build(sheet);
    		PluginEventMgr.recordEvent(new Event("正在加载文件内容 ... "));
    		DataSet dataSet = MessageBuilder.build(structure);
    		localClient = new WRVSLocalClient(context);
    		Segment segment = SegmentBuilder.build(localClient, context.getSelectedIds().get(0));
    		dataSet.apply(segment);
    		SegmentUpdater.create(localClient).update(dataSet);
    		localClient.viewDocument(context.getSelectedIds().get(0));
    	}catch (Exception e) {
    		PluginEventMgr.recordEvent(new Event(e));
		} finally {
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
