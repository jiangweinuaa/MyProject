package com.dsc.spos.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class DemoTest 
{
	
	public static void main(String[] args) throws Exception 
	{		
		//*******************************存
		try 
		{
		    @SuppressWarnings("resource")
			Jedis jedis = new Jedis("172.16.70.158", 6379,10000);//默认数据库索引是0
		    jedis.auth("pos2012");//密码验证
		    
		    //jedis.select(2);//可以切换数据库
		    
		    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		    
		    Map<String, Object> m = new HashMap<String, Object>();
		    m.put("m1", "1");
		    m.put("m2", "2");
		    m.put("m3", "3");
		    result.add(m);
		    
		    ListTranscoder<HashMap<String, Object>> listTranscoder = new ListTranscoder<HashMap<String, Object>>();
		    		    
		    jedis.set("m1".getBytes(), listTranscoder.serialize(result));
		    
		    result.remove(0);
		    m = new HashMap<String, Object>();
		    m.put("m1", "1");
		    m.put("m2", "2");
		    m.put("m3", "3");
		    result.add(m);
		    
            jedis.set("m2".getBytes(), listTranscoder.serialize(result));
		       
		} 
		catch (Exception e) 
		{
		    //如果缓存连不上，则不处理
		    //System.out.println(e.getMessage());
		}
		
		
		
		
		//*********************************取		
		try 
		{
			 @SuppressWarnings("resource")
			 Jedis jedis1 = new Jedis("172.16.70.158", 6379,10000);
			 jedis1.auth("pos2012");//密码验证			 
			 
			 byte[] list = jedis1.get("m1".getBytes());
			 ListTranscoder<HashMap<String, Object>> listTranscoder = new ListTranscoder<HashMap<String, Object>>();
			 
			 //多KEY
			 List<byte[]> lstp=jedis1.mget("m1".getBytes(),"m2".getBytes());
			 for (int i = 0; i < lstp.size(); i++) 
			 {
				 @SuppressWarnings("unchecked")
				 List<Map<String, Object>> cc = (List<Map<String, Object>>) listTranscoder.deserialize(lstp.get(i));
				 for (Map<String, Object> oneData : cc) 
				 {
					 String m1 = oneData.get("m1").toString();
					 String m2 = oneData.get("m2").toString();
					 String m3 = oneData.get("m3").toString();

					 //System.out.println(m1);
					 //System.out.println(m2);
					 //System.out.println(m3);
				 }
			 }
			 
			 @SuppressWarnings("unchecked")
			 List<Map<String, Object>> newList = (List<Map<String, Object>>) listTranscoder.deserialize(list);
			 for (Map<String, Object> oneData : newList) 
			 {
				 String m1 = oneData.get("m1").toString();
				 String m2 = oneData.get("m2").toString();
				 String m3 = oneData.get("m3").toString();
				 
				 //System.out.println(m1);
				 //System.out.println(m2);
				 //System.out.println(m3);
			 }
			 
		}
		catch (Exception e) 
		{
		    //如果缓存连不上，则不处理
		    //System.out.println(e.getMessage());
		}		
		
		
	}
	

}
