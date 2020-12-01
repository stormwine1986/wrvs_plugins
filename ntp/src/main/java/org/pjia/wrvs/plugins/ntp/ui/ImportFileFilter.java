package org.pjia.wrvs.plugins.ntp.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * 导入文件的格式
 * 
 * @author pjia
 *
 */
public class ImportFileFilter extends FileFilter {
	
	public static FileFilter instance = new ImportFileFilter();

	@Override
	public boolean accept(File file) {
		return file.isDirectory() || file.getName().endsWith(".xls");
	}

	@Override
	public String getDescription() {
		return "网络通信协议(.xls)";
	}

}
