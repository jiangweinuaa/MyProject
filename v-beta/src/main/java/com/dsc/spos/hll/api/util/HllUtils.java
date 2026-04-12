package com.dsc.spos.hll.api.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.dsc.spos.hll.api.HllConstants;



/**
 * 工具类
 * 
 * @author LN 08546
 */
public class HllUtils {
	
	/**
	 * 将除signature，requestBody之外的公共参数、接口所需业务参数以及开发者秘钥（devPwd）按照键值的字典顺序拼接排序(忽略大小写)，
	 * 注意：业务参数中键对应的值为空的键值均不参与拼接，键对应的值为基本类型列表的键值对不参与签名，
	 * 键对应的值为复杂类型的键不参与签名，键对应的值为复杂类型列表的取其中第一个参与签名
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String getSignature(Map<String, Object> params) throws Exception {
		String sign1 = HllUtils.getOrderByLexicographic(params);
		return HllUtils.sha1(sign1).toUpperCase();
		
	}

	/**
	 * 按照键值的字典顺序拼接
	 * 
	 * @param params
	 * @return
	 */
	public static String getOrderByLexicographic(Map<String, Object> params) {
		List<String> list = new ArrayList<String>();
		Set<String> se = params.keySet();
		for (String key : se) {
			String value = params.get(key).toString();
			if (value == null) {
				continue;
			} else if ("".equals(value.trim())) {
				continue;
			} else {
				list.add(key + value);
			}
		}

		Collections.sort(list);
		String paramStr = "";
		// 迭代list拼装签名
		for (int i = 0; i < list.size(); i++) {
			if (i != list.size() - 1) {
				paramStr += list.get(i);
			} else {
				paramStr += list.get(i);
			}
		}
		String signStr = "key" + paramStr + "secret";
		return signStr;
	}

	/**
	 * sha1加密
	 * 
	 * @param data
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String sha1(String data) throws Exception {
		// 信息摘要器 算法名称
		MessageDigest md = MessageDigest.getInstance("SHA1");
		// 把字符串转为字节数组
		byte[] b = data.getBytes();
		// 使用指定的字节来更新我们的摘要
		md.update(b);
		// 获取密文 （完成摘要计算）
		byte[] b2 = md.digest();
		// 获取计算的长度
		int len = b2.length;
		// 16进制字符串
		String str = "0123456789abcdef";
		// 把字符串转为字符串数组
		char[] ch = str.toCharArray();

		// 创建一个40位长度的字节数组
		char[] chs = new char[len * 2];
		// 循环20次
		for (int i = 0, k = 0; i < len; i++) {
			byte b3 = b2[i];// 获取摘要计算后的字节数组中的每个字节
			// >>>:无符号右移
			// &:按位与
			// 0xf:0-15的数字
			chs[k++] = ch[b3 >>> 4 & 0xf];
			chs[k++] = ch[b3 & 0xf];
		}

		// 字符数组转为字符串
		return new String(chs);
	}

	/**
	 * 用AES算法加密，秘钥为商户秘钥重复两遍，偏移量为商户秘钥重复两遍，加密模式：CBC,填充模式；NoPadding
	 * 
	 * @param Data
	 * @return
	 * @throws Exception
	 */
	public static String AESEncode(String MER_PWD, String Data) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance(HllConstants.ALGO_MODE);
			int blockSize = cipher.getBlockSize();
			byte[] dataBytes = Data.getBytes("UTF-8");
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
			}
			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
			SecretKeySpec keyspec = new SecretKeySpec((MER_PWD + MER_PWD).getBytes("UTF-8"), HllConstants.ALGO);
			IvParameterSpec ivspec = new IvParameterSpec((MER_PWD + MER_PWD).getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(plaintext);
			// new Base64();
			// String EncStr = Base64.encode(encrypted);//
			// 将cipher加密后的byte数组用base64加密生成字符串
			String EncStr = new String(Base64.encodeBase64(encrypted), "UTF-8");
			return EncStr;
		} catch (Exception e) 
		{
			return null;
		}
	}
	
	public static String getDefaultValueStr(Object obj){
		if(obj==null||obj.toString().isEmpty()){
			return "";
		}else{
			return obj.toString();
		}
	}
	

}
