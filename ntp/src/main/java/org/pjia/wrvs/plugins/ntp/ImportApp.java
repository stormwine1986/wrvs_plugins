package org.pjia.wrvs.plugins.ntp;

import java.io.File;

import javax.swing.JOptionPane;

import org.pjia.wrvs.plugins.client.PluginContext;
import org.pjia.wrvs.plugins.ntp.ui.ImportFileChooser;
import org.pjia.wrvs.plugins.ntp.ui.ImportThread;
import org.pjia.wrvs.plugins.ntp.ui.Monitor;
import org.pjia.wrvs.plugins.ntp.ui.ProgressEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * 导入
 * 
 * @author pjia
 *
 */
@Slf4j
public class ImportApp {
	
	/**
	 * 主方法
	 * 
	 * @param args
	 * @param context
	 */
	public static void run(String[] args, PluginContext context) {
		ImportFileChooser chooser = new ImportFileChooser();
		File file = chooser.getSelected();
		// 没有选择文件直接退出
		if(file == null) return;
		// 启动 Monitor
		Monitor monitor = new Monitor();
		// 启动导入线程
		ProgressEvent event = new ProgressEvent();
		event.updateEvent("导入程序即将开始 ...");
		ImportThread thread = new ImportThread(event, context, file);
		thread.start();
		// 阻塞直到导入结束
		try {
			monitor.watch(event);
			monitor.dispose();
			JOptionPane.showMessageDialog(null, "导入完成");
		} catch (Exception e) {
			e.printStackTrace();
			monitor.dispose();
			// 异常提示
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}
}
