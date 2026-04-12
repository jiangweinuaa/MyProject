package com.dsc.spos.waimai.isv;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ISV_HelpTools {
    // 写日志
    public static void writelog_waimaiException(String log) throws IOException {
        // 生成文件路径
        String sdFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());// 当天日期
        String path = System.getProperty("user.dir") + "\\log\\waimaiExceptionlog" + sdFormat + ".txt";
        File file = new File(path);

        String dirpath = System.getProperty("user.dir") + "\\log";
        File dirfile = new File(dirpath);
        if (!dirfile.exists()) {
            dirfile.mkdir();
        }
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream writerStream = new FileOutputStream(file,true);
        OutputStreamWriter osw=new OutputStreamWriter(writerStream, "UTF-8");
        BufferedWriter writer = new BufferedWriter(osw);

        // 前面加上时间
        String stFormat = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());// 当天日期
        String slog = stFormat + " " + log + "\r\n";

        stFormat=null;
        sdFormat=null;

        writer.append(slog);
        writer.close();
        writer=null;

        osw.close();
        osw=null;

        writerStream.close();
        writerStream=null;

        file=null;

        sdFormat=null;

    }

    // 写日志
    public static void writelog_fileName(String log, String fileName) throws IOException {
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

        FileOutputStream writerStream = new FileOutputStream(file,true);
        OutputStreamWriter osw=new OutputStreamWriter(writerStream, "UTF-8");
        BufferedWriter writer = new BufferedWriter(osw);

        // 前面加上时间
        String stFormat = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());// 当天日期
        String slog = stFormat + " " + log + "\r\n";
        stFormat=null;

        stFormat=null;
        sdFormat=null;

        writer.append(slog);
        writer.close();
        writer=null;

        osw.close();
        osw=null;

        writerStream.close();
        writerStream=null;

        file=null;

        sdFormat=null;
    }

    /**
     * url解码
     * @param str
     * @return
     */
    public static String getURLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {

        }
        return result;
    }

    /**
     * url转码
     * @param str
     * @return
     */
    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {

        }
        return result;
    }

    /**
     * 根据ePoiId(针对该门店自行定义的id)获取客户唯一标识
     * @param ePoiId
     * @return
     */
    public static String getJBPClientNo(String ePoiId) {
        String result = "";
        if (null == ePoiId) {
            return "";
        }
        try {
            int indexofSpec = ePoiId.indexOf("_");//客户标识_企业ID_门店编码
            result = ePoiId.substring(0, indexofSpec);
        } catch (Exception e) {

        }
        return result;

    }

    /**
     * 判断url链接是否可以正常访问
     * @param urlStr
     * @return
     * @throws Exception
     */
    public static boolean doGetUrlConnect (String urlStr) throws  Exception
    {
        boolean nRet = false;
        try
        {
            if (urlStr == null || urlStr.length() <= 0) {
                return false;
            }
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            int state = con.getResponseCode();
            if (state==200)
            {
                return true;
            }

        }
        catch (Exception e)
        {

        }
        return nRet;
    }


    /**
     * 生成客户唯一标识(长度8位）
     * @return
     */
    public static String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();

    }

    private static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };

}
