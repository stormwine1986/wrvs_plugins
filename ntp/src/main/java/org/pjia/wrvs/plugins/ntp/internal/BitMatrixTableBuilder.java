package org.pjia.wrvs.plugins.ntp.internal;

import com.google.gson.JsonArray;

/**
 * Bit Matrix 表格构建者
 * 
 * @author pjia
 *
 */
public class BitMatrixTableBuilder {
	
	/**
	 * 构造
	 * 
	 * @param matrix
	 * @return
	 */
	public static String build(JsonArray matrix) {
		StringBuffer buffer = new StringBuffer("<!-- MKS HTML --><table>");
		for(int i = 0; i < matrix.size(); i++) {
			JsonArray line = matrix.get(i).getAsJsonArray();
			buffer.append("<tr>");
			for(int j = 0; j < line.size(); j++) {
				String value = line.get(j).getAsString();
				buffer.append("<td>");
				buffer.append(value);
				buffer.append("</td>");
			}
			buffer.append("</tr>");
		}
		buffer.append("</table>");
		return buffer.toString();
	}
}
