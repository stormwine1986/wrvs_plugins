package org.pjia.wrvs.plugins.ntp;

import java.awt.Desktop;

import javax.swing.JOptionPane;

import org.pjia.wrvs.plugins.client.PluginContext;
import org.pjia.wrvs.plugins.event.Monitor;
import org.pjia.wrvs.plugins.event.PluginEventMgr;
import org.pjia.wrvs.plugins.ntp.model.Template;
import org.pjia.wrvs.plugins.ntp.ui.ExportThread;
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
		Template template = selector.getSelectedItem();
		log.debug("template = " + template);
		selector.dispose();
		// 显示 Monitor
		Monitor monitor = new Monitor("导出程序");
		PluginEventMgr.addMonitor(monitor);
		// event.updateEvent("导出程序即将开始");
		// PluginEventMgr.recordEvent(new Event("导出程序即将开始"));
		// 启动导出线程
		try {
			ExportThread thread = new ExportThread(context, template);
			monitor.watch(thread);
			thread.start();
			// 阻塞直到导出结束
			thread.join();
			// monitor.watch(event);
			if(monitor.getEx() != null) throw monitor.getEx(); // 执行线程发生了异常
			Desktop.getDesktop().open(template.getTempFile());
			monitor.dispose();
		} catch (Exception e) {
			log.error("", e);
			monitor.dispose();
			// 异常提示
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}
}
