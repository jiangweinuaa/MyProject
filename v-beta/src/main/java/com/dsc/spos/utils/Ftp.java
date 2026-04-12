package com.dsc.spos.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ftp implements FtpInterface 
{

	Logger logger = LogManager.getLogger(Ftp.class.getName());
	
	@Override
	public FTPClient ftp(String ip, String user, String password) 
	{		
		FTPClient ftpClient = new FTPClient();
		try 
		{
			ftpClient.connect(ip);
			boolean bloginOk=ftpClient.login(user, password);
			int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
			if (bloginOk) 
			{
				if(!FTPReply.isPositiveCompletion(replyCode))
				{
					logger.error("\r\nFTP应答失败：hostname="+ip+",user="+user+"password="+password+",replyCode=" + replyCode);
					
					ftpClient.disconnect();
				}
				else 
				{
					String LOCAL_CHARSET = "GBK";
					// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
					if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))) 
					{
						LOCAL_CHARSET = "UTF-8";
					}
					ftpClient.setControlEncoding(LOCAL_CHARSET);
					ftpClient.enterLocalPassiveMode();// 设置被动模式
					ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);// 设置传输的模式
				}
			}
			else 
			{
				logger.error("\r\nFTP登录login失败：hostname="+ip+",user="+user+"password="+password);
				
			}
			
		} 
		catch (SocketException e) 
		{
			logger.error("\r\nFTP initFtpClient()失败：hostname="+ip+",,user="+user+"password="+password+",错误原因："+e.getMessage());
		} 
		catch (IOException e) 
		{
			logger.error("\r\nFTP initFtpClient()失败：hostname="+ip+",,user="+user+"password="+password+",错误原因："+e.getMessage());
		}
 
		if (!ftpClient.isConnected()) 
		{
			ftpClient = null;
		}
 
		return ftpClient;

	}
	
	@Override
	public ArrayList<String[]> excel(InputStream in) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
