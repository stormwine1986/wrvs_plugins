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

import com.mks.api.Command;
import com.mks.api.Option;
import com.mks.api.response.APIException;

/**
 * 导入线程
 * 
 * @author pjia
 *
 */
public class ImportThread extends Thread {
	
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
    		WRVSLocalClient localClient = new WRVSLocalClient(context);
    		workbook = new HSSFWorkbook(is);
    		Sheet sheet = workbook.getSheet(Model.SHEET_NAME_CHASSIS);
    		AssertUtil.checkSheetMustNotNull(Model.SHEET_NAME_CHASSIS, sheet);
    		PluginEventMgr.recordEvent(new Event("正在解析文件结构 ... "));
    		Structure structure = StructureBuilder.create().build(sheet);
    		PluginEventMgr.recordEvent(new Event("正在加载文件内容 ... "));
    		DataSet dataSet = MessageBuilder.create(localClient).build(structure);
    		Segment segment = SegmentBuilder.create(localClient).build(context.getSelectedIds().get(0));
    		dataSet.apply(segment);
    		SegmentUpdater.create(localClient).update(dataSet);
    		viewDocument(localClient, context.getSelectedIds().get(0));
    	}catch (Exception e) {
    		PluginEventMgr.recordEvent(new Event(e));
		} finally {
			// 释放占用的资源
			if(workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void viewDocument(WRVSLocalClient localClient, String id) throws APIException {
		Command cmd = new Command("im", "viewsegment");
		cmd.addOption(new Option("--gui"));
		cmd.addOption(new Option("fields", "Section,Category,Message Name,Signal Name,Bit Number,State,ID"));
		cmd.addSelection(id);
		localClient.execute(cmd);
	}
}
