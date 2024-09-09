package com.hcmute.prse_be.service;

import com.hcmute.prse_be.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogService {
	private static final Logger logger = LoggerFactory.getLogger(LogService.class);
	
	private static LogService gI;

	public static LogService getgI() {
		if(gI == null) {
			gI = new LogService();
		}
		return gI;
	}

	public void debug(Object obj) {
		logger.debug(JsonUtils.Serialize(obj));
	}
	
	public void info(String data) {
		logger.info(data);
	}

	public void error(Exception data) {
		logger.error("Error==> ", data);
	}

	
	public void system(Object obj) {
		logger.info("[SYSTEM] " + JsonUtils.Serialize(obj));
	}
}
