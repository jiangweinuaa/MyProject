package com.dsc.spos.service.webhook;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.dsc.spos.scheduler.job.OrderPostClient;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.thirdpart.qimai.QiMaiService;
import com.dsc.spos.thirdpart.youzan.YouZanUtilsV3;
import com.dsc.spos.utils.BigDecimalUtils;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.StringUtil;
import microsoft.exchange.webservices.data.core.IFileAttachmentContentHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 * 全部废弃掉,同步库存又不是同步库存异动，跟billno有什么关系
 * 改用 WebHookStockSyncServiceV2  by jw 2013-11-16
 */
public class WebHookStockSyncService {

    String logFileName = "stockSync";
    public void execute(String eId,String shopId,String billNo) throws Exception
    {
        List<Map<String,Object>> maps = WebHookService.GetWebHookSettings(eId, WebHookEventEnum.STOCKSYNC.name());
        if (maps==null||maps.isEmpty())
        {
            return;
        }
        for(int i=0;i<maps.size();i++)
        {
            logFileName = "stockSync";
            Map<String,Object> map=maps.get(i);
            String FORMAT = map.get("FORMAT").toString();
            String URL = map.get("URL").toString();
            try
            {
                if (FORMAT==null||FORMAT.trim().isEmpty())
                {
                    continue;
                }
                logFileName = FORMAT.toLowerCase()+"-"+logFileName;
                if ("QIMAI".equalsIgnoreCase(FORMAT)||"YOUZAN".equalsIgnoreCase(FORMAT))
                {
                    executeStock(eId,shopId,billNo,FORMAT,URL);
                }
                else
                {
                    continue;
                }

            }
            catch (Exception e)
            {
                continue;
            }

        }



    }


    public void executeStock(String eId,String shopId,String billNo,String format,String url) throws Exception
    {
        try
        {
            String appId_youzan = "";
            if ("YOUZAN".equalsIgnoreCase(format))
            {
                if (eId.isEmpty()||shopId.isEmpty()||billNo.isEmpty()||url.isEmpty())
                {
                    WebHookUtils.saveLog(logFileName,"eId||shopId||billNo||url存在为空的传值，"+String.join("||",eId,shopId,billNo,url));
                    return;
                }
                YouZanUtilsV3 utils=new YouZanUtilsV3();
                Map<String, Object> paramMap =new HashMap<String, Object>();
                paramMap.put("EID", eId);
                List<Map<String,Object>> dcpECMaps = utils.getYouZanList(paramMap);
                if (dcpECMaps==null||dcpECMaps.isEmpty())
                {
                    WebHookUtils.saveLog(logFileName,"有赞对接DCP_ECOMMERCE资料表未配置!");
                    return;
                }
                appId_youzan = dcpECMaps.get(0).getOrDefault("APIKEY","").toString();
                if (appId_youzan.isEmpty()||appId_youzan.trim().isEmpty())
                {
                    WebHookUtils.saveLog(logFileName,"有赞对接DCP_ECOMMERCE资料表appId(对应APIKEY字段)为空!");
                    return;
                }

            }
            if (StrUtil.hasEmpty(eId,shopId,billNo))
            {
                WebHookUtils.saveLog(logFileName,"eId||shopId||billNo存在为空的传值，"+StrUtil.join("||",eId,shopId,billNo));
                return;
            }

            StringBuffer strBuffer = new StringBuffer("");
            strBuffer.append(" with  P  as (");
            strBuffer.append(" select A.* from dcp_stock_detail A ");
            strBuffer.append(" inner join  Dcp_Goodstemplate_Goods G on A.EID=G.EID AND A.PLUNO=G.PLUNO AND G.STATUS='100' ");
            strBuffer.append(" inner join  DCP_GOODSTEMPLATE GT on GT.EID=G.EID AND GT.TEMPLATEID=G.TEMPLATEID AND GT.STATUS=G.STATUS ");
            strBuffer.append(" inner join  DCP_GOODSTEMPLATE_RANGE GR on GR.EID=GT.EID AND GR.TEMPLATEID=GT.TEMPLATEID AND GR.RANGETYPE='3' ");
            strBuffer.append(" AND exists (select 8 from CRM_CHANNEL WHERE EID=GR.EID AND CHANNELID=GR.ID AND APPNO='"+format+"')");
            strBuffer.append(" where A.EID='"+eId+"' and A.ORGANIZATIONNO='"+shopId+"' and A.BILLNO='"+billNo+"' ");
            strBuffer.append(" )");
            strBuffer.append(" select B.* FROM  P ");
            strBuffer.append(" inner join dcp_stock B on P.EID=B.EID AND P.ORGANIZATIONNO=B.ORGANIZATIONNO AND P.WAREHOUSE=B.WAREHOUSE AND P.PLUNO=B.PLUNO AND P.FEATURENO=B.FEATURENO");
            //strBuffer.append(" where P.EID='"+eId+"' and P.ORGANIZATIONNO='"+shopId+"' and P.BILLNO='"+billNo+"' ");
            String sql = strBuffer.toString();
            WebHookUtils.saveLog(logFileName,"查询需要同步的商品信息sql:"+sql);
            List<Map<String, Object>> maps = StaticInfo.dao.executeQuerySQL(sql,null);
            if (CollectionUtil.isEmpty(maps))
            {
                WebHookUtils.saveLog(logFileName,"查询需要同步的商品信息为空,库存异动单号:"+billNo);
                return;
            }
            if ("QIMAI".equalsIgnoreCase(format))
            {
                executeStock_qimai(eId,shopId,billNo,maps);
            }
            else if ("YOUZAN".equalsIgnoreCase(format))
            {
                executeStock_youzan(eId,shopId,billNo,url,appId_youzan,maps);
            }

        }
        catch (Exception e)
        {
            WebHookUtils.saveLog(logFileName,"库存同步异常:"+e.getMessage()+",库存异动单号:"+billNo);
        }


    }

