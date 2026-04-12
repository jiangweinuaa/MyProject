package com.dsc.spos.redis;

import java.io.Closeable;
import java.io.IOException;

/*
 * 用于序列化和反序列化抽象类
 */
public abstract class SerializeTranscoder 
{

	/**
	 * 序列化
	 * @param value
	 * @return
	 */
	public abstract byte[] serialize(Object value);
	

	/**
	 * 反序列化
	 */
	public abstract Object deserialize(byte[] in) throws IOException;
	
    
	/**
	 * 释放
	 * @param closeable
	 */
	public void close(Closeable closeable)
	{
		if (closeable != null)
		{
			try 
			{
				closeable.close();
			} 
			catch (Exception e) 
			{
				
			}
		}
	}
    
}
