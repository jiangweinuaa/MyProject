package com.dsc.spos.service.webhook;

import com.dsc.spos.utils.PosPub;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


//2020-08-18 增加统一的WebHook管理，对外部系统推送事件消息
//
public class WebHookUtils {
	
	static Logger logger = Logger.getLogger(WebHookUtils.class);
	
	public static void saveLog(String fileName,String logInfo){
		if(fileName!=null&&fileName.trim().length()>0){
			try{
				// 生成文件路径
				String sdFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());// 当天日期
				String path = System.getProperty("user.dir") + "\\log\\" + fileName + sdFormat + ".txt";
				File file = new File(path);
				
				String dirpath = System.getProperty("user.dir") + "\\log";
				File dirfile = new File(dirpath);
				if (!dirfile.exists()) {
					dirfile.mkdir();
				}
				if (!file.exists()) {
					file.createNewFile();
				}
				
				BufferedWriter output = new BufferedWriter(new FileWriter(file, true));
				// 前面加上时间
				String stFormat = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());// 当天日期
				String slog = stFormat + " " + logInfo + "\r\n";
				output.write(slog);
				output.close();
			}catch(Exception e) {
				
			}
		}else{
			logger.info(logInfo);
		}
	}
	
	public String getTrace(Throwable t) {
		StringWriter stringWriter= new StringWriter();
		PrintWriter writer= new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		StringBuffer buffer= stringWriter.getBuffer();
		return buffer.toString();
	}
	
	public String getUUID(){
		return PosPub.getGUID(false);
	}
}
