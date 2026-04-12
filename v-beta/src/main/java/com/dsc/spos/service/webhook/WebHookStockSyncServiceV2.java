package com.dsc.spos.service.webhook;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

import com.dsc.spos.redis.RedisPosPub;
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


public class WebHookStockSyncServiceV2 {

    String logFileName = "stockSync";
    
    private String eId;
    private String format;
    private String goodsquename;
    public WebHookStockSyncServiceV2(String EID,String FORMAT,String GOODSQUENAME)
    {
    	eId = EID;
    	format = FORMAT;
    	goodsquename = GOODSQUENAME;
    }
    
    
    
    public void BillToGoods(String shopId,String billNo)
    {
      try
      {
	        List<Map<String,Object>> settingmaps = WebHookService.GetWebHookSettings(eId, WebHookEventEnum.STOCKSYNC.name());
	        if (settingmaps==null||settingmaps.isEmpty())
	        {
	            return;
	        }

          if (StrUtil.hasEmpty(eId,shopId,billNo))
          {
              WebHookUtils.saveLog(logFileName,"eId||shopId||billNo存在为空的传值，"+StrUtil.join("||",eId,shopId,billNo));
              return;
          }

          StringBuffer strBuffer = new StringBuffer("");
          strBuffer.append(" select A.* from dcp_stock_detail A ");
          strBuffer.append(" inner join  Dcp_Goodstemplate_Goods G on A.EID=G.EID AND A.PLUNO=G.PLUNO AND G.STATUS='100' ");
          strBuffer.append(" inner join  DCP_GOODSTEMPLATE GT on GT.EID=G.EID AND GT.TEMPLATEID=G.TEMPLATEID AND GT.STATUS=G.STATUS ");
          strBuffer.append(" AND exists ( ");
          strBuffer.append(" select 8 from CRM_CHANNEL c ");
          strBuffer.append(" inner join DCP_GOODSTEMPLATE_RANGE GR on GR.EID = c.EID AND GR.ID = c.CHANNELID AND GR.RANGETYPE='3' ");
          strBuffer.append(" WHERE GR.EID=GT.EID AND GR.TEMPLATEID=GT.TEMPLATEID AND c.APPNO='"+format+"' AND c.STATUS = 100 ");
          strBuffer.append(" AND (c.RESTRICTSHOP = 0 or exists( ");
          strBuffer.append(" select 88 from CRM_CHANNELSHOP cs where cs.EID = c.EID and cs.CHANNELID = c.CHANNELID and cs.SHOPID = A.ORGANIZATIONNO");
          strBuffer.append(" )");
          strBuffer.append(" )");
          strBuffer.append(" )");
          strBuffer.append(" where A.EID='"+eId+"' and A.ORGANIZATIONNO='"+shopId+"' and A.BILLNO='"+billNo+"' ");
          String sql = strBuffer.toString();
          WebHookUtils.saveLog(logFileName,"查询需要同步的商品信息sql:"+sql);
          List<Map<String, Object>> maps = StaticInfo.dao.executeQuerySQL(sql,null);
          if (CollectionUtil.isEmpty(maps))
          {
              WebHookUtils.saveLog(logFileName,"查询需要同步的商品信息为空,库存异动单号:"+billNo);
              return;
          }
          
          RedisPosPub redis = new RedisPosPub();
          for(Map<String,Object> map : maps)
          {
	          String PLUNO = map.get("PLUNO").toString();
	          String FEATURENO = map.get("FEATURENO").toString();
	          String WAREHOUSE = map.get("WAREHOUSE").toString();
	          
	          String key = goodsquename;
	          String value = eId+"|"+shopId+"|"+WAREHOUSE+"|"+PLUNO+"|"+FEATURENO;
	          redis.lpush(key, value);
          }
          redis.Close();
          

      }
      catch (Exception e)
      {
          WebHookUtils.saveLog(logFileName,"库存同步异常:"+e.getMessage()+",库存异动单号:"+billNo);
      }
    	
    }
    
    
    public void executeGoods(String shopId,String wareHouse,String goodsId,String featureNo) throws Exception
    {
        Map<String,Object> map = WebHookService.GetWebHookSettings(eId, WebHookEventEnum.STOCKSYNC.name(),format);
        if (map==null||map.isEmpty())
        {
        		WebHookUtils.saveLog(logFileName,"库存同步异常,品号:"+eId+"|"+shopId+"|"+goodsId+": WebHook未配置");
            return;
        }
        
        logFileName = "stockSync";       
        String FORMAT = map.get("FORMAT").toString();
        String URL = map.get("URL").toString();
        try
        {
            if (FORMAT==null||FORMAT.trim().isEmpty())
            {
                return;
            }
            logFileName = FORMAT.toLowerCase()+"-"+logFileName;

            if ("QIMAI".equalsIgnoreCase(FORMAT))
            {
            	executeStock_qimai(shopId,goodsId,wareHouse,featureNo);            	
            }

            if ("YOUZAN".equalsIgnoreCase(FORMAT))
            {
            	executeStock_youzan(shopId,goodsId,wareHouse,featureNo,URL);            	
            }

            WebHookUtils.saveLog(logFileName,"库存同步成功，品号:"+shopId+"|"+goodsId);            
        }
        catch (Exception e)
        {
        		WebHookUtils.saveLog(logFileName,"库存同步异常:"+e.getMessage()+",品号:"+shopId+"|"+goodsId+":"+e.getMessage());
            return;
        }

        



    }



