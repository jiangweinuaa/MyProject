package com.dsc.spos.utils;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.model.*;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.bc.*;
import com.google.common.base.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;
import org.hibernate.validator.constraints.NotBlank;
import org.json.JSONArray;
import org.json.JSONObject;
import software.amazon.ion.Decimal;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PosPub {

    static Logger logger = LogManager.getLogger(PosPub.class.getName());

    //数据库超时
    public static int iTimeoutTime = 30;

    //一次读取返回的数据量
    public static int iFetchSize = 100;
   //秒
    public static int iAuthExpiredTime=300;  

    //记录JSON和时间戳
    public static List<Map<String, Object>> listmapJson = new ArrayList<Map<String, Object>>();

    //订单表是否被分区标记 0:未分区 1:已分区
    public static String tablePartition_dcp_order = "0";

    //销售单表是否被分区标记 0:未分区 1:已分区
    public static String tablePartition_dcp_sale = "0";


    /*
     * 产生GUID
     * isHorizontalLine是否带横线-
     */
    public static String getGUID(boolean isHorizontalLine) {
        UUID uuid = UUID.randomUUID();

        String ret = uuid.toString();
        if (isHorizontalLine == false) {
            ret = ret.replace("-", "");
        }
        return ret;
    }

    /**
     * 將字串編成 Base64 回傳
     *
     * @param s
     * @return
     */
    public static String encodeBASE64(String s) throws Exception {
        if (s == null) return null;
        return new String(Base64.encode(s.getBytes()));
    }

    /**
     * MD5加密
     *
     * @param oldstr
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encodeMD5(String oldstr) throws UnsupportedEncodingException {
        byte[] unencodedPassword = oldstr.getBytes("utf-8");
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {

            return oldstr;
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
     * 判断字符串是否为整数类型0-9,没小数
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        if (str.equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 是否为数字类型（正数）  可以带小数点,不支持负数
     *
     * @param str
     * @return
     */
    public static boolean isNumericType(String str) {
        if (str == null) {
            return false;
        }
        if (str.equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]\\d*\\.?\\d*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 是否为数字类型  可以带小数点,支持负数
     *
     * @param str
     * @return
     */
    public static boolean isNumericTypeMinus(String str) {
        if (str == null) {
            return false;
        }
        if (str.equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\\-|0-9]\\d*\\.?\\d*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 字符串补齐功能
     *
     * @param sourceStr 原字符串
     * @param toLen     总长度
     * @param ch        填充字符
     * @param left      左补/右补
     * @return
     */
    public static String FillStr(String sourceStr, int toLen, String ch, boolean left) {
        StringBuffer tempStr = new StringBuffer(sourceStr);

        int byteLen = getByteCount(sourceStr);
        int Len = toLen - byteLen;

        for (int i = 0; i < Len; i++) {
            if (left) {
                tempStr.insert(0, ch);
            } else {
                tempStr.append(ch);
            }
        }

        return tempStr.toString();
    }

    /**
     * 字节长度
     * 基本原理是将字符串中所有的非标准字符（双字节字符）替换成两个标准字符（**，或其他的也可以）。
     * 这样就可以直接例用length方法获得字符串的字节长度了
     *
     * @param s
     * @return
     */
    public static int getByteCount(String s) {
        s = s.replaceAll("[^\\x00-\\xff]", "**");
        int length = s.length();
        return length;
    }

    /**
     * 更新电商库存DCP_ECOMMERCE_STOCK、DCP_ECOMMERCE_STOCKHIS
     *
     * @param dao
     * @param jsonStr {"datas":[{"EID":"99","ecStockNO":"123","docType":"0","opNo":"admin","loadType":"2","loadDocno":"","loadDocShop":"","detail":[{"loadDocItem":"1","ecPlatformNO":"91app","shopId":"1001","pluNO":"10001","pluBarcode":"","unit":"BAO","qty":"2"},{"loadDocItem":"1","ecPlatformNO":"shopee","shopId":"1001","pluNO":"10001","pluBarcode":"","unit":"BAO","qty":"2"}]},{"EID":"99","ecStockNO":"456","docType":"0","opNo":"admin","loadType":"2","loadDocno":"","loadDocShop":"","detail":[{"loadDocItem":"1","ecPlatformNO":"91app","shopId":"1001","pluNO":"10002","pluBarcode":"","unit":"BAO","qty":"3"},{"loadDocItem":"1","ecPlatformNO":"shopee","shopId":"1001","pluNO":"10002","pluBarcode":"","unit":"BAO","qty":"3"}]}]}
     * @return
     */
    public static List<DataProcessBean> UpdateEC_Stock(DsmDAO dao, String jsonStr) {
        List<DataProcessBean> data = new ArrayList<DataProcessBean>();
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray orders = json.getJSONArray("datas");
            if (orders.length() > 0) {
                for (int a = 0; a < orders.length(); a++) {
                    String eId = orders.getJSONObject(a).getString("eId");


                    String ecStockNO = orders.getJSONObject(a).getString("ecStockNO");
                    String docType = orders.getJSONObject(a).getString("docType");//库存异动方向：0入  1出
                    String opNo = orders.getJSONObject(a).getString("opNo");
                    String loadType = orders.getJSONObject(a).getString("loadType");//来源类型： 0手动上下架 1调拨收货 2订单 3调拨出库
                    String loadDocno = orders.getJSONObject(a).getString("loadDocno");//来源单号
                    String loadDocShop = orders.getJSONObject(a).getString("loadDocShop");//来源门店
                    //
                    JSONArray detail = orders.getJSONObject(a).getJSONArray("detail");
                    for (int b = 0; b < detail.length(); b++) {
                        String loadDocItem = detail.getJSONObject(b).getString("loadDocItem");//来源项次
                        String ecPlatformNO = detail.getJSONObject(b).getString("ecPlatformNO");//1单存在多平台上架
                        String shopId = detail.getJSONObject(b).getString("shopId");//多平台就对应多归属门店
                        if (isNumeric(loadDocItem) == false) {
                            loadDocItem = "1";
                        }
                        String pluNO = detail.getJSONObject(b).getString("pluNO");
                        String pluBarcode = detail.getJSONObject(b).getString("pluBarcode");
                        String unit = detail.getJSONObject(b).getString("unit");
                        String qty = detail.getJSONObject(b).getString("qty");//异动数量
                        if (isNumericType(qty) == false) {
                            qty = "1";
                        }

                        //检查是否存在
                        List<Map<String, Object>> getData = dao.executeQuerySQL("select * from DCP_ECOMMERCE_STOCKHIS where eId='" + eId + "' and SHOPID='" + shopId + "' and ECSTOCKNO='" + ecStockNO + "' ", null);
                        if (getData == null || getData.isEmpty()) {
                            String[] Columns = {
                                    "EID", "SHOPID", "ECSTOCKNO", "ECPLATFORMNO",
                                    "DOCTYPE", "OPNO", "LOAD_TYPE",
                                    "LOAD_DOCNO", "LOAD_DOCSHOP", "LOAD_DOCITEM",
                                    "PLUNO", "PLUBARCODE", "UNIT", "QTY"};

                            DataValue[] insValue = new DataValue[]
                                    {
                                            new DataValue(eId, Types.VARCHAR),
                                            new DataValue(shopId, Types.VARCHAR),
                                            new DataValue(ecStockNO, Types.VARCHAR),
                                            new DataValue(ecPlatformNO, Types.VARCHAR),
                                            new DataValue(docType, Types.VARCHAR),
                                            new DataValue(opNo, Types.VARCHAR),
                                            new DataValue(loadType, Types.VARCHAR),
                                            new DataValue(loadDocno, Types.VARCHAR),
                                            new DataValue(loadDocShop, Types.VARCHAR),
                                            new DataValue(loadDocItem, Types.INTEGER),
                                            new DataValue(pluNO, Types.VARCHAR),
                                            new DataValue(pluBarcode, Types.VARCHAR),
                                            new DataValue(unit, Types.VARCHAR),
                                            new DataValue(qty, Types.FLOAT)
                                    };

                            InsBean ib = new InsBean("DCP_ECOMMERCE_STOCKHIS", Columns);
                            ib.addValues(insValue);
                            data.add(new DataProcessBean(ib));
                            ib = null;

                            //DCP_ECOMMERCE_STOCK
                            StringBuffer sb = new StringBuffer("MERGE INTO DCP_ECOMMERCE_STOCK A USING (select '" + eId + "' EID,'" + shopId + "' SHOPID,'" + ecPlatformNO + "' ECPLATFORMNO,'" + pluNO + "' PLUNO FROM dual ) B ON (A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.ECPLATFORMNO=B.ECPLATFORMNO AND A.PLUNO=B.PLUNO) ");
                            sb.append("WHEN MATCHED THEN ");
                            if (docType.equals("1")) {
                                sb.append("UPDATE SET A.ECPLATFORMSTOCK=A.ECPLATFORMSTOCK-" + qty + " ");
                            } else {
                                sb.append("UPDATE SET A.ECPLATFORMSTOCK=A.ECPLATFORMSTOCK+" + qty + " ");
                            }
                            sb.append("WHEN NOT MATCHED THEN ");
                            sb.append("INSERT (A.eId,A.SHOPID,A.ECPLATFORMNO,A.PLUNO,A.PLUBARCODE,A.UNIT,A.ECPLATFORMSTOCK) ");
                            sb.append("VALUES('" + eId + "','" + shopId + "','" + ecPlatformNO + "','" + pluNO + "','" + pluBarcode + "','" + unit + "'," + qty + ") ");
                            //
                            ExecBean exec = new ExecBean(sb.toString());
                            data.add(new DataProcessBean(exec));
                            exec = null;
                            sb = null;


                        }

                        //
                        if (getData != null) {
                            getData.clear();
                        }
                    }
                }
            }

            orders = null;
            json = null;

            //StaticInfo.dao.useTransactionProcessData(data);
            return data;
        } catch (Exception e) {
            if (data != null) {
                data.clear();
            }
            return data;
        } finally {
            data = null;
        }
    }

    /**
     * 根据传入的日期+-天数
     *
     * @param InputDate 传入的日期20170101
     * @param days      -1
     * @return 20161231
     * @throws Exception
     */
    public static String GetStringDate(String InputDate, int days) {
        String sResDate = InputDate;

        Calendar myTempcal = Calendar.getInstance();//获得当前时间

        SimpleDateFormat myTempdf = new SimpleDateFormat("yyyyMMdd");

        Date dateTemp;
        try {
            dateTemp = myTempdf.parse(sResDate);

            myTempcal.setTime(dateTemp);
            //+-天
            myTempcal.add(Calendar.DATE, days);

            sResDate = myTempdf.format(myTempcal.getTime());
        } catch (ParseException e) {
            // TODO Auto-generated catch block

        }
        return sResDate;
    }

    /**
     * 根据传入的日期+-天数
     *
     * @param InputDate 传入的日期20170101
     * @param days      -1
     * @return 2016-12-31
     * @throws Exception
     */
    public static String GetStringDateLine(String InputDate, int days) {
        String sResDate = InputDate;

        Calendar myTempcal = Calendar.getInstance();//获得当前时间

        SimpleDateFormat myTempdf = new SimpleDateFormat("yyyyMMdd");

        Date dateTemp;
        try {
            dateTemp = myTempdf.parse(sResDate);

            myTempcal.setTime(dateTemp);
            //+-天
            myTempcal.add(Calendar.DATE, days);

            myTempdf = new SimpleDateFormat("yyyy-MM-dd");
            sResDate = myTempdf.format(myTempcal.getTime());
        } catch (ParseException e) {
            // TODO Auto-generated catch block

        }
        return sResDate;
    }

    public static String GetStringDateLine(String InputDate, int days, boolean line) {
        String sResDate = InputDate;

        Calendar myTempcal = Calendar.getInstance();//获得当前时间

        SimpleDateFormat myTempdf = new SimpleDateFormat("yyyyMMdd");

        Date dateTemp;
        try {
            dateTemp = myTempdf.parse(sResDate);

            myTempcal.setTime(dateTemp);
            //+-天
            myTempcal.add(Calendar.DATE, days);

            if (line) {
                myTempdf = new SimpleDateFormat("yyyy-MM-dd");
            }

            sResDate = myTempdf.format(myTempcal.getTime());
        } catch (ParseException e) {
            // TODO Auto-generated catch block

        }
        return sResDate;
    }

    /**
     * double 保留小数位数，表示四舍五入
     *
     * @param source
     * @param scale  小数位数
     * @return
     */
    public static String GetdoubleScale(double source, int scale) {
        int roundingMode = 4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
        BigDecimal bd = new BigDecimal((double) source);
        bd = bd.setScale(scale, roundingMode);
        String dst = bd.toPlainString();
        return dst;
    }


    //写日志
    public static void writelog(String log) throws IOException {
        //生成文件路径
        String sdFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());//当天日期
        String path = System.getProperty("user.dir") + "\\log\\log" + sdFormat + ".txt";
        File file = new File(path);

        String dirpath = System.getProperty("user.dir") + "\\log";
        File dirfile = new File(dirpath);
        if (!dirfile.exists()) {
            dirfile.mkdir();
        }
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream writerStream = new FileOutputStream(file, true);
        OutputStreamWriter osw = new OutputStreamWriter(writerStream, "UTF-8");
        BufferedWriter writer = new BufferedWriter(osw);

        //前面加上时间
        String stFormat = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());//当天日期
        String slog = stFormat + " " + log + "\r\n";

        stFormat = null;
        sdFormat = null;

        writer.append(slog);
        writer.close();
        writer = null;

        osw.close();
        osw = null;

        writerStream.close();
        writerStream = null;

        file = null;

        sdFormat = null;
    }

    //写日志
    public static void writelog_SaleCreate(String log) throws IOException {
        //生成文件路径
        String sdFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());//当天日期
        String path = System.getProperty("user.dir") + "\\log\\SaleCreatelog" + sdFormat + ".txt";
        File file = new File(path);

        String dirpath = System.getProperty("user.dir") + "\\log";
        File dirfile = new File(dirpath);
        if (!dirfile.exists()) {
            dirfile.mkdir();
        }
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream writerStream = new FileOutputStream(file, true);
        OutputStreamWriter osw = new OutputStreamWriter(writerStream, "UTF-8");
        BufferedWriter writer = new BufferedWriter(osw);

        //前面加上时间
        String stFormat = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());//当天日期
        String slog = stFormat + " " + log + "\r\n";

        stFormat = null;
        sdFormat = null;

        writer.append(slog);
        writer.close();
        writer = null;

        osw.close();
        osw = null;

        writerStream.close();
        writerStream = null;

        file = null;

        sdFormat = null;
    }

    //写日志
    public static void writelog_SaleCreateException(String log) throws IOException {
        //生成文件路径
        String sdFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());//当天日期
        String path = System.getProperty("user.dir") + "\\log\\SaleCreateExceptionlog" + sdFormat + ".txt";
        File file = new File(path);

        String dirpath = System.getProperty("user.dir") + "\\log";
        File dirfile = new File(dirpath);
        if (!dirfile.exists()) {
            dirfile.mkdir();
        }
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream writerStream = new FileOutputStream(file, true);
        OutputStreamWriter osw = new OutputStreamWriter(writerStream, "UTF-8");
        BufferedWriter writer = new BufferedWriter(osw);

        //前面加上时间
        String stFormat = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());//当天日期
        String slog = stFormat + " " + log + "\r\n";

        stFormat = null;
        sdFormat = null;

        writer.append(slog);
        writer.close();
        writer = null;

        osw.close();
        osw = null;

        writerStream.close();
        writerStream = null;

        file = null;

        sdFormat = null;
    }

    /**
     * 商品的单位
     *
     * @return
     */
    public static String getGoods() {
        String sql = "select wunit, pluno,eId FROM DCP_GOODS where status='100'";

        return sql;
    }

    /**
     * REDIS查找单位换算率,返回WUNIT,UNIT_RATIO,PUNIT,EID
     *
     * @param eId
     * @param pluNO
     * @param punit
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> getUnit_Ratio_Redis(DsmDAO dao, String eId, String pluNO, String punit) throws Exception {
        List<Map<String, Object>> UnitRatio = new ArrayList<Map<String, Object>>();

        try {
            String fieldpluno = eId + "_" + pluNO;

            //初始化Redis
            RedisPosPub RP = new RedisPosPub();
            List<String> lstJson = RP.getHighHashMap("RETAIL_DCP_GOODS", fieldpluno, "");
            RP.Close();
            RP = null;

            BigDecimal unitRatio = new BigDecimal(1);

            if (lstJson != null && lstJson.size() > 0 && lstJson.get(0) != null) {
                Map<String, Object> tempMap = com.alibaba.fastjson.JSONObject.parseObject(lstJson.get(0));
                String WUNIT = tempMap.get("WUNIT").toString();

                //包装单位与库存单位相等
                if (WUNIT.equals(punit)) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("EID", eId);
                    map.put("WUNIT", WUNIT);
                    map.put("PUNIT", punit);
                    map.put("UNIT_RATIO", unitRatio);

                    UnitRatio.add(map);
                } else {
                    RP = new RedisPosPub();
                    //CST001换算率映射表
                    lstJson = RP.getHighHashMap("RETAIL_CST001", eId + "_" + pluNO + "_" + punit + "_" + WUNIT, eId + "_ALL" + "_" + punit + "_" + WUNIT);
                    RP.Close();
                    RP = null;

                    if (lstJson != null) {
                        for (int i = 0; i < lstJson.size(); i++) {
                            if (lstJson.get(i) != null) {
                                tempMap = com.alibaba.fastjson.JSONObject.parseObject(lstJson.get(i));
                                unitRatio = new BigDecimal(tempMap.get("UNIT_RATIO").toString());
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("EID", eId);
                                map.put("WUNIT", WUNIT);
                                map.put("PUNIT", punit);
                                map.put("UNIT_RATIO", unitRatio);

                                UnitRatio.add(map);
                                map = null;
                            }
                        }
                    }

                    if (UnitRatio.size() == 0) {
                        //缓存没找到，查询数据库
                        String sqlwunitName = "select a.EID,a.PLUNO,ounit as PUNIT,unit as WUNIT,nvl(QTY,1)/nvl(OQTY,1) AS UNIT_RATIO,b.UPDATE_TIME,N'1' as ITEM  "
                                + "from DCP_GOODS a "
                                + "inner join DCP_UNITCONVERT_GOODS b ON a.eId=b.eId "
                                + "and a.PLUNO=b.PLUNO  "
                                + "and a.wunit=b.unit "
                                + "and a.status='100' "
                                + "and b.status='100' "
                                + "and b.OQTY>0 "
                                + "AND a.eId='" + eId + "' "
                                + "AND a.PLUNO=N'" + pluNO + "' "
                                + "AND b.ounit=N'" + punit + "' "
                                + "AND a.WUNIT=N'" + WUNIT + "' "
                                + "union all "
                                + "select eId,N'ALL',ounit as PUNIT,unit as WUNIT,UNIT_RATIO,UPDATE_TIME,N'2' as ITEM  from DCP_UNITCONVERT "
                                + "where eId='" + eId + "' "
                                + "AND ounit=N'" + punit + "' "
                                + "AND unit=N'" + WUNIT + "' "
                                + "AND status='100' "
                                + "order by ITEM  ";

                        List<Map<String, Object>> sqlwunitNameData = dao.executeQuerySQL(sqlwunitName, null);
                        if (sqlwunitNameData != null && sqlwunitNameData.isEmpty() == false) {
                            unitRatio = new BigDecimal(sqlwunitNameData.get(0).get("UNIT_RATIO").toString());
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("EID", eId);
                            map.put("WUNIT", WUNIT);
                            map.put("PUNIT", punit);
                            map.put("UNIT_RATIO", unitRatio);

                            UnitRatio.add(map);

                            //同时写入REDIS缓存
                            //PLUNO这个字段值有可能是ALL,因为是union出来的重命名
                            String field = eId + "_" + sqlwunitNameData.get(0).get("PLUNO").toString() + "_" + punit + "_" + WUNIT;

                            //
                            Map<String, String> tempMapSQL = new HashMap<String, String>();
                            tempMapSQL.put(field, com.alibaba.fastjson.JSONObject.toJSONString(sqlwunitNameData.get(0)));

                            RP = new RedisPosPub();
                            RP.setHighHashMap("RETAIL_CST001", tempMapSQL);
                            RP.Close();
                            RP = null;
                        }

                        //
                        if (sqlwunitNameData != null) {
                            sqlwunitNameData.clear();
                            sqlwunitNameData = null;
                        }

                    }

                }

                //
                tempMap = null;
            } else {
                String sqlWunitName = "select * FROM DCP_GOODS WHERE PLUNO='" + pluNO + "' AND  EID='" + eId + "' AND status='100' ";
                List<Map<String, Object>> sqlWunitNameData = dao.executeQuerySQL(sqlWunitName, null);

                if (sqlWunitNameData != null && sqlWunitNameData.isEmpty() == false) {
                    String WUNIT = sqlWunitNameData.get(0).get("WUNIT").toString();

                    //
                    Map<String, String> tempMapSQL = new HashMap<String, String>();
                    tempMapSQL.put(fieldpluno, com.alibaba.fastjson.JSONObject.toJSONString(sqlWunitNameData.get(0)));

                    RP = new RedisPosPub();
                    //同时写入REDIS缓存
                    RP.setHighHashMap("RETAIL_DCP_GOODS", tempMapSQL);
                    RP.Close();
                    RP = null;

                    //包装单位与库存单位相等
                    if (WUNIT.equals(punit)) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("EID", eId);
                        map.put("WUNIT", WUNIT);
                        map.put("PUNIT", punit);
                        map.put("UNIT_RATIO", unitRatio);

                        UnitRatio.add(map);
                    } else {
                        RP = new RedisPosPub();
                        //CST001换算率映射表
                        lstJson = RP.getHighHashMap("RETAIL_CST001", eId + "_" + pluNO + "_" + punit + "_" + WUNIT, eId + "_ALL" + "_" + punit + "_" + WUNIT);
                        RP.Close();
                        RP = null;

                        if (lstJson != null) {
                            for (int i = 0; i < lstJson.size(); i++) {
                                if (lstJson.get(i) != null) {
                                    Map<String, Object> tempMap = com.alibaba.fastjson.JSONObject.parseObject(lstJson.get(i));

                                    unitRatio = new BigDecimal(tempMap.get("UNIT_RATIO").toString());

                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("EID", eId);
                                    map.put("WUNIT", WUNIT);
                                    map.put("PUNIT", punit);
                                    map.put("UNIT_RATIO", unitRatio);

                                    UnitRatio.add(map);

                                    //
                                    tempMap = null;
                                    map = null;
                                }
                            }
                        }

                        if (UnitRatio.size() == 0) {
                            //缓存没找到，查询数据库
                            String sqlwunitName = "select a.EID,a.PLUNO,ounit as PUNIT,unit as WUNIT,QTY/OQTY AS UNIT_RATIO,b.UPDATE_TIME,N'1' as ITEM  "
                                    + "from DCP_GOODS a "
                                    + "inner join DCP_UNITCONVERT_GOODS b ON a.eId=b.eId "
                                    + "and a.PLUNO=b.PLUNO  "
                                    + "and a.wunit=b.unit "
                                    + "and a.status='100' "
                                    + "and b.status='100' "
                                    + "and b.OQTY>0 "
                                    + "AND a.eId='" + eId + "' "
                                    + "AND a.PLUNO=N'" + pluNO + "' "
                                    + "AND b.ounit=N'" + punit + "' "
                                    + "AND a.WUNIT=N'" + WUNIT + "' "
                                    + "union all "
                                    + "select eId,N'ALL',ounit as PUNIT,unit as WUNIT,UNIT_RATIO,UPDATE_TIME,N'2' as ITEM  from DCP_UNITCONVERT "
                                    + "where eId='" + eId + "' "
                                    + "AND ounit=N'" + punit + "' "
                                    + "AND unit=N'" + WUNIT + "' "
                                    + "AND status='100' "
                                    + "ORDER BY ITEM ";

                            List<Map<String, Object>> sqlwunitNameData = dao.executeQuerySQL(sqlwunitName, null);
                            if (sqlwunitNameData != null && sqlwunitNameData.isEmpty() == false) {
                                unitRatio = new BigDecimal(sqlwunitNameData.get(0).get("UNIT_RATIO").toString());
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("EID", eId);
                                map.put("WUNIT", WUNIT);
                                map.put("PUNIT", punit);
                                map.put("UNIT_RATIO", unitRatio);

                                UnitRatio.add(map);


                                //同时写入REDIS缓存
                                //PLUNO这个字段值有可能是ALL,因为是union出来的重命名
                                String field = eId + "_" + sqlwunitNameData.get(0).get("PLUNO").toString() + "_" + punit + "_" + WUNIT;

                                //
                                tempMapSQL.clear();
                                tempMapSQL.put(field, com.alibaba.fastjson.JSONObject.toJSONString(sqlwunitNameData.get(0)));

                                RP = new RedisPosPub();
                                RP.setHighHashMap("RETAIL_CST001", tempMapSQL);
                                RP.Close();
                                RP = null;
                            }

                            //
                            if (sqlwunitNameData != null) {
                                sqlwunitNameData.clear();
                                sqlwunitNameData = null;
                            }

                        }

                    }

                    //
                    if (tempMapSQL != null) {
                        tempMapSQL.clear();
                        tempMapSQL = null;
                    }

                }

                //
                if (sqlWunitNameData != null) {
                    sqlWunitNameData.clear();
                    sqlWunitNameData = null;
                }

            }

            return UnitRatio;
        } catch (Exception e) {
            if (UnitRatio != null) {
                UnitRatio.clear();
            }

            return UnitRatio;
        } finally {
            UnitRatio = null;
        }

    }

    /**
     * 单位换算率,缓存找不到，再找数据库
     *
     * @param dao
     * @param eId
     * @param pluNO
     * @param punit
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> getUnit_Ratio_Middle(DsmDAO dao, String eId, String pluNO, String punit) throws Exception {
        List<Map<String, Object>> UnitRatio = new ArrayList<Map<String, Object>>();
        try {
            UnitRatio = getUnit_Ratio_Redis(dao, eId, pluNO, punit);

            return UnitRatio;
        } catch (Exception e) {
            return UnitRatio;
        } finally {
            UnitRatio = null;
        }
    }

    /**
     * @param dao
     * @param eId    企业编码
     * @param pluNo  品号
     * @param oUnit  来源单位
     * @param toUnit 目标单位
     * @param oQty   转换数量
     * @return
     * @throws Exception
     */
    public static String getUnitConvert(DsmDAO dao, String eId, String pluNo, String oUnit, String toUnit, String oQty) throws Exception {
        String toQty = "0";

        try {
            if (Check.Null(eId)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取单位换算(F_DCP_UnitConvert)企业编号(eId)不可为空！");
            } else if (Check.Null(oUnit)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取单位换算(F_DCP_UnitConvert)来源单位(oUnit)不可为空！");
            } else if (Check.Null(toUnit)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取单位换算(F_DCP_UnitConvert)目标单位(toUnit)不可为空！");
            } else if (Check.Null(oQty)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取单位换算(F_DCP_UnitConvert)转换数量(oQty)不可为空！");
            }

//			if (Check.Null(eId) || Check.Null(oUnit) || Check.Null(toUnit) || Check.Null(oQty)) {
//				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单位换算时企业编号、来源单位、目标单位，转换数量 不可为空！");
//			} 

            else {
                Boolean strResult = oQty.matches("-?[0-9]+.?[0-9]*");
                if (strResult == false) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单位换算时转换数量oQty必须为数字类型！");
                }
            }

            String sql = " select F_DCP_UnitConvert('" + eId + "','" + pluNo + "','" + oUnit + "','" + toUnit + "','" + oQty + "') as toQty from dual  ";
            List<Map<String, Object>> convertDatas = dao.executeQuerySQL(sql, null);

            if (convertDatas != null && convertDatas.size() > 0) {
                toQty = convertDatas.get(0).get("TOQTY").toString();

                //
                convertDatas.clear();
                convertDatas = null;
            } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "找不到单位换算信息");
            }
            return toQty;
        } catch (Exception e) {
            String err_msg = e.getMessage();
            if (e.getCause() != null) {
                err_msg = e.getCause().getMessage();
            }
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取单位换算信息异常：" + err_msg);
        } finally {
            toQty = null;
        }
    }


    /**
     * 查询商品可销量
     *
     * @param dao
     * @param eId       //企业编码
     * @param pluNo     //品号
     * @param featureNo //特征码
     * @param orgNo     //组织编号
     * @param channelId //渠道编号
     * @param warehouse //仓库编号
     * @param sUnit     //销售单位
     * @return
     * @throws Exception
     */
    public static String queryStockQty(DsmDAO dao, String eId, String pluNo, String featureNo, String orgNo, String channelId, String warehouse, String sUnit) throws Exception {
        String qty = "";
        try {
            String sql = "  SELECT F_DCP_GET_SALEQTY ( '" + eId + "' , '" + pluNo + "','" + featureNo + "','" + orgNo + "','" + channelId + "','" + warehouse + "','" + sUnit + "' ) as qty FROM dual  ";
            logger.info("库存查询F_DCP_GET_SALEQTY sql: " + sql);
            List<Map<String, Object>> qtyDatas = dao.executeQuerySQL(sql, null);

            if (qtyDatas != null && qtyDatas.size() > 0) {
                qty = qtyDatas.get(0).get("QTY").toString();

                qtyDatas.clear();
                qtyDatas = null;
            }
            //库存查不到的时候返回空  BY JZMA 20200916
            //            else {
            //                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "查询可售量时找不到该商品库存信息");
            //            }
            return qty;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "查询可售量时异常：" + e.getMessage());
        } finally {
            qty = null;
        }
    }


    /**
     * 返回in (字段SQL) 例如:'11','22','33'
     *
     * @param str
     * @return
     */
    public static String getArrayStrSQLIn(String[] str) {
        StringBuffer str2 = new StringBuffer("");
        try {
            for (String s : str) {
                str2.append("'" + s + "'" + ",");
            }
            if (str2.length() > 0) {
                str2.deleteCharAt(str2.length() - 1);
            }

            return str2.toString();
        } catch (Exception e) {
            str2.setLength(0);
            return str2.toString();
        } finally {
            str2.setLength(0);
            str2 = null;
        }
    }


    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     * 此方法如果date 带时间，不足24小时会按照小于1天计算
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        return days;
    }

    /**
     * ETLJOB日志单独写文件myLog\
     *
     * @param str
     * @throws IOException
     */
    public static void WriteETLJOBLog(String str) throws IOException {
        //生成文件路径
        String sdFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());//当天日期
        String path = System.getProperty("catalina.home") + "\\myLog\\ETLJOB" + sdFormat + ".txt";
        File file = new File(path);

        String dirpath = System.getProperty("catalina.home") + "\\myLog";
        File dirfile = new File(dirpath);
        if (!dirfile.exists()) {
            dirfile.mkdir();
        }
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream writerStream = new FileOutputStream(file, true);
        OutputStreamWriter osw = new OutputStreamWriter(writerStream, "UTF-8");
        BufferedWriter writer = new BufferedWriter(osw);

        //前面加上时间
        String stFormat = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());//当天日期
        String slog = stFormat + " " + str + "\r\n";

        stFormat = null;
        sdFormat = null;

        writer.append(slog);
        writer.close();
        writer = null;

        osw.close();
        osw = null;

        writerStream.close();
        writerStream = null;

        file = null;

        sdFormat = null;
    }

    public static void WritePriceLog(String str) throws IOException {
        //生成文件路径
        String sdFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());//当天日期
        String path = System.getProperty("catalina.home") + "\\myLog\\price" + sdFormat + ".txt";
        File file = new File(path);

        String dirpath = System.getProperty("catalina.home") + "\\myLog";
        File dirfile = new File(dirpath);
        if (!dirfile.exists()) {
            dirfile.mkdir();
        }
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream writerStream = new FileOutputStream(file, true);
        OutputStreamWriter osw = new OutputStreamWriter(writerStream, "UTF-8");
        BufferedWriter writer = new BufferedWriter(osw);

        //前面加上时间
        String stFormat = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());//当天日期
        String slog = stFormat + " " + str + "\r\n";

        stFormat = null;
        sdFormat = null;

        writer.append(slog);
        writer.close();
        writer = null;

        osw.close();
        osw = null;

        writerStream.close();
        writerStream = null;

        file = null;

        sdFormat = null;
    }

    //深度拷贝
    public static <T> T deepCopy(T originobj) throws Exception {
        T model = null;
        try {
            //将该对象序列化成流,因为写在流里的是对象的一个拷贝，而原对象仍然存在于JVM里面。所以利用这个特性可以实现对象的深拷贝
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(originobj);

            //将流序列化成对象
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            model = (T) ois.readObject();


            ois.close();
            ois = null;

            bis.close();
            bis = null;

            oos.close();
            oos = null;

            bos.close();
            bos = null;

            return model;
        } catch (Exception e) {
            model = null;
            return model;
        } finally {
            model = null;
        }
    }

    //查询用户的功能权限
    public static String getQueryModularFunctionSql(String reqopno, String eId, String funcno, String fproname) throws Exception {
        String sql = null;

        try {
            //查詢條件
            String opNO = reqopno;

            StringBuffer sqlbuf = new StringBuffer("");
            if (opNO.equals("admin")) {
                sqlbuf.append("select modularNO,modularName,upModularNO,modularLevel,isCollection,funcno,funName,modularNO1,PowerType,MproName,FproName from "
                        + "("
                        + "select distinct b.modularNO modularNO,b.modularLevel modularLevel,b.uppermodular as upModularNO, b.chsmsg modularName,d.funcno,c.chsmsg funName,c.modularno modularNO1,d.PowerType ,b.proname as MproName,c.proname as FproName,(case WHEN e.modularno IS NULL  then 'false' ELSE 'true' end) AS isCollection "
                        + "from DCP_MODULAR b  "
                        + "left join DCP_MODULAR_function c on b.eId=c.eId and b.modularno=c.modularno  "
                        + "and c.status='100' ");
                sqlbuf.append("LEFT join platform_power d on c.eId=d.eId and c.funcno=d.funcno "
                        + "LEFT JOIN DCP_COLLECTION e on b.eId=e.eId and b.modularno=e.modularno  "
                        + "and e.opno='" + opNO + "' "
                        + "WHERE b.eId='" + eId + "' "
                        + "and b.status='100' "
                        + "order by b.PRIORITY,b.modularno  "
                        + ")");
            } else {
                sqlbuf.append("select modularNO,modularName,upModularNO,modularLevel,isCollection,funcno,funName,modularNO1,PowerType,MproName,FproName from "
                        + "("
                        + "select distinct a.modularNO modularNO,b.modularLevel modularLevel,b.uppermodular as upModularNO, b.chsmsg modularName,d.funcno,c.chsmsg funName,c.modularno modularNO1,d.PowerType ,b.proname as MproName,c.proname as FproName,(case WHEN e.modularno IS NULL  then 'false' ELSE 'true' end) AS isCollection "
                        + "from platform_billpower a "
                        + "inner join DCP_MODULAR b on a.eId=b.eId and a.modularno=b.modularno "
                        + "left join DCP_MODULAR_function c on a.eId=c.eId and a.modularno=c.modularno  "
                        + "and c.status='100' ");

                sqlbuf.append(" and c.funcno in "
                        + "("
                        + "select funcno from platform_power "
                        + "where eId='" + eId + "' "
                        + "and opgroup in "
                        + "("
                        + "select opgroup from platform_staffs_role "
                        + "where eId='" + eId + "' "
                        + "and opno='" + opNO + "'"
                        + ")"
                        + ") "
                        + "LEFT join platform_power d on c.eId=d.eId and c.funcno=d.funcno "
                        + "LEFT JOIN DCP_COLLECTION e on a.eId=e.eId and a.modularno=e.modularno  "
                        + "and e.opno='" + opNO + "' "
                        + "WHERE A.eId='" + eId + "' "
                        + "and a.opgroup in "
                        + "("
                        + "select opgroup from platform_staffs_role "
                        + "where eId='" + eId + "' "
                        + "and opno='" + opNO + "' ) "
                        + "and b.status='100' "
                        + " ORDER BY a.PRIORITY,a.modularno "
                        //+ "order by a.modularno "

                        + ")");
            }

            if (!funcno.isEmpty() && !fproname.isEmpty()) {
                sqlbuf.append(" where funcno='" + funcno + "' and fproname='" + fproname + "' ");
            }

            sql = sqlbuf.toString();

            //
            sqlbuf.setLength(0);

            return sql;
        } catch (Exception e) {
            sql = null;
            return sql;
        } finally {
            sql = null;
        }
    }


    /**
     * 便于用关联join
     *
     * @param sourcePluno 10001,10002,10003,
     * @return with a as (select  xmlagg(xmlparse(content PLUNO wellformed)).getclobval() PLUNO from (SELECT '10001,10002,' PLUNO FROM DUAL UNION ALL SELECT '10003,10004,' PLUNO FROM DUAL UNION ALL SELECT '10005,' PLUNO FROM DUAL )) select to_char(regexp_substr(PLUNO,'[^,]+',1,rownum)) PLUNO from a connect by rownum <= length(regexp_replace(PLUNO,'[^,]+'))
     * @throws Exception
     */
    public static String getFormatSourcePluno(String[] sourcePluno) throws Exception {
        String tempStr = "";
        try {
            StringBuffer errMsg = new StringBuffer("");
            StringBuffer temp = new StringBuffer("");
            ArrayList<String> list = new ArrayList<String>();
            for (String x1 : sourcePluno) {
                if (x1 == null || x1.trim().equals("")) continue;//排除空

                if (temp.length() + (x1 + ",").length() > 4000) {
                    list.add(temp.toString());
                    temp.setLength(0);
                    temp.append(x1 + ",");
                } else {
                    temp.append(x1 + ",");
                }
            }

            if (temp.length() > 0) {
                list.add(temp.toString());
            }

            int lstCount = list.size();

            //没记录
            if (lstCount == 0) {
                return "";
            }

            for (int i = 0; i < lstCount; i++) {
                if (i != lstCount - 1) {
                    errMsg.append("SELECT '" + list.get(i) + "' PLUNO FROM DUAL UNION ALL ");
                } else {
                    errMsg.append("SELECT '" + list.get(i) + "' PLUNO FROM DUAL ");
                }
            }

            //这个字符串把PLUNO字段替换掉可以大大提高效能
            String sTemp = "(select  xmlagg(xmlparse(content PLUNO wellformed)).getclobval() PLUNO from (" + errMsg.toString() + "))";

            errMsg.insert(0, " (with a as (select  xmlagg(xmlparse(content PLUNO wellformed)).getclobval() PLUNO from (");//从前面追加
            errMsg.append(")) select to_char(regexp_substr(" + sTemp + ",'[^,]+',1,rownum)) PLUNO from a connect by rownum <= length(regexp_replace(" + sTemp + ",'[^,]+'))) ");

            tempStr = errMsg.toString();

            //
            errMsg.setLength(0);
            list.clear();
            list = null;

            return tempStr;
        } catch (Exception e) {
            tempStr = "";
            return tempStr;
        } finally {
            tempStr = null;
        }
    }


    /**
     * 获取营业日期
     *
     * @param dao    DsmDAO
     * @param eId    企业编号
     * @param shopId 门店编号
     * @return 营业日期
     * @throws Exception 异常
     */
    public static String getAccountDate_SMS(DsmDAO dao, String eId, String shopId) throws Exception {
        String sysDate = new SimpleDateFormat("yyyyMMdd").format(new Date());    //系统日期
        String sysTime = new SimpleDateFormat("HHmmss").format(new Date());  //系统时间
        String bDate = sysDate;       //营业日期

        try {
            String eDate = "";            //日结日期
            String EDateTime;            //日结时间
            //【ID1022996】【冠生园3.0】门店没有中午闭店功能 by jinzma 20220105
            String para_ShopEDateTimeCustomize = getPARA_SMS(dao, eId, "", "ShopEDateTimeCustomize"); //是否启用门店自定义日结
            if (Check.Null(para_ShopEDateTimeCustomize) || !para_ShopEDateTimeCustomize.equals("Y")) {
                EDateTime = getPARA_SMS(dao, eId, "", "ShopEDateTime");
            } else {
                EDateTime = getPARA_SMS(dao, eId, shopId, "ShopEDateTime");
            }

            if (Check.Null(EDateTime)) {
                EDateTime = "020000";   //日结时间默认2点
            }
            //查询日结档的日结日期
            String sql = "select max(edate) as edate from dcp_stock_day where eId='" + eId + "' and organizationno='" + shopId + "' ";
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
            if (getQData != null && !getQData.isEmpty()) {
                eDate = getQData.get(0).get("EDATE").toString();
            }
            
             /*营业日期计算说明    BY JZMA 20190103
             IF 系统时间<换日时间   AND 系统时间<日结时间    AND 换日时间<=120000  THEN  营业日期=系统日期-1天
             IF 日结日期>=营业日期  THEN  营业日期=日结日期+1天
		     if (compare_time(sysTime,CHGDTime )== -1 && compare_time(sysTime,EDateTime)== -1 && compare_time(CHGDTime,"120000")<=0) {
                 bDate = GetStringDate(sysDate, -1);    //sysDate-1
             } else {
                 bDate = sysDate;
             }*/


            //是否启用门店自定义日结 by jinzma 20220105, 启用只判断是否小于
            if (Check.Null(para_ShopEDateTimeCustomize) || !para_ShopEDateTimeCustomize.equals("Y")) {
                if (compare_time(sysTime, EDateTime) == -1 && compare_time(EDateTime, "120000") <= 0) {
                    bDate = GetStringDate(sysDate, -1);    //sysDate-1
                } else {
                    bDate = sysDate;
                }
            } else {
                if (compare_time(sysTime, EDateTime) == -1) {
                    bDate = GetStringDate(sysDate, -1);    //sysDate-1
                } else {
                    bDate = sysDate;
                }
            }


            ///管控营业日期无论如何都要比日结日期大1天，例： 当天2点日结的，然后改参数改成8点，2点-8点的单据理论上会出错
            if (!Check.Null(eDate) && compare_date(eDate, bDate) >= 0) {
                bDate = GetStringDate(eDate, 1);    //eDate+1
            }
            return bDate;

        } catch (Exception e) {
            return bDate;
        } finally {
            bDate = null;
        }
    }

    /**
     * 公共取参数值
     *
     * @param dao
     * @param eId    企业编号 (必填)
     * @param shopId 门店编号 (可选)
     * @param para   参数名 (必填)(不区分大小写)
     * @return
     * @throws Exception
     */
    public static String getPARA_SMS(DsmDAO dao, String eId, String shopId, String para) throws Exception {
        String sql = null;
        String def = "";
        String upperPara = para.toUpperCase();
        StringBuffer sqlbuf = new StringBuffer("");
        List<Map<String, Object>> getQData = new ArrayList<Map<String, Object>>();
        try {
            //处理==绑定变量SQL的写法
            List<DataValue> lstDV = new ArrayList<>();
            DataValue dv = null;

            if (Check.Null(shopId)) {
                sqlbuf.append(" select ITEMVALUE from platform_basesettemp "
                        + " where eId=? and status='100' and  upper(item)=? ");

                //?问号参数赋值处理
                dv = new DataValue(eId, Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv = new DataValue(upperPara, Types.VARCHAR);
                lstDV.add(dv);
            } else {
                //【ID1028348】【菲尔雪3.0】门店日结报错  by jinzma 20220901 以下取参数逻辑错误
              /*  sqlbuf.append(""
                        + " SELECT nvl(t.item , e.item ) AS item , NVL(t.itemvalue , e.itemvalue) AS itemValue "
                        + " FROM PLATFORM_BASESETTEMP e "
                        + " LEFT JOIN ( "
                        + " SELECT a.eId, a.templateId , a.restrictshop , a.restrictmachine, b.item , b.itemvalue "
                        + " FROM PLATFORM_PARATEMPLATE a  "
                        + " LEFT JOIN PLATFORM_PARATEMPLATE_ITEM b ON a.eId = b.eId AND a.templateid = b.templateid"
                        + " LEFT JOIN PLATFORM_PARATEMPLATE_SHOP c1 ON a.eId = c1.Eid AND a.templateid = c1.templateid and a.restrictshop = '1' AND c1.shopid = ? "
                        + " LEFT JOIN PLATFORM_PARATEMPLATE_SHOP c2 ON a.eId = c2.Eid AND a.templateid = c2.templateid and a.restrictshop = '2' AND c2.shopid = ? "
                        // + " LEFT JOIN PLATFORM_PARATEMPLATE_MACHINE d ON a.eid = d.eid AND a.templateid = d.templateid"
                        + " WHERE a.eid = ? "
                        + " AND (a.restrictshop='0' or c1.shopid is not null or c2.shopid is null)"
                        + " ) t ON e.eId = t.eId AND e.item = t.item "
                        + " WHERE e.eid = ? "
                        + " AND e.status = '100' "
                        + " AND  upper(e.item) = ? "
                        + " ORDER BY e.item "
                        + "");*/

                sqlbuf.append(""
                        + " SELECT nvl(t.item,e.item ) AS item,NVL(t.itemvalue,e.itemvalue) AS itemValue FROM PLATFORM_BASESETTEMP e"
                        + " LEFT JOIN ("
                        + " SELECT b.item,b.itemvalue FROM PLATFORM_PARATEMPLATE a"
                        + " INNER JOIN PLATFORM_PARATEMPLATE_ITEM b ON a.eId = b.eId AND a.templateid = b.templateid and upper(b.item) = ?"
                        + " WHERE a.eid = ? and a.restrictshop = '0'"
                        + " union all"
                        + " SELECT b.item,b.itemvalue FROM PLATFORM_PARATEMPLATE a"
                        + " INNER JOIN PLATFORM_PARATEMPLATE_ITEM b ON a.eId = b.eId AND a.templateid = b.templateid and upper(b.item) = ?"
                        + " INNER JOIN PLATFORM_PARATEMPLATE_SHOP c ON a.eId = c.Eid AND a.templateid = c.templateid and a.restrictshop = '1' and c.shopid=?"
                        + " WHERE a.eid =? and c.shopid is not null"
                        + " union all"
                        + " SELECT b.item,b.itemvalue FROM PLATFORM_PARATEMPLATE a"
                        + " INNER JOIN PLATFORM_PARATEMPLATE_ITEM b ON a.eId = b.eId AND a.templateid = b.templateid and upper(b.item) = ?"
                        + " left  JOIN PLATFORM_PARATEMPLATE_SHOP c ON a.eId = c.Eid AND a.templateid = c.templateid and a.restrictshop = '2' and c.shopid=?"
                        + " WHERE a.eid = ?  and a.restrictshop = '2' and c.shopid is null "
                        + " ) t ON e.item = t.item"
                        + " WHERE e.eid = ?  AND  upper(e.item) = ? AND e.status = '100'"
                        + " ");
                //?问号参数赋值处理
                dv = new DataValue(upperPara, Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv = new DataValue(eId, Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv = new DataValue(upperPara, Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv = new DataValue(shopId, Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv = new DataValue(eId, Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv = new DataValue(upperPara, Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv = new DataValue(shopId, Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv = new DataValue(eId, Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv = new DataValue(eId, Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv = new DataValue(upperPara, Types.VARCHAR);
                lstDV.add(dv);
            }
            sql = sqlbuf.toString();

            getQData = dao.executeQuerySQL_BindSQL(sql, lstDV);

            if (getQData != null && !getQData.isEmpty()) {
                def = (String) getQData.get(0).get("ITEMVALUE");
            }
            if (getQData != null) {
                getQData.clear();
            }
            getQData = null;
            sqlbuf.setLength(0);

            return def;
        } catch (Exception exception) {
            return def;
        } finally {
            def = null;
        }
    }

    /**
     * 获取大智通配送流水号
     * eId流水记录企业编码,
     * dztno流水记录主键,
     * status流水记录状态,
     * expressno取得的配送编码
     * dztend流水号讫
     *
     * @param dao
     * @param eId 企业编码
     * @return
     * @throws Exception
     */
    public static Map<String, Object> getDZT_Expressno(DsmDAO dao, String eId) throws Exception {
        String expressno = "";
        String dztno = "";
        String status = "";
        String dztend = "";
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            //
            String sql = "select * from OC_shipbookdzt t where t.eId='" + eId + "' and t.status='100' and (t.status=1 or t.status=2) and (t.dzt_endno<>t.dzt_lastno or t.dzt_lastno is null) order by t.status desc,t.dztno  ";
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
            if (getQData != null && getQData.size() > 0) {
                //流水号讫
                dztend = getQData.get(0).get("DZT_ENDNO").toString();
                //已使用到流水号
                String DZT_LASTNO = getQData.get(0).get("DZT_LASTNO").toString();
                if (Strings.isNullOrEmpty(DZT_LASTNO)) {
                    //第一次使用给开始流水号
                    DZT_LASTNO = getQData.get(0).get("DZT_STARTNO").toString();
                } else {
                    //流水+1
                    int tempNo = Integer.parseInt(getQData.get(0).get("DZT_LASTNO").toString()) + 1;

                    DZT_LASTNO = tempNo + "";
                }

                //
                expressno = DZT_LASTNO;
                dztno = getQData.get(0).get("DZTNO").toString();
                status = getQData.get(0).get("STATUS").toString();
            } else {
                expressno = "";
                dztno = "";
                status = "";
                dztend = "";
            }

            getQData.clear();
            getQData = null;

            //此返回结果,便于后续更新处理
            map.put("eid", eId);
            map.put("dztno", dztno);
            map.put("status", status);
            map.put("expressno", expressno);
            map.put("dztend", dztend);

            return map;
        } catch (Exception e) {
            expressno = "";
            dztno = "";
            status = "";
            dztend = "";

            //此返回结果,便于后续更新处理
            map.put("eid", eId);
            map.put("dztno", dztno);
            map.put("status", status);
            map.put("expressno", expressno);
            map.put("dztend", dztend);

            return map;
        } finally {
            map = null;
        }
    }

    public static int compare_date(String DATE1, String DATE2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {

        }
        return 0;
    }

    public static int compare_time(String Time1, String Time2) {
        SimpleDateFormat df = new SimpleDateFormat("HHmmss");
        try {
            Date dt1 = df.parse(Time1);
            Date dt2 = df.parse(Time2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {

        }
        return 0;
    }

    public static List<Map<String, Object>> getWaimaiAppConfig(DsmDAO dao, String eId, String platformType) throws Exception {
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        try {
            StringBuffer sql = new StringBuffer("select * from DCP_ECOMMERCE ");
            sql.append(" where APIKEY is not null and APISECRET  is not null");

            if (eId != null && eId.length() > 0) {
                sql.append(" and eId='" + eId + "'");
            }
            if (platformType != null && platformType.length() > 0) {
                sql.append(" and loaddoctype='" + platformType + "'");
            }

            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql.toString(), null);
            if (getQData != null && getQData.isEmpty() == false) {
                Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
                condition.put("APIKEY", true);
                //调用过滤函数
                listMap = MapDistinct.getMap(getQData, condition);

                condition.clear();
                condition = null;

                getQData.clear();
                getQData = null;
            } else {
                listMap = null;
            }


            return listMap;

        } catch (Exception e) {
            listMap = null;
            return listMap;
        } finally {
            listMap = null;
        }
    }

    public static Map<String, Object> getWaimaiAppConfigByShopNO(DsmDAO dao, String eId, String shopId, String platformType) throws Exception {
        Map<String, Object> tempMap = new HashMap<String, Object>();

        try {
            StringBuffer sql = new StringBuffer("select * from (");

            sql.append(" select * from DCP_MAPPINGSHOP  where eId='" + eId + "'");
            sql.append(" and APPKEY is not null and APPSECRET  is not null");
            if (platformType != null && platformType.length() > 0) {
                sql.append(" and load_doctype='" + platformType + "'");
            }
            sql.append(" and SHOPID='" + shopId + "'");

            sql.append(")");

            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql.toString(), null);
            if (getQData != null && getQData.isEmpty() == false) {
                tempMap = getQData.get(0);

                getQData.clear();
                getQData = null;
            } else {
                tempMap = null;
            }

            return tempMap;
        } catch (Exception e) {
            tempMap = null;
            return tempMap;
        } finally {
            tempMap = null;
        }
    }

    /**
     * 2个不同应用对应线上多个门店绑定线下同一个门店
     *
     * @param dao
     * @param eId
     * @param shopId
     * @param platformType
     * @param app_poi_code
     * @return
     * @throws Exception
     */
    public static Map<String, Object> getWaimaiAppConfigByShopNO_New(DsmDAO dao, String eId, String shopId, String platformType, String app_poi_code) throws Exception {
        Map<String, Object> tempMap = new HashMap<String, Object>();

        try {
            StringBuffer sql = new StringBuffer("select * from (");

            sql.append(" select * from DCP_MAPPINGSHOP  where eId='" + eId + "'");
            sql.append(" and APPKEY is not null and APPSECRET  is not null");
            if (platformType != null && platformType.length() > 0) {
                sql.append(" and load_doctype='" + platformType + "'");
            }
            sql.append(" and SHOPID='" + shopId + "'");
            sql.append(")");

            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql.toString(), null);
            if (getQData != null && getQData.isEmpty() == false) {
                tempMap = getQData.get(0);//默认第1个
                //查询出来多个，匹配下传入的三方门店id
                if (getQData.size() > 1 && app_poi_code != null && !app_poi_code.trim().isEmpty()) {
                    for (Map<String, Object> par : getQData) {
                        try {
                            if (app_poi_code.equals(par.getOrDefault("ORDERSHOPNO", "").toString())) {
                                tempMap = par;
                                return par;
                            }

                        } catch (Exception e) {

                        }

                    }
                }

                getQData.clear();
                getQData = null;
            } else {
                tempMap = null;
            }

            return tempMap;
        } catch (Exception e) {
            tempMap = null;
            return tempMap;
        } finally {
            tempMap = null;
        }
    }


    private static int min(int... is) {
        int min = Integer.MAX_VALUE;
        for (int i : is) {
            if (min > i) {
                min = i;
            }
        }
        return min;
    }

    /**
     * unicode转中文
     *
     * @param unicode
     * @return
     */
    public static String unicodeToCn(String unicode) {
        /** 以 \ u 分割，因为java注释也能识别unicode，因此中间加了一个空格*/
        String[] strs = unicode.split("\\\\u");
        StringBuffer returnStr = new StringBuffer("");
        try {
            // 由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""。
            for (int i = 1; i < strs.length; i++) {
                returnStr.append((char) Integer.valueOf(strs[i], 16).intValue());
            }
            return returnStr.toString();
        } catch (Exception e) {
            returnStr.setLength(0);
            return returnStr.toString();
        } finally {
            returnStr = null;
        }
    }

    /**
     * 中文转unicode
     *
     * @param cn
     * @return
     */
    public static String cnToUnicode(String cn) {
        char[] chars = cn.toCharArray();
        StringBuffer returnStr = new StringBuffer("");
        try {
            for (int i = 0; i < chars.length; i++) {
                returnStr.append("\\u" + Integer.toString(chars[i], 16));
            }
            return returnStr.toString();
        } catch (Exception e) {
            returnStr.setLength(0);
            return returnStr.toString();
        } finally {
            returnStr = null;
        }
    }

    /**
     * 查询外卖映射的第三方门店信息
     *
     * @param dao
     * @param eId
     * @param shopId
     * @param platformType
     * @return
     * @throws Exception
     */
    public static Map<String, Object> getWaimaiMappingShopByErpShopNo(DsmDAO dao, String eId, String shopId, String platformType) throws Exception {
        Map<String, Object> tempMap = new HashMap<String, Object>();
        try {
            StringBuffer sql = new StringBuffer(" select * from DCP_MAPPINGSHOP where BUSINESSID='2' AND LOAD_DOCTYPE='" + platformType + "'");
            sql.append(" and SHOPID='" + shopId + "'");

            if (eId != null && eId.length() > 0) {
                sql.append(" and eId='" + eId + "'");
            }

            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql.toString(), null);
            if (getQData != null && getQData.isEmpty() == false) {
                tempMap = getQData.get(0);

                getQData.clear();
                getQData = null;
            } else {
                tempMap = null;
            }

            return tempMap;
        } catch (Exception e) {
            tempMap = null;
            return tempMap;
        } finally {
            tempMap = null;
        }
    }

    public static void clearTableBaseInfoCache(String posUrl, String apiUserCode, String apiUserKey, String eId, String shopId) {
        if (!StringUtils.isAnyBlank(posUrl, apiUserCode, apiUserKey)) {
            try {
                String requestId = getGUID(false);
                Map<String, Object> map = new HashMap<>();
                map.put("serviceId", "ClearTableBaseInfoCache");
                map.put("requestId", requestId);
                map.put("timestamp", "1");
                map.put("version", "1");
                map.put("apiUserCode", apiUserCode);

                StringBuffer request = new StringBuffer("{\"shopId\":\"" + shopId + "\",\"eId\":\"" + eId + "\"}");

                map.put("sign", PosPub.encodeMD5(request.toString() + apiUserKey));
                HttpSend.doPost(posUrl, request.toString(), map, requestId);

                request.setLength(0);
                request = null;

                map.clear();
                map = null;
            } catch (Exception e) {
                logger.error("\r\n清除缓存失败:{}", e.getMessage());
            }
        } else {
            logger.error("\r\n清除缓存失败:posUrl({}),apiUserCode({}),apiUserKey({})", posUrl, apiUserCode, apiUserKey);
        }
    }

    /**
     * eId-开头的缓存
     *
     * @param posUrl
     * @param apiUserCode
     * @param apiUserKey
     * @param eId
     */
    public static void clearGoodsCache(String posUrl, String apiUserCode, String apiUserKey, String eId) {
        //if (!StringUtils.isAnyBlank(posUrl,apiUserCode,apiUserKey))
        if (!StringUtils.isAnyBlank(posUrl)) {
            try {
                String requestId = getGUID(false);

                Map<String, Object> map = new HashMap<>();
                map.put("serviceId", "ClearGoodsCache");
                map.put("requestId", requestId);
                map.put("timestamp", "1");
                map.put("version", "v3.0");
                map.put("apiUserCode", apiUserCode);
                String request = "{\"shopId\":\"none\",\"eId\":\"" + eId + "\"}";
                map.put("sign", PosPub.encodeMD5(request + apiUserKey));
                HttpSend.doPost(posUrl, request, map, requestId);
            } catch (Exception e) {
                logger.error("\r\n清除缓存失败:{}", e.getMessage());
            }
        } else {
            logger.error("\r\n清除缓存失败:posUrl({}),apiUserCode({}),apiUserKey({})", posUrl, apiUserCode, apiUserKey);
        }
    }

    /**
     * 标准零售价格缓存维护 goodsPrice下面商品+pluBarcode- 缓存
     *
     * @param posUrl
     * @param apiUserCode
     * @param apiUserKey
     * @param pluList
     */
    public static void POS_GoodsPriceRedisUpdate_Cache(String posUrl, String apiUserCode, String apiUserKey, List<Plu_POS_GoodsPriceRedisUpdate> pluList) {
        if (!StringUtils.isAnyBlank(posUrl)) {
            try {
                String requestId = getGUID(false);

                Map<String, Object> map = new HashMap<>();
                map.put("serviceId", "POS_GoodsPriceRedisUpdate");
                map.put("requestId", requestId);
                map.put("timestamp", "1");
                map.put("version", "v3.0");
                map.put("apiUserCode", apiUserCode);

                //
                JSONObject jsonBody = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                for (Plu_POS_GoodsPriceRedisUpdate plu : pluList) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("pluNo", plu.getPluNo());
                    jsonArray.put(jsonObject);
                }
                jsonBody.put("pluList", jsonArray);

                String request = jsonBody.toString();
                map.put("sign", PosPub.encodeMD5(request + apiUserKey));
                HttpSend.doPost(posUrl, request, map, requestId);
            } catch (Exception e) {
                logger.error("\r\n清除缓存失败:{}", e.getMessage());
            }
        } else {
            logger.error("\r\n清除缓存失败:posUrl({}),apiUserCode({}),apiUserKey({})", posUrl, apiUserCode, apiUserKey);
        }

    }

    /**
     * 门店可用商品模板维护shopGoodsTemplate 缓存，不用频繁调用
     *
     * @param posUrl
     * @param apiUserCode
     * @param apiUserKey
     * @param templates
     */
    public static void POS_ShopGoodsTemplateRedisUpdate_Cache(String posUrl, String apiUserCode, String apiUserKey, List<Template_POS_ShopGoodsTemplateRedisUpdate> templates) {
        if (!StringUtils.isAnyBlank(posUrl)) {
            try {
                String requestId = getGUID(false);

                Map<String, Object> map = new HashMap<>();
                map.put("serviceId", "POS_ShopGoodsTemplateRedisUpdate");
                map.put("requestId", requestId);
                map.put("timestamp", "1");
                map.put("version", "v3.0");
                map.put("apiUserCode", apiUserCode);

                //
                JSONObject jsonBody = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                for (Template_POS_ShopGoodsTemplateRedisUpdate template : templates) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("templateId", template.getTemplateId());
                    jsonArray.put(jsonObject);
                }
                jsonBody.put("templateList", jsonArray);

                String request = jsonBody.toString();
                map.put("sign", PosPub.encodeMD5(request + apiUserKey));
                HttpSend.doPost(posUrl, request, map, requestId);
            } catch (Exception e) {
                logger.error("\r\n清除缓存失败:{}", e.getMessage());
            }
        } else {
            logger.error("\r\n清除缓存失败:posUrl({}),apiUserCode({}),apiUserKey({})", posUrl, apiUserCode, apiUserKey);
        }

    }

    /**
     * 商品模板下属商品缓存维护 goodsTemplateDetail+pluBarcode-缓存
     *
     * @param posUrl
     * @param apiUserCode
     * @param apiUserKey
     * @param template
     */
    public static void POS_GoodsTemplateDetailRedisUpdate_Cache(String posUrl, String apiUserCode, String apiUserKey, Template_POS_GoodsTemplateDetailRedisUpdate template) {
        if (!StringUtils.isAnyBlank(posUrl)) {
            try {
                String requestId = getGUID(false);

                Map<String, Object> map = new HashMap<>();
                map.put("serviceId", "POS_GoodsTemplateDetailRedisUpdate");
                map.put("requestId", requestId);
                map.put("timestamp", "1");
                map.put("version", "v3.0");
                map.put("apiUserCode", apiUserCode);

                //
                JSONObject jsonBody = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                for (Template_POS_GoodsTemplateDetailRedisUpdate.plu plu : template.getPluList()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("pluNo", plu.getPluNo());
                    jsonArray.put(jsonObject);
                }
                jsonBody.put("templateId", template.getTemplateId());
                jsonBody.put("pluList", jsonArray);

                String request = jsonBody.toString();
                map.put("sign", PosPub.encodeMD5(request + apiUserKey));
                HttpSend.doPost(posUrl, request, map, requestId);
            } catch (Exception e) {
                logger.error("\r\n清除缓存失败:{}", e.getMessage());
            }
        } else {
            logger.error("\r\n清除缓存失败:posUrl({}),apiUserCode({}),apiUserKey({})", posUrl, apiUserCode, apiUserKey);
        }

    }


    /**
     * 商品渠道零售价格缓存维护 goodsChannelPrice
     *
     * @param posUrl
     * @param apiUserCode
     * @param apiUserKey
     * @param templates
     */
    public static void POS_GoodsChannelPriceRedisUpdate_Cache(String posUrl, String apiUserCode, String apiUserKey, List<Template_POS_GoodsChannelPriceRedisUpdate> templates) {
        if (!StringUtils.isAnyBlank(posUrl)) {
            try {
                String requestId = getGUID(false);

                Map<String, Object> map = new HashMap<>();
                map.put("serviceId", "POS_GoodsChannelPriceRedisUpdate");
                map.put("requestId", requestId);
                map.put("timestamp", "1");
                map.put("version", "v3.0");
                map.put("apiUserCode", apiUserCode);

                //
                JSONObject jsonBody = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                for (Template_POS_GoodsChannelPriceRedisUpdate template : templates) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("templateId", template.getTemplateId());
                    if (template.getPluList() != null && template.getPluList().size() > 0) {
                        JSONArray jsonArray_plu = new JSONArray();
                        for (Template_POS_GoodsChannelPriceRedisUpdate.plu plu : template.getPluList()) {
                            JSONObject jsonObject_plu = new JSONObject();
                            jsonObject_plu.put("item", plu.getItem());
                            jsonObject_plu.put("pluNo", plu.getPluNo());
                            jsonArray_plu.put(jsonObject_plu);
                        }
                        jsonObject.put("pluList", jsonArray_plu);
                    }
                    jsonArray.put(jsonObject);
                }
                jsonBody.put("templateList", jsonArray);

                String request = jsonBody.toString();
                map.put("sign", PosPub.encodeMD5(request + apiUserKey));
                HttpSend.doPost(posUrl, request, map, requestId);
            } catch (Exception e) {
                logger.error("\r\n清除缓存失败:{}", e.getMessage());
            }
        } else {
            logger.error("\r\n清除缓存失败:posUrl({}),apiUserCode({}),apiUserKey({})", posUrl, apiUserCode, apiUserKey);
        }

    }


    public static void repairStock_detail(DsmDAO dao, String eId, String shopId, String beginDay, String endDay) throws Exception {
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");
        //销售、销退 20，21
        sqlbuf.append("SELECT  A.EID,A.SHOPID, CASE WHEN A.TYPE=0 THEN '20' ELSE '21' END as BILLTYPE,A.SALENO AS BILLNO ,B.ITEM,CASE WHEN A.TYPE=0 THEN '-1' ELSE '1' END  AS STOCKTYPE,A.BDATE,B.PLUNO,B.FEATURENO,B.WAREHOUSE,"
                + "cast('' as nvarchar2(40)) AS BATCH_NO,B.UNIT AS PUNIT,B.QTY AS PQTY,B.BASEUNIT,NVL(B.UNITRATIO,1)*B.QTY AS  BASEQTY,NVL(B.UNITRATIO,1) AS UNIT_RATIO,B.PRICE,B.AMT,0 AS DISTRIPRICE,0  AS DISTRIAMT,"
                + "A.BDATE AS ACCOUNT_DATE,cast('' as nvarchar2(20))  AS PROD_DATE,A.BDATE,A.BSNO AS RESON,B.MEMO AS MEMO,A.OPNO AS ACCOUNTBY "
                + "FROM DCP_SALE A INNER JOIN DCP_SALE_DETAIL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.SALENO=B.SALENO "
                + "WHERE A.EID='" + eId + "' AND A.SHOPID='" + shopId + "' AND A.BDATE BETWEEN '" + beginDay + "' AND '" + endDay + "' "
                + "AND NVL(A.ISBUFFER,'N')='N'  AND NVL(B.PACKAGEMASTER,'N')='N' AND NVL(B.DISHESSTATUS,'0')<>'4' "
        );
        sqlbuf.append("  UNION ALL ");
        //01-配送收货 02-调拨收货  14-其它入库   19-移仓收货
        sqlbuf.append("SELECT  A.EID,A.SHOPID,CASE WHEN A.DOC_TYPE='0' THEN '01' ELSE  CASE WHEN A.DOC_TYPE='1' THEN '02' ELSE CASE WHEN A.DOC_TYPE='3' THEN '14' ELSE CASE WHEN A.DOC_TYPE='4' THEN '19' END END  END END  as BILLTYPE,A.STOCKINNO AS BILLNO,B.ITEM,'1' AS STOCKTYPE,A.BDATE,B.PLUNO,B.FEATURENO,B.WAREHOUSE,"
                + "B.BATCH_NO,B.PUNIT,B.PQTY,B.BASEUNIT,B.BASEQTY,B.UNIT_RATIO,B.PRICE,B.AMT,B.DISTRIPRICE,B.DISTRIAMT,"
                + "A.ACCOUNT_DATE,B.PROD_DATE,A.BDATE,A.BSNO AS RESON,B.PLU_MEMO AS MEMO,A.ACCOUNTBY "
                + "FROM DCP_STOCKIN A INNER JOIN DCP_STOCKIN_DETAIL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.STOCKINNO=B.STOCKINNO "
                + "WHERE A.EID='" + eId + "' AND A.SHOPID='" + shopId + "' AND A.ACCOUNT_DATE BETWEEN '" + beginDay + "' AND '" + endDay + "' "
                + "AND A.STATUS=2  "
        );
        sqlbuf.append("  UNION ALL ");
        //05-自采入库
        sqlbuf.append("SELECT  A.EID,A.SHOPID,'05' as BILLTYPE,A.SSTOCKINNO AS BILLNO,B.ITEM,'1' AS STOCKTYPE,CAST(A.BDATE AS NVARCHAR2(20)) AS BDATE,B.PLUNO,B.FEATURENO,B.WAREHOUSE,"
                + "B.BATCH_NO,B.PUNIT,B.PQTY,B.BASEUNIT,B.BASEQTY,B.UNIT_RATIO,B.PRICE,B.AMT,B.DISTRIPRICE,B.DISTRIAMT,"
                + "A.ACCOUNT_DATE,B.PROD_DATE,CAST(A.BDATE AS NVARCHAR2(20)) AS BDATE,cast('' as nvarchar2(20)) AS RESON,B.PLU_MEMO AS MEMO,A.ACCOUNTBY "
                + "FROM DCP_SSTOCKIN A INNER JOIN DCP_SSTOCKIN_DETAIL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.SSTOCKINNO=B.SSTOCKINNO "
                + "WHERE A.EID='" + eId + "' AND A.SHOPID='" + shopId + "' AND A.ACCOUNT_DATE BETWEEN '" + beginDay + "' AND '" + endDay + "' "
                + "AND A.STATUS=2   "
        );
        sqlbuf.append("  UNION ALL ");
        //03-退货出库 04-调拨出库15-其它出库 18-移仓出库
        sqlbuf.append("SELECT  A.EID,A.SHOPID,CASE WHEN A.DOC_TYPE='0' OR A.DOC_TYPE='2' THEN '03' ELSE  CASE WHEN A.DOC_TYPE='1' THEN '04' ELSE CASE WHEN A.DOC_TYPE='3' THEN '15' ELSE CASE WHEN A.DOC_TYPE='4' THEN '18' END END  END END  as BILLTYPE,A.STOCKOUTNO AS BILLNO,B.ITEM,'-1' AS STOCKTYPE,cast(to_char(A.BDATE) as nvarchar2(20)) as BDATE,B.PLUNO,B.FEATURENO,B.WAREHOUSE,"
                + "B.BATCH_NO,B.PUNIT,B.PQTY,B.BASEUNIT,B.BASEQTY,B.UNIT_RATIO,B.PRICE,B.AMT,B.DISTRIPRICE,B.DISTRIAMT,"
                + "A.ACCOUNT_DATE,B.PROD_DATE,cast(to_char(A.BDATE) as nvarchar2(20)) as BDATE,A.BSNO AS RESON,B.PLU_MEMO AS MEMO,A.ACCOUNTBY "
                + "FROM DCP_STOCKOUT A INNER JOIN DCP_STOCKOUT_DETAIL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.STOCKOUTNO=B.STOCKOUTNO "
                + "WHERE A.EID='" + eId + "' AND A.SHOPID='" + shopId + "' AND A.ACCOUNT_DATE BETWEEN '" + beginDay + "' AND '" + endDay + "' "
                + "AND (A.STATUS=2 or A.STATUS=3 or A.STATUS=5) "
        );
        sqlbuf.append("  UNION ALL ");
        //07-报损出库
        sqlbuf.append("SELECT  A.EID,A.SHOPID,'07' as BILLTYPE,A.LSTOCKOUTNO AS BILLNO,B.ITEM,'-1' AS STOCKTYPE,cast(to_char(A.BDATE) as nvarchar2(20)) as BDATE,B.PLUNO,B.FEATURENO,B.WAREHOUSE,"
                + "B.BATCH_NO,B.PUNIT,B.PQTY,B.BASEUNIT,B.BASEQTY,B.UNIT_RATIO,B.PRICE,B.AMT,B.DISTRIPRICE,B.DISTRIAMT,"
                + "A.ACCOUNT_DATE,B.PROD_DATE,cast(to_char(A.BDATE) as nvarchar2(20)) as BDATE,B.BSNO AS RESON,A.MEMO AS MEMO,A.ACCOUNTBY "
                + "FROM DCP_LSTOCKOUT A INNER JOIN DCP_LSTOCKOUT_DETAIL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.LSTOCKOUTNO=B.LSTOCKOUTNO "
                + "WHERE A.EID='" + eId + "' AND A.SHOPID='" + shopId + "' AND A.ACCOUNT_DATE BETWEEN '" + beginDay + "' AND '" + endDay + "' "
                + "AND (A.STATUS=2 or A.STATUS=3 or A.STATUS=5) "
        );
        sqlbuf.append("  UNION ALL ");
        //06-自采退货
        sqlbuf.append("SELECT  A.EID,A.SHOPID,'06' as BILLTYPE,A.SSTOCKOUTNO AS BILLNO,B.ITEM,'-1' AS STOCKTYPE,cast(to_char(A.BDATE) as nvarchar2(20)) as BDATE,B.PLUNO,B.FEATURENO,B.WAREHOUSE,"
                + "B.BATCH_NO,B.PUNIT,B.PQTY,B.BASEUNIT,B.BASEQTY,B.UNIT_RATIO,B.PRICE,B.AMT,B.DISTRIPRICE,B.DISTRIAMT,"
                + "A.ACCOUNT_DATE,B.PROD_DATE,cast(to_char(A.BDATE) as nvarchar2(20)) as BDATE,CAST('' AS NVARCHAR2(20)) AS RESON,CAST('' AS NVARCHAR2(20)) AS MEMO,A.ACCOUNTBY "
                + "FROM DCP_SSTOCKOUT A INNER JOIN DCP_SSTOCKOUT_DETAIL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.SSTOCKOUTNO=B.SSTOCKOUTNO "
                + "WHERE A.EID='" + eId + "' AND A.SHOPID='" + shopId + "' AND A.ACCOUNT_DATE BETWEEN '" + beginDay + "' AND '" + endDay + "' "
                + "AND A.STATUS=2   "
        );
        sqlbuf.append("  UNION ALL ");
        //08-完工入库  0-组合入库 32-拆解出库 34-报损差异调整  35-成品转换合并  37-转换拆解出库
        sqlbuf.append("SELECT  A.EID,A.SHOPID,CASE WHEN A.DOC_TYPE='0' THEN '08' ELSE  CASE WHEN A.DOC_TYPE='1' THEN '30' ELSE CASE WHEN A.DOC_TYPE='2' THEN '32' ELSE CASE WHEN A.DOC_TYPE='3' THEN '35' ELSE CASE WHEN A.DOC_TYPE='4' THEN '37' END END  END END  END as BILLTYPE,"
                + "A.PSTOCKINNO AS BILLNO,B.ITEM,CASE WHEN A.DOC_TYPE='0' OR A.DOC_TYPE='1' OR A.DOC_TYPE='3'THEN '1'   ELSE '-1'    END  AS STOCKTYPE,A.BDATE,B.PLUNO,B.FEATURENO,B.WAREHOUSE,"
                + "B.BATCH_NO,B.PUNIT,B.PQTY,B.BASEUNIT,B.BASEQTY,B.UNIT_RATIO,B.PRICE,B.AMT,B.DISTRIPRICE,B.DISTRIAMT,"
                + "A.ACCOUNT_DATE,B.PROD_DATE,A.BDATE,CAST('' AS NVARCHAR2(20)) AS RESON,CAST('' AS NVARCHAR2(20)) AS MEMO,A.ACCOUNTBY "
                + "FROM DCP_PSTOCKIN A INNER JOIN DCP_PSTOCKIN_DETAIL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.PSTOCKINNO=B.PSTOCKINNO "
                + "WHERE A.EID='" + eId + "' AND A.SHOPID='" + shopId + "' AND A.ACCOUNT_DATE BETWEEN '" + beginDay + "' AND '" + endDay + "' "
                + "AND A.STATUS=2   "
        );
        sqlbuf.append("  UNION ALL ");
        //11-加工原料倒扣 31-组合原料出库  33-拆解原料入库 36-原料转换合并  38-原料转换拆解入库
        sqlbuf.append("SELECT  A.EID,A.SHOPID,CASE WHEN A.DOC_TYPE='0' THEN '11' ELSE  CASE WHEN A.DOC_TYPE='1' THEN '31' ELSE CASE WHEN A.DOC_TYPE='2' THEN '33' ELSE CASE WHEN A.DOC_TYPE='3' THEN '36' ELSE CASE WHEN A.DOC_TYPE='4' THEN '38' END END  END END  END as BILLTYPE,"
                + "A.PSTOCKINNO AS BILLNO,B.ITEM,CASE WHEN A.DOC_TYPE='0' OR A.DOC_TYPE='1' OR A.DOC_TYPE='3' THEN '-1'   ELSE '1'    END  AS STOCKTYPE,A.BDATE,B.PLUNO,B.FEATURENO,B.WAREHOUSE,"
                + "B.BATCH_NO,B.PUNIT,B.PQTY,B.BASEUNIT,B.BASEQTY,B.UNIT_RATIO,B.PRICE,B.AMT,B.DISTRIPRICE,B.DISTRIAMT,"
                + "A.ACCOUNT_DATE,B.PROD_DATE,A.BDATE,CAST('' AS NVARCHAR2(20)) AS RESON,CAST('' AS NVARCHAR2(20)) AS MEMO,A.ACCOUNTBY "
                + "FROM DCP_PSTOCKIN A INNER JOIN DCP_PSTOCKIN_MATERIAL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.PSTOCKINNO=B.PSTOCKINNO "
                + "WHERE A.EID='" + eId + "' AND A.SHOPID='" + shopId + "' AND A.ACCOUNT_DATE BETWEEN '" + beginDay + "' AND '" + endDay + "' "
                + "AND A.STATUS=2  AND NVL(B.ISBUCKLE,'Y')='Y'"
        );
        sqlbuf.append("  UNION ALL ");
        //00-期初  10-盘点盈亏调整  09-配送差异调整 16-调拨收货差异 17-退货差异调整 34-报损差异调整
        sqlbuf.append("SELECT  A.EID,A.SHOPID,CASE WHEN A.DOC_TYPE='1' THEN '00' ELSE  CASE WHEN A.DOC_TYPE='2' THEN '10' ELSE CASE WHEN A.DOC_TYPE='3' THEN '09' ELSE CASE WHEN A.DOC_TYPE='4' THEN '16' ELSE CASE WHEN A.DOC_TYPE='6' THEN '17' ELSE CASE WHEN A.DOC_TYPE='7' THEN '34' END  END END END  END END  as BILLTYPE,A.ADJUSTNO AS BILLNO,B.ITEM,"
                + "'1' AS STOCKTYPE,A.BDATE,B.PLUNO,B.FEATURENO,B.WAREHOUSE,"
                + "B.BATCH_NO,B.PUNIT,B.PQTY,B.BASEUNIT,B.BASEQTY,B.UNIT_RATIO,B.PRICE,B.AMT,B.DISTRIPRICE,B.DISTRIAMT,"
                + "A.ACCOUNT_DATE,B.PROD_DATE,A.BDATE,CAST('' AS NVARCHAR2(20))AS RESON,MEMO AS MEMO,A.ACCOUNTBY "
                + "FROM DCP_ADJUST A INNER JOIN DCP_ADJUST_DETAIL B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.ADJUSTNO=B.ADJUSTNO "
                + "WHERE A.EID='" + eId + "' AND A.SHOPID='" + shopId + "' AND A.ACCOUNT_DATE BETWEEN '" + beginDay + "' AND '" + endDay + "' "
                + "AND A.STATUS=2   "
        );
        sql = sqlbuf.toString();
        List<Map<String, Object>> list = dao.executeQuerySQL(sql, null);
        List<DataProcessBean> data = new ArrayList<DataProcessBean>();
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> map : list) {
                String procedure = "SP_DCP_StockChange";
                Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                inputParameter.put(1, map.get("EID").toString());                //--企业ID
                inputParameter.put(2, map.get("SHOPID").toString());      //--组织
                inputParameter.put(3, map.get("BILLTYPE").toString());    //--单据类型
                inputParameter.put(4, map.get("BILLNO").toString());     //--单据号
                inputParameter.put(5, map.get("ITEM").toString());        //--单据行号
                inputParameter.put(6, map.get("STOCKTYPE").toString());   //--异动方向 1=加库存 -1=减库存
                inputParameter.put(7, map.get("BDATE").toString());       //--营业日期 yyyy-MM-dd
                inputParameter.put(8, map.get("PLUNO").toString());          //--品号
                inputParameter.put(9, map.get("FEATURENO").toString());      //--特征码
                inputParameter.put(10, map.get("WAREHOUSE").toString());  //--仓库
                inputParameter.put(11, map.get("BATCH_NO").toString());      //--批号
                inputParameter.put(12, map.get("PUNIT").toString());         //--交易单位
                inputParameter.put(13, map.get("PQTY").toString());          //--交易数量
                inputParameter.put(14, map.get("BASEUNIT").toString());      //--基准单位
                inputParameter.put(15, map.get("BASEQTY").toString());       //--基准数量
                inputParameter.put(16, map.get("UNIT_RATIO").toString());    //--换算比例
                inputParameter.put(17, map.get("PRICE").toString());         //--零售价
                inputParameter.put(18, map.get("AMT").toString());           //--零售金额
                inputParameter.put(19, map.get("DISTRIPRICE").toString());   //--进货价
                inputParameter.put(20, map.get("DISTRIAMT").toString());     //--进货金额
                inputParameter.put(21, map.get("ACCOUNT_DATE").toString());//--入账日期 yyyy-MM-dd
                inputParameter.put(22, map.get("PROD_DATE").toString());     //--批号的生产日期 yyyy-MM-dd
                inputParameter.put(23, map.get("BDATE").toString());       //--单据日期
                inputParameter.put(24, map.get("RESON").toString());       //--异动原因
                inputParameter.put(25, map.get("MEMO").toString());        //--异动描述
                inputParameter.put(26, map.get("ACCOUNTBY").toString());   //--操作员
                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                data.add(new DataProcessBean(pdb));
                inputParameter = null;
            }
            StaticInfo.dao.useTransactionProcessData(data);
        }
    }


    /**
     * 詹记项目，单位转换，鼎捷转锐翔
     *
     * @param p_Unit 来源单位
     * @return
     */
    public static String F_UNIT_DIGIWINTORX(String p_Unit) {
        String res_Unit = "";

        switch (p_Unit) {
            case "公斤":
                res_Unit = "kg/公斤";
                break;
            case "毫升":
                res_Unit = "ml/毫升";
                break;
            case "升":
                res_Unit = "L/升";
                break;
            default:
                res_Unit = p_Unit;
                break;
        }
        return res_Unit;
    }


    /**
     * 詹记项目，单位转换，锐翔转鼎捷
     *
     * @param p_Unit 来源单位
     * @return
     */
    public static String F_UNIT_RXTODIGIWIN(String p_Unit) {
        String res_Unit = "";

        switch (p_Unit) {
            case "kg/公斤":
                res_Unit = "公斤";
                break;
            case "ml/毫升":
                res_Unit = "毫升";
                break;
            case "L/升":
                res_Unit = "升";
                break;
            default:
                res_Unit = p_Unit;
                break;
        }
        return res_Unit;
    }

    /**
     * 获取CRM服务地址
     *
     * @param eId 企业编号
     * @throws Exception 异常
     */
    public static String getCRM_INNER_URL(String eId) throws Exception {

        String URL = "";
        try {
            if (!Check.Null(StaticInfo.CRM_INNER_URL)) {
                URL = StaticInfo.CRM_INNER_URL;
            } else {
                String sql = null;
                sql = "select ITEMVALUE from platform_basesettemp t WHERE ITEM = 'CrmUrl' and eid='" + eId + "'";
                List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
                if (getQData != null && getQData.isEmpty() == false) {
                    URL = getQData.get(0).get("ITEMVALUE").toString();
                } else {
                    URL = "";
                }
            }
            return URL;

        } catch (Exception e) {
            return URL;
        } finally {
            URL = null;
        }
    }

    /**
     * 获取CRM服务地址
     *
     * @param eId 企业编号
     * @throws Exception 异常
     */
    public static String getCRM_URL(String eId) throws Exception {

        String URL = "";
        try {
            if (!Check.Null(StaticInfo.CRM_URL)) {
                URL = StaticInfo.CRM_URL;
            } else {
                String sql = null;
                sql = "select ITEMVALUE from platform_basesettemp t WHERE ITEM = 'CrmUrl' and eid='" + eId + "'";
                List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
                if (getQData != null && getQData.isEmpty() == false) {
                    URL = getQData.get(0).get("ITEMVALUE").toString();
                } else {
                    URL = "";
                }
            }
            return URL;

        } catch (Exception e) {
            return URL;
        } finally {
            URL = null;
        }
    }

    /**
     * 获取ISV服务回调地址
     */
    public static String getISV_URL() {
        String isvUrl = "http://retaildev.digiwin.com.cn/dcpService_3.0/DCP/services/invoke";
        return isvUrl;
    }

    /**
     * 获取CRM服务地址
     *
     * @param eId 企业编号
     * @throws Exception 异常
     */
    public static String getPAY_INNER_URL(String eId) throws Exception {

        String URL = "";
        try {
            if (!Check.Null(StaticInfo.PAY_INNER_URL)) {
                URL = StaticInfo.PAY_INNER_URL;
            } else {
                String sql = null;
                sql = "select ITEMVALUE from platform_basesettemp t WHERE ITEM = 'Mobile_Url' and eid='" + eId + "'";
                List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
                if (getQData != null && getQData.isEmpty() == false) {
                    URL = getQData.get(0).get("ITEMVALUE").toString();
                } else {
                    URL = "";
                }
            }
            return URL;

        } catch (Exception e) {
            return URL;
        } finally {
            URL = null;
        }
    }

    /**
     * 获取CRM服务地址
     *
     * @param eId 企业编号
     * @throws Exception 异常
     */
    public static String getPAY_URL(String eId) throws Exception {

        String URL = "";
        try {
            if (!Check.Null(StaticInfo.PAY_URL)) {
                URL = StaticInfo.PAY_URL;
            } else {
                String sql = null;
                sql = "select ITEMVALUE from platform_basesettemp t WHERE ITEM = 'Mobile_Url' and eid='" + eId + "'";
                List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
                if (getQData != null && getQData.isEmpty() == false) {
                    URL = getQData.get(0).get("ITEMVALUE").toString();
                } else {
                    URL = "";
                }
            }
            return URL;

        } catch (Exception e) {
            return URL;
        } finally {
            URL = null;
        }
    }

    /**
     * 获取MES服务地址
     *
     * @param eId 企业编号
     * @throws Exception 异常
     */
    public static String getMES_INNER_URL(String eId) throws Exception {

        String URL = "";
        try {
            if (!Check.Null(StaticInfo.MES_INNER_URL)) {
                URL = StaticInfo.MES_INNER_URL;
            } else {
                String sql = null;
                sql = "select ITEMVALUE from platform_basesettemp t WHERE ITEM = 'MesUrl' and eid='" + eId + "'";
                List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
                if (getQData != null && getQData.isEmpty() == false) {
                    URL = getQData.get(0).get("ITEMVALUE").toString();
                } else {
                    URL = "";
                }
            }
            return URL;

        } catch (Exception e) {
            return URL;
        } finally {
            URL = null;
        }
    }

    /**
     * 获取MES服务地址
     *
     * @param eId 企业编号
     * @throws Exception 异常
     */
    public static String getMES_URL(String eId) throws Exception {

        String URL = "";
        try {
            if (!Check.Null(StaticInfo.MES_URL)) {
                URL = StaticInfo.MES_URL;
            } else {
                String sql = null;
                sql = "select ITEMVALUE from platform_basesettemp t WHERE ITEM = 'MesUrl' and eid='" + eId + "'";
                List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
                if (getQData != null && getQData.isEmpty() == false) {
                    URL = getQData.get(0).get("ITEMVALUE").toString();
                } else {
                    URL = "";
                }
            }
            return URL;

        } catch (Exception e) {
            return URL;
        } finally {
            URL = null;
        }
    }

    /**
     * 获取POS服务地址
     *
     * @param eId 企业编号
     * @throws Exception 异常
     */
    public static String getPOS_INNER_URL(String eId) throws Exception {

        String URL = "";
        try {
            if (!Check.Null(StaticInfo.POS_INNER_URL)) {
                URL = StaticInfo.POS_INNER_URL;
            } else {
                String sql = null;
                sql = "select ITEMVALUE from platform_basesettemp t WHERE ITEM = 'PosUrl' and eid='" + eId + "'";
                List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
                if (getQData != null && getQData.isEmpty() == false) {
                    URL = getQData.get(0).get("ITEMVALUE").toString();
                } else {
                    URL = "";
                }
            }
            return URL;

        } catch (Exception e) {
            return URL;
        } finally {
            URL = null;
        }
    }

    /**
     * 获取POS服务地址
     *
     * @param eId 企业编号
     * @throws Exception 异常
     */
    public static String getPOS_URL(String eId) throws Exception {

        String URL = "";
        try {
            if (!Check.Null(StaticInfo.POS_URL)) {
                URL = StaticInfo.POS_URL;
            } else {
                String sql = null;
                sql = "select ITEMVALUE from platform_basesettemp t WHERE ITEM = 'PosUrl' and eid='" + eId + "'";
                List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
                if (getQData != null && getQData.isEmpty() == false) {
                    URL = getQData.get(0).get("ITEMVALUE").toString();
                } else {
                    URL = "";
                }
            }
            return URL;

        } catch (Exception e) {
            return URL;
        } finally {
            URL = null;
        }
    }

    /**
     * 获取PROM服务地址
     *
     * @param eId 企业编号
     * @throws Exception 异常
     */
    public static String getPROM_INNER_URL(String eId) throws Exception {

        String URL = "";
        try {
            if (!Check.Null(StaticInfo.PROM_INNER_URL)) {
                URL = StaticInfo.PROM_INNER_URL;
            } else {
                String sql = null;
                sql = "select ITEMVALUE from platform_basesettemp t WHERE ITEM = 'PromUrl' and eid='" + eId + "'";
                List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
                if (getQData != null && getQData.isEmpty() == false) {
                    URL = getQData.get(0).get("ITEMVALUE").toString();
                } else {
                    URL = "";
                }
            }
            return URL;

        } catch (Exception e) {
            return URL;
        } finally {
            URL = null;
        }
    }

    /**
     * 获取PROM服务地址
     *
     * @param eId 企业编号
     * @throws Exception 异常
     */
    public static String getPROM_URL(String eId) throws Exception {

        String URL = "";
        try {
            if (!Check.Null(StaticInfo.PROM_URL)) {
                URL = StaticInfo.PROM_URL;
            } else {
                String sql = null;
                sql = "select ITEMVALUE from platform_basesettemp t WHERE ITEM = 'PromUrl' and eid='" + eId + "'";
                List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
                if (getQData != null && getQData.isEmpty() == false) {
                    URL = getQData.get(0).get("ITEMVALUE").toString();
                } else {
                    URL = "";
                }
            }
            return URL;

        } catch (Exception e) {
            return URL;
        } finally {
            URL = null;
        }
    }

    /**
     * 获取PAY服务地址
     *
     * @param eId 企业编号
     * @throws Exception 异常
     */
    public static String getDCP_INNER_URL(String eId) throws Exception {

        String URL = "";
        try {
            if (!Check.Null(StaticInfo.DCP_INNER_URL)) {
                URL = StaticInfo.DCP_INNER_URL;
            } else {
                String sql = null;
                sql = "select ITEMVALUE from platform_basesettemp t WHERE ITEM = 'PlatformCentreURL' and eid='" + eId + "'";
                List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
                if (getQData != null && getQData.isEmpty() == false) {
                    URL = getQData.get(0).get("ITEMVALUE").toString();
                } else {
                    URL = "";
                }
            }
            return URL;

        } catch (Exception e) {
            return URL;
        } finally {
            URL = null;
        }
    }

    /**
     * 获取PAY服务地址
     *
     * @param eId 企业编号
     * @throws Exception 异常
     */
    public static String getDCP_URL(String eId) throws Exception {

        String URL = "";
        try {
            if (!Check.Null(StaticInfo.DCP_URL)) {
                URL = StaticInfo.DCP_URL;
            } else {
                String sql = null;
                sql = "select ITEMVALUE from platform_basesettemp t WHERE ITEM = 'PlatformCentreURL' and eid='" + eId + "'";
                List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
                if (getQData != null && getQData.isEmpty() == false) {
                    URL = getQData.get(0).get("ITEMVALUE").toString();
                } else {
                    URL = "";
                }
            }
            return URL;

        } catch (Exception e) {
            return URL;
        } finally {
            URL = null;
        }
    }

    /**
     * 获取PAY服务地址
     *
     * @param eId 企业编号
     * @throws Exception 异常
     */
    public static String getPicture_URL(String eId) throws Exception {

        String URL = "";
        try {
            if (!Check.Null(StaticInfo.PICTURE_URL)) {
                URL = StaticInfo.PICTURE_URL;
            } else {
                String sql = null;
                sql = "select ITEMVALUE from platform_basesettemp t WHERE ITEM = 'DOMAINNAME' and eid='" + eId + "'";
                List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
                if (getQData != null && getQData.isEmpty() == false) {
                    URL = getQData.get(0).get("ITEMVALUE").toString();
                } else {
                    URL = "";
                }
            }
            return URL;

        } catch (Exception e) {
            return URL;
        } finally {
            URL = null;
        }
    }

    public static String getPicture_INNER_URL(String eId) throws Exception {

        String URL = "";
        try {
            if (!Check.Null(StaticInfo.PICTURE_INNER_URL)) {
                URL = StaticInfo.PICTURE_INNER_URL;
            } else {
                String sql = null;
                sql = "select ITEMVALUE from platform_basesettemp t WHERE ITEM = 'DOMAINNAME' and eid='" + eId + "'";
                List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);
                if (getQData != null && getQData.isEmpty() == false) {
                    URL = getQData.get(0).get("ITEMVALUE").toString();
                } else {
                    URL = "";
                }
            }
            return URL;

        } catch (Exception e) {
            return URL;
        } finally {
            URL = null;
        }
    }

    /**
     * 替代料
     *
     * @param dao
     * @param eId
     * @param companyId
     * @param shopId
     * @param pstockinMaterial     完工入庫原料明細
     * @param keyPluNo             成品编号
     * @param keyMaterialPunit     原料录入单位
     * @param keyMaterialPluNo     原料编号
     * @param keyMaterialWunit
     * @param keyMaterialWarehouse 原料库存单位
     * @return true, false
     */
    public static boolean MaterialReplace(DsmDAO dao, String eId, String companyId, String shopId, List<Map<String, Object>> pstockinMaterial,
                                          String keyPluNo, String keyMaterialPunit, String keyMaterialPluNo, String keyMaterialWunit, String keyMaterialWarehouse) {
        try {
            List<Map<String, Object>> pstockinMaterialnew = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> stocklist = new ArrayList<Map<String, Object>>();
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
            condition.put(keyMaterialPluNo, true);
            String amtLength = PosPub.getPARA_SMS(StaticInfo.dao, eId, shopId, "amtLength");
            if (Check.Null(amtLength) || !PosPub.isNumeric(amtLength)) {
                amtLength = "2";
            }
            int amtLengthInt = Integer.valueOf(amtLength);

            String distriAmtLength = PosPub.getPARA_SMS(StaticInfo.dao, eId, shopId, "distriAmtLength");
            if (Check.Null(distriAmtLength) || !PosPub.isNumeric(distriAmtLength)) {
                distriAmtLength = "2";
            }
            int distriAmtLengthInt = Integer.valueOf(distriAmtLength);

            stocklist = PosPub.stockList(dao, eId, shopId, stocklist, keyMaterialPluNo, "BASEQTY");

            if (pstockinMaterial != null && pstockinMaterial.size() > 0) {
                Map<String, Object> condiV = new HashMap<String, Object>();
                for (Map<String, Object> oneData : pstockinMaterial) //完工入库原料List
                {
                    String pluNo = oneData.get(keyPluNo).toString();
                    String materialPluNo = oneData.get(keyMaterialPluNo).toString();
                    String materialPunit = oneData.get(keyMaterialPunit).toString();
                    String pqty = oneData.get("PQTY").toString();
                    String materialUnitRatio = oneData.get("UNIT_RATIO").toString();
                    String materialWunit = oneData.get(keyMaterialWunit).toString();
                    String wqty = oneData.get("BASEQTY").toString();
                    String mitem = oneData.get("MITEM").toString();
                    String materialWarehouse = oneData.get(keyMaterialWarehouse).toString();
                    String bdate = oneData.get("BDATE").toString();
                    String otype = oneData.get("OTYPE").toString();
                    String ofno = oneData.get("OFNO").toString();
                    String memo = oneData.get("MEMO").toString();
                    String oitem = oneData.get("OITEM").toString();
                    String load_doctype = oneData.get("LOAD_DOCTYPE").toString();
                    String load_docno = oneData.get("LOAD_DOCNO").toString();
                    String bsno = oneData.get("BSNO").toString();
                    String scrap_qty = oneData.get("SCRAP_QTY").toString();
                    String batch_no = oneData.get("BATCH_NO").toString();
                    String prod_date = oneData.get("PROD_DATE").toString();
                    String isbuckle = oneData.get("ISBUCKLE").toString();
                    String finalProdBaseQty = oneData.get("FINALPRODBASEQTY").toString();
                    String rawMaterialBaseQty = oneData.get("RAWMATERIALBASEQTY").toString();
                    String replaceRawMaterialBaseQty = rawMaterialBaseQty;
					
					
					/* 20230524 BY tianhq
					String  base_qty     = oneData.get("BASE_QTY").toString();
					String  material_base_qty = oneData.get("MATERIAL_BASE_QTY").toString();
					 */
					/*
	    			float marerialAvailableQty=0;//原料库存可用量
	    			float marerialLackQty=0;     //原料缺少量
	    			float marerialDemandQty=0;   //原料需求用量
	    			float marerialUseQty=0;      //原料使用量
	    			float replaceUseQty=0;       //替代料使用量
	    			float replaceAvailableQty=0; //替代料库存可用量
					 */
                    //修改精度备注
                    BigDecimal marerialAvailableQty = new BigDecimal("0");//原料库存可用量
                    BigDecimal marerialLackQty = new BigDecimal("0");     //原料缺少量
                    BigDecimal marerialDemandQty = new BigDecimal("0");   //原料需求用量
                    BigDecimal marerialUseQty = new BigDecimal("0");     //原料使用量
                    BigDecimal replaceUseQty = new BigDecimal("0");      //替代料使用量
                    BigDecimal replaceAvailableQty = new BigDecimal("0"); //替代料库存可用量
                    BigDecimal marerialUnitRatio = new BigDecimal(materialUnitRatio);
//					if(BigDecimalUtils.bigger(Float.parseFloat(pqty), 0))
//					{
//						marerialUnitRatio=BigDecimal.valueOf(Float.parseFloat(wqty)).
//							divide(BigDecimal.valueOf(Float.parseFloat(pqty))).
//							setScale(6,BigDecimal.ROUND_HALF_UP);
//					}

                    condiV.put("PLUNO", materialPluNo);
                    condiV.put("WAREHOUSE", materialWarehouse);
                    List<Map<String, Object>> getQStockPluno = MapDistinct.getWhereMap(stocklist, condiV, false);//查找库存
                    condiV.clear();
                    if (getQStockPluno != null && getQStockPluno.size() > 0) {
                        //原料库存可用量
                        marerialAvailableQty = BigDecimal.valueOf(Float.parseFloat(getQStockPluno.get(0).get("QTY").toString())).setScale(6, BigDecimal.ROUND_HALF_UP);//库存可用量
                    } else//无库存
                    {
                        marerialAvailableQty = BigDecimal.valueOf(0).setScale(6, BigDecimal.ROUND_HALF_UP);
                    }
                    //原料需求用量
                    marerialDemandQty = BigDecimal.valueOf(Float.parseFloat(wqty)).setScale(6, BigDecimal.ROUND_HALF_UP);//需求用量
                    //原料库存可用量>0 并且大于原料需求用量
                    if (marerialAvailableQty.compareTo(BigDecimal.ZERO) > 0 && marerialAvailableQty.compareTo(marerialDemandQty) >= 0) {
                        //原料使用量
                        marerialUseQty = marerialDemandQty;
                    } else {
                        if (marerialAvailableQty.compareTo(BigDecimal.ZERO) > 0 && marerialAvailableQty.compareTo(marerialDemandQty) < 0) {    //原料缺少量
                            marerialLackQty = marerialDemandQty.subtract(marerialAvailableQty).setScale(6, BigDecimal.ROUND_HALF_UP);//尚缺量
                            //原料使用量
                            marerialUseQty = marerialAvailableQty;
                        } else {
                            //原料缺少量
                            marerialLackQty = marerialDemandQty;
                        }
                    }
                    //原料缺少量>0  并且是扣料件才找替代上商品
                    if (marerialLackQty.compareTo(BigDecimal.ZERO) > 0 && isbuckle.equals("Y")) {
                        //寻找可替代商品
                        List<Map<String, Object>> materialList = PosPub.getMaterialReplace(dao, eId, shopId, pluNo, materialPluNo);
                        if (materialList != null && materialList.size() > 0)//可以替代
                        {
                            for (Map<String, Object> oneData1 : materialList) {
                                String ReplacePluno = oneData1.get("REPLACE_PLUNO").toString();
                                String ReplacePunit = oneData1.get("REPLACE_UNIT").toString();
                                //String baseQty  =oneData1.get("QTY").toString();//原件底数baseQty
                                String replaceCompQty = oneData1.get("REPLACE_QTY").toString();
                                String materialQty = oneData1.get("MATERIAL_QTY").toString();
                                replaceRawMaterialBaseQty = String.valueOf(Double.valueOf(replaceCompQty) / Double.valueOf(materialQty) * Double.valueOf(rawMaterialBaseQty));
//								BigDecimal baseWQty= new BigDecimal("0");
//								BigDecimal replaceCompWQty= new BigDecimal("0");
                                String replaceWunit = oneData1.get("REPLACE_BASEUNIT").toString();
                                String replaceUnitRatio = oneData1.get("REPLACE_UNITRATIO").toString();
//								List<Map<String, Object>> getQData = PosPub.getUnit_Ratio_Middle(dao,eId, ReplacePluno, ReplacePunit);
//								if (getQData != null && getQData.isEmpty() == false)
//								{
//									replaceWunit = getQData.get(0).get("WUNIT").toString();
//									replaceUnitRatio =getQData.get(0).get("UNIT_RATIO").toString();
//									if(replaceUnitRatio.isEmpty())
//									{
//										replaceUnitRatio="1";
//									}
//								}
//								else
//								{
//									replaceUnitRatio="1";
//								}
                                BigDecimal materialBaseQty = BigDecimal.valueOf(Double.valueOf(materialQty)).multiply(marerialUnitRatio).setScale(8, BigDecimal.ROUND_HALF_UP);
                                BigDecimal replaceBaseQty = BigDecimal.valueOf(Double.valueOf(replaceCompQty)).multiply(Decimal.valueOf(Float.parseFloat(replaceUnitRatio))).setScale(8, BigDecimal.ROUND_HALF_UP);
                                BigDecimal replaceRatio = replaceBaseQty.divide(materialBaseQty, 8, BigDecimal.ROUND_HALF_UP).setScale(8, BigDecimal.ROUND_HALF_UP);
                                //baseWQty       =Decimal.valueOf(Float.parseFloat(baseQty)).multiply(BigDecimal.valueOf(Float.parseFloat(materialUnitRatio))).setScale(6,BigDecimal.ROUND_HALF_UP);
                                //replaceCompWQty=Decimal.valueOf(Float.parseFloat(replaceCompQty)).multiply(BigDecimal.valueOf(Float.parseFloat(replaceUnitRatio))).setScale(6,BigDecimal.ROUND_HALF_UP);
                                condiV.put("PLUNO", ReplacePluno);
                                condiV.put("WAREHOUSE", materialWarehouse);
                                List<Map<String, Object>> getQStockReplacePluno = MapDistinct.getWhereMap(stocklist, condiV, false);
                                if (getQStockReplacePluno != null && getQStockReplacePluno.size() > 0) {//可用库存list有
                                    replaceAvailableQty = Decimal.valueOf(Float.parseFloat(getQStockReplacePluno.get(0).get("QTY").toString())).setScale(6, BigDecimal.ROUND_HALF_UP);//库存可用量
                                    //if(replaceAvailableQty>0)
                                    //修改精度
                                    if (replaceAvailableQty.compareTo(BigDecimal.ZERO) > 0) {
                                        BigDecimal ReplaceLackQty = new BigDecimal("0");//替代料尚缺量
                                        //ReplaceLackQty=marerialLackQty*replaceCompWQty/baseWQty;
                                        //ReplaceLackQty=marerialLackQty.multiply(replaceCompWQty).divide(baseWQty,6,BigDecimal.ROUND_HALF_UP).setScale(6,BigDecimal.ROUND_HALF_UP);
                                        ReplaceLackQty = marerialLackQty.multiply(replaceRatio).setScale(6, BigDecimal.ROUND_HALF_UP);
                                        //lackQty= (lackQty*Float.parseFloat(replaceQty) / Float.parseFloat(qty)- availableQty)*Float.parseFloat(qty)/Float.parseFloat(replaceQty) ;//缺少量

                                        //添加到pstockinMaterialnew待完善
                                        //if(ReplaceLackQty <= replaceAvailableQty)
                                        if (ReplaceLackQty.compareTo(replaceAvailableQty) <= 0) {
                                            //已经替代完成
                                            replaceUseQty = ReplaceLackQty;
                                            //getQStockReplacePluno.get(0).put("BASEQTY",String.valueOf(replaceAvailableQty-replaceUseQty));//更新可用库存量
                                            getQStockReplacePluno.get(0).put("BASEQTY", String.valueOf(replaceAvailableQty.subtract(replaceUseQty).setScale(6, BigDecimal.ROUND_HALF_UP)));//更新可用库存量
                                            Map<String, Object> mappstockinMaterialnew = new HashMap<String, Object>();
                                            mappstockinMaterialnew.put(keyPluNo, pluNo);
                                            mappstockinMaterialnew.put(keyMaterialPluNo, ReplacePluno);
                                            mappstockinMaterialnew.put(keyMaterialPunit, ReplacePunit);
                                            //mappstockinMaterialnew.put("PQTY",String.valueOf(replaceUseQty/Float.parseFloat(replaceUnitRatio)) );
                                            //mappstockinMaterialnew.put("PQTY",String.valueOf(replaceUseQty.divide(BigDecimal.valueOf(Float.parseFloat(replaceUnitRatio)),6,BigDecimal.ROUND_HALF_UP).setScale(6,BigDecimal.ROUND_HALF_UP)) );
                                            mappstockinMaterialnew.put("PQTY", String.valueOf(replaceUseQty.divide(BigDecimal.valueOf(Float.parseFloat(replaceUnitRatio)),
                                                    PosPub.getUnitUDLength(dao, eId, ReplacePunit), BigDecimal.ROUND_HALF_UP).setScale(PosPub.getUnitUDLength(dao, eId, ReplacePunit), BigDecimal.ROUND_HALF_UP)));
                                            mappstockinMaterialnew.put("BASEUNIT", replaceWunit);
                                            mappstockinMaterialnew.put("BASEQTY", String.valueOf(replaceUseQty.setScale(
                                                    PosPub.getUnitUDLength(dao, eId, replaceWunit), BigDecimal.ROUND_HALF_UP)));
                                            mappstockinMaterialnew.put("UNIT_RATIO", replaceUnitRatio);
                                            mappstockinMaterialnew.put("UNITRATIO", replaceUnitRatio);
                                            mappstockinMaterialnew.put("MITEM", mitem);
                                            mappstockinMaterialnew.put(keyMaterialWarehouse, materialWarehouse);
                                            mappstockinMaterialnew.put("BDATE", bdate);
                                            mappstockinMaterialnew.put("OTYPE", otype);
                                            mappstockinMaterialnew.put("OFNO", ofno);
                                            mappstockinMaterialnew.put("MEMO", memo);
                                            mappstockinMaterialnew.put("OITEM", oitem);
                                            mappstockinMaterialnew.put("LOAD_DOCTYPE", load_doctype);
                                            mappstockinMaterialnew.put("LOAD_DOCNO", load_docno);
                                            mappstockinMaterialnew.put("BSNO", bsno);
                                            mappstockinMaterialnew.put("SCRAP_QTY", scrap_qty);
                                            mappstockinMaterialnew.put("BATCH_NO", batch_no);
                                            mappstockinMaterialnew.put("PROD_DATE", prod_date);
                                            mappstockinMaterialnew.put("ISBUCKLE", "Y");
                                            mappstockinMaterialnew.put("FINALPRODBASEQTY", finalProdBaseQty);
//											mappstockinMaterialnew.put("RAWMATERIALBASEQTY",
//													String.valueOf(BigDecimal.valueOf(Float.parseFloat(rawMaterialBaseQty)).multiply(BigDecimal.valueOf(Float.parseFloat(replaceCompQty))).divide(BigDecimal.valueOf(Float.parseFloat(baseQty)),6,BigDecimal.ROUND_HALF_UP).setScale(6,BigDecimal.ROUND_HALF_UP)));
                                            mappstockinMaterialnew.put("RAWMATERIALBASEQTY", replaceRawMaterialBaseQty);
                                            pstockinMaterialnew.add(mappstockinMaterialnew);
                                            ReplaceLackQty = BigDecimal.valueOf(0).setScale(6, BigDecimal.ROUND_HALF_UP);
                                            marerialLackQty = BigDecimal.valueOf(0).setScale(6, BigDecimal.ROUND_HALF_UP);
                                            break;
                                        } else {
                                            replaceUseQty = replaceAvailableQty;
                                            getQStockReplacePluno.get(0).put("BASEQTY", String.valueOf(replaceAvailableQty.subtract(replaceUseQty).setScale(6, BigDecimal.ROUND_HALF_UP)));//更新可用库存量
                                            Map<String, Object> mappstockinMaterialnew = new HashMap<String, Object>();
                                            mappstockinMaterialnew.put(keyPluNo, pluNo);
                                            mappstockinMaterialnew.put(keyMaterialPluNo, ReplacePluno);
                                            mappstockinMaterialnew.put(keyMaterialPunit, ReplacePunit);
                                            mappstockinMaterialnew.put("PQTY", String.valueOf(replaceUseQty.divide(BigDecimal.valueOf(Float.parseFloat(replaceUnitRatio)),
                                                    PosPub.getUnitUDLength(dao, eId, ReplacePunit), BigDecimal.ROUND_HALF_UP).setScale(PosPub.getUnitUDLength(dao, eId, ReplacePunit), BigDecimal.ROUND_HALF_UP)));
                                            mappstockinMaterialnew.put("BASEUNIT", replaceWunit);
                                            mappstockinMaterialnew.put("BASEQTY", String.valueOf(replaceUseQty.setScale(
                                                    PosPub.getUnitUDLength(dao, eId, replaceWunit), BigDecimal.ROUND_HALF_UP)));
                                            mappstockinMaterialnew.put("UNIT_RATIO", replaceUnitRatio);
                                            mappstockinMaterialnew.put("UNITRATIO", replaceUnitRatio);
                                            mappstockinMaterialnew.put("MITEM", mitem);
                                            mappstockinMaterialnew.put(keyMaterialWarehouse, materialWarehouse);
                                            mappstockinMaterialnew.put("BDATE", bdate);
                                            mappstockinMaterialnew.put("OTYPE", otype);
                                            mappstockinMaterialnew.put("OFNO", ofno);
                                            mappstockinMaterialnew.put("MEMO", memo);
                                            mappstockinMaterialnew.put("OITEM", oitem);
                                            mappstockinMaterialnew.put("LOAD_DOCTYPE", load_doctype);
                                            mappstockinMaterialnew.put("LOAD_DOCNO", load_docno);
                                            mappstockinMaterialnew.put("BSNO", bsno);
                                            mappstockinMaterialnew.put("SCRAP_QTY", scrap_qty);
                                            mappstockinMaterialnew.put("BATCH_NO", batch_no);
                                            mappstockinMaterialnew.put("PROD_DATE", prod_date);
                                            mappstockinMaterialnew.put("ISBUCKLE", "Y");
                                            mappstockinMaterialnew.put("FINALPRODBASEQTY", finalProdBaseQty);
//											mappstockinMaterialnew.put("RAWMATERIALBASEQTY",
//													String.valueOf(BigDecimal.valueOf(Float.parseFloat(rawMaterialBaseQty)).multiply(BigDecimal
//															.valueOf(Float.parseFloat(replaceCompQty)).divide(BigDecimal.valueOf(Float.parseFloat(baseQty)),6,BigDecimal.ROUND_HALF_UP)).setScale(6,BigDecimal.ROUND_HALF_UP)));
                                            mappstockinMaterialnew.put("RAWMATERIALBASEQTY", replaceRawMaterialBaseQty);
                                            pstockinMaterialnew.add(mappstockinMaterialnew);
                                            //marerialLackQty=marerialLackQty.subtract(replaceAvailableQty.multiply(baseWQty).divide(replaceCompWQty,6,BigDecimal.ROUND_HALF_UP)).setScale(6,BigDecimal.ROUND_HALF_UP);
                                            marerialLackQty = marerialLackQty.subtract(replaceAvailableQty.divide(replaceRatio, 6, BigDecimal.ROUND_HALF_UP)).setScale(6, BigDecimal.ROUND_HALF_UP);

                                        }
                                    }
                                }
                            }
                        }
                    }
                    //原料缺少量
                    if (marerialLackQty.add(marerialUseQty).setScale(6, BigDecimal.ROUND_HALF_UP).compareTo(BigDecimal.ZERO) > 0 || isbuckle.equals("N")) {
                        if (isbuckle.equals("Y")) {
                            if (getQStockPluno != null && getQStockPluno.size() > 0) {
                                //getQStockPluno.get(0).put("WQTY",String.valueOf(marerialAvailableQty-marerialUseQty-marerialLackQty));//更新可用库存量
                                getQStockPluno.get(0).put("QTY", String.valueOf(marerialAvailableQty.subtract(marerialUseQty).subtract(marerialLackQty).setScale(6, BigDecimal.ROUND_HALF_UP)));//更新可用库存量

                            }
                        }
                        //marerialUseQty=marerialUseQty+marerialLackQty;//原料用量为marerialUseQty+缺少量(替代料库存不足后或无替代料)
                        Map<String, Object> mappstockinMaterialnew = new HashMap<String, Object>();
                        mappstockinMaterialnew.put(keyPluNo, pluNo);
                        mappstockinMaterialnew.put(keyMaterialPluNo, materialPluNo);
                        //	    				if(marerialLackQty+marerialUseQty==Float.parseFloat(wqty))//没有被替代过
                        //	    				{
                        //	    					mappstockinMaterialnew.put("REPLACED_PLUNO","");
                        //	    				}else
                        //	    				{
                        //	    					mappstockinMaterialnew.put("REPLACED_PLUNO",materialPluNo);
                        //	    				}
                        mappstockinMaterialnew.put(keyMaterialPunit, materialPunit);
                        //mappstockinMaterialnew.put("PQTY",String.valueOf((marerialLackQty+marerialUseQty)/Float.parseFloat(materialUnitRatio)) );
                        mappstockinMaterialnew.put("PQTY", String.valueOf(marerialLackQty.add(marerialUseQty).divide(BigDecimal.valueOf(Float.parseFloat(materialUnitRatio)),
                                PosPub.getUnitUDLength(dao, eId, materialPunit), BigDecimal.ROUND_HALF_UP).setScale(PosPub.getUnitUDLength(dao, eId, materialPunit), BigDecimal.ROUND_HALF_UP)));
                        mappstockinMaterialnew.put(keyMaterialWunit, materialWunit);
                        //mappstockinMaterialnew.put("WQTY",String.valueOf(marerialLackQty+marerialUseQty)
                        mappstockinMaterialnew.put("BASEQTY", String.valueOf(marerialLackQty.add(marerialUseQty).setScale(
                                PosPub.getUnitUDLength(dao, eId, materialWunit), BigDecimal.ROUND_HALF_UP)));
                        mappstockinMaterialnew.put("UNIT_RATIO", materialUnitRatio);
                        mappstockinMaterialnew.put("UNITRATIO", materialUnitRatio);
                        mappstockinMaterialnew.put("MITEM", mitem);
                        mappstockinMaterialnew.put(keyMaterialWarehouse, materialWarehouse);
                        mappstockinMaterialnew.put("BDATE", bdate);
                        mappstockinMaterialnew.put("OTYPE", otype);
                        mappstockinMaterialnew.put("OFNO", ofno);
                        mappstockinMaterialnew.put("MEMO", memo);
                        mappstockinMaterialnew.put("OITEM", oitem);
                        mappstockinMaterialnew.put("LOAD_DOCTYPE", load_doctype);
                        mappstockinMaterialnew.put("LOAD_DOCNO", load_docno);
                        mappstockinMaterialnew.put("BSNO", bsno);
                        mappstockinMaterialnew.put("SCRAP_QTY", scrap_qty);
                        mappstockinMaterialnew.put("BATCH_NO", batch_no);
                        mappstockinMaterialnew.put("PROD_DATE", prod_date);
                        mappstockinMaterialnew.put("ISBUCKLE", isbuckle);
                        mappstockinMaterialnew.put("FINALPRODBASEQTY", finalProdBaseQty);
                        mappstockinMaterialnew.put("RAWMATERIALBASEQTY", rawMaterialBaseQty);
                        pstockinMaterialnew.add(mappstockinMaterialnew);

                    }
                    //添加原料到pstockinMaterialnew待完善,marerialUseQty
                }
            }
            pstockinMaterial.clear();
            PosPub.getNewSalePrice_goodsPrice(dao, eId, companyId, shopId, pstockinMaterialnew, keyMaterialPluNo, keyMaterialPunit, keyMaterialWunit, "PRICE", "DISTRIPRICE", "UNIT_RATIO");


            if (pstockinMaterialnew != null && pstockinMaterialnew.size() > 0) {
                int item = 0;
                for (Map<String, Object> oneData2 : pstockinMaterialnew) {
                    item++;
                    Map<String, Object> mappstockinMaterial = new HashMap<String, Object>();
                    mappstockinMaterial.put(keyPluNo, oneData2.get(keyPluNo).toString());
                    mappstockinMaterial.put(keyMaterialPluNo, oneData2.get(keyMaterialPluNo).toString());
                    mappstockinMaterial.put(keyMaterialPunit, oneData2.get(keyMaterialPunit).toString());
                    mappstockinMaterial.put("PQTY", oneData2.get("PQTY").toString());
                    mappstockinMaterial.put("BASEUNIT", oneData2.get("BASEUNIT").toString());
                    mappstockinMaterial.put("BASEQTY", oneData2.get("BASEQTY").toString());
                    mappstockinMaterial.put("UNIT_RATIO", oneData2.get("UNIT_RATIO").toString());
                    mappstockinMaterial.put("MITEM", oneData2.get("MITEM").toString());
                    mappstockinMaterial.put("WAREHOUSE", oneData2.get("WAREHOUSE").toString());
                    mappstockinMaterial.put("BDATE", oneData2.get("BDATE").toString());
                    mappstockinMaterial.put("OTYPE", oneData2.get("OTYPE").toString());
                    mappstockinMaterial.put("OFNO", oneData2.get("OFNO").toString());
                    mappstockinMaterial.put("MEMO", oneData2.get("MEMO").toString());
                    mappstockinMaterial.put("ITEM", String.valueOf(item));
                    mappstockinMaterial.put("OITEM", oneData2.get("OITEM").toString());
                    mappstockinMaterial.put("LOAD_DOCTYPE", oneData2.get("LOAD_DOCTYPE").toString());
                    mappstockinMaterial.put("LOAD_DOCNO", oneData2.get("LOAD_DOCNO").toString());
                    mappstockinMaterial.put("BSNO", oneData2.get("BSNO").toString());
                    mappstockinMaterial.put("SCRAP_QTY", oneData2.get("SCRAP_QTY").toString());
                    mappstockinMaterial.put("PRICE", oneData2.get("PRICE").toString());
                    mappstockinMaterial.put("DISTRIPRICE", oneData2.get("DISTRIPRICE").toString());
                    mappstockinMaterial.put("AMT", String.valueOf(BigDecimalUtils.round(Float.valueOf(oneData2.get("PRICE").toString()) * Float.valueOf(oneData2.get("PQTY").toString()), amtLengthInt)));
                    mappstockinMaterial.put("DISTRIAMT", String.valueOf(BigDecimalUtils.round(Float.valueOf(oneData2.get("DISTRIPRICE").toString()) * Float.valueOf(oneData2.get("PQTY").toString()), distriAmtLengthInt)));
                    mappstockinMaterial.put("BATCH_NO", oneData2.get("BATCH_NO").toString());
                    mappstockinMaterial.put("PROD_DATE", oneData2.get("PROD_DATE").toString());
                    mappstockinMaterial.put("ISBUCKLE", oneData2.get("ISBUCKLE").toString());
                    mappstockinMaterial.put("FINALPRODBASEQTY", oneData2.get("FINALPRODBASEQTY").toString());
                    mappstockinMaterial.put("RAWMATERIALBASEQTY", oneData2.get("RAWMATERIALBASEQTY").toString());
                    pstockinMaterial.add(mappstockinMaterial);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            ;
        }
    }

    public static List<Map<String, Object>> stockList(DsmDAO dao, String eId, String shopId, List<Map<String, Object>> getQDataDetail
            , String keyPluNo, String keyWqty) {
        try {
            String sql = "select a.* from DCP_STOCK a"
                    + " WHERE a.eid='" + eId + "' "
                    + " and a.organizationno='" + shopId + "' "
                    + "";
            List<Map<String, Object>> getwqty = dao.executeQuerySQL(sql, null);
            for (Map<String, Object> oneData : getQDataDetail) {
                String pluno = oneData.get(keyPluNo).toString();

                Map<String, Object> condiV = new HashMap<String, Object>();
                condiV.put("PLUNO", pluno);
                List<Map<String, Object>> getpluwqtyList = MapDistinct.getWhereMap(getwqty, condiV, true);
                condiV.clear();
                String qty = "0";
                if (getpluwqtyList != null && getpluwqtyList.size() > 0) {
                    qty = getpluwqtyList.get(0).get("QTY").toString();
                }
                oneData.put(keyWqty, qty);
            }
            return getwqty;
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Map<String, Object>> getMaterialReplace(DsmDAO dao, String eId, String shopId,
                                                               String PluNo, String materialPluNo) {
        try {
//			Calendar cal = Calendar.getInstance();//获得当前时间
//			SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
//			String _SysDATE = df.format(cal.getTime());
//			String sql="SELECT * FROM ( "
//					+ " SELECT A.COMPANYNO,A.BOM_TYPE,A.PLUNO,A.MATERIAL_PLUNO,A.REPLACE_PLUNO"
//					+ " ,A.REPLACE_UNIT,A.REPLACE_QTY,A.QTY ,A.PRIORITY "
//					+ " FROM TB_BOM_REPLACE A LEFT JOIN TB_BOM_REPLACE_SHOP B ON "
//					+ " A.COMPANYNO=B.COMPANYNO AND A.PLUNO=B.PLUNO AND A.MATERIAL_PLUNO=B.MATERIAL_PLUNO "
//					+ " AND B.REPLACE_BDATE<='"+_SysDATE+"' "
//					+ " AND B.REPLACE_EDATE>='"+_SysDATE+"' AND B.CNFFLG='Y' "
//					+ " AND B.BOM_TYPE='0' "
//					+ " AND B.ORGANIZATIONNO='"+shop+"'"
//					+ " WHERE B.PLUNO IS NULL "
//					+ " AND A.COMPANYNO='"+CompanyNo+"' "
//					+ " AND A.PLUNO='"+PluNo+"' "
//					+ " AND A.MATERIAL_PLUNO='"+materialPluNo+"' "
//					+ " AND A.REPLACE_BDATE<='"+_SysDATE+"' "
//					+ " AND A.REPLACE_EDATE>='"+_SysDATE+"' AND A.CNFFLG='Y' "
//					+ " AND A.BOM_TYPE='0' "
//					+ " UNION ALL"
//					+ " SELECT B.COMPANYNO,B.BOM_TYPE,B.PLUNO,B.MATERIAL_PLUNO,B.REPLACE_PLUNO"
//					+ " ,B.REPLACE_UNIT,B.REPLACE_QTY,B.QTY ,B.PRIORITY "
//					+ " FROM TB_BOM_REPLACE_SHOP B "
//					+ " WHERE B.COMPANYNO='"+CompanyNo+"' "
//					+ " AND B.ORGANIZATIONNO='"+shop+"' "
//					+ " AND B.BOM_TYPE='0' "
//					+ " AND B.PLUNO='"+PluNo+"' "
//					+ " AND B.MATERIAL_PLUNO='"+materialPluNo+"' "
//					+ " AND B.REPLACE_BDATE<='"+_SysDATE+"' "
//					+ " AND B.REPLACE_EDATE>='"+_SysDATE+"' AND B.CNFFLG='Y' ) "
//					+ " ORDER BY PLUNO,MATERIAL_PLUNO,PRIORITY ASC ";		
            String sql = "select a.pluno,b.qty,c.*,d.UNITRATIO AS REPLACE_UNITRATIO,d.UNIT AS  REPLACE_BASEUNIT  from ("
                    + " select * from ("
                    + " select a.*,row_number() over (partition by pluno order by effdate desc) as rn from dcp_bom a"
                    + " left join dcp_bom_range d on a.eid=d.eid and a.bomno=d.bomno and d.shopid ='" + shopId + "'"
                    + " where a.eId='" + eId + "' and trunc(a.effdate)<=trunc(sysdate) and a.status='100' and a.bomtype = '0'"
                    + " and (a.restrictshop=0 or (a.restrictshop=1 and d.shopid is not null))"
                    + " and a.pluno='" + PluNo + "') bom where bom.rn=1 ) a"
                    + " inner join dcp_bom_material b on a.eid=b.eid and a.bomno=b.bomno and trunc(b.material_bdate) <=trunc(sysdate) "
                    + " and trunc(sysdate)<=trunc(b.material_edate) "
                    + " and b.MATERIAL_PLUNO='" + materialPluNo + "' AND ISREPLACE='Y' "
                    + " inner join dcp_bom_material_r c on a.eid=c.eid and a.bomno=c.bomno and trunc(c.REPLACE_BDATE) <=trunc(sysdate) "
                    + " and trunc(sysdate)<=trunc(c.REPLACE_EDATE) "
                    + " and c.MATERIAL_PLUNO='" + materialPluNo + "' "
                    + " inner join dcp_goods_unit d on d.eid=c.eid and d.pluno=c.REPLACE_PLUNO and d.ounit=c.REPLACE_UNIT  "
                    + " inner join dcp_goods g on c.eid=g.eid and c.REPLACE_PLUNO=g.pluno and g.status=100 "
                    + " order by a.PLUNO,c.MATERIAL_PLUNO,c.PRIORITY ASC ";

            List<Map<String, Object>> getDataMaterialeList = dao.executeQuerySQL(sql, null);
            return getDataMaterialeList;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 同时获取零售价和进货价
     *
     * @param dao
     * @param companyId
     * @param shopId
     * @param getQDataDetail 这个是你服务查出来的商品明细，至少存在此3个字段(PLUNO,PUNIT,WUNIT)
     * @param keyPluNo       为getQDataDetail里的商品编号名字(PLUNO)
     * @param keyPunit       为getQDataDetail里录入单位编号名称(PUNIT)
     * @param keyWunit       为getQDataDetail里库存单位编号名称(PUNIT)
     * @return 返回的记录包含以下字段：PLUNO、PUNIT、WUNIT、PRICE(录入单位售价)、DISTRIPRICE(录入单位供货价)、RATIO(录入单位)
     */
    public static boolean getNewSalePrice_goodsPrice(DsmDAO dao, String eId, String companyId, String shopId, List<Map<String, Object>> getQDataDetail,
                                                     String keyPluNo, String keyPunit, String keyWunit, String KeyPrice, String KeyDistriPrice, String KeyRatio) {
        try {
            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
//			String sysDate = df.format(cal.getTime());
//			String sqlPrice="";
//			List<Map<String, Object>> getDataPrice=dao.executeQuerySQL(sqlPrice, null);
            MyCommon mc = new MyCommon();
            List<Map<String, Object>> getPluPrice = mc.getSalePrice_distriPrice(StaticInfo.dao, eId, companyId, shopId, getQDataDetail, companyId);

            for (Map<String, Object> oneData2 : getQDataDetail) {
                //计算零售价及进货价
                //String wunit = oneData2.get(keyWunit).toString();
                String punit = oneData2.get(keyPunit).toString();
                String pluNo = oneData2.get(keyPluNo).toString();
                //获取成品的零售价和配送价
                String price = "0";
                String distriPrice = "0";
                BigDecimal amt = new BigDecimal("0");
                BigDecimal distriAmt = new BigDecimal("0");
                Map<String, Object> condiV = new HashMap<>();
                condiV.put("PLUNO", pluNo);
                condiV.put("PUNIT", punit);
                List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPluPrice, condiV, false);
                if (priceList != null && priceList.size() > 0) {
                    price = priceList.get(0).get("PRICE").toString();
                    distriPrice = priceList.get(0).get("DISTRIPRICE").toString();
//					BigDecimal price_b=new BigDecimal(price);
//					BigDecimal distriPrice_b=new BigDecimal(distriPrice);
//					amt = price_b.multiply(new BigDecimal(pqty)).setScale(amtLengthInt, RoundingMode.HALF_UP);
//					distriAmt =  distriPrice_b.multiply(new BigDecimal(pqty)).setScale(distriAmtLengthInt, RoundingMode.HALF_UP);
                }


                oneData2.put(KeyPrice, price);
                oneData2.put(KeyDistriPrice, distriPrice);
                //oneData2.put(KeyRatio,bdcpwRatio.toString());
                condiV.clear();
                condiV = null;
                priceList.clear();
                priceList = null;
            }
//			if (getDataPrice!=null)
//			{
//				getDataPrice.clear();
//				getDataPrice=null;
//			}
            return true;
        } catch (Exception e) {
            return false;
        } finally {
        }
    }

    /**
     * @param dao
     * @param unit 单位编码
     * @return
     */
    public static Integer getUnitUDLength(DsmDAO dao, String eId, String unit) throws Exception {
        Integer length = 0;

        try {

            String sql = "SELECT UDLENGTH FROM DCP_UNIT WHERE UNIT='" + unit + "' and EID='" + eId + "' ";
            List<Map<String, Object>> Datas = dao.executeQuerySQL(sql, null);

            if (Datas != null && Datas.size() > 0) {
                length = Integer.valueOf(Datas.get(0).get("UDLENGTH").toString());
                Datas.clear();
                Datas = null;
            }
            return length;
        } catch (Exception e) {
            return length;
        }
    }

    public static UnitInfo getUnitInfo(DsmDAO dao, String eId, String unit) throws Exception {
        UnitInfo ui=new UnitInfo();
        ui.setUnit( unit);

        try {

            String sql = "SELECT * FROM DCP_UNIT WHERE UNIT='" + unit + "' and EID='" + eId + "' ";
            List<Map<String, Object>> Datas = dao.executeQuerySQL(sql, null);

            if (Datas != null && Datas.size() > 0) {
                int length = Integer.valueOf(Datas.get(0).get("UDLENGTH").toString());
                ui.setUdLength(length);

                String roundType = Datas.get(0).get("ROUNDTYPE").toString();
                ui.setRoundType(roundType);
                ui.setRoundingMode();

                Datas.clear();
                Datas = null;
            }
            return ui;
        } catch (Exception e) {
            return ui;
        }
    }


    /**
     * 替代料
     *
     * @param dao
     * @param eId
     * @param shopId
     * @param pstockinMaterial     完工入庫原料明細
     * @param keyPluNo             成品编号
     * @param keyPunit             成品录入单位
     * @param keyPqty              成品数量
     * @param keyWunit             成品录入单位
     * @param keyUnitRatio         成品库存单位换算率
     * @param keyMaterialPluNo     原料编号
     * @param keyMaterialPunit     原料录入单位
     * @param keyMaterialPqty      原料数量
     * @param keyMaterialWunit     原料库存单位
     * @param keyMaterialUnitRatio 原料库存单位换算率
     * @param keyMaterialWarehouse 原料仓库
     * @param defMaterialWarehouse 原料数量
     * @return true, false
     */
    public static boolean GoodsMaterialReplace(DsmDAO dao, String eId, String shopId, List<Map<String, Object>> pstockinMaterial,
                                               String keyPluNo, String keyPunit, String keyPqty, String keyWunit, String keyUnitRatio,
                                               String keyMaterialPluNo, String keyMaterialPunit, String keyMaterialPqty, String keyMaterialWunit,
                                               String keyMaterialUnitRatio,
                                               String keyMaterialWarehouse, String defMaterialWarehouse) {
        try {
            List<Map<String, Object>> pstockinMaterialnew = new ArrayList<Map<String, Object>>();
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
            condition.put(keyMaterialPluNo, true);
            String sql = "select a.WAREHOUSE,a.PLUNO,a.BASEUNIT,a.qty as BASEQTY from DCP_STOCK a"
                    + " WHERE a.eid='" + eId + "' "
                    + " and a.organizationno='" + shopId + "' ";
            List<Map<String, Object>> stocklist = dao.executeQuerySQL(sql, null);
            if (pstockinMaterial != null && pstockinMaterial.size() > 0) {
                Map<String, Object> condiV = new HashMap<String, Object>();
                for (Map<String, Object> oneData : pstockinMaterial) //完工入库原料List
                {
                    String pluNo = oneData.get(keyPluNo).toString();
                    String punit = oneData.get(keyPunit).toString();
                    String wunit = oneData.get(keyWunit).toString();
                    String pQty = oneData.get("PQTY").toString();
                    String wqty = oneData.get("BASEQTY").toString();//库存单位数量
                    String unitRatio = oneData.get(keyUnitRatio).toString();
                    String warehouse = oneData.get("WAREHOUSE").toString();
                    String materialPluNo = oneData.get(keyMaterialPluNo).toString();
                    String materialPunit = oneData.get(keyMaterialPunit).toString();
                    String materialWunit = oneData.get(keyMaterialWunit).toString();
                    String materialUnitRatio = oneData.get(keyMaterialUnitRatio).toString();
                    String finalProdBaseQty = oneData.get("FINALPRODBASEQTY").toString();//原件底数-成品基础量
                    String rawmaterialbaseqty = oneData.get("RAWMATERIALBASEQTY").toString();//原料基础用量
                    //wqty * rawmaterialbaseqty/finalProdBaseQty/ unitRatio * materialUnitRatio
                    String materialWqty = String.valueOf(BigDecimal.valueOf(Float.parseFloat(wqty))
                            .multiply(BigDecimal.valueOf(Float.parseFloat(rawmaterialbaseqty))
                                    .divide(BigDecimal.valueOf(Float.parseFloat(finalProdBaseQty)), 6, BigDecimal.ROUND_HALF_UP)
                                    .divide(BigDecimal.valueOf(Float.parseFloat(unitRatio)), 6, BigDecimal.ROUND_HALF_UP)
                                    .multiply(BigDecimal.valueOf(Float.parseFloat(materialUnitRatio))))
                            .setScale(6, BigDecimal.ROUND_HALF_UP));
                    String materialWarehouse = oneData.get(keyMaterialWarehouse).toString();
                    if (materialWarehouse.isEmpty()) {
                        materialWarehouse = defMaterialWarehouse;
                    }
                    String batchNo = oneData.get("BATCHNO").toString();
                    String prodDate = oneData.get("PRODDATE").toString();

                    String isBuckle = oneData.get("ISBUCKLE").toString();
                    BigDecimal marerialAvailableQty = new BigDecimal("0");//原料库存可用量
                    BigDecimal marerialLackQty = new BigDecimal("0");    //原料缺少量
                    BigDecimal marerialDemandQty = new BigDecimal("0");   //原料需求用量
                    BigDecimal marerialUseQty = new BigDecimal("0");      //原料使用量
                    BigDecimal replaceUseQty = new BigDecimal("0");       //替代料使用量
                    BigDecimal replaceAvailableQty = new BigDecimal("0"); //替代料库存可用量
                    condiV.put("PLUNO", materialPluNo);
                    condiV.put("WAREHOUSE", materialWarehouse);
                    List<Map<String, Object>> getQStockPluno = MapDistinct.getWhereMap(stocklist, condiV, false);//查找库存
                    condiV.clear();
                    if (getQStockPluno != null && getQStockPluno.size() > 0) {
                        marerialAvailableQty = BigDecimal.valueOf(Float.parseFloat(getQStockPluno.get(0).get("BASEQTY").toString()))
                                .setScale(6, BigDecimal.ROUND_HALF_UP);//库存可用量
                    } else//无库存
                    {
                        marerialAvailableQty = BigDecimal.valueOf(0);
                    }
                    marerialDemandQty = BigDecimal.valueOf(Float.parseFloat(materialWqty)).setScale(6, BigDecimal.ROUND_HALF_UP);//需求用量
                    if (marerialAvailableQty.compareTo(BigDecimal.ZERO) > 0 && marerialAvailableQty.compareTo(marerialDemandQty) >= 0) {
                        marerialUseQty = marerialDemandQty;
                    } else {
                        if (marerialAvailableQty.compareTo(BigDecimal.ZERO) > 0 && marerialAvailableQty.compareTo(marerialDemandQty) < 0) {
                            marerialLackQty = marerialDemandQty.subtract(marerialAvailableQty).setScale(6, BigDecimal.ROUND_HALF_UP);//尚缺量
                            marerialUseQty = marerialAvailableQty;
                        } else {
                            marerialLackQty = marerialDemandQty;
                        }
                    }
                    if (marerialLackQty.compareTo(BigDecimal.ZERO) > 0 && isBuckle.equals("Y"))//原料缺少量 并且是扣料建才找替代上商品
                    {
                        //寻找可替代商品
                        List<Map<String, Object>> materialList = PosPub.getMaterialReplace(dao, eId, shopId, pluNo, materialPluNo);

                        if (materialList != null && materialList.size() > 0)//可以替代
                        {
                            for (Map<String, Object> oneData1 : materialList) {
                                String ReplacePluno = oneData1.get("REPLACE_PLUNO").toString();
                                String ReplacePunit = oneData1.get("REPLACE_UNIT").toString();
                                //String baseQty         =oneData1.get("QTY").toString();//原件底数
                                String replaceCompQty = oneData1.get("REPLACE_QTY").toString();
                                BigDecimal baseWQty = new BigDecimal("0");
                                BigDecimal replaceCompWQty = new BigDecimal("0");
                                String replaceWunit = oneData1.get("REPLACE_BASEUNIT").toString();
                                String replaceUnitRatio = "1";
                                replaceUnitRatio = oneData1.get("REPLACE_UNITRATIO").toString();
                                //baseWQty       =BigDecimal.valueOf(Float.parseFloat(baseQty)).multiply(BigDecimal.valueOf(Float.parseFloat(materialUnitRatio))).setScale(6, BigDecimal.ROUND_HALF_UP);
                                baseWQty = BigDecimal.valueOf(Float.parseFloat(finalProdBaseQty))
                                        .multiply(BigDecimal.valueOf(Float.parseFloat(materialUnitRatio)))
                                        .setScale(6, BigDecimal.ROUND_HALF_UP);

                                replaceCompWQty = BigDecimal.valueOf(Float.parseFloat(replaceCompQty))
                                        .multiply(BigDecimal.valueOf(Float.parseFloat(replaceUnitRatio)))
                                        .setScale(6, BigDecimal.ROUND_HALF_UP);
                                condiV.put("PLUNO", ReplacePluno);
                                condiV.put("WAREHOUSE", materialWarehouse);
                                List<Map<String, Object>> getQStockReplacePluno = MapDistinct.getWhereMap(stocklist, condiV, false);
                                if (getQStockReplacePluno != null && getQStockReplacePluno.size() > 0) {//可用库存list有
                                    replaceAvailableQty = BigDecimal.valueOf(Float.parseFloat(getQStockReplacePluno.get(0).get("BASEQTY").toString())).setScale(6, BigDecimal.ROUND_HALF_UP);//库存可用量
                                    if (replaceAvailableQty.compareTo(BigDecimal.ZERO) > 0) {
                                        BigDecimal ReplaceLackQty = new BigDecimal("0");//替代料尚缺量
                                        ReplaceLackQty = marerialLackQty.multiply(replaceCompWQty).divide(baseWQty, 6, BigDecimal.ROUND_HALF_UP);
                                        if (ReplaceLackQty.compareTo(replaceAvailableQty) <= 0) {//已经替代完成
                                            replaceUseQty = ReplaceLackQty;
                                            getQStockReplacePluno.get(0).put("BASEQTY", String.valueOf(replaceAvailableQty.subtract(replaceUseQty).setScale(6, BigDecimal.ROUND_HALF_UP)));//更新可用库存量
                                            Map<String, Object> mappstockinMaterialnew = new HashMap<String, Object>();
                                            mappstockinMaterialnew.put(keyPluNo, pluNo);
                                            mappstockinMaterialnew.put(keyMaterialPluNo, ReplacePluno);
                                            mappstockinMaterialnew.put(keyMaterialPunit, ReplacePunit);
                                            mappstockinMaterialnew.put(keyMaterialPqty, String.valueOf(replaceUseQty.divide(BigDecimal.valueOf(Float.parseFloat(replaceUnitRatio)),
                                                    6, BigDecimal.ROUND_HALF_UP).setScale(PosPub.getUnitUDLength(dao, eId, ReplacePunit), BigDecimal.ROUND_HALF_UP)));
                                            mappstockinMaterialnew.put(keyMaterialWunit, replaceWunit);
                                            mappstockinMaterialnew.put(keyMaterialUnitRatio, replaceUnitRatio);
                                            mappstockinMaterialnew.put("MATERIAL_BASEQTY", String.valueOf(replaceUseQty));
                                            mappstockinMaterialnew.put(keyMaterialWarehouse, materialWarehouse);
                                            mappstockinMaterialnew.put("BATCHNO", batchNo);
                                            mappstockinMaterialnew.put("PRODDATE", prodDate);
                                            mappstockinMaterialnew.put("ISBUCKLE", "Y");
                                            mappstockinMaterialnew.put("FINALPRODBASEQTY", finalProdBaseQty);
                                            mappstockinMaterialnew.put("RAWMATERIALBASEQTY",
                                                    String.valueOf(BigDecimal.valueOf(Float.parseFloat(rawmaterialbaseqty))
                                                            .multiply(BigDecimal.valueOf(Float.parseFloat(replaceCompQty)))
                                                            .divide(BigDecimal.valueOf(Float.parseFloat(finalProdBaseQty)), 6, BigDecimal.ROUND_HALF_UP)
                                                            .setScale(PosPub.getUnitUDLength(dao, eId, ReplacePunit), BigDecimal.ROUND_HALF_UP)));
                                            mappstockinMaterialnew.put("REPLACED_PUNIT", materialPunit);
                                            mappstockinMaterialnew.put("REPLACED_BASE_QTY", finalProdBaseQty);
                                            mappstockinMaterialnew.put("REPLACED_MATERIAL_BASE_QTY", rawmaterialbaseqty);
                                            mappstockinMaterialnew.put("WAREHOUSE", warehouse);
                                            mappstockinMaterialnew.put("BASEQTY", wqty);
                                            mappstockinMaterialnew.put("PQTY", pQty);
                                            mappstockinMaterialnew.put(keyWunit, wunit);
                                            mappstockinMaterialnew.put(keyUnitRatio, unitRatio);
                                            mappstockinMaterialnew.put(keyPunit, punit);
                                            pstockinMaterialnew.add(mappstockinMaterialnew);
                                            ReplaceLackQty = BigDecimal.valueOf(0);
                                            marerialLackQty = BigDecimal.valueOf(0);
                                            break;
                                        } else {
                                            replaceUseQty = replaceAvailableQty;
                                            getQStockReplacePluno.get(0).put("BASEQTY",
                                                    String.valueOf(replaceAvailableQty.subtract(replaceUseQty)
                                                            .setScale(6, BigDecimal.ROUND_HALF_UP)));//更新可用库存量
                                            Map<String, Object> mappstockinMaterialnew = new HashMap<String, Object>();
                                            mappstockinMaterialnew.put(keyPluNo, pluNo);
                                            mappstockinMaterialnew.put(keyMaterialPluNo, ReplacePluno);
                                            mappstockinMaterialnew.put(keyMaterialPunit, ReplacePunit);
                                            mappstockinMaterialnew.put(keyMaterialPqty,
                                                    String.valueOf(replaceUseQty.divide(BigDecimal.valueOf(Float.parseFloat(replaceUnitRatio)), 6, BigDecimal.ROUND_HALF_UP)
                                                            .setScale(PosPub.getUnitUDLength(dao, eId, ReplacePunit), BigDecimal.ROUND_HALF_UP)));
                                            mappstockinMaterialnew.put(keyMaterialWunit, replaceWunit);
                                            mappstockinMaterialnew.put(keyMaterialUnitRatio, replaceUnitRatio);
                                            mappstockinMaterialnew.put("MATERIAL_BASEQTY", String.valueOf(replaceUseQty));
                                            mappstockinMaterialnew.put(keyMaterialWarehouse, materialWarehouse);
                                            mappstockinMaterialnew.put("BATCHNO", batchNo);
                                            mappstockinMaterialnew.put("PRODDATE", prodDate);
                                            mappstockinMaterialnew.put("ISBUCKLE", "Y");
                                            mappstockinMaterialnew.put("FINALPRODBASEQTY", finalProdBaseQty);
                                            mappstockinMaterialnew.put("RAWMATERIALBASEQTY",
                                                    String.valueOf(BigDecimal.valueOf(Float.parseFloat(rawmaterialbaseqty))
                                                            .multiply(BigDecimal.valueOf(Float.parseFloat(replaceCompQty)))
                                                            .divide(BigDecimal.valueOf(Float.parseFloat(finalProdBaseQty)), 6, BigDecimal.ROUND_HALF_UP)
                                                            .setScale(PosPub.getUnitUDLength(dao, eId, ReplacePunit), BigDecimal.ROUND_HALF_UP)));
                                            mappstockinMaterialnew.put("REPLACED_PUNIT", keyMaterialPunit);
                                            mappstockinMaterialnew.put("REPLACED_BASE_QTY", finalProdBaseQty);
                                            mappstockinMaterialnew.put("REPLACED_MATERIAL_BASE_QTY", rawmaterialbaseqty);
                                            mappstockinMaterialnew.put("WAREHOUSE", warehouse);
                                            mappstockinMaterialnew.put("BASEQTY", wqty);
                                            mappstockinMaterialnew.put("PQTY", pQty);
                                            mappstockinMaterialnew.put(keyWunit, wunit);
                                            mappstockinMaterialnew.put(keyUnitRatio, unitRatio);
                                            mappstockinMaterialnew.put(keyPunit, punit);
                                            pstockinMaterialnew.add(mappstockinMaterialnew);
                                            marerialLackQty = marerialLackQty.subtract(replaceAvailableQty.multiply(baseWQty).divide(replaceCompWQty, 6, BigDecimal.ROUND_HALF_UP))
                                                    .setScale(6, BigDecimal.ROUND_HALF_UP);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (marerialLackQty.add(marerialUseQty).setScale(6, BigDecimal.ROUND_HALF_UP).compareTo(BigDecimal.ZERO) > 0 || isBuckle.equals("N")) {
                        if (isBuckle.equals("Y")) {
                            if (getQStockPluno != null && getQStockPluno.size() > 0) {
                                getQStockPluno.get(0).put("BASEQTY",
                                        String.valueOf(marerialAvailableQty.subtract(marerialUseQty).subtract(marerialLackQty)
                                                .setScale(6, BigDecimal.ROUND_HALF_UP)));//更新可用库存量
                            }
                        }
                        Map<String, Object> mappstockinMaterialnew = new HashMap<String, Object>();
                        mappstockinMaterialnew.put(keyPluNo, pluNo);
                        mappstockinMaterialnew.put(keyMaterialPluNo, materialPluNo);
                        if (marerialLackQty.add(marerialUseQty).setScale(6, BigDecimal.ROUND_HALF_UP)
                                .compareTo(BigDecimal.valueOf(Float.parseFloat(materialWqty)).setScale(6, BigDecimal.ROUND_HALF_UP)) == 0)//没有被替代过
                        {
                            mappstockinMaterialnew.put("REPLACED_PLUNO", "");
                        } else {
                            mappstockinMaterialnew.put("REPLACED_PLUNO", materialPluNo);
                        }
                        mappstockinMaterialnew.put(keyMaterialPunit, materialPunit);
                        mappstockinMaterialnew.put(keyMaterialPqty, String.valueOf(marerialLackQty.add(marerialUseQty)
                                .divide(BigDecimal.valueOf(Float.parseFloat(materialUnitRatio)),
                                        6, BigDecimal.ROUND_HALF_UP).setScale(
                                        PosPub.getUnitUDLength(dao, eId, materialPunit), BigDecimal.ROUND_HALF_UP)));

                        mappstockinMaterialnew.put(keyMaterialWunit, materialWunit);
                        mappstockinMaterialnew.put(keyMaterialUnitRatio, materialUnitRatio);
                        mappstockinMaterialnew.put("MATERIAL_BASEQTY",
                                String.valueOf(marerialLackQty.add(marerialUseQty).setScale(
                                        PosPub.getUnitUDLength(dao, eId, materialWunit), BigDecimal.ROUND_HALF_UP)));
                        mappstockinMaterialnew.put(keyMaterialWarehouse, materialWarehouse);
                        mappstockinMaterialnew.put("BATCHNO", batchNo);
                        mappstockinMaterialnew.put("PRODDATE", prodDate);
                        mappstockinMaterialnew.put("ISBUCKLE", isBuckle);
                        mappstockinMaterialnew.put("FINALPRODBASEQTY", finalProdBaseQty);
                        mappstockinMaterialnew.put("RAWMATERIALBASEQTY", rawmaterialbaseqty);
                        mappstockinMaterialnew.put("WAREHOUSE", warehouse);
                        mappstockinMaterialnew.put("BASEQTY", wqty);
                        mappstockinMaterialnew.put("PQTY", pQty);
                        mappstockinMaterialnew.put(keyWunit, wunit);
                        mappstockinMaterialnew.put(keyUnitRatio, unitRatio);
                        mappstockinMaterialnew.put(keyPunit, punit);
                        pstockinMaterialnew.add(mappstockinMaterialnew);

                    }
                    //添加原料到pstockinMaterialnew待完善,marerialUseQty
                }
            }
            pstockinMaterial.clear();
            if (pstockinMaterialnew != null && pstockinMaterialnew.size() > 0) {
                int item = 0;
                for (Map<String, Object> oneData2 : pstockinMaterialnew) {
                    item++;
                    Map<String, Object> mappstockinMaterial = new HashMap<String, Object>();
                    mappstockinMaterial.put(keyPluNo, oneData2.get(keyPluNo).toString());
                    mappstockinMaterial.put(keyMaterialPluNo, oneData2.get(keyMaterialPluNo).toString());
                    mappstockinMaterial.put(keyMaterialPunit, oneData2.get(keyMaterialPunit).toString());
                    //mappstockinMaterial.put(keyMaterialPqty, String.valueOf(Float.parseFloat(oneData2.get(keyMaterialPqty).toString())/Float.parseFloat(oneData2.get("BASEQTY").toString())));
                    mappstockinMaterial.put(keyMaterialPqty, oneData2.get(keyMaterialPqty).toString());
                    mappstockinMaterial.put(keyMaterialWunit, oneData2.get(keyMaterialWunit).toString());
                    mappstockinMaterial.put(keyMaterialUnitRatio, oneData2.get(keyMaterialUnitRatio).toString());
                    mappstockinMaterial.put("MATERIAL_BASEQTY", oneData2.get("MATERIAL_BASEQTY").toString());
                    mappstockinMaterial.put(keyMaterialWarehouse, oneData2.get(keyMaterialWarehouse).toString());
                    mappstockinMaterial.put("BATCHNO", oneData2.get("BATCHNO").toString());
                    mappstockinMaterial.put("PRODDATE", oneData2.get("PRODDATE").toString());
                    mappstockinMaterial.put("ISBUCKLE", oneData2.get("ISBUCKLE").toString());
                    mappstockinMaterial.put("FINALPRODBASEQTY", oneData2.get("FINALPRODBASEQTY").toString());
                    mappstockinMaterial.put("RAWMATERIALBASEQTY", oneData2.get("RAWMATERIALBASEQTY").toString());
                    mappstockinMaterial.put("MATERIAL_ISBATCH", "N");
                    mappstockinMaterial.put("WAREHOUSE", oneData2.get("WAREHOUSE").toString());
                    mappstockinMaterial.put("BASEQTY", oneData2.get("BASEQTY").toString());
                    mappstockinMaterial.put(keyWunit, oneData2.get(keyWunit).toString());
                    mappstockinMaterial.put(keyUnitRatio, oneData2.get(keyUnitRatio).toString());
                    mappstockinMaterial.put(keyPunit, oneData2.get(keyPunit).toString());
                    mappstockinMaterial.put("FEATURENO", "");
                    mappstockinMaterial.put("GOODSBOMUNIT", oneData2.get(keyPunit).toString());
                    mappstockinMaterial.put("PQTY", oneData2.get("PQTY").toString());
                    pstockinMaterial.add(mappstockinMaterial);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            ;
        }
    }

    public static String getBillNo(DsmDAO dao, String eId, String shopId, String billType) {
        String sql = null;
        String billNo = null;
        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO('" + eId + "','" + shopId + "','" + billType + "') BILLNO FROM dual");
        sql = sqlbuf.toString();
        try {
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
            if (getQData != null && getQData.isEmpty() == false) {
                billNo = (String) getQData.get(0).get("BILLNO");
            } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "取" + billType + "单号失败！");
            }
        } catch (Exception e) {
        }
        return billNo;
    }

    /**
     * 支持多字段去重处理:
     * 1.    p->p.get("PLUNO").toString(),p->p.get("PLUNO").toString()
     * 2.    Record::getId, Record::getName
     *
     * @param keyExtractors
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

        return t ->
        {
            final List<?> keys = Arrays.stream(keyExtractors)
                    .map(ke -> ke.apply(t))
                    .collect(Collectors.toList());

            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
        };
    }

    /**
     *
     */
    public static Map<String, Object> getBaseQty(DsmDAO dao, String eId, String pluNo, String unit, String qty) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        String baseUnit = "";
        String baseQty = "";
        String unitRatio = "1";
        try {
            String sql = "select * from DCP_GOODS_UNIT a where a.EID='" + eId + "' AND pluno='" + pluNo + "' AND OUNIT='" + unit + "' ";
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
            if (getQData != null && getQData.size() > 0) {
                baseUnit = getQData.get(0).get("UNIT").toString();
                unitRatio = getQData.get(0).get("UNITRATIO").toString();
                //BigDecimal bdcUnitRatio = new BigDecimal(unitRatio);
                //baseQty = bdcQty.multiply(bdcUnitRatio).setScale(unitdlength, RoundingMode.HALF_UP).toString();
                String guOqty = getQData.get(0).get("OQTY").toString();
                String guQty = getQData.get(0).get("QTY").toString();

                //unitRatio=guQty/guOqtNo

                UnitInfo baseUnitInfo = PosPub.getUnitInfo(dao, eId, baseUnit);

                int unitdlength=baseUnitInfo.getUdLength();//PosPub.getUnitUDLength(dao, eId, baseUnit);
                RoundingMode roundingMode = baseUnitInfo.getRoundingMode();

                BigDecimal bdcQty=new BigDecimal(qty);
                if(Check.NotNull(guOqty)&&Check.NotNull(guQty)){
                    baseQty=bdcQty.multiply(new BigDecimal(guQty)).divide(new BigDecimal(guOqty),unitdlength, roundingMode).setScale(unitdlength, roundingMode).toString();
                }else{
                    BigDecimal bdcUnitRatio=new BigDecimal(unitRatio);
                    baseQty=bdcQty.multiply(bdcUnitRatio).setScale(unitdlength, roundingMode).toString();
                }
            } else {
                baseUnit = "";
                baseQty = "";
                unitRatio = "1";
            }

            getQData.clear();
            getQData = null;
            map.put("baseUnit", baseUnit);
            map.put("unitRatio", unitRatio);
            map.put("baseQty", baseQty);
            return map;
        } catch (Exception e) {
            map.put("baseUnit", baseUnit);
            map.put("unitRatio", unitRatio);
            map.put("baseQty", baseQty);
            return map;
        } finally {
            map = null;
        }
    }

    public static Map<String, Object> getUnitCalculate(DsmDAO dao, String eId, String pluNo, String beforeUnit,String afterUnit, String decimal){

        Map<String, Object> map = new HashMap<String, Object>();
        String afterDecimal = "";
        String unitRatio = "1";
        String udLength="0";

        if(Check.Null(beforeUnit)||Check.Null(afterUnit)||Check.Null(decimal)||"0".equals(decimal)){
            map.put("afterUnit", afterUnit);
            map.put("unitRatio", unitRatio);
            map.put("afterDecimal", "0");
            map.put("udLength", udLength);
            return map;
        }
        if(beforeUnit.equals(afterUnit)){
            map.put("afterUnit", afterUnit);
            map.put("unitRatio", unitRatio);
            map.put("afterDecimal", decimal);
            map.put("udLength", udLength);
            return map;
        }

        try {
            String sql = "select * from DCP_GOODS_UNIT a where a.EID='" + eId + "' AND pluno='" + pluNo + "' AND (OUNIT='" + beforeUnit + "' or OUNIT='" + afterUnit + "') ";
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
            if(getQData.size()<2){
                afterDecimal="";
                afterUnit=afterUnit;
                unitRatio="1";
            }else{
                List<Map<String, Object>> beforeList = getQData.stream().filter(x -> x.get("OUNIT").toString().equals(beforeUnit)).collect(Collectors.toList());
                String finalAfterUnit = afterUnit;
                List<Map<String, Object>> afterList = getQData.stream().filter(x -> x.get("OUNIT").toString().equals(finalAfterUnit)).collect(Collectors.toList());
                String guOqtyBefore = beforeList.get(0).get("OQTY").toString();
                String guQtyBefore = beforeList.get(0).get("QTY").toString();

                String guOqtyAfter = afterList.get(0).get("OQTY").toString();
                String guQtyAfter = afterList.get(0).get("QTY").toString();

                //unitRatio=guQty/guOqtNo
                UnitInfo beforeUnitInfo = PosPub.getUnitInfo(dao, eId, beforeUnit);
                UnitInfo afterUnitInfo = PosPub.getUnitInfo(dao, eId, afterUnit);

                int unitdlength=beforeUnitInfo.getUdLength();//PosPub.getUnitUDLength(dao, eId, baseUnit);
                RoundingMode roundingMode = beforeUnitInfo.getRoundingMode();

                BigDecimal divide = new BigDecimal(decimal).multiply(new BigDecimal(guQtyBefore)).multiply(new BigDecimal(guOqtyAfter))
                        .divide(new BigDecimal(guOqtyBefore).multiply(new BigDecimal(guQtyAfter)), unitdlength, roundingMode);
                afterDecimal=divide.toString();

                //bdcQty.multiply(new BigDecimal(guQty)).divide(new BigDecimal(guOqty),unitdlength, roundingMode)
            }


            getQData.clear();
            getQData = null;
            map.put("afterUnit", afterUnit);
            map.put("unitRatio", unitRatio);
            map.put("afterDecimal", afterDecimal);
            map.put("udLength", udLength);
            return map;
        } catch (Exception e) {
            map.put("afterUnit", afterUnit);
            map.put("unitRatio", unitRatio);
            map.put("afterDecimal", afterDecimal);
            map.put("udLength", udLength);
            return map;
        } finally {
            map = null;
        }
    }

    public static Map<String, Object> getUnitCalculatePrice(DsmDAO dao, String eId, String pluNo, String beforeUnit,String afterUnit, String decimal){

        Map<String, Object> map = new HashMap<String, Object>();
        String afterDecimal = "";
        String unitRatio = "1";
        String udLength="0";

        if(Check.Null(beforeUnit)||Check.Null(afterUnit)||Check.Null(decimal)||"0".equals(decimal)){
            map.put("afterUnit", afterUnit);
            map.put("unitRatio", unitRatio);
            map.put("afterDecimal", "0");
            map.put("udLength", udLength);
            return map;
        }
        if(beforeUnit.equals(afterUnit)){
            map.put("afterUnit", afterUnit);
            map.put("unitRatio", unitRatio);
            map.put("afterDecimal", decimal);
            map.put("udLength", udLength);
            return map;
        }

        try {
            String sql = "select * from DCP_GOODS_UNIT a where a.EID='" + eId + "' AND pluno='" + pluNo + "' AND (OUNIT='" + beforeUnit + "' or OUNIT='" + afterUnit + "') ";
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
            if(getQData.size()<2){
                afterDecimal="";
                afterUnit=afterUnit;
                unitRatio="1";
            }else{
                List<Map<String, Object>> beforeList = getQData.stream().filter(x -> x.get("OUNIT").toString().equals(beforeUnit)).collect(Collectors.toList());
                String finalAfterUnit = afterUnit;
                List<Map<String, Object>> afterList = getQData.stream().filter(x -> x.get("OUNIT").toString().equals(finalAfterUnit)).collect(Collectors.toList());
                String guOqtyBefore = beforeList.get(0).get("OQTY").toString();
                String guQtyBefore = beforeList.get(0).get("QTY").toString();

                String guOqtyAfter = afterList.get(0).get("OQTY").toString();
                String guQtyAfter = afterList.get(0).get("QTY").toString();

                //unitRatio=guQty/guOqtNo
                UnitInfo beforeUnitInfo = PosPub.getUnitInfo(dao, eId, beforeUnit);
                UnitInfo afterUnitInfo = PosPub.getUnitInfo(dao, eId, afterUnit);

                int unitdlength=afterUnitInfo.getUdLength();//PosPub.getUnitUDLength(dao, eId, baseUnit);
                RoundingMode roundingMode = afterUnitInfo.getRoundingMode();

                BigDecimal divide = new BigDecimal(decimal).multiply(new BigDecimal(guOqtyBefore)).multiply(new BigDecimal(guQtyAfter))
                        .divide(new BigDecimal(guQtyBefore).multiply(new BigDecimal(guOqtyAfter)), unitdlength, roundingMode);
                afterDecimal=divide.toString();

                //bdcQty.multiply(new BigDecimal(guQty)).divide(new BigDecimal(guOqty),unitdlength, roundingMode)
            }


            getQData.clear();
            getQData = null;
            map.put("afterUnit", afterUnit);
            map.put("unitRatio", unitRatio);
            map.put("afterDecimal", afterDecimal);
            map.put("udLength", udLength);
            return map;
        } catch (Exception e) {
            map.put("afterUnit", afterUnit);
            map.put("unitRatio", unitRatio);
            map.put("afterDecimal", afterDecimal);
            map.put("udLength", udLength);
            return map;
        } finally {
            map = null;
        }
    }


    public static RoundingMode getRoundingMode(String roundType) {
        RoundingMode roundingMode=RoundingMode.HALF_UP;
        switch (roundType) {
            case "2":
                roundingMode = RoundingMode.HALF_EVEN;
                break;
            case "3":
                roundingMode = RoundingMode.DOWN;
                break;
            case "4":
                roundingMode = RoundingMode.UP;
                break;
            case "1":
            default:
                roundingMode = RoundingMode.HALF_UP;
        }
        return roundingMode;
    }

    public static RoundingMode getRoundingMode(DsmDAO dao, String eId, String unit) throws Exception{
        String sql = "SELECT * FROM DCP_UNIT WHERE UNIT='" + unit + "' and EID='" + eId + "' ";
        List<Map<String, Object>> Datas = dao.executeQuerySQL(sql, null);

        if (Datas != null && Datas.size() > 0) {
            String roundType = Datas.get(0).get("ROUNDTYPE").toString();
            return getRoundingMode(roundType);
        }
        return getRoundingMode("");
    }

    public static List<BaseUnitCalculate> getBaseQtyList(DsmDAO dao, String eId, List<BaseUnitCalculate> plus) throws Exception {

        //给plus  其他三个字段赋值  再返回
        StringBuffer sJoinPluno=new StringBuffer("");
        StringBuffer sJoinPunit=new StringBuffer("");
        for (BaseUnitCalculate one:plus){
            sJoinPluno.append(one.getPluNo()+",");
            sJoinPunit.append(one.getUnit()+",");
        }

        Map<String, String> mapOrder=new HashMap<String, String>();
        mapOrder.put("PLUNO", sJoinPluno.toString());
        mapOrder.put("UNIT", sJoinPunit.toString());
        MyCommon cm=new MyCommon();
        String withasSql_mono=cm.getFormatSourceMultiColWith(mapOrder);

        String sql = "with p as ("+withasSql_mono+")" +
                " select a.*,b.udlength,b.ROUNDTYPE from DCP_GOODS_UNIT a " +
                " inner join p on p.pluno=a.pluno and a.ounit=p.unit " +
                " left join dcp_unit b on a.eid=b.eid and a.unit=b.unit " +
                " where a.EID='" + eId + "'  ";
        List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);

        plus.forEach(x->{
            List<Map<String, Object>> filterRows = getQData.stream().filter(y -> y.get("PLUNO").toString().equals(x.getPluNo())
                    && y.get("OUNIT").toString().equals(x.getUnit())).collect(Collectors.toList());
            if(filterRows.size()>0){
                String baseUnit = filterRows.get(0).get("UNIT").toString();
                String unitRatio = filterRows.get(0).get("UNITRATIO").toString();
                //Integer udlength = Integer.valueOf(filterRows.get(0).get("UDLENGTH").toString());
                BigDecimal bdcUnitRatio = new BigDecimal(unitRatio);
                BigDecimal bdcQty = new BigDecimal(x.getQty());
                String baseQty ="";// bdcQty.multiply(bdcUnitRatio).setScale(udlength, RoundingMode.HALF_UP).toString();


                String guOqty = filterRows.get(0).get("OQTY").toString();
                String guQty = filterRows.get(0).get("QTY").toString();

                //unitRatio=guQty/guOqtNo

                int unitdlength=Integer.valueOf(filterRows.get(0).get("UDLENGTH").toString());

                RoundingMode roundingMode = PosPub.getRoundingMode(filterRows.get(0).get("ROUNDTYPE").toString());

                if(Check.NotNull(guOqty)&&Check.NotNull(guQty)){
                    baseQty=bdcQty.multiply(new BigDecimal(guQty)).divide(new BigDecimal(guOqty),unitdlength, roundingMode).setScale(unitdlength, roundingMode).toString();
                }else{
                    baseQty=bdcQty.multiply(bdcUnitRatio).setScale(unitdlength, roundingMode).toString();
                }

                x.setBaseUnit(baseUnit);
                x.setUnitRatio(unitRatio);
                x.setBaseQty(baseQty);

            }else{
                x.setBaseUnit("");
                x.setBaseQty("");
                x.setUnitRatio("1");
            }
        });

        return plus;
    }


    public static double getQtyFromBaseUnit(DsmDAO dao, String eId, String pluNo, String unit, String qty) throws Exception {
        double oQty = 0;
        String unitRatio = "1";
        try {
            String sql = "select * from DCP_GOODS_UNIT a where a.EID='" + eId + "' AND pluno='" + pluNo + "' AND OUNIT='" + unit + "' ";
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
            if (getQData != null && getQData.size() > 0) {
                unitRatio = getQData.get(0).get("UNITRATIO").toString();
                int unitdlength = PosPub.getUnitUDLength(dao, eId, unit);
                BigDecimal bdcUnitRatio = new BigDecimal(unitRatio);
                BigDecimal bdcQty = new BigDecimal(qty);
                oQty = bdcQty.divide(bdcUnitRatio).setScale(unitdlength, RoundingMode.HALF_UP).doubleValue();
            } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,
                        "商品[" + pluNo + "],单位[" + unit + "]在表DCP_GOODS_UNIT中没有资料");
            }
            return oQty;
        } catch (Exception e) {
            return oQty;
        } finally {
        }
    }

    //时间戳转日期 yyyy-MM-dd HH:mm:ss  by jinzma 20240123
    public static String timestampToDate(String timestamp) {
        if (Check.Null(timestamp)) {
            return "";
        }
        if (timestamp.length() != 10 && timestamp.length() != 13) {
            return "";
        }

        Date date;// 将时间戳转换为Date对象
        if (timestamp.length() == 10) {
            date = new Date(Long.parseLong(timestamp) * 1000L);
        } else {
            date = new Date(Long.parseLong(timestamp));
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

    }

    public static String combineDateTime(String date, String time) {
        String str = "";
        if (Check.NotNull(date) && date.length() == 8) {
            str += date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
        }
        if (Check.NotNull(time) && time.length() == 6) {
            str += " " + time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4, 6);
        }

        return str;
    }


    /**
     * 轻加工商品自动完工入库sql语句列表生成
     *
     * @param eId                   企业编码
     * @param organizationNO        门店编码
     * @param warehouse             仓库编码
     * @param accountDate           入账日期
     * @param accountBy             操作员编码
     * @param receivingNo           收货通知单单号
     * @param getQData_halfToFinish 数量列表
     * @return
     * @throws Exception
     */
    public static List<DataProcessBean> getPStockInSql(String eId, String organizationNO, String warehouse, String pStockInNO, String accountDate, String accountBy, String receivingNo, List<Map<String, Object>> getQData_halfToFinish) throws Exception {
        List<DataProcessBean> DPB = new ArrayList<>();
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sTime = new SimpleDateFormat("HHmmss").format(new Date());
        String[] pstockinColumnsName = {
                "SHOPID", "ORGANIZATIONNO", "BDATE", "PSTOCKIN_ID", "CREATEBY", "CREATE_DATE", "CREATE_TIME", "TOT_PQTY",
                "TOT_AMT", "TOT_CQTY", "EID", "PSTOCKINNO", "MEMO", "STATUS", "PROCESS_STATUS", "OFNO", "PTEMPLATENO",
                "ACCOUNTBY", "ACCOUNT_DATE", "ACCOUNT_TIME", "OTYPE", "WAREHOUSE", "CONFIRMBY", "CONFIRM_DATE", "CONFIRM_TIME",
                "SUBMITBY", "SUBMIT_DATE", "SUBMIT_TIME", "DOC_TYPE", "TOT_DISTRIAMT", "CREATE_CHATUSERID",
                "UPDATE_TIME", "TRAN_TIME", "RECEIPTTYPE"};

        String[] detailColumnsName = {
                "PSTOCKINNO", "SHOPID", "ITEM", "OITEM", "PLUNO", "PUNIT", "PQTY", "BASEUNIT", "BASEQTY", "UNIT_RATIO",
                "PRICE", "AMT", "EID", "ORGANIZATIONNO", "TASK_QTY", "SCRAP_QTY", "MUL_QTY", "BSNO",
                "MEMO", "GDATE", "GTIME", "WAREHOUSE",
                "BATCH_NO", "PROD_DATE", "DISTRIPRICE", "DISTRIAMT", "ACCOUNT_DATE", "FEATURENO"
        };
        String[] matColumnsName = {
                "MITEM", "ITEM", "WAREHOUSE",
                "PLUNO", "PUNIT",
                "PQTY", "PRICE", "AMT",
                "FINALPRODBASEQTY", "RAWMATERIALBASEQTY",
                "EID", "ORGANIZATIONNO", "PSTOCKINNO",
                "SHOPID", "MPLUNO", "BASEUNIT",
                "BASEQTY", "UNIT_RATIO",
                "BATCH_NO", "PROD_DATE", "DISTRIPRICE", "DISTRIAMT", "ACCOUNT_DATE", "ISBUCKLE", "FEATURENO"
        };

        int finishItem = 1;
        BigDecimal TOT_PQTY = new BigDecimal("0");
        BigDecimal TOT_BASEQTY = new BigDecimal("0");
        BigDecimal TOT_AMT = new BigDecimal("0");
        BigDecimal TOT_DISTRIAMT = new BigDecimal("0");
        int TOT_CQTY = 0;

        String stockType = "1";
        String stockDocType = "08";
        String matStockType = "-1";
        String matStockDocType = "11";  //加工原料倒扣
        //成品和原料一样
        for (Map<String, Object> map : getQData_halfToFinish) {
            String pluno = map.get("PLUNO").toString();
            String featureno = map.get("FEATURENO").toString();
            if (warehouse == null || warehouse.trim().isEmpty()) {
                warehouse = map.get("WAREHOUSE").toString();
            }
            String punit = map.get("PUNIT").toString();
            String pqty = map.get("PQTY").toString();
            BigDecimal pqty_b = new BigDecimal("0");
            try {
                pqty_b = new BigDecimal(pqty);
            } catch (Exception e) {
                pqty = "0";
            }
            String baseunit = map.get("BASEUNIT").toString();
            String unit_ratio = map.get("UNIT_RATIO").toString();
            String baseqty = map.get("BASEQTY").toString();
            BigDecimal baseqty_b = new BigDecimal("0");
            try {
                baseqty_b = new BigDecimal(baseqty);
            } catch (Exception e) {
                baseqty = "0";
            }
            String price = map.get("PRICE").toString();
            BigDecimal price_b = new BigDecimal("0");
            try {
                price_b = new BigDecimal(price);
            } catch (Exception e) {
                price = "0";
            }
            String amt = map.get("AMT").toString();
            BigDecimal amt_b = new BigDecimal("0");
            try {
                amt_b = new BigDecimal(amt);
            } catch (Exception e) {
                amt = "0";
            }
            String distriprice = map.get("DISTRIPRICE").toString();
            BigDecimal distriprice_b = new BigDecimal("0");
            try {
                distriprice_b = new BigDecimal(distriprice);
            } catch (Exception e) {
                distriprice = "0";
            }
            String distriamt = map.get("DISTRIAMT").toString();
            BigDecimal distriamt_b = new BigDecimal("0");
            try {
                distriamt_b = new BigDecimal(distriamt);
            } catch (Exception e) {
                distriamt = "0";
            }
            String batch_no = map.get("BATCH_NO").toString();
            String prod_date = map.get("PROD_DATE").toString();
            String item_origin = map.get("ITEM").toString();
            TOT_AMT = TOT_AMT.add(new BigDecimal(amt));
            TOT_DISTRIAMT = TOT_DISTRIAMT.add(new BigDecimal(distriamt));
            TOT_PQTY = TOT_PQTY.add(new BigDecimal(pqty));
            TOT_BASEQTY = TOT_BASEQTY.add(new BigDecimal(baseqty));
            //先添加原料
            {
                DataValue[] matColumnsVal = new DataValue[]
                        {
                                new DataValue(finishItem, Types.INTEGER),
                                new DataValue(finishItem, Types.INTEGER),
                                new DataValue(warehouse, Types.VARCHAR),
                                new DataValue(pluno, Types.VARCHAR),
                                new DataValue(punit, Types.VARCHAR),
                                new DataValue(pqty, Types.VARCHAR),
                                new DataValue(price, Types.VARCHAR),//PRICE
                                new DataValue(amt, Types.VARCHAR),//AMT
                                new DataValue("1", Types.VARCHAR),//FINALPRODBASEQTY 成品和原料一样
                                new DataValue("1", Types.VARCHAR),//RAWMATERIALBASEQTY 成品和原料一样
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(organizationNO, Types.VARCHAR),
                                new DataValue(pStockInNO, Types.VARCHAR),//PSTOCKINNO
                                new DataValue(organizationNO, Types.VARCHAR),//SHOPID
                                new DataValue(pluno, Types.VARCHAR),//MPLUNO
                                new DataValue(baseunit, Types.VARCHAR),
                                new DataValue(baseqty, Types.VARCHAR),
                                new DataValue(unit_ratio, Types.VARCHAR),//PRICE
                                new DataValue(batch_no, Types.VARCHAR),
                                new DataValue(prod_date, Types.VARCHAR),
                                new DataValue(distriprice, Types.VARCHAR),
                                new DataValue(distriamt, Types.VARCHAR),
                                new DataValue(accountDate, Types.VARCHAR),
                                new DataValue("Y", Types.VARCHAR),//ISBUCKLE 是否扣料件
                                new DataValue(featureno, Types.VARCHAR),
                        };
                InsBean ib3 = new InsBean("DCP_PSTOCKIN_MATERIAL", matColumnsName);
                ib3.addValues(matColumnsVal);
                DPB.add(new DataProcessBean(ib3));
                //原料库存流水
                String procedure = "SP_DCP_StockChange";
                Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                inputParameter.put(1, eId);                                            //--企业ID
                inputParameter.put(2, organizationNO);                                         //--组织
                inputParameter.put(3, matStockDocType);                                //--单据类型
                inputParameter.put(4, pStockInNO);                                        //--单据号
                inputParameter.put(5, finishItem);                                      //--单据行号
                inputParameter.put(6, matStockType);                                   //--异动方向 1=加库存 -1=减库存
                inputParameter.put(7, accountDate);                                   //--营业日期 yyyy-MM-dd
                inputParameter.put(8, pluno);                                         //--品号
                inputParameter.put(9, featureno);                                   //--特征码
                inputParameter.put(10, warehouse);                                  //--仓库
                inputParameter.put(11, batch_no);                                    //--批号
                inputParameter.put(12, punit);                                         //--交易单位
                inputParameter.put(13, pqty);         //--交易数量
                inputParameter.put(14, baseunit);     //--基准单位
                inputParameter.put(15, baseqty);      //--基准数量
                inputParameter.put(16, unit_ratio);   //--换算比例
                inputParameter.put(17, price);        //--零售价
                inputParameter.put(18, amt);          //--零售金额
                inputParameter.put(19, distriprice);  //--进货价
                inputParameter.put(20, distriamt);    //--进货金额
                inputParameter.put(21, accountDate);                                   //--入账日期 yyyy-MM-dd
                inputParameter.put(22, prod_date);    //--批号的生产日期 yyyy-MM-dd
                inputParameter.put(23, accountDate);            //--单据日期
                inputParameter.put(24, "");             //--异动原因
                inputParameter.put(25, "配送收货轻加工商品自动完工入库倒扣料");             //--异动描述
                inputParameter.put(26, accountBy);          //--操作员
                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                DPB.add(new DataProcessBean(pdb));
            }

            //再添加成品
            {
                DataValue[] detailColumnsVal = new DataValue[]
                        {
                                new DataValue(pStockInNO, Types.VARCHAR),
                                new DataValue(organizationNO, Types.VARCHAR),
                                new DataValue(finishItem, Types.VARCHAR),
                                new DataValue(item_origin, Types.VARCHAR),//OITEM
                                new DataValue(pluno, Types.VARCHAR),
                                new DataValue(punit, Types.VARCHAR),//punit
                                new DataValue(pqty, Types.VARCHAR),//pqty
                                new DataValue(baseunit, Types.VARCHAR),//baseunit
                                new DataValue(baseqty, Types.VARCHAR),//baseqty
                                new DataValue(unit_ratio, Types.VARCHAR),
                                new DataValue(price, Types.VARCHAR),//PRICE
                                new DataValue(amt, Types.VARCHAR),
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(organizationNO, Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),//taskQty
                                new DataValue("0", Types.VARCHAR),//scrapQty
                                new DataValue("1", Types.VARCHAR),//TASK_QTY
                                new DataValue("", Types.VARCHAR),
                                new DataValue("配送收货轻加工商品自动完工入库", Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue(warehouse, Types.VARCHAR),
                                new DataValue(batch_no, Types.VARCHAR),
                                new DataValue(prod_date, Types.VARCHAR),
                                new DataValue(distriprice, Types.VARCHAR),
                                new DataValue(distriamt, Types.VARCHAR),
                                new DataValue(accountDate, Types.VARCHAR),
                                new DataValue(featureno, Types.VARCHAR),
                        };
                InsBean ib_detail = new InsBean("DCP_PSTOCKIN_DETAIL", detailColumnsName);
                ib_detail.addValues(detailColumnsVal);
                DPB.add(new DataProcessBean(ib_detail));

                String procedure = "SP_DCP_StockChange";
                Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                inputParameter.put(1, eId);                                      //--企业ID
                inputParameter.put(2, organizationNO);                                   //--组织
                inputParameter.put(3, stockDocType);                             //--单据类型
                inputParameter.put(4, pStockInNO);                                  //--单据号
                inputParameter.put(5, finishItem);           //--单据行号
                inputParameter.put(6, stockType);                                //--异动方向 1=加库存 -1=减库存
                inputParameter.put(7, accountDate);          //--营业日期 yyyy-MM-dd
                inputParameter.put(8, pluno);          //--品号
                inputParameter.put(9, featureno);                                //--特征码
                inputParameter.put(10, warehouse);     //--仓库
                inputParameter.put(11, batch_no);      //--批号
                inputParameter.put(12, punit);         //--交易单位
                inputParameter.put(13, pqty);          //--交易数量
                inputParameter.put(14, baseunit);      //--基准单位
                inputParameter.put(15, baseqty);       //--基准数量
                inputParameter.put(16, unit_ratio);    //--换算比例
                inputParameter.put(17, price);         //--零售价
                inputParameter.put(18, amt);           //--零售金额
                inputParameter.put(19, distriprice);   //--进货价
                inputParameter.put(20, distriamt);     //--进货金额
                inputParameter.put(21, accountDate);                             //--入账日期 yyyy-MM-dd
                inputParameter.put(22, prod_date);     //--批号的生产日期 yyyy-MM-dd
                inputParameter.put(23, accountDate);         //--单据日期
                inputParameter.put(24, "");          //--异动原因
                inputParameter.put(25, "配送收货轻加工商品自动完工入库");          //--异动描述
                inputParameter.put(26, accountBy);                               //--操作员

                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                DPB.add(new DataProcessBean(pdb));
            }
            finishItem++;

        }


        TOT_CQTY = getQData_halfToFinish.size();
        String update_time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        DataValue[] insValue1 = new DataValue[]{
                new DataValue(organizationNO, Types.VARCHAR),
                new DataValue(organizationNO, Types.VARCHAR),
                new DataValue(accountDate, Types.VARCHAR),
                new DataValue("", Types.VARCHAR),//PSTOCKIN_ID
                new DataValue(accountBy, Types.VARCHAR),
                new DataValue(sDate, Types.VARCHAR),
                new DataValue(sTime, Types.VARCHAR),
                new DataValue(TOT_PQTY, Types.VARCHAR),
                new DataValue(TOT_AMT, Types.VARCHAR),
                new DataValue(TOT_CQTY, Types.VARCHAR),
                new DataValue(eId, Types.VARCHAR),
                new DataValue(pStockInNO, Types.VARCHAR),
                new DataValue("收货通知单:" + receivingNo + ",轻加工商品自动完工入库", Types.VARCHAR),
                new DataValue("2", Types.VARCHAR),//直接确认
                new DataValue("N", Types.VARCHAR),
                new DataValue(receivingNo, Types.VARCHAR),//OFNO
                new DataValue("", Types.VARCHAR),
                new DataValue(accountBy, Types.VARCHAR),
                new DataValue(accountDate, Types.VARCHAR),
                new DataValue(sTime, Types.VARCHAR),
                new DataValue("", Types.VARCHAR),
                new DataValue(warehouse, Types.VARCHAR),
                new DataValue(accountBy, Types.VARCHAR),
                new DataValue(sDate, Types.VARCHAR),
                new DataValue(sTime, Types.VARCHAR),
                new DataValue(accountBy, Types.VARCHAR),
                new DataValue(sDate, Types.VARCHAR),
                new DataValue(sTime, Types.VARCHAR),
                new DataValue("0", Types.VARCHAR),//DOC_TYPE
                new DataValue(TOT_DISTRIAMT, Types.VARCHAR),
                new DataValue("", Types.VARCHAR),
                new DataValue(update_time, Types.VARCHAR),
                new DataValue(update_time, Types.VARCHAR),
                new DataValue(0, Types.INTEGER),//加工单类型：0 轻加工
        };
        InsBean ib1 = new InsBean("DCP_PSTOCKIN", pstockinColumnsName);
        ib1.addValues(insValue1);
        DPB.add(new DataProcessBean(ib1));

        return DPB;
    }

    /**
     * 根据企业编码和渠道编码获取接口账号，用于调用接口
     *
     * @param dao
     * @param eId       企业编码
     * @param channelId 渠道编码
     * @return
     * @throws Exception
     */
    public static ApiUser getApiUserByChannelId(DsmDAO dao, String eId, String channelId) throws Exception {
        try {
            String sql = " select * from crm_apiuser where eid='" + eId + "' and CHANNELID='" + channelId + "'";
            List<Map<String, Object>> getQData = dao.executeQuerySQL(sql, null);
            if (getQData == null || getQData.isEmpty()) {
                return null;
            }
            ApiUser apiUser = new ApiUser();
            Map<String, Object> map = getQData.get(0);
            String userKey = map.get("USERKEY").toString();
            String userCode = map.get("USERCODE").toString();
            String userName = map.get("USERNAME").toString();
            String companyId = map.get("COMPANYID").toString();
            String shopId = map.get("SHOPID").toString();
            //String channelId = map.get("CHANNELID").toString();
            String departId = map.get("DEPARTID").toString();
            String msgChannelId = map.get("MSGCHANNELID").toString();
            String appType = map.get("APPTYPE").toString();
            String useDate = map.get("USEDATE").toString();
            String endDate = map.get("ENDDATE").toString();
            String useWhite = map.get("USEWHITE").toString();
            String remark = map.get("REMARK").toString();

            apiUser.setUserCode(userCode);
            apiUser.setUserName(userName);
            apiUser.setUserKey(userKey);
            apiUser.setCompanyId(companyId);
            apiUser.setShopId(shopId);
            apiUser.setChannelId(channelId);
            apiUser.setDepartId(departId);
            apiUser.setMsgChannelId(msgChannelId);
            apiUser.setAppType(appType);
            apiUser.setUseDate(useDate);
            apiUser.setEndDate(endDate);
            apiUser.setUseWhite(useWhite);
            apiUser.setRemark(remark);
            return apiUser;
        } catch (Exception e) {
            return null;
        }

    }

    public static String getPluBarCode(DsmDAO dao, String eId, String pluBarCodeRule, String pluNo, String oldPluBarCode) throws Exception {
        //批量生成不存库
        String pluBarCode = "";
        if (Check.Null(pluBarCodeRule)) {
            pluBarCodeRule = PosPub.getPARA_SMS(dao, eId, "", "PluBarCodeRule");
        }
        if (Check.Null(pluBarCodeRule)) {
            pluBarCodeRule = "1";
        }

        //1.品号+3位流水码
        //2.品号+4位流水码
        //3.品号+5位流水码
        if (Check.Null(pluNo)) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,
                    "商品编码为空");
        }

        int id = 0;
        //不是以pluNo开头的不用管
        String barCodeSql = "select MAX(a.plubarcode) barcode from DCP_GOODS_BARCODE a " +
                "where a.eid='" + eId + "' and a.pluno='" + pluNo + "' and a.plubarcode like '" + pluNo + "%'";
        List<Map<String, Object>> getQData = dao.executeQuerySQL(barCodeSql, null);

        if (Check.Null(oldPluBarCode)) {
            if (CollUtil.isNotEmpty(getQData)) {
                String barcode = getQData.get(0).get("BARCODE").toString();
                if (!Check.Null(barcode)) {
                    String substring = barcode.substring(pluNo.length());
                    if (!Check.Null(substring)) {
                        id = Integer.parseInt(substring);
                    }
                }

            }
        } else {
            //得看看库里面和老的哪一个大 取大的
            if (CollUtil.isNotEmpty(getQData)) {
                String barcode = getQData.get(0).get("BARCODE").toString();

                if (!Check.Null(barcode)) {
                    String substring = barcode.substring(pluNo.length());
                    String substringOld = oldPluBarCode.substring(pluNo.length());
                    if (!Check.Null(substring)) {
                        if (!Check.Null(substringOld)) {
                            if (Integer.parseInt(substringOld) > Integer.parseInt(substring)) {
                                id = Integer.parseInt(substringOld);
                            } else {
                                id = Integer.parseInt(substring);
                            }
                        } else {
                            id = Integer.parseInt(substring);
                        }
                    } else {
                        if (!Check.Null(substringOld)) {
                            id = Integer.parseInt(substringOld);
                        }
                    }
                } else {
                    String substring = oldPluBarCode.substring(pluNo.length());
                    if (!Check.Null(substring)) {
                        id = Integer.parseInt(substring);
                    }
                }

            } else {
                String substring = oldPluBarCode.substring(pluNo.length());
                if (!Check.Null(substring)) {
                    id = Integer.parseInt(substring);
                }
            }
        }
        id += 1;
        int left = 3;
        if (pluBarCodeRule.equals("2")) {
            left = 4;
        } else if (pluBarCodeRule.equals("3")) {
            left = 5;
        }
        pluBarCode = pluNo + FillStr(id + "", left, "0", true);

        return pluBarCode;
    }


    public static boolean isBatchExisted(String eId, String pluNo, String featureNo, String batchNo) throws Exception {
        boolean isExisted = false;

        String sql_mes_batch = "select * from MES_BATCH a " +
                "WHERE a.eid='" + eId + "' " +
                "AND a.PLUNO='" + pluNo + "' " +
                "and a.featureno='" + featureNo + "' " +
                "and a.batchno='" + batchNo + "' ";

        List<Map<String, Object>> info = StaticInfo.dao.executeQuerySQL(sql_mes_batch, null);
        isExisted = null != info && !info.isEmpty();


        return isExisted;

    }

    public static void insertIntoMesBatchList(DsmDAO dao, List<MesBatchInfo> mesBatchList) throws Exception{
        String lastmoditime = DateFormatUtils.getNowDateTime();

        List<MesBatchSimpleInfo> batchList = mesBatchList.stream().filter(x -> Check.NotNull(x.getAddBatchNo()))
                .map(y -> {
                    MesBatchSimpleInfo mesBatchSimpleInfo = new MesBatchSimpleInfo();
                    mesBatchSimpleInfo.setEId(y.getEId());
                    mesBatchSimpleInfo.setShopId(y.getShopId());
                    mesBatchSimpleInfo.setPluNo(y.getPluNo());
                    mesBatchSimpleInfo.setFeatureNo(y.getFeatureNo());
                    mesBatchSimpleInfo.setAddBatchNo(y.getAddBatchNo());
                    return mesBatchSimpleInfo;
                }).distinct()
                .collect(Collectors.toList());


        if(CollUtil.isNotEmpty(batchList)){
            StringBuffer sJoineid=new StringBuffer("");
            StringBuffer sJoinpluno=new StringBuffer("");
            StringBuffer sJoinfeatureno=new StringBuffer("");
            StringBuffer sJoinbatchno=new StringBuffer("");
            for (MesBatchSimpleInfo mesBatchInfo : batchList){
                sJoineid.append(mesBatchInfo.getEId()+",");
                sJoinpluno.append(mesBatchInfo.getPluNo()+",");
                sJoinfeatureno.append(mesBatchInfo.getFeatureNo()+",");
                sJoinbatchno.append(mesBatchInfo.getAddBatchNo()+",");
            }

            Map<String, String> mapPlu=new HashMap<String, String>();
            mapPlu.put("EID", sJoineid.toString());
            mapPlu.put("PLUNO", sJoinpluno.toString());
            mapPlu.put("FEATURENO", sJoinfeatureno.toString());
            mapPlu.put("BATCHNO", sJoinbatchno.toString());
            MyCommon cm=new MyCommon();
            String withasSql_mono=cm.getFormatSourceMultiColWith(mapPlu);
            
            String batchSql=" with p as ("+withasSql_mono+") " +
                    " select a.* from mes_batch a" +
                    " inner join p on p.eid=a.eid and p.pluno=a.pluno and p.featureno=a.featureno and p.batchno=a.batchno " +
                    " ";
            List<Map<String, Object>> yetBatchList = dao.executeQuerySQL(batchSql, null);


            List<DataProcessBean> dataList = new ArrayList<>();
            for (MesBatchSimpleInfo mesBatchInfo : batchList){
                List<Map<String, Object>> collect = yetBatchList.stream().filter(x -> x.get("EID").toString().equals(mesBatchInfo.getEId())
                        && x.get("PLUNO").toString().equals(mesBatchInfo.getPluNo())
                        && x.get("FEATURENO").toString().equals(mesBatchInfo.getFeatureNo())
                        && x.get("BATCHNO").toString().equals(mesBatchInfo.getAddBatchNo())).collect(Collectors.toList());

                if (collect.size()<=0) {
                    //数据库里面没有  随便在原来的里面找一笔插入
                    MesBatchInfo mesBatchInfo1 = mesBatchList.stream().filter(x -> x.getAddBatchNo().equals(mesBatchInfo.getAddBatchNo())
                            && x.getPluNo().equals(mesBatchInfo.getPluNo())
                            && x.getEId().equals(mesBatchInfo.getEId())
                            && x.getFeatureNo().equals(mesBatchInfo.getFeatureNo())
                    ).collect(Collectors.toList()).get(0);


                    ColumnDataValue mesBatch = new ColumnDataValue();
                    mesBatch.add("EID", DataValues.newString(mesBatchInfo.getEId()));
                    mesBatch.add("ORGANIZATIONNO", DataValues.newString(mesBatchInfo.getShopId()));
                    mesBatch.add("PLUNO", DataValues.newString(mesBatchInfo.getPluNo()));
                    mesBatch.add("FEATURENO", DataValues.newString(mesBatchInfo.getFeatureNo()));
                    mesBatch.add("BATCHNO", DataValues.newString(mesBatchInfo.getAddBatchNo()));
                    mesBatch.add("PRODUCTDATE", DataValues.newDate(DateFormatUtils.getDateTime(mesBatchInfo1.getProductDate())));
                    mesBatch.add("LOSEDATE", DataValues.newDate(DateFormatUtils.getDateTime(mesBatchInfo1.getLoseDate())));
                    mesBatch.add("CREATEOPID", DataValues.newString(mesBatchInfo1.getOpNo()));
                    mesBatch.add("CREATEOPNAME", DataValues.newString(mesBatchInfo1.getOpName()));
                    mesBatch.add("CREATETIME", DataValues.newDate(lastmoditime));
                    mesBatch.add("LASTMODIOPID", DataValues.newString(mesBatchInfo1.getOpNo()));
                    mesBatch.add("LASTMODIOPNAME", DataValues.newString(mesBatchInfo1.getOpName()));
                    mesBatch.add("LASTMODITIME", DataValues.newDate(lastmoditime));
                    mesBatch.add("BILLTYPE", DataValues.newString(mesBatchInfo1.getBillType()));
                    mesBatch.add("BILLNO", DataValues.newString(mesBatchInfo1.getBillNo()));
                    mesBatch.add("SUPPLIERTYPE", DataValues.newString(mesBatchInfo1.getSupplierType()));
                    mesBatch.add("SUPPLIERID", DataValues.newString(mesBatchInfo1.getSupplierId()));

                    dataList.add(new DataProcessBean(DataBeans.getInsBean("MES_BATCH", mesBatch)));

                    
                }
            }
            dao.useTransactionProcessData(dataList);
        }
    }
    public static boolean insertIntoMesBatch(DsmDAO dao,
                                             String eId,
                                             String shopId,
                                             String pluNo,
                                             String featureNo,
                                             String productDate,
                                             String loseDate,
                                             String opNo,
                                             String opName,
                                             String addBatchNo,String billType,String billNo,String supplierType,String supplierId) throws Exception {
        List<DataProcessBean> dataList = new ArrayList<>();

        try {
            String lastmoditime = DateFormatUtils.getNowDateTime();
            if (!isBatchExisted(eId, pluNo, featureNo, addBatchNo)) {


                ColumnDataValue mesBatch = new ColumnDataValue();
                mesBatch.add("EID", DataValues.newString(eId));
                mesBatch.add("ORGANIZATIONNO", DataValues.newString(shopId));
                mesBatch.add("PLUNO", DataValues.newString(pluNo));
                mesBatch.add("FEATURENO", DataValues.newString(featureNo));
                mesBatch.add("BATCHNO", DataValues.newString(addBatchNo));
                mesBatch.add("PRODUCTDATE", DataValues.newDate(DateFormatUtils.getDateTime(productDate)));
                mesBatch.add("LOSEDATE", DataValues.newDate(DateFormatUtils.getDateTime(loseDate)));
                mesBatch.add("CREATEOPID", DataValues.newString(opNo));
                mesBatch.add("CREATEOPNAME", DataValues.newString(opName));
                mesBatch.add("CREATETIME", DataValues.newDate(lastmoditime));
                mesBatch.add("LASTMODIOPID", DataValues.newString(opNo));
                mesBatch.add("LASTMODIOPNAME", DataValues.newString(opName));
                mesBatch.add("LASTMODITIME", DataValues.newDate(lastmoditime));

                mesBatch.add("BILLTYPE", DataValues.newString(billType));
                mesBatch.add("BILLNO", DataValues.newString(billNo));
                mesBatch.add("SUPPLIERTYPE", DataValues.newString(supplierType));
                mesBatch.add("SUPPLIERID", DataValues.newString(supplierId));


                dataList.add(new DataProcessBean(DataBeans.getInsBean("MES_BATCH", mesBatch)));

                return dao.useTransactionProcessData(dataList);
            } else {
                return true;
            }

        } catch (Exception e) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "写入批号信息档失败！");
        }

    }

    /**
     * 序列号获取
     *
     * @param eId      企业编号
     * @param codeType 编码类型，例BATCH
     * @param preFix   前缀符
     * @param len      编码长度，支持最大38位序列号
     * @return 序列号 如 202504230001
     */
    public static String getCodeSertial(@NotBlank String eId, @NotBlank String codeType, @NotBlank String preFix, int len) throws Exception {

        String serialNo;
        if (len <= 0) {
            len = 2;
        }
        if (len > 126) {  //大于126没有意义 需要更改数据库的类型放开这个长度
            len = 126;
        }

        String sql = String.format("SELECT F_DCP_CODESERTIAL('%s','%s','%s',%s) SerialNo FROM DUAL",
                eId,
                preFix,
                codeType,
                len);

        List<Map<String, Object>> getQData = StaticInfo.dao.executeQuerySQL(sql, null);

        if (getQData != null && !getQData.isEmpty()) {
            serialNo = (String) getQData.get(0).get("SERIALNO");
        } else {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "获取最大序列号失败！");
        }
        return serialNo;
    }

    /**
     * 取批号流水
     *
     * @param eId         企业编号
     * @param orgNo       组织编码
     * @param pluNo       商品编码
     * @param productDate 生产日期，例如2024-04-23
     * @return 批号 例: 2025042301
     */
    public static String getBatchNo(String eId, String orgNo, String pluNo, String productDate) throws Exception {
        String batchno = "";

        DsmDAO dao = StaticInfo.dao;

        //1.判断组织是否启用批号管理
        String isBatch = getPARA_SMS(dao, eId, orgNo, "Is_BatchNO");
        if ("Y".equals(isBatch)) {
            //2.获取商品参数
            String sql_goods = "select BATCHRULES,ISBATCH,SHELFLIFE from DCP_GOODS " +
                    "WHERE EID='" + eId + "' AND PLUNO='" + pluNo + "' ";
            List<Map<String, Object>> getGoods = dao.executeQuerySQL(sql_goods, null);
            if (getGoods != null && getGoods.isEmpty()) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品表DCP_GOODS查不到记录！");
            }
            Map<String, Object> goodsInfo = getGoods.get(0);
            //3.判断商品是否启用批号管理
            if ("Y".equals(StringUtils.toString(goodsInfo.get("ISBATCH"), ""))) {
                //4.判断商品批号生成规则，根据规则取当前序列号位数
                String batchRules = StringUtils.toString(goodsInfo.get("BATCHRULES"), "1");
                //批号生成规则: 1.生产日期+2位流水码 2.生产日期+3位流水码 3.生产日期+4位流水码 4.系统日期+4位流水码

                //5.获取正确日期
                if ("4".equals(batchRules)) {
                    //取系统日期
                    productDate = DateFormatUtils.getNowPlainDate();
                } else {
                    if (StringUtils.isEmpty(productDate)) {
                        productDate = DateFormatUtils.getNowPlainDate();
                    } else {
                        productDate = DateFormatUtils.getPlainDate(productDate);
                    }
                }
                //6.取流水码长度
                int len = 0;
                switch (batchRules) {
                    case "1":
                        len = 2;
                        break;
                    case "2":
                        len = 3;
                        break;
                    case "3":
                    case "4":
                        len = 4;
                        break;
                }

                //7.获取批号信息
                batchno = getCodeSertial(eId, "BATCH", productDate, len);
            }
        }

        return batchno;
    }


    public static String getBatchNo(DsmDAO dao,
                                    String eId,
                                    String shopId,
                                    String BatchRules,
                                    String pluno,
                                    String featureNo,
                                    @NotBlank String productDate,
                                    String validDate,
                                    String opno, String opname, String addBatchno){
        return getBatchNo(dao, eId, shopId, BatchRules, pluno, featureNo, productDate, validDate, opno, opname, addBatchno,true,"","","","");
    }

    public static String getBatchNo(DsmDAO dao,
                                    String eId,
                                    String shopId,
                                    String BatchRules,
                                    String pluno,
                                    String featureNo,
                                    @NotBlank String productDate,
                                    String validDate,
                                    String opno, String opname, String addBatchno,Boolean isInsertMesBatch){
        return getBatchNo(dao, eId, shopId, BatchRules, pluno, featureNo, productDate, validDate, opno, opname, addBatchno,isInsertMesBatch,"","","","");
    }

    /**
     * @param dao
     * @param eId
     * @param shopId
     * @param BatchRules  空取系统参数，
     *                    如果已知，可以传进来(
     *                    //2和3 先不做
     *                    //枚举值  1 生产日期  2 生产日期+2位流水号 3 生产日期+3位流水号 4 生产日期+4位流水号)
     * @param pluno
     * @param featureNo
     * @param productDate
     * @param opno
     * @param opname
     * @param addBatchno  直接给批号，直接用，不用根据日期产生
     *                    当addbatchNo为空时只返回batchNo
     *                    当addBatchNo不为空时写入MesBatch
     * @return
     */
    public static String getBatchNo(DsmDAO dao,
                                    String eId,
                                    String shopId,
                                    String BatchRules,
                                    String pluno,
                                    String featureNo,
                                    @NotBlank String productDate,
                                    String validDate,
                                    String opno, String opname, String addBatchno,Boolean isInsertMesBatch,String billType,String billNo,String supplierType,String supplierId) {
        //产生批号
        String batchno = "";

        try {

            //查商品保质期
            String sql_goods = "select SHELFLIFE from DCP_GOODS " +
                    "WHERE EID='" + eId + "' AND PLUNO='" + pluno + "' ";
            List<Map<String, Object>> getGoods = dao.executeQuerySQL(sql_goods, null);
            if (getGoods != null && getGoods.isEmpty()) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品表DCP_GOODS查不到记录！");
            }

            productDate = DateFormatUtils.getDate(productDate);
            int shelflife = Integer.parseInt(getGoods.get(0).get("SHELFLIFE").toString());
            if (StringUtils.isEmpty(validDate)) {
                if(shelflife>0) {
                    validDate = DateFormatUtils.format(DateFormatUtils.addDay(DateFormatUtils.parseDate(productDate),
                            shelflife), DateFormatUtils.DATE_FORMAT);
                }
            } else {
                validDate = DateFormatUtils.getDate(validDate);
            }

            if (StringUtils.isEmpty(addBatchno)) {
                batchno = getBatchNo(eId, shopId, pluno, productDate);
            } else {
                batchno = addBatchno;
            }

            if (StringUtils.isNotEmpty(batchno)&&isInsertMesBatch) {
                insertIntoMesBatch(dao, eId, shopId, pluno, featureNo, productDate, validDate, opno, opname, batchno,billType,billNo,supplierType,supplierId);
            }

        } catch (Exception e) {
            batchno = "";
        }

        return batchno;
    }

    public static String getBatchNoWithOutRule(
            String eId,

            @NotBlank String productDate
    ) {
        //产生批号
        String batchno = "";

        try {

            int len = 4;

            productDate = DateFormatUtils.getPlainDate(productDate);
            batchno = getCodeSertial(eId, "BATCH", productDate, len);

        } catch (Exception e) {
            batchno = "";
        }

        return batchno;
    }

    public static String reCalculateAmt(String price, String qty) {
        if (Check.Null(price) || Check.Null(qty)) {
            return "0";
        }
        BigDecimal priceDecimal = new BigDecimal(price);
        BigDecimal qtyDecimal = new BigDecimal(qty);
        return priceDecimal.multiply(qtyDecimal).toString();
    }

    public static Map<String, Object> getStockChangeVerifyMsg(DsmDAO dao, String eid, String organizationNo, String accountDate) {
        Map returnMsg = new HashMap();
        returnMsg.put("errorMsg", "");
        returnMsg.put("check", "Y");
        try {
            // //1.根据出入库单的【记账日期accountDate】判断：
            //                    // 当记账日期小于等于组织所属法人主账套的【库存关帐日】时，
            //                    // 返回报错提示：组织所属账套库存关账日为%，记账日期不可小于等于库存关账日；--公共方法
            //                    //2.若出入库单的【记账日期accountDate】为空（未指定），默认记账日期=系统日期
            //                    //库存关账日：DCP_ACCOUNT_SETTING.INVCLOSINGDATE(ACCTYPE=1.主账套）
            if (Check.Null(accountDate)) {
                accountDate = DateFormatUtils.getNowPlainDate();
            }
            String orgSql = "select * from dcp_org a " +
                    "where a.eid='" + eid + "' and a.organizationno='" + organizationNo + "' ";
            List<Map<String, Object>> getOrgs = dao.executeQuerySQL(orgSql, null);
            if (CollUtil.isNotEmpty(getOrgs)) {
                String corp = getOrgs.get(0).get("CORP").toString();
                if (Check.NotNull(corp)) {
                    String accountSql = "select a.*,to_char(a.INVCLOSINGDATE,'yyyyMMdd') as INVCLOSINGDATES from DCP_ACOUNT_SETTING a where a.eid='" + eid + "' and a.corp='" + corp + "' and a.ACCTTYPE='1' and a.status='100' " +
                            " order by a.INVCLOSINGDATE desc  ";
                    List<Map<String, Object>> getAccounts = dao.executeQuerySQL(accountSql, null);
                    if (CollUtil.isNotEmpty(getAccounts)) {
                        String invClosingDate = getAccounts.get(0).get("INVCLOSINGDATES").toString();
                        if (Check.NotNull(invClosingDate) && Check.NotNull(accountDate)) {
                            if (DateFormatUtils.parseDate(accountDate).getTime() <= DateFormatUtils.parseDate(invClosingDate).getTime()) {
                                returnMsg.put("errorMsg", "组织所属账套库存关账日为" + invClosingDate + "，记账日期不可小于等于库存关账日");
                                returnMsg.put("check", "N");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            returnMsg.put("errorMsg", "");
            returnMsg.put("check", "N");
        }

        return returnMsg;
    }


    public static Map<String, String> getCorpByOrgNo(String... orgs) {

        Map<String, String> result = new HashMap<>();
        if (null == orgs || orgs.length == 0) {
            return result;
        }

        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT ORGANIZATIONNO,CORP FROM DCP_ORG ");
        querySql.append(" WHERE ORGANIZATIONNO IN ( ");
        for (String orgNo : orgs) {
            querySql.append("'").append(orgNo).append("',");
        }
        querySql.deleteCharAt(querySql.length() - 1);
        querySql.append(" ) ");
        try {
            List<Map<String, Object>> getOrgData = StaticInfo.dao.executeQuerySQL(querySql.toString(), null);
            if (null != getOrgData && !getOrgData.isEmpty()) {
                for (Map<String, Object> oneData : getOrgData) {
                    result.put(oneData.get("ORGANIZATIONNO").toString(), oneData.get("CORP").toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static BcRes getBTypeAndCostCode(BcReq bcReq){
        //syneryDiff 跨法人
        BcRes res=new BcRes();
        res.setBType("");
        res.setCostCode("");


        //DCP_StockOutProcess 出库确认
        //DCP_StockOutRefundProcess 出库红冲确认
        //涉及单据类型docType：5-配货出库，0-退配出库、1-调拨出库、4-移仓出库、3-其他出库
        if("StockOutProcess".equals(bcReq.getServiceType()) || "StockOutRefundProcess".equals(bcReq.getServiceType())){
            if("0".equals(bcReq.getDocType())&&"03".equals(bcReq.getBillType())){
                res.setBType("04");
                res.setCostCode("U");
            }
            else if("1".equals(bcReq.getDocType())&&"04".equals(bcReq.getBillType())){
                res.setBType("04");
                res.setCostCode("U");
            }
            else if("4".equals(bcReq.getDocType())&&"18".equals(bcReq.getBillType())){
                res.setBType("04");
                res.setCostCode("U");
            }
            else if("5".equals(bcReq.getDocType())&&"41".equals(bcReq.getBillType())){
                res.setBType("04");
                res.setCostCode("U");
            }

            //其他出库
            else if("3".equals(bcReq.getDocType())){
                res.setBType("15");
                res.setCostCode("U");
            }else if("12".equals(bcReq.getBillType())){
                res.setBType("02");
                res.setCostCode("U");
            }

        }

        //DCP_StockInProcess 入库确认
        //DCP_StockInRefundProcess 入库红冲确认
        //涉及单据类型docType：0-配送收货 1-调拨收货  3-其他入库  4-移仓收货 5-退配收货
        if("StockInProcess".equals(bcReq.getServiceType()) || "StockInRefundProcess".equals(bcReq.getServiceType())){

            //同法人
            if(!bcReq.syneryDiff){
                if("13".equals(bcReq.getBillType())){
                    res.setBType("04");
                    res.setCostCode("U");
                }else {

                    if ("0".equals(bcReq.getDocType())) {
                        res.setBType("02");
                        res.setCostCode("U");
                    }
                    if ("1".equals(bcReq.getDocType())) {
                        res.setBType("02");
                        res.setCostCode("U");
                    }
                    if ("4".equals(bcReq.getDocType())) {
                        res.setBType("02");
                        res.setCostCode("U");
                    }
                    if ("5".equals(bcReq.getDocType())) {
                        res.setBType("02");
                        res.setCostCode("U");
                    }
                }
            }
            else{
                if("0".equals(bcReq.getDocType())){
                    if("13".equals(bcReq.getBillType())){
                        res.setBType("65");
                        res.setCostCode("U");
                    }else if("01".equals(bcReq.getBillType())){
                        res.setBType("63");
                        res.setCostCode("S");
                    }
                }
                else if("1".equals(bcReq.getDocType())){
                    if("02".equals(bcReq.getBillType())){
                        res.setBType("63");
                        res.setCostCode("S");
                    }
                    if("13".equals(bcReq.getBillType())){
                        //得根据内部交易结算判断
                        //调出组织在途仓

                        if("64".equals(bcReq.getDbTBType())){
                            res.setBType("64");
                            res.setCostCode("S");
                        }
                        if("65".equals(bcReq.getDbTBType())){
                            res.setBType("65");
                            res.setCostCode("U");
                        }


                    }
                }
                else if("3".equals(bcReq.getDocType())){
                    res.setBType("14");
                    res.setCostCode("U");
                }
                else if("5".equals(bcReq.getDocType())){
                    if("13".equals(bcReq.getBillType())){
                        res.setBType("64");
                        res.setCostCode("S");
                    }else if("42".equals(bcReq.getBillType())){
                        res.setBType("66");
                        res.setCostCode("U");
                    }
                }
            }
        }

        //DCP_LStockOutProcess 报损出库确认
        //DCP_LStockOutRefundProcess 报损出库红冲确认
        if("LStockOutProcess".equals(bcReq.getServiceType()) || "LStockOutRefundProcess".equals(bcReq.getServiceType())){
            //出库  15,U
            res.setBType("15");
            res.setCostCode("U");
        }

        //DCP_PStockOutProcess 拆解出库确认
        if("PStockOutProcess".equals(bcReq.getServiceType())){
            //产出类型-主件（组合型BOM，成本计算方向：先算子件后算主件），prodType=0.组合品
            if("0".equals(bcReq.getProdType())){
                if("32".equals(bcReq.getBillType())&&"-1".equals(bcReq.getDirection())){
                    res.setBType("08");
                    res.setCostCode("P");
                }
                if("33".equals(bcReq.getBillType())&&"1".equals(bcReq.getDirection())){
                    res.setBType("11");
                    res.setCostCode("U");
                }
            }else if("1".equals(bcReq.getProdType())){
                //产出类型-子件（拆解型BOM，成本计算方向：先算主件后算子件，子件依据价值分摊主件成本），prodType=1.分解品
                if("32".equals(bcReq.getBillType())&&"-1".equals(bcReq.getDirection())){
                    res.setBType("32");
                    res.setCostCode("U");
                }
                if("33".equals(bcReq.getBillType())&&"1".equals(bcReq.getDirection())){
                    res.setBType("33");
                    res.setCostCode("P");
                }
            }
        }

        //库存盘点或其他业务通过「库存调整单」调整库存，业务类型BTYPE均为【库存调整】
        ///DCP_StockTakeProcess
        ///DCP_StockAdjustProcess（3.0迁移）
        if("StockTakeProcess".equals(bcReq.getServiceType()) || "StockAdjustProcess".equals(bcReq.getServiceType())){
            //10-库存调整  U
            res.setBType("10");
            res.setCostCode("U");
        }

        //DCP_PStockInProcess和DCP_PStockInRefundProcess更新成本码
        //
        //docType=0，异动方向1，bType为08，成本码为P；扣料单，异动方向-1，bType为11，成本码为U；
        //docType=1或3，且prodType=0，异动方向1，bType为08，成本码为P；异动方向-1，bType为11，成本码为U；
        //docType=1或3，且prodType=1，异动方向1，bType为32，成本码为U；异动方向-1，bType为33，成本码为P；
        //docType=2或4，且prodType=0，异动方向-1，bType为08，成本码为P；异动方向1，bType为11，成本码为U；
        //docType=2或4，且prodType=1，异动方向-1，bType为32，成本码为U；异动方向1，bType为33，成本码为P；
        if("PStockInProcess".equals(bcReq.getServiceType()) || "PStockInRefundProcess".equals(bcReq.getServiceType())){
            //完工入库  组合入库  detail 入库   material 原料出库
            if("0".equals(bcReq.getDocType())){
                if("1".equals(bcReq.getDirection())){
                    res.setBType("08");
                    res.setCostCode("P");

                }
                if("-1".equals(bcReq.getDirection())){
                    res.setBType("11");
                    res.setCostCode("U");
                }
            }
            if(("1".equals(bcReq.getDocType()) || "3".equals(bcReq.getDocType()))&&"0".equals(bcReq.getProdType())){
                if("1".equals(bcReq.getDirection())){
                    res.setBType("08");
                    res.setCostCode("P");

                }
                if("-1".equals(bcReq.getDirection())){
                    res.setBType("11");
                    res.setCostCode("U");
                }
            }
            if(("1".equals(bcReq.getDocType()) || "3".equals(bcReq.getDocType()))&&"1".equals(bcReq.getProdType())){
                if("1".equals(bcReq.getDirection())){
                    res.setBType("32");
                    res.setCostCode("U");
                }
                if("-1".equals(bcReq.getDirection())){
                    res.setBType("33");
                    res.setCostCode("P");
                }
            }

            if(("2".equals(bcReq.getDocType()) || "4".equals(bcReq.getDocType()))&&"0".equals(bcReq.getProdType())){
                if("-1".equals(bcReq.getDirection())){
                    res.setBType("08");
                    res.setCostCode("P");

                }
                if("1".equals(bcReq.getDirection())){
                    res.setBType("11");
                    res.setCostCode("U");
                }
            }
            if(("2".equals(bcReq.getDocType()) || "4".equals(bcReq.getDocType()))&&"1".equals(bcReq.getProdType())){
                if("-1".equals(bcReq.getDirection())){
                    res.setBType("32");
                    res.setCostCode("U");
                }
                if("1".equals(bcReq.getDirection())){
                    res.setBType("33");
                    res.setCostCode("P");

                }
            }

        }

        if("SStockInProcess".equals(bcReq.getServiceType())){
            //销退  05 U
            //采退  05 S

            if("4".equals(bcReq.getStockInOutType())){
                res.setBType("05");
                res.setCostCode("U");
            }else{
                if(bcReq.syneryDiff){
                    res.setBType("63");
                    res.setCostCode("S");
                }else{
                    res.setBType("05");
                    res.setCostCode("S");
                }

            }

        }

        if("SStockOutProcess".equals(bcReq.getServiceType())){
            if("1".equals(bcReq.getStockInOutType())){
                res.setBType(bcReq.getBillType());
                res.setCostCode("S");
            }else{
                res.setBType(bcReq.getBillType());
                res.setCostCode("U");
            }

        }

        if("BatchingDocProcess".equals(bcReq.getServiceType())){
            if("40".equals(bcReq.getBillType())){
                res.setBType("02");
                res.setCostCode("U");
            }
            if("39".equals(bcReq.getBillType())){
                res.setBType("04");
                res.setCostCode("U");
            }
        }


        return res;
    }

}
