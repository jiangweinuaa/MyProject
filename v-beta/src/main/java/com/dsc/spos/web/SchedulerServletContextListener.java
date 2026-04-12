package com.dsc.spos.web;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dsc.spos.config.SPosConfig;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.xml.utils.ParseXml;

public class SchedulerServletContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			//停止排程.
//			ScheduleTool.getInstance().stop();
		} catch (Exception e) {
			
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		try 
		{
			WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(arg0.getServletContext());
			StaticInfo.dao = null;
			
			URL xmlPath = this.getClass().getResource("/config/PlugInService.xml"); //xml 取得設定檔
			URL propPath = this.getClass().getResource("/config/jdbc.properties"); //取得 jdbc 設定檔
			ParseXml pXml=new ParseXml();
			SPosConfig psc = pXml.xmlToBean(xmlPath, propPath, SPosConfig.class); //取得混合過後的設定
			pXml=null;
			propPath=null;
			xmlPath=null;
			
			//数据库判断依据
			StaticInfo.dbType=0;
			if (psc.getDataBaseConfig().getDriverClassName().getValue().toLowerCase().contains("postgresql")) 
			{
				StaticInfo.dbType=1;
			}
			
			StaticInfo.dao = (DsmDAO)ctx.getBean("sposDao");

            StaticInfo.dao_pos2 = (DsmDAO) ctx.getBean("Pos2Dao");

            StaticInfo.dao_crm2 = (DsmDAO) ctx.getBean("Crm2Dao");

//			List<Map<String, Object>> pos2_map=StaticInfo.dao_pos2.executeQuerySQL("select * from ta_org where organizationno='01' ", null);
//
//			List<Map<String, Object>> crm2_map=StaticInfo.dao_crm2.executeQuerySQL("select * from crm_shop where shopid='01' ",null);
//
//			if (pos2_map != null && pos2_map.size()>0)
//			{
//			  System.out.println("pos2:"+ pos2_map.get(0).get("ORGANIZATIONNO").toString());
//			}
//
//			if (crm2_map != null && crm2_map.size()>0)
//			{
//				System.out.println("crm2:"+ crm2_map.get(0).get("SHOPID").toString());
//			}

		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}
		
	}

}