    private void executeStock_qimai(String eId,String shopId,String billNo,List<Map<String, Object>> maps) throws Exception
    {
        try
        {
            Map<String, Object> setMap = new HashMap<>();
            setMap.put("EID",eId);
            setMap.put("CHANNELID","QIMAI001");//默认
            //获取渠道id,根据不同门店获取
            String appType = "QIMAI";
            String channelId = this.getChannelId(eId,shopId,appType);
            if (channelId!=null&&!channelId.trim().isEmpty())
            {
                setMap.put("CHANNELID",channelId);
            }
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("shopCode",shopId);
            List<com.dsc.spos.thirdpart.qimai.updateStockItem> items = new ArrayList<>();
            for (Map<String, Object> map : maps)
            {
                String pluNo = map.get("PLUNO").toString();
                String qty = map.get("QTY").toString();
                String lockQty = map.getOrDefault("LOCKQTY","0").toString();
                BigDecimal qty_b = new BigDecimal(qty);
                //实时库存-库存锁定
                try
                {
                    BigDecimal lockQty_b = new BigDecimal(lockQty);
                    if (lockQty_b.compareTo(BigDecimal.ZERO)==-1)
                    {
                        //防止库存锁定数 小于0
                        lockQty_b = new BigDecimal("0");
                    }
                    else
                    {
                        qty_b = qty_b.subtract(lockQty_b);
                    }
                }
                catch (Exception e)
                {

                }

                if (qty_b.compareTo(BigDecimal.ZERO)==-1)
                {
                    qty_b = new BigDecimal("0");
                }

                /*int qty_int = qty_b.setScale(0,BigDecimal.ROUND_CEILING).intValue();
                if (qty_int<0)
                {
                    qty_int = 0;
                }*/
                double qty_d = qty_b.setScale(3,BigDecimal.ROUND_HALF_UP).doubleValue();
                com.dsc.spos.thirdpart.qimai.updateStockItem item = new com.dsc.spos.thirdpart.qimai.updateStockItem();
                //item.setStock(qty_int);
                item.setStock(qty_d);
                item.setTrade_mark(pluNo);
                items.add(item);
            }
            paraMap.put("items",items);
            QiMaiService.getInstance().updateStock(setMap, paraMap);
        }
        catch (Exception e)
        {
            WebHookUtils.saveLog(logFileName,"库存同步异常:"+e.getMessage()+",库存异动单号:"+billNo);
        }
    }

