package com.dsc.spos.utils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.log4j.chainsaw.Main;
import org.bouncycastle.util.encoders.Base64;


public class RSAUtil {
	
	//目前用的签名验证，用作客户端
	static String publicKeyStr1="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtm1W2TsziIiDq2AHj9mPgeK7u8zxB3t9ySfNnD6QcqFsSH/fG7FGW4a2MZivOq+cYpMFw/0ZbrmhhpXMb/nRpp+QNdDyMZi+Up05kORsHlGjxK8dIR06l2GL3biILycVZeB2wSIqDDU9GHbJSDwHIYbyJZLlgbh3oWv7Hx1YMYDEjP+TTih3cYlCZWCfzISKINmld3b98WgQ9NeEIjDbZDMzHrYHxhySBpAloq/RpSjDX9z95DFGnWmNWqlWKgTohZVPtgMhvU45muw5dZdt0b8etvQ6jBktJ4z/4lcfv6K5vXbcwX7l5p8Amf5+xYWLi14iDRfsy8wzGFln+zjq9QIDAQAB";
	
	static String privateKeyStr1="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC2bVbZOzOIiIOrYAeP2Y+B4ru7zPEHe33JJ82cPpByoWxIf98bsUZbhrYxmK86r5xikwXD/RluuaGGlcxv+dGmn5A10PIxmL5SnTmQ5GweUaPErx0hHTqXYYvduIgvJxVl4HbBIioMNT0YdslIPAchhvIlkuWBuHeha/sfHVgxgMSM/5NOKHdxiUJlYJ/MhIog2aV3dv3xaBD014QiMNtkMzMetgfGHJIGkCWir9GlKMNf3P3kMUadaY1aqVYqBOiFlU+2AyG9Tjma7Dl1l23Rvx629DqMGS0njP/iVx+/orm9dtzBfuXmnwCZ/n7FhYuLXiINF+zLzDMYWWf7OOr1AgMBAAECggEAHMyCC2UnK4SxlJpiVzk3SH/stycRXecG4nygHuxZjV8iYwecxxhjTQulM4sxDoiunXksTdZk72/qqf53+a1shWtnLre/pEjCsvQTJGaLEvY8bk5ewN2GZGOH2wbIqPj4jc6zsKWeEbNN49DM0dR4I7jT/U6FYMcoS5lqWW0egSHYqSQDvQcSO+99G7mXqquTQHt8qF1xw1/qwapUHnTWGYwT+dgXnjprxd5RJDbK4sUIaiELfgF1T2B94StXZECvrydaLgrbLkmJvZ1el88gatkQSC6YqmZrac4pPfbMsSuEMzaKUNw1jIxMU51rMmPSkvOCaXk3cwL8mhu3j1LTgQKBgQDag0OP0HSIY7kmb6nPD2OkaKoP0AlQWoQAxrTOoNIhnkl//bHDsfYnPavzAVfl99s/03E72Vd7jU0t7J7155+uIKHW3VJ36w/HthOCjSk9B1Xk0rH4jStPOTXbSTsgjFa7l+FNhdkLKv3NwXRMJE58RZ9MOGoSHwemKgzHZ8vrhQKBgQDVuUAscOo9d5M2pzWRiVVn3uqetsRgnaENt1aT1ZMR5rgGnd9chIklJfm/2uY4WCLs1GOzauIGbXMX+luFofRMdhkAD9fgehan9JsMMhM5/vrXLqqn6nbBLE8/nYNC8yfVM9GANBzcvkGdvdKgsfdYWL8l4KoR/xrEhyi1FmkEsQKBgHtIGaMW+dhFYx2DXGu9A/urLV90gPy7xMPnV/VIGKXs9wzmJhgB3J3xFoUomd3eQ+2zrqwUiF3lr8icOmcEgjusn4JpJfEQRrvnRwSd/ck3zUcndE+F3gx5e6A3pctMQlbebrfdL0I0xDME1UdsKHfGlARdw8JrjVY7XTBXqF49AoGBAJbDuqBe+u5nZqLcK27B+UxvtQRHG5qoN49scJeSxs+O+Ow5+/HQ0Moy0s5jRZojmmeEn9xWHqG4HcOhaWSITcSOXq5rfrasiUTryWA+BtAXra2UDvCq9iDo9/KBU7Irw0Kj7jrJCD8zuyfiWy3evQRGfnji1jYhxikyyyWLbIPRAoGBAI04vo6FUps2n9mjALZfsn4XI6e28W0zYi698ByUwUkTG9QS5dDl4+Efl+b3cPkXXP8dGLXeA9YtXy7qLf0h2sam+roZj7EdKkT2UUk3cl3fYPOWafb9+t2vAvz6nE/9ZJ8cRS/NHcIlIOjTwaHVcBAfi8+trlzDrHEsoG2X+a4H";
	
	
	//用作公司服务器端
  static String publicKeyStr2="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiLnA/kOnxonSbdntUZWiCrFot1k+abJY8wouGyo7MYRq/9W14F00ew811FxubgA98YLHYwdeCiJ3BDNsUEO2xAsuwoc2vozCDvhv7SqWbKz19Ps1RBjZ/Ne/qtoz9jjKFi/LNUQtkE7JdXhZQeLlIgxeEDZv8uS4GCH7E5X7LWBqQ0Mbdr7E4Hkd5IigO79sALBhGPZUqQM2Y1f/j1xbldHvcGr4/iSjhIDysiYfY2OeclaKWa8hcK4jtYNzf83PMJsTosbCv1S0VOUTWIEBhxRAnL7pOjQK9d31gitvJgf7N/fseIgcd5JGRk3+54A9S5MgXm+zwWy9+9ha4aMKrwIDAQAB";
	
