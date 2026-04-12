package com.dsc.spos.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/*
 * *************Object****************
 */
public class ObjectTranscoder<M extends Serializable> extends SerializeTranscoder
{

	/**
	 * 序列化对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public byte[] serialize(Object value) 
	{		
		if (value == null) 
		{  
			throw new NullPointerException("Null不能够被序列化！");
		}  
		byte[] result = null;  
		ByteArrayOutputStream bos = null;  
		ObjectOutputStream os = null;  
		try 
		{  
			bos = new ByteArrayOutputStream();  
			os = new ObjectOutputStream(bos);
			M m = (M) value;
			os.writeObject(m);  
			os.close();  
			bos.close();  
			result = bos.toByteArray();  
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
		return result;  
	}

	
	/**
	 * 反序列化
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object deserialize(byte[] in) throws IOException 
	{		
		M result = null;  
		ByteArrayInputStream bis = null;  
		ObjectInputStream is = null;  
		try 
		{  
			if (in != null) 
			{  
				bis = new ByteArrayInputStream(in);  
				is = new ObjectInputStream(bis);  
				result = (M) is.readObject();  
				is.close();  
				bis.close();  
			}  
		} 
		catch (IOException e) 
		{
			
		} 
		catch (ClassNotFoundException e) 
		{  

		} 
		finally 
		{  
			close(is);  
			close(bis);  
		}  
		return result;  
	}

}
