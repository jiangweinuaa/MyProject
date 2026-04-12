package com.dsc.spos.redis;

import java.util.*;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import com.dsc.spos.scheduler.job.StaticInfo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class RedisPosPub
{
    private Logger log = LogManager.getLogger(RedisPosPub.class);


    public  RedisPosPub()
    {

    }

    /**
     * 存List Map对象列表数据
     * @param key
     * @param mapList
     */
    public boolean setList(String key,List<Map<String, Object>> mapList)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;
            try
            {
                ListTranscoder<HashMap<String, Object>> listTranscoder = new ListTranscoder<HashMap<String, Object>>();

                jedis=RedisUtil.getRedis();

                jedis.set(key.getBytes(), listTranscoder.serialize(mapList));

                return true;
            }
            catch (Exception e)
            {
                log.error("\r\n******setList("+key+")失败!" + e.getMessage()+"******\r\n");

                return false;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return false;
        }

    }

    /**
     * 取 List Map 对象列表数据
     * @param key
     * @return
     */
    public List<Map<String, Object>> getList(String key)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;
            try
            {
                jedis=RedisUtil.getRedis();

                byte[] list = jedis.get(key.getBytes());
                if(list!=null)
                {
                    ListTranscoder<HashMap<String, Object>> listTranscoder = new ListTranscoder<HashMap<String, Object>>();

                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> result = (List<Map<String, Object>>) listTranscoder.deserialize(list);


                    return result;
                }
                else
                {

                    return null;
                }

            }
            catch (Exception e)
            {
                log.error("\r\n******getList("+key+")失败!" + e.getMessage()+"******\r\n");

                return null;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return null;
        }

    }

    /**
     * 存 Map对象数据，暂停使用，效能低
     * @param key
     * @param key
     * @param map
     */
    private boolean setHashMap(String key,String field,Map<String, Object> map)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;
            try
            {
                ObjectTranscoder<HashMap<String, Object>> listTranscoder = new ObjectTranscoder<HashMap<String, Object>>();

                jedis=RedisUtil.getRedis();
                //hash
                jedis.hset(key.getBytes(),field.getBytes(),listTranscoder.serialize(map));


                return true;
            }
            catch (Exception e)
            {
                log.error("\r\n******setHashMap("+key+")失败!" + e.getMessage()+"******\r\n");

                return false;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return false;
        }

    }

    /**
     * 取 Map 对象数据，暂停使用，效能低
     * @param key
     * @param field 主键组成
     * @param field1  默认为空串，有些union关联的并集自定义值EX:ALL
     * @return
     */
    private Map<String, Object> getHashMap(String key,String field,String field1)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;

            try
            {
                jedis=RedisUtil.getRedis();

                //hash
                byte[] list = jedis.hget(key.getBytes(),field.getBytes());

                //找不到，找另一个
                if(list==null) list = jedis.hget(key.getBytes(),field1.getBytes());

                ObjectTranscoder<HashMap<String, Object>> listTranscoder = new ObjectTranscoder<HashMap<String, Object>>();

                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) listTranscoder.deserialize(list);
                return result;
            }
            catch (Exception e)
            {
                log.error("\r\n******getHashMap("+key+")失败!" + e.getMessage()+"******\r\n");

                return null;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return null;
        }

    }

    /**
     * 取 List Map 对象列表数据
     * @param key
     * @return
     */
    public Map<String, String> getALLHashMap(String key)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;
            try
            {
                jedis=RedisUtil.getRedis();

                Map<String, String> result = jedis.hgetAll(key);

                return result;
            }
            catch (Exception e)
            {
                log.error("\r\n******getALLHashMap("+key+")失败!" + e.getMessage()+"******\r\n");

                return null;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return null;
        }

    }

    /**
     * 存List Map对象列表数据
     * @param key
     * @param hashmapList
     */
    public boolean setALLHashMap(String key,Map<String, String> hashmapList)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;
            try
            {
                jedis=RedisUtil.getRedis();

                jedis.hmset(key, hashmapList);

                return true;
            }
            catch (Exception e)
            {
                log.error("\r\n******setALLHashMap("+key+")失败!" + e.getMessage()+"******\r\n");
                return false;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return false;
        }

    }

    /**
     * 存 Map对象数据
     * @param key
     */
    public boolean setHashMap(String key,String field,String value)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;

            try
            {
                jedis=RedisUtil.getRedis();

                //hash
                jedis.hset(key, field, value);

                return true;
            }
            catch (Exception e)
            {
                log.error("\r\n******setHashMap("+key+")失败!" + e.getMessage()+"******\r\n");
                return false;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return false;
        }

    }

    /**
     * 取 Map 对象数据
     * @param key
     * @param field 主键组成
     * @return
     */
    public String getHashMap(String key,String field)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;

            try
            {
                jedis=RedisUtil.getRedis();

                String result = jedis.hget(key, field);

                return result;
            }
            catch (Exception e)
            {
                log.error("\r\n******getHashMap("+key+")失败!" + e.getMessage()+"******\r\n");
                return null;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return null;
        }

    }

    public boolean IsExistHashKey(String key,String field)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;
            try
            {
                jedis=RedisUtil.getRedis();

                boolean bExist=jedis.hexists(key, field);

                return bExist;
            }
            catch (Exception e)
            {
                log.error("\r\n******IsExistHashKey("+key+")失败!" + e.getMessage()+"******\r\n");
                return false;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return false;
        }

    }

    public boolean IsExistStringKey(String key)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;

            try
            {
                jedis=RedisUtil.getRedis();

                boolean bExist=jedis.exists(key);

                return bExist;
            }
            catch (Exception e)
            {
                log.error("\r\n******IsExistStringKey("+key+")失败!" + e.getMessage()+"******\r\n");
                return false;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return false;
        }

    }

    /**
     * 删除Key
     * @param key
     * @return
     */
    public boolean DeleteKey(String key)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;

            try
            {
                jedis=RedisUtil.getRedis();

                jedis.del(key.getBytes());

                return true;
            }
            catch (Exception e)
            {
                log.error("\r\n******DeleteKey("+key+")失败!" + e.getMessage()+"******\r\n");

                return false;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return false;
        }

    }

    /**
     * 删除Hash的key键值对应的field记录
     * @param key
     * @param field 记录
     * @return
     */
    public boolean DeleteHkey(String key,String field)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;

            try
            {
                jedis=RedisUtil.getRedis();

                jedis.hdel(key.getBytes(),field.getBytes());

                return true;
            }
            catch (Exception e)
            {
                log.error("\r\n******DeleteHkey("+key+")失败!" + e.getMessage()+"******\r\n");
                return false;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return false;
        }

    }

    /**
     * 一次删多个key，数组方式
     * @param key
     * @param field
     * @return
     */
    public boolean DeleteHighkey(String key,String[] field)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;

            try
            {
                jedis=RedisUtil.getRedis();

                jedis.hdel(key,field);

                return true;
            }
            catch (Exception e)
            {
                log.error("\r\n******DeleteHighkey("+key+")失败!" + e.getMessage()+"******\r\n");
                return false;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return false;
        }

    }

    /**
     * 存 String对象数据
     * @param key
     * @param value
     */
    public boolean setString(String key,String value)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;

            try
            {
                jedis=RedisUtil.getRedis();

                //string
                jedis.set(key, value);

                return true;
            }
            catch (Exception e)
            {
                log.error("\r\n******setString("+key+")失败!" + e.getMessage()+"******\r\n");
                return false;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return false;
        }

    }

    /**
     * 取 String 对象数据
     * @param key
     * @return
     */
    public String getString(String key)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;
            try
            {
                jedis=RedisUtil.getRedis();

                String result = jedis.get(key);

                return result;
            }
            catch (Exception e)
            {
                log.error("\r\n******getString("+key+")失败!" + e.getMessage()+"******\r\n");
                return null;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return null;
        }

    }

    /*
     * 设置键的过期时间
     * @param key
     * @param seconds 秒数
     */
    public boolean setExpire(String key,int seconds)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;

            try
            {
                jedis=RedisUtil.getRedis();

                long nret =	jedis.expire(key, seconds);
                return true;
            }
            catch (Exception e)
            {
                log.error("\r\n******setExpire("+key+")失败!" + e.getMessage()+"******\r\n");
                return false;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return false;
        }

    }

    /**
     * 存Map,一次性保存多笔记录，优化效能保存redis数据，
     * 测试报告:3300笔数据本机连19测试redis只要5秒，如果一笔一笔存要60秒
     * @param key
     * @param map
     * @return
     */
    public boolean setHighHashMap(String key,Map map)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;
            try
            {
		
				/*
				 Map map = new HashMap(); 
	            map.put("field1", "field1-value"); 
	            map.put("field2", "field2-value"); 
	            jedis.hmset("key1", map); 

				 */

                jedis=RedisUtil.getRedis();

                jedis.hmset(key, map);

                return true;
            }
            catch (Exception e)
            {
                log.error("\r\n******setHighHashMap("+key+")失败!" + e.getMessage()+"******\r\n");
                return false;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return false;
        }

    }

    /**
     * 取出Map,一次性保存多笔记录，优化效能保存redis数据,找不到返回List<null>
     * @param key
     * @param field1
     * @param field1
     * @return
     */
    public List<String> getHighHashMap(String key,String field1,String field2)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;
            try
            {
                List<String> result =null;

                jedis=RedisUtil.getRedis();

                if (field2==null || field2.equals(""))
                {
                    result = jedis.hmget(key,field1);
                }
                else
                {
                    result = jedis.hmget(key,field1,field2);
                }

                return result;
            }
            catch (Exception e)
            {
                log.error("\r\n******getHighHashMap("+key+")失败!" + e.getMessage()+"******\r\n");
                return null;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return null;
        }

    }

    /**
     * 取出Map,数组方式，一次性取多笔记录，建议取2000笔以内，优化效能保存redis数据,找不到返回List<null>
     * @param key
     * @param field1
     * @return
     */
    public List<String> getHighHashMapArray(String key,String[] field1)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;
            try
            {
                List<String> result =null;

                jedis=RedisUtil.getRedis();

                result = jedis.hmget(key,field1);

                return result;
            }
            catch (Exception e)
            {
                log.error("\r\n******getHighHashMapArray("+key+")失败!" + e.getMessage()+"******\r\n");
                return null;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return null;
        }

    }

    /**
     * keys、hgetAll等命令容易阻塞，SCAN命令不会阻塞
     * @param key
     * @return
     */
    public Map<String, String> getALLHashMap_Hscan(String key)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;

            Map<String, String> result=new HashMap<>();

            try
            {
                jedis=RedisUtil.getRedis();

                // 游标位置
                String cursor = "0";

                while (true)
                {
                    ScanResult<Map.Entry<String, String>> hscanResult = jedis.hscan(key, cursor);
                    //
                    List<Map.Entry<String, String>> scanResult =hscanResult.getResult();

                    for (Map.Entry<String, String> m : scanResult)
                    {
                        result.put(m.getKey(),m.getValue());
                    }

                    // 如果游标为0，遍历结束
                    cursor=hscanResult.getStringCursor();

                    if (cursor.equals("0"))
                    {
                        break;
                    }
                }

                return result;
            }
            catch (Exception e)
            {
                log.error("\r\n******getHighHashMapArray("+key+")失败!" + e.getMessage()+"******\r\n");
                return null;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return null;
        }
    }

    public void Close()
    {
		/*
		//开启redis才走
		if (StaticInfo.Using_Redis.equals("1")) 
		{
			if (jedis!=null) 
			{
				try 
				{
					jedis.close();
					jedis.disconnect();//这很关键
				} 
				catch (Exception e) 
				{
					if (jedis != null) 
					{
						try 
						{
							//客户端主动关闭连接
							jedis.disconnect();
						} 
						catch (Exception e2) 
						{

						}
					}
				}	

				//jedis=null;
			}
		}	
		*/
    }
    /*
     * 增加1
     * @param key
     */

    public boolean incr(String key)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;
            try
            {
                jedis=RedisUtil.getRedis();
                jedis.scan("1");
                jedis.incr(key);
                return true;
            }
            catch (Exception e)
            {
                log.error("\r\n******jedis.incr("+key+")失败!" + e.getMessage()+"******\r\n");
                return false;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return false;
        }
    }

    /*
     * 查找Keys
     * @param key
     */
    public Set< String >  keys(String key)
    {
        //开启redis才走
        //String keys="";
        Set< String > keys=null;
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;
            try
            {
                jedis=RedisUtil.getRedis();
                keys=jedis.keys(key);
                return keys;
            }
            catch (Exception e)
            {
                log.error("\r\n******jedis.incr("+key+")失败!" + e.getMessage()+"******\r\n");
                return keys;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return keys;
        }
    }

    /*
     * 设置键的过期时间
     * @param key
     * @param seconds 秒数
     */
    public boolean setEx(String key,int seconds,String value)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;

            try
            {
                jedis=RedisUtil.getRedis();
                jedis.setex(key, seconds, value);
                return true;
            }
            catch (Exception e)
            {
                log.error("\r\n******setExpire("+key+")失败!" + e.getMessage()+"******\r\n");
                return false;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return false;
        }

    }

    /**
     * Add the string value to the head (LPUSH) or tail (RPUSH) of the list stored at key. If the key
     * does not exist an empty list is created just before the append operation. If the key exists but
     * is not a List an error is returned.
     * <p>
     * Time complexity: O(1)
     * @param key
     * @param value
     * @return Integer reply, specifically, the number of elements inside the list after the push
     *         operation.
     */
    public boolean lpush(String key,String value)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;

            try
            {
                jedis=RedisUtil.getRedis();

                //string
                jedis.lpush(key, value);

                return true;
            }
            catch (Exception e)
            {
                log.error("\r\n******lpush("+key+")失败!" + e.getMessage()+"******\r\n");
                return false;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return false;
        }

    }

    /**
     * Atomically return and remove the first (LPOP) or last (RPOP) element of the list. For example
     * if the list contains the elements "a","b","c" LPOP will return "a" and the list will become
     * "b","c".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned.
     * @see #rpop(String)
     * @param key
     * @return Bulk reply
     */
    public String lpop(String key)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;
            try
            {
                jedis=RedisUtil.getRedis();

                String result = jedis.lpop(key);

                return result;
            }
            catch (Exception e)
            {
                log.error("\r\n******lpop("+key+")失败!" + e.getMessage()+"******\r\n");
                return null;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return null;
        }

    }

    public boolean brpush(String key,String value)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;

            try
            {
                jedis=RedisUtil.getRedis();

                //string
                jedis.rpush(key, value);

                return true;
            }
            catch (Exception e)
            {
                log.error("\r\n******rpush("+key+")失败!" + e.getMessage()+"******\r\n");
                return false;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return false;
        }

    }

    public String rpop(String key)
    {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1"))
        {
            Jedis jedis=null;
            try
            {
                jedis=RedisUtil.getRedis();

                String result = jedis.rpop(key);

                return result;
            }
            catch (Exception e)
            {
                log.error("\r\n******rpop("+key+")失败!" + e.getMessage()+"******\r\n");
                return null;
            }
            finally
            {
                if (jedis!=null)
                {
                    jedis.close();
                    jedis.disconnect();
                    jedis=null;
                }

                Close();
            }
        }
        else
        {
            return null;
        }

    }

    /**
     * keys、hgetAll等命令容易阻塞，SCAN命令不会阻塞
     * @param key
     * @return
     */
    public List<String> getString_scan(String key) {
        //开启redis才走
        if (StaticInfo.Using_Redis.equals("1")) {
            List<String> result = new ArrayList<>();
            Jedis jedis = null;
            try {
                jedis=RedisUtil.getRedis();
                ScanParams scanParams = new ScanParams();
                scanParams.match(key);
                scanParams.count(100);
                // 游标位置
                int cursor = 0;
                do {
                    ScanResult<String> scanResult = jedis.scan(cursor, scanParams);

                    List<String> resultList = scanResult.getResult();
                    if (resultList != null && !resultList.isEmpty()) {
                        result.addAll(resultList);
                    }

                    // 如果游标为0，遍历结束
                    cursor = scanResult.getCursor();
                } while (cursor != 0);

                return result;

            } catch (Exception e) {
                log.error("\r\n******getString_scan("+key+")失败!" + e.getMessage()+"******\r\n");
                return null;
            } finally {
                if (jedis!=null) {
                    jedis.close();
                    jedis.disconnect();
                }

                Close();
            }
        } else {
            return null;
        }
    }
}