	static String privateKeyStr2="MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCIucD+Q6fGidJt2e1RlaIKsWi3WT5psljzCi4bKjsxhGr/1bXgXTR7DzXUXG5uAD3xgsdjB14KIncEM2xQQ7bECy7Chza+jMIO+G/tKpZsrPX0+zVEGNn817+q2jP2OMoWL8s1RC2QTsl1eFlB4uUiDF4QNm/y5LgYIfsTlfstYGpDQxt2vsTgeR3kiKA7v2wAsGEY9lSpAzZjV/+PXFuV0e9wavj+JKOEgPKyJh9jY55yVopZryFwriO1g3N/zc8wmxOixsK/VLRU5RNYgQGHFECcvuk6NAr13fWCK28mB/s39+x4iBx3kkZGTf7ngD1LkyBeb7PBbL372FrhowqvAgMBAAECggEBAIK8a8g1VRZI7wfPXFeopttmrGS1VzKmMNuknlWnWhaNMesTpOe299m7v5MiFw3j+VcX4CPXBZrXPcqsiSNuvDJVJw8nT7eAzIaYFB5JnLCp6j2of1RwFvDoNAPgxdLoloQEi2CRoqfcTlKfE2om1UZrYP5Lr1woS8f3piciQJ0vq6nPCJIa9URCReWYI2zq6u/f3F0VN8YGzYH7eqAVLwbZCWT8/zHw2mu5QqBg3sG8cD2rQFTDke8Skz27QP6Emu51fcmZ8OX6ubJfyI8GFn6sWtFutuRYzN/Kol1p1uOmxT4RQHYW2TrAkPq9uuGGnOsFFHGvc8Sk31hokb/w7lECgYEA3AwmnXTozs+sN8xGCR6O0zptdjGvXN5QiRTbtU/xsLZFMCJXOX5GUARSJDhJzegXD2D7mgjVMRJDRCffjeqjZNTG5pyTNmtrmJuAt4mW9QIHExg+F/jaiZZyV3MO0oaFfgzWkV+t8pRfK7QoPro9PeMeUdoEv7tVoUM9Eq/QN2MCgYEAnxCHPmm/YYyS+ppOf5h4Vzy8U8geDkV4f4iKJPXT2wVL25coq+MOQoU91qXV0XmeiBTtf2CxpfnB3owNm/5kgi/nT0k3G73KgPEIrQaiD/box/x0CaifnBZSnZwGKrC51s0pYpVxpM2ZIrYaIDrZ2i+GkB4X54PBSpmslgfuf0UCgYB8NheuFgo8g5nRPWvjRe6VaYvUhiNZJt0LJE0JQgFKIPlZPGlEOPVzwZykeWmf43zw32vm/Ulqk8aIuIFG2MY26q0EDCpFM8nesRitgetGjkh0DOEwIvcpZ1VP69DYIbtE9fZAxWOpZPt77gwxrGtZfdTlm03G9PZXO19EJx4/xQKBgQCPJI+gwgdBIQEv1mcn+izpzLVp7B/oFep/Un+HDVZutlByt5AZRwaRJFjm7mwBy6G3sqWbyId15tHplT22EPOgGL3fIvMd2uj+qmp1Uw/KsBcByZUfEAk6rPgB7+O4zTsgK5a/qd8QQRk7HIdcU7my2KwOy5YVeQMcQDkI9vBQxQKBgQCi/u02P1eATgOXrdQcJtxMtyBkdHc3vPvBg0T/bcv/RgvsWESIGItvfSuU1ZMDXlglzPftbqmKIQ2gv8EE+hS3Ns97Jcc0qfNPsAt0lOenOJjkqa/NFTX3atj+orVJs0H2kWHEhrC6jFe9FsYcskn9oTCy3XA91374QG01wshHXg==";
	
