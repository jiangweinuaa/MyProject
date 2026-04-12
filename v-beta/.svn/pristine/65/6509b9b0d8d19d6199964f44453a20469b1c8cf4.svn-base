package com.dsc.spos.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dsc.spos.utils.FtpInterface;

public class FtpUtils implements FtpInterface 
{
	//    //ftp服务器地址
	public String hostname = "101.37.33.19";
	//ftp服务器端口号默认为21
	public Integer port = 21 ;
	//ftp登录账号
	public String username = "ftptest";
	//ftp登录密码
	public String password = "ftptest";
	
	public FTPClient client =  null;
	
	Logger logger = LogManager.getLogger(FtpUtils.class.getName());

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public FTPClient getFtpClient() {
		return client;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.client = ftpClient;
	}

	/**
	 * 初始化ftp服务器
	 */
	private boolean initFtpClient() 
	{
		client = new FTPClient();
		try 
		{			
			client.connect(hostname, port); //连接ftp服务器
			boolean bloginOk=client.login(username, password); //登录ftp服务器
			
			if (bloginOk) 
			{
				int replyCode = client.getReplyCode(); //是否成功登录服务器
								
				if(!FTPReply.isPositiveCompletion(replyCode))
				{					
					logger.error("\r\nFTP应答失败：hostname="+hostname+",port="+port+",username="+username+"password="+password+",replyCode=" + replyCode);
					
					client.disconnect();
					
					return false;
				}
				else 
				{
					String LOCAL_CHARSET = "GBK";
					// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
					if (FTPReply.isPositiveCompletion(client.sendCommand("OPTS UTF8", "ON"))) 
					{
						LOCAL_CHARSET = "UTF-8";
					}
					client.setControlEncoding(LOCAL_CHARSET);
					client.enterLocalPassiveMode();// 设置被动模式
					client.setFileType(FTPClient.BINARY_FILE_TYPE);// 设置传输的模式
				
					return  true;
				}				
			}	
			else 
			{
				logger.error("\r\nFTP登录login失败：hostname="+hostname+",port="+port+",username="+username+"password="+password);
				return false;
			}
		}
		catch (MalformedURLException e) 
		{    
			logger.error("\r\nFTP initFtpClient()失败：hostname="+hostname+",port="+port+",username="+username+"password="+password+",错误原因："+e.getMessage());
			return false;
		}
		catch (IOException e) 
		{ 
			logger.error("\r\nFTP initFtpClient()失败：hostname="+hostname+",port="+port+",username="+username+"password="+password+",错误原因："+e.getMessage());
			return false;
		} 
	}

	/**
	 * 上传文件
	 * @param pathname ftp服务保存地址
	 * @param fileName 上传到ftp的文件名
	 *  @param originfilename 待上传文件的名称（绝对地址） * 
	 * @return
	 */
	public boolean uploadFile( String pathname, String fileName,String originfilename){
		boolean flag = false;
		InputStream inputStream = null;
		try{
			//System.out.println("开始上传文件");
			inputStream = new FileInputStream(new File(originfilename));
			initFtpClient();
			client.setFileType(client.BINARY_FILE_TYPE);
			CreateDirecroty(pathname);
			//            ftpClient.makeDirectory(pathname);
			client.changeWorkingDirectory(pathname);
			client.enterLocalPassiveMode();
			client.storeFile(new String(fileName.getBytes("UTF-8"),"iso-8859-1"), inputStream);
			inputStream.close();
			client.logout();
			flag = true;
			//System.out.println("上传文件成功");
		}catch (Exception e) {

		}finally{
			if(client.isConnected()){ 
				try{
					client.disconnect();
				}catch(IOException e){

				}
			} 
			if(null != inputStream){
				try {
					inputStream.close();
				} catch (IOException e) {

				} 
			} 
		}
		return true;
	}
	/**
	 * 上传文件
	 * @param pathname ftp服务保存地址
	 * @param fileName 上传到ftp的文件名
	 * @param inputStream 输入文件流 
	 * @return
	 */
	public boolean uploadFile( String pathname, String fileName,InputStream inputStream){
		boolean flag = false;
		try{
			//System.out.println("开始上传文件");
			initFtpClient();
			client.setFileType(client.BINARY_FILE_TYPE);
			CreateDirecroty(pathname);
			//            ftpClient.makeDirectory(pathname);
			client.changeWorkingDirectory(pathname);
			//被动模式
			client.enterLocalPassiveMode();
			client.storeFile(new String(fileName.getBytes("UTF-8"),"iso-8859-1"), inputStream);
			inputStream.close();
			client.logout();
			flag = true;
			//System.out.println("上传文件成功");
		}catch (Exception e) {

		}finally{
			if(client.isConnected()){ 
				try{
					client.disconnect();
				}catch(IOException e){

				}
			} 
			if(null != inputStream){
				try {
					inputStream.close();
				} catch (IOException e) {

				} 
			} 
		}
		return true;
	}
	//改变目录路径
	public boolean changeWorkingDirectory(String directory) {
		boolean flag = true;
		try {
			flag = client.changeWorkingDirectory(directory);
			if (flag) {
				//System.out.println("进入文件夹" + directory + " 成功！");

			} else {
				//System.out.println("进入文件夹" + directory + " 失败！开始创建文件夹");
			}
		} catch (IOException ioe) {
			
		}
		return flag;
	}

