package org.pjia.wrvs.plugins.ntp;

import java.io.File;

import javax.swing.JOptionPane;

import org.pjia.wrvs.plugins.client.PluginContext;
import org.pjia.wrvs.plugins.event.PluginEventMgr;
import org.pjia.wrvs.plugins.ntp.ui.ImportFileChooser;
import org.pjia.wrvs.plugins.ntp.ui.ImportThread;
import org.pjia.wrvs.plugins.ntp.ui.Monitor;

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
		Monitor monitor = new Monitor("导入程序");
		// 启动导入线程
		PluginEventMgr.addMonitor(monitor);
		ImportThread thread = new ImportThread(context, file);
		monitor.attach(thread);
		thread.start();
		try {
			// 阻塞直到导入结束
			thread.join();
			monitor.dispose();
		} catch (Exception e) {
			log.error("", e);
			monitor.dispose();
			// 异常提示
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}
}
