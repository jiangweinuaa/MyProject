package com.dsc.spos.scheduler.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.dsc.spos.config.SPosConfig;
import com.dsc.spos.json.cust.req.DCP_ReceivingCreateReq;
import com.dsc.spos.json.cust.res.DCP_ReceivingCreateRes;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.imp.json.DCP_ReceivingCreate;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import org.apache.commons.codec.digest.DigestUtils;
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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.dsc.spos.config.SPosConfig.ProdInterface;

import java.util.*;

/**
 *
 * 配送收货单从锐翔系统获取
 *
 * RuiXiang_DeliveryGet
 * {"startTime":"","endTime":""}
 *
 * 增加：转配送收货通知单失败的锐翔配送单号保存到Hash缓存中，定时处理失败单据
 *
 * RuiXiang_DeliveryGet_Exception 小key  锐翔的单号docno
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class RuiXiangDeliverysGet extends InitJob
{

    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(RuiXiangDeliverysGet.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public  RuiXiangDeliverysGet()
    {
    }

    public  RuiXiangDeliverysGet(String eId,String shopId,String organizationNO, String billNo)
    {
        pEId=eId;
        pShop=shopId;
        pOrganizationNO=organizationNO;
        pBillNo=billNo;
    }


    public String doExe()  throws Exception
    {
        //返回信息
        String sReturnInfo="";

        //此服务是否正在执行中
        if (bRun && pEId.equals(""))
        {
            logger.info("\r\n*********配送收货单从锐翔系统获取RuiXiangDeliverysGet正在执行中,本次调用取消:************\r\n");

            sReturnInfo="定时传输任务-配送收货单从锐翔系统获取RuiXiangDeliverysGet正在执行中！";
            return sReturnInfo;
        }

        //锐翔测试环境
        //地址：https://oms.ebake.net/API/WebApi/zj_v/ApiServers.ashx?action=GetPSGapDate
        //秘钥：0827b188913311e513c8
        String RuiXiang_Url=StaticInfo.RuiXiang_Url;
        String RuiXiang_Secret=StaticInfo.RuiXiang_Secret;
        String ruiXiang_appid=StaticInfo.RuiXiang_Appid;
        String ruiXiang_eid=StaticInfo.RuiXiang_Eid;

        if (Check.Null(RuiXiang_Url) || Check.Null(RuiXiang_Secret)|| Check.Null(ruiXiang_appid)|| Check.Null(ruiXiang_eid))
        {
            sReturnInfo="定时传输任务-配送收货单从锐翔系统获取RuiXiangDeliverysGet配置文件参数RuiXiang_Url、RuiXiang_Secret、ruiXiang_appid、ruiXiang_eid不能为空！";
            logger.error("\r\n*********"+sReturnInfo+"************\r\n");
            return sReturnInfo;
        }

        String action="action";
        String serviceName="GetPSGapDate";

        //排序拼接
        StringBuffer sb_Url=new StringBuffer(RuiXiang_Url+"?");

        bRun=true;//

        logger.info("\r\n*********配送收货单从锐翔系统获取RuiXiangDeliverysGet定时调用Start:************\r\n");

        RedisPosPub redis=new RedisPosPub();
        JSONObject jsonRedis=new JSONObject();

        try
        {
            String values=redis.getString("RuiXiang_DeliveryGet");
            String startTime="";
            String curTime= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String endTime="";
            if (Check.Null(values))
            {
                //减一天
                startTime= LocalDateTime.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyyMMdd")) +"000000";
                endTime=curTime;
            }
            else
            {
                JSONObject jsonObject=new JSONObject(values);
                startTime=jsonObject.get("startTime").toString();
                endTime=jsonObject.get("endTime").toString();

                //开始时间=结束时间 说明那个时间段已经没记录了
                if (startTime.equals(endTime))
                {
                    endTime=curTime;
                }
            }

            //通过配置文件读取
            String langtype="zh_CN";
            List<ProdInterface> lstProd=StaticInfo.psc.getT100Interface().getProdInterface();
            if(lstProd!=null&&!lstProd.isEmpty())
            {
                langtype=lstProd.get(0).getHostLang().getValue();
            }

            //缓存参数格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            //RuiXiang接口参数格式
            SimpleDateFormat sdf_RuiXiang=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //年月日格式
            SimpleDateFormat sdf_Date = new SimpleDateFormat("yyyyMMdd");
            //时分秒格式
            SimpleDateFormat sdf_Time = new SimpleDateFormat("HHmmss");

            //年月日时分秒毫秒
            SimpleDateFormat sdf_ms = new SimpleDateFormat("yyyyMMddHHmmss");

            //签名秘钥
            StringBuffer RuiXiang_sign=new StringBuffer(RuiXiang_Secret);

            //排序字段處理
            Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);
            maps.put(action, serviceName);

            String appid=ruiXiang_appid;
            maps.put("appid", appid);

            //开始时间
            String sday=sdf_RuiXiang.format(sdf.parse(startTime));
            maps.put("sday", sday);

            //结束时间
            String eday=sdf_RuiXiang.format(sdf.parse(endTime));
            maps.put("eday", eday);

            //时间戳
            String timestamp=String.valueOf(System.currentTimeMillis()/1000);
            maps.put("timestamp", timestamp);

            Iterator it = maps.entrySet().iterator();
            while (it.hasNext())
            {
                // entry的输出结果如key0=value0等
                Map.Entry entry =(Map.Entry) it.next();
                Object key = entry.getKey();
                Object value=entry.getValue();

                //签名不转码
                RuiXiang_sign.append(key).append(value);
                //地址转码
                sb_Url.append(key+"=" + URLEncoder.encode(value.toString(),"UTF-8") +"&");
            }
            //& 干掉
            sb_Url.delete(sb_Url.length()-1,sb_Url.length());

            //计算签名
            String sign= DigestUtils.md5Hex(RuiXiang_sign.toString());
            sign=sign.toUpperCase();

            //拼接上sign
            sb_Url.append("&sign=").append(sign);
            RuiXiang_Url=sb_Url.toString();

            logger.info("\r\n*********配送收货单从锐翔系统获取RuiXiangDeliverysGet定时调用请求:"+RuiXiang_Url+"************\r\n");

            String resBody= HttpSend.Sendhttp("Get", "", RuiXiang_Url);

            if (Check.Null(resBody))
            {
                logger.error("\r\n*********配送收货单从锐翔系统获取RuiXiangDeliverysGet 报错，返回为空************\r\n");
            }
            else
            {
                //html错误，非json
                //简单一单搞吧，也不想搞try啦
                if (resBody.startsWith("{") && resBody.endsWith("}"))
                {
                    JSONObject res=new JSONObject(resBody);
                    String resCode=res.get("code").toString();

                    if (resCode.equals("100000"))
                    {
                        JSONArray datas=new JSONArray();
                        if (res.has("data"))
                        {
                            datas=res.getJSONArray("data");
                        }

                        for (int i = 0; i < datas.length(); i++)
                        {
                            //标记单据保存处理成功
                            boolean bill_ok=false;
                            String a_billno="";
                            String error="";

                            try
                            {
                                JSONObject maininfo=datas.getJSONObject(i).getJSONObject("maininfo");
                                a_billno=maininfo.get("number").toString();//单号
                                String eId =ruiXiang_eid;//企业编码
                                String shopId =maininfo.get("JgdmIn").toString();//门店
                                String notes =maininfo.get("notes").toString();//备注
                                String warehouse ="";//maininfo.get("wrhcodeIn").toString();//仓库
                                String dbdlx =maininfo.get("dbdlx").toString();//调拨单类型 10:配送单  12:配送差异单
                                String wrhcodeOut =maininfo.get("wrhcodeOut").toString();//配送仓位，这个退货的时候会用到

                                //差异单不要
                                if (dbdlx.equals("12"))
                                {
                                   continue;
                                }

                                String sql_Incostwarehouse ="select IN_COST_WAREHOUSE from DCP_ORG where EID='"+eId+"' and organizationno='"+shopId+"' and STATUS='100' ";
                                List<Map<String, Object>> getIncostWarehouse = this.doQueryData(sql_Incostwarehouse, null);
                                if (getIncostWarehouse != null && getIncostWarehouse.size()>0)
                                {
                                    warehouse=getIncostWarehouse.get(0).get("IN_COST_WAREHOUSE").toString();
                                }

                                //调用收货通知单
//                                DCP_ReceivingCreateReq dcp_ReceivingCreateReq=new DCP_ReceivingCreateReq();
//                                dcp_ReceivingCreateReq.setServiceId("DCP_ReceivingCreate");
//                                dcp_ReceivingCreateReq.seteId(eId);
//                                dcp_ReceivingCreateReq.setShopId(shopId);
//                                dcp_ReceivingCreateReq.setRemark(notes);
//                                dcp_ReceivingCreateReq.setLoadDocType("0");
//                                dcp_ReceivingCreateReq.setLoadDocNO(a_billno);
//                                dcp_ReceivingCreateReq.setpTemplateNO("");
//                                dcp_ReceivingCreateReq.setSupplier("");
//                                dcp_ReceivingCreateReq.setLoadReceiptNO("");
//                                dcp_ReceivingCreateReq.setTransfer_shop("");
//                                dcp_ReceivingCreateReq.setReceipt_date("");
//                                dcp_ReceivingCreateReq.setDelivery_no("");
//                                dcp_ReceivingCreateReq.setPackingNo("");
//                                dcp_ReceivingCreateReq.setrDate("");
//                                dcp_ReceivingCreateReq.setRxWrhCode(wrhcodeOut);

                                //商品明细
                                List<Map<String, String>> detailMap=new ArrayList<>();

                                JSONArray detailInfo=datas.getJSONObject(i).getJSONArray("details");
                                for (int j = 0; j < detailInfo.length(); j++)
                                {
                                    //主单位是最小单位
                                    //辅单位数=主单位数/zhhl
                                    Map<String, String> receivingDetailMap = new HashMap<String, String>();
                                    receivingDetailMap.put("item",detailInfo.getJSONObject(j).get("id").toString());
                                    receivingDetailMap.put("oItem","0");
                                    receivingDetailMap.put("oType","");
                                    receivingDetailMap.put("ofNO","");
                                    //锐翔品号要转换
                                    String goodsMemo=detailInfo.getJSONObject(j).get("code").toString();
                                    String pluno="";

                                    //查找商品表
                                    String sql_Goods="select PLUNO from dcp_goods where eid='"+ruiXiang_eid+"' and memo='"+goodsMemo+"'";
                                    List<Map<String, Object>> sqlGoods=this.doQueryData(sql_Goods, null);
                                    if (sqlGoods != null && sqlGoods.size()>0)
                                    {
                                        pluno=sqlGoods.get(0).get("PLUNO").toString();
                                    }
                                    else
                                    {
                                        logger.error("\r\n*********配送收货单从锐翔系统获取RuiXiangDeliverysGet 报错，配送单号："+a_billno+",品号"+goodsMemo+",在鼎捷dcp_goods.memo中找不到************\r\n");
                                        bill_ok=false;
                                        error="*********配送收货单从锐翔系统获取RuiXiangDeliverysGet 报错，配送单号："+a_billno+",品号"+goodsMemo+",在鼎捷dcp_goods.memo中找不到************";
                                        continue;
                                    }

                                    //数值类型toString和toPlainString是有区别的，要转换，不然是指数形式
                                    receivingDetailMap.put("pluNO",pluno);
                                    receivingDetailMap.put("pluBarcode",pluno);
                                    receivingDetailMap.put("featureNO"," ");
                                    receivingDetailMap.put("featureName","");
                                    receivingDetailMap.put("punit", PosPub.F_UNIT_RXTODIGIWIN(detailInfo.getJSONObject(j).get("Munits").toString()));//辅单位
                                    receivingDetailMap.put("pqty",new BigDecimal(detailInfo.getJSONObject(j).get("Mqty").toString()).toPlainString());
                                    receivingDetailMap.put("baseUnit",PosPub.F_UNIT_RXTODIGIWIN(detailInfo.getJSONObject(j).get("units").toString()));//主单位
                                    receivingDetailMap.put("baseQty",new BigDecimal(detailInfo.getJSONObject(j).get("qty").toString()).toPlainString());
                                    receivingDetailMap.put("unitRatio", new BigDecimal(detailInfo.getJSONObject(j).get("zhhl").toString()).toPlainString());
                                    receivingDetailMap.put("poQty","0");//要货申请数量
                                    receivingDetailMap.put("price",new BigDecimal(detailInfo.getJSONObject(j).get("Mcosto").toString()).toPlainString());//辅单位价格
                                    receivingDetailMap.put("amt",new BigDecimal(detailInfo.getJSONObject(j).get("Lpriceout").toString()).toPlainString());
                                    receivingDetailMap.put("distriPrice",new BigDecimal(detailInfo.getJSONObject(j).get("Mcosti").toString()).toPlainString());
                                    receivingDetailMap.put("distriAmt",new BigDecimal(detailInfo.getJSONObject(j).get("Lpricein").toString()).toPlainString());
                                    receivingDetailMap.put("PROC_RATE","");//超交率
                                    receivingDetailMap.put("WAREHOUSE",warehouse);//
                                    receivingDetailMap.put("PLU_MEMO",detailInfo.getJSONObject(j).get("notes").toString());//商品说明
                                    receivingDetailMap.put("batchNO","");//
                                    receivingDetailMap.put("prodDate","");
//                                    //锐翔格式：2022-02-28 00:00:00
//                                    String proddate=detailInfo.getJSONObject(j).get("proddate").toString();
//                                    if (Check.Null(proddate) || proddate.length()!=19)
//                                    {
//                                        receivingDetailMap.put("prodDate","");
//                                    }
//                                    else
//                                    {
//                                        receivingDetailMap.put("prodDate",sdf_Date.format(sdf_RuiXiang.parse(proddate)));
//                                    }
                                    receivingDetailMap.put("packingNo","");
                                    detailMap.add(receivingDetailMap);
                                }

//                                dcp_ReceivingCreateReq.setDatas(detailMap);
//
//                                DCP_ReceivingCreate dcp_ReceivingCreate=new DCP_ReceivingCreate();
//                                dcp_ReceivingCreate.setDao(StaticInfo.dao);
//
//                                DCP_ReceivingCreateRes dcp_ReceivingCreateRes=new DCP_ReceivingCreateRes();
//                                dcp_ReceivingCreate.processDUID(dcp_ReceivingCreateReq,dcp_ReceivingCreateRes);
//

                                //单据保存结果
//                                bill_ok=dcp_ReceivingCreateRes.isSuccess();
//
//                                //错误描述
//                                if (!bill_ok)
//                                {
//                                   error=dcp_ReceivingCreateRes.getServiceDescription();
//                                }
//
//                                dcp_ReceivingCreateRes=null;
//                                dcp_ReceivingCreate=null;

                            }
                            catch (Exception e)
                            {
                                bill_ok=false;

                                try
                                {
                                    StringWriter errors = new StringWriter();
                                    PrintWriter pw=new PrintWriter(errors);
                                    e.printStackTrace(pw);

                                    pw.flush();
                                    pw.close();

                                    errors.flush();
                                    errors.close();

                                    logger.error("\r\n******配送收货单从锐翔系统获取RuiXiangDeliverysGet报错单号:"+a_billno + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                                    error="******配送收货单从锐翔系统获取RuiXiangDeliverysGet报错单号:"+a_billno + e.getMessage()+" " + errors.toString() + "******";

                                    pw=null;
                                    errors=null;
                                }
                                catch (IOException e1)
                                {
                                    logger.error("\r\n******配送收货单从锐翔系统获取RuiXiangDeliverysGet报错单号:"+a_billno + e.getMessage() + "******\r\n");

                                    error="******配送收货单从锐翔系统获取RuiXiangDeliverysGet报错单号:"+a_billno + e.getMessage() + "******";
                                }
                            }
                            finally
                            {
                                //处理失败，保存失败的单号缓存记录
                                if (!bill_ok)
                                {
                                    JSONObject jsonExceptionRedis=new JSONObject();
                                    jsonExceptionRedis.put("shipNo",a_billno);
                                    jsonExceptionRedis.put("error",error);
                                    jsonExceptionRedis.put("ruixiang",resBody);
                                    redis.setHashMap("RuiXiang_DeliveryGet_Exception",a_billno,jsonExceptionRedis.toString());
                                }
                            }
                        }
                        //处理成功更新缓存
                        jsonRedis.put("startTime",endTime);
                        jsonRedis.put("endTime",endTime);
                        redis.setString("RuiXiang_DeliveryGet",jsonRedis.toString());
                    }
                    else if (resCode.equals("100006")) //查询数据不存在或数据未审核,当成功处理，只是没有单据
                    {
                        //处理成功更新缓存
                        jsonRedis.put("startTime",endTime);
                        jsonRedis.put("endTime",endTime);
                        redis.setString("RuiXiang_DeliveryGet",jsonRedis.toString());
                    }
                    else
                    {
                        logger.error("\r\n*********配送收货单从锐翔系统获取RuiXiangDeliverysGet 报错，返回"+resBody+"************\r\n");
                    }

                }
                else
                {
                    logger.error("\r\n*********配送收货单从锐翔系统获取RuiXiangDeliverysGet 报错，返回为"+resBody+"************\r\n");
                }

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

                logger.error("\r\n******配送收货单从锐翔系统获取RuiXiangDeliverysGet报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******配送收货单从锐翔系统获取RuiXiangDeliverysGet报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();
        }
        finally
        {
            bRun=false;//
            logger.info("\r\n*********配送收货单从锐翔系统获取RuiXiangDeliverysGet定时调用End:************\r\n");
        }
        redis.Close();
        return sReturnInfo;
    }


}


