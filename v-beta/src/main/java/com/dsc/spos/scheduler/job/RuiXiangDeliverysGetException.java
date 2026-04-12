package com.dsc.spos.scheduler.job;

import com.alibaba.fastjson.JSON;
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
import java.util.*;

/**
 *  转配送收货通知单失败的锐翔配送单号
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class RuiXiangDeliverysGetException extends InitJob
{

    //******兼容即时服务的,只查询指定的那张单据******
    String pEId="";
    String pShop="";
    String pOrganizationNO="";
    String pBillNo="";

    Logger logger = LogManager.getLogger(RuiXiangDeliverysGetException.class.getName());

    static boolean bRun=false;//标记此服务是否正在执行中

    public  RuiXiangDeliverysGetException()
    {
    }

    public  RuiXiangDeliverysGetException(String eId,String shopId,String organizationNO, String billNo)
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
            logger.info("\r\n*********获取锐翔系统异常配送收货单RuiXiangDeliverysGetException正在执行中,本次调用取消:************\r\n");

            sReturnInfo="定时传输任务-获取锐翔系统异常配送收货单RuiXiangDeliverysGetException正在执行中！";
            return sReturnInfo;
        }

        //锐翔测试环境
        //地址：https://oms.ebake.net/API/WebApi/zj_v/ApiServers.ashx?action=GetPSList
        //秘钥：20801eebd05b3bf3f8bd
        String RuiXiang_Url=StaticInfo.RuiXiang_Url;
        String RuiXiang_Secret=StaticInfo.RuiXiang_Secret;
        String ruiXiang_appid=StaticInfo.RuiXiang_Appid;
        String ruiXiang_eid=StaticInfo.RuiXiang_Eid;

        if (Check.Null(RuiXiang_Url) || Check.Null(RuiXiang_Secret)|| Check.Null(ruiXiang_appid)|| Check.Null(ruiXiang_eid))
        {
            sReturnInfo="定时传输任务-获取锐翔系统异常配送收货单RuiXiangDeliverysGetException配置文件参数RuiXiang_Url、RuiXiang_Secret、ruiXiang_appid、ruiXiang_eid不能为空！";
            logger.error("\r\n*********"+sReturnInfo+"************\r\n");
            return sReturnInfo;
        }

        String action="action";
        String serviceName="GetPSList";

        //排序拼接
        StringBuffer sb_Url=new StringBuffer(RuiXiang_Url+"?");
        sb_Url.append(action).append("=").append(serviceName);

        bRun=true;//

        logger.info("\r\n*********获取锐翔系统异常配送收货单RuiXiangDeliverysGetException定时调用Start:************\r\n");

        RedisPosPub redis=new RedisPosPub();


        try
        {
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

            String curTime= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

            Map<String, String> values=redis.getALLHashMap("RuiXiang_DeliveryGet_Exception");

            //单号批量
            StringBuffer numbers=new StringBuffer("[");

            Iterator<Map.Entry<String, String>> it = values.entrySet().iterator();
            int count=0;
            while(it.hasNext())
            {
                //一次5单
                if (count>5)
                {
                    break;
                }

                Map.Entry<String, String> entry = it.next();

                //System.out.println("key:"+entry.getKey()+" key:"+entry.getValue());

                //避免手误添加空行记录，直接过滤掉
                if (Check.Null(entry.getValue()))
                {
                    continue;
                }

                JSONObject jsonRedis=new JSONObject(entry.getValue());

                //处理一下发送标记,发送过，下次过滤掉,不能老发重复的
                if (!jsonRedis.has("send"))
                {
                    //单号批量
                    numbers.append("'" + jsonRedis.get("shipNo").toString()+"',");
                    count+=1;

                    //更新标记
                    jsonRedis.put("send","true");

                    //日期标记
                    if (!jsonRedis.has("sdate"))
                    {
                        jsonRedis.put("sdate",sdf_Date.format(new Date()));
                    }
                    redis.setHashMap("RuiXiang_DeliveryGet_Exception",jsonRedis.get("shipNo").toString(),jsonRedis.toString());
                }
            }

            if (count>0)
            {
                //逗号,删除
                numbers.delete(numbers.length()-1,numbers.length());
            }
            else
            {
                //全部都发过一遍了，重置标记
                Iterator<Map.Entry<String, String>> v_it = values.entrySet().iterator();
                while(v_it.hasNext())
                {
                    Map.Entry<String, String> entry = v_it.next();

                    //避免手误添加空行记录，直接过滤掉
                    if (Check.Null(entry.getValue()))
                    {
                        continue;
                    }

                    JSONObject jsonRedis=new JSONObject(entry.getValue());

                    if (jsonRedis.has("send"))
                    {
                        jsonRedis.remove("send");
                        redis.setHashMap("RuiXiang_DeliveryGet_Exception",jsonRedis.get("shipNo").toString(),jsonRedis.toString());
                    }

                    //三天之外的直接删除，避免异常缓存太大
                    if (jsonRedis.has("sdate"))
                    {
                        String v_sdate=jsonRedis.get("sdate").toString();

                        int days = (int) ((new Date().getTime()-sdf_Date.parse(v_sdate).getTime())/ (1000 * 60 * 60 * 24));

                        if (days>3)
                        {
                            redis.DeleteHkey("RuiXiang_DeliveryGet_Exception",jsonRedis.get("shipNo").toString());
                        }
                    }
                }


                sReturnInfo="定时传输任务-获取锐翔系统异常配送收货单RuiXiangDeliverysGetException无异常单据！";
                return sReturnInfo;
            }
            numbers.append("]");

            //签名秘钥
            StringBuffer RuiXiang_sign=new StringBuffer(RuiXiang_Secret);

            //排序字段處理
            Map<String,Object> maps=new TreeMap<String,Object>(String.CASE_INSENSITIVE_ORDER);
            maps.put(action, serviceName);

            String appid=ruiXiang_appid;
            maps.put("appid", appid);

            //单号批量
            maps.put("numbers", numbers);

            //时间戳
            String timestamp=String.valueOf(System.currentTimeMillis()/1000);
            maps.put("timestamp", timestamp);


            //这个是按照先进先出顺序的
            com.alibaba.fastjson.JSONObject  head=new com.alibaba.fastjson.JSONObject (true);

            Iterator it_M = maps.entrySet().iterator();
            while (it_M.hasNext())
            {
                // entry的输出结果如key0=value0等
                Map.Entry entry =(Map.Entry) it_M.next();
                Object key = entry.getKey();
                Object value=entry.getValue();

                //签名不转码
                RuiXiang_sign.append(key).append(value);

                //POST请求
                head.put(key.toString(),value.toString());
            }

            //计算签名
            String sign= DigestUtils.md5Hex(RuiXiang_sign.toString());
            sign=sign.toUpperCase();

            //签名
            head.put("sign",sign);

            RuiXiang_Url=sb_Url.toString();

            logger.info("\r\n*********获取锐翔系统异常配送收货单RuiXiangDeliverysGetException定时调用请求:"+head.toString()+"************\r\n");

            String resBody= HttpSend.Sendhttp("POST", head.toString(), RuiXiang_Url);
            if (Check.Null(resBody))
            {
                logger.error("\r\n*********获取锐翔系统异常配送收货单RuiXiangDeliverysGetException 请求锐翔系统报错，返回为空************\r\n");
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

                            try
                            {
                                JSONObject maininfo=datas.getJSONObject(i).getJSONObject("maininfo");
                                a_billno=maininfo.get("number").toString();//单号
                                String eId =ruiXiang_eid;//企业编码
                                String shopId =maininfo.get("JgdmIn").toString();//门店
                                String notes =maininfo.get("notes").toString();//备注
                                String warehouse ="";//maininfo.get("wrhcodeIn").toString();//仓库
                                String wrhcodeOut =maininfo.get("wrhcodeOut").toString();//配送仓位，这个退货的时候会用到

                                String sql_Incostwarehouse ="select IN_COST_WAREHOUSE from DCP_ORG where EID='"+eId+"' and organizationno='"+shopId+"' and STATUS='100' ";
                                List<Map<String, Object>> getIncostWarehouse = this.doQueryData(sql_Incostwarehouse, null);
                                if (getIncostWarehouse != null && getIncostWarehouse.size()>0)
                                {
                                    warehouse=getIncostWarehouse.get(0).get("IN_COST_WAREHOUSE").toString();
                                }

                                //调用收货通知单
                                DCP_ReceivingCreateReq dcp_ReceivingCreateReq=new DCP_ReceivingCreateReq();
                                dcp_ReceivingCreateReq.setServiceId("DCP_ReceivingCreate");
                                dcp_ReceivingCreateReq.seteId(eId);
                                dcp_ReceivingCreateReq.setShopId(shopId);
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
                                        logger.error("\r\n*********获取锐翔系统异常配送收货单RuiXiangDeliverysGetException 报错，配送单号："+a_billno+",品号"+goodsMemo+",在鼎捷dcp_goods.memo中找不到************\r\n");
                                        bill_ok=false;
                                        break;
                                    }

                                    receivingDetailMap.put("pluNO",pluno);
                                    receivingDetailMap.put("pluBarcode",pluno);
                                    receivingDetailMap.put("featureNO"," ");
                                    receivingDetailMap.put("featureName","");
                                    receivingDetailMap.put("punit", PosPub.F_UNIT_RXTODIGIWIN(detailInfo.getJSONObject(j).get("Munits").toString()));//辅单位
                                    receivingDetailMap.put("pqty", new BigDecimal(detailInfo.getJSONObject(j).get("Mqty").toString()).toPlainString());
                                    receivingDetailMap.put("baseUnit",PosPub.F_UNIT_RXTODIGIWIN(detailInfo.getJSONObject(j).get("units").toString()));//主单位
                                    receivingDetailMap.put("baseQty",new BigDecimal(detailInfo.getJSONObject(j).get("qty").toString()).toPlainString());
                                    receivingDetailMap.put("unitRatio",new BigDecimal(detailInfo.getJSONObject(j).get("zhhl").toString()).toPlainString());
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

//                                dcp_ReceivingCreateReq.getRequest()(detailMapt);

                                DCP_ReceivingCreate dcp_ReceivingCreate=new DCP_ReceivingCreate();
                                dcp_ReceivingCreate.setDao(StaticInfo.dao);

                                DCP_ReceivingCreateRes dcp_ReceivingCreateRes=new DCP_ReceivingCreateRes();
                                dcp_ReceivingCreate.processDUID(dcp_ReceivingCreateReq,dcp_ReceivingCreateRes);


                                //单据保存结果
                                bill_ok=dcp_ReceivingCreateRes.isSuccess();

                                dcp_ReceivingCreateRes=null;
                                dcp_ReceivingCreate=null;

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

                                    logger.error("\r\n******获取锐翔系统异常配送收货单RuiXiangDeliverysGetException报错单号:"+a_billno + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                                    pw=null;
                                    errors=null;
                                }
                                catch (IOException e1)
                                {
                                    logger.error("\r\n******获取锐翔系统异常配送收货单RuiXiangDeliverysGetException报错单号:"+a_billno + e.getMessage() + "******\r\n");
                                }
                            }
                            finally
                            {
                                //处理成功，直接从异常缓存单据中删除
                                if (bill_ok)
                                {
                                    redis.DeleteHkey("RuiXiang_DeliveryGet_Exception",a_billno);
                                }
                            }
                        }
                    }
                    else
                    {
                        logger.error("\r\n*********获取锐翔系统异常配送收货单RuiXiangDeliverysGetException 报错，返回"+resBody+"************\r\n");
                    }
                }
                else
                {
                    logger.error("\r\n*********获取锐翔系统异常配送收货单RuiXiangDeliverysGetException 请求锐翔系统报错，返回为"+resBody+"************\r\n");
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

                logger.error("\r\n******获取锐翔系统异常配送收货单RuiXiangDeliverysGetException报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                logger.error("\r\n******获取锐翔系统异常配送收货单RuiXiangDeliverysGetException报错信息" + e.getMessage() + "******\r\n");
            }

            //
            sReturnInfo="错误信息:" + e.getMessage();
        }
        finally
        {
            bRun=false;//
            logger.info("\r\n*********获取锐翔系统异常配送收货单RuiXiangDeliverysGetException定时调用End:************\r\n");
        }

        redis.Close();
        return sReturnInfo;
    }



}
