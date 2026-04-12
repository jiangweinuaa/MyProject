package com.dsc.spos.service.webhook;

import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.redis.RedisUtil;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.Check;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebHookService {

    public static void Goods(String EID,String goodsid)
    {
        WebHookThread post = new WebHookThread();
        post.Goods(EID,goodsid);

        Thread thread = new Thread(post);
        thread.setDaemon(true);
        thread.start();
    }

    //用排程来定期执行
    public static void AllGoods(String EID)
    {
        WebHookThread post = new WebHookThread();
        post.AllGoods(EID);
        post.run();
		/*Thread thread = new Thread(post);
        thread.setDaemon(true);
        thread.start();*/
    }

    public static void stockSync(String EID,String shopId,String billNo)
    {
    		/*
        WebHookThread post = new WebHookThread();
        post.stockSync(EID,shopId,billNo);

        Thread thread = new Thread(post);
        thread.setDaemon(true);
        thread.start();
        */
    	
      List<Map<String,Object>> maps = WebHookService.GetWebHookSettings(EID, WebHookEventEnum.STOCKSYNC.name());
      if (maps==null||maps.isEmpty())
      {
          return;
      }

      //一个第三方开一个队列，起一个线程处理这个队列
      RedisPosPub redis = new RedisPosPub();
      String value = EID+"|"+shopId+"|"+billNo;
      for(Map<String,Object> map:maps)
      {
      	//写入队列（先进先出）
      	String FORMAT = map.get("FORMAT").toString();
      	String key = EID+"_"+FORMAT+"_"+StockSyncBillQueue;
      	redis.lpush(key, value);
      	
      	//判断线程是否存在,不存在就创建一个
      	String key2 = EID+"_"+FORMAT+"_"+StockSyncGoodsQueue;
      	stockSyncStart(EID,FORMAT,key,key2);
      	
      }
      redis.Close();      
    }

    
    private static Map<String,List<Map<String,Object>>> webHookSettingMap = new HashMap<String,List<Map<String,Object>>>();
    public static List<Map<String,Object>> GetWebHookSettings(String EID, String EventID)
    {
    	try
    	{
    		//做缓存处理，用不着每条商品都来读一次参数
    		String key = EID+"-"+EventID;
    		
    		if(webHookSettingMap.containsKey(key))
	    	{
	    		return webHookSettingMap.get(key);
	    	}
	    	
	    	
	        //String EventID = WebHookEnum.name();
	        String sql = "select b.* from NRC_WEBHOOK a join NRC_WEBHOOK_URL b"
	                + " on a.EID = b.EID and a.EVENTID = b.EVENTID "
	                + " where a.STATUS = 100 and b.STATUS = 100"
	                + " and a.EID = '"+EID+"' and a.EVENTID = '"+EventID+"'";
	        WebHookUtils.saveLog("webHookService","查询需要的同步事件sql:"+sql);
	        List<Map<String,Object>> maps = StaticInfo.dao.executeQuerySQL(sql,null);
	        if (maps.isEmpty())
	        {
	            WebHookUtils.saveLog("webHookService","查询需要的同步事件id为空，EventID:"+EventID);
	        }
	        webHookSettingMap.put(key, maps);
	        return maps;
	    	}
    	catch(Exception ex)
    	{
    		WebHookUtils.saveLog("webHookService","查询需要的同步事件id为空，EventID:"+ex.toString());
    		return null;
    	}

    }

    public static Map<String,Object> GetWebHookSettings(String EID, String EventID,String FORMAT)
    {
    	try
    	{
    		List<Map<String,Object>> maps = GetWebHookSettings(EID,EventID);
    		for(Map<String,Object> map : maps)
    		{    			
    			String format = map.get("FORMAT").toString();
    			if(FORMAT.equals(format))
    			{
    				return map;
    			}
    		}
    		return null;

    	}
    	catch(Exception ex)
    	{
    		WebHookUtils.saveLog("webHookService","查询需要的同步事件id为空，EventID:"+ex.toString());
    		return null;
    	}

    }

    
    /*
     * 起一个线程去监控需要同步的队列
     */
    private static Map<String,WebHookStockSyncBillThread> stockSyncBillThreads = new HashMap<String,WebHookStockSyncBillThread>();
    private static Map<String,WebHookStockSyncGoodsThread> stockSyncGoodsThreads = new HashMap<String,WebHookStockSyncGoodsThread>();
    public static void stockSyncStart(String EID,String FORMAT,String BILLQUENAME,String GOODSQUENAME)
    {
    	//每个第三方只启动一组（两个）线程，处理自己的队列，线程存在的情况就不再创建了
    	
    	String key = EID+"_"+FORMAT;
    	
    	//第一个线程,将bill队列展开成goods放入待同步队列中。。延续原来的逻辑传bill进来的
    	if(!stockSyncBillThreads.containsKey(key))
    	{
    		WebHookStockSyncBillThread stockSyncBillThread = new WebHookStockSyncBillThread(EID,FORMAT,BILLQUENAME,GOODSQUENAME);
        Thread thread = new Thread(stockSyncBillThread);
        thread.setDaemon(true);
        thread.start();
        stockSyncBillThreads.put(key, stockSyncBillThread);
    	}
    	
    	//第二个线程,将goods队列中的goods的库存同步给第三方(企迈，有赞）
    	if(!stockSyncGoodsThreads.containsKey(key))
    	{
    		WebHookStockSyncGoodsThread stockSyncGoodsThread = new WebHookStockSyncGoodsThread(EID,FORMAT,GOODSQUENAME);
        Thread thread = new Thread(stockSyncGoodsThread);
        thread.setDaemon(true);
        thread.start();
        stockSyncGoodsThreads.put(key, stockSyncGoodsThread);
    	}    	    	
    }
    
    
    public static String StockSyncBillQueue = "StockSyncBillQueue";
    public static String StockSyncGoodsQueue = "StockSyncGoodsQueue";
    
    
    
}
