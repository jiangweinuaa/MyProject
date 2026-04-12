package com.dsc.spos.redis;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.scheduler.eventlistener.QuartzServiceListener;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class RedisUtil implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static Logger log = LogManager.getLogger(RedisUtil.class);

	//Redis服务器IP
	private static String addr;

	//Redis的端口号
	private static int port;

	//访问密码
	private static String auth;

	//可用连接实例的最大数目，默认值为8；
	//如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
	private static int maxActive;

	//控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
	private static int maxIdle;

	//最小空闲连接数, 默认0
	private static int minIdle;

	//等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
	private static int maxWait;

	private static int timeOut;

	//*********针对分布式存储多实例有意义****************
	//在borrow一个jedis实例时，是否提前进行validate操作；
	//如果为true，则得到的jedis实例均是可用的；默认false
	private static boolean testOnBorrow;

	//*********针对分布式存储多实例有意义****************
	//在return给pool时，是否提前进行validate操作；
	private static boolean testOnReturn;

	//*********针对分布式存储多实例有意义****************
	//在空闲时检查有效性, 默认false
	private static boolean testWhileIdle;	

	//private static Jedis jedis=null;//非切片额客户端连接

	private  static JedisPool jedisPool;//非切片连接池

	//public static ShardedJedis shardedJedis;//切片额客户端连接

	//public static ShardedJedisPool shardedJedisPool;//切片连接池

	/**
	 * 初始化非切片池
	 */
	@PostConstruct
	public synchronized static void initialPool() 
	{ 
		// 池基本配置 
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxActive); 
		//连接池的最佳性能是maxTotal = maxIdle ,这样就避免连接池伸缩带来的性能干扰
		config.setMaxIdle(maxIdle); 
		
		//此值是为了控制空闲资源监测。
		config.setMinIdle(minIdle);
		//当资源池用尽后，调用者是否要等待。只有当为true时，下面的maxWaitMillis才会生效
		config.setBlockWhenExhausted(true);
		config.setMaxWaitMillis(maxWait); 
		//业务量很大时候建议设置为false(多一次ping的开销)。
		config.setTestOnBorrow(true);
		//业务量很大时候建议设置为false(多一次ping的开销)。
		config.setTestOnReturn(true);
		
		//是否开启空闲资源监测开关，此开关关闭，下面配置不生效
		config.setTestWhileIdle(true);
		//空闲资源的检测周期(单位为毫秒)，1分钟
		config.setTimeBetweenEvictionRunsMillis(60000);
		//资源池中资源最小空闲时间(单位为毫秒)，达到此值后空闲资源将被移除,10分钟
		config.setMinEvictableIdleTimeMillis(600000);
		//做空闲资源检测时，每次的采样数,如果设置为-1，就是对所有连接做空闲监测
		config.setNumTestsPerEvictionRun(-1);		
		//timeout，这里既是连接超时又是读写超时，从Jedis 2.8开始有区分connectionTimeout和soTimeout的构造函数
		if(auth!=null&&auth.trim().isEmpty()==false)
		{
			jedisPool = new JedisPool(config, addr, port,timeOut,auth,1);
		}
		else
		{
			//没密码就是null
			jedisPool = new JedisPool(config, addr, port,timeOut,null,1);
		}	
		
	}

	//	/** 
	//	 * 初始化切片池 ，这里的切片就是分布式存储，多服务器实例
	//	 */ 
	//	private static  void initialShardedPool() 
	//	{ 
	//		// 池基本配置 
	//		JedisPoolConfig config = new JedisPoolConfig();
	//		config.setMaxTotal(maxActive); 
	//		config.setMaxIdle(maxIdle); 
	//		config.setMinIdle(minIdle);
	//		config.setMaxWaitMillis(maxWait); 
	//		config.setTestOnBorrow(testOnBorrow);
	//		config.setTestOnReturn(testOnReturn);
	//		config.setTestWhileIdle(testWhileIdle);
	//		// slave链接 
	//		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(); 
	//		shards.add(new JedisShardInfo(addr, port, auth)); //认证密码
	//
	//		// 构造池 
	//		shardedJedisPool = new ShardedJedisPool(config, shards); 
	//	}

	public synchronized static Jedis getRedis() throws Exception 
	{		

		//initialPool(); 
		//initialShardedPool();
		//try 
		//{
		//	shardedJedis = shardedJedisPool.getResource(); 
		//} 
		//catch (Exception e) 
		//{
		//	//System.out.println("连接shardedJedisPool失败!");
		//}


		Jedis jedis =null;
		try 
		{

			jedis = jedisPool.getResource();

			//此密码认证已在初始化进行
			//jedis.auth(auth);
			
			//数据缓存在1区
			//jedis.select(1);

			//System.out.println(jedisPool.getNumActive());
		} 
		catch (Exception e) 
		{
			//log.error("\r\n******getRedis获取连接失败!******\r\n");

			if (jedis!=null) 
			{
				jedis.close();
				
			}
			
			try 
			{
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);		
				
				pw.flush();
				pw.close();			
				
				errors.flush();
				errors.close();		
				
				log.error("\r\n******getRedis获取连接失败!" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");
				
				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				log.error("\r\n******getRedis获取连接失败e1!" + e1.getMessage() + "******\r\n");
			}		
			
		}

		return jedis;
	}


	/**
	 * redis 连接测试
	 * @return
	 */
	public synchronized static boolean connectRedisTest() 
	{
		boolean bOK=false;
		
		Jedis jedis =null;
		
		try 
		{
			jedis = jedisPool.getResource();

			//此密码认证已在初始化进行
			//jedis.auth(auth);

			jedis.close();
			jedis.disconnect();//这很关键
			
			jedis=null;

			//System.out.println(jedisPool.getNumActive());

			log.info("\r\n******Redis连接OK!******\r\n");

			bOK=true;
		} 
		catch (Exception e) 
		{
			bOK=false;

			if (jedis!=null) 
			{
				jedis.close();
				jedis.disconnect();//这很关键
			}

			try 
			{
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);		
				
				pw.flush();
				pw.close();			
				
				errors.flush();
				errors.close();	
				
				log.error("\r\n******Redis连接失败!" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");
				
				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				log.error("\r\n******Redis连接失败!" + e1.getMessage() + "******\r\n");
			}		
		}		

		return bOK;
	}



	public String getAddr() 
	{
		return addr;
	}

	public void setAddr(String addr) 
	{
		RedisUtil.addr = addr;
	}

	public int getPort() 
	{
		return port;
	}

	public void setPort(int port) 
	{
		RedisUtil.port = port;
	}

	public String getAuth() 
	{
		return auth;
	}

	public void setAuth(String auth) 
	{
		RedisUtil.auth = auth;
	}

	public int getMaxActive() 
	{
		return maxActive;
	}

	public void setMaxActive(int maxActive) 
	{
		RedisUtil.maxActive = maxActive;
	}

	public int getMaxIdle() 
	{
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) 
	{
		RedisUtil.maxIdle = maxIdle;
	}


	public int getMinIdle() 
	{
		return minIdle;
	}

	public void setMinIdle(int minIdle) 
	{
		RedisUtil.minIdle = minIdle;
	}


	public int getMaxWait() 
	{
		return maxWait;
	}

	public void setMaxWait(int maxWait) 
	{
		RedisUtil.maxWait = maxWait;
	}

	public int getTimeOut() 
	{
		return timeOut;
	}

	public void setTimeOut(int timeOut) 
	{
		RedisUtil.timeOut = timeOut;
	}

	public boolean getTestOnBorrow() 
	{
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) 
	{
		RedisUtil.testOnBorrow = testOnBorrow;
	}


	public boolean getTestOnReturn() 
	{
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) 
	{
		RedisUtil.testOnReturn = testOnReturn;
	}

	public boolean getTestWhileIdle() 
	{
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) 
	{
		RedisUtil.testWhileIdle = testWhileIdle;
	}



}
