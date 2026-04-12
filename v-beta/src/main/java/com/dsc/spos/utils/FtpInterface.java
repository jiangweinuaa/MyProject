package com.dsc.spos.utils;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPClient;

/**
 * ftp 连接
 * @author yuanyy 2019-04-16
 *
 */
public interface FtpInterface {
	public FTPClient ftp(String ip,String user,String password);
	
	public ArrayList<String[]> excel(InputStream in);
}
