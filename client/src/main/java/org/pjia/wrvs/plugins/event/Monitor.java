package org.pjia.wrvs.plugins.event;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.pjia.wrvs.plugins.event.Event;
import org.pjia.wrvs.plugins.event.IMonitor;

import lombok.Getter;

/**
 * 工作线程监视者
 * 
 * 用法：
  Monitor monitor = new Monitor("工作线程任务");
  // 注册为插件事件监听者 
  PluginEventMgr.addMonitor(monitor);
  // 创建工作线程
  TaskThread thread = new TaskThread();
  // 把监视者附加到工作线程，监听工作线程产生的事件
  monitor.watch(thread);
  // 启动工作线程并等待线程结束
  thread.start();
  thread.join();
  if(monitor.getEx() != null) {
	// 工作线程有异常
	throw monitor.getEx();
  }
  // 解散监视者
  monitor.dispose();
 * 
 * 
 * @author pjia
 *
 */
public class Monitor implements IMonitor {
	
	private JLabel label;
	private JFrame mainFrame;
	private Thread worker;
	@Getter
	private Exception ex;

	public Monitor(String name) {
		mainFrame = new JFrame();
		mainFrame.setSize(350, 100);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setUndecorated(true);
		Border border = BorderFactory.createRaisedBevelBorder();
		JPanel panel = new JPanel(new BorderLayout());
		mainFrame.add(panel);
		panel.setBorder(border);
		label = new JLabel(name + "即将开始", JLabel.CENTER);
		panel.add(label,BorderLayout.CENTER);
		mainFrame.setVisible(true);
	}

	/**
	 * 解散
	 */
	public void dispose() {
		mainFrame.dispose();
	}

	@Override
	public void fired(Event event) {
		if(event.getThreadId() == this.worker.getId()) {
			if(event.isExEvent()) {
				this.ex = event.getEx();
			} else {
				label.setText(event.getMessage());				
			}
		}
	}

	/**
	 * 观察指定的 worker 线程
	 * 
	 * @param thread
	 */
	public void watch(Thread thread) {
		this.worker = thread;
	}
}
