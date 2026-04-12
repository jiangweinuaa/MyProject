package com.dsc.spos.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bouncycastle.util.encoders.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.commons.codec.digest.DigestUtils;
//以下為 JDK8會用到的
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.utils.EncryptionConstants;

//JDK7 會用到的[不知為什麼, maven 2.2.1 無法package 過]
//import com.sun.org.apache.xml.internal.security.encryption.XMLCipher;
//import com.sun.org.apache.xml.internal.security.utils.EncryptionConstants;

/**
 * 加解密元件.
 * @author Xavier
 *
 */
public final class EncryptUtils 
{

	/**
	 * 因 iOS中AES加密算法采用的填充是PKCS7Padding，而java不支持PKCS7Padding，只支持PKCS5Padding
	 * 為了讓所平台都可以使用, 故調整到 PKCS7Padding 的加解密模式, 採用 bouncycastle套件 來實做.
	 */
	private final String CIPHER_ALGORITHM = "AES/ECB/PKCS7Padding";
	//	private final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";

	public EncryptUtils() 
	{
		/*
		 * AES 設定使用 PaddingMode.PKCS7加密方式.
		 * 細節可參考： http://blog.csdn.net/Firas/article/details/47043335
		 * 
		 * 另外注意, JDK需更換, Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files
		 * 擴充加密長度到 256位, 原生的為 128位.
		 * 
		 * 下載位置： http://www.oracle.com/technetwork/java/javase/downloads/index.html （Additional Resources）
		 * 更換方式：jar文件解压到JRE目录下的lib/security文件夹，覆盖原来的文件
		 */
		Security.addProvider(getInstance()); //PKCS7PADDING
	}

	private static org.bouncycastle.jce.provider.BouncyCastleProvider bouncyCastleProvider = null;

	/**
	 * new BouncyCastleProvider()这个鸟玩意内存泄漏,全是static,不能每次都new
	 * @return
	 */
	public static synchronized org.bouncycastle.jce.provider.BouncyCastleProvider getInstance() 
	{
		if (bouncyCastleProvider == null) 
		{
			bouncyCastleProvider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
		}
		return bouncyCastleProvider;
	}

	/**
	 * 將字串編成 Base64 回傳
	 * @param s
	 * @return
	 */
	public String encodeBASE64(String s) throws Exception { 
		if (s == null) return null; 
		return new String(Base64.encode(s.getBytes()));
		//    	return Base64.encode(s.getBytes());
	}

	/**
	 * 將 Base64字串解碼回傳
	 * @param s
	 * @return
	 */
	public String decodeBASE64String(String s) throws Exception  { 
		if (s == null) {
			return null;
		}
		//		return new String(Base64.decodeBase64(s.getBytes()));
		return new String(Base64.decode(s));
	}

