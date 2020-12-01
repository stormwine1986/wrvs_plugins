package org.pjia.wrvs.plugins.ntp.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class Monitor {
	
	private JLabel label;
	private JFrame mainFrame;

	public Monitor() {
		mainFrame = new JFrame();
		mainFrame.setSize(350, 100);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setUndecorated(true);
		Border border = BorderFactory.createRaisedBevelBorder();
		JPanel panel = new JPanel(new BorderLayout());
		mainFrame.add(panel);
		panel.setBorder(border);
		label = new JLabel("", JLabel.CENTER);
		panel.add(label,BorderLayout.CENTER);
		mainFrame.setVisible(true);
	}

	/**
	 * 监视 Progress Event
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void watch(ProgressEvent event) throws Exception {
		while(true) {
			if(event.isStop() && event.getEx() != null) {
				// 因异常结束, 抛出异常
				throw event.getEx();
			}
			if(event.isStop() && event.getEx() == null) {
				// 正常结束，直接退出 watch 方法
				return;
			}
			// 进行中, 更新 Monitor
			String eventMessage = event.getLatestEvent();
			update(eventMessage);
			Thread.sleep(200L);
		}
	}

	private void update(String eventMessage) {
		label.setText(eventMessage);
	}

	public void dispose() {
		mainFrame.dispose();
	}
}
