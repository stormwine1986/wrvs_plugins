package org.pjia.wrvs.plugins.ntp.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.pjia.wrvs.plugins.event.Event;
import org.pjia.wrvs.plugins.event.IMonitor;

import lombok.Getter;

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
	 * 指定 worker 线程
	 * 
	 * @param thread
	 */
	public void attach(Thread thread) {
		this.worker = thread;
	}
}