	/**
	 * 密碼字串編碼.
	 * @param password 密碼
	 * @return 演算後的密碼
	 */
	public String encodeMD5(String password) {
		byte[] unencodedPassword = password.getBytes();
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (Exception e) {

			return password;
		}
		md.reset();
		md.update(unencodedPassword);

		byte[] encodedPassword = md.digest();
		StringBuffer buf = new StringBuffer();
		for (byte element : encodedPassword) {
			if ((element & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString(element & 0xff, 16));
		}
		return buf.toString();
	}

	/**
	 * AES-256 PaddingMode.PKCS7 加密方式
	 * @param privateKey
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String encodeAES256(String privateKey, String data) throws Exception {  

		//初始化，設定為加密模式  and 生成加密解密需要的Key
		Cipher cipher=Cipher.getInstance(this.CIPHER_ALGORITHM, "BC");
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(privateKey.getBytes(), "AES"));

		//    	SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		//    	byte[] iv = new byte[16];
		//        random.nextBytes(iv);    	
		//    	IvParameterSpec ivSpec = new IvParameterSpec(iv);
		//        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(privateKey.getBytes(), "AES"), ivSpec);


		//執行加密
		return new String(Base64.encode(cipher.doFinal(data.getBytes())), "UTF-8");
	}

	/**
	 * AES-256 PaddingMode.PKCS7 解密方式
	 * @param privateKey
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String decodeAES256(String privateKey, String encryptData) throws Exception {

		// 初始化，設定為加密模式 and 生成加密解密需要的Key
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(privateKey.getBytes(),"AES"));

		//    	SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		//    	byte[] iv = new byte[16];
		//        random.nextBytes(iv);
		//		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		//		ivSpec = new IvParameterSpec(new byte[cipher.getBlockSize()]);
		//        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(privateKey.getBytes(), "AES"), ivSpec);

		// 執行解密
		byte[] originalBase64 =Base64.decode(encryptData);
		return new String(cipher.doFinal(originalBase64), "UTF-8");
	}

	static {    
		/*
		 * 使用 xml 加密, 需要執行下列的初始化.
		 * 
		 * Here is another very simple workaround:
		 * If you add the following line to your code:
		 * com.sun.org.apache.xml.internal.security.Init.init();
		 * before invoking any of the XML Signature APIs, then the exception does not occur. 
		 * You can also invoke this method in a static initializer, ex:
		 * static {
		 *   com.sun.org.apache.xml.internal.security.Init.init();
		 * }
		 */

		//JDK7 會用到的[不知為什麼, maven 2.2.1 無法package 過]
		//        com.sun.org.apache.xml.internal.security.Init.init();

		//JDK8 會用到的
		org.apache.xml.security.Init.init();
	}

	/**
	 * 執行加密 <br/>
	 * 因為 generno 走的是 XML Encryption Syntax and Processing, 固採用xml 的處理方式.<br/>
	 * 參考：<br/>
	 *     http://javadoc.iaik.tugraz.at/xsect/2.0/apidocs/106/constant-values.html<br/> 
	 *     http://www.gsp.gov.tw/pdf/SOA-1-2_0525.pdf<br/>
	 *     http://santuario.apache.org/Java/api/constant-values.html<br/>
	 *     http://www.w3schools.com/xml/met_element_getelementsbytagnamens.asp<br/>
	 *     https://www.w3.org/TR/2002/REC-xmlenc-core-20021210/Overview.html<br/>
	 *     http://bugs.java.com/view_bug.do?bug_id=6982772<br/>
	 *     
	 * @param privateKey
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String xmlEncodeAES256(String privateKey, String data) throws Exception {
		Document doc = this.builderEncryptDocument(privateKey, data);
		Element rootElement = doc.getDocumentElement();
		return rootElement.getTextContent();
	}

	/**
	 * 執行解密 <br/>
	 * 因為 generno 走的是 XML Encryption Syntax and Processing, 固採用xml 的處理方式.<br/>
	 * 參考：<br/>
	 *     http://javadoc.iaik.tugraz.at/xsect/2.0/apidocs/106/constant-values.html<br/> 
	 *     http://www.gsp.gov.tw/pdf/SOA-1-2_0525.pdf<br/>
	 *     http://santuario.apache.org/Java/api/constant-values.html<br/>
	 *     http://www.w3schools.com/xml/met_element_getelementsbytagnamens.asp<br/>
	 *     https://www.w3.org/TR/2002/REC-xmlenc-core-20021210/Overview.html<br/>
	 *     http://bugs.java.com/view_bug.do?bug_id=6982772<br/>
	 * @param privateKey
	 * @param encryptData
	 * @return
	 * @throws Exception
	 */
	public String xmlDecodeAES256(String privateKey, String encryptData) throws Exception {
		Document doc = this.builderEncryptDocument(privateKey, "fake_value"); //產生一組假的
		Element rootElement = doc.getDocumentElement();

		//調整密文
		Element e = (Element) rootElement.getElementsByTagNameNS(EncryptionConstants.EncryptionSpecNS, "CipherValue").item(0);
		e.setTextContent(encryptData);

		XMLCipher xmlCipher = XMLCipher.getInstance(XMLCipher.AES_256);
		xmlCipher.init(XMLCipher.DECRYPT_MODE, new SecretKeySpec(privateKey.getBytes(), "AES"));
		xmlCipher.doFinal(doc, rootElement, true);

		return rootElement.getTextContent();
	}

