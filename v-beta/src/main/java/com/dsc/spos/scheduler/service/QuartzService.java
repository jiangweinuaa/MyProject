package com.dsc.spos.scheduler.service;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service("quartzService")
public class QuartzService  {
	Logger logger = LogManager.getLogger(QuartzService.class.getName());
	
	public QuartzService() {
		logger.debug("\r\nConstruct QuartzService");
	}

	public void insertIntoDB() {
		logger.debug("\r\nRun Insert Method");
	}
	
	
}
