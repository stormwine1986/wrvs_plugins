package org.pjia.wrvs.plugins.ntp;

import java.awt.Desktop;
import java.io.File;
import java.util.Date;

import javax.swing.JOptionPane;

import org.pjia.wrvs.plugins.client.PluginContext;
import org.pjia.wrvs.plugins.ntp.ui.ExportThread;
import org.pjia.wrvs.plugins.ntp.ui.Monitor;
import org.pjia.wrvs.plugins.ntp.ui.ProgressEvent;
import org.pjia.wrvs.plugins.ntp.ui.TemplateSelector;

import lombok.extern.slf4j.Slf4j;

/**
 * 导出
 * 
 * @author pjia
 *
 */
@Slf4j
public class ExportApp {

	public static void run(String[] args, PluginContext context) {
		// 显示模板选择界面
		TemplateSelector selector = new TemplateSelector();
		String selectedItem = selector.getSelectedItem();
		log.debug("selectedItem = " + selectedItem);
		selector.dispose();
		// 显示 Monitor
		Monitor monitor = new Monitor();
		ProgressEvent event = new ProgressEvent();
		event.updateEvent("导出程序即将开始 ...");
		// 启动导出线程
		try {
			File tempfile = File.createTempFile("TEMP" + new Date().getTime(), ".xls");
			ExportThread thread = new ExportThread(event, context, tempfile);
			thread.start();
			// 阻塞直到导出结束
			monitor.watch(event);
			Desktop.getDesktop().open(tempfile);
			monitor.dispose();
		} catch (Exception e) {
			e.printStackTrace();
			monitor.dispose();
			// 异常提示
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}
}
