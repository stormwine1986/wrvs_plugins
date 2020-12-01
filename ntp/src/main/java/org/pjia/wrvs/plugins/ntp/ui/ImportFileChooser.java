package org.pjia.wrvs.plugins.ntp.ui;

import java.io.File;

import javax.swing.JFileChooser;

/**
 * 导入文件选择窗体
 * 
 * @author pjia
 *
 */
public class ImportFileChooser {

	private JFileChooser chooser;

	public ImportFileChooser() {
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(ImportFileFilter.instance);
		chooser.setAcceptAllFileFilterUsed(false);
	}
	
	/**
	 * 获取选择的文件
	 * 
	 * @return
	 */
	public File getSelected() {
		int opt = chooser.showOpenDialog(null);
		if(opt == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}
}
