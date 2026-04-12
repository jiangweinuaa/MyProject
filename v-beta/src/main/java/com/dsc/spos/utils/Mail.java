package com.dsc.spos.utils;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;


//https://www.cnblogs.com/panchanggui/p/9991213.html
public class Mail 
{
	Logger logger = LogManager.getLogger(Mail.class.getName());
	
	public Mail() 
	{
		
	}
	
	/**
	 * 发送邮件
	 * @param receiverEmail 收件人邮箱地址
	 * @param title 标题
	 * @param message 正文
	 * @param filenames 附件文件路径
	 * @throws Exception
	 */
	public void sendMail(String[] receiverEmail, String title, String message,String[] filenames) 
	{ 
		try
		{			
			if (receiverEmail==null || receiverEmail.length==0) 
			{
				return;				
			}			
			
			// 获取系统属性
			Properties properties = System.getProperties();

			// 设置邮件服务器
			properties.setProperty("mail.smtp.host", "smtp.qq.com");

			properties.put("mail.smtp.auth", "true");	 

			properties.put("mail.smtp.timeout", 5000);//邮件接收时间限制，单位毫秒
			properties.put("mail.smtp.connectiontimeout", 5000);//连接时间限制，单位毫秒
			properties.put("mail.smtp.writetimeout", 5000);//邮件发送时间限制，单位毫秒

			final String emailAddress = "2448041958@qq.com";
			final String emailPassword = "syvxqfupbujqdjaa";
			Session session = Session.getDefaultInstance(properties, new Authenticator() 
			{
				public PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(emailAddress, emailPassword); //发件人邮件用户名、授权码
				}
			});

			// 创建默认的 MimeMessage 对象
			MimeMessage sendMessage = new MimeMessage(session);

			// Set From: 头部头字段
			sendMessage.setFrom(new InternetAddress(emailAddress));
			
			for (int i = 0; i < receiverEmail.length; i++) 
			{
				if (receiverEmail[i]!=null && receiverEmail[i].trim().equals("")==false) 
				{
					// Set To: 头部头字段
					sendMessage.addRecipient(Message.RecipientType.TO,
							new InternetAddress(receiverEmail[i]));
				}				
			}
			
			// Set Subject: 头部头字段
			sendMessage.setSubject(title);

			// 设置消息体
			//sendMessage.setText(message);
			sendMessage.setContent(message, "text/html;charset=GBK");
			
			//发送附件
			if (filenames!=null && filenames.length>0) 
			{
				for (int i = 0; i < filenames.length; i++) 
				{
					if (filenames[i]!=null && filenames[i].trim().equals("")==false) 
					{
						MimeMultipart multipart = new MimeMultipart();
						MimeBodyPart file2 = new MimeBodyPart();
				        DataHandler dataHandler3 = new DataHandler(new FileDataSource(filenames[i]));
				        file2.setDataHandler(dataHandler3);
				        try 
				        {
							file2.setFileName(MimeUtility.encodeText(dataHandler3.getName()));
						} 
				        catch (UnsupportedEncodingException e) 
				        {
				        	logger.error("\r\n******发送邮件SendMail报错信息UnsupportedEncodingException:" + e.getMessage() + "******\r\n");
						}
				        multipart.addBodyPart(file2);
				        
				        //分别为mixed，related和alternative。
				        //两部分数据必须要设置其中的关系，mixed是没关系的关系，比如正文和附件，二者没有关系；
				        //related是引用关系，比如在正文中引用图片；
				        //alternative是二者选其一
				        multipart.setSubType("mixed");//附件   
				        sendMessage.setContent(multipart);
				        //
				        file2=null;
				        multipart=null;
					}					
				}
			}
		        
			// 发送消息
			Transport.send(sendMessage);		

			sendMessage=null;
			session=null;
			properties=null;
		} 
		catch (MessagingException e) 
		{
			try 
			{
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);		
				
				pw.flush();
				pw.close();			
				
				errors.flush();
				errors.close();

				logger.error("\r\n******发送邮件SendMail报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******发送邮件SendMail报错信息" + e.getMessage() + "******\r\n");
			}	

		}

	}
	
	/**
	 * 发送邮件（有返回值）
	 * @param receiverEmail 收件人邮箱地址
	 * @param title 标题
	 * @param message 正文
	 * @param filenames 附件文件路径
	 * @return Map<"isSuccess":true,"errmsg":"错误描述">
	 */
	public Map<String,Object> sendMailRes(String[] receiverEmail, String title, String message,String[] filenames)
	{ 
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("errmsg","");
		map.put("isSuccess", false);
		try
		{			
			if (receiverEmail==null || receiverEmail.length==0) 
			{
				map.put("errmsg","收件人邮箱地址为空！");
				map.put("isSuccess", false);
				return map;				
			}			
			
			// 获取系统属性
			Properties properties = System.getProperties();

			// 设置邮件服务器
			properties.setProperty("mail.smtp.host", "smtp.qq.com");

			properties.put("mail.smtp.auth", "true");	 

			properties.put("mail.smtp.timeout", 5000);//邮件接收时间限制，单位毫秒
			properties.put("mail.smtp.connectiontimeout", 5000);//连接时间限制，单位毫秒
			properties.put("mail.smtp.writetimeout", 5000);//邮件发送时间限制，单位毫秒

			final String emailAddress = "2448041958@qq.com";
			final String emailPassword = "syvxqfupbujqdjaa";
			Session session = Session.getDefaultInstance(properties, new Authenticator() 
			{
				public PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(emailAddress, emailPassword); //发件人邮件用户名、授权码
				}
			});

			// 创建默认的 MimeMessage 对象
			MimeMessage sendMessage = new MimeMessage(session);

			// Set From: 头部头字段
			sendMessage.setFrom(new InternetAddress(emailAddress));
			
			for (int i = 0; i < receiverEmail.length; i++) 
			{
				if (receiverEmail[i]!=null && receiverEmail[i].trim().equals("")==false) 
				{
					// Set To: 头部头字段
					sendMessage.addRecipient(Message.RecipientType.TO,
							new InternetAddress(receiverEmail[i]));
				}				
			}
			
			// Set Subject: 头部头字段
			sendMessage.setSubject(title);

			// 设置消息体
			//sendMessage.setText(message);
			sendMessage.setContent(message, "text/html;charset=GBK");
			//发送附件
			if (filenames!=null && filenames.length>0) 
			{
				for (int i = 0; i < filenames.length; i++) 
				{
					if (filenames[i]!=null && filenames[i].trim().equals("")==false) 
					{
						MimeMultipart multipart = new MimeMultipart();
						MimeBodyPart file2 = new MimeBodyPart();
				        DataHandler dataHandler3 = new DataHandler(new FileDataSource(filenames[i]));
				        file2.setDataHandler(dataHandler3);
				        try 
				        {
							file2.setFileName(MimeUtility.encodeText(dataHandler3.getName()));
						} 
				        catch (UnsupportedEncodingException e) 
				        {
				        	logger.error("\r\n******发送邮件SendMail报错信息UnsupportedEncodingException:" + e.getMessage() + "******\r\n");
						}
				        multipart.addBodyPart(file2);
				        
				        //分别为mixed，related和alternative。
				        //两部分数据必须要设置其中的关系，mixed是没关系的关系，比如正文和附件，二者没有关系；
				        //related是引用关系，比如在正文中引用图片；
				        //alternative是二者选其一
				        multipart.setSubType("mixed");//附件   
				        sendMessage.setContent(multipart);
				        //
				        file2=null;
				        multipart=null;
					}					
				}
			}
		        
			// 发送消息
			Transport.send(sendMessage);
			
			map.put("errmsg","");
			map.put("isSuccess", true);

			sendMessage=null;
			session=null;
			properties=null;
		} 
		catch (MessagingException e) 
		{
			try 
			{
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);		
				
				pw.flush();
				pw.close();			
				
				errors.flush();
				errors.close();

				logger.error("\r\n******发送邮件SendMail报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");
				map.put("errmsg","发送邮件报错信息"+e.getMessage()+" "+errors.toString());
				map.put("isSuccess", false);
				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******发送邮件SendMail报错信息" + e.getMessage() + "******\r\n");
				map.put("errmsg","发送邮件报错信息"+e.getMessage());
				map.put("isSuccess", false);
			}	

		}

		return map;
	}

}