    private void executeStock_youzan(String eId,String shopId,String billNo,String url,String appId,List<Map<String, Object>> maps) throws Exception
    {
        try
        {

            org.json.JSONObject request = new JSONObject();
            request.put("eId",eId);
            request.put("shopNo",shopId);
            request.put("appId",appId);
            org.json.JSONArray items = new JSONArray();
            for (Map<String, Object> map : maps)
            {
                String pluNo = map.get("PLUNO").toString();
                String wqty = map.get("QTY").toString();
                String wUnit = map.get("BASEUNIT").toString();
                BigDecimal qty_b = new BigDecimal(wqty);
                if (qty_b.compareTo(BigDecimal.ZERO)==-1)
                {
                    qty_b = new BigDecimal("0");
                }
                /*int qty_int = qty_b.setScale(0,BigDecimal.ROUND_CEILING).intValue();
                if (qty_int<0)
                {
                    qty_int = 0;
                }*/
                double qty_d = qty_b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();

                org.json.JSONObject obj = new JSONObject();
                obj.put("pluNo",pluNo);
                obj.put("stock",qty_d+"");
                obj.put("wUnit",wUnit);
                items.put(obj);
            }
            request.put("items",items);

            org.json.JSONObject sign = new JSONObject();
            String key = "digiwin";
            String secret = "digiwin";
            String signStr = PosPub.encodeMD5(request.toString()+secret);
            sign.put("key",key);
            sign.put("sign",signStr);

            org.json.JSONObject req = new JSONObject();
            req.put("request",request);
            req.put("sign",sign);
            req.put("requestId", PosPub.getGUID(false)+"-"+billNo);
            req.put("timestamp", System.currentTimeMillis());
            String reqStr = req.toString();
            Map<String, Object> headersMap = new HashMap<String, Object>();
            headersMap.put("Content-Type", "application/json;charset=UTF-8");
            WebHookUtils.saveLog(logFileName,"库存同步请求url:"+url+",请求req:"+reqStr+",库存异动单号:"+billNo);
            String resStr = OrderPostClient.sendSoapPost("",url,headersMap,reqStr);
            WebHookUtils.saveLog(logFileName,"库存同步请求返回:"+resStr+",库存异动单号:"+billNo);

        }
        catch (Exception e)
        {
            WebHookUtils.saveLog(logFileName,"库存同步异常:"+e.getMessage()+",库存异动单号:"+billNo);
        }

    }

    /**
     * 根据接口账号设置的指定门店获取相应的渠道
     * @param eId
     * @param shopId
     * @param appType
     * @return
     * @throws Exception
     */
    private String getChannelId(String eId,String shopId,String appType) throws Exception
    {
        String channelId = "";
        try
        {
            String sql = " Select A.CHANNELID from CRM_APIUSER A left join CRM_CHANNEL B on A.EID=B.EID AND A.CHANNELID=B.CHANNELID and A.STATUS=B.STATUS "
                       + " left join CRM_APIUSERSHOP C on C.EID=A.EID AND C.USERCODE=A.USERCODE "
                       + " WHERE A.EID='"+eId+"' AND A.APPTYPE='"+appType+"' AND A.STATUS='100' AND C.SHOPID='"+shopId+"'";
            List<Map<String, Object>> maps = StaticInfo.dao.executeQuerySQL(sql,null);
            if (CollectionUtil.isNotEmpty(maps))
            {
                channelId = maps.get(0).getOrDefault("CHANNELID","").toString();
            }
        }
        catch (Exception e)
        {

        }
        return channelId;
    }


}
