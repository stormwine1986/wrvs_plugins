package org.pjia.wrvs.plugins.ntp.utils;

import java.util.Locale;

import org.apache.poi.ss.usermodel.Sheet;

public class AssertUtil {
	
	public static void checkSheetMustNotNull(String name, Sheet sheet) {
		if(sheet == null) {
			throw new IllegalStateException(String.format(Locale.ROOT, "Sheet [%s] 不存在。", name));
		}
	}
}
