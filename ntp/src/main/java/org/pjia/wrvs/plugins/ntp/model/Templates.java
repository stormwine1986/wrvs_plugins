package org.pjia.wrvs.plugins.ntp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件模板集合
 * 
 * @author pjia
 *
 */
public class Templates {
	public static String HQ = "红旗";
	public static String BT = "奔腾";
	public static List<Template> ALL = new ArrayList<>(10);
	static {
		ALL.add(new Template(Templates.HQ, "NCP-HQ.xls"));
		ALL.add(new Template(Templates.BT, "NCP-BT.xls"));
	}
}
