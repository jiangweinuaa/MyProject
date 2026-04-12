package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Base64;
import okhttp3.HttpUrl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 扫码点餐微信支付同步给微信平台，可返佣金
 *
 * 此代码由李健、王安驰主导强制开发，产生法律法规风险与本人无关
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ScanSaleSyncPlatform extends InitJob
{

    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(ScanSaleSyncPlatform.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public ScanSaleSyncPlatform()
    {

    }

    public ScanSaleSyncPlatform(String eId,String shopId,String organizationNO, String billNo)
    {
        pEId=eId;
        pShop=shopId;
        pOrganizationNO=organizationNO;
        pBillNo=billNo;
    }

    public String doExe()
    {

        //返回信息
        String sReturnInfo="";
        //此服务是否正在执行中
        if (bRun && pEId.equals(""))
        {
            logger.info("\r\n*********扫码点餐同步平台ScanSaleSyncPlatform正在执行中,本次调用取消:************\r\n");

            sReturnInfo="定时传输任务-扫码点餐同步平台ScanSaleSyncPlatform正在执行中！";
            return sReturnInfo;
        }

        bRun=true;//

        logger.info("\r\n*********扫码点餐同步平台ScanSaleSyncPlatform定时调用Start:************\r\n");

        try
        {
            //
            // 每次100笔,查扫码点餐未同步的，同步失败的不管了标注E，历史数据太多，不能老查那些
            String sql = "select * from (SELECT rownum rn,a.* from dcp_sale a where PARTITION_DATE >= 20230301 AND apptype = 'SCAN' AND UPLOADWECHAT='N') where rn>0 and rn<=100 ";
            List<Map<String, Object>> sqllist=this.doQueryData(sql, null);
            if (sqllist != null && sqllist.size()>0)
            {
                for (Map<String, Object> map : sqllist)
                {
                    String loginToken=map.get("LOGINTOKEN").toString();
                    String eId=map.get("EID").toString();
                    String shopid=map.get("SHOPID").toString();
                    String saleno=map.get("SALENO").toString();
                    String wxopenid=map.get("WXOPENID").toString();
                    String channelid=map.get("CHANNELID").toString();

                    if (Check.Null(loginToken) || Check.Null(wxopenid) || Check.Null(channelid))
                    {
                        logger.info("\r\n*********扫码点餐同步平台ScanSaleSyncPlatform dcp_sale表的LOGINTOKEN或WXOPENID或CHANNELID没值eid="+eId+",shopid="+shopid+",saleno="+saleno+"************\r\n");

                        //微信票据loginToken没值无需同步，直接更新E
                        update_UPLOADWECHAT(eId,shopid,saleno,"E");
                    }
                    else
                    {

                        //查渠道资料，取APPID
                        String sql_channel="select APPID from crm_channel where eid='"+eId+"' and channelid='"+channelid+"' ";
                        List<Map<String, Object>> sqllist_channel=this.doQueryData(sql_channel, null);
                        if (sqllist_channel != null && sqllist_channel.size()==0)
                        {
                            logger.info("\r\n*********扫码点餐同步平台ScanSaleSyncPlatform crm_channel表没记录channelid="+channelid+",eid="+eId+",shopid="+shopid+",saleno="+saleno+"************\r\n");

                            //无需同步，直接更新E
                            update_UPLOADWECHAT(eId,shopid,saleno,"E");

                            continue;//跳出去
                        }

                        //
                        String appid=sqllist_channel.get(0).get("APPID").toString();
                        if (Check.Null(appid))
                        {
                            logger.info("\r\n*********扫码点餐同步平台ScanSaleSyncPlatform crm_channel表appid没值，channelid="+channelid+",eid="+eId+",shopid="+shopid+",saleno="+saleno+"************\r\n");

                            //无需同步，直接更新E
                            update_UPLOADWECHAT(eId,shopid,saleno,"E");

                            continue;//跳出去
                        }

                        //开始组请求
                        JSONObject req=new JSONObject();
                        JSONArray dish_list=new JSONArray();//商品列表
                        JSONObject dish=new JSONObject();//商品


                        //查付款列表
                        String sql_sale_pay="select PAYSERNUM,PAY,SDATE,STIME from dcp_sale_pay where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+saleno+"' and PAYSERNUM is not null ";
                        List<Map<String, Object>> sqllist_sale_pay=this.doQueryData(sql_sale_pay, null);
                        if (sqllist_sale_pay != null && sqllist_sale_pay.size()>0)
                        {
                            String amt=sqllist_sale_pay.get(0).get("PAY").toString();
                            String sdate=sqllist_sale_pay.get(0).get("SDATE").toString();
                            String stime=sqllist_sale_pay.get(0).get("STIME").toString();


                            //查商品明细列表
                            String sql_sale_detail="select * from dcp_sale_detail where eid='"+eId+"' and shopid='"+shopid+"' and saleno='"+saleno+"' ";
                            List<Map<String, Object>> sqllist_sale_detail_list=this.doQueryData(sql_sale_detail, null);
                            if (sqllist_sale_detail_list != null && sqllist_sale_detail_list.size()>0)
                            {
                                //如果明细商品数据有问题，此单直接跳过
                                boolean b_detail_error=false;

                                for (Map<String, Object> detail : sqllist_sale_detail_list)
                                {
                                    try
                                    {
                                        String price=detail.get("PRICE").toString();
                                        BigDecimal bdm_price=new BigDecimal(price);
                                        int i_price=bdm_price.multiply(new BigDecimal("100")).intValue();

                                        String qty=detail.get("QTY").toString();
                                        BigDecimal bdm_qty=new BigDecimal(qty).setScale(2,BigDecimal.ROUND_HALF_UP);


                                        dish.put("out_dish_no",detail.get("PLUNO").toString());//商户菜品ID
                                        dish.put("name",detail.get("PNAME").toString());//菜品名称
                                        dish.put("price",i_price);//菜品单价，单位为分
                                        dish.put("unit","BY_SHARE");//菜品单位，BY_SHARE-按份 BY_WEIGHT-按重量
                                        dish.put("count",bdm_qty);//菜品数量，保留小数点后2位有效数字
                                        dish_list.put(dish);
                                    }
                                    catch (Exception e)
                                    {
                                        b_detail_error=true;
                                        break;
                                    }
                                }

                                if (b_detail_error)
                                {
                                    logger.info("\r\n*********扫码点餐同步平台ScanSaleSyncPlatform dcp_sale_detail表的PRICE/QTY异常eid="+eId+",shopid="+shopid+",saleno="+saleno+"************\r\n");

                                    //商品明细数据有错误无需同步，直接更新E
                                    update_UPLOADWECHAT(eId,shopid,saleno,"E");

                                    continue;//直接跳过
                                }

                            }
                            else
                            {
                                logger.info("\r\n*********扫码点餐同步平台ScanSaleSyncPlatform dcp_sale_detail表无记录eid="+eId+",shopid="+shopid+",saleno="+saleno+"************\r\n");

                                //商品明细没值无需同步，直接更新E
                                update_UPLOADWECHAT(eId,shopid,saleno,"E");

                                continue;//直接跳过
                            }


                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                            Date date = null;

                            //格式为rfc3339格式
                            //同一个时间字符串可以根据时区不同显示的，国际化才需要
                            String v_rfc33399_time="";
                            try
                            {
                                date = sdf.parse(sdate+ stime);

                                v_rfc33399_time=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(date);

                            }
                            catch (Exception e)
                            {
                                logger.info("\r\n*********扫码点餐同步平台ScanSaleSyncPlatform dcp_sale_pay表的SDATE/STIME值异常eid="+eId+",shopid="+shopid+",saleno="+saleno+"************\r\n");

                                //日期时间不对无需同步，直接更新E
                                update_UPLOADWECHAT(eId,shopid,saleno,"E");

                                continue;//直接跳过
                            }


                            BigDecimal bdm_amt=new BigDecimal(amt);
                            int i_amt=bdm_amt.multiply(new BigDecimal(100)).intValue();//*100倍到分

                            String sql_pay_list="select * from PAY_LIST where eid='"+eId+"' and shopid='"+shopid+"' and TRADENO ='"+sqllist_sale_pay.get(0).get("PAYSERNUM").toString()+"'";
                            List<Map<String, Object>> sqllist_pay_list=this.doQueryData(sql_pay_list, null);
                            if (sqllist_pay_list != null && sqllist_pay_list.size()>0)
                            {
                                req.put("sp_mchid","1277291801");//微信支付分配的服务商商户号-给鼎捷的固定
                                req.put("sub_mchid",sqllist_pay_list.get(0).get("MCHID").toString());//微信支付分配子商户商户号-PAY_LIST.MCHID
                                req.put("sp_appid","wxaf5eb822e74ad96f");//服务商在微信公众平台申请服务号对应的APPID-给鼎捷的固定
                                req.put("sub_appid",appid);//子商户的
                                req.put("sub_openid",wxopenid);//子商户的
                                req.put("out_shop_no",shopid);//商户旗下门店的唯一编号-系统的门店号
                                req.put("login_token",loginToken);//微信用户登录接口返回的登录票据
                                req.put("order_entry","pages/ScanMain/ScanMain");//小程序
                                req.put("total_amount",i_amt);//总价，单位为分路径
                                req.put("discount_amount",0);//优惠金额，单位为分
                                req.put("user_amount",i_amt);//实际支付金额，单位为分
                                req.put("status","PAY_SUCCESS");//固定，结账成功

                                // 2022-09-07T00:30:37+0800
                                //System.out.println(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date));

                                // 2022-09-07T00:30:37+08:00
                                //System.out.println(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(date));

                                req.put("action_time",v_rfc33399_time);//状态发生变化的时间，格式为rfc3339格式，如2018-06-08T10:34:56+08:00 代表北京时间2018年06月08日10时34分56秒
                                req.put("pay_time",v_rfc33399_time);//支付时间，格式为rfc3339格式，如2018-06-08T10:34:56+08:00 代表北京时间2018年06月08日10时34分56秒（status为PAY_SUCCESS时必填）
                                req.put("transaction_id",sqllist_pay_list.get(0).get("TRADENO").toString());//支付订单号（status为PAY_SUCCESS时必填）-微信支付单号
                                req.put("out_trade_no",sqllist_sale_pay.get(0).get("PAYSERNUM").toString());//服务商系统内部支付订单号（status为PAY_SUCCESS时必填）商户支付单号，pay_list表里有
                                req.put("out_order_no",saleno);//服务商系统内部订单号-销售单号

                                req.put("dish_list",dish_list);

                                String url="https://api.mch.weixin.qq.com/v3/catering/orders/sync-status";

                                String str=req.toString();

                                logger.info("\r\n*********扫码点餐同步平台ScanSaleSyncPlatform request="+str+"************\r\n");

                                HttpUrl httpurl = HttpUrl.parse(url);

                                String Authorization= schema +" "+ getToken("POST",httpurl,str);

                                String res=HttpSend.SendWechatScanOrder("POST",str,url,Authorization);

                                logger.info("\r\n*********扫码点餐同步平台ScanSaleSyncPlatform response="+res+"************\r\n");

                                if (res.equals("204"))//成功
                                {
                                    update_UPLOADWECHAT(eId,shopid,saleno,"Y");
                                }
                                else
                                {
                                    //接口调用失败无需同步，直接更新E
                                    update_UPLOADWECHAT(eId,shopid,saleno,"E");
                                }
                            }
                            else
                            {
                                logger.info("\r\n*********扫码点餐同步平台ScanSaleSyncPlatform PAY_LIST无记录eid="+eId+",shopid="+shopid+",saleno="+saleno+"************\r\n");

                                //PAY_LIST没值无需同步，直接更新E
                                update_UPLOADWECHAT(eId,shopid,saleno,"E");
                            }
                        }
                        else
                        {
                            logger.info("\r\n*********扫码点餐同步平台ScanSaleSyncPlatform dcp_sale_pay无记录eid="+eId+",shopid="+shopid+",saleno="+saleno+"************\r\n");

                            //销售单付款PAYSERNUM没值无需同步，直接更新E
                            update_UPLOADWECHAT(eId,shopid,saleno,"E");

                        }

                    }

                }
            }
            else
            {
                logger.info("\r\n*********扫码点餐同步平台ScanSaleSyncPlatform没有需要同步的数据SQL="+sql+"************\r\n");
            }
        }
        catch (Exception e)
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

                logger.error("\r\n******扫码点餐同步平台ScanSaleSyncPlatform报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******扫码点餐同步平台ScanSaleSyncPlatform报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();
        }
        finally
        {
            bRun=false;//
            logger.info("\r\n*********扫码点餐同步平台ScanSaleSyncPlatform定时调用End:************\r\n");
        }

        return sReturnInfo;

    }


    /**
     * 直接更新字段值
     * @param eId
     * @param shopid
     * @param saleno
     * @param upLoadwechat Y/N/E
     * @throws Exception
     */
    private void  update_UPLOADWECHAT(String eId,String shopid,String saleno,String upLoadwechat) throws Exception
    {
        // values
        Map<String, DataValue> values = new HashMap<String, DataValue>() ;
        values.put("UPLOADWECHAT", new DataValue(upLoadwechat, Types.VARCHAR));

        // condition
        Map<String, DataValue> conditions = new HashMap<String, DataValue>();
        conditions.put("EID", new DataValue(eId, Types.VARCHAR));
        conditions.put("SHOPID",new DataValue(shopid, Types.VARCHAR));
        conditions.put("SALENO",new DataValue(saleno, Types.VARCHAR));

        StaticInfo.dao.update("dcp_sale", values, conditions);

    }


    // Authorization: <schema> <token>
    // GET - getToken("GET", httpurl, "")
    // POST - getToken("POST", httpurl, json)
    String schema = "WECHATPAY2-SHA256-RSA2048";
    private static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();
    private PrivateKey yourPrivateKey=null;

    /**
     * 这个是组Authorization认证头结构
     * @param method
     * @param url
     * @param body
     * @return
     * @throws UnsupportedEncodingException
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private String getToken(String method, HttpUrl url, String body) throws UnsupportedEncodingException, SignatureException, NoSuchAlgorithmException, InvalidKeyException
    {
        String nonceStr = generateNonceStr();
        long timestamp = System.currentTimeMillis() / 1000;
        String message = buildMessage(method, url, timestamp, nonceStr, body);
        String signature = sign(message.getBytes("utf-8"));
        String yourCertificateSerialNo="14F4050F50EB3E3E500C14F49E2CAC52FBA2CDE0";//证书序列号

        //证书商户号
        String MerchantId="1277291801";

        return "mchid=\"" + MerchantId + "\","
                + "nonce_str=\"" + nonceStr + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + yourCertificateSerialNo + "\","
                + "signature=\"" + signature + "\"";
    }

    /**
     * 这个是签名
     * @param message
     * @return
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     * @throws InvalidKeyException
     */
    private  String sign(byte[] message) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException
    {
        Signature sign = Signature.getInstance("SHA256withRSA");

        String privateKeyStr="/1277291801.p12";

        //不用每次都获取，这个是不变的
        if (yourPrivateKey == null)
        {
            yourPrivateKey=loadPrivateKey_file(privateKeyStr);
        }

        sign.initSign(yourPrivateKey);
        sign.update(message);

        return Base64.getEncoder().encodeToString(sign.sign());
    }

    /**
     * 这个是组签名的结构
     * @param method
     * @param url
     * @param timestamp
     * @param nonceStr
     * @param body
     * @return
     */
    private String buildMessage(String method, HttpUrl url, long timestamp, String nonceStr, String body)
    {
        String canonicalUrl = url.encodedPath();
        if (url.encodedQuery() != null) {
            canonicalUrl += "?" + url.encodedQuery();
        }

        return method + "\n"
                + canonicalUrl + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + body + "\n";
    }


    /**
     * .p12证书文件，密码是商户号mid
     * @param fileName
     * @return
     */
    private PrivateKey loadPrivateKey_file(String fileName)
    {
        try
        {
            KeyStore keystore = KeyStore.getInstance("PKCS12");

            //没密码，就传null
            //有就传password.toCharArray()

            //密码是商户号mid
            String password="1277291801";

            keystore.load(this.getClass().getResourceAsStream(fileName), password.toCharArray());

            String keyAlias=null;
            Enumeration enumeration=keystore.aliases();
            if (enumeration.hasMoreElements())
            {
                keyAlias=(String) enumeration.nextElement();
            }

            //获取key
            PrivateKey key = (PrivateKey)keystore.getKey(keyAlias, password.toCharArray());


            return  key;
        }
        catch (Exception e)
        {
            throw new RuntimeException("加载证书密钥异常:"+e.getMessage());
        }
    }

    /**
     * 产生随机数
     * @return
     */
    private String generateNonceStr()
    {
        char[] nonceChars = new char[12];
        for (int index = 0; index < nonceChars.length; ++index)
        {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(nonceChars);
    }








}