	//生成秘钥对
	public static KeyPair getKeyPair() throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		return keyPair;
	}
	
	//获取公钥(Base64编码)
	public static String getPublicKey(KeyPair keyPair){
		PublicKey publicKey = keyPair.getPublic();
		byte[] bytes = publicKey.getEncoded();
		return byte2Base64(bytes);
	}
	
	//获取私钥(Base64编码)
	public static String getPrivateKey(KeyPair keyPair){
		PrivateKey privateKey = keyPair.getPrivate();
		byte[] bytes = privateKey.getEncoded();
		return byte2Base64(bytes);
	}
	
	//将Base64编码后的公钥转换成PublicKey对象
	public static PublicKey string2PublicKey(String pubStr) throws Exception{
		byte[] keyBytes = base642Byte(pubStr);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}
	
	//将Base64编码后的私钥转换成PrivateKey对象
	public static PrivateKey string2PrivateKey(String priStr) throws Exception{
		byte[] keyBytes = base642Byte(priStr);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}
	
	//公钥加密
	public static byte[] publicEncrypt(byte[] content, PublicKey publicKey) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] bytes = cipher.doFinal(content);
		return bytes;
	}
	
	//私钥解密
	public static byte[] privateDecrypt(byte[] content, PrivateKey privateKey) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] bytes = cipher.doFinal(content);
		return bytes;
	}
	
	
  //私钥加密PrivateKey privateKey
	public static byte[] privateEncrypt(byte[] content, PrivateKey privateKey) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		byte[] bytes = cipher.doFinal(content);
		return bytes;
	}
	
	//公钥解密PublicKey publicKey
	public static byte[] publicDecrypt(byte[] content, PublicKey publicKey) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		byte[] bytes = cipher.doFinal(content);
		return bytes;
	}
	
	
	
//字节数组转Base64编码
	public static String byte2Base64(byte[] bytes){
		return new String(Base64.encode(bytes));
	}
	
	//Base64编码转字节数组
	public static byte[] base642Byte(String base64Key) {
		return Base64.decode(base64Key);
		
	}

