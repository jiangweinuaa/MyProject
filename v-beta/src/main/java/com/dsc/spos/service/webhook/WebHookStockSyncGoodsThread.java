package com.dsc.spos.service.webhook;

import org.apache.log4j.Logger;

import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ESBUtils;

public class WebHookStockSyncGoodsThread implements Runnable {
	static Logger logger = Logger.getLogger(WebHookStockSyncGoodsThread.class);
	
	private String eId;
	private String format;
	private String goodsquename;
	public WebHookStockSyncGoodsThread(String EID,String FORMAT,String GOODSQUENAME)
	{
		eId = EID;
		format = FORMAT;
		goodsquename = GOODSQUENAME;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		execute();
	}

	private int ct=0;
	public void execute()
	{
		ct = 0;
  	
  	RedisPosPub redis = new RedisPosPub();
  	try
  	{
	  	while(true)
	  	{
	  		String key = goodsquename;
	  		String Data = "";
	  		try
	  		{
	  			Data = redis.rpop(key);
		  		if("nil".equals(Data)||"null".equals(Data)||Check.Null(Data))		//nil 个屁，实测是null
		  		{
		  			//break;
		  			Thread.sleep(5000);
		  			continue;
		  		}
		  		
		  		//String value = eId+"|"+shopId+"|"+WAREHOUSE+"|"+PLUNO+"|"+FEATURENO;	  		
		  		
		  		
		  		//如果用“|”作为分隔的话,必须是如下写法,String.split("\\|"),这样才能正确的分隔开,不能用String.split("|")，“.”和“|”都是转义字符,必须得加"\\"。
		  		//靠,真坑
		  		String[] ss = Data.split("\\|");
		  		
		  		String eid = ss[0];
		  		String shopid = ss[1];
		  		String warehouse = ss[2];
		  		String goodsid = ss[3];
		  		String featureno = " ";
		  		if(ss.length>4)
		  		{
		  			featureno = ss[4];
		  		}
		  		if(Check.Null(featureno))
		  		{
		  			featureno = " ";
		  		}
		  		//WebHookUtils.saveLog("webHookService","WebHookStockSyncGoodsThread Data:"+Data);
		  		//WebHookUtils.saveLog("webHookService","WebHookStockSyncGoodsThread EID:"+eid);
		  		//WebHookUtils.saveLog("webHookService","WebHookStockSyncGoodsThread shopid:"+shopid);
		  		//WebHookUtils.saveLog("webHookService","WebHookStockSyncGoodsThread warehouse:"+warehouse);
		  		//WebHookUtils.saveLog("webHookService","WebHookStockSyncGoodsThread goodsid:"+goodsid);
		  		//WebHookUtils.saveLog("webHookService","WebHookStockSyncGoodsThread featureno:"+featureno);
		  		
		  		
		  		//21:45:48.088 WebHookStockSyncGoodsThread Data:66|1062|M062|1005010009| 
		  		//21:45:48.088 WebHookStockSyncGoodsThread EID:6
		  		//21:45:48.088 WebHookStockSyncGoodsThread EID:6
		  		
	        WebHookStockSyncServiceV2 service = new WebHookStockSyncServiceV2(eid,format,goodsquename);
	        service.executeGoods(shopid,warehouse,goodsid,featureno);
	  		}
	  		catch(Exception ex)
	  		{
	  			WebHookUtils.saveLog("webHookService","WebHookStockSyncGoodsThread：同步商品库存出错:"+Data+":"+ex.toString());
	  		}
	  		
	  		ct++;
	  		if(ct == 100)	//每100个停一下，防止CPU过度占用
	  		{
	  			Thread.sleep(1000);
	  			ct = 0;
	  		}
	  	}	  	
	  	
  	}
  	catch(Exception ex)
  	{
  		WebHookUtils.saveLog("webHookService","WebHookStockSyncGoodsThread：同步商品库存出错:"+ex.toString());
  	}
  	finally
  	{
  		redis.Close();
  	}

	}
	
}