	//创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
	public boolean CreateDirecroty(String remote) throws IOException {
		boolean success = true;
		String directory = remote + "/";
		// 如果远程目录不存在，则递归创建远程服务器目录
		if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory))) {
			int start = 0;
			int end = 0;
			if (directory.startsWith("/")) {
				start = 1;
			} else {
				start = 0;
			}
			end = directory.indexOf("/", start);
			String path = "";
			String paths = "";
			while (true) {
				String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "UTF-8");
				path = path + "/" + subDirectory;
				if (!existFile(path)) {
					if (makeDirectory(subDirectory)) {
						changeWorkingDirectory(subDirectory);
					} else {
						//System.out.println("创建目录[" + subDirectory + "]失败");
						changeWorkingDirectory(subDirectory);
					}
				} else {
					changeWorkingDirectory(subDirectory);
				}

				paths = paths + "/" + subDirectory;
				start = end + 1;
				end = directory.indexOf("/", start);
				// 检查所有目录是否创建完毕
				if (end <= start) {
					break;
				}
			}
		}
		return success;
	}

	//判断ftp服务器文件是否存在    
	public boolean existFile(String path) throws IOException {
		boolean flag = false;
		FTPFile[] ftpFileArr = client.listFiles(path);
		if (ftpFileArr.length > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 获取以xxx开头的文件，如果startname="" 返回所有文件
	 * @param path
	 * @param startname 以xxx开头
	 * @return
	 * @throws IOException
	 */
	public List<String> getFileInfo(String path,String startname) throws IOException 
	{	
		List<String> findFiles=new ArrayList<>();
		//
		changeWorkingDirectory(path);

		String[] fs= client.listNames();		

		for (int i = 0; i < fs.length; i++) 
		{
			//如果为空值，是所有文件
			if (startname.equals("")) 
			{
				findFiles.add(fs[i]);
			}
			else 
			{
				if (fs[i].startsWith(startname))
				{
					findFiles.add(fs[i]);
				}
			}			
		}

		return findFiles;
	}	


	//创建目录
	public boolean makeDirectory(String dir) {
		boolean flag = true;
		try {
			flag = client.makeDirectory(dir);
			if (flag) {
				//System.out.println("创建文件夹" + dir + " 成功！");

			} else {
				//System.out.println("创建文件夹" + dir + " 失败！");
			}
		} catch (Exception e) {
			
		}
		return flag;
	}

	/** * 下载文件 * 
	 * @param pathname FTP服务器文件目录 * 
	 * @param filename 文件名称 * 
	 * @param localpath 下载后的文件路径 * 
	 * @param suffixName 后缀名.xls .xlsx
	 * @return */
	public  boolean downloadFile(String pathname, String filename, String localpath,String... suffixName)
	{ 
		boolean flag = false; 
		OutputStream os=null;
		try 
		{ 
			if (initFtpClient())
			{
				//切换FTP目录 
				client.changeWorkingDirectory(pathname); 

				//被动模式
				client.enterLocalPassiveMode();

				FTPFile[] ftpFiles = client.listFiles(); 

				List<String> Listsuffix=new ArrayList<String>();
				if (filename.equals("")) 
				{
					for (int iis = 0; iis < suffixName.length; iis++) 
					{
						Listsuffix.add(suffixName[iis]);
					}				
				}			

				for(FTPFile file : ftpFiles)
				{ 
					//下载全部文件
					if (filename.equals("")) 
					{
						if (Listsuffix.size()==0) 
						{
							File localFile = new File(localpath + "/" + file.getName()); 
							os = new FileOutputStream(localFile); 
							String encodeName=new String(file.getName().getBytes("UTF-8"),"iso-8859-1");
							boolean bok=client.retrieveFile(encodeName, os); 						
							if (bok==false) 
							{
								encodeName=new String(file.getName().getBytes("GBK"),"iso-8859-1");
								bok=client.retrieveFile(encodeName, os); 
							}
							os.close(); 
						}
						else 
						{
							int iPos=file.getName().lastIndexOf(".");
							String fileSuffix=file.getName().substring(iPos);
							if (Listsuffix.contains(fileSuffix)) 
							{
								File localFile = new File(localpath + "/" + file.getName()); 
								os = new FileOutputStream(localFile); 
								String encodeName=new String(file.getName().getBytes("UTF-8"),"iso-8859-1");
								boolean bok=client.retrieveFile(encodeName, os); 						
								if (bok==false) 
								{
									encodeName=new String(file.getName().getBytes("GBK"),"iso-8859-1");
									bok=client.retrieveFile(encodeName, os); 
								}
								os.close(); 
							}					
						}					
					}
					else 
					{
						if(filename.equalsIgnoreCase(file.getName()))
						{ 
							File localFile = new File(localpath + "/" + file.getName()); 
							os = new FileOutputStream(localFile); 
							String encodeName=new String(file.getName().getBytes("UTF-8"),"iso-8859-1");
							boolean bok=client.retrieveFile(encodeName, os); 						
							if (bok==false) 
							{
								encodeName=new String(file.getName().getBytes("GBK"),"iso-8859-1");
								bok=client.retrieveFile(encodeName, os); 
							}
							os.close(); 
						} 
					}				
				} 
				client.logout(); 
				flag = true; 				
			}			
		} 
		catch (Exception e) 
		{ 			
			logger.error("\r\nFTP downloadFile()1失败：hostname="+hostname+",port="+port+",username="+username+"password="+password+",错误原因："+e.getMessage());
		} 
		finally
		{ 
			if(client.isConnected())
			{ 
				try
				{
					client.disconnect();
				}
				catch(IOException e)
				{
					logger.error("\r\nFTP downloadFile()2失败：hostname="+hostname+",port="+port+",username="+username+"password="+password+",错误原因："+e.getMessage());
				}
			} 
			if(null != os)
			{
				try 
				{
					os.close();
				} 
				catch (IOException e) 
				{
					logger.error("\r\nFTP downloadFile()3失败：hostname="+hostname+",port="+port+",username="+username+"password="+password+",错误原因："+e.getMessage());
				} 
			} 
		} 
		return flag; 
	}

	/** * 删除文件 * 
	 * @param pathname FTP服务器保存目录 * 
	 * @param filename 要删除的文件名称 * 
	 * @return */ 
	public boolean deleteFile(String pathname, String filename){ 
		boolean flag = false; 
		try { 
			//System.out.println("开始删除文件");
			initFtpClient();
			//切换FTP目录 
			client.changeWorkingDirectory(pathname); 
			client.dele(new String(filename.getBytes("UTF-8"),"iso-8859-1")); 
			client.logout();
			flag = true; 
			//System.out.println("删除文件成功");
		} catch (Exception e) { 
			//System.out.println("删除文件失败");
			 
		} finally {
			if(client.isConnected()){ 
				try{
					client.disconnect();
				}catch(IOException e){
					
				}
			} 
		}
		return flag; 
	}

	public static void main(String[] args) throws FileNotFoundException {
		FtpUtils ftp =new FtpUtils(); 
		FileInputStream inputStream = new FileInputStream( new File("D:/Download/测试1.xlsx"));
		ftp.uploadFile("FTP_TEST/EC/shopee/import/bak", "测试1.xlsx", inputStream);
		//ftp.downloadFile("ftpFile/data", "123.docx", "F://");
		//            ftp.deleteFile("ftpFile/data", "123.docx");
		//System.out.println("ok");
	}

	@Override
	public FTPClient ftp(String ip, String user, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String[]> excel(InputStream in) {
		// TODO Auto-generated method stub
		return null;
	}
}