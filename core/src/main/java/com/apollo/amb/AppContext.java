package com.apollo.amb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppContext {

	private static boolean useDebug = true;
	
	private static Map dataSet = new HashMap();
	private static AmbConfiguration conf = null;
	
	public static Object getValue(String name) {
		return dataSet.get(name);
	}
	
	public static void setValue(String name, Object value) {
		dataSet.put(name, value);
	}

	public static void setConf(AmbConfiguration conf) {
		AppContext.conf = conf;
	}
	
	public static AmbConfiguration getConf() {
		return AppContext.conf;
	}
	
	public static String getLoginPageUrl() {
		if (useDebug) {
			return "http://localhost:3000/src/login.html";
		} else {
			return conf.getServer() + conf.getContext() + "/app/login.html";
		}
	}
	
	public static String getAdminIndexPageUrl() {
		if (useDebug) {
			return "http://localhost:3000/src/index.html";
		} else {
			return conf.getServer() + conf.getContext() + "/app/index.html";
		}
	}
	
	public static String getUserIndexPageUrl() {
		if (useDebug) {
			return "http://localhost:3000/src/index-c.html";
		} else {
			return conf.getServer() + conf.getContext() + "/app/index-c.html";
		}
	}
}
