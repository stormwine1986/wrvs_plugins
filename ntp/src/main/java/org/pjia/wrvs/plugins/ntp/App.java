package org.pjia.wrvs.plugins.ntp;

/**
 * App
 * 
 * @author pjia
 *
 */
public class App {
	
    public static void main(String[] args) {
    	String appName = args[0];
    	if("import".equals(appName)) {
    		ImportApp.main(args);
    	}else if("export".equals(appName)) {
    		ExportApp.main(args);
    	}
    }
}
