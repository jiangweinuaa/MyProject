package com.dsc.spos.utils;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;

/**
 * DES加解密工具类
 *
 * Created by fangxm on 17-1-9.
 */
@SuppressWarnings("Duplicates")
public class DesUtil {


    /**
     * 加密算法,可用 DES,DESede,Blowfish.
     */
    private static final String ALGORITHM = "DES";
    /**
     * 加密算法,可用 DES,DESede,Blowfish.
     */
    private static final String ALGORITHM_CBC = "DES/CBC/PKCS5Padding";


    /**
     * 用指定的key对数据进行DES加密.
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);
        return cipher.doFinal(data);
    }
    /**
     * 用指定的key对数据进行DES解密.
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
        return cipher.doFinal(data);
    }
    /**
     * 对数据进行DES解密.
     */
    public static String decrypt_CBC(String data,String key,String iv) throws Exception {
        return new String(decrypt(ALGORITHM_CBC, StringUtil.hex2byte(data.getBytes()), key.getBytes(), iv.getBytes()));
    }

    /**
     * 对用DES加密过的数据进行加密.
     */
    public static String encrypt_CBC(String data,String key,String iv) throws Exception {
        return StringUtil.byte2hex(encrypt(ALGORITHM_CBC, data.getBytes(), key.getBytes(), iv.getBytes()));
    }
    /**
     * 用指定的key对数据进行DES加密.
     */
    private static byte[] encrypt(String algorithm, byte[] data, byte[] key, byte[] iv) throws Exception {
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secretKey = keyFactory.generateSecret(dks);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        return cipher.doFinal(data);
    }

    /**
     * 用指定的key对数据进行DES解密.
     */
    private static byte[] decrypt(String algorithm, byte[] data, byte[] key, byte[] iv) throws Exception {
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secretKey = keyFactory.generateSecret(dks);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        return cipher.doFinal(data);
    }

    public static void main(String[] args)  {
//        try {
//            String appsecret = "THPNWDakyA14Gm26";
//            String str = "{\"bhtaxtotal\":\"39.81\",\"checker\":\"测试复核人\",\"clerk\":\"测试开票人\",\"cpybz\":\"0\",\"detail\":[{\"fphxz\":\"0\",\"goodsname\":\"餐饮服务\",\"hsbz\":\"1\",\"lslbs\":\"\",\"num\":\"1\",\"price\":\"42.2\",\"spbm\":\"3070401\",\"tax\":\"2.39\",\"taxamt\":\"42.2\",\"taxfreeamt\":\"39.81\",\"taxrate\":\"0.06\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"}],\"invoicebillno\":\"01311202202251449577\",\"invoicedate\":\"2022-02-25 15:14:34\",\"kptype\":\"1\",\"message\":\"\",\"orderno\":\"01311202202251449577\",\"ordertotal\":\"42.2\",\"payee\":\"测试收款人\",\"platformtype\":\"#NUONUO_V1\",\"qdbz\":\"0\",\"qdxmmc\":\"餐饮服务\",\"saleaccount\":\"1234566781212312312\",\"saleaddress\":\"测试销方地址\",\"salephone\":\"12345667812\",\"saletaxnum\":\"339901999999821\",\"self_flag\":\"1\",\"taxtotal\":\"2.39\"}";
//            String s = encrypt_CBC(str, appsecret.substring(0, 8), appsecret.substring(8, 16));
//            System.out.println(s);
//
//            String json = "2946da76dfcc9f657b4d5e6f6572a05cc769d289ac8a78c9bd41e65f2496cefcd8e19cc26f42c1b1d97c5ca46c2f89e975cda7d68beb9f7c390e78f9a7f8fe30b6876a2b5fbfbd98b11269320a709c69042a917d47abdecbdd0c20df065bff596c87733f6663c535726213386adce9e74280d006beb3757b62532bbf98cd3ef934755d373c40542f529e1838d4ffa6c5e5bd7b72cc9f264bc7745f4ef79b03281c71c45ecaa3e7df239c0198e59e4698b9a9ba4a93d292a449d7d8502f67a42b111ea565a00b2def5f4431f39d141dc512e49222b3a279544fd1b2c25e661542734bf7b703eaca4fdf772040eb4b00563f4b82f04920e5ec05e6714b7f630d5074eea5f4544e56ca068ac525c0d7e69fb7e9234255d4a019136b3551c6152a205c17b8abc813c3885f052f33cf3e4ba40abc6ccd3ee75a3c480a2e3867edfcff5ed742e2876b48886471d67ee4b7e6edc0dd0f5618d23a9dbeac495d8dad0b3cddcffec0b59425126c92a1dc8f77110c28700388e4dfa8b44e20f1701259f65a04d409496125ac8ca6de7aae8ab62bbb8789c7c6569db1df2632e7100f7e792428151635c6bde44f4d7fc6013cda8a3a575bd67ab908d254ebc180d9d239b02d2716770be46a6d6f2a9804e2c40d63fc83dd3f65b261026ac25454e1cef9795a3c431709537f2eb8574978bd0053abca917052090b7f86705240547deae3aa80be1cd22426ad9a92b4829d3f23f76a725f6bb2573766b39bc6a45e9b535eb7157ddc61933c7bab6eced7c8ebb6813c4c69b1f62e3139ab6ef86289e52fbdb651a34e050f7c05aeaddd11254899abdb88d872fbec4ffee03403f6f4a605185487e9d9f72697cf7d5e6190c0321687532854b40ed3496db627d3969d25b9ecb299ee33b530e53aea9d83a5165ee670bf4025791701e7a8b9916024926c3cde5a51b5d39fc5651837488ec6d3501f2d06522ead1f33e16fc808a3848b0057cdd846";
//            String f = decrypt_CBC(json, appsecret.substring(0, 8), appsecret.substring(8, 16));
//            System.out.println(f);
//        }catch (Exception e){
//            System.out.println(e.toString());
//        }

        String str = "operater=queryInfoByOrderId&orderno=20220301100747777";
//        String str.substring(0, str.indexOf("?"));;

    }
}
