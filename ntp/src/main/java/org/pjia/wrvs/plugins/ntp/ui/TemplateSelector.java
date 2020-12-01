package org.pjia.wrvs.plugins.ntp.ui;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TemplateSelector implements ActionListener {

	private JFrame mainFrame;
	private Semaphore semaphore = new Semaphore(1);
	private JComboBox<String> comboBox;

	public TemplateSelector() {
		mainFrame = new JFrame();
		mainFrame.setSize(400, 120);
		mainFrame.setTitle("请选择导出模板版本：");
		mainFrame.setLocationRelativeTo(null);
		JPanel panel = new JPanel(new GridLayout(2,1));
		mainFrame.add(panel);
		comboBox = new JComboBox<String>();
		comboBox.addItem("红旗");
		comboBox.addItem("奔腾");
		panel.add(comboBox);
		JButton button = new JButton("导  出");
		panel.add(button);
		button.addActionListener(this);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
		try {
			semaphore.acquire(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		semaphore.release(1);
	}
	
	public String getSelectedItem() {
		try {
			semaphore.acquire(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return (String) comboBox.getSelectedItem();
	}

	public void dispose() {
		mainFrame.dispose();
	}
	
	
}
