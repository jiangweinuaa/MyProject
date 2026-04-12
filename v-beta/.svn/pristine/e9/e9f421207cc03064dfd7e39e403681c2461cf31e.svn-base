package com.dsc.spos.utils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;


/**
 * ZIP文件压缩/解压
 */
public class ZipUtils
{
    static Logger logger = LogManager.getLogger(ZipUtils.class.getName());


    /**
     * 压缩文件，单个文件压缩并删除
     * @param path 目录D:\apache\apache-tomcat-8.0.35\bin\SposWebLog\info_log
     */
    public static Boolean zipFile_NRC( String path)
    {
        boolean b_ok=false;
        try
        {
            // 要打包的文件夹
            File currentFile = new File(path);
            File[] listFiles = currentFile.listFiles(pathname -> pathname.isDirectory() || pathname.getName().endsWith("txt")||pathname.getName().endsWith("log"));

            if (listFiles != null)
            {
                // 遍历test文件夹下所有的文件、文件夹
                for (File file : listFiles)
                {
                    if (file.isDirectory())
                    {
                        zipFile_NRC(file.getPath());
                    }
                    else
                    {
                        String[] split = file.getName().split("\\.");
                        String ext = split[split.length - 1];

                        String v_filename=file.getName().substring(0,file.getName().length()-ext.length()-1);

                        //1、DCP+CRM里面正在写的不要
                        if (file.getName().toLowerCase().equals("debug.log") || file.getName().toLowerCase().equals("info.log") ||file.getName().toLowerCase().equals("error.log"))
                        {
                            continue;
                        }

                        //解析案例：ETLJOB20220926 / 2022-11-09 / DEBUG-2022-11-01-0

                        //取8位
                        //ETLJOB20220926
                        String v_date=v_filename.substring(v_filename.length()-8,v_filename.length());
                        boolean b_date=isValidDate(v_date);
                        if (!b_date)
                        {
                            //取10位
                            //2022-11-09
                            v_date=v_filename.substring(v_filename.length()-10,v_filename.length());
                            String[] v_split=v_date.split("-");
                            if (v_split.length==3)
                            {
                                v_date=v_split[0]+v_split[1]+v_split[2];
                                b_date=isValidDate(v_date);
                            }
                        }

                        if (!b_date)
                        {
                            //DEBUG-2022-11-01-0
                            String[] v_split=v_filename.split("-");
                            if (v_split.length>4)
                            {
                                v_date=v_split[1]+v_split[2]+v_split[3];
                                b_date=isValidDate(v_date);
                            }
                        }

                        //格式不支持，跳过
                        if (!b_date)
                        {
                            continue;
                        }

                        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
                        //当天的跳过
                        if (sDate.equals(v_date))
                        {
                            continue;
                        }

                        // 生成的压缩文件
                        ZipFile zipFile = new ZipFile(file.getPath().substring(0,file.getPath().length()-ext.length()-1) + ".zip");
                        //
                        ZipParameters parameters = new ZipParameters();
                        // 压缩方式
                        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
                        // 压缩级别
                        parameters.setCompressionLevel(CompressionLevel.NORMAL);

                        zipFile.addFile(file, parameters);

                        //
                        zipFile.close();
                        zipFile=null;

                        //删除文件
                        file.delete();
                    }
                }

                b_ok=true;
            }

        }
        catch (Exception e)
        {
            b_ok=false;
            logger.error("\r\nzipFile压缩文件报错：" + e.getMessage());
        }

        return b_ok;
    }





    /**
     * 压缩文件，在当前目录
     * @param path 目录D:\\TEST
     */
    public static Boolean zipFile( String path)
    {
        boolean b_ok=false;
        try
        {
            // 生成的压缩文件
            ZipFile zipFile = new ZipFile( path + ".zip");
            //
            ZipParameters parameters = new ZipParameters();
            // 压缩方式
            parameters.setCompressionMethod(CompressionMethod.DEFLATE);
            // 压缩级别
            parameters.setCompressionLevel(CompressionLevel.NORMAL);
            // 要打包的文件夹
            File currentFile = new File(path);
            File[] listFiles = currentFile.listFiles();
            if (listFiles != null)
            {
                // 遍历test文件夹下所有的文件、文件夹
                for (File file : listFiles)
                {
                    if (file.isDirectory())
                    {
                        zipFile.addFolder(file, parameters);
                    }
                    else
                    {
                        zipFile.addFile(file, parameters);
                    }
                }

                b_ok=true;
            }

            //
            zipFile.close();
            zipFile=null;
        }
        catch (Exception e)
        {
            b_ok=false;
            logger.error("\r\nzipFile压缩文件报错：" + e.getMessage());
        }

        return b_ok;
    }

    /**
     * 解压
     * @param path
     * @return
     */
    private static boolean unzip(String path)
    {
        boolean b_ok=false;

        try
        {
            ZipFile zipFile = new ZipFile(path + ".zip");
            zipFile.extractAll(path);

            b_ok=true;
        }
        catch (Exception e)
        {
            b_ok=false;
            logger.error("\r\nzipFile压缩文件报错：" + e.getMessage());
        }
        return b_ok;
    }


    /**
     * 判断是否日期格式,20221109只验证次格式
     * @param dateStr
     * @return
     */
    public static boolean isValidDate(String dateStr)
    {
        try
        {
            DateTimeFormatter dateFormatter = DateTimeFormatter.BASIC_ISO_DATE;
            LocalDate.parse(dateStr, dateFormatter);
        }
        catch (DateTimeParseException e)
        {
            return false;
        }
        return true;
    }

    public static void main(String[] args)
    {
//        String path="D:\\apache\\apache-tomcat-8.0.35\\bin\\log";

//        boolean b_ok=zipFile(path);
//        System.out.println("压缩文件：" +b_ok);

//        boolean b_ok2=unzip(path);
//        System.out.println("解压文件：" +b_ok2);


//        String path="D:\\apache\\apache-tomcat-8.0.35\\bin\\SposWebLog\\info_log";
//
//        boolean b_ok=zipFile_NRC(path);
//        System.out.println("压缩文件：" +b_ok);

//        String path="D:\\apache\\apache-tomcat-8.0.35\\bin\\myLog";
//        boolean b_ok=zipFile_NRC(path);
//        System.out.println("压缩文件：" +b_ok);

//        String path="D:\\apache\\apache-tomcat-8.0.35\\bin\\1";
//        boolean b_ok=zipFile_NRC(path);
//        System.out.println("压缩文件：" +b_ok);



        String path="D:\\Digiwin\\Server-DCP\\logs\\SposWebLog\\info_log\\2022-11-09";
        boolean b_ok=zipFile_NRC(path);
        System.out.println("压缩文件：" +b_ok);

    }




}
