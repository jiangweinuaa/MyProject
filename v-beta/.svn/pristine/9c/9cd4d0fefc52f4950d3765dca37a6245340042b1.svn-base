package com.dsc.spos.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/*
 * *****************LIST*******************
 */
public class ListTranscoder<M extends Serializable> extends SerializeTranscoder 
{

	/**
	 * 序列化对象LIST
	 * value List对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public byte[] serialize(Object value) 
	{
		if (value == null)
		{
			throw new NullPointerException("Null不能够被序列化！");
		}

		List<M> values = (List<M>) value;
		byte[] results = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream os = null;

		try 
		{
			bos = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bos);
			for (M m : values) 
			{
				os.writeObject(m);
			}
			results = bos.toByteArray();
			os.close();
			bos.close();
		} 
		catch (IOException e) 
		{
			throw new IllegalArgumentException("该对象序列化失败！", e);
		} 
		finally 
		{
			close(os);
			close(bos);
		}
		return results;
	}

	
	/**
	 * 反序列化LIST
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object deserialize(byte[] in) throws IOException 
	{
		List<M> list = new ArrayList<>();
		ByteArrayInputStream bis = null;
		ObjectInputStream is = null;
		try 
		{
			if (in != null) 
			{
				bis = new ByteArrayInputStream(in);
				is = new ObjectInputStream(bis);
				while (true) 
				{
					M m = (M)is.readObject();
					if (m == null) 
					{
						break;
					}
					list.add(m);
				}
				is.close();
				bis.close();
			}
		} 
		catch (Exception e) 
		{  

		}  
		finally 
		{
			is.close();
			bis.close();
		}
		return  list;
	}
	

}
