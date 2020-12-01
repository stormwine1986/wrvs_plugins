package org.pjia.wrvs.plugins.ntp.model;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import org.apache.commons.io.FileUtils;

import lombok.Data;

/**
 * 导出文件模板
 * 
 * @author pjia
 *
 */
@Data
public class Template {
	
	private String display;
	private String fileName;
	private File tempFile;
	
	public Template(String display, String fileName) {
		this.display = display;
		this.fileName = fileName;
	}
	
	@Override
	public String toString() {
		return display;
	}

	/**
	 * 创建临时文件
	 * 
	 * @param resource
	 * @throws IOException 
	 */
	public void createTempFile(byte[] resource) throws IOException {
		tempFile = File.createTempFile("TEMP" + LocalDate.now(), ".xls");
		FileUtils.writeByteArrayToFile(tempFile, resource);
	}
}
