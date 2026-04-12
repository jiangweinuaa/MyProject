package com.dsc.spos.service.webhook;

import org.apache.log4j.Logger;

import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.utils.Check;

/*
 * 同步库存前的准备，将bill展开成 goods放入队列中
 */
public class WebHookStockSyncBillThread implements Runnable {
	static Logger logger = Logger.getLogger(WebHookStockSyncBillThread.class);
	
	private String eId;
	private String format;
	private String billquename;
	private String goodsquename;
	public WebHookStockSyncBillThread(String EID,String FORMAT,String BILLQUENAME,String GOODSQUENAME)
	{
		eId = EID;
		format = FORMAT;
		billquename = BILLQUENAME;
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
	  		String key = billquename;
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

		  		//如果用“|”作为分隔的话,必须是如下写法,String.split("\\|"),这样才能正确的分隔开,不能用String.split("|")，“.”和“|”都是转义字符,必须得加"\\"。
		  		//靠,真坑
		  		String[] ss = Data.split("\\|");
		  		String eid = ss[0];
		  		String shopid = ss[1];
		  		String billno = ss[2];
		  		
		  		//获取单据中的品号，写入品号队列（先进先出）
	        WebHookStockSyncServiceV2 service = new WebHookStockSyncServiceV2(eid,format,goodsquename);
	        service.BillToGoods(shopid,billno);
	  		}
	  		catch(Exception ex)
	  		{
	  			WebHookUtils.saveLog("webHookService","WebHookStockSyncGoodsThread：同步商品库存单据出错:"+Data+":"+ex.toString());
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
  		WebHookUtils.saveLog("webHookService","WebHookStockSyncGoodsThread：同步商品库存单据出错:"+ex.toString());
  	}
  	finally
  	{
  		redis.Close();
  	}

	}
	
}
