package com.dsc.spos.scheduler.eventlistener;

import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dsc.spos.config.SPosConfig;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.xml.utils.ParseXml;


public class GetInfoListener implements ServletContextListener {
	private Logger log = LogManager.getLogger(GetInfoListener.class);
	@Override
	public void contextDestroyed(ServletContextEvent event) {

	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {

			URL xmlPath = this.getClass().getResource("/config/PlugInService.xml"); //xml 取得設定檔
			URL propPath = this.getClass().getResource("/config/jdbc.properties"); //取得 jdbc 設定檔
			ParseXml pXml=new ParseXml();
			StaticInfo.psc = pXml.xmlToBean(xmlPath,propPath, SPosConfig.class); //取得混合過後的設定
		    //System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			pXml=null;
			propPath=null;
			xmlPath=null;
		    

			
		}catch(Exception e){
			}
		}
      


}