	/**
	 * 產生一組己經加密過的 xml document.
	 * @param privateKey
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private Document builderEncryptDocument(String privateKey, String data) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();  
		Document doc = docBuilder.newDocument();

		Element rootElement = doc.createElement("company");
		rootElement.appendChild(doc.createTextNode(data)); 
		doc.appendChild(rootElement);

		//加密
		//        //System.out.println("加密前：" + rootElement.getTextContent());
		XMLCipher xmlCipher = XMLCipher.getInstance(XMLCipher.AES_256);
		xmlCipher.init(XMLCipher.ENCRYPT_MODE, new SecretKeySpec(privateKey.getBytes(), "AES"));
		xmlCipher.doFinal(doc, rootElement, true);
		//        //System.out.println("加密後：" + rootElement.getTextContent());
		return doc;
	}


	/**
	 * SHA哈希并十六进制转换
	 * @param str
	 * @param encryptType (SHA-256/SHA-512)
	 * @return
	 */
	public String SHA(String str,String encryptType)
	{

		if(str==null || str.length()==0)
		{			
			return "";
		}

		// SHA 加密开始
		// 创建加密对象 并傳入加密類型
		MessageDigest messageDigest;
		try 
		{
			messageDigest = MessageDigest.getInstance(encryptType);

			// 传入要加密的字符串
			messageDigest.update(str.getBytes());
			// 得到 byte 類型结果
			byte byteBuffer[] = messageDigest.digest();		

			// 得到返回結果		
			return byteArrayToHexString(byteBuffer);
		} 
		catch (NoSuchAlgorithmException e) 
		{


			return "";
		}

	}


