package com.dsc.spos.scheduler.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dsc.spos.utils.EncryptUtils;
import com.dsc.spos.utils.HttpSend;

public class DeliverooAsynExecutor 
{
	Logger logger = LogManager.getLogger(DeliverooAsynExecutor.class.getName());

	private ExecutorService executor = Executors.newFixedThreadPool(1);

	public void asynTask(final String apiUrl,final String secret,final String sequence_guid,String orderID) throws InterruptedException 
	{
		executor.submit(new Runnable() 
		{

			@Override
			public void run() 
			{

				try 
				{
					SimpleDateFormat sdfpp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
					Date nowRT = new Date();
					String sNowRT=sdfpp.format(nowRT);			
					//System.out.println(sNowRT);	

					JSONObject header = new JSONObject();
					header.put("occurred_at", sNowRT);//同步时间
					header.put("status", "succeeded");//同步成功状态 succeeded/failed
					header.put("reason", "");//错误原因
					//header.put("notes", "");//可选字段，错误备注字段，支持更多内容

					//
					String request=header.toString();

					//
					String message=String.format("%s \n %s", sequence_guid,request);

					//hash
					EncryptUtils eu = new EncryptUtils();	
					String hash= eu.HMAC_SHA256_Deliveroo(message,secret);
					eu=null;
					
					String resbody=HttpSend.SendDeliveroo("sync_status", request, apiUrl,sequence_guid,hash);
					JSONObject jsonres = new JSONObject(resbody);

				} 
				catch (Exception e) 
				{

				}		


			}
		});

	}
}