    /*
     * 企迈是不分特征码的，所有这个地方要忽略特征码和仓库，汇总成品号的库存传企迈
     */
    private void executeStock_qimai(String shopId,String goodsId,String wareHouse,String featureNo) throws Exception
    {
        try
        {
          StringBuffer strBuffer = new StringBuffer("");
          strBuffer.append(" SELECT NVL(SUM(QTY),0) as QTY,NVL(SUM(LOCKQTY),0) as LOCKQTY FROM DCP_STOCK WHERE EID='"+eId+"' AND ORGANIZATIONNO='"+shopId+"' AND PLUNO='"+goodsId+"'");
          //strBuffer.append(" where P.EID='"+eId+"' and P.ORGANIZATIONNO='"+shopId+"' and P.BILLNO='"+billNo+"' ");
          String sql = strBuffer.toString();
          WebHookUtils.saveLog(logFileName,"查询需要同步的商品信息sql:"+sql);
          List<Map<String, Object>> maps = StaticInfo.dao.executeQuerySQL(sql,null);
          if (CollectionUtil.isEmpty(maps))
          {
              WebHookUtils.saveLog(logFileName,"查询需要同步的商品信息为空,品号:"+shopId+"_"+goodsId);
              return;
          }

        	
            Map<String, Object> setMap = new HashMap<>();
            setMap.put("EID",eId);
            setMap.put("CHANNELID","QIMAI001");//默认
            //获取渠道id,根据不同门店获取
            String appType = "QIMAI";
            String channelId = this.getChannelId(shopId,appType);
            if (channelId!=null&&!channelId.trim().isEmpty())
            {
                setMap.put("CHANNELID",channelId);
            }
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("shopCode",shopId);
            List<com.dsc.spos.thirdpart.qimai.updateStockItem> items = new ArrayList<>();
            for (Map<String, Object> map : maps)
            {
                String pluNo = goodsId;
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
            WebHookUtils.saveLog(logFileName,"库存同步异常:"+e.getMessage()+",品号:"+shopId+"_"+goodsId);
        }
    }

    /*
     * 多特征？ 同企迈。比企迈多了个单位
     */
    private void executeStock_youzan(String shopId,String goodsId,String wareHouse,String featureNo,String url) throws Exception
    {
        try
        {

          String appId_youzan = "";
          {
              if (eId.isEmpty()||shopId.isEmpty()||goodsId.isEmpty()||url.isEmpty())
              {
                  WebHookUtils.saveLog(logFileName,"eId||shopId||billNo||url存在为空的传值，"+String.join("||",eId,shopId,goodsId,url));
                  return;
              }
              YouZanUtilsV3 utils=new YouZanUtilsV3();
              Map<String, Object> paramMap =new HashMap<String, Object>();
              paramMap.put("EID", eId);
              List<Map<String,Object>> dcpECMaps = utils.getYouZanList(paramMap);	//又是这种东西，每个商品都去查一下配置表......
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
          if (StrUtil.hasEmpty(eId,shopId,goodsId,wareHouse,featureNo))
          {
              WebHookUtils.saveLog(logFileName,"eId||shopId||billNo存在为空的传值，"+StrUtil.join("||",eId,shopId,goodsId));
              return;
          }

        	
          StringBuffer strBuffer = new StringBuffer("");
          strBuffer.append(" SELECT PLUNO,BASEUNIT,NVL(SUM(QTY),0) as QTY,NVL(SUM(LOCKQTY),0) as LOCKQTY FROM DCP_STOCK WHERE EID='"+eId+"' AND ORGANIZATIONNO='"+shopId+"' AND PLUNO='"+goodsId+"'");          //strBuffer.append(" where P.EID='"+eId+"' and P.ORGANIZATIONNO='"+shopId+"' and P.BILLNO='"+billNo+"' ");
          strBuffer.append(" GROUP BY PLUNO,BASEUNIT");
          String sql = strBuffer.toString();
          WebHookUtils.saveLog(logFileName,"查询需要同步的商品信息sql:"+sql);
          List<Map<String, Object>> maps = StaticInfo.dao.executeQuerySQL(sql,null);
          if (CollectionUtil.isEmpty(maps))
          {
              WebHookUtils.saveLog(logFileName,"查询需要同步的商品信息为空,品号:"+shopId+"_"+goodsId);
              return;
          }


            org.json.JSONObject request = new JSONObject();
            request.put("eId",eId);
            request.put("shopNo",shopId);
            request.put("appId",appId_youzan);
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
            req.put("requestId", PosPub.getGUID(false)+"-"+goodsId);
            req.put("timestamp", System.currentTimeMillis());
            String reqStr = req.toString();
            Map<String, Object> headersMap = new HashMap<String, Object>();
            headersMap.put("Content-Type", "application/json;charset=UTF-8");
            WebHookUtils.saveLog(logFileName,"库存同步请求url:"+url+",请求req:"+reqStr+",库存品号:"+shopId+"_"+goodsId);
            String resStr = OrderPostClient.sendSoapPost("",url,headersMap,reqStr);
            WebHookUtils.saveLog(logFileName,"库存同步请求返回:"+resStr+",库存品号:"+shopId+"_"+goodsId);

        }
        catch (Exception e)
        {
            WebHookUtils.saveLog(logFileName,"库存同步异常:"+e.getMessage()+",库存品号:"+shopId+"_"+goodsId);
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
    private String getChannelId(String shopId,String appType) throws Exception
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