	/**
	 * 虾皮有用到
	 * @param message 
	 * @param secret 秘钥KEY
	 * @return
	 */
	public String HMAC_SHA256(String message, String secret)
	{

		String hash = "";
		try 
		{
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			byte[] bytes = sha256_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));
			hash = byteArrayToHexString(bytes);
		} 
		catch (Exception e) 
		{
			//System.out.println("Error HmacSHA256 ===========" + e.getMessage());
		}
		return hash;

	}


	/**
	 * 户户送Deliveroo外卖有用到
	 * @param message 
	 * @param secret 秘钥KEY
	 * @return
	 */
	public String HMAC_SHA256_Deliveroo(String message, String secret)
	{

		String hash = "";
		try 
		{
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			byte[] bytes = sha256_HMAC.doFinal(message.getBytes(StandardCharsets.US_ASCII));
			hash = byteArrayToHexString(bytes);
		} 
		catch (Exception e) 
		{
			//System.out.println("Error HmacSHA256 ===========" + e.getMessage());
		}
		return hash;

	}



	/**
	 * HTC新竹物流货运状态
	 * @param dataBytes
	 * @param key
	 * @param iv
	 * @return
	 */
	public String Htc_DES_CBC(byte[] dataBytes, String key, String iv)
	{
		try
		{
			/**
			 *
			 * AES和DES 一共有4种工作模式:
			 *     1.电子密码本模式(ECB) -- 缺点是相同的明文加密成相同的密文，明文的规律带到密文。
			 *     2.加密分组链接模式(CBC)
			 *     3.加密反馈模式(CFB)
			 *     4.输出反馈模式(OFB)四种模式
			 *
			 * PKCS5Padding: 填充方式
			 *
			 * 加密方式/工作模式/填充方式
			 * DES/CBC/PKCS5Padding
			 */
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

			//两个参数，第一个为私钥字节数组， 第二个为加密方式 AES或者DES
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "DES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

			/**
			 * 初始化，此方法可以采用三种方式，按服务器要求来添加。
			 * (1)无第三个参数 --> iv
			 * (2)第三个参数为SecureRandom random = new SecureRandom();中random对象，随机数。(AES不可采用这种方法)
			 * (3)采用此代码中的IVParameterSpec --> 指定好了的
			 *
			 * 解密使用 DECRYPT_MODE 方式
			 */
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(dataBytes);

			return new String(Base64.encode(encrypted), "UTF-8");

			//return byteArrayToHexString(encrypted);

		}
		catch (Exception e)
		{
			return "";
		}

	}


	/**
	 * HTC新竹物流货运状态===解密
	 * @param data
	 * @param key
	 * @return
	 */
	public String Htc_desDecrypt(byte[] encrypted1, String key, String iv) 
	{
		try
		{
			//先用Base64解码


			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "DES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

			// 解密使用 DECRYPT_MODE 方式
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

			byte[] original = cipher.doFinal(encrypted1);

			String originalString = new String(original);
			return originalString;
		}
		catch (Exception e) 
		{

			return null;
		}
	}




	private String byteArrayToHexString(byte[] b) 
	{
		StringBuilder hs = new StringBuilder();
		String stmp;
		for (int n = 0; b!=null && n < b.length; n++) 
		{
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs.append('0');
			hs.append(stmp);
		}
		return hs.toString().toLowerCase();
	}


	/**
	 * 91APP用到
	 * @param message
	 * @param secret
	 * @return
	 */
	public String HMAC_SHA512(String message, String secret)
	{		
		String hash = "";
		try 
		{
			Mac sha256_HMAC = Mac.getInstance("HmacSHA512");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA512");
			sha256_HMAC.init(secret_key);

			byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
			hash = byteArrayToHexString(bytes);
		} 
		catch (Exception e) 
		{
			//System.out.println("Error HmacSHA512 ===========" + e.getMessage());
		}
		return hash;
	}



	public static void main(String[] args) throws Exception {

		//解密密文
		//System.out.println("------------解密密文-----------------");
		EncryptUtils eu = new EncryptUtils();
		/*
		String privateKey = "###DSMT100alex******************";
		String encryptData = "PIVxG4tULrADYQEYyuiGQVy5EqDPA5nACu1OUqChJwyhmjkGgAomvc30Upnx9Zk877x6o/YFcfvOq2aDr0iQBXxJA9x/w/A0VLbT392efMA=";
		//System.out.println("解密：" + eu.xmlDecodeAES256(privateKey, encryptData));

		//加解密
		//System.out.println("------------加解密-----------------");
		String data = "SEKAI OWARI-dragon night";
		//System.out.println("明文：" + data);
		String encryptData2 = eu.xmlEncodeAES256(privateKey, data);
		//System.out.println("暗文：" + encryptData2);
		//System.out.println("還原：" + eu.xmlDecodeAES256(privateKey, encryptData2));
		 */


		//获取微信二维码
		String data1 = "{\"pay_type\":\"#P1\",\"shop_code\":\"100000\",\"pos_code\":\"888888\",\"order_id\":\"10000088888820180102162606989\",\"order_name\":\"加盟商080-麦都\",\"order_des\":\"加盟商信用充值\",\"pay_amt\":0.01,\"pay_nodiscountamt\":0,\"ip\":\"192.168.64.1\",\"operation_id\":\"system\",\"notify_url\":\"http://www.aaa.com/\"}digiwinkey";   

		//
		String encryptData1 = eu.encodeMD5(data1);
		//System.out.println("MD5-1:"+ encryptData1);


		//查询支付结果
		data1 = "{\"pay_type\":\"#P1\",\"order_id\":\"10000088888820180102162606989\",\"shop_code\":\"100000\",\"pos_code\":\"888888\"}digiwinkey";   

		//
		encryptData1 = eu.encodeMD5(data1);
		//System.out.println("MD5-2:"+ encryptData1);

		eu=null;
	}
}