////字节数组转Base64编码
//	public static String byte2Base64(byte[] bytes){
//		BASE64Encoder encoder = new BASE64Encoder();
//		return encoder.encode(bytes);
//	}
//	
//	//Base64编码转字节数组
//	public static byte[] base642Byte(String base64Key) throws IOException{
//		BASE64Decoder decoder = new BASE64Decoder();
//		return decoder.decodeBuffer(base64Key);
//	}

	//RSA公钥加密，用作客户端注册服务
	public static String RSAEncrypt(String msg) throws Exception
	{
	    //将Base64编码后的公钥转换成PublicKey对象
			PublicKey publicKey = RSAUtil.string2PublicKey(publicKeyStr1);
			//用公钥加密
			byte[] publicEncrypt = RSAUtil.publicEncrypt(msg.getBytes(), publicKey);
			//加密后的内容Base64编码
			String byte2Base64 = RSAUtil.byte2Base64(publicEncrypt);
			return byte2Base64;
	}
	
	
  //RSA公钥解密，用作客户端注册服务
	public static String RSADecrypt(String msg) throws Exception
	{
	    //将Base64编码后的私钥转换成PrivateKey对象
			PrivateKey privateKey = RSAUtil.string2PrivateKey(privateKeyStr1);
			//加密后的内容Base64解码
			byte[] base642Byte = RSAUtil.base642Byte(msg);
			//用私钥解密
			byte[] privateDecrypt = RSAUtil.privateDecrypt(base642Byte, privateKey);
			//解密后的明文
			String outstr=new String(privateDecrypt);
			return outstr;
			
	}
	
	
	
	//公司服务器用私钥加密，公钥解密
  //RSA公钥加密，用作公司册服务
	public static String RSAEncrypt2(String msg) throws Exception
	{
	  //将Base64编码后的私钥转换成PrivateKey对象
		PrivateKey privateKey = RSAUtil.string2PrivateKey(privateKeyStr2);
		
		byte[] publicEncrypt1 = RSAUtil.privateEncrypt(msg.getBytes(), privateKey);
		//加密后的内容Base64编码
	  String byte2Base641 = RSAUtil.byte2Base64(publicEncrypt1);
	  return byte2Base641;
	  
	  
	}
	
	
  //RSA公钥解密，用作公司注册服务
	public static String RSADecrypt2(String msg) throws Exception
	{
	    //用公钥解密
			byte[] base642Byte1 = RSAUtil.base642Byte(msg);
		  //将Base64编码后的公钥转换成PublicKey对象
			PublicKey publicKey = RSAUtil.string2PublicKey(publicKeyStr2);
			
			byte[] privateDecrypt1 = RSAUtil.publicDecrypt(base642Byte1, publicKey);
			String outstr1=new String(privateDecrypt1);
			return outstr1;
			
			
	}
	
	
	
	
	public static void main(String[] args) throws Exception
	{
	  //测试加密
	  //===============生成公钥和私钥，公钥传给客户端，私钥服务端保留==================
		//生成RSA公钥和私钥，并Base64编码
		KeyPair keyPair = getKeyPair();
//		String publicKeyStr = RSAUtil.getPublicKey(keyPair);
//		String privateKeyStr = RSAUtil.getPrivateKey(keyPair);
		String publicKeyStr = publicKeyStr1;
		String privateKeyStr = privateKeyStr1;
		
		//System.out.println("RSA公钥Base64编码:" + publicKeyStr);
		//System.out.println("RSA私钥Base64编码:" + privateKeyStr);
		
		//=================客户端=================
		//hello, i am infi, good night!加密
		String message = "hello, i am infi, good night!";
		//将Base64编码后的公钥转换成PublicKey对象
		PublicKey publicKey = RSAUtil.string2PublicKey(publicKeyStr);
		//用公钥加密
		byte[] publicEncrypt = RSAUtil.publicEncrypt(message.getBytes(), publicKey);
		//加密后的内容Base64编码
		String byte2Base64 = RSAUtil.byte2Base64(publicEncrypt);
		//System.out.println("公钥加密并Base64编码的结果：" + byte2Base64);
		
		
		//##############	网络上传输的内容有Base64编码后的公钥 和 Base64编码后的公钥加密的内容     #################
		
		
		
		//===================服务端================
		//将Base64编码后的私钥转换成PrivateKey对象
		PrivateKey privateKey = RSAUtil.string2PrivateKey(privateKeyStr);
		//加密后的内容Base64解码
		byte[] base642Byte = RSAUtil.base642Byte(byte2Base64);
		//用私钥解密
		byte[] privateDecrypt = RSAUtil.privateDecrypt(base642Byte, privateKey);
		//解密后的明文
		//System.out.println("解密后的明文: " + new String(privateDecrypt));
		String outstr=new String(privateDecrypt);
		
		//测试私钥匙加密，公钥解密
	  //用私钥加密
		byte[] publicEncrypt1 = RSAUtil.privateEncrypt(message.getBytes(), privateKey);
			//加密后的内容Base64编码
		String byte2Base641 = RSAUtil.byte2Base64(publicEncrypt1);
	  //用公钥解密
		byte[] base642Byte1 = RSAUtil.base642Byte(byte2Base641);
		byte[] privateDecrypt1 = RSAUtil.publicDecrypt(base642Byte1, publicKey);
		String outstr1=new String(privateDecrypt1);

	}	
	
	
}


