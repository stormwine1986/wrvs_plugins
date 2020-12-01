package org.pjia.wrvs.plugins.ntp.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.pjia.wrvs.plugins.ntp.model.Template;
import org.pjia.wrvs.plugins.ntp.model.Templates;

public class TemplateSelector implements ActionListener {

	private JFrame mainFrame;
	private Semaphore semaphore = new Semaphore(1);
	private JComboBox<Template> comboBox;

	public TemplateSelector() {
		mainFrame = new JFrame();
		mainFrame.setSize(400, 120);
		mainFrame.setTitle("请选择导出模板版本：");
		mainFrame.setLocationRelativeTo(null);
		JPanel panel = new JPanel(new GridLayout(2,1));
		mainFrame.add(panel);
		comboBox = buildTemplateSelector();
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

	private JComboBox<Template> buildTemplateSelector() {
		JComboBox<Template> comboBox = new JComboBox<Template>();
		Iterator<Template> iter = Templates.ALL.iterator();
		while(iter.hasNext()) {
			Template template = iter.next();
			comboBox.addItem(template);
		}
		return comboBox;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		semaphore.release(1);
	}
	
	public Template getSelectedItem() {
		try {
			semaphore.acquire(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return (Template) comboBox.getSelectedItem();
	}

	public void dispose() {
		mainFrame.dispose();
	}
	
	
}
